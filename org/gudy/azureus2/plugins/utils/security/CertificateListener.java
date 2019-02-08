package org.gudy.azureus2.plugins.utils.security;

import java.security.cert.X509Certificate;

public abstract interface CertificateListener
{
  public abstract boolean trustCertificate(String paramString, X509Certificate paramX509Certificate);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/security/CertificateListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */