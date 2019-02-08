/*    */ package org.gudy.bouncycastle.jce.spec;
/*    */ 
/*    */ import java.security.spec.AlgorithmParameterSpec;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ElGamalGenParameterSpec
/*    */   implements AlgorithmParameterSpec
/*    */ {
/*    */   private int primeSize;
/*    */   
/*    */   public ElGamalGenParameterSpec(int primeSize)
/*    */   {
/* 16 */     this.primeSize = primeSize;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getPrimeSize()
/*    */   {
/* 26 */     return this.primeSize;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/ElGamalGenParameterSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */