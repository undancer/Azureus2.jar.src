package com.aelitis.azureus.core.networkmanager.impl.udp;

import java.io.IOException;
import java.net.InetSocketAddress;

public abstract interface NetworkGlue
{
  public abstract int send(int paramInt, InetSocketAddress paramInetSocketAddress, byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract long[] getStats();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/NetworkGlue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */