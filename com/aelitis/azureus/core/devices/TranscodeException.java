/*    */ package com.aelitis.azureus.core.devices;
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
/*    */ public class TranscodeException
/*    */   extends Exception
/*    */ {
/* 29 */   private boolean disable_retry = false;
/*    */   
/*    */ 
/*    */ 
/*    */   public TranscodeException(String str)
/*    */   {
/* 35 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public TranscodeException(String str, Throwable e)
/*    */   {
/* 43 */     super(str, e);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setDisableRetry(boolean b)
/*    */   {
/* 50 */     this.disable_retry = b;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isRetryDisabled()
/*    */   {
/* 56 */     return this.disable_retry;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/TranscodeException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */