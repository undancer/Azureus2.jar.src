package org.gudy.azureus2.plugins.logging;

public abstract interface LoggerChannelListener
{
  public abstract void messageLogged(int paramInt, String paramString);
  
  public abstract void messageLogged(String paramString, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/logging/LoggerChannelListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */