package org.gudy.azureus2.plugins.ddb;

public abstract interface DistributedDatabaseKeyStats
{
  public abstract int getEntryCount();
  
  public abstract int getSize();
  
  public abstract int getReadsPerMinute();
  
  public abstract byte getDiversification();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ddb/DistributedDatabaseKeyStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */