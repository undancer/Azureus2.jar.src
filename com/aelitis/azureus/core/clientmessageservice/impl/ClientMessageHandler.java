package com.aelitis.azureus.core.clientmessageservice.impl;

public abstract interface ClientMessageHandler
{
  public abstract String getMessageTypeID();
  
  public abstract void processMessage(ClientMessage paramClientMessage);
  
  public abstract void sendAttemptCompleted(ClientMessage paramClientMessage);
  
  public abstract void sendAttemptFailed(ClientMessage paramClientMessage, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/impl/ClientMessageHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */