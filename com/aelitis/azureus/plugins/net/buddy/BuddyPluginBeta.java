/*      */ package com.aelitis.azureus.plugins.net.buddy;
/*      */ 
/*      */ import com.aelitis.azureus.core.proxy.impl.AEPluginProxyHandler;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions;
/*      */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.plugins.I2PHelpers;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
/*      */ import org.gudy.azureus2.plugins.PluginEvent;
/*      */ import org.gudy.azureus2.plugins.PluginEventListener;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCException;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareManagerListener;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceDir;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceFile;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.utils.ShortCuts;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ public class BuddyPluginBeta
/*      */ {
/*   91 */   public static final boolean DEBUG_ENABLED = System.getProperty("az.chat.buddy.debug", "0").equals("1");
/*   92 */   public static final boolean BETA_CHAN_ENABLED = System.getProperty("az.chat.buddy.beta.chan", "1").equals("1");
/*      */   
/*      */   public static final String BETA_CHAT_KEY = "test:beta:chat";
/*      */   
/*      */   public static final int PRIVATE_CHAT_DISABLED = 1;
/*      */   
/*      */   public static final int PRIVATE_CHAT_PINNED_ONLY = 2;
/*      */   
/*      */   public static final int PRIVATE_CHAT_ENABLED = 3;
/*      */   
/*      */   private static final String FLAGS_MSG_STATUS_KEY = "s";
/*      */   
/*      */   private static final int FLAGS_MSG_STATUS_CHAT_NONE = 0;
/*      */   
/*      */   private static final int FLAGS_MSG_STATUS_CHAT_QUIT = 1;
/*      */   
/*      */   public static final String FLAGS_MSG_ORIGIN_KEY = "o";
/*      */   
/*      */   public static final int FLAGS_MSG_ORIGIN_USER = 0;
/*      */   public static final int FLAGS_MSG_ORIGIN_RATINGS = 1;
/*      */   public static final int FLAGS_MSG_ORIGIN_SEED_REQ = 2;
/*      */   public static final int FLAGS_MSG_ORIGIN_SUBS = 3;
/*      */   public static final String FLAGS_MSG_FLASH_OVERRIDE = "f";
/*      */   public static final int FLAGS_MSG_FLASH_NO = 0;
/*      */   public static final int FLAGS_MSG_FLASH_YES = 1;
/*      */   public static final String FLAGS_MSG_TYPE_KEY = "t";
/*      */   public static final int FLAGS_MSG_TYPE_NORMAL = 0;
/*      */   public static final int FLAGS_MSG_TYPE_ME = 1;
/*      */   private BuddyPlugin plugin;
/*      */   private PluginInterface plugin_interface;
/*      */   private BooleanParameter enabled;
/*  123 */   private AsyncDispatcher dispatcher = new AsyncDispatcher("BuddyPluginBeta");
/*      */   
/*  125 */   private Map<String, ChatInstance> chat_instances_map = new HashMap();
/*  126 */   private CopyOnWriteList<ChatInstance> chat_instances_list = new CopyOnWriteList();
/*      */   
/*      */   private PluginInterface azmsgsync_pi;
/*      */   
/*      */   private TimerEventPeriodic timer;
/*      */   
/*      */   private String shared_public_nickname;
/*      */   
/*      */   private String shared_anon_nickname;
/*      */   
/*      */   private int max_chat_ui_lines;
/*      */   
/*      */   private int max_chat_ui_kb;
/*      */   private boolean standalone_windows;
/*      */   private boolean windows_to_sidebar;
/*      */   private boolean hide_ratings;
/*      */   private boolean hide_search_subs;
/*      */   private int private_chat_state;
/*      */   private boolean shared_anon_endpoint;
/*      */   private boolean sound_enabled;
/*      */   private String sound_file;
/*      */   private Map<String, Map<String, Object>> opts_map;
/*  148 */   private CopyOnWriteList<FTUXStateChangeListener> ftux_listeners = new CopyOnWriteList();
/*      */   
/*  150 */   private boolean ftux_accepted = false;
/*      */   
/*  152 */   private CopyOnWriteList<ChatManagerListener> listeners = new CopyOnWriteList();
/*      */   
/*  154 */   private AtomicInteger private_chat_id = new AtomicInteger();
/*      */   
/*  156 */   private AESemaphore init_complete = new AESemaphore("bpb:init");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BuddyPluginBeta(PluginInterface _pi, BuddyPlugin _plugin, BooleanParameter _enabled)
/*      */   {
/*  165 */     this.plugin_interface = _pi;
/*  166 */     this.plugin = _plugin;
/*  167 */     this.enabled = _enabled;
/*      */     
/*  169 */     this.ftux_accepted = COConfigurationManager.getBooleanParameter("azbuddy.dchat.ftux.accepted", false);
/*      */     
/*  171 */     this.shared_public_nickname = COConfigurationManager.getStringParameter("azbuddy.chat.shared_nick", "");
/*  172 */     this.shared_anon_nickname = COConfigurationManager.getStringParameter("azbuddy.chat.shared_anon_nick", "");
/*  173 */     this.private_chat_state = COConfigurationManager.getIntParameter("azbuddy.chat.private_chat_state", 3);
/*      */     
/*  175 */     this.shared_anon_endpoint = COConfigurationManager.getBooleanParameter("azbuddy.chat.share_i2p_endpoint", true);
/*  176 */     this.sound_enabled = COConfigurationManager.getBooleanParameter("azbuddy.chat.notif.sound.enable", false);
/*  177 */     this.sound_file = COConfigurationManager.getStringParameter("azbuddy.chat.notif.sound.file", "");
/*      */     
/*  179 */     this.opts_map = COConfigurationManager.getMapParameter("azbuddy.dchat.optsmap", new HashMap());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  184 */     Map<String, Long> favourite_map = COConfigurationManager.getMapParameter("azbuddy.dchat.favemap", new HashMap());
/*      */     
/*  186 */     if (favourite_map.size() > 0)
/*      */     {
/*  188 */       migrateBooleans(favourite_map, "fave");
/*      */       
/*  190 */       COConfigurationManager.removeParameter("azbuddy.dchat.favemap");
/*      */     }
/*      */     
/*  193 */     Map<String, Long> save_messages_map = COConfigurationManager.getMapParameter("azbuddy.dchat.savemsgmap", new HashMap());
/*      */     
/*  195 */     if (save_messages_map.size() > 0)
/*      */     {
/*  197 */       migrateBooleans(save_messages_map, "save");
/*      */       
/*  199 */       COConfigurationManager.removeParameter("azbuddy.dchat.savemsgmap");
/*      */     }
/*      */     
/*  202 */     Map<String, Long> log_messages_map = COConfigurationManager.getMapParameter("azbuddy.dchat.logmsgmap", new HashMap());
/*      */     
/*  204 */     if (log_messages_map.size() > 0)
/*      */     {
/*  206 */       migrateBooleans(log_messages_map, "log");
/*      */       
/*  208 */       COConfigurationManager.removeParameter("azbuddy.dchat.logmsgmap");
/*      */     }
/*      */     
/*  211 */     Map<String, byte[]> lmi_map = COConfigurationManager.getMapParameter("azbuddy.dchat.lmimap", new HashMap());
/*      */     
/*  213 */     if (lmi_map.size() > 0)
/*      */     {
/*  215 */       migrateByteArrays(lmi_map, "lmi");
/*      */       
/*  217 */       COConfigurationManager.removeParameter("azbuddy.dchat.lmimap");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  222 */     this.max_chat_ui_lines = COConfigurationManager.getIntParameter("azbuddy.dchat.ui.max.lines", 250);
/*  223 */     this.max_chat_ui_kb = COConfigurationManager.getIntParameter("azbuddy.dchat.ui.max.char.kb", 10);
/*  224 */     this.standalone_windows = COConfigurationManager.getBooleanParameter("azbuddy.dchat.ui.standalone.windows", false);
/*  225 */     this.windows_to_sidebar = COConfigurationManager.getBooleanParameter("azbuddy.dchat.ui.windows.to.sidebar", false);
/*  226 */     this.hide_ratings = COConfigurationManager.getBooleanParameter("azbuddy.dchat.ui.hide.ratings", false);
/*  227 */     this.hide_search_subs = COConfigurationManager.getBooleanParameter("azbuddy.dchat.ui.hide.search_subs", false);
/*      */     
/*  229 */     SimpleTimer.addPeriodicEvent("BPB:checkfave", 30000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  238 */         BuddyPluginBeta.this.checkFavourites();
/*      */       }
/*      */       
/*  241 */     });
/*  242 */     AEDiagnostics.addEvidenceGenerator(new AEDiagnosticsEvidenceGenerator()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void generate(IndentWriter writer)
/*      */       {
/*      */ 
/*  249 */         writer.println("Chat (active=" + BuddyPluginBeta.this.chat_instances_list.size() + ")");
/*      */         try
/*      */         {
/*  252 */           writer.indent();
/*      */           
/*  254 */           for (BuddyPluginBeta.ChatInstance inst : BuddyPluginBeta.this.chat_instances_list)
/*      */           {
/*  256 */             writer.println("users=" + inst.getEstimatedNodes() + ", msg=" + inst.getMessageCount(true) + ", status=" + inst.getStatus());
/*      */           }
/*      */         }
/*      */         finally {
/*  260 */           writer.exdent();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAvailable()
/*      */   {
/*  269 */     return this.plugin_interface.getPluginManager().getPluginInterfaceByID("azmsgsync", true) != null;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isInitialised()
/*      */   {
/*  275 */     return this.init_complete.isReleasedForever();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxUILines()
/*      */   {
/*  281 */     return this.max_chat_ui_lines;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaxUILines(int num)
/*      */   {
/*  288 */     this.max_chat_ui_lines = num;
/*      */     
/*  290 */     COConfigurationManager.setParameter("azbuddy.dchat.ui.max.lines", num);
/*      */     
/*  292 */     COConfigurationManager.setDirty();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxUICharsKB()
/*      */   {
/*  298 */     return this.max_chat_ui_kb;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaxUICharsKB(int num)
/*      */   {
/*  305 */     this.max_chat_ui_kb = num;
/*      */     
/*  307 */     COConfigurationManager.setParameter("azbuddy.dchat.ui.max.char.kb", num);
/*      */     
/*  309 */     COConfigurationManager.setDirty();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getStandAloneWindows()
/*      */   {
/*  315 */     return this.standalone_windows;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setStandAloneWindows(boolean b)
/*      */   {
/*  322 */     this.standalone_windows = b;
/*      */     
/*  324 */     COConfigurationManager.setParameter("azbuddy.dchat.ui.standalone.windows", b);
/*      */     
/*  326 */     COConfigurationManager.setDirty();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getWindowsToSidebar()
/*      */   {
/*  332 */     return this.windows_to_sidebar;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setWindowsToSidebar(boolean b)
/*      */   {
/*  339 */     this.windows_to_sidebar = b;
/*      */     
/*  341 */     COConfigurationManager.setParameter("azbuddy.dchat.ui.windows.to.sidebar", b);
/*      */     
/*  343 */     COConfigurationManager.setDirty();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getHideRatings()
/*      */   {
/*  350 */     return this.hide_ratings;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setHideRatings(boolean b)
/*      */   {
/*  357 */     this.hide_ratings = b;
/*      */     
/*  359 */     COConfigurationManager.setParameter("azbuddy.dchat.ui.hide.ratings", b);
/*      */     
/*  361 */     COConfigurationManager.setDirty();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getHideSearchSubs()
/*      */   {
/*  367 */     return this.hide_search_subs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setHideSearchSubs(boolean b)
/*      */   {
/*  374 */     this.hide_search_subs = b;
/*      */     
/*  376 */     COConfigurationManager.setParameter("azbuddy.dchat.ui.hide.search_subs", b);
/*      */     
/*  378 */     COConfigurationManager.setDirty();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getFavourite(String net, String key)
/*      */   {
/*  386 */     return getBooleanOption(net, key, "fave", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFavourite(String net, String key, boolean b)
/*      */   {
/*  395 */     setBooleanOption(net, key, "fave", b);
/*      */     
/*  397 */     checkFavourites();
/*      */   }
/*      */   
/*      */ 
/*      */   public List<String[]> getFavourites()
/*      */   {
/*  403 */     synchronized (this.opts_map)
/*      */     {
/*  405 */       List<String[]> result = new ArrayList();
/*      */       
/*  407 */       for (Map.Entry<String, Map<String, Object>> entry : this.opts_map.entrySet())
/*      */       {
/*  409 */         String net_key = (String)entry.getKey();
/*  410 */         Map<String, Object> map = (Map)entry.getValue();
/*      */         
/*  412 */         Long value = (Long)map.get("fave");
/*      */         
/*  414 */         if ((value != null) && (value.longValue() == 1L))
/*      */         {
/*  416 */           String[] bits = net_key.split(":", 2);
/*      */           
/*  418 */           String network = AENetworkClassifier.internalise(bits[0]);
/*  419 */           String key = decodeKey(bits[1]);
/*      */           
/*  421 */           result.add(new String[] { network, key });
/*      */         }
/*      */       }
/*      */       
/*  425 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkFavourites()
/*      */   {
/*  432 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*  440 */           List<String[]> faves = BuddyPluginBeta.this.getFavourites();
/*      */           
/*  442 */           set = new HashSet();
/*      */           
/*  444 */           for (String[] fave : faves)
/*      */           {
/*  446 */             String net = fave[0];
/*  447 */             String key = fave[1];
/*      */             
/*  449 */             set.add(net + ":" + key);
/*      */             
/*  451 */             BuddyPluginBeta.ChatInstance chat = BuddyPluginBeta.this.peekChatInstance(net, key, false);
/*      */             
/*  453 */             if ((chat == null) || (!chat.getKeepAlive()))
/*      */             {
/*      */               try
/*      */               {
/*      */ 
/*  458 */                 chat = BuddyPluginBeta.this.getChat(net, key);
/*      */                 
/*  460 */                 chat.setKeepAlive(true);
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  468 */           for (BuddyPluginBeta.ChatInstance chat : BuddyPluginBeta.this.chat_instances_list)
/*      */           {
/*  470 */             if (chat.getKeepAlive())
/*      */             {
/*  472 */               String net = chat.getNetwork();
/*  473 */               String key = chat.getKey();
/*      */               
/*  475 */               if (!set.contains(net + ":" + key))
/*      */               {
/*  477 */                 if ((net != "Public") || (!key.equals("test:beta:chat")))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  486 */                   chat.setKeepAlive(false);
/*      */                   
/*  488 */                   chat.destroy();
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         } finally {
/*      */           Set<String> set;
/*  495 */           BuddyPluginBeta.this.init_complete.releaseForever();
/*      */         }
/*      */       }
/*      */     });
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
/*      */   public String getNick(String net, String key)
/*      */   {
/*  520 */     String old_key = "azbuddy.chat." + net + ": " + key + ".nick";
/*      */     
/*  522 */     if (COConfigurationManager.doesParameterNonDefaultExist(old_key))
/*      */     {
/*  524 */       String temp = COConfigurationManager.getStringParameter(old_key, "");
/*      */       
/*  526 */       COConfigurationManager.removeParameter(old_key);
/*      */       
/*  528 */       if (temp.length() > 0)
/*      */       {
/*  530 */         setNick(net, key, temp);
/*      */         
/*  532 */         return temp;
/*      */       }
/*      */     }
/*      */     
/*  536 */     String nick = getStringOption(net, key, "nick", "");
/*      */     
/*  538 */     return nick;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNick(String net, String key, String nick)
/*      */   {
/*  547 */     setStringOption(net, key, "nick", nick);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean getSharedNick(String net, String key)
/*      */   {
/*  557 */     String old_key = "azbuddy.chat." + net + ": " + key + ".shared";
/*      */     
/*  559 */     if (COConfigurationManager.doesParameterNonDefaultExist(old_key))
/*      */     {
/*  561 */       boolean temp = COConfigurationManager.getBooleanParameter(old_key, true);
/*      */       
/*  563 */       COConfigurationManager.removeParameter(old_key);
/*      */       
/*  565 */       if (!temp)
/*      */       {
/*  567 */         setSharedNick(net, key, false);
/*      */       }
/*      */       
/*  570 */       return temp;
/*      */     }
/*      */     
/*  573 */     return getBooleanOption(net, key, "sn", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setSharedNick(String net, String key, boolean b)
/*      */   {
/*  582 */     setBooleanOption(net, key, "sn", b);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean getSaveMessages(String net, String key)
/*      */   {
/*  592 */     return getBooleanOption(net, key, "save", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setSaveMessages(String net, String key, boolean b)
/*      */   {
/*  601 */     setBooleanOption(net, key, "save", b);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean getLogMessages(String net, String key)
/*      */   {
/*  611 */     return getBooleanOption(net, key, "log", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setLogMessages(String net, String key, boolean b)
/*      */   {
/*  620 */     setBooleanOption(net, key, "log", b);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean getAutoMute(String net, String key)
/*      */   {
/*  631 */     return getBooleanOption(net, key, "automute", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setAutoMute(String net, String key, boolean b)
/*      */   {
/*  640 */     setBooleanOption(net, key, "automute", b);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean getDisableNewMsgIndications(String net, String key)
/*      */   {
/*  650 */     return getBooleanOption(net, key, "disnot", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setDisableNewMsgIndications(String net, String key, boolean b)
/*      */   {
/*  659 */     setBooleanOption(net, key, "disnot", b);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean getEnableNotificationsPost(String net, String key)
/*      */   {
/*  669 */     return getBooleanOption(net, key, "notipost", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setEnableNotificationsPost(String net, String key, boolean b)
/*      */   {
/*  678 */     setBooleanOption(net, key, "notipost", b);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getLastMessageInfo(String net, String key)
/*      */   {
/*  688 */     return getStringOption(net, key, "lmi", null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLastMessageInfo(String net, String key, String info)
/*      */   {
/*  697 */     setStringOption(net, key, "lmi", info);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void migrateBooleans(Map<String, Long> map, String name)
/*      */   {
/*  707 */     for (Map.Entry<String, Long> entry : map.entrySet())
/*      */     {
/*  709 */       String net_key = (String)entry.getKey();
/*  710 */       Long value = (Long)entry.getValue();
/*      */       
/*  712 */       if (value.longValue() == 1L)
/*      */       {
/*  714 */         String[] bits = net_key.split(":", 2);
/*      */         
/*  716 */         String network = AENetworkClassifier.internalise(bits[0]);
/*  717 */         String key = bits[1];
/*      */         
/*  719 */         setBooleanOption(network, key, name, true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void migrateByteArrays(Map<String, byte[]> map, String name)
/*      */   {
/*  729 */     for (Map.Entry<String, byte[]> entry : map.entrySet())
/*      */     {
/*  731 */       String net_key = (String)entry.getKey();
/*  732 */       byte[] value = (byte[])entry.getValue();
/*      */       
/*  734 */       String[] bits = net_key.split(":", 2);
/*      */       
/*  736 */       String network = AENetworkClassifier.internalise(bits[0]);
/*  737 */       String key = bits[1];
/*      */       
/*  739 */       setByteArrayOption(network, key, name, value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setBooleanOption(String net, String key, String name, boolean value)
/*      */   {
/*  750 */     setGenericOption(net, key, name, Long.valueOf(value ? 1L : 0L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean getBooleanOption(String net, String key, String name, boolean def)
/*      */   {
/*  760 */     Object obj = getGenericOption(net, key, name);
/*      */     
/*  762 */     if ((obj instanceof Number))
/*      */     {
/*  764 */       return ((Number)obj).intValue() != 0;
/*      */     }
/*      */     
/*  767 */     return def;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setStringOption(String net, String key, String name, String value)
/*      */   {
/*      */     try
/*      */     {
/*  778 */       setByteArrayOption(net, key, name, value.getBytes("UTF-8"));
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getStringOption(String net, String key, String name, String def)
/*      */   {
/*  792 */     byte[] bytes = getByteArrayOption(net, key, name);
/*      */     
/*  794 */     if (bytes != null) {
/*      */       try
/*      */       {
/*  797 */         return new String(bytes, "UTF-8");
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  804 */     return def;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setByteArrayOption(String net, String key, String name, byte[] value)
/*      */   {
/*  814 */     setGenericOption(net, key, name, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] getByteArrayOption(String net, String key, String name)
/*      */   {
/*  823 */     Object obj = getGenericOption(net, key, name);
/*      */     
/*  825 */     if ((obj instanceof byte[]))
/*      */     {
/*  827 */       return (byte[])obj;
/*      */     }
/*      */     
/*  830 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private String encodeKey(String key)
/*      */   {
/*      */     try
/*      */     {
/*  838 */       return Base32.encode(key.getBytes("UTF-8"));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  842 */       Debug.out(e);
/*      */     }
/*  844 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String decodeKey(String key)
/*      */   {
/*      */     try
/*      */     {
/*  853 */       return new String(Base32.decode(key), "UTF-8");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  857 */       Debug.out(e);
/*      */     }
/*  859 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Object getGenericOption(String net, String key, String name)
/*      */   {
/*  869 */     String net_key = net + ":" + encodeKey(key);
/*      */     
/*  871 */     synchronized (this.opts_map)
/*      */     {
/*  873 */       Map<String, Object> opts = (Map)this.opts_map.get(net_key);
/*      */       
/*  875 */       if (opts == null)
/*      */       {
/*  877 */         return null;
/*      */       }
/*      */       
/*  880 */       return opts.get(name);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setGenericOption(String net, String key, String name, Object value)
/*      */   {
/*  891 */     String net_key = net + ":" + encodeKey(key);
/*      */     
/*  893 */     synchronized (this.opts_map)
/*      */     {
/*      */       try {
/*  896 */         Map<String, Object> opts = (Map)this.opts_map.get(net_key);
/*      */         
/*  898 */         if (opts == null)
/*      */         {
/*  900 */           opts = new HashMap();
/*      */           
/*  902 */           this.opts_map.put(net_key, opts);
/*      */         }
/*      */         
/*  905 */         opts.put(name, value);
/*      */         
/*  907 */         COConfigurationManager.setParameter("azbuddy.dchat.optsmap", this.opts_map);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*  913 */     COConfigurationManager.setDirty();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void removeAllOptions(String net, String key)
/*      */   {
/*  921 */     String net_key = net + ":" + encodeKey(key);
/*      */     
/*  923 */     synchronized (this.opts_map)
/*      */     {
/*      */       try {
/*  926 */         Map<String, Object> opts = (Map)this.opts_map.remove(net_key);
/*      */         
/*  928 */         if (opts == null)
/*      */         {
/*  930 */           return;
/*      */         }
/*      */         
/*  933 */         COConfigurationManager.setParameter("azbuddy.dchat.optsmap", this.opts_map);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*  939 */     COConfigurationManager.setDirty();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getSharedPublicNickname()
/*      */   {
/*  945 */     return this.shared_public_nickname;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSharedPublicNickname(String _nick)
/*      */   {
/*  952 */     if (!_nick.equals(this.shared_public_nickname))
/*      */     {
/*  954 */       this.shared_public_nickname = _nick;
/*      */       
/*  956 */       COConfigurationManager.setParameter("azbuddy.chat.shared_nick", _nick);
/*      */       
/*  958 */       COConfigurationManager.setDirty();
/*      */       
/*  960 */       allUpdated();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getSharedAnonNickname()
/*      */   {
/*  967 */     return this.shared_anon_nickname;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSharedAnonNickname(String _nick)
/*      */   {
/*  974 */     if (!_nick.equals(this.shared_anon_nickname))
/*      */     {
/*  976 */       this.shared_anon_nickname = _nick;
/*      */       
/*  978 */       COConfigurationManager.setParameter("azbuddy.chat.shared_anon_nick", _nick);
/*      */       
/*  980 */       COConfigurationManager.setDirty();
/*      */       
/*  982 */       allUpdated();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPrivateChatState()
/*      */   {
/*  989 */     return this.private_chat_state;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPrivateChatState(int state)
/*      */   {
/*  996 */     if (state != this.private_chat_state)
/*      */     {
/*  998 */       this.private_chat_state = state;
/*      */       
/* 1000 */       COConfigurationManager.setParameter("azbuddy.chat.private_chat_state", state);
/*      */       
/* 1002 */       COConfigurationManager.setDirty();
/*      */       
/* 1004 */       this.plugin.fireUpdated();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getSharedAnonEndpoint()
/*      */   {
/* 1011 */     return this.shared_anon_endpoint;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSharedAnonEndpoint(boolean b)
/*      */   {
/* 1018 */     if (b != this.shared_anon_endpoint)
/*      */     {
/* 1020 */       this.shared_anon_endpoint = b;
/*      */       
/* 1022 */       COConfigurationManager.setParameter("azbuddy.chat.share_i2p_endpoint", b);
/*      */       
/* 1024 */       COConfigurationManager.setDirty();
/*      */       
/* 1026 */       this.plugin.fireUpdated();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSoundEnabled(boolean b)
/*      */   {
/* 1034 */     if (b != this.sound_enabled)
/*      */     {
/* 1036 */       this.sound_enabled = b;
/*      */       
/* 1038 */       COConfigurationManager.setParameter("azbuddy.chat.notif.sound.enable", b);
/*      */       
/* 1040 */       COConfigurationManager.setDirty();
/*      */       
/* 1042 */       this.plugin.fireUpdated();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getSoundEnabled()
/*      */   {
/* 1049 */     return this.sound_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getSoundFile()
/*      */   {
/* 1055 */     return this.sound_file;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSoundFile(String _file)
/*      */   {
/* 1062 */     if (!_file.equals(this.sound_file))
/*      */     {
/* 1064 */       this.sound_file = _file;
/*      */       
/* 1066 */       COConfigurationManager.setParameter("azbuddy.chat.notif.sound.file", _file);
/*      */       
/* 1068 */       COConfigurationManager.setDirty();
/*      */       
/* 1070 */       this.plugin.fireUpdated();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void allUpdated()
/*      */   {
/* 1077 */     for (ChatInstance chat : this.chat_instances_list)
/*      */     {
/* 1079 */       chat.updated();
/*      */     }
/*      */     
/* 1082 */     this.plugin.fireUpdated();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void startup()
/*      */   {
/* 1088 */     this.plugin_interface.addEventListener(new PluginEventListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(PluginEvent ev)
/*      */       {
/*      */ 
/* 1095 */         int type = ev.getType();
/*      */         
/* 1097 */         if (type == 5) {
/*      */           try
/*      */           {
/* 1100 */             ShareManager share_manager = BuddyPluginBeta.this.plugin_interface.getShareManager();
/*      */             
/* 1102 */             share_manager.addListener(new ShareManagerListener()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void resourceModified(ShareResource old_resource, ShareResource new_resource)
/*      */               {
/*      */ 
/*      */ 
/* 1110 */                 BuddyPluginBeta.this.checkTag(new_resource);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public void resourceDeleted(ShareResource resource) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public void resourceAdded(ShareResource resource)
/*      */               {
/* 1123 */                 BuddyPluginBeta.this.checkTag(resource);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void reportProgress(int percent_complete) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void reportCurrentTask(String task_description) {}
/* 1137 */             });
/* 1138 */             ShareResource[] existing = share_manager.getShares();
/*      */             
/* 1140 */             for (ShareResource sr : existing)
/*      */             {
/* 1142 */               BuddyPluginBeta.this.checkTag(sr);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 1146 */             Debug.out(e);
/*      */           }
/* 1148 */         } else if (type == 8)
/*      */         {
/* 1150 */           BuddyPluginBeta.this.pluginAdded((PluginInterface)ev.getValue());
/*      */         }
/* 1152 */         else if (type == 9)
/*      */         {
/* 1154 */           BuddyPluginBeta.this.pluginRemoved((PluginInterface)ev.getValue());
/*      */         }
/*      */         
/*      */       }
/* 1158 */     });
/* 1159 */     PluginInterface[] plugins = this.plugin_interface.getPluginManager().getPlugins(true);
/*      */     
/* 1161 */     for (PluginInterface pi : plugins)
/*      */     {
/* 1163 */       if (pi.getPluginState().isOperational())
/*      */       {
/* 1165 */         pluginAdded(pi);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void checkTag(ShareResource resource)
/*      */   {
/* 1174 */     Map<String, String> properties = resource.getProperties();
/*      */     
/* 1176 */     if (properties != null)
/*      */     {
/* 1178 */       String ud = (String)properties.get("user_data");
/*      */       
/* 1180 */       if ((ud != null) && (ud.equals("buddyplugin:share")))
/*      */       {
/*      */         try
/*      */         {
/* 1184 */           Torrent torrent = null;
/*      */           
/* 1186 */           if ((resource instanceof ShareResourceFile))
/*      */           {
/* 1188 */             torrent = ((ShareResourceFile)resource).getItem().getTorrent();
/*      */           }
/* 1190 */           else if ((resource instanceof ShareResourceDir))
/*      */           {
/* 1192 */             torrent = ((ShareResourceDir)resource).getItem().getTorrent();
/*      */           }
/*      */           
/* 1195 */           if (torrent != null)
/*      */           {
/* 1197 */             Download download = this.plugin_interface.getPluginManager().getDefaultPluginInterface().getShortCuts().getDownload(torrent.getHash());
/*      */             
/* 1199 */             if (download != null)
/*      */             {
/* 1201 */               tagDownload(download);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void tagDownload(Download download)
/*      */   {
/*      */     try
/*      */     {
/* 1216 */       TagType tt = TagManagerFactory.getTagManager().getTagType(3);
/*      */       
/* 1218 */       Tag tag = tt.getTag("tag.azbuddy.dchat.shares", false);
/*      */       
/* 1220 */       if (tag == null)
/*      */       {
/* 1222 */         tag = tt.createTag("tag.azbuddy.dchat.shares", true);
/*      */         
/* 1224 */         tag.setCanBePublic(false);
/*      */         
/* 1226 */         tag.setPublic(false);
/*      */       }
/*      */       
/* 1229 */       tag.addTaggable(PluginCoreUtils.unwrap(download));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1233 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void closedown() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void pluginAdded(PluginInterface pi)
/*      */   {
/* 1247 */     if (pi.getPluginID().equals("azmsgsync"))
/*      */     {
/* 1249 */       synchronized (this.chat_instances_map)
/*      */       {
/* 1251 */         this.azmsgsync_pi = pi;
/*      */         
/* 1253 */         Iterator<ChatInstance> it = this.chat_instances_map.values().iterator();
/*      */         
/* 1255 */         while (it.hasNext())
/*      */         {
/* 1257 */           ChatInstance inst = (ChatInstance)it.next();
/*      */           try
/*      */           {
/* 1260 */             inst.bind(this.azmsgsync_pi, null);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1264 */             Debug.out(e);
/*      */             
/* 1266 */             it.remove();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1271 */       this.dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/* 1279 */             if ((Constants.isCVSVersion()) && (BuddyPluginBeta.this.enabled.getValue()))
/*      */             {
/* 1281 */               if (BuddyPluginBeta.BETA_CHAN_ENABLED)
/*      */               {
/* 1283 */                 BuddyPluginBeta.ChatInstance chat = BuddyPluginBeta.this.getChat("Public", "test:beta:chat");
/*      */                 
/* 1285 */                 chat.setKeepAlive(true);
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
/*      */   private void pluginRemoved(PluginInterface pi)
/*      */   {
/* 1302 */     if (pi.getPluginID().equals("azmsgsync"))
/*      */     {
/* 1304 */       synchronized (this.chat_instances_map)
/*      */       {
/* 1306 */         this.azmsgsync_pi = null;
/*      */         
/* 1308 */         Iterator<ChatInstance> it = this.chat_instances_map.values().iterator();
/*      */         
/* 1310 */         while (it.hasNext())
/*      */         {
/* 1312 */           ChatInstance inst = (ChatInstance)it.next();
/*      */           
/* 1314 */           inst.unbind();
/*      */           
/* 1316 */           if (inst.isPrivateChat())
/*      */           {
/* 1318 */             it.remove();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isI2PAvailable()
/*      */   {
/* 1328 */     return AEPluginProxyHandler.hasPluginProxyForNetwork("I2P", false);
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
/*      */   public java.io.InputStream handleURI(String url_str, boolean open_only)
/*      */     throws Exception
/*      */   {
/* 1345 */     int pos = url_str.indexOf('?');
/*      */     
/*      */ 
/* 1348 */     String key = null;
/* 1349 */     String format = null;
/*      */     
/* 1351 */     if (pos != -1)
/*      */     {
/* 1353 */       String protocol = url_str.substring(0, pos).toLowerCase(Locale.US);
/*      */       
/* 1355 */       String args = url_str.substring(pos + 1);
/*      */       
/* 1357 */       String[] bits = args.split("&");
/*      */       
/* 1359 */       for (String bit : bits)
/*      */       {
/* 1361 */         String[] temp = bit.split("=");
/*      */         
/* 1363 */         if (temp.length == 1)
/*      */         {
/* 1365 */           key = UrlUtils.decode(temp[0]);
/*      */         }
/*      */         else
/*      */         {
/* 1369 */           String lhs = temp[0].toLowerCase(Locale.US);
/* 1370 */           String rhs = UrlUtils.decode(temp[1]);
/*      */           
/* 1372 */           if (lhs.equals("key"))
/*      */           {
/* 1374 */             key = rhs;
/*      */           }
/* 1376 */           else if (lhs.equals("format"))
/*      */           {
/* 1378 */             format = rhs;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1385 */       throw new Exception("Malformed request");
/*      */     }
/*      */     String protocol;
/* 1388 */     if (key == null)
/*      */     {
/* 1390 */       throw new Exception("Key missing");
/*      */     }
/*      */     
/* 1393 */     if (protocol.startsWith("chat:friend"))
/*      */     {
/* 1395 */       if (!key.equals(this.plugin.getPublicKey()))
/*      */       {
/* 1397 */         this.plugin.addBuddy(key, 1);
/*      */         
/* 1399 */         this.plugin.getSWTUI().selectClassicTab();
/*      */       }
/*      */       
/* 1402 */       return null;
/*      */     }
/*      */     
/* 1405 */     if (open_only)
/*      */     {
/* 1407 */       format = null;
/*      */     }
/*      */     
/*      */     String network;
/*      */     
/* 1412 */     if (protocol.startsWith("chat:anon"))
/*      */     {
/* 1414 */       if (!isI2PAvailable())
/*      */       {
/* 1416 */         boolean[] result = { false };
/*      */         
/* 1418 */         I2PHelpers.installI2PHelper(MessageText.getString("azbuddy.dchat.anon.requested"), "azbuddy.dchat.uri.based.i2p.install", result, new Runnable()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void run() {}
/*      */ 
/*      */ 
/* 1425 */         });
/* 1426 */         throw new Exception("I2P unavailable");
/*      */       }
/*      */       
/* 1429 */       network = "I2P";
/*      */     } else { String network;
/* 1431 */       if (protocol.startsWith("chat"))
/*      */       {
/* 1433 */         network = "Public";
/*      */       }
/*      */       else
/*      */       {
/* 1437 */         throw new Exception("Invalid protocol: " + protocol); }
/*      */     }
/*      */     String network;
/* 1440 */     if ((format == null) || (!format.equalsIgnoreCase("rss")))
/*      */     {
/* 1442 */       BuddyPluginViewInterface ui = this.plugin.getSWTUI();
/*      */       
/* 1444 */       if (ui == null)
/*      */       {
/* 1446 */         throw new Exception("UI unavailable");
/*      */       }
/*      */       
/* 1449 */       ChatInstance chat = getChat(network, key);
/*      */       
/* 1451 */       ui.openChat(chat);
/*      */       
/* 1453 */       return null;
/*      */     }
/*      */     
/*      */ 
/* 1457 */     ChatInstance chat = peekChatInstance(network, key, true);
/*      */     
/* 1459 */     if (chat == null)
/*      */     {
/* 1461 */       throw new Exception("Chat unavailable");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1466 */     if (!chat.isFavourite())
/*      */     {
/* 1468 */       chat.setFavourite(true);
/*      */       
/* 1470 */       chat.setKeepAlive(true);
/*      */     }
/*      */     
/* 1473 */     if (!chat.getSaveMessages())
/*      */     {
/* 1475 */       chat.setSaveMessages(true);
/*      */     }
/*      */     
/* 1478 */     List<ChatMessage> messages = chat.getMessages();
/*      */     
/* 1480 */     ByteArrayOutputStream baos = new ByteArrayOutputStream(10240);
/*      */     
/* 1482 */     PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "UTF-8"));
/*      */     
/* 1484 */     pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
/*      */     
/* 1486 */     pw.println("<rss version=\"2.0\" xmlns:vuze=\"http://www.vuze.com\">");
/*      */     
/* 1488 */     pw.println("<channel>");
/*      */     
/* 1490 */     pw.println("<title>" + escape(chat.getName()) + "</title>");
/*      */     
/*      */     long last_modified;
/*      */     long last_modified;
/* 1494 */     if (messages.size() == 0)
/*      */     {
/* 1496 */       last_modified = SystemTime.getCurrentTime();
/*      */     }
/*      */     else
/*      */     {
/* 1500 */       last_modified = ((ChatMessage)messages.get(messages.size() - 1)).getTimeStamp();
/*      */     }
/*      */     
/* 1503 */     pw.println("<pubDate>" + TimeFormatter.getHTTPDate(last_modified) + "</pubDate>");
/*      */     
/* 1505 */     for (ChatMessage message : messages)
/*      */     {
/* 1507 */       List<Map<String, Object>> message_links = extractLinks(message.getMessage());
/*      */       
/* 1509 */       if (message_links.size() != 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1514 */         item_date = TimeFormatter.getHTTPDate(message.getTimeStamp());
/*      */         
/* 1516 */         for (Map<String, Object> message_link : message_links)
/*      */         {
/* 1518 */           if (message_link.containsKey("magnet"))
/*      */           {
/* 1520 */             Map<String, Object> magnet = message_link;
/*      */             
/* 1522 */             String hash = (String)magnet.get("hash");
/*      */             
/* 1524 */             if (hash != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1529 */               String title = (String)magnet.get("title");
/*      */               
/* 1531 */               if (title == null)
/*      */               {
/* 1533 */                 title = hash;
/*      */               }
/*      */               
/* 1536 */               String link = (String)magnet.get("link");
/*      */               
/* 1538 */               if (link == null)
/*      */               {
/* 1540 */                 link = (String)magnet.get("magnet");
/*      */               }
/*      */               
/* 1543 */               pw.println("<item>");
/*      */               
/* 1545 */               pw.println("<title>" + escape(title) + "</title>");
/*      */               
/* 1547 */               pw.println("<guid>" + hash + "</guid>");
/*      */               
/* 1549 */               String cdp = (String)magnet.get("cdp");
/*      */               
/* 1551 */               if (cdp != null)
/*      */               {
/* 1553 */                 pw.println("<link>" + escape(cdp) + "</link>");
/*      */               }
/*      */               
/* 1556 */               Long size = (Long)magnet.get("size");
/* 1557 */               Long seeds = (Long)magnet.get("seeds");
/* 1558 */               Long leechers = (Long)magnet.get("leechers");
/* 1559 */               Long date = (Long)magnet.get("date");
/*      */               
/* 1561 */               String enclosure = "<enclosure type=\"application/x-bittorrent\" url=\"" + escape(link) + "\"";
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 1566 */               if (size != null)
/*      */               {
/* 1568 */                 enclosure = enclosure + " length=\"" + size + "\"";
/*      */               }
/*      */               
/* 1571 */               enclosure = enclosure + " />";
/*      */               
/* 1573 */               pw.println(enclosure);
/*      */               
/* 1575 */               String date_str = (date == null) || (date.longValue() <= 0L) ? item_date : TimeFormatter.getHTTPDate(date.longValue());
/*      */               
/* 1577 */               pw.println("<pubDate>" + date_str + "</pubDate>");
/*      */               
/*      */ 
/* 1580 */               if (size != null)
/*      */               {
/* 1582 */                 pw.println("<vuze:size>" + size + "</vuze:size>");
/*      */               }
/*      */               
/* 1585 */               if (seeds != null)
/*      */               {
/* 1587 */                 pw.println("<vuze:seeds>" + seeds + "</vuze:seeds>");
/*      */               }
/*      */               
/* 1590 */               if (leechers != null)
/*      */               {
/* 1592 */                 pw.println("<vuze:peers>" + leechers + "</vuze:peers>");
/*      */               }
/*      */               
/* 1595 */               pw.println("<vuze:assethash>" + hash + "</vuze:assethash>");
/*      */               
/* 1597 */               pw.println("<vuze:downloadurl>" + escape(link) + "</vuze:downloadurl>");
/*      */               
/* 1599 */               pw.println("</item>");
/*      */             }
/*      */           }
/*      */           else {
/* 1603 */             String title = (String)message_link.get("title");
/* 1604 */             String link = (String)message_link.get("link");
/*      */             
/* 1606 */             pw.println("<item>");
/*      */             
/* 1608 */             pw.println("<title>" + escape(title) + "</title>");
/*      */             
/* 1610 */             pw.println("<guid>" + escape(link) + "</guid>");
/*      */             
/* 1612 */             pw.println("<link>" + escape(link) + "</link>");
/*      */             
/* 1614 */             pw.println("<pubDate>" + item_date + "</pubDate>");
/*      */             
/* 1616 */             pw.println("<vuze:rank></vuze:rank>");
/*      */             
/* 1618 */             String enclosure = "<enclosure type=\"application/x-bittorrent\" url=\"" + escape(link) + "\"";
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1624 */             enclosure = enclosure + " />";
/*      */             
/* 1626 */             pw.println(enclosure);
/*      */             
/* 1628 */             pw.println("</item>");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     String item_date;
/* 1634 */     pw.println("</channel>");
/*      */     
/* 1636 */     pw.println("</rss>");
/*      */     
/* 1638 */     pw.flush();
/*      */     
/* 1640 */     return new ByteArrayInputStream(baos.toByteArray());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private List<Map<String, Object>> extractLinks(String str)
/*      */   {
/* 1648 */     List<Map<String, Object>> result = new ArrayList();
/*      */     
/* 1650 */     int len = str.length();
/*      */     
/* 1652 */     String lc_str = str.toLowerCase(Locale.US);
/*      */     
/* 1654 */     int pos = 0;
/*      */     
/* 1656 */     while (pos < len)
/*      */     {
/* 1658 */       int temp_pos = lc_str.indexOf("magnet:", pos);
/*      */       
/* 1660 */       int type = -1;
/*      */       
/* 1662 */       if (temp_pos != -1)
/*      */       {
/* 1664 */         pos = temp_pos;
/*      */         
/* 1666 */         type = 0;
/*      */       }
/*      */       else
/*      */       {
/* 1670 */         String[] protocols = { "azplug:", "chat:" };
/*      */         
/* 1672 */         for (String p : protocols)
/*      */         {
/* 1674 */           temp_pos = lc_str.indexOf(p, pos);
/*      */           
/* 1676 */           if (temp_pos != -1)
/*      */           {
/* 1678 */             pos = temp_pos;
/*      */             
/* 1680 */             type = 1;
/*      */             
/* 1682 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1686 */         if (type == -1) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1692 */       int start = pos;
/*      */       
/* 1694 */       while (pos < len)
/*      */       {
/* 1696 */         char c = str.charAt(pos);
/*      */         
/* 1698 */         if ((Character.isWhitespace(c)) || ((c == '"') && (start > 0) && (lc_str.charAt(start - 1) == '"'))) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1704 */         pos++;
/*      */       }
/*      */       
/*      */ 
/* 1708 */       String link = str.substring(start, pos);
/*      */       
/* 1710 */       if (type == 0)
/*      */       {
/* 1712 */         String magnet = link;
/*      */         
/* 1714 */         int x = magnet.indexOf('?');
/*      */         
/* 1716 */         if (x != -1)
/*      */         {
/* 1718 */           Map<String, Object> map = new HashMap();
/*      */           
/*      */ 
/*      */ 
/* 1722 */           int p1 = magnet.lastIndexOf("[[");
/*      */           
/* 1724 */           if ((p1 != -1) && (magnet.endsWith("]]")))
/*      */           {
/* 1726 */             magnet = magnet.substring(0, p1);
/*      */           }
/*      */           
/* 1729 */           map.put("magnet", magnet);
/*      */           
/* 1731 */           List<String> trackers = new ArrayList();
/*      */           
/* 1733 */           map.put("trackers", trackers);
/*      */           
/* 1735 */           String[] bits = magnet.substring(x + 1).split("&");
/*      */           
/* 1737 */           for (String bit : bits)
/*      */           {
/* 1739 */             String[] temp = bit.split("=");
/*      */             
/* 1741 */             if (temp.length == 2)
/*      */             {
/*      */               try
/*      */               {
/* 1745 */                 String lhs = temp[0].toLowerCase(Locale.US);
/* 1746 */                 String rhs = UrlUtils.decode(temp[1]);
/*      */                 
/* 1748 */                 if (lhs.equals("xt"))
/*      */                 {
/* 1750 */                   String lc_rhs = rhs.toLowerCase(Locale.US);
/*      */                   
/* 1752 */                   int p = lc_rhs.indexOf("btih:");
/*      */                   
/* 1754 */                   if (p >= 0)
/*      */                   {
/* 1756 */                     map.put("hash", lc_rhs.substring(p + 5).toUpperCase(Locale.US));
/*      */                   }
/*      */                 }
/* 1759 */                 else if (lhs.equals("dn"))
/*      */                 {
/* 1761 */                   map.put("title", rhs);
/*      */                 }
/* 1763 */                 else if (lhs.equals("tr"))
/*      */                 {
/* 1765 */                   trackers.add(rhs);
/*      */                 }
/* 1767 */                 else if (lhs.equals("fl"))
/*      */                 {
/* 1769 */                   map.put("link", rhs);
/*      */                 }
/* 1771 */                 else if (lhs.equals("xl"))
/*      */                 {
/* 1773 */                   long size = Long.parseLong(rhs);
/*      */                   
/* 1775 */                   map.put("size", Long.valueOf(size));
/*      */                 }
/* 1777 */                 else if (lhs.equals("_d"))
/*      */                 {
/* 1779 */                   long date = Long.parseLong(rhs);
/*      */                   
/* 1781 */                   map.put("date", Long.valueOf(date));
/*      */                 }
/* 1783 */                 else if (lhs.equals("_s"))
/*      */                 {
/* 1785 */                   long seeds = Long.parseLong(rhs);
/*      */                   
/* 1787 */                   map.put("seeds", Long.valueOf(seeds));
/*      */                 }
/* 1789 */                 else if (lhs.equals("_l"))
/*      */                 {
/* 1791 */                   long leechers = Long.parseLong(rhs);
/*      */                   
/* 1793 */                   map.put("leechers", Long.valueOf(leechers));
/*      */                 }
/* 1795 */                 else if (lhs.equals("_c"))
/*      */                 {
/* 1797 */                   map.put("cdp", rhs);
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1808 */           result.add(map);
/*      */         }
/*      */       }
/*      */       else {
/* 1812 */         Map<String, Object> map = new HashMap();
/*      */         
/*      */ 
/*      */ 
/* 1816 */         int p1 = link.lastIndexOf("[[");
/*      */         
/* 1818 */         if ((p1 != -1) && (link.endsWith("]]")))
/*      */         {
/* 1820 */           String title = UrlUtils.decode(link.substring(p1 + 2, link.length() - 2));
/*      */           
/* 1822 */           map.put("title", title);
/*      */           
/* 1824 */           link = link.substring(0, p1);
/*      */           
/* 1826 */           map.put("link", link);
/*      */           
/* 1828 */           result.add(map);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1833 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String escape(String str)
/*      */   {
/* 1840 */     return XUXmlWriter.escapeXML(str);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getFTUXAccepted()
/*      */   {
/* 1846 */     return this.ftux_accepted;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setFTUXAccepted(boolean accepted)
/*      */   {
/* 1853 */     this.ftux_accepted = accepted;
/*      */     
/* 1855 */     COConfigurationManager.setParameter("azbuddy.dchat.ftux.accepted", true);
/*      */     
/* 1857 */     COConfigurationManager.save();
/*      */     
/* 1859 */     for (FTUXStateChangeListener l : this.ftux_listeners)
/*      */     {
/* 1861 */       l.stateChanged(accepted);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addFTUXStateChangeListener(FTUXStateChangeListener listener)
/*      */   {
/* 1869 */     this.ftux_listeners.add(listener);
/*      */     
/* 1871 */     listener.stateChanged(this.ftux_accepted);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeFTUXStateChangeListener(FTUXStateChangeListener listener)
/*      */   {
/* 1878 */     this.ftux_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void logMessage(ChatInstance chat, ChatMessage message)
/*      */   {
/* 1886 */     File log_dir = AEDiagnostics.getLogDir();
/*      */     
/* 1888 */     log_dir = new File(log_dir, "chat");
/*      */     
/* 1890 */     if (!log_dir.exists())
/*      */     {
/* 1892 */       log_dir.mkdir();
/*      */     }
/*      */     
/* 1895 */     File log_file = new File(log_dir, FileUtil.convertOSSpecificChars(chat.getName(), false) + ".log");
/*      */     
/* 1897 */     PrintWriter pw = null;
/*      */     
/*      */     try
/*      */     {
/* 1901 */       pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(log_file, true), "UTF-8"));
/*      */       
/* 1903 */       SimpleDateFormat time_format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
/*      */       
/* 1905 */       String msg = "[" + time_format.format(new Date(message.getTimeStamp())) + "]";
/*      */       
/* 1907 */       msg = msg + " <" + message.getParticipant().getName(true) + "> " + message.getMessage();
/*      */       
/* 1909 */       pw.println(msg);
/*      */ 
/*      */     }
/*      */     catch (Throwable e) {}finally
/*      */     {
/*      */ 
/* 1915 */       if (pw != null)
/*      */       {
/* 1917 */         pw.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ChatInstance getAndShowChat(String network, String key)
/*      */     throws Exception
/*      */   {
/* 1929 */     BuddyPluginViewInterface ui = this.plugin.getSWTUI();
/*      */     
/* 1931 */     if (ui == null)
/*      */     {
/* 1933 */       throw new Exception("UI unavailable");
/*      */     }
/*      */     
/* 1936 */     ChatInstance chat = getChat(network, key);
/*      */     
/* 1938 */     ui.openChat(chat);
/*      */     
/* 1940 */     return chat;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ChatInstance showChat(ChatInstance inst)
/*      */     throws Exception
/*      */   {
/* 1949 */     BuddyPluginViewInterface ui = this.plugin.getSWTUI();
/*      */     
/* 1951 */     if (ui == null)
/*      */     {
/* 1953 */       throw new Exception("UI unavailable");
/*      */     }
/*      */     
/* 1956 */     ui.openChat(inst);
/*      */     
/* 1958 */     return inst;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String pkToString(byte[] pk)
/*      */   {
/* 1965 */     byte[] temp = new byte[3];
/*      */     
/* 1967 */     if (pk != null)
/*      */     {
/* 1969 */       System.arraycopy(pk, 8, temp, 0, 3);
/*      */     }
/*      */     
/* 1972 */     return ByteFormatter.encodeString(temp);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ChatInstance importChat(String import_data)
/*      */     throws Exception
/*      */   {
/* 1981 */     if (this.azmsgsync_pi == null)
/*      */     {
/* 1983 */       throw new Exception("Plugin unavailable ");
/*      */     }
/*      */     
/* 1986 */     Map<String, Object> options = new HashMap();
/*      */     
/* 1988 */     options.put("import_data", import_data.getBytes("UTF-8"));
/*      */     
/* 1990 */     Map<String, Object> reply = (Map)this.azmsgsync_pi.getIPC().invoke("importMessageHandler", new Object[] { options });
/*      */     
/* 1992 */     String key = new String((byte[])reply.get("key"), "UTF-8");
/* 1993 */     String network = (String)reply.get("network");
/* 1994 */     Object handler = reply.get("handler");
/*      */     
/* 1996 */     return getChat(network, key, null, handler, false, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ChatInstance getChat(Download download)
/*      */   {
/* 2003 */     String key = BuddyPluginUtils.getChatKey(download);
/*      */     
/* 2005 */     if (key != null)
/*      */     {
/* 2007 */       String[] networks = PluginCoreUtils.unwrap(download).getDownloadState().getNetworks();
/*      */       
/* 2009 */       boolean has_i2p = false;
/*      */       
/* 2011 */       for (String net : networks)
/*      */       {
/* 2013 */         if (net == "Public") {
/*      */           try
/*      */           {
/* 2016 */             return getChat(net, key);
/*      */ 
/*      */ 
/*      */           }
/*      */           catch (Throwable e) {}
/*      */ 
/*      */         }
/* 2023 */         else if (net == "I2P")
/*      */         {
/* 2025 */           has_i2p = true;
/*      */         }
/*      */       }
/*      */       
/* 2029 */       if (has_i2p) {
/*      */         try
/*      */         {
/* 2032 */           return getChat("I2P", key);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2042 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ChatInstance getChat(String network, String key)
/*      */     throws Exception
/*      */   {
/* 2052 */     return getChat(network, key, null, null, false, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ChatInstance getChat(String network, String key, Map<String, Object> options)
/*      */     throws Exception
/*      */   {
/* 2063 */     return getChat(network, key, null, null, false, options);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ChatInstance getChat(ChatParticipant participant)
/*      */     throws Exception
/*      */   {
/* 2072 */     String key = participant.getChat().getKey() + " - " + participant.getName() + " (outgoing)[" + this.private_chat_id.getAndIncrement() + "]";
/*      */     
/* 2074 */     return getChat(participant.getChat().getNetwork(), key, participant, null, true, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ChatInstance getChat(ChatParticipant parent_participant, Object handler)
/*      */     throws Exception
/*      */   {
/* 2084 */     String key = parent_participant.getChat().getKey() + " - " + parent_participant.getName() + " (incoming)[" + this.private_chat_id.getAndIncrement() + "]";
/*      */     
/* 2086 */     return getChat(parent_participant.getChat().getNetwork(), key, null, handler, true, null);
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
/*      */   private ChatInstance getChat(String network, String key, ChatParticipant private_target, Object handler, boolean is_private_chat, Map<String, Object> options)
/*      */     throws Exception
/*      */   {
/* 2100 */     if (!this.enabled.getValue())
/*      */     {
/* 2102 */       throw new Exception("Plugin not enabled");
/*      */     }
/*      */     
/* 2105 */     String meta_key = network + ":" + key;
/*      */     
/*      */ 
/*      */ 
/* 2109 */     ChatInstance added = null;
/*      */     ChatInstance result;
/* 2111 */     synchronized (this.chat_instances_map)
/*      */     {
/* 2113 */       result = (ChatInstance)this.chat_instances_map.get(meta_key);
/*      */       
/* 2115 */       if (result == null)
/*      */       {
/* 2117 */         result = new ChatInstance(network, key, private_target, is_private_chat, options, null);
/*      */         
/* 2119 */         this.chat_instances_map.put(meta_key, result);
/*      */         
/* 2121 */         this.chat_instances_list.add(result);
/*      */         
/* 2123 */         added = result;
/*      */         
/* 2125 */         if (this.azmsgsync_pi != null) {
/*      */           try
/*      */           {
/* 2128 */             result.bind(this.azmsgsync_pi, handler);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2132 */             this.chat_instances_map.remove(meta_key);
/*      */             
/* 2134 */             this.chat_instances_list.remove(result);
/*      */             
/* 2136 */             added = null;
/*      */             
/* 2138 */             result.destroy();
/*      */             
/* 2140 */             if ((e instanceof Exception))
/*      */             {
/* 2142 */               throw ((Exception)e);
/*      */             }
/*      */             
/* 2145 */             throw new Exception(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 2150 */         result.addReference();
/*      */       }
/*      */       
/* 2153 */       if (this.timer == null)
/*      */       {
/* 2155 */         this.timer = SimpleTimer.addPeriodicEvent("BPB:timer", 2500L, new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 2165 */             for (BuddyPluginBeta.ChatInstance inst : BuddyPluginBeta.this.chat_instances_list)
/*      */             {
/* 2167 */               BuddyPluginBeta.ChatInstance.access$1200(inst);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/* 2174 */     if (added != null)
/*      */     {
/* 2176 */       for (ChatManagerListener l : this.listeners) {
/*      */         try
/*      */         {
/* 2179 */           l.chatAdded(added);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2183 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2188 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ChatInstance peekChatInstance(String network, String key)
/*      */   {
/* 2197 */     return peekChatInstance(network, key, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ChatInstance peekChatInstance(Download download)
/*      */   {
/* 2204 */     String key = BuddyPluginUtils.getChatKey(download);
/*      */     
/* 2206 */     if (key != null)
/*      */     {
/* 2208 */       String[] networks = PluginCoreUtils.unwrap(download).getDownloadState().getNetworks();
/*      */       
/* 2210 */       boolean has_i2p = false;
/*      */       
/* 2212 */       for (String net : networks)
/*      */       {
/* 2214 */         if (net == "Public") {
/*      */           try
/*      */           {
/* 2217 */             return peekChatInstance(net, key);
/*      */ 
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/* 2222 */         else if (net == "I2P")
/*      */         {
/* 2224 */           has_i2p = true;
/*      */         }
/*      */       }
/*      */       
/* 2228 */       if (has_i2p) {
/*      */         try
/*      */         {
/* 2231 */           return peekChatInstance("I2P", key);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2239 */     return null;
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
/*      */   public ChatInstance peekChatInstance(String network, String key, boolean create_if_missing)
/*      */   {
/* 2254 */     String meta_key = network + ":" + key;
/*      */     
/* 2256 */     synchronized (this.chat_instances_map)
/*      */     {
/* 2258 */       ChatInstance inst = (ChatInstance)this.chat_instances_map.get(meta_key);
/*      */       
/* 2260 */       if ((inst == null) && (create_if_missing)) {
/*      */         try
/*      */         {
/* 2263 */           inst = getChat(network, key);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2270 */       return inst;
/*      */     }
/*      */   }
/*      */   
/* 2274 */   private static final Object DOWNLOAD_PEEK_CACHE_KEY = new Object();
/*      */   
/* 2276 */   private static AsyncDispatcher dl_peek_dispatcher = new AsyncDispatcher("dl:peeker");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map<String, Object> peekChat(final Download download, boolean async)
/*      */   {
/* 2283 */     String key = BuddyPluginUtils.getChatKey(download);
/*      */     
/* 2285 */     if (key != null)
/*      */     {
/* 2287 */       if (async)
/*      */       {
/* 2289 */         synchronized (DOWNLOAD_PEEK_CACHE_KEY)
/*      */         {
/* 2291 */           Map<String, Object> map = (Map)download.getUserData(DOWNLOAD_PEEK_CACHE_KEY);
/*      */           
/* 2293 */           if (map != null)
/*      */           {
/*      */ 
/*      */ 
/* 2297 */             return map;
/*      */           }
/*      */           
/* 2300 */           if (dl_peek_dispatcher.getQueueSize() > 200)
/*      */           {
/*      */ 
/*      */ 
/* 2304 */             return null;
/*      */           }
/*      */           
/* 2307 */           map = new HashMap();
/*      */           
/* 2309 */           download.setUserData(DOWNLOAD_PEEK_CACHE_KEY, map);
/*      */           
/* 2311 */           dl_peek_dispatcher.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/*      */               try
/*      */               {
/* 2319 */                 Map<String, Object> map = BuddyPluginBeta.this.peekChat(download, false);
/*      */                 
/* 2321 */                 if ((map != null) && (map.isEmpty()))
/*      */                 {
/* 2323 */                   map = null;
/*      */                 }
/*      */                 
/* 2326 */                 if (map == null)
/*      */                 {
/*      */                   try
/*      */                   {
/*      */ 
/* 2331 */                     Thread.sleep(1000L);
/*      */ 
/*      */ 
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                 }
/* 2337 */                 else if (!map.containsKey("m"))
/*      */                 {
/* 2339 */                   map.put("m", Integer.valueOf(0));
/*      */                 }
/*      */                 
/*      */ 
/* 2343 */                 synchronized (BuddyPluginBeta.DOWNLOAD_PEEK_CACHE_KEY)
/*      */                 {
/* 2345 */                   download.setUserData(BuddyPluginBeta.DOWNLOAD_PEEK_CACHE_KEY, map);
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 2355 */         String[] networks = PluginCoreUtils.unwrap(download).getDownloadState().getNetworks();
/*      */         
/* 2357 */         boolean has_i2p = false;
/*      */         
/* 2359 */         for (String net : networks)
/*      */         {
/* 2361 */           if (net == "Public") {
/*      */             try
/*      */             {
/* 2364 */               return peekChat(net, key);
/*      */ 
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/* 2369 */           else if (net == "I2P")
/*      */           {
/* 2371 */             has_i2p = true;
/*      */           }
/*      */         }
/*      */         
/* 2375 */         if (has_i2p) {
/*      */           try
/*      */           {
/* 2378 */             return peekChat("I2P", key);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2387 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map<String, Object> peekChat(String network, String key)
/*      */   {
/* 2395 */     Map<String, Object> reply = new HashMap();
/*      */     
/*      */     try
/*      */     {
/*      */       PluginInterface pi;
/* 2400 */       synchronized (this.chat_instances_map)
/*      */       {
/* 2402 */         pi = this.azmsgsync_pi;
/*      */       }
/*      */       
/* 2405 */       if (pi != null)
/*      */       {
/* 2407 */         Map<String, Object> options = new HashMap();
/*      */         
/* 2409 */         options.put("network", network);
/* 2410 */         options.put("key", key.getBytes("UTF-8"));
/*      */         
/* 2412 */         options.put("timeout", Integer.valueOf(60000));
/*      */         
/* 2414 */         if (network != "Public")
/*      */         {
/* 2416 */           options.put("server_id", getSharedAnonEndpoint() ? "dchat_shared" : "dchat");
/*      */         }
/*      */         
/* 2419 */         reply = (Map)pi.getIPC().invoke("peekMessageHandler", new Object[] { options });
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2424 */       Debug.out(e);
/*      */     }
/*      */     
/* 2427 */     return reply;
/*      */   }
/*      */   
/*      */ 
/*      */   public List<ChatInstance> getChats()
/*      */   {
/* 2433 */     return this.chat_instances_list.getList();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addChatActivity(ChatInstance inst, ChatMessage message)
/*      */   {
/* 2441 */     if (inst.getEnableNotificationsPost())
/*      */     {
/* 2443 */       if (message != null)
/*      */       {
/* 2445 */         AZ3Functions.provider provider = AZ3Functions.getProvider();
/*      */         
/* 2447 */         if (provider == null)
/*      */         {
/* 2449 */           return;
/*      */         }
/*      */         
/* 2452 */         BuddyPluginViewInterface ui = this.plugin.getSWTUI();
/*      */         
/*      */         String str;
/*      */         
/* 2456 */         if (ui != null)
/*      */         {
/* 2458 */           str = ui.renderMessage(inst, message);
/*      */         }
/*      */         else
/*      */         {
/* 2462 */           str = message.getMessage();
/*      */         }
/*      */         
/* 2465 */         String chan_name = inst.getName(true);
/*      */         
/* 2467 */         int pos = chan_name.lastIndexOf('[');
/*      */         
/* 2469 */         if ((pos != -1) && (chan_name.endsWith("]")))
/*      */         {
/* 2471 */           chan_name = chan_name.substring(0, pos);
/*      */         }
/*      */         
/* 2474 */         String str = chan_name + ": " + str;
/*      */         
/* 2476 */         Map<String, String> cb_data = new HashMap();
/*      */         
/* 2478 */         cb_data.put("allowReAdd", "true");
/* 2479 */         cb_data.put("net", inst.getNetwork());
/* 2480 */         cb_data.put("key", inst.getKey());
/*      */         
/* 2482 */         provider.addLocalActivity(inst.getNetAndKey(), "image.sidebar.chat-overview", str, new String[] { MessageText.getString("label.view") }, ActivityCallback.class, cb_data);
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
/*      */ 
/*      */   public static class ActivityCallback
/*      */     implements com.aelitis.azureus.core.util.AZ3Functions.provider.LocalActivityCallback
/*      */   {
/*      */     public void actionSelected(String action, Map<String, String> data)
/*      */     {
/* 2501 */       String net = (String)data.get("net");
/* 2502 */       String key = (String)data.get("key");
/*      */       
/* 2504 */       if ((net != null) && (key != null))
/*      */       {
/* 2506 */         BuddyPluginUtils.createBetaChat(net, key, null);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(ChatManagerListener l, boolean fire_for_existing)
/*      */   {
/* 2516 */     this.listeners.add(l);
/*      */     
/* 2518 */     if (fire_for_existing)
/*      */     {
/* 2520 */       for (ChatInstance inst : this.chat_instances_list)
/*      */       {
/* 2522 */         l.chatAdded(inst);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(ChatManagerListener l)
/*      */   {
/* 2531 */     this.listeners.remove(l);
/*      */   }
/*      */   
/* 2534 */   private static Pattern auto_dup_pattern1 = Pattern.compile("File '(.*?)' is");
/* 2535 */   private static Pattern auto_dup_pattern2 = Pattern.compile(":([a-zA-Z2-7]{32})", 2);
/* 2536 */   private static Pattern auto_dup_pattern3 = Pattern.compile("See (http://wiki.vuze.com/w/Swarm_Merging)");
/*      */   
/* 2538 */   private static Pattern[] auto_dup_patterns = { auto_dup_pattern1, auto_dup_pattern2, auto_dup_pattern3 };
/*      */   
/*      */ 
/*      */   public class ChatInstance
/*      */   {
/*      */     public static final String OPT_INVISIBLE = "invisible";
/*      */     
/*      */     private static final int MSG_HISTORY_MAX = 512;
/*      */     
/*      */     private final String network;
/*      */     
/*      */     private final String key;
/*      */     
/*      */     private boolean is_private_chat;
/*      */     
/*      */     private boolean is_invisible_chat;
/*      */     private final BuddyPluginBeta.ChatParticipant private_target;
/* 2555 */     private Object binding_lock = new Object();
/*      */     
/*      */     private AESemaphore binding_sem;
/*      */     
/*      */     private volatile PluginInterface msgsync_pi;
/*      */     
/*      */     private volatile Object handler;
/*      */     
/*      */     private byte[] my_public_key;
/*      */     private byte[] managing_public_key;
/*      */     private boolean read_only;
/*      */     private int ipc_version;
/*      */     private InetSocketAddress my_address;
/* 2568 */     private Object chat_lock = this;
/*      */     
/* 2570 */     private AtomicInteger message_uid_next = new AtomicInteger();
/*      */     
/* 2572 */     private List<BuddyPluginBeta.ChatMessage> messages = new ArrayList();
/* 2573 */     private ByteArrayHashMap<String> message_ids = new ByteArrayHashMap();
/*      */     
/*      */     private int messages_not_mine_count;
/* 2576 */     private ByteArrayHashMap<BuddyPluginBeta.ChatParticipant> participants = new ByteArrayHashMap();
/*      */     
/* 2578 */     private Map<String, List<BuddyPluginBeta.ChatParticipant>> nick_clash_map = new HashMap();
/*      */     
/* 2580 */     private CopyOnWriteList<BuddyPluginBeta.ChatListener> listeners = new CopyOnWriteList();
/*      */     
/* 2582 */     private Map<Object, Object> user_data = new HashMap();
/*      */     
/* 2584 */     private LinkedHashMap<String, String> auto_dup_set = new LinkedHashMap(500, 0.75F, true)
/*      */     {
/*      */ 
/*      */ 
/*      */       protected boolean removeEldestEntry(Map.Entry<String, String> eldest)
/*      */       {
/*      */ 
/* 2591 */         return size() > 500;
/*      */       }
/*      */     };
/*      */     
/*      */     private boolean keep_alive;
/*      */     
/*      */     private boolean have_interest;
/*      */     
/*      */     private Map<String, Object> status;
/*      */     
/*      */     private boolean is_shared_nick;
/*      */     
/*      */     private String instance_nick;
/*      */     
/*      */     private int reference_count;
/*      */     
/*      */     private BuddyPluginBeta.ChatMessage last_message_requiring_attention;
/*      */     
/*      */     private boolean message_outstanding;
/*      */     
/*      */     private boolean is_favourite;
/*      */     
/*      */     private boolean auto_notify;
/*      */     
/*      */     private boolean save_messages;
/*      */     
/*      */     private boolean log_messages;
/*      */     private boolean auto_mute;
/*      */     private boolean enable_notification_posts;
/*      */     private boolean disable_new_msg_indications;
/*      */     private boolean destroyed;
/*      */     private TimerEvent sort_event;
/*      */     private boolean sort_force_changed;
/*      */     
/*      */     private ChatInstance(String _network, BuddyPluginBeta.ChatParticipant _key, boolean _private_target, Map<String, Object> _is_private_chat)
/*      */     {
/* 2627 */       this.network = _network;
/* 2628 */       this.key = _key;
/*      */       
/*      */ 
/*      */ 
/* 2632 */       this.private_target = _private_target;
/* 2633 */       this.is_private_chat = _is_private_chat;
/*      */       
/* 2635 */       this.is_shared_nick = BuddyPluginBeta.this.getSharedNick(this.network, this.key);
/* 2636 */       this.instance_nick = BuddyPluginBeta.this.getNick(this.network, this.key);
/*      */       
/* 2638 */       if (!this.is_private_chat)
/*      */       {
/* 2640 */         this.is_favourite = BuddyPluginBeta.this.getFavourite(this.network, this.key);
/* 2641 */         this.save_messages = BuddyPluginBeta.this.getSaveMessages(this.network, this.key);
/* 2642 */         this.log_messages = BuddyPluginBeta.this.getLogMessages(this.network, this.key);
/* 2643 */         this.auto_mute = BuddyPluginBeta.this.getAutoMute(this.network, this.key);
/* 2644 */         this.disable_new_msg_indications = BuddyPluginBeta.this.getDisableNewMsgIndications(this.network, this.key);
/*      */       }
/*      */       
/* 2647 */       this.enable_notification_posts = BuddyPluginBeta.this.getEnableNotificationsPost(this.network, this.key);
/*      */       
/* 2649 */       if (_options != null)
/*      */       {
/* 2651 */         Boolean invis = (Boolean)_options.get("invisible");
/*      */         
/* 2653 */         if ((invis != null) && (invis.booleanValue()))
/*      */         {
/* 2655 */           this.is_invisible_chat = true;
/*      */         }
/*      */       }
/*      */       
/* 2659 */       addReference();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public ChatInstance getClone()
/*      */       throws Exception
/*      */     {
/* 2667 */       if (this.is_private_chat)
/*      */       {
/* 2669 */         addReference();
/*      */         
/* 2671 */         return this;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2677 */       return BuddyPluginBeta.this.getChat(this.network, this.key);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void addReference()
/*      */     {
/* 2684 */       synchronized (this.chat_lock)
/*      */       {
/* 2686 */         this.reference_count += 1;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getName()
/*      */     {
/* 2695 */       return getName(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getName(boolean abbreviated)
/*      */     {
/* 2702 */       String str = this.key;
/*      */       
/* 2704 */       int pos = str.lastIndexOf('[');
/*      */       
/* 2706 */       if ((pos != -1) && (str.endsWith("]")))
/*      */       {
/* 2708 */         String temp = str.substring(pos + 1, str.length() - 1);
/*      */         
/* 2710 */         if (temp.contains("pk="))
/*      */         {
/* 2712 */           str = str.substring(0, pos);
/*      */           
/* 2714 */           if (temp.contains("ro=1"))
/*      */           {
/* 2716 */             str = str + "[R]";
/*      */           }
/*      */           else {
/* 2719 */             str = str + "[M]";
/*      */           }
/*      */         }
/*      */         else {
/* 2723 */           str = str.substring(0, pos);
/*      */         }
/*      */       }
/*      */       
/* 2727 */       if (abbreviated)
/*      */       {
/* 2729 */         return MessageText.getString(this.network == "Public" ? "label.public.medium" : "label.anon.medium") + " - '" + str + "'";
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2735 */       return MessageText.getString(this.network == "Public" ? "label.public" : "label.anon") + " - '" + str + "'";
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getShortName()
/*      */     {
/* 2746 */       String short_name = getName();
/*      */       
/* 2748 */       if (short_name.length() > 60)
/*      */       {
/* 2750 */         short_name = short_name.substring(0, 60) + "...";
/*      */       }
/*      */       
/* 2753 */       return short_name;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getNetwork()
/*      */     {
/* 2759 */       return this.network;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getKey()
/*      */     {
/* 2765 */       return this.key;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isFavourite()
/*      */     {
/* 2771 */       return this.is_favourite;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setAutoNotify(boolean b)
/*      */     {
/* 2778 */       this.auto_notify = b;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isAutoNotify()
/*      */     {
/* 2784 */       return this.auto_notify;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isInteresting()
/*      */     {
/* 2790 */       return this.have_interest;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setInteresting(boolean b)
/*      */     {
/* 2797 */       this.have_interest = b;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isStatistics()
/*      */     {
/* 2803 */       return this.key.startsWith("Statistics:");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setFavourite(boolean b)
/*      */     {
/* 2810 */       if (!this.is_private_chat)
/*      */       {
/* 2812 */         if (b != this.is_favourite)
/*      */         {
/* 2814 */           this.is_favourite = b;
/*      */           
/* 2816 */           BuddyPluginBeta.this.setFavourite(this.network, this.key, b);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean getSaveMessages()
/*      */     {
/* 2824 */       return this.save_messages;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setSaveMessages(boolean b)
/*      */     {
/* 2831 */       if (!this.is_private_chat)
/*      */       {
/* 2833 */         if (b != this.save_messages)
/*      */         {
/* 2835 */           this.save_messages = b;
/*      */           
/* 2837 */           BuddyPluginBeta.this.setSaveMessages(this.network, this.key, b);
/*      */           
/* 2839 */           Map<String, Object> options = new HashMap();
/*      */           
/* 2841 */           options.put("save_messages", Boolean.valueOf(b));
/*      */           try
/*      */           {
/* 2844 */             updateOptions(options);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2848 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean getLogMessages()
/*      */     {
/* 2857 */       return this.log_messages;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setLogMessages(boolean b)
/*      */     {
/* 2864 */       if (!this.is_private_chat)
/*      */       {
/* 2866 */         if (b != this.log_messages)
/*      */         {
/* 2868 */           this.log_messages = b;
/*      */           
/* 2870 */           BuddyPluginBeta.this.setLogMessages(this.network, this.key, b);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean getAutoMute()
/*      */     {
/* 2879 */       return this.auto_mute;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setAutoMute(boolean b)
/*      */     {
/* 2886 */       if (!this.is_private_chat)
/*      */       {
/* 2888 */         if (b != this.auto_mute)
/*      */         {
/* 2890 */           this.auto_mute = b;
/*      */           
/* 2892 */           BuddyPluginBeta.this.setAutoMute(this.network, this.key, b);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean getDisableNewMsgIndications()
/*      */     {
/* 2901 */       return this.disable_new_msg_indications;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setDisableNewMsgIndications(boolean b)
/*      */     {
/* 2908 */       if (!this.is_private_chat)
/*      */       {
/* 2910 */         if (b != this.disable_new_msg_indications)
/*      */         {
/* 2912 */           this.disable_new_msg_indications = b;
/*      */           
/* 2914 */           BuddyPluginBeta.this.setDisableNewMsgIndications(this.network, this.key, b);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean getEnableNotificationsPost()
/*      */     {
/* 2922 */       return this.enable_notification_posts;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setEnableNotificationsPost(boolean b)
/*      */     {
/* 2929 */       if (b != this.enable_notification_posts)
/*      */       {
/* 2931 */         this.enable_notification_posts = b;
/*      */         
/* 2933 */         BuddyPluginBeta.this.setEnableNotificationsPost(this.network, this.key, b);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void setSpammer(BuddyPluginBeta.ChatParticipant participant, boolean is_spammer)
/*      */     {
/* 2942 */       Map<String, Object> options = new HashMap();
/*      */       
/* 2944 */       options.put("pk", participant.getPublicKey());
/* 2945 */       options.put("spammer", Boolean.valueOf(is_spammer));
/*      */       try
/*      */       {
/* 2948 */         updateOptions(options);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2952 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isManaged()
/*      */     {
/* 2959 */       return this.managing_public_key != null;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean amManager()
/*      */     {
/* 2965 */       return (this.managing_public_key != null) && (Arrays.equals(this.my_public_key, this.managing_public_key));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean isManagedFor(String network, String key)
/*      */     {
/* 2973 */       if (getNetwork() != network)
/*      */       {
/* 2975 */         return false;
/*      */       }
/*      */       
/* 2978 */       return getKey().equals(key + "[pk=" + Base32.encode(getPublicKey()) + "]");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public ChatInstance getManagedChannel()
/*      */       throws Exception
/*      */     {
/* 2986 */       if (isManaged())
/*      */       {
/* 2988 */         throw new Exception("Channel is already managed");
/*      */       }
/*      */       
/* 2991 */       String new_key = getKey() + "[pk=" + Base32.encode(getPublicKey()) + "]";
/*      */       
/* 2993 */       ChatInstance inst = BuddyPluginBeta.this.getChat(getNetwork(), new_key);
/*      */       
/* 2995 */       return inst;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean isReadOnlyFor(String network, String key)
/*      */     {
/* 3003 */       if (getNetwork() != network)
/*      */       {
/* 3005 */         return false;
/*      */       }
/*      */       
/* 3008 */       return getKey().equals(key + "[pk=" + Base32.encode(getPublicKey()) + "&ro=1]");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public ChatInstance getReadOnlyChannel()
/*      */       throws Exception
/*      */     {
/* 3016 */       if (isManaged())
/*      */       {
/* 3018 */         throw new Exception("Channel is already managed");
/*      */       }
/*      */       
/* 3021 */       String new_key = getKey() + "[pk=" + Base32.encode(getPublicKey()) + "&ro=1]";
/*      */       
/* 3023 */       ChatInstance inst = BuddyPluginBeta.this.getChat(getNetwork(), new_key);
/*      */       
/* 3025 */       return inst;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isReadOnly()
/*      */     {
/* 3031 */       return (this.read_only) && (!amManager());
/*      */     }
/*      */     
/*      */ 
/*      */     public String getURL()
/*      */     {
/* 3037 */       if (this.network == "Public")
/*      */       {
/* 3039 */         return "chat:?" + UrlUtils.encode(this.key);
/*      */       }
/*      */       
/*      */ 
/* 3043 */       return "chat:anon:?" + UrlUtils.encode(this.key);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[] getPublicKey()
/*      */     {
/* 3050 */       return this.my_public_key;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isInvisible()
/*      */     {
/* 3056 */       return this.is_invisible_chat;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isPrivateChat()
/*      */     {
/* 3062 */       return this.is_private_chat;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isAnonymous()
/*      */     {
/* 3068 */       return this.network != "Public";
/*      */     }
/*      */     
/*      */ 
/*      */     public String getNetAndKey()
/*      */     {
/* 3074 */       return this.network + ": " + this.key;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setKeepAlive(boolean b)
/*      */     {
/* 3081 */       this.keep_alive = b;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean getKeepAlive()
/*      */     {
/* 3087 */       return this.keep_alive;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getDefaultNickname()
/*      */     {
/* 3093 */       return BuddyPluginBeta.this.pkToString(getPublicKey());
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isSharedNickname()
/*      */     {
/* 3099 */       return this.is_shared_nick;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setSharedNickname(boolean _shared)
/*      */     {
/* 3106 */       if (_shared != this.is_shared_nick)
/*      */       {
/* 3108 */         this.is_shared_nick = _shared;
/*      */         
/* 3110 */         BuddyPluginBeta.this.setSharedNick(this.network, this.key, _shared);
/*      */         
/* 3112 */         updated();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public String getInstanceNickname()
/*      */     {
/* 3119 */       return this.instance_nick;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setInstanceNickname(String _nick)
/*      */     {
/* 3126 */       if (!_nick.equals(this.instance_nick))
/*      */       {
/* 3128 */         this.instance_nick = _nick;
/*      */         
/* 3130 */         BuddyPluginBeta.this.setNick(this.network, this.key, _nick);
/*      */         
/* 3132 */         updated();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public String getNickname(boolean use_default)
/*      */     {
/*      */       String nick;
/*      */       
/*      */       String nick;
/* 3142 */       if (this.is_shared_nick)
/*      */       {
/* 3144 */         nick = this.network == "Public" ? BuddyPluginBeta.this.shared_public_nickname : BuddyPluginBeta.this.shared_anon_nickname;
/*      */       }
/*      */       else
/*      */       {
/* 3148 */         nick = this.instance_nick;
/*      */       }
/*      */       
/* 3151 */       if ((nick.length() == 0) && (use_default))
/*      */       {
/* 3153 */         return getDefaultNickname();
/*      */       }
/*      */       
/* 3156 */       return nick;
/*      */     }
/*      */     
/*      */ 
/*      */     private Object getHandler()
/*      */     {
/* 3162 */       return this.handler;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void bind(PluginInterface _msgsync_pi, Object _handler)
/*      */       throws Exception
/*      */     {
/* 3172 */       boolean inform_avail = false;
/*      */       
/* 3174 */       synchronized (this.binding_lock)
/*      */       {
/* 3176 */         this.binding_sem = new AESemaphore("bpb:bind");
/*      */         
/*      */         try
/*      */         {
/* 3180 */           this.msgsync_pi = _msgsync_pi;
/*      */           
/* 3182 */           if (_handler != null)
/*      */           {
/* 3184 */             this.handler = _handler;
/*      */             try
/*      */             {
/* 3187 */               Map<String, Object> options = new HashMap();
/*      */               
/* 3189 */               options.put("handler", _handler);
/*      */               
/* 3191 */               options.put("addlistener", this);
/*      */               
/* 3193 */               Map<String, Object> reply = (Map)this.msgsync_pi.getIPC().invoke("updateMessageHandler", new Object[] { options });
/*      */               
/* 3195 */               this.my_public_key = ((byte[])reply.get("pk"));
/* 3196 */               this.managing_public_key = ((byte[])reply.get("mpk"));
/* 3197 */               Boolean ro = (Boolean)reply.get("ro");
/*      */               
/* 3199 */               this.read_only = ((ro != null) && (ro.booleanValue()));
/*      */               
/* 3201 */               Number ipc_v = (Number)reply.get("ipc_version");
/*      */               
/* 3203 */               this.ipc_version = (ipc_v == null ? 1 : ipc_v.intValue());
/*      */               
/* 3205 */               inform_avail = true;
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 3209 */               throw new Exception(e);
/*      */             }
/*      */           }
/*      */           else {
/*      */             try {
/* 3214 */               Map<String, Object> options = new HashMap();
/*      */               
/* 3216 */               options.put("network", this.network);
/* 3217 */               options.put("key", this.key.getBytes("UTF-8"));
/*      */               
/* 3219 */               if (this.private_target != null)
/*      */               {
/* 3221 */                 options.put("parent_handler", this.private_target.getChat().getHandler());
/* 3222 */                 options.put("target_pk", this.private_target.getPublicKey());
/* 3223 */                 options.put("target_contact", this.private_target.getContact());
/*      */               }
/*      */               
/* 3226 */               if (this.network != "Public")
/*      */               {
/* 3228 */                 options.put("server_id", BuddyPluginBeta.this.getSharedAnonEndpoint() ? "dchat_shared" : "dchat");
/*      */               }
/*      */               
/* 3231 */               options.put("listener", this);
/*      */               
/* 3233 */               if (getSaveMessages())
/*      */               {
/* 3235 */                 options.put("save_messages", Boolean.valueOf(true));
/*      */               }
/*      */               
/* 3238 */               Map<String, Object> reply = (Map)this.msgsync_pi.getIPC().invoke("getMessageHandler", new Object[] { options });
/*      */               
/* 3240 */               this.handler = reply.get("handler");
/*      */               
/* 3242 */               this.my_public_key = ((byte[])reply.get("pk"));
/* 3243 */               this.managing_public_key = ((byte[])reply.get("mpk"));
/* 3244 */               Boolean ro = (Boolean)reply.get("ro");
/*      */               
/* 3246 */               this.read_only = ((ro != null) && (ro.booleanValue()));
/*      */               
/* 3248 */               Number ipc_v = (Number)reply.get("ipc_version");
/*      */               
/* 3250 */               this.ipc_version = (ipc_v == null ? 1 : ipc_v.intValue());
/*      */               
/* 3252 */               inform_avail = true;
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 3256 */               throw new Exception(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/* 3261 */           this.binding_sem.releaseForever();
/*      */           
/* 3263 */           this.binding_sem = null;
/*      */         }
/*      */       }
/*      */       
/* 3267 */       if (inform_avail)
/*      */       {
/* 3269 */         for (BuddyPluginBeta.ChatListener l : this.listeners) {
/*      */           try
/*      */           {
/* 3272 */             l.stateChanged(true);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 3276 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */         
/* 3280 */         if (getKey().startsWith("General: "))
/*      */         {
/* 3282 */           sendLocalMessage("!*" + MessageText.getString("azbuddy.dchat.welcome.general") + "*!", null, 2);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void updateOptions(Map<String, Object> options)
/*      */       throws Exception
/*      */     {
/* 3293 */       if ((this.handler == null) || (this.msgsync_pi == null))
/*      */       {
/* 3295 */         Debug.out("No handler!");
/*      */       }
/*      */       else
/*      */       {
/* 3299 */         options.put("handler", this.handler);
/*      */         
/* 3301 */         this.msgsync_pi.getIPC().invoke("updateMessageHandler", new Object[] { options });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private void unbind()
/*      */     {
/* 3308 */       for (BuddyPluginBeta.ChatListener l : this.listeners) {
/*      */         try
/*      */         {
/* 3311 */           l.stateChanged(false);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3315 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/* 3319 */       this.handler = null;
/* 3320 */       this.msgsync_pi = null;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isAvailable()
/*      */     {
/* 3326 */       return this.handler != null;
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public BuddyPluginBeta.ChatMessage[] getHistory()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 1331	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:chat_lock	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: getfield 1338	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:messages	Ljava/util/List;
/*      */       //   11: aload_0
/*      */       //   12: getfield 1338	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:messages	Ljava/util/List;
/*      */       //   15: invokeinterface 1576 1 0
/*      */       //   20: anewarray 812	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatMessage
/*      */       //   23: invokeinterface 1584 2 0
/*      */       //   28: checkcast 792	[Lcom/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatMessage;
/*      */       //   31: aload_1
/*      */       //   32: monitorexit
/*      */       //   33: areturn
/*      */       //   34: astore_2
/*      */       //   35: aload_1
/*      */       //   36: monitorexit
/*      */       //   37: aload_2
/*      */       //   38: athrow
/*      */       // Line number table:
/*      */       //   Java source line #3332	-> byte code offset #0
/*      */       //   Java source line #3334	-> byte code offset #7
/*      */       //   Java source line #3335	-> byte code offset #34
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	39	0	this	ChatInstance
/*      */       //   5	31	1	Ljava/lang/Object;	Object
/*      */       //   34	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	33	34	finally
/*      */       //   34	37	34	finally
/*      */     }
/*      */     
/*      */     private void update()
/*      */     {
/* 3341 */       PluginInterface current_pi = this.msgsync_pi;
/* 3342 */       Object current_handler = this.handler;
/*      */       
/* 3344 */       if ((current_handler != null) && (current_pi != null)) {
/*      */         try
/*      */         {
/* 3347 */           Map<String, Object> options = new HashMap();
/*      */           
/* 3349 */           options.put("handler", current_handler);
/*      */           
/* 3351 */           this.status = ((Map)current_pi.getIPC().invoke("getStatus", new Object[] { options }));
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3355 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/* 3359 */       updated();
/*      */     }
/*      */     
/*      */ 
/*      */     private void updated()
/*      */     {
/* 3365 */       for (BuddyPluginBeta.ChatListener l : this.listeners) {
/*      */         try
/*      */         {
/* 3368 */           l.updated();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3372 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void handleDrop(String str) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getEstimatedNodes()
/*      */     {
/* 3387 */       Map<String, Object> map = this.status;
/*      */       
/* 3389 */       if (map == null)
/*      */       {
/* 3391 */         return -1;
/*      */       }
/*      */       
/* 3394 */       return ((Number)map.get("node_est")).intValue();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public int getMessageCount(boolean not_mine)
/*      */     {
/* 3401 */       if (not_mine)
/*      */       {
/* 3403 */         return this.messages_not_mine_count;
/*      */       }
/*      */       
/*      */ 
/* 3407 */       return this.messages.size();
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
/*      */     public int getIncomingSyncState()
/*      */     {
/* 3421 */       Map<String, Object> map = this.status;
/*      */       
/* 3423 */       if (map == null)
/*      */       {
/* 3425 */         return -3;
/*      */       }
/*      */       
/* 3428 */       Number in_pending = (Number)map.get("msg_in_pending");
/*      */       
/* 3430 */       return in_pending == null ? -2 : in_pending.intValue();
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
/*      */     public int getOutgoingSyncState()
/*      */     {
/* 3443 */       Map<String, Object> map = this.status;
/*      */       
/* 3445 */       if (map == null)
/*      */       {
/* 3447 */         return -3;
/*      */       }
/*      */       
/* 3450 */       Number out_pending = (Number)map.get("msg_out_pending");
/*      */       
/* 3452 */       return out_pending == null ? -2 : out_pending.intValue();
/*      */     }
/*      */     
/*      */ 
/*      */     public String getStatus()
/*      */     {
/* 3458 */       PluginInterface current_pi = this.msgsync_pi;
/* 3459 */       Object current_handler = this.handler;
/*      */       
/* 3461 */       if (current_pi == null)
/*      */       {
/* 3463 */         return MessageText.getString("azbuddy.dchat.status.noplugin");
/*      */       }
/*      */       
/* 3466 */       if (current_handler == null)
/*      */       {
/* 3468 */         return MessageText.getString("azbuddy.dchat.status.nohandler");
/*      */       }
/*      */       
/* 3471 */       Map<String, Object> map = this.status;
/*      */       
/* 3473 */       if (map == null)
/*      */       {
/* 3475 */         return MessageText.getString("azbuddy.dchat.status.notavail");
/*      */       }
/*      */       
/* 3478 */       int status = ((Number)map.get("status")).intValue();
/* 3479 */       int dht_count = ((Number)map.get("dht_nodes")).intValue();
/*      */       
/* 3481 */       int nodes_local = ((Number)map.get("nodes_local")).intValue();
/* 3482 */       int nodes_live = ((Number)map.get("nodes_live")).intValue();
/* 3483 */       int nodes_dying = ((Number)map.get("nodes_dying")).intValue();
/*      */       
/* 3485 */       int req_in = ((Number)map.get("req_in")).intValue();
/* 3486 */       double req_in_rate = ((Number)map.get("req_in_rate")).doubleValue();
/* 3487 */       int req_out_ok = ((Number)map.get("req_out_ok")).intValue();
/* 3488 */       int req_out_fail = ((Number)map.get("req_out_fail")).intValue();
/* 3489 */       double req_out_rate = ((Number)map.get("req_out_rate")).doubleValue();
/*      */       
/* 3491 */       if ((status == 0) || (status == 1))
/*      */       {
/*      */         String arg2;
/*      */         String arg1;
/*      */         String arg2;
/* 3496 */         if (isPrivateChat())
/*      */         {
/* 3498 */           String arg1 = MessageText.getString("label.private.chat") + ": ";
/* 3499 */           arg2 = "";
/*      */         } else {
/*      */           String arg2;
/* 3502 */           if (status == 0)
/*      */           {
/* 3504 */             String arg1 = MessageText.getString("pairing.status.initialising") + ": ";
/* 3505 */             arg2 = "DHT=" + (dht_count < 0 ? "..." : String.valueOf(dht_count)) + ", ";
/*      */           } else { String arg2;
/* 3507 */             if (status == 1)
/*      */             {
/* 3509 */               String arg1 = "";
/* 3510 */               arg2 = "DHT=" + dht_count + ", ";
/*      */             }
/*      */             else {
/* 3513 */               arg1 = "";
/* 3514 */               arg2 = "";
/*      */             }
/*      */           }
/*      */         }
/* 3518 */         String arg3 = nodes_local + "/" + nodes_live + "/" + nodes_dying;
/* 3519 */         String arg4 = DisplayFormatters.formatDecimal(req_out_rate, 1) + "/" + DisplayFormatters.formatDecimal(req_in_rate, 1);
/*      */         
/* 3521 */         String str = MessageText.getString("azbuddy.dchat.node.status", new String[] { arg1, arg2, arg3, arg4 });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 3526 */         if (isReadOnly())
/*      */         {
/* 3528 */           str = str + ", R-";
/*      */         }
/* 3530 */         else if (amManager())
/*      */         {
/* 3532 */           if (this.read_only)
/*      */           {
/* 3534 */             str = str + ", R+";
/*      */           }
/*      */           else
/*      */           {
/* 3538 */             str = str + ", M+";
/*      */           }
/* 3540 */         } else if (isManaged())
/*      */         {
/* 3542 */           str = str + ", M-";
/*      */         }
/*      */         
/* 3545 */         if (Constants.isCVSVersion())
/*      */         {
/* 3547 */           str = str + ", Refs=" + this.reference_count;
/*      */         }
/*      */         
/* 3550 */         return str;
/*      */       }
/*      */       
/*      */ 
/* 3554 */       return MessageText.getString("azbuddy.dchat.status.destroyed");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void sortMessages(boolean force_change)
/*      */     {
/* 3566 */       synchronized (this.chat_lock)
/*      */       {
/* 3568 */         if (force_change)
/*      */         {
/* 3570 */           this.sort_force_changed = true;
/*      */         }
/*      */         
/* 3573 */         if (this.sort_event != null)
/*      */         {
/* 3575 */           return;
/*      */         }
/*      */         
/* 3578 */         this.sort_event = SimpleTimer.addEvent("msgsort", SystemTime.getOffsetTime(500L), new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 3588 */             boolean changed = false;
/*      */             
/* 3590 */             synchronized (BuddyPluginBeta.ChatInstance.this.chat_lock)
/*      */             {
/* 3592 */               BuddyPluginBeta.ChatInstance.this.sort_event = null;
/*      */               
/* 3594 */               changed = BuddyPluginBeta.ChatInstance.this.sortMessagesSupport();
/*      */               
/* 3596 */               if (BuddyPluginBeta.ChatInstance.this.sort_force_changed)
/*      */               {
/* 3598 */                 changed = true;
/*      */                 
/* 3600 */                 BuddyPluginBeta.ChatInstance.this.sort_force_changed = false;
/*      */               }
/*      */             }
/*      */             
/* 3604 */             if (changed)
/*      */             {
/* 3606 */               for (BuddyPluginBeta.ChatListener l : BuddyPluginBeta.ChatInstance.this.listeners)
/*      */               {
/* 3608 */                 l.messagesChanged();
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean sortMessagesSupport()
/*      */     {
/* 3619 */       int num_messages = this.messages.size();
/*      */       
/* 3621 */       ByteArrayHashMap<BuddyPluginBeta.ChatMessage> id_map = new ByteArrayHashMap(num_messages);
/* 3622 */       Map<BuddyPluginBeta.ChatMessage, BuddyPluginBeta.ChatMessage> prev_map = new HashMap(num_messages);
/*      */       
/* 3624 */       Map<BuddyPluginBeta.ChatMessage, Object> next_map = new HashMap(num_messages);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3630 */       for (BuddyPluginBeta.ChatMessage msg : this.messages)
/*      */       {
/*      */ 
/*      */ 
/* 3634 */         byte[] id = msg.getID();
/*      */         
/* 3636 */         id_map.put(id, msg);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3641 */       for (BuddyPluginBeta.ChatMessage msg : this.messages)
/*      */       {
/* 3643 */         byte[] prev_id = msg.getPreviousID();
/*      */         
/* 3645 */         if (prev_id != null)
/*      */         {
/* 3647 */           BuddyPluginBeta.ChatMessage prev_msg = (BuddyPluginBeta.ChatMessage)id_map.get(prev_id);
/*      */           
/* 3649 */           if (prev_msg != null)
/*      */           {
/* 3651 */             BuddyPluginBeta.ChatMessage.access$3400(msg, prev_msg.getID());
/*      */             
/*      */ 
/*      */ 
/* 3655 */             prev_map.put(msg, prev_msg);
/*      */             
/* 3657 */             Object existing = next_map.get(prev_msg);
/*      */             
/* 3659 */             if (existing == null)
/*      */             {
/* 3661 */               next_map.put(prev_msg, msg);
/*      */             }
/* 3663 */             else if ((existing instanceof BuddyPluginBeta.ChatMessage))
/*      */             {
/* 3665 */               List<BuddyPluginBeta.ChatMessage> list = new ArrayList();
/*      */               
/* 3667 */               list.add((BuddyPluginBeta.ChatMessage)existing);
/* 3668 */               list.add(msg);
/*      */               
/* 3670 */               next_map.put(prev_msg, list);
/*      */             }
/*      */             else
/*      */             {
/* 3674 */               ((List)existing).add(msg);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3682 */       Comparator<BuddyPluginBeta.ChatMessage> message_comparator = new Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */         public int compare(BuddyPluginBeta.ChatMessage o1, BuddyPluginBeta.ChatMessage o2)
/*      */         {
/*      */ 
/*      */ 
/* 3690 */           return o1.getUID() - o2.getUID();
/*      */ 
/*      */         }
/*      */         
/*      */ 
/* 3695 */       };
/* 3696 */       Set<BuddyPluginBeta.ChatMessage> linked_messages = new TreeSet(message_comparator);
/*      */       
/* 3698 */       linked_messages.addAll(prev_map.keySet());
/*      */       
/* 3700 */       while (linked_messages.size() > 0)
/*      */       {
/* 3702 */         BuddyPluginBeta.ChatMessage start = (BuddyPluginBeta.ChatMessage)linked_messages.iterator().next();
/*      */         
/* 3704 */         linked_messages.remove(start);
/*      */         
/* 3706 */         BuddyPluginBeta.ChatMessage current = start;
/*      */         
/* 3708 */         int loops = 0;
/*      */         
/*      */         for (;;)
/*      */         {
/* 3712 */           loops++;
/*      */           
/* 3714 */           if (loops > num_messages)
/*      */           {
/* 3716 */             Debug.out("infinte loop");
/*      */             
/* 3718 */             break;
/*      */           }
/*      */           
/* 3721 */           BuddyPluginBeta.ChatMessage prev_msg = (BuddyPluginBeta.ChatMessage)prev_map.get(current);
/*      */           
/* 3723 */           if (prev_msg == null) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 3729 */           linked_messages.remove(prev_msg);
/*      */           
/* 3731 */           if (prev_msg == start)
/*      */           {
/*      */ 
/*      */ 
/* 3735 */             prev_map.put(current, null);
/* 3736 */             next_map.put(prev_msg, null);
/*      */             
/* 3738 */             Debug.out("Loopage");
/*      */             
/* 3740 */             break;
/*      */           }
/*      */           
/*      */ 
/* 3744 */           current = prev_msg;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3752 */       Set<BuddyPluginBeta.ChatMessage> tree_heads = new TreeSet(message_comparator);
/*      */       
/* 3754 */       for (BuddyPluginBeta.ChatMessage msg : this.messages)
/*      */       {
/* 3756 */         BuddyPluginBeta.ChatMessage prev_msg = (BuddyPluginBeta.ChatMessage)prev_map.get(msg);
/*      */         
/* 3758 */         if (prev_msg != null)
/*      */         {
/* 3760 */           int loops = 0;
/*      */           
/*      */           for (;;)
/*      */           {
/* 3764 */             loops++;
/*      */             
/* 3766 */             if (loops > num_messages)
/*      */             {
/* 3768 */               Debug.out("infinte loop");
/*      */               
/* 3770 */               break;
/*      */             }
/*      */             
/* 3773 */             BuddyPluginBeta.ChatMessage prev_prev = (BuddyPluginBeta.ChatMessage)prev_map.get(prev_msg);
/*      */             
/* 3775 */             if (prev_prev == null)
/*      */             {
/* 3777 */               tree_heads.add(prev_msg);
/*      */               
/* 3779 */               break;
/*      */             }
/*      */             
/*      */ 
/* 3783 */             prev_msg = prev_prev;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 3791 */       Set<BuddyPluginBeta.ChatMessage> remainder_set = new HashSet(this.messages);
/*      */       
/* 3793 */       List<BuddyPluginBeta.ChatMessage> result = null;
/*      */       
/* 3795 */       for (BuddyPluginBeta.ChatMessage head : tree_heads)
/*      */       {
/* 3797 */         List<BuddyPluginBeta.ChatMessage> chain = flattenTree(head, next_map, num_messages);
/*      */         
/* 3799 */         remainder_set.removeAll(chain);
/*      */         
/* 3801 */         if (result == null)
/*      */         {
/* 3803 */           result = chain;
/*      */         }
/*      */         else
/*      */         {
/* 3807 */           result = merge(result, chain);
/*      */         }
/*      */       }
/*      */       
/* 3811 */       if (remainder_set.size() > 0)
/*      */       {
/*      */ 
/*      */ 
/* 3815 */         List<BuddyPluginBeta.ChatMessage> remainder = new ArrayList(remainder_set);
/*      */         
/* 3817 */         Collections.sort(remainder, new Comparator()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public int compare(BuddyPluginBeta.ChatMessage m1, BuddyPluginBeta.ChatMessage m2)
/*      */           {
/*      */ 
/*      */ 
/* 3826 */             long l = m1.getTimeStamp() - m2.getTimeStamp();
/*      */             
/* 3828 */             if (l < 0L)
/* 3829 */               return -1;
/* 3830 */             if (l > 0L) {
/* 3831 */               return 1;
/*      */             }
/* 3833 */             return m1.getUID() - m2.getUID();
/*      */           }
/*      */         });
/*      */         
/*      */ 
/* 3838 */         if (result == null)
/*      */         {
/* 3840 */           result = remainder;
/*      */         }
/*      */         else
/*      */         {
/* 3844 */           result = merge(result, remainder);
/*      */         }
/*      */       }
/*      */       
/* 3848 */       if (result == null)
/*      */       {
/* 3850 */         return false;
/*      */       }
/*      */       
/* 3853 */       boolean changed = false;
/*      */       
/* 3855 */       if (this.messages.size() != result.size())
/*      */       {
/* 3857 */         Debug.out("Inconsistent: " + this.messages.size() + "/" + result.size());
/*      */         
/* 3859 */         changed = true;
/*      */       }
/*      */       
/* 3862 */       Set<BuddyPluginBeta.ChatParticipant> participants = new HashSet();
/*      */       
/* 3864 */       for (int i = 0; i < result.size(); i++)
/*      */       {
/* 3866 */         BuddyPluginBeta.ChatMessage msg = (BuddyPluginBeta.ChatMessage)result.get(i);
/*      */         
/* 3868 */         BuddyPluginBeta.ChatParticipant p = msg.getParticipant();
/*      */         
/* 3870 */         participants.add(p);
/*      */         
/* 3872 */         if (!changed)
/*      */         {
/* 3874 */           if (this.messages.get(i) != msg)
/*      */           {
/*      */ 
/* 3877 */             changed = true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 3882 */       if (changed)
/*      */       {
/* 3884 */         this.messages = result;
/*      */         
/* 3886 */         for (BuddyPluginBeta.ChatParticipant p : participants)
/*      */         {
/* 3888 */           BuddyPluginBeta.ChatParticipant.access$3500(p);
/*      */         }
/*      */         
/* 3891 */         Set<BuddyPluginBeta.ChatParticipant> updated = new HashSet();
/*      */         
/* 3893 */         for (BuddyPluginBeta.ChatMessage msg : this.messages)
/*      */         {
/* 3895 */           BuddyPluginBeta.ChatParticipant p = msg.getParticipant();
/*      */           
/* 3897 */           if (BuddyPluginBeta.ChatParticipant.access$3600(p, msg))
/*      */           {
/* 3899 */             updated.add(p);
/*      */           }
/*      */         }
/*      */         
/* 3903 */         for (BuddyPluginBeta.ChatParticipant p : updated)
/*      */         {
/* 3905 */           updated(p);
/*      */         }
/*      */       }
/*      */       
/* 3909 */       return changed;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private List<BuddyPluginBeta.ChatMessage> flattenTree(BuddyPluginBeta.ChatMessage head, Map<BuddyPluginBeta.ChatMessage, Object> next_map, int num_messages)
/*      */     {
/* 3918 */       if (num_messages <= 0)
/*      */       {
/*      */ 
/*      */ 
/* 3922 */         return new ArrayList();
/*      */       }
/*      */       
/* 3925 */       List<BuddyPluginBeta.ChatMessage> chain = new ArrayList(num_messages);
/*      */       
/* 3927 */       BuddyPluginBeta.ChatMessage msg = head;
/*      */       
/*      */       for (;;)
/*      */       {
/* 3931 */         chain.add(msg);
/*      */         
/* 3933 */         num_messages--;
/*      */         
/* 3935 */         Object entry = next_map.get(msg);
/*      */         
/* 3937 */         if ((entry instanceof BuddyPluginBeta.ChatMessage))
/*      */         {
/* 3939 */           msg = (BuddyPluginBeta.ChatMessage)entry;
/*      */         } else {
/* 3941 */           if (!(entry instanceof List))
/*      */             break;
/* 3943 */           List<BuddyPluginBeta.ChatMessage> list = (List)entry;
/*      */           
/* 3945 */           List<BuddyPluginBeta.ChatMessage> current = null;
/*      */           
/* 3947 */           for (BuddyPluginBeta.ChatMessage node : list)
/*      */           {
/* 3949 */             List<BuddyPluginBeta.ChatMessage> temp = flattenTree(node, next_map, num_messages);
/*      */             
/* 3951 */             num_messages -= temp.size();
/*      */             
/* 3953 */             if (current == null)
/*      */             {
/* 3955 */               current = temp;
/*      */             }
/*      */             else
/*      */             {
/* 3959 */               current = merge(current, temp);
/*      */             }
/*      */           }
/*      */           
/* 3963 */           chain.addAll(current);
/*      */           
/* 3965 */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3973 */       return chain;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private List<BuddyPluginBeta.ChatMessage> merge(List<BuddyPluginBeta.ChatMessage> list1, List<BuddyPluginBeta.ChatMessage> list2)
/*      */     {
/* 3981 */       int size1 = list1.size();
/* 3982 */       int size2 = list2.size();
/*      */       
/* 3984 */       List<BuddyPluginBeta.ChatMessage> result = new ArrayList(size1 + size2);
/*      */       
/* 3986 */       int pos1 = 0;
/* 3987 */       int pos2 = 0;
/*      */       
/*      */       for (;;)
/*      */       {
/* 3991 */         if (pos1 == size1)
/*      */         {
/* 3993 */           for (int i = pos2; i < size2; i++)
/*      */           {
/* 3995 */             result.add(list2.get(i));
/*      */           }
/*      */           
/* 3998 */           break;
/*      */         }
/* 4000 */         if (pos2 == size2)
/*      */         {
/* 4002 */           for (int i = pos1; i < size1; i++)
/*      */           {
/* 4004 */             result.add(list1.get(i));
/*      */           }
/*      */           
/* 4007 */           break;
/*      */         }
/*      */         
/*      */ 
/* 4011 */         BuddyPluginBeta.ChatMessage m1 = (BuddyPluginBeta.ChatMessage)list1.get(pos1);
/* 4012 */         BuddyPluginBeta.ChatMessage m2 = (BuddyPluginBeta.ChatMessage)list2.get(pos2);
/*      */         
/* 4014 */         long t1 = m1.getTimeStamp();
/* 4015 */         long t2 = m2.getTimeStamp();
/*      */         
/* 4017 */         if ((t1 < t2) || ((t1 == t2) && (m1.getUID() < m2.getUID())))
/*      */         {
/* 4019 */           result.add(m1);
/*      */           
/* 4021 */           pos1++;
/*      */         }
/*      */         else
/*      */         {
/* 4025 */           result.add(m2);
/*      */           
/* 4027 */           pos2++;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 4032 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void messageReceived(Map<String, Object> message_map)
/*      */       throws IPCException
/*      */     {
/*      */       AESemaphore sem;
/*      */       
/*      */ 
/* 4043 */       synchronized (this.binding_lock)
/*      */       {
/* 4045 */         sem = this.binding_sem;
/*      */       }
/*      */       
/* 4048 */       if (sem != null)
/*      */       {
/* 4050 */         sem.reserve();
/*      */       }
/*      */       
/* 4053 */       BuddyPluginBeta.ChatMessage msg = new BuddyPluginBeta.ChatMessage(BuddyPluginBeta.this, this.message_uid_next.incrementAndGet(), message_map, null);
/*      */       
/* 4055 */       long sequence = msg.getSequence();
/*      */       
/* 4057 */       BuddyPluginBeta.ChatParticipant new_participant = null;
/*      */       
/* 4059 */       boolean sort_outstanding = false;
/*      */       
/* 4061 */       byte[] prev_id = msg.getPreviousID();
/*      */       
/* 4063 */       synchronized (this.chat_lock)
/*      */       {
/* 4065 */         byte[] id = msg.getID();
/*      */         
/* 4067 */         if (this.message_ids.containsKey(id))
/*      */         {
/*      */ 
/*      */ 
/* 4071 */           return;
/*      */         }
/*      */         
/* 4074 */         this.message_ids.put(id, "");
/*      */         
/*      */ 
/*      */ 
/* 4078 */         int old_msgs = this.messages.size();
/*      */         
/* 4080 */         this.messages.add(msg);
/*      */         
/* 4082 */         if (this.messages.size() > 512)
/*      */         {
/* 4084 */           BuddyPluginBeta.ChatMessage removed = (BuddyPluginBeta.ChatMessage)this.messages.remove(0);
/*      */           
/* 4086 */           old_msgs--;
/*      */           
/* 4088 */           this.message_ids.remove(removed.getID());
/*      */           
/* 4090 */           BuddyPluginBeta.ChatParticipant rem_part = removed.getParticipant();
/*      */           
/* 4092 */           BuddyPluginBeta.ChatParticipant.access$3800(rem_part, removed);
/*      */           
/* 4094 */           if (!rem_part.isMe())
/*      */           {
/* 4096 */             this.messages_not_mine_count -= 1;
/*      */           }
/*      */         }
/*      */         
/* 4100 */         int origin = msg.getFlagOrigin();
/*      */         
/* 4102 */         if (origin != 0)
/*      */         {
/* 4104 */           String auto_msg = msg.getMessage();
/*      */           
/* 4106 */           if (auto_msg.contains("File"))
/*      */           {
/* 4108 */             auto_msg = auto_msg.replace('\\', '/');
/*      */           }
/*      */           
/*      */ 
/* 4112 */           for (Pattern p : BuddyPluginBeta.auto_dup_patterns)
/*      */           {
/* 4114 */             Matcher m = p.matcher(auto_msg);
/*      */             
/* 4116 */             while (m.find())
/*      */             {
/* 4118 */               String dup_key = m.group(1);
/*      */               
/* 4120 */               if (this.auto_dup_set.containsKey(dup_key))
/*      */               {
/* 4122 */                 msg.setDuplicate();
/*      */                 
/*      */                 break label344;
/*      */               }
/*      */               
/* 4127 */               this.auto_dup_set.put(dup_key, "");
/*      */             }
/*      */           }
/*      */         }
/*      */         label344:
/* 4132 */         byte[] pk = msg.getPublicKey();
/*      */         
/* 4134 */         BuddyPluginBeta.ChatParticipant participant = (BuddyPluginBeta.ChatParticipant)this.participants.get(pk);
/*      */         
/* 4136 */         if (participant == null)
/*      */         {
/* 4138 */           new_participant = participant = new BuddyPluginBeta.ChatParticipant(BuddyPluginBeta.this, this, pk, null);
/*      */           
/* 4140 */           this.participants.put(pk, participant);
/*      */           
/* 4142 */           BuddyPluginBeta.ChatParticipant.access$4100(participant, msg);
/*      */           
/* 4144 */           if ((this.auto_mute) && (!participant.isMe()))
/*      */           {
/* 4146 */             participant.setIgnored(true);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 4151 */           BuddyPluginBeta.ChatParticipant.access$4100(participant, msg);
/*      */         }
/*      */         
/* 4154 */         if (this.log_messages)
/*      */         {
/* 4156 */           if (!msg.isIgnored())
/*      */           {
/* 4158 */             BuddyPluginBeta.this.logMessage(this, msg);
/*      */           }
/*      */         }
/*      */         
/* 4162 */         if (participant.isMe())
/*      */         {
/* 4164 */           InetSocketAddress address = msg.getAddress();
/*      */           
/* 4166 */           if (address != null)
/*      */           {
/* 4168 */             this.my_address = address;
/*      */           }
/*      */           
/* 4171 */           if (BuddyPluginBeta.ChatMessage.access$4300(msg))
/*      */           {
/* 4173 */             if ((!BuddyPluginBeta.this.getHideRatings()) || (msg.getFlagOrigin() != 1))
/*      */             {
/* 4175 */               if ((!BuddyPluginBeta.this.getHideSearchSubs()) || (msg.getFlagOrigin() != 3))
/*      */               {
/*      */ 
/*      */ 
/* 4179 */                 this.last_message_requiring_attention = msg;
/*      */               }
/*      */             }
/*      */           }
/*      */         } else {
/* 4184 */           if (!msg.isIgnored())
/*      */           {
/* 4186 */             if ((!BuddyPluginBeta.this.getHideRatings()) || (msg.getFlagOrigin() != 1))
/*      */             {
/* 4188 */               if ((!BuddyPluginBeta.this.getHideSearchSubs()) || (msg.getFlagOrigin() != 3))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4194 */                 this.last_message_requiring_attention = msg;
/*      */               }
/*      */             }
/*      */           }
/* 4198 */           this.messages_not_mine_count += 1;
/*      */         }
/*      */         
/* 4201 */         if (this.sort_event != null)
/*      */         {
/* 4203 */           sort_outstanding = true;
/*      */ 
/*      */ 
/*      */         }
/* 4207 */         else if (old_msgs != 0)
/*      */         {
/* 4209 */           if ((prev_id == null) || (!Arrays.equals(prev_id, ((BuddyPluginBeta.ChatMessage)this.messages.get(old_msgs - 1)).getID())))
/*      */           {
/*      */ 
/*      */ 
/* 4213 */             if (msg.getMessageType() == 1)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4219 */               sortMessages(true);
/*      */               
/* 4221 */               sort_outstanding = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 4226 */       if (new_participant != null)
/*      */       {
/* 4228 */         for (BuddyPluginBeta.ChatListener l : this.listeners)
/*      */         {
/* 4230 */           l.participantAdded(new_participant);
/*      */         }
/*      */       }
/*      */       
/* 4234 */       for (BuddyPluginBeta.ChatListener l : this.listeners)
/*      */       {
/* 4236 */         l.messageReceived(msg, sort_outstanding);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Map<String, Object> chatRequested(Map<String, Object> message_map)
/*      */       throws IPCException
/*      */     {
/*      */       AESemaphore sem;
/*      */       
/*      */ 
/* 4248 */       synchronized (this.binding_lock)
/*      */       {
/* 4250 */         sem = this.binding_sem;
/*      */       }
/*      */       
/* 4253 */       if (sem != null)
/*      */       {
/* 4255 */         sem.reserve();
/*      */       }
/*      */       
/* 4258 */       if (isStatistics())
/*      */       {
/* 4260 */         throw new IPCException("Private chat disabled for statistical channels");
/*      */       }
/*      */       
/* 4263 */       if (BuddyPluginBeta.this.private_chat_state == 1)
/*      */       {
/* 4265 */         throw new IPCException("Private chat disabled by recipient");
/*      */       }
/*      */       try
/*      */       {
/* 4269 */         Object new_handler = message_map.get("handler");
/*      */         
/* 4271 */         byte[] remote_pk = (byte[])message_map.get("pk");
/*      */         
/*      */         BuddyPluginBeta.ChatParticipant participant;
/*      */         
/* 4275 */         synchronized (this.chat_lock)
/*      */         {
/* 4277 */           participant = (BuddyPluginBeta.ChatParticipant)this.participants.get(remote_pk);
/*      */         }
/*      */         
/* 4280 */         if (participant == null)
/*      */         {
/* 4282 */           throw new IPCException("Private chat requires you send at least one message to the main chat first");
/*      */         }
/*      */         
/* 4285 */         if ((BuddyPluginBeta.this.private_chat_state == 2) && (!participant.isPinned()))
/*      */         {
/* 4287 */           throw new IPCException("Recipient will only accept private chats from pinned participants");
/*      */         }
/*      */         
/* 4290 */         BuddyPluginViewInterface ui = BuddyPluginBeta.this.plugin.getSWTUI();
/*      */         
/* 4292 */         if (ui == null)
/*      */         {
/* 4294 */           throw new IPCException("Chat unavailable");
/*      */         }
/*      */         
/* 4297 */         ChatInstance inst = BuddyPluginBeta.this.getChat(participant, new_handler);
/*      */         
/* 4299 */         if (!isSharedNickname())
/*      */         {
/* 4301 */           inst.setSharedNickname(false);
/*      */           
/* 4303 */           inst.setInstanceNickname(getInstanceNickname());
/*      */         }
/*      */         
/* 4306 */         ui.openChat(inst);
/*      */         
/* 4308 */         Map<String, Object> reply = new HashMap();
/*      */         
/* 4310 */         reply.put("nickname", participant.getName());
/*      */         
/* 4312 */         return reply;
/*      */       }
/*      */       catch (IPCException e)
/*      */       {
/* 4316 */         throw e;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 4320 */         throw new IPCException(e);
/*      */       }
/*      */     }
/*      */     
/* 4324 */     AsyncDispatcher dispatcher = new AsyncDispatcher("sendAsync");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void sendMessage(String message, Map<String, Object> options)
/*      */     {
/* 4331 */       sendMessage(message, null, options);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void sendMessage(final String message, final Map<String, Object> flags, final Map<String, Object> options)
/*      */     {
/* 4340 */       this.dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*      */ 
/* 4348 */           BuddyPluginBeta.ChatInstance.this.sendMessageSupport(message, flags, options);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void sendRawMessage(final byte[] message, final Map<String, Object> flags, final Map<String, Object> options)
/*      */     {
/* 4359 */       this.dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*      */ 
/* 4367 */           BuddyPluginBeta.ChatInstance.this.sendMessageSupport(message, flags, options);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void sendLocalMessage(final String message, final String[] args, final int message_type)
/*      */     {
/* 4378 */       if (this.ipc_version < 2)
/*      */       {
/* 4380 */         return;
/*      */       }
/*      */       
/* 4383 */       this.dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*      */ 
/* 4391 */           Map<String, Object> options = new HashMap();
/*      */           
/*      */           String raw_message;
/*      */           String raw_message;
/* 4395 */           if ((message.startsWith("!")) && (message.endsWith("!")))
/*      */           {
/* 4397 */             raw_message = message.substring(1, message.length() - 1);
/*      */           }
/*      */           else
/*      */           {
/* 4401 */             raw_message = MessageText.getString(message, args);
/*      */           }
/* 4403 */           options.put("is_local", Boolean.valueOf(true));
/* 4404 */           options.put("message", raw_message);
/* 4405 */           options.put("message_type", Integer.valueOf(message_type));
/*      */           
/* 4407 */           BuddyPluginBeta.ChatInstance.this.sendMessageSupport("", null, options);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void sendControlMessage(final String cmd)
/*      */     {
/* 4416 */       if (this.ipc_version < 3)
/*      */       {
/* 4418 */         return;
/*      */       }
/*      */       
/* 4421 */       this.dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*      */ 
/* 4429 */           Map<String, Object> options = new HashMap();
/*      */           
/* 4431 */           options.put("is_control", Boolean.valueOf(true));
/* 4432 */           options.put("cmd", cmd);
/*      */           
/* 4434 */           BuddyPluginBeta.ChatInstance.this.sendMessageSupport("", null, options);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void sendMessageSupport(Object o_message, Map<String, Object> flags, Map<String, Object> options)
/*      */     {
/* 4445 */       if ((this.handler == null) || (this.msgsync_pi == null))
/*      */       {
/* 4447 */         Debug.out("No handler/plugin");
/*      */       }
/*      */       else
/*      */       {
/* 4451 */         if ((o_message instanceof String))
/*      */         {
/* 4453 */           String message = (String)o_message;
/*      */           
/* 4455 */           if (message.equals("!dump!"))
/*      */           {
/* 4457 */             synchronized (this.chat_lock)
/*      */             {
/* 4459 */               for (BuddyPluginBeta.ChatMessage msg : this.messages)
/*      */               {
/* 4461 */                 System.out.println(msg.getTimeStamp() + ": " + BuddyPluginBeta.this.pkToString(msg.getID()) + ", " + BuddyPluginBeta.this.pkToString(msg.getPreviousID()) + ", " + msg.getSequence() + " - " + msg.getMessage());
/*      */               }
/*      */             }
/* 4464 */             return;
/*      */           }
/* 4466 */           if (message.equals("!sort!"))
/*      */           {
/* 4468 */             sortMessages(false);
/*      */             
/* 4470 */             return;
/*      */           }
/* 4472 */           if (message.equals("!flood!"))
/*      */           {
/* 4474 */             if (BuddyPluginBeta.DEBUG_ENABLED)
/*      */             {
/* 4476 */               SimpleTimer.addPeriodicEvent("flooder", 1500L, new TimerEventPerformer()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void perform(TimerEvent event)
/*      */                 {
/*      */ 
/* 4483 */                   BuddyPluginBeta.ChatInstance.this.sendMessage("flood - " + SystemTime.getCurrentTime(), null);
/*      */                 }
/*      */               });
/*      */             }
/*      */             
/*      */ 
/* 4489 */             return;
/*      */           }
/*      */           
/* 4492 */           if (message.equals("!ftux!"))
/*      */           {
/* 4494 */             BuddyPluginBeta.this.plugin.getBeta().setFTUXAccepted(false);
/*      */             
/* 4496 */             return;
/*      */           }
/*      */           
/* 4499 */           boolean is_me_msg = false;
/*      */           
/* 4501 */           if (message.startsWith("/"))
/*      */           {
/* 4503 */             String[] bits = message.split("[\\s]+", 3);
/*      */             
/* 4505 */             String command = bits[0].toLowerCase(Locale.US);
/*      */             
/* 4507 */             boolean ok = false;
/* 4508 */             boolean missing_params = false;
/*      */             try
/*      */             {
/* 4511 */               if (command.equals("/help"))
/*      */               {
/* 4513 */                 String link = MessageText.getString("azbuddy.dchat.link.url");
/*      */                 
/* 4515 */                 sendLocalMessage("label.see.x.for.help", new String[] { link }, 2);
/*      */                 
/* 4517 */                 ok = true;
/*      */               }
/* 4519 */               else if (command.equals("/join"))
/*      */               {
/* 4521 */                 if (bits.length > 1)
/*      */                 {
/* 4523 */                   bits = message.split("[\\s]+", 2);
/*      */                   
/* 4525 */                   String key = bits[1];
/*      */                   
/* 4527 */                   if ((key.startsWith("\"")) && (key.endsWith("\""))) {
/* 4528 */                     key = key.substring(1, key.length() - 1);
/*      */                   }
/*      */                   
/* 4531 */                   BuddyPluginBeta.this.getAndShowChat(getNetwork(), key);
/*      */                   
/* 4533 */                   ok = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 4537 */                   missing_params = true;
/*      */                 }
/* 4539 */               } else if (command.equals("/nick"))
/*      */               {
/* 4541 */                 if (bits.length > 1)
/*      */                 {
/* 4543 */                   bits = message.split("[\\s]+", 2);
/*      */                   
/* 4545 */                   setSharedNickname(false);
/*      */                   
/* 4547 */                   setInstanceNickname(bits[1]);
/*      */                   
/* 4549 */                   ok = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 4553 */                   missing_params = true;
/*      */                 }
/*      */               }
/* 4556 */               else if (command.equals("/pjoin"))
/*      */               {
/* 4558 */                 if (bits.length > 1)
/*      */                 {
/* 4560 */                   bits = message.split("[\\s]+", 2);
/*      */                   
/* 4562 */                   String key = bits[1];
/*      */                   
/* 4564 */                   if ((key.startsWith("\"")) && (key.endsWith("\""))) {
/* 4565 */                     key = key.substring(1, key.length() - 1);
/*      */                   }
/*      */                   
/* 4568 */                   BuddyPluginBeta.this.getAndShowChat("Public", key);
/*      */                   
/* 4570 */                   ok = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 4574 */                   missing_params = true;
/*      */                 }
/* 4576 */               } else if (command.equals("/ajoin"))
/*      */               {
/* 4578 */                 if (bits.length <= 1)
/*      */                 {
/* 4580 */                   missing_params = true;
/*      */                 } else {
/* 4582 */                   if (!BuddyPluginBeta.this.isI2PAvailable())
/*      */                   {
/* 4584 */                     throw new Exception("I2P not available");
/*      */                   }
/*      */                   
/*      */ 
/* 4588 */                   bits = message.split("[\\s]+", 2);
/*      */                   
/* 4590 */                   String key = bits[1];
/*      */                   
/* 4592 */                   if ((key.startsWith("\"")) && (key.endsWith("\""))) {
/* 4593 */                     key = key.substring(1, key.length() - 1);
/*      */                   }
/*      */                   
/* 4596 */                   BuddyPluginBeta.this.getAndShowChat("I2P", key);
/*      */                   
/* 4598 */                   ok = true;
/*      */                 }
/* 4600 */               } else if ((command.equals("/msg")) || (command.equals("/query")))
/*      */               {
/* 4602 */                 if (bits.length > 1)
/*      */                 {
/* 4604 */                   String nick = bits[1];
/*      */                   
/* 4606 */                   String pm = bits.length == 2 ? "" : bits[2].trim();
/*      */                   
/* 4608 */                   BuddyPluginBeta.ChatParticipant p = getParticipant(nick);
/*      */                   
/* 4610 */                   if (p == null)
/*      */                   {
/* 4612 */                     throw new Exception("Nick not found: " + nick);
/*      */                   }
/* 4614 */                   if (p.isMe())
/*      */                   {
/* 4616 */                     throw new Exception("Can't chat to yourself");
/*      */                   }
/*      */                   
/* 4619 */                   ChatInstance ci = p.createPrivateChat();
/*      */                   
/* 4621 */                   if (pm.length() > 0)
/*      */                   {
/* 4623 */                     ci.sendMessage(pm, new HashMap());
/*      */                   }
/*      */                   
/* 4626 */                   BuddyPluginBeta.this.showChat(ci);
/*      */                   
/* 4628 */                   ok = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 4632 */                   missing_params = true;
/*      */                 }
/* 4634 */               } else if (command.equals("/me"))
/*      */               {
/* 4636 */                 if (bits.length > 1)
/*      */                 {
/* 4638 */                   is_me_msg = true;
/*      */                   
/* 4640 */                   o_message = message.substring(3).trim();
/*      */                   
/* 4642 */                   if (flags == null)
/*      */                   {
/* 4644 */                     flags = new HashMap();
/*      */                   }
/*      */                   
/* 4647 */                   flags.put("t", Integer.valueOf(1));
/*      */                   
/* 4649 */                   ok = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 4653 */                   missing_params = true;
/*      */                 }
/* 4655 */               } else if (command.equals("/ignore"))
/*      */               {
/* 4657 */                 if (bits.length > 1)
/*      */                 {
/* 4659 */                   String nick = bits[1];
/*      */                   
/* 4661 */                   boolean ignore = true;
/*      */                   
/* 4663 */                   if ((nick.equals("-r")) && (bits.length > 2))
/*      */                   {
/* 4665 */                     nick = bits[2];
/*      */                     
/* 4667 */                     ignore = false;
/*      */                   }
/*      */                   
/* 4670 */                   BuddyPluginBeta.ChatParticipant p = getParticipant(nick);
/*      */                   
/* 4672 */                   if (p == null)
/*      */                   {
/* 4674 */                     throw new Exception("Nick not found: " + nick);
/*      */                   }
/*      */                   
/* 4677 */                   p.setIgnored(ignore);
/*      */                   
/*      */ 
/*      */ 
/* 4681 */                   updated(p);
/*      */                   
/* 4683 */                   ok = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 4687 */                   missing_params = true;
/*      */                 }
/* 4689 */               } else if (command.equals("/control"))
/*      */               {
/* 4691 */                 if (this.ipc_version >= 3)
/*      */                 {
/* 4693 */                   String[] bits2 = message.split("[\\s]+", 2);
/*      */                   
/* 4695 */                   if (bits2.length > 1)
/*      */                   {
/* 4697 */                     sendControlMessage(bits2[1]);
/*      */                     
/* 4699 */                     ok = true;
/*      */                   }
/*      */                   else
/*      */                   {
/* 4703 */                     throw new Exception("Invalid command: " + message);
/*      */                   }
/*      */                 }
/*      */               }
/* 4707 */               else if (command.equals("/peek"))
/*      */               {
/* 4709 */                 if (bits.length > 1)
/*      */                 {
/* 4711 */                   Map<String, Object> result = BuddyPluginBeta.this.peekChat(getNetwork(), message.substring(5).trim());
/*      */                   
/* 4713 */                   sendLocalMessage("!" + result + "!", null, 2);
/*      */                   
/* 4715 */                   ok = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 4719 */                   missing_params = true;
/*      */                 }
/* 4721 */               } else if (command.equals("/clone"))
/*      */               {
/* 4723 */                 BuddyPluginBeta.this.getAndShowChat(getNetwork(), getKey());
/*      */                 
/* 4725 */                 ok = true;
/*      */               }
/*      */               
/* 4728 */               if (!ok)
/*      */               {
/* 4730 */                 if (missing_params)
/*      */                 {
/* 4732 */                   throw new Exception("Error: Insufficient parameters for '" + command + "'");
/*      */                 }
/*      */                 
/* 4735 */                 throw new Exception("Error: Unhandled command: " + message);
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 4739 */               sendLocalMessage("!" + Debug.getNestedExceptionMessage(e) + "!", null, 3);
/*      */             }
/*      */             
/* 4742 */             if (!is_me_msg)
/*      */             {
/* 4744 */               return;
/*      */             }
/*      */           }
/*      */         }
/*      */         try
/*      */         {
/* 4750 */           BuddyPluginBeta.ChatMessage prev_message = null;
/* 4751 */           long prev_sequence = -1L;
/*      */           
/* 4753 */           synchronized (this.chat_lock)
/*      */           {
/* 4755 */             int pos = this.messages.size() - 1;
/*      */             
/* 4757 */             int missing_seq = 0;
/*      */             
/* 4759 */             while (pos >= 0)
/*      */             {
/* 4761 */               BuddyPluginBeta.ChatMessage m = (BuddyPluginBeta.ChatMessage)this.messages.get(pos--);
/*      */               
/* 4763 */               if (m.getMessageType() == 1)
/*      */               {
/* 4765 */                 if (prev_message == null)
/*      */                 {
/* 4767 */                   prev_message = m;
/*      */                 }
/*      */                 
/* 4770 */                 prev_sequence = m.getSequence();
/*      */                 
/* 4772 */                 if (prev_sequence > 0L) {
/*      */                   break;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/* 4778 */                 missing_seq++;
/*      */               }
/*      */             }
/*      */             
/*      */ 
/* 4783 */             if (prev_message != null)
/*      */             {
/* 4785 */               prev_sequence += missing_seq;
/*      */             }
/*      */           }
/*      */           
/* 4789 */           if (options == null)
/*      */           {
/* 4791 */             options = new HashMap();
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 4797 */             options = new HashMap(options);
/*      */           }
/*      */           
/* 4800 */           options.put("handler", this.handler);
/*      */           
/* 4802 */           Map<String, Object> payload = new HashMap();
/*      */           
/* 4804 */           if ((o_message instanceof String))
/*      */           {
/* 4806 */             payload.put("msg", ((String)o_message).getBytes("UTF-8"));
/*      */           }
/*      */           else
/*      */           {
/* 4810 */             payload.put("msg", (byte[])o_message);
/*      */           }
/*      */           
/* 4813 */           payload.put("nick", getNickname(false).getBytes("UTF-8"));
/*      */           
/* 4815 */           if (prev_message != null)
/*      */           {
/* 4817 */             payload.put("pre", prev_message.getID());
/* 4818 */             payload.put("seq", Long.valueOf(prev_sequence + 1L));
/*      */           }
/*      */           
/* 4821 */           if (flags != null)
/*      */           {
/* 4823 */             payload.put("f", flags);
/*      */           }
/*      */           
/* 4826 */           options.put("content", BEncoder.encode(payload));
/*      */           
/* 4828 */           Object reply = (Map)this.msgsync_pi.getIPC().invoke("sendMessage", new Object[] { options });
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 4833 */           this.have_interest = true;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 4837 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public String export()
/*      */     {
/* 4845 */       if ((this.handler == null) || (this.msgsync_pi == null))
/*      */       {
/* 4847 */         return "";
/*      */       }
/*      */       try {
/* 4850 */         Map<String, Object> options = new HashMap();
/*      */         
/* 4852 */         options.put("handler", this.handler);
/*      */         
/* 4854 */         Map<String, Object> reply = (Map)this.msgsync_pi.getIPC().invoke("exportMessageHandler", new Object[] { options });
/*      */         
/* 4856 */         return (String)reply.get("export_data");
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 4860 */         Debug.out(e);
/*      */       }
/* 4862 */       return "";
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public List<BuddyPluginBeta.ChatMessage> getMessages()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 1331	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:chat_lock	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: new 827	java/util/ArrayList
/*      */       //   10: dup
/*      */       //   11: aload_0
/*      */       //   12: getfield 1338	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:messages	Ljava/util/List;
/*      */       //   15: invokespecial 1517	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
/*      */       //   18: aload_1
/*      */       //   19: monitorexit
/*      */       //   20: areturn
/*      */       //   21: astore_2
/*      */       //   22: aload_1
/*      */       //   23: monitorexit
/*      */       //   24: aload_2
/*      */       //   25: athrow
/*      */       // Line number table:
/*      */       //   Java source line #4869	-> byte code offset #0
/*      */       //   Java source line #4871	-> byte code offset #7
/*      */       //   Java source line #4872	-> byte code offset #21
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	26	0	this	ChatInstance
/*      */       //   5	18	1	Ljava/lang/Object;	Object
/*      */       //   21	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	20	21	finally
/*      */       //   21	24	21	finally
/*      */     }
/*      */     
/*      */     public boolean hasUnseenMessageWithNick()
/*      */     {
/* 4878 */       List<BuddyPluginBeta.ChatMessage> messages = getUnseenMessages();
/*      */       
/* 4880 */       for (BuddyPluginBeta.ChatMessage msg : messages)
/*      */       {
/* 4882 */         if (msg.getNickLocations().length > 0)
/*      */         {
/* 4884 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 4888 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public List<BuddyPluginBeta.ChatMessage> getUnseenMessages()
/*      */     {
/* 4894 */       synchronized (this.chat_lock)
/*      */       {
/* 4896 */         LinkedList<BuddyPluginBeta.ChatMessage> result = new LinkedList();
/*      */         
/* 4898 */         if (this.messages.size() > 0)
/*      */         {
/* 4900 */           for (int loop = 0; loop < 2; loop++)
/*      */           {
/* 4902 */             List<BuddyPluginBeta.ChatMessage> need_fixup = new ArrayList();
/*      */             
/* 4904 */             for (int i = this.messages.size() - 1; i >= 0; i--)
/*      */             {
/* 4906 */               BuddyPluginBeta.ChatMessage msg = (BuddyPluginBeta.ChatMessage)this.messages.get(i);
/*      */               
/* 4908 */               if ((!msg.isIgnored()) && (!msg.getParticipant().isMe()))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 4913 */                 int seen_state = msg.getSeenState();
/*      */                 
/* 4915 */                 if (seen_state == 1) {
/*      */                   break;
/*      */                 }
/*      */                 
/* 4919 */                 if (seen_state == 0)
/*      */                 {
/* 4921 */                   need_fixup.add(msg);
/*      */                 }
/*      */                 else
/*      */                 {
/* 4925 */                   result.addFirst(msg);
/*      */                 }
/*      */               }
/*      */             }
/* 4929 */             if ((loop == 0) && (need_fixup.size() > 0))
/*      */             {
/* 4931 */               fixupSeenState(need_fixup);
/*      */               
/* 4933 */               result.clear();
/*      */             }
/*      */             else
/*      */             {
/* 4937 */               if (need_fixup.size() <= 0)
/*      */                 break;
/* 4939 */               Debug.out("Hmm"); break;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 4947 */         return result;
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public BuddyPluginBeta.ChatParticipant[] getParticipants()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 1331	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:chat_lock	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: getfield 1346	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:participants	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */       //   11: invokevirtual 1552	org/gudy/azureus2/core3/util/ByteArrayHashMap:values	()Ljava/util/List;
/*      */       //   14: aload_0
/*      */       //   15: getfield 1346	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:participants	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */       //   18: invokevirtual 1546	org/gudy/azureus2/core3/util/ByteArrayHashMap:size	()I
/*      */       //   21: anewarray 813	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatParticipant
/*      */       //   24: invokeinterface 1584 2 0
/*      */       //   29: checkcast 793	[Lcom/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatParticipant;
/*      */       //   32: aload_1
/*      */       //   33: monitorexit
/*      */       //   34: areturn
/*      */       //   35: astore_2
/*      */       //   36: aload_1
/*      */       //   37: monitorexit
/*      */       //   38: aload_2
/*      */       //   39: athrow
/*      */       // Line number table:
/*      */       //   Java source line #4954	-> byte code offset #0
/*      */       //   Java source line #4956	-> byte code offset #7
/*      */       //   Java source line #4957	-> byte code offset #35
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	40	0	this	ChatInstance
/*      */       //   5	32	1	Ljava/lang/Object;	Object
/*      */       //   35	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	34	35	finally
/*      */       //   35	38	35	finally
/*      */     }
/*      */     
/*      */     public BuddyPluginBeta.ChatParticipant getParticipant(String nick)
/*      */     {
/* 4964 */       synchronized (this.chat_lock)
/*      */       {
/* 4966 */         for (BuddyPluginBeta.ChatParticipant cp : this.participants.values())
/*      */         {
/* 4968 */           if (cp.getName().equals(nick))
/*      */           {
/* 4970 */             return cp;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 4975 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void updated(BuddyPluginBeta.ChatParticipant p)
/*      */     {
/* 4982 */       for (BuddyPluginBeta.ChatListener l : this.listeners)
/*      */       {
/* 4984 */         l.participantChanged(p);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void registerNick(BuddyPluginBeta.ChatParticipant p, String old_nick, String new_nick)
/*      */     {
/* 4994 */       synchronized (this.chat_lock)
/*      */       {
/* 4996 */         if (old_nick != null)
/*      */         {
/* 4998 */           List<BuddyPluginBeta.ChatParticipant> list = (List)this.nick_clash_map.get(old_nick);
/*      */           
/* 5000 */           if ((list != null) && (list.remove(p)))
/*      */           {
/* 5002 */             if (list.size() == 0)
/*      */             {
/* 5004 */               this.nick_clash_map.remove(old_nick);
/*      */ 
/*      */ 
/*      */             }
/* 5008 */             else if (list.size() == 1)
/*      */             {
/* 5010 */               BuddyPluginBeta.ChatParticipant.access$4700((BuddyPluginBeta.ChatParticipant)list.get(0), false);
/*      */             }
/*      */             
/*      */           }
/*      */           else {
/* 5015 */             Debug.out("inconsistent");
/*      */           }
/*      */         }
/*      */         
/* 5019 */         List<BuddyPluginBeta.ChatParticipant> list = (List)this.nick_clash_map.get(new_nick);
/*      */         
/* 5021 */         if (list == null)
/*      */         {
/* 5023 */           list = new ArrayList();
/*      */           
/* 5025 */           this.nick_clash_map.put(new_nick, list);
/*      */         }
/*      */         
/* 5028 */         if (list.contains(p))
/*      */         {
/* 5030 */           Debug.out("inconsistent");
/*      */         }
/*      */         else
/*      */         {
/* 5034 */           list.add(p);
/*      */           
/* 5036 */           if (list.size() > 1)
/*      */           {
/* 5038 */             BuddyPluginBeta.ChatParticipant.access$4700(p, true);
/*      */             
/* 5040 */             if (list.size() == 2)
/*      */             {
/* 5042 */               BuddyPluginBeta.ChatParticipant.access$4700((BuddyPluginBeta.ChatParticipant)list.get(0), true);
/*      */             }
/*      */           }
/*      */           else {
/* 5046 */             BuddyPluginBeta.ChatParticipant.access$4700(p, false);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public BuddyPluginBeta.ChatMessage getLastMessageRequiringAttention()
/*      */     {
/* 5055 */       return this.last_message_requiring_attention;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setUserData(Object key, Object value)
/*      */     {
/* 5063 */       synchronized (this.user_data)
/*      */       {
/* 5065 */         this.user_data.put(key, value);
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public Object getUserData(Object key)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 1341	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:user_data	Ljava/util/Map;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: getfield 1341	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:user_data	Ljava/util/Map;
/*      */       //   11: aload_1
/*      */       //   12: invokeinterface 1587 2 0
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #5073	-> byte code offset #0
/*      */       //   Java source line #5075	-> byte code offset #7
/*      */       //   Java source line #5076	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	ChatInstance
/*      */       //   0	25	1	key	Object
/*      */       //   5	17	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public boolean getMessageOutstanding()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 1331	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:chat_lock	Ljava/lang/Object;
/*      */       //   4: dup
/*      */       //   5: astore_1
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: getfield 1320	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:message_outstanding	Z
/*      */       //   11: aload_1
/*      */       //   12: monitorexit
/*      */       //   13: ireturn
/*      */       //   14: astore_2
/*      */       //   15: aload_1
/*      */       //   16: monitorexit
/*      */       //   17: aload_2
/*      */       //   18: athrow
/*      */       // Line number table:
/*      */       //   Java source line #5082	-> byte code offset #0
/*      */       //   Java source line #5084	-> byte code offset #7
/*      */       //   Java source line #5085	-> byte code offset #14
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	19	0	this	ChatInstance
/*      */       //   5	11	1	Ljava/lang/Object;	Object
/*      */       //   14	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	13	14	finally
/*      */       //   14	17	14	finally
/*      */     }
/*      */     
/*      */     public void setMessageOutstanding(BuddyPluginBeta.ChatMessage message)
/*      */     {
/* 5092 */       boolean outstanding = message != null;
/*      */       
/* 5094 */       boolean changed = false;
/*      */       
/* 5096 */       BuddyPluginBeta.this.addChatActivity(this, message);
/*      */       
/* 5098 */       synchronized (this.chat_lock)
/*      */       {
/* 5100 */         if (this.message_outstanding == outstanding)
/*      */         {
/* 5102 */           return;
/*      */         }
/*      */         
/* 5105 */         this.message_outstanding = outstanding;
/*      */         
/* 5107 */         changed = true;
/*      */         
/* 5109 */         if (!outstanding)
/*      */         {
/* 5111 */           if (this.messages.size() > 0)
/*      */           {
/* 5113 */             BuddyPluginBeta.ChatMessage last_read_msg = (BuddyPluginBeta.ChatMessage)this.messages.get(this.messages.size() - 1);
/*      */             
/* 5115 */             long last_read_time = last_read_msg.getTimeStamp();
/*      */             
/* 5117 */             String last_info = SystemTime.getCurrentTime() / 1000L + "/" + last_read_time / 1000L + "/" + Base32.encode(last_read_msg.getID());
/*      */             
/* 5119 */             BuddyPluginBeta.this.setLastMessageInfo(this.network, this.key, last_info);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 5124 */       if (changed)
/*      */       {
/* 5126 */         updated();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean isOldOutstandingMessage(BuddyPluginBeta.ChatMessage msg)
/*      */     {
/* 5134 */       synchronized (this.chat_lock)
/*      */       {
/* 5136 */         String info = BuddyPluginBeta.this.getLastMessageInfo(this.network, this.key);
/*      */         
/* 5138 */         if (info != null)
/*      */         {
/* 5140 */           String[] bits = info.split("/");
/*      */           try
/*      */           {
/* 5143 */             long old_time_secs = Long.parseLong(bits[0]);
/* 5144 */             long old_msg_secs = Long.parseLong(bits[1]);
/* 5145 */             byte[] old_id = Base32.decode(bits[2]);
/*      */             
/* 5147 */             long msg_secs = msg.getTimeStamp() / 1000L;
/* 5148 */             byte[] id = msg.getID();
/*      */             
/* 5150 */             if (Arrays.equals(id, old_id))
/*      */             {
/* 5152 */               return true;
/*      */             }
/*      */             
/* 5155 */             long old_cuttoff = old_time_secs - 300L;
/*      */             
/* 5157 */             if (old_msg_secs > old_cuttoff)
/*      */             {
/* 5159 */               old_cuttoff = old_msg_secs;
/*      */             }
/*      */             
/* 5162 */             if (msg_secs <= old_cuttoff)
/*      */             {
/* 5164 */               return true;
/*      */             }
/*      */             
/* 5167 */             if ((this.message_ids.containsKey(old_id)) && (this.message_ids.containsKey(id)))
/*      */             {
/* 5169 */               int msg_index = -1;
/* 5170 */               int old_msg_index = -1;
/*      */               
/* 5172 */               for (int i = 0; i < this.messages.size(); i++)
/*      */               {
/* 5174 */                 BuddyPluginBeta.ChatMessage m = (BuddyPluginBeta.ChatMessage)this.messages.get(i);
/*      */                 
/* 5176 */                 if (m == msg)
/*      */                 {
/* 5178 */                   msg_index = i;
/*      */                 }
/* 5180 */                 else if (Arrays.equals(m.getID(), old_id))
/*      */                 {
/* 5182 */                   old_msg_index = i;
/*      */                 }
/*      */               }
/*      */               
/* 5186 */               if (msg_index <= old_msg_index)
/*      */               {
/* 5188 */                 return true;
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 5197 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void fixupSeenState(List<BuddyPluginBeta.ChatMessage> msgs)
/*      */     {
/* 5204 */       for (BuddyPluginBeta.ChatMessage msg : msgs)
/*      */       {
/* 5206 */         msg.setSeen(false);
/*      */       }
/*      */       
/* 5209 */       synchronized (this.chat_lock)
/*      */       {
/* 5211 */         String info = BuddyPluginBeta.this.getLastMessageInfo(this.network, this.key);
/*      */         
/* 5213 */         if (info != null)
/*      */         {
/* 5215 */           String[] bits = info.split("/");
/*      */           Map<BuddyPluginBeta.ChatMessage, Integer> msg_map;
/*      */           int old_msg_index;
/* 5218 */           try { long old_time_secs = Long.parseLong(bits[0]);
/* 5219 */             long old_msg_secs = Long.parseLong(bits[1]);
/* 5220 */             byte[] old_id = Base32.decode(bits[2]);
/*      */             
/* 5222 */             for (BuddyPluginBeta.ChatMessage msg : msgs)
/*      */             {
/* 5224 */               long msg_secs = msg.getTimeStamp() / 1000L;
/* 5225 */               byte[] id = msg.getID();
/*      */               
/* 5227 */               if (Arrays.equals(id, old_id))
/*      */               {
/* 5229 */                 msg.setSeen(true);
/*      */               }
/*      */               else
/*      */               {
/* 5233 */                 long old_cuttoff = old_time_secs - 300L;
/*      */                 
/* 5235 */                 if (old_msg_secs > old_cuttoff)
/*      */                 {
/* 5237 */                   old_cuttoff = old_msg_secs;
/*      */                 }
/*      */                 
/* 5240 */                 if (msg_secs <= old_cuttoff)
/*      */                 {
/* 5242 */                   msg.setSeen(true);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 5247 */             if (this.message_ids.containsKey(old_id))
/*      */             {
/* 5249 */               msg_map = new HashMap();
/*      */               
/* 5251 */               old_msg_index = -1;
/*      */               
/* 5253 */               for (int i = 0; i < this.messages.size(); i++)
/*      */               {
/* 5255 */                 BuddyPluginBeta.ChatMessage m = (BuddyPluginBeta.ChatMessage)this.messages.get(i);
/*      */                 
/* 5257 */                 msg_map.put(m, Integer.valueOf(i));
/*      */                 
/* 5259 */                 if (Arrays.equals(m.getID(), old_id))
/*      */                 {
/* 5261 */                   old_msg_index = i;
/*      */                 }
/*      */               }
/*      */               
/* 5265 */               for (BuddyPluginBeta.ChatMessage msg : msgs)
/*      */               {
/* 5267 */                 Integer msg_index = (Integer)msg_map.get(msg);
/*      */                 
/* 5269 */                 if ((msg_index != null) && (msg_index.intValue() <= old_msg_index))
/*      */                 {
/* 5271 */                   msg.setSeen(true);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public InetSocketAddress getMyAddress()
/*      */     {
/* 5284 */       return this.my_address;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void addListener(BuddyPluginBeta.ChatListener listener)
/*      */     {
/* 5291 */       this.listeners.add(listener);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeListener(BuddyPluginBeta.ChatListener listener)
/*      */     {
/* 5298 */       this.listeners.remove(listener);
/*      */     }
/*      */     
/*      */ 
/*      */     public void remove()
/*      */     {
/* 5304 */       destroy(true);
/*      */       
/* 5306 */       BuddyPluginBeta.this.removeAllOptions(this.network, this.key);
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isDestroyed()
/*      */     {
/* 5312 */       return this.destroyed;
/*      */     }
/*      */     
/*      */ 
/*      */     public void destroy()
/*      */     {
/* 5318 */       destroy(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void destroy(boolean force)
/*      */     {
/* 5325 */       synchronized (this.chat_lock)
/*      */       {
/* 5327 */         if (force)
/*      */         {
/* 5329 */           this.reference_count = 0;
/* 5330 */           this.keep_alive = false;
/* 5331 */           this.have_interest = false;
/*      */         }
/*      */         else
/*      */         {
/* 5335 */           this.reference_count -= 1;
/*      */           
/*      */ 
/*      */ 
/* 5339 */           if (this.reference_count > 0)
/*      */           {
/* 5341 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 5346 */       if ((!this.keep_alive) && ((!this.have_interest) || (this.is_private_chat)))
/*      */       {
/* 5348 */         this.destroyed = true;
/*      */         
/* 5350 */         if (this.handler != null)
/*      */         {
/* 5352 */           if (this.is_private_chat)
/*      */           {
/* 5354 */             Map<String, Object> flags = new HashMap();
/*      */             
/* 5356 */             flags.put("s", Integer.valueOf(1));
/*      */             
/* 5358 */             sendMessageSupport("", flags, new HashMap());
/*      */           }
/*      */           ChatInstance removed;
/*      */           try {
/* 5362 */             Map<String, Object> options = new HashMap();
/*      */             
/* 5364 */             options.put("handler", this.handler);
/*      */             
/* 5366 */             reply = (Map)this.msgsync_pi.getIPC().invoke("removeMessageHandler", new Object[] { options }); } catch (Throwable e) { Object reply;
/*      */             String meta_key;
/*      */             ChatInstance removed;
/*      */             ChatInstance inst;
/* 5370 */             Iterator i$; BuddyPluginBeta.ChatManagerListener l; Debug.out(e); } finally { String meta_key;
/*      */             ChatInstance removed;
/*      */             ChatInstance inst;
/*      */             Iterator i$;
/* 5374 */             BuddyPluginBeta.ChatManagerListener l; String meta_key = this.network + ":" + this.key;
/*      */             
/* 5376 */             removed = null;
/*      */             
/* 5378 */             synchronized (BuddyPluginBeta.this.chat_instances_map)
/*      */             {
/* 5380 */               ChatInstance inst = (ChatInstance)BuddyPluginBeta.this.chat_instances_map.remove(meta_key);
/*      */               
/* 5382 */               if (inst != null)
/*      */               {
/* 5384 */                 removed = inst;
/*      */                 
/* 5386 */                 BuddyPluginBeta.this.chat_instances_list.remove(inst);
/*      */               }
/*      */               
/* 5389 */               if (BuddyPluginBeta.this.chat_instances_map.size() == 0)
/*      */               {
/* 5391 */                 if (BuddyPluginBeta.this.timer != null)
/*      */                 {
/* 5393 */                   BuddyPluginBeta.this.timer.cancel();
/*      */                   
/* 5395 */                   BuddyPluginBeta.this.timer = null;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 5400 */             if (removed != null)
/*      */             {
/* 5402 */               for (BuddyPluginBeta.ChatManagerListener l : BuddyPluginBeta.this.listeners) {
/*      */                 try
/*      */                 {
/* 5405 */                   l.chatRemoved(removed);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 5409 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public class ChatParticipant
/*      */   {
/*      */     private final BuddyPluginBeta.ChatInstance chat;
/*      */     
/*      */     private final byte[] pk;
/*      */     
/*      */     private String nickname;
/*      */     private boolean is_ignored;
/*      */     private boolean is_spammer;
/*      */     private boolean is_pinned;
/*      */     private boolean nick_clash;
/* 5431 */     private List<BuddyPluginBeta.ChatMessage> participant_messages = new ArrayList();
/*      */     
/*      */ 
/*      */     private Boolean is_me;
/*      */     
/*      */ 
/*      */ 
/*      */     private ChatParticipant(BuddyPluginBeta.ChatInstance _chat, byte[] _pk)
/*      */     {
/* 5440 */       this.chat = _chat;
/* 5441 */       this.pk = _pk;
/*      */       
/* 5443 */       this.nickname = BuddyPluginBeta.this.pkToString(this.pk);
/*      */       
/* 5445 */       this.is_pinned = COConfigurationManager.getBooleanParameter(getPinKey(), false);
/*      */       
/* 5447 */       this.chat.registerNick(this, null, this.nickname);
/*      */     }
/*      */     
/*      */ 
/*      */     public BuddyPluginBeta.ChatInstance getChat()
/*      */     {
/* 5453 */       return this.chat;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getPublicKey()
/*      */     {
/* 5459 */       return this.pk;
/*      */     }
/*      */     
/*      */ 
/*      */     public Map<String, Object> getContact()
/*      */     {
/* 5465 */       synchronized (this.chat.chat_lock)
/*      */       {
/* 5467 */         if (this.participant_messages.isEmpty())
/*      */         {
/* 5469 */           return null;
/*      */         }
/*      */         
/* 5472 */         return ((BuddyPluginBeta.ChatMessage)this.participant_messages.get(this.participant_messages.size() - 1)).getContact();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public InetSocketAddress getAddress()
/*      */     {
/* 5479 */       synchronized (this.chat.chat_lock)
/*      */       {
/* 5481 */         if (this.participant_messages.isEmpty())
/*      */         {
/* 5483 */           return null;
/*      */         }
/*      */         
/* 5486 */         return ((BuddyPluginBeta.ChatMessage)this.participant_messages.get(this.participant_messages.size() - 1)).getAddress();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isMe()
/*      */     {
/* 5493 */       if (this.is_me != null)
/*      */       {
/* 5495 */         return this.is_me.booleanValue();
/*      */       }
/*      */       
/* 5498 */       byte[] chat_key = this.chat.getPublicKey();
/*      */       
/* 5500 */       if (chat_key != null)
/*      */       {
/* 5502 */         this.is_me = Boolean.valueOf(Arrays.equals(this.pk, chat_key));
/*      */       }
/*      */       
/* 5505 */       return this.is_me == null ? false : this.is_me.booleanValue();
/*      */     }
/*      */     
/*      */ 
/*      */     public String getName()
/*      */     {
/* 5511 */       return getName(true);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getName(boolean use_nick)
/*      */     {
/* 5518 */       if (use_nick)
/*      */       {
/* 5520 */         return this.nickname;
/*      */       }
/*      */       
/*      */ 
/* 5524 */       return BuddyPluginBeta.this.pkToString(this.pk);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean hasNickname()
/*      */     {
/* 5531 */       return !this.nickname.equals(BuddyPluginBeta.this.pkToString(this.pk));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void addMessage(BuddyPluginBeta.ChatMessage message)
/*      */     {
/* 5538 */       this.participant_messages.add(message);
/*      */       
/* 5540 */       message.setParticipant(this);
/*      */       
/* 5542 */       message.setIgnored((this.is_ignored) || (this.is_spammer));
/*      */       
/* 5544 */       String new_nickname = message.getNickName();
/*      */       
/* 5546 */       if (!this.nickname.equals(new_nickname))
/*      */       {
/* 5548 */         this.chat.registerNick(this, this.nickname, new_nickname);
/*      */         
/* 5550 */         message.setNickClash(isNickClash());
/*      */         
/* 5552 */         this.nickname = new_nickname;
/*      */         
/* 5554 */         this.chat.updated(this);
/*      */       }
/*      */       else
/*      */       {
/* 5558 */         message.setNickClash(isNickClash());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean replayMessage(BuddyPluginBeta.ChatMessage message)
/*      */     {
/* 5566 */       this.participant_messages.add(message);
/*      */       
/* 5568 */       message.setIgnored((this.is_ignored) || (this.is_spammer));
/*      */       
/* 5570 */       String new_nickname = message.getNickName();
/*      */       
/* 5572 */       if (!this.nickname.equals(new_nickname))
/*      */       {
/* 5574 */         this.chat.registerNick(this, this.nickname, new_nickname);
/*      */         
/* 5576 */         message.setNickClash(isNickClash());
/*      */         
/* 5578 */         this.nickname = new_nickname;
/*      */         
/* 5580 */         return true;
/*      */       }
/*      */       
/*      */ 
/* 5584 */       message.setNickClash(isNickClash());
/*      */       
/* 5586 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void removeMessage(BuddyPluginBeta.ChatMessage message)
/*      */     {
/* 5594 */       this.participant_messages.remove(message);
/*      */     }
/*      */     
/*      */ 
/*      */     private void resetMessages()
/*      */     {
/* 5600 */       String new_nickname = BuddyPluginBeta.this.pkToString(this.pk);
/*      */       
/* 5602 */       if (!this.nickname.equals(new_nickname))
/*      */       {
/* 5604 */         this.chat.registerNick(this, this.nickname, new_nickname);
/*      */         
/* 5606 */         this.nickname = new_nickname;
/*      */       }
/*      */       
/* 5609 */       this.participant_messages.clear();
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public List<BuddyPluginBeta.ChatMessage> getMessages()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 241	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatParticipant:chat	Lcom/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance;
/*      */       //   4: invokestatic 254	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance:access$2900	(Lcom/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatInstance;)Ljava/lang/Object;
/*      */       //   7: dup
/*      */       //   8: astore_1
/*      */       //   9: monitorenter
/*      */       //   10: new 126	java/util/ArrayList
/*      */       //   13: dup
/*      */       //   14: aload_0
/*      */       //   15: getfield 244	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta$ChatParticipant:participant_messages	Ljava/util/List;
/*      */       //   18: invokespecial 280	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
/*      */       //   21: aload_1
/*      */       //   22: monitorexit
/*      */       //   23: areturn
/*      */       //   24: astore_2
/*      */       //   25: aload_1
/*      */       //   26: monitorexit
/*      */       //   27: aload_2
/*      */       //   28: athrow
/*      */       // Line number table:
/*      */       //   Java source line #5615	-> byte code offset #0
/*      */       //   Java source line #5617	-> byte code offset #10
/*      */       //   Java source line #5618	-> byte code offset #24
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	29	0	this	ChatParticipant
/*      */       //   8	18	1	Ljava/lang/Object;	Object
/*      */       //   24	4	2	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   10	23	24	finally
/*      */       //   24	27	24	finally
/*      */     }
/*      */     
/*      */     public boolean isIgnored()
/*      */     {
/* 5624 */       return this.is_ignored;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setIgnored(boolean b)
/*      */     {
/* 5631 */       if (b != this.is_ignored)
/*      */       {
/* 5633 */         this.is_ignored = b;
/*      */         
/* 5635 */         synchronized (this.chat.chat_lock)
/*      */         {
/* 5637 */           for (BuddyPluginBeta.ChatMessage message : this.participant_messages)
/*      */           {
/* 5639 */             message.setIgnored((b) || (this.is_spammer));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isSpammer()
/*      */     {
/* 5648 */       return this.is_spammer;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean canSpammer()
/*      */     {
/* 5654 */       return (this.participant_messages.size() >= 5) && (!this.is_spammer);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setSpammer(boolean b)
/*      */     {
/* 5661 */       if (b != this.is_spammer)
/*      */       {
/* 5663 */         this.is_spammer = b;
/*      */         
/* 5665 */         this.chat.setSpammer(this, b);
/*      */         
/* 5667 */         synchronized (this.chat.chat_lock)
/*      */         {
/* 5669 */           for (BuddyPluginBeta.ChatMessage message : this.participant_messages)
/*      */           {
/* 5671 */             message.setIgnored((b) || (this.is_ignored));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isPinned()
/*      */     {
/* 5680 */       return this.is_pinned;
/*      */     }
/*      */     
/*      */ 
/*      */     private String getPinKey()
/*      */     {
/* 5686 */       return "azbuddy.chat.pinned." + ByteFormatter.encodeString(this.pk, 0, 16);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setPinned(boolean b)
/*      */     {
/* 5693 */       if (b != this.is_pinned)
/*      */       {
/* 5695 */         this.is_pinned = b;
/*      */         
/* 5697 */         String key = getPinKey();
/*      */         
/* 5699 */         if (this.is_pinned)
/*      */         {
/* 5701 */           COConfigurationManager.setParameter(key, true);
/*      */         }
/*      */         else
/*      */         {
/* 5705 */           COConfigurationManager.removeParameter(key);
/*      */         }
/*      */         
/* 5708 */         COConfigurationManager.setDirty();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isNickClash()
/*      */     {
/* 5715 */       return this.nick_clash;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setNickClash(boolean b)
/*      */     {
/* 5722 */       this.nick_clash = b;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public BuddyPluginBeta.ChatInstance createPrivateChat()
/*      */       throws Exception
/*      */     {
/* 5730 */       BuddyPluginBeta.ChatInstance inst = BuddyPluginBeta.this.getChat(this);
/*      */       
/* 5732 */       BuddyPluginBeta.ChatInstance parent = getChat();
/*      */       
/* 5734 */       if (!parent.isSharedNickname())
/*      */       {
/* 5736 */         inst.setSharedNickname(false);
/*      */         
/* 5738 */         inst.setInstanceNickname(parent.getInstanceNickname());
/*      */       }
/*      */       
/* 5741 */       return inst;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public class ChatMessage
/*      */   {
/*      */     public static final int MT_NORMAL = 1;
/*      */     
/*      */     public static final int MT_INFO = 2;
/*      */     
/*      */     public static final int MT_ERROR = 3;
/*      */     
/*      */     protected static final int SEEN_UNKNOWN = 0;
/*      */     
/*      */     protected static final int SEEN_YES = 1;
/*      */     
/*      */     protected static final int SEEN_NO = 2;
/*      */     
/*      */     private final int uid;
/*      */     
/*      */     private final Map<String, Object> map;
/*      */     
/*      */     private WeakReference<Map<String, Object>> payload_ref;
/*      */     private final byte[] message_id;
/*      */     private final long timestamp;
/*      */     private BuddyPluginBeta.ChatParticipant participant;
/*      */     private byte[] previous_id;
/*      */     private long sequence;
/*      */     private boolean is_ignored;
/*      */     private boolean is_duplicate;
/*      */     private boolean is_nick_clash;
/* 5773 */     private int seen_state = 0;
/*      */     
/*      */ 
/*      */     private int[] nick_locations;
/*      */     
/*      */ 
/*      */     private ChatMessage(Map<String, Object> _uid)
/*      */     {
/* 5781 */       this.uid = _uid;
/* 5782 */       this.map = _map;
/*      */       
/* 5784 */       this.message_id = ((byte[])this.map.get("id"));
/*      */       
/* 5786 */       this.timestamp = (SystemTime.getCurrentTime() - getAgeWhenReceived() * 1000L);
/*      */       
/* 5788 */       Map<String, Object> payload = getPayload();
/*      */       
/* 5790 */       this.previous_id = ((byte[])payload.get("pre"));
/*      */       
/* 5792 */       Number l_seq = (Number)payload.get("seq");
/*      */       
/* 5794 */       this.sequence = (l_seq == null ? 0L : l_seq.longValue());
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getUID()
/*      */     {
/* 5800 */       return this.uid;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setParticipant(BuddyPluginBeta.ChatParticipant p)
/*      */     {
/* 5807 */       this.participant = p;
/*      */     }
/*      */     
/*      */ 
/*      */     public BuddyPluginBeta.ChatParticipant getParticipant()
/*      */     {
/* 5813 */       return this.participant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setNickClash(boolean clash)
/*      */     {
/* 5820 */       this.is_nick_clash = clash;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isNickClash()
/*      */     {
/* 5826 */       return this.is_nick_clash;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setSeen(boolean is_seen)
/*      */     {
/* 5833 */       this.seen_state = (is_seen ? 1 : 2);
/*      */     }
/*      */     
/*      */ 
/*      */     public int getSeenState()
/*      */     {
/* 5839 */       return this.seen_state;
/*      */     }
/*      */     
/*      */ 
/*      */     public int[] getNickLocations()
/*      */     {
/* 5845 */       synchronized (this)
/*      */       {
/* 5847 */         if (this.nick_locations == null)
/*      */         {
/* 5849 */           if (this.participant == null)
/*      */           {
/* 5851 */             return new int[0];
/*      */           }
/*      */           
/* 5854 */           String my_nick = this.participant.getChat().getNickname(true);
/*      */           
/* 5856 */           int nick_len = my_nick.length();
/*      */           
/* 5858 */           List<Integer> hits = new ArrayList();
/*      */           
/* 5860 */           if (my_nick.length() > 0)
/*      */           {
/* 5862 */             String text = getMessage();
/*      */             
/* 5864 */             int text_len = text.length();
/*      */             
/* 5866 */             int pos = 0;
/*      */             
/* 5868 */             while (pos < text_len)
/*      */             {
/* 5870 */               pos = text.indexOf(my_nick, pos);
/*      */               
/* 5872 */               if (pos < 0)
/*      */                 break;
/* 5874 */               boolean match = true;
/*      */               
/* 5876 */               if (pos > 0)
/*      */               {
/* 5878 */                 if (Character.isLetterOrDigit(text.charAt(pos - 1)))
/*      */                 {
/* 5880 */                   match = false;
/*      */                 }
/*      */               }
/*      */               
/* 5884 */               int nick_end = pos + nick_len;
/*      */               
/* 5886 */               if (nick_end < text_len)
/*      */               {
/* 5888 */                 if (Character.isLetterOrDigit(text.charAt(nick_end)))
/*      */                 {
/* 5890 */                   match = false;
/*      */                 }
/*      */               }
/*      */               
/* 5894 */               if (match)
/*      */               {
/* 5896 */                 hits.add(Integer.valueOf(pos));
/*      */               }
/*      */               
/* 5899 */               pos += nick_len;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5908 */           if (hits.size() == 0)
/*      */           {
/* 5910 */             this.nick_locations = new int[0];
/*      */           }
/*      */           else
/*      */           {
/* 5914 */             this.nick_locations = new int[hits.size() + 1];
/*      */             
/* 5916 */             this.nick_locations[0] = nick_len;
/*      */             
/* 5918 */             for (int i = 0; i < hits.size(); i++)
/*      */             {
/* 5920 */               this.nick_locations[(i + 1)] = ((Integer)hits.get(i)).intValue();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 5925 */         return this.nick_locations;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private Map<String, Object> getPayload()
/*      */     {
/* 5932 */       synchronized (this)
/*      */       {
/* 5934 */         Map<String, Object> payload = null;
/*      */         
/* 5936 */         if (this.payload_ref != null)
/*      */         {
/* 5938 */           payload = (Map)this.payload_ref.get();
/*      */           
/* 5940 */           if (payload != null)
/*      */           {
/* 5942 */             return payload;
/*      */           }
/*      */         }
/*      */         try
/*      */         {
/* 5947 */           byte[] content_bytes = (byte[])this.map.get("content");
/*      */           
/* 5949 */           if ((content_bytes != null) && (content_bytes.length > 0))
/*      */           {
/* 5951 */             payload = BDecoder.decode(content_bytes);
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/* 5956 */         if (payload == null)
/*      */         {
/* 5958 */           payload = new HashMap();
/*      */         }
/*      */         
/* 5961 */         this.payload_ref = new WeakReference(payload);
/*      */         
/* 5963 */         return payload;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private int getMessageStatus()
/*      */     {
/* 5970 */       Map<String, Object> payload = getPayload();
/*      */       
/* 5972 */       if (payload != null)
/*      */       {
/* 5974 */         Map<String, Object> flags = (Map)payload.get("f");
/*      */         
/* 5976 */         if (flags != null)
/*      */         {
/* 5978 */           Number status = (Number)flags.get("s");
/*      */           
/* 5980 */           if (status != null)
/*      */           {
/* 5982 */             return status.intValue();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 5987 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean getFlagFlashOverride()
/*      */     {
/* 5993 */       Map<String, Object> payload = getPayload();
/*      */       
/* 5995 */       if (payload != null)
/*      */       {
/* 5997 */         Map<String, Object> flags = (Map)payload.get("f");
/*      */         
/* 5999 */         if (flags != null)
/*      */         {
/* 6001 */           Number override = (Number)flags.get("f");
/*      */           
/* 6003 */           if (override != null)
/*      */           {
/* 6005 */             return override.intValue() != 0;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 6010 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getFlagOrigin()
/*      */     {
/* 6016 */       Map<String, Object> payload = getPayload();
/*      */       
/* 6018 */       if (payload != null)
/*      */       {
/* 6020 */         Map<String, Object> flags = (Map)payload.get("f");
/*      */         
/* 6022 */         if (flags != null)
/*      */         {
/* 6024 */           Number origin = (Number)flags.get("o");
/*      */           
/* 6026 */           if (origin != null)
/*      */           {
/* 6028 */             return origin.intValue();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 6035 */       String msg_text = getMessage();
/*      */       
/* 6037 */       if (msg_text.startsWith("See http://wiki.vuze.com/w/Swarm_Merging"))
/*      */       {
/* 6039 */         return 1;
/*      */       }
/*      */       
/* 6042 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getFlagType()
/*      */     {
/* 6048 */       Map<String, Object> payload = getPayload();
/*      */       
/* 6050 */       if (payload != null)
/*      */       {
/* 6052 */         Map<String, Object> flags = (Map)payload.get("f");
/*      */         
/* 6054 */         if (flags != null)
/*      */         {
/* 6056 */           Number type = (Number)flags.get("t");
/*      */           
/* 6058 */           if (type != null)
/*      */           {
/* 6060 */             return type.intValue();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 6065 */       return 0;
/*      */     }
/*      */     
/*      */     public String getMessage()
/*      */     {
/*      */       try
/*      */       {
/* 6072 */         String report = (String)this.map.get("error");
/*      */         
/* 6074 */         if (report != null)
/*      */         {
/* 6076 */           if ((report.length() > 2) && (report.charAt(1) == ':'))
/*      */           {
/* 6078 */             return report.substring(2);
/*      */           }
/*      */           
/* 6081 */           return report;
/*      */         }
/*      */         
/* 6084 */         if (getMessageStatus() == 1)
/*      */         {
/* 6086 */           return MessageText.getString("azbuddy.dchat.hasquit", new String[] { this.participant == null ? "<unknown>" : this.participant.getName() });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6095 */         Map<String, Object> payload = getPayload();
/*      */         
/* 6097 */         if (payload != null)
/*      */         {
/* 6099 */           byte[] msg_bytes = (byte[])payload.get("msg");
/*      */           
/* 6101 */           if (msg_bytes != null)
/*      */           {
/* 6103 */             return new String(msg_bytes, "UTF-8");
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 6109 */         return new String((byte[])this.map.get("content"), "UTF-8");
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 6113 */         Debug.out(e);
/*      */       }
/* 6115 */       return "";
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getRawMessage()
/*      */     {
/*      */       try
/*      */       {
/* 6123 */         String report = (String)this.map.get("error");
/*      */         
/* 6125 */         if (report != null)
/*      */         {
/* 6127 */           return null;
/*      */         }
/*      */         
/* 6130 */         if (getMessageStatus() == 1)
/*      */         {
/* 6132 */           return null;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 6137 */         Map<String, Object> payload = getPayload();
/*      */         
/* 6139 */         if (payload != null)
/*      */         {
/* 6141 */           byte[] msg_bytes = (byte[])payload.get("msg");
/*      */           
/* 6143 */           if (msg_bytes != null)
/*      */           {
/* 6145 */             return msg_bytes;
/*      */           }
/*      */         }
/*      */         
/* 6149 */         return (byte[])this.map.get("content");
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 6153 */         Debug.out(e);
/*      */       }
/* 6155 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public int getMessageType()
/*      */     {
/* 6162 */       return getMessageType(true);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private int getMessageType(boolean treat_quit_as_info)
/*      */     {
/* 6169 */       String report = (String)this.map.get("error");
/*      */       
/* 6171 */       if (report == null)
/*      */       {
/* 6173 */         if ((treat_quit_as_info) && (getMessageStatus() == 1))
/*      */         {
/* 6175 */           return 2;
/*      */         }
/*      */         
/* 6178 */         return 1;
/*      */       }
/*      */       
/*      */ 
/* 6182 */       if ((report.length() < 2) || (report.charAt(1) != ':'))
/*      */       {
/* 6184 */         return 3;
/*      */       }
/*      */       
/* 6187 */       char type = report.charAt(0);
/*      */       
/* 6189 */       if (type == 'i')
/*      */       {
/* 6191 */         return 2;
/*      */       }
/*      */       
/* 6194 */       return 3;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setDuplicate()
/*      */     {
/* 6202 */       this.is_duplicate = true;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isIgnored()
/*      */     {
/* 6208 */       return (this.is_duplicate) || (this.is_ignored);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setIgnored(boolean b)
/*      */     {
/* 6215 */       this.is_ignored = b;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getID()
/*      */     {
/* 6221 */       return this.message_id;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getPreviousID()
/*      */     {
/* 6227 */       return this.previous_id;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setPreviousID(byte[] pid)
/*      */     {
/* 6234 */       this.previous_id = pid;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getSequence()
/*      */     {
/* 6240 */       return this.sequence;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getPublicKey()
/*      */     {
/* 6246 */       return (byte[])this.map.get("pk");
/*      */     }
/*      */     
/*      */ 
/*      */     public Map<String, Object> getContact()
/*      */     {
/* 6252 */       return (Map)this.map.get("contact");
/*      */     }
/*      */     
/*      */ 
/*      */     public InetSocketAddress getAddress()
/*      */     {
/* 6258 */       return (InetSocketAddress)this.map.get("address");
/*      */     }
/*      */     
/*      */ 
/*      */     private int getAgeWhenReceived()
/*      */     {
/* 6264 */       return ((Number)this.map.get("age")).intValue();
/*      */     }
/*      */     
/*      */ 
/*      */     public long getTimeStamp()
/*      */     {
/* 6270 */       return this.timestamp;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getNickName()
/*      */     {
/* 6278 */       Map<String, Object> payload = getPayload();
/*      */       
/* 6280 */       if (payload != null)
/*      */       {
/* 6282 */         byte[] nick = (byte[])payload.get("nick");
/*      */         
/* 6284 */         if (nick != null) {
/*      */           try
/*      */           {
/* 6287 */             String str = new String(nick, "UTF-8");
/*      */             
/* 6289 */             if (str.length() > 0)
/*      */             {
/* 6291 */               return str;
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 6300 */       if (getMessageType(false) != 1)
/*      */       {
/* 6302 */         String nick = this.participant.getChat().getNickname(false);
/*      */         
/* 6304 */         if (nick.length() > 0)
/*      */         {
/* 6306 */           return nick;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 6312 */       return BuddyPluginBeta.this.pkToString(getPublicKey());
/*      */     }
/*      */     
/*      */ 
/*      */     public String getString()
/*      */     {
/* 6318 */       return "a=" + new SimpleDateFormat("D HH:mm:ss").format(Long.valueOf(getTimeStamp())) + ", i=" + BuddyPluginBeta.this.pkToString(this.message_id) + ", p=" + BuddyPluginBeta.this.pkToString(this.previous_id) + ": " + getMessage();
/*      */     }
/*      */   }
/*      */   
/*      */   public static class ChatAdapter
/*      */     implements BuddyPluginBeta.ChatListener
/*      */   {
/*      */     public void updated() {}
/*      */     
/*      */     public void stateChanged(boolean avail) {}
/*      */     
/*      */     public void participantRemoved(BuddyPluginBeta.ChatParticipant participant) {}
/*      */     
/*      */     public void participantChanged(BuddyPluginBeta.ChatParticipant participant) {}
/*      */     
/*      */     public void participantAdded(BuddyPluginBeta.ChatParticipant participant) {}
/*      */     
/*      */     public void messagesChanged() {}
/*      */     
/*      */     public void messageReceived(BuddyPluginBeta.ChatMessage message, boolean sort_outstanding) {}
/*      */   }
/*      */   
/*      */   public static abstract interface ChatListener
/*      */   {
/*      */     public abstract void messageReceived(BuddyPluginBeta.ChatMessage paramChatMessage, boolean paramBoolean);
/*      */     
/*      */     public abstract void messagesChanged();
/*      */     
/*      */     public abstract void participantAdded(BuddyPluginBeta.ChatParticipant paramChatParticipant);
/*      */     
/*      */     public abstract void participantChanged(BuddyPluginBeta.ChatParticipant paramChatParticipant);
/*      */     
/*      */     public abstract void participantRemoved(BuddyPluginBeta.ChatParticipant paramChatParticipant);
/*      */     
/*      */     public abstract void stateChanged(boolean paramBoolean);
/*      */     
/*      */     public abstract void updated();
/*      */   }
/*      */   
/*      */   public static abstract interface ChatManagerListener
/*      */   {
/*      */     public abstract void chatAdded(BuddyPluginBeta.ChatInstance paramChatInstance);
/*      */     
/*      */     public abstract void chatRemoved(BuddyPluginBeta.ChatInstance paramChatInstance);
/*      */   }
/*      */   
/*      */   public static abstract interface FTUXStateChangeListener
/*      */   {
/*      */     public abstract void stateChanged(boolean paramBoolean);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginBeta.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */