/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RC5Parameters
/*    */   implements CipherParameters
/*    */ {
/*    */   private byte[] key;
/*    */   private int rounds;
/*    */   
/*    */   public RC5Parameters(byte[] key, int rounds)
/*    */   {
/* 15 */     if (key.length > 255)
/*    */     {
/* 17 */       throw new IllegalArgumentException("RC5 key length can be no greater than 255");
/*    */     }
/*    */     
/* 20 */     this.key = new byte[key.length];
/* 21 */     this.rounds = rounds;
/*    */     
/* 23 */     System.arraycopy(key, 0, this.key, 0, key.length);
/*    */   }
/*    */   
/*    */   public byte[] getKey()
/*    */   {
/* 28 */     return this.key;
/*    */   }
/*    */   
/*    */   public int getRounds()
/*    */   {
/* 33 */     return this.rounds;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/RC5Parameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */