package org.gudy.azureus2.core3.tracker.server;

import java.util.Map;

public abstract interface TRTrackerServerTorrentPeerListener
{
  public static final int ET_STARTED = 1;
  public static final int ET_UPDATED = 2;
  public static final int ET_COMPLETE = 3;
  public static final int ET_STOPPED = 4;
  public static final int ET_TIMEOUT = 5;
  public static final int ET_REPLACED = 5;
  public static final int ET_TOO_MANY_PEERS = 6;
  public static final int ET_FAILED = 7;
  public static final int ET_ANNOUNCE = 8;
  
  public abstract Map eventOccurred(TRTrackerServerTorrent paramTRTrackerServerTorrent, TRTrackerServerPeer paramTRTrackerServerPeer, int paramInt, String paramString)
    throws TRTrackerServerException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerTorrentPeerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */