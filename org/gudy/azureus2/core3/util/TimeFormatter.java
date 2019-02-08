/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
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
/*     */ public class TimeFormatter
/*     */ {
/*  36 */   static final String[] TIME_SUFFIXES = { "s", "m", "h", "d", "y" };
/*     */   
/*  38 */   static final String[] TIME_SUFFIXES_2 = { "sec", "min", "hr", "day", "wk", "mo", "yr" };
/*     */   
/*  40 */   public static final String[] DATEFORMATS_DESC = { "EEEE, MMMM d, yyyy GG", "EEEE, MMMM d, yyyy", "EEE, MMMM d, yyyy", "MMMM d, ''yy", "EEE, MMM d, ''yy", "MMM d, yyyy", "MMM d, ''yy", "yyyy/MM/dd", "''yy/MM/dd", "MMM dd", "MM/dd" };
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
/*  54 */   private static final SimpleDateFormat http_date_format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
/*     */   
/*     */   private static final SimpleDateFormat cookie_date_format;
/*     */   
/*     */   static
/*     */   {
/*  60 */     http_date_format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */     
/*     */ 
/*  63 */     cookie_date_format = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  69 */     cookie_date_format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */     
/*     */ 
/*     */ 
/*  73 */     MessageText.addAndFireListener(new MessageText.MessageTextListener()
/*     */     {
/*     */       public void localeChanged(Locale old_locale, Locale new_locale) {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void loadMessages()
/*     */   {
/*  83 */     TIME_SUFFIXES[0] = MessageText.getString("ConfigView.section.stats.seconds.short");
/*  84 */     TIME_SUFFIXES[1] = MessageText.getString("ConfigView.section.stats.minutes.short");
/*  85 */     TIME_SUFFIXES[2] = MessageText.getString("ConfigView.section.stats.hours.short");
/*  86 */     TIME_SUFFIXES[3] = MessageText.getString("ConfigView.section.stats.days.short");
/*  87 */     TIME_SUFFIXES[4] = MessageText.getString("ConfigView.section.stats.years.short");
/*     */     
/*  89 */     TIME_SUFFIXES_2[0] = MessageText.getString("ConfigView.section.stats.seconds");
/*  90 */     TIME_SUFFIXES_2[1] = MessageText.getString("ConfigView.section.stats.minutes");
/*  91 */     TIME_SUFFIXES_2[2] = MessageText.getString("ConfigView.section.stats.hours");
/*  92 */     TIME_SUFFIXES_2[3] = MessageText.getString("ConfigView.section.stats.days");
/*  93 */     TIME_SUFFIXES_2[4] = MessageText.getString("ConfigView.section.stats.weeks.medium");
/*  94 */     TIME_SUFFIXES_2[5] = MessageText.getString("ConfigView.section.stats.months.medium");
/*  95 */     TIME_SUFFIXES_2[6] = MessageText.getString("ConfigView.section.stats.years.medium");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String format(long time_secs)
/*     */   {
/* 107 */     if ((time_secs == 31536000L) || (time_secs >= 1827387392L)) {
/* 108 */       return "∞";
/*     */     }
/* 110 */     if (time_secs < 0L) {
/* 111 */       return "";
/*     */     }
/*     */     
/* 114 */     int[] vals = { (int)time_secs % 60, (int)(time_secs / 60L) % 60, (int)(time_secs / 3600L) % 24, (int)(time_secs / 86400L) % 365, (int)(time_secs / 31536000L) };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 122 */     int end = vals.length - 1;
/* 123 */     while ((vals[end] == 0) && (end > 0)) {
/* 124 */       end--;
/*     */     }
/*     */     
/* 127 */     String result = vals[end] + TIME_SUFFIXES[end];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 136 */     end--;
/*     */     
/* 138 */     if (end >= 0) {
/* 139 */       result = result + " " + twoDigits(vals[end]) + TIME_SUFFIXES[end];
/*     */     }
/* 141 */     return result;
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
/*     */   public static String format2(long time_secs, boolean do_seconds)
/*     */   {
/* 155 */     if ((time_secs == 31536000L) || (time_secs >= 1827387392L)) {
/* 156 */       return "∞";
/*     */     }
/* 158 */     if (time_secs < 0L) {
/* 159 */       return "";
/*     */     }
/*     */     
/* 162 */     int[] vals = { (int)time_secs % 60, (int)(time_secs / 60L) % 60, (int)(time_secs / 3600L) % 24, (int)(time_secs / 86400L) % 365, (int)(time_secs / 31536000L) };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 170 */     int start = vals.length - 1;
/* 171 */     while ((vals[start] == 0) && (start > 0)) {
/* 172 */       start--;
/*     */     }
/*     */     
/* 175 */     int end = do_seconds ? 0 : 1;
/*     */     
/* 177 */     if ((start == 0) && (!do_seconds)) {
/* 178 */       start = 1;
/*     */     }
/*     */     
/* 181 */     String result = "";
/*     */     
/* 183 */     for (int i = start; i >= end; i--)
/*     */     {
/* 185 */       result = result + (i == start ? Integer.valueOf(vals[i]) : new StringBuilder().append(" ").append(twoDigits(vals[i])).toString()) + TIME_SUFFIXES[i];
/*     */     }
/*     */     
/* 188 */     return result;
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
/*     */   public static String format3(long time_secs)
/*     */   {
/* 201 */     if ((time_secs == 31536000L) || (time_secs >= 1827387392L)) {
/* 202 */       return "∞";
/*     */     }
/* 204 */     if (time_secs < 0L)
/*     */     {
/* 206 */       return "";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 212 */     int[] vals = { (int)time_secs % 60, (int)(time_secs / 60L) % 60, (int)(time_secs / 3600L) % 24, (int)(time_secs / 86400L) % 7, (int)(time_secs / 604800L) % 4, (int)(time_secs / 2592000L) % 12, (int)(time_secs / 31536000L) };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 222 */     int start = vals.length - 1;
/* 223 */     while ((vals[start] == 0) && (start > 0)) {
/* 224 */       start--;
/*     */     }
/*     */     
/* 227 */     String result = vals[start] + " " + TIME_SUFFIXES_2[start];
/*     */     
/* 229 */     return result;
/*     */   }
/*     */   
/*     */   public static String format100ths(long time_millis)
/*     */   {
/* 234 */     long time_secs = time_millis / 1000L;
/*     */     
/* 236 */     int hundredths = (int)(time_millis - time_secs * 1000L) / 10;
/*     */     
/* 238 */     if ((time_millis == 0L) || (time_secs >= 60L))
/*     */     {
/* 240 */       return format(time_secs);
/*     */     }
/*     */     
/* 243 */     return time_secs + "." + twoDigits(hundredths) + TIME_SUFFIXES[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String formatColonMillis(long time)
/*     */   {
/* 252 */     if (time > 0L) {
/* 253 */       if (time < 1000L) {
/* 254 */         time = 1L;
/*     */       } else {
/* 256 */         time /= 1000L;
/*     */       }
/*     */     }
/*     */     
/* 260 */     String str = formatColon(time);
/*     */     
/* 262 */     if (str.startsWith("00:"))
/*     */     {
/* 264 */       str = str.substring(3);
/*     */     }
/*     */     
/* 267 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String formatColon(long time)
/*     */   {
/* 279 */     if ((time == 31536000L) || (time >= 1827387392L)) return "∞";
/* 280 */     if (time < 0L) { return "";
/*     */     }
/* 282 */     int secs = (int)time % 60;
/* 283 */     int mins = (int)(time / 60L) % 60;
/* 284 */     int hours = (int)(time / 3600L) % 24;
/* 285 */     int days = (int)(time / 86400L) % 365;
/* 286 */     int years = (int)(time / 31536000L);
/*     */     
/* 288 */     String result = "";
/* 289 */     if (years > 0) result = result + years + "y ";
/* 290 */     if ((years > 0) || (days > 0)) result = result + days + "d ";
/* 291 */     result = result + twoDigits(hours) + ":" + twoDigits(mins) + ":" + twoDigits(secs);
/*     */     
/* 293 */     return result;
/*     */   }
/*     */   
/*     */   private static String twoDigits(int i) {
/* 297 */     return i < 10 ? "0" + i : String.valueOf(i);
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
/*     */   public static int parseColon(String str)
/*     */   {
/* 310 */     int[] multipliers = { 1, 60, 3600, 86400, 31536000 };
/*     */     
/* 312 */     String[] bits = str.split(":");
/*     */     
/* 314 */     int result = 0;
/*     */     
/* 316 */     for (int i = 0; i < bits.length; i++)
/*     */     {
/* 318 */       String bit = bits[(bits.length - (i + 1))].trim();
/*     */       
/* 320 */       if (bit.length() > 0)
/*     */       {
/* 322 */         result += multipliers[i] * Integer.parseInt(bit);
/*     */       }
/*     */     }
/*     */     
/* 326 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String formatNanoAsMilli(long nanos)
/*     */   {
/* 333 */     long truncator = 60000000000L;
/*     */     
/* 335 */     nanos -= nanos / 60000000000L * 60000000000L;
/*     */     
/* 337 */     return String.valueOf(nanos / 1000000.0D) + " ms";
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
/*     */   public static String milliStamp()
/*     */   {
/* 380 */     long nanos = SystemTime.getHighPrecisionCounter();
/*     */     
/* 382 */     long truncator = 60000000000L;
/*     */     
/* 384 */     nanos -= nanos / 60000000000L * 60000000000L;
/*     */     
/* 386 */     String str = String.valueOf(nanos / 1000000L);
/*     */     
/* 388 */     while (str.length() < 5)
/*     */     {
/* 390 */       str = "0" + str;
/*     */     }
/*     */     
/* 393 */     return str + ": ";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void milliTrace(String str)
/*     */   {
/* 400 */     System.out.println(milliStamp() + str);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static String getHTTPDate(long millis)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 315	org/gudy/azureus2/core3/util/TimeFormatter:http_date_format	Ljava/text/SimpleDateFormat;
/*     */     //   3: dup
/*     */     //   4: astore_2
/*     */     //   5: monitorenter
/*     */     //   6: getstatic 315	org/gudy/azureus2/core3/util/TimeFormatter:http_date_format	Ljava/text/SimpleDateFormat;
/*     */     //   9: new 232	java/util/Date
/*     */     //   12: dup
/*     */     //   13: lload_0
/*     */     //   14: invokespecial 339	java/util/Date:<init>	(J)V
/*     */     //   17: invokevirtual 335	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
/*     */     //   20: aload_2
/*     */     //   21: monitorexit
/*     */     //   22: areturn
/*     */     //   23: astore_3
/*     */     //   24: aload_2
/*     */     //   25: monitorexit
/*     */     //   26: aload_3
/*     */     //   27: athrow
/*     */     // Line number table:
/*     */     //   Java source line #344	-> byte code offset #0
/*     */     //   Java source line #346	-> byte code offset #6
/*     */     //   Java source line #347	-> byte code offset #23
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	28	0	millis	long
/*     */     //   4	21	2	Ljava/lang/Object;	Object
/*     */     //   23	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   6	22	23	finally
/*     */     //   23	26	23	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static long parseHTTPDate(String date)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 315	org/gudy/azureus2/core3/util/TimeFormatter:http_date_format	Ljava/text/SimpleDateFormat;
/*     */     //   3: dup
/*     */     //   4: astore_1
/*     */     //   5: monitorenter
/*     */     //   6: getstatic 315	org/gudy/azureus2/core3/util/TimeFormatter:http_date_format	Ljava/text/SimpleDateFormat;
/*     */     //   9: aload_0
/*     */     //   10: invokevirtual 336	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
/*     */     //   13: invokevirtual 338	java/util/Date:getTime	()J
/*     */     //   16: aload_1
/*     */     //   17: monitorexit
/*     */     //   18: lreturn
/*     */     //   19: astore_2
/*     */     //   20: aload_1
/*     */     //   21: monitorexit
/*     */     //   22: aload_2
/*     */     //   23: athrow
/*     */     //   24: astore_1
/*     */     //   25: new 228	java/lang/StringBuilder
/*     */     //   28: dup
/*     */     //   29: invokespecial 328	java/lang/StringBuilder:<init>	()V
/*     */     //   32: ldc 31
/*     */     //   34: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   37: aload_0
/*     */     //   38: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   41: ldc 6
/*     */     //   43: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   46: invokevirtual 329	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   49: invokestatic 343	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*     */     //   52: lconst_0
/*     */     //   53: lreturn
/*     */     // Line number table:
/*     */     //   Java source line #355	-> byte code offset #0
/*     */     //   Java source line #357	-> byte code offset #6
/*     */     //   Java source line #358	-> byte code offset #19
/*     */     //   Java source line #359	-> byte code offset #24
/*     */     //   Java source line #361	-> byte code offset #25
/*     */     //   Java source line #363	-> byte code offset #52
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	54	0	date	String
/*     */     //   4	17	1	Ljava/lang/Object;	Object
/*     */     //   24	2	1	e	Throwable
/*     */     //   19	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   6	18	19	finally
/*     */     //   19	22	19	finally
/*     */     //   0	18	24	java/lang/Throwable
/*     */     //   19	24	24	java/lang/Throwable
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static String getCookieDate(long millis)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: getstatic 314	org/gudy/azureus2/core3/util/TimeFormatter:cookie_date_format	Ljava/text/SimpleDateFormat;
/*     */     //   3: dup
/*     */     //   4: astore_2
/*     */     //   5: monitorenter
/*     */     //   6: getstatic 314	org/gudy/azureus2/core3/util/TimeFormatter:cookie_date_format	Ljava/text/SimpleDateFormat;
/*     */     //   9: new 232	java/util/Date
/*     */     //   12: dup
/*     */     //   13: lload_0
/*     */     //   14: invokespecial 339	java/util/Date:<init>	(J)V
/*     */     //   17: invokevirtual 335	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
/*     */     //   20: aload_2
/*     */     //   21: monitorexit
/*     */     //   22: areturn
/*     */     //   23: astore_3
/*     */     //   24: aload_2
/*     */     //   25: monitorexit
/*     */     //   26: aload_3
/*     */     //   27: athrow
/*     */     // Line number table:
/*     */     //   Java source line #371	-> byte code offset #0
/*     */     //   Java source line #373	-> byte code offset #6
/*     */     //   Java source line #374	-> byte code offset #23
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	28	0	millis	long
/*     */     //   4	21	2	Ljava/lang/Object;	Object
/*     */     //   23	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   6	22	23	finally
/*     */     //   23	26	23	finally
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/TimeFormatter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */