/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ 
/*     */ public class RIPEMD160Digest
/*     */   extends GeneralDigest
/*     */ {
/*     */   private static final int DIGEST_LENGTH = 20;
/*     */   
/*     */   private int H0;
/*     */   
/*     */   private int H1;
/*     */   
/*     */   private int H2;
/*     */   
/*     */   private int H3;
/*     */   
/*     */   private int H4;
/*  18 */   private int[] X = new int[16];
/*     */   
/*     */ 
/*     */   private int xOff;
/*     */   
/*     */ 
/*     */   public RIPEMD160Digest()
/*     */   {
/*  26 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RIPEMD160Digest(RIPEMD160Digest t)
/*     */   {
/*  35 */     super(t);
/*     */     
/*  37 */     this.H0 = t.H0;
/*  38 */     this.H1 = t.H1;
/*  39 */     this.H2 = t.H2;
/*  40 */     this.H3 = t.H3;
/*  41 */     this.H4 = t.H4;
/*     */     
/*  43 */     System.arraycopy(t.X, 0, this.X, 0, t.X.length);
/*  44 */     this.xOff = t.xOff;
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  49 */     return "RIPEMD160";
/*     */   }
/*     */   
/*     */   public int getDigestSize()
/*     */   {
/*  54 */     return 20;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void processWord(byte[] in, int inOff)
/*     */   {
/*  61 */     this.X[(this.xOff++)] = (in[inOff] & 0xFF | (in[(inOff + 1)] & 0xFF) << 8 | (in[(inOff + 2)] & 0xFF) << 16 | (in[(inOff + 3)] & 0xFF) << 24);
/*     */     
/*     */ 
/*  64 */     if (this.xOff == 16)
/*     */     {
/*  66 */       processBlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void processLength(long bitLength)
/*     */   {
/*  73 */     if (this.xOff > 14)
/*     */     {
/*  75 */       processBlock();
/*     */     }
/*     */     
/*  78 */     this.X[14] = ((int)(bitLength & 0xFFFFFFFFFFFFFFFF));
/*  79 */     this.X[15] = ((int)(bitLength >>> 32));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void unpackWord(int word, byte[] out, int outOff)
/*     */   {
/*  87 */     out[outOff] = ((byte)word);
/*  88 */     out[(outOff + 1)] = ((byte)(word >>> 8));
/*  89 */     out[(outOff + 2)] = ((byte)(word >>> 16));
/*  90 */     out[(outOff + 3)] = ((byte)(word >>> 24));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int doFinal(byte[] out, int outOff)
/*     */   {
/*  97 */     finish();
/*     */     
/*  99 */     unpackWord(this.H0, out, outOff);
/* 100 */     unpackWord(this.H1, out, outOff + 4);
/* 101 */     unpackWord(this.H2, out, outOff + 8);
/* 102 */     unpackWord(this.H3, out, outOff + 12);
/* 103 */     unpackWord(this.H4, out, outOff + 16);
/*     */     
/* 105 */     reset();
/*     */     
/* 107 */     return 20;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 115 */     super.reset();
/*     */     
/* 117 */     this.H0 = 1732584193;
/* 118 */     this.H1 = -271733879;
/* 119 */     this.H2 = -1732584194;
/* 120 */     this.H3 = 271733878;
/* 121 */     this.H4 = -1009589776;
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
/*     */ 
/*     */ 
/*     */   private final int RL(int x, int n)
/*     */   {
/* 138 */     return x << n | x >>> 32 - n;
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
/*     */ 
/*     */ 
/*     */   private final int f1(int x, int y, int z)
/*     */   {
/* 153 */     return x ^ y ^ z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int f2(int x, int y, int z)
/*     */   {
/* 164 */     return x & y | (x ^ 0xFFFFFFFF) & z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int f3(int x, int y, int z)
/*     */   {
/* 175 */     return (x | y ^ 0xFFFFFFFF) ^ z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int f4(int x, int y, int z)
/*     */   {
/* 186 */     return x & z | y & (z ^ 0xFFFFFFFF);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int f5(int x, int y, int z)
/*     */   {
/* 197 */     return x ^ (y | z ^ 0xFFFFFFFF);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processBlock()
/*     */   {
/* 208 */     int a = aa = this.H0;
/* 209 */     int b = bb = this.H1;
/* 210 */     int c = cc = this.H2;
/* 211 */     int d = dd = this.H3;
/* 212 */     int e = ee = this.H4;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 218 */     a = RL(a + f1(b, c, d) + this.X[0], 11) + e;c = RL(c, 10);
/* 219 */     e = RL(e + f1(a, b, c) + this.X[1], 14) + d;b = RL(b, 10);
/* 220 */     d = RL(d + f1(e, a, b) + this.X[2], 15) + c;a = RL(a, 10);
/* 221 */     c = RL(c + f1(d, e, a) + this.X[3], 12) + b;e = RL(e, 10);
/* 222 */     b = RL(b + f1(c, d, e) + this.X[4], 5) + a;d = RL(d, 10);
/* 223 */     a = RL(a + f1(b, c, d) + this.X[5], 8) + e;c = RL(c, 10);
/* 224 */     e = RL(e + f1(a, b, c) + this.X[6], 7) + d;b = RL(b, 10);
/* 225 */     d = RL(d + f1(e, a, b) + this.X[7], 9) + c;a = RL(a, 10);
/* 226 */     c = RL(c + f1(d, e, a) + this.X[8], 11) + b;e = RL(e, 10);
/* 227 */     b = RL(b + f1(c, d, e) + this.X[9], 13) + a;d = RL(d, 10);
/* 228 */     a = RL(a + f1(b, c, d) + this.X[10], 14) + e;c = RL(c, 10);
/* 229 */     e = RL(e + f1(a, b, c) + this.X[11], 15) + d;b = RL(b, 10);
/* 230 */     d = RL(d + f1(e, a, b) + this.X[12], 6) + c;a = RL(a, 10);
/* 231 */     c = RL(c + f1(d, e, a) + this.X[13], 7) + b;e = RL(e, 10);
/* 232 */     b = RL(b + f1(c, d, e) + this.X[14], 9) + a;d = RL(d, 10);
/* 233 */     a = RL(a + f1(b, c, d) + this.X[15], 8) + e;c = RL(c, 10);
/*     */     
/*     */ 
/* 236 */     int aa = RL(aa + f5(bb, cc, dd) + this.X[5] + 1352829926, 8) + ee;int cc = RL(cc, 10);
/* 237 */     int ee = RL(ee + f5(aa, bb, cc) + this.X[14] + 1352829926, 9) + dd;int bb = RL(bb, 10);
/* 238 */     int dd = RL(dd + f5(ee, aa, bb) + this.X[7] + 1352829926, 9) + cc;aa = RL(aa, 10);
/* 239 */     cc = RL(cc + f5(dd, ee, aa) + this.X[0] + 1352829926, 11) + bb;ee = RL(ee, 10);
/* 240 */     bb = RL(bb + f5(cc, dd, ee) + this.X[9] + 1352829926, 13) + aa;dd = RL(dd, 10);
/* 241 */     aa = RL(aa + f5(bb, cc, dd) + this.X[2] + 1352829926, 15) + ee;cc = RL(cc, 10);
/* 242 */     ee = RL(ee + f5(aa, bb, cc) + this.X[11] + 1352829926, 15) + dd;bb = RL(bb, 10);
/* 243 */     dd = RL(dd + f5(ee, aa, bb) + this.X[4] + 1352829926, 5) + cc;aa = RL(aa, 10);
/* 244 */     cc = RL(cc + f5(dd, ee, aa) + this.X[13] + 1352829926, 7) + bb;ee = RL(ee, 10);
/* 245 */     bb = RL(bb + f5(cc, dd, ee) + this.X[6] + 1352829926, 7) + aa;dd = RL(dd, 10);
/* 246 */     aa = RL(aa + f5(bb, cc, dd) + this.X[15] + 1352829926, 8) + ee;cc = RL(cc, 10);
/* 247 */     ee = RL(ee + f5(aa, bb, cc) + this.X[8] + 1352829926, 11) + dd;bb = RL(bb, 10);
/* 248 */     dd = RL(dd + f5(ee, aa, bb) + this.X[1] + 1352829926, 14) + cc;aa = RL(aa, 10);
/* 249 */     cc = RL(cc + f5(dd, ee, aa) + this.X[10] + 1352829926, 14) + bb;ee = RL(ee, 10);
/* 250 */     bb = RL(bb + f5(cc, dd, ee) + this.X[3] + 1352829926, 12) + aa;dd = RL(dd, 10);
/* 251 */     aa = RL(aa + f5(bb, cc, dd) + this.X[12] + 1352829926, 6) + ee;cc = RL(cc, 10);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 257 */     e = RL(e + f2(a, b, c) + this.X[7] + 1518500249, 7) + d;b = RL(b, 10);
/* 258 */     d = RL(d + f2(e, a, b) + this.X[4] + 1518500249, 6) + c;a = RL(a, 10);
/* 259 */     c = RL(c + f2(d, e, a) + this.X[13] + 1518500249, 8) + b;e = RL(e, 10);
/* 260 */     b = RL(b + f2(c, d, e) + this.X[1] + 1518500249, 13) + a;d = RL(d, 10);
/* 261 */     a = RL(a + f2(b, c, d) + this.X[10] + 1518500249, 11) + e;c = RL(c, 10);
/* 262 */     e = RL(e + f2(a, b, c) + this.X[6] + 1518500249, 9) + d;b = RL(b, 10);
/* 263 */     d = RL(d + f2(e, a, b) + this.X[15] + 1518500249, 7) + c;a = RL(a, 10);
/* 264 */     c = RL(c + f2(d, e, a) + this.X[3] + 1518500249, 15) + b;e = RL(e, 10);
/* 265 */     b = RL(b + f2(c, d, e) + this.X[12] + 1518500249, 7) + a;d = RL(d, 10);
/* 266 */     a = RL(a + f2(b, c, d) + this.X[0] + 1518500249, 12) + e;c = RL(c, 10);
/* 267 */     e = RL(e + f2(a, b, c) + this.X[9] + 1518500249, 15) + d;b = RL(b, 10);
/* 268 */     d = RL(d + f2(e, a, b) + this.X[5] + 1518500249, 9) + c;a = RL(a, 10);
/* 269 */     c = RL(c + f2(d, e, a) + this.X[2] + 1518500249, 11) + b;e = RL(e, 10);
/* 270 */     b = RL(b + f2(c, d, e) + this.X[14] + 1518500249, 7) + a;d = RL(d, 10);
/* 271 */     a = RL(a + f2(b, c, d) + this.X[11] + 1518500249, 13) + e;c = RL(c, 10);
/* 272 */     e = RL(e + f2(a, b, c) + this.X[8] + 1518500249, 12) + d;b = RL(b, 10);
/*     */     
/*     */ 
/* 275 */     ee = RL(ee + f4(aa, bb, cc) + this.X[6] + 1548603684, 9) + dd;bb = RL(bb, 10);
/* 276 */     dd = RL(dd + f4(ee, aa, bb) + this.X[11] + 1548603684, 13) + cc;aa = RL(aa, 10);
/* 277 */     cc = RL(cc + f4(dd, ee, aa) + this.X[3] + 1548603684, 15) + bb;ee = RL(ee, 10);
/* 278 */     bb = RL(bb + f4(cc, dd, ee) + this.X[7] + 1548603684, 7) + aa;dd = RL(dd, 10);
/* 279 */     aa = RL(aa + f4(bb, cc, dd) + this.X[0] + 1548603684, 12) + ee;cc = RL(cc, 10);
/* 280 */     ee = RL(ee + f4(aa, bb, cc) + this.X[13] + 1548603684, 8) + dd;bb = RL(bb, 10);
/* 281 */     dd = RL(dd + f4(ee, aa, bb) + this.X[5] + 1548603684, 9) + cc;aa = RL(aa, 10);
/* 282 */     cc = RL(cc + f4(dd, ee, aa) + this.X[10] + 1548603684, 11) + bb;ee = RL(ee, 10);
/* 283 */     bb = RL(bb + f4(cc, dd, ee) + this.X[14] + 1548603684, 7) + aa;dd = RL(dd, 10);
/* 284 */     aa = RL(aa + f4(bb, cc, dd) + this.X[15] + 1548603684, 7) + ee;cc = RL(cc, 10);
/* 285 */     ee = RL(ee + f4(aa, bb, cc) + this.X[8] + 1548603684, 12) + dd;bb = RL(bb, 10);
/* 286 */     dd = RL(dd + f4(ee, aa, bb) + this.X[12] + 1548603684, 7) + cc;aa = RL(aa, 10);
/* 287 */     cc = RL(cc + f4(dd, ee, aa) + this.X[4] + 1548603684, 6) + bb;ee = RL(ee, 10);
/* 288 */     bb = RL(bb + f4(cc, dd, ee) + this.X[9] + 1548603684, 15) + aa;dd = RL(dd, 10);
/* 289 */     aa = RL(aa + f4(bb, cc, dd) + this.X[1] + 1548603684, 13) + ee;cc = RL(cc, 10);
/* 290 */     ee = RL(ee + f4(aa, bb, cc) + this.X[2] + 1548603684, 11) + dd;bb = RL(bb, 10);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 296 */     d = RL(d + f3(e, a, b) + this.X[3] + 1859775393, 11) + c;a = RL(a, 10);
/* 297 */     c = RL(c + f3(d, e, a) + this.X[10] + 1859775393, 13) + b;e = RL(e, 10);
/* 298 */     b = RL(b + f3(c, d, e) + this.X[14] + 1859775393, 6) + a;d = RL(d, 10);
/* 299 */     a = RL(a + f3(b, c, d) + this.X[4] + 1859775393, 7) + e;c = RL(c, 10);
/* 300 */     e = RL(e + f3(a, b, c) + this.X[9] + 1859775393, 14) + d;b = RL(b, 10);
/* 301 */     d = RL(d + f3(e, a, b) + this.X[15] + 1859775393, 9) + c;a = RL(a, 10);
/* 302 */     c = RL(c + f3(d, e, a) + this.X[8] + 1859775393, 13) + b;e = RL(e, 10);
/* 303 */     b = RL(b + f3(c, d, e) + this.X[1] + 1859775393, 15) + a;d = RL(d, 10);
/* 304 */     a = RL(a + f3(b, c, d) + this.X[2] + 1859775393, 14) + e;c = RL(c, 10);
/* 305 */     e = RL(e + f3(a, b, c) + this.X[7] + 1859775393, 8) + d;b = RL(b, 10);
/* 306 */     d = RL(d + f3(e, a, b) + this.X[0] + 1859775393, 13) + c;a = RL(a, 10);
/* 307 */     c = RL(c + f3(d, e, a) + this.X[6] + 1859775393, 6) + b;e = RL(e, 10);
/* 308 */     b = RL(b + f3(c, d, e) + this.X[13] + 1859775393, 5) + a;d = RL(d, 10);
/* 309 */     a = RL(a + f3(b, c, d) + this.X[11] + 1859775393, 12) + e;c = RL(c, 10);
/* 310 */     e = RL(e + f3(a, b, c) + this.X[5] + 1859775393, 7) + d;b = RL(b, 10);
/* 311 */     d = RL(d + f3(e, a, b) + this.X[12] + 1859775393, 5) + c;a = RL(a, 10);
/*     */     
/*     */ 
/* 314 */     dd = RL(dd + f3(ee, aa, bb) + this.X[15] + 1836072691, 9) + cc;aa = RL(aa, 10);
/* 315 */     cc = RL(cc + f3(dd, ee, aa) + this.X[5] + 1836072691, 7) + bb;ee = RL(ee, 10);
/* 316 */     bb = RL(bb + f3(cc, dd, ee) + this.X[1] + 1836072691, 15) + aa;dd = RL(dd, 10);
/* 317 */     aa = RL(aa + f3(bb, cc, dd) + this.X[3] + 1836072691, 11) + ee;cc = RL(cc, 10);
/* 318 */     ee = RL(ee + f3(aa, bb, cc) + this.X[7] + 1836072691, 8) + dd;bb = RL(bb, 10);
/* 319 */     dd = RL(dd + f3(ee, aa, bb) + this.X[14] + 1836072691, 6) + cc;aa = RL(aa, 10);
/* 320 */     cc = RL(cc + f3(dd, ee, aa) + this.X[6] + 1836072691, 6) + bb;ee = RL(ee, 10);
/* 321 */     bb = RL(bb + f3(cc, dd, ee) + this.X[9] + 1836072691, 14) + aa;dd = RL(dd, 10);
/* 322 */     aa = RL(aa + f3(bb, cc, dd) + this.X[11] + 1836072691, 12) + ee;cc = RL(cc, 10);
/* 323 */     ee = RL(ee + f3(aa, bb, cc) + this.X[8] + 1836072691, 13) + dd;bb = RL(bb, 10);
/* 324 */     dd = RL(dd + f3(ee, aa, bb) + this.X[12] + 1836072691, 5) + cc;aa = RL(aa, 10);
/* 325 */     cc = RL(cc + f3(dd, ee, aa) + this.X[2] + 1836072691, 14) + bb;ee = RL(ee, 10);
/* 326 */     bb = RL(bb + f3(cc, dd, ee) + this.X[10] + 1836072691, 13) + aa;dd = RL(dd, 10);
/* 327 */     aa = RL(aa + f3(bb, cc, dd) + this.X[0] + 1836072691, 13) + ee;cc = RL(cc, 10);
/* 328 */     ee = RL(ee + f3(aa, bb, cc) + this.X[4] + 1836072691, 7) + dd;bb = RL(bb, 10);
/* 329 */     dd = RL(dd + f3(ee, aa, bb) + this.X[13] + 1836072691, 5) + cc;aa = RL(aa, 10);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 335 */     c = RL(c + f4(d, e, a) + this.X[1] + -1894007588, 11) + b;e = RL(e, 10);
/* 336 */     b = RL(b + f4(c, d, e) + this.X[9] + -1894007588, 12) + a;d = RL(d, 10);
/* 337 */     a = RL(a + f4(b, c, d) + this.X[11] + -1894007588, 14) + e;c = RL(c, 10);
/* 338 */     e = RL(e + f4(a, b, c) + this.X[10] + -1894007588, 15) + d;b = RL(b, 10);
/* 339 */     d = RL(d + f4(e, a, b) + this.X[0] + -1894007588, 14) + c;a = RL(a, 10);
/* 340 */     c = RL(c + f4(d, e, a) + this.X[8] + -1894007588, 15) + b;e = RL(e, 10);
/* 341 */     b = RL(b + f4(c, d, e) + this.X[12] + -1894007588, 9) + a;d = RL(d, 10);
/* 342 */     a = RL(a + f4(b, c, d) + this.X[4] + -1894007588, 8) + e;c = RL(c, 10);
/* 343 */     e = RL(e + f4(a, b, c) + this.X[13] + -1894007588, 9) + d;b = RL(b, 10);
/* 344 */     d = RL(d + f4(e, a, b) + this.X[3] + -1894007588, 14) + c;a = RL(a, 10);
/* 345 */     c = RL(c + f4(d, e, a) + this.X[7] + -1894007588, 5) + b;e = RL(e, 10);
/* 346 */     b = RL(b + f4(c, d, e) + this.X[15] + -1894007588, 6) + a;d = RL(d, 10);
/* 347 */     a = RL(a + f4(b, c, d) + this.X[14] + -1894007588, 8) + e;c = RL(c, 10);
/* 348 */     e = RL(e + f4(a, b, c) + this.X[5] + -1894007588, 6) + d;b = RL(b, 10);
/* 349 */     d = RL(d + f4(e, a, b) + this.X[6] + -1894007588, 5) + c;a = RL(a, 10);
/* 350 */     c = RL(c + f4(d, e, a) + this.X[2] + -1894007588, 12) + b;e = RL(e, 10);
/*     */     
/*     */ 
/* 353 */     cc = RL(cc + f2(dd, ee, aa) + this.X[8] + 2053994217, 15) + bb;ee = RL(ee, 10);
/* 354 */     bb = RL(bb + f2(cc, dd, ee) + this.X[6] + 2053994217, 5) + aa;dd = RL(dd, 10);
/* 355 */     aa = RL(aa + f2(bb, cc, dd) + this.X[4] + 2053994217, 8) + ee;cc = RL(cc, 10);
/* 356 */     ee = RL(ee + f2(aa, bb, cc) + this.X[1] + 2053994217, 11) + dd;bb = RL(bb, 10);
/* 357 */     dd = RL(dd + f2(ee, aa, bb) + this.X[3] + 2053994217, 14) + cc;aa = RL(aa, 10);
/* 358 */     cc = RL(cc + f2(dd, ee, aa) + this.X[11] + 2053994217, 14) + bb;ee = RL(ee, 10);
/* 359 */     bb = RL(bb + f2(cc, dd, ee) + this.X[15] + 2053994217, 6) + aa;dd = RL(dd, 10);
/* 360 */     aa = RL(aa + f2(bb, cc, dd) + this.X[0] + 2053994217, 14) + ee;cc = RL(cc, 10);
/* 361 */     ee = RL(ee + f2(aa, bb, cc) + this.X[5] + 2053994217, 6) + dd;bb = RL(bb, 10);
/* 362 */     dd = RL(dd + f2(ee, aa, bb) + this.X[12] + 2053994217, 9) + cc;aa = RL(aa, 10);
/* 363 */     cc = RL(cc + f2(dd, ee, aa) + this.X[2] + 2053994217, 12) + bb;ee = RL(ee, 10);
/* 364 */     bb = RL(bb + f2(cc, dd, ee) + this.X[13] + 2053994217, 9) + aa;dd = RL(dd, 10);
/* 365 */     aa = RL(aa + f2(bb, cc, dd) + this.X[9] + 2053994217, 12) + ee;cc = RL(cc, 10);
/* 366 */     ee = RL(ee + f2(aa, bb, cc) + this.X[7] + 2053994217, 5) + dd;bb = RL(bb, 10);
/* 367 */     dd = RL(dd + f2(ee, aa, bb) + this.X[10] + 2053994217, 15) + cc;aa = RL(aa, 10);
/* 368 */     cc = RL(cc + f2(dd, ee, aa) + this.X[14] + 2053994217, 8) + bb;ee = RL(ee, 10);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 374 */     b = RL(b + f5(c, d, e) + this.X[4] + -1454113458, 9) + a;d = RL(d, 10);
/* 375 */     a = RL(a + f5(b, c, d) + this.X[0] + -1454113458, 15) + e;c = RL(c, 10);
/* 376 */     e = RL(e + f5(a, b, c) + this.X[5] + -1454113458, 5) + d;b = RL(b, 10);
/* 377 */     d = RL(d + f5(e, a, b) + this.X[9] + -1454113458, 11) + c;a = RL(a, 10);
/* 378 */     c = RL(c + f5(d, e, a) + this.X[7] + -1454113458, 6) + b;e = RL(e, 10);
/* 379 */     b = RL(b + f5(c, d, e) + this.X[12] + -1454113458, 8) + a;d = RL(d, 10);
/* 380 */     a = RL(a + f5(b, c, d) + this.X[2] + -1454113458, 13) + e;c = RL(c, 10);
/* 381 */     e = RL(e + f5(a, b, c) + this.X[10] + -1454113458, 12) + d;b = RL(b, 10);
/* 382 */     d = RL(d + f5(e, a, b) + this.X[14] + -1454113458, 5) + c;a = RL(a, 10);
/* 383 */     c = RL(c + f5(d, e, a) + this.X[1] + -1454113458, 12) + b;e = RL(e, 10);
/* 384 */     b = RL(b + f5(c, d, e) + this.X[3] + -1454113458, 13) + a;d = RL(d, 10);
/* 385 */     a = RL(a + f5(b, c, d) + this.X[8] + -1454113458, 14) + e;c = RL(c, 10);
/* 386 */     e = RL(e + f5(a, b, c) + this.X[11] + -1454113458, 11) + d;b = RL(b, 10);
/* 387 */     d = RL(d + f5(e, a, b) + this.X[6] + -1454113458, 8) + c;a = RL(a, 10);
/* 388 */     c = RL(c + f5(d, e, a) + this.X[15] + -1454113458, 5) + b;e = RL(e, 10);
/* 389 */     b = RL(b + f5(c, d, e) + this.X[13] + -1454113458, 6) + a;d = RL(d, 10);
/*     */     
/*     */ 
/* 392 */     bb = RL(bb + f1(cc, dd, ee) + this.X[12], 8) + aa;dd = RL(dd, 10);
/* 393 */     aa = RL(aa + f1(bb, cc, dd) + this.X[15], 5) + ee;cc = RL(cc, 10);
/* 394 */     ee = RL(ee + f1(aa, bb, cc) + this.X[10], 12) + dd;bb = RL(bb, 10);
/* 395 */     dd = RL(dd + f1(ee, aa, bb) + this.X[4], 9) + cc;aa = RL(aa, 10);
/* 396 */     cc = RL(cc + f1(dd, ee, aa) + this.X[1], 12) + bb;ee = RL(ee, 10);
/* 397 */     bb = RL(bb + f1(cc, dd, ee) + this.X[5], 5) + aa;dd = RL(dd, 10);
/* 398 */     aa = RL(aa + f1(bb, cc, dd) + this.X[8], 14) + ee;cc = RL(cc, 10);
/* 399 */     ee = RL(ee + f1(aa, bb, cc) + this.X[7], 6) + dd;bb = RL(bb, 10);
/* 400 */     dd = RL(dd + f1(ee, aa, bb) + this.X[6], 8) + cc;aa = RL(aa, 10);
/* 401 */     cc = RL(cc + f1(dd, ee, aa) + this.X[2], 13) + bb;ee = RL(ee, 10);
/* 402 */     bb = RL(bb + f1(cc, dd, ee) + this.X[13], 6) + aa;dd = RL(dd, 10);
/* 403 */     aa = RL(aa + f1(bb, cc, dd) + this.X[14], 5) + ee;cc = RL(cc, 10);
/* 404 */     ee = RL(ee + f1(aa, bb, cc) + this.X[0], 15) + dd;bb = RL(bb, 10);
/* 405 */     dd = RL(dd + f1(ee, aa, bb) + this.X[3], 13) + cc;aa = RL(aa, 10);
/* 406 */     cc = RL(cc + f1(dd, ee, aa) + this.X[9], 11) + bb;ee = RL(ee, 10);
/* 407 */     bb = RL(bb + f1(cc, dd, ee) + this.X[11], 11) + aa;dd = RL(dd, 10);
/*     */     
/* 409 */     dd += c + this.H1;
/* 410 */     this.H1 = (this.H2 + d + ee);
/* 411 */     this.H2 = (this.H3 + e + aa);
/* 412 */     this.H3 = (this.H4 + a + bb);
/* 413 */     this.H4 = (this.H0 + b + cc);
/* 414 */     this.H0 = dd;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 419 */     this.xOff = 0;
/* 420 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 422 */       this.X[i] = 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/RIPEMD160Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */