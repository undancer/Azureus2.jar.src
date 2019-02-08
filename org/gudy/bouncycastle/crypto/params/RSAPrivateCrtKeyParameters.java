/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RSAPrivateCrtKeyParameters
/*    */   extends RSAKeyParameters
/*    */ {
/*    */   private BigInteger e;
/*    */   private BigInteger p;
/*    */   private BigInteger q;
/*    */   private BigInteger dP;
/*    */   private BigInteger dQ;
/*    */   private BigInteger qInv;
/*    */   
/*    */   public RSAPrivateCrtKeyParameters(BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent, BigInteger p, BigInteger q, BigInteger dP, BigInteger dQ, BigInteger qInv)
/*    */   {
/* 30 */     super(true, modulus, privateExponent);
/*    */     
/* 32 */     this.e = publicExponent;
/* 33 */     this.p = p;
/* 34 */     this.q = q;
/* 35 */     this.dP = dP;
/* 36 */     this.dQ = dQ;
/* 37 */     this.qInv = qInv;
/*    */   }
/*    */   
/*    */   public BigInteger getPublicExponent()
/*    */   {
/* 42 */     return this.e;
/*    */   }
/*    */   
/*    */   public BigInteger getP()
/*    */   {
/* 47 */     return this.p;
/*    */   }
/*    */   
/*    */   public BigInteger getQ()
/*    */   {
/* 52 */     return this.q;
/*    */   }
/*    */   
/*    */   public BigInteger getDP()
/*    */   {
/* 57 */     return this.dP;
/*    */   }
/*    */   
/*    */   public BigInteger getDQ()
/*    */   {
/* 62 */     return this.dQ;
/*    */   }
/*    */   
/*    */   public BigInteger getQInv()
/*    */   {
/* 67 */     return this.qInv;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/RSAPrivateCrtKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */