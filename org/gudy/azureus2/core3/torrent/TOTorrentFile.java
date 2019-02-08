package org.gudy.azureus2.core3.torrent;

import java.util.Map;

public abstract interface TOTorrentFile
{
  public abstract TOTorrent getTorrent();
  
  public abstract int getIndex();
  
  public abstract long getLength();
  
  public abstract byte[][] getPathComponents();
  
  public abstract String getRelativePath();
  
  public abstract int getFirstPieceNumber();
  
  public abstract int getLastPieceNumber();
  
  public abstract int getNumberOfPieces();
  
  public abstract Map getAdditionalProperties();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/TOTorrentFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */