package com.aelitis.azureus.core.networkmanager;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public abstract interface ProtocolEndpoint
{
  public static final int PROTOCOL_TCP = 1;
  public static final int PROTOCOL_UDP = 2;
  public static final int PROTOCOL_UTP = 3;
  public static final int CONNECT_PRIORITY_SUPER_HIGHEST = 0;
  public static final int CONNECT_PRIORITY_HIGHEST = 1;
  public static final int CONNECT_PRIORITY_HIGH = 2;
  public static final int CONNECT_PRIORITY_MEDIUM = 3;
  public static final int CONNECT_PRIORITY_LOW = 4;
  
  public abstract int getType();
  
  public abstract ConnectionEndpoint getConnectionEndpoint();
  
  public abstract void setConnectionEndpoint(ConnectionEndpoint paramConnectionEndpoint);
  
  public abstract InetSocketAddress getAddress();
  
  public abstract InetSocketAddress getAdjustedAddress(boolean paramBoolean);
  
  public abstract Transport connectOutbound(boolean paramBoolean1, boolean paramBoolean2, byte[][] paramArrayOfByte, ByteBuffer paramByteBuffer, int paramInt, Transport.ConnectListener paramConnectListener);
  
  public abstract String getDescription();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/ProtocolEndpoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */