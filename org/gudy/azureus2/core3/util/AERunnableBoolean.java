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
/*    */ public abstract class AERunnableBoolean
/*    */   implements Runnable
/*    */ {
/*    */   private Boolean[] returnValueObject;
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
/*    */   public void run()
/*    */   {
/*    */     try {
/* 37 */       boolean b = runSupport();
/*    */       
/* 39 */       if ((this.returnValueObject != null) && (this.returnValueObject.length > 0)) {
/* 40 */         this.returnValueObject[0] = Boolean.valueOf(b);
/*    */       }
/*    */     } catch (Throwable e) {
/* 43 */       Debug.out(this.id, e);
/*    */     }
/*    */     finally {
/* 46 */       if (this.sem != null)
/*    */       {
/* 48 */         this.sem.releaseForever();
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public void setupReturn(String ID, Boolean[] returnValueObject, AESemaphore sem)
/*    */   {
/* 55 */     this.id = ID;
/* 56 */     this.returnValueObject = returnValueObject;
/* 57 */     this.sem = sem;
/*    */   }
/*    */   
/*    */   public abstract boolean runSupport();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AERunnableBoolean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */