/*      */ package com.aelitis.azureus.plugins.net.buddy;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.security.CryptoHandler;
/*      */ import com.aelitis.azureus.core.security.CryptoManager;
/*      */ import com.aelitis.azureus.core.security.CryptoManagerFactory;
/*      */ import com.aelitis.azureus.core.security.CryptoManagerPasswordException;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import com.aelitis.azureus.plugins.magnet.MagnetPlugin;
/*      */ import com.aelitis.azureus.plugins.magnet.MagnetPluginProgressListener;
/*      */ import com.aelitis.azureus.plugins.net.buddy.tracker.BuddyPluginTracker;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.protocol.azplug.AZPluginConnection;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseContact;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseEvent;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseKey;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseListener;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseValue;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadException;
/*      */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCException;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageConnection;
/*      */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageRegistration;
/*      */ import org.gudy.azureus2.plugins.network.ConnectionManager;
/*      */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.IntParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.StringListParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.StringParameter;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*      */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*      */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.security.SEPublicKey;
/*      */ import org.gudy.azureus2.plugins.utils.security.SESecurityManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ public class BuddyPlugin implements org.gudy.azureus2.plugins.Plugin
/*      */ {
/*      */   public static final boolean SUPPORT_ONLINE_STATUS = true;
/*      */   public static final int VERSION_INITIAL = 1;
/*      */   public static final int VERSION_CHAT = 2;
/*      */   public static final int VERSION_CURRENT = 2;
/*      */   public static final int MT_V3_CHAT = 1;
/*      */   private static final int FEED_UPDATE_MIN_MILLIS = 21600000;
/*      */   public static final int MAX_MESSAGE_SIZE = 4194304;
/*      */   public static final int SUBSYSTEM_INTERNAL = 0;
/*      */   public static final int SUBSYSTEM_AZ2 = 1;
/*      */   public static final int SUBSYSTEM_AZ3 = 2;
/*      */   protected static final int SUBSYSTEM_MSG_TYPE_BASE = 1024;
/*      */   public static final int STATUS_ONLINE = 0;
/*      */   public static final int STATUS_AWAY = 1;
/*      */   public static final int STATUS_NOT_AVAILABLE = 2;
/*      */   public static final int STATUS_BUSY = 3;
/*      */   public static final int STATUS_APPEAR_OFFLINE = 4;
/*  108 */   public static final String[] STATUS_VALUES = { "0", "1", "2", "3", "4" };
/*      */   
/*  110 */   public static final String[] STATUS_KEYS = { "os_online", "os_away", "os_not_avail", "os_busy", "os_offline" };
/*      */   
/*      */ 
/*      */ 
/*  114 */   public static final String[] STATUS_STRINGS = new String[STATUS_KEYS.length];
/*      */   
/*      */   protected static final int RT_INTERNAL_REQUEST_PING = 1;
/*      */   
/*      */   protected static final int RT_INTERNAL_REPLY_PING = 2;
/*      */   
/*      */   protected static final int RT_INTERNAL_REQUEST_CLOSE = 3;
/*      */   
/*      */   protected static final int RT_INTERNAL_REPLY_CLOSE = 4;
/*      */   
/*      */   protected static final int RT_INTERNAL_FRAGMENT = 5;
/*      */   
/*      */   protected static final boolean TRACE = false;
/*      */   
/*      */   private static final String VIEW_ID = "azbuddy";
/*      */   
/*      */   private static final int INIT_UNKNOWN = 0;
/*      */   private static final int INIT_OK = 1;
/*      */   private static final int INIT_BAD = 2;
/*      */   private static final int MAX_UNAUTH_BUDDIES = 16;
/*      */   public static final int TIMER_PERIOD = 10000;
/*      */   private static final int BUDDY_STATUS_CHECK_PERIOD_MIN = 180000;
/*      */   private static final int BUDDY_STATUS_CHECK_PERIOD_INC = 60000;
/*      */   protected static final int STATUS_REPUBLISH_PERIOD = 600000;
/*      */   private static final int STATUS_REPUBLISH_TICKS = 60;
/*      */   private static final int CHECK_YGM_PERIOD = 300000;
/*      */   private static final int CHECK_YGM_TICKS = 30;
/*      */   private static final int YGM_BLOOM_LIFE_PERIOD = 3600000;
/*      */   private static final int YGM_BLOOM_LIFE_TICKS = 360;
/*      */   private static final int SAVE_CONFIG_PERIOD = 60000;
/*      */   private static final int SAVE_CONFIG_TICKS = 6;
/*      */   public static final int PERSISTENT_MSG_RETRY_PERIOD = 300000;
/*      */   private static final int PERSISTENT_MSG_CHECK_PERIOD = 60000;
/*      */   private static final int PERSISTENT_MSG_CHECK_TICKS = 6;
/*      */   private static final int UNAUTH_BLOOM_RECREATE = 120000;
/*      */   private static final int UNAUTH_BLOOM_CHUNK = 1000;
/*      */   private static final int BLOOM_CHECK_PERIOD = 60000;
/*      */   private static final int BLOOM_CHECK_TICKS = 6;
/*      */   public static final int STREAM_CRYPTO = 3;
/*      */   public static final int BLOCK_CRYPTO = 2;
/*      */   private volatile int initialisation_state;
/*      */   private PluginInterface plugin_interface;
/*      */   private LoggerChannel logger;
/*      */   private BooleanParameter classic_enabled_param;
/*      */   private StringParameter nick_name_param;
/*      */   private StringListParameter online_status_param;
/*      */   private BooleanParameter enable_chat_notifications;
/*      */   
/*      */   public BuddyPlugin()
/*      */   {
/*  164 */     this.initialisation_state = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  181 */     this.current_publish = new publishDetails(null);
/*  182 */     this.latest_publish = this.current_publish;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  191 */     this.publish_dispatcher = new AsyncDispatcher();
/*      */     
/*      */ 
/*      */ 
/*  195 */     this.ecc_handler = CryptoManagerFactory.getSingleton().getECCHandler();
/*      */     
/*  197 */     this.buddies = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/*  201 */     this.buddies_map = new HashMap();
/*      */     
/*  203 */     this.listeners = new CopyOnWriteList();
/*  204 */     this.request_listeners = new CopyOnWriteList();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  216 */     this.random = org.gudy.azureus2.core3.util.RandomUtils.SECURE_RANDOM;
/*      */     
/*      */ 
/*      */ 
/*  220 */     this.publish_write_contacts = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  225 */     while (this.status_seq == 0)
/*      */     {
/*  227 */       this.status_seq = this.random.nextInt();
/*      */     }
/*      */     
/*      */ 
/*  231 */     this.pd_preinit = new HashSet();
/*      */     
/*  233 */     this.pd_queue = new ArrayList();
/*  234 */     this.pd_queue_sem = new AESemaphore("BuddyPlugin:persistDispatch");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  243 */     this.public_tags_or_categories = new HashSet();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void load(PluginInterface plugin_interface)
/*      */   {
/*  255 */     String name = plugin_interface.getUtilities().getLocaleUtilities().getLocalisedMessageText("Views.plugins.azbuddy.title");
/*      */     
/*      */ 
/*  258 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  259 */     plugin_interface.getPluginProperties().setProperty("plugin.name", name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void initialize(PluginInterface _plugin_interface)
/*      */   {
/*  266 */     this.plugin_interface = _plugin_interface;
/*      */     
/*      */ 
/*  269 */     this.ta_category = this.plugin_interface.getTorrentManager().getAttribute("Category");
/*      */     
/*  271 */     this.az2_handler = new BuddyPluginAZ2(this);
/*      */     
/*  273 */     this.sec_man = this.plugin_interface.getUtilities().getSecurityManager();
/*      */     
/*  275 */     this.logger = this.plugin_interface.getLogger().getChannel("Friends");
/*      */     
/*  277 */     this.logger.setDiagnostic();
/*      */     
/*  279 */     final LocaleUtilities lu = this.plugin_interface.getUtilities().getLocaleUtilities();
/*      */     
/*  281 */     lu.addListener(new org.gudy.azureus2.plugins.utils.LocaleListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void localeChanged(Locale l)
/*      */       {
/*      */ 
/*  288 */         BuddyPlugin.this.updateLocale(lu);
/*      */       }
/*      */       
/*  291 */     });
/*  292 */     updateLocale(lu);
/*      */     
/*  294 */     BasicPluginConfigModel config = this.plugin_interface.getUIManager().createBasicPluginConfigModel("Views.plugins.azbuddy.title");
/*      */     
/*      */ 
/*      */ 
/*  298 */     this.classic_enabled_param = config.addBooleanParameter2("azbuddy.enabled", "azbuddy.enabled", false);
/*      */     
/*      */ 
/*      */ 
/*  302 */     this.nick_name_param = config.addStringParameter2("azbuddy.nickname", "azbuddy.nickname", "");
/*      */     
/*  304 */     this.nick_name_param.setGenerateIntermediateEvents(false);
/*      */     
/*  306 */     this.nick_name_param.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  313 */         BuddyPlugin.this.updateNickName(BuddyPlugin.this.nick_name_param.getValue());
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  318 */     });
/*  319 */     String[] os_values = STATUS_VALUES;
/*  320 */     String[] os_labels = STATUS_STRINGS;
/*      */     
/*  322 */     this.online_status_param = config.addStringListParameter2("azbuddy.online_status", "azbuddy.online_status", os_values, os_labels, os_values[0]);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  328 */     this.online_status_param.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  335 */         int status = Integer.parseInt(BuddyPlugin.this.online_status_param.getValue());
/*      */         
/*  337 */         BuddyPlugin.this.updateOnlineStatus(status);
/*      */       }
/*      */       
/*  340 */     });
/*  341 */     this.online_status_param.setVisible(true);
/*      */     
/*      */ 
/*      */ 
/*  345 */     final IntParameter protocol_speed = config.addIntParameter2("azbuddy.protocolspeed", "azbuddy.protocolspeed", 32);
/*      */     
/*  347 */     protocol_speed.setMinimumRequiredUserMode(2);
/*      */     
/*  349 */     ConnectionManager cman = this.plugin_interface.getConnectionManager();
/*      */     
/*  351 */     int inbound_limit = protocol_speed.getValue() * 1024;
/*      */     
/*  353 */     this.inbound_limiter = cman.createRateLimiter("buddy_up", inbound_limit);
/*  354 */     this.outbound_limiter = cman.createRateLimiter("buddy_down", 0);
/*      */     
/*  356 */     protocol_speed.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  363 */         BuddyPlugin.this.inbound_limiter.setRateLimitBytesPerSecond(protocol_speed.getValue() * 1024);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  368 */     });
/*  369 */     this.enable_chat_notifications = config.addBooleanParameter2("azbuddy.enable_chat_notif", "azbuddy.enable_chat_notif", true);
/*      */     
/*      */ 
/*      */ 
/*  373 */     this.cat_pub = config.addStringParameter2("azbuddy.enable_cat_pub", "azbuddy.enable_cat_pub", "");
/*      */     
/*  375 */     this.cat_pub.setGenerateIntermediateEvents(false);
/*      */     
/*  377 */     setPublicTagsOrCategories(this.cat_pub.getValue(), false);
/*      */     
/*  379 */     final BooleanParameter tracker_enable = config.addBooleanParameter2("azbuddy.tracker.enabled", "azbuddy.tracker.enabled", true);
/*  380 */     final BooleanParameter tracker_so_enable = config.addBooleanParameter2("azbuddy.tracker.seeding.only.enabled", "azbuddy.tracker.seeding.only.enabled", false);
/*      */     
/*  382 */     final BooleanParameter buddies_lan_local = config.addBooleanParameter2("azbuddy.tracker.con.lan.local", "azbuddy.tracker.con.lan.local", true);
/*      */     
/*  384 */     buddies_lan_local.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  391 */         BuddyPlugin.this.lan_local_peers = buddies_lan_local.getValue();
/*      */       }
/*      */       
/*  394 */     });
/*  395 */     this.lan_local_peers = buddies_lan_local.getValue();
/*      */     
/*  397 */     this.cat_pub.addListener(new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  404 */         BuddyPlugin.this.setPublicTagsOrCategories(BuddyPlugin.this.cat_pub.getValue(), false);
/*      */       }
/*      */       
/*  407 */     });
/*  408 */     config.createGroup("label.classic", new Parameter[] { this.classic_enabled_param, this.nick_name_param, this.online_status_param, protocol_speed, this.enable_chat_notifications, this.cat_pub, tracker_enable, tracker_so_enable, buddies_lan_local });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  419 */     this.beta_enabled_param = config.addBooleanParameter2("azbuddy.dchat.decentralized.enabled", "azbuddy.dchat.decentralized.enabled", true);
/*      */     
/*      */ 
/*  422 */     config.createGroup("azbuddy.dchat.decentralized", new Parameter[] { this.beta_enabled_param });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  432 */     this.beta_plugin = new BuddyPluginBeta(this.plugin_interface, this, this.beta_enabled_param);
/*      */     
/*  434 */     final TableContextMenuItem menu_item_itorrents = this.plugin_interface.getUIManager().getTableManager().addContextMenuItem("MyTorrents", "azbuddy.contextmenu");
/*      */     
/*  436 */     final TableContextMenuItem menu_item_ctorrents = this.plugin_interface.getUIManager().getTableManager().addContextMenuItem("MySeeders", "azbuddy.contextmenu");
/*      */     
/*      */ 
/*  439 */     menu_item_itorrents.setStyle(5);
/*  440 */     menu_item_ctorrents.setStyle(5);
/*      */     
/*  442 */     MenuItemFillListener menu_fill_listener = new MenuItemFillListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void menuWillBeShown(MenuItem menu, Object _target)
/*      */       {
/*      */ 
/*      */ 
/*  450 */         menu.removeAllChildItems();
/*      */         
/*  452 */         if ((!BuddyPlugin.this.isClassicEnabled()) || (!BuddyPlugin.this.isAvailable()))
/*      */         {
/*  454 */           menu.setEnabled(false);
/*      */           
/*  456 */           return;
/*      */         }
/*      */         
/*  459 */         final List<Torrent> torrents = new ArrayList();
/*      */         
/*  461 */         if ((_target instanceof TableRow))
/*      */         {
/*  463 */           addDownload(torrents, (TableRow)_target);
/*      */         }
/*      */         else
/*      */         {
/*  467 */           TableRow[] rows = (TableRow[])_target;
/*      */           
/*  469 */           for (TableRow row : rows)
/*      */           {
/*  471 */             addDownload(torrents, row);
/*      */           }
/*      */         }
/*      */         
/*  475 */         if (torrents.size() == 0)
/*      */         {
/*  477 */           menu.setEnabled(false);
/*      */         }
/*      */         else
/*      */         {
/*  481 */           List<BuddyPluginBuddy> buddies = BuddyPlugin.this.getBuddies();
/*      */           
/*  483 */           boolean incomplete = ((TableContextMenuItem)menu).getTableID() == "MyTorrents";
/*      */           
/*  485 */           TableContextMenuItem parent = incomplete ? menu_item_itorrents : menu_item_ctorrents;
/*      */           
/*  487 */           for (int i = 0; i < buddies.size(); i++)
/*      */           {
/*  489 */             final BuddyPluginBuddy buddy = (BuddyPluginBuddy)buddies.get(i);
/*      */             
/*  491 */             if (buddy.isOnline(true))
/*      */             {
/*  493 */               TableContextMenuItem item = BuddyPlugin.this.plugin_interface.getUIManager().getTableManager().addContextMenuItem(parent, "!" + buddy.getName() + "!");
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  498 */               item.addMultiListener(new org.gudy.azureus2.plugins.ui.menus.MenuItemListener()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void selected(MenuItem menu, Object target)
/*      */                 {
/*      */ 
/*      */ 
/*  506 */                   for (Torrent torrent : torrents)
/*      */                   {
/*  508 */                     BuddyPlugin.this.az2_handler.sendAZ2Torrent(torrent, buddy);
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */           
/*  515 */           menu.setEnabled(true);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       protected void addDownload(List<Torrent> torrents, TableRow row)
/*      */       {
/*  524 */         Object obj = row.getDataSource();
/*      */         
/*      */         Download download;
/*      */         Download download;
/*  528 */         if ((obj instanceof Download))
/*      */         {
/*  530 */           download = (Download)obj;
/*      */         }
/*      */         else
/*      */         {
/*  534 */           DiskManagerFileInfo file = (DiskManagerFileInfo)obj;
/*      */           try
/*      */           {
/*  537 */             download = file.getDownload();
/*      */           }
/*      */           catch (DownloadException e)
/*      */           {
/*  541 */             Debug.printStackTrace(e);
/*      */             
/*  543 */             return;
/*      */           }
/*      */         }
/*      */         
/*  547 */         Torrent torrent = download.getTorrent();
/*      */         
/*  549 */         if ((torrent != null) && (!TorrentUtils.isReallyPrivate(PluginCoreUtils.unwrap(torrent))))
/*      */         {
/*  551 */           torrents.add(torrent);
/*      */         }
/*      */         
/*      */       }
/*  555 */     };
/*  556 */     menu_item_itorrents.addFillListener(menu_fill_listener);
/*  557 */     menu_item_ctorrents.addFillListener(menu_fill_listener);
/*      */     
/*  559 */     this.buddy_tracker = new BuddyPluginTracker(this, tracker_enable, tracker_so_enable);
/*      */     
/*  561 */     this.plugin_interface.getUIManager().addUIListener(new org.gudy.azureus2.plugins.ui.UIManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void UIAttached(UIInstance instance)
/*      */       {
/*      */ 
/*  568 */         if (instance.getUIType() == 1) {
/*      */           try
/*      */           {
/*  571 */             BuddyPlugin.this.swt_ui = ((BuddyPluginViewInterface)Class.forName("com.aelitis.azureus.plugins.net.buddy.swt.BuddyPluginView").getConstructor(new Class[] { BuddyPlugin.class, UIInstance.class, String.class }).newInstance(new Object[] { BuddyPlugin.this, instance, "azbuddy" }));
/*      */ 
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/*  578 */             e.printStackTrace();
/*      */           }
/*      */         }
/*      */         
/*  582 */         BuddyPlugin.this.setupDisablePrompt(instance);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void UIDetached(UIInstance instance) {}
/*  591 */     });
/*  592 */     org.gudy.azureus2.plugins.ui.config.ParameterListener enabled_listener = new org.gudy.azureus2.plugins.ui.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  599 */         boolean classic_enabled = BuddyPlugin.this.classic_enabled_param.getValue();
/*      */         
/*  601 */         BuddyPlugin.this.nick_name_param.setEnabled(classic_enabled);
/*  602 */         BuddyPlugin.this.online_status_param.setEnabled(classic_enabled);
/*  603 */         protocol_speed.setEnabled(classic_enabled);
/*  604 */         BuddyPlugin.this.enable_chat_notifications.setEnabled(classic_enabled);
/*  605 */         BuddyPlugin.this.cat_pub.setEnabled(classic_enabled);
/*  606 */         tracker_enable.setEnabled(classic_enabled);
/*      */         
/*  608 */         tracker_so_enable.setEnabled(tracker_enable.getValue());
/*      */         
/*      */ 
/*      */ 
/*  612 */         if (param != null)
/*      */         {
/*  614 */           BuddyPlugin.this.setClassicEnabledInternal(classic_enabled);
/*  615 */           BuddyPlugin.this.fireEnabledStateChanged();
/*      */         }
/*      */         
/*  618 */         boolean beta_enabled = BuddyPlugin.this.beta_enabled_param.getValue();
/*      */       }
/*      */       
/*  621 */     };
/*  622 */     enabled_listener.parameterChanged(null);
/*      */     
/*  624 */     this.classic_enabled_param.addListener(enabled_listener);
/*  625 */     this.beta_enabled_param.addListener(enabled_listener);
/*  626 */     tracker_enable.addListener(enabled_listener);
/*      */     
/*  628 */     loadConfig();
/*      */     
/*  630 */     registerMessageHandler();
/*      */     
/*  632 */     this.plugin_interface.addListener(new org.gudy.azureus2.plugins.PluginListener()
/*      */     {
/*      */ 
/*      */       public void initializationComplete()
/*      */       {
/*      */ 
/*  638 */         DelayedTask dt = BuddyPlugin.this.plugin_interface.getUtilities().createDelayedTask(new Runnable()
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*  643 */             new AEThread2("BuddyPlugin:init", true)
/*      */             {
/*      */ 
/*      */               public void run()
/*      */               {
/*  648 */                 BuddyPlugin.this.startup();
/*      */                 
/*  650 */                 BuddyPlugin.this.beta_plugin.startup();
/*      */               }
/*      */               
/*      */             }.start();
/*      */           }
/*  655 */         });
/*  656 */         dt.queue();
/*      */       }
/*      */       
/*      */ 
/*      */       public void closedownInitiated()
/*      */       {
/*  662 */         BuddyPlugin.this.saveConfig(true);
/*      */         
/*  664 */         BuddyPlugin.this.closedown();
/*      */         
/*  666 */         BuddyPlugin.this.beta_plugin.closedown();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void closedownComplete() {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getPeersAreLANLocal()
/*      */   {
/*  679 */     return this.lan_local_peers;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateLocale(LocaleUtilities lu)
/*      */   {
/*  686 */     for (int i = 0; i < STATUS_STRINGS.length; i++)
/*      */     {
/*  688 */       STATUS_STRINGS[i] = lu.getLocalisedMessageText("azbuddy." + STATUS_KEYS[i]);
/*      */     }
/*      */     
/*  691 */     if (this.online_status_param != null)
/*      */     {
/*  693 */       this.online_status_param.setLabels(STATUS_STRINGS);
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
/*      */   protected void setupDisablePrompt(UIInstance ui)
/*      */   {
/*  706 */     if (this.plugin_interface == null) {
/*  707 */       return;
/*      */     }
/*      */     
/*  710 */     String enabledConfigID = "PluginInfo." + this.plugin_interface.getPluginID() + ".enabled";
/*      */     
/*  712 */     COConfigurationManager.addParameterListener(enabledConfigID, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*  717 */         BuddyPlugin.this.fireEnabledStateChanged();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public void showConfig()
/*      */   {
/*  725 */     this.plugin_interface.getUIManager().showConfigSection("Views.plugins.azbuddy.title");
/*      */   }
/*      */   
/*      */   protected void startup()
/*      */   {
/*      */     try
/*      */     {
/*  732 */       this.ddb = this.plugin_interface.getDistributedDatabase();
/*      */       
/*  734 */       if (!this.ddb.isAvailable())
/*      */       {
/*  736 */         throw new Exception("DDB Unavailable");
/*      */       }
/*      */       
/*      */ 
/*  740 */       this.ddb.addListener(new DistributedDatabaseListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void event(DistributedDatabaseEvent event)
/*      */         {
/*      */ 
/*  747 */           if (event.getType() == 10)
/*      */           {
/*  749 */             BuddyPlugin.this.updateIP();
/*      */           }
/*      */           
/*      */         }
/*  753 */       });
/*  754 */       updateIP();
/*      */       
/*  756 */       updateNickName(this.nick_name_param.getValue());
/*      */       
/*  758 */       updateOnlineStatus(Integer.parseInt(this.online_status_param.getValue()));
/*      */       
/*  760 */       COConfigurationManager.addAndFireParameterListeners(new String[] { "TCP.Listen.Port", "TCP.Listen.Port.Enable", "UDP.Listen.Port", "UDP.Listen.Port.Enable" }, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void parameterChanged(String parameterName)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  772 */           BuddyPlugin.this.updateListenPorts();
/*      */         }
/*      */         
/*  775 */       });
/*  776 */       CryptoManagerFactory.getSingleton().addKeyListener(new com.aelitis.azureus.core.security.CryptoManagerKeyListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void keyChanged(CryptoHandler handler)
/*      */         {
/*      */ 
/*  783 */           BuddyPlugin.this.updateKey();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void keyLockStatusChanged(CryptoHandler handler)
/*      */         {
/*  790 */           boolean unlocked = handler.isUnlocked();
/*      */           
/*  792 */           if (unlocked)
/*      */           {
/*  794 */             if (BuddyPlugin.this.latest_publish.isEnabled())
/*      */             {
/*  796 */               BuddyPlugin.this.updatePublish(BuddyPlugin.this.latest_publish);
/*      */             }
/*      */           }
/*      */           else {
/*  800 */             new AEThread2("BuddyPlugin:disc", true)
/*      */             {
/*      */ 
/*      */               public void run()
/*      */               {
/*  805 */                 List buddies = BuddyPlugin.this.getAllBuddies();
/*      */                 
/*  807 */                 for (int i = 0; i < buddies.size(); i++)
/*      */                 {
/*  809 */                   ((BuddyPluginBuddy)buddies.get(i)).disconnect();
/*      */                 }
/*      */                 
/*      */               }
/*      */             }.start();
/*      */           }
/*      */         }
/*  816 */       });
/*  817 */       this.ready_to_publish = true;
/*      */       
/*  819 */       setClassicEnabledInternal(this.classic_enabled_param.getValue());
/*      */       
/*  821 */       checkBuddiesAndRepublish();
/*      */       
/*  823 */       fireClassicInitialised(true);
/*      */       
/*      */ 
/*      */ 
/*  827 */       List<BuddyPluginBuddy> buddies = getBuddies();
/*      */       
/*  829 */       for (BuddyPluginBuddy buddy : buddies)
/*      */       {
/*  831 */         if ((buddy.getIP() != null) && (!buddy.isConnected()))
/*      */         {
/*  833 */           log("Attempting reconnect to " + buddy.getString());
/*      */           
/*  835 */           buddy.sendKeepAlive();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  841 */       log("Initialisation failed", e);
/*      */       
/*  843 */       fireClassicInitialised(false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isClassicEnabled()
/*      */   {
/*  850 */     if (this.classic_enabled_param == null) { return false;
/*      */     }
/*  852 */     return this.classic_enabled_param.getValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setClassicEnabled(boolean enabled)
/*      */   {
/*  859 */     if (this.classic_enabled_param == null)
/*      */     {
/*  861 */       return;
/*      */     }
/*      */     
/*  864 */     this.classic_enabled_param.setValue(enabled);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setClassicEnabledInternal(boolean _enabled)
/*      */   {
/*  871 */     synchronized (this)
/*      */     {
/*  873 */       if (this.latest_publish.isEnabled() != _enabled)
/*      */       {
/*  875 */         publishDetails new_publish = this.latest_publish.getCopy();
/*      */         
/*  877 */         new_publish.setEnabled(_enabled);
/*      */         
/*  879 */         updatePublish(new_publish);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isBetaEnabled()
/*      */   {
/*  887 */     if (this.beta_enabled_param == null)
/*      */     {
/*  889 */       return false;
/*      */     }
/*      */     
/*  892 */     return this.beta_enabled_param.getValue();
/*      */   }
/*      */   
/*      */ 
/*      */   public BuddyPluginBeta getBeta()
/*      */   {
/*  898 */     return this.beta_plugin;
/*      */   }
/*      */   
/*      */ 
/*      */   public BuddyPluginTracker getTracker()
/*      */   {
/*  904 */     return this.buddy_tracker;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getNickname()
/*      */   {
/*  910 */     return this.nick_name_param.getValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setNickname(String str)
/*      */   {
/*  917 */     this.nick_name_param.setValue(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setOnlineStatus(int status)
/*      */   {
/*  924 */     this.online_status_param.setValue("" + status);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getOnlineStatus()
/*      */   {
/*  930 */     return this.latest_publish.getOnlineStatus();
/*      */   }
/*      */   
/*      */ 
/*      */   public BooleanParameter getEnableChatNotificationsParameter()
/*      */   {
/*  936 */     return this.enable_chat_notifications;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String normaliseCat(String str)
/*      */   {
/*  943 */     if (str == null)
/*      */     {
/*  945 */       return null;
/*      */     }
/*  947 */     if (str.toLowerCase().equals("all"))
/*      */     {
/*  949 */       return "All";
/*      */     }
/*      */     
/*      */ 
/*  953 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void normaliseCats(Set<String> cats)
/*      */   {
/*  961 */     if (cats != null)
/*      */     {
/*  963 */       boolean all_found = false;
/*      */       
/*  965 */       Iterator<String> it = cats.iterator();
/*      */       
/*  967 */       while (it.hasNext())
/*      */       {
/*  969 */         if (((String)it.next()).toLowerCase().equals("all"))
/*      */         {
/*  971 */           it.remove();
/*      */           
/*  973 */           all_found = true;
/*      */         }
/*      */       }
/*      */       
/*  977 */       if (all_found)
/*      */       {
/*  979 */         cats.add("All");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isPublicTagOrCategory(String cat)
/*      */   {
/*  988 */     cat = normaliseCat(cat);
/*      */     
/*  990 */     return this.public_tags_or_categories.contains(cat);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPublicTagOrCategory(String cat)
/*      */   {
/*  997 */     cat = normaliseCat(cat);
/*      */     
/*  999 */     Set<String> new_cats = new HashSet(this.public_tags_or_categories);
/*      */     
/* 1001 */     if (new_cats.add(cat))
/*      */     {
/* 1003 */       setPublicTagsOrCategories(new_cats, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePublicTagOrCategory(String cat)
/*      */   {
/* 1011 */     cat = normaliseCat(cat);
/*      */     
/* 1013 */     Set<String> new_cats = new HashSet(this.public_tags_or_categories);
/*      */     
/* 1015 */     if (new_cats.remove(cat))
/*      */     {
/* 1017 */       setPublicTagsOrCategories(new_cats, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setPublicTagsOrCategories(String str, boolean persist)
/*      */   {
/* 1026 */     Set<String> new_pub_cats = new HashSet();
/*      */     
/* 1028 */     String[] bits = str.split(",");
/*      */     
/* 1030 */     for (String s : bits)
/*      */     {
/* 1032 */       s = s.trim();
/*      */       
/* 1034 */       if (bits.length > 0)
/*      */       {
/* 1036 */         new_pub_cats.add(normaliseCat(s));
/*      */       }
/*      */     }
/*      */     
/* 1040 */     setPublicTagsOrCategories(new_pub_cats, persist);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setPublicTagsOrCategories(Set<String> new_pub_tags_or_cats, boolean persist)
/*      */   {
/*      */     Set<String> removed;
/*      */     
/* 1048 */     if (!this.public_tags_or_categories.equals(new_pub_tags_or_cats))
/*      */     {
/* 1050 */       removed = new HashSet(this.public_tags_or_categories);
/*      */       
/* 1052 */       removed.removeAll(new_pub_tags_or_cats);
/*      */       
/* 1054 */       this.public_tags_or_categories = new_pub_tags_or_cats;
/*      */       
/* 1056 */       if (persist)
/*      */       {
/* 1058 */         String cat_str = "";
/*      */         
/* 1060 */         for (String s : this.public_tags_or_categories)
/*      */         {
/* 1062 */           cat_str = cat_str + (cat_str.length() == 0 ? "" : ",") + s;
/*      */         }
/*      */         
/* 1065 */         this.cat_pub.setValue(cat_str);
/*      */       }
/*      */       
/* 1068 */       List<BuddyPluginBuddy> buds = getBuddies();
/*      */       
/* 1070 */       for (BuddyPluginBuddy b : buds)
/*      */       {
/* 1072 */         Set<String> local = b.getLocalAuthorisedRSSTagsOrCategories();
/*      */         
/* 1074 */         if ((local != null) || (new_pub_tags_or_cats.size() > 0))
/*      */         {
/* 1076 */           if (local == null)
/*      */           {
/* 1078 */             local = new HashSet();
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 1084 */             local = new HashSet(local);
/*      */           }
/*      */           
/* 1087 */           local.addAll(new_pub_tags_or_cats);
/*      */           
/* 1089 */           local.removeAll(removed);
/*      */           
/* 1091 */           b.setLocalAuthorisedRSSTagsOrCategories(local);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void registerMessageHandler()
/*      */   {
/*      */     try
/*      */     {
/* 1101 */       addRequestListener(new BuddyPluginBuddyRequestListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public Map requestReceived(BuddyPluginBuddy from_buddy, int subsystem, Map request)
/*      */           throws BuddyPluginException
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1112 */           if (subsystem == 0)
/*      */           {
/* 1114 */             if (!from_buddy.isAuthorised())
/*      */             {
/* 1116 */               throw new BuddyPluginException("Unauthorised");
/*      */             }
/*      */             
/* 1119 */             return BuddyPlugin.this.processInternalRequest(from_buddy, request);
/*      */           }
/*      */           
/* 1122 */           return null;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void pendingMessages(BuddyPluginBuddy[] from_buddies) {}
/* 1131 */       });
/* 1132 */       this.msg_registration = this.plugin_interface.getMessageManager().registerGenericMessageType("AZBUDDY", "Buddy message handler", 3, new org.gudy.azureus2.plugins.messaging.generic.GenericMessageHandler()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public boolean accept(GenericMessageConnection connection)
/*      */           throws org.gudy.azureus2.plugins.messaging.MessageException
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1144 */           if (!BuddyPlugin.this.isClassicEnabled())
/*      */           {
/* 1146 */             return false;
/*      */           }
/*      */           
/* 1149 */           final String originator = connection.getEndpoint().getNotionalAddress().getAddress().getHostAddress();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/* 1156 */             String reason = "Friend: Incoming connection establishment (" + originator + ")";
/*      */             
/* 1158 */             BuddyPlugin.this.addRateLimiters(connection);
/*      */             
/* 1160 */             connection = BuddyPlugin.this.sec_man.getSTSConnection(connection, BuddyPlugin.this.sec_man.getPublicKey(1, reason), new org.gudy.azureus2.plugins.utils.security.SEPublicKeyLocator()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public boolean accept(Object context, SEPublicKey other_key)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 1171 */                 String other_key_str = Base32.encode(other_key.encodeRawPublicKey());
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 try
/*      */                 {
/* 1178 */                   synchronized (BuddyPlugin.this)
/*      */                   {
/* 1180 */                     int unauth_count = 0;
/*      */                     
/* 1182 */                     for (int i = 0; i < BuddyPlugin.this.buddies.size(); i++)
/*      */                     {
/* 1184 */                       BuddyPluginBuddy buddy = (BuddyPluginBuddy)BuddyPlugin.this.buddies.get(i);
/*      */                       
/* 1186 */                       if (buddy.getPublicKey().equals(other_key_str))
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/* 1191 */                         if (!buddy.isAuthorised())
/*      */                         {
/* 1193 */                           BuddyPlugin.this.log("Incoming connection from " + originator + " failed as for unauthorised buddy");
/*      */                           
/* 1195 */                           return false;
/*      */                         }
/*      */                         
/* 1198 */                         buddy.incomingConnection((GenericMessageConnection)context);
/*      */                         
/* 1200 */                         return true;
/*      */                       }
/*      */                       
/* 1203 */                       if (!buddy.isAuthorised())
/*      */                       {
/* 1205 */                         unauth_count++;
/*      */                       }
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/* 1211 */                     if (unauth_count < 16)
/*      */                     {
/* 1213 */                       if (BuddyPlugin.this.tooManyUnauthConnections(originator))
/*      */                       {
/* 1215 */                         BuddyPlugin.this.log("Too many recent unauthorised connections from " + originator);
/*      */                         
/* 1217 */                         return false;
/*      */                       }
/*      */                       
/* 1220 */                       BuddyPluginBuddy buddy = BuddyPlugin.this.addBuddy(other_key_str, 1, false);
/*      */                       
/* 1222 */                       if (buddy != null)
/*      */                       {
/* 1224 */                         buddy.incomingConnection((GenericMessageConnection)context);
/*      */                         
/* 1226 */                         return true;
/*      */                       }
/*      */                       
/*      */ 
/* 1230 */                       return false;
/*      */                     }
/*      */                   }
/*      */                   
/*      */ 
/* 1235 */                   BuddyPlugin.this.log("Incoming connection from " + originator + " failed due to pk mismatch");
/*      */                   
/* 1237 */                   return false;
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1241 */                   BuddyPlugin.this.log("Incomming connection from " + originator + " failed", e);
/*      */                 }
/* 1243 */                 return false; } }, reason, 2);
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/*      */ 
/* 1252 */             connection.close();
/*      */             
/* 1254 */             BuddyPlugin.this.log("Incoming connection from " + originator + " failed", e);
/*      */           }
/*      */           
/* 1257 */           return true;
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1263 */       log("Failed to register message listener", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void addRateLimiters(GenericMessageConnection connection)
/*      */   {
/* 1271 */     connection.addInboundRateLimiter(this.inbound_limiter);
/* 1272 */     connection.addOutboundRateLimiter(this.outbound_limiter);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean tooManyUnauthConnections(String originator)
/*      */   {
/* 1279 */     synchronized (this)
/*      */     {
/* 1281 */       if (this.unauth_bloom == null)
/*      */       {
/* 1283 */         this.unauth_bloom = BloomFilterFactory.createAddRemove4Bit(1000);
/*      */         
/* 1285 */         this.unauth_bloom_create_time = SystemTime.getCurrentTime();
/*      */       }
/*      */       
/* 1288 */       int hit_count = this.unauth_bloom.add(originator.getBytes());
/*      */       
/* 1290 */       if (hit_count >= 8)
/*      */       {
/* 1292 */         Debug.out("Too many recent unauthorised connection attempts from " + originator);
/*      */         
/* 1294 */         return true;
/*      */       }
/*      */       
/* 1297 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkUnauthBloom()
/*      */   {
/* 1304 */     synchronized (this)
/*      */     {
/* 1306 */       if (this.unauth_bloom != null)
/*      */       {
/* 1308 */         long now = SystemTime.getCurrentTime();
/*      */         
/* 1310 */         if (now < this.unauth_bloom_create_time)
/*      */         {
/* 1312 */           this.unauth_bloom_create_time = now;
/*      */         }
/* 1314 */         else if (now - this.unauth_bloom_create_time > 120000L)
/*      */         {
/* 1316 */           this.unauth_bloom = null;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkMaxMessageSize(int size)
/*      */     throws BuddyPluginException
/*      */   {
/* 1328 */     if (size > 4194304)
/*      */     {
/* 1330 */       throw new BuddyPluginException("Message is too large to send, limit is " + org.gudy.azureus2.core3.util.DisplayFormatters.formatByteCountToKiBEtc(4194304));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkPersistentDispatch()
/*      */   {
/*      */     List buddies_copy;
/*      */     
/* 1339 */     synchronized (this)
/*      */     {
/* 1341 */       buddies_copy = new ArrayList(this.buddies);
/*      */     }
/*      */     
/* 1344 */     for (int i = 0; i < buddies_copy.size(); i++)
/*      */     {
/* 1346 */       BuddyPluginBuddy buddy = (BuddyPluginBuddy)buddies_copy.get(i);
/*      */       
/* 1348 */       buddy.checkPersistentDispatch();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void persistentDispatchInit()
/*      */   {
/* 1355 */     Iterator it = this.pd_preinit.iterator();
/*      */     
/* 1357 */     while (it.hasNext())
/*      */     {
/* 1359 */       persistentDispatchPending((BuddyPluginBuddy)it.next());
/*      */     }
/*      */     
/* 1362 */     this.pd_preinit = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void persistentDispatchPending(BuddyPluginBuddy buddy)
/*      */   {
/* 1369 */     synchronized (this.pd_queue)
/*      */     {
/* 1371 */       if (this.initialisation_state == 0)
/*      */       {
/* 1373 */         this.pd_preinit.add(buddy);
/*      */         
/* 1375 */         return;
/*      */       }
/*      */       
/* 1378 */       if (!this.pd_queue.contains(buddy))
/*      */       {
/* 1380 */         this.pd_queue.add(buddy);
/*      */         
/* 1382 */         this.pd_queue_sem.release();
/*      */         
/* 1384 */         if (this.pd_thread == null)
/*      */         {
/* 1386 */           this.pd_thread = new AEThread2("BuddyPlugin:persistDispatch", true)
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/*      */               for (;;)
/*      */               {
/* 1394 */                 if (!BuddyPlugin.this.pd_queue_sem.reserve(30000L))
/*      */                 {
/* 1396 */                   synchronized (BuddyPlugin.this.pd_queue)
/*      */                   {
/* 1398 */                     if (BuddyPlugin.this.pd_queue.isEmpty())
/*      */                     {
/* 1400 */                       BuddyPlugin.this.pd_thread = null;
/*      */                       
/* 1402 */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 else
/*      */                 {
/*      */                   BuddyPluginBuddy buddy;
/* 1409 */                   synchronized (BuddyPlugin.this.pd_queue)
/*      */                   {
/* 1411 */                     buddy = (BuddyPluginBuddy)BuddyPlugin.this.pd_queue.remove(0);
/*      */                   }
/*      */                   
/* 1414 */                   buddy.persistentDispatch();
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/* 1419 */           };
/* 1420 */           this.pd_thread.start();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map processInternalRequest(BuddyPluginBuddy from_buddy, Map request)
/*      */     throws BuddyPluginException
/*      */   {
/* 1433 */     int type = ((Long)request.get("type")).intValue();
/*      */     
/* 1435 */     if (type == 1)
/*      */     {
/* 1437 */       Map reply = new HashMap();
/*      */       
/* 1439 */       reply.put("type", new Long(2L));
/*      */       
/* 1441 */       return reply;
/*      */     }
/* 1443 */     if (type == 3)
/*      */     {
/* 1445 */       from_buddy.receivedCloseRequest(request);
/*      */       
/* 1447 */       Map reply = new HashMap();
/*      */       
/* 1449 */       reply.put("type", new Long(4L));
/*      */       
/* 1451 */       return reply;
/*      */     }
/*      */     
/*      */ 
/* 1455 */     throw new BuddyPluginException("Unrecognised request type " + type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateListenPorts()
/*      */   {
/* 1462 */     synchronized (this)
/*      */     {
/* 1464 */       int tcp_port = COConfigurationManager.getIntParameter("TCP.Listen.Port");
/* 1465 */       boolean tcp_enabled = COConfigurationManager.getBooleanParameter("TCP.Listen.Port.Enable");
/* 1466 */       int udp_port = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/* 1467 */       boolean udp_enabled = COConfigurationManager.getBooleanParameter("UDP.Listen.Port.Enable");
/*      */       
/* 1469 */       if (!tcp_enabled)
/*      */       {
/* 1471 */         tcp_port = 0;
/*      */       }
/*      */       
/* 1474 */       if (!udp_enabled)
/*      */       {
/* 1476 */         udp_port = 0;
/*      */       }
/*      */       
/* 1479 */       if ((this.latest_publish.getTCPPort() != tcp_port) || (this.latest_publish.getUDPPort() != udp_port))
/*      */       {
/*      */ 
/* 1482 */         publishDetails new_publish = this.latest_publish.getCopy();
/*      */         
/* 1484 */         new_publish.setTCPPort(tcp_port);
/* 1485 */         new_publish.setUDPPort(udp_port);
/*      */         
/* 1487 */         updatePublish(new_publish);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updateIP()
/*      */   {
/* 1495 */     if ((this.ddb == null) || (!this.ddb.isAvailable()))
/*      */     {
/* 1497 */       return;
/*      */     }
/*      */     
/* 1500 */     synchronized (this)
/*      */     {
/* 1502 */       InetAddress public_ip = this.ddb.getLocalContact().getAddress().getAddress();
/*      */       
/* 1504 */       if ((this.latest_publish.getIP() == null) || (!this.latest_publish.getIP().equals(public_ip)))
/*      */       {
/*      */ 
/* 1507 */         publishDetails new_publish = this.latest_publish.getCopy();
/*      */         
/* 1509 */         new_publish.setIP(public_ip);
/*      */         
/* 1511 */         updatePublish(new_publish);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateNickName(String new_nick)
/*      */   {
/* 1520 */     new_nick = new_nick.trim();
/*      */     
/* 1522 */     if (new_nick.length() == 0)
/*      */     {
/* 1524 */       new_nick = null;
/*      */     }
/*      */     
/* 1527 */     synchronized (this)
/*      */     {
/* 1529 */       String old_nick = this.latest_publish.getNickName();
/*      */       
/* 1531 */       if (!stringsEqual(new_nick, old_nick))
/*      */       {
/* 1533 */         publishDetails new_publish = this.latest_publish.getCopy();
/*      */         
/* 1535 */         new_publish.setNickName(new_nick);
/*      */         
/* 1537 */         updatePublish(new_publish);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateOnlineStatus(int new_status)
/*      */   {
/*      */     boolean changed;
/*      */     
/* 1548 */     synchronized (this)
/*      */     {
/* 1550 */       int old_status = this.latest_publish.getOnlineStatus();
/*      */       
/* 1552 */       changed = old_status != new_status;
/*      */       
/* 1554 */       if (changed)
/*      */       {
/* 1556 */         publishDetails new_publish = this.latest_publish.getCopy();
/*      */         
/* 1558 */         new_publish.setOnlineStatus(new_status);
/*      */         
/* 1560 */         updatePublish(new_publish);
/*      */       }
/*      */     }
/*      */     
/* 1564 */     if (changed)
/*      */     {
/* 1566 */       List buddies = getAllBuddies();
/*      */       
/* 1568 */       for (int i = 0; i < buddies.size(); i++)
/*      */       {
/* 1570 */         BuddyPluginBuddy buddy = (BuddyPluginBuddy)buddies.get(i);
/*      */         
/* 1572 */         if (buddy.isConnected())
/*      */         {
/* 1574 */           buddy.sendKeepAlive();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getOnlineStatus(int status)
/*      */   {
/* 1584 */     if ((status >= STATUS_STRINGS.length) || (status < 0))
/*      */     {
/* 1586 */       status = 0;
/*      */     }
/*      */     
/* 1589 */     return STATUS_STRINGS[status];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean stringsEqual(String s1, String s2)
/*      */   {
/* 1597 */     if ((s1 == null) && (s2 == null))
/*      */     {
/* 1599 */       return true;
/*      */     }
/*      */     
/* 1602 */     if ((s1 == null) || (s2 == null))
/*      */     {
/* 1604 */       return false;
/*      */     }
/*      */     
/* 1607 */     return s1.equals(s2);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updateKey()
/*      */   {
/* 1613 */     synchronized (this)
/*      */     {
/* 1615 */       publishDetails new_publish = this.latest_publish.getCopy();
/*      */       
/* 1617 */       new_publish.setPublicKey(null);
/*      */       
/* 1619 */       updatePublish(new_publish);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updatePublish(final publishDetails details)
/*      */   {
/* 1627 */     this.latest_publish = details;
/*      */     
/* 1629 */     if ((this.ddb == null) || (!this.ready_to_publish))
/*      */     {
/* 1631 */       return;
/*      */     }
/*      */     
/* 1634 */     this.publish_dispatcher.dispatch(new org.gudy.azureus2.core3.util.AERunnable()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*      */ 
/* 1642 */         if (BuddyPlugin.this.publish_dispatcher.getQueueSize() > 0)
/*      */         {
/* 1644 */           return;
/*      */         }
/*      */         
/* 1647 */         BuddyPlugin.this.updatePublishSupport(details);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updatePublishSupport(publishDetails details)
/*      */   {
/* 1656 */     byte[] key_to_remove = null;
/*      */     
/*      */     boolean log_this;
/*      */     
/*      */     publishDetails existing_details;
/*      */     
/* 1662 */     synchronized (this)
/*      */     {
/* 1664 */       log_this = !this.current_publish.getString().equals(details.getString());
/*      */       
/* 1666 */       existing_details = this.current_publish;
/*      */       
/* 1668 */       if (!details.isEnabled())
/*      */       {
/* 1670 */         if (this.current_publish.isPublished())
/*      */         {
/* 1672 */           key_to_remove = this.current_publish.getPublicKey();
/*      */         }
/*      */       }
/*      */       else {
/* 1676 */         if (details.getPublicKey() == null) {
/*      */           try
/*      */           {
/* 1679 */             details.setPublicKey(this.ecc_handler.getPublicKey("Creating online status key"));
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1683 */             log("Failed to publish details", e);
/*      */             
/* 1685 */             return;
/*      */           }
/*      */         }
/*      */         
/* 1689 */         if (this.current_publish.isPublished())
/*      */         {
/* 1691 */           byte[] existing_key = this.current_publish.getPublicKey();
/*      */           
/* 1693 */           if (!java.util.Arrays.equals(existing_key, details.getPublicKey()))
/*      */           {
/* 1695 */             key_to_remove = existing_key;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1700 */       this.current_publish = details;
/*      */     }
/*      */     
/* 1703 */     if (key_to_remove != null)
/*      */     {
/* 1705 */       log("Removing old status publish: " + existing_details.getString());
/*      */       try
/*      */       {
/* 1708 */         this.ddb.delete(new DistributedDatabaseListener() { public void event(DistributedDatabaseEvent event) {} }, getStatusKey(key_to_remove, "Friend status de-registration for old key"));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1721 */         log("Failed to remove existing publish", e);
/*      */       }
/*      */     }
/*      */     
/* 1725 */     if (details.isEnabled())
/*      */     {
/*      */ 
/*      */ 
/* 1729 */       InetAddress ip = details.getIP();
/*      */       
/* 1731 */       if ((ip.isLoopbackAddress()) || (ip.isLinkLocalAddress()) || (ip.isSiteLocalAddress()))
/*      */       {
/* 1733 */         log("Can't publish as ip address is invalid: " + details.getString());
/*      */         
/* 1735 */         return;
/*      */       }
/*      */       
/* 1738 */       details.setPublished(true);
/*      */       
/* 1740 */       Map payload = new HashMap();
/*      */       
/* 1742 */       if (details.getTCPPort() > 0)
/*      */       {
/* 1744 */         payload.put("t", new Long(details.getTCPPort()));
/*      */       }
/*      */       
/* 1747 */       if (details.getUDPPort() > 0)
/*      */       {
/* 1749 */         payload.put("u", new Long(details.getUDPPort()));
/*      */       }
/*      */       
/* 1752 */       payload.put("i", ip.getAddress());
/*      */       
/* 1754 */       String nick = details.getNickName();
/*      */       
/* 1756 */       if (nick != null)
/*      */       {
/* 1758 */         if (nick.length() > 32)
/*      */         {
/* 1760 */           nick = nick.substring(0, 32);
/*      */         }
/*      */         
/* 1763 */         payload.put("n", nick);
/*      */       }
/*      */       
/* 1766 */       payload.put("o", new Long(details.getOnlineStatus()));
/*      */       
/* 1768 */       int next_seq = ++this.status_seq;
/*      */       
/* 1770 */       if (next_seq == 0)
/*      */       {
/* 1772 */         next_seq = ++this.status_seq;
/*      */       }
/*      */       
/* 1775 */       details.setSequence(next_seq);
/*      */       
/* 1777 */       payload.put("s", new Long(next_seq));
/*      */       
/* 1779 */       payload.put("v", new Long(2L));
/*      */       
/* 1781 */       boolean failed_to_get_key = true;
/*      */       try
/*      */       {
/* 1784 */         byte[] data = BEncoder.encode(payload);
/*      */         
/* 1786 */         DistributedDatabaseKey key = getStatusKey(details.getPublicKey(), "My buddy status registration " + payload);
/*      */         
/* 1788 */         byte[] signature = this.ecc_handler.sign(data, "Friend online status");
/*      */         
/* 1790 */         failed_to_get_key = false;
/*      */         
/* 1792 */         byte[] signed_payload = new byte[1 + signature.length + data.length];
/*      */         
/* 1794 */         signed_payload[0] = ((byte)signature.length);
/*      */         
/* 1796 */         System.arraycopy(signature, 0, signed_payload, 1, signature.length);
/* 1797 */         System.arraycopy(data, 0, signed_payload, 1 + signature.length, data.length);
/*      */         
/* 1799 */         DistributedDatabaseValue value = this.ddb.createValue(signed_payload);
/*      */         
/* 1801 */         final AESemaphore sem = new AESemaphore("BuddyPlugin:reg");
/*      */         
/* 1803 */         if (log_this)
/*      */         {
/* 1805 */           logMessage("Publishing status starts: " + details.getString());
/*      */         }
/*      */         
/* 1808 */         this.last_publish_start = SystemTime.getMonotonousTime();
/*      */         
/* 1810 */         this.ddb.write(new DistributedDatabaseListener()
/*      */         {
/*      */ 
/* 1813 */           private List<DistributedDatabaseContact> write_contacts = new ArrayList();
/*      */           
/*      */ 
/*      */ 
/*      */           public void event(DistributedDatabaseEvent event)
/*      */           {
/* 1819 */             int type = event.getType();
/*      */             
/* 1821 */             if (type == 1)
/*      */             {
/* 1823 */               this.write_contacts.add(event.getContact());
/*      */             }
/* 1825 */             else if ((type == 5) || (type == 4))
/*      */             {
/*      */ 
/* 1828 */               synchronized (BuddyPlugin.this.publish_write_contacts)
/*      */               {
/* 1830 */                 BuddyPlugin.this.publish_write_contacts.clear();
/*      */                 
/* 1832 */                 BuddyPlugin.this.publish_write_contacts.addAll(this.write_contacts);
/*      */               }
/*      */               
/* 1835 */               sem.release(); } } }, key, value);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1842 */         sem.reserve();
/*      */         
/* 1844 */         if (log_this)
/*      */         {
/* 1846 */           logMessage("My status publish complete");
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1850 */         logMessage("Failed to publish online status", e);
/*      */         
/* 1852 */         if (failed_to_get_key)
/*      */         {
/* 1854 */           synchronized (this)
/*      */           {
/* 1856 */             if (this.republish_delay_event != null)
/*      */             {
/* 1858 */               return;
/*      */             }
/*      */             
/* 1861 */             if ((this.last_publish_start == 0L) || (SystemTime.getMonotonousTime() - this.last_publish_start > 600000L))
/*      */             {
/*      */ 
/* 1864 */               log("Rescheduling publish as failed to get key");
/*      */               
/* 1866 */               this.republish_delay_event = org.gudy.azureus2.core3.util.SimpleTimer.addEvent("BuddyPlugin:republish", SystemTime.getCurrentTime() + 60000L, new org.gudy.azureus2.core3.util.TimerEventPerformer()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void perform(TimerEvent event)
/*      */                 {
/*      */ 
/*      */ 
/* 1875 */                   synchronized (BuddyPlugin.this)
/*      */                   {
/* 1877 */                     BuddyPlugin.this.republish_delay_event = null;
/*      */                   }
/*      */                   
/* 1880 */                   if ((BuddyPlugin.this.last_publish_start == 0L) || (SystemTime.getMonotonousTime() - BuddyPlugin.this.last_publish_start > 600000L))
/*      */                   {
/*      */ 
/* 1883 */                     if (BuddyPlugin.this.latest_publish.isEnabled())
/*      */                     {
/* 1885 */                       BuddyPlugin.this.updatePublish(BuddyPlugin.this.latest_publish);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected int getCurrentStatusSeq()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 2057	com/aelitis/azureus/plugins/net/buddy/BuddyPlugin:current_publish	Lcom/aelitis/azureus/plugins/net/buddy/BuddyPlugin$publishDetails;
/*      */     //   8: invokevirtual 2198	com/aelitis/azureus/plugins/net/buddy/BuddyPlugin$publishDetails:getSequence	()I
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: ireturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1901	-> byte code offset #0
/*      */     //   Java source line #1903	-> byte code offset #4
/*      */     //   Java source line #1904	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	BuddyPlugin
/*      */     //   2	14	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   protected void closedown()
/*      */   {
/* 1910 */     logMessage("Closing down");
/*      */     
/* 1912 */     List<BuddyPluginBuddy> buddies = getAllBuddies();
/*      */     
/* 1914 */     synchronized (this)
/*      */     {
/* 1916 */       this.connected_at_close = new ArrayList();
/*      */       
/* 1918 */       for (BuddyPluginBuddy buddy : buddies)
/*      */       {
/* 1920 */         if (buddy.isConnected())
/*      */         {
/* 1922 */           this.connected_at_close.add(buddy);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1927 */     if (this.ddb != null)
/*      */     {
/* 1929 */       boolean restarting = AzureusCoreFactory.isCoreAvailable() ? AzureusCoreFactory.getSingleton().isRestarting() : false;
/*      */       
/* 1931 */       logMessage("   closing buddy connections");
/*      */       
/* 1933 */       for (int i = 0; i < buddies.size(); i++)
/*      */       {
/* 1935 */         ((BuddyPluginBuddy)buddies.get(i)).sendCloseRequest(restarting);
/*      */       }
/*      */       
/* 1938 */       if (!restarting)
/*      */       {
/* 1940 */         logMessage("   updating online status");
/*      */         
/* 1942 */         List contacts = new ArrayList();
/*      */         
/* 1944 */         synchronized (this.publish_write_contacts)
/*      */         {
/* 1946 */           contacts.addAll(this.publish_write_contacts);
/*      */         }
/*      */         
/*      */         byte[] key_to_remove;
/*      */         
/* 1951 */         synchronized (this)
/*      */         {
/* 1953 */           key_to_remove = this.current_publish.getPublicKey();
/*      */         }
/*      */         
/* 1956 */         if ((contacts.size() == 0) || (key_to_remove == null))
/*      */         {
/* 1958 */           return;
/*      */         }
/*      */         
/* 1961 */         DistributedDatabaseContact[] contact_a = new DistributedDatabaseContact[contacts.size()];
/*      */         
/* 1963 */         contacts.toArray(contact_a);
/*      */         try
/*      */         {
/* 1966 */           this.ddb.delete(new DistributedDatabaseListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void event(DistributedDatabaseEvent event)
/*      */             {
/*      */ 
/* 1973 */               if (event.getType() == 3) {} } }, getStatusKey(key_to_remove, "Friend status de-registration for closedown"), contact_a);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1984 */           log("Failed to remove existing publish", e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DistributedDatabaseKey getStatusKey(byte[] public_key, String reason)
/*      */     throws Exception
/*      */   {
/* 1997 */     byte[] key_prefix = "azbuddy:status".getBytes();
/*      */     
/* 1999 */     byte[] key_bytes = new byte[key_prefix.length + public_key.length];
/*      */     
/* 2001 */     System.arraycopy(key_prefix, 0, key_bytes, 0, key_prefix.length);
/* 2002 */     System.arraycopy(public_key, 0, key_bytes, key_prefix.length, public_key.length);
/*      */     
/* 2004 */     DistributedDatabaseKey key = this.ddb.createKey(key_bytes, reason);
/*      */     
/* 2006 */     return key;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DistributedDatabaseKey getYGMKey(byte[] public_key, String reason)
/*      */     throws Exception
/*      */   {
/* 2016 */     byte[] key_prefix = "azbuddy:ygm".getBytes();
/*      */     
/* 2018 */     byte[] key_bytes = new byte[key_prefix.length + public_key.length];
/*      */     
/* 2020 */     System.arraycopy(key_prefix, 0, key_bytes, 0, key_prefix.length);
/* 2021 */     System.arraycopy(public_key, 0, key_bytes, key_prefix.length, public_key.length);
/*      */     
/* 2023 */     DistributedDatabaseKey key = this.ddb.createKey(key_bytes, reason);
/*      */     
/* 2025 */     return key;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setConfigDirty()
/*      */   {
/* 2031 */     synchronized (this)
/*      */     {
/* 2033 */       this.config_dirty = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void loadConfig()
/*      */   {
/* 2040 */     long now = SystemTime.getCurrentTime();
/*      */     int num_buddies;
/* 2042 */     synchronized (this)
/*      */     {
/* 2044 */       Map map = readConfig();
/*      */       
/* 2046 */       List buddies_config = (List)map.get("friends");
/*      */       
/* 2048 */       if (buddies_config != null)
/*      */       {
/* 2050 */         if (buddies_config.size() == 0)
/*      */         {
/* 2052 */           deleteConfig();
/*      */         }
/*      */         else {
/* 2055 */           for (int i = 0; i < buddies_config.size(); i++)
/*      */           {
/* 2057 */             Object o = buddies_config.get(i);
/*      */             
/* 2059 */             if ((o instanceof Map))
/*      */             {
/* 2061 */               Map details = (Map)o;
/*      */               
/* 2063 */               Long l_ct = (Long)details.get("ct");
/*      */               
/* 2065 */               long created_time = l_ct == null ? now : l_ct.longValue();
/*      */               
/* 2067 */               if (created_time > now)
/*      */               {
/* 2069 */                 created_time = now;
/*      */               }
/*      */               
/* 2072 */               String key = new String((byte[])details.get("pk"));
/*      */               
/* 2074 */               List recent_ygm = (List)details.get("ygm");
/*      */               
/* 2076 */               String nick = decodeString((byte[])details.get("n"));
/*      */               
/* 2078 */               Long l_seq = (Long)details.get("ls");
/*      */               
/* 2080 */               int last_seq = l_seq == null ? 0 : l_seq.intValue();
/*      */               
/* 2082 */               Long l_lo = (Long)details.get("lo");
/*      */               
/* 2084 */               long last_time_online = l_lo == null ? 0L : l_lo.longValue();
/*      */               
/* 2086 */               if (last_time_online > now)
/*      */               {
/* 2088 */                 last_time_online = now;
/*      */               }
/*      */               
/* 2091 */               Long l_subsystem = (Long)details.get("ss");
/*      */               
/* 2093 */               int subsystem = l_subsystem == null ? 1 : l_subsystem.intValue();
/*      */               
/* 2095 */               if (subsystem != 2)
/*      */               {
/*      */ 
/*      */ 
/* 2099 */                 Long l_ver = (Long)details.get("v");
/*      */                 
/* 2101 */                 int ver = l_ver == null ? 1 : l_ver.intValue();
/*      */                 
/* 2103 */                 String loc_cat = decodeString((byte[])details.get("lc"));
/* 2104 */                 String rem_cat = decodeString((byte[])details.get("rc"));
/*      */                 
/* 2106 */                 BuddyPluginBuddy buddy = new BuddyPluginBuddy(this, created_time, subsystem, true, key, nick, ver, loc_cat, rem_cat, last_seq, last_time_online, recent_ygm);
/*      */                 
/* 2108 */                 byte[] ip_bytes = (byte[])details.get("ip");
/*      */                 
/* 2110 */                 if (ip_bytes != null) {
/*      */                   try
/*      */                   {
/* 2113 */                     InetAddress ip = InetAddress.getByAddress(ip_bytes);
/*      */                     
/* 2115 */                     int tcp_port = ((Long)details.get("tcp")).intValue();
/* 2116 */                     int udp_port = ((Long)details.get("udp")).intValue();
/*      */                     
/* 2118 */                     buddy.setCachedStatus(ip, tcp_port, udp_port);
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                 }
/*      */                 
/*      */ 
/* 2124 */                 logMessage("Loaded buddy " + buddy.getString());
/*      */                 
/* 2126 */                 this.buddies.add(buddy);
/*      */                 
/* 2128 */                 this.buddies_map.put(key, buddy);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2134 */       num_buddies = this.buddies.size();
/*      */       
/* 2136 */       for (BuddyPluginBuddy b : this.buddies)
/*      */       {
/* 2138 */         b.setInitialStatus(now, num_buddies);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String decodeString(byte[] bytes)
/*      */   {
/* 2147 */     if (bytes == null)
/*      */     {
/* 2149 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 2153 */       return new String(bytes, "UTF8");
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 2157 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void saveConfig()
/*      */   {
/* 2164 */     saveConfig(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void saveConfig(boolean force)
/*      */   {
/* 2171 */     synchronized (this)
/*      */     {
/* 2173 */       if ((this.config_dirty) || (force))
/*      */       {
/* 2175 */         List buddies_config = new ArrayList();
/*      */         
/* 2177 */         for (int i = 0; i < this.buddies.size(); i++)
/*      */         {
/* 2179 */           BuddyPluginBuddy buddy = (BuddyPluginBuddy)this.buddies.get(i);
/*      */           
/* 2181 */           if (buddy.isAuthorised())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 2186 */             Map map = new HashMap();
/*      */             
/* 2188 */             map.put("ct", new Long(buddy.getCreatedTime()));
/*      */             
/* 2190 */             map.put("pk", buddy.getPublicKey());
/*      */             
/* 2192 */             List ygm = buddy.getYGMMarkers();
/*      */             
/* 2194 */             if (ygm != null)
/*      */             {
/* 2196 */               map.put("ygm", ygm);
/*      */             }
/*      */             
/* 2199 */             String nick = buddy.getNickName();
/*      */             
/* 2201 */             if (nick != null)
/*      */             {
/* 2203 */               map.put("n", nick);
/*      */             }
/*      */             
/* 2206 */             map.put("ls", new Long(buddy.getLastStatusSeq()));
/*      */             
/* 2208 */             map.put("lo", new Long(buddy.getLastTimeOnline()));
/*      */             
/* 2210 */             map.put("ss", new Long(buddy.getSubsystem()));
/*      */             
/* 2212 */             map.put("v", new Long(buddy.getVersion()));
/*      */             
/* 2214 */             if (buddy.getLocalAuthorisedRSSTagsOrCategoriesAsString() != null) {
/* 2215 */               map.put("lc", buddy.getLocalAuthorisedRSSTagsOrCategoriesAsString());
/*      */             }
/*      */             
/* 2218 */             if (buddy.getRemoteAuthorisedRSSTagsOrCategoriesAsString() != null) {
/* 2219 */               map.put("rc", buddy.getRemoteAuthorisedRSSTagsOrCategoriesAsString());
/*      */             }
/*      */             
/* 2222 */             boolean connected = (buddy.isConnected()) || ((this.connected_at_close != null) && (this.connected_at_close.contains(buddy)));
/*      */             
/*      */ 
/*      */ 
/* 2226 */             if (connected)
/*      */             {
/* 2228 */               InetAddress ip = buddy.getIP();
/* 2229 */               int tcp_port = buddy.getTCPPort();
/* 2230 */               int udp_port = buddy.getUDPPort();
/*      */               
/* 2232 */               if (ip != null)
/*      */               {
/* 2234 */                 map.put("ip", ip.getAddress());
/* 2235 */                 map.put("tcp", new Long(tcp_port));
/* 2236 */                 map.put("udp", new Long(udp_port));
/*      */               }
/*      */             }
/*      */             
/* 2240 */             buddies_config.add(map);
/*      */           }
/*      */         }
/* 2243 */         Map map = new HashMap();
/*      */         
/* 2245 */         if (buddies_config.size() > 0)
/*      */         {
/* 2247 */           map.put("friends", buddies_config);
/*      */           
/* 2249 */           writeConfig(map);
/*      */         }
/*      */         else
/*      */         {
/* 2253 */           deleteConfig();
/*      */         }
/*      */         
/* 2256 */         this.config_dirty = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public BuddyPluginBuddy addBuddy(String key, int subsystem)
/*      */   {
/* 2267 */     if (!isClassicEnabled())
/*      */     {
/* 2269 */       setClassicEnabled(true);
/*      */     }
/*      */     
/* 2272 */     return addBuddy(key, subsystem, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BuddyPluginBuddy addBuddy(String key, int subsystem, boolean authorised)
/*      */   {
/* 2281 */     if ((key.length() == 0) || (!verifyPublicKey(key)))
/*      */     {
/* 2283 */       return null;
/*      */     }
/*      */     
/* 2286 */     BuddyPluginBuddy buddy_to_return = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2292 */     synchronized (this)
/*      */     {
/* 2294 */       for (int i = 0; i < this.buddies.size(); i++)
/*      */       {
/* 2296 */         BuddyPluginBuddy buddy = (BuddyPluginBuddy)this.buddies.get(i);
/*      */         
/* 2298 */         if (buddy.getPublicKey().equals(key))
/*      */         {
/* 2300 */           if (buddy.getSubsystem() != subsystem)
/*      */           {
/* 2302 */             log("Buddy " + buddy.getString() + ": subsystem changed from " + buddy.getSubsystem() + " to " + subsystem);
/*      */             
/* 2304 */             buddy.setSubsystem(subsystem);
/*      */             
/* 2306 */             saveConfig(true);
/*      */           }
/*      */           
/* 2309 */           if ((authorised) && (!buddy.isAuthorised()))
/*      */           {
/* 2311 */             log("Buddy " + buddy.getString() + ": no authorised");
/*      */             
/* 2313 */             buddy.setAuthorised(true);
/*      */             
/* 2315 */             buddy_to_return = buddy;
/*      */           }
/*      */           else
/*      */           {
/* 2319 */             return buddy;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2324 */       if (buddy_to_return == null)
/*      */       {
/* 2326 */         buddy_to_return = new BuddyPluginBuddy(this, SystemTime.getCurrentTime(), subsystem, authorised, key, null, 2, null, null, 0, 0L, null);
/*      */         
/*      */ 
/* 2329 */         this.buddies.add(buddy_to_return);
/*      */         
/* 2331 */         this.buddies_map.put(key, buddy_to_return);
/*      */         
/* 2333 */         if (!authorised)
/*      */         {
/* 2335 */           log("Added unauthorised buddy: " + buddy_to_return.getString());
/*      */         }
/*      */       }
/*      */       
/* 2339 */       if (buddy_to_return.isAuthorised())
/*      */       {
/* 2341 */         logMessage("Added buddy " + buddy_to_return.getString());
/*      */         
/* 2343 */         saveConfig(true);
/*      */       }
/*      */     }
/*      */     
/* 2347 */     fireAdded(buddy_to_return);
/*      */     
/* 2349 */     return buddy_to_return;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeBuddy(BuddyPluginBuddy buddy)
/*      */   {
/* 2356 */     synchronized (this)
/*      */     {
/* 2358 */       if (!this.buddies.remove(buddy))
/*      */       {
/* 2360 */         return;
/*      */       }
/*      */       
/* 2363 */       this.buddies_map.remove(buddy.getPublicKey());
/*      */       
/* 2365 */       logMessage("Removed friend " + buddy.getString());
/*      */       
/* 2367 */       saveConfig(true);
/*      */     }
/*      */     
/* 2370 */     buddy.destroy();
/*      */     
/* 2372 */     fireRemoved(buddy);
/*      */   }
/*      */   
/*      */ 
/*      */   protected Map readConfig()
/*      */   {
/* 2378 */     File config_file = new File(this.plugin_interface.getUtilities().getAzureusUserDir(), "friends.config");
/*      */     
/* 2380 */     return readConfigFile(config_file);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void writeConfig(Map map)
/*      */   {
/* 2387 */     File config_file = new File(this.plugin_interface.getUtilities().getAzureusUserDir(), "friends.config");
/*      */     
/* 2389 */     writeConfigFile(config_file, map);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void deleteConfig()
/*      */   {
/* 2395 */     Utilities utils = this.plugin_interface.getUtilities();
/*      */     
/* 2397 */     File config_file = new File(utils.getAzureusUserDir(), "friends.config");
/*      */     
/* 2399 */     utils.deleteResilientBEncodedFile(config_file.getParentFile(), config_file.getName(), true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map readConfigFile(File name)
/*      */   {
/* 2408 */     Utilities utils = this.plugin_interface.getUtilities();
/*      */     
/* 2410 */     Map map = utils.readResilientBEncodedFile(name.getParentFile(), name.getName(), true);
/*      */     
/*      */ 
/* 2413 */     if (map == null)
/*      */     {
/* 2415 */       map = new HashMap();
/*      */     }
/*      */     
/* 2418 */     return map;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean writeConfigFile(File name, Map data)
/*      */   {
/* 2426 */     Utilities utils = this.plugin_interface.getUtilities();
/*      */     
/* 2428 */     utils.writeResilientBEncodedFile(name.getParentFile(), name.getName(), data, true);
/*      */     
/*      */ 
/* 2431 */     return name.exists();
/*      */   }
/*      */   
/*      */ 
/*      */   protected File getBuddyConfigDir()
/*      */   {
/* 2437 */     return new File(this.plugin_interface.getUtilities().getAzureusUserDir(), "friends");
/*      */   }
/*      */   
/*      */ 
/*      */   public BuddyPluginAZ2 getAZ2Handler()
/*      */   {
/* 2443 */     return this.az2_handler;
/*      */   }
/*      */   
/*      */   public String getPublicKey()
/*      */   {
/*      */     try
/*      */     {
/* 2450 */       return Base32.encode(this.ecc_handler.getPublicKey("Friend get key"));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2454 */       logMessage("Failed to access public key", e);
/*      */     }
/* 2456 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean verifyPublicKey(String key)
/*      */   {
/* 2464 */     return this.ecc_handler.verifyPublicKey(Base32.decode(key));
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkBuddiesAndRepublish()
/*      */   {
/* 2470 */     updateBuddys();
/*      */     
/* 2472 */     this.plugin_interface.getUtilities().createTimer("Buddy checker").addPeriodicEvent(10000L, new org.gudy.azureus2.plugins.utils.UTTimerEventPerformer()
/*      */     {
/*      */       int tick_count;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(UTTimerEvent event)
/*      */       {
/* 2482 */         this.tick_count += 1;
/*      */         
/* 2484 */         if (!BuddyPlugin.this.isClassicEnabled())
/*      */         {
/* 2486 */           return;
/*      */         }
/*      */         
/* 2489 */         BuddyPlugin.this.updateBuddys();
/*      */         
/* 2491 */         if (this.tick_count % 60 == 0)
/*      */         {
/* 2493 */           if (BuddyPlugin.this.latest_publish.isEnabled())
/*      */           {
/* 2495 */             BuddyPlugin.this.updatePublish(BuddyPlugin.this.latest_publish);
/*      */           }
/*      */         }
/*      */         
/* 2499 */         if (this.tick_count % 30 == 0)
/*      */         {
/* 2501 */           BuddyPlugin.this.checkMessagePending(this.tick_count);
/*      */         }
/*      */         
/* 2504 */         if (this.tick_count % 6 == 0)
/*      */         {
/* 2506 */           BuddyPlugin.this.checkUnauthBloom();
/*      */         }
/*      */         
/* 2509 */         if (this.tick_count % 6 == 0)
/*      */         {
/* 2511 */           BuddyPlugin.this.saveConfig();
/*      */         }
/*      */         
/* 2514 */         if (this.tick_count % 6 == 0)
/*      */         {
/* 2516 */           BuddyPlugin.this.checkPersistentDispatch();
/*      */         }
/*      */         
/* 2519 */         if (BuddyPlugin.this.buddy_tracker != null)
/*      */         {
/* 2521 */           BuddyPlugin.this.buddy_tracker.tick(this.tick_count);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updateBuddys()
/*      */   {
/*      */     List buddies_copy;
/*      */     
/* 2532 */     synchronized (this)
/*      */     {
/* 2534 */       buddies_copy = new ArrayList(this.buddies);
/*      */     }
/*      */     
/* 2537 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 2539 */     Random random = new Random();
/*      */     
/* 2541 */     for (int i = 0; i < buddies_copy.size(); i++)
/*      */     {
/* 2543 */       BuddyPluginBuddy buddy = (BuddyPluginBuddy)buddies_copy.get(i);
/*      */       
/* 2545 */       long last_check = buddy.getLastStatusCheckTime();
/*      */       
/* 2547 */       buddy.checkTimeouts();
/*      */       
/* 2549 */       int period = 180000 + 60000 * buddies_copy.size() / 5;
/*      */       
/*      */ 
/*      */ 
/* 2553 */       period += random.nextInt(120000);
/*      */       
/*      */ 
/*      */ 
/* 2557 */       if (now - last_check > period)
/*      */       {
/* 2559 */         if (!buddy.statusCheckActive())
/*      */         {
/* 2561 */           if (buddy.isAuthorised())
/*      */           {
/* 2563 */             updateBuddyStatus(buddy);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2571 */     synchronized (this)
/*      */     {
/* 2573 */       for (int i = 0; i < buddies_copy.size(); i++)
/*      */       {
/* 2575 */         BuddyPluginBuddy buddy = (BuddyPluginBuddy)buddies_copy.get(i);
/*      */         
/* 2577 */         if ((buddy.isIdle()) && (!buddy.isAuthorised()))
/*      */         {
/* 2579 */           removeBuddy(buddy);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateBuddyStatus(final BuddyPluginBuddy buddy)
/*      */   {
/* 2589 */     if (!buddy.statusCheckStarts())
/*      */     {
/* 2591 */       return;
/*      */     }
/*      */     
/* 2594 */     log("Updating buddy status: " + buddy.getString());
/*      */     try
/*      */     {
/* 2597 */       final byte[] public_key = buddy.getRawPublicKey();
/*      */       
/* 2599 */       DistributedDatabaseKey key = getStatusKey(public_key, "Friend status check for " + buddy.getName());
/*      */       
/*      */ 
/* 2602 */       this.ddb.read(new DistributedDatabaseListener()
/*      */       {
/*      */         private long latest_time;
/*      */         
/*      */ 
/*      */         private Map status;
/*      */         
/*      */ 
/*      */         public void event(DistributedDatabaseEvent event)
/*      */         {
/* 2612 */           int type = event.getType();
/*      */           
/* 2614 */           if (type == 2) {
/*      */             try
/*      */             {
/* 2617 */               DistributedDatabaseValue value = event.getValue();
/*      */               
/* 2619 */               long time = value.getCreationTime();
/*      */               
/* 2621 */               if (time > this.latest_time)
/*      */               {
/* 2623 */                 byte[] signed_stuff = (byte[])value.getValue(byte[].class);
/*      */                 
/* 2625 */                 Map new_status = BuddyPlugin.this.verifyAndExtract(signed_stuff, public_key);
/*      */                 
/* 2627 */                 if (new_status != null)
/*      */                 {
/* 2629 */                   this.status = new_status;
/*      */                   
/* 2631 */                   this.latest_time = time;
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 2636 */               BuddyPlugin.this.log("Read failed", e);
/*      */             }
/* 2638 */           } else if ((type == 5) || (type == 4))
/*      */           {
/*      */ 
/* 2641 */             if (this.status == null)
/*      */             {
/* 2643 */               buddy.statusCheckFailed();
/*      */             }
/*      */             else
/*      */               try
/*      */               {
/* 2648 */                 Long l_tcp_port = (Long)this.status.get("t");
/* 2649 */                 Long l_udp_port = (Long)this.status.get("u");
/*      */                 
/* 2651 */                 int tcp_port = l_tcp_port == null ? 0 : l_tcp_port.intValue();
/* 2652 */                 int udp_port = l_udp_port == null ? 0 : l_udp_port.intValue();
/*      */                 
/* 2654 */                 InetAddress ip = InetAddress.getByAddress((byte[])this.status.get("i"));
/*      */                 
/* 2656 */                 String nick = BuddyPlugin.this.decodeString((byte[])this.status.get("n"));
/*      */                 
/* 2658 */                 Long l_seq = (Long)this.status.get("s");
/*      */                 
/* 2660 */                 int seq = l_seq == null ? 0 : l_seq.intValue();
/*      */                 
/* 2662 */                 Long l_os = (Long)this.status.get("o");
/*      */                 
/* 2664 */                 int os = l_os == null ? 0 : l_os.intValue();
/*      */                 
/* 2666 */                 Long l_ver = (Long)this.status.get("v");
/*      */                 
/* 2668 */                 int ver = l_ver == null ? 1 : l_ver.intValue();
/*      */                 
/* 2670 */                 buddy.statusCheckComplete(this.latest_time, ip, tcp_port, udp_port, nick, os, seq, ver);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 2674 */                 buddy.statusCheckFailed();
/*      */                 
/* 2676 */                 BuddyPlugin.this.log("Status decode failed", e); } } } }, key, 120000L);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2687 */       buddy.statusCheckFailed();
/*      */       
/* 2689 */       log("Friend status update failed: " + buddy.getString(), e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map verifyAndExtract(byte[] signed_stuff, byte[] public_key)
/*      */     throws BuddyPluginException
/*      */   {
/* 2700 */     int signature_length = signed_stuff[0] & 0xFF;
/*      */     
/* 2702 */     byte[] signature = new byte[signature_length];
/* 2703 */     byte[] data = new byte[signed_stuff.length - 1 - signature_length];
/*      */     
/* 2705 */     System.arraycopy(signed_stuff, 1, signature, 0, signature_length);
/* 2706 */     System.arraycopy(signed_stuff, 1 + signature_length, data, 0, data.length);
/*      */     try
/*      */     {
/* 2709 */       if (this.ecc_handler.verify(public_key, data, signature))
/*      */       {
/* 2711 */         return BDecoder.decode(data);
/*      */       }
/*      */       
/*      */ 
/* 2715 */       logMessage("Signature verification failed");
/*      */       
/* 2717 */       return null;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2721 */       rethrow("Verification failed", e);
/*      */     }
/* 2723 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] signAndInsert(Map plain_stuff, String reason)
/*      */     throws BuddyPluginException
/*      */   {
/*      */     try
/*      */     {
/* 2735 */       byte[] data = BEncoder.encode(plain_stuff);
/*      */       
/* 2737 */       byte[] signature = this.ecc_handler.sign(data, reason);
/*      */       
/* 2739 */       byte[] signed_payload = new byte[1 + signature.length + data.length];
/*      */       
/* 2741 */       signed_payload[0] = ((byte)signature.length);
/*      */       
/* 2743 */       System.arraycopy(signature, 0, signed_payload, 1, signature.length);
/* 2744 */       System.arraycopy(data, 0, signed_payload, 1 + signature.length, data.length);
/*      */       
/* 2746 */       return signed_payload;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2750 */       rethrow("Signing failed", e);
/*      */     }
/* 2752 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean verify(String pk, byte[] payload, byte[] signature)
/*      */     throws BuddyPluginException
/*      */   {
/* 2764 */     return verify(Base32.decode(pk), payload, signature);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean verify(BuddyPluginBuddy buddy, byte[] payload, byte[] signature)
/*      */     throws BuddyPluginException
/*      */   {
/* 2775 */     return verify(buddy.getRawPublicKey(), payload, signature);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean verify(byte[] pk, byte[] payload, byte[] signature)
/*      */     throws BuddyPluginException
/*      */   {
/*      */     try
/*      */     {
/* 2788 */       return this.ecc_handler.verify(pk, payload, signature);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2792 */       rethrow("Verification failed", e);
/*      */     }
/* 2794 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] sign(byte[] payload)
/*      */     throws BuddyPluginException
/*      */   {
/*      */     try
/*      */     {
/* 2806 */       return this.ecc_handler.sign(payload, "Friend message signing");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2810 */       rethrow("Signing failed", e);
/*      */     }
/* 2812 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected cryptoResult encrypt(BuddyPluginBuddy buddy, byte[] payload)
/*      */     throws BuddyPluginException
/*      */   {
/* 2823 */     return encrypt(buddy.getPublicKey(), payload, buddy.getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public cryptoResult encrypt(String pk, byte[] payload, String forWho)
/*      */     throws BuddyPluginException
/*      */   {
/*      */     try
/*      */     {
/* 2836 */       byte[] hash = new byte[20];
/*      */       
/* 2838 */       this.random.nextBytes(hash);
/*      */       
/* 2840 */       Map content = new HashMap();
/*      */       
/* 2842 */       content.put("h", hash);
/* 2843 */       content.put("p", payload);
/*      */       
/* 2845 */       final byte[] encrypted = this.ecc_handler.encrypt(Base32.decode(pk), BEncoder.encode(content), "Encrypting message for " + forWho);
/*      */       
/* 2847 */       final byte[] sha1_hash = new SHA1Simple().calculateHash(hash);
/*      */       
/* 2849 */       new cryptoResult()
/*      */       {
/*      */ 
/*      */         public byte[] getChallenge()
/*      */         {
/*      */ 
/* 2855 */           return sha1_hash;
/*      */         }
/*      */         
/*      */ 
/*      */         public byte[] getPayload()
/*      */         {
/* 2861 */           return encrypted;
/*      */         }
/*      */       };
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2867 */       rethrow("Encryption failed", e);
/*      */     }
/* 2869 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected cryptoResult decrypt(BuddyPluginBuddy buddy, byte[] content, String forName)
/*      */     throws BuddyPluginException
/*      */   {
/*      */     try
/*      */     {
/* 2883 */       byte[] decrypted = this.ecc_handler.decrypt(buddy.getRawPublicKey(), content, "Decrypting message for " + buddy.getName());
/*      */       
/* 2885 */       final Map map = BDecoder.decode(decrypted);
/*      */       
/* 2887 */       new cryptoResult()
/*      */       {
/*      */ 
/*      */         public byte[] getChallenge()
/*      */         {
/*      */ 
/* 2893 */           return (byte[])map.get("h");
/*      */         }
/*      */         
/*      */ 
/*      */         public byte[] getPayload()
/*      */         {
/* 2899 */           return (byte[])map.get("p");
/*      */         }
/*      */       };
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2905 */       rethrow("Decryption failed", e);
/*      */     }
/* 2907 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public cryptoResult decrypt(String public_key, byte[] content)
/*      */     throws BuddyPluginException
/*      */   {
/*      */     try
/*      */     {
/* 2920 */       byte[] decrypted = this.ecc_handler.decrypt(Base32.decode(public_key), content, "Decrypting message for " + public_key);
/*      */       
/* 2922 */       final Map map = BDecoder.decode(decrypted);
/*      */       
/* 2924 */       new cryptoResult()
/*      */       {
/*      */ 
/*      */         public byte[] getChallenge()
/*      */         {
/*      */ 
/* 2930 */           return (byte[])map.get("h");
/*      */         }
/*      */         
/*      */ 
/*      */         public byte[] getPayload()
/*      */         {
/* 2936 */           return (byte[])map.get("p");
/*      */         }
/*      */       };
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2942 */       rethrow("Decryption failed", e);
/*      */     }
/* 2944 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setMessagePending(BuddyPluginBuddy buddy, final operationListener listener)
/*      */     throws BuddyPluginException
/*      */   {
/*      */     try
/*      */     {
/* 2956 */       checkAvailable();
/*      */       
/* 2958 */       final String reason = "Friend YGM write for " + buddy.getName();
/*      */       
/* 2960 */       Map payload = new HashMap();
/*      */       
/* 2962 */       payload.put("r", new Long(this.random.nextLong()));
/*      */       
/* 2964 */       byte[] signed_payload = signAndInsert(payload, reason);
/*      */       
/* 2966 */       Map envelope = new HashMap();
/*      */       
/* 2968 */       envelope.put("pk", this.ecc_handler.getPublicKey(reason));
/* 2969 */       envelope.put("ss", signed_payload);
/*      */       
/* 2971 */       DistributedDatabaseValue value = this.ddb.createValue(BEncoder.encode(envelope));
/*      */       
/* 2973 */       logMessage(reason + " starts: " + payload);
/*      */       
/* 2975 */       DistributedDatabaseKey key = getYGMKey(buddy.getRawPublicKey(), reason);
/*      */       
/* 2977 */       this.ddb.write(new DistributedDatabaseListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void event(DistributedDatabaseEvent event)
/*      */         {
/*      */ 
/* 2984 */           int type = event.getType();
/*      */           
/* 2986 */           if ((type == 5) || (type == 4))
/*      */           {
/*      */ 
/* 2989 */             BuddyPlugin.this.logMessage(reason + " complete");
/*      */             
/* 2991 */             listener.complete(); } } }, key, value);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/* 3001 */         rethrow("Failed to publish YGM", e);
/*      */       }
/*      */       finally
/*      */       {
/* 3005 */         listener.complete();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void checkMessagePending(int tick_count)
/*      */   {
/* 3014 */     log("Checking YGM");
/*      */     
/* 3016 */     if (tick_count % 360 == 0)
/*      */     {
/* 3018 */       synchronized (this)
/*      */       {
/* 3020 */         this.ygm_unauth_bloom = null;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 3025 */       String reason = "Friend YGM check";
/*      */       
/* 3027 */       byte[] public_key = this.ecc_handler.getPublicKey(reason);
/*      */       
/* 3029 */       DistributedDatabaseKey key = getYGMKey(public_key, reason);
/*      */       
/* 3031 */       this.ddb.read(new DistributedDatabaseListener()
/*      */       {
/*      */ 
/* 3034 */         private List new_ygm_buddies = new ArrayList();
/* 3035 */         private boolean unauth_permitted = false;
/*      */         
/*      */ 
/*      */ 
/*      */         public void event(DistributedDatabaseEvent event)
/*      */         {
/* 3041 */           int type = event.getType();
/*      */           
/* 3043 */           if (type == 2)
/*      */             try
/*      */             {
/* 3046 */               DistributedDatabaseValue value = event.getValue();
/*      */               
/* 3048 */               byte[] envelope = (byte[])value.getValue(byte[].class);
/*      */               
/* 3050 */               Map map = BDecoder.decode(envelope);
/*      */               
/* 3052 */               byte[] pk = (byte[])map.get("pk");
/*      */               
/* 3054 */               if (pk == null)
/*      */               {
/* 3056 */                 return;
/*      */               }
/*      */               
/* 3059 */               String pk_str = Base32.encode(pk);
/*      */               
/* 3061 */               BuddyPluginBuddy buddy = BuddyPlugin.this.getBuddyFromPublicKey(pk_str);
/*      */               
/* 3063 */               if ((buddy == null) || (!buddy.isAuthorised()))
/*      */               {
/* 3065 */                 if (buddy == null)
/*      */                 {
/* 3067 */                   BuddyPlugin.this.log("YGM entry from unknown friend '" + pk_str + "' - ignoring");
/*      */                 }
/*      */                 else
/*      */                 {
/* 3071 */                   BuddyPlugin.this.log("YGM entry from unauthorised friend '" + pk_str + "' - ignoring");
/*      */                 }
/*      */                 
/* 3074 */                 byte[] address = event.getContact().getAddress().getAddress().getAddress();
/*      */                 
/* 3076 */                 synchronized (BuddyPlugin.this)
/*      */                 {
/* 3078 */                   if (BuddyPlugin.this.ygm_unauth_bloom == null)
/*      */                   {
/* 3080 */                     BuddyPlugin.this.ygm_unauth_bloom = BloomFilterFactory.createAddOnly(512);
/*      */                   }
/*      */                   
/* 3083 */                   if (!BuddyPlugin.this.ygm_unauth_bloom.contains(address))
/*      */                   {
/* 3085 */                     BuddyPlugin.this.ygm_unauth_bloom.add(address);
/*      */                     
/* 3087 */                     this.unauth_permitted = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               else {
/* 3092 */                 byte[] signed_stuff = (byte[])map.get("ss");
/*      */                 
/* 3094 */                 Map payload = BuddyPlugin.this.verifyAndExtract(signed_stuff, pk);
/*      */                 
/* 3096 */                 if (payload != null)
/*      */                 {
/* 3098 */                   long rand = ((Long)payload.get("r")).longValue();
/*      */                   
/* 3100 */                   if (buddy.addYGMMarker(rand))
/*      */                   {
/* 3102 */                     this.new_ygm_buddies.add(buddy);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 3108 */               BuddyPlugin.this.log("Read failed", e);
/*      */             }
/* 3110 */           if ((type == 5) || (type == 4))
/*      */           {
/*      */ 
/* 3113 */             if ((this.new_ygm_buddies.size() > 0) || (this.unauth_permitted))
/*      */             {
/* 3115 */               BuddyPluginBuddy[] b = new BuddyPluginBuddy[this.new_ygm_buddies.size()];
/*      */               
/* 3117 */               this.new_ygm_buddies.toArray(b);
/*      */               
/* 3119 */               BuddyPlugin.this.fireYGM(b); } } } }, key, 120000L, 1);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3128 */       boolean write_bogus_ygm = false;
/*      */       
/* 3130 */       synchronized (this)
/*      */       {
/* 3132 */         if (!this.bogus_ygm_written)
/*      */         {
/* 3134 */           this.bogus_ygm_written = (write_bogus_ygm = 1);
/*      */         }
/*      */       }
/*      */       
/* 3138 */       if (write_bogus_ygm)
/*      */       {
/* 3140 */         String reason2 = "Friend YGM write for myself";
/*      */         
/* 3142 */         Object envelope = new HashMap();
/*      */         
/* 3144 */         DistributedDatabaseValue value = this.ddb.createValue(BEncoder.encode((Map)envelope));
/*      */         
/* 3146 */         logMessage("Friend YGM write for myself starts");
/*      */         
/* 3148 */         this.ddb.write(new DistributedDatabaseListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void event(DistributedDatabaseEvent event)
/*      */           {
/*      */ 
/* 3155 */             int type = event.getType();
/*      */             
/* 3157 */             if (type == 4)
/*      */             {
/* 3159 */               BuddyPlugin.this.logMessage("Friend YGM write for myself complete"); } } }, key, value);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 3169 */       logMessage("YGM check failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public BuddyPluginBuddy getBuddyFromPublicKey(String key)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_2
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 2070	com/aelitis/azureus/plugins/net/buddy/BuddyPlugin:buddies_map	Ljava/util/Map;
/*      */     //   8: aload_1
/*      */     //   9: invokeinterface 2397 2 0
/*      */     //   14: checkcast 1268	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy
/*      */     //   17: aload_2
/*      */     //   18: monitorexit
/*      */     //   19: areturn
/*      */     //   20: astore_3
/*      */     //   21: aload_2
/*      */     //   22: monitorexit
/*      */     //   23: aload_3
/*      */     //   24: athrow
/*      */     // Line number table:
/*      */     //   Java source line #3177	-> byte code offset #0
/*      */     //   Java source line #3179	-> byte code offset #4
/*      */     //   Java source line #3180	-> byte code offset #20
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	25	0	this	BuddyPlugin
/*      */     //   0	25	1	key	String
/*      */     //   2	20	2	Ljava/lang/Object;	Object
/*      */     //   20	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	19	20	finally
/*      */     //   20	23	20	finally
/*      */   }
/*      */   
/*      */   public PluginInterface getPluginInterface()
/*      */   {
/* 3186 */     return this.plugin_interface;
/*      */   }
/*      */   
/*      */ 
/*      */   protected SESecurityManager getSecurityManager()
/*      */   {
/* 3192 */     return this.sec_man;
/*      */   }
/*      */   
/*      */ 
/*      */   protected GenericMessageRegistration getMessageRegistration()
/*      */   {
/* 3198 */     return this.msg_registration;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<BuddyPluginBuddy> getBuddies()
/*      */   {
/* 3208 */     synchronized (this)
/*      */     {
/* 3210 */       List<BuddyPluginBuddy> result = new ArrayList();
/*      */       
/* 3212 */       for (int i = 0; i < this.buddies.size(); i++)
/*      */       {
/* 3214 */         BuddyPluginBuddy buddy = (BuddyPluginBuddy)this.buddies.get(i);
/*      */         
/* 3216 */         if (buddy.isAuthorised())
/*      */         {
/* 3218 */           result.add(buddy);
/*      */         }
/*      */       }
/*      */       
/* 3222 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected List<BuddyPluginBuddy> getAllBuddies()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: new 1292	java/util/ArrayList
/*      */     //   7: dup
/*      */     //   8: aload_0
/*      */     //   9: getfield 2066	com/aelitis/azureus/plugins/net/buddy/BuddyPlugin:buddies	Ljava/util/List;
/*      */     //   12: invokespecial 2313	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: areturn
/*      */     //   18: astore_2
/*      */     //   19: aload_1
/*      */     //   20: monitorexit
/*      */     //   21: aload_2
/*      */     //   22: athrow
/*      */     // Line number table:
/*      */     //   Java source line #3229	-> byte code offset #0
/*      */     //   Java source line #3231	-> byte code offset #4
/*      */     //   Java source line #3232	-> byte code offset #18
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	23	0	this	BuddyPlugin
/*      */     //   2	18	1	Ljava/lang/Object;	Object
/*      */     //   18	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	17	18	finally
/*      */     //   18	21	18	finally
/*      */   }
/*      */   
/*      */   public boolean isAvailable()
/*      */   {
/*      */     try
/*      */     {
/* 3239 */       checkAvailable();
/*      */       
/* 3241 */       return true;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 3245 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkAvailable()
/*      */     throws BuddyPluginException
/*      */   {
/* 3254 */     if (this.initialisation_state == 0)
/*      */     {
/* 3256 */       throw new BuddyPluginException("Plugin not yet initialised");
/*      */     }
/* 3258 */     if (this.initialisation_state == 2)
/*      */     {
/* 3260 */       throw new BuddyPluginException("Plugin unavailable");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fireClassicInitialised(boolean ok)
/*      */   {
/* 3270 */     if (ok)
/*      */     {
/* 3272 */       this.initialisation_state = 1;
/*      */     }
/*      */     else
/*      */     {
/* 3276 */       this.initialisation_state = 2;
/*      */     }
/*      */     
/* 3279 */     persistentDispatchInit();
/*      */     
/* 3281 */     if (ok)
/*      */     {
/* 3283 */       this.buddy_tracker.initialise();
/*      */     }
/*      */     
/* 3286 */     List listeners_ref = this.listeners.getList();
/*      */     
/* 3288 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*      */       try
/*      */       {
/* 3291 */         ((BuddyPluginListener)listeners_ref.get(i)).initialised(ok);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3295 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(BuddyPluginListener listener)
/*      */   {
/* 3304 */     if (this.listeners.contains(listener)) {
/* 3305 */       return;
/*      */     }
/*      */     
/* 3308 */     this.listeners.add(listener);
/*      */     
/* 3310 */     if (this.initialisation_state != 0)
/*      */     {
/* 3312 */       listener.initialised(this.initialisation_state == 1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(BuddyPluginListener listener)
/*      */   {
/* 3320 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map requestReceived(BuddyPluginBuddy from_buddy, int subsystem, Map content)
/*      */     throws BuddyPluginException
/*      */   {
/* 3331 */     List listeners_ref = this.request_listeners.getList();
/*      */     
/* 3333 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*      */       try
/*      */       {
/* 3336 */         Map reply = ((BuddyPluginBuddyRequestListener)listeners_ref.get(i)).requestReceived(from_buddy, subsystem, content);
/*      */         
/* 3338 */         if (reply != null)
/*      */         {
/* 3340 */           return reply;
/*      */         }
/*      */       }
/*      */       catch (BuddyPluginException e) {
/* 3344 */         throw e;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3348 */         Debug.printStackTrace(e);
/*      */         
/* 3350 */         throw new BuddyPluginException("Request processing failed", e);
/*      */       }
/*      */     }
/*      */     
/* 3354 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireAdded(BuddyPluginBuddy buddy)
/*      */   {
/* 3361 */     if (buddy.isAuthorised())
/*      */     {
/* 3363 */       buddy.setLocalAuthorisedRSSTagsOrCategories(this.public_tags_or_categories);
/*      */       
/* 3365 */       List listeners_ref = this.listeners.getList();
/*      */       
/* 3367 */       for (int i = 0; i < listeners_ref.size(); i++) {
/*      */         try
/*      */         {
/* 3370 */           ((BuddyPluginListener)listeners_ref.get(i)).buddyAdded(buddy);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3374 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireRemoved(BuddyPluginBuddy buddy)
/*      */   {
/* 3384 */     if (buddy.isAuthorised())
/*      */     {
/* 3386 */       List listeners_ref = this.listeners.getList();
/*      */       
/* 3388 */       for (int i = 0; i < listeners_ref.size(); i++) {
/*      */         try
/*      */         {
/* 3391 */           ((BuddyPluginListener)listeners_ref.get(i)).buddyRemoved(buddy);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3395 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireDetailsChanged(BuddyPluginBuddy buddy)
/*      */   {
/* 3405 */     if (buddy.isAuthorised())
/*      */     {
/* 3407 */       List listeners_ref = this.listeners.getList();
/*      */       
/* 3409 */       for (int i = 0; i < listeners_ref.size(); i++) {
/*      */         try
/*      */         {
/* 3412 */           ((BuddyPluginListener)listeners_ref.get(i)).buddyChanged(buddy);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3416 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireYGM(BuddyPluginBuddy[] from_buddies)
/*      */   {
/* 3426 */     List listeners_ref = this.request_listeners.getList();
/*      */     
/* 3428 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*      */       try
/*      */       {
/* 3431 */         ((BuddyPluginBuddyRequestListener)listeners_ref.get(i)).pendingMessages(from_buddies);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3435 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void fireEnabledStateChanged()
/*      */   {
/* 3443 */     boolean enabled = (!this.plugin_interface.getPluginState().isDisabled()) && (isClassicEnabled());
/*      */     
/* 3445 */     List listeners_ref = this.listeners.getList();
/*      */     
/* 3447 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*      */       try
/*      */       {
/* 3450 */         ((BuddyPluginListener)listeners_ref.get(i)).enabledStateChanged(enabled);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3454 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void fireUpdated()
/*      */   {
/* 3462 */     for (BuddyPluginListener listener : this.listeners) {
/*      */       try
/*      */       {
/* 3465 */         listener.updated();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3469 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected BuddyPluginViewInterface getSWTUI()
/*      */   {
/* 3477 */     return this.swt_ui;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void rethrow(String reason, Throwable e)
/*      */     throws BuddyPluginException
/*      */   {
/* 3487 */     logMessage(reason, e);
/*      */     
/* 3489 */     if ((e instanceof CryptoManagerPasswordException))
/*      */     {
/*      */ 
/* 3492 */       throw new BuddyPluginPasswordException(((CryptoManagerPasswordException)e).wasIncorrect(), reason, e);
/*      */     }
/*      */     
/*      */ 
/* 3496 */     throw new BuddyPluginException(reason, e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public InputStream handleURLProtocol(AZPluginConnection connection, String arg_str)
/*      */     throws IPCException
/*      */   {
/* 3507 */     if (arg_str.toLowerCase(Locale.US).startsWith("chat:"))
/*      */     {
/*      */ 
/*      */ 
/* 3511 */       if (!this.beta_enabled_param.getValue())
/*      */       {
/* 3513 */         throw new IPCException("Decentralized chat not enabled");
/*      */       }
/*      */       try
/*      */       {
/* 3517 */         InputStream result = this.beta_plugin.handleURI(arg_str, false);
/*      */         
/* 3519 */         if (result != null)
/*      */         {
/* 3521 */           return result;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 3526 */         return new ByteArrayInputStream(VuzeFileHandler.getSingleton().create().exportToBytes());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3530 */         throw new IPCException(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3535 */     String[] args = arg_str.split("&");
/*      */     
/* 3537 */     String pk = null;
/* 3538 */     String category_or_tag = "All";
/* 3539 */     byte[] hash = null;
/*      */     
/* 3541 */     for (String arg : args)
/*      */     {
/* 3543 */       String[] bits = arg.split("=");
/*      */       
/* 3545 */       String lhs = bits[0];
/* 3546 */       String rhs = org.gudy.azureus2.core3.util.UrlUtils.decode(bits[1]);
/*      */       
/* 3548 */       if (lhs.equals("pk"))
/*      */       {
/* 3550 */         pk = rhs;
/*      */       }
/* 3552 */       else if (lhs.equals("cat"))
/*      */       {
/* 3554 */         category_or_tag = rhs;
/*      */       }
/* 3556 */       else if (lhs.equals("hash"))
/*      */       {
/* 3558 */         hash = Base32.decode(rhs);
/*      */       }
/*      */     }
/*      */     
/* 3562 */     if (pk == null)
/*      */     {
/* 3564 */       throw new IPCException("Public key missing from '" + arg_str + "'");
/*      */     }
/*      */     
/* 3567 */     BuddyPluginBuddy buddy = getBuddyFromPublicKey(pk);
/*      */     
/* 3569 */     if (buddy == null)
/*      */     {
/* 3571 */       throw new IPCException("Buddy with public key '" + pk + "' not found");
/*      */     }
/*      */     
/* 3574 */     if (hash == null)
/*      */     {
/* 3576 */       return handleUPRSS(connection, buddy, category_or_tag);
/*      */     }
/*      */     
/*      */ 
/* 3580 */     return handleUPTorrent(connection, buddy, category_or_tag, hash);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public InputStream handleUPRSS(final AZPluginConnection connection, BuddyPluginBuddy buddy, String tag_or_category)
/*      */     throws IPCException
/*      */   {
/* 3593 */     if (!buddy.isOnline(true))
/*      */     {
/* 3595 */       throw new IPCException("Buddy isn't online");
/*      */     }
/*      */     
/* 3598 */     Map<String, Object> msg = new HashMap();
/*      */     
/* 3600 */     final String if_mod = connection.getRequestProperty("If-Modified-Since");
/*      */     try
/*      */     {
/* 3603 */       msg.put("cat", tag_or_category.getBytes("UTF-8"));
/*      */       
/* 3605 */       if (if_mod != null)
/*      */       {
/* 3607 */         msg.put("if_mod", if_mod);
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3614 */       Debug.out(e);
/*      */     }
/*      */     
/* 3617 */     final Object[] result = { null };
/* 3618 */     final AESemaphore result_sem = new AESemaphore("BuddyPlugin:rss");
/*      */     
/* 3620 */     final String etag = buddy.getPublicKey() + "-" + tag_or_category;
/*      */     
/* 3622 */     this.az2_handler.sendAZ2RSSMessage(buddy, msg, new BuddyPluginAZ2TrackerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public Map messageReceived(BuddyPluginBuddy buddy, Map message)
/*      */       {
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/* 3633 */           byte[] bytes = (byte[])message.get("rss");
/*      */           
/* 3635 */           ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
/*      */           
/* 3637 */           result[0] = bais;
/*      */           
/* 3639 */           connection.setHeaderField("ETag", etag);
/*      */           
/* 3641 */           byte[] b_last_mod = (byte[])message.get("last_mod");
/*      */           
/* 3643 */           if (b_last_mod != null)
/*      */           {
/* 3645 */             String last_mod = new String(b_last_mod, "UTF-8");
/*      */             
/* 3647 */             connection.setHeaderField("Last-Modified", last_mod);
/*      */             
/* 3649 */             if ((if_mod != null) && (if_mod.equals(last_mod)) && (bytes.length == 0))
/*      */             {
/* 3651 */               connection.setResponse(304, "Not Modified");
/*      */             }
/*      */           }
/*      */           
/* 3655 */           result_sem.release();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3659 */           messageFailed(buddy, e);
/*      */         }
/*      */         
/* 3662 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageFailed(BuddyPluginBuddy buddy, Throwable cause)
/*      */       {
/* 3670 */         result[0] = new IPCException("Read failed", cause);
/*      */         
/* 3672 */         result_sem.release();
/*      */       }
/*      */       
/* 3675 */     });
/* 3676 */     result_sem.reserve(60000L);
/*      */     
/* 3678 */     if (result[0] == null)
/*      */     {
/* 3680 */       throw new IPCException("Timeout");
/*      */     }
/* 3682 */     if ((result[0] instanceof InputStream))
/*      */     {
/* 3684 */       return (InputStream)result[0];
/*      */     }
/*      */     
/*      */ 
/* 3688 */     throw ((IPCException)result[0]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public InputStream handleUPTorrent(AZPluginConnection connection, final BuddyPluginBuddy buddy, String tag_or_category, final byte[] hash)
/*      */     throws IPCException
/*      */   {
/* 3701 */     long timeout = 120000L;
/*      */     
/* 3703 */     final Object[] result = { null };
/* 3704 */     final AESemaphore result_sem = new AESemaphore("BuddyPlugin:upt");
/*      */     
/* 3706 */     log("Attempting to download torrent for " + Base32.encode(hash));
/*      */     
/*      */ 
/*      */ 
/* 3710 */     if (buddy.isOnline(true))
/*      */     {
/*      */       try
/*      */       {
/* 3714 */         Map<String, Object> msg = new HashMap();
/*      */         try
/*      */         {
/* 3717 */           msg.put("cat", tag_or_category.getBytes("UTF-8"));
/*      */           
/* 3719 */           msg.put("hash", hash);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3723 */           Debug.out(e);
/*      */         }
/*      */         
/* 3726 */         this.az2_handler.sendAZ2RSSMessage(buddy, msg, new BuddyPluginAZ2TrackerListener()
/*      */         {
/*      */           private boolean result_set;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public Map messageReceived(BuddyPluginBuddy buddy, Map message)
/*      */           {
/*      */             try
/*      */             {
/* 3739 */               byte[] bytes = (byte[])message.get("torrent");
/*      */               
/* 3741 */               BuddyPlugin.this.log("    torrent downloaded from buddy");
/*      */               
/* 3743 */               setResult(bytes);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 3747 */               messageFailed(buddy, e);
/*      */             }
/*      */             
/* 3750 */             return null;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void messageFailed(BuddyPluginBuddy buddy, Throwable cause)
/*      */           {
/* 3758 */             setResult(new IPCException("Read failed", cause));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           protected void setResult(Object obj)
/*      */           {
/* 3765 */             synchronized (result)
/*      */             {
/* 3767 */               if (this.result_set)
/*      */               {
/* 3769 */                 return;
/*      */               }
/*      */               
/* 3772 */               this.result_set = true;
/*      */               
/* 3774 */               if (!(result[0] instanceof byte[]))
/*      */               {
/* 3776 */                 result[0] = obj;
/*      */               }
/*      */               
/* 3779 */               result_sem.release();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       catch (Throwable e) {
/* 3785 */         result[0] = new IPCException("Buddy torrent get failed", e);
/*      */         
/* 3787 */         result_sem.release();
/*      */       }
/*      */     }
/*      */     else {
/* 3791 */       result[0] = new IPCException("Buddy is offline");
/*      */       
/* 3793 */       result_sem.release();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3798 */     final MagnetPlugin magnet_plugin = getMagnetPlugin();
/*      */     
/* 3800 */     if (magnet_plugin == null)
/*      */     {
/* 3802 */       synchronized (result)
/*      */       {
/* 3804 */         if (result[0] == null)
/*      */         {
/* 3806 */           result[0] = new IPCException("Magnet plugin unavailable");
/*      */         }
/*      */       }
/*      */       
/* 3810 */       result_sem.release();
/*      */     }
/*      */     else
/*      */     {
/* 3814 */       new AEThread2("BuddyPlugin:mag", true)
/*      */       {
/*      */         private boolean result_set;
/*      */         
/*      */ 
/*      */         public void run()
/*      */         {
/*      */           try
/*      */           {
/* 3823 */             if (buddy.isOnline(true))
/*      */             {
/* 3825 */               Thread.sleep(10000L);
/*      */             }
/*      */             
/* 3828 */             synchronized (result)
/*      */             {
/* 3830 */               if ((result[0] instanceof byte[]))
/*      */               {
/* 3832 */                 setResult(null);
/*      */                 
/* 3834 */                 return;
/*      */               }
/*      */             }
/*      */             
/* 3838 */             byte[] torrent_data = magnet_plugin.download(!BuddyPlugin.this.logger.isEnabled() ? null : new MagnetPluginProgressListener()
/*      */             {
/*      */               public void reportSize(long size) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void reportActivity(String str)
/*      */               {
/* 3851 */                 BuddyPlugin.this.log("    MagnetDownload: " + str);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public void reportCompleteness(int percent) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public void reportContributor(InetSocketAddress address) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public boolean verbose()
/*      */               {
/* 3869 */                 return false;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 3875 */               public boolean cancelled() { return false; } }, hash, "", new InetSocketAddress[0], 120000L, 0);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3884 */             if (torrent_data == null)
/*      */             {
/* 3886 */               setResult(new IPCException("Magnet timeout"));
/*      */             }
/*      */             else
/*      */             {
/* 3890 */               BuddyPlugin.this.log("    torrent downloaded from magnet");
/*      */               
/* 3892 */               setResult(torrent_data);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 3896 */             setResult(new IPCException("Magnet get failed", e));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         protected void setResult(Object obj)
/*      */         {
/* 3904 */           synchronized (result)
/*      */           {
/* 3906 */             if (this.result_set)
/*      */             {
/* 3908 */               return;
/*      */             }
/*      */             
/* 3911 */             this.result_set = true;
/*      */             
/* 3913 */             if (obj != null)
/*      */             {
/* 3915 */               if ((result[0] == null) || (((obj instanceof byte[])) && (!(result[0] instanceof byte[]))))
/*      */               {
/*      */ 
/* 3918 */                 result[0] = obj;
/*      */               }
/*      */             }
/*      */             
/* 3922 */             result_sem.release();
/*      */           }
/*      */         }
/*      */       }.start();
/*      */     }
/*      */     
/* 3928 */     long start = SystemTime.getMonotonousTime();
/*      */     
/* 3930 */     if (result_sem.reserve(120000L))
/*      */     {
/* 3932 */       if (!(result[0] instanceof byte[]))
/*      */       {
/* 3934 */         long rem = 120000L - (SystemTime.getMonotonousTime() - start);
/*      */         
/* 3936 */         if (rem > 0L)
/*      */         {
/* 3938 */           result_sem.reserve(rem);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3943 */     if (result[0] == null)
/*      */     {
/* 3945 */       log("    torrent download timeout");
/*      */       
/* 3947 */       throw new IPCException("Timeout");
/*      */     }
/* 3949 */     if ((result[0] instanceof byte[]))
/*      */     {
/* 3951 */       return new ByteArrayInputStream((byte[])result[0]);
/*      */     }
/*      */     
/*      */ 
/* 3955 */     IPCException error = (IPCException)result[0];
/*      */     
/* 3957 */     log("    torrent downloaded failed: " + Debug.getNestedExceptionMessage(error));
/*      */     
/* 3959 */     throw error;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected MagnetPlugin getMagnetPlugin()
/*      */   {
/* 3966 */     PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass(MagnetPlugin.class);
/*      */     
/* 3968 */     if (pi == null)
/*      */     {
/* 3970 */       return null;
/*      */     }
/*      */     
/* 3973 */     return (MagnetPlugin)pi.getPlugin();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public feedDetails getRSS(BuddyPluginBuddy buddy, String tag_or_category, String if_mod)
/*      */     throws BuddyPluginException
/*      */   {
/* 3984 */     if (!buddy.isLocalRSSTagOrCategoryAuthorised(tag_or_category))
/*      */     {
/* 3986 */       throw new BuddyPluginException("Unauthorised tag/category '" + tag_or_category + "'");
/*      */     }
/*      */     
/* 3989 */     buddy.localRSSTagOrCategoryRead(tag_or_category);
/*      */     
/* 3991 */     Download[] downloads = this.plugin_interface.getDownloadManager().getDownloads();
/*      */     
/* 3993 */     List<Download> selected_dls = new ArrayList();
/*      */     
/* 3995 */     long fingerprint = 0L;
/*      */     
/* 3997 */     for (int i = 0; i < downloads.length; i++)
/*      */     {
/* 3999 */       Download download = downloads[i];
/*      */       
/* 4001 */       Torrent torrent = download.getTorrent();
/*      */       
/* 4003 */       if (torrent != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 4008 */         boolean match = tag_or_category.equalsIgnoreCase("all");
/*      */         
/* 4010 */         if (!match)
/*      */         {
/* 4012 */           String dl_cat = download.getAttribute(this.ta_category);
/*      */           
/* 4014 */           match = (dl_cat != null) && (dl_cat.equals(tag_or_category));
/*      */         }
/*      */         
/* 4017 */         if (!match) {
/*      */           try
/*      */           {
/* 4020 */             List<Tag> tags = com.aelitis.azureus.core.tag.TagManagerFactory.getTagManager().getTagsForTaggable(3, PluginCoreUtils.unwrap(download));
/*      */             
/* 4022 */             for (Tag tag : tags)
/*      */             {
/* 4024 */               if (tag.getTagName(true).equals(tag_or_category))
/*      */               {
/* 4026 */                 match = true;
/*      */                 
/* 4028 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/* 4035 */         if (match)
/*      */         {
/* 4037 */           if (!TorrentUtils.isReallyPrivate(PluginCoreUtils.unwrap(torrent)))
/*      */           {
/* 4039 */             selected_dls.add(download);
/*      */             
/* 4041 */             byte[] hash = torrent.getHash();
/*      */             
/* 4043 */             int num = hash[0] << 24 & 0xFF000000 | hash[1] << 16 & 0xFF0000 | hash[2] << 8 & 0xFF00 | hash[3] & 0xFF;
/*      */             
/* 4045 */             fingerprint += num;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 4050 */     PluginConfig pc = this.plugin_interface.getPluginconfig();
/*      */     
/* 4052 */     String feed_finger_key = "feed_finger.category." + tag_or_category;
/* 4053 */     String feed_date_key = "feed_date.category." + tag_or_category;
/*      */     
/* 4055 */     long existing_fingerprint = pc.getPluginLongParameter(feed_finger_key, 0L);
/* 4056 */     long feed_date = pc.getPluginLongParameter(feed_date_key, 0L);
/*      */     
/* 4058 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 4060 */     if (existing_fingerprint == fingerprint)
/*      */     {
/*      */ 
/*      */ 
/* 4064 */       if (selected_dls.size() > 0)
/*      */       {
/* 4066 */         if ((now < feed_date) || (now - feed_date > 21600000L))
/*      */         {
/*      */ 
/* 4069 */           feed_date = now;
/*      */           
/* 4071 */           pc.setPluginParameter(feed_date_key, feed_date);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 4076 */       pc.setPluginParameter(feed_finger_key, fingerprint);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 4081 */       if (now <= feed_date)
/*      */       {
/* 4083 */         feed_date += 1L;
/*      */       }
/*      */       else
/*      */       {
/* 4087 */         feed_date = now;
/*      */       }
/*      */       
/* 4090 */       pc.setPluginParameter(feed_date_key, feed_date);
/*      */     }
/*      */     
/* 4093 */     String last_modified = TimeFormatter.getHTTPDate(feed_date);
/*      */     
/* 4095 */     if ((if_mod != null) && (if_mod.equals(last_modified)))
/*      */     {
/* 4097 */       return new feedDetails(new byte[0], last_modified);
/*      */     }
/*      */     
/* 4100 */     ByteArrayOutputStream os = new ByteArrayOutputStream();
/*      */     try
/*      */     {
/* 4103 */       PrintWriter pw = new PrintWriter(new java.io.OutputStreamWriter(os, "UTF-8"));
/*      */       
/* 4105 */       pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
/*      */       
/* 4107 */       pw.println("<rss version=\"2.0\" xmlns:vuze=\"http://www.vuze.com\">");
/*      */       
/* 4109 */       pw.println("<channel>");
/*      */       
/* 4111 */       pw.println("<title>" + escape(tag_or_category) + "</title>");
/*      */       
/* 4113 */       java.util.Collections.sort(selected_dls, new java.util.Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(Download d1, Download d2)
/*      */         {
/*      */ 
/*      */ 
/* 4122 */           long added1 = BuddyPlugin.this.getAddedTime(d1) / 1000L;
/* 4123 */           long added2 = BuddyPlugin.this.getAddedTime(d2) / 1000L;
/*      */           
/* 4125 */           return (int)(added2 - added1);
/*      */         }
/*      */         
/*      */ 
/* 4129 */       });
/* 4130 */       pw.println("<pubDate>" + last_modified + "</pubDate>");
/*      */       
/* 4132 */       for (int i = 0; i < selected_dls.size(); i++)
/*      */       {
/* 4134 */         Download download = (Download)selected_dls.get(i);
/*      */         
/* 4136 */         org.gudy.azureus2.core3.download.DownloadManager core_download = PluginCoreUtils.unwrap(download);
/*      */         
/* 4138 */         Torrent torrent = download.getTorrent();
/*      */         
/* 4140 */         String hash_str = Base32.encode(torrent.getHash());
/*      */         
/* 4142 */         pw.println("<item>");
/*      */         
/* 4144 */         pw.println("<title>" + escape(download.getName()) + "</title>");
/*      */         
/* 4146 */         pw.println("<guid>" + hash_str + "</guid>");
/*      */         
/* 4148 */         long added = core_download.getDownloadState().getLongParameter("stats.download.added.time");
/*      */         
/* 4150 */         pw.println("<pubDate>" + TimeFormatter.getHTTPDate(added) + "</pubDate>");
/*      */         
/* 4152 */         pw.println("<vuze:size>" + torrent.getSize() + "</vuze:size>");
/* 4153 */         pw.println("<vuze:assethash>" + hash_str + "</vuze:assethash>");
/*      */         
/* 4155 */         String url = "azplug:?id=azbuddy&name=Friends&arg=";
/*      */         
/* 4157 */         String arg = "pk=" + getPublicKey() + "&cat=" + tag_or_category + "&hash=" + Base32.encode(torrent.getHash());
/*      */         
/* 4159 */         url = url + java.net.URLEncoder.encode(arg, "UTF-8");
/*      */         
/* 4161 */         pw.println("<vuze:downloadurl>" + escape(url) + "</vuze:downloadurl>");
/*      */         
/* 4163 */         DownloadScrapeResult scrape = download.getLastScrapeResult();
/*      */         
/* 4165 */         if ((scrape != null) && (scrape.getResponseType() == 1))
/*      */         {
/* 4167 */           pw.println("<vuze:seeds>" + scrape.getSeedCount() + "</vuze:seeds>");
/* 4168 */           pw.println("<vuze:peers>" + scrape.getNonSeedCount() + "</vuze:peers>");
/*      */         }
/*      */         
/* 4171 */         pw.println("</item>");
/*      */       }
/*      */       
/* 4174 */       pw.println("</channel>");
/*      */       
/* 4176 */       pw.println("</rss>");
/*      */       
/* 4178 */       pw.flush();
/*      */       
/* 4180 */       return new feedDetails(os.toByteArray(), last_modified);
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/* 4184 */       throw new BuddyPluginException("", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getRSSTorrent(BuddyPluginBuddy buddy, String category, byte[] hash)
/*      */     throws BuddyPluginException
/*      */   {
/* 4196 */     if (!buddy.isLocalRSSTagOrCategoryAuthorised(category))
/*      */     {
/* 4198 */       throw new BuddyPluginException("Unauthorised category '" + category + "'");
/*      */     }
/*      */     try
/*      */     {
/* 4202 */       Download download = this.plugin_interface.getDownloadManager().getDownload(hash);
/*      */       
/* 4204 */       if (download != null)
/*      */       {
/* 4206 */         Torrent torrent = download.getTorrent();
/*      */         
/* 4208 */         if (torrent != null)
/*      */         {
/* 4210 */           String dl_cat = download.getAttribute(this.ta_category);
/*      */           
/* 4212 */           if ((category.equalsIgnoreCase("all")) || ((dl_cat != null) && (dl_cat.equals(category))))
/*      */           {
/*      */ 
/* 4215 */             if (!TorrentUtils.isReallyPrivate(PluginCoreUtils.unwrap(torrent)))
/*      */             {
/* 4217 */               torrent = torrent.removeAdditionalProperties();
/*      */               
/* 4219 */               return torrent.writeToBEncodedData();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 4226 */       throw new BuddyPluginException("getTorrent failed", e);
/*      */     }
/*      */     
/* 4229 */     throw new BuddyPluginException("Not found");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected long getAddedTime(Download download)
/*      */   {
/* 4236 */     org.gudy.azureus2.core3.download.DownloadManager core_download = PluginCoreUtils.unwrap(download);
/*      */     
/* 4238 */     return core_download.getDownloadState().getLongParameter("stats.download.added.time");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String escape(String str)
/*      */   {
/* 4245 */     return org.gudy.azureus2.core3.xml.util.XUXmlWriter.escapeXML(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addRequestListener(BuddyPluginBuddyRequestListener listener)
/*      */   {
/* 4252 */     this.request_listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeRequestListener(BuddyPluginBuddyRequestListener listener)
/*      */   {
/* 4259 */     this.request_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void logMessage(String str, Throwable e)
/*      */   {
/* 4267 */     logMessage(str + ": " + Debug.getNestedExceptionMessage(e), true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void logMessage(String str)
/*      */   {
/* 4274 */     logMessage(str, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void logMessage(String str, boolean is_error)
/*      */   {
/* 4282 */     log(str);
/*      */     
/* 4284 */     Iterator it = this.listeners.iterator();
/*      */     
/* 4286 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 4289 */         ((BuddyPluginListener)it.next()).messageLogged(str, is_error);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 4293 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void log(String str)
/*      */   {
/* 4302 */     this.logger.log(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void log(String str, Throwable e)
/*      */   {
/* 4310 */     this.logger.log(str + ": " + Debug.getNestedExceptionMessageAndStack(e));
/*      */   }
/*      */   
/*      */ 
/*      */   private static class publishDetails
/*      */     implements Cloneable
/*      */   {
/*      */     private byte[] public_key;
/*      */     private InetAddress ip;
/*      */     private int tcp_port;
/*      */     private int udp_port;
/*      */     private String nick_name;
/* 4322 */     private int online_status = 0;
/*      */     
/*      */     private boolean enabled;
/*      */     
/*      */     private boolean published;
/*      */     private int sequence;
/*      */     
/*      */     protected publishDetails getCopy()
/*      */     {
/*      */       try
/*      */       {
/* 4333 */         publishDetails copy = (publishDetails)clone();
/*      */         
/* 4335 */         copy.published = false;
/*      */         
/* 4337 */         return copy;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/* 4341 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean isPublished()
/*      */     {
/* 4348 */       return this.published;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setPublished(boolean b)
/*      */     {
/* 4355 */       this.published = b;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isEnabled()
/*      */     {
/* 4361 */       return this.enabled;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setEnabled(boolean _enabled)
/*      */     {
/* 4368 */       this.enabled = _enabled;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setSequence(int seq)
/*      */     {
/* 4375 */       this.sequence = seq;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getSequence()
/*      */     {
/* 4381 */       return this.sequence;
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte[] getPublicKey()
/*      */     {
/* 4387 */       return this.public_key;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setPublicKey(byte[] k)
/*      */     {
/* 4394 */       this.public_key = k;
/*      */     }
/*      */     
/*      */ 
/*      */     protected InetAddress getIP()
/*      */     {
/* 4400 */       return this.ip;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setIP(InetAddress _ip)
/*      */     {
/* 4407 */       this.ip = _ip;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getTCPPort()
/*      */     {
/* 4413 */       return this.tcp_port;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setTCPPort(int _port)
/*      */     {
/* 4420 */       this.tcp_port = _port;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getUDPPort()
/*      */     {
/* 4426 */       return this.udp_port;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setUDPPort(int _port)
/*      */     {
/* 4433 */       this.udp_port = _port;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getNickName()
/*      */     {
/* 4439 */       return this.nick_name;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setNickName(String n)
/*      */     {
/* 4446 */       this.nick_name = n;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getOnlineStatus()
/*      */     {
/* 4452 */       return this.online_status;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setOnlineStatus(int _status)
/*      */     {
/* 4459 */       this.online_status = _status;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 4465 */       return "enabled=" + this.enabled + ",ip=" + this.ip + ",tcp=" + this.tcp_port + ",udp=" + this.udp_port + ",stat=" + this.online_status + ",key=" + (this.public_key == null ? "<none>" : Base32.encode(this.public_key));
/*      */     }
/*      */   }
/*      */   
/*      */   private StringParameter cat_pub;
/*      */   private BooleanParameter beta_enabled_param;
/*      */   private boolean ready_to_publish;
/*      */   private publishDetails current_publish;
/*      */   private publishDetails latest_publish;
/*      */   private long last_publish_start;
/*      */   private TimerEvent republish_delay_event;
/*      */   private BloomFilter unauth_bloom;
/*      */   private long unauth_bloom_create_time;
/*      */   private BloomFilter ygm_unauth_bloom;
/*      */   private AsyncDispatcher publish_dispatcher;
/*      */   private DistributedDatabase ddb;
/*      */   private CryptoHandler ecc_handler;
/*      */   private List<BuddyPluginBuddy> buddies;
/*      */   private List<BuddyPluginBuddy> connected_at_close;
/*      */   private Map<String, BuddyPluginBuddy> buddies_map;
/*      */   private CopyOnWriteList<BuddyPluginListener> listeners;
/*      */   private CopyOnWriteList<BuddyPluginBuddyRequestListener> request_listeners;
/*      */   private SESecurityManager sec_man;
/*      */   
/*      */   protected static abstract interface operationListener
/*      */   {
/*      */     public abstract void complete();
/*      */   }
/*      */   
/*      */   protected static class feedDetails
/*      */   {
/*      */     protected feedDetails(byte[] _contents, String _last_modified) {
/* 4497 */       this.contents = _contents;
/* 4498 */       this.last_modified = _last_modified;
/*      */     }
/*      */     
/*      */     private byte[] contents;
/*      */     private String last_modified;
/*      */     protected byte[] getContent() {
/* 4504 */       return this.contents;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getLastModified()
/*      */     {
/* 4510 */       return this.last_modified;
/*      */     }
/*      */   }
/*      */   
/*      */   private GenericMessageRegistration msg_registration;
/*      */   private RateLimiter inbound_limiter;
/*      */   private RateLimiter outbound_limiter;
/*      */   private boolean config_dirty;
/*      */   private Random random;
/*      */   private BuddyPluginAZ2 az2_handler;
/*      */   private List<DistributedDatabaseContact> publish_write_contacts;
/*      */   private int status_seq;
/*      */   private Set<BuddyPluginBuddy> pd_preinit;
/*      */   private List<BuddyPluginBuddy> pd_queue;
/*      */   private AESemaphore pd_queue_sem;
/*      */   private AEThread2 pd_thread;
/*      */   private boolean bogus_ygm_written;
/*      */   private BuddyPluginTracker buddy_tracker;
/*      */   private org.gudy.azureus2.plugins.torrent.TorrentAttribute ta_category;
/*      */   private Set<String> public_tags_or_categories;
/*      */   private boolean lan_local_peers;
/*      */   private BuddyPluginBeta beta_plugin;
/*      */   private BuddyPluginViewInterface swt_ui;
/*      */   public static abstract interface cryptoResult
/*      */   {
/*      */     public abstract byte[] getChallenge();
/*      */     
/*      */     public abstract byte[] getPayload();
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */