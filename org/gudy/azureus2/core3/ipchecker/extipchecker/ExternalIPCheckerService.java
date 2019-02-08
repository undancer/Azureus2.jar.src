package org.gudy.azureus2.core3.ipchecker.extipchecker;

public abstract interface ExternalIPCheckerService
{
  public abstract String getName();
  
  public abstract String getDescription();
  
  public abstract String getURL();
  
  public abstract boolean supportsCheck();
  
  public abstract void initiateCheck(long paramLong);
  
  public abstract void addListener(ExternalIPCheckerServiceListener paramExternalIPCheckerServiceListener);
  
  public abstract void removeListener(ExternalIPCheckerServiceListener paramExternalIPCheckerServiceListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipchecker/extipchecker/ExternalIPCheckerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */