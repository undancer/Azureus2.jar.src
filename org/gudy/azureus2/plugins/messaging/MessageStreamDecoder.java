package org.gudy.azureus2.plugins.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.gudy.azureus2.plugins.network.Transport;

public abstract interface MessageStreamDecoder
{
  public abstract int performStreamDecode(Transport paramTransport, int paramInt)
    throws IOException;
  
  public abstract Message[] removeDecodedMessages();
  
  public abstract int getProtocolBytesDecoded();
  
  public abstract int getDataBytesDecoded();
  
  public abstract void pauseDecoding();
  
  public abstract void resumeDecoding();
  
  public abstract ByteBuffer destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/MessageStreamDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */