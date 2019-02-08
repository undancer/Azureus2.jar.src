package com.aelitis.azureus.core.dht.transport.udp.impl.packethandler;

import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketReply;
import java.net.InetSocketAddress;

public abstract interface DHTUDPPacketHandlerStub
{
  public abstract void send(DHTUDPPacketReply paramDHTUDPPacketReply, InetSocketAddress paramInetSocketAddress)
    throws DHTUDPPacketHandlerException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/packethandler/DHTUDPPacketHandlerStub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */