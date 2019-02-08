/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IESParameters
/*    */   implements CipherParameters
/*    */ {
/*    */   private byte[] derivation;
/*    */   private byte[] encoding;
/*    */   private int macKeySize;
/*    */   
/*    */   public IESParameters(byte[] derivation, byte[] encoding, int macKeySize)
/*    */   {
/* 25 */     this.derivation = derivation;
/* 26 */     this.encoding = encoding;
/* 27 */     this.macKeySize = macKeySize;
/*    */   }
/*    */   
/*    */   public byte[] getDerivationV()
/*    */   {
/* 32 */     return this.derivation;
/*    */   }
/*    */   
/*    */   public byte[] getEncodingV()
/*    */   {
/* 37 */     return this.encoding;
/*    */   }
/*    */   
/*    */   public int getMacKeySize()
/*    */   {
/* 42 */     return this.macKeySize;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/IESParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */