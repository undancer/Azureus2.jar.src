/*    */ package org.gudy.bouncycastle.util;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.SecureRandom;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class BigIntegers
/*    */ {
/*    */   private static final int MAX_ITERATIONS = 1000;
/* 12 */   private static final BigInteger ZERO = BigInteger.valueOf(0L);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static byte[] asUnsignedByteArray(BigInteger value)
/*    */   {
/* 23 */     byte[] bytes = value.toByteArray();
/*    */     
/* 25 */     if (bytes[0] == 0)
/*    */     {
/* 27 */       byte[] tmp = new byte[bytes.length - 1];
/*    */       
/* 29 */       System.arraycopy(bytes, 1, tmp, 0, tmp.length);
/*    */       
/* 31 */       return tmp;
/*    */     }
/*    */     
/* 34 */     return bytes;
/*    */   }
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
/*    */   public static BigInteger createRandomInRange(BigInteger min, BigInteger max, SecureRandom random)
/*    */   {
/* 50 */     int cmp = min.compareTo(max);
/* 51 */     if (cmp >= 0)
/*    */     {
/* 53 */       if (cmp > 0)
/*    */       {
/* 55 */         throw new IllegalArgumentException("'min' may not be greater than 'max'");
/*    */       }
/*    */       
/* 58 */       return min;
/*    */     }
/*    */     
/* 61 */     if (min.bitLength() > max.bitLength() / 2)
/*    */     {
/* 63 */       return createRandomInRange(ZERO, max.subtract(min), random).add(min);
/*    */     }
/*    */     
/* 66 */     for (int i = 0; i < 1000; i++)
/*    */     {
/* 68 */       BigInteger x = new BigInteger(max.bitLength(), random);
/* 69 */       if ((x.compareTo(min) >= 0) && (x.compareTo(max) <= 0))
/*    */       {
/* 71 */         return x;
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 76 */     return new BigInteger(max.subtract(min).bitLength() - 1, random).add(min);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/util/BigIntegers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */