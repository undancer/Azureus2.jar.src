package org.gudy.azureus2.plugins.utils.security;

import java.net.Authenticator;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import javax.net.ssl.SSLSocketFactory;
import org.gudy.azureus2.plugins.messaging.generic.GenericMessageConnection;

public abstract interface SESecurityManager
{
  public static final int BLOCK_ENCRYPTION_NONE = 1;
  public static final int BLOCK_ENCRYPTION_AES = 2;
  
  public abstract void runWithAuthenticator(Authenticator paramAuthenticator, Runnable paramRunnable);
  
  public abstract void addPasswordListener(PasswordListener paramPasswordListener);
  
  public abstract void removePasswordListener(PasswordListener paramPasswordListener);
  
  public abstract void addCertificateListener(CertificateListener paramCertificateListener);
  
  public abstract void removeCertificateListener(CertificateListener paramCertificateListener);
  
  public abstract byte[] calculateSHA1(byte[] paramArrayOfByte);
  
  public abstract SSLSocketFactory installServerCertificate(URL paramURL);
  
  public abstract KeyStore getKeyStore()
    throws Exception;
  
  public abstract KeyStore getTrustStore()
    throws Exception;
  
  public abstract Certificate createSelfSignedCertificate(String paramString1, String paramString2, int paramInt)
    throws Exception;
  
  public abstract byte[] getIdentity();
  
  public abstract SEPublicKey getPublicKey(int paramInt, String paramString)
    throws Exception;
  
  public abstract SEPublicKey decodePublicKey(byte[] paramArrayOfByte)
    throws Exception;
  
  public abstract GenericMessageConnection getSTSConnection(GenericMessageConnection paramGenericMessageConnection, SEPublicKey paramSEPublicKey, SEPublicKeyLocator paramSEPublicKeyLocator, String paramString, int paramInt)
    throws Exception;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/security/SESecurityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */