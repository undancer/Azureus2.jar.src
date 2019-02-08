/*    */ package org.gudy.bouncycastle.jce.spec;
/*    */ 
/*    */ import java.security.spec.KeySpec;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ElGamalKeySpec
/*    */   implements KeySpec
/*    */ {
/*    */   private ElGamalParameterSpec spec;
/*    */   
/*    */   public ElGamalKeySpec(ElGamalParameterSpec spec)
/*    */   {
/* 15 */     this.spec = spec;
/*    */   }
/*    */   
/*    */   public ElGamalParameterSpec getParams()
/*    */   {
/* 20 */     return this.spec;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/ElGamalKeySpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */