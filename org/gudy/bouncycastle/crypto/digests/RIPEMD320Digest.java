/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ 
/*     */ public class RIPEMD320Digest
/*     */   extends GeneralDigest
/*     */ {
/*     */   private static final int DIGEST_LENGTH = 40;
/*     */   
/*     */   private int H0;
/*     */   private int H1;
/*     */   private int H2;
/*     */   private int H3;
/*     */   private int H4;
/*     */   private int H5;
/*     */   private int H6;
/*     */   private int H7;
/*     */   private int H8;
/*     */   private int H9;
/*  19 */   private int[] X = new int[16];
/*     */   
/*     */ 
/*     */   private int xOff;
/*     */   
/*     */ 
/*     */   public RIPEMD320Digest()
/*     */   {
/*  27 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RIPEMD320Digest(RIPEMD320Digest t)
/*     */   {
/*  36 */     super(t);
/*     */     
/*  38 */     this.H0 = t.H0;
/*  39 */     this.H1 = t.H1;
/*  40 */     this.H2 = t.H2;
/*  41 */     this.H3 = t.H3;
/*  42 */     this.H4 = t.H4;
/*  43 */     this.H5 = t.H5;
/*  44 */     this.H6 = t.H6;
/*  45 */     this.H7 = t.H7;
/*  46 */     this.H8 = t.H8;
/*  47 */     this.H9 = t.H9;
/*     */     
/*  49 */     System.arraycopy(t.X, 0, this.X, 0, t.X.length);
/*  50 */     this.xOff = t.xOff;
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  55 */     return "RIPEMD320";
/*     */   }
/*     */   
/*     */   public int getDigestSize()
/*     */   {
/*  60 */     return 40;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void processWord(byte[] in, int inOff)
/*     */   {
/*  67 */     this.X[(this.xOff++)] = (in[inOff] & 0xFF | (in[(inOff + 1)] & 0xFF) << 8 | (in[(inOff + 2)] & 0xFF) << 16 | (in[(inOff + 3)] & 0xFF) << 24);
/*     */     
/*     */ 
/*  70 */     if (this.xOff == 16)
/*     */     {
/*  72 */       processBlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void processLength(long bitLength)
/*     */   {
/*  79 */     if (this.xOff > 14)
/*     */     {
/*  81 */       processBlock();
/*     */     }
/*     */     
/*  84 */     this.X[14] = ((int)(bitLength & 0xFFFFFFFFFFFFFFFF));
/*  85 */     this.X[15] = ((int)(bitLength >>> 32));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void unpackWord(int word, byte[] out, int outOff)
/*     */   {
/*  93 */     out[outOff] = ((byte)word);
/*  94 */     out[(outOff + 1)] = ((byte)(word >>> 8));
/*  95 */     out[(outOff + 2)] = ((byte)(word >>> 16));
/*  96 */     out[(outOff + 3)] = ((byte)(word >>> 24));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int doFinal(byte[] out, int outOff)
/*     */   {
/* 103 */     finish();
/*     */     
/* 105 */     unpackWord(this.H0, out, outOff);
/* 106 */     unpackWord(this.H1, out, outOff + 4);
/* 107 */     unpackWord(this.H2, out, outOff + 8);
/* 108 */     unpackWord(this.H3, out, outOff + 12);
/* 109 */     unpackWord(this.H4, out, outOff + 16);
/* 110 */     unpackWord(this.H5, out, outOff + 20);
/* 111 */     unpackWord(this.H6, out, outOff + 24);
/* 112 */     unpackWord(this.H7, out, outOff + 28);
/* 113 */     unpackWord(this.H8, out, outOff + 32);
/* 114 */     unpackWord(this.H9, out, outOff + 36);
/*     */     
/* 116 */     reset();
/*     */     
/* 118 */     return 40;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 126 */     super.reset();
/*     */     
/* 128 */     this.H0 = 1732584193;
/* 129 */     this.H1 = -271733879;
/* 130 */     this.H2 = -1732584194;
/* 131 */     this.H3 = 271733878;
/* 132 */     this.H4 = -1009589776;
/* 133 */     this.H5 = 1985229328;
/* 134 */     this.H6 = -19088744;
/* 135 */     this.H7 = -1985229329;
/* 136 */     this.H8 = 19088743;
/* 137 */     this.H9 = 1009589775;
/*     */     
/* 139 */     this.xOff = 0;
/*     */     
/* 141 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 143 */       this.X[i] = 0;
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
/* 154 */     return x << n | x >>> 32 - n;
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
/* 169 */     return x ^ y ^ z;
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
/* 180 */     return x & y | (x ^ 0xFFFFFFFF) & z;
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
/* 191 */     return (x | y ^ 0xFFFFFFFF) ^ z;
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
/* 202 */     return x & z | y & (z ^ 0xFFFFFFFF);
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
/* 213 */     return x ^ (y | z ^ 0xFFFFFFFF);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processBlock()
/*     */   {
/* 225 */     int a = this.H0;
/* 226 */     int b = this.H1;
/* 227 */     int c = this.H2;
/* 228 */     int d = this.H3;
/* 229 */     int e = this.H4;
/* 230 */     int aa = this.H5;
/* 231 */     int bb = this.H6;
/* 232 */     int cc = this.H7;
/* 233 */     int dd = this.H8;
/* 234 */     int ee = this.H9;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 240 */     a = RL(a + f1(b, c, d) + this.X[0], 11) + e;c = RL(c, 10);
/* 241 */     e = RL(e + f1(a, b, c) + this.X[1], 14) + d;b = RL(b, 10);
/* 242 */     d = RL(d + f1(e, a, b) + this.X[2], 15) + c;a = RL(a, 10);
/* 243 */     c = RL(c + f1(d, e, a) + this.X[3], 12) + b;e = RL(e, 10);
/* 244 */     b = RL(b + f1(c, d, e) + this.X[4], 5) + a;d = RL(d, 10);
/* 245 */     a = RL(a + f1(b, c, d) + this.X[5], 8) + e;c = RL(c, 10);
/* 246 */     e = RL(e + f1(a, b, c) + this.X[6], 7) + d;b = RL(b, 10);
/* 247 */     d = RL(d + f1(e, a, b) + this.X[7], 9) + c;a = RL(a, 10);
/* 248 */     c = RL(c + f1(d, e, a) + this.X[8], 11) + b;e = RL(e, 10);
/* 249 */     b = RL(b + f1(c, d, e) + this.X[9], 13) + a;d = RL(d, 10);
/* 250 */     a = RL(a + f1(b, c, d) + this.X[10], 14) + e;c = RL(c, 10);
/* 251 */     e = RL(e + f1(a, b, c) + this.X[11], 15) + d;b = RL(b, 10);
/* 252 */     d = RL(d + f1(e, a, b) + this.X[12], 6) + c;a = RL(a, 10);
/* 253 */     c = RL(c + f1(d, e, a) + this.X[13], 7) + b;e = RL(e, 10);
/* 254 */     b = RL(b + f1(c, d, e) + this.X[14], 9) + a;d = RL(d, 10);
/* 255 */     a = RL(a + f1(b, c, d) + this.X[15], 8) + e;c = RL(c, 10);
/*     */     
/*     */ 
/* 258 */     aa = RL(aa + f5(bb, cc, dd) + this.X[5] + 1352829926, 8) + ee;cc = RL(cc, 10);
/* 259 */     ee = RL(ee + f5(aa, bb, cc) + this.X[14] + 1352829926, 9) + dd;bb = RL(bb, 10);
/* 260 */     dd = RL(dd + f5(ee, aa, bb) + this.X[7] + 1352829926, 9) + cc;aa = RL(aa, 10);
/* 261 */     cc = RL(cc + f5(dd, ee, aa) + this.X[0] + 1352829926, 11) + bb;ee = RL(ee, 10);
/* 262 */     bb = RL(bb + f5(cc, dd, ee) + this.X[9] + 1352829926, 13) + aa;dd = RL(dd, 10);
/* 263 */     aa = RL(aa + f5(bb, cc, dd) + this.X[2] + 1352829926, 15) + ee;cc = RL(cc, 10);
/* 264 */     ee = RL(ee + f5(aa, bb, cc) + this.X[11] + 1352829926, 15) + dd;bb = RL(bb, 10);
/* 265 */     dd = RL(dd + f5(ee, aa, bb) + this.X[4] + 1352829926, 5) + cc;aa = RL(aa, 10);
/* 266 */     cc = RL(cc + f5(dd, ee, aa) + this.X[13] + 1352829926, 7) + bb;ee = RL(ee, 10);
/* 267 */     bb = RL(bb + f5(cc, dd, ee) + this.X[6] + 1352829926, 7) + aa;dd = RL(dd, 10);
/* 268 */     aa = RL(aa + f5(bb, cc, dd) + this.X[15] + 1352829926, 8) + ee;cc = RL(cc, 10);
/* 269 */     ee = RL(ee + f5(aa, bb, cc) + this.X[8] + 1352829926, 11) + dd;bb = RL(bb, 10);
/* 270 */     dd = RL(dd + f5(ee, aa, bb) + this.X[1] + 1352829926, 14) + cc;aa = RL(aa, 10);
/* 271 */     cc = RL(cc + f5(dd, ee, aa) + this.X[10] + 1352829926, 14) + bb;ee = RL(ee, 10);
/* 272 */     bb = RL(bb + f5(cc, dd, ee) + this.X[3] + 1352829926, 12) + aa;dd = RL(dd, 10);
/* 273 */     aa = RL(aa + f5(bb, cc, dd) + this.X[12] + 1352829926, 6) + ee;cc = RL(cc, 10);
/*     */     
/* 275 */     int t = a;a = aa;aa = t;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 281 */     e = RL(e + f2(a, b, c) + this.X[7] + 1518500249, 7) + d;b = RL(b, 10);
/* 282 */     d = RL(d + f2(e, a, b) + this.X[4] + 1518500249, 6) + c;a = RL(a, 10);
/* 283 */     c = RL(c + f2(d, e, a) + this.X[13] + 1518500249, 8) + b;e = RL(e, 10);
/* 284 */     b = RL(b + f2(c, d, e) + this.X[1] + 1518500249, 13) + a;d = RL(d, 10);
/* 285 */     a = RL(a + f2(b, c, d) + this.X[10] + 1518500249, 11) + e;c = RL(c, 10);
/* 286 */     e = RL(e + f2(a, b, c) + this.X[6] + 1518500249, 9) + d;b = RL(b, 10);
/* 287 */     d = RL(d + f2(e, a, b) + this.X[15] + 1518500249, 7) + c;a = RL(a, 10);
/* 288 */     c = RL(c + f2(d, e, a) + this.X[3] + 1518500249, 15) + b;e = RL(e, 10);
/* 289 */     b = RL(b + f2(c, d, e) + this.X[12] + 1518500249, 7) + a;d = RL(d, 10);
/* 290 */     a = RL(a + f2(b, c, d) + this.X[0] + 1518500249, 12) + e;c = RL(c, 10);
/* 291 */     e = RL(e + f2(a, b, c) + this.X[9] + 1518500249, 15) + d;b = RL(b, 10);
/* 292 */     d = RL(d + f2(e, a, b) + this.X[5] + 1518500249, 9) + c;a = RL(a, 10);
/* 293 */     c = RL(c + f2(d, e, a) + this.X[2] + 1518500249, 11) + b;e = RL(e, 10);
/* 294 */     b = RL(b + f2(c, d, e) + this.X[14] + 1518500249, 7) + a;d = RL(d, 10);
/* 295 */     a = RL(a + f2(b, c, d) + this.X[11] + 1518500249, 13) + e;c = RL(c, 10);
/* 296 */     e = RL(e + f2(a, b, c) + this.X[8] + 1518500249, 12) + d;b = RL(b, 10);
/*     */     
/*     */ 
/* 299 */     ee = RL(ee + f4(aa, bb, cc) + this.X[6] + 1548603684, 9) + dd;bb = RL(bb, 10);
/* 300 */     dd = RL(dd + f4(ee, aa, bb) + this.X[11] + 1548603684, 13) + cc;aa = RL(aa, 10);
/* 301 */     cc = RL(cc + f4(dd, ee, aa) + this.X[3] + 1548603684, 15) + bb;ee = RL(ee, 10);
/* 302 */     bb = RL(bb + f4(cc, dd, ee) + this.X[7] + 1548603684, 7) + aa;dd = RL(dd, 10);
/* 303 */     aa = RL(aa + f4(bb, cc, dd) + this.X[0] + 1548603684, 12) + ee;cc = RL(cc, 10);
/* 304 */     ee = RL(ee + f4(aa, bb, cc) + this.X[13] + 1548603684, 8) + dd;bb = RL(bb, 10);
/* 305 */     dd = RL(dd + f4(ee, aa, bb) + this.X[5] + 1548603684, 9) + cc;aa = RL(aa, 10);
/* 306 */     cc = RL(cc + f4(dd, ee, aa) + this.X[10] + 1548603684, 11) + bb;ee = RL(ee, 10);
/* 307 */     bb = RL(bb + f4(cc, dd, ee) + this.X[14] + 1548603684, 7) + aa;dd = RL(dd, 10);
/* 308 */     aa = RL(aa + f4(bb, cc, dd) + this.X[15] + 1548603684, 7) + ee;cc = RL(cc, 10);
/* 309 */     ee = RL(ee + f4(aa, bb, cc) + this.X[8] + 1548603684, 12) + dd;bb = RL(bb, 10);
/* 310 */     dd = RL(dd + f4(ee, aa, bb) + this.X[12] + 1548603684, 7) + cc;aa = RL(aa, 10);
/* 311 */     cc = RL(cc + f4(dd, ee, aa) + this.X[4] + 1548603684, 6) + bb;ee = RL(ee, 10);
/* 312 */     bb = RL(bb + f4(cc, dd, ee) + this.X[9] + 1548603684, 15) + aa;dd = RL(dd, 10);
/* 313 */     aa = RL(aa + f4(bb, cc, dd) + this.X[1] + 1548603684, 13) + ee;cc = RL(cc, 10);
/* 314 */     ee = RL(ee + f4(aa, bb, cc) + this.X[2] + 1548603684, 11) + dd;bb = RL(bb, 10);
/*     */     
/* 316 */     t = b;b = bb;bb = t;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 322 */     d = RL(d + f3(e, a, b) + this.X[3] + 1859775393, 11) + c;a = RL(a, 10);
/* 323 */     c = RL(c + f3(d, e, a) + this.X[10] + 1859775393, 13) + b;e = RL(e, 10);
/* 324 */     b = RL(b + f3(c, d, e) + this.X[14] + 1859775393, 6) + a;d = RL(d, 10);
/* 325 */     a = RL(a + f3(b, c, d) + this.X[4] + 1859775393, 7) + e;c = RL(c, 10);
/* 326 */     e = RL(e + f3(a, b, c) + this.X[9] + 1859775393, 14) + d;b = RL(b, 10);
/* 327 */     d = RL(d + f3(e, a, b) + this.X[15] + 1859775393, 9) + c;a = RL(a, 10);
/* 328 */     c = RL(c + f3(d, e, a) + this.X[8] + 1859775393, 13) + b;e = RL(e, 10);
/* 329 */     b = RL(b + f3(c, d, e) + this.X[1] + 1859775393, 15) + a;d = RL(d, 10);
/* 330 */     a = RL(a + f3(b, c, d) + this.X[2] + 1859775393, 14) + e;c = RL(c, 10);
/* 331 */     e = RL(e + f3(a, b, c) + this.X[7] + 1859775393, 8) + d;b = RL(b, 10);
/* 332 */     d = RL(d + f3(e, a, b) + this.X[0] + 1859775393, 13) + c;a = RL(a, 10);
/* 333 */     c = RL(c + f3(d, e, a) + this.X[6] + 1859775393, 6) + b;e = RL(e, 10);
/* 334 */     b = RL(b + f3(c, d, e) + this.X[13] + 1859775393, 5) + a;d = RL(d, 10);
/* 335 */     a = RL(a + f3(b, c, d) + this.X[11] + 1859775393, 12) + e;c = RL(c, 10);
/* 336 */     e = RL(e + f3(a, b, c) + this.X[5] + 1859775393, 7) + d;b = RL(b, 10);
/* 337 */     d = RL(d + f3(e, a, b) + this.X[12] + 1859775393, 5) + c;a = RL(a, 10);
/*     */     
/*     */ 
/* 340 */     dd = RL(dd + f3(ee, aa, bb) + this.X[15] + 1836072691, 9) + cc;aa = RL(aa, 10);
/* 341 */     cc = RL(cc + f3(dd, ee, aa) + this.X[5] + 1836072691, 7) + bb;ee = RL(ee, 10);
/* 342 */     bb = RL(bb + f3(cc, dd, ee) + this.X[1] + 1836072691, 15) + aa;dd = RL(dd, 10);
/* 343 */     aa = RL(aa + f3(bb, cc, dd) + this.X[3] + 1836072691, 11) + ee;cc = RL(cc, 10);
/* 344 */     ee = RL(ee + f3(aa, bb, cc) + this.X[7] + 1836072691, 8) + dd;bb = RL(bb, 10);
/* 345 */     dd = RL(dd + f3(ee, aa, bb) + this.X[14] + 1836072691, 6) + cc;aa = RL(aa, 10);
/* 346 */     cc = RL(cc + f3(dd, ee, aa) + this.X[6] + 1836072691, 6) + bb;ee = RL(ee, 10);
/* 347 */     bb = RL(bb + f3(cc, dd, ee) + this.X[9] + 1836072691, 14) + aa;dd = RL(dd, 10);
/* 348 */     aa = RL(aa + f3(bb, cc, dd) + this.X[11] + 1836072691, 12) + ee;cc = RL(cc, 10);
/* 349 */     ee = RL(ee + f3(aa, bb, cc) + this.X[8] + 1836072691, 13) + dd;bb = RL(bb, 10);
/* 350 */     dd = RL(dd + f3(ee, aa, bb) + this.X[12] + 1836072691, 5) + cc;aa = RL(aa, 10);
/* 351 */     cc = RL(cc + f3(dd, ee, aa) + this.X[2] + 1836072691, 14) + bb;ee = RL(ee, 10);
/* 352 */     bb = RL(bb + f3(cc, dd, ee) + this.X[10] + 1836072691, 13) + aa;dd = RL(dd, 10);
/* 353 */     aa = RL(aa + f3(bb, cc, dd) + this.X[0] + 1836072691, 13) + ee;cc = RL(cc, 10);
/* 354 */     ee = RL(ee + f3(aa, bb, cc) + this.X[4] + 1836072691, 7) + dd;bb = RL(bb, 10);
/* 355 */     dd = RL(dd + f3(ee, aa, bb) + this.X[13] + 1836072691, 5) + cc;aa = RL(aa, 10);
/*     */     
/* 357 */     t = c;c = cc;cc = t;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 363 */     c = RL(c + f4(d, e, a) + this.X[1] + -1894007588, 11) + b;e = RL(e, 10);
/* 364 */     b = RL(b + f4(c, d, e) + this.X[9] + -1894007588, 12) + a;d = RL(d, 10);
/* 365 */     a = RL(a + f4(b, c, d) + this.X[11] + -1894007588, 14) + e;c = RL(c, 10);
/* 366 */     e = RL(e + f4(a, b, c) + this.X[10] + -1894007588, 15) + d;b = RL(b, 10);
/* 367 */     d = RL(d + f4(e, a, b) + this.X[0] + -1894007588, 14) + c;a = RL(a, 10);
/* 368 */     c = RL(c + f4(d, e, a) + this.X[8] + -1894007588, 15) + b;e = RL(e, 10);
/* 369 */     b = RL(b + f4(c, d, e) + this.X[12] + -1894007588, 9) + a;d = RL(d, 10);
/* 370 */     a = RL(a + f4(b, c, d) + this.X[4] + -1894007588, 8) + e;c = RL(c, 10);
/* 371 */     e = RL(e + f4(a, b, c) + this.X[13] + -1894007588, 9) + d;b = RL(b, 10);
/* 372 */     d = RL(d + f4(e, a, b) + this.X[3] + -1894007588, 14) + c;a = RL(a, 10);
/* 373 */     c = RL(c + f4(d, e, a) + this.X[7] + -1894007588, 5) + b;e = RL(e, 10);
/* 374 */     b = RL(b + f4(c, d, e) + this.X[15] + -1894007588, 6) + a;d = RL(d, 10);
/* 375 */     a = RL(a + f4(b, c, d) + this.X[14] + -1894007588, 8) + e;c = RL(c, 10);
/* 376 */     e = RL(e + f4(a, b, c) + this.X[5] + -1894007588, 6) + d;b = RL(b, 10);
/* 377 */     d = RL(d + f4(e, a, b) + this.X[6] + -1894007588, 5) + c;a = RL(a, 10);
/* 378 */     c = RL(c + f4(d, e, a) + this.X[2] + -1894007588, 12) + b;e = RL(e, 10);
/*     */     
/*     */ 
/* 381 */     cc = RL(cc + f2(dd, ee, aa) + this.X[8] + 2053994217, 15) + bb;ee = RL(ee, 10);
/* 382 */     bb = RL(bb + f2(cc, dd, ee) + this.X[6] + 2053994217, 5) + aa;dd = RL(dd, 10);
/* 383 */     aa = RL(aa + f2(bb, cc, dd) + this.X[4] + 2053994217, 8) + ee;cc = RL(cc, 10);
/* 384 */     ee = RL(ee + f2(aa, bb, cc) + this.X[1] + 2053994217, 11) + dd;bb = RL(bb, 10);
/* 385 */     dd = RL(dd + f2(ee, aa, bb) + this.X[3] + 2053994217, 14) + cc;aa = RL(aa, 10);
/* 386 */     cc = RL(cc + f2(dd, ee, aa) + this.X[11] + 2053994217, 14) + bb;ee = RL(ee, 10);
/* 387 */     bb = RL(bb + f2(cc, dd, ee) + this.X[15] + 2053994217, 6) + aa;dd = RL(dd, 10);
/* 388 */     aa = RL(aa + f2(bb, cc, dd) + this.X[0] + 2053994217, 14) + ee;cc = RL(cc, 10);
/* 389 */     ee = RL(ee + f2(aa, bb, cc) + this.X[5] + 2053994217, 6) + dd;bb = RL(bb, 10);
/* 390 */     dd = RL(dd + f2(ee, aa, bb) + this.X[12] + 2053994217, 9) + cc;aa = RL(aa, 10);
/* 391 */     cc = RL(cc + f2(dd, ee, aa) + this.X[2] + 2053994217, 12) + bb;ee = RL(ee, 10);
/* 392 */     bb = RL(bb + f2(cc, dd, ee) + this.X[13] + 2053994217, 9) + aa;dd = RL(dd, 10);
/* 393 */     aa = RL(aa + f2(bb, cc, dd) + this.X[9] + 2053994217, 12) + ee;cc = RL(cc, 10);
/* 394 */     ee = RL(ee + f2(aa, bb, cc) + this.X[7] + 2053994217, 5) + dd;bb = RL(bb, 10);
/* 395 */     dd = RL(dd + f2(ee, aa, bb) + this.X[10] + 2053994217, 15) + cc;aa = RL(aa, 10);
/* 396 */     cc = RL(cc + f2(dd, ee, aa) + this.X[14] + 2053994217, 8) + bb;ee = RL(ee, 10);
/*     */     
/* 398 */     t = d;d = dd;dd = t;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 404 */     b = RL(b + f5(c, d, e) + this.X[4] + -1454113458, 9) + a;d = RL(d, 10);
/* 405 */     a = RL(a + f5(b, c, d) + this.X[0] + -1454113458, 15) + e;c = RL(c, 10);
/* 406 */     e = RL(e + f5(a, b, c) + this.X[5] + -1454113458, 5) + d;b = RL(b, 10);
/* 407 */     d = RL(d + f5(e, a, b) + this.X[9] + -1454113458, 11) + c;a = RL(a, 10);
/* 408 */     c = RL(c + f5(d, e, a) + this.X[7] + -1454113458, 6) + b;e = RL(e, 10);
/* 409 */     b = RL(b + f5(c, d, e) + this.X[12] + -1454113458, 8) + a;d = RL(d, 10);
/* 410 */     a = RL(a + f5(b, c, d) + this.X[2] + -1454113458, 13) + e;c = RL(c, 10);
/* 411 */     e = RL(e + f5(a, b, c) + this.X[10] + -1454113458, 12) + d;b = RL(b, 10);
/* 412 */     d = RL(d + f5(e, a, b) + this.X[14] + -1454113458, 5) + c;a = RL(a, 10);
/* 413 */     c = RL(c + f5(d, e, a) + this.X[1] + -1454113458, 12) + b;e = RL(e, 10);
/* 414 */     b = RL(b + f5(c, d, e) + this.X[3] + -1454113458, 13) + a;d = RL(d, 10);
/* 415 */     a = RL(a + f5(b, c, d) + this.X[8] + -1454113458, 14) + e;c = RL(c, 10);
/* 416 */     e = RL(e + f5(a, b, c) + this.X[11] + -1454113458, 11) + d;b = RL(b, 10);
/* 417 */     d = RL(d + f5(e, a, b) + this.X[6] + -1454113458, 8) + c;a = RL(a, 10);
/* 418 */     c = RL(c + f5(d, e, a) + this.X[15] + -1454113458, 5) + b;e = RL(e, 10);
/* 419 */     b = RL(b + f5(c, d, e) + this.X[13] + -1454113458, 6) + a;d = RL(d, 10);
/*     */     
/*     */ 
/* 422 */     bb = RL(bb + f1(cc, dd, ee) + this.X[12], 8) + aa;dd = RL(dd, 10);
/* 423 */     aa = RL(aa + f1(bb, cc, dd) + this.X[15], 5) + ee;cc = RL(cc, 10);
/* 424 */     ee = RL(ee + f1(aa, bb, cc) + this.X[10], 12) + dd;bb = RL(bb, 10);
/* 425 */     dd = RL(dd + f1(ee, aa, bb) + this.X[4], 9) + cc;aa = RL(aa, 10);
/* 426 */     cc = RL(cc + f1(dd, ee, aa) + this.X[1], 12) + bb;ee = RL(ee, 10);
/* 427 */     bb = RL(bb + f1(cc, dd, ee) + this.X[5], 5) + aa;dd = RL(dd, 10);
/* 428 */     aa = RL(aa + f1(bb, cc, dd) + this.X[8], 14) + ee;cc = RL(cc, 10);
/* 429 */     ee = RL(ee + f1(aa, bb, cc) + this.X[7], 6) + dd;bb = RL(bb, 10);
/* 430 */     dd = RL(dd + f1(ee, aa, bb) + this.X[6], 8) + cc;aa = RL(aa, 10);
/* 431 */     cc = RL(cc + f1(dd, ee, aa) + this.X[2], 13) + bb;ee = RL(ee, 10);
/* 432 */     bb = RL(bb + f1(cc, dd, ee) + this.X[13], 6) + aa;dd = RL(dd, 10);
/* 433 */     aa = RL(aa + f1(bb, cc, dd) + this.X[14], 5) + ee;cc = RL(cc, 10);
/* 434 */     ee = RL(ee + f1(aa, bb, cc) + this.X[0], 15) + dd;bb = RL(bb, 10);
/* 435 */     dd = RL(dd + f1(ee, aa, bb) + this.X[3], 13) + cc;aa = RL(aa, 10);
/* 436 */     cc = RL(cc + f1(dd, ee, aa) + this.X[9], 11) + bb;ee = RL(ee, 10);
/* 437 */     bb = RL(bb + f1(cc, dd, ee) + this.X[11], 11) + aa;dd = RL(dd, 10);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 443 */     this.H0 += a;
/* 444 */     this.H1 += b;
/* 445 */     this.H2 += c;
/* 446 */     this.H3 += d;
/* 447 */     this.H4 += ee;
/* 448 */     this.H5 += aa;
/* 449 */     this.H6 += bb;
/* 450 */     this.H7 += cc;
/* 451 */     this.H8 += dd;
/* 452 */     this.H9 += e;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 457 */     this.xOff = 0;
/* 458 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 460 */       this.X[i] = 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/RIPEMD320Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */