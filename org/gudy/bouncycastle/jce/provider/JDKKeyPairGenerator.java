/*     */ package org.gudy.bouncycastle.jce.provider;
/*     */ 
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.KeyPair;
/*     */ import java.security.KeyPairGenerator;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import java.util.Hashtable;
/*     */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPair;
/*     */ import org.gudy.bouncycastle.crypto.generators.ECKeyPairGenerator;
/*     */ import org.gudy.bouncycastle.crypto.params.ECDomainParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ECKeyGenerationParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ECPrivateKeyParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ECPublicKeyParameters;
/*     */ import org.gudy.bouncycastle.jce.ECNamedCurveTable;
/*     */ import org.gudy.bouncycastle.jce.spec.ECParameterSpec;
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JDKKeyPairGenerator
/*     */   extends KeyPairGenerator
/*     */ {
/*     */   public JDKKeyPairGenerator(String algorithmName)
/*     */   {
/*  25 */     super(algorithmName);
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
/*     */   public abstract void initialize(int paramInt, SecureRandom paramSecureRandom);
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
/*     */   public abstract KeyPair generateKeyPair();
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
/*     */   public static class EC
/*     */     extends JDKKeyPairGenerator
/*     */   {
/*     */     ECKeyGenerationParameters param;
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
/* 278 */     ECKeyPairGenerator engine = new ECKeyPairGenerator();
/* 279 */     ECParameterSpec ecParams = null;
/* 280 */     int strength = 239;
/* 281 */     int certainty = 50;
/* 282 */     SecureRandom random = new SecureRandom();
/* 283 */     boolean initialised = false;
/*     */     
/*     */ 
/*     */     String algorithm;
/*     */     
/*     */ 
/* 289 */     private static Hashtable ecParameters = new Hashtable();
/*     */     
/* 291 */     static { ecParameters.put(new Integer(192), ECNamedCurveTable.getParameterSpec("prime192v1"));
/*     */       
/* 293 */       ecParameters.put(new Integer(239), ECNamedCurveTable.getParameterSpec("prime239v1"));
/*     */       
/* 295 */       ecParameters.put(new Integer(256), ECNamedCurveTable.getParameterSpec("prime256v1"));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public EC(String algorithm)
/*     */     {
/* 303 */       super();
/* 304 */       this.algorithm = algorithm;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void initialize(int strength, SecureRandom random)
/*     */     {
/* 311 */       this.strength = strength;
/* 312 */       this.random = random;
/* 313 */       this.ecParams = ((ECParameterSpec)ecParameters.get(new Integer(strength)));
/*     */       
/* 315 */       if (this.ecParams != null)
/*     */       {
/* 317 */         this.param = new ECKeyGenerationParameters(new ECDomainParameters(this.ecParams.getCurve(), this.ecParams.getG(), this.ecParams.getN()), random);
/*     */         
/* 319 */         this.engine.init(this.param);
/* 320 */         this.initialised = true;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void initialize(AlgorithmParameterSpec params, SecureRandom random)
/*     */       throws InvalidAlgorithmParameterException
/*     */     {
/* 329 */       if (!(params instanceof ECParameterSpec))
/*     */       {
/* 331 */         throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec");
/*     */       }
/*     */       
/* 334 */       this.ecParams = ((ECParameterSpec)params);
/*     */       
/* 336 */       this.param = new ECKeyGenerationParameters(new ECDomainParameters(this.ecParams.getCurve(), this.ecParams.getG(), this.ecParams.getN()), random);
/*     */       
/* 338 */       this.engine.init(this.param);
/* 339 */       this.initialised = true;
/*     */     }
/*     */     
/*     */     public KeyPair generateKeyPair()
/*     */     {
/* 344 */       if (!this.initialised)
/*     */       {
/* 346 */         throw new IllegalStateException("EC Key Pair Generator not initialised");
/*     */       }
/*     */       
/* 349 */       AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
/* 350 */       ECPublicKeyParameters pub = (ECPublicKeyParameters)pair.getPublic();
/* 351 */       ECPrivateKeyParameters priv = (ECPrivateKeyParameters)pair.getPrivate();
/*     */       
/* 353 */       return new KeyPair(new JCEECPublicKey(this.algorithm, pub, this.ecParams), new JCEECPrivateKey(this.algorithm, priv, this.ecParams));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static class ECDSA
/*     */     extends JDKKeyPairGenerator.EC
/*     */   {
/*     */     public ECDSA()
/*     */     {
/* 363 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ECDH
/*     */     extends JDKKeyPairGenerator.EC
/*     */   {
/*     */     public ECDH()
/*     */     {
/* 372 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ECDHC
/*     */     extends JDKKeyPairGenerator.EC
/*     */   {
/*     */     public ECDHC()
/*     */     {
/* 381 */       super();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/provider/JDKKeyPairGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */