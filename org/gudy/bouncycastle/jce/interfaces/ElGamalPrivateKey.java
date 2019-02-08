package org.gudy.bouncycastle.jce.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

public abstract interface ElGamalPrivateKey
  extends ElGamalKey, PrivateKey
{
  public abstract BigInteger getX();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/interfaces/ElGamalPrivateKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */