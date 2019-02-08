package com.aelitis.azureus.core.subs;

public abstract interface SubscriptionPopularityListener
{
  public abstract void gotPopularity(long paramLong);
  
  public abstract void failed(SubscriptionException paramSubscriptionException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionPopularityListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */