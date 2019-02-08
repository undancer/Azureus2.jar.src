/*     */ package com.aelitis.azureus.core.proxy.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.UnknownHostException;
/*     */ import com.aelitis.azureus.core.proxy.AEProxySelector;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteMap;
/*     */ import com.aelitis.azureus.core.util.DNSUtils;
/*     */ import com.aelitis.azureus.core.util.DNSUtils.DNSDirContext;
/*     */ import com.aelitis.azureus.core.util.DNSUtils.DNSUtilsIntf;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.ProxySelector;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AEProxySelectorImpl
/*     */   extends ProxySelector
/*     */   implements AEProxySelector
/*     */ {
/*     */   private static final boolean LOG = false;
/*     */   private static final String NL = "\r\n";
/*  50 */   private static final AEProxySelectorImpl singleton = new AEProxySelectorImpl();
/*     */   
/*  52 */   private static final List<Proxy> no_proxy_list = Arrays.asList(new Proxy[] { Proxy.NO_PROXY });
/*     */   
/*  54 */   private static final ThreadLocal<Integer> tls = new ThreadLocal()
/*     */   {
/*     */ 
/*     */     public Integer initialValue()
/*     */     {
/*     */ 
/*  60 */       return Integer.valueOf(0);
/*     */     }
/*     */   };
/*     */   private final ProxySelector existing_selector;
/*     */   private volatile ActiveProxy active_proxy;
/*     */   
/*     */   public static AEProxySelector getSingleton() {
/*  67 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  73 */   private volatile List<String> alt_dns_servers = new ArrayList();
/*     */   
/*  75 */   private final CopyOnWriteMap<String, List<Proxy>> explicit_proxy_map = new CopyOnWriteMap();
/*     */   
/*     */ 
/*     */   private AEProxySelectorImpl()
/*     */   {
/*  80 */     COConfigurationManager.addAndFireListener(new COConfigurationListener()
/*     */     {
/*     */ 
/*     */       public void configurationSaved()
/*     */       {
/*     */ 
/*  86 */         boolean enable_proxy = COConfigurationManager.getBooleanParameter("Enable.Proxy");
/*  87 */         boolean enable_socks = COConfigurationManager.getBooleanParameter("Enable.SOCKS");
/*     */         
/*  89 */         String proxy_host = null;
/*  90 */         int proxy_port = -1;
/*     */         
/*  92 */         if ((enable_proxy) && (enable_socks))
/*     */         {
/*  94 */           proxy_host = COConfigurationManager.getStringParameter("Proxy.Host").trim();
/*     */           try {
/*  96 */             proxy_port = Integer.parseInt(COConfigurationManager.getStringParameter("Proxy.Port").trim());
/*     */           }
/*     */           catch (Exception e) {}
/*     */           
/*     */ 
/* 101 */           if (proxy_host.length() == 0)
/*     */           {
/* 103 */             proxy_host = null;
/*     */           }
/*     */           
/* 106 */           if ((proxy_port <= 0) || (proxy_port > 65535))
/*     */           {
/* 108 */             proxy_host = null;
/*     */           }
/*     */         }
/*     */         
/* 112 */         List<String> new_servers = new ArrayList();
/*     */         
/* 114 */         if (COConfigurationManager.getBooleanParameter("DNS Alt Servers SOCKS Enable"))
/*     */         {
/* 116 */           String alt_servers = COConfigurationManager.getStringParameter("DNS Alt Servers");
/*     */           
/* 118 */           alt_servers = alt_servers.replace(',', ';');
/*     */           
/* 120 */           String[] servers = alt_servers.split(";");
/*     */           
/* 122 */           for (String s : servers)
/*     */           {
/* 124 */             s = s.trim();
/*     */             
/* 126 */             if (s.length() > 0)
/*     */             {
/* 128 */               new_servers.add(s);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 133 */         synchronized (AEProxySelectorImpl.this)
/*     */         {
/* 135 */           boolean servers_changed = false;
/*     */           
/* 137 */           if (AEProxySelectorImpl.this.alt_dns_servers.size() != new_servers.size())
/*     */           {
/* 139 */             servers_changed = true;
/*     */           }
/*     */           else
/*     */           {
/* 143 */             for (String s : new_servers)
/*     */             {
/* 145 */               if (!AEProxySelectorImpl.this.alt_dns_servers.contains(s))
/*     */               {
/* 147 */                 servers_changed = true;
/*     */                 
/* 149 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 154 */           if (servers_changed)
/*     */           {
/* 156 */             AEProxySelectorImpl.this.alt_dns_servers = new_servers;
/*     */           }
/*     */           
/* 159 */           if (proxy_host == null)
/*     */           {
/* 161 */             if (AEProxySelectorImpl.this.active_proxy != null)
/*     */             {
/* 163 */               AEProxySelectorImpl.this.active_proxy = null;
/*     */             }
/*     */           }
/* 166 */           else if ((AEProxySelectorImpl.this.active_proxy == null) || (!AEProxySelectorImpl.ActiveProxy.access$200(AEProxySelectorImpl.this.active_proxy, proxy_host, proxy_port)))
/*     */           {
/*     */ 
/* 169 */             AEProxySelectorImpl.this.active_proxy = new AEProxySelectorImpl.ActiveProxy(proxy_host, proxy_port, new_servers, null);
/*     */ 
/*     */ 
/*     */           }
/* 173 */           else if (servers_changed)
/*     */           {
/* 175 */             AEProxySelectorImpl.ActiveProxy.access$400(AEProxySelectorImpl.this.active_proxy, new_servers);
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 182 */     });
/* 183 */     this.existing_selector = ProxySelector.getDefault();
/*     */     try
/*     */     {
/* 186 */       ProxySelector.setDefault(this);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 190 */       Debug.out(e);
/*     */     }
/*     */     
/* 193 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void azureusCoreRunning(AzureusCore core)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 203 */           Class.forName("com.aelitis.azureus.core.proxy.impl.swt.AEProxySelectorSWTImpl").getConstructor(new Class[] { AzureusCore.class, AEProxySelectorImpl.class }).newInstance(new Object[] { core, AEProxySelectorImpl.this });
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void startNoProxy()
/*     */   {
/* 219 */     tls.set(Integer.valueOf(((Integer)tls.get()).intValue() + 1));
/*     */   }
/*     */   
/*     */ 
/*     */   public void endNoProxy()
/*     */   {
/* 225 */     tls.set(Integer.valueOf(((Integer)tls.get()).intValue() - 1));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Proxy setProxy(InetSocketAddress address, Proxy proxy)
/*     */   {
/* 233 */     List<Proxy> p = new ArrayList();
/*     */     
/* 235 */     p.add(proxy);
/*     */     
/* 237 */     String address_str = AddressUtils.getHostNameNoResolve(address) + ":" + address.getPort();
/*     */     
/* 239 */     List<Proxy> old = (List)this.explicit_proxy_map.put(address_str, Collections.unmodifiableList(p));
/*     */     
/* 241 */     if (old != null)
/*     */     {
/* 243 */       return (Proxy)old.get(0);
/*     */     }
/*     */     
/*     */ 
/* 247 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Proxy removeProxy(InetSocketAddress address)
/*     */   {
/* 255 */     String address_str = AddressUtils.getHostNameNoResolve(address) + ":" + address.getPort();
/*     */     
/* 257 */     List<Proxy> old = (List)this.explicit_proxy_map.remove(address_str);
/*     */     
/* 259 */     if (old != null)
/*     */     {
/* 261 */       return (Proxy)old.get(0);
/*     */     }
/*     */     
/*     */ 
/* 265 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<Proxy> select(URI uri)
/*     */   {
/* 275 */     if (this.explicit_proxy_map.size() > 0)
/*     */       try
/*     */       {
/* 278 */         String host = uri.getHost();
/*     */         
/* 280 */         if (host != null)
/*     */         {
/* 282 */           int port = uri.getPort();
/*     */           
/* 284 */           if (port == -1)
/*     */           {
/* 286 */             String scheme = uri.getScheme();
/*     */             
/* 288 */             if (scheme != null)
/*     */             {
/* 290 */               scheme = scheme.toLowerCase(Locale.US);
/*     */               
/* 292 */               if (scheme.equals("http"))
/*     */               {
/* 294 */                 port = 80;
/*     */               }
/* 296 */               else if (scheme.equals("https"))
/*     */               {
/* 298 */                 port = 443;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 303 */           if (port != -1)
/*     */           {
/* 305 */             List<Proxy> p = (List)this.explicit_proxy_map.get(host + ":" + port);
/*     */             
/* 307 */             if (p != null)
/*     */             {
/* 309 */               return p;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 315 */         e.printStackTrace();
/*     */       }
/*     */     List<Proxy> result;
/*     */     List<Proxy> result;
/* 319 */     if (((Integer)tls.get()).intValue() > 0)
/*     */     {
/* 321 */       result = no_proxy_list;
/*     */     }
/*     */     else
/*     */     {
/* 325 */       result = selectSupport(uri);
/*     */       
/* 327 */       String host = uri.getHost();
/*     */       
/* 329 */       if (host != null)
/*     */       {
/* 331 */         if ((host.endsWith(".i2p")) || (host.endsWith(".onion")))
/*     */         {
/* 333 */           List<Proxy> trimmed = new ArrayList(result.size());
/*     */           
/* 335 */           for (Proxy p : result)
/*     */           {
/* 337 */             if (p.type() != Proxy.Type.DIRECT)
/*     */             {
/* 339 */               trimmed.add(p);
/*     */             }
/*     */           }
/*     */           
/* 343 */           if (trimmed.size() == 0)
/*     */           {
/* 345 */             throw new AEProxyFactory.UnknownHostException(host);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 355 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private List<Proxy> selectSupport(URI uri)
/*     */   {
/* 362 */     ActiveProxy active = this.active_proxy;
/*     */     
/* 364 */     if (active == null)
/*     */     {
/* 366 */       if (this.existing_selector == null)
/*     */       {
/* 368 */         return no_proxy_list;
/*     */       }
/*     */       
/* 371 */       List<Proxy> proxies = this.existing_selector.select(uri);
/*     */       
/* 373 */       Iterator<Proxy> it = proxies.iterator();
/*     */       
/* 375 */       while (it.hasNext())
/*     */       {
/* 377 */         Proxy p = (Proxy)it.next();
/*     */         
/* 379 */         if (p.type() == Proxy.Type.SOCKS)
/*     */         {
/* 381 */           it.remove();
/*     */         }
/*     */       }
/*     */       
/* 385 */       if (proxies.size() > 0)
/*     */       {
/* 387 */         return proxies;
/*     */       }
/*     */       
/* 390 */       return no_proxy_list;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 395 */     if (this.alt_dns_servers.contains(uri.getHost()))
/*     */     {
/* 397 */       return no_proxy_list;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 404 */     if (this.existing_selector != null)
/*     */     {
/* 406 */       List<Proxy> proxies = this.existing_selector.select(uri);
/*     */       
/* 408 */       boolean apply = false;
/*     */       
/* 410 */       for (Proxy p : proxies)
/*     */       {
/* 412 */         if (p.type() == Proxy.Type.SOCKS)
/*     */         {
/* 414 */           apply = true;
/*     */           
/* 416 */           break;
/*     */         }
/*     */       }
/*     */       
/* 420 */       if (!apply)
/*     */       {
/* 422 */         return no_proxy_list;
/*     */       }
/*     */     }
/*     */     
/* 426 */     return Arrays.asList(new Proxy[] { active.select() });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void connectFailed(SocketAddress sa, Throwable error)
/*     */   {
/* 434 */     ActiveProxy active = this.active_proxy;
/*     */     
/* 436 */     if ((active == null) || (!(sa instanceof InetSocketAddress)))
/*     */     {
/* 438 */       return;
/*     */     }
/*     */     
/* 441 */     active.connectFailed((InetSocketAddress)sa, error);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void connectFailed(URI uri, SocketAddress sa, IOException ioe)
/*     */   {
/* 450 */     connectFailed(sa, ioe);
/*     */     
/* 452 */     if (this.existing_selector != null)
/*     */     {
/* 454 */       this.existing_selector.connectFailed(uri, sa, ioe);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Proxy getSOCKSProxy(String host, int port, InetSocketAddress target)
/*     */   {
/* 464 */     InetSocketAddress isa = new InetSocketAddress(host, port);
/*     */     
/* 466 */     return getSOCKSProxy(isa, target);
/*     */   }
/*     */   
/*     */ 
/*     */   public Proxy getSOCKSProxy(InetSocketAddress isa, InetSocketAddress target)
/*     */   {
/*     */     Proxy result;
/*     */     
/*     */     Proxy result;
/*     */     
/* 476 */     if (((Integer)tls.get()).intValue() > 0)
/*     */     {
/* 478 */       result = Proxy.NO_PROXY;
/*     */     }
/*     */     else
/*     */     {
/* 482 */       ActiveProxy active = this.active_proxy;
/*     */       Proxy result;
/* 484 */       if ((active == null) || (!active.getAddress().equals(isa)))
/*     */       {
/*     */ 
/* 487 */         result = new Proxy(Proxy.Type.SOCKS, isa);
/*     */       }
/*     */       else
/*     */       {
/* 491 */         result = active.select();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 499 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public Proxy getActiveProxy()
/*     */   {
/* 505 */     String proxy = System.getProperty("socksProxyHost", "");
/*     */     
/* 507 */     if (proxy.trim().length() == 0)
/*     */     {
/* 509 */       return null;
/*     */     }
/*     */     
/* 512 */     ActiveProxy active = this.active_proxy;
/*     */     
/* 514 */     if (active == null)
/*     */     {
/* 516 */       return null;
/*     */     }
/*     */     
/* 519 */     return new Proxy(Proxy.Type.SOCKS, active.getAddress());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void connectFailed(Proxy proxy, Throwable error)
/*     */   {
/* 527 */     connectFailed(proxy.address(), error);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLastConnectionTime()
/*     */   {
/* 533 */     ActiveProxy active = this.active_proxy;
/*     */     
/* 535 */     if (active == null)
/*     */     {
/* 537 */       return -1L;
/*     */     }
/*     */     
/* 540 */     return active.getLastConnectionTime();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLastFailTime()
/*     */   {
/* 546 */     ActiveProxy active = this.active_proxy;
/*     */     
/* 548 */     if (active == null)
/*     */     {
/* 550 */       return -1L;
/*     */     }
/*     */     
/* 553 */     return active.getLastFailTime();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getConnectionCount()
/*     */   {
/* 559 */     ActiveProxy active = this.active_proxy;
/*     */     
/* 561 */     if (active == null)
/*     */     {
/* 563 */       return 0;
/*     */     }
/*     */     
/* 566 */     return active.getConnectionCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFailCount()
/*     */   {
/* 572 */     ActiveProxy active = this.active_proxy;
/*     */     
/* 574 */     if (active == null)
/*     */     {
/* 576 */       return 0;
/*     */     }
/*     */     
/* 579 */     return active.getFailCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getInfo()
/*     */   {
/* 585 */     ActiveProxy active = this.active_proxy;
/*     */     
/* 587 */     if ((active == null) || (getActiveProxy() == null))
/*     */     {
/* 589 */       return "No proxy active";
/*     */     }
/*     */     
/* 592 */     return active.getInfo();
/*     */   }
/*     */   
/*     */ 
/*     */   private static class ActiveProxy
/*     */   {
/*     */     private static final int DNS_RETRY_MILLIS = 900000;
/*     */     
/*     */     private final String proxy_host;
/*     */     
/*     */     private final int proxy_port;
/*     */     
/*     */     private final InetSocketAddress address;
/* 605 */     private volatile List<AEProxySelectorImpl.MyProxy> proxy_list_cow = new ArrayList();
/*     */     
/*     */     private Boolean alt_dns_enable;
/*     */     
/*     */     private List<String> alt_dns_to_try;
/* 610 */     private final Map<String, Long> alt_dns_tried = new HashMap();
/*     */     
/* 612 */     private long default_dns_tried_time = -1L;
/*     */     
/* 614 */     private volatile long last_connection_time = -1L;
/* 615 */     private volatile int connection_count = 0;
/* 616 */     private volatile long last_fail_time = -1L;
/* 617 */     private volatile int fail_count = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private ActiveProxy(String _proxy_host, int _proxy_port, List<String> _servers)
/*     */     {
/* 625 */       this.proxy_host = _proxy_host;
/* 626 */       this.proxy_port = _proxy_port;
/* 627 */       this.alt_dns_to_try = _servers;
/*     */       
/* 629 */       this.address = new InetSocketAddress(this.proxy_host, this.proxy_port);
/*     */       
/* 631 */       this.proxy_list_cow.add(new AEProxySelectorImpl.MyProxy(this.address, null));
/*     */     }
/*     */     
/*     */ 
/*     */     public String getInfo()
/*     */     {
/* 637 */       StringBuilder sb = new StringBuilder(2048);
/*     */       
/* 639 */       long now = SystemTime.getCurrentTime();
/* 640 */       long mono_now = SystemTime.getMonotonousTime();
/*     */       
/* 642 */       sb.append("Proxy: ").append(this.address).append("\r\n");
/* 643 */       sb.append("Last connection attempt: ").append(this.last_connection_time == -1L ? "Never" : new Date(now - (mono_now - this.last_connection_time))).append("\r\n");
/*     */       
/* 645 */       sb.append("Last failure: ").append(this.last_fail_time == -1L ? "Never" : new Date(now - (mono_now - this.last_fail_time))).append("\r\n");
/* 646 */       sb.append("Total connections: ").append(this.connection_count).append("\r\n");
/* 647 */       sb.append("Total failures: ").append(this.fail_count).append("\r\n");
/*     */       
/* 649 */       List<AEProxySelectorImpl.MyProxy> proxies = new ArrayList(this.proxy_list_cow);
/*     */       
/* 651 */       sb.append("\r\n");
/*     */       
/* 653 */       for (AEProxySelectorImpl.MyProxy p : proxies)
/*     */       {
/* 655 */         sb.append(AEProxySelectorImpl.MyProxy.access$900(p, now, mono_now)).append("\r\n");
/*     */       }
/*     */       
/* 658 */       return sb.toString();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void updateServers(List<String> servers)
/*     */     {
/* 665 */       synchronized (this)
/*     */       {
/* 667 */         this.alt_dns_to_try = servers;
/*     */         
/* 669 */         this.alt_dns_tried.clear();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private boolean sameAddress(String host, int port)
/*     */     {
/* 678 */       return (host.equals(this.proxy_host)) && (port == this.proxy_port);
/*     */     }
/*     */     
/*     */ 
/*     */     private InetSocketAddress getAddress()
/*     */     {
/* 684 */       return this.address;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getLastConnectionTime()
/*     */     {
/* 690 */       return this.last_connection_time;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getConnectionCount()
/*     */     {
/* 696 */       return this.connection_count;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getLastFailTime()
/*     */     {
/* 702 */       return this.last_fail_time;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getFailCount()
/*     */     {
/* 708 */       return this.fail_count;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private AEProxySelectorImpl.MyProxy select()
/*     */     {
/* 718 */       AEProxySelectorImpl.MyProxy proxy = (AEProxySelectorImpl.MyProxy)this.proxy_list_cow.get(0);
/*     */       
/* 720 */       AEProxySelectorImpl.MyProxy.access$1000(proxy);
/*     */       
/* 722 */       this.last_connection_time = SystemTime.getMonotonousTime();
/*     */       
/* 724 */       this.connection_count += 1;
/*     */       
/* 726 */       return proxy;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private void connectFailed(InetSocketAddress failed_isa, Throwable error)
/*     */     {
/* 734 */       String msg = Debug.getNestedExceptionMessage(error).toLowerCase();
/*     */       
/*     */ 
/*     */ 
/* 738 */       if ((msg.contains("unreachable")) || (msg.contains("operation on nonsocket")))
/*     */       {
/*     */ 
/* 741 */         return;
/*     */       }
/*     */       
/* 744 */       long now_mono = SystemTime.getMonotonousTime();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 750 */       synchronized (this)
/*     */       {
/* 752 */         InetAddress failed_ia = failed_isa.getAddress();
/* 753 */         String failed_hostname = failed_ia == null ? failed_isa.getHostName() : null;
/*     */         
/* 755 */         AEProxySelectorImpl.MyProxy matching_proxy = null;
/*     */         
/* 757 */         List<AEProxySelectorImpl.MyProxy> new_list = new ArrayList();
/*     */         
/* 759 */         Set<InetAddress> existing_addresses = new HashSet();
/*     */         
/*     */ 
/*     */ 
/* 763 */         boolean all_failed = true;
/*     */         
/* 765 */         for (AEProxySelectorImpl.MyProxy p : this.proxy_list_cow)
/*     */         {
/* 767 */           InetSocketAddress p_isa = (InetSocketAddress)p.address();
/*     */           
/* 769 */           InetAddress p_ia = p_isa.getAddress();
/* 770 */           String p_hostname = p_ia == null ? p_isa.getHostName() : null;
/*     */           
/* 772 */           if (p_ia != null)
/*     */           {
/* 774 */             existing_addresses.add(p_ia);
/*     */           }
/*     */           
/* 777 */           if (((failed_ia != null) && (failed_ia.equals(p_ia))) || ((failed_hostname != null) && (failed_hostname.equals(p_hostname))))
/*     */           {
/*     */ 
/* 780 */             matching_proxy = p;
/*     */             
/* 782 */             AEProxySelectorImpl.MyProxy.access$1100(matching_proxy);
/*     */           }
/*     */           else
/*     */           {
/* 786 */             new_list.add(p);
/*     */           }
/*     */           
/* 789 */           if (AEProxySelectorImpl.MyProxy.access$1200(p) == 0)
/*     */           {
/* 791 */             all_failed = false;
/*     */           }
/*     */         }
/*     */         
/* 795 */         if (matching_proxy != null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 804 */           this.last_fail_time = now_mono;
/*     */           
/* 806 */           this.fail_count += 1;
/*     */           
/*     */ 
/*     */ 
/* 810 */           new_list.add(matching_proxy);
/*     */         }
/*     */         
/* 813 */         DNSUtils.DNSUtilsIntf dns_utils = DNSUtils.getSingleton();
/*     */         
/* 815 */         if ((dns_utils != null) && (all_failed))
/*     */         {
/* 817 */           DNSUtils.DNSDirContext dns_to_try = null;
/*     */           
/*     */ 
/*     */ 
/* 821 */           if (this.alt_dns_enable == null)
/*     */           {
/* 823 */             this.alt_dns_enable = Boolean.valueOf(HostNameToIPResolver.hostAddressToBytes(this.proxy_host) == null);
/*     */           }
/*     */           
/* 826 */           if (this.alt_dns_enable.booleanValue())
/*     */           {
/* 828 */             if ((this.default_dns_tried_time == -1L) || (now_mono - this.default_dns_tried_time >= 900000L))
/*     */             {
/*     */ 
/* 831 */               this.default_dns_tried_time = now_mono;
/*     */               
/* 833 */               if (failed_ia != null)
/*     */               {
/*     */ 
/*     */                 try
/*     */                 {
/*     */ 
/* 839 */                   dns_to_try = dns_utils.getInitialDirContext();
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 843 */                   Debug.out(e);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 848 */             if (dns_to_try == null)
/*     */             {
/* 850 */               if (this.alt_dns_to_try.size() == 0)
/*     */               {
/* 852 */                 Iterator<Map.Entry<String, Long>> it = this.alt_dns_tried.entrySet().iterator();
/*     */                 
/* 854 */                 while (it.hasNext())
/*     */                 {
/* 856 */                   Map.Entry<String, Long> entry = (Map.Entry)it.next();
/*     */                   
/* 858 */                   if (now_mono - ((Long)entry.getValue()).longValue() >= 900000L)
/*     */                   {
/* 860 */                     it.remove();
/*     */                     
/* 862 */                     this.alt_dns_to_try.add(entry.getKey());
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/* 867 */               if (this.alt_dns_to_try.size() > 0)
/*     */               {
/* 869 */                 String try_dns = (String)this.alt_dns_to_try.remove(0);
/*     */                 
/* 871 */                 this.alt_dns_tried.put(try_dns, Long.valueOf(now_mono));
/*     */                 try
/*     */                 {
/* 874 */                   dns_to_try = dns_utils.getDirContextForServer(try_dns);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 878 */                   Debug.out(e);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 883 */             if (dns_to_try != null) {
/*     */               try
/*     */               {
/* 886 */                 List<InetAddress> addresses = dns_utils.getAllByName(dns_to_try, this.proxy_host);
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 892 */                 Collections.shuffle(addresses);
/*     */                 
/* 894 */                 for (InetAddress a : addresses)
/*     */                 {
/* 896 */                   if (!existing_addresses.contains(a))
/*     */                   {
/* 898 */                     new_list.add(0, new AEProxySelectorImpl.MyProxy(new InetSocketAddress(a, this.proxy_port), null));
/*     */                   }
/*     */                 }
/*     */               }
/*     */               catch (Throwable e) {
/* 903 */                 Debug.out(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 909 */         this.proxy_list_cow = new_list;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class MyProxy
/*     */     extends Proxy
/*     */   {
/* 918 */     private int use_count = 0;
/* 919 */     private int fail_count = 0;
/*     */     
/*     */     private long last_use;
/*     */     
/*     */     private long last_fail;
/*     */     
/*     */ 
/*     */     private MyProxy(InetSocketAddress address)
/*     */     {
/* 928 */       super(address);
/*     */     }
/*     */     
/*     */ 
/*     */     private void handedOut()
/*     */     {
/* 934 */       this.use_count += 1;
/*     */       
/* 936 */       this.last_use = SystemTime.getMonotonousTime();
/*     */     }
/*     */     
/*     */ 
/*     */     private void setFailed()
/*     */     {
/* 942 */       this.fail_count += 1;
/*     */       
/* 944 */       this.last_fail = SystemTime.getMonotonousTime();
/*     */     }
/*     */     
/*     */ 
/*     */     private int getFailCount()
/*     */     {
/* 950 */       return this.fail_count;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private String getInfo(long now, long mono_now)
/*     */     {
/* 958 */       InetSocketAddress address = (InetSocketAddress)address();
/*     */       
/* 960 */       InetAddress ia = address.getAddress();
/*     */       
/* 962 */       String str = ia == null ? address.getHostName() : ia.getHostAddress();
/*     */       
/* 964 */       if (this.last_use > 0L)
/*     */       {
/* 966 */         str = str + "\r\n\tcons=" + this.use_count + ", last=" + new Date(now - (mono_now - this.last_use));
/*     */       }
/* 968 */       if (this.last_fail > 0L)
/*     */       {
/* 970 */         str = str + "\r\n\tfails=" + this.fail_count + ", last=" + new Date(now - (mono_now - this.last_fail));
/*     */       }
/*     */       
/* 973 */       return str;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/impl/AEProxySelectorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */