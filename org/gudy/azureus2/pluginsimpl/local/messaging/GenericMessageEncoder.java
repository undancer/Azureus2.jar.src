/*    */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*    */ import com.aelitis.azureus.core.networkmanager.impl.RawMessageImpl;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*    */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*    */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
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
/*    */ public class GenericMessageEncoder
/*    */   implements MessageStreamEncoder
/*    */ {
/*    */   public RawMessage[] encodeMessage(Message _message)
/*    */   {
/* 38 */     GenericMessage message = (GenericMessage)_message;
/*    */     
/* 40 */     DirectByteBuffer payload = message.getPayload();
/*    */     
/* 42 */     if (message.isAlreadyEncoded())
/*    */     {
/* 44 */       return new RawMessage[] { new RawMessageImpl(message, new DirectByteBuffer[] { payload }, 1, true, new Message[0]) };
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 55 */     DirectByteBuffer header = DirectByteBufferPool.getBuffer((byte)1, 4);
/*    */     
/* 57 */     header.putInt((byte)11, payload.remaining((byte)11));
/*    */     
/* 59 */     header.flip((byte)11);
/*    */     
/* 61 */     return new RawMessage[] { new RawMessageImpl(message, new DirectByteBuffer[] { header, payload }, 1, true, new Message[0]) };
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/GenericMessageEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */