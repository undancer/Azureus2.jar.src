package org.gudy.azureus2.core3.category;

import org.gudy.azureus2.core3.download.DownloadManager;

public abstract interface CategoryListener
{
  public abstract void downloadManagerAdded(Category paramCategory, DownloadManager paramDownloadManager);
  
  public abstract void downloadManagerRemoved(Category paramCategory, DownloadManager paramDownloadManager);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/category/CategoryListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */