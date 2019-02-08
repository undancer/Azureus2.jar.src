package com.aelitis.net.upnp;

import java.net.URL;

public abstract interface UPnPListener
{
  public abstract boolean deviceDiscovered(String paramString, URL paramURL);
  
  public abstract void rootDeviceFound(UPnPRootDevice paramUPnPRootDevice);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */