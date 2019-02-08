package com.aelitis.azureus.plugins.magnet;

import java.net.InetSocketAddress;

public abstract interface MagnetPluginProgressListener
{
  public abstract void reportSize(long paramLong);
  
  public abstract void reportActivity(String paramString);
  
  public abstract void reportCompleteness(int paramInt);
  
  public abstract void reportContributor(InetSocketAddress paramInetSocketAddress);
  
  public abstract boolean cancelled();
  
  public abstract boolean verbose();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/magnet/MagnetPluginProgressListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */