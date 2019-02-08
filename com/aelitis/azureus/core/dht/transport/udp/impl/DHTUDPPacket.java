package com.aelitis.azureus.core.dht.transport.udp.impl;

public abstract interface DHTUDPPacket
{
  public abstract DHTTransportUDPImpl getTransport();
  
  public abstract byte getProtocolVersion();
  
  public abstract byte getGenericFlags();
  
  public abstract byte getGenericFlags2();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */