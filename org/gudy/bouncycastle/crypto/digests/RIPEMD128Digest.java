/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ 
/*     */ public class RIPEMD128Digest
/*     */   extends GeneralDigest
/*     */ {
/*     */   private static final int DIGEST_LENGTH = 16;
/*     */   
/*     */   private int H0;
/*     */   
/*     */   private int H1;
/*     */   
/*     */   private int H2;
/*     */   
/*     */   private int H3;
/*     */   
/*  17 */   private int[] X = new int[16];
/*     */   
/*     */ 
/*     */   private int xOff;
/*     */   
/*     */ 
/*     */   public RIPEMD128Digest()
/*     */   {
/*  25 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RIPEMD128Digest(RIPEMD128Digest t)
/*     */   {
/*  34 */     super(t);
/*     */     
/*  36 */     this.H0 = t.H0;
/*  37 */     this.H1 = t.H1;
/*  38 */     this.H2 = t.H2;
/*  39 */     this.H3 = t.H3;
/*     */     
/*  41 */     System.arraycopy(t.X, 0, this.X, 0, t.X.length);
/*  42 */     this.xOff = t.xOff;
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  47 */     return "RIPEMD128";
/*     */   }
/*     */   
/*     */   public int getDigestSize()
/*     */   {
/*  52 */     return 16;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void processWord(byte[] in, int inOff)
/*     */   {
/*  59 */     this.X[(this.xOff++)] = (in[inOff] & 0xFF | (in[(inOff + 1)] & 0xFF) << 8 | (in[(inOff + 2)] & 0xFF) << 16 | (in[(inOff + 3)] & 0xFF) << 24);
/*     */     
/*     */ 
/*  62 */     if (this.xOff == 16)
/*     */     {
/*  64 */       processBlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void processLength(long bitLength)
/*     */   {
/*  71 */     if (this.xOff > 14)
/*     */     {
/*  73 */       processBlock();
/*     */     }
/*     */     
/*  76 */     this.X[14] = ((int)(bitLength & 0xFFFFFFFFFFFFFFFF));
/*  77 */     this.X[15] = ((int)(bitLength >>> 32));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void unpackWord(int word, byte[] out, int outOff)
/*     */   {
/*  85 */     out[outOff] = ((byte)word);
/*  86 */     out[(outOff + 1)] = ((byte)(word >>> 8));
/*  87 */     out[(outOff + 2)] = ((byte)(word >>> 16));
/*  88 */     out[(outOff + 3)] = ((byte)(word >>> 24));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int doFinal(byte[] out, int outOff)
/*     */   {
/*  95 */     finish();
/*     */     
/*  97 */     unpackWord(this.H0, out, outOff);
/*  98 */     unpackWord(this.H1, out, outOff + 4);
/*  99 */     unpackWord(this.H2, out, outOff + 8);
/* 100 */     unpackWord(this.H3, out, outOff + 12);
/*     */     
/* 102 */     reset();
/*     */     
/* 104 */     return 16;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 112 */     super.reset();
/*     */     
/* 114 */     this.H0 = 1732584193;
/* 115 */     this.H1 = -271733879;
/* 116 */     this.H2 = -1732584194;
/* 117 */     this.H3 = 271733878;
/*     */     
/* 119 */     this.xOff = 0;
/*     */     
/* 121 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 123 */       this.X[i] = 0;
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
/* 134 */     return x << n | x >>> 32 - n;
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
/* 149 */     return x ^ y ^ z;
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
/* 160 */     return x & y | (x ^ 0xFFFFFFFF) & z;
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
/* 171 */     return (x | y ^ 0xFFFFFFFF) ^ z;
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
/* 182 */     return x & z | y & (z ^ 0xFFFFFFFF);
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
/* 193 */     return RL(a + f1(b, c, d) + x, s);
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
/* 204 */     return RL(a + f2(b, c, d) + x + 1518500249, s);
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
/* 215 */     return RL(a + f3(b, c, d) + x + 1859775393, s);
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
/* 226 */     return RL(a + f4(b, c, d) + x + -1894007588, s);
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
/* 237 */     return RL(a + f1(b, c, d) + x, s);
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
/* 248 */     return RL(a + f2(b, c, d) + x + 1836072691, s);
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
/* 259 */     return RL(a + f3(b, c, d) + x + 1548603684, s);
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
/* 270 */     return RL(a + f4(b, c, d) + x + 1352829926, s);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processBlock()
/*     */   {
/* 280 */     int a = aa = this.H0;
/* 281 */     int b = bb = this.H1;
/* 282 */     int c = cc = this.H2;
/* 283 */     int d = dd = this.H3;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 288 */     a = F1(a, b, c, d, this.X[0], 11);
/* 289 */     d = F1(d, a, b, c, this.X[1], 14);
/* 290 */     c = F1(c, d, a, b, this.X[2], 15);
/* 291 */     b = F1(b, c, d, a, this.X[3], 12);
/* 292 */     a = F1(a, b, c, d, this.X[4], 5);
/* 293 */     d = F1(d, a, b, c, this.X[5], 8);
/* 294 */     c = F1(c, d, a, b, this.X[6], 7);
/* 295 */     b = F1(b, c, d, a, this.X[7], 9);
/* 296 */     a = F1(a, b, c, d, this.X[8], 11);
/* 297 */     d = F1(d, a, b, c, this.X[9], 13);
/* 298 */     c = F1(c, d, a, b, this.X[10], 14);
/* 299 */     b = F1(b, c, d, a, this.X[11], 15);
/* 300 */     a = F1(a, b, c, d, this.X[12], 6);
/* 301 */     d = F1(d, a, b, c, this.X[13], 7);
/* 302 */     c = F1(c, d, a, b, this.X[14], 9);
/* 303 */     b = F1(b, c, d, a, this.X[15], 8);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 308 */     a = F2(a, b, c, d, this.X[7], 7);
/* 309 */     d = F2(d, a, b, c, this.X[4], 6);
/* 310 */     c = F2(c, d, a, b, this.X[13], 8);
/* 311 */     b = F2(b, c, d, a, this.X[1], 13);
/* 312 */     a = F2(a, b, c, d, this.X[10], 11);
/* 313 */     d = F2(d, a, b, c, this.X[6], 9);
/* 314 */     c = F2(c, d, a, b, this.X[15], 7);
/* 315 */     b = F2(b, c, d, a, this.X[3], 15);
/* 316 */     a = F2(a, b, c, d, this.X[12], 7);
/* 317 */     d = F2(d, a, b, c, this.X[0], 12);
/* 318 */     c = F2(c, d, a, b, this.X[9], 15);
/* 319 */     b = F2(b, c, d, a, this.X[5], 9);
/* 320 */     a = F2(a, b, c, d, this.X[2], 11);
/* 321 */     d = F2(d, a, b, c, this.X[14], 7);
/* 322 */     c = F2(c, d, a, b, this.X[11], 13);
/* 323 */     b = F2(b, c, d, a, this.X[8], 12);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 328 */     a = F3(a, b, c, d, this.X[3], 11);
/* 329 */     d = F3(d, a, b, c, this.X[10], 13);
/* 330 */     c = F3(c, d, a, b, this.X[14], 6);
/* 331 */     b = F3(b, c, d, a, this.X[4], 7);
/* 332 */     a = F3(a, b, c, d, this.X[9], 14);
/* 333 */     d = F3(d, a, b, c, this.X[15], 9);
/* 334 */     c = F3(c, d, a, b, this.X[8], 13);
/* 335 */     b = F3(b, c, d, a, this.X[1], 15);
/* 336 */     a = F3(a, b, c, d, this.X[2], 14);
/* 337 */     d = F3(d, a, b, c, this.X[7], 8);
/* 338 */     c = F3(c, d, a, b, this.X[0], 13);
/* 339 */     b = F3(b, c, d, a, this.X[6], 6);
/* 340 */     a = F3(a, b, c, d, this.X[13], 5);
/* 341 */     d = F3(d, a, b, c, this.X[11], 12);
/* 342 */     c = F3(c, d, a, b, this.X[5], 7);
/* 343 */     b = F3(b, c, d, a, this.X[12], 5);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 348 */     a = F4(a, b, c, d, this.X[1], 11);
/* 349 */     d = F4(d, a, b, c, this.X[9], 12);
/* 350 */     c = F4(c, d, a, b, this.X[11], 14);
/* 351 */     b = F4(b, c, d, a, this.X[10], 15);
/* 352 */     a = F4(a, b, c, d, this.X[0], 14);
/* 353 */     d = F4(d, a, b, c, this.X[8], 15);
/* 354 */     c = F4(c, d, a, b, this.X[12], 9);
/* 355 */     b = F4(b, c, d, a, this.X[4], 8);
/* 356 */     a = F4(a, b, c, d, this.X[13], 9);
/* 357 */     d = F4(d, a, b, c, this.X[3], 14);
/* 358 */     c = F4(c, d, a, b, this.X[7], 5);
/* 359 */     b = F4(b, c, d, a, this.X[15], 6);
/* 360 */     a = F4(a, b, c, d, this.X[14], 8);
/* 361 */     d = F4(d, a, b, c, this.X[5], 6);
/* 362 */     c = F4(c, d, a, b, this.X[6], 5);
/* 363 */     b = F4(b, c, d, a, this.X[2], 12);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 368 */     int aa = FF4(aa, bb, cc, dd, this.X[5], 8);
/* 369 */     int dd = FF4(dd, aa, bb, cc, this.X[14], 9);
/* 370 */     int cc = FF4(cc, dd, aa, bb, this.X[7], 9);
/* 371 */     int bb = FF4(bb, cc, dd, aa, this.X[0], 11);
/* 372 */     aa = FF4(aa, bb, cc, dd, this.X[9], 13);
/* 373 */     dd = FF4(dd, aa, bb, cc, this.X[2], 15);
/* 374 */     cc = FF4(cc, dd, aa, bb, this.X[11], 15);
/* 375 */     bb = FF4(bb, cc, dd, aa, this.X[4], 5);
/* 376 */     aa = FF4(aa, bb, cc, dd, this.X[13], 7);
/* 377 */     dd = FF4(dd, aa, bb, cc, this.X[6], 7);
/* 378 */     cc = FF4(cc, dd, aa, bb, this.X[15], 8);
/* 379 */     bb = FF4(bb, cc, dd, aa, this.X[8], 11);
/* 380 */     aa = FF4(aa, bb, cc, dd, this.X[1], 14);
/* 381 */     dd = FF4(dd, aa, bb, cc, this.X[10], 14);
/* 382 */     cc = FF4(cc, dd, aa, bb, this.X[3], 12);
/* 383 */     bb = FF4(bb, cc, dd, aa, this.X[12], 6);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 388 */     aa = FF3(aa, bb, cc, dd, this.X[6], 9);
/* 389 */     dd = FF3(dd, aa, bb, cc, this.X[11], 13);
/* 390 */     cc = FF3(cc, dd, aa, bb, this.X[3], 15);
/* 391 */     bb = FF3(bb, cc, dd, aa, this.X[7], 7);
/* 392 */     aa = FF3(aa, bb, cc, dd, this.X[0], 12);
/* 393 */     dd = FF3(dd, aa, bb, cc, this.X[13], 8);
/* 394 */     cc = FF3(cc, dd, aa, bb, this.X[5], 9);
/* 395 */     bb = FF3(bb, cc, dd, aa, this.X[10], 11);
/* 396 */     aa = FF3(aa, bb, cc, dd, this.X[14], 7);
/* 397 */     dd = FF3(dd, aa, bb, cc, this.X[15], 7);
/* 398 */     cc = FF3(cc, dd, aa, bb, this.X[8], 12);
/* 399 */     bb = FF3(bb, cc, dd, aa, this.X[12], 7);
/* 400 */     aa = FF3(aa, bb, cc, dd, this.X[4], 6);
/* 401 */     dd = FF3(dd, aa, bb, cc, this.X[9], 15);
/* 402 */     cc = FF3(cc, dd, aa, bb, this.X[1], 13);
/* 403 */     bb = FF3(bb, cc, dd, aa, this.X[2], 11);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 408 */     aa = FF2(aa, bb, cc, dd, this.X[15], 9);
/* 409 */     dd = FF2(dd, aa, bb, cc, this.X[5], 7);
/* 410 */     cc = FF2(cc, dd, aa, bb, this.X[1], 15);
/* 411 */     bb = FF2(bb, cc, dd, aa, this.X[3], 11);
/* 412 */     aa = FF2(aa, bb, cc, dd, this.X[7], 8);
/* 413 */     dd = FF2(dd, aa, bb, cc, this.X[14], 6);
/* 414 */     cc = FF2(cc, dd, aa, bb, this.X[6], 6);
/* 415 */     bb = FF2(bb, cc, dd, aa, this.X[9], 14);
/* 416 */     aa = FF2(aa, bb, cc, dd, this.X[11], 12);
/* 417 */     dd = FF2(dd, aa, bb, cc, this.X[8], 13);
/* 418 */     cc = FF2(cc, dd, aa, bb, this.X[12], 5);
/* 419 */     bb = FF2(bb, cc, dd, aa, this.X[2], 14);
/* 420 */     aa = FF2(aa, bb, cc, dd, this.X[10], 13);
/* 421 */     dd = FF2(dd, aa, bb, cc, this.X[0], 13);
/* 422 */     cc = FF2(cc, dd, aa, bb, this.X[4], 7);
/* 423 */     bb = FF2(bb, cc, dd, aa, this.X[13], 5);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 428 */     aa = FF1(aa, bb, cc, dd, this.X[8], 15);
/* 429 */     dd = FF1(dd, aa, bb, cc, this.X[6], 5);
/* 430 */     cc = FF1(cc, dd, aa, bb, this.X[4], 8);
/* 431 */     bb = FF1(bb, cc, dd, aa, this.X[1], 11);
/* 432 */     aa = FF1(aa, bb, cc, dd, this.X[3], 14);
/* 433 */     dd = FF1(dd, aa, bb, cc, this.X[11], 14);
/* 434 */     cc = FF1(cc, dd, aa, bb, this.X[15], 6);
/* 435 */     bb = FF1(bb, cc, dd, aa, this.X[0], 14);
/* 436 */     aa = FF1(aa, bb, cc, dd, this.X[5], 6);
/* 437 */     dd = FF1(dd, aa, bb, cc, this.X[12], 9);
/* 438 */     cc = FF1(cc, dd, aa, bb, this.X[2], 12);
/* 439 */     bb = FF1(bb, cc, dd, aa, this.X[13], 9);
/* 440 */     aa = FF1(aa, bb, cc, dd, this.X[9], 12);
/* 441 */     dd = FF1(dd, aa, bb, cc, this.X[7], 5);
/* 442 */     cc = FF1(cc, dd, aa, bb, this.X[10], 15);
/* 443 */     bb = FF1(bb, cc, dd, aa, this.X[14], 8);
/*     */     
/* 445 */     dd += c + this.H1;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 450 */     this.H1 = (this.H2 + d + aa);
/* 451 */     this.H2 = (this.H3 + a + bb);
/* 452 */     this.H3 = (this.H0 + b + cc);
/* 453 */     this.H0 = dd;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 458 */     this.xOff = 0;
/* 459 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 461 */       this.X[i] = 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/RIPEMD128Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */