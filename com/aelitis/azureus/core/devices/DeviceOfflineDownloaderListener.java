package com.aelitis.azureus.core.devices;

public abstract interface DeviceOfflineDownloaderListener
{
  public abstract void downloadAdded(DeviceOfflineDownload paramDeviceOfflineDownload);
  
  public abstract void downloadChanged(DeviceOfflineDownload paramDeviceOfflineDownload);
  
  public abstract void downloadRemoved(DeviceOfflineDownload paramDeviceOfflineDownload);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceOfflineDownloaderListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */