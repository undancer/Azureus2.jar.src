package com.aelitis.azureus.core.speedmanager;

import java.net.InetSocketAddress;

public abstract interface SpeedManagerPingSource
{
  public abstract InetSocketAddress getAddress();
  
  public abstract int getPingTime();
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/SpeedManagerPingSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */