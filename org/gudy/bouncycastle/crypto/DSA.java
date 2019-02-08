package org.gudy.bouncycastle.crypto;

import java.math.BigInteger;

public abstract interface DSA
{
  public abstract void init(boolean paramBoolean, CipherParameters paramCipherParameters);
  
  public abstract BigInteger[] generateSignature(byte[] paramArrayOfByte);
  
  public abstract boolean verifySignature(byte[] paramArrayOfByte, BigInteger paramBigInteger1, BigInteger paramBigInteger2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/DSA.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */