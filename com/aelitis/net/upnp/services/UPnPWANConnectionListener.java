package com.aelitis.net.upnp.services;

public abstract interface UPnPWANConnectionListener
{
  public abstract void mappingResult(UPnPWANConnection paramUPnPWANConnection, boolean paramBoolean);
  
  public abstract void mappingsReadResult(UPnPWANConnection paramUPnPWANConnection, boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/services/UPnPWANConnectionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */