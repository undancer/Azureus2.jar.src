package com.aelitis.azureus.core.dht.db;

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import com.aelitis.azureus.core.dht.transport.DHTTransportValue;

public abstract interface DHTDBValue
  extends DHTTransportValue
{
  public abstract void setFlags(byte paramByte);
  
  public abstract DHTDBValue getValueForRelay(DHTTransportContact paramDHTTransportContact);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/db/DHTDBValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */