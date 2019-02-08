package org.gudy.azureus2.plugins.utils.security;

public abstract interface SEPublicKey
{
  public static final int KEY_TYPE_ECC_192 = 1;
  
  public abstract int getType();
  
  public abstract byte[] encodePublicKey();
  
  public abstract byte[] encodeRawPublicKey();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/security/SEPublicKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */