package com.aelitis.azureus.core.instancemanager;

public abstract interface AZInstanceTracked
{
  public abstract AZInstance getInstance();
  
  public abstract TrackTarget getTarget();
  
  public abstract boolean isSeed();
  
  public static abstract interface TrackTarget
  {
    public abstract Object getTarget();
    
    public abstract boolean isSeed();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/instancemanager/AZInstanceTracked.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */