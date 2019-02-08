/*    */ package org.gudy.bouncycastle.crypto.generators;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.params.DHParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DHParametersGenerator
/*    */ {
/*    */   private int size;
/*    */   private int certainty;
/*    */   private SecureRandom random;
/* 14 */   private static BigInteger ONE = BigInteger.valueOf(1L);
/* 15 */   private static BigInteger TWO = BigInteger.valueOf(2L);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void init(int size, int certainty, SecureRandom random)
/*    */   {
/* 22 */     this.size = size;
/* 23 */     this.certainty = certainty;
/* 24 */     this.random = random;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DHParameters generateParameters()
/*    */   {
/* 36 */     int qLength = this.size - 1;
/*    */     
/*    */     BigInteger q;
/*    */     
/*    */     BigInteger p;
/*    */     for (;;)
/*    */     {
/* 43 */       q = new BigInteger(qLength, this.certainty, this.random);
/* 44 */       p = q.multiply(TWO).add(ONE);
/* 45 */       if (p.isProbablePrime(this.certainty)) {
/*    */         break;
/*    */       }
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */     BigInteger g;
/*    */     
/*    */ 
/*    */     do
/*    */     {
/* 57 */       g = new BigInteger(qLength, this.random);
/*    */     }
/* 59 */     while ((g.modPow(TWO, p).equals(ONE)) || 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 64 */       (g.modPow(q, p).equals(ONE)));
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 72 */     return new DHParameters(p, g, q, 2);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/DHParametersGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */