package org.gudy.azureus2.plugins.logging;

public abstract interface LoggerAlertListener
{
  public abstract void alertLogged(int paramInt, String paramString, boolean paramBoolean);
  
  public abstract void alertLogged(String paramString, Throwable paramThrowable, boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/logging/LoggerAlertListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */