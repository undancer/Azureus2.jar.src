/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECKeyParameters
/*    */   extends AsymmetricKeyParameter
/*    */ {
/*    */   ECDomainParameters params;
/*    */   
/*    */ 
/*    */ 
/*    */   protected ECKeyParameters(boolean isPrivate, ECDomainParameters params)
/*    */   {
/* 15 */     super(isPrivate);
/*    */     
/* 17 */     this.params = params;
/*    */   }
/*    */   
/*    */   public ECDomainParameters getParameters()
/*    */   {
/* 22 */     return this.params;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ECKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */