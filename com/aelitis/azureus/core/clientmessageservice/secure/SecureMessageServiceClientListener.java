package com.aelitis.azureus.core.clientmessageservice.secure;

public abstract interface SecureMessageServiceClientListener
{
  public abstract void complete(SecureMessageServiceClientMessage paramSecureMessageServiceClientMessage);
  
  public abstract void cancelled(SecureMessageServiceClientMessage paramSecureMessageServiceClientMessage);
  
  public abstract void aborted(SecureMessageServiceClientMessage paramSecureMessageServiceClientMessage, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/secure/SecureMessageServiceClientListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */