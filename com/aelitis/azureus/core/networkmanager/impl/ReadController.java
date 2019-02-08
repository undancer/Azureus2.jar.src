/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.EventWaiter;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
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
/*     */ public class ReadController
/*     */   implements AzureusCoreStatsProvider
/*     */ {
/*  43 */   private static int IDLE_SLEEP_TIME = 50;
/*  44 */   private static boolean AGGRESIVE_READ = false;
/*     */   
/*     */   static {
/*  47 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "network.control.read.idle.time", "network.control.read.aggressive" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  58 */         ReadController.access$002(COConfigurationManager.getIntParameter("network.control.read.idle.time"));
/*  59 */         ReadController.access$102(COConfigurationManager.getBooleanParameter("network.control.read.aggressive"));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*  64 */   private volatile ArrayList<RateControlledEntity> normal_priority_entities = new ArrayList();
/*  65 */   private volatile ArrayList<RateControlledEntity> high_priority_entities = new ArrayList();
/*  66 */   private final AEMonitor entities_mon = new AEMonitor("ReadController:EM");
/*  67 */   private int next_normal_position = 0;
/*  68 */   private int next_high_position = 0;
/*     */   
/*     */   private long loop_count;
/*     */   
/*     */   private long wait_count;
/*     */   
/*     */   private long non_progress_count;
/*     */   private long progress_count;
/*     */   private long entity_check_count;
/*     */   private long last_entity_check_count;
/*  78 */   private final EventWaiter read_waiter = new EventWaiter();
/*     */   
/*     */   private int entity_count;
/*     */   
/*     */ 
/*     */   public ReadController()
/*     */   {
/*  85 */     Thread read_processor_thread = new AEThread("ReadController:ReadProcessor") {
/*     */       public void runSupport() {
/*  87 */         ReadController.this.readProcessorLoop();
/*     */       }
/*  89 */     };
/*  90 */     read_processor_thread.setDaemon(true);
/*  91 */     read_processor_thread.setPriority(9);
/*  92 */     read_processor_thread.start();
/*     */     
/*  94 */     Set types = new HashSet();
/*     */     
/*  96 */     types.add("net.read.control.loop.count");
/*  97 */     types.add("net.read.control.np.count");
/*  98 */     types.add("net.read.control.p.count");
/*  99 */     types.add("net.read.control.wait.count");
/* 100 */     types.add("net.read.control.entity.count");
/* 101 */     types.add("net.read.control.con.count");
/* 102 */     types.add("net.read.control.ready.con.count");
/*     */     
/* 104 */     AzureusCoreStats.registerProvider(types, this);
/*     */     
/*     */ 
/*     */ 
/* 108 */     AEDiagnostics.addEvidenceGenerator(new AEDiagnosticsEvidenceGenerator()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void generate(IndentWriter writer)
/*     */       {
/*     */ 
/* 115 */         writer.println("Read Controller");
/*     */         try
/*     */         {
/* 118 */           writer.indent();
/*     */           
/* 120 */           ArrayList<RateControlledEntity> ref = ReadController.this.normal_priority_entities;
/*     */           
/* 122 */           writer.println("normal - " + ref.size());
/*     */           
/* 124 */           for (int i = 0; i < ref.size(); i++)
/*     */           {
/* 126 */             RateControlledEntity entity = (RateControlledEntity)ref.get(i);
/*     */             
/* 128 */             writer.println(entity.getString());
/*     */           }
/*     */           
/* 131 */           ref = ReadController.this.high_priority_entities;
/*     */           
/* 133 */           writer.println("priority - " + ref.size());
/*     */           
/* 135 */           for (int i = 0; i < ref.size(); i++)
/*     */           {
/* 137 */             RateControlledEntity entity = (RateControlledEntity)ref.get(i);
/*     */             
/* 139 */             writer.println(entity.getString());
/*     */           }
/*     */         }
/*     */         finally {
/* 143 */           writer.exdent();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateStats(Set types, Map values)
/*     */   {
/* 154 */     if (types.contains("net.read.control.loop.count"))
/*     */     {
/* 156 */       values.put("net.read.control.loop.count", new Long(this.loop_count));
/*     */     }
/*     */     
/* 159 */     if (types.contains("net.read.control.np.count"))
/*     */     {
/* 161 */       values.put("net.read.control.np.count", new Long(this.non_progress_count));
/*     */     }
/*     */     
/* 164 */     if (types.contains("net.read.control.p.count"))
/*     */     {
/* 166 */       values.put("net.read.control.p.count", new Long(this.progress_count));
/*     */     }
/*     */     
/* 169 */     if (types.contains("net.read.control.wait.count"))
/*     */     {
/* 171 */       values.put("net.read.control.wait.count", new Long(this.wait_count));
/*     */     }
/*     */     
/* 174 */     if (types.contains("net.read.control.entity.count"))
/*     */     {
/* 176 */       values.put("net.read.control.entity.count", new Long(this.high_priority_entities.size() + this.normal_priority_entities.size()));
/*     */     }
/*     */     
/* 179 */     if ((types.contains("net.read.control.con.count")) || (types.contains("net.read.control.ready.con.count")))
/*     */     {
/*     */ 
/* 182 */       int ready_connections = 0;
/* 183 */       int connections = 0;
/*     */       
/* 185 */       ArrayList[] refs = { this.normal_priority_entities, this.high_priority_entities };
/*     */       
/* 187 */       for (int i = 0; i < refs.length; i++)
/*     */       {
/* 189 */         ArrayList ref = refs[i];
/*     */         
/* 191 */         for (int j = 0; j < ref.size(); j++)
/*     */         {
/* 193 */           RateControlledEntity entity = (RateControlledEntity)ref.get(j);
/*     */           
/* 195 */           connections += entity.getConnectionCount(this.read_waiter);
/*     */           
/* 197 */           ready_connections += entity.getReadyConnectionCount(this.read_waiter);
/*     */         }
/*     */       }
/*     */       
/* 201 */       values.put("net.read.control.con.count", new Long(connections));
/* 202 */       values.put("net.read.control.ready.con.count", new Long(ready_connections));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void readProcessorLoop()
/*     */   {
/* 210 */     boolean check_high_first = true;
/*     */     for (;;)
/*     */     {
/* 213 */       this.loop_count += 1L;
/*     */       try {
/* 215 */         if (check_high_first) {
/* 216 */           check_high_first = false;
/* 217 */           if ((!doHighPriorityRead()) && 
/* 218 */             (!doNormalPriorityRead())) {
/* 219 */             if (this.read_waiter.waitForEvent(hasConnections() ? IDLE_SLEEP_TIME : 1000L)) {
/* 220 */               this.wait_count += 1L;
/*     */             }
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 226 */           check_high_first = true;
/* 227 */           if ((!doNormalPriorityRead()) && 
/* 228 */             (!doHighPriorityRead())) {
/* 229 */             if (this.read_waiter.waitForEvent(hasConnections() ? IDLE_SLEEP_TIME : 1000L)) {
/* 230 */               this.wait_count += 1L;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 237 */         Debug.out("readProcessorLoop() EXCEPTION: ", t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean hasConnections()
/*     */   {
/* 245 */     if (this.entity_count == 0)
/*     */     {
/* 247 */       return false;
/*     */     }
/*     */     
/* 250 */     List<RateControlledEntity> ref = this.high_priority_entities;
/*     */     
/* 252 */     for (RateControlledEntity e : ref)
/*     */     {
/* 254 */       if (e.getConnectionCount(this.read_waiter) > 0)
/*     */       {
/* 256 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 260 */     ref = this.normal_priority_entities;
/*     */     
/* 262 */     for (RateControlledEntity e : ref)
/*     */     {
/* 264 */       if (e.getConnectionCount(this.read_waiter) > 0)
/*     */       {
/* 266 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 270 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean doNormalPriorityRead()
/*     */   {
/* 276 */     return doRead(getNextReadyNormalPriorityEntity());
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean doHighPriorityRead()
/*     */   {
/* 282 */     return doRead(getNextReadyHighPriorityEntity());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean doRead(RateControlledEntity ready_entity)
/*     */   {
/* 289 */     if (ready_entity != null)
/*     */     {
/* 291 */       if (AGGRESIVE_READ)
/*     */       {
/*     */ 
/*     */ 
/* 295 */         if (ready_entity.doProcessing(this.read_waiter, 0) > 0)
/*     */         {
/* 297 */           this.progress_count += 1L;
/*     */           
/* 299 */           return true;
/*     */         }
/*     */         
/*     */ 
/* 303 */         this.non_progress_count += 1L;
/*     */         
/* 305 */         if (this.entity_check_count - this.last_entity_check_count >= this.normal_priority_entities.size() + this.high_priority_entities.size())
/*     */         {
/* 307 */           this.last_entity_check_count = this.entity_check_count;
/*     */           
/*     */ 
/*     */ 
/* 311 */           if (this.read_waiter.waitForEvent(IDLE_SLEEP_TIME)) {
/* 312 */             this.wait_count += 1L;
/*     */           }
/*     */           
/* 315 */           return false;
/*     */         }
/*     */         
/* 318 */         return true;
/*     */       }
/*     */       
/*     */ 
/* 322 */       return ready_entity.doProcessing(this.read_waiter, 0) > 0;
/*     */     }
/*     */     
/*     */ 
/* 326 */     return false;
/*     */   }
/*     */   
/*     */   private RateControlledEntity getNextReadyNormalPriorityEntity()
/*     */   {
/* 331 */     ArrayList<RateControlledEntity> ref = this.normal_priority_entities;
/*     */     
/* 333 */     int size = ref.size();
/* 334 */     int num_checked = 0;
/*     */     
/* 336 */     while (num_checked < size) {
/* 337 */       this.entity_check_count += 1L;
/* 338 */       this.next_normal_position = (this.next_normal_position >= size ? 0 : this.next_normal_position);
/* 339 */       RateControlledEntity entity = (RateControlledEntity)ref.get(this.next_normal_position);
/* 340 */       this.next_normal_position += 1;
/* 341 */       num_checked++;
/* 342 */       if (entity.canProcess(this.read_waiter)) {
/* 343 */         return entity;
/*     */       }
/*     */     }
/*     */     
/* 347 */     return null;
/*     */   }
/*     */   
/*     */   private RateControlledEntity getNextReadyHighPriorityEntity()
/*     */   {
/* 352 */     ArrayList<RateControlledEntity> ref = this.high_priority_entities;
/*     */     
/* 354 */     int size = ref.size();
/* 355 */     int num_checked = 0;
/*     */     
/* 357 */     while (num_checked < size) {
/* 358 */       this.entity_check_count += 1L;
/* 359 */       this.next_high_position = (this.next_high_position >= size ? 0 : this.next_high_position);
/* 360 */       RateControlledEntity entity = (RateControlledEntity)ref.get(this.next_high_position);
/* 361 */       this.next_high_position += 1;
/* 362 */       num_checked++;
/* 363 */       if (entity.canProcess(this.read_waiter)) {
/* 364 */         return entity;
/*     */       }
/*     */     }
/*     */     
/* 368 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addReadEntity(RateControlledEntity entity)
/*     */   {
/*     */     try
/*     */     {
/* 378 */       this.entities_mon.enter();
/* 379 */       if (entity.getPriority() == 1)
/*     */       {
/* 381 */         ArrayList<RateControlledEntity> high_new = new ArrayList(this.high_priority_entities.size() + 1);
/* 382 */         high_new.addAll(this.high_priority_entities);
/* 383 */         high_new.add(entity);
/* 384 */         this.high_priority_entities = high_new;
/*     */       }
/*     */       else
/*     */       {
/* 388 */         ArrayList<RateControlledEntity> norm_new = new ArrayList(this.normal_priority_entities.size() + 1);
/* 389 */         norm_new.addAll(this.normal_priority_entities);
/* 390 */         norm_new.add(entity);
/* 391 */         this.normal_priority_entities = norm_new;
/*     */       }
/*     */       
/* 394 */       this.entity_count = (this.normal_priority_entities.size() + this.high_priority_entities.size());
/*     */     } finally {
/* 396 */       this.entities_mon.exit();
/*     */     }
/* 398 */     this.read_waiter.eventOccurred();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeReadEntity(RateControlledEntity entity)
/*     */   {
/*     */     try
/*     */     {
/* 407 */       this.entities_mon.enter();
/* 408 */       if (entity.getPriority() == 1)
/*     */       {
/* 410 */         ArrayList<RateControlledEntity> high_new = new ArrayList(this.high_priority_entities);
/* 411 */         high_new.remove(entity);
/* 412 */         this.high_priority_entities = high_new;
/*     */       }
/*     */       else
/*     */       {
/* 416 */         ArrayList<RateControlledEntity> norm_new = new ArrayList(this.normal_priority_entities);
/* 417 */         norm_new.remove(entity);
/* 418 */         this.normal_priority_entities = norm_new;
/*     */       }
/*     */       
/* 421 */       this.entity_count = (this.normal_priority_entities.size() + this.high_priority_entities.size());
/*     */     } finally {
/* 423 */       this.entities_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public int getEntityCount()
/*     */   {
/* 429 */     return this.entity_count;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/ReadController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */