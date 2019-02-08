/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingSource;
/*     */ import com.aelitis.azureus.core.util.average.Average;
/*     */ import com.aelitis.azureus.core.util.average.AverageFactory;
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
/*     */ public class PingSourceStats
/*     */ {
/*     */   final SpeedManagerPingSource source;
/*     */   double currPing;
/*  34 */   final Average shortTerm = AverageFactory.MovingImmediateAverage(3);
/*  35 */   final Average medTerm = AverageFactory.MovingImmediateAverage(6);
/*  36 */   final Average longTerm = AverageFactory.MovingImmediateAverage(10);
/*     */   
/*  38 */   final Average forChecks = AverageFactory.MovingImmediateAverage(100);
/*     */   
/*     */   public PingSourceStats(SpeedManagerPingSource _source) {
/*  41 */     this.source = _source;
/*     */   }
/*     */   
/*     */ 
/*     */   public void madeChange() {}
/*     */   
/*     */ 
/*     */   public void addPingTime(int ping)
/*     */   {
/*  50 */     this.currPing = ping;
/*  51 */     this.shortTerm.update(ping);
/*  52 */     this.medTerm.update(ping);
/*  53 */     this.longTerm.update(ping);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getTrend()
/*     */   {
/*  63 */     int retVal = 0;
/*     */     
/*     */ 
/*  66 */     if (this.currPing < 0.0D) {
/*  67 */       retVal--;
/*     */     } else {
/*  69 */       if (this.currPing < this.shortTerm.getAverage()) {
/*  70 */         retVal++;
/*     */       } else {
/*  72 */         retVal--;
/*     */       }
/*     */       
/*  75 */       if (this.currPing < this.medTerm.getAverage()) {
/*  76 */         retVal++;
/*     */       } else {
/*  78 */         retVal--;
/*     */       }
/*     */       
/*  81 */       if (this.currPing < this.longTerm.getAverage()) {
/*  82 */         retVal++;
/*     */       } else {
/*  84 */         retVal--;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  89 */     if (this.shortTerm.getAverage() < this.medTerm.getAverage()) {
/*  90 */       retVal++;
/*     */     } else {
/*  92 */       retVal--;
/*     */     }
/*     */     
/*     */ 
/*  96 */     if (this.shortTerm.getAverage() < this.longTerm.getAverage()) {
/*  97 */       retVal++;
/*     */     } else {
/*  99 */       retVal--;
/*     */     }
/*     */     
/*     */ 
/* 103 */     if (this.medTerm.getAverage() < this.longTerm.getAverage()) {
/* 104 */       retVal++;
/*     */     } else {
/* 106 */       retVal--;
/*     */     }
/*     */     
/*     */ 
/* 110 */     int ABSOLUTE_GOOD_PING_VALUE = 30;
/* 111 */     if (this.currPing < 30.0D) {
/* 112 */       retVal++;
/*     */     }
/* 114 */     if (this.shortTerm.getAverage() < 30.0D) {
/* 115 */       retVal++;
/*     */     }
/* 117 */     if (this.medTerm.getAverage() < 30.0D) {
/* 118 */       retVal++;
/*     */     }
/* 120 */     if (this.longTerm.getAverage() < 30.0D) {
/* 121 */       retVal++;
/*     */     }
/*     */     
/*     */ 
/* 125 */     int ABSOLUTE_BAD_PING_VALUE = 300;
/* 126 */     if (this.currPing > 300.0D) {
/* 127 */       retVal--;
/*     */     }
/* 129 */     if (this.shortTerm.getAverage() > 300.0D) {
/* 130 */       retVal--;
/*     */     }
/* 132 */     if (this.medTerm.getAverage() > 300.0D) {
/* 133 */       retVal--;
/*     */     }
/* 135 */     if (this.longTerm.getAverage() > 300.0D) {
/* 136 */       retVal--;
/*     */     }
/*     */     
/* 139 */     return retVal;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Average getLongTermAve()
/*     */   {
/* 147 */     return this.longTerm;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Average getHistory()
/*     */   {
/* 155 */     return this.forChecks;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/PingSourceStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */