/*      */ package com.aelitis.azureus.plugins.net.buddy.tracker;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteSet;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPlugin;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginAZ2;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginAZ2TrackerListener;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBuddy;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginListener;
/*      */ import java.net.InetAddress;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.Average;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.SHA1;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadPeerListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
/*      */ import org.gudy.azureus2.plugins.peers.Peer;
/*      */ import org.gudy.azureus2.plugins.peers.PeerEvent;
/*      */ import org.gudy.azureus2.plugins.peers.PeerListener2;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManagerEvent;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManagerListener2;
/*      */ import org.gudy.azureus2.plugins.peers.PeerStats;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.ParameterListener;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BuddyPluginTracker
/*      */   implements BuddyPluginListener, DownloadManagerListener, BuddyPluginAZ2TrackerListener, DownloadPeerListener
/*      */ {
/*   76 */   private static final Object PEER_KEY = new Object();
/*      */   
/*   78 */   private static final Object PEER_STATS_KEY = new Object();
/*      */   
/*      */   public static final int BUDDY_NETWORK_IDLE = 1;
/*      */   
/*      */   public static final int BUDDY_NETWORK_OUTBOUND = 2;
/*      */   
/*      */   public static final int BUDDY_NETWORK_INBOUND = 3;
/*      */   
/*      */   public static final int BUDDY_NETWORK_INOUTBOUND = 4;
/*      */   
/*      */   private static final int TRACK_CHECK_PERIOD = 15000;
/*      */   
/*      */   private static final int TRACK_CHECK_TICKS = 1;
/*      */   
/*      */   private static final int PEER_CHECK_PERIOD = 60000;
/*      */   
/*      */   private static final int PEER_CHECK_TICKS = 6;
/*      */   
/*      */   private static final int PEER_RECHECK_PERIOD = 120000;
/*      */   
/*      */   private static final int PEER_RECHECK_TICKS = 12;
/*      */   
/*      */   private static final int PEER_CHECK_INTERVAL = 60000;
/*      */   
/*      */   private static final int SHORT_ID_SIZE = 4;
/*      */   
/*      */   private static final int FULL_ID_SIZE = 20;
/*      */   
/*      */   private static final int REQUEST_TRACKER_SUMMARY = 1;
/*      */   
/*      */   private static final int REPLY_TRACKER_SUMMARY = 2;
/*      */   private static final int REQUEST_TRACKER_STATUS = 3;
/*      */   private static final int REPLY_TRACKER_STATUS = 4;
/*      */   private static final int REQUEST_TRACKER_CHANGE = 5;
/*      */   private static final int REPLY_TRACKER_CHANGE = 6;
/*      */   private static final int REQUEST_TRACKER_ADD = 7;
/*      */   private static final int REPLY_TRACKER_ADD = 8;
/*      */   private static final int RETRY_SEND_MIN = 300000;
/*      */   private static final int RETRY_SEND_MAX = 3600000;
/*      */   private static final int BUDDY_NO = 0;
/*      */   private static final int BUDDY_MAYBE = 1;
/*      */   private static final int BUDDY_YES = 2;
/*      */   private final BuddyPlugin plugin;
/*      */   private final TorrentAttribute ta_networks;
/*      */   private boolean plugin_enabled;
/*      */   private boolean tracker_enabled;
/*      */   private boolean seeding_only;
/*      */   private boolean tracker_so_enabled;
/*      */   private boolean old_plugin_enabled;
/*      */   private boolean old_tracker_enabled;
/*      */   private boolean old_seeding_only;
/*  129 */   private int network_status = 1;
/*      */   
/*  131 */   private Set<BuddyPluginBuddy> online_buddies = new HashSet();
/*  132 */   private Map<String, List<BuddyPluginBuddy>> online_buddy_ips = new HashMap();
/*      */   
/*  134 */   private Set<Download> tracked_downloads = new HashSet();
/*      */   
/*      */   private int download_set_id;
/*      */   
/*      */   private Set<Download> last_processed_download_set;
/*      */   private int last_processed_download_set_id;
/*  140 */   private Map<HashWrapper, List<Download>> short_id_map = new HashMap();
/*  141 */   private Map<HashWrapper, Download> full_id_map = new HashMap();
/*      */   
/*  143 */   private Set<Download> actively_tracking = new HashSet();
/*      */   
/*  145 */   private CopyOnWriteSet<Peer> buddy_peers = new CopyOnWriteSet(true);
/*      */   
/*  147 */   private CopyOnWriteList<BuddyPluginTrackerListener> listeners = new CopyOnWriteList();
/*      */   
/*      */   private TimerEventPeriodic buddy_stats_timer;
/*      */   
/*  151 */   private Average buddy_receive_speed = Average.getInstance(1000, 10);
/*      */   
/*  153 */   private Average buddy_send_speed = Average.getInstance(1000, 10);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public BuddyPluginTracker(BuddyPlugin _plugin, final BooleanParameter tracker_enable, final BooleanParameter tracker_so_enable)
/*      */   {
/*  161 */     this.plugin = _plugin;
/*      */     
/*  163 */     PluginInterface pi = this.plugin.getPluginInterface();
/*      */     
/*  165 */     TorrentManager tm = pi.getTorrentManager();
/*      */     
/*  167 */     this.ta_networks = tm.getAttribute("Networks");
/*      */     
/*  169 */     this.tracker_enabled = tracker_enable.getValue();
/*      */     
/*  171 */     tracker_enable.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  178 */         BuddyPluginTracker.this.tracker_enabled = tracker_enable.getValue();
/*      */         
/*  180 */         BuddyPluginTracker.this.checkEnabledState();
/*      */       }
/*      */       
/*  183 */     });
/*  184 */     this.tracker_so_enabled = tracker_so_enable.getValue();
/*      */     
/*  186 */     tracker_so_enable.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  193 */         BuddyPluginTracker.this.tracker_so_enabled = tracker_so_enable.getValue();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  198 */     });
/*  199 */     GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*      */     
/*  201 */     gm.addListener(new GlobalManagerAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void seedingStatusChanged(boolean seeding_only_mode, boolean potentially_seeding_only)
/*      */       {
/*      */ 
/*      */ 
/*  209 */         BuddyPluginTracker.this.seeding_only = potentially_seeding_only;
/*      */         
/*  211 */         BuddyPluginTracker.this.checkEnabledState(); } }, false);
/*      */     
/*      */ 
/*      */ 
/*  215 */     this.seeding_only = gm.isPotentiallySeedingOnly();
/*      */     
/*  217 */     checkEnabledState();
/*      */   }
/*      */   
/*      */ 
/*      */   public void initialise()
/*      */   {
/*  223 */     this.plugin_enabled = this.plugin.isClassicEnabled();
/*      */     
/*  225 */     checkEnabledState();
/*      */     
/*  227 */     List<BuddyPluginBuddy> buddies = this.plugin.getBuddies();
/*      */     
/*  229 */     for (int i = 0; i < buddies.size(); i++)
/*      */     {
/*  231 */       buddyAdded((BuddyPluginBuddy)buddies.get(i));
/*      */     }
/*      */     
/*  234 */     this.plugin.addListener(this);
/*      */     
/*  236 */     this.plugin.getAZ2Handler().addTrackerListener(this);
/*      */     
/*  238 */     this.plugin.getPluginInterface().getDownloadManager().addListener(this, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void tick(int tick_count)
/*      */   {
/*  245 */     if (tick_count % 1 == 0)
/*      */     {
/*  247 */       checkTracking();
/*      */     }
/*      */     
/*  250 */     if ((tick_count - 1) % 1 == 0)
/*      */     {
/*  252 */       doTracking();
/*      */     }
/*      */     
/*  255 */     if (tick_count % 6 == 0)
/*      */     {
/*  257 */       checkPeers();
/*      */     }
/*      */     
/*  260 */     if (tick_count % 12 == 0)
/*      */     {
/*  262 */       recheckPeers();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNetworkStatus()
/*      */   {
/*  269 */     return this.network_status;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getNetworkReceiveBytesPerSecond()
/*      */   {
/*  275 */     return this.buddy_receive_speed.getAverage();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getNetworkSendBytesPerSecond()
/*      */   {
/*  281 */     return this.buddy_send_speed.getAverage();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void doTracking()
/*      */   {
/*  287 */     if ((!this.plugin_enabled) || (!this.tracker_enabled))
/*      */     {
/*  289 */       return;
/*      */     }
/*      */     
/*  292 */     Map<BuddyPluginBuddy, List<Download>> peers_to_check = new HashMap();
/*      */     
/*  294 */     Set<Download> active_set = new HashSet();
/*      */     
/*  296 */     synchronized (this.online_buddies)
/*      */     {
/*  298 */       Iterator<BuddyPluginBuddy> it = this.online_buddies.iterator();
/*      */       
/*  300 */       while (it.hasNext())
/*      */       {
/*  302 */         BuddyPluginBuddy buddy = (BuddyPluginBuddy)it.next();
/*      */         
/*  304 */         BuddyTrackingData buddy_data = getBuddyData(buddy);
/*      */         
/*  306 */         Map<Download, Boolean> active = buddy_data.getDownloadsToTrack();
/*      */         
/*  308 */         if (active.size() > 0)
/*      */         {
/*  310 */           Iterator<Map.Entry<Download, Boolean>> it2 = active.entrySet().iterator();
/*      */           
/*  312 */           List<Download> check_peers = new ArrayList();
/*      */           
/*  314 */           while (it2.hasNext())
/*      */           {
/*  316 */             Map.Entry<Download, Boolean> entry = (Map.Entry)it2.next();
/*      */             
/*  318 */             Download dl = (Download)entry.getKey();
/*  319 */             boolean check_peer = ((Boolean)entry.getValue()).booleanValue();
/*      */             
/*  321 */             if (check_peer)
/*      */             {
/*  323 */               check_peers.add(dl);
/*      */             }
/*      */             
/*  326 */             active_set.add(dl);
/*      */           }
/*      */           
/*  329 */           if (check_peers.size() > 0)
/*      */           {
/*  331 */             peers_to_check.put(buddy, check_peers);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  339 */     synchronized (this.actively_tracking)
/*      */     {
/*  341 */       Iterator<Download> it = active_set.iterator();
/*      */       
/*  343 */       while (it.hasNext())
/*      */       {
/*  345 */         Download dl = (Download)it.next();
/*      */         
/*  347 */         if (!this.actively_tracking.contains(dl))
/*      */         {
/*  349 */           this.actively_tracking.add(dl);
/*      */           
/*  351 */           trackPeers(dl);
/*      */         }
/*      */       }
/*      */       
/*  355 */       it = this.actively_tracking.iterator();
/*      */       
/*  357 */       while (it.hasNext())
/*      */       {
/*  359 */         Download dl = (Download)it.next();
/*      */         
/*  361 */         if (!active_set.contains(dl))
/*      */         {
/*  363 */           it.remove();
/*      */           
/*  365 */           untrackPeers(dl);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  372 */     Iterator<Map.Entry<BuddyPluginBuddy, List<Download>>> it = peers_to_check.entrySet().iterator();
/*      */     
/*  374 */     boolean lan = this.plugin.getPeersAreLANLocal();
/*      */     
/*  376 */     while (it.hasNext())
/*      */     {
/*  378 */       Map.Entry<BuddyPluginBuddy, List<Download>> entry = (Map.Entry)it.next();
/*      */       
/*  380 */       BuddyPluginBuddy buddy = (BuddyPluginBuddy)entry.getKey();
/*      */       
/*  382 */       if (buddy.isOnline(false))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  387 */         InetAddress ip = buddy.getAdjustedIP();
/*      */         
/*  389 */         if (ip != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  394 */           int tcp_port = buddy.getTCPPort();
/*  395 */           int udp_port = buddy.getUDPPort();
/*      */           
/*  397 */           List<Download> downloads = (List)entry.getValue();
/*      */           
/*  399 */           for (int i = 0; i < downloads.size(); i++)
/*      */           {
/*  401 */             Download download = (Download)downloads.get(i);
/*      */             
/*  403 */             PeerManager pm = download.getPeerManager();
/*      */             
/*  405 */             if (pm != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  410 */               Peer[] existing_peers = pm.getPeers(ip.getHostAddress());
/*      */               
/*  412 */               boolean connected = false;
/*      */               
/*  414 */               for (int j = 0; j < existing_peers.length; j++)
/*      */               {
/*  416 */                 Peer peer = existing_peers[j];
/*      */                 
/*  418 */                 if ((peer.getTCPListenPort() == tcp_port) || (peer.getUDPListenPort() == udp_port))
/*      */                 {
/*      */ 
/*  421 */                   if ((lan) && (!peer.isLANLocal()))
/*      */                   {
/*      */ 
/*      */ 
/*  425 */                     AddressUtils.addLANRateLimitAddress(ip);
/*      */                     
/*  427 */                     pm.removePeer(peer);
/*      */                   }
/*      */                   else
/*      */                   {
/*  431 */                     connected = true;
/*      */                     
/*  433 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*  438 */               if (connected)
/*      */               {
/*  440 */                 log(download.getName() + " - peer " + ip.getHostAddress() + " already connected");
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*  445 */                 log(download.getName() + " - connecting to peer " + ip.getHostAddress());
/*      */                 
/*  447 */                 PEPeerManager c_pm = PluginCoreUtils.unwrap(pm);
/*      */                 
/*  449 */                 Map user_data = new LightHashMap();
/*      */                 
/*  451 */                 user_data.put(PEER_KEY, download);
/*      */                 
/*  453 */                 user_data.put(Peer.PR_PRIORITY_CONNECTION, Boolean.TRUE);
/*      */                 
/*  455 */                 c_pm.addPeer(ip.getHostAddress(), tcp_port, udp_port, true, user_data);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } }
/*      */   
/*  463 */   protected void checkTracking() { if ((!this.plugin_enabled) || (!this.tracker_enabled)) {
/*      */       return;
/*      */     }
/*      */     
/*      */ 
/*      */     List<BuddyPluginBuddy> online;
/*      */     
/*  470 */     synchronized (this.online_buddies)
/*      */     {
/*  472 */       online = new ArrayList(this.online_buddies);
/*      */     }
/*      */     
/*      */     Set<Download> downloads;
/*      */     
/*      */     int downloads_id;
/*  478 */     synchronized (this.tracked_downloads)
/*      */     {
/*  480 */       boolean downloads_changed = this.last_processed_download_set_id != this.download_set_id;
/*      */       
/*  482 */       if (downloads_changed)
/*      */       {
/*  484 */         this.last_processed_download_set = new HashSet(this.tracked_downloads);
/*  485 */         this.last_processed_download_set_id = this.download_set_id;
/*      */       }
/*      */       
/*  488 */       downloads = this.last_processed_download_set;
/*  489 */       downloads_id = this.last_processed_download_set_id;
/*      */     }
/*      */     
/*  492 */     Map diff_map = new HashMap();
/*      */     
/*  494 */     for (int i = 0; i < online.size(); i++)
/*      */     {
/*  496 */       BuddyPluginBuddy buddy = (BuddyPluginBuddy)online.get(i);
/*      */       
/*  498 */       BuddyTrackingData buddy_data = getBuddyData(buddy);
/*      */       
/*  500 */       buddy_data.updateLocal(downloads, downloads_id, diff_map);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void initialised(boolean available) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void buddyAdded(BuddyPluginBuddy buddy)
/*      */   {
/*  514 */     buddyChanged(buddy);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void buddyRemoved(BuddyPluginBuddy buddy)
/*      */   {
/*  521 */     buddyChanged(buddy);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void buddyChanged(BuddyPluginBuddy buddy)
/*      */   {
/*  528 */     if (buddy.isOnline(false))
/*      */     {
/*  530 */       addBuddy(buddy);
/*      */     }
/*      */     else
/*      */     {
/*  534 */       removeBuddy(buddy);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected BuddyTrackingData getBuddyData(BuddyPluginBuddy buddy)
/*      */   {
/*  542 */     synchronized (this.online_buddies)
/*      */     {
/*  544 */       BuddyTrackingData buddy_data = (BuddyTrackingData)buddy.getUserData(BuddyPluginTracker.class);
/*      */       
/*  546 */       if (buddy_data == null)
/*      */       {
/*  548 */         buddy_data = new BuddyTrackingData(buddy);
/*      */         
/*  550 */         buddy.setUserData(BuddyPluginTracker.class, buddy_data);
/*      */       }
/*      */       
/*  553 */       return buddy_data;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected BuddyTrackingData addBuddy(BuddyPluginBuddy buddy)
/*      */   {
/*  561 */     synchronized (this.online_buddies)
/*      */     {
/*  563 */       if (!this.online_buddies.contains(buddy))
/*      */       {
/*  565 */         this.online_buddies.add(buddy);
/*      */       }
/*      */       
/*  568 */       BuddyTrackingData bd = getBuddyData(buddy);
/*      */       
/*  570 */       if (bd.hasIPChanged())
/*      */       {
/*  572 */         String ip = bd.getIP();
/*      */         
/*  574 */         if (ip != null)
/*      */         {
/*  576 */           List<BuddyPluginBuddy> l = (List)this.online_buddy_ips.get(ip);
/*      */           
/*  578 */           if (l != null)
/*      */           {
/*  580 */             l.remove(buddy);
/*      */             
/*  582 */             if (l.size() == 0)
/*      */             {
/*  584 */               this.online_buddy_ips.remove(ip);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  589 */         bd.updateIP();
/*      */         
/*  591 */         ip = bd.getIP();
/*      */         
/*  593 */         if (ip != null)
/*      */         {
/*  595 */           List<BuddyPluginBuddy> l = (List)this.online_buddy_ips.get(ip);
/*      */           
/*  597 */           if (l == null)
/*      */           {
/*  599 */             l = new ArrayList();
/*      */             
/*  601 */             this.online_buddy_ips.put(ip, l);
/*      */           }
/*      */           
/*  604 */           l.add(buddy);
/*      */         }
/*      */       }
/*      */       
/*  608 */       return bd;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeBuddy(BuddyPluginBuddy buddy)
/*      */   {
/*  616 */     synchronized (this.online_buddies)
/*      */     {
/*  618 */       if (this.online_buddies.contains(buddy))
/*      */       {
/*  620 */         BuddyTrackingData bd = getBuddyData(buddy);
/*      */         
/*  622 */         this.online_buddies.remove(buddy);
/*      */         
/*  624 */         String ip = bd.getIP();
/*      */         
/*  626 */         if (ip != null)
/*      */         {
/*  628 */           List<BuddyPluginBuddy> l = (List)this.online_buddy_ips.get(ip);
/*      */           
/*  630 */           if (l != null)
/*      */           {
/*  632 */             l.remove(buddy);
/*      */             
/*  634 */             if (l.size() == 0)
/*      */             {
/*  636 */               this.online_buddy_ips.remove(ip);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int isBuddy(Peer peer)
/*      */   {
/*  648 */     String peer_ip = peer.getIp();
/*      */     
/*  650 */     List ips = AddressUtils.getLANAddresses(peer_ip);
/*      */     
/*  652 */     synchronized (this.online_buddies)
/*      */     {
/*  654 */       int result = 0;
/*      */       
/*      */ 
/*  657 */       for (int i = 0; i < ips.size(); i++)
/*      */       {
/*  659 */         String ip = (String)ips.get(i);
/*      */         
/*  661 */         List<BuddyPluginBuddy> buddies = (List)this.online_buddy_ips.get(ip);
/*      */         
/*  663 */         if (buddies != null)
/*      */         {
/*  665 */           if ((peer.getTCPListenPort() == 0) && (peer.getUDPListenPort() == 0))
/*      */           {
/*  667 */             result = 1;
/*      */           }
/*      */           else
/*      */           {
/*  671 */             for (int j = 0; j < buddies.size(); j++)
/*      */             {
/*  673 */               BuddyPluginBuddy buddy = (BuddyPluginBuddy)buddies.get(j);
/*      */               
/*  675 */               if ((buddy.getTCPPort() == peer.getTCPListenPort()) && (buddy.getTCPPort() != 0))
/*      */               {
/*      */ 
/*  678 */                 result = 2;
/*      */                 
/*      */                 break label192;
/*      */               }
/*      */               
/*  683 */               if ((buddy.getUDPPort() == peer.getUDPListenPort()) && (buddy.getUDPPort() != 0))
/*      */               {
/*      */ 
/*  686 */                 result = 2;
/*      */                 
/*      */                 break label192;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       label192:
/*  695 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void messageLogged(String str, boolean error) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enabledStateChanged(boolean _enabled)
/*      */   {
/*  710 */     this.plugin_enabled = _enabled;
/*      */     
/*  712 */     checkEnabledState();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void updated() {}
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/*  723 */     synchronized (this)
/*      */     {
/*  725 */       return (this.plugin_enabled) && (this.tracker_enabled);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkEnabledState()
/*      */   {
/*  732 */     boolean seeding_change = false;
/*  733 */     boolean enabled_change = false;
/*      */     
/*  735 */     synchronized (this)
/*      */     {
/*  737 */       boolean old_enabled = (this.old_plugin_enabled) && (this.old_tracker_enabled);
/*      */       
/*  739 */       if (this.plugin_enabled != this.old_plugin_enabled)
/*      */       {
/*  741 */         log("Plugin enabled state changed to " + this.plugin_enabled);
/*      */         
/*  743 */         this.old_plugin_enabled = this.plugin_enabled;
/*      */       }
/*      */       
/*  746 */       if (this.tracker_enabled != this.old_tracker_enabled)
/*      */       {
/*  748 */         log("Tracker enabled state changed to " + this.tracker_enabled);
/*      */         
/*  750 */         this.old_tracker_enabled = this.tracker_enabled;
/*      */       }
/*      */       
/*  753 */       if (this.seeding_only != this.old_seeding_only)
/*      */       {
/*  755 */         log("Seeding-only state changed to " + this.seeding_only);
/*      */         
/*  757 */         this.old_seeding_only = this.seeding_only;
/*      */         
/*  759 */         seeding_change = true;
/*      */       }
/*      */       
/*  762 */       enabled_change = old_enabled != ((this.plugin_enabled) && (this.tracker_enabled));
/*      */     }
/*      */     
/*  765 */     if (seeding_change)
/*      */     {
/*  767 */       updateSeedingMode();
/*      */     }
/*      */     
/*  770 */     if (enabled_change)
/*      */     {
/*  772 */       fireEnabledChanged(isEnabled());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updateSeedingMode()
/*      */   {
/*  779 */     updateNetworkStatus();
/*      */     
/*      */     List<BuddyPluginBuddy> online;
/*      */     
/*  783 */     synchronized (this.online_buddies)
/*      */     {
/*  785 */       online = new ArrayList(this.online_buddies);
/*      */     }
/*      */     
/*  788 */     for (int i = 0; i < online.size(); i++)
/*      */     {
/*  790 */       BuddyTrackingData buddy_data = getBuddyData((BuddyPluginBuddy)online.get(i));
/*      */       
/*  792 */       if (buddy_data.hasDownloadsInCommon())
/*      */       {
/*  794 */         buddy_data.updateStatus();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void downloadAdded(final Download download)
/*      */   {
/*  803 */     Torrent t = download.getTorrent();
/*      */     
/*  805 */     if (t == null)
/*      */     {
/*  807 */       return;
/*      */     }
/*      */     
/*  810 */     if (t.isPrivate())
/*      */     {
/*  812 */       download.addTrackerListener(new DownloadTrackerListener()
/*      */       {
/*      */         public void scrapeResult(DownloadScrapeResult result) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void announceResult(DownloadAnnounceResult result)
/*      */         {
/*  825 */           if (BuddyPluginTracker.this.okToTrack(download))
/*      */           {
/*  827 */             BuddyPluginTracker.this.trackDownload(download);
/*      */           }
/*      */           else
/*      */           {
/*  831 */             BuddyPluginTracker.this.untrackDownload(download); } } }, false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  838 */     if (okToTrack(download))
/*      */     {
/*  840 */       trackDownload(download);
/*      */     }
/*      */     
/*  843 */     download.addListener(new DownloadListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void stateChanged(Download download, int old_state, int new_state)
/*      */       {
/*      */ 
/*      */ 
/*  852 */         if (BuddyPluginTracker.this.okToTrack(download))
/*      */         {
/*  854 */           BuddyPluginTracker.this.trackDownload(download);
/*      */         }
/*      */         else
/*      */         {
/*  858 */           BuddyPluginTracker.this.untrackDownload(download);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void positionChanged(Download download, int oldPosition, int newPosition) {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void downloadRemoved(Download download)
/*      */   {
/*  876 */     untrackDownload(download);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void trackDownload(Download download)
/*      */   {
/*  883 */     synchronized (this.tracked_downloads)
/*      */     {
/*  885 */       if (this.tracked_downloads.contains(download))
/*      */       {
/*  887 */         return;
/*      */       }
/*      */       
/*  890 */       downloadData download_data = new downloadData(download);
/*      */       
/*  892 */       download.setUserData(BuddyPluginTracker.class, download_data);
/*      */       
/*  894 */       HashWrapper full_id = download_data.getID();
/*      */       
/*  896 */       HashWrapper short_id = new HashWrapper(full_id.getHash(), 0, 4);
/*      */       
/*  898 */       this.full_id_map.put(full_id, download);
/*      */       
/*  900 */       List<Download> dls = (List)this.short_id_map.get(short_id);
/*      */       
/*  902 */       if (dls == null)
/*      */       {
/*  904 */         dls = new ArrayList();
/*      */         
/*  906 */         this.short_id_map.put(short_id, dls);
/*      */       }
/*      */       
/*  909 */       dls.add(download);
/*      */       
/*  911 */       this.tracked_downloads.add(download);
/*      */       
/*  913 */       this.download_set_id += 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void untrackDownload(Download download)
/*      */   {
/*  921 */     synchronized (this.tracked_downloads)
/*      */     {
/*  923 */       if (this.tracked_downloads.remove(download))
/*      */       {
/*  925 */         this.download_set_id += 1;
/*      */         
/*  927 */         downloadData download_data = (downloadData)download.getUserData(BuddyPluginTracker.class);
/*      */         
/*  929 */         download.setUserData(BuddyPluginTracker.class, null);
/*      */         
/*  931 */         HashWrapper full_id = download_data.getID();
/*      */         
/*  933 */         this.full_id_map.remove(full_id);
/*      */         
/*  935 */         HashWrapper short_id = new HashWrapper(full_id.getHash(), 0, 4);
/*      */         
/*  937 */         List dls = (List)this.short_id_map.get(short_id);
/*      */         
/*  939 */         if (dls != null)
/*      */         {
/*  941 */           dls.remove(download);
/*      */           
/*  943 */           if (dls.size() == 0)
/*      */           {
/*  945 */             this.short_id_map.remove(short_id);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  951 */     synchronized (this.online_buddies)
/*      */     {
/*  953 */       Iterator<BuddyPluginBuddy> it = this.online_buddies.iterator();
/*      */       
/*  955 */       while (it.hasNext())
/*      */       {
/*  957 */         BuddyPluginBuddy buddy = (BuddyPluginBuddy)it.next();
/*      */         
/*  959 */         BuddyTrackingData buddy_data = getBuddyData(buddy);
/*      */         
/*  961 */         buddy_data.removeDownload(download);
/*      */       }
/*      */     }
/*      */     
/*  965 */     synchronized (this.actively_tracking)
/*      */     {
/*  967 */       this.actively_tracking.remove(download);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void trackPeers(Download download)
/*      */   {
/*  975 */     PeerManager pm = download.getPeerManager();
/*      */     
/*      */ 
/*      */ 
/*  979 */     if (pm == null)
/*      */     {
/*  981 */       synchronized (this.actively_tracking)
/*      */       {
/*  983 */         this.actively_tracking.remove(download);
/*      */       }
/*      */     }
/*      */     else {
/*  987 */       log("Tracking peers for " + download.getName());
/*      */       
/*  989 */       download.addPeerListener(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void peerManagerAdded(Download download, PeerManager peer_manager)
/*      */   {
/*  998 */     trackPeers(download, peer_manager);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void peerManagerRemoved(Download download, PeerManager peer_manager)
/*      */   {
/* 1006 */     synchronized (this.actively_tracking)
/*      */     {
/* 1008 */       this.actively_tracking.remove(download);
/*      */     }
/*      */     
/* 1011 */     download.removePeerListener(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void trackPeers(final Download download, final PeerManager pm)
/*      */   {
/* 1019 */     pm.addListener(new PeerManagerListener2()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void eventOccurred(PeerManagerEvent event)
/*      */       {
/*      */ 
/*      */ 
/* 1027 */         if (event.getType() == 1)
/*      */         {
/* 1029 */           synchronized (BuddyPluginTracker.this.actively_tracking)
/*      */           {
/* 1031 */             if (!BuddyPluginTracker.this.actively_tracking.contains(download))
/*      */             {
/* 1033 */               pm.removeListener(this);
/*      */               
/* 1035 */               return;
/*      */             }
/*      */           }
/*      */           
/* 1039 */           BuddyPluginTracker.this.trackPeer(download, event.getPeer());
/*      */         }
/*      */         
/*      */       }
/* 1043 */     });
/* 1044 */     Peer[] peers = pm.getPeers();
/*      */     
/* 1046 */     for (int i = 0; i < peers.length; i++)
/*      */     {
/* 1048 */       trackPeer(download, peers[i]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void trackPeer(Download download, final Peer peer)
/*      */   {
/* 1057 */     int type = isBuddy(peer);
/*      */     
/* 1059 */     if (type == 2)
/*      */     {
/* 1061 */       markBuddyPeer(download, peer);
/*      */     }
/* 1063 */     else if (type == 1)
/*      */     {
/*      */ 
/*      */ 
/* 1067 */       markBuddyPeer(download, peer);
/*      */       
/* 1069 */       PeerListener2 listener = new PeerListener2()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void eventOccurred(PeerEvent event)
/*      */         {
/*      */ 
/* 1076 */           if (event.getType() == 1)
/*      */           {
/* 1078 */             if (((Integer)event.getData()).intValue() == 30)
/*      */             {
/* 1080 */               peer.removeListener(this);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 1085 */               if (BuddyPluginTracker.this.isBuddy(peer) != 2)
/*      */               {
/* 1087 */                 BuddyPluginTracker.this.unmarkBuddyPeer(peer);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }
/* 1093 */       };
/* 1094 */       peer.addListener(listener);
/*      */       
/* 1096 */       if (peer.getState() == 30)
/*      */       {
/* 1098 */         peer.removeListener(listener);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1103 */         if (isBuddy(peer) != 2)
/*      */         {
/* 1105 */           unmarkBuddyPeer(peer);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void untrackPeers(Download download)
/*      */   {
/* 1115 */     log("Not tracking peers for " + download.getName());
/*      */     
/* 1117 */     download.removePeerListener(this);
/*      */     
/* 1119 */     PeerManager pm = download.getPeerManager();
/*      */     
/* 1121 */     if (pm != null)
/*      */     {
/* 1123 */       Peer[] peers = pm.getPeers();
/*      */       
/* 1125 */       for (int i = 0; i < peers.length; i++)
/*      */       {
/* 1127 */         Peer peer = peers[i];
/*      */         
/* 1129 */         unmarkBuddyPeer(peer);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void markBuddyPeer(Download download, final Peer peer)
/*      */   {
/* 1139 */     boolean state_changed = false;
/*      */     
/* 1141 */     synchronized (this.buddy_peers)
/*      */     {
/* 1143 */       if (!this.buddy_peers.contains(peer))
/*      */       {
/* 1145 */         log("Adding buddy peer " + peer.getIp());
/*      */         
/* 1147 */         if (this.buddy_peers.size() == 0)
/*      */         {
/* 1149 */           if (this.buddy_stats_timer == null)
/*      */           {
/* 1151 */             this.buddy_stats_timer = SimpleTimer.addPeriodicEvent("BuddyTracker:stats", 1000L, new TimerEventPerformer()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */               public void perform(TimerEvent event)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 1161 */                 Iterator it = BuddyPluginTracker.this.buddy_peers.iterator();
/*      */                 
/* 1163 */                 long total_sent = 0L;
/* 1164 */                 long total_received = 0L;
/*      */                 
/* 1166 */                 while (it.hasNext())
/*      */                 {
/* 1168 */                   Peer p = (Peer)it.next();
/*      */                   
/* 1170 */                   PeerStats ps = p.getStats();
/*      */                   
/* 1172 */                   long sent = ps.getTotalSent();
/* 1173 */                   long received = ps.getTotalReceived();
/*      */                   
/* 1175 */                   long[] last = (long[])p.getUserData(BuddyPluginTracker.PEER_STATS_KEY);
/*      */                   
/* 1177 */                   if (last != null)
/*      */                   {
/* 1179 */                     total_sent += sent - last[0];
/* 1180 */                     total_received += received - last[1];
/*      */                   }
/*      */                   
/* 1183 */                   p.setUserData(BuddyPluginTracker.PEER_STATS_KEY, new long[] { sent, received });
/*      */                 }
/*      */                 
/* 1186 */                 BuddyPluginTracker.this.buddy_receive_speed.addValue(total_received);
/* 1187 */                 BuddyPluginTracker.this.buddy_send_speed.addValue(total_sent);
/*      */               }
/*      */             });
/*      */           }
/*      */           
/* 1192 */           state_changed = true;
/*      */         }
/*      */         
/* 1195 */         this.buddy_peers.add(peer);
/*      */         
/* 1197 */         peer.setUserData(PEER_KEY, download);
/*      */         
/* 1199 */         peer.setPriorityConnection(true);
/*      */         
/* 1201 */         log(download.getName() + ": adding buddy peer " + peer.getIp());
/*      */         
/* 1203 */         peer.addListener(new PeerListener2()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void eventOccurred(PeerEvent event)
/*      */           {
/*      */ 
/* 1210 */             if (event.getType() == 1)
/*      */             {
/* 1212 */               int state = ((Integer)event.getData()).intValue();
/*      */               
/* 1214 */               if ((state == 40) || (state == 50))
/*      */               {
/* 1216 */                 peer.removeListener(this);
/*      */                 
/* 1218 */                 BuddyPluginTracker.this.unmarkBuddyPeer(peer);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/* 1226 */     if ((peer.getState() == 40) || (peer.getState() == 50))
/*      */     {
/* 1228 */       unmarkBuddyPeer(peer);
/*      */     }
/*      */     
/* 1231 */     if (state_changed)
/*      */     {
/* 1233 */       updateNetworkStatus();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void unmarkBuddyPeer(Peer peer)
/*      */   {
/* 1241 */     boolean state_changed = false;
/*      */     
/* 1243 */     synchronized (this.buddy_peers)
/*      */     {
/* 1245 */       Download download = (Download)peer.getUserData(PEER_KEY);
/*      */       
/* 1247 */       if (download == null)
/*      */       {
/* 1249 */         return;
/*      */       }
/*      */       
/* 1252 */       if (this.buddy_peers.remove(peer))
/*      */       {
/* 1254 */         if (this.buddy_peers.size() == 0)
/*      */         {
/* 1256 */           state_changed = true;
/*      */           
/* 1258 */           if (this.buddy_stats_timer != null)
/*      */           {
/* 1260 */             this.buddy_stats_timer.cancel();
/*      */             
/* 1262 */             this.buddy_stats_timer = null;
/*      */           }
/*      */         }
/*      */         
/* 1266 */         log(download.getName() + ": removing buddy peer " + peer.getIp());
/*      */       }
/*      */       
/* 1269 */       peer.setUserData(PEER_KEY, null);
/*      */       
/* 1271 */       peer.setPriorityConnection(false);
/*      */     }
/*      */     
/* 1274 */     if (state_changed)
/*      */     {
/* 1276 */       updateNetworkStatus();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkPeers()
/*      */   {
/* 1283 */     List to_unmark = new ArrayList();
/*      */     
/* 1285 */     synchronized (this.buddy_peers)
/*      */     {
/* 1287 */       Iterator it = this.buddy_peers.iterator();
/*      */       
/* 1289 */       while (it.hasNext())
/*      */       {
/* 1291 */         Peer peer = (Peer)it.next();
/*      */         
/* 1293 */         if ((peer.getState() == 40) || (peer.getState() == 50))
/*      */         {
/* 1295 */           to_unmark.add(peer);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1300 */     for (int i = 0; i < to_unmark.size(); i++)
/*      */     {
/* 1302 */       unmarkBuddyPeer((Peer)to_unmark.get(i));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void recheckPeers()
/*      */   {
/* 1313 */     synchronized (this.actively_tracking)
/*      */     {
/* 1315 */       Iterator it = this.actively_tracking.iterator();
/*      */       
/* 1317 */       while (it.hasNext())
/*      */       {
/* 1319 */         Download download = (Download)it.next();
/*      */         
/* 1321 */         PeerManager pm = download.getPeerManager();
/*      */         
/* 1323 */         if (pm != null)
/*      */         {
/* 1325 */           Peer[] peers = pm.getPeers();
/*      */           
/* 1327 */           for (int i = 0; i < peers.length; i++)
/*      */           {
/* 1329 */             trackPeer(download, peers[i]);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateNetworkStatus()
/*      */   {
/* 1340 */     boolean changed = false;
/*      */     int new_status;
/* 1342 */     synchronized (this.buddy_peers) {
/*      */       int new_status;
/* 1344 */       if (this.buddy_peers.size() == 0)
/*      */       {
/* 1346 */         new_status = 1;
/*      */       }
/*      */       else {
/*      */         int new_status;
/* 1350 */         if (this.tracker_so_enabled)
/*      */         {
/* 1352 */           new_status = this.seeding_only ? 2 : 3;
/*      */         }
/*      */         else
/*      */         {
/* 1356 */           boolean all_outgoing = true;
/* 1357 */           boolean all_incoming = true;
/*      */           
/* 1359 */           for (Peer peer : this.buddy_peers)
/*      */           {
/* 1361 */             boolean we_are_seed = peer.getManager().isSeeding();
/* 1362 */             boolean they_are_seed = peer.isSeed();
/*      */             
/* 1364 */             if (!we_are_seed)
/*      */             {
/* 1366 */               all_outgoing = false;
/*      */               
/* 1368 */               if (!all_incoming) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*      */             }
/* 1374 */             else if (!they_are_seed)
/*      */             {
/* 1376 */               all_incoming = false;
/*      */               
/* 1378 */               if (!all_outgoing) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */           int new_status;
/* 1385 */           if (all_incoming)
/*      */           {
/* 1387 */             new_status = 3;
/*      */           } else { int new_status;
/* 1389 */             if (all_outgoing)
/*      */             {
/* 1391 */               new_status = 2;
/*      */             }
/*      */             else
/*      */             {
/* 1395 */               new_status = 4;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1400 */       if (new_status != this.network_status)
/*      */       {
/* 1402 */         this.network_status = new_status;
/*      */         
/* 1404 */         changed = true;
/*      */       }
/*      */     }
/*      */     
/* 1408 */     if (changed)
/*      */     {
/* 1410 */       fireStateChange(new_status);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(BuddyPluginTrackerListener l)
/*      */   {
/* 1418 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(BuddyPluginTrackerListener l)
/*      */   {
/* 1425 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireStateChange(int state)
/*      */   {
/* 1432 */     Iterator it = this.listeners.iterator();
/*      */     
/* 1434 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 1437 */         ((BuddyPluginTrackerListener)it.next()).networkStatusChanged(this, state);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1441 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireEnabledChanged(boolean enabled)
/*      */   {
/* 1450 */     Iterator it = this.listeners.iterator();
/*      */     
/* 1452 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 1455 */         ((BuddyPluginTrackerListener)it.next()).enabledStateChanged(this, enabled);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1459 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendMessage(BuddyPluginBuddy buddy, int type, Map<String, Object> body)
/*      */   {
/* 1470 */     Map<String, Object> msg = new HashMap();
/*      */     
/* 1472 */     msg.put("type", new Long(type));
/* 1473 */     msg.put("msg", body);
/*      */     
/* 1475 */     this.plugin.getAZ2Handler().sendAZ2TrackerMessage(buddy, msg, this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map<String, Object> messageReceived(BuddyPluginBuddy buddy, Map<String, Object> message)
/*      */   {
/* 1486 */     BuddyTrackingData buddy_data = buddyAlive(buddy);
/*      */     
/* 1488 */     int type = ((Long)message.get("type")).intValue();
/*      */     
/* 1490 */     Map msg = (Map)message.get("msg");
/*      */     
/* 1492 */     return buddy_data.receiveTrackerMessage(type, msg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void messageFailed(BuddyPluginBuddy buddy, Throwable cause)
/*      */   {
/* 1500 */     log("Failed to send message to " + buddy.getName(), cause);
/*      */     
/* 1502 */     buddyDead(buddy);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected BuddyTrackingData buddyAlive(BuddyPluginBuddy buddy)
/*      */   {
/* 1509 */     BuddyTrackingData buddy_data = addBuddy(buddy);
/*      */     
/* 1511 */     buddy_data.setAlive(true);
/*      */     
/* 1513 */     return buddy_data;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void buddyDead(BuddyPluginBuddy buddy)
/*      */   {
/* 1520 */     BuddyTrackingData buddy_data = getBuddyData(buddy);
/*      */     
/* 1522 */     if (buddy_data != null)
/*      */     {
/* 1524 */       buddy_data.setAlive(false);
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public BuddyTrackingData getTrackingData(BuddyPluginBuddy buddy)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 797	com/aelitis/azureus/plugins/net/buddy/tracker/BuddyPluginTracker:online_buddies	Ljava/util/Set;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_1
/*      */     //   8: ldc_w 423
/*      */     //   11: invokevirtual 831	com/aelitis/azureus/plugins/net/buddy/BuddyPluginBuddy:getUserData	(Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   14: checkcast 433	com/aelitis/azureus/plugins/net/buddy/tracker/BuddyPluginTracker$BuddyTrackingData
/*      */     //   17: aload_2
/*      */     //   18: monitorexit
/*      */     //   19: areturn
/*      */     //   20: astore_3
/*      */     //   21: aload_2
/*      */     //   22: monitorexit
/*      */     //   23: aload_3
/*      */     //   24: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1532	-> byte code offset #0
/*      */     //   Java source line #1534	-> byte code offset #7
/*      */     //   Java source line #1535	-> byte code offset #20
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	25	0	this	BuddyPluginTracker
/*      */     //   0	25	1	buddy	BuddyPluginBuddy
/*      */     //   5	17	2	Ljava/lang/Object;	Object
/*      */     //   20	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	19	20	finally
/*      */     //   20	23	20	finally
/*      */   }
/*      */   
/*      */   public String getTrackingStatus(BuddyPluginBuddy buddy)
/*      */   {
/* 1542 */     BuddyTrackingData data = getTrackingData(buddy);
/*      */     
/* 1544 */     if (data == null)
/*      */     {
/* 1546 */       return "";
/*      */     }
/*      */     
/* 1549 */     return data.getStatus();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean okToTrack(Download d)
/*      */   {
/* 1556 */     Torrent t = d.getTorrent();
/*      */     
/* 1558 */     if (t == null)
/*      */     {
/* 1560 */       return false;
/*      */     }
/*      */     
/* 1563 */     String[] networks = d.getListAttribute(this.ta_networks);
/*      */     
/* 1565 */     boolean ok = false;
/*      */     
/* 1567 */     for (String net : networks)
/*      */     {
/* 1569 */       if (net == "Public")
/*      */       {
/* 1571 */         ok = true;
/*      */       }
/*      */     }
/*      */     
/* 1575 */     if (!ok)
/*      */     {
/* 1577 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1585 */     if (t.isPrivate())
/*      */     {
/* 1587 */       DownloadAnnounceResult announce = d.getLastAnnounceResult();
/*      */       
/* 1589 */       if ((announce == null) || (announce.getResponseType() != 1) || (announce.getPeers().length < 2))
/*      */       {
/*      */ 
/*      */ 
/* 1593 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 1597 */     int state = d.getState();
/*      */     
/* 1599 */     return (state != 8) && (state != 6) && (state != 7);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 1608 */     this.plugin.log("Tracker: " + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, boolean verbose)
/*      */   {
/* 1616 */     if (verbose)
/*      */     {
/* 1618 */       if (Constants.isCVSVersion())
/*      */       {
/* 1620 */         log(str);
/*      */       }
/*      */     }
/*      */     else {
/* 1624 */       log(str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 1633 */     this.plugin.log("Tracker: " + str, e);
/*      */   }
/*      */   
/*      */ 
/*      */   public class BuddyTrackingData
/*      */   {
/*      */     private BuddyPluginBuddy buddy;
/*      */     
/*      */     private Set<Download> downloads_sent;
/*      */     
/*      */     private int downloads_sent_id;
/*      */     
/*      */     private int tracking_remote;
/*      */     
/*      */     private Map<Download, BuddyPluginTracker.buddyDownloadData> downloads_in_common;
/*      */     
/*      */     private boolean buddy_seeding_only;
/*      */     
/*      */     private int consecutive_fails;
/*      */     
/*      */     private long last_fail;
/*      */     private String current_ip;
/*      */     
/*      */     protected BuddyTrackingData(BuddyPluginBuddy _buddy)
/*      */     {
/* 1658 */       this.buddy = _buddy;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void updateIP()
/*      */     {
/* 1664 */       InetAddress latest_ip = this.buddy.getAdjustedIP();
/*      */       
/* 1666 */       if (latest_ip != null)
/*      */       {
/* 1668 */         this.current_ip = latest_ip.getHostAddress();
/*      */         
/* 1670 */         log("IP set to " + this.current_ip);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean hasIPChanged()
/*      */     {
/* 1677 */       InetAddress latest_ip = this.buddy.getAdjustedIP();
/*      */       
/* 1679 */       if ((latest_ip == null) && (this.current_ip == null))
/*      */       {
/* 1681 */         return false;
/*      */       }
/* 1683 */       if ((latest_ip == null) || (this.current_ip == null))
/*      */       {
/* 1685 */         return true;
/*      */       }
/*      */       
/*      */ 
/* 1689 */       return !this.current_ip.equals(latest_ip.getHostAddress());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected String getIP()
/*      */     {
/* 1696 */       return this.current_ip;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean hasDownloadsInCommon()
/*      */     {
/* 1702 */       synchronized (this)
/*      */       {
/* 1704 */         return this.downloads_in_common != null;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setAlive(boolean alive)
/*      */     {
/* 1712 */       synchronized (this)
/*      */       {
/* 1714 */         if (alive)
/*      */         {
/* 1716 */           this.consecutive_fails = 0;
/* 1717 */           this.last_fail = 0L;
/*      */         }
/*      */         else
/*      */         {
/* 1721 */           this.consecutive_fails += 1;
/*      */           
/* 1723 */           this.last_fail = SystemTime.getMonotonousTime();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void updateLocal(Set<Download> downloads, int id, Map diff_map)
/*      */     {
/* 1734 */       if (this.consecutive_fails > 0)
/*      */       {
/* 1736 */         long retry_millis = 300000L;
/*      */         
/* 1738 */         for (int i = 0; i < this.consecutive_fails - 1; i++)
/*      */         {
/* 1740 */           retry_millis <<= 2;
/*      */           
/* 1742 */           if (retry_millis > 3600000L)
/*      */           {
/* 1744 */             retry_millis = 3600000L;
/*      */             
/* 1746 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1750 */         long now = SystemTime.getMonotonousTime();
/*      */         
/* 1752 */         if (now - this.last_fail >= retry_millis)
/*      */         {
/* 1754 */           this.last_fail = now;
/*      */           
/*      */ 
/*      */ 
/* 1758 */           this.downloads_sent = null;
/* 1759 */           this.downloads_sent_id = 0;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1765 */       List<Download> comp_changed = new ArrayList();
/*      */       
/* 1767 */       synchronized (this)
/*      */       {
/* 1769 */         if (this.downloads_in_common != null)
/*      */         {
/* 1771 */           Iterator<Map.Entry<Download, BuddyPluginTracker.buddyDownloadData>> it = this.downloads_in_common.entrySet().iterator();
/*      */           
/* 1773 */           while (it.hasNext())
/*      */           {
/* 1775 */             Map.Entry<Download, BuddyPluginTracker.buddyDownloadData> entry = (Map.Entry)it.next();
/*      */             
/* 1777 */             Download d = (Download)entry.getKey();
/*      */             
/* 1779 */             BuddyPluginTracker.buddyDownloadData bdd = (BuddyPluginTracker.buddyDownloadData)entry.getValue();
/*      */             
/* 1781 */             boolean local_complete = d.isComplete(false);
/*      */             
/* 1783 */             if (local_complete != bdd.isLocalComplete())
/*      */             {
/* 1785 */               bdd.setLocalComplete(local_complete);
/*      */               
/* 1787 */               comp_changed.add(d);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1793 */       if (comp_changed.size() > 0)
/*      */       {
/* 1795 */         byte[][] change_details = exportFullIDs(comp_changed);
/*      */         
/* 1797 */         if (change_details[0].length > 0)
/*      */         {
/* 1799 */           Map<String, Object> msg = new HashMap();
/*      */           
/* 1801 */           msg.put("seeding", new Long(BuddyPluginTracker.this.seeding_only ? 1L : 0L));
/*      */           
/* 1803 */           msg.put("change", change_details[0]);
/* 1804 */           msg.put("change_s", change_details[1]);
/*      */           
/* 1806 */           sendTrackerMessage(5, msg);
/*      */         }
/*      */       }
/*      */       
/* 1810 */       if (id == this.downloads_sent_id)
/*      */       {
/* 1812 */         return;
/*      */       }
/*      */       
/* 1815 */       Long key = new Long(id << 32 | this.downloads_sent_id);
/*      */       
/* 1817 */       Object[] diffs = (Object[])diff_map.get(key);
/*      */       
/* 1819 */       boolean incremental = this.downloads_sent != null;
/*      */       
/*      */       byte[] added_bytes;
/*      */       
/*      */       byte[] removed_bytes;
/* 1824 */       if (diffs == null)
/*      */       {
/*      */ 
/* 1827 */         Object removed = new ArrayList();
/*      */         List added;
/*      */         List added;
/* 1830 */         if (this.downloads_sent == null)
/*      */         {
/* 1832 */           added = new ArrayList(downloads);
/*      */         }
/*      */         else
/*      */         {
/* 1836 */           added = new ArrayList();
/*      */           
/* 1838 */           Iterator<Download> it1 = downloads.iterator();
/*      */           
/* 1840 */           while (it1.hasNext())
/*      */           {
/* 1842 */             Download download = (Download)it1.next();
/*      */             
/* 1844 */             if (BuddyPluginTracker.this.okToTrack(download))
/*      */             {
/* 1846 */               if (!this.downloads_sent.contains(download))
/*      */               {
/* 1848 */                 added.add(download);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1853 */           Iterator it2 = this.downloads_sent.iterator();
/*      */           
/* 1855 */           while (it2.hasNext())
/*      */           {
/* 1857 */             Download download = (Download)it2.next();
/*      */             
/* 1859 */             if (!downloads.contains(download))
/*      */             {
/* 1861 */               ((List)removed).add(download);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1866 */         byte[] added_bytes = exportShortIDs(added);
/* 1867 */         byte[] removed_bytes = exportFullIDs(removed)[0];
/*      */         
/* 1869 */         diff_map.put(key, new Object[] { added_bytes, removed_bytes });
/*      */       }
/*      */       else {
/* 1872 */         added_bytes = (byte[])diffs[0];
/* 1873 */         removed_bytes = (byte[])diffs[1];
/*      */       }
/*      */       
/* 1876 */       this.downloads_sent = downloads;
/* 1877 */       this.downloads_sent_id = id;
/*      */       
/* 1879 */       if ((added_bytes.length == 0) && (removed_bytes.length == 0))
/*      */       {
/* 1881 */         return;
/*      */       }
/*      */       
/* 1884 */       Map msg = new HashMap();
/*      */       
/* 1886 */       if (added_bytes.length > 0)
/*      */       {
/* 1888 */         msg.put("added", added_bytes);
/*      */       }
/*      */       
/* 1891 */       if (removed_bytes.length > 0)
/*      */       {
/* 1893 */         msg.put("removed", removed_bytes);
/*      */       }
/*      */       
/* 1896 */       msg.put("inc", new Long(incremental ? 1L : 0L));
/* 1897 */       msg.put("seeding", new Long(BuddyPluginTracker.this.seeding_only ? 1L : 0L));
/*      */       
/* 1899 */       sendTrackerMessage(1, msg);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected Map updateRemote(Map msg)
/*      */     {
/* 1906 */       byte[] added_bytes = (byte[])msg.get("added");
/*      */       
/* 1908 */       List added = importShortIDs(added_bytes);
/*      */       
/* 1910 */       Map reply = new HashMap();
/*      */       
/* 1912 */       byte[][] add_details = exportFullIDs(added);
/*      */       
/* 1914 */       if (add_details[0].length > 0)
/*      */       {
/* 1916 */         reply.put("added", add_details[0]);
/* 1917 */         reply.put("added_s", add_details[1]);
/*      */       }
/*      */       
/* 1920 */       synchronized (this)
/*      */       {
/* 1922 */         if (this.downloads_in_common != null)
/*      */         {
/* 1924 */           byte[] removed_bytes = (byte[])msg.get("removed");
/*      */           
/* 1926 */           Map removed = importFullIDs(removed_bytes, null);
/*      */           
/* 1928 */           Iterator it = removed.keySet().iterator();
/*      */           
/* 1930 */           while (it.hasNext())
/*      */           {
/* 1932 */             Download d = (Download)it.next();
/*      */             
/* 1934 */             if (this.downloads_in_common.remove(d) != null)
/*      */             {
/* 1936 */               log("Removed " + d.getName() + " common download", false, true);
/*      */             }
/*      */           }
/*      */           
/* 1940 */           if (this.downloads_in_common.size() == 0)
/*      */           {
/* 1942 */             this.downloads_in_common = null;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1947 */       return reply;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void updateCommonDownloads(Map downloads, boolean incremental)
/*      */     {
/* 1955 */       synchronized (this)
/*      */       {
/* 1957 */         if (this.downloads_in_common == null)
/*      */         {
/* 1959 */           this.downloads_in_common = new HashMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/* 1966 */         else if (!incremental)
/*      */         {
/* 1968 */           Iterator it = this.downloads_in_common.keySet().iterator();
/*      */           
/* 1970 */           while (it.hasNext())
/*      */           {
/* 1972 */             Download download = (Download)it.next();
/*      */             
/* 1974 */             if (!downloads.containsKey(download))
/*      */             {
/* 1976 */               log("Removing " + download.getName() + " from common downloads", false, true);
/*      */               
/* 1978 */               it.remove();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1984 */         Iterator it = downloads.entrySet().iterator();
/*      */         
/* 1986 */         while (it.hasNext())
/*      */         {
/* 1988 */           Map.Entry entry = (Map.Entry)it.next();
/*      */           
/* 1990 */           Download d = (Download)entry.getKey();
/*      */           
/* 1992 */           BuddyPluginTracker.buddyDownloadData bdd = (BuddyPluginTracker.buddyDownloadData)entry.getValue();
/*      */           
/* 1994 */           BuddyPluginTracker.buddyDownloadData existing = (BuddyPluginTracker.buddyDownloadData)this.downloads_in_common.get(d);
/*      */           
/* 1996 */           if (existing == null)
/*      */           {
/* 1998 */             log("Adding " + d.getName() + " to common downloads (bdd=" + bdd.getString() + ")", false, true);
/*      */             
/* 2000 */             this.downloads_in_common.put(d, bdd);
/*      */           }
/*      */           else
/*      */           {
/* 2004 */             boolean old_rc = existing.isRemoteComplete();
/* 2005 */             boolean new_rc = bdd.isRemoteComplete();
/*      */             
/* 2007 */             if (old_rc != new_rc)
/*      */             {
/* 2009 */               existing.setRemoteComplete(new_rc);
/*      */               
/* 2011 */               log("Changing " + d.getName() + " common downloads (bdd=" + existing.getString() + ")", false, true);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2016 */         if (this.downloads_in_common.size() == 0)
/*      */         {
/* 2018 */           this.downloads_in_common = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected void updateStatus()
/*      */     {
/* 2026 */       Map msg = new HashMap();
/*      */       
/* 2028 */       msg.put("seeding", new Long(BuddyPluginTracker.this.seeding_only ? 1L : 0L));
/*      */       
/* 2030 */       sendTrackerMessage(3, msg);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void sendTrackerMessage(int type, Map<String, Object> body)
/*      */     {
/* 2038 */       body.put("track", Integer.valueOf(BuddyPluginTracker.this.tracked_downloads.size()));
/*      */       
/* 2040 */       BuddyPluginTracker.this.sendMessage(this.buddy, type, body);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Map<String, Object> receiveTrackerMessage(int type, Map<String, Object> msg_in)
/*      */     {
/* 2048 */       int reply_type = -1;
/*      */       
/* 2050 */       Map<String, Object> msg_out = null;
/*      */       
/* 2052 */       Long l_track = (Long)msg_in.get("track");
/*      */       
/* 2054 */       if (l_track != null)
/*      */       {
/* 2056 */         this.tracking_remote = l_track.intValue();
/*      */       }
/*      */       
/* 2059 */       Long l_seeding = (Long)msg_in.get("seeding");
/*      */       
/* 2061 */       if (l_seeding != null)
/*      */       {
/* 2063 */         boolean old = this.buddy_seeding_only;
/*      */         
/* 2065 */         this.buddy_seeding_only = (l_seeding.intValue() == 1);
/*      */         
/* 2067 */         if (old != this.buddy_seeding_only)
/*      */         {
/* 2069 */           log("Seeding only changed to " + this.buddy_seeding_only);
/*      */         }
/*      */       }
/*      */       
/* 2073 */       if (type == 1)
/*      */       {
/* 2075 */         reply_type = 2;
/*      */         
/* 2077 */         msg_out = updateRemote(msg_in);
/*      */         
/* 2079 */         msg_out.put("inc", msg_in.get("inc"));
/*      */       }
/* 2081 */       else if (type == 3)
/*      */       {
/* 2083 */         reply_type = 4;
/*      */       }
/* 2085 */       else if (type == 5)
/*      */       {
/* 2087 */         reply_type = 4;
/*      */         
/* 2089 */         Map downloads = importFullIDs((byte[])msg_in.get("changed"), (byte[])msg_in.get("changed_s"));
/*      */         
/* 2091 */         updateCommonDownloads(downloads, true);
/*      */       }
/* 2093 */       else if (type == 7)
/*      */       {
/* 2095 */         reply_type = 8;
/*      */         
/* 2097 */         Map downloads = importFullIDs((byte[])msg_in.get("added"), (byte[])msg_in.get("added_s"));
/*      */         
/* 2099 */         updateCommonDownloads(downloads, true);
/*      */       }
/* 2101 */       else if (type == 2)
/*      */       {
/*      */ 
/*      */ 
/* 2105 */         byte[] possible_matches = (byte[])msg_in.get("added");
/* 2106 */         byte[] possible_match_states = (byte[])msg_in.get("added_s");
/*      */         
/* 2108 */         boolean incremental = ((Long)msg_in.get("inc")).intValue() == 1;
/*      */         
/* 2110 */         if ((possible_matches != null) && (possible_match_states != null))
/*      */         {
/* 2112 */           Map downloads = importFullIDs(possible_matches, possible_match_states);
/*      */           
/* 2114 */           if (downloads.size() > 0)
/*      */           {
/* 2116 */             updateCommonDownloads(downloads, incremental);
/*      */             
/* 2118 */             byte[][] common_details = exportFullIDs(new ArrayList(downloads.keySet()));
/*      */             
/* 2120 */             if (common_details[0].length > 0)
/*      */             {
/* 2122 */               Map<String, Object> msg = new HashMap();
/*      */               
/* 2124 */               msg.put("seeding", new Long(BuddyPluginTracker.this.seeding_only ? 1L : 0L));
/*      */               
/* 2126 */               msg.put("added", common_details[0]);
/* 2127 */               msg.put("added_s", common_details[1]);
/*      */               
/* 2129 */               sendTrackerMessage(7, msg);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2134 */       else if ((type != 6) && (type != 4) && (type != 8))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2141 */         log("Unrecognised type " + type);
/*      */       }
/*      */       
/* 2144 */       if (reply_type != -1)
/*      */       {
/* 2146 */         Map reply = new HashMap();
/*      */         
/* 2148 */         reply.put("type", new Long(reply_type));
/*      */         
/* 2150 */         if (msg_out == null)
/*      */         {
/* 2152 */           msg_out = new HashMap();
/*      */         }
/*      */         
/* 2155 */         msg_out.put("seeding", new Long(BuddyPluginTracker.this.seeding_only ? 1L : 0L));
/*      */         
/* 2157 */         reply.put("msg", msg_out);
/*      */         
/* 2159 */         return reply;
/*      */       }
/*      */       
/* 2162 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected byte[] exportShortIDs(List<Download> downloads)
/*      */     {
/* 2169 */       byte[] res = new byte[4 * downloads.size()];
/*      */       
/* 2171 */       for (int i = 0; i < downloads.size(); i++)
/*      */       {
/* 2173 */         Download download = (Download)downloads.get(i);
/*      */         
/* 2175 */         BuddyPluginTracker.downloadData download_data = (BuddyPluginTracker.downloadData)download.getUserData(BuddyPluginTracker.class);
/*      */         
/* 2177 */         if (download_data != null)
/*      */         {
/* 2179 */           System.arraycopy(download_data.getID().getBytes(), 0, res, i * 4, 4);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2188 */       return res;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected List<Download> importShortIDs(byte[] ids)
/*      */     {
/* 2195 */       List<Download> res = new ArrayList();
/*      */       
/* 2197 */       if (ids != null)
/*      */       {
/* 2199 */         synchronized (BuddyPluginTracker.this.tracked_downloads)
/*      */         {
/* 2201 */           for (int i = 0; i < ids.length; i += 4)
/*      */           {
/* 2203 */             List<Download> dls = (List)BuddyPluginTracker.this.short_id_map.get(new HashWrapper(ids, i, 4));
/*      */             
/* 2205 */             if (dls != null)
/*      */             {
/* 2207 */               res.addAll(dls);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2213 */       return res;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected byte[][] exportFullIDs(List<Download> downloads)
/*      */     {
/* 2220 */       byte[] hashes = new byte[20 * downloads.size()];
/* 2221 */       byte[] states = new byte[downloads.size()];
/*      */       
/* 2223 */       for (int i = 0; i < downloads.size(); i++)
/*      */       {
/* 2225 */         Download download = (Download)downloads.get(i);
/*      */         
/* 2227 */         BuddyPluginTracker.downloadData download_data = (BuddyPluginTracker.downloadData)download.getUserData(BuddyPluginTracker.class);
/*      */         
/* 2229 */         if (download_data != null)
/*      */         {
/* 2231 */           System.arraycopy(download_data.getID().getBytes(), 0, hashes, i * 20, 20);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2238 */           states[i] = (download.isComplete(false) ? 1 : 0);
/*      */         }
/*      */       }
/*      */       
/* 2242 */       return new byte[][] { hashes, states };
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Map<Download, BuddyPluginTracker.buddyDownloadData> importFullIDs(byte[] ids, byte[] states)
/*      */     {
/* 2250 */       Map<Download, BuddyPluginTracker.buddyDownloadData> res = new HashMap();
/*      */       
/* 2252 */       if (ids != null)
/*      */       {
/* 2254 */         synchronized (BuddyPluginTracker.this.tracked_downloads)
/*      */         {
/* 2256 */           for (int i = 0; i < ids.length; i += 20)
/*      */           {
/* 2258 */             Download dl = (Download)BuddyPluginTracker.this.full_id_map.get(new HashWrapper(ids, i, 20));
/*      */             
/* 2260 */             if (dl != null)
/*      */             {
/* 2262 */               BuddyPluginTracker.buddyDownloadData bdd = new BuddyPluginTracker.buddyDownloadData(dl);
/*      */               
/* 2264 */               if (states != null)
/*      */               {
/* 2266 */                 bdd.setRemoteComplete((states[(i / 20)] & 0x1) != 0);
/*      */               }
/*      */               
/* 2269 */               res.put(dl, bdd);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2275 */       return res;
/*      */     }
/*      */     
/*      */ 
/*      */     protected Map<Download, Boolean> getDownloadsToTrack()
/*      */     {
/* 2281 */       Map<Download, Boolean> res = new HashMap();
/*      */       
/*      */ 
/* 2284 */       if ((BuddyPluginTracker.this.tracker_so_enabled) && (BuddyPluginTracker.this.seeding_only == this.buddy_seeding_only))
/*      */       {
/* 2286 */         log("Not tracking, buddy and me both " + (BuddyPluginTracker.this.seeding_only ? "seeding" : "downloading"), true, false);
/*      */         
/* 2288 */         return res;
/*      */       }
/*      */       
/* 2291 */       long now = SystemTime.getMonotonousTime();
/*      */       
/* 2293 */       synchronized (this)
/*      */       {
/* 2295 */         if (this.downloads_in_common == null)
/*      */         {
/* 2297 */           log("Not tracking, buddy has nothing in common", true, false);
/*      */           
/* 2299 */           return res;
/*      */         }
/*      */         
/* 2302 */         Iterator<Map.Entry<Download, BuddyPluginTracker.buddyDownloadData>> it = this.downloads_in_common.entrySet().iterator();
/*      */         
/* 2304 */         while (it.hasNext())
/*      */         {
/* 2306 */           Map.Entry<Download, BuddyPluginTracker.buddyDownloadData> entry = (Map.Entry)it.next();
/*      */           
/* 2308 */           Download d = (Download)entry.getKey();
/*      */           
/* 2310 */           BuddyPluginTracker.buddyDownloadData bdd = (BuddyPluginTracker.buddyDownloadData)entry.getValue();
/*      */           
/* 2312 */           if ((d.isComplete(false)) && (bdd.isRemoteComplete()))
/*      */           {
/*      */ 
/*      */ 
/* 2316 */             log(d.getName() + " - not tracking, both complete", true, true);
/*      */           }
/*      */           else
/*      */           {
/* 2320 */             long last_check = bdd.getPeerCheckTime();
/*      */             
/* 2322 */             if ((last_check == 0L) || (now - last_check >= 60000L))
/*      */             {
/*      */ 
/* 2325 */               log(d.getName() + " - checking peer", false, true);
/*      */               
/* 2327 */               bdd.setPeerCheckTime(now);
/*      */               
/* 2329 */               res.put(d, Boolean.TRUE);
/*      */             }
/*      */             else
/*      */             {
/* 2333 */               res.put(d, Boolean.FALSE);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2339 */       return res;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void removeDownload(Download download)
/*      */     {
/* 2346 */       synchronized (this)
/*      */       {
/* 2348 */         if (this.downloads_in_common == null)
/*      */         {
/* 2350 */           return;
/*      */         }
/*      */         
/* 2353 */         this.downloads_in_common.remove(download);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getStatus()
/*      */     {
/* 2360 */       Map<Download, BuddyPluginTracker.buddyDownloadData> c = this.downloads_in_common;
/*      */       
/* 2362 */       String str = String.valueOf(BuddyPluginTracker.this.tracked_downloads.size());
/*      */       
/* 2364 */       str = str + "/" + this.tracking_remote + "/" + (c == null ? "0" : Integer.valueOf(c.size()));
/*      */       
/* 2366 */       return str;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void log(String str)
/*      */     {
/* 2373 */       BuddyPluginTracker.this.log(this.buddy.getName() + ": " + str);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void log(String str, boolean verbose, boolean no_buddy)
/*      */     {
/* 2382 */       BuddyPluginTracker.this.log((no_buddy ? "" : new StringBuilder().append(this.buddy.getName()).append(": ").toString()) + str, verbose);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class buddyDownloadData
/*      */   {
/*      */     private boolean local_is_complete;
/*      */     
/*      */     private boolean remote_is_complete;
/*      */     
/*      */     private long last_peer_check;
/*      */     
/*      */     protected buddyDownloadData(Download download)
/*      */     {
/* 2397 */       this.local_is_complete = download.isComplete(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setLocalComplete(boolean b)
/*      */     {
/* 2404 */       this.local_is_complete = b;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isLocalComplete()
/*      */     {
/* 2410 */       return this.local_is_complete;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setRemoteComplete(boolean b)
/*      */     {
/* 2417 */       this.remote_is_complete = b;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isRemoteComplete()
/*      */     {
/* 2423 */       return this.remote_is_complete;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setPeerCheckTime(long time)
/*      */     {
/* 2430 */       this.last_peer_check = time;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getPeerCheckTime()
/*      */     {
/* 2436 */       return this.last_peer_check;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 2442 */       return "lic=" + this.local_is_complete + ",ric=" + this.remote_is_complete + ",lpc=" + this.last_peer_check;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class downloadData
/*      */   {
/* 2449 */     private static final byte[] IV = { 122, 122, -83, -85, -114, -65, -51, 57, -121, 0, -92, -72, -2, 64, -94, -24 };
/*      */     
/*      */ 
/*      */     private HashWrapper id;
/*      */     
/*      */ 
/*      */     protected downloadData(Download download)
/*      */     {
/* 2457 */       Torrent t = download.getTorrent();
/*      */       
/* 2459 */       if (t != null)
/*      */       {
/* 2461 */         byte[] hash = t.getHash();
/*      */         
/* 2463 */         SHA1 sha1 = new SHA1();
/*      */         
/* 2465 */         sha1.update(ByteBuffer.wrap(IV));
/* 2466 */         sha1.update(ByteBuffer.wrap(hash));
/*      */         
/* 2468 */         this.id = new HashWrapper(sha1.digest());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected HashWrapper getID()
/*      */     {
/* 2475 */       return this.id;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/tracker/BuddyPluginTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */