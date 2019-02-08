package com.aelitis.azureus.core.proxy.socks;

import com.aelitis.azureus.core.proxy.AEProxyException;

public abstract interface AESocksProxyPlugableConnectionFactory
{
  public abstract AESocksProxyPlugableConnection create(AESocksProxyConnection paramAESocksProxyConnection)
    throws AEProxyException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/AESocksProxyPlugableConnectionFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */