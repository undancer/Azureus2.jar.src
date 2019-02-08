/*     */ package org.gudy.azureus2.core3.tracker.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminPropertyChangeListener;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerImpl;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
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
/*     */ public class TRTrackerUtils
/*     */ {
/*  55 */   private static final String[] BLACKLISTED_HOSTS = { "krypt.dyndns.org" };
/*     */   
/*     */ 
/*  58 */   private static final int[] BLACKLISTED_PORTS = { 81 };
/*     */   
/*     */   private static String tracker_ip;
/*     */   
/*     */   private static Set<String> tracker_ip_aliases;
/*     */   
/*     */   private static Map override_map;
/*     */   
/*     */   private static String bind_ip;
/*     */   
/*     */   private static String ports_for_url;
/*     */   
/*     */   private static String ports_for_url_with_crypto;
/*  71 */   static final CopyOnWriteList listeners = new CopyOnWriteList();
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
/*     */   private static AEThread2 listener_thread;
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
/*     */   private static final Map az_trackers;
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
/*     */   private static final Map udp_probe_results;
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
/*     */   private static String computePortsForURL(boolean force_crypto, boolean allow_incoming)
/*     */   {
/* 156 */     boolean socks_peer_inform = (COConfigurationManager.getBooleanParameter("Proxy.Data.Enable")) && (COConfigurationManager.getBooleanParameter("Proxy.Data.SOCKS.inform"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 162 */     allow_incoming &= !COConfigurationManager.getBooleanParameter("Tracker Client No Port Announce");
/*     */     
/*     */     int tcp_port_num;
/*     */     
/*     */     int udp_port_num;
/* 167 */     if (allow_incoming) { int udp_port_num;
/*     */       int tcp_port_num;
/* 169 */       int udp_port_num; if (socks_peer_inform)
/*     */       {
/* 171 */         int tcp_port_num = 0;
/* 172 */         udp_port_num = 0;
/*     */       }
/*     */       else {
/* 175 */         tcp_port_num = COConfigurationManager.getIntParameter("TCP.Listen.Port");
/* 176 */         udp_port_num = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/*     */       }
/*     */       
/* 179 */       String portOverride = COConfigurationManager.getStringParameter("TCP.Listen.Port.Override");
/*     */       
/* 181 */       if (!portOverride.equals("")) {
/*     */         try
/*     */         {
/* 184 */           tcp_port_num = Integer.parseInt(portOverride);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 188 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 193 */       tcp_port_num = 0;
/* 194 */       udp_port_num = 0;
/*     */     }
/*     */     
/* 197 */     String port = "";
/*     */     
/* 199 */     if (force_crypto)
/*     */     {
/* 201 */       port = port + "&requirecrypto=1";
/*     */       
/* 203 */       port = port + "&port=0&cryptoport=" + tcp_port_num;
/*     */     }
/*     */     else
/*     */     {
/* 207 */       boolean require_crypto = COConfigurationManager.getBooleanParameter("network.transport.encrypted.require");
/*     */       
/* 209 */       if (require_crypto)
/*     */       {
/* 211 */         port = port + "&requirecrypto=1";
/*     */       }
/*     */       else
/*     */       {
/* 215 */         port = port + "&supportcrypto=1";
/*     */       }
/*     */       
/* 218 */       if ((require_crypto) && (!COConfigurationManager.getBooleanParameter("network.transport.encrypted.fallback.incoming")) && (COConfigurationManager.getBooleanParameter("network.transport.encrypted.use.crypto.port")))
/*     */       {
/*     */ 
/*     */ 
/* 222 */         port = port + "&port=0&cryptoport=" + tcp_port_num;
/*     */       }
/*     */       else
/*     */       {
/* 226 */         port = port + "&port=" + tcp_port_num;
/*     */       }
/*     */       
/* 229 */       port = port + "&azudp=" + udp_port_num;
/*     */       
/*     */ 
/*     */ 
/* 233 */       if (tcp_port_num == 0)
/*     */       {
/* 235 */         port = port + "&hide=1";
/*     */       }
/*     */       
/* 238 */       if (COConfigurationManager.getBooleanParameter("HTTP.Data.Listen.Port.Enable"))
/*     */       {
/* 240 */         int http_port = COConfigurationManager.getIntParameter("HTTP.Data.Listen.Port.Override");
/*     */         
/* 242 */         if (http_port == 0)
/*     */         {
/* 244 */           http_port = COConfigurationManager.getIntParameter("HTTP.Data.Listen.Port");
/*     */         }
/*     */         
/* 247 */         port = port + "&azhttp=" + http_port;
/*     */       }
/*     */     }
/*     */     
/* 251 */     return port;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getPublicIPOverride()
/*     */   {
/* 257 */     String explicit_ips = COConfigurationManager.getStringParameter("Override Ip", "");
/*     */     
/* 259 */     if (explicit_ips.length() > 0)
/*     */     {
/* 261 */       StringTokenizer tok = new StringTokenizer(explicit_ips, ";");
/*     */       
/* 263 */       while (tok.hasMoreTokens())
/*     */       {
/* 265 */         String this_address = tok.nextToken().trim();
/*     */         
/* 267 */         if (this_address.length() > 0)
/*     */         {
/* 269 */           String cat = AENetworkClassifier.categoriseAddress(this_address);
/*     */           
/* 271 */           if (cat == "Public")
/*     */           {
/* 273 */             return this_address;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 279 */     return null;
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*  76 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Proxy.Data.Enable", "Proxy.Data.SOCKS.inform", "TCP.Listen.Port.Override", "Tracker Client No Port Announce", "network.transport.encrypted.use.crypto.port", "network.transport.encrypted.require", "network.transport.encrypted.fallback.incoming", "TCP.Listen.Port", "UDP.Listen.Port", "HTTP.Data.Listen.Port", "HTTP.Data.Listen.Port.Override", "HTTP.Data.Listen.Port.Enable", "Tracker Client Min Announce Interval" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  98 */         String port = TRTrackerUtils.computePortsForURL(false, true);
/*  99 */         String port_with_crypto = TRTrackerUtils.computePortsForURL(true, false);
/*     */         
/* 101 */         if ((TRTrackerUtils.ports_for_url != null) && (!TRTrackerUtils.ports_for_url.equals(port)))
/*     */         {
/* 103 */           synchronized (TRTrackerUtils.listeners)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 108 */             if (TRTrackerUtils.listener_thread == null)
/*     */             {
/* 110 */               TRTrackerUtils.access$202(new AEThread2("TRTrackerUtils:listener", true)
/*     */               {
/*     */ 
/*     */                 public void run()
/*     */                 {
/*     */                   try
/*     */                   {
/* 117 */                     Thread.sleep(30000L);
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                   
/*     */ 
/* 122 */                   synchronized (TRTrackerUtils.listeners)
/*     */                   {
/* 124 */                     TRTrackerUtils.access$202(null);
/*     */                   }
/*     */                   
/* 127 */                   for (Iterator it = TRTrackerUtils.listeners.iterator(); it.hasNext();) {
/*     */                     try
/*     */                     {
/* 130 */                       ((TRTrackerUtilsListener)it.next()).announceDetailsChanged();
/*     */                     }
/*     */                     catch (Throwable e)
/*     */                     {
/* 134 */                       Debug.printStackTrace(e);
/*     */                     }
/*     */                     
/*     */                   }
/*     */                 }
/* 139 */               });
/* 140 */               TRTrackerUtils.listener_thread.start();
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 145 */         TRTrackerUtils.access$102(port);
/* 146 */         TRTrackerUtils.access$302(port_with_crypto);
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
/*     */       }
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
/* 281 */     });
/* 282 */     az_trackers = COConfigurationManager.getMapParameter("Tracker Client AZ Instances", new HashMap());
/*     */     
/* 284 */     udp_probe_results = COConfigurationManager.getMapParameter("Tracker Client UDP Probe Results", new HashMap());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 289 */     COConfigurationManager.addListener(new COConfigurationListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void configurationSaved() {}
/*     */ 
/*     */ 
/*     */ 
/* 298 */     });
/* 299 */     NetworkAdmin.getSingleton().addPropertyChangeListener(new NetworkAdminPropertyChangeListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void propertyChanged(String property)
/*     */       {
/*     */ 
/* 306 */         if (property == "Default Bind IP")
/*     */         {
/* 308 */           TRTrackerUtils.readConfig();
/*     */         }
/*     */         
/*     */       }
/* 312 */     });
/* 313 */     readConfig();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static void readConfig()
/*     */   {
/* 321 */     tracker_ip = COConfigurationManager.getStringParameter("Tracker IP", "");
/*     */     
/* 323 */     tracker_ip = UrlUtils.expandIPV6Host(tracker_ip);
/*     */     
/* 325 */     String aliases = COConfigurationManager.getStringParameter("Tracker IP Aliases", "");
/*     */     
/* 327 */     if (aliases.length() > 0)
/*     */     {
/* 329 */       tracker_ip_aliases = new HashSet();
/*     */       
/* 331 */       String[] bits = aliases.split(",");
/*     */       
/* 333 */       for (String b : bits)
/*     */       {
/* 335 */         b = b.trim();
/*     */         
/* 337 */         if (b.length() > 0)
/*     */         {
/* 339 */           tracker_ip_aliases.add(b);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 344 */       tracker_ip_aliases = null;
/*     */     }
/*     */     
/* 347 */     String override_ips = COConfigurationManager.getStringParameter("Override Ip", "");
/*     */     
/* 349 */     StringTokenizer tok = new StringTokenizer(override_ips, ";");
/*     */     
/* 351 */     Map new_override_map = new HashMap();
/*     */     
/* 353 */     while (tok.hasMoreTokens())
/*     */     {
/* 355 */       String ip = tok.nextToken().trim();
/*     */       
/* 357 */       if (ip.length() > 0)
/*     */       {
/* 359 */         new_override_map.put(AENetworkClassifier.categoriseAddress(ip), ip);
/*     */       }
/*     */     }
/*     */     
/* 363 */     override_map = new_override_map;
/*     */     
/* 365 */     InetAddress bad = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
/*     */     
/* 367 */     if ((bad == null) || (bad.isAnyLocalAddress()))
/*     */     {
/* 369 */       bind_ip = "";
/*     */     }
/*     */     else
/*     */     {
/* 373 */       bind_ip = bad.getHostAddress();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isHosting(URL url_in)
/*     */   {
/* 381 */     if (tracker_ip.length() > 0)
/*     */     {
/* 383 */       String host = UrlUtils.expandIPV6Host(url_in.getHost());
/*     */       
/* 385 */       boolean result = host.equalsIgnoreCase(tracker_ip);
/*     */       
/* 387 */       if ((!result) && (tracker_ip_aliases != null))
/*     */       {
/* 389 */         result = tracker_ip_aliases.contains(host);
/*     */       }
/*     */       
/* 392 */       return result;
/*     */     }
/*     */     
/*     */ 
/* 396 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getTrackerIP()
/*     */   {
/* 403 */     return tracker_ip;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isTrackerEnabled()
/*     */   {
/* 409 */     return getAnnounceURLs().length > 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public static URL[][] getAnnounceURLs()
/*     */   {
/* 415 */     String tracker_host = COConfigurationManager.getStringParameter("Tracker IP", "");
/*     */     
/* 417 */     List urls = new ArrayList();
/*     */     
/* 419 */     if (tracker_host.length() > 0)
/*     */     {
/* 421 */       if (COConfigurationManager.getBooleanParameter("Tracker Port Enable"))
/*     */       {
/* 423 */         int port = COConfigurationManager.getIntParameter("Tracker Port", 6969);
/*     */         try
/*     */         {
/* 426 */           List l = new ArrayList();
/*     */           
/* 428 */           l.add(new URL("http://" + UrlUtils.convertIPV6Host(tracker_host) + ":" + port + "/announce"));
/*     */           
/* 430 */           List ports = stringToPorts(COConfigurationManager.getStringParameter("Tracker Port Backups"));
/*     */           
/* 432 */           for (int i = 0; i < ports.size(); i++)
/*     */           {
/* 434 */             l.add(new URL("http://" + UrlUtils.convertIPV6Host(tracker_host) + ":" + ((Integer)ports.get(i)).intValue() + "/announce"));
/*     */           }
/*     */           
/* 437 */           urls.add(l);
/*     */         }
/*     */         catch (MalformedURLException e)
/*     */         {
/* 441 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/* 445 */       if (COConfigurationManager.getBooleanParameter("Tracker Port SSL Enable"))
/*     */       {
/* 447 */         int port = COConfigurationManager.getIntParameter("Tracker Port SSL", 7000);
/*     */         try
/*     */         {
/* 450 */           List l = new ArrayList();
/*     */           
/* 452 */           l.add(new URL("https://" + UrlUtils.convertIPV6Host(tracker_host) + ":" + port + "/announce"));
/*     */           
/* 454 */           List ports = stringToPorts(COConfigurationManager.getStringParameter("Tracker Port SSL Backups"));
/*     */           
/* 456 */           for (int i = 0; i < ports.size(); i++)
/*     */           {
/* 458 */             l.add(new URL("https://" + UrlUtils.convertIPV6Host(tracker_host) + ":" + ((Integer)ports.get(i)).intValue() + "/announce"));
/*     */           }
/*     */           
/* 461 */           urls.add(l);
/*     */ 
/*     */         }
/*     */         catch (MalformedURLException e)
/*     */         {
/* 466 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/* 470 */       if (COConfigurationManager.getBooleanParameter("Tracker Port UDP Enable"))
/*     */       {
/* 472 */         int port = COConfigurationManager.getIntParameter("Tracker Port", 6969);
/*     */         
/* 474 */         boolean auth = COConfigurationManager.getBooleanParameter("Tracker Password Enable Torrent");
/*     */         try
/*     */         {
/* 477 */           List l = new ArrayList();
/*     */           
/* 479 */           l.add(new URL("udp://" + UrlUtils.convertIPV6Host(tracker_host) + ":" + port + "/announce" + (auth ? "?auth" : "")));
/*     */           
/*     */ 
/* 482 */           urls.add(l);
/*     */         }
/*     */         catch (MalformedURLException e)
/*     */         {
/* 486 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 491 */     URL[][] res = new URL[urls.size()][];
/*     */     
/* 493 */     for (int i = 0; i < urls.size(); i++)
/*     */     {
/* 495 */       List l = (List)urls.get(i);
/*     */       
/* 497 */       URL[] u = new URL[l.size()];
/*     */       
/* 499 */       l.toArray(u);
/*     */       
/* 501 */       res[i] = u;
/*     */     }
/*     */     
/* 504 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static List stringToPorts(String str)
/*     */   {
/* 511 */     str = str.replace(',', ';');
/*     */     
/* 513 */     StringTokenizer tok = new StringTokenizer(str, ";");
/*     */     
/* 515 */     List res = new ArrayList();
/*     */     
/* 517 */     while (tok.hasMoreTokens()) {
/*     */       try
/*     */       {
/* 520 */         res.add(new Integer(tok.nextToken().trim()));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 524 */         Debug.out("Invalid port entry in '" + str + "'", e);
/*     */       }
/*     */     }
/*     */     
/* 528 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static URL adjustURLForHosting(URL url_in)
/*     */   {
/* 535 */     if (isHosting(url_in))
/*     */     {
/* 537 */       String url = url_in.getProtocol() + "://";
/*     */       
/* 539 */       if (bind_ip.length() < 7)
/*     */       {
/*     */ 
/*     */ 
/* 543 */         url = url + "127.0.0.1";
/*     */ 
/*     */ 
/*     */       }
/* 547 */       else if (bind_ip.contains(":"))
/*     */       {
/* 549 */         url = url + "[" + bind_ip + "]";
/*     */       }
/*     */       else {
/* 552 */         url = url + bind_ip;
/*     */       }
/*     */       
/*     */ 
/* 556 */       int port = url_in.getPort();
/*     */       
/* 558 */       if (port != -1)
/*     */       {
/* 560 */         url = url + ":" + url_in.getPort();
/*     */       }
/*     */       
/* 563 */       url = url + url_in.getPath();
/*     */       
/* 565 */       String query = url_in.getQuery();
/*     */       
/* 567 */       if (query != null)
/*     */       {
/* 569 */         url = url + "?" + query;
/*     */       }
/*     */       try
/*     */       {
/* 573 */         return new URL(url);
/*     */       }
/*     */       catch (MalformedURLException e)
/*     */       {
/* 577 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 581 */     return url_in;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String adjustHostFromHosting(String host_in)
/*     */   {
/* 588 */     if (tracker_ip.length() > 0)
/*     */     {
/* 590 */       String address_type = AENetworkClassifier.categoriseAddress(host_in);
/*     */       
/* 592 */       String target_ip = (String)override_map.get(address_type);
/*     */       
/* 594 */       if (target_ip == null)
/*     */       {
/* 596 */         target_ip = tracker_ip;
/*     */       }
/*     */       
/* 599 */       if (isLoopback(host_in))
/*     */       {
/* 601 */         return target_ip;
/*     */       }
/*     */     }
/*     */     
/* 605 */     return host_in;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isLoopback(String host)
/*     */   {
/* 612 */     return (host.equals("127.0.0.1")) || (host.equals("0:0:0:0:0:0:0:1")) || (host.equals("::1")) || (host.equals(bind_ip));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void checkForBlacklistedURLs(URL url)
/*     */     throws IOException
/*     */   {
/* 625 */     for (int i = 0; i < BLACKLISTED_HOSTS.length; i++)
/*     */     {
/* 627 */       if ((url.getHost().equalsIgnoreCase(BLACKLISTED_HOSTS[i])) && (url.getPort() == BLACKLISTED_PORTS[i]))
/*     */       {
/*     */ 
/* 630 */         throw new IOException("http://" + BLACKLISTED_HOSTS[i] + ":" + BLACKLISTED_PORTS[i] + "/ is not a tracker");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Map mergeResponseCache(Map map1, Map map2)
/*     */   {
/* 641 */     return TRTrackerAnnouncerImpl.mergeResponseCache(map1, map2);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getPortsForURL()
/*     */   {
/* 647 */     return ports_for_url;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getPortsForURLFullCrypto()
/*     */   {
/* 653 */     return ports_for_url_with_crypto;
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
/*     */   public static void setAZTracker(URL tracker_url, boolean az_tracker)
/*     */   {
/* 678 */     String key = tracker_url.getHost() + ":" + tracker_url.getPort();
/*     */     
/* 680 */     synchronized (az_trackers)
/*     */     {
/* 682 */       boolean changed = false;
/*     */       
/* 684 */       if (az_trackers.get(key) == null)
/*     */       {
/* 686 */         if (az_tracker)
/*     */         {
/* 688 */           az_trackers.put(key, new Long(SystemTime.getCurrentTime()));
/*     */           
/* 690 */           changed = true;
/*     */         }
/*     */         
/*     */       }
/* 694 */       else if (!az_tracker)
/*     */       {
/* 696 */         if (az_trackers.remove(key) != null)
/*     */         {
/* 698 */           changed = true;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 703 */       if (changed)
/*     */       {
/* 705 */         COConfigurationManager.setParameter("Tracker Client AZ Instances", az_trackers);
/*     */       }
/*     */     }
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
/*     */   public static void setUDPProbeResult(URL tracker_url, boolean probe_ok)
/*     */   {
/* 732 */     String key = tracker_url.getHost();
/*     */     
/* 734 */     synchronized (udp_probe_results)
/*     */     {
/* 736 */       boolean changed = false;
/*     */       
/* 738 */       if (udp_probe_results.get(key) == null)
/*     */       {
/* 740 */         if (probe_ok)
/*     */         {
/*     */ 
/*     */ 
/* 744 */           if (udp_probe_results.size() > 512)
/*     */           {
/* 746 */             udp_probe_results.clear();
/*     */           }
/*     */           
/* 749 */           udp_probe_results.put(key, new Long(SystemTime.getCurrentTime()));
/*     */           
/* 751 */           changed = true;
/*     */         }
/*     */         
/*     */       }
/* 755 */       else if (!probe_ok)
/*     */       {
/* 757 */         if (udp_probe_results.remove(key) != null)
/*     */         {
/* 759 */           changed = true;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 764 */       if (changed)
/*     */       {
/* 766 */         COConfigurationManager.setParameter("Tracker Client UDP Probe Results", udp_probe_results);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addListener(TRTrackerUtilsListener l)
/*     */   {
/* 775 */     listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeListener(TRTrackerUtilsListener l)
/*     */   {
/* 782 */     listeners.remove(l);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static boolean isAZTracker(URL tracker_url)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 485	java/net/URL:getHost	()Ljava/lang/String;
/*     */     //   4: astore_1
/*     */     //   5: aload_1
/*     */     //   6: invokestatic 516	org/gudy/azureus2/core3/util/Constants:isAzureusDomain	(Ljava/lang/String;)Z
/*     */     //   9: ifeq +5 -> 14
/*     */     //   12: iconst_1
/*     */     //   13: ireturn
/*     */     //   14: getstatic 454	org/gudy/azureus2/core3/tracker/util/TRTrackerUtils:az_trackers	Ljava/util/Map;
/*     */     //   17: dup
/*     */     //   18: astore_2
/*     */     //   19: monitorenter
/*     */     //   20: getstatic 454	org/gudy/azureus2/core3/tracker/util/TRTrackerUtils:az_trackers	Ljava/util/Map;
/*     */     //   23: new 296	java/lang/StringBuilder
/*     */     //   26: dup
/*     */     //   27: invokespecial 478	java/lang/StringBuilder:<init>	()V
/*     */     //   30: aload_1
/*     */     //   31: invokevirtual 481	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   34: ldc 15
/*     */     //   36: invokevirtual 481	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   39: aload_0
/*     */     //   40: invokevirtual 484	java/net/URL:getPort	()I
/*     */     //   43: invokevirtual 480	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   46: invokevirtual 479	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   49: invokeinterface 528 2 0
/*     */     //   54: aload_2
/*     */     //   55: monitorexit
/*     */     //   56: ireturn
/*     */     //   57: astore_3
/*     */     //   58: aload_2
/*     */     //   59: monitorexit
/*     */     //   60: aload_3
/*     */     //   61: athrow
/*     */     // Line number table:
/*     */     //   Java source line #660	-> byte code offset #0
/*     */     //   Java source line #662	-> byte code offset #5
/*     */     //   Java source line #664	-> byte code offset #12
/*     */     //   Java source line #667	-> byte code offset #14
/*     */     //   Java source line #669	-> byte code offset #20
/*     */     //   Java source line #670	-> byte code offset #57
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	62	0	tracker_url	URL
/*     */     //   4	27	1	host	String
/*     */     //   18	41	2	Ljava/lang/Object;	Object
/*     */     //   57	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   20	56	57	finally
/*     */     //   57	60	57	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static boolean isUDPProbeOK(URL tracker_url)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 485	java/net/URL:getHost	()Ljava/lang/String;
/*     */     //   4: astore_1
/*     */     //   5: aload_1
/*     */     //   6: invokestatic 516	org/gudy/azureus2/core3/util/Constants:isAzureusDomain	(Ljava/lang/String;)Z
/*     */     //   9: ifeq +5 -> 14
/*     */     //   12: iconst_0
/*     */     //   13: ireturn
/*     */     //   14: getstatic 456	org/gudy/azureus2/core3/tracker/util/TRTrackerUtils:udp_probe_results	Ljava/util/Map;
/*     */     //   17: dup
/*     */     //   18: astore_2
/*     */     //   19: monitorenter
/*     */     //   20: getstatic 456	org/gudy/azureus2/core3/tracker/util/TRTrackerUtils:udp_probe_results	Ljava/util/Map;
/*     */     //   23: aload_1
/*     */     //   24: invokeinterface 528 2 0
/*     */     //   29: aload_2
/*     */     //   30: monitorexit
/*     */     //   31: ireturn
/*     */     //   32: astore_3
/*     */     //   33: aload_2
/*     */     //   34: monitorexit
/*     */     //   35: aload_3
/*     */     //   36: athrow
/*     */     // Line number table:
/*     */     //   Java source line #714	-> byte code offset #0
/*     */     //   Java source line #716	-> byte code offset #5
/*     */     //   Java source line #718	-> byte code offset #12
/*     */     //   Java source line #721	-> byte code offset #14
/*     */     //   Java source line #723	-> byte code offset #20
/*     */     //   Java source line #724	-> byte code offset #32
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	37	0	tracker_url	URL
/*     */     //   4	20	1	host	String
/*     */     //   18	16	2	Ljava/lang/Object;	Object
/*     */     //   32	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   20	31	32	finally
/*     */     //   32	35	32	finally
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/util/TRTrackerUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */