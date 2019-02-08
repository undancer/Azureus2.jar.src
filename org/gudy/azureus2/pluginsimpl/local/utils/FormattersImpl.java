/*     */ package org.gudy.azureus2.pluginsimpl.local.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.utils.Formatters;
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
/*     */ public class FormattersImpl
/*     */   implements Formatters
/*     */ {
/*     */   public String formatByteCountToKiBEtc(long bytes)
/*     */   {
/*  51 */     return DisplayFormatters.formatByteCountToKiBEtc(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String formatByteCountToKiBEtcPerSec(long bytes)
/*     */   {
/*  58 */     return DisplayFormatters.formatByteCountToKiBEtcPerSec(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String formatPercentFromThousands(long thousands)
/*     */   {
/*  65 */     return DisplayFormatters.formatPercentFromThousands((int)thousands);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String formatByteArray(byte[] data, boolean no_spaces)
/*     */   {
/*  73 */     return ByteFormatter.nicePrint(data, no_spaces);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String encodeBytesToString(byte[] bytes)
/*     */   {
/*  80 */     return ByteFormatter.encodeString(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] decodeBytesFromString(String str)
/*     */   {
/*  87 */     return ByteFormatter.decodeString(str);
/*     */   }
/*     */   
/*     */   public String formatDate(long millis) {
/*  91 */     return DisplayFormatters.formatCustomDateTime(millis);
/*     */   }
/*     */   
/*     */   public String formatTimeOnly(long millis) {
/*  95 */     return DisplayFormatters.formatCustomTimeOnly(millis);
/*     */   }
/*     */   
/*     */   public String formatTimeOnly(long millis, boolean with_secs) {
/*  99 */     return DisplayFormatters.formatCustomTimeOnly(millis, with_secs);
/*     */   }
/*     */   
/*     */   public String formatDateOnly(long millis) {
/* 103 */     return DisplayFormatters.formatCustomDateOnly(millis);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String formatTimeFromSeconds(long seconds)
/*     */   {
/* 111 */     return DisplayFormatters.formatTime(seconds * 1000L);
/*     */   }
/*     */   
/*     */   public String formatETAFromSeconds(long seconds)
/*     */   {
/* 116 */     return TimeFormatter.format(seconds);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] bEncode(Map map)
/*     */     throws IOException
/*     */   {
/* 126 */     return BEncoder.encode(map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map bDecode(byte[] data)
/*     */     throws IOException
/*     */   {
/* 135 */     return BDecoder.decode(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String base32Encode(byte[] data)
/*     */   {
/* 142 */     return Base32.encode(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] base32Decode(String data)
/*     */   {
/* 149 */     return Base32.decode(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Comparator getAlphanumericComparator(final boolean ignore_case)
/*     */   {
/* 156 */     new Comparator()
/*     */     {
/*     */ 
/*     */ 
/*     */       public int compare(Object o1, Object o2)
/*     */       {
/*     */ 
/*     */ 
/* 164 */         if (((o1 instanceof String)) && ((o2 instanceof String)))
/*     */         {
/*     */ 
/* 167 */           String s1 = (String)o1;
/* 168 */           String s2 = (String)o2;
/*     */           
/* 170 */           int l1 = s1.length();
/* 171 */           int l2 = s2.length();
/*     */           
/* 173 */           int c1_pos = 0;
/* 174 */           int c2_pos = 0;
/*     */           
/* 176 */           while ((c1_pos < l1) && (c2_pos < l2))
/*     */           {
/* 178 */             char c1 = s1.charAt(c1_pos++);
/* 179 */             char c2 = s2.charAt(c2_pos++);
/*     */             
/* 181 */             if ((Character.isDigit(c1)) && (Character.isDigit(c2)))
/*     */             {
/* 183 */               int n1_pos = c1_pos - 1;
/* 184 */               int n2_pos = c2_pos - 1;
/*     */               
/* 186 */               while (c1_pos < l1)
/*     */               {
/* 188 */                 if (!Character.isDigit(s1.charAt(c1_pos))) {
/*     */                   break;
/*     */                 }
/*     */                 
/*     */ 
/* 193 */                 c1_pos++;
/*     */               }
/*     */               
/* 196 */               while (c2_pos < l2)
/*     */               {
/* 198 */                 if (!Character.isDigit(s2.charAt(c2_pos))) {
/*     */                   break;
/*     */                 }
/*     */                 
/*     */ 
/* 203 */                 c2_pos++;
/*     */               }
/*     */               
/* 206 */               int n1_length = c1_pos - n1_pos;
/* 207 */               int n2_length = c2_pos - n2_pos;
/*     */               
/* 209 */               if (n1_length != n2_length)
/*     */               {
/* 211 */                 return n1_length - n2_length;
/*     */               }
/*     */               
/* 214 */               for (int i = 0; i < n1_length; i++)
/*     */               {
/* 216 */                 char nc1 = s1.charAt(n1_pos++);
/* 217 */                 char nc2 = s2.charAt(n2_pos++);
/*     */                 
/* 219 */                 if (nc1 != nc2)
/*     */                 {
/* 221 */                   return nc1 - nc2;
/*     */                 }
/*     */               }
/*     */             }
/*     */             else {
/* 226 */               if (ignore_case)
/*     */               {
/* 228 */                 c1 = Character.toLowerCase(c1);
/*     */                 
/* 230 */                 c2 = Character.toLowerCase(c2);
/*     */               }
/*     */               
/* 233 */               if (c1 != c2)
/*     */               {
/* 235 */                 return c1 - c2;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 240 */           return l1 - l2;
/*     */         }
/*     */         
/*     */ 
/* 244 */         return 0;
/*     */       }
/*     */     };
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/FormattersImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */