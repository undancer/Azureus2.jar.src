/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MD4Digest
/*     */   extends GeneralDigest
/*     */ {
/*     */   private static final int DIGEST_LENGTH = 16;
/*     */   
/*     */ 
/*     */   private int H1;
/*     */   
/*     */ 
/*     */   private int H2;
/*     */   
/*     */ 
/*     */   private int H3;
/*     */   
/*     */   private int H4;
/*     */   
/*  21 */   private int[] X = new int[16];
/*     */   private int xOff;
/*     */   private static final int S11 = 3;
/*     */   private static final int S12 = 7;
/*     */   private static final int S13 = 11;
/*     */   private static final int S14 = 19;
/*     */   private static final int S21 = 3;
/*     */   
/*  29 */   public MD4Digest() { reset(); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MD4Digest(MD4Digest t)
/*     */   {
/*  38 */     super(t);
/*     */     
/*  40 */     this.H1 = t.H1;
/*  41 */     this.H2 = t.H2;
/*  42 */     this.H3 = t.H3;
/*  43 */     this.H4 = t.H4;
/*     */     
/*  45 */     System.arraycopy(t.X, 0, this.X, 0, t.X.length);
/*  46 */     this.xOff = t.xOff;
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  51 */     return "MD4";
/*     */   }
/*     */   
/*     */   public int getDigestSize()
/*     */   {
/*  56 */     return 16;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void processWord(byte[] in, int inOff)
/*     */   {
/*  63 */     this.X[(this.xOff++)] = (in[inOff] & 0xFF | (in[(inOff + 1)] & 0xFF) << 8 | (in[(inOff + 2)] & 0xFF) << 16 | (in[(inOff + 3)] & 0xFF) << 24);
/*     */     
/*     */ 
/*  66 */     if (this.xOff == 16)
/*     */     {
/*  68 */       processBlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void processLength(long bitLength)
/*     */   {
/*  75 */     if (this.xOff > 14)
/*     */     {
/*  77 */       processBlock();
/*     */     }
/*     */     
/*  80 */     this.X[14] = ((int)(bitLength & 0xFFFFFFFFFFFFFFFF));
/*  81 */     this.X[15] = ((int)(bitLength >>> 32));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void unpackWord(int word, byte[] out, int outOff)
/*     */   {
/*  89 */     out[outOff] = ((byte)word);
/*  90 */     out[(outOff + 1)] = ((byte)(word >>> 8));
/*  91 */     out[(outOff + 2)] = ((byte)(word >>> 16));
/*  92 */     out[(outOff + 3)] = ((byte)(word >>> 24));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int doFinal(byte[] out, int outOff)
/*     */   {
/*  99 */     finish();
/*     */     
/* 101 */     unpackWord(this.H1, out, outOff);
/* 102 */     unpackWord(this.H2, out, outOff + 4);
/* 103 */     unpackWord(this.H3, out, outOff + 8);
/* 104 */     unpackWord(this.H4, out, outOff + 12);
/*     */     
/* 106 */     reset();
/*     */     
/* 108 */     return 16;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 116 */     super.reset();
/*     */     
/* 118 */     this.H1 = 1732584193;
/* 119 */     this.H2 = -271733879;
/* 120 */     this.H3 = -1732584194;
/* 121 */     this.H4 = 271733878;
/*     */     
/* 123 */     this.xOff = 0;
/*     */     
/* 125 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 127 */       this.X[i] = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int S22 = 5;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int S23 = 9;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int S24 = 13;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int S31 = 3;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int S32 = 9;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int S33 = 11;
/*     */   
/*     */ 
/*     */   private static final int S34 = 15;
/*     */   
/*     */ 
/*     */   private int rotateLeft(int x, int n)
/*     */   {
/* 162 */     return x << n | x >>> 32 - n;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int F(int u, int v, int w)
/*     */   {
/* 173 */     return u & v | (u ^ 0xFFFFFFFF) & w;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int G(int u, int v, int w)
/*     */   {
/* 181 */     return u & v | u & w | v & w;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int H(int u, int v, int w)
/*     */   {
/* 189 */     return u ^ v ^ w;
/*     */   }
/*     */   
/*     */   protected void processBlock()
/*     */   {
/* 194 */     int a = this.H1;
/* 195 */     int b = this.H2;
/* 196 */     int c = this.H3;
/* 197 */     int d = this.H4;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 202 */     a = rotateLeft(a + F(b, c, d) + this.X[0], 3);
/* 203 */     d = rotateLeft(d + F(a, b, c) + this.X[1], 7);
/* 204 */     c = rotateLeft(c + F(d, a, b) + this.X[2], 11);
/* 205 */     b = rotateLeft(b + F(c, d, a) + this.X[3], 19);
/* 206 */     a = rotateLeft(a + F(b, c, d) + this.X[4], 3);
/* 207 */     d = rotateLeft(d + F(a, b, c) + this.X[5], 7);
/* 208 */     c = rotateLeft(c + F(d, a, b) + this.X[6], 11);
/* 209 */     b = rotateLeft(b + F(c, d, a) + this.X[7], 19);
/* 210 */     a = rotateLeft(a + F(b, c, d) + this.X[8], 3);
/* 211 */     d = rotateLeft(d + F(a, b, c) + this.X[9], 7);
/* 212 */     c = rotateLeft(c + F(d, a, b) + this.X[10], 11);
/* 213 */     b = rotateLeft(b + F(c, d, a) + this.X[11], 19);
/* 214 */     a = rotateLeft(a + F(b, c, d) + this.X[12], 3);
/* 215 */     d = rotateLeft(d + F(a, b, c) + this.X[13], 7);
/* 216 */     c = rotateLeft(c + F(d, a, b) + this.X[14], 11);
/* 217 */     b = rotateLeft(b + F(c, d, a) + this.X[15], 19);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 222 */     a = rotateLeft(a + G(b, c, d) + this.X[0] + 1518500249, 3);
/* 223 */     d = rotateLeft(d + G(a, b, c) + this.X[4] + 1518500249, 5);
/* 224 */     c = rotateLeft(c + G(d, a, b) + this.X[8] + 1518500249, 9);
/* 225 */     b = rotateLeft(b + G(c, d, a) + this.X[12] + 1518500249, 13);
/* 226 */     a = rotateLeft(a + G(b, c, d) + this.X[1] + 1518500249, 3);
/* 227 */     d = rotateLeft(d + G(a, b, c) + this.X[5] + 1518500249, 5);
/* 228 */     c = rotateLeft(c + G(d, a, b) + this.X[9] + 1518500249, 9);
/* 229 */     b = rotateLeft(b + G(c, d, a) + this.X[13] + 1518500249, 13);
/* 230 */     a = rotateLeft(a + G(b, c, d) + this.X[2] + 1518500249, 3);
/* 231 */     d = rotateLeft(d + G(a, b, c) + this.X[6] + 1518500249, 5);
/* 232 */     c = rotateLeft(c + G(d, a, b) + this.X[10] + 1518500249, 9);
/* 233 */     b = rotateLeft(b + G(c, d, a) + this.X[14] + 1518500249, 13);
/* 234 */     a = rotateLeft(a + G(b, c, d) + this.X[3] + 1518500249, 3);
/* 235 */     d = rotateLeft(d + G(a, b, c) + this.X[7] + 1518500249, 5);
/* 236 */     c = rotateLeft(c + G(d, a, b) + this.X[11] + 1518500249, 9);
/* 237 */     b = rotateLeft(b + G(c, d, a) + this.X[15] + 1518500249, 13);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 242 */     a = rotateLeft(a + H(b, c, d) + this.X[0] + 1859775393, 3);
/* 243 */     d = rotateLeft(d + H(a, b, c) + this.X[8] + 1859775393, 9);
/* 244 */     c = rotateLeft(c + H(d, a, b) + this.X[4] + 1859775393, 11);
/* 245 */     b = rotateLeft(b + H(c, d, a) + this.X[12] + 1859775393, 15);
/* 246 */     a = rotateLeft(a + H(b, c, d) + this.X[2] + 1859775393, 3);
/* 247 */     d = rotateLeft(d + H(a, b, c) + this.X[10] + 1859775393, 9);
/* 248 */     c = rotateLeft(c + H(d, a, b) + this.X[6] + 1859775393, 11);
/* 249 */     b = rotateLeft(b + H(c, d, a) + this.X[14] + 1859775393, 15);
/* 250 */     a = rotateLeft(a + H(b, c, d) + this.X[1] + 1859775393, 3);
/* 251 */     d = rotateLeft(d + H(a, b, c) + this.X[9] + 1859775393, 9);
/* 252 */     c = rotateLeft(c + H(d, a, b) + this.X[5] + 1859775393, 11);
/* 253 */     b = rotateLeft(b + H(c, d, a) + this.X[13] + 1859775393, 15);
/* 254 */     a = rotateLeft(a + H(b, c, d) + this.X[3] + 1859775393, 3);
/* 255 */     d = rotateLeft(d + H(a, b, c) + this.X[11] + 1859775393, 9);
/* 256 */     c = rotateLeft(c + H(d, a, b) + this.X[7] + 1859775393, 11);
/* 257 */     b = rotateLeft(b + H(c, d, a) + this.X[15] + 1859775393, 15);
/*     */     
/* 259 */     this.H1 += a;
/* 260 */     this.H2 += b;
/* 261 */     this.H3 += c;
/* 262 */     this.H4 += d;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 267 */     this.xOff = 0;
/* 268 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 270 */       this.X[i] = 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/MD4Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */