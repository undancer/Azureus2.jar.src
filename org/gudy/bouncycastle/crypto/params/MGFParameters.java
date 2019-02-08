/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import org.gudy.bouncycastle.crypto.DerivationParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MGFParameters
/*    */   implements DerivationParameters
/*    */ {
/*    */   byte[] seed;
/*    */   
/*    */   public MGFParameters(byte[] seed)
/*    */   {
/* 16 */     this.seed = seed;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public MGFParameters(byte[] seed, int off, int len)
/*    */   {
/* 24 */     this.seed = new byte[len];
/* 25 */     System.arraycopy(seed, off, this.seed, 0, len);
/*    */   }
/*    */   
/*    */   public byte[] getSeed()
/*    */   {
/* 30 */     return this.seed;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/MGFParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */