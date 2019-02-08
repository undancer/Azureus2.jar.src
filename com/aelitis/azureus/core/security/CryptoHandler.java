package com.aelitis.azureus.core.security;

import java.security.PrivateKey;
import java.security.PublicKey;

public abstract interface CryptoHandler
{
  public abstract int getType();
  
  public abstract void unlock()
    throws CryptoManagerException;
  
  public abstract void lock();
  
  public abstract boolean isUnlocked();
  
  public abstract int getUnlockTimeoutSeconds();
  
  public abstract void setUnlockTimeoutSeconds(int paramInt);
  
  public abstract byte[] sign(byte[] paramArrayOfByte, String paramString)
    throws CryptoManagerException;
  
  public abstract boolean verify(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    throws CryptoManagerException;
  
  public abstract byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString)
    throws CryptoManagerException;
  
  public abstract byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString)
    throws CryptoManagerException;
  
  public abstract CryptoSTSEngine getSTSEngine(String paramString)
    throws CryptoManagerException;
  
  public abstract CryptoSTSEngine getSTSEngine(PublicKey paramPublicKey, PrivateKey paramPrivateKey)
    throws CryptoManagerException;
  
  public abstract byte[] peekPublicKey();
  
  public abstract byte[] getPublicKey(String paramString)
    throws CryptoManagerException;
  
  public abstract byte[] getEncryptedPrivateKey(String paramString)
    throws CryptoManagerException;
  
  public abstract boolean verifyPublicKey(byte[] paramArrayOfByte);
  
  public abstract void recoverKeys(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws CryptoManagerException;
  
  public abstract void resetKeys(String paramString)
    throws CryptoManagerException;
  
  public abstract String exportKeys()
    throws CryptoManagerException;
  
  public abstract int getDefaultPasswordHandlerType();
  
  public abstract void setDefaultPasswordHandlerType(int paramInt)
    throws CryptoManagerException;
  
  public abstract boolean importKeys(String paramString)
    throws CryptoManagerException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/CryptoHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */