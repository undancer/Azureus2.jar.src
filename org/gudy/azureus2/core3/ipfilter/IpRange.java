package org.gudy.azureus2.core3.ipfilter;

public abstract interface IpRange
{
  public abstract String getDescription();
  
  public abstract void setDescription(String paramString);
  
  public abstract boolean isValid();
  
  public abstract boolean isSessionOnly();
  
  public abstract String getStartIp();
  
  public abstract void setStartIp(String paramString);
  
  public abstract String getEndIp();
  
  public abstract void setEndIp(String paramString);
  
  public abstract void setSessionOnly(boolean paramBoolean);
  
  public abstract boolean isInRange(String paramString);
  
  public abstract void checkValid();
  
  public abstract int compareStartIpTo(IpRange paramIpRange);
  
  public abstract int compareEndIpTo(IpRange paramIpRange);
  
  public abstract int compareDescription(IpRange paramIpRange);
  
  public abstract long getEndIpLong();
  
  public abstract long getStartIpLong();
  
  public abstract long getMergedEndLong();
  
  public abstract IpRange[] getMergedEntries();
  
  public abstract void resetMergeInfo();
  
  public abstract boolean getMerged();
  
  public abstract void setMerged();
  
  public abstract void setMergedEnd(long paramLong);
  
  public abstract void addMergedEntry(IpRange paramIpRange);
  
  public abstract boolean getAddedToRangeList();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/IpRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */