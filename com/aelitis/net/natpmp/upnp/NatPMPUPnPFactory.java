/*    */ package com.aelitis.net.natpmp.upnp;
/*    */ 
/*    */ import com.aelitis.net.natpmp.NatPMPDevice;
/*    */ import com.aelitis.net.natpmp.upnp.impl.NatPMPUPnPImpl;
/*    */ import com.aelitis.net.upnp.UPnP;
/*    */ import com.aelitis.net.upnp.UPnPException;
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
/*    */ public class NatPMPUPnPFactory
/*    */ {
/*    */   public static NatPMPUPnP create(UPnP upnp, NatPMPDevice nat_pmp_device)
/*    */     throws UPnPException
/*    */   {
/* 37 */     return new NatPMPUPnPImpl(upnp, nat_pmp_device);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/natpmp/upnp/NatPMPUPnPFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */