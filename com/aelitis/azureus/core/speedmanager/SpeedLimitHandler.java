/*      */ package com.aelitis.azureus.core.speedmanager;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagDownload;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureExecOnAssign;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureRunState;
/*      */ import com.aelitis.azureus.core.tag.TagListener;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagPeer;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import com.aelitis.azureus.core.tag.impl.TagBase;
/*      */ import com.aelitis.azureus.core.tag.impl.TagTypeWithState;
/*      */ import com.aelitis.azureus.core.util.average.Average;
/*      */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*      */ import com.aelitis.azureus.core.util.average.MovingImmediateAverage;
/*      */ import java.net.InetAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import org.gudy.azureus2.core3.category.Category;
/*      */ import org.gudy.azureus2.core3.category.CategoryManager;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*      */ import org.gudy.azureus2.core3.stats.transfer.LongTermStats;
/*      */ import org.gudy.azureus2.core3.stats.transfer.LongTermStats.GenericStatsSource;
/*      */ import org.gudy.azureus2.core3.stats.transfer.LongTermStats.RecordAccepter;
/*      */ import org.gudy.azureus2.core3.stats.transfer.LongTermStatsListener;
/*      */ import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadPeerListener;
/*      */ import org.gudy.azureus2.plugins.logging.Logger;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
/*      */ import org.gudy.azureus2.plugins.network.ConnectionManager;
/*      */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*      */ import org.gudy.azureus2.plugins.peers.Peer;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManagerEvent;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManagerListener2;
/*      */ import org.gudy.azureus2.plugins.peers.PeerStats;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextField;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.PluginLimitedRateGroup;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SpeedLimitHandler
/*      */   implements LongTermStatsListener
/*      */ {
/*      */   private static SpeedLimitHandler singleton;
/*  115 */   private static final Object RL_TO_BE_REMOVED_LOCK = new Object();
/*  116 */   private static final Object RLD_TO_BE_REMOVED_KEY = new Object();
/*  117 */   private static final Object RLU_TO_BE_REMOVED_KEY = new Object();
/*      */   private static final int SCHEDULER_PERIOD = 30000;
/*      */   private static final int NETLIMIT_TAG_LOG_PERIOD = 60000;
/*      */   private static final int NETLIMIT_TAG_LOG_TICKS = 2;
/*      */   
/*      */   public static SpeedLimitHandler getSingleton(AzureusCore core) {
/*  123 */     synchronized (SpeedLimitHandler.class)
/*      */     {
/*  125 */       if (singleton == null) {
/*      */         try
/*      */         {
/*  128 */           singleton = new SpeedLimitHandler(core);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  132 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/*  136 */       return singleton;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static final int PRIORITISER_CHECK_PERIOD_BASE = 5000;
/*      */   
/*      */   private static final String NET_IPV4 = "IPv4";
/*      */   
/*      */   private static final String NET_IPV6 = "IPv6";
/*      */   
/*      */   private static final String NET_LAN = "LAN";
/*      */   
/*      */   private static final String NET_WAN = "WAN";
/*      */   
/*      */   final AzureusCore core;
/*      */   
/*      */   final PluginInterface plugin_interface;
/*      */   
/*      */   final TorrentAttribute category_attribute;
/*      */   private final LoggerChannel logger;
/*      */   private TimerEventPeriodic schedule_event;
/*  158 */   private List<ScheduleRule> current_rules = new ArrayList();
/*      */   
/*      */   private ScheduleRule active_rule;
/*  161 */   private boolean prioritiser_enabled = true;
/*      */   private TimerEventPeriodic prioritiser_event;
/*  163 */   private List<Prioritiser> current_prioritisers = new ArrayList();
/*      */   
/*  165 */   private Map<String, IPSet> current_ip_sets = new HashMap();
/*  166 */   private final Map<String, RateLimiter> ip_set_rate_limiters_up = new HashMap();
/*  167 */   private final Map<String, RateLimiter> ip_set_rate_limiters_down = new HashMap();
/*      */   
/*      */   private TimerEventPeriodic ip_set_event;
/*      */   
/*      */   private boolean net_limit_listener_added;
/*  172 */   private Map<Integer, List<NetLimit>> net_limits = new HashMap();
/*      */   
/*  174 */   private final List<String> predefined_profile_names = new ArrayList();
/*      */   private boolean rule_pause_all_active;
/*      */   
/*  177 */   private SpeedLimitHandler(AzureusCore _core) { this.predefined_profile_names.add("null");
/*  178 */     this.predefined_profile_names.add("pause_all");
/*  179 */     this.predefined_profile_names.add("resume_all");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  185 */     this.ip_set_tag_type = (TagManagerFactory.getTagManager().isEnabled() ? new IPSetTagType(null) : null);
/*      */     
/*  187 */     this.extensions_lock = new Object();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2075 */     this.check_ip_sets_limiter = new FrequencyLimitedDispatcher(new AERunnable()
/*      */     {
/*      */ 
/* 2078 */       public void runSupport() { SpeedLimitHandler.this.checkIPSetsSupport(); } }, 500);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2083 */     this.check_ip_sets_limiter.setSingleThreaded();this.core = _core;this.plugin_interface = this.core.getPluginManager().getDefaultPluginInterface();this.category_attribute = this.plugin_interface.getTorrentManager().getAttribute("Category");this.logger = this.plugin_interface.getLogger().getTimeStampedChannel("Speed Limit Handler");
/*  202 */     if (Constants.isCVSVersion())
/*      */     {
/*  204 */       this.logger.setDiagnostic(1048576L, true);
/*      */     }
/*      */     
/*  207 */     UIManager ui_manager = this.plugin_interface.getUIManager();
/*      */     
/*  209 */     final BasicPluginViewModel model = ui_manager.createBasicPluginViewModel("Speed Limit Handler");
/*      */     
/*      */ 
/*  212 */     model.getActivity().setVisible(false);
/*  213 */     model.getProgress().setVisible(false);
/*      */     
/*  215 */     this.logger.addListener(new LoggerChannelListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void messageLogged(int type, String message)
/*      */       {
/*      */ 
/*      */ 
/*  223 */         model.getLogArea().appendText(message + "\n");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageLogged(String str, Throwable error)
/*      */       {
/*  231 */         model.getLogArea().appendText(error.toString() + "\n");
/*      */       }
/*      */       
/*  234 */     });
/*  235 */     loadPauseAllActive();
/*      */     
/*  237 */     loadSchedule();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasAnyProfiles()
/*      */   {
/*  243 */     if (!COConfigurationManager.hasParameter("speed.limit.handler.state", true))
/*      */     {
/*  245 */       return false;
/*      */     }
/*      */     
/*  248 */     Map map = loadConfig();
/*  249 */     if (map.size() == 0) {
/*  250 */       return false;
/*      */     }
/*      */     
/*  253 */     List<Map> list = (List)map.get("profiles");
/*  254 */     if ((list == null) || (list.size() == 0)) {
/*  255 */       return false;
/*      */     }
/*  257 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   private synchronized Map loadConfig()
/*      */   {
/*  263 */     return BEncoder.cloneMap(COConfigurationManager.getMapParameter("speed.limit.handler.state", new HashMap()));
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean net_limit_pause_all_active;
/*      */   private synchronized void saveConfig(Map map)
/*      */   {
/*  270 */     if (map.isEmpty()) {
/*  271 */       COConfigurationManager.removeParameter("speed.limit.handler.state");
/*      */     } else {
/*  273 */       COConfigurationManager.setParameter("speed.limit.handler.state", map);
/*      */     }
/*      */     
/*  276 */     COConfigurationManager.save();
/*      */   }
/*      */   
/*      */ 
/*      */   private void loadPauseAllActive()
/*      */   {
/*  282 */     setRulePauseAllActive(COConfigurationManager.getBooleanParameter("speed.limit.handler.schedule.pa_active", false));
/*      */     
/*  284 */     setNetLimitPauseAllActive(COConfigurationManager.getBooleanParameter("speed.limit.handler.schedule.nl_pa_active", false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setRulePauseAllActive(boolean active)
/*      */   {
/*  291 */     GlobalManager gm = this.core.getGlobalManager();
/*      */     
/*  293 */     if (active)
/*      */     {
/*  295 */       if (!this.rule_pause_all_active)
/*      */       {
/*  297 */         this.logger.logAlertRepeatable(1, "Pausing all downloads due to pause_all rule");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  302 */       gm.pauseDownloads();
/*      */       
/*  304 */       this.rule_pause_all_active = true;
/*      */     }
/*      */     else
/*      */     {
/*  308 */       if (!this.net_limit_pause_all_active)
/*      */       {
/*  310 */         if (COConfigurationManager.getBooleanParameter("speed.limit.handler.schedule.pa_capable", false))
/*      */         {
/*  312 */           if (this.rule_pause_all_active)
/*      */           {
/*  314 */             this.logger.logAlertRepeatable(1, "Resuming all downloads as pause_all rule no longer applies");
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  319 */           gm.resumeDownloads(true);
/*      */         }
/*      */       }
/*      */       
/*  323 */       this.rule_pause_all_active = false;
/*      */     }
/*      */     
/*  326 */     COConfigurationManager.setParameter("speed.limit.handler.schedule.pa_active", active);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setNetLimitPauseAllActive(boolean active)
/*      */   {
/*  333 */     GlobalManager gm = this.core.getGlobalManager();
/*      */     
/*  335 */     if (active)
/*      */     {
/*  337 */       if (!this.net_limit_pause_all_active)
/*      */       {
/*  339 */         this.logger.logAlertRepeatable(1, "Pausing all downloads as network limit exceeded");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  344 */       gm.pauseDownloads();
/*      */       
/*  346 */       this.net_limit_pause_all_active = true;
/*      */     }
/*      */     else
/*      */     {
/*  350 */       if (!this.rule_pause_all_active)
/*      */       {
/*  352 */         if (COConfigurationManager.getBooleanParameter("speed.limit.handler.schedule.pa_capable", false))
/*      */         {
/*  354 */           if (this.net_limit_pause_all_active)
/*      */           {
/*  356 */             this.logger.logAlertRepeatable(1, "Resuming all downloads as network limit no longer exceeded");
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  361 */           gm.resumeDownloads(true);
/*      */         }
/*      */       }
/*      */       
/*  365 */       this.net_limit_pause_all_active = false;
/*      */     }
/*      */     
/*  368 */     COConfigurationManager.setParameter("speed.limit.handler.schedule.nl_pa_active", active);
/*      */   }
/*      */   
/*      */ 
/*      */   public List<String> reset()
/*      */   {
/*  374 */     if (this.net_limit_pause_all_active)
/*      */     {
/*  376 */       setNetLimitPauseAllActive(false);
/*      */     }
/*      */     
/*  379 */     return resetRules();
/*      */   }
/*      */   
/*      */ 
/*      */   private List<String> resetRules()
/*      */   {
/*  385 */     if (this.rule_pause_all_active)
/*      */     {
/*  387 */       setRulePauseAllActive(false);
/*      */     }
/*      */     
/*  390 */     LimitDetails details = new LimitDetails(null);
/*      */     
/*  392 */     details.loadForReset();
/*      */     
/*  394 */     details.apply();
/*      */     
/*  396 */     return details.getString(true, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public List<String> getCurrent()
/*      */   {
/*  402 */     LimitDetails details = new LimitDetails(null);
/*      */     
/*  404 */     details.loadCurrent();
/*      */     
/*  406 */     List<String> lines = details.getString(true, false);
/*      */     
/*  408 */     lines.add("");
/*  409 */     lines.add("Peer Sets");
/*  410 */     if (this.current_ip_sets.size() == 0) {
/*  411 */       lines.add("    None");
/*      */     } else {
/*  413 */       for (Map.Entry<String, IPSet> entry : this.current_ip_sets.entrySet()) {
/*  414 */         lines.add("    " + ((IPSet)entry.getValue()).getDetailString());
/*      */       }
/*      */     }
/*      */     
/*  418 */     List<Object[]> tag_nls = new ArrayList();
/*      */     
/*  420 */     for (Iterator i$ = this.net_limits.entrySet().iterator(); i$.hasNext();) { entry = (Map.Entry)i$.next();
/*      */       
/*  422 */       for (NetLimit nl : (List)entry.getValue())
/*      */       {
/*  424 */         if (nl.getTag() != null)
/*      */         {
/*  426 */           tag_nls.add(new Object[] { entry.getKey(), nl });
/*      */         }
/*      */       }
/*      */     }
/*      */     Map.Entry<Integer, List<NetLimit>> entry;
/*  431 */     if (tag_nls.size() > 0)
/*      */     {
/*  433 */       lines.add("");
/*  434 */       lines.add("Tag/Peer Set Net Limits");
/*      */       
/*  436 */       for (Object[] entry : tag_nls)
/*      */       {
/*  438 */         int type = ((Integer)entry[0]).intValue();
/*  439 */         NetLimit nl = (NetLimit)entry[1];
/*      */         
/*  441 */         long[] stats = nl.getLongTermStats().getTotalUsageInPeriod(type, nl.getMultiplier());
/*      */         
/*  443 */         long[] limits = nl.getLimits();
/*      */         
/*  445 */         long total_up = stats[0] + stats[1];
/*  446 */         long total_do = stats[2] + stats[3];
/*      */         
/*  448 */         String lim_str = "";
/*      */         
/*  450 */         lim_str = lim_str + LongTermStats.PT_NAMES[type] + ", mult=" + nl.getMultiplier() + ": ";
/*      */         
/*  452 */         long total_lim = limits[0];
/*  453 */         long up_lim = limits[1];
/*  454 */         long down_lim = limits[2];
/*      */         
/*  456 */         String sep = "";
/*      */         
/*  458 */         if (total_lim > 0L)
/*      */         {
/*  460 */           lim_str = lim_str + "Total limit=" + DisplayFormatters.formatByteCountToKiBEtc(total_lim) + ", used=" + DisplayFormatters.formatByteCountToKiBEtc(total_up + total_do) + " - " + 100L * (total_up + total_do) / total_lim + "%";
/*      */           
/*  462 */           sep = ", ";
/*      */         }
/*  464 */         if (up_lim > 0L)
/*      */         {
/*  466 */           lim_str = lim_str + sep + "Up limit=" + DisplayFormatters.formatByteCountToKiBEtc(up_lim) + ", used=" + DisplayFormatters.formatByteCountToKiBEtc(total_up) + " - " + 100L * total_up / up_lim + "%";
/*      */           
/*  468 */           sep = ", ";
/*      */         }
/*  470 */         if (down_lim > 0L)
/*      */         {
/*  472 */           lim_str = lim_str + sep + "Down limit=" + DisplayFormatters.formatByteCountToKiBEtc(down_lim) + ", used=" + DisplayFormatters.formatByteCountToKiBEtc(total_do) + " - " + 100L * total_do / down_lim + "%";
/*      */         }
/*      */         
/*  475 */         lim_str = lim_str + sep + "enabled=" + nl.isEnabled();
/*      */         
/*  477 */         String tag_name = nl.getTag().getTag().getTagName(true);
/*      */         
/*  479 */         String name = nl.getName();
/*      */         
/*  481 */         name = name + (name.length() == 0 ? "" : " ") + tag_name;
/*      */         
/*  483 */         lines.add("    " + name + ": " + lim_str);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  488 */     if (this.current_prioritisers.size() > 0) {
/*  489 */       lines.add("");
/*  490 */       lines.add("Prioritizers: " + this.current_prioritisers.size());
/*      */     }
/*      */     
/*  493 */     ScheduleRule rule = this.active_rule;
/*      */     
/*  495 */     lines.add("");
/*  496 */     lines.add("Scheduler");
/*  497 */     lines.add("    Rules defined: " + this.current_rules.size());
/*  498 */     lines.add("    Active rule: " + (rule == null ? "None" : rule.getString()));
/*      */     
/*  500 */     lines.add("");
/*  501 */     lines.add("Network Totals");
/*      */     
/*  503 */     LongTermStats lt_stats = StatsFactory.getLongTermStats();
/*      */     
/*  505 */     if ((lt_stats == null) || (!lt_stats.isEnabled()))
/*      */     {
/*  507 */       lines.add("    Not Available");
/*      */     }
/*      */     else
/*      */     {
/*  511 */       lines.add("    Today:\t\t" + getString(lt_stats, 1, (List)this.net_limits.get(Integer.valueOf(1))));
/*  512 */       lines.add("    This week:\t" + getString(lt_stats, 2, (List)this.net_limits.get(Integer.valueOf(2))));
/*  513 */       lines.add("    This month:\t" + getString(lt_stats, 3, (List)this.net_limits.get(Integer.valueOf(3))));
/*  514 */       lines.add("");
/*  515 */       lines.add("    Rate (3 minute average):\t\t" + getString(lt_stats.getCurrentRateBytesPerSecond(), null, true));
/*      */     }
/*      */     
/*  518 */     return lines;
/*      */   }
/*      */   
/*      */ 
/*      */   private final IPSetTagType ip_set_tag_type;
/*      */   private final Object extensions_lock;
/*      */   private DML current_dml;
/*      */   private String getString(LongTermStats lts, int type, List<NetLimit> net_limits)
/*      */   {
/*  527 */     if (net_limits == null)
/*      */     {
/*  529 */       net_limits = new ArrayList();
/*      */       
/*  531 */       net_limits.add(null);
/*      */     }
/*      */     
/*  534 */     String result = "";
/*      */     
/*  536 */     for (NetLimit net_limit : net_limits)
/*      */     {
/*  538 */       long[] stats = getLongTermUsage(lts, type, net_limit);
/*      */       
/*  540 */       long total_up = stats[0] + stats[1] + stats[4];
/*  541 */       long total_do = stats[2] + stats[3] + stats[5];
/*      */       
/*  543 */       String lim_str = "";
/*  544 */       String profile = null;
/*      */       
/*  546 */       if (net_limit != null)
/*      */       {
/*  548 */         if (net_limit.getTag() != null) {
/*      */           continue;
/*      */         }
/*      */         
/*      */ 
/*  553 */         profile = net_limit.getProfile();
/*      */         
/*  555 */         long[] limits = net_limit.getLimits();
/*      */         
/*  557 */         long total_lim = limits[0];
/*  558 */         long up_lim = limits[1];
/*  559 */         long down_lim = limits[2];
/*      */         
/*  561 */         if (total_lim > 0L)
/*      */         {
/*  563 */           lim_str = lim_str + "Total=" + DisplayFormatters.formatByteCountToKiBEtc(total_lim) + " " + 100L * (total_up + total_do) / total_lim + "%";
/*      */         }
/*  565 */         if (up_lim > 0L)
/*      */         {
/*  567 */           lim_str = lim_str + (lim_str.length() == 0 ? "" : ", ") + "Up=" + DisplayFormatters.formatByteCountToKiBEtc(up_lim) + " " + 100L * total_up / up_lim + "%";
/*      */         }
/*  569 */         if (down_lim > 0L)
/*      */         {
/*  571 */           lim_str = lim_str + (lim_str.length() == 0 ? "" : ", ") + "Down=" + DisplayFormatters.formatByteCountToKiBEtc(down_lim) + " " + 100L * total_do / down_lim + "%";
/*      */         }
/*      */         
/*  574 */         if (lim_str.length() > 0)
/*      */         {
/*  576 */           lim_str = "\t[ Limits: " + lim_str + "]";
/*      */         }
/*      */       }
/*      */       
/*  580 */       if (net_limits.size() > 1)
/*      */       {
/*  582 */         result = result + "\r\n        ";
/*      */       }
/*      */       
/*  585 */       result = result + (profile == null ? "Overall" : profile) + " - " + "Upload=" + DisplayFormatters.formatByteCountToKiBEtc(total_up) + ", Download=" + DisplayFormatters.formatByteCountToKiBEtc(total_do) + lim_str;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  590 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long[] getLongTermUsage(LongTermStats lts, int type, NetLimit net_limit)
/*      */   {
/*  599 */     double multiplier = net_limit == null ? 1.0D : net_limit.getMultiplier();
/*      */     
/*  601 */     if ((net_limit == null) || (net_limit.getProfile() == null))
/*      */     {
/*  603 */       return lts.getTotalUsageInPeriod(type, multiplier);
/*      */     }
/*      */     
/*  606 */     final String profile = net_limit.getProfile();
/*      */     
/*  608 */     lts.getTotalUsageInPeriod(type, multiplier, new LongTermStats.RecordAccepter()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean acceptRecord(long timestamp)
/*      */       {
/*      */ 
/*      */ 
/*  617 */         SpeedLimitHandler.ScheduleRule rule = SpeedLimitHandler.this.getActiveRule(new Date(timestamp));
/*      */         
/*  619 */         return (rule != null) && (rule.profile_name.equals(profile));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getString(long[] stats, long[] limits, boolean is_rate)
/*      */   {
/*  630 */     long total_up = stats[0] + stats[1] + stats[4];
/*  631 */     long total_do = stats[2] + stats[3] + stats[5];
/*      */     
/*  633 */     String lim_str = "";
/*      */     
/*  635 */     if (limits != null)
/*      */     {
/*  637 */       long total_lim = limits[0];
/*  638 */       long up_lim = limits[1];
/*  639 */       long down_lim = limits[2];
/*      */       
/*  641 */       if (total_lim > 0L)
/*      */       {
/*  643 */         lim_str = lim_str + "Total=" + DisplayFormatters.formatByteCountToKiBEtc(total_lim) + " " + 100L * (total_up + total_do) / total_lim + "%";
/*      */       }
/*  645 */       if (up_lim > 0L)
/*      */       {
/*  647 */         lim_str = lim_str + (lim_str.length() == 0 ? "" : ", ") + "Up=" + DisplayFormatters.formatByteCountToKiBEtc(up_lim) + " " + 100L * total_up / up_lim + "%";
/*      */       }
/*  649 */       if (down_lim > 0L)
/*      */       {
/*  651 */         lim_str = lim_str + (lim_str.length() == 0 ? "" : ", ") + "Down=" + DisplayFormatters.formatByteCountToKiBEtc(down_lim) + " " + 100L * total_do / down_lim + "%";
/*      */       }
/*      */       
/*  654 */       if (lim_str.length() > 0)
/*      */       {
/*  656 */         lim_str = "\t[ Limits: " + lim_str + "]";
/*      */       }
/*      */     }
/*      */     
/*  660 */     if (is_rate)
/*      */     {
/*  662 */       return "Upload=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(total_up) + ", Download=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(total_do);
/*      */     }
/*      */     
/*      */ 
/*  666 */     return "Upload=" + DisplayFormatters.formatByteCountToKiBEtc(total_up) + ", Download=" + DisplayFormatters.formatByteCountToKiBEtc(total_do) + lim_str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<String> getProfileNames()
/*      */   {
/*  673 */     Map map = loadConfig();
/*      */     
/*  675 */     List<String> profiles = new ArrayList();
/*      */     
/*  677 */     List<Map> list = (List)map.get("profiles");
/*      */     
/*  679 */     if (list != null)
/*      */     {
/*  681 */       for (Map m : list)
/*      */       {
/*  683 */         String name = importString(m, "n");
/*      */         
/*  685 */         if (name != null)
/*      */         {
/*  687 */           profiles.add(name);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  692 */     return profiles;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<String> loadProfile(String name)
/*      */   {
/*  699 */     Map map = loadConfig();
/*      */     
/*  701 */     List<Map> list = (List)map.get("profiles");
/*      */     
/*  703 */     if (list != null)
/*      */     {
/*  705 */       for (Map m : list)
/*      */       {
/*  707 */         String p_name = importString(m, "n");
/*      */         
/*  709 */         if ((p_name != null) && (name.equals(p_name)))
/*      */         {
/*  711 */           Map profile = (Map)m.get("p");
/*      */           
/*  713 */           LimitDetails ld = new LimitDetails(profile, null);
/*      */           
/*  715 */           ld.apply();
/*      */           
/*  717 */           return ld.getString(false, false);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  722 */     List<String> result = new ArrayList();
/*      */     
/*  724 */     result.add("Profile not found");
/*      */     
/*  726 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean profileExists(String name)
/*      */   {
/*  733 */     Map map = loadConfig();
/*      */     
/*  735 */     List<Map> list = (List)map.get("profiles");
/*      */     
/*  737 */     if (list != null)
/*      */     {
/*  739 */       for (Map m : list)
/*      */       {
/*  741 */         String p_name = importString(m, "n");
/*      */         
/*  743 */         if ((p_name != null) && (name.equals(p_name)))
/*      */         {
/*  745 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  750 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<String> getProfile(String name)
/*      */   {
/*  757 */     return getProfileSupport(name, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<String> getProfileSupport(String name, boolean use_hashes)
/*      */   {
/*  765 */     Map map = loadConfig();
/*      */     
/*  767 */     List<Map> list = (List)map.get("profiles");
/*      */     
/*  769 */     if (list != null)
/*      */     {
/*  771 */       for (Map m : list)
/*      */       {
/*  773 */         String p_name = importString(m, "n");
/*      */         
/*  775 */         if ((p_name != null) && (name.equals(p_name)))
/*      */         {
/*  777 */           Map profile = (Map)m.get("p");
/*      */           
/*  779 */           LimitDetails ld = new LimitDetails(profile, null);
/*      */           
/*  781 */           return ld.getString(false, use_hashes);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  786 */     List<String> result = new ArrayList();
/*      */     
/*  788 */     result.add("Profile not found");
/*      */     
/*  790 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<String> getProfilesForDownload(byte[] hash)
/*      */   {
/*  797 */     List<String> result = new ArrayList();
/*      */     
/*  799 */     Map map = loadConfig();
/*      */     
/*  801 */     List<Map> list = (List)map.get("profiles");
/*      */     String hash_str;
/*  803 */     if (list != null)
/*      */     {
/*  805 */       hash_str = Base32.encode(hash);
/*      */       
/*  807 */       for (Map m : list)
/*      */       {
/*  809 */         String p_name = importString(m, "n");
/*      */         
/*  811 */         if (p_name != null)
/*      */         {
/*  813 */           Map profile = (Map)m.get("p");
/*      */           
/*  815 */           LimitDetails ld = new LimitDetails(profile, null);
/*      */           
/*  817 */           if (ld.getLimitsForDownload(hash_str) != null)
/*      */           {
/*  819 */             result.add(p_name);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  825 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addRemoveDownloadsToProfile(String name, List<byte[]> hashes, boolean add)
/*      */   {
/*  834 */     Map map = loadConfig();
/*      */     
/*  836 */     List<Map> list = (List)map.get("profiles");
/*      */     
/*  838 */     List<String> hash_strs = new ArrayList();
/*      */     
/*  840 */     for (byte[] hash : hashes)
/*      */     {
/*  842 */       hash_strs.add(Base32.encode(hash));
/*      */     }
/*      */     
/*  845 */     if (list != null)
/*      */     {
/*  847 */       for (Map m : list)
/*      */       {
/*  849 */         String p_name = importString(m, "n");
/*      */         
/*  851 */         if ((p_name != null) && (name.equals(p_name)))
/*      */         {
/*  853 */           Map profile = (Map)m.get("p");
/*      */           
/*  855 */           LimitDetails ld = new LimitDetails(profile, null);
/*      */           
/*  857 */           ld.addRemoveDownloads(hash_strs, add);
/*      */           
/*  859 */           m.put("p", ld.export());
/*      */           
/*  861 */           saveConfig(map);
/*      */           
/*  863 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addDownloadsToProfile(String name, List<byte[]> hashes)
/*      */   {
/*  874 */     addRemoveDownloadsToProfile(name, hashes, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeDownloadsFromProfile(String name, List<byte[]> hashes)
/*      */   {
/*  882 */     addRemoveDownloadsToProfile(name, hashes, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void deleteProfile(String name)
/*      */   {
/*  889 */     Map map = loadConfig();
/*      */     
/*  891 */     List<Map> list = (List)map.get("profiles");
/*      */     
/*  893 */     if (list != null)
/*      */     {
/*  895 */       for (Map m : list)
/*      */       {
/*  897 */         String p_name = importString(m, "n");
/*      */         
/*  899 */         if ((p_name != null) && (name.equals(p_name)))
/*      */         {
/*  901 */           list.remove(m);
/*      */           
/*  903 */           saveConfig(map);
/*      */           
/*  905 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<String> saveProfile(String name)
/*      */   {
/*  915 */     LimitDetails details = new LimitDetails(null);
/*      */     
/*  917 */     details.loadCurrent();
/*      */     
/*  919 */     Map map = loadConfig();
/*      */     
/*  921 */     List<Map> list = (List)map.get("profiles");
/*      */     
/*  923 */     if (list == null)
/*      */     {
/*  925 */       list = new ArrayList();
/*      */       
/*  927 */       map.put("profiles", list);
/*      */     }
/*      */     
/*  930 */     for (Map m : list)
/*      */     {
/*  932 */       String p_name = importString(m, "n");
/*      */       
/*  934 */       if ((p_name != null) && (name.equals(p_name)))
/*      */       {
/*  936 */         list.remove(m);
/*      */         
/*  938 */         break;
/*      */       }
/*      */     }
/*      */     
/*  942 */     Map m = new HashMap();
/*      */     
/*  944 */     list.add(m);
/*      */     
/*  946 */     m.put("n", name);
/*  947 */     m.put("p", details.export());
/*      */     
/*  949 */     saveConfig(map);
/*      */     
/*      */     ScheduleRule rule;
/*      */     
/*  953 */     synchronized (this)
/*      */     {
/*  955 */       rule = this.active_rule;
/*      */     }
/*      */     
/*  958 */     if ((rule != null) && (rule.profile_name.equals(name)))
/*      */     {
/*  960 */       details.apply();
/*      */     }
/*      */     
/*  963 */     return details.getString(false, false);
/*      */   }
/*      */   
/*      */ 
/*      */   private synchronized List<String> loadSchedule()
/*      */   {
/*  969 */     List<String> result = new ArrayList();
/*      */     
/*  971 */     List list = COConfigurationManager.getListParameter("speed.limit.handler.schedule.lines", new ArrayList());
/*  972 */     List<String> schedule_lines = BDecoder.decodeStrings(BEncoder.cloneList(list));
/*      */     
/*  974 */     boolean enabled = true;
/*      */     
/*  976 */     List<ScheduleRule> rules = new ArrayList();
/*  977 */     Map<String, IPSet> ip_sets = new HashMap();
/*      */     
/*  979 */     Map<Integer, List<NetLimit>> new_net_limits = new HashMap();
/*  980 */     List<NetLimit> net_limits_list = new ArrayList();
/*      */     
/*  982 */     List<Prioritiser> new_prioritisers = new ArrayList();
/*      */     
/*  984 */     boolean checked_lts_enabled = false;
/*  985 */     boolean lts_enabled = false;
/*      */     
/*  987 */     for (String line : schedule_lines)
/*      */     {
/*  989 */       line = line.trim();
/*      */       
/*  991 */       if ((line.length() != 0) && (!line.startsWith("#")))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  996 */         String lc_line = line.toLowerCase(Locale.US);
/*      */         
/*  998 */         if (lc_line.startsWith("enable"))
/*      */         {
/* 1000 */           String[] bits = lc_line.split("=");
/*      */           
/* 1002 */           boolean ok = false;
/*      */           
/* 1004 */           if (bits.length == 2)
/*      */           {
/* 1006 */             String arg = bits[1];
/*      */             
/* 1008 */             if (arg.equals("yes"))
/*      */             {
/* 1010 */               enabled = true;
/* 1011 */               ok = true;
/*      */             }
/* 1013 */             else if (arg.equals("no"))
/*      */             {
/* 1015 */               enabled = false;
/* 1016 */               ok = true;
/*      */             }
/*      */           }
/*      */           
/* 1020 */           if (!ok)
/*      */           {
/* 1022 */             result.add("'" + line + "' is invalid: use enable=(yes|no)");
/*      */           }
/* 1024 */         } else if ((lc_line.startsWith("ip_set")) || (lc_line.startsWith("peer_set")))
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/* 1029 */             String[] args = line.substring(lc_line.indexOf('_') + 4).split(",");
/*      */             
/* 1031 */             boolean inverse = false;
/* 1032 */             int up_lim = -1;
/* 1033 */             int down_lim = -1;
/*      */             
/* 1035 */             int peer_up_lim = 0;
/* 1036 */             int peer_down_lim = 0;
/*      */             
/*      */ 
/* 1039 */             Set<String> categories_or_tags = new HashSet();
/*      */             
/* 1041 */             IPSet set = null;
/*      */             
/* 1043 */             for (String arg : args)
/*      */             {
/* 1045 */               String[] bits = arg.split("=");
/*      */               
/* 1047 */               if (bits.length != 2)
/*      */               {
/* 1049 */                 throw new Exception("Expected <key>=<value> for '" + arg + "'");
/*      */               }
/*      */               
/*      */ 
/* 1053 */               String lhs = bits[0].trim();
/* 1054 */               String lc_lhs = lhs.toLowerCase(Locale.US);
/* 1055 */               String rhs = bits[1].trim();
/*      */               
/* 1057 */               String lc_rhs = rhs.toLowerCase(Locale.US);
/*      */               
/* 1059 */               if (lc_lhs.equals("inverse"))
/*      */               {
/* 1061 */                 inverse = lc_rhs.equals("yes");
/*      */               }
/* 1063 */               else if (lc_lhs.equals("up"))
/*      */               {
/* 1065 */                 up_lim = (int)parseRate(lc_rhs);
/*      */               }
/* 1067 */               else if (lc_lhs.equals("down"))
/*      */               {
/* 1069 */                 down_lim = (int)parseRate(lc_rhs);
/*      */               }
/* 1071 */               else if (lc_lhs.equals("peer_up"))
/*      */               {
/* 1073 */                 peer_up_lim = (int)parseRate(lc_rhs);
/*      */               }
/* 1075 */               else if (lc_lhs.equals("peer_down"))
/*      */               {
/* 1077 */                 peer_down_lim = (int)parseRate(lc_rhs);
/*      */               }
/* 1079 */               else if ((lc_lhs.equals("cat")) || (lc_lhs.equals("tag")))
/*      */               {
/* 1081 */                 String[] cats = rhs.split(" ");
/*      */                 
/* 1083 */                 for (String cat : cats)
/*      */                 {
/* 1085 */                   cat = cat.trim();
/*      */                   
/* 1087 */                   if (cat.length() > 0)
/*      */                   {
/* 1089 */                     categories_or_tags.add(cat);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               else {
/* 1094 */                 String name = lhs;
/*      */                 
/* 1096 */                 String def = rhs.replace(';', ' ');
/*      */                 
/* 1098 */                 set = (IPSet)ip_sets.get(name);
/*      */                 
/* 1100 */                 if (set == null)
/*      */                 {
/* 1102 */                   set = new IPSet(name, null);
/*      */                   
/* 1104 */                   ip_sets.put(name, set);
/*      */                 }
/*      */                 
/* 1107 */                 bits = def.split(" ");
/*      */                 
/* 1109 */                 for (String bit : bits)
/*      */                 {
/* 1111 */                   bit = bit.trim();
/*      */                   
/* 1113 */                   if (bit.length() > 0)
/*      */                   {
/* 1115 */                     IPSet other_set = (IPSet)ip_sets.get(bit);
/*      */                     
/* 1117 */                     if ((other_set != null) && (other_set != set))
/*      */                     {
/* 1119 */                       set.addSet(other_set);
/*      */ 
/*      */ 
/*      */                     }
/* 1123 */                     else if (!set.addCIDRorCCetc(bit))
/*      */                     {
/* 1125 */                       result.add("CIDR, CC, Network or ip_set reference '" + bit + "' isn't valid");
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1134 */             if (set == null)
/*      */             {
/* 1136 */               throw new Exception();
/*      */             }
/*      */             
/* 1139 */             set.setParameters(inverse, up_lim, down_lim, peer_up_lim, peer_down_lim, categories_or_tags);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1143 */             result.add("'" + line + "' is invalid: use ip_set <name>=<cidrs...> [,inverse=[yes|no]] [,up=<limit>] [,down=<limit>] [,peer_up=<limit>] [,peer_down=<limit>] [,cat=<categories>]: " + e.getMessage());
/*      */           }
/* 1145 */         } else if (lc_line.startsWith("net_limit"))
/*      */         {
/* 1147 */           if (!checked_lts_enabled)
/*      */           {
/* 1149 */             checked_lts_enabled = true;
/*      */             
/* 1151 */             lts_enabled = StatsFactory.getLongTermStats().isEnabled();
/*      */             
/* 1153 */             if (!lts_enabled)
/*      */             {
/* 1155 */               result.add("Long-term stats are currently disabled, limits will NOT be applied");
/*      */             }
/*      */           }
/*      */           
/* 1159 */           line = line.substring(9).replace(",", " ");
/*      */           
/* 1161 */           String[] args = line.split(" ");
/*      */           
/* 1163 */           String name = "";
/* 1164 */           int type = -1;
/* 1165 */           double mult = 1.0D;
/* 1166 */           String profile = null;
/*      */           
/* 1168 */           TagType tag_type = null;
/* 1169 */           String tag_name = null;
/*      */           
/* 1171 */           long total_lim = 0L;
/* 1172 */           long up_lim = 0L;
/* 1173 */           long down_lim = 0L;
/*      */           
/* 1175 */           for (String arg : args)
/*      */           {
/* 1177 */             arg = arg.trim();
/*      */             
/* 1179 */             if (arg.length() != 0)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1184 */               if (type == -1)
/*      */               {
/* 1186 */                 int sep = arg.indexOf(":");
/*      */                 
/* 1188 */                 if (sep != -1)
/*      */                 {
/* 1190 */                   profile = arg.substring(sep + 1).trim();
/*      */                   
/* 1192 */                   if (!profileExists(profile))
/*      */                   {
/* 1194 */                     result.add("net_limit profile '" + profile + "' not defined");
/*      */                     
/* 1196 */                     break;
/*      */                   }
/*      */                   
/* 1199 */                   arg = arg.substring(0, sep);
/*      */                 }
/*      */                 else
/*      */                 {
/* 1203 */                   sep = arg.indexOf("$");
/*      */                   
/* 1205 */                   if (sep != -1)
/*      */                   {
/* 1207 */                     tag_name = arg.substring(sep + 1).trim();
/*      */                     
/* 1209 */                     TagType tag_type_dm = TagManagerFactory.getTagManager().getTagType(3);
/*      */                     
/* 1211 */                     TagFeatureRateLimit tag_dm = (TagFeatureRateLimit)tag_type_dm.getTag(tag_name, true);
/*      */                     
/* 1213 */                     if (tag_dm != null)
/*      */                     {
/* 1215 */                       tag_type = tag_type_dm;
/*      */ 
/*      */ 
/*      */                     }
/* 1219 */                     else if (ip_sets.get(tag_name) != null)
/*      */                     {
/* 1221 */                       tag_type = this.ip_set_tag_type;
/*      */                     }
/*      */                     
/*      */ 
/* 1225 */                     if (tag_type == null)
/*      */                     {
/* 1227 */                       result.add("net_limit tag/peer set '" + tag_name + "' not defined or invalid");
/*      */                       
/* 1229 */                       break;
/*      */                     }
/*      */                     
/* 1232 */                     arg = arg.substring(0, sep);
/*      */                   }
/*      */                 }
/*      */                 
/* 1236 */                 int pos = arg.indexOf("*");
/*      */                 
/* 1238 */                 if (pos != -1)
/*      */                 {
/* 1240 */                   mult = Double.parseDouble(arg.substring(pos + 1));
/*      */                   
/* 1242 */                   arg = arg.substring(0, pos);
/*      */                 }
/*      */                 
/* 1245 */                 boolean sliding = false;
/*      */                 
/* 1247 */                 if (arg.equalsIgnoreCase("hourly"))
/*      */                 {
/* 1249 */                   type = 0;
/*      */                 }
/* 1251 */                 else if (arg.equalsIgnoreCase("shourly"))
/*      */                 {
/* 1253 */                   type = 10;
/*      */                   
/* 1255 */                   sliding = true;
/*      */                 }
/* 1257 */                 else if (arg.equalsIgnoreCase("daily"))
/*      */                 {
/* 1259 */                   type = 1;
/*      */                 }
/* 1261 */                 else if (arg.equalsIgnoreCase("sdaily"))
/*      */                 {
/* 1263 */                   type = 11;
/*      */                   
/* 1265 */                   sliding = true;
/*      */                 }
/* 1267 */                 else if (arg.equalsIgnoreCase("weekly"))
/*      */                 {
/* 1269 */                   type = 2;
/*      */                 }
/* 1271 */                 else if (arg.equalsIgnoreCase("sweekly"))
/*      */                 {
/* 1273 */                   type = 12;
/*      */                   
/* 1275 */                   sliding = true;
/*      */                 }
/* 1277 */                 else if (arg.equalsIgnoreCase("monthly"))
/*      */                 {
/* 1279 */                   type = 3;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1283 */                   result.add("net_limit type of '" + arg + "' not recognised - use hourly, daily, weekly or monthly");
/*      */                   
/* 1285 */                   break;
/*      */                 }
/*      */                 
/* 1288 */                 if ((mult != 1.0D) && (!sliding))
/*      */                 {
/* 1290 */                   result.add("'" + line + "': invalid net_limit specification. multiplier only supported for sliding windows.");
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/* 1295 */                 String[] bits = arg.split("=");
/*      */                 
/* 1297 */                 if (bits.length != 2)
/*      */                 {
/* 1299 */                   result.add("'" + line + "': invalid net_limit specification");
/*      */                 }
/*      */                 else
/*      */                 {
/* 1303 */                   String lhs = bits[0];
/* 1304 */                   String rhs = bits[1];
/*      */                   
/* 1306 */                   if (lhs.equalsIgnoreCase("name"))
/*      */                   {
/* 1308 */                     name = rhs;
/*      */                   }
/*      */                   else {
/* 1311 */                     long lim = parseRate(rhs);
/*      */                     
/* 1313 */                     if (lhs.equalsIgnoreCase("total"))
/*      */                     {
/* 1315 */                       total_lim = lim;
/*      */                     }
/* 1317 */                     else if (lhs.equalsIgnoreCase("up"))
/*      */                     {
/* 1319 */                       up_lim = lim;
/*      */                     }
/* 1321 */                     else if (lhs.equalsIgnoreCase("down"))
/*      */                     {
/* 1323 */                       down_lim = lim;
/*      */                     }
/*      */                     else
/*      */                     {
/* 1327 */                       result.add("'" + line + "': invalid net_limit specification");
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 1334 */           if (type != -1)
/*      */           {
/* 1336 */             List<NetLimit> limits = (List)new_net_limits.get(Integer.valueOf(type));
/*      */             
/* 1338 */             if (limits == null)
/*      */             {
/* 1340 */               limits = new ArrayList();
/*      */               
/* 1342 */               new_net_limits.put(Integer.valueOf(type), limits);
/*      */             }
/*      */             
/* 1345 */             NetLimit limit = new NetLimit(name, mult, profile, tag_type, tag_name, total_lim, up_lim, down_lim, null);
/*      */             
/* 1347 */             limits.add(limit);
/*      */             
/* 1349 */             net_limits_list.add(limit);
/*      */           }
/* 1351 */         } else if ((lc_line.startsWith("priority_down ")) || (lc_line.startsWith("priority_up ")))
/*      */         {
/* 1353 */           String[] args = line.substring(lc_line.indexOf(' ') + 1).split(",");
/*      */           
/* 1355 */           Prioritiser pri = new Prioritiser(null);
/*      */           
/* 1357 */           pri.setIsDown(lc_line.startsWith("priority_down "));
/*      */           
/* 1359 */           TagType tag_type_dm = TagManagerFactory.getTagManager().getTagType(3);
/*      */           
/* 1361 */           boolean pri_ok = true;
/*      */           
/* 1363 */           for (String arg : args)
/*      */           {
/* 1365 */             String[] bits = arg.split("=");
/*      */             
/* 1367 */             boolean ok = false;
/*      */             try
/*      */             {
/* 1370 */               if (bits.length == 2)
/*      */               {
/* 1372 */                 String lhs = bits[0].trim();
/* 1373 */                 String rhs = bits[1].trim();
/*      */                 
/* 1375 */                 if (Character.isDigit(lhs.charAt(0)))
/*      */                 {
/* 1377 */                   int p = Integer.parseInt(lhs);
/*      */                   
/* 1379 */                   TagType tag_type = null;
/*      */                   
/* 1381 */                   TagFeatureRateLimit tag_dm = (TagFeatureRateLimit)tag_type_dm.getTag(rhs, true);
/*      */                   
/* 1383 */                   if (tag_dm != null)
/*      */                   {
/* 1385 */                     tag_type = tag_type_dm;
/*      */ 
/*      */ 
/*      */                   }
/* 1389 */                   else if (ip_sets.get(rhs) != null)
/*      */                   {
/* 1391 */                     tag_type = this.ip_set_tag_type;
/*      */                   }
/*      */                   
/*      */ 
/* 1395 */                   if (tag_type != null)
/*      */                   {
/* 1397 */                     pri.addTarget(p, tag_type, rhs);
/*      */                     
/* 1399 */                     ok = true;
/*      */                   }
/* 1401 */                 } else if (lhs.equalsIgnoreCase("freq"))
/*      */                 {
/* 1403 */                   pri.setFrequency(Integer.parseInt(rhs));
/*      */                   
/* 1405 */                   ok = true;
/*      */                 }
/* 1407 */                 else if (lhs.equalsIgnoreCase("rest"))
/*      */                 {
/* 1409 */                   pri.setRestTicks(Integer.parseInt(rhs));
/*      */                   
/* 1411 */                   ok = true;
/*      */                 }
/* 1413 */                 else if (lhs.equalsIgnoreCase("probe"))
/*      */                 {
/* 1415 */                   pri.setProbePeriod(Integer.parseInt(rhs));
/*      */                   
/* 1417 */                   ok = true;
/*      */                 }
/* 1419 */                 else if (lhs.equals("min"))
/*      */                 {
/* 1421 */                   int min = (int)parseRate(rhs);
/*      */                   
/* 1423 */                   if (min == 0)
/*      */                   {
/* 1425 */                     min = -1;
/*      */                   }
/*      */                   
/* 1428 */                   pri.setMinimum(min);
/*      */                   
/* 1430 */                   ok = true;
/*      */                 }
/* 1432 */                 else if (lhs.equals("max"))
/*      */                 {
/* 1434 */                   pri.setMaximum((int)parseRate(rhs));
/*      */                   
/* 1436 */                   ok = true;
/*      */                 }
/* 1438 */                 else if (lhs.equals("name"))
/*      */                 {
/* 1440 */                   pri.setName(rhs);
/*      */                   
/* 1442 */                   ok = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {}
/*      */             
/* 1448 */             if (!ok)
/*      */             {
/* 1450 */               result.add("'" + line + "': invalid argument: " + arg);
/*      */               
/* 1452 */               pri_ok = false;
/*      */             }
/*      */           }
/*      */           
/* 1456 */           if (pri.getTargetCount() < 2)
/*      */           {
/* 1458 */             result.add("'" + line + "': insufficient targets");
/*      */ 
/*      */ 
/*      */           }
/* 1462 */           else if (pri_ok)
/*      */           {
/* 1464 */             new_prioritisers.add(pri);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1469 */           String[] _bits = line.split(" ");
/*      */           
/* 1471 */           List<String> bits = new ArrayList();
/*      */           
/* 1473 */           for (String b : _bits)
/*      */           {
/* 1475 */             b = b.trim();
/*      */             
/* 1477 */             if (b.length() > 0)
/*      */             {
/* 1479 */               bits.add(b);
/*      */             }
/*      */           }
/*      */           
/* 1483 */           List<String> errors = new ArrayList();
/*      */           
/* 1485 */           if (bits.size() >= 6)
/*      */           {
/* 1487 */             String freq_str = ((String)bits.get(0)).toLowerCase(Locale.US);
/*      */             
/* 1489 */             byte freq = 0;
/*      */             
/* 1491 */             if (freq_str.equals("daily"))
/*      */             {
/* 1493 */               freq = Byte.MAX_VALUE;
/*      */             }
/* 1495 */             else if (freq_str.equals("weekdays"))
/*      */             {
/* 1497 */               freq = 31;
/*      */             }
/* 1499 */             else if (freq_str.equals("weekends"))
/*      */             {
/* 1501 */               freq = 96;
/*      */             }
/* 1503 */             else if (freq_str.equals("mon"))
/*      */             {
/* 1505 */               freq = 1;
/*      */             }
/* 1507 */             else if (freq_str.equals("tue"))
/*      */             {
/* 1509 */               freq = 2;
/*      */             }
/* 1511 */             else if (freq_str.equals("wed"))
/*      */             {
/* 1513 */               freq = 4;
/*      */             }
/* 1515 */             else if (freq_str.equals("thu"))
/*      */             {
/* 1517 */               freq = 8;
/*      */             }
/* 1519 */             else if (freq_str.equals("fri"))
/*      */             {
/* 1521 */               freq = 16;
/*      */             }
/* 1523 */             else if (freq_str.equals("sat"))
/*      */             {
/* 1525 */               freq = 32;
/*      */             }
/* 1527 */             else if (freq_str.equals("sun"))
/*      */             {
/* 1529 */               freq = 64;
/*      */             }
/*      */             else
/*      */             {
/* 1533 */               errors.add("frequency '" + freq_str + "' is invalid");
/*      */             }
/*      */             
/* 1536 */             String profile = (String)bits.get(1);
/*      */             
/* 1538 */             if ((!profileExists(profile)) && (!this.predefined_profile_names.contains(profile.toLowerCase())))
/*      */             {
/* 1540 */               errors.add("profile '" + profile + "' not found");
/*      */               
/* 1542 */               profile = null;
/*      */             }
/*      */             
/* 1545 */             int from_mins = -1;
/*      */             
/* 1547 */             if (((String)bits.get(2)).equalsIgnoreCase("from"))
/*      */             {
/* 1549 */               from_mins = getMins((String)bits.get(3));
/*      */             }
/*      */             
/* 1552 */             if (from_mins == -1)
/*      */             {
/* 1554 */               errors.add("'from' is invalid");
/*      */             }
/*      */             
/* 1557 */             int to_mins = -1;
/*      */             
/* 1559 */             if (((String)bits.get(4)).equalsIgnoreCase("to"))
/*      */             {
/* 1561 */               to_mins = getMins((String)bits.get(5));
/*      */             }
/*      */             
/* 1564 */             if (to_mins == -1)
/*      */             {
/* 1566 */               errors.add("'to' is invalid");
/*      */             }
/*      */             
/* 1569 */             List<ScheduleRuleExtensions> extensions = null;
/*      */             
/* 1571 */             for (int i = 6; i < bits.size(); i++)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1576 */               String extension = (String)bits.get(i);
/*      */               
/* 1578 */               String[] temp = extension.split(":");
/*      */               
/* 1580 */               boolean ok = false;
/* 1581 */               String extra = "";
/*      */               
/* 1583 */               if (temp.length == 1)
/*      */               {
/* 1585 */                 String ext_cmd = temp[0];
/*      */                 
/* 1587 */                 if ((ext_cmd.equals("enable_priority")) || (ext_cmd.equals("disable_priority")))
/*      */                 {
/*      */ 
/* 1590 */                   if (extensions == null)
/*      */                   {
/* 1592 */                     extensions = new ArrayList(bits.size() - 6);
/*      */                   }
/*      */                   
/*      */                   int et;
/*      */                   int et;
/* 1597 */                   if (ext_cmd.equals("enable_priority"))
/*      */                   {
/* 1599 */                     et = 5;
/*      */                   }
/*      */                   else
/*      */                   {
/* 1603 */                     et = 6;
/*      */                   }
/*      */                   
/* 1606 */                   extensions.add(new ScheduleRuleExtensions(et, null));
/*      */                   
/* 1608 */                   ok = true;
/*      */                 }
/* 1610 */               } else if (temp.length == 2)
/*      */               {
/* 1612 */                 String ext_cmd = temp[0];
/* 1613 */                 String ext_param = temp[1];
/*      */                 
/* 1615 */                 if ((ext_cmd.equals("start_tag")) || (ext_cmd.equals("stop_tag")) || (ext_cmd.equals("pause_tag")) || (ext_cmd.equals("resume_tag")))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 1620 */                   TagDownload tag = (TagDownload)TagManagerFactory.getTagManager().getTagType(3).getTag(ext_param, true);
/*      */                   
/* 1622 */                   if (tag == null)
/*      */                   {
/* 1624 */                     tag = (TagDownload)TagManagerFactory.getTagManager().getTagType(2).getTag(ext_param, true);
/*      */                   }
/*      */                   
/* 1627 */                   if (tag == null)
/*      */                   {
/* 1629 */                     extra = ", tag '" + ext_param + "' not found";
/*      */                   }
/*      */                   else
/*      */                   {
/* 1633 */                     if (extensions == null)
/*      */                     {
/* 1635 */                       extensions = new ArrayList(bits.size() - 6);
/*      */                     }
/*      */                     
/*      */                     int et;
/*      */                     int et;
/* 1640 */                     if (ext_cmd.equals("start_tag"))
/*      */                     {
/* 1642 */                       et = 1;
/*      */                     } else { int et;
/* 1644 */                       if (ext_cmd.equals("stop_tag"))
/*      */                       {
/* 1646 */                         et = 2;
/*      */                       } else { int et;
/* 1648 */                         if (ext_cmd.equals("pause_tag"))
/*      */                         {
/* 1650 */                           et = 3;
/*      */                         }
/*      */                         else
/*      */                         {
/* 1654 */                           et = 4; }
/*      */                       }
/*      */                     }
/* 1657 */                     extensions.add(new ScheduleRuleExtensions(et, tag, null));
/*      */                     
/* 1659 */                     ok = true;
/*      */                   }
/* 1661 */                 } else if ((ext_cmd.equals("enable_net_limit")) || (ext_cmd.equals("disable_net_limit")))
/*      */                 {
/*      */ 
/* 1664 */                   List<NetLimit> limits = new ArrayList();
/*      */                   
/* 1666 */                   String[] nls = ext_param.split(";");
/*      */                   
/* 1668 */                   List<String> missing = new ArrayList();
/*      */                   
/* 1670 */                   for (String nl : nls)
/*      */                   {
/* 1672 */                     nl = nl.trim();
/*      */                     
/* 1674 */                     boolean found = false;
/*      */                     
/* 1676 */                     for (NetLimit x : net_limits_list)
/*      */                     {
/* 1678 */                       if (x.getName().equals(nl))
/*      */                       {
/* 1680 */                         limits.add(x);
/*      */                         
/* 1682 */                         found = true;
/*      */                         
/* 1684 */                         break;
/*      */                       }
/*      */                     }
/* 1687 */                     if (!found)
/*      */                     {
/* 1689 */                       missing.add(nl);
/*      */                     }
/*      */                   }
/*      */                   
/* 1693 */                   if (missing.size() == 0)
/*      */                   {
/*      */                     int et;
/*      */                     int et;
/* 1697 */                     if (ext_cmd.equals("enable_net_limit"))
/*      */                     {
/* 1699 */                       et = 7;
/*      */                     }
/*      */                     else
/*      */                     {
/* 1703 */                       et = 8;
/*      */                     }
/*      */                     
/* 1706 */                     ok = true;
/*      */                     
/* 1708 */                     if (extensions == null)
/*      */                     {
/* 1710 */                       extensions = new ArrayList(bits.size() - 6);
/*      */                     }
/*      */                     
/* 1713 */                     extensions.add(new ScheduleRuleExtensions(et, limits, null));
/*      */                   }
/*      */                   else
/*      */                   {
/* 1717 */                     extra = ", net_limit(s) '" + missing + "' not found";
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/* 1722 */               if (!ok)
/*      */               {
/* 1724 */                 errors.add("extension '" + extension + "' is invalid" + extra);
/*      */               }
/*      */             }
/*      */             
/* 1728 */             if (errors.size() == 0)
/*      */             {
/* 1730 */               rules.add(new ScheduleRule(freq, profile, from_mins, to_mins, extensions, null));
/*      */             }
/*      */             else
/*      */             {
/* 1734 */               String err_str = "";
/*      */               
/* 1736 */               for (String e : errors)
/*      */               {
/* 1738 */                 err_str = err_str + (err_str.length() == 0 ? "" : ", ") + e;
/*      */               }
/*      */               
/* 1741 */               result.add("'" + line + "' is invalid (" + err_str + ") - use <frequency> <profile> from <hh:mm> to <hh:mm>");
/*      */             }
/*      */           }
/*      */           else {
/* 1745 */             result.add("'" + line + "' is invalid: use <frequency> <profile> from <hh:mm> to <hh:mm> [extensions]*");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1754 */     boolean schedule_has_net_limits = false;
/* 1755 */     boolean schedule_has_pausing = false;
/*      */     
/* 1757 */     if (enabled)
/*      */     {
/* 1759 */       if (new_net_limits.size() > 0)
/*      */       {
/* 1761 */         schedule_has_net_limits = true;
/*      */       }
/*      */       
/* 1764 */       for (ScheduleRule rule : rules)
/*      */       {
/* 1766 */         String profile_name = rule.profile_name;
/*      */         
/* 1768 */         if ((profile_name.equalsIgnoreCase("pause_all")) || (profile_name.equalsIgnoreCase("resume_all")))
/*      */         {
/* 1770 */           schedule_has_pausing = true;
/*      */           
/* 1772 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1777 */     if (!schedule_has_pausing)
/*      */     {
/* 1779 */       setRulePauseAllActive(false);
/*      */     }
/*      */     
/* 1782 */     if (!schedule_has_net_limits)
/*      */     {
/* 1784 */       setNetLimitPauseAllActive(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1793 */     COConfigurationManager.setParameter("speed.limit.handler.schedule.pa_capable", (enabled) && ((schedule_has_pausing) || (schedule_has_net_limits)));
/*      */     
/* 1795 */     if (enabled)
/*      */     {
/* 1797 */       this.current_rules = rules;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1802 */       for (IPSet s : this.current_ip_sets.values())
/*      */       {
/* 1804 */         s.destroy();
/*      */       }
/*      */       
/* 1807 */       this.current_ip_sets = ip_sets;
/*      */       
/* 1809 */       Map<IPSet, Integer> id_map = new HashMap();
/* 1810 */       int id_max = -1;
/*      */       
/* 1812 */       for (int i = 0; i < 2; i++)
/*      */       {
/* 1814 */         for (IPSet s : this.current_ip_sets.values())
/*      */         {
/* 1816 */           String name = s.getName();
/*      */           try
/*      */           {
/* 1819 */             String config_key = "speed.limit.handler.ipset_n." + Base32.encode(name.getBytes("UTF-8"));
/*      */             
/* 1821 */             if (i == 0)
/*      */             {
/* 1823 */               int existing = COConfigurationManager.getIntParameter(config_key, -1);
/*      */               
/* 1825 */               if (existing != -1)
/*      */               {
/* 1827 */                 id_map.put(s, Integer.valueOf(existing));
/*      */                 
/* 1829 */                 id_max = Math.max(id_max, existing);
/*      */               }
/*      */             }
/*      */             else {
/* 1833 */               Integer tag_id = (Integer)id_map.get(s);
/*      */               
/* 1835 */               if (tag_id == null)
/*      */               {
/* 1837 */                 id_max++;tag_id = Integer.valueOf(id_max);
/*      */                 
/* 1839 */                 COConfigurationManager.setParameter(config_key, tag_id.intValue());
/*      */               }
/*      */               
/* 1842 */               s.initialise(tag_id.intValue());
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 1846 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1851 */       checkIPSets();
/*      */       
/*      */ 
/*      */ 
/* 1855 */       if (!lts_enabled)
/*      */       {
/* 1857 */         new_net_limits.clear();
/*      */       }
/*      */       
/* 1860 */       this.net_limits = new_net_limits;
/*      */       
/* 1862 */       if (this.net_limits.size() > 0)
/*      */       {
/* 1864 */         for (List<NetLimit> l : new_net_limits.values())
/*      */         {
/* 1866 */           for (NetLimit n : l)
/*      */           {
/* 1868 */             n.initialise();
/*      */           }
/*      */         }
/*      */         
/* 1872 */         if (!this.net_limit_listener_added)
/*      */         {
/* 1874 */           this.net_limit_listener_added = true;
/*      */           
/* 1876 */           StatsFactory.getLongTermStats().addListener(1048576L, this);
/*      */         }
/*      */         
/* 1879 */         updated(StatsFactory.getLongTermStats());
/*      */ 
/*      */ 
/*      */       }
/* 1883 */       else if (this.net_limit_listener_added)
/*      */       {
/* 1885 */         this.net_limit_listener_added = false;
/*      */         
/* 1887 */         StatsFactory.getLongTermStats().removeListener(this);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1893 */       this.current_prioritisers = new_prioritisers;
/*      */       
/* 1895 */       if (new_prioritisers.size() == 0)
/*      */       {
/* 1897 */         if (this.prioritiser_event != null)
/*      */         {
/* 1899 */           this.prioritiser_event.cancel();
/*      */           
/* 1901 */           this.prioritiser_event = null;
/*      */         }
/*      */       }
/*      */       else {
/* 1905 */         for (Prioritiser p : new_prioritisers)
/*      */         {
/* 1907 */           p.initialise();
/*      */         }
/*      */         
/* 1910 */         if (this.prioritiser_event == null)
/*      */         {
/* 1912 */           this.prioritiser_event = SimpleTimer.addPeriodicEvent("speed handler prioritiser", 5000L, new TimerEventPerformer()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void perform(TimerEvent event)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1922 */               SpeedLimitHandler.this.checkPrioritisers();
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1930 */       if ((this.schedule_event == null) && ((rules.size() > 0) || (this.net_limits.size() > 0)))
/*      */       {
/* 1932 */         this.schedule_event = SimpleTimer.addPeriodicEvent("speed handler scheduler", 30000L, new TimerEventPerformer()
/*      */         {
/*      */           private int tick_count;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/* 1944 */             this.tick_count += 1;
/*      */             
/* 1946 */             SpeedLimitHandler.this.checkSchedule(this.tick_count);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1951 */       if ((this.active_rule != null) || (rules.size() > 0) || (this.net_limits.size() > 0))
/*      */       {
/* 1953 */         checkSchedule(0);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1958 */       this.current_rules.clear();
/*      */       
/* 1960 */       if (this.schedule_event != null)
/*      */       {
/* 1962 */         this.schedule_event.cancel();
/*      */         
/* 1964 */         this.schedule_event = null;
/*      */       }
/*      */       
/* 1967 */       if (this.active_rule != null)
/*      */       {
/* 1969 */         this.active_rule = null;
/*      */         
/* 1971 */         resetRules();
/*      */       }
/*      */       
/* 1974 */       for (IPSet s : this.current_ip_sets.values())
/*      */       {
/* 1976 */         s.destroy();
/*      */       }
/*      */       
/* 1979 */       this.current_ip_sets.clear();
/*      */       
/* 1981 */       checkIPSets();
/*      */       
/* 1983 */       if (this.net_limit_pause_all_active)
/*      */       {
/* 1985 */         setNetLimitPauseAllActive(false);
/*      */       }
/*      */       
/* 1988 */       this.net_limits.clear();
/*      */       
/* 1990 */       if (this.net_limit_listener_added)
/*      */       {
/* 1992 */         this.net_limit_listener_added = false;
/*      */         
/* 1994 */         StatsFactory.getLongTermStats().removeListener(this);
/*      */       }
/*      */     }
/*      */     
/* 1998 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private long parseRate(String str)
/*      */   {
/* 2005 */     str = str.toLowerCase(Locale.US);
/*      */     
/* 2007 */     int pos = str.indexOf("/");
/*      */     
/* 2009 */     if (pos != -1)
/*      */     {
/* 2011 */       str = str.substring(0, pos).trim();
/*      */     }
/*      */     
/* 2014 */     String num = "";
/* 2015 */     long mult = 1L;
/*      */     
/* 2017 */     for (int i = 0; i < str.length(); i++)
/*      */     {
/* 2019 */       char c = str.charAt(i);
/*      */       
/* 2021 */       if ((Character.isDigit(c)) || (c == '.'))
/*      */       {
/* 2023 */         num = num + c;
/*      */       }
/*      */       else
/*      */       {
/* 2027 */         if (c == 'k')
/*      */         {
/* 2029 */           mult = 1024L; break;
/*      */         }
/* 2031 */         if (c == 'm')
/*      */         {
/* 2033 */           mult = 1048576L; break;
/*      */         }
/* 2035 */         if (c != 'g')
/*      */           break;
/* 2037 */         mult = 1073741824L; break;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2044 */     if (num.contains("."))
/*      */     {
/* 2046 */       return (Float.parseFloat(num) * (float)mult);
/*      */     }
/*      */     
/*      */ 
/* 2050 */     return Integer.parseInt(num) * mult;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int getMins(String str)
/*      */   {
/*      */     try
/*      */     {
/* 2059 */       String[] bits = str.split(":");
/*      */       
/* 2061 */       if (bits.length == 2)
/*      */       {
/* 2063 */         return Integer.parseInt(bits[0].trim()) * 60 + Integer.parseInt(bits[1].trim());
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 2068 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/* 2073 */   private static final Object ip_set_peer_key = new Object();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final FrequencyLimitedDispatcher check_ip_sets_limiter;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkIPSets()
/*      */   {
/* 2089 */     this.check_ip_sets_limiter.dispatch();
/*      */   }
/*      */   
/*      */ 
/*      */   private synchronized void checkIPSetsSupport()
/*      */   {
/* 2095 */     org.gudy.azureus2.plugins.download.DownloadManager download_manager = this.plugin_interface.getDownloadManager();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2101 */     if (this.current_dml != null)
/*      */     {
/* 2103 */       this.current_dml.destroy();
/*      */       
/* 2105 */       this.current_dml = null;
/*      */     }
/*      */     
/* 2108 */     Download[] downloads = download_manager.getDownloads();
/*      */     
/* 2110 */     for (Download dm : downloads)
/*      */     {
/* 2112 */       PeerManager pm = dm.getPeerManager();
/*      */       
/* 2114 */       if (pm != null)
/*      */       {
/* 2116 */         Peer[] peers = pm.getPeers();
/*      */         
/* 2118 */         for (Peer peer : peers)
/*      */         {
/* 2120 */           RateLimiter[] lims = peer.getRateLimiters(false);
/*      */           
/* 2122 */           for (RateLimiter l : lims)
/*      */           {
/* 2124 */             if (this.ip_set_rate_limiters_down.containsValue(l))
/*      */             {
/* 2126 */               synchronized (RL_TO_BE_REMOVED_LOCK)
/*      */               {
/* 2128 */                 List<RateLimiter> to_be_removed = (List)peer.getUserData(RLD_TO_BE_REMOVED_KEY);
/*      */                 
/* 2130 */                 if (to_be_removed == null)
/*      */                 {
/* 2132 */                   to_be_removed = new ArrayList();
/*      */                   
/* 2134 */                   peer.setUserData(RLD_TO_BE_REMOVED_KEY, to_be_removed);
/*      */                 }
/*      */                 
/* 2137 */                 to_be_removed.add(l);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2147 */           lims = peer.getRateLimiters(true);
/*      */           
/* 2149 */           for (RateLimiter l : lims)
/*      */           {
/* 2151 */             if (this.ip_set_rate_limiters_up.containsValue(l))
/*      */             {
/* 2153 */               synchronized (RL_TO_BE_REMOVED_LOCK)
/*      */               {
/* 2155 */                 List<RateLimiter> to_be_removed = (List)peer.getUserData(RLU_TO_BE_REMOVED_KEY);
/*      */                 
/* 2157 */                 if (to_be_removed == null)
/*      */                 {
/* 2159 */                   to_be_removed = new ArrayList();
/*      */                   
/* 2161 */                   peer.setUserData(RLU_TO_BE_REMOVED_KEY, to_be_removed);
/*      */                 }
/*      */                 
/* 2164 */                 to_be_removed.add(l);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2174 */     this.ip_set_rate_limiters_down.clear();
/* 2175 */     this.ip_set_rate_limiters_up.clear();
/*      */     
/* 2177 */     boolean has_cats_or_tags = false;
/*      */     
/* 2179 */     for (IPSet set : this.current_ip_sets.values())
/*      */     {
/* 2181 */       this.ip_set_rate_limiters_down.put(set.getName(), set.getDownLimiter());
/*      */       
/* 2183 */       this.ip_set_rate_limiters_up.put(set.getName(), set.getUpLimiter());
/*      */       
/* 2185 */       if (set.getCategoriesOrTags() != null)
/*      */       {
/* 2187 */         has_cats_or_tags = true;
/*      */       }
/*      */       
/* 2190 */       set.removeAllPeers();
/*      */     }
/*      */     
/* 2193 */     if (this.current_ip_sets.size() == 0)
/*      */     {
/* 2195 */       if (this.ip_set_event != null)
/*      */       {
/* 2197 */         this.ip_set_event.cancel();
/*      */         
/* 2199 */         this.ip_set_event = null;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 2204 */       if (this.ip_set_event == null)
/*      */       {
/* 2206 */         this.ip_set_event = SimpleTimer.addPeriodicEvent("speed handler ip set scheduler", 1000L, new TimerEventPerformer()
/*      */         {
/*      */           private int tick_count;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/* 2218 */             this.tick_count += 1;
/*      */             
/* 2220 */             synchronized (SpeedLimitHandler.this)
/*      */             {
/* 2222 */               for (SpeedLimitHandler.IPSet set : SpeedLimitHandler.this.current_ip_sets.values())
/*      */               {
/* 2224 */                 SpeedLimitHandler.IPSet.access$5200(set, this.tick_count);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
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
/* 2245 */       this.current_dml = new DML(download_manager, has_cats_or_tags, null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class DML
/*      */     implements DownloadManagerListener
/*      */   {
/* 2253 */     private final Object lock = SpeedLimitHandler.this;
/*      */     
/*      */     private final org.gudy.azureus2.plugins.download.DownloadManager download_manager;
/*      */     
/*      */     private final boolean has_cats_or_tags;
/* 2258 */     final List<Runnable> listener_removers = new ArrayList();
/*      */     
/*      */ 
/*      */     private volatile boolean destroyed;
/*      */     
/*      */ 
/*      */ 
/*      */     private DML(org.gudy.azureus2.plugins.download.DownloadManager _download_manager, boolean _has_cats_or_tags)
/*      */     {
/* 2267 */       this.download_manager = _download_manager;
/* 2268 */       this.has_cats_or_tags = _has_cats_or_tags;
/*      */       
/* 2270 */       this.download_manager.addListener(this, true);
/*      */     }
/*      */     
/*      */ 
/*      */     private void destroy()
/*      */     {
/* 2276 */       synchronized (this.lock)
/*      */       {
/* 2278 */         this.destroyed = true;
/*      */         
/* 2280 */         this.download_manager.removeListener(this);
/*      */         
/* 2282 */         for (Runnable r : this.listener_removers) {
/*      */           try
/*      */           {
/* 2285 */             r.run();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2289 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */         
/* 2293 */         this.listener_removers.clear();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void downloadAdded(final Download download)
/*      */     {
/* 2301 */       synchronized (this.lock)
/*      */       {
/* 2303 */         if (this.destroyed)
/*      */         {
/* 2305 */           return;
/*      */         }
/*      */         
/* 2308 */         if (this.has_cats_or_tags)
/*      */         {
/*      */ 
/*      */ 
/* 2312 */           final DownloadAttributeListener attr_listener = new DownloadAttributeListener()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void attributeEventOccurred(Download download, TorrentAttribute attribute, int event_type)
/*      */             {
/*      */ 
/*      */ 
/* 2321 */               SpeedLimitHandler.this.checkIPSets();
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 2327 */           };
/* 2328 */           final TagType tt = TagManagerFactory.getTagManager().getTagType(3);
/*      */           
/* 2330 */           final org.gudy.azureus2.core3.download.DownloadManager core_download = PluginCoreUtils.unwrap(download);
/*      */           
/* 2332 */           final TagListener tag_listener = new TagListener()
/*      */           {
/*      */             public void taggableSync(Tag tag) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void taggableRemoved(Tag tag, Taggable tagged)
/*      */             {
/* 2346 */               SpeedLimitHandler.this.checkIPSets();
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void taggableAdded(Tag tag, Taggable tagged)
/*      */             {
/* 2354 */               SpeedLimitHandler.this.checkIPSets();
/*      */             }
/*      */             
/*      */ 
/* 2358 */           };
/* 2359 */           download.addAttributeListener(attr_listener, SpeedLimitHandler.this.category_attribute, 1);
/*      */           
/* 2361 */           tt.addTagListener(core_download, tag_listener);
/*      */           
/* 2363 */           this.listener_removers.add(new Runnable()
/*      */           {
/*      */             public void run() {
/* 2366 */               download.removeAttributeListener(attr_listener, SpeedLimitHandler.this.category_attribute, 1);
/*      */               
/* 2368 */               tt.removeTagListener(core_download, tag_listener);
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/* 2374 */         final DownloadPeerListener peer_listener = new DownloadPeerListener()
/*      */         {
/*      */           private Runnable pm_listener_remover;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void peerManagerAdded(final Download download, final PeerManager peer_manager)
/*      */           {
/* 2385 */             synchronized (SpeedLimitHandler.DML.this.lock)
/*      */             {
/* 2387 */               if (SpeedLimitHandler.DML.this.destroyed)
/*      */               {
/* 2389 */                 return;
/*      */               }
/*      */               
/* 2392 */               final PeerManagerListener2 listener = new PeerManagerListener2()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void eventOccurred(PeerManagerEvent event)
/*      */                 {
/*      */ 
/* 2399 */                   if (SpeedLimitHandler.DML.this.destroyed)
/*      */                   {
/* 2401 */                     return;
/*      */                   }
/*      */                   
/* 2404 */                   if (event.getType() == 1)
/*      */                   {
/* 2406 */                     SpeedLimitHandler.this.peersAdded(download, peer_manager, new Peer[] { event.getPeer() });
/*      */                   }
/* 2408 */                   else if (event.getType() == 2)
/*      */                   {
/* 2410 */                     SpeedLimitHandler.this.peerRemoved(download, peer_manager, event.getPeer());
/*      */                   }
/*      */                   
/*      */                 }
/* 2414 */               };
/* 2415 */               peer_manager.addListener(listener);
/*      */               
/* 2417 */               this.pm_listener_remover = new Runnable() {
/*      */                 public void run() {
/* 2419 */                   peer_manager.removeListener(listener);
/*      */                 }
/* 2421 */               };
/* 2422 */               SpeedLimitHandler.DML.this.listener_removers.add(this.pm_listener_remover);
/*      */             }
/*      */             
/* 2425 */             Peer[] peers = peer_manager.getPeers();
/*      */             
/* 2427 */             SpeedLimitHandler.this.peersAdded(download, peer_manager, peers);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void peerManagerRemoved(Download download, PeerManager peer_manager)
/*      */           {
/* 2435 */             synchronized (SpeedLimitHandler.DML.this.lock)
/*      */             {
/* 2437 */               if ((this.pm_listener_remover != null) && (SpeedLimitHandler.DML.this.listener_removers.contains(this.pm_listener_remover)))
/*      */               {
/* 2439 */                 this.pm_listener_remover.run();
/*      */                 
/* 2441 */                 SpeedLimitHandler.DML.this.listener_removers.remove(this.pm_listener_remover);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/* 2446 */         };
/* 2447 */         download.addPeerListener(peer_listener);
/*      */         
/* 2449 */         this.listener_removers.add(new Runnable() {
/*      */           public void run() {
/* 2451 */             download.removePeerListener(peer_listener);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void downloadRemoved(Download download) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void peersAdded(Download download, PeerManager peer_manager, Peer[] peers)
/*      */   {
/* 2476 */     boolean has_ccs = false;
/* 2477 */     boolean has_nets = false;
/*      */     
/* 2479 */     Set<String> category_or_tags = null;
/*      */     
/* 2481 */     TagManager tm = TagManagerFactory.getTagManager();
/*      */     IPSet[] sets;
/* 2483 */     long[][][] set_ranges; Set[] set_ccs; Set[] set_nets; int pos; synchronized (this)
/*      */     {
/* 2485 */       int len = this.current_ip_sets.size();
/*      */       
/* 2487 */       sets = new IPSet[len];
/* 2488 */       set_ranges = new long[len][][];
/* 2489 */       set_ccs = new Set[len];
/* 2490 */       set_nets = new Set[len];
/*      */       
/* 2492 */       pos = 0;
/*      */       
/* 2494 */       for (IPSet set : this.current_ip_sets.values())
/*      */       {
/* 2496 */         sets[pos] = set;
/* 2497 */         set_ranges[pos] = set.getRanges();
/* 2498 */         set_ccs[pos] = set.getCountryCodes();
/* 2499 */         set_nets[pos] = set.getNetworks();
/*      */         
/* 2501 */         if (set_ccs[pos].size() > 0)
/*      */         {
/* 2503 */           has_ccs = true;
/*      */         }
/*      */         
/* 2506 */         if (set_nets[pos].size() > 0)
/*      */         {
/* 2508 */           has_nets = true;
/*      */         }
/*      */         
/* 2511 */         pos++;
/*      */         
/* 2513 */         if ((category_or_tags == null) && (set.getCategoriesOrTags() != null))
/*      */         {
/* 2515 */           category_or_tags = new HashSet();
/*      */           
/* 2517 */           String cat = download.getAttribute(this.category_attribute);
/*      */           
/* 2519 */           if ((cat != null) && (cat.length() > 0))
/*      */           {
/* 2521 */             category_or_tags.add(cat);
/*      */           }
/*      */           
/* 2524 */           List<Tag> tags = tm.getTagsForTaggable(3, PluginCoreUtils.unwrap(download));
/*      */           
/* 2526 */           for (Tag t : tags)
/*      */           {
/* 2528 */             category_or_tags.add(t.getTagName(true));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2534 */     if (sets.length == 0)
/*      */     {
/* 2536 */       return;
/*      */     }
/*      */     
/* 2539 */     for (Peer peer : peers)
/*      */     {
/*      */       List<RateLimiter> rlu_tbr;
/*      */       
/*      */       List<RateLimiter> rld_tbr;
/* 2544 */       synchronized (RL_TO_BE_REMOVED_LOCK)
/*      */       {
/* 2546 */         rlu_tbr = (List)peer.getUserData(RLU_TO_BE_REMOVED_KEY);
/* 2547 */         rld_tbr = (List)peer.getUserData(RLD_TO_BE_REMOVED_KEY);
/*      */         
/* 2549 */         if (rlu_tbr != null) {
/* 2550 */           peer.setUserData(RLU_TO_BE_REMOVED_KEY, null);
/*      */         }
/* 2552 */         if (rld_tbr != null) {
/* 2553 */           peer.setUserData(RLD_TO_BE_REMOVED_KEY, null);
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 2558 */         long[] entry = (long[])peer.getUserData(ip_set_peer_key);
/*      */         
/*      */         long l_address;
/*      */         
/* 2562 */         if (entry == null)
/*      */         {
/* 2564 */           long l_address = 0L;
/*      */           
/* 2566 */           String ip = peer.getIp();
/*      */           
/* 2568 */           if (!ip.contains(":"))
/*      */           {
/* 2570 */             byte[] bytes = HostNameToIPResolver.hostAddressToBytes(ip);
/*      */             
/* 2572 */             if (bytes != null)
/*      */             {
/* 2574 */               l_address = (bytes[0] << 24 & 0xFF000000 | bytes[1] << 16 & 0xFF0000 | bytes[2] << 8 & 0xFF00 | bytes[3] & 0xFF) & 0xFFFFFFFF;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 2579 */           entry = new long[] { l_address };
/*      */           
/* 2581 */           peer.setUserData(ip_set_peer_key, entry);
/*      */         }
/*      */         else
/*      */         {
/* 2585 */           l_address = entry[0];
/*      */         }
/*      */         
/* 2588 */         String peer_cc = null;
/* 2589 */         String peer_net = null;
/*      */         
/* 2591 */         if (has_ccs)
/*      */         {
/* 2593 */           String[] details = PeerUtils.getCountryDetails(peer);
/*      */           
/* 2595 */           if ((details != null) && (details.length > 0))
/*      */           {
/* 2597 */             peer_cc = details[0];
/*      */           }
/*      */         }
/*      */         
/* 2601 */         if (has_nets)
/*      */         {
/* 2603 */           peer_net = AENetworkClassifier.categoriseAddress(peer.getIp());
/*      */         }
/*      */         
/* 2606 */         Set<IPSet> added_to_sets = new HashSet();
/*      */         
/* 2608 */         if (l_address != 0L)
/*      */         {
/* 2610 */           for (int i = 0; i < set_ranges.length; i++)
/*      */           {
/* 2612 */             long[][] ranges = set_ranges[i];
/*      */             
/* 2614 */             if (ranges.length != 0)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 2619 */               IPSet set = sets[i];
/*      */               
/* 2621 */               boolean is_inverse = set.isInverse();
/*      */               
/* 2623 */               Set<String> set_cats_or_tags = set.getCategoriesOrTags();
/*      */               
/* 2625 */               if ((set_cats_or_tags == null) || (new HashSet(set_cats_or_tags).removeAll(category_or_tags)))
/*      */               {
/* 2627 */                 boolean hit = false;
/*      */                 
/* 2629 */                 for (long[] range : ranges)
/*      */                 {
/* 2631 */                   if ((l_address >= range[0]) && (l_address <= range[1]))
/*      */                   {
/* 2633 */                     hit = true;
/*      */                     
/* 2635 */                     if (is_inverse)
/*      */                       break;
/* 2637 */                     addLimiters(peer_manager, peer, set, rlu_tbr, rld_tbr);
/*      */                     
/* 2639 */                     added_to_sets.add(set); break;
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/* 2646 */                 if ((is_inverse) && (!hit))
/*      */                 {
/* 2648 */                   addLimiters(peer_manager, peer, set, rlu_tbr, rld_tbr);
/*      */                   
/* 2650 */                   added_to_sets.add(set);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2656 */         if (peer_cc != null)
/*      */         {
/* 2658 */           for (int i = 0; i < set_ccs.length; i++)
/*      */           {
/* 2660 */             IPSet set = sets[i];
/*      */             
/* 2662 */             if (!added_to_sets.contains(set))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 2667 */               Set<String> ccs = set_ccs[i];
/*      */               
/* 2669 */               if (ccs.size() != 0)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 2674 */                 boolean not_inverse = !set.isInverse();
/*      */                 
/* 2676 */                 Set<String> set_cats_or_tags = set.getCategoriesOrTags();
/*      */                 
/* 2678 */                 if ((set_cats_or_tags == null) || (new HashSet(set_cats_or_tags).removeAll(category_or_tags)))
/*      */                 {
/* 2680 */                   boolean hit = ccs.contains(peer_cc);
/*      */                   
/* 2682 */                   if (hit == not_inverse)
/*      */                   {
/* 2684 */                     addLimiters(peer_manager, peer, set, rlu_tbr, rld_tbr);
/*      */                     
/* 2686 */                     added_to_sets.add(set);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           } }
/* 2692 */         if (peer_net != null)
/*      */         {
/* 2694 */           String pub_peer_net = null;
/* 2695 */           String pub_lan = null;
/*      */           
/* 2697 */           if (peer_net == "Public")
/*      */           {
/*      */             try {
/* 2700 */               byte[] address = InetAddress.getByName(peer.getIp()).getAddress();
/*      */               
/* 2702 */               pub_peer_net = address.length == 4 ? "IPv4" : "IPv6";
/*      */             }
/*      */             catch (Throwable e) {}
/*      */             
/*      */ 
/*      */ 
/* 2708 */             if (peer.isLANLocal())
/*      */             {
/* 2710 */               pub_lan = "LAN";
/*      */             }
/*      */             else
/*      */             {
/* 2714 */               pub_lan = "WAN";
/*      */             }
/*      */           }
/*      */           
/* 2718 */           for (int i = 0; i < set_nets.length; i++)
/*      */           {
/* 2720 */             IPSet set = sets[i];
/*      */             
/* 2722 */             if (!added_to_sets.contains(set))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 2727 */               Set<String> nets = set_nets[i];
/*      */               
/* 2729 */               if (nets.size() != 0)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 2734 */                 boolean not_inverse = !set.isInverse();
/*      */                 
/* 2736 */                 Set<String> set_cats_or_tags = set.getCategoriesOrTags();
/*      */                 
/* 2738 */                 if ((set_cats_or_tags == null) || (new HashSet(set_cats_or_tags).removeAll(category_or_tags)))
/*      */                 {
/* 2740 */                   boolean hit = nets.contains(peer_net);
/*      */                   
/* 2742 */                   if (!hit)
/*      */                   {
/* 2744 */                     if (pub_peer_net != null)
/*      */                     {
/* 2746 */                       hit = nets.contains(pub_peer_net);
/*      */                     }
/*      */                     
/* 2749 */                     if ((!hit) && (pub_lan != null))
/*      */                     {
/* 2751 */                       hit = nets.contains(pub_lan);
/*      */                     }
/*      */                   }
/*      */                   
/* 2755 */                   if (hit == not_inverse)
/*      */                   {
/* 2757 */                     addLimiters(peer_manager, peer, set, rlu_tbr, rld_tbr);
/*      */                     
/* 2759 */                     added_to_sets.add(set);
/*      */                   }
/*      */                 }
/*      */               } } } } } finally { Iterator i$;
/*      */         RateLimiter l;
/*      */         Iterator i$;
/*      */         RateLimiter l;
/* 2766 */         if (rlu_tbr != null)
/*      */         {
/* 2768 */           for (RateLimiter l : rlu_tbr)
/*      */           {
/* 2770 */             peer.removeRateLimiter(l, true);
/*      */           }
/*      */         }
/*      */         
/* 2774 */         if (rld_tbr != null)
/*      */         {
/* 2776 */           for (RateLimiter l : rld_tbr)
/*      */           {
/* 2778 */             peer.removeRateLimiter(l, false);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void peerRemoved(Download download, PeerManager peer_manager, Peer peer)
/*      */   {
/*      */     Collection<IPSet> sets;
/*      */     
/*      */ 
/* 2793 */     synchronized (this)
/*      */     {
/* 2795 */       if (this.current_ip_sets.size() == 0)
/*      */       {
/* 2797 */         return;
/*      */       }
/*      */       
/* 2800 */       sets = this.current_ip_sets.values();
/*      */     }
/*      */     
/* 2803 */     for (IPSet s : sets)
/*      */     {
/* 2805 */       s.removePeer(peer_manager, peer);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addLimiters(PeerManager peer_manager, Peer peer, IPSet set, List<RateLimiter> up_to_be_removed, List<RateLimiter> down_to_be_removed)
/*      */   {
/* 2817 */     boolean matched = false;
/*      */     
/*      */ 
/* 2820 */     RateLimiter l = set.getUpLimiter();
/*      */     
/* 2822 */     RateLimiter[] existing = peer.getRateLimiters(true);
/*      */     
/* 2824 */     boolean found = false;
/*      */     
/* 2826 */     for (RateLimiter e : existing)
/*      */     {
/* 2828 */       if (e == l)
/*      */       {
/* 2830 */         found = true;
/*      */         
/* 2832 */         break;
/*      */       }
/*      */     }
/*      */     
/* 2836 */     if (found)
/*      */     {
/* 2838 */       if ((up_to_be_removed != null) && (up_to_be_removed.remove(l)))
/*      */       {
/*      */ 
/*      */ 
/* 2842 */         matched = true;
/*      */       }
/*      */     }
/*      */     else {
/* 2846 */       peer.addRateLimiter(l, true);
/*      */       
/* 2848 */       matched = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2853 */     RateLimiter l = set.getDownLimiter();
/*      */     
/* 2855 */     RateLimiter[] existing = peer.getRateLimiters(false);
/*      */     
/* 2857 */     boolean found = false;
/*      */     
/* 2859 */     for (RateLimiter e : existing)
/*      */     {
/* 2861 */       if (e == l)
/*      */       {
/* 2863 */         found = true;
/*      */         
/* 2865 */         break;
/*      */       }
/*      */     }
/*      */     
/* 2869 */     if (found)
/*      */     {
/* 2871 */       if ((down_to_be_removed != null) && (down_to_be_removed.remove(l)))
/*      */       {
/* 2873 */         matched = true;
/*      */       }
/*      */     }
/*      */     else {
/* 2877 */       peer.addRateLimiter(l, false);
/*      */       
/* 2879 */       matched = true;
/*      */     }
/*      */     
/*      */ 
/* 2883 */     if (matched)
/*      */     {
/* 2885 */       set.addPeer(peer_manager, peer);
/*      */     }
/*      */     
/* 2888 */     int peer_up = set.getPeerUpLimit();
/*      */     
/* 2890 */     if (peer_up > 0)
/*      */     {
/* 2892 */       peer.getStats().setUploadRateLimit(peer_up);
/*      */     }
/*      */     
/* 2895 */     int peer_down = set.getPeerDownLimit();
/*      */     
/* 2897 */     if (peer_down > 0)
/*      */     {
/* 2899 */       peer.getStats().setDownloadRateLimit(peer_down);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkPrioritisers()
/*      */   {
/*      */     List<Prioritiser> prioritisers;
/*      */     
/* 2908 */     synchronized (this)
/*      */     {
/* 2910 */       prioritisers = new ArrayList(this.current_prioritisers);
/*      */     }
/*      */     
/* 2913 */     synchronized (this.extensions_lock)
/*      */     {
/* 2915 */       for (Prioritiser p : prioritisers)
/*      */       {
/* 2917 */         p.check();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private ScheduleRule getActiveRule(Date date)
/*      */   {
/* 2926 */     Calendar cal = new GregorianCalendar();
/*      */     
/* 2928 */     cal.setTime(date);
/*      */     
/* 2930 */     int day_of_week = cal.get(7);
/* 2931 */     int hour_of_day = cal.get(11);
/* 2932 */     int min_of_hour = cal.get(12);
/*      */     
/* 2934 */     int day = -1;
/*      */     
/* 2936 */     switch (day_of_week) {
/*      */     case 2: 
/* 2938 */       day = 1;
/* 2939 */       break;
/*      */     case 3: 
/* 2941 */       day = 2;
/* 2942 */       break;
/*      */     case 4: 
/* 2944 */       day = 4;
/* 2945 */       break;
/*      */     case 5: 
/* 2947 */       day = 8;
/* 2948 */       break;
/*      */     case 6: 
/* 2950 */       day = 16;
/* 2951 */       break;
/*      */     case 7: 
/* 2953 */       day = 32;
/* 2954 */       break;
/*      */     case 1: 
/* 2956 */       day = 64;
/*      */     }
/*      */     
/*      */     
/* 2960 */     int min_of_day = hour_of_day * 60 + min_of_hour;
/*      */     
/* 2962 */     ScheduleRule latest_match = null;
/*      */     
/* 2964 */     for (Iterator i$ = this.current_rules.iterator(); i$.hasNext();) { main_rule = (ScheduleRule)i$.next();
/*      */       
/* 2966 */       List<ScheduleRule> sub_rules = main_rule.splitByDay();
/*      */       
/* 2968 */       for (ScheduleRule rule : sub_rules)
/*      */       {
/* 2970 */         if ((rule.frequency & day) != 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2975 */           if ((rule.from_mins <= min_of_day) && (rule.to_mins >= min_of_day))
/*      */           {
/*      */ 
/* 2978 */             latest_match = main_rule; }
/*      */         }
/*      */       }
/*      */     }
/*      */     ScheduleRule main_rule;
/* 2983 */     return latest_match;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void checkSchedule(int tick_count)
/*      */   {
/* 2990 */     GlobalManager gm = this.core.getGlobalManager();
/*      */     
/*      */     ScheduleRule current_rule;
/*      */     
/* 2994 */     synchronized (this)
/*      */     {
/* 2996 */       current_rule = this.active_rule;
/*      */       
/* 2998 */       ScheduleRule latest_match = getActiveRule(new Date());
/*      */       
/* 3000 */       if (latest_match == null)
/*      */       {
/* 3002 */         this.active_rule = null;
/*      */         
/* 3004 */         if (current_rule != null)
/*      */         {
/* 3006 */           resetRules();
/*      */         }
/*      */       }
/*      */       else {
/* 3010 */         String profile_name = latest_match.profile_name;
/*      */         
/* 3012 */         boolean is_rule_pause_all = false;
/*      */         
/* 3014 */         if ((this.active_rule == null) || (!this.active_rule.sameAs(latest_match)))
/*      */         {
/* 3016 */           String lc_profile_name = profile_name.toLowerCase();
/*      */           
/* 3018 */           if (this.predefined_profile_names.contains(lc_profile_name))
/*      */           {
/* 3020 */             if (lc_profile_name.equals("pause_all"))
/*      */             {
/* 3022 */               this.active_rule = latest_match;
/*      */               
/* 3024 */               is_rule_pause_all = true;
/*      */               
/* 3026 */               setRulePauseAllActive(true);
/*      */             }
/* 3028 */             else if (lc_profile_name.equals("resume_all"))
/*      */             {
/* 3030 */               this.active_rule = latest_match;
/*      */               
/* 3032 */               setRulePauseAllActive(false);
/*      */             }
/* 3034 */             else if (lc_profile_name.equals("null"))
/*      */             {
/* 3036 */               this.active_rule = latest_match;
/*      */             }
/*      */             else
/*      */             {
/* 3040 */               Debug.out("Unknown pre-def name '" + profile_name + "'");
/*      */             }
/*      */           }
/* 3043 */           else if (profileExists(profile_name))
/*      */           {
/* 3045 */             this.active_rule = latest_match;
/*      */             
/* 3047 */             loadProfile(profile_name);
/*      */           }
/* 3049 */           else if (this.active_rule != null)
/*      */           {
/* 3051 */             this.active_rule = null;
/*      */             
/* 3053 */             resetRules();
/*      */           }
/*      */         }
/*      */         else {
/* 3057 */           this.active_rule = latest_match;
/*      */           
/* 3059 */           is_rule_pause_all = this.rule_pause_all_active;
/*      */         }
/*      */         
/* 3062 */         if (this.rule_pause_all_active)
/*      */         {
/* 3064 */           if (!is_rule_pause_all)
/*      */           {
/* 3066 */             setRulePauseAllActive(false);
/*      */ 
/*      */ 
/*      */           }
/* 3070 */           else if (gm.canPauseDownloads())
/*      */           {
/* 3072 */             gm.pauseDownloads();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 3081 */     synchronized (this.extensions_lock)
/*      */     {
/* 3083 */       this.prioritiser_enabled = true;
/*      */       
/* 3085 */       for (List<NetLimit> l : this.net_limits.values())
/*      */       {
/* 3087 */         for (NetLimit n : l)
/*      */         {
/* 3089 */           n.setEnabled(true);
/*      */         }
/*      */       }
/*      */       
/* 3093 */       if (this.active_rule != null)
/*      */       {
/* 3095 */         this.active_rule.checkExtensions();
/*      */       }
/*      */     }
/*      */     
/* 3099 */     if (this.net_limits.size() > 0)
/*      */     {
/* 3101 */       checkTagNetLimits(tick_count);
/*      */     }
/*      */     
/* 3104 */     if (((current_rule != this.active_rule) && (this.net_limits.size() > 0)) || (this.net_limit_pause_all_active))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3112 */       updated(StatsFactory.getLongTermStats());
/*      */     }
/*      */     
/* 3115 */     if (this.net_limit_pause_all_active)
/*      */     {
/* 3117 */       if (gm.canPauseDownloads())
/*      */       {
/* 3119 */         gm.pauseDownloads();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public List<String> getSchedule()
/*      */   {
/* 3127 */     List<String> result = new ArrayList();
/*      */     
/* 3129 */     result.add("# Enter rules on separate lines below this section - see http://wiki.vuze.com/w/Speed_Limit_Scheduler for more details");
/* 3130 */     result.add("# Rules are of the following types:");
/* 3131 */     result.add("#    enable=(yes|no)   - controls whether the entire schedule is enabled or not (default=yes)");
/* 3132 */     result.add("#    <frequency> <profile_name> from <time> to <time> [extension]*");
/* 3133 */     result.add("#        frequency: daily|weekdays|weekends|<day_of_week>");
/* 3134 */     result.add("#            day_of_week: mon|tue|wed|thu|fri|sat|sun");
/* 3135 */     result.add("#        time: hh:mm - 24 hour clock; 00:00=midnight; local time");
/* 3136 */     result.add("#        extension: (start_tag|stop_tag|pause_tag|resume_tag):<tag_name> (enable_priority|disable_priority)");
/* 3137 */     result.add("#    peer_set <set_name>=[<CIDR_specs...>|CC list|Network List|<prior_set_name>] [,inverse=[yes|no]] [,up=<limit>] [,down=<limit>] [peer_up=<limit>] [peer_down=<limit>] [,cat=<cat names>] [,tag=<tag names>]");
/* 3138 */     result.add("#    net_limit (hourly|daily|weekly|monthly)[(:<profile>|$<tag>)] [total=<limit>] [up=<limit>] [down=<limit>]");
/* 3139 */     result.add("#    priority_(up|down) <id>=<tag_name> [,<id>=<tag_name>]+ [,freq=<secs>] [,max=<limit>] [,probe=<cycles>]");
/* 3140 */     result.add("#");
/* 3141 */     result.add("# For example - assuming there are profiles called 'no_limits' and 'limited_upload' defined:");
/* 3142 */     result.add("#");
/* 3143 */     result.add("#     daily no_limits from 00:00 to 23:59");
/* 3144 */     result.add("#     daily limited_upload from 06:00 to 22:00 stop_tag:bigstuff");
/* 3145 */     result.add("#     daily pause_all from 08:00 to 17:00");
/* 3146 */     result.add("#");
/* 3147 */     result.add("#     net_limit monthly total=250G          // flat montly limit");
/* 3148 */     result.add("#");
/* 3149 */     result.add("#     net_limit monthly:no_limits                  // no monthly limit when no_limits active");
/* 3150 */     result.add("#     net_limit monthly:limited_upload total=100G  // 100G a month limit when limited_upload active");
/* 3151 */     result.add("#");
/* 3152 */     result.add("#     peer_set external=211.34.128.0/19 211.35.128.0/17");
/* 3153 */     result.add("#     peer_set Europe=EU;AD;AL;AT;BA;BE;BG;BY;CH;CS;CZ;DE;DK;EE;ES;FI;FO;FR;FX;GB;GI;GR;HR;HU;IE;IS;IT;LI;LT;LU;LV;MC;MD;MK;MT;NL;NO;PL;PT;RO;SE;SI;SJ;SK;SM;UA;VA");
/* 3154 */     result.add("#     peer_set Blorp=Europe;US");
/* 3155 */     result.add("#");
/* 3156 */     result.add("# When multiple rules apply the one further down the list of rules take precedence");
/* 3157 */     result.add("# Currently peer_set limits are not schedulable");
/* 3158 */     result.add("# Comment lines are prefixed with '#'");
/* 3159 */     result.add("# Pre-defined profiles: " + this.predefined_profile_names);
/*      */     
/*      */ 
/* 3162 */     List<String> profiles = getProfileNames();
/*      */     
/* 3164 */     if (profiles.size() == 0)
/*      */     {
/* 3166 */       result.add("# No user profiles currently defined.");
/*      */     }
/*      */     else
/*      */     {
/* 3170 */       String str = "";
/*      */       
/* 3172 */       for (String s : profiles) {
/* 3173 */         str = str + (str.length() == 0 ? "" : ", ") + s;
/*      */       }
/*      */       
/* 3176 */       result.add("# Current profiles details:");
/* 3177 */       result.add("#     defined: " + str);
/*      */       
/*      */       ScheduleRule current_rule;
/*      */       
/* 3181 */       synchronized (this)
/*      */       {
/* 3183 */         current_rule = this.active_rule;
/*      */       }
/*      */       
/* 3186 */       result.add("#     active: " + (current_rule == null ? "none" : current_rule.profile_name));
/*      */     }
/*      */     
/* 3189 */     result.add("# ---- Do not edit this line or any text above! ----");
/*      */     
/* 3191 */     List lines_list = COConfigurationManager.getListParameter("speed.limit.handler.schedule.lines", new ArrayList());
/*      */     
/* 3193 */     List<String> schedule_lines = BDecoder.decodeStrings(BEncoder.cloneList(lines_list));
/*      */     
/* 3195 */     if (schedule_lines.size() == 0)
/*      */     {
/* 3197 */       schedule_lines.add("");
/* 3198 */       schedule_lines.add("");
/*      */     }
/*      */     else
/*      */     {
/* 3202 */       for (String l : schedule_lines)
/*      */       {
/* 3204 */         result.add(l.trim());
/*      */       }
/*      */     }
/*      */     
/* 3208 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<String> setSchedule(List<String> lines)
/*      */   {
/* 3215 */     int trim_from = 0;
/*      */     
/* 3217 */     for (int i = 0; i < lines.size(); i++)
/*      */     {
/* 3219 */       String line = (String)lines.get(i);
/*      */       
/* 3221 */       if (line.startsWith("# ---- Do not edit"))
/*      */       {
/* 3223 */         trim_from = i + 1;
/*      */       }
/*      */     }
/*      */     
/* 3227 */     if (trim_from > 0)
/*      */     {
/* 3229 */       lines = lines.subList(trim_from, lines.size());
/*      */     }
/*      */     
/* 3232 */     COConfigurationManager.setParameter("speed.limit.handler.schedule.lines", lines);
/*      */     
/* 3234 */     COConfigurationManager.save();
/*      */     
/* 3236 */     return loadSchedule();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private List<LimitedRateGroup> trim(LimitedRateGroup[] groups)
/*      */   {
/* 3243 */     List<LimitedRateGroup> result = new ArrayList();
/*      */     
/* 3245 */     for (LimitedRateGroup group : groups)
/*      */     {
/* 3247 */       if ((group instanceof UtilitiesImpl.PluginLimitedRateGroup))
/*      */       {
/* 3249 */         result.add(group);
/*      */       }
/*      */     }
/*      */     
/* 3253 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void updated(LongTermStats stats)
/*      */   {
/* 3260 */     boolean exceeded = false;
/*      */     
/* 3262 */     for (Map.Entry<Integer, List<NetLimit>> entry : this.net_limits.entrySet())
/*      */     {
/* 3264 */       int type = ((Integer)entry.getKey()).intValue();
/*      */       
/* 3266 */       for (NetLimit limit : (List)entry.getValue())
/*      */       {
/* 3268 */         LongTermStats net_lts = limit.getLongTermStats();
/*      */         
/* 3270 */         if (net_lts == null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 3275 */           String profile = limit.getProfile();
/*      */           
/* 3277 */           if ((profile == null) || ((this.active_rule != null) && (this.active_rule.profile_name.equals(profile))))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3283 */             long[] usage = getLongTermUsage(stats, type, limit);
/*      */             
/* 3285 */             long total_up = usage[0] + usage[1] + usage[4];
/* 3286 */             long total_do = usage[2] + usage[3] + usage[5];
/*      */             
/* 3288 */             long[] limits = limit.getLimits();
/*      */             
/* 3290 */             if (limits[0] > 0L)
/*      */             {
/* 3292 */               exceeded = total_up + total_do >= limits[0];
/*      */             }
/*      */             
/* 3295 */             if ((limits[1] > 0L) && (!exceeded))
/*      */             {
/* 3297 */               exceeded = total_up >= limits[1];
/*      */             }
/*      */             
/* 3300 */             if ((limits[2] > 0L) && (!exceeded))
/*      */             {
/* 3302 */               exceeded = total_do >= limits[2];
/*      */             }
/*      */             
/* 3305 */             if (exceeded) {
/*      */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3311 */       if (exceeded) {
/*      */         break;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3317 */     if (this.net_limit_pause_all_active != exceeded)
/*      */     {
/* 3319 */       setNetLimitPauseAllActive(exceeded);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void checkTagNetLimits(int tick_count)
/*      */   {
/* 3327 */     boolean do_log = tick_count % 2 == 0;
/*      */     
/* 3329 */     String log_str = "";
/*      */     
/* 3331 */     Map<TagFeatureRunState, List<Object[]>> rs_ops = new HashMap();
/* 3332 */     Map<TagFeatureRateLimit, List<Object[]>> rl_ops = new HashMap();
/*      */     
/* 3334 */     for (Map.Entry<Integer, List<NetLimit>> entry : this.net_limits.entrySet())
/*      */     {
/* 3336 */       type = ((Integer)entry.getKey()).intValue();
/*      */       
/* 3338 */       for (NetLimit limit : (List)entry.getValue())
/*      */       {
/* 3340 */         String name_str = "net_limit";
/*      */         
/* 3342 */         String name = limit.getName();
/*      */         
/* 3344 */         if (name.length() > 0)
/*      */         {
/* 3346 */           name_str = name_str + " " + name;
/*      */         }
/*      */         
/* 3349 */         LongTermStats stats = limit.getLongTermStats();
/*      */         
/* 3351 */         if (stats != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 3356 */           TagFeatureRateLimit tag_rl = limit.getTag();
/*      */           
/* 3358 */           Tag tag = tag_rl.getTag();
/*      */           
/* 3360 */           long[] usage = getLongTermUsage(stats, type, limit);
/*      */           
/* 3362 */           long total_up = usage[0] + usage[1];
/* 3363 */           long total_do = usage[2] + usage[3];
/*      */           
/* 3365 */           boolean enabled = limit.isEnabled();
/*      */           
/* 3367 */           log_str = log_str + (log_str.length() == 0 ? "" : "; ") + (name.length() == 0 ? "" : new StringBuilder().append(name).append(" ").toString()) + tag.getTagName(true) + ": up=" + DisplayFormatters.formatByteCountToKiBEtc(total_up) + ", down=" + DisplayFormatters.formatByteCountToKiBEtc(total_do) + ", enabled=" + enabled;
/*      */           
/*      */ 
/*      */ 
/* 3371 */           long[] limits = limit.getLimits();
/*      */           
/* 3373 */           boolean exceeded_up = false;
/* 3374 */           boolean exceeded_down = false;
/*      */           
/* 3376 */           boolean handled = false;
/*      */           
/* 3378 */           if (enabled)
/*      */           {
/* 3380 */             if (limits[0] > 0L)
/*      */             {
/* 3382 */               exceeded_up = exceeded_down = total_up + total_do >= limits[0] ? 1 : 0;
/*      */             }
/*      */             
/* 3385 */             if ((limits[1] > 0L) && (!exceeded_up))
/*      */             {
/* 3387 */               exceeded_up = total_up >= limits[1];
/*      */             }
/*      */             
/* 3390 */             if ((limits[2] > 0L) && (!exceeded_down))
/*      */             {
/* 3392 */               exceeded_down = total_do >= limits[2];
/*      */             }
/*      */             
/* 3395 */             if ((tag instanceof TagFeatureRunState))
/*      */             {
/* 3397 */               TagFeatureRunState rs = (TagFeatureRunState)tag;
/*      */               
/* 3399 */               if (rs.hasRunStateCapability(2))
/*      */               {
/* 3401 */                 boolean pause = (exceeded_up) && (exceeded_down);
/*      */                 
/* 3403 */                 int op = pause ? 2 : 4;
/*      */                 
/* 3405 */                 List<Object[]> list = (List)rs_ops.get(rs);
/*      */                 
/* 3407 */                 if (list == null)
/*      */                 {
/* 3409 */                   list = new ArrayList();
/*      */                   
/* 3411 */                   rs_ops.put(rs, list);
/*      */                 }
/*      */                 
/* 3414 */                 list.add(new Object[] { name_str + " : " + (pause ? "pausing" : "resuming") + " tag " + tag.getTagName(true), Integer.valueOf(op) });
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/* 3419 */                 handled = pause;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 3424 */           if (!handled)
/*      */           {
/* 3426 */             int target_up = exceeded_up ? -1 : 0;
/* 3427 */             int target_down = exceeded_down ? -1 : 0;
/*      */             
/* 3429 */             List<Object[]> list = (List)rl_ops.get(tag_rl);
/*      */             
/* 3431 */             if (list == null)
/*      */             {
/* 3433 */               list = new ArrayList();
/*      */               
/* 3435 */               rl_ops.put(tag_rl, list);
/*      */             }
/*      */             
/* 3438 */             list.add(new Object[] { name_str + ": setting rates to " + format(target_up) + "/" + format(target_down) + " on tag " + tag.getTagName(true), Integer.valueOf(target_up), Integer.valueOf(target_down) });
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     int type;
/*      */     
/* 3446 */     for (Map.Entry<TagFeatureRunState, List<Object[]>> entry : rs_ops.entrySet())
/*      */     {
/* 3448 */       TagFeatureRunState tag_rs = (TagFeatureRunState)entry.getKey();
/* 3449 */       List<Object[]> details = (List)entry.getValue();
/*      */       
/* 3451 */       int selected_op = 4;
/*      */       
/* 3453 */       String all_str = "";
/*      */       
/* 3455 */       for (Object[] detail : details)
/*      */       {
/* 3457 */         String str = (String)detail[0];
/*      */         
/* 3459 */         all_str = all_str + (all_str.length() == 0 ? "" : ";") + str;
/*      */         
/* 3461 */         int op = ((Integer)detail[1]).intValue();
/*      */         
/* 3463 */         if (op == 2)
/*      */         {
/* 3465 */           selected_op = 2;
/*      */         }
/*      */       }
/*      */       
/* 3469 */       boolean[] result = tag_rs.getPerformableOperations(new int[] { selected_op });
/*      */       
/* 3471 */       if (result[0] != 0)
/*      */       {
/* 3473 */         this.logger.log(all_str);
/*      */         
/* 3475 */         do_log = true;
/*      */         
/* 3477 */         tag_rs.performOperation(selected_op);
/*      */       }
/*      */     }
/*      */     
/* 3481 */     for (Map.Entry<TagFeatureRateLimit, List<Object[]>> entry : rl_ops.entrySet())
/*      */     {
/* 3483 */       TagFeatureRateLimit tag_rl = (TagFeatureRateLimit)entry.getKey();
/* 3484 */       List<Object[]> details = (List)entry.getValue();
/*      */       
/* 3486 */       String all_str = "";
/*      */       
/* 3488 */       int selected_up = 0;
/* 3489 */       int selected_down = 0;
/*      */       
/* 3491 */       for (Object[] detail : details)
/*      */       {
/* 3493 */         String str = (String)detail[0];
/*      */         
/* 3495 */         all_str = all_str + (all_str.length() == 0 ? "" : ";") + str;
/*      */         
/* 3497 */         int up = ((Integer)detail[1]).intValue();
/* 3498 */         int down = ((Integer)detail[2]).intValue();
/*      */         
/* 3500 */         if (up == -1)
/*      */         {
/* 3502 */           selected_up = -1;
/*      */         }
/*      */         
/* 3505 */         if (down == -1)
/*      */         {
/* 3507 */           selected_down = -1;
/*      */         }
/*      */       }
/*      */       
/* 3511 */       int up_lim = tag_rl.getTagUploadLimit();
/* 3512 */       int down_lim = tag_rl.getTagDownloadLimit();
/*      */       
/* 3514 */       if ((up_lim != selected_up) || (down_lim != selected_down))
/*      */       {
/* 3516 */         this.logger.log(all_str);
/*      */         
/* 3518 */         do_log = true;
/*      */         
/* 3520 */         tag_rl.setTagUploadLimit(selected_up);
/*      */         
/* 3522 */         tag_rl.setTagDownloadLimit(selected_down);
/*      */       }
/*      */     }
/*      */     
/* 3526 */     if ((log_str.length() > 0) && (do_log))
/*      */     {
/* 3528 */       this.logger.log("net_limit: current: " + log_str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String formatUp(int rate)
/*      */   {
/* 3536 */     return "Up=" + format(rate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String formatDown(int rate)
/*      */   {
/* 3543 */     return "Down=" + format(rate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String format(int rate)
/*      */   {
/* 3550 */     if (rate < 0)
/*      */     {
/* 3552 */       return "Disabled";
/*      */     }
/* 3554 */     if (rate == 0)
/*      */     {
/* 3556 */       return "Unlimited";
/*      */     }
/*      */     
/*      */ 
/* 3560 */     return DisplayFormatters.formatByteCountToKiBEtcPerSec(rate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String formatUp(List<LimitedRateGroup> groups)
/*      */   {
/* 3568 */     return "Up=" + format(groups);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String formatDown(List<LimitedRateGroup> groups)
/*      */   {
/* 3575 */     return "Down=" + format(groups);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String format(List<LimitedRateGroup> groups)
/*      */   {
/* 3582 */     String str = "";
/*      */     
/* 3584 */     for (LimitedRateGroup group : groups)
/*      */     {
/* 3586 */       str = str + (str.length() == 0 ? "" : ", ") + group.getName() + ":" + format(group.getRateLimitBytesPerSecond());
/*      */     }
/*      */     
/* 3589 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void exportBoolean(Map<String, Object> map, String key, boolean b)
/*      */   {
/* 3598 */     map.put(key, Long.valueOf(b ? 1L : 0L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean importBoolean(Map<String, Object> map, String key)
/*      */   {
/* 3606 */     Long l = (Long)map.get(key);
/*      */     
/* 3608 */     if (l != null)
/*      */     {
/* 3610 */       return l.longValue() == 1L;
/*      */     }
/*      */     
/* 3613 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void exportInt(Map<String, Object> map, String key, int i)
/*      */   {
/* 3622 */     map.put(key, new Long(i));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private int importInt(Map<String, Object> map, String key)
/*      */   {
/* 3630 */     Long l = (Long)map.get(key);
/*      */     
/* 3632 */     if (l != null)
/*      */     {
/* 3634 */       return l.intValue();
/*      */     }
/*      */     
/* 3637 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void exportString(Map<String, Object> map, String key, String s)
/*      */   {
/*      */     try
/*      */     {
/* 3647 */       map.put(key, s.getBytes("UTF-8"));
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String importString(Map<String, Object> map, String key)
/*      */   {
/* 3658 */     Object obj = map.get(key);
/*      */     
/* 3660 */     if ((obj instanceof String))
/*      */     {
/* 3662 */       return (String)obj;
/*      */     }
/* 3664 */     if ((obj instanceof byte[])) {
/*      */       try
/*      */       {
/* 3667 */         return new String((byte[])obj, "UTF-8");
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/* 3673 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void dump(IndentWriter iw)
/*      */   {
/* 3680 */     iw.println("Profiles");
/*      */     
/* 3682 */     iw.indent();
/*      */     try
/*      */     {
/* 3685 */       List<String> profiles = getProfileNames();
/*      */       
/* 3687 */       for (String profile : profiles)
/*      */       {
/* 3689 */         iw.println(profile);
/*      */         
/* 3691 */         iw.indent();
/*      */         try
/*      */         {
/* 3694 */           List<String> p_lines = getProfileSupport(profile, true);
/*      */           
/* 3696 */           for (String line : p_lines)
/*      */           {
/* 3698 */             iw.println(line);
/*      */           }
/*      */           
/*      */         }
/*      */         finally {}
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3707 */       iw.exdent();
/*      */     }
/*      */     
/* 3710 */     iw.println("Schedule");
/*      */     
/* 3712 */     iw.indent();
/*      */     try
/*      */     {
/* 3715 */       List lines_list = COConfigurationManager.getListParameter("speed.limit.handler.schedule.lines", new ArrayList());
/*      */       
/* 3717 */       List<String> schedule_lines = BDecoder.decodeStrings(BEncoder.cloneList(lines_list));
/*      */       
/* 3719 */       for (String line : schedule_lines)
/*      */       {
/* 3721 */         iw.println(line);
/*      */       }
/*      */     }
/*      */     finally {
/* 3725 */       iw.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class LimitDetails
/*      */   {
/*      */     private boolean auto_up_enabled;
/*      */     
/*      */     private boolean auto_up_seeding_enabled;
/*      */     
/*      */     private boolean seeding_limits_enabled;
/*      */     private int up_limit;
/*      */     private int up_seeding_limit;
/*      */     private int down_limit;
/*      */     private boolean lan_rates_enabled;
/*      */     private int lan_up_limit;
/*      */     private int lan_down_limit;
/* 3743 */     private final Map<String, int[]> download_limits = new HashMap();
/* 3744 */     private final Map<String, int[]> category_limits = new HashMap();
/* 3745 */     private final Map<String, int[]> tag_limits = new HashMap();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private LimitDetails() {}
/*      */     
/*      */ 
/*      */ 
/*      */     private LimitDetails()
/*      */     {
/* 3756 */       this.auto_up_enabled = SpeedLimitHandler.this.importBoolean(map, "aue");
/* 3757 */       this.auto_up_seeding_enabled = SpeedLimitHandler.this.importBoolean(map, "ause");
/* 3758 */       this.seeding_limits_enabled = SpeedLimitHandler.this.importBoolean(map, "sle");
/*      */       
/* 3760 */       this.up_limit = SpeedLimitHandler.this.importInt(map, "ul");
/* 3761 */       this.up_seeding_limit = SpeedLimitHandler.this.importInt(map, "usl");
/* 3762 */       this.down_limit = SpeedLimitHandler.this.importInt(map, "dl");
/*      */       
/* 3764 */       if (map.containsKey("lre"))
/*      */       {
/* 3766 */         this.lan_rates_enabled = SpeedLimitHandler.this.importBoolean(map, "lre");
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 3771 */         this.lan_rates_enabled = COConfigurationManager.getBooleanParameter("LAN Speed Enabled");
/*      */       }
/*      */       
/* 3774 */       this.lan_up_limit = SpeedLimitHandler.this.importInt(map, "lul");
/* 3775 */       this.lan_down_limit = SpeedLimitHandler.this.importInt(map, "ldl");
/*      */       
/*      */ 
/* 3778 */       List<Map<String, Object>> d_list = (List)map.get("dms");
/*      */       
/* 3780 */       if (d_list != null)
/*      */       {
/* 3782 */         for (Map<String, Object> m : d_list)
/*      */         {
/* 3784 */           String k = SpeedLimitHandler.this.importString(m, "k");
/*      */           
/* 3786 */           if (k != null)
/*      */           {
/* 3788 */             int ul = SpeedLimitHandler.this.importInt(m, "u");
/* 3789 */             int dl = SpeedLimitHandler.this.importInt(m, "d");
/*      */             
/* 3791 */             this.download_limits.put(k, new int[] { ul, dl });
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 3796 */       List<Map<String, Object>> c_list = (List)map.get("cts");
/*      */       
/* 3798 */       if (c_list != null)
/*      */       {
/* 3800 */         for (Map<String, Object> m : c_list)
/*      */         {
/* 3802 */           String k = SpeedLimitHandler.this.importString(m, "k");
/*      */           
/* 3804 */           if (k != null)
/*      */           {
/* 3806 */             int ul = SpeedLimitHandler.this.importInt(m, "u");
/* 3807 */             int dl = SpeedLimitHandler.this.importInt(m, "d");
/*      */             
/* 3809 */             this.category_limits.put(k, new int[] { ul, dl });
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 3814 */       List<Map<String, Object>> t_list = (List)map.get("tgs");
/*      */       
/* 3816 */       if (t_list != null)
/*      */       {
/* 3818 */         for (Map<String, Object> m : t_list)
/*      */         {
/* 3820 */           String t = SpeedLimitHandler.this.importString(m, "k");
/*      */           
/* 3822 */           if (t != null)
/*      */           {
/* 3824 */             int ul = SpeedLimitHandler.this.importInt(m, "u");
/* 3825 */             int dl = SpeedLimitHandler.this.importInt(m, "d");
/*      */             
/* 3827 */             this.tag_limits.put(t, new int[] { ul, dl });
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private Map<String, Object> export()
/*      */     {
/* 3836 */       Map<String, Object> map = new HashMap();
/*      */       
/* 3838 */       SpeedLimitHandler.this.exportBoolean(map, "aue", this.auto_up_enabled);
/* 3839 */       SpeedLimitHandler.this.exportBoolean(map, "ause", this.auto_up_seeding_enabled);
/* 3840 */       SpeedLimitHandler.this.exportBoolean(map, "sle", this.seeding_limits_enabled);
/*      */       
/* 3842 */       SpeedLimitHandler.this.exportInt(map, "ul", this.up_limit);
/* 3843 */       SpeedLimitHandler.this.exportInt(map, "usl", this.up_seeding_limit);
/* 3844 */       SpeedLimitHandler.this.exportInt(map, "dl", this.down_limit);
/*      */       
/* 3846 */       SpeedLimitHandler.this.exportBoolean(map, "lre", this.lan_rates_enabled);
/* 3847 */       SpeedLimitHandler.this.exportInt(map, "lul", this.lan_up_limit);
/* 3848 */       SpeedLimitHandler.this.exportInt(map, "ldl", this.lan_down_limit);
/*      */       
/*      */ 
/* 3851 */       List<Map<String, Object>> d_list = new ArrayList();
/*      */       
/* 3853 */       map.put("dms", d_list);
/*      */       
/* 3855 */       for (Map.Entry<String, int[]> entry : this.download_limits.entrySet())
/*      */       {
/* 3857 */         Map<String, Object> m = new HashMap();
/*      */         
/* 3859 */         d_list.add(m);
/*      */         
/* 3861 */         SpeedLimitHandler.this.exportString(m, "k", (String)entry.getKey());
/* 3862 */         SpeedLimitHandler.this.exportInt(m, "u", ((int[])entry.getValue())[0]);
/* 3863 */         SpeedLimitHandler.this.exportInt(m, "d", ((int[])entry.getValue())[1]);
/*      */       }
/*      */       
/* 3866 */       List<Map<String, Object>> c_list = new ArrayList();
/*      */       
/* 3868 */       map.put("cts", c_list);
/*      */       
/* 3870 */       for (Map.Entry<String, int[]> entry : this.category_limits.entrySet())
/*      */       {
/* 3872 */         Map<String, Object> m = new HashMap();
/*      */         
/* 3874 */         c_list.add(m);
/*      */         
/* 3876 */         SpeedLimitHandler.this.exportString(m, "k", (String)entry.getKey());
/* 3877 */         SpeedLimitHandler.this.exportInt(m, "u", ((int[])entry.getValue())[0]);
/* 3878 */         SpeedLimitHandler.this.exportInt(m, "d", ((int[])entry.getValue())[1]);
/*      */       }
/*      */       
/* 3881 */       List<Map<String, Object>> t_list = new ArrayList();
/*      */       
/* 3883 */       map.put("tgs", t_list);
/*      */       
/* 3885 */       for (Map.Entry<String, int[]> entry : this.tag_limits.entrySet())
/*      */       {
/* 3887 */         Map<String, Object> m = new HashMap();
/*      */         
/* 3889 */         t_list.add(m);
/*      */         
/* 3891 */         SpeedLimitHandler.this.exportString(m, "k", (String)entry.getKey());
/* 3892 */         SpeedLimitHandler.this.exportInt(m, "u", ((int[])entry.getValue())[0]);
/* 3893 */         SpeedLimitHandler.this.exportInt(m, "d", ((int[])entry.getValue())[1]);
/*      */       }
/*      */       
/* 3896 */       return map;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void loadForReset()
/*      */     {
/* 3904 */       this.auto_up_enabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Enabled");
/*      */     }
/*      */     
/*      */ 
/*      */     private void loadCurrent()
/*      */     {
/* 3910 */       this.auto_up_enabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Enabled");
/* 3911 */       this.auto_up_seeding_enabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Seeding Enabled");
/* 3912 */       this.seeding_limits_enabled = COConfigurationManager.getBooleanParameter("enable.seedingonly.upload.rate");
/* 3913 */       this.up_limit = COConfigurationManager.getIntParameter("Max Upload Speed KBs");
/* 3914 */       this.up_seeding_limit = COConfigurationManager.getIntParameter("Max Upload Speed Seeding KBs");
/* 3915 */       this.down_limit = COConfigurationManager.getIntParameter("Max Download Speed KBs");
/*      */       
/* 3917 */       this.lan_rates_enabled = COConfigurationManager.getBooleanParameter("LAN Speed Enabled");
/* 3918 */       this.lan_up_limit = COConfigurationManager.getIntParameter("Max LAN Upload Speed KBs");
/* 3919 */       this.lan_down_limit = COConfigurationManager.getIntParameter("Max LAN Download Speed KBs");
/*      */       
/* 3921 */       this.download_limits.clear();
/*      */       
/* 3923 */       GlobalManager gm = SpeedLimitHandler.this.core.getGlobalManager();
/*      */       
/* 3925 */       List<org.gudy.azureus2.core3.download.DownloadManager> downloads = gm.getDownloadManagers();
/*      */       
/* 3927 */       for (org.gudy.azureus2.core3.download.DownloadManager download : downloads)
/*      */       {
/* 3929 */         TOTorrent torrent = download.getTorrent();
/*      */         
/* 3931 */         byte[] hash = null;
/*      */         
/* 3933 */         if (torrent != null) {
/*      */           try
/*      */           {
/* 3936 */             hash = torrent.getHash();
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 3943 */         if (hash != null) {
/* 3944 */           int download_up_limit = download.getStats().getUploadRateLimitBytesPerSecond();
/* 3945 */           int download_down_limit = download.getStats().getDownloadRateLimitBytesPerSecond();
/*      */           
/* 3947 */           if ((download_up_limit > 0) || (download_down_limit > 0))
/*      */           {
/* 3949 */             this.download_limits.put(Base32.encode(hash), new int[] { download_up_limit, download_down_limit });
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 3954 */       Category[] categories = CategoryManager.getCategories();
/*      */       
/* 3956 */       this.category_limits.clear();
/*      */       
/* 3958 */       for (Category category : categories)
/*      */       {
/* 3960 */         int cat_up_limit = category.getUploadSpeed();
/* 3961 */         int cat_down_limit = category.getDownloadSpeed();
/*      */         
/* 3963 */         if ((cat_up_limit > 0) || (cat_down_limit > 0))
/*      */         {
/* 3965 */           this.category_limits.put(category.getName(), new int[] { cat_up_limit, cat_down_limit });
/*      */         }
/*      */       }
/*      */       
/* 3969 */       List<TagType> tag_types = TagManagerFactory.getTagManager().getTagTypes();
/*      */       
/* 3971 */       this.tag_limits.clear();
/*      */       
/* 3973 */       for (Iterator i$ = tag_types.iterator(); i$.hasNext();) { tag_type = (TagType)i$.next();
/*      */         
/* 3975 */         if (tag_type.getTagType() != 1)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 3980 */           if (tag_type.hasTagTypeFeature(1L))
/*      */           {
/* 3982 */             List<Tag> tags = tag_type.getTags();
/*      */             
/* 3984 */             for (Tag tag : tags)
/*      */             {
/* 3986 */               TagFeatureRateLimit rl = (TagFeatureRateLimit)tag;
/*      */               
/* 3988 */               int tag_up_limit = rl.getTagUploadLimit();
/* 3989 */               int tag_down_limit = rl.getTagDownloadLimit();
/*      */               
/* 3991 */               if ((tag_up_limit != 0) || (tag_down_limit != 0))
/*      */               {
/* 3993 */                 this.tag_limits.put(tag_type.getTagType() + "." + tag.getTagID(), new int[] { tag_up_limit, tag_down_limit });
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */       TagType tag_type;
/*      */     }
/*      */     
/*      */ 
/*      */     private int[] getLimitsForDownload(String hash)
/*      */     {
/* 4006 */       return (int[])this.download_limits.get(hash);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void addRemoveDownloads(List<String> hashes, boolean add)
/*      */     {
/* 4014 */       GlobalManager gm = SpeedLimitHandler.this.core.getGlobalManager();
/*      */       
/* 4016 */       for (String hash : hashes)
/*      */       {
/* 4018 */         if (add)
/*      */         {
/* 4020 */           org.gudy.azureus2.core3.download.DownloadManager download = gm.getDownloadManager(new HashWrapper(Base32.decode(hash)));
/*      */           
/* 4022 */           if (download != null)
/*      */           {
/* 4024 */             int download_up_limit = download.getStats().getUploadRateLimitBytesPerSecond();
/* 4025 */             int download_down_limit = download.getStats().getDownloadRateLimitBytesPerSecond();
/*      */             
/* 4027 */             if ((download_up_limit > 0) || (download_down_limit > 0))
/*      */             {
/* 4029 */               this.download_limits.put(hash, new int[] { download_up_limit, download_down_limit });
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/* 4034 */           this.download_limits.remove(hash);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void apply()
/*      */     {
/* 4046 */       COConfigurationManager.setParameter("Auto Upload Speed Enabled", this.auto_up_enabled);
/* 4047 */       COConfigurationManager.setParameter("Auto Upload Speed Seeding Enabled", this.auto_up_seeding_enabled);
/*      */       
/* 4049 */       if ((!this.auto_up_enabled) && (!this.auto_up_seeding_enabled))
/*      */       {
/* 4051 */         COConfigurationManager.setParameter("Max Upload Speed KBs", this.up_limit);
/*      */       }
/*      */       
/* 4054 */       COConfigurationManager.setParameter("enable.seedingonly.upload.rate", this.seeding_limits_enabled);
/* 4055 */       COConfigurationManager.setParameter("Max Upload Speed Seeding KBs", this.up_seeding_limit);
/*      */       
/* 4057 */       COConfigurationManager.setParameter("Max Download Speed KBs", this.down_limit);
/*      */       
/* 4059 */       COConfigurationManager.setParameter("LAN Speed Enabled", this.lan_rates_enabled);
/* 4060 */       COConfigurationManager.setParameter("Max LAN Upload Speed KBs", this.lan_up_limit);
/* 4061 */       COConfigurationManager.setParameter("Max LAN Download Speed KBs", this.lan_down_limit);
/*      */       
/* 4063 */       GlobalManager gm = SpeedLimitHandler.this.core.getGlobalManager();
/*      */       
/* 4065 */       Set<org.gudy.azureus2.core3.download.DownloadManager> all_managers = new HashSet(gm.getDownloadManagers());
/*      */       
/* 4067 */       for (Map.Entry<String, int[]> entry : this.download_limits.entrySet())
/*      */       {
/* 4069 */         byte[] hash = Base32.decode((String)entry.getKey());
/*      */         
/* 4071 */         org.gudy.azureus2.core3.download.DownloadManager dm = gm.getDownloadManager(new HashWrapper(hash));
/*      */         
/* 4073 */         if (dm != null)
/*      */         {
/* 4075 */           int[] limits = (int[])entry.getValue();
/*      */           
/* 4077 */           dm.getStats().setUploadRateLimitBytesPerSecond(limits[0]);
/* 4078 */           dm.getStats().setDownloadRateLimitBytesPerSecond(limits[1]);
/*      */           
/* 4080 */           all_managers.remove(dm);
/*      */         }
/*      */       }
/*      */       
/* 4084 */       for (org.gudy.azureus2.core3.download.DownloadManager dm : all_managers)
/*      */       {
/* 4086 */         dm.getStats().setUploadRateLimitBytesPerSecond(0);
/* 4087 */         dm.getStats().setDownloadRateLimitBytesPerSecond(0);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 4092 */       Set<Category> all_categories = new HashSet(Arrays.asList(CategoryManager.getCategories()));
/*      */       
/* 4094 */       Map<String, Category> cat_map = new HashMap();
/*      */       
/* 4096 */       for (Category c : all_categories)
/*      */       {
/* 4098 */         cat_map.put(c.getName(), c);
/*      */       }
/*      */       
/* 4101 */       for (Map.Entry<String, int[]> entry : this.category_limits.entrySet())
/*      */       {
/* 4103 */         String cat_name = (String)entry.getKey();
/*      */         
/* 4105 */         Category category = (Category)cat_map.get(cat_name);
/*      */         
/* 4107 */         if (category != null)
/*      */         {
/* 4109 */           int[] limits = (int[])entry.getValue();
/*      */           
/* 4111 */           category.setUploadSpeed(limits[0]);
/* 4112 */           category.setDownloadSpeed(limits[1]);
/*      */           
/* 4114 */           all_categories.remove(category);
/*      */         }
/*      */       }
/*      */       
/* 4118 */       for (Category category : all_categories)
/*      */       {
/* 4120 */         category.setUploadSpeed(0);
/* 4121 */         category.setDownloadSpeed(0);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 4126 */       TagManager tm = TagManagerFactory.getTagManager();
/*      */       
/* 4128 */       List<TagType> all_tts = tm.getTagTypes();
/*      */       
/* 4130 */       Set<Tag> all_rl_tags = new HashSet();
/*      */       
/* 4132 */       for (TagType tt : all_tts)
/*      */       {
/* 4134 */         if (tt.getTagType() != 1)
/*      */         {
/*      */ 
/*      */ 
/* 4138 */           if (tt.hasTagTypeFeature(1L))
/*      */           {
/* 4140 */             all_rl_tags.addAll(tt.getTags());
/*      */           }
/*      */         }
/*      */       }
/* 4144 */       for (Map.Entry<String, int[]> entry : this.tag_limits.entrySet())
/*      */       {
/* 4146 */         String tag_key = (String)entry.getKey();
/*      */         
/* 4148 */         String[] bits = tag_key.split("\\.");
/*      */         try
/*      */         {
/* 4151 */           int tag_type = Integer.parseInt(bits[0]);
/* 4152 */           int tag_id = Integer.parseInt(bits[1]);
/*      */           
/* 4154 */           TagType tt = tm.getTagType(tag_type);
/*      */           
/* 4156 */           if ((tt == null) || (tt.hasTagTypeFeature(1L)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 4161 */             Tag tag = tt.getTag(tag_id);
/*      */             
/* 4163 */             if (tag != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 4168 */               TagFeatureRateLimit rl = (TagFeatureRateLimit)tag;
/*      */               
/* 4170 */               int[] limits = (int[])entry.getValue();
/*      */               
/* 4172 */               rl.setTagUploadLimit(limits[0]);
/* 4173 */               rl.setTagDownloadLimit(limits[1]);
/*      */               
/* 4175 */               all_rl_tags.remove(tag);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/* 4182 */       for (Tag tag : all_rl_tags) {
/*      */         try
/*      */         {
/* 4185 */           TagFeatureRateLimit rl = (TagFeatureRateLimit)tag;
/*      */           
/* 4187 */           rl.setTagUploadLimit(0);
/* 4188 */           rl.setTagDownloadLimit(0);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private List<String> getString(boolean is_current, boolean use_hashes)
/*      */     {
/* 4201 */       List<String> result = new ArrayList();
/*      */       
/* 4203 */       result.add("Global Limits");
/*      */       
/* 4205 */       if (this.auto_up_enabled)
/*      */       {
/* 4207 */         result.add("    Auto upload limit enabled");
/*      */       }
/* 4209 */       else if (this.auto_up_seeding_enabled)
/*      */       {
/* 4211 */         result.add("    Auto upload seeding limit enabled");
/*      */       }
/*      */       else
/*      */       {
/* 4215 */         result.add("    " + SpeedLimitHandler.this.formatUp(this.up_limit * 1024));
/*      */         
/* 4217 */         if (this.seeding_limits_enabled)
/*      */         {
/* 4219 */           result.add("    Seeding only limit enabled");
/*      */           
/* 4221 */           result.add("    Seeding only: " + SpeedLimitHandler.this.format(this.up_seeding_limit * 1024));
/*      */         }
/*      */       }
/*      */       
/* 4225 */       result.add("    " + SpeedLimitHandler.this.formatDown(this.down_limit * 1024));
/*      */       
/* 4227 */       if (this.lan_rates_enabled)
/*      */       {
/* 4229 */         result.add("");
/* 4230 */         result.add("    LAN limits enabled");
/* 4231 */         result.add("        " + SpeedLimitHandler.this.formatUp(this.lan_up_limit * 1024));
/* 4232 */         result.add("        " + SpeedLimitHandler.this.formatDown(this.lan_down_limit * 1024));
/*      */       }
/*      */       
/* 4235 */       result.add("");
/*      */       
/* 4237 */       result.add("Download Limits");
/*      */       
/* 4239 */       int total_download_limits = 0;
/* 4240 */       int total_download_limits_up = 0;
/* 4241 */       int total_download_limits_up_disabled = 0;
/* 4242 */       int total_download_limits_down = 0;
/* 4243 */       int total_download_limits_down_disabled = 0;
/*      */       
/* 4245 */       GlobalManager gm = SpeedLimitHandler.this.core.getGlobalManager();
/*      */       
/* 4247 */       for (Map.Entry<String, int[]> entry : this.download_limits.entrySet())
/*      */       {
/* 4249 */         String hash_str = (String)entry.getKey();
/*      */         
/* 4251 */         byte[] hash = Base32.decode(hash_str);
/*      */         
/* 4253 */         org.gudy.azureus2.core3.download.DownloadManager dm = gm.getDownloadManager(new HashWrapper(hash));
/*      */         
/* 4255 */         if (dm != null)
/*      */         {
/* 4257 */           int[] limits = (int[])entry.getValue();
/*      */           
/* 4259 */           total_download_limits++;
/*      */           
/* 4261 */           int up = limits[0];
/* 4262 */           int down = limits[1];
/*      */           
/* 4264 */           if (up < 0)
/*      */           {
/* 4266 */             total_download_limits_up_disabled++;
/*      */           }
/*      */           else
/*      */           {
/* 4270 */             total_download_limits_up += up;
/*      */           }
/*      */           
/* 4273 */           if (down < 0)
/*      */           {
/* 4275 */             total_download_limits_down_disabled++;
/*      */           }
/*      */           else
/*      */           {
/* 4279 */             total_download_limits_down += down;
/*      */           }
/*      */           
/* 4282 */           result.add("    " + (use_hashes ? hash_str.substring(0, 16) : dm.getDisplayName()) + ": " + SpeedLimitHandler.this.formatUp(up) + ", " + SpeedLimitHandler.this.formatDown(down));
/*      */         }
/*      */       }
/*      */       
/* 4286 */       if (total_download_limits == 0)
/*      */       {
/* 4288 */         result.add("    None");
/*      */       }
/*      */       else
/*      */       {
/* 4292 */         result.add("    ----");
/*      */         
/* 4294 */         result.add("    Total=" + total_download_limits + " - Compounded limits: " + SpeedLimitHandler.this.formatUp(total_download_limits_up) + (total_download_limits_up_disabled == 0 ? "" : new StringBuilder().append(" [").append(total_download_limits_up_disabled).append(" disabled]").toString()) + ", " + SpeedLimitHandler.this.formatDown(total_download_limits_down) + (total_download_limits_down_disabled == 0 ? "" : new StringBuilder().append(" [").append(total_download_limits_down_disabled).append(" disabled]").toString()));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4302 */       Category[] categories = CategoryManager.getCategories();
/*      */       
/* 4304 */       Map<String, Category> cat_map = new HashMap();
/*      */       
/* 4306 */       for (Category c : categories)
/*      */       {
/* 4308 */         cat_map.put(c.getName(), c);
/*      */       }
/*      */       
/* 4311 */       result.add("");
/*      */       
/* 4313 */       result.add("Category Limits");
/*      */       
/* 4315 */       int total_cat_limits = 0;
/* 4316 */       int total_cat_limits_up = 0;
/* 4317 */       int total_cat_limits_down = 0;
/*      */       
/* 4319 */       Map<String, int[]> sorted_category_limits = new TreeMap(this.category_limits);
/*      */       
/* 4321 */       for (Map.Entry<String, int[]> entry : sorted_category_limits.entrySet())
/*      */       {
/* 4323 */         String cat_name = (String)entry.getKey();
/*      */         
/* 4325 */         Category category = (Category)cat_map.get(cat_name);
/*      */         
/* 4327 */         if (category != null)
/*      */         {
/* 4329 */           if (category.getType() == 2)
/*      */           {
/* 4331 */             cat_name = "Uncategorised";
/*      */           }
/*      */           
/* 4334 */           int[] limits = (int[])entry.getValue();
/*      */           
/* 4336 */           total_cat_limits++;
/*      */           
/* 4338 */           int up = limits[0];
/* 4339 */           int down = limits[1];
/*      */           
/* 4341 */           total_cat_limits_up += up;
/* 4342 */           total_cat_limits_down += down;
/*      */           
/* 4344 */           result.add("    " + cat_name + ": " + SpeedLimitHandler.this.formatUp(up) + ", " + SpeedLimitHandler.this.formatDown(down));
/*      */         }
/*      */       }
/*      */       
/* 4348 */       if (total_cat_limits == 0)
/*      */       {
/* 4350 */         result.add("    None");
/*      */       }
/*      */       else
/*      */       {
/* 4354 */         result.add("    ----");
/*      */         
/* 4356 */         result.add("    Total=" + total_cat_limits + " - Compounded limits: " + SpeedLimitHandler.this.formatUp(total_cat_limits_up) + ", " + SpeedLimitHandler.this.formatDown(total_cat_limits_down));
/*      */       }
/*      */       
/*      */ 
/* 4360 */       result.add("");
/*      */       
/* 4362 */       result.add("Tag Limits");
/*      */       
/* 4364 */       int total_tag_limits = 0;
/* 4365 */       int total_tag_limits_up = 0;
/* 4366 */       int total_tag_limits_down = 0;
/*      */       
/* 4368 */       boolean some_up_disabled = false;
/* 4369 */       boolean some_down_disabled = false;
/*      */       
/* 4371 */       TagManager tm = TagManagerFactory.getTagManager();
/*      */       
/* 4373 */       Map<String, int[]> sorted_tag_limts = new TreeMap(this.tag_limits);
/*      */       
/* 4375 */       for (Map.Entry<String, int[]> entry : sorted_tag_limts.entrySet())
/*      */       {
/* 4377 */         String tag_key = (String)entry.getKey();
/*      */         
/* 4379 */         String[] bits = tag_key.split("\\.");
/*      */         try
/*      */         {
/* 4382 */           int tag_type = Integer.parseInt(bits[0]);
/* 4383 */           int tag_id = Integer.parseInt(bits[1]);
/*      */           
/* 4385 */           TagType tt = tm.getTagType(tag_type);
/*      */           
/* 4387 */           if ((tt == null) || (tt.hasTagTypeFeature(1L)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 4392 */             Tag tag = tt.getTag(tag_id);
/*      */             
/* 4394 */             if (tag != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 4399 */               String tag_name = tt.getTagTypeName(true) + " - " + tag.getTagName(true);
/*      */               
/* 4401 */               int[] limits = (int[])entry.getValue();
/*      */               
/* 4403 */               total_tag_limits++;
/*      */               
/* 4405 */               int up = limits[0];
/* 4406 */               int down = limits[1];
/*      */               
/* 4408 */               if (up > 0) {
/* 4409 */                 total_tag_limits_up += up;
/* 4410 */               } else if (up < 0) {
/* 4411 */                 some_up_disabled = true;
/*      */               }
/*      */               
/* 4414 */               if (down > 0) {
/* 4415 */                 total_tag_limits_down += down;
/* 4416 */               } else if (down < 0) {
/* 4417 */                 some_down_disabled = true;
/*      */               }
/*      */               
/* 4420 */               result.add("    " + tag_name + ": " + SpeedLimitHandler.this.formatUp(up) + ", " + SpeedLimitHandler.this.formatDown(down));
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/* 4427 */       String dis_str = "";
/*      */       
/* 4429 */       if (some_up_disabled)
/*      */       {
/* 4431 */         dis_str = "up";
/*      */       }
/*      */       
/* 4434 */       if (some_down_disabled)
/*      */       {
/* 4436 */         dis_str = dis_str + (dis_str.length() == 0 ? "" : "&") + "down";
/*      */       }
/*      */       
/*      */ 
/* 4440 */       if (dis_str.length() > 0)
/*      */       {
/* 4442 */         dis_str = " (some " + dis_str + " disabled)";
/*      */       }
/*      */       
/* 4445 */       if (total_tag_limits == 0)
/*      */       {
/* 4447 */         result.add("    None" + dis_str);
/*      */       }
/*      */       else
/*      */       {
/* 4451 */         result.add("    ----");
/*      */         
/* 4453 */         result.add("    Total=" + total_tag_limits + " - Compounded limits: " + SpeedLimitHandler.this.formatUp(total_tag_limits_up) + ", " + SpeedLimitHandler.this.formatDown(total_tag_limits_down) + dis_str);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 4459 */       if (is_current)
/*      */       {
/* 4461 */         Map<LimitedRateGroup, List<Object>> plugin_limiters = new HashMap();
/*      */         
/* 4463 */         List<org.gudy.azureus2.core3.download.DownloadManager> dms = gm.getDownloadManagers();
/*      */         
/* 4465 */         for (org.gudy.azureus2.core3.download.DownloadManager dm : dms)
/*      */         {
/* 4467 */           Boolean[] arr$ = { Boolean.valueOf(true), Boolean.valueOf(false) };int len$ = arr$.length; boolean upload; for (int i$ = 0; i$ < len$; i$++) { upload = arr$[i$].booleanValue();
/*      */             
/* 4469 */             List<LimitedRateGroup> limiters = SpeedLimitHandler.this.trim(dm.getRateLimiters(upload));
/*      */             
/* 4471 */             for (LimitedRateGroup g : limiters)
/*      */             {
/* 4473 */               List<Object> entries = (List)plugin_limiters.get(g);
/*      */               
/* 4475 */               if (entries == null)
/*      */               {
/* 4477 */                 entries = new ArrayList();
/*      */                 
/* 4479 */                 plugin_limiters.put(g, entries);
/*      */                 
/* 4481 */                 entries.add(Boolean.valueOf(upload));
/* 4482 */                 entries.add(new int[] { 0 });
/*      */               }
/*      */               
/* 4485 */               entries.add(dm);
/*      */             }
/*      */           }
/*      */           
/* 4489 */           PEPeerManager pm = dm.getPeerManager();
/*      */           
/* 4491 */           if (pm != null)
/*      */           {
/* 4493 */             List<PEPeer> peers = pm.getPeers();
/*      */             
/* 4495 */             for (PEPeer peer : peers)
/*      */             {
/* 4497 */               Boolean[] arr$ = { Boolean.valueOf(true), Boolean.valueOf(false) };int len$ = arr$.length; boolean upload; for (int i$ = 0; i$ < len$; i$++) { upload = arr$[i$].booleanValue();
/*      */                 
/* 4499 */                 List<LimitedRateGroup> limiters = SpeedLimitHandler.this.trim(peer.getRateLimiters(upload));
/*      */                 
/* 4501 */                 for (LimitedRateGroup g : limiters)
/*      */                 {
/* 4503 */                   List<Object> entries = (List)plugin_limiters.get(g);
/*      */                   
/* 4505 */                   if (entries == null)
/*      */                   {
/* 4507 */                     entries = new ArrayList();
/*      */                     
/* 4509 */                     plugin_limiters.put(g, entries);
/*      */                     
/* 4511 */                     entries.add(Boolean.valueOf(upload));
/*      */                     
/* 4513 */                     entries.add(new int[] { 1 });
/*      */                   }
/*      */                   else
/*      */                   {
/* 4517 */                     ((int[])entries.get(1))[0] += 1;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 4525 */         result.add("");
/*      */         
/* 4527 */         result.add("Plugin Limits");
/*      */         
/* 4529 */         if (plugin_limiters.size() == 0)
/*      */         {
/* 4531 */           result.add("    None");
/*      */         }
/*      */         else
/*      */         {
/* 4535 */           List<String> plugin_lines = new ArrayList();
/*      */           
/* 4537 */           for (Map.Entry<LimitedRateGroup, List<Object>> entry : plugin_limiters.entrySet())
/*      */           {
/* 4539 */             LimitedRateGroup group = (LimitedRateGroup)entry.getKey();
/*      */             
/* 4541 */             List<Object> list = (List)entry.getValue();
/*      */             
/* 4543 */             boolean is_upload = ((Boolean)list.get(0)).booleanValue();
/* 4544 */             int peers = ((int[])(int[])list.get(1))[0];
/*      */             
/* 4546 */             String line = "    " + group.getName() + ": " + (is_upload ? SpeedLimitHandler.this.formatUp(group.getRateLimitBytesPerSecond()) : SpeedLimitHandler.this.formatDown(group.getRateLimitBytesPerSecond()));
/*      */             
/* 4548 */             if (peers > 0)
/*      */             {
/* 4550 */               line = line + ", peers=" + peers;
/*      */             }
/*      */             
/* 4553 */             if (list.size() > 2)
/*      */             {
/* 4555 */               line = line + ", downloads=" + (list.size() - 2);
/*      */             }
/*      */             
/* 4558 */             plugin_lines.add(line);
/*      */           }
/*      */           
/* 4561 */           Collections.sort(plugin_lines);
/*      */           
/* 4563 */           result.addAll(plugin_lines);
/*      */         }
/*      */       }
/*      */       
/* 4567 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class ScheduleRule
/*      */   {
/*      */     private static final byte FR_MON = 1;
/*      */     
/*      */     private static final byte FR_TUE = 2;
/*      */     
/*      */     private static final byte FR_WED = 4;
/*      */     
/*      */     private static final byte FR_THU = 8;
/*      */     
/*      */     private static final byte FR_FRI = 16;
/*      */     
/*      */     private static final byte FR_SAT = 32;
/*      */     
/*      */     private static final byte FR_SUN = 64;
/*      */     
/*      */     private static final byte FR_OVERFLOW = -128;
/*      */     
/*      */     private static final byte FR_WEEKDAY = 31;
/*      */     private static final byte FR_WEEKEND = 96;
/*      */     private static final byte FR_DAILY = 127;
/*      */     final String profile_name;
/*      */     final byte frequency;
/*      */     final int from_mins;
/*      */     final int to_mins;
/*      */     private final List<SpeedLimitHandler.ScheduleRuleExtensions> extensions;
/*      */     
/*      */     private ScheduleRule(byte _freq, String _profile, int _from, int _to, List<SpeedLimitHandler.ScheduleRuleExtensions> _exts)
/*      */     {
/* 4601 */       this.frequency = _freq;
/* 4602 */       this.profile_name = _profile;
/* 4603 */       this.from_mins = _from;
/* 4604 */       this.to_mins = _to;
/* 4605 */       this.extensions = _exts;
/*      */     }
/*      */     
/*      */ 
/*      */     private List<ScheduleRule> splitByDay()
/*      */     {
/* 4611 */       List<ScheduleRule> result = new ArrayList();
/*      */       
/* 4613 */       if (this.to_mins > this.from_mins)
/*      */       {
/* 4615 */         result.add(this);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 4621 */         byte next_frequency = (byte)(this.frequency << 1);
/*      */         
/* 4623 */         if ((next_frequency & 0xFFFFFF80) != 0)
/*      */         {
/* 4625 */           next_frequency = (byte)(next_frequency & 0x7F);
/*      */           
/* 4627 */           next_frequency = (byte)(next_frequency | 0x1);
/*      */         }
/*      */         
/* 4630 */         ScheduleRule rule1 = new ScheduleRule(this.frequency, this.profile_name, this.from_mins, 1439, this.extensions);
/* 4631 */         ScheduleRule rule2 = new ScheduleRule(next_frequency, this.profile_name, 0, this.to_mins, this.extensions);
/*      */         
/* 4633 */         result.add(rule1);
/* 4634 */         result.add(rule2);
/*      */       }
/*      */       
/* 4637 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */     private void checkExtensions()
/*      */     {
/* 4643 */       if (this.extensions != null)
/*      */       {
/* 4645 */         for (SpeedLimitHandler.ScheduleRuleExtensions ext : this.extensions)
/*      */         {
/* 4647 */           SpeedLimitHandler.ScheduleRuleExtensions.access$8200(ext);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean sameAs(ScheduleRule other)
/*      */     {
/* 4656 */       if (other == null)
/*      */       {
/* 4658 */         return false;
/*      */       }
/*      */       
/* 4661 */       if (this.extensions != other.extensions)
/*      */       {
/* 4663 */         if ((this.extensions == null) || (other.extensions == null) || (this.extensions.size() != other.extensions.size()))
/*      */         {
/* 4665 */           return false;
/*      */         }
/*      */         
/* 4668 */         for (SpeedLimitHandler.ScheduleRuleExtensions ext1 : this.extensions)
/*      */         {
/* 4670 */           boolean match = false;
/*      */           
/* 4672 */           for (SpeedLimitHandler.ScheduleRuleExtensions ext2 : other.extensions)
/*      */           {
/* 4674 */             if (SpeedLimitHandler.ScheduleRuleExtensions.access$8300(ext1, ext2))
/*      */             {
/* 4676 */               match = true;
/*      */               
/* 4678 */               break;
/*      */             }
/*      */           }
/*      */           
/* 4682 */           if (!match)
/*      */           {
/* 4684 */             return false;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 4689 */       return (this.frequency == other.frequency) && (this.profile_name.equals(other.profile_name)) && (this.from_mins == other.from_mins) && (this.to_mins == other.to_mins);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getString()
/*      */     {
/* 4698 */       String freq_str = "";
/*      */       
/* 4700 */       if (this.frequency == Byte.MAX_VALUE)
/*      */       {
/* 4702 */         freq_str = "daily";
/*      */       }
/* 4704 */       else if (this.frequency == 31)
/*      */       {
/* 4706 */         freq_str = "weekdays";
/*      */       }
/* 4708 */       else if (this.frequency == 96)
/*      */       {
/* 4710 */         freq_str = "weekends";
/*      */       }
/* 4712 */       else if (this.frequency == 1)
/*      */       {
/* 4714 */         freq_str = "mon";
/*      */       }
/* 4716 */       else if (this.frequency == 2)
/*      */       {
/* 4718 */         freq_str = "tue";
/*      */       }
/* 4720 */       else if (this.frequency == 4)
/*      */       {
/* 4722 */         freq_str = "wed";
/*      */       }
/* 4724 */       else if (this.frequency == 8)
/*      */       {
/* 4726 */         freq_str = "thu";
/*      */       }
/* 4728 */       else if (this.frequency == 16)
/*      */       {
/* 4730 */         freq_str = "fri";
/*      */       }
/* 4732 */       else if (this.frequency == 32)
/*      */       {
/* 4734 */         freq_str = "sat";
/*      */       }
/* 4736 */       else if (this.frequency == 64)
/*      */       {
/* 4738 */         freq_str = "sun";
/*      */       }
/*      */       
/* 4741 */       String ext_str = "";
/*      */       
/* 4743 */       if (this.extensions != null)
/*      */       {
/* 4745 */         for (SpeedLimitHandler.ScheduleRuleExtensions ext : this.extensions)
/*      */         {
/* 4747 */           ext_str = ext_str + ", " + SpeedLimitHandler.ScheduleRuleExtensions.access$8400(ext);
/*      */         }
/*      */       }
/*      */       
/* 4751 */       return "profile=" + this.profile_name + ", frequency=" + freq_str + ", from=" + getTime(this.from_mins) + ", to=" + getTime(this.to_mins) + ext_str;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private String getTime(int mins)
/*      */     {
/* 4758 */       String str = getTimeBit(mins / 60) + ":" + getTimeBit(mins % 60);
/*      */       
/* 4760 */       return str;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private String getTimeBit(int num)
/*      */     {
/* 4767 */       String str = String.valueOf(num);
/*      */       
/* 4769 */       if (str.length() < 2)
/*      */       {
/* 4771 */         str = "0" + str;
/*      */       }
/*      */       
/* 4774 */       return str;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class ScheduleRuleExtensions
/*      */   {
/*      */     private static final int ET_START_TAG = 1;
/*      */     
/*      */     private static final int ET_STOP_TAG = 2;
/*      */     
/*      */     private static final int ET_PAUSE_TAG = 3;
/*      */     
/*      */     private static final int ET_RESUME_TAG = 4;
/*      */     
/*      */     private static final int ET_ENABLE_PRIORITY = 5;
/*      */     
/*      */     private static final int ET_DISABLE_PRIORITY = 6;
/*      */     
/*      */     private static final int ET_ENABLE_NET_LIMIT = 7;
/*      */     private static final int ET_DISABLE_NET_LIMIT = 8;
/*      */     private final int extension_type;
/*      */     private final TagDownload tag;
/*      */     private final List<SpeedLimitHandler.NetLimit> net_limits;
/*      */     
/*      */     private ScheduleRuleExtensions(int _et)
/*      */     {
/* 4801 */       this.extension_type = _et;
/* 4802 */       this.tag = null;
/* 4803 */       this.net_limits = null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private ScheduleRuleExtensions(int _et, TagDownload _tag)
/*      */     {
/* 4811 */       this.extension_type = _et;
/* 4812 */       this.tag = _tag;
/* 4813 */       this.net_limits = null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private ScheduleRuleExtensions(List<SpeedLimitHandler.NetLimit> _et)
/*      */     {
/* 4821 */       this.extension_type = _et;
/* 4822 */       this.tag = null;
/* 4823 */       this.net_limits = _net_limits;
/*      */     }
/*      */     
/*      */     private void checkExtension()
/*      */     {
/*      */       boolean enable;
/* 4829 */       if (this.net_limits != null)
/*      */       {
/* 4831 */         enable = this.extension_type == 7;
/*      */         
/* 4833 */         for (SpeedLimitHandler.NetLimit nl : this.net_limits)
/*      */         {
/* 4835 */           nl.setEnabled(enable);
/*      */         }
/* 4837 */       } else if (this.tag == null)
/*      */       {
/* 4839 */         if (this.extension_type == 5)
/*      */         {
/* 4841 */           SpeedLimitHandler.this.prioritiser_enabled = true;
/*      */         }
/*      */         else
/*      */         {
/* 4845 */           SpeedLimitHandler.this.prioritiser_enabled = false;
/*      */         }
/*      */       } else {
/* 4848 */         Set<org.gudy.azureus2.core3.download.DownloadManager> downloads = this.tag.getTaggedDownloads();
/*      */         
/* 4850 */         for (org.gudy.azureus2.core3.download.DownloadManager download : downloads)
/*      */         {
/* 4852 */           if (download.isPaused())
/*      */           {
/* 4854 */             if ((this.extension_type == 4) && 
/*      */             
/* 4856 */               (!SpeedLimitHandler.this.rule_pause_all_active) && (!SpeedLimitHandler.this.net_limit_pause_all_active))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4862 */               download.resume();
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 4869 */             int state = download.getState();
/*      */             
/* 4871 */             if (this.extension_type == 1)
/*      */             {
/* 4873 */               if (state == 70)
/*      */               {
/* 4875 */                 download.setStateWaiting();
/*      */               }
/*      */               
/*      */             }
/* 4879 */             else if (this.extension_type == 3)
/*      */             {
/* 4881 */               if (!download.isPaused())
/*      */               {
/* 4883 */                 download.pause();
/*      */               }
/* 4885 */             } else if (this.extension_type == 2)
/*      */             {
/* 4887 */               if ((state != 8) && (state != 7) && (state != 6))
/*      */               {
/*      */ 
/*      */ 
/* 4891 */                 download.stopIt(70, false, false);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean sameAs(ScheduleRuleExtensions other)
/*      */     {
/* 4903 */       return (this.extension_type == other.extension_type) && (this.tag == other.tag);
/*      */     }
/*      */     
/*      */ 
/*      */     private String getString()
/*      */     {
/*      */       String str;
/*      */       String str;
/* 4911 */       if (this.extension_type == 1)
/*      */       {
/* 4913 */         str = "start_tag";
/*      */       } else { String str;
/* 4915 */         if (this.extension_type == 2)
/*      */         {
/* 4917 */           str = "stop_tag";
/*      */         } else { String str;
/* 4919 */           if (this.extension_type == 4)
/*      */           {
/* 4921 */             str = "resume_tag";
/*      */           } else { String str;
/* 4923 */             if (this.extension_type == 3)
/*      */             {
/* 4925 */               str = "pause_tag";
/*      */             } else { String str;
/* 4927 */               if (this.extension_type == 5)
/*      */               {
/* 4929 */                 str = "enable_priority";
/*      */               } else { String str;
/* 4931 */                 if (this.extension_type == 6)
/*      */                 {
/* 4933 */                   str = "disable_priority";
/*      */                 } else { String str;
/* 4935 */                   if (this.extension_type == 7)
/*      */                   {
/* 4937 */                     str = "enable_net_limit";
/*      */                   } else { String str;
/* 4939 */                     if (this.extension_type == 8)
/*      */                     {
/* 4941 */                       str = "disable_net_limit";
/*      */                     }
/*      */                     else
/*      */                     {
/* 4945 */                       str = "eh?"; }
/*      */                   }
/*      */                 } } } } } }
/* 4948 */       if (this.tag != null)
/*      */       {
/* 4950 */         str = str + ":" + this.tag.getTagName(true);
/*      */       }
/*      */       
/* 4953 */       if (this.net_limits != null)
/*      */       {
/* 4955 */         str = str + ":netlimits=" + this.net_limits.size();
/*      */       }
/* 4957 */       return str;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   class NetLimit
/*      */   {
/*      */     private final String name;
/*      */     
/*      */     private final double multiplier;
/*      */     private final String profile;
/*      */     private final TagType tag_type;
/*      */     private final String tag_name;
/*      */     private final long[] limits;
/* 4971 */     private boolean enabled = true;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private TagFeatureRateLimit tag;
/*      */     
/*      */ 
/*      */ 
/*      */     private LongTermStats lt_stats;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private NetLimit(String _name, double _mult, String _profile, TagType _tag_type, String _tag_name, long _total_lim, long _up_lim, long _down_lim)
/*      */     {
/* 4987 */       this.name = _name;
/* 4988 */       this.multiplier = _mult;
/* 4989 */       this.profile = _profile;
/* 4990 */       this.tag_type = _tag_type;
/* 4991 */       this.tag_name = _tag_name;
/* 4992 */       this.limits = new long[] { _total_lim, _up_lim, _down_lim };
/*      */     }
/*      */     
/*      */ 
/*      */     private void initialise()
/*      */     {
/* 4998 */       if (this.tag_type != null)
/*      */       {
/* 5000 */         this.tag = ((TagFeatureRateLimit)this.tag_type.getTag(this.tag_name, true));
/*      */         
/* 5002 */         if (this.tag == null)
/*      */         {
/* 5004 */           Debug.out("hmm, tag " + this.tag_name + " not found");
/*      */         }
/*      */         else {
/*      */           try
/*      */           {
/* 5009 */             this.lt_stats = StatsFactory.getGenericLongTermStats("tag." + this.tag.getTag().getTagUID(), new NetLimitStatsProvider(this.tag, null));
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/* 5015 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private String getName()
/*      */     {
/* 5024 */       return this.name;
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean isEnabled()
/*      */     {
/* 5030 */       return this.enabled;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setEnabled(boolean _b)
/*      */     {
/* 5037 */       this.enabled = _b;
/*      */     }
/*      */     
/*      */ 
/*      */     private double getMultiplier()
/*      */     {
/* 5043 */       return this.multiplier;
/*      */     }
/*      */     
/*      */ 
/*      */     private LongTermStats getLongTermStats()
/*      */     {
/* 5049 */       return this.lt_stats;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private String getProfile()
/*      */     {
/* 5056 */       return this.profile;
/*      */     }
/*      */     
/*      */ 
/*      */     private TagFeatureRateLimit getTag()
/*      */     {
/* 5062 */       return this.tag;
/*      */     }
/*      */     
/*      */ 
/*      */     private long[] getLimits()
/*      */     {
/* 5068 */       return this.limits;
/*      */     }
/*      */     
/*      */ 
/*      */     private class NetLimitStatsProvider
/*      */       implements LongTermStats.GenericStatsSource
/*      */     {
/*      */       private final TagType tag_type;
/*      */       
/*      */       private final String tag_name;
/*      */       
/*      */       private TagFeatureRateLimit tag_rl;
/*      */       
/*      */ 
/*      */       private NetLimitStatsProvider(TagFeatureRateLimit _tag_rl)
/*      */       {
/* 5084 */         this.tag_rl = _tag_rl;
/*      */         
/* 5086 */         Tag tag = this.tag_rl.getTag();
/*      */         
/* 5088 */         this.tag_type = tag.getTagType();
/* 5089 */         this.tag_name = tag.getTagName(true);
/*      */       }
/*      */       
/*      */ 
/*      */       public int getEntryCount()
/*      */       {
/* 5095 */         return 4;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public long[] getStats(String id)
/*      */       {
/* 5102 */         if (this.tag_type == SpeedLimitHandler.this.ip_set_tag_type)
/*      */         {
/*      */ 
/*      */ 
/* 5106 */           TagFeatureRateLimit t = (TagFeatureRateLimit)SpeedLimitHandler.this.ip_set_tag_type.getTag(this.tag_name, true);
/*      */           
/* 5108 */           if (t != this.tag_rl)
/*      */           {
/* 5110 */             this.tag_rl = t;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 5116 */         long[] up = this.tag_rl.getTagUploadTotal();
/* 5117 */         long[] down = this.tag_rl.getTagDownloadTotal();
/*      */         
/* 5119 */         long[] result = new long[4];
/*      */         
/* 5121 */         if (up != null)
/*      */         {
/* 5123 */           result[1] = up[0];
/*      */         }
/*      */         
/* 5126 */         if (down != null)
/*      */         {
/* 5128 */           result[3] = down[0];
/*      */         }
/*      */         
/*      */ 
/* 5132 */         return result;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class IPSetTagType
/*      */     extends TagTypeWithState
/*      */   {
/* 5141 */     private final int[] color_default = { 132, 16, 57 };
/*      */     
/*      */ 
/*      */     private IPSetTagType()
/*      */     {
/* 5146 */       super(65, "tag.type.ipset");
/*      */       
/* 5148 */       addTagType();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public int[] getColorDefault()
/*      */     {
/* 5155 */       return this.color_default;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class IPSet
/*      */   {
/*      */     private final String name;
/*      */     
/* 5164 */     private long[][] ranges = new long[0][];
/* 5165 */     private final Set<String> country_codes = new HashSet();
/* 5166 */     private final Set<String> networks = new HashSet();
/*      */     
/*      */     private boolean inverse;
/*      */     
/*      */     private Set<String> categories_or_tags;
/*      */     
/*      */     private boolean has_explicit_up_lim;
/*      */     
/*      */     private boolean has_explicit_down_lim;
/* 5175 */     private long last_send_total = -1L;
/* 5176 */     private long last_recv_total = -1L;
/*      */     
/*      */ 
/*      */ 
/* 5180 */     final Average send_rate = AverageFactory.MovingImmediateAverage(10);
/* 5181 */     final Average receive_rate = AverageFactory.MovingImmediateAverage(10);
/*      */     
/*      */     final RateLimiter up_limiter;
/*      */     
/*      */     final RateLimiter down_limiter;
/*      */     
/*      */     private int peer_up_lim;
/*      */     
/*      */     private int peer_down_lim;
/*      */     
/*      */     private TagPeerImpl tag_impl;
/*      */     
/*      */     private IPSet(String _name)
/*      */     {
/* 5195 */       this.name = _name;
/*      */       
/* 5197 */       this.up_limiter = SpeedLimitHandler.this.plugin_interface.getConnectionManager().createRateLimiter("ps-" + this.name, 0);
/* 5198 */       this.down_limiter = SpeedLimitHandler.this.plugin_interface.getConnectionManager().createRateLimiter("ps-" + this.name, 0);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void initialise(int tag_id)
/*      */     {
/* 5205 */       if (SpeedLimitHandler.this.ip_set_tag_type != null)
/*      */       {
/* 5207 */         this.tag_impl = new TagPeerImpl(tag_id, null);
/*      */       }
/*      */       
/* 5210 */       if (!this.has_explicit_up_lim)
/*      */       {
/* 5212 */         this.up_limiter.setRateLimitBytesPerSecond(COConfigurationManager.getIntParameter("speed.limit.handler.ipset_n." + tag_id + ".up", 0));
/*      */       }
/*      */       
/* 5215 */       if (!this.has_explicit_down_lim)
/*      */       {
/* 5217 */         this.down_limiter.setRateLimitBytesPerSecond(COConfigurationManager.getIntParameter("speed.limit.handler.ipset_n." + tag_id + ".down", 0));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void setParameters(boolean _inverse, int _up_lim, int _down_lim, int _peer_up_lim, int _peer_down_lim, Set<String> _cats_or_tags)
/*      */     {
/* 5230 */       this.inverse = _inverse;
/*      */       
/* 5232 */       this.has_explicit_up_lim = (_up_lim >= 0);
/* 5233 */       if (!this.has_explicit_up_lim) {
/* 5234 */         _up_lim = 0;
/*      */       }
/*      */       
/* 5237 */       this.has_explicit_down_lim = (_down_lim >= 0);
/* 5238 */       if (!this.has_explicit_down_lim) {
/* 5239 */         _down_lim = 0;
/*      */       }
/*      */       
/* 5242 */       this.up_limiter.setRateLimitBytesPerSecond(_up_lim);
/* 5243 */       this.down_limiter.setRateLimitBytesPerSecond(_down_lim);
/*      */       
/* 5245 */       this.peer_up_lim = _peer_up_lim;
/* 5246 */       this.peer_down_lim = _peer_down_lim;
/*      */       
/* 5248 */       this.categories_or_tags = (_cats_or_tags.size() == 0 ? null : _cats_or_tags);
/*      */     }
/*      */     
/*      */ 
/*      */     private int getPeerUpLimit()
/*      */     {
/* 5254 */       return this.peer_up_lim;
/*      */     }
/*      */     
/*      */ 
/*      */     private int getPeerDownLimit()
/*      */     {
/* 5260 */       return this.peer_down_lim;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean addCIDRorCCetc(String cidr_or_cc_etc)
/*      */     {
/* 5267 */       if (Character.isDigit(cidr_or_cc_etc.charAt(0)))
/*      */       {
/* 5269 */         String cidr = cidr_or_cc_etc;
/*      */         
/* 5271 */         int pos = cidr.indexOf('/');
/*      */         
/* 5273 */         if (pos == -1)
/*      */         {
/* 5275 */           return false;
/*      */         }
/*      */         
/* 5278 */         String address = cidr.substring(0, pos);
/*      */         
/*      */ 
/*      */ 
/* 5282 */         if (address.contains(":"))
/*      */         {
/* 5284 */           return false;
/*      */         }
/*      */         try
/*      */         {
/* 5288 */           byte[] start_bytes = HostNameToIPResolver.syncResolve(address).getAddress();
/*      */           
/* 5290 */           int cidr_mask = Integer.parseInt(cidr.substring(pos + 1));
/*      */           
/* 5292 */           int rev_mask = 0;
/*      */           
/* 5294 */           for (int i = 0; i < 32 - cidr_mask; i++)
/*      */           {
/* 5296 */             rev_mask = rev_mask << 1 | 0x1;
/*      */           }
/*      */           
/* 5299 */           int tmp102_101 = 0; byte[] tmp102_99 = start_bytes;tmp102_99[tmp102_101] = ((byte)(tmp102_99[tmp102_101] & (rev_mask >> 24 ^ 0xFFFFFFFF))); int 
/* 5300 */             tmp117_116 = 1; byte[] tmp117_114 = start_bytes;tmp117_114[tmp117_116] = ((byte)(tmp117_114[tmp117_116] & (rev_mask >> 16 ^ 0xFFFFFFFF))); int 
/* 5301 */             tmp132_131 = 2; byte[] tmp132_129 = start_bytes;tmp132_129[tmp132_131] = ((byte)(tmp132_129[tmp132_131] & (rev_mask >> 8 ^ 0xFFFFFFFF))); int 
/* 5302 */             tmp147_146 = 3; byte[] tmp147_144 = start_bytes;tmp147_144[tmp147_146] = ((byte)(tmp147_144[tmp147_146] & (rev_mask ^ 0xFFFFFFFF)));
/*      */           
/* 5304 */           byte[] end_bytes = (byte[])start_bytes.clone(); int 
/*      */           
/* 5306 */             tmp169_168 = 0; byte[] tmp169_166 = end_bytes;tmp169_166[tmp169_168] = ((byte)(tmp169_166[tmp169_168] | rev_mask >> 24 & 0xFF)); int 
/* 5307 */             tmp186_185 = 1; byte[] tmp186_183 = end_bytes;tmp186_183[tmp186_185] = ((byte)(tmp186_183[tmp186_185] | rev_mask >> 16 & 0xFF)); int 
/* 5308 */             tmp203_202 = 2; byte[] tmp203_200 = end_bytes;tmp203_200[tmp203_202] = ((byte)(tmp203_200[tmp203_202] | rev_mask >> 8 & 0xFF)); int 
/* 5309 */             tmp220_219 = 3; byte[] tmp220_217 = end_bytes;tmp220_217[tmp220_219] = ((byte)(tmp220_217[tmp220_219] | rev_mask & 0xFF));
/*      */           
/* 5311 */           long l_start = (start_bytes[0] << 24 & 0xFF000000 | start_bytes[1] << 16 & 0xFF0000 | start_bytes[2] << 8 & 0xFF00 | start_bytes[3] & 0xFF) & 0xFFFFFFFF;
/* 5312 */           long l_end = (end_bytes[0] << 24 & 0xFF000000 | end_bytes[1] << 16 & 0xFF0000 | end_bytes[2] << 8 & 0xFF00 | end_bytes[3] & 0xFF) & 0xFFFFFFFF;
/*      */           
/*      */ 
/*      */ 
/* 5316 */           int len = this.ranges.length;
/*      */           
/* 5318 */           long[][] new_ranges = new long[len + 1][];
/*      */           
/* 5320 */           System.arraycopy(this.ranges, 0, new_ranges, 0, len);
/*      */           
/* 5322 */           new_ranges[len] = { l_start, l_end };
/*      */           
/* 5324 */           this.ranges = new_ranges;
/*      */           
/* 5326 */           return true;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 5330 */           return false;
/*      */         }
/*      */       }
/*      */       
/* 5334 */       for (String net : AENetworkClassifier.AT_NETWORKS)
/*      */       {
/* 5336 */         if (cidr_or_cc_etc.equalsIgnoreCase(net))
/*      */         {
/* 5338 */           this.networks.add(net);
/*      */           
/* 5340 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 5344 */       if (cidr_or_cc_etc.equalsIgnoreCase("IPv4"))
/*      */       {
/* 5346 */         this.networks.add("IPv4");
/*      */         
/* 5348 */         return true;
/*      */       }
/* 5350 */       if (cidr_or_cc_etc.equalsIgnoreCase("IPv6"))
/*      */       {
/* 5352 */         this.networks.add("IPv6");
/*      */         
/* 5354 */         return true;
/*      */       }
/* 5356 */       if (cidr_or_cc_etc.equalsIgnoreCase("LAN"))
/*      */       {
/* 5358 */         this.networks.add("LAN");
/*      */         
/* 5360 */         return true;
/*      */       }
/* 5362 */       if (cidr_or_cc_etc.equalsIgnoreCase("WAN"))
/*      */       {
/* 5364 */         this.networks.add("WAN");
/*      */         
/* 5366 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5373 */       if (cidr_or_cc_etc.equalsIgnoreCase("all"))
/*      */       {
/* 5375 */         this.networks.addAll(Arrays.asList(AENetworkClassifier.AT_NETWORKS));
/*      */         
/* 5377 */         return true;
/*      */       }
/*      */       
/* 5380 */       String cc = cidr_or_cc_etc;
/*      */       
/* 5382 */       if (cc.length() != 2)
/*      */       {
/* 5384 */         return false;
/*      */       }
/*      */       
/* 5387 */       this.country_codes.add(cc.toUpperCase(Locale.US));
/*      */       
/* 5389 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void addSet(IPSet other)
/*      */     {
/* 5397 */       long[][] new_ranges = new long[this.ranges.length + other.ranges.length][];
/*      */       
/* 5399 */       System.arraycopy(this.ranges, 0, new_ranges, 0, this.ranges.length);
/* 5400 */       System.arraycopy(other.ranges, 0, new_ranges, this.ranges.length, other.ranges.length);
/*      */       
/* 5402 */       this.ranges = new_ranges;
/*      */       
/* 5404 */       this.country_codes.addAll(other.country_codes);
/*      */       
/* 5406 */       this.networks.addAll(other.networks);
/*      */     }
/*      */     
/*      */ 
/*      */     private String getName()
/*      */     {
/* 5412 */       return this.name;
/*      */     }
/*      */     
/*      */ 
/*      */     private long[][] getRanges()
/*      */     {
/* 5418 */       return this.ranges;
/*      */     }
/*      */     
/*      */ 
/*      */     private Set<String> getCountryCodes()
/*      */     {
/* 5424 */       return this.country_codes;
/*      */     }
/*      */     
/*      */ 
/*      */     private Set<String> getNetworks()
/*      */     {
/* 5430 */       return this.networks;
/*      */     }
/*      */     
/*      */ 
/*      */     private RateLimiter getUpLimiter()
/*      */     {
/* 5436 */       return this.up_limiter;
/*      */     }
/*      */     
/*      */ 
/*      */     private RateLimiter getDownLimiter()
/*      */     {
/* 5442 */       return this.down_limiter;
/*      */     }
/*      */     
/*      */ 
/*      */     private Set<String> getCategoriesOrTags()
/*      */     {
/* 5448 */       return this.categories_or_tags;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void updateStats(int tick_count)
/*      */     {
/* 5455 */       long send_total = this.up_limiter.getRateLimitTotalByteCount();
/* 5456 */       long recv_total = this.down_limiter.getRateLimitTotalByteCount();
/*      */       
/* 5458 */       if (this.last_send_total != -1L)
/*      */       {
/* 5460 */         long send_diff = send_total - this.last_send_total;
/* 5461 */         long recv_diff = recv_total - this.last_recv_total;
/*      */         
/* 5463 */         this.send_rate.update(send_diff);
/* 5464 */         this.receive_rate.update(recv_diff);
/*      */       }
/*      */       
/* 5467 */       this.last_send_total = send_total;
/* 5468 */       this.last_recv_total = recv_total;
/*      */       
/* 5470 */       TagPeerImpl tag = this.tag_impl;
/*      */       
/* 5472 */       if (tag != null)
/*      */       {
/* 5474 */         tag.update(tick_count);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean isInverse()
/*      */     {
/* 5481 */       return this.inverse;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void addPeer(PeerManager peer_manager, Peer peer)
/*      */     {
/* 5489 */       TagPeerImpl tag = this.tag_impl;
/*      */       
/* 5491 */       if (tag != null)
/*      */       {
/* 5493 */         tag.add(peer_manager, peer);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void removePeer(PeerManager peer_manager, Peer peer)
/*      */     {
/* 5502 */       TagPeerImpl tag = this.tag_impl;
/*      */       
/* 5504 */       if (tag != null)
/*      */       {
/* 5506 */         tag.remove(peer_manager, peer);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private void removeAllPeers()
/*      */     {
/* 5513 */       TagPeerImpl tag = this.tag_impl;
/*      */       
/* 5515 */       if (tag != null)
/*      */       {
/* 5517 */         tag.removeAll();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private void destroy()
/*      */     {
/* 5524 */       if (this.tag_impl != null)
/*      */       {
/* 5526 */         this.tag_impl.removeTag();
/*      */         
/* 5528 */         this.tag_impl = null;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private String getAddressString()
/*      */     {
/* 5535 */       long address_count = 0L;
/*      */       
/* 5537 */       for (long[] range : this.ranges) {
/* 5538 */         address_count += range[1] - range[0] + 1L;
/*      */       }
/*      */       
/* 5541 */       if (address_count == 0L)
/*      */       {
/* 5543 */         return "[]";
/*      */       }
/*      */       
/* 5546 */       return String.valueOf(address_count);
/*      */     }
/*      */     
/*      */ 
/*      */     private String getDetailString()
/*      */     {
/* 5552 */       return this.name + ": Up=" + SpeedLimitHandler.this.format(this.up_limiter.getRateLimitBytesPerSecond()) + " (" + DisplayFormatters.formatByteCountToKiBEtcPerSec(this.send_rate.getAverage()) + ")" + ", Down=" + SpeedLimitHandler.this.format(this.down_limiter.getRateLimitBytesPerSecond()) + " (" + DisplayFormatters.formatByteCountToKiBEtcPerSec(this.receive_rate.getAverage()) + ")" + ", Addresses=" + getAddressString() + ", CC=" + this.country_codes + ", Networks=" + this.networks + ", Inverse=" + this.inverse + ", Categories/Tags=" + (this.categories_or_tags == null ? "[]" : String.valueOf(this.categories_or_tags)) + ", Peer_Up=" + SpeedLimitHandler.this.format(this.peer_up_lim) + ", Peer_Down=" + SpeedLimitHandler.this.format(this.peer_down_lim);
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
/*      */     private class TagPeerImpl
/*      */       extends TagBase
/*      */       implements TagPeer, TagFeatureExecOnAssign
/*      */     {
/* 5568 */       private final Object UPLOAD_PRIORITY_ADDED_KEY = new Object();
/*      */       
/*      */       private int upload_priority;
/*      */       
/* 5572 */       private final Set<PEPeer> added_peers = new HashSet();
/* 5573 */       private final Set<PEPeer> pending_peers = new HashSet();
/*      */       
/*      */ 
/*      */ 
/*      */       private TagPeerImpl(int tag_id)
/*      */       {
/* 5579 */         super(tag_id, SpeedLimitHandler.IPSet.this.name);
/*      */         
/* 5581 */         addTag();
/*      */         
/* 5583 */         this.upload_priority = COConfigurationManager.getIntParameter("speed.limit.handler.ipset_n." + getTagID() + ".uppri", 0);
/*      */       }
/*      */       
/*      */ 
/*      */       public int getTaggableTypes()
/*      */       {
/* 5589 */         return 4;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getSupportedActions()
/*      */       {
/* 5595 */         return 1;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private void update(int tick_count)
/*      */       {
/* 5602 */         List<PEPeer> to_remove = null;
/* 5603 */         List<PEPeer> to_add = null;
/*      */         
/* 5605 */         synchronized (this)
/*      */         {
/* 5607 */           if (tick_count % 5 == 0)
/*      */           {
/* 5609 */             Iterator<PEPeer> it = this.added_peers.iterator();
/*      */             
/* 5611 */             while (it.hasNext())
/*      */             {
/* 5613 */               PEPeer peer = (PEPeer)it.next();
/*      */               
/* 5615 */               if (peer.getPeerState() == 50)
/*      */               {
/* 5617 */                 it.remove();
/*      */                 
/* 5619 */                 if (to_remove == null)
/*      */                 {
/* 5621 */                   to_remove = new ArrayList();
/*      */                 }
/*      */                 
/* 5624 */                 to_remove.add(peer);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 5629 */           Iterator<PEPeer> it = this.pending_peers.iterator();
/*      */           
/* 5631 */           while (it.hasNext())
/*      */           {
/* 5633 */             PEPeer peer = (PEPeer)it.next();
/*      */             
/* 5635 */             int state = peer.getPeerState();
/*      */             
/* 5637 */             if (state == 30)
/*      */             {
/* 5639 */               it.remove();
/*      */               
/* 5641 */               this.added_peers.add(peer);
/*      */               
/* 5643 */               if (to_add == null)
/*      */               {
/* 5645 */                 to_add = new ArrayList();
/*      */               }
/*      */               
/* 5648 */               to_add.add(peer);
/*      */             }
/* 5650 */             else if (state == 50)
/*      */             {
/* 5652 */               it.remove();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 5659 */         if (to_add != null)
/*      */         {
/* 5661 */           for (PEPeer peer : to_add)
/*      */           {
/* 5663 */             addTaggable(peer);
/*      */           }
/*      */         }
/*      */         
/* 5667 */         if (to_remove != null)
/*      */         {
/* 5669 */           for (PEPeer peer : to_remove)
/*      */           {
/* 5671 */             removeTaggable(peer);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       private void add(PeerManager peer_manager, Peer _peer)
/*      */       {
/* 5681 */         PEPeer peer = PluginCoreUtils.unwrap(_peer);
/*      */         
/* 5683 */         if (isActionEnabled(1))
/*      */         {
/* 5685 */           peer_manager.removePeer(_peer);
/*      */           
/* 5687 */           return;
/*      */         }
/*      */         
/* 5690 */         synchronized (this)
/*      */         {
/* 5692 */           if (peer.getPeerState() == 30)
/*      */           {
/* 5694 */             if (this.added_peers.contains(peer))
/*      */             {
/* 5696 */               return;
/*      */             }
/*      */             
/* 5699 */             this.pending_peers.remove(peer);
/*      */             
/* 5701 */             this.added_peers.add(peer);
/*      */           }
/*      */           else
/*      */           {
/* 5705 */             this.pending_peers.add(peer);
/*      */             
/* 5707 */             return;
/*      */           }
/*      */         }
/*      */         
/* 5711 */         addTaggable(peer);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       private void remove(PeerManager peer_manager, Peer _peer)
/*      */       {
/* 5719 */         PEPeer peer = PluginCoreUtils.unwrap(_peer);
/*      */         
/* 5721 */         synchronized (this)
/*      */         {
/* 5723 */           if (this.pending_peers.remove(peer))
/*      */           {
/* 5725 */             return;
/*      */           }
/*      */           
/* 5728 */           if (!this.added_peers.remove(peer))
/*      */           {
/* 5730 */             return;
/*      */           }
/*      */         }
/*      */         
/* 5734 */         removeTaggable(peer);
/*      */       }
/*      */       
/*      */ 
/*      */       private void removeAll()
/*      */       {
/*      */         List<PEPeer> to_remove;
/*      */         
/* 5742 */         synchronized (this)
/*      */         {
/* 5744 */           this.pending_peers.clear();
/*      */           
/* 5746 */           to_remove = new ArrayList(this.added_peers);
/*      */           
/* 5748 */           this.added_peers.clear();
/*      */         }
/*      */         
/* 5751 */         for (PEPeer peer : to_remove)
/*      */         {
/* 5753 */           removeTaggable(peer);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void addTaggable(Taggable t)
/*      */       {
/* 5761 */         if (this.upload_priority > 0)
/*      */         {
/* 5763 */           ((PEPeer)t).updateAutoUploadPriority(this.UPLOAD_PRIORITY_ADDED_KEY, true);
/*      */         }
/*      */         
/* 5766 */         super.addTaggable(t);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void removeTaggable(Taggable t)
/*      */       {
/* 5773 */         if (this.upload_priority > 0)
/*      */         {
/* 5775 */           ((PEPeer)t).updateAutoUploadPriority(this.UPLOAD_PRIORITY_ADDED_KEY, false);
/*      */         }
/*      */         
/* 5778 */         super.removeTaggable(t);
/*      */       }
/*      */       
/*      */       /* Error */
/*      */       public int getTaggedCount()
/*      */       {
/*      */         // Byte code:
/*      */         //   0: aload_0
/*      */         //   1: dup
/*      */         //   2: astore_1
/*      */         //   3: monitorenter
/*      */         //   4: aload_0
/*      */         //   5: getfield 272	com/aelitis/azureus/core/speedmanager/SpeedLimitHandler$IPSet$TagPeerImpl:added_peers	Ljava/util/Set;
/*      */         //   8: invokeinterface 317 1 0
/*      */         //   13: aload_1
/*      */         //   14: monitorexit
/*      */         //   15: ireturn
/*      */         //   16: astore_2
/*      */         //   17: aload_1
/*      */         //   18: monitorexit
/*      */         //   19: aload_2
/*      */         //   20: athrow
/*      */         // Line number table:
/*      */         //   Java source line #5784	-> byte code offset #0
/*      */         //   Java source line #5786	-> byte code offset #4
/*      */         //   Java source line #5787	-> byte code offset #16
/*      */         // Local variable table:
/*      */         //   start	length	slot	name	signature
/*      */         //   0	21	0	this	TagPeerImpl
/*      */         //   2	16	1	Ljava/lang/Object;	Object
/*      */         //   16	4	2	localObject1	Object
/*      */         // Exception table:
/*      */         //   from	to	target	type
/*      */         //   4	15	16	finally
/*      */         //   16	19	16	finally
/*      */       }
/*      */       
/*      */       /* Error */
/*      */       public List<PEPeer> getTaggedPeers()
/*      */       {
/*      */         // Byte code:
/*      */         //   0: aload_0
/*      */         //   1: dup
/*      */         //   2: astore_1
/*      */         //   3: monitorenter
/*      */         //   4: new 150	java/util/ArrayList
/*      */         //   7: dup
/*      */         //   8: aload_0
/*      */         //   9: getfield 272	com/aelitis/azureus/core/speedmanager/SpeedLimitHandler$IPSet$TagPeerImpl:added_peers	Ljava/util/Set;
/*      */         //   12: invokespecial 304	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
/*      */         //   15: aload_1
/*      */         //   16: monitorexit
/*      */         //   17: areturn
/*      */         //   18: astore_2
/*      */         //   19: aload_1
/*      */         //   20: monitorexit
/*      */         //   21: aload_2
/*      */         //   22: athrow
/*      */         // Line number table:
/*      */         //   Java source line #5793	-> byte code offset #0
/*      */         //   Java source line #5795	-> byte code offset #4
/*      */         //   Java source line #5796	-> byte code offset #18
/*      */         // Local variable table:
/*      */         //   start	length	slot	name	signature
/*      */         //   0	23	0	this	TagPeerImpl
/*      */         //   2	18	1	Ljava/lang/Object;	Object
/*      */         //   18	4	2	localObject1	Object
/*      */         // Exception table:
/*      */         //   from	to	target	type
/*      */         //   4	17	18	finally
/*      */         //   18	21	18	finally
/*      */       }
/*      */       
/*      */       /* Error */
/*      */       public Set<Taggable> getTagged()
/*      */       {
/*      */         // Byte code:
/*      */         //   0: aload_0
/*      */         //   1: dup
/*      */         //   2: astore_1
/*      */         //   3: monitorenter
/*      */         //   4: new 151	java/util/HashSet
/*      */         //   7: dup
/*      */         //   8: aload_0
/*      */         //   9: getfield 272	com/aelitis/azureus/core/speedmanager/SpeedLimitHandler$IPSet$TagPeerImpl:added_peers	Ljava/util/Set;
/*      */         //   12: invokespecial 306	java/util/HashSet:<init>	(Ljava/util/Collection;)V
/*      */         //   15: aload_1
/*      */         //   16: monitorexit
/*      */         //   17: areturn
/*      */         //   18: astore_2
/*      */         //   19: aload_1
/*      */         //   20: monitorexit
/*      */         //   21: aload_2
/*      */         //   22: athrow
/*      */         // Line number table:
/*      */         //   Java source line #5802	-> byte code offset #0
/*      */         //   Java source line #5804	-> byte code offset #4
/*      */         //   Java source line #5805	-> byte code offset #18
/*      */         // Local variable table:
/*      */         //   start	length	slot	name	signature
/*      */         //   0	23	0	this	TagPeerImpl
/*      */         //   2	18	1	Ljava/lang/Object;	Object
/*      */         //   18	4	2	localObject1	Object
/*      */         // Exception table:
/*      */         //   from	to	target	type
/*      */         //   4	17	18	finally
/*      */         //   18	21	18	finally
/*      */       }
/*      */       
/*      */       /* Error */
/*      */       public boolean hasTaggable(Taggable t)
/*      */       {
/*      */         // Byte code:
/*      */         //   0: aload_0
/*      */         //   1: dup
/*      */         //   2: astore_2
/*      */         //   3: monitorenter
/*      */         //   4: aload_0
/*      */         //   5: getfield 272	com/aelitis/azureus/core/speedmanager/SpeedLimitHandler$IPSet$TagPeerImpl:added_peers	Ljava/util/Set;
/*      */         //   8: aload_1
/*      */         //   9: invokeinterface 320 2 0
/*      */         //   14: aload_2
/*      */         //   15: monitorexit
/*      */         //   16: ireturn
/*      */         //   17: astore_3
/*      */         //   18: aload_2
/*      */         //   19: monitorexit
/*      */         //   20: aload_3
/*      */         //   21: athrow
/*      */         // Line number table:
/*      */         //   Java source line #5812	-> byte code offset #0
/*      */         //   Java source line #5814	-> byte code offset #4
/*      */         //   Java source line #5815	-> byte code offset #17
/*      */         // Local variable table:
/*      */         //   start	length	slot	name	signature
/*      */         //   0	22	0	this	TagPeerImpl
/*      */         //   0	22	1	t	Taggable
/*      */         //   2	17	2	Ljava/lang/Object;	Object
/*      */         //   17	4	3	localObject1	Object
/*      */         // Exception table:
/*      */         //   from	to	target	type
/*      */         //   4	16	17	finally
/*      */         //   17	20	17	finally
/*      */       }
/*      */       
/*      */       public boolean supportsTagRates()
/*      */       {
/* 5821 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean supportsTagUploadLimit()
/*      */       {
/* 5827 */         return !SpeedLimitHandler.IPSet.this.has_explicit_up_lim;
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean supportsTagDownloadLimit()
/*      */       {
/* 5833 */         return !SpeedLimitHandler.IPSet.this.has_explicit_down_lim;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getTagUploadLimit()
/*      */       {
/* 5839 */         return SpeedLimitHandler.IPSet.this.up_limiter.getRateLimitBytesPerSecond();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void setTagUploadLimit(int bps)
/*      */       {
/* 5846 */         if (supportsTagUploadLimit())
/*      */         {
/* 5848 */           SpeedLimitHandler.IPSet.this.up_limiter.setRateLimitBytesPerSecond(bps);
/*      */           
/* 5850 */           COConfigurationManager.setParameter("speed.limit.handler.ipset_n." + getTagID() + ".up", bps);
/*      */           
/*      */ 
/*      */ 
/* 5854 */           List<PEPeer> peers = getTaggedPeers();
/*      */           
/* 5856 */           for (PEPeer peer : peers)
/*      */           {
/* 5858 */             for (LimitedRateGroup l : peer.getRateLimiters(true))
/*      */             {
/* 5860 */               l.getRateLimitBytesPerSecond();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public int getTagCurrentUploadRate()
/*      */       {
/* 5869 */         return (int)SpeedLimitHandler.IPSet.this.send_rate.getAverage();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       protected long[] getTagSessionUploadTotalCurrent()
/*      */       {
/* 5876 */         return new long[] { SpeedLimitHandler.IPSet.this.last_send_total };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       protected long[] getTagSessionDownloadTotalCurrent()
/*      */       {
/* 5883 */         return new long[] { SpeedLimitHandler.IPSet.this.last_recv_total };
/*      */       }
/*      */       
/*      */ 
/*      */       public int getTagDownloadLimit()
/*      */       {
/* 5889 */         return SpeedLimitHandler.IPSet.this.down_limiter.getRateLimitBytesPerSecond();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void setTagDownloadLimit(int bps)
/*      */       {
/* 5896 */         if (supportsTagDownloadLimit())
/*      */         {
/* 5898 */           SpeedLimitHandler.IPSet.this.down_limiter.setRateLimitBytesPerSecond(bps);
/*      */           
/* 5900 */           COConfigurationManager.setParameter("speed.limit.handler.ipset_n." + getTagID() + ".down", bps);
/*      */           
/*      */ 
/*      */ 
/* 5904 */           List<PEPeer> peers = getTaggedPeers();
/*      */           
/* 5906 */           for (PEPeer peer : peers)
/*      */           {
/* 5908 */             for (LimitedRateGroup l : peer.getRateLimiters(false))
/*      */             {
/* 5910 */               l.getRateLimitBytesPerSecond();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public int getTagCurrentDownloadRate()
/*      */       {
/* 5919 */         return (int)SpeedLimitHandler.IPSet.this.receive_rate.getAverage();
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean getCanBePublicDefault()
/*      */       {
/* 5925 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public int getTagUploadPriority()
/*      */       {
/* 5932 */         return this.upload_priority;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void setTagUploadPriority(int priority)
/*      */       {
/* 5939 */         if (priority < 0)
/*      */         {
/* 5941 */           priority = 0;
/*      */         }
/*      */         
/* 5944 */         if (priority == this.upload_priority)
/*      */         {
/* 5946 */           return;
/*      */         }
/*      */         
/* 5949 */         int old_up = this.upload_priority;
/*      */         
/* 5951 */         this.upload_priority = priority;
/*      */         
/* 5953 */         COConfigurationManager.setParameter("speed.limit.handler.ipset_n." + getTagID() + ".uppri", priority);
/*      */         
/* 5955 */         if ((old_up == 0) || (priority == 0))
/*      */         {
/* 5957 */           List<PEPeer> peers = getTaggedPeers();
/*      */           
/* 5959 */           for (PEPeer peer : peers)
/*      */           {
/* 5961 */             peer.updateAutoUploadPriority(this.UPLOAD_PRIORITY_ADDED_KEY, priority > 0);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void removeTag()
/*      */       {
/* 5969 */         if (this.upload_priority > 0)
/*      */         {
/* 5971 */           List<PEPeer> peers = getTaggedPeers();
/*      */           
/* 5973 */           for (PEPeer peer : peers)
/*      */           {
/* 5975 */             peer.updateAutoUploadPriority(this.UPLOAD_PRIORITY_ADDED_KEY, false);
/*      */           }
/*      */         }
/*      */         
/* 5979 */         super.removeTag();
/*      */       }
/*      */       
/*      */ 
/*      */       public String getDescription()
/*      */       {
/* 5985 */         return SpeedLimitHandler.IPSet.this.getDetailString();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class Prioritiser
/*      */   {
/*      */     private static final int FREQ_DEFAULT = 5;
/*      */     
/*      */     private static final int MIN_DEFAULT = 1024;
/*      */     private static final int MAX_DEFAULT = 104857600;
/*      */     private static final int PROBE_DEFAULT = 3;
/*      */     private static final int REST_DEFAULT = 12;
/*      */     private boolean is_down;
/* 6000 */     private int freq = 5;
/* 6001 */     private int min = 1024;
/* 6002 */     private int max = 104857600;
/* 6003 */     private int probe_period = 3;
/* 6004 */     private String name = "";
/* 6005 */     private int rest_ticks = 12;
/*      */     
/* 6007 */     private int tick_count = 0;
/*      */     
/* 6009 */     private int check_ticks = 1;
/* 6010 */     private int skip_ticks = 0;
/*      */     
/* 6012 */     private final List<Object[]> temp_states = new ArrayList();
/*      */     
/* 6014 */     private final List<PrioritiserTagState> tag_states = new ArrayList();
/*      */     
/* 6016 */     private int phase = 0;
/* 6017 */     private int phase_0_stable_waits = 0;
/* 6018 */     private int phase_0_count = 0;
/*      */     
/*      */ 
/* 6021 */     private PrioritiserTagState phase_1_tag = null;
/* 6022 */     private int phase_1_tag_state = 0;
/*      */     
/*      */     private int phase_1_tag_rate;
/*      */     private boolean phase_1_limit_hit;
/*      */     private int phase_1_higher_pri_rates;
/*      */     private int phase_1_lower_pri_decrease;
/* 6028 */     private int consec_limits_hit = 0;
/*      */     
/* 6030 */     private int phase_2_max_detected = 0;
/*      */     
/* 6032 */     private final Map<PrioritiserTagState, int[]> phase_2_limits = new HashMap();
/*      */     
/* 6034 */     private int phase_4_tag_state = 0;
/*      */     
/* 6036 */     private final Map<PrioritiserTagState, int[]> phase_4_limits = new HashMap();
/*      */     
/* 6038 */     private final Set<PrioritiserTagState> wake_on_active_tags = new HashSet();
/*      */     
/*      */ 
/*      */     private Prioritiser()
/*      */     {
/* 6043 */       setFrequency(5);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setIsDown(boolean _down)
/*      */     {
/* 6050 */       this.is_down = _down;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void addTarget(int priority, TagType tag_type, String tag_name)
/*      */     {
/* 6061 */       this.temp_states.add(new Object[] { tag_type, tag_name });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void initialise()
/*      */     {
/* 6069 */       for (Object[] entry : this.temp_states)
/*      */       {
/* 6071 */         TagType tag_type = (TagType)entry[0];
/* 6072 */         String tag_name = (String)entry[1];
/*      */         
/* 6074 */         TagFeatureRateLimit tag = (TagFeatureRateLimit)tag_type.getTag(tag_name, true);
/*      */         
/* 6076 */         if (tag == null)
/*      */         {
/* 6078 */           Debug.out("Hmm, tag '" + tag_name + "' not found for " + tag_type.getTagTypeName(true));
/*      */         }
/*      */         else
/*      */         {
/* 6082 */           PrioritiserTagState tag_state = new PrioritiserTagState(tag, null);
/*      */           
/* 6084 */           this.tag_states.add(tag_state);
/*      */           
/* 6086 */           setLimit(tag_state, this.tag_states.size() == 1 ? this.max : -1, "initial");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private int getTargetCount()
/*      */     {
/* 6094 */       return this.temp_states.size();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setFrequency(int _freq)
/*      */     {
/* 6101 */       this.freq = _freq;
/*      */       
/* 6103 */       this.check_ticks = (this.freq * 1000 / 5000);
/*      */       
/* 6105 */       if (this.check_ticks < 1)
/*      */       {
/* 6107 */         this.check_ticks = 1;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setMinimum(int _min)
/*      */     {
/* 6115 */       this.min = _min;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setMaximum(int _max)
/*      */     {
/* 6122 */       this.max = _max;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setProbePeriod(int _period)
/*      */     {
/* 6129 */       this.probe_period = _period;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setRestTicks(int ticks)
/*      */     {
/* 6136 */       this.rest_ticks = ticks;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setName(String str)
/*      */     {
/* 6143 */       this.name = str;
/*      */     }
/*      */     
/*      */ 
/*      */     private String getName()
/*      */     {
/* 6149 */       return this.name;
/*      */     }
/*      */     
/*      */ 
/*      */     private void check()
/*      */     {
/* 6155 */       if (!SpeedLimitHandler.this.prioritiser_enabled)
/*      */       {
/* 6157 */         for (PrioritiserTagState tag_state : this.tag_states)
/*      */         {
/* 6159 */           tag_state.setLimit(Integer.MAX_VALUE, "disabled");
/*      */         }
/*      */         
/* 6162 */         return;
/*      */       }
/*      */       
/* 6165 */       int num_tags = this.tag_states.size();
/*      */       
/* 6167 */       if (this.skip_ticks > 0)
/*      */       {
/* 6169 */         this.skip_ticks -= 1;
/*      */         
/* 6171 */         int total_wakeup_rate = 0;
/*      */         
/* 6173 */         for (PrioritiserTagState tag_state : this.tag_states)
/*      */         {
/* 6175 */           int raw_rate = tag_state.updateAverage(true);
/*      */           
/* 6177 */           if (this.wake_on_active_tags.contains(tag_state))
/*      */           {
/* 6179 */             boolean active = tag_state.update();
/*      */             
/* 6181 */             if (active)
/*      */             {
/* 6183 */               total_wakeup_rate += raw_rate;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 6188 */         if (total_wakeup_rate > 2048)
/*      */         {
/* 6190 */           log("Waking up early, active tag(s) detected");
/*      */           
/* 6192 */           this.skip_ticks = 0;
/*      */         }
/*      */         else
/*      */         {
/* 6196 */           return;
/*      */         }
/*      */       }
/*      */       
/* 6200 */       this.wake_on_active_tags.clear();
/*      */       
/* 6202 */       this.tick_count += 1;
/*      */       
/* 6204 */       if (this.tick_count % this.check_ticks != 0)
/*      */       {
/* 6206 */         return;
/*      */       }
/*      */       
/* 6209 */       List<PrioritiserTagState> active_tags = new ArrayList();
/*      */       
/* 6211 */       boolean adjusting = false;
/*      */       
/* 6213 */       int rate_available = this.phase_2_max_detected == 0 ? this.max : this.phase_2_max_detected;
/*      */       
/* 6215 */       for (int i = 0; i < num_tags; i++)
/*      */       {
/* 6217 */         PrioritiserTagState tag_state = (PrioritiserTagState)this.tag_states.get(i);
/*      */         
/* 6219 */         tag_state.updateAverage(false);
/*      */         
/* 6221 */         int rate = tag_state.getRate();
/*      */         
/* 6223 */         boolean active = tag_state.update();
/*      */         
/* 6225 */         if (active)
/*      */         {
/* 6227 */           active_tags.add(tag_state);
/*      */           
/* 6229 */           if (tag_state.isAdjusting())
/*      */           {
/* 6231 */             adjusting = true;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*      */           int inactive_rate;
/*      */           
/*      */           int inactive_rate;
/* 6239 */           if (i < num_tags / 3)
/*      */           {
/* 6241 */             inactive_rate = Math.max(rate_available, 5120);
/*      */           }
/*      */           else
/*      */           {
/* 6245 */             inactive_rate = 5120;
/*      */           }
/*      */           
/* 6248 */           tag_state.setLimit(inactive_rate, "inactive[no log]");
/*      */         }
/*      */         
/* 6251 */         rate_available -= rate;
/*      */       }
/*      */       
/* 6254 */       int num_active = active_tags.size();
/*      */       
/* 6256 */       if (num_active == 0)
/*      */       {
/* 6258 */         return;
/*      */       }
/*      */       
/* 6261 */       if (num_active == 1)
/*      */       {
/* 6263 */         ((PrioritiserTagState)active_tags.get(0)).setLimit(this.max, "only one active");
/*      */         
/* 6265 */         return;
/*      */       }
/*      */       
/* 6268 */       if (adjusting)
/*      */       {
/* 6270 */         return;
/*      */       }
/*      */       
/* 6273 */       String str = "";
/*      */       
/* 6275 */       for (int i = 0; i < num_active; i++)
/*      */       {
/* 6277 */         PrioritiserTagState tag_state = (PrioritiserTagState)active_tags.get(i);
/*      */         
/* 6279 */         if (!this.is_down)
/*      */         {
/* 6281 */           tag_state.getTag().setTagUploadPriority(i <= (num_active - 1) / 3 ? 1 : 0);
/*      */         }
/*      */         
/* 6284 */         str = str + (str.length() == 0 ? "" : ", ") + tag_state.getString();
/*      */       }
/*      */       
/* 6287 */       GlobalManagerStats gm_stats = SpeedLimitHandler.this.core.getGlobalManager().getStats();
/*      */       
/* 6289 */       long glob = this.is_down ? gm_stats.getSmoothedReceiveRate() : gm_stats.getSmoothedSendRate();
/*      */       
/* 6291 */       log("* " + str + " [global=" + formatRate(glob, false) + "]");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6314 */       if (this.phase == 0)
/*      */       {
/* 6316 */         boolean all_good = true;
/*      */         
/* 6318 */         if (this.phase_0_stable_waits < 1)
/*      */         {
/* 6320 */           this.phase_0_stable_waits += 1;
/*      */           
/* 6322 */           for (int i = 0; i < active_tags.size(); i++)
/*      */           {
/* 6324 */             PrioritiserTagState tag_state = (PrioritiserTagState)active_tags.get(i);
/*      */             
/* 6326 */             int limit = tag_state.getLimit();
/* 6327 */             int rate = tag_state.getRate();
/*      */             
/* 6329 */             boolean stable = tag_state.isStable();
/*      */             
/* 6331 */             if (limit == -1)
/*      */             {
/* 6333 */               limit = 0;
/*      */             }
/*      */             
/* 6336 */             if ((!stable) || (!sameRate(limit, rate)))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6346 */               boolean weak_tag = false;
/* 6347 */               boolean weakly_stable = false;
/*      */               
/* 6349 */               int probe_rate = tag_state.getProbeRate();
/*      */               
/* 6351 */               if ((tag_state.getStrength() < 5) && (probe_rate > 0))
/*      */               {
/* 6353 */                 weak_tag = true;
/*      */                 
/* 6355 */                 if (rate >= 80 * probe_rate / 100)
/*      */                 {
/* 6357 */                   weakly_stable = true;
/*      */                 }
/*      */               }
/*      */               
/* 6361 */               if (weakly_stable)
/*      */               {
/* 6363 */                 int target = Math.max(probe_rate * 2, rate);
/*      */                 
/* 6365 */                 target = Math.min(this.max, target);
/*      */                 
/* 6367 */                 target -= 2048;
/*      */                 
/* 6369 */                 if (target < 1024)
/*      */                 {
/* 6371 */                   target = 1024;
/*      */                 }
/*      */                 
/* 6374 */                 tag_state.setLimit(target, "0: weak stable");
/*      */               }
/*      */               else
/*      */               {
/* 6378 */                 all_good = false;
/*      */                 
/*      */ 
/*      */ 
/* 6382 */                 if (limit > 0)
/*      */                 {
/* 6384 */                   if (stable)
/*      */                   {
/* 6386 */                     int target = rate;
/*      */                     
/* 6388 */                     if (target < 1024)
/*      */                     {
/* 6390 */                       target = 1024;
/*      */                     }
/*      */                     
/* 6393 */                     tag_state.setLimit(target, "0: reducing to current");
/*      */                   }
/*      */                   else
/*      */                   {
/* 6397 */                     int target = rate - 2048;
/*      */                     
/* 6399 */                     if (target <= 1024)
/*      */                     {
/* 6401 */                       target = -1;
/*      */                     }
/*      */                     
/* 6404 */                     tag_state.setLimit(target, "0: reducing, unstable");
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 6414 */         if (all_good)
/*      */         {
/* 6416 */           this.phase_0_stable_waits = 0;
/*      */           
/* 6418 */           if ((this.probe_period > 0) && (this.phase_0_count % (this.probe_period + 1) == 0))
/*      */           {
/*      */ 
/*      */ 
/* 6422 */             this.phase_2_limits.clear();
/*      */             
/* 6424 */             boolean changed = false;
/*      */             
/* 6426 */             for (PrioritiserTagState tag : active_tags)
/*      */             {
/* 6428 */               int rate = tag.getRate();
/*      */               
/* 6430 */               this.phase_2_limits.put(tag, new int[] { tag.getLimit(), rate, rate });
/*      */               
/* 6432 */               if (tag.setLimit(this.max, "1: probing"))
/*      */               {
/* 6434 */                 changed = true;
/*      */               }
/*      */             }
/*      */             
/* 6438 */             if (changed)
/*      */             {
/* 6440 */               this.phase = 2;
/*      */               
/* 6442 */               this.skip_ticks = 1;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 6448 */           if (this.phase == 0)
/*      */           {
/* 6450 */             this.phase = 1;
/*      */             
/* 6452 */             this.phase_1_tag = ((PrioritiserTagState)active_tags.get(0));
/*      */             
/* 6454 */             this.phase_1_tag_state = 0;
/*      */             
/* 6456 */             this.phase_1_limit_hit = false;
/*      */           }
/*      */         }
/* 6459 */       } else if (this.phase == 1)
/*      */       {
/*      */ 
/*      */ 
/* 6463 */         int start_index = active_tags.indexOf(this.phase_1_tag);
/*      */         
/* 6465 */         if (start_index == -1)
/*      */         {
/*      */ 
/*      */ 
/* 6469 */           this.phase = 0;
/*      */         }
/*      */         else
/*      */         {
/* 6473 */           boolean stay_in_phase_1 = false;
/*      */           
/* 6475 */           for (int i = start_index; i < num_active; i++)
/*      */           {
/* 6477 */             PrioritiserTagState tag_state = (PrioritiserTagState)active_tags.get(i);
/*      */             
/* 6479 */             this.phase_1_tag = tag_state;
/*      */             
/* 6481 */             int current_rate = tag_state.getRate();
/*      */             
/* 6483 */             int total_rate = 0;
/* 6484 */             int higher_pri_rates = 0;
/*      */             
/* 6486 */             int high_priority_strength = 0;
/* 6487 */             int low_priority_strength = 0;
/*      */             
/* 6489 */             for (int j = 0; j < num_active; j++)
/*      */             {
/* 6491 */               PrioritiserTagState s = (PrioritiserTagState)active_tags.get(j);
/*      */               
/* 6493 */               int rate = s.getRate();
/*      */               
/* 6495 */               total_rate += rate;
/*      */               
/* 6497 */               if (j < i)
/*      */               {
/* 6499 */                 higher_pri_rates += rate;
/*      */               }
/*      */               
/* 6502 */               if (j <= i)
/*      */               {
/* 6504 */                 high_priority_strength += s.getStrength();
/*      */               }
/*      */               else
/*      */               {
/* 6508 */                 low_priority_strength += s.getStrength();
/*      */               }
/*      */             }
/*      */             
/* 6512 */             if ((this.phase_2_max_detected > 0) && (this.phase_2_max_detected < total_rate))
/*      */             {
/* 6514 */               this.phase_2_max_detected = total_rate;
/*      */             }
/*      */             
/* 6517 */             if ((tag_state.getLimit() != this.max) && (this.phase_1_tag_state == 0))
/*      */             {
/*      */ 
/*      */ 
/* 6521 */               for (int j = 0; j < i; j++)
/*      */               {
/* 6523 */                 PrioritiserTagState s = (PrioritiserTagState)active_tags.get(j);
/*      */                 
/* 6525 */                 int rate = s.getRate();
/*      */                 
/* 6527 */                 s.setPreTestRate(rate);
/*      */               }
/*      */               
/* 6530 */               int limits_hit = tag_state.getLimitsHit();
/*      */               
/*      */               int raise_to;
/*      */               int raise_to;
/* 6534 */               if (limits_hit == 0)
/*      */               {
/* 6536 */                 raise_to = this.max;
/*      */               }
/*      */               else
/*      */               {
/* 6540 */                 int diff = this.max - current_rate;
/*      */                 
/* 6542 */                 int bump = diff / (limits_hit + 1);
/*      */                 
/* 6544 */                 if (bump < 2048)
/*      */                 {
/* 6546 */                   bump = 2048;
/*      */                 }
/*      */                 
/* 6549 */                 raise_to = Math.min(current_rate + bump, this.max);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 6555 */               int change_type = 0;
/*      */               
/* 6557 */               if (i < num_active / 3)
/*      */               {
/* 6559 */                 if (high_priority_strength <= low_priority_strength / 2)
/*      */                 {
/* 6561 */                   change_type = 1;
/*      */                 }
/*      */               }
/*      */               
/* 6565 */               tag_state.setLimit(raise_to, change_type, "1: raising to " + (raise_to == this.max ? "max" : formatRate(raise_to, true)) + " {" + high_priority_strength + "/" + low_priority_strength + "}");
/*      */               
/* 6567 */               int decrease_by = 2048;
/*      */               
/* 6569 */               for (int j = 0; j < this.consec_limits_hit; j++)
/*      */               {
/* 6571 */                 decrease_by *= 2;
/*      */                 
/* 6573 */                 if (decrease_by > Math.min(this.max / 4, 10240)) {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6582 */               int total_decrease = 0;
/*      */               
/* 6584 */               for (int j = num_active - 1; j > i; j--)
/*      */               {
/* 6586 */                 PrioritiserTagState ts = (PrioritiserTagState)active_tags.get(j);
/*      */                 
/* 6588 */                 int rate = ts.getRate();
/*      */                 
/*      */                 int decrease;
/*      */                 
/*      */                 int target;
/* 6593 */                 if (rate >= decrease_by)
/*      */                 {
/* 6595 */                   int decrease = decrease_by;
/*      */                   
/* 6597 */                   int target = rate - decrease_by;
/*      */                   
/* 6599 */                   decrease_by = 0;
/*      */                 }
/*      */                 else
/*      */                 {
/* 6603 */                   decrease = rate;
/*      */                   
/* 6605 */                   target = 0;
/*      */                   
/* 6607 */                   decrease_by -= rate;
/*      */                 }
/*      */                 
/* 6610 */                 total_decrease += decrease;
/*      */                 
/* 6612 */                 if (target <= 1024)
/*      */                 {
/* 6614 */                   target = -1;
/*      */                 }
/*      */                 
/* 6617 */                 ts.setLimit(target, "1: decreasing lower priority (dec=" + formatRate(decrease, false) + ")");
/*      */                 
/* 6619 */                 if (decrease_by <= 0) {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/* 6625 */               this.phase_1_tag_state = 1;
/* 6626 */               this.phase_1_tag_rate = current_rate;
/* 6627 */               this.phase_1_higher_pri_rates = higher_pri_rates;
/* 6628 */               this.phase_1_lower_pri_decrease = total_decrease;
/*      */               
/* 6630 */               stay_in_phase_1 = true;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6636 */               this.skip_ticks = 1;
/*      */               
/* 6638 */               break;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 6644 */             if (this.phase_1_tag_state == 1)
/*      */             {
/* 6646 */               int my_diff = current_rate - this.phase_1_tag_rate;
/* 6647 */               int hp_diff = higher_pri_rates - this.phase_1_higher_pri_rates;
/*      */               
/* 6649 */               int my_target = current_rate;
/*      */               
/* 6651 */               if (my_target <= 1024)
/*      */               {
/* 6653 */                 my_target = -1;
/*      */               }
/* 6655 */               else if (sameRate(my_target, this.max))
/*      */               {
/* 6657 */                 my_target = this.max;
/*      */               }
/*      */               
/* 6660 */               int overall_gain = my_diff + hp_diff - this.phase_1_lower_pri_decrease;
/*      */               
/* 6662 */               int hp_drop = -hp_diff;
/*      */               
/* 6664 */               boolean limit_hit = (hp_drop > 0) && (my_diff > 0);
/*      */               
/* 6666 */               if (limit_hit)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6675 */                 if ((hp_drop <= 1024) || (hp_drop <= 3 * this.phase_1_higher_pri_rates / 100))
/*      */                 {
/* 6677 */                   limit_hit = false;
/*      */                 }
/* 6679 */                 else if ((hp_drop <= 10240) && (overall_gain >= 3 * hp_drop))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6685 */                   if (high_priority_strength >= low_priority_strength)
/*      */                   {
/* 6687 */                     limit_hit = false;
/*      */                     
/* 6689 */                     my_target -= 3 * hp_drop;
/*      */                     
/* 6691 */                     if (my_target <= 1024)
/*      */                     {
/* 6693 */                       my_target = -1;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/* 6699 */               if (limit_hit)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 6704 */                 boolean stick_with_decision = false;
/*      */                 
/* 6706 */                 for (int j = 0; j < i; j++)
/*      */                 {
/* 6708 */                   PrioritiserTagState s = (PrioritiserTagState)active_tags.get(j);
/*      */                   
/* 6710 */                   int pre_rate = s.getPreTestRate();
/* 6711 */                   int rate = s.getRate();
/*      */                   
/* 6713 */                   int diff = rate - pre_rate;
/*      */                   
/* 6715 */                   if (diff < 0)
/*      */                   {
/*      */ 
/*      */ 
/* 6719 */                     if (s.getStrength() < 5)
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/* 6724 */                       if (-diff >= pre_rate / 4)
/*      */                       {
/* 6726 */                         stick_with_decision = true;
/*      */                         
/* 6728 */                         break;
/*      */                       }
/*      */                       
/* 6731 */                       int probe_rate = s.getProbeRate();
/*      */                       
/* 6733 */                       if ((probe_rate <= 0) || (rate < 110 * probe_rate / 100))
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/* 6738 */                         stick_with_decision = true;
/*      */                         
/* 6740 */                         break;
/*      */                       }
/*      */                       
/*      */                     }
/*      */                     else
/*      */                     {
/* 6746 */                       stick_with_decision = true;
/*      */                       
/* 6748 */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6757 */                 if (!stick_with_decision)
/*      */                 {
/* 6759 */                   limit_hit = false;
/*      */                   
/* 6761 */                   log("Ignoring limit indicator as weak tags within probed limits (diffs=" + formatRate(hp_diff, false) + "/" + formatRate(my_diff, false));
/*      */                 }
/*      */               }
/*      */               
/* 6765 */               if (limit_hit)
/*      */               {
/*      */ 
/*      */ 
/* 6769 */                 if ((this.phase_1_limit_hit) || (hp_drop > 10240))
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
/* 6781 */                   if (overall_gain > 4096)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 6786 */                     my_target = this.phase_1_tag_rate + overall_gain / 4;
/*      */ 
/*      */ 
/*      */                   }
/*      */                   else
/*      */                   {
/*      */ 
/* 6793 */                     my_target = current_rate + hp_diff;
/*      */                     
/*      */ 
/*      */ 
/* 6797 */                     my_target = Math.min(my_target, this.phase_1_tag_rate - 2048);
/*      */                   }
/*      */                   
/* 6800 */                   if (my_target <= 1024)
/*      */                   {
/* 6802 */                     my_target = -1;
/*      */                   }
/*      */                   
/* 6805 */                   this.consec_limits_hit += 1;
/*      */                   
/* 6807 */                   tag_state.hitLimit(true);
/*      */                   
/* 6809 */                   tag_state.setLimit(my_target, 2, "1: adjusting after limit hit (diffs=" + formatRate(hp_diff, false) + "/" + formatRate(my_diff, false) + ", consec=" + this.consec_limits_hit + ")");
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/* 6814 */                   int low_pri_rates = 0;
/*      */                   
/* 6816 */                   for (int j = num_active - 1; j > i; j--)
/*      */                   {
/* 6818 */                     PrioritiserTagState ts = (PrioritiserTagState)active_tags.get(j);
/*      */                     
/* 6820 */                     low_pri_rates += ts.getRate();
/*      */                   }
/*      */                   
/* 6823 */                   int decrease_by = low_pri_rates / 4;
/*      */                   
/* 6825 */                   decrease_by = Math.min(decrease_by, 32768);
/*      */                   
/* 6827 */                   int total_decrease = 0;
/*      */                   
/* 6829 */                   for (int j = active_tags.size() - 1; j > i; j--)
/*      */                   {
/* 6831 */                     if (decrease_by <= 0) {
/*      */                       break;
/*      */                     }
/*      */                     
/*      */ 
/* 6836 */                     PrioritiserTagState ts = (PrioritiserTagState)active_tags.get(j);
/*      */                     
/* 6838 */                     int rate = ts.getRate();
/*      */                     
/*      */                     int decrease;
/*      */                     
/*      */                     int target;
/* 6843 */                     if (rate >= decrease_by)
/*      */                     {
/* 6845 */                       int decrease = decrease_by;
/*      */                       
/* 6847 */                       int target = rate - decrease_by;
/*      */                       
/* 6849 */                       decrease_by = 0;
/*      */                     }
/*      */                     else
/*      */                     {
/* 6853 */                       decrease = rate;
/*      */                       
/* 6855 */                       target = 0;
/*      */                       
/* 6857 */                       decrease_by -= rate;
/*      */                     }
/*      */                     
/* 6860 */                     total_decrease += decrease;
/*      */                     
/* 6862 */                     if (target <= 1024)
/*      */                     {
/* 6864 */                       target = -1;
/*      */                     }
/*      */                     
/* 6867 */                     ts.setLimit(target, "1: decreasing lower priority (dec=" + formatRate(decrease, false) + ")");
/*      */                   }
/*      */                   
/*      */ 
/* 6871 */                   break;
/*      */                 }
/*      */                 
/*      */ 
/* 6875 */                 this.phase_1_limit_hit = true;
/*      */                 
/* 6877 */                 tag_state.hitLimit(true);
/*      */                 
/* 6879 */                 tag_state.setLimit(this.phase_1_tag_rate, 2, "1: limit hit (diffs=" + formatRate(hp_diff, false) + "/" + formatRate(my_diff, false) + ", verifying");
/*      */                 
/* 6881 */                 this.phase_1_tag_state = 0;
/*      */                 
/* 6883 */                 stay_in_phase_1 = true; break;
/*      */               }
/*      */               
/*      */ 
/* 6887 */               this.phase_1_limit_hit = false;
/*      */               
/* 6889 */               tag_state.hitLimit(false);
/*      */               
/* 6891 */               tag_state.setLimit(my_target, "1: setting to current (diffs=" + formatRate(hp_diff, false) + "/" + formatRate(my_diff, false) + ")");
/*      */               
/* 6893 */               if (i < num_active - 1)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6900 */                 boolean quit_now = false;
/*      */                 
/* 6902 */                 if (this.phase_2_max_detected > 0)
/*      */                 {
/* 6904 */                   int hp_rate = 0;
/*      */                   
/* 6906 */                   for (int j = 0; j <= i; j++)
/*      */                   {
/* 6908 */                     hp_rate += ((PrioritiserTagState)active_tags.get(j)).getRate();
/*      */                   }
/*      */                   
/* 6911 */                   if (hp_rate >= 90 * this.phase_2_max_detected / 100)
/*      */                   {
/* 6913 */                     quit_now = true;
/*      */                   }
/*      */                 }
/*      */                 
/* 6917 */                 if (quit_now)
/*      */                 {
/* 6919 */                   log("Higher priority tags satisfy 90% of last probe result (" + formatRate(this.phase_2_max_detected, false) + ")");
/*      */                 }
/*      */                 else
/*      */                 {
/* 6923 */                   this.phase_1_tag = ((PrioritiserTagState)active_tags.get(i + 1));
/*      */                   
/* 6925 */                   this.phase_1_tag_state = 0;
/*      */                   
/* 6927 */                   stay_in_phase_1 = true;
/*      */                 }
/* 6929 */                 break;
/*      */               }
/*      */               
/*      */ 
/* 6933 */               this.consec_limits_hit = 0;
/*      */               
/*      */ 
/*      */ 
/* 6937 */               break;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6946 */           if (!stay_in_phase_1)
/*      */           {
/*      */ 
/*      */ 
/* 6950 */             this.phase = 3;
/*      */           }
/*      */         }
/* 6953 */       } else if (this.phase == 2)
/*      */       {
/*      */ 
/*      */ 
/* 6957 */         long old_total_rate = 0L;
/* 6958 */         long new_total_rate = 0L;
/*      */         
/* 6960 */         long total_inc = 0L;
/*      */         
/* 6962 */         String probe_str = "";
/*      */         
/* 6964 */         boolean tag_rate_went_down = false;
/*      */         
/* 6966 */         for (PrioritiserTagState tag : active_tags)
/*      */         {
/* 6968 */           int[] entry = (int[])this.phase_2_limits.get(tag);
/*      */           
/* 6970 */           if (entry != null)
/*      */           {
/* 6972 */             int old_rate = entry[1];
/*      */             
/* 6974 */             old_total_rate += old_rate;
/*      */             
/* 6976 */             int new_rate = tag.getRate();
/*      */             
/* 6978 */             tag.setProbeRate(new_rate);
/*      */             
/* 6980 */             new_total_rate += new_rate;
/*      */             
/* 6982 */             entry[2] = new_rate;
/*      */             
/* 6984 */             int inc = new_rate - old_rate;
/*      */             
/* 6986 */             if (inc > 0)
/*      */             {
/* 6988 */               total_inc += inc;
/*      */               
/* 6990 */               probe_str = probe_str + (probe_str.length() == 0 ? "" : ", ") + tag.getTagName() + " +" + formatRate(inc, false);
/*      */             }
/* 6992 */             else if (inc < 64512)
/*      */             {
/* 6994 */               tag_rate_went_down = true;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 6999 */         long diff = new_total_rate - old_total_rate;
/*      */         
/* 7001 */         this.phase_2_max_detected = ((int)new_total_rate);
/*      */         
/* 7003 */         log("Probe result: before=" + formatRate(old_total_rate, false) + ", after=" + formatRate(new_total_rate, false) + ", inc=" + formatRate(total_inc, false) + " [" + probe_str + "]");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 7009 */         int major_done = 0;
/* 7010 */         int major_skipped = 0;
/*      */         
/* 7012 */         for (Map.Entry<PrioritiserTagState, int[]> entry : this.phase_2_limits.entrySet())
/*      */         {
/* 7014 */           PrioritiserTagState tag = (PrioritiserTagState)entry.getKey();
/*      */           
/* 7016 */           int[] vals = (int[])entry.getValue();
/*      */           
/* 7018 */           int limit = vals[0];
/*      */           
/*      */           int change_type;
/*      */           int change_type;
/* 7022 */           if (tag_rate_went_down)
/*      */           {
/* 7024 */             change_type = 2;
/*      */           }
/*      */           else
/*      */           {
/* 7028 */             change_type = 0;
/*      */             
/* 7030 */             if ((diff > 0L) && (total_inc > 0L))
/*      */             {
/* 7032 */               int old_rate = vals[1];
/* 7033 */               int new_rate = vals[2];
/*      */               
/* 7035 */               int inc = new_rate - old_rate;
/*      */               
/* 7037 */               if (inc > 0)
/*      */               {
/* 7039 */                 limit += (int)(inc * diff / total_inc);
/*      */                 
/* 7041 */                 if (limit > this.max)
/*      */                 {
/* 7043 */                   limit = this.max;
/*      */                 }
/*      */                 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/* 7050 */                 change_type = 2;
/*      */               }
/*      */               
/*      */             }
/*      */             else
/*      */             {
/* 7056 */               change_type = 2;
/*      */             }
/*      */           }
/*      */           
/* 7060 */           boolean did_it = tag.setLimit(limit, change_type, "2: probe result");
/*      */           
/* 7062 */           if (change_type != 0)
/*      */           {
/* 7064 */             if (did_it)
/*      */             {
/* 7066 */               major_done++;
/*      */             }
/*      */             else
/*      */             {
/* 7070 */               major_skipped++;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 7078 */         if ((major_skipped > 0) && (major_done == 0))
/*      */         {
/* 7080 */           this.skip_ticks = 2;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 7085 */         this.phase = 0;
/*      */         
/* 7087 */         this.phase_0_count += 1;
/*      */       }
/* 7089 */       else if (this.phase == 3)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 7094 */         if ((this.rest_ticks > 0) && (this.phase_2_max_detected > 0))
/*      */         {
/* 7096 */           int current_rate = 0;
/*      */           
/* 7098 */           for (PrioritiserTagState tag : active_tags)
/*      */           {
/* 7100 */             int rate = tag.getRate();
/*      */             
/* 7102 */             current_rate += rate;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 7116 */           int achieved = current_rate * 100 / this.phase_2_max_detected;
/*      */           
/* 7118 */           achieved += 10;
/*      */           
/* 7120 */           this.skip_ticks = Math.min(this.rest_ticks, achieved * this.rest_ticks / 100);
/*      */           
/*      */ 
/*      */ 
/* 7124 */           ((PrioritiserTagState)active_tags.get(0)).setLimit(this.max, "resting");
/*      */           
/* 7126 */           for (int i = 0; i < (num_tags + 2) / 3; i++)
/*      */           {
/* 7128 */             PrioritiserTagState tag = (PrioritiserTagState)this.tag_states.get(i);
/*      */             
/* 7130 */             if (!active_tags.contains(tag))
/*      */             {
/* 7132 */               this.wake_on_active_tags.add(tag);
/*      */             }
/*      */           }
/*      */           
/* 7136 */           log("Resting for " + this.skip_ticks);
/*      */           
/*      */ 
/*      */ 
/* 7140 */           this.phase_4_tag_state = 0;
/*      */           
/* 7142 */           this.phase = 4;
/*      */         }
/*      */         else
/*      */         {
/* 7146 */           this.phase = 0;
/*      */           
/* 7148 */           this.phase_0_count += 1;
/*      */         }
/* 7150 */       } else if (this.phase == 4)
/*      */       {
/* 7152 */         if (this.phase_4_tag_state == 0)
/*      */         {
/* 7154 */           this.phase_4_limits.clear();
/*      */           
/* 7156 */           boolean changed = false;
/*      */           
/* 7158 */           int cutoff = (num_active + 2) / 3;
/*      */           
/* 7160 */           for (int i = 0; i < num_active; i++)
/*      */           {
/* 7162 */             PrioritiserTagState tag = (PrioritiserTagState)active_tags.get(i);
/*      */             
/* 7164 */             int limit = tag.getLimit();
/* 7165 */             int rate = tag.getRate();
/*      */             
/* 7167 */             this.phase_4_limits.put(tag, new int[] { limit, rate, rate, i < cutoff ? 0 : 1 });
/*      */             
/* 7169 */             if (i < cutoff)
/*      */             {
/* 7171 */               if (tag.setLimit(this.max, "4: mini-probing"))
/*      */               {
/* 7173 */                 changed = true;
/*      */               }
/*      */             }
/*      */             else {
/* 7177 */               if (!changed) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 7185 */               int lim = 9 * rate / 10;
/*      */               
/* 7187 */               if (lim < 1024)
/*      */               {
/* 7189 */                 lim = 1024;
/*      */               }
/*      */               
/* 7192 */               tag.setLimit(lim, "4: mini-probing");
/*      */             }
/*      */           }
/*      */           
/* 7196 */           if (changed)
/*      */           {
/* 7198 */             this.phase_4_tag_state = 1;
/*      */             
/* 7200 */             this.skip_ticks = 1;
/*      */           }
/*      */           else
/*      */           {
/* 7204 */             this.phase = 0;
/*      */             
/* 7206 */             this.phase_0_count += 1;
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/* 7212 */           int total_inc = 0;
/*      */           
/* 7214 */           String probe_str = "";
/*      */           
/* 7216 */           for (Map.Entry<PrioritiserTagState, int[]> entry : this.phase_4_limits.entrySet())
/*      */           {
/* 7218 */             PrioritiserTagState tag = (PrioritiserTagState)entry.getKey();
/* 7219 */             int[] details = (int[])entry.getValue();
/*      */             
/* 7221 */             if (active_tags.contains(tag))
/*      */             {
/* 7223 */               boolean hp = details[3] == 0;
/*      */               
/* 7225 */               if (hp)
/*      */               {
/* 7227 */                 int old_rate = details[1];
/*      */                 
/* 7229 */                 int new_rate = tag.getRate();
/*      */                 
/* 7231 */                 details[2] = new_rate;
/*      */                 
/* 7233 */                 int inc = new_rate - old_rate;
/*      */                 
/* 7235 */                 if (inc > 0)
/*      */                 {
/* 7237 */                   if (tag.getProbeRate() < new_rate)
/*      */                   {
/* 7239 */                     tag.setProbeRate(new_rate);
/*      */                   }
/*      */                   
/* 7242 */                   total_inc += inc;
/*      */                   
/* 7244 */                   probe_str = probe_str + (probe_str.length() == 0 ? "" : ", ") + tag.getTagName() + " +" + formatRate(inc, false);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 7251 */           log("Mini-probe result: inc=" + formatRate(total_inc, false) + " [" + probe_str + "]");
/*      */           
/* 7253 */           if (total_inc <= 10240)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 7261 */             for (Map.Entry<PrioritiserTagState, int[]> entry : this.phase_4_limits.entrySet())
/*      */             {
/* 7263 */               PrioritiserTagState tag = (PrioritiserTagState)entry.getKey();
/* 7264 */               int[] details = (int[])entry.getValue();
/*      */               
/* 7266 */               tag.setLimit(details[0], "4: reverting");
/*      */             }
/*      */             
/* 7269 */             this.skip_ticks = 1;
/*      */           }
/*      */           
/* 7272 */           this.phase = 0;
/*      */           
/* 7274 */           this.phase_0_count += 1;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private String formatRate(long rate, boolean is_limit)
/*      */     {
/* 7284 */       if ((rate == -1L) && (is_limit))
/*      */       {
/* 7286 */         return "x";
/*      */       }
/* 7288 */       if (rate < 0L)
/*      */       {
/* 7290 */         return "-" + DisplayFormatters.formatByteCountToKiBEtcPerSec(-rate);
/*      */       }
/* 7292 */       if (((rate == 0L) || (rate >= 104857600L)) && (is_limit))
/*      */       {
/* 7294 */         return "∞";
/*      */       }
/*      */       
/*      */ 
/* 7298 */       return DisplayFormatters.formatByteCountToKiBEtcPerSec(rate);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean setLimit(PrioritiserTagState tag_state, int rate, String reason)
/*      */     {
/* 7310 */       if (rate > 1024)
/*      */       {
/* 7312 */         rate = rate / 256 * 256;
/*      */       }
/*      */       
/* 7315 */       TagFeatureRateLimit tag = tag_state.getTag();
/*      */       
/* 7317 */       if (this.is_down)
/*      */       {
/* 7319 */         if (rate != tag.getTagDownloadLimit())
/*      */         {
/* 7321 */           tag.setTagDownloadLimit(rate);
/*      */           
/* 7323 */           if (!reason.contains("[no log]"))
/*      */           {
/* 7325 */             log(tag_state, "->" + formatRate(rate, true) + " (" + reason + ")");
/*      */           }
/*      */           
/* 7328 */           return true;
/*      */         }
/*      */         
/*      */       }
/* 7332 */       else if (rate != tag.getTagUploadLimit())
/*      */       {
/* 7334 */         tag.setTagUploadLimit(rate);
/*      */         
/* 7336 */         if (!reason.contains("[no log]"))
/*      */         {
/* 7338 */           log(tag_state, "->" + formatRate(rate, true) + " (" + reason + ")");
/*      */         }
/*      */         
/* 7341 */         return true;
/*      */       }
/*      */       
/*      */ 
/* 7345 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean sameRate(int r1, int r2)
/*      */     {
/* 7353 */       int diff = Math.abs(r1 - r2);
/*      */       
/* 7355 */       if (diff <= 1024)
/*      */       {
/* 7357 */         return true;
/*      */       }
/*      */       
/* 7360 */       int max = Math.max(r1, r2);
/*      */       
/*      */ 
/*      */ 
/* 7364 */       return max * 3 / 100 >= diff;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void log(PrioritiserTagState tag_state, String str)
/*      */     {
/* 7372 */       log(tag_state.getTagName() + ": " + str);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void log(String str)
/*      */     {
/* 7379 */       if (this.name.length() > 0)
/*      */       {
/* 7381 */         SpeedLimitHandler.this.logger.log("priority " + this.name + ": " + str);
/*      */       }
/*      */       else
/*      */       {
/* 7385 */         SpeedLimitHandler.this.logger.log("priority: " + str);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     class PrioritiserTagState
/*      */     {
/*      */       private static final int STABLE_PERIODS = 2;
/*      */       
/*      */       private static final int AVERAGE_PERIODS = 3;
/*      */       
/*      */       private static final int ADJUSTMENT_PERIODS = 2;
/*      */       
/*      */       private static final int INITIAL_ADJUSTMENT_PERIODS = 4;
/*      */       
/*      */       private static final int CT_NORMAL = 0;
/*      */       
/*      */       private static final int CT_MEDIUM = 1;
/*      */       private static final int CT_MAJOR = 2;
/*      */       private final TagFeatureRateLimit tag;
/* 7405 */       private final MovingImmediateAverage average = AverageFactory.MovingImmediateAverage(3);
/*      */       
/* 7407 */       private final int[] last_averages = new int[2];
/*      */       
/* 7409 */       private int active_ticks = 0;
/*      */       
/*      */       private int last_average_index;
/*      */       
/*      */       private boolean last_stable;
/*      */       
/*      */       private int last_rate;
/*      */       private int last_limit;
/* 7417 */       private int adjusting_ticks = 4;
/*      */       
/*      */       private int tag_limits_hit;
/*      */       
/*      */       private int strength;
/* 7422 */       private int probe_rate = -1;
/*      */       
/*      */       private int pre_test_rate;
/*      */       
/*      */ 
/*      */       private PrioritiserTagState(TagFeatureRateLimit _tag)
/*      */       {
/* 7429 */         this.tag = _tag;
/*      */       }
/*      */       
/*      */ 
/*      */       private String getTagName()
/*      */       {
/* 7435 */         return this.tag.getTag().getTagName(true);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private int getWeight(List<PEPeer> peers)
/*      */       {
/* 7442 */         int weight = 0;
/*      */         
/* 7444 */         for (PEPeer peer : peers)
/*      */         {
/* 7446 */           if (peer.getPeerState() == 30)
/*      */           {
/* 7448 */             weight++;
/*      */           }
/*      */         }
/*      */         
/* 7452 */         return weight;
/*      */       }
/*      */       
/*      */ 
/*      */       private boolean update()
/*      */       {
/* 7458 */         Tag t = this.tag.getTag();
/*      */         
/* 7460 */         int weight = 0;
/*      */         
/* 7462 */         if ((t instanceof TagDownload))
/*      */         {
/* 7464 */           Set<org.gudy.azureus2.core3.download.DownloadManager> downloads = ((TagDownload)this.tag).getTaggedDownloads();
/*      */           
/* 7466 */           for (org.gudy.azureus2.core3.download.DownloadManager dm : downloads)
/*      */           {
/* 7468 */             PEPeerManager pm = dm.getPeerManager();
/*      */             
/* 7470 */             if (pm != null)
/*      */             {
/* 7472 */               if ((!SpeedLimitHandler.Prioritiser.this.is_down) || (!dm.isDownloadComplete(false)))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 7477 */                 LimitedRateGroup[] limiters = dm.getRateLimiters(!SpeedLimitHandler.Prioritiser.this.is_down);
/*      */                 
/* 7479 */                 boolean disabled = false;
/*      */                 
/* 7481 */                 for (LimitedRateGroup rl : limiters)
/*      */                 {
/* 7483 */                   disabled = rl.isDisabled();
/*      */                   
/* 7485 */                   if (disabled) {
/*      */                     break;
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/* 7491 */                 if (!disabled)
/*      */                 {
/* 7493 */                   List<PEPeer> peers = pm.getPeers();
/*      */                   
/* 7495 */                   weight += getWeight(peers);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         } else {
/* 7501 */           List<PEPeer> peers = ((TagPeer)this.tag).getTaggedPeers();
/*      */           
/* 7503 */           weight = getWeight(peers);
/*      */         }
/*      */         
/* 7506 */         this.strength = weight;
/*      */         
/* 7508 */         if (weight > 0)
/*      */         {
/* 7510 */           this.active_ticks += 1;
/*      */           
/* 7512 */           return this.active_ticks > 1;
/*      */         }
/*      */         
/*      */ 
/* 7516 */         this.active_ticks = 0;
/*      */         
/* 7518 */         return false;
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
/* 7577 */       private long last_byte_count = -1L;
/* 7578 */       private long last_average_time = 0L;
/*      */       
/*      */ 
/*      */ 
/*      */       private int updateAverage(boolean is_skip_cycle)
/*      */       {
/* 7584 */         long now = SystemTime.getMonotonousTime();
/*      */         
/*      */         int limit;
/*      */         
/*      */         long[] current_byte_counts;
/*      */         
/*      */         int limit;
/* 7591 */         if (SpeedLimitHandler.Prioritiser.this.is_down)
/*      */         {
/* 7593 */           long[] current_byte_counts = this.tag.getTagDownloadTotal();
/* 7594 */           limit = this.tag.getTagDownloadLimit();
/*      */         }
/*      */         else
/*      */         {
/* 7598 */           current_byte_counts = this.tag.getTagUploadTotal();
/* 7599 */           limit = this.tag.getTagUploadLimit();
/*      */         }
/*      */         
/* 7602 */         long current_byte_count = 0L;
/*      */         
/* 7604 */         for (long l : current_byte_counts)
/*      */         {
/* 7606 */           current_byte_count += l;
/*      */         }
/*      */         int rate;
/*      */         int rate;
/* 7610 */         if (this.last_byte_count == -1L)
/*      */         {
/* 7612 */           rate = 0;
/*      */         }
/*      */         else
/*      */         {
/* 7616 */           long diff_bytes = current_byte_count - this.last_byte_count;
/* 7617 */           long diff_time = now - this.last_average_time;
/*      */           int rate;
/* 7619 */           if (diff_time <= 0L)
/*      */           {
/* 7621 */             rate = 0;
/*      */           }
/*      */           else
/*      */           {
/* 7625 */             rate = (int)(diff_bytes * 1000L / diff_time);
/*      */           }
/*      */         }
/*      */         
/* 7629 */         this.last_byte_count = current_byte_count;
/* 7630 */         this.last_average_time = now;
/*      */         
/* 7632 */         if (!is_skip_cycle)
/*      */         {
/* 7634 */           if (this.adjusting_ticks > 0)
/*      */           {
/* 7636 */             this.adjusting_ticks -= 1;
/*      */           }
/*      */           
/* 7639 */           if (limit == -1)
/*      */           {
/* 7641 */             rate = 0;
/*      */           }
/* 7643 */           else if (rate > limit)
/*      */           {
/* 7645 */             rate = limit;
/*      */           }
/*      */           
/* 7648 */           int average_rate = (int)this.average.update(rate);
/*      */           
/* 7650 */           boolean stable = true;
/*      */           
/* 7652 */           for (int la : this.last_averages)
/*      */           {
/* 7654 */             if (!SpeedLimitHandler.Prioritiser.this.sameRate(average_rate, la))
/*      */             {
/* 7656 */               stable = false;
/*      */             }
/*      */           }
/*      */           
/* 7660 */           this.last_averages[(this.last_average_index++ % this.last_averages.length)] = average_rate;
/*      */           
/* 7662 */           this.last_limit = limit;
/* 7663 */           this.last_rate = average_rate;
/* 7664 */           this.last_stable = stable;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 7669 */         return rate;
/*      */       }
/*      */       
/*      */ 
/*      */       private TagFeatureRateLimit getTag()
/*      */       {
/* 7675 */         return this.tag;
/*      */       }
/*      */       
/*      */ 
/*      */       private int getLimit()
/*      */       {
/* 7681 */         return this.last_limit;
/*      */       }
/*      */       
/*      */ 
/*      */       private int getRate()
/*      */       {
/* 7687 */         return this.last_rate;
/*      */       }
/*      */       
/*      */ 
/*      */       private boolean isStable()
/*      */       {
/* 7693 */         return this.last_stable;
/*      */       }
/*      */       
/*      */ 
/*      */       private boolean isAdjusting()
/*      */       {
/* 7699 */         return this.adjusting_ticks > 0;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getStrength()
/*      */       {
/* 7705 */         return this.strength;
/*      */       }
/*      */       
/*      */ 
/*      */       private int getLimitsHit()
/*      */       {
/* 7711 */         return this.tag_limits_hit;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private void hitLimit(boolean b)
/*      */       {
/* 7718 */         if (b)
/*      */         {
/* 7720 */           this.tag_limits_hit += 1;
/*      */         }
/*      */         else
/*      */         {
/* 7724 */           this.tag_limits_hit = 0;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       private boolean setLimit(int limit, String reason)
/*      */       {
/* 7733 */         return setLimit(limit, 0, reason);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       private boolean setLimit(int limit, int change_type, String reason)
/*      */       {
/* 7742 */         if (limit == Integer.MAX_VALUE)
/*      */         {
/* 7744 */           limit = 0;
/*      */         }
/* 7746 */         else if (limit < SpeedLimitHandler.Prioritiser.this.min)
/*      */         {
/* 7748 */           limit = SpeedLimitHandler.Prioritiser.this.min;
/*      */         }
/* 7750 */         else if (limit > SpeedLimitHandler.Prioritiser.this.max)
/*      */         {
/* 7752 */           limit = SpeedLimitHandler.Prioritiser.this.max;
/*      */         }
/*      */         
/* 7755 */         if (change_type == 1)
/*      */         {
/* 7757 */           reason = reason + " (medium)";
/*      */         }
/* 7759 */         else if (change_type == 2)
/*      */         {
/* 7761 */           reason = reason + " (major)";
/*      */         }
/*      */         
/* 7764 */         if (SpeedLimitHandler.Prioritiser.this.setLimit(this, limit, reason))
/*      */         {
/* 7766 */           this.last_limit = limit;
/*      */           
/* 7768 */           this.average.reset();
/*      */           
/* 7770 */           this.adjusting_ticks = 2;
/*      */           
/* 7772 */           if (change_type == 1)
/*      */           {
/* 7774 */             this.adjusting_ticks += 1;
/*      */           }
/* 7776 */           else if (change_type == 2)
/*      */           {
/* 7778 */             this.adjusting_ticks *= 2;
/*      */           }
/*      */           
/* 7781 */           return true;
/*      */         }
/*      */         
/*      */ 
/* 7785 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       private void setProbeRate(int rate)
/*      */       {
/* 7793 */         this.probe_rate = rate;
/*      */       }
/*      */       
/*      */ 
/*      */       private int getProbeRate()
/*      */       {
/* 7799 */         return this.probe_rate;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private void setPreTestRate(int rate)
/*      */       {
/* 7806 */         this.pre_test_rate = rate;
/*      */       }
/*      */       
/*      */ 
/*      */       private int getPreTestRate()
/*      */       {
/* 7812 */         return this.pre_test_rate;
/*      */       }
/*      */       
/*      */ 
/*      */       private String getString()
/*      */       {
/* 7818 */         String str = getTagName() + "=" + SpeedLimitHandler.Prioritiser.this.formatRate(getRate(), false) + " (" + SpeedLimitHandler.Prioritiser.this.formatRate(getLimit(), true) + ") {" + getStrength() + (this.probe_rate <= 0 ? "" : new StringBuilder().append("/").append(SpeedLimitHandler.Prioritiser.this.formatRate(this.probe_rate, false)).toString()) + "}";
/*      */         
/*      */ 
/*      */ 
/* 7822 */         return str;
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/SpeedLimitHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */