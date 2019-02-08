/*    */ package com.aelitis.net.upnpms;
/*    */ 
/*    */ import com.aelitis.net.upnpms.impl.UPNPMSBrowserImpl;
/*    */ import java.net.URL;
/*    */ import java.util.List;
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
/*    */ public class UPNPMSBrowserFactory
/*    */ {
/*    */   public static UPNPMSBrowser create(String client_name, List<URL> endpoints, UPNPMSBrowserListener listener)
/*    */     throws Exception
/*    */   {
/* 39 */     return new UPNPMSBrowserImpl(client_name, endpoints, listener);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnpms/UPNPMSBrowserFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */