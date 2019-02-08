package org.gudy.azureus2.plugins.torrent;

public abstract interface TorrentManagerEvent
{
  public static final int ET_CREATION_STATUS = 1;
  public static final int ET_TORRENT_OPTIONS_CREATED = 2;
  public static final int ET_TORRENT_OPTIONS_ACCEPTED = 3;
  public static final int ET_TORRENT_OPTIONS_CANCELLED = 4;
  
  public abstract int getType();
  
  public abstract Object getData();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentManagerEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */