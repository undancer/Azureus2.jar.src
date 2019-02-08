package com.aelitis.azureus.core.dht.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public abstract interface DHTTransport
{
  public static final byte GF_NONE = 0;
  public static final byte GF_DHT_SLEEPING = 1;
  
  public abstract byte getProtocolVersion();
  
  public abstract byte getMinimumProtocolVersion();
  
  public abstract int getNetwork();
  
  public abstract boolean isIPV6();
  
  public abstract byte getGenericFlags();
  
  public abstract void setGenericFlag(byte paramByte, boolean paramBoolean);
  
  public abstract void setSuspended(boolean paramBoolean);
  
  public abstract DHTTransportContact getLocalContact();
  
  public abstract int getPort();
  
  public abstract void setPort(int paramInt)
    throws DHTTransportException;
  
  public abstract long getTimeout();
  
  public abstract void setTimeout(long paramLong);
  
  public abstract DHTTransportContact importContact(DataInputStream paramDataInputStream, boolean paramBoolean)
    throws IOException, DHTTransportException;
  
  public abstract void setRequestHandler(DHTTransportRequestHandler paramDHTTransportRequestHandler);
  
  public abstract DHTTransportStats getStats();
  
  public abstract void registerTransferHandler(byte[] paramArrayOfByte, DHTTransportTransferHandler paramDHTTransportTransferHandler);
  
  public abstract void registerTransferHandler(byte[] paramArrayOfByte, DHTTransportTransferHandler paramDHTTransportTransferHandler, Map<String, Object> paramMap);
  
  public abstract void unregisterTransferHandler(byte[] paramArrayOfByte, DHTTransportTransferHandler paramDHTTransportTransferHandler);
  
  public abstract byte[] readTransfer(DHTTransportProgressListener paramDHTTransportProgressListener, DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long paramLong)
    throws DHTTransportException;
  
  public abstract void writeTransfer(DHTTransportProgressListener paramDHTTransportProgressListener, DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, long paramLong)
    throws DHTTransportException;
  
  public abstract byte[] writeReadTransfer(DHTTransportProgressListener paramDHTTransportProgressListener, DHTTransportContact paramDHTTransportContact, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long paramLong)
    throws DHTTransportException;
  
  public abstract boolean supportsStorage();
  
  public abstract boolean isReachable();
  
  public abstract DHTTransportContact[] getReachableContacts();
  
  public abstract DHTTransportContact[] getRecentContacts();
  
  public abstract void addListener(DHTTransportListener paramDHTTransportListener);
  
  public abstract void removeListener(DHTTransportListener paramDHTTransportListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */