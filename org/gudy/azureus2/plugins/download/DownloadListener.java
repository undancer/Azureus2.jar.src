package org.gudy.azureus2.plugins.download;

public abstract interface DownloadListener
{
  public abstract void stateChanged(Download paramDownload, int paramInt1, int paramInt2);
  
  public abstract void positionChanged(Download paramDownload, int paramInt1, int paramInt2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */