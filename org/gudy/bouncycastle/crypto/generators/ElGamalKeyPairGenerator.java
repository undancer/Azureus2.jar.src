/*    */ package org.gudy.bouncycastle.crypto.generators;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPair;
/*    */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
/*    */ import org.gudy.bouncycastle.crypto.KeyGenerationParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ElGamalParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ElGamalKeyPairGenerator
/*    */   implements AsymmetricCipherKeyPairGenerator
/*    */ {
/*    */   private ElGamalKeyGenerationParameters param;
/*    */   
/*    */   public void init(KeyGenerationParameters param)
/*    */   {
/* 27 */     this.param = ((ElGamalKeyGenerationParameters)param);
/*    */   }
/*    */   
/*    */ 
/*    */   public AsymmetricCipherKeyPair generateKeyPair()
/*    */   {
/* 33 */     int qLength = this.param.getStrength() - 1;
/* 34 */     ElGamalParameters elParams = this.param.getParameters();
/*    */     
/* 36 */     BigInteger p = elParams.getP();
/* 37 */     BigInteger g = elParams.getG();
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 42 */     BigInteger x = new BigInteger(qLength, this.param.getRandom());
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 47 */     BigInteger y = g.modPow(x, p);
/*    */     
/* 49 */     return new AsymmetricCipherKeyPair(new ElGamalPublicKeyParameters(y, elParams), new ElGamalPrivateKeyParameters(x, elParams));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/ElGamalKeyPairGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */