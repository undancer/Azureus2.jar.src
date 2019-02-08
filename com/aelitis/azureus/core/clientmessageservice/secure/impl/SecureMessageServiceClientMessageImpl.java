/*    */ package com.aelitis.azureus.core.clientmessageservice.secure.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.clientmessageservice.secure.SecureMessageServiceClientMessage;
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
/*    */ 
/*    */ 
/*    */ public class SecureMessageServiceClientMessageImpl
/*    */   implements SecureMessageServiceClientMessage
/*    */ {
/*    */   private final SecureMessageServiceClientImpl service;
/*    */   private final Map request;
/*    */   private Map reply;
/*    */   private final Object client_data;
/*    */   private final String description;
/*    */   
/*    */   protected SecureMessageServiceClientMessageImpl(SecureMessageServiceClientImpl _service, Map _content, Object _data, String _description)
/*    */   {
/* 43 */     this.service = _service;
/* 44 */     this.request = _content;
/* 45 */     this.client_data = _data;
/* 46 */     this.description = _description;
/*    */   }
/*    */   
/*    */ 
/*    */   public Map getRequest()
/*    */   {
/* 52 */     return this.request;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected void setReply(Map _reply)
/*    */   {
/* 59 */     this.reply = _reply;
/*    */   }
/*    */   
/*    */ 
/*    */   public Map getReply()
/*    */   {
/* 65 */     return this.reply;
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getClientData()
/*    */   {
/* 71 */     return this.client_data;
/*    */   }
/*    */   
/*    */ 
/*    */   public void cancel()
/*    */   {
/* 77 */     this.service.cancel(this);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getString()
/*    */   {
/* 83 */     return this.description;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/secure/impl/SecureMessageServiceClientMessageImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */