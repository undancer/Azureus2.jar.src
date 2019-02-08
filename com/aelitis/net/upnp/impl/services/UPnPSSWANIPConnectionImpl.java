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
/*    */ public class UPnPSSWANIPConnectionImpl
/*    */   extends UPnPSSWANConnectionImpl
/*    */   implements UPnPWANIPConnection
/*    */ {
/*    */   protected UPnPSSWANIPConnectionImpl(UPnPServiceImpl _service)
/*    */   {
/* 38 */     super(_service);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getConnectionType()
/*    */   {
/* 44 */     return "IP";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/services/UPnPSSWANIPConnectionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */