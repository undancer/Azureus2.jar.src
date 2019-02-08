package com.aelitis.azureus.core.networkmanager;

public abstract interface TransportBase
{
  public abstract boolean isReadyForWrite(EventWaiter paramEventWaiter);
  
  public abstract long isReadyForRead(EventWaiter paramEventWaiter);
  
  public abstract boolean isTCP();
  
  public abstract String getDescription();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/TransportBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */