/*    */ package org.gudy.bouncycastle.crypto.generators;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.params.ElGamalParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ElGamalParametersGenerator
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
/*    */   public ElGamalParameters generateParameters()
/*    */   {
/* 36 */     int qLength = this.size - 1;
/*    */     
/*    */     BigInteger q;
/*    */     
/*    */     BigInteger p;
/*    */     for (;;)
/*    */     {
/* 43 */       q = new BigInteger(qLength, 1, this.random);
/*    */       
/* 45 */       if ((q.bitLength() == qLength) && 
/*    */       
/*    */ 
/*    */ 
/*    */ 
/* 50 */         (q.isProbablePrime(this.certainty)))
/*    */       {
/*    */ 
/*    */ 
/*    */ 
/* 55 */         p = q.multiply(TWO).add(ONE);
/* 56 */         if (p.isProbablePrime(this.certainty)) {
/*    */           break;
/*    */         }
/*    */       }
/*    */     }
/*    */     
/*    */ 
/*    */     BigInteger g;
/*    */     
/*    */ 
/*    */     do
/*    */     {
/* 68 */       g = new BigInteger(qLength, this.random);
/*    */     }
/* 70 */     while ((g.modPow(TWO, p).equals(ONE)) || 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 75 */       (g.modPow(q, p).equals(ONE)));
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 83 */     return new ElGamalParameters(p, g);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/ElGamalParametersGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */