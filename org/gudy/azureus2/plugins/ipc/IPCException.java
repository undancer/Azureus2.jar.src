/*    */ package org.gudy.azureus2.plugins.ipc;
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
/*    */ public class IPCException
/*    */   extends Exception
/*    */ {
/*    */   public IPCException() {}
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
/*    */   public IPCException(String message, Throwable cause)
/*    */   {
/* 32 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public IPCException(String message) {
/* 36 */     super(message);
/*    */   }
/*    */   
/*    */   public IPCException(Throwable cause) {
/* 40 */     super(cause);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ipc/IPCException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */