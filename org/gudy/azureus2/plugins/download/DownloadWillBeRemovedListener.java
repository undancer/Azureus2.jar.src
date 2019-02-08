package org.gudy.azureus2.plugins.download;

public abstract interface DownloadWillBeRemovedListener
{
  public abstract void downloadWillBeRemoved(Download paramDownload)
    throws DownloadRemovalVetoException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadWillBeRemovedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */