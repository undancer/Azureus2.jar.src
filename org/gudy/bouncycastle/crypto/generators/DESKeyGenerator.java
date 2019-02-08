/*    */ package org.gudy.bouncycastle.crypto.generators;
/*    */ 
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.CipherKeyGenerator;
/*    */ import org.gudy.bouncycastle.crypto.params.DESParameters;
/*    */ 
/*    */ public class DESKeyGenerator extends CipherKeyGenerator
/*    */ {
/*    */   public byte[] generateKey()
/*    */   {
/* 11 */     byte[] newKey = new byte[8];
/*    */     
/*    */     do
/*    */     {
/* 15 */       this.random.nextBytes(newKey);
/*    */       
/* 17 */       DESParameters.setOddParity(newKey);
/*    */     }
/* 19 */     while (DESParameters.isWeakKey(newKey, 0));
/*    */     
/* 21 */     return newKey;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/DESKeyGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */