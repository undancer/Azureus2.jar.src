/*     */ package org.gudy.azureus2.pluginsimpl.remote.tracker;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerException;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerPeer;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrentListener;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrentRemovalVetoException;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrentWillBeRemovedListener;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequestDispatcher;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.torrent.RPTorrent;
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
/*     */ public class RPTrackerTorrent
/*     */   extends RPObject
/*     */   implements TrackerTorrent
/*     */ {
/*     */   protected transient TrackerTorrent delegate;
/*     */   public RPTorrent torrent;
/*     */   public int status;
/*     */   public long total_uploaded;
/*     */   public long total_downloaded;
/*     */   public long average_uploaded;
/*     */   public long average_downloaded;
/*     */   public long total_left;
/*     */   public long completed_count;
/*     */   public long total_bytes_in;
/*     */   public long average_bytes_in;
/*     */   public long total_bytes_out;
/*     */   public long average_bytes_out;
/*     */   public long scrape_count;
/*     */   public long average_scrape_count;
/*     */   public long announce_count;
/*     */   public long average_announce_count;
/*     */   public int seed_count;
/*     */   public int leecher_count;
/*     */   public int bad_NAT_count;
/*     */   
/*     */   public static RPTrackerTorrent create(TrackerTorrent _delegate)
/*     */   {
/*  68 */     RPTrackerTorrent res = (RPTrackerTorrent)_lookupLocal(_delegate);
/*     */     
/*  70 */     if (res == null)
/*     */     {
/*  72 */       res = new RPTrackerTorrent(_delegate);
/*     */     }
/*     */     
/*  75 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPTrackerTorrent(TrackerTorrent _delegate)
/*     */   {
/*  82 */     super(_delegate);
/*     */     
/*  84 */     if (this.delegate.getTorrent() != null)
/*     */     {
/*  86 */       this.torrent = ((RPTorrent)_lookupLocal(this.delegate.getTorrent()));
/*     */       
/*  88 */       if (this.torrent == null)
/*     */       {
/*  90 */         this.torrent = RPTorrent.create(this.delegate.getTorrent());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  99 */     this.delegate = ((TrackerTorrent)_delegate);
/*     */     
/* 101 */     this.status = this.delegate.getStatus();
/* 102 */     this.total_uploaded = this.delegate.getTotalUploaded();
/* 103 */     this.total_downloaded = this.delegate.getTotalDownloaded();
/* 104 */     this.average_uploaded = this.delegate.getAverageUploaded();
/* 105 */     this.average_downloaded = this.delegate.getAverageDownloaded();
/* 106 */     this.total_left = this.delegate.getTotalLeft();
/* 107 */     this.completed_count = this.delegate.getCompletedCount();
/* 108 */     this.total_bytes_in = this.delegate.getTotalBytesIn();
/* 109 */     this.average_bytes_in = this.delegate.getAverageBytesIn();
/* 110 */     this.total_bytes_out = this.delegate.getTotalBytesOut();
/* 111 */     this.average_bytes_out = this.delegate.getAverageBytesOut();
/* 112 */     this.scrape_count = this.delegate.getScrapeCount();
/* 113 */     this.average_scrape_count = this.delegate.getAverageScrapeCount();
/* 114 */     this.announce_count = this.delegate.getAnnounceCount();
/* 115 */     this.average_announce_count = this.delegate.getAverageAnnounceCount();
/* 116 */     this.seed_count = this.delegate.getSeedCount();
/* 117 */     this.leecher_count = this.delegate.getLeecherCount();
/* 118 */     this.bad_NAT_count = this.delegate.getBadNATCount();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/* 126 */     Object res = _fixupLocal();
/*     */     
/* 128 */     if (this.torrent != null)
/*     */     {
/* 130 */       this.torrent._setLocal();
/*     */     }
/*     */     
/* 133 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void _setRemote(RPRequestDispatcher dispatcher)
/*     */   {
/* 140 */     super._setRemote(dispatcher);
/*     */     
/* 142 */     if (this.torrent != null)
/*     */     {
/* 144 */       this.torrent._setRemote(dispatcher);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/* 152 */     String method = request.getMethod();
/*     */     
/*     */ 
/* 155 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start()
/*     */     throws TrackerException
/*     */   {
/* 165 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void stop()
/*     */     throws TrackerException
/*     */   {
/* 173 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove()
/*     */     throws TrackerTorrentRemovalVetoException
/*     */   {
/* 181 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean canBeRemoved()
/*     */     throws TrackerTorrentRemovalVetoException
/*     */   {
/* 190 */     notSupported();
/*     */     
/* 192 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Torrent getTorrent()
/*     */   {
/* 199 */     return this.torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public TrackerPeer[] getPeers()
/*     */   {
/* 205 */     notSupported();
/*     */     
/* 207 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStatus()
/*     */   {
/* 213 */     return this.status;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalUploaded()
/*     */   {
/* 219 */     return this.total_uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalDownloaded()
/*     */   {
/* 225 */     return this.total_downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageUploaded()
/*     */   {
/* 231 */     return this.average_uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageDownloaded()
/*     */   {
/* 237 */     return this.average_downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalLeft()
/*     */   {
/* 243 */     return this.total_left;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCompletedCount()
/*     */   {
/* 249 */     return this.completed_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytesIn()
/*     */   {
/* 255 */     return this.total_bytes_in;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesIn()
/*     */   {
/* 261 */     return this.average_bytes_in;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytesOut()
/*     */   {
/* 267 */     return this.total_bytes_out;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesOut()
/*     */   {
/* 273 */     return this.average_bytes_out;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getScrapeCount()
/*     */   {
/* 279 */     return this.scrape_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageScrapeCount()
/*     */   {
/* 285 */     return this.average_scrape_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAnnounceCount()
/*     */   {
/* 291 */     return this.announce_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageAnnounceCount()
/*     */   {
/* 297 */     return this.average_announce_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeedCount()
/*     */   {
/* 303 */     return this.seed_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLeecherCount()
/*     */   {
/* 309 */     return this.leecher_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getBadNATCount()
/*     */   {
/* 315 */     return this.bad_NAT_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public void disableReplyCaching()
/*     */   {
/* 321 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPassive()
/*     */   {
/* 327 */     notSupported();
/*     */     
/* 329 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDateAdded()
/*     */   {
/* 335 */     notSupported();
/*     */     
/* 337 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TrackerTorrentListener listener)
/*     */   {
/* 344 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(TrackerTorrentListener listener)
/*     */   {
/* 351 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addRemovalListener(TrackerTorrentWillBeRemovedListener listener)
/*     */   {
/* 358 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeRemovalListener(TrackerTorrentWillBeRemovedListener listener)
/*     */   {
/* 366 */     notSupported();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/tracker/RPTrackerTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */