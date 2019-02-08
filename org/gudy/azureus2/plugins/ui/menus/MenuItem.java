package org.gudy.azureus2.plugins.ui.menus;

import org.gudy.azureus2.plugins.ui.Graphic;

public abstract interface MenuItem
{
  public static final int STYLE_PUSH = 1;
  public static final int STYLE_CHECK = 2;
  public static final int STYLE_RADIO = 3;
  public static final int STYLE_SEPARATOR = 4;
  public static final int STYLE_MENU = 5;
  
  public abstract String getResourceKey();
  
  public abstract int getStyle();
  
  public abstract void setStyle(int paramInt);
  
  public abstract Object getData();
  
  public abstract void setData(Object paramObject);
  
  public abstract boolean isEnabled();
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract void setGraphic(Graphic paramGraphic);
  
  public abstract Graphic getGraphic();
  
  public abstract void addFillListener(MenuItemFillListener paramMenuItemFillListener);
  
  public abstract void removeFillListener(MenuItemFillListener paramMenuItemFillListener);
  
  public abstract void setSubmenuBuilder(MenuBuilder paramMenuBuilder);
  
  public abstract void addMultiListener(MenuItemListener paramMenuItemListener);
  
  public abstract void removeMultiListener(MenuItemListener paramMenuItemListener);
  
  public abstract void addListener(MenuItemListener paramMenuItemListener);
  
  public abstract void removeListener(MenuItemListener paramMenuItemListener);
  
  public abstract MenuItem getParent();
  
  public abstract MenuItem[] getItems();
  
  public abstract MenuItem getItem(String paramString);
  
  public abstract String getText();
  
  public abstract void setText(String paramString);
  
  public abstract String getMenuID();
  
  public abstract void remove();
  
  public abstract void removeAllChildItems();
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract boolean isVisible();
  
  public abstract boolean isSelected();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/menus/MenuItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */