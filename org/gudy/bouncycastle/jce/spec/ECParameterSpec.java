/*    */ package org.gudy.bouncycastle.jce.spec;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.spec.AlgorithmParameterSpec;
/*    */ import org.gudy.bouncycastle.math.ec.ECCurve;
/*    */ import org.gudy.bouncycastle.math.ec.ECPoint;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECParameterSpec
/*    */   implements AlgorithmParameterSpec
/*    */ {
/*    */   private ECCurve curve;
/*    */   private byte[] seed;
/*    */   private ECPoint G;
/*    */   private BigInteger n;
/*    */   private BigInteger h;
/*    */   
/*    */   public ECParameterSpec(ECCurve curve, ECPoint G, BigInteger n)
/*    */   {
/* 26 */     this.curve = curve;
/* 27 */     this.G = G;
/* 28 */     this.n = n;
/* 29 */     this.h = BigInteger.valueOf(1L);
/* 30 */     this.seed = null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ECParameterSpec(ECCurve curve, ECPoint G, BigInteger n, BigInteger h)
/*    */   {
/* 39 */     this.curve = curve;
/* 40 */     this.G = G;
/* 41 */     this.n = n;
/* 42 */     this.h = h;
/* 43 */     this.seed = null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ECParameterSpec(ECCurve curve, ECPoint G, BigInteger n, BigInteger h, byte[] seed)
/*    */   {
/* 53 */     this.curve = curve;
/* 54 */     this.G = G;
/* 55 */     this.n = n;
/* 56 */     this.h = h;
/* 57 */     this.seed = seed;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ECCurve getCurve()
/*    */   {
/* 65 */     return this.curve;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ECPoint getG()
/*    */   {
/* 73 */     return this.G;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger getN()
/*    */   {
/* 81 */     return this.n;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger getH()
/*    */   {
/* 89 */     return this.h;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] getSeed()
/*    */   {
/* 97 */     return this.seed;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/ECParameterSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */