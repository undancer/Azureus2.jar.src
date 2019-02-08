package org.gudy.azureus2.core3.tracker.client;

import java.net.URL;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.plugins.download.DownloadScrapeResult;

public abstract interface TRTrackerScraper
{
  public static final int REFRESH_MINIMUM_SECS = 120;
  
  public abstract TRTrackerScraperResponse scrape(TOTorrent paramTOTorrent);
  
  public abstract TRTrackerScraperResponse scrape(TOTorrent paramTOTorrent, URL paramURL);
  
  public abstract TRTrackerScraperResponse scrape(TOTorrent paramTOTorrent, boolean paramBoolean);
  
  public abstract TRTrackerScraperResponse scrape(TRTrackerAnnouncer paramTRTrackerAnnouncer);
  
  public abstract void setScrape(TOTorrent paramTOTorrent, URL paramURL, DownloadScrapeResult paramDownloadScrapeResult);
  
  public abstract TRTrackerScraperResponse peekScrape(TOTorrent paramTOTorrent, URL paramURL);
  
  public abstract void remove(TOTorrent paramTOTorrent);
  
  public abstract void setClientResolver(TRTrackerScraperClientResolver paramTRTrackerScraperClientResolver);
  
  public abstract void addListener(TRTrackerScraperListener paramTRTrackerScraperListener);
  
  public abstract void removeListener(TRTrackerScraperListener paramTRTrackerScraperListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/TRTrackerScraper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */