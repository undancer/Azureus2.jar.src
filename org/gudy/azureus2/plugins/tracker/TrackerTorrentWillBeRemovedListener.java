package org.gudy.azureus2.plugins.tracker;

public abstract interface TrackerTorrentWillBeRemovedListener
{
  public abstract void torrentWillBeRemoved(TrackerTorrent paramTrackerTorrent)
    throws TrackerTorrentRemovalVetoException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/TrackerTorrentWillBeRemovedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */