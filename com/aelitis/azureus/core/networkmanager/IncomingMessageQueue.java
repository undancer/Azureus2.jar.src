package com.aelitis.azureus.core.networkmanager;

import com.aelitis.azureus.core.peermanager.messaging.Message;
import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
import java.io.IOException;

public abstract interface IncomingMessageQueue
{
  public abstract void setDecoder(MessageStreamDecoder paramMessageStreamDecoder);
  
  public abstract MessageStreamDecoder getDecoder();
  
  public abstract int getPercentDoneOfCurrentMessage();
  
  public abstract int[] receiveFromTransport(int paramInt, boolean paramBoolean)
    throws IOException;
  
  public abstract void notifyOfExternallyReceivedMessage(Message paramMessage)
    throws IOException;
  
  public abstract void resumeQueueProcessing();
  
  public abstract void registerQueueListener(MessageQueueListener paramMessageQueueListener);
  
  public abstract void cancelQueueListener(MessageQueueListener paramMessageQueueListener);
  
  public abstract void destroy();
  
  public static abstract interface MessageQueueListener
  {
    public abstract boolean messageReceived(Message paramMessage)
      throws IOException;
    
    public abstract void protocolBytesReceived(int paramInt);
    
    public abstract void dataBytesReceived(int paramInt);
    
    public abstract boolean isPriority();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/IncomingMessageQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */