/*    */ package org.gudy.bouncycastle.jce.spec;
/*    */ 
/*    */ import org.gudy.bouncycastle.math.ec.ECPoint;
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
/*    */ public class ECPublicKeySpec
/*    */   extends ECKeySpec
/*    */ {
/*    */   private ECPoint q;
/*    */   
/*    */   public ECPublicKeySpec(ECPoint q, ECParameterSpec spec)
/*    */   {
/* 25 */     super(spec);
/*    */     
/* 27 */     this.q = q;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ECPoint getQ()
/*    */   {
/* 35 */     return this.q;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/ECPublicKeySpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */