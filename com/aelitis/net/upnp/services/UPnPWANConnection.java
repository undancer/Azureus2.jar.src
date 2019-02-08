package com.aelitis.net.upnp.services;

import com.aelitis.net.upnp.UPnPException;

public abstract interface UPnPWANConnection
  extends UPnPSpecificService
{
  public static final int CAP_UDP_TCP_SAME_PORT = 1;
  public static final int CAP_ALL = -1;
  
  public abstract String getConnectionType();
  
  public abstract void addPortMapping(boolean paramBoolean, int paramInt, String paramString)
    throws UPnPException;
  
  public abstract UPnPWANConnectionPortMapping[] getPortMappings()
    throws UPnPException;
  
  public abstract void deletePortMapping(boolean paramBoolean, int paramInt)
    throws UPnPException;
  
  public abstract String[] getStatusInfo()
    throws UPnPException;
  
  public abstract void periodicallyRecheckMappings(boolean paramBoolean);
  
  public abstract int getCapabilities();
  
  public abstract String getExternalIPAddress()
    throws UPnPException;
  
  public abstract void addListener(UPnPWANConnectionListener paramUPnPWANConnectionListener);
  
  public abstract void removeListener(UPnPWANConnectionListener paramUPnPWANConnectionListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/services/UPnPWANConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */