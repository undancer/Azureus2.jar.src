/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IESWithCipherParameters
/*    */   extends IESParameters
/*    */ {
/*    */   private int cipherKeySize;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public IESWithCipherParameters(byte[] derivation, byte[] encoding, int macKeySize, int cipherKeySize)
/*    */   {
/* 23 */     super(derivation, encoding, macKeySize);
/*    */     
/* 25 */     this.cipherKeySize = cipherKeySize;
/*    */   }
/*    */   
/*    */   public int getCipherKeySize()
/*    */   {
/* 30 */     return this.cipherKeySize;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/IESWithCipherParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */