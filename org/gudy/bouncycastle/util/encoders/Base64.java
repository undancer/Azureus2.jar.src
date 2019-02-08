/*     */ package org.gudy.bouncycastle.util.encoders;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class Base64
/*     */ {
/*   8 */   private static final byte[] encodingTable = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
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
/*     */   public static byte[] encode(byte[] data)
/*     */   {
/*  34 */     int modulus = data.length % 3;
/*  35 */     byte[] bytes; byte[] bytes; if (modulus == 0)
/*     */     {
/*  37 */       bytes = new byte[4 * data.length / 3];
/*     */     }
/*     */     else
/*     */     {
/*  41 */       bytes = new byte[4 * (data.length / 3 + 1)];
/*     */     }
/*     */     
/*  44 */     int dataLength = data.length - modulus;
/*     */     
/*  46 */     int i = 0; for (int j = 0; i < dataLength; j += 4)
/*     */     {
/*  48 */       int a1 = data[i] & 0xFF;
/*  49 */       int a2 = data[(i + 1)] & 0xFF;
/*  50 */       int a3 = data[(i + 2)] & 0xFF;
/*     */       
/*  52 */       bytes[j] = encodingTable[(a1 >>> 2 & 0x3F)];
/*  53 */       bytes[(j + 1)] = encodingTable[((a1 << 4 | a2 >>> 4) & 0x3F)];
/*  54 */       bytes[(j + 2)] = encodingTable[((a2 << 2 | a3 >>> 6) & 0x3F)];
/*  55 */       bytes[(j + 3)] = encodingTable[(a3 & 0x3F)];i += 3;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     int d1;
/*     */     
/*     */ 
/*     */ 
/*     */     int b1;
/*     */     
/*     */ 
/*     */ 
/*     */     int b2;
/*     */     
/*     */ 
/*  64 */     switch (modulus)
/*     */     {
/*     */     case 0: 
/*     */       break;
/*     */     case 1: 
/*  69 */       d1 = data[(data.length - 1)] & 0xFF;
/*  70 */       b1 = d1 >>> 2 & 0x3F;
/*  71 */       b2 = d1 << 4 & 0x3F;
/*     */       
/*  73 */       bytes[(bytes.length - 4)] = encodingTable[b1];
/*  74 */       bytes[(bytes.length - 3)] = encodingTable[b2];
/*  75 */       bytes[(bytes.length - 2)] = 61;
/*  76 */       bytes[(bytes.length - 1)] = 61;
/*  77 */       break;
/*     */     case 2: 
/*  79 */       d1 = data[(data.length - 2)] & 0xFF;
/*  80 */       int d2 = data[(data.length - 1)] & 0xFF;
/*     */       
/*  82 */       b1 = d1 >>> 2 & 0x3F;
/*  83 */       b2 = (d1 << 4 | d2 >>> 4) & 0x3F;
/*  84 */       int b3 = d2 << 2 & 0x3F;
/*     */       
/*  86 */       bytes[(bytes.length - 4)] = encodingTable[b1];
/*  87 */       bytes[(bytes.length - 3)] = encodingTable[b2];
/*  88 */       bytes[(bytes.length - 2)] = encodingTable[b3];
/*  89 */       bytes[(bytes.length - 1)] = 61;
/*     */     }
/*     */     
/*     */     
/*  93 */     return bytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 103 */   private static final byte[] decodingTable = new byte[''];
/*     */   
/* 105 */   static { for (int i = 65; i <= 90; i++)
/*     */     {
/* 107 */       decodingTable[i] = ((byte)(i - 65));
/*     */     }
/*     */     
/* 110 */     for (int i = 97; i <= 122; i++)
/*     */     {
/* 112 */       decodingTable[i] = ((byte)(i - 97 + 26));
/*     */     }
/*     */     
/* 115 */     for (int i = 48; i <= 57; i++)
/*     */     {
/* 117 */       decodingTable[i] = ((byte)(i - 48 + 52));
/*     */     }
/*     */     
/* 120 */     decodingTable[43] = 62;
/* 121 */     decodingTable[47] = 63;
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
/*     */   public static byte[] decode(byte[] data)
/*     */   {
/* 134 */     if (data.length == 0) {
/* 135 */       return data;
/*     */     }
/*     */     
/*     */     byte[] bytes;
/*     */     
/*     */     byte[] bytes;
/* 141 */     if (data[(data.length - 2)] == 61)
/*     */     {
/* 143 */       bytes = new byte[(data.length / 4 - 1) * 3 + 1];
/*     */     } else { byte[] bytes;
/* 145 */       if (data[(data.length - 1)] == 61)
/*     */       {
/* 147 */         bytes = new byte[(data.length / 4 - 1) * 3 + 2];
/*     */       }
/*     */       else
/*     */       {
/* 151 */         bytes = new byte[data.length / 4 * 3];
/*     */       }
/*     */     }
/* 154 */     int i = 0; for (int j = 0; i < data.length - 4; j += 3)
/*     */     {
/* 156 */       byte b1 = decodingTable[data[i]];
/* 157 */       byte b2 = decodingTable[data[(i + 1)]];
/* 158 */       byte b3 = decodingTable[data[(i + 2)]];
/* 159 */       byte b4 = decodingTable[data[(i + 3)]];
/*     */       
/* 161 */       bytes[j] = ((byte)(b1 << 2 | b2 >> 4));
/* 162 */       bytes[(j + 1)] = ((byte)(b2 << 4 | b3 >> 2));
/* 163 */       bytes[(j + 2)] = ((byte)(b3 << 6 | b4));i += 4;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 166 */     if (data[(data.length - 2)] == 61)
/*     */     {
/* 168 */       byte b1 = decodingTable[data[(data.length - 4)]];
/* 169 */       byte b2 = decodingTable[data[(data.length - 3)]];
/*     */       
/* 171 */       bytes[(bytes.length - 1)] = ((byte)(b1 << 2 | b2 >> 4));
/*     */     }
/* 173 */     else if (data[(data.length - 1)] == 61)
/*     */     {
/* 175 */       byte b1 = decodingTable[data[(data.length - 4)]];
/* 176 */       byte b2 = decodingTable[data[(data.length - 3)]];
/* 177 */       byte b3 = decodingTable[data[(data.length - 2)]];
/*     */       
/* 179 */       bytes[(bytes.length - 2)] = ((byte)(b1 << 2 | b2 >> 4));
/* 180 */       bytes[(bytes.length - 1)] = ((byte)(b2 << 4 | b3 >> 2));
/*     */     }
/*     */     else
/*     */     {
/* 184 */       byte b1 = decodingTable[data[(data.length - 4)]];
/* 185 */       byte b2 = decodingTable[data[(data.length - 3)]];
/* 186 */       byte b3 = decodingTable[data[(data.length - 2)]];
/* 187 */       byte b4 = decodingTable[data[(data.length - 1)]];
/*     */       
/* 189 */       bytes[(bytes.length - 3)] = ((byte)(b1 << 2 | b2 >> 4));
/* 190 */       bytes[(bytes.length - 2)] = ((byte)(b2 << 4 | b3 >> 2));
/* 191 */       bytes[(bytes.length - 1)] = ((byte)(b3 << 6 | b4));
/*     */     }
/*     */     
/* 194 */     return bytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] decode(char[] data)
/*     */   {
/* 202 */     if (data.length == 0) {
/* 203 */       return new byte[0];
/*     */     }
/*     */     
/*     */     byte[] bytes;
/*     */     
/*     */     byte[] bytes;
/* 209 */     if (data[(data.length - 2)] == '=')
/*     */     {
/* 211 */       bytes = new byte[(data.length / 4 - 1) * 3 + 1];
/*     */     } else { byte[] bytes;
/* 213 */       if (data[(data.length - 1)] == '=')
/*     */       {
/* 215 */         bytes = new byte[(data.length / 4 - 1) * 3 + 2];
/*     */       }
/*     */       else
/*     */       {
/* 219 */         bytes = new byte[data.length / 4 * 3];
/*     */       }
/*     */     }
/* 222 */     int i = 0; for (int j = 0; i < data.length - 4; j += 3)
/*     */     {
/* 224 */       byte b1 = decodingTable[data[i]];
/* 225 */       byte b2 = decodingTable[data[(i + 1)]];
/* 226 */       byte b3 = decodingTable[data[(i + 2)]];
/* 227 */       byte b4 = decodingTable[data[(i + 3)]];
/*     */       
/* 229 */       bytes[j] = ((byte)(b1 << 2 | b2 >> 4));
/* 230 */       bytes[(j + 1)] = ((byte)(b2 << 4 | b3 >> 2));
/* 231 */       bytes[(j + 2)] = ((byte)(b3 << 6 | b4));i += 4;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 234 */     if (data[(data.length - 2)] == '=')
/*     */     {
/* 236 */       byte b1 = decodingTable[data[(data.length - 4)]];
/* 237 */       byte b2 = decodingTable[data[(data.length - 3)]];
/*     */       
/* 239 */       bytes[(bytes.length - 1)] = ((byte)(b1 << 2 | b2 >> 4));
/*     */     }
/* 241 */     else if (data[(data.length - 1)] == '=')
/*     */     {
/* 243 */       byte b1 = decodingTable[data[(data.length - 4)]];
/* 244 */       byte b2 = decodingTable[data[(data.length - 3)]];
/* 245 */       byte b3 = decodingTable[data[(data.length - 2)]];
/*     */       
/* 247 */       bytes[(bytes.length - 2)] = ((byte)(b1 << 2 | b2 >> 4));
/* 248 */       bytes[(bytes.length - 1)] = ((byte)(b2 << 4 | b3 >> 2));
/*     */     }
/*     */     else
/*     */     {
/* 252 */       byte b1 = decodingTable[data[(data.length - 4)]];
/* 253 */       byte b2 = decodingTable[data[(data.length - 3)]];
/* 254 */       byte b3 = decodingTable[data[(data.length - 2)]];
/* 255 */       byte b4 = decodingTable[data[(data.length - 1)]];
/*     */       
/* 257 */       bytes[(bytes.length - 3)] = ((byte)(b1 << 2 | b2 >> 4));
/* 258 */       bytes[(bytes.length - 2)] = ((byte)(b2 << 4 | b3 >> 2));
/* 259 */       bytes[(bytes.length - 1)] = ((byte)(b3 << 6 | b4));
/*     */     }
/*     */     
/* 262 */     return bytes;
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
/*     */   public static byte[] decode(String data)
/*     */   {
/* 275 */     if (data.length() == 0)
/*     */     {
/* 277 */       return new byte[0];
/*     */     }
/*     */     
/*     */     byte[] bytes;
/*     */     
/*     */     byte[] bytes;
/* 283 */     if (data.charAt(data.length() - 2) == '=')
/*     */     {
/* 285 */       bytes = new byte[(data.length() / 4 - 1) * 3 + 1];
/*     */     } else { byte[] bytes;
/* 287 */       if (data.charAt(data.length() - 1) == '=')
/*     */       {
/* 289 */         bytes = new byte[(data.length() / 4 - 1) * 3 + 2];
/*     */       }
/*     */       else
/*     */       {
/* 293 */         bytes = new byte[data.length() / 4 * 3];
/*     */       }
/*     */     }
/* 296 */     int i = 0; for (int j = 0; i < data.length() - 4; j += 3)
/*     */     {
/* 298 */       byte b1 = decodingTable[data.charAt(i)];
/* 299 */       byte b2 = decodingTable[data.charAt(i + 1)];
/* 300 */       byte b3 = decodingTable[data.charAt(i + 2)];
/* 301 */       byte b4 = decodingTable[data.charAt(i + 3)];
/*     */       
/* 303 */       bytes[j] = ((byte)(b1 << 2 | b2 >> 4));
/* 304 */       bytes[(j + 1)] = ((byte)(b2 << 4 | b3 >> 2));
/* 305 */       bytes[(j + 2)] = ((byte)(b3 << 6 | b4));i += 4;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 308 */     if (data.charAt(data.length() - 2) == '=')
/*     */     {
/* 310 */       byte b1 = decodingTable[data.charAt(data.length() - 4)];
/* 311 */       byte b2 = decodingTable[data.charAt(data.length() - 3)];
/*     */       
/* 313 */       bytes[(bytes.length - 1)] = ((byte)(b1 << 2 | b2 >> 4));
/*     */     }
/* 315 */     else if (data.charAt(data.length() - 1) == '=')
/*     */     {
/* 317 */       byte b1 = decodingTable[data.charAt(data.length() - 4)];
/* 318 */       byte b2 = decodingTable[data.charAt(data.length() - 3)];
/* 319 */       byte b3 = decodingTable[data.charAt(data.length() - 2)];
/*     */       
/* 321 */       bytes[(bytes.length - 2)] = ((byte)(b1 << 2 | b2 >> 4));
/* 322 */       bytes[(bytes.length - 1)] = ((byte)(b2 << 4 | b3 >> 2));
/*     */     }
/*     */     else
/*     */     {
/* 326 */       byte b1 = decodingTable[data.charAt(data.length() - 4)];
/* 327 */       byte b2 = decodingTable[data.charAt(data.length() - 3)];
/* 328 */       byte b3 = decodingTable[data.charAt(data.length() - 2)];
/* 329 */       byte b4 = decodingTable[data.charAt(data.length() - 1)];
/*     */       
/* 331 */       bytes[(bytes.length - 3)] = ((byte)(b1 << 2 | b2 >> 4));
/* 332 */       bytes[(bytes.length - 2)] = ((byte)(b2 << 4 | b3 >> 2));
/* 333 */       bytes[(bytes.length - 1)] = ((byte)(b3 << 6 | b4));
/*     */     }
/*     */     
/* 336 */     return bytes;
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
/*     */   public static int decode(String data, OutputStream out)
/*     */     throws IOException
/*     */   {
/* 350 */     if (data.length() == 0)
/*     */     {
/* 352 */       return 0;
/*     */     }
/*     */     
/*     */     int length;
/*     */     
/*     */     int length;
/* 358 */     if (data.charAt(data.length() - 2) == '=')
/*     */     {
/* 360 */       length = (data.length() / 4 - 1) * 3 + 1;
/*     */     } else { int length;
/* 362 */       if (data.charAt(data.length() - 1) == '=')
/*     */       {
/* 364 */         length = (data.length() / 4 - 1) * 3 + 2;
/*     */       }
/*     */       else
/*     */       {
/* 368 */         length = data.length() / 4 * 3;
/*     */       }
/*     */     }
/* 371 */     int i = 0; for (int j = 0; i < data.length() - 4; j += 3)
/*     */     {
/* 373 */       byte b1 = decodingTable[data.charAt(i)];
/* 374 */       byte b2 = decodingTable[data.charAt(i + 1)];
/* 375 */       byte b3 = decodingTable[data.charAt(i + 2)];
/* 376 */       byte b4 = decodingTable[data.charAt(i + 3)];
/*     */       
/* 378 */       out.write(b1 << 2 | b2 >> 4);
/* 379 */       out.write(b2 << 4 | b3 >> 2);
/* 380 */       out.write(b3 << 6 | b4);i += 4;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 383 */     if (data.charAt(data.length() - 2) == '=')
/*     */     {
/* 385 */       byte b1 = decodingTable[data.charAt(data.length() - 4)];
/* 386 */       byte b2 = decodingTable[data.charAt(data.length() - 3)];
/*     */       
/* 388 */       out.write(b1 << 2 | b2 >> 4);
/*     */     }
/* 390 */     else if (data.charAt(data.length() - 1) == '=')
/*     */     {
/* 392 */       byte b1 = decodingTable[data.charAt(data.length() - 4)];
/* 393 */       byte b2 = decodingTable[data.charAt(data.length() - 3)];
/* 394 */       byte b3 = decodingTable[data.charAt(data.length() - 2)];
/*     */       
/* 396 */       out.write(b1 << 2 | b2 >> 4);
/* 397 */       out.write(b2 << 4 | b3 >> 2);
/*     */     }
/*     */     else
/*     */     {
/* 401 */       byte b1 = decodingTable[data.charAt(data.length() - 4)];
/* 402 */       byte b2 = decodingTable[data.charAt(data.length() - 3)];
/* 403 */       byte b3 = decodingTable[data.charAt(data.length() - 2)];
/* 404 */       byte b4 = decodingTable[data.charAt(data.length() - 1)];
/*     */       
/* 406 */       out.write(b1 << 2 | b2 >> 4);
/* 407 */       out.write(b2 << 4 | b3 >> 2);
/* 408 */       out.write(b3 << 6 | b4);
/*     */     }
/*     */     
/* 411 */     return length;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/util/encoders/Base64.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */