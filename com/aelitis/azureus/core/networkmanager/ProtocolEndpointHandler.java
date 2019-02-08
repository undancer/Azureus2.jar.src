package com.aelitis.azureus.core.networkmanager;

import java.net.InetSocketAddress;

public abstract interface ProtocolEndpointHandler
{
  public abstract int getType();
  
  public abstract ProtocolEndpoint create(InetSocketAddress paramInetSocketAddress);
  
  public abstract ProtocolEndpoint create(ConnectionEndpoint paramConnectionEndpoint, InetSocketAddress paramInetSocketAddress);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/ProtocolEndpointHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */