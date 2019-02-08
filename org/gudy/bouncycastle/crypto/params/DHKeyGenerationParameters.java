/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.KeyGenerationParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DHKeyGenerationParameters
/*    */   extends KeyGenerationParameters
/*    */ {
/*    */   private DHParameters params;
/*    */   
/*    */   public DHKeyGenerationParameters(SecureRandom random, DHParameters params)
/*    */   {
/* 17 */     super(random, params.getP().bitLength() - 1);
/*    */     
/* 19 */     this.params = params;
/*    */   }
/*    */   
/*    */   public DHParameters getParameters()
/*    */   {
/* 24 */     return this.params;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DHKeyGenerationParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */