package org.gudy.azureus2.plugins.torrent;

public abstract interface TorrentDownloader
{
  public abstract Torrent download()
    throws TorrentException;
  
  public abstract Torrent download(String paramString)
    throws TorrentException;
  
  public abstract void setRequestProperty(String paramString, Object paramObject)
    throws TorrentException;
  
  public abstract Object getRequestProperty(String paramString)
    throws TorrentException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */