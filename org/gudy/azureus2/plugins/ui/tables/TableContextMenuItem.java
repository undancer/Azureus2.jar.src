package org.gudy.azureus2.plugins.ui.tables;

import org.gudy.azureus2.plugins.ui.menus.MenuItem;
import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;

public abstract interface TableContextMenuItem
  extends MenuItem
{
  public abstract void addListener(MenuItemListener paramMenuItemListener);
  
  public abstract void addMultiListener(MenuItemListener paramMenuItemListener);
  
  public abstract String getTableID();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/tables/TableContextMenuItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */