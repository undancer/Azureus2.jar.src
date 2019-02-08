package com.aelitis.azureus.core.dht.router;

public abstract interface DHTRouterContact
{
  public abstract byte[] getID();
  
  public abstract DHTRouterContactAttachment getAttachment();
  
  public abstract boolean hasBeenAlive();
  
  public abstract boolean isFailing();
  
  public abstract boolean isAlive();
  
  public abstract long getTimeAlive();
  
  public abstract String getString();
  
  public abstract boolean isBucketEntry();
  
  public abstract boolean isReplacement();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/DHTRouterContact.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */