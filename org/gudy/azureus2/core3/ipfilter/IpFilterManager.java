package org.gudy.azureus2.core3.ipfilter;

public abstract interface IpFilterManager
{
  public abstract IpFilter getIPFilter();
  
  public abstract BadIps getBadIps();
  
  public abstract byte[] getDescription(Object paramObject);
  
  public abstract Object addDescription(IpRange paramIpRange, byte[] paramArrayOfByte);
  
  public abstract void cacheAllDescriptions();
  
  public abstract void clearDescriptionCache();
  
  public abstract void deleteAllDescriptions();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/IpFilterManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */