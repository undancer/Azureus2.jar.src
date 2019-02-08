/*     */ package org.gudy.bouncycastle.crypto.generators;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.security.SecureRandom;
/*     */ import org.gudy.bouncycastle.crypto.digests.SHA1Digest;
/*     */ import org.gudy.bouncycastle.crypto.params.DSAParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.DSAValidationParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DSAParametersGenerator
/*     */ {
/*     */   private int size;
/*     */   private int certainty;
/*     */   private SecureRandom random;
/*  19 */   private static BigInteger ONE = BigInteger.valueOf(1L);
/*  20 */   private static BigInteger TWO = BigInteger.valueOf(2L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init(int size, int certainty, SecureRandom random)
/*     */   {
/*  34 */     this.size = size;
/*  35 */     this.certainty = certainty;
/*  36 */     this.random = random;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void add(byte[] a, byte[] b, int value)
/*     */   {
/*  49 */     int x = (b[(b.length - 1)] & 0xFF) + value;
/*     */     
/*  51 */     a[(b.length - 1)] = ((byte)x);
/*  52 */     x >>>= 8;
/*     */     
/*  54 */     for (int i = b.length - 2; i >= 0; i--)
/*     */     {
/*  56 */       x += (b[i] & 0xFF);
/*  57 */       a[i] = ((byte)x);
/*  58 */       x >>>= 8;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DSAParameters generateParameters()
/*     */   {
/*  70 */     byte[] seed = new byte[20];
/*  71 */     byte[] part1 = new byte[20];
/*  72 */     byte[] part2 = new byte[20];
/*  73 */     byte[] u = new byte[20];
/*  74 */     SHA1Digest sha1 = new SHA1Digest();
/*  75 */     int n = (this.size - 1) / 160;
/*  76 */     byte[] w = new byte[this.size / 8];
/*     */     
/*  78 */     BigInteger q = null;BigInteger p = null;BigInteger g = null;
/*  79 */     int counter = 0;
/*  80 */     boolean primesFound = false;
/*     */     
/*  82 */     while (!primesFound)
/*     */     {
/*     */       do
/*     */       {
/*  86 */         this.random.nextBytes(seed);
/*     */         
/*  88 */         sha1.update(seed, 0, seed.length);
/*     */         
/*  90 */         sha1.doFinal(part1, 0);
/*     */         
/*  92 */         System.arraycopy(seed, 0, part2, 0, seed.length);
/*     */         
/*  94 */         add(part2, seed, 1);
/*     */         
/*  96 */         sha1.update(part2, 0, part2.length);
/*     */         
/*  98 */         sha1.doFinal(part2, 0);
/*     */         
/* 100 */         for (int i = 0; i != u.length; i++)
/*     */         {
/* 102 */           u[i] = ((byte)(part1[i] ^ part2[i]));
/*     */         }
/*     */         
/* 105 */         int tmp166_165 = 0; byte[] tmp166_163 = u;tmp166_163[tmp166_165] = ((byte)(tmp166_163[tmp166_165] | 0xFFFFFF80)); byte[] 
/* 106 */           tmp177_173 = u;tmp177_173[19] = ((byte)(tmp177_173[19] | 0x1));
/*     */         
/* 108 */         q = new BigInteger(1, u);
/*     */       }
/* 110 */       while (!q.isProbablePrime(this.certainty));
/*     */       
/* 112 */       counter = 0;
/*     */       
/* 114 */       int offset = 2;
/*     */       
/* 116 */       while (counter < 4096)
/*     */       {
/* 118 */         for (int k = 0; k < n; k++)
/*     */         {
/* 120 */           add(part1, seed, offset + k);
/* 121 */           sha1.update(part1, 0, part1.length);
/* 122 */           sha1.doFinal(part1, 0);
/* 123 */           System.arraycopy(part1, 0, w, w.length - (k + 1) * part1.length, part1.length);
/*     */         }
/*     */         
/* 126 */         add(part1, seed, offset + n);
/* 127 */         sha1.update(part1, 0, part1.length);
/* 128 */         sha1.doFinal(part1, 0);
/* 129 */         System.arraycopy(part1, part1.length - (w.length - n * part1.length), w, 0, w.length - n * part1.length); int 
/*     */         
/* 131 */           tmp344_343 = 0; byte[] tmp344_341 = w;tmp344_341[tmp344_343] = ((byte)(tmp344_341[tmp344_343] | 0xFFFFFF80));
/*     */         
/* 133 */         BigInteger x = new BigInteger(1, w);
/*     */         
/* 135 */         BigInteger c = x.mod(q.multiply(TWO));
/*     */         
/* 137 */         p = x.subtract(c.subtract(ONE));
/*     */         
/* 139 */         if (p.testBit(this.size - 1))
/*     */         {
/* 141 */           if (p.isProbablePrime(this.certainty))
/*     */           {
/* 143 */             primesFound = true;
/* 144 */             break;
/*     */           }
/*     */         }
/*     */         
/* 148 */         counter++;
/* 149 */         offset += n + 1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 156 */     BigInteger pMinusOneOverQ = p.subtract(ONE).divide(q);
/*     */     do {
/*     */       BigInteger h;
/*     */       do {
/* 160 */         h = new BigInteger(this.size, this.random);
/*     */       }
/* 162 */       while ((h.compareTo(ONE) <= 0) || (h.compareTo(p.subtract(ONE)) >= 0));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 167 */       g = h.modPow(pMinusOneOverQ, p);
/* 168 */     } while (g.compareTo(ONE) <= 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 176 */     return new DSAParameters(p, q, g, new DSAValidationParameters(seed, counter));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/DSAParametersGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */