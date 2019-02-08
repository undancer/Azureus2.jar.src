/*     */ package org.gudy.azureus2.pluginsimpl.local.tracker;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostException;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostPeer;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentListener;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentRemovalVetoException;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentRequest;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentWillBeRemovedListener;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerException;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerPeer;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrentListener;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrentRemovalVetoException;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrentWillBeRemovedListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
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
/*     */ public class TrackerTorrentImpl
/*     */   implements TrackerTorrent, TRHostTorrentListener, TRHostTorrentWillBeRemovedListener
/*     */ {
/*     */   protected TRHostTorrent host_torrent;
/*  44 */   protected List listeners_cow = new ArrayList();
/*  45 */   protected List removal_listeners = new ArrayList();
/*     */   
/*  47 */   protected AEMonitor this_mon = new AEMonitor("TrackerTorrent");
/*     */   
/*     */ 
/*     */ 
/*     */   public TrackerTorrentImpl(TRHostTorrent _host_torrent)
/*     */   {
/*  53 */     this.host_torrent = _host_torrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TRHostTorrent getHostTorrent()
/*     */   {
/*  60 */     return this.host_torrent;
/*     */   }
/*     */   
/*     */ 
/*     */   public void start()
/*     */     throws TrackerException
/*     */   {
/*     */     try
/*     */     {
/*  69 */       this.host_torrent.start();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  73 */       throw new TrackerException("Start failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void stop()
/*     */     throws TrackerException
/*     */   {
/*     */     try
/*     */     {
/*  83 */       this.host_torrent.stop();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  87 */       throw new TrackerException("Stop failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void remove()
/*     */     throws TrackerTorrentRemovalVetoException
/*     */   {
/*     */     try
/*     */     {
/*  97 */       this.host_torrent.remove();
/*     */     }
/*     */     catch (TRHostTorrentRemovalVetoException e)
/*     */     {
/* 101 */       throw new TrackerTorrentRemovalVetoException(e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canBeRemoved()
/*     */     throws TrackerTorrentRemovalVetoException
/*     */   {
/*     */     try
/*     */     {
/* 111 */       this.host_torrent.canBeRemoved();
/*     */     }
/*     */     catch (TRHostTorrentRemovalVetoException e)
/*     */     {
/* 115 */       throw new TrackerTorrentRemovalVetoException(e.getMessage());
/*     */     }
/*     */     
/* 118 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public Torrent getTorrent()
/*     */   {
/* 124 */     return new TorrentImpl(this.host_torrent.getTorrent());
/*     */   }
/*     */   
/*     */ 
/*     */   public TrackerPeer[] getPeers()
/*     */   {
/* 130 */     TRHostPeer[] peers = this.host_torrent.getPeers();
/*     */     
/* 132 */     TrackerPeer[] res = new TrackerPeer[peers.length];
/*     */     
/* 134 */     for (int i = 0; i < peers.length; i++)
/*     */     {
/* 136 */       res[i] = new TrackerPeerImpl(peers[i]);
/*     */     }
/*     */     
/* 139 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStatus()
/*     */   {
/* 145 */     int status = this.host_torrent.getStatus();
/*     */     
/* 147 */     switch (status) {
/*     */     case 2: 
/* 149 */       return 0;
/*     */     case 1: 
/* 151 */       return 1;
/*     */     case 3: 
/* 153 */       return 2;
/*     */     }
/* 155 */     throw new RuntimeException("TrackerTorrent: status invalid");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getTotalUploaded()
/*     */   {
/* 162 */     return this.host_torrent.getTotalUploaded();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalDownloaded()
/*     */   {
/* 168 */     return this.host_torrent.getTotalDownloaded();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageUploaded()
/*     */   {
/* 174 */     return this.host_torrent.getAverageUploaded();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageDownloaded()
/*     */   {
/* 180 */     return this.host_torrent.getAverageDownloaded();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalLeft()
/*     */   {
/* 186 */     return this.host_torrent.getTotalLeft();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCompletedCount()
/*     */   {
/* 192 */     return this.host_torrent.getCompletedCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytesIn()
/*     */   {
/* 198 */     return this.host_torrent.getTotalBytesIn();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesIn()
/*     */   {
/* 204 */     return this.host_torrent.getAverageBytesIn();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytesOut()
/*     */   {
/* 210 */     return this.host_torrent.getTotalBytesOut();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesOut()
/*     */   {
/* 216 */     return this.host_torrent.getAverageBytesOut();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageScrapeCount()
/*     */   {
/* 222 */     return this.host_torrent.getAverageScrapeCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getScrapeCount()
/*     */   {
/* 228 */     return this.host_torrent.getScrapeCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageAnnounceCount()
/*     */   {
/* 234 */     return this.host_torrent.getAverageAnnounceCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAnnounceCount()
/*     */   {
/* 240 */     return this.host_torrent.getAnnounceCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeedCount()
/*     */   {
/* 246 */     return this.host_torrent.getSeedCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLeecherCount()
/*     */   {
/* 252 */     return this.host_torrent.getLeecherCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getBadNATCount()
/*     */   {
/* 258 */     return this.host_torrent.getBadNATCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public void disableReplyCaching()
/*     */   {
/* 264 */     this.host_torrent.disableReplyCaching();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPassive()
/*     */   {
/* 270 */     return this.host_torrent.isPassive();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDateAdded()
/*     */   {
/* 276 */     return this.host_torrent.getDateAdded();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void preProcess(TRHostTorrentRequest request)
/*     */     throws TRHostException
/*     */   {
/* 285 */     List listeners_ref = this.listeners_cow;
/*     */     
/* 287 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*     */       try
/*     */       {
/* 290 */         ((TrackerTorrentListener)listeners_ref.get(i)).preProcess(new TrackerTorrentRequestImpl(request));
/*     */       }
/*     */       catch (TrackerException e)
/*     */       {
/* 294 */         throw new TRHostException(e.getMessage(), e);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 298 */         throw new TRHostException("Pre-process fails", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void postProcess(TRHostTorrentRequest request)
/*     */     throws TRHostException
/*     */   {
/* 309 */     List listeners_ref = this.listeners_cow;
/*     */     
/* 311 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*     */       try
/*     */       {
/* 314 */         ((TrackerTorrentListener)listeners_ref.get(i)).postProcess(new TrackerTorrentRequestImpl(request));
/*     */       }
/*     */       catch (TrackerException e)
/*     */       {
/* 318 */         throw new TRHostException(e.getMessage(), e);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 322 */         throw new TRHostException("Post-process fails", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addListener(TrackerTorrentListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 332 */       this.this_mon.enter();
/*     */       
/* 334 */       List new_listeners = new ArrayList(this.listeners_cow);
/*     */       
/* 336 */       new_listeners.add(listener);
/*     */       
/* 338 */       if (new_listeners.size() == 1)
/*     */       {
/* 340 */         this.host_torrent.addListener(this);
/*     */       }
/*     */       
/* 343 */       this.listeners_cow = new_listeners;
/*     */     }
/*     */     finally
/*     */     {
/* 347 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeListener(TrackerTorrentListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 356 */       this.this_mon.enter();
/*     */       
/* 358 */       List new_listeners = new ArrayList(this.listeners_cow);
/*     */       
/* 360 */       new_listeners.remove(listener);
/*     */       
/* 362 */       if (new_listeners.size() == 0)
/*     */       {
/* 364 */         this.host_torrent.removeListener(this);
/*     */       }
/*     */       
/* 367 */       this.listeners_cow = new_listeners;
/*     */     }
/*     */     finally
/*     */     {
/* 371 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void torrentWillBeRemoved(TRHostTorrent t)
/*     */     throws TRHostTorrentRemovalVetoException
/*     */   {
/* 381 */     for (int i = 0; i < this.removal_listeners.size(); i++) {
/*     */       try
/*     */       {
/* 384 */         ((TrackerTorrentWillBeRemovedListener)this.removal_listeners.get(i)).torrentWillBeRemoved(this);
/*     */       }
/*     */       catch (TrackerTorrentRemovalVetoException e)
/*     */       {
/* 388 */         throw new TRHostTorrentRemovalVetoException(e.getMessage());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addRemovalListener(TrackerTorrentWillBeRemovedListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 398 */       this.this_mon.enter();
/*     */       
/* 400 */       this.removal_listeners.add(listener);
/*     */       
/* 402 */       if (this.removal_listeners.size() == 1)
/*     */       {
/* 404 */         this.host_torrent.addRemovalListener(this);
/*     */       }
/*     */     }
/*     */     finally {
/* 408 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeRemovalListener(TrackerTorrentWillBeRemovedListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 417 */       this.this_mon.enter();
/*     */       
/* 419 */       this.removal_listeners.remove(listener);
/*     */       
/* 421 */       if (this.removal_listeners.size() == 0)
/*     */       {
/* 423 */         this.host_torrent.removeRemovalListener(this);
/*     */       }
/*     */     }
/*     */     finally {
/* 427 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 438 */     if ((other instanceof TrackerTorrentImpl))
/*     */     {
/* 440 */       return this.host_torrent == ((TrackerTorrentImpl)other).host_torrent;
/*     */     }
/*     */     
/* 443 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 449 */     return this.host_torrent.hashCode();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/tracker/TrackerTorrentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */