package org.gudy.azureus2.plugins.messaging;

import java.nio.ByteBuffer;

public abstract interface Message
{
  public static final int TYPE_PROTOCOL_PAYLOAD = 0;
  public static final int TYPE_DATA_PAYLOAD = 1;
  
  public abstract String getID();
  
  public abstract int getType();
  
  public abstract String getDescription();
  
  public abstract ByteBuffer[] getPayload();
  
  public abstract Message create(ByteBuffer paramByteBuffer)
    throws MessageException;
  
  public abstract void destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/Message.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */