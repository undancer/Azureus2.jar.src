package com.aelitis.azureus.core.peermanager.nat;

import java.net.InetSocketAddress;

public abstract interface PeerNATTraversalAdapter
{
  public abstract void success(InetSocketAddress paramInetSocketAddress);
  
  public abstract void failed();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/nat/PeerNATTraversalAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */