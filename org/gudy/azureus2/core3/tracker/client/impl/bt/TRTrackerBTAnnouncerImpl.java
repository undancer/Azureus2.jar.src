/*      */ package org.gudy.azureus2.core3.tracker.client.impl.bt;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionManager;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory.UnknownHostException;
/*      */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacket;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerException;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.ConnectException;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Proxy;
/*      */ import java.net.URL;
/*      */ import java.net.URLEncoder;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Random;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.zip.GZIPInputStream;
/*      */ import javax.net.ssl.HttpsURLConnection;
/*      */ import javax.net.ssl.SSLContext;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerDataProvider;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerException;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerListener;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponsePeer;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraper;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*      */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerImpl;
/*      */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerImpl.Helper;
/*      */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerResponseImpl;
/*      */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerResponsePeerImpl;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.PRHelpers;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyAnnounce;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyAnnounce2;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyConnect;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyError;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketRequestAnnounce;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketRequestAnnounce2;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketTracker;
/*      */ import org.gudy.azureus2.core3.tracker.util.TRTrackerUtils;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.BoringException;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.Timer;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.clientid.ClientIDException;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.clientid.ClientIDManagerImpl;
/*      */ 
/*      */ public class TRTrackerBTAnnouncerImpl implements org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerHelper
/*      */ {
/*   89 */   public static final LogIDs LOGID = LogIDs.TRACKER;
/*      */   
/*      */   private static final int OVERRIDE_PERIOD = 10000;
/*      */   
/*   93 */   protected static final Timer tracker_timer = new Timer("Tracker Timer", 32);
/*      */   
/*      */   public static final String UDP_REALM = "UDP Tracker";
/*      */   
/*   97 */   private static int userMinInterval = 0;
/*   98 */   private static int userMaxNumwant = 100;
/*      */   private static boolean tcpAnnounceEnabled;
/*      */   private static boolean udpAnnounceEnabled;
/*      */   private static boolean udpProbeEnabled;
/*      */   
/*      */   static {
/*  104 */     org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPTrackerCodecs.registerCodecs();
/*  105 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Tracker Client Min Announce Interval", "Tracker Client Numwant Limit", "Tracker Client Enable TCP", "Server Enable UDP", "Tracker UDP Probe Enable" }, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  117 */         TRTrackerBTAnnouncerImpl.access$002(COConfigurationManager.getIntParameter("Tracker Client Min Announce Interval"));
/*  118 */         TRTrackerBTAnnouncerImpl.access$102(COConfigurationManager.getIntParameter("Tracker Client Numwant Limit"));
/*  119 */         TRTrackerBTAnnouncerImpl.access$202(COConfigurationManager.getBooleanParameter("Tracker Client Enable TCP"));
/*  120 */         TRTrackerBTAnnouncerImpl.access$302(COConfigurationManager.getBooleanParameter("Server Enable UDP"));
/*  121 */         TRTrackerBTAnnouncerImpl.access$402(COConfigurationManager.getBooleanParameter("Tracker UDP Probe Enable"));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*  126 */   private static final AEMonitor class_mon = new AEMonitor("TRTrackerBTAnnouncer:class");
/*  127 */   private static final Map tracker_report_map = new HashMap();
/*      */   
/*      */   final TOTorrent torrent;
/*      */   
/*      */   private final TOTorrentAnnounceURLSet[] announce_urls;
/*      */   
/*      */   private TRTrackerAnnouncerImpl.Helper helper;
/*      */   
/*      */   private TimerEvent current_timer_event;
/*      */   
/*      */   private TimerEventPerformer timer_event_action;
/*  138 */   protected int tracker_state = 1;
/*  139 */   private String tracker_status_str = "";
/*  140 */   private TRTrackerAnnouncerResponseImpl last_response = null;
/*      */   
/*      */   private long last_update_time_secs;
/*      */   
/*      */   private long current_time_to_wait_secs;
/*      */   
/*      */   final boolean manual_control;
/*      */   private long tracker_interval;
/*      */   private long tracker_min_interval;
/*  149 */   private long min_interval = 0L;
/*      */   
/*  151 */   private int failure_added_time = 0;
/*  152 */   private long failure_time_last_updated = 0L;
/*      */   
/*      */   private boolean stopped;
/*      */   private boolean stopped_for_queue;
/*      */   private boolean completed;
/*  157 */   private boolean complete_reported = false;
/*      */   
/*  159 */   private boolean update_in_progress = false;
/*      */   
/*  161 */   private long rd_last_override = 0L;
/*  162 */   private int rd_override_percentage = 100;
/*      */   
/*  164 */   private long min_interval_override = 0L;
/*      */   
/*      */   private List trackerUrlLists;
/*      */   
/*      */   private URL lastUsedUrl;
/*      */   
/*      */   private URL lastAZTrackerCheckedURL;
/*      */   
/*      */   private HashWrapper torrent_hash;
/*      */   
/*      */   private String last_tracker_message;
/*  175 */   private String info_hash = "info_hash=";
/*      */   private byte[] tracker_peer_id;
/*  177 */   private String tracker_peer_id_str = "&peer_id=";
/*      */   
/*      */   private byte[] data_peer_id;
/*      */   
/*      */   private int announceCount;
/*      */   
/*      */   private int announceFailCount;
/*      */   
/*  185 */   private byte autoUDPprobeEvery = 1;
/*      */   
/*      */   private int autoUDPProbeSuccessCount;
/*      */   
/*  189 */   private String tracker_id = "";
/*      */   
/*      */ 
/*      */   private String ip_override;
/*      */   
/*      */   private final String[] peer_networks;
/*      */   
/*      */   private TRTrackerAnnouncerDataProvider announce_data_provider;
/*      */   
/*  198 */   protected final AEMonitor this_mon = new AEMonitor("TRTrackerBTAnnouncer");
/*      */   
/*      */ 
/*      */   private boolean az_tracker;
/*      */   
/*      */ 
/*      */   private boolean enable_sni_hack;
/*      */   
/*      */ 
/*      */   private boolean internal_error_hack;
/*      */   
/*      */ 
/*      */   private boolean dh_hack;
/*      */   
/*      */ 
/*      */   private boolean destroyed;
/*      */   
/*      */ 
/*      */   public TRTrackerBTAnnouncerImpl(TOTorrent _torrent, TOTorrentAnnounceURLSet[] _announce_urls, String[] _peer_networks, boolean _manual, TRTrackerAnnouncerImpl.Helper _helper)
/*      */     throws TRTrackerAnnouncerException
/*      */   {
/*  219 */     this.torrent = _torrent;
/*  220 */     this.announce_urls = _announce_urls;
/*  221 */     this.peer_networks = _peer_networks;
/*  222 */     this.manual_control = _manual;
/*  223 */     this.helper = _helper;
/*      */     try
/*      */     {
/*  226 */       this.torrent_hash = _torrent.getHashWrapper();
/*      */     }
/*      */     catch (TOTorrentException e)
/*      */     {
/*  230 */       Logger.log(new LogEvent(this.torrent, LOGID, "Torrent hash retrieval fails", e));
/*      */       
/*  232 */       throw new TRTrackerAnnouncerException("TRTrackerAnnouncer: URL encode fails");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  237 */     constructTrackerUrlLists(true);
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  242 */       this.data_peer_id = this.helper.getPeerID();
/*      */       
/*  244 */       if (COConfigurationManager.getBooleanParameter("Tracker Separate Peer IDs"))
/*      */       {
/*  246 */         this.tracker_peer_id = ClientIDManagerImpl.getSingleton().generatePeerID(this.torrent_hash.getBytes(), true);
/*      */       }
/*      */       else
/*      */       {
/*  250 */         this.tracker_peer_id = this.data_peer_id;
/*      */       }
/*      */     }
/*      */     catch (ClientIDException e) {
/*  254 */       throw new TRTrackerAnnouncerException("TRTrackerAnnouncer: Peer ID generation fails", e);
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  259 */       this.info_hash += URLEncoder.encode(new String(this.torrent_hash.getBytes(), "ISO-8859-1"), "ISO-8859-1").replaceAll("\\+", "%20");
/*      */       
/*  261 */       this.tracker_peer_id_str += URLEncoder.encode(new String(this.tracker_peer_id, "ISO-8859-1"), "ISO-8859-1").replaceAll("\\+", "%20");
/*      */     }
/*      */     catch (UnsupportedEncodingException e)
/*      */     {
/*  265 */       Logger.log(new LogEvent(this.torrent, LOGID, "URL encode fails", e));
/*      */       
/*  267 */       throw new TRTrackerAnnouncerException("TRTrackerAnnouncer: URL encode fails");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  272 */     this.timer_event_action = new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent this_event)
/*      */       {
/*      */ 
/*  279 */         if (TRTrackerBTAnnouncerImpl.this.manual_control)
/*      */         {
/*  281 */           TRTrackerBTAnnouncerImpl.this.requestUpdateSupport();
/*      */           
/*  283 */           return;
/*      */         }
/*      */         
/*  286 */         long secs_to_wait = TRTrackerBTAnnouncerImpl.this.getErrorRetryInterval();
/*      */         
/*      */         try
/*      */         {
/*  290 */           secs_to_wait = TRTrackerBTAnnouncerImpl.this.requestUpdateSupport();
/*      */           
/*  292 */           if (Logger.isEnabled()) {
/*  293 */             Logger.log(new LogEvent(TRTrackerBTAnnouncerImpl.this.torrent, TRTrackerBTAnnouncerImpl.LOGID, "Next tracker announce (unadjusted) will be in " + secs_to_wait + "s"));
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*      */           long target_time;
/*  299 */           TRTrackerBTAnnouncerImpl.this.current_time_to_wait_secs = secs_to_wait;
/*      */           
/*  301 */           if (TRTrackerBTAnnouncerImpl.this.tracker_state != 4)
/*      */           {
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/*      */ 
/*  309 */               TRTrackerBTAnnouncerImpl.this.this_mon.enter();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  316 */               if (!this_event.isCancelled())
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  323 */                 secs_to_wait = TRTrackerBTAnnouncerImpl.this.getAdjustedSecsToWait();
/*      */                 
/*  325 */                 if (Logger.isEnabled()) {
/*  326 */                   Logger.log(new LogEvent(TRTrackerBTAnnouncerImpl.this.torrent, TRTrackerBTAnnouncerImpl.LOGID, "Next tracker announce (adjusted) will be in " + secs_to_wait + "s"));
/*      */                 }
/*      */                 
/*      */ 
/*  330 */                 long target_time = SystemTime.getCurrentTime() + secs_to_wait * 1000L;
/*      */                 
/*  332 */                 if ((TRTrackerBTAnnouncerImpl.this.current_timer_event != null) && (!TRTrackerBTAnnouncerImpl.this.current_timer_event.isCancelled()))
/*      */                 {
/*  334 */                   if ((TRTrackerBTAnnouncerImpl.this.current_timer_event != this_event) && (TRTrackerBTAnnouncerImpl.this.current_timer_event.getWhen() < target_time)) {
/*      */                     return;
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  342 */                   TRTrackerBTAnnouncerImpl.this.current_timer_event.cancel();
/*      */                 }
/*      */                 
/*  345 */                 if (!TRTrackerBTAnnouncerImpl.this.destroyed)
/*      */                 {
/*  347 */                   TRTrackerBTAnnouncerImpl.this.current_timer_event = TRTrackerBTAnnouncerImpl.tracker_timer.addEvent(target_time, this);
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/*  353 */               TRTrackerBTAnnouncerImpl.this.this_mon.exit();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     };
/*      */     
/*  360 */     if (Logger.isEnabled()) {
/*  361 */       Logger.log(new LogEvent(this.torrent, LOGID, "Tracker Announcer Created using url : " + trackerURLListToString()));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void cloneFrom(TRTrackerBTAnnouncerImpl other)
/*      */   {
/*  369 */     this.helper = other.helper;
/*  370 */     this.data_peer_id = other.data_peer_id;
/*  371 */     this.tracker_peer_id = other.tracker_peer_id;
/*  372 */     this.tracker_peer_id_str = other.tracker_peer_id_str;
/*  373 */     this.tracker_id = other.tracker_id;
/*      */     
/*  375 */     this.announce_data_provider = other.announce_data_provider;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected long getAdjustedSecsToWait()
/*      */   {
/*  382 */     long secs_to_wait = this.current_time_to_wait_secs;
/*      */     
/*  384 */     if ((this.last_response != null) && (this.last_response.getStatus() != 2))
/*      */     {
/*  386 */       if (this.last_response.getStatus() == 1)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  392 */         if (this.failure_added_time < 900) this.failure_added_time = 900;
/*  393 */         secs_to_wait = getErrorRetryInterval();
/*      */         
/*  395 */         if (Logger.isEnabled()) {
/*  396 */           Logger.log(new LogEvent(this.torrent, LOGID, "MIN INTERVAL CALC: tracker reported error, adjusting to error retry interval"));
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/*  402 */         secs_to_wait = getErrorRetryInterval();
/*  403 */         if (Logger.isEnabled()) {
/*  404 */           Logger.log(new LogEvent(this.torrent, LOGID, "MIN INTERVAL CALC: tracker seems to be offline, adjusting to error retry interval"));
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  413 */       if (this.rd_override_percentage == 0) {
/*  414 */         if (Logger.isEnabled()) {
/*  415 */           Logger.log(new LogEvent(this.torrent, LOGID, "MIN INTERVAL CALC: override, perc = 0"));
/*      */         }
/*  417 */         return 60L;
/*      */       }
/*      */       
/*  420 */       if (this.rd_override_percentage != 100) {
/*  421 */         secs_to_wait = secs_to_wait * this.rd_override_percentage / 100L;
/*  422 */         if (Logger.isEnabled()) {
/*  423 */           Logger.log(new LogEvent(this.torrent, LOGID, "MIN INTERVAL CALC: override, perc = " + this.rd_override_percentage));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  428 */       if (secs_to_wait < 60L)
/*      */       {
/*  430 */         secs_to_wait = 60L;
/*      */       }
/*      */       
/*      */ 
/*  434 */       if ((this.min_interval != 0L) && (secs_to_wait < this.min_interval)) {
/*  435 */         float percentage = (float)this.min_interval / (float)this.current_time_to_wait_secs;
/*      */         
/*      */ 
/*      */ 
/*  439 */         int added_secs = (int)((float)(this.min_interval - secs_to_wait) * percentage);
/*  440 */         secs_to_wait += added_secs;
/*      */         
/*  442 */         if (Logger.isEnabled()) {
/*  443 */           Logger.log(new LogEvent(this.torrent, LOGID, "MIN INTERVAL CALC: min_interval=" + this.min_interval + ", interval=" + this.current_time_to_wait_secs + ", orig=" + this.current_time_to_wait_secs + ", new=" + secs_to_wait + ", added=" + added_secs + ", perc=" + percentage));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  452 */     return secs_to_wait;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getStatus()
/*      */   {
/*  459 */     return this.tracker_state;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getStatusString()
/*      */   {
/*  465 */     return this.tracker_status_str;
/*      */   }
/*      */   
/*      */ 
/*      */   public org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer getBestAnnouncer()
/*      */   {
/*  471 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRefreshDelayOverrides(int percentage)
/*      */   {
/*  478 */     if (percentage > 100)
/*      */     {
/*  480 */       percentage = 100;
/*      */     }
/*  482 */     else if (percentage < 50)
/*      */     {
/*  484 */       percentage = 50;
/*      */     }
/*      */     
/*  487 */     long now = SystemTime.getCurrentTime();
/*      */     
/*  489 */     boolean override_allowed = (this.rd_last_override > 0L) && (now - this.rd_last_override > 10000L);
/*      */     
/*  491 */     if (now < this.rd_last_override) { override_allowed = true;
/*      */     }
/*  493 */     if ((override_allowed) && (this.rd_override_percentage != percentage)) {
/*      */       try
/*      */       {
/*  496 */         this.this_mon.enter();
/*      */         
/*  498 */         this.rd_last_override = now;
/*      */         
/*  500 */         this.rd_override_percentage = percentage;
/*      */         
/*  502 */         if ((this.current_timer_event != null) && (!this.current_timer_event.isCancelled()))
/*      */         {
/*  504 */           long start = this.current_timer_event.getCreatedTime();
/*  505 */           long expiry = this.current_timer_event.getWhen();
/*      */           
/*  507 */           long secs_to_wait = getAdjustedSecsToWait();
/*      */           
/*  509 */           long target_time = start + secs_to_wait * 1000L;
/*      */           
/*  511 */           if (target_time != expiry)
/*      */           {
/*  513 */             this.current_timer_event.cancel();
/*      */             
/*  515 */             if (!this.destroyed)
/*      */             {
/*  517 */               if (Logger.isEnabled()) {
/*  518 */                 Logger.log(new LogEvent(this.torrent, LOGID, "Changed next tracker announce to " + secs_to_wait + "s via " + Debug.getStackTrace(true, false, 0, 3)));
/*      */               }
/*      */               
/*      */ 
/*  522 */               this.current_timer_event = tracker_timer.addEvent(start, target_time, this.timer_event_action);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       finally
/*      */       {
/*  532 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isUpdating()
/*      */   {
/*  540 */     return this.update_in_progress;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getInterval()
/*      */   {
/*  546 */     return this.tracker_interval;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getMinInterval()
/*      */   {
/*  552 */     return this.tracker_min_interval;
/*      */   }
/*      */   
/*      */   public int getTimeUntilNextUpdate()
/*      */   {
/*      */     try
/*      */     {
/*  559 */       this.this_mon.enter();
/*      */       
/*  561 */       if (this.current_timer_event == null)
/*      */       {
/*  563 */         return getErrorRetryInterval();
/*      */       }
/*      */       
/*  566 */       int rem = (int)((this.current_timer_event.getWhen() - SystemTime.getCurrentTime()) / 1000L);
/*      */       
/*  568 */       return rem;
/*      */     }
/*      */     finally
/*      */     {
/*  572 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getLastUpdateTime()
/*      */   {
/*  579 */     return (int)this.last_update_time_secs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void update(boolean force)
/*      */   {
/*  586 */     long now = SystemTime.getCurrentTime() / 1000L;
/*      */     
/*  588 */     if (now < this.last_update_time_secs) { force = true;
/*      */     }
/*  590 */     long effective_min = this.min_interval_override > 0L ? this.min_interval_override : 60L;
/*      */     
/*  592 */     if ((this.manual_control) || (force) || (now - this.last_update_time_secs >= effective_min))
/*      */     {
/*  594 */       requestUpdate();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void complete(boolean already_reported)
/*      */   {
/*  603 */     this.complete_reported = ((this.complete_reported) || (already_reported));
/*      */     
/*  605 */     this.completed = true;
/*      */     
/*  607 */     requestUpdate();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void stop(boolean for_queue)
/*      */   {
/*  614 */     this.stopped = true;
/*  615 */     this.stopped_for_queue = for_queue;
/*      */     
/*  617 */     requestUpdate();
/*      */   }
/*      */   
/*      */   protected void requestUpdate()
/*      */   {
/*      */     try
/*      */     {
/*  624 */       this.this_mon.enter();
/*      */       
/*  626 */       if (this.current_timer_event != null)
/*      */       {
/*  628 */         this.current_timer_event.cancel();
/*      */       }
/*      */       
/*  631 */       this.rd_last_override = SystemTime.getCurrentTime();
/*      */       
/*  633 */       if (!this.destroyed)
/*      */       {
/*  635 */         if (Logger.isEnabled()) {
/*  636 */           Logger.log(new LogEvent(this.torrent, LOGID, "Forcing tracker announce now via " + Debug.getStackTrace(true, false, 0, 3)));
/*      */         }
/*      */         
/*      */ 
/*  640 */         this.current_timer_event = tracker_timer.addEvent(SystemTime.getCurrentTime(), this.timer_event_action);
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  647 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected long requestUpdateSupport()
/*      */   {
/*  655 */     boolean clear_progress = true;
/*      */     try
/*      */     {
/*      */       try {
/*  659 */         this.this_mon.enter();
/*      */         
/*      */ 
/*      */ 
/*  663 */         if ((this.update_in_progress) || (this.announce_data_provider == null))
/*      */         {
/*  665 */           clear_progress = false;
/*      */           
/*  667 */           long l1 = getErrorRetryInterval();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  674 */           this.this_mon.exit();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*  812 */             this.this_mon.enter();
/*      */             
/*  814 */             if (clear_progress)
/*      */             {
/*  816 */               this.update_in_progress = false;
/*      */             }
/*      */           }
/*      */           finally {}
/*  820 */           return l1;
/*      */         }
/*  670 */         this.update_in_progress = true;
/*      */       }
/*      */       finally
/*      */       {
/*  674 */         this.this_mon.exit();
/*      */       }
/*      */       
/*  677 */       this.last_update_time_secs = (SystemTime.getCurrentTime() / 1000L);
/*      */       
/*  679 */       this.tracker_status_str = (MessageText.getString("PeerManager.status.checking") + "...");
/*      */       
/*  681 */       TRTrackerAnnouncerResponseImpl response = null;
/*      */       
/*  683 */       if (this.stopped)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  688 */         if ((this.tracker_state == 1) && (!this.manual_control))
/*      */         {
/*      */ 
/*      */ 
/*  692 */           this.tracker_state = 4;
/*      */         }
/*  694 */         else if (this.tracker_state != 4)
/*      */         {
/*  696 */           response = stopSupport();
/*      */           
/*  698 */           if (response.getStatus() == 2)
/*      */           {
/*  700 */             this.tracker_state = 4;
/*      */ 
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*  707 */             this.tracker_state = 4;
/*      */           }
/*      */         }
/*  710 */       } else if (this.tracker_state == 1)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  715 */         response = startSupport();
/*      */         
/*  717 */         if (response.getStatus() == 2)
/*      */         {
/*  719 */           this.tracker_state = 2;
/*      */         }
/*  721 */       } else if (this.completed)
/*      */       {
/*  723 */         if (!this.complete_reported)
/*      */         {
/*  725 */           response = completeSupport();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  732 */           if (response.getStatus() != 0)
/*      */           {
/*  734 */             this.complete_reported = true;
/*      */             
/*  736 */             this.tracker_state = 3;
/*      */           }
/*      */         } else {
/*  739 */           this.tracker_state = 3;
/*      */           
/*  741 */           response = updateSupport();
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  746 */         response = updateSupport();
/*      */       }
/*      */       
/*  749 */       if (response != null)
/*      */       {
/*  751 */         rs = response.getStatus();
/*      */         
/*  753 */         if (rs == 0)
/*      */         {
/*  755 */           this.tracker_status_str = MessageText.getString("PeerManager.status.offline");
/*      */         }
/*  757 */         else if (rs == 1)
/*      */         {
/*  759 */           this.tracker_status_str = MessageText.getString("PeerManager.status.error");
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  766 */           this.tracker_state = 1;
/*      */ 
/*      */ 
/*      */         }
/*  770 */         else if (this.announce_data_provider.isPeerSourceEnabled("Tracker"))
/*      */         {
/*  772 */           this.tracker_status_str = MessageText.getString("PeerManager.status.ok");
/*      */           
/*  774 */           if (response.wasProbe())
/*      */           {
/*  776 */             this.tracker_status_str = (this.tracker_status_str + " (" + MessageText.getString("label.udp_probe") + ")");
/*      */           }
/*      */         }
/*      */         else {
/*  780 */           this.tracker_status_str = MessageText.getString("PeerManager.status.ps_disabled");
/*      */           
/*  782 */           response.setPeers(new TRTrackerAnnouncerResponsePeerImpl[0]);
/*      */         }
/*      */         
/*      */ 
/*  786 */         String reason = response.getAdditionalInfo();
/*      */         
/*  788 */         if (reason != null) {
/*  789 */           this.tracker_status_str = (this.tracker_status_str + " (" + reason + ")");
/*      */         }
/*      */         
/*  792 */         this.last_response = response;
/*      */         
/*  794 */         this.helper.informResponse(this, response);
/*      */         
/*  796 */         return response.getTimeToWait();
/*      */       }
/*      */       
/*  799 */       this.tracker_status_str = "";
/*      */       
/*  801 */       return getErrorRetryInterval();
/*      */     }
/*      */     catch (Throwable e) {
/*      */       int rs;
/*  805 */       Debug.printStackTrace(e);
/*      */       
/*  807 */       return getErrorRetryInterval();
/*      */     }
/*      */     finally
/*      */     {
/*      */       try {
/*  812 */         this.this_mon.enter();
/*      */         
/*  814 */         if (clear_progress)
/*      */         {
/*  816 */           this.update_in_progress = false;
/*      */         }
/*      */       }
/*      */       finally {
/*  820 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected TRTrackerAnnouncerResponseImpl startSupport() {
/*  826 */     if (Logger.isEnabled()) {
/*  827 */       Logger.log(new LogEvent(this.torrent, LOGID, "Tracker Announcer is sending a start Request"));
/*      */     }
/*      */     
/*  830 */     return update("started");
/*      */   }
/*      */   
/*      */   protected TRTrackerAnnouncerResponseImpl completeSupport() {
/*  834 */     if (Logger.isEnabled()) {
/*  835 */       Logger.log(new LogEvent(this.torrent, LOGID, "Tracker Announcer is sending a completed Request"));
/*      */     }
/*      */     
/*  838 */     return update("completed");
/*      */   }
/*      */   
/*      */   protected TRTrackerAnnouncerResponseImpl stopSupport() {
/*  842 */     if (Logger.isEnabled()) {
/*  843 */       Logger.log(new LogEvent(this.torrent, LOGID, "Tracker Announcer is sending a stopped Request"));
/*      */     }
/*      */     
/*  846 */     return update("stopped");
/*      */   }
/*      */   
/*      */   protected TRTrackerAnnouncerResponseImpl updateSupport() {
/*  850 */     if (Logger.isEnabled()) {
/*  851 */       Logger.log(new LogEvent(this.torrent, LOGID, "Tracker Announcer is sending an update Request"));
/*      */     }
/*      */     
/*  854 */     return update("");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private TRTrackerAnnouncerResponseImpl update(String evt)
/*      */   {
/*  863 */     TRTrackerAnnouncerResponseImpl resp = update2(evt);
/*      */     
/*  865 */     TRTrackerAnnouncerResponsePeer[] peers = resp.getPeers();
/*      */     
/*  867 */     if (peers != null) {
/*  868 */       List p = new ArrayList();
/*      */       
/*  870 */       for (int i = 0; i < peers.length; i++)
/*      */       {
/*  872 */         TRTrackerAnnouncerResponsePeer peer = peers[i];
/*      */         
/*  874 */         if (this.peer_networks == null)
/*      */         {
/*  876 */           p.add(peer);
/*      */         }
/*      */         else
/*      */         {
/*  880 */           String peer_address = peer.getAddress();
/*      */           
/*  882 */           String peer_network = AENetworkClassifier.categoriseAddress(peer_address);
/*      */           
/*  884 */           boolean added = false;
/*      */           
/*  886 */           for (int j = 0; j < this.peer_networks.length; j++)
/*      */           {
/*  888 */             if (this.peer_networks[j] == peer_network)
/*      */             {
/*  890 */               p.add(peer);
/*      */               
/*  892 */               added = true;
/*      */               
/*  894 */               break;
/*      */             }
/*      */           }
/*      */           
/*  898 */           if ((!added) && (Logger.isEnabled())) {
/*  899 */             Logger.log(new LogEvent(this.torrent, LOGID, 1, "Tracker Announcer dropped peer '" + peer_address + "' as incompatible " + "with network selection"));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  905 */       peers = new TRTrackerAnnouncerResponsePeer[p.size()];
/*      */       
/*  907 */       p.toArray(peers);
/*      */       
/*  909 */       resp.setPeers(peers);
/*      */     }
/*      */     
/*  912 */     return resp;
/*      */   }
/*      */   
/*      */ 
/*      */   private TRTrackerAnnouncerResponseImpl update2(String evt)
/*      */   {
/*  918 */     TRTrackerAnnouncerResponseImpl last_failure_resp = null;
/*      */     
/*  920 */     String skip_host = null;
/*      */     
/*      */ 
/*      */ 
/*  924 */     for (int i = 0; i < this.trackerUrlLists.size(); i++)
/*      */     {
/*  926 */       List urls = (List)this.trackerUrlLists.get(i);
/*      */       
/*  928 */       for (int j = 0; j < urls.size(); j++)
/*      */       {
/*  930 */         URL original_url = (URL)urls.get(j);
/*      */         
/*  932 */         if ((skip_host != null) && (skip_host.equals(original_url.getHost())))
/*      */         {
/*  934 */           if (Logger.isEnabled()) {
/*  935 */             Logger.log(new LogEvent(this.torrent, LOGID, 1, "Tracker Announcer is ignoring '" + original_url + "' as already received overloaded response from this host"));
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  945 */           this.lastUsedUrl = original_url;
/*      */           
/*  947 */           if (this.lastUsedUrl != this.lastAZTrackerCheckedURL)
/*      */           {
/*  949 */             this.az_tracker = TRTrackerUtils.isAZTracker(this.lastUsedUrl);
/*      */           }
/*      */           
/*  952 */           URL request_url = null;
/*      */           
/*  954 */           if (last_failure_resp != null)
/*      */           {
/*      */ 
/*      */ 
/*  958 */             this.helper.informResponse(this, last_failure_resp);
/*      */           }
/*      */           
/*      */           try
/*      */           {
/*  963 */             request_url = constructUrl(evt, original_url);
/*      */             
/*  965 */             URL[] tracker_url = { original_url };
/*      */             
/*  967 */             int prev_udp_probes_ok = this.autoUDPProbeSuccessCount;
/*      */             
/*  969 */             byte[] result_bytes = updateOld(tracker_url, request_url);
/*      */             
/*  971 */             this.lastUsedUrl = tracker_url[0];
/*      */             
/*  973 */             TRTrackerAnnouncerResponseImpl resp = decodeTrackerResponse(this.lastUsedUrl, result_bytes);
/*      */             
/*  975 */             int resp_status = resp.getStatus();
/*      */             
/*  977 */             if (resp_status == 2)
/*      */             {
/*  979 */               if (this.autoUDPProbeSuccessCount > prev_udp_probes_ok)
/*      */               {
/*  981 */                 resp.setWasProbe();
/*      */               }
/*      */               
/*      */               try
/*      */               {
/*  986 */                 if (!original_url.toString().equals(this.lastUsedUrl.toString()))
/*      */                 {
/*  988 */                   if (Logger.isEnabled()) {
/*  989 */                     Logger.log(new LogEvent(this.torrent, LOGID, "announce url permanently redirected: old = " + original_url + ", new = " + this.lastUsedUrl));
/*      */                   }
/*      */                   
/*      */ 
/*  993 */                   TorrentUtils.replaceAnnounceURL(this.torrent, original_url, this.lastUsedUrl);
/*      */                 }
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  998 */                 Debug.printStackTrace(e);
/*      */               }
/*      */               
/* 1001 */               urls.remove(j);
/*      */               
/* 1003 */               urls.add(0, this.lastUsedUrl);
/*      */               
/* 1005 */               this.trackerUrlLists.remove(i);
/*      */               
/* 1007 */               this.trackerUrlLists.add(0, urls);
/*      */               
/* 1009 */               informURLChange(original_url, this.lastUsedUrl, false);
/*      */               
/*      */ 
/*      */ 
/* 1013 */               return resp;
/*      */             }
/* 1015 */             if (resp_status == 1)
/*      */             {
/* 1017 */               last_failure_resp = resp;
/*      */               
/* 1019 */               String reason = resp.getAdditionalInfo();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1027 */               if ((reason != null) && ((reason.contains("too many seeds")) || (reason.contains("too many peers"))))
/*      */               {
/*      */ 
/*      */ 
/* 1031 */                 skip_host = original_url.getHost();
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/* 1036 */               this.announceFailCount += 1;
/*      */               
/* 1038 */               last_failure_resp = resp;
/*      */             }
/*      */           }
/*      */           catch (MalformedURLException e)
/*      */           {
/* 1043 */             this.announceFailCount += 1;
/*      */             
/* 1045 */             Debug.printStackTrace(e);
/*      */             
/* 1047 */             last_failure_resp = new TRTrackerAnnouncerResponseImpl(original_url, this.torrent_hash, 0, getErrorRetryInterval(), "malformed URL '" + (request_url == null ? "<null>" : request_url.toString()) + "'");
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1057 */             this.announceFailCount += 1;
/*      */             
/* 1059 */             last_failure_resp = new TRTrackerAnnouncerResponseImpl(original_url, this.torrent_hash, 0, getErrorRetryInterval(), e.getMessage() == null ? e.toString() : e.getMessage());
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1068 */           if (this.destroyed) {
/*      */             break label639;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     label639:
/*      */     
/* 1077 */     if (last_failure_resp == null)
/*      */     {
/* 1079 */       last_failure_resp = new TRTrackerAnnouncerResponseImpl(null, this.torrent_hash, 0, getErrorRetryInterval(), "Reason Unknown");
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
/* 1092 */     int num_want = calculateNumWant() * 4;
/*      */     
/*      */ 
/* 1095 */     TRTrackerAnnouncerResponsePeer[] cached_peers = this.helper.getPeersFromCache(num_want);
/*      */     
/* 1097 */     if (cached_peers.length > 0)
/*      */     {
/*      */ 
/*      */ 
/* 1101 */       last_failure_resp.setPeers(cached_peers);
/*      */     }
/*      */     
/* 1104 */     return last_failure_resp;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] updateOld(URL[] tracker_url, URL reqUrl)
/*      */     throws Exception
/*      */   {
/* 1116 */     boolean errorLevel = true;
/*      */     try
/*      */     {
/* 1119 */       TorrentUtils.setTLSTorrentHash(this.torrent_hash);
/*      */       
/*      */ 
/*      */ 
/* 1123 */       for (int i = 0; i < 2; i++)
/*      */       {
/* 1125 */         String failure_reason = null;
/*      */         
/* 1127 */         String protocol = reqUrl.getProtocol();
/*      */         
/*      */         try
/*      */         {
/* 1131 */           if (Logger.isEnabled()) {
/* 1132 */             Logger.log(new LogEvent(this.torrent, LOGID, "Tracker Announcer is Requesting: " + reqUrl));
/*      */           }
/*      */           
/*      */ 
/* 1136 */           ByteArrayOutputStream message = new ByteArrayOutputStream();
/*      */           
/*      */ 
/* 1139 */           URL udpAnnounceURL = null;
/*      */           
/* 1141 */           boolean udp_probe = false;
/*      */           
/*      */ 
/*      */ 
/* 1145 */           if (protocol.equalsIgnoreCase("udp"))
/*      */           {
/* 1147 */             if (udpAnnounceEnabled)
/*      */             {
/* 1149 */               udpAnnounceURL = reqUrl;
/*      */             }
/*      */             else
/*      */             {
/* 1153 */               throw new IOException("UDP Tracker protocol disabled");
/*      */             }
/*      */           }
/* 1156 */           else if ((protocol.equalsIgnoreCase("http")) && (!this.az_tracker) && (!TorrentUtils.isReallyPrivate(this.torrent)) && (this.announceCount % this.autoUDPprobeEvery == 0) && (udpProbeEnabled) && (udpAnnounceEnabled))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1167 */             if (((!this.stopped) && (this.announceCount != 0) && ((this.announceCount >= this.trackerUrlLists.size()) || (this.announceFailCount != this.announceCount))) || (TRTrackerUtils.isUDPProbeOK(reqUrl)))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1176 */               String tracker_network = AENetworkClassifier.categoriseAddress(reqUrl.getHost());
/*      */               
/* 1178 */               if (tracker_network == "Public")
/*      */               {
/* 1180 */                 udpAnnounceURL = new URL(reqUrl.toString().replaceFirst("^http", "udp"));
/*      */                 
/* 1182 */                 udp_probe = true;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1187 */           if (udpAnnounceURL != null)
/*      */           {
/* 1189 */             failure_reason = announceUDP(reqUrl, message, udp_probe);
/*      */             
/* 1191 */             if (((failure_reason != null) || (message.size() == 0)) && (udp_probe))
/*      */             {
/*      */ 
/*      */ 
/* 1195 */               udpAnnounceURL = null;
/*      */               
/* 1197 */               if (this.autoUDPprobeEvery < 16)
/*      */               {
/* 1199 */                 this.autoUDPprobeEvery = ((byte)(this.autoUDPprobeEvery << 1));
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/* 1204 */                 TRTrackerUtils.setUDPProbeResult(reqUrl, false);
/*      */               }
/*      */               
/* 1207 */               if (Logger.isEnabled()) {
/* 1208 */                 Logger.log(new LogEvent(this.torrent, LOGID, 0, "redirection of http announce [" + tracker_url[0] + "] to udp failed, will retry in " + this.autoUDPprobeEvery + " announces"));
/*      */               }
/* 1210 */             } else if ((failure_reason == null) && (udp_probe))
/*      */             {
/* 1212 */               TRTrackerUtils.setUDPProbeResult(reqUrl, true);
/*      */               
/* 1214 */               if (Logger.isEnabled()) {
/* 1215 */                 Logger.log(new LogEvent(this.torrent, LOGID, 0, "redirection of http announce [" + tracker_url[0] + "] to udp successful"));
/*      */               }
/*      */               
/* 1218 */               this.autoUDPprobeEvery = 1;
/* 1219 */               this.autoUDPProbeSuccessCount += 1;
/*      */             }
/*      */           }
/*      */           
/* 1223 */           this.announceCount += 1;
/*      */           boolean failed;
/* 1225 */           if (udpAnnounceURL == null)
/*      */           {
/* 1227 */             failed = false;
/*      */             
/* 1229 */             if ((!this.az_tracker) && (!tcpAnnounceEnabled))
/*      */             {
/* 1231 */               String tracker_network = AENetworkClassifier.categoriseAddress(reqUrl.getHost());
/*      */               
/* 1233 */               if (tracker_network == "Public")
/*      */               {
/* 1235 */                 failure_reason = "HTTP Tracker protocol disabled";
/*      */                 
/* 1237 */                 failed = true;
/*      */               }
/*      */             }
/*      */             
/* 1241 */             if (!failed)
/*      */             {
/* 1243 */               failure_reason = announceHTTP(tracker_url, reqUrl, message, i == 0);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1249 */           if (message.size() > 0)
/*      */           {
/* 1251 */             return message.toByteArray();
/*      */           }
/*      */           
/* 1254 */           if (failure_reason == null)
/*      */           {
/* 1256 */             failure_reason = "No data received from tracker";
/*      */             
/* 1258 */             if (reqUrl.getProtocol().equalsIgnoreCase("udp"))
/*      */             {
/* 1260 */               errorLevel = false;
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         catch (SSLException e)
/*      */         {
/*      */ 
/* 1270 */           if (i == 0)
/*      */           {
/* 1272 */             if (SESecurityManager.installServerCertificates(reqUrl) != null) {
/*      */               continue;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1280 */             failure_reason = exceptionToString(e);
/*      */           }
/*      */           else
/*      */           {
/* 1284 */             failure_reason = exceptionToString(e);
/*      */           }
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/* 1289 */           if (((e instanceof UnknownHostException)) || ((e instanceof ConnectException)))
/*      */           {
/* 1291 */             errorLevel = false;
/*      */           }
/*      */           
/* 1294 */           if ((i == 0) && (protocol.toLowerCase().startsWith("http")))
/*      */           {
/* 1296 */             URL retry_url = UrlUtils.getIPV4Fallback(reqUrl);
/*      */             
/* 1298 */             if (retry_url != null)
/*      */             {
/* 1300 */               reqUrl = retry_url;
/*      */               
/* 1302 */               continue;
/*      */             }
/*      */           }
/*      */           
/* 1306 */           failure_reason = exceptionToString(e);
/*      */ 
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */ 
/* 1312 */           failure_reason = exceptionToString(e);
/*      */         }
/*      */         
/* 1315 */         if ((failure_reason != null) && (failure_reason.contains("401")))
/*      */         {
/* 1317 */           failure_reason = "Tracker authentication failed";
/* 1318 */           errorLevel = false;
/*      */         }
/*      */         
/* 1321 */         if (Logger.isEnabled()) {
/* 1322 */           Logger.log(new LogEvent(this.torrent, LOGID, errorLevel ? 3 : 1, "Exception while processing the Tracker Request for " + reqUrl + ": " + failure_reason));
/*      */         }
/*      */         
/*      */ 
/* 1326 */         throw new Exception(failure_reason);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1331 */       throw new Exception("Internal Error: should never get here");
/*      */     }
/*      */     finally
/*      */     {
/* 1335 */       TorrentUtils.setTLSTorrentHash(null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String announceHTTP(URL[] tracker_url, URL original_reqUrl, ByteArrayOutputStream message, boolean first_effort)
/*      */     throws Exception
/*      */   {
/*      */     try
/*      */     {
/* 1349 */       return announceHTTPSupport(tracker_url, original_reqUrl, null, first_effort, message);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1353 */       if ((first_effort) && (AENetworkClassifier.categoriseAddress(original_reqUrl.getHost()) != "Public"))
/*      */       {
/*      */ 
/* 1356 */         Map<String, Object> opts = new HashMap();
/*      */         
/* 1358 */         if (this.peer_networks != null)
/*      */         {
/* 1360 */           opts.put("peer_networks", this.peer_networks);
/*      */         }
/*      */         
/* 1363 */         AEProxyFactory.PluginProxy proxy = com.aelitis.azureus.core.proxy.AEProxyFactory.getPluginProxy("Tracker update", original_reqUrl, opts, true);
/*      */         
/* 1365 */         if (proxy != null)
/*      */         {
/* 1367 */           boolean ok = false;
/*      */           
/*      */           try
/*      */           {
/* 1371 */             String result = announceHTTPSupport(tracker_url, proxy.getURL(), proxy.getProxy(), first_effort, message);
/*      */             
/* 1373 */             ok = true;
/*      */             
/* 1375 */             return result;
/*      */ 
/*      */           }
/*      */           catch (Throwable f) {}finally
/*      */           {
/*      */ 
/* 1381 */             proxy.setOK(ok);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1386 */       throw e;
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
/*      */   private String announceHTTPSupport(URL[] tracker_url, URL original_reqUrl, Proxy proxy, boolean first_effort, ByteArrayOutputStream message)
/*      */     throws IOException
/*      */   {
/* 1400 */     TRTrackerUtils.checkForBlacklistedURLs(original_reqUrl);
/*      */     
/* 1402 */     URL reqUrl = TRTrackerUtils.adjustURLForHosting(original_reqUrl);
/*      */     
/* 1404 */     reqUrl = AddressUtils.adjustURL(reqUrl);
/*      */     
/* 1406 */     if ((reqUrl != original_reqUrl) && 
/* 1407 */       (Logger.isEnabled())) {
/* 1408 */       Logger.log(new LogEvent(this.torrent, LOGID, "    HTTP: url adjusted to " + reqUrl));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1413 */     failure_reason = null;
/*      */     
/*      */ 
/*      */ 
/* 1417 */     Properties http_properties = new Properties();
/*      */     
/* 1419 */     http_properties.put("URL", reqUrl);
/*      */     
/* 1421 */     if (proxy != null)
/*      */     {
/* 1423 */       http_properties.put("Proxy", proxy);
/*      */     }
/*      */     
/* 1426 */     if (this.enable_sni_hack)
/*      */     {
/* 1428 */       http_properties.put("SNI-Hack", Boolean.valueOf(true));
/*      */     }
/*      */     try
/*      */     {
/* 1432 */       ClientIDManagerImpl.getSingleton().generateHTTPProperties(this.torrent_hash.getBytes(), http_properties);
/*      */     }
/*      */     catch (ClientIDException e)
/*      */     {
/* 1436 */       throw new IOException(e.getMessage());
/*      */     }
/*      */     
/* 1439 */     reqUrl = (URL)http_properties.get("URL");
/*      */     
/* 1441 */     boolean is_https = reqUrl.getProtocol().equalsIgnoreCase("https");
/*      */     HttpURLConnection con;
/* 1443 */     HttpURLConnection con; if (is_https)
/*      */     {
/*      */       HttpsURLConnection ssl_con;
/*      */       
/*      */       HttpsURLConnection ssl_con;
/*      */       
/* 1449 */       if (proxy == null)
/*      */       {
/* 1451 */         ssl_con = (HttpsURLConnection)reqUrl.openConnection();
/*      */       }
/*      */       else
/*      */       {
/* 1455 */         ssl_con = (HttpsURLConnection)reqUrl.openConnection(proxy);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1461 */       if (!this.internal_error_hack)
/*      */       {
/* 1463 */         ssl_con.setHostnameVerifier(new javax.net.ssl.HostnameVerifier()
/*      */         {
/*      */ 
/*      */ 
/*      */           public boolean verify(String host, SSLSession session)
/*      */           {
/*      */ 
/*      */ 
/* 1471 */             return true;
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1476 */       if (this.dh_hack)
/*      */       {
/* 1478 */         UrlUtils.DHHackIt(ssl_con);
/*      */       }
/*      */       
/* 1481 */       if (!first_effort)
/*      */       {
/*      */ 
/*      */ 
/* 1485 */         javax.net.ssl.TrustManager[] trustAllCerts = SESecurityManager.getAllTrustingTrustManager();
/*      */         try
/*      */         {
/* 1488 */           SSLContext sc = SSLContext.getInstance("SSL");
/*      */           
/* 1490 */           sc.init(null, trustAllCerts, RandomUtils.SECURE_RANDOM);
/*      */           
/* 1492 */           javax.net.ssl.SSLSocketFactory factory = sc.getSocketFactory();
/*      */           
/* 1494 */           ssl_con.setSSLSocketFactory(factory);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/* 1500 */       con = ssl_con;
/*      */     }
/*      */     else {
/*      */       HttpURLConnection con;
/* 1504 */       if (proxy == null)
/*      */       {
/* 1506 */         con = (HttpURLConnection)reqUrl.openConnection();
/*      */       }
/*      */       else
/*      */       {
/* 1510 */         con = (HttpURLConnection)reqUrl.openConnection(proxy);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1517 */     con.setInstanceFollowRedirects(true);
/*      */     
/* 1519 */     String user_agent = (String)http_properties.get("User-Agent");
/*      */     
/* 1521 */     if (user_agent != null)
/*      */     {
/* 1523 */       con.setRequestProperty("User-Agent", user_agent);
/*      */     }
/*      */     
/* 1526 */     con.setRequestProperty("Connection", "close");
/*      */     
/*      */ 
/*      */ 
/* 1530 */     con.addRequestProperty("Accept-Encoding", "gzip");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*      */       try {}catch (AEProxyFactory.UnknownHostException e)
/*      */       {
/* 1539 */         throw new UnknownHostException(e.getMessage());
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
/*      */       String msg;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       InputStream is;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       String resulting_url_str;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       String marker;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       int pos;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       URL redirect_url;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       String encoding;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       boolean gzip;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       int content_length;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       byte[] data;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       int num_read;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       int len;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       label801:
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1702 */       return failure_reason;
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/* 1543 */       if (is_https)
/*      */       {
/* 1545 */         msg = Debug.getNestedExceptionMessage(e);
/*      */         
/* 1547 */         if (msg.contains("unrecognized_name"))
/*      */         {
/*      */ 
/*      */ 
/* 1551 */           this.enable_sni_hack = true;
/*      */         }
/* 1553 */         else if (msg.contains("internal_error"))
/*      */         {
/* 1555 */           this.internal_error_hack = true;
/*      */         }
/* 1557 */         else if (msg.contains("DH keypair"))
/*      */         {
/* 1559 */           this.dh_hack = true;
/*      */         }
/*      */       }
/*      */       
/* 1563 */       throw e;
/*      */       
/*      */ 
/* 1566 */       is = null;
/*      */       
/*      */       try
/*      */       {
/* 1570 */         is = con.getInputStream();
/*      */         
/* 1572 */         resulting_url_str = con.getURL().toString();
/*      */         
/* 1574 */         if (!reqUrl.toString().equals(resulting_url_str))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1580 */           marker = "permredirect=1";
/*      */           
/* 1582 */           pos = resulting_url_str.indexOf(marker);
/*      */           
/* 1584 */           if (pos != -1)
/*      */           {
/* 1586 */             pos -= 1;
/*      */             try
/*      */             {
/* 1589 */               redirect_url = new URL(resulting_url_str.substring(0, pos));
/*      */               
/*      */ 
/* 1592 */               tracker_url[0] = redirect_url;
/*      */             }
/*      */             catch (Throwable e) {
/* 1595 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1600 */         encoding = con.getHeaderField("content-encoding");
/*      */         
/* 1602 */         gzip = (encoding != null) && (encoding.equalsIgnoreCase("gzip"));
/*      */         
/*      */ 
/*      */ 
/* 1606 */         if (gzip)
/*      */         {
/* 1608 */           is = new GZIPInputStream(is);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1614 */         content_length = -1;
/*      */         
/*      */ 
/*      */ 
/* 1618 */         data = new byte['Ѐ'];
/*      */         
/* 1620 */         num_read = 0;
/*      */         
/*      */         for (;;)
/*      */         {
/* 1624 */           if ((content_length <= 0) || (num_read < content_length)) {
/*      */             try
/*      */             {
/* 1627 */               len = is.read(data);
/*      */               
/* 1629 */               if (len > 0)
/*      */               {
/* 1631 */                 message.write(data, 0, len);
/*      */                 
/* 1633 */                 num_read += len;
/*      */                 
/* 1635 */                 if (num_read > 131072)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1641 */                   throw new Exception("Tracker response invalid (too large)");
/*      */                 }
/*      */               }
/* 1644 */               else if (len == 0)
/*      */               {
/* 1646 */                 Thread.sleep(20L);
/*      */               }
/*      */               else
/*      */               {
/*      */                 break label801;
/*      */               }
/*      */             }
/*      */             catch (Exception e)
/*      */             {
/* 1655 */               if (Logger.isEnabled()) {
/* 1656 */                 Logger.log(new LogEvent(this.torrent, LOGID, "Exception while Requesting Tracker", e));
/*      */                 
/* 1658 */                 Logger.log(new LogEvent(this.torrent, LOGID, 3, "Message Received was : " + message));
/*      */               }
/*      */               
/*      */ 
/* 1662 */               failure_reason = exceptionToString(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1668 */         if (Logger.isEnabled()) {
/* 1669 */           Logger.log(new LogEvent(this.torrent, LOGID, "Tracker Announcer [" + this.lastUsedUrl + "] has received : " + message));
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       catch (SSLException e)
/*      */       {
/*      */ 
/* 1677 */         throw e;
/*      */ 
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */ 
/* 1683 */         failure_reason = exceptionToString(e);
/*      */       }
/*      */       finally
/*      */       {
/* 1687 */         if (is != null)
/*      */         {
/*      */           try {}catch (Exception e) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1695 */           is = null;
/*      */         }
/*      */       }
/*      */     } finally {
/* 1699 */       con.disconnect();
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
/*      */   protected String announceUDP(URL original_reqUrl, ByteArrayOutputStream message, boolean is_probe)
/*      */     throws IOException
/*      */   {
/* 1713 */     long timeout = is_probe ? 10000L : 30000L;
/*      */     
/* 1715 */     URL reqUrl = TRTrackerUtils.adjustURLForHosting(original_reqUrl);
/*      */     
/* 1717 */     if ((reqUrl != original_reqUrl) && 
/* 1718 */       (Logger.isEnabled())) {
/* 1719 */       Logger.log(new LogEvent(this.torrent, LOGID, "    UDP: url adjusted to " + reqUrl));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1724 */     String failure_reason = null;
/*      */     
/* 1726 */     java.net.PasswordAuthentication auth = null;
/*      */     try
/*      */     {
/* 1729 */       if ((!is_probe) && (UrlUtils.queryHasParameter(reqUrl.getQuery(), "auth", false))) {
/* 1730 */         auth = SESecurityManager.getPasswordAuthentication("UDP Tracker", reqUrl);
/*      */       }
/*      */       
/* 1733 */       PRUDPPacketHandler handler = com.aelitis.net.udp.uc.PRUDPPacketHandlerFactory.getHandler(UDPNetworkManager.getSingleton().getUDPNonDataListeningPortNumber());
/*      */       
/* 1735 */       InetSocketAddress destination = new InetSocketAddress(reqUrl.getHost(), reqUrl.getPort() == -1 ? 80 : reqUrl.getPort());
/*      */       
/* 1737 */       handler = handler.openSession(destination);
/*      */       
/*      */       try
/*      */       {
/* 1741 */         for (int retry_loop = 0; retry_loop < 1; retry_loop++)
/*      */         {
/*      */           try
/*      */           {
/* 1745 */             PRUDPPacket connect_request = new org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketRequestConnect();
/*      */             
/* 1747 */             PRUDPPacket reply = handler.sendAndReceive(auth, connect_request, destination, timeout);
/*      */             
/* 1749 */             if (reply.getAction() == 0)
/*      */             {
/* 1751 */               PRUDPPacketReplyConnect connect_reply = (PRUDPPacketReplyConnect)reply;
/*      */               
/* 1753 */               long my_connection = connect_reply.getConnectionId();
/*      */               
/*      */               PRUDPPacketRequest request;
/*      */               
/* 1757 */               if (PRUDPPacketTracker.VERSION == 1)
/*      */               {
/* 1759 */                 PRUDPPacketRequestAnnounce announce_request = new PRUDPPacketRequestAnnounce(my_connection);
/*      */                 
/* 1761 */                 PRUDPPacketRequest request = announce_request;
/*      */                 
/*      */ 
/*      */ 
/* 1765 */                 String url_str = reqUrl.toString();
/*      */                 
/* 1767 */                 int p_pos = url_str.indexOf("?");
/*      */                 
/* 1769 */                 url_str = url_str.substring(p_pos + 1);
/*      */                 
/* 1771 */                 String event_str = getURLParam(url_str, "event");
/*      */                 
/* 1773 */                 int event = 0;
/*      */                 
/* 1775 */                 if (event_str != null)
/*      */                 {
/* 1777 */                   if (event_str.equals("started"))
/*      */                   {
/* 1779 */                     event = 2;
/*      */                   }
/* 1781 */                   else if (event_str.equals("stopped"))
/*      */                   {
/* 1783 */                     event = 3;
/*      */                   }
/* 1785 */                   else if (event_str.equals("completed"))
/*      */                   {
/* 1787 */                     event = 1;
/*      */                   }
/*      */                 }
/*      */                 
/* 1791 */                 String ip_str = getURLParam(url_str, "ip");
/*      */                 
/* 1793 */                 int ip = 0;
/*      */                 
/* 1795 */                 if (ip_str != null)
/*      */                 {
/* 1797 */                   ip = PRHelpers.addressToInt(ip_str);
/*      */                 }
/*      */                 
/* 1800 */                 announce_request.setDetails(this.torrent_hash.getBytes(), this.tracker_peer_id, getLongURLParam(url_str, "downloaded"), event, ip, (int)getLongURLParam(url_str, "numwant"), getLongURLParam(url_str, "left"), (short)(int)getLongURLParam(url_str, "port"), getLongURLParam(url_str, "uploaded"));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1812 */                 PRUDPPacketRequestAnnounce2 announce_request = new PRUDPPacketRequestAnnounce2(my_connection);
/*      */                 
/* 1814 */                 request = announce_request;
/*      */                 
/*      */ 
/*      */ 
/* 1818 */                 String url_str = reqUrl.toString();
/*      */                 
/* 1820 */                 int p_pos = url_str.indexOf("?");
/*      */                 
/* 1822 */                 url_str = url_str.substring(p_pos + 1);
/*      */                 
/* 1824 */                 String event_str = getURLParam(url_str, "event");
/*      */                 
/* 1826 */                 int event = 0;
/*      */                 
/* 1828 */                 if (event_str != null)
/*      */                 {
/* 1830 */                   if (event_str.equals("started"))
/*      */                   {
/* 1832 */                     event = 2;
/*      */                   }
/* 1834 */                   else if (event_str.equals("stopped"))
/*      */                   {
/* 1836 */                     event = 3;
/*      */                   }
/* 1838 */                   else if (event_str.equals("completed"))
/*      */                   {
/* 1840 */                     event = 1;
/*      */                   }
/*      */                 }
/*      */                 
/* 1844 */                 String ip_str = getURLParam(url_str, "ip");
/*      */                 
/* 1846 */                 int ip = 0;
/*      */                 
/* 1848 */                 if (ip_str != null)
/*      */                 {
/* 1850 */                   ip = PRHelpers.addressToInt(ip_str);
/*      */                 }
/*      */                 
/* 1853 */                 announce_request.setDetails(this.torrent_hash.getBytes(), this.tracker_peer_id, getLongURLParam(url_str, "downloaded"), event, ip, this.helper.getUDPKey(), (int)getLongURLParam(url_str, "numwant"), getLongURLParam(url_str, "left"), (short)(int)getLongURLParam(url_str, "port"), getLongURLParam(url_str, "uploaded"));
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
/*      */ 
/*      */ 
/* 1866 */               reply = handler.sendAndReceive(auth, request, destination);
/*      */               
/* 1868 */               if (reply.getAction() == 1)
/*      */               {
/* 1870 */                 if (auth != null)
/*      */                 {
/* 1872 */                   SESecurityManager.setPasswordAuthenticationOutcome("UDP Tracker", reqUrl, true);
/*      */                 }
/*      */                 
/* 1875 */                 if (PRUDPPacketTracker.VERSION == 1) {
/* 1876 */                   PRUDPPacketReplyAnnounce announce_reply = (PRUDPPacketReplyAnnounce)reply;
/*      */                   
/* 1878 */                   Map map = new HashMap();
/*      */                   
/* 1880 */                   map.put("interval", new Long(announce_reply.getInterval()));
/*      */                   
/* 1882 */                   int[] addresses = announce_reply.getAddresses();
/* 1883 */                   short[] ports = announce_reply.getPorts();
/*      */                   
/* 1885 */                   List peers = new ArrayList();
/*      */                   
/* 1887 */                   map.put("peers", peers);
/*      */                   Map peer;
/* 1889 */                   for (int i = 0; i < addresses.length; i++)
/*      */                   {
/* 1891 */                     peer = new HashMap();
/*      */                     
/* 1893 */                     peers.add(peer);
/*      */                     
/* 1895 */                     peer.put("ip", PRHelpers.intToAddress(addresses[i]).getBytes());
/* 1896 */                     peer.put("port", new Long(ports[i]));
/*      */                   }
/*      */                   
/* 1899 */                   byte[] data = BEncoder.encode(map);
/*      */                   
/* 1901 */                   message.write(data);
/*      */                   
/* 1903 */                   return null;
/*      */                 }
/*      */                 
/*      */ 
/* 1907 */                 PRUDPPacketReplyAnnounce2 announce_reply = (PRUDPPacketReplyAnnounce2)reply;
/*      */                 
/* 1909 */                 Map map = new HashMap();
/*      */                 
/* 1911 */                 map.put("interval", new Long(announce_reply.getInterval()));
/*      */                 
/* 1913 */                 int[] addresses = announce_reply.getAddresses();
/* 1914 */                 short[] ports = announce_reply.getPorts();
/*      */                 
/* 1916 */                 map.put("complete", new Long(announce_reply.getSeeders()));
/* 1917 */                 map.put("incomplete", new Long(announce_reply.getLeechers()));
/*      */                 
/* 1919 */                 List peers = new ArrayList();
/*      */                 
/* 1921 */                 map.put("peers", peers);
/*      */                 Map peer;
/* 1923 */                 for (int i = 0; i < addresses.length; i++)
/*      */                 {
/* 1925 */                   peer = new HashMap();
/*      */                   
/* 1927 */                   peers.add(peer);
/*      */                   
/* 1929 */                   peer.put("ip", PRHelpers.intToAddress(addresses[i]).getBytes());
/* 1930 */                   peer.put("port", new Long(ports[i]));
/*      */                 }
/*      */                 
/* 1933 */                 byte[] data = BEncoder.encode(map);
/*      */                 
/* 1935 */                 message.write(data);
/*      */                 
/* 1937 */                 return null;
/*      */               }
/*      */               
/*      */ 
/* 1941 */               failure_reason = ((PRUDPPacketReplyError)reply).getMessage();
/*      */             }
/*      */             else
/*      */             {
/* 1945 */               failure_reason = ((PRUDPPacketReplyError)reply).getMessage();
/*      */             }
/*      */           }
/*      */           catch (PRUDPPacketHandlerException e) {
/* 1949 */             if ((e.getMessage() == null) || (!e.getMessage().contains("timed out")))
/*      */             {
/*      */ 
/* 1952 */               throw e;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/* 1958 */         handler.closeSession();
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1963 */       failure_reason = exceptionToString(e);
/*      */     }
/*      */     
/* 1966 */     if (auth != null)
/*      */     {
/* 1968 */       SESecurityManager.setPasswordAuthenticationOutcome("UDP Tracker", reqUrl, false);
/*      */     }
/*      */     
/* 1971 */     return failure_reason;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long getLongURLParam(String url, String param)
/*      */   {
/* 1979 */     String val = getURLParam(url, param);
/*      */     
/* 1981 */     if (val == null)
/*      */     {
/* 1983 */       return 0L;
/*      */     }
/*      */     
/* 1986 */     return Long.parseLong(val);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getURLParam(String url, String param)
/*      */   {
/* 1994 */     int p1 = url.indexOf(param + "=");
/*      */     
/* 1996 */     if (p1 == -1)
/*      */     {
/* 1998 */       return null;
/*      */     }
/*      */     
/* 2001 */     int p2 = url.indexOf("&", p1);
/*      */     
/* 2003 */     if (p2 == -1)
/*      */     {
/* 2005 */       return url.substring(p1 + param.length() + 1);
/*      */     }
/*      */     
/* 2008 */     return url.substring(p1 + param.length() + 1, p2);
/*      */   }
/*      */   
/*      */ 
/*      */   protected String exceptionToString(Throwable e)
/*      */   {
/*      */     String str;
/*      */     
/*      */     String str;
/* 2017 */     if ((e instanceof BoringException))
/*      */     {
/* 2019 */       str = Debug.getNestedExceptionMessage(e);
/*      */     }
/*      */     else
/*      */     {
/* 2023 */       String class_name = e.getClass().getName();
/*      */       
/* 2025 */       int pos = class_name.lastIndexOf('.');
/*      */       
/* 2027 */       if (pos != -1)
/*      */       {
/* 2029 */         class_name = class_name.substring(pos + 1);
/*      */       }
/*      */       
/* 2032 */       pos = class_name.indexOf('$');
/*      */       
/* 2034 */       if (pos != -1)
/*      */       {
/* 2036 */         class_name = class_name.substring(pos + 1);
/*      */       }
/*      */       
/* 2039 */       str = class_name + ": " + Debug.getNestedExceptionMessage(e);
/*      */     }
/*      */     
/* 2042 */     if (str.contains("timed out"))
/*      */     {
/* 2044 */       str = "timeout";
/*      */     }
/*      */     
/* 2047 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public URL constructUrl(String evt, URL _url)
/*      */     throws Exception
/*      */   {
/* 2057 */     String url = _url.toString();
/*      */     
/* 2059 */     StringBuffer request = new StringBuffer(url);
/*      */     
/*      */ 
/* 2062 */     if (url.indexOf('?') != -1) {
/* 2063 */       request.append('&');
/*      */     } else {
/* 2065 */       request.append('?');
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2071 */     request.append(this.info_hash);
/* 2072 */     request.append(this.tracker_peer_id_str);
/*      */     
/* 2074 */     String port_details = this.announce_data_provider.getCryptoLevel() == 1 ? TRTrackerUtils.getPortsForURLFullCrypto() : TRTrackerUtils.getPortsForURL();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2079 */     request.append(port_details);
/* 2080 */     request.append("&uploaded=").append(this.announce_data_provider.getTotalSent());
/* 2081 */     request.append("&downloaded=").append(this.announce_data_provider.getTotalReceived());
/*      */     
/* 2083 */     if (org.gudy.azureus2.core3.util.Constants.DOWNLOAD_SOURCES_PRETEND_COMPLETE)
/*      */     {
/* 2085 */       request.append("&left=0");
/*      */     }
/*      */     else
/*      */     {
/* 2089 */       request.append("&left=").append(this.announce_data_provider.getRemaining());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2094 */     request.append("&corrupt=").append(this.announce_data_provider.getFailedHashCheck());
/*      */     
/*      */ 
/*      */ 
/* 2098 */     if (this.tracker_id.length() > 0) {
/* 2099 */       request.append("&trackerid=").append(this.tracker_id);
/*      */     }
/*      */     
/*      */ 
/* 2103 */     if (evt.length() != 0) {
/* 2104 */       request.append("&event=").append(evt);
/*      */     }
/*      */     
/* 2107 */     boolean stopped = evt.equals("stopped");
/*      */     
/* 2109 */     if (stopped)
/*      */     {
/* 2111 */       request.append("&numwant=0");
/*      */       
/* 2113 */       if (this.stopped_for_queue)
/*      */       {
/* 2115 */         request.append("&azq=1");
/*      */       }
/*      */       
/*      */     }
/*      */     else
/*      */     {
/* 2121 */       int numwant = Math.min(calculateNumWant(), userMaxNumwant);
/*      */       
/*      */ 
/* 2124 */       request.append("&numwant=").append(numwant);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2131 */     request.append("&no_peer_id=1");
/*      */     
/* 2133 */     String tracker_network = AENetworkClassifier.categoriseAddress(_url.getHost());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2139 */     request.append("&compact=1");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2144 */     String explicit_ips = COConfigurationManager.getStringParameter("Override Ip", "");
/*      */     
/* 2146 */     String ip = null;
/*      */     
/*      */ 
/*      */ 
/* 2150 */     boolean network_ok = false;
/* 2151 */     boolean normal_network_ok = false;
/*      */     
/* 2153 */     if (this.peer_networks == null)
/*      */     {
/* 2155 */       network_ok = true;
/* 2156 */       normal_network_ok = true;
/*      */     }
/*      */     else {
/* 2159 */       for (int i = 0; i < this.peer_networks.length; i++)
/*      */       {
/* 2161 */         if (this.peer_networks[i] == "Public")
/*      */         {
/* 2163 */           normal_network_ok = true;
/*      */         }
/*      */         
/* 2166 */         if (this.peer_networks[i] == tracker_network)
/*      */         {
/* 2168 */           network_ok = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2173 */     if (!network_ok)
/*      */     {
/* 2175 */       throw new Exception("Network not enabled for url '" + _url + "'");
/*      */     }
/*      */     
/* 2178 */     String normal_explicit = null;
/*      */     
/* 2180 */     if (explicit_ips.length() > 0)
/*      */     {
/*      */ 
/*      */ 
/* 2184 */       StringTokenizer tok = new StringTokenizer(explicit_ips, ";");
/*      */       
/* 2186 */       while (tok.hasMoreTokens())
/*      */       {
/* 2188 */         String this_address = tok.nextToken().trim();
/*      */         
/* 2190 */         if (this_address.length() > 0)
/*      */         {
/* 2192 */           String cat = AENetworkClassifier.categoriseAddress(this_address);
/*      */           
/* 2194 */           if (cat == "Public")
/*      */           {
/* 2196 */             normal_explicit = this_address;
/*      */           }
/*      */           
/* 2199 */           if (tracker_network == cat)
/*      */           {
/* 2201 */             ip = this_address;
/*      */             
/* 2203 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2209 */     if (ip == null)
/*      */     {
/*      */ 
/*      */ 
/* 2213 */       if ((normal_network_ok) && (normal_explicit != null))
/*      */       {
/* 2215 */         ip = normal_explicit;
/*      */ 
/*      */ 
/*      */       }
/* 2219 */       else if ((this.ip_override != null) && (!TorrentUtils.isDecentralised(this.ip_override)))
/*      */       {
/* 2221 */         ip = this.ip_override;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2226 */     if (ip != null)
/*      */     {
/* 2228 */       if (tracker_network == "Public") {
/*      */         try
/*      */         {
/* 2231 */           ip = PRHelpers.DNSToIPAddress(ip);
/*      */         }
/*      */         catch (UnknownHostException e)
/*      */         {
/* 2235 */           if (Logger.isEnabled()) {
/* 2236 */             Logger.log(new LogEvent(this.torrent, LOGID, 3, "IP Override host resolution of '" + ip + "' fails, using unresolved address"));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2242 */       request.append("&ip=").append(ip);
/*      */     }
/*      */     
/* 2245 */     if (COConfigurationManager.getBooleanParameter("Tracker Key Enable Client", true))
/*      */     {
/* 2247 */       request.append("&key=").append(this.helper.getTrackerKey());
/*      */     }
/*      */     
/* 2250 */     String ext = this.announce_data_provider.getExtensions();
/*      */     
/* 2252 */     if (ext != null)
/*      */     {
/*      */ 
/*      */ 
/* 2256 */       while (ext.startsWith("&"))
/*      */       {
/* 2258 */         ext = ext.substring(1);
/*      */       }
/*      */       
/* 2261 */       request.append("&");
/*      */       
/* 2263 */       request.append(ext);
/*      */     }
/*      */     
/* 2266 */     request.append("&azver=3");
/*      */     
/* 2268 */     if ((this.az_tracker) && (!stopped))
/*      */     {
/* 2270 */       int up = this.announce_data_provider.getUploadSpeedKBSec(evt.equals("started"));
/*      */       
/* 2272 */       if (up > 0)
/*      */       {
/* 2274 */         request.append("&azup=").append(up);
/*      */       }
/*      */       
/* 2277 */       String as = NetworkAdmin.getSingleton().getCurrentASN().getAS();
/*      */       
/* 2279 */       if (as.length() > 0)
/*      */       {
/* 2281 */         request.append("&azas=").append(URLEncoder.encode(as, "UTF8"));
/*      */       }
/*      */       
/* 2284 */       com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition best_position = DHTNetworkPositionManager.getBestLocalPosition();
/*      */       
/* 2286 */       if (best_position != null) {
/*      */         try
/*      */         {
/* 2289 */           byte[] bytes = DHTNetworkPositionManager.serialisePosition(best_position);
/*      */           
/* 2291 */           request.append("&aznp=").append(Base32.encode(bytes));
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2295 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2302 */     if (tracker_network == "I2P")
/*      */     {
/* 2304 */       String temp = request.toString();
/*      */       
/* 2306 */       int pos = temp.indexOf('?');
/*      */       
/* 2308 */       String head = temp.substring(0, pos);
/* 2309 */       String tail = temp.substring(pos + 1);
/*      */       
/* 2311 */       String[] bits = tail.split("&");
/*      */       
/* 2313 */       Map<String, String> map = new HashMap();
/*      */       
/* 2315 */       for (String bit : bits)
/*      */       {
/* 2317 */         String[] arg = bit.split("=");
/*      */         
/* 2319 */         map.put(arg[0], arg[1]);
/*      */       }
/*      */       
/* 2322 */       tail = "";
/*      */       
/* 2324 */       for (String str : new String[] { "info_hash", "peer_id", "port", "ip", "uploaded", "downloaded", "left", "compact", "event", "numwant" })
/*      */       {
/* 2326 */         String val = (String)map.get(str);
/*      */         
/* 2328 */         if (val != null)
/*      */         {
/* 2330 */           tail = tail + (tail.length() == 0 ? "" : "&") + str + "=" + (String)map.get(str);
/*      */         }
/*      */       }
/*      */       
/* 2334 */       request = new StringBuffer(head + "?" + tail);
/*      */     }
/*      */     
/* 2337 */     return new URL(request.toString());
/*      */   }
/*      */   
/*      */ 
/*      */   protected int calculateNumWant()
/*      */   {
/* 2343 */     if (!this.announce_data_provider.isPeerSourceEnabled("Tracker"))
/*      */     {
/* 2345 */       return 0;
/*      */     }
/*      */     
/* 2348 */     int MAX_PEERS = 100;
/*      */     
/*      */ 
/*      */ 
/* 2352 */     int maxAllowed = 3 * this.announce_data_provider.getMaxNewConnectionsAllowed("") / 2;
/*      */     
/* 2354 */     if ((maxAllowed < 0) || (maxAllowed > MAX_PEERS)) {
/* 2355 */       maxAllowed = MAX_PEERS;
/*      */     }
/*      */     
/* 2358 */     return maxAllowed;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getPeerId()
/*      */   {
/* 2364 */     return this.data_peer_id;
/*      */   }
/*      */   
/*      */ 
/*      */   public void setAnnounceDataProvider(TRTrackerAnnouncerDataProvider _provider)
/*      */   {
/*      */     try
/*      */     {
/* 2372 */       this.this_mon.enter();
/*      */       
/* 2374 */       this.announce_data_provider = _provider;
/*      */     }
/*      */     finally
/*      */     {
/* 2378 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TOTorrent getTorrent()
/*      */   {
/* 2385 */     return this.torrent;
/*      */   }
/*      */   
/*      */ 
/*      */   public URL getTrackerURL()
/*      */   {
/* 2391 */     return this.lastUsedUrl;
/*      */   }
/*      */   
/*      */ 
/*      */   public void setTrackerURL(URL new_url)
/*      */   {
/*      */     try
/*      */     {
/* 2399 */       new_url = new URL(new_url.toString().replaceAll(" ", ""));
/*      */       
/* 2401 */       List list = new ArrayList(1);
/*      */       
/* 2403 */       list.add(new_url);
/*      */       
/* 2405 */       this.trackerUrlLists.clear();
/*      */       
/* 2407 */       this.trackerUrlLists.add(list);
/*      */       
/* 2409 */       informURLChange(this.lastUsedUrl, new_url, true);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2413 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TOTorrentAnnounceURLSet[] getAnnounceSets()
/*      */   {
/* 2420 */     return this.announce_urls;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void resetTrackerUrl(boolean shuffle)
/*      */   {
/* 2427 */     String old_list = trackerURLListToString();
/*      */     
/* 2429 */     constructTrackerUrlLists(shuffle);
/*      */     
/* 2431 */     if (this.trackerUrlLists.size() == 0)
/*      */     {
/* 2433 */       return;
/*      */     }
/*      */     
/* 2436 */     if (!old_list.equals(trackerURLListToString()))
/*      */     {
/* 2438 */       URL first_url = (URL)((List)this.trackerUrlLists.get(0)).get(0);
/*      */       
/* 2440 */       informURLChange(this.lastUsedUrl, first_url, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void refreshListeners()
/*      */   {
/* 2447 */     informURLRefresh();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setIPOverride(String override)
/*      */   {
/* 2454 */     this.ip_override = override;
/*      */   }
/*      */   
/*      */ 
/*      */   public void clearIPOverride()
/*      */   {
/* 2460 */     this.ip_override = null;
/*      */   }
/*      */   
/*      */ 
/*      */   private void constructTrackerUrlLists(boolean shuffle)
/*      */   {
/*      */     try
/*      */     {
/* 2468 */       this.trackerUrlLists = new ArrayList(1);
/*      */       
/*      */ 
/*      */ 
/* 2472 */       if (this.announce_urls.length == 0)
/*      */       {
/*      */ 
/*      */ 
/* 2476 */         URL url = this.torrent.getAnnounceURL();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2481 */         List list = new ArrayList();
/*      */         
/* 2483 */         list.add(url);
/*      */         
/* 2485 */         this.trackerUrlLists.add(list);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2490 */         for (int i = 0; i < this.announce_urls.length; i++)
/*      */         {
/*      */ 
/*      */ 
/* 2494 */           URL[] urls = this.announce_urls[i].getAnnounceURLs();
/*      */           
/* 2496 */           List random_urls = new ArrayList();
/*      */           
/* 2498 */           for (int j = 0; j < urls.length; j++)
/*      */           {
/*      */ 
/*      */ 
/* 2502 */             URL url = urls[j];
/*      */             
/*      */ 
/*      */ 
/* 2506 */             int pos = shuffle ? (int)(Math.random() * (random_urls.size() + 1)) : j;
/*      */             
/* 2508 */             random_urls.add(pos, url);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 2513 */           this.trackerUrlLists.add(random_urls);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/* 2518 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected String trackerURLListToString()
/*      */   {
/* 2525 */     String trackerUrlListString = "[";
/*      */     
/* 2527 */     for (int i = 0; i < this.trackerUrlLists.size(); i++)
/*      */     {
/* 2529 */       List group = (List)this.trackerUrlLists.get(i);
/*      */       
/* 2531 */       trackerUrlListString = trackerUrlListString + (i == 0 ? "" : ",") + "[";
/*      */       
/* 2533 */       for (int j = 0; j < group.size(); j++)
/*      */       {
/* 2535 */         URL u = (URL)group.get(j);
/*      */         
/* 2537 */         trackerUrlListString = trackerUrlListString + (j == 0 ? "" : ",") + u.toString();
/*      */       }
/*      */       
/* 2540 */       trackerUrlListString = trackerUrlListString + "]";
/*      */     }
/*      */     
/* 2543 */     trackerUrlListString = trackerUrlListString + "]";
/*      */     
/* 2545 */     return trackerUrlListString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected TRTrackerAnnouncerResponseImpl decodeTrackerResponse(URL url, byte[] data)
/*      */   {
/*      */     String failure_reason;
/*      */     
/*      */ 
/* 2555 */     if (data == null)
/*      */     {
/* 2557 */       failure_reason = "no response";
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/*      */ 
/* 2564 */         Map metaData = null;
/*      */         for (;;) {
/*      */           String failure_reason;
/* 2567 */           try { metaData = org.gudy.azureus2.core3.util.BDecoder.decode(data);
/*      */             
/*      */ 
/*      */ 
/* 2571 */             Object o = metaData.get("az_ps");
/*      */             
/* 2573 */             if ((o instanceof List))
/*      */             {
/* 2575 */               List peer_sources = (List)o;
/*      */               
/* 2577 */               List x = new ArrayList();
/*      */               
/* 2579 */               for (int i = 0; i < peer_sources.size(); i++)
/*      */               {
/* 2581 */                 Object o1 = peer_sources.get(i);
/*      */                 
/* 2583 */                 if ((o1 instanceof byte[]))
/*      */                 {
/* 2585 */                   x.add(new String((byte[])o1));
/*      */                 }
/*      */               }
/*      */               
/* 2589 */               String[] y = new String[x.size()];
/*      */               
/* 2591 */               x.toArray(y);
/*      */               
/* 2593 */               this.announce_data_provider.setPeerSources(y);
/*      */             }
/*      */             
/*      */ 
/*      */             try
/*      */             {
/* 2599 */               byte[] b_warning_message = (byte[])metaData.get("warning message");
/*      */               
/* 2601 */               if ((b_warning_message != null) && (COConfigurationManager.getBooleanParameter("Tracker Client Show Warnings")))
/*      */               {
/*      */ 
/* 2604 */                 String warning_message = new String(b_warning_message);
/*      */                 
/*      */ 
/*      */ 
/* 2608 */                 if (!warning_message.equals(this.last_tracker_message))
/*      */                 {
/* 2610 */                   this.last_tracker_message = warning_message;
/*      */                   
/* 2612 */                   boolean log_it = false;
/*      */                   
/*      */ 
/*      */                   try
/*      */                   {
/* 2617 */                     class_mon.enter();
/*      */                     
/* 2619 */                     String last_warning_message = (String)tracker_report_map.get(url.getHost());
/*      */                     
/* 2621 */                     if ((last_warning_message == null) || (!warning_message.equals(last_warning_message)))
/*      */                     {
/*      */ 
/* 2624 */                       log_it = true;
/*      */                       
/* 2626 */                       tracker_report_map.put(url.getHost(), warning_message);
/*      */                     }
/*      */                   }
/*      */                   finally {
/* 2630 */                     class_mon.exit();
/*      */                   }
/*      */                   
/* 2633 */                   if (log_it) {
/* 2634 */                     Logger.logTextResource(new org.gudy.azureus2.core3.logging.LogAlert(this.torrent, false, 1, "TrackerClient.announce.warningmessage"), new String[] { this.announce_data_provider.getName(), warning_message });
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2643 */               Debug.printStackTrace(e);
/*      */             }
/*      */             
/*      */             long time_to_wait;
/*      */             try
/*      */             {
/* 2649 */               if (!metaData.containsKey("interval")) {
/* 2650 */                 throw new Exception("interval missing");
/*      */               }
/*      */               
/* 2653 */               this.tracker_interval = (time_to_wait = ((Long)metaData.get("interval")).longValue());
/*      */               
/* 2655 */               Long raw_min_interval = (Long)metaData.get("min interval");
/*      */               
/* 2657 */               if (Logger.isEnabled()) {
/* 2658 */                 Logger.log(new LogEvent(this.torrent, LOGID, 0, "Received from announce: 'interval' = " + time_to_wait + "; 'min interval' = " + raw_min_interval));
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 2664 */               if ((time_to_wait < 0L) || (time_to_wait > 4294967295L)) {
/* 2665 */                 time_to_wait = 4294967295L;
/*      */               }
/*      */               
/* 2668 */               if (raw_min_interval != null) {
/* 2669 */                 this.tracker_min_interval = (this.min_interval = raw_min_interval.longValue());
/*      */                 
/*      */ 
/*      */ 
/* 2673 */                 if (this.min_interval < 1L) {
/* 2674 */                   if (Logger.isEnabled()) {
/* 2675 */                     Logger.log(new LogEvent(this.torrent, LOGID, 0, "Tracker being silly and returning a 'min interval' of less than 1 second (" + this.min_interval + ")"));
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2683 */                   this.min_interval = 0L;
/* 2684 */                 } else if (this.min_interval > time_to_wait) {
/* 2685 */                   if (Logger.isEnabled()) {
/* 2686 */                     Logger.log(new LogEvent(this.torrent, LOGID, 0, "Tracker being silly and returning a 'min interval' (" + this.min_interval + ") greater than recommended announce 'interval'" + " (" + time_to_wait + ")"));
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2696 */                   this.min_interval = 0L;
/*      */                 }
/*      */                 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/* 2703 */                 this.min_interval = (time_to_wait > 30L ? time_to_wait - 10L : time_to_wait);
/*      */               }
/*      */               
/* 2706 */               if (userMinInterval != 0)
/*      */               {
/* 2708 */                 time_to_wait = Math.max(userMinInterval, time_to_wait);
/* 2709 */                 this.min_interval = Math.max(this.min_interval, userMinInterval);
/* 2710 */                 if (Logger.isEnabled()) {
/* 2711 */                   Logger.log(new LogEvent(this.torrent, LOGID, 0, "Overriding with user settings: 'interval' = " + time_to_wait + "; 'min interval' = " + this.min_interval));
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2721 */               if (time_to_wait > 30L) {
/* 2722 */                 time_to_wait -= 10L;
/*      */               }
/*      */             }
/*      */             catch (Exception e) {
/* 2726 */               byte[] failure_reason_bytes = (byte[])metaData.get("failure reason");
/*      */               
/* 2728 */               if (failure_reason_bytes == null)
/*      */               {
/* 2730 */                 if (Logger.isEnabled()) {
/* 2731 */                   Logger.log(new LogEvent(this.torrent, LOGID, 1, "Problems with Tracker, will retry in " + getErrorRetryInterval() + "ms"));
/*      */                 }
/*      */                 
/*      */ 
/* 2735 */                 return new TRTrackerAnnouncerResponseImpl(url, this.torrent_hash, 0, getErrorRetryInterval(), "Unknown cause");
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 2741 */               String failure_reason = new String(failure_reason_bytes, "UTF8");
/*      */               
/* 2743 */               return new TRTrackerAnnouncerResponseImpl(url, this.torrent_hash, 1, getErrorRetryInterval(), failure_reason);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2749 */             Long incomplete_l = getLong(metaData, "incomplete");
/* 2750 */             Long complete_l = getLong(metaData, "complete");
/* 2751 */             Long downloaded_l = getLong(metaData, "downloaded");
/*      */             
/* 2753 */             if ((incomplete_l != null) || (complete_l != null))
/*      */             {
/* 2755 */               if (Logger.isEnabled()) {
/* 2756 */                 Logger.log(new LogEvent(this.torrent, LOGID, "ANNOUNCE SCRAPE1: seeds=" + complete_l + " peers=" + incomplete_l));
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2766 */             byte[] trackerid = (byte[])metaData.get("tracker id");
/* 2767 */             if (trackerid != null) {
/* 2768 */               this.tracker_id = new String(trackerid);
/*      */             }
/*      */             
/* 2771 */             byte[] crypto_flags = (byte[])metaData.get("crypto_flags");
/*      */             
/*      */ 
/* 2774 */             List valid_meta_peers = new ArrayList();
/*      */             
/* 2776 */             Object meta_peers_peek = metaData.get("peers");
/*      */             
/*      */ 
/* 2779 */             Long az_compact_l = (Long)metaData.get("azcompact");
/* 2780 */             long az_compact = az_compact_l == null ? 0L : az_compact_l.longValue();
/*      */             
/* 2782 */             boolean this_is_az_tracker = az_compact == 2L;
/*      */             
/* 2784 */             if ((this.az_tracker != this_is_az_tracker) || (this.lastUsedUrl != this.lastAZTrackerCheckedURL))
/*      */             {
/* 2786 */               this.lastAZTrackerCheckedURL = this.lastUsedUrl;
/*      */               
/* 2788 */               this.az_tracker = this_is_az_tracker;
/*      */               
/* 2790 */               TRTrackerUtils.setAZTracker(url, this.az_tracker);
/*      */             }
/*      */             
/* 2793 */             if (az_compact == 2L)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 2798 */               List meta_peers = (List)meta_peers_peek;
/*      */               
/* 2800 */               int peers_length = meta_peers.size();
/*      */               
/* 2802 */               if (Logger.isEnabled()) {
/* 2803 */                 Logger.log(new LogEvent(this.torrent, LOGID, "ANNOUNCE CompactPeers2: num=" + peers_length));
/*      */               }
/*      */               
/*      */ 
/* 2807 */               if (peers_length > 1)
/*      */               {
/*      */ 
/* 2810 */                 long total_rtt = 0L;
/* 2811 */                 int rtt_count = 0;
/*      */                 
/* 2813 */                 for (int i = 0; i < peers_length; i++)
/*      */                 {
/* 2815 */                   Map peer = (Map)meta_peers.get(i);
/*      */                   
/* 2817 */                   Long l_rtt = (Long)peer.get("r");
/*      */                   
/* 2819 */                   if (l_rtt != null)
/*      */                   {
/* 2821 */                     long rtt = l_rtt.longValue();
/*      */                     
/* 2823 */                     if (rtt <= 0L)
/*      */                     {
/*      */ 
/*      */ 
/* 2827 */                       peer.remove("r");
/*      */                     }
/*      */                     else
/*      */                     {
/* 2831 */                       total_rtt += rtt;
/*      */                     }
/*      */                     
/* 2834 */                     rtt_count++;
/*      */                   }
/*      */                 }
/*      */                 
/* 2838 */                 final int average_rtt = (int)(rtt_count == 0 ? 0L : total_rtt / rtt_count);
/*      */                 
/*      */ 
/*      */ 
/* 2842 */                 java.util.Collections.sort(meta_peers, new java.util.Comparator()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */                   public int compare(Object o1, Object o2)
/*      */                   {
/*      */ 
/*      */ 
/* 2851 */                     Map map1 = (Map)o1;
/* 2852 */                     Map map2 = (Map)o2;
/*      */                     
/* 2854 */                     Long l_rtt1 = (Long)map1.get("r");
/* 2855 */                     Long l_rtt2 = (Long)map2.get("r");
/*      */                     
/* 2857 */                     boolean biased_1 = map1.containsKey("b");
/* 2858 */                     boolean biased_2 = map2.containsKey("b");
/*      */                     
/* 2860 */                     if (biased_1 == biased_2)
/*      */                     {
/* 2862 */                       int rtt1 = l_rtt1 == null ? average_rtt : l_rtt1.intValue();
/* 2863 */                       int rtt2 = l_rtt2 == null ? average_rtt : l_rtt2.intValue();
/*      */                       
/* 2865 */                       return rtt1 - rtt2;
/*      */                     }
/* 2867 */                     if (biased_1)
/*      */                     {
/* 2869 */                       return -1;
/*      */                     }
/*      */                     
/* 2872 */                     return 1;
/*      */ 
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/* 2878 */                 });
/* 2879 */                 int biased_pos = peers_length;
/* 2880 */                 int non_biased_pos = peers_length;
/*      */                 
/* 2882 */                 for (int i = 0; i < peers_length; i++)
/*      */                 {
/* 2884 */                   Map peer = (Map)meta_peers.get(i);
/*      */                   
/* 2886 */                   if (peer.containsKey("b"))
/*      */                   {
/* 2888 */                     if (i == 0)
/*      */                     {
/* 2890 */                       biased_pos = i;
/*      */                     }
/*      */                   }
/*      */                   else {
/* 2894 */                     non_biased_pos = i;
/*      */                     
/* 2896 */                     break;
/*      */                   }
/*      */                 }
/*      */                 
/* 2900 */                 List new_peers = new ArrayList(peers_length);
/*      */                 
/* 2902 */                 int non_biased_start = non_biased_pos;
/*      */                 
/* 2904 */                 boolean last_biased = true;
/*      */                 
/* 2906 */                 while ((biased_pos < non_biased_start) || (non_biased_pos < peers_length))
/*      */                 {
/* 2908 */                   if (biased_pos < non_biased_start)
/*      */                   {
/* 2910 */                     if (non_biased_pos < peers_length)
/*      */                     {
/* 2912 */                       Map biased = (Map)meta_peers.get(biased_pos);
/* 2913 */                       Map non_biased = (Map)meta_peers.get(non_biased_pos);
/*      */                       
/*      */                       boolean use_biased;
/*      */                       boolean use_biased;
/* 2917 */                       if (!last_biased)
/*      */                       {
/* 2919 */                         use_biased = true;
/*      */                       }
/*      */                       else
/*      */                       {
/* 2923 */                         Long l_rtt_biased = (Long)biased.get("r");
/* 2924 */                         Long l_rtt_non_biased = (Long)non_biased.get("r");
/*      */                         
/* 2926 */                         int biased_rtt = l_rtt_biased == null ? average_rtt : l_rtt_biased.intValue();
/* 2927 */                         int non_biased_rtt = l_rtt_non_biased == null ? average_rtt : l_rtt_non_biased.intValue();
/*      */                         
/* 2929 */                         use_biased = non_biased_rtt >= biased_rtt;
/*      */                       }
/*      */                       
/* 2932 */                       if (use_biased)
/*      */                       {
/* 2934 */                         new_peers.add(biased);
/*      */                         
/* 2936 */                         biased_pos++;
/*      */                       }
/*      */                       else
/*      */                       {
/* 2940 */                         new_peers.add(non_biased);
/*      */                         
/* 2942 */                         non_biased_pos++;
/*      */                       }
/*      */                       
/* 2945 */                       last_biased = use_biased;
/*      */                     }
/*      */                     else {
/* 2948 */                       new_peers.add(meta_peers.get(biased_pos++));
/*      */                     }
/*      */                   }
/*      */                   else {
/* 2952 */                     new_peers.add(meta_peers.get(non_biased_pos++));
/*      */                   }
/*      */                 }
/*      */                 
/* 2956 */                 meta_peers = new_peers;
/*      */               }
/*      */               
/* 2959 */               for (int i = 0; i < peers_length; i++)
/*      */               {
/* 2961 */                 Map peer = (Map)meta_peers.get(i);
/*      */                 try
/*      */                 {
/* 2964 */                   byte[] ip_bytes = (byte[])peer.get("i");
/*      */                   
/*      */                   String ip;
/*      */                   String ip;
/* 2968 */                   if (ip_bytes.length == 4)
/*      */                   {
/* 2970 */                     int ip1 = 0xFF & ip_bytes[0];
/* 2971 */                     int ip2 = 0xFF & ip_bytes[1];
/* 2972 */                     int ip3 = 0xFF & ip_bytes[2];
/* 2973 */                     int ip4 = 0xFF & ip_bytes[3];
/*      */                     
/* 2975 */                     ip = ip1 + "." + ip2 + "." + ip3 + "." + ip4;
/*      */                   }
/*      */                   else {
/* 2978 */                     StringBuilder sb = new StringBuilder(39);
/*      */                     
/* 2980 */                     for (int j = 0; j < 16; j += 2)
/*      */                     {
/* 2982 */                       sb.append(Integer.toHexString(ip_bytes[j] << 8 & 0xFF00 | ip_bytes[(j + 1)] & 0xFF));
/*      */                       
/*      */ 
/* 2985 */                       if (j < 14)
/*      */                       {
/* 2987 */                         sb.append(":");
/*      */                       }
/*      */                     }
/*      */                     
/* 2991 */                     ip = sb.toString();
/*      */                   }
/*      */                   
/* 2994 */                   byte[] tcp_bytes = (byte[])peer.get("t");
/*      */                   
/* 2996 */                   int tcp_port = ((tcp_bytes[0] & 0xFF) << 8) + (tcp_bytes[1] & 0xFF);
/*      */                   
/* 2998 */                   byte[] peer_peer_id = TRTrackerAnnouncerImpl.getAnonymousPeerId(ip, tcp_port);
/*      */                   
/* 3000 */                   int udp_port = 0;
/*      */                   
/* 3002 */                   byte[] udp_bytes = (byte[])peer.get("u");
/*      */                   
/* 3004 */                   if (udp_bytes != null)
/*      */                   {
/* 3006 */                     if (udp_bytes.length == 0)
/*      */                     {
/* 3008 */                       udp_port = tcp_port;
/*      */                     }
/*      */                     else
/*      */                     {
/* 3012 */                       udp_port = ((udp_bytes[0] & 0xFF) << 8) + (udp_bytes[1] & 0xFF);
/*      */                     }
/*      */                   }
/*      */                   
/* 3016 */                   int http_port = 0;
/*      */                   
/* 3018 */                   byte[] http_bytes = (byte[])peer.get("h");
/*      */                   
/* 3020 */                   if (http_bytes != null)
/*      */                   {
/* 3022 */                     http_port = ((http_bytes[0] & 0xFF) << 8) + (http_bytes[1] & 0xFF);
/*      */                   }
/*      */                   
/* 3025 */                   short protocol = 1;
/*      */                   
/* 3027 */                   byte[] protocol_bytes = (byte[])peer.get("c");
/*      */                   
/* 3029 */                   if (protocol_bytes != null)
/*      */                   {
/* 3031 */                     protocol = (protocol_bytes[0] & 0x1) == 0 ? 1 : 2;
/*      */                   }
/*      */                   
/* 3034 */                   Long l_azver = (Long)peer.get("v");
/*      */                   
/* 3036 */                   byte az_ver = l_azver == null ? 1 : l_azver.byteValue();
/*      */                   
/* 3038 */                   Long l_up_speed = (Long)peer.get("s");
/*      */                   
/* 3040 */                   boolean biased = peer.containsKey("b");
/*      */                   
/* 3042 */                   if (biased)
/*      */                   {
/* 3044 */                     com.aelitis.azureus.core.peermanager.utils.PeerClassifier.setAzureusIP(ip);
/*      */                   }
/*      */                   
/* 3047 */                   TRTrackerAnnouncerResponsePeerImpl new_peer = new TRTrackerAnnouncerResponsePeerImpl("Tracker", peer_peer_id, ip, tcp_port, udp_port, http_port, protocol, az_ver, l_up_speed == null ? 0 : l_up_speed.shortValue());
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3059 */                   if (Logger.isEnabled())
/*      */                   {
/* 3061 */                     String extra = "";
/*      */                     
/* 3063 */                     Long l_rtt = (Long)peer.get("r");
/*      */                     
/* 3065 */                     if (l_rtt != null)
/*      */                     {
/* 3067 */                       extra = ",rtt=" + l_rtt;
/*      */                     }
/*      */                     
/* 3070 */                     if (biased)
/*      */                     {
/* 3072 */                       extra = extra + ",biased";
/*      */                     }
/*      */                     
/* 3075 */                     Logger.log(new LogEvent(this.torrent, LOGID, "AZ2-COMPACT PEER: " + new_peer.getString() + extra));
/*      */                   }
/*      */                   
/*      */ 
/* 3079 */                   valid_meta_peers.add(new_peer);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 3083 */                   if (Logger.isEnabled()) {
/* 3084 */                     Logger.log(new LogEvent(this.torrent, LOGID, 3, "Invalid az2 peer received: " + peer));
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/* 3091 */             else if ((meta_peers_peek instanceof List))
/*      */             {
/*      */ 
/*      */ 
/* 3095 */               List meta_peers = (List)meta_peers_peek;
/*      */               
/*      */ 
/*      */ 
/* 3099 */               int peers_length = meta_peers.size();
/*      */               
/* 3101 */               if (Logger.isEnabled()) {
/* 3102 */                 Logger.log(new LogEvent(this.torrent, LOGID, "ANNOUNCE old style non-compact: num=" + peers_length));
/*      */               }
/*      */               
/*      */ 
/* 3106 */               if ((crypto_flags != null) && (peers_length != crypto_flags.length))
/*      */               {
/* 3108 */                 crypto_flags = null;
/*      */                 
/* 3110 */                 if (Logger.isEnabled()) {
/* 3111 */                   Logger.log(new LogEvent(this.torrent, LOGID, 3, "Invalid crypto_flags returned: length mismatch"));
/*      */                 }
/*      */               }
/*      */               
/* 3115 */               for (int i = 0; i < peers_length; i++)
/*      */               {
/* 3117 */                 Map peer = (Map)meta_peers.get(i);
/*      */                 
/* 3119 */                 Object s_peerid = peer.get("peer id");
/* 3120 */                 Object s_ip = peer.get("ip");
/* 3121 */                 Object s_port = peer.get("port");
/*      */                 
/*      */ 
/*      */ 
/* 3125 */                 if ((s_ip != null) && (s_port != null))
/*      */                 {
/*      */ 
/*      */ 
/* 3129 */                   String base_ip = new String((byte[])s_ip, "UTF8");
/*      */                   
/* 3131 */                   String ip = AddressUtils.convertToShortForm(base_ip);
/*      */                   
/* 3133 */                   if (ip == null)
/*      */                   {
/*      */ 
/*      */ 
/* 3137 */                     Logger.log(new LogEvent(this.torrent, LOGID, 3, "Skipping invalid address: " + base_ip));
/*      */ 
/*      */ 
/*      */                   }
/*      */                   else
/*      */                   {
/*      */ 
/*      */ 
/* 3145 */                     int peer_port = (s_port instanceof byte[]) ? Integer.parseInt(new String((byte[])s_port)) : ((Long)s_port).intValue();
/*      */                     
/*      */ 
/*      */ 
/* 3149 */                     if (peer_port > 65535)
/* 3150 */                       peer_port -= 65536;
/* 3151 */                     if (peer_port < 0) {
/* 3152 */                       peer_port += 65536;
/*      */                     }
/* 3154 */                     if ((peer_port < 0) || (peer_port > 65535)) {
/* 3155 */                       if (Logger.isEnabled()) {
/* 3156 */                         Logger.log(new LogEvent(this.torrent, LOGID, 3, "Invalid peer port given: " + ip + ": " + peer_port));
/*      */                       }
/*      */                     }
/*      */                     else
/*      */                     {
/*      */                       byte[] peer_peer_id;
/*      */                       
/*      */ 
/*      */                       byte[] peer_peer_id;
/*      */                       
/*      */ 
/* 3167 */                       if (s_peerid == null)
/*      */                       {
/*      */ 
/*      */ 
/* 3171 */                         peer_peer_id = TRTrackerAnnouncerImpl.getAnonymousPeerId(ip, peer_port);
/*      */ 
/*      */                       }
/*      */                       else
/*      */                       {
/* 3176 */                         peer_peer_id = (byte[])s_peerid;
/*      */                       }
/*      */                       
/*      */                       short protocol;
/*      */                       short protocol;
/* 3181 */                       if (crypto_flags == null)
/*      */                       {
/* 3183 */                         protocol = 1;
/*      */                       }
/*      */                       else
/*      */                       {
/* 3187 */                         protocol = crypto_flags[i] == 0 ? 1 : 2;
/*      */                       }
/*      */                       
/* 3190 */                       int udp_port = 0;
/* 3191 */                       int http_port = 0;
/*      */                       
/* 3193 */                       TRTrackerAnnouncerResponsePeerImpl new_peer = new TRTrackerAnnouncerResponsePeerImpl("Tracker", peer_peer_id, ip, peer_port, udp_port, http_port, protocol, (byte)1, 0);
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3205 */                       if (Logger.isEnabled()) {
/* 3206 */                         Logger.log(new LogEvent(this.torrent, LOGID, "NON-COMPACT PEER: " + new_peer.getString()));
/*      */                       }
/*      */                       
/* 3209 */                       valid_meta_peers.add(new_peer);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/* 3214 */             } else if ((meta_peers_peek instanceof byte[]))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 3219 */               byte[] meta_peers = (byte[])meta_peers_peek;
/*      */               
/*      */ 
/* 3222 */               String tracker_network = AENetworkClassifier.categoriseAddress(url.getHost());
/*      */               
/* 3224 */               if ((tracker_network == "I2P") && (meta_peers.length % 32 == 0))
/*      */               {
/*      */ 
/*      */ 
/* 3228 */                 for (int i = 0; i < meta_peers.length; i += 32)
/*      */                 {
/* 3230 */                   byte[] i2p_id = new byte[32];
/* 3231 */                   byte[] peer_peer_id = new byte[20];
/*      */                   
/* 3233 */                   System.arraycopy(meta_peers, i, i2p_id, 0, 32);
/* 3234 */                   System.arraycopy(meta_peers, i, peer_peer_id, 0, 20);
/*      */                   
/* 3236 */                   String hostname = Base32.encode(i2p_id).toLowerCase(java.util.Locale.US) + ".b32.i2p";
/*      */                   
/* 3238 */                   TRTrackerAnnouncerResponsePeerImpl peer = new TRTrackerAnnouncerResponsePeerImpl("Tracker", peer_peer_id, hostname, 6881, 0, 0, (short)1, (byte)1, 0);
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3250 */                   if (Logger.isEnabled()) {
/* 3251 */                     Logger.log(new LogEvent(this.torrent, LOGID, "COMPACT PEER: " + peer.getString()));
/*      */                   }
/* 3253 */                   valid_meta_peers.add(peer);
/*      */                 }
/*      */               }
/*      */               else {
/* 3257 */                 int entry_size = az_compact == 1L ? 9 : 6;
/*      */                 
/* 3259 */                 if ((crypto_flags != null) && (meta_peers.length / entry_size != crypto_flags.length))
/*      */                 {
/* 3261 */                   crypto_flags = null;
/*      */                   
/* 3263 */                   if (Logger.isEnabled()) {
/* 3264 */                     Logger.log(new LogEvent(this.torrent, LOGID, 3, "Invalid crypto_flags returned: length mismatch"));
/*      */                   }
/*      */                 }
/*      */                 
/* 3268 */                 int peer_number = 0;
/*      */                 
/* 3270 */                 if (Logger.isEnabled()) {
/* 3271 */                   Logger.log(new LogEvent(this.torrent, LOGID, "ANNOUNCE CompactPeers: num=" + meta_peers.length / entry_size));
/*      */                 }
/*      */                 
/*      */ 
/* 3275 */                 int peers_length = meta_peers.length;
/*      */                 
/* 3277 */                 for (int i = 0; i < peers_length / entry_size * entry_size; i += entry_size)
/*      */                 {
/* 3279 */                   peer_number++;
/*      */                   
/* 3281 */                   int ip1 = 0xFF & meta_peers[i];
/* 3282 */                   int ip2 = 0xFF & meta_peers[(i + 1)];
/* 3283 */                   int ip3 = 0xFF & meta_peers[(i + 2)];
/* 3284 */                   int ip4 = 0xFF & meta_peers[(i + 3)];
/* 3285 */                   int po1 = 0xFF & meta_peers[(i + 4)];
/* 3286 */                   int po2 = 0xFF & meta_peers[(i + 5)];
/*      */                   
/* 3288 */                   String ip = "" + ip1 + "." + ip2 + "." + ip3 + "." + ip4;
/* 3289 */                   int tcp_port = po1 * 256 + po2;
/*      */                   
/* 3291 */                   if ((tcp_port < 0) || (tcp_port > 65535)) {
/* 3292 */                     if (Logger.isEnabled()) {
/* 3293 */                       Logger.log(new LogEvent(this.torrent, LOGID, 3, "Invalid compact peer port given: " + ip + ": " + tcp_port));
/*      */                     }
/*      */                     
/*      */                   }
/*      */                   else
/*      */                   {
/* 3299 */                     byte[] peer_peer_id = TRTrackerAnnouncerImpl.getAnonymousPeerId(ip, tcp_port);
/*      */                     
/*      */                     short protocol;
/*      */                     short protocol;
/*      */                     int udp_port;
/* 3304 */                     if (az_compact == 1L)
/*      */                     {
/* 3306 */                       int upo1 = 0xFF & meta_peers[(i + 6)];
/* 3307 */                       int upo2 = 0xFF & meta_peers[(i + 7)];
/*      */                       
/* 3309 */                       int udp_port = upo1 * 256 + upo2;
/*      */                       
/* 3311 */                       byte flags = meta_peers[(i + 8)];
/*      */                       
/* 3313 */                       protocol = (flags & 0x1) == 0 ? 1 : 2;
/*      */                     }
/*      */                     else
/*      */                     {
/*      */                       short protocol;
/* 3318 */                       if (crypto_flags == null)
/*      */                       {
/* 3320 */                         protocol = 1;
/*      */                       }
/*      */                       else
/*      */                       {
/* 3324 */                         protocol = crypto_flags[(peer_number - 1)] == 0 ? 1 : 2;
/*      */                       }
/*      */                       
/* 3327 */                       udp_port = 0;
/*      */                     }
/*      */                     
/* 3330 */                     int http_port = 0;
/*      */                     
/* 3332 */                     TRTrackerAnnouncerResponsePeerImpl peer = new TRTrackerAnnouncerResponsePeerImpl("Tracker", peer_peer_id, ip, tcp_port, udp_port, http_port, protocol, (byte)1, 0);
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3344 */                     if (Logger.isEnabled()) {
/* 3345 */                       Logger.log(new LogEvent(this.torrent, LOGID, "COMPACT PEER: " + peer.getString()));
/*      */                     }
/* 3347 */                     valid_meta_peers.add(peer);
/*      */                   }
/*      */                 }
/* 3350 */               } } else if ((meta_peers_peek instanceof Map))
/*      */             {
/*      */ 
/*      */ 
/* 3354 */               if (((Map)meta_peers_peek).size() != 0)
/*      */               {
/* 3356 */                 throw new IOException("peers missing from response");
/*      */               }
/* 3358 */             } else if (metaData.containsKey("peers6")) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3367 */             byte[] v6peers = (byte[])metaData.get("peers6");
/*      */             
/* 3369 */             if (v6peers != null)
/*      */             {
/*      */ 
/*      */ 
/* 3373 */               int entry_size = 18;
/*      */               
/* 3375 */               byte[] rawAddr = new byte[16];
/*      */               
/* 3377 */               for (int i = 0; i < v6peers.length; i += 18)
/*      */               {
/* 3379 */                 System.arraycopy(v6peers, i, rawAddr, 0, 16);
/*      */                 
/* 3381 */                 String ip = InetAddress.getByAddress(rawAddr).getHostAddress();
/*      */                 
/* 3383 */                 int po1 = 0xFF & v6peers[(i + 16)];
/* 3384 */                 int po2 = 0xFF & v6peers[(i + 17)];
/*      */                 
/* 3386 */                 int tcp_port = po1 * 256 + po2;
/*      */                 
/* 3388 */                 if ((tcp_port < 0) || (tcp_port > 65535))
/*      */                 {
/* 3390 */                   if (Logger.isEnabled())
/*      */                   {
/* 3392 */                     Logger.log(new LogEvent(this.torrent, LOGID, 3, "Invalid compactv6 peer port given: " + ip + ": " + tcp_port));
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 else
/*      */                 {
/* 3398 */                   byte[] peer_peer_id = TRTrackerAnnouncerImpl.getAnonymousPeerId(ip, tcp_port);
/*      */                   
/* 3400 */                   short protocol = 1;
/*      */                   
/* 3402 */                   TRTrackerAnnouncerResponsePeerImpl peer = new TRTrackerAnnouncerResponsePeerImpl("Tracker", peer_peer_id, ip, tcp_port, 0, 0, protocol, (byte)1, 0);
/*      */                   
/* 3404 */                   if (Logger.isEnabled())
/*      */                   {
/* 3406 */                     Logger.log(new LogEvent(this.torrent, LOGID, "COMPACTv6 PEER: " + peer.getString()));
/*      */                   }
/*      */                   
/* 3409 */                   valid_meta_peers.add(peer);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 3414 */             TRTrackerAnnouncerResponsePeerImpl[] peers = new TRTrackerAnnouncerResponsePeerImpl[valid_meta_peers.size()];
/*      */             
/* 3416 */             valid_meta_peers.toArray(peers);
/*      */             
/* 3418 */             this.helper.addToTrackerCache(peers);
/*      */             
/* 3420 */             TRTrackerAnnouncerResponseImpl resp = new TRTrackerAnnouncerResponseImpl(url, this.torrent_hash, 2, time_to_wait, peers);
/*      */             
/*      */ 
/*      */ 
/* 3424 */             this.failure_added_time = 0;
/*      */             
/* 3426 */             Map extensions = (Map)metaData.get("extensions");
/*      */             
/* 3428 */             resp.setExtensions(extensions);
/*      */             
/* 3430 */             if (extensions != null)
/*      */             {
/* 3432 */               if (complete_l == null) {
/* 3433 */                 complete_l = (Long)extensions.get("complete");
/*      */               }
/*      */               
/* 3436 */               if (incomplete_l == null) {
/* 3437 */                 incomplete_l = (Long)extensions.get("incomplete");
/*      */               }
/*      */               
/* 3440 */               if (Logger.isEnabled()) {
/* 3441 */                 Logger.log(new LogEvent(this.torrent, LOGID, "ANNOUNCE SCRAPE2: seeds=" + complete_l + " peers=" + incomplete_l));
/*      */               }
/*      */               
/*      */ 
/* 3445 */               Object override = extensions.get("min interval override");
/*      */               
/* 3447 */               if ((override != null) && ((override instanceof Long)))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 3452 */                 this.min_interval_override = ((Long)override).longValue();
/*      */               }
/*      */             }
/*      */             
/* 3456 */             if ((complete_l != null) || (incomplete_l != null) || (downloaded_l != null))
/*      */             {
/* 3458 */               int complete = complete_l == null ? 0 : complete_l.intValue();
/* 3459 */               int incomplete = incomplete_l == null ? 0 : incomplete_l.intValue();
/* 3460 */               int downloaded = downloaded_l == null ? -1 : downloaded_l.intValue();
/*      */               
/* 3462 */               if ((complete < 0) || (incomplete < 0)) {
/* 3463 */                 resp.setFailureReason(MessageText.getString("Tracker.announce.ignorePeerSeed", new String[] { (complete < 0 ? MessageText.getString("MyTorrentsView.seeds") + " == " + complete + ". " : "") + (incomplete < 0 ? MessageText.getString("MyTorrentsView.peers") + " == " + incomplete + ". " : "") }));
/*      */ 
/*      */ 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 3473 */                 resp.setScrapeResult(complete, incomplete, downloaded);
/*      */                 
/* 3475 */                 TRTrackerScraper scraper = org.gudy.azureus2.core3.tracker.client.TRTrackerScraperFactory.getSingleton();
/*      */                 
/* 3477 */                 if (scraper != null) {
/* 3478 */                   TRTrackerScraperResponse scrapeResponse = scraper.scrape(this.torrent, getTrackerURL());
/* 3479 */                   if (scrapeResponse != null) {
/* 3480 */                     long lNextScrapeTime = scrapeResponse.getNextScrapeStartTime();
/*      */                     
/* 3482 */                     long now = SystemTime.getCurrentTime();
/*      */                     
/* 3484 */                     long lNewNextScrapeTime = now + org.gudy.azureus2.core3.tracker.client.impl.TRTrackerScraperResponseImpl.calcScrapeIntervalSecs(0, complete) * 1000L;
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3492 */                     scrapeResponse.setScrapeStartTime(now);
/*      */                     
/* 3494 */                     if (lNextScrapeTime < lNewNextScrapeTime)
/*      */                     {
/* 3496 */                       scrapeResponse.setNextScrapeStartTime(lNewNextScrapeTime);
/*      */                     }
/*      */                     
/* 3499 */                     scrapeResponse.setSeedsPeers(complete, incomplete);
/*      */                     
/* 3501 */                     if (downloaded >= 0)
/*      */                     {
/* 3503 */                       scrapeResponse.setCompleted(downloaded);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 3510 */             return resp;
/*      */           }
/*      */           catch (IOException e)
/*      */           {
/* 3514 */             if (metaData != null)
/*      */             {
/* 3516 */               byte[] failure_reason_bytes = (byte[])metaData.get("failure reason");
/*      */               
/*      */               String failure_reason;
/*      */               String failure_reason;
/* 3520 */               if (failure_reason_bytes == null) {
/* 3521 */                 Debug.printStackTrace(e);
/*      */                 
/* 3523 */                 failure_reason = "error: " + e.getMessage();
/*      */               }
/*      */               else {
/* 3526 */                 failure_reason = new String(failure_reason_bytes, "UTF8");
/*      */               }
/*      */               
/* 3529 */               return new TRTrackerAnnouncerResponseImpl(url, this.torrent_hash, 1, Math.max(this.tracker_interval, getErrorRetryInterval()), failure_reason);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             String trace_data;
/*      */             
/*      */ 
/*      */ 
/*      */             String trace_data;
/*      */             
/*      */ 
/*      */ 
/* 3543 */             if (data.length <= 150)
/*      */             {
/* 3545 */               trace_data = new String(data);
/*      */             }
/*      */             else
/*      */             {
/* 3549 */               trace_data = new String(data, 0, 150) + "...";
/*      */             }
/*      */             
/* 3552 */             if (Logger.isEnabled()) {
/* 3553 */               Logger.log(new LogEvent(this.torrent, LOGID, 3, "TRTrackerAnnouncer::invalid reply: " + trace_data));
/*      */             }
/*      */             
/* 3556 */             failure_reason = "invalid reply: " + trace_data;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3566 */         return new TRTrackerAnnouncerResponseImpl(url, this.torrent_hash, 0, getErrorRetryInterval(), failure_reason);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3560 */         Debug.printStackTrace(e);
/*      */         
/* 3562 */         failure_reason = "error: " + e.getMessage();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Long getLong(Map map, String key)
/*      */   {
/* 3574 */     Object o = map.get(key);
/*      */     
/* 3576 */     if ((o instanceof Long))
/*      */     {
/* 3578 */       return (Long)o;
/*      */     }
/*      */     
/* 3581 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void informURLChange(URL old_url, URL new_url, boolean explicit)
/*      */   {
/* 3590 */     this.helper.informURLChange(old_url, new_url, explicit);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void informURLRefresh()
/*      */   {
/* 3596 */     this.helper.informURLRefresh();
/*      */   }
/*      */   
/*      */ 
/*      */   public org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse getLastResponse()
/*      */   {
/* 3602 */     if (this.last_response == null)
/*      */     {
/* 3604 */       return new TRTrackerAnnouncerResponseImpl(null, this.torrent_hash, 0, 60L, "Initialising");
/*      */     }
/*      */     
/* 3607 */     return this.last_response;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isManual()
/*      */   {
/* 3613 */     return this.manual_control;
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroy()
/*      */   {
/* 3619 */     this.destroyed = true;
/*      */     try
/*      */     {
/* 3622 */       this.this_mon.enter();
/*      */       
/* 3624 */       if (this.current_timer_event != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3630 */         if (this.current_timer_event.getWhen() - SystemTime.getCurrentTime() > 10000L)
/*      */         {
/* 3632 */           if (Logger.isEnabled()) {
/* 3633 */             Logger.log(new LogEvent(this.torrent, LOGID, "Canceling announce trigger"));
/*      */           }
/*      */           
/* 3636 */           this.current_timer_event.cancel();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 3641 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getErrorRetryInterval()
/*      */   {
/* 3651 */     long currentTime = SystemTime.getCurrentTime() / 1000L;
/*      */     
/* 3653 */     long diff = currentTime - this.failure_time_last_updated;
/*      */     
/*      */ 
/* 3656 */     if ((diff < this.failure_added_time) && (diff >= 0L)) {
/* 3657 */       return this.failure_added_time;
/*      */     }
/*      */     
/*      */ 
/* 3661 */     this.failure_time_last_updated = currentTime;
/*      */     
/* 3663 */     if (this.failure_added_time == 0) {
/* 3664 */       this.failure_added_time = 10;
/*      */     }
/* 3666 */     else if (this.failure_added_time < 30)
/*      */     {
/* 3668 */       this.failure_added_time += 10;
/*      */     }
/* 3670 */     else if (this.failure_added_time < 60)
/*      */     {
/* 3672 */       this.failure_added_time += 15;
/*      */     }
/* 3674 */     else if (this.failure_added_time < 120)
/*      */     {
/* 3676 */       this.failure_added_time += 30;
/*      */     }
/* 3678 */     else if (this.failure_added_time < 600)
/*      */     {
/* 3680 */       this.failure_added_time += 60;
/*      */     }
/*      */     else
/*      */     {
/* 3684 */       this.failure_added_time += 120 + new Random().nextInt(60);
/*      */     }
/*      */     
/* 3687 */     boolean is_seed = this.announce_data_provider != null;
/*      */     
/* 3689 */     if (is_seed) { this.failure_added_time *= 2;
/*      */     }
/*      */     
/* 3692 */     if ((!is_seed) && (this.failure_added_time > 1800)) {
/* 3693 */       this.failure_added_time = 1800;
/*      */     }
/* 3695 */     else if ((is_seed) && (this.failure_added_time > 3600)) {
/* 3696 */       this.failure_added_time = 3600;
/*      */     }
/*      */     
/* 3699 */     return this.failure_added_time;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAnnounceResult(DownloadAnnounceResult result)
/*      */   {
/*      */     TRTrackerAnnouncerResponseImpl response;
/*      */     
/*      */ 
/*      */     String status;
/*      */     
/*      */ 
/*      */     TRTrackerAnnouncerResponseImpl response;
/*      */     
/* 3714 */     if (result.getResponseType() == 2)
/*      */     {
/* 3716 */       String status = MessageText.getString("PeerManager.status.error");
/*      */       
/* 3718 */       String reason = result.getError();
/*      */       
/* 3720 */       if (reason != null)
/*      */       {
/* 3722 */         status = status + " (" + reason + ")";
/*      */       }
/*      */       
/* 3725 */       response = new TRTrackerAnnouncerResponseImpl(result.getURL(), this.torrent_hash, 0, result.getTimeToWait(), reason);
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/* 3732 */       DownloadAnnounceResultPeer[] ext_peers = result.getPeers();
/*      */       
/* 3734 */       List l_peers = new ArrayList(ext_peers.length);
/*      */       
/* 3736 */       boolean ps_enabled = (this.announce_data_provider != null) && (this.announce_data_provider.isPeerSourceEnabled("Tracker"));
/*      */       
/*      */ 
/* 3739 */       for (int i = 0; i < ext_peers.length; i++)
/*      */       {
/* 3741 */         DownloadAnnounceResultPeer ext_peer = ext_peers[i];
/*      */         
/* 3743 */         String ps = ext_peer.getSource();
/*      */         
/*      */ 
/*      */ 
/* 3747 */         if ((ps_enabled) || (!ps.equals("Tracker")))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3753 */           int http_port = 0;
/* 3754 */           byte az_version = 1;
/*      */           
/* 3756 */           TRTrackerAnnouncerResponsePeerImpl p = new TRTrackerAnnouncerResponsePeerImpl(ext_peer.getSource(), ext_peer.getPeerID(), ext_peer.getAddress(), ext_peer.getPort(), ext_peer.getUDPPort(), http_port, ext_peer.getProtocol(), az_version, 0);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3768 */           l_peers.add(p);
/*      */           
/* 3770 */           if (Logger.isEnabled()) {
/* 3771 */             Logger.log(new LogEvent(this.torrent, LOGID, "EXTERNAL PEER: " + p.getString()));
/*      */           }
/*      */         }
/*      */       }
/* 3775 */       TRTrackerAnnouncerResponsePeerImpl[] peers = new TRTrackerAnnouncerResponsePeerImpl[l_peers.size()];
/*      */       
/* 3777 */       l_peers.toArray(peers);
/*      */       
/* 3779 */       this.helper.addToTrackerCache(peers);
/*      */       String status;
/* 3781 */       if ((ps_enabled) || (peers.length > 0) || (ext_peers.length == 0))
/*      */       {
/* 3783 */         status = MessageText.getString("PeerManager.status.ok");
/*      */       }
/*      */       else
/*      */       {
/* 3787 */         status = MessageText.getString("PeerManager.status.ps_disabled");
/*      */         
/* 3789 */         peers = new TRTrackerAnnouncerResponsePeerImpl[0];
/*      */       }
/*      */       
/* 3792 */       response = new TRTrackerAnnouncerResponseImpl(result.getURL(), this.torrent_hash, 2, result.getTimeToWait(), peers);
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
/* 3804 */     if ((this.last_response == null) || (this.last_response.getStatus() != 2))
/*      */     {
/*      */ 
/* 3807 */       URL result_url = result.getURL();
/*      */       
/* 3809 */       boolean update_is_dht = TorrentUtils.isDecentralised(result_url);
/*      */       
/* 3811 */       this.tracker_status_str = (status + " (" + (result_url == null ? "<null>" : update_is_dht ? MessageText.getString("dht.backup.only") : result_url.getHost()) + ")");
/*      */     }
/*      */     
/* 3814 */     this.helper.informResponse(this, response);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(TRTrackerAnnouncerListener l)
/*      */   {
/* 3821 */     this.helper.addListener(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(TRTrackerAnnouncerListener l)
/*      */   {
/* 3828 */     this.helper.removeListener(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTrackerResponseCache(Map map)
/*      */   {
/* 3835 */     this.helper.setTrackerResponseCache(map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeFromTrackerResponseCache(String ip, int tcpPort)
/*      */   {
/* 3842 */     this.helper.removeFromTrackerResponseCache(ip, tcpPort);
/*      */   }
/*      */   
/*      */ 
/*      */   public Map getTrackerResponseCache()
/*      */   {
/* 3848 */     return this.helper.getTrackerResponseCache();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TrackerPeerSource getTrackerPeerSource(TOTorrentAnnounceURLSet set)
/*      */   {
/* 3855 */     Debug.out("not implemented");
/*      */     
/* 3857 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public TrackerPeerSource getCacheTrackerPeerSource()
/*      */   {
/* 3863 */     Debug.out("not implemented");
/*      */     
/* 3865 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generateEvidence(IndentWriter writer)
/*      */   {
/* 3872 */     writer.println("BT announce:");
/*      */     try
/*      */     {
/* 3875 */       writer.indent();
/*      */       
/* 3877 */       writer.println("state: " + this.tracker_state + ", in_progress=" + this.update_in_progress);
/*      */       
/* 3879 */       writer.println("current: " + (this.lastUsedUrl == null ? "null" : this.lastUsedUrl.toString()));
/*      */       
/* 3881 */       writer.println("last: " + (this.last_response == null ? "null" : this.last_response.getString()));
/*      */       
/* 3883 */       writer.println("last_update_secs: " + this.last_update_time_secs);
/*      */       
/* 3885 */       writer.println("secs_to_wait: " + this.current_time_to_wait_secs + (this.manual_control ? " - manual" : ""));
/*      */       
/* 3887 */       writer.println("t_interval: " + this.tracker_interval);
/*      */       
/* 3889 */       writer.println("t_min_interval: " + this.tracker_min_interval);
/*      */       
/* 3891 */       writer.println("min_interval: " + this.min_interval);
/*      */       
/* 3893 */       writer.println("min_interval_override: " + this.min_interval_override);
/*      */       
/* 3895 */       writer.println("rd: last_override=" + this.rd_last_override + ",percentage=" + this.rd_override_percentage);
/*      */       
/* 3897 */       writer.println("event: " + (this.current_timer_event == null ? "null" : this.current_timer_event.getString()));
/*      */       
/* 3899 */       writer.println("stopped: " + this.stopped + ", for_q=" + this.stopped_for_queue);
/*      */       
/* 3901 */       writer.println("complete: " + this.completed + ", reported=" + this.complete_reported);
/*      */     }
/*      */     finally {
/* 3904 */       writer.exdent();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/bt/TRTrackerBTAnnouncerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */