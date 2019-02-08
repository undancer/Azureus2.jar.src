/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
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
/*     */ public class PingSpaceMonitor
/*     */ {
/*     */   PingSpaceMapper pingMap;
/*     */   long startTime;
/*     */   private static final long INTERVAL = 300000L;
/*     */   final int maxGoodPing;
/*     */   final int minBadPing;
/*     */   int nBadPings;
/*     */   int nGoodPings;
/*  34 */   int nNeutralPings = 0;
/*     */   int upNone;
/*     */   int upLow;
/*  37 */   int upMed; int upHigh; int upAtLimit = 0;
/*  38 */   int downNone; int downLow; int downMed; int downHigh; int downAtLimit = 0;
/*     */   
/*     */   TransferMode transferMode;
/*     */   
/*     */   public static final int UPLOAD = 88;
/*     */   
/*     */   public static final int DOWNLOAD = 89;
/*     */   
/*     */   public static final int NONE = 0;
/*  47 */   boolean hasNewLimit = false;
/*  48 */   int newLimit = -2;
/*  49 */   int limitType = 0;
/*     */   
/*     */ 
/*     */   public PingSpaceMonitor(int _maxGoodPing, int _minBadPing, TransferMode mode)
/*     */   {
/*  54 */     this.maxGoodPing = _maxGoodPing;
/*  55 */     this.minBadPing = _minBadPing;
/*     */     
/*  57 */     reset(mode);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCurrentTransferRates(int downRate, int upRate)
/*     */   {
/*  66 */     this.pingMap.setCurrentTransferRates(downRate, upRate);
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
/*     */   public boolean addToPingMapData(int lastMetricValue, TransferMode mode)
/*     */   {
/*  79 */     if (this.transferMode == null) {
/*  80 */       this.transferMode = mode;
/*     */     }
/*     */     
/*     */ 
/*  84 */     if (!this.transferMode.equals(mode)) {
/*  85 */       reset(mode);
/*  86 */       this.transferMode = mode;
/*  87 */       return false;
/*     */     }
/*     */     
/*  90 */     this.transferMode = mode;
/*     */     
/*  92 */     if (lastMetricValue < this.maxGoodPing) {
/*  93 */       this.nGoodPings += 1;
/*  94 */     } else if (lastMetricValue > this.minBadPing) {
/*  95 */       this.nBadPings += 1;
/*     */     } else {
/*  97 */       this.nNeutralPings += 1;
/*     */     }
/*     */     
/* 100 */     this.pingMap.addMetricToMap(lastMetricValue);
/*     */     
/*     */ 
/* 103 */     long curr = SystemTime.getCurrentTime();
/*     */     
/* 105 */     if (curr > this.startTime + 300000L)
/*     */     {
/* 107 */       boolean needLowerLimts = checkForLowerLimits();
/* 108 */       if (needLowerLimts)
/*     */       {
/*     */ 
/* 111 */         if (this.transferMode.isConfTestingLimits()) {
/* 112 */           reset(mode);
/* 113 */           return false; }
/* 114 */         if (this.transferMode.isDownloadMode())
/*     */         {
/*     */ 
/* 117 */           this.newLimit = this.pingMap.guessDownloadLimit();
/*     */           
/* 119 */           SpeedManagerLogger.trace("PingSpaceMonitor -> guessDownloadLimit: newLimit=" + this.newLimit);
/*     */           
/*     */ 
/* 122 */           int uploadLimitGuess = this.pingMap.guessUploadLimit();
/* 123 */           SpeedManagerLogger.trace("PingSpaceMonitor -> guessUploadLimit: guessUploadLimit=" + uploadLimitGuess);
/*     */           
/*     */ 
/* 126 */           if (this.newLimit < 40960) {
/* 127 */             this.newLimit = 40960;
/*     */           }
/*     */           
/* 130 */           this.hasNewLimit = true;
/* 131 */           this.limitType = 89;
/* 132 */           reset(mode);
/* 133 */           return true;
/*     */         }
/*     */         
/*     */ 
/* 137 */         this.newLimit = this.pingMap.guessUploadLimit();
/*     */         
/*     */ 
/* 140 */         if (this.newLimit < 20480) {
/* 141 */           this.newLimit = 20480;
/*     */         }
/*     */         
/* 144 */         this.hasNewLimit = true;
/* 145 */         this.limitType = 88;
/* 146 */         reset(mode);
/* 147 */         return true;
/*     */       }
/*     */       
/*     */ 
/* 151 */       reset(mode);
/*     */     }
/*     */     
/*     */ 
/* 155 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean checkForLowerLimits()
/*     */   {
/* 162 */     int totalPings = this.nGoodPings + this.nBadPings + this.nNeutralPings;
/*     */     
/* 164 */     float percentBad = this.nBadPings / totalPings;
/*     */     
/* 166 */     if (percentBad > 0.15F) {
/* 167 */       return true;
/*     */     }
/* 169 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset(TransferMode mode)
/*     */   {
/* 177 */     StringBuilder sb = new StringBuilder("ping-monitor:");
/* 178 */     sb.append("good=").append(this.nGoodPings).append(":");
/* 179 */     sb.append("bad=").append(this.nBadPings).append(":");
/* 180 */     sb.append("neutral=").append(this.nNeutralPings);
/*     */     
/* 182 */     SpeedManagerLogger.log(sb.toString());
/*     */     
/*     */ 
/* 185 */     this.nBadPings = (this.nGoodPings = this.nNeutralPings = 0);
/*     */     
/*     */ 
/* 188 */     this.upNone = (this.upLow = this.upMed = this.upHigh = this.upAtLimit = 0);
/* 189 */     this.downNone = (this.downLow = this.downMed = this.downHigh = this.downAtLimit = 0);
/*     */     
/* 191 */     this.pingMap = new PingSpaceMapper(this.maxGoodPing, this.minBadPing);
/* 192 */     this.startTime = SystemTime.getCurrentTime();
/*     */     
/* 194 */     this.transferMode = mode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean hasNewLimit()
/*     */   {
/* 203 */     return this.hasNewLimit;
/*     */   }
/*     */   
/*     */   public int getNewLimit() {
/* 207 */     return this.newLimit;
/*     */   }
/*     */   
/*     */   public int limitType() {
/* 211 */     return this.limitType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void resetNewLimit()
/*     */   {
/* 218 */     this.hasNewLimit = false;
/* 219 */     this.newLimit = -2;
/* 220 */     this.limitType = 0;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/PingSpaceMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */