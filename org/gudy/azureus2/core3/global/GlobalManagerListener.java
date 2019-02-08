package org.gudy.azureus2.core3.global;

import org.gudy.azureus2.core3.download.DownloadManager;

public abstract interface GlobalManagerListener
{
  public abstract void downloadManagerAdded(DownloadManager paramDownloadManager);
  
  public abstract void downloadManagerRemoved(DownloadManager paramDownloadManager);
  
  public abstract void destroyInitiated();
  
  public abstract void destroyed();
  
  public abstract void seedingStatusChanged(boolean paramBoolean1, boolean paramBoolean2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/GlobalManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */