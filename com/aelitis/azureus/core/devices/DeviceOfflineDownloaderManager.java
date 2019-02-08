package com.aelitis.azureus.core.devices;

import org.gudy.azureus2.plugins.download.Download;

public abstract interface DeviceOfflineDownloaderManager
{
  public abstract boolean isOfflineDownloadingEnabled();
  
  public abstract void setOfflineDownloadingEnabled(boolean paramBoolean);
  
  public abstract boolean getOfflineDownloadingIsAuto();
  
  public abstract void setOfflineDownloadingIsAuto(boolean paramBoolean);
  
  public abstract boolean getOfflineDownloadingIncludePrivate();
  
  public abstract void setOfflineDownloadingIncludePrivate(boolean paramBoolean);
  
  public abstract boolean isManualDownload(Download paramDownload);
  
  public abstract void addManualDownloads(Download[] paramArrayOfDownload);
  
  public abstract void removeManualDownloads(Download[] paramArrayOfDownload);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceOfflineDownloaderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */