/*    */ package com.aelitis.net.upnp.impl.services;
/*    */ 
/*    */ import com.aelitis.net.upnp.services.UPnPWANIPConnection;
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
/*    */ public class UPnPSSWANPPPConnectionImpl
/*    */   extends UPnPSSWANConnectionImpl
/*    */   implements UPnPWANIPConnection
/*    */ {
/*    */   protected UPnPSSWANPPPConnectionImpl(UPnPServiceImpl _service)
/*    */   {
/* 38 */     super(_service);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getConnectionType()
/*    */   {
/* 44 */     return "PPP";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/services/UPnPSSWANPPPConnectionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */