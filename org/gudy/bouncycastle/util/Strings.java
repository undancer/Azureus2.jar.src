/*     */ package org.gudy.bouncycastle.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public final class Strings
/*     */ {
/*     */   public static String fromUTF8ByteArray(byte[] bytes)
/*     */   {
/*  10 */     int i = 0;
/*  11 */     int length = 0;
/*     */     
/*  13 */     while (i < bytes.length)
/*     */     {
/*  15 */       length++;
/*  16 */       if ((bytes[i] & 0xF0) == 240)
/*     */       {
/*     */ 
/*  19 */         length++;
/*  20 */         i += 4;
/*     */       }
/*  22 */       else if ((bytes[i] & 0xE0) == 224)
/*     */       {
/*  24 */         i += 3;
/*     */       }
/*  26 */       else if ((bytes[i] & 0xC0) == 192)
/*     */       {
/*  28 */         i += 2;
/*     */       }
/*     */       else
/*     */       {
/*  32 */         i++;
/*     */       }
/*     */     }
/*     */     
/*  36 */     char[] cs = new char[length];
/*     */     
/*  38 */     i = 0;
/*  39 */     length = 0;
/*     */     
/*  41 */     while (i < bytes.length)
/*     */     {
/*     */       char ch;
/*     */       
/*  45 */       if ((bytes[i] & 0xF0) == 240)
/*     */       {
/*  47 */         int codePoint = (bytes[i] & 0x3) << 18 | (bytes[(i + 1)] & 0x3F) << 12 | (bytes[(i + 2)] & 0x3F) << 6 | bytes[(i + 3)] & 0x3F;
/*  48 */         int U = codePoint - 65536;
/*  49 */         char W1 = (char)(0xD800 | U >> 10);
/*  50 */         char W2 = (char)(0xDC00 | U & 0x3FF);
/*  51 */         cs[(length++)] = W1;
/*  52 */         char ch = W2;
/*  53 */         i += 4;
/*     */       }
/*  55 */       else if ((bytes[i] & 0xE0) == 224)
/*     */       {
/*  57 */         char ch = (char)((bytes[i] & 0xF) << 12 | (bytes[(i + 1)] & 0x3F) << 6 | bytes[(i + 2)] & 0x3F);
/*     */         
/*  59 */         i += 3;
/*     */       }
/*  61 */       else if ((bytes[i] & 0xD0) == 208)
/*     */       {
/*  63 */         char ch = (char)((bytes[i] & 0x1F) << 6 | bytes[(i + 1)] & 0x3F);
/*  64 */         i += 2;
/*     */       }
/*  66 */       else if ((bytes[i] & 0xC0) == 192)
/*     */       {
/*  68 */         char ch = (char)((bytes[i] & 0x1F) << 6 | bytes[(i + 1)] & 0x3F);
/*  69 */         i += 2;
/*     */       }
/*     */       else
/*     */       {
/*  73 */         ch = (char)(bytes[i] & 0xFF);
/*  74 */         i++;
/*     */       }
/*     */       
/*  77 */       cs[(length++)] = ch;
/*     */     }
/*     */     
/*  80 */     return new String(cs);
/*     */   }
/*     */   
/*     */   public static byte[] toUTF8ByteArray(String string)
/*     */   {
/*  85 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*  86 */     char[] c = string.toCharArray();
/*  87 */     int i = 0;
/*     */     
/*  89 */     while (i < c.length)
/*     */     {
/*  91 */       char ch = c[i];
/*     */       
/*  93 */       if (ch < '')
/*     */       {
/*  95 */         bOut.write(ch);
/*     */       }
/*  97 */       else if (ch < 'ࠀ')
/*     */       {
/*  99 */         bOut.write(0xC0 | ch >> '\006');
/* 100 */         bOut.write(0x80 | ch & 0x3F);
/*     */ 
/*     */       }
/* 103 */       else if ((ch >= 55296) && (ch <= 57343))
/*     */       {
/*     */ 
/*     */ 
/* 107 */         if (i + 1 >= c.length)
/*     */         {
/* 109 */           throw new IllegalStateException("invalid UTF-16 codepoint");
/*     */         }
/* 111 */         char W1 = ch;
/* 112 */         ch = c[(++i)];
/* 113 */         char W2 = ch;
/*     */         
/*     */ 
/* 116 */         if (W1 > 56319)
/*     */         {
/* 118 */           throw new IllegalStateException("invalid UTF-16 codepoint");
/*     */         }
/* 120 */         int codePoint = ((W1 & 0x3FF) << '\n' | W2 & 0x3FF) + 65536;
/* 121 */         bOut.write(0xF0 | codePoint >> 18);
/* 122 */         bOut.write(0x80 | codePoint >> 12 & 0x3F);
/* 123 */         bOut.write(0x80 | codePoint >> 6 & 0x3F);
/* 124 */         bOut.write(0x80 | codePoint & 0x3F);
/*     */       }
/*     */       else
/*     */       {
/* 128 */         bOut.write(0xE0 | ch >> '\f');
/* 129 */         bOut.write(0x80 | ch >> '\006' & 0x3F);
/* 130 */         bOut.write(0x80 | ch & 0x3F);
/*     */       }
/*     */       
/* 133 */       i++;
/*     */     }
/*     */     
/* 136 */     return bOut.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toUpperCase(String string)
/*     */   {
/* 147 */     boolean changed = false;
/* 148 */     char[] chars = string.toCharArray();
/*     */     
/* 150 */     for (int i = 0; i != chars.length; i++)
/*     */     {
/* 152 */       char ch = chars[i];
/* 153 */       if (('a' <= ch) && ('z' >= ch))
/*     */       {
/* 155 */         changed = true;
/* 156 */         chars[i] = ((char)(ch - 'a' + 65));
/*     */       }
/*     */     }
/*     */     
/* 160 */     if (changed)
/*     */     {
/* 162 */       return new String(chars);
/*     */     }
/*     */     
/* 165 */     return string;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toLowerCase(String string)
/*     */   {
/* 176 */     boolean changed = false;
/* 177 */     char[] chars = string.toCharArray();
/*     */     
/* 179 */     for (int i = 0; i != chars.length; i++)
/*     */     {
/* 181 */       char ch = chars[i];
/* 182 */       if (('A' <= ch) && ('Z' >= ch))
/*     */       {
/* 184 */         changed = true;
/* 185 */         chars[i] = ((char)(ch - 'A' + 97));
/*     */       }
/*     */     }
/*     */     
/* 189 */     if (changed)
/*     */     {
/* 191 */       return new String(chars);
/*     */     }
/*     */     
/* 194 */     return string;
/*     */   }
/*     */   
/*     */   public static byte[] toByteArray(String string)
/*     */   {
/* 199 */     byte[] bytes = new byte[string.length()];
/*     */     
/* 201 */     for (int i = 0; i != bytes.length; i++)
/*     */     {
/* 203 */       char ch = string.charAt(i);
/*     */       
/* 205 */       bytes[i] = ((byte)ch);
/*     */     }
/*     */     
/* 208 */     return bytes;
/*     */   }
/*     */   
/*     */   public static String[] split(String input, char delimiter)
/*     */   {
/* 213 */     Vector v = new Vector();
/* 214 */     boolean moreTokens = true;
/*     */     
/*     */ 
/* 217 */     while (moreTokens)
/*     */     {
/* 219 */       int tokenLocation = input.indexOf(delimiter);
/* 220 */       if (tokenLocation > 0)
/*     */       {
/* 222 */         String subString = input.substring(0, tokenLocation);
/* 223 */         v.addElement(subString);
/* 224 */         input = input.substring(tokenLocation + 1);
/*     */       }
/*     */       else
/*     */       {
/* 228 */         moreTokens = false;
/* 229 */         v.addElement(input);
/*     */       }
/*     */     }
/*     */     
/* 233 */     String[] res = new String[v.size()];
/*     */     
/* 235 */     for (int i = 0; i != res.length; i++)
/*     */     {
/* 237 */       res[i] = ((String)v.elementAt(i));
/*     */     }
/* 239 */     return res;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/util/Strings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */