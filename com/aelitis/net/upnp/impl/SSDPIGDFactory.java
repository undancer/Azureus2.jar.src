/*    */ package com.aelitis.net.upnp.impl;
/*    */ 
/*    */ import com.aelitis.net.upnp.UPnPException;
/*    */ import com.aelitis.net.upnp.impl.ssdp.SSDPIGDImpl;
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
/*    */ public class SSDPIGDFactory
/*    */ {
/*    */   public static SSDPIGD create(UPnPImpl upnp, String[] selected_interfaces)
/*    */     throws UPnPException
/*    */   {
/* 41 */     return new SSDPIGDImpl(upnp, selected_interfaces);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/SSDPIGDFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */