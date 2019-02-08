package org.gudy.azureus2.plugins.ui.tables;

import org.gudy.azureus2.plugins.ui.Graphic;

public abstract interface TableCell
{
  public abstract Object getDataSource();
  
  public abstract TableColumn getTableColumn();
  
  public abstract TableRow getTableRow();
  
  public abstract String getTableID();
  
  public abstract boolean setText(String paramString);
  
  public abstract String getText();
  
  public abstract boolean setForeground(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract boolean setForeground(int[] paramArrayOfInt);
  
  public abstract boolean setForegroundToErrorColor();
  
  public abstract int[] getForeground();
  
  public abstract int[] getBackground();
  
  public abstract boolean setSortValue(Comparable paramComparable);
  
  public abstract boolean setSortValue(long paramLong);
  
  public abstract boolean setSortValue(float paramFloat);
  
  public abstract Comparable getSortValue();
  
  public abstract boolean isShown();
  
  public abstract boolean isValid();
  
  public abstract void invalidate();
  
  public abstract void setToolTip(Object paramObject);
  
  public abstract Object getToolTip();
  
  public abstract boolean isDisposed();
  
  public abstract int getMaxLines();
  
  public abstract int getWidth();
  
  public abstract int getHeight();
  
  public abstract boolean setGraphic(Graphic paramGraphic);
  
  public abstract Graphic getGraphic();
  
  public abstract void setFillCell(boolean paramBoolean);
  
  public abstract int getMarginHeight();
  
  public abstract void setMarginHeight(int paramInt);
  
  public abstract int getMarginWidth();
  
  public abstract void setMarginWidth(int paramInt);
  
  public abstract void addRefreshListener(TableCellRefreshListener paramTableCellRefreshListener);
  
  public abstract void removeRefreshListener(TableCellRefreshListener paramTableCellRefreshListener);
  
  public abstract void addDisposeListener(TableCellDisposeListener paramTableCellDisposeListener);
  
  public abstract void removeDisposeListener(TableCellDisposeListener paramTableCellDisposeListener);
  
  public abstract void addToolTipListener(TableCellToolTipListener paramTableCellToolTipListener);
  
  public abstract void removeToolTipListener(TableCellToolTipListener paramTableCellToolTipListener);
  
  public abstract void addMouseListener(TableCellMouseListener paramTableCellMouseListener);
  
  public abstract void removeMouseListener(TableCellMouseListener paramTableCellMouseListener);
  
  public abstract void addListeners(Object paramObject);
  
  public abstract Graphic getBackgroundGraphic();
  
  public abstract int[] getMouseOffset();
  
  public abstract String getClipboardText();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/tables/TableCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */