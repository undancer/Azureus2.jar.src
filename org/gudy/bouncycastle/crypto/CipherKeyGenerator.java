/*    */ package org.gudy.bouncycastle.crypto;
/*    */ 
/*    */ import java.security.SecureRandom;
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
/*    */ public class CipherKeyGenerator
/*    */ {
/*    */   protected SecureRandom random;
/*    */   protected int strength;
/*    */   
/*    */   public void init(KeyGenerationParameters param)
/*    */   {
/* 23 */     this.random = param.getRandom();
/* 24 */     this.strength = ((param.getStrength() + 7) / 8);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] generateKey()
/*    */   {
/* 34 */     byte[] key = new byte[this.strength];
/*    */     
/* 36 */     this.random.nextBytes(key);
/*    */     
/* 38 */     return key;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/CipherKeyGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */