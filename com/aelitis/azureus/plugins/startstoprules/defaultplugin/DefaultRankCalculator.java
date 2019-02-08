/*      */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin;
/*      */ 
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManagerStats;
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
/*      */ public class DefaultRankCalculator
/*      */   implements DownloadManagerStateAttributeListener, Comparable
/*      */ {
/*      */   public static final int FIRSTPRIORITY_ALL = 0;
/*      */   public static final int FIRSTPRIORITY_ANY = 1;
/*      */   public static final int DOWNLOAD_ORDER_INDEX = 0;
/*      */   public static final int DOWNLOAD_ORDER_SEED_COUNT = 1;
/*      */   public static final int DOWNLOAD_ORDER_SPEED = 2;
/*      */   private static final int FORCE_ACTIVE_FOR = 30000;
/*      */   private static final int ACTIVE_CHANGE_WAIT = 10000;
/*   68 */   private static int SPRATIO_BASE_LIMIT = 99999;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   74 */   private static int SEEDONLY_SHIFT = SPRATIO_BASE_LIMIT + 1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   79 */   private static COConfigurationListener configListener = null;
/*      */   
/*      */ 
/*      */ 
/*      */   public static final int SR_COMPLETE_STARTS_AT = 1000000000;
/*      */   
/*      */ 
/*      */ 
/*      */   public static final int SR_TIMED_QUEUED_ENDS_AT = 199999999;
/*      */   
/*      */ 
/*      */   public static final int SR_IGNORED_LESS_THAN = -1;
/*      */   
/*      */ 
/*      */   public static final int SR_NOTQUEUED = -2;
/*      */   
/*      */ 
/*      */   public static final int SR_FP_SPRATIOMET = -3;
/*      */   
/*      */ 
/*      */   public static final int SR_RATIOMET = -4;
/*      */   
/*      */ 
/*      */   public static final int SR_NUMSEEDSMET = -5;
/*      */   
/*      */ 
/*      */   public static final int SR_FP0PEERS = -6;
/*      */   
/*      */ 
/*      */   public static final int SR_0PEERS = -7;
/*      */   
/*      */ 
/*      */   public static final int SR_SHARERATIOMET = -8;
/*      */   
/*      */ 
/*  114 */   public static final String[] SR_NEGATIVE_DEBUG = { "?", "Not Qd", "FP SPRatioMet", "Ratio Met", "# CDs Met", "FP 0 Peers", "0 Peers", "Share Ratio Met" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final long STALE_REFRESH_INTERVAL = 60000L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  131 */   protected static int iRankType = -1;
/*      */   
/*      */ 
/*      */   private static int minPeersToBoostNoSeeds;
/*      */   
/*      */ 
/*      */   private static int minSpeedForActiveDL;
/*      */   
/*      */ 
/*      */   private static int minSpeedForActiveSeeding;
/*      */   
/*      */ 
/*      */   private static int iIgnoreSeedCount;
/*      */   
/*      */ 
/*      */   private static boolean bIgnore0Peers;
/*      */   
/*      */ 
/*      */   private static int iIgnoreShareRatio;
/*      */   
/*      */ 
/*      */   private static int iIgnoreShareRatio_SeedStart;
/*      */   
/*      */ 
/*      */   private static int iIgnoreRatioPeers;
/*      */   
/*      */ 
/*      */   private static int iIgnoreRatioPeers_SeedStart;
/*      */   
/*      */   private static int iRankTypeSeedFallback;
/*      */   
/*      */   private static boolean bPreferLargerSwarms;
/*      */   
/*      */   private static int minQueueingShareRatio;
/*      */   
/*      */   private static int iFirstPriorityIgnoreSPRatio;
/*      */   
/*      */   private static boolean bFirstPriorityIgnore0Peer;
/*      */   
/*      */   private static int iFirstPriorityType;
/*      */   
/*      */   private static int iFirstPrioritySeedingMinutes;
/*      */   
/*      */   private static int iFirstPriorityActiveMinutes;
/*      */   
/*      */   private static int iFirstPriorityIgnoreIdleHours;
/*      */   
/*      */   private static long minTimeAlive;
/*      */   
/*      */   private static boolean bAutoStart0Peers;
/*      */   
/*      */   private static int iTimed_MinSeedingTimeWithPeers;
/*      */   
/*      */   protected final Download dl;
/*      */   
/*      */   private boolean bActivelyDownloading;
/*      */   
/*      */   private long lDLActivelyChangedOn;
/*      */   
/*      */   private boolean bActivelySeeding;
/*      */   
/*      */   private long lCDActivelyChangedOn;
/*      */   
/*      */   private long staleCDSince;
/*      */   
/*      */   private long staleCDOffset;
/*      */   
/*      */   private long lastStaleCDRefresh;
/*      */   
/*      */   private boolean bIsFirstPriority;
/*      */   
/*      */   private int dlSpecificMinShareRatio;
/*      */   
/*      */   private int dlSpecificMaxShareRatio;
/*      */   
/*      */   private long dlLastActiveTime;
/*      */   
/*  208 */   public String sExplainFP = "";
/*      */   
/*      */ 
/*  211 */   public String sExplainSR = "";
/*      */   
/*      */ 
/*  214 */   public String sTrace = "";
/*      */   
/*  216 */   private AEMonitor downloadData_this_mon = new AEMonitor("StartStopRules:downloadData");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final StartStopRulesDefaultPlugin rules;
/*      */   
/*      */ 
/*      */ 
/*  225 */   int lastModifiedScrapeResultPeers = 0;
/*  226 */   int lastModifiedScrapeResultSeeds = 0;
/*  227 */   int lastModifiedShareRatio = 0;
/*      */   
/*  229 */   boolean lastScrapeResultOk = false;
/*      */   
/*      */   private boolean dlr_test_active;
/*      */   
/*      */   private long dlr_test_start_time;
/*      */   private long dlr_test_bytes_start;
/*      */   
/*      */   public DefaultRankCalculator(StartStopRulesDefaultPlugin _rules, Download _dl)
/*      */   {
/*  238 */     this.rules = _rules;
/*  239 */     this.dl = _dl;
/*      */     
/*  241 */     DownloadManager core_dm = PluginCoreUtils.unwrap(this.dl);
/*      */     
/*  243 */     DownloadManagerState dm_state = core_dm.getDownloadState();
/*      */     
/*  245 */     this.dlSpecificMinShareRatio = dm_state.getIntParameter("sr.min");
/*  246 */     this.dlSpecificMaxShareRatio = dm_state.getIntParameter("sr.max");
/*  247 */     this.dlLastActiveTime = dm_state.getLongParameter("stats.download.last.active.time");
/*  248 */     if (this.dlLastActiveTime <= 0L) {
/*  249 */       this.dlLastActiveTime = dm_state.getLongParameter("stats.download.completed.time");
/*      */     }
/*      */     
/*  252 */     dm_state.addListener(this, "parameters", 1);
/*      */     try
/*      */     {
/*  255 */       this.downloadData_this_mon.enter();
/*      */       
/*  257 */       if (configListener == null)
/*      */       {
/*  259 */         configListener = new COConfigurationListener() {
/*      */           public void configurationSaved() {
/*  261 */             DefaultRankCalculator.reloadConfigParams(DefaultRankCalculator.this.rules.plugin_config);
/*      */           }
/*      */           
/*  264 */         };
/*  265 */         COConfigurationManager.addListener(configListener);
/*  266 */         configListener.configurationSaved();
/*      */       }
/*      */     } finally {
/*  269 */       this.downloadData_this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void attributeEventOccurred(DownloadManager download, String attribute, int event_type)
/*      */   {
/*  279 */     DownloadManager core_dm = PluginCoreUtils.unwrap(this.dl);
/*      */     
/*  281 */     DownloadManagerState dm_state = core_dm.getDownloadState();
/*      */     
/*  283 */     this.dlSpecificMinShareRatio = dm_state.getIntParameter("sr.min");
/*  284 */     this.dlSpecificMaxShareRatio = dm_state.getIntParameter("sr.max");
/*  285 */     this.dlLastActiveTime = dm_state.getLongParameter("stats.download.last.active.time");
/*  286 */     if (this.dlLastActiveTime <= 0L) {
/*  287 */       this.dlLastActiveTime = dm_state.getLongParameter("stats.download.completed.time");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/*  294 */     DownloadManager core_dm = PluginCoreUtils.unwrap(this.dl);
/*      */     
/*  296 */     DownloadManagerState dm_state = core_dm.getDownloadState();
/*      */     
/*  298 */     dm_state.removeListener(this, "parameters", 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void reloadConfigParams(PluginConfig cfg)
/*      */   {
/*  307 */     String PREFIX = "StartStopManager_";
/*      */     
/*  309 */     iRankType = cfg.getUnsafeIntParameter("StartStopManager_iRankType");
/*      */     
/*  311 */     minPeersToBoostNoSeeds = cfg.getUnsafeIntParameter("StartStopManager_iMinPeersToBoostNoSeeds");
/*      */     
/*  313 */     minSpeedForActiveDL = cfg.getUnsafeIntParameter("StartStopManager_iMinSpeedForActiveDL");
/*  314 */     minSpeedForActiveSeeding = cfg.getUnsafeIntParameter("StartStopManager_iMinSpeedForActiveSeeding");
/*      */     
/*      */ 
/*  317 */     iRankTypeSeedFallback = cfg.getUnsafeIntParameter("StartStopManager_iRankTypeSeedFallback");
/*      */     
/*  319 */     bPreferLargerSwarms = cfg.getUnsafeBooleanParameter("StartStopManager_bPreferLargerSwarms");
/*      */     
/*  321 */     minTimeAlive = cfg.getUnsafeIntParameter("StartStopManager_iMinSeedingTime") * 1000;
/*  322 */     bAutoStart0Peers = cfg.getUnsafeBooleanParameter("StartStopManager_bAutoStart0Peers");
/*      */     
/*      */ 
/*  325 */     iIgnoreSeedCount = cfg.getUnsafeIntParameter("StartStopManager_iIgnoreSeedCount");
/*  326 */     bIgnore0Peers = cfg.getUnsafeBooleanParameter("StartStopManager_bIgnore0Peers");
/*  327 */     iIgnoreShareRatio = (int)(1000.0F * cfg.getFloatParameter("Stop Ratio"));
/*  328 */     iIgnoreShareRatio_SeedStart = cfg.getUnsafeIntParameter("StartStopManager_iIgnoreShareRatioSeedStart");
/*      */     
/*  330 */     iIgnoreRatioPeers = cfg.getIntParameter("Stop Peers Ratio", 0);
/*  331 */     iIgnoreRatioPeers_SeedStart = cfg.getUnsafeIntParameter("StartStopManager_iIgnoreRatioPeersSeedStart", 0);
/*      */     
/*      */ 
/*  334 */     minQueueingShareRatio = cfg.getUnsafeIntParameter("StartStopManager_iFirstPriority_ShareRatio");
/*      */     
/*  336 */     iFirstPriorityType = cfg.getUnsafeIntParameter("StartStopManager_iFirstPriority_Type");
/*  337 */     iFirstPrioritySeedingMinutes = cfg.getUnsafeIntParameter("StartStopManager_iFirstPriority_SeedingMinutes");
/*      */     
/*  339 */     iFirstPriorityActiveMinutes = cfg.getUnsafeIntParameter("StartStopManager_iFirstPriority_DLMinutes");
/*      */     
/*      */ 
/*  342 */     iFirstPriorityIgnoreSPRatio = cfg.getUnsafeIntParameter("StartStopManager_iFirstPriority_ignoreSPRatio");
/*      */     
/*  344 */     bFirstPriorityIgnore0Peer = cfg.getUnsafeBooleanParameter("StartStopManager_bFirstPriority_ignore0Peer");
/*      */     
/*  346 */     iFirstPriorityIgnoreIdleHours = cfg.getUnsafeIntParameter("StartStopManager_iFirstPriority_ignoreIdleHours");
/*      */     
/*  348 */     iTimed_MinSeedingTimeWithPeers = cfg.getUnsafeIntParameter("StartStopManager_iTimed_MinSeedingTimeWithPeers") * 1000;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int compareTo(Object obj)
/*      */   {
/*  355 */     if (!(obj instanceof DefaultRankCalculator)) {
/*  356 */       return -1;
/*      */     }
/*      */     
/*  359 */     DefaultRankCalculator dlData = (DefaultRankCalculator)obj;
/*      */     
/*      */ 
/*  362 */     if ((dlData.bIsFirstPriority) && (!this.bIsFirstPriority))
/*  363 */       return 1;
/*  364 */     if ((!dlData.bIsFirstPriority) && (this.bIsFirstPriority)) {
/*  365 */       return -1;
/*      */     }
/*      */     
/*  368 */     boolean aIsComplete = dlData.dl.isComplete();
/*  369 */     boolean bIsComplete = this.dl.isComplete();
/*  370 */     if ((aIsComplete) && (!bIsComplete))
/*  371 */       return -1;
/*  372 */     if ((!aIsComplete) && (bIsComplete)) {
/*  373 */       return 1;
/*      */     }
/*  375 */     if (iRankType == 0) {
/*  376 */       return this.dl.getPosition() - dlData.dl.getPosition();
/*      */     }
/*      */     
/*      */ 
/*  380 */     int value = dlData.dl.getSeedingRank() - this.dl.getSeedingRank();
/*  381 */     if (value != 0) {
/*  382 */       return value;
/*      */     }
/*  384 */     if (iRankType != 3)
/*      */     {
/*  386 */       int numPeersThem = dlData.lastModifiedScrapeResultPeers;
/*  387 */       int numPeersUs = this.lastModifiedScrapeResultPeers;
/*  388 */       if (bPreferLargerSwarms) {
/*  389 */         value = numPeersThem - numPeersUs;
/*      */       } else
/*  391 */         value = numPeersUs - numPeersThem;
/*  392 */       if (value != 0) {
/*  393 */         return value;
/*      */       }
/*      */       
/*  396 */       value = this.lastModifiedShareRatio - dlData.lastModifiedShareRatio;
/*  397 */       if (value != 0) {
/*  398 */         return value;
/*      */       }
/*      */     }
/*      */     
/*  402 */     return this.dl.getPosition() - dlData.dl.getPosition();
/*      */   }
/*      */   
/*      */   public Download getDownloadObject() {
/*  406 */     return this.dl;
/*      */   }
/*      */   
/*      */   public boolean isForceActive() {
/*  410 */     DownloadStats stats = this.dl.getStats();
/*  411 */     return SystemTime.getCurrentTime() - stats.getTimeStarted() <= 30000L;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isQueued()
/*      */   {
/*  417 */     return this.dl.getState() == 9;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getActivelyDownloading()
/*      */   {
/*  426 */     boolean bIsActive = false;
/*  427 */     DownloadStats stats = this.dl.getStats();
/*  428 */     int state = this.dl.getState();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  433 */     if (state != 4) {
/*  434 */       bIsActive = false;
/*  435 */     } else if (SystemTime.getCurrentTime() - stats.getTimeStarted() <= 30000L) {
/*  436 */       bIsActive = true;
/*      */     }
/*      */     else {
/*  439 */       bIsActive = stats.getDownloadAverage() >= minSpeedForActiveDL;
/*      */       
/*  441 */       if (this.bActivelyDownloading != bIsActive) {
/*  442 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  444 */         if (this.lDLActivelyChangedOn == -1L)
/*      */         {
/*  446 */           this.lDLActivelyChangedOn = now;
/*  447 */           bIsActive = !bIsActive;
/*  448 */         } else if (now - this.lDLActivelyChangedOn < 10000L)
/*      */         {
/*  450 */           bIsActive = !bIsActive;
/*      */         }
/*      */       }
/*      */       else {
/*  454 */         this.lDLActivelyChangedOn = -1L;
/*      */       }
/*      */     }
/*      */     
/*  458 */     if (this.bActivelyDownloading != bIsActive) {
/*  459 */       this.bActivelyDownloading = bIsActive;
/*  460 */       if (this.rules != null) {
/*  461 */         this.rules.requestProcessCycle(null);
/*  462 */         if (this.rules.bDebugLog) {
/*  463 */           this.rules.log.log(this.dl.getTorrent(), 1, "somethingChanged: ActivelyDownloading changed");
/*      */         }
/*      */       }
/*      */     }
/*  467 */     return this.bActivelyDownloading;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getActivelySeeding()
/*      */   {
/*  476 */     boolean bIsActive = false;
/*  477 */     DownloadStats stats = this.dl.getStats();
/*  478 */     int state = this.dl.getState();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  483 */     if ((iRankType == 3) && (!isFirstPriority()) && ((!bAutoStart0Peers) || (this.rules.calcPeersNoUs(this.dl, this.dl.getAggregatedScrapeResult()) != 0) || (!this.lastScrapeResultOk)))
/*      */     {
/*      */ 
/*  486 */       bIsActive = state == 5;
/*      */     }
/*  488 */     else if ((state != 5) || ((bAutoStart0Peers) && (this.rules.calcPeersNoUs(this.dl, this.dl.getAggregatedScrapeResult()) == 0)))
/*      */     {
/*      */ 
/*      */ 
/*  492 */       bIsActive = false;
/*  493 */       this.staleCDSince = -1L;
/*  494 */     } else if (SystemTime.getCurrentTime() - stats.getTimeStarted() <= 30000L) {
/*  495 */       bIsActive = true;
/*  496 */       this.staleCDSince = -1L;
/*      */     } else {
/*  498 */       bIsActive = stats.getUploadAverage() >= minSpeedForActiveSeeding;
/*      */       
/*  500 */       if (this.bActivelySeeding != bIsActive) {
/*  501 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  503 */         if (this.lCDActivelyChangedOn < 0L)
/*      */         {
/*  505 */           this.lCDActivelyChangedOn = now;
/*  506 */           bIsActive = !bIsActive;
/*  507 */         } else if (now - this.lCDActivelyChangedOn < 10000L)
/*      */         {
/*  509 */           bIsActive = !bIsActive;
/*      */         }
/*      */         
/*  512 */         if (this.bActivelySeeding != bIsActive) {
/*  513 */           if (bIsActive) {
/*  514 */             this.staleCDSince = -1L;
/*  515 */             this.staleCDOffset = 0L;
/*      */           } else {
/*  517 */             this.staleCDSince = System.currentTimeMillis();
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  523 */         this.lCDActivelyChangedOn = -1L;
/*      */       }
/*      */     }
/*      */     
/*  527 */     if (this.bActivelySeeding != bIsActive) {
/*  528 */       this.bActivelySeeding = bIsActive;
/*      */       
/*  530 */       if (this.rules != null) {
/*  531 */         this.rules.requestProcessCycle(null);
/*  532 */         if (this.rules.bDebugLog) {
/*  533 */           this.rules.log.log(this.dl.getTorrent(), 1, "somethingChanged: ActivelySeeding changed");
/*      */         }
/*      */       }
/*      */     }
/*  537 */     return this.bActivelySeeding;
/*      */   }
/*      */   
/*      */ 
/*      */   public int recalcSeedingRank()
/*      */   {
/*      */     try
/*      */     {
/*  545 */       this.downloadData_this_mon.enter();
/*      */       
/*  547 */       int oldSR = this.dl.getSeedingRank();
/*      */       
/*  549 */       int newSR = _recalcSeedingRankSupport(oldSR);
/*      */       
/*  551 */       if (newSR != oldSR)
/*      */       {
/*  553 */         this.dl.setSeedingRank(newSR);
/*      */       }
/*  555 */       return newSR;
/*      */     }
/*      */     finally
/*      */     {
/*  559 */       this.downloadData_this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private int _recalcSeedingRankSupport(int oldSR)
/*      */   {
/*  565 */     this.sExplainSR = "";
/*      */     
/*  567 */     DownloadStats stats = this.dl.getStats();
/*      */     
/*  569 */     int newSR = 0;
/*      */     
/*      */ 
/*  572 */     if (!this.dl.isComplete()) {
/*  573 */       newSR = 1000000000 + (10000 - this.dl.getPosition());
/*      */       
/*      */ 
/*  576 */       isFirstPriority();
/*  577 */       if (this.rules.bDebugLog) {
/*  578 */         this.sExplainSR = (this.sExplainSR + "  not complete. SetSR " + newSR + "\n");
/*      */       }
/*  580 */       return newSR;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  585 */     this.lastModifiedShareRatio = stats.getShareRatio();
/*  586 */     DownloadScrapeResult sr = this.dl.getAggregatedScrapeResult();
/*  587 */     this.lastModifiedScrapeResultPeers = this.rules.calcPeersNoUs(this.dl, sr);
/*  588 */     this.lastModifiedScrapeResultSeeds = this.rules.calcSeedsNoUs(this.dl, sr);
/*      */     
/*  590 */     boolean bScrapeResultsOk = ((this.lastModifiedScrapeResultPeers > 0) || (this.lastModifiedScrapeResultSeeds > 0) || (this.lastScrapeResultOk)) && (this.lastModifiedScrapeResultPeers >= 0) && (this.lastModifiedScrapeResultSeeds >= 0);
/*      */     
/*      */ 
/*  593 */     if (!isFirstPriority())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  599 */       int activeMaxSR = this.dlSpecificMaxShareRatio;
/*  600 */       if (activeMaxSR <= 0) {
/*  601 */         activeMaxSR = iIgnoreShareRatio;
/*      */       }
/*  603 */       if ((activeMaxSR != 0) && (this.lastModifiedShareRatio >= activeMaxSR) && ((this.lastModifiedScrapeResultSeeds >= iIgnoreShareRatio_SeedStart) || (!bScrapeResultsOk)) && (this.lastModifiedShareRatio != -1))
/*      */       {
/*      */ 
/*      */ 
/*  607 */         if (this.rules.bDebugLog) {
/*  608 */           this.sExplainSR = (this.sExplainSR + "  shareratio met: shareRatio(" + this.lastModifiedShareRatio + ") >= " + activeMaxSR + "\n");
/*      */         }
/*      */         
/*  611 */         return -8; }
/*  612 */       if ((this.rules.bDebugLog) && (activeMaxSR != 0) && (this.lastModifiedShareRatio >= activeMaxSR))
/*      */       {
/*  614 */         this.sExplainSR += "  shareratio NOT met: ";
/*  615 */         if (this.lastModifiedScrapeResultSeeds >= iIgnoreShareRatio_SeedStart) {
/*  616 */           this.sExplainSR = (this.sExplainSR + this.lastModifiedScrapeResultSeeds + " below seed threshold of " + iIgnoreShareRatio_SeedStart);
/*      */         }
/*  618 */         this.sExplainSR += "\n";
/*      */       }
/*      */       
/*  621 */       if ((this.lastModifiedScrapeResultPeers == 0) && (bScrapeResultsOk))
/*      */       {
/*      */ 
/*      */ 
/*  625 */         if (bIgnore0Peers) {
/*  626 */           if (this.rules.bDebugLog) {
/*  627 */             this.sExplainSR += "  Ignore 0 Peers criteria met\n";
/*      */           }
/*  629 */           return -7;
/*      */ 
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*  638 */       else if ((this.rules.bDebugLog) && (this.lastModifiedScrapeResultPeers == 0)) {
/*  639 */         this.sExplainSR += "  0 Peer Ignore rule NOT applied: Scrape invalid\n";
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
/*  654 */       if ((iIgnoreSeedCount != 0) && (this.lastModifiedScrapeResultSeeds >= iIgnoreSeedCount)) {
/*  655 */         if (this.rules.bDebugLog) {
/*  656 */           this.sExplainSR = (this.sExplainSR + "  SeedCount Ignore rule met.  numSeeds(" + this.lastModifiedScrapeResultSeeds + " >= iIgnoreSeedCount(" + iIgnoreSeedCount + ")\n");
/*      */         }
/*      */         
/*  659 */         return -5;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  665 */       if ((iIgnoreRatioPeers != 0) && (this.lastModifiedScrapeResultSeeds != 0)) {
/*  666 */         float ratio = this.lastModifiedScrapeResultPeers / this.lastModifiedScrapeResultSeeds;
/*  667 */         if ((ratio <= iIgnoreRatioPeers) && (this.lastModifiedScrapeResultSeeds >= iIgnoreRatioPeers_SeedStart))
/*      */         {
/*      */ 
/*  670 */           if (this.rules.bDebugLog) {
/*  671 */             this.sExplainSR = (this.sExplainSR + "  P:S Ignore rule met.  ratio(" + ratio + " <= threshold(" + iIgnoreRatioPeers_SeedStart + ")\n");
/*      */           }
/*      */           
/*  674 */           return -4;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  680 */     if (iRankType == 0) {
/*  681 */       if (this.rules.bDebugLog) {
/*  682 */         this.sExplainSR += "  Ranking Type set to none.. blanking seeding rank\n";
/*      */       }
/*      */       
/*  685 */       return newSR;
/*      */     }
/*      */     
/*  688 */     if (iRankType == 3) {
/*  689 */       if (this.bIsFirstPriority) {
/*  690 */         newSR += 200000000;
/*  691 */         return newSR;
/*      */       }
/*      */       
/*  694 */       int state = this.dl.getState();
/*  695 */       if ((state == 6) || (state == 7) || (state == 8))
/*      */       {
/*  697 */         if (this.rules.bDebugLog)
/*  698 */           this.sExplainSR += "  Download stopping, stopped or in error\n";
/*  699 */         return -2; }
/*  700 */       if ((state == 5) || (state == 3) || (state == 1) || (state == 2))
/*      */       {
/*      */ 
/*  703 */         long lMsElapsed = 0L;
/*  704 */         long lMsTimeToSeedFor = minTimeAlive;
/*  705 */         if ((state == 5) && (!this.dl.isForceStart())) {
/*  706 */           lMsElapsed = SystemTime.getCurrentTime() - stats.getTimeStartedSeeding();
/*      */           
/*  708 */           if (iTimed_MinSeedingTimeWithPeers > 0) {
/*  709 */             PeerManager peerManager = this.dl.getPeerManager();
/*  710 */             if (peerManager != null) {
/*  711 */               int connectedLeechers = peerManager.getStats().getConnectedLeechers();
/*  712 */               if (connectedLeechers > 0) {
/*  713 */                 lMsTimeToSeedFor = iTimed_MinSeedingTimeWithPeers;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  719 */         if (lMsElapsed >= lMsTimeToSeedFor) {
/*  720 */           newSR = 1;
/*  721 */           if (oldSR > 199999999) {
/*  722 */             this.rules.requestProcessCycle(null);
/*  723 */             if (this.rules.bDebugLog) {
/*  724 */               this.rules.log.log(this.dl.getTorrent(), 1, "somethingChanged: TimeUp");
/*      */             }
/*      */           }
/*      */         } else {
/*  728 */           newSR = 200000000 + (int)(lMsElapsed / 1000L);
/*  729 */           if (oldSR <= 199999999) {
/*  730 */             this.rules.requestProcessCycle(null);
/*  731 */             if (this.rules.bDebugLog) {
/*  732 */               this.rules.log.log(this.dl.getTorrent(), 1, "somethingChanged: strange timer change");
/*      */             }
/*      */           }
/*      */         }
/*  736 */         return newSR;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  741 */       if (this.dlLastActiveTime == 0L) {
/*  742 */         long diff = this.dl.getStats().getSecondsOnlySeeding();
/*  743 */         if (diff > 199899999L)
/*      */         {
/*  745 */           diff = 199899999 + this.dl.getPosition();
/*      */         }
/*  747 */         newSR = 199999999 - (int)diff;
/*      */       } else {
/*  749 */         long diff = System.currentTimeMillis() / 1000L - this.dlLastActiveTime / 1000L;
/*  750 */         if (diff >= 199999999L) {
/*  751 */           newSR = 199999998;
/*      */         } else {
/*  753 */           newSR = (int)diff;
/*      */         }
/*      */       }
/*  756 */       return newSR;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  765 */     if (bScrapeResultsOk) {
/*  766 */       if (iRankType == 4)
/*      */       {
/*  768 */         if (this.lastModifiedScrapeResultPeers > this.lastModifiedScrapeResultSeeds * 10) {
/*  769 */           newSR = 100 * this.lastModifiedScrapeResultPeers * 10;
/*      */         } else {
/*  771 */           newSR = (int)(100L * this.lastModifiedScrapeResultPeers * this.lastModifiedScrapeResultPeers / (this.lastModifiedScrapeResultSeeds + 1));
/*      */         }
/*  773 */       } else if ((iRankType == 2) && ((iRankTypeSeedFallback == 0) || (iRankTypeSeedFallback > this.lastModifiedScrapeResultSeeds)))
/*      */       {
/*      */ 
/*  776 */         if (this.lastModifiedScrapeResultSeeds < 10000) {
/*  777 */           newSR = 10000 - this.lastModifiedScrapeResultSeeds;
/*      */         } else {
/*  779 */           newSR = 1;
/*      */         }
/*  781 */         newSR *= SEEDONLY_SHIFT;
/*      */ 
/*      */       }
/*  784 */       else if (this.lastModifiedScrapeResultPeers != 0) {
/*  785 */         if (this.lastModifiedScrapeResultSeeds == 0) {
/*  786 */           if (this.lastModifiedScrapeResultPeers >= minPeersToBoostNoSeeds)
/*  787 */             newSR += SPRATIO_BASE_LIMIT;
/*      */         } else {
/*  789 */           float x = this.lastModifiedScrapeResultSeeds / this.lastModifiedScrapeResultPeers;
/*  790 */           newSR = (int)(newSR + SPRATIO_BASE_LIMIT / ((x + 1.0F) * (x + 1.0F)));
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*  795 */     else if (this.rules.bDebugLog) {
/*  796 */       this.sExplainSR += "  Can't calculate SR, no scrape results\n";
/*      */     }
/*      */     
/*  799 */     if (this.staleCDOffset > 0L)
/*      */     {
/*  801 */       if (newSR > this.staleCDOffset) {
/*  802 */         newSR = (int)(newSR - this.staleCDOffset);
/*  803 */         this.sExplainSR = (this.sExplainSR + "  subtracted " + this.staleCDOffset + " due to non-activeness\n");
/*      */       } else {
/*  805 */         this.staleCDOffset = 0L;
/*      */       }
/*      */     }
/*      */     
/*  809 */     if (newSR < 0) {
/*  810 */       newSR = 1;
/*      */     }
/*  812 */     return newSR;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isFirstPriority()
/*      */   {
/*  819 */     boolean bFP = pisFirstPriority();
/*      */     
/*  821 */     if (this.bIsFirstPriority != bFP) {
/*  822 */       this.bIsFirstPriority = bFP;
/*  823 */       this.rules.requestProcessCycle(null);
/*  824 */       if (this.rules.bDebugLog) {
/*  825 */         this.rules.log.log(this.dl.getTorrent(), 1, "somethingChanged: FP changed");
/*      */       }
/*      */     }
/*  828 */     return this.bIsFirstPriority;
/*      */   }
/*      */   
/*      */   private boolean pisFirstPriority() {
/*  832 */     if (this.rules.bDebugLog) {
/*  833 */       this.sExplainFP = ("FP if " + (iFirstPriorityType == 0 ? "all" : "any") + " criteria match:\n");
/*      */     }
/*      */     
/*      */ 
/*  837 */     if (!this.dl.isPersistent()) {
/*  838 */       if (this.rules.bDebugLog)
/*  839 */         this.sExplainFP += "Not FP: Download not persistent\n";
/*  840 */       return false;
/*      */     }
/*      */     
/*  843 */     int state = this.dl.getState();
/*  844 */     if ((state == 8) || (state == 7)) {
/*  845 */       if (this.rules.bDebugLog)
/*  846 */         this.sExplainFP += "Not FP: Download is ERROR or STOPPED\n";
/*  847 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  851 */     if (!this.dl.isComplete()) {
/*  852 */       if (this.rules.bDebugLog)
/*  853 */         this.sExplainFP += "Not FP: Download not complete\n";
/*  854 */       return false;
/*      */     }
/*      */     
/*  857 */     List listeners = this.rules.getFPListeners();
/*  858 */     StringBuffer fp_listener_debug = null;
/*  859 */     Iterator iter; if (!listeners.isEmpty())
/*      */     {
/*  861 */       if (this.rules.bDebugLog)
/*  862 */         fp_listener_debug = new StringBuffer();
/*  863 */       for (iter = listeners.iterator(); iter.hasNext();)
/*      */       {
/*  865 */         StartStopRulesFPListener l = (StartStopRulesFPListener)iter.next();
/*  866 */         boolean result = l.isFirstPriority(this.dl, this.lastModifiedScrapeResultSeeds, this.lastModifiedScrapeResultPeers, fp_listener_debug);
/*  867 */         if ((fp_listener_debug != null) && (fp_listener_debug.length() > 0))
/*      */         {
/*  869 */           char last_ch = fp_listener_debug.charAt(fp_listener_debug.length() - 1);
/*  870 */           if (last_ch != '\n')
/*  871 */             fp_listener_debug.append('\n');
/*  872 */           this.sExplainFP += fp_listener_debug;
/*  873 */           fp_listener_debug.setLength(0);
/*      */         }
/*  875 */         if (result)
/*      */         {
/*  877 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  884 */     if ((this.lastModifiedScrapeResultPeers > 0) && (this.lastModifiedScrapeResultSeeds > 0) && (this.lastModifiedScrapeResultSeeds / this.lastModifiedScrapeResultPeers >= iFirstPriorityIgnoreSPRatio) && (iFirstPriorityIgnoreSPRatio != 0))
/*      */     {
/*      */ 
/*  887 */       if (this.rules.bDebugLog)
/*  888 */         this.sExplainFP = (this.sExplainFP + "Not FP: S:P >= " + iFirstPriorityIgnoreSPRatio + ":1\n");
/*  889 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  893 */     if ((this.lastModifiedScrapeResultPeers == 0) && (this.lastScrapeResultOk) && (bFirstPriorityIgnore0Peer)) {
/*  894 */       if (this.rules.bDebugLog)
/*  895 */         this.sExplainFP += "Not FP: 0 peers\n";
/*  896 */       return false;
/*      */     }
/*      */     
/*  899 */     if (iFirstPriorityIgnoreIdleHours > 0) {
/*  900 */       long lastUploadSecs = this.dl.getStats().getSecondsSinceLastUpload();
/*  901 */       if (lastUploadSecs < 0L) {
/*  902 */         lastUploadSecs = this.dl.getStats().getSecondsOnlySeeding();
/*      */       }
/*  904 */       if (lastUploadSecs > 3600L * iFirstPriorityIgnoreIdleHours) {
/*  905 */         if (this.rules.bDebugLog) {
/*  906 */           this.sExplainFP = (this.sExplainFP + "Not FP: " + lastUploadSecs + "s > " + iFirstPriorityIgnoreIdleHours + "h of no upload\n");
/*      */         }
/*  908 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  912 */     int shareRatio = this.dl.getStats().getShareRatio();
/*      */     
/*  914 */     int activeMinSR = this.dlSpecificMinShareRatio;
/*  915 */     if (activeMinSR <= 0) {
/*  916 */       activeMinSR = minQueueingShareRatio;
/*      */     }
/*  918 */     boolean bLastMatched = (shareRatio != -1) && (shareRatio < activeMinSR);
/*      */     
/*      */ 
/*  921 */     if (this.rules.bDebugLog) {
/*  922 */       this.sExplainFP = (this.sExplainFP + "  shareRatio(" + shareRatio + ") < " + activeMinSR + "=" + bLastMatched + "\n");
/*      */     }
/*      */     
/*  925 */     if ((!bLastMatched) && (iFirstPriorityType == 0)) {
/*  926 */       if (this.rules.bDebugLog)
/*  927 */         this.sExplainFP += "..Not FP.  Exit Early\n";
/*  928 */       return false;
/*      */     }
/*  930 */     if ((bLastMatched) && (iFirstPriorityType == 1)) {
/*  931 */       if (this.rules.bDebugLog)
/*  932 */         this.sExplainFP += "..Is FP.  Exit Early\n";
/*  933 */       return true;
/*      */     }
/*      */     
/*  936 */     bLastMatched = iFirstPrioritySeedingMinutes == 0;
/*  937 */     if (!bLastMatched) {
/*  938 */       long timeSeeding = this.dl.getStats().getSecondsOnlySeeding();
/*  939 */       if (timeSeeding >= 0L) {
/*  940 */         bLastMatched = timeSeeding < iFirstPrioritySeedingMinutes * 60;
/*  941 */         if (this.rules.bDebugLog) {
/*  942 */           this.sExplainFP = (this.sExplainFP + "  SeedingTime(" + timeSeeding + ") < " + iFirstPrioritySeedingMinutes * 60 + "=" + bLastMatched + "\n");
/*      */         }
/*  944 */         if ((!bLastMatched) && (iFirstPriorityType == 0)) {
/*  945 */           if (this.rules.bDebugLog)
/*  946 */             this.sExplainFP += "..Not FP.  Exit Early\n";
/*  947 */           return false;
/*      */         }
/*  949 */         if ((bLastMatched) && (iFirstPriorityType == 1)) {
/*  950 */           if (this.rules.bDebugLog)
/*  951 */             this.sExplainFP += "..Is FP.  Exit Early\n";
/*  952 */           return true;
/*      */         }
/*      */       }
/*  955 */     } else if (this.rules.bDebugLog) {
/*  956 */       this.sExplainFP += "  Skipping Seeding Time check (user disabled)\n";
/*      */     }
/*      */     
/*  959 */     bLastMatched = iFirstPriorityActiveMinutes == 0;
/*  960 */     if (!bLastMatched) {
/*  961 */       long timeActive = this.dl.getStats().getSecondsDownloading() + this.dl.getStats().getSecondsOnlySeeding();
/*      */       
/*  963 */       if (timeActive >= 0L) {
/*  964 */         bLastMatched = timeActive < iFirstPriorityActiveMinutes * 60;
/*  965 */         if (this.rules.bDebugLog) {
/*  966 */           this.sExplainFP = (this.sExplainFP + "  ActiveTime(" + timeActive + ") < " + iFirstPriorityActiveMinutes * 60 + "=" + bLastMatched + "\n");
/*      */         }
/*  968 */         if ((!bLastMatched) && (iFirstPriorityType == 0)) {
/*  969 */           if (this.rules.bDebugLog)
/*  970 */             this.sExplainFP += "..Not FP.  Exit Early\n";
/*  971 */           return false;
/*      */         }
/*  973 */         if ((bLastMatched) && (iFirstPriorityType == 1)) {
/*  974 */           if (this.rules.bDebugLog)
/*  975 */             this.sExplainFP += "..Is FP.  Exit Early\n";
/*  976 */           return true;
/*      */         }
/*      */       }
/*  979 */     } else if (this.rules.bDebugLog) {
/*  980 */       this.sExplainFP += "  Skipping DL Time check (user disabled)\n";
/*      */     }
/*      */     
/*  983 */     if (iFirstPriorityType == 0) {
/*  984 */       if (this.rules.bDebugLog)
/*  985 */         this.sExplainFP += "..Is FP\n";
/*  986 */       return true;
/*      */     }
/*      */     
/*  989 */     if (this.rules.bDebugLog)
/*  990 */       this.sExplainFP += "..Not FP\n";
/*  991 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getCachedIsFP()
/*      */   {
/*  999 */     return this.bIsFirstPriority;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/* 1005 */   private int dlr_test_average_bytes_per_sec = -1;
/*      */   
/*      */ 
/*      */   public void setDLRInactive()
/*      */   {
/* 1010 */     this.dlr_test_active = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDLRActive(long time)
/*      */   {
/* 1017 */     if (this.rules.bDebugLog) {
/* 1018 */       this.rules.log.log(this.dl.getTorrent(), 1, "download speed test starts");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1023 */     this.dlr_test_active = true;
/*      */     
/* 1025 */     this.dlr_test_start_time = time;
/*      */     
/* 1027 */     this.dl.moveTo(1);
/*      */     
/* 1029 */     this.dlr_test_bytes_start = this.dl.getStats().getDownloaded(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDLRComplete(long time)
/*      */   {
/* 1036 */     long dlr_test_bytes_end = this.dl.getStats().getDownloaded(true);
/*      */     
/* 1038 */     long elapsed = time - this.dlr_test_start_time;
/*      */     
/* 1040 */     if (elapsed >= 1000L)
/*      */     {
/* 1042 */       this.dlr_test_average_bytes_per_sec = ((int)((dlr_test_bytes_end - this.dlr_test_bytes_start) * 1000L / elapsed));
/*      */       
/* 1044 */       if (this.rules.bDebugLog) {
/* 1045 */         this.rules.log.log(this.dl.getTorrent(), 1, "download speed test ends - average=" + this.dlr_test_average_bytes_per_sec);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1051 */     this.dlr_test_active = false;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getDLRLastTestTime()
/*      */   {
/* 1057 */     return this.dlr_test_start_time;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDLRLastTestSpeed()
/*      */   {
/* 1063 */     return this.dlr_test_average_bytes_per_sec;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDLRTrace()
/*      */   {
/* 1069 */     if (this.dlr_test_active)
/*      */     {
/* 1071 */       return "test in progress";
/*      */     }
/* 1073 */     if (this.dlr_test_start_time > 0L)
/*      */     {
/* 1075 */       if (this.dlr_test_average_bytes_per_sec >= 0)
/*      */       {
/* 1077 */         return "tested; " + TimeFormatter.format((SystemTime.getMonotonousTime() - this.dlr_test_start_time) / 1000L) + " ago; " + "rate=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(this.dlr_test_average_bytes_per_sec);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1084 */       return "tested; " + TimeFormatter.format((SystemTime.getMonotonousTime() - this.dlr_test_start_time) / 1000L) + " ago; " + "test did not complete";
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1090 */     return "";
/*      */   }
/*      */   
/*      */   public String toString()
/*      */   {
/* 1095 */     return String.valueOf(this.dl.getSeedingRank());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean changeChecker()
/*      */   {
/* 1104 */     if (getActivelySeeding()) {
/* 1105 */       int shareRatio = this.dl.getStats().getShareRatio();
/* 1106 */       int numSeeds = this.rules.calcSeedsNoUs(this.dl, this.dl.getAggregatedScrapeResult());
/*      */       
/* 1108 */       int activeMaxSR = this.dlSpecificMaxShareRatio;
/* 1109 */       if (activeMaxSR <= 0) {
/* 1110 */         activeMaxSR = iIgnoreShareRatio;
/*      */       }
/* 1112 */       if ((activeMaxSR != 0) && (shareRatio >= activeMaxSR) && ((numSeeds >= iIgnoreShareRatio_SeedStart) || (!this.lastScrapeResultOk)) && (shareRatio != -1))
/*      */       {
/*      */ 
/* 1115 */         if (this.rules.bDebugLog) {
/* 1116 */           this.rules.log.log(this.dl.getTorrent(), 1, "somethingChanged: shareRatio changeChecker");
/*      */         }
/*      */         
/* 1119 */         return true;
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
/* 1130 */     if (this.dl.getState() == 3) {
/* 1131 */       if (this.rules.bDebugLog) {
/* 1132 */         this.rules.log.log(this.dl.getTorrent(), 1, "somethingChanged: Download is ready");
/*      */       }
/* 1134 */       return true;
/*      */     }
/*      */     
/* 1137 */     if (this.staleCDSince > 0L) {
/* 1138 */       long now = SystemTime.getCurrentTime();
/* 1139 */       if (now - this.lastStaleCDRefresh > 60000L) {
/* 1140 */         this.staleCDOffset += (now - this.lastStaleCDRefresh) / 60000L;
/* 1141 */         this.lastStaleCDRefresh = now;
/* 1142 */         if (this.rules.bDebugLog) {
/* 1143 */           this.rules.log.log(this.dl.getTorrent(), 1, "somethingChanged: staleCD changeChecker");
/*      */         }
/*      */         
/* 1146 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 1150 */     return false;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/DefaultRankCalculator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */