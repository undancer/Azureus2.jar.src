package com.aelitis.azureus.core.dht.transport.udp.impl.packethandler;

import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketRequest;

public abstract interface DHTUDPRequestHandler
{
  public abstract void process(DHTUDPPacketRequest paramDHTUDPPacketRequest, boolean paramBoolean);
  
  public abstract void process(DHTUDPPacketHandlerStub paramDHTUDPPacketHandlerStub, DHTUDPPacketRequest paramDHTUDPPacketRequest, boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/packethandler/DHTUDPRequestHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */