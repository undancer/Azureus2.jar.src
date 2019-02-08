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
/*    */ 
/*    */ public class KeyGenerationParameters
/*    */ {
/*    */   private SecureRandom random;
/*    */   private int strength;
/*    */   
/*    */   public KeyGenerationParameters(SecureRandom random, int strength)
/*    */   {
/* 24 */     this.random = random;
/* 25 */     this.strength = strength;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SecureRandom getRandom()
/*    */   {
/* 36 */     return this.random;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getStrength()
/*    */   {
/* 46 */     return this.strength;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/KeyGenerationParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */