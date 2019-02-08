package org.gudy.azureus2.plugins.sharing;

import java.io.File;
import org.gudy.azureus2.plugins.torrent.Torrent;

public abstract interface ShareItem
{
  public abstract File getTorrentFile();
  
  public abstract Torrent getTorrent()
    throws ShareException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/sharing/ShareItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */