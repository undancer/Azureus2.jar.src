/*      */ package org.gudy.azureus2.pluginsimpl.local.utils;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.InetAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import java.util.WeakHashMap;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPChecker;
/*      */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerFactory;
/*      */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerService;
/*      */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerServiceListener;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.IPToHostNameResolverListener;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.Timer;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*      */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*      */ import org.gudy.azureus2.plugins.Plugin;
/*      */ import org.gudy.azureus2.plugins.PluginException;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*      */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentFile;
/*      */ import org.gudy.azureus2.plugins.utils.AggregatedDispatcher;
/*      */ import org.gudy.azureus2.plugins.utils.AggregatedList;
/*      */ import org.gudy.azureus2.plugins.utils.AggregatedListAcceptor;
/*      */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*      */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*      */ import org.gudy.azureus2.plugins.utils.FeatureManager.FeatureDetails;
/*      */ import org.gudy.azureus2.plugins.utils.FeatureManager.FeatureEnabler;
/*      */ import org.gudy.azureus2.plugins.utils.FeatureManager.FeatureManagerListener;
/*      */ import org.gudy.azureus2.plugins.utils.FeatureManager.Licence;
/*      */ import org.gudy.azureus2.plugins.utils.Formatters;
/*      */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.LocationProvider;
/*      */ import org.gudy.azureus2.plugins.utils.LocationProviderListener;
/*      */ import org.gudy.azureus2.plugins.utils.Monitor;
/*      */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*      */ import org.gudy.azureus2.plugins.utils.PowerManagementListener;
/*      */ import org.gudy.azureus2.plugins.utils.ScriptProvider;
/*      */ import org.gudy.azureus2.plugins.utils.ScriptProvider.ScriptProviderListener;
/*      */ import org.gudy.azureus2.plugins.utils.Semaphore;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities.JSONClient;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities.JSONServer;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*      */ import org.gudy.azureus2.plugins.utils.resourceuploader.ResourceUploaderFactory;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchException;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchInitiator;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchProvider;
/*      */ import org.gudy.azureus2.plugins.utils.security.SESecurityManager;
/*      */ import org.gudy.azureus2.plugins.utils.subscriptions.Subscription;
/*      */ import org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionException;
/*      */ import org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionManager;
/*      */ import org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionResult;
/*      */ import org.gudy.azureus2.plugins.utils.xml.rss.RSSFeed;
/*      */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException;
/*      */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentFactory;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ddb.DDBaseImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.network.ConnectionManagerImpl.PluginRateLimiter;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.resourceuploader.ResourceUploaderFactoryImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.security.SESecurityManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.xml.rss.RSSFeedImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.xml.simpleparser.SimpleXMLParserDocumentFactoryImpl;
/*      */ 
/*      */ public class UtilitiesImpl implements Utilities, FeatureManager
/*      */ {
/*      */   private static InetAddress last_public_ip_address;
/*      */   private static long last_public_ip_address_time;
/*      */   private AzureusCore core;
/*      */   private PluginInterface pi;
/*  120 */   private static ThreadLocal<PluginInterface> tls = new ThreadLocal()
/*      */   {
/*      */ 
/*      */     public PluginInterface initialValue()
/*      */     {
/*      */ 
/*  126 */       return null;
/*      */     }
/*      */   };
/*      */   
/*  130 */   private static ThreadLocal<Object[]> verified_enablers_tls = new ThreadLocal()
/*      */   {
/*      */ 
/*      */     public Object[] initialValue()
/*      */     {
/*      */ 
/*  136 */       return new Object[] { Long.valueOf(0L), Integer.valueOf(0), null };
/*      */     }
/*      */   };
/*      */   
/*  140 */   private static List<searchManager> search_managers = new ArrayList();
/*  141 */   private static List<Object[]> search_providers = new ArrayList();
/*      */   
/*  143 */   private static CopyOnWriteList<Object[]> feature_enablers = new CopyOnWriteList();
/*  144 */   private static CopyOnWriteList<FeatureManager.FeatureManagerListener> feature_listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*  147 */   private static FeatureManager.FeatureManagerListener feature_listener = new FeatureManager.FeatureManagerListener()
/*      */   {
/*      */     public void licenceAdded(FeatureManager.Licence licence)
/*      */     {
/*      */       
/*      */       
/*      */ 
/*      */ 
/*  155 */       for (FeatureManager.FeatureManagerListener listener : UtilitiesImpl.feature_listeners) {
/*      */         try
/*      */         {
/*  158 */           listener.licenceAdded(licence);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  162 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void licenceChanged(FeatureManager.Licence licence)
/*      */     {
/*      */       
/*      */       
/*  173 */       for (FeatureManager.FeatureManagerListener listener : UtilitiesImpl.feature_listeners) {
/*      */         try
/*      */         {
/*  176 */           listener.licenceChanged(licence);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  180 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void licenceRemoved(FeatureManager.Licence licence)
/*      */     {
/*      */       
/*      */       
/*  191 */       for (FeatureManager.FeatureManagerListener listener : UtilitiesImpl.feature_listeners) {
/*      */         try
/*      */         {
/*  194 */           listener.licenceRemoved(licence);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  198 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*  206 */   private static WeakHashMap<RateLimiter, PluginLimitedRateGroup> limiter_map = new WeakHashMap();
/*      */   
/*      */ 
/*  209 */   private static CopyOnWriteList<LocationProviderListener> lp_listeners = new CopyOnWriteList();
/*  210 */   private static CopyOnWriteList<LocationProvider> location_providers = new CopyOnWriteList();
/*      */   
/*  212 */   private static CopyOnWriteList<ScriptProvider.ScriptProviderListener> sp_listeners = new CopyOnWriteList();
/*  213 */   private static CopyOnWriteList<ScriptProvider> script_providers = new CopyOnWriteList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static PluginLimitedRateGroup wrapLimiter(RateLimiter limiter, boolean disable_disable)
/*      */   {
/*  221 */     synchronized (limiter_map)
/*      */     {
/*  223 */       PluginLimitedRateGroup l = (PluginLimitedRateGroup)limiter_map.get(limiter);
/*      */       
/*  225 */       if (l == null)
/*      */       {
/*  227 */         l = new PluginLimitedRateGroup(limiter, disable_disable, null);
/*      */         
/*  229 */         limiter_map.put(limiter, l);
/*      */ 
/*      */       }
/*  232 */       else if (l.isDisableDisable() != disable_disable)
/*      */       {
/*  234 */         Debug.out("Inconsistent setting for disable_disable");
/*      */       }
/*      */       
/*      */ 
/*  238 */       return l;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static RateLimiter unwrapLmiter(PluginLimitedRateGroup l)
/*      */   {
/*  246 */     return l.limiter;
/*      */   }
/*      */   
/*      */ 
/*      */   private static void checkFeatureCache()
/*      */   {
/*  252 */     Set<String> features = new TreeSet();
/*      */     
/*  254 */     List<FeatureManager.FeatureEnabler> enablers = getVerifiedEnablers();
/*      */     
/*  256 */     for (FeatureManager.FeatureEnabler enabler : enablers) {
/*      */       try
/*      */       {
/*  259 */         FeatureManager.Licence[] licences = enabler.getLicences();
/*      */         
/*  261 */         for (FeatureManager.Licence licence : licences)
/*      */         {
/*  263 */           int licence_state = licence.getState();
/*      */           
/*  265 */           if (licence_state == 2)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  270 */             FeatureManager.FeatureDetails[] details = licence.getFeatures();
/*      */             
/*  272 */             for (FeatureManager.FeatureDetails detail : details)
/*      */             {
/*  274 */               if (!detail.hasExpired())
/*      */               {
/*  276 */                 features.add(detail.getID());
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       } catch (Throwable e) {
/*  282 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*  286 */     if (!getFeaturesInstalled().equals(features))
/*      */     {
/*  288 */       String str = "";
/*      */       
/*  290 */       for (String f : features)
/*      */       {
/*  292 */         str = str + (str.length() == 0 ? "" : ",") + f;
/*      */       }
/*      */       
/*  295 */       COConfigurationManager.setParameter("featman.cache.features.installed", str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static Set<String> getFeaturesInstalled()
/*      */   {
/*  302 */     String str = COConfigurationManager.getStringParameter("featman.cache.features.installed", "");
/*      */     
/*  304 */     Set<String> result = new TreeSet();
/*      */     
/*  306 */     if (str.length() > 0)
/*      */     {
/*  308 */       result.addAll(Arrays.asList(str.split(",")));
/*      */     }
/*      */     
/*  311 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public UtilitiesImpl(AzureusCore _core, PluginInterface _pi)
/*      */   {
/*  319 */     this.core = _core;
/*  320 */     this.pi = _pi;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getAzureusUserDir()
/*      */   {
/*  326 */     String res = SystemProperties.getUserPath();
/*      */     
/*  328 */     if (res.endsWith(File.separator))
/*      */     {
/*  330 */       res = res.substring(0, res.length() - 1);
/*      */     }
/*      */     
/*  333 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getAzureusProgramDir()
/*      */   {
/*  342 */     String res = SystemProperties.getApplicationPath();
/*      */     
/*  344 */     if (res.endsWith(File.separator))
/*      */     {
/*  346 */       res = res.substring(0, res.length() - 1);
/*      */     }
/*      */     
/*  349 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isWindows()
/*      */   {
/*  355 */     return Constants.isWindows;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isLinux()
/*      */   {
/*  361 */     return Constants.isLinux;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isUnix()
/*      */   {
/*  367 */     return Constants.isUnix;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isFreeBSD()
/*      */   {
/*  373 */     return Constants.isFreeBSD;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSolaris()
/*      */   {
/*  379 */     return Constants.isSolaris;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isOSX()
/*      */   {
/*  385 */     return Constants.isOSX;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isCVSVersion()
/*      */   {
/*  391 */     return Constants.isCVSVersion();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public InputStream getImageAsStream(String image_name)
/*      */   {
/*  398 */     return UtilitiesImpl.class.getClassLoader().getResourceAsStream("org/gudy/azureus2/ui/icons/" + image_name);
/*      */   }
/*      */   
/*      */ 
/*      */   public Semaphore getSemaphore()
/*      */   {
/*  404 */     return new SemaphoreImpl(this.pi);
/*      */   }
/*      */   
/*      */   public Monitor getMonitor() {
/*  408 */     return new MonitorImpl(this.pi);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ByteBuffer allocateDirectByteBuffer(int size)
/*      */   {
/*  416 */     return DirectByteBufferPool.getBuffer(, size).getBuffer((byte)1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void freeDirectByteBuffer(ByteBuffer buffer) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PooledByteBuffer allocatePooledByteBuffer(int length)
/*      */   {
/*  431 */     return new PooledByteBufferImpl(length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public PooledByteBuffer allocatePooledByteBuffer(byte[] data)
/*      */   {
/*  438 */     return new PooledByteBufferImpl(data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PooledByteBuffer allocatePooledByteBuffer(Map map)
/*      */     throws IOException
/*      */   {
/*  447 */     return new PooledByteBufferImpl(BEncoder.encode(map));
/*      */   }
/*      */   
/*      */ 
/*      */   public Formatters getFormatters()
/*      */   {
/*  453 */     return new FormattersImpl();
/*      */   }
/*      */   
/*      */ 
/*      */   public LocaleUtilities getLocaleUtilities()
/*      */   {
/*  459 */     return new LocaleUtilitiesImpl(this.pi);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public UTTimer createTimer(String name)
/*      */   {
/*  466 */     return new UTTimerImpl(this.pi, name, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public UTTimer createTimer(String name, boolean lightweight)
/*      */   {
/*  474 */     return new UTTimerImpl(this.pi, name, lightweight);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public UTTimer createTimer(String name, int priority)
/*      */   {
/*  482 */     return new UTTimerImpl(this.pi, name, priority);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void createThread(String name, final Runnable target)
/*      */   {
/*  490 */     AEThread2 t = new AEThread2(this.pi.getPluginName() + "::" + name, true)
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*  496 */         UtilitiesImpl.callWithPluginThreadContext(UtilitiesImpl.this.pi, target);
/*      */       }
/*      */       
/*  499 */     };
/*  500 */     t.start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void createProcess(String command_line)
/*      */     throws PluginException
/*      */   {
/*      */     try
/*      */     {
/*  512 */       PlatformManager pm = PlatformManagerFactory.getPlatformManager();
/*      */       
/*  514 */       if (pm.hasCapability(PlatformManagerCapabilities.CreateCommandLineProcess))
/*      */       {
/*  516 */         pm.createProcess(command_line, false);
/*      */         
/*  518 */         return;
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  522 */       Debug.printStackTrace(e);
/*      */     }
/*      */     try
/*      */     {
/*  526 */       Runtime.getRuntime().exec(command_line);
/*      */     }
/*      */     catch (Throwable f)
/*      */     {
/*  530 */       throw new PluginException("Failed to create process", f);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public ResourceDownloaderFactory getResourceDownloaderFactory()
/*      */   {
/*  537 */     return org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl.getSingleton();
/*      */   }
/*      */   
/*      */ 
/*      */   public ResourceUploaderFactory getResourceUploaderFactory()
/*      */   {
/*  543 */     return ResourceUploaderFactoryImpl.getSingleton();
/*      */   }
/*      */   
/*      */ 
/*      */   public SESecurityManager getSecurityManager()
/*      */   {
/*  549 */     return new SESecurityManagerImpl(this.core);
/*      */   }
/*      */   
/*      */ 
/*      */   public SimpleXMLParserDocumentFactory getSimpleXMLParserDocumentFactory()
/*      */   {
/*  555 */     return new SimpleXMLParserDocumentFactoryImpl();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public RSSFeed getRSSFeed(InputStream is)
/*      */     throws SimpleXMLParserDocumentException
/*      */   {
/*  564 */     return getRSSFeed(null, is);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public RSSFeed getRSSFeed(URL source_url, InputStream is)
/*      */     throws SimpleXMLParserDocumentException
/*      */   {
/*      */     try
/*      */     {
/*  575 */       return new RSSFeedImpl(this, source_url, is);
/*      */     }
/*      */     finally
/*      */     {
/*      */       try {
/*  580 */         is.close();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public RSSFeed getRSSFeed(URL feed_location)
/*      */     throws ResourceDownloaderException, SimpleXMLParserDocumentException
/*      */   {
/*  593 */     String feed_str = feed_location.toExternalForm();
/*      */     
/*  595 */     String lc_feed_str = feed_str.toLowerCase(Locale.US);
/*      */     
/*      */ 
/*      */ 
/*  599 */     AEProxyFactory.PluginProxy plugin_proxy = null;
/*      */     try { String target_resource;
/*      */       ResourceDownloader rd;
/*  602 */       if (lc_feed_str.startsWith("tor:"))
/*      */       {
/*  604 */         target_resource = feed_str.substring(4);
/*      */         try
/*      */         {
/*  607 */           feed_location = new URL(target_resource);
/*      */         }
/*      */         catch (MalformedURLException e)
/*      */         {
/*  611 */           throw new ResourceDownloaderException(e);
/*      */         }
/*      */         
/*  614 */         Map<String, Object> options = new HashMap();
/*      */         
/*  616 */         options.put("peer_networks", new String[] { "Tor" });
/*      */         
/*  618 */         plugin_proxy = AEProxyFactory.getPluginProxy("RSS Feed download of '" + target_resource + "'", feed_location, options, true);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  625 */         if (plugin_proxy == null)
/*      */         {
/*  627 */           throw new ResourceDownloaderException("No Tor plugin proxy available for '" + feed_str + "'");
/*      */         }
/*      */         
/*      */ 
/*  631 */         ResourceDownloader rd = getResourceDownloaderFactory().create(plugin_proxy.getURL(), plugin_proxy.getProxy());
/*      */         
/*  633 */         rd.setProperty("URL_HOST", plugin_proxy.getURLHostRewrite() + (feed_location.getPort() == -1 ? "" : new StringBuilder().append(":").append(feed_location.getPort()).toString()));
/*      */ 
/*      */ 
/*      */       }
/*  637 */       else if (AENetworkClassifier.categoriseAddress(feed_location.getHost()) != "Public")
/*      */       {
/*  639 */         plugin_proxy = AEProxyFactory.getPluginProxy("RSS Feed download of '" + feed_location + "'", feed_location, true);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  645 */         if (plugin_proxy == null)
/*      */         {
/*  647 */           throw new ResourceDownloaderException("No Plugin proxy available for '" + feed_str + "'");
/*      */         }
/*      */         
/*      */ 
/*  651 */         ResourceDownloader rd = getResourceDownloaderFactory().create(plugin_proxy.getURL(), plugin_proxy.getProxy());
/*      */         
/*  653 */         rd.setProperty("URL_HOST", plugin_proxy.getURLHostRewrite() + (feed_location.getPort() == -1 ? "" : new StringBuilder().append(":").append(feed_location.getPort()).toString()));
/*      */       }
/*      */       else
/*      */       {
/*  657 */         rd = getResourceDownloaderFactory().create(feed_location);
/*      */       }
/*      */       
/*      */ 
/*  661 */       return getRSSFeed(feed_location, rd);
/*      */     }
/*      */     finally
/*      */     {
/*  665 */       if (plugin_proxy != null)
/*      */       {
/*  667 */         plugin_proxy.setOK(true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public RSSFeed getRSSFeed(ResourceDownloader feed_location)
/*      */     throws ResourceDownloaderException, SimpleXMLParserDocumentException
/*      */   {
/*  678 */     return getRSSFeed(null, feed_location);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public RSSFeed getRSSFeed(URL source_url, ResourceDownloader feed_location)
/*      */     throws ResourceDownloaderException, SimpleXMLParserDocumentException
/*      */   {
/*  689 */     return new RSSFeedImpl(this, source_url, feed_location);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public InetAddress getPublicAddress(boolean v6)
/*      */   {
/*  696 */     if (v6)
/*      */     {
/*  698 */       String vc_ip = VersionCheckClient.getSingleton().getExternalIpAddress(false, true);
/*      */       
/*  700 */       if ((vc_ip != null) && (vc_ip.length() > 0)) {
/*      */         try
/*      */         {
/*  703 */           return InetAddress.getByName(vc_ip);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  707 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/*  711 */       return null;
/*      */     }
/*      */     
/*  714 */     return getPublicAddress();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public InetAddress getPublicAddress()
/*      */   {
/*  721 */     long now = SystemTime.getCurrentTime();
/*      */     
/*  723 */     if (now < last_public_ip_address_time)
/*      */     {
/*  725 */       last_public_ip_address_time = now;
/*      */ 
/*      */ 
/*      */     }
/*  729 */     else if ((last_public_ip_address != null) && (now - last_public_ip_address_time < 900000L))
/*      */     {
/*  731 */       return last_public_ip_address;
/*      */     }
/*      */     
/*      */ 
/*  735 */     InetAddress res = null;
/*      */     
/*      */     try
/*      */     {
/*  739 */       String vc_ip = VersionCheckClient.getSingleton().getExternalIpAddress(false, false);
/*      */       
/*  741 */       if ((vc_ip != null) && (vc_ip.length() > 0))
/*      */       {
/*  743 */         res = InetAddress.getByName(vc_ip);
/*      */       }
/*      */       else
/*      */       {
/*  747 */         ExternalIPChecker checker = ExternalIPCheckerFactory.create();
/*      */         
/*  749 */         ExternalIPCheckerService[] services = checker.getServices();
/*      */         
/*  751 */         final String[] ip = { null };
/*      */         
/*  753 */         for (int i = 0; (i < services.length) && (ip[0] == null); i++)
/*      */         {
/*  755 */           ExternalIPCheckerService service = services[i];
/*      */           
/*  757 */           if (service.supportsCheck())
/*      */           {
/*  759 */             final AESemaphore sem = new AESemaphore("Utilities:getExtIP");
/*      */             
/*  761 */             ExternalIPCheckerServiceListener listener = new ExternalIPCheckerServiceListener()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void checkComplete(ExternalIPCheckerService _service, String _ip)
/*      */               {
/*      */ 
/*      */ 
/*  769 */                 ip[0] = _ip;
/*      */                 
/*  771 */                 sem.release();
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public void checkFailed(ExternalIPCheckerService _service, String _reason)
/*      */               {
/*  779 */                 sem.release();
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void reportProgress(ExternalIPCheckerService _service, String _message) {}
/*  789 */             };
/*  790 */             services[i].addListener(listener);
/*      */             
/*      */             try
/*      */             {
/*  794 */               services[i].initiateCheck(60000L);
/*      */               
/*  796 */               sem.reserve(60000L);
/*      */             }
/*      */             finally
/*      */             {
/*  800 */               services[i].removeListener(listener);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  805 */           if (ip[0] != null)
/*      */           {
/*  807 */             res = InetAddress.getByName(ip[0]);
/*      */             
/*  809 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  815 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*  818 */     if (res == null)
/*      */     {
/*      */ 
/*      */ 
/*  822 */       res = last_public_ip_address;
/*      */     }
/*      */     else
/*      */     {
/*  826 */       last_public_ip_address = res;
/*      */       
/*  828 */       last_public_ip_address_time = now;
/*      */     }
/*      */     
/*  831 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String reverseDNSLookup(InetAddress address)
/*      */   {
/*  838 */     final AESemaphore sem = new AESemaphore("Utilities:reverseDNS");
/*      */     
/*  840 */     final String[] res = { null };
/*      */     
/*  842 */     org.gudy.azureus2.core3.util.IPToHostNameResolver.addResolverRequest(address.getHostAddress(), new IPToHostNameResolverListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void IPResolutionComplete(String result, boolean succeeded)
/*      */       {
/*      */ 
/*      */ 
/*  851 */         if (succeeded)
/*      */         {
/*  853 */           res[0] = result;
/*      */         }
/*      */         
/*  856 */         sem.release();
/*      */       }
/*      */       
/*  859 */     });
/*  860 */     sem.reserve(60000L);
/*      */     
/*  862 */     return res[0];
/*      */   }
/*      */   
/*      */   public long getCurrentSystemTime()
/*      */   {
/*  867 */     return SystemTime.getCurrentTime();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public org.gudy.azureus2.plugins.utils.ByteArrayWrapper createWrapper(byte[] data)
/*      */   {
/*  874 */     return new HashWrapper(data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public AggregatedDispatcher createAggregatedDispatcher(final long idle_dispatch_time, long max_queue_size)
/*      */   {
/*  882 */     new AggregatedDispatcher()
/*      */     {
/*      */ 
/*  885 */       private AggregatedList list = UtilitiesImpl.this.createAggregatedList(new AggregatedListAcceptor()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void accept(List l)
/*      */         {
/*      */ 
/*      */ 
/*  893 */           for (int i = 0; i < l.size(); i++) {
/*      */             try
/*      */             {
/*  896 */               ((Runnable)l.get(i)).run();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  900 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*  885 */       }, idle_dispatch_time, this.val$max_queue_size);
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
/*      */       public void add(Runnable runnable)
/*      */       {
/*  912 */         this.list.add(runnable);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public Runnable remove(Runnable runnable)
/*      */       {
/*  919 */         return (Runnable)this.list.remove(runnable);
/*      */       }
/*      */       
/*      */ 
/*      */       public void destroy()
/*      */       {
/*  925 */         this.list.destroy();
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public AggregatedList createAggregatedList(final AggregatedListAcceptor acceptor, long idle_dispatch_time, final long max_queue_size)
/*      */   {
/*  936 */     new AggregatedList()
/*      */     {
/*      */ 
/*  939 */       AEMonitor timer_mon = new AEMonitor("aggregatedList");
/*      */       
/*  941 */       Timer timer = new Timer("AggregatedList");
/*      */       
/*      */       TimerEvent event;
/*  944 */       List list = new ArrayList();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void add(Object obj)
/*      */       {
/*  951 */         List dispatch_now = null;
/*      */         try
/*      */         {
/*  954 */           this.timer_mon.enter();
/*      */           
/*      */ 
/*      */ 
/*  958 */           if ((max_queue_size > 0L) && (max_queue_size == this.list.size()))
/*      */           {
/*      */ 
/*  961 */             dispatch_now = this.list;
/*      */             
/*  963 */             this.list = new ArrayList();
/*      */           }
/*      */           
/*      */ 
/*  967 */           this.list.add(obj);
/*      */           
/*      */ 
/*      */ 
/*  971 */           long now = SystemTime.getCurrentTime();
/*      */           
/*  973 */           if (this.event != null)
/*      */           {
/*  975 */             this.event.cancel();
/*      */           }
/*      */           
/*  978 */           this.event = this.timer.addEvent(now + acceptor, new TimerEventPerformer()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void perform(TimerEvent event)
/*      */             {
/*      */ 
/*      */ 
/*  987 */               UtilitiesImpl.8.this.dispatch();
/*      */             }
/*      */           });
/*      */         }
/*      */         finally {
/*  992 */           this.timer_mon.exit();
/*      */         }
/*      */         
/*  995 */         if (dispatch_now != null)
/*      */         {
/*  997 */           dispatch(dispatch_now);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public Object remove(Object obj)
/*      */       {
/* 1005 */         Object res = null;
/*      */         try
/*      */         {
/* 1008 */           this.timer_mon.enter();
/*      */           
/* 1010 */           res = this.list.remove(obj) ? obj : null;
/*      */           
/* 1012 */           if (res != null)
/*      */           {
/* 1014 */             long now = SystemTime.getCurrentTime();
/*      */             
/* 1016 */             if (this.event != null)
/*      */             {
/* 1018 */               this.event.cancel();
/*      */             }
/*      */             
/* 1021 */             if (this.list.size() == 0)
/*      */             {
/* 1023 */               this.event = null;
/*      */             }
/*      */             else
/*      */             {
/* 1027 */               this.event = this.timer.addEvent(now + acceptor, new TimerEventPerformer()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void perform(TimerEvent event)
/*      */                 {
/*      */ 
/*      */ 
/* 1036 */                   UtilitiesImpl.8.this.dispatch();
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/* 1043 */           this.timer_mon.exit();
/*      */         }
/*      */         
/* 1046 */         return res;
/*      */       }
/*      */       
/*      */ 
/*      */       protected void dispatch()
/*      */       {
/*      */         List dispatch_list;
/*      */         try
/*      */         {
/* 1055 */           this.timer_mon.enter();
/*      */           
/* 1057 */           dispatch_list = this.list;
/*      */           
/* 1059 */           this.list = new ArrayList();
/*      */         }
/*      */         finally
/*      */         {
/* 1063 */           this.timer_mon.exit();
/*      */         }
/*      */         
/* 1066 */         dispatch(dispatch_list);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       protected void dispatch(List l)
/*      */       {
/* 1073 */         if (l.size() > 0) {
/*      */           try
/*      */           {
/* 1076 */             this.val$acceptor.accept(l);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1080 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void destroy()
/*      */       {
/* 1088 */         dispatch();
/*      */         
/* 1090 */         this.timer.destroy();
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final void callWithPluginThreadContext(PluginInterface pi, Runnable target)
/*      */   {
/* 1100 */     PluginInterface existing = (PluginInterface)tls.get();
/*      */     try
/*      */     {
/* 1103 */       tls.set(pi);
/*      */       
/* 1105 */       target.run();
/*      */     }
/*      */     finally
/*      */     {
/* 1109 */       tls.set(existing);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final <T extends Exception> void callWithPluginThreadContext(PluginInterface pi, runnableWithException<T> target)
/*      */     throws Exception
/*      */   {
/* 1120 */     PluginInterface existing = (PluginInterface)tls.get();
/*      */     try
/*      */     {
/* 1123 */       tls.set(pi);
/*      */       
/* 1125 */       target.run();
/*      */     }
/*      */     finally
/*      */     {
/* 1129 */       tls.set(existing);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final <T> T callWithPluginThreadContext(PluginInterface pi, runnableWithReturn<T> target)
/*      */   {
/* 1138 */     PluginInterface existing = (PluginInterface)tls.get();
/*      */     try
/*      */     {
/* 1141 */       tls.set(pi);
/*      */       
/* 1143 */       return (T)target.run();
/*      */     }
/*      */     finally
/*      */     {
/* 1147 */       tls.set(existing);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final <T, S extends Exception> T callWithPluginThreadContext(PluginInterface pi, runnableWithReturnAndException<T, S> target)
/*      */     throws Exception
/*      */   {
/* 1158 */     PluginInterface existing = (PluginInterface)tls.get();
/*      */     try
/*      */     {
/* 1161 */       tls.set(pi);
/*      */       
/* 1163 */       return (T)target.run();
/*      */     }
/*      */     finally
/*      */     {
/* 1167 */       tls.set(existing);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static PluginInterface getPluginThreadContext()
/*      */   {
/* 1174 */     return (PluginInterface)tls.get();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map readResilientBEncodedFile(File parent_dir, String file_name, boolean use_backup)
/*      */   {
/* 1183 */     return FileUtil.readResilientFile(parent_dir, file_name, use_backup);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeResilientBEncodedFile(File parent_dir, String file_name, Map data, boolean use_backup)
/*      */   {
/* 1193 */     FileUtil.writeResilientFile(parent_dir, file_name, data, use_backup);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void deleteResilientBEncodedFile(File parent_dir, String file_name, boolean use_backup)
/*      */   {
/* 1202 */     FileUtil.deleteResilientFile(new File(parent_dir, file_name));
/*      */   }
/*      */   
/*      */   public int compareVersions(String v1, String v2) {
/* 1206 */     return Constants.compareVersions(v1, v2);
/*      */   }
/*      */   
/*      */   public String normaliseFileName(String f_name) {
/* 1210 */     return FileUtil.convertOSSpecificChars(f_name, false);
/*      */   }
/*      */   
/*      */   public DelayedTask createDelayedTask(Runnable target) {
/* 1214 */     return addDelayedTask(this.pi.getPluginName(), target);
/*      */   }
/*      */   
/* 1217 */   private static List delayed_tasks = new ArrayList();
/* 1218 */   private static AESemaphore delayed_tasks_sem = new AESemaphore("Utilities:delayedTask");
/*      */   private static AEThread2 delayed_task_thread;
/*      */   
/*      */   public static DelayedTask addDelayedTask(String name, Runnable r) {
/* 1222 */     DelayedTaskImpl res = new DelayedTaskImpl(name, null);
/* 1223 */     res.setTask(r);
/* 1224 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void queueTask(DelayedTaskImpl task, int pos)
/*      */   {
/* 1232 */     synchronized (delayed_tasks)
/*      */     {
/* 1234 */       delayed_tasks.add(pos == -1 ? delayed_tasks.size() : pos, task);
/*      */       
/* 1236 */       delayed_tasks_sem.release();
/*      */       
/* 1238 */       if (delayed_task_thread == null)
/*      */       {
/* 1240 */         delayed_task_thread = new AEThread2("Utilities:delayedTask", true)
/*      */         {
/*      */           public void run()
/*      */           {
/*      */             try
/*      */             {
/*      */               
/*      */               
/*      */ 
/*      */               for (;;)
/*      */               {
/* 1251 */                 if (!UtilitiesImpl.delayed_tasks_sem.reserve(5000L))
/*      */                 {
/* 1253 */                   synchronized (UtilitiesImpl.delayed_tasks)
/*      */                   {
/* 1255 */                     if (UtilitiesImpl.delayed_tasks.isEmpty())
/*      */                     {
/* 1257 */                       UtilitiesImpl.access$802(null);
/*      */                       
/* 1259 */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 else
/*      */                 {
/*      */                   UtilitiesImpl.DelayedTaskImpl task;
/* 1266 */                   synchronized (UtilitiesImpl.delayed_tasks)
/*      */                   {
/* 1268 */                     task = (UtilitiesImpl.DelayedTaskImpl)UtilitiesImpl.delayed_tasks.remove(0);
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/* 1273 */                   task.run();
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally {
/* 1278 */               PluginInitializer.removeInitThread();
/*      */             }
/*      */             
/*      */           }
/* 1282 */         };
/* 1283 */         delayed_task_thread.setPriority(1);
/*      */         
/* 1285 */         delayed_task_thread.start();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerSearchProvider(SearchProvider provider)
/*      */     throws SearchException
/*      */   {
/*      */     List<searchManager> managers;
/*      */     
/*      */ 
/* 1298 */     synchronized (UtilitiesImpl.class)
/*      */     {
/* 1300 */       search_providers.add(new Object[] { this.pi, provider });
/*      */       
/* 1302 */       managers = new ArrayList(search_managers);
/*      */     }
/*      */     
/* 1305 */     for (int i = 0; i < managers.size(); i++)
/*      */     {
/* 1307 */       ((searchManager)managers.get(i)).addProvider(this.pi, provider);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void unregisterSearchProvider(SearchProvider provider)
/*      */     throws SearchException
/*      */   {
/*      */     List<searchManager> managers;
/*      */     
/*      */ 
/* 1319 */     synchronized (UtilitiesImpl.class)
/*      */     {
/* 1321 */       Iterator<Object[]> it = search_providers.iterator();
/*      */       
/* 1323 */       while (it.hasNext())
/*      */       {
/* 1325 */         Object[] entry = (Object[])it.next();
/*      */         
/* 1327 */         if ((entry[0] == this.pi) && (entry[1] == provider))
/*      */         {
/* 1329 */           it.remove();
/*      */         }
/*      */       }
/*      */       
/* 1333 */       managers = new ArrayList(search_managers);
/*      */     }
/*      */     
/* 1336 */     for (int i = 0; i < managers.size(); i++)
/*      */     {
/* 1338 */       ((searchManager)managers.get(i)).removeProvider(this.pi, provider);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SearchInitiator getSearchInitiator()
/*      */     throws SearchException
/*      */   {
/*      */     List<searchManager> managers;
/*      */     
/* 1349 */     synchronized (UtilitiesImpl.class)
/*      */     {
/* 1351 */       managers = new ArrayList(search_managers);
/*      */     }
/*      */     
/* 1354 */     if (managers.size() == 0)
/*      */     {
/* 1356 */       throw new SearchException("No search managers registered - try later");
/*      */     }
/*      */     
/* 1359 */     return (SearchInitiator)managers.get(0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void addSearchManager(searchManager manager)
/*      */   {
/*      */     List providers;
/*      */     
/* 1368 */     synchronized (UtilitiesImpl.class)
/*      */     {
/* 1370 */       search_managers.add(manager);
/*      */       
/* 1372 */       providers = new ArrayList(search_providers);
/*      */     }
/*      */     
/* 1375 */     for (int i = 0; i < providers.size(); i++)
/*      */     {
/* 1377 */       Object[] entry = (Object[])providers.get(i);
/*      */       
/* 1379 */       manager.addProvider((PluginInterface)entry[0], (SearchProvider)entry[1]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public FeatureManager getFeatureManager()
/*      */   {
/* 1386 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public FeatureManager.Licence[] createLicences(String[] feature_ids)
/*      */     throws PluginException
/*      */   {
/* 1395 */     List<FeatureManager.FeatureEnabler> enablers = getVerifiedEnablers();
/*      */     
/* 1397 */     Throwable last_error = null;
/*      */     
/* 1399 */     for (FeatureManager.FeatureEnabler enabler : enablers) {
/*      */       try
/*      */       {
/* 1402 */         FeatureManager.Licence[] licences = enabler.createLicences(feature_ids);
/*      */         
/* 1404 */         if (licences != null)
/*      */         {
/* 1406 */           return licences;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1410 */         Debug.out(e);
/*      */         
/* 1412 */         last_error = e;
/*      */       }
/*      */     }
/*      */     
/* 1416 */     if (last_error == null)
/*      */     {
/* 1418 */       throw getLicenceException("Failed to create licence");
/*      */     }
/*      */     
/*      */ 
/* 1422 */     throw new PluginException("Licence handler failed to create licence", last_error);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public FeatureManager.Licence addLicence(String licence_key)
/*      */     throws PluginException
/*      */   {
/* 1432 */     List<FeatureManager.FeatureEnabler> enablers = getVerifiedEnablers();
/*      */     
/* 1434 */     Throwable last_error = null;
/*      */     
/* 1436 */     for (FeatureManager.FeatureEnabler enabler : enablers) {
/*      */       try
/*      */       {
/* 1439 */         FeatureManager.Licence licence = enabler.addLicence(licence_key);
/*      */         
/* 1441 */         if (licence != null)
/*      */         {
/* 1443 */           return licence;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1447 */         last_error = e;
/*      */         
/* 1449 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1453 */     if (last_error == null)
/*      */     {
/* 1455 */       throw getLicenceException("Licence addition failed");
/*      */     }
/*      */     
/*      */ 
/* 1459 */     throw new PluginException("Licence handler failed to add licence", last_error);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private PluginException getLicenceException(String str)
/*      */   {
/*      */     try
/*      */     {
/* 1468 */       String extra = "";
/*      */       
/* 1470 */       PluginInterface fm_pi = this.core.getPluginManager().getPluginInterfaceByID("aefeatman_v", false);
/*      */       
/*      */ 
/* 1473 */       if ((fm_pi == null) || ((fm_pi.getPluginVersion() != null) && (fm_pi.getPluginVersion().equals("0.0"))))
/*      */       {
/* 1475 */         Download[] downloads = this.pi.getDownloadManager().getDownloads();
/*      */         
/* 1477 */         Download hit = null;
/*      */         
/* 1479 */         for (Download download : downloads)
/*      */         {
/* 1481 */           Torrent torrent = download.getTorrent();
/*      */           
/* 1483 */           if ((torrent != null) && (torrent.isSimpleTorrent()))
/*      */           {
/* 1485 */             String name = torrent.getFiles()[0].getName();
/*      */             
/* 1487 */             if ((name.startsWith("aefeatman_v_")) && (name.endsWith(".zip")))
/*      */             {
/* 1489 */               hit = download;
/*      */               
/* 1491 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1496 */         if (hit == null)
/*      */         {
/* 1498 */           extra = "The 'Vuze Feature Manager' plugin is required but isn't installed";
/*      */         }
/*      */         else
/*      */         {
/* 1502 */           int state = hit.getState();
/*      */           
/* 1504 */           if (((state == 7) && (!hit.isComplete())) || (state == 8))
/*      */           {
/*      */ 
/* 1507 */             extra = "The 'Vuze Feature Manager' plugin has failed to download - check your Library's detailed view for errors or stopped downloads";
/*      */           }
/*      */           else
/*      */           {
/* 1511 */             extra = "The 'Vuze Feature Manager' plugin is currently downloading, please wait for it to complete and install";
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 1516 */         PluginState ps = fm_pi.getPluginState();
/*      */         
/* 1518 */         if (!ps.isLoadedAtStartup())
/*      */         {
/* 1520 */           extra = "You need to set the 'Vuze Feature Manager' plugin to 'load at startup' in the plugin options";
/*      */         }
/* 1522 */         else if (ps.isDisabled())
/*      */         {
/* 1524 */           extra = "The 'Vuze Feature Manager' plugin needs to be enabled";
/*      */         }
/* 1526 */         else if (!ps.isOperational())
/*      */         {
/* 1528 */           extra = "The 'Vuze Feature Manager' plugin isn't operational";
/*      */         }
/*      */       }
/*      */       
/* 1532 */       return new PluginException(str + ": " + extra);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1536 */       return new PluginException(str, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public FeatureManager.Licence[] getLicences()
/*      */   {
/* 1543 */     List<FeatureManager.Licence> all_licences = new ArrayList();
/*      */     
/* 1545 */     List<FeatureManager.FeatureEnabler> enablers = getVerifiedEnablers();
/*      */     
/* 1547 */     for (FeatureManager.FeatureEnabler enabler : enablers) {
/*      */       try
/*      */       {
/* 1550 */         FeatureManager.Licence[] licence = enabler.getLicences();
/*      */         
/* 1552 */         all_licences.addAll(Arrays.asList(licence));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1556 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1560 */     return (FeatureManager.Licence[])all_licences.toArray(new FeatureManager.Licence[all_licences.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   public void refreshLicences()
/*      */   {
/* 1566 */     List<FeatureManager.FeatureEnabler> enablers = getVerifiedEnablers();
/*      */     
/* 1568 */     for (FeatureManager.FeatureEnabler enabler : enablers) {
/*      */       try
/*      */       {
/* 1571 */         enabler.refreshLicences();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1575 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public FeatureManager.FeatureDetails[] getFeatureDetails(String feature_id)
/*      */   {
/* 1584 */     return getFeatureDetailsSupport(feature_id);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isFeatureInstalled(String feature_id)
/*      */   {
/* 1591 */     return (getVerifiedEnablers().size() > 0) && (getFeaturesInstalled().contains(feature_id));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static FeatureManager.FeatureDetails[] getFeatureDetailsSupport(String feature_id)
/*      */   {
/* 1598 */     List<FeatureManager.FeatureDetails> result = new ArrayList();
/*      */     
/* 1600 */     List<FeatureManager.FeatureEnabler> enablers = getVerifiedEnablers();
/*      */     
/* 1602 */     for (FeatureManager.FeatureEnabler enabler : enablers) {
/*      */       try
/*      */       {
/* 1605 */         FeatureManager.Licence[] licences = enabler.getLicences();
/*      */         
/* 1607 */         for (FeatureManager.Licence licence : licences)
/*      */         {
/* 1609 */           FeatureManager.FeatureDetails[] details = licence.getFeatures();
/*      */           
/* 1611 */           for (FeatureManager.FeatureDetails detail : details)
/*      */           {
/* 1613 */             if (detail.getID().equals(feature_id))
/*      */             {
/* 1615 */               result.add(detail);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1621 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1625 */     return (FeatureManager.FeatureDetails[])result.toArray(new FeatureManager.FeatureDetails[result.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(FeatureManager.FeatureManagerListener listener)
/*      */   {
/* 1632 */     synchronized (feature_enablers)
/*      */     {
/* 1634 */       feature_listeners.add(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(FeatureManager.FeatureManagerListener listener)
/*      */   {
/* 1642 */     synchronized (feature_enablers)
/*      */     {
/* 1644 */       feature_listeners.remove(listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static final List<FeatureManager.FeatureEnabler> getVerifiedEnablers()
/*      */   {
/* 1651 */     long now = SystemTime.getMonotonousTime();
/* 1652 */     int mut = feature_enablers.getMutationCount();
/*      */     
/* 1654 */     Object[] cache = (Object[])verified_enablers_tls.get();
/*      */     
/* 1656 */     long last_time = ((Long)cache[0]).longValue();
/* 1657 */     int old_mut = ((Integer)cache[1]).intValue();
/*      */     
/* 1659 */     if ((last_time != 0L) && (now - last_time < 30000L) && (mut == old_mut))
/*      */     {
/*      */ 
/* 1662 */       return (List)cache[2];
/*      */     }
/*      */     
/* 1665 */     List<FeatureManager.FeatureEnabler> enablers = new ArrayList();
/*      */     
/* 1667 */     for (Object[] entry : feature_enablers)
/*      */     {
/* 1669 */       PluginInterface enabler_pi = (PluginInterface)entry[0];
/* 1670 */       Plugin enabler_plugin = (Plugin)entry[1];
/* 1671 */       FeatureManager.FeatureEnabler enabler = (FeatureManager.FeatureEnabler)entry[2];
/*      */       
/* 1673 */       if (PluginInitializer.isVerified(enabler_pi, enabler_plugin))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1681 */         File f1 = FileUtil.getJarFileFromClass(enabler_plugin.getClass());
/* 1682 */         File f2 = FileUtil.getJarFileFromClass(enabler.getClass());
/*      */         
/* 1684 */         if ((f1 != null) && (f1.equals(f2)))
/*      */         {
/* 1686 */           enablers.add(enabler);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1692 */     verified_enablers_tls.set(new Object[] { Long.valueOf(now), Integer.valueOf(mut), enablers });
/*      */     
/* 1694 */     return enablers;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerFeatureEnabler(FeatureManager.FeatureEnabler enabler)
/*      */   {
/* 1701 */     Plugin plugin = this.pi.getPlugin();
/*      */     
/* 1703 */     if (!PluginInitializer.isVerified(this.pi, plugin))
/*      */     {
/* 1705 */       Debug.out("Feature enabler not registered as plugin unverified");
/*      */       
/* 1707 */       return;
/*      */     }
/*      */     
/* 1710 */     synchronized (feature_enablers)
/*      */     {
/* 1712 */       feature_enablers.add(new Object[] { this.pi, plugin, enabler });
/*      */       
/* 1714 */       enabler.addListener(feature_listener);
/*      */     }
/*      */     
/* 1717 */     checkFeatureCache();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void unregisterFeatureEnabler(FeatureManager.FeatureEnabler enabler)
/*      */   {
/* 1724 */     synchronized (feature_enablers)
/*      */     {
/* 1726 */       for (Object[] entry : feature_enablers)
/*      */       {
/* 1728 */         if (entry[2] == enabler)
/*      */         {
/* 1730 */           feature_enablers.remove(entry);
/*      */           
/* 1732 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1737 */     checkFeatureCache();
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
/*      */   public SubscriptionManager getSubscriptionManager()
/*      */     throws SubscriptionException
/*      */   {
/*      */     try
/*      */     {
/* 1762 */       Method m = Class.forName("com.aelitis.azureus.core.subs.SubscriptionManagerFactory").getMethod("getSingleton", new Class[0]);
/*      */       
/* 1764 */       final PluginSubscriptionManager sm = (PluginSubscriptionManager)m.invoke(null, new Object[0]);
/*      */       
/* 1766 */       new SubscriptionManager()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void requestSubscription(URL url)
/*      */         {
/*      */ 
/* 1773 */           sm.requestSubscription(url, new HashMap());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void requestSubscription(URL url, Map<String, Object> options)
/*      */         {
/* 1781 */           sm.requestSubscription(url, options);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void requestSubscription(SearchProvider sp, Map<String, Object> search_parameters)
/*      */           throws SubscriptionException
/*      */         {
/* 1791 */           sm.requestSubscription(sp, search_parameters);
/*      */         }
/*      */         
/*      */ 
/*      */         public Subscription[] getSubscriptions()
/*      */         {
/* 1797 */           UtilitiesImpl.PluginSubscription[] p_subs = sm.getSubscriptions(true);
/*      */           
/* 1799 */           Subscription[] subs = new Subscription[p_subs.length];
/*      */           
/* 1801 */           for (int i = 0; i < subs.length; i++)
/*      */           {
/* 1803 */             final UtilitiesImpl.PluginSubscription p_sub = p_subs[i];
/*      */             
/* 1805 */             subs[i = new Subscription()
/*      */             {
/*      */ 
/*      */               public String getID()
/*      */               {
/*      */ 
/* 1811 */                 return p_sub.getID();
/*      */               }
/*      */               
/*      */ 
/*      */               public String getName()
/*      */               {
/* 1817 */                 return p_sub.getName();
/*      */               }
/*      */               
/*      */ 
/*      */               public boolean isSearchTemplate()
/*      */               {
/* 1823 */                 return p_sub.isSearchTemplate();
/*      */               }
/*      */               
/*      */ 
/*      */               public SubscriptionResult[] getResults()
/*      */               {
/* 1829 */                 UtilitiesImpl.PluginSubscriptionResult[] p_results = p_sub.getResults(false);
/*      */                 
/* 1831 */                 SubscriptionResult[] results = new SubscriptionResult[p_results.length];
/*      */                 
/* 1833 */                 for (int i = 0; i < results.length; i++)
/*      */                 {
/* 1835 */                   final UtilitiesImpl.PluginSubscriptionResult p_res = p_results[i];
/*      */                   
/* 1837 */                   results[i = new SubscriptionResult()
/*      */                   {
/*      */ 
/* 1840 */                     private Map<Integer, Object> map = p_res.toPropertyMap();
/*      */                     
/*      */ 
/*      */ 
/*      */                     public Object getProperty(int property_name)
/*      */                     {
/* 1846 */                       return this.map.get(Integer.valueOf(property_name));
/*      */                     }
/*      */                     
/*      */ 
/*      */                     public boolean isRead()
/*      */                     {
/* 1852 */                       return p_res.getRead();
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */                     public void setRead(boolean read)
/*      */                     {
/* 1859 */                       p_res.setRead(read);
/*      */                     }
/*      */                   };
/*      */                 }
/*      */                 
/* 1864 */                 return results;
/*      */               }
/*      */             };
/*      */           }
/*      */           
/* 1869 */           return subs;
/*      */         }
/*      */       };
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1875 */       throw new SubscriptionException("Subscriptions unavailable", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsPowerStateControl(int state)
/*      */   {
/* 1883 */     if (state == 1)
/*      */     {
/* 1885 */       return PlatformManagerFactory.getPlatformManager().hasCapability(PlatformManagerCapabilities.PreventComputerSleep);
/*      */     }
/*      */     
/* 1888 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPowerManagementListener(PowerManagementListener listener)
/*      */   {
/* 1895 */     this.core.addPowerManagementListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePowerManagementListener(PowerManagementListener listener)
/*      */   {
/* 1902 */     this.core.removePowerManagementListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */   public List<LocationProvider> getLocationProviders()
/*      */   {
/* 1908 */     return location_providers.getList();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addLocationProvider(LocationProvider provider)
/*      */   {
/* 1915 */     location_providers.add(provider);
/*      */     
/* 1917 */     for (LocationProviderListener l : lp_listeners) {
/*      */       try
/*      */       {
/* 1920 */         l.locationProviderAdded(provider);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1924 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeLocationProvider(LocationProvider provider)
/*      */   {
/* 1933 */     location_providers.remove(provider);
/*      */     
/* 1935 */     for (LocationProviderListener l : lp_listeners) {
/*      */       try
/*      */       {
/* 1938 */         l.locationProviderRemoved(provider);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1942 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addLocationProviderListener(LocationProviderListener listener)
/*      */   {
/* 1951 */     lp_listeners.add(listener);
/*      */     
/* 1953 */     for (LocationProvider lp : location_providers)
/*      */     {
/* 1955 */       listener.locationProviderAdded(lp);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeLocationProviderListener(LocationProviderListener listener)
/*      */   {
/* 1963 */     lp_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<ScriptProvider> getScriptProviders()
/*      */   {
/* 1972 */     return script_providers.getList();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerScriptProvider(ScriptProvider provider)
/*      */   {
/* 1979 */     script_providers.add(provider);
/*      */     
/* 1981 */     for (ScriptProvider.ScriptProviderListener l : sp_listeners) {
/*      */       try
/*      */       {
/* 1984 */         l.scriptProviderAdded(provider);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1988 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void unregisterScriptProvider(ScriptProvider provider)
/*      */   {
/* 1997 */     script_providers.remove(provider);
/*      */     
/* 1999 */     for (ScriptProvider.ScriptProviderListener l : sp_listeners) {
/*      */       try
/*      */       {
/* 2002 */         l.scriptProviderRemoved(provider);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2006 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addScriptProviderListener(ScriptProvider.ScriptProviderListener listener)
/*      */   {
/* 2015 */     sp_listeners.add(listener);
/*      */     
/* 2017 */     for (ScriptProvider lp : script_providers)
/*      */     {
/* 2019 */       listener.scriptProviderAdded(lp);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeScriptProviderListener(ScriptProvider.ScriptProviderListener listener)
/*      */   {
/* 2027 */     sp_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public org.gudy.azureus2.plugins.tag.Tag lookupTag(String name)
/*      */   {
/* 2034 */     List<TagType> tts = TagManagerFactory.getTagManager().getTagTypes();
/*      */     
/* 2036 */     for (TagType tt : tts)
/*      */     {
/* 2038 */       org.gudy.azureus2.plugins.tag.Tag t = tt.getTag(name, true);
/*      */       
/* 2040 */       if (t != null)
/*      */       {
/* 2042 */         return t;
/*      */       }
/*      */     }
/*      */     
/* 2046 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<DistributedDatabase> getDistributedDatabases(String[] networks)
/*      */   {
/* 2053 */     return DDBaseImpl.getDDBs(networks, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<DistributedDatabase> getDistributedDatabases(String[] networks, Map<String, Object> options)
/*      */   {
/* 2061 */     return DDBaseImpl.getDDBs(networks, options);
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
/*      */   public static class PluginLimitedRateGroup
/*      */     implements LimitedRateGroup
/*      */   {
/*      */     private RateLimiter limiter;
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
/*      */     private ConnectionManagerImpl.PluginRateLimiter plimiter;
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
/*      */     private CopyOnWriteList<UtilitiesImpl.PluginLimitedRateGroupListener> listeners;
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
/*      */     private final boolean disable_disable;
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
/* 2180 */     private boolean current_disabled = false;
/*      */     
/*      */ 
/*      */     private long last_sync;
/*      */     
/*      */ 
/*      */ 
/*      */     private PluginLimitedRateGroup(RateLimiter _limiter, boolean _disable_disable)
/*      */     {
/* 2189 */       this.limiter = _limiter;
/*      */       
/* 2191 */       this.disable_disable = _disable_disable;
/*      */       
/* 2193 */       if ((this.limiter instanceof ConnectionManagerImpl.PluginRateLimiter))
/*      */       {
/* 2195 */         this.plimiter = ((ConnectionManagerImpl.PluginRateLimiter)this.limiter);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isDisableDisable()
/*      */     {
/* 2202 */       return this.disable_disable;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void addListener(UtilitiesImpl.PluginLimitedRateGroupListener listener)
/*      */     {
/* 2209 */       if (this.disable_disable)
/*      */       {
/*      */ 
/*      */ 
/* 2213 */         getRateLimitBytesPerSecond();
/*      */         
/* 2215 */         synchronized (this)
/*      */         {
/* 2217 */           if (this.listeners == null)
/*      */           {
/* 2219 */             this.listeners = new CopyOnWriteList();
/*      */           }
/*      */           
/* 2222 */           this.listeners.add(listener);
/*      */           
/* 2224 */           if (this.current_disabled) {
/*      */             try
/*      */             {
/* 2227 */               listener.disabledChanged(this, true);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2231 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeListener(UtilitiesImpl.PluginLimitedRateGroupListener listener)
/*      */     {
/* 2242 */       if (this.disable_disable)
/*      */       {
/* 2244 */         synchronized (this)
/*      */         {
/* 2246 */           if (this.listeners != null)
/*      */           {
/* 2248 */             if (this.listeners.remove(listener))
/*      */             {
/* 2250 */               if (this.current_disabled) {
/*      */                 try
/*      */                 {
/* 2253 */                   listener.disabledChanged(this, false);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 2257 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public String getName()
/*      */     {
/* 2269 */       String name = this.limiter.getName();
/*      */       
/* 2271 */       if (Constants.IS_CVS_VERSION)
/*      */       {
/* 2273 */         if (this.disable_disable)
/*      */         {
/* 2275 */           String str = "";
/*      */           
/* 2277 */           if (this.current_disabled)
/*      */           {
/* 2279 */             str = str + "Disabled";
/*      */           }
/*      */           
/* 2282 */           synchronized (this)
/*      */           {
/* 2284 */             if (this.listeners != null)
/*      */             {
/* 2286 */               str = str + (str.length() == 0 ? "" : "/") + this.listeners.size();
/*      */             }
/*      */           }
/*      */           
/* 2290 */           if (str.length() > 0)
/*      */           {
/* 2292 */             name = name + " (" + str + ")";
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2297 */       return name;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getRateLimitBytesPerSecond()
/*      */     {
/* 2303 */       int value = this.limiter.getRateLimitBytesPerSecond();
/*      */       
/* 2305 */       if (this.disable_disable)
/*      */       {
/* 2307 */         boolean is_disabled = value == -1;
/*      */         
/* 2309 */         if (is_disabled != this.current_disabled)
/*      */         {
/* 2311 */           synchronized (this)
/*      */           {
/* 2313 */             this.current_disabled = is_disabled;
/*      */             
/* 2315 */             if (this.listeners != null)
/*      */             {
/* 2317 */               for (UtilitiesImpl.PluginLimitedRateGroupListener l : this.listeners) {
/*      */                 try
/*      */                 {
/* 2320 */                   l.disabledChanged(this, is_disabled);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 2324 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         } else {
/* 2330 */           long now = SystemTime.getMonotonousTime();
/*      */           
/* 2332 */           if (now - this.last_sync > 60000L)
/*      */           {
/* 2334 */             this.last_sync = now;
/*      */             
/* 2336 */             synchronized (this)
/*      */             {
/* 2338 */               if (this.listeners != null)
/*      */               {
/* 2340 */                 for (UtilitiesImpl.PluginLimitedRateGroupListener l : this.listeners) {
/*      */                   try
/*      */                   {
/* 2343 */                     l.sync(this, this.current_disabled);
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/* 2347 */                     Debug.out(e);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2355 */         return is_disabled ? 0 : value;
/*      */       }
/*      */       
/*      */ 
/* 2359 */       return value;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean isDisabled()
/*      */     {
/* 2366 */       return this.limiter.getRateLimitBytesPerSecond() < 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void updateBytesUsed(int used)
/*      */     {
/* 2373 */       if (this.plimiter != null)
/*      */       {
/* 2375 */         this.plimiter.updateBytesUsed(used);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class DelayedTaskImpl
/*      */     implements DelayedTask
/*      */   {
/*      */     private String name;
/*      */     
/*      */     private Runnable target;
/* 2387 */     private long create_time = SystemTime.getCurrentTime();
/*      */     
/*      */     private long run_time;
/*      */     
/*      */ 
/*      */     private DelayedTaskImpl(String _name)
/*      */     {
/* 2394 */       this.name = _name;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setTask(Runnable _target)
/*      */     {
/* 2401 */       this.target = _target;
/*      */     }
/*      */     
/*      */ 
/*      */     public void queue()
/*      */     {
/* 2407 */       if (this.target == null)
/*      */       {
/* 2409 */         throw new RuntimeException("Target must be set before queueing");
/*      */       }
/*      */       
/* 2412 */       UtilitiesImpl.queueTask(this, -1);
/*      */     }
/*      */     
/*      */ 
/*      */     public void queueFirst()
/*      */     {
/* 2418 */       if (this.target == null)
/*      */       {
/* 2420 */         throw new RuntimeException("Target must be set before queueing");
/*      */       }
/*      */       
/* 2423 */       UtilitiesImpl.queueTask(this, 0);
/*      */     }
/*      */     
/*      */     protected void run()
/*      */     {
/*      */       try
/*      */       {
/* 2430 */         this.run_time = SystemTime.getCurrentTime();
/*      */         
/* 2432 */         this.target.run();
/*      */         
/* 2434 */         long now = SystemTime.getCurrentTime();
/*      */         
/* 2436 */         if (Logger.isEnabled()) {
/* 2437 */           Logger.log(new LogEvent(LogIDs.PLUGIN, 0, "Delayed task '" + getName() + "': queue_time=" + (this.run_time - this.create_time) + ", exec_time=" + (now - this.run_time)));
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/* 2446 */         Debug.out("Initialisation task " + getName() + " failed to complete", e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getName()
/*      */     {
/* 2453 */       return this.name + " (" + this.target.getClass() + ")";
/*      */     }
/*      */   }
/*      */   
/*      */ 
/* 2458 */   private static Map<String, Utilities.JSONServer> json_servers = new HashMap();
/* 2459 */   private static Map<String, Utilities.JSONClient> json_clients = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerJSONRPCServer(Utilities.JSONServer server)
/*      */   {
/* 2465 */     String key = (this.pi == null ? "default" : this.pi.getPluginID()) + ":" + server.getName();
/*      */     
/* 2467 */     synchronized (json_servers)
/*      */     {
/* 2469 */       Utilities.JSONServer existing = (Utilities.JSONServer)json_servers.get(key);
/*      */       
/* 2471 */       if (existing != null)
/*      */       {
/* 2473 */         for (Utilities.JSONClient client : json_clients.values())
/*      */         {
/* 2475 */           client.serverUnregistered(existing);
/*      */         }
/*      */       }
/*      */       
/* 2479 */       json_servers.put(key, server);
/*      */       
/* 2481 */       for (Utilities.JSONClient client : json_clients.values())
/*      */       {
/* 2483 */         client.serverRegistered(server);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void unregisterJSONRPCServer(Utilities.JSONServer server)
/*      */   {
/* 2492 */     String key = (this.pi == null ? "default" : this.pi.getPluginID()) + ":" + server.getName();
/*      */     Utilities.JSONServer existing;
/* 2494 */     synchronized (json_servers)
/*      */     {
/* 2496 */       existing = (Utilities.JSONServer)json_servers.remove(key);
/*      */       
/* 2498 */       if (existing != null)
/*      */       {
/* 2500 */         for (Utilities.JSONClient client : json_clients.values())
/*      */         {
/* 2502 */           client.serverUnregistered(existing);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerJSONRPCClient(Utilities.JSONClient client)
/*      */   {
/* 2512 */     String key = this.pi == null ? "default" : this.pi.getPluginID();
/*      */     
/* 2514 */     synchronized (json_servers)
/*      */     {
/* 2516 */       json_clients.put(key, client);
/*      */       
/* 2518 */       for (Utilities.JSONServer server : json_servers.values())
/*      */       {
/* 2520 */         client.serverRegistered(server);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void unregisterJSONRPCClient(Utilities.JSONClient client)
/*      */   {
/* 2529 */     String key = this.pi == null ? "default" : this.pi.getPluginID();
/*      */     
/* 2531 */     synchronized (json_servers)
/*      */     {
/* 2533 */       json_clients.remove(key);
/*      */     }
/*      */   }
/*      */   
/* 2537 */   private TagManagerImpl tag_manager = new TagManagerImpl(null);
/*      */   
/*      */ 
/*      */   public org.gudy.azureus2.plugins.tag.TagManager getTagManager()
/*      */   {
/* 2542 */     return this.tag_manager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class TagManagerImpl
/*      */     implements org.gudy.azureus2.plugins.tag.TagManager
/*      */   {
/*      */     public List<org.gudy.azureus2.plugins.tag.Tag> getTags()
/*      */     {
/* 2553 */       List<com.aelitis.azureus.core.tag.Tag> tags = TagManagerFactory.getTagManager().getTagType(3).getTags();
/*      */       
/* 2555 */       return new ArrayList(tags);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public org.gudy.azureus2.plugins.tag.Tag lookupTag(String name)
/*      */     {
/* 2562 */       return TagManagerFactory.getTagManager().getTagType(3).getTag(name, true);
/*      */     }
/*      */     
/*      */ 
/*      */     public org.gudy.azureus2.plugins.tag.Tag createTag(String name)
/*      */     {
/*      */       try
/*      */       {
/* 2570 */         return TagManagerFactory.getTagManager().getTagType(3).createTag(name, true);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2574 */         Debug.out(e);
/*      */       }
/* 2576 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   public static abstract interface PluginLimitedRateGroupListener
/*      */   {
/*      */     public abstract void disabledChanged(UtilitiesImpl.PluginLimitedRateGroup paramPluginLimitedRateGroup, boolean paramBoolean);
/*      */     
/*      */     public abstract void sync(UtilitiesImpl.PluginLimitedRateGroup paramPluginLimitedRateGroup, boolean paramBoolean);
/*      */   }
/*      */   
/*      */   public static abstract interface PluginSubscription
/*      */   {
/*      */     public abstract String getID();
/*      */     
/*      */     public abstract String getName();
/*      */     
/*      */     public abstract boolean isSearchTemplate();
/*      */     
/*      */     public abstract UtilitiesImpl.PluginSubscriptionResult[] getResults(boolean paramBoolean);
/*      */   }
/*      */   
/*      */   public static abstract interface PluginSubscriptionManager
/*      */   {
/*      */     public abstract void requestSubscription(URL paramURL, Map<String, Object> paramMap);
/*      */     
/*      */     public abstract void requestSubscription(SearchProvider paramSearchProvider, Map<String, Object> paramMap)
/*      */       throws SubscriptionException;
/*      */     
/*      */     public abstract UtilitiesImpl.PluginSubscription[] getSubscriptions(boolean paramBoolean);
/*      */   }
/*      */   
/*      */   public static abstract interface PluginSubscriptionResult
/*      */   {
/*      */     public abstract Map<Integer, Object> toPropertyMap();
/*      */     
/*      */     public abstract void setRead(boolean paramBoolean);
/*      */     
/*      */     public abstract boolean getRead();
/*      */   }
/*      */   
/*      */   public static abstract interface runnableWithException<T extends Exception>
/*      */   {
/*      */     public abstract void run()
/*      */       throws Exception;
/*      */   }
/*      */   
/*      */   public static abstract interface runnableWithReturn<T>
/*      */   {
/*      */     public abstract T run();
/*      */   }
/*      */   
/*      */   public static abstract interface runnableWithReturnAndException<T, S extends Exception>
/*      */   {
/*      */     public abstract T run()
/*      */       throws Exception;
/*      */   }
/*      */   
/*      */   public static abstract interface searchManager
/*      */     extends SearchInitiator
/*      */   {
/*      */     public abstract void addProvider(PluginInterface paramPluginInterface, SearchProvider paramSearchProvider);
/*      */     
/*      */     public abstract void removeProvider(PluginInterface paramPluginInterface, SearchProvider paramSearchProvider);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/UtilitiesImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */