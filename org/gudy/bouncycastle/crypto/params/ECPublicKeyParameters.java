/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import org.gudy.bouncycastle.math.ec.ECPoint;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECPublicKeyParameters
/*    */   extends ECKeyParameters
/*    */ {
/*    */   ECPoint Q;
/*    */   
/*    */   public ECPublicKeyParameters(ECPoint Q, ECDomainParameters params)
/*    */   {
/* 16 */     super(false, params);
/* 17 */     this.Q = Q;
/*    */   }
/*    */   
/*    */   public ECPoint getQ()
/*    */   {
/* 22 */     return this.Q;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ECPublicKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */