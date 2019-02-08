/*     */ package org.gudy.azureus2.core3.security;
/*     */ 
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.net.URL;
/*     */ import java.security.KeyStore;
/*     */ import java.security.cert.Certificate;
/*     */ import javax.net.ssl.SSLServerSocketFactory;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ import org.gudy.azureus2.core3.security.impl.SESecurityManagerImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SESecurityManager
/*     */ {
/*     */   public static final String SSL_CERTS = ".certs";
/*     */   public static final String SSL_KEYS = ".keystore";
/*     */   public static final String SSL_PASSWORD = "changeit";
/*     */   public static final String DEFAULT_ALIAS = "Azureus";
/*     */   
/*     */   public static void initialise()
/*     */   {
/*  53 */     SESecurityManagerImpl.getSingleton().initialise();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void exitVM(int status)
/*     */   {
/*  60 */     SESecurityManagerImpl.getSingleton().exitVM(status);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void stopThread(Thread t)
/*     */   {
/*  67 */     SESecurityManagerImpl.getSingleton().stopThread(t);
/*     */   }
/*     */   
/*     */   public static void installAuthenticator()
/*     */   {
/*  72 */     SESecurityManagerImpl.getSingleton().installAuthenticator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean resetTrustStore(boolean test_only)
/*     */   {
/*  79 */     return SESecurityManagerImpl.getSingleton().resetTrustStore(test_only);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getKeystoreName()
/*     */   {
/*  85 */     return SESecurityManagerImpl.getSingleton().getKeystoreName();
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getKeystorePassword()
/*     */   {
/*  91 */     return SESecurityManagerImpl.getSingleton().getKeystorePassword();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static SSLServerSocketFactory getSSLServerSocketFactory()
/*     */     throws Exception
/*     */   {
/*  99 */     return SESecurityManagerImpl.getSingleton().getSSLServerSocketFactory();
/*     */   }
/*     */   
/*     */ 
/*     */   public static TrustManagerFactory getTrustManagerFactory()
/*     */   {
/* 105 */     return SESecurityManagerImpl.getSingleton().getTrustManagerFactory();
/*     */   }
/*     */   
/*     */ 
/*     */   public static TrustManager[] getAllTrustingTrustManager()
/*     */   {
/* 111 */     return SESecurityManagerImpl.getSingleton().getAllTrustingTrustManager();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static TrustManager[] getAllTrustingTrustManager(X509TrustManager delegate)
/*     */   {
/* 118 */     return SESecurityManagerImpl.getSingleton().getAllTrustingTrustManager(delegate);
/*     */   }
/*     */   
/*     */ 
/*     */   public static SSLSocketFactory getSSLSocketFactory()
/*     */   {
/* 124 */     return SESecurityManagerImpl.getSingleton().getSSLSocketFactory();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static SSLSocketFactory installServerCertificates(URL https_url)
/*     */   {
/* 131 */     return SESecurityManagerImpl.getSingleton().installServerCertificates(https_url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SSLSocketFactory installServerCertificates(String alias, String ip, int port)
/*     */   {
/* 140 */     return SESecurityManagerImpl.getSingleton().installServerCertificates(alias, ip, port);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Certificate createSelfSignedCertificate(String alias, String cert_dn, int strength)
/*     */     throws Exception
/*     */   {
/* 151 */     return SESecurityManagerImpl.getSingleton().createSelfSignedCertificate(alias, cert_dn, strength);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SEKeyDetails getKeyDetails(String alias)
/*     */     throws Exception
/*     */   {
/* 160 */     return SESecurityManagerImpl.getSingleton().getKeyDetails(alias);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static KeyStore getKeyStore()
/*     */     throws Exception
/*     */   {
/* 168 */     return SESecurityManagerImpl.getSingleton().getKeyStore();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static KeyStore getTrustStore()
/*     */     throws Exception
/*     */   {
/* 176 */     return SESecurityManagerImpl.getSingleton().getTrustStore();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PasswordAuthentication getPasswordAuthentication(String realm, URL tracker)
/*     */   {
/* 184 */     return SESecurityManagerImpl.getSingleton().getPasswordAuthentication(realm, tracker);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setPasswordAuthenticationOutcome(String realm, URL tracker, boolean success)
/*     */   {
/* 193 */     SESecurityManagerImpl.getSingleton().setPasswordAuthenticationOutcome(realm, tracker, success);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addPasswordListener(SEPasswordListener l)
/*     */   {
/* 200 */     SESecurityManagerImpl.getSingleton().addPasswordListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removePasswordListener(SEPasswordListener l)
/*     */   {
/* 207 */     SESecurityManagerImpl.getSingleton().removePasswordListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void clearPasswords()
/*     */   {
/* 213 */     SESecurityManagerImpl.getSingleton().clearPasswords();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setThreadPasswordHandler(SEPasswordListener l)
/*     */   {
/* 220 */     SESecurityManagerImpl.getSingleton().setThreadPasswordHandler(l);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void unsetThreadPasswordHandler()
/*     */   {
/* 226 */     SESecurityManagerImpl.getSingleton().unsetThreadPasswordHandler();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setPasswordHandler(URL url, SEPasswordListener l)
/*     */   {
/* 235 */     SESecurityManagerImpl.getSingleton().setPasswordHandler(url, l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addCertificateListener(SECertificateListener l)
/*     */   {
/* 242 */     SESecurityManagerImpl.getSingleton().addCertificateListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setCertificateHandler(URL url, SECertificateListener l)
/*     */   {
/* 250 */     SESecurityManagerImpl.getSingleton().setCertificateHandler(url, l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeCertificateListener(SECertificateListener l)
/*     */   {
/* 257 */     SESecurityManagerImpl.getSingleton().removeCertificateListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */   public static Class[] getClassContext()
/*     */   {
/* 263 */     return SESecurityManagerImpl.getSingleton().getClassContext();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/security/SESecurityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */