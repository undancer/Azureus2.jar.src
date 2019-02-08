/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ 
/*     */ public class MD5Digest
/*     */   extends GeneralDigest
/*     */ {
/*     */   private static final int DIGEST_LENGTH = 16;
/*     */   
/*     */   private int H1;
/*     */   
/*     */   private int H2;
/*     */   
/*     */   private int H3;
/*     */   
/*     */   private int H4;
/*     */   
/*  17 */   private int[] X = new int[16];
/*     */   private int xOff;
/*     */   private static final int S11 = 7;
/*     */   private static final int S12 = 12;
/*     */   private static final int S13 = 17;
/*     */   
/*     */   public MD5Digest()
/*     */   {
/*  25 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MD5Digest(MD5Digest t)
/*     */   {
/*  34 */     super(t);
/*     */     
/*  36 */     this.H1 = t.H1;
/*  37 */     this.H2 = t.H2;
/*  38 */     this.H3 = t.H3;
/*  39 */     this.H4 = t.H4;
/*     */     
/*  41 */     System.arraycopy(t.X, 0, this.X, 0, t.X.length);
/*  42 */     this.xOff = t.xOff;
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  47 */     return "MD5";
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
/*  97 */     unpackWord(this.H1, out, outOff);
/*  98 */     unpackWord(this.H2, out, outOff + 4);
/*  99 */     unpackWord(this.H3, out, outOff + 8);
/* 100 */     unpackWord(this.H4, out, outOff + 12);
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
/* 114 */     this.H1 = 1732584193;
/* 115 */     this.H2 = -271733879;
/* 116 */     this.H3 = -1732584194;
/* 117 */     this.H4 = 271733878;
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
/*     */   private static final int S14 = 22;
/*     */   
/*     */ 
/*     */   private static final int S21 = 5;
/*     */   
/*     */ 
/*     */   private static final int S22 = 9;
/*     */   
/*     */ 
/*     */   private static final int S23 = 14;
/*     */   
/*     */ 
/*     */   private static final int S24 = 20;
/*     */   
/*     */ 
/*     */   private static final int S31 = 4;
/*     */   
/*     */ 
/*     */   private static final int S32 = 11;
/*     */   
/*     */ 
/*     */   private static final int S33 = 16;
/*     */   
/*     */ 
/*     */   private static final int S34 = 23;
/*     */   
/*     */ 
/*     */   private static final int S41 = 6;
/*     */   
/*     */   private static final int S42 = 10;
/*     */   
/*     */   private static final int S43 = 15;
/*     */   
/*     */   private static final int S44 = 21;
/*     */   
/*     */   private int rotateLeft(int x, int n)
/*     */   {
/* 166 */     return x << n | x >>> 32 - n;
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
/* 177 */     return u & v | (u ^ 0xFFFFFFFF) & w;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int G(int u, int v, int w)
/*     */   {
/* 185 */     return u & w | v & (w ^ 0xFFFFFFFF);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int H(int u, int v, int w)
/*     */   {
/* 193 */     return u ^ v ^ w;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int K(int u, int v, int w)
/*     */   {
/* 201 */     return v ^ (u | w ^ 0xFFFFFFFF);
/*     */   }
/*     */   
/*     */   protected void processBlock()
/*     */   {
/* 206 */     int a = this.H1;
/* 207 */     int b = this.H2;
/* 208 */     int c = this.H3;
/* 209 */     int d = this.H4;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 214 */     a = rotateLeft(a + F(b, c, d) + this.X[0] + -680876936, 7) + b;
/* 215 */     d = rotateLeft(d + F(a, b, c) + this.X[1] + -389564586, 12) + a;
/* 216 */     c = rotateLeft(c + F(d, a, b) + this.X[2] + 606105819, 17) + d;
/* 217 */     b = rotateLeft(b + F(c, d, a) + this.X[3] + -1044525330, 22) + c;
/* 218 */     a = rotateLeft(a + F(b, c, d) + this.X[4] + -176418897, 7) + b;
/* 219 */     d = rotateLeft(d + F(a, b, c) + this.X[5] + 1200080426, 12) + a;
/* 220 */     c = rotateLeft(c + F(d, a, b) + this.X[6] + -1473231341, 17) + d;
/* 221 */     b = rotateLeft(b + F(c, d, a) + this.X[7] + -45705983, 22) + c;
/* 222 */     a = rotateLeft(a + F(b, c, d) + this.X[8] + 1770035416, 7) + b;
/* 223 */     d = rotateLeft(d + F(a, b, c) + this.X[9] + -1958414417, 12) + a;
/* 224 */     c = rotateLeft(c + F(d, a, b) + this.X[10] + -42063, 17) + d;
/* 225 */     b = rotateLeft(b + F(c, d, a) + this.X[11] + -1990404162, 22) + c;
/* 226 */     a = rotateLeft(a + F(b, c, d) + this.X[12] + 1804603682, 7) + b;
/* 227 */     d = rotateLeft(d + F(a, b, c) + this.X[13] + -40341101, 12) + a;
/* 228 */     c = rotateLeft(c + F(d, a, b) + this.X[14] + -1502002290, 17) + d;
/* 229 */     b = rotateLeft(b + F(c, d, a) + this.X[15] + 1236535329, 22) + c;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 234 */     a = rotateLeft(a + G(b, c, d) + this.X[1] + -165796510, 5) + b;
/* 235 */     d = rotateLeft(d + G(a, b, c) + this.X[6] + -1069501632, 9) + a;
/* 236 */     c = rotateLeft(c + G(d, a, b) + this.X[11] + 643717713, 14) + d;
/* 237 */     b = rotateLeft(b + G(c, d, a) + this.X[0] + -373897302, 20) + c;
/* 238 */     a = rotateLeft(a + G(b, c, d) + this.X[5] + -701558691, 5) + b;
/* 239 */     d = rotateLeft(d + G(a, b, c) + this.X[10] + 38016083, 9) + a;
/* 240 */     c = rotateLeft(c + G(d, a, b) + this.X[15] + -660478335, 14) + d;
/* 241 */     b = rotateLeft(b + G(c, d, a) + this.X[4] + -405537848, 20) + c;
/* 242 */     a = rotateLeft(a + G(b, c, d) + this.X[9] + 568446438, 5) + b;
/* 243 */     d = rotateLeft(d + G(a, b, c) + this.X[14] + -1019803690, 9) + a;
/* 244 */     c = rotateLeft(c + G(d, a, b) + this.X[3] + -187363961, 14) + d;
/* 245 */     b = rotateLeft(b + G(c, d, a) + this.X[8] + 1163531501, 20) + c;
/* 246 */     a = rotateLeft(a + G(b, c, d) + this.X[13] + -1444681467, 5) + b;
/* 247 */     d = rotateLeft(d + G(a, b, c) + this.X[2] + -51403784, 9) + a;
/* 248 */     c = rotateLeft(c + G(d, a, b) + this.X[7] + 1735328473, 14) + d;
/* 249 */     b = rotateLeft(b + G(c, d, a) + this.X[12] + -1926607734, 20) + c;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 254 */     a = rotateLeft(a + H(b, c, d) + this.X[5] + -378558, 4) + b;
/* 255 */     d = rotateLeft(d + H(a, b, c) + this.X[8] + -2022574463, 11) + a;
/* 256 */     c = rotateLeft(c + H(d, a, b) + this.X[11] + 1839030562, 16) + d;
/* 257 */     b = rotateLeft(b + H(c, d, a) + this.X[14] + -35309556, 23) + c;
/* 258 */     a = rotateLeft(a + H(b, c, d) + this.X[1] + -1530992060, 4) + b;
/* 259 */     d = rotateLeft(d + H(a, b, c) + this.X[4] + 1272893353, 11) + a;
/* 260 */     c = rotateLeft(c + H(d, a, b) + this.X[7] + -155497632, 16) + d;
/* 261 */     b = rotateLeft(b + H(c, d, a) + this.X[10] + -1094730640, 23) + c;
/* 262 */     a = rotateLeft(a + H(b, c, d) + this.X[13] + 681279174, 4) + b;
/* 263 */     d = rotateLeft(d + H(a, b, c) + this.X[0] + -358537222, 11) + a;
/* 264 */     c = rotateLeft(c + H(d, a, b) + this.X[3] + -722521979, 16) + d;
/* 265 */     b = rotateLeft(b + H(c, d, a) + this.X[6] + 76029189, 23) + c;
/* 266 */     a = rotateLeft(a + H(b, c, d) + this.X[9] + -640364487, 4) + b;
/* 267 */     d = rotateLeft(d + H(a, b, c) + this.X[12] + -421815835, 11) + a;
/* 268 */     c = rotateLeft(c + H(d, a, b) + this.X[15] + 530742520, 16) + d;
/* 269 */     b = rotateLeft(b + H(c, d, a) + this.X[2] + -995338651, 23) + c;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 274 */     a = rotateLeft(a + K(b, c, d) + this.X[0] + -198630844, 6) + b;
/* 275 */     d = rotateLeft(d + K(a, b, c) + this.X[7] + 1126891415, 10) + a;
/* 276 */     c = rotateLeft(c + K(d, a, b) + this.X[14] + -1416354905, 15) + d;
/* 277 */     b = rotateLeft(b + K(c, d, a) + this.X[5] + -57434055, 21) + c;
/* 278 */     a = rotateLeft(a + K(b, c, d) + this.X[12] + 1700485571, 6) + b;
/* 279 */     d = rotateLeft(d + K(a, b, c) + this.X[3] + -1894986606, 10) + a;
/* 280 */     c = rotateLeft(c + K(d, a, b) + this.X[10] + -1051523, 15) + d;
/* 281 */     b = rotateLeft(b + K(c, d, a) + this.X[1] + -2054922799, 21) + c;
/* 282 */     a = rotateLeft(a + K(b, c, d) + this.X[8] + 1873313359, 6) + b;
/* 283 */     d = rotateLeft(d + K(a, b, c) + this.X[15] + -30611744, 10) + a;
/* 284 */     c = rotateLeft(c + K(d, a, b) + this.X[6] + -1560198380, 15) + d;
/* 285 */     b = rotateLeft(b + K(c, d, a) + this.X[13] + 1309151649, 21) + c;
/* 286 */     a = rotateLeft(a + K(b, c, d) + this.X[4] + -145523070, 6) + b;
/* 287 */     d = rotateLeft(d + K(a, b, c) + this.X[11] + -1120210379, 10) + a;
/* 288 */     c = rotateLeft(c + K(d, a, b) + this.X[2] + 718787259, 15) + d;
/* 289 */     b = rotateLeft(b + K(c, d, a) + this.X[9] + -343485551, 21) + c;
/*     */     
/* 291 */     this.H1 += a;
/* 292 */     this.H2 += b;
/* 293 */     this.H3 += c;
/* 294 */     this.H4 += d;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 299 */     this.xOff = 0;
/* 300 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 302 */       this.X[i] = 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/MD5Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */