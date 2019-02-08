package com.aelitis.azureus.core.speedmanager;

public abstract interface SpeedManagerPingZone
{
  public abstract int getUploadStartBytesPerSec();
  
  public abstract int getUploadEndBytesPerSec();
  
  public abstract int getDownloadStartBytesPerSec();
  
  public abstract int getDownloadEndBytesPerSec();
  
  public abstract int getMetric();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/SpeedManagerPingZone.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */