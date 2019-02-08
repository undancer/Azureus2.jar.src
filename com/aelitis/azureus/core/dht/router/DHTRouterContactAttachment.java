package com.aelitis.azureus.core.dht.router;

public abstract interface DHTRouterContactAttachment
{
  public abstract void setRouterContact(DHTRouterContact paramDHTRouterContact);
  
  public abstract int getMaxFailForLiveCount();
  
  public abstract int getMaxFailForUnknownCount();
  
  public abstract int getInstanceID();
  
  public abstract boolean isSleeping();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/DHTRouterContactAttachment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */