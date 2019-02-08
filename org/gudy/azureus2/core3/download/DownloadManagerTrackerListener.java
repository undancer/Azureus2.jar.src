package org.gudy.azureus2.core3.download;

import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;

public abstract interface DownloadManagerTrackerListener
{
  public abstract void scrapeResult(TRTrackerScraperResponse paramTRTrackerScraperResponse);
  
  public abstract void announceResult(TRTrackerAnnouncerResponse paramTRTrackerAnnouncerResponse);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerTrackerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */