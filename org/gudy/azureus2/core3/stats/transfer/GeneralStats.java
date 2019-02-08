package org.gudy.azureus2.core3.stats.transfer;

public abstract interface GeneralStats
{
  public abstract long getDownloadedBytes();
  
  public abstract long getUploadedBytes();
  
  public abstract long getTotalUpTime();
  
  public abstract long getDownloadedBytes(boolean paramBoolean);
  
  public abstract long getUploadedBytes(boolean paramBoolean);
  
  public abstract long getTotalUpTime(boolean paramBoolean);
  
  public abstract int getAverageDownloadSpeed(boolean paramBoolean);
  
  public abstract int getAverageUploadSpeed(boolean paramBoolean);
  
  public abstract long getMarkTime();
  
  public abstract void setMark();
  
  public abstract void clearMark();
  
  public abstract long getSessionUpTime();
  
  public abstract int getAverageDownloadSpeed();
  
  public abstract int getAverageUploadSpeed();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/transfer/GeneralStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */