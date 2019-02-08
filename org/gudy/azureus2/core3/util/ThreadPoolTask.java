/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ThreadPoolTask
/*     */   extends AERunnable
/*     */ {
/*     */   static final int RELEASE_AUTO = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final int RELEASE_MANUAL = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final int RELEASE_MANUAL_ALLOWED = 2;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int manualRelease;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ThreadPool.threadPoolWorker worker;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTaskState(String state)
/*     */   {
/*  43 */     this.worker.setState(state);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTaskState()
/*     */   {
/*  49 */     return this.worker == null ? "" : this.worker.getState();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  55 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void interruptTask();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void taskStarted() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void taskCompleted() {}
/*     */   
/*     */ 
/*     */ 
/*     */   final synchronized void join()
/*     */   {
/*  77 */     while (this.manualRelease != 0)
/*     */     {
/*     */       try
/*     */       {
/*  81 */         wait();
/*     */       }
/*     */       catch (Exception e) {
/*  84 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   final synchronized void setManualRelease()
/*     */   {
/*  92 */     this.manualRelease = 1;
/*     */   }
/*     */   
/*     */ 
/*     */   final synchronized boolean canManualRelease()
/*     */   {
/*  98 */     return this.manualRelease == 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final synchronized boolean isAutoReleaseAndAllowManual()
/*     */   {
/* 107 */     if (this.manualRelease == 1)
/* 108 */       this.manualRelease = 2;
/* 109 */     return this.manualRelease == 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public final synchronized void releaseToPool()
/*     */   {
/* 115 */     if (this.manualRelease == 1) {
/* 116 */       this.manualRelease = 0;
/* 117 */     } else if (this.manualRelease == 2)
/*     */     {
/* 119 */       taskCompleted();
/* 120 */       this.worker.getOwner().releaseManual(this);
/* 121 */       this.manualRelease = 0;
/* 122 */     } else if (this.manualRelease == 0) {
/* 123 */       Debug.out("this should not happen");
/*     */     }
/* 125 */     notifyAll();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ThreadPoolTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */