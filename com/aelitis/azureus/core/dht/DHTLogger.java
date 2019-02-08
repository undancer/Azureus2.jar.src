package com.aelitis.azureus.core.dht;

import org.gudy.azureus2.plugins.PluginInterface;

public abstract interface DHTLogger
{
  public static final int LT_GENERAL = 1;
  public static final int LT_IP_FILTER = 2;
  
  public abstract void log(String paramString);
  
  public abstract void log(Throwable paramThrowable);
  
  public abstract void log(int paramInt, String paramString);
  
  public abstract boolean isEnabled(int paramInt);
  
  public abstract PluginInterface getPluginInterface();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/DHTLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */