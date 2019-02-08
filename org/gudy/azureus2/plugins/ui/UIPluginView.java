package org.gudy.azureus2.plugins.ui;

import org.gudy.azureus2.plugins.PluginInterface;

public abstract interface UIPluginView
{
  public abstract Object getDataSource();
  
  public abstract String getViewID();
  
  public abstract void closeView();
  
  public abstract PluginInterface getPluginInterface();
  
  public abstract void setToolBarListener(UIPluginViewToolBarListener paramUIPluginViewToolBarListener);
  
  public abstract UIPluginViewToolBarListener getToolBarListener();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/UIPluginView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */