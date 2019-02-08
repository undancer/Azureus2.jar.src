/*      */ package org.gudy.azureus2.core3.tracker.server.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.URL;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerPeer;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerPeerBase;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrent;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrentListener;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrentPeerListener;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrentStats;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TRTrackerServerTorrentImpl
/*      */   implements TRTrackerServerTorrent
/*      */ {
/*   47 */   private static final LogIDs LOGID = LogIDs.TRACKER;
/*      */   
/*      */   public static final int MIN_CACHE_ENTRY_SIZE = 10;
/*      */   
/*      */   public static final int MAX_UPLOAD_BYTES_PER_SEC = 3145728;
/*      */   
/*      */   public static final int MAX_DOWNLOAD_BYTES_PER_SEC = 3145728;
/*      */   
/*      */   public static final boolean USE_LIGHTWEIGHT_SEEDS = true;
/*      */   
/*      */   public static final int MAX_IP_OVERRIDE_PEERS = 64;
/*      */   
/*      */   public static final byte COMPACT_MODE_NONE = 0;
/*      */   
/*      */   public static final byte COMPACT_MODE_NORMAL = 1;
/*      */   
/*      */   public static final byte COMPACT_MODE_AZ = 2;
/*      */   
/*      */   public static final byte COMPACT_MODE_AZ_2 = 3;
/*      */   
/*      */   public static final byte COMPACT_MODE_XML = 16;
/*      */   private static final int QUEUED_PEERS_MAX_SWARM_SIZE = 32;
/*      */   private static final int QUEUED_PEERS_MAX = 32;
/*      */   private static final int QUEUED_PEERS_ADD_MAX = 3;
/*      */   private final TRTrackerServerImpl server;
/*      */   private final HashWrapper hash;
/*   73 */   private Map<HashWrapper, TRTrackerServerPeerImpl> peer_map = new HashMap();
/*      */   
/*   75 */   private Map<String, TRTrackerServerPeerImpl> peer_reuse_map = new HashMap();
/*      */   
/*   77 */   private List<TRTrackerServerPeerImpl> peer_list = new ArrayList();
/*      */   
/*      */   private int peer_list_hole_count;
/*      */   
/*      */   private boolean peer_list_compaction_suspended;
/*   82 */   private List biased_peers = null;
/*   83 */   private int min_biased_peers = 0;
/*      */   
/*   85 */   private final Map lightweight_seed_map = new HashMap();
/*      */   
/*      */   private int seed_count;
/*      */   
/*      */   private int removed_count;
/*      */   
/*      */   private int ip_override_count;
/*      */   
/*      */   private int bad_NAT_count;
/*   94 */   private final Random random = new Random(SystemTime.getCurrentTime());
/*      */   
/*      */   private long last_scrape_calc_time;
/*      */   
/*      */   private Map last_scrape;
/*   99 */   private final LinkedHashMap announce_cache = new LinkedHashMap();
/*      */   
/*      */   private final TRTrackerServerTorrentStatsImpl stats;
/*      */   
/*  103 */   private final List listeners = new ArrayList();
/*      */   
/*      */   private List peer_listeners;
/*      */   
/*      */   private boolean deleted;
/*      */   private boolean enabled;
/*      */   private boolean map_size_diff_reported;
/*      */   private boolean ip_override_limit_exceeded_reported;
/*  111 */   private byte duplicate_peer_checker_index = 0;
/*  112 */   private byte[] duplicate_peer_checker = new byte[0];
/*      */   
/*      */   private URL[] redirects;
/*      */   
/*  116 */   private boolean caching_enabled = true;
/*      */   
/*      */   private LinkedList queued_peers;
/*      */   
/*  120 */   protected final AEMonitor this_mon = new AEMonitor("TRTrackerServerTorrent");
/*      */   
/*      */ 
/*      */   private List explicit_manual_biased_peers;
/*      */   
/*      */ 
/*      */   private int explicit_next_peer;
/*      */   
/*      */ 
/*      */ 
/*      */   public TRTrackerServerTorrentImpl(TRTrackerServerImpl _server, HashWrapper _hash, boolean _enabled)
/*      */   {
/*  132 */     this.server = _server;
/*  133 */     this.hash = _hash;
/*  134 */     this.enabled = _enabled;
/*      */     
/*  136 */     this.stats = new TRTrackerServerTorrentStatsImpl(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setEnabled(boolean _enabled)
/*      */   {
/*  143 */     this.enabled = _enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/*  149 */     return this.enabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMinBiasedPeers(int num)
/*      */   {
/*  156 */     this.min_biased_peers = num;
/*      */   }
/*      */   
/*      */ 
/*      */   public void importPeers(List peers)
/*      */   {
/*      */     try
/*      */     {
/*  164 */       this.this_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*  168 */       if (this.peer_map.size() > 0)
/*      */       {
/*  170 */         System.out.println("TRTrackerServerTorrent: ignoring peer import as torrent already active");
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  175 */         for (int i = 0; i < peers.size(); i++)
/*      */         {
/*  177 */           TRTrackerServerPeerImpl peer = TRTrackerServerPeerImpl.importPeer((Map)peers.get(i));
/*      */           
/*  179 */           if (peer != null) {
/*      */             try
/*      */             {
/*  182 */               String reuse_key = new String(peer.getIPAsRead(), "ISO-8859-1") + ":" + peer.getTCPPort();
/*      */               
/*  184 */               this.peer_map.put(peer.getPeerId(), peer);
/*      */               
/*  186 */               this.peer_list.add(peer);
/*      */               
/*  188 */               this.peer_reuse_map.put(reuse_key, peer);
/*      */               
/*  190 */               if (peer.isSeed())
/*      */               {
/*  192 */                 this.seed_count += 1;
/*      */               }
/*      */               
/*  195 */               if (peer.isBiased())
/*      */               {
/*  197 */                 if (this.biased_peers == null)
/*      */                 {
/*  199 */                   this.biased_peers = new ArrayList();
/*      */                 }
/*      */                 
/*  202 */                 this.biased_peers.add(peer);
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/*  210 */       this.this_mon.exit();
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
/*      */   public TRTrackerServerPeerImpl peerContact(String url_parameters, String event, HashWrapper peer_id, int tcp_port, int udp_port, int http_port, byte crypto_level, byte az_ver, String original_address, String ip_address, boolean ip_override, boolean loopback, String tracker_key, long uploaded, long downloaded, long left, long interval_requested, int up_speed, DHTNetworkPosition network_position)
/*      */     throws TRTrackerServerException
/*      */   {
/*  238 */     if (!this.enabled)
/*      */     {
/*  240 */       throw new TRTrackerServerException("Torrent temporarily disabled");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  247 */     if (!HostNameToIPResolver.isNonDNSName(ip_address)) {
/*      */       try
/*      */       {
/*  250 */         ip_address = HostNameToIPResolver.syncResolve(ip_address).getHostAddress();
/*      */       }
/*      */       catch (UnknownHostException e) {}
/*      */     }
/*      */     
/*      */ 
/*  256 */     TRTrackerServerException deferred_failure = null;
/*      */     try
/*      */     {
/*  259 */       this.this_mon.enter();
/*      */       
/*  261 */       handleRedirects(url_parameters, ip_address, false);
/*      */       
/*      */ 
/*      */ 
/*  265 */       int event_type = 2;
/*      */       
/*  267 */       if ((event != null) && (event.length() > 2))
/*      */       {
/*  269 */         char c = event.charAt(2);
/*      */         
/*  271 */         if (c == 'm')
/*      */         {
/*  273 */           event_type = 3;
/*      */         }
/*  275 */         else if (c == 'o')
/*      */         {
/*  277 */           event_type = 4;
/*      */         }
/*      */         else
/*      */         {
/*  281 */           event_type = 1;
/*      */         }
/*      */       }
/*      */       
/*  285 */       long now = SystemTime.getCurrentTime();
/*      */       
/*  287 */       int tracker_key_hash_code = tracker_key == null ? 0 : tracker_key.hashCode();
/*      */       
/*  289 */       TRTrackerServerPeerImpl peer = (TRTrackerServerPeerImpl)this.peer_map.get(peer_id);
/*      */       
/*  291 */       boolean new_peer = false;
/*  292 */       boolean peer_already_removed = false;
/*      */       
/*  294 */       boolean already_completed = false;
/*  295 */       long last_contact_time = 0L;
/*      */       
/*  297 */       long ul_diff = 0L;
/*  298 */       long dl_diff = 0L;
/*  299 */       long le_diff = 0L;
/*      */       
/*  301 */       byte[] ip_address_bytes = ip_address.getBytes("ISO-8859-1");
/*      */       
/*  303 */       if (peer == null)
/*      */       {
/*  305 */         String reuse_key = new String(ip_address_bytes, "ISO-8859-1") + ":" + tcp_port;
/*      */         
/*  307 */         byte last_NAT_status = loopback ? 3 : 0;
/*      */         
/*  309 */         new_peer = true;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  318 */         TRTrackerServerPeerImpl old_peer = (TRTrackerServerPeerImpl)this.peer_reuse_map.get(reuse_key);
/*      */         
/*  320 */         if (old_peer != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  326 */           if ((ip_override) && (!old_peer.isIPOverride()))
/*      */           {
/*  328 */             throw new TRTrackerServerException("IP Override denied (existing '" + reuse_key + "' is not override)");
/*      */           }
/*      */           
/*  331 */           last_contact_time = old_peer.getLastContactTime();
/*      */           
/*  333 */           already_completed = old_peer.getDownloadCompleted();
/*      */           
/*  335 */           removePeer(old_peer, 5, null);
/*      */           
/*  337 */           this.lightweight_seed_map.remove(old_peer.getPeerId());
/*      */         }
/*      */         else
/*      */         {
/*  341 */           lightweightSeed lws = (lightweightSeed)this.lightweight_seed_map.remove(peer_id);
/*      */           
/*  343 */           if (lws != null)
/*      */           {
/*  345 */             last_contact_time = lws.getLastContactTime();
/*      */             
/*  347 */             ul_diff = uploaded - lws.getUploaded();
/*      */             
/*  349 */             if (ul_diff < 0L)
/*      */             {
/*  351 */               ul_diff = 0L;
/*      */             }
/*      */             
/*  354 */             last_NAT_status = lws.getNATStatus();
/*      */           }
/*      */           else
/*      */           {
/*  358 */             last_contact_time = now;
/*      */           }
/*      */         }
/*      */         
/*  362 */         if (event_type != 4)
/*      */         {
/*  364 */           Set biased_peer_set = this.server.getBiasedPeers();
/*      */           
/*  366 */           boolean biased = (biased_peer_set != null) && (biased_peer_set.contains(ip_address));
/*      */           
/*  368 */           if ((ip_override) && (this.ip_override_count >= 64) && (!loopback) && (!biased))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  374 */             if (!this.ip_override_limit_exceeded_reported)
/*      */             {
/*  376 */               this.ip_override_limit_exceeded_reported = true;
/*      */               
/*  378 */               Debug.out("Too many ip-override peers for " + ByteFormatter.encodeString(this.hash.getBytes()));
/*      */             }
/*      */             
/*  381 */             return null;
/*      */           }
/*      */           
/*  384 */           peer = new TRTrackerServerPeerImpl(peer_id, tracker_key_hash_code, ip_address_bytes, ip_override, tcp_port, udp_port, http_port, crypto_level, az_ver, last_contact_time, already_completed, last_NAT_status, up_speed, network_position);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  400 */           if (ip_override)
/*      */           {
/*      */ 
/*      */ 
/*  404 */             if (biased)
/*      */             {
/*      */ 
/*      */ 
/*  408 */               if (!biased_peer_set.contains(original_address))
/*      */               {
/*  410 */                 throw new TRTrackerServerException("IP Override denied (you are " + original_address + ")");
/*      */               }
/*      */             }
/*      */             
/*  414 */             this.ip_override_count += 1;
/*      */           }
/*      */           
/*  417 */           this.peer_map.put(peer_id, peer);
/*      */           
/*  419 */           this.peer_list.add(peer);
/*      */           
/*  421 */           this.peer_reuse_map.put(reuse_key, peer);
/*      */           
/*  423 */           if (biased)
/*      */           {
/*  425 */             peer.setBiased(true);
/*      */             
/*  427 */             if (this.biased_peers == null)
/*      */             {
/*  429 */               this.biased_peers = new ArrayList();
/*      */             }
/*      */             
/*  432 */             this.biased_peers.add(peer);
/*      */           }
/*      */           
/*  435 */           if (this.queued_peers != null)
/*      */           {
/*  437 */             if (this.peer_map.size() > 32)
/*      */             {
/*  439 */               this.queued_peers = null;
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  445 */               Iterator it = this.queued_peers.iterator();
/*      */               
/*  447 */               while (it.hasNext())
/*      */               {
/*  449 */                 QueuedPeer qp = (QueuedPeer)it.next();
/*      */                 
/*  451 */                 if (qp.sameAs(peer))
/*      */                 {
/*  453 */                   it.remove();
/*      */                   
/*  455 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  463 */         int existing_tracker_key_hash_code = peer.getKeyHashCode();
/*      */         
/*      */ 
/*      */ 
/*  467 */         if (existing_tracker_key_hash_code != tracker_key_hash_code)
/*      */         {
/*  469 */           if (this.server.isKeyEnabled())
/*      */           {
/*  471 */             throw new TRTrackerServerException("Unauthorised: key mismatch ");
/*      */           }
/*      */         }
/*      */         
/*  475 */         if (ip_override)
/*      */         {
/*      */ 
/*      */ 
/*  479 */           if (peer.isBiased())
/*      */           {
/*      */ 
/*      */ 
/*  483 */             Set biased_peer_set = this.server.getBiasedPeers();
/*      */             
/*  485 */             if ((biased_peer_set == null) || (!biased_peer_set.contains(original_address)))
/*      */             {
/*  487 */               throw new TRTrackerServerException("IP Override denied (you are " + original_address + ")");
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  493 */           if (!peer.isIPOverride())
/*      */           {
/*  495 */             throw new TRTrackerServerException("IP Override denied (existing entry not override)");
/*      */           }
/*      */         }
/*      */         
/*  499 */         already_completed = peer.getDownloadCompleted();
/*      */         
/*  501 */         last_contact_time = peer.getLastContactTime();
/*      */         
/*  503 */         if (event_type == 4)
/*      */         {
/*  505 */           removePeer(peer, event_type, url_parameters);
/*      */           
/*  507 */           peer_already_removed = true;
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  517 */           byte[] old_ip = peer.getIPAsRead();
/*  518 */           int old_port = peer.getTCPPort();
/*      */           
/*  520 */           if (peer.update(ip_address_bytes, tcp_port, udp_port, http_port, crypto_level, az_ver, up_speed, network_position))
/*      */           {
/*      */ 
/*      */ 
/*  524 */             String old_key = new String(old_ip, "ISO-8859-1") + ":" + old_port;
/*      */             
/*  526 */             String new_key = new String(ip_address_bytes, "ISO-8859-1") + ":" + tcp_port;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  533 */             TRTrackerServerPeerImpl old_peer = (TRTrackerServerPeerImpl)this.peer_reuse_map.get(new_key);
/*      */             
/*  535 */             if (old_peer != null)
/*      */             {
/*  537 */               removePeer(old_peer, 5, null);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  542 */             if (this.peer_reuse_map.remove(old_key) == null)
/*      */             {
/*  544 */               Debug.out("TRTrackerServerTorrent: IP address change: '" + old_key + "' -> '" + new_key + "': old key not found");
/*      */             }
/*      */             
/*  547 */             this.peer_reuse_map.put(new_key, peer);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  554 */       long new_timeout = now + interval_requested * 1000L * 3L;
/*      */       
/*  556 */       if (peer != null)
/*      */       {
/*  558 */         peer.setTimeout(now, new_timeout);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  566 */         if (!new_peer)
/*      */         {
/*  568 */           ul_diff = uploaded - peer.getUploaded();
/*  569 */           dl_diff = downloaded - peer.getDownloaded();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  574 */         long elapsed_time = now - last_contact_time;
/*      */         
/*  576 */         if (elapsed_time == 0L)
/*      */         {
/*  578 */           elapsed_time = 25L;
/*      */         }
/*      */         
/*  581 */         long ul_rate = ul_diff * 1000L / elapsed_time;
/*  582 */         long dl_rate = dl_diff * 1000L / elapsed_time;
/*      */         
/*  584 */         if (ul_rate > 3145728L)
/*      */         {
/*  586 */           if (Logger.isEnabled()) {
/*  587 */             Logger.log(new LogEvent(LOGID, "TRTrackerPeer: peer " + peer.getIPRaw() + "/" + new String(peer.getPeerId().getHash()) + " reported an upload rate of " + ul_rate / 1024L + " KiB/s per second"));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  593 */           ul_diff = 0L;
/*      */         }
/*      */         
/*  596 */         if (dl_rate > 3145728L) {
/*  597 */           if (Logger.isEnabled()) {
/*  598 */             Logger.log(new LogEvent(LOGID, "TRTrackerPeer: peer " + peer.getIPRaw() + "/" + new String(peer.getPeerId().getHash()) + " reported a download rate of " + dl_rate / 1024L + " KiB/s per second"));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  604 */           dl_diff = 0L;
/*      */         }
/*      */         
/*      */ 
/*  608 */         le_diff = event_type == 4 ? 0L : left - peer.getAmountLeft();
/*      */         
/*  610 */         boolean was_seed = new_peer ? false : peer.isSeed();
/*      */         
/*  612 */         peer.setStats(uploaded, downloaded, left);
/*      */         
/*  614 */         boolean is_seed = peer.isSeed();
/*      */         
/*  616 */         if ((event_type != 4) && (!was_seed) && (is_seed))
/*      */         {
/*  618 */           this.seed_count += 1;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  627 */         if (!peer_already_removed) {
/*      */           try
/*      */           {
/*  630 */             peerEvent(peer, event_type, url_parameters);
/*      */           }
/*      */           catch (TRTrackerServerException e)
/*      */           {
/*  634 */             deferred_failure = e;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  639 */       this.stats.addAnnounce(ul_diff, dl_diff, le_diff, (peer != null) && (peer.isBiased()));
/*      */       
/*  641 */       if ((event_type == 3) && (!already_completed))
/*      */       {
/*  643 */         peer.setDownloadCompleted();
/*      */         
/*  645 */         this.stats.addCompleted();
/*      */       }
/*      */       int seed_limit;
/*  648 */       if ((peer != null) && (peer.isSeed()))
/*      */       {
/*  650 */         seed_limit = TRTrackerServerImpl.getSeedLimit();
/*      */         
/*  652 */         if ((seed_limit != 0) && (this.seed_count > seed_limit) && (!loopback))
/*      */         {
/*  654 */           if (!peer_already_removed)
/*      */           {
/*  656 */             removePeer(peer, 6, null);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  662 */           throw new TRTrackerServerException("too many seeds");
/*      */         }
/*      */         
/*  665 */         int seed_retention = TRTrackerServerImpl.getMaxSeedRetention();
/*      */         
/*  667 */         if ((seed_retention != 0) && (this.seed_count > seed_retention))
/*      */         {
/*      */ 
/*      */ 
/*  671 */           int to_remove = seed_retention / 20 + 1;
/*      */           try
/*      */           {
/*  674 */             this.peer_list_compaction_suspended = true;
/*      */             
/*      */ 
/*      */ 
/*  678 */             for (int bad_nat_loop = TRTrackerServerNATChecker.getSingleton().isEnabled() ? 0 : 1; bad_nat_loop < 2; bad_nat_loop++)
/*      */             {
/*  680 */               for (int i = 0; i < this.peer_list.size(); i++)
/*      */               {
/*  682 */                 TRTrackerServerPeerImpl this_peer = (TRTrackerServerPeerImpl)this.peer_list.get(i);
/*      */                 
/*  684 */                 if ((this_peer != null) && (this_peer.isSeed()) && (!this_peer.isBiased()))
/*      */                 {
/*  686 */                   boolean bad_nat = this_peer.isNATStatusBad();
/*      */                   
/*  688 */                   if (((bad_nat_loop == 0) && (bad_nat)) || (bad_nat_loop == 1))
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*  693 */                     this.lightweight_seed_map.put(this_peer.getPeerId(), new lightweightSeed(now, new_timeout, this_peer.getUploaded(), this_peer.getNATStatus()));
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  702 */                     removePeer(this_peer, i, 6, null);
/*      */                     
/*  704 */                     to_remove--; if (to_remove == 0) {
/*      */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*  712 */               if (to_remove == 0) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/*  719 */             this.peer_list_compaction_suspended = false;
/*      */           }
/*      */           
/*  722 */           checkForPeerListCompaction(false);
/*      */         }
/*      */       }
/*      */       
/*  726 */       if (deferred_failure != null)
/*      */       {
/*  728 */         if ((peer != null) && (!peer_already_removed))
/*      */         {
/*  730 */           removePeer(peer, 7, url_parameters);
/*      */         }
/*      */         
/*  733 */         throw deferred_failure;
/*      */       }
/*      */       
/*  736 */       return peer;
/*      */     }
/*      */     catch (UnsupportedEncodingException e)
/*      */     {
/*  740 */       throw new TRTrackerServerException("Encoding fails", e);
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*  746 */       this.this_mon.exit();
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
/*      */   public void peerQueued(String ip, int tcp_port, int udp_port, int http_port, byte crypto_level, byte az_ver, long timeout_secs, boolean seed)
/*      */   {
/*  763 */     if ((this.peer_map.size() >= 32) || (tcp_port == 0))
/*      */     {
/*  765 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  769 */       this.this_mon.enter();
/*      */       
/*  771 */       Set biased_peer_set = this.server.getBiasedPeers();
/*      */       
/*  773 */       boolean biased = (biased_peer_set != null) && (biased_peer_set.contains(ip));
/*      */       
/*  775 */       QueuedPeer new_qp = new QueuedPeer(ip, tcp_port, udp_port, http_port, crypto_level, az_ver, (int)timeout_secs, seed, biased);
/*      */       
/*      */ 
/*      */ 
/*  779 */       String reuse_key = new_qp.getIP() + ":" + tcp_port;
/*      */       
/*      */ 
/*      */ 
/*  783 */       if (this.peer_reuse_map.containsKey(reuse_key)) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  788 */       boolean add = true;
/*      */       
/*  790 */       if (this.queued_peers != null)
/*      */       {
/*  792 */         Iterator it = this.queued_peers.iterator();
/*      */         
/*  794 */         while (it.hasNext())
/*      */         {
/*  796 */           QueuedPeer qp = (QueuedPeer)it.next();
/*      */           
/*  798 */           if (qp.sameAs(new_qp))
/*      */           {
/*  800 */             it.remove();
/*      */             
/*  802 */             this.queued_peers.add(new_qp); return;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  808 */         if (this.queued_peers.size() >= 32)
/*      */         {
/*  810 */           QueuedPeer oldest = null;
/*      */           
/*  812 */           it = this.queued_peers.iterator();
/*      */           
/*  814 */           while (it.hasNext())
/*      */           {
/*  816 */             QueuedPeer qp = (QueuedPeer)it.next();
/*      */             
/*      */ 
/*      */ 
/*  820 */             if (!qp.isBiased())
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  825 */               if (oldest == null)
/*      */               {
/*  827 */                 oldest = qp;
/*      */ 
/*      */ 
/*      */               }
/*  831 */               else if (qp.getCreateTime() < oldest.getCreateTime())
/*      */               {
/*  833 */                 oldest = qp;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  838 */           if (oldest == null)
/*      */           {
/*  840 */             add = false;
/*      */           }
/*      */           else
/*      */           {
/*  844 */             this.queued_peers.remove(oldest);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  849 */         this.queued_peers = new LinkedList();
/*      */       }
/*      */       
/*  852 */       if (add)
/*      */       {
/*  854 */         this.queued_peers.addFirst(new_qp);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  859 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void remove(TRTrackerServerPeerBase peer)
/*      */   {
/*      */     try
/*      */     {
/*  868 */       this.this_mon.enter();
/*      */       
/*  870 */       if ((peer instanceof TRTrackerServerPeerImpl))
/*      */       {
/*  872 */         TRTrackerServerPeerImpl pi = (TRTrackerServerPeerImpl)peer;
/*      */         
/*  874 */         if (this.peer_map.containsKey(pi.getPeerId()))
/*      */         {
/*  876 */           int index = this.peer_list.indexOf(pi);
/*      */           
/*  878 */           if (index != -1)
/*      */           {
/*  880 */             removePeer(pi, index, 7, null);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  885 */       else if (this.queued_peers != null)
/*      */       {
/*  887 */         this.queued_peers.remove(peer);
/*      */         
/*  889 */         if (this.queued_peers.size() == 0)
/*      */         {
/*  891 */           this.queued_peers = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  897 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void removePeer(TRTrackerServerPeerImpl peer, int reason, String url_parameters)
/*      */   {
/*  906 */     removePeer(peer, -1, reason, url_parameters);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void removePeer(TRTrackerServerPeerImpl peer, int peer_list_index, int reason, String url_parameters)
/*      */   {
/*      */     try
/*      */     {
/*  917 */       this.this_mon.enter();
/*      */       
/*  919 */       if (peer.isIPOverride())
/*      */       {
/*  921 */         this.ip_override_count -= 1;
/*      */       }
/*      */       
/*  924 */       this.stats.removeLeft(peer.getAmountLeft());
/*      */       
/*  926 */       if (this.peer_map.size() != this.peer_reuse_map.size())
/*      */       {
/*  928 */         if (!this.map_size_diff_reported)
/*      */         {
/*  930 */           this.map_size_diff_reported = true;
/*      */           
/*  932 */           Debug.out("TRTrackerServerTorrent::removePeer: maps size different ( " + this.peer_map.size() + "/" + this.peer_reuse_map.size() + ")");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  937 */       Object o = this.peer_map.remove(peer.getPeerId());
/*      */       
/*  939 */       if (o == null)
/*      */       {
/*  941 */         Debug.out(" TRTrackerServerTorrent::removePeer: peer_map doesn't contain peer");
/*      */       } else {
/*      */         try
/*      */         {
/*  945 */           peerEvent(peer, reason, url_parameters);
/*      */         }
/*      */         catch (TRTrackerServerException e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  953 */       if (peer_list_index == -1)
/*      */       {
/*  955 */         int peer_index = this.peer_list.indexOf(peer);
/*      */         
/*  957 */         if (peer_index == -1)
/*      */         {
/*  959 */           Debug.out(" TRTrackerServerTorrent::removePeer: peer_list doesn't contain peer");
/*      */         }
/*      */         else {
/*  962 */           this.peer_list.set(peer_index, null);
/*      */         }
/*      */         
/*      */       }
/*  966 */       else if (this.peer_list.get(peer_list_index) == peer)
/*      */       {
/*  968 */         this.peer_list.set(peer_list_index, null);
/*      */       }
/*      */       else
/*      */       {
/*  972 */         Debug.out(" TRTrackerServerTorrent::removePeer: peer_list doesn't contain peer at index");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  977 */       this.peer_list_hole_count += 1;
/*      */       
/*  979 */       checkForPeerListCompaction(false);
/*      */       try
/*      */       {
/*  982 */         Object o = this.peer_reuse_map.remove(new String(peer.getIPAsRead(), "ISO-8859-1") + ":" + peer.getTCPPort());
/*      */         
/*  984 */         if (o == null)
/*      */         {
/*  986 */           Debug.out(" TRTrackerServerTorrent::removePeer: peer_reuse_map doesn't contain peer");
/*      */         }
/*      */       }
/*      */       catch (UnsupportedEncodingException e) {}
/*      */       
/*      */ 
/*  992 */       if (this.biased_peers != null)
/*      */       {
/*  994 */         this.biased_peers.remove(peer);
/*      */       }
/*      */       
/*  997 */       if (peer.isSeed())
/*      */       {
/*  999 */         this.seed_count -= 1;
/*      */       }
/*      */       
/* 1002 */       this.removed_count += 1;
/*      */     }
/*      */     finally
/*      */     {
/* 1006 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updateBiasedPeers(Set biased_peers_set)
/*      */   {
/*      */     try
/*      */     {
/* 1015 */       this.this_mon.enter();
/*      */       
/* 1017 */       Iterator it = this.peer_list.iterator();
/*      */       
/* 1019 */       if ((it.hasNext()) && (this.biased_peers == null))
/*      */       {
/* 1021 */         this.biased_peers = new ArrayList();
/*      */       }
/*      */       
/* 1024 */       while (it.hasNext())
/*      */       {
/* 1026 */         TRTrackerServerPeerImpl this_peer = (TRTrackerServerPeerImpl)it.next();
/*      */         
/* 1028 */         if (this_peer != null)
/*      */         {
/* 1030 */           boolean biased = biased_peers_set.contains(this_peer.getIPRaw());
/*      */           
/* 1032 */           this_peer.setBiased(biased);
/*      */           
/* 1034 */           if (biased)
/*      */           {
/* 1036 */             if (!this.biased_peers.contains(this_peer))
/*      */             {
/* 1038 */               this.biased_peers.add(this_peer);
/*      */             }
/*      */           }
/*      */           else {
/* 1042 */             this.biased_peers.remove(this_peer);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1047 */       if (this.queued_peers != null)
/*      */       {
/* 1049 */         it = this.queued_peers.iterator();
/*      */         
/* 1051 */         while (it.hasNext())
/*      */         {
/* 1053 */           QueuedPeer peer = (QueuedPeer)it.next();
/*      */           
/* 1055 */           peer.setBiased(biased_peers_set.contains(peer.getIP()));
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1060 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TRTrackerServerTorrent addLink(String link)
/*      */   {
/* 1068 */     return this.server.addLink(link, this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeLink(String link)
/*      */   {
/* 1075 */     this.server.removeLink(link, this);
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
/*      */   public Map exportAnnounceToMap(String ip_address, HashMap preprocess_map, TRTrackerServerPeerImpl requesting_peer, boolean include_seeds, int num_want, long interval, long min_interval, boolean no_peer_id, byte compact_mode, byte crypto_level, DHTNetworkPosition network_position)
/*      */   {
/*      */     try
/*      */     {
/* 1093 */       this.this_mon.enter();
/*      */       
/* 1095 */       long now = SystemTime.getCurrentTime();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1100 */       boolean nat_warning = (requesting_peer != null) && (requesting_peer.getNATStatus() == 4);
/*      */       
/* 1102 */       int total_peers = this.peer_map.size();
/* 1103 */       int cache_millis = TRTrackerServerImpl.getAnnounceCachePeriod();
/*      */       
/* 1105 */       boolean send_peer_ids = TRTrackerServerImpl.getSendPeerIds();
/*      */       
/*      */ 
/*      */ 
/* 1109 */       if ((no_peer_id) || (compact_mode != 0))
/*      */       {
/* 1111 */         send_peer_ids = false;
/*      */       }
/*      */       
/* 1114 */       boolean add_to_cache = false;
/*      */       
/* 1116 */       int max_peers = TRTrackerServerImpl.getMaxPeersToSend();
/*      */       
/*      */ 
/*      */ 
/* 1120 */       if (num_want < 0)
/*      */       {
/* 1122 */         num_want = total_peers;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1127 */       if ((max_peers > 0) && (num_want > max_peers))
/*      */       {
/* 1129 */         num_want = max_peers;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1135 */       List<TRTrackerServerSimplePeer> explicit_limited_peers = null;
/* 1136 */       List<TRTrackerServerSimplePeer> explicit_biased_peers = null;
/*      */       
/* 1138 */       Set remove_ips = null;
/*      */       int j;
/* 1140 */       if (requesting_peer != null)
/*      */       {
/* 1142 */         if (this.peer_listeners != null)
/*      */         {
/* 1144 */           for (int i = 0; i < this.peer_listeners.size(); i++) {
/*      */             try
/*      */             {
/* 1147 */               Map reply = ((TRTrackerServerTorrentPeerListener)this.peer_listeners.get(i)).eventOccurred(this, requesting_peer, 8, null);
/*      */               
/* 1149 */               if (reply != null)
/*      */               {
/* 1151 */                 List limited_peers = (List)reply.get("limited_peers");
/*      */                 
/* 1153 */                 if (limited_peers != null)
/*      */                 {
/* 1155 */                   if (explicit_limited_peers == null)
/*      */                   {
/* 1157 */                     explicit_limited_peers = new ArrayList();
/*      */                   }
/*      */                   
/* 1160 */                   for (int j = 0; j < limited_peers.size(); j++)
/*      */                   {
/* 1162 */                     Map peer_map = (Map)limited_peers.get(j);
/*      */                     
/* 1164 */                     String ip = (String)peer_map.get("ip");
/* 1165 */                     int port = ((Long)peer_map.get("port")).intValue();
/*      */                     
/* 1167 */                     String reuse_key = ip + ":" + port;
/*      */                     
/* 1169 */                     TRTrackerServerPeerImpl peer = (TRTrackerServerPeerImpl)this.peer_reuse_map.get(reuse_key);
/*      */                     
/* 1171 */                     if ((peer != null) && (!explicit_limited_peers.contains(peer)))
/*      */                     {
/* 1173 */                       explicit_limited_peers.add(peer);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/* 1178 */                 List biased_peers = (List)reply.get("biased_peers");
/*      */                 
/* 1180 */                 if (biased_peers != null)
/*      */                 {
/* 1182 */                   if (explicit_biased_peers == null)
/*      */                   {
/* 1184 */                     explicit_biased_peers = new ArrayList();
/*      */                   }
/*      */                   
/* 1187 */                   for (j = 0; j < biased_peers.size(); j++)
/*      */                   {
/* 1189 */                     Map peer_map = (Map)biased_peers.get(j);
/*      */                     
/* 1191 */                     String ip = (String)peer_map.get("ip");
/* 1192 */                     int port = ((Long)peer_map.get("port")).intValue();
/*      */                     
/* 1194 */                     String reuse_key = ip + ":" + port;
/*      */                     
/* 1196 */                     TRTrackerServerSimplePeer peer = (TRTrackerServerSimplePeer)this.peer_reuse_map.get(reuse_key);
/*      */                     
/* 1198 */                     if (peer == null)
/*      */                     {
/* 1200 */                       peer = new temporaryBiasedSeed(ip, port);
/*      */                     }
/*      */                     
/* 1203 */                     if (!explicit_biased_peers.contains(peer))
/*      */                     {
/* 1205 */                       explicit_biased_peers.add(peer);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/* 1210 */                 remove_ips = (Set)reply.get("remove_ips");
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 1214 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */       boolean requester_is_biased;
/*      */       boolean requester_is_biased;
/* 1222 */       if (requesting_peer == null)
/*      */       {
/* 1224 */         Set bp = this.server.getBiasedPeers();
/*      */         boolean requester_is_biased;
/* 1226 */         if (bp == null)
/*      */         {
/* 1228 */           requester_is_biased = false;
/*      */         }
/*      */         else
/*      */         {
/* 1232 */           requester_is_biased = bp.contains(ip_address);
/*      */         }
/*      */       }
/*      */       else {
/* 1236 */         requester_is_biased = requesting_peer.isBiased();
/*      */       }
/*      */       
/* 1239 */       if ((this.caching_enabled) && (explicit_limited_peers == null) && (explicit_biased_peers == null) && (!requester_is_biased) && (remove_ips == null) && (!nat_warning) && (preprocess_map.size() == 0) && (cache_millis > 0) && (num_want >= 10) && (total_peers >= TRTrackerServerImpl.getAnnounceCachePeerThreshold()) && (crypto_level != 2))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1253 */         network_position = null;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1260 */         Iterator it = this.announce_cache.keySet().iterator();
/*      */         
/* 1262 */         while (it.hasNext())
/*      */         {
/* 1264 */           Integer key = (Integer)it.next();
/*      */           
/* 1266 */           announceCacheEntry entry = (announceCacheEntry)this.announce_cache.get(key);
/*      */           
/* 1268 */           if (now - entry.getTime() > cache_millis)
/*      */           {
/* 1270 */             it.remove();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1277 */         for (int i = num_want / 10; i > num_want / 20; i--)
/*      */         {
/* 1279 */           announceCacheEntry entry = (announceCacheEntry)this.announce_cache.get(new Integer(i));
/*      */           
/* 1281 */           if (entry != null)
/*      */           {
/* 1283 */             if (now - entry.getTime() > cache_millis)
/*      */             {
/* 1285 */               this.announce_cache.remove(new Integer(i));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             }
/* 1291 */             else if ((entry.getSendPeerIds() == send_peer_ids) && (entry.getCompactMode() == compact_mode))
/*      */             {
/*      */ 
/* 1294 */               return entry.getData();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1300 */         add_to_cache = true;
/*      */       }
/*      */       
/*      */ 
/* 1304 */       LinkedList rep_peers = new LinkedList();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1311 */       if ((num_want > 0) && (explicit_limited_peers == null))
/*      */       {
/* 1313 */         if (num_want >= total_peers)
/*      */         {
/*      */ 
/*      */ 
/* 1317 */           for (int i = 0; i < this.peer_list.size(); i++)
/*      */           {
/* 1319 */             TRTrackerServerPeerImpl peer = (TRTrackerServerPeerImpl)this.peer_list.get(i);
/*      */             
/* 1321 */             if ((peer != null) && (peer != requesting_peer))
/*      */             {
/* 1323 */               if (now > peer.getTimeout())
/*      */               {
/*      */ 
/*      */ 
/* 1327 */                 removePeer(peer, i, 5, null);
/*      */               }
/* 1329 */               else if (peer.getTCPPort() != 0)
/*      */               {
/*      */ 
/*      */ 
/* 1333 */                 if ((crypto_level != 0) || (peer.getCryptoLevel() != 2))
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
/* 1344 */                   if ((remove_ips == null) || (!remove_ips.contains(new String(peer.getIP()))))
/*      */                   {
/*      */ 
/*      */ 
/* 1348 */                     if ((include_seeds) || (!peer.isSeed()))
/*      */                     {
/* 1350 */                       Map rep_peer = new HashMap(3);
/*      */                       
/* 1352 */                       if (send_peer_ids)
/*      */                       {
/* 1354 */                         rep_peer.put("peer id", peer.getPeerId().getHash());
/*      */                       }
/*      */                       
/* 1357 */                       if (compact_mode != 0)
/*      */                       {
/* 1359 */                         byte[] peer_bytes = peer.getIPAddressBytes();
/*      */                         
/* 1361 */                         if (peer_bytes == null) {
/*      */                           continue;
/*      */                         }
/*      */                         
/*      */ 
/* 1366 */                         rep_peer.put("ip", peer_bytes);
/*      */                         
/* 1368 */                         if (compact_mode >= 2)
/*      */                         {
/* 1370 */                           rep_peer.put("azver", new Long(peer.getAZVer()));
/*      */                           
/* 1372 */                           rep_peer.put("azudp", new Long(peer.getUDPPort()));
/*      */                           
/* 1374 */                           if (peer.isSeed())
/*      */                           {
/* 1376 */                             rep_peer.put("azhttp", new Long(peer.getHTTPPort()));
/*      */                           }
/*      */                           
/* 1379 */                           if (compact_mode >= 16)
/*      */                           {
/* 1381 */                             rep_peer.put("ip", peer.getIPAsRead());
/*      */                           }
/*      */                           else
/*      */                           {
/* 1385 */                             rep_peer.put("azup", new Long(peer.getUpSpeed()));
/*      */                             
/* 1387 */                             if (peer.isBiased())
/*      */                             {
/* 1389 */                               rep_peer.put("azbiased", "");
/*      */                             }
/*      */                             
/* 1392 */                             if (network_position != null)
/*      */                             {
/* 1394 */                               DHTNetworkPosition peer_pos = peer.getNetworkPosition();
/*      */                               
/* 1396 */                               if ((peer_pos != null) && (network_position.getPositionType() == peer_pos.getPositionType()))
/*      */                               {
/* 1398 */                                 rep_peer.put("azrtt", new Long(peer_pos.estimateRTT(network_position)));
/*      */                               }
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                       else {
/* 1405 */                         rep_peer.put("ip", peer.getIPAsRead());
/*      */                       }
/*      */                       
/* 1408 */                       rep_peer.put("port", new Long(peer.getTCPPort()));
/*      */                       
/* 1410 */                       if (crypto_level != 0)
/*      */                       {
/* 1412 */                         rep_peer.put("crypto_flag", new Long(peer.getCryptoLevel() == 2 ? 1L : 0L));
/*      */                       }
/*      */                       
/* 1415 */                       if (peer.isBiased())
/*      */                       {
/* 1417 */                         rep_peers.addFirst(rep_peer);
/*      */                       }
/*      */                       else
/*      */                       {
/* 1421 */                         rep_peers.addLast(rep_peer); }
/*      */                     } } }
/*      */               }
/*      */             }
/*      */           }
/*      */         } else {
/* 1427 */           int peer_list_size = this.peer_list.size();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1432 */           if (this.duplicate_peer_checker.length < peer_list_size)
/*      */           {
/* 1434 */             this.duplicate_peer_checker = new byte[peer_list_size * 2];
/*      */             
/* 1436 */             this.duplicate_peer_checker_index = 1;
/*      */           }
/* 1438 */           else if (this.duplicate_peer_checker.length > peer_list_size * 2)
/*      */           {
/* 1440 */             this.duplicate_peer_checker = new byte[3 * peer_list_size / 2];
/*      */             
/* 1442 */             this.duplicate_peer_checker_index = 1;
/*      */           }
/*      */           else
/*      */           {
/* 1446 */             this.duplicate_peer_checker_index = ((byte)(this.duplicate_peer_checker_index + 1));
/*      */             
/* 1448 */             if (this.duplicate_peer_checker_index == 0)
/*      */             {
/* 1450 */               Arrays.fill(this.duplicate_peer_checker, (byte)0);
/*      */               
/* 1452 */               this.duplicate_peer_checker_index = 1;
/*      */             }
/*      */           }
/*      */           
/* 1456 */           boolean peer_removed = false;
/*      */           
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/* 1462 */             this.peer_list_compaction_suspended = true;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1469 */             int added = 0;
/*      */             
/*      */ 
/* 1472 */             for (int bad_nat_loop = TRTrackerServerNATChecker.getSingleton().isEnabled() ? 0 : 1; bad_nat_loop < 2; bad_nat_loop++)
/*      */             {
/* 1474 */               int limit = num_want * 2;
/*      */               
/*      */ 
/* 1477 */               if (num_want * 3 > total_peers)
/*      */               {
/* 1479 */                 limit++;
/*      */               }
/*      */               
/* 1482 */               int biased_peers_count = 0;
/*      */               
/* 1484 */               if (this.biased_peers != null)
/*      */               {
/* 1486 */                 if (this.biased_peers.size() > 1)
/*      */                 {
/*      */ 
/*      */ 
/* 1490 */                   Object x = this.biased_peers.remove(0);
/*      */                   
/* 1492 */                   this.biased_peers.add(this.random.nextInt(this.biased_peers.size() + 1), x);
/*      */                 }
/*      */                 
/* 1495 */                 biased_peers_count = Math.min(this.min_biased_peers, this.biased_peers.size());
/*      */               }
/*      */               
/* 1498 */               for (int i = 0; (i < limit) && (added < num_want); i++)
/*      */               {
/*      */                 int peer_index;
/*      */                 
/*      */                 int peer_index;
/*      */                 
/*      */                 TRTrackerServerPeerImpl peer;
/*      */                 
/* 1506 */                 if ((bad_nat_loop == 1) && (i < biased_peers_count))
/*      */                 {
/* 1508 */                   TRTrackerServerPeerImpl peer = (TRTrackerServerPeerImpl)this.biased_peers.get(i);
/*      */                   
/* 1510 */                   peer_index = -1;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1514 */                   peer_index = this.random.nextInt(peer_list_size);
/*      */                   
/* 1516 */                   peer = (TRTrackerServerPeerImpl)this.peer_list.get(peer_index);
/*      */                   
/* 1518 */                   if ((peer == null) || (peer.isBiased())) {
/*      */                     continue;
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/* 1524 */                 if (now > peer.getTimeout())
/*      */                 {
/* 1526 */                   removePeer(peer, 5, null);
/*      */                   
/* 1528 */                   peer_removed = true;
/*      */                 }
/* 1530 */                 else if ((requesting_peer != peer) && (peer.getTCPPort() != 0))
/*      */                 {
/*      */ 
/*      */ 
/* 1534 */                   if ((crypto_level != 0) || (peer.getCryptoLevel() != 2))
/*      */                   {
/*      */ 
/*      */ 
/* 1538 */                     if ((remove_ips == null) || (!remove_ips.contains(new String(peer.getIP()))))
/*      */                     {
/*      */ 
/*      */ 
/* 1542 */                       if ((include_seeds) || (!peer.isSeed()))
/*      */                       {
/* 1544 */                         boolean bad_nat = peer.isNATStatusBad();
/*      */                         
/* 1546 */                         if (((bad_nat_loop == 0) && (!bad_nat)) || (bad_nat_loop == 1))
/*      */                         {
/*      */ 
/* 1549 */                           if ((peer_index == -1) || (this.duplicate_peer_checker[peer_index] != this.duplicate_peer_checker_index))
/*      */                           {
/* 1551 */                             if (peer_index != -1)
/*      */                             {
/* 1553 */                               this.duplicate_peer_checker[peer_index] = this.duplicate_peer_checker_index;
/*      */                             }
/*      */                             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1561 */                             added++;
/*      */                             
/* 1563 */                             Map rep_peer = new HashMap(3);
/*      */                             
/* 1565 */                             if (send_peer_ids)
/*      */                             {
/* 1567 */                               rep_peer.put("peer id", peer.getPeerId().getHash());
/*      */                             }
/*      */                             
/* 1570 */                             if (compact_mode != 0)
/*      */                             {
/* 1572 */                               byte[] peer_bytes = peer.getIPAddressBytes();
/*      */                               
/* 1574 */                               if (peer_bytes == null) {
/*      */                                 continue;
/*      */                               }
/*      */                               
/*      */ 
/* 1579 */                               rep_peer.put("ip", peer_bytes);
/*      */                               
/* 1581 */                               if (compact_mode >= 2)
/*      */                               {
/* 1583 */                                 rep_peer.put("azver", new Long(peer.getAZVer()));
/*      */                                 
/* 1585 */                                 rep_peer.put("azudp", new Long(peer.getUDPPort()));
/*      */                                 
/* 1587 */                                 if (peer.isSeed())
/*      */                                 {
/* 1589 */                                   rep_peer.put("azhttp", new Long(peer.getHTTPPort()));
/*      */                                 }
/*      */                                 
/* 1592 */                                 if (compact_mode >= 16)
/*      */                                 {
/* 1594 */                                   rep_peer.put("ip", peer.getIPAsRead());
/*      */                                 }
/*      */                                 else
/*      */                                 {
/* 1598 */                                   rep_peer.put("azup", new Long(peer.getUpSpeed()));
/*      */                                   
/* 1600 */                                   if (peer.isBiased())
/*      */                                   {
/* 1602 */                                     rep_peer.put("azbiased", "");
/*      */                                   }
/*      */                                   
/* 1605 */                                   if (network_position != null)
/*      */                                   {
/* 1607 */                                     DHTNetworkPosition peer_pos = peer.getNetworkPosition();
/*      */                                     
/* 1609 */                                     if ((peer_pos != null) && (network_position.getPositionType() == peer_pos.getPositionType()))
/*      */                                     {
/* 1611 */                                       rep_peer.put("azrtt", new Long(peer_pos.estimateRTT(network_position)));
/*      */                                     }
/*      */                                   }
/*      */                                 }
/*      */                               }
/*      */                             }
/*      */                             else {
/* 1618 */                               rep_peer.put("ip", peer.getIPAsRead());
/*      */                             }
/*      */                             
/* 1621 */                             rep_peer.put("port", new Long(peer.getTCPPort()));
/*      */                             
/* 1623 */                             if (crypto_level != 0)
/*      */                             {
/* 1625 */                               rep_peer.put("crypto_flag", new Long(peer.getCryptoLevel() == 2 ? 1L : 0L));
/*      */                             }
/*      */                             
/* 1628 */                             if (peer.isBiased())
/*      */                             {
/* 1630 */                               rep_peers.addFirst(rep_peer);
/*      */                             }
/*      */                             else
/*      */                             {
/* 1634 */                               rep_peers.addLast(rep_peer);
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/* 1646 */             this.peer_list_compaction_suspended = false;
/*      */             
/* 1648 */             if (peer_removed)
/*      */             {
/* 1650 */               checkForPeerListCompaction(false);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1733 */       if ((include_seeds) && (explicit_limited_peers == null) && (!send_peer_ids) && (this.seed_count < 3) && (this.queued_peers != null))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1739 */         Iterator it = this.queued_peers.iterator();
/*      */         
/* 1741 */         List added = new ArrayList(3);
/*      */         
/* 1743 */         while ((it.hasNext()) && (num_want > rep_peers.size()) && (added.size() < 3))
/*      */         {
/* 1745 */           QueuedPeer peer = (QueuedPeer)it.next();
/*      */           
/* 1747 */           if (peer.isTimedOut(now))
/*      */           {
/* 1749 */             it.remove();
/*      */           }
/* 1751 */           else if ((crypto_level != 0) || (peer.getCryptoLevel() != 2))
/*      */           {
/*      */ 
/*      */ 
/* 1755 */             if ((remove_ips == null) || (!remove_ips.contains(peer.getIP())))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1761 */               Map rep_peer = new HashMap(3);
/*      */               
/* 1763 */               if (compact_mode != 0)
/*      */               {
/* 1765 */                 byte[] peer_bytes = peer.getIPAddressBytes();
/*      */                 
/* 1767 */                 if (peer_bytes == null) {
/*      */                   continue;
/*      */                 }
/*      */                 
/*      */ 
/* 1772 */                 rep_peer.put("ip", peer_bytes);
/*      */                 
/* 1774 */                 if (compact_mode >= 2)
/*      */                 {
/* 1776 */                   rep_peer.put("azver", new Long(peer.getAZVer()));
/*      */                   
/* 1778 */                   rep_peer.put("azudp", new Long(peer.getUDPPort()));
/*      */                   
/* 1780 */                   if (peer.isSeed())
/*      */                   {
/* 1782 */                     rep_peer.put("azhttp", new Long(peer.getHTTPPort()));
/*      */                   }
/*      */                   
/* 1785 */                   if (compact_mode >= 16)
/*      */                   {
/* 1787 */                     rep_peer.put("ip", peer.getIPAsRead());
/*      */                   }
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/* 1793 */                 rep_peer.put("ip", peer.getIPAsRead());
/*      */               }
/*      */               
/* 1796 */               rep_peer.put("port", new Long(peer.getTCPPort()));
/*      */               
/* 1798 */               if (crypto_level != 0)
/*      */               {
/* 1800 */                 rep_peer.put("crypto_flag", new Long(peer.getCryptoLevel() == 2 ? 1L : 0L));
/*      */               }
/*      */               
/*      */ 
/*      */ 
/* 1805 */               rep_peers.addLast(rep_peer);
/*      */               
/* 1807 */               added.add(peer);
/*      */               
/*      */ 
/*      */ 
/* 1811 */               it.remove();
/*      */             }
/*      */           }
/*      */         }
/* 1815 */         for (int i = 0; i < added.size(); i++)
/*      */         {
/* 1817 */           this.queued_peers.add(added.get(i));
/*      */         }
/*      */       }
/*      */       
/* 1821 */       Map root = new TreeMap();
/*      */       
/* 1823 */       if (preprocess_map.size() > 0)
/*      */       {
/* 1825 */         root.putAll(preprocess_map);
/*      */       }
/*      */       
/* 1828 */       if (explicit_limited_peers != null)
/*      */       {
/* 1830 */         for (int i = 0; i < explicit_limited_peers.size(); i++)
/*      */         {
/* 1832 */           num_want--;
/*      */           
/* 1834 */           TRTrackerServerSimplePeer peer = (TRTrackerServerSimplePeer)explicit_limited_peers.get(i);
/*      */           
/* 1836 */           exportPeer(rep_peers, peer, send_peer_ids, compact_mode, crypto_level, network_position);
/*      */         }
/*      */       }
/*      */       
/* 1840 */       if (explicit_biased_peers != null)
/*      */       {
/* 1842 */         for (int i = 0; i < explicit_biased_peers.size(); i++)
/*      */         {
/* 1844 */           num_want--;
/*      */           
/* 1846 */           TRTrackerServerSimplePeer peer = (TRTrackerServerSimplePeer)explicit_biased_peers.get(i);
/*      */           
/* 1848 */           exportPeer(rep_peers, peer, send_peer_ids, compact_mode, crypto_level, network_position);
/*      */         }
/*      */       }
/*      */       
/* 1852 */       if (this.explicit_manual_biased_peers != null)
/*      */       {
/* 1854 */         if ((requesting_peer != null) && (!requesting_peer.isSeed()))
/*      */         {
/* 1856 */           Object[] explicit_peer = (Object[])this.explicit_manual_biased_peers.get(this.explicit_next_peer++);
/*      */           
/* 1858 */           if (this.explicit_next_peer == this.explicit_manual_biased_peers.size())
/*      */           {
/* 1860 */             this.explicit_next_peer = 0;
/*      */           }
/*      */           
/* 1863 */           Map rep_peer = new HashMap(3);
/*      */           
/* 1865 */           if (send_peer_ids)
/*      */           {
/* 1867 */             byte[] peer_id = new byte[20];
/*      */             
/* 1869 */             this.random.nextBytes(peer_id);
/*      */             
/* 1871 */             rep_peer.put("peer id", peer_id);
/*      */           }
/*      */           
/* 1874 */           if (compact_mode != 0)
/*      */           {
/* 1876 */             byte[] peer_bytes = (byte[])explicit_peer[1];
/*      */             
/* 1878 */             rep_peer.put("ip", peer_bytes);
/*      */             
/* 1880 */             if (compact_mode >= 2)
/*      */             {
/* 1882 */               rep_peer.put("azver", new Long(0L));
/*      */               
/* 1884 */               rep_peer.put("azudp", new Long(0L));
/*      */               
/* 1886 */               rep_peer.put("azup", new Long(0L));
/*      */               
/* 1888 */               rep_peer.put("azbiased", "");
/*      */             }
/*      */           }
/*      */           else {
/* 1892 */             rep_peer.put("ip", ((String)explicit_peer[0]).getBytes());
/*      */           }
/*      */           
/* 1895 */           rep_peer.put("port", new Long(((Integer)explicit_peer[2]).intValue()));
/*      */           
/* 1897 */           if (crypto_level != 0)
/*      */           {
/* 1899 */             rep_peer.put("crypto_flag", new Long(0L));
/*      */           }
/*      */           
/* 1902 */           rep_peers.addFirst(rep_peer);
/*      */         }
/*      */       }
/*      */       
/* 1906 */       int num_peers_returned = rep_peers.size();
/* 1907 */       Iterator it = rep_peers.iterator();
/*      */       byte[] crypto_flags;
/* 1909 */       if (compact_mode == 2)
/*      */       {
/* 1911 */         byte[] compact_peers = new byte[num_peers_returned * 9];
/*      */         
/* 1913 */         int index = 0;
/*      */         
/* 1915 */         while (it.hasNext())
/*      */         {
/* 1917 */           Map rep_peer = (Map)it.next();
/*      */           
/* 1919 */           byte[] ip = (byte[])rep_peer.get("ip");
/* 1920 */           int tcp_port = ((Long)rep_peer.get("port")).intValue();
/* 1921 */           int udp_port = ((Long)rep_peer.get("azudp")).intValue();
/* 1922 */           Long crypto_flag_l = (Long)rep_peer.get("crypto_flag");
/* 1923 */           byte crypto_flag = crypto_flag_l == null ? 0 : crypto_flag_l.byteValue();
/*      */           
/* 1925 */           int pos = index * 9;
/*      */           
/* 1927 */           System.arraycopy(ip, 0, compact_peers, pos, 4);
/*      */           
/* 1929 */           pos += 4;
/*      */           
/* 1931 */           compact_peers[(pos++)] = ((byte)(tcp_port >> 8));
/* 1932 */           compact_peers[(pos++)] = ((byte)(tcp_port & 0xFF));
/* 1933 */           compact_peers[(pos++)] = ((byte)(udp_port >> 8));
/* 1934 */           compact_peers[(pos++)] = ((byte)(udp_port & 0xFF));
/* 1935 */           compact_peers[(pos++)] = crypto_flag;
/*      */           
/* 1937 */           index++;
/*      */         }
/*      */         
/* 1940 */         root.put("peers", compact_peers);
/*      */         
/* 1942 */         root.put("azcompact", new Long(1L));
/*      */       }
/* 1944 */       else if (compact_mode == 3)
/*      */       {
/* 1946 */         List compact_peers = new ArrayList(num_peers_returned);
/*      */         
/* 1948 */         while (it.hasNext())
/*      */         {
/* 1950 */           Map rep_peer = (Map)it.next();
/*      */           
/* 1952 */           Map peer = new HashMap();
/*      */           
/* 1954 */           compact_peers.add(peer);
/*      */           
/* 1956 */           byte[] ip = (byte[])rep_peer.get("ip");
/*      */           
/* 1958 */           peer.put("i", ip);
/*      */           
/* 1960 */           int tcp_port = ((Long)rep_peer.get("port")).intValue();
/*      */           
/* 1962 */           peer.put("t", new byte[] { (byte)(tcp_port >> 8), (byte)(tcp_port & 0xFF) });
/*      */           
/* 1964 */           int udp_port = ((Long)rep_peer.get("azudp")).intValue();
/*      */           
/* 1966 */           if (udp_port != 0)
/*      */           {
/* 1968 */             if (udp_port == tcp_port)
/*      */             {
/* 1970 */               peer.put("u", new byte[0]);
/*      */             }
/*      */             else
/*      */             {
/* 1974 */               peer.put("u", new byte[] { (byte)(udp_port >> 8), (byte)(udp_port & 0xFF) });
/*      */             }
/*      */           }
/*      */           
/* 1978 */           Long http_port_l = (Long)rep_peer.get("azhttp");
/*      */           
/* 1980 */           if (http_port_l != null)
/*      */           {
/* 1982 */             int http_port = http_port_l.intValue();
/*      */             
/* 1984 */             if (http_port != 0)
/*      */             {
/* 1986 */               peer.put("h", new byte[] { (byte)(http_port >> 8), (byte)(http_port & 0xFF) });
/*      */             }
/*      */           }
/*      */           
/* 1990 */           Long crypto_flag_l = (Long)rep_peer.get("crypto_flag");
/* 1991 */           byte crypto_flag = crypto_flag_l == null ? 0 : crypto_flag_l.byteValue();
/*      */           
/* 1993 */           if (crypto_flag != 0)
/*      */           {
/* 1995 */             peer.put("c", new byte[] { crypto_flag });
/*      */           }
/*      */           
/* 1998 */           Long az_ver_l = (Long)rep_peer.get("azver");
/* 1999 */           byte az_ver = az_ver_l == null ? 0 : az_ver_l.byteValue();
/*      */           
/* 2001 */           if (az_ver != 0)
/*      */           {
/* 2003 */             peer.put("v", new Long(az_ver));
/*      */           }
/*      */           
/* 2006 */           Long up_speed = (Long)rep_peer.get("azup");
/*      */           
/* 2008 */           if ((up_speed != null) && (up_speed.longValue() != 0L))
/*      */           {
/* 2010 */             peer.put("s", up_speed);
/*      */           }
/*      */           
/* 2013 */           Long rtt = (Long)rep_peer.get("azrtt");
/*      */           
/* 2015 */           if (rtt != null)
/*      */           {
/* 2017 */             peer.put("r", rtt);
/*      */           }
/*      */           
/* 2020 */           if (rep_peer.containsKey("azbiased"))
/*      */           {
/* 2022 */             peer.put("b", new Long(1L));
/*      */           }
/*      */         }
/*      */         
/* 2026 */         root.put("peers", compact_peers);
/*      */         
/* 2028 */         root.put("azcompact", new Long(2L));
/*      */       }
/* 2030 */       else if (compact_mode == 16)
/*      */       {
/* 2032 */         List xml_peers = new ArrayList(num_peers_returned);
/*      */         
/* 2034 */         while (it.hasNext())
/*      */         {
/* 2036 */           Map rep_peer = (Map)it.next();
/*      */           
/* 2038 */           Map peer = new HashMap();
/*      */           
/* 2040 */           xml_peers.add(peer);
/*      */           
/* 2042 */           peer.put("ip", rep_peer.get("ip"));
/*      */           
/* 2044 */           peer.put("tcp", rep_peer.get("port"));
/*      */           
/* 2046 */           int udp_port = ((Long)rep_peer.get("azudp")).intValue();
/*      */           
/* 2048 */           if (udp_port != 0)
/*      */           {
/* 2050 */             peer.put("udp", new Long(udp_port));
/*      */           }
/*      */           
/* 2053 */           Long http_port_l = (Long)rep_peer.get("azhttp");
/*      */           
/* 2055 */           if (http_port_l != null)
/*      */           {
/* 2057 */             int http_port = http_port_l.intValue();
/*      */             
/* 2059 */             if (http_port != 0)
/*      */             {
/* 2061 */               peer.put("http", new Long(http_port));
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2066 */         root.put("peers", xml_peers);
/*      */       }
/*      */       else
/*      */       {
/* 2070 */         crypto_flags = null;
/*      */         
/* 2072 */         if (crypto_level != 0)
/*      */         {
/* 2074 */           crypto_flags = new byte[num_peers_returned];
/*      */         }
/*      */         
/* 2077 */         if (compact_mode == 1)
/*      */         {
/* 2079 */           byte[] compact_peers = new byte[num_peers_returned * 6];
/*      */           
/* 2081 */           int index = 0;
/*      */           
/* 2083 */           int num_ipv4 = 0;
/* 2084 */           int num_ipv6 = 0;
/*      */           
/* 2086 */           while (it.hasNext())
/*      */           {
/* 2088 */             Map rep_peer = (Map)it.next();
/*      */             
/* 2090 */             byte[] ip = (byte[])rep_peer.get("ip");
/*      */             
/* 2092 */             if (ip.length > 4)
/*      */             {
/* 2094 */               num_ipv6++;
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/* 2100 */               num_ipv4++;
/*      */               
/* 2102 */               if (num_ipv6 == 0)
/*      */               {
/* 2104 */                 int port = ((Long)rep_peer.get("port")).intValue();
/*      */                 
/* 2106 */                 int pos = index * 6;
/*      */                 
/* 2108 */                 System.arraycopy(ip, 0, compact_peers, pos, 4);
/*      */                 
/* 2110 */                 pos += 4;
/*      */                 
/* 2112 */                 compact_peers[(pos++)] = ((byte)(port >> 8));
/* 2113 */                 compact_peers[(pos++)] = ((byte)(port & 0xFF));
/*      */               }
/*      */             }
/*      */             
/* 2117 */             if (crypto_flags != null)
/*      */             {
/* 2119 */               Long crypto_flag = (Long)rep_peer.remove("crypto_flag");
/*      */               
/* 2121 */               crypto_flags[index] = crypto_flag.byteValue();
/*      */             }
/*      */             
/* 2124 */             index++;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 2129 */           if (num_ipv6 > 0)
/*      */           {
/* 2131 */             byte[] compact_peers_v4 = new byte[num_ipv4 * 6];
/* 2132 */             byte[] compact_peers_v6 = new byte[num_ipv6 * 18];
/*      */             
/* 2134 */             it = rep_peers.iterator();
/*      */             
/* 2136 */             int v4_index = 0;
/* 2137 */             int v6_index = 0;
/*      */             
/* 2139 */             while (it.hasNext())
/*      */             {
/* 2141 */               Map rep_peer = (Map)it.next();
/*      */               
/* 2143 */               byte[] ip = (byte[])rep_peer.get("ip");
/*      */               
/* 2145 */               int port = ((Long)rep_peer.get("port")).intValue();
/*      */               
/* 2147 */               if (ip.length > 4)
/*      */               {
/* 2149 */                 int pos = v6_index * 18;
/*      */                 
/* 2151 */                 System.arraycopy(ip, 0, compact_peers_v6, pos, 16);
/*      */                 
/* 2153 */                 pos += 16;
/*      */                 
/* 2155 */                 compact_peers_v6[(pos++)] = ((byte)(port >> 8));
/* 2156 */                 compact_peers_v6[(pos++)] = ((byte)(port & 0xFF));
/*      */                 
/* 2158 */                 v6_index++;
/*      */               }
/*      */               else
/*      */               {
/* 2162 */                 int pos = v4_index * 6;
/*      */                 
/* 2164 */                 System.arraycopy(ip, 0, compact_peers_v4, pos, 4);
/*      */                 
/* 2166 */                 pos += 4;
/*      */                 
/* 2168 */                 compact_peers_v4[(pos++)] = ((byte)(port >> 8));
/* 2169 */                 compact_peers_v4[(pos++)] = ((byte)(port & 0xFF));
/*      */                 
/* 2171 */                 v4_index++;
/*      */               }
/*      */             }
/*      */             
/* 2175 */             if (compact_peers_v4.length > 0)
/*      */             {
/* 2177 */               root.put("peers", compact_peers_v4);
/*      */             }
/*      */             
/* 2180 */             if (compact_peers_v6.length > 0)
/*      */             {
/* 2182 */               root.put("peers6", compact_peers_v6);
/*      */             }
/*      */           }
/*      */           else {
/* 2186 */             root.put("peers", compact_peers);
/*      */           }
/*      */         }
/*      */         else {
/* 2190 */           int index = 0;
/*      */           
/* 2192 */           while (it.hasNext())
/*      */           {
/* 2194 */             Map rep_peer = (Map)it.next();
/*      */             
/* 2196 */             if (crypto_flags != null)
/*      */             {
/* 2198 */               Long crypto_flag = (Long)rep_peer.remove("crypto_flag");
/*      */               
/* 2200 */               crypto_flags[index] = crypto_flag.byteValue();
/*      */             }
/*      */             
/* 2203 */             index++;
/*      */           }
/*      */           
/* 2206 */           root.put("peers", rep_peers);
/*      */         }
/*      */         
/* 2209 */         if (crypto_flags != null)
/*      */         {
/* 2211 */           root.put("crypto_flags", crypto_flags);
/*      */         }
/*      */       }
/*      */       
/* 2215 */       root.put("interval", new Long(interval));
/*      */       
/* 2217 */       root.put("min interval", new Long(min_interval));
/*      */       
/* 2219 */       if (nat_warning)
/*      */       {
/* 2221 */         requesting_peer.setNATStatus((byte)5);
/*      */         
/* 2223 */         root.put("warning message", ("Unable to connect to your incoming data port (" + requesting_peer.getIP() + ":" + requesting_peer.getTCPPort() + "). " + "This will result in slow downloads. Please check your firewall/router settings").getBytes());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2231 */       root.put("complete", new Long(getSeedCountForScrape(requester_is_biased)));
/* 2232 */       root.put("incomplete", new Long(getLeecherCount()));
/* 2233 */       root.put("downloaded", new Long(this.stats.getCompletedCount()));
/*      */       
/* 2235 */       if (add_to_cache)
/*      */       {
/* 2237 */         this.announce_cache.put(new Integer((num_peers_returned + 9) / 10), new announceCacheEntry(root, send_peer_ids, compact_mode));
/*      */       }
/*      */       
/* 2240 */       return root;
/*      */     }
/*      */     finally
/*      */     {
/* 2244 */       this.this_mon.exit();
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
/*      */   private void exportPeer(LinkedList rep_peers, TRTrackerServerSimplePeer peer, boolean send_peer_ids, byte compact_mode, byte crypto_level, DHTNetworkPosition network_position)
/*      */   {
/* 2258 */     Map rep_peer = new HashMap(3);
/*      */     
/* 2260 */     if (send_peer_ids)
/*      */     {
/* 2262 */       rep_peer.put("peer id", peer.getPeerId().getHash());
/*      */     }
/*      */     
/* 2265 */     if (compact_mode != 0)
/*      */     {
/* 2267 */       byte[] peer_bytes = peer.getIPAddressBytes();
/*      */       
/* 2269 */       if (peer_bytes == null)
/*      */       {
/* 2271 */         return;
/*      */       }
/*      */       
/* 2274 */       rep_peer.put("ip", peer_bytes);
/*      */       
/* 2276 */       if (compact_mode >= 2)
/*      */       {
/* 2278 */         rep_peer.put("azver", new Long(peer.getAZVer()));
/*      */         
/* 2280 */         rep_peer.put("azudp", new Long(peer.getUDPPort()));
/*      */         
/* 2282 */         if (peer.isSeed())
/*      */         {
/* 2284 */           rep_peer.put("azhttp", new Long(peer.getHTTPPort()));
/*      */         }
/*      */         
/* 2287 */         if (compact_mode >= 16)
/*      */         {
/* 2289 */           rep_peer.put("ip", peer.getIPAsRead());
/*      */         }
/*      */         else
/*      */         {
/* 2293 */           rep_peer.put("azup", new Long(peer.getUpSpeed()));
/*      */           
/* 2295 */           if (peer.isBiased())
/*      */           {
/* 2297 */             rep_peer.put("azbiased", "");
/*      */           }
/*      */           
/* 2300 */           if (network_position != null)
/*      */           {
/* 2302 */             DHTNetworkPosition peer_pos = peer.getNetworkPosition();
/*      */             
/* 2304 */             if ((peer_pos != null) && (network_position.getPositionType() == peer_pos.getPositionType()))
/*      */             {
/* 2306 */               rep_peer.put("azrtt", new Long(peer_pos.estimateRTT(network_position)));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 2313 */       rep_peer.put("ip", peer.getIPAsRead());
/*      */     }
/*      */     
/* 2316 */     rep_peer.put("port", new Long(peer.getTCPPort()));
/*      */     
/* 2318 */     if (crypto_level != 0)
/*      */     {
/* 2320 */       rep_peer.put("crypto_flag", new Long(peer.getCryptoLevel() == 2 ? 1L : 0L));
/*      */     }
/*      */     
/* 2323 */     if (peer.isBiased())
/*      */     {
/* 2325 */       rep_peers.addFirst(rep_peer);
/*      */     }
/*      */     else
/*      */     {
/* 2329 */       rep_peers.addLast(rep_peer);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map exportScrapeToMap(String url_parameters, String ip_address, boolean allow_cache)
/*      */     throws TRTrackerServerException
/*      */   {
/*      */     try
/*      */     {
/* 2342 */       this.this_mon.enter();
/*      */       
/* 2344 */       handleRedirects(url_parameters, ip_address, true);
/*      */       
/* 2346 */       this.stats.addScrape();
/*      */       
/* 2348 */       long now = SystemTime.getCurrentTime();
/*      */       
/* 2350 */       long diff = now - this.last_scrape_calc_time;
/*      */       
/* 2352 */       if ((allow_cache) && (this.last_scrape != null) && (diff < TRTrackerServerImpl.getScrapeCachePeriod()) && (diff >= 0L))
/*      */       {
/* 2354 */         return this.last_scrape;
/*      */       }
/*      */       
/* 2357 */       this.last_scrape = new TreeMap();
/* 2358 */       this.last_scrape_calc_time = now;
/*      */       
/*      */ 
/*      */ 
/* 2362 */       Set bp = this.server.getBiasedPeers();
/*      */       boolean requester_is_biased;
/* 2364 */       boolean requester_is_biased; if (bp == null)
/*      */       {
/* 2366 */         requester_is_biased = false;
/*      */       }
/*      */       else
/*      */       {
/* 2370 */         requester_is_biased = bp.contains(ip_address);
/*      */       }
/*      */       
/* 2373 */       this.last_scrape.put("complete", new Long(getSeedCountForScrape(requester_is_biased)));
/* 2374 */       this.last_scrape.put("incomplete", new Long(getLeecherCount()));
/* 2375 */       this.last_scrape.put("downloaded", new Long(this.stats.getCompletedCount()));
/*      */       
/* 2377 */       return this.last_scrape;
/*      */     }
/*      */     finally
/*      */     {
/* 2381 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void checkTimeouts()
/*      */   {
/*      */     try
/*      */     {
/* 2389 */       this.this_mon.enter();
/*      */       
/* 2391 */       long now = SystemTime.getCurrentTime();
/*      */       
/* 2393 */       int new_bad_NAT_count = 0;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2398 */       int new_seed_count = 0;
/*      */       try
/*      */       {
/* 2401 */         this.peer_list_compaction_suspended = true;
/*      */         
/* 2403 */         for (int i = 0; i < this.peer_list.size(); i++)
/*      */         {
/* 2405 */           TRTrackerServerPeerImpl peer = (TRTrackerServerPeerImpl)this.peer_list.get(i);
/*      */           
/* 2407 */           if (peer != null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 2412 */             if (now > peer.getTimeout())
/*      */             {
/* 2414 */               removePeer(peer, i, 5, null);
/*      */             }
/*      */             else
/*      */             {
/* 2418 */               if (peer.isSeed())
/*      */               {
/* 2420 */                 new_seed_count++;
/*      */               }
/*      */               
/* 2423 */               if (peer.isNATStatusBad())
/*      */               {
/* 2425 */                 new_bad_NAT_count++;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       } finally {
/* 2431 */         this.peer_list_compaction_suspended = false;
/*      */       }
/*      */       
/* 2434 */       this.bad_NAT_count = new_bad_NAT_count;
/* 2435 */       this.seed_count = new_seed_count;
/*      */       
/* 2437 */       if (this.removed_count > 1000)
/*      */       {
/* 2439 */         this.removed_count = 0;
/*      */         
/* 2441 */         checkForPeerListCompaction(true);
/*      */         
/*      */ 
/*      */ 
/* 2445 */         HashMap new_peer_map = new HashMap(this.peer_map);
/* 2446 */         HashMap new_peer_reuse_map = new HashMap(this.peer_reuse_map);
/*      */         
/* 2448 */         this.peer_map = new_peer_map;
/* 2449 */         this.peer_reuse_map = new_peer_reuse_map;
/*      */       }
/*      */       else
/*      */       {
/* 2453 */         checkForPeerListCompaction(false);
/*      */       }
/*      */       
/* 2456 */       Iterator it = this.lightweight_seed_map.values().iterator();
/*      */       
/* 2458 */       while (it.hasNext())
/*      */       {
/* 2460 */         lightweightSeed lws = (lightweightSeed)it.next();
/*      */         
/* 2462 */         if (now > lws.getTimeout())
/*      */         {
/* 2464 */           it.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 2469 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkForPeerListCompaction(boolean force)
/*      */   {
/* 2477 */     if ((this.peer_list_hole_count > 0) && (!this.peer_list_compaction_suspended))
/*      */     {
/* 2479 */       if ((force) || (this.peer_list_hole_count > this.peer_map.size() / 10))
/*      */       {
/* 2481 */         ArrayList new_peer_list = new ArrayList(this.peer_list.size() - this.peer_list_hole_count / 2);
/*      */         
/* 2483 */         int holes_found = 0;
/*      */         
/* 2485 */         for (int i = 0; i < this.peer_list.size(); i++)
/*      */         {
/* 2487 */           Object obj = this.peer_list.get(i);
/*      */           
/* 2489 */           if (obj == null)
/*      */           {
/* 2491 */             holes_found++;
/*      */           }
/*      */           else {
/* 2494 */             new_peer_list.add(obj);
/*      */           }
/*      */         }
/*      */         
/* 2498 */         if (holes_found != this.peer_list_hole_count)
/*      */         {
/* 2500 */           Debug.out("TRTrackerTorrent:compactHoles: count mismatch");
/*      */         }
/*      */         
/* 2503 */         this.peer_list = new_peer_list;
/*      */         
/* 2505 */         this.peer_list_hole_count = 0;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateXferStats(int bytes_in, int bytes_out)
/*      */   {
/* 2515 */     this.stats.addXferStats(bytes_in, bytes_out);
/*      */   }
/*      */   
/*      */ 
/*      */   public TRTrackerServerTorrentStats getStats()
/*      */   {
/* 2521 */     return this.stats;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getPeerCount()
/*      */   {
/* 2527 */     return this.peer_map.size() + this.lightweight_seed_map.size();
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getSeedCount()
/*      */   {
/* 2533 */     if (this.seed_count < 0)
/*      */     {
/* 2535 */       Debug.out("seed count negative");
/*      */     }
/*      */     
/* 2538 */     return this.seed_count + this.lightweight_seed_map.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int getSeedCountForScrape(boolean requester_is_biased)
/*      */   {
/* 2545 */     int seeds = getSeedCount();
/*      */     
/* 2547 */     if ((this.biased_peers != null) && (!requester_is_biased))
/*      */     {
/* 2549 */       int bpc = 0;
/*      */       
/* 2551 */       Iterator it = this.biased_peers.iterator();
/*      */       
/* 2553 */       while (it.hasNext())
/*      */       {
/* 2555 */         TRTrackerServerPeerImpl bp = (TRTrackerServerPeerImpl)it.next();
/*      */         
/* 2557 */         if (bp.isSeed())
/*      */         {
/* 2559 */           seeds--;
/*      */           
/* 2561 */           bpc++;
/*      */         }
/*      */       }
/*      */       
/* 2565 */       if (seeds < 0)
/*      */       {
/* 2567 */         seeds = 0;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2572 */       if (bpc > 0)
/*      */       {
/* 2574 */         seeds++;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2580 */     int queued = getQueuedCount();
/*      */     
/* 2582 */     if (queued > 0)
/*      */     {
/* 2584 */       seeds++;
/*      */     }
/*      */     
/* 2587 */     return seeds;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getLeecherCount()
/*      */   {
/* 2595 */     int res = this.peer_map.size() - this.seed_count;
/*      */     
/* 2597 */     return res < 0 ? 0 : res;
/*      */   }
/*      */   
/*      */   public TRTrackerServerPeer[] getPeers()
/*      */   {
/*      */     try
/*      */     {
/* 2604 */       this.this_mon.enter();
/*      */       
/* 2606 */       TRTrackerServerPeer[] res = new TRTrackerServerPeer[this.peer_map.size()];
/*      */       
/* 2608 */       this.peer_map.values().toArray(res);
/*      */       
/* 2610 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/* 2614 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getQueuedCount()
/*      */   {
/* 2621 */     List l = this.queued_peers;
/*      */     
/* 2623 */     if (l == null)
/*      */     {
/* 2625 */       return 0;
/*      */     }
/*      */     
/* 2628 */     return l.size();
/*      */   }
/*      */   
/*      */   public TRTrackerServerPeerBase[] getQueuedPeers()
/*      */   {
/*      */     try
/*      */     {
/* 2635 */       this.this_mon.enter();
/*      */       
/* 2637 */       if (this.queued_peers == null)
/*      */       {
/* 2639 */         return new TRTrackerServerPeerBase[0];
/*      */       }
/*      */       
/* 2642 */       TRTrackerServerPeerBase[] res = new TRTrackerServerPeerBase[this.queued_peers.size()];
/*      */       
/* 2644 */       this.queued_peers.toArray(res);
/*      */       
/* 2646 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/* 2650 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public HashWrapper getHash()
/*      */   {
/* 2657 */     return this.hash;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addExplicitBiasedPeer(String ip, int port)
/*      */   {
/* 2665 */     byte[] bytes = HostNameToIPResolver.hostAddressToBytes(ip);
/*      */     
/* 2667 */     if (bytes != null) {
/*      */       try
/*      */       {
/* 2670 */         this.this_mon.enter();
/*      */         
/* 2672 */         if (this.explicit_manual_biased_peers == null)
/*      */         {
/* 2674 */           this.explicit_manual_biased_peers = new ArrayList();
/*      */         }
/*      */         
/* 2677 */         this.explicit_manual_biased_peers.add(new Object[] { ip, bytes, new Integer(port) });
/*      */       }
/*      */       finally
/*      */       {
/* 2681 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRedirects(URL[] urls)
/*      */   {
/*      */     try
/*      */     {
/* 2690 */       this.this_mon.enter();
/*      */       
/* 2692 */       this.redirects = urls;
/*      */     }
/*      */     finally
/*      */     {
/* 2696 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public URL[] getRedirects()
/*      */   {
/* 2703 */     return this.redirects;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void handleRedirects(String url_parameters, String real_ip_address, boolean scrape)
/*      */     throws TRTrackerServerException
/*      */   {
/* 2714 */     if (this.redirects != null)
/*      */     {
/* 2716 */       if (url_parameters.contains("permredirect"))
/*      */       {
/* 2718 */         Debug.out("redirect recursion");
/*      */         
/* 2720 */         throw new TRTrackerServerException("redirection recursion not supported");
/*      */       }
/*      */       
/* 2723 */       URL redirect = this.redirects[((real_ip_address.hashCode() & 0x7FFFFFFF) % this.redirects.length)];
/*      */       
/* 2725 */       Map headers = new HashMap();
/*      */       
/* 2727 */       String redirect_str = redirect.toString();
/*      */       
/* 2729 */       if (scrape)
/*      */       {
/* 2731 */         int pos = redirect_str.indexOf("/announce");
/*      */         
/* 2733 */         if (pos == -1)
/*      */         {
/* 2735 */           return;
/*      */         }
/*      */         
/* 2738 */         redirect_str = redirect_str.substring(0, pos) + "/scrape" + redirect_str.substring(pos + 9);
/*      */       }
/*      */       
/* 2741 */       if (redirect_str.indexOf('?') == -1)
/*      */       {
/* 2743 */         redirect_str = redirect_str + "?";
/*      */       }
/*      */       else
/*      */       {
/* 2747 */         redirect_str = redirect_str + "&";
/*      */       }
/*      */       
/* 2750 */       redirect_str = redirect_str + "permredirect=1";
/*      */       
/* 2752 */       if (url_parameters.length() > 0)
/*      */       {
/* 2754 */         redirect_str = redirect_str + "&" + url_parameters;
/*      */       }
/*      */       
/* 2757 */       System.out.println("redirect -> " + redirect_str);
/*      */       
/* 2759 */       headers.put("Location", redirect_str);
/*      */       
/* 2761 */       throw new TRTrackerServerException(301, "Moved Permanently", headers);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addListener(TRTrackerServerTorrentListener l)
/*      */   {
/* 2768 */     this.listeners.add(l);
/*      */     
/* 2770 */     if (this.deleted)
/*      */     {
/* 2772 */       l.deleted(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(TRTrackerServerTorrentListener l)
/*      */   {
/* 2780 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void peerEvent(TRTrackerServerPeer peer, int event, String url_parameters)
/*      */     throws TRTrackerServerException
/*      */   {
/* 2791 */     if (this.peer_listeners != null)
/*      */     {
/* 2793 */       for (int i = 0; i < this.peer_listeners.size(); i++) {
/*      */         try
/*      */         {
/* 2796 */           ((TRTrackerServerTorrentPeerListener)this.peer_listeners.get(i)).eventOccurred(this, peer, event, url_parameters);
/*      */         }
/*      */         catch (TRTrackerServerException e)
/*      */         {
/* 2800 */           throw e;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2804 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPeerListener(TRTrackerServerTorrentPeerListener l)
/*      */   {
/* 2814 */     if (this.peer_listeners == null)
/*      */     {
/* 2816 */       this.peer_listeners = new ArrayList();
/*      */     }
/*      */     
/* 2819 */     this.peer_listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePeerListener(TRTrackerServerTorrentPeerListener l)
/*      */   {
/* 2826 */     if (this.peer_listeners != null)
/*      */     {
/* 2828 */       this.peer_listeners.remove(l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void disableCaching()
/*      */   {
/* 2835 */     this.caching_enabled = false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isCachingEnabled()
/*      */   {
/* 2841 */     return this.caching_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getBadNATPeerCount()
/*      */   {
/* 2847 */     return this.bad_NAT_count;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void delete()
/*      */   {
/* 2853 */     this.deleted = true;
/*      */     
/* 2855 */     for (int i = 0; i < this.listeners.size(); i++)
/*      */     {
/* 2857 */       ((TRTrackerServerTorrentListener)this.listeners.get(i)).deleted(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class announceCacheEntry
/*      */   {
/*      */     protected final Map data;
/*      */     
/*      */     protected final boolean send_peer_ids;
/*      */     
/*      */     protected final byte compact_mode;
/*      */     
/*      */     protected final long time;
/*      */     
/*      */ 
/*      */     protected announceCacheEntry(Map _data, boolean _send_peer_ids, byte _compact_mode)
/*      */     {
/* 2875 */       this.data = _data;
/* 2876 */       this.send_peer_ids = _send_peer_ids;
/* 2877 */       this.compact_mode = _compact_mode;
/* 2878 */       this.time = SystemTime.getCurrentTime();
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean getSendPeerIds()
/*      */     {
/* 2884 */       return this.send_peer_ids;
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte getCompactMode()
/*      */     {
/* 2890 */       return this.compact_mode;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getTime()
/*      */     {
/* 2896 */       return this.time;
/*      */     }
/*      */     
/*      */ 
/*      */     protected Map getData()
/*      */     {
/* 2902 */       return this.data;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static class lightweightSeed
/*      */   {
/*      */     final long timeout;
/*      */     
/*      */     final long last_contact_time;
/*      */     
/*      */     final long uploaded;
/*      */     
/*      */     final byte nat_status;
/*      */     
/*      */ 
/*      */     protected lightweightSeed(long _now, long _timeout, long _uploaded, byte _nat_status)
/*      */     {
/* 2921 */       this.last_contact_time = _now;
/* 2922 */       this.timeout = _timeout;
/* 2923 */       this.uploaded = _uploaded;
/* 2924 */       this.nat_status = _nat_status;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getTimeout()
/*      */     {
/* 2930 */       return this.timeout;
/*      */     }
/*      */     
/*      */     protected long getLastContactTime()
/*      */     {
/* 2935 */       return this.last_contact_time;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getUploaded()
/*      */     {
/* 2941 */       return this.uploaded;
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte getNATStatus()
/*      */     {
/* 2947 */       return this.nat_status;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static class QueuedPeer
/*      */     implements TRTrackerServerPeerBase
/*      */   {
/*      */     private static final byte FLAG_SEED = 1;
/*      */     
/*      */     private static final byte FLAG_BIASED = 2;
/*      */     
/*      */     private final short tcp_port;
/*      */     
/*      */     private final short udp_port;
/*      */     
/*      */     private final short http_port;
/*      */     
/*      */     private byte[] ip;
/*      */     
/*      */     private final byte crypto_level;
/*      */     
/*      */     private final byte az_ver;
/*      */     
/*      */     private int create_time_secs;
/*      */     
/*      */     private final int timeout_secs;
/*      */     
/*      */     private byte flags;
/*      */     
/*      */     protected QueuedPeer(String _ip_str, int _tcp_port, int _udp_port, int _http_port, byte _crypto_level, byte _az_ver, int _timeout_secs, boolean _seed, boolean _biased)
/*      */     {
/*      */       try
/*      */       {
/* 2981 */         this.ip = _ip_str.getBytes("ISO-8859-1");
/*      */       }
/*      */       catch (UnsupportedEncodingException e)
/*      */       {
/* 2985 */         Debug.printStackTrace(e);
/*      */       }
/*      */       
/* 2988 */       this.tcp_port = ((short)_tcp_port);
/* 2989 */       this.udp_port = ((short)_udp_port);
/* 2990 */       this.http_port = ((short)_http_port);
/* 2991 */       this.crypto_level = _crypto_level;
/* 2992 */       this.az_ver = _az_ver;
/*      */       
/* 2994 */       setFlag((byte)1, _seed);
/* 2995 */       setFlag((byte)2, _biased);
/*      */       
/* 2997 */       this.create_time_secs = ((int)(SystemTime.getCurrentTime() / 1000L));
/*      */       
/* 2999 */       this.timeout_secs = (_timeout_secs * 3);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean sameAs(TRTrackerServerPeerImpl peer)
/*      */     {
/* 3006 */       return (this.tcp_port == peer.getTCPPort()) && (Arrays.equals(this.ip, peer.getIPAsRead())) && (isIPOverride() == peer.isIPOverride());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean sameAs(QueuedPeer other)
/*      */     {
/* 3015 */       return (this.tcp_port == other.tcp_port) && (Arrays.equals(this.ip, other.ip));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected byte[] getIPAsRead()
/*      */     {
/* 3022 */       return this.ip;
/*      */     }
/*      */     
/*      */     public String getIP()
/*      */     {
/*      */       try
/*      */       {
/* 3029 */         return new String(this.ip, "ISO-8859-1");
/*      */       }
/*      */       catch (UnsupportedEncodingException e) {}
/*      */       
/* 3033 */       return new String(this.ip);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean isSeed()
/*      */     {
/* 3040 */       return getFlag((byte)1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setBiased(boolean _biased)
/*      */     {
/* 3047 */       setFlag((byte)2, _biased);
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isBiased()
/*      */     {
/* 3053 */       return getFlag((byte)2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean isIPOverride()
/*      */     {
/* 3061 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void setFlag(byte flag, boolean value)
/*      */     {
/* 3069 */       if (value)
/*      */       {
/* 3071 */         this.flags = ((byte)(this.flags | flag));
/*      */       }
/*      */       else
/*      */       {
/* 3075 */         this.flags = ((byte)(this.flags & (flag ^ 0xFFFFFFFF)));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean getFlag(byte flag)
/*      */     {
/* 3083 */       return (this.flags & flag) != 0;
/*      */     }
/*      */     
/*      */     protected byte[] getIPAddressBytes()
/*      */     {
/*      */       try
/*      */       {
/* 3090 */         return HostNameToIPResolver.hostAddressToBytes(new String(this.ip, "ISO-8859-1"));
/*      */       }
/*      */       catch (UnsupportedEncodingException e)
/*      */       {
/* 3094 */         Debug.printStackTrace(e);
/*      */       }
/* 3096 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public int getTCPPort()
/*      */     {
/* 3103 */       return this.tcp_port & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getUDPPort()
/*      */     {
/* 3109 */       return this.udp_port & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getHTTPPort()
/*      */     {
/* 3115 */       return this.http_port & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte getCryptoLevel()
/*      */     {
/* 3121 */       return this.crypto_level;
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte getAZVer()
/*      */     {
/* 3127 */       return this.az_ver;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getCreateTime()
/*      */     {
/* 3133 */       return this.create_time_secs;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean isTimedOut(long now_millis)
/*      */     {
/* 3140 */       int now_secs = (int)(now_millis / 1000L);
/*      */       
/* 3142 */       if (now_secs < this.create_time_secs)
/*      */       {
/* 3144 */         this.create_time_secs = now_secs;
/*      */       }
/*      */       
/* 3147 */       return this.create_time_secs + this.timeout_secs < now_secs;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getSecsToLive()
/*      */     {
/* 3153 */       int now_secs = (int)(SystemTime.getCurrentTime() / 1000L);
/*      */       
/* 3155 */       if (now_secs < this.create_time_secs)
/*      */       {
/* 3157 */         this.create_time_secs = now_secs;
/*      */       }
/*      */       
/* 3160 */       return this.create_time_secs + this.timeout_secs - now_secs;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 3166 */       return new String(this.ip) + ":" + getTCPPort() + "/" + getUDPPort() + "/" + getCryptoLevel();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class temporaryBiasedSeed
/*      */     implements TRTrackerServerSimplePeer
/*      */   {
/*      */     private final String ip;
/*      */     
/*      */     private final int tcp_port;
/*      */     
/*      */     private final HashWrapper peer_id;
/*      */     
/*      */ 
/*      */     protected temporaryBiasedSeed(String _ip, int _tcp_port)
/*      */     {
/* 3183 */       this.ip = _ip;
/* 3184 */       this.tcp_port = _tcp_port;
/*      */       
/* 3186 */       this.peer_id = new HashWrapper(RandomUtils.nextHash());
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getIPAsRead()
/*      */     {
/*      */       try
/*      */       {
/* 3194 */         return this.ip.getBytes("ISO-8859-1");
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/* 3198 */       return this.ip.getBytes();
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getIPAddressBytes()
/*      */     {
/*      */       try
/*      */       {
/* 3206 */         return InetAddress.getByName(this.ip).getAddress();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/* 3210 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public HashWrapper getPeerId()
/*      */     {
/* 3217 */       return this.peer_id;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getTCPPort()
/*      */     {
/* 3223 */       return this.tcp_port;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getUDPPort()
/*      */     {
/* 3229 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getHTTPPort()
/*      */     {
/* 3235 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isSeed()
/*      */     {
/* 3241 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isBiased()
/*      */     {
/* 3247 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte getCryptoLevel()
/*      */     {
/* 3253 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte getAZVer()
/*      */     {
/* 3259 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getUpSpeed()
/*      */     {
/* 3265 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public DHTNetworkPosition getNetworkPosition()
/*      */     {
/* 3271 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString()
/*      */   {
/*      */     String redirect;
/*      */     String redirect;
/* 3280 */     if (this.redirects == null)
/*      */     {
/* 3282 */       redirect = "none";
/*      */     }
/*      */     else
/*      */     {
/* 3286 */       redirect = "";
/*      */       
/* 3288 */       for (int i = 0; i < this.redirects.length; i++)
/*      */       {
/* 3290 */         redirect = redirect + (i == 0 ? "" : ",") + this.redirects[i];
/*      */       }
/*      */     }
/*      */     
/* 3294 */     return "seeds=" + getSeedCount() + ",leechers=" + getLeecherCount() + ", redirect=" + redirect;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/TRTrackerServerTorrentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */