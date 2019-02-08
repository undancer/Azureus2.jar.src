/*    */ package org.gudy.bouncycastle.crypto.generators;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPair;
/*    */ import org.gudy.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
/*    */ import org.gudy.bouncycastle.crypto.KeyGenerationParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ECDomainParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ECKeyGenerationParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ECPrivateKeyParameters;
/*    */ import org.gudy.bouncycastle.crypto.params.ECPublicKeyParameters;
/*    */ import org.gudy.bouncycastle.math.ec.ECConstants;
/*    */ import org.gudy.bouncycastle.math.ec.ECPoint;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECKeyPairGenerator
/*    */   implements AsymmetricCipherKeyPairGenerator, ECConstants
/*    */ {
/*    */   ECDomainParameters params;
/*    */   SecureRandom random;
/*    */   
/*    */   public void init(KeyGenerationParameters param)
/*    */   {
/* 25 */     ECKeyGenerationParameters ecP = (ECKeyGenerationParameters)param;
/*    */     
/* 27 */     this.random = ecP.getRandom();
/* 28 */     this.params = ecP.getDomainParameters();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public AsymmetricCipherKeyPair generateKeyPair()
/*    */   {
/* 37 */     BigInteger n = this.params.getN();
/* 38 */     int nBitLength = n.bitLength();
/*    */     
/*    */     BigInteger d;
/*    */     do
/*    */     {
/* 43 */       d = new BigInteger(nBitLength, this.random);
/*    */     }
/* 45 */     while ((d.equals(ZERO)) || (d.compareTo(n) >= 0));
/*    */     
/* 47 */     ECPoint Q = this.params.getG().multiply(d);
/*    */     
/* 49 */     return new AsymmetricCipherKeyPair(new ECPublicKeyParameters(Q, this.params), new ECPrivateKeyParameters(d, this.params));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/ECKeyPairGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */