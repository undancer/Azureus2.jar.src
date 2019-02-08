package org.gudy.azureus2.plugins.utils.subscriptions;

import java.net.URL;
import java.util.Map;
import org.gudy.azureus2.plugins.utils.search.SearchProvider;

public abstract interface SubscriptionManager
{
  public static final String SO_ANONYMOUS = "_anonymous_";
  public static final String SO_NAME = "t";
  
  public abstract Subscription[] getSubscriptions();
  
  public abstract void requestSubscription(URL paramURL);
  
  public abstract void requestSubscription(URL paramURL, Map<String, Object> paramMap);
  
  public abstract void requestSubscription(SearchProvider paramSearchProvider, Map<String, Object> paramMap)
    throws SubscriptionException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/subscriptions/SubscriptionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */