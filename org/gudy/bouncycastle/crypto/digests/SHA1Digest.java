/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SHA1Digest
/*     */   extends GeneralDigest
/*     */ {
/*     */   private static final int DIGEST_LENGTH = 20;
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
/*  20 */   private int[] X = new int[80];
/*     */   private int xOff;
/*     */   private static final int Y1 = 1518500249;
/*     */   private static final int Y2 = 1859775393;
/*     */   private static final int Y3 = -1894007588;
/*     */   private static final int Y4 = -899497514;
/*     */   
/*     */   public SHA1Digest() {
/*  28 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SHA1Digest(SHA1Digest t)
/*     */   {
/*  37 */     super(t);
/*     */     
/*  39 */     this.H1 = t.H1;
/*  40 */     this.H2 = t.H2;
/*  41 */     this.H3 = t.H3;
/*  42 */     this.H4 = t.H4;
/*  43 */     this.H5 = t.H5;
/*     */     
/*  45 */     System.arraycopy(t.X, 0, this.X, 0, t.X.length);
/*  46 */     this.xOff = t.xOff;
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  51 */     return "SHA-1";
/*     */   }
/*     */   
/*     */   public int getDigestSize()
/*     */   {
/*  56 */     return 20;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void processWord(byte[] in, int inOff)
/*     */   {
/*  63 */     this.X[(this.xOff++)] = ((in[inOff] & 0xFF) << 24 | (in[(inOff + 1)] & 0xFF) << 16 | (in[(inOff + 2)] & 0xFF) << 8 | in[(inOff + 3)] & 0xFF);
/*     */     
/*     */ 
/*  66 */     if (this.xOff == 16)
/*     */     {
/*  68 */       processBlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void unpackWord(int word, byte[] out, int outOff)
/*     */   {
/*  77 */     out[outOff] = ((byte)(word >>> 24));
/*  78 */     out[(outOff + 1)] = ((byte)(word >>> 16));
/*  79 */     out[(outOff + 2)] = ((byte)(word >>> 8));
/*  80 */     out[(outOff + 3)] = ((byte)word);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void processLength(long bitLength)
/*     */   {
/*  86 */     if (this.xOff > 14)
/*     */     {
/*  88 */       processBlock();
/*     */     }
/*     */     
/*  91 */     this.X[14] = ((int)(bitLength >>> 32));
/*  92 */     this.X[15] = ((int)(bitLength & 0xFFFFFFFFFFFFFFFF));
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
/* 105 */     unpackWord(this.H5, out, outOff + 16);
/*     */     
/* 107 */     reset();
/*     */     
/* 109 */     return 20;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 117 */     super.reset();
/*     */     
/* 119 */     this.H1 = 1732584193;
/* 120 */     this.H2 = -271733879;
/* 121 */     this.H3 = -1732584194;
/* 122 */     this.H4 = 271733878;
/* 123 */     this.H5 = -1009589776;
/*     */     
/* 125 */     this.xOff = 0;
/* 126 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 128 */       this.X[i] = 0;
/*     */     }
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
/*     */ 
/*     */   private int f(int u, int v, int w)
/*     */   {
/* 145 */     return u & v | (u ^ 0xFFFFFFFF) & w;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int h(int u, int v, int w)
/*     */   {
/* 153 */     return u ^ v ^ w;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int g(int u, int v, int w)
/*     */   {
/* 161 */     return u & v | u & w | v & w;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int rotateLeft(int x, int n)
/*     */   {
/* 168 */     return x << n | x >>> 32 - n;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processBlock()
/*     */   {
/* 176 */     for (int i = 16; i <= 79; i++)
/*     */     {
/* 178 */       this.X[i] = rotateLeft(this.X[(i - 3)] ^ this.X[(i - 8)] ^ this.X[(i - 14)] ^ this.X[(i - 16)], 1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 184 */     int A = this.H1;
/* 185 */     int B = this.H2;
/* 186 */     int C = this.H3;
/* 187 */     int D = this.H4;
/* 188 */     int E = this.H5;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 193 */     for (int j = 0; j <= 19; j++)
/*     */     {
/* 195 */       int t = rotateLeft(A, 5) + f(B, C, D) + E + this.X[j] + 1518500249;
/*     */       
/* 197 */       E = D;
/* 198 */       D = C;
/* 199 */       C = rotateLeft(B, 30);
/* 200 */       B = A;
/* 201 */       A = t;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 207 */     for (int j = 20; j <= 39; j++)
/*     */     {
/* 209 */       int t = rotateLeft(A, 5) + h(B, C, D) + E + this.X[j] + 1859775393;
/*     */       
/* 211 */       E = D;
/* 212 */       D = C;
/* 213 */       C = rotateLeft(B, 30);
/* 214 */       B = A;
/* 215 */       A = t;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 221 */     for (int j = 40; j <= 59; j++)
/*     */     {
/* 223 */       int t = rotateLeft(A, 5) + g(B, C, D) + E + this.X[j] + -1894007588;
/*     */       
/* 225 */       E = D;
/* 226 */       D = C;
/* 227 */       C = rotateLeft(B, 30);
/* 228 */       B = A;
/* 229 */       A = t;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 235 */     for (int j = 60; j <= 79; j++)
/*     */     {
/* 237 */       int t = rotateLeft(A, 5) + h(B, C, D) + E + this.X[j] + -899497514;
/*     */       
/* 239 */       E = D;
/* 240 */       D = C;
/* 241 */       C = rotateLeft(B, 30);
/* 242 */       B = A;
/* 243 */       A = t;
/*     */     }
/*     */     
/* 246 */     this.H1 += A;
/* 247 */     this.H2 += B;
/* 248 */     this.H3 += C;
/* 249 */     this.H4 += D;
/* 250 */     this.H5 += E;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 255 */     this.xOff = 0;
/* 256 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/* 258 */       this.X[i] = 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/SHA1Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */