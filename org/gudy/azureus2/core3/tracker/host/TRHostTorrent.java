package org.gudy.azureus2.core3.tracker.host;

import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrent;

public abstract interface TRHostTorrent
{
  public static final int TS_FAILED = 0;
  public static final int TS_STOPPED = 1;
  public static final int TS_STARTED = 2;
  public static final int TS_PUBLISHED = 3;
  
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void remove()
    throws TRHostTorrentRemovalVetoException;
  
  public abstract boolean canBeRemoved()
    throws TRHostTorrentRemovalVetoException;
  
  public abstract int getStatus();
  
  public abstract boolean isPersistent();
  
  public abstract boolean isPassive();
  
  public abstract void setPassive(boolean paramBoolean);
  
  public abstract long getDateAdded();
  
  public abstract TOTorrent getTorrent();
  
  public abstract void setTorrent(TOTorrent paramTOTorrent);
  
  public abstract TRTrackerServerTorrent getTrackerTorrent();
  
  public abstract int getPort();
  
  public abstract TRHostPeer[] getPeers();
  
  public abstract int getSeedCount();
  
  public abstract int getLeecherCount();
  
  public abstract int getBadNATCount();
  
  public abstract long getAnnounceCount();
  
  public abstract long getAverageAnnounceCount();
  
  public abstract long getScrapeCount();
  
  public abstract long getAverageScrapeCount();
  
  public abstract long getCompletedCount();
  
  public abstract long getTotalUploaded();
  
  public abstract long getTotalDownloaded();
  
  public abstract long getTotalLeft();
  
  public abstract long getAverageUploaded();
  
  public abstract long getAverageDownloaded();
  
  public abstract long getTotalBytesIn();
  
  public abstract long getAverageBytesIn();
  
  public abstract long getTotalBytesOut();
  
  public abstract long getAverageBytesOut();
  
  public abstract void disableReplyCaching();
  
  public abstract void addListener(TRHostTorrentListener paramTRHostTorrentListener);
  
  public abstract void removeListener(TRHostTorrentListener paramTRHostTorrentListener);
  
  public abstract void addRemovalListener(TRHostTorrentWillBeRemovedListener paramTRHostTorrentWillBeRemovedListener);
  
  public abstract void removeRemovalListener(TRHostTorrentWillBeRemovedListener paramTRHostTorrentWillBeRemovedListener);
  
  public abstract Object getData(String paramString);
  
  public abstract void setData(String paramString, Object paramObject);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHostTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */