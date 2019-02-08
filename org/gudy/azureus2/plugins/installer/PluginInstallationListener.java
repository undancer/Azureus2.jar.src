package org.gudy.azureus2.plugins.installer;

import org.gudy.azureus2.plugins.PluginException;

public abstract interface PluginInstallationListener
{
  public abstract void completed();
  
  public abstract void cancelled();
  
  public abstract void failed(PluginException paramPluginException);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/installer/PluginInstallationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */