package com.aelitis.net.natpmp.upnp;

import com.aelitis.net.upnp.UPnPListener;

public abstract interface NatPMPUPnP
{
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract boolean isEnabled();
  
  public abstract void addListener(UPnPListener paramUPnPListener);
  
  public abstract void removeListener(UPnPListener paramUPnPListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/natpmp/upnp/NatPMPUPnP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */