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
/*    */ public class ElGamalPrivateKeySpec
/*    */   extends ElGamalKeySpec
/*    */ {
/*    */   private BigInteger x;
/*    */   
/*    */   public ElGamalPrivateKeySpec(BigInteger x, ElGamalParameterSpec spec)
/*    */   {
/* 23 */     super(spec);
/*    */     
/* 25 */     this.x = x;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger getX()
/*    */   {
/* 35 */     return this.x;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/ElGamalPrivateKeySpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */