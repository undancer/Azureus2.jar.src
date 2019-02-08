/*      */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingMapper;
/*      */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProviderAdapter;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.util.RealTimeInfo;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SpeedLimitMonitor
/*      */   implements PSMonitorListener
/*      */ {
/*   61 */   private int uploadLimitMax = 30720;
/*   62 */   private int uploadLimitMin = SMConst.calculateMinUpload(this.uploadLimitMax);
/*   63 */   private int downloadLimitMax = 61440;
/*   64 */   private int downloadLimitMin = SMConst.calculateMinDownload(this.downloadLimitMax);
/*      */   
/*   66 */   private final TransferMode transferMode = new TransferMode();
/*      */   
/*      */ 
/*   69 */   private SaturatedMode uploadBandwidthStatus = SaturatedMode.NONE;
/*   70 */   private SaturatedMode downloadBandwidthStatus = SaturatedMode.NONE;
/*      */   
/*      */ 
/*   73 */   private SaturatedMode uploadLimitSettingStatus = SaturatedMode.AT_LIMIT;
/*   74 */   private SaturatedMode downloadLimitSettingStatus = SaturatedMode.AT_LIMIT;
/*      */   
/*      */ 
/*   77 */   private SpeedLimitConfidence uploadLimitConf = SpeedLimitConfidence.NONE;
/*   78 */   private SpeedLimitConfidence downloadLimitConf = SpeedLimitConfidence.NONE;
/*      */   
/*   80 */   private long clLastIncreaseTime = -1L;
/*   81 */   private long clFirstBadPingTime = -1L;
/*      */   
/*      */   private boolean currTestDone;
/*      */   private boolean beginLimitTest;
/*   85 */   private int highestUploadRate = 0;
/*   86 */   private int highestDownloadRate = 0;
/*   87 */   private int preTestUploadCapacity = 5042;
/*   88 */   private int preTestUploadLimit = 5142;
/*   89 */   private int preTestDownloadCapacity = 5042;
/*   90 */   private int preTestDownloadLimit = 5142;
/*      */   
/*      */   public static final String UPLOAD_CONF_LIMIT_SETTING = "SpeedLimitMonitor.setting.upload.limit.conf";
/*      */   
/*      */   public static final String DOWNLOAD_CONF_LIMIT_SETTING = "SpeedLimitMonitor.setting.download.limit.conf";
/*      */   
/*      */   public static final String UPLOAD_CHOKE_PING_COUNT = "SpeedLimitMonitor.setting.choke.ping.count";
/*      */   private static final long CONF_LIMIT_TEST_LENGTH = 30000L;
/*   98 */   private boolean isUploadMaxPinned = true;
/*   99 */   private boolean isDownloadMaxPinned = true;
/*  100 */   private long uploadAtLimitStartTime = SystemTime.getCurrentTime();
/*  101 */   private long downloadAtLimitStartTime = SystemTime.getCurrentTime();
/*  102 */   private int uploadChokePingCount = 1;
/*  103 */   private int uploadPinCounter = 0;
/*      */   
/*      */   private static final long TIME_AT_LIMIT_BEFORE_UNPINNING = 30000L;
/*      */   
/*      */   public static final String USED_UPLOAD_CAPACITY_DOWNLOAD_MODE = "SpeedLimitMonitor.setting.upload.used.download.mode";
/*      */   
/*      */   public static final String USED_UPLOAD_CAPACITY_SEEDING_MODE = "SpeedLimitMonitor.setting.upload.used.seeding.mode";
/*  110 */   private float percentUploadCapacityDownloadMode = 0.6F;
/*      */   
/*      */ 
/*      */   PingSpaceMapper pingMapOfDownloadMode;
/*      */   
/*      */   PingSpaceMapper pingMapOfSeedingMode;
/*      */   
/*  117 */   boolean useVariancePingMap = false;
/*      */   
/*      */   SpeedManagerPingMapper transientPingMap;
/*  120 */   final PingSpaceMon longTermMonitor = new PingSpaceMon();
/*      */   
/*  122 */   final LimitControl slider = new LimitControlDropUploadFirst();
/*      */   
/*      */   final SpeedLimitListener persistentMapListener;
/*      */   
/*      */ 
/*      */   public SpeedLimitMonitor(SpeedManager sm)
/*      */   {
/*  129 */     this.longTermMonitor.addListener(this);
/*      */     
/*  131 */     this.persistentMapListener = new SpeedLimitListener(this);
/*      */     
/*  133 */     sm.addListener(this.persistentMapListener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updateSettingsFromCOConfigManager()
/*      */   {
/*  141 */     this.percentUploadCapacityDownloadMode = (COConfigurationManager.getIntParameter("SpeedLimitMonitor.setting.upload.used.download.mode", 60) / 100.0F);
/*      */     
/*      */ 
/*  144 */     this.slider.updateSeedSettings(this.percentUploadCapacityDownloadMode);
/*      */   }
/*      */   
/*      */   public void updateFromCOConfigManager()
/*      */   {
/*  149 */     this.uploadLimitMax = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit");
/*  150 */     this.uploadLimitMin = SMConst.calculateMinUpload(this.uploadLimitMax);
/*      */     
/*  152 */     this.downloadLimitMax = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit");
/*  153 */     this.downloadLimitMin = SMConst.calculateMinDownload(this.downloadLimitMax);
/*      */     
/*  155 */     this.uploadLimitConf = SpeedLimitConfidence.parseString(COConfigurationManager.getStringParameter("SpeedLimitMonitor.setting.upload.limit.conf"));
/*      */     
/*  157 */     this.downloadLimitConf = SpeedLimitConfidence.parseString(COConfigurationManager.getStringParameter("SpeedLimitMonitor.setting.download.limit.conf"));
/*      */     
/*      */ 
/*  160 */     this.percentUploadCapacityDownloadMode = (COConfigurationManager.getIntParameter("SpeedLimitMonitor.setting.upload.used.download.mode", 60) / 100.0F);
/*      */     
/*      */ 
/*  163 */     this.uploadChokePingCount = Math.min(COConfigurationManager.getIntParameter("SpeedLimitMonitor.setting.choke.ping.count"), 30);
/*      */     
/*      */ 
/*      */ 
/*  167 */     this.slider.updateLimits(this.uploadLimitMax, this.uploadLimitMin, this.downloadLimitMax, this.downloadLimitMin);
/*  168 */     this.slider.updateSeedSettings(this.percentUploadCapacityDownloadMode);
/*      */     
/*      */ 
/*  171 */     if (isSettingDownloadUnlimited()) {
/*  172 */       this.slider.setDownloadUnlimitedMode(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void readFromPersistentMap()
/*      */   {
/*  182 */     SpeedManager sm = AzureusCoreFactory.getSingleton().getSpeedManager();
/*      */     
/*      */ 
/*  185 */     SpeedManagerLimitEstimate uEst = SMConst.filterEstimate(sm.getEstimatedUploadCapacityBytesPerSec(), 30720);
/*      */     
/*      */ 
/*      */ 
/*  189 */     int upPingMapLimit = uEst.getBytesPerSec();
/*  190 */     if (upPingMapLimit < 30720)
/*      */     {
/*  192 */       this.uploadLimitMax = 30720;
/*      */     } else {
/*  194 */       this.uploadLimitMax = upPingMapLimit;
/*      */     }
/*  196 */     this.uploadLimitMin = SMConst.calculateMinUpload(this.uploadLimitMax);
/*      */     
/*      */ 
/*      */ 
/*  200 */     SpeedManagerLimitEstimate dEst = SMConst.filterEstimate(sm.getEstimatedDownloadCapacityBytesPerSec(), 61440);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  205 */     int downPingMapLimit = dEst.getBytesPerSec();
/*  206 */     if (isSettingDownloadUnlimited()) {
/*  207 */       this.slider.setDownloadUnlimitedMode(true);
/*      */     } else {
/*  209 */       this.slider.setDownloadUnlimitedMode(false);
/*      */     }
/*      */     
/*  212 */     if (downPingMapLimit < 61440) {
/*  213 */       this.downloadLimitMax = 61440;
/*      */     } else {
/*  215 */       this.downloadLimitMax = downPingMapLimit;
/*      */     }
/*  217 */     this.downloadLimitMin = SMConst.calculateMinDownload(this.downloadLimitMax);
/*      */     
/*  219 */     this.uploadLimitConf = SpeedLimitConfidence.convertType(uEst.getEstimateType());
/*  220 */     this.downloadLimitConf = SpeedLimitConfidence.convertType(dEst.getEstimateType());
/*      */     
/*  222 */     this.percentUploadCapacityDownloadMode = (COConfigurationManager.getIntParameter("SpeedLimitMonitor.setting.upload.used.download.mode", 60) / 100.0F);
/*      */     
/*      */ 
/*  225 */     saveToCOConfiguration();
/*      */   }
/*      */   
/*      */   public void saveToCOConfiguration()
/*      */   {
/*  230 */     COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit", this.uploadLimitMax);
/*  231 */     COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit", this.downloadLimitMax);
/*  232 */     COConfigurationManager.setParameter("SpeedLimitMonitor.setting.upload.limit.conf", this.uploadLimitConf.getString());
/*  233 */     COConfigurationManager.setParameter("SpeedLimitMonitor.setting.download.limit.conf", this.downloadLimitConf.getString());
/*  234 */     COConfigurationManager.setParameter("SpeedLimitMonitor.setting.choke.ping.count", this.uploadChokePingCount);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void logPMData(int oRate, SpeedLimitConfidence oConf, int nRate, float nConf, String type) {}
/*      */   
/*      */ 
/*      */ 
/*      */   public void logPMDataEx()
/*      */   {
/*  246 */     int tuploadLimitMax = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit");
/*  247 */     int tdownloadLimitMax = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit");
/*      */     
/*      */ 
/*  250 */     SpeedManager sm = AzureusCoreFactory.getSingleton().getSpeedManager();
/*  251 */     SpeedManagerLimitEstimate dEst = sm.getEstimatedDownloadCapacityBytesPerSec();
/*      */     
/*  253 */     int tmpDMax = dEst.getBytesPerSec();
/*  254 */     float tmpDMaxConf = dEst.getEstimateType();
/*      */     
/*      */ 
/*      */ 
/*  258 */     SpeedManagerLimitEstimate uEst = sm.getEstimatedUploadCapacityBytesPerSec();
/*  259 */     int tmpUMax = uEst.getBytesPerSec();
/*  260 */     float tmpUMaxConf = uEst.getEstimateType();
/*      */     
/*  262 */     SpeedLimitConfidence tuploadLimitConf = SpeedLimitConfidence.parseString(COConfigurationManager.getStringParameter("SpeedLimitMonitor.setting.upload.limit.conf"));
/*      */     
/*  264 */     SpeedLimitConfidence tdownloadLimitConf = SpeedLimitConfidence.parseString(COConfigurationManager.getStringParameter("SpeedLimitMonitor.setting.download.limit.conf"));
/*      */     
/*      */ 
/*      */ 
/*  268 */     logPMData(tuploadLimitMax, tuploadLimitConf, tmpUMax, tmpUMaxConf, "check-upload");
/*  269 */     logPMData(tdownloadLimitMax, tdownloadLimitConf, tmpDMax, tmpDMaxConf, "check-download");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean isSettingDownloadUnlimited()
/*      */   {
/*  281 */     SpeedManagerAlgorithmProviderAdapter adpter = SMInstance.getInstance().getAdapter();
/*      */     
/*  283 */     SpeedManager sm = adpter.getSpeedManager();
/*  284 */     SpeedManagerLimitEstimate dEst = sm.getEstimatedDownloadCapacityBytesPerSec();
/*      */     
/*  286 */     int rate = dEst.getBytesPerSec();
/*  287 */     float type = dEst.getEstimateType();
/*      */     
/*      */ 
/*  290 */     if ((rate == 0) && (type == 1.0F)) {
/*  291 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  295 */     if ((rate == 0) && (type == -0.1F)) {
/*  296 */       return true;
/*      */     }
/*      */     
/*  299 */     return false;
/*      */   }
/*      */   
/*      */   public void setDownloadBandwidthMode(int rate, int limit)
/*      */   {
/*  304 */     this.downloadBandwidthStatus = SaturatedMode.getSaturatedMode(rate, limit);
/*      */   }
/*      */   
/*      */   public void setUploadBandwidthMode(int rate, int limit) {
/*  308 */     this.uploadBandwidthStatus = SaturatedMode.getSaturatedMode(rate, limit);
/*      */   }
/*      */   
/*      */   public void setDownloadLimitSettingMode(int currLimit) {
/*  312 */     this.downloadLimitSettingStatus = SaturatedMode.getSaturatedMode(currLimit, this.downloadLimitMax);
/*      */   }
/*      */   
/*      */   public void setUploadLimitSettingMode(int currLimit) {
/*  316 */     if (!this.transferMode.isDownloadMode()) {
/*  317 */       this.uploadLimitSettingStatus = SaturatedMode.getSaturatedMode(currLimit, this.uploadLimitMax);
/*      */     } else {
/*  319 */       this.uploadLimitSettingStatus = SaturatedMode.getSaturatedMode(currLimit, this.uploadLimitMax);
/*      */     }
/*      */   }
/*      */   
/*      */   public int getUploadMaxLimit() {
/*  324 */     return this.uploadLimitMax;
/*      */   }
/*      */   
/*      */   public int getDownloadMaxLimit() {
/*  328 */     return this.downloadLimitMax;
/*      */   }
/*      */   
/*      */   public int getUploadMinLimit() {
/*  332 */     return this.uploadLimitMin;
/*      */   }
/*      */   
/*      */   public int getDownloadMinLimit() {
/*  336 */     return this.downloadLimitMin;
/*      */   }
/*      */   
/*      */   public String getUploadConfidence() {
/*  340 */     return this.uploadLimitConf.getString();
/*      */   }
/*      */   
/*      */   public String getDownloadConfidence() {
/*  344 */     return this.downloadLimitConf.getString();
/*      */   }
/*      */   
/*      */   public SaturatedMode getDownloadBandwidthMode() {
/*  348 */     return this.downloadBandwidthStatus;
/*      */   }
/*      */   
/*      */   public SaturatedMode getUploadBandwidthMode() {
/*  352 */     return this.uploadBandwidthStatus;
/*      */   }
/*      */   
/*      */   public SaturatedMode getDownloadLimitSettingMode() {
/*  356 */     return this.downloadLimitSettingStatus;
/*      */   }
/*      */   
/*      */   public SaturatedMode getUploadLimitSettingMode() {
/*  360 */     return this.uploadLimitSettingStatus;
/*      */   }
/*      */   
/*      */   public void updateTransferMode()
/*      */   {
/*  365 */     this.transferMode.updateStatus(this.downloadBandwidthStatus);
/*      */   }
/*      */   
/*      */   public String getTransferModeAsString() {
/*  369 */     return this.transferMode.getString();
/*      */   }
/*      */   
/*      */   public TransferMode getTransferMode() {
/*  373 */     return this.transferMode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean bandwidthUsageLow()
/*      */   {
/*  384 */     if ((this.uploadBandwidthStatus.compareTo(SaturatedMode.LOW) <= 0) && (this.downloadBandwidthStatus.compareTo(SaturatedMode.LOW) <= 0))
/*      */     {
/*      */ 
/*  387 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  392 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean bandwidthUsageMedium()
/*      */   {
/*  400 */     if ((this.uploadBandwidthStatus.compareTo(SaturatedMode.MED) <= 0) && (this.downloadBandwidthStatus.compareTo(SaturatedMode.MED) <= 0))
/*      */     {
/*  402 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  406 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean bandwidthUsageAtLimit()
/*      */   {
/*  414 */     if ((this.uploadBandwidthStatus.compareTo(SaturatedMode.AT_LIMIT) == 0) && (this.downloadBandwidthStatus.compareTo(SaturatedMode.AT_LIMIT) == 0))
/*      */     {
/*  416 */       return true;
/*      */     }
/*  418 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isUploadBandwidthUsageHigh()
/*      */   {
/*  426 */     if ((this.uploadBandwidthStatus.compareTo(SaturatedMode.AT_LIMIT) == 0) || (this.uploadBandwidthStatus.compareTo(SaturatedMode.HIGH) == 0))
/*      */     {
/*  428 */       return true;
/*      */     }
/*  430 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isEitherLimitUnpinned() {
/*  434 */     return (!this.isUploadMaxPinned) || (!this.isDownloadMaxPinned);
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
/*      */   public SMUpdate modifyLimits(float signalStrength, float multiple, int currUpLimit, int currDownLimit)
/*      */   {
/*  448 */     if (isStartLimitTestFlagSet()) {
/*  449 */       SpeedManagerLogger.trace("modifyLimits - startLimitTesting.");
/*  450 */       SMUpdate update = startLimitTesting(currUpLimit, currDownLimit);
/*  451 */       return checkActiveProgressiveDownloadLimit(update);
/*      */     }
/*      */     
/*      */ 
/*  455 */     if (isEitherLimitUnpinned()) {
/*  456 */       SpeedManagerLogger.trace("modifyLimits - calculateNewUnpinnedLimits");
/*  457 */       SMUpdate update = calculateNewUnpinnedLimits(signalStrength);
/*  458 */       return checkActiveProgressiveDownloadLimit(update);
/*      */     }
/*      */     
/*  461 */     this.slider.updateLimits(this.uploadLimitMax, this.uploadLimitMin, this.downloadLimitMax, this.downloadLimitMin);
/*      */     
/*      */ 
/*  464 */     this.slider.updateStatus(currUpLimit, this.uploadBandwidthStatus, currDownLimit, this.downloadBandwidthStatus, this.transferMode);
/*      */     
/*      */ 
/*  467 */     SMUpdate update = this.slider.adjust(signalStrength * multiple);
/*  468 */     return checkActiveProgressiveDownloadLimit(update);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private SMUpdate checkActiveProgressiveDownloadLimit(SMUpdate update)
/*      */   {
/*  480 */     long prgDownLimit = RealTimeInfo.getProgressiveActiveBytesPerSec();
/*      */     
/*      */ 
/*  483 */     if (prgDownLimit == 0L) {
/*  484 */       return update;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  489 */     int MULTIPLE = 2;
/*  490 */     if ((prgDownLimit * 2L > update.newDownloadLimit) && (update.newDownloadLimit != 0))
/*      */     {
/*  492 */       log("Active Progressive download in progress. Overriding limit. curr=" + update.newDownloadLimit + " progDownloadLimit=" + prgDownLimit * 2L);
/*      */       
/*      */ 
/*  495 */       update.newDownloadLimit = ((int)prgDownLimit * 2);
/*      */     }
/*      */     
/*  498 */     return update;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void logPinningInfo()
/*      */   {
/*  505 */     StringBuilder sb = new StringBuilder("pin: ");
/*  506 */     if (this.isUploadMaxPinned) {
/*  507 */       sb.append("ul-pinned:");
/*      */     } else {
/*  509 */       sb.append("ul-unpinned:");
/*      */     }
/*  511 */     if (this.isDownloadMaxPinned) {
/*  512 */       sb.append("dl-pinned:");
/*      */     } else {
/*  514 */       sb.append("dl-unpinned:");
/*      */     }
/*  516 */     long currTime = SystemTime.getCurrentTime();
/*  517 */     long upWait = currTime - this.uploadAtLimitStartTime;
/*  518 */     long downWait = currTime - this.downloadAtLimitStartTime;
/*  519 */     sb.append(upWait).append(":").append(downWait);
/*  520 */     log(sb.toString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SMUpdate calculateNewUnpinnedLimits(float signalStrength)
/*      */   {
/*  531 */     if (signalStrength < 0.0F)
/*      */     {
/*  533 */       this.isUploadMaxPinned = true;
/*  534 */       this.isDownloadMaxPinned = true;
/*      */     }
/*      */     
/*      */ 
/*  538 */     boolean updateUpload = false;
/*  539 */     boolean updateDownload = false;
/*      */     
/*  541 */     if ((this.uploadBandwidthStatus.compareTo(SaturatedMode.AT_LIMIT) == 0) && (this.uploadLimitSettingStatus.compareTo(SaturatedMode.AT_LIMIT) == 0))
/*      */     {
/*  543 */       updateUpload = true;
/*      */     }
/*      */     
/*  546 */     if ((this.downloadBandwidthStatus.compareTo(SaturatedMode.AT_LIMIT) == 0) && (this.downloadLimitSettingStatus.compareTo(SaturatedMode.AT_LIMIT) == 0))
/*      */     {
/*  548 */       updateDownload = true;
/*      */     }
/*      */     
/*  551 */     boolean uploadChanged = false;
/*  552 */     boolean downloadChanged = false;
/*      */     
/*      */ 
/*  555 */     if ((updateUpload) && (!this.transferMode.isDownloadMode()))
/*      */     {
/*  557 */       this.uploadPinCounter += 1;
/*  558 */       if (this.uploadPinCounter % Math.ceil(Math.sqrt(this.uploadChokePingCount)) == 0.0D)
/*      */       {
/*  560 */         this.uploadLimitMax += calculateUnpinnedStepSize(this.uploadLimitMax);
/*  561 */         uploadChanged = true;
/*  562 */         COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit", this.uploadLimitMax);
/*      */         
/*  564 */         COConfigurationManager.setParameter("SpeedLimitMonitor.setting.choke.ping.count", this.uploadChokePingCount);
/*      */       }
/*      */     }
/*      */     
/*  568 */     if ((updateDownload) && (!this.slider.isDownloadUnlimitedMode()))
/*      */     {
/*  570 */       this.downloadLimitMax += calculateUnpinnedStepSize(this.downloadLimitMax);
/*  571 */       downloadChanged = true;
/*  572 */       COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit", this.downloadLimitMax);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  578 */     if (this.uploadLimitMax > this.downloadLimitMax) {
/*  579 */       this.downloadLimitMax = this.uploadLimitMax;
/*  580 */       downloadChanged = true;
/*  581 */       COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit", this.downloadLimitMax);
/*      */     }
/*      */     
/*      */ 
/*  585 */     this.uploadLimitMin = SMConst.calculateMinUpload(this.uploadLimitMax);
/*  586 */     this.downloadLimitMin = SMConst.calculateMinDownload(this.downloadLimitMax);
/*      */     
/*  588 */     if (this.slider.isDownloadUnlimitedMode()) {
/*  589 */       SpeedManagerLogger.trace("upload unpinned while download is unlimited.");
/*  590 */       return new SMUpdate(this.uploadLimitMax, uploadChanged, 0, false);
/*      */     }
/*      */     
/*  593 */     return new SMUpdate(this.uploadLimitMax, uploadChanged, this.downloadLimitMax, downloadChanged);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int calculateUnpinnedStepSize(int currLimitMax)
/*      */   {
/*  604 */     if (currLimitMax < 102400)
/*  605 */       return 1024;
/*  606 */     if (currLimitMax < 409600) {
/*  607 */       return 5120;
/*      */     }
/*  609 */     return 10240;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void checkForUnpinningCondition()
/*      */   {
/*  619 */     long currTime = SystemTime.getCurrentTime();
/*      */     
/*      */ 
/*  622 */     this.slider.setDownloadUnlimitedMode(isSettingDownloadUnlimited());
/*      */     
/*      */ 
/*  625 */     if ((!this.uploadBandwidthStatus.equals(SaturatedMode.AT_LIMIT)) || (!this.uploadLimitSettingStatus.equals(SaturatedMode.AT_LIMIT)))
/*      */     {
/*      */ 
/*      */ 
/*  629 */       this.uploadAtLimitStartTime = currTime;
/*      */ 
/*      */     }
/*  632 */     else if (this.uploadAtLimitStartTime + 30000L * this.uploadChokePingCount < currTime)
/*      */     {
/*  634 */       if (isUploadConfidenceLow()) {
/*  635 */         if (!this.transferMode.isDownloadMode())
/*      */         {
/*  637 */           this.isUploadMaxPinned = false;
/*      */         }
/*      */         
/*      */       }
/*  641 */       else if (!isUploadConfidenceAbsolute())
/*      */       {
/*  643 */         this.isUploadMaxPinned = false;
/*  644 */         SpeedManagerLogger.trace("unpinning the upload max limit!! #choke-pings=" + this.uploadChokePingCount + ", pin-counter=" + this.uploadPinCounter);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  652 */     if ((!this.downloadBandwidthStatus.equals(SaturatedMode.AT_LIMIT)) || (!this.downloadLimitSettingStatus.equals(SaturatedMode.AT_LIMIT)))
/*      */     {
/*      */ 
/*      */ 
/*  656 */       this.downloadAtLimitStartTime = currTime;
/*      */ 
/*      */     }
/*  659 */     else if (this.downloadAtLimitStartTime + 30000L < currTime)
/*      */     {
/*  661 */       if (isDownloadConfidenceLow()) {
/*  662 */         if (this.transferMode.isDownloadMode()) {
/*  663 */           triggerLimitTestingFlag();
/*      */         }
/*      */       }
/*  666 */       else if (!isDownloadConfidenceAbsolute())
/*      */       {
/*  668 */         this.isDownloadMaxPinned = false;
/*  669 */         SpeedManagerLogger.trace("unpinning the download max limit!!");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  675 */     logPinningInfo();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void notifyOfDownSignal()
/*      */   {
/*  683 */     if (!this.isUploadMaxPinned) {
/*  684 */       this.uploadChokePingCount += 1;
/*  685 */       String msg = "pinning the upload max limit, due to downtick signal. #downtick=" + this.uploadChokePingCount;
/*  686 */       SpeedManagerLogger.trace(msg);
/*  687 */       SMSearchLogger.log(msg);
/*      */     }
/*      */     
/*  690 */     if (!this.isDownloadMaxPinned) {
/*  691 */       String msg = "pinning the download max limit, due to downtick signal.";
/*  692 */       SpeedManagerLogger.trace(msg);
/*  693 */       SMSearchLogger.log(msg);
/*      */     }
/*      */     
/*  696 */     resetPinSearch();
/*      */   }
/*      */   
/*      */   void resetPinSearch() {
/*  700 */     long currTime = SystemTime.getCurrentTime();
/*      */     
/*  702 */     this.uploadAtLimitStartTime = currTime;
/*  703 */     this.downloadAtLimitStartTime = currTime;
/*  704 */     this.isUploadMaxPinned = true;
/*  705 */     this.isDownloadMaxPinned = true;
/*      */   }
/*      */   
/*      */   void resetPinSearch(SpeedManagerLimitEstimate estimate)
/*      */   {
/*  710 */     float type = estimate.getEstimateType();
/*  711 */     if (type >= 0.5F) {
/*  712 */       this.uploadChokePingCount += 1;
/*      */     }
/*  714 */     resetPinSearch();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isConfTestingLimits()
/*      */   {
/*  722 */     return this.transferMode.isConfTestingLimits();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isDownloadConfidenceLow()
/*      */   {
/*  730 */     return this.downloadLimitConf.compareTo(SpeedLimitConfidence.MED) < 0;
/*      */   }
/*      */   
/*      */   public boolean isUploadConfidenceLow() {
/*  734 */     return this.uploadLimitConf.compareTo(SpeedLimitConfidence.MED) < 0;
/*      */   }
/*      */   
/*      */   public boolean isDownloadConfidenceAbsolute() {
/*  738 */     return this.downloadLimitConf.compareTo(SpeedLimitConfidence.ABSOLUTE) == 0;
/*      */   }
/*      */   
/*      */   public boolean isUploadConfidenceAbsolute() {
/*  742 */     return this.uploadLimitConf.compareTo(SpeedLimitConfidence.ABSOLUTE) == 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void updateLimitTestingData(int downloadRate, int uploadRate)
/*      */   {
/*  752 */     if (downloadRate > this.highestDownloadRate) {
/*  753 */       this.highestDownloadRate = downloadRate;
/*      */     }
/*  755 */     if (uploadRate > this.highestUploadRate) {
/*  756 */       this.highestUploadRate = uploadRate;
/*      */     }
/*      */     
/*      */ 
/*  760 */     long currTime = SystemTime.getCurrentTime();
/*  761 */     if (currTime > this.clLastIncreaseTime + 30000L)
/*      */     {
/*  763 */       this.currTestDone = true;
/*      */     }
/*      */     
/*  766 */     if ((this.clFirstBadPingTime != -1L) && 
/*  767 */       (currTime > this.clFirstBadPingTime + 30000L))
/*      */     {
/*  769 */       this.currTestDone = true;
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
/*      */   public void updateLimitTestingPing(int lastMetric)
/*      */   {
/*  782 */     if (lastMetric > 500) {
/*  783 */       updateLimitTestingPing(-1.0F);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updateLimitTestingPing(float lastMetric)
/*      */   {
/*  792 */     if (lastMetric < -0.3F)
/*      */     {
/*  794 */       this.clFirstBadPingTime = SystemTime.getCurrentTime();
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
/*      */   public SMUpdate startLimitTesting(int currUploadLimit, int currDownloadLimit)
/*      */   {
/*  807 */     this.clLastIncreaseTime = SystemTime.getCurrentTime();
/*  808 */     this.clFirstBadPingTime = -1L;
/*      */     
/*  810 */     this.highestUploadRate = 0;
/*  811 */     this.highestDownloadRate = 0;
/*  812 */     this.currTestDone = false;
/*      */     
/*      */ 
/*  815 */     this.beginLimitTest = false;
/*      */     
/*      */ 
/*  818 */     this.preTestUploadLimit = currUploadLimit;
/*  819 */     this.preTestDownloadLimit = currDownloadLimit;
/*      */     
/*      */     SMUpdate retVal;
/*      */     
/*  823 */     if (this.transferMode.isDownloadMode())
/*      */     {
/*  825 */       SMUpdate retVal = new SMUpdate(this.uploadLimitMin, true, Math.round(this.downloadLimitMax * 1.2F), true);
/*      */       
/*  827 */       this.preTestDownloadCapacity = this.downloadLimitMax;
/*  828 */       this.transferMode.setMode(TransferMode.State.DOWNLOAD_LIMIT_SEARCH);
/*      */     }
/*      */     else {
/*  831 */       retVal = new SMUpdate(Math.round(this.uploadLimitMax * 1.2F), true, this.downloadLimitMin, true);
/*      */       
/*  833 */       this.preTestUploadCapacity = this.uploadLimitMax;
/*  834 */       this.transferMode.setMode(TransferMode.State.UPLOAD_LIMIT_SEARCH);
/*      */     }
/*      */     
/*  837 */     return retVal;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SMUpdate rampTestingLimit(int uploadLimit, int downloadLimit)
/*      */   {
/*      */     SMUpdate retVal;
/*      */     
/*      */     SMUpdate retVal;
/*      */     
/*  848 */     if ((this.transferMode.getMode() == TransferMode.State.DOWNLOAD_LIMIT_SEARCH) && (this.downloadBandwidthStatus.isGreater(SaturatedMode.MED)))
/*      */     {
/*      */ 
/*  851 */       downloadLimit = (int)(downloadLimit * 1.1F);
/*  852 */       this.clLastIncreaseTime = SystemTime.getCurrentTime();
/*  853 */       retVal = new SMUpdate(uploadLimit, false, downloadLimit, true);
/*      */     } else { SMUpdate retVal;
/*  855 */       if ((this.transferMode.getMode() == TransferMode.State.UPLOAD_LIMIT_SEARCH) && (this.uploadBandwidthStatus.isGreater(SaturatedMode.MED)))
/*      */       {
/*      */ 
/*  858 */         uploadLimit = (int)(uploadLimit * 1.1F);
/*  859 */         this.clLastIncreaseTime = SystemTime.getCurrentTime();
/*  860 */         retVal = new SMUpdate(uploadLimit, true, downloadLimit, false);
/*      */       }
/*      */       else {
/*  863 */         retVal = new SMUpdate(uploadLimit, false, downloadLimit, false);
/*  864 */         SpeedManagerLogger.trace("ERROR: rampTestLimit should only be called during limit testing. ");
/*      */       }
/*      */     }
/*  867 */     return retVal;
/*      */   }
/*      */   
/*      */   public void triggerLimitTestingFlag() {
/*  871 */     SpeedManagerLogger.trace("triggerd fast limit test.");
/*  872 */     this.beginLimitTest = true;
/*      */     
/*      */ 
/*  875 */     if (this.useVariancePingMap) {
/*  876 */       SMInstance pm = SMInstance.getInstance();
/*  877 */       SpeedManagerAlgorithmProviderAdapter adapter = pm.getAdapter();
/*      */       
/*      */ 
/*  880 */       if (this.transientPingMap != null) {
/*  881 */         this.transientPingMap.destroy();
/*      */       }
/*  883 */       this.transientPingMap = adapter.createTransientPingMapper();
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized boolean isStartLimitTestFlagSet()
/*      */   {
/*  889 */     return this.beginLimitTest;
/*      */   }
/*      */   
/*      */   public synchronized boolean isConfLimitTestFinished() {
/*  893 */     return this.currTestDone;
/*      */   }
/*      */   
/*      */   public synchronized SMUpdate endLimitTesting(int downloadCapacityGuess, int uploadCapacityGuess)
/*      */   {
/*  898 */     SpeedManagerLogger.trace(" repalce highestDownloadRate: " + this.highestDownloadRate + " with " + downloadCapacityGuess);
/*  899 */     SpeedManagerLogger.trace(" replace highestUploadRate: " + this.highestUploadRate + " with " + uploadCapacityGuess);
/*      */     
/*  901 */     this.highestDownloadRate = downloadCapacityGuess;
/*  902 */     this.highestUploadRate = uploadCapacityGuess;
/*      */     
/*  904 */     return endLimitTesting();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized SMUpdate endLimitTesting()
/*      */   {
/*      */     SMUpdate retVal;
/*      */     
/*      */ 
/*  915 */     if (this.transferMode.getMode() == TransferMode.State.DOWNLOAD_LIMIT_SEARCH)
/*      */     {
/*  917 */       this.downloadLimitConf = determineConfidenceLevel();
/*      */       
/*      */ 
/*  920 */       SpeedManagerLogger.trace("pre-upload-setting=" + this.preTestUploadCapacity + " up-capacity" + this.uploadLimitMax + " pre-download-setting=" + this.preTestDownloadCapacity + " down-capacity=" + this.downloadLimitMax);
/*      */       
/*      */ 
/*  923 */       SMUpdate retVal = new SMUpdate(this.preTestUploadLimit, true, this.downloadLimitMax, true);
/*      */       
/*  925 */       this.transferMode.setMode(TransferMode.State.DOWNLOADING);
/*      */     }
/*  927 */     else if (this.transferMode.getMode() == TransferMode.State.UPLOAD_LIMIT_SEARCH)
/*      */     {
/*  929 */       this.uploadLimitConf = determineConfidenceLevel();
/*      */       
/*      */ 
/*  932 */       SMUpdate retVal = new SMUpdate(this.uploadLimitMax, true, this.downloadLimitMax, true);
/*      */       
/*  934 */       this.transferMode.setMode(TransferMode.State.SEEDING);
/*      */     }
/*      */     else
/*      */     {
/*  938 */       SpeedManagerLogger.log("SpeedLimitMonitor had IllegalState during endLimitTesting.");
/*  939 */       retVal = new SMUpdate(this.preTestUploadLimit, true, this.preTestDownloadLimit, true);
/*      */     }
/*      */     
/*  942 */     this.currTestDone = true;
/*      */     
/*      */ 
/*  945 */     this.uploadAtLimitStartTime = SystemTime.getCurrentTime();
/*  946 */     this.downloadAtLimitStartTime = SystemTime.getCurrentTime();
/*      */     
/*  948 */     return retVal;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SpeedLimitConfidence determineConfidenceLevel()
/*      */   {
/*  958 */     SpeedLimitConfidence retVal = SpeedLimitConfidence.NONE;
/*      */     
/*      */ 
/*      */ 
/*      */     int highestValue;
/*      */     
/*      */ 
/*  965 */     if (this.transferMode.getMode() == TransferMode.State.DOWNLOAD_LIMIT_SEARCH)
/*      */     {
/*  967 */       String settingConfidenceName = "SpeedLimitMonitor.setting.download.limit.conf";
/*  968 */       String settingMaxLimitName = "SpeedManagerAlgorithmProviderV2.setting.download.max.limit";
/*  969 */       boolean isDownload = true;
/*  970 */       int preTestValue = this.preTestDownloadCapacity;
/*  971 */       highestValue = this.highestDownloadRate; } else { int highestValue;
/*  972 */       if (this.transferMode.getMode() == TransferMode.State.UPLOAD_LIMIT_SEARCH)
/*      */       {
/*  974 */         String settingConfidenceName = "SpeedLimitMonitor.setting.upload.limit.conf";
/*  975 */         String settingMaxLimitName = "SpeedManagerAlgorithmProviderV2.setting.upload.max.limit";
/*  976 */         boolean isDownload = false;
/*  977 */         int preTestValue = this.preTestUploadCapacity;
/*  978 */         highestValue = this.highestUploadRate;
/*      */       }
/*      */       else {
/*  981 */         SpeedManagerLogger.log("IllegalState in determineConfidenceLevel(). Setting level to NONE.");
/*  982 */         return SpeedLimitConfidence.NONE; } }
/*      */     int highestValue;
/*      */     int preTestValue;
/*  985 */     String settingConfidenceName; boolean isDownload; String settingMaxLimitName; boolean hadChockingPing = hadChockingPing();
/*  986 */     float percentDiff = Math.abs(highestValue - preTestValue) / Math.max(highestValue, preTestValue);
/*  987 */     if ((percentDiff < 0.15F) && (hadChockingPing))
/*      */     {
/*  989 */       retVal = SpeedLimitConfidence.MED;
/*      */     } else {
/*  991 */       retVal = SpeedLimitConfidence.LOW;
/*      */     }
/*      */     
/*      */ 
/*  995 */     COConfigurationManager.setParameter(settingConfidenceName, retVal.getString());
/*  996 */     int newMaxLimitSetting = highestValue;
/*  997 */     COConfigurationManager.setParameter(settingMaxLimitName, newMaxLimitSetting);
/*      */     int newMinLimitSetting;
/*  999 */     int newMinLimitSetting; if (isDownload) {
/* 1000 */       newMinLimitSetting = SMConst.calculateMinDownload(newMaxLimitSetting);
/*      */     } else {
/* 1002 */       newMinLimitSetting = SMConst.calculateMinUpload(newMaxLimitSetting);
/*      */     }
/*      */     
/* 1005 */     StringBuilder sb = new StringBuilder();
/* 1006 */     if (this.transferMode.getMode() == TransferMode.State.UPLOAD_LIMIT_SEARCH) {
/* 1007 */       sb.append("new upload limits: ");
/* 1008 */       this.uploadLimitMax = newMaxLimitSetting;
/* 1009 */       this.uploadLimitMin = newMinLimitSetting;
/*      */       
/* 1011 */       if (this.downloadLimitMax < this.uploadLimitMax) {
/* 1012 */         this.downloadLimitMax = this.uploadLimitMax;
/* 1013 */         COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit", this.downloadLimitMax);
/*      */       }
/*      */       
/* 1016 */       sb.append(this.uploadLimitMax);
/*      */     } else {
/* 1018 */       sb.append("new download limits: ");
/* 1019 */       this.downloadLimitMax = newMaxLimitSetting;
/* 1020 */       this.downloadLimitMin = newMinLimitSetting;
/*      */       
/* 1022 */       if (this.uploadLimitMax * 40 < this.downloadLimitMax) {
/* 1023 */         this.uploadLimitMax = (this.downloadLimitMax / 40);
/* 1024 */         COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit", this.uploadLimitMax);
/*      */         
/*      */ 
/* 1027 */         this.uploadLimitMin = SMConst.calculateMinUpload(this.uploadLimitMax);
/*      */       }
/* 1029 */       sb.append(this.downloadLimitMax);
/*      */     }
/*      */     
/* 1032 */     this.slider.updateLimits(this.uploadLimitMax, this.uploadLimitMin, this.downloadLimitMax, this.downloadLimitMin);
/*      */     
/* 1034 */     SpeedManagerLogger.trace(sb.toString());
/*      */     
/* 1036 */     return retVal;
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
/*      */   public boolean areSettingsInSpec(int currUploadLimit, int currDownloadLimit)
/*      */   {
/* 1049 */     if (isConfTestingLimits()) {
/* 1050 */       return true;
/*      */     }
/*      */     
/* 1053 */     boolean retVal = true;
/* 1054 */     if (currUploadLimit > this.uploadLimitMax) {
/* 1055 */       retVal = false;
/*      */     }
/* 1057 */     if ((currDownloadLimit > this.downloadLimitMax) && (this.slider.isDownloadUnlimitedMode())) {
/* 1058 */       retVal = false;
/*      */     }
/* 1060 */     return retVal;
/*      */   }
/*      */   
/*      */   private int choseBestLimit(SpeedManagerLimitEstimate estimate, int currMaxLimit, SpeedLimitConfidence currConf) {
/* 1064 */     float type = estimate.getEstimateType();
/* 1065 */     int estBytesPerSec = estimate.getBytesPerSec();
/*      */     
/*      */ 
/*      */ 
/* 1069 */     if ((estBytesPerSec < currMaxLimit) && (estBytesPerSec < 20480)) {
/* 1070 */       return currMaxLimit;
/*      */     }
/*      */     
/* 1073 */     String reason = "";
/* 1074 */     int chosenLimit; if (type == 1.0F) {
/* 1075 */       int chosenLimit = estBytesPerSec;
/* 1076 */       reason = "manual";
/* 1077 */     } else if (type == -0.1F) {
/* 1078 */       int chosenLimit = Math.max(estBytesPerSec, currMaxLimit);
/* 1079 */       reason = "unknown";
/* 1080 */     } else if (type == 0.0F)
/*      */     {
/* 1082 */       if (estimate.getMetricRating() >= 0.0D)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1087 */         return currMaxLimit;
/*      */       }
/*      */       
/*      */ 
/* 1091 */       int chosenLimit = estBytesPerSec;
/* 1092 */       reason = "estimate and bad metric";
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1097 */       chosenLimit = estBytesPerSec;
/*      */     }
/*      */     
/* 1100 */     SpeedManagerLogger.trace("bestChosenLimit: reason=" + reason + ",chosenLimit=" + chosenLimit);
/*      */     
/* 1102 */     return chosenLimit;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRefLimits(SpeedManagerLimitEstimate estUp, SpeedManagerLimitEstimate estDown)
/*      */   {
/* 1112 */     SpeedManagerLimitEstimate up = SMConst.filterEstimate(estUp, 5120);
/* 1113 */     int upMax = choseBestLimit(up, this.uploadLimitMax, this.uploadLimitConf);
/*      */     
/* 1115 */     SpeedManagerLimitEstimate down = SMConst.filterEstimate(estDown, 20480);
/* 1116 */     int downMax = choseBestLimit(down, this.downloadLimitMax, this.downloadLimitConf);
/*      */     
/* 1118 */     if (downMax < upMax) {
/* 1119 */       SpeedManagerLogger.trace("down max-limit was less then up-max limit. increasing down max-limit. upMax=" + upMax + " downMax=" + downMax);
/*      */       
/* 1121 */       downMax = upMax;
/*      */     }
/*      */     
/* 1124 */     setRefLimits(upMax, downMax);
/*      */   }
/*      */   
/*      */   public void setRefLimits(int uploadMax, int downloadMax)
/*      */   {
/* 1129 */     if ((this.uploadLimitMax != uploadMax) && (uploadMax > 0)) {
/* 1130 */       this.uploadLimitMax = uploadMax;
/* 1131 */       COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit", this.uploadLimitMax);
/*      */     }
/*      */     
/*      */ 
/* 1135 */     this.uploadLimitMin = SMConst.calculateMinUpload(uploadMax);
/*      */     
/* 1137 */     if ((this.downloadLimitMax != downloadMax) && (downloadMax > 0)) {
/* 1138 */       this.downloadLimitMax = downloadMax;
/* 1139 */       COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit", this.downloadLimitMax);
/*      */     }
/*      */     
/*      */ 
/* 1143 */     this.downloadLimitMin = SMConst.calculateMinDownload(downloadMax);
/*      */     
/* 1145 */     SpeedManagerLogger.trace("setRefLimits uploadMax=" + uploadMax + " uploadLimitMax=" + this.uploadLimitMax + ", downloadMax=" + downloadMax + " downloadLimitMax=" + this.downloadLimitMax);
/*      */     
/* 1147 */     this.slider.updateLimits(this.uploadLimitMax, this.uploadLimitMin, this.downloadLimitMax, this.downloadLimitMin);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SMUpdate adjustLimitsToSpec(int currUploadLimit, int currDownloadLimit)
/*      */   {
/* 1159 */     int newUploadLimit = currUploadLimit;
/* 1160 */     boolean uploadChanged = false;
/* 1161 */     int newDownloadLimit = currDownloadLimit;
/* 1162 */     boolean downloadChanged = false;
/*      */     
/* 1164 */     StringBuilder reason = new StringBuilder();
/*      */     
/*      */ 
/* 1167 */     if ((currUploadLimit > this.uploadLimitMax) && (this.uploadLimitMax != 0))
/*      */     {
/* 1169 */       newUploadLimit = this.uploadLimitMax;
/* 1170 */       uploadChanged = true;
/*      */       
/* 1172 */       reason.append(" (a) upload line-speed cap below current limit. ");
/*      */     }
/*      */     
/* 1175 */     if (this.uploadLimitMax == 0) {
/* 1176 */       reason.append("** uploadLimitMax=0 (Unlimited)! ** ");
/*      */     }
/*      */     
/*      */ 
/* 1180 */     if ((currDownloadLimit > this.downloadLimitMax) && (!this.slider.isDownloadUnlimitedMode())) {
/* 1181 */       newDownloadLimit = this.downloadLimitMax;
/* 1182 */       downloadChanged = true;
/*      */       
/* 1184 */       reason.append(" (b) download line-speed cap below current limit. ");
/*      */     }
/*      */     
/*      */ 
/* 1188 */     if (currUploadLimit < this.uploadLimitMin) {
/* 1189 */       newUploadLimit = this.uploadLimitMin;
/* 1190 */       uploadChanged = true;
/*      */       
/* 1192 */       reason.append(" (c) min upload limit raised. ");
/*      */     }
/*      */     
/* 1195 */     if (currDownloadLimit < this.downloadLimitMin) {
/* 1196 */       newDownloadLimit = this.downloadLimitMin;
/* 1197 */       downloadChanged = true;
/*      */       
/* 1199 */       reason.append(" (d)  min download limit raised. ");
/*      */     }
/*      */     
/* 1202 */     SpeedManagerLogger.trace("Adjusting limits due to out of spec: new-up=" + newUploadLimit + " new-down=" + newDownloadLimit + "  reasons: " + reason.toString());
/*      */     
/*      */ 
/* 1205 */     return new SMUpdate(newUploadLimit, uploadChanged, newDownloadLimit, downloadChanged);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 1211 */     SpeedManagerLogger.log(str);
/*      */   }
/*      */   
/*      */ 
/*      */   public void initPingSpaceMap(int maxGoodPing, int minBadPing)
/*      */   {
/* 1217 */     this.pingMapOfDownloadMode = new PingSpaceMapper(maxGoodPing, minBadPing);
/* 1218 */     this.pingMapOfSeedingMode = new PingSpaceMapper(maxGoodPing, minBadPing);
/*      */     
/*      */ 
/*      */ 
/* 1222 */     this.useVariancePingMap = false;
/*      */   }
/*      */   
/*      */   public void initPingSpaceMap() {
/* 1226 */     this.useVariancePingMap = true;
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
/*      */   public void betaLogPingMapperEstimates(String name, SpeedManagerLimitEstimate transEst, boolean hadChockPing, SpeedManagerLimitEstimate permEst, PingSpaceMapper downMode, PingSpaceMapper seedMode)
/*      */   {
/* 1251 */     StringBuilder sb = new StringBuilder("beta-ping-maps-").append(name).append(": ");
/*      */     
/* 1253 */     if (transEst != null) {
/* 1254 */       int rate = transEst.getBytesPerSec();
/* 1255 */       float conf = transEst.getMetricRating();
/* 1256 */       sb.append("transient-").append(rate).append("(").append(conf).append(")");
/*      */     }
/* 1258 */     sb.append(" chockPing=").append(hadChockPing);
/*      */     
/*      */ 
/* 1261 */     if (permEst != null) {
/* 1262 */       int rate = permEst.getBytesPerSec();
/* 1263 */       float conf = permEst.getMetricRating();
/* 1264 */       sb.append("; perm-").append(rate).append("(").append(conf).append(")");
/*      */     }
/*      */     
/* 1267 */     if (downMode != null) {
/* 1268 */       int rateDown = downMode.guessDownloadLimit();
/* 1269 */       int rateUp = downMode.guessUploadLimit();
/* 1270 */       boolean downChockPing = downMode.hadChockingPing(true);
/* 1271 */       boolean upChockPing = downMode.hadChockingPing(false);
/*      */       
/* 1273 */       sb.append("; downMode- ");
/* 1274 */       sb.append("rateDown=").append(rateDown).append(" ");
/* 1275 */       sb.append("rateUp=").append(rateUp).append(" ");
/* 1276 */       sb.append("downChockPing=").append(downChockPing).append(" ");
/* 1277 */       sb.append("upChockPing=").append(upChockPing).append(" ");
/*      */     }
/*      */     
/* 1280 */     if (seedMode != null) {
/* 1281 */       int rateDown = seedMode.guessDownloadLimit();
/* 1282 */       int rateUp = seedMode.guessUploadLimit();
/* 1283 */       boolean downChockPing = seedMode.hadChockingPing(true);
/* 1284 */       boolean upChockPing = seedMode.hadChockingPing(false);
/*      */       
/* 1286 */       sb.append("; seedMode- ");
/* 1287 */       sb.append("rateDown=").append(rateDown).append(" ");
/* 1288 */       sb.append("rateUp=").append(rateUp).append(" ");
/* 1289 */       sb.append("downChockPing=").append(downChockPing).append(" ");
/* 1290 */       sb.append("upChockPing=").append(upChockPing).append(" ");
/*      */     }
/* 1292 */     SpeedManagerLogger.log(sb.toString());
/*      */   }
/*      */   
/*      */   public int guessDownloadLimit()
/*      */   {
/* 1297 */     if (!this.useVariancePingMap) {
/* 1298 */       return this.pingMapOfDownloadMode.guessDownloadLimit();
/*      */     }
/*      */     
/* 1301 */     boolean wasChocked = true;
/* 1302 */     SpeedManagerLimitEstimate transientEst = null;
/* 1303 */     if (this.transientPingMap != null) {
/* 1304 */       transientEst = this.transientPingMap.getLastBadDownloadLimit();
/* 1305 */       if (transientEst == null) {
/* 1306 */         wasChocked = false;
/* 1307 */         transientEst = this.transientPingMap.getEstimatedDownloadLimit(false);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1312 */     SMInstance pm = SMInstance.getInstance();
/* 1313 */     SpeedManagerAlgorithmProviderAdapter adapter = pm.getAdapter();
/* 1314 */     SpeedManagerPingMapper persistentMap = adapter.getPingMapper();
/* 1315 */     SpeedManagerLimitEstimate persistentEst = persistentMap.getEstimatedDownloadLimit(false);
/*      */     
/*      */ 
/* 1318 */     betaLogPingMapperEstimates("down", transientEst, wasChocked, persistentEst, this.pingMapOfDownloadMode, this.pingMapOfSeedingMode);
/*      */     
/* 1320 */     if (transientEst != null)
/*      */     {
/* 1322 */       return choseBestLimit(transientEst, this.downloadLimitMax, this.downloadLimitConf);
/*      */     }
/* 1324 */     return this.downloadLimitMax;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int guessUploadLimit()
/*      */   {
/* 1332 */     if (!this.useVariancePingMap)
/*      */     {
/* 1334 */       int dmUpLimitGuess = this.pingMapOfDownloadMode.guessUploadLimit();
/* 1335 */       int smUpLimitGuess = this.pingMapOfSeedingMode.guessUploadLimit();
/*      */       
/* 1337 */       return Math.max(dmUpLimitGuess, smUpLimitGuess);
/*      */     }
/*      */     
/*      */ 
/* 1341 */     boolean wasChocked = true;
/* 1342 */     SpeedManagerLimitEstimate transientEst = null;
/* 1343 */     if (this.transientPingMap != null) {
/* 1344 */       transientEst = this.transientPingMap.getLastBadUploadLimit();
/* 1345 */       if (transientEst == null) {
/* 1346 */         wasChocked = false;
/* 1347 */         transientEst = this.transientPingMap.getEstimatedUploadLimit(false);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1352 */     SMInstance pm = SMInstance.getInstance();
/* 1353 */     SpeedManagerAlgorithmProviderAdapter adapter = pm.getAdapter();
/* 1354 */     SpeedManagerPingMapper persistentMap = adapter.getPingMapper();
/* 1355 */     SpeedManagerLimitEstimate persistentEst = persistentMap.getEstimatedUploadLimit(false);
/*      */     
/*      */ 
/* 1358 */     betaLogPingMapperEstimates("up", transientEst, wasChocked, persistentEst, this.pingMapOfDownloadMode, this.pingMapOfSeedingMode);
/*      */     
/* 1360 */     if (transientEst != null)
/*      */     {
/* 1362 */       return choseBestLimit(transientEst, this.uploadLimitMax, this.uploadLimitConf);
/*      */     }
/* 1364 */     return this.uploadLimitMax;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hadChockingPing()
/*      */   {
/* 1376 */     if (!this.useVariancePingMap)
/*      */     {
/* 1378 */       return this.pingMapOfDownloadMode.hadChockingPing(true);
/*      */     }
/*      */     
/* 1381 */     SpeedManagerPingMapper pm = SMInstance.getInstance().getAdapter().getPingMapper();
/*      */     
/*      */ 
/* 1384 */     SpeedManagerLimitEstimate dEst = pm.getEstimatedDownloadLimit(true);
/* 1385 */     SpeedManagerLimitEstimate uEst = pm.getEstimatedUploadLimit(true);
/*      */     
/* 1387 */     boolean hadChokePingUp = uEst.getEstimateType() == 0.5F;
/* 1388 */     boolean hadChokePingDown = dEst.getEstimateType() == 0.5F;
/*      */     
/* 1390 */     return (hadChokePingUp) || (hadChokePingDown);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void logPingMapData()
/*      */   {
/* 1399 */     if (!this.useVariancePingMap) {
/* 1400 */       int downLimGuess = this.pingMapOfDownloadMode.guessDownloadLimit();
/* 1401 */       int upLimGuess = this.pingMapOfDownloadMode.guessUploadLimit();
/* 1402 */       int seedingUpLimGuess = this.pingMapOfSeedingMode.guessUploadLimit();
/*      */       
/* 1404 */       StringBuilder sb = new StringBuilder("ping-map: ");
/* 1405 */       sb.append(":down=").append(downLimGuess);
/* 1406 */       sb.append(":up=").append(upLimGuess);
/* 1407 */       sb.append(":(seed)up=").append(seedingUpLimGuess);
/*      */       
/* 1409 */       SpeedManagerLogger.log(sb.toString());
/*      */     } else {
/* 1411 */       SMInstance pm = SMInstance.getInstance();
/* 1412 */       SpeedManagerAlgorithmProviderAdapter adapter = pm.getAdapter();
/* 1413 */       SpeedManagerPingMapper persistentMap = adapter.getPingMapper();
/*      */       
/* 1415 */       SpeedManagerLimitEstimate estUp = persistentMap.getEstimatedUploadLimit(false);
/* 1416 */       SpeedManagerLimitEstimate estDown = persistentMap.getEstimatedDownloadLimit(false);
/*      */       
/* 1418 */       int downLimGuess = estDown.getBytesPerSec();
/* 1419 */       float downConf = estDown.getMetricRating();
/* 1420 */       int upLimGuess = estUp.getBytesPerSec();
/* 1421 */       float upConf = estUp.getMetricRating();
/*      */       
/* 1423 */       String name = persistentMap.getName();
/*      */       
/* 1425 */       StringBuilder sb = new StringBuilder("new-ping-map: ");
/* 1426 */       sb.append(" name=").append(name);
/* 1427 */       sb.append(", down=").append(downLimGuess);
/* 1428 */       sb.append(", down-conf=").append(downConf);
/* 1429 */       sb.append(", up=").append(upLimGuess);
/* 1430 */       sb.append(", up-conf=").append(upConf);
/*      */       
/* 1432 */       SpeedManagerLogger.log(sb.toString());
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCurrentTransferRates(int downRate, int upRate)
/*      */   {
/* 1438 */     if ((this.pingMapOfDownloadMode != null) && (this.pingMapOfSeedingMode != null)) {
/* 1439 */       this.pingMapOfDownloadMode.setCurrentTransferRates(downRate, upRate);
/* 1440 */       this.pingMapOfSeedingMode.setCurrentTransferRates(downRate, upRate);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void resetPingSpace()
/*      */   {
/* 1447 */     if ((this.pingMapOfDownloadMode != null) && (this.pingMapOfSeedingMode != null)) {
/* 1448 */       this.pingMapOfDownloadMode.reset();
/* 1449 */       this.pingMapOfSeedingMode.reset();
/*      */     }
/*      */     
/* 1452 */     if (this.transientPingMap != null) {
/* 1453 */       this.transientPingMap.destroy();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addToPingMapData(int lastMetricValue) {
/* 1458 */     String modeStr = getTransferModeAsString();
/*      */     
/* 1460 */     if ((modeStr.equalsIgnoreCase(TransferMode.State.DOWNLOADING.getString())) || (modeStr.equalsIgnoreCase(TransferMode.State.DOWNLOAD_LIMIT_SEARCH.getString())))
/*      */     {
/*      */ 
/*      */ 
/* 1464 */       this.pingMapOfDownloadMode.addMetricToMap(lastMetricValue);
/*      */ 
/*      */     }
/* 1467 */     else if ((modeStr.equalsIgnoreCase(TransferMode.State.SEEDING.getString())) || (modeStr.equalsIgnoreCase(TransferMode.State.UPLOAD_LIMIT_SEARCH.getString())))
/*      */     {
/*      */ 
/*      */ 
/* 1471 */       this.pingMapOfSeedingMode.addMetricToMap(lastMetricValue);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1477 */     updateLimitTestingPing(lastMetricValue);
/*      */     
/* 1479 */     this.longTermMonitor.updateStatus(this.transferMode);
/*      */   }
/*      */   
/*      */ 
/*      */   public void notifyUpload(SpeedManagerLimitEstimate estimate)
/*      */   {
/* 1485 */     int bestLimit = choseBestLimit(estimate, this.uploadLimitMax, this.uploadLimitConf);
/*      */     
/* 1487 */     SpeedManagerLogger.trace("notifyUpload uploadLimitMax=" + this.uploadLimitMax);
/* 1488 */     tempLogEstimate(estimate);
/*      */     
/* 1490 */     if (bestLimit != this.uploadLimitMax)
/*      */     {
/* 1492 */       SpeedManagerLogger.log("persistent PingMap changed upload limit to " + bestLimit);
/*      */       
/* 1494 */       resetPinSearch(estimate);
/*      */       
/* 1496 */       this.uploadLimitMax = bestLimit;
/* 1497 */       COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit", this.uploadLimitMax);
/*      */     }
/*      */     
/*      */ 
/* 1501 */     this.uploadLimitMin = SMConst.calculateMinUpload(this.uploadLimitMax);
/* 1502 */     this.slider.updateLimits(this.uploadLimitMax, this.uploadLimitMin, this.downloadLimitMax, this.downloadLimitMin);
/*      */     
/* 1504 */     SMSearchLogger.log("new upload rate: " + this.uploadLimitMax);
/*      */   }
/*      */   
/*      */   public void notifyDownload(SpeedManagerLimitEstimate estimate) {
/* 1508 */     int bestLimit = choseBestLimit(estimate, this.downloadLimitMax, this.downloadLimitConf);
/*      */     
/* 1510 */     SpeedManagerLogger.trace("notifyDownload downloadLimitMax=" + this.downloadLimitMax + " conf=" + this.downloadLimitConf.getString() + " (" + this.downloadLimitConf.asEstimateType() + ")");
/*      */     
/* 1512 */     tempLogEstimate(estimate);
/*      */     
/* 1514 */     if (this.downloadLimitMax != bestLimit)
/*      */     {
/* 1516 */       SpeedManagerLogger.log("persistent PingMap changed download limit to " + bestLimit);
/* 1517 */       this.downloadLimitMax = bestLimit;
/* 1518 */       COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit", bestLimit);
/*      */     }
/*      */     
/*      */ 
/* 1522 */     this.downloadLimitMin = SMConst.calculateMinDownload(this.downloadLimitMax);
/* 1523 */     this.slider.updateLimits(this.uploadLimitMax, this.uploadLimitMin, this.downloadLimitMax, this.downloadLimitMin);
/*      */     
/*      */ 
/* 1526 */     if (estimate.getBytesPerSec() != 0) {
/* 1527 */       this.slider.setDownloadUnlimitedMode(false);
/*      */     } else {
/* 1529 */       this.slider.setDownloadUnlimitedMode(true);
/*      */     }
/*      */     
/* 1532 */     SMSearchLogger.log("download " + this.downloadLimitMax);
/*      */   }
/*      */   
/*      */   private void tempLogEstimate(SpeedManagerLimitEstimate est)
/*      */   {
/* 1537 */     if (est == null) {
/* 1538 */       SpeedManagerLogger.trace("notify log: SpeedManagerLimitEstimate was null");
/* 1539 */       return;
/*      */     }
/*      */     
/* 1542 */     StringBuilder sb = new StringBuilder();
/* 1543 */     float metric = est.getMetricRating();
/* 1544 */     float type = est.getEstimateType();
/* 1545 */     int rate = est.getBytesPerSec();
/*      */     
/* 1547 */     String str = est.getString();
/*      */     
/* 1549 */     sb.append("notify log: ").append(str);
/* 1550 */     sb.append(" metricRating=").append(metric);
/* 1551 */     sb.append(" rate=").append(rate);
/* 1552 */     sb.append(" type=").append(type);
/*      */     
/* 1554 */     SpeedManagerLogger.trace(sb.toString());
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SpeedLimitMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */