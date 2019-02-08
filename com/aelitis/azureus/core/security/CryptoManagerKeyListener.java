package com.aelitis.azureus.core.security;

public abstract interface CryptoManagerKeyListener
{
  public abstract void keyChanged(CryptoHandler paramCryptoHandler);
  
  public abstract void keyLockStatusChanged(CryptoHandler paramCryptoHandler);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/CryptoManagerKeyListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */