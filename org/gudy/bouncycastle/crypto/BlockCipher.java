package org.gudy.bouncycastle.crypto;

public abstract interface BlockCipher
{
  public abstract void init(boolean paramBoolean, CipherParameters paramCipherParameters)
    throws IllegalArgumentException;
  
  public abstract String getAlgorithmName();
  
  public abstract int getBlockSize();
  
  public abstract int processBlock(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2)
    throws DataLengthException, IllegalStateException;
  
  public abstract void reset();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/BlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */