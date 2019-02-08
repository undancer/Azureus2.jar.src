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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ByteFormatter
/*     */ {
/*  30 */   static final char[] HEXDIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String nicePrint(String str)
/*     */   {
/*  39 */     return nicePrint(str.getBytes(), true);
/*     */   }
/*     */   
/*     */   public static String nicePrint(byte[] data) {
/*  43 */     return nicePrint(data, false);
/*     */   }
/*     */   
/*  46 */   public static String nicePrint(byte[] data, int max) { return nicePrint(data, false, max); }
/*     */   
/*     */   public static String nicePrint(ByteBuffer data)
/*     */   {
/*  50 */     byte[] raw = new byte[data.limit()];
/*     */     
/*  52 */     for (int i = 0; i < raw.length; i++) {
/*  53 */       raw[i] = data.get(i);
/*     */     }
/*     */     
/*  56 */     return nicePrint(raw);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String nicePrint(byte[] data, boolean tight)
/*     */   {
/*  65 */     return nicePrint(data, tight, 1024);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String nicePrint(byte[] data, boolean tight, int max_length)
/*     */   {
/*  74 */     if (data == null) {
/*  75 */       return "";
/*     */     }
/*     */     
/*  78 */     int dataLength = data.length;
/*     */     
/*  80 */     if (dataLength > max_length) {
/*  81 */       dataLength = max_length;
/*     */     }
/*     */     
/*  84 */     int size = dataLength * 2;
/*  85 */     if (!tight) {
/*  86 */       size += (dataLength - 1) / 4;
/*     */     }
/*     */     
/*  89 */     char[] out = new char[size];
/*     */     try
/*     */     {
/*  92 */       int pos = 0;
/*  93 */       for (int i = 0; i < dataLength; i++) {
/*  94 */         if ((!tight) && (i % 4 == 0) && (i > 0)) {
/*  95 */           out[(pos++)] = ' ';
/*     */         }
/*     */         
/*  98 */         out[(pos++)] = HEXDIGITS[((byte)(data[i] >> 4 & 0xF))];
/*  99 */         out[(pos++)] = HEXDIGITS[((byte)(data[i] & 0xF))];
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 103 */       Debug.printStackTrace(e);
/*     */     }
/*     */     try
/*     */     {
/* 107 */       return new String(out) + (data.length > max_length ? "..." : "");
/*     */     } catch (Exception e) {
/* 109 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 112 */     return "";
/*     */   }
/*     */   
/*     */   public static String nicePrint(byte b)
/*     */   {
/* 117 */     byte b1 = (byte)(b >> 4 & 0xF);
/* 118 */     byte b2 = (byte)(b & 0xF);
/* 119 */     return nicePrint2(b1) + nicePrint2(b2);
/*     */   }
/*     */   
/*     */   public static String nicePrint2(byte b)
/*     */   {
/* 124 */     String out = "";
/* 125 */     switch (b) {
/*     */     case 0: 
/* 127 */       out = "0";
/* 128 */       break;
/*     */     case 1: 
/* 130 */       out = "1";
/* 131 */       break;
/*     */     case 2: 
/* 133 */       out = "2";
/* 134 */       break;
/*     */     case 3: 
/* 136 */       out = "3";
/* 137 */       break;
/*     */     case 4: 
/* 139 */       out = "4";
/* 140 */       break;
/*     */     case 5: 
/* 142 */       out = "5";
/* 143 */       break;
/*     */     case 6: 
/* 145 */       out = "6";
/* 146 */       break;
/*     */     case 7: 
/* 148 */       out = "7";
/* 149 */       break;
/*     */     case 8: 
/* 151 */       out = "8";
/* 152 */       break;
/*     */     case 9: 
/* 154 */       out = "9";
/* 155 */       break;
/*     */     case 10: 
/* 157 */       out = "A";
/* 158 */       break;
/*     */     case 11: 
/* 160 */       out = "B";
/* 161 */       break;
/*     */     case 12: 
/* 163 */       out = "C";
/* 164 */       break;
/*     */     case 13: 
/* 166 */       out = "D";
/* 167 */       break;
/*     */     case 14: 
/* 169 */       out = "E";
/* 170 */       break;
/*     */     case 15: 
/* 172 */       out = "F";
/*     */     }
/*     */     
/* 175 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String encodeString(byte[] bytes)
/*     */   {
/* 187 */     return nicePrint(bytes, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String encodeStringFully(byte[] bytes)
/*     */   {
/* 194 */     return nicePrint(bytes, true, Integer.MAX_VALUE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String encodeString(byte[] bytes, int offset, int len)
/*     */   {
/* 203 */     byte[] x = new byte[len];
/*     */     
/* 205 */     System.arraycopy(bytes, offset, x, 0, len);
/*     */     
/* 207 */     return nicePrint(x, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static byte[] decodeString(String str)
/*     */   {
/* 214 */     char[] chars = str.toCharArray();
/*     */     
/* 216 */     int chars_length = chars.length - chars.length % 2;
/*     */     
/* 218 */     byte[] res = new byte[chars_length / 2];
/*     */     
/* 220 */     for (int i = 0; i < chars_length; i += 2)
/*     */     {
/* 222 */       String b = new String(chars, i, 2);
/*     */       
/* 224 */       res[(i / 2)] = ((byte)Integer.parseInt(b, 16));
/*     */     }
/*     */     
/* 227 */     return res;
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
/*     */   public static int byteArrayToInt(byte[] array)
/*     */   {
/* 242 */     return array[0] << 24 & 0xFF000000 | array[1] << 16 & 0xFF0000 | array[2] << 8 & 0xFF00 | array[3] & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] intToByteArray(long v)
/*     */   {
/* 250 */     return new byte[] { (byte)(int)(v >>> 24), (byte)(int)(v >>> 16), (byte)(int)(v >>> 8), (byte)(int)(v >>> 0) };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] longToByteArray(long v)
/*     */   {
/* 261 */     return new byte[] { (byte)(int)(v >>> 56), (byte)(int)(v >>> 48), (byte)(int)(v >>> 40), (byte)(int)(v >>> 32), (byte)(int)(v >>> 24), (byte)(int)(v >>> 16), (byte)(int)(v >>> 8), (byte)(int)(v >>> 0) };
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ByteFormatter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */