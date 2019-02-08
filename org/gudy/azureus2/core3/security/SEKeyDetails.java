package org.gudy.azureus2.core3.security;

import java.security.Key;
import java.security.cert.X509Certificate;

public abstract interface SEKeyDetails
{
  public abstract Key getKey();
  
  public abstract X509Certificate[] getCertificateChain();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/security/SEKeyDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */