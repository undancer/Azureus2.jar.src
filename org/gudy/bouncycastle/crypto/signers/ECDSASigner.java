/*     */ package org.gudy.bouncycastle.crypto.signers;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.SecureRandom;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.DSA;
/*     */ import org.gudy.bouncycastle.crypto.params.ECDomainParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ECKeyParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ECPrivateKeyParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ECPublicKeyParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.ParametersWithRandom;
/*     */ import org.gudy.bouncycastle.math.ec.ECConstants;
/*     */ import org.gudy.bouncycastle.math.ec.ECFieldElement;
/*     */ import org.gudy.bouncycastle.math.ec.ECPoint;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ECDSASigner
/*     */   implements ECConstants, DSA
/*     */ {
/*     */   ECKeyParameters key;
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
/*  36 */         this.key = ((ECPrivateKeyParameters)rParam.getParameters());
/*     */       }
/*     */       else
/*     */       {
/*  40 */         this.random = new SecureRandom();
/*  41 */         this.key = ((ECPrivateKeyParameters)param);
/*     */       }
/*     */       
/*     */     }
/*     */     else {
/*  46 */       this.key = ((ECPublicKeyParameters)param);
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
/*     */ 
/*     */   public BigInteger[] generateSignature(byte[] message)
/*     */   {
/*  61 */     BigInteger e = new BigInteger(1, message);
/*  62 */     BigInteger n = this.key.getParameters().getN();
/*     */     
/*  64 */     BigInteger r = null;
/*  65 */     BigInteger s = null;
/*     */     
/*     */ 
/*     */     do
/*     */     {
/*  70 */       BigInteger k = null;
/*  71 */       int nBitLength = n.bitLength();
/*     */       
/*     */       do
/*     */       {
/*     */         do
/*     */         {
/*  77 */           k = new BigInteger(nBitLength, this.random);
/*     */         }
/*  79 */         while (k.equals(ZERO));
/*     */         
/*  81 */         ECPoint p = this.key.getParameters().getG().multiply(k);
/*     */         
/*     */ 
/*  84 */         BigInteger x = p.getX().toBigInteger();
/*     */         
/*  86 */         r = x.mod(n);
/*     */       }
/*  88 */       while (r.equals(ZERO));
/*     */       
/*  90 */       BigInteger d = ((ECPrivateKeyParameters)this.key).getD();
/*     */       
/*  92 */       s = k.modInverse(n).multiply(e.add(d.multiply(r))).mod(n);
/*     */     }
/*  94 */     while (s.equals(ZERO));
/*     */     
/*  96 */     BigInteger[] res = new BigInteger[2];
/*     */     
/*  98 */     res[0] = r;
/*  99 */     res[1] = s;
/*     */     
/* 101 */     return res;
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
/*     */   public boolean verifySignature(byte[] message, BigInteger r, BigInteger s)
/*     */   {
/* 115 */     BigInteger e = new BigInteger(1, message);
/* 116 */     BigInteger n = this.key.getParameters().getN();
/*     */     
/*     */ 
/* 119 */     if ((r.compareTo(ONE) < 0) || (r.compareTo(n) >= 0))
/*     */     {
/* 121 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 125 */     if ((s.compareTo(ONE) < 0) || (s.compareTo(n) >= 0))
/*     */     {
/* 127 */       return false;
/*     */     }
/*     */     
/* 130 */     BigInteger c = s.modInverse(n);
/*     */     
/* 132 */     BigInteger u1 = e.multiply(c).mod(n);
/* 133 */     BigInteger u2 = r.multiply(c).mod(n);
/*     */     
/* 135 */     ECPoint G = this.key.getParameters().getG();
/* 136 */     ECPoint Q = ((ECPublicKeyParameters)this.key).getQ();
/*     */     
/* 138 */     ECPoint point = G.multiply(u1).add(Q.multiply(u2));
/*     */     
/* 140 */     BigInteger v = point.getX().toBigInteger().mod(n);
/*     */     
/* 142 */     return v.equals(r);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/signers/ECDSASigner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */