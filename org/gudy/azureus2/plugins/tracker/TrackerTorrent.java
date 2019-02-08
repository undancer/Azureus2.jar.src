package org.gudy.azureus2.plugins.tracker;

import org.gudy.azureus2.plugins.torrent.Torrent;

public abstract interface TrackerTorrent
{
  public static final int TS_STARTED = 0;
  public static final int TS_STOPPED = 1;
  public static final int TS_PUBLISHED = 2;
  
  public abstract void start()
    throws TrackerException;
  
  public abstract void stop()
    throws TrackerException;
  
  public abstract void remove()
    throws TrackerTorrentRemovalVetoException;
  
  public abstract boolean canBeRemoved()
    throws TrackerTorrentRemovalVetoException;
  
  public abstract Torrent getTorrent();
  
  public abstract TrackerPeer[] getPeers();
  
  public abstract int getStatus();
  
  public abstract int getSeedCount();
  
  public abstract int getLeecherCount();
  
  public abstract int getBadNATCount();
  
  public abstract long getTotalUploaded();
  
  public abstract long getTotalDownloaded();
  
  public abstract long getAverageUploaded();
  
  public abstract long getAverageDownloaded();
  
  public abstract long getTotalLeft();
  
  public abstract long getCompletedCount();
  
  public abstract long getTotalBytesIn();
  
  public abstract long getAverageBytesIn();
  
  public abstract long getTotalBytesOut();
  
  public abstract long getAverageBytesOut();
  
  public abstract long getScrapeCount();
  
  public abstract long getAverageScrapeCount();
  
  public abstract long getAnnounceCount();
  
  public abstract long getAverageAnnounceCount();
  
  public abstract void disableReplyCaching();
  
  public abstract boolean isPassive();
  
  public abstract long getDateAdded();
  
  public abstract void addListener(TrackerTorrentListener paramTrackerTorrentListener);
  
  public abstract void removeListener(TrackerTorrentListener paramTrackerTorrentListener);
  
  public abstract void addRemovalListener(TrackerTorrentWillBeRemovedListener paramTrackerTorrentWillBeRemovedListener);
  
  public abstract void removeRemovalListener(TrackerTorrentWillBeRemovedListener paramTrackerTorrentWillBeRemovedListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/TrackerTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */