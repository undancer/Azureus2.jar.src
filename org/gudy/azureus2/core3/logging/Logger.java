/*     */ package org.gudy.azureus2.core3.logging;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import org.gudy.azureus2.core3.logging.impl.FileLogging;
/*     */ import org.gudy.azureus2.core3.logging.impl.LoggerImpl;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Logger
/*     */ {
/*  36 */   private static final LogIDs LOGID = LogIDs.LOGGER;
/*     */   
/*  38 */   private static LoggerImpl loggerImpl = null;
/*     */   
/*  40 */   private static final FileLogging fileLogging = new FileLogging();
/*     */   
/*     */   static {
/*     */     try {
/*  44 */       loggerImpl = new LoggerImpl();
/*  45 */       loggerImpl.init();
/*     */       
/*  47 */       fileLogging.initialize();
/*     */       
/*  49 */       if (loggerImpl.isEnabled()) {
/*  50 */         log(new LogEvent(LOGID, "**** Logging starts: " + Constants.APP_NAME + " " + "5.7.6.0" + " ****"));
/*     */         
/*     */ 
/*  53 */         log(new LogEvent(LOGID, "java.home=" + System.getProperty("java.home")));
/*     */         
/*  55 */         log(new LogEvent(LOGID, "java.version=" + Constants.JAVA_VERSION));
/*     */         
/*     */ 
/*  58 */         log(new LogEvent(LOGID, "os=" + System.getProperty("os.arch") + "/" + System.getProperty("os.name") + "/" + System.getProperty("os.version")));
/*     */         
/*     */ 
/*     */ 
/*  62 */         log(new LogEvent(LOGID, "user.dir=" + System.getProperty("user.dir")));
/*     */         
/*  64 */         log(new LogEvent(LOGID, "user.home=" + System.getProperty("user.home")));
/*     */       }
/*     */     } catch (Throwable t) {
/*  67 */       t.printStackTrace();
/*  68 */       Debug.out("Error initializing Logger", t);
/*     */     }
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
/*     */   public static boolean isEnabled()
/*     */   {
/*  82 */     return loggerImpl.isEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void log(LogEvent event)
/*     */   {
/*  92 */     loggerImpl.log(event);
/*     */   }
/*     */   
/*     */   public static void log(LogAlert alert) {
/*  96 */     loggerImpl.log(alert);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void logTextResource(LogEvent event)
/*     */   {
/* 107 */     loggerImpl.logTextResource(event);
/*     */   }
/*     */   
/*     */   public static void logTextResource(LogEvent event, String[] params)
/*     */   {
/* 112 */     loggerImpl.logTextResource(event, params);
/*     */   }
/*     */   
/*     */   public static void logTextResource(LogAlert alert)
/*     */   {
/* 117 */     loggerImpl.logTextResource(alert);
/*     */   }
/*     */   
/*     */   public static void logTextResource(LogAlert alert, String[] params) {
/* 121 */     loggerImpl.logTextResource(alert, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void doRedirects()
/*     */   {
/* 128 */     loggerImpl.doRedirects();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addListener(ILogEventListener aListener)
/*     */   {
/* 138 */     loggerImpl.addListener(aListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addListener(ILogAlertListener aListener)
/*     */   {
/* 148 */     loggerImpl.addListener(aListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void removeListener(ILogEventListener aListener)
/*     */   {
/* 158 */     loggerImpl.removeListener(aListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void removeListener(ILogAlertListener aListener)
/*     */   {
/* 168 */     loggerImpl.removeListener(aListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PrintStream getOldStdErr()
/*     */   {
/* 178 */     return loggerImpl.getOldStdErr();
/*     */   }
/*     */   
/*     */   public static FileLogging getFileLoggingInstance() {
/* 182 */     return fileLogging;
/*     */   }
/*     */   
/*     */   public static void allowLoggingToStdErr(boolean allowed) {
/* 186 */     loggerImpl.allowLoggingToStdErr(allowed);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/logging/Logger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */