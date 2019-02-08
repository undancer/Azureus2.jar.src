package com.aelitis.azureus.ui.common.table;

public abstract interface TableSelectionListener
{
  public abstract void selected(TableRowCore[] paramArrayOfTableRowCore);
  
  public abstract void deselected(TableRowCore[] paramArrayOfTableRowCore);
  
  public abstract void focusChanged(TableRowCore paramTableRowCore);
  
  public abstract void defaultSelected(TableRowCore[] paramArrayOfTableRowCore, int paramInt);
  
  public abstract void mouseEnter(TableRowCore paramTableRowCore);
  
  public abstract void mouseExit(TableRowCore paramTableRowCore);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableSelectionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */