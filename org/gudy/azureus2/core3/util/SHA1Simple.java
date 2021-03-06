/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Random;
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
/*     */ public final class SHA1Simple
/*     */ {
/*     */   private int h0;
/*     */   private int h1;
/*     */   private int h2;
/*     */   private int h3;
/*     */   private int h4;
/*  34 */   private final byte[] temp = new byte[64];
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
/*     */   private void transform(byte[] M, int pos)
/*     */   {
/*  49 */     int w0 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  50 */     int w1 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  51 */     int w2 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  52 */     int w3 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  53 */     int w4 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  54 */     int w5 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  55 */     int w6 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  56 */     int w7 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  57 */     int w8 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  58 */     int w9 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  59 */     int w10 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  60 */     int w11 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  61 */     int w12 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  62 */     int w13 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  63 */     int w14 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*  64 */     int w15 = (M[pos] & 0xFF) << 24 | (M[(pos + 1)] & 0xFF) << 16 | (M[(pos + 2)] & 0xFF) << 8 | (M[(pos + 3)] & 0xFF) << 0;pos += 4;
/*     */     
/*     */ 
/*  67 */     int a = this.h0;int b = this.h1;int c = this.h2;int d = this.h3;int e = this.h4;
/*  68 */     e += (a << 5 | a >>> 27) + w0 + (b & c | (b ^ 0xFFFFFFFF) & d) + 1518500249;
/*  69 */     b = b << 30 | b >>> 2;
/*  70 */     d += (e << 5 | e >>> 27) + w1 + (a & b | (a ^ 0xFFFFFFFF) & c) + 1518500249;
/*  71 */     a = a << 30 | a >>> 2;
/*  72 */     c += (d << 5 | d >>> 27) + w2 + (e & a | (e ^ 0xFFFFFFFF) & b) + 1518500249;
/*  73 */     e = e << 30 | e >>> 2;
/*  74 */     b += (c << 5 | c >>> 27) + w3 + (d & e | (d ^ 0xFFFFFFFF) & a) + 1518500249;
/*  75 */     d = d << 30 | d >>> 2;
/*  76 */     a += (b << 5 | b >>> 27) + w4 + (c & d | (c ^ 0xFFFFFFFF) & e) + 1518500249;
/*  77 */     c = c << 30 | c >>> 2;
/*  78 */     e += (a << 5 | a >>> 27) + w5 + (b & c | (b ^ 0xFFFFFFFF) & d) + 1518500249;
/*  79 */     b = b << 30 | b >>> 2;
/*  80 */     d += (e << 5 | e >>> 27) + w6 + (a & b | (a ^ 0xFFFFFFFF) & c) + 1518500249;
/*  81 */     a = a << 30 | a >>> 2;
/*  82 */     c += (d << 5 | d >>> 27) + w7 + (e & a | (e ^ 0xFFFFFFFF) & b) + 1518500249;
/*  83 */     e = e << 30 | e >>> 2;
/*  84 */     b += (c << 5 | c >>> 27) + w8 + (d & e | (d ^ 0xFFFFFFFF) & a) + 1518500249;
/*  85 */     d = d << 30 | d >>> 2;
/*  86 */     a += (b << 5 | b >>> 27) + w9 + (c & d | (c ^ 0xFFFFFFFF) & e) + 1518500249;
/*  87 */     c = c << 30 | c >>> 2;
/*  88 */     e += (a << 5 | a >>> 27) + w10 + (b & c | (b ^ 0xFFFFFFFF) & d) + 1518500249;
/*  89 */     b = b << 30 | b >>> 2;
/*  90 */     d += (e << 5 | e >>> 27) + w11 + (a & b | (a ^ 0xFFFFFFFF) & c) + 1518500249;
/*  91 */     a = a << 30 | a >>> 2;
/*  92 */     c += (d << 5 | d >>> 27) + w12 + (e & a | (e ^ 0xFFFFFFFF) & b) + 1518500249;
/*  93 */     e = e << 30 | e >>> 2;
/*  94 */     b += (c << 5 | c >>> 27) + w13 + (d & e | (d ^ 0xFFFFFFFF) & a) + 1518500249;
/*  95 */     d = d << 30 | d >>> 2;
/*  96 */     a += (b << 5 | b >>> 27) + w14 + (c & d | (c ^ 0xFFFFFFFF) & e) + 1518500249;
/*  97 */     c = c << 30 | c >>> 2;
/*  98 */     e += (a << 5 | a >>> 27) + w15 + (b & c | (b ^ 0xFFFFFFFF) & d) + 1518500249;
/*  99 */     b = b << 30 | b >>> 2;
/* 100 */     w0 = w13 ^ w8 ^ w2 ^ w0;w0 = w0 << 1 | w0 >>> 31;
/* 101 */     d += (e << 5 | e >>> 27) + w0 + (a & b | (a ^ 0xFFFFFFFF) & c) + 1518500249;
/* 102 */     a = a << 30 | a >>> 2;
/* 103 */     w1 = w14 ^ w9 ^ w3 ^ w1;w1 = w1 << 1 | w1 >>> 31;
/* 104 */     c += (d << 5 | d >>> 27) + w1 + (e & a | (e ^ 0xFFFFFFFF) & b) + 1518500249;
/* 105 */     e = e << 30 | e >>> 2;
/* 106 */     w2 = w15 ^ w10 ^ w4 ^ w2;w2 = w2 << 1 | w2 >>> 31;
/* 107 */     b += (c << 5 | c >>> 27) + w2 + (d & e | (d ^ 0xFFFFFFFF) & a) + 1518500249;
/* 108 */     d = d << 30 | d >>> 2;
/* 109 */     w3 = w0 ^ w11 ^ w5 ^ w3;w3 = w3 << 1 | w3 >>> 31;
/* 110 */     a += (b << 5 | b >>> 27) + w3 + (c & d | (c ^ 0xFFFFFFFF) & e) + 1518500249;
/* 111 */     c = c << 30 | c >>> 2;
/* 112 */     w4 = w1 ^ w12 ^ w6 ^ w4;w4 = w4 << 1 | w4 >>> 31;
/* 113 */     e += (a << 5 | a >>> 27) + w4 + (b ^ c ^ d) + 1859775393;
/* 114 */     b = b << 30 | b >>> 2;
/* 115 */     w5 = w2 ^ w13 ^ w7 ^ w5;w5 = w5 << 1 | w5 >>> 31;
/* 116 */     d += (e << 5 | e >>> 27) + w5 + (a ^ b ^ c) + 1859775393;
/* 117 */     a = a << 30 | a >>> 2;
/* 118 */     w6 = w3 ^ w14 ^ w8 ^ w6;w6 = w6 << 1 | w6 >>> 31;
/* 119 */     c += (d << 5 | d >>> 27) + w6 + (e ^ a ^ b) + 1859775393;
/* 120 */     e = e << 30 | e >>> 2;
/* 121 */     w7 = w4 ^ w15 ^ w9 ^ w7;w7 = w7 << 1 | w7 >>> 31;
/* 122 */     b += (c << 5 | c >>> 27) + w7 + (d ^ e ^ a) + 1859775393;
/* 123 */     d = d << 30 | d >>> 2;
/* 124 */     w8 = w5 ^ w0 ^ w10 ^ w8;w8 = w8 << 1 | w8 >>> 31;
/* 125 */     a += (b << 5 | b >>> 27) + w8 + (c ^ d ^ e) + 1859775393;
/* 126 */     c = c << 30 | c >>> 2;
/* 127 */     w9 = w6 ^ w1 ^ w11 ^ w9;w9 = w9 << 1 | w9 >>> 31;
/* 128 */     e += (a << 5 | a >>> 27) + w9 + (b ^ c ^ d) + 1859775393;
/* 129 */     b = b << 30 | b >>> 2;
/* 130 */     w10 = w7 ^ w2 ^ w12 ^ w10;w10 = w10 << 1 | w10 >>> 31;
/* 131 */     d += (e << 5 | e >>> 27) + w10 + (a ^ b ^ c) + 1859775393;
/* 132 */     a = a << 30 | a >>> 2;
/* 133 */     w11 = w8 ^ w3 ^ w13 ^ w11;w11 = w11 << 1 | w11 >>> 31;
/* 134 */     c += (d << 5 | d >>> 27) + w11 + (e ^ a ^ b) + 1859775393;
/* 135 */     e = e << 30 | e >>> 2;
/* 136 */     w12 = w9 ^ w4 ^ w14 ^ w12;w12 = w12 << 1 | w12 >>> 31;
/* 137 */     b += (c << 5 | c >>> 27) + w12 + (d ^ e ^ a) + 1859775393;
/* 138 */     d = d << 30 | d >>> 2;
/* 139 */     w13 = w10 ^ w5 ^ w15 ^ w13;w13 = w13 << 1 | w13 >>> 31;
/* 140 */     a += (b << 5 | b >>> 27) + w13 + (c ^ d ^ e) + 1859775393;
/* 141 */     c = c << 30 | c >>> 2;
/* 142 */     w14 = w11 ^ w6 ^ w0 ^ w14;w14 = w14 << 1 | w14 >>> 31;
/* 143 */     e += (a << 5 | a >>> 27) + w14 + (b ^ c ^ d) + 1859775393;
/* 144 */     b = b << 30 | b >>> 2;
/* 145 */     w15 = w12 ^ w7 ^ w1 ^ w15;w15 = w15 << 1 | w15 >>> 31;
/* 146 */     d += (e << 5 | e >>> 27) + w15 + (a ^ b ^ c) + 1859775393;
/* 147 */     a = a << 30 | a >>> 2;
/* 148 */     w0 = w13 ^ w8 ^ w2 ^ w0;w0 = w0 << 1 | w0 >>> 31;
/* 149 */     c += (d << 5 | d >>> 27) + w0 + (e ^ a ^ b) + 1859775393;
/* 150 */     e = e << 30 | e >>> 2;
/* 151 */     w1 = w14 ^ w9 ^ w3 ^ w1;w1 = w1 << 1 | w1 >>> 31;
/* 152 */     b += (c << 5 | c >>> 27) + w1 + (d ^ e ^ a) + 1859775393;
/* 153 */     d = d << 30 | d >>> 2;
/* 154 */     w2 = w15 ^ w10 ^ w4 ^ w2;w2 = w2 << 1 | w2 >>> 31;
/* 155 */     a += (b << 5 | b >>> 27) + w2 + (c ^ d ^ e) + 1859775393;
/* 156 */     c = c << 30 | c >>> 2;
/* 157 */     w3 = w0 ^ w11 ^ w5 ^ w3;w3 = w3 << 1 | w3 >>> 31;
/* 158 */     e += (a << 5 | a >>> 27) + w3 + (b ^ c ^ d) + 1859775393;
/* 159 */     b = b << 30 | b >>> 2;
/* 160 */     w4 = w1 ^ w12 ^ w6 ^ w4;w4 = w4 << 1 | w4 >>> 31;
/* 161 */     d += (e << 5 | e >>> 27) + w4 + (a ^ b ^ c) + 1859775393;
/* 162 */     a = a << 30 | a >>> 2;
/* 163 */     w5 = w2 ^ w13 ^ w7 ^ w5;w5 = w5 << 1 | w5 >>> 31;
/* 164 */     c += (d << 5 | d >>> 27) + w5 + (e ^ a ^ b) + 1859775393;
/* 165 */     e = e << 30 | e >>> 2;
/* 166 */     w6 = w3 ^ w14 ^ w8 ^ w6;w6 = w6 << 1 | w6 >>> 31;
/* 167 */     b += (c << 5 | c >>> 27) + w6 + (d ^ e ^ a) + 1859775393;
/* 168 */     d = d << 30 | d >>> 2;
/* 169 */     w7 = w4 ^ w15 ^ w9 ^ w7;w7 = w7 << 1 | w7 >>> 31;
/* 170 */     a += (b << 5 | b >>> 27) + w7 + (c ^ d ^ e) + 1859775393;
/* 171 */     c = c << 30 | c >>> 2;
/* 172 */     w8 = w5 ^ w0 ^ w10 ^ w8;w8 = w8 << 1 | w8 >>> 31;
/* 173 */     e += (a << 5 | a >>> 27) + w8 + (b & c | b & d | c & d) + -1894007588;
/* 174 */     b = b << 30 | b >>> 2;
/* 175 */     w9 = w6 ^ w1 ^ w11 ^ w9;w9 = w9 << 1 | w9 >>> 31;
/* 176 */     d += (e << 5 | e >>> 27) + w9 + (a & b | a & c | b & c) + -1894007588;
/* 177 */     a = a << 30 | a >>> 2;
/* 178 */     w10 = w7 ^ w2 ^ w12 ^ w10;w10 = w10 << 1 | w10 >>> 31;
/* 179 */     c += (d << 5 | d >>> 27) + w10 + (e & a | e & b | a & b) + -1894007588;
/* 180 */     e = e << 30 | e >>> 2;
/* 181 */     w11 = w8 ^ w3 ^ w13 ^ w11;w11 = w11 << 1 | w11 >>> 31;
/* 182 */     b += (c << 5 | c >>> 27) + w11 + (d & e | d & a | e & a) + -1894007588;
/* 183 */     d = d << 30 | d >>> 2;
/* 184 */     w12 = w9 ^ w4 ^ w14 ^ w12;w12 = w12 << 1 | w12 >>> 31;
/* 185 */     a += (b << 5 | b >>> 27) + w12 + (c & d | c & e | d & e) + -1894007588;
/* 186 */     c = c << 30 | c >>> 2;
/* 187 */     w13 = w10 ^ w5 ^ w15 ^ w13;w13 = w13 << 1 | w13 >>> 31;
/* 188 */     e += (a << 5 | a >>> 27) + w13 + (b & c | b & d | c & d) + -1894007588;
/* 189 */     b = b << 30 | b >>> 2;
/* 190 */     w14 = w11 ^ w6 ^ w0 ^ w14;w14 = w14 << 1 | w14 >>> 31;
/* 191 */     d += (e << 5 | e >>> 27) + w14 + (a & b | a & c | b & c) + -1894007588;
/* 192 */     a = a << 30 | a >>> 2;
/* 193 */     w15 = w12 ^ w7 ^ w1 ^ w15;w15 = w15 << 1 | w15 >>> 31;
/* 194 */     c += (d << 5 | d >>> 27) + w15 + (e & a | e & b | a & b) + -1894007588;
/* 195 */     e = e << 30 | e >>> 2;
/* 196 */     w0 = w13 ^ w8 ^ w2 ^ w0;w0 = w0 << 1 | w0 >>> 31;
/* 197 */     b += (c << 5 | c >>> 27) + w0 + (d & e | d & a | e & a) + -1894007588;
/* 198 */     d = d << 30 | d >>> 2;
/* 199 */     w1 = w14 ^ w9 ^ w3 ^ w1;w1 = w1 << 1 | w1 >>> 31;
/* 200 */     a += (b << 5 | b >>> 27) + w1 + (c & d | c & e | d & e) + -1894007588;
/* 201 */     c = c << 30 | c >>> 2;
/* 202 */     w2 = w15 ^ w10 ^ w4 ^ w2;w2 = w2 << 1 | w2 >>> 31;
/* 203 */     e += (a << 5 | a >>> 27) + w2 + (b & c | b & d | c & d) + -1894007588;
/* 204 */     b = b << 30 | b >>> 2;
/* 205 */     w3 = w0 ^ w11 ^ w5 ^ w3;w3 = w3 << 1 | w3 >>> 31;
/* 206 */     d += (e << 5 | e >>> 27) + w3 + (a & b | a & c | b & c) + -1894007588;
/* 207 */     a = a << 30 | a >>> 2;
/* 208 */     w4 = w1 ^ w12 ^ w6 ^ w4;w4 = w4 << 1 | w4 >>> 31;
/* 209 */     c += (d << 5 | d >>> 27) + w4 + (e & a | e & b | a & b) + -1894007588;
/* 210 */     e = e << 30 | e >>> 2;
/* 211 */     w5 = w2 ^ w13 ^ w7 ^ w5;w5 = w5 << 1 | w5 >>> 31;
/* 212 */     b += (c << 5 | c >>> 27) + w5 + (d & e | d & a | e & a) + -1894007588;
/* 213 */     d = d << 30 | d >>> 2;
/* 214 */     w6 = w3 ^ w14 ^ w8 ^ w6;w6 = w6 << 1 | w6 >>> 31;
/* 215 */     a += (b << 5 | b >>> 27) + w6 + (c & d | c & e | d & e) + -1894007588;
/* 216 */     c = c << 30 | c >>> 2;
/* 217 */     w7 = w4 ^ w15 ^ w9 ^ w7;w7 = w7 << 1 | w7 >>> 31;
/* 218 */     e += (a << 5 | a >>> 27) + w7 + (b & c | b & d | c & d) + -1894007588;
/* 219 */     b = b << 30 | b >>> 2;
/* 220 */     w8 = w5 ^ w0 ^ w10 ^ w8;w8 = w8 << 1 | w8 >>> 31;
/* 221 */     d += (e << 5 | e >>> 27) + w8 + (a & b | a & c | b & c) + -1894007588;
/* 222 */     a = a << 30 | a >>> 2;
/* 223 */     w9 = w6 ^ w1 ^ w11 ^ w9;w9 = w9 << 1 | w9 >>> 31;
/* 224 */     c += (d << 5 | d >>> 27) + w9 + (e & a | e & b | a & b) + -1894007588;
/* 225 */     e = e << 30 | e >>> 2;
/* 226 */     w10 = w7 ^ w2 ^ w12 ^ w10;w10 = w10 << 1 | w10 >>> 31;
/* 227 */     b += (c << 5 | c >>> 27) + w10 + (d & e | d & a | e & a) + -1894007588;
/* 228 */     d = d << 30 | d >>> 2;
/* 229 */     w11 = w8 ^ w3 ^ w13 ^ w11;w11 = w11 << 1 | w11 >>> 31;
/* 230 */     a += (b << 5 | b >>> 27) + w11 + (c & d | c & e | d & e) + -1894007588;
/* 231 */     c = c << 30 | c >>> 2;
/* 232 */     w12 = w9 ^ w4 ^ w14 ^ w12;w12 = w12 << 1 | w12 >>> 31;
/* 233 */     e += (a << 5 | a >>> 27) + w12 + (b ^ c ^ d) + -899497514;
/* 234 */     b = b << 30 | b >>> 2;
/* 235 */     w13 = w10 ^ w5 ^ w15 ^ w13;w13 = w13 << 1 | w13 >>> 31;
/* 236 */     d += (e << 5 | e >>> 27) + w13 + (a ^ b ^ c) + -899497514;
/* 237 */     a = a << 30 | a >>> 2;
/* 238 */     w14 = w11 ^ w6 ^ w0 ^ w14;w14 = w14 << 1 | w14 >>> 31;
/* 239 */     c += (d << 5 | d >>> 27) + w14 + (e ^ a ^ b) + -899497514;
/* 240 */     e = e << 30 | e >>> 2;
/* 241 */     w15 = w12 ^ w7 ^ w1 ^ w15;w15 = w15 << 1 | w15 >>> 31;
/* 242 */     b += (c << 5 | c >>> 27) + w15 + (d ^ e ^ a) + -899497514;
/* 243 */     d = d << 30 | d >>> 2;
/* 244 */     w0 = w13 ^ w8 ^ w2 ^ w0;w0 = w0 << 1 | w0 >>> 31;
/* 245 */     a += (b << 5 | b >>> 27) + w0 + (c ^ d ^ e) + -899497514;
/* 246 */     c = c << 30 | c >>> 2;
/* 247 */     w1 = w14 ^ w9 ^ w3 ^ w1;w1 = w1 << 1 | w1 >>> 31;
/* 248 */     e += (a << 5 | a >>> 27) + w1 + (b ^ c ^ d) + -899497514;
/* 249 */     b = b << 30 | b >>> 2;
/* 250 */     w2 = w15 ^ w10 ^ w4 ^ w2;w2 = w2 << 1 | w2 >>> 31;
/* 251 */     d += (e << 5 | e >>> 27) + w2 + (a ^ b ^ c) + -899497514;
/* 252 */     a = a << 30 | a >>> 2;
/* 253 */     w3 = w0 ^ w11 ^ w5 ^ w3;w3 = w3 << 1 | w3 >>> 31;
/* 254 */     c += (d << 5 | d >>> 27) + w3 + (e ^ a ^ b) + -899497514;
/* 255 */     e = e << 30 | e >>> 2;
/* 256 */     w4 = w1 ^ w12 ^ w6 ^ w4;w4 = w4 << 1 | w4 >>> 31;
/* 257 */     b += (c << 5 | c >>> 27) + w4 + (d ^ e ^ a) + -899497514;
/* 258 */     d = d << 30 | d >>> 2;
/* 259 */     w5 = w2 ^ w13 ^ w7 ^ w5;w5 = w5 << 1 | w5 >>> 31;
/* 260 */     a += (b << 5 | b >>> 27) + w5 + (c ^ d ^ e) + -899497514;
/* 261 */     c = c << 30 | c >>> 2;
/* 262 */     w6 = w3 ^ w14 ^ w8 ^ w6;w6 = w6 << 1 | w6 >>> 31;
/* 263 */     e += (a << 5 | a >>> 27) + w6 + (b ^ c ^ d) + -899497514;
/* 264 */     b = b << 30 | b >>> 2;
/* 265 */     w7 = w4 ^ w15 ^ w9 ^ w7;w7 = w7 << 1 | w7 >>> 31;
/* 266 */     d += (e << 5 | e >>> 27) + w7 + (a ^ b ^ c) + -899497514;
/* 267 */     a = a << 30 | a >>> 2;
/* 268 */     w8 = w5 ^ w0 ^ w10 ^ w8;w8 = w8 << 1 | w8 >>> 31;
/* 269 */     c += (d << 5 | d >>> 27) + w8 + (e ^ a ^ b) + -899497514;
/* 270 */     e = e << 30 | e >>> 2;
/* 271 */     w9 = w6 ^ w1 ^ w11 ^ w9;w9 = w9 << 1 | w9 >>> 31;
/* 272 */     b += (c << 5 | c >>> 27) + w9 + (d ^ e ^ a) + -899497514;
/* 273 */     d = d << 30 | d >>> 2;
/* 274 */     w10 = w7 ^ w2 ^ w12 ^ w10;w10 = w10 << 1 | w10 >>> 31;
/* 275 */     a += (b << 5 | b >>> 27) + w10 + (c ^ d ^ e) + -899497514;
/* 276 */     c = c << 30 | c >>> 2;
/* 277 */     w11 = w8 ^ w3 ^ w13 ^ w11;w11 = w11 << 1 | w11 >>> 31;
/* 278 */     e += (a << 5 | a >>> 27) + w11 + (b ^ c ^ d) + -899497514;
/* 279 */     b = b << 30 | b >>> 2;
/* 280 */     w12 = w9 ^ w4 ^ w14 ^ w12;w12 = w12 << 1 | w12 >>> 31;
/* 281 */     d += (e << 5 | e >>> 27) + w12 + (a ^ b ^ c) + -899497514;
/* 282 */     a = a << 30 | a >>> 2;
/* 283 */     w13 = w10 ^ w5 ^ w15 ^ w13;w13 = w13 << 1 | w13 >>> 31;
/* 284 */     c += (d << 5 | d >>> 27) + w13 + (e ^ a ^ b) + -899497514;
/* 285 */     e = e << 30 | e >>> 2;
/* 286 */     w14 = w11 ^ w6 ^ w0 ^ w14;w14 = w14 << 1 | w14 >>> 31;
/* 287 */     b += (c << 5 | c >>> 27) + w14 + (d ^ e ^ a) + -899497514;
/* 288 */     d = d << 30 | d >>> 2;
/* 289 */     w15 = w12 ^ w7 ^ w1 ^ w15;w15 = w15 << 1 | w15 >>> 31;
/* 290 */     a += (b << 5 | b >>> 27) + w15 + (c ^ d ^ e) + -899497514;
/* 291 */     c = c << 30 | c >>> 2;
/*     */     
/* 293 */     this.h0 += a;
/* 294 */     this.h1 += b;
/* 295 */     this.h2 += c;
/* 296 */     this.h3 += d;
/* 297 */     this.h4 += e;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] calculateHash(byte[] buffer)
/*     */   {
/* 305 */     return calculateHash(buffer, 0, buffer.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] calculateHash(byte[] buffer, int offset, int length)
/*     */   {
/* 314 */     this.h0 = 1732584193;
/* 315 */     this.h1 = -271733879;
/* 316 */     this.h2 = -1732584194;
/* 317 */     this.h3 = 271733878;
/* 318 */     this.h4 = -1009589776;
/*     */     
/* 320 */     int pos = offset;
/* 321 */     int rem = length;
/*     */     
/* 323 */     while (rem >= 64)
/*     */     {
/* 325 */       transform(buffer, pos);
/*     */       
/* 327 */       pos += 64;
/* 328 */       rem -= 64;
/*     */     }
/*     */     
/* 331 */     if (rem > 0)
/*     */     {
/* 333 */       System.arraycopy(buffer, pos, this.temp, 0, rem);
/*     */       
/* 335 */       pos = rem;
/*     */     }
/*     */     else
/*     */     {
/* 339 */       pos = 0;
/*     */     }
/*     */     
/* 342 */     this.temp[(pos++)] = Byte.MIN_VALUE;
/*     */     
/* 344 */     if (pos > 56)
/*     */     {
/* 346 */       for (int i = pos; i < 64; i++)
/*     */       {
/* 348 */         this.temp[i] = 0;
/*     */       }
/*     */       
/* 351 */       transform(this.temp, 0);
/*     */       
/* 353 */       pos = 0;
/*     */     }
/*     */     
/* 356 */     for (int i = pos; i < 56; i++)
/*     */     {
/* 358 */       this.temp[i] = 0;
/*     */     }
/*     */     
/* 361 */     long l = length << 3;
/*     */     
/* 363 */     this.temp[56] = ((byte)(int)(l >> 56));
/* 364 */     this.temp[57] = ((byte)(int)(l >> 48));
/* 365 */     this.temp[58] = ((byte)(int)(l >> 40));
/* 366 */     this.temp[59] = ((byte)(int)(l >> 32));
/* 367 */     this.temp[60] = ((byte)(int)(l >> 24));
/* 368 */     this.temp[61] = ((byte)(int)(l >> 16));
/* 369 */     this.temp[62] = ((byte)(int)(l >> 8));
/* 370 */     this.temp[63] = ((byte)(int)l);
/*     */     
/* 372 */     transform(this.temp, 0);
/*     */     
/* 374 */     byte[] result = new byte[20];
/*     */     
/* 376 */     result[0] = ((byte)(this.h0 >> 24));
/* 377 */     result[1] = ((byte)(this.h0 >> 16));
/* 378 */     result[2] = ((byte)(this.h0 >> 8));
/* 379 */     result[3] = ((byte)(this.h0 >> 0));
/* 380 */     result[4] = ((byte)(this.h1 >> 24));
/* 381 */     result[5] = ((byte)(this.h1 >> 16));
/* 382 */     result[6] = ((byte)(this.h1 >> 8));
/* 383 */     result[7] = ((byte)(this.h1 >> 0));
/* 384 */     result[8] = ((byte)(this.h2 >> 24));
/* 385 */     result[9] = ((byte)(this.h2 >> 16));
/* 386 */     result[10] = ((byte)(this.h2 >> 8));
/* 387 */     result[11] = ((byte)(this.h2 >> 0));
/* 388 */     result[12] = ((byte)(this.h3 >> 24));
/* 389 */     result[13] = ((byte)(this.h3 >> 16));
/* 390 */     result[14] = ((byte)(this.h3 >> 8));
/* 391 */     result[15] = ((byte)(this.h3 >> 0));
/* 392 */     result[16] = ((byte)(this.h4 >> 24));
/* 393 */     result[17] = ((byte)(this.h4 >> 16));
/* 394 */     result[18] = ((byte)(this.h4 >> 8));
/* 395 */     result[19] = ((byte)(this.h4 >> 0));
/*     */     
/* 397 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 404 */     SHA1Hasher s1 = new SHA1Hasher();
/* 405 */     SHA1Simple s2 = new SHA1Simple();
/*     */     
/* 407 */     Random r = new Random();
/*     */     
/* 409 */     for (int i = 0; i < 10000; i++)
/*     */     {
/* 411 */       int len = r.nextInt(32);
/*     */       
/* 413 */       byte[] x = new byte[len];
/*     */       
/* 415 */       r.nextBytes(x);
/*     */       
/* 417 */       byte[] h1 = s1.calculateHash(x);
/* 418 */       byte[] h2 = s2.calculateHash(x);
/*     */       
/* 420 */       if (Arrays.equals(h1, h2))
/*     */       {
/* 422 */         System.out.println(ByteFormatter.nicePrint(h1) + " - " + ByteFormatter.nicePrint(x));
/*     */       }
/*     */       else
/*     */       {
/* 426 */         System.out.println("arghh");
/*     */         
/* 428 */         return;
/*     */       }
/*     */     }
/*     */     
/* 432 */     System.out.println("End");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/SHA1Simple.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */