package org.gudy.azureus2.plugins.ddb;

public abstract interface DistributedDatabaseTransferHandler
{
  public abstract DistributedDatabaseValue read(DistributedDatabaseContact paramDistributedDatabaseContact, DistributedDatabaseTransferType paramDistributedDatabaseTransferType, DistributedDatabaseKey paramDistributedDatabaseKey)
    throws DistributedDatabaseException;
  
  public abstract DistributedDatabaseValue write(DistributedDatabaseContact paramDistributedDatabaseContact, DistributedDatabaseTransferType paramDistributedDatabaseTransferType, DistributedDatabaseKey paramDistributedDatabaseKey, DistributedDatabaseValue paramDistributedDatabaseValue)
    throws DistributedDatabaseException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ddb/DistributedDatabaseTransferHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */