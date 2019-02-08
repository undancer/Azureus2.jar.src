package com.aelitis.azureus.core.networkmanager.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public abstract interface TransportHelper
{
  public abstract InetSocketAddress getAddress();
  
  public abstract String getName(boolean paramBoolean);
  
  public abstract boolean minimiseOverheads();
  
  public abstract int getConnectTimeout();
  
  public abstract int getReadTimeout();
  
  public abstract boolean delayWrite(ByteBuffer paramByteBuffer);
  
  public abstract boolean hasDelayedWrite();
  
  public abstract int write(ByteBuffer paramByteBuffer, boolean paramBoolean)
    throws IOException;
  
  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract int read(ByteBuffer paramByteBuffer)
    throws IOException;
  
  public abstract long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void pauseReadSelects();
  
  public abstract void pauseWriteSelects();
  
  public abstract void resumeReadSelects();
  
  public abstract void resumeWriteSelects();
  
  public abstract void registerForReadSelects(selectListener paramselectListener, Object paramObject);
  
  public abstract void registerForWriteSelects(selectListener paramselectListener, Object paramObject);
  
  public abstract void cancelReadSelects();
  
  public abstract void cancelWriteSelects();
  
  public abstract boolean isClosed();
  
  public abstract void close(String paramString);
  
  public abstract void failed(Throwable paramThrowable);
  
  public abstract void setUserData(Object paramObject1, Object paramObject2);
  
  public abstract Object getUserData(Object paramObject);
  
  public abstract void setTrace(boolean paramBoolean);
  
  public abstract void setScatteringMode(long paramLong);
  
  public static abstract interface selectListener
  {
    public abstract boolean selectSuccess(TransportHelper paramTransportHelper, Object paramObject);
    
    public abstract void selectFailure(TransportHelper paramTransportHelper, Object paramObject, Throwable paramThrowable);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */