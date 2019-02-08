package org.gudy.bouncycastle.jce.interfaces;

import java.math.BigInteger;
import java.security.PublicKey;

public abstract interface ElGamalPublicKey
  extends ElGamalKey, PublicKey
{
  public abstract BigInteger getY();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/interfaces/ElGamalPublicKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */