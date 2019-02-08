package org.gudy.bouncycastle.crypto;

public abstract interface Digest
{
  public abstract String getAlgorithmName();
  
  public abstract int getDigestSize();
  
  public abstract void update(byte paramByte);
  
  public abstract void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract int doFinal(byte[] paramArrayOfByte, int paramInt);
  
  public abstract void reset();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */