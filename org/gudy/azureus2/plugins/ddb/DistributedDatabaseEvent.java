package org.gudy.azureus2.plugins.ddb;

public abstract interface DistributedDatabaseEvent
{
  public static final int ET_VALUE_WRITTEN = 1;
  public static final int ET_VALUE_READ = 2;
  public static final int ET_VALUE_DELETED = 3;
  public static final int ET_OPERATION_COMPLETE = 4;
  public static final int ET_OPERATION_TIMEOUT = 5;
  public static final int ET_KEY_STATS_READ = 6;
  public static final int ET_OPERATION_STARTS = 7;
  public static final int ET_LOCAL_CONTACT_CHANGED = 10;
  
  public abstract int getType();
  
  public abstract DistributedDatabaseKey getKey();
  
  public abstract DistributedDatabaseKeyStats getKeyStats();
  
  public abstract DistributedDatabaseValue getValue();
  
  public abstract DistributedDatabaseContact getContact();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ddb/DistributedDatabaseEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */