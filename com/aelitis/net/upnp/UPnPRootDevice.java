package com.aelitis.net.upnp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Map;

public abstract interface UPnPRootDevice
{
  public abstract UPnP getUPnP();
  
  public abstract String getUSN();
  
  public abstract URL getLocation();
  
  public abstract InetAddress getLocalAddress();
  
  public abstract NetworkInterface getNetworkInterface();
  
  public abstract String getInfo();
  
  public abstract UPnPDevice getDevice();
  
  public abstract boolean isDestroyed();
  
  public abstract Map getDiscoveryCache();
  
  public abstract void addListener(UPnPRootDeviceListener paramUPnPRootDeviceListener);
  
  public abstract void removeListener(UPnPRootDeviceListener paramUPnPRootDeviceListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPRootDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */