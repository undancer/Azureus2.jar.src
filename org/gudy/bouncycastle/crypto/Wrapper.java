package org.gudy.bouncycastle.crypto;

public abstract interface Wrapper
{
  public abstract void init(boolean paramBoolean, CipherParameters paramCipherParameters);
  
  public abstract String getAlgorithmName();
  
  public abstract byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws InvalidCipherTextException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/Wrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */