package com.aelitis.azureus.core.subs;

public abstract interface SubscriptionResultFilter
{
  public abstract long getMinSze();
  
  public abstract long getMaxSize();
  
  public abstract String[] getWithWords();
  
  public abstract String[] getWithoutWords();
  
  public abstract void update(String[] paramArrayOfString1, String[] paramArrayOfString2, long paramLong1, long paramLong2)
    throws SubscriptionException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/SubscriptionResultFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */