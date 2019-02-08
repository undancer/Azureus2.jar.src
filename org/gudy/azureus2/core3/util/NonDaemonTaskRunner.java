/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Stack;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NonDaemonTaskRunner
/*     */ {
/*     */   public static final int LINGER_PERIOD = 2500;
/*     */   protected static NonDaemonTaskRunner singleton;
/*  42 */   protected static final AEMonitor class_mon = new AEMonitor("NonDaemonTaskRunner:class");
/*     */   
/*  44 */   public NonDaemonTaskRunner() { this.tasks = new Stack();
/*  45 */     this.tasks_mon = new AEMonitor("NonDaemonTaskRunner:tasks");
/*  46 */     this.task_sem = new AESemaphore("NonDaemonTaskRunner");
/*     */     
/*  48 */     this.wait_until_idle_list = new ArrayList();
/*     */   }
/*     */   
/*     */ 
/*     */   protected static NonDaemonTaskRunner getSingleton()
/*     */   {
/*     */     try
/*     */     {
/*  56 */       class_mon.enter();
/*     */       
/*  58 */       if (singleton == null)
/*     */       {
/*  60 */         singleton = new NonDaemonTaskRunner();
/*     */       }
/*     */       
/*  63 */       return singleton;
/*     */     }
/*     */     finally
/*     */     {
/*  67 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected final Stack tasks;
/*     */   protected final AEMonitor tasks_mon;
/*     */   public static Object run(NonDaemonTask target)
/*     */     throws Throwable
/*     */   {
/*  77 */     return getSingleton().runSupport(target, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object runAsync(NonDaemonTask target)
/*     */     throws Throwable
/*     */   {
/*  86 */     return getSingleton().runSupport(target, true);
/*     */   }
/*     */   
/*     */ 
/*     */   protected final AESemaphore task_sem;
/*     */   
/*     */   protected final List wait_until_idle_list;
/*     */   
/*     */   protected AEThread2 current_thread;
/*     */   protected Object runSupport(NonDaemonTask target, boolean async)
/*     */     throws Throwable
/*     */   {
/*  98 */     if ((this.current_thread != null) && (this.current_thread.isCurrentThread()))
/*     */     {
/* 100 */       return target.run();
/*     */     }
/*     */     
/* 103 */     taskWrapper wrapper = new taskWrapper(target);
/*     */     try
/*     */     {
/* 106 */       this.tasks_mon.enter();
/*     */       
/* 108 */       this.tasks.push(wrapper);
/*     */       
/* 110 */       this.task_sem.release();
/*     */       
/* 112 */       if (this.current_thread == null)
/*     */       {
/* 114 */         final AESemaphore wait_sem = new AESemaphore("NonDaemonTaskRunnerTask: " + target.getName());
/*     */         
/*     */ 
/*     */ 
/* 118 */         this.current_thread = new AEThread2("NonDaemonTaskRunner", false)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 124 */             wait_sem.release();
/*     */             
/*     */ 
/*     */ 
/*     */             for (;;)
/*     */             {
/* 130 */               NonDaemonTaskRunner.this.task_sem.reserve(2500L);
/*     */               
/* 132 */               NonDaemonTaskRunner.taskWrapper t = null;
/*     */               try
/*     */               {
/* 135 */                 NonDaemonTaskRunner.this.tasks_mon.enter();
/*     */                 
/* 137 */                 if (NonDaemonTaskRunner.this.tasks.isEmpty())
/*     */                 {
/* 139 */                   NonDaemonTaskRunner.this.current_thread = null;
/*     */                   
/* 141 */                   for (int i = 0; i < NonDaemonTaskRunner.this.wait_until_idle_list.size(); i++)
/*     */                   {
/* 143 */                     ((AESemaphore)NonDaemonTaskRunner.this.wait_until_idle_list.get(i)).release();
/*     */                   }
/*     */                   
/* 146 */                   NonDaemonTaskRunner.this.wait_until_idle_list.clear();
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 156 */                   NonDaemonTaskRunner.this.tasks_mon.exit(); break;
/*     */                 }
/* 152 */                 t = (NonDaemonTaskRunner.taskWrapper)NonDaemonTaskRunner.this.tasks.pop();
/*     */               }
/*     */               finally
/*     */               {
/* 156 */                 NonDaemonTaskRunner.this.tasks_mon.exit();
/*     */               }
/*     */               
/* 159 */               t.run();
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */ 
/* 165 */         };
/* 166 */         this.current_thread.start();
/*     */         
/* 168 */         wait_sem.reserve();
/*     */       }
/*     */     }
/*     */     finally {
/* 172 */       this.tasks_mon.exit();
/*     */     }
/*     */     
/* 175 */     if (async)
/*     */     {
/* 177 */       return null;
/*     */     }
/*     */     
/* 180 */     return wrapper.waitForResult();
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class taskWrapper
/*     */   {
/*     */     protected final NonDaemonTask task;
/*     */     
/*     */     protected final AESemaphore sem;
/*     */     
/*     */     protected Object result;
/*     */     
/*     */     protected Throwable exception;
/*     */     
/*     */     protected taskWrapper(NonDaemonTask _task)
/*     */     {
/* 196 */       this.task = _task;
/* 197 */       this.sem = new AESemaphore("NonDaemonTaskRunner::taskWrapper");
/*     */     }
/*     */     
/*     */     protected void run()
/*     */     {
/*     */       try
/*     */       {
/* 204 */         this.result = this.task.run();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 208 */         this.exception = e;
/*     */       }
/*     */       finally
/*     */       {
/* 212 */         this.sem.release();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected Object waitForResult()
/*     */       throws Throwable
/*     */     {
/* 221 */       this.sem.reserve();
/*     */       
/* 223 */       if (this.exception != null)
/*     */       {
/* 225 */         throw this.exception;
/*     */       }
/*     */       
/* 228 */       return this.result;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void waitUntilIdle()
/*     */   {
/* 235 */     getSingleton().waitUntilIdleSupport();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void waitUntilIdleSupport()
/*     */   {
/*     */     AESemaphore sem;
/*     */     try
/*     */     {
/* 244 */       this.tasks_mon.enter();
/*     */       
/* 246 */       if (this.current_thread == null) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 251 */       sem = new AESemaphore("NDTR::idleWaiter");
/*     */       
/* 253 */       this.wait_until_idle_list.add(sem);
/*     */     }
/*     */     finally
/*     */     {
/* 257 */       this.tasks_mon.exit();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 262 */     while (!sem.reserve(10000L))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 267 */       if (Logger.isEnabled()) {
/*     */         try
/*     */         {
/* 270 */           this.tasks_mon.enter();
/*     */           
/* 272 */           for (int i = 0; i < this.wait_until_idle_list.size(); i++)
/*     */           {
/* 274 */             AESemaphore pending = (AESemaphore)this.wait_until_idle_list.get(i);
/*     */             
/* 276 */             if (pending != sem)
/*     */             {
/* 278 */               Logger.log(new LogEvent(LogIDs.CORE, "Waiting for " + pending.getName() + " to complete"));
/*     */             }
/*     */           }
/*     */         }
/*     */         finally {
/* 283 */           this.tasks_mon.exit();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/NonDaemonTaskRunner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */