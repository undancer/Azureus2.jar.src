/*     */ package org.gudy.azureus2.core3.logging.impl;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.ILogAlertListener;
/*     */ import org.gudy.azureus2.core3.logging.ILogEventListener;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
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
/*     */ public class LoggerImpl
/*     */ {
/*     */   private static final int MAXHISTORY = 256;
/*  47 */   private static final boolean bLogToStdOut = System.getProperty("azureus.log.stdout") != null;
/*     */   
/*  49 */   private boolean bEventLoggingEnabled = false;
/*     */   
/*  51 */   private PrintStream psOldOut = null;
/*     */   
/*  53 */   private PrintStream psOldErr = null;
/*     */   
/*     */   private PrintStream psOut;
/*     */   
/*     */   private PrintStream psErr;
/*     */   
/*  59 */   private final List logListeners = new ArrayList();
/*     */   
/*     */   private AEDiagnosticsLogger alertLogger;
/*     */   
/*  63 */   private final List alertListeners = new ArrayList();
/*     */   
/*  65 */   private final List alertHistory = new ArrayList();
/*     */   
/*  67 */   private boolean logToStdErrAllowed = true;
/*     */   
/*     */ 
/*     */ 
/*     */   public LoggerImpl()
/*     */   {
/*  73 */     doRedirects();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void init()
/*     */   {
/*  84 */     this.bEventLoggingEnabled = true;
/*     */     
/*     */ 
/*  87 */     final ConfigurationManager config = ConfigurationManager.getInstance();
/*     */     
/*  89 */     boolean overrideLog = System.getProperty("azureus.overridelog") != null;
/*  90 */     if (overrideLog) {
/*  91 */       this.bEventLoggingEnabled = true;
/*     */     } else {
/*  93 */       this.bEventLoggingEnabled = config.getBooleanParameter("Logger.Enabled");
/*     */       
/*  95 */       config.addParameterListener("Logger.Enabled", new ParameterListener() {
/*     */         public void parameterChanged(String parameterName) {
/*  97 */           LoggerImpl.this.bEventLoggingEnabled = config.getBooleanParameter("Logger.Enabled");
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void doRedirects()
/*     */   {
/*     */     try
/*     */     {
/* 108 */       if (System.out != this.psOut) {
/* 109 */         if (this.psOldOut == null) {
/* 110 */           this.psOldOut = System.out;
/*     */         }
/* 112 */         this.psOut = new PrintStream(new RedirectorStream(this.psOldOut, LogIDs.STDOUT, 0));
/*     */         
/*     */ 
/* 115 */         System.setOut(this.psOut);
/*     */       }
/*     */       
/* 118 */       if (System.err != this.psErr) {
/* 119 */         if (this.psOldErr == null) {
/* 120 */           this.psOldErr = System.err;
/*     */         }
/* 122 */         this.psErr = new PrintStream(new RedirectorStream(this.psOldErr, LogIDs.STDERR, 3));
/*     */         
/*     */ 
/* 125 */         System.setErr(this.psErr);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 129 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isEnabled() {
/* 134 */     return this.bEventLoggingEnabled;
/*     */   }
/*     */   
/*     */ 
/*     */   private class RedirectorStream
/*     */     extends OutputStream
/*     */   {
/*     */     protected final PrintStream ps;
/*     */     
/* 143 */     protected final StringBuffer buffer = new StringBuffer(1024);
/*     */     
/*     */     protected final LogIDs logID;
/*     */     protected final int logType;
/*     */     
/*     */     protected RedirectorStream(PrintStream _ps, LogIDs _logID, int _logType)
/*     */     {
/* 150 */       this.ps = _ps;
/* 151 */       this.logType = _logType;
/* 152 */       this.logID = _logID;
/*     */     }
/*     */     
/*     */     public void write(int data) {
/* 156 */       char c = (char)data;
/*     */       
/* 158 */       if (c == '\n') {
/* 159 */         if (!LoggerImpl.bLogToStdOut) {
/* 160 */           this.ps.println(this.buffer);
/*     */         }
/* 162 */         LoggerImpl.this.log(new LogEvent(this.logID, this.logType, this.buffer.toString()));
/* 163 */         this.buffer.setLength(0);
/* 164 */       } else if (c != '\r') {
/* 165 */         this.buffer.append(c);
/*     */       }
/*     */     }
/*     */     
/*     */     public void write(byte[] b, int off, int len) {
/* 170 */       for (int i = off; i < off + len; i++) {
/* 171 */         int d = b[i];
/* 172 */         if (d < 0)
/* 173 */           d += 256;
/* 174 */         write(d);
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
/*     */   public void log(LogEvent event)
/*     */   {
/* 204 */     if ((bLogToStdOut) && (this.psOldOut != null)) {
/* 205 */       this.psOldOut.println(event.text);
/*     */     }
/* 207 */     if (event.entryType == 3) {
/* 208 */       if (AEDiagnostics.isStartupComplete()) {
/*     */         try
/*     */         {
/* 211 */           Debug.outDiagLoggerOnly("[" + event.logID + "] " + event.text);
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/* 215 */       if ((this.logToStdErrAllowed) && (this.psOldErr != null) && (event.logID != LogIDs.STDERR)) {
/* 216 */         this.psOldErr.println("[" + event.logID + "] " + event.text);
/*     */       }
/*     */     }
/* 219 */     if (this.bEventLoggingEnabled) {
/* 220 */       for (int i = 0; i < this.logListeners.size(); i++) {
/*     */         try {
/* 222 */           Object listener = this.logListeners.get(i);
/* 223 */           if ((listener instanceof ILogEventListener))
/* 224 */             ((ILogEventListener)listener).log(event);
/*     */         } catch (Throwable e) {
/* 226 */           if ((this.logToStdErrAllowed) && (this.psOldErr != null)) {
/* 227 */             this.psOldErr.println("Error while logging: " + e.getMessage());
/* 228 */             e.printStackTrace(this.psOldErr);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 234 */     if ((event.err != null) && (event.entryType == 3)) {
/* 235 */       Debug.printStackTrace(event.err);
/*     */     }
/*     */   }
/*     */   
/*     */   public void logTextResource(LogEvent event) {
/* 240 */     event.text = MessageText.getString(event.text);
/* 241 */     log(event);
/*     */   }
/*     */   
/*     */   public void logTextResource(LogEvent event, String[] params) {
/* 245 */     event.text = MessageText.getString(event.text, params);
/* 246 */     log(event);
/*     */   }
/*     */   
/*     */   public void addListener(ILogEventListener aListener) {
/* 250 */     this.logListeners.add(aListener);
/*     */   }
/*     */   
/*     */   public void removeListener(ILogEventListener aListener) {
/* 254 */     this.logListeners.remove(aListener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void log(LogAlert alert)
/*     */   {
/* 261 */     String logText = "Alert:" + alert.entryType + ":" + alert.text;
/*     */     
/*     */ 
/* 264 */     LogEvent alertEvent = new LogEvent(LogIDs.ALERT, alert.entryType, logText);
/*     */     
/* 266 */     alertEvent.err = alert.err;
/* 267 */     Logger.log(alertEvent);
/*     */     
/* 269 */     synchronized (this) {
/* 270 */       if (this.alertLogger == null) {
/* 271 */         this.alertLogger = AEDiagnostics.getLogger("alerts");
/*     */       }
/*     */     }
/*     */     
/* 275 */     Throwable error = alert.getError();
/*     */     
/* 277 */     if (error != null)
/*     */     {
/* 279 */       logText = logText + " (" + Debug.getNestedExceptionMessageAndStack(error) + ")";
/*     */     }
/*     */     
/* 282 */     this.alertLogger.log(logText);
/*     */     
/* 284 */     this.alertHistory.add(alert);
/*     */     
/* 286 */     if (this.alertHistory.size() > 256) {
/* 287 */       this.alertHistory.remove(0);
/*     */     }
/* 289 */     for (int i = 0; i < this.alertListeners.size(); i++) {
/*     */       try {
/* 291 */         Object listener = this.alertListeners.get(i);
/* 292 */         if ((listener instanceof ILogAlertListener))
/* 293 */           ((ILogAlertListener)listener).alertRaised(alert);
/*     */       } catch (Throwable f) {
/* 295 */         if (this.psOldErr != null) {
/* 296 */           this.psOldErr.println("Error while alerting: " + f.getMessage());
/* 297 */           f.printStackTrace(this.psOldErr);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void logTextResource(LogAlert alert) {
/* 304 */     alert.text = MessageText.getString(alert.text);
/* 305 */     log(alert);
/*     */   }
/*     */   
/*     */   public void logTextResource(LogAlert alert, String[] params) {
/* 309 */     alert.text = MessageText.getString(alert.text, params);
/* 310 */     log(alert);
/*     */   }
/*     */   
/*     */   public void addListener(ILogAlertListener l) {
/* 314 */     this.alertListeners.add(l);
/*     */     
/* 316 */     for (int i = 0; i < this.alertHistory.size(); i++) {
/* 317 */       LogAlert alert = (LogAlert)this.alertHistory.get(i);
/* 318 */       l.alertRaised(alert);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeListener(ILogAlertListener l) {
/* 323 */     this.alertListeners.remove(l);
/*     */   }
/*     */   
/*     */   public PrintStream getOldStdErr() {
/* 327 */     return this.psOldErr;
/*     */   }
/*     */   
/*     */   public void allowLoggingToStdErr(boolean allowed) {
/* 331 */     this.logToStdErrAllowed = allowed;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/logging/impl/LoggerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */