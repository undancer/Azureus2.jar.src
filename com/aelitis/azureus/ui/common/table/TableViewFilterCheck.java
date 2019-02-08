package com.aelitis.azureus.ui.common.table;

public abstract interface TableViewFilterCheck<DATASOURCETYPE>
{
  public abstract boolean filterCheck(DATASOURCETYPE paramDATASOURCETYPE, String paramString, boolean paramBoolean);
  
  public abstract void filterSet(String paramString);
  
  public static abstract interface TableViewFilterCheckEx<DATASOURCETYPE>
    extends TableViewFilterCheck<DATASOURCETYPE>
  {
    public abstract void viewChanged(TableView<DATASOURCETYPE> paramTableView);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableViewFilterCheck.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */