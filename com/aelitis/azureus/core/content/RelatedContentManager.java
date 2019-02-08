/*      */ package com.aelitis.azureus.core.content;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginInterface;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginOperationListener;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginValue;
/*      */ import com.aelitis.azureus.util.ImportExportUtils;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.zip.GZIPOutputStream;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap.Entry;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchException;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchInstance;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchObserver;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ddb.DDBaseImpl;
/*      */ 
/*      */ public class RelatedContentManager
/*      */ {
/*      */   public static final long FILE_ASSOC_MIN_SIZE = 52428800L;
/*      */   public static final int RCM_SEARCH_PROPERTY_CONTENT_NETWORK = 50000;
/*      */   public static final int RCM_SEARCH_PROPERTY_TRACKER_KEYS = 50001;
/*      */   public static final int RCM_SEARCH_PROPERTY_WEB_SEED_KEYS = 50002;
/*      */   public static final int RCM_SEARCH_PROPERTY_TAGS = 50003;
/*      */   public static final int RCM_SEARCH_PROPERTY_NETWORKS = 50004;
/*      */   private static final boolean TRACE = false;
/*      */   private static final int MAX_HISTORY = 16;
/*      */   private static final int MAX_TITLE_LENGTH = 80;
/*      */   private static final int MAX_CONCURRENT_PUBLISH;
/*      */   private static final boolean DISABLE_PUBLISHING;
/*      */   private static final int TEMPORARY_SPACE_DELTA = 50;
/*      */   private static final int MAX_RANK = 100;
/*      */   private static final String CONFIG_FILE = "rcm.config";
/*      */   private static final String PERSIST_DEL_FILE = "rcmx.config";
/*      */   private static final String CONFIG_TOTAL_UNREAD = "rcm.numunread.cache";
/*      */   private static RelatedContentManager singleton;
/*      */   private static AzureusCore core;
/*      */   protected static final int TIMER_PERIOD = 30000;
/*      */   private static final int CONFIG_SAVE_CHECK_PERIOD = 60000;
/*      */   private static final int CONFIG_SAVE_PERIOD = 300000;
/*      */   private static final int CONFIG_SAVE_CHECK_TICKS = 2;
/*      */   private static final int CONFIG_SAVE_TICKS = 10;
/*      */   private static final int PUBLISH_CHECK_PERIOD = 30000;
/*      */   private static final int PUBLISH_CHECK_TICKS = 1;
/*      */   private static final int PUBLISH_SLEEPING_CHECK_PERIOD = 300000;
/*      */   private static final int PUBLISH_SLEEPING_CHECK_TICKS = 10;
/*      */   private static final int SECONDARY_LOOKUP_PERIOD = 900000;
/*      */   private static final int SECONDARY_LOOKUP_TICKS = 30;
/*      */   private static final int REPUBLISH_PERIOD = 28800000;
/*      */   private static final int REPUBLISH_TICKS = 960;
/*      */   private static final int I2P_SEARCHER_CHECK_PERIOD = 600000;
/*      */   private static final int I2P_SEARCHER_CHECK_TICKS = 20;
/*      */   private static final int INITIAL_PUBLISH_DELAY = 180000;
/*      */   private static final int INITIAL_PUBLISH_TICKS = 6;
/*      */   private static final int CONFIG_DISCARD_MILLIS = 60000;
/*      */   protected static final byte NET_NONE = 0;
/*      */   protected static final byte NET_PUBLIC = 1;
/*      */   protected static final byte NET_I2P = 2;
/*      */   protected static final byte NET_TOR = 4;
/*      */   
/*      */   static
/*      */   {
/*  114 */     int max_conc_pub = 2;
/*      */     
/*  116 */     DISABLE_PUBLISHING = System.getProperty("azureus.rcm.publish.disable", "0").equals("1");
/*      */     
/*      */     try
/*      */     {
/*  120 */       max_conc_pub = Integer.parseInt(System.getProperty("azureus.rcm.max.concurrent.publish", "" + max_conc_pub));
/*      */     }
/*      */     catch (Throwable e) {
/*  123 */       Debug.out(e);
/*      */     }
/*      */     
/*  126 */     MAX_CONCURRENT_PUBLISH = max_conc_pub;
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
/*  173 */   private static final String[] NET_PUBLIC_ARRAY = { "Public" };
/*  174 */   private static final String[] NET_I2P_ARRAY = { "I2P" };
/*  175 */   private static final String[] NET_TOR_ARRAY = { "Tor" };
/*  176 */   private static final String[] NET_PUBLIC_AND_I2P_ARRAY = { "Public", "I2P" };
/*      */   
/*      */ 
/*      */ 
/*      */   public static synchronized void preInitialise(AzureusCore _core)
/*      */   {
/*  182 */     core = _core;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static synchronized RelatedContentManager getSingleton()
/*      */     throws ContentException
/*      */   {
/*  190 */     if (singleton == null)
/*      */     {
/*  192 */       singleton = new RelatedContentManager();
/*      */     }
/*      */     
/*  195 */     return singleton;
/*      */   }
/*      */   
/*  198 */   protected final Object rcm_lock = new Object();
/*      */   
/*      */   private PluginInterface plugin_interface;
/*      */   
/*      */   private org.gudy.azureus2.plugins.torrent.TorrentAttribute ta_networks;
/*      */   private org.gudy.azureus2.plugins.torrent.TorrentAttribute ta_category;
/*      */   private DHTPluginInterface public_dht_plugin;
/*  205 */   private volatile Map<Byte, DHTPluginInterface> i2p_dht_plugin_map = new HashMap();
/*      */   
/*      */   private TagManager tag_manager;
/*      */   
/*  209 */   private long global_random_id = -1L;
/*      */   
/*  211 */   private LinkedList<DownloadInfo> pub_download_infos1 = new LinkedList();
/*  212 */   private LinkedList<DownloadInfo> pub_download_infos2 = new LinkedList();
/*      */   
/*  214 */   private LinkedList<DownloadInfo> non_pub_download_infos1 = new LinkedList();
/*  215 */   private LinkedList<DownloadInfo> non_pub_download_infos2 = new LinkedList();
/*      */   
/*  217 */   private ByteArrayHashMapEx<DownloadInfo> download_info_map = new ByteArrayHashMapEx();
/*  218 */   private Set<String> download_priv_set = new HashSet();
/*      */   
/*      */   private final boolean enabled;
/*      */   
/*      */   private int max_search_level;
/*      */   
/*      */   private int max_results;
/*      */   
/*  226 */   private AtomicInteger temporary_space = new AtomicInteger();
/*      */   
/*  228 */   private int publishing_count = 0;
/*      */   
/*  230 */   private CopyOnWriteList<RelatedContentManagerListener> listeners = new CopyOnWriteList();
/*      */   
/*  232 */   private AESemaphore initialisation_complete_sem = new AESemaphore("RCM:init");
/*      */   
/*      */   private ContentCache content_cache_ref;
/*      */   
/*      */   private WeakReference<ContentCache> content_cache;
/*      */   
/*      */   private boolean content_dirty;
/*      */   private long last_config_access;
/*      */   private int content_discard_ticks;
/*  241 */   private AtomicInteger total_unread = new AtomicInteger(COConfigurationManager.getIntParameter("rcm.numunread.cache", 0));
/*      */   
/*  243 */   private AsyncDispatcher content_change_dispatcher = new AsyncDispatcher();
/*      */   
/*      */   private static final int SECONDARY_LOOKUP_CACHE_MAX = 10;
/*      */   
/*  247 */   private LinkedList<SecondaryLookup> secondary_lookups = new LinkedList();
/*      */   
/*      */   private boolean secondary_lookup_in_progress;
/*      */   
/*      */   private long secondary_lookup_complete_time;
/*  252 */   private RCMSearchXFer transfer_type = new RCMSearchXFer();
/*      */   
/*  254 */   private final CopyOnWriteList<RelatedContentSearcher> searchers = new CopyOnWriteList();
/*      */   
/*      */   private boolean added_i2p_searcher;
/*      */   
/*      */   private static final int MAX_TRANSIENT_CACHE = 256;
/*  259 */   protected static Map<String, DownloadInfo> transient_info_cache = new LinkedHashMap(256, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry<String, RelatedContentManager.DownloadInfo> eldest)
/*      */     {
/*      */ 
/*  266 */       return size() > 256;
/*      */     }
/*      */   };
/*      */   private boolean persist;
/*      */   private boolean last_pub_was_pub;
/*      */   private static final int MAX_TAG_LENGTH = 20;
/*      */   
/*      */   protected RelatedContentManager() throws ContentException {
/*  274 */     COConfigurationManager.addAndFireParameterListener("rcm.persist", new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*  282 */         if (!COConfigurationManager.getBooleanParameter("rcm.persist")) {} RelatedContentManager.this.persist = true;
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  287 */     });
/*  288 */     COConfigurationManager.removeParameter("rcm.dlinfo.history");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  296 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "rcm.ui.enabled", "rcm.max_search_level", "rcm.max_results" }, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  308 */         RelatedContentManager.this.max_search_level = COConfigurationManager.getIntParameter("rcm.max_search_level", 3);
/*  309 */         RelatedContentManager.this.max_results = COConfigurationManager.getIntParameter("rcm.max_results", 500);
/*      */       }
/*      */     });
/*      */     
/*  313 */     if ((!com.aelitis.azureus.core.util.FeatureAvailability.isRCMEnabled()) || (!COConfigurationManager.getBooleanParameter("rcm.overall.enabled", true)))
/*      */     {
/*      */ 
/*  316 */       this.enabled = false;
/*      */       
/*  318 */       deleteRelatedContent();
/*      */       
/*  320 */       this.initialisation_complete_sem.releaseForever();
/*      */       
/*  322 */       return;
/*      */     }
/*      */     
/*  325 */     this.enabled = true;
/*      */     try
/*      */     {
/*  328 */       if (core == null)
/*      */       {
/*  330 */         throw new ContentException("getSingleton called before pre-initialisation");
/*      */       }
/*      */       
/*  333 */       while (this.global_random_id == -1L)
/*      */       {
/*  335 */         this.global_random_id = COConfigurationManager.getLongParameter("rcm.random.id", -1L);
/*      */         
/*  337 */         if (this.global_random_id == -1L)
/*      */         {
/*  339 */           this.global_random_id = RandomUtils.nextLong();
/*      */           
/*  341 */           COConfigurationManager.setParameter("rcm.random.id", this.global_random_id);
/*      */         }
/*      */       }
/*      */       
/*  345 */       this.plugin_interface = core.getPluginManager().getDefaultPluginInterface();
/*      */       
/*  347 */       this.ta_networks = this.plugin_interface.getTorrentManager().getAttribute("Networks");
/*  348 */       this.ta_category = this.plugin_interface.getTorrentManager().getAttribute("Category");
/*      */       
/*  350 */       this.tag_manager = com.aelitis.azureus.core.tag.TagManagerFactory.getTagManager();
/*      */       
/*  352 */       this.plugin_interface.getUtilities().createDelayedTask(new AERunnable() {
/*      */         public void runSupport() {
/*  354 */           SimpleTimer.addEvent("rcm.delay.init", SystemTime.getOffsetTime(15000L), new TimerEventPerformer()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void perform(TimerEvent event)
/*      */             {
/*      */ 
/*      */ 
/*  363 */               RelatedContentManager.this.delayedInit();
/*      */             }
/*      */           });
/*      */         }
/*      */       }).queue();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  371 */       this.initialisation_complete_sem.releaseForever();
/*      */       
/*  373 */       if ((e instanceof ContentException))
/*      */       {
/*  375 */         throw ((ContentException)e);
/*      */       }
/*      */       
/*  378 */       throw new ContentException("Initialisation failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected PluginInterface getPluginInterface()
/*      */   {
/*  385 */     return this.plugin_interface;
/*      */   }
/*      */   
/*      */   private void delayedInit()
/*      */   {
/*  390 */     this.plugin_interface.addListener(new org.gudy.azureus2.plugins.PluginListener()
/*      */     {
/*      */ 
/*      */       public void initializationComplete()
/*      */       {
/*      */ 
/*  396 */         if (!RelatedContentManager.this.persist)
/*      */         {
/*  398 */           RelatedContentManager.this.deleteRelatedContent();
/*      */         }
/*      */         try
/*      */         {
/*  402 */           PluginInterface dht_pi = RelatedContentManager.this.plugin_interface.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*      */           
/*      */ 
/*      */ 
/*  406 */           if (dht_pi != null)
/*      */           {
/*  408 */             DHTPlugin dp = (DHTPlugin)dht_pi.getPlugin();
/*      */             
/*  410 */             RelatedContentManager.this.public_dht_plugin = dp;
/*      */             
/*  412 */             RelatedContentSearcher public_searcher = new RelatedContentSearcher(RelatedContentManager.this, RelatedContentManager.this.transfer_type, dp, true);
/*      */             
/*  414 */             RelatedContentManager.this.searchers.add(public_searcher);
/*      */             
/*  416 */             org.gudy.azureus2.plugins.download.DownloadManager dm = RelatedContentManager.this.plugin_interface.getDownloadManager();
/*      */             
/*  418 */             Download[] downloads = dm.getDownloads();
/*      */             
/*  420 */             RelatedContentManager.this.addDownloads(downloads, true);
/*      */             
/*  422 */             dm.addListener(new org.gudy.azureus2.plugins.download.DownloadManagerListener()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void downloadAdded(Download download) {
/*  429 */                 RelatedContentManager.this.addDownloads(new Download[] { download }, false); } public void downloadRemoved(Download download) {} }, false);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  440 */             SimpleTimer.addPeriodicEvent("RCM:publisher", 30000L, new TimerEventPerformer()
/*      */             {
/*      */               private int tick_count;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void perform(TimerEvent event)
/*      */               {
/*  451 */                 this.tick_count += 1;
/*      */                 
/*  453 */                 if ((this.tick_count == 1) || (this.tick_count % 20 == 0))
/*      */                 {
/*  455 */                   RelatedContentManager.this.checkI2PSearcher(false);
/*      */                 }
/*      */                 
/*  458 */                 if (RelatedContentManager.this.enabled)
/*      */                 {
/*  460 */                   if (this.tick_count >= 6)
/*      */                   {
/*  462 */                     if (this.tick_count % (RelatedContentManager.this.public_dht_plugin.isSleeping() ? 10 : 1) == 0)
/*      */                     {
/*  464 */                       RelatedContentManager.this.publish();
/*      */                     }
/*      */                     
/*  467 */                     if (this.tick_count % 30 == 0)
/*      */                     {
/*  469 */                       RelatedContentManager.this.secondaryLookup();
/*      */                     }
/*      */                     
/*  472 */                     if (this.tick_count % 960 == 0)
/*      */                     {
/*  474 */                       RelatedContentManager.this.republish();
/*      */                     }
/*      */                     
/*  477 */                     if (this.tick_count % 2 == 0)
/*      */                     {
/*  479 */                       RelatedContentManager.this.saveRelatedContent(this.tick_count);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*  484 */                 for (RelatedContentSearcher searcher : RelatedContentManager.this.searchers)
/*      */                 {
/*  486 */                   searcher.timerTick(RelatedContentManager.this.enabled, this.tick_count);
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */         finally {
/*  493 */           RelatedContentManager.this.initialisation_complete_sem.releaseForever();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void closedownInitiated()
/*      */       {
/*  500 */         RelatedContentManager.this.saveRelatedContent(0);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void closedownComplete() {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkI2PSearcher(boolean force)
/*      */   {
/*  516 */     synchronized (this.searchers)
/*      */     {
/*  518 */       if (this.added_i2p_searcher)
/*      */       {
/*  520 */         return;
/*      */       }
/*      */       
/*  523 */       if (!force)
/*      */       {
/*  525 */         org.gudy.azureus2.plugins.download.DownloadManager dm = this.plugin_interface.getDownloadManager();
/*      */         
/*  527 */         Download[] downloads = dm.getDownloads();
/*      */         
/*  529 */         boolean found = false;
/*      */         
/*  531 */         for (Download download : downloads)
/*      */         {
/*  533 */           String[] nets = PluginCoreUtils.unwrap(download).getDownloadState().getNetworks();
/*      */           
/*  535 */           if ((nets.length == 1) && (nets[0] == "I2P"))
/*      */           {
/*  537 */             found = true;
/*      */             
/*  539 */             break;
/*      */           }
/*      */         }
/*      */         
/*  543 */         if (!found)
/*      */         {
/*  545 */           return;
/*      */         }
/*      */       }
/*      */       
/*  549 */       List<DistributedDatabase> ddbs = DDBaseImpl.getDDBs(new String[] { "I2P" });
/*      */       
/*  551 */       for (DistributedDatabase ddb : ddbs)
/*      */       {
/*  553 */         if (ddb.getNetwork() == "I2P")
/*      */         {
/*  555 */           DHTPluginInterface i2p_dht = ddb.getDHTPlugin();
/*      */           
/*  557 */           RelatedContentSearcher i2p_searcher = new RelatedContentSearcher(this, this.transfer_type, i2p_dht, false);
/*      */           
/*  559 */           this.searchers.add(i2p_searcher);
/*      */           
/*  561 */           this.added_i2p_searcher = true;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/*  570 */     return this.enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxSearchLevel()
/*      */   {
/*  576 */     return this.max_search_level;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaxSearchLevel(int _level)
/*      */   {
/*  583 */     COConfigurationManager.setParameter("rcm.max_search_level", _level);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxResults()
/*      */   {
/*  589 */     return this.max_results;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaxResults(int _max)
/*      */   {
/*  596 */     COConfigurationManager.setParameter("rcm.max_results", _max);
/*      */     
/*  598 */     enforceMaxResults(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int[] getAggregateSeedsLeechers(DownloadManagerState state)
/*      */   {
/*  605 */     String cache = state.getAttribute("agsc");
/*      */     
/*  607 */     int[] result = null;
/*      */     
/*  609 */     if (cache != null)
/*      */     {
/*  611 */       String[] bits = cache.split(",");
/*      */       
/*  613 */       if (bits.length == 3) {
/*      */         try
/*      */         {
/*  616 */           long updated_mins = Long.parseLong(bits[0]);
/*      */           
/*  618 */           long mins = SystemTime.getCurrentTime() / 60000L;
/*      */           
/*  620 */           long age_mins = mins - updated_mins;
/*      */           
/*  622 */           long WEEK_MINS = 10080L;
/*      */           
/*  624 */           if (age_mins <= WEEK_MINS)
/*      */           {
/*  626 */             int seeds = Integer.parseInt(bits[1]);
/*  627 */             int peers = Integer.parseInt(bits[2]);
/*      */             
/*  629 */             if ((seeds >= 0) && (peers >= 0))
/*      */             {
/*  631 */               result = new int[] { seeds, peers };
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  640 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private DHTPluginInterface selectDHT(byte networks)
/*      */   {
/*  647 */     DHTPluginInterface result = null;
/*      */     
/*  649 */     if ((networks & 0x1) != 0)
/*      */     {
/*  651 */       result = this.public_dht_plugin;
/*      */     }
/*  653 */     else if ((networks & 0x2) != 0)
/*      */     {
/*  655 */       synchronized (this.i2p_dht_plugin_map)
/*      */       {
/*  657 */         result = (DHTPluginInterface)this.i2p_dht_plugin_map.get(Byte.valueOf(networks));
/*      */         
/*  659 */         if ((result == null) && (!this.i2p_dht_plugin_map.containsKey(Byte.valueOf(networks))))
/*      */         {
/*      */           try
/*      */           {
/*  663 */             List<DistributedDatabase> ddbs = DDBaseImpl.getDDBs(convertNetworks(networks));
/*      */             
/*  665 */             for (DistributedDatabase ddb : ddbs)
/*      */             {
/*  667 */               if (ddb.getNetwork() == "I2P")
/*      */               {
/*  669 */                 result = ddb.getDHTPlugin();
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/*  674 */             this.i2p_dht_plugin_map.put(Byte.valueOf(networks), result);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  680 */     if (result != null)
/*      */     {
/*  682 */       if (!result.isEnabled())
/*      */       {
/*  684 */         result = null;
/*      */       }
/*      */     }
/*      */     
/*  688 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addDownloads(Download[] downloads, boolean initialising)
/*      */   {
/*  696 */     synchronized (this.rcm_lock)
/*      */     {
/*  698 */       List<DownloadInfo> new_info = new ArrayList(downloads.length);
/*      */       
/*  700 */       for (Download download : downloads) {
/*      */         try
/*      */         {
/*  703 */           if (download.isPersistent())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  708 */             Torrent torrent = download.getTorrent();
/*      */             
/*  710 */             if (torrent != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  715 */               byte[] hash = torrent.getHash();
/*      */               
/*  717 */               if (!this.download_info_map.containsKey(hash))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  722 */                 byte nets = getNetworks(download);
/*      */                 
/*  724 */                 if (nets != 0)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*  729 */                   TOTorrent to_torrent = PluginCoreUtils.unwrap(torrent);
/*      */                   
/*  731 */                   if (!org.gudy.azureus2.core3.util.TorrentUtils.isReallyPrivate(to_torrent))
/*      */                   {
/*  733 */                     DownloadManagerState state = PluginCoreUtils.unwrap(download).getDownloadState();
/*      */                     
/*  735 */                     if ((!state.getFlag(16L)) && (state.getFlag(512L))) {
/*      */                       continue;
/*      */                     }
/*      */                     
/*      */                     LinkedList<DownloadInfo> download_infos2;
/*      */                     
/*      */                     LinkedList<DownloadInfo> download_infos1;
/*      */                     LinkedList<DownloadInfo> download_infos2;
/*  743 */                     if ((nets & 0x1) != 0)
/*      */                     {
/*  745 */                       LinkedList<DownloadInfo> download_infos1 = this.pub_download_infos1;
/*  746 */                       download_infos2 = this.pub_download_infos2;
/*      */                     }
/*      */                     else
/*      */                     {
/*  750 */                       download_infos1 = this.non_pub_download_infos1;
/*  751 */                       download_infos2 = this.non_pub_download_infos2;
/*      */                     }
/*      */                     
/*  754 */                     int version = 0;
/*      */                     
/*  756 */                     long rand = this.global_random_id ^ state.getLongParameter("rand");
/*      */                     
/*      */ 
/*      */ 
/*  760 */                     int[] aggregate_seeds_leechers = getAggregateSeedsLeechers(state);
/*      */                     int seeds_leechers;
/*  762 */                     int seeds_leechers; if (aggregate_seeds_leechers == null)
/*      */                     {
/*  764 */                       long cache = state.getLongAttribute("scrapecache");
/*      */                       int seeds_leechers;
/*  766 */                       if (cache == -1L)
/*      */                       {
/*  768 */                         seeds_leechers = -1;
/*      */                       }
/*      */                       else
/*      */                       {
/*  772 */                         int seeds = (int)(cache >> 32 & 0xFFFFFF);
/*  773 */                         int leechers = (int)(cache & 0xFFFFFF);
/*      */                         
/*  775 */                         seeds_leechers = seeds << 16 | leechers & 0xFFFF;
/*      */                       }
/*      */                     }
/*      */                     else {
/*  779 */                       version = 1;
/*      */                       
/*  781 */                       int seeds = aggregate_seeds_leechers[0];
/*  782 */                       int leechers = aggregate_seeds_leechers[1];
/*      */                       
/*  784 */                       seeds_leechers = seeds << 16 | leechers & 0xFFFF;
/*      */                     }
/*      */                     
/*  787 */                     byte[][] keys = getKeys(download);
/*      */                     
/*  789 */                     DownloadInfo info = new DownloadInfo(version, hash, hash, download.getName(), (int)rand, torrent.isPrivate() ? org.gudy.azureus2.core3.util.StringInterner.intern(torrent.getAnnounceURL().getHost()) : null, keys[0], keys[1], getTags(download), nets, 0, false, torrent.getSize(), (int)(to_torrent.getCreationDate() / 3600L), seeds_leechers, (byte)(int)com.aelitis.azureus.core.torrent.PlatformTorrentUtils.getContentNetworkID(to_torrent));
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  808 */                     new_info.add(info);
/*      */                     
/*  810 */                     if ((initialising) || (download_infos1.size() == 0))
/*      */                     {
/*  812 */                       download_infos1.add(info);
/*      */                     }
/*      */                     else
/*      */                     {
/*  816 */                       download_infos1.add(RandomUtils.nextInt(download_infos1.size()), info);
/*      */                     }
/*      */                     
/*  819 */                     download_infos2.add(info);
/*      */                     
/*  821 */                     this.download_info_map.put(hash, info);
/*      */                     
/*  823 */                     if (info.getTracker() != null)
/*      */                     {
/*  825 */                       this.download_priv_set.add(getPrivateInfoKey(info)); }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*  830 */           } } catch (Throwable e) { Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/*  834 */       List<Map<String, Object>> history = COConfigurationManager.getListParameter("rcm.dlinfo.history.privx", new ArrayList());
/*      */       
/*  836 */       if (initialising)
/*      */       {
/*  838 */         int padd = 16 - this.download_info_map.size();
/*      */         
/*  840 */         for (int i = 0; (i < history.size()) && (padd > 0); i++) {
/*      */           try
/*      */           {
/*  843 */             DownloadInfo info = deserialiseDI((Map)history.get(i), null);
/*      */             
/*  845 */             if ((info != null) && (!this.download_info_map.containsKey(info.getHash())))
/*      */             {
/*  847 */               this.download_info_map.put(info.getHash(), info);
/*      */               
/*  849 */               if (info.getTracker() != null)
/*      */               {
/*  851 */                 this.download_priv_set.add(getPrivateInfoKey(info));
/*      */               }
/*      */               
/*  854 */               byte nets = info.getNetworksInternal();
/*      */               
/*  856 */               if (nets != 0)
/*      */               {
/*  858 */                 if ((nets & 0x1) != 0)
/*      */                 {
/*  860 */                   this.pub_download_infos1.add(info);
/*  861 */                   this.pub_download_infos2.add(info);
/*      */                 }
/*      */                 else
/*      */                 {
/*  865 */                   this.non_pub_download_infos1.add(info);
/*  866 */                   this.non_pub_download_infos2.add(info);
/*      */                 }
/*      */                 
/*  869 */                 padd--;
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/*  876 */         Collections.shuffle(this.pub_download_infos1);
/*  877 */         Collections.shuffle(this.non_pub_download_infos1);
/*      */ 
/*      */ 
/*      */       }
/*  881 */       else if (new_info.size() > 0)
/*      */       {
/*  883 */         final List<String> base32_hashes = new ArrayList();
/*      */         
/*  885 */         for (DownloadInfo info : new_info)
/*      */         {
/*  887 */           byte[] hash = info.getHash();
/*      */           
/*  889 */           if (hash != null)
/*      */           {
/*  891 */             base32_hashes.add(Base32.encode(hash));
/*      */           }
/*      */           
/*  894 */           Map<String, Object> map = serialiseDI(info, null);
/*      */           
/*  896 */           if (map != null)
/*      */           {
/*  898 */             history.add(map);
/*      */           }
/*      */         }
/*      */         
/*  902 */         while (history.size() > 16)
/*      */         {
/*  904 */           history.remove(0);
/*      */         }
/*      */         
/*  907 */         COConfigurationManager.setParameter("rcm.dlinfo.history.privx", history);
/*      */         
/*  909 */         if (base32_hashes.size() > 0)
/*      */         {
/*  911 */           this.content_change_dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/*  917 */               List<RelatedContent> to_remove = new ArrayList();
/*      */               RelatedContentManager.ContentCache content_cache;
/*  919 */               synchronized (RelatedContentManager.this.rcm_lock)
/*      */               {
/*  921 */                 content_cache = RelatedContentManager.this.loadRelatedContent();
/*      */                 
/*  923 */                 for (String h : base32_hashes)
/*      */                 {
/*  925 */                   RelatedContentManager.DownloadInfo di = (RelatedContentManager.DownloadInfo)content_cache.related_content.get(h);
/*      */                   
/*  927 */                   if (di != null)
/*      */                   {
/*  929 */                     to_remove.add(di);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*  934 */               if (to_remove.size() > 0)
/*      */               {
/*  936 */                 RelatedContentManager.this.delete((RelatedContent[])to_remove.toArray(new RelatedContent[to_remove.size()]));
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void republish()
/*      */   {
/*  949 */     if (DISABLE_PUBLISHING)
/*      */     {
/*  951 */       return;
/*      */     }
/*      */     
/*  954 */     synchronized (this.rcm_lock)
/*      */     {
/*  956 */       if (this.publishing_count > 0)
/*      */       {
/*  958 */         return;
/*      */       }
/*      */       
/*  961 */       if ((this.pub_download_infos1.isEmpty()) || ((this.pub_download_infos1.size() == 1) && (this.pub_download_infos1.getFirst() == this.pub_download_infos2.getFirst())))
/*      */       {
/*      */ 
/*      */ 
/*  965 */         this.pub_download_infos1.clear();
/*  966 */         this.pub_download_infos2.clear();
/*      */         
/*  968 */         List<DownloadInfo> list = this.download_info_map.values();
/*      */         
/*  970 */         for (DownloadInfo info : list)
/*      */         {
/*  972 */           if ((info.getNetworksInternal() & 0x1) != 0)
/*      */           {
/*  974 */             this.pub_download_infos1.add(info);
/*  975 */             this.pub_download_infos2.add(info);
/*      */           }
/*      */         }
/*      */         
/*  979 */         Collections.shuffle(this.pub_download_infos1);
/*      */       }
/*      */       
/*  982 */       if ((this.non_pub_download_infos1.isEmpty()) || ((this.non_pub_download_infos1.size() == 1) && (this.non_pub_download_infos1.getFirst() == this.non_pub_download_infos2.getFirst())))
/*      */       {
/*      */ 
/*      */ 
/*  986 */         this.non_pub_download_infos1.clear();
/*  987 */         this.non_pub_download_infos2.clear();
/*      */         
/*  989 */         List<DownloadInfo> list = this.download_info_map.values();
/*      */         
/*  991 */         for (DownloadInfo info : list)
/*      */         {
/*  993 */           byte nets = info.getNetworksInternal();
/*      */           
/*  995 */           if (nets != 0)
/*      */           {
/*  997 */             if ((nets & 0x1) == 0)
/*      */             {
/*  999 */               this.non_pub_download_infos1.add(info);
/* 1000 */               this.non_pub_download_infos2.add(info);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1005 */         Collections.shuffle(this.non_pub_download_infos1);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void publish()
/*      */   {
/* 1015 */     if (DISABLE_PUBLISHING)
/*      */     {
/* 1017 */       return;
/*      */     }
/*      */     
/*      */     for (;;)
/*      */     {
/* 1022 */       DownloadInfo info1 = null;
/* 1023 */       DownloadInfo info2 = null;
/*      */       
/* 1025 */       synchronized (this.rcm_lock)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1031 */         if (this.publishing_count >= MAX_CONCURRENT_PUBLISH)
/*      */         {
/*      */ 
/*      */ 
/* 1035 */           return;
/*      */         }
/*      */         
/* 1038 */         if (this.download_info_map.size() == 1)
/*      */         {
/*      */ 
/*      */ 
/* 1042 */           return;
/*      */         }
/*      */         
/* 1045 */         boolean pub_ok = false;
/*      */         
/* 1047 */         if ((!this.pub_download_infos1.isEmpty()) && ((this.pub_download_infos1.size() != 1) || (this.pub_download_infos1.getFirst() != this.pub_download_infos2.getFirst())))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1055 */           pub_ok = true;
/*      */         }
/*      */         
/* 1058 */         boolean non_pub_ok = false;
/*      */         
/* 1060 */         if ((!this.non_pub_download_infos1.isEmpty()) && ((this.non_pub_download_infos1.size() != 1) || (this.non_pub_download_infos1.getFirst() != this.non_pub_download_infos2.getFirst())))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1068 */           non_pub_ok = true;
/*      */         }
/*      */         
/* 1071 */         if ((!pub_ok) && (!non_pub_ok))
/*      */         {
/* 1073 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1079 */         if ((pub_ok) && (non_pub_ok))
/*      */         {
/* 1081 */           if (this.last_pub_was_pub)
/*      */           {
/* 1083 */             pub_ok = false;
/*      */           }
/*      */           
/* 1086 */           this.last_pub_was_pub = (!this.last_pub_was_pub); }
/*      */         LinkedList<DownloadInfo> download_infos2;
/*      */         LinkedList<DownloadInfo> download_infos1;
/* 1089 */         LinkedList<DownloadInfo> download_infos2; if (pub_ok)
/*      */         {
/* 1091 */           LinkedList<DownloadInfo> download_infos1 = this.pub_download_infos1;
/* 1092 */           download_infos2 = this.pub_download_infos2;
/*      */         }
/*      */         else
/*      */         {
/* 1096 */           download_infos1 = this.non_pub_download_infos1;
/* 1097 */           download_infos2 = this.non_pub_download_infos2;
/*      */         }
/*      */         
/* 1100 */         if ((download_infos1.isEmpty()) || (this.download_info_map.size() == 1))
/*      */         {
/* 1102 */           return;
/*      */         }
/*      */         
/* 1105 */         info1 = (DownloadInfo)download_infos1.removeFirst();
/*      */         
/* 1107 */         Iterator<DownloadInfo> it = download_infos2.iterator();
/*      */         
/* 1109 */         while (it.hasNext())
/*      */         {
/* 1111 */           info2 = (DownloadInfo)it.next();
/*      */           
/* 1113 */           if ((info1 != info2) || (download_infos2.size() == 1))
/*      */           {
/* 1115 */             it.remove();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1121 */         if (info1 == info2)
/*      */         {
/* 1123 */           return;
/*      */         }
/*      */         
/* 1126 */         this.publishing_count += 1;
/*      */       }
/*      */       try
/*      */       {
/* 1130 */         if (!publish(info1, info2))
/*      */         {
/* 1132 */           synchronized (this.rcm_lock)
/*      */           {
/* 1134 */             this.publishing_count -= 1;
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1139 */         synchronized (this.rcm_lock)
/*      */         {
/* 1141 */           this.publishing_count -= 1;
/*      */         }
/*      */         
/* 1144 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void publishNext()
/*      */   {
/* 1152 */     synchronized (this.rcm_lock)
/*      */     {
/* 1154 */       this.publishing_count -= 1;
/*      */       
/* 1156 */       if (this.publishing_count < 0)
/*      */       {
/*      */ 
/*      */ 
/* 1160 */         this.publishing_count = 0;
/*      */       }
/*      */     }
/*      */     
/* 1164 */     publish();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean publish(final DownloadInfo from_info, final DownloadInfo to_info)
/*      */     throws Exception
/*      */   {
/* 1174 */     final DHTPluginInterface dht_plugin = selectDHT(from_info.getNetworksInternal());
/*      */     
/*      */ 
/*      */ 
/* 1178 */     if (dht_plugin == null)
/*      */     {
/* 1180 */       return false;
/*      */     }
/*      */     
/* 1183 */     final String from_hash = ByteFormatter.encodeString(from_info.getHash());
/* 1184 */     final String to_hash = ByteFormatter.encodeString(to_info.getHash());
/*      */     
/* 1186 */     final byte[] key_bytes = ("az:rcm:assoc:" + from_hash).getBytes("UTF-8");
/*      */     
/* 1188 */     String title = to_info.getTitle();
/*      */     
/* 1190 */     if (title.length() > 80)
/*      */     {
/* 1192 */       title = title.substring(0, 80);
/*      */     }
/*      */     
/* 1195 */     Map<String, Object> map = new HashMap();
/*      */     
/* 1197 */     map.put("d", title);
/* 1198 */     map.put("r", new Long(Math.abs(to_info.getRand() % 1000)));
/*      */     
/* 1200 */     String tracker = to_info.getTracker();
/*      */     
/* 1202 */     if (tracker == null)
/*      */     {
/* 1204 */       map.put("h", to_info.getHash());
/*      */     }
/*      */     else
/*      */     {
/* 1208 */       map.put("t", tracker);
/*      */     }
/*      */     
/* 1211 */     if (to_info.getLevel() == 0) {
/*      */       try
/*      */       {
/* 1214 */         Download d = to_info.getRelatedToDownload();
/*      */         
/* 1216 */         if (d != null)
/*      */         {
/* 1218 */           int version = 0;
/*      */           
/* 1220 */           Torrent torrent = d.getTorrent();
/*      */           
/* 1222 */           if (torrent != null)
/*      */           {
/* 1224 */             long cnet = com.aelitis.azureus.core.torrent.PlatformTorrentUtils.getContentNetworkID(PluginCoreUtils.unwrap(torrent));
/*      */             
/* 1226 */             if (cnet != -1L)
/*      */             {
/* 1228 */               map.put("c", new Long(cnet));
/*      */             }
/*      */             
/* 1231 */             long secs = torrent.getCreationDate();
/*      */             
/* 1233 */             long hours = secs / 3600L;
/*      */             
/* 1235 */             if (hours > 0L)
/*      */             {
/* 1237 */               map.put("p", new Long(hours));
/*      */             }
/*      */           }
/*      */           
/* 1241 */           DownloadManagerState state = PluginCoreUtils.unwrap(d).getDownloadState();
/*      */           
/* 1243 */           int leechers = -1;
/* 1244 */           int seeds = -1;
/*      */           
/* 1246 */           int[] aggregate_seeds_leechers = getAggregateSeedsLeechers(state);
/*      */           
/* 1248 */           if (aggregate_seeds_leechers == null)
/*      */           {
/* 1250 */             long cache = state.getLongAttribute("scrapecache");
/*      */             
/* 1252 */             if (cache != -1L)
/*      */             {
/* 1254 */               seeds = (int)(cache >> 32 & 0xFFFFFF);
/* 1255 */               leechers = (int)(cache & 0xFFFFFF);
/*      */             }
/*      */           }
/*      */           else {
/* 1259 */             seeds = aggregate_seeds_leechers[0];
/* 1260 */             leechers = aggregate_seeds_leechers[1];
/*      */             
/* 1262 */             version = 1;
/*      */           }
/*      */           
/* 1265 */           if (version > 0) {
/* 1266 */             map.put("v", new Long(version));
/*      */           }
/*      */           
/* 1269 */           if (leechers > 0) {
/* 1270 */             map.put("l", new Long(leechers));
/*      */           }
/* 1272 */           if (seeds > 0) {
/* 1273 */             map.put("z", new Long(seeds));
/*      */           }
/*      */           
/* 1276 */           byte[][] keys = getKeys(d);
/*      */           
/* 1278 */           if (keys[0] != null) {
/* 1279 */             map.put("k", keys[0]);
/*      */           }
/* 1281 */           if (keys[1] != null) {
/* 1282 */             map.put("w", keys[1]);
/*      */           }
/*      */           
/* 1285 */           String[] _tags = getTags(d);
/*      */           
/* 1287 */           if (_tags != null) {
/* 1288 */             map.put("g", encodeTags(_tags));
/*      */           }
/*      */           
/* 1291 */           byte nets = getNetworks(d);
/*      */           
/* 1293 */           if (nets != 1) {
/* 1294 */             map.put("o", new Long(nets & 0xFF));
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/* 1301 */     final Set<String> my_tags = new HashSet();
/*      */     try
/*      */     {
/* 1304 */       Download d = from_info.getRelatedToDownload();
/*      */       
/* 1306 */       if (d != null)
/*      */       {
/* 1308 */         String[] _tags = getTags(d);
/*      */         
/* 1310 */         if (_tags != null)
/*      */         {
/* 1312 */           map.put("b", Integer.valueOf(from_info.getRand() % 100));
/*      */           
/* 1314 */           map.put("m", encodeTags(_tags));
/*      */           
/* 1316 */           Collections.addAll(my_tags, _tags);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1322 */     long size = to_info.getSize();
/*      */     
/* 1324 */     if (size != 0L)
/*      */     {
/* 1326 */       map.put("s", new Long(size));
/*      */     }
/*      */     
/* 1329 */     final byte[] map_bytes = BEncoder.encode(map);
/*      */     
/*      */ 
/*      */ 
/* 1333 */     int max_hits = 30;
/*      */     
/* 1335 */     dht_plugin.get(key_bytes, "Content rel test: " + from_hash.substring(0, 16), (byte)0, 30, 30000L, false, false, new DHTPluginOperationListener()
/*      */     {
/*      */       private boolean diversified;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       private int hits;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1348 */       private Set<String> entries = new HashSet();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void starts(byte[] key) {}
/*      */       
/*      */ 
/*      */ 
/*      */       public boolean diversified()
/*      */       {
/* 1359 */         this.diversified = true;
/*      */         
/* 1361 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */       {
/*      */         try
/*      */         {
/* 1370 */           Map<String, Object> map = BDecoder.decode(value.getValue());
/*      */           
/* 1372 */           RelatedContentManager.DownloadInfo info = RelatedContentManager.this.decodeInfo(map, from_info.getHash(), 1, false, this.entries);
/*      */           try
/*      */           {
/* 1375 */             String[] r_tags = RelatedContentManager.this.decodeTags((byte[])map.get("m"));
/*      */             
/* 1377 */             if (r_tags != null)
/*      */             {
/* 1379 */               Long b = (Long)map.get("b");
/*      */               
/*      */ 
/*      */ 
/* 1383 */               if ((b == null) || (from_info.getRand() % 100 != b.longValue() % 100L))
/*      */               {
/* 1385 */                 for (String tag : r_tags)
/*      */                 {
/* 1387 */                   synchronized (my_tags)
/*      */                   {
/* 1389 */                     my_tags.remove(tag);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/* 1397 */           if (info != null)
/*      */           {
/* 1399 */             RelatedContentManager.this.analyseResponse(info, null);
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/* 1404 */         this.hits += 1;
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
/*      */       public void complete(byte[] key, boolean timeout_occurred)
/*      */       {
/* 1422 */         int f_cutoff = my_tags.size() > 0 ? 20 : 10;
/*      */         
/*      */         try
/*      */         {
/*      */           boolean do_it;
/*      */           
/*      */           boolean do_it;
/* 1429 */           if ((this.diversified) || (this.hits >= f_cutoff))
/*      */           {
/* 1431 */             do_it = false;
/*      */           } else { boolean do_it;
/* 1433 */             if (this.hits <= f_cutoff / 2)
/*      */             {
/* 1435 */               do_it = true;
/*      */             }
/*      */             else
/*      */             {
/* 1439 */               do_it = RandomUtils.nextInt(this.hits - f_cutoff / 2 + 1) == 0;
/*      */             }
/*      */           }
/* 1442 */           if (do_it) {
/*      */             try
/*      */             {
/* 1445 */               dht_plugin.put(key_bytes, "Content rel: " + from_hash.substring(0, 16) + " -> " + to_hash.substring(0, 16), map_bytes, (byte)16, new DHTPluginOperationListener()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public boolean diversified()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 1455 */                   return true;
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
/* 1483 */                   RelatedContentManager.this.publishNext();
/*      */                 }
/*      */               });
/*      */             }
/*      */             catch (Throwable e) {
/* 1488 */               Debug.printStackTrace(e);
/*      */               
/* 1490 */               RelatedContentManager.this.publishNext();
/*      */             }
/*      */             
/*      */           } else {
/* 1494 */             RelatedContentManager.this.publishNext();
/*      */           }
/*      */         }
/*      */         finally {
/* 1498 */           RelatedContentManager.this.checkAlternativePubs(to_info, map_bytes, f_cutoff);
/*      */         }
/*      */         
/*      */       }
/* 1502 */     });
/* 1503 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkAlternativePubs(DownloadInfo to_info, final byte[] map_bytes, final int f_cutoff)
/*      */   {
/* 1512 */     Download dl = to_info.getRelatedToDownload();
/*      */     
/* 1514 */     if (dl != null)
/*      */     {
/* 1516 */       DiskManagerFileInfo[] files = dl.getDiskManagerFileInfo();
/*      */       
/* 1518 */       List<Long> sizes = new ArrayList();
/*      */       
/* 1520 */       for (DiskManagerFileInfo file : files)
/*      */       {
/* 1522 */         long size = file.getLength();
/*      */         
/* 1524 */         if (size >= 52428800L)
/*      */         {
/* 1526 */           sizes.add(Long.valueOf(size));
/*      */         }
/*      */       }
/*      */       
/* 1530 */       final DHTPluginInterface dht_plugin = selectDHT(to_info.getNetworksInternal());
/*      */       
/* 1532 */       if ((dht_plugin != null) && (sizes.size() > 0)) {
/*      */         try
/*      */         {
/* 1535 */           String to_hash = ByteFormatter.encodeString(to_info.getHash());
/*      */           
/* 1537 */           final long selected_size = ((Long)sizes.get(new Random().nextInt(sizes.size()))).longValue();
/*      */           
/* 1539 */           final byte[] key_bytes = ("az:rcm:size:assoc:" + selected_size).getBytes("UTF-8");
/*      */           
/* 1541 */           int max_hits = 30;
/*      */           
/* 1543 */           dht_plugin.get(key_bytes, "Content size rel test: " + to_hash.substring(0, 16), (byte)0, max_hits, 30000L, false, false, new DHTPluginOperationListener()
/*      */           {
/*      */             private boolean diversified;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             private int hits;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1556 */             private Set<String> entries = new HashSet();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void starts(byte[] key) {}
/*      */             
/*      */ 
/*      */ 
/*      */             public boolean diversified()
/*      */             {
/* 1567 */               this.diversified = true;
/*      */               
/* 1569 */               return false;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */             {
/* 1577 */               this.hits += 1;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void complete(byte[] key, boolean timeout_occurred)
/*      */             {
/*      */               boolean do_it;
/*      */               
/*      */ 
/*      */ 
/*      */               boolean do_it;
/*      */               
/*      */ 
/* 1597 */               if ((this.diversified) || (this.hits >= f_cutoff))
/*      */               {
/* 1599 */                 do_it = false;
/*      */               } else { boolean do_it;
/* 1601 */                 if (this.hits <= f_cutoff / 2)
/*      */                 {
/* 1603 */                   do_it = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1607 */                   do_it = RandomUtils.nextInt(this.hits - f_cutoff / 2 + 1) == 0;
/*      */                 }
/*      */               }
/* 1610 */               if (do_it) {
/*      */                 try
/*      */                 {
/* 1613 */                   dht_plugin.put(key_bytes, "Content size rel: " + selected_size + " -> " + map_bytes.substring(0, 16), this.val$map_bytes, (byte)16, new DHTPluginOperationListener()
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */                     public boolean diversified()
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/* 1623 */                       return true;
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void starts(byte[] key) {}
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void complete(byte[] key, boolean timeout_occurred) {}
/*      */                   });
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1655 */                   Debug.printStackTrace(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (Throwable e) {
/* 1662 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DownloadInfo decodeInfo(Map map, byte[] from_hash, int level, boolean explicit, Set<String> unique_keys)
/*      */   {
/*      */     try
/*      */     {
/* 1677 */       String title = new String((byte[])map.get("d"), "UTF-8");
/*      */       
/* 1679 */       String tracker = null;
/*      */       
/* 1681 */       byte[] hash = (byte[])map.get("h");
/*      */       
/* 1683 */       if (hash == null)
/*      */       {
/* 1685 */         tracker = new String((byte[])map.get("t"), "UTF-8");
/*      */       }
/*      */       
/* 1688 */       int rand = ((Long)map.get("r")).intValue();
/*      */       
/* 1690 */       String key = title + " % " + rand;
/*      */       
/* 1692 */       synchronized (unique_keys)
/*      */       {
/* 1694 */         if (unique_keys.contains(key))
/*      */         {
/* 1696 */           return null;
/*      */         }
/*      */         
/* 1699 */         unique_keys.add(key);
/*      */       }
/*      */       
/* 1702 */       Long l_version = (Long)map.get("v");
/*      */       
/* 1704 */       int version = l_version == null ? 0 : l_version.intValue();
/*      */       
/* 1706 */       Long l_size = (Long)map.get("s");
/*      */       
/* 1708 */       long size = l_size == null ? 0L : l_size.longValue();
/*      */       
/* 1710 */       Long cnet = (Long)map.get("c");
/* 1711 */       Long published = (Long)map.get("p");
/* 1712 */       Long leechers = (Long)map.get("l");
/* 1713 */       Long seeds = (Long)map.get("z");
/*      */       
/*      */       int seeds_leechers;
/*      */       
/*      */       int seeds_leechers;
/*      */       
/* 1719 */       if ((leechers == null) && (seeds == null))
/*      */       {
/* 1721 */         seeds_leechers = -1;
/*      */       } else { int seeds_leechers;
/* 1723 */         if (leechers == null)
/*      */         {
/* 1725 */           seeds_leechers = seeds.intValue() << 16;
/*      */         } else { int seeds_leechers;
/* 1727 */           if (seeds == null)
/*      */           {
/* 1729 */             seeds_leechers = leechers.intValue() & 0xFFFF;
/*      */           }
/*      */           else
/*      */           {
/* 1733 */             seeds_leechers = seeds.intValue() << 16 | leechers.intValue() & 0xFFFF; }
/*      */         }
/*      */       }
/* 1736 */       byte[] tracker_keys = (byte[])map.get("k");
/* 1737 */       byte[] ws_keys = (byte[])map.get("w");
/*      */       
/* 1739 */       if ((tracker_keys != null) && (tracker_keys.length % 4 != 0))
/*      */       {
/* 1741 */         tracker_keys = null;
/*      */       }
/*      */       
/* 1744 */       if ((ws_keys != null) && (ws_keys.length % 4 != 0))
/*      */       {
/* 1746 */         ws_keys = null;
/*      */       }
/*      */       
/* 1749 */       byte[] _tags = (byte[])map.get("g");
/*      */       
/* 1751 */       String[] tags = decodeTags(_tags);
/*      */       
/* 1753 */       Long _nets = (Long)map.get("o");
/*      */       
/* 1755 */       byte nets = _nets == null ? 1 : _nets.byteValue();
/*      */       
/* 1757 */       return new DownloadInfo(version, from_hash, hash, title, rand, tracker, tracker_keys, ws_keys, tags, nets, level, explicit, size, published == null ? 0 : published.intValue(), seeds_leechers, (byte)(int)(cnet == null ? -1L : cnet.byteValue()));
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1767 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void lookupAttributes(byte[] from_hash, RelatedAttributeLookupListener listener)
/*      */     throws ContentException
/*      */   {
/* 1778 */     lookupAttributes(from_hash, new String[] { "Public" }, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void lookupAttributes(final byte[] from_hash, final String[] networks, final RelatedAttributeLookupListener listener)
/*      */     throws ContentException
/*      */   {
/* 1789 */     if (from_hash == null)
/*      */     {
/* 1791 */       throw new ContentException("hash is null");
/*      */     }
/*      */     
/* 1794 */     if ((!this.initialisation_complete_sem.isReleasedForever()) || ((this.public_dht_plugin != null) && (this.public_dht_plugin.isInitialising())))
/*      */     {
/*      */ 
/* 1797 */       AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */       
/* 1799 */       dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */           try
/*      */           {
/* 1806 */             RelatedContentManager.this.initialisation_complete_sem.reserve();
/*      */             
/* 1808 */             RelatedContentManager.this.lookupAttributesSupport(from_hash, RelatedContentManager.convertNetworks(networks), listener);
/*      */           }
/*      */           catch (ContentException e)
/*      */           {
/* 1812 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     else {
/* 1818 */       lookupAttributesSupport(from_hash, convertNetworks(networks), listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void lookupAttributesSupport(final byte[] from_hash, byte networks, final RelatedAttributeLookupListener listener)
/*      */     throws ContentException
/*      */   {
/*      */     try
/*      */     {
/* 1831 */       if (!this.enabled)
/*      */       {
/* 1833 */         throw new ContentException("rcm is disabled");
/*      */       }
/*      */       
/* 1836 */       DHTPluginInterface dht_plugin = selectDHT(networks);
/*      */       
/* 1838 */       if (dht_plugin == null)
/*      */       {
/* 1840 */         throw new Exception("DHT Plugin unavailable for networks " + getString(convertNetworks(networks)));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1845 */       final String dht_plugin_network = dht_plugin == this.public_dht_plugin ? "Public" : "I2P";
/*      */       
/* 1847 */       String from_hash_str = ByteFormatter.encodeString(from_hash);
/*      */       
/* 1849 */       byte[] key_bytes = ("az:rcm:assoc:" + from_hash_str).getBytes("UTF-8");
/*      */       
/* 1851 */       String op_str = "Content attr read: " + from_hash_str.substring(0, 16);
/*      */       
/* 1853 */       dht_plugin.get(key_bytes, op_str, (byte)0, 512, 30000L, false, true, new DHTPluginOperationListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1863 */         private Set<String> tags = new HashSet();
/*      */         
/*      */ 
/*      */ 
/*      */         public void starts(byte[] key)
/*      */         {
/* 1869 */           if (listener != null)
/*      */           {
/*      */             try {
/* 1872 */               listener.lookupStart();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1876 */               Debug.out(e);
/*      */             }
/*      */             
/* 1879 */             RelatedContentManager.ContentCache content_cache = RelatedContentManager.this.loadRelatedContent();
/*      */             
/* 1881 */             RelatedContentManager.DownloadInfo info = (RelatedContentManager.DownloadInfo)content_cache.related_content.get(Base32.encode(from_hash));
/*      */             
/* 1883 */             if (info != null)
/*      */             {
/* 1885 */               String[] l_tags = info.getTags();
/*      */               
/* 1887 */               if (l_tags != null)
/*      */               {
/* 1889 */                 for (String tag : l_tags)
/*      */                 {
/* 1891 */                   synchronized (this.tags)
/*      */                   {
/* 1893 */                     if (this.tags.contains(tag)) {
/*      */                       continue;
/*      */                     }
/*      */                     
/*      */ 
/* 1898 */                     this.tags.add(tag);
/*      */                   }
/*      */                   try
/*      */                   {
/* 1902 */                     listener.tagFound(tag, dht_plugin_network);
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/* 1906 */                     Debug.out(e);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         public boolean diversified()
/*      */         {
/* 1917 */           return true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */         {
/*      */           try
/*      */           {
/* 1926 */             Map<String, Object> map = BDecoder.decode(value.getValue());
/*      */             
/* 1928 */             String[] r_tags = RelatedContentManager.this.decodeTags((byte[])map.get("m"));
/*      */             
/* 1930 */             if (r_tags != null)
/*      */             {
/* 1932 */               for (String tag : r_tags)
/*      */               {
/* 1934 */                 synchronized (this.tags)
/*      */                 {
/* 1936 */                   if (this.tags.contains(tag)) {
/*      */                     continue;
/*      */                   }
/*      */                   
/*      */ 
/* 1941 */                   this.tags.add(tag);
/*      */                 }
/*      */                 try
/*      */                 {
/* 1945 */                   listener.tagFound(tag, dht_plugin_network);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1949 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
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
/*      */ 
/*      */         public void complete(byte[] key, boolean timeout_occurred)
/*      */         {
/* 1971 */           if (listener != null) {
/*      */             try
/*      */             {
/* 1974 */               listener.lookupComplete();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1978 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e) {
/*      */       ContentException ce;
/*      */       ContentException ce;
/* 1987 */       if ((e instanceof ContentException))
/*      */       {
/* 1989 */         ce = (ContentException)e;
/*      */       }
/*      */       else {
/* 1992 */         ce = new ContentException("Lookup failed", e);
/*      */       }
/*      */       
/* 1995 */       if (listener != null) {
/*      */         try
/*      */         {
/* 1998 */           listener.lookupFailed(ce);
/*      */         }
/*      */         catch (Throwable f)
/*      */         {
/* 2002 */           Debug.out(f);
/*      */         }
/*      */       }
/*      */       
/* 2006 */       throw ce;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void lookupContent(final byte[] hash, final RelatedContentLookupListener listener)
/*      */     throws ContentException
/*      */   {
/* 2017 */     if (hash == null)
/*      */     {
/* 2019 */       throw new ContentException("hash is null");
/*      */     }
/*      */     
/* 2022 */     byte net = 1;
/*      */     try
/*      */     {
/* 2025 */       Download download = this.plugin_interface.getDownloadManager().getDownload(hash);
/*      */       
/* 2027 */       if (download != null)
/*      */       {
/* 2029 */         net = getNetworks(download);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/* 2035 */     final byte f_net = net;
/*      */     
/* 2037 */     if ((!this.initialisation_complete_sem.isReleasedForever()) || ((this.public_dht_plugin != null) && (this.public_dht_plugin.isInitialising())))
/*      */     {
/*      */ 
/* 2040 */       AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */       
/* 2042 */       dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */           try
/*      */           {
/* 2049 */             RelatedContentManager.this.initialisation_complete_sem.reserve();
/*      */             
/* 2051 */             RelatedContentManager.this.lookupContentSupport(hash, 0, f_net, true, listener);
/*      */           }
/*      */           catch (ContentException e)
/*      */           {
/* 2055 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     else {
/* 2061 */       lookupContentSupport(hash, 0, f_net, true, listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void lookupContent(final byte[] hash, String[] networks, final RelatedContentLookupListener listener)
/*      */     throws ContentException
/*      */   {
/* 2073 */     if (hash == null)
/*      */     {
/* 2075 */       throw new ContentException("hash is null");
/*      */     }
/*      */     
/* 2078 */     final byte net = convertNetworks(networks);
/*      */     
/* 2080 */     if (net == 0)
/*      */     {
/* 2082 */       throw new ContentException("No networks specified");
/*      */     }
/*      */     
/* 2085 */     if ((!this.initialisation_complete_sem.isReleasedForever()) || ((this.public_dht_plugin != null) && (this.public_dht_plugin.isInitialising())))
/*      */     {
/*      */ 
/* 2088 */       AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */       
/* 2090 */       dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */           try
/*      */           {
/* 2097 */             RelatedContentManager.this.initialisation_complete_sem.reserve();
/*      */             
/* 2099 */             RelatedContentManager.this.lookupContentSupport(hash, 0, net, true, listener);
/*      */           }
/*      */           catch (ContentException e)
/*      */           {
/* 2103 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     else {
/* 2109 */       lookupContentSupport(hash, 0, net, true, listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void lookupContent(final long file_size, RelatedContentLookupListener listener)
/*      */     throws ContentException
/*      */   {
/* 2120 */     if (file_size < 52428800L)
/*      */     {
/* 2122 */       throw new ContentException("file size is invalid - min=52428800");
/*      */     }
/*      */     
/* 2125 */     if ((!this.initialisation_complete_sem.isReleasedForever()) || ((this.public_dht_plugin != null) && (this.public_dht_plugin.isInitialising())))
/*      */     {
/*      */ 
/* 2128 */       AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */       
/* 2130 */       dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */           try
/*      */           {
/* 2137 */             RelatedContentManager.this.initialisation_complete_sem.reserve();
/*      */             
/* 2139 */             RelatedContentManager.this.lookupContentSupport(file_size, (byte)1, this.val$listener);
/*      */           }
/*      */           catch (ContentException e)
/*      */           {
/* 2143 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     else {
/* 2149 */       lookupContentSupport(file_size, (byte)1, listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void lookupContent(final long file_size, String[] networks, final RelatedContentLookupListener listener)
/*      */     throws ContentException
/*      */   {
/* 2161 */     if (file_size < 52428800L)
/*      */     {
/* 2163 */       throw new ContentException("file size is invalid - min=52428800");
/*      */     }
/*      */     
/* 2166 */     byte net = convertNetworks(networks);
/*      */     
/* 2168 */     if (net == 0)
/*      */     {
/* 2170 */       throw new ContentException("No networks specified");
/*      */     }
/*      */     
/* 2173 */     if ((!this.initialisation_complete_sem.isReleasedForever()) || ((this.public_dht_plugin != null) && (this.public_dht_plugin.isInitialising())))
/*      */     {
/*      */ 
/* 2176 */       AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */       
/* 2178 */       dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */           try
/*      */           {
/* 2185 */             RelatedContentManager.this.initialisation_complete_sem.reserve();
/*      */             
/* 2187 */             RelatedContentManager.this.lookupContentSupport(file_size, listener, this.val$listener);
/*      */           }
/*      */           catch (ContentException e)
/*      */           {
/* 2191 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     else {
/* 2197 */       lookupContentSupport(file_size, net, listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void lookupContentSupport(long file_size, byte networks, RelatedContentLookupListener listener)
/*      */     throws ContentException
/*      */   {
/* 2209 */     if (!this.enabled)
/*      */     {
/* 2211 */       throw new ContentException("rcm is disabled");
/*      */     }
/*      */     try
/*      */     {
/* 2215 */       byte[] key_bytes = ("az:rcm:size:assoc:" + file_size).getBytes("UTF-8");
/*      */       
/*      */ 
/*      */ 
/* 2219 */       byte[] from_hash = new SHA1Simple().calculateHash(key_bytes);
/*      */       
/* 2221 */       String op_str = "Content rel read: size=" + file_size;
/*      */       
/* 2223 */       lookupContentSupport0(from_hash, key_bytes, op_str, 0, networks, true, listener);
/*      */     }
/*      */     catch (ContentException e)
/*      */     {
/* 2227 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2231 */       throw new ContentException("lookup failed", e);
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
/*      */   private void lookupContentSupport(byte[] from_hash, int level, byte networks, boolean explicit, RelatedContentLookupListener listener)
/*      */     throws ContentException
/*      */   {
/* 2245 */     if (!this.enabled)
/*      */     {
/* 2247 */       throw new ContentException("rcm is disabled");
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 2252 */       String from_hash_str = ByteFormatter.encodeString(from_hash);
/*      */       
/* 2254 */       byte[] key_bytes = ("az:rcm:assoc:" + from_hash_str).getBytes("UTF-8");
/*      */       
/* 2256 */       String op_str = "Content rel read: " + from_hash_str.substring(0, 16);
/*      */       
/* 2258 */       lookupContentSupport0(from_hash, key_bytes, op_str, level, networks, explicit, listener);
/*      */     }
/*      */     catch (ContentException e)
/*      */     {
/* 2262 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2266 */       throw new ContentException("lookup failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getString(String[] args)
/*      */   {
/* 2274 */     String str = "";
/*      */     
/* 2276 */     for (String s : args)
/*      */     {
/* 2278 */       str = str + (str.length() == 0 ? "" : ",") + s;
/*      */     }
/*      */     
/* 2281 */     return str;
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
/*      */   private void lookupContentSupport0(final byte[] from_hash, byte[] key_bytes, String op_str, final int level, byte networks, final boolean explicit, final RelatedContentLookupListener listener)
/*      */     throws ContentException
/*      */   {
/*      */     try
/*      */     {
/* 2297 */       int max_hits = 30;
/*      */       
/* 2299 */       DHTPluginInterface dht_plugin = selectDHT(networks);
/*      */       
/* 2301 */       if (dht_plugin == null)
/*      */       {
/* 2303 */         throw new Exception("DHT Plugin unavailable for networks '" + getString(convertNetworks(networks)) + "'");
/*      */       }
/*      */       
/* 2306 */       dht_plugin.get(key_bytes, op_str, (byte)0, 30, 60000L, false, true, new DHTPluginOperationListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2316 */         private Set<String> entries = new HashSet();
/*      */         
/* 2318 */         private RelatedContentManagerListener manager_listener = new RelatedContentManagerListener()
/*      */         {
/*      */ 
/* 2321 */           private Set<RelatedContent> content_list = new HashSet();
/*      */           
/*      */ 
/*      */ 
/*      */           public void contentFound(RelatedContent[] content)
/*      */           {
/* 2327 */             handle(content);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void contentChanged(RelatedContent[] content)
/*      */           {
/* 2334 */             handle(content);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void contentRemoved(RelatedContent[] content) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void contentChanged() {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void contentReset() {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           private void handle(RelatedContent[] content)
/*      */           {
/* 2357 */             List<RelatedContent> new_content = new ArrayList(content.length);
/*      */             
/* 2359 */             synchronized (this.content_list)
/*      */             {
/* 2361 */               for (RelatedContent rc : content)
/*      */               {
/* 2363 */                 if (!this.content_list.contains(rc))
/*      */                 {
/* 2365 */                   new_content.add(rc);
/*      */                 }
/*      */               }
/*      */               
/* 2369 */               if (new_content.size() == 0)
/*      */               {
/* 2371 */                 return;
/*      */               }
/*      */               
/* 2374 */               this.content_list.addAll(new_content);
/*      */             }
/*      */             
/* 2377 */             RelatedContentManager.15.this.val$listener.contentFound((RelatedContent[])new_content.toArray(new RelatedContent[new_content.size()]));
/*      */           }
/*      */         };
/*      */         
/*      */ 
/*      */ 
/*      */         public void starts(byte[] key)
/*      */         {
/* 2385 */           if (listener != null) {
/*      */             try
/*      */             {
/* 2388 */               listener.lookupStart();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2392 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         public boolean diversified()
/*      */         {
/* 2400 */           return true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */         {
/*      */           try
/*      */           {
/* 2409 */             Map<String, Object> map = BDecoder.decode(value.getValue());
/*      */             
/* 2411 */             RelatedContentManager.DownloadInfo info = RelatedContentManager.this.decodeInfo(map, from_hash, level + 1, explicit, this.entries);
/*      */             
/* 2413 */             if (info != null)
/*      */             {
/* 2415 */               RelatedContentManager.this.analyseResponse(info, listener == null ? null : this.manager_listener);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
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
/* 2434 */           if (listener != null) {
/*      */             try
/*      */             {
/* 2437 */               listener.lookupComplete();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2441 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e) {
/*      */       ContentException ce;
/*      */       ContentException ce;
/* 2450 */       if ((e instanceof ContentException))
/*      */       {
/* 2452 */         ce = (ContentException)e;
/*      */       }
/*      */       else {
/* 2455 */         ce = new ContentException("Lookup failed", e);
/*      */       }
/*      */       
/* 2458 */       if (listener != null) {
/*      */         try
/*      */         {
/* 2461 */           listener.lookupFailed(ce);
/*      */         }
/*      */         catch (Throwable f)
/*      */         {
/* 2465 */           Debug.out(f);
/*      */         }
/*      */       }
/*      */       
/* 2469 */       throw ce;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void popuplateSecondaryLookups(ContentCache content_cache)
/*      */   {
/* 2477 */     Random rand = new Random();
/*      */     
/* 2479 */     this.secondary_lookups.clear();
/*      */     
/*      */ 
/*      */ 
/* 2483 */     List<DownloadInfo> primaries = this.download_info_map.values();
/*      */     
/* 2485 */     int primary_count = primaries.size();
/*      */     
/*      */     int primaries_to_add;
/*      */     int primaries_to_add;
/* 2489 */     if (primary_count < 2)
/*      */     {
/* 2491 */       primaries_to_add = 0;
/*      */     } else { int primaries_to_add;
/* 2493 */       if (primary_count < 5) {
/*      */         int primaries_to_add;
/* 2495 */         if (rand.nextInt(4) == 0)
/*      */         {
/* 2497 */           primaries_to_add = 1;
/*      */         }
/*      */         else
/*      */         {
/* 2501 */           primaries_to_add = 0; }
/*      */       } else { int primaries_to_add;
/* 2503 */         if (primary_count < 10)
/*      */         {
/* 2505 */           primaries_to_add = 1;
/*      */         }
/*      */         else
/*      */         {
/* 2509 */           primaries_to_add = 2; }
/*      */       }
/*      */     }
/* 2512 */     if (primaries_to_add > 0)
/*      */     {
/* 2514 */       Set<DownloadInfo> added = new HashSet();
/*      */       
/* 2516 */       for (int i = 0; i < primaries_to_add; i++)
/*      */       {
/* 2518 */         DownloadInfo info = (DownloadInfo)primaries.get(rand.nextInt(primaries.size()));
/*      */         
/* 2520 */         if (!added.contains(info))
/*      */         {
/* 2522 */           added.add(info);
/*      */           
/* 2524 */           this.secondary_lookups.addLast(new SecondaryLookup(info.getHash(), info.getLevel(), info.getNetworksInternal(), null));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2529 */     Map<String, DownloadInfo> related_content = content_cache.related_content;
/*      */     
/* 2531 */     Iterator<DownloadInfo> it = related_content.values().iterator();
/*      */     
/* 2533 */     List<DownloadInfo> secondary_cache_temp = new ArrayList(related_content.size());
/*      */     
/* 2535 */     while (it.hasNext())
/*      */     {
/* 2537 */       DownloadInfo di = (DownloadInfo)it.next();
/*      */       
/* 2539 */       if ((di.getHash() != null) && (di.getLevel() < this.max_search_level))
/*      */       {
/* 2541 */         secondary_cache_temp.add(di);
/*      */       }
/*      */     }
/*      */     
/* 2545 */     int cache_size = Math.min(secondary_cache_temp.size(), 10 - this.secondary_lookups.size());
/*      */     
/* 2547 */     if (cache_size > 0)
/*      */     {
/* 2549 */       for (int i = 0; i < cache_size; i++)
/*      */       {
/* 2551 */         int index = rand.nextInt(secondary_cache_temp.size());
/*      */         
/* 2553 */         DownloadInfo x = (DownloadInfo)secondary_cache_temp.get(index);
/*      */         
/* 2555 */         secondary_cache_temp.set(index, secondary_cache_temp.get(i));
/*      */         
/* 2557 */         secondary_cache_temp.set(i, x);
/*      */       }
/*      */       
/* 2560 */       for (int i = 0; i < cache_size; i++)
/*      */       {
/* 2562 */         DownloadInfo x = (DownloadInfo)secondary_cache_temp.get(i);
/*      */         
/* 2564 */         this.secondary_lookups.addLast(new SecondaryLookup(x.getHash(), x.getLevel(), x.getNetworksInternal(), null));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void secondaryLookup()
/*      */   {
/* 2574 */     long now = SystemTime.getMonotonousTime();
/*      */     SecondaryLookup sl;
/* 2576 */     synchronized (this.rcm_lock)
/*      */     {
/* 2578 */       if (this.secondary_lookup_in_progress)
/*      */       {
/* 2580 */         return;
/*      */       }
/*      */       
/* 2583 */       if (now - this.secondary_lookup_complete_time < 900000L)
/*      */       {
/* 2585 */         return;
/*      */       }
/*      */       
/* 2588 */       if (this.secondary_lookups.size() == 0)
/*      */       {
/* 2590 */         ContentCache cc = this.content_cache == null ? null : (ContentCache)this.content_cache.get();
/*      */         
/* 2592 */         if (cc == null)
/*      */         {
/*      */ 
/*      */ 
/* 2596 */           cc = loadRelatedContent();
/*      */         }
/*      */         else
/*      */         {
/* 2600 */           popuplateSecondaryLookups(cc);
/*      */         }
/*      */       }
/*      */       
/* 2604 */       if (this.secondary_lookups.size() == 0)
/*      */       {
/* 2606 */         return;
/*      */       }
/*      */       
/* 2609 */       sl = (SecondaryLookup)this.secondary_lookups.removeFirst();
/*      */       
/* 2611 */       this.secondary_lookup_in_progress = true;
/*      */     }
/*      */     try
/*      */     {
/* 2615 */       lookupContentSupport(sl.getHash(), sl.getLevel(), sl.getNetworks(), false, new RelatedContentLookupListener()
/*      */       {
/*      */         public void lookupStart() {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void contentFound(RelatedContent[] content) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void lookupComplete()
/*      */         {
/* 2636 */           next();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void lookupFailed(ContentException error)
/*      */         {
/* 2643 */           next();
/*      */         }
/*      */         
/*      */ 
/*      */         protected void next()
/*      */         {
/*      */           final RelatedContentManager.SecondaryLookup next_sl;
/*      */           
/* 2651 */           synchronized (RelatedContentManager.this.rcm_lock)
/*      */           {
/* 2653 */             if (RelatedContentManager.this.secondary_lookups.size() == 0)
/*      */             {
/* 2655 */               RelatedContentManager.this.secondary_lookup_in_progress = false;
/*      */               
/* 2657 */               RelatedContentManager.this.secondary_lookup_complete_time = SystemTime.getMonotonousTime();
/*      */               
/* 2659 */               return;
/*      */             }
/*      */             
/*      */ 
/* 2663 */             next_sl = (RelatedContentManager.SecondaryLookup)RelatedContentManager.this.secondary_lookups.removeFirst();
/*      */           }
/*      */           
/*      */ 
/* 2667 */           final RelatedContentLookupListener listener = this;
/*      */           
/* 2669 */           SimpleTimer.addEvent("RCM:SLDelay", SystemTime.getOffsetTime(30000L), new TimerEventPerformer()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void perform(TimerEvent event)
/*      */             {
/*      */ 
/*      */               try
/*      */               {
/*      */ 
/* 2679 */                 RelatedContentManager.this.lookupContentSupport(RelatedContentManager.SecondaryLookup.access$2000(next_sl), RelatedContentManager.SecondaryLookup.access$2100(next_sl), RelatedContentManager.SecondaryLookup.access$2200(next_sl), false, listener);
/*      */ 
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*      */ 
/* 2685 */                 synchronized (RelatedContentManager.this.rcm_lock)
/*      */                 {
/* 2687 */                   RelatedContentManager.this.secondary_lookup_in_progress = false;
/*      */                   
/* 2689 */                   RelatedContentManager.this.secondary_lookup_complete_time = SystemTime.getMonotonousTime();
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */             }
/*      */           });
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2701 */       synchronized (this.rcm_lock)
/*      */       {
/* 2703 */         this.secondary_lookup_in_progress = false;
/*      */         
/* 2705 */         this.secondary_lookup_complete_time = now;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void contentChanged(final DownloadInfo info)
/*      */   {
/* 2714 */     setConfigDirty();
/*      */     
/* 2716 */     this.content_change_dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 2722 */         for (RelatedContentManagerListener l : RelatedContentManager.this.listeners) {
/*      */           try
/*      */           {
/* 2725 */             l.contentChanged(new RelatedContent[] { info });
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2729 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void contentChanged(boolean is_dirty)
/*      */   {
/* 2740 */     if (is_dirty)
/*      */     {
/* 2742 */       setConfigDirty();
/*      */     }
/*      */     
/* 2745 */     this.content_change_dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 2751 */         for (RelatedContentManagerListener l : RelatedContentManager.this.listeners) {
/*      */           try
/*      */           {
/* 2754 */             l.contentChanged();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2758 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void delete(RelatedContent[] content)
/*      */   {
/* 2769 */     synchronized (this.rcm_lock)
/*      */     {
/* 2771 */       ContentCache content_cache = loadRelatedContent();
/*      */       
/* 2773 */       delete(content, content_cache, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void delete(final RelatedContent[] content, ContentCache content_cache, boolean persistent)
/*      */   {
/* 2783 */     if (persistent)
/*      */     {
/* 2785 */       addPersistentlyDeleted(content);
/*      */     }
/*      */     
/* 2788 */     Map<String, DownloadInfo> related_content = content_cache.related_content;
/*      */     
/* 2790 */     Iterator<DownloadInfo> it = related_content.values().iterator();
/*      */     
/* 2792 */     while (it.hasNext())
/*      */     {
/* 2794 */       DownloadInfo di = (DownloadInfo)it.next();
/*      */       
/* 2796 */       for (RelatedContent c : content)
/*      */       {
/* 2798 */         if (c == di)
/*      */         {
/* 2800 */           it.remove();
/*      */           
/* 2802 */           if (di.isUnread())
/*      */           {
/* 2804 */             decrementUnread();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2810 */     ByteArrayHashMapEx<ArrayList<DownloadInfo>> related_content_map = content_cache.related_content_map;
/*      */     
/* 2812 */     List<byte[]> delete = new ArrayList();
/*      */     
/* 2814 */     for (byte[] key : related_content_map.keys())
/*      */     {
/* 2816 */       ArrayList<DownloadInfo> infos = (ArrayList)related_content_map.get(key);
/*      */       
/* 2818 */       for (RelatedContent c : content)
/*      */       {
/* 2820 */         if (infos.remove(c))
/*      */         {
/* 2822 */           if (infos.size() == 0)
/*      */           {
/* 2824 */             delete.add(key);
/*      */             
/* 2826 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2832 */     for (byte[] key : delete)
/*      */     {
/* 2834 */       related_content_map.remove(key);
/*      */     }
/*      */     
/* 2837 */     setConfigDirty();
/*      */     
/* 2839 */     this.content_change_dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 2845 */         for (RelatedContentManagerListener l : RelatedContentManager.this.listeners) {
/*      */           try
/*      */           {
/* 2848 */             l.contentRemoved(content);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2852 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getPrivateInfoKey(RelatedContent info)
/*      */   {
/* 2863 */     return info.getTitle() + ":" + info.getTracker();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void analyseResponse(DownloadInfo to_info, final RelatedContentManagerListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 2872 */       synchronized (this.rcm_lock)
/*      */       {
/* 2874 */         byte[] target = to_info.getHash();
/*      */         
/*      */         String key;
/*      */         String key;
/* 2878 */         if (target != null)
/*      */         {
/* 2880 */           if (this.download_info_map.containsKey(target))
/*      */           {
/*      */ 
/*      */ 
/* 2884 */             return;
/*      */           }
/*      */           
/* 2887 */           key = Base32.encode(target);
/*      */         }
/*      */         else
/*      */         {
/* 2891 */           key = getPrivateInfoKey(to_info);
/*      */           
/* 2893 */           if (this.download_priv_set.contains(key))
/*      */           {
/*      */ 
/*      */ 
/* 2897 */             return;
/*      */           }
/*      */         }
/*      */         
/* 2901 */         if (isPersistentlyDeleted(to_info))
/*      */         {
/* 2903 */           return;
/*      */         }
/*      */         
/* 2906 */         ContentCache content_cache = loadRelatedContent();
/*      */         
/* 2908 */         DownloadInfo target_info = null;
/*      */         
/* 2910 */         boolean changed_content = false;
/* 2911 */         boolean new_content = false;
/*      */         
/*      */ 
/* 2914 */         target_info = (DownloadInfo)content_cache.related_content.get(key);
/*      */         
/* 2916 */         if (target_info == null)
/*      */         {
/* 2918 */           if (enoughSpaceFor(content_cache, to_info))
/*      */           {
/* 2920 */             target_info = to_info;
/*      */             
/* 2922 */             content_cache.related_content.put(key, target_info);
/*      */             
/* 2924 */             byte[] from_hash = to_info.getRelatedToHash();
/*      */             
/* 2926 */             ArrayList<DownloadInfo> links = (ArrayList)content_cache.related_content_map.get(from_hash);
/*      */             
/* 2928 */             if (links == null)
/*      */             {
/* 2930 */               links = new ArrayList(1);
/*      */               
/* 2932 */               content_cache.related_content_map.put(from_hash, links);
/*      */             }
/*      */             
/* 2935 */             links.add(target_info);
/*      */             
/* 2937 */             links.trimToSize();
/*      */             
/* 2939 */             target_info.setPublic(content_cache);
/*      */             
/* 2941 */             if (this.secondary_lookups.size() < 10)
/*      */             {
/* 2943 */               byte[] hash = target_info.getHash();
/* 2944 */               int level = target_info.getLevel();
/*      */               
/* 2946 */               if ((hash != null) && (level < this.max_search_level))
/*      */               {
/* 2948 */                 this.secondary_lookups.add(new SecondaryLookup(hash, level, target_info.getNetworksInternal(), null));
/*      */               }
/*      */             }
/*      */             
/* 2952 */             new_content = true;
/*      */           }
/*      */           else
/*      */           {
/* 2956 */             transient_info_cache.put(key, to_info);
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 2962 */         else if (to_info.getVersion() >= target_info.getVersion())
/*      */         {
/* 2964 */           changed_content = target_info.addInfo(to_info);
/*      */         }
/*      */         
/*      */ 
/* 2968 */         if (target_info != null)
/*      */         {
/* 2970 */           final RelatedContent[] f_target = { target_info };
/* 2971 */           final boolean f_change = changed_content;
/*      */           
/* 2973 */           final boolean something_changed = (changed_content) || (new_content);
/*      */           
/* 2975 */           if (something_changed)
/*      */           {
/* 2977 */             setConfigDirty();
/*      */           }
/*      */           
/* 2980 */           this.content_change_dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 2986 */               if (something_changed)
/*      */               {
/* 2988 */                 for (RelatedContentManagerListener l : RelatedContentManager.this.listeners) {
/*      */                   try
/*      */                   {
/* 2991 */                     if (f_change)
/*      */                     {
/* 2993 */                       l.contentChanged(f_target);
/*      */                     }
/*      */                     else
/*      */                     {
/* 2997 */                       l.contentFound(f_target);
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e) {
/* 3001 */                     Debug.out(e);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/* 3006 */               if (listener != null) {
/*      */                 try
/*      */                 {
/* 3009 */                   if (f_change)
/*      */                   {
/* 3011 */                     listener.contentChanged(f_target);
/*      */                   }
/*      */                   else
/*      */                   {
/* 3015 */                     listener.contentFound(f_target);
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {
/* 3019 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3029 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean enoughSpaceFor(ContentCache content_cache, DownloadInfo fi)
/*      */   {
/* 3038 */     Map<String, DownloadInfo> related_content = content_cache.related_content;
/*      */     
/* 3040 */     if (related_content.size() < this.max_results + this.temporary_space.get())
/*      */     {
/* 3042 */       return true;
/*      */     }
/*      */     
/* 3045 */     Iterator<Map.Entry<String, DownloadInfo>> it = related_content.entrySet().iterator();
/*      */     
/* 3047 */     int max_level = fi.getLevel();
/*      */     
/*      */ 
/*      */ 
/* 3051 */     Map<Integer, DownloadInfo> oldest_per_rank = new HashMap();
/*      */     
/* 3053 */     int min_rank = Integer.MAX_VALUE;
/* 3054 */     int max_rank = -1;
/*      */     
/* 3056 */     while (it.hasNext())
/*      */     {
/* 3058 */       Map.Entry<String, DownloadInfo> entry = (Map.Entry)it.next();
/*      */       
/* 3060 */       DownloadInfo info = (DownloadInfo)entry.getValue();
/*      */       
/* 3062 */       if (!info.isExplicit())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 3067 */         int info_level = info.getLevel();
/*      */         
/* 3069 */         if (info_level >= max_level)
/*      */         {
/* 3071 */           if (info_level > max_level)
/*      */           {
/* 3073 */             max_level = info_level;
/*      */             
/* 3075 */             min_rank = Integer.MAX_VALUE;
/* 3076 */             max_rank = -1;
/*      */             
/* 3078 */             oldest_per_rank.clear();
/*      */           }
/*      */           
/* 3081 */           int rank = info.getRank();
/*      */           
/* 3083 */           if (rank < min_rank)
/*      */           {
/* 3085 */             min_rank = rank;
/*      */           }
/* 3087 */           else if (rank > max_rank)
/*      */           {
/* 3089 */             max_rank = rank;
/*      */           }
/*      */           
/* 3092 */           DownloadInfo oldest = (DownloadInfo)oldest_per_rank.get(Integer.valueOf(rank));
/*      */           
/* 3094 */           if (oldest == null)
/*      */           {
/* 3096 */             oldest_per_rank.put(Integer.valueOf(rank), info);
/*      */ 
/*      */ 
/*      */           }
/* 3100 */           else if (info.getLastSeenSecs() < oldest.getLastSeenSecs())
/*      */           {
/* 3102 */             oldest_per_rank.put(Integer.valueOf(rank), info);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3108 */     DownloadInfo to_remove = (DownloadInfo)oldest_per_rank.get(Integer.valueOf(min_rank));
/*      */     
/* 3110 */     if (to_remove != null)
/*      */     {
/* 3112 */       delete(new RelatedContent[] { to_remove }, content_cache, false);
/*      */       
/* 3114 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3119 */     if (max_level == 1)
/*      */     {
/* 3121 */       to_remove = (DownloadInfo)oldest_per_rank.get(Integer.valueOf(max_rank));
/*      */       
/* 3123 */       if (to_remove != null)
/*      */       {
/* 3125 */         int now_secs = (int)(SystemTime.getCurrentTime() / 1000L);
/*      */         
/*      */ 
/*      */ 
/* 3129 */         if (now_secs - to_remove.getLastSeenSecs() >= 86400)
/*      */         {
/* 3131 */           delete(new RelatedContent[] { to_remove }, content_cache, false);
/*      */           
/* 3133 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3138 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public RelatedContent[] getRelatedContent()
/*      */   {
/* 3144 */     synchronized (this.rcm_lock)
/*      */     {
/* 3146 */       ContentCache content_cache = loadRelatedContent();
/*      */       
/* 3148 */       return (RelatedContent[])content_cache.related_content.values().toArray(new DownloadInfo[content_cache.related_content.size()]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected List<DownloadInfo> getRelatedContentAsList()
/*      */   {
/* 3155 */     synchronized (this.rcm_lock)
/*      */     {
/* 3157 */       ContentCache content_cache = loadRelatedContent();
/*      */       
/* 3159 */       return new ArrayList(content_cache.related_content.values());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void reset()
/*      */   {
/* 3166 */     reset(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void reset(boolean reset_perm_dels)
/*      */   {
/* 3173 */     synchronized (this.rcm_lock)
/*      */     {
/* 3175 */       ContentCache cc = this.content_cache == null ? null : (ContentCache)this.content_cache.get();
/*      */       
/* 3177 */       if (cc == null)
/*      */       {
/* 3179 */         FileUtil.deleteResilientConfigFile("rcm.config");
/*      */       }
/*      */       else
/*      */       {
/* 3183 */         cc.related_content = new HashMap();
/* 3184 */         cc.related_content_map = new ByteArrayHashMapEx();
/*      */       }
/*      */       
/* 3187 */       this.pub_download_infos1.clear();
/* 3188 */       this.pub_download_infos2.clear();
/*      */       
/* 3190 */       this.non_pub_download_infos1.clear();
/* 3191 */       this.non_pub_download_infos2.clear();
/*      */       
/* 3193 */       List<DownloadInfo> list = this.download_info_map.values();
/*      */       
/* 3195 */       for (DownloadInfo info : list)
/*      */       {
/* 3197 */         byte nets = info.getNetworksInternal();
/*      */         
/* 3199 */         if (nets != 0)
/*      */         {
/* 3201 */           if ((nets & 0x1) != 0)
/*      */           {
/* 3203 */             this.pub_download_infos1.add(info);
/* 3204 */             this.pub_download_infos2.add(info);
/*      */           }
/*      */           else
/*      */           {
/* 3208 */             this.non_pub_download_infos1.add(info);
/* 3209 */             this.non_pub_download_infos2.add(info);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 3214 */       Collections.shuffle(this.pub_download_infos1);
/* 3215 */       Collections.shuffle(this.non_pub_download_infos1);
/*      */       
/* 3217 */       this.total_unread.set(0);
/*      */       
/* 3219 */       if (reset_perm_dels)
/*      */       {
/* 3221 */         resetPersistentlyDeleted();
/*      */       }
/*      */       
/* 3224 */       setConfigDirty();
/*      */     }
/*      */     
/* 3227 */     this.content_change_dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 3233 */         for (RelatedContentManagerListener l : RelatedContentManager.this.listeners)
/*      */         {
/* 3235 */           l.contentReset();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SearchInstance searchRCM(final Map<String, Object> search_parameters, final SearchObserver observer)
/*      */     throws SearchException
/*      */   {
/* 3248 */     if (!this.initialisation_complete_sem.isReleasedForever())
/*      */     {
/* 3250 */       AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */       
/* 3252 */       final boolean[] cancelled = { false };
/* 3253 */       final boolean[] went_async = { false };
/* 3254 */       final SearchInstance[] si = { null };
/* 3255 */       final SearchException[] error = { null };
/*      */       
/* 3257 */       final AESemaphore temp_sem = new AESemaphore("");
/*      */       
/* 3259 */       dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */           try
/*      */           {
/* 3266 */             si[0] = RelatedContentManager.this.searchRCMSupport(search_parameters, observer);
/*      */             
/* 3268 */             synchronized (cancelled)
/*      */             {
/* 3270 */               if (cancelled[0] != 0)
/*      */               {
/* 3272 */                 si[0].cancel();
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 3277 */             Debug.out(e);
/*      */             
/*      */             SearchException se;
/*      */             SearchException se;
/* 3281 */             if ((e instanceof SearchException))
/*      */             {
/* 3283 */               se = (SearchException)e;
/*      */             }
/*      */             else
/*      */             {
/* 3287 */               se = new SearchException("Search failed", e);
/*      */             }
/*      */             
/* 3290 */             synchronized (cancelled)
/*      */             {
/* 3292 */               error[0] = se;
/*      */               
/* 3294 */               if (went_async[0] != 0)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 3299 */                 observer.complete();
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/* 3304 */             temp_sem.release();
/*      */           }
/*      */           
/*      */         }
/* 3308 */       });
/* 3309 */       temp_sem.reserve(500L);
/*      */       
/* 3311 */       synchronized (cancelled)
/*      */       {
/* 3313 */         if (si[0] != null)
/*      */         {
/* 3315 */           return si[0];
/*      */         }
/*      */         
/* 3318 */         if (error[0] != null)
/*      */         {
/* 3320 */           throw error[0];
/*      */         }
/*      */         
/* 3323 */         went_async[0] = true;
/*      */       }
/*      */       
/* 3326 */       SearchInstance result = new SearchInstance()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void cancel()
/*      */         {
/*      */ 
/* 3333 */           synchronized (cancelled)
/*      */           {
/* 3335 */             if (si[0] != null)
/*      */             {
/* 3337 */               si[0].cancel();
/*      */             }
/*      */             
/* 3340 */             cancelled[0] = true;
/*      */           }
/*      */           
/*      */         }
/* 3344 */       };
/* 3345 */       return result;
/*      */     }
/*      */     
/*      */ 
/* 3349 */     return searchRCMSupport(search_parameters, observer);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private SearchInstance searchRCMSupport(Map<String, Object> search_parameters, SearchObserver observer)
/*      */     throws SearchException
/*      */   {
/* 3360 */     this.initialisation_complete_sem.reserve();
/*      */     
/* 3362 */     if (!this.enabled)
/*      */     {
/* 3364 */       throw new SearchException("rcm is disabled");
/*      */     }
/*      */     
/* 3367 */     String[] networks = (String[])search_parameters.get("n");
/*      */     
/* 3369 */     String target_net = "Public";
/*      */     
/* 3371 */     if (networks != null)
/*      */     {
/* 3373 */       for (String net : networks)
/*      */       {
/* 3375 */         if (net == "Public")
/*      */         {
/* 3377 */           target_net = "Public";
/*      */           
/* 3379 */           break;
/*      */         }
/* 3381 */         if (net == "I2P")
/*      */         {
/* 3383 */           target_net = "I2P";
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3388 */     if (target_net == "I2P")
/*      */     {
/* 3390 */       checkI2PSearcher(true);
/*      */     }
/*      */     
/* 3393 */     for (RelatedContentSearcher searcher : this.searchers)
/*      */     {
/* 3395 */       String net = searcher.getDHTPlugin().getNetwork();
/*      */       
/* 3397 */       if (net == target_net)
/*      */       {
/* 3399 */         return searcher.searchRCM(search_parameters, observer);
/*      */       }
/*      */     }
/*      */     
/* 3403 */     throw new SearchException("no searchers available");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setConfigDirty()
/*      */   {
/* 3410 */     synchronized (this.rcm_lock)
/*      */     {
/* 3412 */       this.content_dirty = true;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int MAX_TAGS_TOTAL_LENGTH = 64;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int PD_BLOOM_INITIAL_SIZE = 1000;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int PD_BLOOM_INCREMENT_SIZE = 1000;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private BloomFilter persist_del_bloom;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void saveRelatedContent(int tick_count)
/*      */   {
/*      */     ContentCache cc;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3640 */     synchronized (this.rcm_lock)
/*      */     {
/* 3642 */       COConfigurationManager.setParameter("rcm.numunread.cache", this.total_unread.get());
/*      */       
/* 3644 */       long now = SystemTime.getMonotonousTime();
/*      */       
/* 3646 */       cc = this.content_cache == null ? null : (ContentCache)this.content_cache.get();
/*      */       
/* 3648 */       if (!this.content_dirty)
/*      */       {
/* 3650 */         if (cc != null)
/*      */         {
/* 3652 */           if (now - this.last_config_access > 60000L)
/*      */           {
/* 3654 */             if (this.content_cache_ref != null)
/*      */             {
/* 3656 */               this.content_discard_ticks = 0;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3663 */             this.content_cache_ref = null;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3672 */         return;
/*      */       }
/*      */       
/* 3675 */       if (tick_count % 10 != 0)
/*      */       {
/* 3677 */         return;
/*      */       }
/*      */       
/* 3680 */       this.last_config_access = now;
/*      */       
/* 3682 */       this.content_dirty = false;
/*      */       
/* 3684 */       if (cc != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3690 */         if (this.persist)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3696 */           Map<String, DownloadInfo> related_content = cc.related_content;
/* 3697 */           ByteArrayHashMapEx<ArrayList<DownloadInfo>> related_content_map = cc.related_content_map;
/*      */           
/* 3699 */           if (related_content.size() == 0)
/*      */           {
/* 3701 */             FileUtil.deleteResilientConfigFile("rcm.config");
/*      */           }
/*      */           else
/*      */           {
/* 3705 */             Map<String, Object> map = new HashMap();
/*      */             
/* 3707 */             Set<Map.Entry<String, DownloadInfo>> rcs = related_content.entrySet();
/*      */             
/* 3709 */             List<Map<String, Object>> rc_map_list = new ArrayList(rcs.size());
/*      */             
/* 3711 */             map.put("rc", rc_map_list);
/*      */             
/* 3713 */             int id = 0;
/*      */             
/* 3715 */             Map<DownloadInfo, Integer> info_map = new HashMap();
/*      */             
/* 3717 */             for (Map.Entry<String, DownloadInfo> entry : rcs)
/*      */             {
/* 3719 */               DownloadInfo info = (DownloadInfo)entry.getValue();
/*      */               
/* 3721 */               Map<String, Object> di_map = serialiseDI(info, cc);
/*      */               
/* 3723 */               if (di_map != null)
/*      */               {
/* 3725 */                 info_map.put(info, Integer.valueOf(id));
/*      */                 
/* 3727 */                 di_map.put("_i", new Long(id));
/* 3728 */                 di_map.put("_k", entry.getKey());
/*      */                 
/* 3730 */                 rc_map_list.add(di_map);
/*      */                 
/* 3732 */                 id++;
/*      */               }
/*      */             }
/*      */             
/* 3736 */             Map<String, Object> rcm_map = new HashMap();
/*      */             
/* 3738 */             map.put("rcm", rcm_map);
/*      */             
/* 3740 */             for (byte[] hash : related_content_map.keys())
/*      */             {
/* 3742 */               List<DownloadInfo> dis = (List)related_content_map.get(hash);
/*      */               
/* 3744 */               int[] ids = new int[dis.size()];
/*      */               
/* 3746 */               int pos = 0;
/*      */               
/* 3748 */               for (DownloadInfo di : dis)
/*      */               {
/* 3750 */                 Integer index = (Integer)info_map.get(di);
/*      */                 
/* 3752 */                 if (index == null) {
/*      */                   break;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3760 */                 ids[(pos++)] = index.intValue();
/*      */               }
/*      */               
/*      */ 
/* 3764 */               if (pos == ids.length)
/*      */               {
/* 3766 */                 ImportExportUtils.exportIntArray(rcm_map, Base32.encode(hash), ids);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 3772 */             ByteArrayOutputStream baos = new ByteArrayOutputStream(102400);
/*      */             try
/*      */             {
/* 3775 */               GZIPOutputStream gos = new GZIPOutputStream(baos);
/*      */               
/* 3777 */               gos.write(BEncoder.encode(map));
/*      */               
/* 3779 */               gos.close();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 3783 */               Debug.out(e);
/*      */             }
/*      */             
/* 3786 */             map.clear();
/*      */             
/* 3788 */             map.put("d", com.aelitis.azureus.core.security.CryptoManagerFactory.getSingleton().obfuscate(baos.toByteArray()));
/*      */             
/*      */ 
/* 3791 */             FileUtil.writeResilientConfigFile("rcm.config", map);
/*      */           }
/*      */         }
/*      */         else {
/* 3795 */           deleteRelatedContent();
/*      */         }
/*      */         
/* 3798 */         for (RelatedContentSearcher searcher : this.searchers)
/*      */         {
/* 3800 */           searcher.updateKeyBloom(cc);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void deleteRelatedContent()
/*      */   {
/* 3809 */     FileUtil.deleteResilientConfigFile("rcm.config");
/* 3810 */     FileUtil.deleteResilientConfigFile("rcmx.config");
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNumUnread()
/*      */   {
/* 3816 */     return this.total_unread.get();
/*      */   }
/*      */   
/*      */ 
/*      */   public void setAllRead()
/*      */   {
/* 3822 */     boolean changed = false;
/*      */     
/* 3824 */     synchronized (this.rcm_lock)
/*      */     {
/* 3826 */       DownloadInfo[] content = (DownloadInfo[])getRelatedContent();
/*      */       
/* 3828 */       for (DownloadInfo c : content)
/*      */       {
/* 3830 */         if (c.isUnread())
/*      */         {
/* 3832 */           changed = true;
/*      */           
/* 3834 */           c.setUnreadInternal(false);
/*      */         }
/*      */       }
/*      */       
/* 3838 */       this.total_unread.set(0);
/*      */     }
/*      */     
/* 3841 */     if (changed)
/*      */     {
/* 3843 */       contentChanged(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void deleteAll()
/*      */   {
/* 3850 */     synchronized (this.rcm_lock)
/*      */     {
/* 3852 */       ContentCache content_cache = loadRelatedContent();
/*      */       
/* 3854 */       addPersistentlyDeleted((RelatedContent[])content_cache.related_content.values().toArray(new DownloadInfo[content_cache.related_content.size()]));
/*      */       
/* 3856 */       reset(false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void incrementUnread()
/*      */   {
/* 3863 */     this.total_unread.incrementAndGet();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void decrementUnread()
/*      */   {
/* 3869 */     synchronized (this.rcm_lock)
/*      */     {
/* 3871 */       int val = this.total_unread.decrementAndGet();
/*      */       
/* 3873 */       if (val < 0)
/*      */       {
/*      */ 
/*      */ 
/* 3877 */         this.total_unread.set(0);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected Download getDownload(byte[] hash)
/*      */   {
/*      */     try
/*      */     {
/* 3887 */       return this.plugin_interface.getDownloadManager().getDownload(hash);
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 3891 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[][] getKeys(Download download)
/*      */   {
/* 3899 */     byte[] tracker_keys = null;
/* 3900 */     byte[] ws_keys = null;
/*      */     try
/*      */     {
/* 3903 */       Torrent torrent = download.getTorrent();
/*      */       
/* 3905 */       if (torrent != null)
/*      */       {
/* 3907 */         TOTorrent to_torrent = PluginCoreUtils.unwrap(torrent);
/*      */         
/* 3909 */         Set<String> tracker_domains = new HashSet();
/*      */         
/* 3911 */         addURLToDomainKeySet(tracker_domains, to_torrent.getAnnounceURL());
/*      */         
/* 3913 */         TOTorrentAnnounceURLGroup group = to_torrent.getAnnounceURLGroup();
/*      */         
/* 3915 */         TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */         
/* 3917 */         for (TOTorrentAnnounceURLSet set : sets)
/*      */         {
/* 3919 */           URL[] urls = set.getAnnounceURLs();
/*      */           
/* 3921 */           for (URL u : urls)
/*      */           {
/* 3923 */             addURLToDomainKeySet(tracker_domains, u);
/*      */           }
/*      */         }
/*      */         
/* 3927 */         tracker_keys = domainsToArray(tracker_domains, 8);
/*      */         
/* 3929 */         Set<String> ws_domains = new HashSet();
/*      */         
/* 3931 */         List getright = BDecoder.decodeStrings(getURLList(to_torrent, "url-list"));
/* 3932 */         List webseeds = BDecoder.decodeStrings(getURLList(to_torrent, "httpseeds"));
/*      */         
/* 3934 */         for (List l : new List[] { getright, webseeds })
/*      */         {
/* 3936 */           for (Object o : l)
/*      */           {
/* 3938 */             if ((o instanceof String)) {
/*      */               try
/*      */               {
/* 3941 */                 addURLToDomainKeySet(ws_domains, new URL((String)o));
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 3950 */         ws_keys = domainsToArray(ws_domains, 3);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 3955 */     return new byte[][] { tracker_keys, ws_keys };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] domainsToArray(Set<String> domains, int max)
/*      */   {
/* 3963 */     int entries = Math.min(domains.size(), max);
/*      */     
/* 3965 */     if (entries > 0)
/*      */     {
/* 3967 */       byte[] keys = new byte[entries * 4];
/*      */       
/* 3969 */       int pos = 0;
/*      */       
/* 3971 */       for (String dom : domains)
/*      */       {
/* 3973 */         int hash = dom.hashCode();
/*      */         
/* 3975 */         byte[] bytes = { (byte)(hash >> 24), (byte)(hash >> 16), (byte)(hash >> 8), (byte)hash };
/*      */         
/* 3977 */         System.arraycopy(bytes, 0, keys, pos, 4);
/*      */         
/* 3979 */         pos += 4;
/*      */       }
/*      */       
/* 3982 */       return keys;
/*      */     }
/*      */     
/* 3985 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected List getURLList(TOTorrent torrent, String key)
/*      */   {
/* 3993 */     Object obj = torrent.getAdditionalProperty(key);
/*      */     
/* 3995 */     if ((obj instanceof byte[]))
/*      */     {
/* 3997 */       List l = new ArrayList();
/*      */       
/* 3999 */       l.add(obj);
/*      */       
/* 4001 */       return l;
/*      */     }
/* 4003 */     if ((obj instanceof List))
/*      */     {
/* 4005 */       return (List)BEncoder.clone(obj);
/*      */     }
/*      */     
/*      */ 
/* 4009 */     return new ArrayList();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addURLToDomainKeySet(Set<String> set, URL u)
/*      */   {
/* 4018 */     String prot = u.getProtocol();
/*      */     
/* 4020 */     if (prot != null)
/*      */     {
/* 4022 */       if ((prot.equalsIgnoreCase("http")) || (prot.equalsIgnoreCase("udp")))
/*      */       {
/* 4024 */         String host = u.getHost().toLowerCase(Locale.US);
/*      */         
/* 4026 */         if (host.contains(":"))
/*      */         {
/*      */ 
/*      */ 
/* 4030 */           return;
/*      */         }
/*      */         
/* 4033 */         String[] bits = host.split("\\.");
/*      */         
/* 4035 */         int len = bits.length;
/*      */         
/* 4037 */         if (len >= 2)
/*      */         {
/* 4039 */           String end = bits[(len - 1)];
/*      */           
/* 4041 */           char[] chars = end.toCharArray();
/*      */           
/*      */ 
/*      */ 
/* 4045 */           boolean all_digits = true;
/*      */           
/* 4047 */           for (char c : chars)
/*      */           {
/* 4049 */             if (!Character.isDigit(c))
/*      */             {
/* 4051 */               all_digits = false;
/*      */               
/* 4053 */               break;
/*      */             }
/*      */           }
/*      */           
/* 4057 */           if (!all_digits)
/*      */           {
/* 4059 */             set.add(bits[(len - 2)] + "." + end);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private byte getNetworks(Download download)
/*      */   {
/* 4070 */     String[] networks = download.getListAttribute(this.ta_networks);
/*      */     
/* 4072 */     if (networks == null)
/*      */     {
/* 4074 */       return 0;
/*      */     }
/*      */     
/*      */ 
/* 4078 */     return convertNetworks(networks);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String[] convertNetworks(byte net)
/*      */   {
/* 4086 */     if (net == 0)
/* 4087 */       return new String[0];
/* 4088 */     if (net == 1)
/* 4089 */       return NET_PUBLIC_ARRAY;
/* 4090 */     if (net == 2)
/* 4091 */       return NET_I2P_ARRAY;
/* 4092 */     if (net == 4)
/* 4093 */       return NET_TOR_ARRAY;
/* 4094 */     if (net == 3) {
/* 4095 */       return NET_PUBLIC_AND_I2P_ARRAY;
/*      */     }
/* 4097 */     List<String> nets = new ArrayList();
/*      */     
/* 4099 */     if ((net & 0x1) != 0) {
/* 4100 */       nets.add("Public");
/*      */     }
/* 4102 */     if ((net & 0x2) != 0) {
/* 4103 */       nets.add("I2P");
/*      */     }
/* 4105 */     if ((net & 0x4) != 0) {
/* 4106 */       nets.add("Tor");
/*      */     }
/*      */     
/* 4109 */     return (String[])nets.toArray(new String[nets.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte convertNetworks(String[] networks)
/*      */   {
/* 4117 */     byte nets = 0;
/*      */     
/* 4119 */     for (int i = 0; i < networks.length; i++)
/*      */     {
/* 4121 */       String n = networks[i];
/*      */       
/* 4123 */       if (n.equalsIgnoreCase("Public"))
/*      */       {
/* 4125 */         nets = (byte)(nets | 0x1);
/*      */       }
/* 4127 */       else if (n.equalsIgnoreCase("I2P"))
/*      */       {
/* 4129 */         nets = (byte)(nets | 0x2);
/*      */       }
/* 4131 */       else if (n.equalsIgnoreCase("Tor"))
/*      */       {
/* 4133 */         nets = (byte)(nets | 0x4);
/*      */       }
/*      */     }
/*      */     
/* 4137 */     return nets;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String[] getTags(Download download)
/*      */   {
/* 4144 */     Set<String> all_tags = new HashSet();
/*      */     
/* 4146 */     if (this.tag_manager.isEnabled())
/*      */     {
/* 4148 */       String cat_name = this.ta_category == null ? null : download.getAttribute(this.ta_category);
/*      */       
/* 4150 */       if (cat_name != null)
/*      */       {
/* 4152 */         Tag cat_tag = this.tag_manager.getTagType(1).getTag(cat_name, true);
/*      */         
/* 4154 */         if ((cat_tag != null) && (cat_tag.isPublic()))
/*      */         {
/* 4156 */           all_tags.add(cat_name.toLowerCase(Locale.US));
/*      */         }
/*      */       }
/*      */       
/* 4160 */       List<Tag> tags = this.tag_manager.getTagType(3).getTagsForTaggable(PluginCoreUtils.unwrap(download));
/*      */       
/* 4162 */       for (Tag t : tags)
/*      */       {
/* 4164 */         if (t.isPublic())
/*      */         {
/* 4166 */           all_tags.add(t.getTagName(true).toLowerCase(Locale.US));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4171 */     String[] networks = download.getListAttribute(this.ta_networks);
/*      */     
/* 4173 */     for (String network : networks)
/*      */     {
/* 4175 */       if (!network.equals("Public"))
/*      */       {
/* 4177 */         if (com.aelitis.azureus.core.proxy.impl.AEPluginProxyHandler.hasPluginProxyForNetwork(network, true))
/*      */         {
/* 4179 */           all_tags.add("_" + network.toLowerCase(Locale.US) + "_");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4184 */     if (all_tags.size() == 0)
/*      */     {
/* 4186 */       return null;
/*      */     }
/* 4188 */     if (all_tags.size() == 1)
/*      */     {
/* 4190 */       return new String[] { (String)all_tags.iterator().next() };
/*      */     }
/*      */     
/*      */ 
/* 4194 */     List<String> temp = new ArrayList(all_tags);
/*      */     
/* 4196 */     Collections.shuffle(temp);
/*      */     
/* 4198 */     return (String[])temp.toArray(new String[temp.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] encodeTags(String[] tags)
/*      */   {
/* 4209 */     if ((tags == null) || (tags.length == 0))
/*      */     {
/* 4211 */       return null;
/*      */     }
/*      */     
/* 4214 */     byte[] temp = new byte[64];
/* 4215 */     int pos = 0;
/* 4216 */     int rem = temp.length;
/*      */     
/* 4218 */     for (int i = 0; i < tags.length; i++)
/*      */     {
/* 4220 */       String tag = tags[i];
/*      */       
/* 4222 */       tag = truncateTag(tag);
/*      */       try
/*      */       {
/* 4225 */         byte[] tag_bytes = tag.getBytes("UTF-8");
/*      */         
/* 4227 */         int tb_len = tag_bytes.length;
/*      */         
/* 4229 */         if (rem < tb_len + 1) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 4234 */         temp[(pos++)] = ((byte)tb_len);
/*      */         
/* 4236 */         System.arraycopy(tag_bytes, 0, temp, pos, tb_len);
/*      */         
/* 4238 */         pos += tb_len;
/* 4239 */         rem -= tb_len + 1;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4246 */     if (pos == 0)
/*      */     {
/* 4248 */       return null;
/*      */     }
/*      */     
/*      */ 
/* 4252 */     byte[] result = new byte[pos];
/*      */     
/* 4254 */     System.arraycopy(temp, 0, result, 0, pos);
/*      */     
/* 4256 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String truncateTag(String tag)
/*      */   {
/* 4264 */     if (tag.length() > 20)
/*      */     {
/* 4266 */       tag = tag.substring(0, 20);
/*      */     }
/*      */     for (;;) {
/* 4269 */       if (tag.length() > 0) {
/*      */         try
/*      */         {
/* 4272 */           byte[] tag_bytes = tag.getBytes("UTF-8");
/*      */           
/* 4274 */           if (tag_bytes.length > 20)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4280 */             tag = tag.substring(0, tag.length() - 1);
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4289 */     return tag;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String[] decodeTags(byte[] bytes)
/*      */   {
/* 4296 */     if ((bytes == null) || (bytes.length == 0))
/*      */     {
/* 4298 */       return null;
/*      */     }
/*      */     
/* 4301 */     List<String> tags = new ArrayList(10);
/*      */     
/* 4303 */     int pos = 0;
/*      */     
/* 4305 */     while (pos < bytes.length)
/*      */     {
/* 4307 */       int tag_len = bytes[(pos++)] & 0xFF;
/*      */       
/* 4309 */       if (tag_len > 20) {
/*      */         break;
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 4315 */         tags.add(new String(bytes, pos, tag_len, "UTF-8"));
/*      */         
/* 4317 */         pos += tag_len;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/*      */     
/* 4325 */     if (tags.size() == 0)
/*      */     {
/* 4327 */       return null;
/*      */     }
/*      */     
/*      */ 
/* 4331 */     return (String[])tags.toArray(new String[tags.size()]);
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
/*      */   protected byte[] getPermDelKey(RelatedContent info)
/*      */   {
/* 4345 */     byte[] bytes = info.getHash();
/*      */     
/* 4347 */     if (bytes == null) {
/*      */       try
/*      */       {
/* 4350 */         bytes = new SHA1Simple().calculateHash(getPrivateInfoKey(info).getBytes("ISO-8859-1"));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 4354 */         Debug.out(e);
/*      */         
/* 4356 */         return null;
/*      */       }
/*      */     }
/*      */     
/* 4360 */     byte[] key = new byte[8];
/*      */     
/* 4362 */     System.arraycopy(bytes, 0, key, 0, 8);
/*      */     
/* 4364 */     return key;
/*      */   }
/*      */   
/*      */ 
/*      */   protected List<byte[]> loadPersistentlyDeleted()
/*      */   {
/* 4370 */     List<byte[]> entries = null;
/*      */     
/* 4372 */     if (FileUtil.resilientConfigFileExists("rcmx.config"))
/*      */     {
/* 4374 */       Map<String, Object> map = FileUtil.readResilientConfigFile("rcmx.config");
/*      */       
/* 4376 */       entries = (List)map.get("entries");
/*      */     }
/*      */     
/* 4379 */     if (entries == null)
/*      */     {
/* 4381 */       entries = new ArrayList(0);
/*      */     }
/*      */     
/* 4384 */     return entries;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void addPersistentlyDeleted(RelatedContent[] content)
/*      */   {
/* 4391 */     if (content.length == 0)
/*      */     {
/* 4393 */       return;
/*      */     }
/*      */     
/* 4396 */     List<byte[]> entries = loadPersistentlyDeleted();
/*      */     
/* 4398 */     List<byte[]> new_keys = new ArrayList(content.length);
/*      */     
/* 4400 */     for (RelatedContent rc : content)
/*      */     {
/* 4402 */       byte[] key = getPermDelKey(rc);
/*      */       
/* 4404 */       new_keys.add(key);
/*      */       
/* 4406 */       entries.add(key);
/*      */     }
/*      */     
/* 4409 */     Map<String, Object> map = new HashMap();
/*      */     
/* 4411 */     map.put("entries", entries);
/*      */     
/* 4413 */     FileUtil.writeResilientConfigFile("rcmx.config", map);
/*      */     
/* 4415 */     if (this.persist_del_bloom != null)
/*      */     {
/* 4417 */       if (this.persist_del_bloom.getSize() / (this.persist_del_bloom.getEntryCount() + content.length) < 10)
/*      */       {
/* 4419 */         this.persist_del_bloom = BloomFilterFactory.createAddOnly(Math.max(1000, this.persist_del_bloom.getSize() * 10 + 1000 + content.length));
/*      */         
/* 4421 */         for (byte[] k : entries)
/*      */         {
/* 4423 */           this.persist_del_bloom.add(k);
/*      */         }
/*      */       }
/*      */       else {
/* 4427 */         for (byte[] k : new_keys)
/*      */         {
/* 4429 */           this.persist_del_bloom.add(k);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean isPersistentlyDeleted(RelatedContent content)
/*      */   {
/* 4439 */     if (this.persist_del_bloom == null)
/*      */     {
/* 4441 */       List<byte[]> entries = loadPersistentlyDeleted();
/*      */       
/* 4443 */       this.persist_del_bloom = BloomFilterFactory.createAddOnly(Math.max(1000, entries.size() * 10 + 1000));
/*      */       
/* 4445 */       for (byte[] k : entries)
/*      */       {
/* 4447 */         this.persist_del_bloom.add(k);
/*      */       }
/*      */     }
/*      */     
/* 4451 */     byte[] key = getPermDelKey(content);
/*      */     
/* 4453 */     return this.persist_del_bloom.contains(key);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void resetPersistentlyDeleted()
/*      */   {
/* 4459 */     FileUtil.deleteResilientConfigFile("rcmx.config");
/*      */     
/* 4461 */     this.persist_del_bloom = BloomFilterFactory.createAddOnly(1000);
/*      */   }
/*      */   
/*      */ 
/*      */   public void reserveTemporarySpace()
/*      */   {
/* 4467 */     this.temporary_space.addAndGet(50);
/*      */   }
/*      */   
/*      */ 
/*      */   public void releaseTemporarySpace()
/*      */   {
/* 4473 */     boolean reset_explicit = this.temporary_space.addAndGet(-50) == 0;
/*      */     
/* 4475 */     enforceMaxResults(reset_explicit);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void enforceMaxResults(boolean reset_explicit)
/*      */   {
/* 4482 */     synchronized (this.rcm_lock)
/*      */     {
/* 4484 */       ContentCache content_cache = loadRelatedContent();
/*      */       
/* 4486 */       enforceMaxResults(content_cache, reset_explicit);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void enforceMaxResults(ContentCache content_cache, boolean reset_explicit)
/*      */   {
/* 4495 */     Map<String, DownloadInfo> related_content = content_cache.related_content;
/*      */     
/* 4497 */     int num_to_remove = related_content.size() - (this.max_results + this.temporary_space.get());
/*      */     
/* 4499 */     if (num_to_remove > 0)
/*      */     {
/* 4501 */       List<DownloadInfo> infos = new ArrayList(related_content.values());
/*      */       
/* 4503 */       if (reset_explicit)
/*      */       {
/* 4505 */         for (DownloadInfo info : infos)
/*      */         {
/* 4507 */           if (info.isExplicit())
/*      */           {
/* 4509 */             info.setExplicit(false);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 4514 */       Collections.sort(infos, new java.util.Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(RelatedContentManager.DownloadInfo o1, RelatedContentManager.DownloadInfo o2)
/*      */         {
/*      */ 
/*      */ 
/* 4523 */           int res = o2.getLevel() - o1.getLevel();
/*      */           
/* 4525 */           if (res != 0)
/*      */           {
/* 4527 */             return res;
/*      */           }
/*      */           
/* 4530 */           res = o1.getRank() - o2.getRank();
/*      */           
/* 4532 */           if (res != 0)
/*      */           {
/* 4534 */             return res;
/*      */           }
/*      */           
/* 4537 */           return o1.getLastSeenSecs() - o2.getLastSeenSecs();
/*      */         }
/*      */         
/* 4540 */       });
/* 4541 */       List<RelatedContent> to_remove = new ArrayList();
/*      */       
/* 4543 */       for (int i = 0; i < Math.min(num_to_remove, infos.size()); i++)
/*      */       {
/* 4545 */         to_remove.add(infos.get(i));
/*      */       }
/*      */       
/* 4548 */       if (to_remove.size() > 0)
/*      */       {
/* 4550 */         delete((RelatedContent[])to_remove.toArray(new RelatedContent[to_remove.size()]), content_cache, false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(RelatedContentManagerListener listener)
/*      */   {
/* 4559 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(RelatedContentManagerListener listener)
/*      */   {
/* 4566 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static class ByteArrayHashMapEx<T>
/*      */     extends ByteArrayHashMap<T>
/*      */   {
/*      */     public T getRandomValueExcluding(T excluded)
/*      */     {
/* 4577 */       int num = RandomUtils.nextInt(this.size);
/*      */       
/* 4579 */       T result = null;
/*      */       
/* 4581 */       for (int j = 0; j < this.table.length; j++)
/*      */       {
/* 4583 */         ByteArrayHashMap.Entry<T> e = this.table[j];
/*      */         
/* 4585 */         while (e != null)
/*      */         {
/* 4587 */           T value = e.value;
/*      */           
/* 4589 */           if (value != excluded)
/*      */           {
/* 4591 */             result = value;
/*      */           }
/*      */           
/* 4594 */           if ((num <= 0) && (result != null))
/*      */           {
/* 4596 */             return result;
/*      */           }
/*      */           
/* 4599 */           num--;
/*      */           
/* 4601 */           e = e.next;
/*      */         }
/*      */       }
/*      */       
/* 4605 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Map<String, Object> serialiseDI(DownloadInfo info, ContentCache cc)
/*      */   {
/*      */     try
/*      */     {
/* 4615 */       Map<String, Object> info_map = new HashMap();
/*      */       
/* 4617 */       ImportExportUtils.exportLong(info_map, "v", info.getVersion());
/*      */       
/* 4619 */       info_map.put("h", info.getHash());
/*      */       
/* 4621 */       ImportExportUtils.exportString(info_map, "d", info.getTitle());
/* 4622 */       ImportExportUtils.exportInt(info_map, "r", info.getRand());
/* 4623 */       ImportExportUtils.exportString(info_map, "t", info.getTracker());
/* 4624 */       ImportExportUtils.exportLong(info_map, "z", info.getSize());
/*      */       
/* 4626 */       ImportExportUtils.exportInt(info_map, "p", (int)(info.getPublishDate() / 3600000L));
/* 4627 */       ImportExportUtils.exportInt(info_map, "q", info.getSeeds() << 16 | info.getLeechers() & 0xFFFF);
/* 4628 */       ImportExportUtils.exportInt(info_map, "c", (int)info.getContentNetwork());
/*      */       
/* 4630 */       byte[] tracker_keys = info.getTrackerKeys();
/* 4631 */       if (tracker_keys != null) {
/* 4632 */         info_map.put("k", tracker_keys);
/*      */       }
/*      */       
/* 4635 */       byte[] ws_keys = info.getWebSeedKeys();
/* 4636 */       if (ws_keys != null) {
/* 4637 */         info_map.put("w", ws_keys);
/*      */       }
/*      */       
/* 4640 */       String[] tags = info.getTags();
/* 4641 */       if (tags != null) {
/* 4642 */         info_map.put("g", encodeTags(tags));
/*      */       }
/*      */       
/* 4645 */       byte nets = info.getNetworksInternal();
/* 4646 */       if (nets != 1) {
/* 4647 */         info_map.put("o", new Long(nets & 0xFF));
/*      */       }
/*      */       
/* 4650 */       if (cc != null)
/*      */       {
/* 4652 */         ImportExportUtils.exportBoolean(info_map, "u", info.isUnread());
/* 4653 */         ImportExportUtils.exportIntArray(info_map, "l", info.getRandList());
/* 4654 */         ImportExportUtils.exportInt(info_map, "s", info.getLastSeenSecs());
/* 4655 */         ImportExportUtils.exportInt(info_map, "e", info.getLevel());
/*      */       }
/*      */       
/* 4658 */       ImportExportUtils.exportLong(info_map, "cl", info.getChangedLocallyOn());
/*      */       
/* 4660 */       return info_map;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 4664 */       Debug.out(e);
/*      */     }
/* 4666 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private DownloadInfo deserialiseDI(Map<String, Object> info_map, ContentCache cc)
/*      */   {
/*      */     try
/*      */     {
/* 4676 */       int version = (int)ImportExportUtils.importLong(info_map, "v", 0L);
/* 4677 */       byte[] hash = (byte[])info_map.get("h");
/* 4678 */       String title = ImportExportUtils.importString(info_map, "d");
/* 4679 */       int rand = ImportExportUtils.importInt(info_map, "r");
/* 4680 */       String tracker = ImportExportUtils.importString(info_map, "t");
/* 4681 */       long size = ImportExportUtils.importLong(info_map, "z");
/*      */       
/* 4683 */       int date = ImportExportUtils.importInt(info_map, "p", 0);
/* 4684 */       int seeds_leechers = ImportExportUtils.importInt(info_map, "q", -1);
/* 4685 */       byte cnet = (byte)ImportExportUtils.importInt(info_map, "c", -1);
/* 4686 */       byte[] tracker_keys = (byte[])info_map.get("k");
/* 4687 */       byte[] ws_keys = (byte[])info_map.get("w");
/* 4688 */       long lastChangedLocally = ImportExportUtils.importLong(info_map, "cl");
/*      */       
/* 4690 */       if ((tracker_keys != null) && (tracker_keys.length % 4 != 0))
/*      */       {
/* 4692 */         tracker_keys = null;
/*      */       }
/*      */       
/* 4695 */       if ((ws_keys != null) && (ws_keys.length % 4 != 0))
/*      */       {
/* 4697 */         ws_keys = null;
/*      */       }
/*      */       
/* 4700 */       byte[] _tags = (byte[])info_map.get("g");
/*      */       
/* 4702 */       String[] tags = decodeTags(_tags);
/*      */       
/* 4704 */       Long _nets = (Long)info_map.get("o");
/*      */       
/* 4706 */       byte nets = _nets == null ? 1 : _nets.byteValue();
/*      */       
/* 4708 */       if (cc == null)
/*      */       {
/* 4710 */         DownloadInfo info = new DownloadInfo(version, hash, hash, title, rand, tracker, tracker_keys, ws_keys, tags, nets, 0, false, size, date, seeds_leechers, cnet);
/*      */         
/* 4712 */         info.setChangedLocallyOn(lastChangedLocally);
/*      */         
/* 4714 */         return info;
/*      */       }
/*      */       
/*      */ 
/* 4718 */       boolean unread = ImportExportUtils.importBoolean(info_map, "u");
/*      */       
/* 4720 */       int[] rand_list = ImportExportUtils.importIntArray(info_map, "l");
/*      */       
/* 4722 */       int last_seen = ImportExportUtils.importInt(info_map, "s");
/*      */       
/* 4724 */       int level = ImportExportUtils.importInt(info_map, "e");
/*      */       
/* 4726 */       DownloadInfo info = new DownloadInfo(version, hash, title, rand, tracker, tracker_keys, ws_keys, tags, nets, unread, rand_list, last_seen, level, size, date, seeds_leechers, cnet, cc);
/*      */       
/* 4728 */       info.setChangedLocallyOn(lastChangedLocally);
/*      */       
/* 4730 */       return info;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 4734 */       Debug.out(e);
/*      */     }
/* 4736 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void dump()
/*      */   {
/* 4743 */     RelatedContent[] related_content = getRelatedContent();
/*      */     
/* 4745 */     ByteArrayHashMap<List<String>> tk_map = new ByteArrayHashMap();
/* 4746 */     ByteArrayHashMap<List<String>> ws_map = new ByteArrayHashMap();
/*      */     
/* 4748 */     for (RelatedContent rc : related_content)
/*      */     {
/* 4750 */       byte[] tracker_keys = rc.getTrackerKeys();
/*      */       
/* 4752 */       if (tracker_keys != null)
/*      */       {
/* 4754 */         for (int i = 0; i < tracker_keys.length; i += 4)
/*      */         {
/* 4756 */           byte[] tk = new byte[4];
/*      */           
/* 4758 */           System.arraycopy(tracker_keys, i, tk, 0, 4);
/*      */           
/* 4760 */           List<String> titles = (List)tk_map.get(tk);
/*      */           
/* 4762 */           if (titles == null)
/*      */           {
/* 4764 */             titles = new ArrayList();
/*      */             
/* 4766 */             tk_map.put(tk, titles);
/*      */           }
/*      */           
/* 4769 */           titles.add(rc.getTitle());
/*      */         }
/*      */       }
/* 4772 */       byte[] ws_keys = rc.getWebSeedKeys();
/*      */       
/* 4774 */       if (ws_keys != null)
/*      */       {
/* 4776 */         for (int i = 0; i < ws_keys.length; i += 4)
/*      */         {
/* 4778 */           byte[] wk = new byte[4];
/*      */           
/* 4780 */           System.arraycopy(ws_keys, i, wk, 0, 4);
/*      */           
/* 4782 */           List<String> titles = (List)ws_map.get(wk);
/*      */           
/* 4784 */           if (titles == null)
/*      */           {
/* 4786 */             titles = new ArrayList();
/*      */             
/* 4788 */             ws_map.put(wk, titles);
/*      */           }
/*      */           
/* 4791 */           titles.add(rc.getTitle());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4796 */     System.out.println("-- Trackers --");
/*      */     
/* 4798 */     for (byte[] key : tk_map.keys())
/*      */     {
/* 4800 */       List<String> titles = (List)tk_map.get(key);
/*      */       
/* 4802 */       System.out.println(ByteFormatter.encodeString(key));
/*      */       
/* 4804 */       for (String title : titles)
/*      */       {
/* 4806 */         System.out.println("    " + title);
/*      */       }
/*      */     }
/*      */     
/* 4810 */     System.out.println("-- Web Seeds --");
/*      */     
/* 4812 */     for (byte[] key : ws_map.keys())
/*      */     {
/* 4814 */       List<String> titles = (List)ws_map.get(key);
/*      */       
/* 4816 */       System.out.println(ByteFormatter.encodeString(key));
/*      */       
/* 4818 */       for (String title : titles)
/*      */       {
/* 4820 */         System.out.println("    " + title);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected ContentCache loadRelatedContent()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: iconst_0
/*      */     //   1: istore_1
/*      */     //   2: aload_0
/*      */     //   3: getfield 1747	com/aelitis/azureus/core/content/RelatedContentManager:rcm_lock	Ljava/lang/Object;
/*      */     //   6: dup
/*      */     //   7: astore_2
/*      */     //   8: monitorenter
/*      */     //   9: aload_0
/*      */     //   10: invokestatic 2041	org/gudy/azureus2/core3/util/SystemTime:getMonotonousTime	()J
/*      */     //   13: putfield 1728	com/aelitis/azureus/core/content/RelatedContentManager:last_config_access	J
/*      */     //   16: aload_0
/*      */     //   17: getfield 1752	com/aelitis/azureus/core/content/RelatedContentManager:content_cache	Ljava/lang/ref/WeakReference;
/*      */     //   20: ifnonnull +7 -> 27
/*      */     //   23: aconst_null
/*      */     //   24: goto +13 -> 37
/*      */     //   27: aload_0
/*      */     //   28: getfield 1752	com/aelitis/azureus/core/content/RelatedContentManager:content_cache	Ljava/lang/ref/WeakReference;
/*      */     //   31: invokevirtual 1962	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
/*      */     //   34: checkcast 1005	com/aelitis/azureus/core/content/RelatedContentManager$ContentCache
/*      */     //   37: astore_3
/*      */     //   38: aload_3
/*      */     //   39: ifnonnull +790 -> 829
/*      */     //   42: iconst_1
/*      */     //   43: istore_1
/*      */     //   44: new 1005	com/aelitis/azureus/core/content/RelatedContentManager$ContentCache
/*      */     //   47: dup
/*      */     //   48: invokespecial 1859	com/aelitis/azureus/core/content/RelatedContentManager$ContentCache:<init>	()V
/*      */     //   51: astore_3
/*      */     //   52: aload_0
/*      */     //   53: new 1040	java/lang/ref/WeakReference
/*      */     //   56: dup
/*      */     //   57: aload_3
/*      */     //   58: invokespecial 1963	java/lang/ref/WeakReference:<init>	(Ljava/lang/Object;)V
/*      */     //   61: putfield 1752	com/aelitis/azureus/core/content/RelatedContentManager:content_cache	Ljava/lang/ref/WeakReference;
/*      */     //   64: iconst_0
/*      */     //   65: istore 4
/*      */     //   67: ldc_w 953
/*      */     //   70: invokestatic 2032	org/gudy/azureus2/core3/util/FileUtil:resilientConfigFileExists	(Ljava/lang/String;)Z
/*      */     //   73: ifeq +711 -> 784
/*      */     //   76: ldc_w 953
/*      */     //   79: invokestatic 2033	org/gudy/azureus2/core3/util/FileUtil:readResilientConfigFile	(Ljava/lang/String;)Ljava/util/Map;
/*      */     //   82: astore 5
/*      */     //   84: aload_3
/*      */     //   85: getfield 1769	com/aelitis/azureus/core/content/RelatedContentManager$ContentCache:related_content	Ljava/util/Map;
/*      */     //   88: astore 6
/*      */     //   90: aload_3
/*      */     //   91: getfield 1768	com/aelitis/azureus/core/content/RelatedContentManager$ContentCache:related_content_map	Lcom/aelitis/azureus/core/content/RelatedContentManager$ByteArrayHashMapEx;
/*      */     //   94: astore 7
/*      */     //   96: aload 5
/*      */     //   98: ldc 12
/*      */     //   100: invokeinterface 2085 2 0
/*      */     //   105: checkcast 959	[B
/*      */     //   108: checkcast 959	[B
/*      */     //   111: astore 9
/*      */     //   113: aload 9
/*      */     //   115: ifnull +53 -> 168
/*      */     //   118: new 1025	java/io/BufferedInputStream
/*      */     //   121: dup
/*      */     //   122: new 1056	java/util/zip/GZIPInputStream
/*      */     //   125: dup
/*      */     //   126: new 1026	java/io/ByteArrayInputStream
/*      */     //   129: dup
/*      */     //   130: invokestatic 1901	com/aelitis/azureus/core/security/CryptoManagerFactory:getSingleton	()Lcom/aelitis/azureus/core/security/CryptoManager;
/*      */     //   133: aload 9
/*      */     //   135: invokeinterface 2051 2 0
/*      */     //   140: invokespecial 1923	java/io/ByteArrayInputStream:<init>	([B)V
/*      */     //   143: invokespecial 1998	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
/*      */     //   146: invokespecial 1922	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
/*      */     //   149: invokestatic 2020	org/gudy/azureus2/core3/util/BDecoder:decode	(Ljava/io/BufferedInputStream;)Ljava/util/Map;
/*      */     //   152: astore 5
/*      */     //   154: goto +14 -> 168
/*      */     //   157: astore 10
/*      */     //   159: new 1045	java/util/HashMap
/*      */     //   162: dup
/*      */     //   163: invokespecial 1977	java/util/HashMap:<init>	()V
/*      */     //   166: astore 5
/*      */     //   168: aload 5
/*      */     //   170: ldc_w 951
/*      */     //   173: invokeinterface 2085 2 0
/*      */     //   178: checkcast 1051	java/util/Map
/*      */     //   181: astore 8
/*      */     //   183: aload 5
/*      */     //   185: ldc_w 950
/*      */     //   188: invokeinterface 2085 2 0
/*      */     //   193: astore 10
/*      */     //   195: aload 10
/*      */     //   197: ifnull +587 -> 784
/*      */     //   200: aload 8
/*      */     //   202: ifnull +582 -> 784
/*      */     //   205: new 1045	java/util/HashMap
/*      */     //   208: dup
/*      */     //   209: invokespecial 1977	java/util/HashMap:<init>	()V
/*      */     //   212: astore 11
/*      */     //   214: aload 10
/*      */     //   216: instanceof 1051
/*      */     //   219: ifeq +151 -> 370
/*      */     //   222: aload 10
/*      */     //   224: checkcast 1051	java/util/Map
/*      */     //   227: astore 12
/*      */     //   229: aload 12
/*      */     //   231: invokeinterface 2083 1 0
/*      */     //   236: invokeinterface 2092 1 0
/*      */     //   241: astore 13
/*      */     //   243: aload 13
/*      */     //   245: invokeinterface 2070 1 0
/*      */     //   250: ifeq +117 -> 367
/*      */     //   253: aload 13
/*      */     //   255: invokeinterface 2071 1 0
/*      */     //   260: checkcast 1052	java/util/Map$Entry
/*      */     //   263: astore 14
/*      */     //   265: aload 14
/*      */     //   267: invokeinterface 2087 1 0
/*      */     //   272: checkcast 1036	java/lang/String
/*      */     //   275: astore 15
/*      */     //   277: aload 14
/*      */     //   279: invokeinterface 2088 1 0
/*      */     //   284: checkcast 1051	java/util/Map
/*      */     //   287: astore 16
/*      */     //   289: aload_0
/*      */     //   290: aload 16
/*      */     //   292: aload_3
/*      */     //   293: invokespecial 1823	com/aelitis/azureus/core/content/RelatedContentManager:deserialiseDI	(Ljava/util/Map;Lcom/aelitis/azureus/core/content/RelatedContentManager$ContentCache;)Lcom/aelitis/azureus/core/content/RelatedContentManager$DownloadInfo;
/*      */     //   296: astore 17
/*      */     //   298: aload 17
/*      */     //   300: invokevirtual 1873	com/aelitis/azureus/core/content/RelatedContentManager$DownloadInfo:isUnread	()Z
/*      */     //   303: ifeq +6 -> 309
/*      */     //   306: iinc 4 1
/*      */     //   309: aload 6
/*      */     //   311: aload 15
/*      */     //   313: aload 17
/*      */     //   315: invokeinterface 2086 3 0
/*      */     //   320: pop
/*      */     //   321: aload 16
/*      */     //   323: ldc_w 931
/*      */     //   326: invokeinterface 2085 2 0
/*      */     //   331: checkcast 1033	java/lang/Long
/*      */     //   334: invokevirtual 1934	java/lang/Long:intValue	()I
/*      */     //   337: istore 18
/*      */     //   339: aload 11
/*      */     //   341: iload 18
/*      */     //   343: invokestatic 1931	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */     //   346: aload 17
/*      */     //   348: invokeinterface 2086 3 0
/*      */     //   353: pop
/*      */     //   354: goto +10 -> 364
/*      */     //   357: astore 15
/*      */     //   359: aload 15
/*      */     //   361: invokestatic 2030	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   364: goto -121 -> 243
/*      */     //   367: goto +146 -> 513
/*      */     //   370: aload 10
/*      */     //   372: checkcast 1049	java/util/List
/*      */     //   375: astore 12
/*      */     //   377: aload 12
/*      */     //   379: invokeinterface 2076 1 0
/*      */     //   384: astore 13
/*      */     //   386: aload 13
/*      */     //   388: invokeinterface 2070 1 0
/*      */     //   393: ifeq +120 -> 513
/*      */     //   396: aload 13
/*      */     //   398: invokeinterface 2071 1 0
/*      */     //   403: checkcast 1051	java/util/Map
/*      */     //   406: astore 14
/*      */     //   408: new 1036	java/lang/String
/*      */     //   411: dup
/*      */     //   412: aload 14
/*      */     //   414: ldc_w 932
/*      */     //   417: invokeinterface 2085 2 0
/*      */     //   422: checkcast 959	[B
/*      */     //   425: checkcast 959	[B
/*      */     //   428: ldc 8
/*      */     //   430: invokespecial 1952	java/lang/String:<init>	([BLjava/lang/String;)V
/*      */     //   433: astore 15
/*      */     //   435: aload_0
/*      */     //   436: aload 14
/*      */     //   438: aload_3
/*      */     //   439: invokespecial 1823	com/aelitis/azureus/core/content/RelatedContentManager:deserialiseDI	(Ljava/util/Map;Lcom/aelitis/azureus/core/content/RelatedContentManager$ContentCache;)Lcom/aelitis/azureus/core/content/RelatedContentManager$DownloadInfo;
/*      */     //   442: astore 16
/*      */     //   444: aload 16
/*      */     //   446: invokevirtual 1873	com/aelitis/azureus/core/content/RelatedContentManager$DownloadInfo:isUnread	()Z
/*      */     //   449: ifeq +6 -> 455
/*      */     //   452: iinc 4 1
/*      */     //   455: aload 6
/*      */     //   457: aload 15
/*      */     //   459: aload 16
/*      */     //   461: invokeinterface 2086 3 0
/*      */     //   466: pop
/*      */     //   467: aload 14
/*      */     //   469: ldc_w 931
/*      */     //   472: invokeinterface 2085 2 0
/*      */     //   477: checkcast 1033	java/lang/Long
/*      */     //   480: invokevirtual 1934	java/lang/Long:intValue	()I
/*      */     //   483: istore 17
/*      */     //   485: aload 11
/*      */     //   487: iload 17
/*      */     //   489: invokestatic 1931	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */     //   492: aload 16
/*      */     //   494: invokeinterface 2086 3 0
/*      */     //   499: pop
/*      */     //   500: goto +10 -> 510
/*      */     //   503: astore 15
/*      */     //   505: aload 15
/*      */     //   507: invokestatic 2030	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   510: goto -124 -> 386
/*      */     //   513: aload 8
/*      */     //   515: invokeinterface 2079 1 0
/*      */     //   520: ifeq +194 -> 714
/*      */     //   523: aload 11
/*      */     //   525: invokeinterface 2079 1 0
/*      */     //   530: ifeq +184 -> 714
/*      */     //   533: aload 8
/*      */     //   535: invokeinterface 2084 1 0
/*      */     //   540: invokeinterface 2092 1 0
/*      */     //   545: astore 12
/*      */     //   547: aload 12
/*      */     //   549: invokeinterface 2070 1 0
/*      */     //   554: ifeq +160 -> 714
/*      */     //   557: aload 12
/*      */     //   559: invokeinterface 2071 1 0
/*      */     //   564: checkcast 1036	java/lang/String
/*      */     //   567: astore 13
/*      */     //   569: aload 13
/*      */     //   571: invokestatic 2023	org/gudy/azureus2/core3/util/Base32:decode	(Ljava/lang/String;)[B
/*      */     //   574: astore 14
/*      */     //   576: aload 8
/*      */     //   578: aload 13
/*      */     //   580: invokestatic 1913	com/aelitis/azureus/util/ImportExportUtils:importIntArray	(Ljava/util/Map;Ljava/lang/String;)[I
/*      */     //   583: astore 15
/*      */     //   585: aload 15
/*      */     //   587: ifnull +114 -> 701
/*      */     //   590: aload 15
/*      */     //   592: arraylength
/*      */     //   593: ifne +6 -> 599
/*      */     //   596: goto +105 -> 701
/*      */     //   599: new 1042	java/util/ArrayList
/*      */     //   602: dup
/*      */     //   603: aload 15
/*      */     //   605: arraylength
/*      */     //   606: invokespecial 1970	java/util/ArrayList:<init>	(I)V
/*      */     //   609: astore 16
/*      */     //   611: aload 15
/*      */     //   613: astore 17
/*      */     //   615: aload 17
/*      */     //   617: arraylength
/*      */     //   618: istore 18
/*      */     //   620: iconst_0
/*      */     //   621: istore 19
/*      */     //   623: iload 19
/*      */     //   625: iload 18
/*      */     //   627: if_icmpge +56 -> 683
/*      */     //   630: aload 17
/*      */     //   632: iload 19
/*      */     //   634: iaload
/*      */     //   635: istore 20
/*      */     //   637: aload 11
/*      */     //   639: iload 20
/*      */     //   641: invokestatic 1931	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */     //   644: invokeinterface 2085 2 0
/*      */     //   649: checkcast 1006	com/aelitis/azureus/core/content/RelatedContentManager$DownloadInfo
/*      */     //   652: astore 21
/*      */     //   654: aload 21
/*      */     //   656: ifnonnull +6 -> 662
/*      */     //   659: goto +18 -> 677
/*      */     //   662: aload 21
/*      */     //   664: aload 14
/*      */     //   666: invokevirtual 1882	com/aelitis/azureus/core/content/RelatedContentManager$DownloadInfo:setRelatedToHash	([B)V
/*      */     //   669: aload 16
/*      */     //   671: aload 21
/*      */     //   673: invokevirtual 1971	java/util/ArrayList:add	(Ljava/lang/Object;)Z
/*      */     //   676: pop
/*      */     //   677: iinc 19 1
/*      */     //   680: goto -57 -> 623
/*      */     //   683: aload 16
/*      */     //   685: invokevirtual 1967	java/util/ArrayList:size	()I
/*      */     //   688: ifle +13 -> 701
/*      */     //   691: aload 7
/*      */     //   693: aload 14
/*      */     //   695: aload 16
/*      */     //   697: invokevirtual 1858	com/aelitis/azureus/core/content/RelatedContentManager$ByteArrayHashMapEx:put	([BLjava/lang/Object;)Ljava/lang/Object;
/*      */     //   700: pop
/*      */     //   701: goto +10 -> 711
/*      */     //   704: astore 14
/*      */     //   706: aload 14
/*      */     //   708: invokestatic 2030	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   711: goto -164 -> 547
/*      */     //   714: aload 6
/*      */     //   716: invokeinterface 2082 1 0
/*      */     //   721: invokeinterface 2067 1 0
/*      */     //   726: astore 12
/*      */     //   728: aload 12
/*      */     //   730: invokeinterface 2070 1 0
/*      */     //   735: ifeq +44 -> 779
/*      */     //   738: aload 12
/*      */     //   740: invokeinterface 2071 1 0
/*      */     //   745: checkcast 1006	com/aelitis/azureus/core/content/RelatedContentManager$DownloadInfo
/*      */     //   748: astore 13
/*      */     //   750: aload 13
/*      */     //   752: invokevirtual 1875	com/aelitis/azureus/core/content/RelatedContentManager$DownloadInfo:getRelatedToHash	()[B
/*      */     //   755: ifnonnull +21 -> 776
/*      */     //   758: aload 13
/*      */     //   760: invokevirtual 1873	com/aelitis/azureus/core/content/RelatedContentManager$DownloadInfo:isUnread	()Z
/*      */     //   763: ifeq +6 -> 769
/*      */     //   766: iinc 4 -1
/*      */     //   769: aload 12
/*      */     //   771: invokeinterface 2069 1 0
/*      */     //   776: goto -48 -> 728
/*      */     //   779: aload_0
/*      */     //   780: aload_3
/*      */     //   781: invokevirtual 1800	com/aelitis/azureus/core/content/RelatedContentManager:popuplateSecondaryLookups	(Lcom/aelitis/azureus/core/content/RelatedContentManager$ContentCache;)V
/*      */     //   784: aload_0
/*      */     //   785: getfield 1762	com/aelitis/azureus/core/content/RelatedContentManager:total_unread	Ljava/util/concurrent/atomic/AtomicInteger;
/*      */     //   788: invokevirtual 1992	java/util/concurrent/atomic/AtomicInteger:get	()I
/*      */     //   791: iload 4
/*      */     //   793: if_icmpeq +20 -> 813
/*      */     //   796: aload_0
/*      */     //   797: getfield 1762	com/aelitis/azureus/core/content/RelatedContentManager:total_unread	Ljava/util/concurrent/atomic/AtomicInteger;
/*      */     //   800: iload 4
/*      */     //   802: invokevirtual 1997	java/util/concurrent/atomic/AtomicInteger:set	(I)V
/*      */     //   805: ldc 25
/*      */     //   807: iload 4
/*      */     //   809: invokestatic 2004	org/gudy/azureus2/core3/config/COConfigurationManager:setParameter	(Ljava/lang/String;I)Z
/*      */     //   812: pop
/*      */     //   813: goto +10 -> 823
/*      */     //   816: astore 4
/*      */     //   818: aload 4
/*      */     //   820: invokestatic 2030	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   823: aload_0
/*      */     //   824: aload_3
/*      */     //   825: iconst_0
/*      */     //   826: invokevirtual 1801	com/aelitis/azureus/core/content/RelatedContentManager:enforceMaxResults	(Lcom/aelitis/azureus/core/content/RelatedContentManager$ContentCache;Z)V
/*      */     //   829: aload_0
/*      */     //   830: aload_3
/*      */     //   831: putfield 1740	com/aelitis/azureus/core/content/RelatedContentManager:content_cache_ref	Lcom/aelitis/azureus/core/content/RelatedContentManager$ContentCache;
/*      */     //   834: aload_3
/*      */     //   835: astore 4
/*      */     //   837: aload_2
/*      */     //   838: monitorexit
/*      */     //   839: iload_1
/*      */     //   840: ifeq +8 -> 848
/*      */     //   843: aload_0
/*      */     //   844: iconst_0
/*      */     //   845: invokevirtual 1789	com/aelitis/azureus/core/content/RelatedContentManager:contentChanged	(Z)V
/*      */     //   848: aload 4
/*      */     //   850: areturn
/*      */     //   851: astore 22
/*      */     //   853: aload_2
/*      */     //   854: monitorexit
/*      */     //   855: aload 22
/*      */     //   857: athrow
/*      */     //   858: astore 23
/*      */     //   860: iload_1
/*      */     //   861: ifeq +8 -> 869
/*      */     //   864: aload_0
/*      */     //   865: iconst_0
/*      */     //   866: invokevirtual 1789	com/aelitis/azureus/core/content/RelatedContentManager:contentChanged	(Z)V
/*      */     //   869: aload 23
/*      */     //   871: athrow
/*      */     // Line number table:
/*      */     //   Java source line #3419	-> byte code offset #0
/*      */     //   Java source line #3422	-> byte code offset #2
/*      */     //   Java source line #3424	-> byte code offset #9
/*      */     //   Java source line #3426	-> byte code offset #16
/*      */     //   Java source line #3428	-> byte code offset #38
/*      */     //   Java source line #3434	-> byte code offset #42
/*      */     //   Java source line #3436	-> byte code offset #44
/*      */     //   Java source line #3438	-> byte code offset #52
/*      */     //   Java source line #3441	-> byte code offset #64
/*      */     //   Java source line #3443	-> byte code offset #67
/*      */     //   Java source line #3445	-> byte code offset #76
/*      */     //   Java source line #3447	-> byte code offset #84
/*      */     //   Java source line #3448	-> byte code offset #90
/*      */     //   Java source line #3452	-> byte code offset #96
/*      */     //   Java source line #3454	-> byte code offset #113
/*      */     //   Java source line #3457	-> byte code offset #118
/*      */     //   Java source line #3464	-> byte code offset #154
/*      */     //   Java source line #3459	-> byte code offset #157
/*      */     //   Java source line #3463	-> byte code offset #159
/*      */     //   Java source line #3467	-> byte code offset #168
/*      */     //   Java source line #3469	-> byte code offset #183
/*      */     //   Java source line #3471	-> byte code offset #195
/*      */     //   Java source line #3473	-> byte code offset #205
/*      */     //   Java source line #3475	-> byte code offset #214
/*      */     //   Java source line #3479	-> byte code offset #222
/*      */     //   Java source line #3481	-> byte code offset #229
/*      */     //   Java source line #3485	-> byte code offset #265
/*      */     //   Java source line #3487	-> byte code offset #277
/*      */     //   Java source line #3489	-> byte code offset #289
/*      */     //   Java source line #3491	-> byte code offset #298
/*      */     //   Java source line #3493	-> byte code offset #306
/*      */     //   Java source line #3496	-> byte code offset #309
/*      */     //   Java source line #3498	-> byte code offset #321
/*      */     //   Java source line #3500	-> byte code offset #339
/*      */     //   Java source line #3505	-> byte code offset #354
/*      */     //   Java source line #3502	-> byte code offset #357
/*      */     //   Java source line #3504	-> byte code offset #359
/*      */     //   Java source line #3505	-> byte code offset #364
/*      */     //   Java source line #3507	-> byte code offset #367
/*      */     //   Java source line #3509	-> byte code offset #370
/*      */     //   Java source line #3511	-> byte code offset #377
/*      */     //   Java source line #3515	-> byte code offset #408
/*      */     //   Java source line #3517	-> byte code offset #435
/*      */     //   Java source line #3519	-> byte code offset #444
/*      */     //   Java source line #3521	-> byte code offset #452
/*      */     //   Java source line #3524	-> byte code offset #455
/*      */     //   Java source line #3526	-> byte code offset #467
/*      */     //   Java source line #3528	-> byte code offset #485
/*      */     //   Java source line #3533	-> byte code offset #500
/*      */     //   Java source line #3530	-> byte code offset #503
/*      */     //   Java source line #3532	-> byte code offset #505
/*      */     //   Java source line #3533	-> byte code offset #510
/*      */     //   Java source line #3537	-> byte code offset #513
/*      */     //   Java source line #3539	-> byte code offset #533
/*      */     //   Java source line #3542	-> byte code offset #569
/*      */     //   Java source line #3544	-> byte code offset #576
/*      */     //   Java source line #3546	-> byte code offset #585
/*      */     //   Java source line #3552	-> byte code offset #599
/*      */     //   Java source line #3554	-> byte code offset #611
/*      */     //   Java source line #3556	-> byte code offset #637
/*      */     //   Java source line #3558	-> byte code offset #654
/*      */     //   Java source line #3566	-> byte code offset #662
/*      */     //   Java source line #3568	-> byte code offset #669
/*      */     //   Java source line #3554	-> byte code offset #677
/*      */     //   Java source line #3572	-> byte code offset #683
/*      */     //   Java source line #3574	-> byte code offset #691
/*      */     //   Java source line #3580	-> byte code offset #701
/*      */     //   Java source line #3577	-> byte code offset #704
/*      */     //   Java source line #3579	-> byte code offset #706
/*      */     //   Java source line #3580	-> byte code offset #711
/*      */     //   Java source line #3584	-> byte code offset #714
/*      */     //   Java source line #3586	-> byte code offset #728
/*      */     //   Java source line #3588	-> byte code offset #738
/*      */     //   Java source line #3590	-> byte code offset #750
/*      */     //   Java source line #3594	-> byte code offset #758
/*      */     //   Java source line #3596	-> byte code offset #766
/*      */     //   Java source line #3599	-> byte code offset #769
/*      */     //   Java source line #3601	-> byte code offset #776
/*      */     //   Java source line #3603	-> byte code offset #779
/*      */     //   Java source line #3607	-> byte code offset #784
/*      */     //   Java source line #3611	-> byte code offset #796
/*      */     //   Java source line #3613	-> byte code offset #805
/*      */     //   Java source line #3618	-> byte code offset #813
/*      */     //   Java source line #3615	-> byte code offset #816
/*      */     //   Java source line #3617	-> byte code offset #818
/*      */     //   Java source line #3620	-> byte code offset #823
/*      */     //   Java source line #3623	-> byte code offset #829
/*      */     //   Java source line #3625	-> byte code offset #834
/*      */     //   Java source line #3629	-> byte code offset #839
/*      */     //   Java source line #3631	-> byte code offset #843
/*      */     //   Java source line #3626	-> byte code offset #851
/*      */     //   Java source line #3629	-> byte code offset #858
/*      */     //   Java source line #3631	-> byte code offset #864
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	872	0	this	RelatedContentManager
/*      */     //   1	860	1	fire_event	boolean
/*      */     //   37	798	3	cc	ContentCache
/*      */     //   65	743	4	new_total_unread	int
/*      */     //   816	33	4	e	Throwable
/*      */     //   82	102	5	map	Map
/*      */     //   88	627	6	related_content	Map<String, DownloadInfo>
/*      */     //   94	598	7	related_content_map	ByteArrayHashMapEx<ArrayList<DownloadInfo>>
/*      */     //   181	396	8	rcm_map	Map<String, String>
/*      */     //   111	23	9	data	byte[]
/*      */     //   157	3	10	e	Throwable
/*      */     //   193	178	10	rc_map_stuff	Object
/*      */     //   212	426	11	id_map	Map<Integer, DownloadInfo>
/*      */     //   227	3	12	rc_map	Map<String, Map<String, Object>>
/*      */     //   375	3	12	rc_map_list	List<Map<String, Object>>
/*      */     //   545	13	12	i$	Iterator
/*      */     //   726	44	12	it	Iterator<DownloadInfo>
/*      */     //   241	13	13	i$	Iterator
/*      */     //   384	13	13	i$	Iterator
/*      */     //   567	12	13	key	String
/*      */     //   748	11	13	di	DownloadInfo
/*      */     //   263	15	14	entry	Map.Entry<String, Map<String, Object>>
/*      */     //   406	62	14	info_map	Map<String, Object>
/*      */     //   574	120	14	hash	byte[]
/*      */     //   704	3	14	e	Throwable
/*      */     //   275	37	15	key	String
/*      */     //   357	3	15	e	Throwable
/*      */     //   433	25	15	key	String
/*      */     //   503	3	15	e	Throwable
/*      */     //   583	29	15	ids	int[]
/*      */     //   287	35	16	info_map	Map<String, Object>
/*      */     //   442	51	16	info	DownloadInfo
/*      */     //   609	87	16	di_list	ArrayList<DownloadInfo>
/*      */     //   296	51	17	info	DownloadInfo
/*      */     //   483	5	17	id	int
/*      */     //   613	18	17	arr$	int[]
/*      */     //   337	5	18	id	int
/*      */     //   618	8	18	len$	int
/*      */     //   621	57	19	i$	int
/*      */     //   635	5	20	id	int
/*      */     //   652	20	21	di	DownloadInfo
/*      */     //   851	5	22	localObject1	Object
/*      */     //   858	12	23	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   118	154	157	java/lang/Throwable
/*      */     //   265	354	357	java/lang/Throwable
/*      */     //   408	500	503	java/lang/Throwable
/*      */     //   569	701	704	java/lang/Throwable
/*      */     //   64	813	816	java/lang/Throwable
/*      */     //   9	839	851	finally
/*      */     //   851	855	851	finally
/*      */     //   2	839	858	finally
/*      */     //   851	860	858	finally
/*      */   }
/*      */   
/*      */   protected class DownloadInfo
/*      */     extends RelatedContent
/*      */   {
/*      */     private final int rand;
/* 4831 */     private boolean unread = true;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private int[] rand_list;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private int last_seen;
/*      */     
/*      */ 
/*      */ 
/*      */     private int level;
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean explicit;
/*      */     
/*      */ 
/*      */ 
/*      */     private RelatedContentManager.ContentCache cc;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected DownloadInfo(int _version, byte[] _related_to, byte[] _hash, String _title, int _rand, String _tracker, byte[] _tracker_keys, byte[] _ws_keys, String[] _tags, byte _nets, int _level, boolean _explicit, long _size, int _date, int _seeds_leechers, byte _cnet)
/*      */     {
/* 4860 */       super(_related_to, _title, _hash, _tracker, _tracker_keys, _ws_keys, _tags, _nets, _size, _date, _seeds_leechers, _cnet);
/*      */       
/* 4862 */       this.rand = _rand;
/* 4863 */       this.level = _level;
/* 4864 */       this.explicit = _explicit;
/*      */       
/* 4866 */       updateLastSeen();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected DownloadInfo(int _version, byte[] _hash, String _title, int _rand, String _tracker, byte[] _tracker_keys, byte[] _ws_keys, String[] _tags, byte _nets, boolean _unread, int[] _rand_list, int _last_seen, int _level, long _size, int _date, int _seeds_leechers, byte _cnet, RelatedContentManager.ContentCache _cc)
/*      */     {
/* 4890 */       super(_title, _hash, _tracker, _tracker_keys, _ws_keys, _tags, _nets, _size, _date, _seeds_leechers, _cnet);
/*      */       
/* 4892 */       this.rand = _rand;
/* 4893 */       this.unread = _unread;
/* 4894 */       this.rand_list = _rand_list;
/* 4895 */       this.last_seen = _last_seen;
/* 4896 */       this.level = _level;
/* 4897 */       this.cc = _cc;
/*      */       int[] temp;
/* 4899 */       if (this.rand_list != null)
/*      */       {
/* 4901 */         if (this.rand_list.length > 100)
/*      */         {
/* 4903 */           temp = new int[100];
/*      */           
/* 4905 */           System.arraycopy(this.rand_list, 0, temp, 0, 100);
/*      */           
/* 4907 */           this.rand_list = temp;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean addInfo(DownloadInfo info)
/*      */     {
/* 4916 */       boolean result = false;
/*      */       
/* 4918 */       synchronized (this)
/*      */       {
/* 4920 */         updateLastSeen();
/*      */         
/* 4922 */         int r = info.getRand();
/*      */         
/* 4924 */         if (this.rand_list == null)
/*      */         {
/* 4926 */           this.rand_list = new int[] { r };
/*      */           
/* 4928 */           result = true;
/*      */         }
/*      */         else
/*      */         {
/* 4932 */           boolean match = false;
/*      */           
/* 4934 */           for (int i = 0; i < this.rand_list.length; i++)
/*      */           {
/* 4936 */             if (this.rand_list[i] == r)
/*      */             {
/* 4938 */               match = true;
/*      */               
/* 4940 */               break;
/*      */             }
/*      */           }
/*      */           
/* 4944 */           if ((!match) && (this.rand_list.length < 100))
/*      */           {
/* 4946 */             int len = this.rand_list.length;
/*      */             
/* 4948 */             int[] new_rand_list = new int[len + 1];
/*      */             
/* 4950 */             System.arraycopy(this.rand_list, 0, new_rand_list, 0, len);
/*      */             
/* 4952 */             new_rand_list[len] = r;
/*      */             
/* 4954 */             this.rand_list = new_rand_list;
/*      */             
/* 4956 */             result = true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 4961 */       if (info.getVersion() > getVersion())
/*      */       {
/* 4963 */         setVersion(info.getVersion());
/*      */         
/* 4965 */         result = true;
/*      */       }
/*      */       
/* 4968 */       if (info.getLevel() < this.level)
/*      */       {
/* 4970 */         this.level = info.getLevel();
/*      */         
/* 4972 */         result = true;
/*      */       }
/*      */       
/* 4975 */       long cn = info.getContentNetwork();
/*      */       
/* 4977 */       if ((cn != -1L) && (getContentNetwork() == -1L))
/*      */       {
/*      */ 
/* 4980 */         setContentNetwork(cn);
/*      */       }
/*      */       
/* 4983 */       if (info.getVersion() >= getVersion())
/*      */       {
/*      */ 
/*      */ 
/* 4987 */         int sl = info.getSeedsLeechers();
/*      */         
/* 4989 */         if ((sl != -1) && (sl != getSeedsLeechers()))
/*      */         {
/* 4991 */           setSeedsLeechers(sl);
/*      */           
/* 4993 */           result = true;
/*      */         }
/*      */       }
/*      */       
/* 4997 */       int d = info.getDateHours();
/*      */       
/* 4999 */       if ((d > 0) && (getDateHours() == 0))
/*      */       {
/* 5001 */         setDateHours(d);
/*      */         
/* 5003 */         result = true;
/*      */       }
/*      */       
/* 5006 */       String[] other_tags = info.getTags();
/*      */       
/* 5008 */       if ((other_tags != null) && (other_tags.length > 0))
/*      */       {
/* 5010 */         String[] existing_tags = getTags();
/*      */         
/* 5012 */         if (existing_tags == NO_TAGS)
/*      */         {
/* 5014 */           setTags(other_tags);
/*      */           
/* 5016 */           result = true;
/*      */         }
/*      */         else
/*      */         {
/*      */           boolean same;
/*      */           
/* 5022 */           if (other_tags.length == existing_tags.length) {
/*      */             boolean same;
/* 5024 */             if (existing_tags.length == 1)
/*      */             {
/* 5026 */               same = other_tags[0].equals(existing_tags[0]);
/*      */             }
/*      */             else
/*      */             {
/* 5030 */               boolean same = true;
/*      */               
/* 5032 */               for (int i = 0; i < existing_tags.length; i++)
/*      */               {
/* 5034 */                 String e_tag = existing_tags[i];
/*      */                 
/* 5036 */                 boolean found = false;
/*      */                 
/* 5038 */                 for (int j = 0; j < other_tags.length; j++)
/*      */                 {
/* 5040 */                   if (e_tag.equals(other_tags[j]))
/*      */                   {
/* 5042 */                     found = true;
/*      */                     
/* 5044 */                     break;
/*      */                   }
/*      */                 }
/*      */                 
/* 5048 */                 if (!found)
/*      */                 {
/* 5050 */                   same = false;
/*      */                   
/* 5052 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           else {
/* 5058 */             same = false;
/*      */           }
/*      */           
/* 5061 */           if (!same)
/*      */           {
/* 5063 */             Set<String> tags = new HashSet();
/*      */             
/* 5065 */             Collections.addAll(tags, existing_tags);
/* 5066 */             Collections.addAll(tags, other_tags);
/*      */             
/* 5068 */             setTags((String[])tags.toArray(new String[tags.size()]));
/*      */             
/* 5070 */             result = true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 5075 */       byte other_nets = info.getNetworksInternal();
/* 5076 */       byte existing_nets = getNetworksInternal();
/*      */       
/* 5078 */       if (other_nets != existing_nets)
/*      */       {
/* 5080 */         setNetworksInternal((byte)(other_nets | existing_nets));
/*      */         
/* 5082 */         result = true;
/*      */       }
/*      */       
/* 5085 */       if (result) {
/* 5086 */         setChangedLocallyOn(0L);
/*      */       }
/* 5088 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getLevel()
/*      */     {
/* 5094 */       return this.level;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isExplicit()
/*      */     {
/* 5100 */       return this.explicit;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setExplicit(boolean b)
/*      */     {
/* 5107 */       this.explicit = b;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void updateLastSeen()
/*      */     {
/* 5116 */       this.last_seen = ((int)(SystemTime.getCurrentTime() / 1000L));
/*      */     }
/*      */     
/*      */ 
/*      */     public int getRank()
/*      */     {
/* 5122 */       return this.rand_list == null ? 0 : this.rand_list.length;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isUnread()
/*      */     {
/* 5128 */       return this.unread;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setPublic(RelatedContentManager.ContentCache _cc)
/*      */     {
/* 5135 */       this.cc = _cc;
/*      */       
/* 5137 */       if (this.unread)
/*      */       {
/* 5139 */         RelatedContentManager.this.incrementUnread();
/*      */       }
/*      */       
/* 5142 */       this.rand_list = new int[] { this.rand };
/* 5143 */       setChangedLocallyOn(0L);
/*      */     }
/*      */     
/*      */ 
/*      */     public int getLastSeenSecs()
/*      */     {
/* 5149 */       return this.last_seen;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setUnreadInternal(boolean _unread)
/*      */     {
/* 5156 */       synchronized (this)
/*      */       {
/* 5158 */         this.unread = _unread;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setUnread(boolean _unread)
/*      */     {
/* 5166 */       boolean changed = false;
/*      */       
/* 5168 */       synchronized (this)
/*      */       {
/* 5170 */         if (this.unread != _unread)
/*      */         {
/* 5172 */           this.unread = _unread;
/*      */           
/* 5174 */           changed = true;
/*      */         }
/*      */       }
/*      */       
/* 5178 */       if (changed)
/*      */       {
/* 5180 */         if (_unread)
/*      */         {
/* 5182 */           RelatedContentManager.this.incrementUnread();
/*      */         }
/*      */         else
/*      */         {
/* 5186 */           RelatedContentManager.this.decrementUnread();
/*      */         }
/*      */         
/* 5189 */         setChangedLocallyOn(0L);
/* 5190 */         RelatedContentManager.this.contentChanged(this);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getRand()
/*      */     {
/* 5197 */       return this.rand;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int[] getRandList()
/*      */     {
/* 5203 */       return this.rand_list;
/*      */     }
/*      */     
/*      */     public Download getRelatedToDownload()
/*      */     {
/*      */       try
/*      */       {
/* 5210 */         return RelatedContentManager.this.getDownload(getRelatedToHash());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 5214 */         Debug.out(e);
/*      */       }
/* 5216 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void delete()
/*      */     {
/* 5223 */       setChangedLocallyOn(0L);
/* 5224 */       RelatedContentManager.this.delete(new RelatedContent[] { this });
/*      */     }
/*      */     
/*      */ 
/*      */     public String getString()
/*      */     {
/* 5230 */       return super.getString() + ", " + this.rand + ", rl=" + this.rand_list + ", last_seen=" + this.last_seen + ", level=" + this.level;
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
/*      */   protected static class ContentCache
/*      */   {
/* 5246 */     protected Map<String, RelatedContentManager.DownloadInfo> related_content = new HashMap();
/* 5247 */     protected RelatedContentManager.ByteArrayHashMapEx<ArrayList<RelatedContentManager.DownloadInfo>> related_content_map = new RelatedContentManager.ByteArrayHashMapEx();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class SecondaryLookup
/*      */   {
/*      */     private final byte[] hash;
/*      */     
/*      */     private final int level;
/*      */     
/*      */     private final byte nets;
/*      */     
/*      */ 
/*      */     private SecondaryLookup(byte[] _hash, int _level, byte _nets)
/*      */     {
/* 5263 */       this.hash = _hash;
/* 5264 */       this.level = _level;
/* 5265 */       this.nets = _nets;
/*      */     }
/*      */     
/*      */ 
/*      */     private byte[] getHash()
/*      */     {
/* 5271 */       return this.hash;
/*      */     }
/*      */     
/*      */ 
/*      */     private int getLevel()
/*      */     {
/* 5277 */       return this.level;
/*      */     }
/*      */     
/*      */ 
/*      */     private byte getNetworks()
/*      */     {
/* 5283 */       return this.nets;
/*      */     }
/*      */   }
/*      */   
/*      */   protected static class RCMSearchXFer
/*      */     implements org.gudy.azureus2.plugins.ddb.DistributedDatabaseTransferType
/*      */   {}
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/RelatedContentManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */