/*     */ package com.aelitis.azureus.core.metasearch.impl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class DateParserRegex
/*     */   extends DateParser
/*     */ {
/*  32 */   static boolean DEBUG = false;
/*     */   
/*     */   TimeZone timeZone;
/*     */   
/*     */   DateFormat userDateFormat;
/*     */   boolean auto;
/*  38 */   private static final Pattern hasLettersPattern = Pattern.compile("(?i).*[a-z]");
/*  39 */   private static final Pattern isAgeBasedPattern = Pattern.compile("(?i)(ago)|(min)|(hour)|(day)|(week)|(month)|(year)|([0-9](h|d|w|m|y))");
/*  40 */   private static final Map<String, Pattern> isAgeBasedPatternCN = new HashMap();
/*  41 */   private static final Pattern getTimeComponent = Pattern.compile("(?i)([0-9]{2}):([0-9]{2})(:([0-9]{2}))?( ?(a|p)m)?");
/*  42 */   private static final Pattern timeBasedDateWithLettersPattern = Pattern.compile("(?i)([0-9]{1,2})[^ ]{0,2}(?: |-)([a-z]{3,10})\\.?(?: |-)?([0-9]{2,4})?");
/*  43 */   private static final Pattern timeBasedDateWithLettersPatternMonthFirst = Pattern.compile("(?i)([a-z]{3,10})\\.?(?: |-)?([0-9]{1,2})[^ ]{0,2}(?: |-)([0-9]{2,4})?");
/*  44 */   private static final Pattern todayPattern = Pattern.compile("(?i)(t.?day)");
/*  45 */   private static final Pattern yesterdayPattern = Pattern.compile("(?i)(y[a-z\\-]+day)");
/*  46 */   private static final Pattern agoSpacerPattern = Pattern.compile("(?i)([0-9])([a-z])");
/*  47 */   private static final Pattern agoTimeRangePattern = Pattern.compile("(?i)([0-9.]+) ([a-z\\(\\)]+)");
/*  48 */   private static final Pattern numbersOnlyDatePattern = Pattern.compile("([0-9]{2,4})[ \\-\\./]([0-9]{2,4})[ \\-\\./]?([0-9]{2,4})?");
/*     */   
/*  50 */   private static final String[] MONTHS_LIST = { " january janvier enero januar", " february fevrier février febrero februar", " march mars marzo marz marz märz", " april avril abril april ", " may mai mayo mai", " june juin junio juni", " july juillet julio juli", " august aout août agosto august", " september septembre septiembre september", " october octobre octubre oktober", " november novembre noviembre november", " december decembre décembre diciembre dezember" };
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
/*     */   static
/*     */   {
/*  65 */     isAgeBasedPatternCN.put("min", Pattern.compile("([0-9]+)\\s*分钟前"));
/*  66 */     isAgeBasedPatternCN.put("hour", Pattern.compile("([0-9]+)\\s*小时前"));
/*  67 */     isAgeBasedPatternCN.put("day", Pattern.compile("([0-9]+)\\s*天前"));
/*  68 */     isAgeBasedPatternCN.put("week", Pattern.compile("([0-9]+)\\s*周前"));
/*  69 */     isAgeBasedPatternCN.put("month", Pattern.compile("([0-9]+)\\s*个月前"));
/*  70 */     isAgeBasedPatternCN.put("year", Pattern.compile("([0-9]+)\\s*年前"));
/*     */   }
/*     */   
/*     */   public DateParserRegex() {
/*  74 */     this("GMT-7", true, null);
/*     */   }
/*     */   
/*     */   public DateParserRegex(String timeZone, boolean auto, String dateFormat)
/*     */   {
/*  79 */     this.timeZone = TimeZone.getTimeZone(timeZone);
/*  80 */     this.auto = auto;
/*     */     
/*  82 */     if ((!auto) && 
/*  83 */       (dateFormat != null)) {
/*  84 */       this.userDateFormat = new SimpleDateFormat(dateFormat);
/*  85 */       this.userDateFormat.setTimeZone(this.timeZone);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date parseDate(String date)
/*     */   {
/*  94 */     Date result = null;
/*  95 */     if (this.auto) {
/*  96 */       result = parseDateInternal(date);
/*     */     }
/*  98 */     else if (this.userDateFormat != null) {
/*     */       try {
/* 100 */         result = this.userDateFormat.parse(date);
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/*     */ 
/* 106 */     if ((DEBUG) && (result != null)) {
/* 107 */       System.out.println(date + " > " + result.toString());
/*     */     }
/*     */     
/* 110 */     return result;
/*     */   }
/*     */   
/*     */   private Date parseDateInternal(String input)
/*     */   {
/* 115 */     if (input == null) {
/* 116 */       return null;
/*     */     }
/*     */     
/* 119 */     String s = input;
/*     */     
/* 121 */     Calendar calendar = new GregorianCalendar(this.timeZone);
/*     */     
/*     */ 
/* 124 */     Matcher matcher = getTimeComponent.matcher(s);
/*     */     
/* 126 */     s = matcher.replaceFirst("").trim();
/*     */     
/*     */ 
/*     */ 
/* 130 */     if (s.endsWith(" at"))
/*     */     {
/* 132 */       s = s.substring(0, s.length() - 3).trim();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 137 */     matcher = hasLettersPattern.matcher(s);
/* 138 */     if (matcher.find())
/*     */     {
/*     */ 
/*     */ 
/* 142 */       matcher = isAgeBasedPattern.matcher(s);
/* 143 */       if (matcher.find())
/*     */       {
/*     */ 
/* 146 */         matcher = todayPattern.matcher(s);
/* 147 */         if (!matcher.find())
/*     */         {
/*     */ 
/* 150 */           matcher = yesterdayPattern.matcher(s);
/* 151 */           if (matcher.find()) {
/* 152 */             calendar.add(5, -1);
/*     */           }
/*     */           else {
/* 155 */             s = s.replaceAll("ago", "").trim();
/* 156 */             matcher = agoSpacerPattern.matcher(s);
/* 157 */             s = matcher.replaceAll("$1 $2");
/* 158 */             matcher = agoTimeRangePattern.matcher(s);
/* 159 */             boolean seenHoursAsLowerCaseH = false;
/* 160 */             while (matcher.find()) {
/* 161 */               String unit = matcher.group(2);
/*     */               
/* 163 */               if (unit.equals("h")) {
/* 164 */                 seenHoursAsLowerCaseH = true;
/*     */               }
/*     */               
/*     */ 
/* 168 */               float value = Float.parseFloat(matcher.group(1));
/* 169 */               int intValue = (int)value;
/* 170 */               adjustDate(calendar, unit, value, intValue, seenHoursAsLowerCaseH);
/*     */ 
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 183 */         matcher = timeBasedDateWithLettersPattern.matcher(s);
/* 184 */         if (matcher.find()) {
/* 185 */           int day = Integer.parseInt(matcher.group(1));
/* 186 */           calendar.set(5, day);
/*     */           
/* 188 */           String monthStr = " " + matcher.group(2).toLowerCase();
/* 189 */           int month = -1;
/* 190 */           for (int i = 0; i < MONTHS_LIST.length; i++) {
/* 191 */             if (MONTHS_LIST[i].contains(monthStr)) {
/* 192 */               month = i;
/*     */             }
/*     */           }
/* 195 */           if (month > -1) {
/* 196 */             calendar.set(2, month);
/*     */           }
/*     */           
/* 199 */           boolean hasYear = matcher.group(3) != null;
/* 200 */           if (hasYear) {
/* 201 */             int year = Integer.parseInt(matcher.group(3));
/* 202 */             if (year < 100) {
/* 203 */               year += 2000;
/*     */             }
/* 205 */             calendar.set(1, year);
/*     */           }
/*     */           
/* 208 */           calendar.set(11, 0);
/* 209 */           calendar.set(12, 0);
/* 210 */           calendar.set(13, 0);
/* 211 */           calendar.set(14, 0);
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 216 */           matcher = timeBasedDateWithLettersPatternMonthFirst.matcher(s);
/* 217 */           if (matcher.find()) {
/* 218 */             int day = Integer.parseInt(matcher.group(2));
/* 219 */             calendar.set(5, day);
/*     */             
/* 221 */             String monthStr = " " + matcher.group(1).toLowerCase();
/* 222 */             int month = -1;
/* 223 */             for (int i = 0; i < MONTHS_LIST.length; i++) {
/* 224 */               if (MONTHS_LIST[i].contains(monthStr)) {
/* 225 */                 month = i;
/*     */               }
/*     */             }
/* 228 */             if (month > -1) {
/* 229 */               calendar.set(2, month);
/*     */             }
/*     */             
/* 232 */             boolean hasYear = matcher.group(3) != null;
/* 233 */             if (hasYear) {
/* 234 */               int year = Integer.parseInt(matcher.group(3));
/* 235 */               if (year < 100) {
/* 236 */                 year += 2000;
/*     */               }
/* 238 */               calendar.set(1, year);
/*     */             }
/*     */             
/* 241 */             calendar.set(11, 0);
/* 242 */             calendar.set(12, 0);
/* 243 */             calendar.set(13, 0);
/* 244 */             calendar.set(14, 0);
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 249 */             Date d = new DateParserClassic().parseDate(input);
/*     */             
/* 251 */             if (d != null) {
/* 252 */               return d;
/*     */             }
/*     */             
/* 255 */             System.err.println("DateParserRegex: Unparseable date : " + input);
/*     */           }
/*     */         }
/*     */       }
/*     */     } else {
/* 260 */       for (String unit : isAgeBasedPatternCN.keySet()) {
/* 261 */         Pattern p = (Pattern)isAgeBasedPatternCN.get(unit);
/* 262 */         matcher = p.matcher(s);
/* 263 */         if (matcher.find()) {
/*     */           try {
/* 265 */             int intValue = Integer.parseInt(matcher.group(1));
/* 266 */             adjustDate(calendar, unit, intValue, intValue, false);
/*     */             
/* 268 */             return calendar.getTime();
/*     */           } catch (Throwable t) {
/* 270 */             t.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 279 */       matcher = numbersOnlyDatePattern.matcher(s);
/* 280 */       if (matcher.find()) {
/*     */         try
/*     */         {
/* 283 */           String g1 = matcher.group(1);
/* 284 */           String g2 = matcher.group(2);
/* 285 */           String g3 = matcher.group(3);
/*     */           
/* 287 */           int i1 = Integer.parseInt(g1);
/* 288 */           int i2 = Integer.parseInt(g2);
/*     */           
/* 290 */           if (g3 != null) {
/* 291 */             int i3 = Integer.parseInt(g3);
/*     */             
/* 293 */             int day = i1;
/* 294 */             int month = i2;
/* 295 */             int year = i3;
/*     */             
/* 297 */             if (month > 12) {
/* 298 */               day = i2;
/* 299 */               month = i1;
/*     */             }
/*     */             
/* 302 */             if (year < 100) {
/* 303 */               year += 2000;
/*     */             }
/*     */             
/* 306 */             if (g1.length() == 4) {
/* 307 */               year = i1;
/* 308 */               day = i3;
/*     */             }
/*     */             
/* 311 */             calendar.set(1, year);
/* 312 */             calendar.set(2, month - 1);
/* 313 */             calendar.set(5, day);
/*     */           }
/*     */           else
/*     */           {
/* 317 */             int month = i1;
/* 318 */             int day = i2;
/* 319 */             if (month > 12) {
/* 320 */               day = i1;
/* 321 */               month = i2;
/*     */             }
/* 323 */             if (month > 12)
/*     */             {
/* 325 */               System.err.println("DateParserRegex: Unparseable date : " + input);
/*     */             } else {
/* 327 */               calendar.set(2, month - 1);
/* 328 */               calendar.set(5, day);
/*     */             }
/*     */           }
/*     */           
/* 332 */           calendar.set(11, 0);
/* 333 */           calendar.set(12, 0);
/* 334 */           calendar.set(13, 0);
/* 335 */           calendar.set(14, 0);
/*     */         }
/*     */         catch (Exception e) {
/* 338 */           e.printStackTrace();
/*     */         }
/*     */       } else {
/*     */         try
/*     */         {
/* 343 */           long parseLong = Long.parseLong(s);
/* 344 */           if (parseLong < SystemTime.getCurrentTime() / 1000L)
/*     */           {
/* 346 */             parseLong *= 1000L;
/*     */           }
/* 348 */           calendar.setTimeInMillis(parseLong);
/*     */         }
/*     */         catch (Throwable t) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 357 */     matcher = getTimeComponent.matcher(input);
/* 358 */     if (matcher.find()) {
/*     */       try {
/* 360 */         int hours = Integer.parseInt(matcher.group(1));
/*     */         
/* 362 */         int minutes = Integer.parseInt(matcher.group(2));
/* 363 */         calendar.set(12, minutes);
/*     */         
/* 365 */         boolean amPMModifier = matcher.group(5) != null;
/*     */         
/* 367 */         boolean hasSeconds = matcher.group(4) != null;
/*     */         
/* 369 */         if (hasSeconds) {
/* 370 */           int seconds = Integer.parseInt(matcher.group(4));
/* 371 */           calendar.set(13, seconds);
/*     */         }
/*     */         
/* 374 */         if (amPMModifier) {
/* 375 */           String amPm = matcher.group(5).trim().toLowerCase();
/* 376 */           if (amPm.equals("am")) {
/* 377 */             calendar.set(9, 0);
/*     */           } else {
/* 379 */             calendar.set(9, 1);
/*     */           }
/* 381 */           calendar.set(10, hours);
/*     */         }
/*     */         else {
/* 384 */           calendar.set(11, hours);
/*     */         }
/*     */       } catch (Exception e) {
/* 387 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 391 */     int nbBack = 0;
/* 392 */     Calendar calendarCompare = new GregorianCalendar();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 399 */     while ((calendar.after(calendarCompare)) && (nbBack++ < 50)) {
/* 400 */       calendar.add(1, -1);
/*     */     }
/*     */     
/*     */ 
/* 404 */     return calendar.getTime();
/*     */   }
/*     */   
/*     */   private void adjustDate(Calendar calendar, String unit, float value, int intValue, boolean seenHoursAsLowerCaseH)
/*     */   {
/* 409 */     String lUnit = unit.toLowerCase();
/* 410 */     if (lUnit.startsWith("sec")) {
/* 411 */       calendar.add(13, -intValue);
/* 412 */     } else if ((lUnit.startsWith("min")) || ((unit.equals("m")) && (seenHoursAsLowerCaseH))) {
/* 413 */       calendar.add(12, -intValue);
/* 414 */       int seconds = (int)((value - intValue) * 60.0F);
/* 415 */       calendar.add(13, -seconds);
/* 416 */     } else if (lUnit.startsWith("h")) {
/* 417 */       calendar.add(11, -intValue);
/* 418 */       int seconds = (int)((value - intValue) * 3600.0F);
/* 419 */       calendar.add(13, -seconds);
/* 420 */     } else if (lUnit.startsWith("d")) {
/* 421 */       calendar.add(5, -intValue);
/* 422 */       int seconds = (int)((value - intValue) * 86400.0F);
/* 423 */       calendar.add(13, -seconds);
/* 424 */     } else if (lUnit.startsWith("w")) {
/* 425 */       calendar.add(3, -intValue);
/*     */       
/* 427 */       int seconds = (int)((value - intValue) * 640800.0F);
/* 428 */       calendar.add(13, -seconds);
/*     */     }
/* 430 */     else if (lUnit.startsWith("m")) {
/* 431 */       calendar.add(2, -intValue);
/*     */       
/* 433 */       int hours = (int)((value - intValue) * 720.0F);
/* 434 */       calendar.add(11, -hours);
/* 435 */     } else if (lUnit.startsWith("y")) {
/* 436 */       calendar.add(1, -intValue);
/*     */       
/* 438 */       int hours = (int)((value - intValue) * 8760.0F);
/* 439 */       calendar.add(11, -hours);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 446 */     DEBUG = true;
/* 447 */     DateParserRegex dateParser = new DateParserRegex();
/*     */     
/* 449 */     dateParser.parseDate("Today 05:34");
/* 450 */     dateParser.parseDate("Y-Day 21:55");
/* 451 */     dateParser.parseDate("07-25 2006");
/* 452 */     dateParser.parseDate("02-01 02:53");
/* 453 */     dateParser.parseDate("02-01 02:53 am");
/* 454 */     dateParser.parseDate("02-01 02:53 pm");
/* 455 */     dateParser.parseDate("03 Mar 2006");
/* 456 */     dateParser.parseDate("0 minute ago");
/* 457 */     dateParser.parseDate("3 hours ago");
/* 458 */     dateParser.parseDate("2 days ago");
/* 459 */     dateParser.parseDate("10 months ago");
/* 460 */     dateParser.parseDate("45 mins ago");
/* 461 */     dateParser.parseDate("Today");
/* 462 */     dateParser.parseDate("Yesterday");
/* 463 */     dateParser.parseDate("16.9w");
/* 464 */     dateParser.parseDate("22.6h");
/* 465 */     dateParser.parseDate("1.7d");
/* 466 */     dateParser.parseDate("2d 7h");
/* 467 */     dateParser.parseDate("1w");
/* 468 */     dateParser.parseDate("1w 4d");
/* 469 */     dateParser.parseDate("1mon 1w");
/* 470 */     dateParser.parseDate("22.11.");
/* 471 */     dateParser.parseDate("22 Apr 08");
/* 472 */     dateParser.parseDate("3 months");
/* 473 */     dateParser.parseDate("1 day");
/* 474 */     dateParser.parseDate("3 weeks");
/* 475 */     dateParser.parseDate("1 year");
/* 476 */     dateParser.parseDate("4 hours ago");
/* 477 */     dateParser.parseDate("yesterday");
/* 478 */     dateParser.parseDate("2 days ago");
/* 479 */     dateParser.parseDate("1 month ago");
/* 480 */     dateParser.parseDate("2 months ago");
/* 481 */     dateParser.parseDate("06/18");
/* 482 */     dateParser.parseDate("02:10");
/* 483 */     dateParser.parseDate("2005-02-26 20:55:10");
/* 484 */     dateParser.parseDate("2005-02-26 10:55:10 PM");
/* 485 */     dateParser.parseDate("2005-02-26 10:55:10 AM");
/* 486 */     dateParser.parseDate("25-04-08");
/* 487 */     dateParser.parseDate("142 Day(s) ago");
/* 488 */     dateParser.parseDate("6 Minute(s) ago");
/* 489 */     dateParser.parseDate("1 Hour(s) ago");
/* 490 */     dateParser.parseDate("1.4h");
/* 491 */     dateParser.parseDate("3.5d");
/* 492 */     dateParser.parseDate("392w");
/* 493 */     dateParser.parseDate("01st Mar");
/* 494 */     dateParser.parseDate("19th Apr");
/* 495 */     dateParser.parseDate("03rd Apr");
/* 496 */     dateParser.parseDate("2nd Apr");
/* 497 */     dateParser.parseDate("3rd Nov");
/* 498 */     dateParser.parseDate("04-28");
/* 499 */     dateParser.parseDate("2007-07-14");
/* 500 */     dateParser.parseDate("2008.04.28");
/* 501 */     dateParser.parseDate("16/04/08");
/* 502 */     dateParser.parseDate("20-Dec-07");
/* 503 */     dateParser.parseDate("2009-01-12 at 03:36:38");
/* 504 */     dateParser.parseDate("2013-08-11T18:30:00.000Z");
/* 505 */     dateParser.parseDate("12å°æ¶å");
/* 506 */     dateParser.parseDate("12 å°æ¶å");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/DateParserRegex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */