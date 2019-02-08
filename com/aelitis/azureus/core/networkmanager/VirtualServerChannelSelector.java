package com.aelitis.azureus.core.networkmanager;

import java.net.InetAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public abstract interface VirtualServerChannelSelector
{
  public abstract void start();
  
  public abstract void stop();
  
  public abstract boolean isRunning();
  
  public abstract InetAddress getBoundToAddress();
  
  public abstract int getPort();
  
  public abstract long getTimeOfLastAccept();
  
  public static abstract interface SelectListener
  {
    public abstract void newConnectionAccepted(ServerSocketChannel paramServerSocketChannel, SocketChannel paramSocketChannel);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/VirtualServerChannelSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */