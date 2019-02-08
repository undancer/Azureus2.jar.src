package org.gudy.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.gudy.bouncycastle.crypto.InvalidCipherTextException;

public abstract interface BlockCipherPadding
{
  public abstract void init(SecureRandom paramSecureRandom)
    throws IllegalArgumentException;
  
  public abstract String getPaddingName();
  
  public abstract int addPadding(byte[] paramArrayOfByte, int paramInt);
  
  public abstract int padCount(byte[] paramArrayOfByte)
    throws InvalidCipherTextException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/paddings/BlockCipherPadding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */