/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ElGamalKeyParameters
/*    */   extends AsymmetricKeyParameter
/*    */ {
/*    */   private ElGamalParameters params;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected ElGamalKeyParameters(boolean isPrivate, ElGamalParameters params)
/*    */   {
/* 17 */     super(isPrivate);
/*    */     
/* 19 */     this.params = params;
/*    */   }
/*    */   
/*    */   public ElGamalParameters getParameters()
/*    */   {
/* 24 */     return this.params;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 30 */     if (!(obj instanceof ElGamalKeyParameters))
/*    */     {
/* 32 */       return false;
/*    */     }
/*    */     
/* 35 */     ElGamalKeyParameters dhKey = (ElGamalKeyParameters)obj;
/*    */     
/* 37 */     return (this.params != null) && (!this.params.equals(dhKey.getParameters()));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ElGamalKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */