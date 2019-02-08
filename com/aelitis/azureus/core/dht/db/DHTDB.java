package com.aelitis.azureus.core.dht.db;

import com.aelitis.azureus.core.dht.DHTStorageBlock;
import com.aelitis.azureus.core.dht.control.DHTControl;
import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import com.aelitis.azureus.core.dht.transport.DHTTransportQueryStoreReply;
import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
import java.util.Iterator;
import java.util.List;
import org.gudy.azureus2.core3.util.HashWrapper;

public abstract interface DHTDB
{
  public abstract void setControl(DHTControl paramDHTControl);
  
  public abstract DHTDBValue store(HashWrapper paramHashWrapper, byte[] paramArrayOfByte, short paramShort, byte paramByte1, byte paramByte2);
  
  public abstract byte store(DHTTransportContact paramDHTTransportContact, HashWrapper paramHashWrapper, DHTTransportValue[] paramArrayOfDHTTransportValue);
  
  public abstract DHTTransportQueryStoreReply queryStore(DHTTransportContact paramDHTTransportContact, int paramInt, List<Object[]> paramList);
  
  public abstract DHTDBValue get(HashWrapper paramHashWrapper);
  
  public abstract DHTDBValue getAnyValue(HashWrapper paramHashWrapper);
  
  public abstract List<DHTDBValue> getAllValues(HashWrapper paramHashWrapper);
  
  public abstract boolean hasKey(HashWrapper paramHashWrapper);
  
  public abstract DHTDBLookupResult get(DHTTransportContact paramDHTTransportContact, HashWrapper paramHashWrapper, int paramInt, short paramShort, boolean paramBoolean);
  
  public abstract DHTDBValue remove(DHTTransportContact paramDHTTransportContact, HashWrapper paramHashWrapper);
  
  public abstract DHTStorageBlock keyBlockRequest(DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  public abstract DHTStorageBlock getKeyBlockDetails(byte[] paramArrayOfByte);
  
  public abstract boolean isKeyBlocked(byte[] paramArrayOfByte);
  
  public abstract DHTStorageBlock[] getDirectKeyBlocks();
  
  public abstract boolean isEmpty();
  
  public abstract Iterator<HashWrapper> getKeys();
  
  public abstract DHTDBStats getStats();
  
  public abstract void setSleeping(boolean paramBoolean);
  
  public abstract void setSuspended(boolean paramBoolean);
  
  public abstract void destroy();
  
  public abstract void print(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/db/DHTDB.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */