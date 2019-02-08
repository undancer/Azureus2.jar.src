package org.gudy.azureus2.plugins.torrent;

public abstract interface TorrentCreator
{
  public abstract void start();
  
  public abstract void cancel();
  
  public abstract void addListener(TorrentCreatorListener paramTorrentCreatorListener);
  
  public abstract void removeListener(TorrentCreatorListener paramTorrentCreatorListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/torrent/TorrentCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */