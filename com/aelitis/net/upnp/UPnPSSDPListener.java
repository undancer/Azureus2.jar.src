package com.aelitis.net.upnp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;

public abstract interface UPnPSSDPListener
{
  public abstract void receivedResult(NetworkInterface paramNetworkInterface, InetAddress paramInetAddress1, InetAddress paramInetAddress2, String paramString1, URL paramURL, String paramString2, String paramString3);
  
  public abstract void receivedNotify(NetworkInterface paramNetworkInterface, InetAddress paramInetAddress1, InetAddress paramInetAddress2, String paramString1, URL paramURL, String paramString2, String paramString3);
  
  public abstract String[] receivedSearch(NetworkInterface paramNetworkInterface, InetAddress paramInetAddress1, InetAddress paramInetAddress2, String paramString);
  
  public abstract void interfaceChanged(NetworkInterface paramNetworkInterface);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPSSDPListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */