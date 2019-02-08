/*     */ package org.gudy.bouncycastle.crypto.signers;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.SecureRandom;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.DSA;
/*     */ import org.gudy.bouncycastle.crypto.params.DSAKeyParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.DSAParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.DSAPrivateKeyParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.DSAPublicKeyParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ParametersWithRandom;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DSASigner
/*     */   implements DSA
/*     */ {
/*     */   DSAKeyParameters key;
/*     */   SecureRandom random;
/*     */   
/*     */   public void init(boolean forSigning, CipherParameters param)
/*     */   {
/*  29 */     if (forSigning)
/*     */     {
/*  31 */       if ((param instanceof ParametersWithRandom))
/*     */       {
/*  33 */         ParametersWithRandom rParam = (ParametersWithRandom)param;
/*     */         
/*  35 */         this.random = rParam.getRandom();
/*  36 */         this.key = ((DSAPrivateKeyParameters)rParam.getParameters());
/*     */       }
/*     */       else
/*     */       {
/*  40 */         this.random = new SecureRandom();
/*  41 */         this.key = ((DSAPrivateKeyParameters)param);
/*     */       }
/*     */       
/*     */     }
/*     */     else {
/*  46 */       this.key = ((DSAPublicKeyParameters)param);
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
/*     */ 
/*     */   public BigInteger[] generateSignature(byte[] message)
/*     */   {
/*  60 */     DSAParameters params = this.key.getParameters();
/*  61 */     BigInteger m = calculateE(params.getQ(), message);
/*     */     
/*  63 */     int qBitLength = params.getQ().bitLength();
/*     */     
/*     */     do
/*     */     {
/*  67 */       k = new BigInteger(qBitLength, this.random);
/*     */     }
/*  69 */     while (k.compareTo(params.getQ()) >= 0);
/*     */     
/*  71 */     BigInteger r = params.getG().modPow(k, params.getP()).mod(params.getQ());
/*     */     
/*  73 */     BigInteger k = k.modInverse(params.getQ()).multiply(m.add(((DSAPrivateKeyParameters)this.key).getX().multiply(r)));
/*     */     
/*     */ 
/*  76 */     BigInteger s = k.mod(params.getQ());
/*     */     
/*  78 */     BigInteger[] res = new BigInteger[2];
/*     */     
/*  80 */     res[0] = r;
/*  81 */     res[1] = s;
/*     */     
/*  83 */     return res;
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
/*     */   public boolean verifySignature(byte[] message, BigInteger r, BigInteger s)
/*     */   {
/*  96 */     DSAParameters params = this.key.getParameters();
/*  97 */     BigInteger m = calculateE(params.getQ(), message);
/*  98 */     BigInteger zero = BigInteger.valueOf(0L);
/*     */     
/* 100 */     if ((zero.compareTo(r) >= 0) || (params.getQ().compareTo(r) <= 0))
/*     */     {
/* 102 */       return false;
/*     */     }
/*     */     
/* 105 */     if ((zero.compareTo(s) >= 0) || (params.getQ().compareTo(s) <= 0))
/*     */     {
/* 107 */       return false;
/*     */     }
/*     */     
/* 110 */     BigInteger w = s.modInverse(params.getQ());
/*     */     
/* 112 */     BigInteger u1 = m.multiply(w).mod(params.getQ());
/* 113 */     BigInteger u2 = r.multiply(w).mod(params.getQ());
/*     */     
/* 115 */     u1 = params.getG().modPow(u1, params.getP());
/* 116 */     u2 = ((DSAPublicKeyParameters)this.key).getY().modPow(u2, params.getP());
/*     */     
/* 118 */     BigInteger v = u1.multiply(u2).mod(params.getP()).mod(params.getQ());
/*     */     
/* 120 */     return v.equals(r);
/*     */   }
/*     */   
/*     */   private BigInteger calculateE(BigInteger n, byte[] message)
/*     */   {
/* 125 */     if (n.bitLength() >= message.length * 8)
/*     */     {
/* 127 */       return new BigInteger(1, message);
/*     */     }
/*     */     
/*     */ 
/* 131 */     byte[] trunc = new byte[n.bitLength() / 8];
/*     */     
/* 133 */     System.arraycopy(message, 0, trunc, 0, trunc.length);
/*     */     
/* 135 */     return new BigInteger(1, trunc);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/signers/DSASigner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */