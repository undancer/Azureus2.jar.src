package org.gudy.azureus2.plugins.network;

import java.nio.ByteBuffer;
import org.gudy.azureus2.plugins.messaging.Message;

public abstract interface RawMessage
  extends Message
{
  public abstract ByteBuffer[] getRawPayload();
  
  public abstract Message getOriginalMessage();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/network/RawMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */