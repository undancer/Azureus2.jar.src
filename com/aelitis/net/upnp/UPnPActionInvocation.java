package com.aelitis.net.upnp;

import java.util.Map;

public abstract interface UPnPActionInvocation
{
  public abstract void addArgument(String paramString1, String paramString2);
  
  public abstract UPnPActionArgument[] invoke()
    throws UPnPException;
  
  public abstract Map invoke2()
    throws UPnPException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPActionInvocation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */