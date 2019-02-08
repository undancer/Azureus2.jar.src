/*     */ package com.aelitis.azureus.core.metasearch.impl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TimeZone;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.xml.rss.RSSUtils;
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
/*     */ public class DateParserClassic
/*     */   extends DateParser
/*     */ {
/*  34 */   static boolean DEBUG = false;
/*     */   
/*     */   TimeZone timeZone;
/*     */   
/*     */   DateFormat ddMMMyyyyFormat;
/*     */   DateFormat ddMMMyyFormat;
/*     */   DateFormat MMddyyyyFormat;
/*     */   DateFormat userDateFormat;
/*     */   boolean auto;
/*     */   
/*     */   public DateParserClassic()
/*     */   {
/*  46 */     this("GMT", true, null);
/*     */   }
/*     */   
/*     */   public DateParserClassic(String timeZone, boolean auto, String dateFormat)
/*     */   {
/*  51 */     this.timeZone = TimeZone.getTimeZone(timeZone);
/*  52 */     this.auto = auto;
/*     */     
/*  54 */     if ((!auto) && 
/*  55 */       (dateFormat != null)) {
/*  56 */       this.userDateFormat = new SimpleDateFormat(dateFormat);
/*  57 */       this.userDateFormat.setTimeZone(this.timeZone);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  63 */     this.ddMMMyyyyFormat = new SimpleDateFormat("dd MMM yyyy");
/*  64 */     this.ddMMMyyyyFormat.setTimeZone(this.timeZone);
/*     */     
/*  66 */     this.ddMMMyyFormat = new SimpleDateFormat("dd MMM yy");
/*  67 */     this.ddMMMyyFormat.setTimeZone(this.timeZone);
/*     */     
/*  69 */     this.MMddyyyyFormat = new SimpleDateFormat("MM-dd yyyy");
/*  70 */     this.MMddyyyyFormat.setTimeZone(this.timeZone);
/*     */   }
/*     */   
/*     */   public Date parseDate(String date)
/*     */   {
/*  75 */     Date result = null;
/*  76 */     if (this.auto) {
/*  77 */       result = parseDateInternal(date);
/*     */     }
/*  79 */     else if (this.userDateFormat != null) {
/*     */       try {
/*  81 */         result = this.userDateFormat.parse(date);
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/*     */ 
/*  87 */     if (DEBUG) {
/*  88 */       System.out.println(date + " > " + (result == null ? "null" : result.toString()));
/*     */     }
/*     */     
/*  91 */     return result;
/*     */   }
/*     */   
/*     */   private Date parseDateInternal(String s) {
/*  95 */     if (s == null) {
/*  96 */       return null;
/*     */     }
/*  98 */     s = s.toLowerCase().trim();
/*     */     
/*     */ 
/*     */ 
/* 102 */     Date d = RSSUtils.parseRSSDate(s);
/* 103 */     if (d != null) {
/* 104 */       return d;
/*     */     }
/*     */     
/* 107 */     if ((s.startsWith("today ")) || (s.startsWith("y-day "))) {
/*     */       try {
/* 109 */         Calendar calendar = new GregorianCalendar();
/* 110 */         calendar.setTimeZone(this.timeZone);
/* 111 */         String time = s.substring(6);
/* 112 */         StringTokenizer st = new StringTokenizer(time, ":");
/* 113 */         int hours = Integer.parseInt(st.nextToken());
/* 114 */         int minutes = Integer.parseInt(st.nextToken());
/* 115 */         calendar.set(11, hours);
/* 116 */         calendar.set(12, minutes);
/* 117 */         if (s.startsWith("y-day ")) {
/* 118 */           calendar.add(5, -1);
/*     */         }
/* 120 */         return calendar.getTime();
/*     */       } catch (Exception e) {
/* 122 */         e.printStackTrace();
/* 123 */         return null;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 128 */     if (s.length() > 3) {
/* 129 */       String thirdCharacter = s.substring(2, 3);
/*     */       
/*     */ 
/* 132 */       if (thirdCharacter.equals("-")) {
/* 133 */         if (s.length() > 9) {
/* 134 */           String ninthCharacter = s.substring(8, 9);
/*     */           
/* 136 */           if (ninthCharacter.equals(":")) {
/*     */             try {
/* 138 */               int month = Integer.parseInt(s.substring(0, 2));
/* 139 */               int day = Integer.parseInt(s.substring(3, 5));
/* 140 */               int hours = Integer.parseInt(s.substring(6, 8));
/* 141 */               int minutes = Integer.parseInt(s.substring(9, 11));
/* 142 */               Calendar calendar = new GregorianCalendar();
/* 143 */               calendar.setTimeZone(this.timeZone);
/* 144 */               calendar.set(2, month - 1);
/* 145 */               calendar.set(5, day);
/* 146 */               calendar.set(11, hours);
/* 147 */               calendar.set(12, minutes);
/* 148 */               return calendar.getTime();
/*     */             } catch (Exception e) {
/* 150 */               e.printStackTrace();
/*     */             }
/*     */             
/*     */           } else {
/*     */             try
/*     */             {
/* 156 */               return this.MMddyyyyFormat.parse(s);
/*     */             } catch (Exception e) {
/* 158 */               e.printStackTrace();
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/*     */           try {
/* 164 */             int month = Integer.parseInt(s.substring(0, 2));
/* 165 */             int day = Integer.parseInt(s.substring(3, 5));
/* 166 */             Calendar calendar = new GregorianCalendar();
/* 167 */             calendar.setTimeZone(this.timeZone);
/* 168 */             calendar.set(2, month);
/* 169 */             calendar.set(5, day);
/* 170 */             return calendar.getTime();
/*     */           } catch (Exception e) {
/* 172 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/* 176 */       else if ((s.length() == 9) && (s.contains(" "))) {
/*     */         try
/*     */         {
/* 179 */           return this.ddMMMyyFormat.parse(s);
/*     */         } catch (Exception e) {
/* 181 */           if (DEBUG) { e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 186 */       if (thirdCharacter.equals(" ")) {
/*     */         try {
/* 188 */           return this.ddMMMyyyyFormat.parse(s);
/*     */         } catch (Exception e) {
/* 190 */           if (DEBUG) { e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 196 */     if ((s.endsWith(" ago")) || (s.contains("month")) || (s.contains("hour")) || (s.contains("day")) || (s.contains("week")) || (s.contains("year")))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 203 */       s = s.replaceAll(" ago", "");
/* 204 */       StringTokenizer st = new StringTokenizer(s, " ");
/* 205 */       if (st.countTokens() >= 2) {
/*     */         try {
/* 207 */           Calendar calendar = new GregorianCalendar();
/* 208 */           while (st.hasMoreTokens()) {
/* 209 */             float value = Float.parseFloat(st.nextToken());
/* 210 */             String unit = st.nextToken();
/*     */             
/* 212 */             calendar.setTimeZone(this.timeZone);
/* 213 */             if (unit.startsWith("min")) {
/* 214 */               calendar.add(12, -(int)value);
/*     */             }
/* 216 */             if (unit.startsWith("hour")) {
/* 217 */               calendar.add(11, -(int)value);
/*     */             }
/* 219 */             if (unit.startsWith("day")) {
/* 220 */               calendar.add(5, -(int)value);
/*     */             }
/* 222 */             if (unit.startsWith("week")) {
/* 223 */               calendar.add(3, -(int)value);
/*     */             }
/* 225 */             if (unit.startsWith("month")) {
/* 226 */               calendar.add(2, -(int)value);
/*     */             }
/* 228 */             if (unit.startsWith("year")) {
/* 229 */               calendar.add(1, -(int)value);
/*     */             }
/*     */           }
/* 232 */           return calendar.getTime();
/*     */         } catch (Exception e) {
/* 234 */           if (DEBUG) { e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 239 */     if (s.equals("today")) {
/* 240 */       Calendar calendar = new GregorianCalendar();
/* 241 */       calendar.setTimeZone(this.timeZone);
/*     */       
/*     */ 
/* 244 */       return calendar.getTime();
/*     */     }
/*     */     
/* 247 */     if (s.equals("yesterday")) {
/* 248 */       Calendar calendar = new GregorianCalendar();
/* 249 */       calendar.setTimeZone(this.timeZone);
/* 250 */       calendar.add(5, -1);
/*     */       
/*     */ 
/* 253 */       return calendar.getTime();
/*     */     }
/*     */     try
/*     */     {
/* 257 */       StringTokenizer st = new StringTokenizer(s, " ");
/* 258 */       Calendar calendar = new GregorianCalendar();
/* 259 */       calendar.setTimeZone(this.timeZone);
/* 260 */       while (st.hasMoreTokens()) {
/* 261 */         String element = st.nextToken();
/* 262 */         int field = -1;
/* 263 */         int end_offset = -1;
/* 264 */         if (element.endsWith("h")) {
/* 265 */           field = 11;
/* 266 */           end_offset = 1;
/*     */         }
/* 268 */         if (element.endsWith("d")) {
/* 269 */           field = 5;
/* 270 */           end_offset = 1;
/*     */         }
/* 272 */         if (element.endsWith("w")) {
/* 273 */           field = 3;
/* 274 */           end_offset = 1;
/*     */         }
/* 276 */         if (element.endsWith("m")) {
/* 277 */           field = 2;
/* 278 */           end_offset = 1;
/*     */         }
/* 280 */         if (element.endsWith("mon")) {
/* 281 */           field = 2;
/* 282 */           end_offset = 3;
/*     */         }
/* 284 */         if (element.endsWith("y")) {
/* 285 */           field = 1;
/* 286 */           end_offset = 1;
/*     */         }
/* 288 */         if ((field != -1) && (end_offset != -1)) {
/* 289 */           int value = (int)Float.parseFloat(element.substring(0, element.length() - end_offset));
/* 290 */           calendar.add(field, -value);
/*     */         }
/*     */       }
/*     */       
/* 294 */       return calendar.getTime();
/*     */     }
/*     */     catch (Exception e) {
/* 297 */       if (DEBUG) { e.printStackTrace();
/*     */       }
/*     */     }
/* 300 */     return null;
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/* 305 */     DEBUG = true;
/* 306 */     DateParserClassic dateParser = new DateParserClassic();
/*     */     
/* 308 */     dateParser.parseDate("Today 05:34");
/* 309 */     dateParser.parseDate("Y-Day 21:55");
/* 310 */     dateParser.parseDate("07-25 2006");
/* 311 */     dateParser.parseDate("02-01 02:53");
/* 312 */     dateParser.parseDate("03 Mar 2006");
/* 313 */     dateParser.parseDate("0 minute ago");
/* 314 */     dateParser.parseDate("3 hours ago");
/* 315 */     dateParser.parseDate("2 days ago");
/* 316 */     dateParser.parseDate("10 months ago");
/* 317 */     dateParser.parseDate("45 mins ago");
/* 318 */     dateParser.parseDate("Today");
/* 319 */     dateParser.parseDate("Yesterday");
/* 320 */     dateParser.parseDate("16.9w");
/* 321 */     dateParser.parseDate("22.6h");
/* 322 */     dateParser.parseDate("1.7d");
/* 323 */     dateParser.parseDate("2d 7h");
/* 324 */     dateParser.parseDate("1w");
/* 325 */     dateParser.parseDate("1w 4d");
/* 326 */     dateParser.parseDate("1mon 1w");
/* 327 */     dateParser.parseDate("2013-08-11T18:30:00.000Z");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/DateParserClassic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */