package com.aelitis.azureus.core.proxy;

import java.io.IOException;

public abstract interface AEProxyHandler
{
  public abstract AEProxyState getInitialState(AEProxyConnection paramAEProxyConnection)
    throws IOException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/AEProxyHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */