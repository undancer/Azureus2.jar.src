/*    */ package org.gudy.bouncycastle.crypto.generators;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPair;
/*    */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
/*    */ import org.gudy.bouncycastle.crypto.KeyGenerationParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DSAKeyGenerationParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DSAParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DSAPrivateKeyParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DSAPublicKeyParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DSAKeyPairGenerator
/*    */   implements AsymmetricCipherKeyPairGenerator
/*    */ {
/* 23 */   private static BigInteger ZERO = BigInteger.valueOf(0L);
/*    */   
/*    */   private DSAKeyGenerationParameters param;
/*    */   
/*    */ 
/*    */   public void init(KeyGenerationParameters param)
/*    */   {
/* 30 */     this.param = ((DSAKeyGenerationParameters)param);
/*    */   }
/*    */   
/*    */ 
/*    */   public AsymmetricCipherKeyPair generateKeyPair()
/*    */   {
/* 36 */     DSAParameters dsaParams = this.param.getParameters();
/* 37 */     SecureRandom random = this.param.getRandom();
/*    */     
/* 39 */     BigInteger q = dsaParams.getQ();
/* 40 */     BigInteger p = dsaParams.getP();
/* 41 */     BigInteger g = dsaParams.getG();
/*    */     BigInteger x;
/*    */     do
/*    */     {
/* 45 */       x = new BigInteger(160, random);
/*    */     }
/* 47 */     while ((x.equals(ZERO)) || (x.compareTo(q) >= 0));
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 52 */     BigInteger y = g.modPow(x, p);
/*    */     
/* 54 */     return new AsymmetricCipherKeyPair(new DSAPublicKeyParameters(y, dsaParams), new DSAPrivateKeyParameters(x, dsaParams));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/DSAKeyPairGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */