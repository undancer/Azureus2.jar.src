package org.gudy.azureus2.core3.ipfilter;

public abstract interface BlockedIp
{
  public abstract String getBlockedIp();
  
  public abstract long getBlockedTime();
  
  public abstract IpRange getBlockingRange();
  
  public abstract String getTorrentName();
  
  public abstract boolean isLoggable();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/BlockedIp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */