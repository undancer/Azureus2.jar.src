package com.aelitis.azureus.core.dht.transport.udp.impl.packethandler;

import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketReply;
import java.net.InetSocketAddress;

public abstract interface DHTUDPPacketReceiver
{
  public abstract void packetReceived(DHTUDPPacketReply paramDHTUDPPacketReply, InetSocketAddress paramInetSocketAddress, long paramLong);
  
  public abstract void error(DHTUDPPacketHandlerException paramDHTUDPPacketHandlerException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/packethandler/DHTUDPPacketReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */