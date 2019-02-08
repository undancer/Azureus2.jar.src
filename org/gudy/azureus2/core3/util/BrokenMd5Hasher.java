/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
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
/*     */ 
/*     */ public final class BrokenMd5Hasher
/*     */ {
/*  44 */   private final ByteBuffer buffer = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
/*  45 */   private int stateA = 1732584193;
/*  46 */   private int stateB = -271733879;
/*  47 */   private int stateC = -1732584194;
/*  48 */   private int stateD = 271733878;
/*  49 */   private long count = 0L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] calculateHash(byte[] data)
/*     */   {
/*  61 */     ByteBuffer input_buffer = ByteBuffer.wrap(data);
/*     */     
/*  63 */     reset();
/*     */     
/*  65 */     update(input_buffer);
/*     */     
/*  67 */     ByteBuffer result_buffer = ByteBuffer.allocate(16);
/*     */     
/*  69 */     finalDigest(result_buffer);
/*     */     
/*  71 */     byte[] result = new byte[16];
/*     */     
/*  73 */     result_buffer.position(0);
/*     */     
/*  75 */     for (int i = 0; i < result.length; i++)
/*     */     {
/*  77 */       result[i] = result_buffer.get();
/*     */     }
/*     */     
/*  80 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/*  87 */     this.stateA = 1732584193;
/*  88 */     this.stateB = -271733879;
/*  89 */     this.stateC = -1732584194;
/*  90 */     this.stateD = 271733878;
/*  91 */     this.count = 0L;
/*  92 */     this.buffer.rewind();
/*  93 */     for (int i = 0; i < 64; i++) {
/*  94 */       this.buffer.put((byte)0);
/*     */     }
/*  96 */     this.buffer.rewind();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(ByteBuffer input)
/*     */   {
/* 108 */     int inputLen = input.remaining();
/* 109 */     int index = (int)this.count & 0x3F;
/* 110 */     this.count += inputLen;
/* 111 */     int partLen = 64 - index;
/* 112 */     int i = 0;
/* 113 */     if (inputLen >= partLen) {
/* 114 */       if (index > 0) {
/* 115 */         int t = input.limit();
/* 116 */         input.limit(input.position() + partLen);
/* 117 */         this.buffer.put(input);
/* 118 */         this.buffer.rewind();
/* 119 */         input.limit(t);
/* 120 */         transform(this.buffer);
/* 121 */         this.buffer.rewind();
/* 122 */         i = partLen;
/* 123 */         index = partLen;
/*     */       }
/*     */       
/* 126 */       while (i + 63 < inputLen) {
/* 127 */         transform(input);
/* 128 */         i += 64;
/*     */       }
/*     */     }
/* 131 */     if (i < inputLen) {
/* 132 */       this.buffer.put(input);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void update(byte[] data)
/*     */   {
/* 140 */     update(ByteBuffer.wrap(data));
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getDigest()
/*     */   {
/* 146 */     ByteBuffer result_buffer = ByteBuffer.allocate(16);
/*     */     
/* 148 */     finalDigest(result_buffer);
/*     */     
/* 150 */     byte[] result = new byte[16];
/*     */     
/* 152 */     result_buffer.position(0);
/*     */     
/* 154 */     for (int i = 0; i < result.length; i++)
/*     */     {
/* 156 */       result[i] = result_buffer.get();
/*     */     }
/*     */     
/* 159 */     return result;
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
/*     */   public void finalDigest(ByteBuffer digest)
/*     */   {
/* 173 */     int index = (int)this.count & 0x3F;
/* 174 */     if (index < 56) {
/* 175 */       this.buffer.put((byte)Byte.MIN_VALUE);
/* 176 */       for (int i = index; i < 55; i++)
/* 177 */         this.buffer.put((byte)0);
/* 178 */       this.buffer.putLong(this.count << 3);
/* 179 */       this.buffer.rewind();
/* 180 */       transform(this.buffer);
/* 181 */       this.buffer.rewind();
/*     */     } else {
/* 183 */       this.buffer.put((byte)Byte.MIN_VALUE);
/* 184 */       for (int i = index; i < 63; i++)
/* 185 */         this.buffer.put((byte)0);
/* 186 */       this.buffer.rewind();
/* 187 */       transform(this.buffer);
/* 188 */       this.buffer.rewind();
/* 189 */       for (int i = 0; i < 56; i++)
/* 190 */         this.buffer.put((byte)0);
/* 191 */       this.buffer.putLong(this.count << 3);
/* 192 */       this.buffer.rewind();
/* 193 */       transform(this.buffer);
/* 194 */       this.buffer.rewind();
/*     */     }
/*     */     
/* 197 */     digest.putInt(this.stateA);
/* 198 */     digest.putInt(this.stateB);
/* 199 */     digest.putInt(this.stateC);
/* 200 */     digest.putInt(this.stateD);
/*     */     
/* 202 */     reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void transform(ByteBuffer block)
/*     */   {
/* 209 */     int a = this.stateA;
/* 210 */     int b = this.stateB;
/* 211 */     int c = this.stateC;
/* 212 */     int d = this.stateD;
/* 213 */     long e = block.getLong();
/* 214 */     long f = block.getLong();
/* 215 */     long g = block.getLong();
/* 216 */     long h = block.getLong();
/* 217 */     long i = block.getLong();
/* 218 */     long j = block.getLong();
/* 219 */     long k = block.getLong();
/* 220 */     long l = block.getLong();
/*     */     
/* 222 */     a = FF(a, b, c, d, (int)e, 7, -680876936);
/* 223 */     d = FF(d, a, b, c, (int)(e >>> 32), 12, -389564586);
/* 224 */     c = FF(c, d, a, b, (int)f, 17, 606105819);
/* 225 */     b = FF(b, c, d, a, (int)(f >>> 32), 22, -1044525330);
/* 226 */     a = FF(a, b, c, d, (int)g, 7, -176418897);
/* 227 */     d = FF(d, a, b, c, (int)(g >>> 32), 12, 1200080426);
/* 228 */     c = FF(c, d, a, b, (int)h, 17, -1473231341);
/* 229 */     b = FF(b, c, d, a, (int)(h >>> 32), 22, -45705983);
/* 230 */     a = FF(a, b, c, d, (int)i, 7, 1770035416);
/* 231 */     d = FF(d, a, b, c, (int)(i >>> 32), 12, -1958414417);
/* 232 */     c = FF(c, d, a, b, (int)j, 17, -42063);
/* 233 */     b = FF(b, c, d, a, (int)(j >>> 32), 22, -1990404162);
/* 234 */     a = FF(a, b, c, d, (int)k, 7, 1804603682);
/* 235 */     d = FF(d, a, b, c, (int)(k >>> 32), 12, -40341101);
/* 236 */     c = FF(c, d, a, b, (int)l, 17, -1502002290);
/* 237 */     b = FF(b, c, d, a, (int)(l >>> 32), 22, 1236535329);
/*     */     
/* 239 */     a = GG(a, b, c, d, (int)(e >>> 32), 5, -165796510);
/* 240 */     d = GG(d, a, b, c, (int)h, 9, -1069501632);
/* 241 */     c = GG(c, d, a, b, (int)(j >>> 32), 14, 643717713);
/* 242 */     b = GG(b, c, d, a, (int)e, 20, -373897302);
/* 243 */     a = GG(a, b, c, d, (int)(g >>> 32), 5, -701558691);
/* 244 */     d = GG(d, a, b, c, (int)j, 9, 38016083);
/* 245 */     c = GG(c, d, a, b, (int)(l >>> 32), 14, -660478335);
/* 246 */     b = GG(b, c, d, a, (int)g, 20, -405537848);
/* 247 */     a = GG(a, b, c, d, (int)(i >>> 32), 5, 568446438);
/* 248 */     d = GG(d, a, b, c, (int)l, 9, -1019803690);
/* 249 */     c = GG(c, d, a, b, (int)(f >>> 32), 14, -187363961);
/* 250 */     b = GG(b, c, d, a, (int)i, 20, 1163531501);
/* 251 */     a = GG(a, b, c, d, (int)(k >>> 32), 5, -1444681467);
/* 252 */     d = GG(d, a, b, c, (int)f, 9, -51403784);
/* 253 */     c = GG(c, d, a, b, (int)(h >>> 32), 14, 1735328473);
/* 254 */     b = GG(b, c, d, a, (int)k, 20, -1926607734);
/*     */     
/* 256 */     a = HH(a, b, c, d, (int)(g >>> 32), 4, -378558);
/* 257 */     d = HH(d, a, b, c, (int)i, 11, -2022574463);
/* 258 */     c = HH(c, d, a, b, (int)(j >>> 32), 16, 1839030562);
/* 259 */     b = HH(b, c, d, a, (int)l, 23, -35309556);
/* 260 */     a = HH(a, b, c, d, (int)(e >>> 32), 4, -1530992060);
/* 261 */     d = HH(d, a, b, c, (int)g, 11, 1272893353);
/* 262 */     c = HH(c, d, a, b, (int)(h >>> 32), 16, -155497632);
/* 263 */     b = HH(b, c, d, a, (int)j, 23, -1094730640);
/* 264 */     a = HH(a, b, c, d, (int)(k >>> 32), 4, 681279174);
/* 265 */     d = HH(d, a, b, c, (int)e, 11, -358537222);
/* 266 */     c = HH(c, d, a, b, (int)(f >>> 32), 16, -722521979);
/* 267 */     b = HH(b, c, d, a, (int)h, 23, 76029189);
/* 268 */     a = HH(a, b, c, d, (int)(i >>> 32), 4, -640364487);
/* 269 */     d = HH(d, a, b, c, (int)k, 11, -421815835);
/* 270 */     c = HH(c, d, a, b, (int)(l >>> 32), 16, 530742520);
/* 271 */     b = HH(b, c, d, a, (int)f, 23, -995338651);
/*     */     
/* 273 */     a = II(a, b, c, d, (int)e, 6, -198630844);
/* 274 */     d = II(d, a, b, c, (int)(h >>> 32), 10, 1126891415);
/* 275 */     c = II(c, d, a, b, (int)l, 15, -1416354905);
/* 276 */     b = II(b, c, d, a, (int)(g >>> 32), 21, -57434055);
/* 277 */     a = II(a, b, c, d, (int)k, 6, 1700485571);
/* 278 */     d = II(d, a, b, c, (int)(f >>> 32), 10, -1894986606);
/* 279 */     c = II(c, d, a, b, (int)j, 15, -1051523);
/* 280 */     b = II(b, c, d, a, (int)(e >>> 32), 21, -2054922799);
/* 281 */     a = II(a, b, c, d, (int)i, 6, 1873313359);
/* 282 */     d = II(d, a, b, c, (int)(l >>> 32), 10, -30611744);
/* 283 */     c = II(c, d, a, b, (int)h, 15, -1560198380);
/* 284 */     b = II(b, c, d, a, (int)(k >>> 32), 21, 1309151649);
/* 285 */     a = II(a, b, c, d, (int)g, 6, -145523070);
/* 286 */     d = II(d, a, b, c, (int)(j >>> 32), 10, -1120210379);
/* 287 */     c = II(c, d, a, b, (int)f, 15, 718787259);
/* 288 */     b = II(b, c, d, a, (int)(i >>> 32), 21, -343485551);
/*     */     
/* 290 */     this.stateA += a;
/* 291 */     this.stateB += b;
/* 292 */     this.stateC += c;
/* 293 */     this.stateD += d;
/*     */   }
/*     */   
/*     */   private static int FF(int a, int b, int c, int d, int x, int s, int t) {
/* 297 */     int r = a + x + t + (d ^ b & (c ^ d));
/* 298 */     return (r << s | r >>> 32 - s) + b;
/*     */   }
/*     */   
/*     */   private static int GG(int a, int b, int c, int d, int x, int s, int t) {
/* 302 */     int r = a + x + t + (c ^ d & (b ^ c));
/* 303 */     return (r << s | r >>> 32 - s) + b;
/*     */   }
/*     */   
/*     */   private static int HH(int a, int b, int c, int d, int x, int s, int t) {
/* 307 */     int r = a + x + t + (b ^ c ^ d);
/* 308 */     return (r << s | r >>> 32 - s) + b;
/*     */   }
/*     */   
/*     */   private static int II(int a, int b, int c, int d, int x, int s, int t) {
/* 312 */     int r = a + x + t + (c ^ (b | d ^ 0xFFFFFFFF));
/* 313 */     return (r << s | r >>> 32 - s) + b;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/BrokenMd5Hasher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */