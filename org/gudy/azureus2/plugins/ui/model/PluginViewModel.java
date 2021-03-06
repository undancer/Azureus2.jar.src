package org.gudy.azureus2.plugins.ui.model;

import org.gudy.azureus2.plugins.PluginInterface;

public abstract interface PluginViewModel
{
  public abstract String getName();
  
  public abstract PluginInterface getPluginInterface();
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/model/PluginViewModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */