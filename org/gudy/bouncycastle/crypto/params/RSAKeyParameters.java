/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RSAKeyParameters
/*    */   extends AsymmetricKeyParameter
/*    */ {
/*    */   private BigInteger modulus;
/*    */   private BigInteger exponent;
/*    */   
/*    */   public RSAKeyParameters(boolean isPrivate, BigInteger modulus, BigInteger exponent)
/*    */   {
/* 18 */     super(isPrivate);
/*    */     
/* 20 */     this.modulus = modulus;
/* 21 */     this.exponent = exponent;
/*    */   }
/*    */   
/*    */   public BigInteger getModulus()
/*    */   {
/* 26 */     return this.modulus;
/*    */   }
/*    */   
/*    */   public BigInteger getExponent()
/*    */   {
/* 31 */     return this.exponent;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/RSAKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */