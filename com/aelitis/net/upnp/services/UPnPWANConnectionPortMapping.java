package com.aelitis.net.upnp.services;

public abstract interface UPnPWANConnectionPortMapping
{
  public abstract boolean isTCP();
  
  public abstract int getExternalPort();
  
  public abstract String getInternalHost();
  
  public abstract String getDescription();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/services/UPnPWANConnectionPortMapping.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */