package org.gudy.azureus2.plugins.tracker;

import java.util.Map;

public abstract interface TrackerTorrentRequest
{
  public static final int RT_ANNOUNCE = 1;
  public static final int RT_SCRAPE = 2;
  public static final int RT_FULL_SCRAPE = 3;
  
  public abstract int getRequestType();
  
  public abstract TrackerTorrent getTorrent();
  
  public abstract TrackerPeer getPeer();
  
  public abstract String getRequest();
  
  public abstract Map getResponse();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/TrackerTorrentRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */