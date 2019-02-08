/*    */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
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
/*    */ public class BTLTMessage
/*    */   implements BTMessage
/*    */ {
/*    */   public final byte extension_id;
/*    */   public final Message base_message;
/*    */   public DirectByteBuffer buffer_header;
/*    */   
/*    */   public BTLTMessage(Message base_message, byte extension_id)
/*    */   {
/* 37 */     this.base_message = base_message;
/* 38 */     this.extension_id = extension_id;
/*    */   }
/*    */   
/*    */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*    */   {
/* 43 */     throw new MessageException("BTLTMessage cannot be used for message deserialization!");
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 47 */     if (this.base_message != null) this.base_message.destroy();
/* 48 */     if (this.buffer_header != null) {
/* 49 */       this.buffer_header.returnToPool();
/* 50 */       this.buffer_header = null;
/*    */     }
/*    */   }
/*    */   
/*    */   public DirectByteBuffer[] getData() {
/* 55 */     DirectByteBuffer[] orig_data = this.base_message.getData();
/* 56 */     DirectByteBuffer[] new_data = new DirectByteBuffer[orig_data.length + 1];
/*    */     
/* 58 */     if (this.buffer_header == null) {
/* 59 */       this.buffer_header = DirectByteBufferPool.getBuffer((byte)27, 1);
/* 60 */       this.buffer_header.put((byte)11, this.extension_id);
/* 61 */       this.buffer_header.flip((byte)11);
/*    */     }
/*    */     
/* 64 */     new_data[0] = this.buffer_header;
/* 65 */     System.arraycopy(orig_data, 0, new_data, 1, orig_data.length);
/* 66 */     return new_data;
/*    */   }
/*    */   
/*    */   public String getDescription() {
/* 70 */     return this.base_message.getDescription();
/*    */   }
/*    */   
/*    */   public String getFeatureID() {
/* 74 */     return this.base_message.getFeatureID();
/*    */   }
/*    */   
/*    */   public int getFeatureSubID() {
/* 78 */     return this.base_message.getFeatureSubID();
/*    */   }
/*    */   
/*    */   public String getID() {
/* 82 */     return "BT_LT_EXT_MESSAGE";
/*    */   }
/*    */   
/*    */   public byte[] getIDBytes() {
/* 86 */     return BTMessage.ID_BT_LT_EXT_MESSAGE_BYTES;
/*    */   }
/*    */   
/*    */   public int getType() {
/* 90 */     return 0;
/*    */   }
/*    */   
/*    */   public byte getVersion() {
/* 94 */     return 0;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTLTMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */