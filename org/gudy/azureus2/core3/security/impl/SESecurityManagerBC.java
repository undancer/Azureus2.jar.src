/*     */ package org.gudy.azureus2.core3.security.impl;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.net.URL;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.KeyPair;
/*     */ import java.security.KeyPairGenerator;
/*     */ import java.security.Security;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Calendar;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Name;
/*     */ import org.gudy.bouncycastle.jce.X509V3CertificateGenerator;
/*     */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
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
/*     */ public class SESecurityManagerBC
/*     */ {
/*     */   protected static void initialise()
/*     */   {
/*     */     try
/*     */     {
/*  51 */       Security.addProvider(new BouncyCastleProvider());
/*     */       
/*  53 */       KeyFactory kf = KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
/*     */       
/*  55 */       if (Constants.IS_CVS_VERSION)
/*     */       {
/*  57 */         String where = "";
/*     */         
/*     */         try
/*     */         {
/*  61 */           where = BouncyCastleProvider.class.getClassLoader().getResource("org/gudy/bouncycastle/jce/provider/BouncyCastleProvider.class").toExternalForm();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*  65 */           ClassLoader cl = BouncyCastleProvider.class.getClassLoader();
/*     */           
/*  67 */           if (cl == null)
/*     */           {
/*  69 */             where = "<bootstrap>";
/*     */           }
/*     */           else
/*     */           {
/*  73 */             where = cl.toString();
/*     */           }
/*     */         }
/*     */         
/*  77 */         if (!where.contains("Azureus2"))
/*     */         {
/*  79 */           Debug.outNoStack("BC Provider '" + BouncyCastleProvider.PROVIDER_NAME + "' initialised successfully (loaded from " + where + ")");
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/*  84 */       Debug.out("BC Provider initialisation failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Certificate createSelfSignedCertificate(SESecurityManagerImpl manager, String alias, String cert_dn, int strength)
/*     */     throws Exception
/*     */   {
/*  97 */     KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
/*     */     
/*  99 */     kg.initialize(strength, RandomUtils.SECURE_RANDOM);
/*     */     
/* 101 */     KeyPair pair = kg.generateKeyPair();
/*     */     
/* 103 */     X509V3CertificateGenerator certificateGenerator = new X509V3CertificateGenerator();
/*     */     
/*     */ 
/* 106 */     certificateGenerator.setSignatureAlgorithm("MD5WithRSAEncryption");
/*     */     
/* 108 */     certificateGenerator.setSerialNumber(new BigInteger("" + SystemTime.getCurrentTime()));
/*     */     
/* 110 */     X509Name issuer_dn = new X509Name(true, cert_dn);
/*     */     
/* 112 */     certificateGenerator.setIssuerDN(issuer_dn);
/*     */     
/* 114 */     X509Name subject_dn = new X509Name(true, cert_dn);
/*     */     
/* 116 */     certificateGenerator.setSubjectDN(subject_dn);
/*     */     
/* 118 */     Calendar not_after = Calendar.getInstance();
/*     */     
/* 120 */     not_after.add(1, 1);
/*     */     
/* 122 */     certificateGenerator.setNotAfter(not_after.getTime());
/*     */     
/* 124 */     certificateGenerator.setNotBefore(Calendar.getInstance().getTime());
/*     */     
/* 126 */     certificateGenerator.setPublicKey(pair.getPublic());
/*     */     
/* 128 */     X509Certificate certificate = certificateGenerator.generateX509Certificate(pair.getPrivate());
/*     */     
/* 130 */     Certificate[] certChain = { certificate };
/*     */     
/* 132 */     manager.addCertToKeyStore(alias, pair.getPrivate(), certChain);
/*     */     
/* 134 */     return certificate;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/security/impl/SESecurityManagerBC.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */