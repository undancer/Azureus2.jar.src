package org.gudy.azureus2.plugins.torrent;

public abstract interface TorrentAttributeEvent
{
  public static final int ET_ATTRIBUTE_VALUE_ADDED = 1;
  public static final int ET_ATTRIBUTE_VALUE_REMOVED = 2;
  
  public abstract int getType();
  
  public abstract TorrentAttribute getAttribute();
  
  public abstract Object getData();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentAttributeEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */