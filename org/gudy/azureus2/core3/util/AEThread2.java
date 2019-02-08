/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AEThread2
/*     */ {
/*     */   public static final boolean TRACE_TIMES = false;
/*  31 */   private static final int MIN_RETAINED = Math.max(Runtime.getRuntime().availableProcessors(), 2);
/*  32 */   private static final int MAX_RETAINED = Math.max(MIN_RETAINED * 4, 16);
/*     */   
/*     */   private static final int THREAD_TIMEOUT_CHECK_PERIOD = 10000;
/*     */   
/*     */   private static final int THREAD_TIMEOUT = 60000;
/*  37 */   private static final LinkedList daemon_threads = new LinkedList();
/*     */   private static long last_timeout_check;
/*     */   
/*  40 */   private static final class JoinLock { volatile boolean released = false;
/*     */   }
/*     */   
/*     */ 
/*     */   private static long total_starts;
/*     */   
/*     */   private static long total_creates;
/*     */   
/*     */   private threadWrapper wrapper;
/*     */   
/*     */   private String name;
/*     */   
/*     */   private final boolean daemon;
/*  53 */   private int priority = 5;
/*  54 */   private volatile JoinLock lock = new JoinLock(null);
/*     */   
/*     */ 
/*     */ 
/*     */   public AEThread2(String _name)
/*     */   {
/*  60 */     this(_name, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AEThread2(String _name, boolean _daemon)
/*     */   {
/*  68 */     this.name = _name;
/*  69 */     this.daemon = _daemon;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start()
/*     */   {
/*  79 */     JoinLock currentLock = this.lock;
/*     */     
/*     */     JoinLock newLock;
/*  82 */     synchronized (currentLock)
/*     */     {
/*     */       JoinLock newLock;
/*  85 */       if (currentLock.released) {
/*  86 */         newLock = this.lock = new JoinLock(null);
/*     */       } else {
/*  88 */         newLock = currentLock;
/*     */       }
/*     */     }
/*  91 */     if (this.daemon)
/*     */     {
/*  93 */       synchronized (daemon_threads)
/*     */       {
/*  95 */         total_starts += 1L;
/*     */         
/*  97 */         if (daemon_threads.isEmpty())
/*     */         {
/*  99 */           total_creates += 1L;
/*     */           
/* 101 */           this.wrapper = new threadWrapper(this.name, true);
/*     */         }
/*     */         else
/*     */         {
/* 105 */           this.wrapper = ((threadWrapper)daemon_threads.removeLast());
/*     */           
/* 107 */           this.wrapper.setName(this.name);
/*     */         }
/*     */         
/*     */       }
/*     */     } else {
/* 112 */       this.wrapper = new threadWrapper(this.name, false);
/*     */     }
/*     */     
/* 115 */     if (this.priority != this.wrapper.getPriority())
/*     */     {
/* 117 */       this.wrapper.setPriority(this.priority);
/*     */     }
/*     */     
/* 120 */     this.wrapper.currentLock = newLock;
/*     */     
/* 122 */     this.wrapper.start(this, this.name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPriority(int _priority)
/*     */   {
/* 129 */     this.priority = _priority;
/*     */     
/* 131 */     if (this.wrapper != null) {
/* 132 */       this.wrapper.setPriority(this.priority);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setName(String s)
/*     */   {
/* 140 */     this.name = s;
/*     */     
/* 142 */     if (this.wrapper != null)
/*     */     {
/* 144 */       this.wrapper.setName(this.name);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 151 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public void interrupt()
/*     */   {
/* 157 */     if (this.wrapper == null)
/*     */     {
/* 159 */       throw new IllegalStateException("Interrupted before started!");
/*     */     }
/*     */     
/*     */ 
/* 163 */     this.wrapper.interrupt();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isAlive()
/*     */   {
/* 169 */     return this.wrapper == null ? false : this.wrapper.isAlive();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCurrentThread()
/*     */   {
/* 175 */     return this.wrapper == Thread.currentThread();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 181 */     if (this.wrapper == null)
/*     */     {
/* 183 */       return this.name + " [daemon=" + this.daemon + ",priority=" + this.priority + "]";
/*     */     }
/*     */     
/*     */ 
/* 187 */     return this.wrapper.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract void run();
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isOurThread(Thread thread)
/*     */   {
/* 198 */     return AEThread.isOurThread(thread);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setOurThread() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setOurThread(Thread thread)
/*     */   {
/* 211 */     AEThread.setOurThread(thread);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setDebug(Object debug)
/*     */   {
/* 218 */     Thread current = Thread.currentThread();
/*     */     
/* 220 */     if ((current instanceof threadWrapper))
/*     */     {
/* 222 */       ((threadWrapper)current).setDebug(debug);
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
/*     */   public static Object[] getDebug(Thread t)
/*     */   {
/* 236 */     if ((t instanceof threadWrapper))
/*     */     {
/* 238 */       return ((threadWrapper)t).getDebug();
/*     */     }
/*     */     
/* 241 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class threadWrapper
/*     */     extends Thread
/*     */   {
/*     */     private AESemaphore2 sem;
/*     */     
/*     */     private AEThread2 target;
/*     */     
/*     */     private AEThread2.JoinLock currentLock;
/*     */     
/*     */     private long last_active_time;
/*     */     
/*     */     private Object[] debug;
/*     */     
/*     */ 
/*     */     protected threadWrapper(String name, boolean daemon)
/*     */     {
/* 261 */       super();
/*     */       
/* 263 */       setDaemon(daemon);
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       for (;;)
/*     */       {
/* 271 */         synchronized (this.currentLock)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 294 */             this.target.run();
/*     */ 
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 299 */             DebugLight.printStackTrace(e);
/*     */           }
/*     */           finally
/*     */           {
/* 303 */             this.target = null;
/*     */             
/* 305 */             this.debug = null;
/*     */             
/* 307 */             this.currentLock.released = true;
/*     */             
/* 309 */             this.currentLock.notifyAll();
/*     */           }
/*     */         }
/*     */         
/* 313 */         if ((!isInterrupted()) && (Thread.currentThread().isDaemon()))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 319 */           synchronized (AEThread2.daemon_threads)
/*     */           {
/* 321 */             this.last_active_time = SystemTime.getCurrentTime();
/*     */             
/* 323 */             if ((this.last_active_time < AEThread2.last_timeout_check) || (this.last_active_time - AEThread2.last_timeout_check > 10000L))
/*     */             {
/*     */ 
/* 326 */               AEThread2.access$302(this.last_active_time);
/*     */               
/* 328 */               while ((AEThread2.daemon_threads.size() > 0) && (AEThread2.daemon_threads.size() > AEThread2.MIN_RETAINED))
/*     */               {
/* 330 */                 threadWrapper thread = (threadWrapper)AEThread2.daemon_threads.getFirst();
/*     */                 
/* 332 */                 long thread_time = thread.last_active_time;
/*     */                 
/* 334 */                 if ((this.last_active_time >= thread_time) && (this.last_active_time - thread_time <= 60000L)) {
/*     */                   break;
/*     */                 }
/* 337 */                 AEThread2.daemon_threads.removeFirst();
/*     */                 
/* 339 */                 thread.retire();
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 348 */             if (AEThread2.daemon_threads.size() >= AEThread2.MAX_RETAINED)
/*     */             {
/* 350 */               return;
/*     */             }
/*     */             
/* 353 */             AEThread2.daemon_threads.addLast(this);
/*     */             
/* 355 */             setName("AEThread2:parked[" + AEThread2.daemon_threads.size() + "]");
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 360 */           this.sem.reserve();
/*     */           
/* 362 */           if (this.target == null) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void start(AEThread2 _target, String _name)
/*     */     {
/* 375 */       this.target = _target;
/*     */       
/* 377 */       setName(_name);
/*     */       
/* 379 */       if (this.sem == null)
/*     */       {
/* 381 */         this.sem = new AESemaphore2("AEThread2");
/*     */         
/* 383 */         super.start();
/*     */       }
/*     */       else
/*     */       {
/* 387 */         this.sem.release();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected void retire()
/*     */     {
/* 394 */       this.sem.release();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setDebug(Object d)
/*     */     {
/* 401 */       this.debug = new Object[] { d, Long.valueOf(SystemTime.getMonotonousTime()) };
/*     */     }
/*     */     
/*     */ 
/*     */     protected Object[] getDebug()
/*     */     {
/* 407 */       return this.debug;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void join()
/*     */   {
/* 414 */     JoinLock currentLock = this.lock;
/*     */     
/*     */ 
/*     */ 
/* 418 */     synchronized (currentLock)
/*     */     {
/*     */ 
/*     */ 
/* 422 */       while (!currentLock.released) {
/*     */         try
/*     */         {
/* 425 */           currentLock.wait();
/*     */         }
/*     */         catch (InterruptedException e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AEThread2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */