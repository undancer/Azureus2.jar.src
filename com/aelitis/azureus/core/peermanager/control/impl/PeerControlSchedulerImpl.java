/*     */ package com.aelitis.azureus.core.peermanager.control.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.control.PeerControlScheduler;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class PeerControlSchedulerImpl
/*     */   implements PeerControlScheduler, AzureusCoreStatsProvider, ParameterListener
/*     */ {
/*     */   private static final PeerControlSchedulerImpl[] singletons;
/*  59 */   protected boolean useWeights = true;
/*     */   protected long schedule_count;
/*     */   protected long wait_count;
/*     */   protected long yield_count;
/*     */   protected long total_wait_time;
/*     */   
/*     */   public void parameterChanged(String parameterName) {
/*  66 */     this.useWeights = COConfigurationManager.getBooleanParameter("Use Request Limiting Priorities");
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*  36 */     int num = COConfigurationManager.getIntParameter("peercontrol.scheduler.parallelism", 1);
/*     */     
/*  38 */     if (num < 1)
/*     */     {
/*  40 */       num = 1;
/*     */     }
/*  42 */     else if (num > 1)
/*     */     {
/*  44 */       if (COConfigurationManager.getBooleanParameter("peercontrol.scheduler.use.priorities"))
/*     */       {
/*  46 */         Debug.out("Multiple peer schedulers not supported for prioritised scheduling");
/*     */         
/*  48 */         num = 1;
/*     */       }
/*     */       else
/*     */       {
/*  52 */         System.out.println("Peer control scheduler parallelism=" + num);
/*     */       }
/*     */     }
/*     */     
/*  56 */     singletons = new PeerControlSchedulerImpl[num];
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
/*  71 */     for (int i = 0; i < singletons.length; i++)
/*     */     {
/*     */       PeerControlSchedulerImpl singleton;
/*     */       PeerControlSchedulerImpl singleton;
/*  75 */       if (COConfigurationManager.getBooleanParameter("peercontrol.scheduler.use.priorities"))
/*     */       {
/*  77 */         singleton = new PeerControlSchedulerPrioritised();
/*     */       }
/*     */       else
/*     */       {
/*  81 */         singleton = new PeerControlSchedulerBasic();
/*     */       }
/*     */       
/*     */ 
/*  85 */       singletons[i] = singleton;
/*     */       
/*  87 */       singleton.start();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static PeerControlScheduler getSingleton(int id)
/*     */   {
/*  95 */     return singletons[(id % singletons.length)];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void overrideAllWeightedPriorities(boolean b)
/*     */   {
/* 102 */     for (PeerControlSchedulerImpl s : singletons)
/*     */     {
/* 104 */       s.overrideWeightedPriorities(b);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void updateAllScheduleOrdering()
/*     */   {
/* 111 */     for (PeerControlSchedulerImpl s : singletons)
/*     */     {
/* 113 */       s.updateScheduleOrdering();
/*     */     }
/*     */   }
/*     */   
/*     */   protected PeerControlSchedulerImpl()
/*     */   {
/*  62 */     COConfigurationManager.addAndFireParameterListener("Use Request Limiting Priorities", this);
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
/* 125 */     Set types = new HashSet();
/*     */     
/* 127 */     types.add("peer.control.schedule.count");
/* 128 */     types.add("peer.control.loop.count");
/* 129 */     types.add("peer.control.yield.count");
/* 130 */     types.add("peer.control.wait.count");
/* 131 */     types.add("peer.control.wait.time");
/*     */     
/* 133 */     AzureusCoreStats.registerProvider(types, this);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void start()
/*     */   {
/* 139 */     new AEThread2("PeerControlScheduler", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 144 */         PeerControlSchedulerImpl.this.schedule();
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateStats(Set types, Map values)
/*     */   {
/* 155 */     if (types.contains("peer.control.schedule.count"))
/*     */     {
/* 157 */       values.put("peer.control.schedule.count", new Long(this.schedule_count));
/*     */     }
/* 159 */     if (types.contains("peer.control.loop.count"))
/*     */     {
/* 161 */       values.put("peer.control.loop.count", new Long(this.wait_count + this.yield_count));
/*     */     }
/* 163 */     if (types.contains("peer.control.yield.count"))
/*     */     {
/* 165 */       values.put("peer.control.yield.count", new Long(this.yield_count));
/*     */     }
/* 167 */     if (types.contains("peer.control.wait.count"))
/*     */     {
/* 169 */       values.put("peer.control.wait.count", new Long(this.wait_count));
/*     */     }
/* 171 */     if (types.contains("peer.control.wait.time"))
/*     */     {
/* 173 */       values.put("peer.control.wait.time", new Long(this.total_wait_time));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void overrideWeightedPriorities(boolean override)
/*     */   {
/* 181 */     if (override) {
/* 182 */       this.useWeights = false;
/*     */     } else {
/* 184 */       parameterChanged(null);
/*     */     }
/*     */   }
/*     */   
/*     */   protected abstract void schedule();
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/control/impl/PeerControlSchedulerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */