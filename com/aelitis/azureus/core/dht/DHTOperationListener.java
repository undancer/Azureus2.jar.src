package com.aelitis.azureus.core.dht;

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import com.aelitis.azureus.core.dht.transport.DHTTransportValue;

public abstract interface DHTOperationListener
{
  public abstract void searching(DHTTransportContact paramDHTTransportContact, int paramInt1, int paramInt2);
  
  public abstract boolean diversified(String paramString);
  
  public abstract void found(DHTTransportContact paramDHTTransportContact, boolean paramBoolean);
  
  public abstract void read(DHTTransportContact paramDHTTransportContact, DHTTransportValue paramDHTTransportValue);
  
  public abstract void wrote(DHTTransportContact paramDHTTransportContact, DHTTransportValue paramDHTTransportValue);
  
  public abstract void complete(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/DHTOperationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */