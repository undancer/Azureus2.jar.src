/*    */ package com.aelitis.net.upnp;
/*    */ 
/*    */ import com.aelitis.net.upnp.impl.UPnPImpl;
/*    */ import com.aelitis.net.upnp.impl.ssdp.SSDPCore;
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
/*    */ public class UPnPFactory
/*    */ {
/*    */   public static UPnP getSingleton(UPnPAdapter adapter, String[] selected_interfaces)
/*    */     throws UPnPException
/*    */   {
/* 41 */     return UPnPImpl.getSingleton(adapter, selected_interfaces);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static UPnPSSDP getSSDP(UPnPSSDPAdapter adapter, String group_address, int group_port, int control_port, String[] selected_interfaces)
/*    */     throws UPnPException
/*    */   {
/* 54 */     return SSDPCore.getSingleton(adapter, group_address, group_port, control_port, selected_interfaces);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */