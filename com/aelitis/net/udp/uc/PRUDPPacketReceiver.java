package com.aelitis.net.udp.uc;

import java.net.InetSocketAddress;

public abstract interface PRUDPPacketReceiver
{
  public abstract void packetReceived(PRUDPPacketHandlerRequest paramPRUDPPacketHandlerRequest, PRUDPPacket paramPRUDPPacket, InetSocketAddress paramInetSocketAddress);
  
  public abstract void error(PRUDPPacketHandlerException paramPRUDPPacketHandlerException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/PRUDPPacketReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */