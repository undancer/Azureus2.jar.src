/*     */ package org.gudy.azureus2.core3.util;
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
/*     */ public class Base32
/*     */ {
/*     */   private static final String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
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
/*  36 */   private static final int[] base32Lookup = { 255, 255, 26, 27, 28, 29, 30, 31, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 255, 255, 255, 255, 255, 255, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 255, 255, 255, 255, 255 };
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
/*     */   public static String encode(byte[] bytes)
/*     */   {
/*  53 */     int i = 0;int index = 0;int digit = 0;
/*     */     
/*  55 */     StringBuilder base32 = new StringBuilder((bytes.length + 7) * 8 / 5);
/*     */     
/*  57 */     while (i < bytes.length)
/*     */     {
/*  59 */       int currByte = bytes[i] >= 0 ? bytes[i] : bytes[i] + 256;
/*     */       
/*     */ 
/*  62 */       if (index > 3) { int nextByte;
/*     */         int nextByte;
/*  64 */         if (i + 1 < bytes.length) {
/*  65 */           nextByte = bytes[(i + 1)] >= 0 ? bytes[(i + 1)] : bytes[(i + 1)] + 256;
/*     */         } else {
/*  67 */           nextByte = 0;
/*     */         }
/*  69 */         digit = currByte & 255 >> index;
/*  70 */         index = (index + 5) % 8;
/*  71 */         digit <<= index;
/*  72 */         digit |= nextByte >> 8 - index;
/*  73 */         i++;
/*     */       }
/*     */       else
/*     */       {
/*  77 */         digit = currByte >> 8 - (index + 5) & 0x1F;
/*  78 */         index = (index + 5) % 8;
/*  79 */         if (index == 0)
/*  80 */           i++;
/*     */       }
/*  82 */       base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(digit));
/*     */     }
/*     */     
/*  85 */     return base32.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] decode(String base32)
/*     */   {
/*  93 */     byte[] bytes = new byte[base32.length() * 5 / 8];
/*     */     
/*  95 */     if (bytes.length == 0) {
/*  96 */       return bytes;
/*     */     }
/*     */     
/*  99 */     int i = 0;int index = 0; for (int offset = 0; i < base32.length(); i++)
/*     */     {
/* 101 */       int lookup = base32.charAt(i) - '0';
/*     */       
/*     */ 
/* 104 */       if ((lookup >= 0) && (lookup < base32Lookup.length))
/*     */       {
/*     */ 
/* 107 */         int digit = base32Lookup[lookup];
/*     */         
/*     */ 
/* 110 */         if (digit != 255)
/*     */         {
/*     */ 
/* 113 */           if (index <= 3)
/*     */           {
/* 115 */             index = (index + 5) % 8;
/* 116 */             if (index == 0)
/*     */             {
/* 118 */               int tmp99_97 = offset; byte[] tmp99_95 = bytes;tmp99_95[tmp99_97] = ((byte)(tmp99_95[tmp99_97] | digit));
/* 119 */               offset++;
/* 120 */               if (offset >= bytes.length)
/*     */                 break;
/*     */             } else {
/* 123 */               int tmp124_122 = offset; byte[] tmp124_120 = bytes;tmp124_120[tmp124_122] = ((byte)(tmp124_120[tmp124_122] | digit << 8 - index));
/*     */             }
/*     */           }
/*     */           else {
/* 127 */             index = (index + 5) % 8; int 
/* 128 */               tmp150_148 = offset; byte[] tmp150_146 = bytes;tmp150_146[tmp150_148] = ((byte)(tmp150_146[tmp150_148] | digit >>> index));
/* 129 */             offset++;
/*     */             
/* 131 */             if (offset >= bytes.length) break;
/* 132 */             int tmp177_175 = offset; byte[] tmp177_173 = bytes;tmp177_173[tmp177_175] = ((byte)(tmp177_173[tmp177_175] | digit << 8 - index));
/*     */           } }
/*     */       } }
/* 135 */     return bytes;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/Base32.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */