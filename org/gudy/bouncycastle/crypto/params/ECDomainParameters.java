/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.math.ec.ECConstants;
/*    */ import org.gudy.bouncycastle.math.ec.ECCurve;
/*    */ import org.gudy.bouncycastle.math.ec.ECPoint;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECDomainParameters
/*    */   implements ECConstants
/*    */ {
/*    */   ECCurve curve;
/*    */   byte[] seed;
/*    */   ECPoint G;
/*    */   BigInteger n;
/*    */   BigInteger h;
/*    */   
/*    */   public ECDomainParameters(ECCurve curve, ECPoint G, BigInteger n)
/*    */   {
/* 23 */     this.curve = curve;
/* 24 */     this.G = G;
/* 25 */     this.n = n;
/* 26 */     this.h = ONE;
/* 27 */     this.seed = null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ECDomainParameters(ECCurve curve, ECPoint G, BigInteger n, BigInteger h)
/*    */   {
/* 36 */     this.curve = curve;
/* 37 */     this.G = G;
/* 38 */     this.n = n;
/* 39 */     this.h = h;
/* 40 */     this.seed = null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ECDomainParameters(ECCurve curve, ECPoint G, BigInteger n, BigInteger h, byte[] seed)
/*    */   {
/* 50 */     this.curve = curve;
/* 51 */     this.G = G;
/* 52 */     this.n = n;
/* 53 */     this.h = h;
/* 54 */     this.seed = seed;
/*    */   }
/*    */   
/*    */   public ECCurve getCurve()
/*    */   {
/* 59 */     return this.curve;
/*    */   }
/*    */   
/*    */   public ECPoint getG()
/*    */   {
/* 64 */     return this.G;
/*    */   }
/*    */   
/*    */   public BigInteger getN()
/*    */   {
/* 69 */     return this.n;
/*    */   }
/*    */   
/*    */   public BigInteger getH()
/*    */   {
/* 74 */     return this.h;
/*    */   }
/*    */   
/*    */   public byte[] getSeed()
/*    */   {
/* 79 */     return this.seed;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ECDomainParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */