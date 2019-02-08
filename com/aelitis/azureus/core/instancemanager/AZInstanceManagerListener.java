package com.aelitis.azureus.core.instancemanager;

public abstract interface AZInstanceManagerListener
{
  public abstract void instanceFound(AZInstance paramAZInstance);
  
  public abstract void instanceChanged(AZInstance paramAZInstance);
  
  public abstract void instanceLost(AZInstance paramAZInstance);
  
  public abstract void instanceTracked(AZInstanceTracked paramAZInstanceTracked);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/AZInstanceManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */