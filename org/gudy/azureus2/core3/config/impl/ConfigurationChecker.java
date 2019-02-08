/*     */ package org.gudy.azureus2.core3.config.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.custom.CustomizationManager;
/*     */ import com.aelitis.azureus.core.custom.CustomizationManagerFactory;
/*     */ import com.aelitis.azureus.core.util.FeatureAvailability;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.InetAddress;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigurationChecker
/*     */ {
/*  59 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*     */   private static final boolean system_properties_set = false;
/*     */   
/*  63 */   private static boolean checked = false;
/*  64 */   private static boolean new_install = false;
/*     */   
/*  66 */   private static final AEMonitor class_mon = new AEMonitor("ConfigChecker");
/*     */   
/*  68 */   private static boolean new_version = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void setSystemProperties()
/*     */   {
/*     */     try
/*     */     {
/*  77 */       class_mon.enter();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  84 */       COConfigurationManager.preInitialise();
/*     */       
/*     */ 
/*     */ 
/*  88 */       String app_path = SystemProperties.getApplicationPath();
/*  89 */       String user_path = SystemProperties.getUserPath();
/*     */       
/*  91 */       loadProperties(app_path);
/*     */       
/*  93 */       if (!app_path.equals(user_path))
/*     */       {
/*  95 */         loadProperties(user_path);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */       COConfigurationManager.addAndFireParameterListener("IPV6 Prefer Addresses", new ParameterListener()
/*     */       {
/*     */ 
/*     */ 
/* 106 */         private boolean done_something = false;
/*     */         
/*     */ 
/*     */ 
/*     */         public void parameterChanged(String name)
/*     */         {
/* 112 */           boolean prefer_ipv6 = COConfigurationManager.getBooleanParameter(name);
/*     */           
/* 114 */           boolean existing = !System.getProperty("java.net.preferIPv6Addresses", "false").equalsIgnoreCase("false");
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 119 */           if ((existing) && (!this.done_something))
/*     */           {
/* 121 */             return;
/*     */           }
/*     */           
/* 124 */           if (existing != prefer_ipv6)
/*     */           {
/* 126 */             this.done_something = true;
/*     */             
/* 128 */             System.setProperty("java.net.preferIPv6Addresses", prefer_ipv6 ? "true" : "false");
/*     */             try
/*     */             {
/* 131 */               Field field = InetAddress.class.getDeclaredField("preferIPv6Address");
/*     */               
/* 133 */               field.setAccessible(true);
/*     */               
/* 135 */               field.setBoolean(null, prefer_ipv6);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 139 */               Debug.out("Failed to update 'preferIPv6Address'", e);
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */       
/* 145 */       if ((Constants.isWindowsVistaOrHigher) && (Constants.isJava7OrHigher))
/*     */       {
/* 147 */         COConfigurationManager.addAndFireParameterListener("IPV4 Prefer Stack", new ParameterListener()
/*     */         {
/*     */ 
/*     */ 
/* 151 */           private boolean done_something = false;
/*     */           
/*     */ 
/*     */ 
/*     */           public void parameterChanged(String name)
/*     */           {
/* 157 */             boolean prefer_ipv4 = COConfigurationManager.getBooleanParameter(name);
/*     */             
/* 159 */             boolean existing = !System.getProperty("java.net.preferIPv4Stack", "false").equalsIgnoreCase("false");
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 164 */             if ((existing) && (!this.done_something))
/*     */             {
/* 166 */               return;
/*     */             }
/*     */             
/* 169 */             if (existing != prefer_ipv4)
/*     */             {
/* 171 */               this.done_something = true;
/*     */               
/* 173 */               System.setProperty("java.net.preferIPv4Stack", prefer_ipv4 ? "true" : "false");
/*     */               try
/*     */               {
/* 176 */                 getClass();Class<?> plainSocketImpl = Class.forName("java.net.PlainSocketImpl");
/*     */                 
/* 178 */                 Field pref_field = plainSocketImpl.getDeclaredField("preferIPv4Stack");
/*     */                 
/* 180 */                 pref_field.setAccessible(true);
/*     */                 
/* 182 */                 pref_field.setBoolean(null, prefer_ipv4);
/*     */                 
/* 184 */                 Field dual_field = plainSocketImpl.getDeclaredField("useDualStackImpl");
/*     */                 
/* 186 */                 dual_field.setAccessible(true);
/*     */                 
/* 188 */                 dual_field.setBoolean(null, !prefer_ipv4);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 192 */                 Debug.out("Failed to update 'preferIPv4Stack'", e);
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 202 */       int connect_timeout = COConfigurationManager.getIntParameter("Tracker Client Connect Timeout");
/* 203 */       int read_timeout = COConfigurationManager.getIntParameter("Tracker Client Read Timeout");
/*     */       
/* 205 */       if (Logger.isEnabled()) {
/* 206 */         Logger.log(new LogEvent(LOGID, "TrackerClient: connect timeout = " + connect_timeout + ", read timeout = " + read_timeout));
/*     */       }
/*     */       
/* 209 */       System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(connect_timeout * 1000));
/*     */       
/*     */ 
/*     */ 
/* 213 */       System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(read_timeout * 1000));
/*     */       
/*     */ 
/*     */ 
/* 217 */       if (COConfigurationManager.getBooleanParameter("Enable.Proxy")) {
/* 218 */         String host = COConfigurationManager.getStringParameter("Proxy.Host");
/* 219 */         String port = COConfigurationManager.getStringParameter("Proxy.Port");
/* 220 */         String user = COConfigurationManager.getStringParameter("Proxy.Username");
/* 221 */         String pass = COConfigurationManager.getStringParameter("Proxy.Password");
/*     */         
/* 223 */         if (user.trim().equalsIgnoreCase("<none>")) {
/* 224 */           user = "";
/*     */         }
/*     */         
/* 227 */         if (COConfigurationManager.getBooleanParameter("Enable.SOCKS")) {
/* 228 */           System.setProperty("socksProxyHost", host);
/* 229 */           System.setProperty("socksProxyPort", port);
/*     */           
/* 231 */           if (user.length() > 0) {
/* 232 */             System.setProperty("java.net.socks.username", user);
/* 233 */             System.setProperty("java.net.socks.password", pass);
/*     */           }
/*     */         }
/*     */         else {
/* 237 */           System.setProperty("http.proxyHost", host);
/* 238 */           System.setProperty("http.proxyPort", port);
/* 239 */           System.setProperty("https.proxyHost", host);
/* 240 */           System.setProperty("https.proxyPort", port);
/*     */           
/* 242 */           if (user.length() > 0) {
/* 243 */             System.setProperty("http.proxyUser", user);
/* 244 */             System.setProperty("http.proxyPassword", pass);
/* 245 */             System.setProperty("https.proxyUser", user);
/* 246 */             System.setProperty("https.proxyPassword", pass);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 251 */       SESecurityManager.initialise();
/*     */     }
/*     */     finally {
/* 254 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected static void loadProperties(String dir)
/*     */   {
/*     */     try
/*     */     {
/* 263 */       File prop_file = new File(dir, "azureus.properties");
/*     */       
/* 265 */       if (prop_file.exists())
/*     */       {
/* 267 */         Logger.log(new LogEvent(LOGID, "Loading properties file from " + prop_file.getAbsolutePath()));
/*     */         
/* 269 */         Properties props = new Properties();
/*     */         
/* 271 */         InputStream is = new FileInputStream(prop_file);
/*     */         try
/*     */         {
/* 274 */           props.load(is);
/*     */           
/* 276 */           Iterator it = props.entrySet().iterator();
/*     */           
/* 278 */           while (it.hasNext())
/*     */           {
/* 280 */             Map.Entry entry = (Map.Entry)it.next();
/*     */             
/* 282 */             String key = (String)entry.getKey();
/* 283 */             String value = (String)entry.getValue();
/*     */             
/* 285 */             Logger.log(new LogEvent(LOGID, "    " + key + "=" + value));
/*     */             
/* 287 */             System.setProperty(key, value);
/*     */           }
/*     */         }
/*     */         finally {
/* 291 */           is.close();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */   public static void checkConfiguration()
/*     */   {
/*     */     try
/*     */     {
/* 303 */       class_mon.enter();
/*     */       
/* 305 */       if (checked)
/*     */         return;
/* 307 */       checked = true;
/*     */       
/* 309 */       boolean changed = CustomizationManagerFactory.getSingleton().preInitialize();
/*     */       
/* 311 */       String last_version = COConfigurationManager.getStringParameter("azureus.version", "");
/*     */       
/* 313 */       String this_version = "5.7.6.0";
/*     */       
/* 315 */       if (!last_version.equals(this_version)) {
/* 316 */         if (!Constants.getBaseVersion(last_version).equals(Constants.getBaseVersion()))
/*     */         {
/* 318 */           COConfigurationManager.setParameter("Last Version", last_version);
/* 319 */           new_version = true;
/*     */         }
/*     */         
/* 322 */         if (!COConfigurationManager.hasParameter("First Recorded Version", true)) {
/* 323 */           COConfigurationManager.setParameter("First Recorded Version", last_version.length() == 0 ? this_version : last_version);
/*     */         }
/*     */         else {
/* 326 */           String sFirstVersion = COConfigurationManager.getStringParameter("First Recorded Version");
/* 327 */           String sMinVersion = Constants.compareVersions(sFirstVersion, this_version) > 0 ? this_version : sFirstVersion;
/*     */           
/* 329 */           if (last_version.length() > 0) {
/* 330 */             sMinVersion = Constants.compareVersions(sMinVersion, last_version) > 0 ? last_version : sMinVersion;
/*     */           }
/*     */           
/* 333 */           COConfigurationManager.setParameter("First Recorded Version", sMinVersion);
/*     */         }
/*     */         
/*     */ 
/* 337 */         COConfigurationManager.setParameter("azureus.version", this_version);
/*     */         
/* 339 */         changed = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 345 */       if (last_version.length() == 0)
/*     */       {
/* 347 */         new_install = true;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 355 */         if (COConfigurationManager.doesParameterNonDefaultExist("diagnostics.tidy_close"))
/*     */         {
/* 357 */           if (!COConfigurationManager.doesParameterNonDefaultExist("Tracker Port Enable"))
/*     */           {
/* 359 */             COConfigurationManager.setParameter("Tracker Port Enable", true);
/*     */             
/* 361 */             changed = true;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 366 */         if (!COConfigurationManager.doesParameterNonDefaultExist("User Mode")) {
/* 367 */           COConfigurationManager.setParameter("User Mode", 0);
/* 368 */           changed = true;
/*     */         }
/*     */         
/*     */ 
/* 372 */         if (!COConfigurationManager.doesParameterNonDefaultExist("TCP.Listen.Port")) {
/* 373 */           int rand_port = RandomUtils.generateRandomNetworkListenPort();
/* 374 */           COConfigurationManager.setParameter("TCP.Listen.Port", rand_port);
/* 375 */           COConfigurationManager.setParameter("UDP.Listen.Port", rand_port);
/* 376 */           COConfigurationManager.setParameter("UDP.NonData.Listen.Port", rand_port);
/* 377 */           changed = true;
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 384 */         if ((COConfigurationManager.getBooleanParameter("network.tcp.enable_safe_selector_mode")) && ((!Constants.isWindows) || ((!Constants.JAVA_VERSION.startsWith("1.4")) && (!Constants.JAVA_VERSION.startsWith("1.5")))))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 392 */           COConfigurationManager.removeParameter("network.tcp.enable_safe_selector_mode");
/* 393 */           changed = true;
/*     */         }
/*     */         
/*     */ 
/* 397 */         if (COConfigurationManager.doesParameterNonDefaultExist("TCP.Announce.Port"))
/*     */         {
/* 399 */           COConfigurationManager.setParameter("TCP.Listen.Port.Override", COConfigurationManager.getStringParameter("TCP.Announce.Port", ""));
/* 400 */           COConfigurationManager.removeParameter("TCP.Announce.Port");
/* 401 */           changed = true;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 408 */         if (!COConfigurationManager.doesParameterNonDefaultExist("User Mode")) {
/* 409 */           COConfigurationManager.setParameter("User Mode", 2);
/* 410 */           changed = true;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 416 */       if (!COConfigurationManager.doesParameterNonDefaultExist("UDP.Listen.Port")) {
/* 417 */         COConfigurationManager.setParameter("UDP.Listen.Port", COConfigurationManager.getIntParameter("TCP.Listen.Port"));
/*     */         
/* 419 */         changed = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 424 */       if (!COConfigurationManager.getBooleanParameter("Plugin.DHT.dht.portdefault", true))
/*     */       {
/* 426 */         COConfigurationManager.removeParameter("Plugin.DHT.dht.portdefault");
/*     */         
/* 428 */         int tcp_port = COConfigurationManager.getIntParameter("TCP.Listen.Port");
/* 429 */         int udp_port = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/*     */         
/* 431 */         int dht_port = COConfigurationManager.getIntParameter("Plugin.DHT.dht.port", udp_port);
/*     */         
/* 433 */         if (dht_port != udp_port)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 438 */           if (tcp_port == udp_port)
/*     */           {
/* 440 */             COConfigurationManager.setParameter("UDP.Listen.Port", dht_port);
/*     */           }
/*     */         }
/*     */         
/* 444 */         changed = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 449 */       if (!COConfigurationManager.doesParameterNonDefaultExist("UDP.NonData.Listen.Port")) {
/* 450 */         COConfigurationManager.setParameter("UDP.NonData.Listen.Port", COConfigurationManager.getIntParameter("UDP.Listen.Port"));
/*     */         
/* 452 */         changed = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 459 */       int udp1 = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/* 460 */       int udp2 = COConfigurationManager.getIntParameter("UDP.NonData.Listen.Port");
/*     */       
/* 462 */       if (udp1 != udp2)
/*     */       {
/* 464 */         COConfigurationManager.setParameter("UDP.NonData.Listen.Port", udp1);
/*     */         
/* 466 */         changed = true;
/*     */       }
/*     */       
/*     */ 
/* 470 */       boolean randomize_ports = COConfigurationManager.getBooleanParameter("Listen.Port.Randomize.Enable");
/*     */       
/* 472 */       if (randomize_ports)
/*     */       {
/* 474 */         String random_range = COConfigurationManager.getStringParameter("Listen.Port.Randomize.Range");
/*     */         
/* 476 */         if ((random_range == null) || (random_range.trim().length() == 0))
/*     */         {
/* 478 */           random_range = "10000-65535";
/*     */         }
/*     */         else
/*     */         {
/* 482 */           random_range = random_range.trim();
/*     */         }
/*     */         
/* 485 */         int min_port = 10000;
/* 486 */         int max_port = 65535;
/*     */         
/* 488 */         String[] bits = random_range.split("-");
/*     */         
/* 490 */         boolean valid = bits.length == 2;
/*     */         
/* 492 */         if (!valid)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 497 */           char[] chars = random_range.toCharArray();
/*     */           
/* 499 */           for (int i = 0; i < chars.length - 1; i++)
/*     */           {
/* 501 */             if (!Character.isDigit(chars[i]))
/*     */             {
/* 503 */               bits = new String[] { random_range.substring(0, i), random_range.substring(i + 1) };
/*     */               
/* 505 */               valid = true;
/*     */               
/* 507 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 512 */         if (valid)
/*     */         {
/* 514 */           String lhs = bits[0].trim();
/*     */           
/* 516 */           if (lhs.length() > 0) {
/*     */             try
/*     */             {
/* 519 */               min_port = Integer.parseInt(lhs);
/*     */               
/* 521 */               valid = (min_port > 0) && (min_port < 65536);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 525 */               valid = false;
/*     */             }
/*     */           }
/*     */           
/* 529 */           String rhs = bits[1].trim();
/*     */           
/* 531 */           if (rhs.length() > 0) {
/*     */             try
/*     */             {
/* 534 */               max_port = Integer.parseInt(rhs);
/*     */               
/* 536 */               valid = (max_port > 0) && (max_port < 65536);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 540 */               valid = false;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 545 */         if (valid)
/*     */         {
/* 547 */           boolean randomize_together = COConfigurationManager.getBooleanParameter("Listen.Port.Randomize.Together");
/*     */           
/* 549 */           if (randomize_together)
/*     */           {
/* 551 */             int port = RandomUtils.generateRandomNetworkListenPort(min_port, max_port);
/*     */             
/* 553 */             COConfigurationManager.setParameter("TCP.Listen.Port", port);
/* 554 */             COConfigurationManager.setParameter("UDP.Listen.Port", port);
/* 555 */             COConfigurationManager.setParameter("UDP.NonData.Listen.Port", port);
/*     */           }
/*     */           else
/*     */           {
/* 559 */             int old_udp1 = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/* 560 */             int old_udp2 = COConfigurationManager.getIntParameter("UDP.NonData.Listen.Port");
/*     */             
/* 562 */             int port1 = RandomUtils.generateRandomNetworkListenPort(min_port, max_port);
/*     */             
/* 564 */             COConfigurationManager.setParameter("TCP.Listen.Port", port1);
/*     */             
/* 566 */             int port2 = RandomUtils.generateRandomNetworkListenPort(min_port, max_port);
/*     */             
/* 568 */             COConfigurationManager.setParameter("UDP.Listen.Port", port2);
/*     */             
/* 570 */             if (old_udp1 == old_udp2)
/*     */             {
/* 572 */               COConfigurationManager.setParameter("UDP.NonData.Listen.Port", port2);
/*     */             }
/*     */             else
/*     */             {
/* 576 */               int port3 = RandomUtils.generateRandomNetworkListenPort(min_port, max_port);
/*     */               
/* 578 */               COConfigurationManager.setParameter("UDP.NonData.Listen.Port", port3);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 584 */       int tcp_port = COConfigurationManager.getIntParameter("TCP.Listen.Port");
/*     */       
/*     */ 
/*     */ 
/* 588 */       if ((tcp_port == Constants.INSTANCE_PORT) || ((tcp_port >= 45100) && (tcp_port <= 45103)))
/*     */       {
/* 590 */         int new_tcp_port = RandomUtils.generateRandomNetworkListenPort();
/*     */         
/* 592 */         COConfigurationManager.setParameter("TCP.Listen.Port", new_tcp_port);
/*     */         
/* 594 */         if (COConfigurationManager.getIntParameter("UDP.Listen.Port") == tcp_port)
/*     */         {
/* 596 */           COConfigurationManager.setParameter("UDP.Listen.Port", new_tcp_port);
/*     */         }
/*     */         
/* 599 */         if (COConfigurationManager.getIntParameter("UDP.NonData.Listen.Port") == tcp_port)
/*     */         {
/* 601 */           COConfigurationManager.setParameter("UDP.NonData.Listen.Port", new_tcp_port);
/*     */         }
/*     */         
/* 604 */         changed = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 609 */       if (!COConfigurationManager.doesParameterDefaultExist("Tracker Key Enable Client"))
/*     */       {
/* 611 */         boolean old_value = COConfigurationManager.getBooleanParameter("Tracker Key Enable");
/*     */         
/* 613 */         COConfigurationManager.setParameter("Tracker Key Enable Client", old_value);
/*     */         
/* 615 */         COConfigurationManager.setParameter("Tracker Key Enable Server", old_value);
/*     */         
/* 617 */         changed = true;
/*     */       }
/*     */       
/* 620 */       int maxUpSpeed = COConfigurationManager.getIntParameter("Max Upload Speed KBs", 0);
/* 621 */       int maxDownSpeed = COConfigurationManager.getIntParameter("Max Download Speed KBs", 0);
/*     */       
/* 623 */       if ((maxUpSpeed > 0) && (maxUpSpeed < 5) && ((maxDownSpeed == 0) || (maxDownSpeed > 2 * maxUpSpeed)))
/*     */       {
/*     */ 
/*     */ 
/* 627 */         changed = true;
/* 628 */         COConfigurationManager.setParameter("Max Upload Speed KBs", 5);
/*     */       }
/*     */       
/*     */ 
/* 632 */       int peersRatio = COConfigurationManager.getIntParameter("Stop Peers Ratio", 0);
/* 633 */       if (peersRatio > 14) {
/* 634 */         COConfigurationManager.setParameter("Stop Peers Ratio", 14);
/* 635 */         changed = true;
/*     */       }
/*     */       
/* 638 */       int minQueueingShareRatio = COConfigurationManager.getIntParameter("StartStopManager_iFirstPriority_ShareRatio");
/* 639 */       if (minQueueingShareRatio < 500) {
/* 640 */         COConfigurationManager.setParameter("StartStopManager_iFirstPriority_ShareRatio", 500);
/* 641 */         changed = true;
/*     */       }
/*     */       
/* 644 */       int iSeedingMin = COConfigurationManager.getIntParameter("StartStopManager_iFirstPriority_SeedingMinutes");
/* 645 */       if ((iSeedingMin < 90) && (iSeedingMin != 0)) {
/* 646 */         COConfigurationManager.setParameter("StartStopManager_iFirstPriority_SeedingMinutes", 90);
/* 647 */         changed = true;
/*     */       }
/*     */       
/* 650 */       int iDLMin = COConfigurationManager.getIntParameter("StartStopManager_iFirstPriority_DLMinutes");
/* 651 */       if ((iDLMin < 180) && (iDLMin != 0)) {
/* 652 */         COConfigurationManager.setParameter("StartStopManager_iFirstPriority_DLMinutes", 180);
/* 653 */         changed = true;
/*     */       }
/*     */       
/* 656 */       int iIgnoreSPRatio = COConfigurationManager.getIntParameter("StartStopManager_iFirstPriority_ignoreSPRatio");
/* 657 */       if ((iIgnoreSPRatio < 10) && (iIgnoreSPRatio != 0)) {
/* 658 */         COConfigurationManager.setParameter("StartStopManager_iFirstPriority_ignoreSPRatio", 10);
/* 659 */         changed = true;
/*     */       }
/*     */       
/* 662 */       String uniqueId = COConfigurationManager.getStringParameter("ID", null);
/* 663 */       if ((uniqueId == null) || (uniqueId.length() != 20)) {
/* 664 */         uniqueId = RandomUtils.generateRandomAlphanumerics(20);
/* 665 */         COConfigurationManager.setParameter("ID", uniqueId);
/* 666 */         changed = true;
/*     */       }
/*     */       
/* 669 */       int cache_max = COConfigurationManager.getIntParameter("diskmanager.perf.cache.size");
/* 670 */       if (cache_max > COConfigurationManager.CONFIG_CACHE_SIZE_MAX_MB) {
/* 671 */         COConfigurationManager.setParameter("diskmanager.perf.cache.size", COConfigurationManager.CONFIG_CACHE_SIZE_MAX_MB);
/* 672 */         changed = true;
/*     */       }
/* 674 */       if (cache_max < 1) {
/* 675 */         COConfigurationManager.setParameter("diskmanager.perf.cache.size", 4);
/* 676 */         changed = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 682 */       if (Constants.isOSX) {
/* 683 */         boolean sound = COConfigurationManager.getBooleanParameter("Play Download Finished", true);
/*     */         
/*     */ 
/* 686 */         boolean confirmExit = COConfigurationManager.getBooleanParameter("confirmationOnExit");
/*     */         
/* 688 */         if ((sound) || (confirmExit)) {
/* 689 */           COConfigurationManager.setParameter("Play Download Finished", false);
/* 690 */           COConfigurationManager.setParameter("confirmationOnExit", false);
/* 691 */           changed = true;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 696 */       if (Constants.isOSX) {
/* 697 */         if (COConfigurationManager.getBooleanParameter("enable_small_osx_fonts")) {
/* 698 */           System.setProperty("org.eclipse.swt.internal.carbon.smallFonts", "1");
/*     */         }
/*     */         else {
/* 701 */           System.getProperties().remove("org.eclipse.swt.internal.carbon.smallFonts");
/*     */         }
/* 703 */         System.setProperty("org.eclipse.swt.internal.carbon.noFocusRing", "1");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 708 */       String[] path_params = { "Default save path", "General_sDefaultTorrent_Directory", "Watch Torrent Folder Path", "Completed Files Directory" };
/*     */       
/*     */ 
/*     */ 
/* 712 */       for (int i = 0; i < path_params.length; i++) {
/* 713 */         if (path_params[i].endsWith(SystemProperties.SEP)) {
/* 714 */           String new_path = path_params[i].substring(0, path_params[i].length() - 1);
/* 715 */           COConfigurationManager.setParameter(path_params[i], new_path);
/* 716 */           changed = true;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 724 */       if (ConfigurationManager.getInstance().doesParameterNonDefaultExist("General_bEnableLanguageUpdate")) {
/* 725 */         File user_dir = new File(SystemProperties.getUserPath());
/* 726 */         File[] files = user_dir.listFiles(new FilenameFilter() {
/*     */           public boolean accept(File dir, String name) {
/* 728 */             if ((name.startsWith("MessagesBundle")) && (name.endsWith(".properties"))) {
/* 729 */               return true;
/*     */             }
/* 731 */             return false;
/*     */           }
/*     */         });
/*     */         
/* 735 */         if (files != null) {
/* 736 */           for (int i = 0; i < files.length; i++) {
/* 737 */             File file = files[i];
/* 738 */             if (file.exists()) {
/* 739 */               if (Logger.isEnabled()) {
/* 740 */                 Logger.log(new LogEvent(LOGID, 1, "ConfigurationChecker:: removing old language file: " + file.getAbsolutePath()));
/*     */               }
/*     */               
/* 743 */               file.renameTo(new File(file.getParentFile(), "delme" + file.getName()));
/*     */             }
/*     */           }
/*     */         }
/* 747 */         ConfigurationManager.getInstance().removeParameter("General_bEnableLanguageUpdate");
/* 748 */         changed = true;
/*     */       }
/*     */       
/*     */ 
/* 752 */       String CFG_CONFIRM_DELETE_CONTENT = "confirm.delete.content";
/* 753 */       if (ConfigurationManager.getInstance().doesParameterNonDefaultExist("confirm.delete.content")) {
/* 754 */         boolean confirm = COConfigurationManager.getBooleanParameter("confirm.delete.content");
/* 755 */         if ((!confirm) && (!ConfigurationManager.getInstance().doesParameterNonDefaultExist("tb.confirm.delete.content")))
/*     */         {
/*     */ 
/* 758 */           COConfigurationManager.setParameter("tb.confirm.delete.content", 1);
/*     */         }
/* 760 */         COConfigurationManager.removeParameter("confirm.delete.content");
/* 761 */         changed = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 766 */       if (System.getProperty("azureus.internal.browser.disable", "0").equals("1"))
/*     */       {
/* 768 */         if (!COConfigurationManager.getBooleanParameter("browser.internal.disable", false))
/*     */         {
/* 770 */           COConfigurationManager.setParameter("browser.internal.disable", true);
/*     */           
/* 772 */           changed = true;
/*     */         }
/*     */       }
/*     */       
/* 776 */       if (COConfigurationManager.getBooleanParameter("azpromo.dump.disable.plugin", false))
/*     */       {
/*     */ 
/*     */ 
/* 780 */         if (!COConfigurationManager.doesParameterNonDefaultExist("browser.internal.disable"))
/*     */         {
/* 782 */           COConfigurationManager.setParameter("browser.internal.disable", true);
/*     */           
/* 784 */           changed = true;
/*     */         }
/*     */       }
/*     */       
/* 788 */       COConfigurationManager.addAndFireParameterListener("browser.internal.disable", new ParameterListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void parameterChanged(String parameterName)
/*     */         {
/*     */ 
/*     */ 
/* 796 */           if (COConfigurationManager.getBooleanParameter("browser.internal.disable", false))
/*     */           {
/* 798 */             COConfigurationManager.setParameter("azpromo.dump.disable.plugin", true);
/*     */             
/* 800 */             COConfigurationManager.setDirty();
/*     */           }
/*     */         }
/*     */       });
/*     */       
/*     */ 
/* 806 */       if (FeatureAvailability.isAutoSpeedDefaultClassic())
/*     */       {
/* 808 */         ConfigurationDefaults.getInstance().addParameter("Auto Upload Speed Version", 1);
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 814 */         if (last_version.equals("5.7.1.0"))
/*     */         {
/* 816 */           long max_peers = COConfigurationManager.getLongParameter("Max.Peer.Connections.Per.Torrent");
/*     */           
/* 818 */           if (max_peers == 1023L)
/*     */           {
/* 820 */             COConfigurationManager.setParameter("Max.Peer.Connections.Per.Torrent", 0);
/*     */             
/* 822 */             changed = true;
/*     */           }
/*     */           
/* 825 */           long max_seeds = COConfigurationManager.getLongParameter("Max.Peer.Connections.Per.Torrent.When.Seeding");
/*     */           
/* 827 */           if (max_seeds == 1023L)
/*     */           {
/* 829 */             COConfigurationManager.setParameter("Max.Peer.Connections.Per.Torrent.When.Seeding", 0);
/*     */             
/* 831 */             changed = true;
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 838 */       int check_level = COConfigurationManager.getIntParameter("config.checker.level", 0);
/*     */       
/* 840 */       if (check_level < 1)
/*     */       {
/* 842 */         COConfigurationManager.setParameter("config.checker.level", 1);
/*     */         
/* 844 */         changed = true;
/*     */         
/*     */ 
/*     */ 
/* 848 */         String[] params = { "Max Uploads", "enable.seedingonly.maxuploads", "Max Uploads Seeding", "Max.Peer.Connections.Per.Torrent", "Max.Peer.Connections.Per.Torrent.When.Seeding.Enable", "Max.Peer.Connections.Per.Torrent.When.Seeding", "Max.Peer.Connections.Total", "Max Seeds Per Torrent" };
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 859 */         boolean has_been_set = false;
/*     */         
/* 861 */         for (String param : params)
/*     */         {
/* 863 */           if (COConfigurationManager.doesParameterNonDefaultExist(param))
/*     */           {
/* 865 */             has_been_set = true;
/*     */             
/* 867 */             break;
/*     */           }
/*     */         }
/*     */         
/* 871 */         if (has_been_set)
/*     */         {
/* 873 */           COConfigurationManager.setParameter("Auto Adjust Transfer Defaults", false);
/*     */         }
/*     */       }
/*     */       
/* 877 */       if ((Constants.isOSX) && (check_level < 2))
/*     */       {
/*     */ 
/*     */ 
/* 881 */         COConfigurationManager.setParameter("config.checker.level", 2);
/*     */         
/* 883 */         changed = true;
/*     */         
/* 885 */         if (!COConfigurationManager.getBooleanParameter("Zero New"))
/*     */         {
/* 887 */           COConfigurationManager.setParameter("Enable reorder storage mode", true);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 893 */       if (COConfigurationManager.doesParameterNonDefaultExist("Watch Torrent Folder Interval"))
/*     */       {
/* 895 */         long mins = COConfigurationManager.getIntParameter("Watch Torrent Folder Interval");
/*     */         
/* 897 */         COConfigurationManager.removeParameter("Watch Torrent Folder Interval");
/*     */         
/* 899 */         COConfigurationManager.setParameter("Watch Torrent Folder Interval Secs", 60L * mins);
/*     */         
/* 901 */         changed = true;
/*     */       }
/*     */       
/* 904 */       if (changed) {
/* 905 */         COConfigurationManager.save();
/*     */       }
/*     */       
/* 908 */       setupVerifier();
/*     */     }
/*     */     finally
/*     */     {
/* 912 */       class_mon.exit();
/*     */     }
/*     */     
/* 915 */     ConfigurationDefaults.getInstance().runVerifiers();
/*     */   }
/*     */   
/*     */ 
/*     */   private static void setupVerifier()
/*     */   {
/* 921 */     SimpleTimer.addEvent("ConfigCheck:ver", SystemTime.getOffsetTime(10000L), new TimerEventPerformer()
/*     */     {
/*     */       private TimerEventPeriodic event;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent ev)
/*     */       {
/*     */         
/*     */         
/*     */ 
/*     */ 
/* 934 */         if (this.event == null)
/*     */         {
/* 936 */           long freq = COConfigurationManager.getLongParameter("Config Verify Frequency");
/*     */           
/* 938 */           if (freq > 0L)
/*     */           {
/* 940 */             freq = Math.max(freq, 300000L);
/*     */             
/* 942 */             this.event = SimpleTimer.addPeriodicEvent("ConfigCheck:ver", freq, this);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void runVerifier()
/*     */   {
/*     */     try
/*     */     {
/* 957 */       PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/*     */       
/* 959 */       if (pm.hasCapability(PlatformManagerCapabilities.RunAtLogin))
/*     */       {
/* 961 */         boolean start_on_login = COConfigurationManager.getBooleanParameter("Start On Login");
/*     */         
/* 963 */         if (pm.getRunAtLogin() != start_on_login)
/*     */         {
/* 965 */           pm.setRunAtLogin(start_on_login);
/*     */         }
/*     */       }
/*     */       
/* 969 */       if (pm.hasCapability(PlatformManagerCapabilities.RegisterFileAssociations))
/*     */       {
/* 971 */         boolean auto_reg = COConfigurationManager.getBooleanParameter("Auto Register App");
/*     */         
/* 973 */         if (auto_reg)
/*     */         {
/* 975 */           pm.registerApplication();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 980 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static final boolean isNewInstall()
/*     */   {
/* 987 */     return new_install;
/*     */   }
/*     */   
/*     */ 
/*     */   public static final boolean isNewVersion()
/*     */   {
/* 993 */     return new_version;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/config/impl/ConfigurationChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */