package org.gudy.azureus2.plugins.download;

public abstract interface DownloadActivationEvent
{
  public abstract Download getDownload();
  
  public abstract int getActivationCount();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadActivationEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */