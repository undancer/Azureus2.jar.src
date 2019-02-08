package com.aelitis.azureus.core.tag;

public abstract interface TagFeatureNotifications
{
  public static final int NOTIFY_NONE = 0;
  public static final int NOTIFY_ON_ADD = 1;
  public static final int NOTIFY_ON_REMOVE = 2;
  
  public abstract int getPostingNotifications();
  
  public abstract void setPostingNotifications(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagFeatureNotifications.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */