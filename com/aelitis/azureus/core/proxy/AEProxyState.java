package com.aelitis.azureus.core.proxy;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public abstract interface AEProxyState
{
  public abstract String getStateName();
  
  public abstract boolean read(SocketChannel paramSocketChannel)
    throws IOException;
  
  public abstract boolean write(SocketChannel paramSocketChannel)
    throws IOException;
  
  public abstract boolean connect(SocketChannel paramSocketChannel)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/AEProxyState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */