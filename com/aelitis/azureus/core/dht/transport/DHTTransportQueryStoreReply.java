package com.aelitis.azureus.core.dht.transport;

import java.util.List;

public abstract interface DHTTransportQueryStoreReply
{
  public abstract int getHeaderSize();
  
  public abstract List<byte[]> getEntries();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportQueryStoreReply.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */