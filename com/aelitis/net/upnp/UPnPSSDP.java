package com.aelitis.net.upnp;

public abstract interface UPnPSSDP
{
  public static final String SSDP_GROUP_ADDRESS = "239.255.255.250";
  public static final int SSDP_GROUP_PORT = 1900;
  
  public abstract int getControlPort();
  
  public abstract void search(String[] paramArrayOfString);
  
  public abstract void notify(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract void addListener(UPnPSSDPListener paramUPnPSSDPListener);
  
  public abstract void removeListener(UPnPSSDPListener paramUPnPSSDPListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPSSDP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */