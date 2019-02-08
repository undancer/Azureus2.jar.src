/*     */ package com.aelitis.azureus.core.lws;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.peermanager.PeerManager;
/*     */ import com.aelitis.azureus.core.peermanager.PeerManagerRegistration;
/*     */ import com.aelitis.azureus.core.peermanager.PeerManagerRegistrationAdapter;
/*     */ import java.io.File;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.LogRelation;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerFactory;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerListenerAdapter;
/*     */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerDataProvider;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerException;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerFactory;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerListener;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponsePeer;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LightWeightSeed
/*     */   extends LogRelation
/*     */   implements PeerManagerRegistrationAdapter
/*     */ {
/*     */   private static final byte ACT_NONE = 0;
/*     */   private static final byte ACT_HAS_PEERS = 2;
/*     */   private static final byte ACT_HAS_POTENTIAL_PEERS = 3;
/*     */   private static final byte ACT_INCOMING = 4;
/*     */   private static final byte ACT_NO_PM = 5;
/*     */   private static final byte ACT_TIMING_OUT = 6;
/*     */   private static final byte ACT_TRACKER_ANNOUNCE = 7;
/*     */   private static final byte ACT_TRACKER_SCRAPE = 8;
/*     */   private static final int DEACTIVATION_TIMEOUT = 300000;
/*     */   private static final int DEACTIVATION_WITH_POTENTIAL_TIMEOUT = 900000;
/*     */   private final LightWeightSeedManager manager;
/*     */   private final LightWeightSeedAdapter adapter;
/*     */   private final String name;
/*     */   private final HashWrapper hash;
/*     */   private final URL announce_url;
/*     */   private final File data_location;
/*     */   private final String network;
/*     */   private PeerManagerRegistration peer_manager_registration;
/*     */   private volatile PEPeerManager peer_manager;
/*     */   private volatile LWSDiskManager disk_manager;
/*     */   private LWSDownload pseudo_download;
/*     */   private volatile LWSTorrent torrent_facade;
/*     */   private TRTrackerAnnouncer announcer;
/*     */   private TOTorrent actual_torrent;
/*     */   private boolean is_running;
/*     */   private long last_activity_time;
/*  99 */   private int activation_state = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected LightWeightSeed(LightWeightSeedManager _manager, String _name, HashWrapper _hash, URL _announce_url, File _data_location, String _network, LightWeightSeedAdapter _adapter)
/*     */   {
/* 111 */     this.manager = _manager;
/* 112 */     this.name = _name;
/* 113 */     this.hash = _hash;
/* 114 */     this.announce_url = _announce_url;
/* 115 */     this.data_location = _data_location;
/* 116 */     this.network = _network;
/* 117 */     this.adapter = _adapter;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getName()
/*     */   {
/* 123 */     return this.name + "/" + ByteFormatter.encodeString(this.hash.getBytes());
/*     */   }
/*     */   
/*     */ 
/*     */   protected Torrent getTorrent()
/*     */   {
/* 129 */     return new TorrentImpl(getTOTorrent(false));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected TOTorrent getTOTorrent(boolean actual)
/*     */   {
/* 136 */     if (actual)
/*     */     {
/* 138 */       synchronized (this)
/*     */       {
/* 140 */         if (this.actual_torrent == null)
/*     */         {
/*     */           try
/*     */           {
/* 144 */             this.actual_torrent = this.adapter.getTorrent(this.hash.getBytes(), this.announce_url, this.data_location);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 148 */             log("Failed to get torrent", e);
/*     */           }
/*     */           
/* 151 */           if (this.actual_torrent == null)
/*     */           {
/* 153 */             throw new RuntimeException("Torrent not available");
/*     */           }
/*     */         }
/*     */         
/* 157 */         return this.actual_torrent;
/*     */       }
/*     */     }
/*     */     
/* 161 */     return this.torrent_facade;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public HashWrapper getHash()
/*     */   {
/* 168 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getAnnounceURL()
/*     */   {
/* 174 */     return this.announce_url;
/*     */   }
/*     */   
/*     */ 
/*     */   public File getDataLocation()
/*     */   {
/* 180 */     return this.data_location;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getNetwork()
/*     */   {
/* 186 */     return this.network;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getSize()
/*     */   {
/* 192 */     return this.data_location.length();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isPeerSourceEnabled(String peer_source)
/*     */   {
/* 199 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean manualRoute(NetworkConnection connection)
/*     */   {
/* 206 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[][] getSecrets()
/*     */   {
/* 212 */     return new byte[][] { this.hash.getBytes() };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean activateRequest(InetSocketAddress remote_address)
/*     */   {
/* 219 */     ensureActive("Incoming[" + AddressUtils.getHostAddress(remote_address) + "]", (byte)4);
/*     */     
/* 221 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deactivateRequest(InetSocketAddress remote_address) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 234 */     return "LWS: " + getName();
/*     */   }
/*     */   
/*     */ 
/*     */   protected synchronized void start()
/*     */   {
/* 240 */     log("Start");
/*     */     
/* 242 */     if (this.is_running)
/*     */     {
/* 244 */       log("Start of '" + getString() + "' failed - already running");
/*     */       
/* 246 */       return;
/*     */     }
/*     */     
/* 249 */     if (this.peer_manager_registration != null)
/*     */     {
/* 251 */       log("Start of '" + getString() + "' failed - router already registered");
/*     */       
/* 253 */       return;
/*     */     }
/*     */     
/* 256 */     if (this.pseudo_download != null)
/*     */     {
/* 258 */       log("Start of '" + getString() + "' failed - pseudo download already registered");
/*     */       
/* 260 */       return;
/*     */     }
/*     */     
/* 263 */     if (this.disk_manager != null)
/*     */     {
/* 265 */       log("Start of '" + getString() + "' failed - disk manager already started");
/*     */       
/* 267 */       return;
/*     */     }
/*     */     
/* 270 */     if (this.peer_manager != null)
/*     */     {
/* 272 */       log("Start of '" + getString() + "' failed - peer manager already started");
/*     */       
/* 274 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 278 */       if (this.torrent_facade == null)
/*     */       {
/* 280 */         this.torrent_facade = new LWSTorrent(this);
/*     */       }
/*     */       
/* 283 */       this.peer_manager_registration = PeerManager.getSingleton().registerLegacyManager(this.hash, this);
/*     */       
/* 285 */       this.announcer = createAnnouncer();
/*     */       
/* 287 */       this.pseudo_download = new LWSDownload(this, this.announcer);
/*     */       
/* 289 */       this.manager.addToDHTTracker(this.pseudo_download);
/*     */       
/* 291 */       this.is_running = true;
/*     */       
/* 293 */       this.last_activity_time = SystemTime.getMonotonousTime();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 297 */       log("Start of '" + getString() + "' failed", e);
/*     */     }
/*     */     finally
/*     */     {
/* 301 */       if (this.is_running)
/*     */       {
/* 303 */         log("Started " + getString());
/*     */       }
/*     */       else
/*     */       {
/* 307 */         stop();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected synchronized void stop()
/*     */   {
/* 315 */     log("Stop");
/*     */     try
/*     */     {
/* 318 */       if (this.disk_manager != null)
/*     */       {
/* 320 */         this.disk_manager.stop(false);
/*     */         
/* 322 */         this.disk_manager = null;
/*     */       }
/*     */       
/* 325 */       if (this.peer_manager != null)
/*     */       {
/* 327 */         this.peer_manager.stopAll();
/*     */         
/* 329 */         this.peer_manager = null;
/*     */       }
/*     */       
/* 332 */       if (this.pseudo_download != null)
/*     */       {
/* 334 */         this.manager.removeFromDHTTracker(this.pseudo_download);
/*     */         
/* 336 */         this.pseudo_download = null;
/*     */       }
/*     */       
/* 339 */       if (this.announcer != null)
/*     */       {
/* 341 */         this.announcer.stop(false);
/*     */         
/* 343 */         this.announcer.destroy();
/*     */         
/* 345 */         this.announcer = null;
/*     */       }
/*     */       
/* 348 */       if (this.peer_manager_registration != null)
/*     */       {
/* 350 */         this.peer_manager_registration.unregister();
/*     */         
/* 352 */         this.peer_manager_registration = null;
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 357 */       this.is_running = false;
/*     */       
/* 359 */       this.activation_state = 0;
/*     */       
/* 361 */       log("Stopped " + getString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected synchronized void activate(String reason_str, byte activation_reason)
/*     */   {
/* 370 */     log("Activate: " + activation_reason + "/" + reason_str);
/*     */     
/* 372 */     if (this.activation_state != 0)
/*     */     {
/* 374 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 378 */       this.disk_manager = new LWSDiskManager(this, this.data_location);
/*     */       
/* 380 */       this.disk_manager.start();
/*     */       
/* 382 */       if (this.disk_manager.getState() != 4)
/*     */       {
/* 384 */         log("Start of '" + getString() + "' failed, disk manager failed = " + this.disk_manager.getErrorMessage());
/*     */       }
/*     */       else
/*     */       {
/* 388 */         this.peer_manager = PEPeerManagerFactory.create(this.announcer.getPeerId(), new LWSPeerManagerAdapter(this, this.peer_manager_registration), this.disk_manager);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 396 */         this.peer_manager.addListener(new PEPeerManagerListenerAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void peerAdded(PEPeerManager manager, PEPeer peer)
/*     */           {
/*     */ 
/*     */ 
/* 404 */             LightWeightSeed.this.last_activity_time = SystemTime.getMonotonousTime();
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void peerRemoved(PEPeerManager manager, PEPeer peer)
/*     */           {
/* 413 */             LightWeightSeed.this.last_activity_time = SystemTime.getMonotonousTime();
/*     */           }
/*     */           
/* 416 */         });
/* 417 */         this.peer_manager.start();
/*     */         
/* 419 */         this.announcer.update(true);
/*     */         
/* 421 */         this.activation_state = activation_reason;
/*     */         
/* 423 */         this.last_activity_time = SystemTime.getMonotonousTime();
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 428 */       log("Activation of '" + getString() + "' failed", e);
/*     */     }
/*     */     finally
/*     */     {
/* 432 */       if (this.activation_state == 0)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 437 */         deactivate();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected synchronized void deactivate()
/*     */   {
/* 445 */     log("Deactivate");
/*     */     try
/*     */     {
/* 448 */       if (this.disk_manager != null)
/*     */       {
/* 450 */         this.disk_manager.stop(false);
/*     */         
/* 452 */         this.disk_manager = null;
/*     */       }
/*     */       
/* 455 */       if (this.peer_manager != null)
/*     */       {
/* 457 */         this.peer_manager.stopAll();
/*     */         
/* 459 */         this.peer_manager = null;
/*     */       }
/*     */     }
/*     */     finally {
/* 463 */       this.activation_state = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected synchronized TRTrackerAnnouncer createAnnouncer()
/*     */     throws TRTrackerAnnouncerException
/*     */   {
/* 475 */     TRTrackerAnnouncer result = TRTrackerAnnouncerFactory.create(this.torrent_facade, true);
/*     */     
/* 477 */     result.addListener(new TRTrackerAnnouncerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void receivedTrackerResponse(TRTrackerAnnouncerResponse response)
/*     */       {
/*     */ 
/* 484 */         TRTrackerAnnouncerResponsePeer[] peers = response.getPeers();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 489 */         if ((peers != null) && (peers.length > 0))
/*     */         {
/* 491 */           LightWeightSeed.this.ensureActive("Tracker[" + peers[0].getAddress() + "]", (byte)7);
/*     */         }
/* 493 */         else if (response.getScrapeIncompleteCount() > 0)
/*     */         {
/* 495 */           LightWeightSeed.this.ensureActive("Tracker[scrape]", (byte)8);
/*     */         }
/*     */         
/* 498 */         PEPeerManager pm = LightWeightSeed.this.peer_manager;
/*     */         
/* 500 */         if (pm != null)
/*     */         {
/* 502 */           pm.processTrackerResponse(response);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void urlChanged(TRTrackerAnnouncer announcer, URL old_url, URL new_url, boolean explicit) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void urlRefresh() {}
/* 520 */     });
/* 521 */     result.setAnnounceDataProvider(new TRTrackerAnnouncerDataProvider()
/*     */     {
/*     */ 
/*     */       public String getName()
/*     */       {
/*     */ 
/* 527 */         return LightWeightSeed.this.getName();
/*     */       }
/*     */       
/*     */ 
/*     */       public long getTotalSent()
/*     */       {
/* 533 */         return 0L;
/*     */       }
/*     */       
/*     */ 
/*     */       public long getTotalReceived()
/*     */       {
/* 539 */         return 0L;
/*     */       }
/*     */       
/*     */ 
/*     */       public long getFailedHashCheck()
/*     */       {
/* 545 */         return 0L;
/*     */       }
/*     */       
/*     */ 
/*     */       public long getRemaining()
/*     */       {
/* 551 */         return 0L;
/*     */       }
/*     */       
/*     */ 
/*     */       public String getExtensions()
/*     */       {
/* 557 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public int getMaxNewConnectionsAllowed(String network)
/*     */       {
/* 564 */         PEPeerManager pm = LightWeightSeed.this.peer_manager;
/*     */         
/* 566 */         if (pm == null)
/*     */         {
/*     */ 
/*     */ 
/* 570 */           return 8;
/*     */         }
/*     */         
/* 573 */         return PeerUtils.numNewConnectionsAllowed(pm.getPeerIdentityDataID(), 0);
/*     */       }
/*     */       
/*     */ 
/*     */       public int getPendingConnectionCount()
/*     */       {
/* 579 */         PEPeerManager pm = LightWeightSeed.this.peer_manager;
/*     */         
/* 581 */         if (pm == null)
/*     */         {
/* 583 */           return 0;
/*     */         }
/*     */         
/* 586 */         return pm.getPendingPeerCount();
/*     */       }
/*     */       
/*     */ 
/*     */       public int getConnectedConnectionCount()
/*     */       {
/* 592 */         PEPeerManager pm = LightWeightSeed.this.peer_manager;
/*     */         
/* 594 */         if (pm == null)
/*     */         {
/* 596 */           return 0;
/*     */         }
/*     */         
/* 599 */         return pm.getNbPeers() + pm.getNbSeeds();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public int getUploadSpeedKBSec(boolean estimate)
/*     */       {
/* 606 */         return 0;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getCryptoLevel()
/*     */       {
/* 612 */         return 0;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean isPeerSourceEnabled(String peer_source)
/*     */       {
/* 619 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void setPeerSources(String[] sources) {}
/* 628 */     });
/* 629 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected synchronized void ensureActive(String reason, byte a_reason)
/*     */   {
/* 637 */     if ((this.is_running) && (this.activation_state == 0))
/*     */     {
/* 639 */       activate(reason, a_reason);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected synchronized void checkDeactivation()
/*     */   {
/* 646 */     if (this.activation_state == 0)
/*     */     {
/* 648 */       return;
/*     */     }
/*     */     
/* 651 */     if (this.peer_manager == null)
/*     */     {
/* 653 */       this.activation_state = 5;
/*     */       
/* 655 */       return;
/*     */     }
/*     */     
/*     */ 
/* 659 */     if (this.peer_manager.getNbPeers() > 0)
/*     */     {
/* 661 */       this.activation_state = 2;
/*     */       
/* 663 */       return;
/*     */     }
/*     */     
/* 666 */     long now = SystemTime.getMonotonousTime();
/*     */     
/* 668 */     long millis_since_last_act = now - this.last_activity_time;
/*     */     
/* 670 */     if (this.peer_manager.hasPotentialConnections())
/*     */     {
/* 672 */       if (millis_since_last_act < 900000L)
/*     */       {
/* 674 */         this.activation_state = 3;
/*     */         
/* 676 */         return;
/*     */       }
/*     */     }
/*     */     
/* 680 */     if (millis_since_last_act >= 300000L)
/*     */     {
/* 682 */       deactivate();
/*     */     }
/*     */     else
/*     */     {
/* 686 */       this.activation_state = 6;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void enqueueReadRequest(PEPeer peer, DiskManagerReadRequest request, DiskManagerReadRequestListener listener)
/*     */   {
/* 696 */     LWSDiskManager dm = this.disk_manager;
/*     */     
/* 698 */     if (dm == null)
/*     */     {
/* 700 */       listener.readFailed(request, new Throwable("download is stopped"));
/*     */     }
/*     */     else
/*     */     {
/* 704 */       dm.enqueueReadRequest(request, listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void remove()
/*     */   {
/* 711 */     this.manager.remove(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRelationText()
/*     */   {
/* 717 */     return "LWS: '" + getName() + "'";
/*     */   }
/*     */   
/*     */ 
/*     */   public Object[] getQueryableInterfaces()
/*     */   {
/* 723 */     return new Object[0];
/*     */   }
/*     */   
/*     */ 
/*     */   public LogRelation getRelation()
/*     */   {
/* 729 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 735 */     return getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 742 */     Logger.log(new LogEvent(this, LogIDs.CORE, str));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(String str, Throwable e)
/*     */   {
/* 750 */     Logger.log(new LogEvent(this, LogIDs.CORE, str, e));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/lws/LightWeightSeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */