/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MD2Digest
/*     */   implements Digest
/*     */ {
/*     */   private static final int DIGEST_LENGTH = 16;
/*  15 */   private byte[] X = new byte[48];
/*     */   
/*     */ 
/*     */   private int xOff;
/*     */   
/*  20 */   private byte[] M = new byte[16];
/*     */   
/*     */ 
/*     */   private int mOff;
/*     */   
/*  25 */   private byte[] C = new byte[16];
/*     */   private int COff;
/*     */   
/*     */   public MD2Digest()
/*     */   {
/*  30 */     reset();
/*     */   }
/*     */   
/*     */   public MD2Digest(MD2Digest t) {
/*  34 */     System.arraycopy(t.X, 0, this.X, 0, t.X.length);
/*  35 */     this.xOff = t.xOff;
/*  36 */     System.arraycopy(t.M, 0, this.M, 0, t.M.length);
/*  37 */     this.mOff = t.mOff;
/*  38 */     System.arraycopy(t.C, 0, this.C, 0, t.C.length);
/*  39 */     this.COff = t.COff;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAlgorithmName()
/*     */   {
/*  48 */     return "MD2";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDigestSize()
/*     */   {
/*  57 */     return 16;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int doFinal(byte[] out, int outOff)
/*     */   {
/*  69 */     byte paddingByte = (byte)(this.M.length - this.mOff);
/*  70 */     for (int i = this.mOff; i < this.M.length; i++)
/*     */     {
/*  72 */       this.M[i] = paddingByte;
/*     */     }
/*     */     
/*  75 */     processCheckSum(this.M);
/*     */     
/*  77 */     processBlock(this.M);
/*     */     
/*  79 */     processBlock(this.C);
/*     */     
/*  81 */     System.arraycopy(this.X, this.xOff, out, outOff, 16);
/*     */     
/*  83 */     reset();
/*     */     
/*  85 */     return 16;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/*  92 */     this.xOff = 0;
/*  93 */     for (int i = 0; i != this.X.length; i++)
/*     */     {
/*  95 */       this.X[i] = 0;
/*     */     }
/*  97 */     this.mOff = 0;
/*  98 */     for (int i = 0; i != this.M.length; i++)
/*     */     {
/* 100 */       this.M[i] = 0;
/*     */     }
/* 102 */     this.COff = 0;
/* 103 */     for (int i = 0; i != this.C.length; i++)
/*     */     {
/* 105 */       this.C[i] = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte in)
/*     */   {
/* 115 */     this.M[(this.mOff++)] = in;
/*     */     
/* 117 */     if (this.mOff == 16)
/*     */     {
/* 119 */       processCheckSum(this.M);
/* 120 */       processBlock(this.M);
/* 121 */       this.mOff = 0;
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
/*     */   public void update(byte[] in, int inOff, int len)
/*     */   {
/* 137 */     while ((this.mOff != 0) && (len > 0))
/*     */     {
/* 139 */       update(in[inOff]);
/* 140 */       inOff++;
/* 141 */       len--;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 147 */     while (len > 16)
/*     */     {
/* 149 */       System.arraycopy(in, inOff, this.M, 0, 16);
/* 150 */       processCheckSum(this.M);
/* 151 */       processBlock(this.M);
/* 152 */       len -= 16;
/* 153 */       inOff += 16;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 159 */     while (len > 0)
/*     */     {
/* 161 */       update(in[inOff]);
/* 162 */       inOff++;
/* 163 */       len--;
/*     */     }
/*     */   }
/*     */   
/*     */   protected void processCheckSum(byte[] m) {
/* 168 */     int L = this.C[15];
/* 169 */     for (int i = 0; i < 16; i++)
/*     */     {
/* 171 */       int tmp21_20 = i; byte[] tmp21_17 = this.C;tmp21_17[tmp21_20] = ((byte)(tmp21_17[tmp21_20] ^ S[((m[i] ^ L) & 0xFF)]));
/* 172 */       L = this.C[i];
/*     */     }
/*     */   }
/*     */   
/*     */   protected void processBlock(byte[] m) {
/* 177 */     for (int i = 0; i < 16; i++)
/*     */     {
/* 179 */       this.X[(i + 16)] = m[i];
/* 180 */       this.X[(i + 32)] = ((byte)(m[i] ^ this.X[i]));
/*     */     }
/*     */     
/* 183 */     int t = 0;
/*     */     
/* 185 */     for (int j = 0; j < 18; j++)
/*     */     {
/* 187 */       for (int k = 0; k < 48; k++)
/*     */       {
/* 189 */         int tmp72_70 = k; byte[] tmp72_67 = this.X;t = tmp72_67[tmp72_70] = (byte)(tmp72_67[tmp72_70] ^ S[t]);
/* 190 */         t &= 0xFF;
/*     */       }
/* 192 */       t = (t + j) % 256;
/*     */     }
/*     */   }
/*     */   
/* 196 */   private static final byte[] S = { 41, 46, 67, -55, -94, -40, 124, 1, 61, 54, 84, -95, -20, -16, 6, 19, 98, -89, 5, -13, -64, -57, 115, -116, -104, -109, 43, -39, -68, 76, -126, -54, 30, -101, 87, 60, -3, -44, -32, 22, 103, 66, 111, 24, -118, 23, -27, 18, -66, 78, -60, -42, -38, -98, -34, 73, -96, -5, -11, -114, -69, 47, -18, 122, -87, 104, 121, -111, 21, -78, 7, 63, -108, -62, 16, -119, 11, 34, 95, 33, Byte.MIN_VALUE, Byte.MAX_VALUE, 93, -102, 90, -112, 50, 39, 53, 62, -52, -25, -65, -9, -105, 3, -1, 25, 48, -77, 72, -91, -75, -47, -41, 94, -110, 42, -84, 86, -86, -58, 79, -72, 56, -46, -106, -92, 125, -74, 118, -4, 107, -30, -100, 116, 4, -15, 69, -99, 112, 89, 100, 113, -121, 32, -122, 91, -49, 101, -26, 45, -88, 2, 27, 96, 37, -83, -82, -80, -71, -10, 28, 70, 97, 105, 52, 64, 126, 15, 85, 71, -93, 35, -35, 81, -81, 58, -61, 92, -7, -50, -70, -59, -22, 38, 44, 83, 13, 110, -123, 40, -124, 9, -45, -33, -51, -12, 65, -127, 77, 82, 106, -36, 55, -56, 108, -63, -85, -6, 36, -31, 123, 8, 12, -67, -79, 74, 120, -120, -107, -117, -29, 99, -24, 109, -23, -53, -43, -2, 59, 0, 29, 57, -14, -17, -73, 14, 102, 88, -48, -28, -90, 119, 114, -8, -21, 117, 75, 10, 49, 68, 80, -76, -113, -19, 31, 26, -37, -103, -115, 51, -97, 17, -125, 20 };
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/MD2Digest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */