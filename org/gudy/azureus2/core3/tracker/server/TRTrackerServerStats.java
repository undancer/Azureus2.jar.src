package org.gudy.azureus2.core3.tracker.server;

public abstract interface TRTrackerServerStats
{
  public abstract int getTorrentCount();
  
  public abstract long getBytesIn();
  
  public abstract long getBytesOut();
  
  public abstract long getAnnounceCount();
  
  public abstract long getScrapeCount();
  
  public abstract long getAnnounceTime();
  
  public abstract long getScrapeTime();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */