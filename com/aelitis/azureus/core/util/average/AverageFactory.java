/*     */ package com.aelitis.azureus.core.util.average;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.GeneralUtils;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer.TimerTickReceiver;
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
/*     */ public abstract class AverageFactory
/*     */ {
/*     */   public static RunningAverage RunningAverage()
/*     */   {
/*  37 */     return new RunningAverage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static MovingAverage MovingAverage(int periods)
/*     */   {
/*  44 */     return new MovingAverage(periods);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static MovingImmediateAverage MovingImmediateAverage(int periods)
/*     */   {
/*  53 */     return new MovingImmediateAverage(periods);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static ExponentialMovingAverage ExponentialMovingAverage(int periods)
/*     */   {
/*  60 */     return new ExponentialMovingAverage(periods);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ExponentialMovingAverage ExponentialMovingAverage(float weight)
/*     */   {
/*  70 */     return new ExponentialMovingAverage(weight);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> long LazySmoothMovingImmediateAverage(LazyMovingImmediateAverageAdapter<T> adapter, T instance)
/*     */   {
/*  87 */     int update_window = GeneralUtils.getSmoothUpdateWindow();
/*  88 */     int update_interval = GeneralUtils.getSmoothUpdateInterval();
/*     */     
/*  90 */     return LazyMovingImmediateAverage(update_window / update_interval, update_interval, adapter, instance);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> long LazyMovingImmediateAverage(final int periods, final int interval_secs, final LazyMovingImmediateAverageAdapter<T> adapter, final T instance)
/*     */   {
/* 100 */     LazyMovingImmediateAverageState current = adapter.getCurrent(instance);
/*     */     
/* 102 */     if (current == null)
/*     */     {
/* 104 */       LazyMovingImmediateAverageState state = current = new LazyMovingImmediateAverageState();
/*     */       
/* 106 */       SimpleTimer.addTickReceiver(new SimpleTimer.TimerTickReceiver()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void tick(long mono_now, int tick_count)
/*     */         {
/*     */ 
/*     */ 
/* 114 */           long now = SystemTime.getMonotonousTime();
/*     */           
/* 116 */           if (now - AverageFactory.LazyMovingImmediateAverageState.access$000(this.val$state) > 60000L)
/*     */           {
/* 118 */             SimpleTimer.removeTickReceiver(this);
/*     */             
/* 120 */             adapter.setCurrent(instance, null);
/*     */           }
/* 122 */           else if (tick_count % interval_secs == 0)
/*     */           {
/* 124 */             long value = adapter.getValue(instance);
/*     */             
/* 126 */             long last = AverageFactory.LazyMovingImmediateAverageState.access$100(this.val$state);
/* 127 */             long diff = value - last;
/*     */             
/* 129 */             if ((last >= 0L) && (diff >= 0L))
/*     */             {
/* 131 */               MovingImmediateAverage average = AverageFactory.LazyMovingImmediateAverageState.access$200(this.val$state);
/*     */               
/* 133 */               if (diff == 0L)
/*     */               {
/* 135 */                 AverageFactory.LazyMovingImmediateAverageState.access$308(this.val$state);
/*     */               }
/*     */               else
/*     */               {
/* 139 */                 AverageFactory.LazyMovingImmediateAverageState.access$302(this.val$state, 0);
/*     */               }
/*     */               
/* 142 */               if (average == null)
/*     */               {
/* 144 */                 if (diff > 0L)
/*     */                 {
/* 146 */                   AverageFactory.LazyMovingImmediateAverageState.access$202(this.val$state, average = AverageFactory.MovingImmediateAverage(periods));
/*     */                   
/* 148 */                   int zeros_to_do = Math.min(AverageFactory.LazyMovingImmediateAverageState.access$300(this.val$state), periods);
/*     */                   
/* 150 */                   for (int i = 0; i < zeros_to_do; i++)
/*     */                   {
/* 152 */                     average.update(0.0D);
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/* 157 */               if (average != null)
/*     */               {
/* 159 */                 long ave = average.update(diff);
/*     */                 
/* 161 */                 if ((ave == 0L) && (average.getSampleCount() >= periods))
/*     */                 {
/*     */ 
/*     */ 
/* 165 */                   AverageFactory.LazyMovingImmediateAverageState.access$202(this.val$state, null);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 170 */             AverageFactory.LazyMovingImmediateAverageState.access$102(this.val$state, value);
/*     */           }
/*     */           
/*     */         }
/* 174 */       });
/* 175 */       adapter.setCurrent(instance, current);
/*     */     }
/*     */     else
/*     */     {
/* 179 */       current.last_read = SystemTime.getMonotonousTime();
/*     */     }
/*     */     
/* 182 */     MovingImmediateAverage average = current.average;
/*     */     
/* 184 */     if (average == null)
/*     */     {
/* 186 */       return 0L;
/*     */     }
/*     */     
/*     */ 
/* 190 */     return average.getAverage() / interval_secs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static abstract interface LazyMovingImmediateAverageAdapter<T>
/*     */   {
/*     */     public abstract AverageFactory.LazyMovingImmediateAverageState getCurrent(T paramT);
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract void setCurrent(T paramT, AverageFactory.LazyMovingImmediateAverageState paramLazyMovingImmediateAverageState);
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract long getValue(T paramT);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class LazyMovingImmediateAverageState
/*     */   {
/*     */     private MovingImmediateAverage average;
/*     */     
/*     */     private int consec_zeros;
/*     */     
/* 216 */     private long last_value = -1L;
/* 217 */     private long last_read = SystemTime.getMonotonousTime();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/average/AverageFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */