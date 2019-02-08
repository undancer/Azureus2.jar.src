package org.gudy.azureus2.plugins.utils.resourcedownloader;

import java.io.InputStream;

public abstract interface ResourceDownloaderListener
{
  public abstract void reportPercentComplete(ResourceDownloader paramResourceDownloader, int paramInt);
  
  public abstract void reportAmountComplete(ResourceDownloader paramResourceDownloader, long paramLong);
  
  public abstract void reportActivity(ResourceDownloader paramResourceDownloader, String paramString);
  
  public abstract boolean completed(ResourceDownloader paramResourceDownloader, InputStream paramInputStream);
  
  public abstract void failed(ResourceDownloader paramResourceDownloader, ResourceDownloaderException paramResourceDownloaderException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloaderListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */