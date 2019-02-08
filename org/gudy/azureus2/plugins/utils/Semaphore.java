package org.gudy.azureus2.plugins.utils;

public abstract interface Semaphore
{
  public abstract void reserve();
  
  public abstract boolean reserveIfAvailable();
  
  public abstract boolean reserve(long paramLong);
  
  public abstract void release();
  
  public abstract void releaseAllWaiters();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/Semaphore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */