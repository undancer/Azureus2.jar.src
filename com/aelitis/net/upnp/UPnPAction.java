package com.aelitis.net.upnp;

public abstract interface UPnPAction
{
  public abstract String getName();
  
  public abstract UPnPService getService();
  
  public abstract UPnPActionInvocation getInvocation();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */