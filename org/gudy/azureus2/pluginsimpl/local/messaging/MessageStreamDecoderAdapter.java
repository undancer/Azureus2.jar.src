/*    */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.Transport;
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import org.gudy.azureus2.pluginsimpl.local.network.TransportImpl;
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
/*    */ public class MessageStreamDecoderAdapter
/*    */   implements com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder
/*    */ {
/*    */   private final org.gudy.azureus2.plugins.messaging.MessageStreamDecoder plug_decoder;
/*    */   
/*    */   public MessageStreamDecoderAdapter(org.gudy.azureus2.plugins.messaging.MessageStreamDecoder plug_decoder)
/*    */   {
/* 40 */     this.plug_decoder = plug_decoder;
/*    */   }
/*    */   
/*    */   public int performStreamDecode(Transport transport, int max_bytes) throws IOException
/*    */   {
/* 45 */     return this.plug_decoder.performStreamDecode(new TransportImpl(transport), max_bytes);
/*    */   }
/*    */   
/*    */   public int getPercentDoneOfCurrentMessage()
/*    */   {
/* 50 */     return -1;
/*    */   }
/*    */   
/*    */   public com.aelitis.azureus.core.peermanager.messaging.Message[] removeDecodedMessages()
/*    */   {
/* 55 */     org.gudy.azureus2.plugins.messaging.Message[] plug_msgs = this.plug_decoder.removeDecodedMessages();
/*    */     
/* 57 */     if ((plug_msgs == null) || (plug_msgs.length < 1)) {
/* 58 */       return null;
/*    */     }
/*    */     
/* 61 */     com.aelitis.azureus.core.peermanager.messaging.Message[] core_msgs = new com.aelitis.azureus.core.peermanager.messaging.Message[plug_msgs.length];
/*    */     
/* 63 */     for (int i = 0; i < plug_msgs.length; i++) {
/* 64 */       core_msgs[i] = new MessageAdapter(plug_msgs[i]);
/*    */     }
/*    */     
/* 67 */     return core_msgs;
/*    */   }
/*    */   
/* 70 */   public int getProtocolBytesDecoded() { return this.plug_decoder.getProtocolBytesDecoded(); }
/*    */   
/* 72 */   public int getDataBytesDecoded() { return this.plug_decoder.getDataBytesDecoded(); }
/*    */   
/* 74 */   public void pauseDecoding() { this.plug_decoder.pauseDecoding(); }
/*    */   
/* 76 */   public void resumeDecoding() { this.plug_decoder.resumeDecoding(); }
/*    */   
/* 78 */   public ByteBuffer destroy() { return this.plug_decoder.destroy(); }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/MessageStreamDecoderAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */