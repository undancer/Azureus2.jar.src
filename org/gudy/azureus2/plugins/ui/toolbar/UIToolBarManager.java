package org.gudy.azureus2.plugins.ui.toolbar;

public abstract interface UIToolBarManager
{
  public static final String GROUP_BIG = "big";
  public static final String GROUP_MAIN = "main";
  
  public abstract UIToolBarItem createToolBarItem(String paramString);
  
  public abstract void addToolBarItem(UIToolBarItem paramUIToolBarItem);
  
  public abstract UIToolBarItem getToolBarItem(String paramString);
  
  public abstract UIToolBarItem[] getAllToolBarItems();
  
  public abstract void removeToolBarItem(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/toolbar/UIToolBarManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */