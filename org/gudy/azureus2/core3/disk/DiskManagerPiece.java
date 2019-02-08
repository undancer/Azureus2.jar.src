package org.gudy.azureus2.core3.disk;

public abstract interface DiskManagerPiece
{
  public abstract DiskManager getManager();
  
  public abstract int getLength();
  
  public abstract int getPieceNumber();
  
  public abstract int getNbBlocks();
  
  public abstract int getBlockSize(int paramInt);
  
  public abstract short getReadCount();
  
  public abstract void setReadCount(short paramShort);
  
  public abstract boolean calcNeeded();
  
  public abstract void clearNeeded();
  
  public abstract boolean isNeeded();
  
  public abstract void setNeeded();
  
  public abstract void setNeeded(boolean paramBoolean);
  
  public abstract boolean isWritten();
  
  public abstract int getNbWritten();
  
  public abstract boolean[] getWritten();
  
  public abstract boolean isWritten(int paramInt);
  
  public abstract void setWritten(int paramInt);
  
  public abstract void setChecking();
  
  public abstract boolean isChecking();
  
  public abstract boolean isNeedsCheck();
  
  public abstract boolean spansFiles();
  
  public abstract boolean calcDone();
  
  public abstract boolean isDone();
  
  public abstract void setDone(boolean paramBoolean);
  
  public abstract boolean isInteresting();
  
  public abstract boolean isDownloadable();
  
  public abstract void setDownloadable();
  
  public abstract boolean isSkipped();
  
  public abstract void reDownloadBlock(int paramInt);
  
  public abstract void reset();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerPiece.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */