package org.gudy.bouncycastle.crypto;

public abstract interface AsymmetricCipherKeyPairGenerator
{
  public abstract void init(KeyGenerationParameters paramKeyGenerationParameters);
  
  public abstract AsymmetricCipherKeyPair generateKeyPair();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/AsymmetricCipherKeyPairGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */