package com.aelitis.azureus.core.crypto;

public abstract interface VuzeCryptoListener
{
  public abstract char[] getSessionPassword(String paramString)
    throws VuzeCryptoException;
  
  public abstract void sessionPasswordCorrect();
  
  public abstract void sessionPasswordIncorrect();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/crypto/VuzeCryptoListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */