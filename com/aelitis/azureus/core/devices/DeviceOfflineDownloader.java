package com.aelitis.azureus.core.devices;

public abstract interface DeviceOfflineDownloader
  extends Device
{
  public abstract boolean isEnabled();
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract boolean hasShownFTUX();
  
  public abstract void setShownFTUX();
  
  public abstract String getManufacturer();
  
  public abstract long getSpaceAvailable(boolean paramBoolean)
    throws DeviceManagerException;
  
  public abstract int getTransferingCount();
  
  public abstract DeviceOfflineDownload[] getDownloads();
  
  public abstract void addListener(DeviceOfflineDownloaderListener paramDeviceOfflineDownloaderListener);
  
  public abstract void removeListener(DeviceOfflineDownloaderListener paramDeviceOfflineDownloaderListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceOfflineDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */