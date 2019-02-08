/*     */ package org.gudy.azureus2.core3.logging.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.ILogEventListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.LogRelation;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class FileLogging
/*     */   implements ILogEventListener
/*     */ {
/*     */   public static final String LOG_FILE_NAME = "az.log";
/*     */   public static final String BAK_FILE_NAME = "az.log.bak";
/*  46 */   public static final LogIDs[] configurableLOGIDs = { LogIDs.STDOUT, LogIDs.ALERT, LogIDs.CORE, LogIDs.DISK, LogIDs.GUI, LogIDs.NET, LogIDs.NWMAN, LogIDs.PEER, LogIDs.PLUGIN, LogIDs.TRACKER, LogIDs.CACHE, LogIDs.PIECES };
/*     */   
/*     */ 
/*     */   private static final String CFG_ENABLELOGTOFILE = "Logging Enable";
/*     */   
/*     */ 
/*  52 */   private boolean bLogToFile = false;
/*  53 */   private boolean bLogToFileErrorPrinted = false;
/*     */   
/*  55 */   private String sLogDir = "";
/*     */   
/*  57 */   private int iLogFileMaxMB = 1;
/*     */   
/*     */ 
/*     */ 
/*  61 */   private final ArrayList[] ignoredComponents = new ArrayList[3];
/*     */   
/*  63 */   private final ArrayList listeners = new ArrayList();
/*     */   private SimpleDateFormat format;
/*     */   
/*     */   public void initialize() {
/*  67 */     ConfigurationManager config = ConfigurationManager.getInstance();
/*  68 */     boolean overrideLog = System.getProperty("azureus.overridelog") != null;
/*     */     
/*  70 */     for (int i = 0; i < this.ignoredComponents.length; i++) {
/*  71 */       this.ignoredComponents[i] = new ArrayList();
/*     */     }
/*     */     
/*  74 */     if (!overrideLog) {
/*  75 */       config.addListener(new COConfigurationListener() {
/*     */         public void configurationSaved() {
/*  77 */           FileLogging.this.checkLoggingConfig();
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*  82 */     checkLoggingConfig();
/*  83 */     config.addParameterListener("Logging Enable", new ParameterListener() {
/*     */       public void parameterChanged(String parameterName) {
/*  85 */         FileLogging.this.reloadLogToFileParam();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void reloadLogToFileParam()
/*     */   {
/*  94 */     ConfigurationManager config = ConfigurationManager.getInstance();
/*  95 */     boolean bNewLogToFile = (System.getProperty("azureus.overridelog") != null) || (config.getBooleanParameter("Logging Enable"));
/*  96 */     if (bNewLogToFile != this.bLogToFile) {
/*  97 */       this.bLogToFile = bNewLogToFile;
/*  98 */       if (this.bLogToFile) {
/*  99 */         Logger.addListener(this);
/*     */       } else {
/* 101 */         Logger.removeListener(this);
/*     */         
/* 103 */         synchronized (Logger.class)
/*     */         {
/*     */ 
/*     */ 
/* 107 */           checkAndSwapLog();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkLoggingConfig()
/*     */   {
/*     */     try {
/* 116 */       ConfigurationManager config = ConfigurationManager.getInstance();
/*     */       
/*     */ 
/*     */ 
/* 120 */       boolean overrideLog = System.getProperty("azureus.overridelog") != null;
/* 121 */       String timeStampFormat; if (overrideLog)
/*     */       {
/*     */ 
/*     */ 
/* 125 */         this.sLogDir = System.getProperty("azureus.overridelogdir", ".");
/* 126 */         this.iLogFileMaxMB = 2;
/* 127 */         String timeStampFormat = "HH:mm:ss.SSS ";
/*     */         
/* 129 */         for (int i = 0; i < this.ignoredComponents.length; i++) {
/* 130 */           this.ignoredComponents[i].clear();
/*     */         }
/*     */         
/* 133 */         reloadLogToFileParam();
/*     */       } else {
/* 135 */         reloadLogToFileParam();
/*     */         
/* 137 */         this.sLogDir = config.getStringParameter("Logging Dir", "");
/*     */         
/* 139 */         this.iLogFileMaxMB = config.getIntParameter("Logging Max Size");
/*     */         
/* 141 */         timeStampFormat = config.getStringParameter("Logging Timestamp") + " ";
/*     */         
/* 143 */         for (int i = 0; i < this.ignoredComponents.length; i++) {
/* 144 */           this.ignoredComponents[i].clear();
/* 145 */           int logType = indexToLogType(i);
/* 146 */           for (int j = 0; j < configurableLOGIDs.length; j++) {
/* 147 */             if (!config.getBooleanParameter("bLog." + logType + "." + configurableLOGIDs[j], true))
/*     */             {
/* 149 */               this.ignoredComponents[i].add(configurableLOGIDs[j]);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 154 */       synchronized (Logger.class)
/*     */       {
/*     */ 
/* 157 */         this.format = new SimpleDateFormat(timeStampFormat);
/* 158 */         checkAndSwapLog();
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 163 */       Debug.printStackTrace(t);
/*     */     }
/*     */   }
/*     */   
/*     */   private void logToFile(String str) {
/* 168 */     if (!this.bLogToFile) {
/* 169 */       return;
/*     */     }
/* 171 */     String dateStr = this.format.format(new Date());
/*     */     
/* 173 */     synchronized (Logger.class)
/*     */     {
/*     */ 
/* 176 */       if (this.logFilePrinter != null)
/*     */       {
/* 178 */         this.logFilePrinter.print(dateStr);
/* 179 */         this.logFilePrinter.print(str);
/* 180 */         this.logFilePrinter.flush();
/*     */       }
/*     */       
/* 183 */       checkAndSwapLog();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkAndSwapLog()
/*     */   {
/* 192 */     if (!this.bLogToFile)
/*     */     {
/* 194 */       if (this.logFilePrinter != null)
/*     */       {
/* 196 */         this.logFilePrinter.close();
/* 197 */         this.logFilePrinter = null;
/*     */       }
/* 199 */       return;
/*     */     }
/*     */     
/*     */ 
/* 203 */     long lMaxBytes = this.iLogFileMaxMB * 1024L * 1024L / 2L;
/* 204 */     File logFile = new File(this.sLogDir + File.separator + "az.log");
/*     */     
/* 206 */     if ((logFile.length() > lMaxBytes) && (this.logFilePrinter != null))
/*     */     {
/* 208 */       File back_name = new File(this.sLogDir + File.separator + "az.log.bak");
/* 209 */       this.logFilePrinter.close();
/* 210 */       this.logFilePrinter = null;
/*     */       
/* 212 */       if ((!back_name.exists()) || (back_name.delete())) {
/* 213 */         if (!logFile.renameTo(back_name))
/*     */         {
/* 215 */           logFile.delete();
/*     */         }
/*     */       }
/*     */       else {
/* 219 */         logFile.delete();
/*     */       }
/*     */     }
/*     */     
/* 223 */     if (this.logFilePrinter == null)
/*     */     {
/*     */       try
/*     */       {
/* 227 */         this.logFilePrinter = new PrintWriter(new FileWriter(logFile, true));
/*     */       }
/*     */       catch (IOException e) {
/* 230 */         if (!this.bLogToFileErrorPrinted)
/*     */         {
/*     */ 
/* 233 */           this.bLogToFileErrorPrinted = true;
/* 234 */           Debug.out("Unable to write to log file: " + logFile);
/* 235 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int logTypeToIndex(int entryType)
/*     */   {
/* 249 */     switch (entryType) {
/*     */     case 0: 
/* 251 */       return 0;
/*     */     case 1: 
/* 253 */       return 1;
/*     */     case 3: 
/* 255 */       return 2;
/*     */     }
/* 257 */     return 0;
/*     */   }
/*     */   
/*     */   private int indexToLogType(int index) {
/* 261 */     switch (index) {
/*     */     case 0: 
/* 263 */       return 0;
/*     */     case 1: 
/* 265 */       return 1;
/*     */     case 2: 
/* 267 */       return 3;
/*     */     }
/* 269 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private PrintWriter logFilePrinter;
/*     */   
/*     */ 
/*     */   private static final int DEFPADDING = 100;
/*     */   
/* 279 */   private int lastWidth = 100;
/*     */   
/* 281 */   public void log(LogEvent event) { if (this.ignoredComponents[logTypeToIndex(event.entryType)].contains(event.logID))
/*     */     {
/* 283 */       return;
/*     */     }
/* 285 */     StringBuffer text = new StringBuffer(event.text.length());
/*     */     
/* 287 */     text.append(event.entryType).append(" ");
/*     */     
/* 289 */     padAndAppend(text, event.logID.toString(), 8, 1);
/*     */     
/*     */ 
/*     */ 
/* 293 */     if (event.relatedTo != null) {
/* 294 */       this.lastWidth = padAndAppend(text, event.text, this.lastWidth, 1);
/* 295 */       if (this.lastWidth > 200) {
/* 296 */         this.lastWidth = 200;
/*     */       }
/* 298 */       for (int i = 0; i < event.relatedTo.length; i++) {
/* 299 */         Object obj = event.relatedTo[i];
/*     */         
/* 301 */         if (obj != null)
/*     */         {
/*     */ 
/* 304 */           if (i > 0) {
/* 305 */             text.append("; ");
/*     */           }
/* 307 */           if ((obj instanceof LogRelation)) {
/* 308 */             text.append(((LogRelation)obj).getRelationText());
/*     */           } else {
/* 310 */             text.append("RelatedTo[").append(obj.toString()).append("]");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 316 */       text.append(event.text);
/*     */       
/* 318 */       this.lastWidth = 100;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 323 */     if (!event.text.endsWith("\n")) {
/* 324 */       text.append("\r\n");
/*     */     }
/* 326 */     boolean okToLog = true;
/* 327 */     for (Iterator iter = this.listeners.iterator(); (iter.hasNext()) && (okToLog);) {
/* 328 */       FileLoggingAdapter listener = (FileLoggingAdapter)iter.next();
/* 329 */       okToLog = listener.logToFile(event, text);
/*     */     }
/*     */     
/* 332 */     logToFile(text.toString());
/*     */   }
/*     */   
/*     */   private int padAndAppend(StringBuffer appendTo, String s, int width, int growBy) {
/* 336 */     if (s == null)
/* 337 */       s = "null";
/* 338 */     appendTo.append(s);
/*     */     
/* 340 */     int sLen = s.length();
/* 341 */     int len = width - sLen;
/* 342 */     while (len <= 0) {
/* 343 */       len += growBy;
/*     */     }
/* 345 */     char[] padding = new char[len];
/* 346 */     if (len > 5) {
/* 347 */       for (int i = 0; i < len; i += 2)
/* 348 */         padding[i] = ' ';
/* 349 */       for (int i = 1; i < len; i += 2)
/* 350 */         padding[i] = '.';
/*     */     } else {
/* 352 */       for (int i = 0; i < len; i++) {
/* 353 */         padding[i] = ' ';
/*     */       }
/*     */     }
/* 356 */     appendTo.append(padding);
/*     */     
/* 358 */     return len + sLen;
/*     */   }
/*     */   
/*     */   public void addListener(FileLoggingAdapter listener) {
/* 362 */     if (!this.listeners.contains(listener))
/* 363 */       this.listeners.add(listener);
/*     */   }
/*     */   
/*     */   public void removeListener(FileLoggingAdapter listener) {
/* 367 */     this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */   public List getListeners() {
/* 371 */     return this.listeners;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/logging/impl/FileLogging.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */