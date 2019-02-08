/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.security;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.security.CryptoHandler;
/*     */ import com.aelitis.azureus.core.security.CryptoManager;
/*     */ import java.net.Authenticator;
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.net.URL;
/*     */ import java.security.KeyStore;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import org.gudy.azureus2.core3.security.SECertificateListener;
/*     */ import org.gudy.azureus2.core3.security.SEPasswordListener;
/*     */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*     */ import org.gudy.azureus2.plugins.messaging.generic.GenericMessageConnection;
/*     */ import org.gudy.azureus2.plugins.utils.security.CertificateListener;
/*     */ import org.gudy.azureus2.plugins.utils.security.PasswordListener;
/*     */ import org.gudy.azureus2.plugins.utils.security.SEPublicKey;
/*     */ import org.gudy.azureus2.plugins.utils.security.SEPublicKeyLocator;
/*     */ import org.gudy.azureus2.pluginsimpl.local.messaging.GenericMessageConnectionImpl;
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
/*     */ public class SESecurityManagerImpl
/*     */   implements org.gudy.azureus2.plugins.utils.security.SESecurityManager
/*     */ {
/*     */   private AzureusCore core;
/*  57 */   private Map password_listeners = new HashMap();
/*  58 */   private Map certificate_listeners = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   public SESecurityManagerImpl(AzureusCore _core)
/*     */   {
/*  64 */     this.core = _core;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] calculateSHA1(byte[] data_in)
/*     */   {
/*  71 */     if (data_in == null)
/*     */     {
/*  73 */       data_in = new byte[0];
/*     */     }
/*     */     
/*  76 */     SHA1Hasher hasher = new SHA1Hasher();
/*     */     
/*  78 */     return hasher.calculateHash(data_in);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void runWithAuthenticator(Authenticator authenticator, Runnable target)
/*     */   {
/*     */     try
/*     */     {
/*  87 */       Authenticator.setDefault(authenticator);
/*     */       
/*  89 */       target.run();
/*     */     }
/*     */     finally
/*     */     {
/*  93 */       org.gudy.azureus2.core3.security.SESecurityManager.installAuthenticator();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPasswordListener(final PasswordListener listener)
/*     */   {
/* 101 */     SEPasswordListener sepl = new SEPasswordListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public PasswordAuthentication getAuthentication(String realm, URL tracker)
/*     */       {
/*     */ 
/*     */ 
/* 109 */         return listener.getAuthentication(realm, tracker);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void setAuthenticationOutcome(String realm, URL tracker, boolean success)
/*     */       {
/* 118 */         listener.setAuthenticationOutcome(realm, tracker, success);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void clearPasswords() {}
/* 126 */     };
/* 127 */     this.password_listeners.put(listener, sepl);
/*     */     
/* 129 */     org.gudy.azureus2.core3.security.SESecurityManager.addPasswordListener(sepl);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removePasswordListener(PasswordListener listener)
/*     */   {
/* 136 */     SEPasswordListener sepl = (SEPasswordListener)this.password_listeners.get(listener);
/*     */     
/* 138 */     if (sepl != null)
/*     */     {
/* 140 */       org.gudy.azureus2.core3.security.SESecurityManager.removePasswordListener(sepl);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addCertificateListener(final CertificateListener listener)
/*     */   {
/* 148 */     SECertificateListener sepl = new SECertificateListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public boolean trustCertificate(String resource, X509Certificate cert)
/*     */       {
/*     */ 
/*     */ 
/* 156 */         return listener.trustCertificate(resource, cert);
/*     */       }
/*     */       
/* 159 */     };
/* 160 */     this.certificate_listeners.put(listener, sepl);
/*     */     
/* 162 */     org.gudy.azureus2.core3.security.SESecurityManager.addCertificateListener(sepl);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeCertificateListener(CertificateListener listener)
/*     */   {
/* 169 */     SECertificateListener sepl = (SECertificateListener)this.certificate_listeners.get(listener);
/*     */     
/* 171 */     if (sepl != null)
/*     */     {
/* 173 */       org.gudy.azureus2.core3.security.SESecurityManager.removeCertificateListener(sepl);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SSLSocketFactory installServerCertificate(URL url)
/*     */   {
/* 181 */     return org.gudy.azureus2.core3.security.SESecurityManager.installServerCertificates(url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public KeyStore getKeyStore()
/*     */     throws Exception
/*     */   {
/* 189 */     return org.gudy.azureus2.core3.security.SESecurityManager.getKeyStore();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public KeyStore getTrustStore()
/*     */     throws Exception
/*     */   {
/* 197 */     return org.gudy.azureus2.core3.security.SESecurityManager.getTrustStore();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Certificate createSelfSignedCertificate(String alias, String cert_dn, int strength)
/*     */     throws Exception
/*     */   {
/* 209 */     return org.gudy.azureus2.core3.security.SESecurityManager.createSelfSignedCertificate(alias, cert_dn, strength);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIdentity()
/*     */   {
/* 215 */     return this.core.getCryptoManager().getSecureID();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SEPublicKey getPublicKey(int key_type, String reason_resource)
/*     */     throws Exception
/*     */   {
/* 225 */     byte[] encoded = this.core.getCryptoManager().getECCHandler().getPublicKey(reason_resource);
/*     */     
/* 227 */     return new SEPublicKeyImpl(key_type, encoded);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SEPublicKey decodePublicKey(byte[] encoded)
/*     */   {
/* 234 */     return SEPublicKeyImpl.decode(encoded);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GenericMessageConnection getSTSConnection(GenericMessageConnection connection, SEPublicKey my_public_key, SEPublicKeyLocator key_locator, String reason_resource, int block_crypto)
/*     */     throws Exception
/*     */   {
/* 247 */     return new SESTSConnectionImpl(this.core, (GenericMessageConnectionImpl)connection, my_public_key, key_locator, reason_resource, block_crypto);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/security/SESecurityManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */