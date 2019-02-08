/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimeLimitedTask
/*     */ {
/*     */   private final String name;
/*     */   private final int max_millis;
/*     */   private final int priority;
/*     */   private task t;
/*     */   
/*     */   public TimeLimitedTask(String _name, int _max_millis, int _priority, task _t)
/*     */   {
/*  40 */     this.name = _name;
/*  41 */     this.max_millis = _max_millis;
/*  42 */     this.priority = _priority;
/*  43 */     this.t = _t;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object run()
/*     */     throws Throwable
/*     */   {
/*  51 */     final Object[] result = { null };
/*     */     
/*  53 */     final AESemaphore sem = new AESemaphore(this.name);
/*     */     
/*  55 */     final Thread thread = new Thread(this.name)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/*  62 */           result[0] = TimeLimitedTask.this.t.run();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*  66 */           result[0] = e;
/*     */         }
/*     */         finally
/*     */         {
/*  70 */           TimeLimitedTask.this.t = null;
/*     */           
/*  72 */           sem.releaseForever();
/*     */         }
/*     */         
/*     */       }
/*  76 */     };
/*  77 */     DelayedEvent ev = new DelayedEvent(this.name, this.max_millis, new AERunnable()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/*     */ 
/*  86 */         if (!sem.isReleasedForever())
/*     */         {
/*  88 */           SESecurityManager.stopThread(thread);
/*     */         }
/*     */         
/*     */       }
/*  92 */     });
/*  93 */     thread.setPriority(this.priority);
/*     */     
/*  95 */     thread.setDaemon(true);
/*     */     
/*  97 */     thread.start();
/*     */     
/*  99 */     sem.reserve();
/*     */     
/* 101 */     ev.cancel();
/*     */     
/* 103 */     if ((result[0] instanceof Throwable))
/*     */     {
/* 105 */       throw ((Throwable)result[0]);
/*     */     }
/*     */     
/* 108 */     return result[0];
/*     */   }
/*     */   
/*     */   public static abstract interface task
/*     */   {
/*     */     public abstract Object run()
/*     */       throws Exception;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/TimeLimitedTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */