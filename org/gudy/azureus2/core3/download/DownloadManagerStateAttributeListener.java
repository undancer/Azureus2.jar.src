package org.gudy.azureus2.core3.download;

public abstract interface DownloadManagerStateAttributeListener
{
  public static final int WRITTEN = 1;
  public static final int WILL_BE_READ = 2;
  
  public abstract void attributeEventOccurred(DownloadManager paramDownloadManager, String paramString, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerStateAttributeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */