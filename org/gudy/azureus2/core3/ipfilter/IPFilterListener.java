package org.gudy.azureus2.core3.ipfilter;

public abstract interface IPFilterListener
{
  public abstract void IPFilterEnabledChanged(boolean paramBoolean);
  
  public abstract boolean canIPBeBanned(String paramString);
  
  public abstract void IPBanned(BannedIp paramBannedIp);
  
  public abstract void IPBlockedListChanged(IpFilter paramIpFilter);
  
  public abstract boolean canIPBeBlocked(String paramString, byte[] paramArrayOfByte);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/IPFilterListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */