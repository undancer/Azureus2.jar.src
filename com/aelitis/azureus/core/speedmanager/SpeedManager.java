package com.aelitis.azureus.core.speedmanager;

import com.aelitis.azureus.core.dht.speed.DHTSpeedTester;

public abstract interface SpeedManager
{
  public abstract boolean isAvailable();
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract boolean isEnabled();
  
  public abstract String getASN();
  
  public abstract SpeedManagerLimitEstimate getEstimatedUploadCapacityBytesPerSec();
  
  public abstract void setEstimatedUploadCapacityBytesPerSec(int paramInt, float paramFloat);
  
  public abstract SpeedManagerLimitEstimate getEstimatedDownloadCapacityBytesPerSec();
  
  public abstract void setEstimatedDownloadCapacityBytesPerSec(int paramInt, float paramFloat);
  
  public abstract void setSpeedTester(DHTSpeedTester paramDHTSpeedTester);
  
  public abstract DHTSpeedTester getSpeedTester();
  
  public abstract SpeedManagerPingSource[] getPingSources();
  
  public abstract SpeedManagerPingMapper getActiveMapper();
  
  public abstract SpeedManagerPingMapper[] getMappers();
  
  public abstract void reset();
  
  public abstract void addListener(SpeedManagerListener paramSpeedManagerListener);
  
  public abstract void removeListener(SpeedManagerListener paramSpeedManagerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/SpeedManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */