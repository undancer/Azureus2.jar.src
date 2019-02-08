/*      */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.average.Average;
/*      */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.LightHashSet;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.Plugin;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginListener;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadActivationEvent;
/*      */ import org.gudy.azureus2.plugins.download.DownloadActivationListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadException;
/*      */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*      */ import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
/*      */ import org.gudy.azureus2.plugins.logging.Logger;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*      */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class StartStopRulesDefaultPlugin
/*      */   implements Plugin, COConfigurationListener, AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   private static final String sStates = " WPRDS.XEQ";
/*      */   public static final int RANK_NONE = 0;
/*      */   public static final int RANK_SPRATIO = 1;
/*      */   public static final int RANK_SEEDCOUNT = 2;
/*      */   public static final int RANK_TIMED = 3;
/*      */   public static final int RANK_PEERCOUNT = 4;
/*      */   private static final int FORCE_CHECK_PERIOD = 60000;
/*      */   private static final int CHECK_FOR_GROSS_CHANGE_PERIOD = 30000;
/*      */   private static final int PROCESS_CHECK_PERIOD = 1500;
/*      */   private static final int MIN_SEEDING_STARTUP_WAIT = 20000;
/*      */   private static final int MIN_FIRST_SCRAPE_WAIT = 90000;
/*      */   private static final float IGNORE_SLOT_THRESHOLD_FACTOR = 0.9F;
/*      */   private static final int MIN_DOWNLOADING_STARTUP_WAIT = 30000;
/*      */   private static final int SMOOTHING_PERIOD_SECS = 15;
/*      */   private static final int SMOOTHING_PERIOD = 15000;
/*      */   private Average globalDownloadSpeedAverage;
/*      */   private AEMonitor this_mon;
/*      */   private PluginInterface pi;
/*      */   protected PluginConfig plugin_config;
/*      */   private org.gudy.azureus2.plugins.download.DownloadManager download_manager;
/*      */   protected LoggerChannel log;
/*      */   private RecalcSeedingRanksTask recalcSeedingRanksTask;
/*  138 */   private static Map<Download, DefaultRankCalculator> downloadDataMap = Collections.synchronizedMap(new HashMap());
/*      */   
/*      */ 
/*      */   private volatile DefaultRankCalculator[] sortedArrayCache;
/*      */   
/*      */ 
/*      */   private volatile boolean closingDown;
/*      */   
/*      */ 
/*      */   private volatile boolean somethingChanged;
/*      */   
/*      */ 
/*      */   private Set ranksToRecalc;
/*      */   
/*      */ 
/*      */   private AEMonitor ranksToRecalc_mon;
/*      */   
/*      */ 
/*      */   private long monoStartedOn;
/*      */   
/*      */ 
/*      */   protected boolean bDebugLog;
/*      */   
/*      */ 
/*      */   private int iRankType;
/*      */   
/*      */   private int minSpeedForActiveSeeding;
/*      */   
/*      */   private int maxStalledSeeding;
/*      */   
/*      */   private int numPeersAsFullCopy;
/*      */   
/*      */   private int iFakeFullCopySeedStart;
/*      */   
/*      */   private int _maxActive;
/*      */   
/*      */   private boolean _maxActiveWhenSeedingEnabled;
/*      */   
/*      */   private int _maxActiveWhenSeeding;
/*      */   
/*      */   private int globalDownloadLimit;
/*      */   
/*      */   private int globalUploadLimit;
/*      */   
/*      */   private int globalUploadWhenSeedingLimit;
/*      */   
/*      */   private int maxConfiguredDownloads;
/*      */   
/*      */   private boolean bMaxDownloadIgnoreChecking;
/*      */   
/*      */   private int minDownloads;
/*      */   
/*      */   private boolean bAutoReposition;
/*      */   
/*      */   private long minTimeAlive;
/*      */   
/*      */   private boolean bAutoStart0Peers;
/*      */   
/*      */   private boolean bStopOnceBandwidthMet;
/*      */   
/*      */   private boolean bStartNoMoreSeedsWhenUpLimitMet;
/*      */   
/*      */   private boolean bStartNoMoreSeedsWhenUpLimitMetPercent;
/*      */   
/*      */   private int bStartNoMoreSeedsWhenUpLimitMetSlack;
/*      */   
/*      */   private int iDownloadSortType;
/*      */   
/*      */   private int iDownloadTestTimeMillis;
/*      */   
/*      */   private int iDownloadReTestMillis;
/*      */   
/*  210 */   private static boolean bAlreadyInitialized = false;
/*      */   
/*      */ 
/*      */   private TableColumn seedingRankColumn;
/*      */   
/*      */ 
/*      */   private TableContextMenuItem debugMenuItem;
/*      */   
/*      */   private UIAdapter swt_ui;
/*      */   
/*      */   private CopyOnWriteList listenersFP;
/*      */   
/*  222 */   public static boolean pauseChangeFlagChecker = false;
/*      */   private volatile boolean immediateProcessingScheduled;
/*      */   private long changeCheckCount;
/*      */   private long changeCheckTotalMS;
/*      */   
/*      */   public static void load(PluginInterface plugin_interface) {
/*  228 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  229 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "Start/Stop Rules");
/*      */   }
/*      */   
/*      */   public void initialize(PluginInterface _plugin_interface) {
/*  233 */     if (bAlreadyInitialized) {
/*  234 */       System.err.println("StartStopRulesDefaultPlugin Already initialized!!");
/*      */     } else {
/*  236 */       bAlreadyInitialized = true;
/*      */     }
/*      */     
/*  239 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  241 */     this.monoStartedOn = SystemTime.getMonotonousTime();
/*      */     
/*  243 */     this.pi = _plugin_interface;
/*      */     
/*  245 */     this.plugin_config = this.pi.getPluginconfig();
/*      */     
/*  247 */     this.plugin_config.setPluginConfigKeyPrefix("");
/*      */     
/*  249 */     this.download_manager = this.pi.getDownloadManager();
/*      */     
/*      */ 
/*      */ 
/*  253 */     UIManager manager = this.pi.getUIManager();
/*      */     
/*      */ 
/*  256 */     final BasicPluginConfigModel configModel = manager.createBasicPluginConfigModel("root", "Q");
/*      */     
/*  258 */     setupConfigModel(configModel);
/*      */     
/*  260 */     this.pi.addListener(new PluginListener() {
/*      */       public void initializationComplete() {}
/*      */       
/*      */       public void closedownInitiated() {
/*  264 */         StartStopRulesDefaultPlugin.this.closingDown = true;
/*      */         
/*      */ 
/*      */ 
/*  268 */         COConfigurationManager.removeListener(StartStopRulesDefaultPlugin.this);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void closedownComplete() {}
/*  274 */     });
/*  275 */     Runnable r = new Runnable() {
/*      */       public void run() {
/*  277 */         StartStopRulesDefaultPlugin.this.download_manager.addListener(new StartStopRulesDefaultPlugin.StartStopDMListener(StartStopRulesDefaultPlugin.this));
/*  278 */         SimpleTimer.addPeriodicEvent("StartStop:gross", 30000L, new StartStopRulesDefaultPlugin.ChangeCheckerTimerTask(StartStopRulesDefaultPlugin.this, null));
/*      */         
/*  280 */         SimpleTimer.addPeriodicEvent("StartStop:check", 1500L, new StartStopRulesDefaultPlugin.ChangeFlagCheckerTask(StartStopRulesDefaultPlugin.this, null));
/*      */       }
/*      */       
/*      */ 
/*  284 */     };
/*  285 */     this.pi.getUtilities().createDelayedTask(r).queue();
/*      */     
/*  287 */     this.log = this.pi.getLogger().getTimeStampedChannel("StartStopRules");
/*  288 */     this.log.log(1, "Default StartStopRules Plugin Initialisation");
/*      */     
/*      */ 
/*  291 */     COConfigurationManager.addListener(this);
/*      */     try
/*      */     {
/*  294 */       this.pi.getUIManager().createLoggingViewModel(this.log, true);
/*  295 */       this.pi.getUIManager().addUIListener(new UIManagerListener() {
/*      */         public void UIAttached(UIInstance instance) {
/*  297 */           TableManager tm = StartStopRulesDefaultPlugin.this.pi.getUIManager().getTableManager();
/*      */           
/*  299 */           StartStopRulesDefaultPlugin.this.seedingRankColumn = tm.createColumn("MySeeders", "SeedingRank");
/*      */           
/*  301 */           StartStopRulesDefaultPlugin.this.seedingRankColumn.initialize(2, -2, 80, -2);
/*      */           
/*      */ 
/*  304 */           TableCellRefreshListener columnListener = new SeedingRankColumnListener(StartStopRulesDefaultPlugin.downloadDataMap, StartStopRulesDefaultPlugin.this.plugin_config);
/*      */           
/*  306 */           StartStopRulesDefaultPlugin.this.seedingRankColumn.addCellRefreshListener(columnListener);
/*      */           
/*  308 */           tm.addColumn(StartStopRulesDefaultPlugin.this.seedingRankColumn);
/*      */           
/*  310 */           TableColumn downloadingRankColumn = tm.createColumn("MyTorrents", "DownloadingRank");
/*      */           
/*      */ 
/*  313 */           downloadingRankColumn.setMinimumRequiredUserMode(1);
/*      */           
/*  315 */           downloadingRankColumn.initialize(2, -1, 80, -2);
/*      */           
/*      */ 
/*  318 */           columnListener = new DownloadingRankColumnListener(StartStopRulesDefaultPlugin.this);
/*      */           
/*  320 */           downloadingRankColumn.addCellRefreshListener(columnListener);
/*      */           
/*  322 */           tm.addColumn(downloadingRankColumn);
/*      */           
/*  324 */           if (instance.getUIType() == 1)
/*      */           {
/*      */ 
/*      */ 
/*  328 */             configModel.destroy();
/*      */             try
/*      */             {
/*  331 */               StartStopRulesDefaultPlugin.this.swt_ui = ((StartStopRulesDefaultPlugin.UIAdapter)Class.forName("com.aelitis.azureus.plugins.startstoprules.defaultplugin.ui.swt.StartStopRulesDefaultPluginSWTUI").getConstructor(new Class[] { PluginInterface.class }).newInstance(new Object[] { StartStopRulesDefaultPlugin.this.pi }));
/*      */ 
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*      */ 
/*  337 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */         public void UIDetached(UIInstance instance) {}
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  347 */       Debug.printStackTrace(e);
/*      */     }
/*  349 */     reloadConfigParams();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setupConfigModel(BasicPluginConfigModel configModel)
/*      */   {
/*  357 */     String PREFIX_RES = "ConfigView.label.seeding.";
/*      */     
/*  359 */     configModel.addIntParameter2("StartStopManager_iRankType", "ConfigView.label.seeding.rankType", 1);
/*      */     
/*      */ 
/*      */ 
/*  363 */     configModel.addIntParameter2("StartStopManager_iRankTypeSeedFallback", "ConfigView.label.seeding.rankType.seed.fallback", 0);
/*      */     
/*  365 */     configModel.addIntParameter2("StartStopManager_iTimed_MinSeedingTimeWithPeers", "ConfigView.label.seeding.rankType.timed.minTimeWithPeers", 0);
/*      */     
/*      */ 
/*  368 */     configModel.addBooleanParameter2("StartStopManager_bAutoReposition", "ConfigView.label.seeding.autoReposition", false);
/*      */     
/*  370 */     configModel.addIntParameter2("StartStopManager_iMinSeedingTime", "ConfigView.label.minSeedingTime", 600);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  375 */     configModel.addBooleanParameter2("StartStopManager_bIgnore0Peers", "ConfigView.label.seeding.ignore0Peers", true);
/*      */     
/*  377 */     configModel.addIntParameter2("StartStopManager_iIgnoreSeedCount", "ConfigView.label.ignoreSeeds", 0);
/*      */     
/*      */ 
/*      */ 
/*  381 */     configModel.addIntParameter2("StartStopManager_iIgnoreRatioPeersSeedStart", "ConfigView.label.seeding.fakeFullCopySeedStart", 0);
/*      */     
/*      */ 
/*      */ 
/*  385 */     configModel.addIntParameter2("StartStopManager_iIgnoreShareRatioSeedStart", "ConfigView.label.seeding.fakeFullCopySeedStart", 0);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  390 */     configModel.addBooleanParameter2("StartStopManager_bPreferLargerSwarms", "ConfigView.label.seeding.preferLargerSwarms", true);
/*      */     
/*  392 */     configModel.addBooleanParameter2("StartStopManager_bAutoStart0Peers", "ConfigView.label.seeding.autoStart0Peers", false);
/*      */     
/*  394 */     configModel.addIntParameter2("StartStopManager_iMinPeersToBoostNoSeeds", "ConfigView.label.minPeersToBoostNoSeeds", 1);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  400 */     configModel.addBooleanParameter2("StartStopManager_bMaxDownloadIgnoreChecking", "ConfigView.label.ignoreChecking", false);
/*      */     
/*      */ 
/*  403 */     configModel.addIntParameter2("StartStopManager_iMinSpeedForActiveDL", "ConfigView.label.minSpeedForActiveDL", 512);
/*      */     
/*  405 */     configModel.addIntParameter2("StartStopManager_iMinSpeedForActiveSeeding", "ConfigView.label.minSpeedForActiveSeeding", 512);
/*      */     
/*  407 */     configModel.addIntParameter2("StartStopManager_iMaxStalledSeeding", "ConfigView.label.maxStalledSeeding", 5);
/*      */     
/*      */ 
/*      */ 
/*  411 */     configModel.addBooleanParameter2("StartStopManager_bDebugLog", "ConfigView.label.queue.debuglog", false);
/*      */     
/*  413 */     configModel.addBooleanParameter2("StartStopManager_bNewSeedsMoveTop", "ConfigView.label.queue.newseedsmovetop", true);
/*      */     
/*  415 */     configModel.addBooleanParameter2("StartStopManager_bRetainForceStartWhenComplete", "ConfigView.label.queue.retainforce", false);
/*      */     
/*      */ 
/*  418 */     configModel.addIntParameter2("StartStopManager_iMaxActiveTorrentsWhenSeeding", "ConfigView.label.queue.maxactivetorrentswhenseeding", 0);
/*      */     
/*      */ 
/*  421 */     configModel.addBooleanParameter2("StartStopManager_bMaxActiveTorrentsWhenSeedingEnabled", "ConfigView.label.queue.maxactivetorrentswhenseeding", false);
/*      */     
/*      */ 
/*      */ 
/*  425 */     configModel.addBooleanParameter2("StartStopManager_bStopOnceBandwidthMet", "ConfigView.label.queue.stoponcebandwidthmet", true);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  431 */     configModel.addIntParameter2("StartStopManager_iFirstPriority_Type", "ConfigView.label.seeding.firstPriority", 1);
/*      */     
/*      */ 
/*      */ 
/*  435 */     configModel.addIntParameter2("StartStopManager_iFirstPriority_ShareRatio", "ConfigView.label.seeding.firstPriority.shareRatio", 500);
/*      */     
/*      */ 
/*  438 */     configModel.addIntParameter2("StartStopManager_iFirstPriority_SeedingMinutes", "ConfigView.label.seeding.firstPriority.seedingMinutes", 0);
/*      */     
/*      */ 
/*      */ 
/*  442 */     configModel.addIntParameter2("StartStopManager_iFirstPriority_DLMinutes", "ConfigView.label.seeding.firstPriority.DLMinutes", 0);
/*      */     
/*      */ 
/*      */ 
/*  446 */     configModel.addIntParameter2("StartStopManager_iFirstPriority_ignoreSPRatio", "ConfigView.label.seeding.firstPriority.ignoreSPRatio", 0);
/*      */     
/*      */ 
/*      */ 
/*  450 */     configModel.addBooleanParameter2("StartStopManager_bFirstPriority_ignore0Peer", "ConfigView.label.seeding.firstPriority.ignore0Peer", !COConfigurationManager.getStringParameter("ui", "").equals("az2"));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  455 */     configModel.addIntParameter2("StartStopManager_iFirstPriority_ignoreIdleHours", "ConfigView.label.seeding.firstPriority.ignoreIdleHours", 24);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  461 */     configModel.addIntParameter2("StartStopManager_iAddForSeedingDLCopyCount", "ConfigView.label.seeding.addForSeedingDLCopyCount", 1);
/*      */     
/*      */ 
/*  464 */     configModel.addIntParameter2("StartStopManager_iNumPeersAsFullCopy", PREFIX_RES + "numPeersAsFullCopy", 0);
/*      */     
/*  466 */     configModel.addIntParameter2("StartStopManager_iFakeFullCopySeedStart", PREFIX_RES + "fakeFullCopySeedStart", 1);
/*      */     
/*      */ 
/*  469 */     configModel.addBooleanParameter2("StartStopManager_bStartNoMoreSeedsWhenUpLimitMet", "ConfigView.label.seeding.StartStopManager_bStartNoMoreSeedsWhenUpLimitMet", false);
/*      */     
/*      */ 
/*  472 */     configModel.addBooleanParameter2("StartStopManager_bStartNoMoreSeedsWhenUpLimitMetPercent", "ConfigView.label.seeding.bStartNoMoreSeedsWhenUpLimitMetPercent", true);
/*      */     
/*      */ 
/*  475 */     configModel.addIntParameter2("StartStopManager_bStartNoMoreSeedsWhenUpLimitMetSlack", "ConfigView.label.seeding.bStartNoMoreSeedsWhenUpLimitMetSlack", 95);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  480 */     PREFIX_RES = "ConfigView.label.downloading.";
/*      */     
/*  482 */     configModel.addIntParameter2("StartStopManager_Downloading_iSortType", "ConfigView.label.downloading.autoReposition", 0);
/*      */     
/*      */ 
/*  485 */     configModel.addIntParameter2("StartStopManager_Downloading_iTestTimeSecs", PREFIX_RES + "testTime", 120);
/*      */     
/*      */ 
/*  488 */     configModel.addIntParameter2("StartStopManager_Downloading_iRetestTimeMins", PREFIX_RES + "reTest", 30);
/*      */     
/*      */ 
/*  491 */     configModel.destroy();
/*      */   }
/*      */   
/*      */   public static DefaultRankCalculator getRankCalculator(Download dl) {
/*  495 */     return (DefaultRankCalculator)downloadDataMap.get(dl);
/*      */   }
/*      */   
/*      */   private void recalcAllSeedingRanks(boolean force) {
/*  499 */     if (this.closingDown) {
/*  500 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  504 */       this.this_mon.enter();
/*      */       
/*      */       DefaultRankCalculator[] dlDataArray;
/*  507 */       synchronized (downloadDataMap) {
/*  508 */         dlDataArray = (DefaultRankCalculator[])downloadDataMap.values().toArray(new DefaultRankCalculator[0]);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  513 */       for (int i = 0; i < dlDataArray.length; i++) {
/*  514 */         if (force)
/*  515 */           dlDataArray[i].getDownloadObject().setSeedingRank(0);
/*  516 */         dlDataArray[i].recalcSeedingRank();
/*      */       }
/*      */     }
/*      */     finally {
/*  520 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private class RecalcSeedingRanksTask implements TimerEventPerformer
/*      */   {
/*      */     private RecalcSeedingRanksTask() {}
/*      */     
/*  528 */     boolean bCancel = false;
/*      */     
/*      */     public void perform(TimerEvent event) {
/*  531 */       if (this.bCancel) {
/*  532 */         event.cancel();
/*  533 */         return;
/*      */       }
/*      */       
/*  536 */       StartStopRulesDefaultPlugin.this.recalcAllSeedingRanks(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void cancel()
/*      */     {
/*  543 */       this.bCancel = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private class ChangeFlagCheckerTask
/*      */     implements TimerEventPerformer
/*      */   {
/*  553 */     final long FORCE_CHECK_CYCLES = 40L;
/*      */     
/*  555 */     final DownloadManagerStats dmStats = StartStopRulesDefaultPlugin.this.download_manager.getStats();
/*      */     
/*  557 */     long prevReceived = -1L;
/*      */     
/*  559 */     long cycleNo = 0L;
/*      */     
/*      */     private ChangeFlagCheckerTask() {}
/*      */     
/*  563 */     public void perform(TimerEvent event) { long recv = this.dmStats.getDataBytesReceived() + this.dmStats.getProtocolBytesReceived();
/*      */       
/*  565 */       if (this.prevReceived != -1L)
/*      */       {
/*  567 */         StartStopRulesDefaultPlugin.this.globalDownloadSpeedAverage.update(recv - this.prevReceived);
/*      */       }
/*      */       
/*  570 */       this.prevReceived = recv;
/*      */       
/*  572 */       if ((StartStopRulesDefaultPlugin.this.closingDown) || (StartStopRulesDefaultPlugin.pauseChangeFlagChecker)) {
/*  573 */         return;
/*      */       }
/*      */       
/*  576 */       this.cycleNo += 1L;
/*  577 */       if (this.cycleNo > 40L) {
/*  578 */         if (StartStopRulesDefaultPlugin.this.bDebugLog) {
/*  579 */           StartStopRulesDefaultPlugin.this.log.log(1, ">>force process");
/*      */         }
/*  581 */         StartStopRulesDefaultPlugin.this.somethingChanged = true;
/*      */       }
/*      */       
/*  584 */       if (StartStopRulesDefaultPlugin.this.somethingChanged) {
/*      */         try {
/*  586 */           this.cycleNo = 0L;
/*  587 */           StartStopRulesDefaultPlugin.this.process();
/*      */         } catch (Exception e) {
/*  589 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private class StartStopDownloadListener
/*      */     implements DownloadListener
/*      */   {
/*      */     private StartStopDownloadListener() {}
/*      */     
/*      */     public void stateChanged(Download download, int old_state, int new_state)
/*      */     {
/*  602 */       DefaultRankCalculator dlData = (DefaultRankCalculator)StartStopRulesDefaultPlugin.downloadDataMap.get(download);
/*      */       
/*  604 */       if (dlData != null)
/*      */       {
/*  606 */         StartStopRulesDefaultPlugin.this.requestProcessCycle(dlData);
/*  607 */         if ((new_state == 3) || (new_state == 1)) {
/*  608 */           if (StartStopRulesDefaultPlugin.this.immediateProcessingScheduled) {
/*  609 */             StartStopRulesDefaultPlugin.this.requestProcessCycle(dlData);
/*      */           } else {
/*  611 */             StartStopRulesDefaultPlugin.this.immediateProcessingScheduled = true;
/*  612 */             new AEThread2("processReady", true) {
/*      */               public void run() {
/*  614 */                 StartStopRulesDefaultPlugin.this.process();
/*      */               }
/*      */             }.start();
/*      */           }
/*      */         }
/*      */         
/*  620 */         if (StartStopRulesDefaultPlugin.this.bDebugLog) {
/*  621 */           StartStopRulesDefaultPlugin.this.log.log(dlData.dl.getTorrent(), 1, "somethingChanged: stateChange from " + " WPRDS.XEQ".charAt(old_state) + " (" + old_state + ") to " + " WPRDS.XEQ".charAt(new_state) + " (" + new_state + ")");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void positionChanged(Download download, int oldPosition, int newPosition)
/*      */     {
/*  630 */       DefaultRankCalculator dlData = (DefaultRankCalculator)StartStopRulesDefaultPlugin.downloadDataMap.get(download);
/*  631 */       if (dlData != null) {
/*  632 */         StartStopRulesDefaultPlugin.this.requestProcessCycle(dlData);
/*  633 */         if (StartStopRulesDefaultPlugin.this.bDebugLog) {
/*  634 */           StartStopRulesDefaultPlugin.this.log.log(dlData.dl.getTorrent(), 1, "somethingChanged: positionChanged from " + oldPosition + " to " + newPosition);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private class StartStopDMTrackerListener implements DownloadTrackerListener
/*      */   {
/*      */     private StartStopDMTrackerListener() {}
/*      */     
/*      */     public void scrapeResult(DownloadScrapeResult result)
/*      */     {
/*  646 */       Download dl = result.getDownload();
/*  647 */       if (dl == null) {
/*  648 */         return;
/*      */       }
/*      */       
/*  651 */       DefaultRankCalculator dlData = (DefaultRankCalculator)StartStopRulesDefaultPlugin.downloadDataMap.get(dl);
/*      */       
/*      */ 
/*      */ 
/*  655 */       if (result.getResponseType() == 2) {
/*  656 */         if (StartStopRulesDefaultPlugin.this.bDebugLog) {
/*  657 */           StartStopRulesDefaultPlugin.this.log.log(dl.getTorrent(), 1, "Ignored somethingChanged: new scrapeResult (RT_ERROR)");
/*      */         }
/*  659 */         if (dlData != null)
/*  660 */           dlData.lastScrapeResultOk = false;
/*  661 */         return;
/*      */       }
/*      */       
/*  664 */       if (dlData != null) {
/*  665 */         dlData.lastScrapeResultOk = true;
/*  666 */         StartStopRulesDefaultPlugin.this.requestProcessCycle(dlData);
/*  667 */         if (StartStopRulesDefaultPlugin.this.bDebugLog) {
/*  668 */           StartStopRulesDefaultPlugin.this.log.log(dl.getTorrent(), 1, "somethingChanged: new scrapeResult S:" + result.getSeedCount() + ";P:" + result.getNonSeedCount());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void announceResult(DownloadAnnounceResult result) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class StartStopDownloadActivationListener
/*      */     implements DownloadActivationListener
/*      */   {
/*      */     private StartStopDownloadActivationListener() {}
/*      */     
/*      */ 
/*      */     public boolean activationRequested(DownloadActivationEvent event)
/*      */     {
/*  688 */       Download download = event.getDownload();
/*  689 */       DefaultRankCalculator dlData = (DefaultRankCalculator)StartStopRulesDefaultPlugin.downloadDataMap.get(download);
/*      */       
/*  691 */       if (StartStopRulesDefaultPlugin.this.bDebugLog) {
/*  692 */         StartStopRulesDefaultPlugin.this.log.log(download, 1, ">> somethingChanged: ActivationRequest");
/*      */       }
/*      */       
/*      */ 
/*  696 */       StartStopRulesDefaultPlugin.this.requestProcessCycle(dlData);
/*      */       
/*  698 */       if (download.isComplete())
/*      */       {
/*      */ 
/*  701 */         DownloadScrapeResult sr = event.getDownload().getAggregatedScrapeResult();
/*  702 */         int numPeers = sr.getNonSeedCount();
/*  703 */         if (numPeers <= 0) {
/*  704 */           return true;
/*      */         }
/*      */       }
/*      */       
/*  708 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class StartStopDMListener
/*      */     implements DownloadManagerListener
/*      */   {
/*      */     private DownloadTrackerListener download_tracker_listener;
/*      */     
/*      */     private DownloadListener download_listener;
/*      */     
/*      */     private DownloadActivationListener download_activation_listener;
/*      */     
/*      */     public StartStopDMListener()
/*      */     {
/*  724 */       this.download_tracker_listener = new StartStopRulesDefaultPlugin.StartStopDMTrackerListener(StartStopRulesDefaultPlugin.this, null);
/*  725 */       this.download_listener = new StartStopRulesDefaultPlugin.StartStopDownloadListener(StartStopRulesDefaultPlugin.this, null);
/*  726 */       this.download_activation_listener = new StartStopRulesDefaultPlugin.StartStopDownloadActivationListener(StartStopRulesDefaultPlugin.this, null);
/*      */     }
/*      */     
/*      */     public void downloadAdded(Download download) {
/*  730 */       DefaultRankCalculator dlData = null;
/*  731 */       if (StartStopRulesDefaultPlugin.downloadDataMap.containsKey(download)) {
/*  732 */         dlData = (DefaultRankCalculator)StartStopRulesDefaultPlugin.downloadDataMap.get(download);
/*      */       } else {
/*  734 */         dlData = new DefaultRankCalculator(StartStopRulesDefaultPlugin.this, download);
/*      */         
/*  736 */         StartStopRulesDefaultPlugin.this.sortedArrayCache = null;
/*  737 */         StartStopRulesDefaultPlugin.downloadDataMap.put(download, dlData);
/*  738 */         download.addListener(this.download_listener);
/*  739 */         download.addTrackerListener(this.download_tracker_listener, false);
/*  740 */         download.addActivationListener(this.download_activation_listener);
/*      */       }
/*      */       
/*  743 */       if (dlData != null) {
/*  744 */         StartStopRulesDefaultPlugin.this.requestProcessCycle(dlData);
/*  745 */         if (StartStopRulesDefaultPlugin.this.bDebugLog) {
/*  746 */           StartStopRulesDefaultPlugin.this.log.log(download.getTorrent(), 1, "somethingChanged: downloadAdded, state: " + " WPRDS.XEQ".charAt(download.getState()));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public void downloadRemoved(Download download)
/*      */     {
/*  753 */       download.removeListener(this.download_listener);
/*  754 */       download.removeTrackerListener(this.download_tracker_listener);
/*  755 */       download.removeActivationListener(this.download_activation_listener);
/*      */       
/*  757 */       DefaultRankCalculator dlData = (DefaultRankCalculator)StartStopRulesDefaultPlugin.downloadDataMap.remove(download);
/*  758 */       if (dlData != null) {
/*  759 */         StartStopRulesDefaultPlugin.this.sortedArrayCache = null;
/*  760 */         dlData.destroy();
/*      */       }
/*      */       
/*  763 */       StartStopRulesDefaultPlugin.this.requestProcessCycle(null);
/*  764 */       if (StartStopRulesDefaultPlugin.this.bDebugLog) {
/*  765 */         StartStopRulesDefaultPlugin.this.log.log(download.getTorrent(), 1, "somethingChanged: downloadRemoved");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class ChangeCheckerTimerTask
/*      */     implements TimerEventPerformer
/*      */   {
/*      */     private ChangeCheckerTimerTask() {}
/*      */     
/*      */ 
/*  778 */     long lLastRunTime = 0L;
/*      */     
/*      */     public void perform(TimerEvent event) {
/*  781 */       long now = 0L;
/*      */       
/*      */       try
/*      */       {
/*  785 */         StartStopRulesDefaultPlugin.this.this_mon.enter();
/*      */         
/*  787 */         now = SystemTime.getCurrentTime();
/*      */         
/*      */ 
/*  790 */         if ((now > this.lLastRunTime) && (now - this.lLastRunTime < 1000L))
/*      */         {
/*      */           long timeTaken;
/*      */           return;
/*      */         }
/*  795 */         this.lLastRunTime = now;
/*      */         
/*      */         DefaultRankCalculator[] dlDataArray;
/*  798 */         synchronized (StartStopRulesDefaultPlugin.downloadDataMap) {
/*  799 */           dlDataArray = (DefaultRankCalculator[])StartStopRulesDefaultPlugin.downloadDataMap.values().toArray(new DefaultRankCalculator[0]);
/*      */         }
/*      */         
/*      */ 
/*  803 */         int iNumDLing = 0;
/*  804 */         int iNumCDing = 0;
/*  805 */         for (int i = 0; i < dlDataArray.length; i++) {
/*  806 */           if (dlDataArray[i].changeChecker()) {
/*  807 */             StartStopRulesDefaultPlugin.this.requestProcessCycle(dlDataArray[i]);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  812 */           if (dlDataArray[i].getActivelyDownloading()) {
/*  813 */             iNumDLing++;
/*      */           }
/*      */           
/*      */ 
/*  817 */           if (dlDataArray[i].getActivelySeeding()) {
/*  818 */             iNumCDing++;
/*      */           }
/*      */         }
/*      */         
/*  822 */         int iMaxSeeders = StartStopRulesDefaultPlugin.this.calcMaxSeeders(iNumDLing);
/*  823 */         if (iNumCDing > iMaxSeeders) {
/*  824 */           StartStopRulesDefaultPlugin.this.requestProcessCycle(null);
/*  825 */           if (StartStopRulesDefaultPlugin.this.bDebugLog) {
/*  826 */             StartStopRulesDefaultPlugin.this.log.log(1, "somethingChanged: More Seeding than limit");
/*      */           }
/*      */         }
/*      */       } finally {
/*      */         long timeTaken;
/*  831 */         if (now > 0L) {
/*  832 */           StartStopRulesDefaultPlugin.access$1808(StartStopRulesDefaultPlugin.this);
/*  833 */           long timeTaken = SystemTime.getCurrentTime() - now;
/*  834 */           StartStopRulesDefaultPlugin.access$1914(StartStopRulesDefaultPlugin.this, timeTaken);
/*  835 */           if (timeTaken > StartStopRulesDefaultPlugin.this.changeCheckMaxMS) {
/*  836 */             StartStopRulesDefaultPlugin.this.changeCheckMaxMS = timeTaken;
/*      */           }
/*      */         }
/*      */         
/*  840 */         StartStopRulesDefaultPlugin.this.this_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void configurationSaved()
/*      */   {
/*  847 */     new AEThread2("reloadConfigParams", true)
/*      */     {
/*      */       public void run() {
/*  850 */         StartStopRulesDefaultPlugin.this.reloadConfigParams();
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */   private void reloadConfigParams() {
/*      */     try {
/*  857 */       this.this_mon.enter();
/*      */       
/*  859 */       int iNewRankType = this.plugin_config.getIntParameter("StartStopManager_iRankType");
/*  860 */       this.minSpeedForActiveSeeding = this.plugin_config.getIntParameter("StartStopManager_iMinSpeedForActiveSeeding");
/*  861 */       this.maxStalledSeeding = this.plugin_config.getIntParameter("StartStopManager_iMaxStalledSeeding");
/*  862 */       if (this.maxStalledSeeding <= 0)
/*      */       {
/*  864 */         this.maxStalledSeeding = 999;
/*      */       }
/*  866 */       this._maxActive = this.plugin_config.getIntParameter("max active torrents");
/*  867 */       this._maxActiveWhenSeedingEnabled = this.plugin_config.getBooleanParameter("StartStopManager_bMaxActiveTorrentsWhenSeedingEnabled");
/*  868 */       this._maxActiveWhenSeeding = this.plugin_config.getIntParameter("StartStopManager_iMaxActiveTorrentsWhenSeeding");
/*      */       
/*  870 */       this.minDownloads = this.plugin_config.getIntParameter("min downloads");
/*  871 */       this.maxConfiguredDownloads = this.plugin_config.getIntParameter("max downloads");
/*  872 */       this.bMaxDownloadIgnoreChecking = this.plugin_config.getBooleanParameter("StartStopManager_bMaxDownloadIgnoreChecking");
/*      */       
/*  874 */       this.numPeersAsFullCopy = this.plugin_config.getIntParameter("StartStopManager_iNumPeersAsFullCopy");
/*  875 */       this.iFakeFullCopySeedStart = this.plugin_config.getIntParameter("StartStopManager_iFakeFullCopySeedStart");
/*  876 */       this.bAutoReposition = this.plugin_config.getBooleanParameter("StartStopManager_bAutoReposition");
/*  877 */       this.minTimeAlive = (this.plugin_config.getIntParameter("StartStopManager_iMinSeedingTime") * 1000);
/*  878 */       this.bDebugLog = this.plugin_config.getBooleanParameter("StartStopManager_bDebugLog");
/*      */       
/*  880 */       this.bAutoStart0Peers = this.plugin_config.getBooleanParameter("StartStopManager_bAutoStart0Peers");
/*      */       
/*      */ 
/*  883 */       this.globalDownloadLimit = this.plugin_config.getIntParameter("Max Download Speed KBs", 0);
/*  884 */       this.globalUploadLimit = this.plugin_config.getIntParameter("Max Upload Speed KBs", 0);
/*  885 */       this.globalUploadWhenSeedingLimit = (this.plugin_config.getBooleanParameter("enable.seedingonly.upload.rate") ? this.plugin_config.getIntParameter("Max Upload Speed Seeding KBs", 0) : this.globalUploadLimit);
/*      */       
/*  887 */       this.bStopOnceBandwidthMet = this.plugin_config.getBooleanParameter("StartStopManager_bStopOnceBandwidthMet");
/*      */       
/*  889 */       this.bStartNoMoreSeedsWhenUpLimitMet = this.plugin_config.getBooleanParameter("StartStopManager_bStartNoMoreSeedsWhenUpLimitMet");
/*  890 */       this.bStartNoMoreSeedsWhenUpLimitMetPercent = this.plugin_config.getBooleanParameter("StartStopManager_bStartNoMoreSeedsWhenUpLimitMetPercent");
/*  891 */       this.bStartNoMoreSeedsWhenUpLimitMetSlack = this.plugin_config.getIntParameter("StartStopManager_bStartNoMoreSeedsWhenUpLimitMetSlack");
/*      */       
/*  893 */       boolean move_top = this.plugin_config.getBooleanParameter("StartStopManager_bNewSeedsMoveTop");
/*  894 */       this.plugin_config.setBooleanParameter("Newly Seeding Torrents Get First Priority", move_top);
/*      */       
/*      */ 
/*  897 */       if (iNewRankType != this.iRankType) {
/*  898 */         this.iRankType = iNewRankType;
/*      */         
/*      */ 
/*  901 */         if (this.iRankType == 3) {
/*  902 */           if (this.recalcSeedingRanksTask == null) {
/*  903 */             recalcAllSeedingRanks(false);
/*  904 */             this.recalcSeedingRanksTask = new RecalcSeedingRanksTask(null);
/*  905 */             SimpleTimer.addPeriodicEvent("StartStop:recalcSR", 1000L, this.recalcSeedingRanksTask);
/*      */           }
/*      */         }
/*  908 */         else if (this.recalcSeedingRanksTask != null) {
/*  909 */           this.recalcSeedingRanksTask.cancel();
/*  910 */           this.recalcSeedingRanksTask = null;
/*      */         }
/*      */       }
/*      */       
/*  914 */       this.iDownloadSortType = this.plugin_config.getIntParameter("StartStopManager_Downloading_iSortType", -1);
/*      */       
/*  916 */       if (this.iDownloadSortType == -1)
/*      */       {
/*  918 */         boolean bDownloadAutoReposition = this.plugin_config.getBooleanParameter("StartStopManager_Downloading_bAutoReposition");
/*      */         
/*  920 */         this.iDownloadSortType = (bDownloadAutoReposition ? 2 : 0);
/*      */         
/*  922 */         this.plugin_config.setIntParameter("StartStopManager_Downloading_iSortType", this.iDownloadSortType);
/*      */       }
/*      */       
/*  925 */       this.iDownloadTestTimeMillis = (this.plugin_config.getIntParameter("StartStopManager_Downloading_iTestTimeSecs") * 1000);
/*  926 */       this.iDownloadReTestMillis = (this.plugin_config.getIntParameter("StartStopManager_Downloading_iRetestTimeMins") * 60 * 1000);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  954 */       Collection<DefaultRankCalculator> allDownloads = downloadDataMap.values();
/*  955 */       DefaultRankCalculator[] dlDataArray = (DefaultRankCalculator[])allDownloads.toArray(new DefaultRankCalculator[0]);
/*  956 */       for (int i = 0; i < dlDataArray.length; i++) {
/*  957 */         dlDataArray[i].getDownloadObject().setSeedingRank(0);
/*      */       }
/*      */       try {
/*  960 */         this.ranksToRecalc_mon.enter();
/*      */         
/*  962 */         synchronized (downloadDataMap) {
/*  963 */           this.ranksToRecalc.addAll(allDownloads);
/*      */         }
/*      */       }
/*      */       finally {
/*  967 */         this.ranksToRecalc_mon.exit();
/*      */       }
/*  969 */       requestProcessCycle(null);
/*      */       
/*  971 */       if (this.bDebugLog) {
/*  972 */         this.log.log(1, "somethingChanged: config reload");
/*      */         try {
/*  974 */           if (this.debugMenuItem == null) {
/*  975 */             String DEBUG_MENU_ID = "StartStopRules.menu.viewDebug";
/*  976 */             Object menuListener = new MenuItemListener() {
/*      */               public void selected(MenuItem menu, Object target) {
/*  978 */                 if (!(target instanceof TableRow)) {
/*  979 */                   return;
/*      */                 }
/*  981 */                 TableRow tr = (TableRow)target;
/*  982 */                 Object ds = tr.getDataSource();
/*      */                 
/*  984 */                 if (!(ds instanceof Download)) {
/*  985 */                   return;
/*      */                 }
/*  987 */                 DefaultRankCalculator dlData = (DefaultRankCalculator)StartStopRulesDefaultPlugin.downloadDataMap.get(ds);
/*      */                 
/*  989 */                 if (dlData != null) {
/*  990 */                   if (StartStopRulesDefaultPlugin.this.swt_ui != null) {
/*  991 */                     StartStopRulesDefaultPlugin.this.swt_ui.openDebugWindow(dlData);
/*      */                   } else {
/*  993 */                     StartStopRulesDefaultPlugin.this.pi.getUIManager().showTextMessage(null, null, "FP:\n" + dlData.sExplainFP + "\n" + "SR:" + dlData.sExplainSR + "\n" + "TRACE:\n" + dlData.sTrace);
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */ 
/* 1001 */             };
/* 1002 */             TableManager tm = this.pi.getUIManager().getTableManager();
/*      */             
/* 1004 */             this.debugMenuItem = tm.addContextMenuItem("MySeeders", "StartStopRules.menu.viewDebug");
/*      */             
/* 1006 */             this.debugMenuItem.addListener((MenuItemListener)menuListener);
/* 1007 */             this.debugMenuItem = tm.addContextMenuItem("MyTorrents", "StartStopRules.menu.viewDebug");
/*      */             
/* 1009 */             this.debugMenuItem.addListener((MenuItemListener)menuListener);
/*      */           }
/*      */         } catch (Throwable t) {
/* 1012 */           Debug.printStackTrace(t);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1017 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private int calcMaxSeeders(int iDLs)
/*      */   {
/* 1023 */     int maxActive = getMaxActive();
/* 1024 */     if (maxActive == 0) {
/* 1025 */       return 999999;
/*      */     }
/* 1027 */     return maxActive - iDLs;
/*      */   }
/*      */   
/*      */   protected int getMaxActive() {
/* 1031 */     if (!this._maxActiveWhenSeedingEnabled) {
/* 1032 */       return this._maxActive;
/*      */     }
/* 1034 */     if (this.download_manager.isSeedingOnly())
/*      */     {
/* 1036 */       if (this._maxActiveWhenSeeding <= this._maxActive) {
/* 1037 */         return this._maxActiveWhenSeeding;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1042 */       Download[] downloads = this.download_manager.getDownloads();
/*      */       
/* 1044 */       boolean danger = false;
/*      */       
/* 1046 */       for (int i = 0; (i < downloads.length) && (!danger); i++)
/*      */       {
/* 1048 */         Download download = downloads[i];
/*      */         
/* 1050 */         int state = download.getState();
/*      */         
/* 1052 */         if ((state != 4) && (state != 5) && (state != 7) && (state != 6) && (state != 8))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1062 */           DiskManagerFileInfo[] files = download.getDiskManagerFileInfo();
/*      */           
/* 1064 */           for (int j = 0; j < files.length; j++)
/*      */           {
/* 1066 */             DiskManagerFileInfo file = files[j];
/*      */             
/* 1068 */             if ((!file.isSkipped()) && (file.getDownloaded() != file.getLength()))
/*      */             {
/* 1070 */               danger = true;
/*      */               
/* 1072 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1078 */       if (!danger) {
/* 1079 */         return this._maxActiveWhenSeeding;
/*      */       }
/*      */     }
/* 1082 */     return this._maxActive;
/*      */   }
/*      */   
/*      */ 
/*      */   private class TotalsStats
/*      */   {
/* 1088 */     int forcedSeeding = 0;
/*      */     
/* 1090 */     int forcedSeedingNonFP = 0;
/*      */     
/* 1092 */     int waitingToSeed = 0;
/*      */     
/* 1094 */     int waitingToDL = 0;
/*      */     
/* 1096 */     int downloading = 0;
/*      */     
/* 1098 */     int activelyDLing = 0;
/*      */     
/* 1100 */     int activelyCDing = 0;
/*      */     
/* 1102 */     int complete = 0;
/*      */     
/* 1104 */     int incompleteQueued = 0;
/*      */     
/* 1106 */     int firstPriority = 0;
/*      */     
/* 1108 */     int stalledSeeders = 0;
/*      */     
/* 1110 */     int stalledFPSeeders = 0;
/*      */     
/* 1112 */     int forcedActive = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean bOkToStartSeeding;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     int maxSeeders;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     int maxActive;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     int maxTorrents;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean upLimitProhibitsNewSeeds;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public int maxUploadSpeed()
/*      */     {
/* 1144 */       return this.downloading == 0 ? StartStopRulesDefaultPlugin.this.globalUploadWhenSeedingLimit : StartStopRulesDefaultPlugin.this.globalUploadLimit;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public TotalsStats(DefaultRankCalculator[] dlDataArray)
/*      */     {
/* 1154 */       this.bOkToStartSeeding = ((StartStopRulesDefaultPlugin.this.iRankType == 0) || (StartStopRulesDefaultPlugin.this.iRankType == 3) || (SystemTime.getMonotonousTime() - StartStopRulesDefaultPlugin.this.monoStartedOn > 90000L));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1159 */       int totalOKScrapes = 0;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1164 */       for (int i = 0; i < dlDataArray.length; i++) {
/* 1165 */         DefaultRankCalculator dlData = dlDataArray[i];
/* 1166 */         if (dlData != null)
/*      */         {
/*      */ 
/*      */ 
/* 1170 */           Download download = dlData.getDownloadObject();
/* 1171 */           int state = download.getState();
/*      */           
/*      */ 
/* 1174 */           if ((state != 8) && (state != 7))
/*      */           {
/*      */ 
/*      */ 
/* 1178 */             boolean completed = download.isComplete();
/* 1179 */             boolean bIsFirstP = false;
/*      */             
/*      */ 
/*      */ 
/* 1183 */             if ((completed) || (!download.isForceStart()))
/*      */             {
/*      */ 
/* 1186 */               if (completed)
/*      */               {
/* 1188 */                 boolean bScrapeOk = true;
/* 1189 */                 if (!this.bOkToStartSeeding) {
/* 1190 */                   bScrapeOk = StartStopRulesDefaultPlugin.this.scrapeResultOk(download);
/* 1191 */                   if ((StartStopRulesDefaultPlugin.this.calcSeedsNoUs(download, download.getAggregatedScrapeResult()) == 0) && (bScrapeOk)) {
/* 1192 */                     this.bOkToStartSeeding = true;
/* 1193 */                   } else if ((download.getSeedingRank() > 0) && ((state == 9) || (state == 3)) && (SystemTime.getMonotonousTime() - StartStopRulesDefaultPlugin.this.monoStartedOn > 20000L))
/*      */                   {
/*      */ 
/* 1196 */                     this.bOkToStartSeeding = true;
/*      */                   }
/*      */                 }
/* 1199 */                 this.complete += 1;
/*      */                 
/* 1201 */                 if ((!this.bOkToStartSeeding) && (bScrapeOk)) {
/* 1202 */                   totalOKScrapes++;
/*      */                 }
/* 1204 */                 if (dlData.isFirstPriority()) {
/* 1205 */                   if (!this.bOkToStartSeeding) {
/* 1206 */                     this.bOkToStartSeeding = true;
/*      */                   }
/* 1208 */                   this.firstPriority += 1;
/* 1209 */                   bIsFirstP = true;
/*      */                 }
/*      */                 
/* 1212 */                 if (dlData.getActivelySeeding()) {
/* 1213 */                   if (dlData.isForceActive()) {
/* 1214 */                     this.forcedActive += 1;
/*      */                   }
/*      */                   
/* 1217 */                   this.activelyCDing += 1;
/* 1218 */                   if (download.isForceStart()) {
/* 1219 */                     this.forcedSeeding += 1;
/* 1220 */                     if (!bIsFirstP)
/* 1221 */                       this.forcedSeedingNonFP += 1;
/*      */                   }
/* 1223 */                 } else if (state == 5) {
/* 1224 */                   if (bIsFirstP) {
/* 1225 */                     this.stalledFPSeeders += 1;
/*      */                   }
/*      */                   
/* 1228 */                   this.stalledSeeders += 1;
/*      */                 }
/*      */                 
/* 1231 */                 if ((state == 3) || (state == 1) || (state == 2))
/*      */                 {
/* 1233 */                   this.waitingToSeed += 1;
/*      */                 }
/*      */               }
/*      */               else {
/* 1237 */                 if (state == 4) {
/* 1238 */                   this.downloading += 1;
/*      */                   
/* 1240 */                   if (dlData.getActivelyDownloading()) {
/* 1241 */                     this.activelyDLing += 1;
/*      */                   }
/*      */                 }
/* 1244 */                 if ((state == 3) || (state == 1) || (state == 2))
/*      */                 {
/* 1246 */                   this.waitingToDL += 1;
/* 1247 */                 } else if (state == 9)
/* 1248 */                   this.incompleteQueued += 1;
/*      */               } }
/*      */           }
/*      */         }
/*      */       }
/* 1253 */       if ((!this.bOkToStartSeeding) && (totalOKScrapes == this.complete)) {
/* 1254 */         this.bOkToStartSeeding = true;
/*      */       }
/* 1256 */       this.maxSeeders = StartStopRulesDefaultPlugin.this.calcMaxSeeders(this.activelyDLing + this.waitingToDL);
/* 1257 */       this.maxActive = StartStopRulesDefaultPlugin.this.getMaxActive();
/*      */       
/* 1259 */       if (this.maxActive == 0) {
/* 1260 */         this.maxTorrents = 9999;
/* 1261 */       } else if (maxUploadSpeed() == 0) {
/* 1262 */         this.maxTorrents = (this.maxActive + 4);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1268 */         int minSpeedPerActive = StartStopRulesDefaultPlugin.this.minSpeedForActiveSeeding * 2 / 1024;
/*      */         
/*      */ 
/* 1271 */         if (minSpeedPerActive < 3)
/* 1272 */           minSpeedPerActive = 3;
/* 1273 */         this.maxTorrents = (maxUploadSpeed() / minSpeedPerActive);
/*      */         
/*      */ 
/* 1276 */         if (this.maxTorrents < this.maxActive) {
/* 1277 */           this.maxTorrents = this.maxActive;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1284 */       long up_limit = maxUploadSpeed();
/*      */       
/* 1286 */       if ((StartStopRulesDefaultPlugin.this.bStartNoMoreSeedsWhenUpLimitMet) && (up_limit > 0L))
/*      */       {
/* 1288 */         long current_up_kbps = StartStopRulesDefaultPlugin.this.download_manager.getStats().getSmoothedSendRate() / 1024L;
/*      */         
/*      */         long target;
/*      */         long target;
/* 1292 */         if (StartStopRulesDefaultPlugin.this.bStartNoMoreSeedsWhenUpLimitMetPercent)
/*      */         {
/* 1294 */           target = up_limit * StartStopRulesDefaultPlugin.this.bStartNoMoreSeedsWhenUpLimitMetSlack / 100L;
/*      */         }
/*      */         else
/*      */         {
/* 1298 */           target = up_limit - StartStopRulesDefaultPlugin.this.bStartNoMoreSeedsWhenUpLimitMetSlack;
/*      */         }
/*      */         
/* 1301 */         if (current_up_kbps > target)
/*      */         {
/* 1303 */           this.upLimitProhibitsNewSeeds = true;
/*      */         }
/*      */       }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void process()
/*      */   {
/* 1358 */     long now = 0L;
/*      */     try {
/* 1360 */       this.this_mon.enter();
/*      */       
/* 1362 */       now = SystemTime.getCurrentTime();
/*      */       
/* 1364 */       this.somethingChanged = false;
/*      */       Object[] recalcArray;
/*      */       try {
/* 1367 */         this.ranksToRecalc_mon.enter();
/*      */         
/* 1369 */         recalcArray = this.ranksToRecalc.toArray();
/* 1370 */         this.ranksToRecalc.clear();
/*      */       } finally {
/* 1372 */         this.ranksToRecalc_mon.exit();
/*      */       }
/* 1374 */       for (int i = 0; i < recalcArray.length; i++) {
/* 1375 */         DefaultRankCalculator rankObj = (DefaultRankCalculator)recalcArray[i];
/* 1376 */         if (this.bDebugLog) {
/* 1377 */           long oldSR = rankObj.dl.getSeedingRank();
/* 1378 */           rankObj.recalcSeedingRank();
/* 1379 */           String s = "recalc seeding rank.  old/new=" + oldSR + "/" + rankObj.dl.getSeedingRank();
/*      */           
/* 1381 */           this.log.log(rankObj.dl.getTorrent(), 1, s);
/*      */         } else {
/* 1383 */           rankObj.recalcSeedingRank();
/*      */         }
/*      */       }
/* 1386 */       this.processTotalRecalcs += recalcArray.length;
/* 1387 */       if (recalcArray.length == 0) {
/* 1388 */         this.processTotalZeroRecalcs += 1L;
/*      */       }
/*      */       
/*      */       DefaultRankCalculator[] dlDataArray;
/*      */       DefaultRankCalculator[] dlDataArray;
/* 1393 */       if ((this.sortedArrayCache != null) && (this.sortedArrayCache.length == downloadDataMap.size())) {
/* 1394 */         dlDataArray = this.sortedArrayCache;
/*      */       } else {
/* 1396 */         synchronized (downloadDataMap) {
/* 1397 */           dlDataArray = this.sortedArrayCache = (DefaultRankCalculator[])downloadDataMap.values().toArray(new DefaultRankCalculator[downloadDataMap.size()]);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1402 */       TotalsStats totals = new TotalsStats(dlDataArray);
/*      */       
/* 1404 */       String[] mainDebugEntries = null;
/* 1405 */       if (this.bDebugLog) {
/* 1406 */         this.log.log(1, ">>process()");
/* 1407 */         mainDebugEntries = new String[] { "ok2Start=" + boolDebug(totals.bOkToStartSeeding), "tFrcdCding=" + totals.forcedSeeding, "actvCDs=" + totals.activelyCDing, "tW8tingToCd=" + totals.waitingToSeed, "tDLing=" + totals.downloading, "actvDLs=" + totals.activelyDLing, "tW8tingToDL=" + totals.waitingToDL, "tCom=" + totals.complete, "tIncQd=" + totals.incompleteQueued, "mxCdrs=" + totals.maxSeeders, "tFP=" + totals.firstPriority, "maxT=" + totals.maxTorrents, "maxA=" + totals.maxActive };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1425 */       Arrays.sort(dlDataArray);
/*      */       
/* 1427 */       ProcessVars vars = new ProcessVars(null);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1432 */       vars.numWaitingOrSeeding = totals.forcedSeeding;
/* 1433 */       vars.numWaitingOrDLing = 0;
/* 1434 */       vars.higherCDtoStart = false;
/* 1435 */       vars.higherDLtoStart = false;
/* 1436 */       vars.posComplete = 0;
/* 1437 */       vars.stalledSeeders = 0;
/*      */       
/* 1439 */       List<DefaultRankCalculator> incompleteDownloads = new ArrayList();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1444 */       for (int i = 0; i < dlDataArray.length; i++) {
/* 1445 */         DefaultRankCalculator dlData = dlDataArray[i];
/* 1446 */         Download download = dlData.getDownloadObject();
/* 1447 */         vars.bStopAndQueued = false;
/* 1448 */         dlData.sTrace = "";
/*      */         
/*      */ 
/* 1451 */         if (download.getState() == 1) {
/*      */           try {
/* 1453 */             download.initialize();
/*      */             
/* 1455 */             String s = "initialize: state is waiting";
/* 1456 */             this.log.log(download.getTorrent(), 1, s);
/*      */           }
/*      */           catch (Exception ignore) {}
/*      */           
/* 1460 */           if ((this.bDebugLog) && (download.getState() == 1)) {
/* 1461 */             dlData.sTrace += "still in waiting state after initialize!\n";
/*      */           }
/*      */         }
/*      */         
/* 1465 */         if ((this.bAutoReposition) && (this.iRankType != 0) && (download.isComplete()) && ((totals.bOkToStartSeeding) || (totals.firstPriority > 0)))
/*      */         {
/*      */ 
/* 1468 */           download.setPosition(++vars.posComplete);
/*      */         }
/* 1470 */         int state = download.getState();
/*      */         
/*      */ 
/* 1473 */         if ((state != 6) && (state != 7) && (state != 8))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1478 */           if (download.isForceStart()) {
/* 1479 */             if ((state == 7) || (state == 9)) {
/*      */               try {
/* 1481 */                 download.restart();
/* 1482 */                 String s = "restart: isForceStart";
/* 1483 */                 this.log.log(download.getTorrent(), 1, s); DefaultRankCalculator 
/* 1484 */                   tmp1059_1057 = dlData;tmp1059_1057.sTrace = (tmp1059_1057.sTrace + s + "\n");
/*      */               }
/*      */               catch (DownloadException e) {}
/*      */               
/* 1488 */               state = download.getState();
/*      */             }
/*      */             
/* 1491 */             if (state == 3) {
/*      */               try {
/* 1493 */                 download.start();
/* 1494 */                 String s = "Start: isForceStart";
/* 1495 */                 this.log.log(download.getTorrent(), 1, s); DefaultRankCalculator 
/* 1496 */                   tmp1143_1141 = dlData;tmp1143_1141.sTrace = (tmp1143_1141.sTrace + s + "\n");
/*      */               }
/*      */               catch (DownloadException e) {}
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1504 */           if (!download.isComplete()) {
/* 1505 */             incompleteDownloads.add(dlData);
/* 1506 */             handleInCompleteDownload(dlData, vars, totals);
/*      */           } else {
/* 1508 */             handleCompletedDownload(dlDataArray, dlData, vars, totals);
/*      */           }
/*      */         }
/*      */       }
/* 1512 */       processDownloadingRules(incompleteDownloads);
/*      */       
/* 1514 */       if (this.bDebugLog) {
/* 1515 */         String[] mainDebugEntries2 = { "ok2Start=" + boolDebug(totals.bOkToStartSeeding), "tFrcdCding=" + totals.forcedSeeding, "actvCDs=" + totals.activelyCDing, "tW8tingToCd=" + totals.waitingToSeed, "tDLing=" + totals.downloading, "actvDLs=" + totals.activelyDLing, "tW8tingToDL=" + totals.waitingToDL, "tCom=" + totals.complete, "tIncQd=" + totals.incompleteQueued, "mxCdrs=" + totals.maxSeeders, "tFP=" + totals.firstPriority, "maxT=" + totals.maxTorrents, "maxA=" + totals.maxActive };
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1530 */         printDebugChanges("<<process() ", mainDebugEntries, mainDebugEntries2, "", "", true, null);
/*      */       }
/*      */     } finally {
/*      */       long timeTaken;
/* 1534 */       if (now > 0L) {
/* 1535 */         this.processCount += 1L;
/* 1536 */         long timeTaken = SystemTime.getCurrentTime() - now;
/* 1537 */         if (this.bDebugLog) {
/* 1538 */           this.log.log(1, "process() took " + timeTaken);
/*      */         }
/* 1540 */         this.processTotalMS += timeTaken;
/* 1541 */         if (timeTaken > this.processMaxMS) {
/* 1542 */           this.processMaxMS = timeTaken;
/*      */         }
/* 1544 */         if (this.processLastComplete > 0L) {
/* 1545 */           this.processTotalGap += now - this.processLastComplete;
/*      */         }
/* 1547 */         this.processLastComplete = now;
/*      */       }
/*      */       
/* 1550 */       this.immediateProcessingScheduled = false;
/*      */       
/* 1552 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void processDownloadingRules(List<DefaultRankCalculator> downloads)
/*      */   {
/* 1563 */     long mono_now = SystemTime.getMonotonousTime();
/*      */     
/* 1565 */     if (mono_now - this.monoStartedOn < 30000L)
/*      */     {
/* 1567 */       return;
/*      */     }
/*      */     
/* 1570 */     if (this.iDownloadSortType != 2)
/*      */     {
/*      */ 
/*      */ 
/* 1574 */       if (this.dlr_current_active != null)
/*      */       {
/* 1576 */         this.dlr_current_active.setDLRInactive();
/*      */         
/* 1578 */         this.dlr_current_active = null;
/*      */       }
/*      */     }
/*      */     
/* 1582 */     if (this.iDownloadSortType == 0)
/*      */     {
/*      */ 
/*      */ 
/* 1586 */       return;
/*      */     }
/* 1588 */     if (this.iDownloadSortType == 1)
/*      */     {
/* 1590 */       Collections.sort(downloads, new Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(DefaultRankCalculator d1, DefaultRankCalculator d2)
/*      */         {
/*      */ 
/*      */ 
/* 1599 */           DownloadScrapeResult s1 = d1.getDownloadObject().getAggregatedScrapeResult();
/* 1600 */           DownloadScrapeResult s2 = d2.getDownloadObject().getAggregatedScrapeResult();
/*      */           
/* 1602 */           int result = s2.getSeedCount() - s1.getSeedCount();
/*      */           
/* 1604 */           if (result == 0)
/*      */           {
/* 1606 */             result = s2.getNonSeedCount() - s1.getNonSeedCount();
/*      */           }
/*      */           
/* 1609 */           return result;
/*      */         }
/*      */       });
/*      */       
/* 1613 */       for (int i = 0; i < downloads.size(); i++)
/*      */       {
/* 1615 */         DefaultRankCalculator drc = (DefaultRankCalculator)downloads.get(i);
/*      */         
/* 1617 */         if (drc.dl.getPosition() != i + 1)
/*      */         {
/* 1619 */           drc.dl.moveTo(i + 1);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1626 */       if (this.dlr_current_active != null)
/*      */       {
/* 1628 */         if (!downloads.contains(this.dlr_current_active))
/*      */         {
/* 1630 */           this.dlr_current_active.setDLRInactive();
/*      */           
/* 1632 */           this.dlr_current_active = null;
/*      */         }
/*      */       }
/*      */       
/* 1636 */       if (downloads.size() < 2)
/*      */       {
/* 1638 */         return;
/*      */       }
/*      */       
/* 1641 */       if (this.globalDownloadLimit > 0)
/*      */       {
/* 1643 */         int downloadKBSec = (int)(this.globalDownloadSpeedAverage.getAverage() * 1000.0D / 1500.0D / 1024.0D);
/*      */         
/* 1645 */         if (this.globalDownloadLimit - downloadKBSec < 5)
/*      */         {
/* 1647 */           if (this.dlr_max_rate_time == 0L)
/*      */           {
/* 1649 */             this.dlr_max_rate_time = mono_now;
/*      */           }
/* 1651 */           else if (mono_now - this.dlr_max_rate_time >= 60000L)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1656 */             if (this.dlr_current_active != null)
/*      */             {
/* 1658 */               this.dlr_current_active.setDLRInactive();
/*      */               
/* 1660 */               this.dlr_current_active = null;
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*      */         else {
/* 1667 */           this.dlr_max_rate_time = 0L;
/*      */         }
/*      */       }
/*      */       else {
/* 1671 */         this.dlr_max_rate_time = 0L;
/*      */       }
/*      */       
/* 1674 */       if (this.dlr_current_active != null)
/*      */       {
/* 1676 */         long last_test = this.dlr_current_active.getDLRLastTestTime();
/*      */         
/* 1678 */         long tested_ago = mono_now - last_test;
/*      */         
/* 1680 */         if (tested_ago < this.iDownloadTestTimeMillis)
/*      */         {
/* 1682 */           return;
/*      */         }
/*      */         
/* 1685 */         this.dlr_current_active.setDLRComplete(mono_now);
/*      */         
/* 1687 */         this.dlr_current_active = null;
/*      */       }
/*      */       
/* 1690 */       if (this.dlr_current_active == null)
/*      */       {
/* 1692 */         DefaultRankCalculator to_test = null;
/*      */         
/* 1694 */         long oldest_test = 0L;
/*      */         
/*      */ 
/*      */ 
/* 1698 */         long adjustedReTest = this.iDownloadReTestMillis + this.iDownloadTestTimeMillis * downloads.size();
/*      */         
/*      */ 
/*      */ 
/* 1702 */         for (DefaultRankCalculator drc : downloads)
/*      */         {
/* 1704 */           if (drc.isQueued())
/*      */           {
/* 1706 */             long last_test = drc.getDLRLastTestTime();
/*      */             
/* 1708 */             if (last_test == 0L)
/*      */             {
/*      */ 
/*      */ 
/* 1712 */               to_test = drc;
/*      */               
/* 1714 */               break;
/*      */             }
/*      */             
/*      */ 
/* 1718 */             if (this.iDownloadReTestMillis > 0)
/*      */             {
/*      */ 
/*      */ 
/* 1722 */               long tested_ago = mono_now - last_test;
/*      */               
/* 1724 */               if (tested_ago >= adjustedReTest)
/*      */               {
/* 1726 */                 if (tested_ago > oldest_test)
/*      */                 {
/* 1728 */                   oldest_test = tested_ago;
/* 1729 */                   to_test = drc;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1737 */         if (to_test != null)
/*      */         {
/* 1739 */           this.dlr_current_active = to_test;
/*      */           
/* 1741 */           to_test.setDLRActive(mono_now);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1747 */       Collections.sort(downloads, new Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(DefaultRankCalculator o1, DefaultRankCalculator o2)
/*      */         {
/*      */ 
/*      */ 
/* 1756 */           if (o1 == StartStopRulesDefaultPlugin.this.dlr_current_active)
/*      */           {
/* 1758 */             return -1;
/*      */           }
/* 1760 */           if (o2 == StartStopRulesDefaultPlugin.this.dlr_current_active)
/*      */           {
/* 1762 */             return 1;
/*      */           }
/*      */           
/* 1765 */           int speed1 = o1.getDLRLastTestSpeed();
/* 1766 */           int speed2 = o2.getDLRLastTestSpeed();
/*      */           
/* 1768 */           int res = speed2 - speed1;
/*      */           
/* 1770 */           if (res == 0)
/*      */           {
/* 1772 */             res = o1.dl.getPosition() - o2.dl.getPosition();
/*      */           }
/*      */           
/*      */ 
/* 1776 */           return res;
/*      */         }
/*      */       });
/*      */       
/* 1780 */       for (int i = 0; i < downloads.size(); i++)
/*      */       {
/* 1782 */         DefaultRankCalculator drc = (DefaultRankCalculator)downloads.get(i);
/*      */         
/* 1784 */         if (drc.getDLRLastTestSpeed() > 0)
/*      */         {
/* 1786 */           if (drc.dl.getPosition() != i + 1)
/*      */           {
/* 1788 */             drc.dl.moveTo(i + 1);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private int getMaxDownloads()
/*      */   {
/* 1798 */     if (this.dlr_current_active == null)
/*      */     {
/* 1800 */       return this.maxConfiguredDownloads;
/*      */     }
/*      */     
/*      */ 
/* 1804 */     return this.maxConfiguredDownloads + 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void handleInCompleteDownload(DefaultRankCalculator dlData, ProcessVars vars, TotalsStats totals)
/*      */   {
/* 1815 */     Download download = dlData.dl;
/* 1816 */     int state = download.getState();
/*      */     
/* 1818 */     if (download.isForceStart()) {
/* 1819 */       if (this.bDebugLog) {
/* 1820 */         String s = "isForceStart.. rules skipped";
/* 1821 */         this.log.log(download.getTorrent(), 1, s); DefaultRankCalculator 
/* 1822 */           tmp64_63 = dlData;tmp64_63.sTrace = (tmp64_63.sTrace + s + "\n");
/*      */       }
/* 1824 */       return;
/*      */     }
/*      */     
/* 1827 */     if (this.bMaxDownloadIgnoreChecking)
/*      */     {
/*      */ 
/* 1830 */       org.gudy.azureus2.core3.download.DownloadManager core_dm = PluginCoreUtils.unwrap(download);
/*      */       
/* 1832 */       if ((core_dm != null) && (core_dm.getState() == 30))
/*      */       {
/* 1834 */         if (this.bDebugLog) {
/* 1835 */           String s = "isChecking.. rules skipped";
/* 1836 */           this.log.log(download.getTorrent(), 1, s); DefaultRankCalculator 
/* 1837 */             tmp159_158 = dlData;tmp159_158.sTrace = (tmp159_158.sTrace + s + "\n");
/*      */         }
/* 1839 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1844 */     if (state == 2) {
/* 1845 */       vars.numWaitingOrDLing += 1;
/* 1846 */       if (this.bDebugLog) {
/* 1847 */         String s = "ST_PREPARING.. rules skipped. numW8tngorDLing=" + vars.numWaitingOrDLing;
/*      */         
/* 1849 */         this.log.log(download.getTorrent(), 1, s); DefaultRankCalculator 
/* 1850 */           tmp259_258 = dlData;tmp259_258.sTrace = (tmp259_258.sTrace + s + "\n");
/*      */       }
/* 1852 */       return;
/*      */     }
/*      */     
/* 1855 */     int maxDLs = 0;
/* 1856 */     int maxDownloads = getMaxDownloads();
/* 1857 */     if (totals.maxActive == 0) {
/* 1858 */       maxDLs = maxDownloads;
/*      */     } else {
/* 1860 */       int DLmax = 0;
/* 1861 */       DLmax = totals.stalledFPSeeders + totals.forcedActive + totals.maxActive - totals.firstPriority - totals.forcedSeedingNonFP;
/*      */       
/* 1863 */       maxDLs = maxDownloads - DLmax <= 0 ? maxDownloads : DLmax <= 0 ? 0 : DLmax;
/*      */     }
/*      */     
/*      */ 
/* 1867 */     if (maxDLs < this.minDownloads) {
/* 1868 */       maxDLs = this.minDownloads;
/*      */     }
/*      */     
/* 1871 */     boolean bActivelyDownloading = dlData.getActivelyDownloading();
/*      */     boolean globalDownLimitReached;
/*      */     boolean globalRateAdjustedActivelyDownloading;
/*      */     boolean fakedActively;
/* 1875 */     if (this.bStopOnceBandwidthMet) {
/* 1876 */       boolean isRunning = download.getState() == 4;
/* 1877 */       boolean globalDownLimitReached = (this.globalDownloadLimit > 0) && (vars.accumulatedDownloadSpeed / 1024.0D > this.globalDownloadLimit * 0.9F);
/* 1878 */       boolean globalRateAdjustedActivelyDownloading = (bActivelyDownloading) || ((isRunning) && (globalDownLimitReached));
/* 1879 */       boolean fakedActively = (globalRateAdjustedActivelyDownloading) && (!bActivelyDownloading);
/* 1880 */       if (fakedActively)
/*      */       {
/* 1882 */         totals.activelyDLing += 1;
/* 1883 */         totals.maxSeeders = calcMaxSeeders(totals.activelyDLing + totals.waitingToDL);
/*      */       }
/*      */     } else {
/* 1886 */       globalDownLimitReached = false;
/* 1887 */       globalRateAdjustedActivelyDownloading = bActivelyDownloading;
/* 1888 */       fakedActively = false;
/*      */     }
/*      */     
/*      */ 
/* 1892 */     if (this.bDebugLog) {
/* 1893 */       String s = ">> DL state=" + " WPRDS.XEQ".charAt(download.getState()) + ";shareRatio=" + download.getStats().getShareRatio() + ";numW8tngorDLing=" + vars.numWaitingOrDLing + ";maxCDrs=" + totals.maxSeeders + ";forced=" + boolDebug(download.isForceStart()) + ";actvDLs=" + totals.activelyDLing + ";maxDLs=" + maxDLs + ";ActDLing=" + boolDebug(bActivelyDownloading) + ";globDwnRchd=" + boolDebug(globalDownLimitReached) + ";hgherQd=" + boolDebug(vars.higherDLtoStart) + ";isCmplt=" + boolDebug(download.isComplete());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1901 */       this.log.log(download.getTorrent(), 1, s); DefaultRankCalculator 
/* 1902 */         tmp755_754 = dlData;tmp755_754.sTrace = (tmp755_754.sTrace + s + "\n");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1907 */     if (((state == 4) && (globalRateAdjustedActivelyDownloading)) || (state == 3) || (state == 1) || (state == 2))
/*      */     {
/*      */ 
/* 1910 */       vars.numWaitingOrDLing += 1;
/*      */     }
/*      */     
/* 1913 */     if ((state == 3) || (state == 4) || (state == 1))
/*      */     {
/*      */ 
/* 1916 */       boolean bOverLimit = (vars.numWaitingOrDLing > maxDLs) || ((vars.numWaitingOrDLing >= maxDLs) && (vars.higherDLtoStart));
/*      */       
/*      */ 
/* 1919 */       boolean bDownloading = state == 4;
/*      */       
/* 1921 */       if ((maxDownloads != 0) && (bOverLimit) && (!download.isChecking()) && (!download.isMoving()) && ((globalRateAdjustedActivelyDownloading) || (!bDownloading) || ((bDownloading) && (totals.maxActive != 0) && (!globalRateAdjustedActivelyDownloading) && (totals.activelyCDing + totals.activelyDLing >= totals.maxActive))))
/*      */       {
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/*      */ 
/* 1929 */           if (this.bDebugLog)
/*      */           {
/* 1931 */             String s = "   stopAndQueue: " + vars.numWaitingOrDLing + " waiting or downloading, when limit is " + maxDLs + "(" + maxDownloads + ")";
/* 1932 */             if (vars.higherDLtoStart)
/*      */             {
/* 1934 */               s = s + " and higher DL is starting";
/*      */             }
/* 1936 */             this.log.log(download.getTorrent(), 1, s); DefaultRankCalculator 
/* 1937 */               tmp1071_1070 = dlData;tmp1071_1070.sTrace = (tmp1071_1070.sTrace + s + "\n");
/*      */           }
/* 1939 */           download.stopAndQueue();
/*      */           
/* 1941 */           vars.numWaitingOrDLing -= 1;
/* 1942 */           if (state == 4)
/*      */           {
/* 1944 */             totals.downloading -= 1;
/* 1945 */             if ((bActivelyDownloading) || (fakedActively)) {
/* 1946 */               totals.activelyDLing -= 1;
/*      */             }
/*      */           } else {
/* 1949 */             totals.waitingToDL -= 1;
/*      */           }
/* 1951 */           totals.maxSeeders = calcMaxSeeders(totals.activelyDLing + totals.waitingToDL);
/*      */         }
/*      */         catch (Exception ignore) {}
/*      */         
/*      */ 
/*      */ 
/* 1957 */         state = download.getState();
/*      */       }
/* 1959 */       else if (this.bDebugLog) {
/* 1960 */         String s = "NOT queuing: ";
/* 1961 */         if (maxDownloads == 0) {
/* 1962 */           s = s + "maxDownloads = " + maxDownloads;
/* 1963 */         } else if (!bOverLimit) {
/* 1964 */           s = s + "not over limit.  numWaitingOrDLing(" + vars.numWaitingOrDLing + ") <= maxDLs(" + maxDLs + ")";
/*      */         }
/* 1966 */         else if ((!bActivelyDownloading) || (bDownloading)) {
/* 1967 */           s = s + "not actively downloading";
/* 1968 */         } else if (totals.maxActive == 0) {
/* 1969 */           s = s + "unlimited active allowed (set)";
/*      */         } else {
/* 1971 */           s = s + "# active(" + (totals.activelyCDing + totals.activelyDLing) + ") < maxActive(" + totals.maxActive + ")";
/*      */         }
/*      */         
/* 1974 */         this.log.log(download.getTorrent(), 1, s); DefaultRankCalculator 
/* 1975 */           tmp1448_1447 = dlData;tmp1448_1447.sTrace = (tmp1448_1447.sTrace + s + "\n");
/*      */       }
/*      */     }
/*      */     
/* 1979 */     if ((state == 3) && (
/* 1980 */       (maxDownloads == 0) || (totals.activelyDLing < maxDLs))) {
/*      */       try {
/* 1982 */         if (this.bDebugLog) {
/* 1983 */           String s = "   start: READY && activelyDLing (" + totals.activelyDLing + ") < maxDLs (" + maxDownloads + ")";
/*      */           
/* 1985 */           this.log.log(download.getTorrent(), 1, s); DefaultRankCalculator 
/* 1986 */             tmp1568_1567 = dlData;tmp1568_1567.sTrace = (tmp1568_1567.sTrace + s + "\n");
/*      */         }
/* 1988 */         download.start();
/*      */         
/*      */ 
/* 1991 */         totals.waitingToDL -= 1;
/* 1992 */         totals.activelyDLing += 1;
/* 1993 */         totals.maxSeeders = calcMaxSeeders(totals.activelyDLing + totals.waitingToDL);
/*      */       }
/*      */       catch (Exception ignore) {}
/*      */       
/*      */ 
/* 1998 */       state = download.getState();
/*      */     }
/*      */     
/*      */ 
/* 2002 */     if ((state == 9) && (
/* 2003 */       (maxDownloads == 0) || (vars.numWaitingOrDLing < maxDLs))) {
/*      */       try {
/* 2005 */         if (this.bDebugLog) {
/* 2006 */           String s = "   restart: QUEUED && numWaitingOrDLing (" + vars.numWaitingOrDLing + ") < maxDLS (" + maxDLs + ")";
/*      */           
/* 2008 */           this.log.log(1, s); DefaultRankCalculator 
/* 2009 */             tmp1740_1739 = dlData;tmp1740_1739.sTrace = (tmp1740_1739.sTrace + s + "\n");
/*      */         }
/* 2011 */         download.restart();
/*      */         
/*      */ 
/* 2014 */         vars.numWaitingOrDLing += 1;
/* 2015 */         totals.waitingToDL += 1;
/* 2016 */         totals.maxSeeders = calcMaxSeeders(totals.activelyDLing + totals.waitingToDL);
/*      */       }
/*      */       catch (Exception ignore) {}
/*      */       
/* 2020 */       state = download.getState();
/*      */     }
/*      */     
/*      */ 
/* 2024 */     int oldState = state;
/* 2025 */     state = download.getState();
/*      */     
/* 2027 */     if (oldState != state) {
/* 2028 */       if (this.bDebugLog) {
/* 2029 */         this.log.log(1, ">> somethingChanged: state");
/*      */       }
/* 2031 */       this.somethingChanged = true;
/*      */     }
/*      */     
/* 2034 */     if ((download.getSeedingRank() >= 0) && ((state == 9) || (state == 3) || (state == 1) || (state == 2)))
/*      */     {
/*      */ 
/* 2037 */       vars.higherDLtoStart = true;
/*      */     }
/*      */     
/* 2040 */     if (this.bDebugLog) {
/* 2041 */       String s = "<< DL state=" + " WPRDS.XEQ".charAt(download.getState()) + ";shareRatio=" + download.getStats().getShareRatio() + ";numW8tngorDLing=" + vars.numWaitingOrDLing + ";maxCDrs=" + totals.maxSeeders + ";forced=" + boolDebug(download.isForceStart()) + ";actvDLs=" + totals.activelyDLing + ";hgherQd=" + boolDebug(vars.higherDLtoStart) + ";ActDLing=" + boolDebug(dlData.getActivelyDownloading());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2048 */       this.log.log(download.getTorrent(), 1, s); DefaultRankCalculator 
/* 2049 */         tmp2089_2088 = dlData;tmp2089_2088.sTrace = (tmp2089_2088.sTrace + s + "\n");
/*      */     }
/*      */     
/* 2052 */     if (this.bStopOnceBandwidthMet) {
/* 2053 */       vars.accumulatedDownloadSpeed += download.getStats().getDownloadAverage();
/* 2054 */       vars.accumulatedUploadSpeed += download.getStats().getUploadAverage();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long changeCheckMaxMS;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long processCount;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long processTotalMS;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long processMaxMS;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long processLastComplete;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long processTotalGap;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long processTotalRecalcs;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long processTotalZeroRecalcs;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private DefaultRankCalculator dlr_current_active;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long dlr_max_rate_time;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long processMergeCount;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String boolDebug(boolean b)
/*      */   {
/* 2503 */     return b ? "Y" : "N";
/*      */   }
/*      */   
/*      */ 
/*      */   private void printDebugChanges(String sPrefixFirstLine, String[] oldEntries, String[] newEntries, String sDebugLine, String sPrefix, boolean bAlwaysPrintNoChangeLine, DefaultRankCalculator dlData)
/*      */   {
/* 2509 */     boolean bAnyChanged = false;
/* 2510 */     String sDebugLineNoChange = sPrefixFirstLine;
/* 2511 */     StringBuilder sDebugLineOld = new StringBuilder(120);
/* 2512 */     StringBuilder sDebugLineNew = new StringBuilder(120);
/* 2513 */     for (int j = 0; j < oldEntries.length; j++) {
/* 2514 */       if (oldEntries[j].equals(newEntries[j])) {
/* 2515 */         sDebugLineNoChange = sDebugLineNoChange + oldEntries[j] + ";";
/*      */       } else {
/* 2517 */         sDebugLineOld.append(oldEntries[j]);sDebugLineOld.append(";");
/* 2518 */         sDebugLineNew.append(newEntries[j]);sDebugLineNew.append(";");
/* 2519 */         bAnyChanged = true;
/*      */       }
/*      */     }
/* 2522 */     String sDebugLineOut = ((bAlwaysPrintNoChangeLine) || (bAnyChanged) ? sDebugLineNoChange : "") + (bAnyChanged ? "\nOld:" + sDebugLineOld + "\nNew:" + sDebugLineNew : "") + sDebugLine;
/*      */     
/*      */ 
/*      */ 
/* 2526 */     if (!sDebugLineOut.equals("")) {
/* 2527 */       String[] lines = sDebugLineOut.split("\n");
/* 2528 */       for (int i = 0; i < lines.length; i++) {
/* 2529 */         String s = sPrefix + (i > 0 ? "  " : "") + lines[i];
/* 2530 */         if (dlData == null) {
/* 2531 */           this.log.log(1, s);
/*      */         } else {
/* 2533 */           this.log.log(dlData.dl.getTorrent(), 1, s); DefaultRankCalculator 
/* 2534 */             tmp337_335 = dlData;tmp337_335.sTrace = (tmp337_335.sTrace + s + "\n");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int calcPeersNoUs(Download download, DownloadScrapeResult sr)
/*      */   {
/* 2545 */     int numPeers = 0;
/* 2546 */     if (sr.getScrapeStartTime() > 0L) {
/* 2547 */       numPeers = sr.getNonSeedCount();
/*      */       
/*      */ 
/* 2550 */       if ((numPeers > 0) && (download.getState() == 4) && (sr.getScrapeStartTime() > download.getStats().getTimeStarted()))
/*      */       {
/* 2552 */         numPeers--; }
/*      */     }
/* 2554 */     if (numPeers == 0)
/*      */     {
/* 2556 */       DownloadAnnounceResult ar = download.getLastAnnounceResult();
/* 2557 */       if ((ar != null) && (ar.getResponseType() == 1))
/*      */       {
/* 2559 */         numPeers = ar.getNonSeedCount();
/*      */       }
/* 2561 */       if (numPeers == 0) {
/* 2562 */         DownloadActivationEvent activationState = download.getActivationState();
/* 2563 */         if (activationState != null) {
/* 2564 */           numPeers = activationState.getActivationCount();
/*      */         }
/*      */       }
/*      */     }
/* 2568 */     return numPeers;
/*      */   }
/*      */   
/*      */   private boolean scrapeResultOk(Download download) {
/* 2572 */     DownloadScrapeResult sr = download.getAggregatedScrapeResult();
/* 2573 */     return sr.getResponseType() == 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int calcSeedsNoUs(Download download, DownloadScrapeResult sr)
/*      */   {
/* 2582 */     return calcSeedsNoUs(download, sr, calcPeersNoUs(download, sr));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int calcSeedsNoUs(Download download, DownloadScrapeResult sr, int numPeers)
/*      */   {
/* 2592 */     int numSeeds = 0;
/* 2593 */     if (sr.getScrapeStartTime() > 0L) {
/* 2594 */       long seedingStartedOn = download.getStats().getTimeStartedSeeding();
/* 2595 */       numSeeds = sr.getSeedCount();
/*      */       
/*      */ 
/* 2598 */       if ((numSeeds > 0) && (seedingStartedOn > 0L) && (download.getState() == 5) && (sr.getScrapeStartTime() > seedingStartedOn))
/*      */       {
/*      */ 
/* 2601 */         numSeeds--; }
/*      */     }
/* 2603 */     if (numSeeds == 0)
/*      */     {
/* 2605 */       DownloadAnnounceResult ar = download.getLastAnnounceResult();
/* 2606 */       if ((ar != null) && (ar.getResponseType() == 1))
/*      */       {
/* 2608 */         numSeeds = ar.getSeedCount();
/*      */       }
/*      */     }
/* 2611 */     if ((this.numPeersAsFullCopy != 0) && (numSeeds >= this.iFakeFullCopySeedStart)) {
/* 2612 */       numSeeds += numPeers / this.numPeersAsFullCopy;
/*      */     }
/* 2614 */     return numSeeds;
/*      */   }
/*      */   
/*      */   public StartStopRulesDefaultPlugin()
/*      */   {
/*  121 */     this.globalDownloadSpeedAverage = AverageFactory.MovingImmediateAverage(10);
/*      */     
/*      */ 
/*  124 */     this.this_mon = new AEMonitor("StartStopRules");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  150 */     this.ranksToRecalc = new LightHashSet();
/*      */     
/*  152 */     this.ranksToRecalc_mon = new AEMonitor("ranksToRecalc");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  162 */     this.iRankType = -1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  196 */     this.bStopOnceBandwidthMet = false;
/*      */     
/*  198 */     this.bStartNoMoreSeedsWhenUpLimitMet = false;
/*  199 */     this.bStartNoMoreSeedsWhenUpLimitMetPercent = true;
/*  200 */     this.bStartNoMoreSeedsWhenUpLimitMetSlack = 95;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  216 */     this.debugMenuItem = null;
/*      */     
/*      */ 
/*      */ 
/*  220 */     this.listenersFP = new CopyOnWriteList();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  595 */     this.immediateProcessingScheduled = false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  770 */     this.changeCheckCount = 0L;
/*      */     
/*  772 */     this.changeCheckTotalMS = 0L;
/*      */     
/*  774 */     this.changeCheckMaxMS = 0L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1343 */     this.processCount = 0L;
/*      */     
/* 1345 */     this.processTotalMS = 0L;
/*      */     
/* 1347 */     this.processMaxMS = 0L;
/*      */     
/* 1349 */     this.processLastComplete = 0L;
/*      */     
/* 1351 */     this.processTotalGap = 0L;
/*      */     
/* 1353 */     this.processTotalRecalcs = 0L;
/*      */     
/* 1355 */     this.processTotalZeroRecalcs = 0L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2621 */     this.processMergeCount = 0L;
/*      */   }
/*      */   
/* 2624 */   public void requestProcessCycle(DefaultRankCalculator rankToRecalc) { if (rankToRecalc != null) {
/*      */       try {
/* 2626 */         this.ranksToRecalc_mon.enter();
/*      */         
/* 2628 */         this.ranksToRecalc.add(rankToRecalc);
/*      */       } finally {
/* 2630 */         this.ranksToRecalc_mon.exit();
/*      */       }
/*      */     }
/*      */     
/* 2634 */     if (this.somethingChanged) {
/* 2635 */       this.processMergeCount += 1L;
/*      */     } else {
/* 2637 */       this.somethingChanged = true;
/*      */     }
/*      */   }
/*      */   
/*      */   public void generate(IndentWriter writer) {
/* 2642 */     writer.println("StartStopRules Manager");
/*      */     try
/*      */     {
/* 2645 */       writer.indent();
/* 2646 */       writer.println("Started " + TimeFormatter.format100ths(SystemTime.getMonotonousTime() - this.monoStartedOn) + " ago");
/*      */       
/* 2648 */       writer.println("debugging = " + this.bDebugLog);
/* 2649 */       writer.println("downloadDataMap size = " + downloadDataMap.size());
/* 2650 */       if (this.changeCheckCount > 0L) {
/* 2651 */         writer.println("changeCheck CPU ms: avg=" + this.changeCheckTotalMS / this.changeCheckCount + "; max = " + this.changeCheckMaxMS);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2656 */       if (this.processCount > 0L) {
/* 2657 */         writer.println("# process cycles: " + this.processCount);
/*      */         
/* 2659 */         writer.println("process CPU ms: avg=" + this.processTotalMS / this.processCount + "; max = " + this.processMaxMS);
/*      */         
/* 2661 */         if (this.processCount > 1L) {
/* 2662 */           writer.println("process avg gap: " + this.processTotalGap / (this.processCount - 1L) + "ms");
/*      */         }
/*      */         
/* 2665 */         writer.println("Avg # recalcs per process cycle: " + this.processTotalRecalcs / this.processCount);
/*      */         
/* 2667 */         if (this.processTotalZeroRecalcs > 0L) {
/* 2668 */           writer.println("# process cycle with 0 recalcs: " + this.processTotalZeroRecalcs);
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */     }
/*      */     catch (Exception e) {}finally
/*      */     {
/* 2676 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addListener(StartStopRulesFPListener listener) {
/* 2681 */     this.listenersFP.add(listener);
/*      */   }
/*      */   
/*      */   public void removeListener(StartStopRulesFPListener listener) {
/* 2685 */     this.listenersFP.remove(listener);
/*      */   }
/*      */   
/*      */   public List getFPListeners() {
/* 2689 */     return this.listenersFP.getList();
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   private void handleCompletedDownload(DefaultRankCalculator[] dlDataArray, DefaultRankCalculator dlData, ProcessVars vars, TotalsStats totals)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload 4
/*      */     //   2: getfield 1506	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:bOkToStartSeeding	Z
/*      */     //   5: ifne +4 -> 9
/*      */     //   8: return
/*      */     //   9: aload_2
/*      */     //   10: getfield 1422	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:dl	Lorg/gudy/azureus2/plugins/download/Download;
/*      */     //   13: astore 5
/*      */     //   15: aload 5
/*      */     //   17: invokeinterface 1624 1 0
/*      */     //   22: istore 6
/*      */     //   24: iload 6
/*      */     //   26: iconst_3
/*      */     //   27: if_icmpeq +9 -> 36
/*      */     //   30: iload 6
/*      */     //   32: iconst_5
/*      */     //   33: if_icmpne +7 -> 40
/*      */     //   36: iconst_1
/*      */     //   37: goto +4 -> 41
/*      */     //   40: iconst_0
/*      */     //   41: istore 7
/*      */     //   43: aconst_null
/*      */     //   44: astore 8
/*      */     //   46: ldc 2
/*      */     //   48: astore 9
/*      */     //   50: aload_2
/*      */     //   51: getfield 1417	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultPeers	I
/*      */     //   54: istore 10
/*      */     //   56: iconst_0
/*      */     //   57: istore 11
/*      */     //   59: aload_0
/*      */     //   60: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   63: ifeq +361 -> 424
/*      */     //   66: aload_2
/*      */     //   67: invokevirtual 1520	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:isFirstPriority	()Z
/*      */     //   70: istore 11
/*      */     //   72: bipush 12
/*      */     //   74: anewarray 988	java/lang/String
/*      */     //   77: dup
/*      */     //   78: iconst_0
/*      */     //   79: new 989	java/lang/StringBuilder
/*      */     //   82: dup
/*      */     //   83: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   86: ldc_w 897
/*      */     //   89: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   92: ldc_w 846
/*      */     //   95: iload 6
/*      */     //   97: invokevirtual 1555	java/lang/String:charAt	(I)C
/*      */     //   100: invokevirtual 1561	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
/*      */     //   103: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   106: aastore
/*      */     //   107: dup
/*      */     //   108: iconst_1
/*      */     //   109: new 989	java/lang/StringBuilder
/*      */     //   112: dup
/*      */     //   113: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   116: ldc_w 946
/*      */     //   119: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   122: aload 5
/*      */     //   124: invokeinterface 1640 1 0
/*      */     //   129: invokeinterface 1652 1 0
/*      */     //   134: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   137: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   140: aastore
/*      */     //   141: dup
/*      */     //   142: iconst_2
/*      */     //   143: new 989	java/lang/StringBuilder
/*      */     //   146: dup
/*      */     //   147: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   150: ldc_w 932
/*      */     //   153: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   156: aload_3
/*      */     //   157: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   160: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   163: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   166: aastore
/*      */     //   167: dup
/*      */     //   168: iconst_3
/*      */     //   169: new 989	java/lang/StringBuilder
/*      */     //   172: dup
/*      */     //   173: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   176: ldc_w 933
/*      */     //   179: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   182: aload_3
/*      */     //   183: getfield 1482	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrDLing	I
/*      */     //   186: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   189: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   192: aastore
/*      */     //   193: dup
/*      */     //   194: iconst_4
/*      */     //   195: new 989	java/lang/StringBuilder
/*      */     //   198: dup
/*      */     //   199: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   202: ldc_w 947
/*      */     //   205: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   208: aload 5
/*      */     //   210: invokeinterface 1623 1 0
/*      */     //   215: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   218: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   221: aastore
/*      */     //   222: dup
/*      */     //   223: iconst_5
/*      */     //   224: new 989	java/lang/StringBuilder
/*      */     //   227: dup
/*      */     //   228: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   231: ldc_w 916
/*      */     //   234: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   237: aload_0
/*      */     //   238: aload_3
/*      */     //   239: getfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   242: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   245: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   248: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   251: aastore
/*      */     //   252: dup
/*      */     //   253: bipush 6
/*      */     //   255: new 989	java/lang/StringBuilder
/*      */     //   258: dup
/*      */     //   259: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   262: ldc_w 924
/*      */     //   265: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   268: aload 4
/*      */     //   270: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   273: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   276: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   279: aastore
/*      */     //   280: dup
/*      */     //   281: bipush 7
/*      */     //   283: new 989	java/lang/StringBuilder
/*      */     //   286: dup
/*      */     //   287: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   290: ldc_w 898
/*      */     //   293: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   296: aload_0
/*      */     //   297: iload 11
/*      */     //   299: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   302: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   305: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   308: aastore
/*      */     //   309: dup
/*      */     //   310: bipush 8
/*      */     //   312: new 989	java/lang/StringBuilder
/*      */     //   315: dup
/*      */     //   316: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   319: ldc_w 929
/*      */     //   322: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   325: aload 4
/*      */     //   327: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   330: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   333: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   336: aastore
/*      */     //   337: dup
/*      */     //   338: bipush 9
/*      */     //   340: new 989	java/lang/StringBuilder
/*      */     //   343: dup
/*      */     //   344: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   347: ldc_w 895
/*      */     //   350: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   353: aload_0
/*      */     //   354: aload_2
/*      */     //   355: invokevirtual 1519	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:getActivelySeeding	()Z
/*      */     //   358: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   361: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   364: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   367: aastore
/*      */     //   368: dup
/*      */     //   369: bipush 10
/*      */     //   371: new 989	java/lang/StringBuilder
/*      */     //   374: dup
/*      */     //   375: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   378: ldc_w 931
/*      */     //   381: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   384: aload_2
/*      */     //   385: getfield 1418	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultSeeds	I
/*      */     //   388: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   391: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   394: aastore
/*      */     //   395: dup
/*      */     //   396: bipush 11
/*      */     //   398: new 989	java/lang/StringBuilder
/*      */     //   401: dup
/*      */     //   402: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   405: ldc_w 930
/*      */     //   408: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   411: aload_2
/*      */     //   412: getfield 1417	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultPeers	I
/*      */     //   415: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   418: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   421: aastore
/*      */     //   422: astore 8
/*      */     //   424: aload_2
/*      */     //   425: getfield 1419	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastScrapeResultOk	Z
/*      */     //   428: istore 12
/*      */     //   430: aload_0
/*      */     //   431: getfield 1456	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bAutoStart0Peers	Z
/*      */     //   434: ifeq +597 -> 1031
/*      */     //   437: iload 10
/*      */     //   439: ifne +592 -> 1031
/*      */     //   442: iload 12
/*      */     //   444: ifeq +587 -> 1031
/*      */     //   447: iload 6
/*      */     //   449: bipush 9
/*      */     //   451: if_icmpne +129 -> 580
/*      */     //   454: aload_0
/*      */     //   455: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   458: ifeq +26 -> 484
/*      */     //   461: new 989	java/lang/StringBuilder
/*      */     //   464: dup
/*      */     //   465: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   468: aload 9
/*      */     //   470: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   473: ldc_w 839
/*      */     //   476: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   479: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   482: astore 9
/*      */     //   484: aload 5
/*      */     //   486: invokeinterface 1626 1 0
/*      */     //   491: aload 4
/*      */     //   493: dup
/*      */     //   494: getfield 1505	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:waitingToSeed	I
/*      */     //   497: iconst_1
/*      */     //   498: iadd
/*      */     //   499: putfield 1505	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:waitingToSeed	I
/*      */     //   502: aload_3
/*      */     //   503: dup
/*      */     //   504: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   507: iconst_1
/*      */     //   508: iadd
/*      */     //   509: putfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   512: aload 5
/*      */     //   514: invokeinterface 1624 1 0
/*      */     //   519: istore 6
/*      */     //   521: iload 6
/*      */     //   523: iconst_3
/*      */     //   524: if_icmpne +51 -> 575
/*      */     //   527: aload_0
/*      */     //   528: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   531: ifeq +26 -> 557
/*      */     //   534: new 989	java/lang/StringBuilder
/*      */     //   537: dup
/*      */     //   538: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   541: aload 9
/*      */     //   543: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   546: ldc_w 840
/*      */     //   549: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   552: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   555: astore 9
/*      */     //   557: aload 5
/*      */     //   559: invokeinterface 1627 1 0
/*      */     //   564: aload 4
/*      */     //   566: dup
/*      */     //   567: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   570: iconst_1
/*      */     //   571: iadd
/*      */     //   572: putfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   575: goto +5 -> 580
/*      */     //   578: astore 13
/*      */     //   580: iload 6
/*      */     //   582: iconst_3
/*      */     //   583: if_icmpne +66 -> 649
/*      */     //   586: aload_0
/*      */     //   587: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   590: ifeq +26 -> 616
/*      */     //   593: new 989	java/lang/StringBuilder
/*      */     //   596: dup
/*      */     //   597: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   600: aload 9
/*      */     //   602: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   605: ldc_w 840
/*      */     //   608: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   611: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   614: astore 9
/*      */     //   616: aload 5
/*      */     //   618: invokeinterface 1627 1 0
/*      */     //   623: aload 4
/*      */     //   625: dup
/*      */     //   626: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   629: iconst_1
/*      */     //   630: iadd
/*      */     //   631: putfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   634: aload_3
/*      */     //   635: dup
/*      */     //   636: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   639: iconst_1
/*      */     //   640: iadd
/*      */     //   641: putfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   644: goto +5 -> 649
/*      */     //   647: astore 13
/*      */     //   649: aload_0
/*      */     //   650: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   653: ifeq +377 -> 1030
/*      */     //   656: bipush 12
/*      */     //   658: anewarray 988	java/lang/String
/*      */     //   661: dup
/*      */     //   662: iconst_0
/*      */     //   663: new 989	java/lang/StringBuilder
/*      */     //   666: dup
/*      */     //   667: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   670: ldc_w 897
/*      */     //   673: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   676: ldc_w 846
/*      */     //   679: aload 5
/*      */     //   681: invokeinterface 1624 1 0
/*      */     //   686: invokevirtual 1555	java/lang/String:charAt	(I)C
/*      */     //   689: invokevirtual 1561	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
/*      */     //   692: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   695: aastore
/*      */     //   696: dup
/*      */     //   697: iconst_1
/*      */     //   698: new 989	java/lang/StringBuilder
/*      */     //   701: dup
/*      */     //   702: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   705: ldc_w 946
/*      */     //   708: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   711: aload 5
/*      */     //   713: invokeinterface 1640 1 0
/*      */     //   718: invokeinterface 1652 1 0
/*      */     //   723: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   726: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   729: aastore
/*      */     //   730: dup
/*      */     //   731: iconst_2
/*      */     //   732: new 989	java/lang/StringBuilder
/*      */     //   735: dup
/*      */     //   736: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   739: ldc_w 932
/*      */     //   742: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   745: aload_3
/*      */     //   746: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   749: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   752: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   755: aastore
/*      */     //   756: dup
/*      */     //   757: iconst_3
/*      */     //   758: new 989	java/lang/StringBuilder
/*      */     //   761: dup
/*      */     //   762: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   765: ldc_w 933
/*      */     //   768: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   771: aload_3
/*      */     //   772: getfield 1482	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrDLing	I
/*      */     //   775: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   778: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   781: aastore
/*      */     //   782: dup
/*      */     //   783: iconst_4
/*      */     //   784: new 989	java/lang/StringBuilder
/*      */     //   787: dup
/*      */     //   788: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   791: ldc_w 947
/*      */     //   794: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   797: aload 5
/*      */     //   799: invokeinterface 1623 1 0
/*      */     //   804: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   807: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   810: aastore
/*      */     //   811: dup
/*      */     //   812: iconst_5
/*      */     //   813: new 989	java/lang/StringBuilder
/*      */     //   816: dup
/*      */     //   817: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   820: ldc_w 916
/*      */     //   823: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   826: aload_0
/*      */     //   827: aload_3
/*      */     //   828: getfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   831: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   834: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   837: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   840: aastore
/*      */     //   841: dup
/*      */     //   842: bipush 6
/*      */     //   844: new 989	java/lang/StringBuilder
/*      */     //   847: dup
/*      */     //   848: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   851: ldc_w 924
/*      */     //   854: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   857: aload 4
/*      */     //   859: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   862: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   865: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   868: aastore
/*      */     //   869: dup
/*      */     //   870: bipush 7
/*      */     //   872: new 989	java/lang/StringBuilder
/*      */     //   875: dup
/*      */     //   876: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   879: ldc_w 898
/*      */     //   882: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   885: aload_0
/*      */     //   886: iload 11
/*      */     //   888: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   891: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   894: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   897: aastore
/*      */     //   898: dup
/*      */     //   899: bipush 8
/*      */     //   901: new 989	java/lang/StringBuilder
/*      */     //   904: dup
/*      */     //   905: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   908: ldc_w 929
/*      */     //   911: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   914: aload 4
/*      */     //   916: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   919: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   922: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   925: aastore
/*      */     //   926: dup
/*      */     //   927: bipush 9
/*      */     //   929: new 989	java/lang/StringBuilder
/*      */     //   932: dup
/*      */     //   933: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   936: ldc_w 895
/*      */     //   939: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   942: aload_0
/*      */     //   943: aload_2
/*      */     //   944: invokevirtual 1519	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:getActivelySeeding	()Z
/*      */     //   947: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   950: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   953: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   956: aastore
/*      */     //   957: dup
/*      */     //   958: bipush 10
/*      */     //   960: new 989	java/lang/StringBuilder
/*      */     //   963: dup
/*      */     //   964: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   967: ldc_w 931
/*      */     //   970: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   973: aload_2
/*      */     //   974: getfield 1418	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultSeeds	I
/*      */     //   977: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   980: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   983: aastore
/*      */     //   984: dup
/*      */     //   985: bipush 11
/*      */     //   987: new 989	java/lang/StringBuilder
/*      */     //   990: dup
/*      */     //   991: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   994: ldc_w 930
/*      */     //   997: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1000: aload_2
/*      */     //   1001: getfield 1417	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultPeers	I
/*      */     //   1004: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   1007: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1010: aastore
/*      */     //   1011: astore 13
/*      */     //   1013: aload_0
/*      */     //   1014: ldc 2
/*      */     //   1016: aload 8
/*      */     //   1018: aload 13
/*      */     //   1020: aload 9
/*      */     //   1022: ldc_w 841
/*      */     //   1025: iconst_1
/*      */     //   1026: aload_2
/*      */     //   1027: invokespecial 1539	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:printDebugChanges	(Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator;)V
/*      */     //   1030: return
/*      */     //   1031: aload 5
/*      */     //   1033: invokeinterface 1623 1 0
/*      */     //   1038: istore 13
/*      */     //   1040: iload 13
/*      */     //   1042: iconst_m1
/*      */     //   1043: if_icmpge +484 -> 1527
/*      */     //   1046: aload 5
/*      */     //   1048: invokeinterface 1631 1 0
/*      */     //   1053: ifne +474 -> 1527
/*      */     //   1056: iload 7
/*      */     //   1058: ifne +469 -> 1527
/*      */     //   1061: aload_0
/*      */     //   1062: getfield 1456	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bAutoStart0Peers	Z
/*      */     //   1065: ifne +462 -> 1527
/*      */     //   1068: aload_0
/*      */     //   1069: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   1072: ifeq +73 -> 1145
/*      */     //   1075: new 989	java/lang/StringBuilder
/*      */     //   1078: dup
/*      */     //   1079: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1082: aload 9
/*      */     //   1084: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1087: ldc_w 831
/*      */     //   1090: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1093: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1096: astore 9
/*      */     //   1098: iload 13
/*      */     //   1100: iconst_m1
/*      */     //   1101: imul
/*      */     //   1102: istore 14
/*      */     //   1104: iload 14
/*      */     //   1106: getstatic 1421	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:SR_NEGATIVE_DEBUG	[Ljava/lang/String;
/*      */     //   1109: arraylength
/*      */     //   1110: if_icmpge +35 -> 1145
/*      */     //   1113: new 989	java/lang/StringBuilder
/*      */     //   1116: dup
/*      */     //   1117: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1120: aload 9
/*      */     //   1122: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1125: ldc_w 845
/*      */     //   1128: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1131: getstatic 1421	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:SR_NEGATIVE_DEBUG	[Ljava/lang/String;
/*      */     //   1134: iload 14
/*      */     //   1136: aaload
/*      */     //   1137: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1140: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1143: astore 9
/*      */     //   1145: aload_0
/*      */     //   1146: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   1149: ifeq +377 -> 1526
/*      */     //   1152: bipush 12
/*      */     //   1154: anewarray 988	java/lang/String
/*      */     //   1157: dup
/*      */     //   1158: iconst_0
/*      */     //   1159: new 989	java/lang/StringBuilder
/*      */     //   1162: dup
/*      */     //   1163: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1166: ldc_w 897
/*      */     //   1169: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1172: ldc_w 846
/*      */     //   1175: aload 5
/*      */     //   1177: invokeinterface 1624 1 0
/*      */     //   1182: invokevirtual 1555	java/lang/String:charAt	(I)C
/*      */     //   1185: invokevirtual 1561	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
/*      */     //   1188: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1191: aastore
/*      */     //   1192: dup
/*      */     //   1193: iconst_1
/*      */     //   1194: new 989	java/lang/StringBuilder
/*      */     //   1197: dup
/*      */     //   1198: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1201: ldc_w 946
/*      */     //   1204: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1207: aload 5
/*      */     //   1209: invokeinterface 1640 1 0
/*      */     //   1214: invokeinterface 1652 1 0
/*      */     //   1219: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   1222: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1225: aastore
/*      */     //   1226: dup
/*      */     //   1227: iconst_2
/*      */     //   1228: new 989	java/lang/StringBuilder
/*      */     //   1231: dup
/*      */     //   1232: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1235: ldc_w 932
/*      */     //   1238: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1241: aload_3
/*      */     //   1242: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   1245: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   1248: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1251: aastore
/*      */     //   1252: dup
/*      */     //   1253: iconst_3
/*      */     //   1254: new 989	java/lang/StringBuilder
/*      */     //   1257: dup
/*      */     //   1258: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1261: ldc_w 933
/*      */     //   1264: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1267: aload_3
/*      */     //   1268: getfield 1482	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrDLing	I
/*      */     //   1271: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   1274: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1277: aastore
/*      */     //   1278: dup
/*      */     //   1279: iconst_4
/*      */     //   1280: new 989	java/lang/StringBuilder
/*      */     //   1283: dup
/*      */     //   1284: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1287: ldc_w 947
/*      */     //   1290: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1293: aload 5
/*      */     //   1295: invokeinterface 1623 1 0
/*      */     //   1300: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   1303: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1306: aastore
/*      */     //   1307: dup
/*      */     //   1308: iconst_5
/*      */     //   1309: new 989	java/lang/StringBuilder
/*      */     //   1312: dup
/*      */     //   1313: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1316: ldc_w 916
/*      */     //   1319: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1322: aload_0
/*      */     //   1323: aload_3
/*      */     //   1324: getfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   1327: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   1330: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1333: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1336: aastore
/*      */     //   1337: dup
/*      */     //   1338: bipush 6
/*      */     //   1340: new 989	java/lang/StringBuilder
/*      */     //   1343: dup
/*      */     //   1344: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1347: ldc_w 924
/*      */     //   1350: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1353: aload 4
/*      */     //   1355: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   1358: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   1361: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1364: aastore
/*      */     //   1365: dup
/*      */     //   1366: bipush 7
/*      */     //   1368: new 989	java/lang/StringBuilder
/*      */     //   1371: dup
/*      */     //   1372: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1375: ldc_w 898
/*      */     //   1378: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1381: aload_0
/*      */     //   1382: iload 11
/*      */     //   1384: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   1387: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1390: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1393: aastore
/*      */     //   1394: dup
/*      */     //   1395: bipush 8
/*      */     //   1397: new 989	java/lang/StringBuilder
/*      */     //   1400: dup
/*      */     //   1401: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1404: ldc_w 929
/*      */     //   1407: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1410: aload 4
/*      */     //   1412: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   1415: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   1418: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1421: aastore
/*      */     //   1422: dup
/*      */     //   1423: bipush 9
/*      */     //   1425: new 989	java/lang/StringBuilder
/*      */     //   1428: dup
/*      */     //   1429: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1432: ldc_w 895
/*      */     //   1435: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1438: aload_0
/*      */     //   1439: aload_2
/*      */     //   1440: invokevirtual 1519	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:getActivelySeeding	()Z
/*      */     //   1443: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   1446: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1449: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1452: aastore
/*      */     //   1453: dup
/*      */     //   1454: bipush 10
/*      */     //   1456: new 989	java/lang/StringBuilder
/*      */     //   1459: dup
/*      */     //   1460: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1463: ldc_w 931
/*      */     //   1466: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1469: aload_2
/*      */     //   1470: getfield 1418	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultSeeds	I
/*      */     //   1473: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   1476: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1479: aastore
/*      */     //   1480: dup
/*      */     //   1481: bipush 11
/*      */     //   1483: new 989	java/lang/StringBuilder
/*      */     //   1486: dup
/*      */     //   1487: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1490: ldc_w 930
/*      */     //   1493: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1496: aload_2
/*      */     //   1497: getfield 1417	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultPeers	I
/*      */     //   1500: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   1503: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1506: aastore
/*      */     //   1507: astore 14
/*      */     //   1509: aload_0
/*      */     //   1510: ldc 2
/*      */     //   1512: aload 8
/*      */     //   1514: aload 14
/*      */     //   1516: aload 9
/*      */     //   1518: ldc_w 841
/*      */     //   1521: iconst_1
/*      */     //   1522: aload_2
/*      */     //   1523: invokespecial 1539	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:printDebugChanges	(Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator;)V
/*      */     //   1526: return
/*      */     //   1527: aload_3
/*      */     //   1528: getfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   1531: ifeq +48 -> 1579
/*      */     //   1534: aload 5
/*      */     //   1536: invokeinterface 1631 1 0
/*      */     //   1541: ifne +38 -> 1579
/*      */     //   1544: aload_0
/*      */     //   1545: getfield 1456	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bAutoStart0Peers	Z
/*      */     //   1548: ifne +31 -> 1579
/*      */     //   1551: iload 7
/*      */     //   1553: ifne +26 -> 1579
/*      */     //   1556: new 989	java/lang/StringBuilder
/*      */     //   1559: dup
/*      */     //   1560: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1563: aload 9
/*      */     //   1565: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1568: ldc_w 847
/*      */     //   1571: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1574: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1577: astore 9
/*      */     //   1579: aload_0
/*      */     //   1580: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   1583: ifeq +56 -> 1639
/*      */     //   1586: aload_0
/*      */     //   1587: getfield 1456	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bAutoStart0Peers	Z
/*      */     //   1590: ifeq +49 -> 1639
/*      */     //   1593: iload 10
/*      */     //   1595: ifne +44 -> 1639
/*      */     //   1598: iload 12
/*      */     //   1600: ifne +39 -> 1639
/*      */     //   1603: iload 6
/*      */     //   1605: bipush 9
/*      */     //   1607: if_icmpeq +9 -> 1616
/*      */     //   1610: iload 6
/*      */     //   1612: iconst_3
/*      */     //   1613: if_icmpne +26 -> 1639
/*      */     //   1616: new 989	java/lang/StringBuilder
/*      */     //   1619: dup
/*      */     //   1620: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1623: aload 9
/*      */     //   1625: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1628: ldc_w 830
/*      */     //   1631: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1634: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   1637: astore 9
/*      */     //   1639: aload_0
/*      */     //   1640: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   1643: ifne +9 -> 1652
/*      */     //   1646: aload_2
/*      */     //   1647: invokevirtual 1520	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:isFirstPriority	()Z
/*      */     //   1650: istore 11
/*      */     //   1652: aload_2
/*      */     //   1653: invokevirtual 1519	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:getActivelySeeding	()Z
/*      */     //   1656: istore 14
/*      */     //   1658: aload_0
/*      */     //   1659: getfield 1461	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bStopOnceBandwidthMet	Z
/*      */     //   1662: ifeq +160 -> 1822
/*      */     //   1665: aload 5
/*      */     //   1667: invokeinterface 1624 1 0
/*      */     //   1672: iconst_5
/*      */     //   1673: if_icmpne +7 -> 1680
/*      */     //   1676: iconst_1
/*      */     //   1677: goto +4 -> 1681
/*      */     //   1680: iconst_0
/*      */     //   1681: istore 19
/*      */     //   1683: aload 4
/*      */     //   1685: invokevirtual 1551	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxUploadSpeed	()I
/*      */     //   1688: ifle +31 -> 1719
/*      */     //   1691: aload_3
/*      */     //   1692: getfield 1487	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:accumulatedUploadSpeed	J
/*      */     //   1695: l2d
/*      */     //   1696: ldc2_w 822
/*      */     //   1699: ddiv
/*      */     //   1700: aload 4
/*      */     //   1702: invokevirtual 1551	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxUploadSpeed	()I
/*      */     //   1705: i2f
/*      */     //   1706: ldc_w 813
/*      */     //   1709: fmul
/*      */     //   1710: f2d
/*      */     //   1711: dcmpl
/*      */     //   1712: ifle +7 -> 1719
/*      */     //   1715: iconst_1
/*      */     //   1716: goto +4 -> 1720
/*      */     //   1719: iconst_0
/*      */     //   1720: istore 16
/*      */     //   1722: aload_0
/*      */     //   1723: getfield 1426	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:globalDownloadLimit	I
/*      */     //   1726: ifle +30 -> 1756
/*      */     //   1729: aload_3
/*      */     //   1730: getfield 1486	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:accumulatedDownloadSpeed	J
/*      */     //   1733: l2d
/*      */     //   1734: ldc2_w 822
/*      */     //   1737: ddiv
/*      */     //   1738: aload_0
/*      */     //   1739: getfield 1426	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:globalDownloadLimit	I
/*      */     //   1742: i2f
/*      */     //   1743: ldc_w 813
/*      */     //   1746: fmul
/*      */     //   1747: f2d
/*      */     //   1748: dcmpl
/*      */     //   1749: ifle +7 -> 1756
/*      */     //   1752: iconst_1
/*      */     //   1753: goto +4 -> 1757
/*      */     //   1756: iconst_0
/*      */     //   1757: istore 15
/*      */     //   1759: iload 14
/*      */     //   1761: ifne +18 -> 1779
/*      */     //   1764: iload 19
/*      */     //   1766: ifeq +17 -> 1783
/*      */     //   1769: iload 16
/*      */     //   1771: ifne +8 -> 1779
/*      */     //   1774: iload 15
/*      */     //   1776: ifeq +7 -> 1783
/*      */     //   1779: iconst_1
/*      */     //   1780: goto +4 -> 1784
/*      */     //   1783: iconst_0
/*      */     //   1784: istore 17
/*      */     //   1786: iload 17
/*      */     //   1788: ifeq +12 -> 1800
/*      */     //   1791: iload 14
/*      */     //   1793: ifne +7 -> 1800
/*      */     //   1796: iconst_1
/*      */     //   1797: goto +4 -> 1801
/*      */     //   1800: iconst_0
/*      */     //   1801: istore 18
/*      */     //   1803: iload 18
/*      */     //   1805: ifeq +14 -> 1819
/*      */     //   1808: aload 4
/*      */     //   1810: dup
/*      */     //   1811: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   1814: iconst_1
/*      */     //   1815: iadd
/*      */     //   1816: putfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   1819: goto +16 -> 1835
/*      */     //   1822: iconst_0
/*      */     //   1823: istore 16
/*      */     //   1825: iload 14
/*      */     //   1827: istore 17
/*      */     //   1829: iconst_0
/*      */     //   1830: istore 15
/*      */     //   1832: iconst_0
/*      */     //   1833: istore 18
/*      */     //   1835: iload 6
/*      */     //   1837: iconst_5
/*      */     //   1838: if_icmpne +18 -> 1856
/*      */     //   1841: iload 14
/*      */     //   1843: ifne +13 -> 1856
/*      */     //   1846: aload_3
/*      */     //   1847: dup
/*      */     //   1848: getfield 1485	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:stalledSeeders	I
/*      */     //   1851: iconst_1
/*      */     //   1852: iadd
/*      */     //   1853: putfield 1485	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:stalledSeeders	I
/*      */     //   1856: iload 7
/*      */     //   1858: ifeq +52 -> 1910
/*      */     //   1861: iload 11
/*      */     //   1863: ifeq +33 -> 1896
/*      */     //   1866: iload 11
/*      */     //   1868: ifeq +42 -> 1910
/*      */     //   1871: aload 4
/*      */     //   1873: getfield 1500	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxActive	I
/*      */     //   1876: ifeq +34 -> 1910
/*      */     //   1879: aload_3
/*      */     //   1880: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   1883: aload 4
/*      */     //   1885: getfield 1500	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxActive	I
/*      */     //   1888: aload_0
/*      */     //   1889: getfield 1436	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:minDownloads	I
/*      */     //   1892: isub
/*      */     //   1893: if_icmplt +17 -> 1910
/*      */     //   1896: aload 5
/*      */     //   1898: invokeinterface 1631 1 0
/*      */     //   1903: ifne +7 -> 1910
/*      */     //   1906: iconst_1
/*      */     //   1907: goto +4 -> 1911
/*      */     //   1910: iconst_0
/*      */     //   1911: istore 19
/*      */     //   1913: iload 19
/*      */     //   1915: ifeq +103 -> 2018
/*      */     //   1918: iload 6
/*      */     //   1920: iconst_5
/*      */     //   1921: if_icmpne +97 -> 2018
/*      */     //   1924: invokestatic 1585	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*      */     //   1927: aload 5
/*      */     //   1929: invokeinterface 1640 1 0
/*      */     //   1934: invokeinterface 1654 1 0
/*      */     //   1939: lsub
/*      */     //   1940: lstore 20
/*      */     //   1942: lload 20
/*      */     //   1944: aload_0
/*      */     //   1945: getfield 1443	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:minTimeAlive	J
/*      */     //   1948: lcmp
/*      */     //   1949: iflt +7 -> 1956
/*      */     //   1952: iconst_1
/*      */     //   1953: goto +4 -> 1957
/*      */     //   1956: iconst_0
/*      */     //   1957: istore 19
/*      */     //   1959: iload 19
/*      */     //   1961: ifne +57 -> 2018
/*      */     //   1964: aload_0
/*      */     //   1965: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   1968: ifeq +50 -> 2018
/*      */     //   1971: new 989	java/lang/StringBuilder
/*      */     //   1974: dup
/*      */     //   1975: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   1978: aload 9
/*      */     //   1980: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1983: ldc_w 832
/*      */     //   1986: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   1989: lload 20
/*      */     //   1991: invokevirtual 1563	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
/*      */     //   1994: ldc_w 867
/*      */     //   1997: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2000: aload_0
/*      */     //   2001: getfield 1443	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:minTimeAlive	J
/*      */     //   2004: invokevirtual 1563	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
/*      */     //   2007: ldc_w 860
/*      */     //   2010: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2013: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2016: astore 9
/*      */     //   2018: iload 6
/*      */     //   2020: bipush 9
/*      */     //   2022: if_icmpeq +82 -> 2104
/*      */     //   2025: iload 6
/*      */     //   2027: iconst_3
/*      */     //   2028: if_icmpeq +36 -> 2064
/*      */     //   2031: iload 6
/*      */     //   2033: iconst_1
/*      */     //   2034: if_icmpeq +30 -> 2064
/*      */     //   2037: iload 6
/*      */     //   2039: iconst_2
/*      */     //   2040: if_icmpeq +24 -> 2064
/*      */     //   2043: iload 6
/*      */     //   2045: iconst_5
/*      */     //   2046: if_icmpne +58 -> 2104
/*      */     //   2049: iload 17
/*      */     //   2051: ifeq +53 -> 2104
/*      */     //   2054: aload 5
/*      */     //   2056: invokeinterface 1631 1 0
/*      */     //   2061: ifne +43 -> 2104
/*      */     //   2064: aload_3
/*      */     //   2065: dup
/*      */     //   2066: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2069: iconst_1
/*      */     //   2070: iadd
/*      */     //   2071: putfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2074: aload_0
/*      */     //   2075: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   2078: ifeq +26 -> 2104
/*      */     //   2081: new 989	java/lang/StringBuilder
/*      */     //   2084: dup
/*      */     //   2085: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2088: aload 9
/*      */     //   2090: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2093: ldc_w 833
/*      */     //   2096: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2099: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2102: astore 9
/*      */     //   2104: iconst_0
/*      */     //   2105: istore 20
/*      */     //   2107: iload 19
/*      */     //   2109: ifne +17 -> 2126
/*      */     //   2112: aload 4
/*      */     //   2114: getfield 1507	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:upLimitProhibitsNewSeeds	Z
/*      */     //   2117: ifeq +9 -> 2126
/*      */     //   2120: iconst_1
/*      */     //   2121: istore 19
/*      */     //   2123: iconst_1
/*      */     //   2124: istore 20
/*      */     //   2126: iload 19
/*      */     //   2128: ifne +183 -> 2311
/*      */     //   2131: iload 6
/*      */     //   2133: bipush 9
/*      */     //   2135: if_icmpne +176 -> 2311
/*      */     //   2138: aload 4
/*      */     //   2140: getfield 1500	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxActive	I
/*      */     //   2143: ifeq +15 -> 2158
/*      */     //   2146: aload_3
/*      */     //   2147: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2150: aload 4
/*      */     //   2152: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   2155: if_icmpge +156 -> 2311
/*      */     //   2158: iload 13
/*      */     //   2160: iconst_m1
/*      */     //   2161: if_icmplt +150 -> 2311
/*      */     //   2164: aload_3
/*      */     //   2165: getfield 1485	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:stalledSeeders	I
/*      */     //   2168: aload_0
/*      */     //   2169: getfield 1435	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:maxStalledSeeding	I
/*      */     //   2172: if_icmpge +139 -> 2311
/*      */     //   2175: aload_3
/*      */     //   2176: getfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   2179: ifne +132 -> 2311
/*      */     //   2182: aload_0
/*      */     //   2183: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   2186: ifeq +64 -> 2250
/*      */     //   2189: new 989	java/lang/StringBuilder
/*      */     //   2192: dup
/*      */     //   2193: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2196: aload 9
/*      */     //   2198: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2201: ldc_w 834
/*      */     //   2204: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2207: iload 19
/*      */     //   2209: invokevirtual 1564	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*      */     //   2212: ldc_w 876
/*      */     //   2215: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2218: aload_3
/*      */     //   2219: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2222: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   2225: ldc_w 865
/*      */     //   2228: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2231: aload 4
/*      */     //   2233: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   2236: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   2239: ldc_w 860
/*      */     //   2242: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2245: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2248: astore 9
/*      */     //   2250: aload 5
/*      */     //   2252: invokeinterface 1626 1 0
/*      */     //   2257: iconst_0
/*      */     //   2258: istore 19
/*      */     //   2260: aload 4
/*      */     //   2262: dup
/*      */     //   2263: getfield 1505	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:waitingToSeed	I
/*      */     //   2266: iconst_1
/*      */     //   2267: iadd
/*      */     //   2268: putfield 1505	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:waitingToSeed	I
/*      */     //   2271: aload_3
/*      */     //   2272: dup
/*      */     //   2273: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2276: iconst_1
/*      */     //   2277: iadd
/*      */     //   2278: putfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2281: aload_0
/*      */     //   2282: getfield 1433	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:iRankType	I
/*      */     //   2285: iconst_3
/*      */     //   2286: if_icmpne +8 -> 2294
/*      */     //   2289: aload_2
/*      */     //   2290: invokevirtual 1515	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:recalcSeedingRank	()I
/*      */     //   2293: pop
/*      */     //   2294: goto +5 -> 2299
/*      */     //   2297: astore 21
/*      */     //   2299: aload 5
/*      */     //   2301: invokeinterface 1624 1 0
/*      */     //   2306: istore 6
/*      */     //   2308: goto +462 -> 2770
/*      */     //   2311: aload_0
/*      */     //   2312: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   2315: ifeq +455 -> 2770
/*      */     //   2318: iload 6
/*      */     //   2320: bipush 9
/*      */     //   2322: if_icmpne +448 -> 2770
/*      */     //   2325: new 989	java/lang/StringBuilder
/*      */     //   2328: dup
/*      */     //   2329: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2332: aload 9
/*      */     //   2334: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2337: ldc_w 829
/*      */     //   2340: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2343: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2346: astore 9
/*      */     //   2348: iload 13
/*      */     //   2350: iconst_m1
/*      */     //   2351: if_icmpge +76 -> 2427
/*      */     //   2354: new 989	java/lang/StringBuilder
/*      */     //   2357: dup
/*      */     //   2358: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2361: aload 9
/*      */     //   2363: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2366: ldc_w 853
/*      */     //   2369: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2372: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2375: astore 9
/*      */     //   2377: iload 13
/*      */     //   2379: iconst_m1
/*      */     //   2380: imul
/*      */     //   2381: istore 21
/*      */     //   2383: iload 21
/*      */     //   2385: getstatic 1421	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:SR_NEGATIVE_DEBUG	[Ljava/lang/String;
/*      */     //   2388: arraylength
/*      */     //   2389: if_icmpge +35 -> 2424
/*      */     //   2392: new 989	java/lang/StringBuilder
/*      */     //   2395: dup
/*      */     //   2396: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2399: aload 9
/*      */     //   2401: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2404: ldc_w 873
/*      */     //   2407: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2410: getstatic 1421	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:SR_NEGATIVE_DEBUG	[Ljava/lang/String;
/*      */     //   2413: iload 21
/*      */     //   2415: aaload
/*      */     //   2416: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2419: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2422: astore 9
/*      */     //   2424: goto +346 -> 2770
/*      */     //   2427: aload_3
/*      */     //   2428: getfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   2431: ifeq +29 -> 2460
/*      */     //   2434: new 989	java/lang/StringBuilder
/*      */     //   2437: dup
/*      */     //   2438: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2441: aload 9
/*      */     //   2443: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2446: ldc_w 847
/*      */     //   2449: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2452: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2455: astore 9
/*      */     //   2457: goto +313 -> 2770
/*      */     //   2460: iload 19
/*      */     //   2462: ifeq +26 -> 2488
/*      */     //   2465: new 989	java/lang/StringBuilder
/*      */     //   2468: dup
/*      */     //   2469: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2472: aload 9
/*      */     //   2474: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2477: ldc_w 852
/*      */     //   2480: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2483: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2486: astore 9
/*      */     //   2488: aload_3
/*      */     //   2489: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2492: aload 4
/*      */     //   2494: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   2497: if_icmplt +56 -> 2553
/*      */     //   2500: new 989	java/lang/StringBuilder
/*      */     //   2503: dup
/*      */     //   2504: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2507: aload 9
/*      */     //   2509: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2512: ldc_w 850
/*      */     //   2515: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2518: aload_3
/*      */     //   2519: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2522: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   2525: ldc_w 870
/*      */     //   2528: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2531: aload 4
/*      */     //   2533: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   2536: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   2539: ldc_w 860
/*      */     //   2542: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2545: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2548: astore 9
/*      */     //   2550: goto +220 -> 2770
/*      */     //   2553: aload_3
/*      */     //   2554: getfield 1485	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:stalledSeeders	I
/*      */     //   2557: aload_0
/*      */     //   2558: getfield 1435	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:maxStalledSeeding	I
/*      */     //   2561: if_icmplt +55 -> 2616
/*      */     //   2564: new 989	java/lang/StringBuilder
/*      */     //   2567: dup
/*      */     //   2568: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2571: aload 9
/*      */     //   2573: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2576: ldc_w 851
/*      */     //   2579: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2582: aload_3
/*      */     //   2583: getfield 1485	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:stalledSeeders	I
/*      */     //   2586: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   2589: ldc_w 871
/*      */     //   2592: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2595: aload_0
/*      */     //   2596: getfield 1435	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:maxStalledSeeding	I
/*      */     //   2599: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   2602: ldc_w 861
/*      */     //   2605: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2608: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2611: astore 9
/*      */     //   2613: goto +157 -> 2770
/*      */     //   2616: iload 20
/*      */     //   2618: ifeq +29 -> 2647
/*      */     //   2621: new 989	java/lang/StringBuilder
/*      */     //   2624: dup
/*      */     //   2625: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2628: aload 9
/*      */     //   2630: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2633: ldc_w 854
/*      */     //   2636: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2639: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2642: astore 9
/*      */     //   2644: goto +126 -> 2770
/*      */     //   2647: new 989	java/lang/StringBuilder
/*      */     //   2650: dup
/*      */     //   2651: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2654: aload 9
/*      */     //   2656: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2659: ldc_w 918
/*      */     //   2662: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2665: iload 6
/*      */     //   2667: bipush 9
/*      */     //   2669: if_icmpne +7 -> 2676
/*      */     //   2672: iconst_1
/*      */     //   2673: goto +4 -> 2677
/*      */     //   2676: iconst_0
/*      */     //   2677: invokevirtual 1564	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*      */     //   2680: ldc_w 875
/*      */     //   2683: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2686: aload 4
/*      */     //   2688: getfield 1500	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxActive	I
/*      */     //   2691: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   2694: ldc_w 874
/*      */     //   2697: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2700: aload_3
/*      */     //   2701: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2704: aload 4
/*      */     //   2706: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   2709: if_icmpge +7 -> 2716
/*      */     //   2712: iconst_1
/*      */     //   2713: goto +4 -> 2717
/*      */     //   2716: iconst_0
/*      */     //   2717: invokevirtual 1564	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*      */     //   2720: ldc_w 874
/*      */     //   2723: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2726: aload_3
/*      */     //   2727: getfield 1485	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:stalledSeeders	I
/*      */     //   2730: aload_0
/*      */     //   2731: getfield 1435	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:maxStalledSeeding	I
/*      */     //   2734: if_icmpgt +7 -> 2741
/*      */     //   2737: iconst_1
/*      */     //   2738: goto +4 -> 2742
/*      */     //   2741: iconst_0
/*      */     //   2742: invokevirtual 1564	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*      */     //   2745: ldc_w 884
/*      */     //   2748: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2751: iload 13
/*      */     //   2753: iconst_m1
/*      */     //   2754: if_icmplt +7 -> 2761
/*      */     //   2757: iconst_1
/*      */     //   2758: goto +4 -> 2762
/*      */     //   2761: iconst_0
/*      */     //   2762: invokevirtual 1564	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*      */     //   2765: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2768: astore 9
/*      */     //   2770: iconst_0
/*      */     //   2771: istore 21
/*      */     //   2773: iload 6
/*      */     //   2775: iconst_3
/*      */     //   2776: if_icmpne +152 -> 2928
/*      */     //   2779: aload 4
/*      */     //   2781: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   2784: aload 4
/*      */     //   2786: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   2789: if_icmpge +139 -> 2928
/*      */     //   2792: iload 13
/*      */     //   2794: iconst_m1
/*      */     //   2795: if_icmpge +13 -> 2808
/*      */     //   2798: aload 5
/*      */     //   2800: invokeinterface 1631 1 0
/*      */     //   2805: ifeq +115 -> 2920
/*      */     //   2808: aload_0
/*      */     //   2809: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   2812: ifeq +54 -> 2866
/*      */     //   2815: new 989	java/lang/StringBuilder
/*      */     //   2818: dup
/*      */     //   2819: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   2822: aload 9
/*      */     //   2824: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2827: ldc_w 835
/*      */     //   2830: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2833: aload 4
/*      */     //   2835: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   2838: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   2841: ldc_w 866
/*      */     //   2844: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2847: aload 4
/*      */     //   2849: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   2852: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   2855: ldc_w 860
/*      */     //   2858: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   2861: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   2864: astore 9
/*      */     //   2866: aload 5
/*      */     //   2868: invokeinterface 1627 1 0
/*      */     //   2873: iconst_0
/*      */     //   2874: istore 19
/*      */     //   2876: goto +5 -> 2881
/*      */     //   2879: astore 22
/*      */     //   2881: aload 5
/*      */     //   2883: invokeinterface 1624 1 0
/*      */     //   2888: istore 6
/*      */     //   2890: aload 4
/*      */     //   2892: dup
/*      */     //   2893: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   2896: iconst_1
/*      */     //   2897: iadd
/*      */     //   2898: putfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   2901: iconst_1
/*      */     //   2902: dup
/*      */     //   2903: istore 14
/*      */     //   2905: istore 17
/*      */     //   2907: aload_3
/*      */     //   2908: dup
/*      */     //   2909: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2912: iconst_1
/*      */     //   2913: iadd
/*      */     //   2914: putfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2917: goto +11 -> 2928
/*      */     //   2920: iload 19
/*      */     //   2922: ifeq +6 -> 2928
/*      */     //   2925: iconst_1
/*      */     //   2926: istore 21
/*      */     //   2928: iload 19
/*      */     //   2930: ifne +8 -> 2938
/*      */     //   2933: iload 21
/*      */     //   2935: ifeq +769 -> 3704
/*      */     //   2938: iload 21
/*      */     //   2940: istore 22
/*      */     //   2942: iload 22
/*      */     //   2944: ifne +623 -> 3567
/*      */     //   2947: aload_3
/*      */     //   2948: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2951: aload 4
/*      */     //   2953: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   2956: if_icmpgt +46 -> 3002
/*      */     //   2959: iload 14
/*      */     //   2961: ifne +14 -> 2975
/*      */     //   2964: aload_3
/*      */     //   2965: getfield 1485	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:stalledSeeders	I
/*      */     //   2968: aload_0
/*      */     //   2969: getfield 1435	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:maxStalledSeeding	I
/*      */     //   2972: if_icmpgt +30 -> 3002
/*      */     //   2975: aload_3
/*      */     //   2976: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   2979: aload 4
/*      */     //   2981: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   2984: if_icmpge +11 -> 2995
/*      */     //   2987: aload 4
/*      */     //   2989: getfield 1507	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:upLimitProhibitsNewSeeds	Z
/*      */     //   2992: ifeq +14 -> 3006
/*      */     //   2995: aload_3
/*      */     //   2996: getfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   2999: ifeq +7 -> 3006
/*      */     //   3002: iconst_1
/*      */     //   3003: goto +4 -> 3007
/*      */     //   3006: iconst_0
/*      */     //   3007: istore 23
/*      */     //   3009: iload 6
/*      */     //   3011: iconst_5
/*      */     //   3012: if_icmpne +7 -> 3019
/*      */     //   3015: iconst_1
/*      */     //   3016: goto +4 -> 3020
/*      */     //   3019: iconst_0
/*      */     //   3020: istore 24
/*      */     //   3022: aload 5
/*      */     //   3024: invokeinterface 1629 1 0
/*      */     //   3029: ifne +48 -> 3077
/*      */     //   3032: aload 5
/*      */     //   3034: invokeinterface 1632 1 0
/*      */     //   3039: ifne +38 -> 3077
/*      */     //   3042: iload 23
/*      */     //   3044: ifne +9 -> 3053
/*      */     //   3047: iload 13
/*      */     //   3049: iconst_m1
/*      */     //   3050: if_icmpge +27 -> 3077
/*      */     //   3053: iload 17
/*      */     //   3055: ifne +18 -> 3073
/*      */     //   3058: iload 24
/*      */     //   3060: ifeq +13 -> 3073
/*      */     //   3063: iload 17
/*      */     //   3065: ifne +12 -> 3077
/*      */     //   3068: iload 24
/*      */     //   3070: ifeq +7 -> 3077
/*      */     //   3073: iconst_1
/*      */     //   3074: goto +4 -> 3078
/*      */     //   3077: iconst_0
/*      */     //   3078: istore 22
/*      */     //   3080: aload_0
/*      */     //   3081: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   3084: ifeq +288 -> 3372
/*      */     //   3087: iload 22
/*      */     //   3089: ifeq +475 -> 3564
/*      */     //   3092: new 989	java/lang/StringBuilder
/*      */     //   3095: dup
/*      */     //   3096: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3099: aload 9
/*      */     //   3101: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3104: ldc_w 836
/*      */     //   3107: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3110: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3113: astore 9
/*      */     //   3115: iload 23
/*      */     //   3117: ifeq +105 -> 3222
/*      */     //   3120: aload_3
/*      */     //   3121: getfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   3124: ifeq +29 -> 3153
/*      */     //   3127: new 989	java/lang/StringBuilder
/*      */     //   3130: dup
/*      */     //   3131: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3134: aload 9
/*      */     //   3136: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3139: ldc_w 917
/*      */     //   3142: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3145: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3148: astore 9
/*      */     //   3150: goto +101 -> 3251
/*      */     //   3153: iload 14
/*      */     //   3155: ifne +41 -> 3196
/*      */     //   3158: aload_3
/*      */     //   3159: getfield 1485	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:stalledSeeders	I
/*      */     //   3162: aload 4
/*      */     //   3164: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   3167: if_icmple +29 -> 3196
/*      */     //   3170: new 989	java/lang/StringBuilder
/*      */     //   3173: dup
/*      */     //   3174: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3177: aload 9
/*      */     //   3179: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3182: ldc_w 940
/*      */     //   3185: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3188: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3191: astore 9
/*      */     //   3193: goto +58 -> 3251
/*      */     //   3196: new 989	java/lang/StringBuilder
/*      */     //   3199: dup
/*      */     //   3200: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3203: aload 9
/*      */     //   3205: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3208: ldc_w 939
/*      */     //   3211: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3214: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3217: astore 9
/*      */     //   3219: goto +32 -> 3251
/*      */     //   3222: iload 13
/*      */     //   3224: iconst_m1
/*      */     //   3225: if_icmpge +26 -> 3251
/*      */     //   3228: new 989	java/lang/StringBuilder
/*      */     //   3231: dup
/*      */     //   3232: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3235: aload 9
/*      */     //   3237: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3240: ldc_w 919
/*      */     //   3243: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3246: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3249: astore 9
/*      */     //   3251: new 989	java/lang/StringBuilder
/*      */     //   3254: dup
/*      */     //   3255: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3258: aload 9
/*      */     //   3260: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3263: ldc_w 845
/*      */     //   3266: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3269: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3272: astore 9
/*      */     //   3274: iload 14
/*      */     //   3276: ifeq +29 -> 3305
/*      */     //   3279: new 989	java/lang/StringBuilder
/*      */     //   3282: dup
/*      */     //   3283: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3286: aload 9
/*      */     //   3288: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3291: ldc_w 907
/*      */     //   3294: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3297: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3300: astore 9
/*      */     //   3302: goto +262 -> 3564
/*      */     //   3305: iload 24
/*      */     //   3307: ifne +29 -> 3336
/*      */     //   3310: new 989	java/lang/StringBuilder
/*      */     //   3313: dup
/*      */     //   3314: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3317: aload 9
/*      */     //   3319: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3322: ldc_w 934
/*      */     //   3325: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3328: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3331: astore 9
/*      */     //   3333: goto +231 -> 3564
/*      */     //   3336: iload 14
/*      */     //   3338: ifne +226 -> 3564
/*      */     //   3341: iload 24
/*      */     //   3343: ifeq +221 -> 3564
/*      */     //   3346: new 989	java/lang/StringBuilder
/*      */     //   3349: dup
/*      */     //   3350: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3353: aload 9
/*      */     //   3355: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3358: ldc_w 901
/*      */     //   3361: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3364: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3367: astore 9
/*      */     //   3369: goto +195 -> 3564
/*      */     //   3372: new 989	java/lang/StringBuilder
/*      */     //   3375: dup
/*      */     //   3376: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3379: aload 9
/*      */     //   3381: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3384: ldc_w 828
/*      */     //   3387: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3390: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3393: astore 9
/*      */     //   3395: aload 5
/*      */     //   3397: invokeinterface 1629 1 0
/*      */     //   3402: ifeq +29 -> 3431
/*      */     //   3405: new 989	java/lang/StringBuilder
/*      */     //   3408: dup
/*      */     //   3409: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3412: aload 9
/*      */     //   3414: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3417: ldc_w 911
/*      */     //   3420: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3423: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3426: astore 9
/*      */     //   3428: goto +136 -> 3564
/*      */     //   3431: aload 5
/*      */     //   3433: invokeinterface 1632 1 0
/*      */     //   3438: ifeq +29 -> 3467
/*      */     //   3441: new 989	java/lang/StringBuilder
/*      */     //   3444: dup
/*      */     //   3445: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3448: aload 9
/*      */     //   3450: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3453: ldc_w 912
/*      */     //   3456: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3459: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3462: astore 9
/*      */     //   3464: goto +100 -> 3564
/*      */     //   3467: iload 23
/*      */     //   3469: ifne +56 -> 3525
/*      */     //   3472: new 989	java/lang/StringBuilder
/*      */     //   3475: dup
/*      */     //   3476: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3479: aload 9
/*      */     //   3481: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3484: ldc_w 937
/*      */     //   3487: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3490: aload_3
/*      */     //   3491: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   3494: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   3497: ldc_w 869
/*      */     //   3500: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3503: aload 4
/*      */     //   3505: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   3508: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   3511: ldc_w 860
/*      */     //   3514: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3517: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3520: astore 9
/*      */     //   3522: goto +42 -> 3564
/*      */     //   3525: new 989	java/lang/StringBuilder
/*      */     //   3528: dup
/*      */     //   3529: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3532: aload 9
/*      */     //   3534: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3537: ldc_w 910
/*      */     //   3540: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3543: iload 14
/*      */     //   3545: invokevirtual 1564	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*      */     //   3548: ldc_w 880
/*      */     //   3551: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3554: iload 24
/*      */     //   3556: invokevirtual 1564	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
/*      */     //   3559: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3562: astore 9
/*      */     //   3564: goto +33 -> 3597
/*      */     //   3567: aload_0
/*      */     //   3568: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   3571: ifeq +26 -> 3597
/*      */     //   3574: new 989	java/lang/StringBuilder
/*      */     //   3577: dup
/*      */     //   3578: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3581: aload 9
/*      */     //   3583: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3586: ldc_w 827
/*      */     //   3589: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3592: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3595: astore 9
/*      */     //   3597: iload 22
/*      */     //   3599: ifeq +105 -> 3704
/*      */     //   3602: iload 6
/*      */     //   3604: iconst_3
/*      */     //   3605: if_icmpne +14 -> 3619
/*      */     //   3608: aload 4
/*      */     //   3610: dup
/*      */     //   3611: getfield 1505	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:waitingToSeed	I
/*      */     //   3614: iconst_1
/*      */     //   3615: isub
/*      */     //   3616: putfield 1505	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:waitingToSeed	I
/*      */     //   3619: aload 5
/*      */     //   3621: invokeinterface 1628 1 0
/*      */     //   3626: aload_3
/*      */     //   3627: iconst_1
/*      */     //   3628: putfield 1488	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:bStopAndQueued	Z
/*      */     //   3631: iload 14
/*      */     //   3633: ifne +8 -> 3641
/*      */     //   3636: iload 18
/*      */     //   3638: ifeq +17 -> 3655
/*      */     //   3641: aload 4
/*      */     //   3643: dup
/*      */     //   3644: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   3647: iconst_1
/*      */     //   3648: isub
/*      */     //   3649: putfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   3652: iconst_0
/*      */     //   3653: istore 14
/*      */     //   3655: iload 17
/*      */     //   3657: ifeq +16 -> 3673
/*      */     //   3660: aload_3
/*      */     //   3661: dup
/*      */     //   3662: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   3665: iconst_1
/*      */     //   3666: isub
/*      */     //   3667: putfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   3670: iconst_0
/*      */     //   3671: istore 17
/*      */     //   3673: iload 6
/*      */     //   3675: iconst_3
/*      */     //   3676: if_icmpne +14 -> 3690
/*      */     //   3679: aload 4
/*      */     //   3681: dup
/*      */     //   3682: getfield 1505	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:waitingToSeed	I
/*      */     //   3685: iconst_1
/*      */     //   3686: isub
/*      */     //   3687: putfield 1505	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:waitingToSeed	I
/*      */     //   3690: goto +5 -> 3695
/*      */     //   3693: astore 23
/*      */     //   3695: aload 5
/*      */     //   3697: invokeinterface 1624 1 0
/*      */     //   3702: istore 6
/*      */     //   3704: aload 5
/*      */     //   3706: invokeinterface 1624 1 0
/*      */     //   3711: istore 6
/*      */     //   3713: iload 13
/*      */     //   3715: iflt +33 -> 3748
/*      */     //   3718: iload 6
/*      */     //   3720: bipush 9
/*      */     //   3722: if_icmpeq +21 -> 3743
/*      */     //   3725: iload 6
/*      */     //   3727: iconst_3
/*      */     //   3728: if_icmpeq +15 -> 3743
/*      */     //   3731: iload 6
/*      */     //   3733: iconst_1
/*      */     //   3734: if_icmpeq +9 -> 3743
/*      */     //   3737: iload 6
/*      */     //   3739: iconst_2
/*      */     //   3740: if_icmpne +8 -> 3748
/*      */     //   3743: aload_3
/*      */     //   3744: iconst_1
/*      */     //   3745: putfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   3748: aload_0
/*      */     //   3749: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   3752: ifeq +766 -> 4518
/*      */     //   3755: bipush 12
/*      */     //   3757: anewarray 988	java/lang/String
/*      */     //   3760: dup
/*      */     //   3761: iconst_0
/*      */     //   3762: new 989	java/lang/StringBuilder
/*      */     //   3765: dup
/*      */     //   3766: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3769: ldc_w 897
/*      */     //   3772: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3775: ldc_w 846
/*      */     //   3778: aload 5
/*      */     //   3780: invokeinterface 1624 1 0
/*      */     //   3785: invokevirtual 1555	java/lang/String:charAt	(I)C
/*      */     //   3788: invokevirtual 1561	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
/*      */     //   3791: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3794: aastore
/*      */     //   3795: dup
/*      */     //   3796: iconst_1
/*      */     //   3797: new 989	java/lang/StringBuilder
/*      */     //   3800: dup
/*      */     //   3801: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3804: ldc_w 946
/*      */     //   3807: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3810: aload 5
/*      */     //   3812: invokeinterface 1640 1 0
/*      */     //   3817: invokeinterface 1652 1 0
/*      */     //   3822: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   3825: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3828: aastore
/*      */     //   3829: dup
/*      */     //   3830: iconst_2
/*      */     //   3831: new 989	java/lang/StringBuilder
/*      */     //   3834: dup
/*      */     //   3835: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3838: ldc_w 932
/*      */     //   3841: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3844: aload_3
/*      */     //   3845: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   3848: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   3851: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3854: aastore
/*      */     //   3855: dup
/*      */     //   3856: iconst_3
/*      */     //   3857: new 989	java/lang/StringBuilder
/*      */     //   3860: dup
/*      */     //   3861: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3864: ldc_w 933
/*      */     //   3867: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3870: aload_3
/*      */     //   3871: getfield 1482	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrDLing	I
/*      */     //   3874: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   3877: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3880: aastore
/*      */     //   3881: dup
/*      */     //   3882: iconst_4
/*      */     //   3883: new 989	java/lang/StringBuilder
/*      */     //   3886: dup
/*      */     //   3887: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3890: ldc_w 947
/*      */     //   3893: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3896: aload 5
/*      */     //   3898: invokeinterface 1623 1 0
/*      */     //   3903: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   3906: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3909: aastore
/*      */     //   3910: dup
/*      */     //   3911: iconst_5
/*      */     //   3912: new 989	java/lang/StringBuilder
/*      */     //   3915: dup
/*      */     //   3916: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3919: ldc_w 916
/*      */     //   3922: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3925: aload_0
/*      */     //   3926: aload_3
/*      */     //   3927: getfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   3930: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   3933: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3936: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3939: aastore
/*      */     //   3940: dup
/*      */     //   3941: bipush 6
/*      */     //   3943: new 989	java/lang/StringBuilder
/*      */     //   3946: dup
/*      */     //   3947: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3950: ldc_w 924
/*      */     //   3953: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3956: aload 4
/*      */     //   3958: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   3961: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   3964: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3967: aastore
/*      */     //   3968: dup
/*      */     //   3969: bipush 7
/*      */     //   3971: new 989	java/lang/StringBuilder
/*      */     //   3974: dup
/*      */     //   3975: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   3978: ldc_w 898
/*      */     //   3981: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3984: aload_0
/*      */     //   3985: iload 11
/*      */     //   3987: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   3990: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   3993: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   3996: aastore
/*      */     //   3997: dup
/*      */     //   3998: bipush 8
/*      */     //   4000: new 989	java/lang/StringBuilder
/*      */     //   4003: dup
/*      */     //   4004: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4007: ldc_w 929
/*      */     //   4010: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4013: aload 4
/*      */     //   4015: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   4018: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4021: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4024: aastore
/*      */     //   4025: dup
/*      */     //   4026: bipush 9
/*      */     //   4028: new 989	java/lang/StringBuilder
/*      */     //   4031: dup
/*      */     //   4032: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4035: ldc_w 895
/*      */     //   4038: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4041: aload_0
/*      */     //   4042: aload_2
/*      */     //   4043: invokevirtual 1519	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:getActivelySeeding	()Z
/*      */     //   4046: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   4049: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4052: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4055: aastore
/*      */     //   4056: dup
/*      */     //   4057: bipush 10
/*      */     //   4059: new 989	java/lang/StringBuilder
/*      */     //   4062: dup
/*      */     //   4063: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4066: ldc_w 931
/*      */     //   4069: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4072: aload_2
/*      */     //   4073: getfield 1418	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultSeeds	I
/*      */     //   4076: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4079: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4082: aastore
/*      */     //   4083: dup
/*      */     //   4084: bipush 11
/*      */     //   4086: new 989	java/lang/StringBuilder
/*      */     //   4089: dup
/*      */     //   4090: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4093: ldc_w 930
/*      */     //   4096: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4099: aload_2
/*      */     //   4100: getfield 1417	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultPeers	I
/*      */     //   4103: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4106: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4109: aastore
/*      */     //   4110: astore 12
/*      */     //   4112: aload_0
/*      */     //   4113: ldc 2
/*      */     //   4115: aload 8
/*      */     //   4117: aload 12
/*      */     //   4119: aload 9
/*      */     //   4121: ldc_w 841
/*      */     //   4124: iconst_1
/*      */     //   4125: aload_2
/*      */     //   4126: invokespecial 1539	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:printDebugChanges	(Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator;)V
/*      */     //   4129: goto +389 -> 4518
/*      */     //   4132: astore 25
/*      */     //   4134: aload_0
/*      */     //   4135: getfield 1457	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bDebugLog	Z
/*      */     //   4138: ifeq +377 -> 4515
/*      */     //   4141: bipush 12
/*      */     //   4143: anewarray 988	java/lang/String
/*      */     //   4146: dup
/*      */     //   4147: iconst_0
/*      */     //   4148: new 989	java/lang/StringBuilder
/*      */     //   4151: dup
/*      */     //   4152: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4155: ldc_w 897
/*      */     //   4158: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4161: ldc_w 846
/*      */     //   4164: aload 5
/*      */     //   4166: invokeinterface 1624 1 0
/*      */     //   4171: invokevirtual 1555	java/lang/String:charAt	(I)C
/*      */     //   4174: invokevirtual 1561	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
/*      */     //   4177: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4180: aastore
/*      */     //   4181: dup
/*      */     //   4182: iconst_1
/*      */     //   4183: new 989	java/lang/StringBuilder
/*      */     //   4186: dup
/*      */     //   4187: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4190: ldc_w 946
/*      */     //   4193: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4196: aload 5
/*      */     //   4198: invokeinterface 1640 1 0
/*      */     //   4203: invokeinterface 1652 1 0
/*      */     //   4208: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4211: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4214: aastore
/*      */     //   4215: dup
/*      */     //   4216: iconst_2
/*      */     //   4217: new 989	java/lang/StringBuilder
/*      */     //   4220: dup
/*      */     //   4221: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4224: ldc_w 932
/*      */     //   4227: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4230: aload_3
/*      */     //   4231: getfield 1483	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrSeeding	I
/*      */     //   4234: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4237: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4240: aastore
/*      */     //   4241: dup
/*      */     //   4242: iconst_3
/*      */     //   4243: new 989	java/lang/StringBuilder
/*      */     //   4246: dup
/*      */     //   4247: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4250: ldc_w 933
/*      */     //   4253: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4256: aload_3
/*      */     //   4257: getfield 1482	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:numWaitingOrDLing	I
/*      */     //   4260: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4263: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4266: aastore
/*      */     //   4267: dup
/*      */     //   4268: iconst_4
/*      */     //   4269: new 989	java/lang/StringBuilder
/*      */     //   4272: dup
/*      */     //   4273: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4276: ldc_w 947
/*      */     //   4279: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4282: aload 5
/*      */     //   4284: invokeinterface 1623 1 0
/*      */     //   4289: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4292: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4295: aastore
/*      */     //   4296: dup
/*      */     //   4297: iconst_5
/*      */     //   4298: new 989	java/lang/StringBuilder
/*      */     //   4301: dup
/*      */     //   4302: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4305: ldc_w 916
/*      */     //   4308: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4311: aload_0
/*      */     //   4312: aload_3
/*      */     //   4313: getfield 1489	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:higherCDtoStart	Z
/*      */     //   4316: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   4319: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4322: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4325: aastore
/*      */     //   4326: dup
/*      */     //   4327: bipush 6
/*      */     //   4329: new 989	java/lang/StringBuilder
/*      */     //   4332: dup
/*      */     //   4333: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4336: ldc_w 924
/*      */     //   4339: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4342: aload 4
/*      */     //   4344: getfield 1501	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:maxSeeders	I
/*      */     //   4347: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4350: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4353: aastore
/*      */     //   4354: dup
/*      */     //   4355: bipush 7
/*      */     //   4357: new 989	java/lang/StringBuilder
/*      */     //   4360: dup
/*      */     //   4361: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4364: ldc_w 898
/*      */     //   4367: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4370: aload_0
/*      */     //   4371: iload 11
/*      */     //   4373: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   4376: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4379: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4382: aastore
/*      */     //   4383: dup
/*      */     //   4384: bipush 8
/*      */     //   4386: new 989	java/lang/StringBuilder
/*      */     //   4389: dup
/*      */     //   4390: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4393: ldc_w 929
/*      */     //   4396: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4399: aload 4
/*      */     //   4401: getfield 1491	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$TotalsStats:activelyCDing	I
/*      */     //   4404: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4407: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4410: aastore
/*      */     //   4411: dup
/*      */     //   4412: bipush 9
/*      */     //   4414: new 989	java/lang/StringBuilder
/*      */     //   4417: dup
/*      */     //   4418: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4421: ldc_w 895
/*      */     //   4424: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4427: aload_0
/*      */     //   4428: aload_2
/*      */     //   4429: invokevirtual 1519	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:getActivelySeeding	()Z
/*      */     //   4432: invokespecial 1531	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:boolDebug	(Z)Ljava/lang/String;
/*      */     //   4435: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4438: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4441: aastore
/*      */     //   4442: dup
/*      */     //   4443: bipush 10
/*      */     //   4445: new 989	java/lang/StringBuilder
/*      */     //   4448: dup
/*      */     //   4449: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4452: ldc_w 931
/*      */     //   4455: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4458: aload_2
/*      */     //   4459: getfield 1418	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultSeeds	I
/*      */     //   4462: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4465: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4468: aastore
/*      */     //   4469: dup
/*      */     //   4470: bipush 11
/*      */     //   4472: new 989	java/lang/StringBuilder
/*      */     //   4475: dup
/*      */     //   4476: invokespecial 1558	java/lang/StringBuilder:<init>	()V
/*      */     //   4479: ldc_w 930
/*      */     //   4482: invokevirtual 1566	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   4485: aload_2
/*      */     //   4486: getfield 1417	com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator:lastModifiedScrapeResultPeers	I
/*      */     //   4489: invokevirtual 1562	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   4492: invokevirtual 1560	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   4495: aastore
/*      */     //   4496: astore 26
/*      */     //   4498: aload_0
/*      */     //   4499: ldc 2
/*      */     //   4501: aload 8
/*      */     //   4503: aload 26
/*      */     //   4505: aload 9
/*      */     //   4507: ldc_w 841
/*      */     //   4510: iconst_1
/*      */     //   4511: aload_2
/*      */     //   4512: invokespecial 1539	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:printDebugChanges	(Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator;)V
/*      */     //   4515: aload 25
/*      */     //   4517: athrow
/*      */     //   4518: aload_0
/*      */     //   4519: getfield 1461	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin:bStopOnceBandwidthMet	Z
/*      */     //   4522: ifeq +24 -> 4546
/*      */     //   4525: aload_3
/*      */     //   4526: dup
/*      */     //   4527: getfield 1487	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:accumulatedUploadSpeed	J
/*      */     //   4530: aload 5
/*      */     //   4532: invokeinterface 1640 1 0
/*      */     //   4537: invokeinterface 1656 1 0
/*      */     //   4542: ladd
/*      */     //   4543: putfield 1487	com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin$ProcessVars:accumulatedUploadSpeed	J
/*      */     //   4546: return
/*      */     // Line number table:
/*      */     //   Java source line #2068	-> byte code offset #0
/*      */     //   Java source line #2069	-> byte code offset #8
/*      */     //   Java source line #2071	-> byte code offset #9
/*      */     //   Java source line #2072	-> byte code offset #15
/*      */     //   Java source line #2073	-> byte code offset #24
/*      */     //   Java source line #2075	-> byte code offset #43
/*      */     //   Java source line #2076	-> byte code offset #46
/*      */     //   Java source line #2089	-> byte code offset #50
/*      */     //   Java source line #2090	-> byte code offset #56
/*      */     //   Java source line #2092	-> byte code offset #59
/*      */     //   Java source line #2093	-> byte code offset #66
/*      */     //   Java source line #2094	-> byte code offset #72
/*      */     //   Java source line #2111	-> byte code offset #424
/*      */     //   Java source line #2116	-> byte code offset #430
/*      */     //   Java source line #2117	-> byte code offset #447
/*      */     //   Java source line #2119	-> byte code offset #454
/*      */     //   Java source line #2120	-> byte code offset #461
/*      */     //   Java source line #2121	-> byte code offset #484
/*      */     //   Java source line #2122	-> byte code offset #491
/*      */     //   Java source line #2123	-> byte code offset #502
/*      */     //   Java source line #2125	-> byte code offset #512
/*      */     //   Java source line #2126	-> byte code offset #521
/*      */     //   Java source line #2127	-> byte code offset #527
/*      */     //   Java source line #2128	-> byte code offset #534
/*      */     //   Java source line #2129	-> byte code offset #557
/*      */     //   Java source line #2130	-> byte code offset #564
/*      */     //   Java source line #2133	-> byte code offset #575
/*      */     //   Java source line #2132	-> byte code offset #578
/*      */     //   Java source line #2135	-> byte code offset #580
/*      */     //   Java source line #2137	-> byte code offset #586
/*      */     //   Java source line #2138	-> byte code offset #593
/*      */     //   Java source line #2139	-> byte code offset #616
/*      */     //   Java source line #2140	-> byte code offset #623
/*      */     //   Java source line #2141	-> byte code offset #634
/*      */     //   Java source line #2143	-> byte code offset #644
/*      */     //   Java source line #2142	-> byte code offset #647
/*      */     //   Java source line #2476	-> byte code offset #649
/*      */     //   Java source line #2477	-> byte code offset #656
/*      */     //   Java source line #2491	-> byte code offset #1013
/*      */     //   Java source line #2493	-> byte code offset #1030
/*      */     //   Java source line #2148	-> byte code offset #1031
/*      */     //   Java source line #2155	-> byte code offset #1040
/*      */     //   Java source line #2158	-> byte code offset #1068
/*      */     //   Java source line #2159	-> byte code offset #1075
/*      */     //   Java source line #2160	-> byte code offset #1098
/*      */     //   Java source line #2161	-> byte code offset #1104
/*      */     //   Java source line #2162	-> byte code offset #1113
/*      */     //   Java source line #2476	-> byte code offset #1145
/*      */     //   Java source line #2477	-> byte code offset #1152
/*      */     //   Java source line #2491	-> byte code offset #1509
/*      */     //   Java source line #2493	-> byte code offset #1526
/*      */     //   Java source line #2173	-> byte code offset #1527
/*      */     //   Java source line #2175	-> byte code offset #1556
/*      */     //   Java source line #2178	-> byte code offset #1579
/*      */     //   Java source line #2180	-> byte code offset #1616
/*      */     //   Java source line #2183	-> byte code offset #1639
/*      */     //   Java source line #2185	-> byte code offset #1646
/*      */     //   Java source line #2188	-> byte code offset #1652
/*      */     //   Java source line #2193	-> byte code offset #1658
/*      */     //   Java source line #2194	-> byte code offset #1665
/*      */     //   Java source line #2195	-> byte code offset #1683
/*      */     //   Java source line #2196	-> byte code offset #1722
/*      */     //   Java source line #2197	-> byte code offset #1759
/*      */     //   Java source line #2198	-> byte code offset #1786
/*      */     //   Java source line #2199	-> byte code offset #1803
/*      */     //   Java source line #2200	-> byte code offset #1808
/*      */     //   Java source line #2201	-> byte code offset #1819
/*      */     //   Java source line #2202	-> byte code offset #1822
/*      */     //   Java source line #2203	-> byte code offset #1825
/*      */     //   Java source line #2204	-> byte code offset #1829
/*      */     //   Java source line #2205	-> byte code offset #1832
/*      */     //   Java source line #2208	-> byte code offset #1835
/*      */     //   Java source line #2209	-> byte code offset #1846
/*      */     //   Java source line #2230	-> byte code offset #1856
/*      */     //   Java source line #2237	-> byte code offset #1913
/*      */     //   Java source line #2238	-> byte code offset #1924
/*      */     //   Java source line #2239	-> byte code offset #1942
/*      */     //   Java source line #2241	-> byte code offset #1959
/*      */     //   Java source line #2242	-> byte code offset #1971
/*      */     //   Java source line #2246	-> byte code offset #2018
/*      */     //   Java source line #2251	-> byte code offset #2064
/*      */     //   Java source line #2252	-> byte code offset #2074
/*      */     //   Java source line #2253	-> byte code offset #2081
/*      */     //   Java source line #2256	-> byte code offset #2104
/*      */     //   Java source line #2258	-> byte code offset #2107
/*      */     //   Java source line #2260	-> byte code offset #2112
/*      */     //   Java source line #2262	-> byte code offset #2120
/*      */     //   Java source line #2264	-> byte code offset #2123
/*      */     //   Java source line #2272	-> byte code offset #2126
/*      */     //   Java source line #2280	-> byte code offset #2182
/*      */     //   Java source line #2281	-> byte code offset #2189
/*      */     //   Java source line #2286	-> byte code offset #2250
/*      */     //   Java source line #2287	-> byte code offset #2257
/*      */     //   Java source line #2288	-> byte code offset #2260
/*      */     //   Java source line #2289	-> byte code offset #2271
/*      */     //   Java source line #2290	-> byte code offset #2281
/*      */     //   Java source line #2291	-> byte code offset #2289
/*      */     //   Java source line #2293	-> byte code offset #2294
/*      */     //   Java source line #2292	-> byte code offset #2297
/*      */     //   Java source line #2294	-> byte code offset #2299
/*      */     //   Java source line #2295	-> byte code offset #2311
/*      */     //   Java source line #2296	-> byte code offset #2325
/*      */     //   Java source line #2297	-> byte code offset #2348
/*      */     //   Java source line #2298	-> byte code offset #2354
/*      */     //   Java source line #2299	-> byte code offset #2377
/*      */     //   Java source line #2300	-> byte code offset #2383
/*      */     //   Java source line #2301	-> byte code offset #2392
/*      */     //   Java source line #2303	-> byte code offset #2424
/*      */     //   Java source line #2304	-> byte code offset #2434
/*      */     //   Java source line #2306	-> byte code offset #2460
/*      */     //   Java source line #2307	-> byte code offset #2465
/*      */     //   Java source line #2309	-> byte code offset #2488
/*      */     //   Java source line #2310	-> byte code offset #2500
/*      */     //   Java source line #2313	-> byte code offset #2553
/*      */     //   Java source line #2314	-> byte code offset #2564
/*      */     //   Java source line #2318	-> byte code offset #2616
/*      */     //   Java source line #2320	-> byte code offset #2621
/*      */     //   Java source line #2324	-> byte code offset #2647
/*      */     //   Java source line #2338	-> byte code offset #2770
/*      */     //   Java source line #2340	-> byte code offset #2773
/*      */     //   Java source line #2343	-> byte code offset #2792
/*      */     //   Java source line #2346	-> byte code offset #2808
/*      */     //   Java source line #2347	-> byte code offset #2815
/*      */     //   Java source line #2351	-> byte code offset #2866
/*      */     //   Java source line #2352	-> byte code offset #2873
/*      */     //   Java source line #2355	-> byte code offset #2876
/*      */     //   Java source line #2353	-> byte code offset #2879
/*      */     //   Java source line #2356	-> byte code offset #2881
/*      */     //   Java source line #2357	-> byte code offset #2890
/*      */     //   Java source line #2358	-> byte code offset #2901
/*      */     //   Java source line #2359	-> byte code offset #2907
/*      */     //   Java source line #2360	-> byte code offset #2920
/*      */     //   Java source line #2363	-> byte code offset #2925
/*      */     //   Java source line #2369	-> byte code offset #2928
/*      */     //   Java source line #2371	-> byte code offset #2938
/*      */     //   Java source line #2372	-> byte code offset #2942
/*      */     //   Java source line #2378	-> byte code offset #2947
/*      */     //   Java source line #2384	-> byte code offset #3009
/*      */     //   Java source line #2388	-> byte code offset #3022
/*      */     //   Java source line #2395	-> byte code offset #3080
/*      */     //   Java source line #2396	-> byte code offset #3087
/*      */     //   Java source line #2397	-> byte code offset #3092
/*      */     //   Java source line #2398	-> byte code offset #3115
/*      */     //   Java source line #2399	-> byte code offset #3120
/*      */     //   Java source line #2400	-> byte code offset #3127
/*      */     //   Java source line #2401	-> byte code offset #3153
/*      */     //   Java source line #2402	-> byte code offset #3170
/*      */     //   Java source line #2404	-> byte code offset #3196
/*      */     //   Java source line #2406	-> byte code offset #3222
/*      */     //   Java source line #2407	-> byte code offset #3228
/*      */     //   Java source line #2409	-> byte code offset #3251
/*      */     //   Java source line #2410	-> byte code offset #3274
/*      */     //   Java source line #2411	-> byte code offset #3279
/*      */     //   Java source line #2412	-> byte code offset #3305
/*      */     //   Java source line #2413	-> byte code offset #3310
/*      */     //   Java source line #2414	-> byte code offset #3336
/*      */     //   Java source line #2415	-> byte code offset #3346
/*      */     //   Java source line #2418	-> byte code offset #3372
/*      */     //   Java source line #2419	-> byte code offset #3395
/*      */     //   Java source line #2420	-> byte code offset #3405
/*      */     //   Java source line #2421	-> byte code offset #3431
/*      */     //   Java source line #2422	-> byte code offset #3441
/*      */     //   Java source line #2423	-> byte code offset #3467
/*      */     //   Java source line #2424	-> byte code offset #3472
/*      */     //   Java source line #2428	-> byte code offset #3525
/*      */     //   Java source line #2432	-> byte code offset #3564
/*      */     //   Java source line #2433	-> byte code offset #3567
/*      */     //   Java source line #2434	-> byte code offset #3574
/*      */     //   Java source line #2437	-> byte code offset #3597
/*      */     //   Java source line #2439	-> byte code offset #3602
/*      */     //   Java source line #2440	-> byte code offset #3608
/*      */     //   Java source line #2442	-> byte code offset #3619
/*      */     //   Java source line #2443	-> byte code offset #3626
/*      */     //   Java source line #2446	-> byte code offset #3631
/*      */     //   Java source line #2447	-> byte code offset #3641
/*      */     //   Java source line #2448	-> byte code offset #3652
/*      */     //   Java source line #2450	-> byte code offset #3655
/*      */     //   Java source line #2452	-> byte code offset #3660
/*      */     //   Java source line #2453	-> byte code offset #3670
/*      */     //   Java source line #2458	-> byte code offset #3673
/*      */     //   Java source line #2459	-> byte code offset #3679
/*      */     //   Java source line #2462	-> byte code offset #3690
/*      */     //   Java source line #2460	-> byte code offset #3693
/*      */     //   Java source line #2464	-> byte code offset #3695
/*      */     //   Java source line #2468	-> byte code offset #3704
/*      */     //   Java source line #2469	-> byte code offset #3713
/*      */     //   Java source line #2472	-> byte code offset #3743
/*      */     //   Java source line #2476	-> byte code offset #3748
/*      */     //   Java source line #2477	-> byte code offset #3755
/*      */     //   Java source line #2491	-> byte code offset #4112
/*      */     //   Java source line #2493	-> byte code offset #4129
/*      */     //   Java source line #2476	-> byte code offset #4132
/*      */     //   Java source line #2477	-> byte code offset #4141
/*      */     //   Java source line #2491	-> byte code offset #4498
/*      */     //   Java source line #2493	-> byte code offset #4515
/*      */     //   Java source line #2497	-> byte code offset #4518
/*      */     //   Java source line #2498	-> byte code offset #4525
/*      */     //   Java source line #2500	-> byte code offset #4546
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	4547	0	this	StartStopRulesDefaultPlugin
/*      */     //   0	4547	1	dlDataArray	DefaultRankCalculator[]
/*      */     //   0	4547	2	dlData	DefaultRankCalculator
/*      */     //   0	4547	3	vars	ProcessVars
/*      */     //   0	4547	4	totals	TotalsStats
/*      */     //   13	4518	5	download	Download
/*      */     //   22	3716	6	state	int
/*      */     //   41	1816	7	stateReadyOrSeeding	boolean
/*      */     //   44	4458	8	debugEntries	String[]
/*      */     //   48	4458	9	sDebugLine	String
/*      */     //   54	1540	10	numPeers	int
/*      */     //   57	4315	11	isFP	boolean
/*      */     //   428	1171	12	bScrapeOk	boolean
/*      */     //   4110	8	12	debugEntries2	String[]
/*      */     //   578	3	13	ignore	Exception
/*      */     //   647	3	13	ignore	Exception
/*      */     //   1011	8	13	debugEntries2	String[]
/*      */     //   1038	2676	13	rank	int
/*      */     //   1102	33	14	idx	int
/*      */     //   1507	8	14	debugEntries2	String[]
/*      */     //   1656	1998	14	bActivelySeeding	boolean
/*      */     //   1757	18	15	globalDownLimitReached	boolean
/*      */     //   1830	3	15	globalDownLimitReached	boolean
/*      */     //   1720	50	16	globalUpLimitReached	boolean
/*      */     //   1823	3	16	globalUpLimitReached	boolean
/*      */     //   1784	3	17	globalRateAdjustedActivelySeeding	boolean
/*      */     //   1827	1845	17	globalRateAdjustedActivelySeeding	boolean
/*      */     //   1801	3	18	fakedActively	boolean
/*      */     //   1833	1804	18	fakedActively	boolean
/*      */     //   1681	84	19	isRunning	boolean
/*      */     //   1911	1018	19	okToQueue	boolean
/*      */     //   1940	50	20	timeAlive	long
/*      */     //   2105	512	20	up_limit_prohibits	boolean
/*      */     //   2297	3	21	ignore	Exception
/*      */     //   2381	33	21	idx	int
/*      */     //   2771	168	21	bForceStop	boolean
/*      */     //   2879	3	22	ignore	Exception
/*      */     //   2940	658	22	okToStop	boolean
/*      */     //   3007	461	23	bOverLimit	boolean
/*      */     //   3693	3	23	ignore	Exception
/*      */     //   3020	535	24	bSeeding	boolean
/*      */     //   4132	384	25	localObject	Object
/*      */     //   4496	8	26	debugEntries2	String[]
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   454	575	578	java/lang/Exception
/*      */     //   586	644	647	java/lang/Exception
/*      */     //   2182	2294	2297	java/lang/Exception
/*      */     //   2808	2876	2879	java/lang/Exception
/*      */     //   3602	3690	3693	java/lang/Exception
/*      */     //   424	649	4132	finally
/*      */     //   1031	1145	4132	finally
/*      */     //   1527	3748	4132	finally
/*      */     //   4132	4134	4132	finally
/*      */   }
/*      */   
/*      */   private static class ProcessVars
/*      */   {
/*      */     int numWaitingOrSeeding;
/*      */     int numWaitingOrDLing;
/*      */     long accumulatedDownloadSpeed;
/*      */     long accumulatedUploadSpeed;
/*      */     boolean higherCDtoStart;
/*      */     boolean higherDLtoStart;
/*      */     int posComplete;
/*      */     boolean bStopAndQueued;
/*      */     int stalledSeeders;
/*      */   }
/*      */   
/*      */   public static abstract interface UIAdapter
/*      */   {
/*      */     public abstract void openDebugWindow(DefaultRankCalculator paramDefaultRankCalculator);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/StartStopRulesDefaultPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */