package org.gudy.azureus2.core3.tracker.client;

import java.net.URL;
import java.util.Map;
import org.gudy.azureus2.core3.util.HashWrapper;

public abstract interface TRTrackerAnnouncerResponse
{
  public static final int ST_OFFLINE = 0;
  public static final int ST_REPORTED_ERROR = 1;
  public static final int ST_ONLINE = 2;
  
  public abstract int getStatus();
  
  public abstract String getStatusString();
  
  public abstract HashWrapper getHash();
  
  public abstract long getTimeToWait();
  
  public abstract String getAdditionalInfo();
  
  public abstract TRTrackerAnnouncerResponsePeer[] getPeers();
  
  public abstract void setPeers(TRTrackerAnnouncerResponsePeer[] paramArrayOfTRTrackerAnnouncerResponsePeer);
  
  public abstract Map getExtensions();
  
  public abstract URL getURL();
  
  public abstract int getScrapeCompleteCount();
  
  public abstract int getScrapeIncompleteCount();
  
  public abstract int getScrapeDownloadedCount();
  
  public abstract void print();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/TRTrackerAnnouncerResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */