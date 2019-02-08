/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*    */ 
/*    */ 
/*    */ public class KeyParameter
/*    */   implements CipherParameters
/*    */ {
/*    */   private byte[] key;
/*    */   
/*    */   public KeyParameter(byte[] key)
/*    */   {
/* 13 */     this(key, 0, key.length);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public KeyParameter(byte[] key, int keyOff, int keyLen)
/*    */   {
/* 21 */     this.key = new byte[keyLen];
/*    */     
/* 23 */     System.arraycopy(key, keyOff, this.key, 0, keyLen);
/*    */   }
/*    */   
/*    */   public byte[] getKey()
/*    */   {
/* 28 */     return this.key;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/KeyParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */