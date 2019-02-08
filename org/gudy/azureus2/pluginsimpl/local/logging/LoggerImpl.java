/*     */ package org.gudy.azureus2.pluginsimpl.local.logging;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.logging.ILogAlertListener;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.impl.FileLogging;
/*     */ import org.gudy.azureus2.core3.logging.impl.FileLoggingAdapter;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.logging.FileLoggerAdapter;
/*     */ import org.gudy.azureus2.plugins.logging.LogAlertListener;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerAlertListener;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
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
/*     */ public class LoggerImpl
/*     */   implements org.gudy.azureus2.plugins.logging.Logger
/*     */ {
/*     */   private PluginInterface pi;
/*  50 */   private List channels = new ArrayList();
/*  51 */   private Map alert_listeners_map = new HashMap();
/*  52 */   private Map alert_listeners_map2 = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   public LoggerImpl(PluginInterface _pi)
/*     */   {
/*  58 */     this.pi = _pi;
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface getPluginInterface()
/*     */   {
/*  64 */     return this.pi;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public LoggerChannel getChannel(String name)
/*     */   {
/*  71 */     LoggerChannel channel = new LoggerChannelImpl(this, name, false, false);
/*     */     
/*  73 */     this.channels.add(channel);
/*     */     
/*  75 */     return channel;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public LoggerChannel getTimeStampedChannel(String name)
/*     */   {
/*  82 */     LoggerChannel channel = new LoggerChannelImpl(this, name, true, false);
/*     */     
/*  84 */     this.channels.add(channel);
/*     */     
/*  86 */     return channel;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public LoggerChannel getNullChannel(String name)
/*     */   {
/*  93 */     LoggerChannel channel = new LoggerChannelImpl(this, name, true, true);
/*     */     
/*  95 */     this.channels.add(channel);
/*     */     
/*  97 */     return channel;
/*     */   }
/*     */   
/*     */ 
/*     */   public LoggerChannel[] getChannels()
/*     */   {
/* 103 */     LoggerChannel[] res = new LoggerChannel[this.channels.size()];
/*     */     
/* 105 */     this.channels.toArray(res);
/*     */     
/* 107 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addAlertListener(final LoggerAlertListener listener)
/*     */   {
/* 115 */     ILogAlertListener lg_listener = new ILogAlertListener() {
/*     */       public void alertRaised(LogAlert alert) {
/* 117 */         if (alert.err == null) {
/*     */           int type;
/*     */           int type;
/* 120 */           if (alert.entryType == 0) {
/* 121 */             type = 1; } else { int type;
/* 122 */             if (alert.entryType == 1) {
/* 123 */               type = 2;
/*     */             } else {
/* 125 */               type = 3;
/*     */             }
/*     */           }
/* 128 */           listener.alertLogged(type, alert.text, alert.repeatable);
/*     */         }
/*     */         else {
/* 131 */           listener.alertLogged(alert.text, alert.err, alert.repeatable);
/*     */         }
/*     */         
/*     */       }
/* 135 */     };
/* 136 */     this.alert_listeners_map.put(listener, lg_listener);
/*     */     
/* 138 */     org.gudy.azureus2.core3.logging.Logger.addListener(lg_listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeAlertListener(LoggerAlertListener listener)
/*     */   {
/* 145 */     ILogAlertListener lg_listener = (ILogAlertListener)this.alert_listeners_map.remove(listener);
/*     */     
/* 147 */     if (lg_listener != null)
/*     */     {
/* 149 */       org.gudy.azureus2.core3.logging.Logger.removeListener(lg_listener);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addAlertListener(final LogAlertListener listener) {
/* 154 */     ILogAlertListener lg_listener = new ILogAlertListener() {
/* 155 */       private HashSet set = new HashSet();
/*     */       
/* 157 */       public void alertRaised(LogAlert alert) { if (!alert.repeatable) {
/* 158 */           if (this.set.contains(alert.text)) return;
/* 159 */           this.set.add(alert.text);
/*     */         }
/* 161 */         listener.alertRaised(alert);
/*     */       }
/* 163 */     };
/* 164 */     this.alert_listeners_map2.put(listener, lg_listener);
/* 165 */     org.gudy.azureus2.core3.logging.Logger.addListener(lg_listener);
/*     */   }
/*     */   
/*     */   public void removeAlertListener(LogAlertListener listener) {
/* 169 */     ILogAlertListener lg_listener = (ILogAlertListener)this.alert_listeners_map2.remove(listener);
/* 170 */     if (lg_listener != null) {
/* 171 */       org.gudy.azureus2.core3.logging.Logger.removeListener(lg_listener);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addFileLoggingListener(FileLoggerAdapter listener) {
/* 176 */     FileLogging fileLogging = org.gudy.azureus2.core3.logging.Logger.getFileLoggingInstance();
/* 177 */     if (fileLogging == null) {
/* 178 */       return;
/*     */     }
/* 180 */     fileLogging.addListener(new PluginFileLoggerAdapater(fileLogging, listener));
/*     */   }
/*     */   
/*     */   public void removeFileLoggingListener(FileLoggerAdapter listener) {
/* 184 */     FileLogging fileLogging = org.gudy.azureus2.core3.logging.Logger.getFileLoggingInstance();
/* 185 */     if (fileLogging == null) {
/* 186 */       return;
/*     */     }
/*     */     
/* 189 */     Object[] listeners = fileLogging.getListeners().toArray();
/* 190 */     for (int i = 0; i < listeners.length; i++) {
/* 191 */       if ((listeners[i] instanceof PluginFileLoggerAdapater)) {
/* 192 */         PluginFileLoggerAdapater l = (PluginFileLoggerAdapater)listeners[i];
/* 193 */         if (l.listener == listener) {
/* 194 */           fileLogging.removeListener(l);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static class PluginFileLoggerAdapater extends FileLoggingAdapter {
/*     */     public FileLoggerAdapter listener;
/*     */     
/*     */     public PluginFileLoggerAdapater(FileLogging fileLogging, FileLoggerAdapter listener) {
/* 204 */       fileLogging.addListener(this);
/* 205 */       this.listener = listener;
/*     */     }
/*     */     
/*     */     public boolean logToFile(LogEvent event, StringBuffer lineOut) {
/* 209 */       return this.listener.logToFile(lineOut);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/logging/LoggerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */