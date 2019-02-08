package org.gudy.azureus2.plugins.ipfilter;

public abstract interface IPRange
  extends Comparable
{
  public abstract String getDescription();
  
  public abstract void setDescription(String paramString);
  
  public abstract void checkValid();
  
  public abstract boolean isValid();
  
  public abstract boolean isSessionOnly();
  
  public abstract String getStartIP();
  
  public abstract void setStartIP(String paramString);
  
  public abstract String getEndIP();
  
  public abstract void setEndIP(String paramString);
  
  public abstract void setSessionOnly(boolean paramBoolean);
  
  public abstract boolean isInRange(String paramString);
  
  public abstract void delete();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ipfilter/IPRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */