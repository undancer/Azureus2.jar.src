package com.aelitis.azureus.core.dht.transport;

import java.util.List;

public abstract interface DHTTransportReplyHandler
{
  public abstract void pingReply(DHTTransportContact paramDHTTransportContact, int paramInt);
  
  public abstract void statsReply(DHTTransportContact paramDHTTransportContact, DHTTransportFullStats paramDHTTransportFullStats);
  
  public abstract void storeReply(DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte);
  
  public abstract void queryStoreReply(DHTTransportContact paramDHTTransportContact, List<byte[]> paramList);
  
  public abstract void findNodeReply(DHTTransportContact paramDHTTransportContact, DHTTransportContact[] paramArrayOfDHTTransportContact);
  
  public abstract void findValueReply(DHTTransportContact paramDHTTransportContact, DHTTransportValue[] paramArrayOfDHTTransportValue, byte paramByte, boolean paramBoolean);
  
  public abstract void findValueReply(DHTTransportContact paramDHTTransportContact, DHTTransportContact[] paramArrayOfDHTTransportContact);
  
  public abstract void keyBlockReply(DHTTransportContact paramDHTTransportContact);
  
  public abstract void keyBlockRequest(DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  public abstract void failed(DHTTransportContact paramDHTTransportContact, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportReplyHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */