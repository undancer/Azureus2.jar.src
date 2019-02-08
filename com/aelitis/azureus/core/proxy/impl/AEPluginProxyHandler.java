/*     */ package com.aelitis.azureus.core.proxy.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginHTTPProxy;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*     */ import com.aelitis.azureus.core.proxy.AEProxySelector;
/*     */ import com.aelitis.azureus.core.proxy.AEProxySelectorFactory;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteSet;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginInterface;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginAdapter;
/*     */ import org.gudy.azureus2.plugins.PluginEvent;
/*     */ import org.gudy.azureus2.plugins.PluginEventListener;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*     */ public class AEPluginProxyHandler
/*     */ {
/*  57 */   private static final CopyOnWriteList<PluginInterface> plugins = new CopyOnWriteList();
/*     */   
/*     */   private static final int plugin_init_max_wait = 30000;
/*  60 */   private static final AESemaphore plugin_init_complete = new AESemaphore("init:waiter");
/*     */   private static boolean enable_plugin_proxies_with_socks;
/*     */   
/*     */   static
/*     */   {
/*     */     try {
/*  66 */       COConfigurationManager.addAndFireParameterListener("Proxy.SOCKS.disable.plugin.proxies", new ParameterListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void parameterChanged(String parameterName)
/*     */         {
/*     */ 
/*  73 */           AEPluginProxyHandler.access$002(!COConfigurationManager.getBooleanParameter(parameterName));
/*     */         }
/*     */         
/*  76 */       });
/*  77 */       AzureusCore core = AzureusCoreFactory.getSingleton();
/*     */       
/*  79 */       PluginInterface default_pi = core.getPluginManager().getDefaultPluginInterface();
/*     */       
/*  81 */       default_pi.addEventListener(new PluginEventListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void handleEvent(PluginEvent ev)
/*     */         {
/*     */ 
/*  88 */           int type = ev.getType();
/*     */           
/*  90 */           if (type == 8)
/*     */           {
/*  92 */             AEPluginProxyHandler.pluginAdded((PluginInterface)ev.getValue());
/*     */           }
/*  94 */           if (type == 9)
/*     */           {
/*  96 */             AEPluginProxyHandler.pluginRemoved((PluginInterface)ev.getValue());
/*     */           }
/*     */           
/*     */         }
/* 100 */       });
/* 101 */       PluginInterface[] plugins = default_pi.getPluginManager().getPlugins(true);
/*     */       
/* 103 */       for (PluginInterface pi : plugins)
/*     */       {
/* 105 */         if (pi.getPluginState().isOperational())
/*     */         {
/* 107 */           pluginAdded(pi);
/*     */         }
/*     */       }
/*     */       
/* 111 */       default_pi.addListener(new PluginAdapter()
/*     */       {
/*     */ 
/*     */         public void initializationComplete()
/*     */         {
/*     */ 
/* 117 */           AEPluginProxyHandler.plugin_init_complete.releaseForever();
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 123 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void pluginAdded(PluginInterface pi)
/*     */   {
/* 131 */     String pid = pi.getPluginID();
/*     */     
/* 133 */     if ((pid.equals("aznettor")) || (pid.equals("azneti2phelper")))
/*     */     {
/* 135 */       plugins.add(pi);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void pluginRemoved(PluginInterface pi)
/*     */   {
/* 143 */     String pid = pi.getPluginID();
/*     */     
/* 145 */     if ((pid.equals("aznettor")) || (pid.equals("azneti2phelper")))
/*     */     {
/* 147 */       plugins.remove(pi);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static boolean waitForPlugins(int max_wait)
/*     */   {
/* 155 */     if (PluginInitializer.isInitThread())
/*     */     {
/* 157 */       Debug.out("Hmm, rework this");
/*     */     }
/*     */     
/* 160 */     return plugin_init_complete.reserve(max_wait);
/*     */   }
/*     */   
/* 163 */   private static final Map<Proxy, WeakReference<PluginProxyImpl>> proxy_map = new IdentityHashMap();
/* 164 */   private static final CopyOnWriteSet<SocketAddress> proxy_list = new CopyOnWriteSet(false);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean hasPluginProxyForNetwork(String network, boolean supports_data)
/*     */   {
/* 171 */     long start = SystemTime.getMonotonousTime();
/*     */     
/*     */     for (;;)
/*     */     {
/* 175 */       long rem = 30000L - (SystemTime.getMonotonousTime() - start);
/*     */       
/* 177 */       if (rem <= 0L)
/*     */       {
/* 179 */         return false;
/*     */       }
/*     */       
/* 182 */       boolean wait_complete = waitForPlugins(Math.min((int)rem, 1000));
/*     */       
/* 184 */       boolean result = getPluginProxyForNetwork(network, supports_data) != null;
/*     */       
/* 186 */       if ((result) || (wait_complete))
/*     */       {
/* 188 */         return result;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static PluginInterface getPluginProxyForNetwork(String network, boolean supports_data)
/*     */   {
/* 198 */     for (PluginInterface pi : plugins)
/*     */     {
/* 200 */       String pid = pi.getPluginID();
/*     */       
/* 202 */       if ((pid.equals("aznettor")) && (network == "Tor"))
/*     */       {
/* 204 */         if (!supports_data)
/*     */         {
/* 206 */           return pi;
/*     */         }
/*     */       }
/*     */       
/* 210 */       if ((pid.equals("azneti2phelper")) && (network == "I2P"))
/*     */       {
/* 212 */         return pi;
/*     */       }
/*     */     }
/*     */     
/* 216 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean hasPluginProxy()
/*     */   {
/* 222 */     waitForPlugins(30000);
/*     */     
/* 224 */     for (PluginInterface pi : plugins) {
/*     */       try
/*     */       {
/* 227 */         IPCInterface ipc = pi.getIPC();
/*     */         
/* 229 */         if (ipc.canInvoke("testHTTPPseudoProxy", new Object[] { TorrentUtils.getDecentralisedEmptyURL() }))
/*     */         {
/* 231 */           return true;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/* 237 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private static boolean isEnabled()
/*     */   {
/* 243 */     Proxy system_proxy = AEProxySelectorFactory.getSelector().getActiveProxy();
/*     */     
/* 245 */     if ((system_proxy == null) || (system_proxy.equals(Proxy.NO_PROXY)))
/*     */     {
/* 247 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 251 */     return enable_plugin_proxies_with_socks;
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
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static PluginProxyImpl getPluginProxy(String reason, URL target)
/*     */   {
/* 269 */     return getPluginProxy(reason, target, null, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PluginProxyImpl getPluginProxy(String reason, URL target, Map<String, Object> properties, boolean can_wait)
/*     */   {
/* 279 */     if (isEnabled())
/*     */     {
/* 281 */       String url_protocol = target.getProtocol().toLowerCase();
/*     */       
/* 283 */       if ((url_protocol.startsWith("http")) || (url_protocol.equals("ftp")))
/*     */       {
/* 285 */         if (can_wait)
/*     */         {
/* 287 */           waitForPlugins(0);
/*     */         }
/*     */         
/* 290 */         if (properties == null)
/*     */         {
/* 292 */           properties = new HashMap();
/*     */         }
/*     */         
/* 295 */         for (PluginInterface pi : plugins) {
/*     */           try
/*     */           {
/* 298 */             IPCInterface ipc = pi.getIPC();
/*     */             
/*     */             Object[] proxy_details;
/*     */             Object[] proxy_details;
/* 302 */             if (ipc.canInvoke("getProxy", new Object[] { reason, target, properties }))
/*     */             {
/* 304 */               proxy_details = (Object[])ipc.invoke("getProxy", new Object[] { reason, target, properties });
/*     */             }
/*     */             else
/*     */             {
/* 308 */               proxy_details = (Object[])ipc.invoke("getProxy", new Object[] { reason, target });
/*     */             }
/*     */             
/* 311 */             if (proxy_details != null)
/*     */             {
/* 313 */               if (proxy_details.length == 2)
/*     */               {
/*     */ 
/*     */ 
/* 317 */                 proxy_details = new Object[] { proxy_details[0], proxy_details[1], target.getHost() };
/*     */               }
/*     */               
/* 320 */               return new PluginProxyImpl(target.toExternalForm(), reason, ipc, properties, proxy_details, null);
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 328 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PluginProxyImpl getPluginProxy(String reason, String host, int port, Map<String, Object> properties)
/*     */   {
/* 338 */     if (isEnabled())
/*     */     {
/* 340 */       if (properties == null)
/*     */       {
/* 342 */         properties = new HashMap();
/*     */       }
/*     */       
/* 345 */       for (PluginInterface pi : plugins) {
/*     */         try
/*     */         {
/* 348 */           IPCInterface ipc = pi.getIPC();
/*     */           
/*     */           Object[] proxy_details;
/*     */           Object[] proxy_details;
/* 352 */           if (ipc.canInvoke("getProxy", new Object[] { reason, host, Integer.valueOf(port), properties }))
/*     */           {
/* 354 */             proxy_details = (Object[])ipc.invoke("getProxy", new Object[] { reason, host, Integer.valueOf(port), properties });
/*     */           }
/*     */           else
/*     */           {
/* 358 */             proxy_details = (Object[])ipc.invoke("getProxy", new Object[] { reason, host, Integer.valueOf(port) });
/*     */           }
/*     */           
/* 361 */           if (proxy_details != null)
/*     */           {
/* 363 */             return new PluginProxyImpl(host + ":" + port, reason, ipc, properties, proxy_details, null);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */     
/* 370 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static AEProxyFactory.PluginProxy getPluginProxy(Proxy proxy)
/*     */   {
/* 377 */     if (proxy != null)
/*     */     {
/* 379 */       synchronized (proxy_map)
/*     */       {
/* 381 */         WeakReference<PluginProxyImpl> ref = (WeakReference)proxy_map.get(proxy);
/*     */         
/* 383 */         if (ref != null)
/*     */         {
/* 385 */           return (AEProxyFactory.PluginProxy)ref.get();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 390 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isPluginProxy(SocketAddress address)
/*     */   {
/* 397 */     return proxy_list.contains(address);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Boolean testPluginHTTPProxy(URL url, boolean can_wait)
/*     */   {
/* 405 */     if (isEnabled())
/*     */     {
/* 407 */       String url_protocol = url.getProtocol().toLowerCase();
/*     */       
/* 409 */       if (url_protocol.startsWith("http"))
/*     */       {
/* 411 */         if (can_wait)
/*     */         {
/* 413 */           waitForPlugins(0);
/*     */         }
/*     */         
/* 416 */         for (PluginInterface pi : plugins) {
/*     */           try
/*     */           {
/* 419 */             IPCInterface ipc = pi.getIPC();
/*     */             
/* 421 */             return (Boolean)ipc.invoke("testHTTPPseudoProxy", new Object[] { url });
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 428 */         Debug.out("Unsupported protocol: " + url_protocol);
/*     */       }
/*     */     }
/*     */     
/* 432 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PluginHTTPProxyImpl getPluginHTTPProxy(String reason, URL url, boolean can_wait)
/*     */   {
/* 441 */     if (isEnabled())
/*     */     {
/* 443 */       String url_protocol = url.getProtocol().toLowerCase();
/*     */       
/* 445 */       if (url_protocol.startsWith("http"))
/*     */       {
/* 447 */         if (can_wait)
/*     */         {
/* 449 */           waitForPlugins(0);
/*     */         }
/*     */         
/* 452 */         for (PluginInterface pi : plugins) {
/*     */           try
/*     */           {
/* 455 */             IPCInterface ipc = pi.getIPC();
/*     */             
/* 457 */             Proxy proxy = (Proxy)ipc.invoke("createHTTPPseudoProxy", new Object[] { reason, url });
/*     */             
/* 459 */             if (proxy != null)
/*     */             {
/* 461 */               return new PluginHTTPProxyImpl(reason, ipc, proxy, null);
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       else {
/* 468 */         Debug.out("Unsupported protocol: " + url_protocol);
/*     */       }
/*     */     }
/*     */     
/* 472 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static List<PluginInterface> getPluginHTTPProxyProviders(boolean can_wait)
/*     */   {
/* 479 */     if (can_wait)
/*     */     {
/* 481 */       waitForPlugins(0);
/*     */     }
/*     */     
/* 484 */     List<PluginInterface> pis = AzureusCoreFactory.getSingleton().getPluginManager().getPluginsWithMethod("createHTTPPseudoProxy", new Class[] { String.class, URL.class });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 489 */     return pis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Map<String, Object> getPluginServerProxy(String reason, String network, String server_uid, Map<String, Object> options)
/*     */   {
/* 499 */     waitForPlugins(30000);
/*     */     
/* 501 */     PluginInterface pi = getPluginProxyForNetwork(network, false);
/*     */     
/* 503 */     if (pi == null)
/*     */     {
/* 505 */       return null;
/*     */     }
/*     */     
/* 508 */     options = new HashMap(options);
/*     */     
/* 510 */     options.put("id", server_uid);
/*     */     try
/*     */     {
/* 513 */       IPCInterface ipc = pi.getIPC();
/*     */       
/* 515 */       return (Map)ipc.invoke("getProxyServer", new Object[] { reason, options });
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 523 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DHTPluginInterface getPluginDHTProxy(String reason, String network, Map<String, Object> options)
/*     */   {
/* 532 */     waitForPlugins(30000);
/*     */     
/* 534 */     PluginInterface pi = getPluginProxyForNetwork(network, false);
/*     */     
/* 536 */     if (pi == null)
/*     */     {
/* 538 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 542 */       IPCInterface ipc = pi.getIPC();
/*     */       
/* 544 */       return (DHTPluginInterface)ipc.invoke("getProxyDHT", new Object[] { reason, options });
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 552 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private static class PluginProxyImpl
/*     */     implements AEProxyFactory.PluginProxy
/*     */   {
/* 559 */     private final long create_time = SystemTime.getMonotonousTime();
/*     */     
/*     */     private final String target;
/*     */     
/*     */     private final String reason;
/*     */     
/*     */     private final IPCInterface ipc;
/*     */     private final Map<String, Object> proxy_options;
/*     */     private final Object[] proxy_details;
/* 568 */     private final List<PluginProxyImpl> children = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private PluginProxyImpl(String _target, String _reason, IPCInterface _ipc, Map<String, Object> _proxy_options, Object[] _proxy_details)
/*     */     {
/* 578 */       this.target = _target;
/* 579 */       this.reason = _reason;
/* 580 */       this.ipc = _ipc;
/* 581 */       this.proxy_options = _proxy_options;
/* 582 */       this.proxy_details = _proxy_details;
/*     */       
/* 584 */       WeakReference<PluginProxyImpl> my_ref = new WeakReference(this);
/*     */       
/* 586 */       List<PluginProxyImpl> removed = new ArrayList();
/*     */       
/* 588 */       synchronized (AEPluginProxyHandler.proxy_map)
/*     */       {
/* 590 */         Proxy proxy = getProxy();
/*     */         
/* 592 */         SocketAddress address = proxy.address();
/*     */         
/* 594 */         if (!AEPluginProxyHandler.proxy_list.contains(address))
/*     */         {
/* 596 */           AEPluginProxyHandler.proxy_list.add(address);
/*     */         }
/*     */         
/* 599 */         AEPluginProxyHandler.proxy_map.put(proxy, my_ref);
/*     */         
/* 601 */         if (AEPluginProxyHandler.proxy_map.size() > 1024)
/*     */         {
/* 603 */           long now = SystemTime.getMonotonousTime();
/*     */           
/* 605 */           Iterator<WeakReference<PluginProxyImpl>> it = AEPluginProxyHandler.proxy_map.values().iterator();
/*     */           
/* 607 */           while (it.hasNext())
/*     */           {
/* 609 */             WeakReference<PluginProxyImpl> ref = (WeakReference)it.next();
/*     */             
/* 611 */             PluginProxyImpl pp = (PluginProxyImpl)ref.get();
/*     */             
/* 613 */             if (pp == null)
/*     */             {
/* 615 */               it.remove();
/*     */ 
/*     */ 
/*     */             }
/* 619 */             else if (now - pp.create_time > 300000L)
/*     */             {
/* 621 */               removed.add(pp);
/*     */               
/* 623 */               it.remove();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 630 */       for (PluginProxyImpl pp : removed)
/*     */       {
/* 632 */         pp.setOK(false);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public String getTarget()
/*     */     {
/* 639 */       return this.target;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public AEProxyFactory.PluginProxy getChildProxy(String child_reason, URL url)
/*     */     {
/* 647 */       PluginProxyImpl child = AEPluginProxyHandler.getPluginProxy(this.reason + " - " + child_reason, url, this.proxy_options, false);
/*     */       
/* 649 */       if (child != null)
/*     */       {
/* 651 */         synchronized (this.children)
/*     */         {
/* 653 */           this.children.add(child);
/*     */         }
/*     */       }
/*     */       
/* 657 */       return child;
/*     */     }
/*     */     
/*     */ 
/*     */     public Proxy getProxy()
/*     */     {
/* 663 */       return (Proxy)this.proxy_details[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public URL getURL()
/*     */     {
/* 671 */       return (URL)this.proxy_details[1];
/*     */     }
/*     */     
/*     */ 
/*     */     public String getURLHostRewrite()
/*     */     {
/* 677 */       return (String)this.proxy_details[2];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getHost()
/*     */     {
/* 685 */       return (String)this.proxy_details[1];
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPort()
/*     */     {
/* 691 */       return ((Integer)this.proxy_details[2]).intValue();
/*     */     }
/*     */     
/*     */ 
/*     */     public void setOK(boolean good)
/*     */     {
/*     */       try
/*     */       {
/* 699 */         this.ipc.invoke("setProxyStatus", new Object[] { this.proxy_details[0], Boolean.valueOf(good) });
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/*     */       List<PluginProxyImpl> kids;
/*     */       
/* 706 */       synchronized (this.children)
/*     */       {
/* 708 */         kids = new ArrayList(this.children);
/*     */         
/* 710 */         this.children.clear();
/*     */       }
/*     */       
/* 713 */       for (PluginProxyImpl child : kids)
/*     */       {
/* 715 */         child.setOK(good);
/*     */       }
/*     */       
/* 718 */       synchronized (AEPluginProxyHandler.proxy_map)
/*     */       {
/* 720 */         AEPluginProxyHandler.proxy_map.remove(getProxy());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class PluginHTTPProxyImpl
/*     */     implements AEProxyFactory.PluginHTTPProxy
/*     */   {
/*     */     private final String reason;
/*     */     
/*     */     private final IPCInterface ipc;
/*     */     
/*     */     private final Proxy proxy;
/*     */     
/*     */ 
/*     */     private PluginHTTPProxyImpl(String _reason, IPCInterface _ipc, Proxy _proxy)
/*     */     {
/* 739 */       this.reason = _reason;
/* 740 */       this.ipc = _ipc;
/* 741 */       this.proxy = _proxy;
/*     */     }
/*     */     
/*     */ 
/*     */     public Proxy getProxy()
/*     */     {
/* 747 */       return this.proxy;
/*     */     }
/*     */     
/*     */ 
/*     */     public String proxifyURL(String url)
/*     */     {
/*     */       try
/*     */       {
/* 755 */         URL _url = new URL(url);
/*     */         
/* 757 */         InetSocketAddress pa = (InetSocketAddress)this.proxy.address();
/*     */         
/* 759 */         _url = UrlUtils.setHost(_url, pa.getAddress().getHostAddress());
/* 760 */         _url = UrlUtils.setPort(_url, pa.getPort());
/*     */         
/* 762 */         url = _url.toExternalForm();
/*     */         
/* 764 */         return url + (url.indexOf('?') == -1 ? "?" : "&") + "_azpproxy=1";
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/* 770 */         Debug.out("Failed to proxify URL: " + url, e);
/*     */       }
/* 772 */       return url;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void destroy()
/*     */     {
/*     */       try
/*     */       {
/* 781 */         this.ipc.invoke("destroyHTTPPseudoProxy", new Object[] { this.proxy });
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 785 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/impl/AEPluginProxyHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */