package org.gudy.azureus2.plugins.ui;

import java.util.Map;
import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarActivationListener;
import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarEnablerBase;

public abstract interface UIPluginViewToolBarListener
  extends UIToolBarActivationListener, UIToolBarEnablerBase
{
  public abstract void refreshToolBarItems(Map<String, Long> paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/UIPluginViewToolBarListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */