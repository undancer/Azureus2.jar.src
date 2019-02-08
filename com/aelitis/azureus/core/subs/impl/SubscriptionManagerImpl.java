/*      */ package com.aelitis.azureus.core.subs.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.custom.Customization;
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformSubscriptionsMessenger;
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformSubscriptionsMessenger.subscriptionDetails;
/*      */ import com.aelitis.azureus.core.metasearch.Engine;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearch;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchManager;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchManagerFactory;
/*      */ import com.aelitis.azureus.core.metasearch.impl.web.rss.RSSEngine;
/*      */ import com.aelitis.azureus.core.security.CryptoECCUtils;
/*      */ import com.aelitis.azureus.core.subs.Subscription;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionAssociationLookup;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionLookupListener;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionManagerListener;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionPopularityListener;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionResult;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionUtils;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionUtils.SubscriptionDownloadDetails;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginInterface;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginOperationListener;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginValue;
/*      */ import com.aelitis.azureus.plugins.magnet.MagnetPlugin;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatMessage;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils;
/*      */ import com.aelitis.azureus.util.ImportExportUtils;
/*      */ import com.aelitis.net.magneturi.MagnetURIHandler;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URL;
/*      */ import java.security.KeyPair;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.zip.GZIPInputStream;
/*      */ import java.util.zip.GZIPOutputStream;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchException;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchInstance;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchObserver;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchProvider;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchResult;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.bouncycastle.util.encoders.Base64;
/*      */ 
/*      */ public class SubscriptionManagerImpl implements SubscriptionManager, org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   private static final String CONFIG_FILE = "subscriptions.config";
/*      */   private static final String LOGGER_NAME = "Subscriptions";
/*      */   private static final String CONFIG_MAX_RESULTS = "subscriptions.max.non.deleted.results";
/*      */   private static final String CONFIG_AUTO_START_DLS = "subscriptions.auto.start.downloads";
/*      */   private static final String CONFIG_AUTO_START_MIN_MB = "subscriptions.auto.start.min.mb";
/*      */   private static final String CONFIG_AUTO_START_MAX_MB = "subscriptions.auto.start.max.mb";
/*      */   private static final String CONFIG_AUTO_MARK_READ = "subscriptions.auto.dl.mark.read.days";
/*      */   private static final String CONFIG_RSS_ENABLE = "subscriptions.config.rss_enable";
/*      */   private static final String CONFIG_ENABLE_SEARCH = "subscriptions.config.search_enable";
/*      */   private static final String CONFIG_HIDE_SEARCH_TEMPLATES = "subscriptions.config.hide_search_templates";
/*      */   private static final String CONFIG_DL_SUBS_ENABLE = "subscriptions.config.dl_subs_enable";
/*      */   private static final String CONFIG_DL_RATE_LIMITS = "subscriptions.config.rate_limits";
/*      */   private static final String CONFIG_ACTIVATE_ON_CHANGE = "subscriptions.config.activate.sub.on.change";
/*      */   private static final int DELETE_UNUSED_AFTER_MILLIS = 1209600000;
/*      */   private static final int PUB_ASSOC_CONC_MAX;
/*      */   private static final int PUB_SLEEPING_ASSOC_CONC_MAX = 1;
/*      */   private static SubscriptionManagerImpl singleton;
/*      */   private static boolean pre_initialised;
/*      */   
/*      */   static
/*      */   {
/*  127 */     int max_conc_assoc_pub = 3;
/*      */     
/*      */     try
/*      */     {
/*  131 */       max_conc_assoc_pub = Integer.parseInt(System.getProperty("azureus.subs.max.concurrent.assoc.publish", "" + max_conc_assoc_pub));
/*      */     }
/*      */     catch (Throwable e) {
/*  134 */       Debug.out(e);
/*      */     }
/*      */     
/*  137 */     PUB_ASSOC_CONC_MAX = max_conc_assoc_pub;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  143 */   private static final int random_seed = org.gudy.azureus2.core3.util.RandomUtils.nextInt(256);
/*      */   private boolean started;
/*      */   private static final int TIMER_PERIOD = 30000;
/*      */   private static final int ASSOC_CHECK_PERIOD = 300000;
/*      */   
/*  148 */   public static void preInitialise() { synchronized (SubscriptionManagerImpl.class)
/*      */     {
/*  150 */       if (pre_initialised)
/*      */       {
/*  152 */         return;
/*      */       }
/*      */       
/*  155 */       pre_initialised = true;
/*      */     }
/*      */     
/*  158 */     VuzeFileHandler.getSingleton().addProcessor(new com.aelitis.azureus.core.vuzefile.VuzeFileProcessor()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void process(VuzeFile[] files, int expected_types)
/*      */       {
/*      */ 
/*      */ 
/*  166 */         for (int i = 0; i < files.length; i++)
/*      */         {
/*  168 */           VuzeFile vf = files[i];
/*      */           
/*  170 */           VuzeFileComponent[] comps = vf.getComponents();
/*      */           
/*  172 */           for (int j = 0; j < comps.length; j++)
/*      */           {
/*  174 */             VuzeFileComponent comp = comps[j];
/*      */             
/*  176 */             int type = comp.getType();
/*      */             
/*  178 */             if ((type == 16) || (type == 32))
/*      */             {
/*      */               try
/*      */               {
/*  182 */                 Subscription subs = ((SubscriptionManagerImpl)SubscriptionManagerImpl.getSingleton(false)).importSubscription(type, comp.getContent(), (expected_types & 0x30) == 0);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  188 */                 comp.setProcessed();
/*      */                 
/*  190 */                 comp.setData(Subscription.VUZE_FILE_COMPONENT_SUBSCRIPTION_KEY, subs);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  194 */                 Debug.printStackTrace(e);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static SubscriptionManager getSingleton(boolean stand_alone)
/*      */   {
/*      */     
/*      */     
/*  209 */     synchronized (SubscriptionManagerImpl.class)
/*      */     {
/*  211 */       if (singleton != null)
/*      */       {
/*  213 */         return singleton;
/*      */       }
/*      */       
/*  216 */       singleton = new SubscriptionManagerImpl(stand_alone);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  222 */     if (!stand_alone)
/*      */     {
/*  224 */       singleton.initialise();
/*      */     }
/*      */     
/*  227 */     return singleton;
/*      */   }
/*      */   
/*      */ 
/*      */   private static final int ASSOC_CHECK_TICKS = 10;
/*      */   
/*      */   private static final int ASSOC_PUBLISH_PERIOD = 300000;
/*      */   
/*      */   private static final int ASSOC_PUBLISH_TICKS = 10;
/*      */   
/*      */   private static final int CHAT_CHECK_PERIOD = 180000;
/*      */   
/*      */   private static final int CHAT_CHECK_TICKS = 6;
/*      */   
/*      */   private static final int SERVER_PUB_CHECK_PERIOD = 600000;
/*      */   
/*      */   private static final int SERVER_PUB_CHECK_TICKS = 20;
/*      */   
/*      */   private static final int TIDY_POT_ASSOC_PERIOD = 1800000;
/*      */   
/*      */   private static final int TIDY_POT_ASSOC_TICKS = 60;
/*      */   
/*      */   private static final int SET_SELECTED_PERIOD = 82800000;
/*      */   
/*      */   private static final int SET_SELECTED_FIRST_TICK = 6;
/*      */   
/*      */   private static final int SET_SELECTED_TICKS = 2760;
/*  254 */   private static final Object SP_LAST_ATTEMPTED = new Object();
/*  255 */   private static final Object SP_CONSEC_FAIL = new Object();
/*      */   
/*      */   private AzureusCore azureus_core;
/*      */   
/*      */   private volatile DHTPluginInterface dht_plugin_public;
/*      */   
/*  261 */   private List<SubscriptionImpl> subscriptions = new ArrayList();
/*      */   
/*      */   private boolean config_dirty;
/*      */   
/*      */   private int publish_associations_active;
/*      */   
/*      */   private boolean publish_next_asyc_pending;
/*      */   
/*      */   private boolean publish_subscription_active;
/*      */   
/*      */   private TorrentAttribute ta_subs_download;
/*      */   
/*      */   private TorrentAttribute ta_subs_download_rd;
/*      */   private TorrentAttribute ta_subscription_info;
/*      */   private TorrentAttribute ta_category;
/*      */   private TorrentAttribute ta_networks;
/*      */   private boolean periodic_lookup_in_progress;
/*      */   private int priority_lookup_pending;
/*  279 */   private CopyOnWriteList<SubscriptionManagerListener> listeners = new CopyOnWriteList();
/*      */   
/*      */   private SubscriptionSchedulerImpl scheduler;
/*      */   
/*  283 */   private List<Object[]> potential_associations = new ArrayList();
/*  284 */   private Map<HashWrapper, Object[]> potential_associations2 = new HashMap();
/*  285 */   private Map<HashWrapper, Object[]> potential_associations3 = new HashMap();
/*      */   
/*      */   private boolean meta_search_listener_added;
/*      */   
/*  289 */   private Pattern exclusion_pattern = Pattern.compile("azdev[0-9]+\\.azureus\\.com");
/*      */   
/*      */   private SubscriptionRSSFeed rss_publisher;
/*      */   
/*      */   private AEDiagnosticsLogger logger;
/*      */   
/*  295 */   private Map<SubscriptionImpl, Object[]> result_cache = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SubscriptionManagerImpl(boolean stand_alone)
/*      */   {
/*  302 */     if (!stand_alone)
/*      */     {
/*  304 */       loadConfig();
/*      */       
/*  306 */       org.gudy.azureus2.core3.util.AEDiagnostics.addEvidenceGenerator(this);
/*      */       
/*  308 */       com.aelitis.azureus.core.custom.CustomizationManager cust_man = com.aelitis.azureus.core.custom.CustomizationManagerFactory.getSingleton();
/*      */       
/*  310 */       Customization cust = cust_man.getActiveCustomization();
/*      */       
/*  312 */       if (cust != null)
/*      */       {
/*  314 */         String cust_name = COConfigurationManager.getStringParameter("subscriptions.custom.name", "");
/*  315 */         String cust_version = COConfigurationManager.getStringParameter("subscriptions.custom.version", "0");
/*      */         
/*  317 */         boolean new_name = !cust_name.equals(cust.getName());
/*  318 */         boolean new_version = org.gudy.azureus2.core3.util.Constants.compareVersions(cust_version, cust.getVersion()) < 0;
/*      */         
/*  320 */         if ((new_name) || (new_version))
/*      */         {
/*  322 */           log("Customization: checking templates for " + cust.getName() + "/" + cust.getVersion());
/*      */           try
/*      */           {
/*  325 */             streams = cust.getResources("subs");
/*      */             
/*  327 */             for (i = 0; i < streams.length;)
/*      */             {
/*  329 */               InputStream is = streams[i];
/*      */               try
/*      */               {
/*  332 */                 VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(is);
/*      */                 
/*  334 */                 if (vf != null)
/*      */                 {
/*  336 */                   VuzeFileComponent[] comps = vf.getComponents();
/*      */                   
/*  338 */                   for (int j = 0; j < comps.length; j++)
/*      */                   {
/*  340 */                     VuzeFileComponent comp = comps[j];
/*      */                     
/*  342 */                     int type = comp.getType();
/*      */                     
/*  344 */                     if ((type == 16) || (type == 32))
/*      */                     {
/*      */                       try
/*      */                       {
/*  348 */                         importSubscription(type, comp.getContent(), false);
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*  353 */                         comp.setProcessed();
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/*  357 */                         Debug.printStackTrace(e);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*      */                 try
/*      */                 {
/*  365 */                   is.close();
/*      */                 }
/*      */                 catch (Throwable e) {}
/*  327 */                 i++;
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
/*      */               }
/*      */               finally
/*      */               {
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
/*      */                 try
/*      */                 {
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
/*  365 */                   is.close();
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */             }
/*      */           } finally {
/*      */             InputStream[] streams;
/*      */             int i;
/*  373 */             COConfigurationManager.setParameter("subscriptions.custom.name", cust.getName());
/*  374 */             COConfigurationManager.setParameter("subscriptions.custom.version", cust.getVersion());
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  379 */       this.scheduler = new SubscriptionSchedulerImpl(this);
/*      */     }
/*      */     
/*  382 */     SimpleTimer.addPeriodicEvent("SubscriptionCacheCheck", 10000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  390 */         long now = SystemTime.getMonotonousTime();
/*      */         
/*  392 */         synchronized (SubscriptionManagerImpl.this.result_cache)
/*      */         {
/*  394 */           Iterator<Object[]> it = SubscriptionManagerImpl.this.result_cache.values().iterator();
/*      */           
/*  396 */           while (it.hasNext())
/*      */           {
/*  398 */             long time = ((Long)((Object[])it.next())[1]).longValue();
/*      */             
/*  400 */             if (now - time > 15000L)
/*      */             {
/*  402 */               it.remove();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initialise()
/*      */   {
/*  413 */     AzureusCoreFactory.addCoreRunningListener(new com.aelitis.azureus.core.AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/*  415 */         SubscriptionManagerImpl.this.initWithCore(core);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void initWithCore(AzureusCore _core)
/*      */   {
/*  424 */     synchronized (this)
/*      */     {
/*  426 */       if (this.started)
/*      */       {
/*  428 */         return;
/*      */       }
/*      */       
/*  431 */       this.started = true;
/*      */     }
/*      */     
/*  434 */     this.azureus_core = _core;
/*      */     
/*  436 */     final PluginInterface default_pi = PluginInitializer.getDefaultInterface();
/*      */     
/*  438 */     this.rss_publisher = new SubscriptionRSSFeed(this, default_pi);
/*      */     
/*  440 */     TorrentManager tm = default_pi.getTorrentManager();
/*      */     
/*  442 */     this.ta_subs_download = tm.getPluginAttribute("azsubs.subs_dl");
/*  443 */     this.ta_subs_download_rd = tm.getPluginAttribute("azsubs.subs_dl_rd");
/*  444 */     this.ta_subscription_info = tm.getPluginAttribute("azsubs.subs_info");
/*  445 */     this.ta_category = tm.getAttribute("Category");
/*  446 */     this.ta_networks = tm.getAttribute("Networks");
/*      */     
/*  448 */     PluginInterface dht_plugin_pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass(com.aelitis.azureus.plugins.dht.DHTPlugin.class);
/*      */     
/*  450 */     if (dht_plugin_pi != null)
/*      */     {
/*  452 */       this.dht_plugin_public = ((com.aelitis.azureus.plugins.dht.DHTPlugin)dht_plugin_pi.getPlugin());
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
/*  495 */       default_pi.getDownloadManager().addListener(new org.gudy.azureus2.plugins.download.DownloadManagerListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void downloadAdded(Download download)
/*      */         {
/*      */ 
/*  502 */           Torrent torrent = download.getTorrent();
/*      */           
/*  504 */           if (torrent != null)
/*      */           {
/*  506 */             byte[] hash = torrent.getHash();
/*      */             
/*      */             Object[] entry;
/*      */             
/*  510 */             synchronized (SubscriptionManagerImpl.this.potential_associations2)
/*      */             {
/*  512 */               entry = (Object[])SubscriptionManagerImpl.this.potential_associations2.remove(new HashWrapper(hash));
/*      */             }
/*      */             
/*  515 */             if (entry != null)
/*      */             {
/*  517 */               SubscriptionImpl[] subs = (SubscriptionImpl[])entry[0];
/*      */               
/*  519 */               String subs_str = "";
/*  520 */               for (int i = 0; i < subs.length; i++) {
/*  521 */                 subs_str = subs_str + (i == 0 ? "" : ",") + subs[i].getName();
/*      */               }
/*      */               
/*  524 */               SubscriptionManagerImpl.this.log("Applying deferred asocciation for " + ByteFormatter.encodeString(hash) + " -> " + subs_str);
/*      */               
/*  526 */               SubscriptionManagerImpl.this.recordAssociationsSupport(hash, subs, ((Boolean)entry[1]).booleanValue()); } } } public void downloadRemoved(Download download) {} }, false);
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
/*  542 */       default_pi.getDownloadManager().addDownloadWillBeAddedListener(new org.gudy.azureus2.plugins.download.DownloadWillBeAddedListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void initialised(Download download)
/*      */         {
/*      */ 
/*  549 */           Torrent torrent = download.getTorrent();
/*      */           
/*  551 */           if (torrent != null)
/*      */           {
/*  553 */             byte[] hash = torrent.getHash();
/*      */             
/*  555 */             HashWrapper hw = new HashWrapper(hash);
/*      */             
/*      */             Object[] entry;
/*      */             
/*  559 */             synchronized (SubscriptionManagerImpl.this.potential_associations2)
/*      */             {
/*  561 */               entry = (Object[])SubscriptionManagerImpl.this.potential_associations2.get(hw);
/*      */             }
/*      */             
/*  564 */             if (entry != null)
/*      */             {
/*  566 */               SubscriptionImpl[] subs = (SubscriptionImpl[])entry[0];
/*      */               
/*  568 */               SubscriptionManagerImpl.this.prepareDownload(download, subs, null);
/*      */             }
/*      */             else
/*      */             {
/*  572 */               synchronized (SubscriptionManagerImpl.this.potential_associations3)
/*      */               {
/*  574 */                 entry = (Object[])SubscriptionManagerImpl.this.potential_associations3.get(hw);
/*      */               }
/*      */               
/*  577 */               if (entry != null)
/*      */               {
/*  579 */                 Subscription[] subs = (Subscription[])entry[0];
/*      */                 
/*  581 */                 SubscriptionResult[] results = (SubscriptionResult[])entry[1];
/*      */                 
/*  583 */                 SubscriptionManagerImpl.this.prepareDownload(download, subs, results);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }
/*  589 */       });
/*  590 */       org.gudy.azureus2.core3.util.TorrentUtils.addTorrentAttributeListener(new org.gudy.azureus2.core3.util.TorrentUtils.torrentAttributeListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void attributeSet(TOTorrent torrent, String attribute, Object value)
/*      */         {
/*      */ 
/*      */ 
/*  599 */           if (attribute == "obtained_from") {
/*      */             try
/*      */             {
/*  602 */               SubscriptionManagerImpl.this.checkPotentialAssociations(torrent.getHash(), (String)value);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  606 */               Debug.printStackTrace(e);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*  611 */       });
/*  612 */       org.gudy.azureus2.plugins.utils.DelayedTask delayed_task = org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.addDelayedTask("Subscriptions", new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/*  618 */           new AEThread2("Subscriptions:delayInit", true)
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*  623 */               SubscriptionManagerImpl.7.this.asyncInit();
/*      */             }
/*      */           }.start();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         protected void asyncInit()
/*      */         {
/*  632 */           Download[] downloads = default_pi.getDownloadManager().getDownloads();
/*      */           
/*  634 */           for (int i = 0; i < downloads.length; i++)
/*      */           {
/*  636 */             Download download = downloads[i];
/*      */             
/*  638 */             if (download.getBooleanAttribute(SubscriptionManagerImpl.this.ta_subs_download))
/*      */             {
/*  640 */               Map rd = download.getMapAttribute(SubscriptionManagerImpl.this.ta_subs_download_rd);
/*      */               
/*      */               boolean delete_it;
/*      */               boolean delete_it;
/*  644 */               if (rd == null)
/*      */               {
/*  646 */                 delete_it = true;
/*      */               }
/*      */               else
/*      */               {
/*  650 */                 delete_it = !SubscriptionManagerImpl.this.recoverSubscriptionUpdate(download, rd);
/*      */               }
/*      */               
/*  653 */               if (delete_it)
/*      */               {
/*  655 */                 SubscriptionManagerImpl.this.removeDownload(download, true);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  660 */           default_pi.getDownloadManager().addListener(new org.gudy.azureus2.plugins.download.DownloadManagerListener()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void downloadAdded(final Download download)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  670 */               if (!SubscriptionManagerImpl.this.downloadIsIgnored(download))
/*      */               {
/*  672 */                 if (!SubscriptionManagerImpl.this.dht_plugin_public.isInitialising())
/*      */                 {
/*      */ 
/*      */ 
/*  676 */                   SubscriptionManagerImpl.this.lookupAssociations(download.getMapAttribute(SubscriptionManagerImpl.this.ta_subscription_info) == null);
/*      */                 }
/*      */                 else
/*      */                 {
/*  680 */                   new AEThread2("Subscriptions:delayInit", true)
/*      */                   {
/*      */ 
/*      */                     public void run()
/*      */                     {
/*  685 */                       SubscriptionManagerImpl.this.lookupAssociations(download.getMapAttribute(SubscriptionManagerImpl.this.ta_subscription_info) == null);
/*      */                     }
/*      */                   }.start();
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */             public void downloadRemoved(Download download) {}
/*  680 */           }, false);
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
/*  700 */           for (int i = 0; i < SubscriptionManagerImpl.PUB_ASSOC_CONC_MAX; i++)
/*      */           {
/*  702 */             if (SubscriptionManagerImpl.this.publishAssociations()) {
/*      */               break;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  708 */           SubscriptionManagerImpl.this.publishSubscriptions();
/*      */           
/*  710 */           COConfigurationManager.addParameterListener("subscriptions.max.non.deleted.results", new org.gudy.azureus2.core3.config.ParameterListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void parameterChanged(String name)
/*      */             {
/*      */ 
/*      */ 
/*  718 */               final int max_results = COConfigurationManager.getIntParameter("subscriptions.max.non.deleted.results");
/*      */               
/*  720 */               new AEThread2("Subs:max results changer", true)
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*  725 */                   SubscriptionManagerImpl.this.checkMaxResults(max_results);
/*      */                 }
/*      */                 
/*      */               }.start();
/*      */             }
/*  730 */           });
/*  731 */           SimpleTimer.addPeriodicEvent("SubscriptionChecker", 30000L, new TimerEventPerformer()
/*      */           {
/*      */             private int ticks;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void perform(TimerEvent event)
/*      */             {
/*  742 */               this.ticks += 1;
/*      */               
/*  744 */               SubscriptionManagerImpl.this.checkStuff(this.ticks);
/*      */             }
/*      */             
/*      */           });
/*      */         }
/*  749 */       });
/*  750 */       delayed_task.queue();
/*      */     }
/*      */     
/*  753 */     if (isSearchEnabled()) {
/*      */       try
/*      */       {
/*  756 */         default_pi.getUtilities().registerSearchProvider(new SearchProvider()
/*      */         {
/*      */           private Map<Integer, Object> properties;
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
/*      */           public SearchInstance search(Map<String, Object> search_parameters, SearchObserver observer)
/*      */             throws SearchException
/*      */           {
/*      */             try
/*      */             {
/*  838 */               return SubscriptionManagerImpl.this.searchSubscriptions(search_parameters, observer);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  842 */               throw new SearchException("Search failed", e);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public Object getProperty(int property)
/*      */           {
/*  850 */             return this.properties.get(Integer.valueOf(property));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void setProperty(int property, Object value)
/*      */           {
/*  858 */             this.properties.put(Integer.valueOf(property), value);
/*      */           }
/*      */         });
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  864 */         Debug.out("Failed to register search provider");
/*      */       }
/*      */     }
/*      */     
/*  868 */     default_pi.getUtilities().registerJSONRPCServer(new org.gudy.azureus2.plugins.utils.Utilities.JSONServer()
/*      */     {
/*      */       private List<String> methods;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public String getName()
/*      */       {
/*  880 */         return "Subscriptions";
/*      */       }
/*      */       
/*      */ 
/*      */       public List<String> getSupportedMethods()
/*      */       {
/*  886 */         return this.methods;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public Map call(String method, Map args)
/*      */         throws org.gudy.azureus2.plugins.PluginException
/*      */       {
/*  896 */         throw new org.gudy.azureus2.plugins.PluginException("derp");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   protected Object[] getSearchTemplateVuzeFile(SubscriptionImpl sub)
/*      */   {
/*      */     try
/*      */     {
/*  906 */       String subs_url_str = ((RSSEngine)sub.getEngine()).getSearchUrl(true);
/*      */       
/*  908 */       URL subs_url = new URL(subs_url_str);
/*      */       
/*  910 */       byte[] vf_bytes = FileUtil.readInputStreamAsByteArray(subs_url.openConnection().getInputStream());
/*      */       
/*  912 */       VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(vf_bytes);
/*      */       
/*  914 */       if (MetaSearchManagerFactory.getSingleton().isImportable(vf))
/*      */       {
/*  916 */         return new Object[] { vf, vf_bytes };
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  920 */       Debug.out(e);
/*      */     }
/*      */     
/*  923 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSearchTemplateImportable(SubscriptionImpl sub)
/*      */   {
/*      */     try
/*      */     {
/*  931 */       String subs_url_str = ((RSSEngine)sub.getEngine()).getSearchUrl(true);
/*      */       
/*  933 */       URL subs_url = new URL(subs_url_str);
/*      */       
/*  935 */       byte[] vf_bytes = FileUtil.readInputStreamAsByteArray(subs_url.openConnection().getInputStream());
/*      */       
/*  937 */       VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(vf_bytes);
/*      */       
/*  939 */       return MetaSearchManagerFactory.getSingleton().isImportable(vf);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  943 */       Debug.out(e);
/*      */     }
/*      */     
/*  946 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SearchInstance searchSubscriptions(Map<String, Object> search_parameters, final SearchObserver observer)
/*      */     throws SearchException
/*      */   {
/*  956 */     final String term = (String)search_parameters.get("s");
/*      */     
/*  958 */     final SearchInstance si = new SearchInstance()
/*      */     {
/*      */ 
/*      */       public void cancel()
/*      */       {
/*      */ 
/*  964 */         Debug.out("Cancelled");
/*      */       }
/*      */     };
/*      */     
/*  968 */     if (term == null) {
/*      */       try
/*      */       {
/*  971 */         observer.complete();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  975 */         Debug.out(e);
/*      */       }
/*      */       
/*      */     } else {
/*  979 */       new AEThread2("Subscriptions:search", true)
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*  984 */           Set<String> hashes = new java.util.HashSet();
/*      */           
/*  986 */           SubscriptionManagerImpl.searchMatcher matcher = new SubscriptionManagerImpl.searchMatcher(term);
/*      */           try
/*      */           {
/*  989 */             List<SubscriptionResult> matches = SubscriptionManagerImpl.this.matchSubscriptionResults(matcher);
/*      */             
/*  991 */             for (SubscriptionResult result : matches)
/*      */             {
/*  993 */               final Map result_properties = result.toPropertyMap();
/*      */               
/*  995 */               byte[] hash = (byte[])result_properties.get(Integer.valueOf(21));
/*      */               
/*  997 */               if (hash != null)
/*      */               {
/*  999 */                 String hash_str = Base32.encode(hash);
/*      */                 
/* 1001 */                 if (!hashes.contains(hash_str))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 1006 */                   hashes.add(hash_str);
/*      */                 }
/*      */               } else {
/* 1009 */                 SearchResult search_result = new SearchResult()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public Object getProperty(int property_name)
/*      */                   {
/*      */ 
/* 1016 */                     return result_properties.get(Integer.valueOf(property_name));
/*      */                   }
/*      */                 };
/*      */                 try
/*      */                 {
/* 1021 */                   observer.resultReceived(si, search_result);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1025 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/* 1029 */             Map<String, Object[]> template_matches = new HashMap();
/*      */             
/* 1031 */             Engine[] engines = MetaSearchManagerFactory.getSingleton().getMetaSearch().getEngines(false, false);
/*      */             
/* 1033 */             Map<Subscription, List<String>> sub_dl_name_map = null;
/*      */             
/* 1035 */             for (Subscription sub : SubscriptionManagerImpl.this.getSubscriptions(false))
/*      */             {
/* 1037 */               if (sub.isSearchTemplate())
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 1042 */                 String sub_name = sub.getName(false);
/*      */                 
/* 1044 */                 Engine sub_engine = sub.getEngine();
/*      */                 
/* 1046 */                 if ((!sub_engine.isActive()) && ((sub_engine instanceof RSSEngine)))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 1051 */                   int pos = sub_name.indexOf(":");
/*      */                   
/* 1053 */                   String t_name = sub_name.substring(pos + 1);
/*      */                   
/* 1055 */                   pos = t_name.indexOf("(v");
/*      */                   
/*      */                   int t_ver;
/*      */                   int t_ver;
/* 1059 */                   if (pos == -1)
/*      */                   {
/* 1061 */                     t_ver = 1;
/*      */                   }
/*      */                   else
/*      */                   {
/* 1065 */                     String s = t_name.substring(pos + 2, t_name.length() - 1);
/*      */                     
/* 1067 */                     t_name = t_name.substring(0, pos);
/*      */                     
/*      */                     try
/*      */                     {
/* 1071 */                       t_ver = Integer.parseInt(s);
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/* 1075 */                       t_ver = 1;
/*      */                     }
/*      */                   }
/*      */                   
/* 1079 */                   t_name = t_name.trim();
/*      */                   
/* 1081 */                   boolean skip = false;
/*      */                   
/* 1083 */                   for (Engine e : engines)
/*      */                   {
/* 1085 */                     if ((e != sub_engine) && (e.sameLogicAs(sub_engine)))
/*      */                     {
/* 1087 */                       skip = true;
/*      */                       
/* 1089 */                       break;
/*      */                     }
/*      */                     
/* 1092 */                     if (e.getName().equalsIgnoreCase(t_name))
/*      */                     {
/* 1094 */                       if (e.getVersion() >= t_ver)
/*      */                       {
/* 1096 */                         skip = true;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   
/* 1101 */                   if (!skip)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 1106 */                     if (sub_dl_name_map == null)
/*      */                     {
/* 1108 */                       sub_dl_name_map = new HashMap();
/*      */                       
/* 1110 */                       SubscriptionUtils.SubscriptionDownloadDetails[] sdds = SubscriptionUtils.getAllCachedDownloadDetails(SubscriptionManagerImpl.this.azureus_core);
/*      */                       
/* 1112 */                       for (SubscriptionUtils.SubscriptionDownloadDetails sdd : sdds)
/*      */                       {
/* 1114 */                         String name = sdd.getDownload().getDisplayName();
/*      */                         
/* 1116 */                         if (matcher.matches(name))
/*      */                         {
/* 1118 */                           Subscription[] x = sdd.getSubscriptions();
/*      */                           
/* 1120 */                           for (Subscription s : x)
/*      */                           {
/* 1122 */                             List<String> g = (List)sub_dl_name_map.get(s);
/*      */                             
/* 1124 */                             if (g == null)
/*      */                             {
/* 1126 */                               g = new ArrayList();
/*      */                               
/* 1128 */                               sub_dl_name_map.put(s, g);
/*      */                             }
/*      */                             
/* 1131 */                             g.add(name);
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     
/* 1137 */                     List<String> names = (List)sub_dl_name_map.get(sub);
/*      */                     
/* 1139 */                     if (names != null)
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/* 1144 */                       String key = t_name.toLowerCase();
/*      */                       
/* 1146 */                       Object[] entry = (Object[])template_matches.get(key);
/*      */                       
/* 1148 */                       if (entry == null)
/*      */                       {
/* 1150 */                         entry = new Object[] { sub, Integer.valueOf(t_ver) };
/*      */                         
/* 1152 */                         template_matches.put(key, entry);
/*      */ 
/*      */ 
/*      */                       }
/* 1156 */                       else if (t_ver > ((Integer)entry[1]).intValue())
/*      */                       {
/* 1158 */                         entry[0] = sub;
/* 1159 */                         entry[1] = Integer.valueOf(t_ver);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 } } }
/* 1164 */             List<SubscriptionImpl> interesting = new ArrayList();
/*      */             
/* 1166 */             for (Object[] entry : template_matches.values())
/*      */             {
/* 1168 */               interesting.add((SubscriptionImpl)entry[0]);
/*      */             }
/*      */             
/* 1171 */             Collections.sort(interesting, new Comparator()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public int compare(Subscription o1, Subscription o2)
/*      */               {
/*      */ 
/*      */ 
/* 1180 */                 long res = o2.getCachedPopularity() - o1.getCachedPopularity();
/*      */                 
/* 1182 */                 if (res < 0L)
/* 1183 */                   return -1;
/* 1184 */                 if (res > 0L) {
/* 1185 */                   return 1;
/*      */                 }
/* 1187 */                 return 0;
/*      */               }
/*      */               
/*      */ 
/* 1191 */             });
/* 1192 */             added = 0;
/*      */             
/* 1194 */             for (final SubscriptionImpl sub : interesting)
/*      */             {
/* 1196 */               if (added >= 3) {
/*      */                 break;
/*      */               }
/*      */               
/*      */               try
/*      */               {
/* 1202 */                 Object[] vf_entry = SubscriptionManagerImpl.this.getSearchTemplateVuzeFile(sub);
/*      */                 
/* 1204 */                 if (vf_entry != null)
/*      */                 {
/* 1206 */                   final byte[] vf_bytes = (byte[])vf_entry[1];
/*      */                   
/* 1208 */                   final URL url = MagnetURIHandler.getSingleton().registerResource(new com.aelitis.net.magneturi.MagnetURIHandler.ResourceProvider()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public String getUID()
/*      */                     {
/*      */ 
/* 1215 */                       return SubscriptionManager.class.getName() + ".sid." + sub.getID();
/*      */                     }
/*      */                     
/*      */ 
/*      */                     public String getFileType()
/*      */                     {
/* 1221 */                       return "vuze";
/*      */                     }
/*      */                     
/*      */ 
/*      */                     public byte[] getData()
/*      */                     {
/* 1227 */                       return vf_bytes;
/*      */                     }
/*      */                     
/* 1230 */                   });
/* 1231 */                   SearchResult search_result = new SearchResult()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public Object getProperty(int property_name)
/*      */                     {
/*      */ 
/* 1238 */                       if (property_name == 1)
/*      */                       {
/* 1240 */                         return sub.getName();
/*      */                       }
/* 1242 */                       if ((property_name == 12) || (property_name == 16))
/*      */                       {
/*      */ 
/* 1245 */                         return url.toExternalForm();
/*      */                       }
/* 1247 */                       if (property_name == 2)
/*      */                       {
/* 1249 */                         return new java.util.Date(sub.getAddTime());
/*      */                       }
/* 1251 */                       if (property_name == 3)
/*      */                       {
/* 1253 */                         return Long.valueOf(1024L);
/*      */                       }
/* 1255 */                       if ((property_name == 5) || (property_name == 9))
/*      */                       {
/*      */ 
/* 1258 */                         return Long.valueOf(sub.getCachedPopularity());
/*      */                       }
/* 1260 */                       if (property_name == 17)
/*      */                       {
/* 1262 */                         return Long.valueOf(100L);
/*      */                       }
/*      */                       
/* 1265 */                       return null;
/*      */                     }
/*      */                     
/* 1268 */                   };
/* 1269 */                   added++;
/*      */                   try
/*      */                   {
/* 1272 */                     observer.resultReceived(si, search_result);
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/* 1276 */                     Debug.out(e);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {
/* 1281 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           } catch (Throwable e) {
/*      */             int added;
/* 1286 */             Debug.out(e);
/*      */           }
/*      */           finally
/*      */           {
/* 1290 */             observer.complete();
/*      */           }
/*      */         }
/*      */       }.start();
/*      */     }
/*      */     
/* 1296 */     return si;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private List<SubscriptionResult> matchSubscriptionResults(searchMatcher matcher)
/*      */   {
/* 1303 */     List<SubscriptionResult> result = new ArrayList();
/*      */     
/* 1305 */     for (Subscription sub : getSubscriptions(true))
/*      */     {
/* 1307 */       SubscriptionResult[] results = sub.getResults(false);
/*      */       
/* 1309 */       for (SubscriptionResult r : results)
/*      */       {
/* 1311 */         Map properties = r.toPropertyMap();
/*      */         
/* 1313 */         String name = (String)properties.get(Integer.valueOf(1));
/*      */         
/* 1315 */         if (name != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1320 */           if (matcher.matches(name))
/*      */           {
/* 1322 */             result.add(r);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1327 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkMaxResults(int max)
/*      */   {
/* 1334 */     Subscription[] subs = getSubscriptions();
/*      */     
/* 1336 */     for (int i = 0; i < subs.length; i++)
/*      */     {
/* 1338 */       ((SubscriptionHistoryImpl)subs[i].getHistory()).checkMaxResults(max);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public com.aelitis.azureus.core.subs.SubscriptionScheduler getScheduler()
/*      */   {
/* 1345 */     return this.scheduler;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isRSSPublishEnabled()
/*      */   {
/* 1351 */     return COConfigurationManager.getBooleanParameter("subscriptions.config.rss_enable", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRSSPublishEnabled(boolean enabled)
/*      */   {
/* 1358 */     COConfigurationManager.setParameter("subscriptions.config.rss_enable", enabled);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSearchEnabled()
/*      */   {
/* 1364 */     return COConfigurationManager.getBooleanParameter("subscriptions.config.search_enable", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSearchEnabled(boolean enabled)
/*      */   {
/* 1371 */     COConfigurationManager.setParameter("subscriptions.config.search_enable", enabled);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hideSearchTemplates()
/*      */   {
/* 1377 */     return COConfigurationManager.getBooleanParameter("subscriptions.config.hide_search_templates", true);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSubsDownloadEnabled()
/*      */   {
/* 1383 */     return COConfigurationManager.getBooleanParameter("subscriptions.config.dl_subs_enable", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSubsDownloadEnabled(boolean enabled)
/*      */   {
/* 1390 */     COConfigurationManager.setParameter("subscriptions.config.dl_subs_enable", enabled);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRateLimits(String limits)
/*      */   {
/* 1397 */     COConfigurationManager.setParameter("subscriptions.config.rate_limits", limits);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getRateLimits()
/*      */   {
/* 1404 */     return COConfigurationManager.getStringParameter("subscriptions.config.rate_limits", "");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setActivateSubscriptionOnChange(boolean b)
/*      */   {
/* 1411 */     COConfigurationManager.setParameter("subscriptions.config.activate.sub.on.change", b);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getActivateSubscriptionOnChange()
/*      */   {
/* 1418 */     return COConfigurationManager.getBooleanParameter("subscriptions.config.activate.sub.on.change", false);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRSSLink()
/*      */   {
/* 1424 */     return this.rss_publisher.getFeedURL();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Subscription create(String name, boolean public_subs, String json)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 1435 */     name = getUniqueName(name);
/*      */     
/* 1437 */     boolean is_anonymous = false;
/*      */     
/* 1439 */     SubscriptionImpl subs = new SubscriptionImpl(this, name, public_subs, is_anonymous, null, json, 1);
/*      */     
/* 1441 */     log("Created new subscription: " + subs.getString());
/*      */     
/* 1443 */     if (subs.isPublic())
/*      */     {
/* 1445 */       updatePublicSubscription(subs);
/*      */     }
/*      */     
/* 1448 */     return addSubscription(subs);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Subscription createSingletonRSS(String name, URL url, int check_interval_mins, boolean is_anon)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 1460 */     return createSingletonRSSSupport(name, url, true, check_interval_mins, is_anon, 1, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Subscription createFromURI(String uri)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 1469 */     final AESemaphore sem = new AESemaphore("subswait");
/*      */     
/* 1471 */     final Object[] result = { null };
/*      */     
/* 1473 */     byte[] sid = null;
/* 1474 */     int version = -1;
/* 1475 */     boolean is_anon = false;
/*      */     
/* 1477 */     int pos = uri.indexOf('?');
/*      */     
/* 1479 */     String[] bits = uri.substring(pos + 1).split("&");
/*      */     
/* 1481 */     for (String bit : bits)
/*      */     {
/* 1483 */       String[] temp = bit.split("=");
/*      */       
/* 1485 */       if (temp.length == 2)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1490 */         String lhs = temp[0].toLowerCase(java.util.Locale.US);
/* 1491 */         String rhs = temp[1];
/*      */         
/* 1493 */         if (lhs.equals("id"))
/*      */         {
/* 1495 */           sid = Base32.decode(rhs);
/*      */         }
/* 1497 */         else if (lhs.equals("v"))
/*      */         {
/* 1499 */           version = Integer.parseInt(rhs);
/*      */         }
/* 1501 */         else if (lhs.equals("a"))
/*      */         {
/* 1503 */           is_anon = rhs.equals("1");
/*      */         }
/*      */       }
/*      */     }
/* 1507 */     if ((sid == null) || (version == -1))
/*      */     {
/* 1509 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Invalid URI");
/*      */     }
/*      */     
/* 1512 */     lookupSubscription(new byte[20], sid, version, is_anon, new subsLookupListener()
/*      */     {
/*      */       public void found(byte[] hash, Subscription subscription) {}
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
/*      */       public void failed(byte[] hash, com.aelitis.azureus.core.subs.SubscriptionException error)
/*      */       {
/* 1532 */         synchronized (result)
/*      */         {
/* 1534 */           result[0] = error;
/*      */         }
/*      */         
/* 1537 */         sem.release();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(byte[] hash, Subscription[] subscriptions)
/*      */       {
/* 1545 */         synchronized (result)
/*      */         {
/* 1547 */           if (subscriptions.length > 0)
/*      */           {
/* 1549 */             result[0] = subscriptions[0];
/*      */           }
/*      */           else
/*      */           {
/* 1553 */             result[0] = new com.aelitis.azureus.core.subs.SubscriptionException("Subscription not found");
/*      */           }
/*      */         }
/*      */         
/* 1557 */         sem.release();
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean isCancelled()
/*      */       {
/* 1563 */         return false;
/*      */       }
/*      */       
/* 1566 */     });
/* 1567 */     sem.reserve();
/*      */     
/* 1569 */     if ((result[0] instanceof Subscription))
/*      */     {
/* 1571 */       return (Subscription)result[0];
/*      */     }
/*      */     
/*      */ 
/* 1575 */     throw ((com.aelitis.azureus.core.subs.SubscriptionException)result[0]);
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
/*      */   protected SubscriptionImpl lookupSingletonRSS(String name, URL url, boolean is_public, int check_interval_mins, boolean is_anon)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 1589 */     checkURL(url);
/*      */     
/* 1591 */     Map singleton_details = getSingletonMap(name, url, is_public, check_interval_mins, is_anon);
/*      */     
/* 1593 */     byte[] sid = SubscriptionBodyImpl.deriveSingletonShortID(singleton_details);
/*      */     
/* 1595 */     return getSubscriptionFromSID(sid);
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
/*      */   protected Subscription createSingletonRSSSupport(String name, URL url, boolean is_public, int check_interval_mins, boolean is_anon, int add_type, boolean subscribe)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 1611 */     checkURL(url);
/*      */     try
/*      */     {
/* 1614 */       Subscription existing = lookupSingletonRSS(name, url, is_public, check_interval_mins, is_anon);
/*      */       
/* 1616 */       if (existing != null)
/*      */       {
/* 1618 */         return existing;
/*      */       }
/*      */       
/* 1621 */       Engine engine = MetaSearchManagerFactory.getSingleton().getMetaSearch().createRSSEngine(name, url);
/*      */       
/* 1623 */       String json = SubscriptionImpl.getSkeletonJSON(engine, check_interval_mins);
/*      */       
/* 1625 */       Map singleton_details = getSingletonMap(name, url, is_public, check_interval_mins, is_anon);
/*      */       
/* 1627 */       SubscriptionImpl subs = new SubscriptionImpl(this, name, is_public, is_anon, singleton_details, json, add_type);
/*      */       
/* 1629 */       subs.setSubscribed(subscribe);
/*      */       
/* 1631 */       log("Created new singleton subscription: " + subs.getString());
/*      */       
/* 1633 */       subs = addSubscription(subs);
/*      */       
/* 1635 */       if ((subs.isPublic()) && (subs.isMine()) && (subs.isSearchTemplate()))
/*      */       {
/* 1637 */         updatePublicSubscription(subs);
/*      */       }
/*      */       
/* 1640 */       return subs;
/*      */     }
/*      */     catch (com.aelitis.azureus.core.subs.SubscriptionException e)
/*      */     {
/* 1644 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1648 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Failed to create subscription", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getUniqueName(String name)
/*      */   {
/* 1656 */     for (int i = 0; i < 1024; i++)
/*      */     {
/* 1658 */       String test_name = name + (i == 0 ? "" : new StringBuilder().append(" (").append(i).append(")").toString());
/*      */       
/* 1660 */       if (getSubscriptionFromName(test_name) == null)
/*      */       {
/* 1662 */         return test_name;
/*      */       }
/*      */     }
/*      */     
/* 1666 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map getSingletonMap(String name, URL url, boolean is_public, int check_interval_mins, boolean is_anon)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/*      */     try
/*      */     {
/* 1680 */       Map singleton_details = new HashMap();
/*      */       
/* 1682 */       if (url.getProtocol().equalsIgnoreCase("vuze"))
/*      */       {
/*      */ 
/*      */ 
/* 1686 */         singleton_details.put("key", url.toExternalForm().getBytes("ISO-8859-1"));
/*      */       }
/*      */       else {
/* 1689 */         singleton_details.put("key", url.toExternalForm().getBytes("UTF-8"));
/*      */       }
/*      */       
/* 1692 */       String name2 = name.length() > 64 ? name.substring(0, 64) : name;
/*      */       
/* 1694 */       singleton_details.put("name", name2);
/*      */       
/* 1696 */       if (check_interval_mins != 120)
/*      */       {
/* 1698 */         singleton_details.put("ci", new Long(check_interval_mins));
/*      */       }
/*      */       
/* 1701 */       if (is_anon)
/*      */       {
/* 1703 */         singleton_details.put("a", new Long(1L));
/*      */       }
/*      */       
/* 1706 */       return singleton_details;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1710 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Failed to create subscription", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SubscriptionImpl createSingletonSubscription(Map singleton_details, int add_type, boolean subscribe)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/*      */     try
/*      */     {
/* 1723 */       String name = ImportExportUtils.importString(singleton_details, "name", "(Anonymous)");
/*      */       
/* 1725 */       URL url = new URL(ImportExportUtils.importString(singleton_details, "key"));
/*      */       
/* 1727 */       int check_interval_mins = (int)ImportExportUtils.importLong(singleton_details, "ci", 120L);
/*      */       
/* 1729 */       boolean is_anon = ImportExportUtils.importLong(singleton_details, "a", 0L) != 0L;
/*      */       
/*      */ 
/*      */ 
/* 1733 */       return (SubscriptionImpl)createSingletonRSSSupport(name, url, true, check_interval_mins, is_anon, add_type, subscribe);
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 1739 */       log("Creation of singleton from " + singleton_details + " failed", e);
/*      */       
/* 1741 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Creation of singleton from " + singleton_details + " failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestSubscription(URL url, Map<String, Object> options)
/*      */   {
/* 1750 */     for (SubscriptionManagerListener listener : this.listeners) {
/*      */       try
/*      */       {
/* 1753 */         listener.subscriptionRequested(url, options);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1757 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestSubscription(SearchProvider sp, Map<String, Object> search_parameters)
/*      */     throws org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionException
/*      */   {
/*      */     try
/*      */     {
/* 1770 */       Engine engine = MetaSearchManagerFactory.getSingleton().getEngine(sp);
/*      */       
/* 1772 */       if (engine == null)
/*      */       {
/* 1774 */         throw new com.aelitis.azureus.core.subs.SubscriptionException("Engine not found ");
/*      */       }
/*      */       
/* 1777 */       Boolean anonymous = (Boolean)search_parameters.get("_anonymous_");
/*      */       
/* 1779 */       String term = (String)search_parameters.get("s");
/* 1780 */       String[] networks = (String[])search_parameters.get("n");
/*      */       
/* 1782 */       String networks_str = null;
/*      */       
/* 1784 */       if ((networks != null) && (networks.length > 0))
/*      */       {
/* 1786 */         networks_str = "";
/*      */         
/* 1788 */         for (String network : networks)
/*      */         {
/* 1790 */           networks_str = networks_str + (networks_str.length() == 0 ? "" : ",") + network;
/*      */         }
/*      */       }
/*      */       
/* 1794 */       String json = SubscriptionImpl.getSkeletonJSON(engine, term, networks_str, 60);
/*      */       
/* 1796 */       String name = (String)search_parameters.get("t");
/*      */       
/* 1798 */       if ((name == null) || (name.length() == 0))
/*      */       {
/* 1800 */         name = engine.getName() + ": " + search_parameters.get("s");
/*      */       }
/*      */       
/* 1803 */       boolean anon = (anonymous != null) && (anonymous.booleanValue());
/*      */       
/* 1805 */       SubscriptionImpl subs = new SubscriptionImpl(this, name, engine.isPublic(), anon, null, json, 1);
/*      */       
/* 1807 */       if (anon)
/*      */       {
/* 1809 */         subs.getHistory().setDownloadNetworks(new String[] { "I2P" });
/*      */       }
/*      */       
/* 1812 */       log("Created new subscription: " + subs.getString());
/*      */       
/* 1814 */       subs = addSubscription(subs);
/*      */       
/* 1816 */       Number freq = (Number)search_parameters.get("_frequency_");
/*      */       
/* 1818 */       if (freq != null)
/*      */       {
/* 1820 */         subs.getHistory().setCheckFrequencyMins(freq.intValue());
/*      */       }
/*      */       
/* 1823 */       if (subs.isPublic())
/*      */       {
/* 1825 */         updatePublicSubscription(subs);
/*      */       }
/*      */       
/* 1828 */       Boolean silent = (Boolean)search_parameters.get("_silent_");
/*      */       
/* 1830 */       if ((silent == null) || (!silent.booleanValue()))
/*      */       {
/* 1832 */         subs.requestAttention();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1836 */       throw new org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionException("Failed to create subscription", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Subscription createRSS(String name, URL url, int check_interval_mins, Map user_data)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 1849 */     return createRSS(name, url, check_interval_mins, false, user_data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Subscription createRSS(String name, URL url, int check_interval_mins, boolean is_anonymous, Map user_data)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 1862 */     checkURL(url);
/*      */     try
/*      */     {
/* 1865 */       name = getUniqueName(name);
/*      */       
/* 1867 */       Engine engine = MetaSearchManagerFactory.getSingleton().getMetaSearch().createRSSEngine(name, url);
/*      */       
/* 1869 */       String json = SubscriptionImpl.getSkeletonJSON(engine, check_interval_mins);
/*      */       
/*      */ 
/*      */ 
/* 1873 */       SubscriptionImpl subs = new SubscriptionImpl(this, engine.getName(), engine.isPublic(), is_anonymous, null, json, 1);
/*      */       
/* 1875 */       if (user_data != null)
/*      */       {
/* 1877 */         Iterator it = user_data.entrySet().iterator();
/*      */         
/* 1879 */         while (it.hasNext())
/*      */         {
/* 1881 */           Map.Entry entry = (Map.Entry)it.next();
/*      */           
/* 1883 */           subs.setUserData(entry.getKey(), entry.getValue());
/*      */         }
/*      */       }
/*      */       
/* 1887 */       log("Created new subscription: " + subs.getString());
/*      */       
/* 1889 */       subs = addSubscription(subs);
/*      */       
/* 1891 */       if (subs.isPublic())
/*      */       {
/* 1893 */         updatePublicSubscription(subs);
/*      */       }
/*      */       
/* 1896 */       return subs;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1900 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Failed to create subscription", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkURL(URL url)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 1910 */     if (url.getHost().trim().length() == 0)
/*      */     {
/* 1912 */       String protocol = url.getProtocol().toLowerCase();
/*      */       
/* 1914 */       if ((!protocol.equals("azplug")) && (!protocol.equals("file")) && (!protocol.equals("vuze")))
/*      */       {
/* 1916 */         throw new com.aelitis.azureus.core.subs.SubscriptionException("Invalid URL '" + url + "'");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected SubscriptionImpl addSubscription(SubscriptionImpl subs)
/*      */   {
/*      */     SubscriptionImpl existing;
/*      */     
/* 1927 */     synchronized (this)
/*      */     {
/* 1929 */       int index = Collections.binarySearch(this.subscriptions, subs, new Comparator() {
/*      */         public int compare(Subscription arg0, Subscription arg1) {
/* 1931 */           return arg0.getID().compareTo(arg1.getID());
/*      */         }
/*      */       });
/* 1934 */       if (index < 0) {
/* 1935 */         SubscriptionImpl existing = null;
/* 1936 */         index = -1 * index - 1;
/*      */         
/* 1938 */         this.subscriptions.add(index, subs);
/*      */         
/* 1940 */         saveConfig();
/*      */       } else {
/* 1942 */         existing = (SubscriptionImpl)this.subscriptions.get(index);
/*      */       }
/*      */     }
/*      */     
/* 1946 */     if (existing != null)
/*      */     {
/* 1948 */       log("Attempted to add subscription when already present: " + subs.getString());
/*      */       
/* 1950 */       subs.destroy();
/*      */       
/* 1952 */       return existing;
/*      */     }
/*      */     
/* 1955 */     if (subs.isMine())
/*      */     {
/* 1957 */       addMetaSearchListener();
/*      */     }
/*      */     
/* 1960 */     if (subs.getCachedPopularity() == -1L) {
/*      */       try
/*      */       {
/* 1963 */         subs.getPopularity(new SubscriptionPopularityListener()
/*      */         {
/*      */           public void gotPopularity(long popularity) {}
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
/*      */           public void failed(com.aelitis.azureus.core.subs.SubscriptionException error) {}
/*      */         });
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1981 */         log("", e);
/*      */       }
/*      */     }
/*      */     
/* 1985 */     Iterator it = this.listeners.iterator();
/*      */     
/* 1987 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 1990 */         ((SubscriptionManagerListener)it.next()).subscriptionAdded(subs);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1994 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1998 */     if ((subs.isSubscribed()) && (subs.isPublic()))
/*      */     {
/* 2000 */       setSelected(subs);
/*      */     }
/*      */     
/* 2003 */     if (this.dht_plugin_public != null)
/*      */     {
/* 2005 */       new AEThread2("Publish check", true)
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/* 2010 */           SubscriptionManagerImpl.this.publishSubscriptions();
/*      */         }
/*      */       }.start();
/*      */     }
/*      */     
/* 2015 */     return subs;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void addMetaSearchListener()
/*      */   {
/* 2021 */     synchronized (this)
/*      */     {
/* 2023 */       if (this.meta_search_listener_added)
/*      */       {
/* 2025 */         return;
/*      */       }
/*      */       
/* 2028 */       this.meta_search_listener_added = true;
/*      */     }
/*      */     
/* 2031 */     MetaSearchManagerFactory.getSingleton().getMetaSearch().addListener(new com.aelitis.azureus.core.metasearch.MetaSearchListener()
/*      */     {
/*      */       public void engineAdded(Engine engine) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void engineUpdated(Engine engine)
/*      */       {
/* 2044 */         synchronized (SubscriptionManagerImpl.this)
/*      */         {
/* 2046 */           for (int i = 0; i < SubscriptionManagerImpl.this.subscriptions.size(); i++)
/*      */           {
/* 2048 */             SubscriptionImpl subs = (SubscriptionImpl)SubscriptionManagerImpl.this.subscriptions.get(i);
/*      */             
/* 2050 */             if (subs.isMine())
/*      */             {
/* 2052 */               subs.engineUpdated(engine);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void engineRemoved(Engine engine) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void engineStateChanged(Engine engine) {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void changeSubscription(SubscriptionImpl subs)
/*      */   {
/* 2076 */     if (!subs.isRemoved())
/*      */     {
/* 2078 */       Iterator it = this.listeners.iterator();
/*      */       
/* 2080 */       while (it.hasNext()) {
/*      */         try
/*      */         {
/* 2083 */           ((SubscriptionManagerListener)it.next()).subscriptionChanged(subs);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2087 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void selectSubscription(SubscriptionImpl subs)
/*      */   {
/* 2097 */     if (!subs.isRemoved())
/*      */     {
/* 2099 */       Iterator it = this.listeners.iterator();
/*      */       
/* 2101 */       while (it.hasNext()) {
/*      */         try
/*      */         {
/* 2104 */           ((SubscriptionManagerListener)it.next()).subscriptionSelected(subs);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2108 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeSubscription(SubscriptionImpl subs)
/*      */   {
/* 2118 */     synchronized (this)
/*      */     {
/* 2120 */       if (this.subscriptions.remove(subs))
/*      */       {
/* 2122 */         saveConfig();
/*      */       }
/*      */       else
/*      */       {
/* 2126 */         return;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 2131 */       Engine engine = subs.getEngine(true);
/*      */       
/* 2133 */       if (engine.getType() == 4)
/*      */       {
/* 2135 */         engine.delete();
/*      */         
/* 2137 */         log("Removed engine " + engine.getName() + " due to subscription removal");
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2142 */       log("Failed to check for engine deletion", e);
/*      */     }
/*      */     
/* 2145 */     Iterator<SubscriptionManagerListener> it = this.listeners.iterator();
/*      */     
/* 2147 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 2150 */         ((SubscriptionManagerListener)it.next()).subscriptionRemoved(subs);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2154 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 2159 */       FileUtil.deleteResilientFile(getResultsFile(subs));
/*      */       
/* 2161 */       synchronized (this.result_cache)
/*      */       {
/* 2163 */         this.result_cache.remove(subs);
/*      */       }
/*      */       
/* 2166 */       File vuze_file = getVuzeFile(subs);
/*      */       
/* 2168 */       vuze_file.delete();
/*      */       
/* 2170 */       new File(vuze_file.getParent(), vuze_file.getName() + ".bak").delete();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2174 */       log("Failed to delete results/vuze file", e);
/*      */     }
/*      */   }
/*      */   
/* 2178 */   private AsyncDispatcher async_dispatcher = new AsyncDispatcher("SubsManDispatcher");
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updatePublicSubscription(final SubscriptionImpl subs)
/*      */   {
/* 2184 */     if ((subs.isSingleton()) && ((!subs.isMine()) || (!subs.isSearchTemplate())))
/*      */     {
/*      */ 
/*      */ 
/* 2188 */       subs.setServerPublished();
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/* 2194 */       final AESemaphore sem = new AESemaphore("pub:async");
/*      */       
/* 2196 */       this.async_dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/* 2204 */             Long l_last_pub = (Long)subs.getUserData(SubscriptionManagerImpl.SP_LAST_ATTEMPTED);
/* 2205 */             Long l_consec_fail = (Long)subs.getUserData(SubscriptionManagerImpl.SP_CONSEC_FAIL);
/*      */             
/* 2207 */             if ((l_last_pub != null) && (l_consec_fail != null))
/*      */             {
/* 2209 */               long delay = 600000L;
/*      */               
/* 2211 */               for (int i = 0; i < l_consec_fail.longValue(); i++)
/*      */               {
/* 2213 */                 delay <<= 1;
/*      */                 
/* 2215 */                 if (delay > 86400000L) {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/* 2221 */               if (l_last_pub.longValue() + delay > SystemTime.getMonotonousTime()) {
/*      */                 return;
/*      */               }
/*      */             }
/*      */             
/*      */             try
/*      */             {
/* 2228 */               File vf = SubscriptionManagerImpl.this.getVuzeFile(subs);
/*      */               
/* 2230 */               byte[] bytes = FileUtil.readFileAsByteArray(vf);
/*      */               
/* 2232 */               byte[] encoded_subs = Base64.encode(bytes);
/*      */               
/* 2234 */               PlatformSubscriptionsMessenger.updateSubscription(!subs.getServerPublished(), subs.getName(false), subs.getPublicKey(), subs.getPrivateKey(), subs.getShortID(), subs.getVersion(), subs.isAnonymous(), new String(encoded_subs));
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2244 */               subs.setUserData(SubscriptionManagerImpl.SP_LAST_ATTEMPTED, null);
/* 2245 */               subs.setUserData(SubscriptionManagerImpl.SP_CONSEC_FAIL, null);
/*      */               
/* 2247 */               subs.setServerPublished();
/*      */               
/* 2249 */               SubscriptionManagerImpl.this.log("    Updated public subscription " + subs.getString());
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2253 */               SubscriptionManagerImpl.this.log("    Failed to update public subscription " + subs.getString(), e);
/*      */               
/* 2255 */               subs.setUserData(SubscriptionManagerImpl.SP_LAST_ATTEMPTED, new Long(SystemTime.getMonotonousTime()));
/*      */               
/* 2257 */               subs.setUserData(SubscriptionManagerImpl.SP_CONSEC_FAIL, new Long(l_consec_fail == null ? 1L : l_consec_fail.longValue() + 1L));
/*      */               
/* 2259 */               subs.setServerPublicationOutstanding();
/*      */             }
/*      */           }
/*      */           finally {
/* 2263 */             sem.release();
/*      */           }
/*      */           
/*      */         }
/* 2267 */       });
/* 2268 */       sem.reserve(5000L);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkSingletonPublish(SubscriptionImpl subs)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 2278 */     if (subs.getSingletonPublishAttempted())
/*      */     {
/* 2280 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Singleton publish already attempted");
/*      */     }
/*      */     
/* 2283 */     subs.setSingletonPublishAttempted();
/*      */     try
/*      */     {
/* 2286 */       File vf = getVuzeFile(subs);
/*      */       
/* 2288 */       byte[] bytes = FileUtil.readFileAsByteArray(vf);
/*      */       
/* 2290 */       byte[] encoded_subs = Base64.encode(bytes);
/*      */       
/*      */ 
/*      */ 
/* 2294 */       KeyPair kp = CryptoECCUtils.createKeys();
/*      */       
/* 2296 */       byte[] public_key = CryptoECCUtils.keyToRawdata(kp.getPublic());
/* 2297 */       byte[] private_key = CryptoECCUtils.keyToRawdata(kp.getPrivate());
/*      */       
/* 2299 */       PlatformSubscriptionsMessenger.updateSubscription(true, subs.getName(false), public_key, private_key, subs.getShortID(), 1, subs.isAnonymous(), new String(encoded_subs));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2309 */       log("    created singleton public subscription " + subs.getString());
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2313 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Failed to publish singleton", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkServerPublications(List subs)
/*      */   {
/* 2321 */     for (int i = 0; i < subs.size(); i++)
/*      */     {
/* 2323 */       SubscriptionImpl sub = (SubscriptionImpl)subs.get(i);
/*      */       
/* 2325 */       if (sub.getServerPublicationOutstanding())
/*      */       {
/* 2327 */         updatePublicSubscription(sub);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/* 2332 */   private static final Object SUBS_CHAT_KEY = new Object();
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkStuff(int ticks)
/*      */   {
/* 2338 */     long now = SystemTime.getCurrentTime();
/*      */     
/*      */     List<SubscriptionImpl> subs;
/*      */     
/* 2342 */     synchronized (this)
/*      */     {
/* 2344 */       subs = new ArrayList(this.subscriptions);
/*      */     }
/*      */     
/* 2347 */     SubscriptionImpl expired_subs = null;
/*      */     
/* 2349 */     for (int i = 0; i < subs.size(); i++)
/*      */     {
/* 2351 */       SubscriptionImpl sub = (SubscriptionImpl)subs.get(i);
/*      */       
/* 2353 */       if ((!sub.isMine()) && (!sub.isSubscribed()))
/*      */       {
/* 2355 */         long age = now - sub.getAddTime();
/*      */         
/* 2357 */         if (age > 1209600000L)
/*      */         {
/* 2359 */           if ((expired_subs != null) && (sub.getAddTime() >= expired_subs.getAddTime())) {
/*      */             continue;
/*      */           }
/* 2362 */           expired_subs = sub; continue;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2370 */       sub.checkPublish();
/*      */     }
/*      */     
/* 2373 */     if (expired_subs != null)
/*      */     {
/* 2375 */       log("Removing unsubscribed subscription '" + expired_subs.getName() + "' as expired");
/*      */       
/* 2377 */       expired_subs.remove();
/*      */     }
/*      */     long mono_now;
/* 2380 */     if (ticks % 6 == 0)
/*      */     {
/* 2382 */       Object subs_copy = new ArrayList(subs);
/*      */       
/* 2384 */       Collections.shuffle((List)subs_copy);
/*      */       
/* 2386 */       mono_now = SystemTime.getMonotonousTime();
/*      */       
/* 2388 */       for (final SubscriptionImpl sub : (List)subs_copy)
/*      */       {
/* 2390 */         if ((sub.isSubscribed()) && 
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2395 */           (!sub.isSearchTemplate()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2400 */           Long data = (Long)sub.getUserData(SUBS_CHAT_KEY);
/*      */           
/* 2402 */           if ((data == null) || (
/*      */           
/* 2404 */             (data.longValue() >= 0L) && (mono_now - data.longValue() >= 14400000L)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2410 */             String chat_key = SubscriptionUtils.getSubscriptionChatKey(sub);
/*      */             
/* 2412 */             if (chat_key != null)
/*      */             {
/* 2414 */               sub.setUserData(SUBS_CHAT_KEY, Long.valueOf(-1L));
/*      */               
/* 2416 */               SubscriptionUtils.peekChatAsync(sub.isAnonymous() ? "I2P" : "Public", chat_key, new Runnable()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/*      */ 
/* 2424 */                   sub.setUserData(SubscriptionManagerImpl.SUBS_CHAT_KEY, Long.valueOf(SystemTime.getMonotonousTime()));
/*      */ 
/*      */                 }
/*      */                 
/*      */ 
/* 2429 */               });
/* 2430 */               break;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2436 */             sub.setUserData(SUBS_CHAT_KEY, Long.valueOf(-2L));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2441 */     if (ticks % 10 == 0)
/*      */     {
/* 2443 */       lookupAssociations(false);
/*      */     }
/*      */     
/* 2446 */     if (ticks % 10 == 0)
/*      */     {
/* 2448 */       int rem = getPublishRemainingCount();
/*      */       
/* 2450 */       if (rem == 0)
/*      */       {
/* 2452 */         log("No associations to publish");
/*      */       }
/*      */       else
/*      */       {
/* 2456 */         log(rem + " associations remaining to publish");
/*      */         
/* 2458 */         publishAssociations();
/*      */       }
/*      */     }
/*      */     
/* 2462 */     if (ticks % 20 == 0)
/*      */     {
/* 2464 */       checkServerPublications(subs);
/*      */     }
/*      */     
/* 2467 */     if (ticks % 60 == 0)
/*      */     {
/* 2469 */       tidyPotentialAssociations();
/*      */     }
/*      */     
/* 2472 */     if ((ticks == 6) || (ticks % 2760 == 0))
/*      */     {
/*      */ 
/* 2475 */       setSelected(subs);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Subscription importSubscription(int type, Map map, boolean warn_user)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 2487 */     boolean log_errors = true;
/*      */     
/*      */     try
/*      */     {
/* 2491 */       if (type == 32)
/*      */       {
/* 2493 */         String name = new String((byte[])map.get("name"), "UTF-8");
/*      */         
/* 2495 */         URL url = new URL(new String((byte[])map.get("url"), "UTF-8"));
/*      */         
/* 2497 */         Long l_interval = (Long)map.get("check_interval_mins");
/*      */         
/* 2499 */         int check_interval_mins = l_interval == null ? 120 : l_interval.intValue();
/*      */         
/* 2501 */         Long l_public = (Long)map.get("public");
/*      */         
/* 2503 */         boolean is_public = l_public == null;
/*      */         
/* 2505 */         Long l_anon = (Long)map.get("anon");
/*      */         
/* 2507 */         boolean is_anon = l_anon != null;
/*      */         
/* 2509 */         SubscriptionImpl existing = lookupSingletonRSS(name, url, is_public, check_interval_mins, is_anon);
/*      */         
/* 2511 */         if (com.aelitis.azureus.util.UrlFilter.getInstance().urlCanRPC(url.toExternalForm()))
/*      */         {
/* 2513 */           warn_user = false;
/*      */         }
/*      */         
/* 2516 */         if ((existing != null) && (existing.isSubscribed()))
/*      */         {
/* 2518 */           if (warn_user)
/*      */           {
/* 2520 */             UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */             
/* 2522 */             String details = MessageText.getString("subscript.add.dup.desc", new String[] { existing.getName() });
/*      */             
/*      */ 
/*      */ 
/* 2526 */             ui_manager.showMessageBox("subscript.add.dup.title", "!" + details + "!", 1L);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 2532 */           selectSubscription(existing);
/*      */           
/* 2534 */           return existing;
/*      */         }
/*      */         
/*      */ 
/* 2538 */         if (warn_user)
/*      */         {
/* 2540 */           UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */           
/* 2542 */           String details = MessageText.getString("subscript.add.desc", new String[] { name });
/*      */           
/*      */ 
/*      */ 
/* 2546 */           long res = ui_manager.showMessageBox("subscript.add.title", "!" + details + "!", 12L);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 2551 */           if (res != 4L)
/*      */           {
/* 2553 */             log_errors = false;
/*      */             
/* 2555 */             throw new com.aelitis.azureus.core.subs.SubscriptionException("User declined addition");
/*      */           }
/*      */         }
/*      */         
/* 2559 */         if (existing == null)
/*      */         {
/* 2561 */           SubscriptionImpl new_subs = (SubscriptionImpl)createSingletonRSSSupport(name, url, is_public, check_interval_mins, is_anon, 2, true);
/*      */           
/* 2563 */           log("Imported new singleton subscription: " + new_subs.getString());
/*      */           
/* 2565 */           return new_subs;
/*      */         }
/*      */         
/*      */ 
/* 2569 */         existing.setSubscribed(true);
/*      */         
/* 2571 */         selectSubscription(existing);
/*      */         
/* 2573 */         return existing;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2578 */       SubscriptionBodyImpl body = new SubscriptionBodyImpl(this, map);
/*      */       
/* 2580 */       SubscriptionImpl existing = getSubscriptionFromSID(body.getShortID());
/*      */       
/* 2582 */       if ((existing != null) && (existing.isSubscribed()))
/*      */       {
/* 2584 */         if (existing.getVersion() >= body.getVersion())
/*      */         {
/* 2586 */           log("Not upgrading subscription: " + existing.getString() + " as supplied (" + body.getVersion() + ") is not more recent than existing (" + existing.getVersion() + ")");
/*      */           
/* 2588 */           if (warn_user)
/*      */           {
/* 2590 */             UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */             
/* 2592 */             String details = MessageText.getString("subscript.add.dup.desc", new String[] { existing.getName() });
/*      */             
/*      */ 
/*      */ 
/* 2596 */             ui_manager.showMessageBox("subscript.add.dup.title", "!" + details + "!", 1L);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2603 */           selectSubscription(existing);
/*      */           
/* 2605 */           return existing;
/*      */         }
/*      */         
/*      */ 
/* 2609 */         if (warn_user)
/*      */         {
/* 2611 */           UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */           
/* 2613 */           String details = MessageText.getString("subscript.add.upgrade.desc", new String[] { existing.getName() });
/*      */           
/*      */ 
/*      */ 
/* 2617 */           long res = ui_manager.showMessageBox("subscript.add.upgrade.title", "!" + details + "!", 12L);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 2622 */           if (res != 4L)
/*      */           {
/* 2624 */             throw new com.aelitis.azureus.core.subs.SubscriptionException("User declined upgrade");
/*      */           }
/*      */         }
/*      */         
/* 2628 */         log("Upgrading subscription: " + existing.getString());
/*      */         
/* 2630 */         existing.upgrade(body);
/*      */         
/* 2632 */         saveConfig();
/*      */         
/* 2634 */         subscriptionUpdated();
/*      */         
/* 2636 */         return existing;
/*      */       }
/*      */       
/*      */ 
/* 2640 */       SubscriptionImpl new_subs = null;
/*      */       
/*      */       String subs_name;
/*      */       String subs_name;
/* 2644 */       if (existing == null)
/*      */       {
/* 2646 */         new_subs = new SubscriptionImpl(this, body, 2, true);
/*      */         
/* 2648 */         subs_name = new_subs.getName();
/*      */       }
/*      */       else
/*      */       {
/* 2652 */         subs_name = existing.getName();
/*      */       }
/*      */       
/* 2655 */       if (warn_user)
/*      */       {
/* 2657 */         UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */         
/* 2659 */         String details = MessageText.getString("subscript.add.desc", new String[] { subs_name });
/*      */         
/*      */ 
/*      */ 
/* 2663 */         long res = ui_manager.showMessageBox("subscript.add.title", "!" + details + "!", 12L);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2668 */         if (res != 4L)
/*      */         {
/* 2670 */           throw new com.aelitis.azureus.core.subs.SubscriptionException("User declined addition");
/*      */         }
/*      */       }
/*      */       
/* 2674 */       if (new_subs == null)
/*      */       {
/* 2676 */         existing.setSubscribed(true);
/*      */         
/* 2678 */         selectSubscription(existing);
/*      */         
/* 2680 */         return existing;
/*      */       }
/*      */       
/*      */ 
/* 2684 */       log("Imported new subscription: " + new_subs.getString());
/*      */       
/* 2686 */       return addSubscription(new_subs);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/* 2694 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Subscription import failed", e);
/*      */     }
/*      */     catch (com.aelitis.azureus.core.subs.SubscriptionException e)
/*      */     {
/* 2698 */       if ((warn_user) && (log_errors))
/*      */       {
/* 2700 */         UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */         
/* 2702 */         String details = MessageText.getString("subscript.import.fail.desc", new String[] { Debug.getNestedExceptionMessage(e) });
/*      */         
/*      */ 
/*      */ 
/* 2706 */         ui_manager.showMessageBox("subscript.import.fail.title", "!" + details + "!", 1L);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2712 */       throw e;
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public Subscription[] getSubscriptions(boolean subscribed_only)
/*      */   {
/* 2729 */     if (!subscribed_only)
/*      */     {
/* 2731 */       return getSubscriptions();
/*      */     }
/*      */     
/* 2734 */     List result = new ArrayList();
/*      */     
/* 2736 */     synchronized (this)
/*      */     {
/* 2738 */       for (int i = 0; i < this.subscriptions.size(); i++)
/*      */       {
/* 2740 */         SubscriptionImpl subs = (SubscriptionImpl)this.subscriptions.get(i);
/*      */         
/* 2742 */         if (subs.isSubscribed())
/*      */         {
/* 2744 */           result.add(subs);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2749 */     return (SubscriptionImpl[])result.toArray(new SubscriptionImpl[result.size()]);
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
/*      */   protected SubscriptionImpl getSubscriptionFromName(String name)
/*      */   {
/* 2786 */     synchronized (this)
/*      */     {
/* 2788 */       for (int i = 0; i < this.subscriptions.size(); i++)
/*      */       {
/* 2790 */         SubscriptionImpl s = (SubscriptionImpl)this.subscriptions.get(i);
/*      */         
/* 2792 */         if (s.getName().equalsIgnoreCase(name))
/*      */         {
/* 2794 */           return s;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2799 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Subscription getSubscriptionByID(String id)
/*      */   {
/* 2806 */     synchronized (this)
/*      */     {
/* 2808 */       int index = Collections.binarySearch(this.subscriptions, id, new Comparator() {
/*      */         public int compare(Object o1, Object o2) {
/* 2810 */           String id1 = (o1 instanceof Subscription) ? ((Subscription)o1).getID() : o1.toString();
/* 2811 */           String id2 = (o2 instanceof Subscription) ? ((Subscription)o2).getID() : o2.toString();
/* 2812 */           return id1.compareTo(id2);
/*      */         }
/*      */       });
/*      */       
/* 2816 */       if (index >= 0) {
/* 2817 */         return (Subscription)this.subscriptions.get(index);
/*      */       }
/*      */     }
/*      */     
/* 2821 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected SubscriptionImpl getSubscriptionFromSID(byte[] sid)
/*      */   {
/* 2828 */     return (SubscriptionImpl)getSubscriptionByID(Base32.encode(sid));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected File getSubsDir()
/*      */     throws IOException
/*      */   {
/* 2836 */     File dir = new File(org.gudy.azureus2.core3.util.SystemProperties.getUserPath());
/*      */     
/* 2838 */     dir = new File(dir, "subs");
/*      */     
/* 2840 */     if (!dir.exists())
/*      */     {
/* 2842 */       if (!dir.mkdirs())
/*      */       {
/* 2844 */         throw new IOException("Failed to create '" + dir + "'");
/*      */       }
/*      */     }
/*      */     
/* 2848 */     return dir;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected File getVuzeFile(SubscriptionImpl subs)
/*      */     throws IOException
/*      */   {
/* 2857 */     File dir = getSubsDir();
/*      */     
/* 2859 */     return new File(dir, ByteFormatter.encodeString(subs.getShortID()) + ".vuze");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected File getResultsFile(SubscriptionImpl subs)
/*      */     throws IOException
/*      */   {
/* 2868 */     File dir = getSubsDir();
/*      */     
/* 2870 */     return new File(dir, ByteFormatter.encodeString(subs.getShortID()) + ".results");
/*      */   }
/*      */   
/*      */ 
/*      */   public int getKnownSubscriptionCount()
/*      */   {
/* 2876 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*      */     
/* 2878 */     Download[] downloads = pi.getDownloadManager().getDownloads();
/*      */     
/* 2880 */     ByteArrayHashMap<String> results = new ByteArrayHashMap(Math.max(16, downloads.length * 2));
/*      */     try
/*      */     {
/* 2883 */       for (Download download : downloads)
/*      */       {
/* 2885 */         Map m = download.getMapAttribute(this.ta_subscription_info);
/*      */         
/* 2887 */         if (m != null)
/*      */         {
/* 2889 */           List s = (List)m.get("s");
/*      */           
/* 2891 */           if ((s != null) && (s.size() > 0))
/*      */           {
/* 2893 */             for (int i = 0; i < s.size(); i++)
/*      */             {
/* 2895 */               byte[] sid = (byte[])s.get(i);
/*      */               
/* 2897 */               results.put(sid, "");
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 2904 */       log("Failed to get known subscriptions", e);
/*      */     }
/*      */     
/* 2907 */     return results.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Subscription[] getKnownSubscriptions(byte[] hash)
/*      */   {
/* 2914 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*      */     try
/*      */     {
/* 2917 */       Download download = pi.getDownloadManager().getDownload(hash);
/*      */       
/* 2919 */       if (download != null)
/*      */       {
/* 2921 */         Map m = download.getMapAttribute(this.ta_subscription_info);
/*      */         
/* 2923 */         if (m != null)
/*      */         {
/* 2925 */           List s = (List)m.get("s");
/*      */           
/* 2927 */           if ((s != null) && (s.size() > 0))
/*      */           {
/* 2929 */             List result = new ArrayList(s.size());
/*      */             
/* 2931 */             boolean hide_search = hideSearchTemplates();
/*      */             
/* 2933 */             for (int i = 0; i < s.size(); i++)
/*      */             {
/* 2935 */               byte[] sid = (byte[])s.get(i);
/*      */               
/* 2937 */               SubscriptionImpl subs = getSubscriptionFromSID(sid);
/*      */               
/* 2939 */               if (subs != null)
/*      */               {
/* 2941 */                 if (isVisible(subs))
/*      */                 {
/* 2943 */                   if ((!hide_search) || (!subs.isSearchTemplate()))
/*      */                   {
/*      */ 
/*      */ 
/* 2947 */                     result.add(subs);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 2953 */             return (Subscription[])result.toArray(new Subscription[result.size()]);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 2959 */       log("Failed to get known subscriptions", e);
/*      */     }
/*      */     
/* 2962 */     return new Subscription[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean subscriptionExists(Download download, SubscriptionImpl subs)
/*      */   {
/* 2970 */     byte[] sid = subs.getShortID();
/*      */     
/* 2972 */     Map m = download.getMapAttribute(this.ta_subscription_info);
/*      */     
/* 2974 */     if (m != null)
/*      */     {
/* 2976 */       List s = (List)m.get("s");
/*      */       
/* 2978 */       if ((s != null) && (s.size() > 0))
/*      */       {
/* 2980 */         for (int i = 0; i < s.size(); i++)
/*      */         {
/* 2982 */           byte[] x = (byte[])s.get(i);
/*      */           
/* 2984 */           if (Arrays.equals(x, sid))
/*      */           {
/* 2986 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2992 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean downloadIsIgnored(Download download)
/*      */   {
/* 2999 */     if ((download.getTorrent() == null) || (!download.isPersistent()))
/*      */     {
/* 3001 */       return true;
/*      */     }
/*      */     
/* 3004 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isVisible(SubscriptionImpl subs)
/*      */   {
/* 3013 */     if ((org.gudy.azureus2.core3.util.Constants.isCVSVersion()) || (subs.isSubscribed()))
/*      */     {
/* 3015 */       return true;
/*      */     }
/*      */     try
/*      */     {
/* 3019 */       Engine engine = subs.getEngine(true);
/*      */       
/* 3021 */       if ((engine instanceof com.aelitis.azureus.core.metasearch.impl.web.WebEngine))
/*      */       {
/* 3023 */         String url = ((com.aelitis.azureus.core.metasearch.impl.web.WebEngine)engine).getSearchUrl();
/*      */         try
/*      */         {
/* 3026 */           String host = new URL(url).getHost();
/*      */           
/* 3028 */           return !this.exclusion_pattern.matcher(host).matches();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/* 3034 */       return true;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3038 */       log("isVisible failed for " + subs.getString(), e);
/*      */     }
/* 3040 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Subscription[] getLinkedSubscriptions(byte[] hash)
/*      */   {
/* 3048 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*      */     try
/*      */     {
/* 3051 */       Download download = pi.getDownloadManager().getDownload(hash);
/*      */       
/* 3053 */       if (download != null)
/*      */       {
/* 3055 */         Map m = download.getMapAttribute(this.ta_subscription_info);
/*      */         
/* 3057 */         if (m != null)
/*      */         {
/* 3059 */           List s = (List)m.get("s");
/*      */           
/* 3061 */           if ((s != null) && (s.size() > 0))
/*      */           {
/* 3063 */             List result = new ArrayList(s.size());
/*      */             
/* 3065 */             for (int i = 0; i < s.size(); i++)
/*      */             {
/* 3067 */               byte[] sid = (byte[])s.get(i);
/*      */               
/* 3069 */               SubscriptionImpl subs = getSubscriptionFromSID(sid);
/*      */               
/* 3071 */               if (subs != null)
/*      */               {
/* 3073 */                 if (subs.hasAssociation(hash))
/*      */                 {
/* 3075 */                   result.add(subs);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 3080 */             return (Subscription[])result.toArray(new Subscription[result.size()]);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 3086 */       log("Failed to get known subscriptions", e);
/*      */     }
/*      */     
/* 3089 */     return new Subscription[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void lookupAssociations(boolean high_priority)
/*      */   {
/* 3096 */     synchronized (this)
/*      */     {
/* 3098 */       if (this.periodic_lookup_in_progress)
/*      */       {
/* 3100 */         if (high_priority)
/*      */         {
/* 3102 */           this.priority_lookup_pending += 1;
/*      */         }
/*      */         
/* 3105 */         return;
/*      */       }
/*      */       
/* 3108 */       this.periodic_lookup_in_progress = true;
/*      */     }
/*      */     try
/*      */     {
/* 3112 */       PluginInterface pi = PluginInitializer.getDefaultInterface();
/*      */       
/* 3114 */       Download[] downloads = pi.getDownloadManager().getDownloads();
/*      */       
/* 3116 */       long now = SystemTime.getCurrentTime();
/*      */       
/* 3118 */       long newest_time = 0L;
/* 3119 */       Download newest_download = null;
/*      */       
/*      */ 
/* 3122 */       for (int i = 0; i < downloads.length; i++)
/*      */       {
/* 3124 */         Download download = downloads[i];
/*      */         
/* 3126 */         if (!downloadIsIgnored(download))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 3131 */           Map map = download.getMapAttribute(this.ta_subscription_info);
/*      */           
/* 3133 */           if (map == null)
/*      */           {
/* 3135 */             map = new LightHashMap();
/*      */           }
/*      */           else
/*      */           {
/* 3139 */             map = new LightHashMap(map);
/*      */           }
/*      */           
/* 3142 */           Long l_last_check = (Long)map.get("lc");
/*      */           
/* 3144 */           long last_check = l_last_check == null ? 0L : l_last_check.longValue();
/*      */           
/* 3146 */           if (last_check > now)
/*      */           {
/* 3148 */             last_check = now;
/*      */             
/* 3150 */             map.put("lc", new Long(last_check));
/*      */             
/* 3152 */             download.setMapAttribute(this.ta_subscription_info, map);
/*      */           }
/*      */           
/* 3155 */           List subs = (List)map.get("s");
/*      */           
/* 3157 */           int sub_count = subs == null ? 0 : subs.size();
/*      */           
/* 3159 */           if (sub_count <= 8)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 3164 */             long create_time = download.getCreationTime();
/*      */             
/* 3166 */             int time_between_checks = (sub_count + 1) * 24 * 60 * 60 * 1000 + (int)(create_time % 4L * 60L * 60L * 1000L);
/*      */             
/* 3168 */             if (now - last_check >= time_between_checks)
/*      */             {
/* 3170 */               if (create_time > newest_time)
/*      */               {
/* 3172 */                 newest_time = create_time;
/* 3173 */                 newest_download = download;
/*      */               } }
/*      */           }
/*      */         }
/*      */       }
/* 3178 */       if (newest_download != null)
/*      */       {
/* 3180 */         DHTPluginInterface dht_plugin = selectDHTPlugin(newest_download);
/*      */         
/* 3182 */         if (dht_plugin != null)
/*      */         {
/* 3184 */           byte[] hash = newest_download.getTorrent().getHash();
/*      */           
/* 3186 */           log("Association lookup starts for " + newest_download.getName() + "/" + ByteFormatter.encodeString(hash));
/*      */           
/* 3188 */           lookupAssociationsSupport(dht_plugin, hash, new SubscriptionLookupListener()
/*      */           {
/*      */             public void found(byte[] hash, Subscription subscription) {}
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
/*      */             public void failed(byte[] hash, com.aelitis.azureus.core.subs.SubscriptionException error)
/*      */             {
/* 3205 */               SubscriptionManagerImpl.this.log("Association lookup failed for " + ByteFormatter.encodeString(hash), error);
/*      */               
/* 3207 */               SubscriptionManagerImpl.this.associationLookupComplete();
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void complete(byte[] hash, Subscription[] subs)
/*      */             {
/* 3215 */               SubscriptionManagerImpl.this.log("Association lookup complete for " + ByteFormatter.encodeString(hash));
/*      */               
/* 3217 */               SubscriptionManagerImpl.this.associationLookupComplete();
/*      */             }
/*      */           });
/*      */         }
/*      */         else {
/* 3222 */           associationLookupComplete();
/*      */         }
/*      */       }
/*      */       else {
/* 3226 */         associationLookupComplete();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 3230 */       log("Association lookup check failed", e);
/*      */       
/* 3232 */       associationLookupComplete();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void associationLookupComplete()
/*      */   {
/*      */     boolean recheck;
/*      */     
/* 3241 */     synchronized (this)
/*      */     {
/* 3243 */       this.periodic_lookup_in_progress = false;
/*      */       
/* 3245 */       recheck = this.priority_lookup_pending > 0;
/*      */       
/* 3247 */       if (recheck)
/*      */       {
/* 3249 */         this.priority_lookup_pending -= 1;
/*      */       }
/*      */     }
/*      */     
/* 3253 */     if (recheck)
/*      */     {
/* 3255 */       new AEThread2("SM:priAssLookup", true)
/*      */       {
/*      */         public void run()
/*      */         {
/* 3259 */           SubscriptionManagerImpl.this.lookupAssociations(false);
/*      */         }
/*      */       }.start();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setSelected(List subs)
/*      */   {
/* 3269 */     List<byte[]> sids = new ArrayList();
/* 3270 */     List<SubscriptionImpl> used_subs = new ArrayList();
/*      */     
/* 3272 */     final List<SubscriptionImpl> dht_pops = new ArrayList();
/*      */     
/* 3274 */     for (int i = 0; i < subs.size(); i++)
/*      */     {
/* 3276 */       SubscriptionImpl sub = (SubscriptionImpl)subs.get(i);
/*      */       
/* 3278 */       if (sub.isSubscribed())
/*      */       {
/* 3280 */         if (sub.isPublic())
/*      */         {
/* 3282 */           if (!sub.isAnonymous())
/*      */           {
/* 3284 */             used_subs.add(sub);
/*      */             
/* 3286 */             sids.add(sub.getShortID());
/*      */           }
/*      */           else
/*      */           {
/* 3290 */             dht_pops.add(sub);
/*      */           }
/*      */         }
/*      */         else {
/* 3294 */           checkInitialDownload(sub);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3299 */     if (sids.size() > 0) {
/*      */       try
/*      */       {
/* 3302 */         List[] result = PlatformSubscriptionsMessenger.setSelected(sids);
/*      */         
/* 3304 */         List<Long> versions = result[0];
/* 3305 */         List<Long> popularities = result[1];
/*      */         
/* 3307 */         log("Popularity update: updated " + sids.size());
/*      */         
/* 3309 */         for (int i = 0; i < sids.size(); i++)
/*      */         {
/* 3311 */           SubscriptionImpl sub = (SubscriptionImpl)used_subs.get(i);
/*      */           
/* 3313 */           int latest_version = ((Long)versions.get(i)).intValue();
/*      */           
/* 3315 */           if (latest_version > sub.getVersion())
/*      */           {
/* 3317 */             updateSubscription(sub, latest_version);
/*      */           }
/*      */           else
/*      */           {
/* 3321 */             checkInitialDownload(sub);
/*      */           }
/*      */           
/* 3324 */           if (latest_version > 0) {
/*      */             try
/*      */             {
/* 3327 */               long pop = ((Long)popularities.get(i)).longValue();
/*      */               
/* 3329 */               if ((pop >= 0L) && (pop != sub.getCachedPopularity()))
/*      */               {
/* 3331 */                 sub.setCachedPopularity(pop);
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 3335 */               log("Popularity update: Failed to extract popularity", e);
/*      */             }
/*      */             
/*      */           } else {
/* 3339 */             dht_pops.add(sub);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3345 */         log("Popularity update: Failed to record selected subscriptions", e);
/*      */       }
/*      */       
/*      */     } else {
/* 3349 */       log("Popularity update: No selected, public subscriptions");
/*      */     }
/*      */     
/* 3352 */     if (dht_pops.size() <= 3)
/*      */     {
/* 3354 */       for (int i = 0; i < dht_pops.size(); i++)
/*      */       {
/* 3356 */         updatePopularityFromDHT((SubscriptionImpl)dht_pops.get(i), false);
/*      */       }
/*      */       
/*      */     } else {
/* 3360 */       new AEThread2("SM:asyncPop", true)
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/* 3365 */           for (int i = 0; i < dht_pops.size(); i++)
/*      */           {
/* 3367 */             SubscriptionManagerImpl.this.updatePopularityFromDHT((SubscriptionImpl)dht_pops.get(i), true);
/*      */           }
/*      */         }
/*      */       }.start();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkUpgrade(SubscriptionImpl sub)
/*      */   {
/* 3378 */     setSelected(sub);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setSelected(final SubscriptionImpl sub)
/*      */   {
/* 3385 */     if (sub.isSubscribed())
/*      */     {
/* 3387 */       if (sub.isPublic())
/*      */       {
/* 3389 */         new org.gudy.azureus2.core3.util.DelayedEvent("SM:setSelected", 0L, new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/* 3398 */               if (!sub.isAnonymous())
/*      */               {
/* 3400 */                 List sids = new ArrayList();
/*      */                 
/* 3402 */                 sids.add(sub.getShortID());
/*      */                 
/* 3404 */                 List[] result = PlatformSubscriptionsMessenger.setSelected(sids);
/*      */                 
/* 3406 */                 SubscriptionManagerImpl.this.log("setSelected: " + sub.getName());
/*      */                 
/* 3408 */                 int latest_version = ((Long)result[0].get(0)).intValue();
/*      */                 
/* 3410 */                 if (latest_version == 0)
/*      */                 {
/* 3412 */                   if (sub.isSingleton())
/*      */                   {
/* 3414 */                     SubscriptionManagerImpl.this.checkSingletonPublish(sub);
/*      */                   }
/* 3416 */                 } else if (latest_version > sub.getVersion())
/*      */                 {
/* 3418 */                   SubscriptionManagerImpl.this.updateSubscription(sub, latest_version);
/*      */                 }
/*      */                 
/* 3421 */                 if (latest_version > 0) {
/*      */                   try
/*      */                   {
/* 3424 */                     long pop = ((Long)result[1].get(0)).longValue();
/*      */                     
/* 3426 */                     if ((pop >= 0L) && (pop != sub.getCachedPopularity()))
/*      */                     {
/* 3428 */                       sub.setCachedPopularity(pop);
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e) {
/* 3432 */                     SubscriptionManagerImpl.this.log("Popularity update: Failed to extract popularity", e);
/*      */                   }
/*      */                   
/*      */                 } else {
/* 3436 */                   SubscriptionManagerImpl.this.updatePopularityFromDHT(sub, true);
/*      */                 }
/*      */               }
/*      */               else {
/* 3440 */                 SubscriptionManagerImpl.this.updatePopularityFromDHT(sub, true);
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 3444 */               SubscriptionManagerImpl.this.log("setSelected: failed for " + sub.getName(), e);
/*      */             }
/*      */             finally
/*      */             {
/* 3448 */               SubscriptionManagerImpl.this.checkInitialDownload(sub);
/*      */             }
/*      */             
/*      */           }
/*      */         });
/*      */       } else {
/* 3454 */         checkInitialDownload(sub);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkInitialDownload(SubscriptionImpl subs)
/*      */   {
/* 3463 */     if (subs.getHistory().getLastScanTime() == 0L)
/*      */     {
/* 3465 */       this.scheduler.download(subs, true, new com.aelitis.azureus.core.subs.SubscriptionDownloadListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void complete(Subscription subs)
/*      */         {
/*      */ 
/*      */ 
/* 3474 */           SubscriptionManagerImpl.this.log("Initial download of " + subs.getName() + " complete");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void failed(Subscription subs, com.aelitis.azureus.core.subs.SubscriptionException error)
/*      */         {
/* 3482 */           SubscriptionManagerImpl.this.log("Initial download of " + subs.getName() + " failed", error);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SubscriptionAssociationLookup lookupAssociations(byte[] hash, String[] networks, SubscriptionLookupListener listener)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 3496 */     return lookupAssociations(selectDHTPlugin(networks), hash, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SubscriptionAssociationLookup lookupAssociations(byte[] hash, SubscriptionLookupListener listener)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/*      */     DHTPluginInterface dht_plugin;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 3509 */       Download download = PluginInitializer.getDefaultInterface().getDownloadManager().getDownload(hash);
/*      */       DHTPluginInterface dht_plugin;
/* 3511 */       if (download != null)
/*      */       {
/* 3513 */         dht_plugin = selectDHTPlugin(download);
/*      */       }
/*      */       else
/*      */       {
/* 3517 */         dht_plugin = this.dht_plugin_public;
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 3521 */       dht_plugin = this.dht_plugin_public;
/*      */     }
/*      */     
/* 3524 */     return lookupAssociations(dht_plugin, hash, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private SubscriptionAssociationLookup lookupAssociations(DHTPluginInterface dht_plugin, final byte[] hash, final SubscriptionLookupListener listener)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 3535 */     if (dht_plugin != null)
/*      */     {
/* 3537 */       if (!dht_plugin.isInitialising())
/*      */       {
/* 3539 */         return lookupAssociationsSupport(dht_plugin, hash, listener);
/*      */       }
/*      */       
/* 3542 */       final boolean[] cancelled = { false };
/* 3543 */       final long[] timeout = { 0L };
/*      */       
/* 3545 */       final SubscriptionAssociationLookup[] actual_res = { null };
/*      */       
/* 3547 */       SubscriptionAssociationLookup res = new SubscriptionAssociationLookup()
/*      */       {
/*      */ 
/*      */         public void cancel()
/*      */         {
/*      */ 
/* 3553 */           SubscriptionManagerImpl.this.log("    Association lookup cancelled");
/*      */           
/* 3555 */           synchronized (actual_res)
/*      */           {
/* 3557 */             cancelled[0] = true;
/*      */             
/* 3559 */             if (actual_res[0] != null)
/*      */             {
/* 3561 */               actual_res[0].cancel();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */         public void setTimeout(long millis)
/*      */         {
/* 3568 */           synchronized (actual_res)
/*      */           {
/* 3570 */             timeout[0] = millis;
/*      */             
/* 3572 */             if (actual_res[0] != null)
/*      */             {
/* 3574 */               actual_res[0].setTimeout(millis);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/* 3579 */       };
/* 3580 */       final DHTPluginInterface f_dht_plugin = dht_plugin;
/*      */       
/* 3582 */       new AEThread2("SM:initwait", true)
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try
/*      */           {
/* 3588 */             SubscriptionAssociationLookup x = SubscriptionManagerImpl.this.lookupAssociationsSupport(f_dht_plugin, hash, listener);
/*      */             
/* 3590 */             synchronized (actual_res)
/*      */             {
/* 3592 */               actual_res[0] = x;
/*      */               
/* 3594 */               if (cancelled[0] != 0)
/*      */               {
/* 3596 */                 x.cancel();
/*      */               }
/*      */               
/* 3599 */               if (timeout[0] != 0L)
/*      */               {
/* 3601 */                 x.setTimeout(timeout[0]);
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (com.aelitis.azureus.core.subs.SubscriptionException e)
/*      */           {
/* 3607 */             listener.failed(hash, e);
/*      */           }
/*      */           
/*      */         }
/*      */         
/* 3612 */       }.start();
/* 3613 */       return res;
/*      */     }
/*      */     
/*      */ 
/* 3617 */     throw new com.aelitis.azureus.core.subs.SubscriptionException("No DHT available");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SubscriptionAssociationLookup lookupAssociationsSupport(final DHTPluginInterface dht_plugin, final byte[] hash, final SubscriptionLookupListener _listener)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 3629 */     log("Looking up associations for '" + ByteFormatter.encodeString(hash));
/*      */     
/* 3631 */     String key = "subscription:assoc:" + ByteFormatter.encodeString(hash);
/*      */     
/* 3633 */     final boolean[] cancelled = { false };
/*      */     
/* 3635 */     final com.aelitis.azureus.core.subs.SubscriptionException timeout_exception = new com.aelitis.azureus.core.subs.SubscriptionException("Timeout");
/*      */     
/* 3637 */     final SubscriptionLookupListener listener = new SubscriptionLookupListener()
/*      */     {
/*      */ 
/* 3640 */       private boolean done = false;
/*      */       
/* 3642 */       private List<Subscription> subs = new ArrayList();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void found(byte[] hash, Subscription subscription)
/*      */       {
/* 3649 */         synchronized (this) {
/* 3650 */           if (this.done) {
/* 3651 */             return;
/*      */           }
/* 3653 */           this.subs.add(subscription);
/*      */         }
/*      */         
/* 3656 */         _listener.found(hash, subscription);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(byte[] hash, Subscription[] subscriptions)
/*      */       {
/* 3664 */         synchronized (this) {
/* 3665 */           if (this.done) {
/* 3666 */             return;
/*      */           }
/*      */           
/* 3669 */           this.done = true;
/*      */         }
/*      */         
/* 3672 */         _listener.complete(hash, subscriptions);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void failed(byte[] hash, com.aelitis.azureus.core.subs.SubscriptionException error)
/*      */       {
/*      */         Subscription[] subscriptions;
/*      */         
/*      */ 
/* 3682 */         synchronized (this) {
/* 3683 */           if (this.done) {
/* 3684 */             return;
/*      */           }
/*      */           
/* 3687 */           this.done = true;
/*      */           
/* 3689 */           subscriptions = (Subscription[])this.subs.toArray(new Subscription[this.subs.size()]);
/*      */         }
/*      */         
/* 3692 */         if (error == timeout_exception)
/*      */         {
/* 3694 */           _listener.complete(hash, subscriptions);
/*      */         }
/*      */         else
/*      */         {
/* 3698 */           _listener.failed(hash, error);
/*      */         }
/*      */         
/*      */       }
/* 3702 */     };
/* 3703 */     dht_plugin.get(getKeyBytes(key), "Subs assoc read: " + Base32.encode(hash).substring(0, 16), (byte)0, 30, 60000 * (dht_plugin != this.dht_plugin_public ? 2 : 1), true, true, new DHTPluginOperationListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3713 */       private Map<HashWrapper, Integer> hits = new HashMap();
/* 3714 */       private AESemaphore hits_sem = new AESemaphore("Subs:lookup");
/* 3715 */       private List<Subscription> found_subscriptions = new ArrayList();
/*      */       
/*      */       private boolean complete;
/*      */       
/* 3719 */       private AsyncDispatcher dispatcher = new AsyncDispatcher("SubsMan:AL");
/*      */       
/*      */ 
/*      */       public boolean diversified()
/*      */       {
/* 3724 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void starts(byte[] key) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */       {
/* 3738 */         if (isCancelled2())
/*      */         {
/* 3740 */           return;
/*      */         }
/*      */         
/* 3743 */         byte[] val = value.getValue();
/*      */         
/* 3745 */         if (val.length > 4)
/*      */         {
/* 3747 */           final int ver = val[0] << 16 & 0xFF0000 | val[1] << 8 & 0xFF00 | val[2] & 0xFF;
/*      */           
/*      */ 
/*      */ 
/* 3751 */           final byte[] sid = new byte[val.length - 4];
/*      */           
/* 3753 */           System.arraycopy(val, 4, sid, 0, sid.length);
/*      */           
/* 3755 */           HashWrapper hw = new HashWrapper(sid);
/*      */           
/* 3757 */           boolean new_sid = false;
/*      */           
/* 3759 */           synchronized (this.hits)
/*      */           {
/* 3761 */             if (this.complete)
/*      */             {
/* 3763 */               return;
/*      */             }
/*      */             
/* 3766 */             Integer v = (Integer)this.hits.get(hw);
/*      */             
/* 3768 */             if (v != null)
/*      */             {
/* 3770 */               if (ver > v.intValue())
/*      */               {
/* 3772 */                 this.hits.put(hw, new Integer(ver));
/*      */               }
/*      */             }
/*      */             else {
/* 3776 */               new_sid = true;
/*      */               
/* 3778 */               this.hits.put(hw, new Integer(ver));
/*      */             }
/*      */           }
/*      */           
/* 3782 */           if (new_sid)
/*      */           {
/* 3784 */             SubscriptionManagerImpl.this.log("    Found subscription " + ByteFormatter.encodeString(sid) + " version " + ver);
/*      */             
/*      */ 
/*      */ 
/* 3788 */             SubscriptionImpl subs = SubscriptionManagerImpl.this.getSubscriptionFromSID(sid);
/*      */             
/* 3790 */             if (subs != null)
/*      */             {
/* 3792 */               synchronized (this.hits)
/*      */               {
/* 3794 */                 this.found_subscriptions.add(subs);
/*      */               }
/*      */               try
/*      */               {
/* 3798 */                 listener.found(hash, subs);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 3802 */                 Debug.printStackTrace(e);
/*      */               }
/*      */               
/* 3805 */               this.hits_sem.release();
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/* 3811 */               this.dispatcher.dispatch(new AERunnable()
/*      */               {
/*      */ 
/*      */                 public void runSupport()
/*      */                 {
/*      */ 
/* 3817 */                   boolean is_anon = SubscriptionManagerImpl.28.this.val$dht_plugin != SubscriptionManagerImpl.this.dht_plugin_public;
/*      */                   
/* 3819 */                   SubscriptionManagerImpl.this.lookupSubscription(SubscriptionManagerImpl.28.this.val$hash, sid, ver, is_anon, new SubscriptionManagerImpl.subsLookupListener()
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3826 */                     private boolean sem_done = false;
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void found(byte[] hash, Subscription subscription) {}
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void complete(byte[] hash, Subscription[] subscriptions)
/*      */                     {
/* 3840 */                       done(subscriptions);
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void failed(byte[] hash, com.aelitis.azureus.core.subs.SubscriptionException error)
/*      */                     {
/* 3848 */                       done(new Subscription[0]);
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */                     protected void done(Subscription[] subs)
/*      */                     {
/* 3855 */                       synchronized (this)
/*      */                       {
/* 3857 */                         if (this.sem_done)
/*      */                         {
/* 3859 */                           return;
/*      */                         }
/*      */                         
/* 3862 */                         this.sem_done = true;
/*      */                       }
/*      */                       try
/*      */                       {
/* 3866 */                         if (isCancelled()) {
/*      */                           return;
/*      */                         }
/*      */                         
/*      */ 
/* 3871 */                         if (subs.length > 0)
/*      */                         {
/* 3873 */                           synchronized (SubscriptionManagerImpl.28.this.hits)
/*      */                           {
/* 3875 */                             SubscriptionManagerImpl.28.this.found_subscriptions.add(subs[0]);
/*      */                           }
/*      */                           try
/*      */                           {
/* 3879 */                             SubscriptionManagerImpl.28.this.val$listener.found(SubscriptionManagerImpl.28.this.val$hash, subs[0]);
/*      */                           }
/*      */                           catch (Throwable e)
/*      */                           {
/* 3883 */                             Debug.printStackTrace(e);
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                       finally {
/* 3888 */                         SubscriptionManagerImpl.28.this.hits_sem.release();
/*      */                       }
/*      */                     }
/*      */                     
/*      */ 
/*      */                     public boolean isCancelled()
/*      */                     {
/* 3895 */                       return SubscriptionManagerImpl.28.this.isCancelled2();
/*      */                     }
/*      */                   });
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(byte[] original_key, boolean timeout_occurred)
/*      */       {
/* 3920 */         new AEThread2("SubsManAL:comp")
/*      */         {
/*      */           public void run()
/*      */           {
/*      */             int num_hits;
/*      */             
/*      */ 
/* 3927 */             synchronized (SubscriptionManagerImpl.28.this.hits)
/*      */             {
/* 3929 */               if (SubscriptionManagerImpl.28.this.complete)
/*      */               {
/* 3931 */                 return;
/*      */               }
/*      */               
/* 3934 */               SubscriptionManagerImpl.28.this.complete = true;
/*      */               
/* 3936 */               num_hits = SubscriptionManagerImpl.28.this.hits.size();
/*      */             }
/*      */             
/* 3939 */             for (int i = 0; i < num_hits; i++)
/*      */             {
/* 3941 */               if (SubscriptionManagerImpl.28.this.isCancelled2())
/*      */               {
/* 3943 */                 SubscriptionManagerImpl.28.this.val$listener.failed(SubscriptionManagerImpl.28.this.val$hash, new com.aelitis.azureus.core.subs.SubscriptionException("Cancelled"));
/*      */                 
/* 3945 */                 return;
/*      */               }
/*      */               
/* 3948 */               SubscriptionManagerImpl.28.this.hits_sem.reserve();
/*      */             }
/*      */             
/*      */             SubscriptionImpl[] s;
/*      */             
/* 3953 */             synchronized (SubscriptionManagerImpl.28.this.hits)
/*      */             {
/* 3955 */               s = (SubscriptionImpl[])SubscriptionManagerImpl.28.this.found_subscriptions.toArray(new SubscriptionImpl[SubscriptionManagerImpl.28.this.found_subscriptions.size()]);
/*      */             }
/*      */             
/* 3958 */             SubscriptionManagerImpl.this.log("    Association lookup complete - " + s.length + " found");
/*      */             
/*      */ 
/*      */             try
/*      */             {
/* 3963 */               SubscriptionManagerImpl.this.recordAssociations(SubscriptionManagerImpl.28.this.val$hash, s, true);
/*      */             }
/*      */             finally
/*      */             {
/* 3967 */               SubscriptionManagerImpl.28.this.val$listener.complete(SubscriptionManagerImpl.28.this.val$hash, s);
/*      */             }
/*      */           }
/*      */         }.start();
/*      */       }
/*      */       
/*      */       /* Error */
/*      */       protected boolean isCancelled2()
/*      */       {
/*      */         // Byte code:
/*      */         //   0: aload_0
/*      */         //   1: getfield 202	com/aelitis/azureus/core/subs/impl/SubscriptionManagerImpl$28:val$cancelled	[Z
/*      */         //   4: dup
/*      */         //   5: astore_1
/*      */         //   6: monitorenter
/*      */         //   7: aload_0
/*      */         //   8: getfield 202	com/aelitis/azureus/core/subs/impl/SubscriptionManagerImpl$28:val$cancelled	[Z
/*      */         //   11: iconst_0
/*      */         //   12: baload
/*      */         //   13: aload_1
/*      */         //   14: monitorexit
/*      */         //   15: ireturn
/*      */         //   16: astore_2
/*      */         //   17: aload_1
/*      */         //   18: monitorexit
/*      */         //   19: aload_2
/*      */         //   20: athrow
/*      */         // Line number table:
/*      */         //   Java source line #3976	-> byte code offset #0
/*      */         //   Java source line #3978	-> byte code offset #7
/*      */         //   Java source line #3979	-> byte code offset #16
/*      */         // Local variable table:
/*      */         //   start	length	slot	name	signature
/*      */         //   0	21	0	this	28
/*      */         //   5	13	1	Ljava/lang/Object;	Object
/*      */         //   16	4	2	localObject1	Object
/*      */         // Exception table:
/*      */         //   from	to	target	type
/*      */         //   7	15	16	finally
/*      */         //   16	19	16	finally
/*      */       }
/* 3982 */     });
/* 3983 */     new SubscriptionAssociationLookup()
/*      */     {
/*      */ 
/*      */       public void cancel()
/*      */       {
/*      */ 
/* 3989 */         SubscriptionManagerImpl.this.log("    Association lookup cancelled");
/*      */         
/* 3991 */         synchronized (cancelled)
/*      */         {
/* 3993 */           cancelled[0] = true;
/*      */         }
/*      */       }
/*      */       
/*      */       public void setTimeout(long millis) {
/* 3998 */         SimpleTimer.addEvent("subs:timeout", SystemTime.getOffsetTime(millis), new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/* 4004 */             SubscriptionManagerImpl.29.this.val$listener.failed(SubscriptionManagerImpl.29.this.val$hash, SubscriptionManagerImpl.29.this.val$timeout_exception);
/*      */           }
/*      */         });
/*      */       }
/*      */     };
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
/*      */   protected void getPopularity(SubscriptionImpl subs, SubscriptionPopularityListener listener)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 4026 */     if (!subs.isAnonymous()) {
/*      */       try
/*      */       {
/* 4029 */         long pop = PlatformSubscriptionsMessenger.getPopularityBySID(subs.getShortID());
/*      */         
/* 4031 */         if (pop >= 0L)
/*      */         {
/* 4033 */           log("Got popularity of " + subs.getName() + " from platform: " + pop);
/*      */           
/* 4035 */           listener.gotPopularity(pop);
/*      */           
/* 4037 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 4043 */         if (subs.isSingleton())
/*      */         {
/*      */           try {
/* 4046 */             checkSingletonPublish(subs);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/*      */ 
/* 4051 */           listener.gotPopularity(subs.isSubscribed() ? 1L : 0L);
/*      */           
/* 4053 */           return;
/*      */         }
/*      */         
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 4059 */         log("Subscription lookup via platform failed", e);
/*      */       }
/*      */     }
/*      */     
/* 4063 */     getPopularityFromDHT(subs, listener, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void getPopularityFromDHT(final SubscriptionImpl subs, final SubscriptionPopularityListener listener, final boolean sync)
/*      */   {
/* 4073 */     final DHTPluginInterface dht_plugin = selectDHTPlugin(subs);
/*      */     
/* 4075 */     if (dht_plugin != null)
/*      */     {
/* 4077 */       if (!dht_plugin.isInitialising())
/*      */       {
/* 4079 */         getPopularitySupport(dht_plugin, subs, listener, sync);
/*      */       }
/*      */       else
/*      */       {
/* 4083 */         new AEThread2("SM:popwait", true)
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/* 4088 */             SubscriptionManagerImpl.this.getPopularitySupport(dht_plugin, subs, listener, sync);
/*      */           }
/*      */         }.start();
/*      */       }
/*      */     }
/*      */     else {
/* 4094 */       listener.failed(new com.aelitis.azureus.core.subs.SubscriptionException("DHT unavailable"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updatePopularityFromDHT(final SubscriptionImpl subs, boolean sync)
/*      */   {
/* 4103 */     getPopularityFromDHT(subs, new SubscriptionPopularityListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void gotPopularity(long popularity)
/*      */       {
/*      */ 
/*      */ 
/* 4111 */         subs.setCachedPopularity(popularity);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4118 */       public void failed(com.aelitis.azureus.core.subs.SubscriptionException error) { SubscriptionManagerImpl.this.log("Failed to update subscription popularity from DHT", error); } }, sync);
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
/*      */   protected void getPopularitySupport(final DHTPluginInterface dht_plugin, final SubscriptionImpl subs, final SubscriptionPopularityListener _listener, final boolean sync)
/*      */   {
/* 4131 */     log("Getting popularity of " + subs.getName() + " from DHT (" + dht_plugin.getNetwork() + ")");
/*      */     
/* 4133 */     byte[] sub_id = subs.getShortID();
/* 4134 */     int sub_version = subs.getVersion();
/*      */     
/* 4136 */     String key = "subscription:publish:" + ByteFormatter.encodeString(sub_id) + ":" + sub_version;
/*      */     
/*      */ 
/*      */ 
/* 4140 */     byte[][] keys = { subs.getPublicationHash(), getKeyBytes(key) };
/*      */     
/* 4142 */     final AESemaphore sem = new AESemaphore("SM:pop");
/*      */     
/* 4144 */     final long[] result = { -1L };
/*      */     
/* 4146 */     int timeout = '㪘' * (subs.isAnonymous() ? 3 : 1);
/*      */     
/* 4148 */     final SubscriptionPopularityListener listener = new SubscriptionPopularityListener()
/*      */     {
/*      */       private boolean done;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void gotPopularity(long popularity)
/*      */       {
/* 4157 */         synchronized (this) {
/* 4158 */           if (this.done) {
/* 4159 */             return;
/*      */           }
/* 4161 */           this.done = true;
/*      */         }
/* 4163 */         _listener.gotPopularity(popularity);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void failed(com.aelitis.azureus.core.subs.SubscriptionException error)
/*      */       {
/* 4170 */         synchronized (this) {
/* 4171 */           if (this.done) {
/* 4172 */             return;
/*      */           }
/* 4174 */           this.done = true;
/*      */         }
/* 4176 */         _listener.failed(error);
/*      */       }
/*      */     };
/*      */     
/* 4180 */     for (byte[] hash : keys)
/*      */     {
/* 4182 */       dht_plugin.get(hash, "Popularity lookup for subscription " + subs.getName(), (byte)8, 5, timeout, false, true, new DHTPluginOperationListener()
/*      */       {
/*      */         private boolean diversified;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4194 */         private int hits = 0;
/*      */         
/*      */ 
/*      */         public boolean diversified()
/*      */         {
/* 4199 */           this.diversified = true;
/*      */           
/* 4201 */           return false;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void starts(byte[] key) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */         {
/* 4215 */           com.aelitis.azureus.plugins.dht.DHTPluginKeyStats stats = dht_plugin.decodeStats(value);
/*      */           
/* 4217 */           if (stats != null)
/*      */           {
/* 4219 */             result[0] = Math.max(result[0], stats.getEntryCount());
/*      */             
/* 4221 */             this.hits += 1;
/*      */             
/* 4223 */             if (this.hits >= 3)
/*      */             {
/* 4225 */               done();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void complete(byte[] key, boolean timeout_occurred)
/*      */         {
/* 4243 */           if (this.diversified)
/*      */           {
/*      */ 
/*      */ 
/* 4247 */             result[0] *= 11L;
/*      */             
/* 4249 */             if (result[0] == 0L)
/*      */             {
/* 4251 */               result[0] = 10L;
/*      */             }
/*      */           }
/*      */           
/* 4255 */           done();
/*      */         }
/*      */         
/*      */ 
/*      */         protected void done()
/*      */         {
/* 4261 */           if (sync)
/*      */           {
/* 4263 */             sem.release();
/*      */ 
/*      */ 
/*      */           }
/* 4267 */           else if (result[0] == -1L)
/*      */           {
/* 4269 */             SubscriptionManagerImpl.this.log("Failed to get popularity of " + subs.getName() + " from DHT");
/*      */             
/* 4271 */             listener.failed(new com.aelitis.azureus.core.subs.SubscriptionException("Timeout"));
/*      */           }
/*      */           else
/*      */           {
/* 4275 */             SubscriptionManagerImpl.this.log("Get popularity of " + subs.getName() + " from DHT: " + result[0]);
/*      */             
/* 4277 */             listener.gotPopularity(result[0]);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/* 4284 */     if (sync)
/*      */     {
/* 4286 */       sem.reserve(timeout);
/*      */       
/* 4288 */       if (result[0] == -1L)
/*      */       {
/* 4290 */         log("Failed to get popularity of " + subs.getName() + " from DHT");
/*      */         
/* 4292 */         listener.failed(new com.aelitis.azureus.core.subs.SubscriptionException("Timeout"));
/*      */       }
/*      */       else
/*      */       {
/* 4296 */         log("Get popularity of " + subs.getName() + " from DHT: " + result[0]);
/*      */         
/* 4298 */         listener.gotPopularity(result[0]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void lookupSubscription(final byte[] association_hash, final byte[] sid, final int version, boolean is_anon, final subsLookupListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 4312 */       SubscriptionImpl subs = getSubscriptionFromPlatform(sid, is_anon, 3);
/*      */       
/* 4314 */       log("Added temporary subscription: " + subs.getString());
/*      */       
/* 4316 */       subs = addSubscription(subs);
/*      */       
/* 4318 */       listener.complete(association_hash, new Subscription[] { subs });
/*      */       
/* 4320 */       return;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 4324 */       if (listener.isCancelled())
/*      */       {
/* 4326 */         listener.failed(association_hash, new com.aelitis.azureus.core.subs.SubscriptionException("Cancelled"));
/*      */         
/* 4328 */         return;
/*      */       }
/*      */       
/* 4331 */       final String sid_str = ByteFormatter.encodeString(sid);
/*      */       
/* 4333 */       log("Subscription lookup via platform for " + sid_str + " failed", e);
/*      */       
/* 4335 */       if (getSubscriptionDownloadCount() > 8)
/*      */       {
/* 4337 */         log("Too many existing subscription downloads");
/*      */         
/* 4339 */         listener.complete(association_hash, new Subscription[0]);
/*      */         
/* 4341 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 4346 */       log("Subscription lookup via DHT starts for " + sid_str);
/*      */       
/* 4348 */       String key = "subscription:publish:" + ByteFormatter.encodeString(sid) + ":" + version;
/*      */       
/* 4350 */       this.dht_plugin_public.get(getKeyBytes(key), "Subs lookup read: " + ByteFormatter.encodeString(sid) + ":" + version, (byte)0, 12, 60000L, false, true, new DHTPluginOperationListener()
/*      */       {
/*      */         private boolean listener_handled;
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
/*      */         public boolean diversified()
/*      */         {
/* 4365 */           return true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void starts(byte[] key) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */         {
/* 4379 */           byte[] data = value.getValue();
/*      */           try
/*      */           {
/* 4382 */             final Map details = SubscriptionManagerImpl.this.decodeSubscriptionDetails(data);
/*      */             
/* 4384 */             if (SubscriptionImpl.getPublicationVersion(details) == version)
/*      */             {
/* 4386 */               Map singleton_details = (Map)details.get("x");
/*      */               
/* 4388 */               if (singleton_details == null)
/*      */               {
/* 4390 */                 synchronized (this)
/*      */                 {
/* 4392 */                   if (this.listener_handled)
/*      */                   {
/* 4394 */                     return;
/*      */                   }
/*      */                   
/* 4397 */                   this.listener_handled = true;
/*      */                 }
/*      */                 
/* 4400 */                 SubscriptionManagerImpl.this.log("    found " + sid_str + ", non-singleton");
/*      */                 
/* 4402 */                 new AEThread2("Subs:lookup download", true)
/*      */                 {
/*      */ 
/*      */                   public void run()
/*      */                   {
/* 4407 */                     SubscriptionManagerImpl.this.downloadSubscription(SubscriptionManagerImpl.34.this.val$association_hash, SubscriptionImpl.getPublicationHash(details), SubscriptionManagerImpl.34.this.val$sid, SubscriptionManagerImpl.34.this.val$version, SubscriptionImpl.getPublicationSize(details), SubscriptionManagerImpl.34.this.val$listener);
/*      */ 
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */                 }.start();
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/* 4419 */                 synchronized (this)
/*      */                 {
/* 4421 */                   if (this.listener_handled)
/*      */                   {
/* 4423 */                     return;
/*      */                   }
/*      */                   
/* 4426 */                   this.listener_handled = true;
/*      */                 }
/*      */                 
/* 4429 */                 SubscriptionManagerImpl.this.log("    found " + sid_str + ", singleton");
/*      */                 try
/*      */                 {
/* 4432 */                   SubscriptionImpl subs = SubscriptionManagerImpl.this.createSingletonSubscription(singleton_details, 3, false);
/*      */                   
/* 4434 */                   listener.complete(association_hash, new Subscription[] { subs });
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 4438 */                   listener.failed(association_hash, new com.aelitis.azureus.core.subs.SubscriptionException("Subscription creation failed", e));
/*      */                 }
/*      */               }
/*      */             }
/*      */             else {
/* 4443 */               SubscriptionManagerImpl.this.log("    found " + sid_str + " but version mismatch");
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 4448 */             SubscriptionManagerImpl.this.log("    found " + sid_str + " but verification failed", e);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void complete(byte[] original_key, boolean timeout_occurred)
/*      */         {
/* 4465 */           SubscriptionManagerImpl.this.log("    " + sid_str + " complete");
/*      */           
/* 4467 */           synchronized (this)
/*      */           {
/* 4469 */             if (this.listener_handled)
/*      */             {
/* 4471 */               return;
/*      */             }
/*      */             
/* 4474 */             this.listener_handled = true;
/*      */           }
/*      */           
/* 4477 */           listener.complete(association_hash, new Subscription[0]);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SubscriptionImpl getSubscriptionFromPlatform(byte[] sid, boolean is_anon, int add_type)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/*      */     try
/*      */     {
/* 4492 */       PlatformSubscriptionsMessenger.subscriptionDetails details = PlatformSubscriptionsMessenger.getSubscriptionBySID(sid, is_anon);
/*      */       
/* 4494 */       SubscriptionImpl res = getSubscriptionFromVuzeFileContent(sid, add_type, details.getContent());
/*      */       
/* 4496 */       int pop = details.getPopularity();
/*      */       
/* 4498 */       if (pop >= 0)
/*      */       {
/* 4500 */         res.setCachedPopularity(pop);
/*      */       }
/*      */       
/* 4503 */       return res;
/*      */     }
/*      */     catch (com.aelitis.azureus.core.subs.SubscriptionException e)
/*      */     {
/* 4507 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 4511 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Failed to read subscription from platform", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SubscriptionImpl getSubscriptionFromVuzeFile(byte[] sid, int add_type, File file)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 4523 */     VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*      */     
/* 4525 */     String file_str = file.getAbsolutePath();
/*      */     
/* 4527 */     VuzeFile vf = vfh.loadVuzeFile(file_str);
/*      */     
/* 4529 */     if (vf == null)
/*      */     {
/* 4531 */       log("Failed to load vuze file from " + file_str);
/*      */       
/* 4533 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Failed to load vuze file from " + file_str);
/*      */     }
/*      */     
/* 4536 */     return getSubscriptionFromVuzeFile(sid, add_type, vf);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SubscriptionImpl getSubscriptionFromVuzeFileContent(byte[] sid, int add_type, String content)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 4547 */     VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*      */     
/* 4549 */     VuzeFile vf = vfh.loadVuzeFile(Base64.decode(content));
/*      */     
/* 4551 */     if (vf == null)
/*      */     {
/* 4553 */       log("Failed to load vuze file from " + content);
/*      */       
/* 4555 */       throw new com.aelitis.azureus.core.subs.SubscriptionException("Failed to load vuze file from content");
/*      */     }
/*      */     
/* 4558 */     return getSubscriptionFromVuzeFile(sid, add_type, vf);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SubscriptionImpl getSubscriptionFromVuzeFile(byte[] sid, int add_type, VuzeFile vf)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 4569 */     VuzeFileComponent[] comps = vf.getComponents();
/*      */     
/* 4571 */     for (int j = 0; j < comps.length; j++)
/*      */     {
/* 4573 */       VuzeFileComponent comp = comps[j];
/*      */       
/* 4575 */       if (comp.getType() == 16)
/*      */       {
/* 4577 */         Map map = comp.getContent();
/*      */         try
/*      */         {
/* 4580 */           SubscriptionBodyImpl body = new SubscriptionBodyImpl(this, map);
/*      */           
/* 4582 */           SubscriptionImpl new_subs = new SubscriptionImpl(this, body, add_type, false);
/*      */           
/* 4584 */           if (Arrays.equals(new_subs.getShortID(), sid))
/*      */           {
/* 4586 */             return new_subs;
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 4590 */           log("Subscription decode failed", e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4595 */     throw new com.aelitis.azureus.core.subs.SubscriptionException("Subscription not found");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void downloadSubscription(final byte[] association_hash, byte[] torrent_hash, final byte[] sid, int version, int size, final subsLookupListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 4608 */       Object[] res = downloadTorrent(torrent_hash, size);
/*      */       
/* 4610 */       if (listener.isCancelled())
/*      */       {
/* 4612 */         listener.failed(association_hash, new com.aelitis.azureus.core.subs.SubscriptionException("Cancelled"));
/*      */         
/* 4614 */         return;
/*      */       }
/*      */       
/* 4617 */       if (res == null)
/*      */       {
/* 4619 */         listener.complete(association_hash, new Subscription[0]);
/*      */         
/* 4621 */         return;
/*      */       }
/*      */       
/* 4624 */       downloadSubscription((TOTorrent)res[0], (InetSocketAddress)res[1], sid, version, "Subscription " + ByteFormatter.encodeString(sid) + " for " + ByteFormatter.encodeString(association_hash), new downloadListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void complete(File data_file)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4636 */           boolean reported = false;
/*      */           try
/*      */           {
/* 4639 */             if (listener.isCancelled())
/*      */             {
/* 4641 */               listener.failed(association_hash, new com.aelitis.azureus.core.subs.SubscriptionException("Cancelled"));
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 4646 */               SubscriptionImpl subs = SubscriptionManagerImpl.this.getSubscriptionFromVuzeFile(sid, 3, data_file);
/*      */               
/* 4648 */               SubscriptionManagerImpl.this.log("Added temporary subscription: " + subs.getString());
/*      */               
/* 4650 */               subs = SubscriptionManagerImpl.this.addSubscription(subs);
/*      */               
/* 4652 */               listener.complete(association_hash, new Subscription[] { subs });
/*      */               
/* 4654 */               reported = true;
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 4658 */             SubscriptionManagerImpl.this.log("Subscription decode failed", e);
/*      */           }
/*      */           finally
/*      */           {
/* 4662 */             if (!reported)
/*      */             {
/* 4664 */               listener.complete(association_hash, new Subscription[0]);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void complete(Download download, File torrent_file)
/*      */         {
/* 4674 */           File data_file = new File(download.getSavePath());
/*      */           try
/*      */           {
/* 4677 */             SubscriptionManagerImpl.this.removeDownload(download, false);
/*      */             
/* 4679 */             complete(data_file);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 4683 */             SubscriptionManagerImpl.this.log("Failed to remove download", e);
/*      */             
/* 4685 */             listener.complete(association_hash, new Subscription[0]);
/*      */           }
/*      */           finally
/*      */           {
/* 4689 */             torrent_file.delete();
/*      */             
/* 4691 */             data_file.delete();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void failed(Throwable error)
/*      */         {
/* 4699 */           listener.complete(association_hash, new Subscription[0]);
/*      */         }
/*      */         
/*      */ 
/*      */         public Map getRecoveryData()
/*      */         {
/* 4705 */           return null;
/*      */         }
/*      */         
/*      */ 
/*      */         public boolean isCancelled()
/*      */         {
/* 4711 */           return listener.isCancelled();
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 4717 */       log("Subscription download failed", e);
/*      */       
/* 4719 */       listener.complete(association_hash, new Subscription[0]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getSubscriptionDownloadCount()
/*      */   {
/* 4726 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*      */     
/* 4728 */     Download[] downloads = pi.getDownloadManager().getDownloads();
/*      */     
/* 4730 */     int res = 0;
/*      */     
/* 4732 */     for (int i = 0; i < downloads.length; i++)
/*      */     {
/* 4734 */       Download download = downloads[i];
/*      */       
/* 4736 */       if (download.getBooleanAttribute(this.ta_subs_download))
/*      */       {
/* 4738 */         res++;
/*      */       }
/*      */     }
/*      */     
/* 4742 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void associationAdded(SubscriptionImpl subscription, byte[] association_hash)
/*      */   {
/* 4750 */     recordAssociations(association_hash, new SubscriptionImpl[] { subscription }, false);
/*      */     
/* 4752 */     DHTPluginInterface dht_plugin = selectDHTPlugin(subscription);
/*      */     
/* 4754 */     if (dht_plugin != null)
/*      */     {
/* 4756 */       publishAssociations();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addPotentialAssociation(SubscriptionImpl subs, String result_id, String key)
/*      */   {
/* 4766 */     if (key == null)
/*      */     {
/* 4768 */       Debug.out("Attempt to add null key!");
/*      */       
/* 4770 */       return;
/*      */     }
/*      */     
/* 4773 */     log("Added potential association: " + subs.getName() + "/" + result_id + " -> " + key);
/*      */     
/* 4775 */     synchronized (this.potential_associations)
/*      */     {
/* 4777 */       this.potential_associations.add(new Object[] { subs, result_id, key, new Long(System.currentTimeMillis()) });
/*      */       
/* 4779 */       if (this.potential_associations.size() > 512)
/*      */       {
/* 4781 */         this.potential_associations.remove(0);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkPotentialAssociations(byte[] hash, String key)
/*      */   {
/* 4791 */     log("Checking potential association: " + key + " -> " + ByteFormatter.encodeString(hash));
/*      */     
/* 4793 */     SubscriptionImpl subs = null;
/* 4794 */     String result_id = null;
/*      */     
/* 4796 */     synchronized (this.potential_associations)
/*      */     {
/* 4798 */       Iterator<Object[]> it = this.potential_associations.iterator();
/*      */       
/* 4800 */       while (it.hasNext())
/*      */       {
/* 4802 */         Object[] entry = (Object[])it.next();
/*      */         
/* 4804 */         String this_key = (String)entry[2];
/*      */         
/*      */ 
/*      */ 
/* 4808 */         if (key.startsWith(this_key))
/*      */         {
/* 4810 */           subs = (SubscriptionImpl)entry[0];
/* 4811 */           result_id = (String)entry[1];
/*      */           
/* 4813 */           log("    key matched to subscription " + subs.getName() + "/" + result_id);
/*      */           
/* 4815 */           it.remove();
/*      */           
/* 4817 */           break;
/*      */         }
/*      */       }
/*      */       
/* 4821 */       if (subs == null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 4826 */         it = this.potential_associations.iterator();
/*      */         
/* 4828 */         while (it.hasNext())
/*      */         {
/* 4830 */           Object[] entry = (Object[])it.next();
/*      */           
/* 4832 */           SubscriptionImpl subs_temp = (SubscriptionImpl)entry[0];
/* 4833 */           String result_id_temp = (String)entry[1];
/*      */           
/* 4835 */           SubscriptionResult result = subs_temp.getHistory().getResult(result_id_temp);
/*      */           
/* 4837 */           if (result != null)
/*      */           {
/* 4839 */             Map<Integer, Object> props = result.toPropertyMap();
/*      */             
/* 4841 */             byte[] result_hash = (byte[])props.get(Integer.valueOf(21));
/*      */             
/* 4843 */             if (result_hash == null)
/*      */             {
/* 4845 */               String url = (String)props.get(Integer.valueOf(23));
/*      */               
/* 4847 */               if (url == null)
/*      */               {
/* 4849 */                 url = (String)props.get(Integer.valueOf(12));
/*      */               }
/*      */               
/* 4852 */               if (url != null)
/*      */               {
/* 4854 */                 String lc_url = url.toLowerCase(java.util.Locale.US);
/*      */                 
/* 4856 */                 if (lc_url.startsWith("http"))
/*      */                 {
/* 4858 */                   String alt_url = UrlUtils.parseTextForURL(url.substring(5), true);
/*      */                   
/* 4860 */                   if (key.startsWith(alt_url))
/*      */                   {
/* 4862 */                     result_hash = hash;
/*      */                   }
/* 4864 */                 } else if (lc_url.startsWith("magnet"))
/*      */                 {
/* 4866 */                   result_hash = UrlUtils.extractHash(lc_url);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 4871 */             if ((result_hash != null) && (Arrays.equals(result_hash, hash)))
/*      */             {
/* 4873 */               subs = subs_temp;
/* 4874 */               result_id = result_id_temp;
/*      */               
/* 4876 */               log("    hash matched to subscription " + subs.getName() + "/" + result_id);
/*      */               
/* 4878 */               it.remove();
/*      */               
/* 4880 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4887 */     if (subs == null)
/*      */     {
/* 4889 */       log("    no potential associations found");
/*      */     }
/*      */     else
/*      */     {
/* 4893 */       SubscriptionResult result = subs.getHistory().getResult(result_id);
/*      */       
/* 4895 */       if (result != null)
/*      */       {
/* 4897 */         log("    result found, marking as read");
/*      */         
/* 4899 */         result.setRead(true);
/*      */       }
/*      */       else
/*      */       {
/* 4903 */         log("    result not found");
/*      */       }
/*      */       
/* 4906 */       log("    adding association");
/*      */       
/* 4908 */       subs.addAssociation(hash);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void tidyPotentialAssociations()
/*      */   {
/* 4915 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 4917 */     synchronized (this.potential_associations)
/*      */     {
/* 4919 */       Iterator it = this.potential_associations.iterator();
/*      */       
/* 4921 */       while ((it.hasNext()) && (this.potential_associations.size() > 16))
/*      */       {
/* 4923 */         Object[] entry = (Object[])it.next();
/*      */         
/* 4925 */         long created = ((Long)entry[3]).longValue();
/*      */         
/* 4927 */         if (created > now)
/*      */         {
/* 4929 */           entry[3] = new Long(now);
/*      */         }
/* 4931 */         else if (now - created > 3600000L)
/*      */         {
/* 4933 */           SubscriptionImpl subs = (SubscriptionImpl)entry[0];
/*      */           
/* 4935 */           String result_id = (String)entry[1];
/* 4936 */           String key = (String)entry[2];
/*      */           
/* 4938 */           log("Removing expired potential association: " + subs.getName() + "/" + result_id + " -> " + key);
/*      */           
/* 4940 */           it.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4945 */     synchronized (this.potential_associations2)
/*      */     {
/* 4947 */       Iterator it = this.potential_associations2.entrySet().iterator();
/*      */       
/* 4949 */       while ((it.hasNext()) && (this.potential_associations2.size() > 16))
/*      */       {
/* 4951 */         Map.Entry map_entry = (Map.Entry)it.next();
/*      */         
/* 4953 */         byte[] hash = ((HashWrapper)map_entry.getKey()).getBytes();
/*      */         
/* 4955 */         Object[] entry = (Object[])map_entry.getValue();
/*      */         
/* 4957 */         long created = ((Long)entry[2]).longValue();
/*      */         
/* 4959 */         if (created > now)
/*      */         {
/* 4961 */           entry[2] = new Long(now);
/*      */         }
/* 4963 */         else if (now - created > 3600000L)
/*      */         {
/* 4965 */           SubscriptionImpl[] subs = (SubscriptionImpl[])entry[0];
/*      */           
/* 4967 */           String subs_str = "";
/*      */           
/* 4969 */           for (int i = 0; i < subs.length; i++) {
/* 4970 */             subs_str = subs_str + (i == 0 ? "" : ",") + subs[i].getName();
/*      */           }
/*      */           
/* 4973 */           log("Removing expired potential association: " + ByteFormatter.encodeString(hash) + " -> " + subs_str);
/*      */           
/* 4975 */           it.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void recordAssociations(byte[] association_hash, SubscriptionImpl[] subscriptions, boolean full_lookup)
/*      */   {
/* 4987 */     HashWrapper hw = new HashWrapper(association_hash);
/*      */     
/* 4989 */     synchronized (this.potential_associations2)
/*      */     {
/* 4991 */       this.potential_associations2.put(hw, new Object[] { subscriptions, Boolean.valueOf(full_lookup), new Long(SystemTime.getCurrentTime()) });
/*      */     }
/*      */     
/* 4994 */     if (recordAssociationsSupport(association_hash, subscriptions, full_lookup))
/*      */     {
/* 4996 */       synchronized (this.potential_associations2)
/*      */       {
/* 4998 */         this.potential_associations2.remove(hw);
/*      */       }
/*      */       
/*      */     } else {
/* 5002 */       log("Deferring association for " + ByteFormatter.encodeString(association_hash));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addPrepareTrigger(byte[] hash, Subscription[] subs, SubscriptionResult[] results)
/*      */   {
/* 5012 */     synchronized (this.potential_associations3)
/*      */     {
/* 5014 */       this.potential_associations3.put(new HashWrapper(hash), new Object[] { subs, results });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removePrepareTrigger(byte[] hash)
/*      */   {
/* 5022 */     synchronized (this.potential_associations3)
/*      */     {
/* 5024 */       this.potential_associations3.remove(new HashWrapper(hash));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void prepareDownload(Download download, Subscription[] subscriptions, SubscriptionResult[] results)
/*      */   {
/*      */     try
/*      */     {
/* 5035 */       if (subscriptions.length > 0)
/*      */       {
/* 5037 */         Subscription subs = subscriptions[0];
/*      */         
/* 5039 */         if ((results != null) && (results.length > 0)) {
/*      */           try
/*      */           {
/* 5042 */             SubscriptionResult result = results[0];
/*      */             
/* 5044 */             Map<Integer, Object> props = result.toPropertyMap();
/*      */             
/* 5046 */             Long leechers = (Long)props.get(Integer.valueOf(4));
/* 5047 */             Long seeds = (Long)props.get(Integer.valueOf(5));
/*      */             
/* 5049 */             if ((leechers != null) && (seeds != null) && (leechers.longValue() >= 0L) && (seeds.longValue() >= 0L))
/*      */             {
/* 5051 */               org.gudy.azureus2.core3.download.DownloadManager core_dm = PluginCoreUtils.unwrap(download);
/*      */               
/* 5053 */               DownloadManagerState state = core_dm.getDownloadState();
/*      */               
/* 5055 */               long cache = (seeds.longValue() & 0xFFFFFF) << 32 | leechers.longValue() & 0xFFFFFF;
/*      */               
/* 5057 */               state.setLongAttribute("scsrc", 1L);
/* 5058 */               state.setLongAttribute("scrapecache", cache);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/*      */ 
/* 5065 */         String category = subs.getCategory();
/*      */         
/* 5067 */         if (category != null)
/*      */         {
/* 5069 */           String existing = download.getAttribute(this.ta_category);
/*      */           
/* 5071 */           if (existing == null)
/*      */           {
/* 5073 */             download.setAttribute(this.ta_category, category);
/*      */           }
/*      */         }
/*      */         
/* 5077 */         long tag_id = subs.getTagID();
/*      */         
/* 5079 */         if (tag_id >= 0L)
/*      */         {
/* 5081 */           Tag tag = com.aelitis.azureus.core.tag.TagManagerFactory.getTagManager().lookupTagByUID(tag_id);
/*      */           
/* 5083 */           if (tag != null)
/*      */           {
/* 5085 */             org.gudy.azureus2.core3.download.DownloadManager core_dm = PluginCoreUtils.unwrap(download);
/*      */             
/* 5087 */             if (!tag.hasTaggable(core_dm))
/*      */             {
/* 5089 */               tag.addTaggable(core_dm);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 5094 */         String[] nets = subs.getHistory().getDownloadNetworks();
/*      */         
/* 5096 */         if (nets != null)
/*      */         {
/* 5098 */           org.gudy.azureus2.core3.download.DownloadManager core_dm = PluginCoreUtils.unwrap(download);
/*      */           
/* 5100 */           DownloadManagerState state = core_dm.getDownloadState();
/*      */           
/* 5102 */           state.setNetworks(nets);
/*      */           
/*      */ 
/*      */ 
/* 5106 */           state.setFlag(4096L, true);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 5112 */       log("Failed to prepare association", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean recordAssociationsSupport(byte[] association_hash, SubscriptionImpl[] subscriptions, boolean full_lookup)
/*      */   {
/* 5122 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*      */     
/* 5124 */     boolean download_found = false;
/* 5125 */     boolean changed = false;
/* 5126 */     boolean assoc_added = false;
/*      */     try
/*      */     {
/* 5129 */       Download download = pi.getDownloadManager().getDownload(association_hash);
/*      */       
/* 5131 */       if (download != null)
/*      */       {
/* 5133 */         download_found = true;
/*      */         
/* 5135 */         Map<String, Object> map = download.getMapAttribute(this.ta_subscription_info);
/*      */         
/* 5137 */         if (map == null)
/*      */         {
/* 5139 */           map = new LightHashMap();
/*      */         }
/*      */         else
/*      */         {
/* 5143 */           map = new LightHashMap(map);
/*      */         }
/*      */         
/* 5146 */         List<byte[]> s = (List)map.get("s");
/*      */         
/* 5148 */         for (int i = 0; i < subscriptions.length; i++)
/*      */         {
/* 5150 */           SubscriptionImpl subscription = subscriptions[i];
/*      */           
/* 5152 */           byte[] sid = subscription.getShortID();
/*      */           
/* 5154 */           if (s == null)
/*      */           {
/* 5156 */             s = new ArrayList();
/*      */             
/* 5158 */             s.add(sid);
/*      */             
/* 5160 */             changed = true;
/*      */             
/* 5162 */             map.put("s", s);
/*      */           }
/*      */           else
/*      */           {
/* 5166 */             boolean found = false;
/*      */             
/* 5168 */             for (int j = 0; j < s.size(); j++)
/*      */             {
/* 5170 */               byte[] existing = (byte[])s.get(j);
/*      */               
/* 5172 */               if (Arrays.equals(sid, existing))
/*      */               {
/* 5174 */                 found = true;
/*      */                 
/* 5176 */                 break;
/*      */               }
/*      */             }
/*      */             
/* 5180 */             if (!found)
/*      */             {
/* 5182 */               s.add(sid);
/*      */               
/* 5184 */               if ((subscription.isSubscribed()) && (subscription.isPublic()) && (!subscription.isSearchTemplate()))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5190 */                 if (subscription.addAssociationSupport(association_hash, true))
/*      */                 {
/* 5192 */                   assoc_added = true;
/*      */                 }
/*      */               }
/*      */               
/* 5196 */               changed = true;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 5201 */         if (full_lookup)
/*      */         {
/* 5203 */           map.put("lc", new Long(SystemTime.getCurrentTime()));
/*      */           
/* 5205 */           changed = true;
/*      */         }
/*      */         
/* 5208 */         if (changed)
/*      */         {
/* 5210 */           download.setMapAttribute(this.ta_subscription_info, map);
/*      */         }
/*      */         
/* 5213 */         if ((subscriptions.length == 1) && (subscriptions[0].isSearchTemplate()) && (!full_lookup))
/*      */         {
/* 5215 */           searchTemplateOK(subscriptions[0], download);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 5220 */       log("Failed to record associations", e);
/*      */     }
/*      */     
/* 5223 */     if (changed)
/*      */     {
/* 5225 */       Iterator it = this.listeners.iterator();
/*      */       
/* 5227 */       while (it.hasNext()) {
/*      */         try
/*      */         {
/* 5230 */           ((SubscriptionManagerListener)it.next()).associationsChanged(association_hash);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 5234 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 5239 */     if (assoc_added)
/*      */     {
/* 5241 */       publishAssociations();
/*      */     }
/*      */     
/* 5244 */     return download_found;
/*      */   }
/*      */   
/* 5247 */   private AsyncDispatcher chat_write_dispatcher = new AsyncDispatcher("Subscriptions:cwd");
/* 5248 */   private Set<String> chat_st_done = new java.util.HashSet();
/* 5249 */   private LinkedList<BuddyPluginBeta.ChatInstance> chat_assoc_done = new LinkedList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void searchTemplateOK(final SubscriptionImpl subs, final Download download)
/*      */   {
/* 5256 */     if (BuddyPluginUtils.isBetaChatAvailable())
/*      */     {
/* 5258 */       this.chat_write_dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/* 5265 */           DHTPluginInterface dht = SubscriptionManagerImpl.this.selectDHTPlugin(download);
/*      */           
/* 5267 */           if (dht == null)
/*      */           {
/* 5269 */             return;
/*      */           }
/*      */           
/* 5272 */           String target_net = dht.getNetwork();
/*      */           
/* 5274 */           if (target_net != "Public")
/*      */           {
/* 5276 */             if (!BuddyPluginUtils.isBetaChatAnonAvailable())
/*      */             {
/* 5278 */               return;
/*      */             }
/*      */             
/* 5281 */             target_net = "I2P";
/*      */           }
/*      */           
/* 5284 */           String name = subs.getName();
/*      */           
/* 5286 */           int pos = name.indexOf(':');
/*      */           
/* 5288 */           if (pos != -1)
/*      */           {
/* 5290 */             name = name.substring(pos + 1).trim();
/*      */           }
/*      */           
/* 5293 */           if (SubscriptionManagerImpl.this.chat_st_done.contains(name))
/*      */           {
/* 5295 */             return;
/*      */           }
/*      */           
/* 5298 */           SubscriptionManagerImpl.this.chat_st_done.add(name);
/*      */           
/* 5300 */           final BuddyPluginBeta.ChatInstance chat = BuddyPluginUtils.getChat(target_net, "Search Templates");
/*      */           
/* 5302 */           if (chat != null)
/*      */           {
/* 5304 */             chat.setSharedNickname(false);
/*      */             
/* 5306 */             chat.setSaveMessages(false);
/*      */             
/* 5308 */             final String f_msg = subs.getURI() + "[[" + UrlUtils.encode(name) + "]]";
/*      */             
/* 5310 */             final Runnable do_write = new Runnable()
/*      */             {
/*      */ 
/*      */               public void run()
/*      */               {
/*      */ 
/* 5316 */                 Map<String, Object> flags = new HashMap();
/*      */                 
/* 5318 */                 flags.put("o", Integer.valueOf(3));
/*      */                 
/* 5320 */                 Map<String, Object> options = new HashMap();
/*      */                 
/* 5322 */                 chat.sendMessage(f_msg, flags, options);
/*      */               }
/*      */               
/* 5325 */             };
/* 5326 */             SubscriptionManagerImpl.this.waitForChat(chat, new AERunnable()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void runSupport()
/*      */               {
/*      */ 
/* 5333 */                 List<BuddyPluginBeta.ChatMessage> messages = chat.getMessages();
/*      */                 
/* 5335 */                 for (BuddyPluginBeta.ChatMessage message : messages)
/*      */                 {
/* 5337 */                   if (message.getMessage().equals(f_msg))
/*      */                   {
/* 5339 */                     return;
/*      */                   }
/*      */                 }
/*      */                 
/* 5343 */                 do_write.run();
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void assocOK(final SubscriptionImpl subs, final SubscriptionImpl.association assoc)
/*      */   {
/* 5357 */     if (BuddyPluginUtils.isBetaChatAvailable())
/*      */     {
/* 5359 */       this.chat_write_dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/* 5367 */             Download download = SubscriptionManagerImpl.this.azureus_core.getPluginManager().getDefaultPluginInterface().getDownloadManager().getDownload(assoc.getHash());
/*      */             
/* 5369 */             if (download != null)
/*      */             {
/* 5371 */               if (org.gudy.azureus2.core3.util.TorrentUtils.isReallyPrivate(PluginCoreUtils.unwrap(download.getTorrent())))
/*      */               {
/* 5373 */                 return;
/*      */               }
/*      */               
/* 5376 */               final BuddyPluginBeta.ChatInstance chat = BuddyPluginUtils.getChat(download);
/*      */               
/* 5378 */               if (chat != null)
/*      */               {
/* 5380 */                 String net = chat.getNetwork();
/*      */                 
/* 5382 */                 if ((net == "Public") || (subs.isAnonymous()))
/*      */                 {
/* 5384 */                   synchronized (SubscriptionManagerImpl.this.chat_assoc_done)
/*      */                   {
/* 5386 */                     if (!SubscriptionManagerImpl.this.chat_assoc_done.contains(chat))
/*      */                     {
/* 5388 */                       SubscriptionManagerImpl.this.chat_assoc_done.add(chat);
/*      */                       
/* 5390 */                       if (SubscriptionManagerImpl.this.chat_assoc_done.size() > 50)
/*      */                       {
/* 5392 */                         BuddyPluginBeta.ChatInstance c = (BuddyPluginBeta.ChatInstance)SubscriptionManagerImpl.this.chat_assoc_done.removeFirst();
/*      */                         
/* 5394 */                         c.setInteresting(false);
/*      */                         
/* 5396 */                         c.destroy();
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   
/* 5401 */                   String name = subs.getName();
/*      */                   
/* 5403 */                   if (subs.isSearchTemplate())
/*      */                   {
/* 5405 */                     int pos = name.indexOf(':');
/*      */                     
/* 5407 */                     if (pos != -1)
/*      */                     {
/* 5409 */                       name = name.substring(pos + 1).trim();
/*      */                     }
/*      */                   }
/*      */                   
/* 5413 */                   final String f_msg = (subs.isSearchTemplate() ? "Search Template" : "Subscription") + " " + subs.getURI() + "[[" + UrlUtils.encode(name) + "]]";
/*      */                   
/* 5415 */                   SubscriptionManagerImpl.this.waitForChat(chat, new AERunnable()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public void runSupport()
/*      */                     {
/*      */ 
/* 5422 */                       List<BuddyPluginBeta.ChatMessage> messages = chat.getMessages();
/*      */                       
/* 5424 */                       for (BuddyPluginBeta.ChatMessage message : messages)
/*      */                       {
/* 5426 */                         if (message.getMessage().equals(f_msg))
/*      */                         {
/* 5428 */                           synchronized (SubscriptionManagerImpl.this.chat_assoc_done)
/*      */                           {
/* 5430 */                             if (SubscriptionManagerImpl.this.chat_assoc_done.remove(chat))
/*      */                             {
/* 5432 */                               chat.destroy();
/*      */                             }
/*      */                           }
/*      */                           
/* 5436 */                           return;
/*      */                         }
/*      */                       }
/*      */                       
/* 5440 */                       Map<String, Object> flags = new HashMap();
/*      */                       
/* 5442 */                       flags.put("o", Integer.valueOf(3));
/*      */                       
/* 5444 */                       Map<String, Object> options = new HashMap();
/*      */                       
/* 5446 */                       chat.sendMessage(f_msg, flags, options);
/*      */                     }
/*      */                   });
/*      */                 }
/*      */                 else
/*      */                 {
/* 5452 */                   chat.destroy();
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void waitForChat(final BuddyPluginBeta.ChatInstance chat, final AERunnable runnable)
/*      */   {
/* 5471 */     final TimerEventPeriodic[] event = { null };
/*      */     
/* 5473 */     synchronized (event)
/*      */     {
/* 5475 */       event[0 = SimpleTimer.addPeriodicEvent("Subs:chat:checker", 30000L, new TimerEventPerformer()
/*      */       {
/*      */         private int elapsed_time;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent e)
/*      */         {
/* 5487 */           this.elapsed_time += 30000;
/*      */           
/* 5489 */           if (chat.isDestroyed())
/*      */           {
/* 5491 */             synchronized (event)
/*      */             {
/* 5493 */               event[0].cancel();
/*      */             }
/*      */             
/*      */ 
/*      */           }
/* 5498 */           else if ((chat.getIncomingSyncState() == 0) || (this.elapsed_time >= 300000))
/*      */           {
/*      */ 
/* 5501 */             synchronized (event)
/*      */             {
/* 5503 */               event[0].cancel();
/*      */             }
/*      */             
/* 5506 */             SimpleTimer.addEvent("Subs:chat:checker", SystemTime.getOffsetTime(300000L), new TimerEventPerformer()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public void perform(TimerEvent event)
/*      */               {
/*      */ 
/*      */ 
/* 5515 */                 if (!SubscriptionManagerImpl.38.this.val$chat.isDestroyed())
/*      */                 {
/* 5517 */                   SubscriptionManagerImpl.this.chat_write_dispatcher.dispatch(new AERunnable()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public void runSupport()
/*      */                     {
/*      */ 
/* 5524 */                       if (!SubscriptionManagerImpl.38.this.val$chat.isDestroyed())
/*      */                       {
/* 5526 */                         SubscriptionManagerImpl.38.this.val$runnable.runSupport();
/*      */                       }
/*      */                     }
/*      */                   });
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean publishAssociations()
/*      */   {
/* 5543 */     SubscriptionImpl subs_to_publish = null;
/* 5544 */     SubscriptionImpl.association assoc_to_publish = null;
/*      */     
/* 5546 */     synchronized (this)
/*      */     {
/* 5548 */       if (this.publish_associations_active >= (this.dht_plugin_public.isSleeping() ? 1 : PUB_ASSOC_CONC_MAX))
/*      */       {
/* 5550 */         return false;
/*      */       }
/*      */       
/* 5553 */       this.publish_associations_active += 1;
/*      */       
/* 5555 */       log("Publishing Associations Starts (conc=" + this.publish_associations_active + ")");
/*      */       
/* 5557 */       List<SubscriptionImpl> shuffled_subs = new ArrayList(this.subscriptions);
/*      */       
/* 5559 */       Collections.shuffle(shuffled_subs);
/*      */       
/* 5561 */       for (int i = 0; i < shuffled_subs.size(); i++)
/*      */       {
/* 5563 */         SubscriptionImpl sub = (SubscriptionImpl)shuffled_subs.get(i);
/*      */         
/* 5565 */         if ((sub.isSubscribed()) && (sub.isPublic()))
/*      */         {
/* 5567 */           assoc_to_publish = sub.getAssociationForPublish();
/*      */           
/* 5569 */           if (assoc_to_publish != null)
/*      */           {
/* 5571 */             subs_to_publish = sub;
/*      */             
/* 5573 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 5579 */     if (assoc_to_publish != null)
/*      */     {
/* 5581 */       publishAssociation(subs_to_publish, assoc_to_publish);
/*      */       
/* 5583 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 5587 */     log("Publishing Associations Complete");
/*      */     
/* 5589 */     synchronized (this)
/*      */     {
/* 5591 */       this.publish_associations_active -= 1;
/*      */     }
/*      */     
/* 5594 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int getPublishRemainingCount()
/*      */   {
/* 5601 */     synchronized (this)
/*      */     {
/* 5603 */       int result = 0;
/*      */       
/* 5605 */       for (SubscriptionImpl sub : this.subscriptions)
/*      */       {
/* 5607 */         if ((sub.isSubscribed()) && (sub.isPublic()))
/*      */         {
/* 5609 */           result += sub.getAssociationsRemainingForPublish();
/*      */         }
/*      */       }
/*      */       
/* 5613 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void publishAssociation(final SubscriptionImpl subs, final SubscriptionImpl.association assoc)
/*      */   {
/* 5622 */     log("Checking association '" + subs.getString() + "' -> '" + assoc.getString() + "'");
/*      */     
/* 5624 */     byte[] sub_id = subs.getShortID();
/* 5625 */     int sub_version = subs.getVersion();
/*      */     
/* 5627 */     byte[] assoc_hash = assoc.getHash();
/*      */     
/* 5629 */     final String key = "subscription:assoc:" + ByteFormatter.encodeString(assoc_hash);
/*      */     
/* 5631 */     final byte[] put_value = new byte[sub_id.length + 4];
/*      */     
/* 5633 */     System.arraycopy(sub_id, 0, put_value, 4, sub_id.length);
/*      */     
/* 5635 */     put_value[0] = ((byte)(sub_version >> 16));
/* 5636 */     put_value[1] = ((byte)(sub_version >> 8));
/* 5637 */     put_value[2] = ((byte)sub_version);
/* 5638 */     put_value[3] = ((byte)subs.getFixedRandom());
/*      */     
/* 5640 */     final DHTPluginInterface dht_plugin = selectDHTPlugin(subs);
/*      */     
/* 5642 */     if (dht_plugin == null)
/*      */     {
/* 5644 */       synchronized (this)
/*      */       {
/* 5646 */         this.publish_associations_active -= 1;
/*      */       }
/*      */       
/* 5649 */       return;
/*      */     }
/*      */     
/* 5652 */     dht_plugin.get(getKeyBytes(key), "Subs assoc read: " + Base32.encode(assoc_hash).substring(0, 16), (byte)0, 30, 60000 * (subs.isAnonymous() ? 2 : 1), false, false, new DHTPluginOperationListener()
/*      */     {
/*      */       private int hits;
/*      */       
/*      */ 
/*      */ 
/*      */       private boolean diversified;
/*      */       
/*      */ 
/*      */ 
/*      */       private int max_ver;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean diversified()
/*      */       {
/* 5669 */         this.diversified = true;
/*      */         
/* 5671 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void starts(byte[] key) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */       {
/* 5685 */         byte[] val = value.getValue();
/*      */         
/* 5687 */         if (val.length == put_value.length)
/*      */         {
/* 5689 */           boolean diff = false;
/*      */           
/* 5691 */           for (int i = 4; i < val.length; i++)
/*      */           {
/* 5693 */             if (val[i] != put_value[i])
/*      */             {
/* 5695 */               diff = true;
/*      */               
/* 5697 */               break;
/*      */             }
/*      */           }
/*      */           
/* 5701 */           if (!diff)
/*      */           {
/* 5703 */             this.hits += 1;
/*      */             
/* 5705 */             int ver = val[0] << 16 & 0xFF0000 | val[1] << 8 & 0xFF00 | val[2] & 0xFF;
/*      */             
/* 5707 */             if (ver > this.max_ver)
/*      */             {
/* 5709 */               this.max_ver = ver;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(byte[] original_key, boolean timeout_occurred)
/*      */       {
/* 5727 */         SubscriptionManagerImpl.this.log("Checked association '" + subs.getString() + "' -> '" + assoc.getString() + "' - max_ver=" + this.max_ver + ",hits=" + this.hits + ",div=" + this.diversified);
/*      */         
/* 5729 */         if (this.max_ver > subs.getVersion())
/*      */         {
/* 5731 */           if (!subs.isMine())
/*      */           {
/* 5733 */             SubscriptionManagerImpl.this.updateSubscription(subs, this.max_ver);
/*      */           }
/*      */         }
/*      */         
/* 5737 */         if ((this.hits < 10) && (!this.diversified))
/*      */         {
/* 5739 */           SubscriptionManagerImpl.this.log("    Publishing association '" + subs.getString() + "' -> '" + assoc.getString() + "', existing=" + this.hits + ", net=" + dht_plugin.getNetwork());
/*      */           
/* 5741 */           byte flags = 16;
/*      */           
/* 5743 */           if ((this.hits < 3) && (!this.diversified))
/*      */           {
/* 5745 */             flags = (byte)(flags | 0x20);
/*      */           }
/*      */           
/* 5748 */           if (subs.isAnonymous())
/*      */           {
/* 5750 */             flags = (byte)(flags | 0x40);
/*      */           }
/*      */           
/* 5753 */           dht_plugin.put(SubscriptionManagerImpl.this.getKeyBytes(key), "Subs assoc write: " + Base32.encode(assoc.getHash()).substring(0, 16) + " -> " + Base32.encode(subs.getShortID()) + ":" + subs.getVersion(), put_value, flags, new DHTPluginOperationListener()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public boolean diversified()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 5763 */               return true;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void starts(byte[] key) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void complete(byte[] key, boolean timeout_occurred)
/*      */             {
/* 5791 */               SubscriptionManagerImpl.this.log("        completed '" + SubscriptionManagerImpl.39.this.val$subs.getString() + "' -> '" + SubscriptionManagerImpl.39.this.val$assoc.getString() + "'");
/*      */               
/* 5793 */               SubscriptionManagerImpl.39.this.publishNext();
/*      */             }
/*      */             
/* 5796 */           });
/* 5797 */           SubscriptionManagerImpl.this.assocOK(subs, assoc);
/*      */         }
/*      */         else
/*      */         {
/* 5801 */           SubscriptionManagerImpl.this.log("    Not publishing association '" + subs.getString() + "' -> '" + assoc.getString() + "', existing =" + this.hits);
/*      */           
/* 5803 */           publishNext();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       protected void publishNext()
/*      */       {
/* 5810 */         synchronized (SubscriptionManagerImpl.this)
/*      */         {
/* 5812 */           SubscriptionManagerImpl.access$2710(SubscriptionManagerImpl.this);
/*      */         }
/*      */         
/* 5815 */         SubscriptionManagerImpl.this.publishNextAssociation();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void publishNextAssociation()
/*      */   {
/* 5823 */     boolean dht_sleeping = this.dht_plugin_public.isSleeping();
/*      */     
/* 5825 */     if (dht_sleeping)
/*      */     {
/* 5827 */       synchronized (this)
/*      */       {
/* 5829 */         if (this.publish_next_asyc_pending)
/*      */         {
/* 5831 */           return;
/*      */         }
/*      */         
/* 5834 */         this.publish_next_asyc_pending = true;
/*      */       }
/*      */       
/* 5837 */       SimpleTimer.addEvent("subs:pn:async", SystemTime.getCurrentTime() + 60000L, new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/* 5846 */           synchronized (SubscriptionManagerImpl.this)
/*      */           {
/* 5848 */             SubscriptionManagerImpl.this.publish_next_asyc_pending = false;
/*      */           }
/*      */           
/* 5851 */           SubscriptionManagerImpl.this.publishAssociations();
/*      */         }
/*      */         
/* 5854 */       });
/* 5855 */       return;
/*      */     }
/*      */     
/* 5858 */     publishAssociations();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void subscriptionUpdated()
/*      */   {
/* 5864 */     if (this.dht_plugin_public != null)
/*      */     {
/* 5866 */       publishSubscriptions();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void publishSubscriptions()
/*      */   {
/*      */     List shuffled_subs;
/*      */     
/* 5875 */     synchronized (this)
/*      */     {
/* 5877 */       if (this.publish_subscription_active)
/*      */       {
/* 5879 */         return;
/*      */       }
/*      */       
/* 5882 */       shuffled_subs = new ArrayList(this.subscriptions);
/*      */       
/* 5884 */       this.publish_subscription_active = true;
/*      */     }
/*      */     
/* 5887 */     boolean publish_initiated = false;
/*      */     try
/*      */     {
/* 5890 */       Collections.shuffle(shuffled_subs);
/*      */       
/* 5892 */       for (int i = 0; i < shuffled_subs.size(); i++)
/*      */       {
/* 5894 */         SubscriptionImpl sub = (SubscriptionImpl)shuffled_subs.get(i);
/*      */         
/* 5896 */         if ((sub.isSubscribed()) && (sub.isPublic()) && (!sub.getPublished()))
/*      */         {
/* 5898 */           sub.setPublished(true);
/*      */           
/* 5900 */           publishSubscription(sub);
/*      */           
/* 5902 */           publish_initiated = true;
/*      */           
/* 5904 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 5909 */       if (!publish_initiated)
/*      */       {
/* 5911 */         log("Publishing Subscriptions Complete");
/*      */         
/* 5913 */         synchronized (this)
/*      */         {
/* 5915 */           this.publish_subscription_active = false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void publishSubscription(final SubscriptionImpl subs)
/*      */   {
/* 5925 */     log("Checking subscription publication '" + subs.getString() + "'");
/*      */     
/* 5927 */     byte[] sub_id = subs.getShortID();
/* 5928 */     int sub_version = subs.getVersion();
/*      */     
/* 5930 */     final String key = "subscription:publish:" + ByteFormatter.encodeString(sub_id) + ":" + sub_version;
/*      */     
/* 5932 */     final DHTPluginInterface dht_plugin = selectDHTPlugin(subs);
/*      */     
/* 5934 */     if (dht_plugin == null)
/*      */     {
/* 5936 */       return;
/*      */     }
/*      */     
/* 5939 */     dht_plugin.get(getKeyBytes(key), "Subs presence read: " + ByteFormatter.encodeString(sub_id) + ":" + sub_version, (byte)0, 24, 60000 * (subs.isAnonymous() ? 2 : 1), false, false, new DHTPluginOperationListener()
/*      */     {
/*      */       private int hits;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       private boolean diversified;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean diversified()
/*      */       {
/* 5955 */         this.diversified = true;
/*      */         
/* 5957 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void starts(byte[] key) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */       {
/* 5971 */         byte[] data = value.getValue();
/*      */         try
/*      */         {
/* 5974 */           Map details = SubscriptionManagerImpl.this.decodeSubscriptionDetails(data);
/*      */           
/* 5976 */           if (subs.getVerifiedPublicationVersion(details) == subs.getVersion())
/*      */           {
/* 5978 */             this.hits += 1;
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(byte[] original_key, boolean timeout_occurred)
/*      */       {
/* 5997 */         SubscriptionManagerImpl.this.log("Checked subscription publication '" + subs.getString() + "' - hits=" + this.hits + ",div=" + this.diversified);
/*      */         
/* 5999 */         if ((this.hits < 10) && (!this.diversified))
/*      */         {
/* 6001 */           SubscriptionManagerImpl.this.log("    Publishing subscription '" + subs.getString() + ", existing=" + this.hits);
/*      */           try
/*      */           {
/* 6004 */             byte[] put_value = SubscriptionManagerImpl.this.encodeSubscriptionDetails(subs);
/*      */             
/* 6006 */             if (put_value.length < 512)
/*      */             {
/* 6008 */               byte flags = 0;
/*      */               
/* 6010 */               if ((this.hits < 3) && (!this.diversified))
/*      */               {
/* 6012 */                 flags = (byte)(flags | 0x20);
/*      */               }
/*      */               
/* 6015 */               if (subs.isAnonymous())
/*      */               {
/* 6017 */                 flags = (byte)(flags | 0x40);
/*      */               }
/*      */               
/* 6020 */               dht_plugin.put(SubscriptionManagerImpl.this.getKeyBytes(key), "Subs presence write: " + Base32.encode(subs.getShortID()) + ":" + subs.getVersion(), put_value, flags, new DHTPluginOperationListener()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public boolean diversified()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 6030 */                   return true;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void starts(byte[] key) {}
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void complete(byte[] key, boolean timeout_occurred)
/*      */                 {
/* 6058 */                   SubscriptionManagerImpl.this.log("        completed '" + SubscriptionManagerImpl.41.this.val$subs.getString() + "'");
/*      */                   
/* 6060 */                   SubscriptionManagerImpl.41.this.publishNext();
/*      */                 }
/*      */               });
/*      */             }
/*      */             else
/*      */             {
/* 6066 */               publishNext();
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 6070 */             Debug.printStackTrace(e);
/*      */             
/* 6072 */             publishNext();
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 6077 */           SubscriptionManagerImpl.this.log("    Not publishing subscription '" + subs.getString() + "', existing =" + this.hits);
/*      */           
/* 6079 */           publishNext();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       protected void publishNext()
/*      */       {
/* 6086 */         synchronized (SubscriptionManagerImpl.this)
/*      */         {
/* 6088 */           SubscriptionManagerImpl.this.publish_subscription_active = false;
/*      */         }
/*      */         
/* 6091 */         SubscriptionManagerImpl.this.publishSubscriptions();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateSubscription(final SubscriptionImpl subs, final int new_version)
/*      */   {
/* 6101 */     log("Subscription " + subs.getString() + " - higher version found: " + new_version);
/*      */     
/* 6103 */     if (!subs.canAutoUpgradeCheck())
/*      */     {
/* 6105 */       log("    Checked too recently or not updateable, ignoring");
/*      */       
/* 6107 */       return;
/*      */     }
/*      */     
/* 6110 */     if (subs.getHighestUserPromptedVersion() >= new_version)
/*      */     {
/* 6112 */       log("    User has already been prompted for version " + new_version + " so ignoring");
/*      */       
/* 6114 */       return;
/*      */     }
/*      */     
/* 6117 */     byte[] sub_id = subs.getShortID();
/*      */     
/* 6119 */     if (!subs.isAnonymous()) {
/*      */       try
/*      */       {
/* 6122 */         PlatformSubscriptionsMessenger.subscriptionDetails details = PlatformSubscriptionsMessenger.getSubscriptionBySID(sub_id, false);
/*      */         
/* 6124 */         if (!askIfCanUpgrade(subs, new_version))
/*      */         {
/* 6126 */           return;
/*      */         }
/*      */         
/* 6129 */         VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*      */         
/* 6131 */         VuzeFile vf = vfh.loadVuzeFile(Base64.decode(details.getContent()));
/*      */         
/* 6133 */         vfh.handleFiles(new VuzeFile[] { vf }, 16);
/*      */         
/* 6135 */         return;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 6139 */         log("Failed to read subscription from platform, trying DHT");
/*      */       }
/*      */     }
/*      */     
/* 6143 */     log("Checking subscription '" + subs.getString() + "' upgrade to version " + new_version);
/*      */     
/* 6145 */     String key = "subscription:publish:" + ByteFormatter.encodeString(sub_id) + ":" + new_version;
/*      */     
/* 6147 */     DHTPluginInterface dht_plugin = selectDHTPlugin(subs);
/*      */     
/* 6149 */     dht_plugin.get(getKeyBytes(key), "Subs update read: " + Base32.encode(sub_id) + ":" + new_version, (byte)0, 12, 60000 * (subs.isAnonymous() ? 2 : 1), false, false, new DHTPluginOperationListener()
/*      */     {
/*      */       private byte[] verified_hash;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       private int verified_size;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean diversified()
/*      */       {
/* 6165 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void starts(byte[] key) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */       {
/* 6179 */         byte[] data = value.getValue();
/*      */         try
/*      */         {
/* 6182 */           Map details = SubscriptionManagerImpl.this.decodeSubscriptionDetails(data);
/*      */           
/* 6184 */           if ((this.verified_hash == null) && (subs.getVerifiedPublicationVersion(details) == new_version))
/*      */           {
/*      */ 
/* 6187 */             this.verified_hash = SubscriptionImpl.getPublicationHash(details);
/* 6188 */             this.verified_size = SubscriptionImpl.getPublicationSize(details);
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(byte[] original_key, boolean timeout_occurred)
/*      */       {
/* 6208 */         if (this.verified_hash != null)
/*      */         {
/* 6210 */           SubscriptionManagerImpl.this.log("    Subscription '" + subs.getString() + " upgrade verified as authentic");
/*      */           
/* 6212 */           SubscriptionManagerImpl.this.updateSubscription(subs, new_version, this.verified_hash, this.verified_size);
/*      */         }
/*      */         else
/*      */         {
/* 6216 */           SubscriptionManagerImpl.this.log("    Subscription '" + subs.getString() + " upgrade not verified");
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] encodeSubscriptionDetails(SubscriptionImpl subs)
/*      */     throws IOException
/*      */   {
/* 6228 */     Map details = subs.getPublicationDetails();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 6233 */     details.put("!", new Long(random_seed));
/*      */     
/* 6235 */     byte[] encoded = org.gudy.azureus2.core3.util.BEncoder.encode(details);
/*      */     
/* 6237 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */     
/* 6239 */     GZIPOutputStream os = new GZIPOutputStream(baos);
/*      */     
/* 6241 */     os.write(encoded);
/*      */     
/* 6243 */     os.close();
/*      */     
/* 6245 */     byte[] compressed = baos.toByteArray();
/*      */     
/*      */     byte[] data;
/*      */     byte header;
/*      */     byte[] data;
/* 6250 */     if (compressed.length < encoded.length)
/*      */     {
/* 6252 */       byte header = 1;
/* 6253 */       data = compressed;
/*      */     }
/*      */     else {
/* 6256 */       header = 0;
/* 6257 */       data = encoded;
/*      */     }
/*      */     
/* 6260 */     byte[] result = new byte[data.length + 1];
/*      */     
/* 6262 */     result[0] = header;
/*      */     
/* 6264 */     System.arraycopy(data, 0, result, 1, data.length);
/*      */     
/* 6266 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Map decodeSubscriptionDetails(byte[] data)
/*      */     throws IOException
/*      */   {
/*      */     byte[] to_decode;
/*      */     
/*      */ 
/* 6277 */     if (data[0] == 0)
/*      */     {
/* 6279 */       byte[] to_decode = new byte[data.length - 1];
/*      */       
/* 6281 */       System.arraycopy(data, 1, to_decode, 0, data.length - 1);
/*      */     }
/*      */     else
/*      */     {
/* 6285 */       GZIPInputStream is = new GZIPInputStream(new java.io.ByteArrayInputStream(data, 1, data.length - 1));
/*      */       
/* 6287 */       to_decode = FileUtil.readInputStreamAsByteArray(is);
/*      */       
/* 6289 */       is.close();
/*      */     }
/*      */     
/* 6292 */     Map res = org.gudy.azureus2.core3.util.BDecoder.decode(to_decode);
/*      */     
/*      */ 
/*      */ 
/* 6296 */     res.remove("!");
/*      */     
/* 6298 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateSubscription(final SubscriptionImpl subs, final int update_version, final byte[] update_hash, final int update_size)
/*      */   {
/* 6308 */     log("Subscription " + subs.getString() + " - update hash=" + ByteFormatter.encodeString(update_hash) + ", size=" + update_size);
/*      */     
/* 6310 */     new AEThread2("SubsUpdate", true)
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/* 6316 */           Object[] res = SubscriptionManagerImpl.this.downloadTorrent(update_hash, update_size);
/*      */           
/* 6318 */           if (res != null)
/*      */           {
/* 6320 */             SubscriptionManagerImpl.this.updateSubscription(subs, update_version, (TOTorrent)res[0], (InetSocketAddress)res[1]);
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 6324 */           SubscriptionManagerImpl.this.log("    update failed", e);
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Object[] downloadTorrent(byte[] hash, int update_size)
/*      */   {
/* 6335 */     if (!isSubsDownloadEnabled())
/*      */     {
/* 6337 */       log("    Can't download subscription " + Base32.encode(hash) + " as feature disabled");
/*      */       
/* 6339 */       return null;
/*      */     }
/*      */     
/* 6342 */     MagnetPlugin magnet_plugin = getMagnetPlugin();
/*      */     
/* 6344 */     if (magnet_plugin == null)
/*      */     {
/* 6346 */       log("    Can't download, no magnet plugin");
/*      */       
/* 6348 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 6352 */       final InetSocketAddress[] sender = { null };
/*      */       
/* 6354 */       byte[] torrent_data = magnet_plugin.download(new com.aelitis.azureus.plugins.magnet.MagnetPluginProgressListener()
/*      */       {
/*      */         public void reportSize(long size) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void reportActivity(String str)
/*      */         {
/* 6367 */           SubscriptionManagerImpl.this.log("    MagnetDownload: " + str);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void reportCompleteness(int percent) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void reportContributor(InetSocketAddress address)
/*      */         {
/* 6380 */           synchronized (sender)
/*      */           {
/* 6382 */             sender[0] = address;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         public boolean verbose()
/*      */         {
/* 6389 */           return false;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 6395 */         public boolean cancelled() { return false; } }, hash, "", new InetSocketAddress[0], 300000L, 1);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6404 */       if (torrent_data == null)
/*      */       {
/* 6406 */         log("    download failed - timeout");
/*      */         
/* 6408 */         return null;
/*      */       }
/*      */       
/* 6411 */       log("Subscription torrent downloaded");
/*      */       
/* 6413 */       TOTorrent torrent = org.gudy.azureus2.core3.torrent.TOTorrentFactory.deserialiseFromBEncodedByteArray(torrent_data);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 6418 */       if (torrent.getSize() > update_size + 10240)
/*      */       {
/* 6420 */         log("Subscription download abandoned, torrent size is " + torrent.getSize() + ", underlying data size is " + update_size);
/*      */         
/* 6422 */         return null;
/*      */       }
/*      */       
/* 6425 */       if (torrent.getSize() > 4194304L)
/*      */       {
/* 6427 */         log("Subscription download abandoned, torrent size is too large (" + torrent.getSize() + ")");
/*      */         
/* 6429 */         return null;
/*      */       }
/*      */       
/* 6432 */       synchronized (sender)
/*      */       {
/* 6434 */         return new Object[] { torrent, sender[0] };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6441 */       return null;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 6439 */       log("    download failed", e);
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
/*      */ 
/*      */ 
/*      */   protected void downloadSubscription(final TOTorrent torrent, final InetSocketAddress peer, byte[] subs_id, int version, String name, final downloadListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 6457 */       com.aelitis.azureus.core.lws.LightWeightSeed lws = com.aelitis.azureus.core.lws.LightWeightSeedManager.getSingleton().get(new HashWrapper(torrent.getHash()));
/*      */       
/* 6459 */       if (lws != null)
/*      */       {
/* 6461 */         log("Light weight seed found");
/*      */         
/* 6463 */         listener.complete(lws.getDataLocation());
/*      */       }
/*      */       else {
/* 6466 */         String sid = ByteFormatter.encodeString(subs_id);
/*      */         
/* 6468 */         File dir = getSubsDir();
/*      */         
/* 6470 */         dir = new File(dir, "temp");
/*      */         
/* 6472 */         if (!dir.exists())
/*      */         {
/* 6474 */           if (!dir.mkdirs())
/*      */           {
/* 6476 */             throw new IOException("Failed to create dir '" + dir + "'");
/*      */           }
/*      */         }
/*      */         
/* 6480 */         final File torrent_file = new File(dir, sid + "_" + version + ".torrent");
/* 6481 */         File data_file = new File(dir, sid + "_" + version + ".vuze");
/*      */         
/* 6483 */         PluginInterface pi = PluginInitializer.getDefaultInterface();
/*      */         
/* 6485 */         final org.gudy.azureus2.plugins.download.DownloadManager dm = pi.getDownloadManager();
/*      */         
/* 6487 */         Download download = dm.getDownload(torrent.getHash());
/*      */         
/* 6489 */         if (download == null)
/*      */         {
/* 6491 */           log("Adding download for subscription '" + new String(torrent.getName()) + "'");
/*      */           
/* 6493 */           boolean is_update = getSubscriptionFromSID(subs_id) != null;
/*      */           
/* 6495 */           com.aelitis.azureus.core.torrent.PlatformTorrentUtils.setContentTitle(torrent, (is_update ? "Update" : "Download") + " for subscription '" + name + "'");
/*      */           
/*      */ 
/*      */ 
/* 6499 */           org.gudy.azureus2.core3.util.TorrentUtils.setFlag(torrent, 1, true);
/*      */           
/* 6501 */           Torrent t = new org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl(torrent);
/*      */           
/* 6503 */           t.setDefaultEncoding();
/*      */           
/* 6505 */           t.writeToFile(torrent_file);
/*      */           
/* 6507 */           download = dm.addDownload(t, torrent_file, data_file);
/*      */           
/* 6509 */           download.setFlag(4L, true);
/*      */           
/* 6511 */           download.setBooleanAttribute(this.ta_subs_download, true);
/*      */           
/* 6513 */           Map rd = listener.getRecoveryData();
/*      */           
/* 6515 */           if (rd != null)
/*      */           {
/* 6517 */             download.setMapAttribute(this.ta_subs_download_rd, rd);
/*      */           }
/*      */         }
/*      */         else {
/* 6521 */           log("Existing download found for subscription '" + new String(torrent.getName()) + "'");
/*      */         }
/*      */         
/* 6524 */         final Download f_download = download;
/*      */         
/* 6526 */         final TimerEventPeriodic[] event = { null };
/*      */         
/* 6528 */         event[0 = SimpleTimer.addPeriodicEvent("SM:cancelTimer", 10000L, new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6534 */           private long start_time = SystemTime.getMonotonousTime();
/*      */           
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent ev)
/*      */           {
/* 6540 */             boolean kill = false;
/*      */             try
/*      */             {
/* 6543 */               Download download = dm.getDownload(torrent.getHash());
/*      */               
/* 6545 */               if ((listener.isCancelled()) || (download == null))
/*      */               {
/* 6547 */                 kill = true;
/*      */               }
/*      */               else
/*      */               {
/* 6551 */                 int state = download.getState();
/*      */                 
/* 6553 */                 if (state == 8)
/*      */                 {
/* 6555 */                   SubscriptionManagerImpl.this.log("Download entered error state, removing");
/*      */                   
/* 6557 */                   kill = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 6561 */                   long now = SystemTime.getMonotonousTime();
/*      */                   
/* 6563 */                   long running_for = now - this.start_time;
/*      */                   
/* 6565 */                   if (running_for > 600000L)
/*      */                   {
/* 6567 */                     SubscriptionManagerImpl.this.log("Download hasn't completed in permitted time, removing");
/*      */                     
/* 6569 */                     kill = true;
/*      */                   }
/* 6571 */                   else if (running_for > 240000L)
/*      */                   {
/* 6573 */                     if (download.getStats().getDownloaded() == 0L)
/*      */                     {
/* 6575 */                       SubscriptionManagerImpl.this.log("Download has zero downloaded, removing");
/*      */                       
/* 6577 */                       kill = true;
/*      */                     }
/* 6579 */                   } else if (running_for > 120000L)
/*      */                   {
/* 6581 */                     org.gudy.azureus2.plugins.download.DownloadScrapeResult scrape = download.getLastScrapeResult();
/*      */                     
/* 6583 */                     if ((scrape == null) || (scrape.getSeedCount() <= 0))
/*      */                     {
/* 6585 */                       SubscriptionManagerImpl.this.log("Download has no seeds, removing");
/*      */                       
/* 6587 */                       kill = true;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 6594 */               SubscriptionManagerImpl.this.log("Download failed", e);
/*      */               
/* 6596 */               kill = true;
/*      */             }
/*      */             
/* 6599 */             if ((kill) && (event[0] != null)) {
/*      */               try
/*      */               {
/* 6602 */                 event[0].cancel();
/*      */                 
/* 6604 */                 if (!listener.isCancelled())
/*      */                 {
/* 6606 */                   listener.failed(new com.aelitis.azureus.core.subs.SubscriptionException("Download abandoned"));
/*      */                 }
/*      */               }
/*      */               finally {
/* 6610 */                 SubscriptionManagerImpl.this.removeDownload(f_download, true);
/*      */                 
/* 6612 */                 torrent_file.delete();
/*      */               }
/*      */               
/*      */             }
/*      */           }
/* 6617 */         });
/* 6618 */         download.addCompletionListener(new org.gudy.azureus2.plugins.download.DownloadCompletionListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void onCompletion(Download d)
/*      */           {
/*      */ 
/* 6625 */             listener.complete(d, torrent_file);
/*      */           }
/*      */         });
/*      */         
/* 6629 */         if (download.isComplete())
/*      */         {
/* 6631 */           listener.complete(download, torrent_file);
/*      */         }
/*      */         else
/*      */         {
/* 6635 */           download.setForceStart(true);
/*      */           
/* 6637 */           if (peer != null)
/*      */           {
/* 6639 */             download.addPeerListener(new org.gudy.azureus2.plugins.download.DownloadPeerListener()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void peerManagerAdded(Download download, PeerManager peer_manager)
/*      */               {
/*      */ 
/*      */ 
/* 6647 */                 InetSocketAddress tcp = org.gudy.azureus2.core3.util.AddressUtils.adjustTCPAddress(peer, true);
/* 6648 */                 InetSocketAddress udp = org.gudy.azureus2.core3.util.AddressUtils.adjustUDPAddress(peer, true);
/*      */                 
/* 6650 */                 SubscriptionManagerImpl.this.log("    Injecting peer into download: " + tcp);
/*      */                 
/* 6652 */                 peer_manager.addPeer(tcp.getAddress().getHostAddress(), tcp.getPort(), udp.getPort(), true);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void peerManagerRemoved(Download download, PeerManager peer_manager) {}
/*      */             });
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 6667 */       log("Failed to add download", e);
/*      */       
/* 6669 */       listener.failed(e);
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
/*      */   protected void updateSubscription(final SubscriptionImpl subs, final int new_version, TOTorrent torrent, InetSocketAddress peer)
/*      */   {
/* 6703 */     log("Subscription " + subs.getString() + " - update torrent: " + new String(torrent.getName()));
/*      */     
/* 6705 */     if (!askIfCanUpgrade(subs, new_version))
/*      */     {
/* 6707 */       return;
/*      */     }
/*      */     
/* 6710 */     downloadSubscription(torrent, peer, subs.getShortID(), new_version, subs.getName(false), new downloadListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(File data_file)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6722 */         SubscriptionManagerImpl.this.updateSubscription(subs, data_file);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(Download download, File torrent_file)
/*      */       {
/* 6730 */         SubscriptionManagerImpl.this.updateSubscription(subs, download, torrent_file, new File(download.getSavePath()));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void failed(Throwable error)
/*      */       {
/* 6737 */         SubscriptionManagerImpl.this.log("Failed to download subscription", error);
/*      */       }
/*      */       
/*      */ 
/*      */       public Map getRecoveryData()
/*      */       {
/* 6743 */         Map rd = new HashMap();
/*      */         
/* 6745 */         rd.put("sid", subs.getShortID());
/* 6746 */         rd.put("ver", new Long(new_version));
/*      */         
/* 6748 */         return rd;
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean isCancelled()
/*      */       {
/* 6754 */         return false;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean askIfCanUpgrade(SubscriptionImpl subs, int new_version)
/*      */   {
/* 6764 */     subs.setHighestUserPromptedVersion(new_version);
/*      */     
/* 6766 */     UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */     
/* 6768 */     String details = MessageText.getString("subscript.add.upgradeto.desc", new String[] { String.valueOf(new_version), subs.getName() });
/*      */     
/*      */ 
/*      */ 
/* 6772 */     long res = ui_manager.showMessageBox("subscript.add.upgrade.title", "!" + details + "!", 12L);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 6777 */     if (res != 4L)
/*      */     {
/* 6779 */       log("    User declined upgrade");
/*      */       
/* 6781 */       return false;
/*      */     }
/*      */     
/* 6784 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean recoverSubscriptionUpdate(Download download, final Map rd)
/*      */   {
/* 6792 */     byte[] sid = (byte[])rd.get("sid");
/* 6793 */     int version = ((Long)rd.get("ver")).intValue();
/*      */     
/* 6795 */     final SubscriptionImpl subs = getSubscriptionFromSID(sid);
/*      */     
/* 6797 */     if (subs == null)
/*      */     {
/* 6799 */       log("Can't recover '" + download.getName() + "' - subscription " + ByteFormatter.encodeString(sid) + " not found");
/*      */       
/* 6801 */       return false;
/*      */     }
/*      */     
/* 6804 */     downloadSubscription(((org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl)download.getTorrent()).getTorrent(), null, subs.getShortID(), version, subs.getName(false), new downloadListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(File data_file)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6816 */         SubscriptionManagerImpl.this.updateSubscription(subs, data_file);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(Download download, File torrent_file)
/*      */       {
/* 6824 */         SubscriptionManagerImpl.this.updateSubscription(subs, download, torrent_file, new File(download.getSavePath()));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void failed(Throwable error)
/*      */       {
/* 6831 */         SubscriptionManagerImpl.this.log("Failed to download subscription", error);
/*      */       }
/*      */       
/*      */ 
/*      */       public Map getRecoveryData()
/*      */       {
/* 6837 */         return rd;
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean isCancelled()
/*      */       {
/* 6843 */         return false;
/*      */       }
/*      */       
/* 6846 */     });
/* 6847 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateSubscription(SubscriptionImpl subs, Download download, File torrent_file, File data_file)
/*      */   {
/*      */     try
/*      */     {
/* 6858 */       removeDownload(download, false);
/*      */       try
/*      */       {
/* 6861 */         updateSubscription(subs, data_file);
/*      */       }
/*      */       finally
/*      */       {
/* 6865 */         if (!data_file.delete())
/*      */         {
/* 6867 */           log("Failed to delete update file '" + data_file + "'");
/*      */         }
/*      */         
/* 6870 */         if (!torrent_file.delete())
/*      */         {
/* 6872 */           log("Failed to delete update torrent '" + torrent_file + "'");
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 6877 */       log("Failed to remove update download", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeDownload(Download download, boolean remove_data)
/*      */   {
/*      */     try
/*      */     {
/* 6887 */       download.stop();
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */     try
/*      */     {
/* 6893 */       download.remove(true, remove_data);
/*      */       
/* 6895 */       log("Removed download '" + download.getName() + "'");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 6899 */       log("Failed to remove download '" + download.getName() + "'", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateSubscription(SubscriptionImpl subs, File data_location)
/*      */   {
/* 6908 */     log("Updating subscription '" + subs.getString() + " using '" + data_location + "'");
/*      */     
/* 6910 */     VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*      */     
/* 6912 */     VuzeFile vf = vfh.loadVuzeFile(data_location.getAbsolutePath());
/*      */     
/* 6914 */     vfh.handleFiles(new VuzeFile[] { vf }, 16);
/*      */   }
/*      */   
/*      */ 
/*      */   protected MagnetPlugin getMagnetPlugin()
/*      */   {
/* 6920 */     PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass(MagnetPlugin.class);
/*      */     
/* 6922 */     if (pi == null)
/*      */     {
/* 6924 */       return null;
/*      */     }
/*      */     
/* 6927 */     return (MagnetPlugin)pi.getPlugin();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Engine getEngine(SubscriptionImpl subs, Map json_map, boolean local_only)
/*      */     throws com.aelitis.azureus.core.subs.SubscriptionException
/*      */   {
/* 6938 */     long id = ((Long)json_map.get("engine_id")).longValue();
/*      */     
/* 6940 */     Engine engine = MetaSearchManagerFactory.getSingleton().getMetaSearch().getEngine(id);
/*      */     
/* 6942 */     if (engine != null)
/*      */     {
/* 6944 */       return engine;
/*      */     }
/*      */     
/* 6947 */     if (!local_only) {
/*      */       try
/*      */       {
/* 6950 */         if ((id >= 0L) && (id < 2147483647L))
/*      */         {
/* 6952 */           log("Engine " + id + " not present, loading");
/*      */           
/*      */ 
/*      */           try
/*      */           {
/* 6957 */             return MetaSearchManagerFactory.getSingleton().getMetaSearch().addEngine(id);
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/* 6963 */             throw new com.aelitis.azureus.core.subs.SubscriptionException("Failed to load engine '" + id + "'", e);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 6968 */         log("Failed to load search template", e);
/*      */       }
/*      */     }
/*      */     
/* 6972 */     engine = subs.extractEngine(json_map, id);
/*      */     
/* 6974 */     if (engine != null)
/*      */     {
/* 6976 */       return engine;
/*      */     }
/*      */     
/* 6979 */     throw new com.aelitis.azureus.core.subs.SubscriptionException("Failed to extract engine id " + id);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected LinkedHashMap<String, SubscriptionResultImpl> loadResults(SubscriptionImpl subs)
/*      */   {
/* 6986 */     synchronized (this.result_cache)
/*      */     {
/* 6988 */       Object[] entry = (Object[])this.result_cache.get(subs);
/*      */       
/* 6990 */       if (entry != null)
/*      */       {
/* 6992 */         entry[1] = Long.valueOf(SystemTime.getMonotonousTime());
/*      */         
/* 6994 */         return (LinkedHashMap)entry[0];
/*      */       }
/*      */       
/* 6997 */       LinkedHashMap results = new LinkedHashMap(1024);
/*      */       try
/*      */       {
/* 7000 */         File f = getResultsFile(subs);
/*      */         
/* 7002 */         Map map = FileUtil.readResilientFile(f);
/*      */         
/* 7004 */         List list = (List)map.get("results");
/*      */         
/* 7006 */         if (list != null)
/*      */         {
/* 7008 */           SubscriptionHistoryImpl history = (SubscriptionHistoryImpl)subs.getHistory();
/*      */           
/* 7010 */           for (int i = 0; i < list.size(); i++)
/*      */           {
/* 7012 */             Map result_map = (Map)list.get(i);
/*      */             try
/*      */             {
/* 7015 */               SubscriptionResultImpl result = new SubscriptionResultImpl(history, result_map);
/*      */               
/* 7017 */               results.put(result.getID(), result);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 7021 */               log("Failed to decode result '" + result_map + "'", e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 7028 */         log("Failed to load results for '" + subs.getName() + "' - continuing with empty result set", e);
/*      */       }
/*      */       
/* 7031 */       this.result_cache.put(subs, new Object[] { results, Long.valueOf(SystemTime.getMonotonousTime()) });
/*      */       
/* 7033 */       if (this.result_cache.size() > 5)
/*      */       {
/* 7035 */         SubscriptionImpl oldest_sub = null;
/* 7036 */         long oldest_time = Long.MAX_VALUE;
/*      */         
/* 7038 */         for (Map.Entry<SubscriptionImpl, Object[]> x : this.result_cache.entrySet())
/*      */         {
/* 7040 */           long time = ((Long)((Object[])x.getValue())[1]).longValue();
/*      */           
/* 7042 */           if (time < oldest_time)
/*      */           {
/* 7044 */             oldest_time = time;
/* 7045 */             oldest_sub = (SubscriptionImpl)x.getKey();
/*      */           }
/*      */         }
/*      */         
/* 7049 */         this.result_cache.remove(oldest_sub);
/*      */       }
/*      */       
/* 7052 */       return results;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setCategoryOnExisting(SubscriptionImpl subscription, String old_category, String new_category)
/*      */   {
/* 7062 */     PluginInterface default_pi = PluginInitializer.getDefaultInterface();
/*      */     
/* 7064 */     Download[] downloads = default_pi.getDownloadManager().getDownloads();
/*      */     
/* 7066 */     for (Download d : downloads)
/*      */     {
/* 7068 */       if (subscriptionExists(d, subscription))
/*      */       {
/* 7070 */         String existing = d.getAttribute(this.ta_category);
/*      */         
/* 7072 */         if ((existing == null) || (existing.equals(old_category)))
/*      */         {
/* 7074 */           d.setAttribute(this.ta_category, new_category);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxNonDeletedResults()
/*      */   {
/* 7083 */     return COConfigurationManager.getIntParameter("subscriptions.max.non.deleted.results");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaxNonDeletedResults(int max)
/*      */   {
/* 7090 */     if (max != getMaxNonDeletedResults())
/*      */     {
/* 7092 */       COConfigurationManager.setParameter("subscriptions.max.non.deleted.results", max);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getAutoStartDownloads()
/*      */   {
/* 7099 */     return COConfigurationManager.getBooleanParameter("subscriptions.auto.start.downloads");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAutoStartDownloads(boolean auto_start)
/*      */   {
/* 7106 */     if (auto_start != getAutoStartDownloads())
/*      */     {
/* 7108 */       COConfigurationManager.setParameter("subscriptions.auto.start.downloads", auto_start);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getAutoStartMinMB()
/*      */   {
/* 7115 */     return COConfigurationManager.getIntParameter("subscriptions.auto.start.min.mb");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAutoStartMinMB(int mb)
/*      */   {
/* 7122 */     if (mb != getAutoStartMinMB())
/*      */     {
/* 7124 */       COConfigurationManager.setParameter("subscriptions.auto.start.min.mb", mb);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getAutoStartMaxMB()
/*      */   {
/* 7131 */     return COConfigurationManager.getIntParameter("subscriptions.auto.start.max.mb");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAutoStartMaxMB(int mb)
/*      */   {
/* 7138 */     if (mb != getAutoStartMaxMB())
/*      */     {
/* 7140 */       COConfigurationManager.setParameter("subscriptions.auto.start.max.mb", mb);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getAutoDownloadMarkReadAfterDays()
/*      */   {
/* 7147 */     return COConfigurationManager.getIntParameter("subscriptions.auto.dl.mark.read.days");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAutoDownloadMarkReadAfterDays(int days)
/*      */   {
/* 7154 */     if (days != getAutoDownloadMarkReadAfterDays())
/*      */     {
/* 7156 */       COConfigurationManager.setParameter("subscriptions.auto.dl.mark.read.days", days);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean shouldAutoStart(Torrent torrent)
/*      */   {
/* 7164 */     if (getAutoStartDownloads())
/*      */     {
/* 7166 */       long min = getAutoStartMinMB() * 1024 * 1024L;
/* 7167 */       long max = getAutoStartMaxMB() * 1024 * 1024L;
/*      */       
/* 7169 */       if ((min <= 0L) && (max <= 0L))
/*      */       {
/* 7171 */         return true;
/*      */       }
/*      */       
/* 7174 */       long size = torrent.getSize();
/*      */       
/* 7176 */       if ((min > 0L) && (size < min))
/*      */       {
/* 7178 */         return false;
/*      */       }
/*      */       
/* 7181 */       if ((max > 0L) && (size > max))
/*      */       {
/* 7183 */         return false;
/*      */       }
/*      */       
/* 7186 */       return true;
/*      */     }
/*      */     
/*      */ 
/* 7190 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void saveResults(SubscriptionImpl subs, SubscriptionResultImpl[] results)
/*      */   {
/* 7199 */     synchronized (this.result_cache)
/*      */     {
/* 7201 */       this.result_cache.remove(subs);
/*      */       try
/*      */       {
/* 7204 */         File f = getResultsFile(subs);
/*      */         
/* 7206 */         Map map = new HashMap();
/*      */         
/* 7208 */         List list = new ArrayList(results.length);
/*      */         
/* 7210 */         map.put("results", list);
/*      */         
/* 7212 */         for (int i = 0; i < results.length; i++)
/*      */         {
/* 7214 */           list.add(results[i].toBEncodedMap());
/*      */         }
/*      */         
/* 7217 */         FileUtil.writeResilientFile(f, map);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 7221 */         log("Failed to save results for '" + subs.getName(), e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void loadConfig()
/*      */   {
/* 7229 */     if (!FileUtil.resilientConfigFileExists("subscriptions.config"))
/*      */     {
/* 7231 */       return;
/*      */     }
/*      */     
/* 7234 */     log("Loading configuration");
/*      */     
/* 7236 */     boolean some_are_mine = false;
/*      */     
/* 7238 */     synchronized (this)
/*      */     {
/* 7240 */       Map map = FileUtil.readResilientConfigFile("subscriptions.config");
/*      */       
/* 7242 */       List l_subs = (List)map.get("subs");
/*      */       
/* 7244 */       if (l_subs != null)
/*      */       {
/* 7246 */         for (int i = 0; i < l_subs.size(); i++)
/*      */         {
/* 7248 */           Map m = (Map)l_subs.get(i);
/*      */           try
/*      */           {
/* 7251 */             SubscriptionImpl sub = new SubscriptionImpl(this, m);
/*      */             
/* 7253 */             int index = Collections.binarySearch(this.subscriptions, sub, new Comparator() {
/*      */               public int compare(Subscription arg0, Subscription arg1) {
/* 7255 */                 return arg0.getID().compareTo(arg1.getID());
/*      */               }
/*      */             });
/* 7258 */             if (index < 0) {
/* 7259 */               index = -1 * index - 1;
/*      */               
/* 7261 */               this.subscriptions.add(index, sub);
/*      */             }
/*      */             
/* 7264 */             if (sub.isMine())
/*      */             {
/* 7266 */               some_are_mine = true;
/*      */             }
/*      */             
/* 7269 */             log("    loaded " + sub.getString());
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 7273 */             log("Failed to import subscription from " + m, e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 7279 */     if (some_are_mine)
/*      */     {
/* 7281 */       addMetaSearchListener();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void configDirty(SubscriptionImpl subs)
/*      */   {
/* 7289 */     changeSubscription(subs);
/*      */     
/* 7291 */     configDirty();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void configDirty()
/*      */   {
/* 7297 */     synchronized (this)
/*      */     {
/* 7299 */       if (this.config_dirty)
/*      */       {
/* 7301 */         return;
/*      */       }
/*      */       
/* 7304 */       this.config_dirty = true;
/*      */       
/* 7306 */       new org.gudy.azureus2.core3.util.DelayedEvent("Subscriptions:save", 5000L, new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/* 7313 */           synchronized (SubscriptionManagerImpl.this)
/*      */           {
/* 7315 */             if (!SubscriptionManagerImpl.this.config_dirty)
/*      */             {
/* 7317 */               return;
/*      */             }
/*      */             
/* 7320 */             SubscriptionManagerImpl.this.saveConfig();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void saveConfig()
/*      */   {
/* 7330 */     log("Saving configuration");
/*      */     
/* 7332 */     synchronized (this)
/*      */     {
/* 7334 */       this.config_dirty = false;
/*      */       
/* 7336 */       if (this.subscriptions.size() == 0)
/*      */       {
/* 7338 */         FileUtil.deleteResilientConfigFile("subscriptions.config");
/*      */       }
/*      */       else
/*      */       {
/* 7342 */         Map map = new HashMap();
/*      */         
/* 7344 */         List l_subs = new ArrayList();
/*      */         
/* 7346 */         map.put("subs", l_subs);
/*      */         
/* 7348 */         Iterator it = this.subscriptions.iterator();
/*      */         
/* 7350 */         while (it.hasNext())
/*      */         {
/* 7352 */           SubscriptionImpl sub = (SubscriptionImpl)it.next();
/*      */           try
/*      */           {
/* 7355 */             l_subs.add(sub.toMap());
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 7359 */             log("Failed to save subscription " + sub.getString(), e);
/*      */           }
/*      */         }
/*      */         
/* 7363 */         FileUtil.writeResilientConfigFile("subscriptions.config", map);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private byte[] getKeyBytes(String key)
/*      */   {
/*      */     try
/*      */     {
/* 7373 */       return key.getBytes("UTF-8");
/*      */     }
/*      */     catch (java.io.UnsupportedEncodingException e)
/*      */     {
/* 7377 */       Debug.out(e);
/*      */     }
/* 7379 */     return key.getBytes();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private AEDiagnosticsLogger getLogger()
/*      */   {
/* 7387 */     if (this.logger == null)
/*      */     {
/* 7389 */       this.logger = org.gudy.azureus2.core3.util.AEDiagnostics.getLogger("Subscriptions");
/*      */     }
/*      */     
/* 7392 */     return this.logger;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void log(String s, Throwable e)
/*      */   {
/* 7400 */     AEDiagnosticsLogger diag_logger = getLogger();
/*      */     
/* 7402 */     diag_logger.log(s);
/* 7403 */     diag_logger.log(e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void log(String s)
/*      */   {
/* 7410 */     AEDiagnosticsLogger diag_logger = getLogger();
/*      */     
/* 7412 */     diag_logger.log(s);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(SubscriptionManagerListener listener)
/*      */   {
/* 7419 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(SubscriptionManagerListener listener)
/*      */   {
/* 7426 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 7433 */     writer.println("Subscriptions");
/*      */     try
/*      */     {
/* 7436 */       writer.indent();
/*      */       
/* 7438 */       Subscription[] subs = getSubscriptions();
/*      */       
/* 7440 */       for (int i = 0; i < subs.length; i++)
/*      */       {
/* 7442 */         SubscriptionImpl sub = (SubscriptionImpl)subs[i];
/*      */         
/* 7444 */         sub.generate(writer);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 7449 */       writer.exdent();
/*      */     } }
/*      */   
/*      */   protected static abstract interface downloadListener { public abstract void complete(File paramFile);
/*      */     
/*      */     public abstract void complete(Download paramDownload, File paramFile);
/*      */     
/*      */     public abstract void failed(Throwable paramThrowable);
/*      */     
/*      */     public abstract Map getRecoveryData();
/*      */     
/*      */     public abstract boolean isCancelled(); }
/*      */   
/*      */   private static class searchMatcher { private String[] bits;
/*      */     
/* 7464 */     protected searchMatcher(String term) { this.bits = org.gudy.azureus2.core3.util.Constants.PAT_SPLIT_SPACE.split(term.toLowerCase());
/*      */       
/* 7466 */       this.bit_types = new int[this.bits.length];
/* 7467 */       this.bit_patterns = new Pattern[this.bits.length];
/*      */       
/* 7469 */       for (int i = 0; i < this.bits.length; i++)
/*      */       {
/* 7471 */         String bit = this.bits[i] = this.bits[i].trim();
/*      */         
/* 7473 */         if (bit.length() > 0)
/*      */         {
/* 7475 */           char c = bit.charAt(0);
/*      */           
/* 7477 */           if (c == '+')
/*      */           {
/* 7479 */             this.bit_types[i] = 1;
/*      */             
/* 7481 */             bit = this.bits[i] = bit.substring(1);
/*      */           }
/* 7483 */           else if (c == '-')
/*      */           {
/* 7485 */             this.bit_types[i] = 2;
/*      */             
/* 7487 */             bit = this.bits[i] = bit.substring(1);
/*      */           }
/*      */           
/* 7490 */           if ((bit.startsWith("(")) && (bit.endsWith(")")))
/*      */           {
/* 7492 */             bit = bit.substring(1, bit.length() - 1);
/*      */             try
/*      */             {
/* 7495 */               this.bit_patterns[i] = Pattern.compile(bit, 2);
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/* 7499 */           else if (bit.contains("|"))
/*      */           {
/*      */             try {
/* 7502 */               this.bit_patterns[i] = Pattern.compile(bit, 2);
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private int[] bit_types;
/*      */     
/*      */ 
/*      */     private Pattern[] bit_patterns;
/*      */     
/*      */ 
/*      */     public boolean matches(String str)
/*      */     {
/* 7520 */       str = str.toLowerCase();
/*      */       
/* 7522 */       boolean match = true;
/* 7523 */       boolean at_least_one = false;
/*      */       
/* 7525 */       for (int i = 0; i < this.bits.length; i++)
/*      */       {
/* 7527 */         String bit = this.bits[i];
/*      */         
/* 7529 */         if (bit.length() > 0)
/*      */         {
/*      */           boolean hit;
/*      */           boolean hit;
/* 7533 */           if (this.bit_patterns[i] == null)
/*      */           {
/* 7535 */             hit = str.contains(bit);
/*      */           }
/*      */           else
/*      */           {
/* 7539 */             hit = this.bit_patterns[i].matcher(str).find();
/*      */           }
/*      */           
/* 7542 */           int type = this.bit_types[i];
/*      */           
/* 7544 */           if (hit)
/*      */           {
/* 7546 */             if (type == 2)
/*      */             {
/* 7548 */               match = false;
/*      */               
/* 7550 */               break;
/*      */             }
/*      */             
/*      */ 
/* 7554 */             at_least_one = true;
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/* 7559 */           else if (type == 2)
/*      */           {
/* 7561 */             at_least_one = true;
/*      */           }
/*      */           else
/*      */           {
/* 7565 */             match = false;
/*      */             
/* 7567 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 7573 */       boolean res = (match) && (at_least_one);
/*      */       
/* 7575 */       return res;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private DHTPluginInterface selectDHTPlugin(SubscriptionImpl subs)
/*      */   {
/* 7583 */     if (subs.isAnonymous())
/*      */     {
/* 7585 */       List<DistributedDatabase> ddbs = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getUtilities().getDistributedDatabases(new String[] { "I2P" });
/*      */       
/* 7587 */       if (ddbs.size() > 0)
/*      */       {
/* 7589 */         return ((DistributedDatabase)ddbs.get(0)).getDHTPlugin();
/*      */       }
/*      */       
/* 7592 */       return null;
/*      */     }
/*      */     
/*      */ 
/* 7596 */     return this.dht_plugin_public;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private DHTPluginInterface selectDHTPlugin(Download download)
/*      */   {
/* 7604 */     String[] networks = download.getListAttribute(this.ta_networks);
/*      */     
/* 7606 */     return selectDHTPlugin(networks);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private DHTPluginInterface selectDHTPlugin(String[] networks)
/*      */   {
/* 7613 */     if (networks.length > 0)
/*      */     {
/* 7615 */       for (String net : networks)
/*      */       {
/* 7617 */         if (net == "Public")
/*      */         {
/* 7619 */           return this.dht_plugin_public;
/*      */         }
/*      */       }
/*      */       
/* 7623 */       List<DistributedDatabase> ddbs = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getUtilities().getDistributedDatabases(new String[] { "I2P" });
/*      */       
/* 7625 */       if (ddbs.size() > 0)
/*      */       {
/* 7627 */         return ((DistributedDatabase)ddbs.get(0)).getDHTPlugin();
/*      */       }
/*      */     }
/*      */     
/* 7631 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 7638 */     String NAME = "lalalal";
/* 7639 */     String URL_STR = "http://www.vuze.com/feed/publisher/ALL/1";
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
/*      */     try
/*      */     {
/* 7655 */       VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*      */       
/* 7657 */       Map map = new HashMap();
/*      */       
/* 7659 */       map.put("name", "lalalal");
/* 7660 */       map.put("url", "http://www.vuze.com/feed/publisher/ALL/1");
/* 7661 */       map.put("public", new Long(0L));
/* 7662 */       map.put("check_interval_mins", new Long(345L));
/*      */       
/* 7664 */       vf.addComponent(32, map);
/*      */       
/* 7666 */       vf.write(new File("C:\\temp\\srss_2.vuze"));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 7670 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public Subscription[] getSubscriptions()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 2608	com/aelitis/azureus/core/subs/impl/SubscriptionManagerImpl:subscriptions	Ljava/util/List;
/*      */     //   8: aload_0
/*      */     //   9: getfield 2608	com/aelitis/azureus/core/subs/impl/SubscriptionManagerImpl:subscriptions	Ljava/util/List;
/*      */     //   12: invokeinterface 3079 1 0
/*      */     //   17: anewarray 1538	com/aelitis/azureus/core/subs/impl/SubscriptionImpl
/*      */     //   20: invokeinterface 3086 2 0
/*      */     //   25: checkcast 1498	[Lcom/aelitis/azureus/core/subs/impl/SubscriptionImpl;
/*      */     //   28: checkcast 1498	[Lcom/aelitis/azureus/core/subs/impl/SubscriptionImpl;
/*      */     //   31: aload_1
/*      */     //   32: monitorexit
/*      */     //   33: areturn
/*      */     //   34: astore_2
/*      */     //   35: aload_1
/*      */     //   36: monitorexit
/*      */     //   37: aload_2
/*      */     //   38: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2719	-> byte code offset #0
/*      */     //   Java source line #2721	-> byte code offset #4
/*      */     //   Java source line #2722	-> byte code offset #34
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	39	0	this	SubscriptionManagerImpl
/*      */     //   2	34	1	Ljava/lang/Object;	Object
/*      */     //   34	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	33	34	finally
/*      */     //   34	37	34	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getSubscriptionCount(boolean subscribed_only)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: iload_1
/*      */     //   1: ifeq +72 -> 73
/*      */     //   4: iconst_0
/*      */     //   5: istore_2
/*      */     //   6: aload_0
/*      */     //   7: dup
/*      */     //   8: astore_3
/*      */     //   9: monitorenter
/*      */     //   10: aload_0
/*      */     //   11: getfield 2608	com/aelitis/azureus/core/subs/impl/SubscriptionManagerImpl:subscriptions	Ljava/util/List;
/*      */     //   14: invokeinterface 3085 1 0
/*      */     //   19: astore 4
/*      */     //   21: aload 4
/*      */     //   23: invokeinterface 3077 1 0
/*      */     //   28: ifeq +31 -> 59
/*      */     //   31: aload 4
/*      */     //   33: invokeinterface 3078 1 0
/*      */     //   38: checkcast 1538	com/aelitis/azureus/core/subs/impl/SubscriptionImpl
/*      */     //   41: astore 5
/*      */     //   43: aload 5
/*      */     //   45: invokeinterface 3037 1 0
/*      */     //   50: ifeq +6 -> 56
/*      */     //   53: iinc 2 1
/*      */     //   56: goto -35 -> 21
/*      */     //   59: aload_3
/*      */     //   60: monitorexit
/*      */     //   61: goto +10 -> 71
/*      */     //   64: astore 6
/*      */     //   66: aload_3
/*      */     //   67: monitorexit
/*      */     //   68: aload 6
/*      */     //   70: athrow
/*      */     //   71: iload_2
/*      */     //   72: ireturn
/*      */     //   73: aload_0
/*      */     //   74: dup
/*      */     //   75: astore_2
/*      */     //   76: monitorenter
/*      */     //   77: aload_0
/*      */     //   78: getfield 2608	com/aelitis/azureus/core/subs/impl/SubscriptionManagerImpl:subscriptions	Ljava/util/List;
/*      */     //   81: invokeinterface 3079 1 0
/*      */     //   86: aload_2
/*      */     //   87: monitorexit
/*      */     //   88: ireturn
/*      */     //   89: astore 7
/*      */     //   91: aload_2
/*      */     //   92: monitorexit
/*      */     //   93: aload 7
/*      */     //   95: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2756	-> byte code offset #0
/*      */     //   Java source line #2758	-> byte code offset #4
/*      */     //   Java source line #2760	-> byte code offset #6
/*      */     //   Java source line #2762	-> byte code offset #10
/*      */     //   Java source line #2764	-> byte code offset #43
/*      */     //   Java source line #2766	-> byte code offset #53
/*      */     //   Java source line #2769	-> byte code offset #59
/*      */     //   Java source line #2771	-> byte code offset #71
/*      */     //   Java source line #2775	-> byte code offset #73
/*      */     //   Java source line #2777	-> byte code offset #77
/*      */     //   Java source line #2778	-> byte code offset #89
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	96	0	this	SubscriptionManagerImpl
/*      */     //   0	96	1	subscribed_only	boolean
/*      */     //   5	67	2	total	int
/*      */     //   75	17	2	Ljava/lang/Object;	Object
/*      */     //   8	59	3	Ljava/lang/Object;	Object
/*      */     //   19	13	4	i$	Iterator
/*      */     //   41	3	5	subs	Subscription
/*      */     //   64	5	6	localObject1	Object
/*      */     //   89	5	7	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   10	61	64	finally
/*      */     //   64	68	64	finally
/*      */     //   77	88	89	finally
/*      */     //   89	93	89	finally
/*      */   }
/*      */   
/*      */   static abstract interface subsLookupListener
/*      */     extends SubscriptionLookupListener
/*      */   {
/*      */     public abstract boolean isCancelled();
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/impl/SubscriptionManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */