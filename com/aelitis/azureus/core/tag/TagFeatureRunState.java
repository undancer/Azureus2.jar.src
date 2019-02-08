package com.aelitis.azureus.core.tag;

public abstract interface TagFeatureRunState
  extends TagFeatureRateLimit
{
  public static final int RSC_STOP = 1;
  public static final int RSC_PAUSE = 2;
  public static final int RSC_RESUME = 4;
  public static final int RSC_START = 8;
  public static final int RSC_NONE = 0;
  public static final int RSC_ALL = -1;
  public static final int RSC_STOP_PAUSE = 3;
  public static final int RSC_START_STOP_PAUSE = 11;
  
  public abstract int getRunStateCapabilities();
  
  public abstract boolean hasRunStateCapability(int paramInt);
  
  public abstract boolean[] getPerformableOperations(int[] paramArrayOfInt);
  
  public abstract void performOperation(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagFeatureRunState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */