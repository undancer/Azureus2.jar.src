package com.aelitis.azureus.core.proxy.socks;

import java.io.IOException;
import java.net.InetAddress;

public abstract interface AESocksProxyPlugableConnection
{
  public abstract String getName();
  
  public abstract InetAddress getLocalAddress();
  
  public abstract int getLocalPort();
  
  public abstract void connect(AESocksProxyAddress paramAESocksProxyAddress)
    throws IOException;
  
  public abstract void relayData()
    throws IOException;
  
  public abstract void close()
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/AESocksProxyPlugableConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */