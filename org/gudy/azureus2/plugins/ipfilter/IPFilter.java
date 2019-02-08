package org.gudy.azureus2.plugins.ipfilter;

import java.io.File;

public abstract interface IPFilter
{
  public abstract File getFile();
  
  public abstract IPRange createRange(boolean paramBoolean);
  
  public abstract void addRange(IPRange paramIPRange);
  
  public abstract IPRange createAndAddRange(String paramString1, String paramString2, String paramString3, boolean paramBoolean);
  
  public abstract void removeRange(IPRange paramIPRange);
  
  public abstract void reload()
    throws IPFilterException;
  
  public abstract IPRange[] getRanges();
  
  public abstract int getNumberOfRanges();
  
  public abstract boolean isInRange(String paramString);
  
  public abstract IPBlocked[] getBlockedIPs();
  
  public abstract int getNumberOfBlockedIPs();
  
  public abstract void block(String paramString);
  
  public abstract IPBanned[] getBannedIPs();
  
  public abstract int getNumberOfBannedIPs();
  
  public abstract void ban(String paramString1, String paramString2);
  
  public abstract void unban(String paramString);
  
  public abstract boolean getInRangeAddressesAreAllowed();
  
  public abstract void setInRangeAddressesAreAllowed(boolean paramBoolean);
  
  public abstract boolean isEnabled();
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract void save()
    throws IPFilterException;
  
  public abstract void markAsUpToDate();
  
  public abstract long getLastUpdateTime();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ipfilter/IPFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */