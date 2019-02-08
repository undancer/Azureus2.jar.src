package org.gudy.azureus2.core3.tracker.client;

public abstract interface TRTrackerAnnouncerDataProvider
{
  public abstract String getName();
  
  public abstract long getTotalSent();
  
  public abstract long getTotalReceived();
  
  public abstract long getRemaining();
  
  public abstract long getFailedHashCheck();
  
  public abstract String getExtensions();
  
  public abstract int getMaxNewConnectionsAllowed(String paramString);
  
  public abstract int getPendingConnectionCount();
  
  public abstract int getConnectedConnectionCount();
  
  public abstract int getUploadSpeedKBSec(boolean paramBoolean);
  
  public abstract int getCryptoLevel();
  
  public abstract boolean isPeerSourceEnabled(String paramString);
  
  public abstract void setPeerSources(String[] paramArrayOfString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/TRTrackerAnnouncerDataProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */