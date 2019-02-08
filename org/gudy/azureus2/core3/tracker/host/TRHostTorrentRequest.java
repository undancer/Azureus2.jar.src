package org.gudy.azureus2.core3.tracker.host;

import java.util.Map;

public abstract interface TRHostTorrentRequest
{
  public static final int RT_ANNOUNCE = 1;
  public static final int RT_SCRAPE = 2;
  public static final int RT_FULL_SCRAPE = 3;
  
  public abstract TRHostPeer getPeer();
  
  public abstract TRHostTorrent getTorrent();
  
  public abstract int getRequestType();
  
  public abstract String getRequest();
  
  public abstract Map getResponse();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHostTorrentRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */