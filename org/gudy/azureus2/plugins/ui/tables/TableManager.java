package org.gudy.azureus2.plugins.ui.tables;

public abstract interface TableManager
{
  public static final String TABLE_MYTORRENTS_COMPLETE = "MySeeders";
  public static final String TABLE_MYTORRENTS_INCOMPLETE = "MyTorrents";
  public static final String TABLE_MYTORRENTS_UNOPENED = "Unopened";
  public static final String TABLE_MYTORRENTS_COMPLETE_BIG = "MySeeders.big";
  public static final String TABLE_MYTORRENTS_INCOMPLETE_BIG = "MyTorrents.big";
  public static final String TABLE_MYTORRENTS_UNOPENED_BIG = "Unopened.big";
  public static final String TABLE_MYTORRENTS_ALL_BIG = "MyLibrary.big";
  public static final String TABLE_ACTIVITY = "Activity";
  public static final String TABLE_ACTIVITY_BIG = "Activity.big";
  public static final String TABLE_TORRENT_PEERS = "Peers";
  public static final String TABLE_TORRENT_PIECES = "Pieces";
  public static final String TABLE_TORRENT_FILES = "Files";
  public static final String TABLE_TORRENT_TRACKERS = "Trackers";
  public static final String TABLE_MYTRACKER = "MyTracker";
  public static final String TABLE_MYSHARES = "MyShares";
  public static final String TABLE_ALL_PEERS = "AllPeers";
  
  public abstract TableColumn createColumn(String paramString1, String paramString2);
  
  public abstract void registerColumn(Class paramClass, String paramString, TableColumnCreationListener paramTableColumnCreationListener);
  
  public abstract void unregisterColumn(Class paramClass, String paramString, TableColumnCreationListener paramTableColumnCreationListener);
  
  public abstract void addColumn(TableColumn paramTableColumn);
  
  public abstract TableContextMenuItem addContextMenuItem(String paramString1, String paramString2);
  
  public abstract TableContextMenuItem addContextMenuItem(TableContextMenuItem paramTableContextMenuItem, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/tables/TableManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */