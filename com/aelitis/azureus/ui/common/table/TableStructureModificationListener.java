package com.aelitis.azureus.ui.common.table;

public abstract interface TableStructureModificationListener<T>
{
  public abstract void tableStructureChanged(boolean paramBoolean, Class paramClass);
  
  public abstract void columnOrderChanged(int[] paramArrayOfInt);
  
  public abstract void columnSizeChanged(TableColumnCore paramTableColumnCore, int paramInt);
  
  public abstract void columnInvalidate(TableColumnCore paramTableColumnCore);
  
  public abstract void cellInvalidate(TableColumnCore paramTableColumnCore, T paramT);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableStructureModificationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */