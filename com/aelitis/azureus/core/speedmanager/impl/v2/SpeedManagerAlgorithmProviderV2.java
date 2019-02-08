/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingSource;
/*     */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProvider;
/*     */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProviderAdapter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpeedManagerAlgorithmProviderV2
/*     */   implements SpeedManagerAlgorithmProvider
/*     */ {
/*     */   private final SpeedManagerAlgorithmProviderAdapter adapter;
/*     */   private final SpeedManagerAlgorithmProvider strategy;
/*     */   public static final String SETTING_DOWNLOAD_MAX_LIMIT = "SpeedManagerAlgorithmProviderV2.setting.download.max.limit";
/*     */   public static final String SETTING_UPLOAD_MAX_LIMIT = "SpeedManagerAlgorithmProviderV2.setting.upload.max.limit";
/*     */   public static final String SETTING_UPLOAD_LIMIT_ESTIMATE_TYPE_FROM_UI = "AutoSpeed Network Upload Speed Type (temp)";
/*     */   public static final String SETTING_DOWNLOAD_LIMIT_ESTIMATE_TYPE_FROM_UI = "AutoSpeed Network Download Speed Type (temp)";
/*     */   public static final String SETTING_DATA_SOURCE_INPUT = "SpeedManagerAlgorithmProviderV2.source.data.input";
/*     */   public static final String SETTING_DHT_GOOD_SET_POINT = "SpeedManagerAlgorithmProviderV2.setting.dht.good.setpoint";
/*     */   public static final String SETTING_DHT_GOOD_TOLERANCE = "SpeedManagerAlgorithmProviderV2.setting.dht.good.tolerance";
/*     */   public static final String SETTING_DHT_BAD_SET_POINT = "SpeedManagerAlgorithmProviderV2.setting.dht.bad.setpoint";
/*     */   public static final String SETTING_DHT_BAD_TOLERANCE = "SpeedManagerAlgorithmProviderV2.setting.dht.bad.tolerance";
/*     */   public static final String SETTING_WAIT_AFTER_ADJUST = "SpeedManagerAlgorithmProviderV2.setting.wait.after.adjust";
/*     */   public static final String SETTING_INTERVALS_BETWEEN_ADJUST = "SpeedManagerAlgorithmProviderV2.intervals.between.adjust";
/*     */   public static final String SETTING_V2_BETA_ENABLED = "SpeedManagerAlgorithmProviderV2.setting.beta.enabled";
/*     */   
/*     */   public SpeedManagerAlgorithmProviderV2(SpeedManagerAlgorithmProviderAdapter _adapter)
/*     */   {
/*  67 */     this.adapter = _adapter;
/*     */     
/*  69 */     SpeedManagerLogger.setAdapter("v2", this.adapter);
/*     */     
/*     */ 
/*  72 */     this.strategy = new SpeedManagerAlgorithmProviderPingMap(_adapter);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/*  78 */     this.strategy.destroy();
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset()
/*     */   {
/*  84 */     this.strategy.reset();
/*     */   }
/*     */   
/*     */ 
/*     */   public void updateStats()
/*     */   {
/*  90 */     this.strategy.updateStats();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pingSourceFound(SpeedManagerPingSource source, boolean is_replacement)
/*     */   {
/*  98 */     log("Found ping source: " + source.getAddress());
/*     */     
/* 100 */     this.strategy.pingSourceFound(source, is_replacement);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void pingSourceFailed(SpeedManagerPingSource source)
/*     */   {
/* 107 */     log("Lost ping source: " + source.getAddress());
/*     */     
/* 109 */     this.strategy.pingSourceFailed(source);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void calculate(SpeedManagerPingSource[] sources)
/*     */   {
/* 116 */     String str = "";
/*     */     
/* 118 */     for (int i = 0; i < sources.length; i++)
/*     */     {
/* 120 */       str = str + (i == 0 ? "" : ",") + sources[i].getAddress() + " -> " + sources[i].getPingTime();
/*     */     }
/*     */     
/* 123 */     log("ping-data: " + str);
/*     */     
/*     */ 
/* 126 */     this.strategy.calculate(sources);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIdlePingMillis()
/*     */   {
/* 132 */     return this.strategy.getIdlePingMillis();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCurrentPingMillis()
/*     */   {
/* 138 */     return this.strategy.getCurrentPingMillis();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxPingMillis()
/*     */   {
/* 144 */     return this.strategy.getMaxPingMillis();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCurrentChokeSpeed()
/*     */   {
/* 150 */     return this.strategy.getCurrentChokeSpeed();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxUploadSpeed()
/*     */   {
/* 156 */     return this.strategy.getMaxUploadSpeed();
/*     */   }
/*     */   
/*     */   public boolean getAdjustsDownloadLimits()
/*     */   {
/* 161 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 168 */     SpeedManagerLogger.log(str);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SpeedManagerAlgorithmProviderV2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */