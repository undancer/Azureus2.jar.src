package org.gudy.azureus2.core3.history;

import java.util.List;

public abstract interface DownloadHistoryManager
{
  public abstract boolean isEnabled();
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract int getHistoryCount();
  
  public abstract void removeHistory(List<DownloadHistory> paramList);
  
  public abstract void resetHistory();
  
  public abstract long[] getDates(byte[] paramArrayOfByte);
  
  public abstract void addListener(DownloadHistoryListener paramDownloadHistoryListener, boolean paramBoolean);
  
  public abstract void removeListener(DownloadHistoryListener paramDownloadHistoryListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/history/DownloadHistoryManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */