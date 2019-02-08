/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstance;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstanceManager;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import java.io.PrintStream;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.bouncycastle.util.encoders.Base64;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AddressUtils
/*     */ {
/*     */   public static final byte LAN_LOCAL_MAYBE = 0;
/*     */   public static final byte LAN_LOCAL_YES = 1;
/*     */   public static final byte LAN_LOCAL_NO = 2;
/*     */   private static boolean i2p_is_lan_limit;
/*     */   private static AZInstanceManager instance_manager;
/*     */   
/*     */   static
/*     */   {
/*  49 */     COConfigurationManager.addAndFireParameterListener("Plugin.azneti2phelper.azi2phelper.rates.use.lan", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*  57 */         AddressUtils.access$002(COConfigurationManager.getBooleanParameter("Plugin.azneti2phelper.azi2phelper.rates.use.lan", false));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static AZInstanceManager getInstanceManager()
/*     */   {
/*  67 */     if (instance_manager == null)
/*     */     {
/*  69 */       if (AzureusCoreFactory.isCoreAvailable()) {
/*     */         try
/*     */         {
/*  72 */           instance_manager = AzureusCoreFactory.getSingleton().getInstanceManager();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  81 */     return instance_manager;
/*     */   }
/*     */   
/*  84 */   private static Map host_map = null;
/*     */   
/*     */ 
/*     */ 
/*     */   public static URL adjustURL(URL url)
/*     */   {
/*  90 */     url = AEProxyFactory.getAddressMapper().internalise(url);
/*     */     
/*  92 */     if (host_map != null)
/*     */     {
/*  94 */       String rewrite = (String)host_map.get(url.getHost());
/*     */       
/*  96 */       if (rewrite != null)
/*     */       {
/*  98 */         String str = url.toExternalForm();
/*     */         try
/*     */         {
/* 101 */           int pos = str.indexOf("//") + 2;
/*     */           
/* 103 */           int pos2 = str.indexOf("/", pos);
/*     */           
/* 105 */           String host_bit = str.substring(pos, pos2);
/*     */           
/* 107 */           int pos3 = host_bit.indexOf(':');
/*     */           
/*     */           String port_bit;
/*     */           String port_bit;
/* 111 */           if (pos3 == -1)
/*     */           {
/* 113 */             port_bit = "";
/*     */           }
/*     */           else
/*     */           {
/* 117 */             port_bit = host_bit.substring(pos3);
/*     */           }
/*     */           
/* 120 */           String new_str = str.substring(0, pos) + rewrite + port_bit + str.substring(pos2);
/*     */           
/* 122 */           url = new URL(new_str);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 126 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 131 */     return url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized void addHostRedirect(String from_host, String to_host)
/*     */   {
/* 139 */     System.out.println("AddressUtils::addHostRedirect - " + from_host + " -> " + to_host);
/*     */     
/*     */     Map new_map;
/*     */     Map new_map;
/* 143 */     if (host_map == null)
/*     */     {
/* 145 */       new_map = new HashMap();
/*     */     }
/*     */     else {
/* 148 */       new_map = new HashMap(host_map);
/*     */     }
/*     */     
/* 151 */     new_map.put(from_host, to_host);
/*     */     
/* 153 */     host_map = new_map;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InetSocketAddress adjustTCPAddress(InetSocketAddress address, boolean ext_to_lan)
/*     */   {
/* 161 */     return adjustAddress(address, ext_to_lan, 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InetSocketAddress adjustUDPAddress(InetSocketAddress address, boolean ext_to_lan)
/*     */   {
/* 169 */     return adjustAddress(address, ext_to_lan, 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InetSocketAddress adjustDHTAddress(InetSocketAddress address, boolean ext_to_lan)
/*     */   {
/* 177 */     return adjustAddress(address, ext_to_lan, 3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static InetSocketAddress adjustAddress(InetSocketAddress address, boolean ext_to_lan, int port_type)
/*     */   {
/* 186 */     AZInstanceManager im = getInstanceManager();
/*     */     
/* 188 */     if ((im == null) || (!im.isInitialized()))
/*     */     {
/* 190 */       return address;
/*     */     }
/*     */     
/*     */     InetSocketAddress adjusted_address;
/*     */     InetSocketAddress adjusted_address;
/* 195 */     if (ext_to_lan)
/*     */     {
/* 197 */       adjusted_address = im.getLANAddress(address, port_type);
/*     */     }
/*     */     else
/*     */     {
/* 201 */       adjusted_address = im.getExternalAddress(address, port_type);
/*     */     }
/*     */     
/* 204 */     if (adjusted_address == null)
/*     */     {
/* 206 */       adjusted_address = address;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */     return adjusted_address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static List<String> getLANAddresses(String address)
/*     */   {
/* 220 */     List<String> result = new ArrayList();
/*     */     
/* 222 */     result.add(address);
/*     */     try
/*     */     {
/* 225 */       InetAddress ad = InetAddress.getByName(address);
/*     */       
/* 227 */       if (isLANLocalAddress(address) != 2)
/*     */       {
/* 229 */         AZInstanceManager im = getInstanceManager();
/*     */         
/* 231 */         if ((im == null) || (!im.isInitialized()))
/*     */         {
/* 233 */           return result;
/*     */         }
/*     */         
/* 236 */         AZInstance[] instances = im.getOtherInstances();
/*     */         
/* 238 */         for (int i = 0; i < instances.length; i++)
/*     */         {
/* 240 */           AZInstance instance = instances[i];
/*     */           
/* 242 */           List addresses = instance.getInternalAddresses();
/*     */           
/* 244 */           if (addresses.contains(ad))
/*     */           {
/* 246 */             for (int j = 0; j < addresses.size(); j++)
/*     */             {
/* 248 */               InetAddress ia = (InetAddress)addresses.get(j);
/*     */               
/* 250 */               String str = ia.getHostAddress();
/*     */               
/* 252 */               if (!result.contains(str))
/*     */               {
/* 254 */                 result.add(str);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 264 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static byte isLANLocalAddress(InetSocketAddress socket_address)
/*     */   {
/* 271 */     InetAddress address = socket_address.getAddress();
/*     */     
/* 273 */     return isLANLocalAddress(address);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte isLANLocalAddress(InetAddress address)
/*     */   {
/* 283 */     if (address == null)
/*     */     {
/* 285 */       return 2;
/*     */     }
/*     */     
/* 288 */     AZInstanceManager im = getInstanceManager();
/*     */     
/* 290 */     if ((im == null) || (!im.isInitialized()))
/*     */     {
/* 292 */       return 0;
/*     */     }
/*     */     
/* 295 */     return im.isLANAddress(address) ? 1 : 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static byte isLANLocalAddress(String address)
/*     */   {
/* 302 */     byte is_lan_local = 0;
/*     */     try
/*     */     {
/* 305 */       is_lan_local = isLANLocalAddress(HostNameToIPResolver.syncResolve(address));
/*     */ 
/*     */     }
/*     */     catch (UnknownHostException e) {}catch (Throwable t)
/*     */     {
/*     */ 
/* 311 */       t.printStackTrace();
/*     */     }
/*     */     
/* 314 */     return is_lan_local;
/*     */   }
/*     */   
/* 317 */   private static Set<InetAddress> pending_addresses = new HashSet();
/*     */   
/*     */   private static TimerEventPeriodic pa_timer;
/*     */   
/*     */ 
/*     */   public static void addLANRateLimitAddress(InetAddress address)
/*     */   {
/* 324 */     synchronized (pending_addresses)
/*     */     {
/* 326 */       AZInstanceManager im = getInstanceManager();
/*     */       
/* 328 */       if ((im == null) || (!im.isInitialized()))
/*     */       {
/* 330 */         pending_addresses.add(address);
/*     */         
/* 332 */         if (pa_timer == null)
/*     */         {
/* 334 */           pa_timer = SimpleTimer.addPeriodicEvent("au:pa", 250L, new TimerEventPerformer()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             public void perform(TimerEvent event)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 345 */               synchronized (AddressUtils.pending_addresses)
/*     */               {
/* 347 */                 AZInstanceManager im = AddressUtils.access$200();
/*     */                 
/* 349 */                 if ((im != null) && (im.isInitialized()))
/*     */                 {
/* 351 */                   for (InetAddress address : AddressUtils.pending_addresses) {
/*     */                     try
/*     */                     {
/* 354 */                       im.addLANAddress(address);
/*     */                     }
/*     */                     catch (Throwable e) {}
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/* 361 */                   AddressUtils.pending_addresses.clear();
/*     */                   
/* 363 */                   AddressUtils.pa_timer.cancel();
/*     */                   
/* 365 */                   AddressUtils.access$302(null);
/*     */                 }
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       else {
/* 373 */         im.addLANAddress(address);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeLANRateLimitAddress(InetAddress address)
/*     */   {
/* 382 */     synchronized (pending_addresses)
/*     */     {
/* 384 */       AZInstanceManager im = getInstanceManager();
/*     */       
/* 386 */       if ((im == null) || (!im.isInitialized()))
/*     */       {
/* 388 */         pending_addresses.remove(address);
/*     */       }
/*     */       else
/*     */       {
/* 392 */         im.removeLANAddress(address);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean applyLANRateLimits(InetSocketAddress address)
/*     */   {
/* 401 */     if (i2p_is_lan_limit)
/*     */     {
/* 403 */       if (address.isUnresolved())
/*     */       {
/* 405 */         return AENetworkClassifier.categoriseAddress(address) == "I2P";
/*     */       }
/*     */     }
/*     */     
/* 409 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isGlobalAddressV6(InetAddress addr)
/*     */   {
/* 416 */     return ((addr instanceof Inet6Address)) && (!addr.isAnyLocalAddress()) && (!addr.isLinkLocalAddress()) && (!addr.isLoopbackAddress()) && (!addr.isMulticastAddress()) && (!addr.isSiteLocalAddress()) && (!((Inet6Address)addr).isIPv4CompatibleAddress());
/*     */   }
/*     */   
/*     */   public static boolean isTeredo(InetAddress addr)
/*     */   {
/* 421 */     if (!(addr instanceof Inet6Address))
/* 422 */       return false;
/* 423 */     byte[] bytes = addr.getAddress();
/*     */     
/* 425 */     return (bytes[0] == 32) && (bytes[1] == 1) && (bytes[2] == 0) && (bytes[3] == 0);
/*     */   }
/*     */   
/*     */   public static boolean is6to4(InetAddress addr)
/*     */   {
/* 430 */     if (!(addr instanceof Inet6Address))
/* 431 */       return false;
/* 432 */     byte[] bytes = addr.getAddress();
/*     */     
/* 434 */     return (bytes[0] == 32) && (bytes[1] == 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InetAddress pickBestGlobalV6Address(List<InetAddress> addrs)
/*     */   {
/* 445 */     InetAddress bestPick = null;
/* 446 */     int currentRanking = 0;
/* 447 */     for (InetAddress addr : addrs)
/*     */     {
/* 449 */       if (isGlobalAddressV6(addr))
/*     */       {
/* 451 */         int ranking = 3;
/* 452 */         if (isTeredo(addr)) {
/* 453 */           ranking = 1;
/* 454 */         } else if (is6to4(addr)) {
/* 455 */           ranking = 2;
/*     */         }
/* 457 */         if (ranking > currentRanking)
/*     */         {
/* 459 */           bestPick = addr;
/* 460 */           currentRanking = ranking;
/*     */         }
/*     */       }
/*     */     }
/* 464 */     return bestPick;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InetAddress getByName(String host)
/*     */     throws UnknownHostException
/*     */   {
/* 473 */     if (AENetworkClassifier.categoriseAddress(host) == "Public")
/*     */     {
/* 475 */       return InetAddress.getByName(host);
/*     */     }
/*     */     
/* 478 */     throw new UnknownHostException(host);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InetAddress[] getAllByName(String host)
/*     */     throws UnknownHostException
/*     */   {
/* 487 */     if (AENetworkClassifier.categoriseAddress(host) == "Public")
/*     */     {
/* 489 */       return InetAddress.getAllByName(host);
/*     */     }
/*     */     
/* 492 */     throw new UnknownHostException(host);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static byte[] getAddressBytes(InetSocketAddress address)
/*     */   {
/* 499 */     if (address.isUnresolved()) {
/*     */       try
/*     */       {
/* 502 */         return address.getHostName().getBytes("ISO8859-1");
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 506 */         Debug.out(e);
/*     */         
/* 508 */         return null;
/*     */       }
/*     */     }
/*     */     
/* 512 */     return address.getAddress().getAddress();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getHostAddress(InetSocketAddress address)
/*     */   {
/* 520 */     if (address.isUnresolved())
/*     */     {
/* 522 */       return address.getHostName();
/*     */     }
/*     */     
/*     */ 
/* 526 */     return address.getAddress().getHostAddress();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getHostNameNoResolve(InetSocketAddress address)
/*     */   {
/* 534 */     InetAddress i_address = address.getAddress();
/*     */     
/* 536 */     if (i_address == null)
/*     */     {
/* 538 */       return address.getHostName();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 545 */     String str = i_address.toString();
/*     */     
/* 547 */     int pos = str.indexOf('/');
/*     */     
/* 549 */     if (pos == -1)
/*     */     {
/*     */ 
/*     */ 
/* 553 */       System.out.println("InetAddress::toString not returning expected result: " + str);
/*     */       
/* 555 */       return i_address.getHostAddress();
/*     */     }
/*     */     
/* 558 */     if (pos > 0)
/*     */     {
/* 560 */       return str.substring(0, pos);
/*     */     }
/*     */     
/*     */ 
/* 564 */     return str.substring(pos + 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String convertToShortForm(String address)
/*     */   {
/* 573 */     int address_length = address.length();
/*     */     
/* 575 */     if (address_length > 256)
/*     */     {
/*     */       String to_decode;
/*     */       
/* 579 */       if (address.endsWith(".i2p"))
/*     */       {
/* 581 */         to_decode = address.substring(0, address.length() - 4);
/*     */       } else { String to_decode;
/* 583 */         if (address.indexOf('.') == -1)
/*     */         {
/* 585 */           to_decode = address;
/*     */         }
/*     */         else
/*     */         {
/* 589 */           return address;
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/*     */         String to_decode;
/* 595 */         char[] encoded = to_decode.toCharArray();
/*     */         
/* 597 */         for (int i = 0; i < encoded.length; i++)
/*     */         {
/* 599 */           char c = encoded[i];
/*     */           
/* 601 */           if (c == '~') {
/* 602 */             encoded[i] = '/';
/* 603 */           } else if (c == '-') {
/* 604 */             encoded[i] = '+';
/*     */           }
/*     */         }
/*     */         
/* 608 */         byte[] decoded = Base64.decode(encoded);
/*     */         
/* 610 */         byte[] hash = MessageDigest.getInstance("SHA-256").digest(decoded);
/*     */         
/* 612 */         return Base32.encode(hash).toLowerCase(Locale.US) + ".b32.i2p";
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 616 */         return null;
/*     */       }
/*     */     }
/*     */     
/* 620 */     return address;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AddressUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */