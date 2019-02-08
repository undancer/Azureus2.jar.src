package com.aelitis.azureus.core.devices;

import org.gudy.azureus2.plugins.download.Download;

public abstract interface DeviceOfflineDownload
{
  public abstract Download getDownload();
  
  public abstract boolean isTransfering();
  
  public abstract long getCurrentTransferSize();
  
  public abstract long getRemaining();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceOfflineDownload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */