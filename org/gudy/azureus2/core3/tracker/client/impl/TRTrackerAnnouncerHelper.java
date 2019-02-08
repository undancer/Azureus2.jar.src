package org.gudy.azureus2.core3.tracker.client.impl;

import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;

public abstract interface TRTrackerAnnouncerHelper
  extends TRTrackerAnnouncer
{
  public abstract TOTorrentAnnounceURLSet[] getAnnounceSets();
  
  public abstract boolean isUpdating();
  
  public abstract long getInterval();
  
  public abstract long getMinInterval();
  
  public abstract int getTimeUntilNextUpdate();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/TRTrackerAnnouncerHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */