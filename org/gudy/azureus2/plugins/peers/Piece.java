package org.gudy.azureus2.plugins.peers;

public abstract interface Piece
{
  public abstract int getIndex();
  
  public abstract int getLength();
  
  public abstract boolean isDone();
  
  public abstract boolean isNeeded();
  
  public abstract boolean isDownloading();
  
  public abstract boolean isFullyAllocatable();
  
  public abstract int getAllocatableRequestCount();
  
  public abstract Peer getReservedFor();
  
  public abstract void setReservedFor(Peer paramPeer);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/Piece.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */