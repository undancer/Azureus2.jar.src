/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*     */ public class SMConfigurationAdapterImpl
/*     */   implements SMConfigurationAdapter
/*     */ {
/*     */   public SpeedManagerLimitEstimate getUploadLimit()
/*     */   {
/*  32 */     int upMax = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit");
/*     */     
/*  34 */     SpeedLimitConfidence upConf = SpeedLimitConfidence.parseString(COConfigurationManager.getStringParameter("SpeedLimitMonitor.setting.upload.limit.conf"));
/*     */     
/*     */ 
/*  37 */     return new SMConfigLimitEstimate(upMax, upConf);
/*     */   }
/*     */   
/*     */   public SpeedManagerLimitEstimate getDownloadLimit()
/*     */   {
/*  42 */     int upMax = COConfigurationManager.getIntParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit");
/*     */     
/*  44 */     SpeedLimitConfidence upConf = SpeedLimitConfidence.parseString(COConfigurationManager.getStringParameter("SpeedLimitMonitor.setting.download.limit.conf"));
/*     */     
/*     */ 
/*  47 */     return new SMConfigLimitEstimate(upMax, upConf);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUploadLimit(SpeedManagerLimitEstimate est) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDownloadLimit(SpeedManagerLimitEstimate est) {}
/*     */   
/*     */ 
/*     */   static class SMConfigLimitEstimate
/*     */     implements SpeedManagerLimitEstimate
/*     */   {
/*     */     final int bytesPerSec;
/*     */     
/*     */     final float limitEstimateType;
/*     */     
/*     */ 
/*     */     public SMConfigLimitEstimate(int rateInBytesPerSec, SpeedLimitConfidence conf)
/*     */     {
/*  69 */       this.bytesPerSec = rateInBytesPerSec;
/*  70 */       this.limitEstimateType = conf.asEstimateType();
/*     */     }
/*     */     
/*     */     public int getBytesPerSec()
/*     */     {
/*  75 */       return this.bytesPerSec;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public float getEstimateType()
/*     */     {
/*  85 */       return this.limitEstimateType;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public float getMetricRating()
/*     */     {
/*  98 */       return 0.0F;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public int[][] getSegments()
/*     */     {
/* 106 */       return new int[0][];
/*     */     }
/*     */     
/*     */     public long getWhen() {
/* 110 */       return 0L;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getString()
/*     */     {
/* 117 */       StringBuilder sb = new StringBuilder("estiamte: ");
/* 118 */       sb.append(this.bytesPerSec);
/* 119 */       sb.append(" (").append(this.limitEstimateType).append(") ");
/*     */       
/* 121 */       return sb.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SMConfigurationAdapterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */