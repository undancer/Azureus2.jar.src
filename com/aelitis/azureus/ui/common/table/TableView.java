package com.aelitis.azureus.ui.common.table;

import java.util.List;
import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
import org.gudy.azureus2.plugins.ui.tables.TableColumn;
import org.gudy.azureus2.plugins.ui.tables.TableRow;

public abstract interface TableView<DATASOURCETYPE>
  extends AEDiagnosticsEvidenceGenerator
{
  public abstract void addCountChangeListener(TableCountChangeListener paramTableCountChangeListener);
  
  public abstract void addDataSource(DATASOURCETYPE paramDATASOURCETYPE);
  
  public abstract void addDataSources(DATASOURCETYPE[] paramArrayOfDATASOURCETYPE);
  
  public abstract void addLifeCycleListener(TableLifeCycleListener paramTableLifeCycleListener);
  
  public abstract void addRefreshListener(TableRefreshListener paramTableRefreshListener, boolean paramBoolean);
  
  public abstract void addSelectionListener(TableSelectionListener paramTableSelectionListener, boolean paramBoolean);
  
  public abstract void addTableDataSourceChangedListener(TableDataSourceChangedListener paramTableDataSourceChangedListener, boolean paramBoolean);
  
  public abstract void clipboardSelected();
  
  public abstract void columnInvalidate(String paramString);
  
  public abstract void columnInvalidate(TableColumnCore paramTableColumnCore);
  
  public abstract void delete();
  
  public abstract TableCellCore[] getColumnCells(String paramString);
  
  public abstract List<DATASOURCETYPE> getDataSources();
  
  public abstract List<DATASOURCETYPE> getDataSources(boolean paramBoolean);
  
  public abstract Object getFirstSelectedDataSource();
  
  public abstract String getPropertiesPrefix();
  
  public abstract TableRowCore getRow(DATASOURCETYPE paramDATASOURCETYPE);
  
  public abstract TableRowCore[] getRows();
  
  public abstract List<Object> getSelectedDataSources();
  
  public abstract Object[] getSelectedDataSources(boolean paramBoolean);
  
  public abstract TableRowCore[] getSelectedRows();
  
  public abstract TableColumnCore getSortColumn();
  
  public abstract boolean isDisposed();
  
  public abstract void processDataSourceQueue();
  
  public abstract void refreshTable(boolean paramBoolean);
  
  public abstract void removeAllTableRows();
  
  public abstract void removeDataSource(DATASOURCETYPE paramDATASOURCETYPE);
  
  public abstract void removeTableDataSourceChangedListener(TableDataSourceChangedListener paramTableDataSourceChangedListener);
  
  public abstract void runForAllRows(TableGroupRowRunner paramTableGroupRowRunner);
  
  public abstract void runForAllRows(TableGroupRowVisibilityRunner paramTableGroupRowVisibilityRunner);
  
  public abstract void runForSelectedRows(TableGroupRowRunner paramTableGroupRowRunner);
  
  public abstract void selectAll();
  
  public abstract void setEnableTabViews(boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString);
  
  public abstract void setFocus();
  
  public abstract void setParentDataSource(Object paramObject);
  
  public abstract Object getParentDataSource();
  
  public abstract void setRowDefaultHeight(int paramInt);
  
  public abstract void setRowDefaultHeightEM(float paramFloat);
  
  public abstract void setRowDefaultHeightPX(int paramInt);
  
  public abstract void setSelectedRows(TableRowCore[] paramArrayOfTableRowCore);
  
  public abstract int size(boolean paramBoolean);
  
  public abstract TableRowCore getFocusedRow();
  
  public abstract String getTableID();
  
  public abstract TableViewCreator getTableViewCreator();
  
  public abstract TableRowCore getRow(int paramInt1, int paramInt2);
  
  public abstract boolean dataSourceExists(DATASOURCETYPE paramDATASOURCETYPE);
  
  public abstract TableColumnCore[] getVisibleColumns();
  
  public abstract void removeDataSources(DATASOURCETYPE[] paramArrayOfDATASOURCETYPE);
  
  public abstract int getSelectedRowsSize();
  
  public abstract int indexOf(TableRowCore paramTableRowCore);
  
  public abstract boolean isRowVisible(TableRowCore paramTableRowCore);
  
  public abstract TableCellCore getTableCellWithCursor();
  
  public abstract TableRowCore getTableRowWithCursor();
  
  public abstract int getRowDefaultHeight();
  
  public abstract boolean isColumnVisible(TableColumn paramTableColumn);
  
  public abstract TableRowCore getRow(int paramInt);
  
  public abstract Class getDataSourceType();
  
  public abstract TableColumn getTableColumn(String paramString);
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract boolean canHaveSubItems();
  
  public abstract boolean isSelected(TableRow paramTableRow);
  
  public abstract boolean isUnfilteredDataSourceAdded(Object paramObject);
  
  public abstract void setHeaderVisible(boolean paramBoolean);
  
  public abstract boolean getHeaderVisible();
  
  public abstract void processDataSourceQueueSync();
  
  public abstract int getMaxItemShown();
  
  public abstract void setMaxItemShown(int paramInt);
  
  public abstract int getRowCount();
  
  public abstract void resetLastSortedOn();
  
  public abstract TableColumnCore[] getAllColumns();
  
  public abstract void removeCountChangeListener(TableCountChangeListener paramTableCountChangeListener);
  
  public abstract void addExpansionChangeListener(TableExpansionChangeListener paramTableExpansionChangeListener);
  
  public abstract void removeExpansionChangeListener(TableExpansionChangeListener paramTableExpansionChangeListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */