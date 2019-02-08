/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.ILogAlertListener;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageSlideShell;
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
/*     */ public class Alerts
/*     */ {
/*  48 */   private static List<LogAlert> alert_queue = new ArrayList();
/*     */   
/*  50 */   private static AEMonitor alert_queue_mon = new AEMonitor("Alerts:Q");
/*     */   
/*  52 */   private static ArrayList<String> alert_history = new ArrayList(0);
/*     */   
/*  54 */   private static ArrayList<LogAlert> listUnviewedLogAlerts = new ArrayList(0);
/*     */   
/*  56 */   private static AEMonitor alert_history_mon = new AEMonitor("Alerts:H");
/*     */   
/*  58 */   private static CopyOnWriteList<AlertHistoryListener> listMessageHistoryListeners = new CopyOnWriteList(1);
/*     */   
/*  60 */   private static boolean initialisation_complete = false;
/*     */   
/*     */   private static volatile boolean stopping;
/*     */   
/*  64 */   private static CopyOnWriteList<AlertListener> listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void showAlert(LogAlert alert)
/*     */   {
/*  74 */     Display display = SWTThread.getInstance().getDisplay();
/*     */     
/*  76 */     if (alert.err != null) {
/*  77 */       alert.details = Debug.getStackTrace(alert.err);
/*     */     }
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
/*  92 */     for (Iterator<AlertListener> iter = listeners.iterator(); iter.hasNext();) {
/*  93 */       AlertListener l = (AlertListener)iter.next();
/*  94 */       if (!l.allowPopup(alert.relatedTo, alert.entryType)) {
/*  95 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 100 */     if ((stopping) || (display.isDisposed())) {
/*     */       try
/*     */       {
/* 103 */         alert_queue_mon.enter();
/*     */         
/* 105 */         List close_alerts = COConfigurationManager.getListParameter("Alerts.raised.at.close", new ArrayList());
/*     */         
/*     */ 
/* 108 */         Map alert_map = new HashMap();
/*     */         
/* 110 */         alert_map.put("type", new Long(alert.entryType));
/* 111 */         alert_map.put("message", alert.text);
/* 112 */         alert_map.put("timeout", new Long(alert.getGivenTimeoutSecs()));
/*     */         
/* 114 */         if (alert.details != null) {
/* 115 */           alert_map.put("details", alert.details);
/*     */         }
/*     */         
/* 118 */         close_alerts.add(alert_map);
/*     */         
/* 120 */         COConfigurationManager.setParameter("Alerts.raised.at.close", close_alerts); return;
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/* 125 */         alert_queue_mon.exit();
/*     */       }
/*     */     }
/*     */     
/* 129 */     if (display.isDisposed()) {
/* 130 */       return;
/*     */     }
/*     */     
/*     */ 
/* 134 */     String key = alert.text + ":" + alert.err.toString();
/*     */     try
/*     */     {
/* 137 */       alert_history_mon.enter();
/*     */       
/* 139 */       if (!alert.repeatable) {
/* 140 */         if (alert_history.contains(key)) {
/*     */           return;
/*     */         }
/*     */         
/* 144 */         alert_history.add(key);
/*     */         
/* 146 */         if (alert_history.size() > 512) {
/* 147 */           alert_history.remove(0);
/*     */         }
/*     */       }
/*     */       
/* 151 */       listUnviewedLogAlerts.add(alert);
/*     */     } finally {
/* 153 */       alert_history_mon.exit();
/*     */     }
/*     */     
/* 156 */     AlertHistoryListener[] array = (AlertHistoryListener[])listMessageHistoryListeners.toArray(new AlertHistoryListener[0]);
/* 157 */     for (AlertHistoryListener l : array) {
/* 158 */       l.alertHistoryAdded(alert);
/*     */     }
/*     */     
/* 161 */     if (alert.forceNotify)
/*     */     {
/* 163 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 165 */           int swtIconID = 2;
/* 166 */           switch (this.val$alert.getType()) {
/*     */           case 2: 
/* 168 */             swtIconID = 8;
/* 169 */             break;
/*     */           
/*     */           case 3: 
/* 172 */             swtIconID = 1;
/*     */           }
/*     */           
/*     */           
/* 176 */           String text = this.val$alert.getText();
/*     */           
/* 178 */           int pos = text.indexOf(":");
/*     */           
/*     */           String title;
/*     */           String title;
/* 182 */           if (pos == -1)
/*     */           {
/* 184 */             title = "";
/*     */           }
/*     */           else
/*     */           {
/* 188 */             title = text.substring(0, pos).trim();
/*     */             
/* 190 */             text = text.substring(pos + 1).trim();
/*     */           }
/*     */           
/* 193 */           new MessageSlideShell(SWTThread.getInstance().getDisplay(), swtIconID, title, text, this.val$alert.details, this.val$alert.getContext(), this.val$alert.getTimeoutSecs());
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void initComplete()
/*     */   {
/* 203 */     new AEThread2("Init Complete", true) {
/*     */       public void run() {
/*     */         try {
/* 206 */           Alerts.alert_queue_mon.enter();
/*     */           
/* 208 */           Alerts.access$102(true);
/*     */           
/* 210 */           for (int i = 0; i < Alerts.alert_queue.size(); i++) {
/* 211 */             LogAlert alert = (LogAlert)Alerts.alert_queue.get(i);
/*     */             
/* 213 */             Alerts.showAlert(alert);
/*     */           }
/*     */           
/* 216 */           List close_alerts = COConfigurationManager.getListParameter("Alerts.raised.at.close", new ArrayList());
/*     */           
/*     */ 
/*     */ 
/* 220 */           if (close_alerts.size() > 0)
/*     */           {
/* 222 */             COConfigurationManager.setParameter("Alerts.raised.at.close", new ArrayList());
/*     */             
/*     */ 
/* 225 */             String intro = MessageText.getString("alert.raised.at.close") + "\n";
/*     */             
/*     */ 
/* 228 */             for (int i = 0; i < close_alerts.size(); i++) {
/*     */               try
/*     */               {
/* 231 */                 Map alert_map = (Map)close_alerts.get(i);
/*     */                 
/* 233 */                 BDecoder.decodeStrings(alert_map);
/*     */                 
/* 235 */                 String details = MapUtils.getMapString(alert_map, "details", null);
/*     */                 
/* 237 */                 int timeout = MapUtils.getMapInt(alert_map, "timeout", -1);
/*     */                 
/* 239 */                 int entryType = MapUtils.getMapInt(alert_map, "type", 0);
/*     */                 
/* 241 */                 String message = intro + MapUtils.getMapString(alert_map, "message", "");
/*     */                 
/* 243 */                 LogAlert logAlert = new LogAlert(false, entryType, message, timeout);
/* 244 */                 logAlert.details = details;
/*     */                 
/* 246 */                 Alerts.showAlert(logAlert);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 250 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 255 */           Alerts.alert_queue.clear();
/*     */         }
/*     */         finally
/*     */         {
/* 259 */           Alerts.alert_queue_mon.exit();
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */   public static void stopInitiated() {
/* 266 */     stopping = true;
/*     */   }
/*     */   
/*     */   public static void init() {
/* 270 */     Logger.addListener(new ILogAlertListener()
/*     */     {
/*     */ 
/*     */       public void alertRaised(LogAlert alert)
/*     */       {
/* 275 */         if (!Alerts.initialisation_complete) {
/*     */           try {
/* 277 */             Alerts.alert_queue_mon.enter();
/*     */             
/* 279 */             Alerts.alert_queue.add(alert);
/*     */           }
/*     */           finally
/*     */           {
/* 283 */             Alerts.alert_queue_mon.exit();
/*     */           }
/*     */           
/* 286 */           return;
/*     */         }
/*     */         
/* 289 */         Alerts.showAlert(alert);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void addListener(AlertListener l)
/*     */   {
/* 296 */     listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ArrayList<LogAlert> getUnviewedLogAlerts()
/*     */   {
/* 305 */     return new ArrayList(listUnviewedLogAlerts);
/*     */   }
/*     */   
/*     */ 
/*     */   public static int getUnviewedLogAlertCount()
/*     */   {
/* 311 */     return listUnviewedLogAlerts.size();
/*     */   }
/*     */   
/*     */   public static void addMessageHistoryListener(AlertHistoryListener l) {
/* 315 */     listMessageHistoryListeners.add(l);
/*     */   }
/*     */   
/*     */   public static void markAlertAsViewed(LogAlert alert) {
/* 319 */     boolean removed = listUnviewedLogAlerts.remove(alert);
/* 320 */     if (removed) {
/* 321 */       AlertHistoryListener[] array = (AlertHistoryListener[])listMessageHistoryListeners.toArray(new AlertHistoryListener[0]);
/* 322 */       for (AlertHistoryListener l : array) {
/* 323 */         l.alertHistoryRemoved(alert);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface AlertHistoryListener
/*     */   {
/*     */     public abstract void alertHistoryAdded(LogAlert paramLogAlert);
/*     */     
/*     */     public abstract void alertHistoryRemoved(LogAlert paramLogAlert);
/*     */   }
/*     */   
/*     */   public static abstract interface AlertListener
/*     */   {
/*     */     public abstract boolean allowPopup(Object[] paramArrayOfObject, int paramInt);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/Alerts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */