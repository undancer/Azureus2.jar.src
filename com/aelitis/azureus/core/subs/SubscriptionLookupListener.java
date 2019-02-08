package com.aelitis.azureus.core.subs;

public abstract interface SubscriptionLookupListener
{
  public abstract void found(byte[] paramArrayOfByte, Subscription paramSubscription);
  
  public abstract void complete(byte[] paramArrayOfByte, Subscription[] paramArrayOfSubscription);
  
  public abstract void failed(byte[] paramArrayOfByte, SubscriptionException paramSubscriptionException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionLookupListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */