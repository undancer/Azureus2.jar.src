package org.gudy.azureus2.core3.tracker.server;

public abstract interface TRTrackerServerTorrentStats
{
  public abstract int getSeedCount();
  
  public abstract int getLeecherCount();
  
  public abstract int getQueuedCount();
  
  public abstract long getScrapeCount();
  
  public abstract long getAnnounceCount();
  
  public abstract long getCompletedCount();
  
  public abstract long getUploaded();
  
  public abstract long getDownloaded();
  
  public abstract long getBiasedUploaded();
  
  public abstract long getBiasedDownloaded();
  
  public abstract long getAmountLeft();
  
  public abstract long getBytesIn();
  
  public abstract long getBytesOut();
  
  public abstract int getBadNATPeerCount();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerTorrentStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */