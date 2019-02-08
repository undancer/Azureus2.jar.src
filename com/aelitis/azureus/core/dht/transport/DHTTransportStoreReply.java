package com.aelitis.azureus.core.dht.transport;

public abstract interface DHTTransportStoreReply
{
  public abstract byte[] getDiversificationTypes();
  
  public abstract boolean blocked();
  
  public abstract byte[] getBlockRequest();
  
  public abstract byte[] getBlockSignature();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportStoreReply.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */