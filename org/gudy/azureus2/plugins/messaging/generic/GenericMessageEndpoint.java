package org.gudy.azureus2.plugins.messaging.generic;

import java.net.InetSocketAddress;

public abstract interface GenericMessageEndpoint
{
  public abstract InetSocketAddress getNotionalAddress();
  
  public abstract void addTCP(InetSocketAddress paramInetSocketAddress);
  
  public abstract InetSocketAddress getTCP();
  
  public abstract void addUDP(InetSocketAddress paramInetSocketAddress);
  
  public abstract InetSocketAddress getUDP();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/generic/GenericMessageEndpoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */