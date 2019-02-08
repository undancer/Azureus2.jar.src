/*    */ package com.aelitis.azureus.core.clientmessageservice;
/*    */ 
/*    */ import com.aelitis.azureus.core.clientmessageservice.impl.AEClientService;
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
/*    */ public class ClientMessageServiceClient
/*    */ {
/*    */   public static ClientMessageService getServerService(String server_address, int server_port, int timeout_secs, String msg_type_id)
/*    */   {
/* 37 */     return new AEClientService(server_address, server_port, timeout_secs, msg_type_id);
/*    */   }
/*    */   
/*    */   public static ClientMessageService getServerService(String server_address, int server_port, String msg_type_id) {
/* 41 */     return new AEClientService(server_address, server_port, msg_type_id);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/ClientMessageServiceClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */