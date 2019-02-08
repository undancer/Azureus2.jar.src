package org.gudy.azureus2.plugins.network;

import java.io.IOException;
import org.gudy.azureus2.plugins.messaging.Message;

public abstract interface IncomingMessageQueue
{
  public abstract void registerListener(IncomingMessageQueueListener paramIncomingMessageQueueListener);
  
  public abstract void registerPriorityListener(IncomingMessageQueueListener paramIncomingMessageQueueListener);
  
  public abstract void deregisterListener(IncomingMessageQueueListener paramIncomingMessageQueueListener);
  
  public abstract void notifyOfExternalReceive(Message paramMessage)
    throws IOException;
  
  public abstract int getPercentDoneOfCurrentMessage();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/network/IncomingMessageQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */