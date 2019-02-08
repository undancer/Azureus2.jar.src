package org.gudy.azureus2.core3.tracker.server;

public abstract interface TRTrackerServerPeerBase
{
  public abstract String getIP();
  
  public abstract int getTCPPort();
  
  public abstract int getHTTPPort();
  
  public abstract int getSecsToLive();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerPeerBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */