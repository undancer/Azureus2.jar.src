/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ 
/*     */ public class RIPEMD256Digest
/*     */   extends GeneralDigest
/*     */ {
/*     */   private static final int DIGEST_LENGTH = 32;
/*     */   
/*     */   private int H0;
/*     */   
/*     */   private int H1;
/*     */   private int H2;
/*     */   private int H3;
/*     */   private int H4;
/*     */   private int H5;
/*     */   private int H6;
/*     */   private int H7;
/*  18 */   private int[] X = new int[16];
/*     */   
/*     */ 
/*     */   private int xOff;
/*     */   
/*     */ 
/*     */   public RIPEMD256Digest()
/*     */   {
/*  26 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RIPEMD256Digest(RIPEMD256Digest t)
/*     */   {
/*  35 */     super(t);
/*     */     
/*  37 */     this.H0 = t.H0;
/*  38 */     this.H1 = t.H1;
/*  39 */     this.H2 = t.H2;
/*  40 */     this.H3 = t.H3;
/*  41 */     this.H4 = t.H4;
/*  42 */     this.H5 = t.H5;
/*  43 */     this.H6 = t.H6;
/*  44 */     this.H7 = t.H7;
/*     */     
/*  46 */     System.arraycopy(t.X, 0, this.X, 0, t.X.length);
/*  47 */     this.xOff = t.xOff;
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  52 */     return "RIPEMD256";
/*     */   }
/*     */   
/*     */   public int getDigestSize()
/*     */   {
/*  57 */     return 32;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void processWord(byte[] in, int inOff)
/*     */   {
/*  64 */     this.X[(this.xOff++)] = (in[inOff] & 0xFF | (in[(inOff + 1)] & 0xFF) << 8 | (in[(inOff + 2)] & 0xFF) << 16 | (in[(inOff + 3)] & 0xFF) << 24);
/*     */     
/*     */ 
/*  67 */     if (this.xOff == 16)
/*     */     {
/*  69 */       processBlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void processLength(long bitLength)
/*     */   {
/*  76 */     if (this.xOff > 14)
/*     */     {
/*  78 */       processBlock();
/*     */     }
/*     */     
/*  81 */     this.X[14] = ((int)(bitLength & 0xFFFFFFFFFFFFFFFF));
/*  82 */     this.X[15] = ((int)(bitLength >>> 32));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void unpackWord(int word, byte[] out, int outOff)
/*     */   {
/*  90 */     out[outOff] = ((byte)word);
/*  91 */     out[(outOff + 1)] = ((byte)(word >>> 8));
/*  92 */     out[(outOff + 2)] = ((byte)(word >>> 16));
/*  93 */     out[(outOff + 3)] = ((byte)(word >>> 24));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int doFinal(byte[] out, int outOff)
/*     */   {
/* 100 */     finish();
/*     */     
/* 102 */     unpackWord(this.H0, out, outOff);
/* 103 */     unpackWord(this.H1, out, outOff + 4);
/* 104 */     unpackWord(this.H2, out, outOff + 8);
/* 105 */     unpackWord(this.H3, out, outOff + 12);
/* 106 */     unpackWord(this.H4, out, outOff + 16);
/* 107 */     unpackWord(this.H5, out, outOff + 20);
/* 108 */     unpackWord(this.H6, out, outOff + 24);
/* 109 */     unpackWord(this.H7, out, outOff + 28);
/*     */     
/* 111 */     reset();
/*     */     
/* 113 */     return 32;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 121 */     super.reset();
/*     */     
/* 123 */     this.H0 = 1732584193;
/* 124 */     this.H1 = -271733879;
/* 125 */     this.H2 = -1732584194;
/* 126 */     this.H3 = 271733878;
/* 127 */     this.H4 = 1985229328;
/* 128 */     this.H5 = -19088744;
/* 129 */     this.H6 = -1985229329;
/* 130 */     this.H7 = 19088743;
/*     */     
/* 132 */     this.xOff = 0;
/*     */     
/* 134 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 136 */       this.X[i] = 0;
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
/* 147 */     return x << n | x >>> 32 - n;
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
/* 162 */     return x ^ y ^ z;
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
/* 173 */     return x & y | (x ^ 0xFFFFFFFF) & z;
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
/* 184 */     return (x | y ^ 0xFFFFFFFF) ^ z;
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
/* 195 */     return x & z | y & (z ^ 0xFFFFFFFF);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int F1(int a, int b, int c, int d, int x, int s)
/*     */   {
/* 206 */     return RL(a + f1(b, c, d) + x, s);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int F2(int a, int b, int c, int d, int x, int s)
/*     */   {
/* 217 */     return RL(a + f2(b, c, d) + x + 1518500249, s);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int F3(int a, int b, int c, int d, int x, int s)
/*     */   {
/* 228 */     return RL(a + f3(b, c, d) + x + 1859775393, s);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int F4(int a, int b, int c, int d, int x, int s)
/*     */   {
/* 239 */     return RL(a + f4(b, c, d) + x + -1894007588, s);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int FF1(int a, int b, int c, int d, int x, int s)
/*     */   {
/* 250 */     return RL(a + f1(b, c, d) + x, s);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int FF2(int a, int b, int c, int d, int x, int s)
/*     */   {
/* 261 */     return RL(a + f2(b, c, d) + x + 1836072691, s);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int FF3(int a, int b, int c, int d, int x, int s)
/*     */   {
/* 272 */     return RL(a + f3(b, c, d) + x + 1548603684, s);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int FF4(int a, int b, int c, int d, int x, int s)
/*     */   {
/* 283 */     return RL(a + f4(b, c, d) + x + 1352829926, s);
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
/* 294 */     int a = this.H0;
/* 295 */     int b = this.H1;
/* 296 */     int c = this.H2;
/* 297 */     int d = this.H3;
/* 298 */     int aa = this.H4;
/* 299 */     int bb = this.H5;
/* 300 */     int cc = this.H6;
/* 301 */     int dd = this.H7;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 307 */     a = F1(a, b, c, d, this.X[0], 11);
/* 308 */     d = F1(d, a, b, c, this.X[1], 14);
/* 309 */     c = F1(c, d, a, b, this.X[2], 15);
/* 310 */     b = F1(b, c, d, a, this.X[3], 12);
/* 311 */     a = F1(a, b, c, d, this.X[4], 5);
/* 312 */     d = F1(d, a, b, c, this.X[5], 8);
/* 313 */     c = F1(c, d, a, b, this.X[6], 7);
/* 314 */     b = F1(b, c, d, a, this.X[7], 9);
/* 315 */     a = F1(a, b, c, d, this.X[8], 11);
/* 316 */     d = F1(d, a, b, c, this.X[9], 13);
/* 317 */     c = F1(c, d, a, b, this.X[10], 14);
/* 318 */     b = F1(b, c, d, a, this.X[11], 15);
/* 319 */     a = F1(a, b, c, d, this.X[12], 6);
/* 320 */     d = F1(d, a, b, c, this.X[13], 7);
/* 321 */     c = F1(c, d, a, b, this.X[14], 9);
/* 322 */     b = F1(b, c, d, a, this.X[15], 8);
/*     */     
/* 324 */     aa = FF4(aa, bb, cc, dd, this.X[5], 8);
/* 325 */     dd = FF4(dd, aa, bb, cc, this.X[14], 9);
/* 326 */     cc = FF4(cc, dd, aa, bb, this.X[7], 9);
/* 327 */     bb = FF4(bb, cc, dd, aa, this.X[0], 11);
/* 328 */     aa = FF4(aa, bb, cc, dd, this.X[9], 13);
/* 329 */     dd = FF4(dd, aa, bb, cc, this.X[2], 15);
/* 330 */     cc = FF4(cc, dd, aa, bb, this.X[11], 15);
/* 331 */     bb = FF4(bb, cc, dd, aa, this.X[4], 5);
/* 332 */     aa = FF4(aa, bb, cc, dd, this.X[13], 7);
/* 333 */     dd = FF4(dd, aa, bb, cc, this.X[6], 7);
/* 334 */     cc = FF4(cc, dd, aa, bb, this.X[15], 8);
/* 335 */     bb = FF4(bb, cc, dd, aa, this.X[8], 11);
/* 336 */     aa = FF4(aa, bb, cc, dd, this.X[1], 14);
/* 337 */     dd = FF4(dd, aa, bb, cc, this.X[10], 14);
/* 338 */     cc = FF4(cc, dd, aa, bb, this.X[3], 12);
/* 339 */     bb = FF4(bb, cc, dd, aa, this.X[12], 6);
/*     */     
/* 341 */     int t = a;a = aa;aa = t;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 346 */     a = F2(a, b, c, d, this.X[7], 7);
/* 347 */     d = F2(d, a, b, c, this.X[4], 6);
/* 348 */     c = F2(c, d, a, b, this.X[13], 8);
/* 349 */     b = F2(b, c, d, a, this.X[1], 13);
/* 350 */     a = F2(a, b, c, d, this.X[10], 11);
/* 351 */     d = F2(d, a, b, c, this.X[6], 9);
/* 352 */     c = F2(c, d, a, b, this.X[15], 7);
/* 353 */     b = F2(b, c, d, a, this.X[3], 15);
/* 354 */     a = F2(a, b, c, d, this.X[12], 7);
/* 355 */     d = F2(d, a, b, c, this.X[0], 12);
/* 356 */     c = F2(c, d, a, b, this.X[9], 15);
/* 357 */     b = F2(b, c, d, a, this.X[5], 9);
/* 358 */     a = F2(a, b, c, d, this.X[2], 11);
/* 359 */     d = F2(d, a, b, c, this.X[14], 7);
/* 360 */     c = F2(c, d, a, b, this.X[11], 13);
/* 361 */     b = F2(b, c, d, a, this.X[8], 12);
/*     */     
/* 363 */     aa = FF3(aa, bb, cc, dd, this.X[6], 9);
/* 364 */     dd = FF3(dd, aa, bb, cc, this.X[11], 13);
/* 365 */     cc = FF3(cc, dd, aa, bb, this.X[3], 15);
/* 366 */     bb = FF3(bb, cc, dd, aa, this.X[7], 7);
/* 367 */     aa = FF3(aa, bb, cc, dd, this.X[0], 12);
/* 368 */     dd = FF3(dd, aa, bb, cc, this.X[13], 8);
/* 369 */     cc = FF3(cc, dd, aa, bb, this.X[5], 9);
/* 370 */     bb = FF3(bb, cc, dd, aa, this.X[10], 11);
/* 371 */     aa = FF3(aa, bb, cc, dd, this.X[14], 7);
/* 372 */     dd = FF3(dd, aa, bb, cc, this.X[15], 7);
/* 373 */     cc = FF3(cc, dd, aa, bb, this.X[8], 12);
/* 374 */     bb = FF3(bb, cc, dd, aa, this.X[12], 7);
/* 375 */     aa = FF3(aa, bb, cc, dd, this.X[4], 6);
/* 376 */     dd = FF3(dd, aa, bb, cc, this.X[9], 15);
/* 377 */     cc = FF3(cc, dd, aa, bb, this.X[1], 13);
/* 378 */     bb = FF3(bb, cc, dd, aa, this.X[2], 11);
/*     */     
/* 380 */     t = b;b = bb;bb = t;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 385 */     a = F3(a, b, c, d, this.X[3], 11);
/* 386 */     d = F3(d, a, b, c, this.X[10], 13);
/* 387 */     c = F3(c, d, a, b, this.X[14], 6);
/* 388 */     b = F3(b, c, d, a, this.X[4], 7);
/* 389 */     a = F3(a, b, c, d, this.X[9], 14);
/* 390 */     d = F3(d, a, b, c, this.X[15], 9);
/* 391 */     c = F3(c, d, a, b, this.X[8], 13);
/* 392 */     b = F3(b, c, d, a, this.X[1], 15);
/* 393 */     a = F3(a, b, c, d, this.X[2], 14);
/* 394 */     d = F3(d, a, b, c, this.X[7], 8);
/* 395 */     c = F3(c, d, a, b, this.X[0], 13);
/* 396 */     b = F3(b, c, d, a, this.X[6], 6);
/* 397 */     a = F3(a, b, c, d, this.X[13], 5);
/* 398 */     d = F3(d, a, b, c, this.X[11], 12);
/* 399 */     c = F3(c, d, a, b, this.X[5], 7);
/* 400 */     b = F3(b, c, d, a, this.X[12], 5);
/*     */     
/* 402 */     aa = FF2(aa, bb, cc, dd, this.X[15], 9);
/* 403 */     dd = FF2(dd, aa, bb, cc, this.X[5], 7);
/* 404 */     cc = FF2(cc, dd, aa, bb, this.X[1], 15);
/* 405 */     bb = FF2(bb, cc, dd, aa, this.X[3], 11);
/* 406 */     aa = FF2(aa, bb, cc, dd, this.X[7], 8);
/* 407 */     dd = FF2(dd, aa, bb, cc, this.X[14], 6);
/* 408 */     cc = FF2(cc, dd, aa, bb, this.X[6], 6);
/* 409 */     bb = FF2(bb, cc, dd, aa, this.X[9], 14);
/* 410 */     aa = FF2(aa, bb, cc, dd, this.X[11], 12);
/* 411 */     dd = FF2(dd, aa, bb, cc, this.X[8], 13);
/* 412 */     cc = FF2(cc, dd, aa, bb, this.X[12], 5);
/* 413 */     bb = FF2(bb, cc, dd, aa, this.X[2], 14);
/* 414 */     aa = FF2(aa, bb, cc, dd, this.X[10], 13);
/* 415 */     dd = FF2(dd, aa, bb, cc, this.X[0], 13);
/* 416 */     cc = FF2(cc, dd, aa, bb, this.X[4], 7);
/* 417 */     bb = FF2(bb, cc, dd, aa, this.X[13], 5);
/*     */     
/* 419 */     t = c;c = cc;cc = t;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 424 */     a = F4(a, b, c, d, this.X[1], 11);
/* 425 */     d = F4(d, a, b, c, this.X[9], 12);
/* 426 */     c = F4(c, d, a, b, this.X[11], 14);
/* 427 */     b = F4(b, c, d, a, this.X[10], 15);
/* 428 */     a = F4(a, b, c, d, this.X[0], 14);
/* 429 */     d = F4(d, a, b, c, this.X[8], 15);
/* 430 */     c = F4(c, d, a, b, this.X[12], 9);
/* 431 */     b = F4(b, c, d, a, this.X[4], 8);
/* 432 */     a = F4(a, b, c, d, this.X[13], 9);
/* 433 */     d = F4(d, a, b, c, this.X[3], 14);
/* 434 */     c = F4(c, d, a, b, this.X[7], 5);
/* 435 */     b = F4(b, c, d, a, this.X[15], 6);
/* 436 */     a = F4(a, b, c, d, this.X[14], 8);
/* 437 */     d = F4(d, a, b, c, this.X[5], 6);
/* 438 */     c = F4(c, d, a, b, this.X[6], 5);
/* 439 */     b = F4(b, c, d, a, this.X[2], 12);
/*     */     
/* 441 */     aa = FF1(aa, bb, cc, dd, this.X[8], 15);
/* 442 */     dd = FF1(dd, aa, bb, cc, this.X[6], 5);
/* 443 */     cc = FF1(cc, dd, aa, bb, this.X[4], 8);
/* 444 */     bb = FF1(bb, cc, dd, aa, this.X[1], 11);
/* 445 */     aa = FF1(aa, bb, cc, dd, this.X[3], 14);
/* 446 */     dd = FF1(dd, aa, bb, cc, this.X[11], 14);
/* 447 */     cc = FF1(cc, dd, aa, bb, this.X[15], 6);
/* 448 */     bb = FF1(bb, cc, dd, aa, this.X[0], 14);
/* 449 */     aa = FF1(aa, bb, cc, dd, this.X[5], 6);
/* 450 */     dd = FF1(dd, aa, bb, cc, this.X[12], 9);
/* 451 */     cc = FF1(cc, dd, aa, bb, this.X[2], 12);
/* 452 */     bb = FF1(bb, cc, dd, aa, this.X[13], 9);
/* 453 */     aa = FF1(aa, bb, cc, dd, this.X[9], 12);
/* 454 */     dd = FF1(dd, aa, bb, cc, this.X[7], 5);
/* 455 */     cc = FF1(cc, dd, aa, bb, this.X[10], 15);
/* 456 */     bb = FF1(bb, cc, dd, aa, this.X[14], 8);
/*     */     
/* 458 */     t = d;d = dd;dd = t;
/*     */     
/* 460 */     this.H0 += a;
/* 461 */     this.H1 += b;
/* 462 */     this.H2 += c;
/* 463 */     this.H3 += d;
/* 464 */     this.H4 += aa;
/* 465 */     this.H5 += bb;
/* 466 */     this.H6 += cc;
/* 467 */     this.H7 += dd;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 472 */     this.xOff = 0;
/* 473 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 475 */       this.X[i] = 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/RIPEMD256Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */