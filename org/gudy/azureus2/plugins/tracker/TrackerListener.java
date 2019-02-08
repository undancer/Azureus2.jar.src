package org.gudy.azureus2.plugins.tracker;

public abstract interface TrackerListener
{
  public abstract void torrentAdded(TrackerTorrent paramTrackerTorrent);
  
  public abstract void torrentChanged(TrackerTorrent paramTrackerTorrent);
  
  public abstract void torrentRemoved(TrackerTorrent paramTrackerTorrent);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/TrackerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */