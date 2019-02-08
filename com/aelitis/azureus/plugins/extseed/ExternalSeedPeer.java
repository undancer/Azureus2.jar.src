/*      */ package com.aelitis.azureus.plugins.extseed;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.messaging.Message;
/*      */ import org.gudy.azureus2.plugins.messaging.MessageStreamEncoder;
/*      */ import org.gudy.azureus2.plugins.network.Connection;
/*      */ import org.gudy.azureus2.plugins.network.ConnectionListener;
/*      */ import org.gudy.azureus2.plugins.network.ConnectionStub;
/*      */ import org.gudy.azureus2.plugins.network.IncomingMessageQueue;
/*      */ import org.gudy.azureus2.plugins.network.IncomingMessageQueueListener;
/*      */ import org.gudy.azureus2.plugins.network.OutgoingMessageQueue;
/*      */ import org.gudy.azureus2.plugins.network.OutgoingMessageQueueListener;
/*      */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*      */ import org.gudy.azureus2.plugins.network.Transport;
/*      */ import org.gudy.azureus2.plugins.peers.Peer;
/*      */ import org.gudy.azureus2.plugins.peers.PeerEvent;
/*      */ import org.gudy.azureus2.plugins.peers.PeerListener;
/*      */ import org.gudy.azureus2.plugins.peers.PeerListener2;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*      */ import org.gudy.azureus2.plugins.peers.PeerReadRequest;
/*      */ import org.gudy.azureus2.plugins.peers.PeerStats;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.utils.Monitor;
/*      */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ExternalSeedPeer
/*      */   implements Peer, ExternalSeedReaderListener
/*      */ {
/*      */   private ExternalSeedPlugin plugin;
/*      */   private Download download;
/*      */   private PeerManager manager;
/*      */   private ConnectionStub connection_stub;
/*      */   private PeerStats stats;
/*      */   private Map user_data;
/*      */   private ExternalSeedReader reader;
/*      */   private int state;
/*      */   private byte[] peer_id;
/*      */   private boolean[] available;
/*      */   private boolean availabilityAdded;
/*      */   private long snubbed;
/*      */   private boolean is_optimistic;
/*      */   private Monitor connection_mon;
/*      */   private boolean peer_added;
/*   70 */   private List<PeerReadRequest> request_list = new ArrayList();
/*      */   
/*      */   private CopyOnWriteList listeners;
/*      */   
/*      */   private Monitor listeners_mon;
/*      */   
/*      */   private boolean doing_allocations;
/*   77 */   private final ESConnection connection = new ESConnection(null);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ExternalSeedPeer(ExternalSeedPlugin _plugin, Download _download, ExternalSeedReader _reader)
/*      */   {
/*   86 */     this.plugin = _plugin;
/*   87 */     this.download = _download;
/*   88 */     this.reader = _reader;
/*      */     
/*   90 */     this.connection_mon = this.plugin.getPluginInterface().getUtilities().getMonitor();
/*      */     
/*   92 */     Torrent torrent = this.reader.getTorrent();
/*      */     
/*   94 */     this.available = new boolean[(int)torrent.getPieceCount()];
/*      */     
/*   96 */     Arrays.fill(this.available, true);
/*      */     
/*   98 */     this.peer_id = new byte[20];
/*      */     
/*  100 */     new Random().nextBytes(this.peer_id);
/*      */     
/*  102 */     this.peer_id[0] = 69;
/*  103 */     this.peer_id[1] = 120;
/*  104 */     this.peer_id[2] = 116;
/*  105 */     this.peer_id[3] = 32;
/*      */     
/*  107 */     this.listeners = new CopyOnWriteList();
/*  108 */     this.listeners_mon = this.plugin.getPluginInterface().getUtilities().getMonitor();
/*      */     
/*  110 */     _reader.addListener(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean sameAs(ExternalSeedPeer other)
/*      */   {
/*  117 */     return this.reader.sameAs(other.reader);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setManager(PeerManager _manager)
/*      */   {
/*  124 */     setState(10);
/*      */     try
/*      */     {
/*  127 */       this.connection_mon.enter();
/*      */       
/*  129 */       this.manager = _manager;
/*      */       
/*  131 */       if (this.manager == null)
/*      */       {
/*  133 */         this.stats = null;
/*      */       }
/*      */       else
/*      */       {
/*  137 */         this.stats = this.manager.createPeerStats(this);
/*      */       }
/*      */       
/*  140 */       checkConnection();
/*      */     }
/*      */     finally
/*      */     {
/*  144 */       this.connection_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public PeerManager getManager()
/*      */   {
/*  151 */     return this.manager;
/*      */   }
/*      */   
/*      */ 
/*      */   protected Download getDownload()
/*      */   {
/*  157 */     return this.download;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void bindConnection(ConnectionStub stub)
/*      */   {
/*  164 */     this.connection_stub = stub;
/*      */   }
/*      */   
/*      */ 
/*      */   protected ExternalSeedReader getReader()
/*      */   {
/*  170 */     return this.reader;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setState(int newState)
/*      */   {
/*  177 */     this.state = newState;
/*      */     
/*  179 */     fireEvent(1, new Integer(newState));
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean checkConnection()
/*      */   {
/*  185 */     boolean state_changed = false;
/*      */     try
/*      */     {
/*  188 */       this.connection_mon.enter();
/*      */       
/*  190 */       boolean active = this.reader.checkActivation(this.manager, this);
/*      */       
/*  192 */       if ((this.manager != null) && (active != this.peer_added))
/*      */       {
/*  194 */         state_changed = true;
/*      */         
/*  196 */         boolean peer_was_added = this.peer_added;
/*      */         
/*  198 */         this.peer_added = active;
/*      */         
/*  200 */         if (active)
/*      */         {
/*  202 */           addPeer();
/*      */ 
/*      */ 
/*      */         }
/*  206 */         else if (peer_was_added)
/*      */         {
/*  208 */           removePeer();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  214 */       this.connection_mon.exit();
/*      */     }
/*      */     
/*  217 */     return state_changed;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void addPeer()
/*      */   {
/*  223 */     setState(20);
/*      */     
/*  225 */     this.manager.addPeer(this);
/*      */     
/*      */ 
/*      */ 
/*  229 */     if (this.peer_added)
/*      */     {
/*  231 */       setState(30);
/*      */       try
/*      */       {
/*  234 */         this.listeners_mon.enter();
/*      */         
/*  236 */         if (this.availabilityAdded)
/*      */         {
/*  238 */           Debug.out("availabililty already added");
/*      */         }
/*      */         else
/*      */         {
/*  242 */           this.availabilityAdded = true;
/*      */           
/*  244 */           fireEvent(3, getAvailable());
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/*  249 */         this.listeners_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void removePeer()
/*      */   {
/*  257 */     setState(40);
/*      */     try
/*      */     {
/*  260 */       this.listeners_mon.enter();
/*      */       
/*  262 */       if (this.availabilityAdded)
/*      */       {
/*  264 */         this.availabilityAdded = false;
/*      */         
/*  266 */         fireEvent(4, getAvailable());
/*      */       }
/*      */     }
/*      */     finally {
/*  270 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/*  273 */     this.manager.removePeer(this);
/*      */     
/*  275 */     setState(50);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestComplete(PeerReadRequest request, PooledByteBuffer data)
/*      */   {
/*  283 */     PeerManager man = this.manager;
/*      */     
/*  285 */     if ((request.isCancelled()) || (man == null))
/*      */     {
/*  287 */       data.returnToPool();
/*      */     }
/*      */     else {
/*      */       try
/*      */       {
/*  292 */         man.requestComplete(request, data, this);
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/*  299 */         data.returnToPool();
/*      */         
/*  301 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void requestCancelled(PeerReadRequest request)
/*      */   {
/*  310 */     PeerManager man = this.manager;
/*      */     
/*  312 */     if (man != null)
/*      */     {
/*  314 */       man.requestCancelled(request, this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void requestFailed(PeerReadRequest request)
/*      */   {
/*  322 */     PeerManager man = this.manager;
/*      */     
/*  324 */     if (man != null)
/*      */     {
/*  326 */       man.requestCancelled(request, this);
/*      */       try
/*      */       {
/*  329 */         this.connection_mon.enter();
/*      */         
/*  331 */         if (this.peer_added)
/*      */         {
/*  333 */           this.plugin.log(this.reader.getName() + " failed - " + this.reader.getStatus() + ", permanent = " + this.reader.isPermanentlyUnavailable());
/*      */           
/*  335 */           this.peer_added = false;
/*      */           
/*  337 */           removePeer();
/*      */         }
/*      */       }
/*      */       finally {
/*  341 */         this.connection_mon.exit();
/*      */       }
/*      */       
/*  344 */       if ((this.reader.isTransient()) && (this.reader.isPermanentlyUnavailable()))
/*      */       {
/*  346 */         this.plugin.removePeer(this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getState()
/*      */   {
/*  354 */     return this.state;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getId()
/*      */   {
/*  360 */     return this.peer_id;
/*      */   }
/*      */   
/*      */ 
/*      */   public URL getURL()
/*      */   {
/*  366 */     return this.reader.getURL();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getIp()
/*      */   {
/*  372 */     return this.reader.getIP();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTCPListenPort()
/*      */   {
/*  378 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getUDPListenPort()
/*      */   {
/*  384 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getUDPNonDataListenPort()
/*      */   {
/*  390 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  396 */     return this.reader.getPort();
/*      */   }
/*      */   
/*      */   public boolean isLANLocal()
/*      */   {
/*  401 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public final boolean[] getAvailable()
/*      */   {
/*  407 */     return this.available;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final boolean isPieceAvailable(int pieceNumber)
/*      */   {
/*  414 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isTransferAvailable()
/*      */   {
/*  420 */     return this.reader.isActive();
/*      */   }
/*      */   
/*      */   public boolean isDownloadPossible()
/*      */   {
/*  425 */     return (this.peer_added) && (this.reader.isActive());
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isChoked()
/*      */   {
/*  431 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isChoking()
/*      */   {
/*  437 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isInterested()
/*      */   {
/*  443 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isInteresting()
/*      */   {
/*  449 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSeed()
/*      */   {
/*  455 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSnubbed()
/*      */   {
/*  461 */     if (this.snubbed != 0L)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  466 */       if (this.reader.getRequestCount() == 0)
/*      */       {
/*  468 */         this.snubbed = 0L;
/*      */       }
/*      */     }
/*      */     
/*  472 */     return this.snubbed != 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSnubbedTime()
/*      */   {
/*  478 */     if (!isSnubbed())
/*      */     {
/*  480 */       return 0L;
/*      */     }
/*      */     
/*  483 */     long now = this.plugin.getPluginInterface().getUtilities().getCurrentSystemTime();
/*      */     
/*  485 */     if (now < this.snubbed)
/*      */     {
/*  487 */       this.snubbed = (now - 26L);
/*      */     }
/*      */     
/*  490 */     return now - this.snubbed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSnubbed(boolean b)
/*      */   {
/*  497 */     if (!b)
/*      */     {
/*  499 */       this.snubbed = 0L;
/*      */     }
/*  501 */     else if (this.snubbed == 0L)
/*      */     {
/*  503 */       this.snubbed = this.plugin.getPluginInterface().getUtilities().getCurrentSystemTime();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isOptimisticUnchoke()
/*      */   {
/*  510 */     return this.is_optimistic;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setOptimisticUnchoke(boolean _is_optimistic)
/*      */   {
/*  517 */     this.is_optimistic = _is_optimistic;
/*      */   }
/*      */   
/*      */ 
/*      */   public PeerStats getStats()
/*      */   {
/*  523 */     return this.stats;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isIncoming()
/*      */   {
/*  529 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDone()
/*      */   {
/*  535 */     return 1000;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDoneInThousandNotation()
/*      */   {
/*  541 */     return 1000;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getClient()
/*      */   {
/*  547 */     return this.reader.getName();
/*      */   }
/*      */   
/*      */ 
/*      */   public List getExpiredRequests()
/*      */   {
/*  553 */     return this.reader.getExpiredRequests();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<PeerReadRequest> getRequests()
/*      */   {
/*  560 */     List<PeerReadRequest> requests = this.reader.getRequests();
/*      */     
/*  562 */     if (this.request_list.size() > 0) {
/*      */       try
/*      */       {
/*  565 */         requests.addAll(this.request_list);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  569 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*  573 */     return requests;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaximumNumberOfRequests()
/*      */   {
/*  579 */     return this.reader.getMaximumNumberOfRequests();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNumberOfRequests()
/*      */   {
/*  585 */     return this.reader.getRequestCount() + this.request_list.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int[] getPriorityOffsets()
/*      */   {
/*  592 */     return this.reader.getPriorityOffsets();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean requestAllocationStarts(int[] base_priorities)
/*      */   {
/*  599 */     if (this.doing_allocations)
/*      */     {
/*  601 */       Debug.out("recursive allocations");
/*      */     }
/*      */     
/*  604 */     this.doing_allocations = true;
/*      */     
/*  606 */     if (this.request_list.size() != 0)
/*      */     {
/*  608 */       Debug.out("req list must be empty");
/*      */     }
/*      */     
/*  611 */     PeerManager pm = this.manager;
/*      */     
/*  613 */     if (pm != null)
/*      */     {
/*  615 */       this.reader.calculatePriorityOffsets(pm, base_priorities);
/*      */     }
/*      */     
/*  618 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public void requestAllocationComplete()
/*      */   {
/*  624 */     this.reader.addRequests(this.request_list);
/*      */     
/*  626 */     this.request_list.clear();
/*      */     
/*  628 */     this.doing_allocations = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean addRequest(PeerReadRequest request)
/*      */   {
/*  635 */     if (!this.doing_allocations)
/*      */     {
/*  637 */       Debug.out("request added when not in allocation phase");
/*      */     }
/*      */     
/*  640 */     if (!this.request_list.contains(request))
/*      */     {
/*  642 */       this.request_list.add(request);
/*      */       
/*  644 */       this.snubbed = 0L;
/*      */     }
/*      */     
/*  647 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void cancelRequest(PeerReadRequest request)
/*      */   {
/*  654 */     this.reader.cancelRequest(request);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void close(String reason, boolean closedOnError, boolean attemptReconnect)
/*      */   {
/*      */     boolean peer_was_added;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  666 */       this.connection_mon.enter();
/*      */       
/*  668 */       peer_was_added = this.peer_added;
/*      */       
/*  670 */       this.reader.cancelAllRequests();
/*      */       
/*  672 */       this.reader.deactivate(reason);
/*      */       
/*  674 */       this.peer_added = false;
/*      */       try
/*      */       {
/*  677 */         this.listeners_mon.enter();
/*      */         
/*  679 */         if (this.availabilityAdded)
/*      */         {
/*  681 */           this.availabilityAdded = false;
/*      */           
/*  683 */           fireEvent(4, getAvailable());
/*      */         }
/*      */       }
/*      */       finally {
/*  687 */         this.listeners_mon.exit();
/*      */       }
/*      */     }
/*      */     finally {
/*  691 */       this.connection_mon.exit();
/*      */     }
/*      */     
/*  694 */     if (peer_was_added)
/*      */     {
/*  696 */       this.manager.removePeer(this);
/*      */     }
/*      */     
/*  699 */     setState(50);
/*      */     
/*  701 */     if (this.reader.isTransient())
/*      */     {
/*  703 */       this.plugin.removePeer(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void remove()
/*      */   {
/*  710 */     this.plugin.removePeer(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public void addListener(PeerListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  718 */       this.listeners_mon.enter();
/*      */       
/*  720 */       this.listeners.add(listener);
/*      */     }
/*      */     finally
/*      */     {
/*  724 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeListener(PeerListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  733 */       this.listeners_mon.enter();
/*      */       
/*  735 */       this.listeners.remove(listener);
/*      */     }
/*      */     finally
/*      */     {
/*  739 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addListener(PeerListener2 listener)
/*      */   {
/*      */     try
/*      */     {
/*  748 */       this.listeners_mon.enter();
/*      */       
/*  750 */       this.listeners.add(listener);
/*      */     }
/*      */     finally
/*      */     {
/*  754 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeListener(PeerListener2 listener)
/*      */   {
/*      */     try
/*      */     {
/*  763 */       this.listeners_mon.enter();
/*      */       
/*  765 */       this.listeners.remove(listener);
/*      */     }
/*      */     finally
/*      */     {
/*  769 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireEvent(final int type, final Object data)
/*      */   {
/*      */     try
/*      */     {
/*  779 */       this.listeners_mon.enter();
/*      */       
/*  781 */       List ref = this.listeners.getList();
/*      */       
/*  783 */       for (int i = 0; i < ref.size(); i++) {
/*      */         try
/*      */         {
/*  786 */           Object _listener = ref.get(i);
/*      */           
/*  788 */           if ((_listener instanceof PeerListener))
/*      */           {
/*  790 */             PeerListener listener = (PeerListener)_listener;
/*      */             
/*  792 */             if (type == 1)
/*      */             {
/*  794 */               listener.stateChanged(((Integer)data).intValue());
/*      */             }
/*  796 */             else if (type == 2)
/*      */             {
/*  798 */               Integer[] d = (Integer[])data;
/*      */               
/*  800 */               listener.sentBadChunk(d[0].intValue(), d[1].intValue());
/*      */             }
/*      */           }
/*      */           else {
/*  804 */             PeerListener2 listener = (PeerListener2)_listener;
/*      */             
/*  806 */             listener.eventOccurred(new PeerEvent()
/*      */             {
/*      */ 
/*      */               public int getType()
/*      */               {
/*      */ 
/*  812 */                 return type;
/*      */               }
/*      */               
/*      */ 
/*      */               public Object getData()
/*      */               {
/*  818 */                 return data;
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  824 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  829 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public Connection getConnection()
/*      */   {
/*  835 */     return this.connection;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsMessaging()
/*      */   {
/*  842 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public Message[] getSupportedMessages()
/*      */   {
/*  848 */     return new Message[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int readBytes(int max)
/*      */   {
/*  855 */     int res = this.reader.readBytes(max);
/*      */     
/*  857 */     if (res > 0)
/*      */     {
/*  859 */       this.stats.received(res);
/*      */     }
/*      */     
/*  862 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int writeBytes(int max)
/*      */   {
/*  869 */     throw new RuntimeException("Not supported");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addRateLimiter(RateLimiter limiter, boolean is_upload)
/*      */   {
/*  877 */     if (this.connection_stub != null)
/*      */     {
/*  879 */       this.connection_stub.addRateLimiter(limiter, is_upload);
/*      */     }
/*      */     else
/*      */     {
/*  883 */       Debug.out("connection not bound");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeRateLimiter(RateLimiter limiter, boolean is_upload)
/*      */   {
/*  892 */     if (this.connection_stub != null)
/*      */     {
/*  894 */       this.connection_stub.removeRateLimiter(limiter, is_upload);
/*      */     }
/*      */     else
/*      */     {
/*  898 */       Debug.out("connection not bound");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public RateLimiter[] getRateLimiters(boolean is_upload)
/*      */   {
/*  906 */     if (this.connection_stub != null)
/*      */     {
/*  908 */       return this.connection_stub.getRateLimiters(is_upload);
/*      */     }
/*      */     
/*      */ 
/*  912 */     Debug.out("connection not bound");
/*      */     
/*  914 */     return new RateLimiter[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getPercentDoneOfCurrentIncomingRequest()
/*      */   {
/*  921 */     return this.reader.getPercentDoneOfCurrentIncomingRequest();
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getOutgoingRequestedPieceNumbers()
/*      */   {
/*  927 */     return this.reader.getOutgoingRequestedPieceNumbers();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getOutgoingRequestCount()
/*      */   {
/*  933 */     return this.reader.getOutgoingRequestCount();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getPercentDoneOfCurrentOutgoingRequest()
/*      */   {
/*  940 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public Map getProperties()
/*      */   {
/*  946 */     return new HashMap();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  952 */     return this.reader.getName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUserData(Object key, Object value)
/*      */   {
/*  960 */     if (this.user_data == null)
/*      */     {
/*  962 */       this.user_data = new HashMap();
/*      */     }
/*      */     
/*  965 */     this.user_data.put(key, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getUserData(Object key)
/*      */   {
/*  972 */     if (key == Peer.PR_PROTOCOL)
/*      */     {
/*  974 */       return this.reader.getURL().getProtocol().toUpperCase();
/*      */     }
/*  976 */     if (key == Peer.PR_PROTOCOL_QUALIFIER)
/*      */     {
/*  978 */       return this.reader.getType();
/*      */     }
/*      */     
/*  981 */     if (this.user_data == null)
/*      */     {
/*  983 */       return null;
/*      */     }
/*      */     
/*  986 */     return this.user_data.get(key);
/*      */   }
/*      */   
/*      */   public byte[] getHandshakeReservedBytes() {
/*  990 */     return null;
/*      */   }
/*      */   
/*      */   public boolean isPriorityConnection() {
/*  994 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void setPriorityConnection(boolean is_priority) {}
/*      */   
/*      */ 
/*      */   private class ESConnection
/*      */     implements Connection
/*      */   {
/* 1004 */     private OutgoingMessageQueue out_q = new OutgoingMessageQueue()
/*      */     {
/*      */       public void setEncoder(MessageStreamEncoder encoder) {}
/*      */       
/*      */ 
/*      */       public void sendMessage(Message message) {}
/*      */       
/*      */       public void registerListener(OutgoingMessageQueueListener listener) {}
/*      */       
/*      */       public void deregisterListener(OutgoingMessageQueueListener listener) {}
/*      */       
/*      */       public void notifyOfExternalSend(Message message) {}
/*      */       
/* 1017 */       public int getPercentDoneOfCurrentMessage() { return -1; }
/*      */       
/* 1019 */       public int getDataQueuedBytes() { return 0; }
/*      */       
/* 1021 */       public int getProtocolQueuedBytes() { return 0; }
/*      */       
/* 1023 */       public boolean isBlocked() { return false; }
/*      */     };
/*      */     
/* 1026 */     private IncomingMessageQueue in_q = new IncomingMessageQueue()
/*      */     {
/*      */       public void registerListener(IncomingMessageQueueListener listener) {}
/*      */       
/*      */       public void registerPriorityListener(IncomingMessageQueueListener listener) {}
/*      */       
/*      */       public void deregisterListener(IncomingMessageQueueListener listener) {}
/*      */       
/*      */       public void notifyOfExternalReceive(Message message) {}
/*      */       
/*      */       public int getPercentDoneOfCurrentMessage() {
/* 1037 */         return ExternalSeedPeer.this.getPercentDoneOfCurrentIncomingRequest();
/*      */       }
/*      */     };
/*      */     
/*      */ 
/*      */     private ESConnection() {}
/*      */     
/*      */ 
/*      */     public void connect(ConnectionListener listener) {}
/*      */     
/*      */     public void close()
/*      */     {
/* 1049 */       Debug.out("hmm");
/*      */     }
/*      */     
/*      */ 
/*      */     public OutgoingMessageQueue getOutgoingMessageQueue()
/*      */     {
/* 1055 */       return this.out_q;
/*      */     }
/*      */     
/*      */ 
/*      */     public IncomingMessageQueue getIncomingMessageQueue()
/*      */     {
/* 1061 */       return this.in_q;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void startMessageProcessing() {}
/*      */     
/*      */ 
/*      */ 
/*      */     public Transport getTransport()
/*      */     {
/* 1072 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isIncoming()
/*      */     {
/* 1078 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getString()
/*      */     {
/* 1084 */       return "External Seed";
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/ExternalSeedPeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */