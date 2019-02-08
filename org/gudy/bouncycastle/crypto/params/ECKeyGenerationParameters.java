/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.KeyGenerationParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECKeyGenerationParameters
/*    */   extends KeyGenerationParameters
/*    */ {
/*    */   private ECDomainParameters domainParams;
/*    */   
/*    */   public ECKeyGenerationParameters(ECDomainParameters domainParams, SecureRandom random)
/*    */   {
/* 17 */     super(random, domainParams.getN().bitLength());
/*    */     
/* 19 */     this.domainParams = domainParams;
/*    */   }
/*    */   
/*    */   public ECDomainParameters getDomainParameters()
/*    */   {
/* 24 */     return this.domainParams;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ECKeyGenerationParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */