package org.gudy.azureus2.core3.global;

import org.gudy.azureus2.core3.download.DownloadManager;

public abstract interface GlobalManagerDownloadWillBeRemovedListener
{
  public abstract void downloadWillBeRemoved(DownloadManager paramDownloadManager, boolean paramBoolean1, boolean paramBoolean2)
    throws GlobalManagerDownloadRemovalVetoException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/GlobalManagerDownloadWillBeRemovedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */