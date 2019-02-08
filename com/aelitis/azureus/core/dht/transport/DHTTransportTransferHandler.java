package com.aelitis.azureus.core.dht.transport;

public abstract interface DHTTransportTransferHandler
{
  public abstract String getName();
  
  public abstract byte[] handleRead(DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte);
  
  public abstract byte[] handleWrite(DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportTransferHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */