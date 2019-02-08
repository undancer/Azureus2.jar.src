/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import javax.crypto.KeyAgreementSpi;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.ShortBufferException;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.gudy.bouncycastle.crypto.BasicAgreement;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.agreement.ECDHBasicAgreement;
/*     */ import org.gudy.bouncycastle.jce.interfaces.ECPrivateKey;
/*     */ import org.gudy.bouncycastle.jce.interfaces.ECPublicKey;
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
/*     */ public class JCEECDHKeyAgreement
/*     */   extends KeyAgreementSpi
/*     */ {
/*     */   private BigInteger result;
/*     */   private CipherParameters privKey;
/*     */   private BasicAgreement agreement;
/*     */   
/*     */   protected JCEECDHKeyAgreement(BasicAgreement agreement)
/*     */   {
/*  40 */     this.agreement = agreement;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Key doPhase(Key key, boolean lastPhase)
/*     */     throws InvalidKeyException, IllegalStateException
/*     */   {
/*  50 */     return engineDoPhase(key, lastPhase);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected Key engineDoPhase(Key key, boolean lastPhase)
/*     */     throws InvalidKeyException, IllegalStateException
/*     */   {
/*  58 */     if (this.privKey == null)
/*     */     {
/*  60 */       throw new IllegalStateException("EC Diffie-Hellman not initialised.");
/*     */     }
/*     */     
/*  63 */     if (!lastPhase)
/*     */     {
/*  65 */       throw new IllegalStateException("EC Diffie-Hellman can only be between two parties.");
/*     */     }
/*     */     
/*  68 */     if (!(key instanceof ECPublicKey))
/*     */     {
/*  70 */       throw new InvalidKeyException("EC Key Agreement doPhase requires ECPublicKey");
/*     */     }
/*     */     
/*  73 */     CipherParameters pubKey = ECUtil.generatePublicKeyParameter((PublicKey)key);
/*     */     
/*  75 */     this.result = this.agreement.calculateAgreement(pubKey);
/*     */     
/*  77 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] generateSecret()
/*     */     throws IllegalStateException
/*     */   {
/*  84 */     return engineGenerateSecret();
/*     */   }
/*     */   
/*     */   protected byte[] engineGenerateSecret()
/*     */     throws IllegalStateException
/*     */   {
/*  90 */     return this.result.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int engineGenerateSecret(byte[] sharedSecret, int offset)
/*     */     throws IllegalStateException, ShortBufferException
/*     */   {
/*  98 */     byte[] secret = this.result.toByteArray();
/*     */     
/* 100 */     if (sharedSecret.length - offset < secret.length)
/*     */     {
/* 102 */       throw new ShortBufferException("ECKeyAgreement - buffer too short");
/*     */     }
/*     */     
/* 105 */     System.arraycopy(secret, 0, sharedSecret, offset, secret.length);
/*     */     
/* 107 */     return secret.length;
/*     */   }
/*     */   
/*     */ 
/*     */   protected SecretKey engineGenerateSecret(String algorithm)
/*     */   {
/* 113 */     return new SecretKeySpec(this.result.toByteArray(), algorithm);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(Key key)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 122 */     engineInit(key, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void engineInit(Key key, AlgorithmParameterSpec params, SecureRandom random)
/*     */     throws InvalidKeyException, InvalidAlgorithmParameterException
/*     */   {
/* 131 */     if (!(key instanceof ECPrivateKey))
/*     */     {
/* 133 */       throw new InvalidKeyException("ECKeyAgreement requires ECPrivateKey for initialisation");
/*     */     }
/*     */     
/* 136 */     this.privKey = ECUtil.generatePrivateKeyParameter((PrivateKey)key);
/*     */     
/* 138 */     this.agreement.init(this.privKey);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void engineInit(Key key, SecureRandom random)
/*     */     throws InvalidKeyException
/*     */   {
/* 146 */     if (!(key instanceof ECPrivateKey))
/*     */     {
/* 148 */       throw new InvalidKeyException("ECKeyAgreement requires ECPrivateKey");
/*     */     }
/*     */     
/* 151 */     this.privKey = ECUtil.generatePrivateKeyParameter((PrivateKey)key);
/*     */     
/* 153 */     this.agreement.init(this.privKey);
/*     */   }
/*     */   
/*     */   public static class DH
/*     */     extends JCEECDHKeyAgreement
/*     */   {
/*     */     public DH()
/*     */     {
/* 161 */       super();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JCEECDHKeyAgreement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */