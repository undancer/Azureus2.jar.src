package org.gudy.azureus2.plugins.utils;

public abstract interface AggregatedDispatcher
{
  public abstract void add(Runnable paramRunnable);
  
  public abstract Runnable remove(Runnable paramRunnable);
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/AggregatedDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */