/*      */ package com.aelitis.azureus.plugins.extseed.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteSet;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedException;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPeer;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedReader;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedReaderListener;
/*      */ import com.aelitis.azureus.plugins.extseed.util.ExternalSeedHTTPDownloaderListener;
/*      */ import java.net.InetAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.clientid.ClientIDGenerator;
/*      */ import org.gudy.azureus2.plugins.clientid.ClientIDManager;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*      */ import org.gudy.azureus2.plugins.peers.Peer;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManagerEvent;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManagerListener2;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManagerStats;
/*      */ import org.gudy.azureus2.plugins.peers.PeerReadRequest;
/*      */ import org.gudy.azureus2.plugins.peers.PeerStats;
/*      */ import org.gudy.azureus2.plugins.peers.Piece;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.utils.Monitor;
/*      */ import org.gudy.azureus2.plugins.utils.Semaphore;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ public abstract class ExternalSeedReaderImpl implements ExternalSeedReader, PeerManagerListener2
/*      */ {
/*      */   public static final int RECONNECT_DEFAULT = 30000;
/*      */   public static final int INITIAL_DELAY = 30000;
/*      */   public static final int STALLED_DOWNLOAD_SPEED = 20480;
/*      */   public static final int STALLED_PEER_SPEED = 5120;
/*      */   public static final int TOP_PIECE_PRIORITY = 100000;
/*      */   private static boolean use_avail_to_activate;
/*      */   private ExternalSeedPlugin plugin;
/*      */   private Torrent torrent;
/*      */   private final String host;
/*      */   private final String host_net;
/*      */   private String ip_use_accessor;
/*      */   private String status;
/*      */   private boolean active;
/*      */   private boolean permanent_fail;
/*      */   private long last_failed_read;
/*      */   private int consec_failures;
/*      */   private String user_agent;
/*      */   private long peer_manager_change_time;
/*      */   private volatile PeerManager current_manager;
/*      */   
/*      */   static
/*      */   {
/*   71 */     COConfigurationManager.addAndFireParameterListener("webseed.activation.uses.availability", new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*   79 */         ExternalSeedReaderImpl.access$002(COConfigurationManager.getBooleanParameter(name));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  106 */   private List<PeerReadRequest> requests = new java.util.LinkedList();
/*      */   
/*      */   private List<PeerReadRequest> dangling_requests;
/*      */   
/*      */   private Thread request_thread;
/*      */   
/*      */   private Semaphore request_sem;
/*      */   
/*      */   private Monitor requests_mon;
/*      */   
/*      */   private ExternalSeedReaderRequest active_read_request;
/*      */   private int[] priority_offsets;
/*      */   private boolean fast_activate;
/*      */   private int min_availability;
/*      */   private int min_download_speed;
/*      */   private int max_peer_speed;
/*      */   private long valid_until;
/*      */   private boolean transient_seed;
/*  124 */   private int reconnect_delay = 30000;
/*      */   
/*      */   private volatile ExternalSeedReaderRequest current_request;
/*      */   
/*  128 */   private List listeners = new ArrayList();
/*      */   
/*  130 */   private AESemaphore rate_sem = new AESemaphore("ExternalSeedReaderRequest");
/*      */   
/*      */   private int rate_bytes_read;
/*      */   private int rate_bytes_permitted;
/*  134 */   private volatile CopyOnWriteSet<MutableInteger> bad_pieces = new CopyOnWriteSet(true);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ExternalSeedReaderImpl(ExternalSeedPlugin _plugin, Torrent _torrent, String _host, Map _params)
/*      */   {
/*  143 */     this.plugin = _plugin;
/*  144 */     this.torrent = _torrent;
/*  145 */     this.host = _host;
/*      */     
/*  147 */     this.host_net = AENetworkClassifier.categoriseAddress(this.host);
/*      */     
/*  149 */     this.fast_activate = getBooleanParam(_params, "fast_start", false);
/*  150 */     this.min_availability = getIntParam(_params, "min_avail", 1);
/*  151 */     this.min_download_speed = getIntParam(_params, "min_speed", 0);
/*  152 */     this.max_peer_speed = getIntParam(_params, "max_speed", 0);
/*  153 */     this.valid_until = getIntParam(_params, "valid_ms", 0);
/*      */     
/*  155 */     if (this.valid_until > 0L)
/*      */     {
/*  157 */       this.valid_until += getSystemTime();
/*      */     }
/*      */     
/*  160 */     this.transient_seed = getBooleanParam(_params, "transient", false);
/*      */     
/*  162 */     this.requests_mon = this.plugin.getPluginInterface().getUtilities().getMonitor();
/*  163 */     this.request_sem = this.plugin.getPluginInterface().getUtilities().getSemaphore();
/*      */     
/*  165 */     PluginInterface pi = this.plugin.getPluginInterface();
/*      */     
/*  167 */     this.user_agent = pi.getAzureusName();
/*      */     try
/*      */     {
/*  170 */       Properties props = new Properties();
/*      */       
/*  172 */       pi.getClientIDManager().getGenerator().generateHTTPProperties(this.torrent.getHash(), props);
/*      */       
/*  174 */       String ua = props.getProperty("User-Agent");
/*      */       
/*  176 */       if (ua != null)
/*      */       {
/*  178 */         this.user_agent = ua;
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  183 */     setActive(null, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getIP()
/*      */   {
/*  189 */     synchronized (this.host)
/*      */     {
/*  191 */       if (this.ip_use_accessor == null) {
/*      */         try
/*      */         {
/*  194 */           this.ip_use_accessor = HostNameToIPResolver.syncResolve(this.host).getHostAddress();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  198 */           this.ip_use_accessor = this.host;
/*      */           
/*  200 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/*  204 */       return this.ip_use_accessor;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Torrent getTorrent()
/*      */   {
/*  211 */     return this.torrent;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getStatus()
/*      */   {
/*  217 */     return this.status;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isTransient()
/*      */   {
/*  223 */     return this.transient_seed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/*  230 */     this.plugin.log(str);
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getUserAgent()
/*      */   {
/*  236 */     return this.user_agent;
/*      */   }
/*      */   
/*      */   protected long getSystemTime()
/*      */   {
/*  241 */     return this.plugin.getPluginInterface().getUtilities().getCurrentSystemTime();
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getFailureCount()
/*      */   {
/*  247 */     return this.consec_failures;
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getLastFailTime()
/*      */   {
/*  253 */     return this.last_failed_read;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPermanentlyUnavailable()
/*      */   {
/*  259 */     return this.permanent_fail;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setReconnectDelay(int delay, boolean reset_failures)
/*      */   {
/*  267 */     this.reconnect_delay = delay;
/*      */     
/*  269 */     if (reset_failures)
/*      */     {
/*  271 */       this.consec_failures = 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void eventOccurred(PeerManagerEvent event)
/*      */   {
/*  279 */     if (event.getType() == 4)
/*      */     {
/*  281 */       if (event.getPeer().getIp().equals(getIP()))
/*      */       {
/*  283 */         if (this.bad_pieces.size() > 128)
/*      */         {
/*  285 */           return;
/*      */         }
/*      */         
/*  288 */         this.bad_pieces.add(new MutableInteger(((Integer)event.getData()).intValue()));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean readyToActivate(PeerManager peer_manager, Peer peer, long time_since_start)
/*      */   {
/*  299 */     boolean early_days = time_since_start < 30000L;
/*      */     try
/*      */     {
/*  302 */       Download download = peer_manager.getDownload();
/*      */       
/*      */ 
/*      */ 
/*  306 */       int fail_count = getFailureCount();
/*      */       
/*  308 */       if (fail_count > 0)
/*      */       {
/*  310 */         int delay = this.reconnect_delay;
/*      */         
/*  312 */         for (int i = 1; i < fail_count; i++)
/*      */         {
/*  314 */           delay += delay;
/*      */           
/*  316 */           if (delay > 1800000) {
/*      */             break;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  322 */         long now = getSystemTime();
/*      */         
/*  324 */         long last_fail = getLastFailTime();
/*      */         
/*  326 */         if ((last_fail < now) && (now - last_fail < delay))
/*      */         {
/*  328 */           return false;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  334 */       if ((this.valid_until > 0L) && (getSystemTime() > this.valid_until))
/*      */       {
/*  336 */         return false;
/*      */       }
/*      */       
/*  339 */       if (download.getState() != 4)
/*      */       {
/*  341 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  346 */       if (download.isComplete())
/*      */       {
/*  348 */         return false;
/*      */       }
/*      */       
/*  351 */       if (!PluginCoreUtils.unwrap(download).getDownloadState().isNetworkEnabled(this.host_net))
/*      */       {
/*  353 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  358 */       if (this.transient_seed)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  363 */         Peer[] existing_peers = peer_manager.getPeers(getIP());
/*      */         
/*  365 */         int existing_peer_count = existing_peers.length;
/*      */         
/*  367 */         int global_limit = TransferSpeedValidator.getGlobalDownloadRateLimitBytesPerSecond();
/*      */         
/*  369 */         if (global_limit > 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  374 */           int current_down = this.plugin.getGlobalDownloadRateBytesPerSec();
/*      */           
/*  376 */           if (global_limit - current_down < 5120)
/*      */           {
/*  378 */             return false;
/*      */           }
/*      */         }
/*      */         
/*  382 */         int download_limit = peer_manager.getDownloadRateLimitBytesPerSecond();
/*      */         
/*  384 */         if ((global_limit > 0) && (global_limit < download_limit))
/*      */         {
/*  386 */           download_limit = global_limit;
/*      */         }
/*      */         
/*  389 */         if (((download_limit == 0) || (download_limit > 25600)) && (peer_manager.getStats().getDownloadAverage() < 20480L))
/*      */         {
/*      */ 
/*  392 */           for (int i = 0; i < existing_peers.length; i++)
/*      */           {
/*  394 */             Peer existing_peer = existing_peers[i];
/*      */             
/*      */ 
/*      */ 
/*  398 */             if (!(existing_peer instanceof ExternalSeedPeer))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  403 */               PeerStats stats = existing_peer.getStats();
/*      */               
/*  405 */               if (stats.getTimeSinceConnectionEstablished() > 30000L)
/*      */               {
/*  407 */                 if (stats.getDownloadAverage() < 5120)
/*      */                 {
/*  409 */                   existing_peer.close("Replacing slow peer with web-seed", false, false);
/*      */                   
/*  411 */                   existing_peer_count--;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  417 */         if (existing_peer_count == 0)
/*      */         {
/*      */ 
/*      */ 
/*  421 */           if (peer_manager.getPendingPeers(getIP()).length == 0)
/*      */           {
/*  423 */             log(getName() + ": activating as transient seed and nothing blocking it");
/*      */             
/*  425 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  432 */       if (!use_avail_to_activate)
/*      */       {
/*  434 */         log(getName() + ": activating as availability-based activation disabled");
/*      */         
/*  436 */         return true;
/*      */       }
/*      */       
/*  439 */       if ((this.fast_activate) || (!early_days))
/*      */       {
/*  441 */         if (this.min_availability > 0)
/*      */         {
/*  443 */           float availability = download.getStats().getAvailability();
/*      */           
/*  445 */           if (availability < this.min_availability)
/*      */           {
/*  447 */             log(getName() + ": activating as availability is poor");
/*      */             
/*  449 */             return true;
/*      */           }
/*      */         }
/*      */         
/*  453 */         if (this.min_download_speed > 0)
/*      */         {
/*  455 */           if (peer_manager.getStats().getDownloadAverage() < this.min_download_speed)
/*      */           {
/*  457 */             log(getName() + ": activating as speed is slow");
/*      */             
/*  459 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  466 */       DownloadAnnounceResult ar = download.getLastAnnounceResult();
/*      */       
/*  468 */       if (ar != null)
/*      */       {
/*  470 */         if (ar.getResponseType() == 2)
/*      */         {
/*  472 */           log(getName() + ": activating as tracker unavailable");
/*      */           
/*  474 */           return true;
/*      */         }
/*      */         
/*  477 */         if (ar.getSeedCount() == 0)
/*      */         {
/*  479 */           log(getName() + ": activating as no seeds");
/*      */           
/*  481 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  486 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*  489 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean readyToDeactivate(PeerManager peer_manager, Peer peer)
/*      */   {
/*      */     try
/*      */     {
/*  500 */       if ((this.valid_until > 0L) && (getSystemTime() > this.valid_until))
/*      */       {
/*  502 */         return true;
/*      */       }
/*      */       
/*  505 */       if (peer_manager.getDownload().getState() == 5)
/*      */       {
/*  507 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  512 */       if (this.transient_seed)
/*      */       {
/*  514 */         return false;
/*      */       }
/*      */       
/*  517 */       boolean deactivate = false;
/*  518 */       String reason = "";
/*      */       
/*  520 */       if (use_avail_to_activate)
/*      */       {
/*  522 */         if (this.min_availability > 0)
/*      */         {
/*  524 */           float availability = peer_manager.getDownload().getStats().getAvailability();
/*      */           
/*  526 */           if (availability >= this.min_availability + 1)
/*      */           {
/*  528 */             reason = "availability is good";
/*      */             
/*  530 */             deactivate = true;
/*      */           }
/*      */         }
/*      */         
/*  534 */         if (this.min_download_speed > 0)
/*      */         {
/*  536 */           long my_speed = peer.getStats().getDownloadAverage();
/*      */           
/*  538 */           long overall_speed = peer_manager.getStats().getDownloadAverage();
/*      */           
/*  540 */           if (overall_speed - my_speed > 2 * this.min_download_speed)
/*      */           {
/*  542 */             reason = reason + (reason.length() == 0 ? "" : ", ") + "speed is good";
/*      */             
/*  544 */             deactivate = true;
/*      */           }
/*      */           else
/*      */           {
/*  548 */             deactivate = false;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  553 */       if (deactivate)
/*      */       {
/*  555 */         log(getName() + ": deactivating as " + reason);
/*      */         
/*  557 */         return true;
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  561 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*  564 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean checkActivation(PeerManager peer_manager, Peer peer)
/*      */   {
/*  572 */     long now = getSystemTime();
/*      */     
/*  574 */     if (peer_manager == this.current_manager)
/*      */     {
/*  576 */       if (this.peer_manager_change_time > now)
/*      */       {
/*  578 */         this.peer_manager_change_time = now;
/*      */       }
/*      */       
/*  581 */       long time_since_started = now - this.peer_manager_change_time;
/*      */       
/*      */ 
/*  584 */       if (peer_manager != null)
/*      */       {
/*  586 */         if (this.active)
/*      */         {
/*  588 */           if ((now - this.peer_manager_change_time > 30000L) && (readyToDeactivate(peer_manager, peer)))
/*      */           {
/*  590 */             setActive(peer_manager, false);
/*      */ 
/*      */ 
/*      */           }
/*  594 */           else if (this.max_peer_speed > 0)
/*      */           {
/*  596 */             PeerStats ps = peer.getStats();
/*      */             
/*  598 */             if ((ps != null) && (ps.getDownloadRateLimit() != this.max_peer_speed))
/*      */             {
/*  600 */               ps.setDownloadRateLimit(this.max_peer_speed);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*  606 */         else if (!isPermanentlyUnavailable())
/*      */         {
/*  608 */           if (readyToActivate(peer_manager, peer, time_since_started))
/*      */           {
/*  610 */             if (this.max_peer_speed > 0)
/*      */             {
/*  612 */               PeerStats ps = peer.getStats();
/*      */               
/*  614 */               if (ps != null)
/*      */               {
/*  616 */                 ps.setDownloadRateLimit(this.max_peer_speed);
/*      */               }
/*      */             }
/*      */             
/*  620 */             setActive(peer_manager, true);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */     }
/*      */     else
/*      */     {
/*  630 */       this.peer_manager_change_time = now;
/*      */       
/*  632 */       PeerManager existing_manager = this.current_manager;
/*      */       
/*  634 */       if (this.current_manager != null)
/*      */       {
/*  636 */         this.current_manager.removeListener(this);
/*      */       }
/*      */       
/*  639 */       this.current_manager = peer_manager;
/*      */       
/*  641 */       if (this.current_manager != null)
/*      */       {
/*  643 */         this.current_manager.addListener(this);
/*      */       }
/*      */       
/*  646 */       setActive(existing_manager, false);
/*      */     }
/*      */     
/*  649 */     return this.active;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void deactivate(String reason)
/*      */   {
/*  656 */     this.plugin.log(getName() + ": deactivating (" + reason + ")");
/*      */     
/*  658 */     checkActivation(null, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setActive(PeerManager _peer_manager, boolean _active)
/*      */   {
/*      */     try
/*      */     {
/*  667 */       this.requests_mon.enter();
/*      */       
/*  669 */       this.active = _active;
/*      */       
/*  671 */       this.status = (this.active ? "Active" : "Idle");
/*      */       
/*  673 */       this.rate_bytes_permitted = 0;
/*  674 */       this.rate_bytes_read = 0;
/*      */       
/*  676 */       setActiveSupport(_peer_manager, _active);
/*      */     }
/*      */     finally
/*      */     {
/*  680 */       this.requests_mon.exit();
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
/*      */   public boolean isActive()
/*      */   {
/*  695 */     return this.active;
/*      */   }
/*      */   
/*      */   protected void processRequests()
/*      */   {
/*      */     try
/*      */     {
/*  702 */       this.requests_mon.enter();
/*      */       
/*  704 */       if (this.request_thread != null) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  709 */       this.request_thread = Thread.currentThread();
/*      */     }
/*      */     finally
/*      */     {
/*  713 */       this.requests_mon.exit();
/*      */     }
/*      */     try
/*      */     {
/*      */       for (;;)
/*      */       {
/*  719 */         if (!this.request_sem.reserve(30000L))
/*      */         {
/*      */           try {
/*  722 */             this.requests_mon.enter();
/*      */             
/*  724 */             if (this.requests.size() == 0)
/*      */             {
/*  726 */               this.dangling_requests = null;
/*      */               
/*  728 */               this.request_thread = null;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  734 */               this.requests_mon.exit(); break; } } finally { this.requests_mon.exit();
/*      */           }
/*      */         }
/*      */         else {
/*  738 */           Object selected_requests = new ArrayList();
/*  739 */           PeerReadRequest cancelled_request = null;
/*      */           try
/*      */           {
/*  742 */             this.requests_mon.enter();
/*      */             
/*      */ 
/*      */ 
/*  746 */             int count = selectRequests(this.requests);
/*      */             
/*  748 */             if ((count <= 0) || (count > this.requests.size()))
/*      */             {
/*  750 */               Debug.out("invalid count");
/*      */               
/*  752 */               count = 1;
/*      */             }
/*      */             
/*  755 */             for (int i = 0; i < count; i++)
/*      */             {
/*  757 */               PeerReadRequest request = (PeerReadRequest)this.requests.remove(0);
/*      */               
/*  759 */               if (request.isCancelled())
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  764 */                 if (i == 0)
/*      */                 {
/*  766 */                   cancelled_request = request; break;
/*      */                 }
/*      */                 
/*      */ 
/*  770 */                 this.requests.add(0, request);
/*      */                 
/*      */ 
/*  773 */                 break;
/*      */               }
/*      */               
/*      */ 
/*  777 */               ((List)selected_requests).add(request);
/*      */               
/*  779 */               if (i > 0)
/*      */               {
/*      */ 
/*      */ 
/*  783 */                 this.request_sem.reserve();
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*  788 */             this.dangling_requests = new ArrayList((Collection)selected_requests);
/*      */           }
/*      */           finally
/*      */           {
/*  792 */             this.requests_mon.exit();
/*      */           }
/*      */           
/*  795 */           if (cancelled_request != null)
/*      */           {
/*  797 */             informCancelled(cancelled_request);
/*      */           }
/*      */           else
/*      */           {
/*  801 */             processRequests((List)selected_requests);
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Throwable e) {
/*  806 */       e.printStackTrace();
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
/*      */   public int readBytes(int max)
/*      */   {
/*  824 */     int res = 0;
/*      */     
/*  826 */     synchronized (this.rate_sem)
/*      */     {
/*  828 */       if (this.rate_bytes_read > 0)
/*      */       {
/*  830 */         res = this.rate_bytes_read;
/*      */         
/*  832 */         if (res > max)
/*      */         {
/*  834 */           res = max;
/*      */         }
/*      */         
/*  837 */         this.rate_bytes_read -= res;
/*      */       }
/*      */       
/*  840 */       int rem = max - res;
/*      */       
/*  842 */       if (rem > this.rate_bytes_permitted)
/*      */       {
/*  844 */         if (this.rate_bytes_permitted == 0)
/*      */         {
/*  846 */           this.rate_sem.release();
/*      */         }
/*      */         
/*  849 */         this.rate_bytes_permitted = rem;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  854 */       if (this.rate_bytes_permitted > max * 10L)
/*      */       {
/*  856 */         this.rate_bytes_permitted = max;
/*      */       }
/*      */     }
/*      */     
/*  860 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getPermittedBytes()
/*      */     throws ExternalSeedException
/*      */   {
/*  868 */     synchronized (this.rate_sem)
/*      */     {
/*  870 */       if (this.rate_bytes_permitted > 0)
/*      */       {
/*  872 */         return this.rate_bytes_permitted;
/*      */       }
/*      */     }
/*      */     
/*  876 */     if (!this.rate_sem.reserve(1000L))
/*      */     {
/*  878 */       return 1;
/*      */     }
/*      */     
/*  881 */     return this.rate_bytes_permitted;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void reportBytesRead(int num)
/*      */   {
/*  888 */     synchronized (this.rate_sem)
/*      */     {
/*  890 */       this.rate_bytes_read += num;
/*      */       
/*  892 */       this.rate_bytes_permitted -= num;
/*      */       
/*  894 */       if (this.rate_bytes_permitted < 0)
/*      */       {
/*  896 */         this.rate_bytes_permitted = 0;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDoneOfCurrentIncomingRequest()
/*      */   {
/*  904 */     ExternalSeedReaderRequest cr = this.current_request;
/*      */     
/*  906 */     if (cr == null)
/*      */     {
/*  908 */       return -1;
/*      */     }
/*      */     
/*  911 */     return cr.getPercentDoneOfCurrentIncomingRequest();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaximumNumberOfRequests()
/*      */   {
/*  917 */     if (getRequestCount() == 0)
/*      */     {
/*  919 */       return (int)(getPieceGroupSize() * this.torrent.getPieceSize() / 16384L);
/*      */     }
/*      */     
/*      */ 
/*  923 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void calculatePriorityOffsets(PeerManager peer_manager, int[] base_priorities)
/*      */   {
/*      */     try
/*      */     {
/*  933 */       Piece[] pieces = peer_manager.getPieces();
/*      */       
/*  935 */       int piece_group_size = getPieceGroupSize();
/*      */       
/*  937 */       int[] contiguous_best_pieces = new int[piece_group_size];
/*  938 */       int[] contiguous_highest_pri = new int[piece_group_size];
/*      */       
/*  940 */       Arrays.fill(contiguous_highest_pri, -1);
/*      */       
/*  942 */       int contiguous = 0;
/*  943 */       int contiguous_best_pri = -1;
/*      */       
/*  945 */       int max_contiguous = 0;
/*      */       
/*  947 */       int max_free_reqs = 0;
/*  948 */       int max_free_reqs_piece = -1;
/*      */       
/*  950 */       MutableInteger mi = new MutableInteger(0);
/*      */       
/*  952 */       for (int i = 0; i < pieces.length; i++)
/*      */       {
/*  954 */         mi.setValue(i);
/*      */         
/*  956 */         if (!this.bad_pieces.contains(mi))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  961 */           Piece piece = pieces[i];
/*      */           
/*  963 */           if (piece.isFullyAllocatable())
/*      */           {
/*  965 */             contiguous++;
/*      */             
/*  967 */             int base_pri = base_priorities[i];
/*      */             
/*  969 */             if (base_pri > contiguous_best_pri)
/*      */             {
/*  971 */               contiguous_best_pri = base_pri;
/*      */             }
/*      */             
/*  974 */             for (int j = 0; (j < contiguous) && (j < contiguous_highest_pri.length); j++)
/*      */             {
/*  976 */               if (contiguous_best_pri > contiguous_highest_pri[j])
/*      */               {
/*  978 */                 contiguous_highest_pri[j] = contiguous_best_pri;
/*  979 */                 contiguous_best_pieces[j] = (i - j);
/*      */               }
/*      */               
/*  982 */               if (j + 1 > max_contiguous)
/*      */               {
/*  984 */                 max_contiguous = j + 1;
/*      */               }
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  990 */             contiguous = 0;
/*  991 */             contiguous_best_pri = -1;
/*      */             
/*  993 */             if (max_contiguous == 0)
/*      */             {
/*  995 */               int free_reqs = piece.getAllocatableRequestCount();
/*      */               
/*  997 */               if (free_reqs > max_free_reqs)
/*      */               {
/*  999 */                 max_free_reqs = free_reqs;
/* 1000 */                 max_free_reqs_piece = i;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1006 */       if (max_contiguous == 0)
/*      */       {
/* 1008 */         if (max_free_reqs_piece >= 0)
/*      */         {
/* 1010 */           this.priority_offsets = new int[(int)getTorrent().getPieceCount()];
/*      */           
/* 1012 */           this.priority_offsets[max_free_reqs_piece] = 100000;
/*      */         }
/*      */         else
/*      */         {
/* 1016 */           this.priority_offsets = null;
/*      */         }
/*      */       }
/*      */       else {
/* 1020 */         this.priority_offsets = new int[(int)getTorrent().getPieceCount()];
/*      */         
/* 1022 */         int start_piece = contiguous_best_pieces[(max_contiguous - 1)];
/*      */         
/* 1024 */         for (int i = start_piece; i < start_piece + max_contiguous; i++)
/*      */         {
/* 1026 */           this.priority_offsets[i] = (100000 - (i - start_piece));
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1031 */       Debug.printStackTrace(e);
/*      */       
/* 1033 */       this.priority_offsets = null;
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
/*      */   public int[] getPriorityOffsets()
/*      */   {
/* 1046 */     return this.priority_offsets;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int selectRequests(List<PeerReadRequest> requests)
/*      */   {
/* 1053 */     long next_start = -1L;
/*      */     
/* 1055 */     int last_piece_number = -1;
/*      */     
/* 1057 */     for (int i = 0; i < requests.size(); i++)
/*      */     {
/* 1059 */       PeerReadRequest request = (PeerReadRequest)requests.get(i);
/*      */       
/* 1061 */       int this_piece_number = request.getPieceNumber();
/*      */       
/* 1063 */       if ((last_piece_number != -1) && (last_piece_number != this_piece_number))
/*      */       {
/* 1065 */         if (!getRequestCanSpanPieces())
/*      */         {
/* 1067 */           return i;
/*      */         }
/*      */       }
/*      */       
/* 1071 */       long this_start = this_piece_number * this.torrent.getPieceSize() + request.getOffset();
/*      */       
/* 1073 */       if ((next_start != -1L) && (this_start != next_start))
/*      */       {
/* 1075 */         return i;
/*      */       }
/*      */       
/* 1078 */       next_start = this_start + request.getLength();
/*      */       
/* 1080 */       last_piece_number = this_piece_number;
/*      */     }
/*      */     
/* 1083 */     return requests.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] read(int piece_number, int piece_offset, int length, final int timeout)
/*      */     throws ExternalSeedException
/*      */   {
/* 1095 */     final byte[] result = new byte[length];
/*      */     
/* 1097 */     ExternalSeedHTTPDownloaderListener listener = new ExternalSeedHTTPDownloaderListener()
/*      */     {
/*      */       private int bp;
/*      */       
/* 1101 */       private long start_time = SystemTime.getCurrentTime();
/*      */       
/*      */ 
/*      */ 
/*      */       public byte[] getBuffer()
/*      */         throws ExternalSeedException
/*      */       {
/* 1108 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void setBufferPosition(int position)
/*      */       {
/* 1115 */         this.bp = position;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getBufferPosition()
/*      */       {
/* 1121 */         return this.bp;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getBufferLength()
/*      */       {
/* 1127 */         return result.length;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public int getPermittedBytes()
/*      */         throws ExternalSeedException
/*      */       {
/* 1135 */         return result.length;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getPermittedTime()
/*      */       {
/* 1141 */         if (timeout == 0)
/*      */         {
/* 1143 */           return 0;
/*      */         }
/*      */         
/* 1146 */         int rem = timeout - (int)(SystemTime.getCurrentTime() - this.start_time);
/*      */         
/* 1148 */         if (rem <= 0)
/*      */         {
/* 1150 */           return -1;
/*      */         }
/*      */         
/* 1153 */         return rem;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void reportBytesRead(int num) {}
/*      */       
/*      */ 
/*      */ 
/*      */       public boolean isCancelled()
/*      */       {
/* 1165 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void done() {}
/* 1173 */     };
/* 1174 */     readData(piece_number, piece_offset, length, listener);
/*      */     
/* 1176 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void readData(ExternalSeedReaderRequest request)
/*      */     throws ExternalSeedException
/*      */   {
/* 1185 */     readData(request.getStartPieceNumber(), request.getStartPieceOffset(), request.getLength(), request);
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
/*      */   protected void processRequests(List<PeerReadRequest> requests)
/*      */   {
/* 1201 */     boolean ok = false;
/*      */     
/* 1203 */     ExternalSeedReaderRequest request = new ExternalSeedReaderRequest(this, requests);
/*      */     
/* 1205 */     this.active_read_request = request;
/*      */     try
/*      */     {
/* 1208 */       this.current_request = request;
/*      */       
/* 1210 */       readData(request);
/*      */       
/* 1212 */       ok = true;
/*      */     }
/*      */     catch (ExternalSeedException e)
/*      */     {
/* 1216 */       if (e.isPermanentFailure())
/*      */       {
/* 1218 */         this.permanent_fail = true;
/*      */       }
/*      */       
/* 1221 */       this.status = ("Failed: " + Debug.getNestedExceptionMessage(e));
/*      */       
/* 1223 */       request.failed();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1227 */       this.status = ("Failed: " + Debug.getNestedExceptionMessage(e));
/*      */       
/* 1229 */       request.failed();
/*      */     }
/*      */     finally
/*      */     {
/* 1233 */       this.active_read_request = null;
/*      */       
/* 1235 */       if (ok)
/*      */       {
/* 1237 */         this.last_failed_read = 0L;
/*      */         
/* 1239 */         this.consec_failures = 0;
/*      */       }
/*      */       else {
/* 1242 */         this.last_failed_read = getSystemTime();
/*      */         
/* 1244 */         this.consec_failures += 1;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addRequests(List<PeerReadRequest> new_requests)
/*      */   {
/*      */     try
/*      */     {
/* 1254 */       this.requests_mon.enter();
/*      */       
/* 1256 */       if (!this.active)
/*      */       {
/* 1258 */         Debug.out("request added when not active!!!!");
/*      */       }
/*      */       
/* 1261 */       for (int i = 0; i < new_requests.size(); i++)
/*      */       {
/* 1263 */         this.requests.add(new_requests.get(i));
/*      */         
/* 1265 */         this.request_sem.release();
/*      */       }
/*      */       
/* 1268 */       if (this.request_thread == null)
/*      */       {
/* 1270 */         this.plugin.getPluginInterface().getUtilities().createThread("RequestProcessor", new Runnable()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/* 1277 */             ExternalSeedReaderImpl.this.processRequests();
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1284 */       this.requests_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void cancelRequest(PeerReadRequest request)
/*      */   {
/*      */     try
/*      */     {
/* 1293 */       this.requests_mon.enter();
/*      */       
/* 1295 */       if ((this.requests.contains(request)) && (!request.isCancelled()))
/*      */       {
/* 1297 */         request.cancel();
/*      */       }
/*      */       
/* 1300 */       if ((this.dangling_requests != null) && (this.dangling_requests.contains(request)) && (!request.isCancelled()))
/*      */       {
/* 1302 */         request.cancel();
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1307 */       this.requests_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void cancelAllRequests()
/*      */   {
/*      */     try
/*      */     {
/* 1315 */       this.requests_mon.enter();
/*      */       
/* 1317 */       for (PeerReadRequest request : this.requests)
/*      */       {
/* 1319 */         if (!request.isCancelled())
/*      */         {
/* 1321 */           request.cancel();
/*      */         }
/*      */       }
/*      */       
/* 1325 */       if (this.dangling_requests != null)
/*      */       {
/* 1327 */         for (PeerReadRequest request : this.dangling_requests)
/*      */         {
/* 1329 */           if (!request.isCancelled())
/*      */           {
/* 1331 */             request.cancel();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1336 */       if (this.active_read_request != null)
/*      */       {
/* 1338 */         this.active_read_request.cancel();
/*      */       }
/*      */     }
/*      */     finally {
/* 1342 */       this.requests_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public int getRequestCount()
/*      */   {
/*      */     try
/*      */     {
/* 1350 */       this.requests_mon.enter();
/*      */       
/* 1352 */       return this.requests.size();
/*      */     }
/*      */     finally
/*      */     {
/* 1356 */       this.requests_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public List<PeerReadRequest> getExpiredRequests()
/*      */   {
/* 1363 */     List<PeerReadRequest> res = null;
/*      */     try
/*      */     {
/* 1366 */       this.requests_mon.enter();
/*      */       
/* 1368 */       for (int i = 0; i < this.requests.size(); i++)
/*      */       {
/* 1370 */         PeerReadRequest request = (PeerReadRequest)this.requests.get(i);
/*      */         
/* 1372 */         if (request.isExpired())
/*      */         {
/* 1374 */           if (res == null)
/*      */           {
/* 1376 */             res = new ArrayList();
/*      */           }
/*      */           
/* 1379 */           res.add(request);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1384 */       this.requests_mon.exit();
/*      */     }
/*      */     
/* 1387 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public List<PeerReadRequest> getRequests()
/*      */   {
/* 1393 */     List<PeerReadRequest> res = null;
/*      */     try
/*      */     {
/* 1396 */       this.requests_mon.enter();
/*      */       
/* 1398 */       res = new ArrayList(this.requests);
/*      */     }
/*      */     finally
/*      */     {
/* 1402 */       this.requests_mon.exit();
/*      */     }
/*      */     
/* 1405 */     return res;
/*      */   }
/*      */   
/*      */   public int[] getOutgoingRequestedPieceNumbers()
/*      */   {
/*      */     try
/*      */     {
/* 1412 */       this.requests_mon.enter();
/*      */       
/* 1414 */       int size = this.requests.size();
/*      */       
/* 1416 */       if (this.dangling_requests != null)
/*      */       {
/* 1418 */         size += this.dangling_requests.size();
/*      */       }
/*      */       
/* 1421 */       int[] res = new int[size];
/*      */       
/* 1423 */       int pos = 0;
/*      */       
/* 1425 */       if (this.dangling_requests != null)
/*      */       {
/* 1427 */         for (PeerReadRequest r : this.dangling_requests)
/*      */         {
/* 1429 */           int piece_number = r.getPieceNumber();
/*      */           
/* 1431 */           boolean hit = false;
/*      */           
/* 1433 */           for (int i = 0; i < pos; i++)
/*      */           {
/* 1435 */             if (piece_number == res[i])
/*      */             {
/* 1437 */               hit = true;
/*      */               
/* 1439 */               break;
/*      */             }
/*      */           }
/*      */           
/* 1443 */           if (!hit)
/*      */           {
/* 1445 */             res[(pos++)] = piece_number;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1450 */       for (Iterator i$ = this.requests.iterator(); i$.hasNext();) { r = (PeerReadRequest)i$.next();
/*      */         
/* 1452 */         int piece_number = r.getPieceNumber();
/*      */         
/* 1454 */         boolean hit = false;
/*      */         
/* 1456 */         for (int i = 0; i < pos; i++)
/*      */         {
/* 1458 */           if (piece_number == res[i])
/*      */           {
/* 1460 */             hit = true;
/*      */             
/* 1462 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1466 */         if (!hit)
/*      */         {
/* 1468 */           res[(pos++)] = piece_number;
/*      */         }
/*      */       }
/*      */       PeerReadRequest r;
/* 1472 */       if (pos == res.length)
/*      */       {
/* 1474 */         return res;
/*      */       }
/*      */       
/* 1477 */       int[] trunc = new int[pos];
/*      */       
/* 1479 */       System.arraycopy(res, 0, trunc, 0, pos);
/*      */       
/* 1481 */       return trunc;
/*      */     }
/*      */     finally
/*      */     {
/* 1485 */       this.requests_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public int getOutgoingRequestCount()
/*      */   {
/*      */     try
/*      */     {
/* 1493 */       this.requests_mon.enter();
/*      */       
/* 1495 */       int res = this.requests.size();
/*      */       
/* 1497 */       if (this.dangling_requests != null)
/*      */       {
/* 1499 */         res += this.dangling_requests.size();
/*      */       }
/*      */       
/* 1502 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/* 1506 */       this.requests_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void informComplete(PeerReadRequest request, byte[] buffer)
/*      */   {
/* 1516 */     org.gudy.azureus2.plugins.utils.PooledByteBuffer pool_buffer = this.plugin.getPluginInterface().getUtilities().allocatePooledByteBuffer(buffer);
/*      */     
/* 1518 */     for (int i = 0; i < this.listeners.size(); i++) {
/*      */       try
/*      */       {
/* 1521 */         ((ExternalSeedReaderListener)this.listeners.get(i)).requestComplete(request, pool_buffer);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1525 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void informCancelled(PeerReadRequest request)
/*      */   {
/* 1534 */     for (int i = 0; i < this.listeners.size(); i++) {
/*      */       try
/*      */       {
/* 1537 */         ((ExternalSeedReaderListener)this.listeners.get(i)).requestCancelled(request);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1541 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void informFailed(PeerReadRequest request)
/*      */   {
/* 1550 */     for (int i = 0; i < this.listeners.size(); i++) {
/*      */       try
/*      */       {
/* 1553 */         ((ExternalSeedReaderListener)this.listeners.get(i)).requestFailed(request);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1557 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(ExternalSeedReaderListener l)
/*      */   {
/* 1566 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(ExternalSeedReaderListener l)
/*      */   {
/* 1573 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getIntParam(Map map, String name, int def)
/*      */   {
/* 1582 */     Object obj = map.get(name);
/*      */     
/* 1584 */     if ((obj instanceof Long))
/*      */     {
/* 1586 */       return ((Long)obj).intValue();
/*      */     }
/*      */     
/* 1589 */     return def;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1598 */   protected boolean getBooleanParam(Map map, String name, boolean def) { return getIntParam(map, name, def ? 1 : 0) != 0; }
/*      */   
/*      */   protected void setActiveSupport(PeerManager _peer_manager, boolean _active) {}
/*      */   
/*      */   protected abstract int getPieceGroupSize();
/*      */   
/*      */   protected abstract boolean getRequestCanSpanPieces();
/*      */   
/*      */   protected abstract void readData(int paramInt1, int paramInt2, int paramInt3, ExternalSeedHTTPDownloaderListener paramExternalSeedHTTPDownloaderListener) throws ExternalSeedException;
/*      */   
/*      */   protected static class MutableInteger { private int value;
/*      */     
/* 1610 */     protected MutableInteger(int v) { this.value = v; }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void setValue(int v)
/*      */     {
/* 1617 */       this.value = v;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getValue()
/*      */     {
/* 1623 */       return this.value;
/*      */     }
/*      */     
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1629 */       return this.value;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/* 1636 */       if ((obj instanceof MutableInteger)) {
/* 1637 */         return this.value == ((MutableInteger)obj).value;
/*      */       }
/* 1639 */       return false;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/impl/ExternalSeedReaderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */