package org.gudy.azureus2.core3.tracker.server;

public abstract interface TRTrackerServerRequestListener
{
  public abstract void preProcess(TRTrackerServerRequest paramTRTrackerServerRequest)
    throws TRTrackerServerException;
  
  public abstract void postProcess(TRTrackerServerRequest paramTRTrackerServerRequest)
    throws TRTrackerServerException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerRequestListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */