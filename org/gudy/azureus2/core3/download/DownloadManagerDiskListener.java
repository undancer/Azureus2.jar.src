package org.gudy.azureus2.core3.download;

import org.gudy.azureus2.core3.disk.DiskManager;

public abstract interface DownloadManagerDiskListener
{
  public abstract void diskManagerAdded(DiskManager paramDiskManager);
  
  public abstract void diskManagerRemoved(DiskManager paramDiskManager);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerDiskListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */