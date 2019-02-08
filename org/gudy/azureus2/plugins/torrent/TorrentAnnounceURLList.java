package org.gudy.azureus2.plugins.torrent;

import java.net.URL;

public abstract interface TorrentAnnounceURLList
{
  public abstract TorrentAnnounceURLListSet[] getSets();
  
  public abstract void setSets(TorrentAnnounceURLListSet[] paramArrayOfTorrentAnnounceURLListSet);
  
  public abstract TorrentAnnounceURLListSet create(URL[] paramArrayOfURL);
  
  public abstract void addSet(URL[] paramArrayOfURL);
  
  public abstract void insertSetAtFront(URL[] paramArrayOfURL);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentAnnounceURLList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */