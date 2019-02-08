/*     */ package org.gudy.azureus2.core3.tracker.host.impl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostException;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostPeer;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentListener;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentRemovalVetoException;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentRequest;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentWillBeRemovedListener;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServer;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerPeer;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrentStats;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Average;
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
/*     */ public class TRHostTorrentHostImpl
/*     */   implements TRHostTorrent
/*     */ {
/*     */   private final TRHostImpl host;
/*     */   private final TRTrackerServer server;
/*     */   private TRTrackerServerTorrent server_torrent;
/*     */   private TOTorrent torrent;
/*     */   private long date_added;
/*     */   private final int port;
/*  48 */   private List listeners_cow = new ArrayList();
/*  49 */   private final List removal_listeners = new ArrayList();
/*     */   
/*  51 */   private int status = 1;
/*     */   
/*     */   private boolean persistent;
/*     */   
/*     */   private boolean passive;
/*     */   
/*     */   private long sos_uploaded;
/*     */   
/*     */   private long sos_downloaded;
/*     */   
/*     */   private long sos_bytes_in;
/*     */   private long sos_bytes_out;
/*     */   private long sos_announce;
/*     */   private long sos_scrape;
/*     */   private long sos_complete;
/*     */   private long last_uploaded;
/*     */   private long last_downloaded;
/*     */   private long last_bytes_in;
/*     */   private long last_bytes_out;
/*     */   private long last_announce;
/*     */   private long last_scrape;
/*  72 */   private final Average average_uploaded = Average.getInstance(60000, 600);
/*  73 */   private final Average average_downloaded = Average.getInstance(60000, 600);
/*  74 */   private final Average average_bytes_in = Average.getInstance(60000, 600);
/*  75 */   private final Average average_bytes_out = Average.getInstance(60000, 600);
/*  76 */   private final Average average_announce = Average.getInstance(60000, 600);
/*  77 */   private final Average average_scrape = Average.getInstance(60000, 600);
/*     */   
/*     */   private boolean disable_reply_caching;
/*     */   
/*     */   private HashMap data;
/*     */   
/*  83 */   protected final AEMonitor this_mon = new AEMonitor("TRHostTorrentHost");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TRHostTorrentHostImpl(TRHostImpl _host, TRTrackerServer _server, TOTorrent _torrent, int _port, long _date_added)
/*     */   {
/*  93 */     this.host = _host;
/*  94 */     this.server = _server;
/*  95 */     this.torrent = _torrent;
/*  96 */     this.port = _port;
/*  97 */     this.date_added = _date_added;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 103 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start()
/*     */   {
/* 116 */     this.host.startTorrent(this);
/*     */   }
/*     */   
/*     */   protected void startSupport()
/*     */   {
/*     */     try
/*     */     {
/* 123 */       this.this_mon.enter();
/*     */       
/*     */ 
/*     */ 
/* 127 */       this.status = 2;
/*     */       
/* 129 */       this.server_torrent = this.server.permit("", this.torrent.getHash(), true);
/*     */       
/* 131 */       if (this.disable_reply_caching)
/*     */       {
/* 133 */         this.server_torrent.disableCaching();
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 138 */       Debug.printStackTrace(e);
/*     */     }
/*     */     finally
/*     */     {
/* 142 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 145 */     this.host.hostTorrentStateChange(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void stop()
/*     */   {
/* 151 */     this.host.stopTorrent(this);
/*     */   }
/*     */   
/*     */   protected void stopSupport()
/*     */   {
/*     */     try
/*     */     {
/* 158 */       this.this_mon.enter();
/*     */       
/*     */ 
/*     */ 
/* 162 */       this.status = 1;
/*     */       
/* 164 */       this.server.deny(this.torrent.getHash(), true);
/*     */       
/* 166 */       TRTrackerServerTorrent st = this.server_torrent;
/*     */       
/* 168 */       TRTrackerServerTorrentStats torrent_stats = st == null ? null : st.getStats();
/*     */       
/* 170 */       if (torrent_stats != null)
/*     */       {
/* 172 */         this.sos_uploaded += torrent_stats.getUploaded();
/* 173 */         this.sos_downloaded += torrent_stats.getDownloaded();
/* 174 */         this.sos_bytes_in += torrent_stats.getBytesIn();
/* 175 */         this.sos_bytes_out += torrent_stats.getBytesOut();
/* 176 */         this.sos_announce += torrent_stats.getAnnounceCount();
/* 177 */         this.sos_scrape += torrent_stats.getScrapeCount();
/* 178 */         this.sos_complete += torrent_stats.getCompletedCount();
/*     */         
/* 180 */         torrent_stats = null;
/*     */       }
/*     */       
/* 183 */       this.last_uploaded = 0L;
/* 184 */       this.last_downloaded = 0L;
/* 185 */       this.last_bytes_in = 0L;
/* 186 */       this.last_bytes_out = 0L;
/* 187 */       this.last_announce = 0L;
/* 188 */       this.last_scrape = 0L;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 192 */       Debug.printStackTrace(e);
/*     */     }
/*     */     finally
/*     */     {
/* 196 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 199 */     this.host.hostTorrentStateChange(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove()
/*     */     throws TRHostTorrentRemovalVetoException
/*     */   {
/* 207 */     canBeRemoved();
/*     */     
/* 209 */     stop();
/*     */     
/* 211 */     this.host.remove(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canBeRemoved()
/*     */     throws TRHostTorrentRemovalVetoException
/*     */   {
/*     */     ArrayList listeners_copy;
/*     */     
/*     */     try
/*     */     {
/* 222 */       this.this_mon.enter();
/*     */       
/* 224 */       listeners_copy = new ArrayList(this.removal_listeners);
/*     */     }
/*     */     finally
/*     */     {
/* 228 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 231 */     for (int i = 0; i < listeners_copy.size(); i++)
/*     */     {
/* 233 */       ((TRHostTorrentWillBeRemovedListener)listeners_copy.get(i)).torrentWillBeRemoved(this);
/*     */     }
/*     */     
/* 236 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStatus()
/*     */   {
/* 242 */     return this.status;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPersistent()
/*     */   {
/* 248 */     return this.persistent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setPersistent(boolean _persistent)
/*     */   {
/* 255 */     this.persistent = _persistent;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPassive()
/*     */   {
/* 261 */     return this.passive;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPassive(boolean b)
/*     */   {
/* 268 */     this.passive = b;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDateAdded()
/*     */   {
/* 274 */     return this.date_added;
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrent getTorrent()
/*     */   {
/* 280 */     return this.torrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTorrent(TOTorrent _torrent)
/*     */   {
/* 287 */     if (_torrent != this.torrent)
/*     */     {
/* 289 */       this.torrent = _torrent;
/*     */       
/* 291 */       if (this.torrent != null)
/*     */       {
/* 293 */         this.passive = false;
/*     */       }
/*     */       
/* 296 */       this.host.torrentUpdated(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setTorrentInternal(TOTorrent _torrent)
/*     */   {
/* 304 */     this.torrent = _torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public TRTrackerServerTorrent getTrackerTorrent()
/*     */   {
/* 310 */     return this.server_torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public TRHostPeer[] getPeers()
/*     */   {
/*     */     try
/*     */     {
/* 318 */       TRTrackerServerPeer[] peers = this.server.getPeers(this.torrent.getHash());
/*     */       
/* 320 */       if (peers != null)
/*     */       {
/* 322 */         TRHostPeer[] res = new TRHostPeer[peers.length];
/*     */         
/* 324 */         for (int i = 0; i < peers.length; i++)
/*     */         {
/* 326 */           res[i] = new TRHostPeerHostImpl(peers[i]);
/*     */         }
/*     */         
/* 329 */         return res;
/*     */       }
/*     */     }
/*     */     catch (TOTorrentException e) {
/* 333 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 336 */     return new TRHostPeer[0];
/*     */   }
/*     */   
/*     */ 
/*     */   protected TRTrackerServerTorrentStats getStats()
/*     */   {
/* 342 */     TRTrackerServerTorrent st = this.server_torrent;
/*     */     
/* 344 */     if (st != null)
/*     */     {
/* 346 */       return st.getStats();
/*     */     }
/*     */     
/* 349 */     return null;
/*     */   }
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
/*     */   protected void setStartOfDayValues(long _date_added, long completed, long announces, long scrapes, long uploaded, long downloaded, long bytes_in, long bytes_out)
/*     */   {
/* 363 */     this.date_added = _date_added;
/* 364 */     this.sos_complete = completed;
/* 365 */     this.sos_announce = announces;
/* 366 */     this.sos_scrape = scrapes;
/* 367 */     this.sos_uploaded = uploaded;
/* 368 */     this.sos_downloaded = downloaded;
/* 369 */     this.sos_bytes_in = bytes_in;
/* 370 */     this.sos_bytes_out = bytes_out;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeedCount()
/*     */   {
/* 376 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 378 */     if (stats != null)
/*     */     {
/* 380 */       return stats.getSeedCount();
/*     */     }
/*     */     
/* 383 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLeecherCount()
/*     */   {
/* 389 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 391 */     if (stats != null)
/*     */     {
/* 393 */       return stats.getLeecherCount();
/*     */     }
/*     */     
/* 396 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getBadNATCount()
/*     */   {
/* 402 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 404 */     if (stats != null)
/*     */     {
/* 406 */       return stats.getBadNATPeerCount();
/*     */     }
/*     */     
/* 409 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void updateStats()
/*     */   {
/* 415 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 417 */     if (stats != null)
/*     */     {
/* 419 */       long current_uploaded = stats.getUploaded();
/*     */       
/* 421 */       long ul_diff = current_uploaded - this.last_uploaded;
/*     */       
/* 423 */       if (ul_diff < 0L)
/*     */       {
/* 425 */         ul_diff = 0L;
/*     */       }
/*     */       
/* 428 */       this.average_uploaded.addValue(ul_diff);
/*     */       
/* 430 */       this.last_uploaded = current_uploaded;
/*     */       
/*     */ 
/*     */ 
/* 434 */       long current_downloaded = stats.getDownloaded();
/*     */       
/* 436 */       long dl_diff = current_downloaded - this.last_downloaded;
/*     */       
/* 438 */       if (dl_diff < 0L)
/*     */       {
/* 440 */         dl_diff = 0L;
/*     */       }
/*     */       
/* 443 */       this.average_downloaded.addValue(dl_diff);
/*     */       
/* 445 */       this.last_downloaded = current_downloaded;
/*     */       
/*     */ 
/*     */ 
/* 449 */       long current_bytes_in = stats.getBytesIn();
/*     */       
/* 451 */       long bi_diff = current_bytes_in - this.last_bytes_in;
/*     */       
/* 453 */       if (bi_diff < 0L)
/*     */       {
/* 455 */         bi_diff = 0L;
/*     */       }
/*     */       
/* 458 */       this.average_bytes_in.addValue(bi_diff);
/*     */       
/* 460 */       this.last_bytes_in = current_bytes_in;
/*     */       
/*     */ 
/*     */ 
/* 464 */       long current_bytes_out = stats.getBytesOut();
/*     */       
/* 466 */       long bo_diff = current_bytes_out - this.last_bytes_out;
/*     */       
/* 468 */       if (bo_diff < 0L)
/*     */       {
/* 470 */         bo_diff = 0L;
/*     */       }
/*     */       
/* 473 */       this.average_bytes_out.addValue(bo_diff);
/*     */       
/* 475 */       this.last_bytes_out = current_bytes_out;
/*     */       
/*     */ 
/*     */ 
/* 479 */       long current_announce = stats.getAnnounceCount();
/*     */       
/* 481 */       long an_diff = current_announce - this.last_announce;
/*     */       
/* 483 */       if (an_diff < 0L)
/*     */       {
/* 485 */         an_diff = 0L;
/*     */       }
/*     */       
/* 488 */       this.average_announce.addValue(an_diff);
/*     */       
/* 490 */       this.last_announce = current_announce;
/*     */       
/*     */ 
/*     */ 
/* 494 */       long current_scrape = stats.getScrapeCount();
/*     */       
/* 496 */       long sc_diff = current_scrape - this.last_scrape;
/*     */       
/* 498 */       if (sc_diff < 0L)
/*     */       {
/* 500 */         sc_diff = 0L;
/*     */       }
/*     */       
/* 503 */       this.average_scrape.addValue(sc_diff);
/*     */       
/* 505 */       this.last_scrape = current_scrape;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected TRTrackerServer getServer()
/*     */   {
/* 512 */     return this.server;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalUploaded()
/*     */   {
/* 518 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 520 */     if (stats != null)
/*     */     {
/* 522 */       return this.sos_uploaded + stats.getUploaded();
/*     */     }
/*     */     
/* 525 */     return this.sos_uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalDownloaded()
/*     */   {
/* 531 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 533 */     if (stats != null)
/*     */     {
/* 535 */       return this.sos_downloaded + stats.getDownloaded();
/*     */     }
/*     */     
/* 538 */     return this.sos_downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalLeft()
/*     */   {
/* 544 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 546 */     if (stats != null)
/*     */     {
/* 548 */       return stats.getAmountLeft();
/*     */     }
/*     */     
/* 551 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytesIn()
/*     */   {
/* 557 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 559 */     if (stats != null)
/*     */     {
/* 561 */       return this.sos_bytes_in + stats.getBytesIn();
/*     */     }
/*     */     
/* 564 */     return this.sos_bytes_in;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytesOut()
/*     */   {
/* 570 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 572 */     if (stats != null)
/*     */     {
/* 574 */       return this.sos_bytes_out + stats.getBytesOut();
/*     */     }
/*     */     
/* 577 */     return this.sos_bytes_out;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAnnounceCount()
/*     */   {
/* 583 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 585 */     if (stats != null)
/*     */     {
/* 587 */       return this.sos_announce + stats.getAnnounceCount();
/*     */     }
/*     */     
/* 590 */     return this.sos_announce;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getScrapeCount()
/*     */   {
/* 596 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 598 */     if (stats != null)
/*     */     {
/* 600 */       return this.sos_scrape + stats.getScrapeCount();
/*     */     }
/*     */     
/* 603 */     return this.sos_scrape;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCompletedCount()
/*     */   {
/* 609 */     TRTrackerServerTorrentStats stats = getStats();
/*     */     
/* 611 */     if (stats != null)
/*     */     {
/* 613 */       return this.sos_complete + stats.getCompletedCount();
/*     */     }
/*     */     
/* 616 */     return this.sos_complete;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getAverageBytesIn()
/*     */   {
/* 624 */     return this.average_bytes_in.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesOut()
/*     */   {
/* 630 */     return this.average_bytes_out.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageUploaded()
/*     */   {
/* 636 */     return this.average_uploaded.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageDownloaded()
/*     */   {
/* 642 */     return this.average_downloaded.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageAnnounceCount()
/*     */   {
/* 648 */     return this.average_announce.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageScrapeCount()
/*     */   {
/* 654 */     return this.average_scrape.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void disableReplyCaching()
/*     */   {
/* 661 */     TRTrackerServerTorrent st = this.server_torrent;
/*     */     
/* 663 */     this.disable_reply_caching = true;
/*     */     
/* 665 */     if (st != null)
/*     */     {
/* 667 */       st.disableCaching();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void preProcess(TRHostTorrentRequest req)
/*     */     throws TRHostException
/*     */   {
/* 677 */     List listeners_ref = this.listeners_cow;
/*     */     
/* 679 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*     */       try
/*     */       {
/* 682 */         ((TRHostTorrentListener)listeners_ref.get(i)).preProcess(req);
/*     */       }
/*     */       catch (TRHostException e)
/*     */       {
/* 686 */         throw e;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 690 */         Debug.printStackTrace(e);
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
/* 701 */     List listeners_ref = this.listeners_cow;
/*     */     
/* 703 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*     */       try
/*     */       {
/* 706 */         ((TRHostTorrentListener)listeners_ref.get(i)).postProcess(req);
/*     */       }
/*     */       catch (TRHostException e)
/*     */       {
/* 710 */         throw e;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 714 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addListener(TRHostTorrentListener l)
/*     */   {
/*     */     try
/*     */     {
/* 724 */       this.this_mon.enter();
/*     */       
/* 726 */       List new_listeners = new ArrayList(this.listeners_cow);
/*     */       
/* 728 */       new_listeners.add(l);
/*     */       
/* 730 */       this.listeners_cow = new_listeners;
/*     */     }
/*     */     finally
/*     */     {
/* 734 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 737 */     this.host.torrentListenerRegistered();
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeListener(TRHostTorrentListener l)
/*     */   {
/*     */     try
/*     */     {
/* 745 */       this.this_mon.enter();
/*     */       
/* 747 */       List new_listeners = new ArrayList(this.listeners_cow);
/*     */       
/* 749 */       new_listeners.remove(l);
/*     */       
/* 751 */       this.listeners_cow = new_listeners;
/*     */     }
/*     */     finally
/*     */     {
/* 755 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addRemovalListener(TRHostTorrentWillBeRemovedListener l)
/*     */   {
/*     */     try
/*     */     {
/* 764 */       this.this_mon.enter();
/*     */       
/* 766 */       this.removal_listeners.add(l);
/*     */     }
/*     */     finally
/*     */     {
/* 770 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeRemovalListener(TRHostTorrentWillBeRemovedListener l)
/*     */   {
/*     */     try
/*     */     {
/* 779 */       this.this_mon.enter();
/*     */       
/* 781 */       this.removal_listeners.remove(l);
/*     */     }
/*     */     finally {
/* 784 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getData(String key)
/*     */   {
/* 790 */     if (this.data == null) return null;
/* 791 */     return this.data.get(key);
/*     */   }
/*     */   
/*     */   public void setData(String key, Object value)
/*     */   {
/*     */     try {
/* 797 */       this.this_mon.enter();
/*     */       
/* 799 */       if (this.data == null) {
/* 800 */         this.data = new HashMap();
/*     */       }
/* 802 */       if (value == null) {
/* 803 */         if (this.data.containsKey(key))
/* 804 */           this.data.remove(key);
/*     */       } else {
/* 806 */         this.data.put(key, value);
/*     */       }
/*     */     }
/*     */     finally {
/* 810 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/impl/TRHostTorrentHostImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */