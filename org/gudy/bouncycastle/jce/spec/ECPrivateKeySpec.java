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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECPrivateKeySpec
/*    */   extends ECKeySpec
/*    */ {
/*    */   private BigInteger d;
/*    */   
/*    */   public ECPrivateKeySpec(BigInteger d, ECParameterSpec spec)
/*    */   {
/* 26 */     super(spec);
/*    */     
/* 28 */     this.d = d;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger getD()
/*    */   {
/* 36 */     return this.d;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/ECPrivateKeySpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */