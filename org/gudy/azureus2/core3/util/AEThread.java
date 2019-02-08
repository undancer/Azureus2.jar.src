/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ /**
/*     */  * @deprecated
/*     */  */
/*     */ public abstract class AEThread
/*     */   extends Thread
/*     */ {
/*  34 */   private static final WeakHashMap our_thread_map = new WeakHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   public AEThread(String name)
/*     */   {
/*  40 */     super(name);
/*     */     
/*  42 */     setDaemon(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AEThread(String name, boolean daemon)
/*     */   {
/*  50 */     super(name);
/*     */     
/*  52 */     setDaemon(daemon);
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
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/*  71 */       runSupport();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  75 */       DebugLight.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void runSupport();
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isOurThread(Thread thread)
/*     */   {
/*  88 */     if ((thread instanceof AEThread))
/*     */     {
/*  90 */       return true;
/*     */     }
/*     */     
/*  93 */     synchronized (our_thread_map)
/*     */     {
/*  95 */       return our_thread_map.get(thread) != null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void setOurThread()
/*     */   {
/* 102 */     setOurThread(Thread.currentThread());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setOurThread(Thread thread)
/*     */   {
/* 109 */     if (((thread instanceof AEThread)) || ((thread instanceof AEThread2.threadWrapper)))
/*     */     {
/* 111 */       return;
/*     */     }
/*     */     
/* 114 */     synchronized (our_thread_map)
/*     */     {
/* 116 */       our_thread_map.put(thread, "");
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AEThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */