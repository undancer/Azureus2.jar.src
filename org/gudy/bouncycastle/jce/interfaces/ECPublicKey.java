package org.gudy.bouncycastle.jce.interfaces;

import java.security.PublicKey;
import org.gudy.bouncycastle.math.ec.ECPoint;

public abstract interface ECPublicKey
  extends ECKey, PublicKey
{
  public abstract ECPoint getQ();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/interfaces/ECPublicKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */