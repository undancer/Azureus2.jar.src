package com.aelitis.net.udp.uc;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public abstract interface PRUDPPacketReplyDecoder
{
  public abstract PRUDPPacketReply decode(PRUDPPacketHandler paramPRUDPPacketHandler, InetSocketAddress paramInetSocketAddress, DataInputStream paramDataInputStream, int paramInt1, int paramInt2)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/PRUDPPacketReplyDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */