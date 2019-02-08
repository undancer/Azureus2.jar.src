package org.gudy.azureus2.core3.torrent;

public abstract interface TOTorrentListener
{
  public static final int CT_ANNOUNCE_URLS = 1;
  
  public abstract void torrentChanged(TOTorrent paramTOTorrent, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/TOTorrentListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */