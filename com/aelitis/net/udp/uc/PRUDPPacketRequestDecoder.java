package com.aelitis.net.udp.uc;

import java.io.DataInputStream;
import java.io.IOException;

public abstract interface PRUDPPacketRequestDecoder
{
  public abstract PRUDPPacketRequest decode(PRUDPPacketHandler paramPRUDPPacketHandler, DataInputStream paramDataInputStream, long paramLong, int paramInt1, int paramInt2)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/PRUDPPacketRequestDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */