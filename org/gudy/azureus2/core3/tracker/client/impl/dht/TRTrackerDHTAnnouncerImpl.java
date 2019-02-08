/*     */ package org.gudy.azureus2.core3.tracker.client.impl.dht;
/*     */ 
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerDataProvider;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerException;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerListener;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponsePeer;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerHelper;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerImpl.Helper;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerResponseImpl;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerAnnouncerResponsePeerImpl;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.clientid.ClientIDException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer;
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
/*     */ public class TRTrackerDHTAnnouncerImpl
/*     */   implements TRTrackerAnnouncerHelper
/*     */ {
/*  62 */   public static final LogIDs LOGID = LogIDs.TRACKER;
/*     */   
/*     */   private final TOTorrent torrent;
/*     */   
/*     */   private HashWrapper torrent_hash;
/*     */   
/*     */   private final TRTrackerAnnouncerImpl.Helper helper;
/*     */   
/*     */   private byte[] data_peer_id;
/*     */   
/*     */   private String tracker_status_str;
/*     */   private long last_update_time;
/*  74 */   private int state = 1;
/*     */   
/*     */ 
/*     */ 
/*     */   private TRTrackerAnnouncerResponseImpl last_response;
/*     */   
/*     */ 
/*     */ 
/*     */   private final boolean manual;
/*     */   
/*     */ 
/*     */ 
/*     */   public TRTrackerDHTAnnouncerImpl(TOTorrent _torrent, String[] _networks, boolean _manual, TRTrackerAnnouncerImpl.Helper _helper)
/*     */     throws TRTrackerAnnouncerException
/*     */   {
/*  89 */     this.torrent = _torrent;
/*  90 */     this.manual = _manual;
/*  91 */     this.helper = _helper;
/*     */     try
/*     */     {
/*  94 */       this.torrent_hash = this.torrent.getHashWrapper();
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/*  98 */       Debug.printStackTrace(e);
/*     */     }
/*     */     try {
/* 101 */       this.data_peer_id = ClientIDManagerImpl.getSingleton().generatePeerID(this.torrent_hash.getBytes(), false);
/*     */     }
/*     */     catch (ClientIDException e)
/*     */     {
/* 105 */       throw new TRTrackerAnnouncerException("TRTrackerAnnouncer: Peer ID generation fails", e);
/*     */     }
/*     */     
/* 108 */     this.last_response = new TRTrackerAnnouncerResponseImpl(this.torrent.getAnnounceURL(), this.torrent_hash, 0, 0L, "Initialising");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 114 */     this.tracker_status_str = (MessageText.getString("PeerManager.status.checking") + "...");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAnnounceDataProvider(TRTrackerAnnouncerDataProvider provider) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isManual()
/*     */   {
/* 126 */     return this.manual;
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrent getTorrent()
/*     */   {
/* 132 */     return this.torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getTrackerURL()
/*     */   {
/* 138 */     return TorrentUtils.getDecentralisedURL(this.torrent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTrackerURL(URL url)
/*     */   {
/* 145 */     Debug.out("Not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrentAnnounceURLSet[] getAnnounceSets()
/*     */   {
/* 151 */     return new TOTorrentAnnounceURLSet[] { this.torrent.getAnnounceURLGroup().createAnnounceURLSet(new URL[] { TorrentUtils.getDecentralisedURL(this.torrent) }) };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resetTrackerUrl(boolean shuffle) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIPOverride(String override) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clearIPOverride() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 176 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getPeerId()
/*     */   {
/* 182 */     return this.data_peer_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRefreshDelayOverrides(int percentage) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public int getTimeUntilNextUpdate()
/*     */   {
/* 194 */     long elapsed = (SystemTime.getCurrentTime() - this.last_update_time) / 1000L;
/*     */     
/* 196 */     return (int)(this.last_response.getTimeToWait() - elapsed);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLastUpdateTime()
/*     */   {
/* 202 */     return (int)(this.last_update_time / 1000L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void update(boolean force)
/*     */   {
/* 209 */     this.state = 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void complete(boolean already_reported)
/*     */   {
/* 216 */     this.state = 3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void stop(boolean for_queue)
/*     */   {
/* 223 */     this.state = 4;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public int getStatus()
/*     */   {
/* 234 */     return this.state;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStatusString()
/*     */   {
/* 240 */     return this.tracker_status_str;
/*     */   }
/*     */   
/*     */ 
/*     */   public TRTrackerAnnouncer getBestAnnouncer()
/*     */   {
/* 246 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public TRTrackerAnnouncerResponse getLastResponse()
/*     */   {
/* 252 */     return this.last_response;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isUpdating()
/*     */   {
/* 258 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getInterval()
/*     */   {
/* 264 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getMinInterval()
/*     */   {
/* 270 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void refreshListeners() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAnnounceResult(DownloadAnnounceResult result)
/*     */   {
/* 282 */     this.last_update_time = SystemTime.getCurrentTime();
/*     */     
/*     */     TRTrackerAnnouncerResponseImpl response;
/*     */     TRTrackerAnnouncerResponseImpl response;
/* 286 */     if (result.getResponseType() == 2)
/*     */     {
/* 288 */       this.tracker_status_str = MessageText.getString("PeerManager.status.error");
/*     */       
/* 290 */       String reason = result.getError();
/*     */       
/* 292 */       if (reason != null)
/*     */       {
/* 294 */         this.tracker_status_str = (this.tracker_status_str + " (" + reason + ")");
/*     */       }
/*     */       
/* 297 */       response = new TRTrackerAnnouncerResponseImpl(result.getURL(), this.torrent_hash, 0, result.getTimeToWait(), reason);
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 304 */       DownloadAnnounceResultPeer[] ext_peers = result.getPeers();
/*     */       
/* 306 */       List<TRTrackerAnnouncerResponsePeerImpl> peers_list = new ArrayList(ext_peers.length);
/*     */       
/* 308 */       for (int i = 0; i < ext_peers.length; i++)
/*     */       {
/* 310 */         DownloadAnnounceResultPeer ext_peer = ext_peers[i];
/*     */         
/* 312 */         if (ext_peer != null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 317 */           if (Logger.isEnabled()) {
/* 318 */             Logger.log(new LogEvent(this.torrent, LOGID, "EXTERNAL PEER DHT: ip=" + ext_peer.getAddress() + ",port=" + ext_peer.getPort() + ",prot=" + ext_peer.getProtocol()));
/*     */           }
/*     */           
/*     */ 
/* 322 */           int http_port = 0;
/* 323 */           byte az_version = 1;
/*     */           
/* 325 */           peers_list.add(new TRTrackerAnnouncerResponsePeerImpl(ext_peer.getSource(), ext_peer.getPeerID(), ext_peer.getAddress(), ext_peer.getPort(), ext_peer.getUDPPort(), http_port, ext_peer.getProtocol(), az_version, 0));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 337 */       TRTrackerAnnouncerResponsePeerImpl[] peers = (TRTrackerAnnouncerResponsePeerImpl[])peers_list.toArray(new TRTrackerAnnouncerResponsePeerImpl[peers_list.size()]);
/*     */       
/* 339 */       this.helper.addToTrackerCache(peers);
/*     */       
/* 341 */       this.tracker_status_str = MessageText.getString("PeerManager.status.ok");
/*     */       
/* 343 */       response = new TRTrackerAnnouncerResponseImpl(result.getURL(), this.torrent_hash, 2, result.getTimeToWait(), peers);
/*     */     }
/*     */     
/* 346 */     this.last_response = response;
/*     */     
/* 348 */     TRTrackerAnnouncerResponsePeer[] peers = response.getPeers();
/*     */     
/* 350 */     if ((peers == null) || (peers.length < 5))
/*     */     {
/* 352 */       TRTrackerAnnouncerResponsePeer[] cached_peers = this.helper.getPeersFromCache(100);
/*     */       
/* 354 */       if (cached_peers.length > 0)
/*     */       {
/* 356 */         Set<TRTrackerAnnouncerResponsePeer> new_peers = new TreeSet(new Comparator()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public int compare(TRTrackerAnnouncerResponsePeer o1, TRTrackerAnnouncerResponsePeer o2)
/*     */           {
/*     */ 
/*     */ 
/* 365 */             return o1.compareTo(o2);
/*     */           }
/*     */         });
/*     */         
/* 369 */         if (peers != null)
/*     */         {
/* 371 */           new_peers.addAll(Arrays.asList(peers));
/*     */         }
/*     */         
/* 374 */         new_peers.addAll(Arrays.asList(cached_peers));
/*     */         
/* 376 */         response.setPeers((TRTrackerAnnouncerResponsePeer[])new_peers.toArray(new TRTrackerAnnouncerResponsePeer[new_peers.size()]));
/*     */       }
/*     */     }
/*     */     
/* 380 */     this.helper.informResponse(this, response);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TRTrackerAnnouncerListener l)
/*     */   {
/* 387 */     this.helper.addListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(TRTrackerAnnouncerListener l)
/*     */   {
/* 394 */     this.helper.removeListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTrackerResponseCache(Map map)
/*     */   {
/* 401 */     this.helper.setTrackerResponseCache(map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeFromTrackerResponseCache(String ip, int tcpPort)
/*     */   {
/* 408 */     this.helper.removeFromTrackerResponseCache(ip, tcpPort);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getTrackerResponseCache()
/*     */   {
/* 414 */     return this.helper.getTrackerResponseCache();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TrackerPeerSource getTrackerPeerSource(TOTorrentAnnounceURLSet set)
/*     */   {
/* 421 */     Debug.out("not implemented");
/*     */     
/* 423 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public TrackerPeerSource getCacheTrackerPeerSource()
/*     */   {
/* 429 */     Debug.out("not implemented");
/*     */     
/* 431 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void generateEvidence(IndentWriter writer)
/*     */   {
/* 438 */     writer.println("DHT announce: " + (this.last_response == null ? "null" : this.last_response.getString()));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/dht/TRTrackerDHTAnnouncerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */