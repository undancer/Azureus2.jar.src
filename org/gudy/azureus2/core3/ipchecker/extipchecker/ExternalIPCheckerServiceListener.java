package org.gudy.azureus2.core3.ipchecker.extipchecker;

public abstract interface ExternalIPCheckerServiceListener
{
  public abstract void checkComplete(ExternalIPCheckerService paramExternalIPCheckerService, String paramString);
  
  public abstract void checkFailed(ExternalIPCheckerService paramExternalIPCheckerService, String paramString);
  
  public abstract void reportProgress(ExternalIPCheckerService paramExternalIPCheckerService, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipchecker/extipchecker/ExternalIPCheckerServiceListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */