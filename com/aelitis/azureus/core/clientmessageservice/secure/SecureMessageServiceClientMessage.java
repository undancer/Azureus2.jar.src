package com.aelitis.azureus.core.clientmessageservice.secure;

import java.util.Map;

public abstract interface SecureMessageServiceClientMessage
{
  public abstract Map getRequest();
  
  public abstract Map getReply();
  
  public abstract Object getClientData();
  
  public abstract void cancel();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/secure/SecureMessageServiceClientMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */