package org.gudy.azureus2.plugins.ui;

public abstract interface UIManagerEvent
{
  public static final int MT_NONE = 0;
  public static final int MT_OK = 1;
  public static final int MT_CANCEL = 2;
  public static final int MT_YES = 4;
  public static final int MT_NO = 8;
  public static final int MT_YES_DEFAULT = 16;
  public static final int MT_NO_DEFAULT = 32;
  public static final int MT_OK_DEFAULT = 64;
  public static final int ET_SHOW_TEXT_MESSAGE = 1;
  public static final int ET_OPEN_TORRENT_VIA_FILE = 2;
  public static final int ET_OPEN_TORRENT_VIA_URL = 3;
  public static final int ET_PLUGIN_VIEW_MODEL_CREATED = 4;
  public static final int ET_PLUGIN_CONFIG_MODEL_CREATED = 5;
  public static final int ET_COPY_TO_CLIPBOARD = 6;
  public static final int ET_PLUGIN_VIEW_MODEL_DESTROYED = 7;
  public static final int ET_PLUGIN_CONFIG_MODEL_DESTROYED = 8;
  public static final int ET_OPEN_URL = 9;
  public static final int ET_CREATE_TABLE_COLUMN = 10;
  public static final int ET_ADD_TABLE_COLUMN = 11;
  public static final int ET_ADD_TABLE_CONTEXT_MENU_ITEM = 12;
  public static final int ET_SHOW_CONFIG_SECTION = 13;
  public static final int ET_ADD_TABLE_CONTEXT_SUBMENU_ITEM = 14;
  public static final int ET_ADD_MENU_ITEM = 15;
  public static final int ET_ADD_SUBMENU_ITEM = 16;
  public static final int ET_REMOVE_TABLE_CONTEXT_MENU_ITEM = 17;
  public static final int ET_REMOVE_TABLE_CONTEXT_SUBMENU_ITEM = 18;
  public static final int ET_REMOVE_MENU_ITEM = 19;
  public static final int ET_REMOVE_SUBMENU_ITEM = 20;
  public static final int ET_SHOW_MSG_BOX = 21;
  public static final int ET_OPEN_TORRENT_VIA_TORRENT = 22;
  public static final int ET_FILE_SHOW = 23;
  public static final int ET_FILE_OPEN = 24;
  public static final int ET_REGISTER_COLUMN = 25;
  public static final int ET_UNREGISTER_COLUMN = 26;
  public static final int ET_HIDE_ALL = 27;
  public static final int ET_CALLBACK_MSG_SELECTION = 100;
  
  public abstract int getType();
  
  public abstract Object getData();
  
  public abstract void setResult(Object paramObject);
  
  public abstract Object getResult();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/UIManagerEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */