/*     */ package com.aelitis.azureus.core.peermanager.control.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.control.PeerControlInstance;
/*     */ import com.aelitis.azureus.core.peermanager.control.SpeedTokenDispenser;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.SystemTime.TickConsumer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PeerControlSchedulerBasic
/*     */   extends PeerControlSchedulerImpl
/*     */   implements AzureusCoreStatsProvider
/*     */ {
/*     */   private final Random random;
/*     */   private Map<PeerControlInstance, instanceWrapper> instance_map;
/*     */   private final List<instanceWrapper> pending_registrations;
/*     */   private volatile boolean registrations_changed;
/*     */   protected final AEMonitor this_mon;
/*     */   private final SpeedTokenDispenserBasic tokenDispenser;
/*     */   private long latest_time;
/*     */   private long last_lag_log;
/*     */   
/*     */   public PeerControlSchedulerBasic()
/*     */   {
/*  38 */     this.random = new Random();
/*     */     
/*  40 */     this.instance_map = new HashMap();
/*     */     
/*  42 */     this.pending_registrations = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*  46 */     this.this_mon = new AEMonitor("PeerControlSchedulerBasic");
/*     */     
/*  48 */     this.tokenDispenser = new SpeedTokenDispenserBasic();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void schedule()
/*     */   {
/*  56 */     SystemTime.registerMonotonousConsumer(new SystemTime.TickConsumer()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void consume(long time)
/*     */       {
/*     */ 
/*  63 */         synchronized (PeerControlSchedulerBasic.this)
/*     */         {
/*  65 */           PeerControlSchedulerBasic.this.notify();
/*     */         }
/*     */         
/*     */       }
/*     */       
/*  70 */     });
/*  71 */     List<instanceWrapper> instances = new LinkedList();
/*     */     
/*  73 */     long tick_count = 0L;
/*  74 */     long last_stats_time = SystemTime.getMonotonousTime();
/*     */     
/*     */     for (;;)
/*     */     {
/*  78 */       if (this.registrations_changed) {
/*     */         try
/*     */         {
/*  81 */           this.this_mon.enter();
/*     */           
/*  83 */           Iterator<instanceWrapper> it = instances.iterator();
/*     */           
/*  85 */           while (it.hasNext())
/*     */           {
/*  87 */             if (((instanceWrapper)it.next()).isUnregistered())
/*     */             {
/*  89 */               it.remove();
/*     */             }
/*     */           }
/*     */           
/*  93 */           for (int i = 0; i < this.pending_registrations.size(); i++)
/*     */           {
/*  95 */             instances.add(this.pending_registrations.get(i));
/*     */           }
/*     */           
/*  98 */           this.pending_registrations.clear();
/*     */           
/* 100 */           this.registrations_changed = false;
/*     */         }
/*     */         finally
/*     */         {
/* 104 */           this.this_mon.exit();
/*     */         }
/*     */       }
/*     */       
/* 108 */       this.latest_time = SystemTime.getMonotonousTime();
/*     */       
/* 110 */       long current_schedule_count = this.schedule_count;
/*     */       
/* 112 */       for (instanceWrapper inst : instances)
/*     */       {
/* 114 */         long target = inst.getNextTick();
/*     */         
/* 116 */         long diff = this.latest_time - target;
/*     */         
/* 118 */         if (diff >= 0L)
/*     */         {
/* 120 */           tick_count += 1L;
/*     */           
/* 122 */           inst.schedule(this.latest_time);
/*     */           
/* 124 */           this.schedule_count += 1L;
/*     */           
/* 126 */           long new_target = target + SCHEDULE_PERIOD_MILLIS;
/*     */           
/* 128 */           if (new_target <= this.latest_time)
/*     */           {
/* 130 */             new_target = this.latest_time + target % SCHEDULE_PERIOD_MILLIS;
/*     */           }
/*     */           
/* 133 */           inst.setNextTick(new_target);
/*     */         }
/*     */       }
/*     */       
/* 137 */       synchronized (this)
/*     */       {
/* 139 */         if (current_schedule_count == this.schedule_count)
/*     */         {
/* 141 */           this.wait_count += 1L;
/*     */           try
/*     */           {
/* 144 */             long wait_start = SystemTime.getHighPrecisionCounter();
/*     */             
/* 146 */             wait(SCHEDULE_PERIOD_MILLIS);
/*     */             
/* 148 */             long wait_time = SystemTime.getHighPrecisionCounter() - wait_start;
/*     */             
/* 150 */             this.total_wait_time += wait_time;
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 154 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 159 */           this.yield_count += 1L;
/*     */           
/* 161 */           Thread.yield();
/*     */         }
/*     */       }
/*     */       
/* 165 */       long stats_diff = this.latest_time - last_stats_time;
/*     */       
/* 167 */       if (stats_diff > 10000L)
/*     */       {
/*     */ 
/*     */ 
/* 171 */         last_stats_time = this.latest_time;
/*     */         
/* 173 */         tick_count = 0L;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void register(PeerControlInstance instance)
/*     */   {
/* 182 */     instanceWrapper wrapper = new instanceWrapper(instance);
/*     */     
/* 184 */     wrapper.setNextTick(this.latest_time + this.random.nextInt(SCHEDULE_PERIOD_MILLIS));
/*     */     try
/*     */     {
/* 187 */       this.this_mon.enter();
/*     */       
/* 189 */       Map<PeerControlInstance, instanceWrapper> new_map = new HashMap(this.instance_map);
/*     */       
/* 191 */       new_map.put(instance, wrapper);
/*     */       
/* 193 */       this.instance_map = new_map;
/*     */       
/* 195 */       this.pending_registrations.add(wrapper);
/*     */       
/* 197 */       this.registrations_changed = true;
/*     */     }
/*     */     finally
/*     */     {
/* 201 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void unregister(PeerControlInstance instance)
/*     */   {
/*     */     try
/*     */     {
/* 210 */       this.this_mon.enter();
/*     */       
/* 212 */       Map<PeerControlInstance, instanceWrapper> new_map = new HashMap(this.instance_map);
/*     */       
/* 214 */       instanceWrapper wrapper = (instanceWrapper)new_map.remove(instance);
/*     */       
/* 216 */       if (wrapper == null)
/*     */       {
/* 218 */         Debug.out("instance wrapper not found");
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 223 */         wrapper.unregister();
/*     */         
/* 225 */         this.instance_map = new_map;
/*     */         
/* 227 */         this.registrations_changed = true;
/*     */       }
/*     */     }
/*     */     finally {
/* 231 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public SpeedTokenDispenser getSpeedTokenDispenser()
/*     */   {
/* 238 */     return this.tokenDispenser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void updateScheduleOrdering() {}
/*     */   
/*     */ 
/*     */ 
/*     */   protected class instanceWrapper
/*     */   {
/*     */     private final PeerControlInstance instance;
/*     */     
/*     */     private boolean unregistered;
/*     */     
/*     */     private long next_tick;
/*     */     
/*     */     private long last_schedule;
/*     */     
/*     */ 
/*     */     protected instanceWrapper(PeerControlInstance _instance)
/*     */     {
/* 260 */       this.instance = _instance;
/*     */     }
/*     */     
/*     */ 
/*     */     protected void unregister()
/*     */     {
/* 266 */       this.unregistered = true;
/*     */     }
/*     */     
/*     */ 
/*     */     protected boolean isUnregistered()
/*     */     {
/* 272 */       return this.unregistered;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setNextTick(long t)
/*     */     {
/* 279 */       this.next_tick = t;
/*     */     }
/*     */     
/*     */ 
/*     */     protected long getNextTick()
/*     */     {
/* 285 */       return this.next_tick;
/*     */     }
/*     */     
/*     */ 
/*     */     protected String getName()
/*     */     {
/* 291 */       return this.instance.getName();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void schedule(long mono_now)
/*     */     {
/* 298 */       if (mono_now < 100000L)
/*     */       {
/* 300 */         Debug.out("eh?");
/*     */       }
/*     */       
/* 303 */       if (this.last_schedule > 0L)
/*     */       {
/*     */ 
/* 306 */         if (mono_now - this.last_schedule > 1000L)
/*     */         {
/* 308 */           if (mono_now - PeerControlSchedulerBasic.this.last_lag_log > 1000L)
/*     */           {
/* 310 */             PeerControlSchedulerBasic.this.last_lag_log = mono_now;
/*     */             
/* 312 */             System.out.println("Scheduling lagging: " + (mono_now - this.last_schedule) + " - instances=" + PeerControlSchedulerBasic.this.instance_map.size());
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 317 */       this.last_schedule = mono_now;
/*     */       try
/*     */       {
/* 320 */         this.instance.schedule();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 324 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/control/impl/PeerControlSchedulerBasic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */