package org.gudy.azureus2.core3.tracker.host;

public abstract interface TRHostTorrentWillBeRemovedListener
{
  public abstract void torrentWillBeRemoved(TRHostTorrent paramTRHostTorrent)
    throws TRHostTorrentRemovalVetoException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHostTorrentWillBeRemovedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */