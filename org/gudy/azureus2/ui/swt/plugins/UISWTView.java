package org.gudy.azureus2.ui.swt.plugins;

import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.ui.UIPluginView;

public abstract interface UISWTView
  extends UIPluginView
{
  public static final int CONTROLTYPE_SWT = 0;
  public static final int CONTROLTYPE_AWT = 1;
  
  public abstract void setControlType(int paramInt);
  
  public abstract int getControlType();
  
  public abstract Object getDataSource();
  
  public abstract Object getInitialDataSource();
  
  public abstract UISWTView getParentView();
  
  public abstract void triggerEvent(int paramInt, Object paramObject);
  
  public abstract void setTitle(String paramString);
  
  public abstract PluginInterface getPluginInterface();
  
  public abstract void setDestroyOnDeactivate(boolean paramBoolean);
  
  public abstract boolean isDestroyOnDeactivate();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/plugins/UISWTView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */