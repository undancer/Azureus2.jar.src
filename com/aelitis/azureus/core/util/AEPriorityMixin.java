package com.aelitis.azureus.core.util;

public abstract interface AEPriorityMixin
{
  public static final int PRIORITY_LOW = 1;
  public static final int PRIORITY_NORMAL = 2;
  public static final int PRIORITY_HIGH = 3;
  
  public abstract int getPriority();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/AEPriorityMixin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */