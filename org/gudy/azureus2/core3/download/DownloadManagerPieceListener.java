package org.gudy.azureus2.core3.download;

import org.gudy.azureus2.core3.peer.PEPiece;

public abstract interface DownloadManagerPieceListener
{
  public abstract void pieceAdded(PEPiece paramPEPiece);
  
  public abstract void pieceRemoved(PEPiece paramPEPiece);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerPieceListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */