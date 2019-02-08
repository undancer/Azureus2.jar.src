package com.aelitis.azureus.plugins.net.netstatus;

public abstract interface NetStatusProtocolTesterListener
{
  public abstract void sessionAdded(NetStatusProtocolTesterBT.Session paramSession);
  
  public abstract void complete(NetStatusProtocolTesterBT paramNetStatusProtocolTesterBT);
  
  public abstract void log(String paramString, boolean paramBoolean);
  
  public abstract void logError(String paramString);
  
  public abstract void logError(String paramString, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/netstatus/NetStatusProtocolTesterListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */