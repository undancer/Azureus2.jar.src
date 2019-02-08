package com.aelitis.azureus.core.dht.transport;

public abstract interface DHTTransportFullStats
{
  public abstract long getDBValueCount();
  
  public abstract long getDBKeyCount();
  
  public abstract long getDBValuesStored();
  
  public abstract long getDBKeysBlocked();
  
  public abstract long getDBKeyDivSizeCount();
  
  public abstract long getDBKeyDivFreqCount();
  
  public abstract long getDBStoreSize();
  
  public abstract long getRouterNodes();
  
  public abstract long getRouterLeaves();
  
  public abstract long getRouterContacts();
  
  public abstract long getRouterUptime();
  
  public abstract int getRouterCount();
  
  public abstract long getTotalBytesReceived();
  
  public abstract long getTotalBytesSent();
  
  public abstract long getTotalPacketsReceived();
  
  public abstract long getTotalPacketsSent();
  
  public abstract long getTotalPingsReceived();
  
  public abstract long getTotalFindNodesReceived();
  
  public abstract long getTotalFindValuesReceived();
  
  public abstract long getTotalStoresReceived();
  
  public abstract long getTotalKeyBlocksReceived();
  
  public abstract long getIncomingRequests();
  
  public abstract long getAverageBytesReceived();
  
  public abstract long getAverageBytesSent();
  
  public abstract long getAveragePacketsReceived();
  
  public abstract long getAveragePacketsSent();
  
  public abstract String getVersion();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportFullStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */