package com.aelitis.net.upnp.impl;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;

public abstract interface SSDPIGDListener
{
  public abstract void rootDiscovered(NetworkInterface paramNetworkInterface, InetAddress paramInetAddress, String paramString, URL paramURL);
  
  public abstract void rootAlive(String paramString, URL paramURL);
  
  public abstract void rootLost(InetAddress paramInetAddress, String paramString);
  
  public abstract void interfaceChanged(NetworkInterface paramNetworkInterface);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/SSDPIGDListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */