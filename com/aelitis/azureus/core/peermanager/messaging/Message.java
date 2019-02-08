package com.aelitis.azureus.core.peermanager.messaging;

import org.gudy.azureus2.core3.util.DirectByteBuffer;

public abstract interface Message
{
  public static final int TYPE_PROTOCOL_PAYLOAD = 0;
  public static final int TYPE_DATA_PAYLOAD = 1;
  
  public abstract String getID();
  
  public abstract byte[] getIDBytes();
  
  public abstract String getFeatureID();
  
  public abstract int getFeatureSubID();
  
  public abstract byte getVersion();
  
  public abstract int getType();
  
  public abstract String getDescription();
  
  public abstract DirectByteBuffer[] getData();
  
  public abstract Message deserialize(DirectByteBuffer paramDirectByteBuffer, byte paramByte)
    throws MessageException;
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/Message.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */