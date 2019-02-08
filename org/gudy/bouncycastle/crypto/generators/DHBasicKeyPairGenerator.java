/*    */ package org.gudy.bouncycastle.crypto.generators;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPair;
/*    */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
/*    */ import org.gudy.bouncycastle.crypto.KeyGenerationParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DHKeyGenerationParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DHParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DHPrivateKeyParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.DHPublicKeyParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DHBasicKeyPairGenerator
/*    */   implements AsymmetricCipherKeyPairGenerator
/*    */ {
/*    */   private DHKeyGenerationParameters param;
/*    */   
/*    */   public void init(KeyGenerationParameters param)
/*    */   {
/* 27 */     this.param = ((DHKeyGenerationParameters)param);
/*    */   }
/*    */   
/*    */ 
/*    */   public AsymmetricCipherKeyPair generateKeyPair()
/*    */   {
/* 33 */     int qLength = this.param.getStrength() - 1;
/* 34 */     DHParameters dhParams = this.param.getParameters();
/*    */     
/* 36 */     BigInteger p = dhParams.getP();
/* 37 */     BigInteger g = dhParams.getG();
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
/* 49 */     return new AsymmetricCipherKeyPair(new DHPublicKeyParameters(y, dhParams), new DHPrivateKeyParameters(x, dhParams));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/DHBasicKeyPairGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */