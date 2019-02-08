package org.gudy.azureus2.plugins.ddb;

import java.net.InetSocketAddress;
import java.util.Map;

public abstract interface DistributedDatabaseContact
{
  public abstract byte[] getID();
  
  public abstract String getName();
  
  public abstract int getVersion();
  
  public abstract InetSocketAddress getAddress();
  
  public abstract int getDHT();
  
  public abstract boolean isAlive(long paramLong);
  
  public abstract void isAlive(long paramLong, DistributedDatabaseListener paramDistributedDatabaseListener);
  
  public abstract boolean isOrHasBeenLocal();
  
  public abstract Map<String, Object> exportToMap();
  
  public abstract boolean openTunnel();
  
  public abstract DistributedDatabaseValue call(DistributedDatabaseProgressListener paramDistributedDatabaseProgressListener, DistributedDatabaseTransferType paramDistributedDatabaseTransferType, DistributedDatabaseValue paramDistributedDatabaseValue, long paramLong)
    throws DistributedDatabaseException;
  
  public abstract void write(DistributedDatabaseProgressListener paramDistributedDatabaseProgressListener, DistributedDatabaseTransferType paramDistributedDatabaseTransferType, DistributedDatabaseKey paramDistributedDatabaseKey, DistributedDatabaseValue paramDistributedDatabaseValue, long paramLong)
    throws DistributedDatabaseException;
  
  public abstract DistributedDatabaseValue read(DistributedDatabaseProgressListener paramDistributedDatabaseProgressListener, DistributedDatabaseTransferType paramDistributedDatabaseTransferType, DistributedDatabaseKey paramDistributedDatabaseKey, long paramLong)
    throws DistributedDatabaseException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ddb/DistributedDatabaseContact.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */