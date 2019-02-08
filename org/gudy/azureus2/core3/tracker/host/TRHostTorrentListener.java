package org.gudy.azureus2.core3.tracker.host;

public abstract interface TRHostTorrentListener
{
  public abstract void preProcess(TRHostTorrentRequest paramTRHostTorrentRequest)
    throws TRHostException;
  
  public abstract void postProcess(TRHostTorrentRequest paramTRHostTorrentRequest)
    throws TRHostException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHostTorrentListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */