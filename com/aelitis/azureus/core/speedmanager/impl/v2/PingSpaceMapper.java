/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PingSpaceMapper
/*     */ {
/*     */   GridRegion[][] gridRegion;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int lastDownloadBitsPerSec;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int lastUploadBitsPerSec;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final int goodPingInMilliSec;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final int badPingInMilliSec;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  49 */   int totalPointsInMap = 0;
/*     */   
/*     */   private static final int maxMeshIndex = 70;
/*     */   
/*     */   public static final int RESULT_UPLOAD_INDEX = 0;
/*     */   public static final int RESULT_DOWNLOAD_INDEX = 1;
/*     */   static final int GOOD_PING_INDEX = 0;
/*     */   static final int ANY_PING_INDEX = 1;
/*     */   
/*     */   public PingSpaceMapper(int _goodPingInMilliSec, int _badPingInMilliSec)
/*     */   {
/*  60 */     createNewGrid();
/*     */     
/*  62 */     this.goodPingInMilliSec = _goodPingInMilliSec;
/*  63 */     this.badPingInMilliSec = _badPingInMilliSec;
/*     */   }
/*     */   
/*     */ 
/*     */   private void createNewGrid()
/*     */   {
/*  69 */     this.gridRegion = ((GridRegion[][])null);
/*  70 */     this.gridRegion = new GridRegion[70][70];
/*  71 */     for (int upIndex = 0; upIndex < 70; upIndex++) {
/*  72 */       for (int downIndex = 0; downIndex < 70; downIndex++) {
/*  73 */         this.gridRegion[upIndex][downIndex] = new GridRegion();
/*     */       }
/*     */     }
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
/*     */   private int convertBitsPerSec2meshIndex(int bitsPerSec)
/*     */   {
/*  89 */     if (bitsPerSec < 0) {
/*  90 */       return 0;
/*     */     }
/*     */     
/*  93 */     int bytesPerSec = bitsPerSec / 1024;
/*     */     
/*  95 */     if (bytesPerSec < 100)
/*  96 */       return bytesPerSec / 10;
/*  97 */     if (bytesPerSec < 500)
/*  98 */       return 10 + (bytesPerSec - 100) / 50;
/*  99 */     if (bytesPerSec < 5000) {
/* 100 */       return 18 + (bytesPerSec - 500) / 100;
/*     */     }
/*     */     
/*     */ 
/* 104 */     return 63;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int convertMeshIndex2bitsPerSec(int meshIndex)
/*     */   {
/* 114 */     int bytesPerSec = 0;
/* 115 */     if (meshIndex <= 0) {
/* 116 */       return 0;
/*     */     }
/*     */     
/* 119 */     if (meshIndex <= 10) {
/* 120 */       bytesPerSec = meshIndex * 10;
/* 121 */     } else if (meshIndex <= 18) {
/* 122 */       bytesPerSec = 100 + (meshIndex - 10) * 50;
/*     */     } else {
/* 124 */       bytesPerSec = 500 + (meshIndex - 18) * 100;
/*     */     }
/*     */     
/* 127 */     return bytesPerSec * 1024;
/*     */   }
/*     */   
/*     */   public void setCurrentTransferRates(int downloadBitPerSec, int uploadBitsPerSec) {
/* 131 */     this.lastDownloadBitsPerSec = downloadBitPerSec;
/* 132 */     this.lastUploadBitsPerSec = uploadBitsPerSec;
/*     */   }
/*     */   
/*     */   public void addMetricToMap(int metric)
/*     */   {
/* 137 */     int downIndex = convertBitsPerSec2meshIndex(this.lastDownloadBitsPerSec);
/* 138 */     int upIndex = convertBitsPerSec2meshIndex(this.lastUploadBitsPerSec);
/*     */     
/* 140 */     this.totalPointsInMap += 1;
/*     */     
/* 142 */     if (metric < this.goodPingInMilliSec) {
/* 143 */       this.gridRegion[upIndex][downIndex].incrementMetricCount(0);
/* 144 */     } else if (metric < this.badPingInMilliSec) {
/* 145 */       this.gridRegion[upIndex][downIndex].incrementMetricCount(1);
/*     */     } else {
/* 147 */       this.gridRegion[upIndex][downIndex].incrementMetricCount(2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 156 */     this.totalPointsInMap = 0;
/* 157 */     createNewGrid();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Result getHighestMeshIndexWithGoodPing()
/*     */   {
/* 164 */     Result[] retVal = calculate();
/* 165 */     return retVal[0];
/*     */   }
/*     */   
/*     */   private Result getHighestMeshIndexWithAnyPing()
/*     */   {
/* 170 */     Result[] retVal = calculate();
/* 171 */     return retVal[1];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hadChockingPing(boolean isDownloadTest)
/*     */   {
/* 181 */     Result[] res = calculate();
/*     */     
/*     */     int highPingIndex;
/*     */     int goodPingIndex;
/*     */     int highPingIndex;
/* 186 */     if (isDownloadTest) {
/* 187 */       int goodPingIndex = res[0].getDownloadIndex();
/* 188 */       highPingIndex = res[1].getDownloadIndex();
/*     */     } else {
/* 190 */       goodPingIndex = res[0].getUploadIndex();
/* 191 */       highPingIndex = res[1].getUploadIndex();
/*     */     }
/*     */     
/* 194 */     return highPingIndex > goodPingIndex;
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
/*     */   private Result[] calculate()
/*     */   {
/* 207 */     Result[] retVal = new Result[2];
/* 208 */     retVal[0] = new Result();
/* 209 */     retVal[1] = new Result();
/*     */     
/* 211 */     for (int upIndex = 0; upIndex < 70; upIndex++) {
/* 212 */       for (int downIndex = 0; downIndex < 70; downIndex++)
/*     */       {
/*     */ 
/* 215 */         float rating = this.gridRegion[upIndex][downIndex].getRating();
/* 216 */         if (rating > 0.0F) {
/* 217 */           retVal[0].checkAndUpdate(upIndex, downIndex);
/*     */         }
/*     */         
/*     */ 
/* 221 */         int count = this.gridRegion[upIndex][downIndex].getTotal();
/* 222 */         if (count > 0) {
/* 223 */           retVal[1].checkAndUpdate(upIndex, downIndex);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 229 */     return retVal;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int guessUploadLimit()
/*     */   {
/* 238 */     Result result = getHighestMeshIndexWithGoodPing();
/* 239 */     int upMeshIndex = result.getUploadIndex();
/* 240 */     return convertMeshIndex2bitsPerSec(upMeshIndex);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int guessDownloadLimit()
/*     */   {
/* 249 */     Result result = getHighestMeshIndexWithGoodPing();
/* 250 */     int downMeshIndex = result.getDownloadIndex();
/* 251 */     return convertMeshIndex2bitsPerSec(downMeshIndex);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static class Result
/*     */   {
/* 258 */     int highestUploadIndex = 0;
/* 259 */     int highestDownloadIndex = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void checkAndUpdate(int uploadIndex, int downloadIndex)
/*     */     {
/* 268 */       if (uploadIndex > this.highestUploadIndex) {
/* 269 */         this.highestUploadIndex = uploadIndex;
/*     */       }
/*     */       
/* 272 */       if (downloadIndex > this.highestDownloadIndex) {
/* 273 */         this.highestDownloadIndex = downloadIndex;
/*     */       }
/*     */     }
/*     */     
/*     */     public int getUploadIndex() {
/* 278 */       return this.highestUploadIndex;
/*     */     }
/*     */     
/*     */     public int getDownloadIndex() {
/* 282 */       return this.highestDownloadIndex;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static class GridRegion
/*     */   {
/*     */     public static final int INDEX_PING_GOOD = 0;
/*     */     
/*     */     public static final int INDEX_PING_NEUTRAL = 1;
/*     */     
/*     */     public static final int INDEX_PING_BAD = 2;
/*     */     
/* 295 */     final int[] pingCount = new int[3];
/* 296 */     int[] uploadBound = new int[2];
/* 297 */     int[] downloadBound = new int[2];
/*     */     
/*     */     public void incrementMetricCount(int pingIndex) {
/* 300 */       if ((pingIndex >= 0) && (pingIndex <= 3)) {
/* 301 */         this.pingCount[pingIndex] += 1;
/*     */       }
/*     */     }
/*     */     
/*     */     public float getRating()
/*     */     {
/* 307 */       int total = getTotal();
/*     */       
/* 309 */       if (total == 0) {
/* 310 */         return 0.0F;
/*     */       }
/*     */       
/* 313 */       float score = this.pingCount[0] + 0.3F * this.pingCount[1] - this.pingCount[2];
/*     */       
/*     */ 
/*     */ 
/* 317 */       return score / total;
/*     */     }
/*     */     
/*     */     public int getTotal()
/*     */     {
/* 322 */       return this.pingCount[0] + this.pingCount[1] + this.pingCount[2];
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/PingSpaceMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */