package org.gudy.azureus2.plugins.network;

public abstract interface RateLimiter
{
  public abstract String getName();
  
  public abstract int getRateLimitBytesPerSecond();
  
  public abstract void setRateLimitBytesPerSecond(int paramInt);
  
  public abstract long getRateLimitTotalByteCount();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/network/RateLimiter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */