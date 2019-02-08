/*     */ package org.gudy.azureus2.core3.stats.transfer.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.util.Date;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.stats.transfer.LongTermStats;
/*     */ import org.gudy.azureus2.core3.stats.transfer.LongTermStats.GenericStatsSource;
/*     */ import org.gudy.azureus2.core3.stats.transfer.LongTermStats.RecordAccepter;
/*     */ import org.gudy.azureus2.core3.stats.transfer.LongTermStatsListener;
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
/*     */ public class LongTermStatsWrapper
/*     */   implements LongTermStats
/*     */ {
/*     */   private AzureusCore core;
/*     */   private GlobalManagerStats gm_stats;
/*     */   private String id;
/*     */   private LongTermStats.GenericStatsSource source;
/*     */   private LongTermStatsWrapperHelper delegate;
/*  45 */   private final Map<LongTermStatsListener, Long> listeners = new IdentityHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public LongTermStatsWrapper(AzureusCore _core, GlobalManagerStats _stats)
/*     */   {
/*  52 */     this.core = _core;
/*  53 */     this.gm_stats = _stats;
/*     */     
/*  55 */     this.delegate = new LongTermStatsImpl(this.core, this.gm_stats);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public LongTermStatsWrapper(String _id, LongTermStats.GenericStatsSource _source)
/*     */   {
/*  63 */     this.id = _id;
/*  64 */     this.source = _source;
/*     */     
/*  66 */     this.delegate = new LongTermStatsGenericImpl(this.id, this.source);
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized boolean isEnabled()
/*     */   {
/*  72 */     return this.delegate.isEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized long[] getCurrentRateBytesPerSecond()
/*     */   {
/*  78 */     return this.delegate.getCurrentRateBytesPerSecond();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized long[] getTotalUsageInPeriod(Date start_date, Date end_date)
/*     */   {
/*  86 */     return this.delegate.getTotalUsageInPeriod(start_date, end_date);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized long[] getTotalUsageInPeriod(int period_type, double multiplier)
/*     */   {
/*  94 */     return this.delegate.getTotalUsageInPeriod(period_type, multiplier);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized long[] getTotalUsageInPeriod(int period_type, double multiplier, LongTermStats.RecordAccepter accepter)
/*     */   {
/* 103 */     return this.delegate.getTotalUsageInPeriod(period_type, multiplier, accepter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void addListener(long min_delta_bytes, LongTermStatsListener listener)
/*     */   {
/* 111 */     this.listeners.put(listener, Long.valueOf(min_delta_bytes));
/*     */     
/* 113 */     this.delegate.addListener(min_delta_bytes, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void removeListener(LongTermStatsListener listener)
/*     */   {
/* 120 */     this.listeners.remove(listener);
/*     */     
/* 122 */     this.delegate.removeListener(listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 128 */     this.delegate.destroyAndDeleteData();
/*     */     
/* 130 */     if (this.core != null)
/*     */     {
/* 132 */       this.delegate = new LongTermStatsImpl(this.core, this.gm_stats);
/*     */     }
/*     */     else
/*     */     {
/* 136 */       this.delegate = new LongTermStatsGenericImpl(this.id, this.source);
/*     */     }
/*     */     
/* 139 */     for (Map.Entry<LongTermStatsListener, Long> entry : this.listeners.entrySet())
/*     */     {
/* 141 */       this.delegate.addListener(((Long)entry.getValue()).longValue(), (LongTermStatsListener)entry.getKey());
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface LongTermStatsWrapperHelper
/*     */     extends LongTermStats
/*     */   {
/*     */     public abstract void destroyAndDeleteData();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/transfer/impl/LongTermStatsWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */