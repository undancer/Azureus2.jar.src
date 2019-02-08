/*     */ package com.aelitis.azureus.core.security;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.Key;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.KeyPair;
/*     */ import java.security.KeyPairGenerator;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Signature;
/*     */ import java.security.spec.KeySpec;
/*     */ import org.gudy.bouncycastle.jce.ECNamedCurveTable;
/*     */ import org.gudy.bouncycastle.jce.interfaces.ECPrivateKey;
/*     */ import org.gudy.bouncycastle.jce.interfaces.ECPublicKey;
/*     */ import org.gudy.bouncycastle.jce.provider.BouncyCastleProvider;
/*     */ import org.gudy.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
/*     */ import org.gudy.bouncycastle.jce.spec.ECPrivateKeySpec;
/*     */ import org.gudy.bouncycastle.jce.spec.ECPublicKeySpec;
/*     */ import org.gudy.bouncycastle.math.ec.ECCurve;
/*     */ import org.gudy.bouncycastle.math.ec.ECPoint;
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
/*     */ public class CryptoECCUtils
/*     */ {
/*  46 */   private static final ECNamedCurveParameterSpec ECCparam = ECNamedCurveTable.getParameterSpec("prime192v2");
/*     */   
/*     */ 
/*     */ 
/*     */   public static KeyPair createKeys()
/*     */     throws CryptoManagerException
/*     */   {
/*     */     try
/*     */     {
/*  55 */       KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
/*     */       
/*  57 */       keyGen.initialize(ECCparam);
/*     */       
/*  59 */       return keyGen.genKeyPair();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  63 */       throw new CryptoManagerException("Failed to create keys", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Signature getSignature(Key key)
/*     */     throws CryptoManagerException
/*     */   {
/*     */     try
/*     */     {
/*  75 */       Signature ECCsig = Signature.getInstance("SHA1withECDSA", BouncyCastleProvider.PROVIDER_NAME);
/*     */       
/*  77 */       if ((key instanceof ECPrivateKey))
/*     */       {
/*  79 */         ECCsig.initSign((ECPrivateKey)key);
/*     */       }
/*  81 */       else if ((key instanceof ECPublicKey))
/*     */       {
/*  83 */         ECCsig.initVerify((ECPublicKey)key);
/*     */       }
/*     */       else
/*     */       {
/*  87 */         throw new CryptoManagerException("Invalid Key Type, ECC keys required");
/*     */       }
/*     */       
/*  90 */       return ECCsig;
/*     */     }
/*     */     catch (CryptoManagerException e)
/*     */     {
/*  94 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  98 */       throw new CryptoManagerException("Failed to create Signature", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] keyToRawdata(PrivateKey privkey)
/*     */     throws CryptoManagerException
/*     */   {
/* 108 */     if (!(privkey instanceof ECPrivateKey))
/*     */     {
/* 110 */       throw new CryptoManagerException("Invalid private key");
/*     */     }
/*     */     
/* 113 */     return ((ECPrivateKey)privkey).getD().toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PrivateKey rawdataToPrivkey(byte[] input)
/*     */     throws CryptoManagerException
/*     */   {
/* 122 */     BigInteger D = new BigInteger(input);
/*     */     
/* 124 */     KeySpec keyspec = new ECPrivateKeySpec(D, ECCparam);
/*     */     
/* 126 */     PrivateKey privkey = null;
/*     */     try
/*     */     {
/* 129 */       return KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME).generatePrivate(keyspec);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 135 */       throw new CryptoManagerException("Failed to decode private key");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] keyToRawdata(PublicKey pubkey)
/*     */     throws CryptoManagerException
/*     */   {
/* 145 */     if (!(pubkey instanceof ECPublicKey))
/*     */     {
/* 147 */       throw new CryptoManagerException("Invalid public key");
/*     */     }
/*     */     
/* 150 */     return ((ECPublicKey)pubkey).getQ().getEncoded();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PublicKey rawdataToPubkey(byte[] input)
/*     */     throws CryptoManagerException
/*     */   {
/* 160 */     ECPoint W = ECCparam.getCurve().decodePoint(input);
/*     */     
/* 162 */     KeySpec keyspec = new ECPublicKeySpec(W, ECCparam);
/*     */     
/*     */     try
/*     */     {
/* 166 */       return KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME).generatePublic(keyspec);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 170 */       throw new CryptoManagerException("Failed to decode public key", e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/CryptoECCUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */