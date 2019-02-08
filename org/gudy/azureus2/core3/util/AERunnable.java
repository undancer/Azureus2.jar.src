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
/*    */ public abstract class AERunnable
/*    */   implements Runnable
/*    */ {
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/* 35 */       runSupport();
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/* 39 */       DebugLight.printStackTrace(e);
/*    */     }
/*    */   }
/*    */   
/*    */   public abstract void runSupport();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AERunnable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */