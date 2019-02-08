/*    */ package org.gudy.bouncycastle.jce.spec;
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
/*    */ public class ElGamalPublicKeySpec
/*    */   extends ElGamalKeySpec
/*    */ {
/*    */   private BigInteger y;
/*    */   
/*    */   public ElGamalPublicKeySpec(BigInteger y, ElGamalParameterSpec spec)
/*    */   {
/* 23 */     super(spec);
/*    */     
/* 25 */     this.y = y;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger getY()
/*    */   {
/* 35 */     return this.y;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/ElGamalPublicKeySpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */