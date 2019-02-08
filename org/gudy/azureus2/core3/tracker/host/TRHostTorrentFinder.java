package org.gudy.azureus2.core3.tracker.host;

import org.gudy.azureus2.core3.torrent.TOTorrent;

public abstract interface TRHostTorrentFinder
{
  public abstract TOTorrent lookupTorrent(byte[] paramArrayOfByte);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/TRHostTorrentFinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */