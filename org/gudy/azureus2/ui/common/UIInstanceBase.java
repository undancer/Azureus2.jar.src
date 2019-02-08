package org.gudy.azureus2.ui.common;

import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.ui.UIInstance;

public abstract interface UIInstanceBase
  extends UIInstance
{
  public abstract void unload(PluginInterface paramPluginInterface);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/UIInstanceBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */