package com.aelitis.net.upnp;

public abstract interface UPnPStateVariable
{
  public abstract String getName();
  
  public abstract UPnPService getService();
  
  public abstract String getValue()
    throws UPnPException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPStateVariable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */