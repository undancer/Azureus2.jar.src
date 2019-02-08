package org.gudy.bouncycastle.jce.interfaces;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

public abstract interface IESKey
  extends Key
{
  public abstract PublicKey getPublic();
  
  public abstract PrivateKey getPrivate();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/interfaces/IESKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */