/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.KeyFactorySpi;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.interfaces.DSAPrivateKey;
/*     */ import java.security.interfaces.DSAPublicKey;
/*     */ import java.security.interfaces.RSAPrivateCrtKey;
/*     */ import java.security.interfaces.RSAPrivateKey;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.security.spec.InvalidKeySpecException;
/*     */ import java.security.spec.KeySpec;
/*     */ import java.security.spec.PKCS8EncodedKeySpec;
/*     */ import java.security.spec.RSAPrivateCrtKeySpec;
/*     */ import java.security.spec.RSAPrivateKeySpec;
/*     */ import java.security.spec.RSAPublicKeySpec;
/*     */ import java.security.spec.X509EncodedKeySpec;
/*     */ import javax.crypto.interfaces.DHPrivateKey;
/*     */ import javax.crypto.interfaces.DHPublicKey;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DERInputStream;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PrivateKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509ObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.asn1.x9.X9ObjectIdentifiers;
/*     */ import org.gudy.bouncycastle.jce.interfaces.ElGamalPrivateKey;
/*     */ import org.gudy.bouncycastle.jce.interfaces.ElGamalPublicKey;
/*     */ import org.gudy.bouncycastle.jce.spec.ECPrivateKeySpec;
/*     */ import org.gudy.bouncycastle.jce.spec.ECPublicKeySpec;
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
/*     */ public abstract class JDKKeyFactory
/*     */   extends KeyFactorySpi
/*     */ {
/*     */   protected KeySpec engineGetKeySpec(Key key, Class spec)
/*     */     throws InvalidKeySpecException
/*     */   {
/*  77 */     if ((spec.isAssignableFrom(PKCS8EncodedKeySpec.class)) && (key.getFormat().equals("PKCS#8")))
/*     */     {
/*  79 */       return new PKCS8EncodedKeySpec(key.getEncoded());
/*     */     }
/*  81 */     if ((spec.isAssignableFrom(X509EncodedKeySpec.class)) && (key.getFormat().equals("X.509")))
/*     */     {
/*  83 */       return new X509EncodedKeySpec(key.getEncoded());
/*     */     }
/*  85 */     if ((spec.isAssignableFrom(RSAPublicKeySpec.class)) && ((key instanceof RSAPublicKey)))
/*     */     {
/*  87 */       RSAPublicKey k = (RSAPublicKey)key;
/*     */       
/*  89 */       return new RSAPublicKeySpec(k.getModulus(), k.getPublicExponent());
/*     */     }
/*  91 */     if ((spec.isAssignableFrom(RSAPrivateKeySpec.class)) && ((key instanceof RSAPrivateKey)))
/*     */     {
/*  93 */       RSAPrivateKey k = (RSAPrivateKey)key;
/*     */       
/*  95 */       return new RSAPrivateKeySpec(k.getModulus(), k.getPrivateExponent());
/*     */     }
/*  97 */     if ((spec.isAssignableFrom(RSAPrivateCrtKeySpec.class)) && ((key instanceof RSAPrivateCrtKey)))
/*     */     {
/*  99 */       RSAPrivateCrtKey k = (RSAPrivateCrtKey)key;
/*     */       
/* 101 */       return new RSAPrivateCrtKeySpec(k.getModulus(), k.getPublicExponent(), k.getPrivateExponent(), k.getPrimeP(), k.getPrimeQ(), k.getPrimeExponentP(), k.getPrimeExponentQ(), k.getCrtCoefficient());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 110 */     throw new RuntimeException("not implemented yet " + key + " " + spec);
/*     */   }
/*     */   
/*     */ 
/*     */   protected Key engineTranslateKey(Key key)
/*     */     throws InvalidKeyException
/*     */   {
/* 117 */     if ((key instanceof RSAPublicKey))
/*     */     {
/* 119 */       return new JCERSAPublicKey((RSAPublicKey)key);
/*     */     }
/* 121 */     if (!(key instanceof RSAPrivateCrtKey))
/*     */     {
/*     */ 
/*     */ 
/* 125 */       if (!(key instanceof RSAPrivateKey))
/*     */       {
/*     */ 
/*     */ 
/* 129 */         if (!(key instanceof DHPublicKey))
/*     */         {
/*     */ 
/*     */ 
/* 133 */           if (!(key instanceof DHPrivateKey))
/*     */           {
/*     */ 
/*     */ 
/* 137 */             if (!(key instanceof DSAPublicKey))
/*     */             {
/*     */ 
/*     */ 
/* 141 */               if (!(key instanceof DSAPrivateKey))
/*     */               {
/*     */ 
/*     */ 
/* 145 */                 if (!(key instanceof ElGamalPublicKey))
/*     */                 {
/*     */ 
/*     */ 
/* 149 */                   if (!(key instanceof ElGamalPrivateKey)) {} } } }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 154 */     throw new InvalidKeyException("key type unknown");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static PublicKey createPublicKeyFromDERStream(InputStream in)
/*     */     throws IOException
/*     */   {
/* 162 */     return createPublicKeyFromPublicKeyInfo(new SubjectPublicKeyInfo((ASN1Sequence)new DERInputStream(in).readObject()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static PublicKey createPublicKeyFromPublicKeyInfo(SubjectPublicKeyInfo info)
/*     */   {
/* 169 */     AlgorithmIdentifier algId = info.getAlgorithmId();
/*     */     
/* 171 */     if ((algId.getObjectId().equals(PKCSObjectIdentifiers.rsaEncryption)) || (algId.getObjectId().equals(X509ObjectIdentifiers.id_ea_rsa)))
/*     */     {
/*     */ 
/* 174 */       return new JCERSAPublicKey(info);
/*     */     }
/* 176 */     if (algId.getObjectId().equals(X9ObjectIdentifiers.id_ecPublicKey))
/*     */     {
/* 178 */       return new JCEECPublicKey(info);
/*     */     }
/*     */     
/*     */ 
/* 182 */     throw new RuntimeException("algorithm identifier in key not recognised");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static PrivateKey createPrivateKeyFromDERStream(InputStream in)
/*     */     throws IOException
/*     */   {
/* 190 */     return createPrivateKeyFromPrivateKeyInfo(new PrivateKeyInfo((ASN1Sequence)new DERInputStream(in).readObject()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static PrivateKey createPrivateKeyFromPrivateKeyInfo(PrivateKeyInfo info)
/*     */   {
/* 200 */     AlgorithmIdentifier algId = info.getAlgorithmId();
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
/* 220 */     if (algId.getObjectId().equals(X9ObjectIdentifiers.id_ecPublicKey))
/*     */     {
/* 222 */       return new JCEECPrivateKey(info);
/*     */     }
/*     */     
/*     */ 
/* 226 */     throw new RuntimeException("algorithm identifier in key not recognised");
/*     */   }
/*     */   
/*     */ 
/*     */   public static class EC
/*     */     extends JDKKeyFactory
/*     */   {
/*     */     String algorithm;
/*     */     
/*     */ 
/*     */     public EC()
/*     */       throws NoSuchAlgorithmException
/*     */     {
/* 239 */       this("EC");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 251 */         StackTraceElement[] elements = new Exception().getStackTrace();
/*     */         
/* 253 */         boolean ssl = false;
/* 254 */         boolean x509 = false;
/*     */         
/* 256 */         for (StackTraceElement elt : elements)
/*     */         {
/* 258 */           String name = elt.getClassName() + "." + elt.getMethodName();
/*     */           
/* 260 */           if ((name.contains("SSLSocketFactory")) || (name.contains("KeyStore.load")))
/*     */           {
/* 262 */             ssl = true;
/* 263 */           } else if (name.contains("X509"))
/*     */           {
/*     */ 
/* 266 */             x509 = true;
/*     */           }
/*     */         }
/*     */         
/* 270 */         if ((ssl) && (x509))
/*     */         {
/*     */ 
/*     */ 
/* 274 */           throw new NoSuchAlgorithmException();
/*     */         }
/*     */       }
/*     */       catch (NoSuchAlgorithmException e) {
/* 278 */         throw e;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 282 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public EC(String algorithm)
/*     */     {
/* 289 */       this.algorithm = algorithm;
/*     */     }
/*     */     
/*     */ 
/*     */     protected PrivateKey engineGeneratePrivate(KeySpec keySpec)
/*     */       throws InvalidKeySpecException
/*     */     {
/* 296 */       if ((keySpec instanceof PKCS8EncodedKeySpec))
/*     */       {
/*     */         try
/*     */         {
/* 300 */           return JDKKeyFactory.createPrivateKeyFromDERStream(new ByteArrayInputStream(((PKCS8EncodedKeySpec)keySpec).getEncoded()));
/*     */ 
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 305 */           throw new InvalidKeySpecException(e.toString());
/*     */         }
/*     */       }
/* 308 */       if ((keySpec instanceof ECPrivateKeySpec))
/*     */       {
/* 310 */         return new JCEECPrivateKey(this.algorithm, (ECPrivateKeySpec)keySpec);
/*     */       }
/*     */       
/* 313 */       throw new InvalidKeySpecException("Unknown KeySpec type.");
/*     */     }
/*     */     
/*     */ 
/*     */     protected PublicKey engineGeneratePublic(KeySpec keySpec)
/*     */       throws InvalidKeySpecException
/*     */     {
/* 320 */       if ((keySpec instanceof X509EncodedKeySpec))
/*     */       {
/*     */         try
/*     */         {
/* 324 */           return JDKKeyFactory.createPublicKeyFromDERStream(new ByteArrayInputStream(((X509EncodedKeySpec)keySpec).getEncoded()));
/*     */ 
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 329 */           throw new InvalidKeySpecException(e.toString());
/*     */         }
/*     */       }
/* 332 */       if ((keySpec instanceof ECPublicKeySpec))
/*     */       {
/* 334 */         return new JCEECPublicKey(this.algorithm, (ECPublicKeySpec)keySpec);
/*     */       }
/*     */       
/* 337 */       throw new InvalidKeySpecException("Unknown KeySpec type.");
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ECDSA
/*     */     extends JDKKeyFactory.EC
/*     */   {
/*     */     public ECDSA()
/*     */     {
/* 346 */       super();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JDKKeyFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */