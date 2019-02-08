package com.aelitis.azureus.core.dht.control;

import com.aelitis.azureus.core.dht.router.DHTRouterContact;
import com.aelitis.azureus.core.dht.transport.DHTTransportContact;

public abstract interface DHTControlContact
{
  public abstract DHTRouterContact getRouterContact();
  
  public abstract DHTTransportContact getTransportContact();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/control/DHTControlContact.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */