package com.aelitis.azureus.core.dht.transport.udp;

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import java.net.InetSocketAddress;

public abstract interface DHTTransportUDPContact
  extends DHTTransportContact
{
  public abstract void setTransportAddress(InetSocketAddress paramInetSocketAddress);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/DHTTransportUDPContact.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */