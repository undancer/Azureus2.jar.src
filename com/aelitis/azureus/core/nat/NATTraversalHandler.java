package com.aelitis.azureus.core.nat;

import java.net.InetSocketAddress;
import java.util.Map;

public abstract interface NATTraversalHandler
{
  public abstract int getType();
  
  public abstract String getName();
  
  public abstract Map process(InetSocketAddress paramInetSocketAddress, Map paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/nat/NATTraversalHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */