package com.aelitis.azureus.ui.common.table;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.gudy.azureus2.core3.util.IndentWriter;
import org.gudy.azureus2.plugins.ui.tables.TableCell;
import org.gudy.azureus2.plugins.ui.tables.TableCellInplaceEditorListener;
import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
import org.gudy.azureus2.plugins.ui.tables.TableColumn;
import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;

public abstract interface TableColumnCore
  extends TableColumn, Comparator
{
  public abstract void setColumnAdded();
  
  public abstract boolean getColumnAdded();
  
  public abstract void setUseCoreDataSource(boolean paramBoolean);
  
  public abstract boolean getUseCoreDataSource();
  
  public abstract void invokeCellRefreshListeners(TableCell paramTableCell, boolean paramBoolean)
    throws Throwable;
  
  public abstract List getCellRefreshListeners();
  
  public abstract void invokeCellAddedListeners(TableCell paramTableCell);
  
  public abstract List getCellAddedListeners();
  
  public abstract void invokeCellDisposeListeners(TableCell paramTableCell);
  
  public abstract void invokeCellToolTipListeners(TableCellCore paramTableCellCore, int paramInt);
  
  public abstract void invokeCellMouseListeners(TableCellMouseEvent paramTableCellMouseEvent);
  
  public abstract void invokeCellVisibilityListeners(TableCellCore paramTableCellCore, int paramInt);
  
  public abstract void setPositionNoShift(int paramInt);
  
  public abstract void loadSettings(Map paramMap);
  
  public abstract void saveSettings(Map paramMap);
  
  public abstract String getTitleLanguageKey();
  
  public abstract String getTitleLanguageKey(boolean paramBoolean);
  
  public abstract int getConsecutiveErrCount();
  
  public abstract void setConsecutiveErrCount(int paramInt);
  
  public abstract void removeContextMenuItem(TableContextMenuItem paramTableContextMenuItem);
  
  public abstract TableContextMenuItem[] getContextMenuItems(int paramInt);
  
  public abstract boolean hasCellRefreshListener();
  
  public abstract long getLastSortValueChange();
  
  public abstract void setLastSortValueChange(long paramLong);
  
  public abstract void setSortValueLive(boolean paramBoolean);
  
  public abstract boolean isSortValueLive();
  
  public abstract void addRefreshTime(long paramLong);
  
  public abstract void generateDiagnostics(IndentWriter paramIndentWriter);
  
  public abstract void setTableID(String paramString);
  
  public abstract boolean isSortAscending();
  
  public abstract void setSortAscending(boolean paramBoolean);
  
  public abstract void setDefaultSortAscending(boolean paramBoolean);
  
  public abstract boolean hasCellMouseMoveListener();
  
  public abstract void triggerColumnSizeChange(int paramInt);
  
  public abstract void setAutoTooltip(boolean paramBoolean);
  
  public abstract boolean doesAutoTooltip();
  
  public abstract void addCellOtherListener(String paramString, Object paramObject);
  
  public abstract void removeCellOtherListener(String paramString, Object paramObject);
  
  public abstract Object[] getCellOtherListeners(String paramString);
  
  public abstract boolean hasCellOtherListeners(String paramString);
  
  public abstract boolean isRemoved();
  
  public abstract List<TableColumnExtraInfoListener> getColumnExtraInfoListeners();
  
  public abstract void reset();
  
  public abstract String getClipboardText(TableCell paramTableCell);
  
  public abstract boolean handlesDataSourceType(Class<?> paramClass);
  
  public abstract void addDataSourceType(Class<?> paramClass);
  
  public abstract boolean showOnlyImage();
  
  public abstract TableCellInplaceEditorListener getInplaceEditorListener();
  
  public abstract boolean hasInplaceEditorListener();
  
  public abstract void setInplaceEditorListener(TableCellInplaceEditorListener paramTableCellInplaceEditorListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableColumnCore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */