/*     */ package org.gudy.azureus2.core3.tracker.client.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSourceAdapter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerException;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerListener;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponsePeer;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
/*     */ import org.gudy.azureus2.core3.util.ListenerManager;
/*     */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.plugins.clientid.ClientIDException;
/*     */ import org.gudy.azureus2.pluginsimpl.local.clientid.ClientIDManagerImpl;
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
/*     */ public abstract class TRTrackerAnnouncerImpl
/*     */   implements TRTrackerAnnouncer
/*     */ {
/*  65 */   public static final LogIDs LOGID = LogIDs.TRACKER;
/*     */   
/*     */   protected static final int LDT_TRACKER_RESPONSE = 1;
/*     */   
/*     */   protected static final int LDT_URL_CHANGED = 2;
/*     */   
/*     */   protected static final int LDT_URL_REFRESH = 3;
/*     */   
/*     */   private static final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
/*     */   
/*     */   private static final int key_id_length = 8;
/*     */   
/*     */ 
/*     */   private static String createKeyID()
/*     */   {
/*  80 */     String key_id = "";
/*     */     
/*  82 */     for (int i = 0; i < 8; i++) {
/*  83 */       int pos = RandomUtils.nextInt("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length());
/*  84 */       key_id = key_id + "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(pos);
/*     */     }
/*     */     
/*  87 */     return key_id;
/*     */   }
/*     */   
/*  90 */   protected final ListenerManager<TRTrackerAnnouncerListener> listeners = ListenerManager.createManager("TrackerClient:ListenDispatcher", new ListenerManagerDispatcher()
/*     */   {
/*     */ 
/*     */ 
/*     */ 
/*     */     public void dispatch(TRTrackerAnnouncerListener listener, int type, Object value)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 100 */       if (type == 1)
/*     */       {
/* 102 */         listener.receivedTrackerResponse((TRTrackerAnnouncerResponse)value);
/*     */       }
/* 104 */       else if (type == 2)
/*     */       {
/* 106 */         Object[] x = (Object[])value;
/*     */         
/* 108 */         URL old_url = (URL)x[0];
/* 109 */         URL new_url = (URL)x[1];
/* 110 */         boolean explicit = ((Boolean)x[2]).booleanValue();
/*     */         
/* 112 */         listener.urlChanged(TRTrackerAnnouncerImpl.this, old_url, new_url, explicit);
/*     */       }
/*     */       else
/*     */       {
/* 116 */         listener.urlRefresh();
/*     */       }
/*     */     }
/*  90 */   });
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
/* 121 */   final Map tracker_peer_cache = new LinkedHashMap();
/* 122 */   private final AEMonitor tracker_peer_cache_mon = new AEMonitor("TRTrackerClientClassic:PC");
/*     */   
/*     */   private int cache_peers_used;
/*     */   
/*     */   private final TOTorrent torrent;
/*     */   
/*     */   private final byte[] peer_id;
/*     */   
/*     */   private final String tracker_key;
/*     */   
/*     */   private final int udp_key;
/*     */   
/*     */   protected TRTrackerAnnouncerImpl(TOTorrent _torrent)
/*     */     throws TRTrackerAnnouncerException
/*     */   {
/* 137 */     this.torrent = _torrent;
/*     */     
/* 139 */     this.tracker_key = createKeyID();
/*     */     
/* 141 */     this.udp_key = RandomUtils.nextInt();
/*     */     try
/*     */     {
/* 144 */       byte[] hash = null;
/*     */       try
/*     */       {
/* 147 */         hash = this.torrent.getHash();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 152 */       this.peer_id = ClientIDManagerImpl.getSingleton().generatePeerID(hash, false);
/*     */     }
/*     */     catch (ClientIDException e)
/*     */     {
/* 156 */       throw new TRTrackerAnnouncerException("TRTrackerAnnouncer: Peer ID generation fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrent getTorrent()
/*     */   {
/* 163 */     return this.torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public Helper getHelper()
/*     */   {
/* 169 */     new Helper()
/*     */     {
/*     */ 
/*     */       public byte[] getPeerID()
/*     */       {
/*     */ 
/* 175 */         return TRTrackerAnnouncerImpl.this.peer_id;
/*     */       }
/*     */       
/*     */ 
/*     */       public String getTrackerKey()
/*     */       {
/* 181 */         return TRTrackerAnnouncerImpl.this.tracker_key;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getUDPKey()
/*     */       {
/* 187 */         return TRTrackerAnnouncerImpl.this.udp_key;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void addToTrackerCache(TRTrackerAnnouncerResponsePeerImpl[] peers)
/*     */       {
/* 194 */         TRTrackerAnnouncerImpl.this.addToTrackerCache(peers);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public TRTrackerAnnouncerResponsePeer[] getPeersFromCache(int num_want)
/*     */       {
/* 201 */         return TRTrackerAnnouncerImpl.this.getPeersFromCache(num_want);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setTrackerResponseCache(Map map)
/*     */       {
/* 208 */         TRTrackerAnnouncerImpl.this.setTrackerResponseCache(map);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void removeFromTrackerResponseCache(String ip, int tcpPort)
/*     */       {
/* 215 */         TRTrackerAnnouncerImpl.this.removeFromTrackerResponseCache(ip, tcpPort);
/*     */       }
/*     */       
/*     */ 
/*     */       public Map getTrackerResponseCache()
/*     */       {
/* 221 */         return TRTrackerAnnouncerImpl.this.getTrackerResponseCache();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void informResponse(TRTrackerAnnouncerHelper helper, TRTrackerAnnouncerResponse response)
/*     */       {
/* 229 */         TRTrackerAnnouncerImpl.this.informResponse(helper, response);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void informURLChange(URL old_url, URL new_url, boolean explicit)
/*     */       {
/* 238 */         TRTrackerAnnouncerImpl.this.listeners.dispatch(2, new Object[] { old_url, new_url, Boolean.valueOf(explicit) });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void informURLRefresh()
/*     */       {
/* 245 */         TRTrackerAnnouncerImpl.this.informURLRefresh();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void addListener(TRTrackerAnnouncerListener l)
/*     */       {
/* 252 */         TRTrackerAnnouncerImpl.this.addListener(l);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void removeListener(TRTrackerAnnouncerListener l)
/*     */       {
/* 259 */         TRTrackerAnnouncerImpl.this.removeListener(l);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getPeerId()
/*     */   {
/* 267 */     return this.peer_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] getAnonymousPeerId(String my_ip, int my_port)
/*     */   {
/* 275 */     byte[] anon_peer_id = new byte[20];
/*     */     
/*     */ 
/*     */ 
/* 279 */     anon_peer_id[0] = 91;
/* 280 */     anon_peer_id[1] = 93;
/*     */     try
/*     */     {
/* 283 */       byte[] ip_bytes = my_ip.getBytes("UTF8");
/* 284 */       int ip_len = ip_bytes.length;
/*     */       
/* 286 */       if (ip_len > 18)
/*     */       {
/* 288 */         ip_len = 18;
/*     */       }
/*     */       
/* 291 */       System.arraycopy(ip_bytes, 0, anon_peer_id, 2, ip_len);
/*     */       
/* 293 */       int port_copy = my_port;
/*     */       
/* 295 */       for (int j = 2 + ip_len; j < 20; j++)
/*     */       {
/* 297 */         anon_peer_id[j] = ((byte)(port_copy & 0xFF));
/*     */         
/* 299 */         port_copy >>= 8;
/*     */       }
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 303 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 306 */     return anon_peer_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map getTrackerResponseCache()
/*     */   {
/* 315 */     return exportTrackerCache();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTrackerResponseCache(Map map)
/*     */   {
/* 323 */     int num = importTrackerCache(map);
/*     */     
/* 325 */     if (Logger.isEnabled()) {
/* 326 */       Logger.log(new LogEvent(getTorrent(), LOGID, "TRTrackerClient: imported " + num + " cached peers"));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected Map exportTrackerCache()
/*     */   {
/* 333 */     Map res = new LightHashMap(1);
/*     */     
/* 335 */     List peers = new ArrayList();
/*     */     
/* 337 */     res.put("tracker_peers", peers);
/*     */     try
/*     */     {
/* 340 */       this.tracker_peer_cache_mon.enter();
/*     */       
/* 342 */       Iterator it = this.tracker_peer_cache.values().iterator();
/*     */       
/* 344 */       while (it.hasNext())
/*     */       {
/* 346 */         TRTrackerAnnouncerResponsePeer peer = (TRTrackerAnnouncerResponsePeer)it.next();
/*     */         
/* 348 */         LightHashMap entry = new LightHashMap();
/*     */         
/* 350 */         entry.put("ip", peer.getAddress().getBytes());
/* 351 */         entry.put("src", peer.getSource().getBytes());
/* 352 */         entry.put("port", new Long(peer.getPort()));
/*     */         
/* 354 */         int udp_port = peer.getUDPPort();
/* 355 */         if (udp_port != 0) {
/* 356 */           entry.put("udpport", new Long(udp_port));
/*     */         }
/* 358 */         int http_port = peer.getHTTPPort();
/* 359 */         if (http_port != 0) {
/* 360 */           entry.put("httpport", new Long(http_port));
/*     */         }
/*     */         
/* 363 */         entry.put("prot", new Long(peer.getProtocol()));
/*     */         
/* 365 */         byte az_ver = peer.getAZVersion();
/*     */         
/* 367 */         if (az_ver != 1) {
/* 368 */           entry.put("azver", new Long(az_ver));
/*     */         }
/*     */         
/* 371 */         entry.compactify(0.9F);
/*     */         
/* 373 */         peers.add(entry);
/*     */       }
/*     */       
/* 376 */       if (Logger.isEnabled()) {
/* 377 */         Logger.log(new LogEvent(getTorrent(), LOGID, "TRTrackerClient: exported " + this.tracker_peer_cache.size() + " cached peers"));
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 382 */       this.tracker_peer_cache_mon.exit();
/*     */     }
/*     */     
/* 385 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int importTrackerCache(Map map)
/*     */   {
/* 392 */     if (!COConfigurationManager.getBooleanParameter("File.save.peers.enable"))
/*     */     {
/* 394 */       return 0;
/*     */     }
/*     */     try
/*     */     {
/* 398 */       if (map == null)
/*     */       {
/* 400 */         return 0;
/*     */       }
/*     */       
/* 403 */       List peers = (List)map.get("tracker_peers");
/*     */       
/* 405 */       if (peers == null)
/*     */       {
/* 407 */         return 0;
/*     */       }
/*     */       try
/*     */       {
/* 411 */         this.tracker_peer_cache_mon.enter();
/*     */         
/* 413 */         for (int i = 0; i < peers.size(); i++)
/*     */         {
/* 415 */           Map peer = (Map)peers.get(i);
/*     */           
/* 417 */           byte[] src_bytes = (byte[])peer.get("src");
/* 418 */           String peer_source = src_bytes == null ? "Tracker" : new String(src_bytes);
/* 419 */           String peer_ip_address = new String((byte[])peer.get("ip"));
/* 420 */           int peer_tcp_port = ((Long)peer.get("port")).intValue();
/* 421 */           byte[] peer_peer_id = getAnonymousPeerId(peer_ip_address, peer_tcp_port);
/* 422 */           Long l_protocol = (Long)peer.get("prot");
/* 423 */           short protocol = l_protocol == null ? 1 : l_protocol.shortValue();
/* 424 */           Long l_udp_port = (Long)peer.get("udpport");
/* 425 */           int peer_udp_port = l_udp_port == null ? 0 : l_udp_port.intValue();
/* 426 */           Long l_http_port = (Long)peer.get("httpport");
/* 427 */           int peer_http_port = l_http_port == null ? 0 : l_http_port.intValue();
/* 428 */           Long l_az_ver = (Long)peer.get("azver");
/* 429 */           byte az_ver = l_az_ver == null ? 1 : l_az_ver.byteValue();
/*     */           
/*     */ 
/*     */ 
/* 433 */           TRTrackerAnnouncerResponsePeerImpl entry = new TRTrackerAnnouncerResponsePeerImpl(peer_source, peer_peer_id, peer_ip_address, peer_tcp_port, peer_udp_port, peer_http_port, protocol, az_ver, 0);
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
/* 445 */           this.tracker_peer_cache.put(entry.getKey(), entry);
/*     */         }
/*     */         
/* 448 */         return this.tracker_peer_cache.size();
/*     */       }
/*     */       finally
/*     */       {
/* 452 */         this.tracker_peer_cache_mon.exit();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 458 */       return this.tracker_peer_cache.size();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 456 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addToTrackerCache(TRTrackerAnnouncerResponsePeerImpl[] peers)
/*     */   {
/* 466 */     if (!COConfigurationManager.getBooleanParameter("File.save.peers.enable"))
/*     */     {
/* 468 */       return;
/*     */     }
/*     */     
/* 471 */     int max = COConfigurationManager.getIntParameter("File.save.peers.max", 512);
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 476 */       this.tracker_peer_cache_mon.enter();
/*     */       
/* 478 */       for (int i = 0; i < peers.length; i++)
/*     */       {
/* 480 */         TRTrackerAnnouncerResponsePeerImpl peer = peers[i];
/*     */         
/*     */ 
/*     */ 
/* 484 */         this.tracker_peer_cache.remove(peer.getKey());
/*     */         
/* 486 */         this.tracker_peer_cache.put(peer.getKey(), peer);
/*     */       }
/*     */       
/* 489 */       Iterator it = this.tracker_peer_cache.keySet().iterator();
/*     */       
/* 491 */       if (max > 0)
/*     */       {
/* 493 */         while (this.tracker_peer_cache.size() > max)
/*     */         {
/* 495 */           it.next();
/*     */           
/* 497 */           it.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 502 */       this.tracker_peer_cache_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeFromTrackerResponseCache(String ip, int tcp_port)
/*     */   {
/*     */     try
/*     */     {
/* 512 */       this.tracker_peer_cache_mon.enter();
/*     */       
/*     */ 
/*     */ 
/* 516 */       TRTrackerAnnouncerResponsePeerImpl peer = new TRTrackerAnnouncerResponsePeerImpl("", new byte[0], ip, tcp_port, 0, 0, (short)0, (byte)0, 0);
/*     */       
/*     */ 
/* 519 */       if (this.tracker_peer_cache.remove(peer.getKey()) != null)
/*     */       {
/* 521 */         if (Logger.isEnabled()) {
/* 522 */           Logger.log(new LogEvent(getTorrent(), LOGID, "Explicit removal of peer cache for " + ip + ":" + tcp_port));
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 527 */       this.tracker_peer_cache_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Map mergeResponseCache(Map map1, Map map2)
/*     */   {
/* 536 */     if ((map1 == null) && (map2 == null))
/* 537 */       return new HashMap();
/* 538 */     if (map1 == null)
/* 539 */       return map2;
/* 540 */     if (map2 == null) {
/* 541 */       return map1;
/*     */     }
/*     */     
/* 544 */     Map res = new HashMap();
/*     */     
/* 546 */     List peers = (List)map1.get("tracker_peers");
/*     */     
/* 548 */     if (peers == null)
/*     */     {
/* 550 */       peers = new ArrayList();
/*     */     }
/*     */     
/* 553 */     List p2 = (List)map2.get("tracker_peers");
/*     */     
/* 555 */     if (p2 != null)
/*     */     {
/* 557 */       if (Logger.isEnabled()) {
/* 558 */         Logger.log(new LogEvent(LOGID, "TRTrackerClient: merged peer sets: p1 = " + peers.size() + ", p2 = " + p2.size()));
/*     */       }
/*     */       
/*     */ 
/* 562 */       for (int i = 0; i < p2.size(); i++)
/*     */       {
/* 564 */         peers.add(p2.get(i));
/*     */       }
/*     */     }
/*     */     
/* 568 */     res.put("tracker_peers", peers);
/*     */     
/* 570 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract int getPeerCacheLimit();
/*     */   
/*     */ 
/*     */   protected TRTrackerAnnouncerResponsePeer[] getPeersFromCache(int num_want)
/*     */   {
/* 580 */     int limit = getPeerCacheLimit();
/*     */     
/* 582 */     if (limit <= 0)
/*     */     {
/* 584 */       return new TRTrackerAnnouncerResponsePeer[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 590 */     num_want = Math.min(limit, num_want);
/*     */     try
/*     */     {
/* 593 */       this.tracker_peer_cache_mon.enter();
/*     */       
/*     */       TRTrackerAnnouncerResponsePeerImpl[] res;
/*     */       
/* 597 */       if (this.tracker_peer_cache.size() <= num_want)
/*     */       {
/* 599 */         TRTrackerAnnouncerResponsePeerImpl[] res = new TRTrackerAnnouncerResponsePeerImpl[this.tracker_peer_cache.size()];
/*     */         
/* 601 */         this.tracker_peer_cache.values().toArray(res);
/*     */       }
/*     */       else
/*     */       {
/* 605 */         res = new TRTrackerAnnouncerResponsePeerImpl[num_want];
/*     */         
/* 607 */         Iterator it = this.tracker_peer_cache.keySet().iterator();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 612 */         for (int i = 0; i < num_want; i++)
/*     */         {
/* 614 */           String key = (String)it.next();
/*     */           
/* 616 */           res[i] = ((TRTrackerAnnouncerResponsePeerImpl)this.tracker_peer_cache.get(key));
/*     */           
/* 618 */           it.remove();
/*     */         }
/*     */         
/* 621 */         for (int i = 0; i < num_want; i++)
/*     */         {
/* 623 */           this.tracker_peer_cache.put(res[i].getKey(), res[i]);
/*     */         }
/*     */       }
/*     */       int i;
/* 627 */       if (Logger.isEnabled())
/*     */       {
/* 629 */         for (i = 0; i < res.length; i++)
/*     */         {
/* 631 */           Logger.log(new LogEvent(getTorrent(), LOGID, "CACHED PEER: " + res[i].getString()));
/*     */         }
/*     */         
/* 634 */         Logger.log(new LogEvent(getTorrent(), LOGID, "TRTrackerClient: returned " + res.length + " cached peers"));
/*     */       }
/*     */       
/*     */ 
/* 638 */       this.cache_peers_used += res.length;
/*     */       
/* 640 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 644 */       this.tracker_peer_cache_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public TrackerPeerSource getCacheTrackerPeerSource()
/*     */   {
/* 651 */     new TrackerPeerSourceAdapter()
/*     */     {
/*     */ 
/*     */       public String getName()
/*     */       {
/*     */ 
/* 657 */         return MessageText.getString("tps.tracker.cache1", new String[] { String.valueOf(TRTrackerAnnouncerImpl.this.cache_peers_used) });
/*     */       }
/*     */       
/*     */ 
/*     */       public int getPeers()
/*     */       {
/* 663 */         return TRTrackerAnnouncerImpl.this.tracker_peer_cache.size();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void informResponse(TRTrackerAnnouncerHelper helper, TRTrackerAnnouncerResponse response)
/*     */   {
/* 673 */     this.listeners.dispatch(1, response);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void informURLRefresh()
/*     */   {
/* 679 */     this.listeners.dispatch(3, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TRTrackerAnnouncerListener l)
/*     */   {
/* 686 */     this.listeners.addListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(TRTrackerAnnouncerListener l)
/*     */   {
/* 693 */     this.listeners.removeListener(l);
/*     */   }
/*     */   
/*     */   public static abstract interface Helper
/*     */   {
/*     */     public abstract byte[] getPeerID();
/*     */     
/*     */     public abstract String getTrackerKey();
/*     */     
/*     */     public abstract int getUDPKey();
/*     */     
/*     */     public abstract void addToTrackerCache(TRTrackerAnnouncerResponsePeerImpl[] paramArrayOfTRTrackerAnnouncerResponsePeerImpl);
/*     */     
/*     */     public abstract TRTrackerAnnouncerResponsePeer[] getPeersFromCache(int paramInt);
/*     */     
/*     */     public abstract void setTrackerResponseCache(Map paramMap);
/*     */     
/*     */     public abstract void removeFromTrackerResponseCache(String paramString, int paramInt);
/*     */     
/*     */     public abstract Map getTrackerResponseCache();
/*     */     
/*     */     public abstract void informResponse(TRTrackerAnnouncerHelper paramTRTrackerAnnouncerHelper, TRTrackerAnnouncerResponse paramTRTrackerAnnouncerResponse);
/*     */     
/*     */     public abstract void informURLChange(URL paramURL1, URL paramURL2, boolean paramBoolean);
/*     */     
/*     */     public abstract void informURLRefresh();
/*     */     
/*     */     public abstract void addListener(TRTrackerAnnouncerListener paramTRTrackerAnnouncerListener);
/*     */     
/*     */     public abstract void removeListener(TRTrackerAnnouncerListener paramTRTrackerAnnouncerListener);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/TRTrackerAnnouncerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */