package org.gudy.azureus2.plugins.download;

import java.net.URL;

public abstract interface DownloadScrapeResult
{
  public static final int RT_SUCCESS = 1;
  public static final int RT_ERROR = 2;
  
  public abstract Download getDownload();
  
  public abstract int getResponseType();
  
  public abstract int getSeedCount();
  
  public abstract int getNonSeedCount();
  
  public abstract long getScrapeStartTime();
  
  public abstract void setNextScrapeStartTime(long paramLong);
  
  public abstract long getNextScrapeStartTime();
  
  public abstract String getStatus();
  
  public abstract URL getURL();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadScrapeResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */