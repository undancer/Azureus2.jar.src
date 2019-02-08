/*     */ package org.gudy.bouncycastle.crypto.generators;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPair;
/*     */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
/*     */ import org.gudy.bouncycastle.crypto.KeyGenerationParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.RSAKeyGenerationParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.RSAKeyParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RSAKeyPairGenerator
/*     */   implements AsymmetricCipherKeyPairGenerator
/*     */ {
/*  18 */   private static BigInteger ONE = BigInteger.valueOf(1L);
/*     */   
/*     */   private RSAKeyGenerationParameters param;
/*     */   
/*     */ 
/*     */   public void init(KeyGenerationParameters param)
/*     */   {
/*  25 */     this.param = ((RSAKeyGenerationParameters)param);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AsymmetricCipherKeyPair generateKeyPair()
/*     */   {
/*  35 */     int pbitlength = (this.param.getStrength() + 1) / 2;
/*  36 */     int qbitlength = this.param.getStrength() - pbitlength;
/*  37 */     BigInteger e = this.param.getPublicExponent();
/*     */     
/*     */ 
/*     */     BigInteger p;
/*     */     
/*     */     for (;;)
/*     */     {
/*  44 */       p = new BigInteger(pbitlength, 1, this.param.getRandom());
/*     */       
/*  46 */       if ((!p.mod(e).equals(ONE)) && 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  51 */         (p.isProbablePrime(this.param.getCertainty())) && 
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*  56 */         (e.gcd(p.subtract(ONE)).equals(ONE))) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     BigInteger q;
/*     */     
/*     */ 
/*     */ 
/*     */     BigInteger n;
/*     */     
/*     */ 
/*     */     for (;;)
/*     */     {
/*  72 */       q = new BigInteger(qbitlength, 1, this.param.getRandom());
/*     */       
/*  74 */       if ((!q.equals(p)) && 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  79 */         (!q.mod(e).equals(ONE)) && 
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*  84 */         (q.isProbablePrime(this.param.getCertainty())) && 
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*  89 */         (e.gcd(q.subtract(ONE)).equals(ONE)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  98 */         n = p.multiply(q);
/*     */         
/* 100 */         if (n.bitLength() == this.param.getStrength()) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 109 */         p = p.max(q);
/*     */       }
/*     */     }
/* 112 */     if (p.compareTo(q) < 0)
/*     */     {
/* 114 */       BigInteger phi = p;
/* 115 */       p = q;
/* 116 */       q = phi;
/*     */     }
/*     */     
/* 119 */     BigInteger pSub1 = p.subtract(ONE);
/* 120 */     BigInteger qSub1 = q.subtract(ONE);
/* 121 */     BigInteger phi = pSub1.multiply(qSub1);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 126 */     BigInteger d = e.modInverse(phi);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 133 */     BigInteger dP = d.remainder(pSub1);
/* 134 */     BigInteger dQ = d.remainder(qSub1);
/* 135 */     BigInteger qInv = q.modInverse(p);
/*     */     
/* 137 */     return new AsymmetricCipherKeyPair(new RSAKeyParameters(false, n, e), new RSAPrivateCrtKeyParameters(n, e, d, p, q, dP, dQ, qInv));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/RSAKeyPairGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */