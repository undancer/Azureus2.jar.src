package org.gudy.azureus2.plugins.ipfilter;

public abstract interface IPBlocked
{
  public abstract String getBlockedIP();
  
  public abstract String getBlockedTorrentName();
  
  public abstract long getBlockedTime();
  
  public abstract IPRange getBlockingRange();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ipfilter/IPBlocked.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */