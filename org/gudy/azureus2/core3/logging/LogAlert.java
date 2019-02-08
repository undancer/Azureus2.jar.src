/*     */ package org.gudy.azureus2.core3.logging;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.GeneralUtils;
/*     */ import java.util.ArrayList;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ public class LogAlert
/*     */   implements org.gudy.azureus2.plugins.logging.LogAlert
/*     */ {
/*     */   public static final int AT_INFORMATION = 0;
/*     */   public static final int AT_WARNING = 1;
/*     */   public static final int AT_ERROR = 3;
/*     */   public static final boolean REPEATABLE = true;
/*     */   public static final boolean UNREPEATABLE = false;
/*     */   public final int entryType;
/*  41 */   public Throwable err = null;
/*     */   
/*     */ 
/*     */   public final boolean repeatable;
/*     */   
/*     */ 
/*     */   public String text;
/*     */   
/*     */   public Object[] relatedTo;
/*     */   
/*  51 */   public int timeoutSecs = -1;
/*     */   
/*     */ 
/*     */   public String details;
/*     */   
/*     */ 
/*     */   public boolean forceNotify;
/*     */   
/*     */ 
/*     */ 
/*     */   public LogAlert(boolean repeatable, int type, String text)
/*     */   {
/*  63 */     this.entryType = type;
/*  64 */     this.text = text;
/*  65 */     this.repeatable = repeatable;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public LogAlert(boolean repeatable, int type, String text, int timeoutSecs)
/*     */   {
/*  75 */     this.entryType = type;
/*  76 */     this.text = text;
/*  77 */     this.repeatable = repeatable;
/*  78 */     this.timeoutSecs = timeoutSecs;
/*     */   }
/*     */   
/*     */   public LogAlert(Object[] relatedTo, boolean repeatable, int type, String text) {
/*  82 */     this(repeatable, type, text);
/*  83 */     this.relatedTo = relatedTo;
/*     */   }
/*     */   
/*     */   public LogAlert(Object relatedTo, boolean repeatable, int type, String text) {
/*  87 */     this(repeatable, type, text);
/*  88 */     this.relatedTo = new Object[] { relatedTo };
/*     */   }
/*     */   
/*     */   public LogAlert(boolean repeatable, String text, Throwable err) {
/*  92 */     this(repeatable, 3, text);
/*     */   }
/*     */   
/*     */   public LogAlert(boolean repeatable, int type, String text, Throwable err)
/*     */   {
/*  97 */     this(repeatable, type, text);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public LogAlert(Object relatedTo, boolean repeatable, String text, Throwable err)
/*     */   {
/* 109 */     this(repeatable, text, err);
/* 110 */     this.relatedTo = new Object[] { relatedTo };
/*     */   }
/*     */   
/*     */ 
/* 114 */   public int getGivenTimeoutSecs() { return this.timeoutSecs; }
/* 115 */   public String getText() { return this.text; }
/* 116 */   public Throwable getError() { return this.err; }
/*     */   
/* 118 */   public int getType() { switch (this.entryType) {
/*     */     case 0: 
/* 120 */       return 1;
/*     */     case 3: 
/* 122 */       return 3;
/*     */     case 1: 
/* 124 */       return 2;
/*     */     }
/* 126 */     return 1;
/*     */   }
/*     */   
/*     */   public Object[] getContext()
/*     */   {
/* 131 */     if (this.relatedTo == null) return null;
/* 132 */     ArrayList l = new ArrayList();
/* 133 */     for (int i = 0; i < this.relatedTo.length; i++) {
/* 134 */       l.add(PluginCoreUtils.convert(this.relatedTo[i], false));
/*     */     }
/* 136 */     return l.toArray();
/*     */   }
/*     */   
/*     */   public int getTimeoutSecs() {
/* 140 */     if (this.timeoutSecs != -1) return this.timeoutSecs;
/* 141 */     return COConfigurationManager.getIntParameter("Message Popup Autoclose in Seconds");
/*     */   }
/*     */   
/*     */   public String getPlainText() {
/* 145 */     return GeneralUtils.stripOutHyperlinks(this.text);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/logging/LogAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */