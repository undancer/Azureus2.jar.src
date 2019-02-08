package com.aelitis.net.udp.uc;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;

public abstract interface PRUDPPacketHandler
{
  public static final int PRIORITY_LOW = 2;
  public static final int PRIORITY_MEDIUM = 1;
  public static final int PRIORITY_HIGH = 0;
  public static final int PRIORITY_IMMEDIATE = 99;
  
  public abstract void sendAndReceive(PRUDPPacket paramPRUDPPacket, InetSocketAddress paramInetSocketAddress, PRUDPPacketReceiver paramPRUDPPacketReceiver, long paramLong, int paramInt)
    throws PRUDPPacketHandlerException;
  
  public abstract PRUDPPacket sendAndReceive(PasswordAuthentication paramPasswordAuthentication, PRUDPPacket paramPRUDPPacket, InetSocketAddress paramInetSocketAddress)
    throws PRUDPPacketHandlerException;
  
  public abstract PRUDPPacket sendAndReceive(PasswordAuthentication paramPasswordAuthentication, PRUDPPacket paramPRUDPPacket, InetSocketAddress paramInetSocketAddress, long paramLong)
    throws PRUDPPacketHandlerException;
  
  public abstract PRUDPPacket sendAndReceive(PasswordAuthentication paramPasswordAuthentication, PRUDPPacket paramPRUDPPacket, InetSocketAddress paramInetSocketAddress, long paramLong, int paramInt)
    throws PRUDPPacketHandlerException;
  
  public abstract void send(PRUDPPacket paramPRUDPPacket, InetSocketAddress paramInetSocketAddress)
    throws PRUDPPacketHandlerException;
  
  public abstract PRUDPRequestHandler getRequestHandler();
  
  public abstract void setRequestHandler(PRUDPRequestHandler paramPRUDPRequestHandler);
  
  public abstract void primordialSend(byte[] paramArrayOfByte, InetSocketAddress paramInetSocketAddress)
    throws PRUDPPacketHandlerException;
  
  public abstract boolean hasPrimordialHandler();
  
  public abstract void addPrimordialHandler(PRUDPPrimordialHandler paramPRUDPPrimordialHandler);
  
  public abstract void removePrimordialHandler(PRUDPPrimordialHandler paramPRUDPPrimordialHandler);
  
  public abstract int getPort();
  
  public abstract InetAddress getBindIP();
  
  public abstract void setDelays(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void setExplicitBindAddress(InetAddress paramInetAddress);
  
  public abstract PRUDPPacketHandlerStats getStats();
  
  public abstract PRUDPPacketHandler openSession(InetSocketAddress paramInetSocketAddress)
    throws PRUDPPacketHandlerException;
  
  public abstract void closeSession()
    throws PRUDPPacketHandlerException;
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/PRUDPPacketHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */