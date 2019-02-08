/*     */ package org.gudy.azureus2.pluginsimpl.local.logging;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.LogRelation;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
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
/*     */ public class LoggerChannelImpl
/*     */   implements LoggerChannel
/*     */ {
/*  46 */   private static final LogIDs LOGID = LogIDs.PLUGIN;
/*     */   private final org.gudy.azureus2.plugins.logging.Logger logger;
/*     */   private final String name;
/*     */   private final boolean timestamp;
/*     */   final boolean no_output;
/*  51 */   final List listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */   private AEDiagnosticsLogger diagnostic_logger;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected LoggerChannelImpl(org.gudy.azureus2.plugins.logging.Logger _logger, String _name, boolean _timestamp, boolean _no_output)
/*     */   {
/*  62 */     this.logger = _logger;
/*  63 */     this.name = _name;
/*  64 */     this.timestamp = _timestamp;
/*  65 */     this.no_output = _no_output;
/*     */   }
/*     */   
/*     */ 
/*     */   public org.gudy.azureus2.plugins.logging.Logger getLogger()
/*     */   {
/*  71 */     return this.logger;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  77 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  83 */     return org.gudy.azureus2.core3.logging.Logger.isEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDiagnostic()
/*     */   {
/*  89 */     setDiagnostic(0L, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setForce(boolean forceToFile)
/*     */   {
/*  96 */     this.diagnostic_logger.setForced(forceToFile);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getForce()
/*     */   {
/* 102 */     return this.diagnostic_logger.isForced();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDiagnostic(long max_file_size, boolean diag_timestamp)
/*     */   {
/* 110 */     if (this.diagnostic_logger == null)
/*     */     {
/* 112 */       this.diagnostic_logger = AEDiagnostics.getLogger(FileUtil.convertOSSpecificChars(this.name, false));
/*     */       
/* 114 */       if (max_file_size > 0L)
/*     */       {
/* 116 */         this.diagnostic_logger.setMaxFileSize((int)max_file_size);
/*     */       }
/*     */       
/* 119 */       this.diagnostic_logger.enableTimeStamp((!this.timestamp) && (diag_timestamp));
/*     */       
/* 121 */       addListener(new LoggerChannelListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void messageLogged(int type, String content)
/*     */         {
/*     */ 
/*     */ 
/* 129 */           LoggerChannelImpl.this.diagnostic_logger.log(content);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void messageLogged(String str, Throwable error)
/*     */         {
/* 137 */           LoggerChannelImpl.this.diagnostic_logger.log(str);
/* 138 */           LoggerChannelImpl.this.diagnostic_logger.log(error);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   private int LogTypePluginToCore(int pluginLogType) {
/* 145 */     switch (pluginLogType) {
/*     */     case 1: 
/* 147 */       return 0;
/*     */     case 2: 
/* 149 */       return 1;
/*     */     case 3: 
/* 151 */       return 3;
/*     */     }
/* 153 */     return 0;
/*     */   }
/*     */   
/*     */   private void notifyListeners(int log_type, String data) {
/* 157 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try {
/* 159 */         LoggerChannelListener l = (LoggerChannelListener)this.listeners.get(i);
/* 160 */         l.messageLogged(log_type, data);
/*     */       } catch (Throwable e) {
/* 162 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyListeners(String listenersText, Throwable error) {
/* 168 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try {
/* 170 */         LoggerChannelListener l = (LoggerChannelListener)this.listeners.get(i);
/* 171 */         l.messageLogged(listenersText, error);
/*     */       } catch (Throwable e) {
/* 173 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void log(int log_type, String data) {
/* 179 */     notifyListeners(log_type, addTimeStamp(data));
/*     */     
/* 181 */     if ((isEnabled()) && (!this.no_output)) {
/* 182 */       data = "[" + this.name + "] " + data;
/*     */       
/* 184 */       org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LOGID, LogTypePluginToCore(log_type), data));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void log(String data)
/*     */   {
/* 193 */     log(1, data);
/*     */   }
/*     */   
/*     */   public void log(Object[] relatedTo, int log_type, String data) {
/*     */     String listenerData;
/*     */     String listenerData;
/* 199 */     if (relatedTo != null) {
/* 200 */       StringBuilder text = new StringBuilder();
/* 201 */       for (int i = 0; i < relatedTo.length; i++) {
/* 202 */         Object obj = relatedTo[i];
/*     */         
/* 204 */         if (obj != null)
/*     */         {
/*     */ 
/* 207 */           if (i > 0) {
/* 208 */             text.append("; ");
/*     */           }
/* 210 */           if ((obj instanceof LogRelation)) {
/* 211 */             text.append(((LogRelation)obj).getRelationText());
/*     */           } else {
/* 213 */             text.append("RelatedTo[").append(obj.toString()).append("]");
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 219 */       listenerData = text.toString() + "] " + data;
/*     */     } else {
/* 221 */       listenerData = data;
/*     */     }
/*     */     
/* 224 */     notifyListeners(log_type, addTimeStamp(listenerData));
/*     */     
/* 226 */     if ((isEnabled()) && (!this.no_output)) {
/* 227 */       data = "[" + this.name + "] " + data;
/* 228 */       org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(relatedTo, LOGID, LogTypePluginToCore(log_type), data));
/*     */     }
/*     */   }
/*     */   
/*     */   public void log(Object relatedTo, int log_type, String data)
/*     */   {
/* 234 */     log(new Object[] { relatedTo }, log_type, data);
/*     */   }
/*     */   
/*     */   public void log(Throwable error)
/*     */   {
/* 239 */     log("", error);
/*     */   }
/*     */   
/*     */   public void log(String str, Throwable error) {
/* 243 */     notifyListeners(str.equals("") ? "" : addTimeStamp(str), error);
/*     */     
/* 245 */     if (!this.no_output) {
/* 246 */       LogEvent event = new LogEvent(LOGID, "[" + this.name + "] " + str, error);
/* 247 */       org.gudy.azureus2.core3.logging.Logger.log(event);
/*     */     }
/*     */   }
/*     */   
/*     */   public void log(Object[] relatedTo, String str, Throwable error) {
/* 252 */     notifyListeners(str.equals("") ? "" : addTimeStamp(str), error);
/*     */     
/* 254 */     if ((isEnabled()) && (!this.no_output)) {
/* 255 */       str = "[" + this.name + "] " + str;
/*     */       
/* 257 */       org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(relatedTo, LOGID, str, error));
/*     */     }
/*     */   }
/*     */   
/*     */   public void log(Object relatedTo, String str, Throwable error)
/*     */   {
/* 263 */     log(new Object[] { relatedTo }, str, error);
/*     */   }
/*     */   
/*     */   public void log(Object[] relatedTo, String data) {
/* 267 */     log(relatedTo, 1, data);
/*     */   }
/*     */   
/*     */   public void log(Object relatedTo, String data) {
/* 271 */     log(relatedTo, 1, data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void logAlert(int alert_type, String message, boolean repeatable)
/*     */   {
/* 280 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try {
/* 282 */         ((LoggerChannelListener)this.listeners.get(i)).messageLogged(alert_type, addTimeStamp(message));
/*     */       }
/*     */       catch (Throwable e) {
/* 285 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 289 */     if (!this.no_output)
/*     */     {
/*     */       int at;
/* 292 */       switch (alert_type) {
/*     */       case 1: 
/* 294 */         at = 0;
/* 295 */         break;
/*     */       
/*     */       case 2: 
/* 298 */         at = 1;
/* 299 */         break;
/*     */       
/*     */       default: 
/* 302 */         at = 3;
/*     */       }
/*     */       
/*     */       
/*     */ 
/* 307 */       org.gudy.azureus2.core3.logging.Logger.log(new LogAlert(repeatable, at, message));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logAlert(int alert_type, String message)
/*     */   {
/* 317 */     logAlert(alert_type, message, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logAlertRepeatable(int alert_type, String message)
/*     */   {
/* 325 */     logAlert(alert_type, message, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logAlert(String message, Throwable e)
/*     */   {
/* 333 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 336 */         ((LoggerChannelListener)this.listeners.get(i)).messageLogged(addTimeStamp(message), e);
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/* 340 */         Debug.printStackTrace(f);
/*     */       }
/*     */     }
/*     */     
/* 344 */     if (!this.no_output) {
/* 345 */       org.gudy.azureus2.core3.logging.Logger.log(new LogAlert(false, message, e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logAlertRepeatable(String message, Throwable e)
/*     */   {
/* 355 */     for (int i = 0; i < this.listeners.size(); i++) {
/*     */       try
/*     */       {
/* 358 */         ((LoggerChannelListener)this.listeners.get(i)).messageLogged(addTimeStamp(message), e);
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/* 362 */         Debug.printStackTrace(f);
/*     */       }
/*     */     }
/*     */     
/* 366 */     if (!this.no_output) {
/* 367 */       org.gudy.azureus2.core3.logging.Logger.log(new LogAlert(true, message, e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(LoggerChannelListener l)
/*     */   {
/* 379 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(LoggerChannelListener l)
/*     */   {
/* 386 */     this.listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String addTimeStamp(String data)
/*     */   {
/* 393 */     if (this.timestamp)
/*     */     {
/* 395 */       return getTimeStamp() + data;
/*     */     }
/*     */     
/*     */ 
/* 399 */     return data;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getTimeStamp()
/*     */   {
/* 406 */     Calendar now = GregorianCalendar.getInstance();
/*     */     
/* 408 */     String timeStamp = "[" + now.get(11) + ":" + format(now.get(12)) + ":" + format(now.get(13)) + "] ";
/*     */     
/*     */ 
/* 411 */     return timeStamp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String format(int n)
/*     */   {
/* 418 */     if (n < 10)
/*     */     {
/* 420 */       return "0" + n;
/*     */     }
/*     */     
/* 423 */     return String.valueOf(n);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/logging/LoggerChannelImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */