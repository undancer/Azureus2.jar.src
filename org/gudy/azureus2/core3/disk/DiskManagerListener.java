package org.gudy.azureus2.core3.disk;

public abstract interface DiskManagerListener
{
  public abstract void stateChanged(int paramInt1, int paramInt2);
  
  public abstract void filePriorityChanged(DiskManagerFileInfo paramDiskManagerFileInfo);
  
  public abstract void pieceDoneChanged(DiskManagerPiece paramDiskManagerPiece);
  
  public abstract void fileAccessModeChanged(DiskManagerFileInfo paramDiskManagerFileInfo, int paramInt1, int paramInt2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */