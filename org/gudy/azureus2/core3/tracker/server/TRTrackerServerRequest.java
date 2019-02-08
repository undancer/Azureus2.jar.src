package org.gudy.azureus2.core3.tracker.server;

import java.util.Map;

public abstract interface TRTrackerServerRequest
{
  public static final int RT_UNKNOWN = -1;
  public static final int RT_ANNOUNCE = 1;
  public static final int RT_SCRAPE = 2;
  public static final int RT_FULL_SCRAPE = 3;
  public static final int RT_QUERY = 4;
  
  public abstract int getType();
  
  public abstract TRTrackerServerPeer getPeer();
  
  public abstract TRTrackerServerTorrent getTorrent();
  
  public abstract String getRequest();
  
  public abstract Map getResponse();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */