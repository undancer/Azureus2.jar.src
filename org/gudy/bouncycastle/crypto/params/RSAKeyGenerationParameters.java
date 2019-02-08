/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.KeyGenerationParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RSAKeyGenerationParameters
/*    */   extends KeyGenerationParameters
/*    */ {
/*    */   private BigInteger publicExponent;
/*    */   private int certainty;
/*    */   
/*    */   public RSAKeyGenerationParameters(BigInteger publicExponent, SecureRandom random, int strength, int certainty)
/*    */   {
/* 20 */     super(random, strength);
/*    */     
/* 22 */     this.publicExponent = publicExponent;
/* 23 */     this.certainty = certainty;
/*    */   }
/*    */   
/*    */   public BigInteger getPublicExponent()
/*    */   {
/* 28 */     return this.publicExponent;
/*    */   }
/*    */   
/*    */   public int getCertainty()
/*    */   {
/* 33 */     return this.certainty;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/RSAKeyGenerationParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */