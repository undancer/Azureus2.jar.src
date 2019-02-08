package com.aelitis.azureus.core.devices;

import com.aelitis.net.upnp.UPnPDevice;
import java.io.File;
import java.net.InetAddress;

public abstract interface DeviceManager
{
  public static final String CONFIG_VIEW_HIDE_REND_GENERIC = "device.sidebar.ui.rend.hidegeneric";
  public static final String CONFIG_VIEW_SHOW_ONLY_TAGGED = "device.sidebar.ui.rend.showonlytagged";
  
  public abstract DeviceTemplate[] getDeviceTemplates(int paramInt);
  
  public abstract DeviceManufacturer[] getDeviceManufacturers(int paramInt);
  
  public abstract Device[] getDevices();
  
  public abstract Device addVirtualDevice(int paramInt, String paramString1, String paramString2, String paramString3)
    throws DeviceManagerException;
  
  public abstract Device addInetDevice(int paramInt, String paramString1, String paramString2, String paramString3, InetAddress paramInetAddress)
    throws DeviceManagerException;
  
  public abstract void search(int paramInt, DeviceSearchListener paramDeviceSearchListener);
  
  public abstract boolean getAutoSearch();
  
  public abstract void setAutoSearch(boolean paramBoolean);
  
  public abstract int getAutoHideOldDevicesDays();
  
  public abstract void setAutoHideOldDevicesDays(int paramInt);
  
  public abstract boolean isRSSPublishEnabled();
  
  public abstract void setRSSPublishEnabled(boolean paramBoolean);
  
  public abstract String getRSSLink();
  
  public abstract UnassociatedDevice[] getUnassociatedDevices();
  
  public abstract TranscodeManager getTranscodeManager();
  
  public abstract File getDefaultWorkingDirectory();
  
  public abstract void setDefaultWorkingDirectory(File paramFile);
  
  public abstract boolean isBusy(int paramInt);
  
  public abstract DeviceOfflineDownloaderManager getOfflineDownlaoderManager();
  
  public abstract boolean isTiVoEnabled();
  
  public abstract void setTiVoEnabled(boolean paramBoolean);
  
  public abstract boolean getDisableSleep();
  
  public abstract void setDisableSleep(boolean paramBoolean);
  
  public abstract String getLocalServiceName();
  
  public abstract void addDiscoveryListener(DeviceManagerDiscoveryListener paramDeviceManagerDiscoveryListener);
  
  public abstract void removeDiscoveryListener(DeviceManagerDiscoveryListener paramDeviceManagerDiscoveryListener);
  
  public abstract void addListener(DeviceManagerListener paramDeviceManagerListener);
  
  public abstract void removeListener(DeviceManagerListener paramDeviceManagerListener);
  
  public abstract Device findDevice(UPnPDevice paramUPnPDevice);
  
  public static abstract interface DeviceManufacturer
  {
    public abstract String getName();
    
    public abstract DeviceTemplate[] getDeviceTemplates();
  }
  
  public static abstract interface UnassociatedDevice
  {
    public abstract InetAddress getAddress();
    
    public abstract String getDescription();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */