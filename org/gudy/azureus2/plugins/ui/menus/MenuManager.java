package org.gudy.azureus2.plugins.ui.menus;

public abstract interface MenuManager
{
  public static final String MENU_TABLE = "table";
  public static final String MENU_SYSTRAY = "systray";
  public static final String MENU_DOWNLOAD_BAR = "downloadbar";
  public static final String MENU_MENUBAR = "mainmenu";
  public static final String MENU_TRANSFERSBAR = "transfersbar";
  public static final String MENU_TORRENT_MENU = "torrentmenu";
  public static final String MENU_DOWNLOAD_CONTEXT = "download_context";
  public static final String MENU_FILE_CONTEXT = "file_context";
  public static final String MENU_TAG_CONTEXT = "tag_content";
  
  public abstract MenuItem addMenuItem(String paramString1, String paramString2);
  
  public abstract MenuItem addMenuItem(MenuContext paramMenuContext, String paramString);
  
  public abstract MenuItem addMenuItem(MenuItem paramMenuItem, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/menus/MenuManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */