/*     */ package org.gudy.bouncycastle.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.Provider;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.Security;
/*     */ import java.security.Signature;
/*     */ import java.security.SignatureException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERNull;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x9.X9ObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.jce.X509Principal;
/*     */ import org.gudy.bouncycastle.util.Strings;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class X509Util
/*     */ {
/*  40 */   private static Hashtable algorithms = new Hashtable();
/*  41 */   private static Hashtable params = new Hashtable();
/*  42 */   private static Set noParams = new HashSet();
/*     */   
/*     */   static
/*     */   {
/*  46 */     algorithms.put("MD2WITHRSAENCRYPTION", PKCSObjectIdentifiers.md2WithRSAEncryption);
/*  47 */     algorithms.put("MD2WITHRSA", PKCSObjectIdentifiers.md2WithRSAEncryption);
/*  48 */     algorithms.put("MD5WITHRSAENCRYPTION", PKCSObjectIdentifiers.md5WithRSAEncryption);
/*  49 */     algorithms.put("MD5WITHRSA", PKCSObjectIdentifiers.md5WithRSAEncryption);
/*  50 */     algorithms.put("SHA1WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha1WithRSAEncryption);
/*  51 */     algorithms.put("SHA1WITHRSA", PKCSObjectIdentifiers.sha1WithRSAEncryption);
/*     */     
/*     */ 
/*  54 */     algorithms.put("SHA256WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha256WithRSAEncryption);
/*  55 */     algorithms.put("SHA256WITHRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption);
/*  56 */     algorithms.put("SHA384WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha384WithRSAEncryption);
/*  57 */     algorithms.put("SHA384WITHRSA", PKCSObjectIdentifiers.sha384WithRSAEncryption);
/*  58 */     algorithms.put("SHA512WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha512WithRSAEncryption);
/*  59 */     algorithms.put("SHA512WITHRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  65 */     algorithms.put("RIPEMD160WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
/*  66 */     algorithms.put("RIPEMD160WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
/*  67 */     algorithms.put("RIPEMD128WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
/*  68 */     algorithms.put("RIPEMD128WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
/*  69 */     algorithms.put("RIPEMD256WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
/*  70 */     algorithms.put("RIPEMD256WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
/*  71 */     algorithms.put("SHA1WITHDSA", X9ObjectIdentifiers.id_dsa_with_sha1);
/*  72 */     algorithms.put("DSAWITHSHA1", X9ObjectIdentifiers.id_dsa_with_sha1);
/*     */     
/*     */ 
/*  75 */     algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
/*  76 */     algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
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
/*  91 */     noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA1);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  96 */     noParams.add(X9ObjectIdentifiers.id_dsa_with_sha1);
/*     */   }
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
/*     */   static DERObjectIdentifier getAlgorithmOID(String algorithmName)
/*     */   {
/* 141 */     algorithmName = Strings.toUpperCase(algorithmName);
/*     */     
/* 143 */     if (algorithms.containsKey(algorithmName))
/*     */     {
/* 145 */       return (DERObjectIdentifier)algorithms.get(algorithmName);
/*     */     }
/*     */     
/* 148 */     return new DERObjectIdentifier(algorithmName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static AlgorithmIdentifier getSigAlgID(DERObjectIdentifier sigOid, String algorithmName)
/*     */   {
/* 155 */     if (noParams.contains(sigOid))
/*     */     {
/* 157 */       return new AlgorithmIdentifier(sigOid);
/*     */     }
/*     */     
/* 160 */     algorithmName = Strings.toUpperCase(algorithmName);
/*     */     
/* 162 */     if (params.containsKey(algorithmName))
/*     */     {
/* 164 */       return new AlgorithmIdentifier(sigOid, (DEREncodable)params.get(algorithmName));
/*     */     }
/*     */     
/*     */ 
/* 168 */     return new AlgorithmIdentifier(sigOid, new DERNull());
/*     */   }
/*     */   
/*     */ 
/*     */   static Iterator getAlgNames()
/*     */   {
/* 174 */     Enumeration e = algorithms.keys();
/* 175 */     List l = new ArrayList();
/*     */     
/* 177 */     while (e.hasMoreElements())
/*     */     {
/* 179 */       l.add(e.nextElement());
/*     */     }
/*     */     
/* 182 */     return l.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   static Signature getSignatureInstance(String algorithm)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 189 */     return Signature.getInstance(algorithm);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static Signature getSignatureInstance(String algorithm, String provider)
/*     */     throws NoSuchProviderException, NoSuchAlgorithmException
/*     */   {
/* 197 */     if (provider != null)
/*     */     {
/* 199 */       return Signature.getInstance(algorithm, provider);
/*     */     }
/*     */     
/*     */ 
/* 203 */     return Signature.getInstance(algorithm);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static byte[] calculateSignature(DERObjectIdentifier sigOid, String sigName, PrivateKey key, SecureRandom random, ASN1Encodable object)
/*     */     throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
/*     */   {
/* 217 */     if (sigOid == null)
/*     */     {
/* 219 */       throw new IllegalStateException("no signature algorithm specified");
/*     */     }
/*     */     
/* 222 */     Signature sig = getSignatureInstance(sigName);
/*     */     
/* 224 */     if (random != null)
/*     */     {
/* 226 */       sig.initSign(key, random);
/*     */     }
/*     */     else
/*     */     {
/* 230 */       sig.initSign(key);
/*     */     }
/*     */     
/* 233 */     sig.update(object.getEncoded("DER"));
/*     */     
/* 235 */     return sig.sign();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static byte[] calculateSignature(DERObjectIdentifier sigOid, String sigName, String provider, PrivateKey key, SecureRandom random, ASN1Encodable object)
/*     */     throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
/*     */   {
/* 249 */     if (sigOid == null)
/*     */     {
/* 251 */       throw new IllegalStateException("no signature algorithm specified");
/*     */     }
/*     */     
/* 254 */     Signature sig = getSignatureInstance(sigName, provider);
/*     */     
/* 256 */     if (random != null)
/*     */     {
/* 258 */       sig.initSign(key, random);
/*     */     }
/*     */     else
/*     */     {
/* 262 */       sig.initSign(key);
/*     */     }
/*     */     
/* 265 */     sig.update(object.getEncoded("DER"));
/*     */     
/* 267 */     return sig.sign();
/*     */   }
/*     */   
/*     */ 
/*     */   static X509Principal convertPrincipal(X500Principal principal)
/*     */   {
/*     */     try
/*     */     {
/* 275 */       return new X509Principal(principal.getEncoded());
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 279 */       throw new IllegalArgumentException("cannot convert principal");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static class Implementation
/*     */   {
/*     */     Object engine;
/*     */     
/*     */     Provider provider;
/*     */     
/*     */     Implementation(Object engine, Provider provider)
/*     */     {
/* 292 */       this.engine = engine;
/* 293 */       this.provider = provider;
/*     */     }
/*     */     
/*     */     Object getEngine()
/*     */     {
/* 298 */       return this.engine;
/*     */     }
/*     */     
/*     */     Provider getProvider()
/*     */     {
/* 303 */       return this.provider;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Implementation getImplementation(String baseName, String algorithm, Provider prov)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 317 */     algorithm = Strings.toUpperCase(algorithm);
/*     */     
/*     */     String alias;
/*     */     
/* 321 */     while ((alias = prov.getProperty("Alg.Alias." + baseName + "." + algorithm)) != null)
/*     */     {
/* 323 */       algorithm = alias;
/*     */     }
/*     */     
/* 326 */     String className = prov.getProperty(baseName + "." + algorithm);
/*     */     
/* 328 */     if (className != null)
/*     */     {
/*     */       try
/*     */       {
/*     */ 
/* 333 */         ClassLoader clsLoader = prov.getClass().getClassLoader();
/*     */         Class cls;
/* 335 */         Class cls; if (clsLoader != null)
/*     */         {
/* 337 */           cls = clsLoader.loadClass(className);
/*     */         }
/*     */         else
/*     */         {
/* 341 */           cls = Class.forName(className);
/*     */         }
/*     */         
/* 344 */         return new Implementation(cls.newInstance(), prov);
/*     */       }
/*     */       catch (ClassNotFoundException e)
/*     */       {
/* 348 */         throw new IllegalStateException("algorithm " + algorithm + " in provider " + prov.getName() + " but no class \"" + className + "\" found!");
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 353 */         throw new IllegalStateException("algorithm " + algorithm + " in provider " + prov.getName() + " but class \"" + className + "\" inaccessible!");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 358 */     throw new NoSuchAlgorithmException("cannot find implementation " + algorithm + " for provider " + prov.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Implementation getImplementation(String baseName, String algorithm)
/*     */     throws NoSuchAlgorithmException
/*     */   {
/* 370 */     Provider[] prov = Security.getProviders();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 375 */     for (int i = 0; i != prov.length; i++)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 380 */       Implementation imp = getImplementation(baseName, Strings.toUpperCase(algorithm), prov[i]);
/* 381 */       if (imp != null)
/*     */       {
/* 383 */         return imp;
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 388 */         imp = getImplementation(baseName, algorithm, prov[i]);
/*     */       }
/*     */       catch (NoSuchAlgorithmException e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 396 */     throw new NoSuchAlgorithmException("cannot find implementation " + algorithm);
/*     */   }
/*     */   
/*     */   static Provider getProvider(String provider)
/*     */     throws NoSuchProviderException
/*     */   {
/* 402 */     Provider prov = Security.getProvider(provider);
/*     */     
/* 404 */     if (prov == null)
/*     */     {
/* 406 */       throw new NoSuchProviderException("Provider " + provider + " not found");
/*     */     }
/*     */     
/* 409 */     return prov;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/x509/X509Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */