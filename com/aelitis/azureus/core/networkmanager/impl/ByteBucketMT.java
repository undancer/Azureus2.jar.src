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
/*     */ public class ByteBucketMT
/*     */   implements ByteBucket
/*     */ {
/*     */   private int rate;
/*     */   private int burst_rate;
/*     */   private volatile long avail_bytes;
/*     */   private volatile long prev_update_time;
/*     */   private volatile boolean frozen;
/*     */   
/*     */   public ByteBucketMT(int rate_bytes_per_sec)
/*     */   {
/*  45 */     this(rate_bytes_per_sec, rate_bytes_per_sec + rate_bytes_per_sec / 5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ByteBucketMT(int rate_bytes_per_sec, int burst_rate)
/*     */   {
/*  55 */     this.rate = rate_bytes_per_sec;
/*  56 */     this.burst_rate = burst_rate;
/*  57 */     this.avail_bytes = 0L;
/*  58 */     this.prev_update_time = SystemTime.getMonotonousTime();
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
/*  72 */     int res = (int)this.avail_bytes;
/*     */     
/*  74 */     if (res < 0) {
/*  75 */       res = 0;
/*     */     }
/*  77 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBytesUsed(int bytes_used)
/*     */   {
/*  86 */     if (this.avail_bytes >= 104857600L) {
/*  87 */       return;
/*     */     }
/*     */     
/*  90 */     this.avail_bytes -= bytes_used;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getRate()
/*     */   {
/*  99 */     return this.rate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getBurstRate()
/*     */   {
/* 106 */     return this.burst_rate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRate(int rate_bytes_per_sec)
/*     */   {
/* 114 */     setRate(rate_bytes_per_sec, rate_bytes_per_sec + rate_bytes_per_sec / 5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFrozen(boolean f)
/*     */   {
/* 121 */     this.frozen = f;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRate(int rate_bytes_per_sec, int burst_rate)
/*     */   {
/* 130 */     if (rate_bytes_per_sec < 0) {
/* 131 */       Debug.out("rate_bytes_per_sec [" + rate_bytes_per_sec + "] < 0");
/* 132 */       rate_bytes_per_sec = 0;
/*     */     }
/* 134 */     if (burst_rate < rate_bytes_per_sec) {
/* 135 */       Debug.out("burst_rate [" + burst_rate + "] < rate_bytes_per_sec [" + rate_bytes_per_sec + "]");
/* 136 */       burst_rate = rate_bytes_per_sec;
/*     */     }
/* 138 */     this.rate = rate_bytes_per_sec;
/* 139 */     this.burst_rate = burst_rate;
/* 140 */     if (this.avail_bytes > burst_rate) {
/* 141 */       this.avail_bytes = burst_rate;
/*     */     }
/* 143 */     ensureByteBucketMinBurstRate();
/*     */   }
/*     */   
/*     */   private void update_avail_byte_count()
/*     */   {
/* 148 */     if (this.frozen) {
/* 149 */       return;
/*     */     }
/* 151 */     synchronized (this) {
/* 152 */       long now = SystemTime.getMonotonousTime();
/* 153 */       if (this.prev_update_time < now) {
/* 154 */         this.avail_bytes += (now - this.prev_update_time) * this.rate / 1000L;
/* 155 */         this.prev_update_time = now;
/* 156 */         if (this.avail_bytes > this.burst_rate) { this.avail_bytes = this.burst_rate;
/* 157 */         } else if (this.avail_bytes >= 0L) {}
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
/*     */   private void ensureByteBucketMinBurstRate()
/*     */   {
/* 170 */     int mss = NetworkManager.getMinMssSize();
/* 171 */     if (this.burst_rate < mss) {
/* 172 */       this.burst_rate = mss;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/ByteBucketMT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */