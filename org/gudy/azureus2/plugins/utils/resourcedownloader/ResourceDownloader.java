package org.gudy.azureus2.plugins.utils.resourcedownloader;

import java.io.InputStream;

public abstract interface ResourceDownloader
{
  public static final String PR_STRING_CONTENT_TYPE = "ContentType";
  public static final String PR_BOOLEAN_ANONYMOUS = "Anonymous";
  
  public abstract String getName();
  
  public abstract InputStream download()
    throws ResourceDownloaderException;
  
  public abstract void asyncDownload();
  
  public abstract long getSize()
    throws ResourceDownloaderException;
  
  public abstract void setProperty(String paramString, Object paramObject)
    throws ResourceDownloaderException;
  
  public abstract Object getProperty(String paramString)
    throws ResourceDownloaderException;
  
  public abstract void cancel();
  
  public abstract boolean isCancelled();
  
  public abstract ResourceDownloader getClone();
  
  public abstract void reportActivity(String paramString);
  
  public abstract void addListener(ResourceDownloaderListener paramResourceDownloaderListener);
  
  public abstract void removeListener(ResourceDownloaderListener paramResourceDownloaderListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/resourcedownloader/ResourceDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */