package org.gudy.azureus2.plugins.ddb;

public abstract interface DistributedDatabaseValue
{
  public abstract Object getValue(Class paramClass)
    throws DistributedDatabaseException;
  
  public abstract long getCreationTime();
  
  public abstract long getVersion();
  
  public abstract DistributedDatabaseContact getContact();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ddb/DistributedDatabaseValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */