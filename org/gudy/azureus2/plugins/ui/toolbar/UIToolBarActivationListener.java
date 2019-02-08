package org.gudy.azureus2.plugins.ui.toolbar;

import com.aelitis.azureus.ui.common.ToolBarItem;

public abstract interface UIToolBarActivationListener
{
  public static final long ACTIVATIONTYPE_NORMAL = 0L;
  public static final long ACTIVATIONTYPE_HELD = 1L;
  public static final long ACTIVATIONTYPE_RIGHTCLICK = 2L;
  
  public abstract boolean toolBarItemActivated(ToolBarItem paramToolBarItem, long paramLong, Object paramObject);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/toolbar/UIToolBarActivationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */