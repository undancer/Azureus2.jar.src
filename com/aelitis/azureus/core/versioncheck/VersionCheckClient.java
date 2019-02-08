/*      */ package com.aelitis.azureus.core.versioncheck;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.clientmessageservice.ClientMessageService;
/*      */ import com.aelitis.azureus.core.clientmessageservice.ClientMessageServiceClient;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASN;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
/*      */ import com.aelitis.azureus.core.security.CryptoManagerFactory;
/*      */ import com.aelitis.azureus.core.util.DNSUtils.DNSUtilsIntf;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*      */ import com.aelitis.net.udp.uc.PRUDPReleasablePacketHandler;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketException;
/*      */ import java.net.URLEncoder;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.stats.transfer.OverallStats;
/*      */ import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AEVerifier;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*      */ 
/*      */ public class VersionCheckClient
/*      */ {
/*   67 */   private static final LogIDs LOGID = LogIDs.CORE;
/*      */   
/*      */   public static final String REASON_UPDATE_CHECK_START = "us";
/*      */   
/*      */   public static final String REASON_UPDATE_CHECK_PERIODIC = "up";
/*      */   
/*      */   public static final String REASON_CHECK_SWT = "sw";
/*      */   
/*      */   public static final String REASON_DHT_FLAGS = "df";
/*      */   
/*      */   public static final String REASON_DHT_EXTENDED_ALLOWED = "dx";
/*      */   
/*      */   public static final String REASON_DHT_ENABLE_ALLOWED = "de";
/*      */   
/*      */   public static final String REASON_EXTERNAL_IP = "ip";
/*      */   
/*      */   public static final String REASON_RECOMMENDED_PLUGINS = "rp";
/*      */   
/*      */   public static final String REASON_SECONDARY_CHECK = "sc";
/*      */   
/*      */   public static final String REASON_PLUGIN_UPDATE = "pu";
/*      */   public static final String REASON_DHT_BOOTSTRAP = "db";
/*      */   private static final String AZ_MSG_SERVER_ADDRESS_V4 = "version.vuze.com";
/*      */   private static final int AZ_MSG_SERVER_PORT = 27001;
/*      */   private static final String MESSAGE_TYPE_ID = "AZVER";
/*      */   public static final String HTTP_SERVER_ADDRESS_V4 = "version.vuze.com";
/*      */   public static final int HTTP_SERVER_PORT = 80;
/*      */   public static final String TCP_SERVER_ADDRESS_V4 = "version.vuze.com";
/*      */   public static final int TCP_SERVER_PORT = 80;
/*      */   public static final String UDP_SERVER_ADDRESS_V4 = "version.vuze.com";
/*      */   public static final int UDP_SERVER_PORT = 2080;
/*      */   public static final String AZ_MSG_SERVER_ADDRESS_V6 = "version6.vuze.com";
/*      */   public static final String HTTP_SERVER_ADDRESS_V6 = "version6.vuze.com";
/*      */   public static final String TCP_SERVER_ADDRESS_V6 = "version6.vuze.com";
/*      */   public static final String UDP_SERVER_ADDRESS_V6 = "version6.vuze.com";
/*      */   private static final long CACHE_PERIOD = 300000L;
/*      */   private static boolean secondary_check_done;
/*  104 */   private final List<VersionCheckClientListener> listeners = new ArrayList(1);
/*  105 */   private boolean startCheckRan = false;
/*      */   private static final int AT_V4 = 1;
/*      */   
/*  108 */   static { VersionCheckClientUDPCodecs.registerCodecs(); }
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int AT_V6 = 2;
/*      */   
/*      */   private static final int AT_EITHER = 3;
/*      */   
/*      */   private static VersionCheckClient instance;
/*      */   
/*      */   private boolean enable_v6;
/*      */   
/*      */   private boolean prefer_v6;
/*      */   
/*      */   public static synchronized VersionCheckClient getSingleton()
/*      */   {
/*  124 */     if (instance == null)
/*      */     {
/*  126 */       instance = new VersionCheckClient();
/*      */     }
/*      */     
/*  129 */     return instance;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  135 */   private Map last_check_data_v4 = null;
/*  136 */   private Map last_check_data_v6 = null;
/*      */   
/*  138 */   private final AEMonitor check_mon = new AEMonitor("versioncheckclient");
/*      */   
/*  140 */   private long last_check_time_v4 = 0L;
/*  141 */   private long last_check_time_v6 = 0L;
/*      */   
/*      */   private long last_feature_flag_cache;
/*      */   
/*      */   private long last_feature_flag_cache_time;
/*      */   
/*      */ 
/*      */   private VersionCheckClient()
/*      */   {
/*  150 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "IPV6 Prefer Addresses", "IPV6 Enable Support" }, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*  158 */         VersionCheckClient.this.enable_v6 = COConfigurationManager.getBooleanParameter("IPV6 Enable Support");
/*  159 */         VersionCheckClient.this.prefer_v6 = COConfigurationManager.getBooleanParameter("IPV6 Prefer Addresses");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public void initialise()
/*      */   {
/*  167 */     DelayedTask delayed_task = UtilitiesImpl.addDelayedTask("VersionCheck", new Runnable()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*      */ 
/*  175 */         final AESemaphore sem = new AESemaphore("VCC:init");
/*      */         
/*  177 */         new AEThread2("VCC:init", true)
/*      */         {
/*      */           public void run()
/*      */           {
/*      */             try
/*      */             {
/*  183 */               VersionCheckClient.this.getVersionCheckInfo("us");
/*      */             }
/*      */             finally
/*      */             {
/*  187 */               sem.release();
/*      */             }
/*      */           }
/*      */         }.start();
/*      */         
/*  192 */         if (!sem.reserve(5000L))
/*      */         {
/*  194 */           Debug.out("Timeout waiting for version check to complete");
/*      */         }
/*      */         
/*      */       }
/*  198 */     });
/*  199 */     delayed_task.queue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map getVersionCheckInfo(String reason)
/*      */   {
/*  216 */     return getVersionCheckInfo(reason, 3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map getVersionCheckInfo(String reason, int address_type)
/*      */   {
/*  224 */     if (address_type == 1)
/*      */     {
/*  226 */       return getVersionCheckInfoSupport(reason, false, false, false);
/*      */     }
/*  228 */     if (address_type == 2)
/*      */     {
/*  230 */       return getVersionCheckInfoSupport(reason, false, false, true);
/*      */     }
/*      */     
/*      */ 
/*  234 */     Map reply = getVersionCheckInfoSupport(reason, false, false, this.prefer_v6);
/*      */     
/*  236 */     if ((reply == null) || (reply.size() == 0))
/*      */     {
/*  238 */       reply = getVersionCheckInfoSupport(reason, false, false, !this.prefer_v6);
/*      */     }
/*      */     
/*  241 */     return reply;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Map getVersionCheckInfoSupport(String reason, boolean only_if_cached, boolean force, boolean v6)
/*      */   {
/*      */     VersionCheckClientListener l;
/*      */     
/*      */     try
/*      */     {
/*      */       Iterator i$;
/*      */       
/*  254 */       synchronized (this.listeners) {
/*  255 */         if ("us".equals(reason)) {
/*  256 */           this.startCheckRan = true;
/*      */         }
/*  258 */         for (i$ = this.listeners.iterator(); i$.hasNext();) { l = (VersionCheckClientListener)i$.next();
/*  259 */           l.versionCheckStarted(reason);
/*      */         }
/*      */       }
/*      */     } catch (Throwable t) {
/*  263 */       Debug.out(t);
/*      */     }
/*      */     
/*  266 */     if (v6)
/*      */     {
/*  268 */       if (this.enable_v6) {
/*      */         try {
/*  270 */           this.check_mon.enter();
/*      */           
/*  272 */           long time_diff = SystemTime.getCurrentTime() - this.last_check_time_v6;
/*      */           
/*  274 */           force = (force) || (time_diff > 300000L) || (time_diff < 0L);
/*      */           
/*  276 */           if ((this.last_check_data_v6 == null) || (this.last_check_data_v6.size() == 0) || (force))
/*      */           {
/*      */ 
/*  279 */             if ((only_if_cached) && (this.last_check_data_v6 != null)) {
/*  280 */               return new HashMap();
/*      */             }
/*      */             try {
/*  283 */               this.last_check_data_v6 = performVersionCheck(constructVersionCheckMessage(reason), true, true, true);
/*      */               
/*  285 */               if ((this.last_check_data_v6 != null) && (this.last_check_data_v6.size() > 0))
/*      */               {
/*  287 */                 COConfigurationManager.setParameter("versioncheck.cache.v6", this.last_check_data_v6);
/*      */ 
/*      */ 
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */             }
/*      */             catch (SocketException t) {}catch (UnknownHostException t) {}catch (Throwable t)
/*      */             {
/*      */ 
/*      */ 
/*  299 */               Debug.out(t);
/*  300 */               this.last_check_data_v6 = new HashMap();
/*      */             }
/*      */           }
/*      */           else {
/*  304 */             Logger.log(new LogEvent(LOGID, "VersionCheckClient is using cached version check info. Using " + this.last_check_data_v6.size() + " reply keys."));
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  309 */           this.check_mon.exit();
/*      */         }
/*      */       }
/*  312 */       if (this.last_check_data_v6 == null) { this.last_check_data_v6 = new HashMap();
/*      */       }
/*  314 */       return this.last_check_data_v6;
/*      */     }
/*      */     try
/*      */     {
/*  318 */       this.check_mon.enter();
/*      */       
/*  320 */       long time_diff = SystemTime.getCurrentTime() - this.last_check_time_v4;
/*      */       
/*  322 */       force = (force) || (time_diff > 300000L) || (time_diff < 0L);
/*      */       
/*  324 */       if ((this.last_check_data_v4 == null) || (this.last_check_data_v4.size() == 0) || (force))
/*      */       {
/*      */ 
/*  327 */         if ((only_if_cached) && (this.last_check_data_v4 != null)) {
/*  328 */           return new HashMap();
/*      */         }
/*      */         try {
/*  331 */           this.last_check_data_v4 = performVersionCheck(constructVersionCheckMessage(reason), true, true, false);
/*      */           
/*  333 */           if ((this.last_check_data_v4 != null) && (this.last_check_data_v4.size() > 0))
/*      */           {
/*  335 */             COConfigurationManager.setParameter("versioncheck.cache.v4", this.last_check_data_v4);
/*      */           }
/*      */           
/*      */ 
/*      */           try
/*      */           {
/*  341 */             if ((AzureusCoreFactory.isCoreAvailable()) && (AzureusCoreFactory.getSingleton().getPluginManager().isInitialized()))
/*      */             {
/*      */ 
/*  344 */               PluginInterface[] plugins = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaces();
/*      */               
/*  346 */               for (int i = 0; i < plugins.length; i++)
/*      */               {
/*  348 */                 PluginInterface plugin = plugins[i];
/*      */                 
/*  350 */                 Map data = plugin.getPluginconfig().getPluginMapParameter("plugin.versionserver.data", null);
/*      */                 
/*  352 */                 if (data != null)
/*      */                 {
/*  354 */                   plugin.getPluginconfig().setPluginMapParameter("plugin.versionserver.data", new HashMap());
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         catch (UnknownHostException t)
/*      */         {
/*  363 */           Debug.outNoStack("VersionCheckClient - " + t.getClass().getName() + ": " + t.getMessage());
/*      */         }
/*      */         catch (IOException t)
/*      */         {
/*  367 */           Debug.outNoStack("VersionCheckClient - " + t.getClass().getName() + ": " + t.getMessage());
/*      */         }
/*      */         catch (Throwable t) {
/*  370 */           Debug.out(t);
/*  371 */           this.last_check_data_v4 = new HashMap();
/*      */         }
/*      */         
/*      */       }
/*  375 */       else if (Logger.isEnabled()) {
/*  376 */         Logger.log(new LogEvent(LOGID, "VersionCheckClient is using cached version check info. Using " + this.last_check_data_v4.size() + " reply keys."));
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  381 */       this.check_mon.exit();
/*      */     }
/*  383 */     if (this.last_check_data_v4 == null) { this.last_check_data_v4 = new HashMap();
/*      */     }
/*  385 */     this.last_feature_flag_cache_time = 0L;
/*      */     
/*  387 */     return this.last_check_data_v4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map getMostRecentVersionCheckData()
/*      */   {
/*  396 */     if (this.last_check_data_v4 != null)
/*      */     {
/*  398 */       return this.last_check_data_v4;
/*      */     }
/*      */     
/*  401 */     Map res = COConfigurationManager.getMapParameter("versioncheck.cache.v4", null);
/*      */     
/*  403 */     if (res != null)
/*      */     {
/*  405 */       return res;
/*      */     }
/*      */     
/*  408 */     if (this.last_check_data_v6 != null)
/*      */     {
/*  410 */       return this.last_check_data_v6;
/*      */     }
/*      */     
/*  413 */     res = COConfigurationManager.getMapParameter("versioncheck.cache.v6", null);
/*      */     
/*  415 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean isVersionCheckDataValid(int address_type)
/*      */   {
/*  422 */     boolean v6_ok = (this.last_check_data_v6 != null) && (this.last_check_data_v6.size() > 0);
/*  423 */     boolean v4_ok = (this.last_check_data_v4 != null) && (this.last_check_data_v4.size() > 0);
/*      */     
/*  425 */     if (address_type == 1)
/*      */     {
/*  427 */       return v4_ok;
/*      */     }
/*  429 */     if (address_type == 2)
/*      */     {
/*  431 */       return v6_ok;
/*      */     }
/*      */     
/*      */ 
/*  435 */     return v4_ok | v6_ok;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getCacheTime(boolean v6)
/*      */   {
/*  443 */     return v6 ? this.last_check_time_v6 : this.last_check_time_v4;
/*      */   }
/*      */   
/*      */ 
/*      */   public void clearCache()
/*      */   {
/*  449 */     this.last_check_time_v6 = 0L;
/*  450 */     this.last_check_time_v4 = 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getFeatureFlags()
/*      */   {
/*  456 */     long now = SystemTime.getCurrentTime();
/*      */     
/*  458 */     if ((now > this.last_feature_flag_cache_time) && (now - this.last_feature_flag_cache_time < 60000L))
/*      */     {
/*  460 */       return this.last_feature_flag_cache;
/*      */     }
/*      */     
/*  463 */     Map m = getMostRecentVersionCheckData();
/*      */     
/*      */     long result;
/*      */     long result;
/*  467 */     if (m == null)
/*      */     {
/*  469 */       result = 0L;
/*      */     }
/*      */     else
/*      */     {
/*  473 */       byte[] b_feat_flags = (byte[])m.get("feat_flags");
/*      */       
/*  475 */       if (b_feat_flags != null)
/*      */       {
/*      */         try
/*      */         {
/*  479 */           result = Long.parseLong(new String((byte[])b_feat_flags));
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  483 */           long result = 0L;
/*      */         }
/*      */         
/*      */       } else {
/*  487 */         result = 0L;
/*      */       }
/*      */     }
/*      */     
/*  491 */     this.last_feature_flag_cache = result;
/*  492 */     this.last_feature_flag_cache_time = now;
/*      */     
/*  494 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public Set<String> getDisabledPluginIDs()
/*      */   {
/*  500 */     Set<String> result = new HashSet();
/*      */     
/*  502 */     Map m = getMostRecentVersionCheckData();
/*      */     
/*  504 */     if (m != null)
/*      */     {
/*  506 */       byte[] x = (byte[])m.get("disabled_pids");
/*      */       
/*  508 */       if (x != null)
/*      */       {
/*  510 */         String str = new String(x);
/*      */         
/*  512 */         String latest = COConfigurationManager.getStringParameter("vc.disabled_pids.latest", "");
/*      */         
/*  514 */         if (!str.equals(latest))
/*      */         {
/*  516 */           byte[] sig = (byte[])m.get("disabled_pids_sig");
/*      */           
/*  518 */           if (sig == null)
/*      */           {
/*  520 */             Debug.out("disabled plugins sig missing");
/*      */             
/*  522 */             return result;
/*      */           }
/*      */           try
/*      */           {
/*  526 */             AEVerifier.verifyData(str, sig);
/*      */             
/*  528 */             COConfigurationManager.setParameter("vc.disabled_pids.latest", str);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  532 */             return result;
/*      */           }
/*      */         }
/*      */         
/*  536 */         String[] bits = str.split(",");
/*      */         
/*  538 */         for (String b : bits)
/*      */         {
/*  540 */           b = b.trim();
/*      */           
/*  542 */           if (b.length() > 0)
/*      */           {
/*  544 */             result.add(b);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  550 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public Set<String> getAutoInstallPluginIDs()
/*      */   {
/*  556 */     Set<String> result = new HashSet();
/*      */     
/*  558 */     Map m = getMostRecentVersionCheckData();
/*      */     
/*  560 */     if (m != null)
/*      */     {
/*  562 */       byte[] x = (byte[])m.get("autoinstall_pids");
/*      */       
/*  564 */       if (x != null)
/*      */       {
/*  566 */         String str = new String(x);
/*      */         
/*  568 */         String latest = COConfigurationManager.getStringParameter("vc.autoinstall_pids.latest", "");
/*      */         
/*  570 */         if (!str.equals(latest))
/*      */         {
/*  572 */           byte[] sig = (byte[])m.get("autoinstall_pids_sig");
/*      */           
/*  574 */           if (sig == null)
/*      */           {
/*  576 */             Debug.out("autoinstall plugins sig missing");
/*      */             
/*  578 */             return result;
/*      */           }
/*      */           try
/*      */           {
/*  582 */             AEVerifier.verifyData(str, sig);
/*      */             
/*  584 */             COConfigurationManager.setParameter("vc.autoinstall_pids.latest", str);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  588 */             return result;
/*      */           }
/*      */         }
/*      */         
/*  592 */         String[] bits = str.split(",");
/*      */         
/*  594 */         for (String b : bits)
/*      */         {
/*  596 */           b = b.trim();
/*      */           
/*  598 */           if (b.length() > 0)
/*      */           {
/*  600 */             result.add(b);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  606 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getExternalIpAddress(boolean only_if_cached, boolean v6)
/*      */   {
/*  620 */     return getExternalIpAddress(only_if_cached, v6, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getExternalIpAddress(boolean only_if_cached, boolean v6, boolean force)
/*      */   {
/*  629 */     Map reply = getVersionCheckInfoSupport("ip", only_if_cached, force, v6);
/*      */     
/*  631 */     byte[] address = (byte[])reply.get("source_ip_address");
/*  632 */     if (address != null) {
/*  633 */       return new String(address);
/*      */     }
/*      */     
/*  636 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean DHTEnableAllowed()
/*      */   {
/*  645 */     Map reply = getVersionCheckInfo("de", 3);
/*      */     
/*  647 */     boolean res = false;
/*      */     
/*  649 */     byte[] value = (byte[])reply.get("enable_dht");
/*      */     
/*  651 */     if (value != null)
/*      */     {
/*  653 */       res = new String(value).equalsIgnoreCase("true");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  659 */     if (!res) {
/*  660 */       res = !isVersionCheckDataValid(3);
/*      */     }
/*      */     
/*  663 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean DHTExtendedUseAllowed()
/*      */   {
/*  674 */     Map reply = getVersionCheckInfo("dx", 3);
/*      */     
/*  676 */     boolean res = false;
/*      */     
/*  678 */     byte[] value = (byte[])reply.get("enable_dht_extended_use");
/*  679 */     if (value != null) {
/*  680 */       res = new String(value).equalsIgnoreCase("true");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  685 */     if (!res) {
/*  686 */       res = !isVersionCheckDataValid(3);
/*      */     }
/*      */     
/*  689 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte getDHTFlags()
/*      */   {
/*  695 */     Map map = getMostRecentVersionCheckData();
/*      */     
/*  697 */     if (map != null)
/*      */     {
/*  699 */       byte[] b_flags = (byte[])map.get("dht_flags");
/*      */       
/*  701 */       if (b_flags != null)
/*      */       {
/*  703 */         return new Integer(new String(b_flags)).byteValue();
/*      */       }
/*      */     }
/*      */     
/*  707 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */   public String[] getRecommendedPlugins()
/*      */   {
/*  713 */     Map reply = getVersionCheckInfo("rp", 3);
/*      */     
/*  715 */     List l = (List)reply.get("recommended_plugins");
/*      */     
/*  717 */     if (l == null)
/*      */     {
/*  719 */       return new String[0];
/*      */     }
/*      */     
/*  722 */     String[] res = new String[l.size()];
/*      */     
/*  724 */     for (int i = 0; i < l.size(); i++)
/*      */     {
/*  726 */       res[i] = new String((byte[])(byte[])l.get(i));
/*      */     }
/*      */     
/*  729 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<InetSocketAddress> getDHTBootstrap(boolean ipv4)
/*      */   {
/*  736 */     List<InetSocketAddress> result = new ArrayList();
/*      */     try
/*      */     {
/*  739 */       Map reply = getVersionCheckInfo("db", ipv4 ? 1 : 2);
/*      */       
/*  741 */       List<Map> l = (List)reply.get("dht_boot");
/*      */       
/*  743 */       if (l != null)
/*      */       {
/*  745 */         for (Map m : l)
/*      */         {
/*  747 */           byte[] address = (byte[])m.get("a");
/*  748 */           int port = ((Long)m.get("p")).intValue();
/*      */           
/*  750 */           if (((ipv4) && (address.length == 4)) || ((!ipv4) && (address.length == 16)))
/*      */           {
/*      */ 
/*  753 */             InetAddress iaddress = InetAddress.getByAddress(address);
/*      */             
/*  755 */             if ((!iaddress.isLoopbackAddress()) && (!iaddress.isLinkLocalAddress()) && (!iaddress.isSiteLocalAddress()))
/*      */             {
/*      */ 
/*      */ 
/*  759 */               result.add(new InetSocketAddress(iaddress, port));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  766 */       Debug.out(e);
/*      */     }
/*      */     
/*  769 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public Map<String, Object> getCountryInfo()
/*      */   {
/*  775 */     Map reply = getVersionCheckInfo("ip", 3);
/*      */     
/*  777 */     Map<String, Object> info = (Map)reply.get("source_info");
/*      */     
/*  779 */     if (info == null)
/*      */     {
/*  781 */       return new HashMap();
/*      */     }
/*      */     
/*      */ 
/*  785 */     return BDecoder.decodeStrings(info);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map performVersionCheck(Map data_to_send, boolean use_az_message, boolean use_http, boolean v6)
/*      */     throws Exception
/*      */   {
/*  804 */     Exception error = null;
/*  805 */     Map reply = null;
/*      */     
/*      */ 
/*  808 */     if (use_http) {
/*      */       try
/*      */       {
/*  811 */         reply = executeHTTP(data_to_send, v6);
/*      */         
/*  813 */         reply.put("protocol_used", "HTTP");
/*      */         
/*  815 */         error = null;
/*      */       }
/*      */       catch (IOException e) {
/*  818 */         error = e;
/*      */       }
/*      */       catch (Exception e) {
/*  821 */         Debug.printStackTrace(e);
/*  822 */         error = e;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  827 */     if ((reply == null) && (use_az_message)) {
/*      */       try
/*      */       {
/*  830 */         reply = executeAZMessage(data_to_send, v6);
/*      */         
/*  832 */         reply.put("protocol_used", "AZMSG");
/*      */         
/*  834 */         error = null;
/*      */       }
/*      */       catch (IOException e) {
/*  837 */         error = e;
/*      */       }
/*      */       catch (Exception e) {
/*  840 */         Debug.printStackTrace(e);
/*  841 */         error = e;
/*      */       }
/*      */     }
/*  844 */     if (error != null)
/*      */     {
/*  846 */       throw error;
/*      */     }
/*      */     
/*  849 */     if (Logger.isEnabled()) {
/*  850 */       Logger.log(new LogEvent(LOGID, "VersionCheckClient server version check successful. Received " + (reply == null ? "null" : Integer.valueOf(reply.size())) + " reply keys."));
/*      */     }
/*      */     
/*      */ 
/*  854 */     if (v6)
/*      */     {
/*  856 */       this.last_check_time_v6 = SystemTime.getCurrentTime();
/*      */     }
/*      */     else
/*      */     {
/*  860 */       this.last_check_time_v4 = SystemTime.getCurrentTime();
/*      */     }
/*      */     
/*  863 */     return reply;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map executeAZMessage(Map data_to_send, boolean v6)
/*      */     throws Exception
/*      */   {
/*  873 */     if (COConfigurationManager.getBooleanParameter("update.anonymous"))
/*      */     {
/*  875 */       throw new Exception("AZ Messaging disabled for anonymous updates");
/*      */     }
/*      */     
/*  878 */     if ((v6) && (!this.enable_v6))
/*      */     {
/*  880 */       throw new Exception("IPv6 is disabled");
/*      */     }
/*      */     
/*  883 */     String host = getHost(v6, "version6.vuze.com", "version.vuze.com");
/*      */     
/*  885 */     if (Logger.isEnabled()) {
/*  886 */       Logger.log(new LogEvent(LOGID, "VersionCheckClient retrieving version information from " + host + ":" + 27001));
/*      */     }
/*      */     
/*  889 */     ClientMessageService msg_service = null;
/*  890 */     Map reply = null;
/*      */     try
/*      */     {
/*  893 */       msg_service = ClientMessageServiceClient.getServerService(host, 27001, 20, "AZVER");
/*      */       
/*  895 */       msg_service.sendMessage(data_to_send);
/*      */       
/*  897 */       reply = msg_service.receiveMessage();
/*      */       
/*  899 */       preProcessReply(reply, v6);
/*      */     }
/*      */     finally
/*      */     {
/*  903 */       if (msg_service != null)
/*      */       {
/*  905 */         msg_service.close();
/*      */       }
/*      */     }
/*      */     
/*  909 */     return reply;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getHTTPGetString(boolean for_proxy, boolean v6)
/*      */   {
/* 1032 */     return getHTTPGetString(new HashMap(), for_proxy, v6);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getHTTPGetString(Map content, boolean for_proxy, boolean v6)
/*      */   {
/* 1041 */     String host = getHost(v6, "version6.vuze.com", "version.vuze.com");
/*      */     
/* 1043 */     String get_str = "GET " + (for_proxy ? "http://" + (v6 ? UrlUtils.convertIPV6Host(host) : host) + ":" + 80 : "") + "/version?";
/*      */     try
/*      */     {
/* 1046 */       get_str = get_str + URLEncoder.encode(new String(BEncoder.encode(content), "ISO-8859-1"), "ISO-8859-1");
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/* 1051 */     get_str = get_str + " HTTP/1.1\r\n\r\n";
/*      */     
/* 1053 */     return get_str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map executeTCP(Map data_to_send, InetAddress bind_ip, int bind_port, boolean v6)
/*      */     throws Exception
/*      */   {
/* 1065 */     if (COConfigurationManager.getBooleanParameter("update.anonymous"))
/*      */     {
/* 1067 */       throw new Exception("TCP disabled for anonymous updates");
/*      */     }
/*      */     
/* 1070 */     if ((v6) && (!this.enable_v6))
/*      */     {
/* 1072 */       throw new Exception("IPv6 is disabled");
/*      */     }
/*      */     
/* 1075 */     String host = getHost(v6, "version6.vuze.com", "version.vuze.com");
/*      */     
/* 1077 */     if (Logger.isEnabled()) {
/* 1078 */       Logger.log(new LogEvent(LOGID, "VersionCheckClient retrieving version information from " + host + ":" + 80 + " via TCP"));
/*      */     }
/*      */     
/* 1081 */     String get_str = getHTTPGetString(data_to_send, false, v6);
/*      */     
/* 1083 */     Socket socket = null;
/*      */     try
/*      */     {
/* 1086 */       socket = new Socket();
/*      */       
/* 1088 */       if (bind_ip != null)
/*      */       {
/* 1090 */         socket.bind(new InetSocketAddress(bind_ip, bind_port));
/*      */       }
/* 1092 */       else if (bind_port != 0)
/*      */       {
/* 1094 */         socket.bind(new InetSocketAddress(bind_port));
/*      */       }
/*      */       
/* 1097 */       socket.setSoTimeout(10000);
/*      */       
/* 1099 */       socket.connect(new InetSocketAddress(host, 80), 10000);
/*      */       
/* 1101 */       OutputStream os = socket.getOutputStream();
/*      */       
/* 1103 */       os.write(get_str.getBytes("ISO-8859-1"));
/*      */       
/* 1105 */       os.flush();
/*      */       
/* 1107 */       InputStream is = socket.getInputStream();
/*      */       
/* 1109 */       byte[] buffer = new byte[1];
/*      */       
/* 1111 */       String header = "";
/*      */       
/* 1113 */       int content_length = -1;
/*      */       
/*      */       for (;;)
/*      */       {
/* 1117 */         int len = is.read(buffer);
/*      */         
/* 1119 */         if (len <= 0) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 1124 */         header = header + (char)buffer[0];
/*      */         
/* 1126 */         if (header.endsWith("\r\n\r\n"))
/*      */         {
/* 1128 */           header = header.toLowerCase(MessageText.LOCALE_ENGLISH);
/*      */           
/* 1130 */           int pos = header.indexOf("content-length:");
/*      */           
/* 1132 */           if (pos == -1)
/*      */           {
/* 1134 */             throw new IOException("content length missing");
/*      */           }
/*      */           
/* 1137 */           header = header.substring(pos + 15);
/*      */           
/* 1139 */           pos = header.indexOf('\r');
/*      */           
/* 1141 */           header = header.substring(0, pos).trim();
/*      */           
/* 1143 */           content_length = Integer.parseInt(header);
/*      */           
/* 1145 */           if (content_length <= 10000)
/*      */             break;
/* 1147 */           throw new IOException("content length too large");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1153 */         if (header.length() > 2048)
/*      */         {
/* 1155 */           throw new IOException("header too large");
/*      */         }
/*      */       }
/*      */       
/* 1159 */       ByteArrayOutputStream baos = new ByteArrayOutputStream(content_length);
/*      */       
/* 1161 */       buffer = new byte[content_length];
/*      */       
/* 1163 */       while (content_length > 0)
/*      */       {
/* 1165 */         int len = is.read(buffer);
/*      */         
/* 1167 */         if (len <= 0) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 1172 */         baos.write(buffer, 0, len);
/*      */         
/* 1174 */         content_length -= len;
/*      */       }
/*      */       
/* 1177 */       if (content_length != 0)
/*      */       {
/* 1179 */         throw new IOException("error reading reply");
/*      */       }
/*      */       
/* 1182 */       byte[] reply_bytes = baos.toByteArray();
/*      */       
/* 1184 */       Map reply = BDecoder.decode(new BufferedInputStream(new java.io.ByteArrayInputStream(reply_bytes)));
/*      */       
/* 1186 */       preProcessReply(reply, v6);
/*      */       
/* 1188 */       return reply;
/*      */     }
/*      */     finally
/*      */     {
/* 1192 */       if (socket != null) {
/*      */         try
/*      */         {
/* 1195 */           socket.close();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map executeUDP(Map data_to_send, InetAddress bind_ip, int bind_port, boolean v6)
/*      */     throws Exception
/*      */   {
/* 1213 */     if (COConfigurationManager.getBooleanParameter("update.anonymous"))
/*      */     {
/* 1215 */       throw new Exception("UDP disabled for anonymous updates");
/*      */     }
/*      */     
/* 1218 */     if ((v6) && (!this.enable_v6))
/*      */     {
/* 1220 */       throw new Exception("IPv6 is disabled");
/*      */     }
/*      */     
/* 1223 */     String host = getHost(v6, "version6.vuze.com", "version.vuze.com");
/*      */     
/* 1225 */     PRUDPReleasablePacketHandler handler = com.aelitis.net.udp.uc.PRUDPPacketHandlerFactory.getReleasableHandler(bind_port);
/*      */     
/* 1227 */     PRUDPPacketHandler packet_handler = handler.getHandler();
/*      */     
/* 1229 */     long timeout = 5L;
/*      */     
/* 1231 */     Random random = new Random();
/*      */     try
/*      */     {
/* 1234 */       Exception last_error = null;
/*      */       
/* 1236 */       packet_handler.setExplicitBindAddress(bind_ip);
/*      */       
/* 1238 */       for (int i = 0; i < 3; i++)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/* 1244 */           long connection_id = 0x8000000000000000 | random.nextLong();
/*      */           
/* 1246 */           VersionCheckClientUDPRequest request_packet = new VersionCheckClientUDPRequest(connection_id);
/*      */           
/* 1248 */           request_packet.setPayload(data_to_send);
/*      */           
/* 1250 */           VersionCheckClientUDPReply reply_packet = (VersionCheckClientUDPReply)packet_handler.sendAndReceive(null, request_packet, new InetSocketAddress(host, 2080), timeout);
/*      */           
/* 1252 */           Map reply = reply_packet.getPayload();
/*      */           
/* 1254 */           preProcessReply(reply, v6);
/*      */           
/* 1256 */           return reply;
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/* 1260 */           last_error = e;
/*      */           
/* 1262 */           timeout *= 2L;
/*      */         }
/*      */       }
/*      */       
/* 1266 */       if (last_error != null)
/*      */       {
/* 1268 */         throw last_error;
/*      */       }
/*      */       
/* 1271 */       throw new Exception("Timeout");
/*      */     }
/*      */     finally
/*      */     {
/* 1275 */       packet_handler.setExplicitBindAddress(null);
/*      */       
/* 1277 */       handler.release();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void preProcessReply(Map reply, final boolean v6)
/*      */   {
/* 1286 */     NetworkAdmin admin = NetworkAdmin.getSingleton();
/*      */     try
/*      */     {
/* 1289 */       byte[] address = (byte[])reply.get("source_ip_address");
/*      */       
/* 1291 */       if (address != null)
/*      */       {
/* 1293 */         InetAddress my_ip = InetAddress.getByName(new String(address));
/*      */         
/* 1295 */         NetworkAdminASN old_asn = admin.getCurrentASN();
/*      */         
/* 1297 */         NetworkAdminASN new_asn = admin.lookupCurrentASN(my_ip);
/*      */         
/* 1299 */         if (!new_asn.sameAs(old_asn))
/*      */         {
/*      */ 
/*      */ 
/* 1303 */           if (!secondary_check_done)
/*      */           {
/* 1305 */             secondary_check_done = true;
/*      */             
/* 1307 */             new AEThread("Secondary version check", true)
/*      */             {
/*      */ 
/*      */               public void runSupport()
/*      */               {
/* 1312 */                 VersionCheckClient.this.getVersionCheckInfoSupport("sc", false, true, v6);
/*      */               }
/*      */             }.start();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1320 */       if (!Debug.containsException(e, UnknownHostException.class))
/*      */       {
/* 1322 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1326 */     Long as_advice = (Long)reply.get("as_advice");
/*      */     
/* 1328 */     if (as_advice != null)
/*      */     {
/* 1330 */       NetworkAdminASN current_asn = admin.getCurrentASN();
/*      */       
/* 1332 */       String asn = current_asn.getASName();
/*      */       
/* 1334 */       if (asn != null)
/*      */       {
/* 1336 */         long advice = as_advice.longValue();
/*      */         
/* 1338 */         if (advice != 0L)
/*      */         {
/*      */ 
/*      */ 
/* 1342 */           String done_asn = COConfigurationManager.getStringParameter("ASN Advice Followed", "");
/*      */           
/* 1344 */           if (!done_asn.equals(asn))
/*      */           {
/* 1346 */             COConfigurationManager.setParameter("ASN Advice Followed", asn);
/*      */             
/* 1348 */             boolean change = (advice == 1L) || (advice == 2L);
/* 1349 */             boolean alert = (advice == 1L) || (advice == 3L);
/*      */             
/* 1351 */             if (!COConfigurationManager.getBooleanParameter("network.transport.encrypted.require"))
/*      */             {
/* 1353 */               if (change)
/*      */               {
/* 1355 */                 COConfigurationManager.setParameter("network.transport.encrypted.require", true);
/*      */               }
/*      */               
/* 1358 */               if (alert)
/*      */               {
/* 1360 */                 String msg = MessageText.getString("crypto.alert.as.warning", new String[] { asn });
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/* 1365 */                 Logger.log(new org.gudy.azureus2.core3.logging.LogAlert(false, 1, msg));
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1378 */     Long lEnabledUISwitcher = (Long)reply.get("ui.toolbar.uiswitcher");
/* 1379 */     if (lEnabledUISwitcher != null) {
/* 1380 */       COConfigurationManager.setBooleanDefault("ui.toolbar.uiswitcher", lEnabledUISwitcher.longValue() == 1L);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public InetAddress getExternalIpAddressHTTP(boolean v6)
/*      */     throws Exception
/*      */   {
/* 1391 */     Map reply = executeHTTP(new HashMap(), v6);
/*      */     
/* 1393 */     byte[] address = (byte[])reply.get("source_ip_address");
/*      */     
/* 1395 */     return address == null ? null : InetAddress.getByName(new String(address));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public InetAddress getExternalIpAddressTCP(InetAddress bind_ip, int bind_port, boolean v6)
/*      */     throws Exception
/*      */   {
/* 1406 */     Map reply = executeTCP(new HashMap(), bind_ip, bind_port, v6);
/*      */     
/* 1408 */     byte[] address = (byte[])reply.get("source_ip_address");
/*      */     
/* 1410 */     return address == null ? null : InetAddress.getByName(new String(address));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public InetAddress getExternalIpAddressUDP(InetAddress bind_ip, int bind_port, boolean v6)
/*      */     throws Exception
/*      */   {
/* 1421 */     Map reply = executeUDP(new HashMap(), bind_ip, bind_port, v6);
/*      */     
/* 1423 */     byte[] address = (byte[])reply.get("source_ip_address");
/*      */     
/* 1425 */     return address == null ? null : InetAddress.getByName(new String(address));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getHost(boolean v6, String v6_address, String v4_address)
/*      */   {
/* 1434 */     if (v6)
/*      */     {
/* 1436 */       v6_address = getTestAddress(true, v6_address);
/*      */       try
/*      */       {
/* 1439 */         return InetAddress.getByName(v6_address).getHostAddress();
/*      */       }
/*      */       catch (UnknownHostException e)
/*      */       {
/* 1443 */         DNSUtils.DNSUtilsIntf dns_utils = com.aelitis.azureus.core.util.DNSUtils.getSingleton();
/*      */         
/* 1445 */         if (dns_utils != null) {
/*      */           try
/*      */           {
/* 1448 */             return dns_utils.getIPV6ByName(v6_address).getHostAddress();
/*      */           }
/*      */           catch (UnknownHostException f) {}
/*      */         }
/*      */         
/*      */ 
/* 1454 */         return v6_address;
/*      */       }
/*      */     }
/*      */     
/* 1458 */     v4_address = getTestAddress(false, v4_address);
/*      */     
/* 1460 */     return v4_address;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getTestAddress(boolean v6, String address)
/*      */   {
/* 1469 */     return COConfigurationManager.getStringParameter("versioncheck.test.address." + v6, address);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map<String, Object> constructVersionCheckMessage(String reason)
/*      */   {
/* 1483 */     boolean send_info = COConfigurationManager.getBooleanParameter("Send Version Info");
/*      */     
/* 1485 */     Map<String, Object> message = new HashMap();
/*      */     
/*      */ 
/* 1488 */     message.put("appid", SystemProperties.getApplicationIdentifier());
/* 1489 */     message.put("appname", SystemProperties.getApplicationName());
/* 1490 */     message.put("version", "5.7.6.0");
/* 1491 */     message.put("first_version", COConfigurationManager.getStringParameter("First Recorded Version", ""));
/*      */     
/* 1493 */     String sub_ver = "";
/*      */     
/* 1495 */     if (sub_ver.length() > 0) {
/* 1496 */       message.put("subver", sub_ver);
/*      */     }
/*      */     
/* 1499 */     if (COConfigurationManager.getBooleanParameter("Beta Programme Enabled"))
/*      */     {
/* 1501 */       message.put("beta_prog", "true");
/*      */     }
/*      */     
/* 1504 */     message.put("ui", COConfigurationManager.getStringParameter("ui", "unknown"));
/* 1505 */     message.put("os", Constants.OSName);
/* 1506 */     message.put("os_version", System.getProperty("os.version"));
/* 1507 */     message.put("os_arch", System.getProperty("os.arch"));
/* 1508 */     message.put("os_arch_dm", System.getProperty("sun.arch.data.model"));
/*      */     
/* 1510 */     boolean using_phe = COConfigurationManager.getBooleanParameter("network.transport.encrypted.require");
/* 1511 */     message.put("using_phe", using_phe ? new Long(1L) : new Long(0L));
/*      */     
/* 1513 */     message.put("imode", COConfigurationManager.getStringParameter("installer.mode", ""));
/*      */     
/*      */     try
/*      */     {
/* 1517 */       Class c = Class.forName("org.eclipse.swt.SWT");
/*      */       
/* 1519 */       String swt_platform = (String)c.getMethod("getPlatform", new Class[0]).invoke(null, new Object[0]);
/* 1520 */       message.put("swt_platform", swt_platform);
/*      */       
/* 1522 */       Integer swt_version = (Integer)c.getMethod("getVersion", new Class[0]).invoke(null, new Object[0]);
/* 1523 */       message.put("swt_version", new Long(swt_version.longValue()));
/*      */ 
/*      */     }
/*      */     catch (ClassNotFoundException e) {}catch (NoClassDefFoundError er) {}catch (InvocationTargetException err) {}catch (Throwable t)
/*      */     {
/* 1528 */       t.printStackTrace();
/*      */     }
/*      */     
/* 1531 */     int last_send_time = COConfigurationManager.getIntParameter("Send Version Info Last Time", -1);
/* 1532 */     int current_send_time = (int)(SystemTime.getCurrentTime() / 1000L);
/* 1533 */     COConfigurationManager.setParameter("Send Version Info Last Time", current_send_time);
/*      */     
/*      */ 
/* 1536 */     String id = COConfigurationManager.getStringParameter("ID", null);
/*      */     
/* 1538 */     if ((id != null) && (send_info)) {
/* 1539 */       message.put("id", id);
/*      */       try
/*      */       {
/* 1542 */         byte[] id2 = CryptoManagerFactory.getSingleton().getSecureID();
/*      */         
/* 1544 */         message.put("id2", id2);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/* 1549 */       if ((last_send_time != -1) && (last_send_time < current_send_time))
/*      */       {
/* 1551 */         message.put("tsl", new Long(current_send_time - last_send_time));
/*      */       }
/*      */       
/* 1554 */       message.put("reason", reason);
/*      */       
/* 1556 */       String java_version = Constants.JAVA_VERSION;
/* 1557 */       if (java_version == null) java_version = "unknown";
/* 1558 */       message.put("java", java_version);
/*      */       
/*      */ 
/* 1561 */       String java_vendor = System.getProperty("java.vm.vendor");
/* 1562 */       if (java_vendor == null) java_vendor = "unknown";
/* 1563 */       message.put("javavendor", java_vendor);
/*      */       
/* 1565 */       int api_level = Constants.API_LEVEL;
/* 1566 */       if (api_level > 0) {
/* 1567 */         message.put("api_level", Integer.valueOf(api_level));
/*      */       }
/*      */       
/* 1570 */       long max_mem = Runtime.getRuntime().maxMemory() / 1048576L;
/* 1571 */       message.put("javamx", new Long(max_mem));
/*      */       
/* 1573 */       String java_rt_name = System.getProperty("java.runtime.name");
/* 1574 */       if (java_rt_name != null) {
/* 1575 */         message.put("java_rt_name", java_rt_name);
/*      */       }
/*      */       
/* 1578 */       String java_rt_version = System.getProperty("java.runtime.version");
/* 1579 */       if (java_rt_version != null) {
/* 1580 */         message.put("java_rt_version", java_rt_version);
/*      */       }
/*      */       
/* 1583 */       OverallStats stats = StatsFactory.getStats();
/*      */       
/* 1585 */       if (stats != null)
/*      */       {
/*      */ 
/*      */ 
/* 1589 */         long total_uptime = stats.getTotalUpTime();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1594 */         message.put("total_uptime", new Long(total_uptime));
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 1599 */         int port = UDPNetworkManager.getSingleton().getUDPNonDataListeningPortNumber();
/*      */         
/* 1601 */         message.put("dht", Integer.valueOf(port));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1605 */         Debug.out(e);
/*      */       }
/*      */       try
/*      */       {
/* 1609 */         NetworkAdminASN current_asn = NetworkAdmin.getSingleton().getCurrentASN();
/*      */         
/* 1611 */         message.put("ip_as", current_asn.getAS());
/*      */         
/* 1613 */         String asn = current_asn.getASName();
/*      */         
/* 1615 */         if (asn.length() > 64)
/*      */         {
/* 1617 */           asn = asn.substring(0, 64);
/*      */         }
/*      */         
/* 1620 */         message.put("ip_asn", asn);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1624 */         Debug.out(e);
/*      */       }
/*      */       
/*      */ 
/* 1628 */       message.put("locale", Locale.getDefault().toString());
/* 1629 */       String originalLocale = System.getProperty("user.language") + "_" + System.getProperty("user.country");
/*      */       
/* 1631 */       String variant = System.getProperty("user.variant");
/* 1632 */       if ((variant != null) && (variant.length() > 0)) {
/* 1633 */         originalLocale = originalLocale + "_" + variant;
/*      */       }
/* 1635 */       message.put("orig_locale", originalLocale);
/*      */       
/*      */ 
/* 1638 */       message.put("user_mode", Integer.valueOf(COConfigurationManager.getIntParameter("User Mode", -1)));
/*      */       
/*      */ 
/* 1641 */       Set<String> features = UtilitiesImpl.getFeaturesInstalled();
/*      */       
/* 1643 */       if (features.size() > 0)
/*      */       {
/* 1645 */         String str = "";
/*      */         
/* 1647 */         for (String f : features) {
/* 1648 */           str = str + (str.length() == 0 ? "" : ",") + f;
/*      */         }
/*      */         
/* 1651 */         message.put("vzfeatures", str);
/*      */       }
/*      */       try
/*      */       {
/* 1655 */         if ((AzureusCoreFactory.isCoreAvailable()) && (AzureusCoreFactory.getSingleton().getPluginManager().isInitialized()))
/*      */         {
/*      */ 
/*      */ 
/* 1659 */           PluginInterface[] plugins = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaces();
/*      */           
/* 1661 */           List pids = new ArrayList();
/*      */           
/* 1663 */           List vs_data = new ArrayList();
/*      */           
/* 1665 */           for (int i = 0; i < plugins.length; i++)
/*      */           {
/* 1667 */             PluginInterface plugin = plugins[i];
/*      */             
/* 1669 */             String pid = plugin.getPluginID();
/*      */             
/* 1671 */             String info = plugin.getPluginconfig().getPluginStringParameter("plugin.info");
/*      */             
/*      */ 
/* 1674 */             if (((info != null) && (info.length() > 0)) || ((!pid.startsWith("<")) && (!pid.startsWith("azbp")) && (!pid.startsWith("azupdater")) && (!pid.startsWith("azplatform")) && (!pids.contains(pid))))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1681 */               if ((info != null) && (info.length() > 0))
/*      */               {
/* 1683 */                 if (info.length() < 256)
/*      */                 {
/* 1685 */                   pid = pid + ":" + info;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1689 */                   Debug.out("Plugin '" + pid + "' reported excessive info string '" + info + "'");
/*      */                 }
/*      */               }
/*      */               
/* 1693 */               pids.add(pid);
/*      */             }
/*      */             
/* 1696 */             Map data = plugin.getPluginconfig().getPluginMapParameter("plugin.versionserver.data", null);
/*      */             
/* 1698 */             if (data != null)
/*      */             {
/* 1700 */               Map payload = new HashMap();
/*      */               
/* 1702 */               byte[] data_bytes = BEncoder.encode(data);
/*      */               
/* 1704 */               if (data_bytes.length > 16384)
/*      */               {
/* 1706 */                 Debug.out("Plugin '" + pid + "' reported excessive version server data (length=" + data_bytes.length + ")");
/*      */                 
/* 1708 */                 payload.put("error", "data too long: " + data_bytes.length);
/*      */               }
/*      */               else
/*      */               {
/* 1712 */                 payload.put("data", data_bytes);
/*      */               }
/*      */               
/* 1715 */               payload.put("id", pid);
/* 1716 */               payload.put("version", plugin.getPluginVersion());
/*      */               
/* 1718 */               vs_data.add(payload);
/*      */             }
/*      */           }
/* 1721 */           message.put("plugins", pids);
/*      */           
/* 1723 */           if (vs_data.size() > 0)
/*      */           {
/* 1725 */             message.put("plugin_data", vs_data);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1730 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1735 */     return message;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addVersionCheckClientListener(boolean triggerStartListener, VersionCheckClientListener l)
/*      */   {
/* 1743 */     synchronized (this.listeners) {
/* 1744 */       this.listeners.add(l);
/*      */       
/* 1746 */       if ((triggerStartListener) && (this.startCheckRan)) {
/*      */         try {
/* 1748 */           l.versionCheckStarted("us");
/*      */         } catch (Exception e) {
/* 1750 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeVersionCheckClientListener(VersionCheckClientListener l)
/*      */   {
/* 1760 */     synchronized (this.listeners) {
/* 1761 */       this.listeners.remove(l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*      */     try
/*      */     {
/* 1771 */       COConfigurationManager.initialise();
/*      */       
/* 1773 */       COConfigurationManager.setParameter("IPV6 Enable Support", true);
/*      */       
/* 1775 */       boolean v6 = true;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1781 */       System.out.println("HTTP: " + getSingleton().getExternalIpAddressHTTP(v6));
/*      */       
/*      */ 
/* 1784 */       Map data = constructVersionCheckMessage("us");
/* 1785 */       System.out.println("Sending (pre-initialisation):");
/* 1786 */       printDataMap(data);
/* 1787 */       System.out.println("-----------");
/*      */       
/* 1789 */       System.out.println("Receiving (pre-initialisation):");
/* 1790 */       printDataMap(getSingleton().getVersionCheckInfo("us"));
/* 1791 */       System.out.println("-----------");
/*      */       
/* 1793 */       System.out.println();
/* 1794 */       System.out.print("Initialising core... ");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1801 */       com.aelitis.azureus.core.impl.AzureusCoreImpl.SUPPRESS_CLASSLOADER_ERRORS = true;
/* 1802 */       org.gudy.azureus2.core3.download.impl.DownloadManagerStateImpl.SUPPRESS_FIXUP_ERRORS = true;
/*      */       
/* 1804 */       AzureusCore core = AzureusCoreFactory.create();
/* 1805 */       core.start();
/* 1806 */       System.out.println("done.");
/* 1807 */       System.out.println();
/* 1808 */       System.out.println("-----------");
/*      */       
/* 1810 */       data = constructVersionCheckMessage("us");
/* 1811 */       System.out.println("Sending (post-initialisation):");
/* 1812 */       printDataMap(data);
/* 1813 */       System.out.println("-----------");
/*      */       
/* 1815 */       System.out.println("Receiving (post-initialisation):");
/* 1816 */       printDataMap(getSingleton().getVersionCheckInfo("us"));
/* 1817 */       System.out.println("-----------");
/* 1818 */       System.out.println();
/*      */       
/* 1820 */       System.out.print("Shutting down core... ");
/* 1821 */       core.stop();
/* 1822 */       System.out.println("done.");
/*      */     }
/*      */     catch (Throwable e) {
/* 1825 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void printDataMap(Map map)
/*      */     throws Exception
/*      */   {
/* 1834 */     TreeMap res = new TreeMap(map);
/*      */     
/* 1836 */     Iterator key_itr = map.keySet().iterator();
/*      */     
/* 1838 */     while (key_itr.hasNext()) {
/* 1839 */       Object key = key_itr.next();
/* 1840 */       Object val = map.get(key);
/* 1841 */       if ((val instanceof byte[])) {
/* 1842 */         String as_bytes = ByteFormatter.nicePrint((byte[])val);
/* 1843 */         String as_text = new String((byte[])val, "ISO-8859-1");
/* 1844 */         res.put(key, as_text + " [" + as_bytes + "]");
/*      */       }
/*      */     }
/*      */     
/* 1848 */     Iterator entries = res.entrySet().iterator();
/*      */     
/* 1850 */     while (entries.hasNext()) {
/* 1851 */       Map.Entry entry = (Map.Entry)entries.next();
/* 1852 */       System.out.print("  ");
/* 1853 */       System.out.print(entry.getKey());
/* 1854 */       System.out.print(": ");
/* 1855 */       System.out.print(entry.getValue());
/* 1856 */       System.out.println();
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   private Map executeHTTP(Map data_to_send, boolean v6)
/*      */     throws Exception
/*      */   {
/*      */     // Byte code:
/*      */     //   0: iload_2
/*      */     //   1: ifeq +20 -> 21
/*      */     //   4: aload_0
/*      */     //   5: getfield 1295	com/aelitis/azureus/core/versioncheck/VersionCheckClient:enable_v6	Z
/*      */     //   8: ifne +13 -> 21
/*      */     //   11: new 844	java/lang/Exception
/*      */     //   14: dup
/*      */     //   15: ldc 19
/*      */     //   17: invokespecial 1370	java/lang/Exception:<init>	(Ljava/lang/String;)V
/*      */     //   20: athrow
/*      */     //   21: aload_0
/*      */     //   22: iload_2
/*      */     //   23: ldc 65
/*      */     //   25: ldc 64
/*      */     //   27: invokevirtual 1341	com/aelitis/azureus/core/versioncheck/VersionCheckClient:getHost	(ZLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*      */     //   30: astore_3
/*      */     //   31: invokestatic 1465	org/gudy/azureus2/core3/logging/Logger:isEnabled	()Z
/*      */     //   34: ifeq +50 -> 84
/*      */     //   37: new 881	org/gudy/azureus2/core3/logging/LogEvent
/*      */     //   40: dup
/*      */     //   41: getstatic 1303	com/aelitis/azureus/core/versioncheck/VersionCheckClient:LOGID	Lorg/gudy/azureus2/core3/logging/LogIDs;
/*      */     //   44: new 851	java/lang/StringBuilder
/*      */     //   47: dup
/*      */     //   48: invokespecial 1399	java/lang/StringBuilder:<init>	()V
/*      */     //   51: ldc 26
/*      */     //   53: invokevirtual 1405	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   56: aload_3
/*      */     //   57: invokevirtual 1405	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   60: ldc 9
/*      */     //   62: invokevirtual 1405	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   65: bipush 80
/*      */     //   67: invokevirtual 1402	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   70: ldc 5
/*      */     //   72: invokevirtual 1405	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   75: invokevirtual 1400	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   78: invokespecial 1464	org/gudy/azureus2/core3/logging/LogEvent:<init>	(Lorg/gudy/azureus2/core3/logging/LogIDs;Ljava/lang/String;)V
/*      */     //   81: invokestatic 1467	org/gudy/azureus2/core3/logging/Logger:log	(Lorg/gudy/azureus2/core3/logging/LogEvent;)V
/*      */     //   84: new 851	java/lang/StringBuilder
/*      */     //   87: dup
/*      */     //   88: invokespecial 1399	java/lang/StringBuilder:<init>	()V
/*      */     //   91: ldc 49
/*      */     //   93: invokevirtual 1405	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   96: iload_2
/*      */     //   97: ifeq +10 -> 107
/*      */     //   100: aload_3
/*      */     //   101: invokestatic 1485	org/gudy/azureus2/core3/util/UrlUtils:convertIPV6Host	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   104: goto +4 -> 108
/*      */     //   107: aload_3
/*      */     //   108: invokevirtual 1405	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   111: ldc 1
/*      */     //   113: invokevirtual 1405	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   116: ldc 8
/*      */     //   118: invokevirtual 1405	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   121: invokevirtual 1400	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   124: astore 4
/*      */     //   126: new 851	java/lang/StringBuilder
/*      */     //   129: dup
/*      */     //   130: invokespecial 1399	java/lang/StringBuilder:<init>	()V
/*      */     //   133: aload 4
/*      */     //   135: invokevirtual 1405	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   138: new 850	java/lang/String
/*      */     //   141: dup
/*      */     //   142: aload_1
/*      */     //   143: invokestatic 1475	org/gudy/azureus2/core3/util/BEncoder:encode	(Ljava/util/Map;)[B
/*      */     //   146: ldc 20
/*      */     //   148: invokespecial 1396	java/lang/String:<init>	([BLjava/lang/String;)V
/*      */     //   151: ldc 20
/*      */     //   153: invokestatic 1435	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*      */     //   156: invokevirtual 1405	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   159: invokevirtual 1400	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   162: astore 4
/*      */     //   164: new 862	java/net/URL
/*      */     //   167: dup
/*      */     //   168: aload 4
/*      */     //   170: invokespecial 1432	java/net/URL:<init>	(Ljava/lang/String;)V
/*      */     //   173: astore 5
/*      */     //   175: ldc 60
/*      */     //   177: invokestatic 1451	org/gudy/azureus2/core3/config/COConfigurationManager:getBooleanParameter	(Ljava/lang/String;)Z
/*      */     //   180: ifeq +13 -> 193
/*      */     //   183: new 844	java/lang/Exception
/*      */     //   186: dup
/*      */     //   187: ldc 14
/*      */     //   189: invokespecial 1370	java/lang/Exception:<init>	(Ljava/lang/String;)V
/*      */     //   192: athrow
/*      */     //   193: new 873	java/util/Properties
/*      */     //   196: dup
/*      */     //   197: invokespecial 1443	java/util/Properties:<init>	()V
/*      */     //   200: astore 6
/*      */     //   202: aload 6
/*      */     //   204: ldc 22
/*      */     //   206: aload 5
/*      */     //   208: invokevirtual 1445	java/util/Properties:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   211: pop
/*      */     //   212: invokestatic 1490	org/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl:getSingleton	()Lorg/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl;
/*      */     //   215: astore 7
/*      */     //   217: aload 7
/*      */     //   219: ifnull +19 -> 238
/*      */     //   222: aload 7
/*      */     //   224: invokevirtual 1489	org/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl:getGenerator	()Lorg/gudy/azureus2/plugins/clientid/ClientIDGenerator;
/*      */     //   227: ifnull +11 -> 238
/*      */     //   230: aload 7
/*      */     //   232: aconst_null
/*      */     //   233: aload 6
/*      */     //   235: invokevirtual 1488	org/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl:generateHTTPProperties	([BLjava/util/Properties;)V
/*      */     //   238: goto +23 -> 261
/*      */     //   241: astore 7
/*      */     //   243: aload 7
/*      */     //   245: invokestatic 1479	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   248: new 837	java/io/IOException
/*      */     //   251: dup
/*      */     //   252: aload 7
/*      */     //   254: invokevirtual 1408	java/lang/Throwable:getMessage	()Ljava/lang/String;
/*      */     //   257: invokespecial 1359	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   260: athrow
/*      */     //   261: aload 6
/*      */     //   263: ldc 22
/*      */     //   265: invokevirtual 1444	java/util/Properties:get	(Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   268: checkcast 862	java/net/URL
/*      */     //   271: astore 5
/*      */     //   273: aload 5
/*      */     //   275: invokevirtual 1433	java/net/URL:openConnection	()Ljava/net/URLConnection;
/*      */     //   278: checkcast 856	java/net/HttpURLConnection
/*      */     //   281: astore 7
/*      */     //   283: aload 7
/*      */     //   285: sipush 10000
/*      */     //   288: invokevirtual 1412	java/net/HttpURLConnection:setConnectTimeout	(I)V
/*      */     //   291: aload 7
/*      */     //   293: sipush 10000
/*      */     //   296: invokevirtual 1413	java/net/HttpURLConnection:setReadTimeout	(I)V
/*      */     //   299: aload 7
/*      */     //   301: invokevirtual 1410	java/net/HttpURLConnection:connect	()V
/*      */     //   304: aload 7
/*      */     //   306: invokevirtual 1414	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
/*      */     //   309: astore 8
/*      */     //   311: new 834	java/io/BufferedInputStream
/*      */     //   314: dup
/*      */     //   315: aload 8
/*      */     //   317: invokespecial 1353	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
/*      */     //   320: invokestatic 1473	org/gudy/azureus2/core3/util/BDecoder:decode	(Ljava/io/BufferedInputStream;)Ljava/util/Map;
/*      */     //   323: astore 9
/*      */     //   325: aload_0
/*      */     //   326: aload 9
/*      */     //   328: iload_2
/*      */     //   329: invokevirtual 1331	com/aelitis/azureus/core/versioncheck/VersionCheckClient:preProcessReply	(Ljava/util/Map;Z)V
/*      */     //   332: aload 9
/*      */     //   334: astore 10
/*      */     //   336: aload 7
/*      */     //   338: invokevirtual 1411	java/net/HttpURLConnection:disconnect	()V
/*      */     //   341: aload 10
/*      */     //   343: areturn
/*      */     //   344: astore 11
/*      */     //   346: aload 7
/*      */     //   348: invokevirtual 1411	java/net/HttpURLConnection:disconnect	()V
/*      */     //   351: aload 11
/*      */     //   353: athrow
/*      */     //   354: astore 6
/*      */     //   356: iload_2
/*      */     //   357: ifne +139 -> 496
/*      */     //   360: ldc 28
/*      */     //   362: aload 5
/*      */     //   364: invokestatic 1321	com/aelitis/azureus/core/proxy/AEProxyFactory:getPluginProxy	(Ljava/lang/String;Ljava/net/URL;)Lcom/aelitis/azureus/core/proxy/AEProxyFactory$PluginProxy;
/*      */     //   367: astore 7
/*      */     //   369: aload 7
/*      */     //   371: ifnull +125 -> 496
/*      */     //   374: iconst_0
/*      */     //   375: istore 8
/*      */     //   377: aload 7
/*      */     //   379: invokeinterface 1504 1 0
/*      */     //   384: aload 7
/*      */     //   386: invokeinterface 1503 1 0
/*      */     //   391: invokevirtual 1434	java/net/URL:openConnection	(Ljava/net/Proxy;)Ljava/net/URLConnection;
/*      */     //   394: checkcast 856	java/net/HttpURLConnection
/*      */     //   397: astore 9
/*      */     //   399: aload 9
/*      */     //   401: sipush 30000
/*      */     //   404: invokevirtual 1412	java/net/HttpURLConnection:setConnectTimeout	(I)V
/*      */     //   407: aload 9
/*      */     //   409: sipush 30000
/*      */     //   412: invokevirtual 1413	java/net/HttpURLConnection:setReadTimeout	(I)V
/*      */     //   415: aload 9
/*      */     //   417: invokevirtual 1410	java/net/HttpURLConnection:connect	()V
/*      */     //   420: aload 9
/*      */     //   422: invokevirtual 1414	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
/*      */     //   425: astore 10
/*      */     //   427: new 834	java/io/BufferedInputStream
/*      */     //   430: dup
/*      */     //   431: aload 10
/*      */     //   433: invokespecial 1353	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
/*      */     //   436: invokestatic 1473	org/gudy/azureus2/core3/util/BDecoder:decode	(Ljava/io/BufferedInputStream;)Ljava/util/Map;
/*      */     //   439: astore 11
/*      */     //   441: aload_0
/*      */     //   442: aload 11
/*      */     //   444: iload_2
/*      */     //   445: invokevirtual 1331	com/aelitis/azureus/core/versioncheck/VersionCheckClient:preProcessReply	(Ljava/util/Map;Z)V
/*      */     //   448: iconst_1
/*      */     //   449: istore 8
/*      */     //   451: aload 11
/*      */     //   453: astore 12
/*      */     //   455: aload 9
/*      */     //   457: invokevirtual 1411	java/net/HttpURLConnection:disconnect	()V
/*      */     //   460: aload 7
/*      */     //   462: iload 8
/*      */     //   464: invokeinterface 1502 2 0
/*      */     //   469: aload 12
/*      */     //   471: areturn
/*      */     //   472: astore 13
/*      */     //   474: aload 9
/*      */     //   476: invokevirtual 1411	java/net/HttpURLConnection:disconnect	()V
/*      */     //   479: aload 13
/*      */     //   481: athrow
/*      */     //   482: astore 14
/*      */     //   484: aload 7
/*      */     //   486: iload 8
/*      */     //   488: invokeinterface 1502 2 0
/*      */     //   493: aload 14
/*      */     //   495: athrow
/*      */     //   496: aload 6
/*      */     //   498: athrow
/*      */     // Line number table:
/*      */     //   Java source line #919	-> byte code offset #0
/*      */     //   Java source line #921	-> byte code offset #11
/*      */     //   Java source line #924	-> byte code offset #21
/*      */     //   Java source line #926	-> byte code offset #31
/*      */     //   Java source line #927	-> byte code offset #37
/*      */     //   Java source line #930	-> byte code offset #84
/*      */     //   Java source line #932	-> byte code offset #126
/*      */     //   Java source line #934	-> byte code offset #164
/*      */     //   Java source line #937	-> byte code offset #175
/*      */     //   Java source line #939	-> byte code offset #183
/*      */     //   Java source line #942	-> byte code offset #193
/*      */     //   Java source line #944	-> byte code offset #202
/*      */     //   Java source line #947	-> byte code offset #212
/*      */     //   Java source line #949	-> byte code offset #217
/*      */     //   Java source line #951	-> byte code offset #230
/*      */     //   Java source line #959	-> byte code offset #238
/*      */     //   Java source line #954	-> byte code offset #241
/*      */     //   Java source line #956	-> byte code offset #243
/*      */     //   Java source line #958	-> byte code offset #248
/*      */     //   Java source line #961	-> byte code offset #261
/*      */     //   Java source line #963	-> byte code offset #273
/*      */     //   Java source line #965	-> byte code offset #283
/*      */     //   Java source line #966	-> byte code offset #291
/*      */     //   Java source line #968	-> byte code offset #299
/*      */     //   Java source line #971	-> byte code offset #304
/*      */     //   Java source line #973	-> byte code offset #311
/*      */     //   Java source line #975	-> byte code offset #325
/*      */     //   Java source line #977	-> byte code offset #332
/*      */     //   Java source line #981	-> byte code offset #336
/*      */     //   Java source line #983	-> byte code offset #354
/*      */     //   Java source line #985	-> byte code offset #356
/*      */     //   Java source line #987	-> byte code offset #360
/*      */     //   Java source line #989	-> byte code offset #369
/*      */     //   Java source line #991	-> byte code offset #374
/*      */     //   Java source line #994	-> byte code offset #377
/*      */     //   Java source line #996	-> byte code offset #399
/*      */     //   Java source line #997	-> byte code offset #407
/*      */     //   Java source line #999	-> byte code offset #415
/*      */     //   Java source line #1002	-> byte code offset #420
/*      */     //   Java source line #1004	-> byte code offset #427
/*      */     //   Java source line #1006	-> byte code offset #441
/*      */     //   Java source line #1008	-> byte code offset #448
/*      */     //   Java source line #1010	-> byte code offset #451
/*      */     //   Java source line #1014	-> byte code offset #455
/*      */     //   Java source line #1018	-> byte code offset #460
/*      */     //   Java source line #1014	-> byte code offset #472
/*      */     //   Java source line #1018	-> byte code offset #482
/*      */     //   Java source line #1023	-> byte code offset #496
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	499	0	this	VersionCheckClient
/*      */     //   0	499	1	data_to_send	Map
/*      */     //   0	499	2	v6	boolean
/*      */     //   30	78	3	host	String
/*      */     //   124	45	4	url_str	String
/*      */     //   173	190	5	url	java.net.URL
/*      */     //   200	62	6	http_properties	java.util.Properties
/*      */     //   354	143	6	e	Exception
/*      */     //   215	16	7	cman	org.gudy.azureus2.pluginsimpl.local.clientid.ClientIDManagerImpl
/*      */     //   241	12	7	e	Throwable
/*      */     //   281	66	7	url_connection	java.net.HttpURLConnection
/*      */     //   367	118	7	proxy	com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy
/*      */     //   309	7	8	is	InputStream
/*      */     //   375	112	8	worked	boolean
/*      */     //   323	10	9	reply	Map
/*      */     //   397	78	9	url_connection	java.net.HttpURLConnection
/*      */     //   425	7	10	is	InputStream
/*      */     //   344	8	11	localObject1	Object
/*      */     //   439	13	11	reply	Map
/*      */     //   453	17	12	localMap2	Map
/*      */     //   472	8	13	localObject2	Object
/*      */     //   482	12	14	localObject3	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   212	238	241	java/lang/Throwable
/*      */     //   304	336	344	finally
/*      */     //   344	346	344	finally
/*      */     //   175	341	354	java/lang/Exception
/*      */     //   344	354	354	java/lang/Exception
/*      */     //   420	455	472	finally
/*      */     //   472	474	472	finally
/*      */     //   377	460	482	finally
/*      */     //   472	484	482	finally
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/versioncheck/VersionCheckClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */