/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DSAKeyParameters
/*    */   extends AsymmetricKeyParameter
/*    */ {
/*    */   private DSAParameters params;
/*    */   
/*    */ 
/*    */ 
/*    */   public DSAKeyParameters(boolean isPrivate, DSAParameters params)
/*    */   {
/* 15 */     super(isPrivate);
/*    */     
/* 17 */     this.params = params;
/*    */   }
/*    */   
/*    */   public DSAParameters getParameters()
/*    */   {
/* 22 */     return this.params;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DSAKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */