/*     */ package org.gudy.azureus2.core3.util.spi;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.plugins.I2PHelpers;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginAdapter;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import sun.net.spi.nameservice.NameService;
/*     */ import sun.net.spi.nameservice.NameServiceDescriptor;
/*     */ import sun.net.spi.nameservice.dns.DNSNameService;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AENameServiceDescriptor
/*     */   implements NameServiceDescriptor
/*     */ {
/*     */   private static final String TEST_HOST = "dns.test.client.vuze.com";
/*     */   private static final NameService delegate_ns;
/*     */   private static final Method delegate_ns_method_lookupAllHostAddr;
/*     */   private static final Object delegate_iai;
/*     */   private static final Method delegate_iai_method_lookupAllHostAddr;
/*     */   private static final NameService proxy_name_service;
/*     */   private static boolean config_listener_added;
/*     */   private static volatile boolean tracker_dns_disabled;
/*     */   private static volatile boolean tracker_plugin_proxies_permit;
/*     */   
/*     */   static
/*     */   {
/*  77 */     NameService default_ns = null;
/*  78 */     Method default_lookupAllHostAddr = null;
/*     */     
/*  80 */     NameService new_ns = null;
/*     */     try
/*     */     {
/*  83 */       default_ns = new DNSNameService();
/*     */       
/*  85 */       if (default_ns != null)
/*     */       {
/*  87 */         default_lookupAllHostAddr = default_ns.getClass().getMethod("lookupAllHostAddr", new Class[] { String.class });
/*     */         
/*  89 */         new_ns = (NameService)Proxy.newProxyInstance(NameService.class.getClassLoader(), new Class[] { NameService.class }, new NameServiceProxy(null));
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/*  97 */       e.printStackTrace();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 106 */     Object iai = null;
/* 107 */     Method iai_lookupAllHostAddr = null;
/*     */     try
/*     */     {
/* 110 */       Field field = InetAddress.class.getDeclaredField("impl");
/*     */       
/* 112 */       field.setAccessible(true);
/*     */       
/* 114 */       iai = field.get(null);
/*     */       
/* 116 */       iai_lookupAllHostAddr = iai.getClass().getMethod("lookupAllHostAddr", new Class[] { String.class });
/*     */       
/* 118 */       iai_lookupAllHostAddr.setAccessible(true);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 122 */       System.err.println("Issue resolving the default name service...");
/*     */     }
/*     */     
/* 125 */     proxy_name_service = new_ns;
/* 126 */     delegate_ns = default_ns;
/* 127 */     delegate_ns_method_lookupAllHostAddr = default_lookupAllHostAddr;
/* 128 */     delegate_iai = iai;
/* 129 */     delegate_iai_method_lookupAllHostAddr = iai_lookupAllHostAddr;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NameService createNameService()
/*     */     throws Exception
/*     */   {
/* 141 */     if (proxy_name_service == null)
/*     */     {
/* 143 */       throw new Exception("Failed to create proxy name service");
/*     */     }
/*     */     
/* 146 */     return proxy_name_service;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/* 152 */     return "dns";
/*     */   }
/*     */   
/*     */ 
/*     */   public String getProviderName()
/*     */   {
/* 158 */     return "aednsproxy";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class NameServiceProxy
/*     */     implements InvocationHandler
/*     */   {
/*     */     public Object invoke(Object proxy, Method method, Object[] args)
/*     */       throws Throwable
/*     */     {
/* 173 */       String method_name = method.getName();
/*     */       
/* 175 */       if (method_name.equals("lookupAllHostAddr"))
/*     */       {
/* 177 */         String host_name = (String)args[0];
/*     */         
/* 179 */         if (host_name.equals("dns.test.client.vuze.com"))
/*     */         {
/* 181 */           if (AENameServiceDescriptor.delegate_ns == null)
/*     */           {
/* 183 */             throw new RuntimeException("Delegate Name Service unavailable");
/*     */           }
/*     */           
/* 186 */           host_name = "www.google.com";
/*     */           try
/*     */           {
/* 189 */             Object result = null;
/*     */             
/* 191 */             if (AENameServiceDescriptor.delegate_iai_method_lookupAllHostAddr != null) {
/*     */               try
/*     */               {
/* 194 */                 result = AENameServiceDescriptor.delegate_iai_method_lookupAllHostAddr.invoke(AENameServiceDescriptor.delegate_iai, new Object[] { host_name });
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */             
/*     */ 
/* 200 */             if (result == null)
/*     */             {
/* 202 */               result = AENameServiceDescriptor.delegate_ns_method_lookupAllHostAddr.invoke(AENameServiceDescriptor.delegate_ns, new Object[] { host_name });
/*     */             }
/*     */             
/*     */ 
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 209 */             if ((e instanceof UnknownHostException))
/*     */             {
/*     */ 
/*     */ 
/* 213 */               System.err.println("DNS resolution of " + host_name + " failed, DNS unavailable?");
/*     */             }
/*     */             else
/*     */             {
/* 217 */               throw new RuntimeException("Delegate lookup failed", e);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 223 */           Class ret_type = method.getReturnType();
/*     */           
/* 225 */           if (ret_type.equals(byte[][].class))
/*     */           {
/* 227 */             return new byte[][] { { Byte.MAX_VALUE, 0, 0, 1 } };
/*     */           }
/*     */           
/*     */ 
/* 231 */           return new InetAddress[] { InetAddress.getByAddress(new byte[] { Byte.MAX_VALUE, 0, 0, 1 }) };
/*     */         }
/*     */         
/*     */ 
/* 235 */         boolean tracker_request = TorrentUtils.getTLSTorrentHash() != null;
/*     */         
/* 237 */         if (tracker_request)
/*     */         {
/* 239 */           synchronized (this)
/*     */           {
/* 241 */             if (!AENameServiceDescriptor.config_listener_added)
/*     */             {
/* 243 */               AENameServiceDescriptor.access$502(true);
/*     */               
/* 245 */               COConfigurationManager.addAndFireListener(new COConfigurationListener()
/*     */               {
/*     */ 
/*     */                 public void configurationSaved()
/*     */                 {
/*     */ 
/* 251 */                   boolean enable_proxy = COConfigurationManager.getBooleanParameter("Enable.Proxy");
/* 252 */                   boolean enable_socks = COConfigurationManager.getBooleanParameter("Enable.SOCKS");
/* 253 */                   boolean prevent_dns = COConfigurationManager.getBooleanParameter("Proxy.SOCKS.Tracker.DNS.Disable");
/*     */                   
/* 255 */                   AENameServiceDescriptor.access$602((enable_proxy) && (enable_socks) && (!COConfigurationManager.getBooleanParameter("Proxy.SOCKS.disable.plugin.proxies")));
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/* 260 */                   AENameServiceDescriptor.access$702((enable_proxy) && (enable_socks) && (prevent_dns));
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */           
/* 266 */           if (AENameServiceDescriptor.tracker_plugin_proxies_permit)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 272 */             if (AENetworkClassifier.categoriseAddress(host_name) != "Public")
/*     */             {
/* 274 */               throw new RuntimeException("Plugin proxies enabled for SOCKS");
/*     */             }
/*     */           }
/*     */           
/* 278 */           if (AENameServiceDescriptor.tracker_dns_disabled)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 283 */             throw new UnknownHostException(host_name);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 288 */       return invokeSupport(method_name, args);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private Object invokeSupport(String method_name, Object[] args)
/*     */       throws Throwable
/*     */     {
/* 298 */       if (method_name.equals("getHostByAddr"))
/*     */       {
/* 300 */         byte[] address_bytes = (byte[])args[0];
/*     */         
/*     */ 
/*     */ 
/* 304 */         return AENameServiceDescriptor.delegate_ns.getHostByAddr(address_bytes);
/*     */       }
/* 306 */       if (method_name.equals("lookupAllHostAddr"))
/*     */       {
/* 308 */         String host_name = (String)args[0];
/*     */         
/*     */ 
/*     */ 
/* 312 */         if ((host_name != null) && (!host_name.equals("null")))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 317 */           if (host_name.endsWith(".i2p"))
/*     */           {
/*     */ 
/*     */ 
/* 321 */             AENameServiceDescriptor.checkI2PInstall(host_name);
/*     */             
/* 323 */             throw new UnknownHostException(host_name);
/*     */           }
/* 325 */           if (host_name.endsWith(".onion"))
/*     */           {
/*     */ 
/*     */ 
/* 329 */             throw new UnknownHostException(host_name);
/*     */           }
/*     */         }
/*     */         
/*     */         try
/*     */         {
/* 335 */           if (AENameServiceDescriptor.delegate_iai_method_lookupAllHostAddr != null) {
/*     */             try
/*     */             {
/* 338 */               return AENameServiceDescriptor.delegate_iai_method_lookupAllHostAddr.invoke(AENameServiceDescriptor.delegate_iai, new Object[] { host_name });
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/*     */ 
/* 344 */           return AENameServiceDescriptor.delegate_ns_method_lookupAllHostAddr.invoke(AENameServiceDescriptor.delegate_ns, new Object[] { host_name });
/*     */         }
/*     */         catch (InvocationTargetException e)
/*     */         {
/* 348 */           throw e.getTargetException();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 353 */       throw new IllegalArgumentException("Unknown method '" + method_name + "'");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 358 */   private static boolean i2p_checked = false;
/*     */   
/*     */ 
/*     */ 
/*     */   private static void checkI2PInstall(String host_name)
/*     */   {
/* 364 */     synchronized (AENameServiceDescriptor.class)
/*     */     {
/* 366 */       if (i2p_checked)
/*     */       {
/* 368 */         return;
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 373 */         AzureusCore core = AzureusCoreFactory.getSingleton();
/*     */         
/* 375 */         if (core != null)
/*     */         {
/* 377 */           i2p_checked = true;
/*     */           
/* 379 */           PluginInterface pi = core.getPluginManager().getDefaultPluginInterface();
/*     */           
/* 381 */           pi.addListener(new PluginAdapter()
/*     */           {
/*     */ 
/*     */             public void initializationComplete()
/*     */             {
/*     */ 
/* 387 */               if (I2PHelpers.isI2PInstalled())
/*     */               {
/* 389 */                 return;
/*     */               }
/*     */               
/* 392 */               final boolean[] install_outcome = { false };
/*     */               
/* 394 */               String enable_i2p_reason = MessageText.getString("azneti2phelper.install.reason.dns", new String[] { this.val$host_name });
/*     */               
/*     */ 
/* 397 */               I2PHelpers.installI2PHelper(enable_i2p_reason, "azneti2phelper.install.dns.resolve", install_outcome, new Runnable()
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */                 public void run()
/*     */                 {
/*     */ 
/*     */ 
/* 406 */                   if (install_outcome[0] == 0) {}
/*     */                 }
/*     */               });
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/spi/AENameServiceDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */