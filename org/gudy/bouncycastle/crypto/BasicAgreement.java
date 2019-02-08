package org.gudy.bouncycastle.crypto;

import java.math.BigInteger;

public abstract interface BasicAgreement
{
  public abstract void init(CipherParameters paramCipherParameters);
  
  public abstract BigInteger calculateAgreement(CipherParameters paramCipherParameters);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/BasicAgreement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */