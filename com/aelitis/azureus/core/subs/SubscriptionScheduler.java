package com.aelitis.azureus.core.subs;

public abstract interface SubscriptionScheduler
{
  public abstract boolean download(Subscription paramSubscription, boolean paramBoolean)
    throws SubscriptionException;
  
  public abstract void downloadAsync(Subscription paramSubscription, boolean paramBoolean)
    throws SubscriptionException;
  
  public abstract void download(Subscription paramSubscription, boolean paramBoolean, SubscriptionDownloadListener paramSubscriptionDownloadListener)
    throws SubscriptionException;
  
  public abstract void download(Subscription paramSubscription, SubscriptionResult paramSubscriptionResult);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionScheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */