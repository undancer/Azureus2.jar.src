package org.gudy.azureus2.core3.global;

import org.gudy.azureus2.core3.download.DownloadManager;

public abstract interface GlobalManagerEvent
{
  public static final int ET_REQUEST_ATTENTION = 1;
  
  public abstract int getEventType();
  
  public abstract DownloadManager getDownload();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/GlobalManagerEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */