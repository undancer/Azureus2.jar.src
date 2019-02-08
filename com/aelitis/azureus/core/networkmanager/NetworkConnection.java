package com.aelitis.azureus.core.networkmanager;

import java.nio.ByteBuffer;

public abstract interface NetworkConnection
  extends NetworkConnectionBase
{
  public abstract void connect(int paramInt, ConnectionListener paramConnectionListener);
  
  public abstract void connect(ByteBuffer paramByteBuffer, int paramInt, ConnectionListener paramConnectionListener);
  
  public abstract void close(String paramString);
  
  public abstract void startMessageProcessing();
  
  public abstract void enableEnhancedMessageProcessing(boolean paramBoolean, int paramInt);
  
  public abstract Transport detachTransport();
  
  public abstract Transport getTransport();
  
  public abstract boolean isConnected();
  
  public abstract Object setUserData(Object paramObject1, Object paramObject2);
  
  public abstract Object getUserData(Object paramObject);
  
  public static abstract interface ConnectionListener
  {
    public abstract int connectStarted(int paramInt);
    
    public abstract void connectSuccess(ByteBuffer paramByteBuffer);
    
    public abstract void connectFailure(Throwable paramThrowable);
    
    public abstract void exceptionThrown(Throwable paramThrowable);
    
    public abstract Object getConnectionProperty(String paramString);
    
    public abstract String getDescription();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/NetworkConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */