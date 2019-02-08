package com.aelitis.azureus.core.peermanager.control;

public abstract interface PeerControlInstance
{
  public abstract void schedule();
  
  public abstract int getSchedulePriority();
  
  public abstract String getName();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/control/PeerControlInstance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */