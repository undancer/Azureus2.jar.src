/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import org.gudy.bouncycastle.crypto.DerivationParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ISO18033KDFParameters
/*    */   implements DerivationParameters
/*    */ {
/*    */   byte[] seed;
/*    */   
/*    */   public ISO18033KDFParameters(byte[] seed)
/*    */   {
/* 16 */     this.seed = seed;
/*    */   }
/*    */   
/*    */   public byte[] getSeed()
/*    */   {
/* 21 */     return this.seed;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ISO18033KDFParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */