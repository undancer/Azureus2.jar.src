/*      */ package org.gudy.azureus2.core3.tracker.server.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.PrintStream;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URL;
/*      */ import java.net.URLDecoder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServer;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerAuthenticationListener;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener2;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerPeer;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerRequestListener;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerStats;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrent;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrentStats;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AEThread;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class TRTrackerServerImpl
/*      */   implements TRTrackerServer
/*      */ {
/*      */   public static final int RETRY_MINIMUM_SECS = 60;
/*      */   public static final int RETRY_MINIMUM_MILLIS = 60000;
/*      */   public static final int CLIENT_TIMEOUT_MULTIPLIER = 3;
/*      */   public static final int TIMEOUT_CHECK = 180000;
/*   53 */   public static int max_peers_to_send = 0;
/*   54 */   public static boolean send_peer_ids = true;
/*   55 */   public static int announce_cache_period = 500;
/*   56 */   public static int scrape_cache_period = 5000;
/*   57 */   public static int announce_cache_threshold = 500;
/*   58 */   public static int max_seed_retention = 0;
/*   59 */   public static int seed_limit = 0;
/*   60 */   public static boolean full_scrape_enable = true;
/*   61 */   public static boolean restrict_non_blocking_requests = true;
/*      */   
/*   63 */   public static boolean all_networks_permitted = true;
/*   64 */   public static String[] permitted_networks = new String[0];
/*      */   
/*      */   public static boolean support_experimental_extensions;
/*      */   
/*   68 */   public static String redirect_on_not_found = "";
/*      */   
/*   70 */   public static final List<String> banned_clients = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*   74 */   private static final Map torrent_map = new HashMap();
/*      */   
/*   76 */   private static final Map link_map = new HashMap();
/*      */   
/*   78 */   protected final AEMonitor class_mon = new AEMonitor("TRTrackerServer:class");
/*      */   
/*      */ 
/*      */   static
/*      */   {
/*   83 */     COConfigurationManager.addListener(new COConfigurationListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void configurationSaved() {}
/*      */ 
/*      */ 
/*      */ 
/*   92 */     });
/*   93 */     readConfig();
/*      */   }
/*      */   
/*      */ 
/*      */   protected static void readConfig()
/*      */   {
/*   99 */     send_peer_ids = COConfigurationManager.getBooleanParameter("Tracker Send Peer IDs");
/*      */     
/*  101 */     max_peers_to_send = COConfigurationManager.getIntParameter("Tracker Max Peers Returned");
/*      */     
/*  103 */     scrape_cache_period = COConfigurationManager.getIntParameter("Tracker Scrape Cache", 5000);
/*      */     
/*  105 */     announce_cache_period = COConfigurationManager.getIntParameter("Tracker Announce Cache", 500);
/*      */     
/*  107 */     announce_cache_threshold = COConfigurationManager.getIntParameter("Tracker Announce Cache Min Peers", 500);
/*      */     
/*  109 */     max_seed_retention = COConfigurationManager.getIntParameter("Tracker Max Seeds Retained", 0);
/*      */     
/*  111 */     seed_limit = COConfigurationManager.getIntParameter("Tracker Max Seeds", 0);
/*      */     
/*  113 */     List nets = new ArrayList();
/*      */     
/*  115 */     for (int i = 0; i < AENetworkClassifier.AT_NETWORKS.length; i++)
/*      */     {
/*  117 */       String net = AENetworkClassifier.AT_NETWORKS[i];
/*      */       
/*  119 */       boolean enabled = COConfigurationManager.getBooleanParameter("Tracker Network Selection Default." + net);
/*      */       
/*      */ 
/*      */ 
/*  123 */       if (enabled)
/*      */       {
/*  125 */         nets.add(net);
/*      */       }
/*      */     }
/*      */     
/*  129 */     String[] s_nets = new String[nets.size()];
/*      */     
/*  131 */     nets.toArray(s_nets);
/*      */     
/*  133 */     permitted_networks = s_nets;
/*      */     
/*  135 */     all_networks_permitted = s_nets.length == AENetworkClassifier.AT_NETWORKS.length;
/*      */     
/*  137 */     full_scrape_enable = COConfigurationManager.getBooleanParameter("Tracker Server Full Scrape Enable");
/*      */     
/*  139 */     redirect_on_not_found = COConfigurationManager.getStringParameter("Tracker Server Not Found Redirect").trim();
/*      */     
/*  141 */     support_experimental_extensions = COConfigurationManager.getBooleanParameter("Tracker Server Support Experimental Extensions");
/*      */     
/*  143 */     restrict_non_blocking_requests = COConfigurationManager.getBooleanParameter("Tracker TCP NonBlocking Restrict Request Types");
/*      */     
/*  145 */     String banned = COConfigurationManager.getStringParameter("Tracker Banned Clients", "").trim();
/*      */     
/*  147 */     banned_clients.clear();
/*      */     
/*  149 */     if (banned.length() > 0)
/*      */     {
/*  151 */       banned = banned.toLowerCase(Locale.US).replaceAll(";", ",");
/*      */       
/*  153 */       String[] bits = banned.split(",");
/*      */       
/*  155 */       for (String b : bits)
/*      */       {
/*  157 */         b = b.trim();
/*      */         
/*  159 */         if (b.length() > 0)
/*      */         {
/*  161 */           banned_clients.add(b);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static boolean getSendPeerIds()
/*      */   {
/*  170 */     return send_peer_ids;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static int getMaxPeersToSend()
/*      */   {
/*  176 */     return max_peers_to_send;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static int getScrapeCachePeriod()
/*      */   {
/*  182 */     return scrape_cache_period;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static int getAnnounceCachePeriod()
/*      */   {
/*  188 */     return announce_cache_period;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static int getAnnounceCachePeerThreshold()
/*      */   {
/*  194 */     return announce_cache_threshold;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static int getMaxSeedRetention()
/*      */   {
/*  200 */     return max_seed_retention;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static int getSeedLimit()
/*      */   {
/*  206 */     return seed_limit;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean isFullScrapeEnabled()
/*      */   {
/*  212 */     return full_scrape_enable;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static boolean getAllNetworksSupported()
/*      */   {
/*  218 */     return all_networks_permitted;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static String[] getPermittedNetworks()
/*      */   {
/*  224 */     return permitted_networks;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean supportsExtensions()
/*      */   {
/*  230 */     return support_experimental_extensions;
/*      */   }
/*      */   
/*  233 */   protected final IpFilter ip_filter = IpFilterManagerFactory.getSingleton().getIPFilter();
/*      */   
/*      */   private long current_announce_retry_interval;
/*      */   
/*      */   private long current_scrape_retry_interval;
/*      */   
/*      */   private long current_total_clients;
/*      */   private int current_min_poll_interval;
/*      */   private final int current_min_seed_announce_mult;
/*  242 */   private final TRTrackerServerStatsImpl stats = new TRTrackerServerStatsImpl(this);
/*      */   
/*      */   private final String name;
/*      */   
/*      */   private boolean web_password_enabled;
/*      */   
/*      */   private boolean web_password_https_only;
/*      */   private boolean tracker_password_enabled;
/*      */   private String password_user;
/*      */   private byte[] password_pw;
/*      */   private boolean compact_enabled;
/*      */   private boolean key_enabled;
/*  254 */   private boolean enabled = true;
/*      */   
/*  256 */   private boolean keep_alive_enabled = false;
/*      */   
/*      */ 
/*  259 */   protected final CopyOnWriteList<TRTrackerServerListener> listeners = new CopyOnWriteList();
/*  260 */   protected final CopyOnWriteList<TRTrackerServerListener2> listeners2 = new CopyOnWriteList();
/*      */   
/*  262 */   private final List<TRTrackerServerAuthenticationListener> auth_listeners = new ArrayList();
/*      */   
/*  264 */   private final Vector<TRTrackerServerRequestListener> request_listeners = new Vector();
/*      */   
/*  266 */   protected AEMonitor this_mon = new AEMonitor("TRTrackerServer");
/*      */   
/*      */ 
/*      */   private final COConfigurationListener config_listener;
/*      */   
/*      */   private boolean destroyed;
/*      */   
/*      */   private Set biased_peers;
/*      */   
/*      */   private boolean is_ready;
/*      */   
/*      */ 
/*      */   public TRTrackerServerImpl(String _name, boolean _start_up_ready)
/*      */   {
/*  280 */     this.name = (_name == null ? DEFAULT_NAME : _name);
/*  281 */     this.is_ready = _start_up_ready;
/*      */     
/*      */ 
/*  284 */     this.config_listener = new COConfigurationListener()
/*      */     {
/*      */ 
/*      */       public void configurationSaved()
/*      */       {
/*      */ 
/*  290 */         TRTrackerServerImpl.this.readConfigSettings();
/*      */       }
/*      */       
/*  293 */     };
/*  294 */     COConfigurationManager.addListener(this.config_listener);
/*      */     
/*  296 */     readConfigSettings();
/*      */     
/*  298 */     this.current_min_poll_interval = COConfigurationManager.getIntParameter("Tracker Poll Interval Min", 120);
/*      */     
/*  300 */     if (this.current_min_poll_interval < 60)
/*      */     {
/*  302 */       this.current_min_poll_interval = 60;
/*      */     }
/*      */     
/*  305 */     this.current_min_seed_announce_mult = COConfigurationManager.getIntParameter("Tracker Poll Seed Interval Mult");
/*      */     
/*  307 */     this.current_announce_retry_interval = this.current_min_poll_interval;
/*      */     
/*  309 */     int scrape_percentage = COConfigurationManager.getIntParameter("Tracker Scrape Retry Percentage", 200);
/*      */     
/*  311 */     this.current_scrape_retry_interval = (this.current_announce_retry_interval * scrape_percentage / 100L);
/*      */     
/*  313 */     Thread timer_thread = new AEThread("TrackerServer:timer.loop")
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  319 */         TRTrackerServerImpl.this.timerLoop();
/*      */       }
/*      */       
/*  322 */     };
/*  323 */     timer_thread.setDaemon(true);
/*      */     
/*  325 */     timer_thread.start();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void readConfigSettings()
/*      */   {
/*  331 */     this.web_password_enabled = COConfigurationManager.getBooleanParameter("Tracker Password Enable Web");
/*  332 */     this.tracker_password_enabled = COConfigurationManager.getBooleanParameter("Tracker Password Enable Torrent");
/*      */     
/*  334 */     this.web_password_https_only = COConfigurationManager.getBooleanParameter("Tracker Password Web HTTPS Only");
/*      */     
/*  336 */     if ((this.web_password_enabled) || (this.tracker_password_enabled))
/*      */     {
/*  338 */       this.password_user = COConfigurationManager.getStringParameter("Tracker Username", "");
/*  339 */       this.password_pw = COConfigurationManager.getByteParameter("Tracker Password", new byte[0]);
/*      */     }
/*      */     
/*  342 */     this.compact_enabled = COConfigurationManager.getBooleanParameter("Tracker Compact Enable");
/*      */     
/*  344 */     this.key_enabled = COConfigurationManager.getBooleanParameter("Tracker Key Enable Server");
/*      */   }
/*      */   
/*      */ 
/*      */   public void setReady()
/*      */   {
/*  350 */     this.is_ready = true;
/*      */   }
/*      */   
/*      */ 
/*      */   public final boolean isReady()
/*      */   {
/*  356 */     return this.is_ready;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setEnabled(boolean e)
/*      */   {
/*  363 */     this.enabled = e;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/*  369 */     return this.enabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setEnableKeepAlive(boolean enable)
/*      */   {
/*  376 */     this.keep_alive_enabled = this.enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isKeepAliveEnabled()
/*      */   {
/*  382 */     return this.keep_alive_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TRTrackerServerTorrent addLink(String link, TRTrackerServerTorrent target)
/*      */   {
/*      */     try
/*      */     {
/*  391 */       this.class_mon.enter();
/*      */       
/*  393 */       return (TRTrackerServerTorrent)link_map.put(link, target);
/*      */     }
/*      */     finally
/*      */     {
/*  397 */       this.class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeLink(String link, TRTrackerServerTorrent target)
/*      */   {
/*      */     try
/*      */     {
/*  407 */       this.class_mon.enter();
/*      */       
/*  409 */       link_map.remove(link);
/*      */     }
/*      */     finally
/*      */     {
/*  413 */       this.class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBiasedPeers(Set peers)
/*      */   {
/*  421 */     if ((this.biased_peers != null) && (peers.equals(this.biased_peers)))
/*      */     {
/*  423 */       return;
/*      */     }
/*      */     
/*  426 */     String str = "";
/*      */     
/*  428 */     Iterator it = peers.iterator();
/*      */     
/*  430 */     while (it.hasNext())
/*      */     {
/*  432 */       str = str + " " + it.next();
/*      */     }
/*      */     
/*  435 */     System.out.println("biased peers: " + str);
/*      */     try
/*      */     {
/*  438 */       this.class_mon.enter();
/*      */       
/*  440 */       this.biased_peers = new HashSet(peers);
/*      */       
/*  442 */       Iterator tit = torrent_map.values().iterator();
/*      */       
/*  444 */       while (tit.hasNext())
/*      */       {
/*  446 */         TRTrackerServerTorrentImpl this_torrent = (TRTrackerServerTorrentImpl)tit.next();
/*      */         
/*  448 */         this_torrent.updateBiasedPeers(this.biased_peers);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  453 */       this.class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected Set getBiasedPeers()
/*      */   {
/*  460 */     return this.biased_peers;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isWebPasswordEnabled()
/*      */   {
/*  466 */     return (this.web_password_enabled) || (this.auth_listeners.size() > 0);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isTrackerPasswordEnabled()
/*      */   {
/*  472 */     return (this.tracker_password_enabled) || (this.auth_listeners.size() > 0);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isWebPasswordHTTPSOnly()
/*      */   {
/*  478 */     return this.web_password_https_only;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasExternalAuthorisation()
/*      */   {
/*  484 */     return this.auth_listeners.size() > 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasInternalAuthorisation()
/*      */   {
/*  490 */     return (this.web_password_enabled) || (this.tracker_password_enabled);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean performExternalAuthorisation(InetSocketAddress remote_ip, String headers, URL resource, String user, String password)
/*      */   {
/*  501 */     headers = headers.trim() + "\r\nX-Real-IP: " + remote_ip.getAddress().getHostAddress() + "\r\n\r\n";
/*      */     
/*  503 */     for (int i = 0; i < this.auth_listeners.size(); i++)
/*      */     {
/*      */       try
/*      */       {
/*  507 */         if (((TRTrackerServerAuthenticationListener)this.auth_listeners.get(i)).authenticate(headers, resource, user, password))
/*      */         {
/*  509 */           return true;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  513 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  517 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] performExternalAuthorisation(URL resource, String user)
/*      */   {
/*  525 */     for (int i = 0; i < this.auth_listeners.size(); i++)
/*      */     {
/*      */       try
/*      */       {
/*  529 */         byte[] sha_pw = ((TRTrackerServerAuthenticationListener)this.auth_listeners.get(i)).authenticate(resource, user);
/*      */         
/*  531 */         if (sha_pw != null)
/*      */         {
/*  533 */           return sha_pw;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  537 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  541 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  547 */     return this.name;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isCompactEnabled()
/*      */   {
/*  553 */     return this.compact_enabled;
/*      */   }
/*      */   
/*      */   public boolean isKeyEnabled()
/*      */   {
/*  558 */     return this.key_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getUsername()
/*      */   {
/*  564 */     return this.password_user;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getPassword()
/*      */   {
/*  570 */     return this.password_pw;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getMinAnnounceRetryInterval()
/*      */   {
/*  576 */     return this.current_min_poll_interval;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getAnnounceRetryInterval(TRTrackerServerTorrentImpl torrent)
/*      */   {
/*  583 */     long clients = this.current_total_clients;
/*      */     
/*  585 */     if (clients == 0L)
/*      */     {
/*  587 */       return this.current_announce_retry_interval;
/*      */     }
/*      */     
/*  590 */     long res = torrent.getPeerCount() * this.current_announce_retry_interval / clients;
/*      */     
/*  592 */     if (res < this.current_min_poll_interval)
/*      */     {
/*  594 */       res = this.current_min_poll_interval;
/*      */     }
/*      */     
/*  597 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSeedAnnounceIntervalMultiplier()
/*      */   {
/*  603 */     return this.current_min_seed_announce_mult;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getScrapeRetryInterval(TRTrackerServerTorrentImpl torrent)
/*      */   {
/*  610 */     long clients = this.current_total_clients;
/*      */     
/*  612 */     if ((torrent == null) || (clients == 0L))
/*      */     {
/*  614 */       return this.current_scrape_retry_interval;
/*      */     }
/*      */     
/*  617 */     long res = torrent.getPeerCount() * this.current_scrape_retry_interval / clients;
/*      */     
/*  619 */     if (res < this.current_min_poll_interval)
/*      */     {
/*  621 */       res = this.current_min_poll_interval;
/*      */     }
/*      */     
/*  624 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getMinScrapeRetryInterval()
/*      */   {
/*  630 */     return this.current_min_poll_interval;
/*      */   }
/*      */   
/*      */ 
/*      */   public TRTrackerServerStats getStats()
/*      */   {
/*  636 */     return this.stats;
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
/*      */   public void updateStats(int request_type, TRTrackerServerTorrentImpl torrent, int bytes_in, int bytes_out)
/*      */   {
/*  649 */     this.stats.update(request_type, bytes_in, bytes_out);
/*      */     
/*  651 */     if (torrent != null)
/*      */     {
/*  653 */       torrent.updateXferStats(bytes_in, bytes_out);
/*      */     }
/*      */     else
/*      */     {
/*  657 */       int num = torrent_map.size();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  662 */       if (num < 256) {
/*      */         try
/*      */         {
/*  665 */           this.class_mon.enter();
/*      */           
/*      */ 
/*  668 */           if (num > 0)
/*      */           {
/*      */ 
/*      */ 
/*  672 */             int ave_in = bytes_in / num;
/*  673 */             int ave_out = bytes_out / num;
/*      */             
/*  675 */             int rem_in = bytes_in - ave_in * num;
/*  676 */             int rem_out = bytes_out - ave_out * num;
/*      */             
/*  678 */             Iterator it = torrent_map.values().iterator();
/*      */             
/*  680 */             while (it.hasNext())
/*      */             {
/*  682 */               TRTrackerServerTorrentImpl this_torrent = (TRTrackerServerTorrentImpl)it.next();
/*      */               
/*  684 */               if (it.hasNext())
/*      */               {
/*  686 */                 this_torrent.updateXferStats(ave_in, ave_out);
/*      */               }
/*      */               else
/*      */               {
/*  690 */                 this_torrent.updateXferStats(ave_in + rem_in, ave_out + rem_out);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  697 */           this.class_mon.exit();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateTime(int request_type, long time)
/*      */   {
/*  708 */     this.stats.updateTime(request_type, time);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void timerLoop()
/*      */   {
/*  715 */     long time_to_go = 180000L;
/*      */     
/*  717 */     while (!this.destroyed) {
/*      */       try
/*      */       {
/*  720 */         Thread.sleep(60000L);
/*      */         
/*  722 */         time_to_go -= 60000L;
/*      */         
/*      */ 
/*      */ 
/*  726 */         this.current_min_poll_interval = COConfigurationManager.getIntParameter("Tracker Poll Interval Min", 120);
/*      */         
/*  728 */         if (this.current_min_poll_interval < 60)
/*      */         {
/*  730 */           this.current_min_poll_interval = 60;
/*      */         }
/*      */         
/*  733 */         int min = this.current_min_poll_interval;
/*  734 */         int max = COConfigurationManager.getIntParameter("Tracker Poll Interval Max", 3600);
/*  735 */         int inc_by = COConfigurationManager.getIntParameter("Tracker Poll Inc By", 60);
/*  736 */         int inc_per = COConfigurationManager.getIntParameter("Tracker Poll Inc Per", 10);
/*      */         
/*  738 */         int scrape_percentage = COConfigurationManager.getIntParameter("Tracker Scrape Retry Percentage", 200);
/*      */         
/*  740 */         int retry = min;
/*      */         
/*  742 */         int clients = 0;
/*      */         try
/*      */         {
/*  745 */           this.class_mon.enter();
/*      */           
/*  747 */           Iterator it = torrent_map.values().iterator();
/*      */           
/*  749 */           while (it.hasNext())
/*      */           {
/*  751 */             TRTrackerServerTorrentImpl t = (TRTrackerServerTorrentImpl)it.next();
/*      */             
/*  753 */             clients += t.getPeerCount();
/*      */           }
/*      */         }
/*      */         finally {
/*  757 */           this.class_mon.exit();
/*      */         }
/*      */         
/*  760 */         if ((inc_by > 0) && (inc_per > 0))
/*      */         {
/*  762 */           retry += inc_by * (clients / inc_per);
/*      */         }
/*      */         
/*  765 */         if ((max > 0) && (retry > max))
/*      */         {
/*  767 */           retry = max;
/*      */         }
/*      */         
/*  770 */         if (retry < 60)
/*      */         {
/*  772 */           retry = 60;
/*      */         }
/*      */         
/*  775 */         this.current_announce_retry_interval = retry;
/*      */         
/*  777 */         this.current_scrape_retry_interval = (this.current_announce_retry_interval * scrape_percentage / 100L);
/*      */         
/*  779 */         this.current_total_clients = clients;
/*      */         
/*      */ 
/*      */ 
/*  783 */         if (time_to_go <= 0L)
/*      */         {
/*  785 */           time_to_go = 180000L;
/*      */           try
/*      */           {
/*  788 */             this.class_mon.enter();
/*      */             
/*  790 */             Iterator it = torrent_map.values().iterator();
/*      */             
/*  792 */             while (it.hasNext())
/*      */             {
/*  794 */               TRTrackerServerTorrentImpl t = (TRTrackerServerTorrentImpl)it.next();
/*      */               
/*  796 */               t.checkTimeouts();
/*      */             }
/*      */           }
/*      */           finally {
/*  800 */             this.class_mon.exit();
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (InterruptedException e)
/*      */       {
/*  806 */         Debug.printStackTrace(e);
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
/*      */   public TRTrackerServerTorrent permit(String _originator, byte[] _hash, boolean _explicit)
/*      */     throws TRTrackerServerException
/*      */   {
/*  820 */     return permit(_originator, _hash, _explicit, true);
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
/*      */   public TRTrackerServerTorrent permit(String _originator, byte[] _hash, boolean _explicit, boolean _enabled)
/*      */     throws TRTrackerServerException
/*      */   {
/*  834 */     HashWrapper hash = new HashWrapper(_hash);
/*      */     
/*      */ 
/*      */     TRTrackerServerTorrentImpl entry;
/*      */     
/*      */     try
/*      */     {
/*  841 */       this.class_mon.enter();
/*      */       
/*  843 */       entry = (TRTrackerServerTorrentImpl)torrent_map.get(hash);
/*      */     }
/*      */     finally
/*      */     {
/*  847 */       this.class_mon.exit();
/*      */     }
/*      */     
/*  850 */     if (entry == null)
/*      */     {
/*  852 */       Object it = this.listeners.iterator();
/*      */       
/*  854 */       while (((Iterator)it).hasNext())
/*      */       {
/*  856 */         if (!((TRTrackerServerListener)((Iterator)it).next()).permitted(_originator, _hash, _explicit))
/*      */         {
/*  858 */           throw new TRTrackerServerException("operation denied");
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/*  863 */         this.class_mon.enter();
/*      */         
/*      */ 
/*      */ 
/*  867 */         entry = (TRTrackerServerTorrentImpl)torrent_map.get(hash);
/*      */         
/*  869 */         if (entry == null)
/*      */         {
/*  871 */           entry = new TRTrackerServerTorrentImpl(this, hash, _enabled);
/*      */           
/*  873 */           torrent_map.put(hash, entry);
/*      */         }
/*      */       }
/*      */       finally {
/*  877 */         this.class_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*  881 */     return entry;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void deny(byte[] _hash, boolean _explicit)
/*      */     throws TRTrackerServerException
/*      */   {
/*  893 */     HashWrapper hash = new HashWrapper(_hash);
/*      */     
/*  895 */     Iterator<TRTrackerServerListener> it = this.listeners.iterator();
/*      */     
/*  897 */     while (it.hasNext())
/*      */     {
/*  899 */       if (!((TRTrackerServerListener)it.next()).denied(_hash, _explicit))
/*      */       {
/*  901 */         throw new TRTrackerServerException("operation denied");
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  906 */       this.class_mon.enter();
/*      */       
/*  908 */       TRTrackerServerTorrentImpl entry = (TRTrackerServerTorrentImpl)torrent_map.get(hash);
/*      */       
/*  910 */       if (entry != null)
/*      */       {
/*  912 */         entry.delete();
/*      */       }
/*      */       
/*  915 */       torrent_map.remove(hash);
/*      */     }
/*      */     finally
/*      */     {
/*  919 */       this.class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TRTrackerServerTorrentImpl getTorrent(byte[] hash)
/*      */   {
/*      */     try
/*      */     {
/*  928 */       this.class_mon.enter();
/*      */       
/*  930 */       return (TRTrackerServerTorrentImpl)torrent_map.get(new HashWrapper(hash));
/*      */     }
/*      */     finally
/*      */     {
/*  934 */       this.class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TRTrackerServerTorrentImpl getTorrent(String link)
/*      */   {
/*      */     try
/*      */     {
/*  943 */       this.class_mon.enter();
/*      */       
/*  945 */       return (TRTrackerServerTorrentImpl)link_map.get(link);
/*      */     }
/*      */     finally
/*      */     {
/*  949 */       this.class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public TRTrackerServerTorrentImpl[] getTorrents()
/*      */   {
/*      */     try
/*      */     {
/*  957 */       this.class_mon.enter();
/*      */       
/*  959 */       TRTrackerServerTorrentImpl[] res = new TRTrackerServerTorrentImpl[torrent_map.size()];
/*      */       
/*  961 */       torrent_map.values().toArray(res);
/*      */       
/*  963 */       return res;
/*      */     }
/*      */     finally {
/*  966 */       this.class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTorrentCount()
/*      */   {
/*  973 */     return torrent_map.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TRTrackerServerTorrentStats getStats(byte[] hash)
/*      */   {
/*  980 */     TRTrackerServerTorrentImpl torrent = getTorrent(hash);
/*      */     
/*  982 */     if (torrent == null)
/*      */     {
/*  984 */       return null;
/*      */     }
/*      */     
/*  987 */     return torrent.getStats();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TRTrackerServerPeer[] getPeers(byte[] hash)
/*      */   {
/*  994 */     TRTrackerServerTorrentImpl torrent = getTorrent(hash);
/*      */     
/*  996 */     if (torrent == null)
/*      */     {
/*  998 */       return null;
/*      */     }
/*      */     
/* 1001 */     return torrent.getPeers();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(TRTrackerServerListener l)
/*      */   {
/* 1008 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(TRTrackerServerListener l)
/*      */   {
/* 1015 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener2(TRTrackerServerListener2 l)
/*      */   {
/* 1022 */     this.listeners2.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener2(TRTrackerServerListener2 l)
/*      */   {
/* 1029 */     this.listeners2.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addAuthenticationListener(TRTrackerServerAuthenticationListener l)
/*      */   {
/* 1036 */     this.auth_listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeAuthenticationListener(TRTrackerServerAuthenticationListener l)
/*      */   {
/* 1043 */     this.auth_listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void preProcess(TRTrackerServerPeer peer, TRTrackerServerTorrent torrent, int type, String request, Map response)
/*      */     throws TRTrackerServerException
/*      */   {
/* 1056 */     if (this.request_listeners.size() > 0)
/*      */     {
/*      */ 
/*      */ 
/* 1060 */       if (type == 2) {
/*      */         try
/*      */         {
/* 1063 */           int request_pos = 10;
/*      */           
/*      */           for (;;)
/*      */           {
/* 1067 */             int p = request.indexOf("info_hash=", request_pos);
/*      */             
/*      */             String bit;
/*      */             String bit;
/* 1071 */             if (p == -1)
/*      */             {
/* 1073 */               if (request_pos == 10) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/* 1078 */               bit = request.substring(request_pos);
/*      */             }
/*      */             else
/*      */             {
/* 1082 */               bit = request.substring(request_pos, p);
/*      */             }
/*      */             
/* 1085 */             int pos = bit.indexOf('&');
/*      */             
/* 1087 */             String hash_str = pos == -1 ? bit : bit.substring(0, pos);
/*      */             
/* 1089 */             hash_str = URLDecoder.decode(hash_str, "ISO-8859-1");
/*      */             
/* 1091 */             byte[] hash = hash_str.getBytes("ISO-8859-1");
/*      */             
/* 1093 */             if (Arrays.equals(hash, torrent.getHash().getBytes()))
/*      */             {
/* 1095 */               request = "info_hash=" + bit;
/*      */               
/* 1097 */               if (!request.endsWith("&"))
/*      */                 break;
/* 1099 */               request = request.substring(0, request.length() - 1); break;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1105 */             if (p == -1) {
/*      */               break;
/*      */             }
/*      */             
/*      */ 
/* 1110 */             request_pos = p + 10;
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 1114 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/* 1118 */       TRTrackerServerRequestImpl req = new TRTrackerServerRequestImpl(this, peer, torrent, type, request, response);
/*      */       
/* 1120 */       for (int i = 0; i < this.request_listeners.size(); i++) {
/*      */         try
/*      */         {
/* 1123 */           ((TRTrackerServerRequestListener)this.request_listeners.elementAt(i)).preProcess(req);
/*      */         }
/*      */         catch (TRTrackerServerException e)
/*      */         {
/* 1127 */           throw e;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1131 */           Debug.printStackTrace(e);
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
/*      */ 
/*      */   public void postProcess(TRTrackerServerPeer peer, TRTrackerServerTorrentImpl torrent, int type, String request, Map response)
/*      */     throws TRTrackerServerException
/*      */   {
/* 1147 */     if (this.request_listeners.size() > 0)
/*      */     {
/* 1149 */       TRTrackerServerRequestImpl req = new TRTrackerServerRequestImpl(this, peer, torrent, type, request, response);
/*      */       
/* 1151 */       for (int i = 0; i < this.request_listeners.size(); i++) {
/*      */         try
/*      */         {
/* 1154 */           ((TRTrackerServerRequestListener)this.request_listeners.elementAt(i)).postProcess(req);
/*      */         }
/*      */         catch (TRTrackerServerException e)
/*      */         {
/* 1158 */           throw e;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1162 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addRequestListener(TRTrackerServerRequestListener l)
/*      */   {
/* 1172 */     this.request_listeners.addElement(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeRequestListener(TRTrackerServerRequestListener l)
/*      */   {
/* 1179 */     this.request_listeners.removeElement(l);
/*      */   }
/*      */   
/*      */ 
/*      */   public void close()
/*      */   {
/* 1185 */     TRTrackerServerFactoryImpl.close(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void destroySupport()
/*      */   {
/* 1194 */     this.destroyed = true;
/*      */     
/* 1196 */     COConfigurationManager.removeListener(this.config_listener);
/*      */   }
/*      */   
/*      */   protected abstract void closeSupport();
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/TRTrackerServerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */