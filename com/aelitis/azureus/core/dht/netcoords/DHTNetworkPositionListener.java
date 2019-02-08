package com.aelitis.azureus.core.dht.netcoords;

import java.net.InetAddress;

public abstract interface DHTNetworkPositionListener
{
  public abstract void positionFound(DHTNetworkPositionProvider paramDHTNetworkPositionProvider, InetAddress paramInetAddress, DHTNetworkPosition paramDHTNetworkPosition);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/netcoords/DHTNetworkPositionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */