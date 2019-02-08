/*    */ package com.aelitis.azureus.core.peermanager.messaging.azureus;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
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
/*    */ public class AZMessageEncoder
/*    */   implements MessageStreamEncoder
/*    */ {
/*    */   public static final int PADDING_MODE_NONE = 0;
/*    */   public static final int PADDING_MODE_NORMAL = 1;
/*    */   public static final int PADDING_MODE_MINIMAL = 2;
/*    */   private final int padding_mode;
/*    */   
/*    */   public AZMessageEncoder(int _padding_mode)
/*    */   {
/* 48 */     this.padding_mode = _padding_mode;
/*    */   }
/*    */   
/*    */ 
/*    */   public RawMessage[] encodeMessage(Message message)
/*    */   {
/* 54 */     return new RawMessage[] { AZMessageFactory.createAZRawMessage(message, this.padding_mode) };
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZMessageEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */