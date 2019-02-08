package com.aelitis.azureus.core.dht;

public abstract interface DHTStorageKeyStats
{
  public abstract int getReadsPerMinute();
  
  public abstract int getSize();
  
  public abstract int getEntryCount();
  
  public abstract byte getDiversification();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/DHTStorageKeyStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */