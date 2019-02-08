/*    */ package com.aelitis.azureus.core.clientmessageservice.impl;
/*    */ 
/*    */ import java.util.Map;
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
/*    */ public class ClientMessage
/*    */ {
/*    */   private final String message_id;
/*    */   private final ClientConnection client;
/*    */   private final Map payload;
/*    */   private ClientMessageHandler handler;
/*    */   private boolean outcome_reported;
/*    */   
/*    */   public ClientMessage(String msg_id, ClientConnection _client, Map msg_payload, ClientMessageHandler _handler)
/*    */   {
/* 39 */     this.message_id = msg_id;
/* 40 */     this.client = _client;
/* 41 */     this.payload = msg_payload;
/* 42 */     this.handler = _handler;
/*    */   }
/*    */   
/*    */ 
/* 46 */   public String getMessageID() { return this.message_id; }
/*    */   
/* 48 */   public ClientConnection getClient() { return this.client; }
/*    */   
/* 50 */   public Map getPayload() { return this.payload; }
/*    */   
/* 52 */   public ClientMessageHandler getHandler() { return this.handler; }
/*    */   
/* 54 */   public void setHandler(ClientMessageHandler new_handler) { this.handler = new_handler; }
/*    */   
/*    */ 
/*    */   public void reportComplete()
/*    */   {
/* 59 */     synchronized (this) {
/* 60 */       if (this.outcome_reported)
/*    */       {
/* 62 */         return;
/*    */       }
/*    */       
/* 65 */       this.outcome_reported = true;
/*    */     }
/*    */     
/* 68 */     this.handler.sendAttemptCompleted(this);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void reportFailed(Throwable error)
/*    */   {
/* 75 */     synchronized (this) {
/* 76 */       if (this.outcome_reported)
/*    */       {
/* 78 */         return;
/*    */       }
/*    */       
/* 81 */       this.outcome_reported = true;
/*    */     }
/*    */     
/* 84 */     this.handler.sendAttemptFailed(this, error);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/impl/ClientMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */