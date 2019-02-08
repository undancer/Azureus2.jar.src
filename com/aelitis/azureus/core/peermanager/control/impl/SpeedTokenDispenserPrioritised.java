/*     */ package com.aelitis.azureus.core.peermanager.control.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.control.SpeedTokenDispenser;
/*     */ import com.aelitis.azureus.core.util.FeatureAvailability;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class SpeedTokenDispenserPrioritised
/*     */   implements SpeedTokenDispenser
/*     */ {
/*     */   private int rateKiB;
/*     */   private long threshold;
/*     */   private long bucket;
/*     */   private long lastTime;
/*     */   private long currentTime;
/*     */   private static final int BUCKET_THRESHOLD_LOWER_BOUND = 32768;
/*     */   private static final int BUCKET_RESPONSE_TIME = 1;
/*     */   private static final int BUCKET_THRESHOLD_FACTOR = 1024;
/*     */   
/*     */   public SpeedTokenDispenserPrioritised()
/*     */   {
/*  36 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Max Download Speed KBs", "Use Request Limiting" }, new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/*  39 */         SpeedTokenDispenserPrioritised.this.rateKiB = COConfigurationManager.getIntParameter("Max Download Speed KBs");
/*  40 */         if ((!COConfigurationManager.getBooleanParameter("Use Request Limiting")) || (!FeatureAvailability.isRequestLimitingEnabled())) {
/*  41 */           SpeedTokenDispenserPrioritised.this.rateKiB = 0;
/*     */         }
/*     */         
/*  44 */         if (SpeedTokenDispenserPrioritised.this.rateKiB < 0) {
/*  45 */           SpeedTokenDispenserPrioritised.this.rateKiB = 0;
/*     */         }
/*     */         
/*  48 */         SpeedTokenDispenserPrioritised.this.threshold = Math.max(1024 * SpeedTokenDispenserPrioritised.this.rateKiB, 32768);
/*  49 */         SpeedTokenDispenserPrioritised.this.lastTime = (SpeedTokenDispenserPrioritised.this.currentTime - 1L);
/*  50 */         SpeedTokenDispenserPrioritised.this.refill();
/*     */       }
/*     */       
/*     */ 
/*  54 */     });
/*  55 */     this.bucket = 0L;
/*  56 */     this.lastTime = SystemTime.getCurrentTime();
/*     */   }
/*     */   
/*     */   public void update(long newTime) {
/*  60 */     this.currentTime = newTime;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void refill()
/*     */   {
/*  71 */     if ((this.lastTime == this.currentTime) || (this.rateKiB == 0)) {
/*  72 */       return;
/*     */     }
/*  74 */     if (this.lastTime > this.currentTime) {
/*  75 */       this.lastTime = this.currentTime;
/*  76 */       return;
/*     */     }
/*     */     
/*  79 */     if (this.bucket < 0L) {
/*  80 */       Debug.out("Bucket is more than empty! - " + this.bucket);
/*  81 */       this.bucket = 0L;
/*     */     }
/*  83 */     long delta = this.currentTime - this.lastTime;
/*  84 */     this.lastTime = this.currentTime;
/*     */     
/*     */ 
/*  87 */     long tickDelta = this.rateKiB * 1024L * delta / 1000L;
/*     */     
/*  89 */     this.bucket += tickDelta;
/*  90 */     if (this.bucket > this.threshold)
/*  91 */       this.bucket = this.threshold;
/*     */   }
/*     */   
/*     */   public int dispense(int numberOfChunks, int chunkSize) {
/*  95 */     if (this.rateKiB == 0)
/*  96 */       return numberOfChunks;
/*  97 */     if (chunkSize > this.bucket)
/*  98 */       return 0;
/*  99 */     if (chunkSize * numberOfChunks <= this.bucket)
/*     */     {
/* 101 */       this.bucket -= chunkSize * numberOfChunks;
/* 102 */       return numberOfChunks;
/*     */     }
/* 104 */     int availableChunks = (int)(this.bucket / chunkSize);
/* 105 */     this.bucket -= chunkSize * availableChunks;
/* 106 */     return availableChunks;
/*     */   }
/*     */   
/*     */   public void returnUnusedChunks(int unused, int chunkSize) {
/* 110 */     this.bucket += unused * chunkSize;
/*     */   }
/*     */   
/*     */   public int peek(int chunkSize) {
/* 114 */     if (this.rateKiB != 0) {
/* 115 */       return (int)(this.bucket / chunkSize);
/*     */     }
/* 117 */     return Integer.MAX_VALUE;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/control/impl/SpeedTokenDispenserPrioritised.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */