package org.gudy.azureus2.plugins.ui.tables;

public abstract interface TableColumn
{
  public static final int MENU_STYLE_HEADER = 1;
  public static final int MENU_STYLE_COLUMN_DATA = 2;
  public static final int TYPE_TEXT = 1;
  public static final int TYPE_GRAPHIC = 2;
  public static final int TYPE_TEXT_ONLY = 3;
  public static final int ALIGN_LEAD = 1;
  public static final int ALIGN_TRAIL = 2;
  public static final int ALIGN_CENTER = 3;
  public static final int ALIGN_TOP = 4;
  public static final int ALIGN_BOTTOM = 8;
  public static final int POSITION_INVISIBLE = -1;
  public static final int POSITION_LAST = -2;
  public static final int INTERVAL_GRAPHIC = -1;
  public static final int INTERVAL_LIVE = -2;
  public static final int INTERVAL_INVALID_ONLY = -3;
  public static final String CAT_ESSENTIAL = "essential";
  public static final String CAT_SHARING = "sharing";
  public static final String CAT_TRACKER = "tracker";
  public static final String CAT_TIME = "time";
  public static final String CAT_SWARM = "swarm";
  public static final String CAT_CONTENT = "content";
  public static final String CAT_PEER_IDENTIFICATION = "identification";
  public static final String CAT_PROTOCOL = "protocol";
  public static final String CAT_BYTES = "bytes";
  public static final String CAT_SETTINGS = "settings";
  public static final String CAT_CONNECTION = "connection";
  public static final String CAT_PROGRESS = "progress";
  public static final String UD_FORCE_VISIBLE = "ud_fv";
  
  public abstract void initialize(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void initialize(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract String getName();
  
  public abstract String getNameOverride();
  
  public abstract void setNameOverride(String paramString);
  
  public abstract String getTableID();
  
  public abstract void setType(int paramInt);
  
  public abstract int getType();
  
  public abstract void setWidth(int paramInt);
  
  public abstract void setWidthPX(int paramInt);
  
  public abstract int getWidth();
  
  public abstract void setPosition(int paramInt);
  
  public abstract int getPosition();
  
  public abstract void setAlignment(int paramInt);
  
  public abstract int getAlignment();
  
  public abstract void setRefreshInterval(int paramInt);
  
  public abstract int getRefreshInterval();
  
  public abstract void setMinWidth(int paramInt);
  
  public abstract int getMinWidth();
  
  public abstract void setMaxWidth(int paramInt);
  
  public abstract int getMaxWidth();
  
  public abstract void setWidthLimits(int paramInt1, int paramInt2);
  
  public abstract void setMaxWidthAuto(boolean paramBoolean);
  
  public abstract boolean isMaxWidthAuto();
  
  public abstract void setMinWidthAuto(boolean paramBoolean);
  
  public abstract boolean isMinWidthAuto();
  
  public abstract void setPreferredWidth(int paramInt);
  
  public abstract int getPreferredWidth();
  
  public abstract boolean isPreferredWidthAuto();
  
  public abstract void setPreferredWidthAuto(boolean paramBoolean);
  
  public abstract boolean isVisible();
  
  public abstract void setUserData(String paramString, Object paramObject);
  
  public abstract void removeUserData(String paramString);
  
  public abstract void postConfigLoad();
  
  public abstract void preConfigSave();
  
  public abstract Object getUserData(String paramString);
  
  public abstract String getUserDataString(String paramString);
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract void addCellRefreshListener(TableCellRefreshListener paramTableCellRefreshListener);
  
  public abstract void removeCellRefreshListener(TableCellRefreshListener paramTableCellRefreshListener);
  
  public abstract void addCellAddedListener(TableCellAddedListener paramTableCellAddedListener);
  
  public abstract void removeCellAddedListener(TableCellAddedListener paramTableCellAddedListener);
  
  public abstract void addCellDisposeListener(TableCellDisposeListener paramTableCellDisposeListener);
  
  public abstract void removeCellDisposeListener(TableCellDisposeListener paramTableCellDisposeListener);
  
  public abstract void addCellToolTipListener(TableCellToolTipListener paramTableCellToolTipListener);
  
  public abstract void removeCellToolTipListener(TableCellToolTipListener paramTableCellToolTipListener);
  
  public abstract void addCellMouseListener(TableCellMouseListener paramTableCellMouseListener);
  
  public abstract void removeCellMouseListener(TableCellMouseListener paramTableCellMouseListener);
  
  public abstract void addListeners(Object paramObject);
  
  public abstract void invalidateCells();
  
  public abstract void invalidateCell(Object paramObject);
  
  public abstract TableContextMenuItem addContextMenuItem(String paramString, int paramInt);
  
  public abstract TableContextMenuItem addContextMenuItem(String paramString);
  
  public abstract boolean isObfusticated();
  
  public abstract void setObfustication(boolean paramBoolean);
  
  public abstract void remove();
  
  public abstract void addColumnExtraInfoListener(TableColumnExtraInfoListener paramTableColumnExtraInfoListener);
  
  public abstract void removeColumnExtraInfoListener(TableColumnExtraInfoListener paramTableColumnExtraInfoListener);
  
  public abstract Class getForDataSourceType();
  
  public abstract void setIconReference(String paramString, boolean paramBoolean);
  
  public abstract String getIconReference();
  
  public abstract void setMinimumRequiredUserMode(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/tables/TableColumn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */