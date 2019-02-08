package org.gudy.azureus2.core3.ipfilter;

public abstract interface BadIp
{
  public abstract String getIp();
  
  public abstract int getNumberOfWarnings();
  
  public abstract long getLastTime();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/BadIp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */