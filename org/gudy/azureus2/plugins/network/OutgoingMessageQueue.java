package org.gudy.azureus2.plugins.network;

import org.gudy.azureus2.plugins.messaging.Message;
import org.gudy.azureus2.plugins.messaging.MessageStreamEncoder;

public abstract interface OutgoingMessageQueue
{
  public abstract void setEncoder(MessageStreamEncoder paramMessageStreamEncoder);
  
  public abstract void sendMessage(Message paramMessage);
  
  public abstract void registerListener(OutgoingMessageQueueListener paramOutgoingMessageQueueListener);
  
  public abstract void deregisterListener(OutgoingMessageQueueListener paramOutgoingMessageQueueListener);
  
  public abstract void notifyOfExternalSend(Message paramMessage);
  
  public abstract int getPercentDoneOfCurrentMessage();
  
  public abstract int getDataQueuedBytes();
  
  public abstract int getProtocolQueuedBytes();
  
  public abstract boolean isBlocked();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/network/OutgoingMessageQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */