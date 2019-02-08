package org.gudy.azureus2.core3.torrent;

public abstract interface TOTorrentCreator
{
  public abstract void setFileIsLayoutDescriptor(boolean paramBoolean);
  
  public abstract TOTorrent create()
    throws TOTorrentException;
  
  public abstract long getTorrentDataSizeFromFileOrDir()
    throws TOTorrentException;
  
  public abstract void cancel();
  
  public abstract void addListener(TOTorrentProgressListener paramTOTorrentProgressListener);
  
  public abstract void removeListener(TOTorrentProgressListener paramTOTorrentProgressListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/TOTorrentCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */