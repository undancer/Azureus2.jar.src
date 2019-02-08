package com.aelitis.azureus.core.networkmanager;

import com.aelitis.azureus.core.peermanager.messaging.Message;
import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
import java.io.IOException;

public abstract interface OutgoingMessageQueue
{
  public abstract void setTransport(Transport paramTransport);
  
  public abstract int getMssSize();
  
  public abstract void setEncoder(MessageStreamEncoder paramMessageStreamEncoder);
  
  public abstract MessageStreamEncoder getEncoder();
  
  public abstract int getPercentDoneOfCurrentMessage();
  
  public abstract void destroy();
  
  public abstract boolean isDestroyed();
  
  public abstract int getTotalSize();
  
  public abstract int getDataQueuedBytes();
  
  public abstract int getProtocolQueuedBytes();
  
  public abstract boolean isBlocked();
  
  public abstract boolean getPriorityBoost();
  
  public abstract void setPriorityBoost(boolean paramBoolean);
  
  public abstract boolean hasUrgentMessage();
  
  public abstract Message peekFirstMessage();
  
  public abstract void addMessage(Message paramMessage, boolean paramBoolean);
  
  public abstract void removeMessagesOfType(Message[] paramArrayOfMessage, boolean paramBoolean);
  
  public abstract boolean removeMessage(Message paramMessage, boolean paramBoolean);
  
  public abstract int[] deliverToTransport(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException;
  
  public abstract void doListenerNotifications();
  
  public abstract void flush();
  
  public abstract void setTrace(boolean paramBoolean);
  
  public abstract String getQueueTrace();
  
  public abstract void registerQueueListener(MessageQueueListener paramMessageQueueListener);
  
  public abstract void cancelQueueListener(MessageQueueListener paramMessageQueueListener);
  
  public abstract void notifyOfExternallySentMessage(Message paramMessage);
  
  public static abstract interface MessageQueueListener
  {
    public abstract boolean messageAdded(Message paramMessage);
    
    public abstract void messageQueued(Message paramMessage);
    
    public abstract void messageRemoved(Message paramMessage);
    
    public abstract void messageSent(Message paramMessage);
    
    public abstract void protocolBytesSent(int paramInt);
    
    public abstract void dataBytesSent(int paramInt);
    
    public abstract void flush();
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/OutgoingMessageQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */