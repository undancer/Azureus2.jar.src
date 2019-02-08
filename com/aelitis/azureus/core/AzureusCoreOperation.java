package com.aelitis.azureus.core;

public abstract interface AzureusCoreOperation
{
  public static final int OP_FILE_MOVE = 2;
  public static final int OP_PROGRESS = 3;
  
  public abstract int getOperationType();
  
  public abstract AzureusCoreOperationTask getTask();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/AzureusCoreOperation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */