package com.aelitis.azureus.core.proxy;

public abstract interface AEProxy
{
  public abstract int getPort();
  
  public abstract void setAllowExternalConnections(boolean paramBoolean);
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/AEProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */