package org.gudy.azureus2.core3.peer;

import org.gudy.azureus2.core3.disk.DiskManagerPiece;

public abstract interface PEPiece
{
  public abstract PEPeerManager getManager();
  
  public abstract DiskManagerPiece getDMPiece();
  
  public abstract int getPieceNumber();
  
  public abstract int getLength();
  
  public abstract int getNbBlocks();
  
  public abstract int getBlockNumber(int paramInt);
  
  public abstract int getBlockSize(int paramInt);
  
  public abstract long getCreationTime();
  
  public abstract long getTimeSinceLastActivity();
  
  public abstract long getLastDownloadTime(long paramLong);
  
  public abstract void addWrite(int paramInt, String paramString, byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract int getNbWritten();
  
  public abstract int getAvailability();
  
  public abstract boolean hasUnrequestedBlock();
  
  public abstract int[] getAndMarkBlocks(PEPeer paramPEPeer, int paramInt, int[] paramArrayOfInt, boolean paramBoolean);
  
  public abstract void getAndMarkBlock(PEPeer paramPEPeer, int paramInt);
  
  public abstract Object getRealTimeData();
  
  public abstract void setRealTimeData(Object paramObject);
  
  public abstract boolean setRequested(PEPeer paramPEPeer, int paramInt);
  
  public abstract void clearRequested(int paramInt);
  
  public abstract boolean isRequested(int paramInt);
  
  public abstract boolean isRequested();
  
  public abstract void setRequested();
  
  public abstract boolean isRequestable();
  
  public abstract int getNbRequests();
  
  public abstract int getNbUnrequested();
  
  public abstract boolean isDownloaded(int paramInt);
  
  public abstract void setDownloaded(int paramInt);
  
  public abstract void clearDownloaded(int paramInt);
  
  public abstract boolean isDownloaded();
  
  public abstract boolean[] getDownloaded();
  
  public abstract boolean hasUndownloadedBlock();
  
  public abstract String getReservedBy();
  
  public abstract void setReservedBy(String paramString);
  
  public abstract int getResumePriority();
  
  public abstract void setResumePriority(int paramInt);
  
  public abstract String[] getWriters();
  
  public abstract void setWritten(String paramString, int paramInt);
  
  public abstract boolean isWritten();
  
  public abstract boolean isWritten(int paramInt);
  
  public abstract int getSpeed();
  
  public abstract void setSpeed(int paramInt);
  
  public abstract void setLastRequestedPeerSpeed(int paramInt);
  
  public abstract void reset();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPiece.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */