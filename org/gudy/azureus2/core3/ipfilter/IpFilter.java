package org.gudy.azureus2.core3.ipfilter;

import java.io.File;
import java.net.InetAddress;
import java.util.List;

public abstract interface IpFilter
{
  public abstract File getFile();
  
  public abstract void save()
    throws Exception;
  
  public abstract void reload()
    throws Exception;
  
  /**
   * @deprecated
   */
  public abstract List getIpRanges();
  
  public abstract IpRange[] getRanges();
  
  public abstract boolean isInRange(String paramString);
  
  public abstract boolean isInRange(String paramString1, String paramString2, byte[] paramArrayOfByte);
  
  public abstract boolean isInRange(String paramString1, String paramString2, byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract boolean isInRange(InetAddress paramInetAddress, String paramString, byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract IpRange createRange(boolean paramBoolean);
  
  public abstract void addRange(IpRange paramIpRange);
  
  public abstract void removeRange(IpRange paramIpRange);
  
  public abstract int getNbRanges();
  
  public abstract int getNbIpsBlocked();
  
  public abstract int getNbIpsBlockedAndLoggable();
  
  public abstract BlockedIp[] getBlockedIps();
  
  public abstract void clearBlockedIPs();
  
  public abstract boolean ban(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract boolean ban(String paramString1, String paramString2, boolean paramBoolean, int paramInt);
  
  public abstract void unban(String paramString);
  
  public abstract void unban(String paramString, boolean paramBoolean);
  
  public abstract int getNbBannedIps();
  
  public abstract BannedIp[] getBannedIps();
  
  public abstract void clearBannedIps();
  
  public abstract void addExcludedHash(byte[] paramArrayOfByte);
  
  public abstract void removeExcludedHash(byte[] paramArrayOfByte);
  
  public abstract boolean isEnabled();
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract boolean getInRangeAddressesAreAllowed();
  
  public abstract void setInRangeAddressesAreAllowed(boolean paramBoolean);
  
  public abstract void markAsUpToDate();
  
  public abstract long getLastUpdateTime();
  
  public abstract long getTotalAddressesInRange();
  
  public abstract void addListener(IPFilterListener paramIPFilterListener);
  
  public abstract void removeListener(IPFilterListener paramIPFilterListener);
  
  public abstract void addExternalHandler(IpFilterExternalHandler paramIpFilterExternalHandler);
  
  public abstract void removeExternalHandler(IpFilterExternalHandler paramIpFilterExternalHandler);
  
  public abstract void reloadSync()
    throws Exception;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/IpFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */