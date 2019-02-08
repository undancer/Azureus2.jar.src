package com.aelitis.azureus.core.subs;

import java.net.URL;
import java.util.Map;

public abstract interface SubscriptionManagerListener
{
  public abstract void subscriptionAdded(Subscription paramSubscription);
  
  public abstract void subscriptionChanged(Subscription paramSubscription);
  
  public abstract void subscriptionSelected(Subscription paramSubscription);
  
  public abstract void subscriptionRemoved(Subscription paramSubscription);
  
  public abstract void associationsChanged(byte[] paramArrayOfByte);
  
  public abstract void subscriptionRequested(URL paramURL, Map<String, Object> paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */