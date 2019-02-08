/*    */ package com.aelitis.azureus.core.peermanager.messaging;
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
/*    */   public MessageException(String reason, Throwable cause) {
/* 31 */     super(reason, cause);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/MessageException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */