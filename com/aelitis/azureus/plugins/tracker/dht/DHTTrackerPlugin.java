/*      */ package com.aelitis.azureus.plugins.tracker.dht;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*      */ import com.aelitis.azureus.core.tracker.TrackerPeerSourceAdapter;
/*      */ import com.aelitis.azureus.plugins.I2PHelpers;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginOperationListener;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginValue;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URL;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.WeakHashMap;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.PRHelpers;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.Plugin;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginListener;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
/*      */ import org.gudy.azureus2.plugins.logging.Logger;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
/*      */ import org.gudy.azureus2.plugins.peers.Peer;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManagerStats;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextField;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.IntParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*      */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DHTTrackerPlugin
/*      */   implements Plugin, DownloadListener, DownloadAttributeListener, DownloadTrackerListener
/*      */ {
/*   88 */   public static Object DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY = new Object();
/*      */   
/*      */   private static final String PLUGIN_NAME = "Distributed Tracker";
/*      */   
/*      */   private static final String PLUGIN_CONFIGSECTION_ID = "plugins.dhttracker";
/*      */   
/*      */   private static final String PLUGIN_RESOURCE_ID = "ConfigView.section.plugins.dhttracker";
/*      */   
/*      */   private static final int ANNOUNCE_TIMEOUT = 120000;
/*      */   
/*      */   private static final int ANNOUNCE_DERIVED_TIMEOUT = 60000;
/*      */   
/*      */   private static final int SCRAPE_TIMEOUT = 30000;
/*      */   
/*      */   private static final int ANNOUNCE_MIN_DEFAULT = 120000;
/*      */   
/*      */   private static final int ANNOUNCE_MAX = 3600000;
/*      */   
/*      */   private static final int ANNOUNCE_MAX_DERIVED_ONLY = 1800000;
/*      */   
/*      */   private static final int INTERESTING_CHECK_PERIOD = 14400000;
/*      */   
/*      */   private static final int INTERESTING_INIT_RAND_OURS = 300000;
/*      */   
/*      */   private static final int INTERESTING_INIT_MIN_OURS = 120000;
/*      */   
/*      */   private static final int INTERESTING_INIT_RAND_OTHERS = 1800000;
/*      */   private static final int INTERESTING_INIT_MIN_OTHERS = 300000;
/*      */   private static final int INTERESTING_DHT_CHECK_PERIOD = 3600000;
/*      */   private static final int INTERESTING_DHT_INIT_RAND = 300000;
/*      */   private static final int INTERESTING_DHT_INIT_MIN = 120000;
/*      */   private static final int INTERESTING_AVAIL_MAX = 8;
/*      */   private static final int INTERESTING_PUB_MAX_DEFAULT = 30;
/*      */   private static final int REG_TYPE_NONE = 1;
/*      */   private static final int REG_TYPE_FULL = 2;
/*      */   private static final int REG_TYPE_DERIVED = 3;
/*      */   private static final int LIMITED_TRACK_SIZE = 16;
/*      */   private static final boolean TRACK_NORMAL_DEFAULT = true;
/*      */   private static final boolean TRACK_LIMITED_DEFAULT = true;
/*      */   private static final boolean TEST_ALWAYS_TRACK = false;
/*      */   public static final int NUM_WANT = 30;
/*  129 */   private static final long start_time = SystemTime.getCurrentTime();
/*      */   
/*  131 */   private static final Object DL_DERIVED_METRIC_KEY = new Object();
/*      */   private static final int DL_DERIVED_MIN_TRACK = 5;
/*      */   private static final int DL_DERIVED_MAX_TRACK = 20;
/*      */   private static final int DIRECT_INJECT_PEER_MAX = 5;
/*      */   private static URL DEFAULT_URL;
/*      */   private PluginInterface plugin_interface;
/*      */   private BasicPluginViewModel model;
/*      */   private DHTPlugin dht;
/*      */   
/*      */   static
/*      */   {
/*      */     try {
/*  143 */       DEFAULT_URL = new URL("dht:");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  147 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTTrackerPlugin()
/*      */   {
/*  158 */     this.interesting_downloads = new HashMap();
/*  159 */     this.interesting_published = 0;
/*  160 */     this.interesting_pub_max = 30;
/*  161 */     this.running_downloads = new HashMap();
/*  162 */     this.run_data_cache = new HashMap();
/*  163 */     this.registered_downloads = new HashMap();
/*      */     
/*  165 */     this.limited_online_tracking = new HashMap();
/*  166 */     this.query_map = new HashMap();
/*      */     
/*  168 */     this.in_progress = new HashMap();
/*      */     
/*      */ 
/*      */ 
/*  172 */     this.track_only_decentralsed = COConfigurationManager.getBooleanParameter("dhtplugin.track.only.decentralised", false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  177 */     this.current_announce_interval = 120000L;
/*      */     
/*      */ 
/*      */ 
/*  181 */     this.scrape_injection_map = new WeakHashMap();
/*      */     
/*  183 */     this.random = new Random();
/*      */     
/*      */ 
/*  186 */     this.this_mon = new AEMonitor("DHTTrackerPlugin");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  191 */     this.initialised_sem = new AESemaphore("DHTTrackerPlugin:init");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  198 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Enable.Proxy", "Enable.SOCKS" }, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameter_name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  209 */         boolean enable_proxy = COConfigurationManager.getBooleanParameter("Enable.Proxy");
/*  210 */         boolean enable_socks = COConfigurationManager.getBooleanParameter("Enable.SOCKS");
/*      */         
/*  212 */         DHTTrackerPlugin.this.disable_put = ((enable_proxy) && (enable_socks));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void load(PluginInterface plugin_interface)
/*      */   {
/*  221 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  222 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "Distributed Tracker");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void initialize(PluginInterface _plugin_interface)
/*      */   {
/*  229 */     this.plugin_interface = _plugin_interface;
/*      */     
/*  231 */     this.log = this.plugin_interface.getLogger().getTimeStampedChannel("Distributed Tracker");
/*      */     
/*  233 */     this.ta_networks = this.plugin_interface.getTorrentManager().getAttribute("Networks");
/*  234 */     this.ta_peer_sources = this.plugin_interface.getTorrentManager().getAttribute("PeerSources");
/*      */     
/*  236 */     UIManager ui_manager = this.plugin_interface.getUIManager();
/*      */     
/*  238 */     this.model = ui_manager.createBasicPluginViewModel("ConfigView.section.plugins.dhttracker");
/*      */     
/*      */ 
/*  241 */     this.model.setConfigSectionID("plugins.dhttracker");
/*      */     
/*  243 */     BasicPluginConfigModel config = ui_manager.createBasicPluginConfigModel("plugins", "plugins.dhttracker");
/*      */     
/*      */ 
/*      */ 
/*  247 */     this.track_normal_when_offline = config.addBooleanParameter2("dhttracker.tracknormalwhenoffline", "dhttracker.tracknormalwhenoffline", true);
/*      */     
/*  249 */     this.track_limited_when_online = config.addBooleanParameter2("dhttracker.tracklimitedwhenonline", "dhttracker.tracklimitedwhenonline", true);
/*      */     
/*  251 */     this.track_limited_when_online.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  258 */         DHTTrackerPlugin.this.configChanged();
/*      */       }
/*      */       
/*  261 */     });
/*  262 */     this.track_normal_when_offline.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  269 */         DHTTrackerPlugin.this.track_limited_when_online.setEnabled(DHTTrackerPlugin.this.track_normal_when_offline.getValue());
/*      */         
/*  271 */         DHTTrackerPlugin.this.configChanged();
/*      */       }
/*      */     });
/*      */     
/*  275 */     if (!this.track_normal_when_offline.getValue())
/*      */     {
/*  277 */       this.track_limited_when_online.setEnabled(false);
/*      */     }
/*      */     
/*  280 */     this.interesting_pub_max = this.plugin_interface.getPluginconfig().getPluginIntParameter("dhttracker.presencepubmax", 30);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  288 */     BooleanParameter enable_alt = config.addBooleanParameter2("dhttracker.enable_alt", "dhttracker.enable_alt", true);
/*      */     
/*  290 */     IntParameter alt_port = config.addIntParameter2("dhttracker.alt_port", "dhttracker.alt_port", 0, 0, 65535);
/*      */     
/*  292 */     enable_alt.addEnabledOnSelection(alt_port);
/*      */     
/*  294 */     config.createGroup("dhttracker.alt_group", new Parameter[] { enable_alt, alt_port });
/*      */     
/*  296 */     if (enable_alt.getValue())
/*      */     {
/*  298 */       this.alt_lookup_handler = new DHTTrackerPluginAlt(alt_port.getValue());
/*      */     }
/*      */     
/*  301 */     this.model.getActivity().setVisible(false);
/*  302 */     this.model.getProgress().setVisible(false);
/*      */     
/*  304 */     this.model.getLogArea().setMaximumSize(80000);
/*      */     
/*  306 */     this.log.addListener(new LoggerChannelListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void messageLogged(int type, String message)
/*      */       {
/*      */ 
/*      */ 
/*  314 */         DHTTrackerPlugin.this.model.getLogArea().appendText(message + "\n");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageLogged(String str, Throwable error)
/*      */       {
/*  322 */         DHTTrackerPlugin.this.model.getLogArea().appendText(error.toString() + "\n");
/*      */       }
/*      */       
/*  325 */     });
/*  326 */     this.model.getStatus().setText(MessageText.getString("ManagerItem.initializing"));
/*      */     
/*  328 */     this.log.log("Waiting for Distributed Database initialisation");
/*      */     
/*  330 */     this.plugin_interface.addListener(new PluginListener()
/*      */     {
/*      */ 
/*      */       public void initializationComplete()
/*      */       {
/*      */ 
/*  336 */         boolean release_now = true;
/*      */         try
/*      */         {
/*  339 */           PluginInterface dht_pi = DHTTrackerPlugin.this.plugin_interface.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*      */           
/*      */ 
/*      */ 
/*  343 */           if (dht_pi != null)
/*      */           {
/*  345 */             DHTTrackerPlugin.this.dht = ((DHTPlugin)dht_pi.getPlugin());
/*      */             
/*  347 */             DelayedTask dt = DHTTrackerPlugin.this.plugin_interface.getUtilities().createDelayedTask(new Runnable()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void run()
/*      */               {
/*      */ 
/*      */ 
/*  355 */                 AEThread2 t = new AEThread2("DHTTrackerPlugin:init", true)
/*      */                 {
/*      */ 
/*      */                   public void run()
/*      */                   {
/*      */ 
/*      */                     try
/*      */                     {
/*  363 */                       if (DHTTrackerPlugin.this.dht.isEnabled())
/*      */                       {
/*  365 */                         DHTTrackerPlugin.this.log.log("DDB Available");
/*      */                         
/*  367 */                         DHTTrackerPlugin.this.model.getStatus().setText(MessageText.getString("DHTView.activity.status.false"));
/*      */                         
/*  369 */                         DHTTrackerPlugin.this.initialise();
/*      */                       }
/*      */                       else
/*      */                       {
/*  373 */                         DHTTrackerPlugin.this.log.log("DDB Disabled");
/*      */                         
/*  375 */                         DHTTrackerPlugin.this.model.getStatus().setText(MessageText.getString("dht.status.disabled"));
/*      */                         
/*  377 */                         DHTTrackerPlugin.this.notRunning();
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e) {
/*  381 */                       DHTTrackerPlugin.this.log.log("DDB Failed", e);
/*      */                       
/*  383 */                       DHTTrackerPlugin.this.model.getStatus().setText(MessageText.getString("DHTView.operations.failed"));
/*      */                       
/*  385 */                       DHTTrackerPlugin.this.notRunning();
/*      */                     }
/*      */                     finally
/*      */                     {
/*  389 */                       DHTTrackerPlugin.this.initialised_sem.releaseForever();
/*      */                     }
/*      */                     
/*      */                   }
/*  393 */                 };
/*  394 */                 t.start();
/*      */               }
/*      */               
/*  397 */             });
/*  398 */             dt.queue();
/*      */             
/*  400 */             release_now = false;
/*      */           }
/*      */           else
/*      */           {
/*  404 */             DHTTrackerPlugin.this.log.log("DDB Plugin missing");
/*      */             
/*  406 */             DHTTrackerPlugin.this.model.getStatus().setText(MessageText.getString("DHTView.operations.failed"));
/*      */             
/*  408 */             DHTTrackerPlugin.this.notRunning();
/*      */           }
/*      */         }
/*      */         finally {
/*  412 */           if (release_now)
/*      */           {
/*  414 */             DHTTrackerPlugin.this.initialised_sem.releaseForever();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void closedownInitiated() {}
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
/*      */   protected void notRunning()
/*      */   {
/*  436 */     this.plugin_interface.getDownloadManager().addListener(new DownloadManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void downloadAdded(Download download)
/*      */       {
/*      */ 
/*  443 */         DHTTrackerPlugin.this.addDownload(download);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void downloadRemoved(Download download)
/*      */       {
/*  450 */         DHTTrackerPlugin.this.removeDownload(download);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initialise()
/*      */   {
/*  458 */     this.is_running = true;
/*      */     
/*  460 */     this.plugin_interface.getDownloadManager().addListener(new DownloadManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void downloadAdded(Download download)
/*      */       {
/*      */ 
/*  467 */         DHTTrackerPlugin.this.addDownload(download);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void downloadRemoved(Download download)
/*      */       {
/*  474 */         DHTTrackerPlugin.this.removeDownload(download);
/*      */       }
/*      */       
/*  477 */     });
/*  478 */     this.plugin_interface.getUtilities().createTimer("DHT Tracker", true).addPeriodicEvent(15000L, new UTTimerEventPerformer()
/*      */     {
/*      */       private int ticks;
/*      */       
/*      */ 
/*      */ 
/*  484 */       private String prev_alt_status = "";
/*      */       
/*      */ 
/*      */ 
/*      */       public void perform(UTTimerEvent event)
/*      */       {
/*  490 */         this.ticks += 1;
/*      */         
/*  492 */         DHTTrackerPlugin.this.processRegistrations(this.ticks % 8 == 0);
/*      */         
/*  494 */         if ((this.ticks == 2) || (this.ticks % 4 == 0))
/*      */         {
/*  496 */           DHTTrackerPlugin.this.processNonRegistrations();
/*      */         }
/*      */         
/*  499 */         if (DHTTrackerPlugin.this.alt_lookup_handler != null)
/*      */         {
/*  501 */           if (this.ticks % 4 == 0)
/*      */           {
/*  503 */             String alt_status = DHTTrackerPlugin.this.alt_lookup_handler.getString();
/*      */             
/*  505 */             if (!alt_status.equals(this.prev_alt_status))
/*      */             {
/*  507 */               DHTTrackerPlugin.this.log.log("Alternative stats: " + alt_status);
/*      */               
/*  509 */               this.prev_alt_status = alt_status;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public void waitUntilInitialised()
/*      */   {
/*  520 */     this.initialised_sem.reserve();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isRunning()
/*      */   {
/*  526 */     return this.is_running;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addDownload(final Download download)
/*      */   {
/*  533 */     Torrent torrent = download.getTorrent();
/*      */     
/*  535 */     boolean is_decentralised = false;
/*      */     
/*  537 */     if (torrent != null)
/*      */     {
/*  539 */       is_decentralised = TorrentUtils.isDecentralised(torrent.getAnnounceURL());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  544 */     if ((download.getFlag(16L)) && (!is_decentralised))
/*      */     {
/*  546 */       return;
/*      */     }
/*      */     
/*  549 */     if (this.track_only_decentralsed)
/*      */     {
/*  551 */       if (torrent != null)
/*      */       {
/*  553 */         if (!is_decentralised)
/*      */         {
/*  555 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  560 */     if (this.is_running)
/*      */     {
/*  562 */       String[] networks = download.getListAttribute(this.ta_networks);
/*      */       
/*  564 */       if ((torrent != null) && (networks != null))
/*      */       {
/*  566 */         boolean public_net = false;
/*      */         
/*  568 */         for (int i = 0; i < networks.length; i++)
/*      */         {
/*  570 */           if (networks[i].equalsIgnoreCase("Public"))
/*      */           {
/*  572 */             public_net = true;
/*      */             
/*  574 */             break;
/*      */           }
/*      */         }
/*      */         
/*  578 */         if ((public_net) && (!torrent.isPrivate()))
/*      */         {
/*  580 */           boolean our_download = torrent.wasCreatedByUs();
/*      */           
/*      */           long delay;
/*      */           long delay;
/*  584 */           if (our_download) {
/*      */             long delay;
/*  586 */             if (download.getCreationTime() > start_time)
/*      */             {
/*  588 */               delay = 0L;
/*      */             }
/*      */             else
/*      */             {
/*  592 */               delay = this.plugin_interface.getUtilities().getCurrentSystemTime() + 120000L + this.random.nextInt(300000);
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*      */             int rand;
/*      */             
/*      */             int min;
/*      */             
/*      */             int rand;
/*  602 */             if (TorrentUtils.isDecentralised(torrent.getAnnounceURL()))
/*      */             {
/*  604 */               int min = 120000;
/*  605 */               rand = 300000;
/*      */             }
/*      */             else
/*      */             {
/*  609 */               min = 300000;
/*  610 */               rand = 1800000;
/*      */             }
/*      */             
/*  613 */             delay = this.plugin_interface.getUtilities().getCurrentSystemTime() + min + this.random.nextInt(rand);
/*      */           }
/*      */           
/*      */           try
/*      */           {
/*  618 */             this.this_mon.enter();
/*      */             
/*  620 */             this.interesting_downloads.put(download, new Long(delay));
/*      */           }
/*      */           finally
/*      */           {
/*  624 */             this.this_mon.exit();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  629 */       download.addAttributeListener(this, this.ta_networks, 1);
/*  630 */       download.addAttributeListener(this, this.ta_peer_sources, 1);
/*      */       
/*  632 */       download.addTrackerListener(this);
/*      */       
/*  634 */       download.addListener(this);
/*      */       
/*  636 */       checkDownloadForRegistration(download, true);
/*      */ 
/*      */ 
/*      */     }
/*  640 */     else if ((torrent != null) && (torrent.isDecentralised()))
/*      */     {
/*  642 */       download.addListener(new DownloadListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void stateChanged(final Download download, int old_state, int new_state)
/*      */         {
/*      */ 
/*      */ 
/*  651 */           int state = download.getState();
/*      */           
/*  653 */           if ((state == 4) || (state == 5))
/*      */           {
/*      */ 
/*  656 */             download.setAnnounceResult(new DownloadAnnounceResult()
/*      */             {
/*      */ 
/*      */               public Download getDownload()
/*      */               {
/*      */ 
/*  662 */                 return download;
/*      */               }
/*      */               
/*      */ 
/*      */               public int getResponseType()
/*      */               {
/*  668 */                 return 2;
/*      */               }
/*      */               
/*      */ 
/*      */               public int getReportedPeerCount()
/*      */               {
/*  674 */                 return 0;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */               public int getSeedCount()
/*      */               {
/*  681 */                 return 0;
/*      */               }
/*      */               
/*      */ 
/*      */               public int getNonSeedCount()
/*      */               {
/*  687 */                 return 0;
/*      */               }
/*      */               
/*      */ 
/*      */               public String getError()
/*      */               {
/*  693 */                 return "Distributed Database Offline";
/*      */               }
/*      */               
/*      */ 
/*      */               public URL getURL()
/*      */               {
/*  699 */                 return download.getTorrent().getAnnounceURL();
/*      */               }
/*      */               
/*      */ 
/*      */               public DownloadAnnounceResultPeer[] getPeers()
/*      */               {
/*  705 */                 return new DownloadAnnounceResultPeer[0];
/*      */               }
/*      */               
/*      */ 
/*      */               public long getTimeToWait()
/*      */               {
/*  711 */                 return 0L;
/*      */               }
/*      */               
/*      */ 
/*      */               public Map getExtensions()
/*      */               {
/*  717 */                 return null;
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void positionChanged(Download download, int oldPosition, int newPosition) {}
/*  733 */       });
/*  734 */       download.setScrapeResult(new DownloadScrapeResult()
/*      */       {
/*      */ 
/*      */         public Download getDownload()
/*      */         {
/*      */ 
/*  740 */           return download;
/*      */         }
/*      */         
/*      */ 
/*      */         public int getResponseType()
/*      */         {
/*  746 */           return 2;
/*      */         }
/*      */         
/*      */ 
/*      */         public int getSeedCount()
/*      */         {
/*  752 */           return -1;
/*      */         }
/*      */         
/*      */ 
/*      */         public int getNonSeedCount()
/*      */         {
/*  758 */           return -1;
/*      */         }
/*      */         
/*      */ 
/*      */         public long getScrapeStartTime()
/*      */         {
/*  764 */           return SystemTime.getCurrentTime();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void setNextScrapeStartTime(long nextScrapeStartTime) {}
/*      */         
/*      */ 
/*      */ 
/*      */         public long getNextScrapeStartTime()
/*      */         {
/*  776 */           return -1L;
/*      */         }
/*      */         
/*      */ 
/*      */         public String getStatus()
/*      */         {
/*  782 */           return "Distributed Database Offline";
/*      */         }
/*      */         
/*      */ 
/*      */         public URL getURL()
/*      */         {
/*  788 */           return download.getTorrent().getAnnounceURL();
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeDownload(Download download)
/*      */   {
/*  799 */     if (this.is_running) {
/*  800 */       download.removeTrackerListener(this);
/*      */       
/*  802 */       download.removeListener(this);
/*      */       try
/*      */       {
/*  805 */         this.this_mon.enter();
/*      */         
/*  807 */         this.interesting_downloads.remove(download);
/*      */         
/*  809 */         this.running_downloads.remove(download);
/*      */         
/*  811 */         this.run_data_cache.remove(download);
/*      */         
/*  813 */         this.limited_online_tracking.remove(download);
/*      */       }
/*      */       finally
/*      */       {
/*  817 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void attributeEventOccurred(Download download, TorrentAttribute attr, int event_type)
/*      */   {
/*  825 */     checkDownloadForRegistration(download, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void scrapeResult(DownloadScrapeResult result)
/*      */   {
/*  832 */     checkDownloadForRegistration(result.getDownload(), false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void announceResult(DownloadAnnounceResult result)
/*      */   {
/*  839 */     checkDownloadForRegistration(result.getDownload(), false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkDownloadForRegistration(Download download, boolean first_time)
/*      */   {
/*  848 */     if (download == null)
/*      */     {
/*  850 */       return;
/*      */     }
/*      */     
/*  853 */     boolean skip_log = false;
/*      */     
/*  855 */     int state = download.getState();
/*      */     
/*  857 */     int register_type = 1;
/*      */     
/*      */ 
/*      */ 
/*  861 */     Random random = new Random();
/*      */     
/*      */ 
/*      */ 
/*      */     String register_reason;
/*      */     
/*      */ 
/*      */ 
/*  869 */     if ((state == 4) || (state == 5) || (download.isPaused()))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  874 */       String[] networks = download.getListAttribute(this.ta_networks);
/*      */       
/*  876 */       Torrent torrent = download.getTorrent();
/*      */       String register_reason;
/*  878 */       String register_reason; if ((torrent != null) && (networks != null))
/*      */       {
/*  880 */         boolean public_net = false;
/*      */         
/*  882 */         for (int i = 0; i < networks.length; i++)
/*      */         {
/*  884 */           if (networks[i].equalsIgnoreCase("Public"))
/*      */           {
/*  886 */             public_net = true;
/*      */             
/*  888 */             break;
/*      */           }
/*      */         }
/*      */         String register_reason;
/*  892 */         if ((public_net) && (!torrent.isPrivate())) {
/*      */           String register_reason;
/*  894 */           if (torrent.isDecentralised())
/*      */           {
/*      */ 
/*      */ 
/*  898 */             register_type = 2;
/*      */             
/*  900 */             register_reason = "decentralised";
/*      */           }
/*      */           else {
/*      */             String register_reason;
/*  904 */             if (torrent.isDecentralisedBackupEnabled())
/*      */             {
/*  906 */               String[] sources = download.getListAttribute(this.ta_peer_sources);
/*      */               
/*  908 */               boolean ok = false;
/*      */               
/*  910 */               if (sources != null)
/*      */               {
/*  912 */                 for (int i = 0; i < sources.length; i++)
/*      */                 {
/*  914 */                   if (sources[i].equalsIgnoreCase("DHT"))
/*      */                   {
/*  916 */                     ok = true;
/*      */                     
/*  918 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               String register_reason;
/*  923 */               if (!ok)
/*      */               {
/*  925 */                 register_reason = "decentralised peer source disabled";
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*  930 */                 boolean is_active = (state == 4) || (state == 5) || (download.isPaused());
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*  935 */                 if (is_active)
/*      */                 {
/*  937 */                   register_type = 3;
/*      */                 }
/*      */                 String register_reason;
/*  940 */                 if (torrent.isDecentralisedBackupRequested())
/*      */                 {
/*  942 */                   register_type = 2;
/*      */                   
/*  944 */                   register_reason = "torrent requests decentralised tracking";
/*      */                 }
/*  946 */                 else if (this.track_normal_when_offline.getValue())
/*      */                 {
/*      */                   String register_reason;
/*      */                   String register_reason;
/*  950 */                   if (is_active)
/*      */                   {
/*  952 */                     DownloadAnnounceResult result = download.getLastAnnounceResult();
/*      */                     String register_reason;
/*  954 */                     if ((result == null) || (result.getResponseType() == 2) || (TorrentUtils.isDecentralised(result.getURL())))
/*      */                     {
/*      */ 
/*      */ 
/*  958 */                       register_type = 2;
/*      */                       
/*  960 */                       register_reason = "tracker unavailable (announce)";
/*      */                     }
/*      */                     else
/*      */                     {
/*  964 */                       register_reason = "tracker available (announce: " + result.getURL() + ")";
/*      */                     }
/*      */                   }
/*      */                   else {
/*  968 */                     DownloadScrapeResult result = download.getLastScrapeResult();
/*      */                     String register_reason;
/*  970 */                     if ((result == null) || (result.getResponseType() == 2) || (TorrentUtils.isDecentralised(result.getURL())))
/*      */                     {
/*      */ 
/*      */ 
/*  974 */                       register_type = 2;
/*      */                       
/*  976 */                       register_reason = "tracker unavailable (scrape)";
/*      */                     }
/*      */                     else
/*      */                     {
/*  980 */                       register_reason = "tracker available (scrape: " + result.getURL() + ")";
/*      */                     }
/*      */                   }
/*      */                   
/*  984 */                   if ((register_type != 2) && (this.track_limited_when_online.getValue()))
/*      */                   {
/*  986 */                     Boolean existing = (Boolean)this.limited_online_tracking.get(download);
/*      */                     
/*  988 */                     boolean track_it = false;
/*      */                     
/*  990 */                     if (existing != null)
/*      */                     {
/*  992 */                       track_it = existing.booleanValue();
/*      */                     }
/*      */                     else
/*      */                     {
/*  996 */                       DownloadScrapeResult result = download.getLastScrapeResult();
/*      */                       
/*  998 */                       if ((result != null) && (result.getResponseType() == 1))
/*      */                       {
/*      */ 
/* 1001 */                         int seeds = result.getSeedCount();
/* 1002 */                         int leechers = result.getNonSeedCount();
/*      */                         
/* 1004 */                         int swarm_size = seeds + leechers;
/*      */                         
/* 1006 */                         if (swarm_size <= 16)
/*      */                         {
/* 1008 */                           track_it = true;
/*      */                         }
/*      */                         else
/*      */                         {
/* 1012 */                           track_it = random.nextInt(swarm_size) < 16;
/*      */                         }
/*      */                         
/* 1015 */                         if (track_it)
/*      */                         {
/* 1017 */                           this.limited_online_tracking.put(download, Boolean.valueOf(track_it));
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     
/* 1022 */                     if (track_it)
/*      */                     {
/* 1024 */                       register_type = 2;
/*      */                       
/* 1026 */                       register_reason = "limited online tracking";
/*      */                     }
/*      */                   }
/*      */                 } else {
/* 1030 */                   register_type = 2;
/*      */                   
/* 1032 */                   register_reason = "peer source enabled";
/*      */                 }
/*      */               }
/*      */             }
/*      */             else {
/* 1037 */               register_reason = "decentralised backup disabled for the torrent";
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/* 1042 */           register_reason = "not public";
/*      */         }
/*      */       }
/*      */       else {
/* 1046 */         register_reason = "torrent is broken";
/*      */       }
/*      */       
/* 1049 */       if (register_type == 3)
/*      */       {
/* 1051 */         if (register_reason.length() == 0)
/*      */         {
/* 1053 */           register_reason = "derived";
/*      */         }
/*      */         else
/*      */         {
/* 1057 */           register_reason = "derived (overriding ' " + register_reason + "')";
/*      */         }
/*      */       }
/* 1060 */     } else if ((state == 7) || (state == 8))
/*      */     {
/*      */ 
/* 1063 */       String register_reason = "not running";
/*      */       
/* 1065 */       skip_log = true;
/*      */     } else { String register_reason;
/* 1067 */       if (state == 9)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1072 */         register_reason = "";
/*      */       }
/*      */       else
/*      */       {
/* 1076 */         register_reason = "";
/*      */       }
/*      */     }
/* 1079 */     if (register_reason.length() > 0) {
/*      */       try
/*      */       {
/* 1082 */         this.this_mon.enter();
/*      */         
/* 1084 */         int[] run_data = (int[])this.running_downloads.get(download);
/*      */         
/* 1086 */         if (register_type != 1)
/*      */         {
/* 1088 */           if (run_data == null)
/*      */           {
/* 1090 */             log(download, "Monitoring '" + download.getName() + "': " + register_reason);
/*      */             
/* 1092 */             int[] cache = (int[])this.run_data_cache.remove(download);
/*      */             
/* 1094 */             if (cache == null)
/*      */             {
/* 1096 */               this.running_downloads.put(download, new int[] { register_type, 0, 0, 0, 0 });
/*      */             }
/*      */             else
/*      */             {
/* 1100 */               cache[0] = register_type;
/*      */               
/* 1102 */               this.running_downloads.put(download, cache);
/*      */             }
/*      */             
/* 1105 */             this.query_map.put(download, new Long(SystemTime.getCurrentTime()));
/*      */           }
/*      */           else
/*      */           {
/* 1109 */             Integer existing_type = Integer.valueOf(run_data[0]);
/*      */             
/* 1111 */             if ((existing_type.intValue() == 3) && (register_type == 2))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1116 */               run_data[0] = register_type;
/*      */             }
/*      */             
/*      */           }
/*      */         }
/* 1121 */         else if (run_data != null)
/*      */         {
/* 1123 */           if (!skip_log)
/*      */           {
/* 1125 */             log(download, "Not monitoring: " + register_reason);
/*      */           }
/*      */           
/* 1128 */           this.running_downloads.remove(download);
/*      */           
/* 1130 */           this.run_data_cache.put(download, run_data);
/*      */           
/*      */ 
/*      */ 
/* 1134 */           this.interesting_downloads.put(download, new Long(this.plugin_interface.getUtilities().getCurrentSystemTime() + 300000L));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/* 1141 */         else if ((first_time) && (!skip_log))
/*      */         {
/* 1143 */           log(download, "Not monitoring: " + register_reason);
/*      */         }
/*      */         
/*      */       }
/*      */       finally
/*      */       {
/* 1149 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void processRegistrations(boolean full_processing)
/*      */   {
/* 1158 */     int tcp_port = this.plugin_interface.getPluginconfig().getIntParameter("TCP.Listen.Port");
/*      */     
/* 1160 */     String port_override = COConfigurationManager.getStringParameter("TCP.Listen.Port.Override");
/*      */     
/* 1162 */     if (!port_override.equals("")) {
/*      */       try
/*      */       {
/* 1165 */         tcp_port = Integer.parseInt(port_override);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/* 1171 */     if (tcp_port == 0)
/*      */     {
/* 1173 */       this.log.log("TCP port=0, registration not performed");
/*      */       
/* 1175 */       return;
/*      */     }
/*      */     
/* 1178 */     String override_ips = COConfigurationManager.getStringParameter("Override Ip", "");
/*      */     
/* 1180 */     String override_ip = null;
/*      */     
/* 1182 */     if (override_ips.length() > 0)
/*      */     {
/*      */ 
/*      */ 
/* 1186 */       StringTokenizer tok = new StringTokenizer(override_ips, ";");
/*      */       
/* 1188 */       while (tok.hasMoreTokens())
/*      */       {
/* 1190 */         String this_address = tok.nextToken().trim();
/*      */         
/* 1192 */         if (this_address.length() > 0)
/*      */         {
/* 1194 */           String cat = AENetworkClassifier.categoriseAddress(this_address);
/*      */           
/* 1196 */           if (cat == "Public")
/*      */           {
/* 1198 */             override_ip = this_address;
/*      */             
/* 1200 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1206 */     if (override_ip != null) {
/*      */       try
/*      */       {
/* 1209 */         override_ip = PRHelpers.DNSToIPAddress(override_ip);
/*      */       }
/*      */       catch (UnknownHostException e)
/*      */       {
/* 1213 */         this.log.log("    Can't resolve IP override '" + override_ip + "'");
/*      */         
/* 1215 */         override_ip = null;
/*      */       }
/*      */     }
/*      */     
/*      */     ArrayList<Download> rds;
/*      */     try
/*      */     {
/* 1222 */       this.this_mon.enter();
/*      */       
/* 1224 */       rds = new ArrayList(this.running_downloads.keySet());
/*      */     }
/*      */     finally
/*      */     {
/* 1228 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1231 */     long now = SystemTime.getCurrentTime();
/*      */     
/*      */ 
/* 1234 */     if (full_processing)
/*      */     {
/* 1236 */       Object rds_it = rds.iterator();
/*      */       
/* 1238 */       List<Object[]> interesting = new ArrayList();
/*      */       
/* 1240 */       while (((Iterator)rds_it).hasNext())
/*      */       {
/* 1242 */         Download dl = (Download)((Iterator)rds_it).next();
/*      */         
/* 1244 */         int reg_type = 1;
/*      */         try
/*      */         {
/* 1247 */           this.this_mon.enter();
/*      */           
/* 1249 */           int[] run_data = (int[])this.running_downloads.get(dl);
/*      */           
/* 1251 */           if (run_data != null)
/*      */           {
/* 1253 */             reg_type = run_data[0];
/*      */           }
/*      */         }
/*      */         finally {
/* 1257 */           this.this_mon.exit();
/*      */         }
/*      */         
/* 1260 */         if (reg_type != 1)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1265 */           long metric = getDerivedTrackMetric(dl);
/*      */           
/* 1267 */           interesting.add(new Object[] { dl, new Long(metric) });
/*      */         }
/*      */       }
/* 1270 */       Collections.sort(interesting, new Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(Object[] entry1, Object[] entry2)
/*      */         {
/*      */ 
/*      */ 
/* 1279 */           long res = ((Long)entry2[1]).longValue() - ((Long)entry1[1]).longValue();
/*      */           
/* 1281 */           if (res < 0L)
/*      */           {
/* 1283 */             return -1;
/*      */           }
/* 1285 */           if (res > 0L)
/*      */           {
/* 1287 */             return 1;
/*      */           }
/*      */           
/*      */ 
/* 1291 */           return 0;
/*      */         }
/*      */         
/*      */ 
/* 1295 */       });
/* 1296 */       Iterator<Object[]> it = interesting.iterator();
/*      */       
/* 1298 */       int num = 0;
/*      */       
/* 1300 */       while (it.hasNext())
/*      */       {
/* 1302 */         Object[] entry = (Object[])it.next();
/*      */         
/* 1304 */         Download dl = (Download)entry[0];
/* 1305 */         long metric = ((Long)entry[1]).longValue();
/*      */         
/* 1307 */         num++;
/*      */         
/* 1309 */         if (metric > 0L)
/*      */         {
/* 1311 */           if (num > 5)
/*      */           {
/*      */ 
/*      */ 
/* 1315 */             if (num <= 20)
/*      */             {
/*      */ 
/*      */ 
/* 1319 */               metric = metric * (20 - num) / 15L;
/*      */             }
/*      */             else
/*      */             {
/* 1323 */               metric = 0L;
/*      */             }
/*      */           }
/*      */         }
/* 1327 */         if (metric > 0L)
/*      */         {
/* 1329 */           dl.setUserData(DL_DERIVED_METRIC_KEY, new Long(metric));
/*      */         }
/*      */         else
/*      */         {
/* 1333 */           dl.setUserData(DL_DERIVED_METRIC_KEY, null);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1338 */     Object rds_it = rds.iterator();
/*      */     
/*      */ 
/*      */ 
/* 1342 */     while (((Iterator)rds_it).hasNext())
/*      */     {
/* 1344 */       Download dl = (Download)((Iterator)rds_it).next();
/*      */       
/* 1346 */       int reg_type = 1;
/*      */       try
/*      */       {
/* 1349 */         this.this_mon.enter();
/*      */         
/* 1351 */         int[] run_data = (int[])this.running_downloads.get(dl);
/*      */         
/* 1353 */         if (run_data != null)
/*      */         {
/* 1355 */           reg_type = run_data[0];
/*      */         }
/*      */       }
/*      */       finally {
/* 1359 */         this.this_mon.exit();
/*      */       }
/*      */       
/* 1362 */       if (reg_type != 1)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1369 */         String value_to_put = override_ip + ":";
/*      */         
/* 1371 */         value_to_put = value_to_put + tcp_port;
/*      */         
/* 1373 */         String put_flags = ";";
/*      */         
/* 1375 */         if (NetworkManager.REQUIRE_CRYPTO_HANDSHAKE)
/*      */         {
/* 1377 */           put_flags = put_flags + "C";
/*      */         }
/*      */         
/* 1380 */         String[] networks = dl.getListAttribute(this.ta_networks);
/*      */         
/* 1382 */         boolean i2p = false;
/*      */         
/* 1384 */         if (networks != null)
/*      */         {
/* 1386 */           for (String net : networks)
/*      */           {
/* 1388 */             if (net == "I2P")
/*      */             {
/* 1390 */               if (I2PHelpers.isI2PInstalled())
/*      */               {
/* 1392 */                 put_flags = put_flags + "I";
/*      */               }
/*      */               
/* 1395 */               i2p = true;
/*      */               
/* 1397 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1402 */         if (put_flags.length() > 1)
/*      */         {
/* 1404 */           value_to_put = value_to_put + put_flags;
/*      */         }
/*      */         
/* 1407 */         int udp_port = this.plugin_interface.getPluginconfig().getIntParameter("UDP.Listen.Port");
/*      */         
/* 1409 */         int dht_port = this.dht.getLocalAddress().getAddress().getPort();
/*      */         
/* 1411 */         if (udp_port != dht_port)
/*      */         {
/* 1413 */           value_to_put = value_to_put + ";" + udp_port;
/*      */         }
/*      */         
/* 1416 */         putDetails put_details = new putDetails(value_to_put, override_ip, tcp_port, udp_port, i2p, null);
/*      */         
/* 1418 */         byte dht_flags = isComplete(dl) ? 2 : 1;
/*      */         
/* 1420 */         RegistrationDetails registration = (RegistrationDetails)this.registered_downloads.get(dl);
/*      */         
/* 1422 */         boolean do_it = false;
/*      */         
/* 1424 */         if (registration == null)
/*      */         {
/* 1426 */           log(dl, "Registering download as " + (dht_flags == 2 ? "Seeding" : "Downloading"));
/*      */           
/* 1428 */           registration = new RegistrationDetails(dl, reg_type, put_details, dht_flags);
/*      */           
/* 1430 */           this.registered_downloads.put(dl, registration);
/*      */           
/* 1432 */           do_it = true;
/*      */         }
/*      */         else
/*      */         {
/* 1436 */           boolean targets_changed = false;
/*      */           
/* 1438 */           if (full_processing)
/*      */           {
/* 1440 */             targets_changed = registration.updateTargets(dl, reg_type);
/*      */           }
/*      */           
/* 1443 */           if ((targets_changed) || (registration.getFlags() != dht_flags) || (!registration.getPutDetails().sameAs(put_details)))
/*      */           {
/*      */ 
/*      */ 
/* 1447 */             log(dl, (registration == null ? "Registering" : "Re-registering") + " download as " + (dht_flags == 2 ? "Seeding" : "Downloading"));
/*      */             
/* 1449 */             registration.update(put_details, dht_flags);
/*      */             
/* 1451 */             do_it = true;
/*      */           }
/*      */         }
/*      */         
/* 1455 */         if (do_it)
/*      */         {
/*      */           try {
/* 1458 */             this.this_mon.enter();
/*      */             
/* 1460 */             this.query_map.put(dl, new Long(now));
/*      */           }
/*      */           finally
/*      */           {
/* 1464 */             this.this_mon.exit();
/*      */           }
/*      */           
/* 1467 */           trackerPut(dl, registration);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1473 */     Iterator<Map.Entry<Download, RegistrationDetails>> rd_it = this.registered_downloads.entrySet().iterator();
/*      */     
/* 1475 */     while (rd_it.hasNext())
/*      */     {
/* 1477 */       Map.Entry<Download, RegistrationDetails> entry = (Map.Entry)rd_it.next();
/*      */       
/* 1479 */       Download dl = (Download)entry.getKey();
/*      */       
/*      */       boolean unregister;
/*      */       try
/*      */       {
/* 1484 */         this.this_mon.enter();
/*      */         
/* 1486 */         unregister = !this.running_downloads.containsKey(dl);
/*      */       }
/*      */       finally
/*      */       {
/* 1490 */         this.this_mon.exit();
/*      */       }
/*      */       
/* 1493 */       if (unregister)
/*      */       {
/* 1495 */         log(dl, "Unregistering download");
/*      */         
/* 1497 */         rd_it.remove();
/*      */         try
/*      */         {
/* 1500 */           this.this_mon.enter();
/*      */           
/* 1502 */           this.query_map.remove(dl);
/*      */         }
/*      */         finally
/*      */         {
/* 1506 */           this.this_mon.exit();
/*      */         }
/*      */         
/* 1509 */         trackerRemove(dl, (RegistrationDetails)entry.getValue());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1515 */     rds_it = rds.iterator();
/*      */     
/* 1517 */     while (((Iterator)rds_it).hasNext())
/*      */     {
/* 1519 */       Download dl = (Download)((Iterator)rds_it).next();
/*      */       
/*      */       Long next_time;
/*      */       try
/*      */       {
/* 1524 */         this.this_mon.enter();
/*      */         
/* 1526 */         next_time = (Long)this.query_map.get(dl);
/*      */       }
/*      */       finally
/*      */       {
/* 1530 */         this.this_mon.exit();
/*      */       }
/*      */       
/* 1533 */       if ((next_time != null) && (now >= next_time.longValue()))
/*      */       {
/* 1535 */         int reg_type = 1;
/*      */         try
/*      */         {
/* 1538 */           this.this_mon.enter();
/*      */           
/* 1540 */           this.query_map.remove(dl);
/*      */           
/* 1542 */           int[] run_data = (int[])this.running_downloads.get(dl);
/*      */           
/* 1544 */           if (run_data != null)
/*      */           {
/* 1546 */             reg_type = run_data[0];
/*      */           }
/*      */         }
/*      */         finally {
/* 1550 */           this.this_mon.exit();
/*      */         }
/*      */         
/* 1553 */         long start = SystemTime.getCurrentTime();
/*      */         
/*      */ 
/*      */ 
/* 1557 */         PeerManager pm = dl.getPeerManager();
/*      */         
/*      */ 
/*      */ 
/* 1561 */         boolean skip = (isActive(dl)) || (reg_type == 1);
/*      */         
/* 1563 */         if (skip)
/*      */         {
/* 1565 */           log(dl, "Deferring announce as activity outstanding");
/*      */         }
/*      */         
/* 1568 */         RegistrationDetails registration = (RegistrationDetails)this.registered_downloads.get(dl);
/*      */         
/* 1570 */         if (registration == null)
/*      */         {
/* 1572 */           Debug.out("Inconsistent, registration should be non-null");
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1577 */           boolean derived_only = false;
/*      */           
/* 1579 */           if ((pm != null) && (!skip))
/*      */           {
/* 1581 */             int con = pm.getStats().getConnectedLeechers() + pm.getStats().getConnectedSeeds();
/*      */             
/* 1583 */             derived_only = con >= 30;
/*      */           }
/*      */           
/* 1586 */           if (!skip)
/*      */           {
/* 1588 */             skip = trackerGet(dl, registration, derived_only) == 0;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1595 */           if (skip) {
/*      */             try
/*      */             {
/* 1598 */               this.this_mon.enter();
/*      */               
/* 1600 */               if (this.running_downloads.containsKey(dl))
/*      */               {
/*      */ 
/*      */ 
/* 1604 */                 this.query_map.put(dl, new Long(start + 120000L));
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/* 1609 */               this.this_mon.exit();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long getDerivedTrackMetric(Download download)
/*      */   {
/* 1623 */     Torrent t = download.getTorrent();
/*      */     
/* 1625 */     if (t == null)
/*      */     {
/* 1627 */       return -100L;
/*      */     }
/*      */     
/* 1630 */     if (t.getSize() < 10485760L)
/*      */     {
/* 1632 */       return -99L;
/*      */     }
/*      */     
/* 1635 */     DownloadAnnounceResult announce = download.getLastAnnounceResult();
/*      */     
/* 1637 */     if ((announce == null) || (announce.getResponseType() != 1))
/*      */     {
/*      */ 
/* 1640 */       return -98L;
/*      */     }
/*      */     
/* 1643 */     DownloadScrapeResult scrape = download.getLastScrapeResult();
/*      */     
/* 1645 */     if ((scrape == null) || (scrape.getResponseType() != 1))
/*      */     {
/*      */ 
/* 1648 */       return -97L;
/*      */     }
/*      */     
/* 1651 */     int leechers = scrape.getNonSeedCount();
/*      */     
/*      */ 
/* 1654 */     int total = leechers;
/*      */     
/* 1656 */     if (total >= 2000)
/*      */     {
/* 1658 */       return 100L;
/*      */     }
/* 1660 */     if (total <= 200)
/*      */     {
/* 1662 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/* 1666 */     return (total - 200) / 4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void trackerPut(final Download download, RegistrationDetails details)
/*      */   {
/* 1675 */     final long start = SystemTime.getCurrentTime();
/*      */     
/* 1677 */     trackerTarget[] targets = details.getTargets(true);
/*      */     
/* 1679 */     byte flags = details.getFlags();
/*      */     
/* 1681 */     for (int i = 0; i < targets.length; i++)
/*      */     {
/* 1683 */       final trackerTarget target = targets[i];
/*      */       
/* 1685 */       int target_type = target.getType();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1692 */       String encoded = details.getPutDetails().getEncoded();
/*      */       
/* 1694 */       byte[] encoded_bytes = encoded.getBytes();
/*      */       
/* 1696 */       DHTPluginValue existing = this.dht.getLocalValue(target.getHash());
/*      */       
/* 1698 */       if ((existing == null) || (existing.getFlags() != flags) || (!Arrays.equals(existing.getValue(), encoded_bytes)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1707 */         if (this.disable_put)
/*      */         {
/* 1709 */           if (target_type == 2)
/*      */           {
/* 1711 */             log(download, "Registration of '" + target.getDesc() + "' skipped as disabled due to use of SOCKS proxy");
/*      */           }
/* 1713 */         } else if (download.getFlag(512L))
/*      */         {
/* 1715 */           log(download, "Registration of '" + target.getDesc() + "' skipped as metadata download");
/*      */         }
/* 1717 */         else if ((target_type == 3) && (this.dht.isSleeping()))
/*      */         {
/* 1719 */           log(download, "Registration of '" + target.getDesc() + "' skipped as sleeping");
/*      */         }
/*      */         else
/*      */         {
/* 1723 */           this.dht.put(target.getHash(), "Tracker reg of '" + download.getName() + "'" + target.getDesc() + " -> " + encoded, encoded_bytes, flags, false, new DHTPluginOperationListener()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public boolean diversified()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1734 */               return true;
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
/* 1762 */               if (target.getType() == 2)
/*      */               {
/* 1764 */                 DHTTrackerPlugin.this.log(download, "Registration of '" + target.getDesc() + "' completed (elapsed=" + TimeFormatter.formatColonMillis(SystemTime.getCurrentTime() - start) + ")");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int trackerGet(final Download download, final RegistrationDetails details, boolean derived_only)
/*      */   {
/* 1781 */     final long start = SystemTime.getCurrentTime();
/*      */     
/* 1783 */     final Torrent torrent = download.getTorrent();
/*      */     
/* 1785 */     final URL url_to_report = torrent.isDecentralised() ? torrent.getAnnounceURL() : DEFAULT_URL;
/*      */     
/* 1787 */     trackerTarget[] targets = details.getTargets(false);
/*      */     
/* 1789 */     final long[] max_retry = { 0L };
/*      */     
/* 1791 */     final boolean do_alt = (this.alt_lookup_handler != null) && (!download.getFlag(16L)) && (!download.getFlag(1024L));
/*      */     
/*      */ 
/*      */ 
/* 1795 */     int num_done = 0;
/*      */     
/* 1797 */     for (int i = 0; i < targets.length; i++)
/*      */     {
/* 1799 */       final trackerTarget target = targets[i];
/*      */       
/* 1801 */       int target_type = target.getType();
/*      */       
/* 1803 */       if ((target_type != 2) || (!derived_only))
/*      */       {
/*      */ 
/*      */ 
/* 1807 */         if ((target_type != 3) || (!this.dht.isSleeping()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1812 */           increaseActive(download);
/*      */           
/* 1814 */           num_done++;
/*      */           
/* 1816 */           final boolean is_complete = isComplete(download);
/*      */           
/* 1818 */           this.dht.get(target.getHash(), "Tracker announce for '" + download.getName() + "'" + target.getDesc(), (byte)(is_complete ? 2 : 1), 30, target_type == 2 ? 120000L : 60000L, false, false, new DHTPluginOperationListener()
/*      */           {
/*      */             List<String> addresses;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             List<Integer> ports;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             List<Integer> udp_ports;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             List<Boolean> is_seeds;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             List<String> flags;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             int seed_count;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             int leecher_count;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             int i2p_seed_count;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             int i2p_leecher_count;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             volatile boolean complete;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public boolean diversified()
/*      */             {
/* 1872 */               return true;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void starts(byte[] key) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             private void alternativePeerRead(InetSocketAddress peer)
/*      */             {
/* 1885 */               boolean try_injection = false;
/*      */               
/* 1887 */               synchronized (this)
/*      */               {
/* 1889 */                 if (this.complete)
/*      */                 {
/* 1891 */                   try_injection = this.addresses.size() < 5;
/*      */                 }
/*      */                 else {
/*      */                   try
/*      */                   {
/* 1896 */                     this.addresses.add(peer.getAddress().getHostAddress());
/* 1897 */                     this.ports.add(Integer.valueOf(peer.getPort()));
/* 1898 */                     this.udp_ports.add(Integer.valueOf(0));
/* 1899 */                     this.flags.add(null);
/*      */                     
/* 1901 */                     this.is_seeds.add(Boolean.valueOf(false));
/* 1902 */                     this.leecher_count += 1;
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/* 1909 */               if (try_injection)
/*      */               {
/* 1911 */                 PeerManager pm = download.getPeerManager();
/*      */                 
/* 1913 */                 if (pm != null)
/*      */                 {
/* 1915 */                   pm.peerDiscovered("DHT", peer.getAddress().getHostAddress(), peer.getPort(), 0, NetworkManager.getCryptoRequired(0));
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */             {
/* 1930 */               synchronized (this)
/*      */               {
/* 1932 */                 if (this.complete)
/*      */                 {
/* 1934 */                   return;
/*      */                 }
/*      */                 try
/*      */                 {
/* 1938 */                   String[] tokens = new String(value.getValue()).split(";");
/*      */                   
/* 1940 */                   String tcp_part = tokens[0].trim();
/*      */                   
/* 1942 */                   int sep = tcp_part.indexOf(':');
/*      */                   
/* 1944 */                   String ip_str = null;
/*      */                   String tcp_port_str;
/*      */                   String tcp_port_str;
/* 1947 */                   if (sep == -1)
/*      */                   {
/* 1949 */                     tcp_port_str = tcp_part;
/*      */                   }
/*      */                   else
/*      */                   {
/* 1953 */                     ip_str = tcp_part.substring(0, sep);
/* 1954 */                     tcp_port_str = tcp_part.substring(sep + 1);
/*      */                   }
/*      */                   
/* 1957 */                   int tcp_port = Integer.parseInt(tcp_port_str);
/*      */                   
/* 1959 */                   if ((tcp_port > 0) && (tcp_port < 65536))
/*      */                   {
/* 1961 */                     String flag_str = null;
/* 1962 */                     int udp_port = -1;
/*      */                     
/* 1964 */                     boolean has_i2p = false;
/*      */                     try
/*      */                     {
/* 1967 */                       for (int i = 1; i < tokens.length; i++)
/*      */                       {
/* 1969 */                         String token = tokens[i].trim();
/*      */                         
/* 1971 */                         if (token.length() > 0)
/*      */                         {
/* 1973 */                           if (Character.isDigit(token.charAt(0)))
/*      */                           {
/* 1975 */                             udp_port = Integer.parseInt(token);
/*      */                             
/* 1977 */                             if ((udp_port <= 0) || (udp_port >= 65536))
/*      */                             {
/* 1979 */                               udp_port = -1;
/*      */                             }
/*      */                           }
/*      */                           else {
/* 1983 */                             flag_str = token;
/*      */                             
/* 1985 */                             if (flag_str.contains("I"))
/*      */                             {
/* 1987 */                               has_i2p = true;
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e) {}
/*      */                     
/* 1995 */                     this.addresses.add(ip_str == null ? originator.getAddress().getAddress().getHostAddress() : ip_str);
/*      */                     
/*      */ 
/* 1998 */                     this.ports.add(new Integer(tcp_port));
/*      */                     
/* 2000 */                     this.udp_ports.add(new Integer(udp_port == -1 ? originator.getAddress().getPort() : udp_port));
/*      */                     
/* 2002 */                     this.flags.add(flag_str);
/*      */                     
/* 2004 */                     if ((value.getFlags() & 0x1) == 1)
/*      */                     {
/* 2006 */                       this.leecher_count += 1;
/*      */                       
/* 2008 */                       this.is_seeds.add(Boolean.FALSE);
/*      */                       
/* 2010 */                       if (has_i2p)
/*      */                       {
/* 2012 */                         this.i2p_leecher_count += 1;
/*      */                       }
/*      */                     }
/*      */                     else {
/* 2016 */                       this.is_seeds.add(Boolean.TRUE);
/*      */                       
/* 2018 */                       this.seed_count += 1;
/*      */                       
/* 2020 */                       if (has_i2p)
/*      */                       {
/* 2022 */                         this.i2p_seed_count += 1;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */             }
/*      */             
/*      */ 
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
/*      */ 
/*      */ 
/*      */             public void complete(byte[] key, boolean timeout_occurred)
/*      */             {
/* 2047 */               synchronized (this)
/*      */               {
/* 2049 */                 if (this.complete)
/*      */                 {
/* 2051 */                   return;
/*      */                 }
/*      */                 
/* 2054 */                 this.complete = true;
/*      */               }
/*      */               
/* 2057 */               if ((target.getType() == 2) || ((target.getType() == 3) && (this.seed_count + this.leecher_count > 1)))
/*      */               {
/*      */ 
/*      */ 
/* 2061 */                 DHTTrackerPlugin.this.log(download, "Get of '" + target.getDesc() + "' completed (elapsed=" + TimeFormatter.formatColonMillis(SystemTime.getCurrentTime() - start) + "), addresses=" + this.addresses.size() + ", seeds=" + this.seed_count + ", leechers=" + this.leecher_count);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 2067 */               DHTTrackerPlugin.this.decreaseActive(download);
/*      */               
/* 2069 */               int peers_found = this.addresses.size();
/*      */               
/* 2071 */               Object peers_for_announce = new ArrayList();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 2076 */               int announce_per_min = 4;
/*      */               
/* 2078 */               int num_active = DHTTrackerPlugin.this.query_map.size();
/*      */               
/* 2080 */               int announce_min = Math.max(120000, num_active / announce_per_min * 60 * 1000);
/*      */               
/* 2082 */               int announce_max = max_retry ? 1800000 : 3600000;
/*      */               
/* 2084 */               announce_min = Math.min(announce_min, announce_max);
/*      */               
/* 2086 */               DHTTrackerPlugin.this.current_announce_interval = announce_min;
/*      */               
/* 2088 */               final long retry = announce_min + peers_found * (announce_max - announce_min) / 30L;
/*      */               
/* 2090 */               int download_state = download.getState();
/*      */               
/* 2092 */               boolean we_are_seeding = download_state == 5;
/*      */               try
/*      */               {
/* 2095 */                 DHTTrackerPlugin.this.this_mon.enter();
/*      */                 
/* 2097 */                 int[] run_data = (int[])DHTTrackerPlugin.this.running_downloads.get(download);
/*      */                 
/* 2099 */                 if (run_data != null)
/*      */                 {
/* 2101 */                   boolean full = target.getType() == 2;
/*      */                   
/* 2103 */                   int peer_count = we_are_seeding ? this.leecher_count : this.seed_count + this.leecher_count;
/*      */                   
/* 2105 */                   run_data[1] = (full ? this.seed_count : Math.max(run_data[1], this.seed_count));
/* 2106 */                   run_data[2] = (full ? this.leecher_count : Math.max(run_data[2], this.leecher_count));
/* 2107 */                   run_data[3] = (full ? peer_count : Math.max(run_data[3], peer_count));
/*      */                   
/* 2109 */                   run_data[4] = ((int)(SystemTime.getCurrentTime() / 1000L));
/*      */                   
/* 2111 */                   long absolute_retry = SystemTime.getCurrentTime() + retry;
/*      */                   
/* 2113 */                   if (absolute_retry > details[0])
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2120 */                     Long existing = (Long)DHTTrackerPlugin.this.query_map.get(download);
/*      */                     
/* 2122 */                     if ((existing == null) || (existing.longValue() == details[0]))
/*      */                     {
/*      */ 
/* 2125 */                       details[0] = absolute_retry;
/*      */                       
/* 2127 */                       DHTTrackerPlugin.this.query_map.put(download, new Long(absolute_retry));
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               finally {
/* 2133 */                 DHTTrackerPlugin.this.this_mon.exit();
/*      */               }
/*      */               
/* 2136 */               DHTTrackerPlugin.putDetails put_details = url_to_report.getPutDetails();
/*      */               
/* 2138 */               String ext_address = put_details.getIPOverride();
/*      */               
/* 2140 */               if (ext_address == null)
/*      */               {
/* 2142 */                 ext_address = DHTTrackerPlugin.this.dht.getLocalAddress().getAddress().getAddress().getHostAddress();
/*      */               }
/*      */               
/* 2145 */               if (DHTTrackerPlugin.putDetails.access$1600(put_details))
/*      */               {
/* 2147 */                 if (we_are_seeding) {
/* 2148 */                   if (this.i2p_seed_count > 0) {
/* 2149 */                     this.i2p_seed_count -= 1;
/*      */                   }
/*      */                 }
/* 2152 */                 else if (this.i2p_leecher_count > 0) {
/* 2153 */                   this.i2p_leecher_count -= 1;
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/* 2158 */               if (this.i2p_seed_count + this.i2p_leecher_count > 0)
/*      */               {
/* 2160 */                 download.setUserData(DHTTrackerPlugin.DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY, new int[] { this.i2p_seed_count, this.i2p_leecher_count });
/*      */               }
/*      */               else
/*      */               {
/* 2164 */                 download.setUserData(DHTTrackerPlugin.DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY, null);
/*      */               }
/*      */               
/* 2167 */               for (int i = 0; i < this.addresses.size(); i++)
/*      */               {
/*      */ 
/*      */ 
/* 2171 */                 if ((!we_are_seeding) || (!((Boolean)this.is_seeds.get(i)).booleanValue()))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2178 */                   String ip = (String)this.addresses.get(i);
/*      */                   
/* 2180 */                   if ((!ip.equals(ext_address)) || 
/*      */                   
/* 2182 */                     (((Integer)this.ports.get(i)).intValue() != put_details.getTCPPort()) || (((Integer)this.udp_ports.get(i)).intValue() != put_details.getUDPPort()))
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2189 */                     final int f_i = i;
/*      */                     
/* 2191 */                     ((List)peers_for_announce).add(new DownloadAnnounceResultPeer()
/*      */                     {
/*      */ 
/*      */                       public String getSource()
/*      */                       {
/*      */ 
/* 2197 */                         return "DHT";
/*      */                       }
/*      */                       
/*      */ 
/*      */                       public String getAddress()
/*      */                       {
/* 2203 */                         return (String)DHTTrackerPlugin.13.this.addresses.get(f_i);
/*      */                       }
/*      */                       
/*      */ 
/*      */                       public int getPort()
/*      */                       {
/* 2209 */                         return ((Integer)DHTTrackerPlugin.13.this.ports.get(f_i)).intValue();
/*      */                       }
/*      */                       
/*      */ 
/*      */                       public int getUDPPort()
/*      */                       {
/* 2215 */                         return ((Integer)DHTTrackerPlugin.13.this.udp_ports.get(f_i)).intValue();
/*      */                       }
/*      */                       
/*      */ 
/*      */                       public byte[] getPeerID()
/*      */                       {
/* 2221 */                         return null;
/*      */                       }
/*      */                       
/*      */ 
/*      */                       public short getProtocol()
/*      */                       {
/* 2227 */                         String flag = (String)DHTTrackerPlugin.13.this.flags.get(f_i);
/*      */                         
/* 2229 */                         short protocol = 1;
/*      */                         
/* 2231 */                         if (flag != null)
/*      */                         {
/* 2233 */                           if (flag.contains("C"))
/*      */                           {
/* 2235 */                             protocol = 2;
/*      */                           }
/*      */                         }
/*      */                         
/* 2239 */                         return protocol;
/*      */                       }
/*      */                     });
/*      */                   }
/*      */                 }
/*      */               }
/* 2245 */               if ((target.getType() == 3) && (((List)peers_for_announce).size() > 0))
/*      */               {
/* 2247 */                 PeerManager pm = download.getPeerManager();
/*      */                 
/* 2249 */                 if (pm != null)
/*      */                 {
/*      */ 
/*      */ 
/* 2253 */                   List<DownloadAnnounceResultPeer> temp = new ArrayList((Collection)peers_for_announce);
/*      */                   
/* 2255 */                   Random rand = new Random();
/*      */                   
/* 2257 */                   for (int i = 0; (i < 5) && (temp.size() > 0); i++)
/*      */                   {
/* 2259 */                     DownloadAnnounceResultPeer peer = (DownloadAnnounceResultPeer)temp.remove(rand.nextInt(temp.size()));
/*      */                     
/* 2261 */                     DHTTrackerPlugin.this.log(download, "Injecting derived peer " + peer.getAddress() + " into " + download.getName());
/*      */                     
/* 2263 */                     Map<Object, Object> user_data = new HashMap();
/*      */                     
/* 2265 */                     user_data.put(Peer.PR_PRIORITY_CONNECTION, Boolean.TRUE);
/*      */                     
/* 2267 */                     pm.addPeer(peer.getAddress(), peer.getPort(), peer.getUDPPort(), peer.getProtocol() == 2, user_data);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2277 */               if ((download_state == 4) || (download_state == 5))
/*      */               {
/*      */ 
/* 2280 */                 final DownloadAnnounceResultPeer[] peers = new DownloadAnnounceResultPeer[((List)peers_for_announce).size()];
/*      */                 
/* 2282 */                 ((List)peers_for_announce).toArray(peers);
/*      */                 
/* 2284 */                 download.setAnnounceResult(new DownloadAnnounceResult()
/*      */                 {
/*      */ 
/*      */                   public Download getDownload()
/*      */                   {
/*      */ 
/* 2290 */                     return DHTTrackerPlugin.13.this.val$download;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public int getResponseType()
/*      */                   {
/* 2296 */                     return 1;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public int getReportedPeerCount()
/*      */                   {
/* 2302 */                     return peers.length;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public int getSeedCount()
/*      */                   {
/* 2308 */                     return DHTTrackerPlugin.13.this.seed_count;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public int getNonSeedCount()
/*      */                   {
/* 2314 */                     return DHTTrackerPlugin.13.this.leecher_count;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public String getError()
/*      */                   {
/* 2320 */                     return null;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public URL getURL()
/*      */                   {
/* 2326 */                     return DHTTrackerPlugin.13.this.val$url_to_report;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public DownloadAnnounceResultPeer[] getPeers()
/*      */                   {
/* 2332 */                     return peers;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public long getTimeToWait()
/*      */                   {
/* 2338 */                     return retry / 1000L;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public Map getExtensions()
/*      */                   {
/* 2344 */                     return null;
/*      */                   }
/*      */                 });
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2357 */               boolean inject_scrape = this.leecher_count > 0;
/*      */               
/* 2359 */               DownloadScrapeResult result = download.getLastScrapeResult();
/*      */               
/* 2361 */               if ((result != null) && (result.getResponseType() != 2))
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
/* 2372 */                 synchronized (DHTTrackerPlugin.this.scrape_injection_map)
/*      */                 {
/* 2374 */                   int[] prev = (int[])DHTTrackerPlugin.this.scrape_injection_map.get(download);
/*      */                   
/* 2376 */                   if ((prev != null) && (prev[0] == result.getSeedCount()) && (prev[1] == result.getNonSeedCount()))
/*      */                   {
/*      */ 
/*      */ 
/* 2380 */                     inject_scrape = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/* 2385 */               if ((this.val$torrent.isDecentralised()) || (inject_scrape))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2391 */                 PeerManager pm = download.getPeerManager();
/*      */                 
/* 2393 */                 int local_seeds = 0;
/* 2394 */                 int local_leechers = 0;
/*      */                 
/* 2396 */                 if (pm != null)
/*      */                 {
/* 2398 */                   Peer[] dl_peers = pm.getPeers();
/*      */                   
/* 2400 */                   for (int i = 0; i < dl_peers.length; i++)
/*      */                   {
/* 2402 */                     Peer dl_peer = dl_peers[i];
/*      */                     
/* 2404 */                     if (dl_peer.getPercentDoneInThousandNotation() == 1000)
/*      */                     {
/* 2406 */                       local_seeds++;
/*      */                     }
/*      */                     else {
/* 2409 */                       local_leechers++;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/* 2414 */                 final int f_adj_seeds = Math.max(this.seed_count, local_seeds);
/* 2415 */                 final int f_adj_leechers = Math.max(this.leecher_count, local_leechers);
/*      */                 
/* 2417 */                 synchronized (DHTTrackerPlugin.this.scrape_injection_map)
/*      */                 {
/* 2419 */                   DHTTrackerPlugin.this.scrape_injection_map.put(download, new int[] { f_adj_seeds, f_adj_leechers });
/*      */                 }
/*      */                 try
/*      */                 {
/* 2423 */                   DHTTrackerPlugin.this.this_mon.enter();
/*      */                   
/* 2425 */                   int[] run_data = (int[])DHTTrackerPlugin.this.running_downloads.get(download);
/*      */                   
/* 2427 */                   if (run_data == null)
/*      */                   {
/* 2429 */                     run_data = (int[])DHTTrackerPlugin.this.run_data_cache.get(download);
/*      */                   }
/*      */                   
/* 2432 */                   if (run_data != null)
/*      */                   {
/* 2434 */                     run_data[1] = f_adj_seeds;
/* 2435 */                     run_data[2] = f_adj_leechers;
/*      */                     
/* 2437 */                     run_data[4] = ((int)(SystemTime.getCurrentTime() / 1000L));
/*      */                   }
/*      */                 }
/*      */                 finally {
/* 2441 */                   DHTTrackerPlugin.this.this_mon.exit();
/*      */                 }
/*      */                 
/* 2444 */                 download.setScrapeResult(new DownloadScrapeResult()
/*      */                 {
/*      */ 
/*      */                   public Download getDownload()
/*      */                   {
/*      */ 
/* 2450 */                     return DHTTrackerPlugin.13.this.val$download;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public int getResponseType()
/*      */                   {
/* 2456 */                     return 1;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public int getSeedCount()
/*      */                   {
/* 2462 */                     return f_adj_seeds;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public int getNonSeedCount()
/*      */                   {
/* 2468 */                     return f_adj_leechers;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public long getScrapeStartTime()
/*      */                   {
/* 2474 */                     return DHTTrackerPlugin.13.this.val$start;
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void setNextScrapeStartTime(long nextScrapeStartTime) {}
/*      */                   
/*      */ 
/*      */ 
/*      */                   public long getNextScrapeStartTime()
/*      */                   {
/* 2486 */                     return SystemTime.getCurrentTime() + retry;
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public String getStatus()
/*      */                   {
/* 2492 */                     return "OK";
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public URL getURL()
/*      */                   {
/* 2498 */                     return DHTTrackerPlugin.13.this.val$url_to_report;
/*      */                   }
/*      */                 });
/*      */               }
/*      */             }
/*      */           });
/*      */         } }
/*      */     }
/* 2506 */     return num_done;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean isComplete(Download download)
/*      */   {
/* 2513 */     if (Constants.DOWNLOAD_SOURCES_PRETEND_COMPLETE)
/*      */     {
/* 2515 */       return true;
/*      */     }
/*      */     
/* 2518 */     boolean is_complete = download.isComplete();
/*      */     
/* 2520 */     if (is_complete)
/*      */     {
/* 2522 */       PeerManager pm = download.getPeerManager();
/*      */       
/* 2524 */       if (pm != null)
/*      */       {
/* 2526 */         PEPeerManager core_pm = PluginCoreUtils.unwrap(pm);
/*      */         
/* 2528 */         if ((core_pm != null) && (core_pm.getHiddenBytes() > 0L))
/*      */         {
/* 2530 */           is_complete = false;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2535 */     return is_complete;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void trackerRemove(final Download download, RegistrationDetails details)
/*      */   {
/* 2543 */     if (this.disable_put)
/*      */     {
/* 2545 */       return;
/*      */     }
/*      */     
/* 2548 */     if (download.getFlag(512L))
/*      */     {
/* 2550 */       return;
/*      */     }
/*      */     
/* 2553 */     final long start = SystemTime.getCurrentTime();
/*      */     
/* 2555 */     trackerTarget[] targets = details.getTargets(true);
/*      */     
/* 2557 */     for (int i = 0; i < targets.length; i++)
/*      */     {
/* 2559 */       final trackerTarget target = targets[i];
/*      */       
/* 2561 */       if (this.dht.hasLocalKey(target.getHash()))
/*      */       {
/* 2563 */         increaseActive(download);
/*      */         
/* 2565 */         this.dht.remove(target.getHash(), "Tracker dereg of '" + download.getName() + "'" + target.getDesc(), new DHTPluginOperationListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public boolean diversified()
/*      */           {
/*      */ 
/*      */ 
/* 2573 */             return true;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void starts(byte[] key) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void complete(byte[] key, boolean timeout_occurred)
/*      */           {
/* 2601 */             if (target.getType() == 2)
/*      */             {
/* 2603 */               DHTTrackerPlugin.this.log(download, "Unregistration of '" + target.getDesc() + "' completed (elapsed=" + TimeFormatter.formatColonMillis(SystemTime.getCurrentTime() - start) + ")");
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 2608 */             DHTTrackerPlugin.this.decreaseActive(download);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void trackerRemove(final Download download, final trackerTarget target)
/*      */   {
/* 2620 */     if (this.disable_put)
/*      */     {
/* 2622 */       return;
/*      */     }
/*      */     
/* 2625 */     if (download.getFlag(512L))
/*      */     {
/* 2627 */       return;
/*      */     }
/*      */     
/* 2630 */     final long start = SystemTime.getCurrentTime();
/*      */     
/* 2632 */     if (this.dht.hasLocalKey(target.getHash()))
/*      */     {
/* 2634 */       increaseActive(download);
/*      */       
/* 2636 */       this.dht.remove(target.getHash(), "Tracker dereg of '" + download.getName() + "'" + target.getDesc(), new DHTPluginOperationListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public boolean diversified()
/*      */         {
/*      */ 
/*      */ 
/* 2644 */           return true;
/*      */         }
/*      */         
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
/*      */ 
/*      */         public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
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
/* 2672 */           if (target.getType() == 2)
/*      */           {
/* 2674 */             DHTTrackerPlugin.this.log(download, "Unregistration of '" + target.getDesc() + "' completed (elapsed=" + TimeFormatter.formatColonMillis(SystemTime.getCurrentTime() - start) + ")");
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 2679 */           DHTTrackerPlugin.this.decreaseActive(download);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void processNonRegistrations()
/*      */   {
/* 2688 */     Download ready_download = null;
/* 2689 */     long ready_download_next_check = -1L;
/*      */     
/* 2691 */     long now = this.plugin_interface.getUtilities().getCurrentSystemTime();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2696 */     List<Download> to_scrape = new ArrayList();
/*      */     try
/*      */     {
/* 2699 */       this.this_mon.enter();
/*      */       
/* 2701 */       Iterator<Download> it = this.interesting_downloads.keySet().iterator();
/*      */       
/* 2703 */       while ((it.hasNext()) && (ready_download == null))
/*      */       {
/* 2705 */         Download download = (Download)it.next();
/*      */         
/* 2707 */         Torrent torrent = download.getTorrent();
/*      */         
/* 2709 */         if (torrent != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2714 */           int[] run_data = (int[])this.running_downloads.get(download);
/*      */           
/* 2716 */           if ((run_data == null) || (run_data[0] == 3))
/*      */           {
/*      */ 
/*      */ 
/* 2720 */             to_scrape.add(download);
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 2725 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 2728 */     Map<Download, DownloadScrapeResult> scrapes = new HashMap();
/*      */     
/* 2730 */     for (int i = 0; i < to_scrape.size(); i++)
/*      */     {
/* 2732 */       Download download = (Download)to_scrape.get(i);
/*      */       
/* 2734 */       scrapes.put(download, download.getLastScrapeResult());
/*      */     }
/*      */     try
/*      */     {
/* 2738 */       this.this_mon.enter();
/*      */       
/* 2740 */       Iterator<Download> it = this.interesting_downloads.keySet().iterator();
/*      */       
/* 2742 */       while ((it.hasNext()) && (ready_download == null))
/*      */       {
/* 2744 */         Download download = (Download)it.next();
/*      */         
/* 2746 */         Torrent torrent = download.getTorrent();
/*      */         
/* 2748 */         if (torrent != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2753 */           int[] run_data = (int[])this.running_downloads.get(download);
/*      */           
/* 2755 */           if ((run_data == null) || (run_data[0] == 3))
/*      */           {
/* 2757 */             boolean force = torrent.wasCreatedByUs();
/*      */             
/* 2759 */             if (!force)
/*      */             {
/* 2761 */               if ((this.interesting_pub_max <= 0) || (this.interesting_published <= this.interesting_pub_max))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 2766 */                 DownloadScrapeResult scrape = (DownloadScrapeResult)scrapes.get(download);
/*      */                 
/* 2768 */                 if ((scrape == null) || 
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2775 */                   (scrape.getSeedCount() + scrape.getNonSeedCount() > 30)) {}
/*      */               }
/*      */               
/*      */             }
/*      */             else
/*      */             {
/* 2781 */               long target = ((Long)this.interesting_downloads.get(download)).longValue();
/*      */               
/* 2783 */               long check_period = TorrentUtils.isDecentralised(torrent.getAnnounceURL()) ? 3600000L : 14400000L;
/*      */               
/* 2785 */               if (target <= now)
/*      */               {
/* 2787 */                 ready_download = download;
/* 2788 */                 ready_download_next_check = now + check_period;
/*      */                 
/* 2790 */                 this.interesting_downloads.put(download, new Long(ready_download_next_check));
/*      */               }
/* 2792 */               else if (target - now > check_period)
/*      */               {
/* 2794 */                 this.interesting_downloads.put(download, new Long(now + target % check_period));
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 2801 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 2804 */     if (ready_download != null)
/*      */     {
/* 2806 */       Download f_ready_download = ready_download;
/*      */       
/* 2808 */       final Torrent torrent = ready_download.getTorrent();
/*      */       
/* 2810 */       if (ready_download.getFlag(512L))
/*      */       {
/*      */         try {
/* 2813 */           this.this_mon.enter();
/*      */           
/* 2815 */           this.interesting_downloads.remove(f_ready_download);
/*      */         }
/*      */         finally
/*      */         {
/* 2819 */           this.this_mon.exit();
/*      */         }
/*      */       }
/* 2822 */       else if (this.dht.isDiversified(torrent.getHash()))
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/* 2827 */           this.this_mon.enter();
/*      */           
/* 2829 */           this.interesting_downloads.remove(f_ready_download);
/*      */         }
/*      */         finally
/*      */         {
/* 2833 */           this.this_mon.exit();
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/* 2839 */         final long start = now;
/* 2840 */         final long f_next_check = ready_download_next_check;
/*      */         
/* 2842 */         this.dht.get(torrent.getHash(), "Presence query for '" + ready_download.getName() + "'", (byte)0, 8, 120000L, false, false, new DHTPluginOperationListener()
/*      */         {
/*      */           private boolean diversified;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2851 */           private int leechers = 0;
/* 2852 */           private int seeds = 0;
/*      */           
/* 2854 */           private int i2p_leechers = 0;
/* 2855 */           private int i2p_seeds = 0;
/*      */           
/*      */ 
/*      */           public boolean diversified()
/*      */           {
/* 2860 */             this.diversified = true;
/*      */             
/* 2862 */             return false;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void starts(byte[] key) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */           {
/* 2876 */             boolean is_leecher = (value.getFlags() & 0x1) == 1;
/*      */             
/* 2878 */             if (is_leecher)
/*      */             {
/* 2880 */               this.leechers += 1;
/*      */             }
/*      */             else
/*      */             {
/* 2884 */               this.seeds += 1;
/*      */             }
/*      */             try
/*      */             {
/* 2888 */               String[] tokens = new String(value.getValue()).split(";");
/*      */               
/* 2890 */               for (int i = 1; i < tokens.length; i++)
/*      */               {
/* 2892 */                 String token = tokens[i].trim();
/*      */                 
/* 2894 */                 if (token.length() > 0)
/*      */                 {
/* 2896 */                   if (!Character.isDigit(token.charAt(0)))
/*      */                   {
/* 2898 */                     String flag_str = token;
/*      */                     
/* 2900 */                     if (flag_str.contains("I"))
/*      */                     {
/* 2902 */                       if (is_leecher)
/*      */                       {
/* 2904 */                         this.i2p_leechers += 1;
/*      */                       }
/*      */                       else
/*      */                       {
/* 2908 */                         this.i2p_seeds += 1;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void complete(byte[] key, boolean timeout_occurred)
/*      */           {
/* 2934 */             int total = this.leechers + this.seeds;
/*      */             
/* 2936 */             DHTTrackerPlugin.this.log(torrent, "Presence query: availability=" + (total == 8 ? "8+" : new StringBuilder().append(total).append("").toString()) + ",div=" + this.diversified + " (elapsed=" + TimeFormatter.formatColonMillis(SystemTime.getCurrentTime() - start) + ")");
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2941 */             if (this.diversified)
/*      */             {
/*      */               try {
/* 2944 */                 DHTTrackerPlugin.this.this_mon.enter();
/*      */                 
/* 2946 */                 DHTTrackerPlugin.this.interesting_downloads.remove(f_next_check);
/*      */               }
/*      */               finally
/*      */               {
/* 2950 */                 DHTTrackerPlugin.this.this_mon.exit();
/*      */               }
/*      */             }
/* 2953 */             else if (total < 8)
/*      */             {
/*      */ 
/*      */               try
/*      */               {
/*      */ 
/* 2959 */                 DHTTrackerPlugin.this.this_mon.enter();
/*      */                 
/* 2961 */                 DHTTrackerPlugin.this.interesting_downloads.remove(f_next_check);
/*      */               }
/*      */               finally
/*      */               {
/* 2965 */                 DHTTrackerPlugin.this.this_mon.exit();
/*      */               }
/*      */               
/* 2968 */               DHTTrackerPlugin.access$2108(DHTTrackerPlugin.this);
/*      */               
/* 2970 */               if (!DHTTrackerPlugin.this.disable_put)
/*      */               {
/* 2972 */                 DHTTrackerPlugin.this.dht.put(torrent.getHash(), "Presence store '" + f_next_check.getName() + "'", "0".getBytes(), (byte)0, new DHTPluginOperationListener()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */                   public boolean diversified()
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 2982 */                     return true;
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void starts(byte[] key) {}
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void complete(byte[] key, boolean timeout_occurred) {}
/*      */                 });
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/* 3017 */               DHTTrackerPlugin.this.this_mon.enter();
/*      */               
/* 3019 */               int[] run_data = (int[])DHTTrackerPlugin.this.running_downloads.get(f_next_check);
/*      */               
/* 3021 */               if (run_data == null)
/*      */               {
/* 3023 */                 run_data = (int[])DHTTrackerPlugin.this.run_data_cache.get(f_next_check);
/*      */               }
/*      */               
/* 3026 */               if (run_data != null)
/*      */               {
/* 3028 */                 if (total < 8)
/*      */                 {
/* 3030 */                   run_data[1] = this.seeds;
/* 3031 */                   run_data[2] = this.leechers;
/* 3032 */                   run_data[3] = total;
/*      */                 }
/*      */                 else
/*      */                 {
/* 3036 */                   run_data[1] = Math.max(run_data[1], this.seeds);
/* 3037 */                   run_data[2] = Math.max(run_data[2], this.leechers);
/*      */                 }
/*      */                 
/* 3040 */                 run_data[4] = ((int)(SystemTime.getCurrentTime() / 1000L));
/*      */               }
/*      */             }
/*      */             finally {
/* 3044 */               DHTTrackerPlugin.this.this_mon.exit();
/*      */             }
/*      */             
/* 3047 */             if (this.i2p_seeds + this.i2p_leechers > 0)
/*      */             {
/* 3049 */               int[] details = (int[])f_next_check.getUserData(DHTTrackerPlugin.DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY);
/*      */               
/* 3051 */               if (details == null)
/*      */               {
/* 3053 */                 details = new int[] { this.i2p_seeds, this.i2p_leechers };
/*      */                 
/* 3055 */                 f_next_check.setUserData(DHTTrackerPlugin.DOWNLOAD_USER_DATA_I2P_SCRAPE_KEY, details);
/*      */               }
/*      */               else
/*      */               {
/* 3059 */                 details[0] = Math.max(details[0], this.i2p_seeds);
/* 3060 */                 details[1] = Math.max(details[1], this.i2p_leechers);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/* 3065 */             f_next_check.setScrapeResult(new DownloadScrapeResult()
/*      */             {
/*      */ 
/*      */               public Download getDownload()
/*      */               {
/*      */ 
/* 3071 */                 return null;
/*      */               }
/*      */               
/*      */ 
/*      */               public int getResponseType()
/*      */               {
/* 3077 */                 return 1;
/*      */               }
/*      */               
/*      */ 
/*      */               public int getSeedCount()
/*      */               {
/* 3083 */                 return DHTTrackerPlugin.16.this.seeds;
/*      */               }
/*      */               
/*      */ 
/*      */               public int getNonSeedCount()
/*      */               {
/* 3089 */                 return DHTTrackerPlugin.16.this.leechers;
/*      */               }
/*      */               
/*      */ 
/*      */               public long getScrapeStartTime()
/*      */               {
/* 3095 */                 return SystemTime.getCurrentTime();
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public void setNextScrapeStartTime(long nextScrapeStartTime) {}
/*      */               
/*      */ 
/*      */ 
/*      */               public long getNextScrapeStartTime()
/*      */               {
/* 3107 */                 return DHTTrackerPlugin.16.this.val$f_next_check;
/*      */               }
/*      */               
/*      */ 
/*      */               public String getStatus()
/*      */               {
/* 3113 */                 return "OK";
/*      */               }
/*      */               
/*      */ 
/*      */               public URL getURL()
/*      */               {
/* 3119 */                 URL url_to_report = DHTTrackerPlugin.16.this.val$torrent.isDecentralised() ? DHTTrackerPlugin.16.this.val$torrent.getAnnounceURL() : DHTTrackerPlugin.DEFAULT_URL;
/*      */                 
/* 3121 */                 return url_to_report;
/*      */               }
/*      */             });
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void stateChanged(Download download, int old_state, int new_state)
/*      */   {
/* 3137 */     int state = download.getState();
/*      */     try
/*      */     {
/* 3140 */       this.this_mon.enter();
/*      */       
/* 3142 */       if ((state == 4) || (state == 5) || (state == 9))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 3147 */         if (this.running_downloads.containsKey(download))
/*      */         {
/*      */ 
/*      */ 
/* 3151 */           this.query_map.put(download, new Long(SystemTime.getCurrentTime()));
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 3156 */       this.this_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3161 */     if (!download.isPaused())
/*      */     {
/* 3163 */       checkDownloadForRegistration(download, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void announceAll()
/*      */   {
/* 3170 */     this.log.log("Announce-all requested");
/*      */     
/* 3172 */     Long now = new Long(SystemTime.getCurrentTime());
/*      */     try
/*      */     {
/* 3175 */       this.this_mon.enter();
/*      */       
/* 3177 */       Iterator<Map.Entry<Download, Long>> it = this.query_map.entrySet().iterator();
/*      */       
/* 3179 */       while (it.hasNext())
/*      */       {
/* 3181 */         Map.Entry<Download, Long> entry = (Map.Entry)it.next();
/*      */         
/* 3183 */         entry.setValue(now);
/*      */       }
/*      */     }
/*      */     finally {
/* 3187 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void announce(Download download)
/*      */   {
/* 3195 */     this.log.log("Announce requested for " + download.getName());
/*      */     try
/*      */     {
/* 3198 */       this.this_mon.enter();
/*      */       
/* 3200 */       this.query_map.put(download, Long.valueOf(SystemTime.getCurrentTime()));
/*      */     }
/*      */     finally
/*      */     {
/* 3204 */       this.this_mon.exit();
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
/*      */   protected void configChanged()
/*      */   {
/* 3219 */     Download[] downloads = this.plugin_interface.getDownloadManager().getDownloads();
/*      */     
/* 3221 */     for (int i = 0; i < downloads.length; i++)
/*      */     {
/* 3223 */       checkDownloadForRegistration(downloads[i], false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DownloadScrapeResult scrape(byte[] hash)
/*      */   {
/* 3235 */     final int[] seeds = { 0 };
/* 3236 */     final int[] leechers = { 0 };
/*      */     
/* 3238 */     final AESemaphore sem = new AESemaphore("DHTTrackerPlugin:scrape");
/*      */     
/* 3240 */     this.dht.get(hash, "Scrape for " + ByteFormatter.encodeString(hash).substring(0, 16), (byte)1, 30, 30000L, false, false, new DHTPluginOperationListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean diversified()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 3251 */         return true;
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
/* 3265 */         if ((value.getFlags() & 0x1) == 1)
/*      */         {
/* 3267 */           leechers[0] += 1;
/*      */         }
/*      */         else
/*      */         {
/* 3271 */           seeds[0] += 1;
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
/*      */       public void complete(byte[] key, boolean timeout_occurred)
/*      */       {
/* 3287 */         sem.release();
/*      */       }
/*      */       
/* 3290 */     });
/* 3291 */     sem.reserve();
/*      */     
/* 3293 */     new DownloadScrapeResult()
/*      */     {
/*      */ 
/*      */       public Download getDownload()
/*      */       {
/*      */ 
/* 3299 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getResponseType()
/*      */       {
/* 3305 */         return 1;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getSeedCount()
/*      */       {
/* 3311 */         return seeds[0];
/*      */       }
/*      */       
/*      */ 
/*      */       public int getNonSeedCount()
/*      */       {
/* 3317 */         return leechers[0];
/*      */       }
/*      */       
/*      */ 
/*      */       public long getScrapeStartTime()
/*      */       {
/* 3323 */         return 0L;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void setNextScrapeStartTime(long nextScrapeStartTime) {}
/*      */       
/*      */ 
/*      */ 
/*      */       public long getNextScrapeStartTime()
/*      */       {
/* 3335 */         return 0L;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getStatus()
/*      */       {
/* 3341 */         return "OK";
/*      */       }
/*      */       
/*      */ 
/*      */       public URL getURL()
/*      */       {
/* 3347 */         return null;
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */   protected void increaseActive(Download dl)
/*      */   {
/*      */     try
/*      */     {
/* 3357 */       this.this_mon.enter();
/*      */       
/* 3359 */       Integer active_i = (Integer)this.in_progress.get(dl);
/*      */       
/* 3361 */       int active = active_i == null ? 0 : active_i.intValue();
/*      */       
/* 3363 */       this.in_progress.put(dl, new Integer(active + 1));
/*      */     }
/*      */     finally
/*      */     {
/* 3367 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void decreaseActive(Download dl)
/*      */   {
/*      */     try
/*      */     {
/* 3376 */       this.this_mon.enter();
/*      */       
/* 3378 */       Integer active_i = (Integer)this.in_progress.get(dl);
/*      */       
/* 3380 */       if (active_i == null)
/*      */       {
/* 3382 */         Debug.out("active count inconsistent");
/*      */       }
/*      */       else
/*      */       {
/* 3386 */         int active = active_i.intValue() - 1;
/*      */         
/* 3388 */         if (active == 0)
/*      */         {
/* 3390 */           this.in_progress.remove(dl);
/*      */         }
/*      */         else
/*      */         {
/* 3394 */           this.in_progress.put(dl, new Integer(active));
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 3399 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isActive(Download dl)
/*      */   {
/*      */     try
/*      */     {
/* 3408 */       this.this_mon.enter();
/*      */       
/* 3410 */       return this.in_progress.get(dl) != null;
/*      */     }
/*      */     finally
/*      */     {
/* 3414 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected class RegistrationDetails
/*      */   {
/*      */     private DHTTrackerPlugin.putDetails put_details;
/*      */     
/*      */ 
/*      */     private byte flags;
/*      */     
/*      */ 
/*      */     private DHTTrackerPlugin.trackerTarget[] put_targets;
/*      */     
/*      */ 
/*      */     private List<DHTTrackerPlugin.trackerTarget> not_put_targets;
/*      */     
/*      */ 
/*      */ 
/*      */     protected RegistrationDetails(Download _download, int _reg_type, DHTTrackerPlugin.putDetails _put_details, byte _flags)
/*      */     {
/* 3438 */       this.put_details = _put_details;
/* 3439 */       this.flags = _flags;
/*      */       
/* 3441 */       getTrackerTargets(_download, _reg_type);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void update(DHTTrackerPlugin.putDetails _put_details, byte _flags)
/*      */     {
/* 3449 */       this.put_details = _put_details;
/* 3450 */       this.flags = _flags;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean updateTargets(Download _download, int _reg_type)
/*      */     {
/* 3458 */       DHTTrackerPlugin.trackerTarget[] old_put_targets = this.put_targets;
/*      */       
/* 3460 */       getTrackerTargets(_download, _reg_type);
/*      */       
/*      */ 
/*      */ 
/* 3464 */       for (int i = 0; i < old_put_targets.length; i++)
/*      */       {
/* 3466 */         boolean found = false;
/*      */         
/* 3468 */         byte[] old_hash = old_put_targets[i].getHash();
/*      */         
/* 3470 */         for (int j = 0; j < this.put_targets.length; j++)
/*      */         {
/* 3472 */           if (Arrays.equals(this.put_targets[j].getHash(), old_hash))
/*      */           {
/* 3474 */             found = true;
/*      */             
/* 3476 */             break;
/*      */           }
/*      */         }
/*      */         
/* 3480 */         if (!found)
/*      */         {
/* 3482 */           DHTTrackerPlugin.this.trackerRemove(_download, old_put_targets[i]);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3488 */       boolean changed = false;
/*      */       
/* 3490 */       for (int i = 0; i < this.put_targets.length; i++)
/*      */       {
/* 3492 */         byte[] new_hash = this.put_targets[i].getHash();
/*      */         
/* 3494 */         boolean found = false;
/*      */         
/* 3496 */         for (int j = 0; j < old_put_targets.length; j++)
/*      */         {
/* 3498 */           if (Arrays.equals(old_put_targets[j].getHash(), new_hash))
/*      */           {
/* 3500 */             found = true;
/*      */             
/* 3502 */             break;
/*      */           }
/*      */         }
/*      */         
/* 3506 */         if (!found)
/*      */         {
/* 3508 */           changed = true;
/*      */         }
/*      */       }
/*      */       
/* 3512 */       return changed;
/*      */     }
/*      */     
/*      */ 
/*      */     protected DHTTrackerPlugin.putDetails getPutDetails()
/*      */     {
/* 3518 */       return this.put_details;
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte getFlags()
/*      */     {
/* 3524 */       return this.flags;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected DHTTrackerPlugin.trackerTarget[] getTargets(boolean for_put)
/*      */     {
/* 3531 */       if ((for_put) || (this.not_put_targets == null))
/*      */       {
/* 3533 */         return this.put_targets;
/*      */       }
/*      */       
/*      */ 
/* 3537 */       List<DHTTrackerPlugin.trackerTarget> result = new ArrayList(Arrays.asList(this.put_targets));
/*      */       
/* 3539 */       for (int i = 0; (i < this.not_put_targets.size()) && (i < 2); i++)
/*      */       {
/* 3541 */         DHTTrackerPlugin.trackerTarget target = (DHTTrackerPlugin.trackerTarget)this.not_put_targets.remove(0);
/*      */         
/* 3543 */         this.not_put_targets.add(target);
/*      */         
/*      */ 
/*      */ 
/* 3547 */         result.add(target);
/*      */       }
/*      */       
/* 3550 */       return (DHTTrackerPlugin.trackerTarget[])result.toArray(new DHTTrackerPlugin.trackerTarget[result.size()]);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void getTrackerTargets(Download download, int type)
/*      */     {
/* 3559 */       byte[] torrent_hash = download.getTorrent().getHash();
/*      */       
/* 3561 */       List<DHTTrackerPlugin.trackerTarget> result = new ArrayList();
/*      */       
/* 3563 */       if (type == 2)
/*      */       {
/* 3565 */         result.add(new DHTTrackerPlugin.trackerTarget(torrent_hash, 2, ""));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3696 */       this.put_targets = ((DHTTrackerPlugin.trackerTarget[])result.toArray(new DHTTrackerPlugin.trackerTarget[result.size()]));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private TorrentAttribute ta_networks;
/*      */   
/*      */ 
/*      */   private TorrentAttribute ta_peer_sources;
/*      */   
/*      */ 
/*      */   private Map<Download, Long> interesting_downloads;
/*      */   
/*      */ 
/*      */   private int interesting_published;
/*      */   
/*      */ 
/*      */   private int interesting_pub_max;
/*      */   
/*      */ 
/*      */   private Map<Download, int[]> running_downloads;
/*      */   
/*      */   private Map<Download, int[]> run_data_cache;
/*      */   
/*      */   private Map<Download, RegistrationDetails> registered_downloads;
/*      */   
/*      */   private void log(Download download, String str)
/*      */   {
/* 3725 */     log(download.getTorrent(), str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void log(Torrent torrent, String str)
/*      */   {
/* 3733 */     this.log.log(torrent, 1, str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TrackerPeerSource getTrackerPeerSource(final Download download)
/*      */   {
/* 3740 */     new TrackerPeerSourceAdapter()
/*      */     {
/*      */       private long last_fixup;
/*      */       
/*      */       private boolean updating;
/* 3745 */       private int status = 0;
/* 3746 */       private long next_time = -1L;
/*      */       
/*      */       private int[] run_data;
/*      */       
/*      */       private void fixup()
/*      */       {
/* 3752 */         long now = SystemTime.getMonotonousTime();
/*      */         
/* 3754 */         if (now - this.last_fixup > 5000L)
/*      */         {
/*      */           try {
/* 3757 */             DHTTrackerPlugin.this.this_mon.enter();
/*      */             
/* 3759 */             this.updating = false;
/* 3760 */             this.next_time = -1L;
/*      */             
/* 3762 */             this.run_data = ((int[])DHTTrackerPlugin.this.running_downloads.get(download));
/*      */             
/* 3764 */             if (this.run_data != null)
/*      */             {
/* 3766 */               if (DHTTrackerPlugin.this.in_progress.containsKey(download))
/*      */               {
/* 3768 */                 this.updating = true;
/*      */               }
/*      */               
/* 3771 */               this.status = (DHTTrackerPlugin.this.initialised_sem.isReleasedForever() ? 5 : 2);
/*      */               
/* 3773 */               Long l_next_time = (Long)DHTTrackerPlugin.this.query_map.get(download);
/*      */               
/* 3775 */               if (l_next_time != null)
/*      */               {
/* 3777 */                 this.next_time = l_next_time.longValue();
/*      */               }
/* 3779 */             } else if (DHTTrackerPlugin.this.interesting_downloads.containsKey(download))
/*      */             {
/* 3781 */               this.status = 2;
/*      */             }
/*      */             else
/*      */             {
/* 3785 */               int dl_state = download.getState();
/*      */               
/* 3787 */               if ((dl_state == 4) || (dl_state == 5) || (dl_state == 9))
/*      */               {
/*      */ 
/*      */ 
/* 3791 */                 this.status = 1;
/*      */               }
/*      */               else
/*      */               {
/* 3795 */                 this.status = 2;
/*      */               }
/*      */             }
/*      */             
/* 3799 */             if (this.run_data == null)
/*      */             {
/* 3801 */               this.run_data = ((int[])DHTTrackerPlugin.this.run_data_cache.get(download));
/*      */             }
/*      */           }
/*      */           finally {
/* 3805 */             DHTTrackerPlugin.this.this_mon.exit();
/*      */           }
/*      */           
/* 3808 */           String[] sources = download.getListAttribute(DHTTrackerPlugin.this.ta_peer_sources);
/*      */           
/* 3810 */           boolean ok = false;
/*      */           
/* 3812 */           if (sources != null)
/*      */           {
/* 3814 */             for (int i = 0; i < sources.length; i++)
/*      */             {
/* 3816 */               if (sources[i].equalsIgnoreCase("DHT"))
/*      */               {
/* 3818 */                 ok = true;
/*      */                 
/* 3820 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 3825 */           if (!ok)
/*      */           {
/* 3827 */             this.status = 1;
/*      */           }
/*      */           
/* 3830 */           this.last_fixup = now;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public int getType()
/*      */       {
/* 3837 */         return 3;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getName()
/*      */       {
/* 3843 */         return "DHT: " + DHTTrackerPlugin.this.model.getStatus().getText();
/*      */       }
/*      */       
/*      */ 
/*      */       public int getStatus()
/*      */       {
/* 3849 */         fixup();
/*      */         
/* 3851 */         return this.status;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getSeedCount()
/*      */       {
/* 3857 */         fixup();
/*      */         
/* 3859 */         if (this.run_data == null)
/*      */         {
/* 3861 */           return -1;
/*      */         }
/*      */         
/* 3864 */         return this.run_data[1];
/*      */       }
/*      */       
/*      */ 
/*      */       public int getLeecherCount()
/*      */       {
/* 3870 */         fixup();
/*      */         
/* 3872 */         if (this.run_data == null)
/*      */         {
/* 3874 */           return -1;
/*      */         }
/*      */         
/* 3877 */         return this.run_data[2];
/*      */       }
/*      */       
/*      */ 
/*      */       public int getPeers()
/*      */       {
/* 3883 */         fixup();
/*      */         
/* 3885 */         if (this.run_data == null)
/*      */         {
/* 3887 */           return -1;
/*      */         }
/*      */         
/* 3890 */         return this.run_data[3];
/*      */       }
/*      */       
/*      */ 
/*      */       public int getLastUpdate()
/*      */       {
/* 3896 */         fixup();
/*      */         
/* 3898 */         if (this.run_data == null)
/*      */         {
/* 3900 */           return 0;
/*      */         }
/*      */         
/* 3903 */         return this.run_data[4];
/*      */       }
/*      */       
/*      */ 
/*      */       public int getSecondsToUpdate()
/*      */       {
/* 3909 */         fixup();
/*      */         
/* 3911 */         if (this.next_time < 0L)
/*      */         {
/* 3913 */           return -1;
/*      */         }
/*      */         
/* 3916 */         return (int)((this.next_time - SystemTime.getCurrentTime()) / 1000L);
/*      */       }
/*      */       
/*      */ 
/*      */       public int getInterval()
/*      */       {
/* 3922 */         fixup();
/*      */         
/* 3924 */         if (this.run_data == null)
/*      */         {
/* 3926 */           return -1;
/*      */         }
/*      */         
/* 3929 */         return (int)(DHTTrackerPlugin.this.current_announce_interval / 1000L);
/*      */       }
/*      */       
/*      */ 
/*      */       public int getMinInterval()
/*      */       {
/* 3935 */         fixup();
/*      */         
/* 3937 */         if (this.run_data == null)
/*      */         {
/* 3939 */           return -1;
/*      */         }
/*      */         
/* 3942 */         return 120;
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean isUpdating()
/*      */       {
/* 3948 */         return this.updating;
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean canManuallyUpdate()
/*      */       {
/* 3954 */         fixup();
/*      */         
/* 3956 */         return this.run_data != null;
/*      */       }
/*      */       
/*      */ 
/*      */       public void manualUpdate()
/*      */       {
/* 3962 */         DHTTrackerPlugin.this.announce(download);
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TrackerPeerSource[] getTrackerPeerSources(final Torrent torrent)
/*      */   {
/* 3972 */     TrackerPeerSource vuze_dht = new TrackerPeerSourceAdapter()
/*      */     {
/*      */       private volatile boolean query_done;
/*      */       
/* 3976 */       private volatile int status = 9;
/*      */       
/* 3978 */       private volatile int seeds = 0;
/* 3979 */       private volatile int leechers = 0;
/*      */       
/*      */ 
/*      */ 
/*      */       private void fixup()
/*      */       {
/* 3985 */         if (DHTTrackerPlugin.this.initialised_sem.isReleasedForever())
/*      */         {
/* 3987 */           synchronized (this)
/*      */           {
/* 3989 */             if (this.query_done)
/*      */             {
/* 3991 */               return;
/*      */             }
/*      */             
/* 3994 */             this.query_done = true;
/*      */             
/* 3996 */             this.status = 4;
/*      */           }
/*      */           
/* 3999 */           DHTTrackerPlugin.this.dht.get(torrent.getHash(), "Availability lookup for '" + torrent.getName() + "'", (byte)1, 30, 60000L, false, true, new DHTPluginOperationListener()
/*      */           {
/*      */             public void starts(byte[] key) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public boolean diversified()
/*      */             {
/* 4016 */               return true;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */             {
/* 4024 */               if ((value.getFlags() & 0x1) == 1)
/*      */               {
/* 4026 */                 DHTTrackerPlugin.20.access$2808(DHTTrackerPlugin.20.this);
/*      */               }
/*      */               else
/*      */               {
/* 4030 */                 DHTTrackerPlugin.20.access$2908(DHTTrackerPlugin.20.this);
/*      */               }
/*      */             }
/*      */             
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
/* 4047 */               DHTTrackerPlugin.20.this.status = 5;
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public int getType()
/*      */       {
/* 4056 */         return 3;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getName()
/*      */       {
/* 4062 */         return "Vuze DHT";
/*      */       }
/*      */       
/*      */ 
/*      */       public int getStatus()
/*      */       {
/* 4068 */         fixup();
/*      */         
/* 4070 */         return this.status;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getSeedCount()
/*      */       {
/* 4076 */         fixup();
/*      */         
/* 4078 */         int result = this.seeds;
/*      */         
/* 4080 */         if ((result == 0) && (this.status != 5))
/*      */         {
/* 4082 */           return -1;
/*      */         }
/*      */         
/* 4085 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getLeecherCount()
/*      */       {
/* 4091 */         fixup();
/*      */         
/* 4093 */         int result = this.leechers;
/*      */         
/* 4095 */         if ((result == 0) && (this.status != 5))
/*      */         {
/* 4097 */           return -1;
/*      */         }
/*      */         
/* 4100 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getPeers()
/*      */       {
/* 4106 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean isUpdating()
/*      */       {
/* 4112 */         return this.status == 4;
/*      */       }
/*      */     };
/*      */     
/*      */ 
/* 4117 */     if (this.alt_lookup_handler != null)
/*      */     {
/* 4119 */       TrackerPeerSource alt_dht = new TrackerPeerSourceAdapter()
/*      */       {
/*      */         private volatile int status;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         private volatile int peers;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public int getType()
/*      */         {
/* 4156 */           return 3;
/*      */         }
/*      */         
/*      */ 
/*      */         public String getName()
/*      */         {
/* 4162 */           return "Mainline DHT";
/*      */         }
/*      */         
/*      */ 
/*      */         public int getStatus()
/*      */         {
/* 4168 */           return this.status;
/*      */         }
/*      */         
/*      */ 
/*      */         public int getPeers()
/*      */         {
/* 4174 */           int result = this.peers;
/*      */           
/* 4176 */           if ((result == 0) && (this.status != 5))
/*      */           {
/* 4178 */             return -1;
/*      */           }
/*      */           
/* 4181 */           return result;
/*      */         }
/*      */         
/*      */ 
/*      */         public boolean isUpdating()
/*      */         {
/* 4187 */           return this.status == 4;
/*      */         }
/*      */         
/*      */ 
/* 4191 */       };
/* 4192 */       return new TrackerPeerSource[] { vuze_dht, alt_dht };
/*      */     }
/*      */     
/*      */ 
/* 4196 */     return new TrackerPeerSource[] { vuze_dht };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map<Download, Boolean> limited_online_tracking;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map<Download, Long> query_map;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map<Download, Integer> in_progress;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean track_only_decentralsed;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private BooleanParameter track_normal_when_offline;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private BooleanParameter track_limited_when_online;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long current_announce_interval;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private LoggerChannel log;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map<Download, int[]> scrape_injection_map;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Random random;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean is_running;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private AEMonitor this_mon;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private AESemaphore initialised_sem;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private DHTTrackerPluginAlt alt_lookup_handler;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean disable_put;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void positionChanged(Download download, int oldPosition, int newPosition) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static class putDetails
/*      */   {
/*      */     private String encoded;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private String ip_override;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private int tcp_port;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private int udp_port;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean i2p;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private putDetails(String _encoded, String _ip, int _tcp_port, int _udp_port, boolean _i2p)
/*      */     {
/* 4323 */       this.encoded = _encoded;
/* 4324 */       this.ip_override = _ip;
/* 4325 */       this.tcp_port = _tcp_port;
/* 4326 */       this.udp_port = _udp_port;
/* 4327 */       this.i2p = _i2p;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getEncoded()
/*      */     {
/* 4333 */       return this.encoded;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getIPOverride()
/*      */     {
/* 4339 */       return this.ip_override;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getTCPPort()
/*      */     {
/* 4345 */       return this.tcp_port;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getUDPPort()
/*      */     {
/* 4351 */       return this.udp_port;
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean hasI2P()
/*      */     {
/* 4357 */       return this.i2p;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean sameAs(putDetails other)
/*      */     {
/* 4364 */       return getEncoded().equals(other.getEncoded());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static class trackerTarget
/*      */   {
/*      */     private String desc;
/*      */     
/*      */     private byte[] hash;
/*      */     
/*      */     private int type;
/*      */     
/*      */ 
/*      */     protected trackerTarget(byte[] _hash, int _type, String _desc)
/*      */     {
/* 4381 */       this.hash = _hash;
/* 4382 */       this.type = _type;
/* 4383 */       this.desc = _desc;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getType()
/*      */     {
/* 4389 */       return this.type;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getHash()
/*      */     {
/* 4395 */       return this.hash;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getDesc()
/*      */     {
/* 4401 */       if (this.type != 2)
/*      */       {
/* 4403 */         return "(" + this.desc + ")";
/*      */       }
/*      */       
/* 4406 */       return "";
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static class TriangleSlicer
/*      */   {
/*      */     int width;
/*      */     
/*      */     private double w;
/*      */     private double w2;
/*      */     private double h;
/*      */     private double tan60;
/*      */     
/*      */     public TriangleSlicer(int width)
/*      */     {
/* 4422 */       this.width = width;
/*      */       
/* 4424 */       this.w = width;
/* 4425 */       this.w2 = (this.w / 2.0D);
/* 4426 */       this.h = (Math.cos(0.5235987755982988D) * this.w);
/*      */       
/* 4428 */       this.tan60 = Math.tan(1.0471975511965976D);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int[] findVertices(double x, double y)
/*      */     {
/* 4440 */       int yN = (int)Math.floor(y / this.h);
/* 4441 */       int xN = (int)Math.floor(x / this.w2);
/*      */       
/*      */       double v1y;
/*      */       
/*      */       boolean upTriangle;
/*      */       double v1x;
/*      */       double v1y;
/* 4448 */       if ((xN + yN) % 2 == 0) {
/*      */         double v1y;
/* 4450 */         if (y - this.h * yN > (x - this.w2 * xN) * this.tan60)
/*      */         {
/* 4452 */           boolean upTriangle = false;
/* 4453 */           double v1x = this.w2 * (xN - 1);
/* 4454 */           v1y = this.h * (yN + 1);
/*      */         }
/*      */         else {
/* 4457 */           boolean upTriangle = true;
/* 4458 */           double v1x = this.w2 * xN;
/* 4459 */           v1y = this.h * yN;
/*      */         }
/*      */       } else {
/*      */         double v1y;
/* 4463 */         if (y - this.h * yN > (this.w2 - (x - this.w2 * xN)) * this.tan60)
/*      */         {
/* 4465 */           boolean upTriangle = false;
/* 4466 */           double v1x = this.w2 * xN;
/* 4467 */           v1y = this.h * (yN + 1);
/*      */         }
/*      */         else {
/* 4470 */           upTriangle = true;
/* 4471 */           v1x = this.w2 * (xN - 1);
/* 4472 */           v1y = this.h * yN; } }
/*      */       double v3y;
/*      */       double v2x;
/*      */       double v2y;
/* 4476 */       double v3x; double v3y; if (upTriangle) {
/* 4477 */         double v2x = v1x + this.w;
/* 4478 */         double v2y = v1y;
/*      */         
/* 4480 */         double v3x = v1x + this.w2;
/* 4481 */         v3y = v1y + this.h;
/*      */       } else {
/* 4483 */         v2x = v1x + this.w;
/* 4484 */         v2y = v1y;
/*      */         
/* 4486 */         v3x = v1x + this.w2;
/* 4487 */         v3y = v1y - this.h;
/*      */       }
/*      */       
/* 4490 */       int[] result = new int[6];
/*      */       
/* 4492 */       result[0] = ((int)v1x);
/* 4493 */       result[1] = ((int)v1y);
/*      */       
/* 4495 */       result[2] = ((int)v2x);
/* 4496 */       result[3] = ((int)v2y);
/*      */       
/* 4498 */       result[4] = ((int)v3x);
/* 4499 */       result[5] = ((int)v3y);
/*      */       
/* 4501 */       return result;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/tracker/dht/DHTTrackerPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */