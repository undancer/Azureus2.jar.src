package com.aelitis.azureus.ui.common;

import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarActivationListener;
import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarItem;

public abstract interface ToolBarItem
  extends UIToolBarItem
{
  public abstract boolean triggerToolBarItem(long paramLong, Object paramObject);
  
  public abstract void setDefaultActivationListener(UIToolBarActivationListener paramUIToolBarActivationListener);
  
  public abstract void setAlwaysAvailable(boolean paramBoolean);
  
  public abstract String getTooltipID();
  
  public abstract UIToolBarActivationListener getDefaultActivationListener();
  
  public abstract void addToolBarItemListener(ToolBarItemListener paramToolBarItemListener);
  
  public abstract void removeToolBarItemListener(ToolBarItemListener paramToolBarItemListener);
  
  public static abstract interface ToolBarItemListener
  {
    public abstract void uiFieldChanged(ToolBarItem paramToolBarItem);
    
    public abstract boolean triggerToolBarItem(ToolBarItem paramToolBarItem, long paramLong, Object paramObject);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/ToolBarItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */