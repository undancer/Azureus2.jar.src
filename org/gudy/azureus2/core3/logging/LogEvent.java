/*     */ package org.gudy.azureus2.core3.logging;
/*     */ 
/*     */ import java.util.Date;
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
/*     */ public class LogEvent
/*     */ {
/*     */   public static final int LT_INFORMATION = 0;
/*     */   public static final int LT_WARNING = 1;
/*     */   public static final int LT_ERROR = 3;
/*  38 */   public Date timeStamp = new Date();
/*     */   
/*     */ 
/*     */   public final Object[] relatedTo;
/*     */   
/*     */ 
/*     */   public final LogIDs logID;
/*     */   
/*     */ 
/*     */   public final int entryType;
/*     */   
/*     */ 
/*     */   public String text;
/*     */   
/*     */ 
/*  53 */   public Throwable err = null;
/*     */   
/*     */   public LogEvent(Object[] relatedTo, LogIDs logID, int entryType, String text) {
/*  56 */     this.logID = logID;
/*  57 */     this.entryType = entryType;
/*  58 */     this.text = text;
/*  59 */     this.relatedTo = relatedTo;
/*     */   }
/*     */   
/*     */   public LogEvent(Object relatedTo, LogIDs logID, int entryType, String text) {
/*  63 */     this(new Object[] { relatedTo }, logID, entryType, text);
/*     */   }
/*     */   
/*     */   public LogEvent(LogIDs logID, int entryType, String text)
/*     */   {
/*  68 */     this(null, logID, entryType, text);
/*     */   }
/*     */   
/*     */   public LogEvent(Object[] relatedTo, LogIDs logID, String text) {
/*  72 */     this(relatedTo, logID, 0, text);
/*     */   }
/*     */   
/*     */   public LogEvent(Object relatedTo, LogIDs logID, String text) {
/*  76 */     this(new Object[] { relatedTo }, logID, 0, text);
/*     */   }
/*     */   
/*     */   public LogEvent(LogIDs logID, String text) {
/*  80 */     this(null, logID, 0, text);
/*     */   }
/*     */   
/*     */ 
/*     */   public LogEvent(Object[] relatedTo, LogIDs logID, int entryType, String text, Throwable e)
/*     */   {
/*  86 */     this(relatedTo, logID, entryType, text);
/*  87 */     this.err = e;
/*     */   }
/*     */   
/*  90 */   public LogEvent(Object[] relatedTo, LogIDs logID, String text, Throwable e) { this(relatedTo, logID, 3, text, e); }
/*     */   
/*     */   public LogEvent(Object relatedTo, LogIDs logID, String text, Throwable e)
/*     */   {
/*  94 */     this(new Object[] { relatedTo }, logID, text, e);
/*     */   }
/*     */   
/*     */   public LogEvent(LogIDs logID, int entryType, String text, Throwable e) {
/*  98 */     this(null, logID, entryType, text, e);
/*     */   }
/*     */   
/*     */   public LogEvent(LogIDs logID, String text, Throwable e) {
/* 102 */     this(null, logID, text, e);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/logging/LogEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */