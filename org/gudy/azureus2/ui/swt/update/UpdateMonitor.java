/*     */ package org.gudy.azureus2.ui.swt.update;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UIFunctionsUserPrompter;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.io.File;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckerListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManagerDecisionListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManagerListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManagerVerificationListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateProgressListener;
/*     */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.StringListChooser;
/*     */ import org.gudy.azureus2.ui.swt.progress.IProgressReport;
/*     */ import org.gudy.azureus2.ui.swt.progress.IProgressReportConstants;
/*     */ import org.gudy.azureus2.ui.swt.progress.IProgressReporter;
/*     */ import org.gudy.azureus2.ui.swt.progress.IProgressReporterListener;
/*     */ import org.gudy.azureus2.ui.swt.progress.ProgressReportingManager;
/*     */ import org.gudy.azureus2.update.CoreUpdateChecker;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UpdateMonitor
/*     */   implements UpdateCheckInstanceListener
/*     */ {
/*  61 */   private static final LogIDs LOGID = LogIDs.GUI;
/*     */   
/*     */   public static final long AUTO_UPDATE_CHECK_PERIOD = 82800000L;
/*     */   
/*     */   public static final long AUTO_UPDATE_CHECK_PERIOD_BETA = 14400000L;
/*     */   
/*     */   private static final String MSG_PREFIX = "UpdateMonitor.messagebox.";
/*     */   
/*     */   private static UpdateMonitor singleton;
/*  70 */   private static AEMonitor class_mon = new AEMonitor("UpdateMonitor:class");
/*     */   private AzureusCore azCore;
/*     */   private UpdateWindow current_update_window;
/*     */   
/*  74 */   public static UpdateMonitor getSingleton(AzureusCore core) { try { class_mon.enter();
/*     */       
/*  76 */       if (singleton == null)
/*     */       {
/*  78 */         singleton = new UpdateMonitor(core);
/*     */       }
/*     */       
/*  81 */       return singleton;
/*     */     }
/*     */     finally
/*     */     {
/*  85 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private UpdateCheckInstance current_update_instance;
/*     */   
/*     */ 
/*     */   private long last_recheck_time;
/*     */   
/*     */   protected UpdateMonitor(AzureusCore _azureus_core)
/*     */   {
/*  98 */     this.azCore = _azureus_core;
/*     */     
/* 100 */     PluginInterface defPI = PluginInitializer.getDefaultInterface();
/* 101 */     UpdateManager um = defPI.getUpdateManager();
/*     */     
/* 103 */     um.addListener(new UpdateManagerListener() {
/*     */       public void checkInstanceCreated(UpdateCheckInstance instance) {
/* 105 */         instance.addListener(UpdateMonitor.this);
/*     */         
/* 107 */         if (!instance.isLowNoise())
/*     */         {
/* 109 */           new UpdateMonitor.updateStatusChanger(UpdateMonitor.this, instance);
/*     */         }
/*     */         
/*     */       }
/* 113 */     });
/* 114 */     um.addVerificationListener(new UpdateManagerVerificationListener() {
/*     */       public boolean acceptUnVerifiedUpdate(Update update) {
/* 116 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 117 */         if (uiFunctions != null) {
/* 118 */           String title = MessageText.getString("UpdateMonitor.messagebox.accept.unverified.title");
/*     */           
/* 120 */           String text = MessageText.getString("UpdateMonitor.messagebox.accept.unverified.text", new String[] { update.getName() });
/*     */           
/*     */ 
/*     */ 
/* 124 */           UIFunctionsUserPrompter prompter = uiFunctions.getUserPrompter(title, text, new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 1);
/*     */           
/*     */ 
/*     */ 
/* 128 */           prompter.setRemember("UpdateMonitor.messagebox.accept.unverified", false, MessageText.getString("MessageBoxWindow.nomoreprompting"));
/*     */           
/* 130 */           prompter.setAutoCloseInMS(0);
/* 131 */           prompter.open(null);
/* 132 */           return prompter.waitUntilClosed() == 0;
/*     */         }
/*     */         
/* 135 */         return false;
/*     */       }
/*     */       
/*     */       public void verificationFailed(Update update, Throwable cause) {
/* 139 */         String cause_str = Debug.getNestedExceptionMessage(cause);
/* 140 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 141 */         if (uiFunctions != null) {
/* 142 */           String title = MessageText.getString("UpdateMonitor.messagebox.verification.failed.title");
/*     */           
/* 144 */           String text = MessageText.getString("UpdateMonitor.messagebox.verification.failed.text", new String[] { update.getName(), cause_str });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 149 */           uiFunctions.promptUser(title, text, new String[] { MessageText.getString("Button.ok") }, 0, null, null, false, 0, null);
/*     */ 
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 156 */     });
/* 157 */     SimpleTimer.addPeriodicEvent("UpdateMon:autocheck", COConfigurationManager.getBooleanParameter("Beta Programme Enabled") ? 14400000L : 82800000L, new TimerEventPerformer()
/*     */     {
/*     */       public void perform(TimerEvent ev)
/*     */       {
/* 161 */         UpdateMonitor.this.performAutoCheck(false);
/*     */       }
/*     */       
/* 164 */     });
/* 165 */     DelayedTask delayed_task = UtilitiesImpl.addDelayedTask("Update Check", new Runnable()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 177 */         if ((!Constants.isWindowsVistaOrHigher) && (!SystemProperties.isJavaWebStartInstance()))
/*     */         {
/* 179 */           String app_str = SystemProperties.getApplicationPath();
/*     */           
/* 181 */           if (!new File(app_str).canWrite())
/*     */           {
/* 183 */             final UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */             
/* 185 */             if (uiFunctions != null)
/*     */             {
/* 187 */               if (app_str.endsWith(File.separator))
/*     */               {
/* 189 */                 app_str = app_str.substring(0, app_str.length() - 1);
/*     */               }
/*     */               
/* 192 */               final String f_app_str = app_str;
/*     */               
/* 194 */               Utils.execSWTThread(new Runnable()
/*     */               {
/*     */ 
/*     */                 public void run()
/*     */                 {
/*     */ 
/* 200 */                   UIFunctionsUserPrompter prompt = uiFunctions.getUserPrompter(MessageText.getString("updater.cant.write.to.app.title"), MessageText.getString("updater.cant.write.to.app.details", new String[] { f_app_str }), new String[] { MessageText.getString("Button.ok") }, 0);
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 209 */                   prompt.setIconResource("warning");
/*     */                   
/* 211 */                   prompt.setRemember("UpdateMonitor.can.not.write.to.app.dir.2", false, MessageText.getString("MessageBoxWindow.nomoreprompting"));
/*     */                   
/*     */ 
/* 214 */                   prompt.open(null); } }, true);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 222 */         UpdateMonitor.this.performAutoCheck(true);
/*     */       }
/*     */       
/* 225 */     });
/* 226 */     delayed_task.queue();
/*     */   }
/*     */   
/*     */ 
/*     */   protected class updateStatusChanger
/*     */     implements IProgressReportConstants
/*     */   {
/*     */     UpdateCheckInstance instance;
/* 234 */     int check_num = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 239 */     IProgressReporter updateReporter = ProgressReportingManager.getInstance().addReporter(MessageText.getString("UpdateWindow.title"));
/*     */     
/*     */ 
/*     */     protected updateStatusChanger(UpdateCheckInstance _instance)
/*     */     {
/* 244 */       this.instance = _instance;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 249 */       this.updateReporter.setReporterType("reporterType_updater");
/* 250 */       this.updateReporter.setCancelAllowed(true);
/* 251 */       this.updateReporter.setTitle(MessageText.getString("updater.progress.window.title"));
/* 252 */       this.updateReporter.appendDetailMessage(UpdateMonitor.this.format(this.instance, "added"));
/*     */       
/* 254 */       String name = this.instance.getName();
/* 255 */       if (MessageText.keyExists(name)) {
/* 256 */         this.updateReporter.setMessage(MessageText.getString(name));
/*     */       } else {
/* 258 */         this.updateReporter.setMessage(name);
/*     */       }
/*     */       
/* 261 */       this.updateReporter.setMinimum(0);
/* 262 */       this.updateReporter.setMaximum(this.instance.getCheckers().length);
/* 263 */       this.updateReporter.setSelection(this.check_num, null);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 269 */       this.updateReporter.addListener(new IProgressReporterListener()
/*     */       {
/*     */         public int report(IProgressReport progressReport) {
/* 272 */           if ((progressReport.getReportType() == 2) || (progressReport.getReportType() == 4))
/*     */           {
/* 274 */             return 1;
/*     */           }
/*     */           
/* 277 */           if (progressReport.getReportType() == 1) {
/* 278 */             if (null != UpdateMonitor.updateStatusChanger.this.instance) {
/* 279 */               UpdateMonitor.updateStatusChanger.this.instance.cancel();
/*     */             }
/* 281 */             return 1;
/*     */           }
/*     */           
/* 284 */           return 0;
/*     */ 
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 292 */       });
/* 293 */       this.instance.addListener(new UpdateCheckInstanceListener() {
/*     */         public void cancelled(UpdateCheckInstance instance) {
/* 295 */           UpdateMonitor.updateStatusChanger.this.updateReporter.appendDetailMessage(UpdateMonitor.this.format(instance, MessageText.getString("Progress.reporting.status.canceled")));
/*     */           
/*     */ 
/* 298 */           UpdateMonitor.updateStatusChanger.this.updateReporter.cancel();
/*     */         }
/*     */         
/*     */         public void complete(UpdateCheckInstance instance) {
/* 302 */           UpdateMonitor.updateStatusChanger.this.updateReporter.appendDetailMessage(UpdateMonitor.this.format(instance, MessageText.getString("Progress.reporting.status.finished")));
/*     */           
/* 304 */           UpdateMonitor.updateStatusChanger.this.updateReporter.setDone();
/*     */         }
/*     */         
/* 307 */       });
/* 308 */       UpdateChecker[] checkers = this.instance.getCheckers();
/*     */       
/* 310 */       for (int i = 0; i < checkers.length; i++) {
/* 311 */         final UpdateChecker checker = checkers[i];
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 316 */         checker.addListener(new UpdateCheckerListener()
/*     */         {
/*     */           public void cancelled(UpdateChecker checker)
/*     */           {
/* 320 */             UpdateMonitor.updateStatusChanger.this.updateReporter.appendDetailMessage(UpdateMonitor.this.format(checker, MessageText.getString("Progress.reporting.status.canceled")));
/*     */           }
/*     */           
/*     */ 
/*     */           public void completed(UpdateChecker checker)
/*     */           {
/* 326 */             UpdateMonitor.updateStatusChanger.this.updateReporter.appendDetailMessage(UpdateMonitor.this.format(checker, MessageText.getString("Progress.reporting.status.finished")));
/*     */             
/*     */ 
/* 329 */             UpdateMonitor.updateStatusChanger.this.updateReporter.setSelection(++UpdateMonitor.updateStatusChanger.this.check_num, null);
/*     */           }
/*     */           
/*     */           public void failed(UpdateChecker checker)
/*     */           {
/* 334 */             UpdateMonitor.updateStatusChanger.this.updateReporter.appendDetailMessage(UpdateMonitor.this.format(checker, MessageText.getString("Progress.reporting.default.error")));
/*     */             
/*     */ 
/* 337 */             UpdateMonitor.updateStatusChanger.this.updateReporter.setSelection(++UpdateMonitor.updateStatusChanger.this.check_num, null);
/*     */             
/*     */ 
/* 340 */             UpdateMonitor.updateStatusChanger.this.updateReporter.setErrorMessage(null);
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 346 */         });
/* 347 */         checker.addProgressListener(new UpdateProgressListener() {
/*     */           public void reportProgress(String str) {
/* 349 */             UpdateMonitor.updateStatusChanger.this.updateReporter.appendDetailMessage(UpdateMonitor.this.format(checker, "    " + str));
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String format(UpdateCheckInstance instance, String str)
/*     */   {
/* 362 */     String name = instance.getName();
/* 363 */     if (MessageText.keyExists(name)) {
/* 364 */       name = MessageText.getString(name);
/*     */     }
/* 366 */     return name + " - " + str;
/*     */   }
/*     */   
/*     */   private String format(UpdateChecker checker, String str) {
/* 370 */     return "    " + checker.getComponent().getName() + " - " + str;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void requestRecheck()
/*     */   {
/* 376 */     if (Logger.isEnabled()) {
/* 377 */       Logger.log(new LogEvent(LOGID, "UpdateMonitor: recheck requested"));
/*     */     }
/*     */     
/* 380 */     performCheck(false, true, true, null);
/*     */   }
/*     */   
/*     */   protected void performAutoCheck(final boolean start_of_day) {
/* 384 */     boolean check_at_start = false;
/* 385 */     boolean check_periodic = false;
/* 386 */     boolean bOldSWT = SWT.getVersion() < 3139;
/*     */     
/*     */ 
/*     */ 
/* 390 */     if (!SystemProperties.isJavaWebStartInstance())
/*     */     {
/*     */ 
/* 393 */       check_at_start = (COConfigurationManager.getBooleanParameter("update.start")) || (bOldSWT);
/*     */       
/* 395 */       check_periodic = COConfigurationManager.getBooleanParameter("update.periodic");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 400 */     check_at_start = (check_at_start) || (check_periodic);
/*     */     
/* 402 */     if (((check_at_start) && (start_of_day)) || ((check_periodic) && (!start_of_day)))
/*     */     {
/* 404 */       performCheck(bOldSWT, true, false, null);
/*     */     }
/*     */     else
/*     */     {
/* 408 */       new DelayedEvent("UpdateMon:wait2", 5000L, new AERunnable() {
/*     */         public void runSupport() {
/* 410 */           if (start_of_day) {
/* 411 */             UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 412 */             if (uiFunctions != null) {
/* 413 */               uiFunctions.setStatusText("");
/*     */             }
/*     */           }
/*     */           
/* 417 */           CoreUpdateChecker.doUsageStats();
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void performCheck(final boolean bForce, final boolean automatic, boolean isRecheck, final UpdateCheckInstanceListener l)
/*     */   {
/* 430 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 432 */     if (isRecheck)
/*     */     {
/* 434 */       if ((this.last_recheck_time > now) || (now - this.last_recheck_time < 82800000L))
/*     */       {
/* 436 */         if (Logger.isEnabled()) {
/* 437 */           Logger.log(new LogEvent(LOGID, "skipping recheck as consecutive recheck too soon"));
/*     */         }
/*     */         
/* 440 */         return;
/*     */       }
/*     */       
/* 443 */       this.last_recheck_time = now;
/*     */     }
/*     */     else
/*     */     {
/* 447 */       this.last_recheck_time = 0L;
/*     */     }
/*     */     
/* 450 */     if (SystemProperties.isJavaWebStartInstance())
/*     */     {
/*     */ 
/* 453 */       if (Logger.isEnabled()) {
/* 454 */         Logger.log(new LogEvent(LOGID, "skipping update check as java web start"));
/*     */       }
/*     */       
/* 457 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 462 */     if ((this.current_update_window != null) && (!this.current_update_window.isDisposed())) {
/* 463 */       this.current_update_window.dispose();
/*     */     }
/*     */     
/* 466 */     if (this.current_update_instance != null)
/*     */     {
/* 468 */       this.current_update_instance.cancel();
/*     */     }
/*     */     
/* 471 */     if (bForce)
/*     */     {
/* 473 */       VersionCheckClient.getSingleton().clearCache();
/*     */     }
/*     */     
/* 476 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 477 */     if (uiFunctions != null)
/*     */     {
/* 479 */       uiFunctions.setStatusText("MainWindow.status.checking ...");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 484 */     AEThread2 t = new AEThread2("UpdateMonitor:kickoff", true) {
/*     */       public void run() {
/* 486 */         UpdateManager um = PluginInitializer.getDefaultInterface().getUpdateManager();
/*     */         
/* 488 */         UpdateMonitor.this.current_update_instance = um.createUpdateCheckInstance(bForce ? 1 : 2, "update.instance.update");
/*     */         
/*     */ 
/*     */ 
/* 492 */         if (!automatic)
/*     */         {
/* 494 */           UpdateMonitor.this.current_update_instance.setAutomatic(false);
/*     */         }
/*     */         
/* 497 */         if (l != null) {
/* 498 */           UpdateMonitor.this.current_update_instance.addListener(l);
/*     */         }
/* 500 */         UpdateMonitor.this.current_update_instance.start();
/*     */       }
/*     */       
/* 503 */     };
/* 504 */     t.start();
/*     */   }
/*     */   
/*     */   public void complete(final UpdateCheckInstance instance)
/*     */   {
/* 509 */     if (instance.isLowNoise())
/*     */     {
/* 511 */       handleLowNoise(instance);
/*     */       
/* 513 */       return;
/*     */     }
/*     */     
/* 516 */     boolean hasDownloads = false;
/*     */     
/* 518 */     Update[] us = instance.getUpdates();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 523 */     for (int i = 0; i < us.length; i++)
/*     */     {
/* 525 */       if (us[i].getDownloaders().length > 0)
/*     */       {
/* 527 */         hasDownloads = true;
/*     */         
/* 529 */         break;
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 534 */       int ui = ((Integer)instance.getProperty(1)).intValue();
/*     */       
/* 536 */       if (ui == 2)
/*     */       {
/* 538 */         new SimpleInstallUI(this, instance);
/*     */         
/* 540 */         return;
/*     */       }
/* 542 */       if (ui == 3)
/*     */       {
/* 544 */         new SilentInstallUI(this, instance);
/*     */         
/* 546 */         return;
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 551 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 557 */     boolean update_action = instance.getType() == 2;
/*     */     
/* 559 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 560 */     if (uiFunctions != null) {
/* 561 */       uiFunctions.setStatusText("");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 571 */     if (hasDownloads)
/*     */     {
/*     */ 
/*     */ 
/* 575 */       UpdateWindow this_window = null;
/* 576 */       boolean autoDownload = COConfigurationManager.getBooleanParameter("update.autodownload");
/*     */       
/* 578 */       if (update_action) {
/* 579 */         if ((!autoDownload) && ((this.current_update_window == null) || (this.current_update_window.isDisposed())))
/*     */         {
/*     */ 
/* 582 */           this_window = this.current_update_window = new UpdateWindow(this, this.azCore, instance);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else {
/* 588 */         this_window = new UpdateWindow(this, this.azCore, instance);
/*     */       }
/*     */       
/* 591 */       if (this_window != null)
/*     */       {
/* 593 */         for (int i = 0; i < us.length; i++)
/*     */         {
/* 595 */           if (us[i].getDownloaders().length > 0)
/*     */           {
/* 597 */             this_window.addUpdate(us[i]);
/*     */           }
/*     */         }
/*     */         
/* 601 */         this_window.updateAdditionComplete();
/*     */ 
/*     */       }
/* 604 */       else if (autoDownload) {
/* 605 */         new UpdateAutoDownloader(us, new UpdateAutoDownloader.cbCompletion()
/*     */         {
/*     */ 
/*     */           public void allUpdatesComplete(boolean requiresRestart, boolean bHadMandatoryUpdates)
/*     */           {
/*     */ 
/* 611 */             Boolean b = (Boolean)instance.getProperty(4);
/*     */             
/* 613 */             if ((b != null) && (b.booleanValue()))
/*     */             {
/* 615 */               return;
/*     */             }
/*     */             
/* 618 */             if (requiresRestart) {
/* 619 */               UpdateMonitor.this.handleRestart();
/* 620 */             } else if (bHadMandatoryUpdates)
/*     */             {
/*     */ 
/*     */ 
/* 624 */               UpdateMonitor.this.requestRecheck();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 629 */       else if (Logger.isEnabled()) {
/* 630 */         Logger.log(new LogEvent(LOGID, 1, "UpdateMonitor: user dialog already in progress, updates skipped"));
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */     }
/* 637 */     else if (Logger.isEnabled()) {
/* 638 */       Logger.log(new LogEvent(LOGID, "UpdateMonitor: check instance resulted in no user-actionable updates"));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancelled(UpdateCheckInstance instance)
/*     */   {
/* 645 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 646 */     if (uiFunctions != null) {
/* 647 */       uiFunctions.setStatusText("");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void handleRestart()
/*     */   {
/* 654 */     final UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */     
/* 656 */     if (uiFunctions != null)
/*     */     {
/* 658 */       int visiblity_state = uiFunctions.getVisibilityState();
/*     */       
/* 660 */       if ((visiblity_state == 1) && (COConfigurationManager.getBooleanParameter("Low Resource Silent Update Restart Enabled")))
/*     */       {
/*     */ 
/* 663 */         uiFunctions.dispose(true, false);
/*     */       }
/*     */       else
/*     */       {
/* 667 */         uiFunctions.performAction(2, Boolean.valueOf(Constants.isWindows7OrHigher), new UIFunctions.actionListener()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public void actionComplete(Object result)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 677 */             if (((Boolean)result).booleanValue())
/*     */             {
/* 679 */               uiFunctions.dispose(true, false);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     else {
/* 686 */       Debug.out("Can't handle restart as no ui functions available");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void addDecisionHandler(UpdateCheckInstance instance)
/*     */   {
/* 694 */     instance.addDecisionListener(new UpdateManagerDecisionListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public Object decide(Update update, int decision_type, String decision_name, String decision_description, Object decision_data)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 705 */         if (decision_type == 0)
/*     */         {
/* 707 */           String[] options = (String[])decision_data;
/*     */           
/* 709 */           Shell shell = UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell();
/*     */           
/* 711 */           if (shell == null)
/*     */           {
/* 713 */             Debug.out("Shell doesn't exist");
/*     */             
/* 715 */             return null;
/*     */           }
/*     */           
/* 718 */           StringListChooser chooser = new StringListChooser(shell);
/*     */           
/* 720 */           chooser.setTitle(decision_name);
/* 721 */           chooser.setText(decision_description);
/*     */           
/* 723 */           for (int i = 0; i < options.length; i++)
/*     */           {
/* 725 */             chooser.addOption(options[i]);
/*     */           }
/*     */           
/* 728 */           String result = chooser.open();
/*     */           
/* 730 */           return result;
/*     */         }
/*     */         
/* 733 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void handleLowNoise(UpdateCheckInstance instance)
/*     */   {
/* 742 */     addDecisionHandler(instance);
/*     */     
/* 744 */     Update[] updates = instance.getUpdates();
/*     */     try
/*     */     {
/* 747 */       for (int i = 0; i < updates.length; i++)
/*     */       {
/* 749 */         ResourceDownloader[] downloaders = updates[i].getDownloaders();
/*     */         
/* 751 */         for (int j = 0; j < downloaders.length; j++)
/*     */         {
/* 753 */           downloaders[j].download();
/*     */         }
/*     */       }
/*     */       
/* 757 */       boolean restart_required = false;
/*     */       
/* 759 */       for (int i = 0; i < updates.length; i++)
/*     */       {
/* 761 */         if (updates[i].getRestartRequired() == 2)
/*     */         {
/* 763 */           restart_required = true;
/*     */         }
/*     */       }
/*     */       
/* 767 */       if (restart_required)
/*     */       {
/* 769 */         handleRestart();
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 774 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/update/UpdateMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */