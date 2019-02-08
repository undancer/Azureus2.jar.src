package com.aelitis.azureus.core.dht.transport;

public abstract interface DHTTransportStats
{
  public static final int STAT_SENT = 0;
  public static final int STAT_OK = 1;
  public static final int STAT_FAILED = 2;
  public static final int STAT_RECEIVED = 3;
  public static final int AT_FIND_NODE = 0;
  public static final int AT_FIND_VALUE = 1;
  public static final int AT_PING = 2;
  public static final int AT_STATS = 3;
  public static final int AT_STORE = 4;
  public static final int AT_KEY_BLOCK = 5;
  public static final int AT_QUERY_STORE = 6;
  
  public abstract long[] getPings();
  
  public abstract long[] getFindNodes();
  
  public abstract long[] getFindValues();
  
  public abstract long[] getStores();
  
  public abstract long[] getQueryStores();
  
  public abstract long[] getData();
  
  public abstract long[] getKeyBlocks();
  
  public abstract long[] getAliens();
  
  public abstract long getIncomingRequests();
  
  public abstract long getPacketsSent();
  
  public abstract long getPacketsReceived();
  
  public abstract long getRequestsTimedOut();
  
  public abstract long getBytesSent();
  
  public abstract long getBytesReceived();
  
  public abstract DHTTransportStats snapshot();
  
  public abstract long getSkewAverage();
  
  public abstract int getRouteablePercentage();
  
  public abstract int[] getRTTHistory();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */