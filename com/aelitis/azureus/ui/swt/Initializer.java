/*     */ package com.aelitis.azureus.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*     */ import com.aelitis.azureus.core.AzureusCoreException;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.messenger.ClientMessageContext;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessenger;
/*     */ import com.aelitis.azureus.core.messenger.config.PlatformConfigMessenger;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*     */ import com.aelitis.azureus.core.versioncheck.VersionCheckClientListener;
/*     */ import com.aelitis.azureus.ui.IUIIntializer;
/*     */ import com.aelitis.azureus.ui.InitializerListener;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.ConfigListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.DisplayListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.TorrentListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.VuzeListener;
/*     */ import com.aelitis.azureus.ui.swt.browser.msg.MessageDispatcherSWT;
/*     */ import com.aelitis.azureus.ui.swt.devices.DeviceManagerUI;
/*     */ import com.aelitis.azureus.ui.swt.feature.FeatureManagerUI;
/*     */ import com.aelitis.azureus.ui.swt.search.SearchUI;
/*     */ import com.aelitis.azureus.ui.swt.shells.main.MainWindowFactory;
/*     */ import com.aelitis.azureus.ui.swt.shells.main.MainWindowFactory.MainWindowInitStub;
/*     */ import com.aelitis.azureus.ui.swt.subscriptions.SubscriptionManagerUI;
/*     */ import com.aelitis.azureus.ui.swt.utils.UIMagnetHandler;
/*     */ import com.aelitis.azureus.util.InitialisationFunctions;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginEvent;
/*     */ import org.gudy.azureus2.plugins.PluginEventListener;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*     */ import org.gudy.azureus2.ui.swt.Alerts;
/*     */ import org.gudy.azureus2.ui.swt.LocaleUtilSWT;
/*     */ import org.gudy.azureus2.ui.swt.StartServer;
/*     */ import org.gudy.azureus2.ui.swt.UIConfigDefaultsSWT;
/*     */ import org.gudy.azureus2.ui.swt.UISwitcherUtil;
/*     */ import org.gudy.azureus2.ui.swt.UserAlerts;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.auth.AuthenticatorWindow;
/*     */ import org.gudy.azureus2.ui.swt.auth.CertificateTrustWindow;
/*     */ import org.gudy.azureus2.ui.swt.auth.CryptoWindow;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThreadAlreadyInstanciatedException;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SplashWindow;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*     */ import org.gudy.azureus2.ui.swt.networks.SWTNetworkSelection;
/*     */ import org.gudy.azureus2.ui.swt.pluginsinstaller.InstallPluginWizard;
/*     */ import org.gudy.azureus2.ui.swt.progress.ProgressWindow;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ import org.gudy.azureus2.ui.swt.update.UpdateMonitor;
/*     */ import org.gudy.azureus2.ui.swt.updater2.PreUpdateChecker;
/*     */ import org.gudy.azureus2.ui.swt.updater2.SWTUpdateChecker;
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
/*     */ public class Initializer
/*     */   implements IUIIntializer
/*     */ {
/* 109 */   private static boolean STARTUP_UIFIRST = System.getProperty("ui.startfirst", "1").equals("1");
/*     */   
/*     */ 
/* 112 */   public static final long startTime = System.currentTimeMillis();
/*     */   
/*     */   private StartServer startServer;
/*     */   
/*     */   private final AzureusCore core;
/*     */   
/*     */   private final String[] args;
/*     */   
/* 120 */   private CopyOnWriteList listeners = new CopyOnWriteList();
/*     */   
/* 122 */   private AEMonitor listeners_mon = new AEMonitor("Initializer:l");
/*     */   
/* 124 */   private int curPercent = 0;
/*     */   
/* 126 */   private AESemaphore semFilterLoader = new AESemaphore("filter loader");
/*     */   
/* 128 */   private AESemaphore init_task = new AESemaphore("delayed init");
/*     */   
/*     */ 
/*     */ 
/*     */   private MainWindowFactory.MainWindowInitStub windowInitStub;
/*     */   
/*     */ 
/*     */   private static Initializer lastInitializer;
/*     */   
/*     */ 
/*     */ 
/*     */   public Initializer(final AzureusCore core, StartServer startServer, String[] args)
/*     */   {
/* 141 */     this.core = core;
/* 142 */     this.args = args;
/* 143 */     this.startServer = startServer;
/* 144 */     lastInitializer = this;
/*     */     
/* 146 */     Thread filterLoaderThread = new AEThread("filter loader", true) {
/*     */       public void runSupport() {
/*     */         try {
/* 149 */           core.getIpFilterManager().getIPFilter();
/*     */         } finally {
/* 151 */           Initializer.this.semFilterLoader.releaseForever();
/*     */         }
/*     */       }
/* 154 */     };
/* 155 */     filterLoaderThread.setPriority(1);
/* 156 */     filterLoaderThread.start();
/*     */     try
/*     */     {
/* 159 */       SWTThread.createInstance(this);
/*     */     } catch (SWTThreadAlreadyInstanciatedException e) {
/* 161 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private void cleanupOldStuff() {
/* 166 */     File v3Shares = new File(SystemProperties.getUserPath(), "v3shares");
/* 167 */     if (v3Shares.isDirectory()) {
/* 168 */       FileUtil.recursiveDeleteNoCheck(v3Shares);
/*     */     }
/* 170 */     File dirFriends = new File(SystemProperties.getUserPath(), "friends");
/* 171 */     if (dirFriends.isDirectory()) {
/* 172 */       FileUtil.recursiveDeleteNoCheck(dirFriends);
/*     */     }
/* 174 */     File dirMedia = new File(SystemProperties.getUserPath(), "media");
/* 175 */     if (dirMedia.isDirectory()) {
/* 176 */       FileUtil.recursiveDeleteNoCheck(dirMedia);
/*     */     }
/* 178 */     deleteConfig("v3.Friends.dat");
/* 179 */     deleteConfig("unsentdata.config");
/* 180 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(final AzureusCore core) {
/* 182 */         new AEThread2("cleanupOldStuff", true) {
/*     */           public void run() {
/* 184 */             GlobalManager gm = core.getGlobalManager();
/* 185 */             List dms = gm.getDownloadManagers();
/* 186 */             for (Object o : dms) {
/* 187 */               DownloadManager dm = (DownloadManager)o;
/* 188 */               if (dm != null) {
/* 189 */                 String val = PlatformTorrentUtils.getContentMapString(dm.getTorrent(), "Ad ID");
/*     */                 
/* 191 */                 if (val != null) {
/*     */                   try {
/* 193 */                     gm.removeDownloadManager(dm, true, true);
/*     */                   }
/*     */                   catch (Exception e) {}
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }.start();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void deleteConfig(String name) {
/*     */     try {
/* 207 */       File file = new File(SystemProperties.getUserPath(), name);
/* 208 */       if (file.exists()) {
/* 209 */         file.delete();
/*     */       }
/*     */     }
/*     */     catch (Exception e) {}
/*     */     try {
/* 214 */       File file = new File(SystemProperties.getUserPath(), name + ".bak");
/* 215 */       if (file.exists()) {
/* 216 */         file.delete();
/*     */       }
/*     */     }
/*     */     catch (Exception e) {}
/*     */   }
/*     */   
/*     */   public void runInSWTThread() {
/* 223 */     UISwitcherUtil.calcUIMode();
/*     */     try
/*     */     {
/* 226 */       initializePlatformClientMessageContext();
/*     */     } catch (Exception e) {
/* 228 */       Debug.out(e);
/*     */     }
/* 230 */     new AEThread2("cleanupOldStuff", true) {
/*     */       public void run() {
/* 232 */         Initializer.this.cleanupOldStuff();
/*     */       }
/*     */       
/* 235 */     }.start();
/* 236 */     boolean uiClassic = COConfigurationManager.getStringParameter("ui").equals("az2");
/*     */     
/* 238 */     if (!uiClassic) {
/* 239 */       PlatformConfigMessenger.login(0L);
/*     */     }
/*     */     
/* 242 */     VersionCheckClient.getSingleton().addVersionCheckClientListener(true, new VersionCheckClientListener()
/*     */     {
/*     */       public void versionCheckStarted(String reason) {
/* 245 */         if (("us".equals(reason)) || ("up".equals(reason)))
/*     */         {
/* 247 */           PlatformConfigMessenger.sendVersionServerMap(VersionCheckClient.constructVersionCheckMessage(reason));
/*     */         }
/*     */         
/*     */       }
/* 251 */     });
/* 252 */     FeatureManagerUI.registerWithFeatureManager();
/*     */     
/* 254 */     COConfigurationManager.setBooleanDefault("ui.startfirst", true);
/* 255 */     STARTUP_UIFIRST = (STARTUP_UIFIRST) && (COConfigurationManager.getBooleanParameter("ui.startfirst", true));
/*     */     
/*     */ 
/* 258 */     if (!STARTUP_UIFIRST) {
/* 259 */       return;
/*     */     }
/*     */     
/*     */ 
/* 263 */     Colors.getInstance();
/*     */     
/* 265 */     UIConfigDefaultsSWT.initialize();
/*     */     
/* 267 */     UIConfigDefaultsSWTv3.initialize(this.core);
/*     */     
/* 269 */     checkInstallID();
/*     */     
/* 271 */     this.windowInitStub = MainWindowFactory.createAsync(Display.getDefault(), this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkInstallID()
/*     */   {
/* 280 */     String storedInstallID = COConfigurationManager.getStringParameter("install.id", null);
/* 281 */     String installID = "";
/* 282 */     File file = FileUtil.getApplicationFile("installer.log");
/* 283 */     if (file != null) {
/*     */       try {
/* 285 */         String s = FileUtil.readFileAsString(file, 1024);
/* 286 */         String[] split = s.split("[\r\n]");
/* 287 */         for (int i = 0; i < split.length; i++) {
/* 288 */           int posEquals = split[i].indexOf('=');
/* 289 */           if ((posEquals > 0) && (split[i].length() > posEquals + 1)) {
/* 290 */             installID = split[i].substring(posEquals + 1);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (IOException e) {}
/*     */     }
/*     */     
/* 297 */     if ((storedInstallID == null) || (!storedInstallID.equals(installID))) {
/* 298 */       COConfigurationManager.setParameter("install.id", installID);
/*     */     }
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/* 304 */     DelayedTask delayed_task = UtilitiesImpl.addDelayedTask("SWT Initialisation", new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 309 */         Initializer.this.init_task.reserve();
/*     */       }
/*     */       
/* 312 */     });
/* 313 */     delayed_task.queueFirst();
/*     */     
/*     */ 
/* 316 */     long startTime = SystemTime.getCurrentTime();
/*     */     
/* 318 */     new LocaleUtilSWT(this.core);
/*     */     
/* 320 */     final Display display = SWTThread.getInstance().getDisplay();
/*     */     
/* 322 */     new UIMagnetHandler(this.core);
/*     */     
/* 324 */     if (!STARTUP_UIFIRST)
/*     */     {
/* 326 */       Colors.getInstance();
/*     */       
/* 328 */       UIConfigDefaultsSWT.initialize();
/* 329 */       UIConfigDefaultsSWTv3.initialize(this.core);
/*     */     } else {
/* 331 */       COConfigurationManager.setBooleanDefault("Show Splash", false);
/*     */     }
/*     */     
/* 334 */     if (COConfigurationManager.getBooleanParameter("Show Splash")) {
/* 335 */       display.asyncExec(new AERunnable() {
/*     */         public void runSupport() {
/* 337 */           new SplashWindow(display, Initializer.this);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 342 */     System.out.println("Locale Initializing took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*     */     
/* 344 */     startTime = SystemTime.getCurrentTime();
/*     */     
/* 346 */     this.core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*     */     {
/*     */       private GlobalManager gm;
/*     */       
/*     */ 
/*     */ 
/*     */       public void componentCreated(AzureusCore core, AzureusCoreComponent component)
/*     */       {
/* 354 */         Initializer.this.reportPercent(Initializer.this.curPercent + 1);
/*     */         
/*     */ 
/* 357 */         if ((component instanceof GlobalManager))
/*     */         {
/* 359 */           Initializer.this.reportCurrentTaskByKey("splash.initializePlugins");
/*     */           
/* 361 */           this.gm = ((GlobalManager)component);
/*     */           
/* 363 */           InitialisationFunctions.earlyInitialisation(core);
/*     */         }
/* 365 */         else if ((component instanceof PluginInterface)) {
/* 366 */           PluginInterface pi = (PluginInterface)component;
/*     */           
/* 368 */           String s = MessageText.getString("splash.plugin.init") + " " + pi.getPluginName() + " v" + pi.getPluginVersion();
/*     */           
/* 370 */           Initializer.this.reportCurrentTask(s);
/*     */         }
/*     */       }
/*     */       
/*     */       public void started(AzureusCore core)
/*     */       {
/* 376 */         boolean main_window_will_report_complete = false;
/*     */         
/*     */         try
/*     */         {
/* 380 */           InitialisationFunctions.lateInitialisation(core);
/* 381 */           if (this.gm == null) {
/*     */             return;
/*     */           }
/*     */           
/*     */ 
/* 386 */           Colors.getInstance();
/*     */           
/* 388 */           Initializer.this.reportPercent(Initializer.this.curPercent + 1);
/* 389 */           new UserAlerts(this.gm);
/*     */           
/* 391 */           Initializer.this.reportCurrentTaskByKey("splash.initializeGui");
/*     */           
/* 393 */           Initializer.this.reportPercent(Initializer.this.curPercent + 1);
/*     */           
/* 395 */           main_window_will_report_complete = true;
/*     */           
/* 397 */           if (Initializer.STARTUP_UIFIRST) {
/* 398 */             Initializer.this.windowInitStub.init(core);
/*     */           } else {
/* 400 */             MainWindowFactory.create(core, Display.getDefault(), Initializer.this);
/*     */           }
/*     */           
/* 403 */           Initializer.this.reportCurrentTaskByKey("splash.openViews");
/*     */           
/* 405 */           SWTUpdateChecker.initialize();
/*     */           
/* 407 */           PreUpdateChecker.initialize(core, COConfigurationManager.getStringParameter("ui"));
/*     */           
/*     */ 
/* 410 */           UpdateMonitor.getSingleton(core);
/*     */           
/*     */ 
/* 413 */           Alerts.initComplete();
/*     */           
/*     */ 
/* 416 */           for (int i = 0; i < Initializer.this.args.length; i++)
/*     */           {
/* 418 */             String arg = Initializer.this.args[i];
/*     */             
/* 420 */             if (!arg.equalsIgnoreCase("--open"))
/*     */             {
/*     */ 
/*     */               try
/*     */               {
/*     */ 
/* 426 */                 TorrentOpener.openTorrent(arg);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 430 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 437 */           if (!main_window_will_report_complete) {
/* 438 */             Initializer.this.init_task.release();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void stopping(AzureusCore core) {}
/*     */       
/*     */ 
/*     */       public void stopped(AzureusCore core) {}
/*     */       
/*     */ 
/*     */       public boolean syncInvokeRequired()
/*     */       {
/* 452 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */       public boolean requiresPluginInitCompleteBeforeStartedEvent()
/*     */       {
/* 458 */         return false;
/*     */       }
/*     */       
/*     */       public boolean stopRequested(AzureusCore _core) throws AzureusCoreException
/*     */       {
/* 463 */         return Initializer.handleStopRestart(false);
/*     */       }
/*     */       
/*     */       public boolean restartRequested(AzureusCore core) {
/* 467 */         return Initializer.handleStopRestart(true);
/*     */       }
/*     */       
/*     */ 
/* 471 */     });
/* 472 */     reportCurrentTaskByKey("splash.initializeCore");
/*     */     
/* 474 */     boolean uiClassic = COConfigurationManager.getStringParameter("ui").equals("az2");
/*     */     try
/*     */     {
/* 477 */       new SearchUI();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 481 */       Debug.printStackTrace(e);
/*     */     }
/*     */     try
/*     */     {
/* 485 */       new SubscriptionManagerUI();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 489 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/*     */ 
/* 493 */     if (!uiClassic) {
/*     */       try {
/* 495 */         new DeviceManagerUI(this.core);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 499 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 503 */     if (this.core.canStart())
/*     */     {
/* 505 */       this.core.start();
/*     */       
/* 507 */       reportPercent(50);
/*     */       
/* 509 */       System.out.println("Core Initializing took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*     */       
/* 511 */       startTime = SystemTime.getCurrentTime();
/*     */       
/* 513 */       reportCurrentTaskByKey("splash.initializeUIElements");
/*     */       
/*     */ 
/* 516 */       Colors.getInstance();
/*     */       
/* 518 */       reportPercent(this.curPercent + 1);
/* 519 */       Alerts.init();
/*     */       
/* 521 */       reportPercent(this.curPercent + 1);
/* 522 */       ProgressWindow.register(this.core);
/*     */       
/* 524 */       reportPercent(this.curPercent + 1);
/* 525 */       new SWTNetworkSelection();
/*     */       
/* 527 */       reportPercent(this.curPercent + 1);
/* 528 */       new AuthenticatorWindow();
/* 529 */       new CryptoWindow();
/*     */       
/* 531 */       reportPercent(this.curPercent + 1);
/* 532 */       new CertificateTrustWindow();
/*     */       
/* 534 */       InstallPluginWizard.register(this.core, display);
/*     */       
/*     */ 
/*     */ 
/* 538 */       for (int i = 0; i < this.args.length; i++)
/*     */       {
/* 540 */         String arg = this.args[i];
/*     */         
/* 542 */         if (arg.equalsIgnoreCase("--open"))
/*     */         {
/* 544 */           UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */           
/* 546 */           if (uif == null)
/*     */             break;
/* 548 */           uif.bringToFront(); break;
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 556 */       final AESemaphore sem = new AESemaphore("waiter");
/*     */       
/* 558 */       Utils.execSWTThread(new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 564 */           MessageBoxShell mb = new MessageBoxShell(MessageText.getString("msgbox.force.close.title"), MessageText.getString("msgbox.force.close.text", new String[] { Initializer.this.core.getLockFile().getAbsolutePath() }), new String[] { MessageText.getString("Button.ok") }, 0);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 573 */           mb.setIconResource("error");
/*     */           
/* 575 */           mb.setModal(true);
/*     */           
/* 577 */           mb.open(new UserPrompterResultListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void prompterClosed(int result)
/*     */             {
/*     */ 
/*     */ 
/* 585 */               Initializer.8.this.val$sem.releaseForever();
/*     */             }
/*     */             
/*     */           });
/*     */         }
/* 590 */       });
/* 591 */       sem.reserve();
/*     */       
/* 593 */       SESecurityManager.exitVM(1);
/*     */     }
/*     */   }
/*     */   
/*     */   public void stopIt(boolean isForRestart, boolean isCloseAreadyInProgress) throws AzureusCoreException
/*     */   {
/* 599 */     if ((this.core != null) && (!isCloseAreadyInProgress))
/*     */     {
/* 601 */       if (isForRestart)
/*     */       {
/* 603 */         this.core.checkRestartSupported();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/*     */       try
/*     */       {
/* 612 */         UIFunctionsManager.getUIFunctions().getUIUpdater().stopIt();
/*     */       } catch (Exception e) {
/* 614 */         Debug.out(e);
/*     */       }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 653 */         if ((this.core != null) && (!isCloseAreadyInProgress)) {
/*     */           try
/*     */           {
/* 656 */             if (isForRestart)
/*     */             {
/* 658 */               this.core.restart();
/*     */             }
/*     */             else
/*     */             {
/* 662 */               long lStopStarted = System.currentTimeMillis();
/* 663 */               System.out.println("core.stop");
/* 664 */               this.core.stop();
/* 665 */               System.out.println("core.stop done in " + (System.currentTimeMillis() - lStopStarted));
/*     */             }
/*     */             
/*     */ 
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 672 */             Debug.out(e);
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/* 681 */         if (this.startServer != null) {
/* 682 */           this.startServer.stopIt();
/*     */         }
/*     */       }
/*     */       
/* 686 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 688 */           SWTThread.getInstance().terminate();
/*     */         }
/*     */       });
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 653 */         if ((this.core != null) && (!isCloseAreadyInProgress)) {
/*     */           try
/*     */           {
/* 656 */             if (isForRestart)
/*     */             {
/* 658 */               this.core.restart();
/*     */             }
/*     */             else
/*     */             {
/* 662 */               long lStopStarted = System.currentTimeMillis();
/* 663 */               System.out.println("core.stop");
/* 664 */               this.core.stop();
/* 665 */               System.out.println("core.stop done in " + (System.currentTimeMillis() - lStopStarted));
/*     */             }
/*     */             
/*     */ 
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 672 */             Debug.out(e);
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/* 681 */         if (this.startServer != null) {
/* 682 */           this.startServer.stopIt();
/*     */         }
/*     */       }
/*     */       
/* 686 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 688 */           SWTThread.getInstance().terminate();
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public void addListener(InitializerListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 698 */       this.listeners_mon.enter();
/*     */       
/* 700 */       this.listeners.add(listener);
/*     */     }
/*     */     finally {
/* 703 */       this.listeners_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeListener(InitializerListener listener)
/*     */   {
/*     */     try {
/* 710 */       this.listeners_mon.enter();
/*     */       
/* 712 */       this.listeners.remove(listener);
/*     */     }
/*     */     finally {
/* 715 */       this.listeners_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void reportCurrentTask(String currentTaskString) {
/*     */     try {
/* 721 */       this.listeners_mon.enter();
/*     */       
/* 723 */       Iterator iter = this.listeners.iterator();
/* 724 */       while (iter.hasNext()) {
/* 725 */         InitializerListener listener = (InitializerListener)iter.next();
/*     */         try {
/* 727 */           listener.reportCurrentTask(currentTaskString);
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 734 */       this.listeners_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   private void reportCurrentTaskByKey(String key) {
/* 739 */     reportCurrentTask(MessageText.getString(key));
/*     */   }
/*     */   
/*     */   public void increaseProgress() {
/* 743 */     if (this.curPercent < 100) {
/* 744 */       reportPercent(this.curPercent + 1);
/*     */     }
/*     */   }
/*     */   
/*     */   public void abortProgress()
/*     */   {
/* 750 */     reportPercent(101);
/*     */   }
/*     */   
/*     */   public void reportPercent(int percent) {
/* 754 */     if (this.curPercent > percent) {
/* 755 */       return;
/*     */     }
/*     */     
/* 758 */     this.curPercent = percent;
/*     */     try {
/* 760 */       this.listeners_mon.enter();
/*     */       
/* 762 */       Iterator iter = this.listeners.iterator();
/* 763 */       while (iter.hasNext()) {
/* 764 */         InitializerListener listener = (InitializerListener)iter.next();
/*     */         try {
/* 766 */           listener.reportPercent(percent);
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */       
/*     */ 
/* 772 */       if (percent > 100) {
/* 773 */         this.listeners.clear();
/*     */       }
/*     */     }
/*     */     finally {
/* 777 */       this.listeners_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void initializationComplete()
/*     */   {
/* 784 */     this.core.getPluginManager().firePluginEvent(6);
/*     */     
/*     */ 
/*     */ 
/* 788 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 790 */         new DelayedEvent("SWTInitComplete:delay", 500L, new AERunnable()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 808 */             Initializer.this.init_task.release();
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initializePlatformClientMessageContext()
/*     */   {
/* 821 */     ClientMessageContext clientMsgContext = PlatformMessenger.getClientMessageContext();
/* 822 */     if (clientMsgContext != null) {
/* 823 */       clientMsgContext.setMessageDispatcher(new MessageDispatcherSWT(clientMsgContext));
/* 824 */       clientMsgContext.addMessageListener(new TorrentListener());
/* 825 */       clientMsgContext.addMessageListener(new VuzeListener());
/* 826 */       clientMsgContext.addMessageListener(new DisplayListener(null));
/* 827 */       clientMsgContext.addMessageListener(new ConfigListener(null));
/*     */     }
/* 829 */     PluginInitializer.getDefaultInterface().addEventListener(new PluginEventListener() {
/*     */       public void handleEvent(PluginEvent ev) {
/*     */         try {
/* 832 */           int type = ev.getType();
/* 833 */           String event = null;
/* 834 */           if (type == 10) {
/* 835 */             event = "installed";
/* 836 */           } else if (type == 12) {
/* 837 */             event = "uninstalled";
/*     */           }
/* 839 */           if ((event != null) && ((ev.getValue() instanceof String))) {
/* 840 */             PlatformConfigMessenger.logPlugin(event, (String)ev.getValue());
/*     */           }
/*     */         } catch (Exception e) {
/* 843 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean handleStopRestart(boolean restart)
/*     */   {
/* 853 */     UIFunctionsSWT functionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 854 */     if (functionsSWT != null) {
/* 855 */       return functionsSWT.dispose(restart, true);
/*     */     }
/*     */     
/* 858 */     return false;
/*     */   }
/*     */   
/*     */   public static Initializer getLastInitializer() {
/* 862 */     return lastInitializer;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/Initializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */