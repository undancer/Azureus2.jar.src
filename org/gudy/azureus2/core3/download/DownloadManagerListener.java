package org.gudy.azureus2.core3.download;

import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;

public abstract interface DownloadManagerListener
{
  public abstract void stateChanged(DownloadManager paramDownloadManager, int paramInt);
  
  public abstract void downloadComplete(DownloadManager paramDownloadManager);
  
  public abstract void completionChanged(DownloadManager paramDownloadManager, boolean paramBoolean);
  
  public abstract void positionChanged(DownloadManager paramDownloadManager, int paramInt1, int paramInt2);
  
  public abstract void filePriorityChanged(DownloadManager paramDownloadManager, DiskManagerFileInfo paramDiskManagerFileInfo);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */