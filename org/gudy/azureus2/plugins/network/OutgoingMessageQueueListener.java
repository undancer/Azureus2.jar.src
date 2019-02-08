package org.gudy.azureus2.plugins.network;

import org.gudy.azureus2.plugins.messaging.Message;

public abstract interface OutgoingMessageQueueListener
{
  public abstract boolean messageAdded(Message paramMessage);
  
  public abstract void messageSent(Message paramMessage);
  
  public abstract void bytesSent(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/network/OutgoingMessageQueueListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */