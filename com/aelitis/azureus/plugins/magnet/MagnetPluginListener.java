package com.aelitis.azureus.plugins.magnet;

import java.util.Map;

public abstract interface MagnetPluginListener
{
  public abstract boolean set(String paramString, Map paramMap);
  
  public abstract int get(String paramString, Map paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/magnet/MagnetPluginListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */