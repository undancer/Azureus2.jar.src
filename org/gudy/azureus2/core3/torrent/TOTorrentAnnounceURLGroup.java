package org.gudy.azureus2.core3.torrent;

import java.net.URL;

public abstract interface TOTorrentAnnounceURLGroup
{
  public abstract TOTorrentAnnounceURLSet[] getAnnounceURLSets();
  
  public abstract void setAnnounceURLSets(TOTorrentAnnounceURLSet[] paramArrayOfTOTorrentAnnounceURLSet);
  
  public abstract TOTorrentAnnounceURLSet createAnnounceURLSet(URL[] paramArrayOfURL);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/TOTorrentAnnounceURLGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */