package com.aelitis.net.upnp;

import java.util.Map;

public abstract interface UPnP
{
  public abstract UPnPRootDevice[] getRootDevices();
  
  public abstract void reset();
  
  public abstract void search();
  
  public abstract void search(String[] paramArrayOfString);
  
  public abstract void injectDiscoveryCache(Map paramMap);
  
  public abstract UPnPSSDP getSSDP();
  
  public abstract void log(String paramString);
  
  public abstract void addRootDeviceListener(UPnPListener paramUPnPListener);
  
  public abstract void removeRootDeviceListener(UPnPListener paramUPnPListener);
  
  public abstract void addLogListener(UPnPLogListener paramUPnPLogListener);
  
  public abstract void removeLogListener(UPnPLogListener paramUPnPLogListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */