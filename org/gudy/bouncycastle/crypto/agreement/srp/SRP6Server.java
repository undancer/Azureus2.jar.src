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
/*    */ 
/*    */ public class SRP6Server
/*    */ {
/*    */   protected BigInteger N;
/*    */   protected BigInteger g;
/*    */   protected BigInteger v;
/*    */   protected SecureRandom random;
/*    */   protected Digest digest;
/*    */   protected BigInteger A;
/*    */   protected BigInteger b;
/*    */   protected BigInteger B;
/*    */   protected BigInteger u;
/*    */   protected BigInteger S;
/*    */   
/*    */   public void init(BigInteger N, BigInteger g, BigInteger v, Digest digest, SecureRandom random)
/*    */   {
/* 45 */     this.N = N;
/* 46 */     this.g = g;
/* 47 */     this.v = v;
/*    */     
/* 49 */     this.random = random;
/* 50 */     this.digest = digest;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger generateServerCredentials()
/*    */   {
/* 59 */     BigInteger k = SRP6Util.calculateK(this.digest, this.N, this.g);
/* 60 */     this.b = selectPrivateValue();
/* 61 */     this.B = k.multiply(this.v).mod(this.N).add(this.g.modPow(this.b, this.N)).mod(this.N);
/*    */     
/* 63 */     return this.B;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger calculateSecret(BigInteger clientA)
/*    */     throws CryptoException
/*    */   {
/* 74 */     this.A = SRP6Util.validatePublicValue(this.N, clientA);
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
/* 88 */     return this.v.modPow(this.u, this.N).multiply(this.A).mod(this.N).modPow(this.b, this.N);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/agreement/srp/SRP6Server.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */