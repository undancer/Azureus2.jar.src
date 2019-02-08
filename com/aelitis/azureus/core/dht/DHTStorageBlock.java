package com.aelitis.azureus.core.dht;

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;

public abstract interface DHTStorageBlock
{
  public abstract byte[] getKey();
  
  public abstract byte[] getRequest();
  
  public abstract byte[] getCertificate();
  
  public abstract boolean hasBeenSentTo(DHTTransportContact paramDHTTransportContact);
  
  public abstract void sentTo(DHTTransportContact paramDHTTransportContact);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/DHTStorageBlock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */