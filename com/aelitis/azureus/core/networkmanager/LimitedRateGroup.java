package com.aelitis.azureus.core.networkmanager;

public abstract interface LimitedRateGroup
{
  public abstract String getName();
  
  public abstract int getRateLimitBytesPerSecond();
  
  public abstract void updateBytesUsed(int paramInt);
  
  public abstract boolean isDisabled();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/LimitedRateGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */