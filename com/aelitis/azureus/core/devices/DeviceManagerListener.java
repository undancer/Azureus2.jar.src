package com.aelitis.azureus.core.devices;

public abstract interface DeviceManagerListener
{
  public abstract void deviceAdded(Device paramDevice);
  
  public abstract void deviceChanged(Device paramDevice);
  
  public abstract void deviceAttentionRequest(Device paramDevice);
  
  public abstract void deviceRemoved(Device paramDevice);
  
  public abstract void deviceManagerLoaded();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */