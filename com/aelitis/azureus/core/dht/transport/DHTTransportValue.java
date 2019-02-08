package com.aelitis.azureus.core.dht.transport;

public abstract interface DHTTransportValue
{
  public abstract boolean isLocal();
  
  public abstract long getCreationTime();
  
  public abstract byte[] getValue();
  
  public abstract int getVersion();
  
  public abstract DHTTransportContact getOriginator();
  
  public abstract int getFlags();
  
  public abstract int getLifeTimeHours();
  
  public abstract byte getReplicationControl();
  
  public abstract byte getReplicationFactor();
  
  public abstract byte getReplicationFrequencyHours();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */