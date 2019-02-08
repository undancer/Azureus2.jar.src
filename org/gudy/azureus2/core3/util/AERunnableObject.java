/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AERunnableObject
/*    */   implements Runnable
/*    */ {
/*    */   private Object[] returnValueObject;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private AESemaphore sem;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 32 */   private String id = "AEReturningRunnable";
/*    */   
/*    */   public void run() {
/*    */     try {
/* 36 */       Object o = runSupport();
/* 37 */       if ((this.returnValueObject != null) && (this.returnValueObject.length > 0)) {
/* 38 */         this.returnValueObject[0] = o;
/*    */       }
/*    */     } catch (Throwable e) {
/* 41 */       Debug.out(this.id, e);
/*    */     } finally {
/* 43 */       if (this.sem != null) {
/* 44 */         this.sem.releaseForever();
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public void setupReturn(String ID, Object[] returnValueObject, AESemaphore sem) {
/* 50 */     this.id = ID;
/* 51 */     this.returnValueObject = returnValueObject;
/* 52 */     this.sem = sem;
/*    */   }
/*    */   
/*    */   public abstract Object runSupport();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AERunnableObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */