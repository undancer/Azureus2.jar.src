package com.aelitis.azureus.core.peer.cache;

import java.net.InetAddress;

public abstract interface CachePeer
{
  public static final int PT_NONE = 1;
  public static final int PT_CACHE_LOGIC = 2;
  
  public abstract int getType();
  
  public abstract InetAddress getAddress();
  
  public abstract int getPort();
  
  public abstract long getCreateTime(long paramLong);
  
  public abstract long getInjectTime(long paramLong);
  
  public abstract void setInjectTime(long paramLong);
  
  public abstract long getSpeedChangeTime(long paramLong);
  
  public abstract void setSpeedChangeTime(long paramLong);
  
  public abstract boolean getAutoReconnect();
  
  public abstract void setAutoReconnect(boolean paramBoolean);
  
  public abstract boolean sameAs(CachePeer paramCachePeer);
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peer/cache/CachePeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */