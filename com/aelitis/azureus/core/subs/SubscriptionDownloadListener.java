package com.aelitis.azureus.core.subs;

public abstract interface SubscriptionDownloadListener
{
  public abstract void complete(Subscription paramSubscription);
  
  public abstract void failed(Subscription paramSubscription, SubscriptionException paramSubscriptionException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionDownloadListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */