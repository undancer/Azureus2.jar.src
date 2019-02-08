/*      */ package org.gudy.azureus2.core3.global.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.helpers.TorrentFolderWatcher;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import com.aelitis.azureus.core.tag.TaggableLifecycleHandler;
/*      */ import com.aelitis.azureus.core.tag.impl.TagDownloadWithState;
/*      */ import com.aelitis.azureus.core.tag.impl.TagTypeWithState;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.category.CategoryManager;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerFactory;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerInitialisationAdapter;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateFactory;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerDownloadRemovalVetoException;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerDownloadWillBeRemovedListener;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerEvent;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerEventListener;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*      */ import org.gudy.azureus2.core3.global.GlobalMangerProgressListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraper;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperClientResolver;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperListener;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.ListenerManager;
/*      */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider;
/*      */ 
/*      */ public class GlobalManagerImpl extends org.gudy.azureus2.core3.download.impl.DownloadManagerAdapter implements GlobalManager, org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator
/*      */ {
/*   90 */   private static final LogIDs LOGID = LogIDs.CORE;
/*      */   
/*      */   private static final int LDT_MANAGER_ADDED = 1;
/*      */   
/*      */   private static final int LDT_MANAGER_REMOVED = 2;
/*      */   
/*      */   private static final int LDT_DESTROY_INITIATED = 3;
/*      */   
/*      */   private static final int LDT_DESTROYED = 4;
/*      */   
/*      */   private static final int LDT_SEEDING_ONLY = 5;
/*      */   
/*      */   private static final int LDT_EVENT = 6;
/*      */   
/*  104 */   private final ListenerManager listeners_and_event_listeners = ListenerManager.createAsyncManager("GM:ListenDispatcher", new ListenerManagerDispatcher()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatch(Object _listener, int type, Object value)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  114 */       if (type == 6)
/*      */       {
/*  116 */         if ((_listener instanceof GlobalManagerEventListener))
/*      */         {
/*  118 */           ((GlobalManagerEventListener)_listener).eventOccurred((GlobalManagerEvent)value);
/*      */         }
/*      */         
/*      */       }
/*  122 */       else if ((_listener instanceof GlobalManagerListener))
/*      */       {
/*  124 */         GlobalManagerListener target = (GlobalManagerListener)_listener;
/*      */         
/*  126 */         if (type == 1)
/*      */         {
/*  128 */           target.downloadManagerAdded((DownloadManager)value);
/*      */         }
/*  130 */         else if (type == 2)
/*      */         {
/*  132 */           target.downloadManagerRemoved((DownloadManager)value);
/*      */         }
/*  134 */         else if (type == 3)
/*      */         {
/*  136 */           target.destroyInitiated();
/*      */         }
/*  138 */         else if (type == 4)
/*      */         {
/*  140 */           target.destroyed();
/*      */         }
/*  142 */         else if (type == 5)
/*      */         {
/*  144 */           boolean[] temp = (boolean[])value;
/*      */           
/*  146 */           target.seedingStatusChanged(temp[0], temp[1]);
/*      */         }
/*      */       }
/*      */     }
/*  104 */   });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int LDT_MANAGER_WBR = 1;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  158 */   private final ListenerManager removal_listeners = ListenerManager.createManager("GM:DLWBRMListenDispatcher", new org.gudy.azureus2.core3.util.ListenerManagerDispatcherWithException()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatchWithException(Object _listener, int type, Object value)
/*      */       throws GlobalManagerDownloadRemovalVetoException
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  170 */       GlobalManagerDownloadWillBeRemovedListener target = (GlobalManagerDownloadWillBeRemovedListener)_listener;
/*      */       
/*  172 */       DownloadManager dm = (DownloadManager)((Object[])(Object[])value)[0];
/*  173 */       boolean remove_torrent = ((Boolean)((Object[])(Object[])value)[1]).booleanValue();
/*  174 */       boolean remove_data = ((Boolean)((Object[])(Object[])value)[2]).booleanValue();
/*      */       
/*  176 */       target.downloadWillBeRemoved(dm, remove_torrent, remove_data);
/*      */     }
/*  158 */   });
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean enable_stopped_scrapes;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean enable_no_space_dl_restarts;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int no_space_dl_restart_check_period_millis;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static
/*      */   {
/*  186 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Tracker Client Scrape Stopped Enable", "Insufficient Space Download Restart Enable", "Insufficient Space Download Restart Period" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*  194 */         GlobalManagerImpl.access$002(COConfigurationManager.getBooleanParameter("Tracker Client Scrape Stopped Enable"));
/*      */         
/*  196 */         GlobalManagerImpl.access$102(COConfigurationManager.getBooleanParameter("Insufficient Space Download Restart Enable"));
/*      */         
/*  198 */         if (GlobalManagerImpl.enable_no_space_dl_restarts)
/*      */         {
/*  200 */           int mins = COConfigurationManager.getIntParameter("Insufficient Space Download Restart Period");
/*      */           
/*  202 */           GlobalManagerImpl.access$202(Math.max(1, mins) * 60 * 1000);
/*      */         }
/*      */         else
/*      */         {
/*  206 */           GlobalManagerImpl.access$202(0);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*  213 */   private volatile List<DownloadManager> managers_cow = new ArrayList();
/*  214 */   private final AEMonitor managers_mon = new AEMonitor("GM:Managers");
/*      */   
/*  216 */   final Map manager_map = new HashMap();
/*      */   
/*      */   private final GlobalMangerProgressListener progress_listener;
/*      */   
/*      */   private long lastListenerUpdate;
/*      */   private final Checker checker;
/*      */   private final GlobalManagerStatsImpl stats;
/*  223 */   private long last_swarm_stats_calc_time = 0L;
/*  224 */   private long last_swarm_stats = 0L;
/*      */   
/*      */ 
/*      */   private final boolean cripple_downloads_config;
/*      */   
/*      */   private final TRTrackerScraper trackerScraper;
/*      */   
/*      */   private GlobalManagerStatsWriter stats_writer;
/*      */   
/*      */   private GlobalManagerHostSupport host_support;
/*      */   
/*      */   private Object download_history_manager;
/*      */   
/*  237 */   private final Map<HashWrapper, Map> saved_download_manager_state = new HashMap();
/*      */   
/*      */ 
/*      */   private int next_seed_piece_recheck_index;
/*      */   
/*      */   private final TorrentFolderWatcher torrent_folder_watcher;
/*      */   
/*  244 */   private final ArrayList<Object[]> paused_list = new ArrayList();
/*      */   
/*  246 */   private final AEMonitor paused_list_mon = new AEMonitor("GlobalManager:PL");
/*      */   
/*      */   private final GlobalManagerFileMerger file_merger;
/*      */   
/*      */   private volatile boolean isStopping;
/*      */   
/*      */   private volatile boolean destroyed;
/*      */   
/*  254 */   private volatile boolean needsSaving = false;
/*      */   
/*      */   private volatile long needsSavingCozStateChanged;
/*  257 */   private boolean seeding_only_mode = false;
/*  258 */   private boolean potentially_seeding_only_mode = false;
/*      */   
/*  260 */   private final FrequencyLimitedDispatcher check_seeding_only_state_dispatcher = new FrequencyLimitedDispatcher(new AERunnable() {
/*      */     public void runSupport() {
/*  262 */       GlobalManagerImpl.this.checkSeedingOnlyStateSupport();
/*      */     }
/*  260 */   }, 5000);
/*      */   
/*      */ 
/*      */   private boolean force_start_non_seed_exists;
/*      */   
/*  265 */   private int nat_status = 0;
/*  266 */   private long nat_status_last_good = -1L;
/*      */   
/*      */   private boolean nat_status_probably_ok;
/*  269 */   private final CopyOnWriteList dm_adapters = new CopyOnWriteList();
/*      */   
/*      */ 
/*  272 */   DelayedEvent loadTorrentsDelay = null;
/*      */   
/*  274 */   boolean loadingComplete = false;
/*      */   
/*  276 */   final AESemaphore loadingSem = new AESemaphore("Loading Torrents");
/*      */   
/*  278 */   final AEMonitor addingDM_monitor = new AEMonitor("addingDM");
/*      */   
/*  280 */   final List addingDMs = new ArrayList();
/*      */   
/*  282 */   private MainlineDHTProvider provider = null;
/*      */   
/*      */ 
/*      */ 
/*      */   private TimerEvent auto_resume_timer;
/*      */   
/*      */ 
/*      */ 
/*  290 */   private boolean auto_resume_disabled = (COConfigurationManager.getBooleanParameter("Pause Downloads On Exit")) && (!COConfigurationManager.getBooleanParameter("Resume Downloads On Start"));
/*      */   
/*      */ 
/*      */ 
/*  294 */   private final TaggableLifecycleHandler taggable_life_manager = TagManagerFactory.getTagManager().registerTaggableResolver(this);
/*      */   
/*      */ 
/*      */   public class Checker
/*      */     extends AEThread
/*      */   {
/*      */     int loopFactor;
/*      */     
/*      */     private static final int waitTime = 10000;
/*  303 */     private int saveResumeLoopCount = 30;
/*      */     
/*      */     private static final int initSaveResumeLoopCount = 6;
/*      */     private static final int natCheckLoopCount = 3;
/*      */     private static final int seedPieceCheckCount = 3;
/*      */     private static final int oneMinuteThingCount = 6;
/*  309 */     private final AESemaphore run_sem = new AESemaphore("GM:Checker:run");
/*      */     
/*      */     public Checker() {
/*  312 */       super();
/*  313 */       this.loopFactor = 0;
/*  314 */       setPriority(1);
/*      */     }
/*      */     
/*      */     private void determineSaveResumeDataInterval()
/*      */     {
/*  319 */       int saveResumeInterval = COConfigurationManager.getIntParameter("Save Resume Interval", 5);
/*  320 */       if ((saveResumeInterval >= 1) && (saveResumeInterval <= 90)) {
/*  321 */         this.saveResumeLoopCount = (saveResumeInterval * 60000 / 10000);
/*      */       }
/*      */     }
/*      */     
/*      */     public void runSupport()
/*      */     {
/*      */       for (;;)
/*      */       {
/*      */         try {
/*  330 */           this.loopFactor += 1;
/*      */           
/*  332 */           determineSaveResumeDataInterval();
/*      */           
/*  334 */           if (this.loopFactor % this.saveResumeLoopCount == 0)
/*      */           {
/*  336 */             GlobalManagerImpl.this.saveDownloads(true);
/*      */           }
/*  338 */           else if ((GlobalManagerImpl.this.loadingComplete) && (this.loopFactor > 6))
/*      */           {
/*  340 */             if (GlobalManagerImpl.this.needsSavingCozStateChanged > 0L)
/*      */             {
/*  342 */               int num_downloads = GlobalManagerImpl.this.managers_cow.size();
/*      */               
/*  344 */               boolean do_save = false;
/*      */               
/*  346 */               if (num_downloads < 10)
/*      */               {
/*  348 */                 do_save = true;
/*      */ 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*  355 */                 long now = SystemTime.getMonotonousTime();
/*      */                 
/*  357 */                 long elapsed_secs = (now - GlobalManagerImpl.this.needsSavingCozStateChanged) / 1000L;
/*      */                 
/*  359 */                 do_save = elapsed_secs > num_downloads;
/*      */               }
/*      */               
/*  362 */               if (do_save)
/*      */               {
/*  364 */                 GlobalManagerImpl.this.saveDownloads(true);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  369 */           if (this.loopFactor % 3 == 0)
/*      */           {
/*  371 */             GlobalManagerImpl.this.computeNATStatus();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  377 */             GlobalManagerImpl.this.checkSeedingOnlyState();
/*      */             
/*      */ 
/*      */ 
/*  381 */             GlobalManagerImpl.this.checkForceStart(false);
/*      */           }
/*      */           
/*  384 */           if (this.loopFactor % 3 == 0)
/*      */           {
/*  386 */             GlobalManagerImpl.this.seedPieceRecheck();
/*      */           }
/*      */           
/*  389 */           if (this.loopFactor % this.saveResumeLoopCount == 0)
/*      */           {
/*  391 */             Iterator it = GlobalManagerImpl.this.managers_cow.iterator(); if (it.hasNext())
/*      */             {
/*  393 */               DownloadManager manager = (DownloadManager)it.next();
/*      */               
/*      */ 
/*  396 */               manager.saveResumeData();
/*  397 */               continue;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  404 */           if (GlobalManagerImpl.no_space_dl_restart_check_period_millis > 0)
/*      */           {
/*  406 */             int lc = GlobalManagerImpl.no_space_dl_restart_check_period_millis / 10000;
/*      */             
/*  408 */             if (this.loopFactor % lc == 0)
/*      */             {
/*  410 */               List<DownloadManager> eligible = new ArrayList();
/*      */               
/*  412 */               Iterator<DownloadManager> it = GlobalManagerImpl.this.managers_cow.iterator(); if (it.hasNext())
/*      */               {
/*  414 */                 DownloadManager manager = (DownloadManager)it.next();
/*      */                 
/*  416 */                 if ((manager.getState() == 100) && (!manager.isDownloadComplete(false)) && (!manager.isPaused()) && (manager.getErrorType() == 2))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*  421 */                   eligible.add(manager);
/*      */                 }
/*  423 */                 continue;
/*      */               }
/*  425 */               if (!eligible.isEmpty())
/*      */               {
/*  427 */                 if (eligible.size() > 1)
/*      */                 {
/*  429 */                   Collections.sort(eligible, new Comparator()
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */                     public int compare(DownloadManager o1, DownloadManager o2)
/*      */                     {
/*      */ 
/*      */ 
/*  438 */                       return o1.getPosition() - o2.getPosition();
/*      */                     }
/*      */                   });
/*      */                 }
/*      */                 
/*  443 */                 DownloadManager manager = (DownloadManager)eligible.get(0);
/*      */                 
/*  445 */                 Logger.log(new LogEvent(GlobalManagerImpl.LOGID, "Restarting download '" + manager.getDisplayName() + "' to check if disk space now available"));
/*      */                 
/*  447 */                 manager.setStateQueued();
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  453 */           if (this.loopFactor % 6 == 0) {
/*      */             try
/*      */             {
/*  456 */               if (!HttpURLConnection.getFollowRedirects())
/*      */               {
/*  458 */                 Debug.outNoStack("Something has set global 'follow redirects' to false!!!!");
/*      */                 
/*  460 */                 HttpURLConnection.setFollowRedirects(true);
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/*  464 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  469 */           Debug.printStackTrace(e);
/*      */         }
/*      */         try
/*      */         {
/*  473 */           this.run_sem.reserve(10000L);
/*      */           
/*  475 */           if (this.run_sem.isReleasedForever()) {
/*      */             break;
/*      */           }
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*  481 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public void stopIt() {
/*  487 */       this.run_sem.releaseForever();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public GlobalManagerImpl(AzureusCore core, GlobalMangerProgressListener listener, long existingTorrentLoadDelay)
/*      */   {
/*  498 */     this.progress_listener = listener;
/*      */     
/*  500 */     this.cripple_downloads_config = "1".equals(System.getProperty("azureus.disabledownloads"));
/*      */     
/*  502 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  504 */     this.stats = new GlobalManagerStatsImpl(this);
/*      */     try
/*      */     {
/*  507 */       this.stats_writer = new GlobalManagerStatsWriter(core, this.stats);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  511 */       Logger.log(new LogEvent(LOGID, "Stats unavailable", e));
/*      */     }
/*      */     try
/*      */     {
/*      */       try {
/*  516 */         Class<?> impl_class = GlobalManagerImpl.class.getClassLoader().loadClass("org.gudy.azureus2.core3.history.impl.DownloadHistoryManagerImpl");
/*      */         
/*  518 */         this.download_history_manager = impl_class.newInstance();
/*      */ 
/*      */       }
/*      */       catch (ClassNotFoundException e) {}
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  525 */       Logger.log(new LogEvent(LOGID, "Download History unavailable", e));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  531 */     if (existingTorrentLoadDelay > 0L) {
/*  532 */       this.loadTorrentsDelay = new DelayedEvent("GM:tld", existingTorrentLoadDelay, new AERunnable()
/*      */       {
/*      */         public void runSupport() {
/*  535 */           GlobalManagerImpl.this.loadExistingTorrentsNow(false);
/*      */         }
/*      */         
/*      */       });
/*      */     } else {
/*  540 */       loadDownloads();
/*      */     }
/*      */     
/*  543 */     if (this.progress_listener != null) {
/*  544 */       this.progress_listener.reportCurrentTask(MessageText.getString("splash.initializeGM"));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  549 */     this.trackerScraper = org.gudy.azureus2.core3.tracker.client.TRTrackerScraperFactory.getSingleton();
/*      */     
/*  551 */     this.trackerScraper.setClientResolver(new TRTrackerScraperClientResolver()
/*      */     {
/*      */ 
/*      */ 
/*      */       public boolean isScrapable(HashWrapper torrent_hash)
/*      */       {
/*      */ 
/*  558 */         DownloadManager dm = GlobalManagerImpl.this.getDownloadManager(torrent_hash);
/*      */         
/*  560 */         if (dm == null)
/*      */         {
/*  562 */           return false;
/*      */         }
/*      */         
/*      */ 
/*  566 */         int dm_state = dm.getState();
/*      */         
/*  568 */         if (dm_state == 75)
/*      */         {
/*  570 */           return true;
/*      */         }
/*  572 */         if ((dm_state == 50) || (dm_state == 60))
/*      */         {
/*      */ 
/*  575 */           return true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  580 */         if (!GlobalManagerImpl.enable_stopped_scrapes)
/*      */         {
/*  582 */           return false;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  590 */         DownloadManagerStats stats = dm.getStats();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  595 */         if ((stats.getTotalDataBytesReceived() == 0L) && (stats.getPercentDoneExcludingDND() == 0))
/*      */         {
/*  597 */           return false;
/*      */         }
/*      */         
/*  600 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean isNetworkEnabled(HashWrapper hash, URL url)
/*      */       {
/*  608 */         DownloadManager dm = GlobalManagerImpl.this.getDownloadManager(hash);
/*      */         
/*  610 */         if (dm == null)
/*      */         {
/*  612 */           return false;
/*      */         }
/*      */         
/*  615 */         String nw = AENetworkClassifier.categoriseAddress(url.getHost());
/*      */         
/*  617 */         String[] networks = dm.getDownloadState().getNetworks();
/*      */         
/*  619 */         for (int i = 0; i < networks.length; i++)
/*      */         {
/*  621 */           if (networks[i] == nw)
/*      */           {
/*  623 */             return true;
/*      */           }
/*      */         }
/*      */         
/*  627 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public String[] getEnabledNetworks(HashWrapper hash)
/*      */       {
/*  634 */         DownloadManager dm = GlobalManagerImpl.this.getDownloadManager(hash);
/*      */         
/*  636 */         if (dm == null)
/*      */         {
/*  638 */           return null;
/*      */         }
/*      */         
/*  641 */         return dm.getDownloadState().getNetworks();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public int[] getCachedScrape(HashWrapper hash)
/*      */       {
/*  648 */         DownloadManager dm = GlobalManagerImpl.this.getDownloadManager(hash);
/*      */         
/*  650 */         if (dm == null)
/*      */         {
/*  652 */           return null;
/*      */         }
/*      */         
/*  655 */         long cache = dm.getDownloadState().getLongAttribute("scrapecache");
/*      */         
/*  657 */         if (cache == -1L)
/*      */         {
/*  659 */           return null;
/*      */         }
/*      */         
/*      */ 
/*  663 */         int cache_src = dm.getDownloadState().getIntAttribute("scsrc");
/*      */         
/*  665 */         if (cache_src == 0)
/*      */         {
/*  667 */           int seeds = (int)(cache >> 32 & 0xFFFFFF);
/*  668 */           int leechers = (int)(cache & 0xFFFFFF);
/*      */           
/*  670 */           return new int[] { seeds, leechers };
/*      */         }
/*      */         
/*      */ 
/*  674 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public Object[] getExtensions(HashWrapper hash)
/*      */       {
/*  683 */         DownloadManager dm = GlobalManagerImpl.this.getDownloadManager(hash);
/*      */         
/*      */         Character state;
/*      */         String ext;
/*      */         Character state;
/*  688 */         if (dm == null)
/*      */         {
/*  690 */           String ext = "";
/*  691 */           state = TRTrackerScraperClientResolver.FL_NONE;
/*      */         }
/*      */         else
/*      */         {
/*  695 */           ext = dm.getDownloadState().getTrackerClientExtensions();
/*      */           
/*  697 */           if (ext == null)
/*      */           {
/*  699 */             ext = "";
/*      */           }
/*      */           
/*  702 */           boolean comp = dm.isDownloadComplete(false);
/*      */           
/*  704 */           int dm_state = dm.getState();
/*      */           
/*      */ 
/*      */           Character state;
/*      */           
/*  709 */           if ((dm_state == 100) || (dm_state == 70) || ((dm_state == 65) && (dm.getSubState() != 75)))
/*      */           {
/*      */ 
/*      */ 
/*  713 */             state = comp ? TRTrackerScraperClientResolver.FL_COMPLETE_STOPPED : TRTrackerScraperClientResolver.FL_INCOMPLETE_STOPPED;
/*      */           } else { Character state;
/*  715 */             if ((dm_state == 50) || (dm_state == 60))
/*      */             {
/*      */ 
/*  718 */               state = comp ? TRTrackerScraperClientResolver.FL_COMPLETE_RUNNING : TRTrackerScraperClientResolver.FL_INCOMPLETE_RUNNING;
/*      */             }
/*      */             else
/*      */             {
/*  722 */               state = comp ? TRTrackerScraperClientResolver.FL_COMPLETE_QUEUED : TRTrackerScraperClientResolver.FL_INCOMPLETE_QUEUED;
/*      */             }
/*      */           }
/*      */         }
/*  726 */         return new Object[] { ext, state };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean redirectTrackerUrl(HashWrapper hash, URL old_url, URL new_url)
/*      */       {
/*  735 */         DownloadManager dm = GlobalManagerImpl.this.getDownloadManager(hash);
/*      */         
/*  737 */         if ((dm == null) || (dm.getTorrent() == null))
/*      */         {
/*  739 */           return false;
/*      */         }
/*      */         
/*  742 */         return TorrentUtils.replaceAnnounceURL(dm.getTorrent(), old_url, new_url);
/*      */       }
/*      */       
/*  745 */     });
/*  746 */     this.trackerScraper.addListener(new TRTrackerScraperListener()
/*      */     {
/*      */       public void scrapeReceived(TRTrackerScraperResponse response) {
/*  749 */         HashWrapper hash = response.getHash();
/*      */         
/*  751 */         DownloadManager manager = (DownloadManager)GlobalManagerImpl.this.manager_map.get(hash);
/*  752 */         if (manager != null) {
/*  753 */           manager.setTrackerScrapeResponse(response);
/*      */         }
/*      */       }
/*      */     });
/*      */     try
/*      */     {
/*  759 */       this.host_support = new GlobalManagerHostSupport(this);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  763 */       Logger.log(new LogEvent(LOGID, "Hosting unavailable", e));
/*      */     }
/*      */     
/*  766 */     this.checker = new Checker();
/*      */     
/*  768 */     this.checker.start();
/*      */     
/*  770 */     this.torrent_folder_watcher = new TorrentFolderWatcher(this);
/*      */     
/*  772 */     this.torrent_folder_watcher.start();
/*      */     
/*  774 */     org.gudy.azureus2.core3.tracker.util.TRTrackerUtils.addListener(new org.gudy.azureus2.core3.tracker.util.TRTrackerUtilsListener()
/*      */     {
/*      */ 
/*      */       public void announceDetailsChanged()
/*      */       {
/*      */ 
/*  780 */         Logger.log(new LogEvent(GlobalManagerImpl.LOGID, "Announce details have changed, updating trackers"));
/*      */         
/*  782 */         List managers = GlobalManagerImpl.this.managers_cow;
/*      */         
/*  784 */         for (int i = 0; i < managers.size(); i++)
/*      */         {
/*  786 */           DownloadManager manager = (DownloadManager)managers.get(i);
/*      */           
/*  788 */           manager.requestTrackerAnnounce(true);
/*      */         }
/*      */         
/*      */       }
/*  792 */     });
/*  793 */     TorrentUtils.addTorrentURLChangeListener(new org.gudy.azureus2.core3.util.TorrentUtils.TorrentAnnounceURLChangeListener()
/*      */     {
/*      */ 
/*      */       public void changed()
/*      */       {
/*      */ 
/*  799 */         Logger.log(new LogEvent(GlobalManagerImpl.LOGID, "Announce URL details have changed, updating trackers"));
/*      */         
/*  801 */         List managers = GlobalManagerImpl.this.managers_cow;
/*      */         
/*  803 */         for (int i = 0; i < managers.size(); i++)
/*      */         {
/*  805 */           DownloadManager manager = (DownloadManager)managers.get(i);
/*      */           
/*  807 */           TRTrackerAnnouncer client = manager.getTrackerClient();
/*      */           
/*  809 */           if (client != null)
/*      */           {
/*  811 */             client.resetTrackerUrl(false);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*  817 */     if (TagManagerFactory.getTagManager().isEnabled())
/*      */     {
/*  819 */       new DownloadStateTagger(this, null);
/*      */     }
/*      */     
/*  822 */     this.file_merger = new GlobalManagerFileMerger(this);
/*      */   }
/*      */   
/*      */   public void loadExistingTorrentsNow(boolean async)
/*      */   {
/*  827 */     if (this.loadTorrentsDelay == null) {
/*  828 */       return;
/*      */     }
/*  830 */     this.loadTorrentsDelay = null;
/*      */     
/*      */ 
/*  833 */     if (async) {
/*  834 */       AEThread thread = new AEThread("load torrents", true) {
/*      */         public void runSupport() {
/*  836 */           GlobalManagerImpl.this.loadDownloads();
/*      */         }
/*  838 */       };
/*  839 */       thread.setPriority(3);
/*  840 */       thread.start();
/*      */     } else {
/*  842 */       loadDownloads();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DownloadManager addDownloadManager(String fileName, String savePath)
/*      */   {
/*  852 */     return addDownloadManager(fileName, null, savePath, 0, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DownloadManager addDownloadManager(String fileName, byte[] optionalHash, String savePath, int initialState, boolean persistent)
/*      */   {
/*  863 */     return addDownloadManager(fileName, optionalHash, savePath, initialState, persistent, false, null);
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
/*      */   public DownloadManager addDownloadManager(String torrent_file_name, byte[] optionalHash, String savePath, int initialState, boolean persistent, boolean for_seeding, DownloadManagerInitialisationAdapter _adapter)
/*      */   {
/*  877 */     return addDownloadManager(torrent_file_name, optionalHash, savePath, null, initialState, persistent, for_seeding, _adapter);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DownloadManager addDownloadManager(String torrent_file_name, byte[] optionalHash, String savePath, String saveFile, int initialState, boolean persistent, boolean for_seeding, DownloadManagerInitialisationAdapter _adapter)
/*      */   {
/*  898 */     boolean needsFixup = false;
/*      */     
/*      */ 
/*      */ 
/*  902 */     this.loadingSem.reserve(60000L);
/*      */     
/*  904 */     DownloadManagerInitialisationAdapter adapter = getDMAdapter(_adapter);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  909 */     List file_priorities = null;
/*      */     
/*  911 */     if (!persistent)
/*      */     {
/*  913 */       Map save_download_state = (Map)this.saved_download_manager_state.get(new HashWrapper(optionalHash));
/*      */       
/*      */ 
/*  916 */       if (save_download_state != null)
/*      */       {
/*  918 */         if (save_download_state.containsKey("state"))
/*      */         {
/*  920 */           int saved_state = ((Long)save_download_state.get("state")).intValue();
/*      */           
/*  922 */           if (saved_state == 70)
/*      */           {
/*  924 */             initialState = saved_state;
/*      */           }
/*      */         }
/*      */         
/*  928 */         file_priorities = (List)save_download_state.get("file_priorities");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  934 */         Long lPosition = (Long)save_download_state.get("position");
/*  935 */         if ((lPosition != null) && 
/*  936 */           (lPosition.longValue() != -1L)) {
/*  937 */           needsFixup = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  943 */     File torrentDir = null;
/*  944 */     File fDest = null;
/*  945 */     HashWrapper hash = null;
/*  946 */     boolean deleteDest = false;
/*  947 */     boolean removeFromAddingDM = false;
/*      */     DownloadManager manager;
/*      */     try {
/*  950 */       File f = new File(torrent_file_name);
/*      */       
/*  952 */       if (!f.exists()) {
/*  953 */         throw new IOException("Torrent file '" + torrent_file_name + "' doesn't exist");
/*      */       }
/*      */       
/*      */ 
/*  957 */       if (!f.isFile()) {
/*  958 */         throw new IOException("Torrent '" + torrent_file_name + "' is not a file");
/*      */       }
/*      */       
/*      */ 
/*  962 */       fDest = TorrentUtils.copyTorrentFileToSaveDir(f, persistent);
/*      */       
/*  964 */       String fName = fDest.getCanonicalPath();
/*      */       
/*      */ 
/*      */       try
/*      */       {
/*  969 */         if (optionalHash != null) {
/*  970 */           hash = new HashWrapper(optionalHash);
/*      */         }
/*      */         else {
/*  973 */           TOTorrent torrent = TorrentUtils.readFromFile(fDest, false);
/*  974 */           hash = torrent.getHashWrapper();
/*      */         }
/*      */         
/*  977 */         if (hash != null) {
/*  978 */           removeFromAddingDM = true;
/*      */           
/*      */ 
/*  981 */           DownloadManager existingDM = getDownloadManager(hash);
/*  982 */           DownloadManager localDownloadManager1; if (existingDM != null) {
/*  983 */             deleteDest = true;
/*  984 */             localDownloadManager1 = existingDM;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1060 */             if (deleteDest) {
/* 1061 */               fDest.delete();
/*      */               try
/*      */               {
/* 1064 */                 File backupFile = new File(fDest.getCanonicalPath() + ".bak");
/* 1065 */                 if (backupFile.exists()) {
/* 1066 */                   backupFile.delete();
/*      */                 }
/*      */               }
/*      */               catch (IOException e) {}
/*      */             }
/* 1071 */             if ((removeFromAddingDM) && (hash != null))
/*      */               try {
/* 1073 */                 this.addingDM_monitor.enter();
/*      */                 
/* 1075 */                 this.addingDMs.remove(hash);
/*      */               } finally {
/* 1077 */                 this.addingDM_monitor.exit(); } return localDownloadManager1;
/*      */           }
/*      */           try
/*      */           {
/*  989 */             this.addingDM_monitor.enter();
/*      */             
/*  991 */             if (this.addingDMs.contains(hash)) {
/*  992 */               removeFromAddingDM = false;
/*  993 */               deleteDest = true;
/*  994 */               localDownloadManager1 = null;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  999 */               this.addingDM_monitor.exit();
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
/* 1060 */               if (deleteDest) {
/* 1061 */                 fDest.delete();
/*      */                 try
/*      */                 {
/* 1064 */                   File backupFile = new File(fDest.getCanonicalPath() + ".bak");
/* 1065 */                   if (backupFile.exists()) {
/* 1066 */                     backupFile.delete();
/*      */                   }
/*      */                 }
/*      */                 catch (IOException e) {}
/*      */               }
/* 1071 */               if ((removeFromAddingDM) && (hash != null))
/*      */                 try {
/* 1073 */                   this.addingDM_monitor.enter();
/*      */                   
/* 1075 */                   this.addingDMs.remove(hash);
/*      */                 } finally {}
/* 1077 */               return localDownloadManager1;
/*      */             }
/*  997 */             this.addingDMs.add(hash);
/*      */           } finally {
/*  999 */             this.addingDM_monitor.exit();
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Exception e) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1011 */       DownloadManager new_manager = DownloadManagerFactory.create(this, optionalHash, fName, savePath, saveFile, initialState, persistent, for_seeding, file_priorities, adapter);
/*      */       
/*      */ 
/*      */ 
/* 1015 */       manager = addDownloadManager(new_manager, true, true);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1020 */       if ((manager == null) || (manager != new_manager)) {
/* 1021 */         deleteDest = true;
/*      */       }
/*      */       else
/*      */       {
/* 1025 */         if (initialState == 70)
/*      */         {
/* 1027 */           if (COConfigurationManager.getBooleanParameter("Default Start Torrents Stopped Auto Pause")) {
/*      */             try
/*      */             {
/* 1030 */               this.paused_list_mon.enter();
/*      */               
/* 1032 */               this.paused_list.add(new Object[] { manager.getTorrent().getHashWrapper(), Boolean.valueOf(false) });
/*      */             }
/*      */             finally
/*      */             {
/* 1036 */               this.paused_list_mon.exit();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1041 */         if (TorrentUtils.shouldDeleteTorrentFileAfterAdd(fDest, persistent))
/* 1042 */           deleteDest = true;
/*      */       }
/*      */     } catch (IOException e) {
/*      */       File backupFile;
/* 1046 */       System.out.println("DownloadManager::addDownloadManager: fails - td = " + torrentDir + ", fd = " + fDest);
/*      */       
/* 1048 */       Debug.printStackTrace(e);
/* 1049 */       manager = DownloadManagerFactory.create(this, optionalHash, torrent_file_name, savePath, saveFile, initialState, persistent, for_seeding, file_priorities, adapter);
/*      */       
/*      */ 
/* 1052 */       manager = addDownloadManager(manager, true, true);
/*      */     } catch (Exception e) {
/*      */       File backupFile;
/* 1055 */       manager = DownloadManagerFactory.create(this, optionalHash, torrent_file_name, savePath, saveFile, initialState, persistent, for_seeding, file_priorities, adapter);
/*      */       
/*      */ 
/* 1058 */       manager = addDownloadManager(manager, true, true);
/*      */     } finally { File backupFile;
/* 1060 */       if (deleteDest) {
/* 1061 */         fDest.delete();
/*      */         try
/*      */         {
/* 1064 */           File backupFile = new File(fDest.getCanonicalPath() + ".bak");
/* 1065 */           if (backupFile.exists()) {
/* 1066 */             backupFile.delete();
/*      */           }
/*      */         }
/*      */         catch (IOException e) {}
/*      */       }
/* 1071 */       if ((removeFromAddingDM) && (hash != null)) {
/*      */         try {
/* 1073 */           this.addingDM_monitor.enter();
/*      */           
/* 1075 */           this.addingDMs.remove(hash);
/*      */         } finally {
/* 1077 */           this.addingDM_monitor.exit();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1082 */     if ((needsFixup) && (manager != null) && 
/* 1083 */       (manager.getPosition() <= downloadManagerCount(manager.isDownloadComplete(false)))) {
/* 1084 */       fixUpDownloadManagerPositions();
/*      */     }
/*      */     
/*      */ 
/* 1088 */     return manager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void clearNonPersistentDownloadState(byte[] hash)
/*      */   {
/* 1095 */     this.saved_download_manager_state.remove(new HashWrapper(hash));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DownloadManager addDownloadManager(DownloadManager download_manager, boolean save, boolean notifyListeners)
/*      */   {
/* 1104 */     if (!this.isStopping)
/*      */     {
/* 1106 */       loadExistingTorrentsNow(false);
/*      */       try
/*      */       {
/* 1109 */         this.managers_mon.enter();
/*      */         
/* 1111 */         int existing_index = this.managers_cow.indexOf(download_manager);
/*      */         
/* 1113 */         if (existing_index != -1)
/*      */         {
/* 1115 */           DownloadManager existing = (DownloadManager)this.managers_cow.get(existing_index);
/*      */           
/* 1117 */           download_manager.destroy(true);
/*      */           
/* 1119 */           return existing;
/*      */         }
/*      */         
/* 1122 */         DownloadManagerStats dm_stats = download_manager.getStats();
/*      */         
/* 1124 */         HashWrapper hashwrapper = null;
/*      */         try
/*      */         {
/* 1127 */           TOTorrent torrent = download_manager.getTorrent();
/*      */           
/* 1129 */           if (torrent != null)
/*      */           {
/* 1131 */             hashwrapper = torrent.getHashWrapper();
/*      */           }
/*      */         }
/*      */         catch (Exception e1) {}
/* 1135 */         Map save_download_state = (Map)this.saved_download_manager_state.remove(hashwrapper);
/*      */         
/* 1137 */         long saved_data_bytes_downloaded = 0L;
/* 1138 */         long saved_data_bytes_uploaded = 0L;
/* 1139 */         long saved_discarded = 0L;
/* 1140 */         long saved_hashfails = 0L;
/* 1141 */         long saved_SecondsDownloading = 0L;
/* 1142 */         long saved_SecondsOnlySeeding = 0L;
/*      */         
/* 1144 */         if (save_download_state != null)
/*      */         {
/* 1146 */           int maxDL = save_download_state.get("maxdl") == null ? 0 : ((Long)save_download_state.get("maxdl")).intValue();
/* 1147 */           int maxUL = save_download_state.get("maxul") == null ? 0 : ((Long)save_download_state.get("maxul")).intValue();
/*      */           
/* 1149 */           Long lDownloaded = (Long)save_download_state.get("downloaded");
/* 1150 */           Long lUploaded = (Long)save_download_state.get("uploaded");
/* 1151 */           Long lCompletedBytes = (Long)save_download_state.get("completedbytes");
/* 1152 */           Long lDiscarded = (Long)save_download_state.get("discarded");
/* 1153 */           Long lHashFailsCount = (Long)save_download_state.get("hashfails");
/* 1154 */           Long lHashFailsBytes = (Long)save_download_state.get("hashfailbytes");
/*      */           
/* 1156 */           Long nbUploads = (Long)save_download_state.get("uploads");
/*      */           
/* 1158 */           if (nbUploads != null)
/*      */           {
/* 1160 */             int maxUploads = nbUploads.intValue();
/* 1161 */             if (maxUploads != 4)
/*      */             {
/*      */ 
/*      */ 
/* 1165 */               if (download_manager.getMaxUploads() == 4) {
/* 1166 */                 download_manager.setMaxUploads(maxUploads);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1171 */           dm_stats.setDownloadRateLimitBytesPerSecond(maxDL);
/* 1172 */           dm_stats.setUploadRateLimitBytesPerSecond(maxUL);
/*      */           
/* 1174 */           if (lCompletedBytes != null) {
/* 1175 */             dm_stats.setDownloadCompletedBytes(lCompletedBytes.longValue());
/*      */           }
/*      */           
/* 1178 */           if (lDiscarded != null) {
/* 1179 */             saved_discarded = lDiscarded.longValue();
/*      */           }
/*      */           
/* 1182 */           if (lHashFailsBytes != null)
/*      */           {
/* 1184 */             saved_hashfails = lHashFailsBytes.longValue();
/*      */           }
/* 1186 */           else if (lHashFailsCount != null)
/*      */           {
/* 1188 */             TOTorrent torrent = download_manager.getTorrent();
/*      */             
/* 1190 */             if (torrent != null)
/*      */             {
/* 1192 */               saved_hashfails = lHashFailsCount.longValue() * torrent.getPieceLength();
/*      */             }
/*      */           }
/*      */           
/* 1196 */           Long lPosition = (Long)save_download_state.get("position");
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1201 */           String sCategory = null;
/* 1202 */           if (save_download_state.containsKey("category")) {
/*      */             try {
/* 1204 */               sCategory = new String((byte[])save_download_state.get("category"), "UTF8");
/*      */             }
/*      */             catch (UnsupportedEncodingException e) {
/* 1207 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */           
/* 1211 */           if (sCategory != null) {
/* 1212 */             org.gudy.azureus2.core3.category.Category cat = CategoryManager.getCategory(sCategory);
/* 1213 */             if (cat != null) { download_manager.getDownloadState().setCategory(cat);
/*      */             }
/*      */           }
/* 1216 */           download_manager.requestAssumedCompleteMode();
/*      */           
/* 1218 */           if ((lDownloaded != null) && (lUploaded != null)) {
/* 1219 */             boolean bCompleted = download_manager.isDownloadComplete(false);
/*      */             
/* 1221 */             long lUploadedValue = lUploaded.longValue();
/*      */             
/* 1223 */             long lDownloadedValue = lDownloaded.longValue();
/*      */             
/* 1225 */             if ((bCompleted) && (lDownloadedValue == 0L))
/*      */             {
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
/* 1237 */               int dl_copies = COConfigurationManager.getIntParameter("StartStopManager_iAddForSeedingDLCopyCount");
/*      */               
/* 1239 */               lDownloadedValue = download_manager.getSize() * dl_copies;
/*      */               
/* 1241 */               download_manager.getDownloadState().setFlag(1L, true);
/*      */             }
/*      */             
/* 1244 */             saved_data_bytes_downloaded = lDownloadedValue;
/* 1245 */             saved_data_bytes_uploaded = lUploadedValue;
/*      */           }
/*      */           
/* 1248 */           if (lPosition != null) {
/* 1249 */             download_manager.setPosition(lPosition.intValue());
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1254 */           Long lSecondsDLing = (Long)save_download_state.get("secondsDownloading");
/* 1255 */           if (lSecondsDLing != null) {
/* 1256 */             saved_SecondsDownloading = lSecondsDLing.longValue();
/*      */           }
/*      */           
/* 1259 */           Long lSecondsOnlySeeding = (Long)save_download_state.get("secondsOnlySeeding");
/* 1260 */           if (lSecondsOnlySeeding != null) {
/* 1261 */             saved_SecondsOnlySeeding = lSecondsOnlySeeding.longValue();
/*      */           }
/*      */           
/* 1264 */           Long already_allocated = (Long)save_download_state.get("allocated");
/* 1265 */           if ((already_allocated != null) && (already_allocated.intValue() == 1)) {
/* 1266 */             download_manager.setDataAlreadyAllocated(true);
/*      */           }
/*      */           
/* 1269 */           Long creation_time = (Long)save_download_state.get("creationTime");
/*      */           
/* 1271 */           if (creation_time != null)
/*      */           {
/* 1273 */             long ct = creation_time.longValue();
/*      */             
/* 1275 */             if (ct < SystemTime.getCurrentTime())
/*      */             {
/* 1277 */               download_manager.setCreationTime(ct);
/*      */ 
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 1285 */         else if (dm_stats.getDownloadCompleted(false) == 1000)
/*      */         {
/* 1287 */           int dl_copies = COConfigurationManager.getIntParameter("StartStopManager_iAddForSeedingDLCopyCount");
/*      */           
/* 1289 */           saved_data_bytes_downloaded = download_manager.getSize() * dl_copies;
/*      */         }
/*      */         
/*      */ 
/* 1293 */         dm_stats.restoreSessionTotals(saved_data_bytes_downloaded, saved_data_bytes_uploaded, saved_discarded, saved_hashfails, saved_SecondsDownloading, saved_SecondsOnlySeeding);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1301 */         boolean isCompleted = download_manager.isDownloadComplete(false);
/*      */         
/* 1303 */         if (download_manager.getPosition() == -1) {
/* 1304 */           int endPosition = 0;
/* 1305 */           for (int i = 0; i < this.managers_cow.size(); i++) {
/* 1306 */             DownloadManager dm = (DownloadManager)this.managers_cow.get(i);
/* 1307 */             boolean dmIsCompleted = dm.isDownloadComplete(false);
/* 1308 */             if (dmIsCompleted == isCompleted)
/* 1309 */               endPosition++;
/*      */           }
/* 1311 */           download_manager.setPosition(endPosition + 1);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1319 */         download_manager.requestAssumedCompleteMode();
/*      */         
/* 1321 */         List<DownloadManager> new_download_managers = new ArrayList(this.managers_cow);
/*      */         
/* 1323 */         new_download_managers.add(download_manager);
/*      */         
/* 1325 */         this.managers_cow = new_download_managers;
/*      */         
/* 1327 */         TOTorrent torrent = download_manager.getTorrent();
/*      */         
/* 1329 */         if (torrent != null) {
/*      */           try
/*      */           {
/* 1332 */             this.manager_map.put(new HashWrapper(torrent.getHash()), download_manager);
/*      */           }
/*      */           catch (TOTorrentException e)
/*      */           {
/* 1336 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1342 */         if (COConfigurationManager.getBooleanParameter("Set Completion Flag For Completed Downloads On Start"))
/*      */         {
/*      */ 
/*      */ 
/* 1346 */           if (download_manager.isDownloadComplete(true)) {
/* 1347 */             download_manager.getDownloadState().setFlag(8L, true);
/*      */           }
/*      */         }
/*      */         
/* 1351 */         if (notifyListeners)
/*      */         {
/* 1353 */           this.listeners_and_event_listeners.dispatch(1, download_manager);
/*      */           
/* 1355 */           this.taggable_life_manager.taggableCreated(download_manager);
/*      */           
/* 1357 */           if (this.host_support != null)
/*      */           {
/* 1359 */             this.host_support.torrentAdded(download_manager.getTorrentFileName(), download_manager.getTorrent());
/*      */           }
/*      */         }
/*      */         
/* 1363 */         download_manager.addListener(this);
/*      */         
/* 1365 */         if (save_download_state != null)
/*      */         {
/* 1367 */           Long lForceStart = (Long)save_download_state.get("forceStart");
/* 1368 */           if (lForceStart == null) {
/* 1369 */             Long lStartStopLocked = (Long)save_download_state.get("startStopLocked");
/* 1370 */             if (lStartStopLocked != null) {
/* 1371 */               lForceStart = lStartStopLocked;
/*      */             }
/*      */           }
/*      */           
/* 1375 */           if ((lForceStart != null) && 
/* 1376 */             (lForceStart.intValue() == 1)) {
/* 1377 */             download_manager.setForceStart(true);
/*      */           }
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 1383 */         this.managers_mon.exit();
/*      */       }
/*      */       
/* 1386 */       if (save) {
/* 1387 */         saveDownloads(false);
/*      */       }
/*      */       
/* 1390 */       return download_manager;
/*      */     }
/*      */     
/* 1393 */     Logger.log(new LogEvent(LOGID, 3, "Tried to add a DownloadManager after shutdown of GlobalManager."));
/*      */     
/* 1395 */     return null;
/*      */   }
/*      */   
/*      */   public List<DownloadManager> getDownloadManagers()
/*      */   {
/* 1400 */     return this.managers_cow;
/*      */   }
/*      */   
/*      */   public DownloadManager getDownloadManager(TOTorrent torrent) {
/* 1404 */     if (torrent == null) {
/* 1405 */       return null;
/*      */     }
/*      */     try {
/* 1408 */       return getDownloadManager(torrent.getHashWrapper());
/*      */     } catch (TOTorrentException e) {}
/* 1410 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DownloadManager getDownloadManager(HashWrapper hw)
/*      */   {
/* 1417 */     return (DownloadManager)this.manager_map.get(hw);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void canDownloadManagerBeRemoved(DownloadManager manager, boolean remove_torrent, boolean remove_data)
/*      */     throws GlobalManagerDownloadRemovalVetoException
/*      */   {
/*      */     try
/*      */     {
/* 1428 */       this.removal_listeners.dispatchWithException(1, new Object[] { manager, new Boolean(remove_torrent), Boolean.valueOf(remove_data) });
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 1435 */       if ((e instanceof GlobalManagerDownloadRemovalVetoException)) {
/* 1436 */         throw ((GlobalManagerDownloadRemovalVetoException)e);
/*      */       }
/* 1438 */       GlobalManagerDownloadRemovalVetoException gmv = new GlobalManagerDownloadRemovalVetoException("Error running veto check");
/* 1439 */       gmv.initCause(e);
/* 1440 */       Debug.out(e);
/* 1441 */       throw gmv;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeDownloadManager(DownloadManager manager)
/*      */     throws GlobalManagerDownloadRemovalVetoException
/*      */   {
/* 1452 */     removeDownloadManager(manager, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeDownloadManager(DownloadManager manager, boolean remove_torrent, boolean remove_data)
/*      */     throws GlobalManagerDownloadRemovalVetoException
/*      */   {
/* 1465 */     if (!this.managers_cow.contains(manager))
/*      */     {
/* 1467 */       return;
/*      */     }
/*      */     
/* 1470 */     canDownloadManagerBeRemoved(manager, remove_torrent, remove_data);
/*      */     
/* 1472 */     manager.stopIt(70, remove_torrent, remove_data, true);
/*      */     try
/*      */     {
/* 1475 */       this.managers_mon.enter();
/*      */       
/* 1477 */       List new_download_managers = new ArrayList(this.managers_cow);
/*      */       
/* 1479 */       new_download_managers.remove(manager);
/*      */       
/* 1481 */       this.managers_cow = new_download_managers;
/*      */       
/* 1483 */       TOTorrent torrent = manager.getTorrent();
/*      */       
/* 1485 */       if (torrent != null) {
/*      */         try
/*      */         {
/* 1488 */           this.manager_map.remove(new HashWrapper(torrent.getHash()));
/*      */         }
/*      */         catch (TOTorrentException e)
/*      */         {
/* 1492 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1498 */       this.managers_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1504 */     TOTorrent torrent = manager.getTorrent();
/*      */     
/* 1506 */     if (torrent != null)
/*      */     {
/* 1508 */       TorrentUtils.removeCreatedTorrent(torrent);
/*      */     }
/*      */     
/* 1511 */     manager.destroy(false);
/*      */     
/* 1513 */     fixUpDownloadManagerPositions();
/*      */     
/* 1515 */     this.listeners_and_event_listeners.dispatch(2, manager);
/*      */     
/* 1517 */     TorrentUtils.setTorrentDeleted();
/*      */     
/* 1519 */     this.taggable_life_manager.taggableDestroyed(manager);
/*      */     
/* 1521 */     manager.removeListener(this);
/*      */     
/* 1523 */     saveDownloads(false);
/*      */     
/* 1525 */     DownloadManagerState dms = manager.getDownloadState();
/*      */     
/* 1527 */     if (dms.getCategory() != null)
/*      */     {
/* 1529 */       dms.setCategory(null);
/*      */     }
/*      */     
/* 1532 */     if (manager.getTorrent() != null)
/*      */     {
/* 1534 */       this.trackerScraper.remove(manager.getTorrent());
/*      */     }
/*      */     
/* 1537 */     if (this.host_support != null)
/*      */     {
/* 1539 */       this.host_support.torrentRemoved(manager.getTorrentFileName(), manager.getTorrent());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1545 */     dms.delete();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void stopGlobalManager()
/*      */   {
/*      */     try
/*      */     {
/* 1554 */       this.managers_mon.enter();
/*      */       
/* 1556 */       if (this.isStopping) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/* 1561 */       this.isStopping = true;
/*      */     }
/*      */     finally
/*      */     {
/* 1565 */       this.managers_mon.exit();
/*      */     }
/*      */     
/* 1568 */     this.stats.save();
/*      */     
/* 1570 */     informDestroyInitiated();
/*      */     
/* 1572 */     if (this.host_support != null) {
/* 1573 */       this.host_support.destroy();
/*      */     }
/*      */     
/* 1576 */     this.torrent_folder_watcher.destroy();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1583 */       org.gudy.azureus2.core3.util.NonDaemonTaskRunner.run(new org.gudy.azureus2.core3.util.NonDaemonTask()
/*      */       {
/*      */ 
/*      */         public Object run()
/*      */         {
/*      */ 
/* 1589 */           return null;
/*      */         }
/*      */         
/*      */ 
/*      */         public String getName()
/*      */         {
/* 1595 */           return "Stopping global manager";
/*      */         }
/*      */       });
/*      */     } catch (Throwable e) {
/* 1599 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/* 1602 */     this.checker.stopIt();
/*      */     
/* 1604 */     if (COConfigurationManager.getBooleanParameter("Pause Downloads On Exit"))
/*      */     {
/* 1606 */       pauseDownloads(true);
/*      */       
/*      */ 
/*      */ 
/* 1610 */       stopAllDownloads(true);
/*      */       
/* 1612 */       saveDownloads(true);
/*      */     }
/*      */     else
/*      */     {
/* 1616 */       saveDownloads(true);
/*      */       
/* 1618 */       stopAllDownloads(true);
/*      */     }
/*      */     
/* 1621 */     if (this.stats_writer != null)
/*      */     {
/* 1623 */       this.stats_writer.destroy();
/*      */     }
/*      */     
/* 1626 */     DownloadManagerStateFactory.saveGlobalStateCache();
/*      */     try
/*      */     {
/* 1629 */       this.managers_mon.enter();
/*      */       
/* 1631 */       this.managers_cow = new ArrayList();
/*      */       
/* 1633 */       this.manager_map.clear();
/*      */     }
/*      */     finally
/*      */     {
/* 1637 */       this.managers_mon.exit();
/*      */     }
/*      */     
/* 1640 */     informDestroyed();
/*      */   }
/*      */   
/*      */   public void stopAllDownloads() {
/* 1644 */     stopAllDownloads(false);
/*      */   }
/*      */   
/*      */   protected void stopAllDownloads(boolean for_close)
/*      */   {
/* 1649 */     if ((for_close) && 
/* 1650 */       (this.progress_listener != null)) {
/* 1651 */       this.progress_listener.reportCurrentTask(MessageText.getString("splash.unloadingTorrents"));
/*      */     }
/*      */     
/*      */ 
/* 1655 */     long lastListenerUpdate = 0L;
/*      */     
/* 1657 */     List<DownloadManager> managers = sortForStop();
/*      */     
/* 1659 */     int nbDownloads = managers.size();
/*      */     
/* 1661 */     for (int i = 0; i < nbDownloads; i++)
/*      */     {
/* 1663 */       DownloadManager manager = (DownloadManager)managers.get(i);
/*      */       
/* 1665 */       long now = SystemTime.getCurrentTime();
/*      */       
/* 1667 */       if ((this.progress_listener != null) && (now - lastListenerUpdate > 100L)) {
/* 1668 */         lastListenerUpdate = now;
/*      */         
/* 1670 */         int currentDownload = i + 1;
/*      */         
/* 1672 */         this.progress_listener.reportPercent(100 * currentDownload / nbDownloads);
/* 1673 */         this.progress_listener.reportCurrentTask(MessageText.getString("splash.unloadingTorrent") + " " + currentDownload + " " + MessageText.getString("splash.of") + " " + nbDownloads + " : " + manager.getTorrentFileName());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1679 */       int state = manager.getState();
/*      */       
/* 1681 */       if ((state != 70) && (state != 65))
/*      */       {
/*      */ 
/* 1684 */         manager.stopIt(for_close ? 71 : 70, false, false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void startAllDownloads()
/*      */   {
/* 1694 */     for (Iterator iter = this.managers_cow.iterator(); iter.hasNext();) {
/* 1695 */       DownloadManager manager = (DownloadManager)iter.next();
/*      */       
/* 1697 */       if (manager.getState() == 70)
/*      */       {
/* 1699 */         manager.stopIt(75, false, false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean pauseDownload(DownloadManager manager)
/*      */   {
/* 1708 */     if (manager.getTorrent() == null)
/*      */     {
/* 1710 */       return false;
/*      */     }
/*      */     
/* 1713 */     int state = manager.getState();
/*      */     
/* 1715 */     if ((state != 70) && (state != 100) && (state != 65))
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/* 1721 */         HashWrapper wrapper = manager.getTorrent().getHashWrapper();
/*      */         
/* 1723 */         boolean forced = manager.isForceStart();
/*      */         
/*      */ 
/*      */         try
/*      */         {
/* 1728 */           this.paused_list_mon.enter();
/*      */           
/* 1730 */           this.paused_list.add(new Object[] { wrapper, Boolean.valueOf(forced) });
/*      */         }
/*      */         finally
/*      */         {
/* 1734 */           this.paused_list_mon.exit();
/*      */         }
/*      */         
/* 1737 */         manager.stopIt(70, false, false);
/*      */         
/* 1739 */         return true;
/*      */       }
/*      */       catch (TOTorrentException e)
/*      */       {
/* 1743 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1747 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void pauseDownloadsForPeriod(int seconds)
/*      */   {
/*      */     try
/*      */     {
/* 1755 */       this.paused_list_mon.enter();
/*      */       
/* 1757 */       if (this.auto_resume_timer != null)
/*      */       {
/* 1759 */         this.auto_resume_timer.cancel();
/*      */       }
/*      */       
/* 1762 */       this.auto_resume_timer = SimpleTimer.addEvent("GM:auto-resume", SystemTime.getOffsetTime(seconds * 1000), new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1772 */           GlobalManagerImpl.this.resumeDownloads();
/*      */         }
/*      */       });
/*      */     }
/*      */     finally {
/* 1777 */       this.paused_list_mon.exit();
/*      */     }
/*      */     
/* 1780 */     pauseDownloads();
/*      */   }
/*      */   
/*      */   public int getPauseDownloadPeriodRemaining()
/*      */   {
/*      */     try
/*      */     {
/* 1787 */       this.paused_list_mon.enter();
/*      */       
/* 1789 */       if (this.auto_resume_timer != null)
/*      */       {
/* 1791 */         long rem = this.auto_resume_timer.getWhen() - SystemTime.getCurrentTime();
/*      */         
/* 1793 */         return Math.max(0, (int)(rem / 1000L));
/*      */       }
/*      */     }
/*      */     finally {
/* 1797 */       this.paused_list_mon.exit();
/*      */     }
/*      */     
/* 1800 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public void pauseDownloads()
/*      */   {
/* 1806 */     pauseDownloads(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void pauseDownloads(boolean tag_only)
/*      */   {
/* 1813 */     List<DownloadManager> managers = sortForStop();
/*      */     
/* 1815 */     for (DownloadManager manager : managers)
/*      */     {
/* 1817 */       if (manager.getTorrent() != null)
/*      */       {
/*      */ 
/*      */ 
/* 1821 */         int state = manager.getState();
/*      */         
/* 1823 */         if ((state != 70) && (state != 100) && (state != 65))
/*      */         {
/*      */           try
/*      */           {
/*      */ 
/* 1828 */             boolean forced = manager.isForceStart();
/*      */             
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/* 1834 */               this.paused_list_mon.enter();
/*      */               
/* 1836 */               this.paused_list.add(new Object[] { manager.getTorrent().getHashWrapper(), Boolean.valueOf(forced) });
/*      */             }
/*      */             finally
/*      */             {
/* 1840 */               this.paused_list_mon.exit();
/*      */             }
/*      */             
/* 1843 */             if (!tag_only)
/*      */             {
/* 1845 */               manager.stopIt(70, false, false);
/*      */             }
/*      */           }
/*      */           catch (TOTorrentException e) {
/* 1849 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean canPauseDownload(DownloadManager manager)
/*      */   {
/* 1860 */     if (manager.getTorrent() == null)
/*      */     {
/* 1862 */       return false;
/*      */     }
/*      */     
/* 1865 */     int state = manager.getState();
/*      */     
/* 1867 */     if ((state != 70) && (state != 100) && (state != 65))
/*      */     {
/*      */ 
/*      */ 
/* 1871 */       return true;
/*      */     }
/*      */     
/* 1874 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isPaused(DownloadManager manager)
/*      */   {
/* 1881 */     if (this.paused_list.size() == 0)
/*      */     {
/* 1883 */       return false;
/*      */     }
/*      */     try
/*      */     {
/* 1887 */       this.paused_list_mon.enter();
/*      */       
/* 1889 */       for (int i = 0; i < this.paused_list.size(); i++)
/*      */       {
/* 1891 */         Object[] data = (Object[])this.paused_list.get(i);
/*      */         
/* 1893 */         HashWrapper hash = (HashWrapper)data[0];
/*      */         
/* 1895 */         DownloadManager this_manager = getDownloadManager(hash);
/*      */         
/* 1897 */         if (this_manager == manager)
/*      */         {
/* 1899 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 1903 */       return 0;
/*      */     }
/*      */     finally
/*      */     {
/* 1907 */       this.paused_list_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canPauseDownloads()
/*      */   {
/* 1914 */     for (Iterator i = this.managers_cow.iterator(); i.hasNext();)
/*      */     {
/* 1916 */       DownloadManager manager = (DownloadManager)i.next();
/*      */       
/* 1918 */       if (canPauseDownload(manager))
/*      */       {
/* 1920 */         return true;
/*      */       }
/*      */     }
/* 1923 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void resumeDownload(DownloadManager manager)
/*      */   {
/* 1931 */     boolean resume_ok = false;
/* 1932 */     boolean force = false;
/*      */     try
/*      */     {
/* 1935 */       this.paused_list_mon.enter();
/*      */       
/* 1937 */       for (int i = 0; i < this.paused_list.size(); i++)
/*      */       {
/* 1939 */         Object[] data = (Object[])this.paused_list.get(i);
/*      */         
/* 1941 */         HashWrapper hash = (HashWrapper)data[0];
/*      */         
/* 1943 */         force = ((Boolean)data[1]).booleanValue();
/*      */         
/* 1945 */         DownloadManager this_manager = getDownloadManager(hash);
/*      */         
/* 1947 */         if (this_manager == manager)
/*      */         {
/* 1949 */           resume_ok = true;
/*      */           
/* 1951 */           this.paused_list.remove(i);
/*      */           
/* 1953 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1958 */       this.paused_list_mon.exit();
/*      */     }
/*      */     
/* 1961 */     if (resume_ok)
/*      */     {
/* 1963 */       if (manager.getState() == 70)
/*      */       {
/* 1965 */         if (force)
/*      */         {
/* 1967 */           manager.setForceStart(true);
/*      */         }
/*      */         else
/*      */         {
/* 1971 */           manager.stopIt(75, false, false);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean resumingDownload(DownloadManager manager)
/*      */   {
/*      */     try
/*      */     {
/* 1982 */       this.paused_list_mon.enter();
/*      */       
/* 1984 */       for (int i = 0; i < this.paused_list.size(); i++)
/*      */       {
/* 1986 */         Object[] data = (Object[])this.paused_list.get(i);
/*      */         
/* 1988 */         HashWrapper hash = (HashWrapper)data[0];
/*      */         
/* 1990 */         DownloadManager this_manager = getDownloadManager(hash);
/*      */         
/* 1992 */         if (this_manager == manager)
/*      */         {
/* 1994 */           this.paused_list.remove(i);
/*      */           
/* 1996 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 2001 */       this.paused_list_mon.exit();
/*      */     }
/*      */     
/* 2004 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void resumeDownloads()
/*      */   {
/* 2011 */     this.auto_resume_disabled = false;
/*      */     try
/*      */     {
/* 2014 */       this.paused_list_mon.enter();
/*      */       
/* 2016 */       if (this.auto_resume_timer != null)
/*      */       {
/* 2018 */         this.auto_resume_timer.cancel();
/*      */         
/* 2020 */         this.auto_resume_timer = null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2026 */       ArrayList<Object[]> copy = new ArrayList(this.paused_list);
/*      */       
/* 2028 */       for (Object[] data : copy)
/*      */       {
/* 2030 */         HashWrapper hash = (HashWrapper)data[0];
/* 2031 */         boolean force = ((Boolean)data[1]).booleanValue();
/*      */         
/* 2033 */         DownloadManager manager = getDownloadManager(hash);
/*      */         
/* 2035 */         if ((manager != null) && (manager.getState() == 70))
/*      */         {
/* 2037 */           if (force)
/*      */           {
/* 2039 */             manager.setForceStart(true);
/*      */           }
/*      */           else
/*      */           {
/* 2043 */             manager.stopIt(75, false, false);
/*      */           }
/*      */         }
/*      */       }
/* 2047 */       this.paused_list.clear();
/*      */     }
/*      */     finally
/*      */     {
/* 2051 */       this.paused_list_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean resumeDownloads(boolean is_auto_resume)
/*      */   {
/* 2059 */     if ((is_auto_resume) && (this.auto_resume_disabled))
/*      */     {
/* 2061 */       return false;
/*      */     }
/*      */     
/* 2064 */     resumeDownloads();
/*      */     
/* 2066 */     return true;
/*      */   }
/*      */   
/*      */   public boolean canResumeDownloads() {
/* 2070 */     try { this.paused_list_mon.enter();
/* 2071 */       for (int i = 0; i < this.paused_list.size(); i++) {
/* 2072 */         Object[] data = (Object[])this.paused_list.get(i);
/* 2073 */         HashWrapper hash = (HashWrapper)data[0];
/* 2074 */         DownloadManager manager = getDownloadManager(hash);
/*      */         
/* 2076 */         if ((manager != null) && (manager.getState() == 70)) {
/* 2077 */           return true;
/*      */         }
/*      */       }
/*      */     } finally {
/* 2081 */       this.paused_list_mon.exit();
/*      */     }
/* 2083 */     return false;
/*      */   }
/*      */   
/*      */   public String isSwarmMerging(DownloadManager dm) {
/* 2087 */     return this.file_merger.isSwarmMerging(dm);
/*      */   }
/*      */   
/*      */ 
/*      */   private List<DownloadManager> sortForStop()
/*      */   {
/* 2093 */     List<DownloadManager> managers = new ArrayList(this.managers_cow);
/*      */     
/* 2095 */     Collections.sort(managers, new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public int compare(DownloadManager o1, DownloadManager o2)
/*      */       {
/*      */ 
/*      */ 
/* 2104 */         int s1 = o1.getState();
/* 2105 */         int s2 = o2.getState();
/*      */         
/* 2107 */         if (s2 == 75)
/*      */         {
/* 2109 */           return 1;
/*      */         }
/* 2111 */         if (s1 == 75)
/*      */         {
/* 2113 */           return -1;
/*      */         }
/*      */         
/* 2116 */         return 0;
/*      */       }
/*      */       
/* 2119 */     });
/* 2120 */     return managers;
/*      */   }
/*      */   
/*      */   private void loadDownloads()
/*      */   {
/*      */     try {
/* 2126 */       if (this.cripple_downloads_config) {
/* 2127 */         this.loadingComplete = true;
/* 2128 */         this.loadingSem.releaseForever();
/*      */       }
/*      */       else
/*      */       {
/*      */         try
/*      */         {
/* 2134 */           DownloadManagerStateFactory.loadGlobalStateCache();
/*      */           
/* 2136 */           int triggerOnCount = 2;
/* 2137 */           ArrayList<DownloadManager> downloadsAdded = new ArrayList();
/* 2138 */           this.lastListenerUpdate = 0L;
/*      */           try {
/* 2140 */             if (this.progress_listener != null) {
/* 2141 */               this.progress_listener.reportCurrentTask(MessageText.getString("splash.loadingTorrents"));
/*      */             }
/*      */             
/* 2144 */             Map map = FileUtil.readResilientConfigFile("downloads.config");
/*      */             
/* 2146 */             boolean debug = Boolean.getBoolean("debug");
/*      */             
/* 2148 */             Iterator iter = null;
/*      */             
/* 2150 */             List downloads = (List)map.get("downloads");
/*      */             int nbDownloads;
/* 2152 */             int nbDownloads; if (downloads == null)
/*      */             {
/* 2154 */               iter = map.values().iterator();
/* 2155 */               nbDownloads = map.size();
/*      */             }
/*      */             else
/*      */             {
/* 2159 */               iter = downloads.iterator();
/* 2160 */               nbDownloads = downloads.size();
/*      */             }
/* 2162 */             int currentDownload = 0;
/* 2163 */             while (iter.hasNext()) {
/* 2164 */               currentDownload++;
/* 2165 */               Map mDownload = (Map)iter.next();
/*      */               
/* 2167 */               DownloadManager dm = loadDownload(mDownload, currentDownload, nbDownloads, this.progress_listener, debug);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2175 */               if (dm != null)
/*      */               {
/* 2177 */                 downloadsAdded.add(dm);
/*      */                 
/* 2179 */                 if (downloadsAdded.size() >= triggerOnCount) {
/* 2180 */                   triggerOnCount *= 2;
/* 2181 */                   triggerAddListener(downloadsAdded);
/* 2182 */                   downloadsAdded.clear();
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2190 */             COConfigurationManager.setParameter("Set Completion Flag For Completed Downloads On Start", false);
/*      */             
/*      */ 
/* 2193 */             ArrayList pause_data = (ArrayList)map.get("pause_data");
/* 2194 */             if (pause_data != null) {
/* 2195 */               try { this.paused_list_mon.enter();
/* 2196 */                 for (int i = 0; i < pause_data.size(); i++) {
/* 2197 */                   Object pd = pause_data.get(i);
/*      */                   
/*      */                   boolean force;
/*      */                   byte[] key;
/*      */                   boolean force;
/* 2202 */                   if ((pd instanceof byte[]))
/*      */                   {
/* 2204 */                     byte[] key = (byte[])pause_data.get(i);
/* 2205 */                     force = false;
/*      */                   } else {
/* 2207 */                     Map m = (Map)pd;
/*      */                     
/* 2209 */                     key = (byte[])m.get("hash");
/* 2210 */                     force = ((Long)m.get("force")).intValue() == 1;
/*      */                   }
/* 2212 */                   this.paused_list.add(new Object[] { new HashWrapper(key), Boolean.valueOf(force) });
/*      */                 }
/*      */               } finally {
/* 2215 */                 this.paused_list_mon.exit();
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 2221 */             fixUpDownloadManagerPositions();
/* 2222 */             Logger.log(new LogEvent(LOGID, "Loaded " + this.managers_cow.size() + " torrents"));
/*      */ 
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/* 2229 */             Debug.printStackTrace(e);
/*      */           } finally {
/* 2231 */             this.loadingComplete = true;
/* 2232 */             triggerAddListener(downloadsAdded);
/*      */             
/* 2234 */             this.loadingSem.releaseForever();
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 2239 */           DownloadManagerStateFactory.discardGlobalStateCache();
/*      */         }
/*      */       }
/*      */     } finally {
/* 2243 */       this.taggable_life_manager.initialized(getResolvedTaggables());
/*      */     }
/*      */   }
/*      */   
/*      */   private void triggerAddListener(List downloadsToAdd) {
/*      */     try {
/* 2249 */       this.managers_mon.enter();
/* 2250 */       List listenersCopy = this.listeners_and_event_listeners.getListenersCopy();
/*      */       
/* 2252 */       for (int j = 0; j < listenersCopy.size(); j++) {
/* 2253 */         Object listener = listenersCopy.get(j);
/*      */         
/* 2255 */         if ((listener instanceof GlobalManagerListener)) {
/* 2256 */           GlobalManagerListener gmListener = (GlobalManagerListener)listener;
/* 2257 */           for (int i = 0; i < downloadsToAdd.size(); i++) {
/* 2258 */             DownloadManager dm = (DownloadManager)downloadsToAdd.get(i);
/* 2259 */             gmListener.downloadManagerAdded(dm);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 2265 */       this.managers_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void saveState()
/*      */   {
/* 2272 */     saveDownloads(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void saveDownloads(boolean immediate)
/*      */   {
/* 2279 */     if (!immediate)
/*      */     {
/* 2281 */       this.needsSaving = true;
/*      */       
/* 2283 */       return;
/*      */     }
/*      */     
/* 2286 */     if (!this.loadingComplete) {
/* 2287 */       this.needsSaving = true;
/* 2288 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2293 */     this.needsSaving = false;
/* 2294 */     this.needsSavingCozStateChanged = 0L;
/*      */     
/* 2296 */     if (this.cripple_downloads_config) {
/* 2297 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2301 */       this.managers_mon.enter();
/*      */       
/* 2303 */       List<DownloadManager> managers_temp = new ArrayList(this.managers_cow);
/*      */       
/* 2305 */       Collections.sort(managers_temp, new Comparator()
/*      */       {
/*      */ 
/*      */         public final int compare(Object a, Object b)
/*      */         {
/*      */ 
/* 2311 */           return ((DownloadManager)a).getPosition() - ((DownloadManager)b).getPosition();
/*      */         }
/*      */         
/* 2314 */       });
/* 2315 */       this.managers_cow = managers_temp;
/*      */       
/* 2317 */       if (Logger.isEnabled()) {
/* 2318 */         Logger.log(new LogEvent(LOGID, "Saving Download List (" + this.managers_cow.size() + " items)"));
/*      */       }
/* 2320 */       Map map = new HashMap();
/* 2321 */       List list = new ArrayList(this.managers_cow.size());
/* 2322 */       for (int i = 0; i < this.managers_cow.size(); i++) {
/* 2323 */         DownloadManager dm = (DownloadManager)this.managers_cow.get(i);
/*      */         
/* 2325 */         Map dmMap = exportDownloadStateToMapSupport(dm, true);
/*      */         
/* 2327 */         list.add(dmMap);
/*      */       }
/*      */       
/* 2330 */       map.put("downloads", list);
/*      */       try
/*      */       {
/* 2333 */         this.paused_list_mon.enter();
/* 2334 */         if (!this.paused_list.isEmpty()) {
/* 2335 */           ArrayList pause_data = new ArrayList();
/* 2336 */           for (int i = 0; i < this.paused_list.size(); i++) {
/* 2337 */             Object[] data = (Object[])this.paused_list.get(i);
/*      */             
/* 2339 */             HashWrapper hash = (HashWrapper)data[0];
/* 2340 */             Boolean force = (Boolean)data[1];
/*      */             
/* 2342 */             Map m = new HashMap();
/*      */             
/* 2344 */             m.put("hash", hash.getHash());
/* 2345 */             m.put("force", new Long(force.booleanValue() ? 1L : 0L));
/*      */             
/* 2347 */             pause_data.add(m);
/*      */           }
/* 2349 */           map.put("pause_data", pause_data);
/*      */         }
/*      */       } finally {
/* 2352 */         this.paused_list_mon.exit();
/*      */       }
/*      */       
/* 2355 */       FileUtil.writeResilientConfigFile("downloads.config", map);
/*      */     }
/*      */     finally {
/* 2358 */       this.managers_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DownloadManager loadDownload(Map mDownload, int currentDownload, int nbDownloads, GlobalMangerProgressListener progress_listener, boolean debug)
/*      */   {
/*      */     try
/*      */     {
/* 2371 */       byte[] torrent_hash = (byte[])mDownload.get("torrent_hash");
/*      */       
/* 2373 */       Long lPersistent = (Long)mDownload.get("persistent");
/*      */       
/* 2375 */       boolean persistent = (lPersistent == null) || (lPersistent.longValue() == 1L);
/*      */       
/*      */ 
/* 2378 */       String fileName = new String((byte[])mDownload.get("torrent"), "UTF8");
/*      */       
/* 2380 */       if ((progress_listener != null) && (SystemTime.getCurrentTime() - this.lastListenerUpdate > 100L)) {
/* 2381 */         this.lastListenerUpdate = SystemTime.getCurrentTime();
/*      */         
/* 2383 */         String shortFileName = fileName;
/*      */         try {
/* 2385 */           File f = new File(fileName);
/* 2386 */           shortFileName = f.getName();
/*      */         }
/*      */         catch (Exception e) {}
/*      */         
/*      */ 
/* 2391 */         progress_listener.reportPercent(100 * currentDownload / nbDownloads);
/* 2392 */         progress_listener.reportCurrentTask(MessageText.getString("splash.loadingTorrent") + " " + currentDownload + " " + MessageText.getString("splash.of") + " " + nbDownloads + " : " + shortFileName);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2402 */       byte[] torrent_save_dir_bytes = (byte[])mDownload.get("save_dir");
/*      */       String torrent_save_file;
/* 2404 */       String torrent_save_dir; String torrent_save_file; if (torrent_save_dir_bytes != null)
/*      */       {
/* 2406 */         byte[] torrent_save_file_bytes = (byte[])mDownload.get("save_file");
/*      */         
/* 2408 */         String torrent_save_dir = new String(torrent_save_dir_bytes, "UTF8");
/*      */         String torrent_save_file;
/* 2410 */         if (torrent_save_file_bytes != null)
/*      */         {
/* 2412 */           torrent_save_file = new String(torrent_save_file_bytes, "UTF8");
/*      */         }
/*      */         else {
/* 2415 */           torrent_save_file = null;
/*      */         }
/*      */       }
/*      */       else {
/* 2419 */         byte[] savePathBytes = (byte[])mDownload.get("path");
/* 2420 */         torrent_save_dir = new String(savePathBytes, "UTF8");
/* 2421 */         torrent_save_file = null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2426 */       int state = 0;
/* 2427 */       if (debug)
/*      */       {
/* 2429 */         state = 70;
/*      */ 
/*      */ 
/*      */       }
/* 2433 */       else if (mDownload.containsKey("state")) {
/* 2434 */         state = ((Long)mDownload.get("state")).intValue();
/* 2435 */         if ((state != 70) && (state != 75) && (state != 0))
/*      */         {
/*      */ 
/*      */ 
/* 2439 */           state = 75;
/*      */         }
/*      */       }
/*      */       else {
/* 2443 */         int stopped = ((Long)mDownload.get("stopped")).intValue();
/*      */         
/* 2445 */         if (stopped == 1)
/*      */         {
/* 2447 */           state = 70;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2452 */       Long seconds_downloading = (Long)mDownload.get("secondsDownloading");
/*      */       
/* 2454 */       boolean has_ever_been_started = (seconds_downloading != null) && (seconds_downloading.longValue() > 0L);
/*      */       
/* 2456 */       if (torrent_hash != null) {
/* 2457 */         this.saved_download_manager_state.put(new HashWrapper(torrent_hash), mDownload);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2464 */       if (persistent)
/*      */       {
/*      */ 
/* 2467 */         Map map_file_priorities = (Map)mDownload.get("file_priorities_c");
/*      */         List file_priorities;
/* 2469 */         List file_priorities; if (map_file_priorities != null)
/*      */         {
/* 2471 */           Long[] array_file_priorities = new Long[0];
/* 2472 */           for (Object key : map_file_priorities.keySet()) {
/* 2473 */             long priority = Long.parseLong(key.toString());
/* 2474 */             String indexRanges = new String((byte[])map_file_priorities.get(key), "utf-8");
/* 2475 */             String[] rangesStrings = indexRanges.split(",");
/*      */             
/* 2477 */             if ((array_file_priorities.length == 0) && (rangesStrings.length > 1))
/*      */             {
/* 2479 */               array_file_priorities = new Long[rangesStrings.length];
/*      */             }
/*      */             
/* 2482 */             for (String rangeString : rangesStrings) {
/* 2483 */               String[] ranges = rangeString.split("-");
/* 2484 */               int start = Integer.parseInt(ranges[0]);
/* 2485 */               int end = ranges.length == 1 ? start : Integer.parseInt(ranges[1]);
/* 2486 */               if (end >= array_file_priorities.length) {
/* 2487 */                 array_file_priorities = enlargeLongArray(array_file_priorities, end + 1);
/*      */               }
/* 2489 */               Arrays.fill(array_file_priorities, start, end + 1, Long.valueOf(priority));
/*      */             }
/*      */           }
/* 2492 */           file_priorities = Arrays.asList(array_file_priorities);
/*      */         } else {
/* 2494 */           file_priorities = (List)mDownload.get("file_priorities");
/*      */         }
/*      */         
/* 2497 */         DownloadManager dm = DownloadManagerFactory.create(this, torrent_hash, fileName, torrent_save_dir, torrent_save_file, state, true, true, has_ever_been_started, file_priorities);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2502 */         if (addDownloadManager(dm, false, false) == dm)
/*      */         {
/* 2504 */           return dm;
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */     }
/*      */     catch (UnsupportedEncodingException e1) {}catch (Throwable e)
/*      */     {
/* 2512 */       Logger.log(new LogEvent(LOGID, "Error while loading downloads.  One download may not have been added to the list.", e));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2517 */     return null;
/*      */   }
/*      */   
/*      */   public static Long[] enlargeLongArray(Long[] array, int expandTo) {
/* 2521 */     Long[] new_array = new Long[expandTo];
/* 2522 */     if (array.length > 0) {
/* 2523 */       System.arraycopy(array, 0, new_array, 0, array.length);
/*      */     }
/*      */     
/*      */ 
/* 2527 */     return new_array;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Map exportDownloadStateToMap(DownloadManager dm)
/*      */   {
/* 2534 */     return exportDownloadStateToMapSupport(dm, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DownloadManager importDownloadStateFromMap(Map map)
/*      */   {
/* 2541 */     DownloadManager dm = loadDownload(map, 1, 1, null, false);
/*      */     
/* 2543 */     if (dm != null)
/*      */     {
/* 2545 */       List<DownloadManager> dms = new ArrayList(1);
/*      */       
/* 2547 */       dms.add(dm);
/*      */       
/* 2549 */       triggerAddListener(dms);
/*      */       
/* 2551 */       this.taggable_life_manager.taggableCreated(dm);
/*      */       
/* 2553 */       if (this.host_support != null)
/*      */       {
/* 2555 */         this.host_support.torrentAdded(dm.getTorrentFileName(), dm.getTorrent());
/*      */       }
/*      */     }
/*      */     
/* 2559 */     return dm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map exportDownloadStateToMapSupport(DownloadManager dm, boolean internal_export)
/*      */   {
/* 2567 */     DownloadManagerStats dm_stats = dm.getStats();
/* 2568 */     Map<String, Object> dmMap = new HashMap();
/* 2569 */     TOTorrent torrent = dm.getTorrent();
/*      */     
/* 2571 */     if (torrent != null) {
/*      */       try {
/* 2573 */         dmMap.put("torrent_hash", torrent.getHash());
/*      */       }
/*      */       catch (TOTorrentException e)
/*      */       {
/* 2577 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 2581 */     File save_loc = dm.getAbsoluteSaveLocation();
/* 2582 */     dmMap.put("persistent", new Long(dm.isPersistent() ? 1L : 0L));
/* 2583 */     dmMap.put("torrent", dm.getTorrentFileName());
/* 2584 */     dmMap.put("save_dir", save_loc.getParent());
/* 2585 */     dmMap.put("save_file", save_loc.getName());
/*      */     
/* 2587 */     dmMap.put("maxdl", new Long(dm_stats.getDownloadRateLimitBytesPerSecond()));
/* 2588 */     dmMap.put("maxul", new Long(dm_stats.getUploadRateLimitBytesPerSecond()));
/*      */     
/* 2590 */     int state = dm.getState();
/*      */     
/* 2592 */     if (state == 100)
/*      */     {
/*      */ 
/*      */ 
/* 2596 */       state = 70;
/*      */     }
/* 2598 */     else if ((dm.getAssumedComplete()) && (!dm.isForceStart()) && (state != 70))
/*      */     {
/*      */ 
/* 2601 */       state = 75;
/*      */     }
/* 2603 */     else if ((state != 70) && (state != 75) && (state != 0))
/*      */     {
/*      */ 
/*      */ 
/* 2607 */       state = 0;
/*      */     }
/*      */     
/*      */ 
/* 2611 */     dmMap.put("state", new Long(state));
/*      */     
/* 2613 */     if (internal_export) {
/* 2614 */       dmMap.put("position", new Long(dm.getPosition()));
/*      */     }
/* 2616 */     dmMap.put("downloaded", new Long(dm_stats.getTotalDataBytesReceived()));
/* 2617 */     dmMap.put("uploaded", new Long(dm_stats.getTotalDataBytesSent()));
/* 2618 */     dmMap.put("completedbytes", new Long(dm_stats.getDownloadCompletedBytes()));
/* 2619 */     dmMap.put("discarded", new Long(dm_stats.getDiscarded()));
/* 2620 */     dmMap.put("hashfailbytes", new Long(dm_stats.getHashFailBytes()));
/* 2621 */     dmMap.put("forceStart", new Long((dm.isForceStart()) && (dm.getState() != 30) ? 1L : 0L));
/* 2622 */     dmMap.put("secondsDownloading", new Long(dm_stats.getSecondsDownloading()));
/* 2623 */     dmMap.put("secondsOnlySeeding", new Long(dm_stats.getSecondsOnlySeeding()));
/*      */     
/*      */ 
/* 2626 */     dmMap.put("uploads", new Long(dm.getMaxUploads()));
/*      */     
/* 2628 */     dmMap.put("creationTime", new Long(dm.getCreationTime()));
/*      */     
/*      */ 
/*      */ 
/* 2632 */     dm.saveDownload();
/*      */     
/* 2634 */     List file_priorities = (List)dm.getData("file_priorities");
/* 2635 */     if (file_priorities != null) {
/* 2636 */       int count = file_priorities.size();
/* 2637 */       Map<String, String> map_file_priorities = new HashMap();
/* 2638 */       Long priority = (Long)file_priorities.get(0);
/* 2639 */       int posStart = 0;
/* 2640 */       int posEnd = 0;
/* 2641 */       while (posStart < count) {
/* 2642 */         priority = (Long)file_priorities.get(posStart);
/* 2643 */         while ((posEnd + 1 < count) && ((Long)file_priorities.get(posEnd + 1) == priority)) {
/* 2644 */           posEnd++;
/*      */         }
/* 2646 */         String key = priority.toString();
/* 2647 */         String val = (String)map_file_priorities.get(key);
/* 2648 */         if (val == null) {
/* 2649 */           val = "" + posStart;
/*      */         } else {
/* 2651 */           val = val + "," + posStart;
/*      */         }
/* 2653 */         if (posStart != posEnd) {
/* 2654 */           val = val + "-" + posEnd;
/*      */         }
/* 2656 */         map_file_priorities.put(key, val);
/* 2657 */         posStart = posEnd + 1;
/*      */       }
/*      */       
/* 2660 */       dmMap.put("file_priorities_c", map_file_priorities);
/*      */     }
/*      */     
/* 2663 */     dmMap.put("allocated", new Long(dm.isDataAlreadyAllocated() ? 1L : 0L));
/*      */     
/* 2665 */     return dmMap;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TRTrackerScraper getTrackerScraper()
/*      */   {
/* 2672 */     return this.trackerScraper;
/*      */   }
/*      */   
/*      */ 
/*      */   public GlobalManagerStats getStats()
/*      */   {
/* 2678 */     return this.stats;
/*      */   }
/*      */   
/*      */   public boolean contains(DownloadManager manager) {
/* 2682 */     if ((this.managers_cow != null) && (manager != null)) {
/* 2683 */       return this.managers_cow.contains(manager);
/*      */     }
/* 2685 */     return false;
/*      */   }
/*      */   
/*      */   public int getIndexOf(DownloadManager manager) {
/* 2689 */     if ((this.managers_cow != null) && (manager != null))
/* 2690 */       return this.managers_cow.indexOf(manager);
/* 2691 */     return -1;
/*      */   }
/*      */   
/*      */   public boolean isMoveableUp(DownloadManager manager)
/*      */   {
/* 2696 */     if ((manager.isDownloadComplete(false)) && (COConfigurationManager.getIntParameter("StartStopManager_iRankType") != 0) && (COConfigurationManager.getBooleanParameter("StartStopManager_bAutoReposition")))
/*      */     {
/*      */ 
/* 2699 */       return false;
/*      */     }
/* 2701 */     return manager.getPosition() > 1;
/*      */   }
/*      */   
/*      */   public int downloadManagerCount(boolean bCompleted) {
/* 2705 */     int numInGroup = 0;
/* 2706 */     for (Iterator it = this.managers_cow.iterator(); it.hasNext();) {
/* 2707 */       DownloadManager dm = (DownloadManager)it.next();
/* 2708 */       if (dm.isDownloadComplete(false) == bCompleted)
/* 2709 */         numInGroup++;
/*      */     }
/* 2711 */     return numInGroup;
/*      */   }
/*      */   
/*      */   public boolean isMoveableDown(DownloadManager manager)
/*      */   {
/* 2716 */     boolean isCompleted = manager.isDownloadComplete(false);
/*      */     
/* 2718 */     if ((isCompleted) && (COConfigurationManager.getIntParameter("StartStopManager_iRankType") != 0) && (COConfigurationManager.getBooleanParameter("StartStopManager_bAutoReposition")))
/*      */     {
/*      */ 
/* 2721 */       return false;
/*      */     }
/* 2723 */     return manager.getPosition() < downloadManagerCount(isCompleted);
/*      */   }
/*      */   
/*      */   public void moveUp(DownloadManager manager) {
/* 2727 */     moveTo(manager, manager.getPosition() - 1);
/*      */   }
/*      */   
/*      */   public void moveDown(DownloadManager manager) {
/* 2731 */     moveTo(manager, manager.getPosition() + 1);
/*      */   }
/*      */   
/*      */   public void moveTop(DownloadManager[] manager)
/*      */   {
/*      */     try {
/* 2737 */       this.managers_mon.enter();
/*      */       
/* 2739 */       int newPosition = 1;
/* 2740 */       for (int i = 0; i < manager.length; i++) {
/* 2741 */         moveTo(manager[i], newPosition++);
/*      */       }
/*      */     } finally {
/* 2744 */       this.managers_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void moveEnd(DownloadManager[] manager) {
/*      */     try {
/* 2750 */       this.managers_mon.enter();
/*      */       
/* 2752 */       int endPosComplete = 0;
/* 2753 */       int endPosIncomplete = 0;
/* 2754 */       for (int j = 0; j < this.managers_cow.size(); j++) {
/* 2755 */         DownloadManager dm = (DownloadManager)this.managers_cow.get(j);
/* 2756 */         if (dm.isDownloadComplete(false)) {
/* 2757 */           endPosComplete++;
/*      */         } else
/* 2759 */           endPosIncomplete++;
/*      */       }
/* 2761 */       for (int i = manager.length - 1; i >= 0; i--) {
/* 2762 */         if ((manager[i].isDownloadComplete(false)) && (endPosComplete > 0)) {
/* 2763 */           moveTo(manager[i], endPosComplete--);
/* 2764 */         } else if (endPosIncomplete > 0) {
/* 2765 */           moveTo(manager[i], endPosIncomplete--);
/*      */         }
/*      */       }
/*      */     } finally {
/* 2769 */       this.managers_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void moveTo(DownloadManager manager, int newPosition) {
/* 2774 */     boolean curCompleted = manager.isDownloadComplete(false);
/*      */     
/* 2776 */     if ((newPosition < 1) || (newPosition > downloadManagerCount(curCompleted))) {
/* 2777 */       return;
/*      */     }
/*      */     try {
/* 2780 */       this.managers_mon.enter();
/*      */       
/* 2782 */       int curPosition = manager.getPosition();
/* 2783 */       if (newPosition > curPosition)
/*      */       {
/*      */ 
/* 2786 */         int numToMove = newPosition - curPosition;
/* 2787 */         for (int i = 0; i < this.managers_cow.size(); i++) {
/* 2788 */           DownloadManager dm = (DownloadManager)this.managers_cow.get(i);
/* 2789 */           boolean dmCompleted = dm.isDownloadComplete(false);
/* 2790 */           if (dmCompleted == curCompleted) {
/* 2791 */             int dmPosition = dm.getPosition();
/* 2792 */             if ((dmPosition > curPosition) && (dmPosition <= newPosition)) {
/* 2793 */               dm.setPosition(dmPosition - 1);
/* 2794 */               numToMove--;
/* 2795 */               if (numToMove <= 0) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2801 */         manager.setPosition(newPosition);
/*      */       }
/* 2803 */       else if ((newPosition < curPosition) && (curPosition > 1))
/*      */       {
/*      */ 
/* 2806 */         int numToMove = curPosition - newPosition;
/*      */         
/* 2808 */         for (int i = 0; i < this.managers_cow.size(); i++) {
/* 2809 */           DownloadManager dm = (DownloadManager)this.managers_cow.get(i);
/* 2810 */           boolean dmCompleted = dm.isDownloadComplete(false);
/* 2811 */           int dmPosition = dm.getPosition();
/* 2812 */           if ((dmCompleted == curCompleted) && (dmPosition >= newPosition) && (dmPosition < curPosition))
/*      */           {
/*      */ 
/*      */ 
/* 2816 */             dm.setPosition(dmPosition + 1);
/* 2817 */             numToMove--;
/* 2818 */             if (numToMove <= 0)
/*      */               break;
/*      */           }
/*      */         }
/* 2822 */         manager.setPosition(newPosition);
/*      */       }
/*      */     }
/*      */     finally {
/* 2826 */       this.managers_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void fixUpDownloadManagerPositions() {
/*      */     try {
/* 2832 */       this.managers_mon.enter();
/*      */       
/* 2834 */       int posComplete = 1;
/* 2835 */       int posIncomplete = 1;
/*      */       
/* 2837 */       List<DownloadManager> managers_temp = new ArrayList(this.managers_cow);
/*      */       
/* 2839 */       Collections.sort(managers_temp, new Comparator()
/*      */       {
/*      */ 
/*      */         public final int compare(Object a, Object b)
/*      */         {
/* 2844 */           int i = ((DownloadManager)a).getPosition() - ((DownloadManager)b).getPosition();
/* 2845 */           if (i != 0) {
/* 2846 */             return i;
/*      */           }
/*      */           
/*      */ 
/* 2850 */           if (((DownloadManager)a).isPersistent())
/* 2851 */             return 1;
/* 2852 */           if (((DownloadManager)b).isPersistent()) {
/* 2853 */             return -1;
/*      */           }
/*      */           
/* 2856 */           return 0;
/*      */         }
/*      */         
/* 2859 */       });
/* 2860 */       this.managers_cow = managers_temp;
/*      */       
/* 2862 */       for (int i = 0; i < this.managers_cow.size(); i++) {
/* 2863 */         DownloadManager dm = (DownloadManager)this.managers_cow.get(i);
/* 2864 */         if (dm.isDownloadComplete(false)) {
/* 2865 */           dm.setPosition(posComplete++);
/*      */         } else {
/* 2867 */           dm.setPosition(posIncomplete++);
/*      */         }
/*      */       }
/*      */     } finally {
/* 2871 */       this.managers_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getResolverTaggableType()
/*      */   {
/* 2879 */     return 2L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Taggable resolveTaggable(String id)
/*      */   {
/* 2886 */     if (id == null)
/*      */     {
/* 2888 */       return null;
/*      */     }
/*      */     
/* 2891 */     return getDownloadManager(new HashWrapper(Base32.decode(id)));
/*      */   }
/*      */   
/*      */ 
/*      */   public List<Taggable> getResolvedTaggables()
/*      */   {
/* 2897 */     return new ArrayList(getDownloadManagers());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDisplayName(Taggable taggable)
/*      */   {
/* 2905 */     return ((DownloadManager)taggable).getDisplayName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestAttention(String id)
/*      */   {
/* 2913 */     DownloadManager dm = getDownloadManager(new HashWrapper(Base32.decode(id)));
/*      */     
/* 2915 */     if (dm != null)
/*      */     {
/* 2917 */       dm.requestAttention();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void informDestroyed() {
/* 2922 */     if (this.destroyed)
/*      */     {
/* 2924 */       return;
/*      */     }
/*      */     
/* 2927 */     this.destroyed = true;
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
/* 2962 */     this.listeners_and_event_listeners.dispatch(4, null, true);
/*      */   }
/*      */   
/*      */ 
/*      */   public void informDestroyInitiated()
/*      */   {
/* 2968 */     this.listeners_and_event_listeners.dispatch(3, null, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(GlobalManagerListener listener)
/*      */   {
/* 2975 */     addListener(listener, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(GlobalManagerListener listener, boolean trigger)
/*      */   {
/* 2983 */     if (this.isStopping)
/*      */     {
/* 2985 */       listener.destroyed();
/*      */     }
/*      */     else
/*      */     {
/* 2989 */       this.listeners_and_event_listeners.addListener(listener);
/*      */       
/* 2991 */       if (!trigger) {
/* 2992 */         return;
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 2997 */         this.managers_mon.enter();
/*      */         
/* 2999 */         List managers = this.managers_cow;
/*      */         
/* 3001 */         for (int i = 0; i < managers.size(); i++)
/*      */         {
/* 3003 */           listener.downloadManagerAdded((DownloadManager)managers.get(i));
/*      */         }
/*      */       }
/*      */       finally {
/* 3007 */         this.managers_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(GlobalManagerListener listener)
/*      */   {
/* 3016 */     this.listeners_and_event_listeners.removeListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addEventListener(GlobalManagerEventListener listener)
/*      */   {
/* 3023 */     this.listeners_and_event_listeners.addListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeEventListener(GlobalManagerEventListener listener)
/*      */   {
/* 3030 */     this.listeners_and_event_listeners.removeListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void fireGlobalManagerEvent(final int type, final DownloadManager param)
/*      */   {
/* 3039 */     this.listeners_and_event_listeners.dispatch(6, new GlobalManagerEvent()
/*      */     {
/*      */ 
/*      */ 
/*      */       public int getEventType()
/*      */       {
/*      */ 
/* 3046 */         return type;
/*      */       }
/*      */       
/*      */ 
/*      */       public DownloadManager getDownload()
/*      */       {
/* 3052 */         return param;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addDownloadWillBeRemovedListener(GlobalManagerDownloadWillBeRemovedListener l)
/*      */   {
/* 3061 */     this.removal_listeners.addListener(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeDownloadWillBeRemovedListener(GlobalManagerDownloadWillBeRemovedListener l)
/*      */   {
/* 3068 */     this.removal_listeners.removeListener(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void stateChanged(DownloadManager manager, int new_state)
/*      */   {
/* 3077 */     if (this.needsSavingCozStateChanged == 0L)
/*      */     {
/* 3079 */       this.needsSavingCozStateChanged = SystemTime.getMonotonousTime();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3084 */     PEPeerManager pm_manager = manager.getPeerManager();
/*      */     
/* 3086 */     if ((new_state == 50) && (pm_manager != null) && (pm_manager.hasDownloadablePiece()))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3092 */       setSeedingOnlyState(false, false);
/*      */     }
/*      */     else
/*      */     {
/* 3096 */       checkSeedingOnlyState();
/*      */     }
/*      */     
/* 3099 */     checkForceStart((manager.isForceStart()) && (new_state == 50));
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkForceStart(boolean known_to_exist)
/*      */   {
/*      */     boolean exists;
/*      */     
/*      */     boolean exists;
/* 3108 */     if (known_to_exist)
/*      */     {
/* 3110 */       exists = true;
/*      */     }
/*      */     else
/*      */     {
/* 3114 */       exists = false;
/*      */       
/* 3116 */       if (this.force_start_non_seed_exists)
/*      */       {
/* 3118 */         List managers = this.managers_cow;
/*      */         
/* 3120 */         for (int i = 0; i < managers.size(); i++)
/*      */         {
/* 3122 */           DownloadManager dm = (DownloadManager)managers.get(i);
/*      */           
/* 3124 */           if ((dm.isForceStart()) && (dm.getState() == 50))
/*      */           {
/* 3126 */             exists = true;
/*      */             
/* 3128 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3134 */     if (exists != this.force_start_non_seed_exists)
/*      */     {
/* 3136 */       this.force_start_non_seed_exists = exists;
/*      */       
/* 3138 */       Logger.log(new LogEvent(LOGID, "Force start download " + (this.force_start_non_seed_exists ? "exists" : "doesn't exist") + ", modifying download weighting"));
/*      */       
/*      */ 
/*      */ 
/* 3142 */       com.aelitis.azureus.core.peermanager.control.PeerControlSchedulerFactory.overrideWeightedPriorities(this.force_start_non_seed_exists);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkSeedingOnlyState()
/*      */   {
/* 3149 */     this.check_seeding_only_state_dispatcher.dispatch();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkSeedingOnlyStateSupport()
/*      */   {
/* 3155 */     boolean seeding = false;
/* 3156 */     boolean seeding_set = false;
/* 3157 */     boolean potentially_seeding = false;
/*      */     
/* 3159 */     List managers = this.managers_cow;
/*      */     
/* 3161 */     for (int i = 0; i < managers.size(); i++)
/*      */     {
/* 3163 */       DownloadManager dm = (DownloadManager)managers.get(i);
/*      */       
/* 3165 */       PEPeerManager pm = dm.getPeerManager();
/*      */       
/* 3167 */       int state = dm.getState();
/*      */       
/* 3169 */       if ((dm.getDiskManager() == null) || (pm == null))
/*      */       {
/*      */ 
/*      */ 
/* 3173 */         if (state == 75)
/*      */         {
/* 3175 */           if (dm.isDownloadComplete(false))
/*      */           {
/* 3177 */             potentially_seeding = true;
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 3183 */             seeding = false;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 3188 */             seeding_set = true;
/*      */ 
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*      */       }
/* 3196 */       else if (state == 50)
/*      */       {
/* 3198 */         if (!pm.hasDownloadablePiece())
/*      */         {
/*      */ 
/*      */ 
/* 3202 */           if (!seeding_set)
/*      */           {
/* 3204 */             seeding = true;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 3209 */           seeding = false;
/* 3210 */           potentially_seeding = false;
/*      */           
/* 3212 */           break;
/*      */         }
/* 3214 */       } else if (state == 60)
/*      */       {
/* 3216 */         if (!seeding_set)
/*      */         {
/* 3218 */           seeding = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3223 */     if (seeding)
/*      */     {
/* 3225 */       potentially_seeding = true;
/*      */     }
/*      */     
/* 3228 */     setSeedingOnlyState(seeding, potentially_seeding);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setSeedingOnlyState(boolean seeding, boolean potentially_seeding)
/*      */   {
/* 3237 */     synchronized (this)
/*      */     {
/* 3239 */       if ((seeding != this.seeding_only_mode) || (potentially_seeding != this.potentially_seeding_only_mode))
/*      */       {
/*      */ 
/* 3242 */         this.seeding_only_mode = seeding;
/* 3243 */         this.potentially_seeding_only_mode = potentially_seeding;
/*      */         
/*      */ 
/*      */ 
/* 3247 */         this.listeners_and_event_listeners.dispatch(5, new boolean[] { this.seeding_only_mode, this.potentially_seeding_only_mode });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSeedingOnly()
/*      */   {
/* 3255 */     return this.seeding_only_mode;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPotentiallySeedingOnly()
/*      */   {
/* 3261 */     return this.potentially_seeding_only_mode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getTotalSwarmsPeerRate(boolean downloading, boolean seeding)
/*      */   {
/* 3269 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 3271 */     if ((now < this.last_swarm_stats_calc_time) || (now - this.last_swarm_stats_calc_time >= 1000L))
/*      */     {
/*      */ 
/* 3274 */       long total = 0L;
/*      */       
/* 3276 */       List managers = this.managers_cow;
/*      */       
/* 3278 */       for (int i = 0; i < managers.size(); i++)
/*      */       {
/* 3280 */         DownloadManager manager = (DownloadManager)managers.get(i);
/*      */         
/* 3282 */         boolean is_seeding = manager.getState() == 60;
/*      */         
/* 3284 */         if (((downloading) && (!is_seeding)) || ((seeding) && (is_seeding)))
/*      */         {
/*      */ 
/* 3287 */           total += manager.getStats().getTotalAveragePerPeer();
/*      */         }
/*      */       }
/*      */       
/* 3291 */       this.last_swarm_stats = total;
/*      */       
/* 3293 */       this.last_swarm_stats_calc_time = now;
/*      */     }
/*      */     
/* 3296 */     return this.last_swarm_stats;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void computeNATStatus()
/*      */   {
/* 3302 */     int num_ok = 0;
/* 3303 */     int num_probably_ok = 0;
/* 3304 */     int num_bad = 0;
/*      */     
/* 3306 */     for (Iterator it = this.managers_cow.iterator(); it.hasNext();)
/*      */     {
/* 3308 */       DownloadManager manager = (DownloadManager)it.next();
/*      */       
/* 3310 */       int status = manager.getNATStatus();
/*      */       
/* 3312 */       if (status == 1)
/*      */       {
/* 3314 */         num_ok++;
/*      */       }
/* 3316 */       else if (status == 2)
/*      */       {
/* 3318 */         num_probably_ok++;
/*      */       }
/* 3320 */       else if (status == 3)
/*      */       {
/* 3322 */         num_bad++;
/*      */       }
/*      */     }
/*      */     
/* 3326 */     long now = SystemTime.getMonotonousTime();
/*      */     
/* 3328 */     if (num_ok > 0)
/*      */     {
/* 3330 */       this.nat_status = 1;
/*      */       
/* 3332 */       this.nat_status_last_good = now;
/*      */     }
/* 3334 */     else if ((this.nat_status_last_good != -1L) && (now - this.nat_status_last_good < 1800000L))
/*      */     {
/* 3336 */       this.nat_status = 1;
/*      */     }
/* 3338 */     else if ((this.nat_status_last_good != -1L) && (SystemTime.getCurrentTime() - TCPNetworkManager.getSingleton().getLastIncomingNonLocalConnectionTime() < 1800000L))
/*      */     {
/* 3340 */       this.nat_status = 1;
/*      */     }
/* 3342 */     else if ((num_probably_ok > 0) || (this.nat_status_probably_ok))
/*      */     {
/* 3344 */       this.nat_status = 2;
/*      */       
/* 3346 */       this.nat_status_probably_ok = true;
/*      */     }
/* 3348 */     else if (num_bad > 0)
/*      */     {
/* 3350 */       this.nat_status = 3;
/*      */     }
/*      */     else
/*      */     {
/* 3354 */       this.nat_status = 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNATStatus()
/*      */   {
/* 3361 */     return this.nat_status;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void seedPieceRecheck()
/*      */   {
/* 3367 */     List managers = this.managers_cow;
/*      */     
/* 3369 */     if (this.next_seed_piece_recheck_index >= managers.size())
/*      */     {
/* 3371 */       this.next_seed_piece_recheck_index = 0;
/*      */     }
/*      */     
/* 3374 */     for (int i = this.next_seed_piece_recheck_index; i < managers.size(); i++)
/*      */     {
/* 3376 */       DownloadManager manager = (DownloadManager)managers.get(i);
/*      */       
/* 3378 */       if (seedPieceRecheck(manager))
/*      */       {
/* 3380 */         this.next_seed_piece_recheck_index = (i + 1);
/*      */         
/* 3382 */         if (this.next_seed_piece_recheck_index >= managers.size())
/*      */         {
/* 3384 */           this.next_seed_piece_recheck_index = 0;
/*      */         }
/*      */         
/* 3387 */         return;
/*      */       }
/*      */     }
/*      */     
/* 3391 */     for (int i = 0; i < this.next_seed_piece_recheck_index; i++)
/*      */     {
/* 3393 */       DownloadManager manager = (DownloadManager)managers.get(i);
/*      */       
/* 3395 */       if (seedPieceRecheck(manager))
/*      */       {
/* 3397 */         this.next_seed_piece_recheck_index = (i + 1);
/*      */         
/* 3399 */         if (this.next_seed_piece_recheck_index >= managers.size())
/*      */         {
/* 3401 */           this.next_seed_piece_recheck_index = 0;
/*      */         }
/*      */         
/* 3404 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean seedPieceRecheck(DownloadManager manager)
/*      */   {
/* 3413 */     if (manager.getState() != 60)
/*      */     {
/* 3415 */       return false;
/*      */     }
/*      */     
/* 3418 */     return manager.seedPieceRecheck();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DownloadManagerInitialisationAdapter getDMAdapter(DownloadManagerInitialisationAdapter adapter)
/*      */   {
/* 3425 */     List<DownloadManagerInitialisationAdapter> adapters = this.dm_adapters.getList();
/*      */     
/* 3427 */     adapters = new ArrayList(adapters);
/*      */     
/* 3429 */     if (adapter != null)
/*      */     {
/* 3431 */       adapters.add(adapter);
/*      */     }
/*      */     
/* 3434 */     List<DownloadManagerInitialisationAdapter> tag_assigners = new ArrayList();
/* 3435 */     List<DownloadManagerInitialisationAdapter> tag_processors = new ArrayList();
/*      */     
/* 3437 */     for (DownloadManagerInitialisationAdapter a : adapters)
/*      */     {
/* 3439 */       int actions = a.getActions();
/*      */       
/* 3441 */       if ((actions & 0x1) != 0)
/*      */       {
/* 3443 */         tag_assigners.add(a);
/*      */       }
/* 3445 */       if ((actions & 0x2) != 0)
/*      */       {
/* 3447 */         tag_processors.add(a);
/*      */       }
/*      */     }
/*      */     int pos;
/* 3451 */     if ((tag_assigners.size() > 0) && (tag_processors.size() > 0))
/*      */     {
/* 3453 */       for (DownloadManagerInitialisationAdapter a : tag_processors)
/*      */       {
/* 3455 */         adapters.remove(a);
/*      */       }
/*      */       
/* 3458 */       pos = adapters.indexOf(tag_assigners.get(tag_assigners.size() - 1));
/*      */       
/* 3460 */       for (DownloadManagerInitialisationAdapter a : tag_processors)
/*      */       {
/* 3462 */         adapters.add(++pos, a);
/*      */       }
/*      */     }
/*      */     
/* 3466 */     final List<DownloadManagerInitialisationAdapter> f_adapters = adapters;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 3471 */     new DownloadManagerInitialisationAdapter()
/*      */     {
/*      */ 
/*      */       public int getActions()
/*      */       {
/* 3476 */         return 0;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void initialised(DownloadManager manager, boolean for_seeding)
/*      */       {
/* 3484 */         for (int i = 0; i < f_adapters.size(); i++) {
/*      */           try
/*      */           {
/* 3487 */             ((DownloadManagerInitialisationAdapter)f_adapters.get(i)).initialised(manager, for_seeding);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 3491 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/* 3495 */         if (org.gudy.azureus2.core3.util.Constants.isOSX) {
/* 3496 */           GlobalManagerImpl.this.fixLongFileName(manager);
/*      */         }
/*      */         
/* 3499 */         if (COConfigurationManager.getBooleanParameter("Rename Incomplete Files"))
/*      */         {
/* 3501 */           String ext = COConfigurationManager.getStringParameter("Rename Incomplete Files Extension").trim();
/*      */           
/* 3503 */           boolean use_prefix = COConfigurationManager.getBooleanParameter("Use Incomplete File Prefix");
/*      */           
/* 3505 */           DownloadManagerState state = manager.getDownloadState();
/*      */           
/* 3507 */           String existing_ext = state.getAttribute("incompfilesuffix");
/*      */           
/* 3509 */           if ((ext.length() > 0) && (existing_ext == null))
/*      */           {
/* 3511 */             DiskManagerFileInfo[] fileInfos = manager.getDiskManagerFileInfo();
/*      */             
/* 3513 */             if (fileInfos.length <= DownloadManagerStateFactory.MAX_FILES_FOR_INCOMPLETE_AND_DND_LINKAGE)
/*      */             {
/* 3515 */               ext = FileUtil.convertOSSpecificChars(ext, false);
/*      */               
/* 3517 */               String prefix = "";
/*      */               
/* 3519 */               if (use_prefix) {
/*      */                 try
/*      */                 {
/* 3522 */                   prefix = Base32.encode(manager.getTorrent().getHash()).substring(0, 12).toLowerCase(java.util.Locale.US) + "_";
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */               
/*      */ 
/*      */               try
/*      */               {
/* 3530 */                 state.suppressStateSave(true);
/*      */                 
/* 3532 */                 List<Integer> from_indexes = new ArrayList();
/* 3533 */                 List<File> from_links = new ArrayList();
/* 3534 */                 List<File> to_links = new ArrayList();
/*      */                 
/* 3536 */                 for (int i = 0; i < fileInfos.length; i++)
/*      */                 {
/* 3538 */                   DiskManagerFileInfo fileInfo = fileInfos[i];
/*      */                   
/* 3540 */                   File base_file = fileInfo.getFile(false);
/*      */                   
/* 3542 */                   File existing_link = state.getFileLink(i, base_file);
/*      */                   
/* 3544 */                   if ((existing_link != null) || (!base_file.exists()))
/*      */                   {
/*      */ 
/*      */ 
/* 3548 */                     if ((existing_link == null) || (!existing_link.exists()))
/*      */                     {
/*      */                       File new_link;
/*      */                       File new_link;
/* 3552 */                       if (existing_link == null)
/*      */                       {
/* 3554 */                         new_link = new File(base_file.getParentFile(), prefix + base_file.getName() + ext);
/*      */                       }
/*      */                       else
/*      */                       {
/* 3558 */                         String link_name = existing_link.getName();
/*      */                         
/* 3560 */                         if (!link_name.startsWith(prefix))
/*      */                         {
/* 3562 */                           link_name = prefix + link_name;
/*      */                         }
/*      */                         
/* 3565 */                         new_link = new File(existing_link.getParentFile(), link_name + ext);
/*      */                       }
/*      */                       
/* 3568 */                       from_indexes.add(Integer.valueOf(i));
/*      */                       
/* 3570 */                       from_links.add(base_file);
/*      */                       
/* 3572 */                       to_links.add(new_link);
/*      */                     }
/*      */                   }
/*      */                 }
/* 3576 */                 if (from_links.size() > 0)
/*      */                 {
/* 3578 */                   state.setFileLinks(from_indexes, from_links, to_links);
/*      */                 }
/*      */               }
/*      */               finally {
/* 3582 */                 state.setAttribute("incompfilesuffix", ext);
/*      */                 
/* 3584 */                 if (use_prefix)
/*      */                 {
/* 3586 */                   state.setAttribute("dnd_pfx", prefix);
/*      */                 }
/*      */                 
/* 3589 */                 state.suppressStateSave(false);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void fixLongFileName(DownloadManager manager)
/*      */   {
/* 3602 */     DiskManagerFileInfo[] fileInfos = manager.getDiskManagerFileInfo();
/*      */     
/* 3604 */     DownloadManagerState state = manager.getDownloadState();
/*      */     try
/*      */     {
/* 3607 */       state.suppressStateSave(true);
/*      */       
/* 3609 */       for (int i = 0; i < fileInfos.length; i++)
/*      */       {
/* 3611 */         DiskManagerFileInfo fileInfo = fileInfos[i];
/*      */         
/* 3613 */         File base_file = fileInfo.getFile(false);
/*      */         
/* 3615 */         File existing_link = state.getFileLink(i, base_file);
/*      */         
/* 3617 */         if ((existing_link == null) && (!base_file.exists()))
/*      */         {
/* 3619 */           String name = base_file.getName();
/* 3620 */           String ext = FileUtil.getExtension(name);
/* 3621 */           int extLength = ext.length();
/* 3622 */           name = name.substring(0, name.length() - extLength);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3632 */           int origLength = name.length();
/* 3633 */           if (origLength > 50) {
/* 3634 */             File parentFile = base_file.getParentFile();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3640 */             parentFile.mkdirs();
/*      */             
/* 3642 */             File newFile = null;
/* 3643 */             boolean first = true;
/* 3644 */             while (name.length() > 50) {
/*      */               try {
/* 3646 */                 newFile = new File(parentFile, name + ext);
/* 3647 */                 newFile.getCanonicalPath();
/*      */                 
/* 3649 */                 if (first) {
/*      */                   break;
/*      */                 }
/*      */                 
/*      */ 
/* 3654 */                 int fixNameID = 255;
/*      */                 boolean redo;
/*      */                 do {
/* 3657 */                   redo = false;
/* 3658 */                   for (int j = 0; j < i; j++) {
/* 3659 */                     DiskManagerFileInfo convertedFileInfo = fileInfos[j];
/* 3660 */                     if (newFile.equals(convertedFileInfo.getFile(true))) {
/*      */                       do {
/* 3662 */                         fixNameID++;
/* 3663 */                         if (fixNameID >= 4095) {
/*      */                           break;
/*      */                         }
/*      */                         
/* 3667 */                         name = name.substring(0, name.length() - 3) + Integer.toHexString(fixNameID);
/*      */                         
/* 3669 */                         newFile = new File(parentFile, name + ext);
/* 3670 */                       } while (newFile.equals(convertedFileInfo.getFile(true)));
/* 3671 */                       redo = fixNameID <= 4095;
/* 3672 */                       break;
/*      */                     }
/*      */                   }
/* 3675 */                 } while (redo);
/*      */                 
/* 3677 */                 if (fixNameID <= 4095) {
/* 3678 */                   state.setFileLink(i, base_file, newFile);
/*      */                 }
/*      */               }
/*      */               catch (IOException e) {
/* 3682 */                 first = false;
/* 3683 */                 name = name.substring(0, name.length() - 1);
/*      */               } catch (Throwable t) {
/* 3685 */                 Debug.out(t);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3694 */       state.suppressStateSave(false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addDownloadManagerInitialisationAdapter(DownloadManagerInitialisationAdapter adapter)
/*      */   {
/* 3702 */     this.dm_adapters.add(adapter);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeDownloadManagerInitialisationAdapter(DownloadManagerInitialisationAdapter adapter)
/*      */   {
/* 3709 */     this.dm_adapters.remove(adapter);
/*      */   }
/*      */   
/*      */ 
/*      */   public Object getDownloadHistoryManager()
/*      */   {
/* 3715 */     return this.download_history_manager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 3722 */     writer.println("Global Manager");
/*      */     try
/*      */     {
/* 3725 */       writer.indent();
/*      */       
/* 3727 */       this.managers_mon.enter();
/*      */       
/* 3729 */       writer.println("  managers: " + this.managers_cow.size());
/*      */       
/* 3731 */       for (int i = 0; i < this.managers_cow.size(); i++)
/*      */       {
/* 3733 */         DownloadManager manager = (DownloadManager)this.managers_cow.get(i);
/*      */         try
/*      */         {
/* 3736 */           writer.indent();
/*      */           
/* 3738 */           manager.generateEvidence(writer);
/*      */         }
/*      */         finally
/*      */         {
/* 3742 */           writer.exdent();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3748 */       this.managers_mon.exit();
/*      */       
/* 3750 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 3758 */     if (args.length == 0) {
/* 3759 */       args = new String[] { "C:\\temp\\downloads.config", "C:\\temp\\downloads-9-3-05.config", "C:\\temp\\merged.config" };
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/* 3764 */     else if (args.length != 3)
/*      */     {
/* 3766 */       System.out.println("Usage: newer_config_file older_config_file save_config_file");
/*      */       
/* 3768 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 3772 */       Map map1 = FileUtil.readResilientFile(new File(args[0]));
/* 3773 */       Map map2 = FileUtil.readResilientFile(new File(args[1]));
/*      */       
/* 3775 */       List downloads1 = (List)map1.get("downloads");
/* 3776 */       List downloads2 = (List)map2.get("downloads");
/*      */       
/* 3778 */       Set torrents = new HashSet();
/*      */       
/* 3780 */       Iterator it1 = downloads1.iterator();
/*      */       
/* 3782 */       while (it1.hasNext())
/*      */       {
/* 3784 */         Map m = (Map)it1.next();
/*      */         
/* 3786 */         byte[] hash = (byte[])m.get("torrent_hash");
/*      */         
/* 3788 */         System.out.println("1:" + ByteFormatter.nicePrint(hash));
/*      */         
/* 3790 */         torrents.add(new HashWrapper(hash));
/*      */       }
/*      */       
/* 3793 */       List to_add = new ArrayList();
/*      */       
/* 3795 */       Iterator it2 = downloads2.iterator();
/*      */       
/* 3797 */       while (it2.hasNext())
/*      */       {
/* 3799 */         Map m = (Map)it2.next();
/*      */         
/* 3801 */         byte[] hash = (byte[])m.get("torrent_hash");
/*      */         
/* 3803 */         HashWrapper wrapper = new HashWrapper(hash);
/*      */         
/* 3805 */         if (torrents.contains(wrapper))
/*      */         {
/* 3807 */           System.out.println("-:" + ByteFormatter.nicePrint(hash));
/*      */         }
/*      */         else
/*      */         {
/* 3811 */           System.out.println("2:" + ByteFormatter.nicePrint(hash));
/*      */           
/* 3813 */           to_add.add(m);
/*      */         }
/*      */       }
/*      */       
/* 3817 */       downloads1.addAll(to_add);
/*      */       
/* 3819 */       System.out.println(to_add.size() + " copied from " + args[1] + " to " + args[2]);
/*      */       
/* 3821 */       FileUtil.writeResilientFile(new File(args[2]), map1);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3825 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */   public void setMainlineDHTProvider(MainlineDHTProvider provider) {
/* 3830 */     this.provider = provider;
/*      */   }
/*      */   
/*      */   public MainlineDHTProvider getMainlineDHTProvider() {
/* 3834 */     return this.provider;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void statsRequest(Map request, Map reply)
/*      */   {
/* 3842 */     AzureusCore core = com.aelitis.azureus.core.AzureusCoreFactory.getSingleton();
/*      */     
/* 3844 */     Map glob = new HashMap();
/*      */     
/* 3846 */     reply.put("gm", glob);
/*      */     try
/*      */     {
/* 3849 */       glob.put("u_rate", new Long(this.stats.getDataAndProtocolSendRate()));
/* 3850 */       glob.put("d_rate", new Long(this.stats.getDataAndProtocolReceiveRate()));
/*      */       
/* 3852 */       glob.put("d_lim", new Long(TransferSpeedValidator.getGlobalDownloadRateLimitBytesPerSecond()));
/*      */       
/* 3854 */       boolean auto_up = (TransferSpeedValidator.isAutoSpeedActive(this)) && (TransferSpeedValidator.isAutoUploadAvailable(core));
/*      */       
/* 3856 */       glob.put("auto_up", new Long(auto_up ? COConfigurationManager.getLongParameter("Auto Upload Speed Version") : 0L));
/*      */       
/* 3858 */       long up_lim = NetworkManager.getMaxUploadRateBPSNormal();
/*      */       
/* 3860 */       boolean seeding_only = NetworkManager.isSeedingOnlyUploadRate();
/*      */       
/* 3862 */       glob.put("so", new Long(seeding_only ? 1L : 0L));
/*      */       
/* 3864 */       if (seeding_only)
/*      */       {
/* 3866 */         up_lim = NetworkManager.getMaxUploadRateBPSSeedingOnly();
/*      */       }
/*      */       
/* 3869 */       glob.put("u_lim", new Long(up_lim));
/*      */       
/* 3871 */       SpeedManager sm = core.getSpeedManager();
/*      */       
/* 3873 */       if (sm != null)
/*      */       {
/* 3875 */         glob.put("u_cap", new Long(sm.getEstimatedUploadCapacityBytesPerSec().getBytesPerSec()));
/* 3876 */         glob.put("d_cap", new Long(sm.getEstimatedDownloadCapacityBytesPerSec().getBytesPerSec()));
/*      */       }
/*      */       
/* 3879 */       List<DownloadManager> dms = getDownloadManagers();
/*      */       
/* 3881 */       int comp = 0;
/* 3882 */       int incomp = 0;
/*      */       
/* 3884 */       long comp_up = 0L;
/* 3885 */       long incomp_up = 0L;
/* 3886 */       long incomp_down = 0L;
/*      */       
/* 3888 */       for (DownloadManager dm : dms)
/*      */       {
/* 3890 */         int state = dm.getState();
/*      */         
/* 3892 */         if ((state == 60) || (state == 50))
/*      */         {
/* 3894 */           DownloadManagerStats stats = dm.getStats();
/*      */           
/* 3896 */           if (dm.isDownloadComplete(false))
/*      */           {
/* 3898 */             comp++;
/*      */             
/* 3900 */             comp_up += stats.getProtocolSendRate() + stats.getDataSendRate();
/*      */           }
/*      */           else
/*      */           {
/* 3904 */             incomp++;
/*      */             
/* 3906 */             incomp_up += stats.getProtocolSendRate() + stats.getDataSendRate();
/* 3907 */             incomp_down += stats.getProtocolReceiveRate() + stats.getDataReceiveRate();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 3912 */       glob.put("dm_i", new Long(incomp));
/* 3913 */       glob.put("dm_c", new Long(comp));
/*      */       
/* 3915 */       glob.put("dm_i_u", new Long(incomp_up));
/* 3916 */       glob.put("dm_i_d", new Long(incomp_down));
/* 3917 */       glob.put("dm_c_u", new Long(comp_up));
/*      */       
/* 3919 */       glob.put("nat", new Long(this.nat_status));
/*      */       
/* 3921 */       boolean request_limiting = COConfigurationManager.getBooleanParameter("Use Request Limiting");
/*      */       
/* 3923 */       glob.put("req_lim", new Long(request_limiting ? 1L : 0L));
/*      */       
/* 3925 */       if (request_limiting)
/*      */       {
/* 3927 */         glob.put("req_focus", new Long(COConfigurationManager.getBooleanParameter("Use Request Limiting Priorities") ? 1L : 0L));
/*      */       }
/*      */       
/* 3930 */       boolean bias_up = COConfigurationManager.getBooleanParameter("Bias Upload Enable");
/*      */       
/* 3932 */       glob.put("bias_up", new Long(bias_up ? 1L : 0L));
/*      */       
/* 3934 */       if (bias_up)
/*      */       {
/* 3936 */         glob.put("bias_slack", new Long(COConfigurationManager.getLongParameter("Bias Upload Slack KBs")));
/*      */         
/* 3938 */         glob.put("bias_ulim", new Long(COConfigurationManager.getBooleanParameter("Bias Upload Handle No Limit") ? 1L : 0L));
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */   private static class DownloadStateTagger
/*      */     extends TagTypeWithState
/*      */     implements org.gudy.azureus2.core3.download.DownloadManagerListener
/*      */   {
/* 3949 */     private static final int[] color_default = { 41, 140, 165 };
/*      */     
/* 3951 */     private final Object main_tag_key = new Object();
/* 3952 */     private final Object comp_tag_key = new Object();
/*      */     
/*      */     private final TagDownloadWithState tag_initialising;
/*      */     
/*      */     private final TagDownloadWithState tag_downloading;
/*      */     
/*      */     private final TagDownloadWithState tag_seeding;
/*      */     
/*      */     private final TagDownloadWithState tag_queued_downloading;
/*      */     
/*      */     private final TagDownloadWithState tag_queued_seeding;
/*      */     
/*      */     private final TagDownloadWithState tag_stopped;
/*      */     
/*      */     private final TagDownloadWithState tag_error;
/*      */     
/*      */     private final TagDownloadWithState tag_active;
/*      */     private final TagDownloadWithState tag_inactive;
/*      */     private final TagDownloadWithState tag_complete;
/*      */     private final TagDownloadWithState tag_incomplete;
/*      */     private final TagDownloadWithState tag_paused;
/* 3973 */     private int user_mode = -1;
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
/*      */ 
/*      */ 
/*      */     private DownloadStateTagger(GlobalManagerImpl _gm)
/*      */     {
/* 4000 */       super(255, "tag.type.ds");COConfigurationManager.addAndFireParameterListener("User Mode", new ParameterListener()
/*      */       {
/*      */         public void parameterChanged(String parameterName)
/*      */         {
/* 3984 */           int old_mode = GlobalManagerImpl.DownloadStateTagger.this.user_mode;
/*      */           
/* 3986 */           GlobalManagerImpl.DownloadStateTagger.this.user_mode = COConfigurationManager.getIntParameter("User Mode");
/*      */           
/* 3988 */           if (old_mode != -1)
/*      */           {
/* 3990 */             GlobalManagerImpl.DownloadStateTagger.this.fireChanged();
/*      */ 
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 4001 */       });
/* 4002 */       addTagType();
/*      */       
/*      */ 
/*      */ 
/* 4006 */       this.tag_initialising = new MyTag(0, "tag.type.ds.init", false, false, false, false, 0, null);
/* 4007 */       this.tag_downloading = new MyTag(1, "tag.type.ds.down", true, true, true, true, 3, null);
/* 4008 */       this.tag_seeding = new MyTag(2, "tag.type.ds.seed", true, true, false, true, 3, null);
/* 4009 */       this.tag_queued_downloading = new MyTag(3, "tag.type.ds.qford", false, false, false, false, 3, null);
/* 4010 */       this.tag_queued_seeding = new MyTag(4, "tag.type.ds.qfors", false, false, false, false, 3, null);
/* 4011 */       this.tag_stopped = new MyTag(5, "tag.type.ds.stop", false, false, false, false, 8, null);
/* 4012 */       this.tag_error = new MyTag(6, "tag.type.ds.err", false, false, false, false, 0, null);
/* 4013 */       this.tag_active = new MyTag(7, "tag.type.ds.act", true, false, false, false, 3, null);
/* 4014 */       this.tag_paused = new MyTag(8, "tag.type.ds.pau", false, false, false, false, 4, null);
/* 4015 */       this.tag_inactive = new MyTag(9, "tag.type.ds.inact", false, false, false, false, 11, null);
/* 4016 */       this.tag_complete = new MyTag(10, "tag.type.ds.comp", true, true, false, true, 11, null);
/* 4017 */       this.tag_incomplete = new MyTag(11, "tag.type.ds.incomp", true, true, true, true, 11, null);
/*      */       
/* 4019 */       if (this.tag_active.isColorDefault()) {
/* 4020 */         this.tag_active.setColor(new int[] { 96, 160, 96 });
/*      */       }
/*      */       
/* 4023 */       if (this.tag_error.isColorDefault()) {
/* 4024 */         this.tag_error.setColor(new int[] { 132, 16, 58 });
/*      */       }
/*      */       
/* 4027 */       _gm.addListener(new GlobalManagerAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void downloadManagerAdded(DownloadManager dm)
/*      */         {
/*      */ 
/* 4034 */           dm.addListener(GlobalManagerImpl.DownloadStateTagger.this, true);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void downloadManagerRemoved(DownloadManager dm)
/*      */         {
/* 4041 */           dm.removeListener(GlobalManagerImpl.DownloadStateTagger.this);
/*      */           
/* 4043 */           GlobalManagerImpl.DownloadStateTagger.this.remove(dm);
/*      */         }
/*      */         
/* 4046 */       });
/* 4047 */       SimpleTimer.addPeriodicEvent("gm:ds", 10000L, new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/* 4056 */           GlobalManagerImpl.DownloadStateTagger.this.updateActive();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void stateChanged(DownloadManager manager, int state)
/*      */     {
/* 4066 */       if (manager.isDestroyed())
/*      */       {
/* 4068 */         remove(manager);
/*      */         
/* 4070 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 4075 */       Tag old_tag = (Tag)manager.getUserData(this.main_tag_key);
/*      */       
/* 4077 */       boolean complete = manager.isDownloadComplete(false);
/*      */       Tag new_tag;
/* 4079 */       Tag new_tag; switch (state) {
/*      */       case 0: case 5: 
/*      */       case 10: 
/*      */       case 20: 
/*      */       case 30: 
/*      */       case 40: 
/*      */         Tag new_tag;
/* 4086 */         if (old_tag == null) {
/* 4087 */           new_tag = this.tag_initialising;
/*      */         } else {
/* 4089 */           new_tag = old_tag;
/*      */         }
/* 4091 */         break;
/*      */       case 50: 
/*      */       case 55: 
/* 4094 */         new_tag = this.tag_downloading;
/* 4095 */         break;
/*      */       case 60: 
/* 4097 */         new_tag = this.tag_seeding;
/* 4098 */         break;
/*      */       case 65: 
/*      */       case 70: 
/*      */       case 71: 
/* 4102 */         new_tag = this.tag_stopped;
/* 4103 */         break;
/*      */       case 75: 
/* 4105 */         if (complete) {
/* 4106 */           new_tag = this.tag_queued_seeding;
/*      */         } else {
/* 4108 */           new_tag = this.tag_queued_downloading;
/*      */         }
/* 4110 */         break;
/*      */       case 100: 
/*      */       default: 
/* 4113 */         new_tag = this.tag_error;
/*      */       }
/*      */       
/*      */       
/* 4117 */       if (old_tag != new_tag)
/*      */       {
/* 4119 */         if (old_tag != null)
/*      */         {
/* 4121 */           old_tag.removeTaggable(manager);
/*      */         }
/*      */         
/* 4124 */         new_tag.addTaggable(manager);
/*      */         
/* 4126 */         manager.setUserData(this.main_tag_key, new_tag);
/*      */         
/* 4128 */         synchronized (this)
/*      */         {
/* 4130 */           boolean was_inactive = this.tag_inactive.hasTaggable(manager);
/*      */           
/* 4132 */           if ((new_tag != this.tag_seeding) && (new_tag != this.tag_downloading))
/*      */           {
/* 4134 */             this.tag_active.removeTaggable(manager);
/*      */             
/* 4136 */             if (!was_inactive)
/*      */             {
/* 4138 */               this.tag_inactive.addTaggable(manager);
/*      */             }
/*      */           }
/*      */           else {
/* 4142 */             boolean was_active = this.tag_active.hasTaggable(manager);
/*      */             
/* 4144 */             if ((!was_active) && (!was_inactive))
/*      */             {
/* 4146 */               this.tag_inactive.addTaggable(manager);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 4151 */         if ((new_tag == this.tag_stopped) && (manager.isPaused()))
/*      */         {
/* 4153 */           this.tag_paused.addTaggable(manager);
/*      */         }
/* 4155 */         else if (old_tag == this.tag_stopped)
/*      */         {
/* 4157 */           this.tag_paused.removeTaggable(manager);
/*      */         }
/*      */       }
/*      */       
/* 4161 */       Boolean was_complete = (Boolean)manager.getUserData(this.comp_tag_key);
/*      */       
/* 4163 */       if ((was_complete == null) || (was_complete.booleanValue() != complete))
/*      */       {
/* 4165 */         synchronized (this)
/*      */         {
/* 4167 */           if (complete)
/*      */           {
/* 4169 */             if (!this.tag_complete.hasTaggable(manager))
/*      */             {
/* 4171 */               this.tag_complete.addTaggable(manager);
/*      */               
/* 4173 */               this.tag_incomplete.removeTaggable(manager);
/*      */             }
/*      */             
/*      */           }
/* 4177 */           else if (!this.tag_incomplete.hasTaggable(manager))
/*      */           {
/* 4179 */             this.tag_incomplete.addTaggable(manager);
/*      */             
/* 4181 */             this.tag_complete.removeTaggable(manager);
/*      */           }
/*      */           
/*      */ 
/* 4185 */           manager.setUserData(this.comp_tag_key, Boolean.valueOf(complete));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private void updateActive()
/*      */     {
/* 4193 */       synchronized (this)
/*      */       {
/* 4195 */         Set<DownloadManager> active = new HashSet(this.tag_active.getTaggedDownloads());
/*      */         
/* 4197 */         for (TagDownloadWithState tag : new TagDownloadWithState[] { this.tag_downloading, this.tag_seeding })
/*      */         {
/* 4199 */           for (DownloadManager dm : tag.getTaggedDownloads())
/*      */           {
/* 4201 */             DownloadManagerStats stats = dm.getStats();
/*      */             
/* 4203 */             boolean is_active = (stats.getDataReceiveRate() + stats.getDataSendRate() > 0L) && (!dm.isDestroyed());
/*      */             
/*      */ 
/*      */ 
/* 4207 */             if (is_active)
/*      */             {
/* 4209 */               if (!active.remove(dm))
/*      */               {
/* 4211 */                 this.tag_active.addTaggable(dm);
/*      */                 
/* 4213 */                 this.tag_inactive.removeTaggable(dm);
/*      */               }
/*      */               
/* 4216 */               dm.getDownloadState().setLongAttribute("last.act.tag", SystemTime.getCurrentTime());
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 4221 */         for (DownloadManager dm : active)
/*      */         {
/* 4223 */           this.tag_active.removeTaggable(dm);
/*      */           
/* 4225 */           if (!dm.isDestroyed())
/*      */           {
/* 4227 */             this.tag_inactive.addTaggable(dm);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void remove(DownloadManager manager)
/*      */     {
/* 4237 */       Tag old_tag = (Tag)manager.getUserData(this.main_tag_key);
/*      */       
/* 4239 */       if (old_tag != null)
/*      */       {
/* 4241 */         old_tag.removeTaggable(manager);
/*      */       }
/*      */       
/* 4244 */       synchronized (this)
/*      */       {
/* 4246 */         if (this.tag_active.hasTaggable(manager))
/*      */         {
/* 4248 */           this.tag_active.removeTaggable(manager);
/*      */         }
/*      */         else
/*      */         {
/* 4252 */           this.tag_inactive.removeTaggable(manager);
/*      */         }
/*      */         
/* 4255 */         if (this.tag_complete.hasTaggable(manager))
/*      */         {
/* 4257 */           this.tag_complete.removeTaggable(manager);
/*      */         }
/*      */         else
/*      */         {
/* 4261 */           this.tag_incomplete.removeTaggable(manager);
/*      */         }
/*      */         
/* 4264 */         if (this.tag_paused.hasTaggable(manager))
/*      */         {
/* 4266 */           this.tag_paused.removeTaggable(manager);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void downloadComplete(DownloadManager manager) {}
/*      */     
/*      */ 
/*      */ 
/*      */     public void completionChanged(DownloadManager manager, boolean bCompleted)
/*      */     {
/* 4279 */       stateChanged(manager, manager.getState());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void positionChanged(DownloadManager download, int oldPosition, int newPosition) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void filePriorityChanged(DownloadManager download, DiskManagerFileInfo file) {}
/*      */     
/*      */ 
/*      */ 
/*      */     public int[] getColorDefault()
/*      */     {
/* 4296 */       return color_default;
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
/*      */     private class MyTag
/*      */       extends TagDownloadWithState
/*      */     {
/*      */       private MyTag(int tag_id, String name, boolean do_rates, boolean do_up, boolean do_down, boolean do_bytes, int run_states)
/*      */       {
/* 4313 */         super(tag_id, name, do_rates, do_up, do_down, do_bytes, run_states);
/*      */         
/* 4315 */         addTag();
/*      */       }
/*      */       
/*      */ 
/*      */       protected boolean getVisibleDefault()
/*      */       {
/* 4321 */         int id = getTagID();
/*      */         
/* 4323 */         if ((id >= 7) && (id <= 9))
/*      */         {
/* 4325 */           return GlobalManagerImpl.DownloadStateTagger.this.user_mode > 0;
/*      */         }
/*      */         
/*      */ 
/* 4329 */         return id == 7;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       protected boolean getCanBePublicDefault()
/*      */       {
/* 4336 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */       public void removeTag()
/*      */       {
/* 4342 */         throw new RuntimeException("Not Supported");
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/impl/GlobalManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */