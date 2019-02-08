/*    */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*    */ 
/*    */ import org.gudy.azureus2.pluginsimpl.local.network.RawMessageAdapter;
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
/*    */ public class MessageStreamEncoderAdapter
/*    */   implements com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder
/*    */ {
/*    */   private final org.gudy.azureus2.plugins.messaging.MessageStreamEncoder plug_encoder;
/*    */   
/*    */   public MessageStreamEncoderAdapter(org.gudy.azureus2.plugins.messaging.MessageStreamEncoder plug_encoder)
/*    */   {
/* 37 */     this.plug_encoder = plug_encoder;
/*    */   }
/*    */   
/*    */   public com.aelitis.azureus.core.networkmanager.RawMessage[] encodeMessage(com.aelitis.azureus.core.peermanager.messaging.Message message) {
/*    */     org.gudy.azureus2.plugins.messaging.Message plug_msg;
/*    */     org.gudy.azureus2.plugins.messaging.Message plug_msg;
/* 43 */     if ((message instanceof MessageAdapter)) {
/* 44 */       plug_msg = ((MessageAdapter)message).getPluginMessage();
/*    */     }
/*    */     else {
/* 47 */       plug_msg = new MessageAdapter(message);
/*    */     }
/*    */     
/* 50 */     org.gudy.azureus2.plugins.network.RawMessage raw_plug = this.plug_encoder.encodeMessage(plug_msg);
/* 51 */     return new com.aelitis.azureus.core.networkmanager.RawMessage[] { new RawMessageAdapter(raw_plug) };
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/MessageStreamEncoderAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */