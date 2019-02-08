package org.gudy.azureus2.plugins.ddb;

import com.aelitis.azureus.plugins.dht.DHTPluginInterface;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public abstract interface DistributedDatabase
{
  public static final int OP_NONE = 0;
  public static final int OP_EXHAUSTIVE_READ = 1;
  public static final int OP_PRIORITY_HIGH = 2;
  public static final byte DT_NONE = 1;
  public static final byte DT_FREQUENCY = 2;
  public static final byte DT_SIZE = 3;
  public static final int DHT_MAIN = 1;
  public static final int DHT_CVS = 2;
  
  public abstract boolean isAvailable();
  
  public abstract boolean isInitialized();
  
  public abstract boolean isExtendedUseAllowed();
  
  public abstract String getNetwork();
  
  public abstract DHTPluginInterface getDHTPlugin();
  
  public abstract DistributedDatabaseContact getLocalContact();
  
  public abstract DistributedDatabaseKey createKey(Object paramObject)
    throws DistributedDatabaseException;
  
  public abstract DistributedDatabaseKey createKey(Object paramObject, String paramString)
    throws DistributedDatabaseException;
  
  public abstract DistributedDatabaseValue createValue(Object paramObject)
    throws DistributedDatabaseException;
  
  public abstract DistributedDatabaseContact importContact(InetSocketAddress paramInetSocketAddress)
    throws DistributedDatabaseException;
  
  public abstract DistributedDatabaseContact importContact(InetSocketAddress paramInetSocketAddress, byte paramByte)
    throws DistributedDatabaseException;
  
  public abstract DistributedDatabaseContact importContact(InetSocketAddress paramInetSocketAddress, byte paramByte, int paramInt)
    throws DistributedDatabaseException;
  
  public abstract DistributedDatabaseContact importContact(Map<String, Object> paramMap)
    throws DistributedDatabaseException;
  
  public abstract void write(DistributedDatabaseListener paramDistributedDatabaseListener, DistributedDatabaseKey paramDistributedDatabaseKey, DistributedDatabaseValue paramDistributedDatabaseValue)
    throws DistributedDatabaseException;
  
  public abstract void write(DistributedDatabaseListener paramDistributedDatabaseListener, DistributedDatabaseKey paramDistributedDatabaseKey, DistributedDatabaseValue[] paramArrayOfDistributedDatabaseValue)
    throws DistributedDatabaseException;
  
  public abstract void read(DistributedDatabaseListener paramDistributedDatabaseListener, DistributedDatabaseKey paramDistributedDatabaseKey, long paramLong)
    throws DistributedDatabaseException;
  
  public abstract void read(DistributedDatabaseListener paramDistributedDatabaseListener, DistributedDatabaseKey paramDistributedDatabaseKey, long paramLong, int paramInt)
    throws DistributedDatabaseException;
  
  public abstract void readKeyStats(DistributedDatabaseListener paramDistributedDatabaseListener, DistributedDatabaseKey paramDistributedDatabaseKey, long paramLong)
    throws DistributedDatabaseException;
  
  public abstract List<DistributedDatabaseValue> getValues(DistributedDatabaseKey paramDistributedDatabaseKey)
    throws DistributedDatabaseException;
  
  public abstract void delete(DistributedDatabaseListener paramDistributedDatabaseListener, DistributedDatabaseKey paramDistributedDatabaseKey)
    throws DistributedDatabaseException;
  
  public abstract void delete(DistributedDatabaseListener paramDistributedDatabaseListener, DistributedDatabaseKey paramDistributedDatabaseKey, DistributedDatabaseContact[] paramArrayOfDistributedDatabaseContact)
    throws DistributedDatabaseException;
  
  public abstract void addTransferHandler(DistributedDatabaseTransferType paramDistributedDatabaseTransferType, DistributedDatabaseTransferHandler paramDistributedDatabaseTransferHandler)
    throws DistributedDatabaseException;
  
  public abstract DistributedDatabaseTransferType getStandardTransferType(int paramInt)
    throws DistributedDatabaseException;
  
  public abstract void addListener(DistributedDatabaseListener paramDistributedDatabaseListener);
  
  public abstract void removeListener(DistributedDatabaseListener paramDistributedDatabaseListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ddb/DistributedDatabase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */