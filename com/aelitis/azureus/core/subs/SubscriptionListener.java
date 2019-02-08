package com.aelitis.azureus.core.subs;

public abstract interface SubscriptionListener
{
  public static final int CR_METADATA = 1;
  public static final int CR_RESULTS = 2;
  
  public abstract void subscriptionChanged(Subscription paramSubscription, int paramInt);
  
  public abstract void subscriptionDownloaded(Subscription paramSubscription, boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */