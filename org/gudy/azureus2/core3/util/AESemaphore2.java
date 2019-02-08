/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ import java.util.concurrent.Semaphore;
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
/*    */ public class AESemaphore2
/*    */ {
/* 28 */   private final Semaphore sem = new Semaphore(0);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public AESemaphore2(String name) {}
/*    */   
/*    */ 
/*    */ 
/*    */   public void reserve()
/*    */   {
/* 39 */     this.sem.acquireUninterruptibly();
/*    */   }
/*    */   
/*    */ 
/*    */   public void release()
/*    */   {
/* 45 */     this.sem.release();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AESemaphore2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */