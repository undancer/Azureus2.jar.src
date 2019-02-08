package org.gudy.azureus2.core3.ipfilter;

public abstract interface BadIps
{
  public abstract int addWarningForIp(String paramString);
  
  public abstract int getNbWarningForIp(String paramString);
  
  public abstract int getNbBadIps();
  
  public abstract BadIp[] getBadIps();
  
  public abstract void clearBadIps();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/BadIps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */