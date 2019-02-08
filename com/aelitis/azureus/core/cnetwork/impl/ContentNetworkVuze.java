/*    */ package com.aelitis.azureus.core.cnetwork.impl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ContentNetworkVuze
/*    */   extends ContentNetworkVuzeGeneric
/*    */ {
/*    */   private static final String DEFAULT_ADDRESS = "client.vuze.com";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private static final String DEFAULT_PORT = "80";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private static final String DEFAULT_RELAY_ADDRESS = "www.vuze.com";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private static final String DEFAULT_RELAY_PORT = "80";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private static final String DEFAULT_EXT_ADDRESS = "www.vuze.com";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 53 */   private static final String URL_ADDRESS = System.getProperty("platform_address", "client.vuze.com");
/*    */   
/* 55 */   private static final String URL_PORT = System.getProperty("platform_port", "80");
/*    */   
/* 57 */   private static final String URL_PREFIX = "http://" + URL_ADDRESS + ":" + URL_PORT + "/";
/*    */   
/* 59 */   private static final String URL_EXT_PREFIX = "http://" + System.getProperty("platform_address_ext", "www.vuze.com") + ":" + System.getProperty("platform_port_ext", "80") + "/";
/*    */   
/*    */ 
/*    */ 
/* 63 */   private static final String DEFAULT_AUTHORIZED_RPC = "https://" + URL_ADDRESS + ":443/rpc";
/*    */   
/* 65 */   private static String URL_RELAY_RPC = System.getProperty("relay_url", "http://" + System.getProperty("relay_address", "www.vuze.com") + ":" + System.getProperty("relay_port", "80") + "/msgrelay/rpc");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 70 */   private static final String URL_AUTHORIZED_RPC = URL_PREFIX + "app";
/*    */   
/*    */ 
/*    */   private static final String URL_FAQ = "http://wiki.vuze.com/";
/*    */   
/*    */ 
/*    */   private static final String URL_BLOG = "http://blog.vuze.com/";
/*    */   
/*    */ 
/*    */   private static final String URL_FORUMS = "http://forum.vuze.com/";
/*    */   
/*    */   private static final String URL_WIKI = "http://wiki.vuze.com/";
/*    */   
/*    */ 
/*    */   protected ContentNetworkVuze(ContentNetworkManagerImpl manager)
/*    */   {
/* 86 */     super(manager, 1L, 1L, "Vuze StudioHD Network", null, null, URL_ADDRESS, URL_PREFIX, null, URL_RELAY_RPC, URL_AUTHORIZED_RPC, "http://wiki.vuze.com/", "http://blog.vuze.com/", "http://forum.vuze.com/", "http://wiki.vuze.com/", URL_EXT_PREFIX);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/cnetwork/impl/ContentNetworkVuze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */