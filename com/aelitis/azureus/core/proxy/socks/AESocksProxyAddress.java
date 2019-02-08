package com.aelitis.azureus.core.proxy.socks;

import java.net.InetAddress;

public abstract interface AESocksProxyAddress
{
  public abstract String getUnresolvedAddress();
  
  public abstract InetAddress getAddress();
  
  public abstract int getPort();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/AESocksProxyAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */