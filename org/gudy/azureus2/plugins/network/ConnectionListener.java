package org.gudy.azureus2.plugins.network;

public abstract interface ConnectionListener
{
  public abstract void connectStarted();
  
  public abstract void connectSuccess();
  
  public abstract void connectFailure(Throwable paramThrowable);
  
  public abstract void exceptionThrown(Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/network/ConnectionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */