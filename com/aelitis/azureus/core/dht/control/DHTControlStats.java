package com.aelitis.azureus.core.dht.control;

import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;

public abstract interface DHTControlStats
  extends DHTTransportFullStats
{
  public abstract long getRouterUptime();
  
  public abstract int getRouterCount();
  
  public abstract long getEstimatedDHTSize();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/control/DHTControlStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */