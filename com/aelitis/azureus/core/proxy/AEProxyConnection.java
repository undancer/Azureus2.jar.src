package com.aelitis.azureus.core.proxy;

import java.nio.channels.SocketChannel;

public abstract interface AEProxyConnection
{
  public abstract String getName();
  
  public abstract SocketChannel getSourceChannel();
  
  public abstract void setReadState(AEProxyState paramAEProxyState);
  
  public abstract void setWriteState(AEProxyState paramAEProxyState);
  
  public abstract void setConnectState(AEProxyState paramAEProxyState);
  
  public abstract void requestReadSelect(SocketChannel paramSocketChannel);
  
  public abstract void cancelReadSelect(SocketChannel paramSocketChannel);
  
  public abstract void requestWriteSelect(SocketChannel paramSocketChannel);
  
  public abstract void cancelWriteSelect(SocketChannel paramSocketChannel);
  
  public abstract void requestConnectSelect(SocketChannel paramSocketChannel);
  
  public abstract void cancelConnectSelect(SocketChannel paramSocketChannel);
  
  public abstract void setConnected();
  
  public abstract void setTimeStamp();
  
  public abstract void failed(Throwable paramThrowable);
  
  public abstract void close();
  
  public abstract boolean isClosed();
  
  public abstract void addListener(AEProxyConnectionListener paramAEProxyConnectionListener);
  
  public abstract void removeListener(AEProxyConnectionListener paramAEProxyConnectionListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/AEProxyConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */