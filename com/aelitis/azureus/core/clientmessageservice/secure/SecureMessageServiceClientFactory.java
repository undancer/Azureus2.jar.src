/*    */ package com.aelitis.azureus.core.clientmessageservice.secure;
/*    */ 
/*    */ import com.aelitis.azureus.core.clientmessageservice.secure.impl.SecureMessageServiceClientImpl;
/*    */ import java.security.interfaces.RSAPublicKey;
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
/*    */ 
/*    */ public class SecureMessageServiceClientFactory
/*    */ {
/*    */   public static SecureMessageServiceClient create(String host, int port, int timeout_secs, RSAPublicKey key, SecureMessageServiceClientAdapter adapter)
/*    */   {
/* 42 */     return new SecureMessageServiceClientImpl(host, port, timeout_secs, key, adapter);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/secure/SecureMessageServiceClientFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */