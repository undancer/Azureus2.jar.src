package org.gudy.azureus2.plugins.tracker;

public abstract interface TrackerTorrentListener
{
  public abstract void preProcess(TrackerTorrentRequest paramTrackerTorrentRequest)
    throws TrackerException;
  
  public abstract void postProcess(TrackerTorrentRequest paramTrackerTorrentRequest)
    throws TrackerException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/TrackerTorrentListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */