/*    */ package org.gudy.azureus2.pluginsimpl.local.utils;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.AESemaphore;
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.utils.Semaphore;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SemaphoreImpl
/*    */   implements Semaphore
/*    */ {
/*    */   private static long next_sem_id;
/*    */   private AESemaphore sem;
/*    */   
/*    */   protected SemaphoreImpl(PluginInterface pi)
/*    */   {
/* 46 */     synchronized (SemaphoreImpl.class)
/*    */     {
/* 48 */       this.sem = new AESemaphore("Plugin " + pi.getPluginID() + ":" + next_sem_id++);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void reserve()
/*    */   {
/* 55 */     this.sem.reserve();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean reserveIfAvailable()
/*    */   {
/* 61 */     return this.sem.reserveIfAvailable();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean reserve(long timeout_millis)
/*    */   {
/* 68 */     return this.sem.reserve(timeout_millis);
/*    */   }
/*    */   
/*    */ 
/*    */   public void release()
/*    */   {
/* 74 */     this.sem.release();
/*    */   }
/*    */   
/*    */   public void releaseAllWaiters()
/*    */   {
/* 79 */     this.sem.releaseAllWaiters();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/SemaphoreImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */