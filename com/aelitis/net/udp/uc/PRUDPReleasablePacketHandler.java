package com.aelitis.net.udp.uc;

public abstract interface PRUDPReleasablePacketHandler
{
  public abstract PRUDPPacketHandler getHandler();
  
  public abstract void release();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/PRUDPReleasablePacketHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */