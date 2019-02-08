/*    */ package org.gudy.bouncycastle.crypto.agreement.srp;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.CryptoException;
/*    */ import org.gudy.bouncycastle.crypto.Digest;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SRP6Client
/*    */ {
/*    */   protected BigInteger N;
/*    */   protected BigInteger g;
/*    */   protected BigInteger a;
/*    */   protected BigInteger A;
/*    */   protected BigInteger B;
/*    */   protected BigInteger x;
/*    */   protected BigInteger u;
/*    */   protected BigInteger S;
/*    */   protected Digest digest;
/*    */   protected SecureRandom random;
/*    */   
/*    */   public void init(BigInteger N, BigInteger g, Digest digest, SecureRandom random)
/*    */   {
/* 44 */     this.N = N;
/* 45 */     this.g = g;
/* 46 */     this.digest = digest;
/* 47 */     this.random = random;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger generateClientCredentials(byte[] salt, byte[] identity, byte[] password)
/*    */   {
/* 59 */     this.x = SRP6Util.calculateX(this.digest, this.N, salt, identity, password);
/* 60 */     this.a = selectPrivateValue();
/* 61 */     this.A = this.g.modPow(this.a, this.N);
/*    */     
/* 63 */     return this.A;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger calculateSecret(BigInteger serverB)
/*    */     throws CryptoException
/*    */   {
/* 74 */     this.B = SRP6Util.validatePublicValue(this.N, serverB);
/* 75 */     this.u = SRP6Util.calculateU(this.digest, this.N, this.A, this.B);
/* 76 */     this.S = calculateS();
/*    */     
/* 78 */     return this.S;
/*    */   }
/*    */   
/*    */   protected BigInteger selectPrivateValue()
/*    */   {
/* 83 */     return SRP6Util.generatePrivateValue(this.digest, this.N, this.g, this.random);
/*    */   }
/*    */   
/*    */   private BigInteger calculateS()
/*    */   {
/* 88 */     BigInteger k = SRP6Util.calculateK(this.digest, this.N, this.g);
/* 89 */     BigInteger exp = this.u.multiply(this.x).add(this.a);
/* 90 */     BigInteger tmp = this.g.modPow(this.x, this.N).multiply(k).mod(this.N);
/* 91 */     return this.B.subtract(tmp).mod(this.N).modPow(exp, this.N);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/agreement/srp/SRP6Client.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */