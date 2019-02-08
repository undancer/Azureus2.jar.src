/*     */ package org.gudy.azureus2.core3.tracker.server.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerPeer;
/*     */ import org.gudy.azureus2.core3.tracker.util.TRTrackerUtils;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.ByteEncodedKeyHashMap;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.ThreadPoolTask;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TRTrackerServerProcessor
/*     */   extends ThreadPoolTask
/*     */ {
/*     */   private static final boolean QUEUE_TEST = false;
/*     */   private TRTrackerServerImpl server;
/*     */   private long start;
/*     */   private int request_type;
/*     */   
/*     */   protected TRTrackerServerTorrentImpl processTrackerRequest(TRTrackerServerImpl _server, String request, Map[] root_out, TRTrackerServerPeerImpl[] peer_out, int _request_type, byte[][] hashes, String link, String scrape_flags, HashWrapper peer_id, boolean no_peer_id, byte compact_mode, String key, String event, boolean stop_to_queue, int port, int udp_port, int http_port, String real_ip_address, String original_client_ip_address, long downloaded, long uploaded, long left, int num_want, byte crypto_level, byte az_ver, int up_speed, DHTNetworkPosition network_position)
/*     */     throws TRTrackerServerException
/*     */   {
/*  92 */     this.server = _server;
/*  93 */     this.request_type = _request_type;
/*     */     
/*  95 */     if (!this.server.isReady())
/*     */     {
/*  97 */       throw new TRTrackerServerException("Tracker initialising, please wait");
/*     */     }
/*     */     
/* 100 */     this.start = SystemTime.getHighPrecisionCounter();
/*     */     
/* 102 */     boolean ip_override = real_ip_address != original_client_ip_address;
/*     */     
/* 104 */     boolean loopback = TRTrackerUtils.isLoopback(real_ip_address);
/*     */     
/* 106 */     if (loopback)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 111 */       ip_override = false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 117 */     String client_ip_address = TRTrackerUtils.adjustHostFromHosting(original_client_ip_address);
/*     */     
/* 119 */     if (client_ip_address != original_client_ip_address)
/*     */     {
/* 121 */       if (Logger.isEnabled())
/*     */       {
/* 123 */         Logger.log(new LogEvent(LogIDs.TRACKER, "    address adjusted: original=" + original_client_ip_address + ", real=" + real_ip_address + ", adjusted=" + client_ip_address + ", loopback=" + loopback));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 132 */     if (!TRTrackerServerImpl.getAllNetworksSupported())
/*     */     {
/* 134 */       String network = AENetworkClassifier.categoriseAddress(client_ip_address);
/*     */       
/* 136 */       String[] permitted_networks = TRTrackerServerImpl.getPermittedNetworks();
/*     */       
/* 138 */       boolean ok = false;
/*     */       
/* 140 */       for (int i = 0; i < permitted_networks.length; i++)
/*     */       {
/* 142 */         if (network == permitted_networks[i])
/*     */         {
/* 144 */           ok = true;
/*     */           
/* 146 */           break;
/*     */         }
/*     */       }
/*     */       
/* 150 */       if (!ok)
/*     */       {
/* 152 */         throw new TRTrackerServerException("Network '" + network + "' not supported");
/*     */       }
/*     */     }
/*     */     
/* 156 */     TRTrackerServerTorrentImpl torrent = null;
/*     */     
/* 158 */     if (this.request_type != 3)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 164 */       if (this.request_type == 1)
/*     */       {
/* 166 */         if ((hashes == null) || (hashes.length == 0))
/*     */         {
/* 168 */           throw new TRTrackerServerException("Hash missing from request ");
/*     */         }
/*     */         
/* 171 */         if (hashes.length != 1)
/*     */         {
/* 173 */           throw new TRTrackerServerException("Too many hashes for announce");
/*     */         }
/*     */         
/* 176 */         byte[] hash = hashes[0];
/*     */         
/* 178 */         torrent = this.server.getTorrent(hash);
/*     */         
/* 180 */         if (torrent == null)
/*     */         {
/* 182 */           if (!COConfigurationManager.getBooleanParameter("Tracker Public Enable"))
/*     */           {
/* 184 */             throw new TRTrackerServerException("Torrent unauthorised");
/*     */           }
/*     */           
/*     */ 
/*     */           try
/*     */           {
/* 190 */             torrent = (TRTrackerServerTorrentImpl)this.server.permit(real_ip_address, hash, false);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 194 */             throw new TRTrackerServerException("Torrent unauthorised", e);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 199 */         if (peer_id == null)
/*     */         {
/* 201 */           throw new TRTrackerServerException("peer_id missing from request");
/*     */         }
/*     */         
/* 204 */         boolean queue_it = stop_to_queue;
/*     */         
/* 206 */         if (queue_it)
/*     */         {
/* 208 */           Set biased = this.server.getBiasedPeers();
/*     */           
/* 210 */           if ((biased == null) || (!biased.contains(real_ip_address)))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 216 */             if ((loopback) || (ip_override))
/*     */             {
/* 218 */               queue_it = false;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */         long min_interval;
/*     */         long interval;
/*     */         long min_interval;
/* 226 */         if (queue_it)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 231 */           long interval = this.server.getScrapeRetryInterval(torrent);
/* 232 */           min_interval = this.server.getMinScrapeRetryInterval();
/*     */         }
/*     */         else
/*     */         {
/* 236 */           interval = this.server.getAnnounceRetryInterval(torrent);
/* 237 */           min_interval = this.server.getMinAnnounceRetryInterval();
/*     */           
/* 239 */           if (left == 0L)
/*     */           {
/* 241 */             long mult = this.server.getSeedAnnounceIntervalMultiplier();
/*     */             
/* 243 */             interval *= mult;
/* 244 */             min_interval *= mult;
/*     */           }
/*     */         }
/*     */         
/* 248 */         TRTrackerServerPeerImpl peer = torrent.peerContact(request, event, peer_id, port, udp_port, http_port, crypto_level, az_ver, real_ip_address, client_ip_address, ip_override, loopback, key, uploaded, downloaded, left, interval, up_speed, network_position);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 258 */         if (queue_it)
/*     */         {
/* 260 */           torrent.peerQueued(client_ip_address, port, udp_port, http_port, crypto_level, az_ver, interval, left == 0L);
/*     */         }
/*     */         
/* 263 */         HashMap pre_map = new HashMap();
/*     */         
/* 265 */         TRTrackerServerPeer pre_process_peer = peer;
/*     */         
/* 267 */         if (pre_process_peer == null)
/*     */         {
/*     */ 
/*     */ 
/* 271 */           pre_process_peer = new lightweightPeer(client_ip_address, port, peer_id);
/*     */         }
/*     */         
/* 274 */         this.server.preProcess(pre_process_peer, torrent, this.request_type, request, pre_map);
/*     */         
/*     */ 
/*     */ 
/* 278 */         boolean stopped = (event != null) && (event.equalsIgnoreCase("stopped"));
/*     */         
/* 280 */         root_out[0] = torrent.exportAnnounceToMap(client_ip_address, pre_map, peer, left > 0L ? 1 : false, stopped ? 0 : num_want, interval, min_interval, no_peer_id, compact_mode, crypto_level, network_position);
/*     */         
/* 282 */         peer_out[0] = peer;
/*     */       }
/* 284 */       else if (this.request_type == 4)
/*     */       {
/* 286 */         if (link == null)
/*     */         {
/* 288 */           if ((hashes == null) || (hashes.length == 0))
/*     */           {
/* 290 */             throw new TRTrackerServerException("Hash missing from request ");
/*     */           }
/*     */           
/* 293 */           if (hashes.length != 1)
/*     */           {
/* 295 */             throw new TRTrackerServerException("Too many hashes for query");
/*     */           }
/*     */           
/* 298 */           byte[] hash = hashes[0];
/*     */           
/* 300 */           torrent = this.server.getTorrent(hash);
/*     */         }
/*     */         else
/*     */         {
/* 304 */           torrent = this.server.getTorrent(link);
/*     */         }
/*     */         
/* 307 */         if (torrent == null)
/*     */         {
/* 309 */           throw new TRTrackerServerException("Torrent unauthorised");
/*     */         }
/*     */         
/* 312 */         long interval = this.server.getAnnounceRetryInterval(torrent);
/*     */         
/* 314 */         root_out[0] = torrent.exportAnnounceToMap(client_ip_address, new HashMap(), null, true, num_want, interval, this.server.getMinAnnounceRetryInterval(), true, compact_mode, crypto_level, network_position);
/*     */       }
/*     */       else
/*     */       {
/* 318 */         if ((hashes == null) || (hashes.length == 0))
/*     */         {
/* 320 */           throw new TRTrackerServerException("Hash missing from request ");
/*     */         }
/*     */         
/* 323 */         boolean local_scrape = client_ip_address.equals("127.0.0.1");
/*     */         
/* 325 */         long max_interval = this.server.getMinScrapeRetryInterval();
/*     */         
/* 327 */         Map root = new HashMap();
/*     */         
/* 329 */         root_out[0] = root;
/*     */         
/* 331 */         Map files = new ByteEncodedKeyHashMap();
/*     */         
/* 333 */         root.put("files", files);
/*     */         
/* 335 */         char[] scrape_chars = scrape_flags == null ? null : scrape_flags.toCharArray();
/*     */         
/* 337 */         if ((scrape_chars != null) && (scrape_chars.length != hashes.length))
/*     */         {
/* 339 */           scrape_chars = null;
/*     */         }
/*     */         
/* 342 */         for (int i = 0; i < hashes.length; i++)
/*     */         {
/* 344 */           byte[] hash = hashes[i];
/*     */           
/*     */           String str_hash;
/*     */           try
/*     */           {
/* 349 */             str_hash = new String(hash, "ISO-8859-1");
/*     */             
/*     */ 
/*     */ 
/* 353 */             if ((i > 0) && (files.get(str_hash) != null)) {
/*     */               continue;
/*     */             }
/*     */           }
/*     */           catch (UnsupportedEncodingException e)
/*     */           {
/*     */             continue;
/*     */           }
/*     */           
/*     */ 
/* 363 */           torrent = this.server.getTorrent(hash);
/*     */           
/* 365 */           if (torrent == null)
/*     */           {
/* 367 */             if (!COConfigurationManager.getBooleanParameter("Tracker Public Enable")) {
/*     */               continue;
/*     */             }
/*     */             
/*     */ 
/*     */             try
/*     */             {
/* 374 */               torrent = (TRTrackerServerTorrentImpl)this.server.permit(real_ip_address, hash, false);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/*     */               continue;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 383 */           long interval = this.server.getScrapeRetryInterval(torrent);
/*     */           
/* 385 */           if (interval > max_interval)
/*     */           {
/* 387 */             max_interval = interval;
/*     */           }
/*     */           
/* 390 */           if ((scrape_chars != null) && (!loopback) && (!ip_override))
/*     */           {
/*     */ 
/*     */ 
/* 394 */             if (scrape_chars[i] == 'Q')
/*     */             {
/* 396 */               torrent.peerQueued(client_ip_address, port, udp_port, http_port, crypto_level, az_ver, (int)interval, true);
/*     */             }
/*     */           }
/*     */           
/* 400 */           if ((torrent.getRedirects() == null) || 
/*     */           
/* 402 */             (hashes.length <= 1))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 411 */             this.server.preProcess(new lightweightPeer(client_ip_address, port, peer_id), torrent, this.request_type, request, null);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 417 */             Map hash_entry = torrent.exportScrapeToMap(request, client_ip_address, !local_scrape);
/*     */             
/*     */ 
/*     */ 
/* 421 */             files.put(str_hash, hash_entry);
/*     */           }
/*     */         }
/* 424 */         if (hashes.length > 1)
/*     */         {
/* 426 */           torrent = null;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 431 */         addScrapeInterval(max_interval, root);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 436 */       if (!TRTrackerServerImpl.isFullScrapeEnabled())
/*     */       {
/* 438 */         throw new TRTrackerServerException("Full scrape disabled");
/*     */       }
/*     */       
/* 441 */       Map files = new ByteEncodedKeyHashMap();
/*     */       
/* 443 */       TRTrackerServerTorrentImpl[] torrents = this.server.getTorrents();
/*     */       
/* 445 */       for (int i = 0; i < torrents.length; i++)
/*     */       {
/* 447 */         TRTrackerServerTorrentImpl this_torrent = torrents[i];
/*     */         
/* 449 */         if (this_torrent.getRedirects() == null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 456 */           this.server.preProcess(new lightweightPeer(client_ip_address, port, peer_id), this_torrent, this.request_type, request, null);
/*     */           
/* 458 */           byte[] torrent_hash = this_torrent.getHash().getHash();
/*     */           try
/*     */           {
/* 461 */             String str_hash = new String(torrent_hash, "ISO-8859-1");
/*     */             
/*     */ 
/*     */ 
/* 465 */             Map hash_entry = this_torrent.exportScrapeToMap(request, client_ip_address, true);
/*     */             
/* 467 */             files.put(str_hash, hash_entry);
/*     */           }
/*     */           catch (UnsupportedEncodingException e)
/*     */           {
/* 471 */             throw new TRTrackerServerException("Encoding error", e);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 476 */       Map root = new HashMap();
/*     */       
/* 478 */       root_out[0] = root;
/*     */       
/* 480 */       addScrapeInterval(null, root);
/*     */       
/* 482 */       root.put("files", files);
/*     */     }
/*     */     
/* 485 */     return torrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addScrapeInterval(TRTrackerServerTorrentImpl torrent, Map root)
/*     */   {
/* 493 */     long interval = this.server.getScrapeRetryInterval(torrent);
/*     */     
/* 495 */     addScrapeInterval(interval, root);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addScrapeInterval(long interval, Map root)
/*     */   {
/* 503 */     if (interval > 0L)
/*     */     {
/* 505 */       Map flags = new HashMap();
/*     */       
/* 507 */       flags.put("min_request_interval", new Long(interval));
/*     */       
/* 509 */       root.put("flags", flags);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void taskCompleted()
/*     */   {
/* 516 */     if (this.start > 0L)
/*     */     {
/* 518 */       long time = SystemTime.getHighPrecisionCounter() - this.start;
/*     */       
/* 520 */       this.server.updateTime(this.request_type, time);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class lightweightPeer
/*     */     implements TRTrackerServerPeer
/*     */   {
/*     */     private final String ip;
/*     */     
/*     */     private final int port;
/*     */     
/*     */     private final byte[] peer_id;
/*     */     
/*     */ 
/*     */     public lightweightPeer(String _ip, int _port, HashWrapper _peer_id)
/*     */     {
/* 538 */       this.ip = _ip;
/* 539 */       this.port = _port;
/* 540 */       this.peer_id = (_peer_id == null ? null : _peer_id.getBytes());
/*     */     }
/*     */     
/*     */ 
/*     */     public long getUploaded()
/*     */     {
/* 546 */       return -1L;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getDownloaded()
/*     */     {
/* 552 */       return -1L;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getAmountLeft()
/*     */     {
/* 558 */       return -1L;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getIP()
/*     */     {
/* 564 */       return this.ip;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getIPRaw()
/*     */     {
/* 570 */       return this.ip;
/*     */     }
/*     */     
/*     */ 
/*     */     public byte getNATStatus()
/*     */     {
/* 576 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getTCPPort()
/*     */     {
/* 582 */       return this.port;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getHTTPPort()
/*     */     {
/* 588 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getUDPPort()
/*     */     {
/* 594 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */     public byte[] getPeerID()
/*     */     {
/* 600 */       return this.peer_id;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isBiased()
/*     */     {
/* 606 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setBiased(boolean biased) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setUserData(Object key, Object data) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public Object getUserData(Object key)
/*     */     {
/* 626 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getSecsToLive()
/*     */     {
/* 632 */       return -1;
/*     */     }
/*     */     
/*     */ 
/*     */     public Map export()
/*     */     {
/* 638 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/TRTrackerServerProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */