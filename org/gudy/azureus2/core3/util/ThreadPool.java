/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
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
/*     */ public class ThreadPool
/*     */ {
/*  41 */   private static final boolean NAME_THREADS = (Constants.IS_CVS_VERSION) && (System.getProperty("az.thread.pool.naming.enable", "true").equals("true"));
/*     */   
/*     */   private static final boolean LOG_WARNINGS = false;
/*     */   
/*     */   private static final int WARN_TIME = 10000;
/*  46 */   static final List busy_pools = new ArrayList();
/*  47 */   private static boolean busy_pool_timer_set = false;
/*     */   private static boolean debug_thread_pool;
/*     */   private static boolean debug_thread_pool_log_on;
/*     */   
/*     */   static
/*     */   {
/*  53 */     if (System.getProperty("transitory.startup", "0").equals("0"))
/*     */     {
/*  55 */       AEDiagnostics.addEvidenceGenerator(new AEDiagnosticsEvidenceGenerator()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void generate(IndentWriter writer)
/*     */         {
/*     */ 
/*  62 */           writer.println("Thread Pools");
/*     */           try
/*     */           {
/*  65 */             writer.indent();
/*     */             
/*     */             List pools;
/*     */             
/*  69 */             synchronized (ThreadPool.busy_pools)
/*     */             {
/*  71 */               pools = new ArrayList(ThreadPool.busy_pools);
/*     */             }
/*     */             
/*  74 */             for (int i = 0; i < pools.size(); i++)
/*     */             {
/*  76 */               ((ThreadPool)pools.get(i)).generateEvidence(writer);
/*     */             }
/*     */           }
/*     */           finally {
/*  80 */             writer.exdent();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*  87 */   static final ThreadLocal tls = new ThreadLocal()
/*     */   {
/*     */ 
/*     */     public Object initialValue()
/*     */     {
/*     */ 
/*  93 */       return null;
/*     */     }
/*     */   };
/*     */   
/*     */   final String name;
/*     */   
/*     */   private final int max_size;
/*     */   
/*     */ 
/*     */   protected static void checkAllTimeouts()
/*     */   {
/*     */     List pools;
/* 105 */     synchronized (busy_pools)
/*     */     {
/* 107 */       pools = new ArrayList(busy_pools);
/*     */     }
/*     */     
/* 110 */     for (int i = 0; i < pools.size(); i++)
/*     */     {
/* 112 */       ((ThreadPool)pools.get(i)).checkTimeouts();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 119 */   private int thread_name_index = 1;
/*     */   
/*     */   private long execution_limit;
/*     */   
/*     */   final List busy;
/*     */   private final boolean queue_when_full;
/* 125 */   final List task_queue = new ArrayList();
/*     */   
/*     */   final AESemaphore thread_sem;
/*     */   
/*     */   private int reserved_target;
/*     */   private int reserved_actual;
/* 131 */   private int thread_priority = 5;
/*     */   
/*     */   private boolean warn_when_full;
/*     */   private long task_total;
/*     */   private long task_total_last;
/* 136 */   private final Average task_average = Average.getInstance(10000, 120);
/*     */   
/* 138 */   private boolean log_cpu = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ThreadPool(String _name, int _max_size)
/*     */   {
/* 145 */     this(_name, _max_size, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ThreadPool(String _name, int _max_size, boolean _queue_when_full)
/*     */   {
/* 154 */     this.name = _name;
/* 155 */     this.max_size = _max_size;
/* 156 */     this.queue_when_full = _queue_when_full;
/*     */     
/* 158 */     this.thread_sem = new AESemaphore("ThreadPool::" + this.name, _max_size);
/*     */     
/* 160 */     this.busy = new ArrayList(_max_size);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void generateEvidence(IndentWriter writer)
/*     */   {
/* 167 */     writer.println(this.name + ": max=" + this.max_size + ",qwf=" + this.queue_when_full + ",queue=" + this.task_queue.size() + ",busy=" + this.busy.size() + ",total=" + this.task_total + ":" + DisplayFormatters.formatDecimal(this.task_average.getDoubleAverage(), 2) + "/sec");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setWarnWhenFull()
/*     */   {
/* 173 */     this.warn_when_full = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setLogCPU()
/*     */   {
/* 179 */     this.log_cpu = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxThreads()
/*     */   {
/* 185 */     return this.max_size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setThreadPriority(int _priority)
/*     */   {
/* 192 */     this.thread_priority = _priority;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setExecutionLimit(long millis)
/*     */   {
/* 199 */     synchronized (this)
/*     */     {
/* 201 */       this.execution_limit = millis;
/*     */     }
/*     */   }
/*     */   
/*     */   public threadPoolWorker run(AERunnable runnable) {
/* 206 */     return run(runnable, false, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public threadPoolWorker run(AERunnable runnable, boolean high_priority, boolean manualRelease)
/*     */   {
/* 218 */     if ((manualRelease) && (!(runnable instanceof ThreadPoolTask)))
/* 219 */       throw new IllegalArgumentException("manual release only allowed for ThreadPoolTasks");
/* 220 */     if (manualRelease) {
/* 221 */       ((ThreadPoolTask)runnable).setManualRelease();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 227 */     if (!this.queue_when_full)
/*     */     {
/* 229 */       if (!this.thread_sem.reserveIfAvailable())
/*     */       {
/*     */ 
/*     */ 
/* 233 */         threadPoolWorker recursive_worker = (threadPoolWorker)tls.get();
/*     */         
/* 235 */         if ((recursive_worker == null) || (recursive_worker.getOwner() != this))
/*     */         {
/*     */ 
/*     */ 
/* 239 */           checkWarning();
/*     */           
/* 241 */           this.thread_sem.reserve();
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 246 */           if ((runnable instanceof ThreadPoolTask))
/*     */           {
/* 248 */             ThreadPoolTask task = (ThreadPoolTask)runnable;
/*     */             
/* 250 */             task.worker = recursive_worker;
/*     */             try
/*     */             {
/* 253 */               task.taskStarted();
/*     */               
/* 255 */               runIt(runnable);
/*     */               
/* 257 */               task.join();
/*     */             }
/*     */             finally
/*     */             {
/* 261 */               task.taskCompleted();
/*     */             }
/*     */           }
/*     */           else {
/* 265 */             runIt(runnable);
/*     */           }
/*     */           
/* 268 */           return recursive_worker;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     threadPoolWorker allocated_worker;
/*     */     
/* 275 */     synchronized (this)
/*     */     {
/* 277 */       if (high_priority) {
/* 278 */         this.task_queue.add(0, runnable);
/*     */       } else {
/* 280 */         this.task_queue.add(runnable);
/*     */       }
/*     */       
/*     */ 
/* 284 */       if ((this.queue_when_full) && (!this.thread_sem.reserveIfAvailable()))
/*     */       {
/* 286 */         threadPoolWorker allocated_worker = null;
/*     */         
/* 288 */         checkWarning();
/*     */       }
/*     */       else
/*     */       {
/* 292 */         allocated_worker = new threadPoolWorker();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 297 */     return allocated_worker;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void runIt(AERunnable runnable)
/*     */   {
/* 304 */     if (this.log_cpu)
/*     */     {
/* 306 */       long start_cpu = this.log_cpu ? AEJavaManagement.getThreadCPUTime() : 0L;
/* 307 */       long start_time = SystemTime.getHighPrecisionCounter();
/*     */       
/* 309 */       runnable.run();
/*     */       
/* 311 */       if (start_cpu > 0L)
/*     */       {
/* 313 */         long end_cpu = this.log_cpu ? AEJavaManagement.getThreadCPUTime() : 0L;
/*     */         
/* 315 */         long diff_cpu = (end_cpu - start_cpu) / 1000000L;
/*     */         
/* 317 */         long end_time = SystemTime.getHighPrecisionCounter();
/*     */         
/* 319 */         long diff_millis = (end_time - start_time) / 1000000L;
/*     */         
/* 321 */         if ((diff_cpu > 10L) || (diff_millis > 10L))
/*     */         {
/* 323 */           System.out.println(TimeFormatter.milliStamp() + ": Thread: " + Thread.currentThread().getName() + ": " + runnable + " -> " + diff_cpu + "/" + diff_millis);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 328 */       runnable.run();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void checkWarning() {
/* 333 */     if (this.warn_when_full)
/*     */     {
/* 335 */       String task_names = "";
/*     */       try
/*     */       {
/* 338 */         synchronized (this)
/*     */         {
/* 340 */           for (int i = 0; i < this.busy.size(); i++)
/*     */           {
/* 342 */             threadPoolWorker x = (threadPoolWorker)this.busy.get(i);
/* 343 */             AERunnable r = x.runnable;
/* 344 */             if (r != null) {
/*     */               String name;
/*     */               String name;
/* 347 */               if ((r instanceof ThreadPoolTask)) {
/* 348 */                 name = ((ThreadPoolTask)r).getName();
/*     */               } else
/* 350 */                 name = r.getClass().getName();
/* 351 */               task_names = task_names + (task_names.length() == 0 ? "" : ",") + name;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/* 357 */       Debug.out("Thread pool '" + getName() + "' is full (busy=" + task_names + ")");
/* 358 */       this.warn_when_full = false;
/*     */     }
/*     */   }
/*     */   
/*     */   public AERunnable[] getQueuedTasks() {
/* 363 */     synchronized (this)
/*     */     {
/* 365 */       AERunnable[] res = new AERunnable[this.task_queue.size()];
/* 366 */       this.task_queue.toArray(res);
/* 367 */       return res;
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public AERunnable[] getRunningTasks()
/*     */   {
/* 388 */     List runnables = new ArrayList();
/*     */     
/* 390 */     synchronized (this)
/*     */     {
/* 392 */       Iterator it = this.busy.iterator();
/*     */       
/* 394 */       while (it.hasNext())
/*     */       {
/* 396 */         threadPoolWorker worker = (threadPoolWorker)it.next();
/*     */         
/* 398 */         AERunnable runnable = worker.getRunnable();
/*     */         
/* 400 */         if (runnable != null)
/*     */         {
/* 402 */           runnables.add(runnable);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 407 */     AERunnable[] res = new AERunnable[runnables.size()];
/*     */     
/* 409 */     runnables.toArray(res);
/*     */     
/* 411 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRunningCount()
/*     */   {
/* 417 */     int res = 0;
/*     */     
/* 419 */     synchronized (this)
/*     */     {
/* 421 */       Iterator it = this.busy.iterator();
/*     */       
/* 423 */       while (it.hasNext())
/*     */       {
/* 425 */         threadPoolWorker worker = (threadPoolWorker)it.next();
/*     */         
/* 427 */         AERunnable runnable = worker.getRunnable();
/*     */         
/* 429 */         if (runnable != null)
/*     */         {
/* 431 */           res++;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 436 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFull()
/*     */   {
/* 442 */     return this.thread_sem.getValue() == 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaxThreads(int max)
/*     */   {
/* 449 */     if (max > this.max_size)
/*     */     {
/* 451 */       Debug.out("should support this sometime...");
/*     */       
/* 453 */       return;
/*     */     }
/*     */     
/* 456 */     setReservedThreadCount(this.max_size - max);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setReservedThreadCount(int res)
/*     */   {
/* 463 */     synchronized (this)
/*     */     {
/* 465 */       if (res < 0)
/*     */       {
/* 467 */         res = 0;
/*     */       }
/* 469 */       else if (res > this.max_size)
/*     */       {
/* 471 */         res = this.max_size;
/*     */       }
/*     */       
/* 474 */       int diff = res - this.reserved_actual;
/*     */       
/* 476 */       while (diff < 0)
/*     */       {
/* 478 */         this.thread_sem.release();
/*     */         
/* 480 */         this.reserved_actual -= 1;
/*     */         
/* 482 */         diff++;
/*     */       }
/*     */       
/* 485 */       while (diff > 0)
/*     */       {
/* 487 */         if (!this.thread_sem.reserveIfAvailable())
/*     */           break;
/* 489 */         this.reserved_actual += 1;
/*     */         
/* 491 */         diff--;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 499 */       this.reserved_target = res;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void checkTimeouts()
/*     */   {
/* 506 */     synchronized (this)
/*     */     {
/* 508 */       long diff = this.task_total - this.task_total_last;
/*     */       
/* 510 */       this.task_average.addValue(diff);
/*     */       
/* 512 */       this.task_total_last = this.task_total;
/*     */       
/* 514 */       if (debug_thread_pool_log_on)
/*     */       {
/* 516 */         System.out.println("ThreadPool '" + getName() + "'/" + this.thread_name_index + ": max=" + this.max_size + ",sem=[" + this.thread_sem.getString() + "],busy=" + this.busy.size() + ",queue=" + this.task_queue.size());
/*     */       }
/*     */       
/* 519 */       long now = SystemTime.getMonotonousTime();
/*     */       
/* 521 */       for (int i = 0; i < this.busy.size(); i++)
/*     */       {
/* 523 */         threadPoolWorker x = (threadPoolWorker)this.busy.get(i);
/*     */         
/* 525 */         long elapsed = now - x.run_start_time;
/*     */         
/* 527 */         if (elapsed > 10000L * (x.warn_count + 1))
/*     */         {
/* 529 */           threadPoolWorker.access$308(x);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 536 */           if ((this.execution_limit > 0L) && (elapsed > this.execution_limit))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 543 */             AERunnable r = x.runnable;
/*     */             
/* 545 */             if (r != null) {
/*     */               try
/*     */               {
/* 548 */                 if ((r instanceof ThreadPoolTask))
/*     */                 {
/* 550 */                   ((ThreadPoolTask)r).interruptTask();
/*     */                 }
/*     */                 else
/*     */                 {
/* 554 */                   x.interrupt();
/*     */                 }
/*     */               }
/*     */               catch (Throwable e) {
/* 558 */                 DebugLight.printStackTrace(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String getName() {
/* 568 */     return this.name;
/*     */   }
/*     */   
/*     */   void releaseManual(ThreadPoolTask toRelease) {
/* 572 */     if (!toRelease.canManualRelease()) {
/* 573 */       throw new IllegalStateException("task not manually releasable");
/*     */     }
/*     */     
/* 576 */     synchronized (this)
/*     */     {
/* 578 */       long elapsed = SystemTime.getMonotonousTime() - toRelease.worker.run_start_time;
/* 579 */       if ((elapsed <= 10000L) || 
/*     */       
/*     */ 
/* 582 */         (!this.busy.remove(toRelease.worker)))
/*     */       {
/* 584 */         throw new IllegalStateException("task already released");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 590 */       if ((this.busy.size() == 0) && (!debug_thread_pool))
/*     */       {
/* 592 */         synchronized (busy_pools)
/*     */         {
/* 594 */           busy_pools.remove(this);
/*     */         }
/*     */       }
/*     */       
/* 598 */       if (this.busy.size() == 0)
/*     */       {
/* 600 */         if (this.reserved_target > this.reserved_actual)
/*     */         {
/* 602 */           this.reserved_actual += 1;
/*     */         }
/*     */         else
/*     */         {
/* 606 */           this.thread_sem.release();
/*     */         }
/*     */       }
/*     */       else {
/* 610 */         new threadPoolWorker();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void registerThreadAsChild(threadPoolWorker parent)
/*     */   {
/* 618 */     if ((tls.get() == null) || (tls.get() == parent)) {
/* 619 */       tls.set(parent);
/*     */     } else {
/* 621 */       throw new IllegalStateException("another parent is already set for this thread");
/*     */     }
/*     */   }
/*     */   
/*     */   public void deregisterThreadAsChild(threadPoolWorker parent) {
/* 626 */     if (tls.get() == parent) {
/* 627 */       tls.set(null);
/*     */     } else {
/* 629 */       throw new IllegalStateException("tls is not set to parent");
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int getQueueSize()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 461	org/gudy/azureus2/core3/util/ThreadPool:task_queue	Ljava/util/List;
/*     */     //   8: invokeinterface 534 1 0
/*     */     //   13: aload_1
/*     */     //   14: monitorexit
/*     */     //   15: ireturn
/*     */     //   16: astore_2
/*     */     //   17: aload_1
/*     */     //   18: monitorexit
/*     */     //   19: aload_2
/*     */     //   20: athrow
/*     */     // Line number table:
/*     */     //   Java source line #372	-> byte code offset #0
/*     */     //   Java source line #374	-> byte code offset #4
/*     */     //   Java source line #375	-> byte code offset #16
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	21	0	this	ThreadPool
/*     */     //   2	16	1	Ljava/lang/Object;	Object
/*     */     //   16	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	15	16	finally
/*     */     //   16	19	16	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean isQueued(AERunnable task)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 461	org/gudy/azureus2/core3/util/ThreadPool:task_queue	Ljava/util/List;
/*     */     //   8: aload_1
/*     */     //   9: invokeinterface 538 2 0
/*     */     //   14: aload_2
/*     */     //   15: monitorexit
/*     */     //   16: ireturn
/*     */     //   17: astore_3
/*     */     //   18: aload_2
/*     */     //   19: monitorexit
/*     */     //   20: aload_3
/*     */     //   21: athrow
/*     */     // Line number table:
/*     */     //   Java source line #379	-> byte code offset #0
/*     */     //   Java source line #381	-> byte code offset #4
/*     */     //   Java source line #382	-> byte code offset #17
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	22	0	this	ThreadPool
/*     */     //   0	22	1	task	AERunnable
/*     */     //   2	17	2	Ljava/lang/Object;	Object
/*     */     //   17	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	16	17	finally
/*     */     //   17	20	17	finally
/*     */   }
/*     */   
/*     */   class threadPoolWorker
/*     */     extends AEThread2
/*     */   {
/*     */     private final String worker_name;
/*     */     private volatile AERunnable runnable;
/*     */     private long run_start_time;
/*     */     private int warn_count;
/* 638 */     private String state = "<none>";
/*     */     
/*     */     protected threadPoolWorker()
/*     */     {
/* 642 */       super(true);
/* 643 */       ThreadPool.access$608(ThreadPool.this);
/* 644 */       setPriority(ThreadPool.this.thread_priority);
/* 645 */       this.worker_name = getName();
/* 646 */       start();
/*     */     }
/*     */     
/*     */     public void run() {
/* 650 */       ThreadPool.tls.set(this);
/*     */       
/* 652 */       boolean autoRelease = true;
/*     */       try
/*     */       {
/*     */         label734:
/*     */         label737:
/*     */         do
/*     */         {
/*     */           try {
/* 660 */             synchronized (ThreadPool.this)
/*     */             {
/* 662 */               if (ThreadPool.this.task_queue.size() > 0) {
/* 663 */                 this.runnable = ((AERunnable)ThreadPool.this.task_queue.remove(0));
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
/*     */               }
/*     */               else
/*     */               {
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
/* 740 */                 if (!autoRelease)
/*     */                   break;
/* 742 */                 synchronized (ThreadPool.this)
/*     */                 {
/* 744 */                   long elapsed = SystemTime.getMonotonousTime() - this.run_start_time;
/* 745 */                   if (elapsed > 10000L) {}
/*     */                   
/*     */ 
/* 748 */                   ThreadPool.this.busy.remove(this);
/*     */                   
/*     */ 
/*     */ 
/* 752 */                   if ((ThreadPool.this.busy.size() == 0) && (!ThreadPool.debug_thread_pool))
/* 753 */                     synchronized (ThreadPool.busy_pools)
/*     */                     {
/* 755 */                       ThreadPool.busy_pools.remove(ThreadPool.this);
/*     */                     } }
/* 757 */                 break;
/*     */               }
/*     */             }
/* 668 */             synchronized (ThreadPool.this)
/*     */             {
/* 670 */               this.run_start_time = SystemTime.getMonotonousTime();
/* 671 */               this.warn_count = 0;
/* 672 */               ThreadPool.this.busy.add(this);
/* 673 */               ThreadPool.access$808(ThreadPool.this);
/* 674 */               if (ThreadPool.this.busy.size() == 1)
/*     */               {
/* 676 */                 synchronized (ThreadPool.busy_pools)
/*     */                 {
/* 678 */                   if (!ThreadPool.busy_pools.contains(ThreadPool.this))
/*     */                   {
/* 680 */                     ThreadPool.busy_pools.add(ThreadPool.this);
/* 681 */                     if (!ThreadPool.busy_pool_timer_set)
/*     */                     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 687 */                       COConfigurationManager.addAndFireParameterListeners(new String[] { "debug.threadpool.log.enable", "debug.threadpool.debug.trace" }, new ParameterListener()
/*     */                       {
/*     */                         public void parameterChanged(String name) {
/* 690 */                           ThreadPool.access$1002(COConfigurationManager.getBooleanParameter("debug.threadpool.log.enable", false));
/* 691 */                           ThreadPool.access$1102(COConfigurationManager.getBooleanParameter("debug.threadpool.debug.trace", false));
/*     */                         }
/* 693 */                       });
/* 694 */                       ThreadPool.access$902(true);
/* 695 */                       SimpleTimer.addPeriodicEvent("ThreadPool:timeout", 10000L, new TimerEventPerformer()
/*     */                       {
/*     */                         public void perform(TimerEvent event) {}
/*     */                       });
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 707 */             if ((this.runnable instanceof ThreadPoolTask))
/*     */             {
/* 709 */               ThreadPoolTask tpt = (ThreadPoolTask)this.runnable;
/* 710 */               tpt.worker = this;
/* 711 */               String task_name = ThreadPool.NAME_THREADS ? tpt.getName() : null;
/*     */               try
/*     */               {
/* 714 */                 if (task_name != null)
/* 715 */                   setName(this.worker_name + "{" + task_name + "}");
/* 716 */                 tpt.taskStarted();
/* 717 */                 ThreadPool.this.runIt(this.runnable);
/*     */                 
/*     */ 
/* 720 */                 if (task_name != null) {
/* 721 */                   setName(this.worker_name);
/*     */                 }
/* 723 */                 if (tpt.isAutoReleaseAndAllowManual()) {
/* 724 */                   tpt.taskCompleted();
/*     */                   break label737;
/*     */                 }
/* 727 */                 autoRelease = false;
/*     */               }
/*     */               finally
/*     */               {
/*     */                 long elapsed;
/* 720 */                 if (task_name != null) {
/* 721 */                   setName(this.worker_name);
/*     */                 }
/* 723 */                 if (tpt.isAutoReleaseAndAllowManual()) {
/* 724 */                   tpt.taskCompleted();
/*     */                   break label734;
/*     */                 }
/* 727 */                 autoRelease = false;
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
/* 740 */                 if (!autoRelease) break;
/*     */               }
/* 742 */               synchronized (ThreadPool.this)
/*     */               {
/* 744 */                 long elapsed = SystemTime.getMonotonousTime() - this.run_start_time;
/* 745 */                 if (elapsed > 10000L) {}
/*     */                 
/*     */ 
/* 748 */                 ThreadPool.this.busy.remove(this);
/*     */                 
/*     */ 
/*     */ 
/* 752 */                 if ((ThreadPool.this.busy.size() == 0) && (!ThreadPool.debug_thread_pool))
/* 753 */                   synchronized (ThreadPool.busy_pools)
/*     */                   {
/* 755 */                     ThreadPool.busy_pools.remove(ThreadPool.this);
/*     */                   } }
/* 757 */               break; throw ((Throwable)localObject8);
/*     */             }
/*     */             else
/*     */             {
/* 733 */               ThreadPool.this.runIt(this.runnable);
/*     */             }
/*     */           } catch (Throwable e) {
/*     */             long elapsed;
/* 737 */             DebugLight.printStackTrace(e);
/*     */           } finally {
/*     */             long elapsed;
/* 740 */             if (autoRelease)
/*     */             {
/* 742 */               synchronized (ThreadPool.this)
/*     */               {
/* 744 */                 long elapsed = SystemTime.getMonotonousTime() - this.run_start_time;
/* 745 */                 if (elapsed > 10000L) {}
/*     */                 
/*     */ 
/* 748 */                 ThreadPool.this.busy.remove(this);
/*     */                 
/*     */ 
/*     */ 
/* 752 */                 if ((ThreadPool.this.busy.size() == 0) && (!ThreadPool.debug_thread_pool))
/* 753 */                   synchronized (ThreadPool.busy_pools)
/*     */                   {
/* 755 */                     ThreadPool.busy_pools.remove(ThreadPool.this);
/*     */                   }
/*     */               }
/*     */             }
/*     */           }
/* 760 */         } while (this.runnable != null);
/*     */       }
/*     */       catch (Throwable e) {
/* 763 */         DebugLight.printStackTrace(e);
/*     */       }
/*     */       finally {
/* 766 */         if (autoRelease)
/*     */         {
/* 768 */           synchronized (ThreadPool.this)
/*     */           {
/* 770 */             if (ThreadPool.this.reserved_target > ThreadPool.this.reserved_actual)
/*     */             {
/* 772 */               ThreadPool.access$1308(ThreadPool.this);
/*     */             }
/*     */             else
/*     */             {
/* 776 */               ThreadPool.this.thread_sem.release();
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 781 */         ThreadPool.tls.set(null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void setState(String _state)
/*     */     {
/* 787 */       this.state = _state;
/*     */     }
/*     */     
/*     */     public String getState() {
/* 791 */       return this.state;
/*     */     }
/*     */     
/*     */     protected String getWorkerName() {
/* 795 */       return this.worker_name;
/*     */     }
/*     */     
/*     */     protected ThreadPool getOwner() {
/* 799 */       return ThreadPool.this;
/*     */     }
/*     */     
/*     */     protected AERunnable getRunnable() {
/* 803 */       return this.runnable;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ThreadPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */