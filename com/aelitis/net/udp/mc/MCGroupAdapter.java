package com.aelitis.net.udp.mc;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

public abstract interface MCGroupAdapter
{
  public abstract void received(NetworkInterface paramNetworkInterface, InetAddress paramInetAddress, InetSocketAddress paramInetSocketAddress, byte[] paramArrayOfByte, int paramInt);
  
  public abstract void interfaceChanged(NetworkInterface paramNetworkInterface);
  
  public abstract void trace(String paramString);
  
  public abstract void log(Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/mc/MCGroupAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */