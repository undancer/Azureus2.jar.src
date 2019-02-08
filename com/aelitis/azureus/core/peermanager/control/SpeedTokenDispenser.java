package com.aelitis.azureus.core.peermanager.control;

public abstract interface SpeedTokenDispenser
{
  public abstract int dispense(int paramInt1, int paramInt2);
  
  public abstract void returnUnusedChunks(int paramInt1, int paramInt2);
  
  public abstract int peek(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/control/SpeedTokenDispenser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */