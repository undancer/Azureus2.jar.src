package com.aelitis.azureus.core.dht.control;

import com.aelitis.azureus.core.dht.DHTOperationListener;
import com.aelitis.azureus.core.dht.db.DHTDB;
import com.aelitis.azureus.core.dht.router.DHTRouter;
import com.aelitis.azureus.core.dht.transport.DHTTransport;
import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public abstract interface DHTControl
{
  public static final int K_DEFAULT = 20;
  public static final int B_DEFAULT = 4;
  public static final int MAX_REP_PER_NODE_DEFAULT = 5;
  public static final int SEARCH_CONCURRENCY_DEFAULT = 5;
  public static final int LOOKUP_CONCURRENCY_DEFAULT = 10;
  public static final int CACHE_AT_CLOSEST_N_DEFAULT = 1;
  public static final int ORIGINAL_REPUBLISH_INTERVAL_DEFAULT = 28800000;
  public static final int CACHE_REPUBLISH_INTERVAL_DEFAULT = 1800000;
  public static final int ENCODE_KEYS_DEFAULT = 1;
  public static final int ENABLE_RANDOM_DEFAULT = 1;
  
  public abstract void seed(boolean paramBoolean);
  
  public abstract boolean isSeeded();
  
  public abstract void setSeeded();
  
  public abstract void setSuspended(boolean paramBoolean);
  
  public abstract void put(byte[] paramArrayOfByte1, String paramString, byte[] paramArrayOfByte2, short paramShort, byte paramByte1, byte paramByte2, boolean paramBoolean, DHTOperationListener paramDHTOperationListener);
  
  public abstract boolean isDiversified(byte[] paramArrayOfByte);
  
  public abstract DHTTransportValue getLocalValue(byte[] paramArrayOfByte);
  
  public abstract List<DHTTransportValue> getStoredValues(byte[] paramArrayOfByte);
  
  public abstract void get(byte[] paramArrayOfByte, String paramString, short paramShort, int paramInt, long paramLong, boolean paramBoolean1, boolean paramBoolean2, DHTOperationListener paramDHTOperationListener);
  
  public abstract byte[] remove(byte[] paramArrayOfByte, String paramString, DHTOperationListener paramDHTOperationListener);
  
  public abstract byte[] remove(DHTTransportContact[] paramArrayOfDHTTransportContact, byte[] paramArrayOfByte, String paramString, DHTOperationListener paramDHTOperationListener);
  
  public abstract DHTControlStats getStats();
  
  public abstract void setSleeping(boolean paramBoolean);
  
  public abstract DHTTransport getTransport();
  
  public abstract DHTRouter getRouter();
  
  public abstract DHTDB getDataBase();
  
  public abstract DHTControlActivity[] getActivities();
  
  public abstract void exportState(DataOutputStream paramDataOutputStream, int paramInt)
    throws IOException;
  
  public abstract void importState(DataInputStream paramDataInputStream)
    throws IOException;
  
  public abstract List<DHTTransportContact> getClosestKContactsList(byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract List<DHTTransportContact> getClosestContactsList(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean);
  
  public abstract void putEncodedKey(byte[] paramArrayOfByte, String paramString, DHTTransportValue paramDHTTransportValue, long paramLong, boolean paramBoolean);
  
  public abstract void putDirectEncodedKeys(byte[][] paramArrayOfByte, String paramString, DHTTransportValue[][] paramArrayOfDHTTransportValue, List<DHTTransportContact> paramList);
  
  public abstract void putDirectEncodedKeys(byte[][] paramArrayOfByte, String paramString, DHTTransportValue[][] paramArrayOfDHTTransportValue, DHTTransportContact paramDHTTransportContact, DHTOperationListener paramDHTOperationListener);
  
  public abstract int computeAndCompareDistances(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3);
  
  public abstract byte[] computeDistance(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  public abstract int compareDistances(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  public abstract boolean verifyContact(DHTTransportContact paramDHTTransportContact, boolean paramBoolean);
  
  public abstract boolean lookup(byte[] paramArrayOfByte, String paramString, long paramLong, DHTOperationListener paramDHTOperationListener);
  
  public abstract boolean lookupEncoded(byte[] paramArrayOfByte, String paramString, long paramLong, boolean paramBoolean, DHTOperationListener paramDHTOperationListener);
  
  public abstract byte[] getObfuscatedKey(byte[] paramArrayOfByte);
  
  public abstract List<DHTControlContact> getContacts();
  
  public abstract void pingAll();
  
  public abstract void addListener(DHTControlListener paramDHTControlListener);
  
  public abstract void removeListener(DHTControlListener paramDHTControlListener);
  
  public abstract void destroy();
  
  public abstract void print(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/control/DHTControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */