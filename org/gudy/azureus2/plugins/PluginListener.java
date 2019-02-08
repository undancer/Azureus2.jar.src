package org.gudy.azureus2.plugins;

public abstract interface PluginListener
{
  public abstract void initializationComplete();
  
  public abstract void closedownInitiated();
  
  public abstract void closedownComplete();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/PluginListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */