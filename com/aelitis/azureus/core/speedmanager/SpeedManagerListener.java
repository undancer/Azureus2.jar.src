package com.aelitis.azureus.core.speedmanager;

public abstract interface SpeedManagerListener
{
  public static final int PR_ASN = 1;
  public static final int PR_UP_CAPACITY = 2;
  public static final int PR_DOWN_CAPACITY = 3;
  
  public abstract void propertyChanged(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/SpeedManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */