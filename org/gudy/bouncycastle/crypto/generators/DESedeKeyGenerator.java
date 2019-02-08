/*    */ package org.gudy.bouncycastle.crypto.generators;
/*    */ 
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.KeyGenerationParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DESedeParameters;
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
/*    */ public class DESedeKeyGenerator
/*    */   extends DESKeyGenerator
/*    */ {
/*    */   public void init(KeyGenerationParameters param)
/*    */   {
/* 21 */     super.init(param);
/*    */     
/* 23 */     if ((this.strength == 0) || (this.strength == 21))
/*    */     {
/* 25 */       this.strength = 24;
/*    */     }
/* 27 */     else if (this.strength == 14)
/*    */     {
/* 29 */       this.strength = 16;
/*    */     }
/* 31 */     else if ((this.strength != 24) && (this.strength != 16))
/*    */     {
/*    */ 
/* 34 */       throw new IllegalArgumentException("DESede key must be 192 or 128 bits long.");
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] generateKey()
/*    */   {
/* 43 */     byte[] newKey = new byte[this.strength];
/*    */     
/*    */     do
/*    */     {
/* 47 */       this.random.nextBytes(newKey);
/*    */       
/* 49 */       DESedeParameters.setOddParity(newKey);
/*    */     }
/* 51 */     while (DESedeParameters.isWeakKey(newKey, 0, newKey.length));
/*    */     
/* 53 */     return newKey;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/DESedeKeyGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */