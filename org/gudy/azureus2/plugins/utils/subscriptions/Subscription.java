package org.gudy.azureus2.plugins.utils.subscriptions;

public abstract interface Subscription
{
  public abstract String getID();
  
  public abstract String getName();
  
  public abstract boolean isSearchTemplate();
  
  public abstract SubscriptionResult[] getResults();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/subscriptions/Subscription.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */