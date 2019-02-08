package org.gudy.bouncycastle.jce.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

public abstract interface ECPrivateKey
  extends ECKey, PrivateKey
{
  public abstract BigInteger getD();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/interfaces/ECPrivateKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */