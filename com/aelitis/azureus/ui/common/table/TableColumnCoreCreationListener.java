package com.aelitis.azureus.ui.common.table;

import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;

public abstract interface TableColumnCoreCreationListener
  extends TableColumnCreationListener
{
  public abstract TableColumnCore createTableColumnCore(Class<?> paramClass, String paramString1, String paramString2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableColumnCoreCreationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */