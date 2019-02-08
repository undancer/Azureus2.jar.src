package com.aelitis.azureus.core.dht.transport;

import java.util.List;

public abstract interface DHTTransportRequestHandler
{
  public abstract void pingRequest(DHTTransportContact paramDHTTransportContact);
  
  public abstract void keyBlockRequest(DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  public abstract DHTTransportFullStats statsRequest(DHTTransportContact paramDHTTransportContact);
  
  public abstract DHTTransportStoreReply storeRequest(DHTTransportContact paramDHTTransportContact, byte[][] paramArrayOfByte, DHTTransportValue[][] paramArrayOfDHTTransportValue);
  
  public abstract DHTTransportQueryStoreReply queryStoreRequest(DHTTransportContact paramDHTTransportContact, int paramInt, List<Object[]> paramList);
  
  public abstract DHTTransportContact[] findNodeRequest(DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte);
  
  public abstract DHTTransportFindValueReply findValueRequest(DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte, int paramInt, short paramShort);
  
  public abstract void contactImported(DHTTransportContact paramDHTTransportContact, boolean paramBoolean);
  
  public abstract void contactRemoved(DHTTransportContact paramDHTTransportContact);
  
  public abstract int getTransportEstimatedDHTSize();
  
  public abstract void setTransportEstimatedDHTSize(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportRequestHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */