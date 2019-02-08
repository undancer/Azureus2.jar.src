package com.aelitis.azureus.core.dht.transport;

import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public abstract interface DHTTransportContact
{
  public static final int RANDOM_ID_TYPE1 = 1;
  public static final int RANDOM_ID_TYPE2 = 2;
  
  public abstract int getMaxFailForLiveCount();
  
  public abstract int getMaxFailForUnknownCount();
  
  public abstract int getInstanceID();
  
  public abstract byte[] getID();
  
  public abstract byte getProtocolVersion();
  
  public abstract long getClockSkew();
  
  public abstract int getRandomIDType();
  
  public abstract void setRandomID(int paramInt);
  
  public abstract int getRandomID();
  
  public abstract void setRandomID2(byte[] paramArrayOfByte);
  
  public abstract byte[] getRandomID2();
  
  public abstract String getName();
  
  public abstract byte[] getBloomKey();
  
  public abstract InetSocketAddress getAddress();
  
  public abstract InetSocketAddress getTransportAddress();
  
  public abstract InetSocketAddress getExternalAddress();
  
  public abstract boolean isAlive(long paramLong);
  
  public abstract void isAlive(DHTTransportReplyHandler paramDHTTransportReplyHandler, long paramLong);
  
  public abstract boolean isValid();
  
  public abstract boolean isSleeping();
  
  public abstract void sendPing(DHTTransportReplyHandler paramDHTTransportReplyHandler);
  
  public abstract void sendImmediatePing(DHTTransportReplyHandler paramDHTTransportReplyHandler, long paramLong);
  
  public abstract void sendStats(DHTTransportReplyHandler paramDHTTransportReplyHandler);
  
  public abstract void sendStore(DHTTransportReplyHandler paramDHTTransportReplyHandler, byte[][] paramArrayOfByte, DHTTransportValue[][] paramArrayOfDHTTransportValue, boolean paramBoolean);
  
  public abstract void sendQueryStore(DHTTransportReplyHandler paramDHTTransportReplyHandler, int paramInt, List<Object[]> paramList);
  
  public abstract void sendFindNode(DHTTransportReplyHandler paramDHTTransportReplyHandler, byte[] paramArrayOfByte, short paramShort);
  
  public abstract void sendFindValue(DHTTransportReplyHandler paramDHTTransportReplyHandler, byte[] paramArrayOfByte, int paramInt, short paramShort);
  
  public abstract void sendKeyBlock(DHTTransportReplyHandler paramDHTTransportReplyHandler, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  public abstract DHTTransportFullStats getStats();
  
  public abstract void exportContact(DataOutputStream paramDataOutputStream)
    throws IOException, DHTTransportException;
  
  public abstract Map<String, Object> exportContactToMap();
  
  public abstract void remove();
  
  public abstract void createNetworkPositions(boolean paramBoolean);
  
  public abstract DHTNetworkPosition[] getNetworkPositions();
  
  public abstract DHTNetworkPosition getNetworkPosition(byte paramByte);
  
  public abstract DHTTransport getTransport();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportContact.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */