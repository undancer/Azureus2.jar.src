/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.nio.ByteBuffer;
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
/*     */ public final class SHA1
/*     */ {
/*     */   private int h0;
/*     */   private int h1;
/*     */   private int h2;
/*     */   private int h3;
/*     */   private int h4;
/*     */   private final ByteBuffer finalBuffer;
/*     */   private final ByteBuffer saveBuffer;
/*     */   private int s0;
/*     */   private int s1;
/*     */   private int s2;
/*     */   private int s3;
/*     */   private int s4;
/*     */   private long length;
/*     */   private long saveLength;
/*     */   private static final int cacheSize = 4096;
/*     */   private byte[] cacheBlock;
/*     */   
/*     */   public SHA1()
/*     */   {
/*  45 */     this.finalBuffer = ByteBuffer.allocate(64);
/*  46 */     this.finalBuffer.position(0);
/*  47 */     this.finalBuffer.limit(64);
/*     */     
/*  49 */     this.saveBuffer = ByteBuffer.allocate(64);
/*  50 */     this.saveBuffer.position(0);
/*  51 */     this.saveBuffer.limit(64);
/*     */     
/*  53 */     reset();
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
/*     */   private final void transform(byte[] ar, int offset)
/*     */   {
/*  66 */     int w0 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  67 */     int w1 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  68 */     int w2 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  69 */     int w3 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  70 */     int w4 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  71 */     int w5 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  72 */     int w6 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  73 */     int w7 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  74 */     int w8 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  75 */     int w9 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  76 */     int w10 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  77 */     int w11 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  78 */     int w12 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  79 */     int w13 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  80 */     int w14 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[(offset++)] & 0xFF;
/*  81 */     int w15 = (ar[(offset++)] & 0xFF) << 24 | (ar[(offset++)] & 0xFF) << 16 | (ar[(offset++)] & 0xFF) << 8 | ar[offset] & 0xFF;
/*     */     
/*  83 */     int a = this.h0;int b = this.h1;int c = this.h2;int d = this.h3;int e = this.h4;
/*  84 */     e += (a << 5 | a >>> 27) + w0 + (b & c | (b ^ 0xFFFFFFFF) & d) + 1518500249;
/*  85 */     b = b << 30 | b >>> 2;
/*  86 */     d += (e << 5 | e >>> 27) + w1 + (a & b | (a ^ 0xFFFFFFFF) & c) + 1518500249;
/*  87 */     a = a << 30 | a >>> 2;
/*  88 */     c += (d << 5 | d >>> 27) + w2 + (e & a | (e ^ 0xFFFFFFFF) & b) + 1518500249;
/*  89 */     e = e << 30 | e >>> 2;
/*  90 */     b += (c << 5 | c >>> 27) + w3 + (d & e | (d ^ 0xFFFFFFFF) & a) + 1518500249;
/*  91 */     d = d << 30 | d >>> 2;
/*  92 */     a += (b << 5 | b >>> 27) + w4 + (c & d | (c ^ 0xFFFFFFFF) & e) + 1518500249;
/*  93 */     c = c << 30 | c >>> 2;
/*  94 */     e += (a << 5 | a >>> 27) + w5 + (b & c | (b ^ 0xFFFFFFFF) & d) + 1518500249;
/*  95 */     b = b << 30 | b >>> 2;
/*  96 */     d += (e << 5 | e >>> 27) + w6 + (a & b | (a ^ 0xFFFFFFFF) & c) + 1518500249;
/*  97 */     a = a << 30 | a >>> 2;
/*  98 */     c += (d << 5 | d >>> 27) + w7 + (e & a | (e ^ 0xFFFFFFFF) & b) + 1518500249;
/*  99 */     e = e << 30 | e >>> 2;
/* 100 */     b += (c << 5 | c >>> 27) + w8 + (d & e | (d ^ 0xFFFFFFFF) & a) + 1518500249;
/* 101 */     d = d << 30 | d >>> 2;
/* 102 */     a += (b << 5 | b >>> 27) + w9 + (c & d | (c ^ 0xFFFFFFFF) & e) + 1518500249;
/* 103 */     c = c << 30 | c >>> 2;
/* 104 */     e += (a << 5 | a >>> 27) + w10 + (b & c | (b ^ 0xFFFFFFFF) & d) + 1518500249;
/* 105 */     b = b << 30 | b >>> 2;
/* 106 */     d += (e << 5 | e >>> 27) + w11 + (a & b | (a ^ 0xFFFFFFFF) & c) + 1518500249;
/* 107 */     a = a << 30 | a >>> 2;
/* 108 */     c += (d << 5 | d >>> 27) + w12 + (e & a | (e ^ 0xFFFFFFFF) & b) + 1518500249;
/* 109 */     e = e << 30 | e >>> 2;
/* 110 */     b += (c << 5 | c >>> 27) + w13 + (d & e | (d ^ 0xFFFFFFFF) & a) + 1518500249;
/* 111 */     d = d << 30 | d >>> 2;
/* 112 */     a += (b << 5 | b >>> 27) + w14 + (c & d | (c ^ 0xFFFFFFFF) & e) + 1518500249;
/* 113 */     c = c << 30 | c >>> 2;
/* 114 */     e += (a << 5 | a >>> 27) + w15 + (b & c | (b ^ 0xFFFFFFFF) & d) + 1518500249;
/* 115 */     b = b << 30 | b >>> 2;
/* 116 */     w0 = w13 ^ w8 ^ w2 ^ w0;w0 = w0 << 1 | w0 >>> 31;
/* 117 */     d += (e << 5 | e >>> 27) + w0 + (a & b | (a ^ 0xFFFFFFFF) & c) + 1518500249;
/* 118 */     a = a << 30 | a >>> 2;
/* 119 */     w1 = w14 ^ w9 ^ w3 ^ w1;w1 = w1 << 1 | w1 >>> 31;
/* 120 */     c += (d << 5 | d >>> 27) + w1 + (e & a | (e ^ 0xFFFFFFFF) & b) + 1518500249;
/* 121 */     e = e << 30 | e >>> 2;
/* 122 */     w2 = w15 ^ w10 ^ w4 ^ w2;w2 = w2 << 1 | w2 >>> 31;
/* 123 */     b += (c << 5 | c >>> 27) + w2 + (d & e | (d ^ 0xFFFFFFFF) & a) + 1518500249;
/* 124 */     d = d << 30 | d >>> 2;
/* 125 */     w3 = w0 ^ w11 ^ w5 ^ w3;w3 = w3 << 1 | w3 >>> 31;
/* 126 */     a += (b << 5 | b >>> 27) + w3 + (c & d | (c ^ 0xFFFFFFFF) & e) + 1518500249;
/* 127 */     c = c << 30 | c >>> 2;
/* 128 */     w4 = w1 ^ w12 ^ w6 ^ w4;w4 = w4 << 1 | w4 >>> 31;
/* 129 */     e += (a << 5 | a >>> 27) + w4 + (b ^ c ^ d) + 1859775393;
/* 130 */     b = b << 30 | b >>> 2;
/* 131 */     w5 = w2 ^ w13 ^ w7 ^ w5;w5 = w5 << 1 | w5 >>> 31;
/* 132 */     d += (e << 5 | e >>> 27) + w5 + (a ^ b ^ c) + 1859775393;
/* 133 */     a = a << 30 | a >>> 2;
/* 134 */     w6 = w3 ^ w14 ^ w8 ^ w6;w6 = w6 << 1 | w6 >>> 31;
/* 135 */     c += (d << 5 | d >>> 27) + w6 + (e ^ a ^ b) + 1859775393;
/* 136 */     e = e << 30 | e >>> 2;
/* 137 */     w7 = w4 ^ w15 ^ w9 ^ w7;w7 = w7 << 1 | w7 >>> 31;
/* 138 */     b += (c << 5 | c >>> 27) + w7 + (d ^ e ^ a) + 1859775393;
/* 139 */     d = d << 30 | d >>> 2;
/* 140 */     w8 = w5 ^ w0 ^ w10 ^ w8;w8 = w8 << 1 | w8 >>> 31;
/* 141 */     a += (b << 5 | b >>> 27) + w8 + (c ^ d ^ e) + 1859775393;
/* 142 */     c = c << 30 | c >>> 2;
/* 143 */     w9 = w6 ^ w1 ^ w11 ^ w9;w9 = w9 << 1 | w9 >>> 31;
/* 144 */     e += (a << 5 | a >>> 27) + w9 + (b ^ c ^ d) + 1859775393;
/* 145 */     b = b << 30 | b >>> 2;
/* 146 */     w10 = w7 ^ w2 ^ w12 ^ w10;w10 = w10 << 1 | w10 >>> 31;
/* 147 */     d += (e << 5 | e >>> 27) + w10 + (a ^ b ^ c) + 1859775393;
/* 148 */     a = a << 30 | a >>> 2;
/* 149 */     w11 = w8 ^ w3 ^ w13 ^ w11;w11 = w11 << 1 | w11 >>> 31;
/* 150 */     c += (d << 5 | d >>> 27) + w11 + (e ^ a ^ b) + 1859775393;
/* 151 */     e = e << 30 | e >>> 2;
/* 152 */     w12 = w9 ^ w4 ^ w14 ^ w12;w12 = w12 << 1 | w12 >>> 31;
/* 153 */     b += (c << 5 | c >>> 27) + w12 + (d ^ e ^ a) + 1859775393;
/* 154 */     d = d << 30 | d >>> 2;
/* 155 */     w13 = w10 ^ w5 ^ w15 ^ w13;w13 = w13 << 1 | w13 >>> 31;
/* 156 */     a += (b << 5 | b >>> 27) + w13 + (c ^ d ^ e) + 1859775393;
/* 157 */     c = c << 30 | c >>> 2;
/* 158 */     w14 = w11 ^ w6 ^ w0 ^ w14;w14 = w14 << 1 | w14 >>> 31;
/* 159 */     e += (a << 5 | a >>> 27) + w14 + (b ^ c ^ d) + 1859775393;
/* 160 */     b = b << 30 | b >>> 2;
/* 161 */     w15 = w12 ^ w7 ^ w1 ^ w15;w15 = w15 << 1 | w15 >>> 31;
/* 162 */     d += (e << 5 | e >>> 27) + w15 + (a ^ b ^ c) + 1859775393;
/* 163 */     a = a << 30 | a >>> 2;
/* 164 */     w0 = w13 ^ w8 ^ w2 ^ w0;w0 = w0 << 1 | w0 >>> 31;
/* 165 */     c += (d << 5 | d >>> 27) + w0 + (e ^ a ^ b) + 1859775393;
/* 166 */     e = e << 30 | e >>> 2;
/* 167 */     w1 = w14 ^ w9 ^ w3 ^ w1;w1 = w1 << 1 | w1 >>> 31;
/* 168 */     b += (c << 5 | c >>> 27) + w1 + (d ^ e ^ a) + 1859775393;
/* 169 */     d = d << 30 | d >>> 2;
/* 170 */     w2 = w15 ^ w10 ^ w4 ^ w2;w2 = w2 << 1 | w2 >>> 31;
/* 171 */     a += (b << 5 | b >>> 27) + w2 + (c ^ d ^ e) + 1859775393;
/* 172 */     c = c << 30 | c >>> 2;
/* 173 */     w3 = w0 ^ w11 ^ w5 ^ w3;w3 = w3 << 1 | w3 >>> 31;
/* 174 */     e += (a << 5 | a >>> 27) + w3 + (b ^ c ^ d) + 1859775393;
/* 175 */     b = b << 30 | b >>> 2;
/* 176 */     w4 = w1 ^ w12 ^ w6 ^ w4;w4 = w4 << 1 | w4 >>> 31;
/* 177 */     d += (e << 5 | e >>> 27) + w4 + (a ^ b ^ c) + 1859775393;
/* 178 */     a = a << 30 | a >>> 2;
/* 179 */     w5 = w2 ^ w13 ^ w7 ^ w5;w5 = w5 << 1 | w5 >>> 31;
/* 180 */     c += (d << 5 | d >>> 27) + w5 + (e ^ a ^ b) + 1859775393;
/* 181 */     e = e << 30 | e >>> 2;
/* 182 */     w6 = w3 ^ w14 ^ w8 ^ w6;w6 = w6 << 1 | w6 >>> 31;
/* 183 */     b += (c << 5 | c >>> 27) + w6 + (d ^ e ^ a) + 1859775393;
/* 184 */     d = d << 30 | d >>> 2;
/* 185 */     w7 = w4 ^ w15 ^ w9 ^ w7;w7 = w7 << 1 | w7 >>> 31;
/* 186 */     a += (b << 5 | b >>> 27) + w7 + (c ^ d ^ e) + 1859775393;
/* 187 */     c = c << 30 | c >>> 2;
/* 188 */     w8 = w5 ^ w0 ^ w10 ^ w8;w8 = w8 << 1 | w8 >>> 31;
/* 189 */     e += (a << 5 | a >>> 27) + w8 + (b & c | b & d | c & d) + -1894007588;
/* 190 */     b = b << 30 | b >>> 2;
/* 191 */     w9 = w6 ^ w1 ^ w11 ^ w9;w9 = w9 << 1 | w9 >>> 31;
/* 192 */     d += (e << 5 | e >>> 27) + w9 + (a & b | a & c | b & c) + -1894007588;
/* 193 */     a = a << 30 | a >>> 2;
/* 194 */     w10 = w7 ^ w2 ^ w12 ^ w10;w10 = w10 << 1 | w10 >>> 31;
/* 195 */     c += (d << 5 | d >>> 27) + w10 + (e & a | e & b | a & b) + -1894007588;
/* 196 */     e = e << 30 | e >>> 2;
/* 197 */     w11 = w8 ^ w3 ^ w13 ^ w11;w11 = w11 << 1 | w11 >>> 31;
/* 198 */     b += (c << 5 | c >>> 27) + w11 + (d & e | d & a | e & a) + -1894007588;
/* 199 */     d = d << 30 | d >>> 2;
/* 200 */     w12 = w9 ^ w4 ^ w14 ^ w12;w12 = w12 << 1 | w12 >>> 31;
/* 201 */     a += (b << 5 | b >>> 27) + w12 + (c & d | c & e | d & e) + -1894007588;
/* 202 */     c = c << 30 | c >>> 2;
/* 203 */     w13 = w10 ^ w5 ^ w15 ^ w13;w13 = w13 << 1 | w13 >>> 31;
/* 204 */     e += (a << 5 | a >>> 27) + w13 + (b & c | b & d | c & d) + -1894007588;
/* 205 */     b = b << 30 | b >>> 2;
/* 206 */     w14 = w11 ^ w6 ^ w0 ^ w14;w14 = w14 << 1 | w14 >>> 31;
/* 207 */     d += (e << 5 | e >>> 27) + w14 + (a & b | a & c | b & c) + -1894007588;
/* 208 */     a = a << 30 | a >>> 2;
/* 209 */     w15 = w12 ^ w7 ^ w1 ^ w15;w15 = w15 << 1 | w15 >>> 31;
/* 210 */     c += (d << 5 | d >>> 27) + w15 + (e & a | e & b | a & b) + -1894007588;
/* 211 */     e = e << 30 | e >>> 2;
/* 212 */     w0 = w13 ^ w8 ^ w2 ^ w0;w0 = w0 << 1 | w0 >>> 31;
/* 213 */     b += (c << 5 | c >>> 27) + w0 + (d & e | d & a | e & a) + -1894007588;
/* 214 */     d = d << 30 | d >>> 2;
/* 215 */     w1 = w14 ^ w9 ^ w3 ^ w1;w1 = w1 << 1 | w1 >>> 31;
/* 216 */     a += (b << 5 | b >>> 27) + w1 + (c & d | c & e | d & e) + -1894007588;
/* 217 */     c = c << 30 | c >>> 2;
/* 218 */     w2 = w15 ^ w10 ^ w4 ^ w2;w2 = w2 << 1 | w2 >>> 31;
/* 219 */     e += (a << 5 | a >>> 27) + w2 + (b & c | b & d | c & d) + -1894007588;
/* 220 */     b = b << 30 | b >>> 2;
/* 221 */     w3 = w0 ^ w11 ^ w5 ^ w3;w3 = w3 << 1 | w3 >>> 31;
/* 222 */     d += (e << 5 | e >>> 27) + w3 + (a & b | a & c | b & c) + -1894007588;
/* 223 */     a = a << 30 | a >>> 2;
/* 224 */     w4 = w1 ^ w12 ^ w6 ^ w4;w4 = w4 << 1 | w4 >>> 31;
/* 225 */     c += (d << 5 | d >>> 27) + w4 + (e & a | e & b | a & b) + -1894007588;
/* 226 */     e = e << 30 | e >>> 2;
/* 227 */     w5 = w2 ^ w13 ^ w7 ^ w5;w5 = w5 << 1 | w5 >>> 31;
/* 228 */     b += (c << 5 | c >>> 27) + w5 + (d & e | d & a | e & a) + -1894007588;
/* 229 */     d = d << 30 | d >>> 2;
/* 230 */     w6 = w3 ^ w14 ^ w8 ^ w6;w6 = w6 << 1 | w6 >>> 31;
/* 231 */     a += (b << 5 | b >>> 27) + w6 + (c & d | c & e | d & e) + -1894007588;
/* 232 */     c = c << 30 | c >>> 2;
/* 233 */     w7 = w4 ^ w15 ^ w9 ^ w7;w7 = w7 << 1 | w7 >>> 31;
/* 234 */     e += (a << 5 | a >>> 27) + w7 + (b & c | b & d | c & d) + -1894007588;
/* 235 */     b = b << 30 | b >>> 2;
/* 236 */     w8 = w5 ^ w0 ^ w10 ^ w8;w8 = w8 << 1 | w8 >>> 31;
/* 237 */     d += (e << 5 | e >>> 27) + w8 + (a & b | a & c | b & c) + -1894007588;
/* 238 */     a = a << 30 | a >>> 2;
/* 239 */     w9 = w6 ^ w1 ^ w11 ^ w9;w9 = w9 << 1 | w9 >>> 31;
/* 240 */     c += (d << 5 | d >>> 27) + w9 + (e & a | e & b | a & b) + -1894007588;
/* 241 */     e = e << 30 | e >>> 2;
/* 242 */     w10 = w7 ^ w2 ^ w12 ^ w10;w10 = w10 << 1 | w10 >>> 31;
/* 243 */     b += (c << 5 | c >>> 27) + w10 + (d & e | d & a | e & a) + -1894007588;
/* 244 */     d = d << 30 | d >>> 2;
/* 245 */     w11 = w8 ^ w3 ^ w13 ^ w11;w11 = w11 << 1 | w11 >>> 31;
/* 246 */     a += (b << 5 | b >>> 27) + w11 + (c & d | c & e | d & e) + -1894007588;
/* 247 */     c = c << 30 | c >>> 2;
/* 248 */     w12 = w9 ^ w4 ^ w14 ^ w12;w12 = w12 << 1 | w12 >>> 31;
/* 249 */     e += (a << 5 | a >>> 27) + w12 + (b ^ c ^ d) + -899497514;
/* 250 */     b = b << 30 | b >>> 2;
/* 251 */     w13 = w10 ^ w5 ^ w15 ^ w13;w13 = w13 << 1 | w13 >>> 31;
/* 252 */     d += (e << 5 | e >>> 27) + w13 + (a ^ b ^ c) + -899497514;
/* 253 */     a = a << 30 | a >>> 2;
/* 254 */     w14 = w11 ^ w6 ^ w0 ^ w14;w14 = w14 << 1 | w14 >>> 31;
/* 255 */     c += (d << 5 | d >>> 27) + w14 + (e ^ a ^ b) + -899497514;
/* 256 */     e = e << 30 | e >>> 2;
/* 257 */     w15 = w12 ^ w7 ^ w1 ^ w15;w15 = w15 << 1 | w15 >>> 31;
/* 258 */     b += (c << 5 | c >>> 27) + w15 + (d ^ e ^ a) + -899497514;
/* 259 */     d = d << 30 | d >>> 2;
/* 260 */     w0 = w13 ^ w8 ^ w2 ^ w0;w0 = w0 << 1 | w0 >>> 31;
/* 261 */     a += (b << 5 | b >>> 27) + w0 + (c ^ d ^ e) + -899497514;
/* 262 */     c = c << 30 | c >>> 2;
/* 263 */     w1 = w14 ^ w9 ^ w3 ^ w1;w1 = w1 << 1 | w1 >>> 31;
/* 264 */     e += (a << 5 | a >>> 27) + w1 + (b ^ c ^ d) + -899497514;
/* 265 */     b = b << 30 | b >>> 2;
/* 266 */     w2 = w15 ^ w10 ^ w4 ^ w2;w2 = w2 << 1 | w2 >>> 31;
/* 267 */     d += (e << 5 | e >>> 27) + w2 + (a ^ b ^ c) + -899497514;
/* 268 */     a = a << 30 | a >>> 2;
/* 269 */     w3 = w0 ^ w11 ^ w5 ^ w3;w3 = w3 << 1 | w3 >>> 31;
/* 270 */     c += (d << 5 | d >>> 27) + w3 + (e ^ a ^ b) + -899497514;
/* 271 */     e = e << 30 | e >>> 2;
/* 272 */     w4 = w1 ^ w12 ^ w6 ^ w4;w4 = w4 << 1 | w4 >>> 31;
/* 273 */     b += (c << 5 | c >>> 27) + w4 + (d ^ e ^ a) + -899497514;
/* 274 */     d = d << 30 | d >>> 2;
/* 275 */     w5 = w2 ^ w13 ^ w7 ^ w5;w5 = w5 << 1 | w5 >>> 31;
/* 276 */     a += (b << 5 | b >>> 27) + w5 + (c ^ d ^ e) + -899497514;
/* 277 */     c = c << 30 | c >>> 2;
/* 278 */     w6 = w3 ^ w14 ^ w8 ^ w6;w6 = w6 << 1 | w6 >>> 31;
/* 279 */     e += (a << 5 | a >>> 27) + w6 + (b ^ c ^ d) + -899497514;
/* 280 */     b = b << 30 | b >>> 2;
/* 281 */     w7 = w4 ^ w15 ^ w9 ^ w7;w7 = w7 << 1 | w7 >>> 31;
/* 282 */     d += (e << 5 | e >>> 27) + w7 + (a ^ b ^ c) + -899497514;
/* 283 */     a = a << 30 | a >>> 2;
/* 284 */     w8 = w5 ^ w0 ^ w10 ^ w8;w8 = w8 << 1 | w8 >>> 31;
/* 285 */     c += (d << 5 | d >>> 27) + w8 + (e ^ a ^ b) + -899497514;
/* 286 */     e = e << 30 | e >>> 2;
/* 287 */     w9 = w6 ^ w1 ^ w11 ^ w9;w9 = w9 << 1 | w9 >>> 31;
/* 288 */     b += (c << 5 | c >>> 27) + w9 + (d ^ e ^ a) + -899497514;
/* 289 */     d = d << 30 | d >>> 2;
/* 290 */     w10 = w7 ^ w2 ^ w12 ^ w10;w10 = w10 << 1 | w10 >>> 31;
/* 291 */     a += (b << 5 | b >>> 27) + w10 + (c ^ d ^ e) + -899497514;
/* 292 */     c = c << 30 | c >>> 2;
/* 293 */     w11 = w8 ^ w3 ^ w13 ^ w11;w11 = w11 << 1 | w11 >>> 31;
/* 294 */     e += (a << 5 | a >>> 27) + w11 + (b ^ c ^ d) + -899497514;
/* 295 */     b = b << 30 | b >>> 2;
/* 296 */     w12 = w9 ^ w4 ^ w14 ^ w12;w12 = w12 << 1 | w12 >>> 31;
/* 297 */     d += (e << 5 | e >>> 27) + w12 + (a ^ b ^ c) + -899497514;
/* 298 */     a = a << 30 | a >>> 2;
/* 299 */     w13 = w10 ^ w5 ^ w15 ^ w13;w13 = w13 << 1 | w13 >>> 31;
/* 300 */     c += (d << 5 | d >>> 27) + w13 + (e ^ a ^ b) + -899497514;
/* 301 */     e = e << 30 | e >>> 2;
/* 302 */     w14 = w11 ^ w6 ^ w0 ^ w14;w14 = w14 << 1 | w14 >>> 31;
/* 303 */     b += (c << 5 | c >>> 27) + w14 + (d ^ e ^ a) + -899497514;
/* 304 */     d = d << 30 | d >>> 2;
/* 305 */     w15 = w12 ^ w7 ^ w1 ^ w15;w15 = w15 << 1 | w15 >>> 31;
/* 306 */     a += (b << 5 | b >>> 27) + w15 + (c ^ d ^ e) + -899497514;
/* 307 */     c = c << 30 | c >>> 2;
/*     */     
/* 309 */     this.h0 += a;
/* 310 */     this.h1 += b;
/* 311 */     this.h2 += c;
/* 312 */     this.h3 += d;
/* 313 */     this.h4 += e;
/*     */   }
/*     */   
/*     */   private void completeFinalBuffer(ByteBuffer buffer)
/*     */   {
/* 318 */     if (this.finalBuffer.position() == 0) {
/* 319 */       return;
/*     */     }
/* 321 */     while ((buffer.remaining() > 0) && (this.finalBuffer.remaining() > 0)) {
/* 322 */       this.finalBuffer.put(buffer.get());
/*     */     }
/*     */     
/* 325 */     if (this.finalBuffer.remaining() == 0) {
/* 326 */       transform(this.finalBuffer.array(), 0);
/* 327 */       this.finalBuffer.rewind();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 338 */     this.h0 = 1732584193;
/* 339 */     this.h1 = -271733879;
/* 340 */     this.h2 = -1732584194;
/* 341 */     this.h3 = 271733878;
/* 342 */     this.h4 = -1009589776;
/*     */     
/* 344 */     this.length = 0L;
/*     */     
/* 346 */     this.finalBuffer.clear();
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
/*     */   public void update(ByteBuffer buffer)
/*     */   {
/* 361 */     this.length += buffer.remaining();
/*     */     
/* 363 */     int position = buffer.position();
/*     */     
/*     */ 
/* 366 */     completeFinalBuffer(buffer);
/*     */     
/* 368 */     if ((!buffer.hasArray()) || (buffer.isDirect()))
/*     */     {
/* 370 */       if (this.cacheBlock == null)
/* 371 */         this.cacheBlock = new byte['က']; }
/* 372 */     while (buffer.remaining() >= 64)
/*     */     {
/* 374 */       int toProcess = Math.min(buffer.remaining() - buffer.remaining() % 64, 4096);
/* 375 */       buffer.get(this.cacheBlock, 0, toProcess);
/* 376 */       for (int i = 0; i < toProcess; i += 64)
/* 377 */         transform(this.cacheBlock, i);
/* 378 */       continue;
/*     */       
/*     */ 
/* 381 */       int endPos = buffer.position() + buffer.remaining() - buffer.remaining() % 64;
/* 382 */       int internalEndPos = endPos + buffer.arrayOffset();
/* 383 */       for (int i = buffer.arrayOffset() + buffer.position(); i < internalEndPos; i += 64)
/* 384 */         transform(buffer.array(), i);
/* 385 */       buffer.position(endPos);
/*     */     }
/*     */     
/*     */ 
/* 389 */     if (buffer.remaining() != 0) {
/* 390 */       this.finalBuffer.put(buffer);
/*     */     }
/*     */     
/* 393 */     buffer.position(position);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] digest()
/*     */   {
/* 402 */     byte[] result = new byte[20];
/*     */     
/* 404 */     this.finalBuffer.put((byte)Byte.MIN_VALUE);
/* 405 */     if (this.finalBuffer.remaining() < 8) {
/* 406 */       while (this.finalBuffer.remaining() > 0) {
/* 407 */         this.finalBuffer.put((byte)0);
/*     */       }
/* 409 */       this.finalBuffer.position(0);
/* 410 */       transform(this.finalBuffer.array(), 0);
/* 411 */       this.finalBuffer.position(0);
/*     */     }
/*     */     
/* 414 */     while (this.finalBuffer.remaining() > 8) {
/* 415 */       this.finalBuffer.put((byte)0);
/*     */     }
/*     */     
/* 418 */     this.finalBuffer.putLong(this.length << 3);
/* 419 */     this.finalBuffer.position(0);
/* 420 */     transform(this.finalBuffer.array(), 0);
/*     */     
/* 422 */     this.finalBuffer.position(0);
/* 423 */     this.finalBuffer.putInt(this.h0);
/* 424 */     this.finalBuffer.putInt(this.h1);
/* 425 */     this.finalBuffer.putInt(this.h2);
/* 426 */     this.finalBuffer.putInt(this.h3);
/* 427 */     this.finalBuffer.putInt(this.h4);
/* 428 */     this.finalBuffer.rewind();
/*     */     
/* 430 */     this.finalBuffer.get(result, 0, 20);
/*     */     
/* 432 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] digest(ByteBuffer buffer)
/*     */   {
/* 443 */     update(buffer);
/* 444 */     return digest();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void saveState()
/*     */   {
/* 455 */     this.s0 = this.h0;
/* 456 */     this.s1 = this.h1;
/* 457 */     this.s2 = this.h2;
/* 458 */     this.s3 = this.h3;
/* 459 */     this.s4 = this.h4;
/*     */     
/* 461 */     this.saveLength = this.length;
/*     */     
/* 463 */     int position = this.finalBuffer.position();
/*     */     
/* 465 */     this.finalBuffer.rewind();
/* 466 */     this.finalBuffer.limit(position);
/*     */     
/* 468 */     this.saveBuffer.clear();
/* 469 */     this.saveBuffer.put(this.finalBuffer);
/* 470 */     this.saveBuffer.flip();
/*     */     
/* 472 */     this.finalBuffer.limit(64);
/* 473 */     this.finalBuffer.position(position);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void restoreState()
/*     */   {
/* 481 */     this.h0 = this.s0;
/* 482 */     this.h1 = this.s1;
/* 483 */     this.h2 = this.s2;
/* 484 */     this.h3 = this.s3;
/* 485 */     this.h4 = this.s4;
/*     */     
/* 487 */     this.length = this.saveLength;
/*     */     
/* 489 */     this.finalBuffer.clear();
/* 490 */     this.finalBuffer.put(this.saveBuffer);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/SHA1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */