package com.aelitis.azureus.core.download;

import java.net.URL;
import org.gudy.azureus2.core3.download.DownloadManager;

public abstract interface StreamManagerDownload
{
  public abstract DownloadManager getDownload();
  
  public abstract int getFileIndex();
  
  public abstract URL getURL();
  
  public abstract boolean getPreviewMode();
  
  public abstract void setPreviewMode(boolean paramBoolean);
  
  public abstract void cancel();
  
  public abstract boolean isCancelled();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/download/StreamManagerDownload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */