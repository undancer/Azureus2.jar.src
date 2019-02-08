package com.aelitis.azureus.core.networkmanager.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface TransportHelperFilter
{
  public abstract long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract boolean hasBufferedWrite();
  
  public abstract boolean hasBufferedRead();
  
  public abstract TransportHelper getHelper();
  
  public abstract String getName(boolean paramBoolean);
  
  public abstract boolean isEncrypted();
  
  public abstract void setTrace(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportHelperFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */