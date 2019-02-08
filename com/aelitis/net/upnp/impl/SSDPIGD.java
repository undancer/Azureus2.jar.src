package com.aelitis.net.upnp.impl;

import com.aelitis.net.upnp.UPnPException;
import com.aelitis.net.upnp.UPnPSSDP;

public abstract interface SSDPIGD
{
  public abstract UPnPSSDP getSSDP();
  
  public abstract void start()
    throws UPnPException;
  
  public abstract void searchNow();
  
  public abstract void searchNow(String[] paramArrayOfString);
  
  public abstract void addListener(SSDPIGDListener paramSSDPIGDListener);
  
  public abstract void removeListener(SSDPIGDListener paramSSDPIGDListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/SSDPIGD.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */