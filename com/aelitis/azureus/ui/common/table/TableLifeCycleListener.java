package com.aelitis.azureus.ui.common.table;

public abstract interface TableLifeCycleListener
{
  public static final int EVENT_INITIALIZED = 0;
  public static final int EVENT_DESTROYED = 1;
  
  public abstract void tableViewInitialized();
  
  public abstract void tableViewDestroyed();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableLifeCycleListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */