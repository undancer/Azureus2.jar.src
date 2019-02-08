package org.gudy.azureus2.plugins.torrent;

public abstract interface TorrentCreatorListener
{
  public abstract void reportPercentageDone(int paramInt);
  
  public abstract void reportActivity(String paramString);
  
  public abstract void complete(Torrent paramTorrent);
  
  public abstract void failed(TorrentException paramTorrentException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentCreatorListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */