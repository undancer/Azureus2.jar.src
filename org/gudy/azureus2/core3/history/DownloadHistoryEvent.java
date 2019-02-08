package org.gudy.azureus2.core3.history;

import java.util.List;

public abstract interface DownloadHistoryEvent
{
  public static final int DHE_HISTORY_ADDED = 1;
  public static final int DHE_HISTORY_REMOVED = 2;
  public static final int DHE_HISTORY_MODIFIED = 3;
  
  public abstract int getEventType();
  
  public abstract List<DownloadHistory> getHistory();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/history/DownloadHistoryEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */