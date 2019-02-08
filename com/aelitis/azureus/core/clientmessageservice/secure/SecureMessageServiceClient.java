package com.aelitis.azureus.core.clientmessageservice.secure;

import java.util.Map;

public abstract interface SecureMessageServiceClient
{
  public abstract SecureMessageServiceClientMessage sendMessage(Map paramMap, Object paramObject, String paramString);
  
  public abstract void sendMessages();
  
  public abstract SecureMessageServiceClientMessage[] getMessages();
  
  public abstract void addListener(SecureMessageServiceClientListener paramSecureMessageServiceClientListener);
  
  public abstract void removeListener(SecureMessageServiceClientListener paramSecureMessageServiceClientListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/secure/SecureMessageServiceClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */