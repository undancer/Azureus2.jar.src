package com.aelitis.azureus.core.networkmanager.impl.udp;

import java.net.InetSocketAddress;

public abstract interface NetworkGlueListener
{
  public abstract void receive(int paramInt1, InetSocketAddress paramInetSocketAddress, byte[] paramArrayOfByte, int paramInt2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/NetworkGlueListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */