/*    */ package com.aelitis.azureus.core.networkmanager.impl.http;
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
/*    */ public class HTTPMessageEncoder
/*    */   implements MessageStreamEncoder
/*    */ {
/*    */   private HTTPNetworkConnection http_connection;
/*    */   
/*    */   public void setConnection(HTTPNetworkConnection _http_connection)
/*    */   {
/* 37 */     this.http_connection = _http_connection;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public RawMessage[] encodeMessage(Message message)
/*    */   {
/* 44 */     String id = message.getID();
/*    */     
/*    */ 
/*    */ 
/* 48 */     RawMessage raw_message = null;
/*    */     
/* 50 */     if (id.equals("BT_HANDSHAKE"))
/*    */     {
/* 52 */       raw_message = this.http_connection.encodeHandShake(message);
/*    */     }
/* 54 */     else if (id.equals("BT_CHOKE"))
/*    */     {
/* 56 */       raw_message = this.http_connection.encodeChoke();
/*    */     }
/* 58 */     else if (id.equals("BT_UNCHOKE"))
/*    */     {
/* 60 */       raw_message = this.http_connection.encodeUnchoke();
/*    */     }
/* 62 */     else if (id.equals("BT_BITFIELD"))
/*    */     {
/* 64 */       raw_message = this.http_connection.encodeBitField();
/*    */     } else {
/* 66 */       if (id.equals("BT_PIECE"))
/*    */       {
/* 68 */         return this.http_connection.encodePiece(message);
/*    */       }
/* 70 */       if (id.equals("HTTP_DATA"))
/*    */       {
/* 72 */         raw_message = ((HTTPMessage)message).encode(message);
/*    */       }
/*    */     }
/* 75 */     if (raw_message == null)
/*    */     {
/* 77 */       raw_message = this.http_connection.getEmptyRawMessage(message);
/*    */     }
/*    */     
/* 80 */     return new RawMessage[] { raw_message };
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/http/HTTPMessageEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */