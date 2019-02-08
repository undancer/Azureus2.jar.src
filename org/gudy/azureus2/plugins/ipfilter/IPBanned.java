package org.gudy.azureus2.plugins.ipfilter;

public abstract interface IPBanned
{
  public abstract String getBannedIP();
  
  public abstract String getBannedTorrentName();
  
  public abstract long getBannedTime();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ipfilter/IPBanned.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */