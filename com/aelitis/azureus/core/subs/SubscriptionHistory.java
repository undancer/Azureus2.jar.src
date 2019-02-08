package com.aelitis.azureus.core.subs;

public abstract interface SubscriptionHistory
{
  public static final int DEFAULT_CHECK_INTERVAL_MINS = 120;
  
  public abstract boolean isEnabled();
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract boolean isAutoDownload();
  
  public abstract void setAutoDownload(boolean paramBoolean);
  
  public abstract void setDetails(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void deleteResults(String[] paramArrayOfString);
  
  public abstract void deleteAllResults();
  
  public abstract void markAllResultsRead();
  
  public abstract void markAllResultsUnread();
  
  public abstract void markResults(String[] paramArrayOfString, boolean[] paramArrayOfBoolean);
  
  public abstract void reset();
  
  public abstract long getLastScanTime();
  
  public abstract long getLastNewResultTime();
  
  public abstract long getNextScanTime();
  
  public abstract int getNumUnread();
  
  public abstract int getNumRead();
  
  public abstract int getCheckFrequencyMins();
  
  public abstract void setCheckFrequencyMins(int paramInt);
  
  public abstract String getLastError();
  
  public abstract boolean isAuthFail();
  
  public abstract int getConsecFails();
  
  public abstract SubscriptionResult[] getResults(boolean paramBoolean);
  
  public abstract SubscriptionResult getResult(String paramString);
  
  public abstract boolean getDownloadWithReferer();
  
  public abstract void setDownloadWithReferer(boolean paramBoolean);
  
  public abstract int getMaxNonDeletedResults();
  
  public abstract void setMaxNonDeletedResults(int paramInt);
  
  public abstract String[] getDownloadNetworks();
  
  public abstract void setDownloadNetworks(String[] paramArrayOfString);
  
  public abstract boolean getNotificationPostEnabled();
  
  public abstract void setNotificationPostEnabled(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionHistory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */