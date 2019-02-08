package com.aelitis.azureus.core.proxy;

import java.net.InetSocketAddress;
import java.net.Proxy;

public abstract interface AEProxySelector
{
  public abstract Proxy getActiveProxy();
  
  public abstract void startNoProxy();
  
  public abstract void endNoProxy();
  
  public abstract Proxy setProxy(InetSocketAddress paramInetSocketAddress, Proxy paramProxy);
  
  public abstract Proxy removeProxy(InetSocketAddress paramInetSocketAddress);
  
  public abstract Proxy getSOCKSProxy(InetSocketAddress paramInetSocketAddress1, InetSocketAddress paramInetSocketAddress2);
  
  public abstract Proxy getSOCKSProxy(String paramString, int paramInt, InetSocketAddress paramInetSocketAddress);
  
  public abstract void connectFailed(Proxy paramProxy, Throwable paramThrowable);
  
  public abstract long getLastConnectionTime();
  
  public abstract int getConnectionCount();
  
  public abstract long getLastFailTime();
  
  public abstract int getFailCount();
  
  public abstract String getInfo();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/AEProxySelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */