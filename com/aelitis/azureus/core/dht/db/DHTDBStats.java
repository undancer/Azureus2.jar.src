package com.aelitis.azureus.core.dht.db;

public abstract interface DHTDBStats
{
  public static final int VD_VALUE_COUNT = 0;
  public static final int VD_LOCAL_SIZE = 1;
  public static final int VD_DIRECT_SIZE = 2;
  public static final int VD_INDIRECT_SIZE = 3;
  public static final int VD_DIV_FREQ = 4;
  public static final int VD_DIV_SIZE = 5;
  
  public abstract int getKeyCount();
  
  public abstract int getLocalKeyCount();
  
  public abstract int getKeyBlockCount();
  
  public abstract int getSize();
  
  public abstract int getValueCount();
  
  public abstract int[] getValueDetails();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/db/DHTDBStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */