/*     */ package com.aelitis.azureus.ui.swt.browser.listener;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*     */ import com.aelitis.azureus.core.messenger.browser.BrowserMessage;
/*     */ import com.aelitis.azureus.core.messenger.browser.listeners.AbstractBrowserMessageListener;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import com.aelitis.net.magneturi.MagnetURIHandler;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.update.UpdateMonitor;
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
/*     */ public class ConfigListener
/*     */   extends AbstractBrowserMessageListener
/*     */ {
/*     */   public static final String DEFAULT_LISTENER_ID = "config";
/*     */   public static final String OP_GET_VERSION = "get-version";
/*     */   public static final String OP_NEW_INSTALL = "is-new-install";
/*     */   public static final String OP_CHECK_FOR_UPDATES = "check-for-updates";
/*     */   public static final String OP_GET_MAGNET_PORT = "get-magnet-port";
/*     */   public static final String OP_LOG_DIAGS = "log-diags";
/*     */   public static final String OP_LOG = "log";
/*     */   
/*     */   public ConfigListener(String id, BrowserWrapper browser)
/*     */   {
/*  63 */     super(id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ConfigListener(BrowserWrapper browser)
/*     */   {
/*  70 */     this("config", browser);
/*     */   }
/*     */   
/*     */   public void handleMessage(BrowserMessage message)
/*     */   {
/*     */     try {
/*  76 */       String opid = message.getOperationId();
/*     */       
/*  78 */       if ("get-version".equals(opid)) {
/*  79 */         Map decodedMap = message.getDecodedMap();
/*  80 */         String callback = MapUtils.getMapString(decodedMap, "callback", null);
/*  81 */         if (callback != null) {
/*  82 */           this.context.executeInBrowser(callback + "('" + "5.7.6.0" + "')");
/*     */         } else {
/*  84 */           message.debug("bad or no callback param");
/*     */         }
/*  86 */       } else if ("is-new-install".equals(opid)) {
/*  87 */         Map decodedMap = message.getDecodedMap();
/*  88 */         String callback = MapUtils.getMapString(decodedMap, "callback", null);
/*  89 */         if (callback != null) {
/*  90 */           this.context.executeInBrowser(callback + "(" + COConfigurationManager.isNewInstall() + ")");
/*     */         } else {
/*  92 */           message.debug("bad or no callback param");
/*     */         }
/*  94 */       } else if ("check-for-updates".equals(opid))
/*     */       {
/*  96 */         checkForUpdates();
/*     */       }
/*  98 */       else if ("get-magnet-port".equals(opid))
/*     */       {
/* 100 */         Map decodedMap = message.getDecodedMap();
/*     */         
/* 102 */         String callback = MapUtils.getMapString(decodedMap, "callback", null);
/*     */         
/* 104 */         if (callback != null)
/*     */         {
/* 106 */           this.context.executeInBrowser(callback + "('" + MagnetURIHandler.getSingleton().getPort() + "')");
/*     */         }
/*     */         else
/*     */         {
/* 110 */           message.debug("bad or no callback param");
/*     */         }
/* 112 */       } else if ("log-diags".equals(opid))
/*     */       {
/* 114 */         logDiagnostics();
/* 115 */       } else if ("log".equals(opid)) {
/* 116 */         Map decodedMap = message.getDecodedMap();
/* 117 */         String loggerName = MapUtils.getMapString(decodedMap, "log-name", "browser");
/*     */         
/* 119 */         String text = MapUtils.getMapString(decodedMap, "text", "");
/*     */         
/* 121 */         AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger(loggerName);
/* 122 */         diag_logger.log(text);
/* 123 */         if (ConstantsVuze.DIAG_TO_STDOUT) {
/* 124 */           System.out.println(Thread.currentThread().getName() + "|" + System.currentTimeMillis() + "] " + text);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {
/* 129 */       message.debug("handle Config message", t);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void logDiagnostics() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void checkForUpdates()
/*     */   {
/* 145 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 146 */     if (uiFunctions != null) {
/* 147 */       uiFunctions.bringToFront();
/*     */     }
/* 149 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 151 */         UpdateMonitor.getSingleton(core).performCheck(true, false, false, null);
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/listener/ConfigListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */