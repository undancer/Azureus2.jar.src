package com.aelitis.azureus.core.devices;

import java.net.URL;
import java.util.List;

public abstract interface DeviceContentDirectory
  extends Device
{
  public abstract List<URL> getControlURLs();
  
  public abstract void setPreferredControlURL(URL paramURL);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceContentDirectory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */