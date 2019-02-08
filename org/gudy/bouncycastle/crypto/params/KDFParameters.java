/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import org.gudy.bouncycastle.crypto.DerivationParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class KDFParameters
/*    */   implements DerivationParameters
/*    */ {
/*    */   byte[] iv;
/*    */   byte[] shared;
/*    */   
/*    */   public KDFParameters(byte[] shared, byte[] iv)
/*    */   {
/* 18 */     this.shared = shared;
/* 19 */     this.iv = iv;
/*    */   }
/*    */   
/*    */   public byte[] getSharedSecret()
/*    */   {
/* 24 */     return this.shared;
/*    */   }
/*    */   
/*    */   public byte[] getIV()
/*    */   {
/* 29 */     return this.iv;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/KDFParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */