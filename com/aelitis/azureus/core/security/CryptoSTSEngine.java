package com.aelitis.azureus.core.security;

import java.nio.ByteBuffer;

public abstract interface CryptoSTSEngine
{
  public abstract void getKeys(ByteBuffer paramByteBuffer)
    throws CryptoManagerException;
  
  public abstract void putKeys(ByteBuffer paramByteBuffer)
    throws CryptoManagerException;
  
  public abstract void getAuth(ByteBuffer paramByteBuffer)
    throws CryptoManagerException;
  
  public abstract void putAuth(ByteBuffer paramByteBuffer)
    throws CryptoManagerException;
  
  public abstract byte[] getSharedSecret()
    throws CryptoManagerException;
  
  public abstract byte[] getRemotePublicKey()
    throws CryptoManagerException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/CryptoSTSEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */