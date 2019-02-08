package com.aelitis.azureus.core.peermanager.messaging;

import com.aelitis.azureus.core.networkmanager.Transport;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface MessageStreamDecoder
{
  public abstract int performStreamDecode(Transport paramTransport, int paramInt)
    throws IOException;
  
  public abstract Message[] removeDecodedMessages();
  
  public abstract int getProtocolBytesDecoded();
  
  public abstract int getDataBytesDecoded();
  
  public abstract int getPercentDoneOfCurrentMessage();
  
  public abstract void pauseDecoding();
  
  public abstract void resumeDecoding();
  
  public abstract ByteBuffer destroy();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/MessageStreamDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */