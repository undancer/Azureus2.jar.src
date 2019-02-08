package org.gudy.azureus2.plugins.network;

public abstract interface ConnectionStub
{
  public abstract void addRateLimiter(RateLimiter paramRateLimiter, boolean paramBoolean);
  
  public abstract void removeRateLimiter(RateLimiter paramRateLimiter, boolean paramBoolean);
  
  public abstract RateLimiter[] getRateLimiters(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/network/ConnectionStub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */