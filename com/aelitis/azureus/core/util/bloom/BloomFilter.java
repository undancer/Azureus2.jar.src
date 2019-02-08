package com.aelitis.azureus.core.util.bloom;

import java.util.Map;

public abstract interface BloomFilter
{
  public abstract int add(byte[] paramArrayOfByte);
  
  public abstract int remove(byte[] paramArrayOfByte);
  
  public abstract boolean contains(byte[] paramArrayOfByte);
  
  public abstract int count(byte[] paramArrayOfByte);
  
  public abstract int getEntryCount();
  
  public abstract void clear();
  
  public abstract long getStartTimeMono();
  
  public abstract int getSize();
  
  public abstract BloomFilter getReplica();
  
  public abstract Map<String, Object> serialiseToMap();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/bloom/BloomFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */