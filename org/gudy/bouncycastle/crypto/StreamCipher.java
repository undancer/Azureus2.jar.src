package org.gudy.bouncycastle.crypto;

public abstract interface StreamCipher
{
  public abstract void init(boolean paramBoolean, CipherParameters paramCipherParameters)
    throws IllegalArgumentException;
  
  public abstract String getAlgorithmName();
  
  public abstract byte returnByte(byte paramByte);
  
  public abstract void processBytes(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
    throws DataLengthException;
  
  public abstract void reset();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/StreamCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */