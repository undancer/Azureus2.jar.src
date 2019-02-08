/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*    */ 
/*    */ 
/*    */ public class AsymmetricKeyParameter
/*    */   implements CipherParameters
/*    */ {
/*    */   boolean privateKey;
/*    */   
/*    */   public AsymmetricKeyParameter(boolean privateKey)
/*    */   {
/* 13 */     this.privateKey = privateKey;
/*    */   }
/*    */   
/*    */   public boolean isPrivate()
/*    */   {
/* 18 */     return this.privateKey;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/AsymmetricKeyParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */