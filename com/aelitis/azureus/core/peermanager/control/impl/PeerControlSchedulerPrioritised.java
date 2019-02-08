/*     */ package com.aelitis.azureus.core.peermanager.control.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.control.PeerControlInstance;
/*     */ import com.aelitis.azureus.core.peermanager.control.SpeedTokenDispenser;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.SystemTime.TickConsumer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PeerControlSchedulerPrioritised
/*     */   extends PeerControlSchedulerImpl
/*     */   implements AzureusCoreStatsProvider
/*     */ {
/*     */   private Map instance_map;
/*     */   final List pending_registrations;
/*     */   private volatile boolean registrations_changed;
/*     */   private volatile long latest_time;
/*     */   protected final AEMonitor this_mon;
/*     */   private final SpeedTokenDispenserPrioritised tokenDispenser;
/*     */   
/*     */   public PeerControlSchedulerPrioritised()
/*     */   {
/*  38 */     this.instance_map = new HashMap();
/*     */     
/*  40 */     this.pending_registrations = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  45 */     this.this_mon = new AEMonitor("PeerControlSchedulerPrioritised");
/*     */     
/*     */ 
/*  48 */     this.tokenDispenser = new SpeedTokenDispenserPrioritised();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void schedule()
/*     */   {
/*  55 */     this.latest_time = SystemTime.getMonotonousTime();
/*  56 */     SystemTime.registerMonotonousConsumer(new SystemTime.TickConsumer()
/*     */     {
/*     */ 
/*     */       public void consume(long time)
/*     */       {
/*     */ 
/*  62 */         synchronized (PeerControlSchedulerPrioritised.this) {
/*  63 */           PeerControlSchedulerPrioritised.this.latest_time = time;
/*  64 */           if ((PeerControlSchedulerPrioritised.this.instance_map.size() > 0) || (PeerControlSchedulerPrioritised.this.pending_registrations.size() > 0))
/*     */           {
/*  66 */             PeerControlSchedulerPrioritised.this.notify();
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*  72 */     });
/*  73 */     ArrayList instances = new ArrayList();
/*     */     
/*  75 */     long latest_time_used = 0L;
/*  76 */     int scheduledNext = 0;
/*  77 */     long currentScheduleStart = this.latest_time;
/*  78 */     long last_stats_time = this.latest_time;
/*     */     
/*     */     for (;;)
/*     */     {
/*  82 */       if (this.registrations_changed) {
/*     */         try {
/*  84 */           this.this_mon.enter();
/*  85 */           Iterator it = instances.iterator();
/*  86 */           while (it.hasNext()) {
/*  87 */             if (((instanceWrapper)it.next()).isUnregistered()) {
/*  88 */               it.remove();
/*     */             }
/*     */           }
/*     */           
/*  92 */           for (int i = 0; i < this.pending_registrations.size(); i++) {
/*  93 */             instances.add(this.pending_registrations.get(i));
/*     */           }
/*  95 */           this.pending_registrations.clear();
/*     */           
/*     */ 
/*  98 */           Collections.sort(instances);
/*     */           
/* 100 */           if (instances.size() > 0)
/*     */           {
/* 102 */             for (int i = 0; i < instances.size(); i++) {
/* 103 */               ((instanceWrapper)instances.get(i)).setScheduleOffset(SCHEDULE_PERIOD_MILLIS * i / instances.size());
/*     */             }
/*     */           }
/* 106 */           scheduledNext = 0;
/* 107 */           currentScheduleStart = this.latest_time;
/*     */           
/* 109 */           this.registrations_changed = false;
/*     */         } finally {
/* 111 */           this.this_mon.exit();
/*     */         }
/*     */       }
/*     */       
/* 115 */       this.tokenDispenser.update(this.latest_time);
/*     */       
/* 117 */       for (int i = scheduledNext; i < instances.size(); i++)
/*     */       {
/* 119 */         instanceWrapper inst = (instanceWrapper)instances.get(i);
/* 120 */         if (currentScheduleStart + inst.getScheduleOffset() > latest_time_used)
/*     */           break;
/* 122 */         if ((i == 0) || (!this.useWeights)) {
/* 123 */           this.tokenDispenser.refill();
/*     */         }
/* 125 */         inst.schedule();
/* 126 */         this.schedule_count += 1L;
/* 127 */         scheduledNext++;
/* 128 */         if (scheduledNext >= instances.size())
/*     */         {
/* 130 */           scheduledNext = 0;
/*     */           
/* 132 */           currentScheduleStart += SCHEDULE_PERIOD_MILLIS;
/*     */           
/*     */ 
/* 135 */           if (latest_time_used - currentScheduleStart > SCHEDULE_PERIOD_MAX_CATCHUP) {
/* 136 */             currentScheduleStart = latest_time_used + SCHEDULE_PERIOD_MILLIS;
/*     */           }
/*     */         }
/*     */       }
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
/* 158 */       synchronized (this) {
/* 159 */         if (this.latest_time == latest_time_used) {
/* 160 */           this.wait_count += 1L;
/*     */           try {
/* 162 */             long wait_start = SystemTime.getHighPrecisionCounter();
/* 163 */             wait(5000L);
/* 164 */             long wait_time = SystemTime.getHighPrecisionCounter() - wait_start;
/* 165 */             this.total_wait_time += wait_time;
/*     */           } catch (Throwable e) {
/* 167 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */         else {
/* 171 */           this.yield_count += 1L;
/* 172 */           Thread.yield();
/*     */         }
/*     */         
/* 175 */         latest_time_used = this.latest_time;
/*     */       }
/*     */       
/* 178 */       long stats_diff = latest_time_used - last_stats_time;
/*     */       
/* 180 */       if (stats_diff > 10000L)
/*     */       {
/* 182 */         last_stats_time = latest_time_used;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void register(PeerControlInstance instance)
/*     */   {
/* 191 */     instanceWrapper wrapper = new instanceWrapper(instance);
/*     */     try
/*     */     {
/* 194 */       this.this_mon.enter();
/*     */       
/* 196 */       Map new_map = new HashMap(this.instance_map);
/*     */       
/* 198 */       new_map.put(instance, wrapper);
/*     */       
/* 200 */       this.instance_map = new_map;
/*     */       
/* 202 */       this.pending_registrations.add(wrapper);
/*     */       
/* 204 */       this.registrations_changed = true;
/*     */     }
/*     */     finally
/*     */     {
/* 208 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void unregister(PeerControlInstance instance)
/*     */   {
/*     */     try
/*     */     {
/* 217 */       this.this_mon.enter();
/*     */       
/* 219 */       Map new_map = new HashMap(this.instance_map);
/*     */       
/* 221 */       instanceWrapper wrapper = (instanceWrapper)new_map.remove(instance);
/*     */       
/* 223 */       if (wrapper == null)
/*     */       {
/* 225 */         Debug.out("instance wrapper not found");
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 230 */         wrapper.unregister();
/*     */         
/* 232 */         this.instance_map = new_map;
/*     */         
/* 234 */         this.registrations_changed = true;
/*     */       }
/*     */     }
/*     */     finally {
/* 238 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public SpeedTokenDispenser getSpeedTokenDispenser()
/*     */   {
/* 245 */     return this.tokenDispenser;
/*     */   }
/*     */   
/*     */   public void updateScheduleOrdering() {
/* 249 */     this.registrations_changed = true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class instanceWrapper
/*     */     implements Comparable
/*     */   {
/*     */     private final PeerControlInstance instance;
/*     */     
/*     */     private boolean unregistered;
/*     */     
/*     */     private long offset;
/*     */     
/*     */     protected instanceWrapper(PeerControlInstance _instance)
/*     */     {
/* 264 */       this.instance = _instance;
/*     */     }
/*     */     
/*     */ 
/*     */     protected void unregister()
/*     */     {
/* 270 */       this.unregistered = true;
/*     */     }
/*     */     
/*     */ 
/*     */     protected boolean isUnregistered()
/*     */     {
/* 276 */       return this.unregistered;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setScheduleOffset(long t)
/*     */     {
/* 283 */       this.offset = t;
/*     */     }
/*     */     
/*     */ 
/*     */     protected long getScheduleOffset()
/*     */     {
/* 289 */       return this.offset;
/*     */     }
/*     */     
/*     */ 
/*     */     protected PeerControlInstance getInstance()
/*     */     {
/* 295 */       return this.instance;
/*     */     }
/*     */     
/*     */     protected void schedule()
/*     */     {
/*     */       try
/*     */       {
/* 302 */         this.instance.schedule();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 306 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public int compareTo(Object o) {
/* 311 */       return this.instance.getSchedulePriority() - ((instanceWrapper)o).instance.getSchedulePriority();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/control/impl/PeerControlSchedulerPrioritised.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */