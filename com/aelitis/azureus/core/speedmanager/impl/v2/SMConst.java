/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
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
/*     */ public class SMConst
/*     */ {
/*     */   public static final int START_DOWNLOAD_RATE_MAX = 61440;
/*     */   public static final int START_UPLOAD_RATE_MAX = 30720;
/*     */   public static final int MIN_UPLOAD_BYTES_PER_SEC = 5120;
/*     */   public static final int MIN_DOWNLOAD_BYTES_PER_SEC = 20480;
/*     */   public static final int RATE_UNLIMITED = 0;
/*     */   
/*     */   public static int checkForMinUploadValue(int rateBytesPerSec)
/*     */   {
/*  45 */     if (rateBytesPerSec < 5120) {
/*  46 */       return 5120;
/*     */     }
/*  48 */     return rateBytesPerSec;
/*     */   }
/*     */   
/*     */   public static int checkForMinDownloadValue(int rateBytesPerSec) {
/*  52 */     if (rateBytesPerSec < 20480) {
/*  53 */       return 20480;
/*     */     }
/*  55 */     return rateBytesPerSec;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int calculateMinUpload(int maxBytesPerSec)
/*     */   {
/*  65 */     int min = maxBytesPerSec / 10;
/*  66 */     return checkForMinUploadValue(min);
/*     */   }
/*     */   
/*     */   public static int calculateMinDownload(int maxBytesPerSec) {
/*  70 */     int min = maxBytesPerSec / 10;
/*  71 */     return checkForMinDownloadValue(min);
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
/*     */   public static SpeedManagerLimitEstimate filterEstimate(SpeedManagerLimitEstimate estimate, int startValue)
/*     */   {
/*  85 */     int estBytesPerSec = filterLimit(estimate.getBytesPerSec(), startValue);
/*     */     
/*  87 */     return new FilteredLimitEstimate(estBytesPerSec, estimate.getEstimateType(), estimate.getMetricRating(), estimate.getWhen(), estimate.getString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int filterLimit(int bytesPerSec, int startValue)
/*     */   {
/*  97 */     int retVal = Math.max(bytesPerSec, startValue);
/*     */     
/*     */ 
/* 100 */     if (bytesPerSec == 0) {
/* 101 */       return bytesPerSec;
/*     */     }
/*     */     
/* 104 */     return retVal;
/*     */   }
/*     */   
/*     */   static class FilteredLimitEstimate implements SpeedManagerLimitEstimate
/*     */   {
/*     */     final int bytesPerSec;
/*     */     final float type;
/*     */     final float metric;
/*     */     final long when;
/*     */     final String name;
/*     */     
/*     */     public FilteredLimitEstimate(int _bytesPerSec, float _type, float _metric, long _when, String _name) {
/* 116 */       this.bytesPerSec = _bytesPerSec;
/* 117 */       this.type = _type;
/* 118 */       this.metric = _metric;
/* 119 */       this.when = _when;
/* 120 */       this.name = _name;
/*     */     }
/*     */     
/*     */     public int getBytesPerSec() {
/* 124 */       return this.bytesPerSec;
/*     */     }
/*     */     
/*     */     public float getEstimateType() {
/* 128 */       return this.type;
/*     */     }
/*     */     
/* 131 */     public float getMetricRating() { return this.metric; }
/*     */     
/*     */     public int[][] getSegments()
/*     */     {
/* 135 */       return new int[0][];
/*     */     }
/*     */     
/* 138 */     public long getWhen() { return this.when; }
/*     */     
/*     */     public String getString() {
/* 141 */       return this.name;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SMConst.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */