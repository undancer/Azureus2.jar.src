package com.aelitis.net.udp.mc;

import java.net.InetSocketAddress;

public abstract interface MCGroup
{
  public abstract int getControlPort();
  
  public abstract void sendToGroup(byte[] paramArrayOfByte);
  
  public abstract void sendToGroup(String paramString);
  
  public abstract void sendToMember(InetSocketAddress paramInetSocketAddress, byte[] paramArrayOfByte)
    throws MCGroupException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/mc/MCGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */