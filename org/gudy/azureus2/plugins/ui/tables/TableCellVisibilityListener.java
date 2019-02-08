package org.gudy.azureus2.plugins.ui.tables;

public abstract interface TableCellVisibilityListener
{
  public static final int VISIBILITY_SHOWN = 0;
  public static final int VISIBILITY_HIDDEN = 1;
  
  public abstract void cellVisibilityChanged(TableCell paramTableCell, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/tables/TableCellVisibilityListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */