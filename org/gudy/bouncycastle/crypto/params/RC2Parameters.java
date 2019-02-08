/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*    */ 
/*    */ 
/*    */ public class RC2Parameters
/*    */   implements CipherParameters
/*    */ {
/*    */   private byte[] key;
/*    */   private int bits;
/*    */   
/*    */   public RC2Parameters(byte[] key)
/*    */   {
/* 14 */     this(key, key.length > 128 ? 1024 : key.length * 8);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public RC2Parameters(byte[] key, int bits)
/*    */   {
/* 21 */     this.key = new byte[key.length];
/* 22 */     this.bits = bits;
/*    */     
/* 24 */     System.arraycopy(key, 0, this.key, 0, key.length);
/*    */   }
/*    */   
/*    */   public byte[] getKey()
/*    */   {
/* 29 */     return this.key;
/*    */   }
/*    */   
/*    */   public int getEffectiveKeyBits()
/*    */   {
/* 34 */     return this.bits;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/RC2Parameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */