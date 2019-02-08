package org.gudy.azureus2.ui.swt.pluginsimpl;

import com.aelitis.azureus.ui.common.ToolBarItem;
import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarItem;
import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarManager;

public abstract interface UIToolBarManagerCore
  extends UIToolBarManager
{
  public abstract ToolBarItem[] getAllSWTToolBarItems();
  
  public abstract void addListener(UIToolBarManagerImpl.ToolBarManagerListener paramToolBarManagerListener);
  
  public abstract void removeListener(UIToolBarManagerImpl.ToolBarManagerListener paramToolBarManagerListener);
  
  public abstract void addToolBarItem(UIToolBarItem paramUIToolBarItem, boolean paramBoolean);
  
  public abstract String[] getToolBarIDsByGroup(String paramString);
  
  public abstract String[] getGroupIDs();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UIToolBarManagerCore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */