package org.gudy.azureus2.plugins.utils;

public abstract interface Monitor
{
  public abstract void enter();
  
  public abstract void exit();
  
  public abstract boolean isOwned();
  
  public abstract boolean hasWaiters();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/Monitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */