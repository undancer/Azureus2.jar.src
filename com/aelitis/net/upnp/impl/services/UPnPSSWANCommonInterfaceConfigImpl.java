/*    */ package com.aelitis.net.upnp.impl.services;
/*    */ 
/*    */ import com.aelitis.net.upnp.UPnP;
/*    */ import com.aelitis.net.upnp.UPnPAction;
/*    */ import com.aelitis.net.upnp.UPnPActionArgument;
/*    */ import com.aelitis.net.upnp.UPnPActionInvocation;
/*    */ import com.aelitis.net.upnp.UPnPDevice;
/*    */ import com.aelitis.net.upnp.UPnPException;
/*    */ import com.aelitis.net.upnp.UPnPRootDevice;
/*    */ import com.aelitis.net.upnp.UPnPService;
/*    */ import com.aelitis.net.upnp.services.UPnPWANCommonInterfaceConfig;
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
/*    */ public class UPnPSSWANCommonInterfaceConfigImpl
/*    */   implements UPnPWANCommonInterfaceConfig
/*    */ {
/*    */   private UPnPServiceImpl service;
/*    */   
/*    */   protected UPnPSSWANCommonInterfaceConfigImpl(UPnPServiceImpl _service)
/*    */   {
/* 39 */     this.service = _service;
/*    */   }
/*    */   
/*    */ 
/*    */   public UPnPService getGenericService()
/*    */   {
/* 45 */     return this.service;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public long[] getCommonLinkProperties()
/*    */     throws UPnPException
/*    */   {
/* 53 */     UPnPAction act = this.service.getAction("GetCommonLinkProperties");
/*    */     
/* 55 */     if (act == null)
/*    */     {
/* 57 */       this.service.getDevice().getRootDevice().getUPnP().log("Action 'GetCommonLinkProperties' not supported, binding not established");
/*    */       
/* 59 */       throw new UPnPException("GetCommonLinkProperties not supported");
/*    */     }
/*    */     
/*    */ 
/* 63 */     UPnPActionInvocation inv = act.getInvocation();
/*    */     
/* 65 */     UPnPActionArgument[] args = inv.invoke();
/*    */     
/* 67 */     long[] res = new long[2];
/*    */     
/* 69 */     for (int i = 0; i < args.length; i++)
/*    */     {
/* 71 */       UPnPActionArgument arg = args[i];
/*    */       
/* 73 */       String name = arg.getName();
/*    */       
/* 75 */       if (name.equalsIgnoreCase("NewLayer1UpstreamMaxBitRate"))
/*    */       {
/* 77 */         res[1] = Long.parseLong(arg.getValue());
/*    */       }
/* 79 */       else if (name.equalsIgnoreCase("NewLayer1DownstreamMaxBitRate"))
/*    */       {
/* 81 */         res[0] = Long.parseLong(arg.getValue());
/*    */       }
/*    */     }
/*    */     
/* 85 */     return res;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/services/UPnPSSWANCommonInterfaceConfigImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */