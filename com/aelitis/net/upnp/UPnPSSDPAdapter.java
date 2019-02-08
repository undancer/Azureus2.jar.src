package com.aelitis.net.upnp;

import org.gudy.azureus2.plugins.utils.UTTimer;

public abstract interface UPnPSSDPAdapter
{
  public abstract UTTimer createTimer(String paramString);
  
  public abstract void createThread(String paramString, Runnable paramRunnable);
  
  public abstract void trace(String paramString);
  
  public abstract void log(String paramString);
  
  public abstract void log(Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/UPnPSSDPAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */