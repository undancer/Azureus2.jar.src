package com.aelitis.net.udp.uc;

public abstract interface PRUDPPacketHandlerStats
{
  public abstract long getPacketsSent();
  
  public abstract long getPacketsReceived();
  
  public abstract long getRequestsTimedOut();
  
  public abstract long getBytesSent();
  
  public abstract long getBytesReceived();
  
  public abstract long getSendQueueLength();
  
  public abstract long getReceiveQueueLength();
  
  public abstract PRUDPPacketHandlerStats snapshot();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/PRUDPPacketHandlerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */