package org.gudy.azureus2.plugins.utils;

public abstract interface PowerManagementListener
{
  public static final int ST_SLEEP = 1;
  
  public abstract String getPowerName();
  
  public abstract boolean requestPowerStateChange(int paramInt, Object paramObject);
  
  public abstract void informPowerStateChange(int paramInt, Object paramObject);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/PowerManagementListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */