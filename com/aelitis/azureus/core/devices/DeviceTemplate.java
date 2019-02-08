package com.aelitis.azureus.core.devices;

public abstract interface DeviceTemplate
{
  public abstract int getType();
  
  public abstract String getName();
  
  public abstract String getManufacturer();
  
  public abstract String getClassification();
  
  public abstract String getShortDescription();
  
  public abstract boolean isAuto();
  
  public abstract Device createInstance(String paramString)
    throws DeviceManagerException;
  
  public abstract Device createInstance(String paramString1, String paramString2, boolean paramBoolean)
    throws DeviceManagerException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/DeviceTemplate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */