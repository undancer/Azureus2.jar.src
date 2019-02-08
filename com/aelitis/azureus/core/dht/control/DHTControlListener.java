package com.aelitis.azureus.core.dht.control;

public abstract interface DHTControlListener
{
  public static final int CT_ADDED = 1;
  public static final int CT_CHANGED = 2;
  public static final int CT_REMOVED = 3;
  
  public abstract void activityChanged(DHTControlActivity paramDHTControlActivity, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/control/DHTControlListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */