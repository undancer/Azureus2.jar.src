/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
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
/*     */ public class ByteBucketST
/*     */   implements ByteBucket
/*     */ {
/*     */   private int rate;
/*     */   private int burst_rate;
/*     */   private long avail_bytes;
/*     */   private long prev_update_time;
/*     */   private boolean frozen;
/*     */   
/*     */   public ByteBucketST(int rate_bytes_per_sec)
/*     */   {
/*  45 */     this(rate_bytes_per_sec, rate_bytes_per_sec + rate_bytes_per_sec / 5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ByteBucketST(int rate_bytes_per_sec, int burst_rate)
/*     */   {
/*  55 */     this.rate = rate_bytes_per_sec;
/*  56 */     this.burst_rate = burst_rate;
/*  57 */     this.avail_bytes = 0L;
/*  58 */     this.prev_update_time = SystemTime.getSteppedMonotonousTime();
/*  59 */     ensureByteBucketMinBurstRate();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getAvailableByteCount()
/*     */   {
/*  68 */     if (this.avail_bytes < 104857600L) {
/*  69 */       update_avail_byte_count();
/*     */     }
/*     */     
/*  72 */     return (int)this.avail_bytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBytesUsed(int bytes_used)
/*     */   {
/*  81 */     if (this.avail_bytes >= 104857600L) {
/*  82 */       return;
/*     */     }
/*     */     
/*  85 */     this.avail_bytes -= bytes_used;
/*  86 */     if (this.avail_bytes < 0L) {
/*  87 */       this.avail_bytes = 0L;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getRate()
/*     */   {
/*  98 */     return this.rate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getBurstRate()
/*     */   {
/* 105 */     return this.burst_rate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRate(int rate_bytes_per_sec)
/*     */   {
/* 113 */     setRate(rate_bytes_per_sec, rate_bytes_per_sec + rate_bytes_per_sec / 5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRate(int rate_bytes_per_sec, int burst_rate)
/*     */   {
/* 123 */     if (rate_bytes_per_sec < 0) {
/* 124 */       Debug.out("rate_bytes_per_sec [" + rate_bytes_per_sec + "] < 0");
/* 125 */       rate_bytes_per_sec = 0;
/*     */     }
/* 127 */     if (burst_rate < rate_bytes_per_sec) {
/* 128 */       Debug.out("burst_rate [" + burst_rate + "] < rate_bytes_per_sec [" + rate_bytes_per_sec + "]");
/* 129 */       burst_rate = rate_bytes_per_sec;
/*     */     }
/* 131 */     this.rate = rate_bytes_per_sec;
/* 132 */     this.burst_rate = burst_rate;
/* 133 */     if (this.avail_bytes > burst_rate) {
/* 134 */       this.avail_bytes = burst_rate;
/*     */     }
/* 136 */     ensureByteBucketMinBurstRate();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFrozen(boolean f)
/*     */   {
/* 143 */     if ((f) && (this.frozen)) {
/* 144 */       Debug.out("Already frozen!");
/*     */     }
/* 146 */     this.frozen = f;
/*     */   }
/*     */   
/*     */   private void update_avail_byte_count() {
/* 150 */     if (this.frozen) {
/* 151 */       return;
/*     */     }
/* 153 */     long now = SystemTime.getSteppedMonotonousTime();
/* 154 */     if (this.prev_update_time < now) {
/* 155 */       this.avail_bytes += (now - this.prev_update_time) * this.rate / 1000L;
/* 156 */       this.prev_update_time = now;
/* 157 */       if (this.avail_bytes > this.burst_rate) { this.avail_bytes = this.burst_rate;
/* 158 */       } else if (this.avail_bytes < 0L) { Debug.out("ERROR: avail_bytes < 0: " + this.avail_bytes);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void ensureByteBucketMinBurstRate()
/*     */   {
/* 168 */     int mss = NetworkManager.getMinMssSize();
/* 169 */     if (this.burst_rate < mss) {
/* 170 */       this.burst_rate = mss;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/ByteBucketST.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */