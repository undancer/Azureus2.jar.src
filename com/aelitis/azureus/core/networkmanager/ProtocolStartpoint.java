package com.aelitis.azureus.core.networkmanager;

import java.net.InetSocketAddress;

public abstract interface ProtocolStartpoint
{
  public abstract int getType();
  
  public abstract InetSocketAddress getAddress();
  
  public abstract String getDescription();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/ProtocolStartpoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */