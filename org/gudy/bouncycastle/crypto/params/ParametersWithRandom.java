/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ParametersWithRandom
/*    */   implements CipherParameters
/*    */ {
/*    */   private SecureRandom random;
/*    */   private CipherParameters parameters;
/*    */   
/*    */   public ParametersWithRandom(CipherParameters parameters, SecureRandom random)
/*    */   {
/* 17 */     this.random = random;
/* 18 */     this.parameters = parameters;
/*    */   }
/*    */   
/*    */ 
/*    */   public ParametersWithRandom(CipherParameters parameters)
/*    */   {
/* 24 */     this.random = null;
/* 25 */     this.parameters = parameters;
/*    */   }
/*    */   
/*    */   public SecureRandom getRandom()
/*    */   {
/* 30 */     if (this.random == null)
/*    */     {
/* 32 */       this.random = new SecureRandom();
/*    */     }
/* 34 */     return this.random;
/*    */   }
/*    */   
/*    */   public CipherParameters getParameters()
/*    */   {
/* 39 */     return this.parameters;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ParametersWithRandom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */