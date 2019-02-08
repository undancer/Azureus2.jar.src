package com.aelitis.azureus.core.speedmanager;

public abstract interface SpeedManagerPingMapper
{
  public abstract String getName();
  
  public abstract int[][] getHistory();
  
  public abstract SpeedManagerPingZone[] getZones();
  
  public abstract SpeedManagerLimitEstimate getEstimatedUploadLimit(boolean paramBoolean);
  
  public abstract SpeedManagerLimitEstimate getEstimatedDownloadLimit(boolean paramBoolean);
  
  public abstract double getCurrentMetricRating();
  
  public abstract SpeedManagerLimitEstimate getLastBadUploadLimit();
  
  public abstract SpeedManagerLimitEstimate getLastBadDownloadLimit();
  
  public abstract SpeedManagerLimitEstimate[] getBadUploadHistory();
  
  public abstract SpeedManagerLimitEstimate[] getBadDownloadHistory();
  
  public abstract boolean isActive();
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/SpeedManagerPingMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */