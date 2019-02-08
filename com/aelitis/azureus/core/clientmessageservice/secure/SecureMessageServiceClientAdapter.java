package com.aelitis.azureus.core.clientmessageservice.secure;

public abstract interface SecureMessageServiceClientAdapter
{
  public abstract void serverOK();
  
  public abstract void serverFailed(Throwable paramThrowable);
  
  public abstract String getUser();
  
  public abstract byte[] getPassword();
  
  public abstract void authenticationFailed();
  
  public abstract long getMessageSequence();
  
  public abstract void setMessageSequence(long paramLong);
  
  public abstract void log(String paramString);
  
  public abstract void log(String paramString, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/clientmessageservice/secure/SecureMessageServiceClientAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */