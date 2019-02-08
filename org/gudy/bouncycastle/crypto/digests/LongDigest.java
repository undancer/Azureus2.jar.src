/*     */ package org.gudy.bouncycastle.crypto.digests;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ 
/*     */ public abstract class LongDigest
/*     */   implements Digest
/*     */ {
/*     */   private byte[] xBuf;
/*     */   private int xBufOff;
/*     */   private long byteCount1;
/*     */   private long byteCount2;
/*     */   protected long H1;
/*     */   protected long H2;
/*     */   protected long H3;
/*     */   protected long H4;
/*     */   protected long H5;
/*     */   protected long H6;
/*     */   protected long H7;
/*     */   protected long H8;
/*  20 */   private long[] W = new long[80];
/*     */   
/*     */ 
/*     */   private int wOff;
/*     */   
/*     */ 
/*     */   protected LongDigest()
/*     */   {
/*  28 */     this.xBuf = new byte[8];
/*  29 */     this.xBufOff = 0;
/*     */     
/*  31 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected LongDigest(LongDigest t)
/*     */   {
/*  41 */     this.xBuf = new byte[t.xBuf.length];
/*  42 */     System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);
/*     */     
/*  44 */     this.xBufOff = t.xBufOff;
/*  45 */     this.byteCount1 = t.byteCount1;
/*  46 */     this.byteCount2 = t.byteCount2;
/*     */     
/*  48 */     this.H1 = t.H1;
/*  49 */     this.H2 = t.H2;
/*  50 */     this.H3 = t.H3;
/*  51 */     this.H4 = t.H4;
/*  52 */     this.H5 = t.H5;
/*  53 */     this.H6 = t.H6;
/*  54 */     this.H7 = t.H7;
/*  55 */     this.H8 = t.H8;
/*     */     
/*  57 */     System.arraycopy(t.W, 0, this.W, 0, t.W.length);
/*  58 */     this.wOff = t.wOff;
/*     */   }
/*     */   
/*     */ 
/*     */   public void update(byte in)
/*     */   {
/*  64 */     this.xBuf[(this.xBufOff++)] = in;
/*     */     
/*  66 */     if (this.xBufOff == this.xBuf.length)
/*     */     {
/*  68 */       processWord(this.xBuf, 0);
/*  69 */       this.xBufOff = 0;
/*     */     }
/*     */     
/*  72 */     this.byteCount1 += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte[] in, int inOff, int len)
/*     */   {
/*  83 */     while ((this.xBufOff != 0) && (len > 0))
/*     */     {
/*  85 */       update(in[inOff]);
/*     */       
/*  87 */       inOff++;
/*  88 */       len--;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  94 */     while (len > this.xBuf.length)
/*     */     {
/*  96 */       processWord(in, inOff);
/*     */       
/*  98 */       inOff += this.xBuf.length;
/*  99 */       len -= this.xBuf.length;
/* 100 */       this.byteCount1 += this.xBuf.length;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 106 */     while (len > 0)
/*     */     {
/* 108 */       update(in[inOff]);
/*     */       
/* 110 */       inOff++;
/* 111 */       len--;
/*     */     }
/*     */   }
/*     */   
/*     */   public void finish()
/*     */   {
/* 117 */     adjustByteCounts();
/*     */     
/* 119 */     long lowBitLength = this.byteCount1 << 3;
/* 120 */     long hiBitLength = this.byteCount2;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 125 */     update((byte)Byte.MIN_VALUE);
/*     */     
/* 127 */     while (this.xBufOff != 0)
/*     */     {
/* 129 */       update((byte)0);
/*     */     }
/*     */     
/* 132 */     processLength(lowBitLength, hiBitLength);
/*     */     
/* 134 */     processBlock();
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/* 139 */     this.byteCount1 = 0L;
/* 140 */     this.byteCount2 = 0L;
/*     */     
/* 142 */     this.xBufOff = 0;
/* 143 */     for (int i = 0; i < this.xBuf.length; i++) {
/* 144 */       this.xBuf[i] = 0;
/*     */     }
/*     */     
/* 147 */     this.wOff = 0;
/* 148 */     for (int i = 0; i != this.W.length; i++)
/*     */     {
/* 150 */       this.W[i] = 0L;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void processWord(byte[] in, int inOff)
/*     */   {
/* 158 */     this.W[(this.wOff++)] = ((in[inOff] & 0xFF) << 56 | (in[(inOff + 1)] & 0xFF) << 48 | (in[(inOff + 2)] & 0xFF) << 40 | (in[(inOff + 3)] & 0xFF) << 32 | (in[(inOff + 4)] & 0xFF) << 24 | (in[(inOff + 5)] & 0xFF) << 16 | (in[(inOff + 6)] & 0xFF) << 8 | in[(inOff + 7)] & 0xFF);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 167 */     if (this.wOff == 16)
/*     */     {
/* 169 */       processBlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void unpackWord(long word, byte[] out, int outOff)
/*     */   {
/* 178 */     out[outOff] = ((byte)(int)(word >>> 56));
/* 179 */     out[(outOff + 1)] = ((byte)(int)(word >>> 48));
/* 180 */     out[(outOff + 2)] = ((byte)(int)(word >>> 40));
/* 181 */     out[(outOff + 3)] = ((byte)(int)(word >>> 32));
/* 182 */     out[(outOff + 4)] = ((byte)(int)(word >>> 24));
/* 183 */     out[(outOff + 5)] = ((byte)(int)(word >>> 16));
/* 184 */     out[(outOff + 6)] = ((byte)(int)(word >>> 8));
/* 185 */     out[(outOff + 7)] = ((byte)(int)word);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void adjustByteCounts()
/*     */   {
/* 194 */     if (this.byteCount1 > 2305843009213693951L)
/*     */     {
/* 196 */       this.byteCount2 += (this.byteCount1 >>> 61);
/* 197 */       this.byteCount1 &= 0x1FFFFFFFFFFFFFFF;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void processLength(long lowW, long hiW)
/*     */   {
/* 205 */     if (this.wOff > 14)
/*     */     {
/* 207 */       processBlock();
/*     */     }
/*     */     
/* 210 */     this.W[14] = hiW;
/* 211 */     this.W[15] = lowW;
/*     */   }
/*     */   
/*     */   protected void processBlock()
/*     */   {
/* 216 */     adjustByteCounts();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 221 */     for (int t = 16; t <= 79; t++)
/*     */     {
/* 223 */       this.W[t] = (Sigma1(this.W[(t - 2)]) + this.W[(t - 7)] + Sigma0(this.W[(t - 15)]) + this.W[(t - 16)]);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 229 */     long a = this.H1;
/* 230 */     long b = this.H2;
/* 231 */     long c = this.H3;
/* 232 */     long d = this.H4;
/* 233 */     long e = this.H5;
/* 234 */     long f = this.H6;
/* 235 */     long g = this.H7;
/* 236 */     long h = this.H8;
/*     */     
/* 238 */     for (int t = 0; t <= 79; t++)
/*     */     {
/*     */ 
/*     */ 
/* 242 */       long T1 = h + Sum1(e) + Ch(e, f, g) + K[t] + this.W[t];
/* 243 */       long T2 = Sum0(a) + Maj(a, b, c);
/* 244 */       h = g;
/* 245 */       g = f;
/* 246 */       f = e;
/* 247 */       e = d + T1;
/* 248 */       d = c;
/* 249 */       c = b;
/* 250 */       b = a;
/* 251 */       a = T1 + T2;
/*     */     }
/*     */     
/* 254 */     this.H1 += a;
/* 255 */     this.H2 += b;
/* 256 */     this.H3 += c;
/* 257 */     this.H4 += d;
/* 258 */     this.H5 += e;
/* 259 */     this.H6 += f;
/* 260 */     this.H7 += g;
/* 261 */     this.H8 += h;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 266 */     this.wOff = 0;
/* 267 */     for (int i = 0; i != this.W.length; i++)
/*     */     {
/* 269 */       this.W[i] = 0L;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private long rotateRight(long x, int n)
/*     */   {
/* 277 */     return x >>> n | x << 64 - n;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private long Ch(long x, long y, long z)
/*     */   {
/* 286 */     return x & y ^ (x ^ 0xFFFFFFFFFFFFFFFF) & z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private long Maj(long x, long y, long z)
/*     */   {
/* 294 */     return x & y ^ x & z ^ y & z;
/*     */   }
/*     */   
/*     */ 
/*     */   private long Sum0(long x)
/*     */   {
/* 300 */     return rotateRight(x, 28) ^ rotateRight(x, 34) ^ rotateRight(x, 39);
/*     */   }
/*     */   
/*     */ 
/*     */   private long Sum1(long x)
/*     */   {
/* 306 */     return rotateRight(x, 14) ^ rotateRight(x, 18) ^ rotateRight(x, 41);
/*     */   }
/*     */   
/*     */ 
/*     */   private long Sigma0(long x)
/*     */   {
/* 312 */     return rotateRight(x, 1) ^ rotateRight(x, 8) ^ x >>> 7;
/*     */   }
/*     */   
/*     */ 
/*     */   private long Sigma1(long x)
/*     */   {
/* 318 */     return rotateRight(x, 19) ^ rotateRight(x, 61) ^ x >>> 6;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 325 */   static final long[] K = { 4794697086780616226L, 8158064640168781261L, -5349999486874862801L, -1606136188198331460L, 4131703408338449720L, 6480981068601479193L, -7908458776815382629L, -6116909921290321640L, -2880145864133508542L, 1334009975649890238L, 2608012711638119052L, 6128411473006802146L, 8268148722764581231L, -9160688886553864527L, -7215885187991268811L, -4495734319001033068L, -1973867731355612462L, -1171420211273849373L, 1135362057144423861L, 2597628984639134821L, 3308224258029322869L, 5365058923640841347L, 6679025012923562964L, 8573033837759648693L, -7476448914759557205L, -6327057829258317296L, -5763719355590565569L, -4658551843659510044L, -4116276920077217854L, -3051310485924567259L, 489312712824947311L, 1452737877330783856L, 2861767655752347644L, 3322285676063803686L, 5560940570517711597L, 5996557281743188959L, 7280758554555802590L, 8532644243296465576L, -9096487096722542874L, -7894198246740708037L, -6719396339535248540L, -6333637450476146687L, -4446306890439682159L, -4076793802049405392L, -3345356375505022440L, -2983346525034927856L, -860691631967231958L, 1182934255886127544L, 1847814050463011016L, 2177327727835720531L, 2830643537854262169L, 3796741975233480872L, 4115178125766777443L, 5681478168544905931L, 6601373596472566643L, 7507060721942968483L, 8399075790359081724L, 8693463985226723168L, -8878714635349349518L, -8302665154208450068L, -8016688836872298968L, -6606660893046293015L, -4685533653050689259L, -4147400797238176981L, -3880063495543823972L, -3348786107499101689L, -1523767162380948706L, -757361751448694408L, 500013540394364858L, 748580250866718886L, 1242879168328830382L, 1977374033974150939L, 2944078676154940804L, 3659926193048069267L, 4368137639120453308L, 4836135668995329356L, 5532061633213252278L, 6448918945643986474L, 6902733635092675308L, 7801388544844847127L };
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/digests/LongDigest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */