/*      */ package org.gudy.azureus2.pluginsimpl.local.peers;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkConnectionBase;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;
/*      */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItemFactory;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*      */ import com.aelitis.azureus.core.tag.TaggableResolver;
/*      */ import java.net.InetAddress;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerListener;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.network.Connection;
/*      */ import org.gudy.azureus2.plugins.network.ConnectionStub;
/*      */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*      */ import org.gudy.azureus2.plugins.peers.Peer;
/*      */ import org.gudy.azureus2.plugins.peers.PeerEvent;
/*      */ import org.gudy.azureus2.plugins.peers.PeerListener;
/*      */ import org.gudy.azureus2.plugins.peers.PeerListener2;
/*      */ import org.gudy.azureus2.plugins.peers.PeerReadRequest;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageAdapter;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.PluginLimitedRateGroup;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PeerForeignDelegate
/*      */   implements PEPeerTransport
/*      */ {
/*   69 */   protected volatile int _lastPiece = -1;
/*      */   
/*      */   private PeerManagerImpl manager;
/*      */   
/*      */   private Peer foreign;
/*      */   
/*      */   private NetworkConnectionBase network_connection;
/*   76 */   private long create_time = SystemTime.getCurrentTime();
/*   77 */   private long last_data_received_time = -1L;
/*   78 */   private long last_data_message_received_time = -1L;
/*   79 */   private int[] reserved_pieces = null;
/*      */   
/*      */   private int consecutive_no_requests;
/*      */   
/*      */   private BitFlags bit_flags;
/*      */   
/*      */   private boolean priority_connection;
/*      */   
/*      */   private Map data;
/*      */   
/*      */   private HashMap peer_listeners;
/*   90 */   protected AEMonitor this_mon = new AEMonitor("PeerForeignDelegate");
/*      */   
/*      */ 
/*      */   private Set<Object> download_disabled_set;
/*      */   
/*      */ 
/*      */   private boolean is_download_disabled;
/*      */   
/*      */   private volatile boolean closed;
/*      */   
/*      */ 
/*      */   protected PeerForeignDelegate(PeerManagerImpl _manager, Peer _foreign)
/*      */   {
/*  103 */     this.manager = _manager;
/*  104 */     this.foreign = _foreign;
/*      */     
/*  106 */     PEPeerManager pm = this.manager.getDelegate();
/*      */     
/*  108 */     this.network_connection = new PeerForeignNetworkConnection(this, this.foreign);
/*      */     
/*  110 */     this.network_connection.addRateLimiter(pm.getUploadLimitedRateGroup(), true);
/*  111 */     this.network_connection.addRateLimiter(pm.getDownloadLimitedRateGroup(), false);
/*      */     
/*  113 */     _foreign.bindConnection(new ConnectionStub()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void addRateLimiter(RateLimiter limiter, boolean is_upload)
/*      */       {
/*      */ 
/*      */ 
/*  121 */         PeerForeignDelegate.this.network_connection.addRateLimiter(UtilitiesImpl.wrapLimiter(limiter, false), is_upload);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void removeRateLimiter(RateLimiter limiter, boolean is_upload)
/*      */       {
/*  129 */         PeerForeignDelegate.this.network_connection.removeRateLimiter(UtilitiesImpl.wrapLimiter(limiter, false), is_upload);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public RateLimiter[] getRateLimiters(boolean is_upload)
/*      */       {
/*  136 */         LimitedRateGroup[] limiters = PeerForeignDelegate.this.network_connection.getRateLimiters(is_upload);
/*      */         
/*  138 */         RateLimiter[] result = new RateLimiter[limiters.length];
/*      */         
/*  140 */         int pos = 0;
/*      */         
/*  142 */         for (LimitedRateGroup l : limiters)
/*      */         {
/*  144 */           if ((l instanceof UtilitiesImpl.PluginLimitedRateGroup))
/*      */           {
/*  146 */             result[(pos++)] = UtilitiesImpl.unwrapLmiter((UtilitiesImpl.PluginLimitedRateGroup)l);
/*      */           }
/*      */         }
/*      */         
/*  150 */         if (pos == result.length)
/*      */         {
/*  152 */           return result;
/*      */         }
/*      */         
/*  155 */         RateLimiter[] result_mod = new RateLimiter[pos];
/*      */         
/*  157 */         System.arraycopy(result, 0, result_mod, 0, pos);
/*      */         
/*  159 */         return result_mod;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public void start()
/*      */   {
/*  167 */     NetworkManager.getSingleton().startTransferProcessing(this.network_connection);
/*      */     
/*  169 */     NetworkManager.getSingleton().upgradeTransferProcessing(this.network_connection, this.manager.getPartitionID());
/*      */   }
/*      */   
/*      */ 
/*      */   protected void stop()
/*      */   {
/*  175 */     NetworkManager.getSingleton().stopTransferProcessing(this.network_connection);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void sendChoke() {}
/*      */   
/*      */ 
/*      */ 
/*      */   public void sendHave(int piece) {}
/*      */   
/*      */ 
/*      */ 
/*      */   public void sendUnChoke() {}
/*      */   
/*      */ 
/*      */   public InetAddress getAlternativeIPv6()
/*      */   {
/*  193 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean transferAvailable()
/*      */   {
/*  200 */     return this.foreign.isTransferAvailable();
/*      */   }
/*      */   
/*      */   public boolean isDownloadPossible()
/*      */   {
/*  205 */     if (this.is_download_disabled)
/*      */     {
/*  207 */       return false;
/*      */     }
/*      */     
/*  210 */     return this.foreign.isDownloadPossible();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void sendCancel(DiskManagerReadRequest request)
/*      */   {
/*  217 */     this.foreign.cancelRequest(request);
/*      */   }
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
/*      */   public DiskManagerReadRequest request(int pieceNumber, int pieceOffset, int pieceLength, boolean return_duplicates)
/*      */   {
/*  234 */     DiskManagerReadRequest request = this.manager.getDelegate().getDiskManager().createReadRequest(pieceNumber, pieceOffset, pieceLength);
/*      */     
/*  236 */     if (this.foreign.addRequest(request))
/*      */     {
/*  238 */       return request;
/*      */     }
/*      */     
/*      */ 
/*  242 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getRequestIndex(DiskManagerReadRequest request)
/*      */   {
/*  250 */     return this.foreign.getRequests().indexOf(request);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void dataReceived()
/*      */   {
/*  256 */     this.last_data_received_time = SystemTime.getCurrentTime();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void closeConnection(String reason)
/*      */   {
/*  263 */     this.closed = true;
/*      */     try
/*      */     {
/*  266 */       this.foreign.close(reason, false, false);
/*      */     }
/*      */     finally
/*      */     {
/*  270 */       stop();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/*  277 */     return this.closed;
/*      */   }
/*      */   
/*      */ 
/*      */   public List getExpiredRequests()
/*      */   {
/*  283 */     return this.foreign.getExpiredRequests();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLastMessageSentTime()
/*      */   {
/*  289 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxNbRequests()
/*      */   {
/*  295 */     return this.foreign.getMaximumNumberOfRequests();
/*      */   }
/*      */   
/*      */   public int getNbRequests()
/*      */   {
/*  300 */     return this.foreign.getNumberOfRequests();
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getPriorityOffsets()
/*      */   {
/*  306 */     return this.foreign.getPriorityOffsets();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean requestAllocationStarts(int[] base_priorities)
/*      */   {
/*  313 */     return this.foreign.requestAllocationStarts(base_priorities);
/*      */   }
/*      */   
/*      */ 
/*      */   public void requestAllocationComplete()
/*      */   {
/*  319 */     this.foreign.requestAllocationComplete();
/*      */   }
/*      */   
/*      */ 
/*      */   public PEPeerControl getControl()
/*      */   {
/*  325 */     return (PEPeerControl)this.manager.getDelegate();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updatePeerExchange() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeerItem getPeerItemIdentity()
/*      */   {
/*  338 */     return PeerItemFactory.createPeerItem(this.foreign.getIp(), this.foreign.getTCPListenPort(), (byte)3, (byte)0, this.foreign.getUDPListenPort(), (byte)1, 0);
/*      */   }
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
/*      */   public int getConnectionState()
/*      */   {
/*  352 */     int peer_state = getPeerState();
/*      */     
/*  354 */     if (peer_state == 10)
/*      */     {
/*  356 */       return 1;
/*      */     }
/*  358 */     if (peer_state == 20)
/*      */     {
/*  360 */       return 2;
/*      */     }
/*  362 */     if (peer_state == 30)
/*      */     {
/*  364 */       return 4;
/*      */     }
/*      */     
/*      */ 
/*  368 */     return 4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void doKeepAliveCheck() {}
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean doTimeoutChecks()
/*      */   {
/*  380 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void doPerformanceTuningCheck() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSuspendedLazyBitFieldEnabled(boolean enable) {}
/*      */   
/*      */ 
/*      */ 
/*      */   public long getTimeSinceConnectionEstablished()
/*      */   {
/*  397 */     long now = SystemTime.getCurrentTime();
/*      */     
/*  399 */     if (now > this.create_time)
/*      */     {
/*  401 */       return now - this.create_time;
/*      */     }
/*      */     
/*  404 */     return 0L;
/*      */   }
/*      */   
/*      */   public long getTimeSinceLastDataMessageReceived() {
/*  408 */     if (this.last_data_message_received_time == -1L) {
/*  409 */       return -1L;
/*      */     }
/*  411 */     long now = SystemTime.getCurrentTime();
/*  412 */     if (this.last_data_message_received_time < now)
/*  413 */       this.last_data_message_received_time = now;
/*  414 */     return now - this.last_data_message_received_time;
/*      */   }
/*      */   
/*      */   public long getTimeSinceGoodDataReceived()
/*      */   {
/*  419 */     if (this.last_data_received_time == -1L)
/*  420 */       return -1L;
/*  421 */     long now = SystemTime.getCurrentTime();
/*  422 */     long time_since = now - this.last_data_received_time;
/*      */     
/*  424 */     if (time_since < 0L)
/*      */     {
/*  426 */       this.last_data_received_time = now;
/*  427 */       time_since = 0L;
/*      */     }
/*      */     
/*  430 */     return time_since;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTimeSinceLastDataMessageSent()
/*      */   {
/*  436 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getUnchokedForMillis()
/*      */   {
/*  442 */     return 0L;
/*      */   }
/*      */   
/*      */   public long getLatency() {
/*  446 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getConsecutiveNoRequestCount()
/*      */   {
/*  452 */     return this.consecutive_no_requests;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setConsecutiveNoRequestCount(int num)
/*      */   {
/*  459 */     this.consecutive_no_requests = num;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PEPeerManager getManager()
/*      */   {
/*  467 */     return this.manager.getDelegate();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getPeerSource()
/*      */   {
/*  473 */     return "Plugin";
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPeerState()
/*      */   {
/*  479 */     int peer_state = this.foreign.getState();
/*      */     
/*  481 */     return peer_state;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getId()
/*      */   {
/*  490 */     return this.foreign.getId();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getIp()
/*      */   {
/*  497 */     return this.foreign.getIp();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getIPHostName()
/*      */   {
/*  503 */     return this.foreign.getIp();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  509 */     return this.foreign.getPort();
/*      */   }
/*      */   
/*      */ 
/*  513 */   public int getTCPListenPort() { return this.foreign.getTCPListenPort(); }
/*  514 */   public int getUDPListenPort() { return this.foreign.getUDPListenPort(); }
/*  515 */   public int getUDPNonDataListenPort() { return this.foreign.getUDPNonDataListenPort(); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public BitFlags getAvailable()
/*      */   {
/*  523 */     boolean[] flags = this.foreign.getAvailable();
/*      */     
/*  525 */     if (flags != null)
/*      */     {
/*  527 */       if ((this.bit_flags == null) || (this.bit_flags.flags != flags))
/*      */       {
/*  529 */         this.bit_flags = new BitFlags(flags);
/*      */       }
/*      */     }
/*      */     
/*  533 */     return this.bit_flags;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasReceivedBitField()
/*      */   {
/*  539 */     return true;
/*      */   }
/*      */   
/*      */   public boolean isPieceAvailable(int pieceNumber)
/*      */   {
/*  544 */     return this.foreign.isPieceAvailable(pieceNumber);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setSnubbed(boolean b)
/*      */   {
/*  550 */     this.foreign.setSnubbed(b);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isChokingMe()
/*      */   {
/*  557 */     if (this.is_download_disabled)
/*      */     {
/*  559 */       return true;
/*      */     }
/*      */     
/*  562 */     return this.foreign.isChoked();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isChokedByMe()
/*      */   {
/*  569 */     return this.foreign.isChoking();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isUnchokeOverride()
/*      */   {
/*  575 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isInteresting()
/*      */   {
/*  581 */     if (this.is_download_disabled)
/*      */     {
/*  583 */       return false;
/*      */     }
/*      */     
/*  586 */     return this.foreign.isInteresting();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isInterested()
/*      */   {
/*  593 */     return this.foreign.isInterested();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isSeed()
/*      */   {
/*  600 */     return this.foreign.isSeed();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isRelativeSeed()
/*      */   {
/*  606 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isSnubbed()
/*      */   {
/*  613 */     return this.foreign.isSnubbed();
/*      */   }
/*      */   
/*      */   public long getSnubbedTime()
/*      */   {
/*  618 */     return this.foreign.getSnubbedTime();
/*      */   }
/*      */   
/*      */   public boolean isLANLocal()
/*      */   {
/*  623 */     return AddressUtils.isLANLocalAddress(this.foreign.getIp()) == 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean sendRequestHint(int piece_number, int offset, int length, int life)
/*      */   {
/*  633 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getRequestHint()
/*      */   {
/*  639 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clearRequestHint() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sendRejectRequest(DiskManagerReadRequest request) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sendBadPiece(int piece_number) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sendStatsRequest(Map request) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sendStatsReply(Map reply) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isTCP()
/*      */   {
/*  674 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getNetwork()
/*      */   {
/*  680 */     return AENetworkClassifier.categoriseAddress(getIp());
/*      */   }
/*      */   
/*      */ 
/*      */   public PEPeerStats getStats()
/*      */   {
/*  686 */     return ((PeerStatsImpl)this.foreign.getStats()).getDelegate();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isIncoming()
/*      */   {
/*  693 */     return this.foreign.isIncoming();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDoneInThousandNotation()
/*      */   {
/*  699 */     return this.foreign.getPercentDoneInThousandNotation();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getBytesRemaining()
/*      */   {
/*  705 */     int rem_pm = 1000 - getPercentDoneInThousandNotation();
/*      */     
/*  707 */     if (rem_pm == 0)
/*      */     {
/*  709 */       return 0L;
/*      */     }
/*      */     try
/*      */     {
/*  713 */       Torrent t = this.manager.getDownload().getTorrent();
/*      */       
/*  715 */       if (t == null)
/*      */       {
/*  717 */         return Long.MAX_VALUE;
/*      */       }
/*      */       
/*  720 */       return t.getSize() * rem_pm / 1000L;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  724 */     return Long.MAX_VALUE;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getClient()
/*      */   {
/*  731 */     return this.foreign.getClient();
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getHandshakeReservedBytes()
/*      */   {
/*  737 */     return this.foreign.getHandshakeReservedBytes();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isOptimisticUnchoke()
/*      */   {
/*  743 */     return this.foreign.isOptimisticUnchoke();
/*      */   }
/*      */   
/*      */   public void setOptimisticUnchoke(boolean is_optimistic) {
/*  747 */     this.foreign.setOptimisticUnchoke(is_optimistic);
/*      */   }
/*      */   
/*      */   public int getUniqueAnnounce()
/*      */   {
/*  752 */     return -1;
/*      */   }
/*      */   
/*      */   public int getUploadHint()
/*      */   {
/*  757 */     return 0;
/*      */   }
/*      */   
/*      */   public void setUniqueAnnounce(int uniquePieceNumber) {}
/*      */   
/*      */   public void setUploadHint(int timeToSpread) {}
/*      */   
/*      */   public boolean isStalledPendingLoad() {
/*  765 */     return false;
/*      */   }
/*      */   
/*      */   public void addListener(final PEPeerListener l) {
/*  769 */     final PEPeer self = this;
/*      */     
/*  771 */     PeerListener2 core_listener = new PeerListener2()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void eventOccurred(PeerEvent event)
/*      */       {
/*      */ 
/*  778 */         Object data = event.getData();
/*      */         
/*  780 */         switch (event.getType()) {
/*      */         case 1: 
/*  782 */           l.stateChanged(self, ((Integer)data).intValue());
/*  783 */           break;
/*      */         
/*      */         case 2: 
/*  786 */           Integer[] d = (Integer[])data;
/*  787 */           l.sentBadChunk(self, d[0].intValue(), d[1].intValue());
/*  788 */           break;
/*      */         
/*      */         case 3: 
/*  791 */           l.addAvailability(self, new BitFlags((boolean[])data));
/*  792 */           break;
/*      */         
/*      */         case 4: 
/*  795 */           l.removeAvailability(self, new BitFlags((boolean[])data));
/*      */         
/*      */ 
/*      */         }
/*      */         
/*      */       }
/*  801 */     };
/*  802 */     this.foreign.addListener(core_listener);
/*      */     
/*  804 */     if (this.peer_listeners == null)
/*      */     {
/*  806 */       this.peer_listeners = new HashMap();
/*      */     }
/*      */     
/*  809 */     this.peer_listeners.put(l, core_listener);
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeListener(PEPeerListener l)
/*      */   {
/*  815 */     if (this.peer_listeners != null)
/*      */     {
/*  817 */       Object core_listener = this.peer_listeners.remove(l);
/*      */       
/*  819 */       if (core_listener != null)
/*      */       {
/*  821 */         if ((core_listener instanceof PeerListener))
/*      */         {
/*  823 */           this.foreign.removeListener((PeerListener)core_listener);
/*      */         }
/*      */         else
/*      */         {
/*  827 */           this.foreign.removeListener((PeerListener2)core_listener);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Connection getPluginConnection()
/*      */   {
/*  837 */     return this.foreign.getConnection();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDoneOfCurrentIncomingRequest()
/*      */   {
/*  843 */     return this.foreign.getPercentDoneOfCurrentIncomingRequest();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDoneOfCurrentOutgoingRequest()
/*      */   {
/*  849 */     return this.foreign.getPercentDoneOfCurrentOutgoingRequest();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsMessaging()
/*      */   {
/*  855 */     return this.foreign.supportsMessaging();
/*      */   }
/*      */   
/*      */   public int getMessagingMode()
/*      */   {
/*  860 */     return 4;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getEncryption()
/*      */   {
/*  866 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getProtocol()
/*      */   {
/*  872 */     String res = (String)this.foreign.getUserData(Peer.PR_PROTOCOL);
/*      */     
/*  874 */     if (res != null)
/*      */     {
/*  876 */       return res;
/*      */     }
/*      */     
/*  879 */     return "Plugin";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getProtocolQualifier()
/*      */   {
/*  885 */     return (String)this.foreign.getUserData(Peer.PR_PROTOCOL_QUALIFIER);
/*      */   }
/*      */   
/*      */ 
/*      */   public com.aelitis.azureus.core.peermanager.messaging.Message[] getSupportedMessages()
/*      */   {
/*  891 */     org.gudy.azureus2.plugins.messaging.Message[] plug_msgs = this.foreign.getSupportedMessages();
/*      */     
/*  893 */     com.aelitis.azureus.core.peermanager.messaging.Message[] core_msgs = new com.aelitis.azureus.core.peermanager.messaging.Message[plug_msgs.length];
/*      */     
/*  895 */     for (int i = 0; i < plug_msgs.length; i++) {
/*  896 */       core_msgs[i] = new MessageAdapter(plug_msgs[i]);
/*      */     }
/*      */     
/*  899 */     return core_msgs;
/*      */   }
/*      */   
/*      */   public Object getData(String key)
/*      */   {
/*  904 */     return getUserData(key);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setData(String key, Object value)
/*      */   {
/*  910 */     setUserData(key, value);
/*      */   }
/*      */   
/*      */   public Object getUserData(Object key)
/*      */   {
/*      */     try {
/*  916 */       this.this_mon.enter();
/*  917 */       Object localObject1; if (this.data == null) return null;
/*  918 */       return this.data.get(key);
/*      */     }
/*      */     finally {
/*  921 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUserData(Object key, Object value)
/*      */   {
/*      */     try {
/*  928 */       this.this_mon.enter();
/*      */       
/*  930 */       if (this.data == null) {
/*  931 */         this.data = new LightHashMap();
/*      */       }
/*  933 */       if (value == null) {
/*  934 */         if (this.data.containsKey(key)) {
/*  935 */           this.data.remove(key);
/*  936 */           if (this.data.size() == 0) {
/*  937 */             this.data = null;
/*      */           }
/*      */         }
/*      */       } else {
/*  941 */         this.data.put(key, value);
/*      */       }
/*      */     }
/*      */     finally {
/*  945 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean equals(Object other)
/*      */   {
/*  953 */     if ((other instanceof PeerForeignDelegate))
/*      */     {
/*  955 */       return this.foreign.equals(((PeerForeignDelegate)other).foreign);
/*      */     }
/*      */     
/*  958 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  964 */     return this.foreign.hashCode();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[] getReservedPieceNumbers()
/*      */   {
/*  972 */     return this.reserved_pieces;
/*      */   }
/*      */   
/*      */ 
/*      */   public void addReservedPieceNumber(int piece_number)
/*      */   {
/*  978 */     int[] existing = this.reserved_pieces;
/*      */     
/*  980 */     if (existing == null)
/*      */     {
/*  982 */       this.reserved_pieces = new int[] { piece_number };
/*      */     }
/*      */     else
/*      */     {
/*  986 */       int[] updated = new int[existing.length + 1];
/*      */       
/*  988 */       System.arraycopy(existing, 0, updated, 0, existing.length);
/*      */       
/*  990 */       updated[existing.length] = piece_number;
/*      */       
/*  992 */       this.reserved_pieces = updated;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeReservedPieceNumber(int piece_number)
/*      */   {
/*  999 */     int[] existing = this.reserved_pieces;
/*      */     
/* 1001 */     if (existing != null)
/*      */     {
/* 1003 */       if (existing.length == 1)
/*      */       {
/* 1005 */         if (existing[0] == piece_number)
/*      */         {
/* 1007 */           this.reserved_pieces = null;
/*      */         }
/*      */       }
/*      */       else {
/* 1011 */         int[] updated = new int[existing.length - 1];
/*      */         
/* 1013 */         int pos = 0;
/* 1014 */         boolean found = false;
/*      */         
/* 1016 */         for (int i = 0; i < existing.length; i++)
/*      */         {
/* 1018 */           int pn = existing[i];
/*      */           
/* 1020 */           if ((found) || (pn != piece_number))
/*      */           {
/* 1022 */             if (pos == updated.length)
/*      */             {
/* 1024 */               return;
/*      */             }
/*      */             
/* 1027 */             updated[(pos++)] = pn;
/*      */           }
/*      */           else
/*      */           {
/* 1031 */             found = true;
/*      */           }
/*      */         }
/*      */         
/* 1035 */         this.reserved_pieces = updated;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getIncomingRequestedPieceNumbers()
/*      */   {
/* 1043 */     return new int[0];
/*      */   }
/*      */   
/*      */ 
/*      */   public int getIncomingRequestCount()
/*      */   {
/* 1049 */     return 0;
/*      */   }
/*      */   
/*      */   public int getOutgoingRequestCount()
/*      */   {
/* 1054 */     return this.foreign.getOutgoingRequestCount();
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getOutgoingRequestedPieceNumbers()
/*      */   {
/* 1060 */     return this.foreign.getOutgoingRequestedPieceNumbers();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getOutboundDataQueueSize()
/*      */   {
/* 1067 */     return getOutgoingRequestCount() * 16384;
/*      */   }
/*      */   
/*      */   public int getLastPiece()
/*      */   {
/* 1072 */     return this._lastPiece;
/*      */   }
/*      */   
/*      */   public void setLastPiece(int pieceNumber)
/*      */   {
/* 1077 */     this._lastPiece = pieceNumber;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void checkInterested() {}
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isAvailabilityAdded()
/*      */   {
/* 1088 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void clearAvailabilityAdded() {}
/*      */   
/*      */ 
/* 1095 */   public PEPeerTransport reconnect(boolean tryUDP, boolean tryIPv6) { return null; }
/* 1096 */   public boolean isSafeForReconnect() { return false; }
/*      */   
/* 1098 */   public void setUploadRateLimitBytesPerSecond(int bytes) { this.network_connection.setUploadLimit(bytes); }
/* 1099 */   public void setDownloadRateLimitBytesPerSecond(int bytes) { this.network_connection.setDownloadLimit(bytes); }
/* 1100 */   public int getUploadRateLimitBytesPerSecond() { return this.network_connection.getUploadLimit(); }
/* 1101 */   public int getDownloadRateLimitBytesPerSecond() { return this.network_connection.getDownloadLimit(); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updateAutoUploadPriority(Object key, boolean inc) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addRateLimiter(LimitedRateGroup limiter, boolean upload)
/*      */   {
/* 1115 */     this.network_connection.addRateLimiter(limiter, upload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public LimitedRateGroup[] getRateLimiters(boolean upload)
/*      */   {
/* 1122 */     return this.network_connection.getRateLimiters(upload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeRateLimiter(LimitedRateGroup limiter, boolean upload)
/*      */   {
/* 1130 */     this.network_connection.removeRateLimiter(limiter, upload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUploadDisabled(Object key, boolean disabled) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDownloadDisabled(Object key, boolean disabled)
/*      */   {
/* 1146 */     synchronized (this)
/*      */     {
/* 1148 */       if (this.download_disabled_set == null)
/*      */       {
/* 1150 */         if (disabled)
/*      */         {
/* 1152 */           this.download_disabled_set = new HashSet();
/*      */           
/* 1154 */           this.download_disabled_set.add(key);
/*      */         }
/*      */         else
/*      */         {
/* 1158 */           Debug.out("derp");
/*      */         }
/*      */         
/*      */       }
/* 1162 */       else if (disabled)
/*      */       {
/* 1164 */         if (!this.download_disabled_set.add(key))
/*      */         {
/* 1166 */           Debug.out("derp");
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1171 */         if (!this.download_disabled_set.remove(key))
/*      */         {
/* 1173 */           Debug.out("derp");
/*      */         }
/*      */         
/* 1176 */         if (this.download_disabled_set.size() == 0)
/*      */         {
/* 1178 */           this.download_disabled_set = null;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1183 */       this.is_download_disabled = (this.download_disabled_set != null);
/*      */       
/* 1185 */       if (this.is_download_disabled)
/*      */       {
/* 1187 */         List<Object> list = this.foreign.getRequests();
/*      */         
/* 1189 */         if (list != null)
/*      */         {
/* 1191 */           for (Object obj : list)
/*      */           {
/* 1193 */             if ((obj instanceof PeerReadRequest))
/*      */             {
/* 1195 */               this.foreign.cancelRequest((PeerReadRequest)obj);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isUploadDisabled()
/*      */   {
/* 1207 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isDownloadDisabled()
/*      */   {
/* 1213 */     return this.is_download_disabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setHaveAggregationEnabled(boolean enabled) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPriorityConnection(boolean is_priority)
/*      */   {
/* 1226 */     this.priority_connection = is_priority;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPriorityConnection()
/*      */   {
/* 1232 */     return this.priority_connection;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generateEvidence(IndentWriter writer)
/*      */   {
/* 1239 */     writer.println("delegate: ip=" + getIp() + ",tcp=" + getTCPListenPort() + ",udp=" + getUDPListenPort() + ",state=" + this.foreign.getState() + ",foreign=" + this.foreign);
/*      */   }
/*      */   
/* 1242 */   public String getClientNameFromExtensionHandshake() { return null; }
/* 1243 */   public String getClientNameFromPeerID() { return null; }
/*      */   
/* 1245 */   public int getTaggableType() { return 4; }
/* 1246 */   public String getTaggableID() { return null; }
/* 1247 */   public TaggableResolver getTaggableResolver() { return null; }
/*      */   
/*      */   public Object getTaggableTransientProperty(String key) {
/* 1250 */     return null;
/*      */   }
/*      */   
/*      */   public void setTaggableTransientProperty(String key, Object value) {}
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/peers/PeerForeignDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */