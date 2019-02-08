package org.gudy.azureus2.plugins.ui.tables;

import com.aelitis.azureus.ui.common.table.TableView;

public abstract interface TableRow
{
  public abstract Object getDataSource();
  
  public abstract String getTableID();
  
  public abstract TableView<?> getView();
  
  public abstract int getIndex();
  
  public abstract void setForeground(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void setForeground(int[] paramArrayOfInt);
  
  public abstract void setForegroundToErrorColor();
  
  public abstract boolean isValid();
  
  public abstract TableCell getTableCell(String paramString);
  
  public abstract boolean isSelected();
  
  public abstract void addMouseListener(TableRowMouseListener paramTableRowMouseListener);
  
  public abstract void removeMouseListener(TableRowMouseListener paramTableRowMouseListener);
  
  public abstract Object getData(String paramString);
  
  public abstract void setData(String paramString, Object paramObject);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/tables/TableRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */