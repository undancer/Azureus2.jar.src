package com.aelitis.azureus.core.dht.transport;

public abstract interface DHTTransportListener
{
  public abstract void localContactChanged(DHTTransportContact paramDHTTransportContact);
  
  public abstract void resetNetworkPositions();
  
  public abstract void currentAddress(String paramString);
  
  public abstract void reachabilityChanged(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */