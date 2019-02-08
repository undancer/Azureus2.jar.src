package org.gudy.azureus2.core3.ipfilter;

public abstract interface BannedIp
{
  public abstract String getIp();
  
  public abstract long getBanningTime();
  
  public abstract String getTorrentName();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/BannedIp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */