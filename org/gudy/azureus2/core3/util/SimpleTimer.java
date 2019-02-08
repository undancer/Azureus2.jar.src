/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
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
/*     */ 
/*     */ 
/*     */ public class SimpleTimer
/*     */ {
/*     */   protected static final Timer timer;
/*  40 */   static final CopyOnWriteList<TimerTickReceiver> tick_receivers = new CopyOnWriteList(true);
/*     */   
/*     */   static {
/*  43 */     timer = new Timer("Simple Timer", 32);
/*     */     
/*  45 */     timer.setIndestructable();
/*     */     
/*  47 */     timer.setWarnWhenFull();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  53 */     addPeriodicEvent("SimpleTimer:ticker", 1000L, new TimerEventPerformer()
/*     */     {
/*     */       private int tick_count;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/*  64 */         this.tick_count += 1;
/*     */         long mono_now;
/*  66 */         if (SimpleTimer.tick_receivers.size() > 0)
/*     */         {
/*  68 */           mono_now = SystemTime.getMonotonousTime();
/*     */           
/*  70 */           for (SimpleTimer.TimerTickReceiver ttr : SimpleTimer.tick_receivers) {
/*     */             try
/*     */             {
/*  73 */               ttr.tick(mono_now, this.tick_count);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/*  77 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TimerEvent addEvent(String name, long when, TimerEventPerformer performer)
/*     */   {
/*  91 */     TimerEvent res = timer.addEvent(name, when, performer);
/*     */     
/*  93 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TimerEvent addEvent(String name, long when, boolean absolute, TimerEventPerformer performer)
/*     */   {
/* 103 */     TimerEvent res = timer.addEvent(name, when, absolute, performer);
/*     */     
/* 105 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TimerEventPeriodic addPeriodicEvent(String name, long frequency, TimerEventPerformer performer)
/*     */   {
/* 114 */     TimerEventPeriodic res = timer.addPeriodicEvent(name, frequency, performer);
/*     */     
/* 116 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static TimerEventPeriodic addPeriodicEvent(String name, long frequency, boolean absolute, TimerEventPerformer performer)
/*     */   {
/* 126 */     TimerEventPeriodic res = timer.addPeriodicEvent(name, frequency, absolute, performer);
/*     */     
/* 128 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addTickReceiver(TimerTickReceiver receiver)
/*     */   {
/* 135 */     tick_receivers.add(receiver);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeTickReceiver(TimerTickReceiver receiver)
/*     */   {
/* 142 */     tick_receivers.remove(receiver);
/*     */   }
/*     */   
/*     */   public static abstract interface TimerTickReceiver
/*     */   {
/*     */     public abstract void tick(long paramLong, int paramInt);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/SimpleTimer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */