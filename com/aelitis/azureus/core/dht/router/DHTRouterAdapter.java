package com.aelitis.azureus.core.dht.router;

public abstract interface DHTRouterAdapter
{
  public abstract void requestAdd(DHTRouterContact paramDHTRouterContact);
  
  public abstract void requestPing(DHTRouterContact paramDHTRouterContact);
  
  public abstract void requestLookup(byte[] paramArrayOfByte, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/DHTRouterAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */