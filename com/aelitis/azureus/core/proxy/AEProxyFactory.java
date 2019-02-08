/*     */ package com.aelitis.azureus.core.proxy;
/*     */ 
/*     */ import com.aelitis.azureus.core.proxy.impl.AEPluginProxyHandler;
/*     */ import com.aelitis.azureus.core.proxy.impl.AEProxyAddressMapperImpl;
/*     */ import com.aelitis.azureus.core.proxy.impl.AEProxyImpl;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginInterface;
/*     */ import java.net.Proxy;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.URL;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AEProxyFactory
/*     */ {
/*     */   public static final String PO_PEER_NETWORKS = "peer_networks";
/*     */   public static final String SP_HOST = "host";
/*     */   public static final String SP_PORT = "port";
/*     */   public static final String DP_DOWNLOAD = "download";
/*     */   public static final String DP_NETWORKS = "networks";
/*     */   
/*     */   public static AEProxy create(int port, long connect_timeout, long read_timeout, AEProxyHandler state_factory)
/*     */     throws AEProxyException
/*     */   {
/*  58 */     return new AEProxyImpl(port, connect_timeout, read_timeout, state_factory);
/*     */   }
/*     */   
/*     */ 
/*     */   public static AEProxyAddressMapper getAddressMapper()
/*     */   {
/*  64 */     return AEProxyAddressMapperImpl.getSingleton();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PluginProxy getPluginProxy(String reason, URL target)
/*     */   {
/*  74 */     return getPluginProxy(reason, target, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PluginProxy getPluginProxy(String reason, URL target, boolean can_wait)
/*     */   {
/*  83 */     return getPluginProxy(reason, target, null, can_wait);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PluginProxy getPluginProxy(String reason, URL target, Map<String, Object> proxy_options, boolean can_wait)
/*     */   {
/*  93 */     return AEPluginProxyHandler.getPluginProxy(reason, target, proxy_options, can_wait);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PluginProxy getPluginProxy(String reason, String host, int port)
/*     */   {
/* 102 */     return getPluginProxy(reason, host, port, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PluginProxy getPluginProxy(String reason, String host, int port, Map<String, Object> proxy_options)
/*     */   {
/* 112 */     return AEPluginProxyHandler.getPluginProxy(reason, host, port, proxy_options);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static PluginProxy getPluginProxy(Proxy proxy)
/*     */   {
/* 119 */     return AEPluginProxyHandler.getPluginProxy(proxy);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isPluginProxy(SocketAddress address)
/*     */   {
/* 126 */     return AEPluginProxyHandler.isPluginProxy(address);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Boolean testPluginHTTPProxy(URL target, boolean can_wait)
/*     */   {
/* 134 */     return AEPluginProxyHandler.testPluginHTTPProxy(target, can_wait);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PluginHTTPProxy getPluginHTTPProxy(String reason, URL target, boolean can_wait)
/*     */   {
/* 144 */     return AEPluginProxyHandler.getPluginHTTPProxy(reason, target, can_wait);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static List<PluginInterface> getPluginHTTPProxyProviders(boolean can_wait)
/*     */   {
/* 151 */     return AEPluginProxyHandler.getPluginHTTPProxyProviders(can_wait);
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean hasPluginProxy()
/*     */   {
/* 157 */     return AEPluginProxyHandler.hasPluginProxy();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Map<String, Object> getPluginServerProxy(String reason, String network, String server_uid, Map<String, Object> options)
/*     */   {
/* 170 */     return AEPluginProxyHandler.getPluginServerProxy(reason, network, server_uid, options);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DHTPluginInterface getPluginDHTProxy(String reason, String network, Map<String, Object> options)
/*     */   {
/* 183 */     return AEPluginProxyHandler.getPluginDHTProxy(reason, network, options);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static abstract interface PluginHTTPProxy
/*     */   {
/*     */     public abstract Proxy getProxy();
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract String proxifyURL(String paramString);
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract void destroy();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static abstract interface PluginProxy
/*     */   {
/*     */     public abstract String getTarget();
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract PluginProxy getChildProxy(String paramString, URL paramURL);
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract Proxy getProxy();
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract URL getURL();
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract String getURLHostRewrite();
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract String getHost();
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract int getPort();
/*     */     
/*     */ 
/*     */     public abstract void setOK(boolean paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */   public static class UnknownHostException
/*     */     extends RuntimeException
/*     */   {
/*     */     public UnknownHostException(String host)
/*     */     {
/* 241 */       super();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/AEProxyFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */