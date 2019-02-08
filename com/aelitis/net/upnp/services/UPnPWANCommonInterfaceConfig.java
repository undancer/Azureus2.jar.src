package com.aelitis.net.upnp.services;

import com.aelitis.net.upnp.UPnPException;

public abstract interface UPnPWANCommonInterfaceConfig
  extends UPnPSpecificService
{
  public abstract long[] getCommonLinkProperties()
    throws UPnPException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/services/UPnPWANCommonInterfaceConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */