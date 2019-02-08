/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.EventWaiter;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
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
/*     */ public class WriteController
/*     */   implements AzureusCoreStatsProvider
/*     */ {
/*  39 */   private static int IDLE_SLEEP_TIME = 50;
/*  40 */   private static boolean AGGRESIVE_WRITE = false;
/*  41 */   private static int BOOSTER_GIFT = 5120;
/*     */   
/*     */   static {
/*  44 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "network.control.write.idle.time", "network.control.write.aggressive", "Bias Upload Slack KBs" }, new ParameterListener()
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
/*     */ 
/*  56 */         WriteController.access$002(COConfigurationManager.getIntParameter("network.control.write.idle.time"));
/*  57 */         WriteController.access$102(COConfigurationManager.getBooleanParameter("network.control.write.aggressive"));
/*  58 */         WriteController.access$202(COConfigurationManager.getIntParameter("Bias Upload Slack KBs") * 1024);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*  63 */   private volatile ArrayList<RateControlledEntity> normal_priority_entities = new ArrayList();
/*  64 */   private volatile ArrayList<RateControlledEntity> boosted_priority_entities = new ArrayList();
/*  65 */   private volatile ArrayList<RateControlledEntity> high_priority_entities = new ArrayList();
/*  66 */   private final AEMonitor entities_mon = new AEMonitor("WriteController:EM");
/*  67 */   private int next_normal_position = 0;
/*  68 */   private int next_boost_position = 0;
/*  69 */   private int next_high_position = 0;
/*     */   
/*     */   private long booster_process_time;
/*     */   
/*     */   private int booster_normal_written;
/*     */   private int booster_stat_index;
/*  75 */   private final int[] booster_normal_writes = new int[5];
/*  76 */   private final int[] booster_gifts = new int[5];
/*     */   
/*     */   private int aggressive_np_normal_priority_count;
/*     */   
/*     */   private int aggressive_np_high_priority_count;
/*     */   
/*     */   private long process_loop_time;
/*     */   private long wait_count;
/*     */   private long progress_count;
/*     */   private long non_progress_count;
/*  86 */   private final EventWaiter write_waiter = new EventWaiter();
/*     */   
/*     */   private NetworkManager net_man;
/*     */   
/*  90 */   private int entity_count = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public WriteController()
/*     */   {
/*  98 */     Thread write_processor_thread = new AEThread("WriteController:WriteProcessor") {
/*     */       public void runSupport() {
/* 100 */         WriteController.this.writeProcessorLoop();
/*     */       }
/* 102 */     };
/* 103 */     write_processor_thread.setDaemon(true);
/* 104 */     write_processor_thread.setPriority(9);
/* 105 */     write_processor_thread.start();
/*     */     
/* 107 */     Set types = new HashSet();
/*     */     
/* 109 */     types.add("net.write.control.wait.count");
/* 110 */     types.add("net.write.control.np.count");
/* 111 */     types.add("net.write.control.p.count");
/* 112 */     types.add("net.write.control.entity.count");
/* 113 */     types.add("net.write.control.con.count");
/* 114 */     types.add("net.write.control.ready.con.count");
/* 115 */     types.add("net.write.control.ready.byte.count");
/*     */     
/* 117 */     AzureusCoreStats.registerProvider(types, this);
/*     */     
/*     */ 
/*     */ 
/* 121 */     AEDiagnostics.addEvidenceGenerator(new AEDiagnosticsEvidenceGenerator()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void generate(IndentWriter writer)
/*     */       {
/*     */ 
/* 128 */         writer.println("Write Controller");
/*     */         try
/*     */         {
/* 131 */           writer.indent();
/*     */           
/* 133 */           ArrayList ref = WriteController.this.normal_priority_entities;
/*     */           
/* 135 */           writer.println("normal - " + ref.size());
/*     */           
/* 137 */           for (int i = 0; i < ref.size(); i++)
/*     */           {
/* 139 */             RateControlledEntity entity = (RateControlledEntity)ref.get(i);
/*     */             
/* 141 */             writer.println(entity.getString());
/*     */           }
/*     */           
/* 144 */           ref = WriteController.this.boosted_priority_entities;
/*     */           
/* 146 */           writer.println("boosted - " + ref.size());
/*     */           
/* 148 */           for (int i = 0; i < ref.size(); i++)
/*     */           {
/* 150 */             RateControlledEntity entity = (RateControlledEntity)ref.get(i);
/*     */             
/* 152 */             writer.println(entity.getString());
/*     */           }
/*     */           
/* 155 */           ref = WriteController.this.high_priority_entities;
/*     */           
/* 157 */           writer.println("priority - " + ref.size());
/*     */           
/* 159 */           for (int i = 0; i < ref.size(); i++)
/*     */           {
/* 161 */             RateControlledEntity entity = (RateControlledEntity)ref.get(i);
/*     */             
/* 163 */             writer.println(entity.getString());
/*     */           }
/*     */         }
/*     */         finally {
/* 167 */           writer.exdent();
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
/* 178 */     if (types.contains("net.write.control.wait.count"))
/*     */     {
/* 180 */       values.put("net.write.control.wait.count", new Long(this.wait_count));
/*     */     }
/*     */     
/* 183 */     if (types.contains("net.write.control.np.count"))
/*     */     {
/* 185 */       values.put("net.write.control.np.count", new Long(this.non_progress_count));
/*     */     }
/*     */     
/* 188 */     if (types.contains("net.write.control.p.count"))
/*     */     {
/* 190 */       values.put("net.write.control.p.count", new Long(this.progress_count));
/*     */     }
/*     */     
/* 193 */     if (types.contains("net.write.control.entity.count"))
/*     */     {
/* 195 */       values.put("net.write.control.entity.count", new Long(this.high_priority_entities.size() + this.boosted_priority_entities.size() + this.normal_priority_entities.size()));
/*     */     }
/*     */     
/* 198 */     if ((types.contains("net.write.control.con.count")) || (types.contains("net.write.control.ready.con.count")) || (types.contains("net.write.control.ready.byte.count")))
/*     */     {
/*     */ 
/*     */ 
/* 202 */       long ready_bytes = 0L;
/* 203 */       int ready_connections = 0;
/* 204 */       int connections = 0;
/*     */       
/* 206 */       ArrayList[] refs = { this.normal_priority_entities, this.boosted_priority_entities, this.high_priority_entities };
/*     */       
/* 208 */       for (int i = 0; i < refs.length; i++)
/*     */       {
/* 210 */         ArrayList ref = refs[i];
/*     */         
/* 212 */         for (int j = 0; j < ref.size(); j++)
/*     */         {
/* 214 */           RateControlledEntity entity = (RateControlledEntity)ref.get(j);
/*     */           
/* 216 */           connections += entity.getConnectionCount(this.write_waiter);
/*     */           
/* 218 */           ready_connections += entity.getReadyConnectionCount(this.write_waiter);
/*     */           
/* 220 */           ready_bytes += entity.getBytesReadyToWrite();
/*     */         }
/*     */       }
/*     */       
/* 224 */       values.put("net.write.control.con.count", new Long(connections));
/* 225 */       values.put("net.write.control.ready.con.count", new Long(ready_connections));
/* 226 */       values.put("net.write.control.ready.byte.count", new Long(ready_bytes));
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeProcessorLoop() {
/* 231 */     boolean check_high_first = true;
/*     */     
/* 233 */     long last_check = SystemTime.getMonotonousTime();
/*     */     
/* 235 */     this.net_man = NetworkManager.getSingleton();
/*     */     
/*     */     for (;;)
/*     */     {
/* 239 */       this.process_loop_time = SystemTime.getMonotonousTime();
/*     */       try
/*     */       {
/* 242 */         if (check_high_first) {
/* 243 */           check_high_first = false;
/* 244 */           if ((!doHighPriorityWrite()) && 
/* 245 */             (!doNormalPriorityWrite())) {
/* 246 */             if (this.write_waiter.waitForEvent(hasConnections() ? IDLE_SLEEP_TIME : 1000L)) {
/* 247 */               this.wait_count += 1L;
/*     */             }
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 253 */           check_high_first = true;
/* 254 */           if ((!doNormalPriorityWrite()) && 
/* 255 */             (!doHighPriorityWrite())) {
/* 256 */             if (this.write_waiter.waitForEvent(hasConnections() ? IDLE_SLEEP_TIME : 1000L)) {
/* 257 */               this.wait_count += 1L;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 264 */         Debug.out("writeProcessorLoop() EXCEPTION: ", t);
/*     */       }
/*     */       
/* 267 */       if (this.process_loop_time - last_check > 5000L)
/*     */       {
/* 269 */         last_check = this.process_loop_time;
/*     */         
/* 271 */         boolean changed = false;
/*     */         
/* 273 */         ArrayList<RateControlledEntity> ref = this.normal_priority_entities;
/*     */         
/* 275 */         for (RateControlledEntity e : ref)
/*     */         {
/* 277 */           if (e.getPriorityBoost())
/*     */           {
/* 279 */             changed = true;
/*     */             
/* 281 */             break;
/*     */           }
/*     */         }
/*     */         
/* 285 */         if (!changed)
/*     */         {
/* 287 */           ref = this.boosted_priority_entities;
/*     */           
/* 289 */           for (RateControlledEntity e : ref)
/*     */           {
/* 291 */             if (!e.getPriorityBoost())
/*     */             {
/* 293 */               changed = true;
/*     */               
/* 295 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 300 */         if (changed) {
/*     */           try
/*     */           {
/* 303 */             this.entities_mon.enter();
/*     */             
/* 305 */             ArrayList<RateControlledEntity> new_normal = new ArrayList();
/* 306 */             ArrayList<RateControlledEntity> new_boosted = new ArrayList();
/*     */             
/* 308 */             for (RateControlledEntity e : this.normal_priority_entities)
/*     */             {
/* 310 */               if (e.getPriorityBoost())
/*     */               {
/* 312 */                 new_boosted.add(e);
/*     */               }
/*     */               else
/*     */               {
/* 316 */                 new_normal.add(e);
/*     */               }
/*     */             }
/*     */             
/* 320 */             for (RateControlledEntity e : this.boosted_priority_entities)
/*     */             {
/* 322 */               if (e.getPriorityBoost())
/*     */               {
/* 324 */                 new_boosted.add(e);
/*     */               }
/*     */               else
/*     */               {
/* 328 */                 new_normal.add(e);
/*     */               }
/*     */             }
/*     */             
/* 332 */             this.normal_priority_entities = new_normal;
/* 333 */             this.boosted_priority_entities = new_boosted;
/*     */           }
/*     */           finally
/*     */           {
/* 337 */             this.entities_mon.exit();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean hasConnections()
/*     */   {
/* 347 */     if (this.entity_count == 0)
/*     */     {
/* 349 */       return false;
/*     */     }
/*     */     
/* 352 */     List<RateControlledEntity> ref = this.high_priority_entities;
/*     */     
/* 354 */     for (RateControlledEntity e : ref)
/*     */     {
/* 356 */       if (e.getConnectionCount(this.write_waiter) > 0)
/*     */       {
/* 358 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 362 */     ref = this.boosted_priority_entities;
/*     */     
/* 364 */     for (RateControlledEntity e : ref)
/*     */     {
/* 366 */       if (e.getConnectionCount(this.write_waiter) > 0)
/*     */       {
/* 368 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 372 */     ref = this.normal_priority_entities;
/*     */     
/* 374 */     for (RateControlledEntity e : ref)
/*     */     {
/* 376 */       if (e.getConnectionCount(this.write_waiter) > 0)
/*     */       {
/* 378 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 382 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean doNormalPriorityWrite()
/*     */   {
/* 388 */     int result = processNextReadyNormalPriorityEntity();
/*     */     
/* 390 */     if (result > 0)
/*     */     {
/* 392 */       this.progress_count += 1L;
/*     */       
/* 394 */       return true;
/*     */     }
/* 396 */     if (result == 0)
/*     */     {
/* 398 */       this.non_progress_count += 1L;
/*     */       
/* 400 */       if (AGGRESIVE_WRITE)
/*     */       {
/* 402 */         this.aggressive_np_normal_priority_count += 1;
/*     */         
/* 404 */         if (this.aggressive_np_normal_priority_count < this.normal_priority_entities.size() + this.boosted_priority_entities.size())
/*     */         {
/* 406 */           return true;
/*     */         }
/*     */         
/*     */ 
/* 410 */         this.aggressive_np_normal_priority_count = 0;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 415 */     return false;
/*     */   }
/*     */   
/*     */   private boolean doHighPriorityWrite() {
/* 419 */     RateControlledEntity ready_entity = getNextReadyHighPriorityEntity();
/* 420 */     if (ready_entity != null) {
/* 421 */       if (ready_entity.doProcessing(this.write_waiter, 0) > 0)
/*     */       {
/* 423 */         this.progress_count += 1L;
/*     */         
/* 425 */         return true;
/*     */       }
/*     */       
/*     */ 
/* 429 */       this.non_progress_count += 1L;
/*     */       
/* 431 */       if (AGGRESIVE_WRITE)
/*     */       {
/* 433 */         this.aggressive_np_high_priority_count += 1;
/*     */         
/* 435 */         if (this.aggressive_np_high_priority_count < this.high_priority_entities.size())
/*     */         {
/* 437 */           return true;
/*     */         }
/*     */         
/*     */ 
/* 441 */         this.aggressive_np_high_priority_count = 0;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 446 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int processNextReadyNormalPriorityEntity()
/*     */   {
/* 453 */     ArrayList<RateControlledEntity> boosted_ref = this.boosted_priority_entities;
/*     */     
/* 455 */     int boosted_size = boosted_ref.size();
/*     */     try {
/*     */       int num_checked;
/* 458 */       if (boosted_size > 0)
/*     */       {
/* 460 */         if (this.process_loop_time - this.booster_process_time >= 1000L)
/*     */         {
/* 462 */           this.booster_process_time = this.process_loop_time;
/*     */           
/* 464 */           this.booster_gifts[this.booster_stat_index] = BOOSTER_GIFT;
/* 465 */           this.booster_normal_writes[this.booster_stat_index] = this.booster_normal_written;
/*     */           
/* 467 */           this.booster_stat_index += 1;
/*     */           
/* 469 */           if (this.booster_stat_index >= this.booster_gifts.length)
/*     */           {
/* 471 */             this.booster_stat_index = 0;
/*     */           }
/*     */           
/* 474 */           this.booster_normal_written = 0;
/*     */         }
/*     */         
/* 477 */         int total_gifts = 0;
/* 478 */         int total_normal_writes = 0;
/*     */         
/* 480 */         for (int i = 0; i < this.booster_gifts.length; i++)
/*     */         {
/* 482 */           total_gifts += this.booster_gifts[i];
/* 483 */           total_normal_writes += this.booster_normal_writes[i];
/*     */         }
/*     */         
/* 486 */         int effective_gift = total_gifts - total_normal_writes;
/*     */         
/* 488 */         if (effective_gift > 0)
/*     */         {
/* 490 */           ArrayList<RateControlledEntity> normal_ref = this.normal_priority_entities;
/*     */           
/* 492 */           int normal_size = normal_ref.size();
/*     */           
/* 494 */           num_checked = 0;
/*     */           
/* 496 */           int position = this.next_normal_position;
/*     */           
/* 498 */           List<RateControlledEntity> ready = new ArrayList();
/*     */           
/* 500 */           while (num_checked < normal_size) {
/* 501 */             position = position >= normal_size ? 0 : position;
/* 502 */             RateControlledEntity entity = (RateControlledEntity)normal_ref.get(position);
/* 503 */             position++;
/* 504 */             num_checked++;
/* 505 */             if (entity.canProcess(this.write_waiter)) {
/* 506 */               this.next_normal_position = position;
/* 507 */               ready.add(entity);
/*     */             }
/*     */           }
/*     */           
/* 511 */           int num_ready = ready.size();
/*     */           
/* 513 */           if (num_ready > 0)
/*     */           {
/* 515 */             int gift_used = 0;
/*     */             
/* 517 */             for (RateControlledEntity r : ready)
/*     */             {
/* 519 */               int permitted = effective_gift / num_ready;
/*     */               
/* 521 */               if (permitted <= 0)
/*     */               {
/* 523 */                 permitted = 1;
/*     */               }
/*     */               
/* 526 */               if (r.canProcess(this.write_waiter))
/*     */               {
/* 528 */                 int done = r.doProcessing(this.write_waiter, permitted);
/*     */                 
/* 530 */                 if (done > 0)
/*     */                 {
/* 532 */                   this.booster_normal_written += done;
/*     */                   
/* 534 */                   gift_used += done;
/*     */                 }
/*     */               }
/*     */               
/* 538 */               num_ready--;
/*     */             }
/*     */             
/* 541 */             for (int i = this.booster_stat_index; (gift_used > 0) && (i < this.booster_stat_index + this.booster_gifts.length); i++)
/*     */             {
/* 543 */               int avail = this.booster_gifts[(i % this.booster_gifts.length)];
/*     */               
/* 545 */               if (avail > 0)
/*     */               {
/* 547 */                 int temp = Math.min(avail, gift_used);
/*     */                 
/* 549 */                 avail -= temp;
/* 550 */                 gift_used -= temp;
/*     */                 
/* 552 */                 this.booster_gifts[(i % this.booster_gifts.length)] = avail;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 558 */         int num_checked = 0;
/*     */         
/* 560 */         while (num_checked < boosted_size) {
/* 561 */           this.next_boost_position = (this.next_boost_position >= boosted_size ? 0 : this.next_boost_position);
/* 562 */           RateControlledEntity entity = (RateControlledEntity)boosted_ref.get(this.next_boost_position);
/* 563 */           this.next_boost_position += 1;
/* 564 */           num_checked++;
/* 565 */           if (entity.canProcess(this.write_waiter)) {
/* 566 */             return entity.doProcessing(this.write_waiter, 0);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 573 */         this.net_man.getUploadProcessor().setRateLimiterFreezeState(true);
/*     */       }
/*     */       else
/*     */       {
/* 577 */         this.booster_normal_written = 0;
/*     */       }
/*     */       
/* 580 */       ArrayList<RateControlledEntity> normal_ref = this.normal_priority_entities;
/*     */       
/* 582 */       int normal_size = normal_ref.size();
/*     */       
/* 584 */       int num_checked = 0;
/*     */       RateControlledEntity entity;
/* 586 */       while (num_checked < normal_size) {
/* 587 */         this.next_normal_position = (this.next_normal_position >= normal_size ? 0 : this.next_normal_position);
/* 588 */         entity = (RateControlledEntity)normal_ref.get(this.next_normal_position);
/* 589 */         this.next_normal_position += 1;
/* 590 */         num_checked++;
/* 591 */         if (entity.canProcess(this.write_waiter)) {
/* 592 */           int bytes = entity.doProcessing(this.write_waiter, 0);
/*     */           
/* 594 */           if (bytes > 0)
/*     */           {
/* 596 */             this.booster_normal_written += bytes;
/*     */           }
/*     */           
/* 599 */           return bytes;
/*     */         }
/*     */       }
/*     */       
/* 603 */       return -1;
/*     */     }
/*     */     finally
/*     */     {
/* 607 */       if (boosted_size > 0)
/*     */       {
/* 609 */         this.net_man.getUploadProcessor().setRateLimiterFreezeState(false);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private RateControlledEntity getNextReadyHighPriorityEntity()
/*     */   {
/* 616 */     ArrayList ref = this.high_priority_entities;
/*     */     
/* 618 */     int size = ref.size();
/* 619 */     int num_checked = 0;
/*     */     
/* 621 */     while (num_checked < size) {
/* 622 */       this.next_high_position = (this.next_high_position >= size ? 0 : this.next_high_position);
/* 623 */       RateControlledEntity entity = (RateControlledEntity)ref.get(this.next_high_position);
/* 624 */       this.next_high_position += 1;
/* 625 */       num_checked++;
/* 626 */       if (entity.canProcess(this.write_waiter)) {
/* 627 */         return entity;
/*     */       }
/*     */     }
/*     */     
/* 631 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addWriteEntity(RateControlledEntity entity)
/*     */   {
/*     */     try
/*     */     {
/* 641 */       this.entities_mon.enter();
/* 642 */       if (entity.getPriority() == 1)
/*     */       {
/* 644 */         ArrayList high_new = new ArrayList(this.high_priority_entities.size() + 1);
/* 645 */         high_new.addAll(this.high_priority_entities);
/* 646 */         high_new.add(entity);
/* 647 */         this.high_priority_entities = high_new;
/*     */ 
/*     */       }
/* 650 */       else if (entity.getPriorityBoost()) {
/* 651 */         ArrayList boost_new = new ArrayList(this.boosted_priority_entities.size() + 1);
/* 652 */         boost_new.addAll(this.boosted_priority_entities);
/* 653 */         boost_new.add(entity);
/* 654 */         this.boosted_priority_entities = boost_new;
/*     */       } else {
/* 656 */         ArrayList norm_new = new ArrayList(this.normal_priority_entities.size() + 1);
/* 657 */         norm_new.addAll(this.normal_priority_entities);
/* 658 */         norm_new.add(entity);
/* 659 */         this.normal_priority_entities = norm_new;
/*     */       }
/*     */       
/*     */ 
/* 663 */       this.entity_count = (this.normal_priority_entities.size() + this.boosted_priority_entities.size() + this.high_priority_entities.size());
/*     */     } finally {
/* 665 */       this.entities_mon.exit();
/*     */     }
/* 667 */     this.write_waiter.eventOccurred();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeWriteEntity(RateControlledEntity entity)
/*     */   {
/*     */     try
/*     */     {
/* 676 */       this.entities_mon.enter();
/* 677 */       if (entity.getPriority() == 1)
/*     */       {
/* 679 */         ArrayList high_new = new ArrayList(this.high_priority_entities);
/* 680 */         high_new.remove(entity);
/* 681 */         this.high_priority_entities = high_new;
/*     */ 
/*     */ 
/*     */       }
/* 685 */       else if (this.boosted_priority_entities.contains(entity)) {
/* 686 */         ArrayList boosted_new = new ArrayList(this.boosted_priority_entities);
/* 687 */         boosted_new.remove(entity);
/* 688 */         this.boosted_priority_entities = boosted_new;
/*     */       } else {
/* 690 */         ArrayList norm_new = new ArrayList(this.normal_priority_entities);
/* 691 */         norm_new.remove(entity);
/* 692 */         this.normal_priority_entities = norm_new;
/*     */       }
/*     */       
/*     */ 
/* 696 */       this.entity_count = (this.normal_priority_entities.size() + this.boosted_priority_entities.size() + this.high_priority_entities.size());
/*     */     } finally {
/* 698 */       this.entities_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public int getEntityCount()
/*     */   {
/* 704 */     return this.entity_count;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/WriteController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */