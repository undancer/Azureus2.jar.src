/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.locks.ReentrantLock;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AEMonitor2
/*    */ {
/* 33 */   final ReentrantLock lock = new ReentrantLock();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public AEMonitor2(String _name) {}
/*    */   
/*    */ 
/*    */ 
/*    */   public void enter()
/*    */   {
/* 44 */     this.lock.lock();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean enter(int max_millis)
/*    */   {
/*    */     try
/*    */     {
/* 57 */       if (this.lock.tryLock(max_millis, TimeUnit.MILLISECONDS))
/*    */       {
/* 59 */         return true;
/*    */       }
/*    */       
/*    */ 
/* 63 */       return false;
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 67 */       Debug.out(e);
/*    */     }
/* 69 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */   public void exit()
/*    */   {
/*    */     try
/*    */     {
/* 77 */       this.lock.unlock();
/*    */     }
/*    */     finally {}
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean isHeld()
/*    */   {
/* 87 */     return this.lock.isHeldByCurrentThread();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean hasWaiters()
/*    */   {
/* 93 */     return this.lock.getQueueLength() > 0;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AEMonitor2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */