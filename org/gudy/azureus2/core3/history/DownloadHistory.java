package org.gudy.azureus2.core3.history;

public abstract interface DownloadHistory
{
  public abstract long getUID();
  
  public abstract byte[] getTorrentHash();
  
  public abstract String getName();
  
  public abstract long getSize();
  
  public abstract String getSaveLocation();
  
  public abstract long getAddTime();
  
  public abstract long getCompleteTime();
  
  public abstract long getRemoveTime();
  
  public abstract void setRedownloading();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/history/DownloadHistory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */