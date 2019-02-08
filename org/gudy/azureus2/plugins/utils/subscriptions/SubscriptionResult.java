package org.gudy.azureus2.plugins.utils.subscriptions;

public abstract interface SubscriptionResult
{
  public abstract Object getProperty(int paramInt);
  
  public abstract boolean isRead();
  
  public abstract void setRead(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/subscriptions/SubscriptionResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */