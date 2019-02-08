/*      */ package com.aelitis.azureus.core.peermanager;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager.ByteMatcher;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager.RoutingListener;
/*      */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*      */ import com.aelitis.azureus.core.networkmanager.Transport;
/*      */ import com.aelitis.azureus.core.networkmanager.TransportEndpoint;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.IncomingConnectionManager;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.MessageManager;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamFactory;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessageDecoder;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessageEncoder;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*      */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*      */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerListener;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransportFactory;
/*      */ import org.gudy.azureus2.core3.peer.util.PeerIdentityManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AEGenericCallback;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunStateHandler;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ 
/*      */ public class PeerManager
/*      */   implements AzureusCoreStatsProvider
/*      */ {
/*   61 */   private static final LogIDs LOGID = LogIDs.PEER;
/*      */   
/*   63 */   private static final PeerManager instance = new PeerManager();
/*      */   
/*      */   private static final int PENDING_TIMEOUT = 10000;
/*      */   
/*   67 */   private static final AEMonitor timer_mon = new AEMonitor("PeerManager:timeouts");
/*      */   private static AEThread2 timer_thread;
/*   69 */   static final Set timer_targets = new HashSet();
/*      */   
/*      */ 
/*      */   protected static void registerForTimeouts(PeerManagerRegistrationImpl reg)
/*      */   {
/*      */     try
/*      */     {
/*   76 */       timer_mon.enter();
/*      */       
/*   78 */       timer_targets.add(reg);
/*      */       
/*   80 */       if (timer_thread == null)
/*      */       {
/*   82 */         timer_thread = new AEThread2("PeerManager:timeouts", true)
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/*   88 */             int idle_time = 0;
/*      */             for (;;)
/*      */             {
/*      */               try
/*      */               {
/*   93 */                 Thread.sleep(5000L);
/*      */               }
/*      */               catch (Throwable e) {}
/*      */               
/*      */               try
/*      */               {
/*   99 */                 PeerManager.timer_mon.enter();
/*      */                 
/*  101 */                 if (PeerManager.timer_targets.size() == 0)
/*      */                 {
/*  103 */                   idle_time += 5000;
/*      */                   
/*  105 */                   if (idle_time >= 30000)
/*      */                   {
/*  107 */                     PeerManager.access$102(null);
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
/*  129 */                     PeerManager.timer_mon.exit(); break;
/*      */                   }
/*      */                 }
/*      */                 else
/*      */                 {
/*  113 */                   idle_time = 0;
/*      */                   
/*  115 */                   Iterator it = PeerManager.timer_targets.iterator();
/*      */                   
/*  117 */                   while (it.hasNext())
/*      */                   {
/*  119 */                     PeerManager.PeerManagerRegistrationImpl registration = (PeerManager.PeerManagerRegistrationImpl)it.next();
/*      */                     
/*  121 */                     if (!registration.timeoutCheck())
/*      */                     {
/*  123 */                       it.remove();
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               finally {
/*  129 */                 PeerManager.timer_mon.exit();
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*  134 */         };
/*  135 */         timer_thread.start();
/*      */       }
/*      */     }
/*      */     finally {
/*  139 */       timer_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static PeerManager getSingleton()
/*      */   {
/*  147 */     return instance;
/*      */   }
/*      */   
/*      */ 
/*  151 */   private final Map<HashWrapper, List<PeerManagerRegistrationImpl>> registered_legacy_managers = new HashMap();
/*  152 */   private final Map<String, PeerManagerRegistrationImpl> registered_links = new HashMap();
/*      */   
/*      */   private final ByteBuffer legacy_handshake_header;
/*      */   
/*  156 */   private final AEMonitor managers_mon = new AEMonitor("PeerManager:managers");
/*      */   
/*      */   private PeerManager()
/*      */   {
/*  160 */     this.legacy_handshake_header = ByteBuffer.allocate(20);
/*  161 */     this.legacy_handshake_header.put((byte)"BitTorrent protocol".length());
/*  162 */     this.legacy_handshake_header.put("BitTorrent protocol".getBytes());
/*  163 */     this.legacy_handshake_header.flip();
/*      */     
/*  165 */     Set<String> types = new HashSet();
/*      */     
/*  167 */     types.add("peer.manager.count");
/*  168 */     types.add("peer.manager.peer.count");
/*  169 */     types.add("peer.manager.peer.snubbed.count");
/*  170 */     types.add("peer.manager.peer.stalled.disk.count");
/*      */     
/*  172 */     AzureusCoreStats.registerProvider(types, this);
/*      */     
/*  174 */     init();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updateStats(Set types, Map values)
/*      */   {
/*  182 */     if (types.contains("peer.manager.count"))
/*      */     {
/*  184 */       values.put("peer.manager.count", new Long(this.registered_legacy_managers.size()));
/*      */     }
/*      */     
/*  187 */     if ((types.contains("peer.manager.peer.count")) || (types.contains("peer.manager.peer.snubbed.count")) || (types.contains("peer.manager.peer.stalled.disk.count")))
/*      */     {
/*      */ 
/*      */ 
/*  191 */       long total_peers = 0L;
/*  192 */       long total_snubbed_peers = 0L;
/*  193 */       long total_stalled_pending_load = 0L;
/*      */       
/*      */       try
/*      */       {
/*  197 */         this.managers_mon.enter();
/*      */         
/*  199 */         Iterator<List<PeerManagerRegistrationImpl>> it = this.registered_legacy_managers.values().iterator();
/*      */         
/*  201 */         while (it.hasNext())
/*      */         {
/*  203 */           List<PeerManagerRegistrationImpl> registrations = (List)it.next();
/*      */           
/*  205 */           Iterator<PeerManagerRegistrationImpl> it2 = registrations.iterator();
/*      */           
/*  207 */           while (it2.hasNext())
/*      */           {
/*  209 */             PeerManagerRegistrationImpl reg = (PeerManagerRegistrationImpl)it2.next();
/*      */             
/*  211 */             PEPeerControl control = reg.getActiveControl();
/*      */             
/*  213 */             if (control != null)
/*      */             {
/*  215 */               total_peers += control.getNbPeers();
/*  216 */               total_snubbed_peers += control.getNbPeersSnubbed();
/*  217 */               total_stalled_pending_load += control.getNbPeersStalledPendingLoad();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/*  223 */         this.managers_mon.exit();
/*      */       }
/*  225 */       if (types.contains("peer.manager.peer.count"))
/*      */       {
/*  227 */         values.put("peer.manager.peer.count", new Long(total_peers));
/*      */       }
/*  229 */       if (types.contains("peer.manager.peer.snubbed.count"))
/*      */       {
/*  231 */         values.put("peer.manager.peer.snubbed.count", new Long(total_snubbed_peers));
/*      */       }
/*  233 */       if (types.contains("peer.manager.peer.stalled.disk.count"))
/*      */       {
/*  235 */         values.put("peer.manager.peer.stalled.disk.count", new Long(total_stalled_pending_load));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void init()
/*      */   {
/*  243 */     MessageManager.getSingleton().initialize();
/*      */     
/*  245 */     NetworkManager.ByteMatcher matcher = new NetworkManager.ByteMatcher()
/*      */     {
/*      */ 
/*  248 */       public int matchThisSizeOrBigger() { return 48; }
/*  249 */       public int maxSize() { return 48; }
/*  250 */       public int minSize() { return 20; }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public Object matches(TransportHelper transport, ByteBuffer to_compare, int port)
/*      */       {
/*  258 */         InetSocketAddress address = transport.getAddress();
/*      */         
/*  260 */         int old_limit = to_compare.limit();
/*  261 */         int old_position = to_compare.position();
/*      */         
/*  263 */         to_compare.limit(old_position + 20);
/*      */         
/*  265 */         PeerManager.PeerManagerRegistrationImpl routing_data = null;
/*      */         
/*  267 */         if (to_compare.equals(PeerManager.this.legacy_handshake_header)) {
/*  268 */           to_compare.limit(old_position + 48);
/*  269 */           to_compare.position(old_position + 28);
/*      */           
/*  271 */           byte[] hash = new byte[to_compare.remaining()];
/*      */           
/*  273 */           to_compare.get(hash);
/*      */           try
/*      */           {
/*  276 */             PeerManager.this.managers_mon.enter();
/*      */             
/*  278 */             List<PeerManager.PeerManagerRegistrationImpl> registrations = (List)PeerManager.this.registered_legacy_managers.get(new HashWrapper(hash));
/*      */             
/*  280 */             if (registrations != null)
/*      */             {
/*  282 */               routing_data = (PeerManager.PeerManagerRegistrationImpl)registrations.get(0);
/*      */             }
/*      */           }
/*      */           finally {
/*  286 */             PeerManager.this.managers_mon.exit();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  291 */         to_compare.limit(old_limit);
/*  292 */         to_compare.position(old_position);
/*      */         
/*  294 */         if (routing_data != null)
/*      */         {
/*  296 */           if (!routing_data.isActive())
/*      */           {
/*  298 */             if (routing_data.isKnownSeed(address))
/*      */             {
/*  300 */               String reason = "Activation request from " + address + " denied as known seed";
/*      */               
/*  302 */               if (Logger.isEnabled()) {
/*  303 */                 Logger.log(new LogEvent(PeerManager.LOGID, reason));
/*      */               }
/*      */               
/*  306 */               transport.close(reason);
/*      */               
/*  308 */               routing_data = null;
/*      */ 
/*      */ 
/*      */             }
/*  312 */             else if (!routing_data.getAdapter().activateRequest(address))
/*      */             {
/*  314 */               String reason = "Activation request from " + address + " denied by rules";
/*      */               
/*  316 */               if (Logger.isEnabled()) {
/*  317 */                 Logger.log(new LogEvent(PeerManager.LOGID, reason));
/*      */               }
/*      */               
/*  320 */               transport.close(reason);
/*      */               
/*  322 */               routing_data = null;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  328 */         return routing_data;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public Object minMatches(TransportHelper transport, ByteBuffer to_compare, int port)
/*      */       {
/*  337 */         boolean matches = false;
/*      */         
/*  339 */         int old_limit = to_compare.limit();
/*  340 */         int old_position = to_compare.position();
/*      */         
/*  342 */         to_compare.limit(old_position + 20);
/*      */         
/*  344 */         if (to_compare.equals(PeerManager.this.legacy_handshake_header)) {
/*  345 */           matches = true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  350 */         to_compare.limit(old_limit);
/*  351 */         to_compare.position(old_position);
/*      */         
/*  353 */         return matches ? "" : null;
/*      */       }
/*      */       
/*      */ 
/*      */       public byte[][] getSharedSecrets()
/*      */       {
/*  359 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getSpecificPort()
/*      */       {
/*  365 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*  369 */     };
/*  370 */     NetworkManager.getSingleton().requestIncomingConnectionRouting(matcher, new NetworkManager.RoutingListener()
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
/*  387 */       new MessageStreamFactory
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void connectionRouted(NetworkConnection connection, Object routing_data)
/*      */         {
/*      */ 
/*      */ 
/*  379 */           PeerManager.PeerManagerRegistrationImpl registration = (PeerManager.PeerManagerRegistrationImpl)routing_data;
/*      */           
/*  381 */           registration.route(connection, null);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  387 */         public boolean autoCryptoFallback() { return false; } }, new MessageStreamFactory()
/*      */       {
/*      */ 
/*      */         public MessageStreamEncoder createEncoder() {
/*  391 */           return new BTMessageEncoder(); }
/*  392 */         public MessageStreamDecoder createDecoder() { return new BTMessageDecoder(); }
/*      */       });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeerManagerRegistration manualMatchHash(InetSocketAddress address, byte[] hash)
/*      */   {
/*  401 */     PeerManagerRegistrationImpl routing_data = null;
/*      */     
/*      */     try
/*      */     {
/*  405 */       this.managers_mon.enter();
/*      */       
/*  407 */       List<PeerManagerRegistrationImpl> registrations = (List)this.registered_legacy_managers.get(new HashWrapper(hash));
/*      */       
/*  409 */       if (registrations != null)
/*      */       {
/*  411 */         routing_data = (PeerManagerRegistrationImpl)registrations.get(0);
/*      */       }
/*      */     }
/*      */     finally {
/*  415 */       this.managers_mon.exit();
/*      */     }
/*      */     
/*  418 */     if (routing_data != null)
/*      */     {
/*  420 */       if (!routing_data.isActive())
/*      */       {
/*  422 */         if (routing_data.isKnownSeed(address))
/*      */         {
/*  424 */           if (Logger.isEnabled()) {
/*  425 */             Logger.log(new LogEvent(LOGID, "Activation request from " + address + " denied as known seed"));
/*      */           }
/*      */           
/*  428 */           routing_data = null;
/*      */ 
/*      */ 
/*      */         }
/*  432 */         else if (!routing_data.getAdapter().activateRequest(address))
/*      */         {
/*  434 */           if (Logger.isEnabled()) {
/*  435 */             Logger.log(new LogEvent(LOGID, "Activation request from " + address + " denied by rules"));
/*      */           }
/*      */           
/*  438 */           routing_data = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  444 */     return routing_data;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public PeerManagerRegistration manualMatchLink(InetSocketAddress address, String link)
/*      */   {
/*      */     byte[] hash;
/*      */     
/*      */     try
/*      */     {
/*  455 */       this.managers_mon.enter();
/*      */       
/*  457 */       PeerManagerRegistrationImpl registration = (PeerManagerRegistrationImpl)this.registered_links.get(link);
/*      */       
/*  459 */       if (registration == null)
/*      */       {
/*  461 */         return null;
/*      */       }
/*      */       
/*  464 */       hash = registration.getHash();
/*      */     }
/*      */     finally
/*      */     {
/*  468 */       this.managers_mon.exit();
/*      */     }
/*      */     
/*  471 */     return manualMatchHash(address, hash);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void manualRoute(PeerManagerRegistration _registration, NetworkConnection _connection, PeerManagerRoutingListener _listener)
/*      */   {
/*  480 */     PeerManagerRegistrationImpl registration = (PeerManagerRegistrationImpl)_registration;
/*      */     
/*  482 */     registration.route(_connection, _listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public PeerManagerRegistration registerLegacyManager(HashWrapper hash, PeerManagerRegistrationAdapter adapter)
/*      */   {
/*      */     try
/*      */     {
/*  491 */       this.managers_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  496 */       List<PeerManagerRegistrationImpl> registrations = (List)this.registered_legacy_managers.get(hash);
/*      */       
/*  498 */       byte[][] secrets = adapter.getSecrets();
/*      */       
/*  500 */       if (registrations == null)
/*      */       {
/*  502 */         registrations = new ArrayList(1);
/*      */         
/*  504 */         this.registered_legacy_managers.put(hash, registrations);
/*      */         
/*  506 */         IncomingConnectionManager.getSingleton().addSharedSecrets(secrets);
/*      */       }
/*      */       
/*  509 */       PeerManagerRegistrationImpl registration = new PeerManagerRegistrationImpl(hash, adapter);
/*      */       
/*  511 */       registrations.add(registration);
/*      */       
/*  513 */       return registration;
/*      */     }
/*      */     finally
/*      */     {
/*  517 */       this.managers_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class PeerManagerRegistrationImpl
/*      */     implements PeerManagerRegistration
/*      */   {
/*      */     private final HashWrapper hash;
/*      */     
/*      */ 
/*      */     final PeerManagerRegistrationAdapter adapter;
/*      */     
/*      */ 
/*      */     private PEPeerControl download;
/*      */     
/*      */     private volatile PEPeerControl active_control;
/*      */     
/*      */     private List<Object[]> pending_connections;
/*      */     
/*      */     private BloomFilter known_seeds;
/*      */     
/*      */     private Map<String, TOTorrentFile> links;
/*      */     
/*      */ 
/*      */     protected PeerManagerRegistrationImpl(HashWrapper _hash, PeerManagerRegistrationAdapter _adapter)
/*      */     {
/*  545 */       this.hash = _hash;
/*  546 */       this.adapter = _adapter;
/*      */     }
/*      */     
/*      */ 
/*      */     protected PeerManagerRegistrationAdapter getAdapter()
/*      */     {
/*  552 */       return this.adapter;
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte[] getHash()
/*      */     {
/*  558 */       return this.hash.getBytes();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public TOTorrentFile getLink(String target)
/*      */     {
/*  565 */       synchronized (this)
/*      */       {
/*  567 */         if (this.links == null)
/*      */         {
/*  569 */           return null;
/*      */         }
/*      */         
/*  572 */         return (TOTorrentFile)this.links.get(target);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addLink(String link, TOTorrentFile target)
/*      */       throws Exception
/*      */     {
/*      */       try
/*      */       {
/*  584 */         PeerManager.this.managers_mon.enter();
/*      */         
/*  586 */         if (PeerManager.this.registered_links.get(link) != null)
/*      */         {
/*  588 */           throw new Exception("Duplicate link '" + link + "'");
/*      */         }
/*      */         
/*  591 */         PeerManager.this.registered_links.put(link, this);
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*      */ 
/*  597 */         PeerManager.this.managers_mon.exit();
/*      */       }
/*      */       
/*  600 */       synchronized (this)
/*      */       {
/*  602 */         if (this.links == null)
/*      */         {
/*  604 */           this.links = new HashMap();
/*      */         }
/*      */         
/*  607 */         this.links.put(link, target);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public void removeLink(String link)
/*      */     {
/*      */       try
/*      */       {
/*  616 */         PeerManager.this.managers_mon.enter();
/*      */         
/*  618 */         PeerManager.this.registered_links.remove(link);
/*      */       }
/*      */       finally
/*      */       {
/*  622 */         PeerManager.this.managers_mon.exit();
/*      */       }
/*      */       
/*  625 */       synchronized (this)
/*      */       {
/*  627 */         if (this.links != null)
/*      */         {
/*  629 */           this.links.remove(link);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/*  637 */       return this.active_control != null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void activate(PEPeerControl _active_control)
/*      */     {
/*  644 */       List<Object[]> connections = null;
/*      */       try
/*      */       {
/*  647 */         PeerManager.this.managers_mon.enter();
/*      */         
/*  649 */         this.active_control = _active_control;
/*      */         
/*  651 */         if (this.download != null)
/*      */         {
/*  653 */           Debug.out("Already activated");
/*      */         }
/*      */         
/*  656 */         this.download = _active_control;
/*      */         
/*  658 */         connections = this.pending_connections;
/*      */         
/*  660 */         this.pending_connections = null;
/*      */       }
/*      */       finally
/*      */       {
/*  664 */         PeerManager.this.managers_mon.exit();
/*      */       }
/*      */       
/*  667 */       if (connections != null)
/*      */       {
/*  669 */         for (int i = 0; i < connections.size(); i++)
/*      */         {
/*  671 */           Object[] entry = (Object[])connections.get(i);
/*      */           
/*  673 */           NetworkConnection nc = (NetworkConnection)entry[0];
/*      */           
/*  675 */           PeerManagerRoutingListener listener = (PeerManagerRoutingListener)entry[2];
/*      */           
/*  677 */           route(_active_control, nc, true, listener);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public void deactivate()
/*      */     {
/*      */       try
/*      */       {
/*  686 */         PeerManager.this.managers_mon.enter();
/*      */         
/*  688 */         if (this.download == null)
/*      */         {
/*  690 */           Debug.out("Already deactivated");
/*      */         }
/*      */         else
/*      */         {
/*  694 */           this.download = null;
/*      */         }
/*      */         
/*  697 */         this.active_control = null;
/*      */         
/*  699 */         if (this.pending_connections != null)
/*      */         {
/*  701 */           for (int i = 0; i < this.pending_connections.size(); i++)
/*      */           {
/*  703 */             Object[] entry = (Object[])this.pending_connections.get(i);
/*      */             
/*  705 */             NetworkConnection connection = (NetworkConnection)entry[0];
/*      */             
/*  707 */             if (Logger.isEnabled()) {
/*  708 */               Logger.log(new LogEvent(PeerManager.LOGID, 1, "Incoming connection from [" + connection + "] closed due to deactivation"));
/*      */             }
/*      */             
/*      */ 
/*  712 */             connection.close("deactivated");
/*      */           }
/*      */           
/*  715 */           this.pending_connections = null;
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/*  720 */         PeerManager.this.managers_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */     public void unregister()
/*      */     {
/*      */       try
/*      */       {
/*  728 */         PeerManager.this.managers_mon.enter();
/*      */         
/*  730 */         if (this.active_control != null)
/*      */         {
/*  732 */           Debug.out("Not deactivated");
/*      */           
/*  734 */           deactivate();
/*      */         }
/*      */         
/*  737 */         List<PeerManagerRegistrationImpl> registrations = (List)PeerManager.this.registered_legacy_managers.get(this.hash);
/*      */         
/*  739 */         if (registrations == null)
/*      */         {
/*  741 */           Debug.out("manager already deregistered");
/*      */ 
/*      */ 
/*      */         }
/*  745 */         else if (registrations.remove(this))
/*      */         {
/*  747 */           if (registrations.size() == 0)
/*      */           {
/*  749 */             IncomingConnectionManager.getSingleton().removeSharedSecrets(this.adapter.getSecrets());
/*      */             
/*  751 */             PeerManager.this.registered_legacy_managers.remove(this.hash);
/*      */           }
/*      */         }
/*      */         else {
/*  755 */           Debug.out("manager already deregistered");
/*      */         }
/*      */         
/*      */ 
/*  759 */         synchronized (this)
/*      */         {
/*  761 */           if (this.links != null)
/*      */           {
/*  763 */             Iterator<String> it = this.links.keySet().iterator();
/*      */             
/*  765 */             while (it.hasNext())
/*      */             {
/*  767 */               PeerManager.this.registered_links.remove(it.next());
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/*  773 */         PeerManager.this.managers_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isKnownSeed(InetSocketAddress address)
/*      */     {
/*      */       try
/*      */       {
/*  782 */         PeerManager.this.managers_mon.enter();
/*      */         boolean bool;
/*  784 */         if (this.known_seeds == null)
/*      */         {
/*  786 */           return false;
/*      */         }
/*      */         
/*  789 */         return this.known_seeds.contains(AddressUtils.getAddressBytes(address));
/*      */       }
/*      */       finally
/*      */       {
/*  793 */         PeerManager.this.managers_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected void setKnownSeed(InetSocketAddress address)
/*      */     {
/*      */       try
/*      */       {
/*  802 */         PeerManager.this.managers_mon.enter();
/*      */         
/*  804 */         if (this.known_seeds == null)
/*      */         {
/*  806 */           this.known_seeds = BloomFilterFactory.createAddOnly(1024);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  812 */         this.known_seeds.add(AddressUtils.getAddressBytes(address));
/*      */       }
/*      */       finally
/*      */       {
/*  816 */         PeerManager.this.managers_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected PEPeerControl getActiveControl()
/*      */     {
/*  823 */       return this.active_control;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void route(NetworkConnection connection, PeerManagerRoutingListener listener)
/*      */     {
/*  831 */       if (this.adapter.manualRoute(connection))
/*      */       {
/*  833 */         return;
/*      */       }
/*      */       
/*  836 */       if (!this.adapter.isPeerSourceEnabled("Incoming"))
/*      */       {
/*  838 */         if (Logger.isEnabled()) {
/*  839 */           Logger.log(new LogEvent(PeerManager.LOGID, 1, "Incoming connection from [" + connection + "] to " + this.adapter.getDescription() + " dropped as peer source disabled"));
/*      */         }
/*      */         
/*      */ 
/*  843 */         connection.close("peer source disabled");
/*      */         
/*  845 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  850 */       boolean register_for_timeouts = false;
/*      */       PEPeerControl control;
/*      */       try {
/*  853 */         PeerManager.this.managers_mon.enter();
/*      */         
/*  855 */         control = this.active_control;
/*      */         
/*  857 */         if (control == null)
/*      */         {
/*      */ 
/*      */ 
/*  861 */           if ((this.pending_connections != null) && (this.pending_connections.size() > 10))
/*      */           {
/*  863 */             if (Logger.isEnabled()) {
/*  864 */               Logger.log(new LogEvent(PeerManager.LOGID, 1, "Incoming connection from [" + connection + "] to " + this.adapter.getDescription() + " dropped too many pending activations"));
/*      */             }
/*      */             
/*      */ 
/*  868 */             connection.close("too many pending activations"); return;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  874 */           if (this.pending_connections == null)
/*      */           {
/*  876 */             this.pending_connections = new ArrayList();
/*      */           }
/*      */           
/*  879 */           this.pending_connections.add(new Object[] { connection, new Long(SystemTime.getCurrentTime()), listener });
/*      */           
/*  881 */           if (this.pending_connections.size() == 1)
/*      */           {
/*  883 */             register_for_timeouts = true;
/*      */           }
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/*  889 */         PeerManager.this.managers_mon.exit();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  895 */       if (register_for_timeouts)
/*      */       {
/*  897 */         PeerManager.registerForTimeouts(this);
/*      */       }
/*      */       
/*  900 */       if (control != null)
/*      */       {
/*  902 */         route(control, connection, false, listener);
/*      */       }
/*      */     }
/*      */     
/*      */     protected boolean timeoutCheck()
/*      */     {
/*      */       try
/*      */       {
/*  910 */         PeerManager.this.managers_mon.enter();
/*      */         
/*  912 */         if (this.pending_connections == null)
/*      */         {
/*  914 */           return false;
/*      */         }
/*      */         
/*  917 */         Object it = this.pending_connections.iterator();
/*      */         
/*  919 */         long now = SystemTime.getCurrentTime();
/*      */         Object[] entry;
/*  921 */         while (((Iterator)it).hasNext())
/*      */         {
/*  923 */           entry = (Object[])((Iterator)it).next();
/*      */           
/*  925 */           long start_time = ((Long)entry[1]).longValue();
/*      */           
/*  927 */           if (now < start_time)
/*      */           {
/*  929 */             entry[1] = new Long(now);
/*      */           }
/*  931 */           else if (now - start_time > 10000L)
/*      */           {
/*  933 */             ((Iterator)it).remove();
/*      */             
/*  935 */             NetworkConnection connection = (NetworkConnection)entry[0];
/*      */             
/*  937 */             if (Logger.isEnabled()) {
/*  938 */               Logger.log(new LogEvent(PeerManager.LOGID, 1, "Incoming connection from [" + connection + "] to " + this.adapter.getDescription() + " closed due to activation timeout"));
/*      */             }
/*      */             
/*      */ 
/*  942 */             connection.close("activation timeout");
/*      */           }
/*      */         }
/*      */         
/*  946 */         if (this.pending_connections.size() == 0)
/*      */         {
/*  948 */           this.pending_connections = null;
/*      */         }
/*      */         
/*  951 */         return this.pending_connections != null ? 1 : 0;
/*      */       }
/*      */       finally
/*      */       {
/*  955 */         PeerManager.this.managers_mon.exit();
/*      */       }
/*      */     }
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
/*      */     protected void route(PEPeerControl control, final NetworkConnection connection, boolean is_activation, PeerManagerRoutingListener listener)
/*      */     {
/*  970 */       Object callback = connection.getUserData("RoutedCallback");
/*      */       
/*  972 */       if ((callback instanceof AEGenericCallback)) {
/*      */         try
/*      */         {
/*  975 */           ((AEGenericCallback)callback).invoke(control);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  979 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/*  983 */       ConnectionEndpoint ep = connection.getEndpoint();
/*      */       
/*      */ 
/*  986 */       InetSocketAddress is_address = ep.getNotionalAddress();
/*      */       
/*  988 */       String host_address = AddressUtils.getHostAddress(is_address);
/*      */       
/*  990 */       String net_cat = AENetworkClassifier.categoriseAddress(host_address);
/*      */       
/*  992 */       if (!control.isNetworkEnabled(net_cat))
/*      */       {
/*  994 */         connection.close("Network '" + net_cat + "' is not enabled");
/*      */         
/*  996 */         return;
/*      */       }
/*      */       
/*  999 */       InetAddress address_mbn = is_address.getAddress();
/*      */       
/* 1001 */       boolean same_allowed = (COConfigurationManager.getBooleanParameter("Allow Same IP Peers")) || ((address_mbn != null) && (address_mbn.isLoopbackAddress()));
/*      */       
/* 1003 */       if ((!same_allowed) && (PeerIdentityManager.containsIPAddress(control.getPeerIdentityDataID(), host_address)))
/*      */       {
/* 1005 */         if (Logger.isEnabled())
/*      */         {
/* 1007 */           Logger.log(new LogEvent(PeerManager.LOGID, 1, "Incoming connection from [" + connection + "] dropped as IP address already " + "connected for [" + control.getDisplayName() + "]"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1014 */         connection.close("already connected to peer");
/*      */         
/* 1016 */         return;
/*      */       }
/*      */       
/* 1019 */       if (AERunStateHandler.isUDPNetworkOnly())
/*      */       {
/* 1021 */         if (connection.getTransport().getTransportEndpoint().getProtocolEndpoint().getType() == 1)
/*      */         {
/* 1023 */           if (!connection.isLANLocal())
/*      */           {
/* 1025 */             connection.close("limited network mode: tcp disabled");
/*      */             
/* 1027 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1032 */       if (Logger.isEnabled())
/*      */       {
/* 1034 */         Logger.log(new LogEvent(PeerManager.LOGID, "Incoming connection from [" + connection + "] routed to legacy download [" + control.getDisplayName() + "]"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1039 */       PEPeerTransport pt = PEPeerTransportFactory.createTransport(control, "Incoming", connection, null);
/*      */       
/* 1041 */       if (listener != null)
/*      */       {
/* 1043 */         boolean ok = false;
/*      */         try
/*      */         {
/* 1046 */           if (listener.routed(pt))
/*      */           {
/* 1048 */             ok = true;
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1053 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/* 1056 */         if (!ok)
/*      */         {
/* 1058 */           connection.close("routing denied");
/*      */           
/* 1060 */           return;
/*      */         }
/*      */       }
/*      */       
/* 1064 */       pt.start();
/*      */       
/* 1066 */       if (is_activation)
/*      */       {
/* 1068 */         pt.addListener(new PEPeerListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void stateChanged(PEPeer peer, int new_state)
/*      */           {
/*      */ 
/*      */ 
/* 1076 */             if (new_state == 40)
/*      */             {
/* 1078 */               if (peer.isSeed())
/*      */               {
/* 1080 */                 InetSocketAddress address = connection.getEndpoint().getNotionalAddress();
/*      */                 
/* 1082 */                 PeerManager.PeerManagerRegistrationImpl.this.setKnownSeed(address);
/*      */                 
/*      */ 
/*      */ 
/* 1086 */                 PeerManager.PeerManagerRegistrationImpl.this.adapter.deactivateRequest(address);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */           public void sentBadChunk(PEPeer peer, int piece_num, int total_bad_chunks) {}
/*      */           
/*      */           public void addAvailability(PEPeer peer, BitFlags peerHavePieces) {}
/*      */           
/*      */           public void removeAvailability(PEPeer peer, BitFlags peerHavePieces) {}
/*      */         });
/*      */       }
/* 1099 */       control.addPeerTransport(pt);
/*      */     }
/*      */     
/*      */ 
/*      */     public String getDescription()
/*      */     {
/* 1105 */       PEPeerControl control = this.active_control;
/*      */       
/* 1107 */       return ByteFormatter.encodeString(this.hash.getBytes()) + ", control=" + (control == null ? null : control.getDisplayName()) + ": " + this.adapter.getDescription();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/PeerManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */