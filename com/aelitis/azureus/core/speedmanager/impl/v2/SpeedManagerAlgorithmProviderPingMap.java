/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingMapper;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingSource;
/*     */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProvider;
/*     */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProviderAdapter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class SpeedManagerAlgorithmProviderPingMap
/*     */   implements SpeedManagerAlgorithmProvider, COConfigurationListener
/*     */ {
/*     */   private final SpeedManagerAlgorithmProviderAdapter adapter;
/*     */   private long timeSinceLastUpdate;
/*  38 */   private int consecutiveUpticks = 0;
/*  39 */   private int consecutiveDownticks = 0;
/*     */   
/*     */ 
/*     */ 
/*     */   private final SpeedLimitMonitor limitMonitor;
/*     */   
/*     */ 
/*     */   private float lastMetricValue;
/*     */   
/*     */ 
/*  49 */   private static int numIntervalsBetweenCal = 2;
/*  50 */   private static boolean skipIntervalAfterAdjustment = true;
/*     */   
/*  52 */   private List pingTimeList = new ArrayList();
/*  53 */   private boolean hadAdjustmentLastInterval = false;
/*  54 */   private int intervalCount = 0;
/*     */   
/*     */ 
/*  57 */   final PingSourceManager pingSourceManager = new PingSourceManager();
/*     */   
/*     */ 
/*  60 */   int sessionMaxUploadRate = 0;
/*     */   
/*     */ 
/*     */   SpeedManagerAlgorithmProviderPingMap(SpeedManagerAlgorithmProviderAdapter _adapter)
/*     */   {
/*  65 */     this.adapter = _adapter;
/*     */     
/*  67 */     SpeedManagerLogger.setAdapter("pm", this.adapter);
/*     */     
/*  69 */     this.limitMonitor = new SpeedLimitMonitor(this.adapter.getSpeedManager());
/*     */     
/*  71 */     COConfigurationManager.addListener(this);
/*     */     
/*  73 */     SMInstance.init(_adapter);
/*     */     
/*  75 */     this.limitMonitor.initPingSpaceMap();
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/*  81 */     COConfigurationManager.removeListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void configurationSaved()
/*     */   {
/*     */     try
/*     */     {
/*  89 */       this.limitMonitor.readFromPersistentMap();
/*  90 */       this.limitMonitor.updateFromCOConfigManager();
/*     */       
/*  92 */       skipIntervalAfterAdjustment = COConfigurationManager.getBooleanParameter("SpeedManagerAlgorithmProviderV2.setting.wait.after.adjust");
/*     */       
/*  94 */       numIntervalsBetweenCal = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.intervals.between.adjust");
/*     */       
/*     */ 
/*  97 */       this.limitMonitor.initPingSpaceMap();
/*     */       
/*  99 */       SpeedManagerLogger.trace("..VariancePingMap - configurationSaved called.");
/*     */     }
/*     */     catch (Throwable t) {
/* 102 */       SpeedManagerLogger.log(t.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 111 */     log("reset");
/*     */     
/* 113 */     log("curr-data-m: curr-down-rate : curr-down-limit : down-capacity : down-bandwith-mode : down-limit-mode : curr-up-rate : curr-up-limit : up-capacity : upload-bandwidth-mode : upload-limit-mode : transfer-mode");
/*     */     
/* 115 */     log("new-limit:newLimit:currStep:signalStrength:multiple:currUpLimit:maxStep:uploadLimitMax:uploadLimitMin:transferMode");
/*     */     
/* 117 */     log("consecutive:up:down");
/*     */     
/* 119 */     log("metric:value:type");
/*     */     
/* 121 */     log("user-comment:log");
/*     */     
/* 123 */     log("pin:upload-status,download-status,upload-unpin-timer,download-unpin-timer");
/*     */     
/* 125 */     log("limits:down-max:down-min:down-conf:up-max:up-min:up-conf");
/*     */     
/* 127 */     this.limitMonitor.resetPingSpace();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateStats()
/*     */   {
/* 138 */     int currUploadLimit = this.adapter.getCurrentUploadLimit();
/* 139 */     int currDataUploadSpeed = this.adapter.getCurrentDataUploadSpeed();
/* 140 */     int currProtoUploadSpeed = this.adapter.getCurrentProtocolUploadSpeed();
/* 141 */     int upRateBitsPerSec = currDataUploadSpeed + currProtoUploadSpeed;
/*     */     
/* 143 */     int currDownLimit = this.adapter.getCurrentDownloadLimit();
/* 144 */     int downDataRate = this.adapter.getCurrentDataDownloadSpeed();
/* 145 */     int downProtoRate = this.adapter.getCurrentProtocolDownloadSpeed();
/* 146 */     int downRateBitsPerSec = downDataRate + downProtoRate;
/*     */     
/*     */ 
/* 149 */     this.limitMonitor.setDownloadBandwidthMode(downRateBitsPerSec, currDownLimit);
/* 150 */     this.limitMonitor.setUploadBandwidthMode(upRateBitsPerSec, currUploadLimit);
/*     */     
/*     */ 
/* 153 */     this.limitMonitor.setDownloadLimitSettingMode(currDownLimit);
/* 154 */     this.limitMonitor.setUploadLimitSettingMode(currUploadLimit);
/*     */     
/* 156 */     this.limitMonitor.updateTransferMode();
/*     */     
/* 158 */     if (this.limitMonitor.isConfTestingLimits()) {
/* 159 */       this.limitMonitor.updateLimitTestingData(downRateBitsPerSec, upRateBitsPerSec);
/*     */     }
/*     */     
/*     */ 
/* 163 */     this.limitMonitor.setCurrentTransferRates(downRateBitsPerSec, upRateBitsPerSec);
/*     */     
/*     */ 
/* 166 */     if (upRateBitsPerSec > this.sessionMaxUploadRate) {
/* 167 */       this.sessionMaxUploadRate = upRateBitsPerSec;
/*     */     }
/*     */     
/*     */ 
/* 171 */     logCurrentData(downRateBitsPerSec, currDownLimit, upRateBitsPerSec, currUploadLimit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void logCurrentData(int downRate, int currDownLimit, int upRate, int currUploadLimit)
/*     */   {
/* 182 */     StringBuilder sb = new StringBuilder("curr-data-m:" + downRate + ":" + currDownLimit + ":");
/* 183 */     sb.append(this.limitMonitor.getDownloadMaxLimit()).append(":");
/* 184 */     sb.append(this.limitMonitor.getDownloadBandwidthMode()).append(":");
/* 185 */     sb.append(this.limitMonitor.getDownloadLimitSettingMode()).append(":");
/* 186 */     sb.append(upRate).append(":").append(currUploadLimit).append(":");
/* 187 */     sb.append(this.limitMonitor.getUploadMaxLimit()).append(":");
/* 188 */     sb.append(this.limitMonitor.getUploadBandwidthMode()).append(":");
/* 189 */     sb.append(this.limitMonitor.getUploadLimitSettingMode()).append(":");
/* 190 */     sb.append(this.limitMonitor.getTransferModeAsString());
/*     */     
/* 192 */     SpeedManagerLogger.log(sb.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pingSourceFound(SpeedManagerPingSource source, boolean is_replacement)
/*     */   {
/* 204 */     log("pingSourceFound");
/*     */     
/* 206 */     this.pingSourceManager.pingSourceFound(source, is_replacement);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pingSourceFailed(SpeedManagerPingSource source)
/*     */   {
/* 217 */     log("pingSourceFailed");
/*     */     
/* 219 */     this.pingSourceManager.pingSourceFailed(source);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void calculate(SpeedManagerPingSource[] sources)
/*     */   {
/* 231 */     this.limitMonitor.logPMDataEx();
/*     */     
/*     */ 
/* 234 */     int len = sources.length;
/* 235 */     for (int i = 0; i < len; i++) {
/* 236 */       this.pingSourceManager.addPingTime(sources[i]);
/* 237 */       int pingTime = sources[i].getPingTime();
/*     */       
/*     */ 
/* 240 */       if (pingTime > 0)
/*     */       {
/* 242 */         this.intervalCount += 1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 247 */     if (this.limitMonitor.isConfTestingLimits())
/*     */     {
/* 249 */       if (this.limitMonitor.isConfLimitTestFinished()) {
/* 250 */         endLimitTesting();
/* 251 */         return;
/*     */       }
/*     */       
/* 254 */       SMUpdate ramp = this.limitMonitor.rampTestingLimit(this.adapter.getCurrentUploadLimit(), this.adapter.getCurrentDownloadLimit());
/*     */       
/*     */ 
/*     */ 
/* 258 */       logNewLimits(ramp);
/* 259 */       setNewLimits(ramp);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 264 */     long currTime = SystemTime.getCurrentTime();
/*     */     
/* 266 */     if (this.timeSinceLastUpdate == 0L) {
/* 267 */       this.timeSinceLastUpdate = currTime;
/*     */     }
/*     */     
/*     */ 
/* 271 */     if (calculatePingMetric()) {
/* 272 */       return;
/*     */     }
/*     */     
/* 275 */     log("metric:" + this.lastMetricValue);
/* 276 */     logLimitStatus();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 281 */     float signalStrength = determineSignalStrength(this.lastMetricValue);
/*     */     
/*     */ 
/* 284 */     if ((signalStrength != 0.0F) && (!this.limitMonitor.isConfTestingLimits())) {
/* 285 */       this.hadAdjustmentLastInterval = true;
/*     */       
/* 287 */       float multiple = consectiveMultiplier();
/* 288 */       int currUpLimit = this.adapter.getCurrentUploadLimit();
/* 289 */       int currDownLimit = this.adapter.getCurrentDownloadLimit();
/*     */       
/* 291 */       this.limitMonitor.checkForUnpinningCondition();
/*     */       
/* 293 */       SMUpdate update = this.limitMonitor.modifyLimits(signalStrength, multiple, currUpLimit, currDownLimit);
/*     */       
/*     */ 
/* 296 */       logNewLimits(update);
/*     */       
/*     */ 
/* 299 */       setNewLimits(update);
/*     */     }
/*     */     else {
/* 302 */       this.hadAdjustmentLastInterval = false;
/*     */       
/*     */ 
/* 305 */       int currUploadLimit = this.adapter.getCurrentUploadLimit();
/* 306 */       int currDownloadLimit = this.adapter.getCurrentDownloadLimit();
/* 307 */       if (!this.limitMonitor.areSettingsInSpec(currUploadLimit, currDownloadLimit)) {
/* 308 */         SMUpdate update = this.limitMonitor.adjustLimitsToSpec(currUploadLimit, currDownloadLimit);
/* 309 */         logNewLimits(update);
/* 310 */         setNewLimits(update);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 316 */     this.pingSourceManager.checkPingSources(sources);
/*     */   }
/*     */   
/*     */   private void endLimitTesting() {
/* 320 */     int downLimitGuess = this.limitMonitor.guessDownloadLimit();
/* 321 */     int upLimitGuess = this.limitMonitor.guessUploadLimit();
/*     */     
/* 323 */     SMUpdate update = this.limitMonitor.endLimitTesting(downLimitGuess, upLimitGuess);
/*     */     
/*     */ 
/*     */ 
/* 327 */     this.limitMonitor.logPingMapData();
/*     */     
/*     */ 
/* 330 */     this.limitMonitor.resetPingSpace();
/*     */     
/*     */ 
/* 333 */     logNewLimits(update);
/*     */     
/* 335 */     setNewLimits(update);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void logLimitStatus()
/*     */   {
/* 345 */     StringBuilder msg = new StringBuilder();
/* 346 */     msg.append("limits:");
/* 347 */     msg.append(this.limitMonitor.getUploadMaxLimit()).append(":");
/* 348 */     msg.append(this.limitMonitor.getUploadMinLimit()).append(":");
/* 349 */     msg.append(this.limitMonitor.getUploadConfidence()).append(":");
/* 350 */     msg.append(this.limitMonitor.getDownloadMaxLimit()).append(":");
/* 351 */     msg.append(this.limitMonitor.getDownloadMinLimit()).append(":");
/* 352 */     msg.append(this.limitMonitor.getDownloadConfidence());
/*     */     
/* 354 */     SpeedManagerLogger.log(msg.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean calculatePingMetric()
/*     */   {
/* 363 */     if ((skipIntervalAfterAdjustment) && (this.hadAdjustmentLastInterval)) {
/* 364 */       this.hadAdjustmentLastInterval = false;
/* 365 */       this.pingTimeList = new ArrayList();
/* 366 */       this.intervalCount = 0;
/* 367 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 371 */     if (this.intervalCount < numIntervalsBetweenCal)
/*     */     {
/* 373 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 377 */     this.lastMetricValue = ((float)this.adapter.getPingMapper().getCurrentMetricRating());
/*     */     
/*     */ 
/* 380 */     this.intervalCount = 0;
/* 381 */     return false;
/*     */   }
/*     */   
/*     */   private void logNewLimits(SMUpdate update) {
/* 385 */     if (update.hasNewUploadLimit) {
/* 386 */       int kbpsUpoadLimit = update.newUploadLimit / 1024;
/* 387 */       log(" new up limit  : " + kbpsUpoadLimit + " kb/s");
/*     */     }
/*     */     
/* 390 */     if (update.hasNewDownloadLimit) {
/* 391 */       int kpbsDownloadLimit = update.newDownloadLimit / 1024;
/* 392 */       log(" new down limit: " + kpbsDownloadLimit + " kb/s");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setNewLimits(SMUpdate update)
/*     */   {
/* 402 */     this.adapter.setCurrentUploadLimit(update.newUploadLimit);
/* 403 */     this.adapter.setCurrentDownloadLimit(update.newDownloadLimit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private float determineSignalStrength(float lastMetric)
/*     */   {
/* 412 */     float signal = convertTestMetricToSignal(lastMetric);
/*     */     
/* 414 */     if (signal > 0.0F) {
/* 415 */       this.consecutiveUpticks += 1;
/* 416 */       this.consecutiveDownticks = 0;
/* 417 */     } else if (signal < 0.0F) {
/* 418 */       this.consecutiveUpticks = 0;
/* 419 */       this.consecutiveDownticks += 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 424 */     log("consecutive:" + this.consecutiveUpticks + ":" + this.consecutiveDownticks);
/*     */     
/* 426 */     return signal;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private float convertTestMetricToSignal(float testMetric)
/*     */   {
/* 436 */     if (testMetric >= 1.0F) {
/* 437 */       return 1.0F;
/*     */     }
/* 439 */     if (testMetric <= -1.0F) {
/* 440 */       return -1.0F;
/*     */     }
/*     */     
/*     */ 
/* 444 */     if ((testMetric > -0.5F) && (testMetric < 0.5F)) {
/* 445 */       return 0.0F;
/*     */     }
/*     */     
/*     */ 
/* 449 */     if (testMetric > 0.0F) {
/* 450 */       return (testMetric - 0.5F) * 2.0F;
/*     */     }
/*     */     
/*     */ 
/* 454 */     return (testMetric + 0.5F) * 2.0F;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private float consectiveMultiplier()
/*     */   {
/*     */     float multiple;
/*     */     
/*     */ 
/*     */     float multiple;
/*     */     
/*     */ 
/* 468 */     if (this.consecutiveUpticks > this.consecutiveDownticks)
/*     */     {
/*     */ 
/* 471 */       if (this.limitMonitor.bandwidthUsageLow()) {
/* 472 */         this.consecutiveUpticks = 0;
/*     */       }
/*     */       
/* 475 */       multiple = calculateUpTickMultiple(this.consecutiveUpticks);
/*     */     } else {
/* 477 */       multiple = calculateDownTickMultiple(this.consecutiveDownticks);
/* 478 */       this.limitMonitor.notifyOfDownSignal();
/*     */     }
/*     */     
/* 481 */     return multiple;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private float calculateUpTickMultiple(int c)
/*     */   {
/* 493 */     float multiple = 0.0F;
/*     */     
/* 495 */     if (c < 0) {
/* 496 */       return multiple;
/*     */     }
/*     */     
/* 499 */     switch (c) {
/*     */     case 0: 
/*     */     case 1: 
/* 502 */       multiple = 0.25F;
/* 503 */       break;
/*     */     case 2: 
/* 505 */       multiple = 0.5F;
/* 506 */       break;
/*     */     case 3: 
/* 508 */       multiple = 1.0F;
/* 509 */       break;
/*     */     case 4: 
/* 511 */       multiple = 1.25F;
/* 512 */       break;
/*     */     case 5: 
/* 514 */       multiple = 1.5F;
/* 515 */       break;
/*     */     case 6: 
/* 517 */       multiple = 1.75F;
/* 518 */       break;
/*     */     case 7: 
/* 520 */       multiple = 2.0F;
/* 521 */       break;
/*     */     case 8: 
/* 523 */       multiple = 2.25F;
/* 524 */       break;
/*     */     case 9: 
/* 526 */       multiple = 2.5F;
/* 527 */       break;
/*     */     default: 
/* 529 */       multiple = 3.0F;
/*     */     }
/*     */     
/*     */     
/* 533 */     if (this.limitMonitor.bandwidthUsageMedium()) {
/* 534 */       multiple /= 2.0F;
/*     */     }
/*     */     
/* 537 */     return multiple;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private float calculateDownTickMultiple(int c)
/*     */   {
/* 547 */     float multiple = 0.0F;
/* 548 */     if (c < 0) {
/* 549 */       return multiple;
/*     */     }
/*     */     
/* 552 */     switch (c) {
/*     */     case 0: 
/*     */     case 1: 
/* 555 */       multiple = 0.25F;
/* 556 */       break;
/*     */     case 2: 
/* 558 */       multiple = 0.5F;
/* 559 */       break;
/*     */     case 3: 
/* 561 */       multiple = 1.0F;
/* 562 */       break;
/*     */     case 4: 
/* 564 */       multiple = 2.0F;
/* 565 */       break;
/*     */     case 5: 
/* 567 */       multiple = 3.0F;
/* 568 */       break;
/*     */     case 6: 
/* 570 */       multiple = 4.0F;
/* 571 */       break;
/*     */     case 7: 
/* 573 */       multiple = 6.0F;
/* 574 */       break;
/*     */     case 8: 
/* 576 */       multiple = 9.0F;
/* 577 */       break;
/*     */     case 9: 
/* 579 */       multiple = 15.0F;
/* 580 */       break;
/*     */     default: 
/* 582 */       multiple = 20.0F;
/*     */     }
/* 584 */     return multiple;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getIdlePingMillis()
/*     */   {
/* 596 */     return 0;
/*     */   }
/*     */   
/*     */   public int getCurrentPingMillis()
/*     */   {
/* 601 */     return 0;
/*     */   }
/*     */   
/*     */   public int getMaxPingMillis() {
/* 605 */     return 910;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getCurrentChokeSpeed()
/*     */   {
/* 615 */     return 0;
/*     */   }
/*     */   
/*     */   public int getMaxUploadSpeed() {
/* 619 */     return this.sessionMaxUploadRate;
/*     */   }
/*     */   
/*     */   public boolean getAdjustsDownloadLimits()
/*     */   {
/* 624 */     return true;
/*     */   }
/*     */   
/*     */   protected void log(String str) {
/* 628 */     SpeedManagerLogger.log(str);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SpeedManagerAlgorithmProviderPingMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */