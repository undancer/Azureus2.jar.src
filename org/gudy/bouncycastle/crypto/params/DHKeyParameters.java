/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DHKeyParameters
/*    */   extends AsymmetricKeyParameter
/*    */ {
/*    */   private DHParameters params;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected DHKeyParameters(boolean isPrivate, DHParameters params)
/*    */   {
/* 17 */     super(isPrivate);
/*    */     
/* 19 */     this.params = params;
/*    */   }
/*    */   
/*    */   public DHParameters getParameters()
/*    */   {
/* 24 */     return this.params;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 30 */     if (!(obj instanceof DHKeyParameters))
/*    */     {
/* 32 */       return false;
/*    */     }
/*    */     
/* 35 */     DHKeyParameters dhKey = (DHKeyParameters)obj;
/*    */     
/* 37 */     return (this.params != null) && (!this.params.equals(dhKey.getParameters()));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DHKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */