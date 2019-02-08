/*    */ package org.gudy.bouncycastle.crypto.agreement.srp;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.CryptoException;
/*    */ import org.gudy.bouncycastle.crypto.Digest;
/*    */ import org.gudy.bouncycastle.util.BigIntegers;
/*    */ 
/*    */ 
/*    */ public class SRP6Util
/*    */ {
/* 12 */   private static BigInteger ZERO = BigInteger.valueOf(0L);
/* 13 */   private static BigInteger ONE = BigInteger.valueOf(1L);
/*    */   
/*    */   public static BigInteger calculateK(Digest digest, BigInteger N, BigInteger g)
/*    */   {
/* 17 */     return hashPaddedPair(digest, N, N, g);
/*    */   }
/*    */   
/*    */   public static BigInteger calculateU(Digest digest, BigInteger N, BigInteger A, BigInteger B)
/*    */   {
/* 22 */     return hashPaddedPair(digest, N, A, B);
/*    */   }
/*    */   
/*    */   public static BigInteger calculateX(Digest digest, BigInteger N, byte[] salt, byte[] identity, byte[] password)
/*    */   {
/* 27 */     byte[] output = new byte[digest.getDigestSize()];
/*    */     
/* 29 */     digest.update(identity, 0, identity.length);
/* 30 */     digest.update((byte)58);
/* 31 */     digest.update(password, 0, password.length);
/* 32 */     digest.doFinal(output, 0);
/*    */     
/* 34 */     digest.update(salt, 0, salt.length);
/* 35 */     digest.update(output, 0, output.length);
/* 36 */     digest.doFinal(output, 0);
/*    */     
/* 38 */     return new BigInteger(1, output);
/*    */   }
/*    */   
/*    */   public static BigInteger generatePrivateValue(Digest digest, BigInteger N, BigInteger g, SecureRandom random)
/*    */   {
/* 43 */     int minBits = Math.min(256, N.bitLength() / 2);
/* 44 */     BigInteger min = ONE.shiftLeft(minBits - 1);
/* 45 */     BigInteger max = N.subtract(ONE);
/*    */     
/* 47 */     return BigIntegers.createRandomInRange(min, max, random);
/*    */   }
/*    */   
/*    */   public static BigInteger validatePublicValue(BigInteger N, BigInteger val)
/*    */     throws CryptoException
/*    */   {
/* 53 */     val = val.mod(N);
/*    */     
/*    */ 
/* 56 */     if (val.equals(ZERO))
/*    */     {
/* 58 */       throw new CryptoException("Invalid public value: 0");
/*    */     }
/*    */     
/* 61 */     return val;
/*    */   }
/*    */   
/*    */   private static BigInteger hashPaddedPair(Digest digest, BigInteger N, BigInteger n1, BigInteger n2)
/*    */   {
/* 66 */     int padLength = (N.bitLength() + 7) / 8;
/*    */     
/* 68 */     byte[] n1_bytes = getPadded(n1, padLength);
/* 69 */     byte[] n2_bytes = getPadded(n2, padLength);
/*    */     
/* 71 */     digest.update(n1_bytes, 0, n1_bytes.length);
/* 72 */     digest.update(n2_bytes, 0, n2_bytes.length);
/*    */     
/* 74 */     byte[] output = new byte[digest.getDigestSize()];
/* 75 */     digest.doFinal(output, 0);
/*    */     
/* 77 */     return new BigInteger(1, output);
/*    */   }
/*    */   
/*    */   private static byte[] getPadded(BigInteger n, int length)
/*    */   {
/* 82 */     byte[] bs = BigIntegers.asUnsignedByteArray(n);
/* 83 */     if (bs.length < length)
/*    */     {
/* 85 */       byte[] tmp = new byte[length];
/* 86 */       System.arraycopy(bs, 0, tmp, length - bs.length, bs.length);
/* 87 */       bs = tmp;
/*    */     }
/* 89 */     return bs;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/agreement/srp/SRP6Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */