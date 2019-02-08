/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreException;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingSource;
/*     */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProvider;
/*     */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProviderAdapter;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
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
/*     */ public class SpeedManagerAlgorithmProviderDHTPing
/*     */   implements SpeedManagerAlgorithmProvider, COConfigurationListener
/*     */ {
/*     */   private final SpeedManagerAlgorithmProviderAdapter adapter;
/*     */   private PluginInterface dhtPlugin;
/*     */   private long timeSinceLastUpdate;
/*  52 */   private static int metricGoodResult = 100;
/*  53 */   private static int metricGoodTolerance = 300;
/*  54 */   private static int metricBadResult = 1300;
/*  55 */   private static int metricBadTolerance = 300;
/*     */   
/*  57 */   private int consecutiveUpticks = 0;
/*  58 */   private int consecutiveDownticks = 0;
/*     */   
/*     */ 
/*     */   private final SpeedLimitMonitor limitMonitor;
/*     */   
/*     */ 
/*     */   private int lastMetricValue;
/*     */   
/*     */ 
/*  67 */   private static int numIntervalsBetweenCal = 2;
/*  68 */   private static boolean skipIntervalAfterAdjustment = true;
/*     */   
/*  70 */   private List pingTimeList = new ArrayList();
/*  71 */   private boolean hadAdjustmentLastInterval = false;
/*  72 */   private int intervalCount = 0;
/*     */   
/*     */ 
/*  75 */   final PingSourceManager pingSourceManager = new PingSourceManager();
/*     */   
/*  77 */   int sessionMaxUploadRate = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   SpeedManagerAlgorithmProviderDHTPing(SpeedManagerAlgorithmProviderAdapter _adapter)
/*     */   {
/*  84 */     this.adapter = _adapter;
/*     */     
/*  86 */     SpeedManagerLogger.setAdapter("dht", this.adapter);
/*     */     
/*  88 */     this.limitMonitor = new SpeedLimitMonitor(this.adapter.getSpeedManager());
/*     */     
/*  90 */     COConfigurationManager.addListener(this);
/*     */     
/*  92 */     SMInstance.init(_adapter);
/*     */     try
/*     */     {
/*  95 */       this.dhtPlugin = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*     */     } catch (AzureusCoreException ace) {
/*  97 */       log("Warning: AzureusCore was not initialized on startup.");
/*     */     }
/*     */     
/* 100 */     if (this.dhtPlugin == null)
/*     */     {
/* 102 */       log(" Error: failed to get DHT Plugin ");
/*     */     }
/*     */     
/* 105 */     this.limitMonitor.initPingSpaceMap(metricGoodResult + metricGoodTolerance, metricBadResult - metricBadTolerance);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 111 */     COConfigurationManager.removeListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void configurationSaved()
/*     */   {
/*     */     try
/*     */     {
/* 119 */       this.limitMonitor.readFromPersistentMap();
/* 120 */       this.limitMonitor.updateFromCOConfigManager();
/*     */       
/* 122 */       metricGoodResult = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.setting.dht.good.setpoint");
/*     */       
/* 124 */       metricGoodTolerance = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.setting.dht.good.tolerance");
/*     */       
/* 126 */       metricBadResult = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.setting.dht.bad.setpoint");
/*     */       
/* 128 */       metricBadTolerance = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.setting.dht.bad.tolerance");
/*     */       
/*     */ 
/* 131 */       skipIntervalAfterAdjustment = COConfigurationManager.getBooleanParameter("SpeedManagerAlgorithmProviderV2.setting.wait.after.adjust");
/*     */       
/* 133 */       numIntervalsBetweenCal = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.intervals.between.adjust");
/*     */       
/*     */ 
/* 136 */       this.limitMonitor.initPingSpaceMap(metricGoodResult + metricGoodTolerance, metricBadResult - metricBadTolerance);
/*     */       
/* 138 */       SpeedManagerLogger.trace("..DHTPing - configurationSaved called.");
/*     */     }
/*     */     catch (Throwable t) {
/* 141 */       SpeedManagerLogger.log(t.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 150 */     log("reset");
/*     */     
/* 152 */     log("curr-data: curr-down-rate : curr-down-limit : down-capacity : down-bandwith-mode : down-limit-mode : curr-up-rate : curr-up-limit : up-capacity : upload-bandwidth-mode : upload-limit-mode : transfer-mode");
/*     */     
/* 154 */     log("new-limit:newLimit:currStep:signalStrength:multiple:currUpLimit:maxStep:uploadLimitMax:uploadLimitMin:transferMode");
/*     */     
/* 156 */     log("consecutive:up:down");
/*     */     
/* 158 */     log("metric:value:type");
/*     */     
/* 160 */     log("user-comment:log");
/*     */     
/* 162 */     log("pin:upload-status,download-status,upload-unpin-timer,download-unpin-timer");
/*     */     
/* 164 */     log("limits:down-max:down-min:down-conf:up-max:up-min:up-conf");
/*     */     
/* 166 */     this.limitMonitor.resetPingSpace();
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
/* 177 */     int currUploadLimit = this.adapter.getCurrentUploadLimit();
/* 178 */     int currDataUploadSpeed = this.adapter.getCurrentDataUploadSpeed();
/* 179 */     int currProtoUploadSpeed = this.adapter.getCurrentProtocolUploadSpeed();
/* 180 */     int upRateBitsPerSec = currDataUploadSpeed + currProtoUploadSpeed;
/*     */     
/* 182 */     int currDownLimit = this.adapter.getCurrentDownloadLimit();
/* 183 */     int downDataRate = this.adapter.getCurrentDataDownloadSpeed();
/* 184 */     int downProtoRate = this.adapter.getCurrentProtocolDownloadSpeed();
/* 185 */     int downRateBitsPerSec = downDataRate + downProtoRate;
/*     */     
/*     */ 
/* 188 */     this.limitMonitor.setDownloadBandwidthMode(downRateBitsPerSec, currDownLimit);
/* 189 */     this.limitMonitor.setUploadBandwidthMode(upRateBitsPerSec, currUploadLimit);
/*     */     
/*     */ 
/* 192 */     this.limitMonitor.setDownloadLimitSettingMode(currDownLimit);
/* 193 */     this.limitMonitor.setUploadLimitSettingMode(currUploadLimit);
/*     */     
/* 195 */     this.limitMonitor.updateTransferMode();
/*     */     
/* 197 */     if (this.limitMonitor.isConfTestingLimits()) {
/* 198 */       this.limitMonitor.updateLimitTestingData(downRateBitsPerSec, upRateBitsPerSec);
/*     */     }
/*     */     
/*     */ 
/* 202 */     this.limitMonitor.setCurrentTransferRates(downRateBitsPerSec, upRateBitsPerSec);
/*     */     
/*     */ 
/* 205 */     if (upRateBitsPerSec > this.sessionMaxUploadRate) {
/* 206 */       this.sessionMaxUploadRate = upRateBitsPerSec;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 212 */     logCurrentData(downRateBitsPerSec, currDownLimit, upRateBitsPerSec, currUploadLimit);
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
/* 223 */     StringBuilder sb = new StringBuilder("curr-data-v:" + downRate + ":" + currDownLimit + ":");
/* 224 */     sb.append(this.limitMonitor.getDownloadMaxLimit()).append(":");
/* 225 */     sb.append(this.limitMonitor.getDownloadBandwidthMode()).append(":");
/* 226 */     sb.append(this.limitMonitor.getDownloadLimitSettingMode()).append(":");
/* 227 */     sb.append(upRate).append(":").append(currUploadLimit).append(":");
/* 228 */     sb.append(this.limitMonitor.getUploadMaxLimit()).append(":");
/* 229 */     sb.append(this.limitMonitor.getUploadBandwidthMode()).append(":");
/* 230 */     sb.append(this.limitMonitor.getUploadLimitSettingMode()).append(":");
/* 231 */     sb.append(this.limitMonitor.getTransferModeAsString());
/*     */     
/* 233 */     SpeedManagerLogger.log(sb.toString());
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
/* 245 */     log("pingSourceFound");
/*     */     
/* 247 */     this.pingSourceManager.pingSourceFound(source, is_replacement);
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
/* 258 */     log("pingSourceFailed");
/*     */     
/* 260 */     this.pingSourceManager.pingSourceFailed(source);
/*     */   }
/*     */   
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
/* 273 */     this.limitMonitor.logPMDataEx();
/*     */     
/*     */ 
/*     */ 
/* 277 */     int len = sources.length;
/* 278 */     for (int i = 0; i < len; i++) {
/* 279 */       this.pingSourceManager.addPingTime(sources[i]);
/* 280 */       int pingTime = sources[i].getPingTime();
/*     */       
/*     */ 
/* 283 */       if (pingTime > 0) {
/* 284 */         this.pingTimeList.add(new Integer(sources[i].getPingTime()));
/* 285 */         this.intervalCount += 1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 290 */     if (this.limitMonitor.isConfTestingLimits())
/*     */     {
/* 292 */       if (this.limitMonitor.isConfLimitTestFinished()) {
/* 293 */         endLimitTesting();
/* 294 */         return;
/*     */       }
/*     */       
/* 297 */       SMUpdate ramp = this.limitMonitor.rampTestingLimit(this.adapter.getCurrentUploadLimit(), this.adapter.getCurrentDownloadLimit());
/*     */       
/*     */ 
/*     */ 
/* 301 */       logNewLimits(ramp);
/* 302 */       setNewLimits(ramp);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 307 */     long currTime = SystemTime.getCurrentTime();
/*     */     
/* 309 */     if (this.timeSinceLastUpdate == 0L) {
/* 310 */       this.timeSinceLastUpdate = currTime;
/*     */     }
/*     */     
/*     */ 
/* 314 */     if (calculateMediaDHTPingTime()) {
/* 315 */       return;
/*     */     }
/*     */     
/* 318 */     log("metric:" + this.lastMetricValue);
/* 319 */     logLimitStatus();
/*     */     
/*     */ 
/* 322 */     this.limitMonitor.addToPingMapData(this.lastMetricValue);
/*     */     
/* 324 */     float signalStrength = determineSignalStrength(this.lastMetricValue);
/*     */     
/*     */ 
/* 327 */     if ((signalStrength != 0.0F) && (!this.limitMonitor.isConfTestingLimits())) {
/* 328 */       this.hadAdjustmentLastInterval = true;
/*     */       
/* 330 */       float multiple = consectiveMultiplier();
/* 331 */       int currUpLimit = this.adapter.getCurrentUploadLimit();
/* 332 */       int currDownLimit = this.adapter.getCurrentDownloadLimit();
/*     */       
/* 334 */       this.limitMonitor.checkForUnpinningCondition();
/*     */       
/* 336 */       SMUpdate update = this.limitMonitor.modifyLimits(signalStrength, multiple, currUpLimit, currDownLimit);
/*     */       
/*     */ 
/* 339 */       logNewLimits(update);
/*     */       
/*     */ 
/* 342 */       setNewLimits(update);
/*     */     }
/*     */     else {
/* 345 */       this.hadAdjustmentLastInterval = false;
/*     */       
/*     */ 
/* 348 */       int currUploadLimit = this.adapter.getCurrentUploadLimit();
/* 349 */       int currDownloadLimit = this.adapter.getCurrentDownloadLimit();
/* 350 */       if (!this.limitMonitor.areSettingsInSpec(currUploadLimit, currDownloadLimit)) {
/* 351 */         SMUpdate update = this.limitMonitor.adjustLimitsToSpec(currUploadLimit, currDownloadLimit);
/* 352 */         logNewLimits(update);
/* 353 */         setNewLimits(update);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 359 */     this.pingSourceManager.checkPingSources(sources);
/*     */   }
/*     */   
/*     */   private void endLimitTesting() {
/* 363 */     int downLimitGuess = this.limitMonitor.guessDownloadLimit();
/* 364 */     int upLimitGuess = this.limitMonitor.guessUploadLimit();
/*     */     
/* 366 */     SMUpdate update = this.limitMonitor.endLimitTesting(downLimitGuess, upLimitGuess);
/*     */     
/*     */ 
/*     */ 
/* 370 */     this.limitMonitor.logPingMapData();
/*     */     
/*     */ 
/* 373 */     this.limitMonitor.resetPingSpace();
/*     */     
/*     */ 
/* 376 */     logNewLimits(update);
/*     */     
/* 378 */     setNewLimits(update);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void logLimitStatus()
/*     */   {
/* 388 */     StringBuilder msg = new StringBuilder();
/* 389 */     msg.append("limits:");
/* 390 */     msg.append(this.limitMonitor.getUploadMaxLimit()).append(":");
/* 391 */     msg.append(this.limitMonitor.getUploadMinLimit()).append(":");
/* 392 */     msg.append(this.limitMonitor.getUploadConfidence()).append(":");
/* 393 */     msg.append(this.limitMonitor.getDownloadMaxLimit()).append(":");
/* 394 */     msg.append(this.limitMonitor.getDownloadMinLimit()).append(":");
/* 395 */     msg.append(this.limitMonitor.getDownloadConfidence());
/*     */     
/* 397 */     SpeedManagerLogger.log(msg.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean calculateMediaDHTPingTime()
/*     */   {
/* 406 */     if ((skipIntervalAfterAdjustment) && (this.hadAdjustmentLastInterval)) {
/* 407 */       this.hadAdjustmentLastInterval = false;
/* 408 */       this.pingTimeList = new ArrayList();
/* 409 */       this.intervalCount = 0;
/* 410 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 414 */     if (this.intervalCount < numIntervalsBetweenCal)
/*     */     {
/* 416 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 420 */     Collections.sort(this.pingTimeList);
/*     */     
/*     */ 
/*     */ 
/* 424 */     if (this.pingTimeList.size() == 0) {
/* 425 */       this.lastMetricValue = 10000;
/*     */     } else {
/* 427 */       int medianIndex = this.pingTimeList.size() / 2;
/*     */       
/* 429 */       Integer medianPingTime = (Integer)this.pingTimeList.get(medianIndex);
/* 430 */       this.lastMetricValue = medianPingTime.intValue();
/*     */     }
/*     */     
/*     */ 
/* 434 */     this.intervalCount = 0;
/* 435 */     this.pingTimeList = new ArrayList();
/* 436 */     return false;
/*     */   }
/*     */   
/*     */   private void logNewLimits(SMUpdate update) {
/* 440 */     if (update.hasNewUploadLimit) {
/* 441 */       int kbpsUpoadLimit = update.newUploadLimit / 1024;
/* 442 */       log(" new up limit  : " + kbpsUpoadLimit + " kb/s");
/*     */     }
/*     */     
/* 445 */     if (update.hasNewDownloadLimit) {
/* 446 */       int kpbsDownloadLimit = update.newDownloadLimit / 1024;
/* 447 */       log(" new down limit: " + kpbsDownloadLimit + " kb/s");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setNewLimits(SMUpdate update)
/*     */   {
/* 457 */     this.adapter.setCurrentUploadLimit(update.newUploadLimit);
/* 458 */     this.adapter.setCurrentDownloadLimit(update.newDownloadLimit);
/*     */   }
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
/*     */   private float determineSignalStrength(int currMetricValue)
/*     */   {
/* 472 */     float signal = 0.0F;
/* 473 */     if (currMetricValue < metricGoodResult)
/*     */     {
/* 475 */       signal = 1.0F;
/* 476 */       this.consecutiveUpticks += 1;
/* 477 */       this.consecutiveDownticks = 0;
/*     */     }
/* 479 */     else if (currMetricValue < metricGoodResult + metricGoodTolerance)
/*     */     {
/* 481 */       signal = (currMetricValue - metricGoodResult) / metricGoodTolerance;
/*     */       
/* 483 */       this.consecutiveUpticks += 1;
/* 484 */       this.consecutiveDownticks = 0;
/*     */     }
/* 486 */     else if (currMetricValue > metricBadResult)
/*     */     {
/* 488 */       signal = -1.0F;
/* 489 */       this.consecutiveUpticks = 0;
/* 490 */       this.consecutiveDownticks += 1;
/*     */     }
/* 492 */     else if (currMetricValue > metricBadResult - metricBadTolerance)
/*     */     {
/* 494 */       this.consecutiveUpticks = 0;
/* 495 */       this.consecutiveDownticks += 1;
/*     */       
/* 497 */       int lowerBound = metricBadResult - metricBadTolerance;
/* 498 */       signal = (currMetricValue - lowerBound) / metricBadTolerance;
/* 499 */       signal -= 1.0F;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 505 */     log("consecutive:" + this.consecutiveUpticks + ":" + this.consecutiveDownticks);
/*     */     
/* 507 */     return signal;
/*     */   }
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
/* 520 */     if (this.consecutiveUpticks > this.consecutiveDownticks)
/*     */     {
/*     */ 
/* 523 */       if (this.limitMonitor.bandwidthUsageLow()) {
/* 524 */         this.consecutiveUpticks = 0;
/*     */       }
/*     */       
/* 527 */       multiple = calculateUpTickMultiple(this.consecutiveUpticks);
/*     */     } else {
/* 529 */       multiple = calculateDownTickMultiple(this.consecutiveDownticks);
/* 530 */       this.limitMonitor.notifyOfDownSignal();
/*     */     }
/*     */     
/* 533 */     return multiple;
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
/* 545 */     float multiple = 0.0F;
/*     */     
/* 547 */     if (c < 0) {
/* 548 */       return multiple;
/*     */     }
/*     */     
/* 551 */     switch (c) {
/*     */     case 0: 
/*     */     case 1: 
/* 554 */       multiple = 0.25F;
/* 555 */       break;
/*     */     case 2: 
/* 557 */       multiple = 0.5F;
/* 558 */       break;
/*     */     case 3: 
/* 560 */       multiple = 1.0F;
/* 561 */       break;
/*     */     case 4: 
/* 563 */       multiple = 1.25F;
/* 564 */       break;
/*     */     case 5: 
/* 566 */       multiple = 1.5F;
/* 567 */       break;
/*     */     case 6: 
/* 569 */       multiple = 1.75F;
/* 570 */       break;
/*     */     case 7: 
/* 572 */       multiple = 2.0F;
/* 573 */       break;
/*     */     case 8: 
/* 575 */       multiple = 2.25F;
/* 576 */       break;
/*     */     case 9: 
/* 578 */       multiple = 2.5F;
/* 579 */       break;
/*     */     default: 
/* 581 */       multiple = 3.0F;
/*     */     }
/*     */     
/*     */     
/* 585 */     if (this.limitMonitor.bandwidthUsageMedium()) {
/* 586 */       multiple /= 2.0F;
/*     */     }
/*     */     
/* 589 */     return multiple;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private float calculateDownTickMultiple(int c)
/*     */   {
/* 599 */     float multiple = 0.0F;
/* 600 */     if (c < 0) {
/* 601 */       return multiple;
/*     */     }
/*     */     
/* 604 */     switch (c) {
/*     */     case 0: 
/*     */     case 1: 
/* 607 */       multiple = 0.25F;
/* 608 */       break;
/*     */     case 2: 
/* 610 */       multiple = 0.5F;
/* 611 */       break;
/*     */     case 3: 
/* 613 */       multiple = 1.0F;
/* 614 */       break;
/*     */     case 4: 
/* 616 */       multiple = 2.0F;
/* 617 */       break;
/*     */     case 5: 
/* 619 */       multiple = 3.0F;
/* 620 */       break;
/*     */     case 6: 
/* 622 */       multiple = 4.0F;
/* 623 */       break;
/*     */     case 7: 
/* 625 */       multiple = 6.0F;
/* 626 */       break;
/*     */     case 8: 
/* 628 */       multiple = 9.0F;
/* 629 */       break;
/*     */     case 9: 
/* 631 */       multiple = 15.0F;
/* 632 */       break;
/*     */     default: 
/* 634 */       multiple = 20.0F;
/*     */     }
/* 636 */     return multiple;
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
/* 648 */     return this.lastMetricValue;
/*     */   }
/*     */   
/*     */   public int getCurrentPingMillis()
/*     */   {
/* 653 */     return 0;
/*     */   }
/*     */   
/*     */   public int getMaxPingMillis() {
/* 657 */     return 912;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getCurrentChokeSpeed()
/*     */   {
/* 667 */     return 0;
/*     */   }
/*     */   
/*     */   public int getMaxUploadSpeed() {
/* 671 */     return this.sessionMaxUploadRate;
/*     */   }
/*     */   
/*     */   public boolean getAdjustsDownloadLimits()
/*     */   {
/* 676 */     return true;
/*     */   }
/*     */   
/*     */   protected void log(String str) {
/* 680 */     SpeedManagerLogger.log(str);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SpeedManagerAlgorithmProviderDHTPing.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */