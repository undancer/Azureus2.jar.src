package org.gudy.azureus2.core3.tracker.client;

import java.net.URL;
import org.gudy.azureus2.core3.util.HashWrapper;

public abstract interface TRTrackerScraperResponse
{
  public static final int ST_INITIALIZING = 0;
  public static final int ST_ERROR = 1;
  public static final int ST_ONLINE = 2;
  public static final int ST_SCRAPING = 3;
  
  public abstract HashWrapper getHash();
  
  public abstract int getCompleted();
  
  public abstract void setCompleted(int paramInt);
  
  public abstract int getSeeds();
  
  public abstract int getPeers();
  
  public abstract void setSeedsPeers(int paramInt1, int paramInt2);
  
  public abstract int getStatus();
  
  public abstract int getScrapeTime();
  
  public abstract long getScrapeStartTime();
  
  public abstract void setScrapeStartTime(long paramLong);
  
  public abstract long getNextScrapeStartTime();
  
  public abstract void setNextScrapeStartTime(long paramLong);
  
  public abstract String getStatusString();
  
  public abstract boolean isValid();
  
  public abstract URL getURL();
  
  public abstract boolean isDHTBackup();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/TRTrackerScraperResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */