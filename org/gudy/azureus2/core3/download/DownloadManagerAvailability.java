package org.gudy.azureus2.core3.download;

import com.aelitis.azureus.core.tracker.TrackerPeerSource;
import java.util.List;

public abstract interface DownloadManagerAvailability
{
  public abstract List<TrackerPeerSource> getTrackerPeerSources();
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerAvailability.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */