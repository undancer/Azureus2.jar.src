package org.gudy.bouncycastle.crypto;

public abstract interface Mac
{
  public abstract void init(CipherParameters paramCipherParameters)
    throws IllegalArgumentException;
  
  public abstract String getAlgorithmName();
  
  public abstract int getMacSize();
  
  public abstract void update(byte paramByte)
    throws IllegalStateException;
  
  public abstract void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws DataLengthException, IllegalStateException;
  
  public abstract int doFinal(byte[] paramArrayOfByte, int paramInt)
    throws DataLengthException, IllegalStateException;
  
  public abstract void reset();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/Mac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */