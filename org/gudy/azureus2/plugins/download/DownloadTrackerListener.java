package org.gudy.azureus2.plugins.download;

public abstract interface DownloadTrackerListener
{
  public abstract void scrapeResult(DownloadScrapeResult paramDownloadScrapeResult);
  
  public abstract void announceResult(DownloadAnnounceResult paramDownloadAnnounceResult);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadTrackerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */