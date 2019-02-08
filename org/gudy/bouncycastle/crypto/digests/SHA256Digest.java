/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SHA256Digest
/*     */   extends GeneralDigest
/*     */ {
/*     */   private static final int DIGEST_LENGTH = 32;
/*     */   
/*     */   private int H1;
/*     */   
/*     */   private int H2;
/*     */   
/*     */   private int H3;
/*     */   
/*     */   private int H4;
/*     */   
/*     */   private int H5;
/*     */   
/*     */   private int H6;
/*     */   
/*     */   private int H7;
/*     */   
/*     */   private int H8;
/*     */   
/*  26 */   private int[] X = new int[64];
/*     */   
/*     */ 
/*     */   private int xOff;
/*     */   
/*     */ 
/*     */   public SHA256Digest()
/*     */   {
/*  34 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SHA256Digest(SHA256Digest t)
/*     */   {
/*  43 */     super(t);
/*     */     
/*  45 */     this.H1 = t.H1;
/*  46 */     this.H2 = t.H2;
/*  47 */     this.H3 = t.H3;
/*  48 */     this.H4 = t.H4;
/*  49 */     this.H5 = t.H5;
/*  50 */     this.H6 = t.H6;
/*  51 */     this.H7 = t.H7;
/*  52 */     this.H8 = t.H8;
/*     */     
/*  54 */     System.arraycopy(t.X, 0, this.X, 0, t.X.length);
/*  55 */     this.xOff = t.xOff;
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  60 */     return "SHA-256";
/*     */   }
/*     */   
/*     */   public int getDigestSize()
/*     */   {
/*  65 */     return 32;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void processWord(byte[] in, int inOff)
/*     */   {
/*  72 */     this.X[(this.xOff++)] = ((in[inOff] & 0xFF) << 24 | (in[(inOff + 1)] & 0xFF) << 16 | (in[(inOff + 2)] & 0xFF) << 8 | in[(inOff + 3)] & 0xFF);
/*     */     
/*     */ 
/*  75 */     if (this.xOff == 16)
/*     */     {
/*  77 */       processBlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void unpackWord(int word, byte[] out, int outOff)
/*     */   {
/*  86 */     out[outOff] = ((byte)(word >>> 24));
/*  87 */     out[(outOff + 1)] = ((byte)(word >>> 16));
/*  88 */     out[(outOff + 2)] = ((byte)(word >>> 8));
/*  89 */     out[(outOff + 3)] = ((byte)word);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void processLength(long bitLength)
/*     */   {
/*  95 */     if (this.xOff > 14)
/*     */     {
/*  97 */       processBlock();
/*     */     }
/*     */     
/* 100 */     this.X[14] = ((int)(bitLength >>> 32));
/* 101 */     this.X[15] = ((int)(bitLength & 0xFFFFFFFFFFFFFFFF));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int doFinal(byte[] out, int outOff)
/*     */   {
/* 108 */     finish();
/*     */     
/* 110 */     unpackWord(this.H1, out, outOff);
/* 111 */     unpackWord(this.H2, out, outOff + 4);
/* 112 */     unpackWord(this.H3, out, outOff + 8);
/* 113 */     unpackWord(this.H4, out, outOff + 12);
/* 114 */     unpackWord(this.H5, out, outOff + 16);
/* 115 */     unpackWord(this.H6, out, outOff + 20);
/* 116 */     unpackWord(this.H7, out, outOff + 24);
/* 117 */     unpackWord(this.H8, out, outOff + 28);
/*     */     
/* 119 */     reset();
/*     */     
/* 121 */     return 32;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 129 */     super.reset();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 136 */     this.H1 = 1779033703;
/* 137 */     this.H2 = -1150833019;
/* 138 */     this.H3 = 1013904242;
/* 139 */     this.H4 = -1521486534;
/* 140 */     this.H5 = 1359893119;
/* 141 */     this.H6 = -1694144372;
/* 142 */     this.H7 = 528734635;
/* 143 */     this.H8 = 1541459225;
/*     */     
/* 145 */     this.xOff = 0;
/* 146 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 148 */       this.X[i] = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processBlock()
/*     */   {
/* 157 */     for (int t = 16; t <= 63; t++)
/*     */     {
/* 159 */       this.X[t] = (Theta1(this.X[(t - 2)]) + this.X[(t - 7)] + Theta0(this.X[(t - 15)]) + this.X[(t - 16)]);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 165 */     int a = this.H1;
/* 166 */     int b = this.H2;
/* 167 */     int c = this.H3;
/* 168 */     int d = this.H4;
/* 169 */     int e = this.H5;
/* 170 */     int f = this.H6;
/* 171 */     int g = this.H7;
/* 172 */     int h = this.H8;
/*     */     
/* 174 */     for (int t = 0; t <= 63; t++)
/*     */     {
/*     */ 
/*     */ 
/* 178 */       int T1 = h + Sum1(e) + Ch(e, f, g) + K[t] + this.X[t];
/* 179 */       int T2 = Sum0(a) + Maj(a, b, c);
/* 180 */       h = g;
/* 181 */       g = f;
/* 182 */       f = e;
/* 183 */       e = d + T1;
/* 184 */       d = c;
/* 185 */       c = b;
/* 186 */       b = a;
/* 187 */       a = T1 + T2;
/*     */     }
/*     */     
/* 190 */     this.H1 += a;
/* 191 */     this.H2 += b;
/* 192 */     this.H3 += c;
/* 193 */     this.H4 += d;
/* 194 */     this.H5 += e;
/* 195 */     this.H6 += f;
/* 196 */     this.H7 += g;
/* 197 */     this.H8 += h;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 202 */     this.xOff = 0;
/* 203 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 205 */       this.X[i] = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int rotateRight(int x, int n)
/*     */   {
/* 213 */     return x >>> n | x << 32 - n;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int Ch(int x, int y, int z)
/*     */   {
/* 222 */     return x & y ^ (x ^ 0xFFFFFFFF) & z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int Maj(int x, int y, int z)
/*     */   {
/* 230 */     return x & y ^ x & z ^ y & z;
/*     */   }
/*     */   
/*     */ 
/*     */   private int Sum0(int x)
/*     */   {
/* 236 */     return rotateRight(x, 2) ^ rotateRight(x, 13) ^ rotateRight(x, 22);
/*     */   }
/*     */   
/*     */ 
/*     */   private int Sum1(int x)
/*     */   {
/* 242 */     return rotateRight(x, 6) ^ rotateRight(x, 11) ^ rotateRight(x, 25);
/*     */   }
/*     */   
/*     */ 
/*     */   private int Theta0(int x)
/*     */   {
/* 248 */     return rotateRight(x, 7) ^ rotateRight(x, 18) ^ x >>> 3;
/*     */   }
/*     */   
/*     */ 
/*     */   private int Theta1(int x)
/*     */   {
/* 254 */     return rotateRight(x, 17) ^ rotateRight(x, 19) ^ x >>> 10;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 261 */   static final int[] K = { 1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998 };
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/SHA256Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */