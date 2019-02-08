/*    */ package org.gudy.bouncycastle.jce.spec;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.spec.AlgorithmParameterSpec;
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
/*    */ public class ElGamalParameterSpec
/*    */   implements AlgorithmParameterSpec
/*    */ {
/*    */   private BigInteger p;
/*    */   private BigInteger g;
/*    */   
/*    */   public ElGamalParameterSpec(BigInteger p, BigInteger g)
/*    */   {
/* 23 */     this.p = p;
/* 24 */     this.g = g;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger getP()
/*    */   {
/* 34 */     return this.p;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger getG()
/*    */   {
/* 44 */     return this.g;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/ElGamalParameterSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */