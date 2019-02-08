package com.aelitis.azureus.core.clientmessageservice;

import java.io.IOException;
import java.util.Map;

public abstract interface ClientMessageService
{
  public abstract void sendMessage(Map paramMap)
    throws IOException;
  
  public abstract Map receiveMessage()
    throws IOException;
  
  public abstract void close();
  
  public abstract void setMaximumMessageSize(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/ClientMessageService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */