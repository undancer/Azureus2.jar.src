/*     */ package org.gudy.azureus2.core3.tracker.host.impl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraper;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperFactory;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostException;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostPeer;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentListener;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentRemovalVetoException;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentRequest;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentWillBeRemovedListener;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrent;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class TRHostTorrentPublishImpl
/*     */   implements TRHostTorrent
/*     */ {
/*     */   private final TRHostImpl host;
/*     */   private TOTorrent torrent;
/*     */   private final long date_added;
/*     */   private static final int status = 3;
/*     */   private boolean persistent;
/*     */   private int seed_count;
/*     */   private int peer_count;
/*  53 */   private TRHostPeer[] peers = new TRHostPeer[0];
/*     */   
/*  55 */   private List listeners_cow = new ArrayList();
/*  56 */   private final List removal_listeners = new ArrayList();
/*     */   
/*     */   private HashMap data;
/*     */   
/*  60 */   protected final AEMonitor this_mon = new AEMonitor("TRHostTorrentPublish");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TRHostTorrentPublishImpl(TRHostImpl _host, TOTorrent _torrent, long _date_added)
/*     */   {
/*  68 */     this.host = _host;
/*  69 */     this.torrent = _torrent;
/*  70 */     this.date_added = _date_added;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void stop() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove()
/*     */     throws TRHostTorrentRemovalVetoException
/*     */   {
/*     */     try
/*     */     {
/*  89 */       this.this_mon.enter();
/*     */       
/*  91 */       canBeRemoved();
/*     */       
/*  93 */       this.host.remove(this);
/*     */     }
/*     */     finally
/*     */     {
/*  97 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean canBeRemoved()
/*     */     throws TRHostTorrentRemovalVetoException
/*     */   {
/* 106 */     for (int i = 0; i < this.removal_listeners.size(); i++)
/*     */     {
/* 108 */       ((TRHostTorrentWillBeRemovedListener)this.removal_listeners.get(i)).torrentWillBeRemoved(this);
/*     */     }
/*     */     
/* 111 */     return true;
/*     */   }
/*     */   
/*     */   public int getStatus()
/*     */   {
/* 116 */     return 3;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPersistent()
/*     */   {
/* 122 */     return this.persistent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPersistent(boolean _persistent)
/*     */   {
/* 129 */     this.persistent = _persistent;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPassive()
/*     */   {
/* 135 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPassive(boolean passive) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public long getDateAdded()
/*     */   {
/* 147 */     return this.date_added;
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrent getTorrent()
/*     */   {
/* 153 */     return this.torrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTorrent(TOTorrent t)
/*     */   {
/* 160 */     this.torrent = t;
/*     */   }
/*     */   
/*     */ 
/*     */   public TRTrackerServerTorrent getTrackerTorrent()
/*     */   {
/* 166 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 172 */     return -1;
/*     */   }
/*     */   
/*     */   public TRHostPeer[] getPeers()
/*     */   {
/*     */     try
/*     */     {
/* 179 */       this.this_mon.enter();
/*     */       
/* 181 */       return this.peers;
/*     */     }
/*     */     finally {
/* 184 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAnnounceCount()
/*     */   {
/* 191 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageAnnounceCount()
/*     */   {
/* 197 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getScrapeCount()
/*     */   {
/* 203 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageScrapeCount()
/*     */   {
/* 209 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCompletedCount()
/*     */   {
/* 215 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void updateStats()
/*     */   {
/* 221 */     TRTrackerScraperResponse resp = null;
/*     */     
/* 223 */     TRTrackerAnnouncer tc = this.host.getTrackerClient(this);
/*     */     
/* 225 */     if (tc != null)
/*     */     {
/* 227 */       resp = TRTrackerScraperFactory.getSingleton().scrape(tc);
/*     */     }
/*     */     
/* 230 */     if (resp == null)
/*     */     {
/* 232 */       resp = TRTrackerScraperFactory.getSingleton().scrape(this.torrent);
/*     */     }
/*     */     try
/*     */     {
/* 236 */       this.this_mon.enter();
/*     */       
/* 238 */       if ((resp != null) && (resp.isValid()))
/*     */       {
/* 240 */         this.peer_count = resp.getPeers();
/* 241 */         this.seed_count = resp.getSeeds();
/*     */         
/* 243 */         this.peers = new TRHostPeer[this.peer_count + this.seed_count];
/*     */         
/* 245 */         for (int i = 0; i < this.peers.length; i++)
/*     */         {
/* 247 */           this.peers[i] = new TRHostPeerPublishImpl(i < this.seed_count);
/*     */         }
/*     */       }
/*     */       else {
/* 251 */         this.peers = new TRHostPeer[0];
/*     */       }
/*     */     }
/*     */     finally {
/* 255 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeedCount()
/*     */   {
/* 262 */     return this.seed_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLeecherCount()
/*     */   {
/* 268 */     return this.peer_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getBadNATCount()
/*     */   {
/* 274 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalUploaded()
/*     */   {
/* 280 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalDownloaded()
/*     */   {
/* 286 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalLeft()
/*     */   {
/* 292 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageUploaded()
/*     */   {
/* 298 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageDownloaded()
/*     */   {
/* 304 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytesIn()
/*     */   {
/* 310 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytesOut()
/*     */   {
/* 316 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesIn()
/*     */   {
/* 322 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesOut()
/*     */   {
/* 328 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void disableReplyCaching() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void preProcess(TRHostTorrentRequest req)
/*     */     throws TRHostException
/*     */   {
/* 342 */     List listeners_ref = this.listeners_cow;
/*     */     
/* 344 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*     */       try
/*     */       {
/* 347 */         ((TRHostTorrentListener)listeners_ref.get(i)).preProcess(req);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 351 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void postProcess(TRHostTorrentRequest req)
/*     */     throws TRHostException
/*     */   {
/* 362 */     List listeners_ref = this.listeners_cow;
/*     */     
/* 364 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*     */       try
/*     */       {
/* 367 */         ((TRHostTorrentListener)listeners_ref.get(i)).postProcess(req);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 371 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addListener(TRHostTorrentListener l)
/*     */   {
/*     */     try
/*     */     {
/* 381 */       this.this_mon.enter();
/*     */       
/* 383 */       List new_listeners = new ArrayList(this.listeners_cow);
/*     */       
/* 385 */       new_listeners.add(l);
/*     */       
/* 387 */       this.listeners_cow = new_listeners;
/*     */     }
/*     */     finally
/*     */     {
/* 391 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 394 */     this.host.torrentListenerRegistered();
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeListener(TRHostTorrentListener l)
/*     */   {
/*     */     try
/*     */     {
/* 402 */       this.this_mon.enter();
/*     */       
/* 404 */       List new_listeners = new ArrayList(this.listeners_cow);
/*     */       
/* 406 */       new_listeners.remove(l);
/*     */       
/* 408 */       this.listeners_cow = new_listeners;
/*     */     }
/*     */     finally
/*     */     {
/* 412 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addRemovalListener(TRHostTorrentWillBeRemovedListener l)
/*     */   {
/*     */     try
/*     */     {
/* 421 */       this.this_mon.enter();
/*     */       
/* 423 */       this.removal_listeners.add(l);
/*     */     }
/*     */     finally
/*     */     {
/* 427 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeRemovalListener(TRHostTorrentWillBeRemovedListener l)
/*     */   {
/*     */     try
/*     */     {
/* 436 */       this.this_mon.enter();
/*     */       
/* 438 */       this.removal_listeners.remove(l);
/*     */     }
/*     */     finally {
/* 441 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getData(String key)
/*     */   {
/* 447 */     if (this.data == null) return null;
/* 448 */     return this.data.get(key);
/*     */   }
/*     */   
/*     */   public void setData(String key, Object value)
/*     */   {
/*     */     try {
/* 454 */       this.this_mon.enter();
/*     */       
/* 456 */       if (this.data == null) {
/* 457 */         this.data = new HashMap();
/*     */       }
/* 459 */       if (value == null) {
/* 460 */         if (this.data.containsKey(key))
/* 461 */           this.data.remove(key);
/*     */       } else {
/* 463 */         this.data.put(key, value);
/*     */       }
/*     */     }
/*     */     finally {
/* 467 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/impl/TRHostTorrentPublishImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */