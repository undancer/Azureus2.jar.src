package com.aelitis.azureus.ui.common.table;

public abstract class TableSelectionAdapter
  implements TableSelectionListener
{
  public void defaultSelected(TableRowCore[] rows, int stateMask) {}
  
  public void deselected(TableRowCore[] rows) {}
  
  public void focusChanged(TableRowCore focus) {}
  
  public void selected(TableRowCore[] rows) {}
  
  public void mouseEnter(TableRowCore row) {}
  
  public void mouseExit(TableRowCore row) {}
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableSelectionAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */