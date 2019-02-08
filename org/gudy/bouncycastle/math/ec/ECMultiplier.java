package org.gudy.bouncycastle.math.ec;

import java.math.BigInteger;

abstract interface ECMultiplier
{
  public abstract ECPoint multiply(ECPoint paramECPoint, BigInteger paramBigInteger, PreCompInfo paramPreCompInfo);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/math/ec/ECMultiplier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */