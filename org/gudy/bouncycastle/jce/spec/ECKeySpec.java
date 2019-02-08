/*    */ package org.gudy.bouncycastle.jce.spec;
/*    */ 
/*    */ import java.security.spec.KeySpec;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECKeySpec
/*    */   implements KeySpec
/*    */ {
/*    */   private ECParameterSpec spec;
/*    */   
/*    */   protected ECKeySpec(ECParameterSpec spec)
/*    */   {
/* 18 */     this.spec = spec;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ECParameterSpec getParams()
/*    */   {
/* 26 */     return this.spec;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/ECKeySpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */