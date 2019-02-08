/*      */ package org.gudy.azureus2.core3.download.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.RateHandler;
/*      */ import com.aelitis.azureus.core.peermanager.PeerManager;
/*      */ import com.aelitis.azureus.core.peermanager.PeerManagerRegistration;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPeer;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin;
/*      */ import java.io.File;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFactory;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerDiskListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.ForceRecheckListener;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.LogRelation;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManagerStats;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*      */ import org.gudy.azureus2.core3.peer.PEPiece;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.ListenerManager;
/*      */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ 
/*      */ public class DownloadManagerController extends LogRelation implements org.gudy.azureus2.core3.peer.PEPeerManagerAdapter, com.aelitis.azureus.core.peermanager.PeerManagerRegistrationAdapter, org.gudy.azureus2.core3.util.SimpleTimer.TimerTickReceiver
/*      */ {
/*      */   private static final long STATE_FLAG_HASDND = 1L;
/*      */   private static final long STATE_FLAG_COMPLETE_NO_DND = 2L;
/*      */   private static long skeleton_builds;
/*      */   private static boolean tracker_stats_exclude_lan;
/*      */   private static ExternalSeedPlugin ext_seed_plugin;
/*      */   private static boolean ext_seed_plugin_tried;
/*      */   private static final int LDT_DL_ADDED = 1;
/*      */   private static final int LDT_DL_REMOVED = 2;
/*      */   
/*      */   static
/*      */   {
/*   79 */     COConfigurationManager.addAndFireParameterListener("Tracker Client Exclude LAN", new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*   87 */         DownloadManagerController.access$002(COConfigurationManager.getBooleanParameter(name));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ExternalSeedPlugin getExternalSeedPlugin()
/*      */   {
/*   98 */     if (!ext_seed_plugin_tried)
/*      */     {
/*  100 */       ext_seed_plugin_tried = true;
/*      */       try
/*      */       {
/*  103 */         PluginInterface ext_pi = com.aelitis.azureus.core.AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass(ExternalSeedPlugin.class);
/*  104 */         if (ext_pi != null) {
/*  105 */           ext_seed_plugin = (ExternalSeedPlugin)ext_pi.getPlugin();
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  110 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  114 */     return ext_seed_plugin;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  122 */   static final ListenerManager disk_listeners_agregator = ListenerManager.createAsyncManager("DMC:DiskListenAgregatorDispatcher", new ListenerManagerDispatcher()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatch(Object _listener, int type, Object value)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  132 */       DownloadManagerDiskListener listener = (DownloadManagerDiskListener)_listener;
/*      */       
/*  134 */       if (type == 1)
/*      */       {
/*  136 */         listener.diskManagerAdded((DiskManager)value);
/*      */       }
/*  138 */       else if (type == 2)
/*      */       {
/*  140 */         listener.diskManagerRemoved((DiskManager)value);
/*      */       }
/*      */     }
/*  122 */   });
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
/*  145 */   private final ListenerManager disk_listeners = ListenerManager.createManager("DMC:DiskListenDispatcher", new ListenerManagerDispatcher()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatch(Object listener, int type, Object value)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  155 */       DownloadManagerController.disk_listeners_agregator.dispatch(listener, type, value);
/*      */     }
/*  145 */   });
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
/*  159 */   private final AEMonitor disk_listeners_mon = new AEMonitor("DownloadManagerController:DL");
/*      */   
/*  161 */   final AEMonitor control_mon = new AEMonitor("DownloadManagerController");
/*  162 */   private final AEMonitor state_mon = new AEMonitor("DownloadManagerController:State");
/*  163 */   final AEMonitor facade_mon = new AEMonitor("DownloadManagerController:Facade");
/*      */   
/*      */ 
/*      */   final DownloadManagerImpl download_manager;
/*      */   
/*      */ 
/*      */   final DownloadManagerStatsImpl stats;
/*      */   
/*      */ 
/*  172 */   private volatile int state_set_by_method = -1;
/*      */   
/*      */   private volatile int substate;
/*      */   
/*      */   private volatile boolean force_start;
/*      */   
/*      */   private volatile DiskManager disk_manager_use_accessors;
/*      */   
/*      */   private DiskManagerListener disk_manager_listener_use_accessors;
/*      */   
/*  182 */   final FileInfoFacadeSet fileFacadeSet = new FileInfoFacadeSet();
/*      */   
/*      */   private boolean files_facade_destroyed;
/*      */   
/*      */   private boolean cached_complete_excluding_dnd;
/*      */   private boolean cached_has_dnd_files;
/*      */   private boolean cached_values_set;
/*      */   private Set<String> cached_networks;
/*  190 */   final Object cached_networks_lock = new Object();
/*      */   
/*      */   private PeerManagerRegistration peer_manager_registration;
/*      */   
/*      */   private PEPeerManager peer_manager;
/*      */   
/*      */   private List<Object[]> external_rate_limiters_cow;
/*      */   private String errorDetail;
/*  198 */   private int errorType = 0;
/*      */   
/*      */   final GlobalManagerStats global_stats;
/*      */   
/*  202 */   private boolean bInitialized = false;
/*      */   
/*      */   private long data_send_rate_at_close;
/*      */   
/*      */   private static final int ACTIVATION_REBUILD_TIME = 600000;
/*      */   private static final int BLOOM_SIZE = 64;
/*      */   private volatile BloomFilter activation_bloom;
/*  209 */   private volatile long activation_bloom_create_time = SystemTime.getCurrentTime();
/*      */   
/*      */   private volatile int activation_count;
/*      */   private volatile long activation_count_time;
/*  213 */   private boolean piece_checking_enabled = true;
/*      */   
/*      */   private long priority_connection_count;
/*      */   
/*      */   private static final int HTTP_SEEDS_MAX = 64;
/*  218 */   private final LinkedList<ExternalSeedPeer> http_seeds = new LinkedList();
/*      */   
/*      */   private int md_info_dict_size;
/*  221 */   private volatile WeakReference<byte[]> md_info_dict_ref = new WeakReference(null);
/*      */   
/*      */   private static final int MD_INFO_PEER_HISTORY_MAX = 128;
/*      */   
/*  225 */   private final Map<String, int[]> md_info_peer_history = new LinkedHashMap(128, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(java.util.Map.Entry<String, int[]> eldest)
/*      */     {
/*      */ 
/*  232 */       return size() > 128;
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DownloadManagerController(DownloadManagerImpl _download_manager)
/*      */   {
/*  242 */     this.download_manager = _download_manager;
/*      */     
/*  244 */     GlobalManager gm = this.download_manager.getGlobalManager();
/*      */     
/*  246 */     this.global_stats = gm.getStats();
/*      */     
/*  248 */     this.stats = ((DownloadManagerStatsImpl)this.download_manager.getStats());
/*      */     
/*  250 */     this.cached_values_set = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setInitialState(int initial_state)
/*      */   {
/*  259 */     this.bInitialized = true;
/*      */     
/*  261 */     if (getState() == -1)
/*      */     {
/*  263 */       setState(initial_state, true);
/*      */     }
/*      */     
/*  266 */     DownloadManagerState state = this.download_manager.getDownloadState();
/*      */     
/*  268 */     TOTorrent torrent = this.download_manager.getTorrent();
/*      */     
/*  270 */     if (torrent != null) {
/*      */       try
/*      */       {
/*  273 */         this.peer_manager_registration = PeerManager.getSingleton().registerLegacyManager(torrent.getHashWrapper(), this);
/*      */         
/*  275 */         this.md_info_dict_size = state.getIntAttribute("mdinfodictsize");
/*      */         
/*  277 */         if (this.md_info_dict_size == 0)
/*      */         {
/*      */           try {
/*  280 */             this.md_info_dict_size = BEncoder.encode((Map)torrent.serialiseToMap().get("info")).length;
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  284 */             this.md_info_dict_size = -1;
/*      */           }
/*      */           
/*  287 */           state.setIntAttribute("mdinfodictsize", this.md_info_dict_size);
/*      */         }
/*      */       }
/*      */       catch (TOTorrentException e)
/*      */       {
/*  292 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  296 */     if (state.parameterExists("dndflags")) {
/*  297 */       long flags = state.getLongParameter("dndflags");
/*  298 */       this.cached_complete_excluding_dnd = ((flags & 0x2) != 0L);
/*  299 */       this.cached_has_dnd_files = ((flags & 1L) != 0L);
/*  300 */       this.cached_values_set = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void startDownload(TRTrackerAnnouncer tracker_client)
/*      */   {
/*      */     DiskManager dm;
/*      */     
/*      */     try
/*      */     {
/*  311 */       this.control_mon.enter();
/*      */       
/*  313 */       if (getState() != 40)
/*      */       {
/*  315 */         Debug.out("DownloadManagerController::startDownload state must be ready, " + getState());
/*      */         
/*  317 */         setFailed("Inconsistent download state: startDownload, state = " + getState()); return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  322 */       if (tracker_client == null)
/*      */       {
/*  324 */         Debug.out("DownloadManagerController:startDownload: tracker_client is null");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  331 */         stopIt(70, false, false, false); return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  336 */       if (this.peer_manager != null)
/*      */       {
/*  338 */         Debug.out("DownloadManagerController::startDownload: peer manager not null");
/*      */         
/*      */ 
/*      */ 
/*  342 */         this.peer_manager.stopAll();
/*      */         
/*  344 */         SimpleTimer.removeTickReceiver(this);
/*      */         
/*  346 */         DownloadManagerRateController.removePeerManager(this.peer_manager);
/*      */         
/*  348 */         this.peer_manager = null;
/*      */       }
/*      */       
/*  351 */       dm = getDiskManager();
/*      */       
/*  353 */       if (dm == null)
/*      */       {
/*  355 */         Debug.out("DownloadManagerController::startDownload: disk manager is null"); return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  360 */       setState(50, false);
/*      */     }
/*      */     finally
/*      */     {
/*  364 */       this.control_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*  368 */     cacheNetworks();
/*      */     
/*      */ 
/*      */ 
/*  372 */     final PEPeerManager temp = org.gudy.azureus2.core3.peer.PEPeerManagerFactory.create(tracker_client.getPeerId(), this, dm);
/*      */     
/*  374 */     this.download_manager.informWillBeStarted(temp);
/*      */     
/*  376 */     temp.start();
/*      */     
/*      */ 
/*      */ 
/*  380 */     tracker_client.setAnnounceDataProvider(new org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerDataProvider()
/*      */     {
/*      */ 
/*  383 */       private final PEPeerManagerStats pm_stats = temp.getStats();
/*      */       
/*      */       private long last_reported_total_received;
/*      */       
/*      */       private long last_reported_total_received_data;
/*      */       private long last_reported_total_received_discard;
/*      */       private long last_reported_total_received_failed;
/*      */       
/*      */       public String getName()
/*      */       {
/*  393 */         return DownloadManagerController.this.getDisplayName();
/*      */       }
/*      */       
/*      */ 
/*      */       public long getTotalSent()
/*      */       {
/*  399 */         return DownloadManagerController.tracker_stats_exclude_lan ? this.pm_stats.getTotalDataBytesSentNoLan() : this.pm_stats.getTotalDataBytesSent();
/*      */       }
/*      */       
/*      */ 
/*      */       public long getTotalReceived()
/*      */       {
/*  405 */         long received = DownloadManagerController.tracker_stats_exclude_lan ? this.pm_stats.getTotalDataBytesReceivedNoLan() : this.pm_stats.getTotalDataBytesReceived();
/*  406 */         long discarded = this.pm_stats.getTotalDiscarded();
/*  407 */         long failed = this.pm_stats.getTotalHashFailBytes();
/*      */         
/*  409 */         long verified = received - (discarded + failed);
/*      */         
/*  411 */         verified -= temp.getHiddenBytes();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  416 */         if (verified < this.last_reported_total_received)
/*      */         {
/*  418 */           verified = this.last_reported_total_received;
/*      */           
/*      */ 
/*      */ 
/*  422 */           if (this.last_reported_total_received_data != -1L)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  432 */             this.last_reported_total_received_data = -1L;
/*      */           }
/*      */         }
/*      */         else {
/*  436 */           this.last_reported_total_received = verified;
/*      */           
/*  438 */           this.last_reported_total_received_data = received;
/*  439 */           this.last_reported_total_received_discard = discarded;
/*  440 */           this.last_reported_total_received_failed = failed;
/*      */         }
/*      */         
/*  443 */         return verified < 0L ? 0L : verified;
/*      */       }
/*      */       
/*      */ 
/*      */       public long getRemaining()
/*      */       {
/*  449 */         return Math.max(temp.getRemaining(), temp.getHiddenBytes());
/*      */       }
/*      */       
/*      */ 
/*      */       public long getFailedHashCheck()
/*      */       {
/*  455 */         return this.pm_stats.getTotalHashFailBytes();
/*      */       }
/*      */       
/*      */ 
/*      */       public String getExtensions()
/*      */       {
/*  461 */         return DownloadManagerController.this.getTrackerClientExtensions();
/*      */       }
/*      */       
/*      */ 
/*      */       public int getMaxNewConnectionsAllowed(String network)
/*      */       {
/*  467 */         return temp.getMaxNewConnectionsAllowed(network);
/*      */       }
/*      */       
/*      */ 
/*      */       public int getPendingConnectionCount()
/*      */       {
/*  473 */         return temp.getPendingPeerCount();
/*      */       }
/*      */       
/*      */ 
/*      */       public int getConnectedConnectionCount()
/*      */       {
/*  479 */         return temp.getNbPeers() + temp.getNbSeeds();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public int getUploadSpeedKBSec(boolean estimate)
/*      */       {
/*  486 */         long current_local = DownloadManagerController.this.stats.getDataSendRate();
/*      */         
/*  488 */         if (estimate)
/*      */         {
/*      */ 
/*      */ 
/*  492 */           current_local = DownloadManagerController.this.data_send_rate_at_close;
/*      */           
/*  494 */           if (current_local == 0L)
/*      */           {
/*  496 */             int current_global = DownloadManagerController.this.global_stats.getDataSendRate();
/*      */             
/*  498 */             int old_global = DownloadManagerController.this.global_stats.getDataSendRateAtClose();
/*      */             
/*  500 */             if (current_global < old_global)
/*      */             {
/*  502 */               current_global = old_global;
/*      */             }
/*      */             
/*  505 */             List managers = DownloadManagerController.this.download_manager.getGlobalManager().getDownloadManagers();
/*      */             
/*  507 */             int num_dls = 0;
/*      */             
/*      */ 
/*      */ 
/*  511 */             for (int i = 0; i < managers.size(); i++)
/*      */             {
/*  513 */               DownloadManager dm = (DownloadManager)managers.get(i);
/*      */               
/*  515 */               if (dm.getStats().getDownloadCompleted(false) != 1000)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  520 */                 int state = dm.getState();
/*      */                 
/*  522 */                 if ((state != 100) && (state != 65) && (state != 70))
/*      */                 {
/*      */ 
/*      */ 
/*  526 */                   num_dls++;
/*      */                 }
/*      */               }
/*      */             }
/*  530 */             if (num_dls == 0)
/*      */             {
/*  532 */               current_local = current_global;
/*      */             }
/*      */             else {
/*  535 */               current_local = current_global / num_dls;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  540 */         return (int)((current_local + 1023L) / 1024L);
/*      */       }
/*      */       
/*      */ 
/*      */       public int getCryptoLevel()
/*      */       {
/*  546 */         return DownloadManagerController.this.download_manager.getCryptoLevel();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void setPeerSources(String[] allowed_sources)
/*      */       {
/*  553 */         DownloadManagerState dms = DownloadManagerController.this.download_manager.getDownloadState();
/*      */         
/*  555 */         String[] sources = org.gudy.azureus2.core3.peer.PEPeerSource.PS_SOURCES;
/*      */         
/*  557 */         for (int i = 0; i < sources.length; i++)
/*      */         {
/*  559 */           String s = sources[i];
/*      */           
/*  561 */           boolean ok = false;
/*      */           
/*  563 */           for (int j = 0; j < allowed_sources.length; j++)
/*      */           {
/*  565 */             if (s.equals(allowed_sources[j]))
/*      */             {
/*  567 */               ok = true;
/*      */               
/*  569 */               break;
/*      */             }
/*      */           }
/*      */           
/*  573 */           if (!ok)
/*      */           {
/*  575 */             dms.setPeerSourcePermitted(s, false);
/*      */           }
/*      */         }
/*      */         
/*  579 */         PEPeerManager pm = DownloadManagerController.this.getPeerManager();
/*      */         
/*  581 */         if (pm != null)
/*      */         {
/*  583 */           Set<String> allowed = new HashSet();
/*      */           
/*  585 */           allowed.addAll(Arrays.asList(allowed_sources));
/*      */           
/*  587 */           Iterator<PEPeer> it = pm.getPeers().iterator();
/*      */           
/*  589 */           while (it.hasNext())
/*      */           {
/*  591 */             PEPeer peer = (PEPeer)it.next();
/*      */             
/*  593 */             if (!allowed.contains(peer.getPeerSource()))
/*      */             {
/*  595 */               pm.removePeer(peer, "Peer source not permitted");
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public boolean isPeerSourceEnabled(String peer_source)
/*      */       {
/*  605 */         return DownloadManagerController.this.isPeerSourceEnabled(peer_source);
/*      */       }
/*      */     });
/*      */     
/*      */     List<Object[]> limiters;
/*      */     
/*      */     try
/*      */     {
/*  613 */       this.control_mon.enter();
/*      */       
/*  615 */       this.peer_manager = temp;
/*      */       
/*  617 */       DownloadManagerRateController.addPeerManager(this.peer_manager);
/*      */       
/*  619 */       SimpleTimer.addTickReceiver(this);
/*      */       
/*  621 */       limiters = this.external_rate_limiters_cow;
/*      */     }
/*      */     finally
/*      */     {
/*  625 */       this.control_mon.exit();
/*      */     }
/*      */     
/*  628 */     if (limiters != null)
/*      */     {
/*  630 */       for (int i = 0; i < limiters.size(); i++)
/*      */       {
/*  632 */         Object[] entry = (Object[])limiters.get(i);
/*      */         
/*  634 */         temp.addRateLimiter((LimitedRateGroup)entry[0], ((Boolean)entry[1]).booleanValue());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  642 */     if (getState() == 50)
/*      */     {
/*  644 */       this.download_manager.informStateChanged();
/*      */     }
/*      */     
/*  647 */     this.download_manager.informStarted(temp);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void initializeDiskManager(boolean open_for_seeding)
/*      */   {
/*  657 */     initializeDiskManagerSupport(10, new DiskManagerListener_Default(open_for_seeding));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void initializeDiskManagerSupport(int initialising_state, DiskManagerListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  668 */       this.control_mon.enter();
/*      */       
/*  670 */       int entry_state = getState();
/*      */       
/*  672 */       if ((entry_state != 0) && (entry_state != 70) && (entry_state != 75) && (entry_state != 100))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  677 */         Debug.out("DownloadManagerController::initializeDiskManager: Illegal initialize state, " + entry_state);
/*      */         
/*  679 */         setFailed("Inconsistent download state: initSupport, state = " + entry_state);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  684 */         DiskManager old_dm = getDiskManager();
/*      */         
/*  686 */         if (old_dm != null)
/*      */         {
/*  688 */           Debug.out("DownloadManagerController::initializeDiskManager: disk manager is not null");
/*      */           
/*      */ 
/*      */ 
/*  692 */           old_dm.stop(false);
/*      */           
/*  694 */           setDiskManager(null, null);
/*      */         }
/*      */         
/*  697 */         this.errorDetail = "";
/*  698 */         this.errorType = 0;
/*      */         
/*  700 */         setState(initialising_state, false);
/*      */         
/*  702 */         DiskManager dm = DiskManagerFactory.create(this.download_manager.getTorrent(), this.download_manager);
/*      */         
/*  704 */         setDiskManager(dm, listener);
/*      */       }
/*      */     }
/*      */     finally {
/*  708 */       this.control_mon.exit();
/*      */       
/*  710 */       this.download_manager.informStateChanged();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canForceRecheck()
/*      */   {
/*  717 */     int state = getState();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  722 */     return (state == 70) || (state == 75) || ((state == 100) && (getDiskManager() == null));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void forceRecheck(ForceRecheckListener l)
/*      */   {
/*      */     try
/*      */     {
/*  731 */       this.control_mon.enter();
/*      */       
/*  733 */       if ((getDiskManager() != null) || (!canForceRecheck()))
/*      */       {
/*  735 */         Debug.out("DownloadManagerController::forceRecheck: illegal entry state");
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  740 */         int start_state = getState();
/*      */         
/*      */ 
/*      */ 
/*  744 */         this.download_manager.getDownloadState().clearResumeData();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  749 */         boolean wasForceStarted = this.force_start;
/*      */         
/*  751 */         this.force_start = true;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  757 */         this.download_manager.setDataAlreadyAllocated(false);
/*      */         
/*  759 */         initializeDiskManagerSupport(30, new forceRecheckDiskManagerListener(wasForceStarted, start_state, l));
/*      */       }
/*      */       
/*      */     }
/*      */     finally
/*      */     {
/*  765 */       this.control_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPieceCheckingEnabled(boolean enabled)
/*      */   {
/*  773 */     this.piece_checking_enabled = enabled;
/*      */     
/*  775 */     DiskManager dm = getDiskManager();
/*      */     
/*  777 */     if (dm != null)
/*      */     {
/*  779 */       dm.setPieceCheckingEnabled(enabled);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void stopIt(int _stateAfterStopping, boolean remove_torrent, boolean remove_data, boolean for_removal)
/*      */   {
/*  790 */     long current_up = this.stats.getDataSendRate();
/*      */     
/*  792 */     if (current_up != 0L)
/*      */     {
/*  794 */       this.data_send_rate_at_close = current_up;
/*      */     }
/*      */     
/*  797 */     boolean closing = _stateAfterStopping == 71;
/*      */     
/*  799 */     if (closing)
/*      */     {
/*  801 */       _stateAfterStopping = 70;
/*      */     }
/*      */     
/*  804 */     int stateAfterStopping = _stateAfterStopping;
/*      */     try
/*      */     {
/*  807 */       this.control_mon.enter();
/*      */       
/*  809 */       int state = getState();
/*      */       
/*  811 */       if ((state == 70) || ((state == 100) && (getDiskManager() == null)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  816 */         if (remove_data)
/*      */         {
/*  818 */           this.download_manager.deleteDataFiles();
/*      */ 
/*      */ 
/*      */         }
/*  822 */         else if ((for_removal) && (COConfigurationManager.getBooleanParameter("Delete Partial Files On Library Removal")))
/*      */         {
/*  824 */           this.download_manager.deletePartialDataFiles();
/*      */         }
/*      */         
/*      */ 
/*  828 */         if (remove_torrent)
/*      */         {
/*  830 */           this.download_manager.deleteTorrentFile();
/*      */         }
/*      */         
/*  833 */         setState(_stateAfterStopping, false);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  839 */         if (state == 65) {
/*      */           return;
/*      */         }
/*      */         
/*      */ 
/*  844 */         setSubState(_stateAfterStopping);
/*      */         
/*  846 */         setState(65, false);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  853 */         final AESemaphore nd_sem = new AESemaphore("DM:DownloadManager.NDTR");
/*      */         
/*  855 */         org.gudy.azureus2.core3.util.NonDaemonTaskRunner.runAsync(new org.gudy.azureus2.core3.util.NonDaemonTask()
/*      */         {
/*      */ 
/*      */           public Object run()
/*      */           {
/*      */ 
/*  861 */             nd_sem.reserve();
/*      */             
/*  863 */             return null;
/*      */           }
/*      */           
/*      */ 
/*      */           public String getName()
/*      */           {
/*  869 */             return "Stopping '" + DownloadManagerController.this.getDisplayName() + "'";
/*      */           }
/*      */         });
/*      */         
/*      */         try
/*      */         {
/*      */           try
/*      */           {
/*  877 */             if (this.peer_manager != null)
/*      */             {
/*  879 */               this.peer_manager.stopAll();
/*      */               
/*  881 */               this.stats.saveSessionTotals();
/*      */               
/*  883 */               DownloadManagerState dmState = this.download_manager.getDownloadState();
/*  884 */               dmState.setLongParameter("stats.download.last.active.time", SystemTime.getCurrentTime());
/*      */               
/*  886 */               SimpleTimer.removeTickReceiver(this);
/*      */               
/*  888 */               DownloadManagerRateController.removePeerManager(this.peer_manager);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  893 */             this.download_manager.informStopped(this.peer_manager, stateAfterStopping == 75);
/*      */             
/*  895 */             this.peer_manager = null;
/*      */             
/*  897 */             DiskManager dm = getDiskManager();
/*      */             
/*  899 */             if (dm != null)
/*      */             {
/*  901 */               boolean went_async = dm.stop(closing);
/*      */               
/*  903 */               if (went_async)
/*      */               {
/*  905 */                 int wait_count = 0;
/*      */                 
/*      */ 
/*  908 */                 Thread.sleep(10L);
/*      */                 
/*  910 */                 while (!dm.isStopped())
/*      */                 {
/*  912 */                   wait_count++;
/*      */                   
/*  914 */                   if (wait_count > 1200)
/*      */                   {
/*  916 */                     Debug.out("Download stop took too long to complete");
/*      */                     
/*  918 */                     break;
/*      */                   }
/*  920 */                   if (wait_count % 200 == 0)
/*      */                   {
/*  922 */                     Debug.out("Waiting for download to stop - elapsed=" + wait_count + " sec");
/*      */                   }
/*      */                   
/*  925 */                   Thread.sleep(100L);
/*      */                 }
/*      */               }
/*      */               
/*  929 */               this.stats.setCompleted(this.stats.getCompleted());
/*  930 */               this.stats.recalcDownloadCompleteBytes();
/*      */               
/*      */ 
/*      */ 
/*  934 */               if (!this.download_manager.getAssumedComplete()) {
/*  935 */                 this.download_manager.getDownloadState().save();
/*      */               }
/*      */               
/*  938 */               setDiskManager(null, null);
/*      */             }
/*      */           } finally { List<ExternalSeedPeer> to_remove;
/*      */             Iterator i$;
/*      */             ExternalSeedPeer peer;
/*  943 */             this.force_start = false;
/*      */             
/*  945 */             if (remove_data)
/*      */             {
/*  947 */               this.download_manager.deleteDataFiles();
/*      */ 
/*      */ 
/*      */             }
/*  951 */             else if ((for_removal) && (COConfigurationManager.getBooleanParameter("Delete Partial Files On Library Removal")))
/*      */             {
/*  953 */               this.download_manager.deletePartialDataFiles();
/*      */             }
/*      */             
/*      */ 
/*  957 */             if (remove_torrent)
/*      */             {
/*  959 */               this.download_manager.deleteTorrentFile();
/*      */             }
/*      */             
/*  962 */             List<ExternalSeedPeer> to_remove = new ArrayList();
/*      */             
/*  964 */             synchronized (this.http_seeds)
/*      */             {
/*  966 */               to_remove.addAll(this.http_seeds);
/*      */               
/*  968 */               this.http_seeds.clear();
/*      */             }
/*      */             
/*  971 */             for (ExternalSeedPeer peer : to_remove)
/*      */             {
/*  973 */               peer.remove();
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  978 */             if (getState() == 65)
/*      */             {
/*  980 */               setState(stateAfterStopping, true);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/*  985 */           nd_sem.release();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  990 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/*  994 */       this.control_mon.exit();
/*      */       
/*  996 */       this.download_manager.informStateChanged();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setStateWaiting()
/*      */   {
/* 1003 */     setState(0, true);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setStateFinishing()
/*      */   {
/* 1009 */     setState(55, true);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setStateDownloading()
/*      */   {
/* 1015 */     if (getState() == 60) {
/* 1016 */       setState(50, true);
/* 1017 */     } else if (getState() != 50) {
/* 1018 */       Logger.log(new LogEvent(this, LogIDs.CORE, 1, "Trying to set state to downloading when state is not seeding"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setStateSeeding(boolean never_downloaded)
/*      */   {
/* 1030 */     setStateFinishing();
/*      */     
/* 1032 */     this.download_manager.downloadEnded(never_downloaded);
/*      */     
/* 1034 */     setState(60, true);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isStateSeeding()
/*      */   {
/* 1040 */     return getState() == 60;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setStateQueued()
/*      */   {
/* 1046 */     setState(75, true);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getState()
/*      */   {
/* 1052 */     if (this.state_set_by_method != 10)
/*      */     {
/* 1054 */       return this.state_set_by_method;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1061 */     DiskManager dm = getDiskManager();
/*      */     
/* 1063 */     if (dm == null)
/*      */     {
/* 1065 */       return 10;
/*      */     }
/*      */     
/* 1068 */     int diskManagerState = dm.getState();
/*      */     
/* 1070 */     if (diskManagerState == 1)
/*      */     {
/* 1072 */       return 10;
/*      */     }
/* 1074 */     if (diskManagerState == 2)
/*      */     {
/* 1076 */       return 20;
/*      */     }
/* 1078 */     if (diskManagerState == 3)
/*      */     {
/* 1080 */       return 30;
/*      */     }
/* 1082 */     if (diskManagerState == 4)
/*      */     {
/* 1084 */       return 40;
/*      */     }
/* 1086 */     if (diskManagerState == 10)
/*      */     {
/* 1088 */       return 100;
/*      */     }
/*      */     
/* 1091 */     return 100;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getSubState()
/*      */   {
/* 1097 */     if (this.state_set_by_method == 65)
/*      */     {
/* 1099 */       return this.substate;
/*      */     }
/*      */     
/* 1102 */     return getState();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setSubState(int ss)
/*      */   {
/* 1110 */     this.substate = ss;
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
/*      */   private void setState(int _state, boolean _inform_changed)
/*      */   {
/* 1126 */     boolean call_filesExist = false;
/*      */     try
/*      */     {
/* 1129 */       this.state_mon.enter();
/*      */       
/* 1131 */       int old_state = this.state_set_by_method;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1138 */       if (old_state != _state)
/*      */       {
/* 1140 */         this.state_set_by_method = _state;
/*      */         
/* 1142 */         if (this.state_set_by_method != 75)
/*      */         {
/*      */ 
/*      */ 
/* 1146 */           this.activation_bloom = null;
/*      */           
/* 1148 */           if (this.state_set_by_method == 70)
/*      */           {
/* 1150 */             this.activation_count = 0;
/*      */           }
/*      */         }
/*      */         
/* 1154 */         if (this.state_set_by_method != 75)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1165 */           if (this.state_set_by_method == 100)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1172 */             TOTorrent torrent = this.download_manager.getTorrent();
/*      */             
/* 1174 */             if ((torrent != null) && (!torrent.isSimpleTorrent()))
/*      */             {
/* 1176 */               File save_dir_file = this.download_manager.getAbsoluteSaveLocation();
/*      */               
/* 1178 */               if ((save_dir_file != null) && (save_dir_file.exists()) && (save_dir_file.isDirectory()))
/*      */               {
/* 1180 */                 org.gudy.azureus2.core3.util.TorrentUtils.recursiveEmptyDirDelete(save_dir_file, false);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 1187 */       this.state_mon.exit();
/*      */     }
/*      */     
/* 1190 */     if (call_filesExist)
/*      */     {
/* 1192 */       filesExist(true);
/*      */     }
/*      */     
/* 1195 */     if (_inform_changed)
/*      */     {
/* 1197 */       this.download_manager.informStateChanged();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void restartDownload(boolean forceRecheck)
/*      */   {
/* 1208 */     boolean was_force_start = isForceStart();
/*      */     
/* 1210 */     stopIt(70, false, false, false);
/*      */     
/* 1212 */     if (forceRecheck) {
/* 1213 */       this.download_manager.getDownloadState().clearResumeData();
/*      */     }
/*      */     
/* 1216 */     this.download_manager.initialize();
/*      */     
/* 1218 */     if (was_force_start)
/*      */     {
/* 1220 */       setForceStart(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/* 1227 */     if (this.peer_manager_registration != null)
/*      */     {
/* 1229 */       this.peer_manager_registration.unregister();
/*      */       
/* 1231 */       this.peer_manager_registration = null;
/*      */     }
/*      */     
/* 1234 */     this.fileFacadeSet.destroyFileInfo();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isPeerSourceEnabled(String peer_source)
/*      */   {
/* 1241 */     return this.download_manager.getDownloadState().isPeerSourceEnabled(peer_source);
/*      */   }
/*      */   
/*      */ 
/*      */   private void cacheNetworks()
/*      */   {
/* 1247 */     synchronized (this.cached_networks_lock)
/*      */     {
/* 1249 */       if (this.cached_networks != null)
/*      */       {
/* 1251 */         return;
/*      */       }
/*      */       
/* 1254 */       DownloadManagerState state = this.download_manager.getDownloadState();
/*      */       
/* 1256 */       this.cached_networks = new HashSet(Arrays.asList(state.getNetworks()));
/*      */       
/* 1258 */       state.addListener(new org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void attributeEventOccurred(DownloadManager download, String attribute, int event_type)
/*      */         {
/*      */ 
/*      */ 
/* 1267 */           DownloadManagerState state = DownloadManagerController.this.download_manager.getDownloadState();
/*      */           
/* 1269 */           synchronized (DownloadManagerController.this.cached_networks_lock)
/*      */           {
/* 1271 */             DownloadManagerController.this.cached_networks = new HashSet(Arrays.asList(state.getNetworks()));
/*      */           }
/*      */           
/* 1274 */           PEPeerManager pm = DownloadManagerController.this.peer_manager;
/*      */           
/* 1276 */           if (pm != null)
/*      */           {
/* 1278 */             Object peers = pm.getPeers();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1283 */             for (PEPeer peer : (List)peers)
/*      */             {
/* 1285 */               pm.removePeer(peer, "Networks changed, reconnection required"); } } } }, "networks", 1);
/*      */     }
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
/*      */   public boolean isNetworkEnabled(String network)
/*      */   {
/* 1299 */     Set<String> cache = this.cached_networks;
/*      */     
/* 1301 */     if (cache == null)
/*      */     {
/* 1303 */       return this.download_manager.getDownloadState().isNetworkEnabled(network);
/*      */     }
/*      */     
/* 1306 */     return cache.contains(network);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String[] getEnabledNetworks()
/*      */   {
/* 1313 */     Set<String> cache = this.cached_networks;
/*      */     
/* 1315 */     if (cache == null)
/*      */     {
/* 1317 */       return this.download_manager.getDownloadState().getNetworks();
/*      */     }
/*      */     
/*      */ 
/* 1321 */     return (String[])cache.toArray(new String[cache.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[][] getSecrets()
/*      */   {
/* 1329 */     TOTorrent torrent = this.download_manager.getTorrent();
/*      */     try
/*      */     {
/* 1332 */       byte[] secret1 = torrent.getHash();
/*      */       
/*      */       try
/*      */       {
/* 1336 */         byte[] secret2 = getSecret2(torrent);
/*      */         
/* 1338 */         return new byte[][] { secret1, secret2 };
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1342 */         Debug.printStackTrace(e);
/*      */         
/* 1344 */         return new byte[][] { secret1 };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1351 */       return new byte[0][];
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1349 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[][] getSecrets(int crypto_level)
/*      */   {
/* 1361 */     TOTorrent torrent = this.download_manager.getTorrent();
/*      */     try
/*      */     {
/*      */       byte[] secret;
/*      */       byte[] secret;
/* 1366 */       if (crypto_level == 1)
/*      */       {
/* 1368 */         secret = torrent.getHash();
/*      */       }
/*      */       else
/*      */       {
/* 1372 */         secret = getSecret2(torrent);
/*      */       }
/*      */       
/* 1375 */       return new byte[][] { secret };
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1379 */       Debug.printStackTrace(e);
/*      */     }
/* 1381 */     return new byte[0][];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] getSecret2(TOTorrent torrent)
/*      */     throws TOTorrentException
/*      */   {
/* 1391 */     Map secrets_map = this.download_manager.getDownloadState().getMapAttribute("secrets");
/*      */     
/* 1393 */     if (secrets_map == null)
/*      */     {
/* 1395 */       secrets_map = new HashMap();
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/* 1401 */       secrets_map = new org.gudy.azureus2.core3.util.LightHashMap(secrets_map);
/*      */     }
/*      */     
/* 1404 */     if (secrets_map.size() == 0)
/*      */     {
/* 1406 */       secrets_map.put("p1", torrent.getPieces()[0]);
/*      */       
/* 1408 */       this.download_manager.getDownloadState().setMapAttribute("secrets", secrets_map);
/*      */     }
/*      */     
/* 1411 */     return (byte[])secrets_map.get("p1");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean manualRoute(com.aelitis.azureus.core.networkmanager.NetworkConnection connection)
/*      */   {
/* 1418 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getRandomSeed()
/*      */   {
/* 1424 */     return this.download_manager.getDownloadState().getLongParameter("rand");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addRateLimiter(LimitedRateGroup group, boolean upload)
/*      */   {
/*      */     PEPeerManager pm;
/*      */     
/*      */     try
/*      */     {
/* 1435 */       this.control_mon.enter();
/*      */       
/* 1437 */       ArrayList<Object[]> new_limiters = new ArrayList(this.external_rate_limiters_cow == null ? 1 : this.external_rate_limiters_cow.size() + 1);
/*      */       
/* 1439 */       if (this.external_rate_limiters_cow != null)
/*      */       {
/* 1441 */         new_limiters.addAll(this.external_rate_limiters_cow);
/*      */       }
/*      */       
/* 1444 */       new_limiters.add(new Object[] { group, Boolean.valueOf(upload) });
/*      */       
/* 1446 */       this.external_rate_limiters_cow = new_limiters;
/*      */       
/* 1448 */       pm = this.peer_manager;
/*      */     }
/*      */     finally
/*      */     {
/* 1452 */       this.control_mon.exit();
/*      */     }
/*      */     
/* 1455 */     if (pm != null)
/*      */     {
/* 1457 */       pm.addRateLimiter(group, upload);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public LimitedRateGroup[] getRateLimiters(boolean upload)
/*      */   {
/*      */     try
/*      */     {
/* 1466 */       this.control_mon.enter();
/*      */       
/* 1468 */       if (this.external_rate_limiters_cow == null)
/*      */       {
/* 1470 */         return new LimitedRateGroup[0];
/*      */       }
/*      */       
/*      */ 
/* 1474 */       Object result = new ArrayList();
/*      */       
/* 1476 */       for (Object[] entry : this.external_rate_limiters_cow)
/*      */       {
/* 1478 */         if (((Boolean)entry[1]).booleanValue() == upload)
/*      */         {
/* 1480 */           ((List)result).add((LimitedRateGroup)entry[0]);
/*      */         }
/*      */       }
/*      */       
/* 1484 */       return (LimitedRateGroup[])((List)result).toArray(new LimitedRateGroup[((List)result).size()]);
/*      */     }
/*      */     finally
/*      */     {
/* 1488 */       this.control_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeRateLimiter(LimitedRateGroup group, boolean upload)
/*      */   {
/*      */     PEPeerManager pm;
/*      */     
/*      */     try
/*      */     {
/* 1500 */       this.control_mon.enter();
/*      */       
/* 1502 */       if (this.external_rate_limiters_cow != null)
/*      */       {
/* 1504 */         ArrayList<Object[]> new_limiters = new ArrayList(this.external_rate_limiters_cow.size() - 1);
/*      */         
/* 1506 */         for (int i = 0; i < this.external_rate_limiters_cow.size(); i++)
/*      */         {
/* 1508 */           Object[] entry = (Object[])this.external_rate_limiters_cow.get(i);
/*      */           
/* 1510 */           if (entry[0] != group)
/*      */           {
/* 1512 */             new_limiters.add(entry);
/*      */           }
/*      */         }
/*      */         
/* 1516 */         if (new_limiters.size() == 0)
/*      */         {
/* 1518 */           this.external_rate_limiters_cow = null;
/*      */         }
/*      */         else
/*      */         {
/* 1522 */           this.external_rate_limiters_cow = new_limiters;
/*      */         }
/*      */       }
/*      */       
/* 1526 */       pm = this.peer_manager;
/*      */     }
/*      */     finally
/*      */     {
/* 1530 */       this.control_mon.exit();
/*      */     }
/*      */     
/* 1533 */     if (pm != null)
/*      */     {
/* 1535 */       pm.removeRateLimiter(group, upload);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enqueueReadRequest(PEPeer peer, DiskManagerReadRequest request, DiskManagerReadRequestListener listener)
/*      */   {
/* 1545 */     getDiskManager().enqueueReadRequest(request, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean activateRequest(InetSocketAddress address)
/*      */   {
/* 1552 */     if (getState() == 75)
/*      */     {
/* 1554 */       BloomFilter bloom = this.activation_bloom;
/*      */       
/* 1556 */       if (bloom == null)
/*      */       {
/* 1558 */         this.activation_bloom = (bloom = BloomFilterFactory.createAddRemove4Bit(64));
/*      */       }
/*      */       
/* 1561 */       byte[] address_bytes = AddressUtils.getAddressBytes(address);
/*      */       
/* 1563 */       int hit_count = bloom.add(address_bytes);
/*      */       
/* 1565 */       if (hit_count > 5)
/*      */       {
/* 1567 */         Logger.log(new LogEvent(this, LogIDs.CORE, 1, "Activate request for " + getDisplayName() + " from " + address + " denied as too many recently received"));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1574 */         return false;
/*      */       }
/*      */       
/* 1577 */       Logger.log(new LogEvent(this, LogIDs.CORE, "Activate request for " + getDisplayName() + " from " + address));
/*      */       
/* 1579 */       long now = SystemTime.getCurrentTime();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1584 */       if ((now < this.activation_bloom_create_time) || (now - this.activation_bloom_create_time > 600000L))
/*      */       {
/* 1586 */         this.activation_bloom = BloomFilterFactory.createAddRemove4Bit(64);
/*      */         
/* 1588 */         this.activation_bloom_create_time = now;
/*      */       }
/*      */       
/* 1591 */       this.activation_count = bloom.getEntryCount();
/*      */       
/* 1593 */       this.activation_count_time = now;
/*      */       
/* 1595 */       return this.download_manager.activateRequest(this.activation_count);
/*      */     }
/*      */     
/* 1598 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void deactivateRequest(InetSocketAddress address)
/*      */   {
/* 1605 */     BloomFilter bloom = this.activation_bloom;
/*      */     
/* 1607 */     if (bloom != null)
/*      */     {
/* 1609 */       byte[] address_bytes = AddressUtils.getAddressBytes(address);
/*      */       
/* 1611 */       int count = bloom.count(address_bytes);
/*      */       
/* 1613 */       for (int i = 0; i < count; i++)
/*      */       {
/* 1615 */         bloom.remove(address_bytes);
/*      */       }
/*      */       
/* 1618 */       this.activation_count = bloom.getEntryCount();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getActivationCount()
/*      */   {
/* 1628 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1630 */     if (now < this.activation_count_time)
/*      */     {
/* 1632 */       this.activation_count_time = now;
/*      */     }
/* 1634 */     else if (now - this.activation_count_time > 600000L)
/*      */     {
/* 1636 */       this.activation_count = 0;
/*      */     }
/*      */     
/* 1639 */     return this.activation_count;
/*      */   }
/*      */   
/*      */ 
/*      */   public PeerManagerRegistration getPeerManagerRegistration()
/*      */   {
/* 1645 */     return this.peer_manager_registration;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isForceStart()
/*      */   {
/* 1651 */     return this.force_start;
/*      */   }
/*      */   
/*      */ 
/*      */   public void setForceStart(boolean _force_start)
/*      */   {
/*      */     try
/*      */     {
/* 1659 */       this.state_mon.enter();
/*      */       
/* 1661 */       if (this.force_start != _force_start)
/*      */       {
/* 1663 */         this.force_start = _force_start;
/*      */         
/* 1665 */         int state = getState();
/*      */         
/* 1667 */         if ((this.force_start) && ((state == 70) || (state == 75)))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1673 */           setState(0, false);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1678 */       this.state_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1683 */     this.download_manager.informStateChanged();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setFailed(DiskManager dm)
/*      */   {
/* 1690 */     setFailed(dm.getErrorType(), dm.getErrorMessage());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setFailed(String reason)
/*      */   {
/* 1697 */     setFailed(1, reason);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setFailed(int type, String reason)
/*      */   {
/* 1705 */     if (reason != null)
/*      */     {
/* 1707 */       this.errorDetail = reason;
/*      */     }
/*      */     
/* 1710 */     this.errorType = type;
/*      */     
/* 1712 */     stopIt(100, false, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean filesExist(boolean expected_to_be_allocated)
/*      */   {
/* 1719 */     if (!expected_to_be_allocated)
/*      */     {
/* 1721 */       if (!this.download_manager.isDataAlreadyAllocated())
/*      */       {
/* 1723 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 1727 */     DiskManager dm = getDiskManager();
/*      */     
/* 1729 */     if (dm != null) {
/* 1730 */       return dm.filesExist();
/*      */     }
/*      */     
/* 1733 */     this.fileFacadeSet.makeSureFilesFacadeFilled(false);
/*      */     
/* 1735 */     DiskManagerFileInfo[] files = this.fileFacadeSet.getFiles();
/*      */     
/* 1737 */     for (int i = 0; i < files.length; i++) {
/* 1738 */       DiskManagerFileInfo fileInfo = files[i];
/* 1739 */       if (!fileInfo.isSkipped()) {
/* 1740 */         File file = fileInfo.getFile(true);
/*      */         try {
/* 1742 */           long start = SystemTime.getMonotonousTime();
/*      */           
/* 1744 */           boolean exists = file.exists();
/*      */           
/* 1746 */           long elapsed = SystemTime.getMonotonousTime() - start;
/*      */           
/* 1748 */           if (elapsed >= 500L)
/*      */           {
/* 1750 */             Debug.out("Accessing '" + file.getAbsolutePath() + "' in '" + getDisplayName() + "' took " + elapsed + "ms - possibly offline");
/*      */           }
/*      */           
/* 1753 */           if (!exists)
/*      */           {
/*      */ 
/* 1756 */             if (!this.download_manager.getTorrent().isSimpleTorrent()) {
/* 1757 */               File save_path = this.download_manager.getAbsoluteSaveLocation();
/* 1758 */               if ((org.gudy.azureus2.core3.util.FileUtil.isAncestorOf(save_path, file)) && (!save_path.exists())) {
/* 1759 */                 file = save_path;
/*      */               }
/*      */             }
/*      */             
/* 1763 */             setFailed(MessageText.getString("DownloadManager.error.datamissing") + " " + file);
/*      */             
/* 1765 */             return false;
/*      */           }
/* 1767 */           if (fileInfo.getLength() < file.length())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1773 */             if (!COConfigurationManager.getBooleanParameter("File.truncate.if.too.large"))
/*      */             {
/* 1775 */               setFailed(MessageText.getString("DownloadManager.error.badsize") + " " + file + "(" + fileInfo.getLength() + "/" + file.length() + ")");
/*      */               
/*      */ 
/*      */ 
/* 1779 */               return false;
/*      */             }
/*      */           }
/*      */         } catch (Exception e) {
/* 1783 */           setFailed(e.getMessage());
/* 1784 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1789 */     return true;
/*      */   }
/*      */   
/*      */   public DiskManagerFileInfoSet getDiskManagerFileInfoSet()
/*      */   {
/* 1794 */     this.fileFacadeSet.makeSureFilesFacadeFilled(false);
/*      */     
/* 1796 */     return this.fileFacadeSet;
/*      */   }
/*      */   
/*      */ 
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public DiskManagerFileInfo[] getDiskManagerFileInfo()
/*      */   {
/* 1805 */     this.fileFacadeSet.makeSureFilesFacadeFilled(false);
/*      */     
/* 1807 */     return this.fileFacadeSet.getFiles();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void fileInfoChanged()
/*      */   {
/* 1813 */     this.fileFacadeSet.makeSureFilesFacadeFilled(true);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void filePrioritiesChanged(List files)
/*      */   {
/* 1819 */     if (!this.cached_values_set) {
/* 1820 */       this.fileFacadeSet.makeSureFilesFacadeFilled(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1825 */     if ((!this.cached_has_dnd_files) && (files.size() == 1) && (!((DiskManagerFileInfo)files.get(0)).isSkipped())) {
/* 1826 */       return;
/*      */     }
/*      */     
/* 1829 */     this.fileFacadeSet.makeSureFilesFacadeFilled(false);
/* 1830 */     calculateCompleteness(this.fileFacadeSet.facadeFiles);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void calculateCompleteness(DiskManagerFileInfo[] active)
/*      */   {
/* 1837 */     boolean complete_excluding_dnd = true;
/*      */     
/* 1839 */     boolean has_dnd_files = false;
/*      */     
/* 1841 */     for (int i = 0; i < active.length; i++)
/*      */     {
/* 1843 */       DiskManagerFileInfo file = active[i];
/*      */       
/* 1845 */       if (file.isSkipped())
/*      */       {
/* 1847 */         has_dnd_files = true;
/*      */       }
/* 1849 */       else if (file.getDownloaded() != file.getLength())
/*      */       {
/* 1851 */         complete_excluding_dnd = false;
/*      */       }
/*      */       
/*      */ 
/* 1855 */       if ((has_dnd_files) && (!complete_excluding_dnd)) {
/*      */         break;
/*      */       }
/*      */     }
/* 1859 */     this.cached_complete_excluding_dnd = complete_excluding_dnd;
/* 1860 */     this.cached_has_dnd_files = has_dnd_files;
/* 1861 */     this.cached_values_set = true;
/* 1862 */     DownloadManagerState state = this.download_manager.getDownloadState();
/* 1863 */     long flags = (this.cached_complete_excluding_dnd ? 2L : 0L) | (this.cached_has_dnd_files ? 1L : 0L);
/*      */     
/* 1865 */     state.setLongParameter("dndflags", flags);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isDownloadComplete(boolean bIncludeDND)
/*      */   {
/* 1875 */     if (!this.cached_values_set) {
/* 1876 */       this.fileFacadeSet.makeSureFilesFacadeFilled(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1882 */     if (!this.cached_has_dnd_files) {
/* 1883 */       return this.stats.getRemaining() == 0L;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1888 */     DiskManager dm = getDiskManager();
/*      */     
/*      */ 
/* 1891 */     if (dm != null)
/*      */     {
/* 1893 */       int dm_state = dm.getState();
/*      */       
/* 1895 */       if (dm_state == 4) {
/* 1896 */         long remaining = bIncludeDND ? dm.getRemaining() : dm.getRemainingExcludingDND();
/*      */         
/* 1898 */         return remaining == 0L;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1904 */     if (bIncludeDND)
/*      */     {
/*      */ 
/* 1907 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1911 */     return this.cached_complete_excluding_dnd;
/*      */   }
/*      */   
/*      */ 
/*      */   protected PEPeerManager getPeerManager()
/*      */   {
/* 1917 */     return this.peer_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   protected DiskManager getDiskManager()
/*      */   {
/* 1923 */     return this.disk_manager_use_accessors;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getErrorDetail()
/*      */   {
/* 1929 */     return this.errorDetail;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getErrorType()
/*      */   {
/* 1935 */     return this.errorType;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setDiskManager(DiskManager new_disk_manager, DiskManagerListener new_disk_manager_listener)
/*      */   {
/* 1943 */     if (new_disk_manager != null)
/*      */     {
/* 1945 */       new_disk_manager.setPieceCheckingEnabled(this.piece_checking_enabled);
/*      */     }
/*      */     try
/*      */     {
/* 1949 */       this.disk_listeners_mon.enter();
/*      */       
/* 1951 */       DiskManager old_disk_manager = this.disk_manager_use_accessors;
/*      */       
/*      */ 
/*      */ 
/* 1955 */       if ((old_disk_manager != null) && (this.disk_manager_listener_use_accessors != null))
/*      */       {
/* 1957 */         old_disk_manager.removeListener(this.disk_manager_listener_use_accessors);
/*      */       }
/*      */       
/* 1960 */       this.disk_manager_use_accessors = new_disk_manager;
/* 1961 */       this.disk_manager_listener_use_accessors = new_disk_manager_listener;
/*      */       
/* 1963 */       if (new_disk_manager != null)
/*      */       {
/* 1965 */         new_disk_manager.addListener(new_disk_manager_listener);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1971 */       fileInfoChanged();
/*      */       
/* 1973 */       if ((new_disk_manager == null) && (old_disk_manager != null))
/*      */       {
/* 1975 */         this.disk_listeners.dispatch(2, old_disk_manager);
/*      */       }
/* 1977 */       else if ((new_disk_manager != null) && (old_disk_manager == null))
/*      */       {
/* 1979 */         this.disk_listeners.dispatch(1, new_disk_manager);
/*      */       }
/*      */       else
/*      */       {
/* 1983 */         Debug.out("inconsistent DiskManager state - " + new_disk_manager + "/" + old_disk_manager);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1988 */       this.disk_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addDiskListener(DownloadManagerDiskListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 1997 */       this.disk_listeners_mon.enter();
/*      */       
/* 1999 */       this.disk_listeners.addListener(listener);
/*      */       
/* 2001 */       DiskManager dm = getDiskManager();
/*      */       
/* 2003 */       if (dm != null)
/*      */       {
/* 2005 */         this.disk_listeners.dispatch(listener, 1, dm);
/*      */       }
/*      */     }
/*      */     finally {
/* 2009 */       this.disk_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeDiskListener(DownloadManagerDiskListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 2018 */       this.disk_listeners_mon.enter();
/*      */       
/* 2020 */       this.disk_listeners.removeListener(listener);
/*      */     }
/*      */     finally
/*      */     {
/* 2024 */       this.disk_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public long getDiskListenerCount() {
/* 2029 */     return this.disk_listeners.size();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDisplayName()
/*      */   {
/* 2035 */     return this.download_manager.getDisplayName();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getUploadRateLimitBytesPerSecond()
/*      */   {
/* 2041 */     return this.download_manager.getEffectiveUploadRateLimitBytesPerSecond();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDownloadRateLimitBytesPerSecond()
/*      */   {
/* 2047 */     return this.stats.getDownloadRateLimitBytesPerSecond();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getPermittedBytesToReceive()
/*      */   {
/* 2056 */     return NetworkManager.getSingleton().getRateHandler(false, false).getCurrentNumBytesAllowed()[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void permittedReceiveBytesUsed(int bytes)
/*      */   {
/* 2063 */     NetworkManager.getSingleton().getRateHandler(false, false).bytesProcessed(bytes, 0);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPermittedBytesToSend()
/*      */   {
/* 2069 */     return NetworkManager.getSingleton().getRateHandler(true, false).getCurrentNumBytesAllowed()[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void permittedSendBytesUsed(int bytes)
/*      */   {
/* 2076 */     NetworkManager.getSingleton().getRateHandler(true, false).bytesProcessed(bytes, 0);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxUploads()
/*      */   {
/* 2082 */     return this.download_manager.getEffectiveMaxUploads();
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getMaxConnections()
/*      */   {
/*      */     int[] result;
/*      */     int[] result;
/* 2090 */     if ((this.download_manager.isMaxConnectionsWhenSeedingEnabled()) && (isStateSeeding()))
/*      */     {
/* 2092 */       result = this.download_manager.getMaxConnectionsWhenSeeding(getEnabledNetworks().length > 1);
/*      */     }
/*      */     else
/*      */     {
/* 2096 */       result = this.download_manager.getMaxConnections(getEnabledNetworks().length > 1);
/*      */     }
/*      */     
/* 2099 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getMaxSeedConnections()
/*      */   {
/* 2105 */     return this.download_manager.getMaxSeedConnections(getEnabledNetworks().length > 1);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getUploadPriority()
/*      */   {
/* 2111 */     return this.download_manager.getEffectiveUploadPriority();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getExtendedMessagingMode()
/*      */   {
/* 2117 */     return this.download_manager.getExtendedMessagingMode();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isPeerExchangeEnabled()
/*      */   {
/* 2124 */     return this.download_manager.getDownloadState().isPeerSourceEnabled("PeerExchange");
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCryptoLevel()
/*      */   {
/* 2130 */     return this.download_manager.getCryptoLevel();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPeriodicRescanEnabled()
/*      */   {
/* 2136 */     return this.download_manager.getDownloadState().getFlag(2L);
/*      */   }
/*      */   
/*      */ 
/*      */   public org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse getTrackerScrapeResponse()
/*      */   {
/* 2142 */     return this.download_manager.getTrackerScrapeResponse();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getTrackerClientExtensions()
/*      */   {
/* 2148 */     return this.download_manager.getDownloadState().getTrackerClientExtensions();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTrackerRefreshDelayOverrides(int percent)
/*      */   {
/* 2155 */     this.download_manager.setTrackerRefreshDelayOverrides(percent);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isNATHealthy()
/*      */   {
/* 2161 */     return this.download_manager.getNATStatus() == 1;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isMetadataDownload()
/*      */   {
/* 2167 */     return this.download_manager.getDownloadState().getFlag(512L);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTorrentInfoDictSize()
/*      */   {
/* 2173 */     return this.md_info_dict_size;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getTorrentInfoDict(PEPeer peer)
/*      */   {
/*      */     try
/*      */     {
/* 2181 */       String ip = peer.getIp();
/*      */       
/* 2183 */       synchronized (this.md_info_peer_history)
/*      */       {
/* 2185 */         int now_secs = (int)(SystemTime.getMonotonousTime() / 1000L);
/*      */         
/* 2187 */         int[] stats = (int[])this.md_info_peer_history.get(ip);
/*      */         
/* 2189 */         if (stats == null)
/*      */         {
/* 2191 */           stats = new int[] { now_secs, 0 };
/*      */           
/* 2193 */           this.md_info_peer_history.put(ip, stats);
/*      */         }
/*      */         
/* 2196 */         if (now_secs - stats[0] > 300)
/*      */         {
/* 2198 */           stats[1] = 16384;
/*      */         }
/*      */         else
/*      */         {
/* 2202 */           int bytes = stats[1];
/*      */           
/* 2204 */           if (bytes >= this.md_info_dict_size * 3)
/*      */           {
/* 2206 */             return null;
/*      */           }
/*      */           
/* 2209 */           stats[1] = (bytes + 16384);
/*      */         }
/*      */       }
/*      */       
/* 2213 */       byte[] data = (byte[])this.md_info_dict_ref.get();
/*      */       
/* 2215 */       if (data == null)
/*      */       {
/* 2217 */         TOTorrent torrent = this.download_manager.getTorrent();
/*      */         
/* 2219 */         data = BEncoder.encode((Map)torrent.serialiseToMap().get("info"));
/*      */         
/* 2221 */         this.md_info_dict_ref = new WeakReference(data);
/*      */       }
/*      */       
/* 2224 */       return data;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 2228 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addPeer(PEPeer peer)
/*      */   {
/* 2236 */     this.download_manager.addPeer(peer);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePeer(PEPeer peer)
/*      */   {
/* 2243 */     this.download_manager.removePeer(peer);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPiece(PEPiece piece)
/*      */   {
/* 2250 */     this.download_manager.addPiece(piece);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePiece(PEPiece piece)
/*      */   {
/* 2257 */     this.download_manager.removePiece(piece);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void discarded(PEPeer peer, int bytes)
/*      */   {
/* 2265 */     if (this.global_stats != null)
/*      */     {
/* 2267 */       this.global_stats.discarded(bytes);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void protocolBytesReceived(PEPeer peer, int bytes)
/*      */   {
/* 2276 */     if (this.global_stats != null)
/*      */     {
/* 2278 */       this.global_stats.protocolBytesReceived(bytes, peer.isLANLocal());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void dataBytesReceived(PEPeer peer, int bytes)
/*      */   {
/* 2287 */     if (this.global_stats != null)
/*      */     {
/* 2289 */       this.global_stats.dataBytesReceived(bytes, peer.isLANLocal());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void protocolBytesSent(PEPeer peer, int bytes)
/*      */   {
/* 2298 */     if (this.global_stats != null)
/*      */     {
/* 2300 */       this.global_stats.protocolBytesSent(bytes, peer.isLANLocal());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void dataBytesSent(PEPeer peer, int bytes)
/*      */   {
/* 2309 */     if (this.global_stats != null)
/*      */     {
/* 2311 */       this.global_stats.dataBytesSent(bytes, peer.isLANLocal());
/*      */     }
/*      */   }
/*      */   
/*      */   public int getPosition() {
/* 2316 */     return this.download_manager.getPosition();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void tick(long mono_now, int tick_count)
/*      */   {
/* 2324 */     this.stats.timerTick(tick_count);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void statsRequest(PEPeer originator, Map request, Map reply)
/*      */   {
/* 2333 */     GlobalManager gm = this.download_manager.getGlobalManager();
/*      */     
/* 2335 */     gm.statsRequest(request, reply);
/*      */     
/* 2337 */     Map info = new HashMap();
/*      */     
/* 2339 */     reply.put("dl", info);
/*      */     List<Long> slot_up;
/*      */     try {
/* 2342 */       info.put("u_lim", new Long(getUploadRateLimitBytesPerSecond()));
/* 2343 */       info.put("d_lim", new Long(getDownloadRateLimitBytesPerSecond()));
/*      */       
/* 2345 */       info.put("u_rate", new Long(this.stats.getProtocolSendRate() + this.stats.getDataSendRate()));
/* 2346 */       info.put("d_rate", new Long(this.stats.getProtocolReceiveRate() + this.stats.getDataReceiveRate()));
/*      */       
/* 2348 */       info.put("u_slot", new Long(getMaxUploads()));
/* 2349 */       info.put("c_max", new Long(getMaxConnections()[0]));
/*      */       
/* 2351 */       info.put("c_leech", new Long(this.download_manager.getNbPeers()));
/* 2352 */       info.put("c_seed", new Long(this.download_manager.getNbSeeds()));
/*      */       
/* 2354 */       PEPeerManager pm = this.peer_manager;
/*      */       
/* 2356 */       if (pm != null)
/*      */       {
/* 2358 */         info.put("c_rem", Integer.valueOf(pm.getNbRemoteTCPConnections()));
/* 2359 */         info.put("c_rem_utp", Integer.valueOf(pm.getNbRemoteUTPConnections()));
/* 2360 */         info.put("c_rem_udp", Integer.valueOf(pm.getNbRemoteUDPConnections()));
/*      */         
/* 2362 */         List<PEPeer> peers = pm.getPeers();
/*      */         
/* 2364 */         slot_up = new ArrayList();
/*      */         
/* 2366 */         info.put("slot_up", slot_up);
/*      */         
/* 2368 */         for (PEPeer p : peers)
/*      */         {
/* 2370 */           if (!p.isChokedByMe())
/*      */           {
/* 2372 */             long up = p.getStats().getDataSendRate() + p.getStats().getProtocolSendRate();
/*      */             
/* 2374 */             slot_up.add(Long.valueOf(up));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addHTTPSeed(String address, int port)
/*      */   {
/* 2387 */     ExternalSeedPlugin plugin = getExternalSeedPlugin();
/*      */     try
/*      */     {
/* 2390 */       if (plugin != null)
/*      */       {
/* 2392 */         Map config = new HashMap();
/*      */         
/* 2394 */         List urls = new ArrayList();
/*      */         
/* 2396 */         String seed_url = "http://" + org.gudy.azureus2.core3.util.UrlUtils.convertIPV6Host(address) + ":" + port + "/webseed";
/*      */         
/* 2398 */         urls.add(seed_url.getBytes());
/*      */         
/* 2400 */         config.put("httpseeds", urls);
/*      */         
/* 2402 */         Map params = new HashMap();
/*      */         
/* 2404 */         params.put("supports_503", new Long(0L));
/* 2405 */         params.put("transient", new Long(1L));
/*      */         
/* 2407 */         config.put("httpseeds-params", params);
/*      */         
/* 2409 */         List<ExternalSeedPeer> new_seeds = plugin.addSeed(org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl.getDownloadStatic(this.download_manager), config);
/*      */         
/* 2411 */         if (new_seeds.size() > 0)
/*      */         {
/* 2413 */           List<ExternalSeedPeer> to_remove = new ArrayList();
/*      */           
/* 2415 */           synchronized (this.http_seeds)
/*      */           {
/* 2417 */             this.http_seeds.addAll(new_seeds);
/*      */             
/* 2419 */             while (this.http_seeds.size() > 64)
/*      */             {
/* 2421 */               ExternalSeedPeer x = (ExternalSeedPeer)this.http_seeds.removeFirst();
/*      */               
/* 2423 */               to_remove.add(x);
/*      */             }
/*      */           }
/*      */           
/* 2427 */           for (ExternalSeedPeer peer : to_remove)
/*      */           {
/* 2429 */             peer.remove();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 2435 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void priorityConnectionChanged(boolean added)
/*      */   {
/* 2443 */     synchronized (this)
/*      */     {
/* 2445 */       if (added)
/*      */       {
/* 2447 */         this.priority_connection_count += 1L;
/*      */       }
/*      */       else
/*      */       {
/* 2451 */         this.priority_connection_count -= 1L;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasPriorityConnection()
/*      */   {
/* 2459 */     synchronized (this)
/*      */     {
/* 2461 */       return this.priority_connection_count > 0L;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDescription()
/*      */   {
/* 2468 */     return this.download_manager.getDisplayName();
/*      */   }
/*      */   
/*      */ 
/*      */   public LogRelation getLogRelation()
/*      */   {
/* 2474 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRelationText()
/*      */   {
/* 2480 */     return this.download_manager.getRelationText();
/*      */   }
/*      */   
/*      */ 
/*      */   public Object[] getQueryableInterfaces()
/*      */   {
/* 2486 */     List interfaces = new ArrayList();
/*      */     
/* 2488 */     Object[] intf = this.download_manager.getQueryableInterfaces();
/*      */     
/* 2490 */     java.util.Collections.addAll(interfaces, intf);
/*      */     
/* 2492 */     interfaces.add(this.download_manager);
/*      */     
/* 2494 */     DiskManager dm = getDiskManager();
/*      */     
/* 2496 */     if (dm != null)
/*      */     {
/* 2498 */       interfaces.add(dm);
/*      */     }
/*      */     
/* 2501 */     return interfaces.toArray(); }
/*      */   
/*      */   protected class FileInfoFacadeSet implements DiskManagerFileInfoSet { DiskManagerFileInfoSet delegate;
/*      */     
/*      */     protected FileInfoFacadeSet() {}
/*      */     
/* 2507 */     DownloadManagerController.fileInfoFacade[] facadeFiles = new DownloadManagerController.fileInfoFacade[0];
/*      */     
/*      */     public DiskManagerFileInfo[] getFiles() {
/* 2510 */       return this.facadeFiles;
/*      */     }
/*      */     
/*      */     public int nbFiles() {
/* 2514 */       if (this.delegate == null) {
/* 2515 */         return 0;
/*      */       }
/* 2517 */       return this.delegate.nbFiles();
/*      */     }
/*      */     
/*      */     public void setPriority(int[] toChange) {
/* 2521 */       this.delegate.setPriority(toChange);
/*      */     }
/*      */     
/*      */     public void setSkipped(boolean[] toChange, boolean setSkipped) {
/* 2525 */       this.delegate.setSkipped(toChange, setSkipped);
/*      */     }
/*      */     
/*      */     public boolean[] setStorageTypes(boolean[] toChange, int newStroageType) {
/* 2529 */       return this.delegate.setStorageTypes(toChange, newStroageType);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void fixupFileInfo(DownloadManagerController.fileInfoFacade[] info)
/*      */     {
/* 2536 */       if (info.length == 0) { return;
/*      */       }
/* 2538 */       final List<DiskManagerFileInfo> delayed_prio_changes = new ArrayList(0);
/*      */       try
/*      */       {
/* 2541 */         DownloadManagerController.this.facade_mon.enter();
/* 2542 */         if (DownloadManagerController.this.files_facade_destroyed)
/*      */           return;
/* 2544 */         DiskManager dm = DownloadManagerController.this.getDiskManager();
/* 2545 */         DiskManagerFileInfoSet active = null;
/*      */         
/* 2547 */         if (dm != null) {
/* 2548 */           int dm_state = dm.getState();
/*      */           
/* 2550 */           if ((dm_state == 3) || (dm_state == 4)) {
/* 2551 */             active = dm.getFileSet();
/*      */           }
/*      */         }
/* 2554 */         if (active == null) {
/* 2555 */           final boolean[] initialising = { true };
/*      */           
/*      */ 
/*      */           try
/*      */           {
/* 2560 */             DownloadManagerController.access$608();
/* 2561 */             if (DownloadManagerController.skeleton_builds % 1000L == 0L) {
/* 2562 */               Debug.outNoStack("Skeleton builds: " + DownloadManagerController.skeleton_builds);
/*      */             }
/* 2564 */             active = DiskManagerFactory.getFileInfoSkeleton(DownloadManagerController.this.download_manager, new DiskManagerListener() {
/*      */               public void stateChanged(int oldState, int newState) {}
/*      */               
/*      */               public void filePriorityChanged(DiskManagerFileInfo file) {
/* 2568 */                 if (initialising[0] != 0) {
/* 2569 */                   delayed_prio_changes.add(file);
/*      */                 } else {
/* 2571 */                   DownloadManagerController.this.download_manager.informPriorityChange(file);
/*      */                 }
/*      */               }
/*      */               
/*      */               public void pieceDoneChanged(DiskManagerPiece piece) {}
/*      */               
/*      */               public void fileAccessModeChanged(DiskManagerFileInfo file, int old_mode, int new_mode) {}
/*      */             });
/*      */           } finally {
/* 2580 */             initialising[0] = false;
/*      */           }
/* 2582 */           DownloadManagerController.this.calculateCompleteness(active.getFiles());
/*      */         }
/*      */         
/* 2585 */         DiskManagerFileInfo[] activeFiles = active.getFiles();
/*      */         
/* 2587 */         for (int i = 0; i < info.length; i++) {
/* 2588 */           info[i].setDelegate(activeFiles[i]);
/*      */         }
/* 2590 */         this.delegate = active;
/*      */       }
/*      */       finally {
/* 2593 */         DownloadManagerController.this.facade_mon.exit();
/*      */       }
/*      */       
/* 2596 */       DownloadManagerController.this.fileFacadeSet.facadeFiles = info;
/* 2597 */       DownloadManagerController.this.download_manager.informPrioritiesChange(delayed_prio_changes);
/*      */       
/* 2599 */       delayed_prio_changes.clear();
/*      */     }
/*      */     
/*      */     private void makeSureFilesFacadeFilled(boolean refresh) {
/* 2603 */       if (!DownloadManagerController.this.bInitialized) { return;
/*      */       }
/* 2605 */       if (this.facadeFiles.length == 0) {
/* 2606 */         DownloadManagerController.fileInfoFacade[] newFacadeFiles = new DownloadManagerController.fileInfoFacade[DownloadManagerController.this.download_manager.getTorrent() == null ? 0 : DownloadManagerController.this.download_manager.getTorrent().getFiles().length];
/*      */         
/*      */ 
/* 2609 */         for (int i = 0; i < newFacadeFiles.length; i++) {
/* 2610 */           newFacadeFiles[i] = new DownloadManagerController.fileInfoFacade(DownloadManagerController.this);
/*      */         }
/*      */         
/* 2613 */         DownloadManagerController.this.fileFacadeSet.fixupFileInfo(newFacadeFiles);
/* 2614 */       } else if (refresh) {
/* 2615 */         fixupFileInfo(this.facadeFiles);
/*      */       }
/*      */     }
/*      */     
/*      */     protected void destroyFileInfo()
/*      */     {
/*      */       try {
/* 2622 */         DownloadManagerController.this.facade_mon.enter();
/* 2623 */         if ((DownloadManagerController.this.fileFacadeSet == null) || (DownloadManagerController.this.files_facade_destroyed)) {
/*      */           return;
/*      */         }
/* 2626 */         DownloadManagerController.this.files_facade_destroyed = true;
/*      */         
/* 2628 */         for (int i = 0; i < this.facadeFiles.length; i++)
/* 2629 */           this.facadeFiles[i].close();
/*      */       } finally {
/* 2631 */         DownloadManagerController.this.facade_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected class fileInfoFacade
/*      */     implements DiskManagerFileInfo
/*      */   {
/*      */     private volatile DiskManagerFileInfo delegate;
/*      */     
/*      */ 
/*      */     private List<DiskManagerFileInfoListener> listeners;
/*      */     
/*      */ 
/*      */ 
/*      */     protected fileInfoFacade() {}
/*      */     
/*      */ 
/*      */     protected void setDelegate(DiskManagerFileInfo new_delegate)
/*      */     {
/*      */       DiskManagerFileInfo old_delegate;
/*      */       
/*      */       List<DiskManagerFileInfoListener> existing_listeners;
/*      */       
/* 2657 */       synchronized (this)
/*      */       {
/* 2659 */         if (new_delegate == this.delegate)
/*      */         {
/* 2661 */           return;
/*      */         }
/*      */         
/* 2664 */         old_delegate = this.delegate;
/*      */         
/* 2666 */         this.delegate = new_delegate;
/*      */         List<DiskManagerFileInfoListener> existing_listeners;
/* 2668 */         if (this.listeners == null)
/*      */         {
/* 2670 */           existing_listeners = null;
/*      */         }
/*      */         else
/*      */         {
/* 2674 */           existing_listeners = new ArrayList(this.listeners);
/*      */         }
/*      */       }
/*      */       
/* 2678 */       if (old_delegate != null)
/*      */       {
/* 2680 */         old_delegate.close();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2685 */       if (existing_listeners != null)
/*      */       {
/* 2687 */         for (int i = 0; i < existing_listeners.size(); i++)
/*      */         {
/* 2689 */           new_delegate.addListener((DiskManagerFileInfoListener)existing_listeners.get(i));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setPriority(int b)
/*      */     {
/* 2698 */       this.delegate.setPriority(b);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setSkipped(boolean b)
/*      */     {
/* 2705 */       this.delegate.setSkipped(b);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean setLink(File link_destination)
/*      */     {
/* 2713 */       return this.delegate.setLink(link_destination);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean setLinkAtomic(File link_destination)
/*      */     {
/* 2720 */       return this.delegate.setLinkAtomic(link_destination);
/*      */     }
/*      */     
/*      */ 
/*      */     public File getLink()
/*      */     {
/* 2726 */       return this.delegate.getLink();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean setStorageType(int type)
/*      */     {
/* 2733 */       return this.delegate.setStorageType(type);
/*      */     }
/*      */     
/*      */ 
/*      */     public int getStorageType()
/*      */     {
/* 2739 */       return this.delegate.getStorageType();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public int getAccessMode()
/*      */     {
/* 2746 */       return this.delegate.getAccessMode();
/*      */     }
/*      */     
/*      */ 
/*      */     public long getDownloaded()
/*      */     {
/* 2752 */       return this.delegate.getDownloaded();
/*      */     }
/*      */     
/*      */ 
/*      */     public String getExtension()
/*      */     {
/* 2758 */       return this.delegate.getExtension();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getFirstPieceNumber()
/*      */     {
/* 2764 */       return this.delegate.getFirstPieceNumber();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getLastPieceNumber()
/*      */     {
/* 2770 */       return this.delegate.getLastPieceNumber();
/*      */     }
/*      */     
/*      */ 
/*      */     public long getLength()
/*      */     {
/* 2776 */       return this.delegate.getLength();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getNbPieces()
/*      */     {
/* 2782 */       return this.delegate.getNbPieces();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getPriority()
/*      */     {
/* 2788 */       return this.delegate.getPriority();
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isSkipped()
/*      */     {
/* 2794 */       return this.delegate.isSkipped();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getIndex()
/*      */     {
/* 2800 */       return this.delegate.getIndex();
/*      */     }
/*      */     
/*      */ 
/*      */     public DiskManager getDiskManager()
/*      */     {
/* 2806 */       return this.delegate.getDiskManager();
/*      */     }
/*      */     
/*      */ 
/*      */     public DownloadManager getDownloadManager()
/*      */     {
/* 2812 */       return DownloadManagerController.this.download_manager;
/*      */     }
/*      */     
/*      */ 
/*      */     public File getFile(boolean follow_link)
/*      */     {
/* 2818 */       return this.delegate.getFile(follow_link);
/*      */     }
/*      */     
/*      */ 
/*      */     public org.gudy.azureus2.core3.torrent.TOTorrentFile getTorrentFile()
/*      */     {
/* 2824 */       return this.delegate.getTorrentFile();
/*      */     }
/*      */     
/*      */ 
/*      */     public void flushCache()
/*      */       throws Exception
/*      */     {
/*      */       try
/*      */       {
/* 2833 */         DownloadManagerController.this.facade_mon.enter();
/*      */         
/* 2835 */         this.delegate.flushCache();
/*      */       }
/*      */       finally
/*      */       {
/* 2839 */         DownloadManagerController.this.facade_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public org.gudy.azureus2.core3.util.DirectByteBuffer read(long offset, int length)
/*      */       throws java.io.IOException
/*      */     {
/*      */       try
/*      */       {
/* 2851 */         DownloadManagerController.this.facade_mon.enter();
/*      */         
/* 2853 */         return this.delegate.read(offset, length);
/*      */       }
/*      */       finally
/*      */       {
/* 2857 */         DownloadManagerController.this.facade_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public int getReadBytesPerSecond()
/*      */     {
/* 2864 */       return this.delegate.getReadBytesPerSecond();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getWriteBytesPerSecond()
/*      */     {
/* 2870 */       return this.delegate.getWriteBytesPerSecond();
/*      */     }
/*      */     
/*      */ 
/*      */     public long getETA()
/*      */     {
/* 2876 */       return this.delegate.getETA();
/*      */     }
/*      */     
/*      */     public void close()
/*      */     {
/*      */       try
/*      */       {
/* 2883 */         DownloadManagerController.this.facade_mon.enter();
/*      */         
/* 2885 */         this.delegate.close();
/*      */       }
/*      */       finally
/*      */       {
/* 2889 */         DownloadManagerController.this.facade_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void addListener(DiskManagerFileInfoListener listener)
/*      */     {
/*      */       DiskManagerFileInfo existing_delegate;
/*      */       
/* 2899 */       synchronized (this)
/*      */       {
/* 2901 */         if (this.listeners == null)
/*      */         {
/* 2903 */           this.listeners = new ArrayList();
/*      */         }
/*      */         
/* 2906 */         this.listeners.add(listener);
/*      */         
/* 2908 */         existing_delegate = this.delegate;
/*      */       }
/*      */       
/* 2911 */       if (existing_delegate != null)
/*      */       {
/* 2913 */         existing_delegate.addListener(listener);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeListener(DiskManagerFileInfoListener listener)
/*      */     {
/*      */       DiskManagerFileInfo existing_delegate;
/*      */       
/* 2923 */       synchronized (this)
/*      */       {
/* 2925 */         this.listeners.remove(listener);
/*      */         
/* 2927 */         existing_delegate = this.delegate;
/*      */       }
/*      */       
/* 2930 */       if (existing_delegate != null)
/*      */       {
/* 2932 */         existing_delegate.removeListener(listener);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void generateEvidence(IndentWriter writer) {
/* 2938 */     writer.println("DownloadManager Controller:");
/*      */     
/* 2940 */     writer.indent();
/*      */     try {
/* 2942 */       writer.println("cached info: complete w/o DND=" + this.cached_complete_excluding_dnd + "; hasDND? " + this.cached_has_dnd_files);
/*      */       
/*      */ 
/* 2945 */       writer.println("Complete w/DND? " + isDownloadComplete(true) + "; w/o DND? " + isDownloadComplete(false));
/*      */       
/*      */ 
/* 2948 */       writer.println("filesFacade length: " + this.fileFacadeSet.nbFiles());
/*      */       
/* 2950 */       if (this.force_start) {
/* 2951 */         writer.println("Force Start");
/*      */       }
/*      */       
/* 2954 */       writer.println("FilesExist? " + filesExist(this.download_manager.isDataAlreadyAllocated()));
/*      */     }
/*      */     finally {
/* 2957 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public class forceRecheckDiskManagerListener
/*      */     implements DiskManagerListener
/*      */   {
/*      */     private final boolean wasForceStarted;
/*      */     
/*      */     private final int start_state;
/*      */     private final ForceRecheckListener l;
/*      */     
/*      */     public forceRecheckDiskManagerListener(boolean wasForceStarted, int start_state, ForceRecheckListener l)
/*      */     {
/* 2972 */       this.wasForceStarted = wasForceStarted;
/* 2973 */       this.start_state = start_state;
/* 2974 */       this.l = l;
/*      */     }
/*      */     
/*      */     public void stateChanged(int oldDMState, int newDMState) {
/*      */       try {
/* 2979 */         DownloadManagerController.this.control_mon.enter();
/*      */         
/* 2981 */         if (DownloadManagerController.this.getDiskManager() == null)
/*      */         {
/*      */ 
/*      */ 
/* 2985 */           DownloadManagerController.this.download_manager.setAssumedComplete(false);
/*      */           
/* 2987 */           if (this.l != null) {
/* 2988 */             this.l.forceRecheckComplete(DownloadManagerController.this.download_manager);
/*      */           }
/*      */           return;
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 2995 */         DownloadManagerController.this.control_mon.exit();
/*      */       }
/*      */       
/*      */ 
/* 2999 */       if (newDMState == 3)
/*      */       {
/* 3001 */         DownloadManagerController.this.fileFacadeSet.makeSureFilesFacadeFilled(true);
/*      */       }
/*      */       
/* 3004 */       if ((newDMState == 4) || (newDMState == 10))
/*      */       {
/* 3006 */         DownloadManagerController.this.force_start = this.wasForceStarted;
/*      */         
/* 3008 */         DownloadManagerController.this.stats.recalcDownloadCompleteBytes();
/*      */         
/* 3010 */         if (newDMState == 4)
/*      */         {
/*      */           try {
/* 3013 */             boolean only_seeding = false;
/* 3014 */             boolean update_only_seeding = false;
/*      */             try
/*      */             {
/* 3017 */               DownloadManagerController.this.control_mon.enter();
/*      */               
/* 3019 */               DiskManager dm = DownloadManagerController.this.getDiskManager();
/*      */               
/* 3021 */               if (dm != null)
/*      */               {
/* 3023 */                 dm.stop(false);
/*      */                 
/* 3025 */                 only_seeding = dm.getRemainingExcludingDND() == 0L;
/*      */                 
/* 3027 */                 update_only_seeding = true;
/*      */                 
/* 3029 */                 DownloadManagerController.this.setDiskManager(null, null);
/*      */                 
/* 3031 */                 if (this.start_state == 100)
/*      */                 {
/* 3033 */                   DownloadManagerController.this.setState(70, false);
/*      */                 }
/*      */                 else
/*      */                 {
/* 3037 */                   DownloadManagerController.this.setState(this.start_state, false);
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally {
/* 3042 */               DownloadManagerController.this.control_mon.exit();
/*      */               
/* 3044 */               DownloadManagerController.this.download_manager.informStateChanged();
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 3050 */             if (update_only_seeding)
/*      */             {
/* 3052 */               DownloadManagerController.this.download_manager.setAssumedComplete(only_seeding);
/*      */             }
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/* 3057 */             DownloadManagerController.this.setFailed("Resume data save fails: " + Debug.getNestedExceptionMessage(e));
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*      */           try {
/* 3063 */             DownloadManagerController.this.control_mon.enter();
/*      */             
/* 3065 */             DiskManager dm = DownloadManagerController.this.getDiskManager();
/*      */             
/* 3067 */             if (dm != null)
/*      */             {
/* 3069 */               dm.stop(false);
/*      */               
/* 3071 */               DownloadManagerController.this.setDiskManager(null, null);
/*      */               
/* 3073 */               DownloadManagerController.this.setFailed(dm);
/*      */             }
/*      */           }
/*      */           finally {
/* 3077 */             DownloadManagerController.this.control_mon.exit();
/*      */           }
/*      */           
/* 3080 */           DownloadManagerController.this.download_manager.setAssumedComplete(false);
/*      */         }
/*      */         
/* 3083 */         if (this.l != null) {
/* 3084 */           this.l.forceRecheckComplete(DownloadManagerController.this.download_manager);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public void filePriorityChanged(DiskManagerFileInfo file) {
/* 3090 */       DownloadManagerController.this.download_manager.informPriorityChange(file);
/*      */     }
/*      */     
/*      */     public void pieceDoneChanged(DiskManagerPiece piece) {}
/*      */     
/*      */     public void fileAccessModeChanged(DiskManagerFileInfo file, int old_mode, int new_mode) {}
/*      */   }
/*      */   
/*      */   private class DiskManagerListener_Default
/*      */     implements DiskManagerListener
/*      */   {
/*      */     private final boolean open_for_seeding;
/*      */     
/*      */     public DiskManagerListener_Default(boolean open_for_seeding)
/*      */     {
/* 3105 */       this.open_for_seeding = open_for_seeding;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void stateChanged(int oldDMState, int newDMState)
/*      */     {
/*      */       DiskManager dm;
/*      */       
/*      */       try
/*      */       {
/* 3116 */         DownloadManagerController.this.control_mon.enter();
/*      */         
/* 3118 */         dm = DownloadManagerController.this.getDiskManager();
/*      */         
/* 3120 */         if (dm == null)
/*      */         {
/*      */           return;
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 3129 */         DownloadManagerController.this.control_mon.exit();
/*      */       }
/*      */       try
/*      */       {
/* 3133 */         if (newDMState == 10)
/*      */         {
/* 3135 */           DownloadManagerController.this.setFailed(dm);
/*      */         }
/*      */         
/* 3138 */         if ((oldDMState == 3) && (newDMState != 3))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 3143 */           DownloadManagerController.FileInfoFacadeSet.access$400(DownloadManagerController.this.fileFacadeSet, true);
/*      */           
/* 3145 */           DownloadManagerController.this.stats.recalcDownloadCompleteBytes();
/*      */           
/* 3147 */           DownloadManagerController.this.download_manager.setAssumedComplete(DownloadManagerController.this.isDownloadComplete(false));
/*      */         }
/*      */         
/* 3150 */         if (newDMState == 4)
/*      */         {
/* 3152 */           int completed = DownloadManagerController.this.stats.getDownloadCompleted(false);
/*      */           
/* 3154 */           if ((DownloadManagerController.this.stats.getTotalDataBytesReceived() == 0L) && (DownloadManagerController.this.stats.getTotalDataBytesSent() == 0L) && (DownloadManagerController.this.stats.getSecondsDownloading() == 0L))
/*      */           {
/*      */ 
/*      */ 
/* 3158 */             if (completed < 1000)
/*      */             {
/* 3160 */               if (this.open_for_seeding)
/*      */               {
/* 3162 */                 DownloadManagerController.this.setFailed("File check failed");
/*      */                 
/* 3164 */                 DownloadManagerController.this.download_manager.getDownloadState().clearResumeData();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3176 */                 long amount_downloaded = completed * dm.getTotalLength() / 1000L;
/*      */                 
/* 3178 */                 DownloadManagerController.this.stats.setSavedDownloadedUploaded(amount_downloaded, amount_downloaded);
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/* 3183 */               int dl_copies = COConfigurationManager.getIntParameter("StartStopManager_iAddForSeedingDLCopyCount");
/*      */               
/* 3185 */               if (dl_copies > 0)
/*      */               {
/* 3187 */                 DownloadManagerController.this.stats.setSavedDownloadedUploaded(DownloadManagerController.this.download_manager.getSize() * dl_copies, DownloadManagerController.this.stats.getTotalDataBytesSent());
/*      */               }
/*      */               
/* 3190 */               DownloadManagerController.this.download_manager.getDownloadState().setFlag(1L, true);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3199 */           if (completed == 1000) {
/* 3200 */             DownloadManagerController.this.download_manager.getDownloadState().discardFluff();
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/* 3205 */         DownloadManagerController.this.download_manager.informStateChanged();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void filePriorityChanged(DiskManagerFileInfo file)
/*      */     {
/* 3213 */       DownloadManagerController.this.download_manager.informPriorityChange(file);
/*      */     }
/*      */     
/*      */     public void pieceDoneChanged(DiskManagerPiece piece) {}
/*      */     
/*      */     public void fileAccessModeChanged(DiskManagerFileInfo file, int old_mode, int new_mode) {}
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/impl/DownloadManagerController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */