package org.gudy.azureus2.plugins.ui;

import org.gudy.azureus2.plugins.PluginInterface;

public abstract interface UIInstanceFactory
{
  public abstract UIInstance getInstance(PluginInterface paramPluginInterface);
  
  public abstract void detach()
    throws UIException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/UIInstanceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */