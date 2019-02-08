package org.gudy.azureus2.core3.tracker.client;

import java.net.URL;

public abstract interface TRTrackerAnnouncerListener
{
  public abstract void receivedTrackerResponse(TRTrackerAnnouncerResponse paramTRTrackerAnnouncerResponse);
  
  public abstract void urlChanged(TRTrackerAnnouncer paramTRTrackerAnnouncer, URL paramURL1, URL paramURL2, boolean paramBoolean);
  
  public abstract void urlRefresh();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/TRTrackerAnnouncerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */