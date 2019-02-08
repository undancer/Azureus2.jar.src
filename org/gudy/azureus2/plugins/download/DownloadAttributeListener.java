package org.gudy.azureus2.plugins.download;

import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

public abstract interface DownloadAttributeListener
{
  public static final int WRITTEN = 1;
  public static final int WILL_BE_READ = 2;
  
  public abstract void attributeEventOccurred(Download paramDownload, TorrentAttribute paramTorrentAttribute, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadAttributeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */