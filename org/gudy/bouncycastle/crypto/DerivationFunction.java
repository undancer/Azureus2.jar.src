package org.gudy.bouncycastle.crypto;

public abstract interface DerivationFunction
{
  public abstract void init(DerivationParameters paramDerivationParameters);
  
  public abstract Digest getDigest();
  
  public abstract int generateBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws DataLengthException, IllegalArgumentException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/DerivationFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */