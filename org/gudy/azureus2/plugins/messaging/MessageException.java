/*    */ package org.gudy.azureus2.plugins.messaging;
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
/*    */ public class MessageException
/*    */   extends Exception
/*    */ {
/*    */   public MessageException(String reason)
/*    */   {
/* 27 */     super(reason);
/*    */   }
/*    */   
/*    */   public MessageException(String reason, Throwable e) {
/* 31 */     super(reason, e);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/MessageException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */