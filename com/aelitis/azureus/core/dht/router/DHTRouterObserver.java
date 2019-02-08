package com.aelitis.azureus.core.dht.router;

public abstract interface DHTRouterObserver
{
  public abstract void added(DHTRouterContact paramDHTRouterContact);
  
  public abstract void removed(DHTRouterContact paramDHTRouterContact);
  
  public abstract void locationChanged(DHTRouterContact paramDHTRouterContact);
  
  public abstract void nowAlive(DHTRouterContact paramDHTRouterContact);
  
  public abstract void nowFailing(DHTRouterContact paramDHTRouterContact);
  
  public abstract void destroyed(DHTRouter paramDHTRouter);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/DHTRouterObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */