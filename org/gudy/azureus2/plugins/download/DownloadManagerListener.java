package org.gudy.azureus2.plugins.download;

public abstract interface DownloadManagerListener
{
  public abstract void downloadAdded(Download paramDownload);
  
  public abstract void downloadRemoved(Download paramDownload);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */