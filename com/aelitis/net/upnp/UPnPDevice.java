package com.aelitis.net.upnp;

public abstract interface UPnPDevice
{
  public abstract String getDeviceType();
  
  public abstract String getFriendlyName();
  
  public abstract String getManufacturer();
  
  public abstract String getManufacturerURL();
  
  public abstract String getModelDescription();
  
  public abstract String getModelName();
  
  public abstract String getModelNumber();
  
  public abstract String getModelURL();
  
  public abstract String getPresentation();
  
  public abstract UPnPDevice[] getSubDevices();
  
  public abstract UPnPService[] getServices();
  
  public abstract UPnPRootDevice getRootDevice();
  
  public abstract UPnPDeviceImage[] getImages();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */