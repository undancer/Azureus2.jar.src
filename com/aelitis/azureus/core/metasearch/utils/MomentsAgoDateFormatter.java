/*     */ package com.aelitis.azureus.core.metasearch.utils;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.SimpleTimeZone;
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
/*     */ public class MomentsAgoDateFormatter
/*     */ {
/*  38 */   private static final Integer ID_YEAR = new Integer(1);
/*  39 */   private static final Integer ID_MONTH = new Integer(2);
/*  40 */   private static final Integer ID_WEEK_OF_YEAR = new Integer(3);
/*  41 */   private static final Integer ID_DAY = new Integer(5);
/*  42 */   private static final Integer ID_HOUR_OF_DAY = new Integer(11);
/*  43 */   private static final Integer ID_MINUTE = new Integer(12);
/*  44 */   private static final Integer ID_SECOND = new Integer(13);
/*     */   
/*     */ 
/*  47 */   private static final Long MS_IN_YEAR = new Long(31536000000L);
/*  48 */   private static final Long MS_IN_MONTH = new Long(2678400000L);
/*  49 */   private static final Long MS_IN_WEEK = new Long(604800000L);
/*  50 */   private static final Long MS_IN_DAY = new Long(86400000L);
/*  51 */   private static final Long MS_IN_HOUR = new Long(3600000L);
/*  52 */   private static final Long MS_IN_MINUTE = new Long(60000L);
/*  53 */   private static final Long MS_IN_SECOND = new Long(1000L);
/*     */   
/*     */   private static final String AGO = " ago";
/*     */   
/*     */   private static final String PLURAL = "s";
/*     */   
/*  59 */   private static final Map CONVERSION_MAP = new HashMap();
/*     */   private static final Map UNIT_MAP;
/*     */   
/*     */   static {
/*  63 */     CONVERSION_MAP.put(ID_YEAR, MS_IN_YEAR);
/*  64 */     CONVERSION_MAP.put(ID_MONTH, MS_IN_MONTH);
/*  65 */     CONVERSION_MAP.put(ID_WEEK_OF_YEAR, MS_IN_WEEK);
/*  66 */     CONVERSION_MAP.put(ID_DAY, MS_IN_DAY);
/*  67 */     CONVERSION_MAP.put(ID_HOUR_OF_DAY, MS_IN_HOUR);
/*     */     
/*     */ 
/*  70 */     UNIT_MAP = new HashMap();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  75 */     UNIT_MAP.put(ID_YEAR, " yr");
/*  76 */     UNIT_MAP.put(ID_MONTH, " mo");
/*  77 */     UNIT_MAP.put(ID_WEEK_OF_YEAR, " wk");
/*  78 */     UNIT_MAP.put(ID_DAY, " day");
/*  79 */     UNIT_MAP.put(ID_HOUR_OF_DAY, " hr");
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
/*     */   public static String getMomentsAgoString(Date pastDate, DateFormat format)
/*     */   {
/*  93 */     String timeAgo = getMomentsAgoString(pastDate);
/*  94 */     format.setTimeZone(new SimpleTimeZone(0, "GMT"));
/*  95 */     if (timeAgo.length() > 0) timeAgo = timeAgo.concat(" on ");
/*  96 */     return timeAgo.concat(format.format(pastDate));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getMomentsAgoString(Date pastDate)
/*     */   {
/* 107 */     Calendar then = Calendar.getInstance();
/* 108 */     then.setTime(pastDate);
/* 109 */     Calendar now = Calendar.getInstance();
/* 110 */     String result = null;
/* 111 */     result = handleUnit(then, now, ID_YEAR);
/* 112 */     if (result == null) {
/* 113 */       result = handleUnit(then, now, ID_MONTH);
/* 114 */       if (result == null) {
/* 115 */         result = handleUnit(then, now, ID_WEEK_OF_YEAR);
/* 116 */         if (result == null) {
/* 117 */           result = handleUnit(then, now, ID_DAY);
/* 118 */           if (result == null) {
/* 119 */             result = handleUnit(then, now, ID_HOUR_OF_DAY);
/* 120 */             if (result == null) {
/* 121 */               return "< 1 h";
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 127 */     return result;
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
/*     */   private static String handleUnit(Calendar then, Calendar now, Integer field)
/*     */   {
/* 142 */     String result = null;
/* 143 */     long diff = now.getTimeInMillis() - then.getTimeInMillis();
/* 144 */     long comparison = ((Long)CONVERSION_MAP.get(field)).longValue();
/* 145 */     if (diff > comparison) {
/* 146 */       long timeAgo = diff / comparison;
/* 147 */       result = String.valueOf(timeAgo).concat((String)UNIT_MAP.get(field));
/*     */     }
/*     */     
/*     */ 
/* 151 */     return result;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/utils/MomentsAgoDateFormatter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */