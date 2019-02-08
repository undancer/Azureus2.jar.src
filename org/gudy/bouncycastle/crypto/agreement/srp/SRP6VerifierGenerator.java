/*    */ package org.gudy.bouncycastle.crypto.agreement.srp;
/*    */ 
/*    */ import java.math.BigInteger;
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
/*    */ public class SRP6VerifierGenerator
/*    */ {
/*    */   protected BigInteger N;
/*    */   protected BigInteger g;
/*    */   protected Digest digest;
/*    */   
/*    */   public void init(BigInteger N, BigInteger g, Digest digest)
/*    */   {
/* 29 */     this.N = N;
/* 30 */     this.g = g;
/* 31 */     this.digest = digest;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger generateVerifier(byte[] salt, byte[] identity, byte[] password)
/*    */   {
/* 43 */     BigInteger x = SRP6Util.calculateX(this.digest, this.N, salt, identity, password);
/*    */     
/* 45 */     return this.g.modPow(x, this.N);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/agreement/srp/SRP6VerifierGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */