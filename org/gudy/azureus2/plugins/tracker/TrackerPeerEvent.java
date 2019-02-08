package org.gudy.azureus2.plugins.tracker;

public abstract interface TrackerPeerEvent
{
  public static final int ET_PEER_ADDED = 1;
  public static final int ET_PEER_CHANGED = 2;
  public static final int ET_PEER_REMOVED = 3;
  
  public abstract int getEventType();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/tracker/TrackerPeerEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */