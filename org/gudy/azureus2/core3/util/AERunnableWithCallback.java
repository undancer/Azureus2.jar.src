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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AERunnableWithCallback
/*    */   implements Runnable
/*    */ {
/*    */   private final AECallback callback;
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
/*    */   public AERunnableWithCallback(AECallback callback)
/*    */   {
/* 34 */     this.callback = callback;
/*    */   }
/*    */   
/*    */   public final void run() {
/*    */     try {
/* 39 */       Object o = runSupport();
/* 40 */       if (this.callback != null) {
/* 41 */         this.callback.callbackSuccess(o);
/*    */       }
/*    */     } catch (Throwable e) {
/* 44 */       Debug.out(e);
/* 45 */       if (this.callback != null) {
/* 46 */         this.callback.callbackFailure(e);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public abstract Object runSupport();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AERunnableWithCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */