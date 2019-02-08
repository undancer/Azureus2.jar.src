package com.aelitis.azureus.core.dht.transport;

import java.util.Map;

public abstract interface DHTTransportAlternativeContact
{
  public abstract int getNetworkType();
  
  public abstract int getVersion();
  
  public abstract int getID();
  
  /**
   * @deprecated
   */
  public abstract int getLastAlive();
  
  public abstract int getAge();
  
  public abstract Map<String, Object> getProperties();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportAlternativeContact.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */