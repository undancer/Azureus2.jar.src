package com.aelitis.azureus.core.networkmanager;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface Transport
  extends TransportBase
{
  public static final int TRANSPORT_MODE_NORMAL = 0;
  public static final int TRANSPORT_MODE_FAST = 1;
  public static final int TRANSPORT_MODE_TURBO = 2;
  
  public abstract int getMssSize();
  
  public abstract void setAlreadyRead(ByteBuffer paramByteBuffer);
  
  public abstract TransportStartpoint getTransportStartpoint();
  
  public abstract TransportEndpoint getTransportEndpoint();
  
  public abstract boolean isEncrypted();
  
  public abstract String getEncryption(boolean paramBoolean);
  
  public abstract String getProtocol();
  
  public abstract boolean isSOCKS();
  
  public abstract void setReadyForRead();
  
  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void setTransportMode(int paramInt);
  
  public abstract int getTransportMode();
  
  public abstract void connectOutbound(ByteBuffer paramByteBuffer, ConnectListener paramConnectListener, int paramInt);
  
  public abstract void connectedInbound();
  
  public abstract void close(String paramString);
  
  public abstract void bindConnection(NetworkConnection paramNetworkConnection);
  
  public abstract void unbindConnection(NetworkConnection paramNetworkConnection);
  
  public abstract void setTrace(boolean paramBoolean);
  
  public static abstract interface ConnectListener
  {
    public abstract int connectAttemptStarted(int paramInt);
    
    public abstract void connectSuccess(Transport paramTransport, ByteBuffer paramByteBuffer);
    
    public abstract void connectFailure(Throwable paramThrowable);
    
    public abstract Object getConnectionProperty(String paramString);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/Transport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */