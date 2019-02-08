package com.aelitis.azureus.ui.common.table;

import java.util.List;
import org.gudy.azureus2.plugins.ui.tables.TableRow;
import org.gudy.azureus2.plugins.ui.tables.TableRowMouseEvent;

public abstract interface TableRowCore
  extends TableRow
{
  public abstract void invalidate();
  
  public abstract void invalidate(boolean paramBoolean);
  
  public abstract void delete();
  
  public abstract List refresh(boolean paramBoolean);
  
  public abstract void locationChanged(int paramInt);
  
  public abstract Object getDataSource(boolean paramBoolean);
  
  public abstract int getIndex();
  
  public abstract boolean setHeight(int paramInt);
  
  public abstract TableCellCore getTableCellCore(String paramString);
  
  public abstract boolean isVisible();
  
  public abstract boolean setTableItem(int paramInt);
  
  public abstract boolean setTableItem(int paramInt, boolean paramBoolean);
  
  public abstract void setSelected(boolean paramBoolean);
  
  public abstract boolean isRowDisposed();
  
  public abstract void setUpToDate(boolean paramBoolean);
  
  public abstract List<TableCellCore> refresh(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void redraw();
  
  public abstract void redraw(boolean paramBoolean);
  
  public abstract TableView getView();
  
  public abstract void invokeMouseListeners(TableRowMouseEvent paramTableRowMouseEvent);
  
  public abstract boolean isMouseOver();
  
  public abstract void setSubItemCount(int paramInt);
  
  public abstract int getSubItemCount();
  
  public abstract boolean isExpanded();
  
  public abstract void setExpanded(boolean paramBoolean);
  
  public abstract TableRowCore getParentRowCore();
  
  public abstract boolean isInPaintItem();
  
  public abstract TableRowCore linkSubItem(int paramInt);
  
  public abstract void setSubItems(Object[] paramArrayOfObject);
  
  public abstract TableRowCore[] getSubRowsWithNull();
  
  public abstract void removeSubRow(Object paramObject);
  
  public abstract int getHeight();
  
  public abstract TableRowCore getSubRow(int paramInt);
  
  public abstract void setSortColumn(String paramString);
  
  public abstract TableCellCore getSortColumnCell(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableRowCore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */