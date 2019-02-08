/*      */ package com.aelitis.azureus.ui.swt.shells.main;
/*      */ 
/*      */ import com.aelitis.azureus.activities.VuzeActivitiesManager;
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformConfigMessenger;
/*      */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*      */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*      */ import com.aelitis.azureus.ui.IUIIntializer;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.mdi.MdiListener;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.Initializer;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import com.aelitis.azureus.ui.swt.UISkinnableManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.columns.utils.TableColumnCreatorV3;
/*      */ import com.aelitis.azureus.ui.swt.extlistener.StimulusRPC;
/*      */ import com.aelitis.azureus.ui.swt.mdi.BaseMDI;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectButton;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinUtils;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.WelcomeView;
/*      */ import com.aelitis.azureus.util.MapUtils;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.SWT;
/*      */ import org.eclipse.swt.dnd.Clipboard;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.ShellEvent;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.config.impl.ConfigurationChecker;
/*      */ import org.gudy.azureus2.core3.config.impl.ConfigurationDefaults;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.LogRelationUtils;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.ui.swt.Alerts;
/*      */ import org.gudy.azureus2.ui.swt.Alerts.AlertListener;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.PasswordWindow;
/*      */ import org.gudy.azureus2.ui.swt.TextWithHistory;
/*      */ import org.gudy.azureus2.ui.swt.TrayWindow;
/*      */ import org.gudy.azureus2.ui.swt.UIExitUtilsSWT;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.shell.ShellManager;
/*      */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
/*      */ import org.gudy.azureus2.ui.swt.donations.DonationWindow;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.IMainMenu;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.MainStatusBar;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*      */ import org.gudy.azureus2.ui.swt.minibar.AllTransfersBar;
/*      */ import org.gudy.azureus2.ui.swt.minibar.MiniBarManager;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTInstanceImpl;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ import org.gudy.azureus2.ui.systray.SystemTraySWT;
/*      */ 
/*      */ public class MainWindowImpl implements MainWindow, org.gudy.azureus2.ui.swt.debug.ObfusticateShell, MdiListener, org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator, com.aelitis.azureus.ui.mdi.MdiEntryLogIdListener, com.aelitis.azureus.ui.common.updater.UIUpdatable
/*      */ {
/*  117 */   private static final LogIDs LOGID = LogIDs.GUI;
/*      */   
/*      */   private Shell shell;
/*      */   
/*      */   private Display display;
/*      */   
/*      */   private AzureusCore core;
/*      */   
/*      */   private IUIIntializer uiInitializer;
/*      */   
/*      */   private SWTSkin skin;
/*      */   
/*      */   private IMainMenu menu;
/*      */   
/*      */   private UISWTInstanceImpl uiSWTInstanceImpl;
/*      */   
/*      */   private UIFunctionsImpl uiFunctions;
/*      */   
/*      */   private SystemTraySWT systemTraySWT;
/*      */   
/*  137 */   private static Map<String, List> mapTrackUsage = null;
/*      */   
/*  139 */   private static final AEMonitor mapTrackUsage_mon = new AEMonitor("mapTrackUsage");
/*      */   
/*      */ 
/*  142 */   private long lCurrentTrackTime = 0L;
/*      */   
/*  144 */   private long lCurrentTrackTimeIdle = 0L;
/*      */   
/*      */   private boolean disposedOrDisposing;
/*      */   
/*      */   private DownloadManager[] dms_Startup;
/*      */   
/*  150 */   private boolean isReady = false;
/*      */   
/*      */   private MainStatusBar statusBar;
/*      */   
/*  154 */   private String lastShellStatus = null;
/*      */   
/*      */ 
/*      */ 
/*      */   private Color colorSearchTextBG;
/*      */   
/*      */ 
/*      */ 
/*      */   private Color colorSearchTextFG;
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean delayedCore;
/*      */   
/*      */ 
/*      */ 
/*      */   private TrayWindow downloadBasket;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected MainWindowImpl(AzureusCore core, Display display, final IUIIntializer uiInitializer)
/*      */   {
/*  177 */     this.delayedCore = false;
/*  178 */     this.core = core;
/*  179 */     this.display = display;
/*  180 */     this.uiInitializer = uiInitializer;
/*  181 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  183 */     this.disposedOrDisposing = false;
/*      */     
/*  185 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*      */         try {
/*  188 */           MainWindowImpl.this.createWindow(uiInitializer);
/*      */         } catch (Throwable e) {
/*  190 */           Logger.log(new LogAlert(false, "Error Initialize MainWindow", e));
/*      */         }
/*  192 */         if (uiInitializer != null) {
/*  193 */           uiInitializer.abortProgress();
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/*  199 */     });
/*  200 */     GlobalManager gm = core.getGlobalManager();
/*  201 */     this.dms_Startup = ((DownloadManager[])gm.getDownloadManagers().toArray(new DownloadManager[0]));
/*      */     
/*  203 */     gm.addListener(new GlobalManagerListener()
/*      */     {
/*      */       public void seedingStatusChanged(boolean seeding_only_mode, boolean b) {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void downloadManagerRemoved(DownloadManager dm) {}
/*      */       
/*      */ 
/*  212 */       public void downloadManagerAdded(DownloadManager dm) { MainWindowImpl.this.downloadAdded(new DownloadManager[] { dm }); } public void destroyed() {} public void destroyInitiated() {} }, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  225 */     Alerts.addListener(new Alerts.AlertListener()
/*      */     {
/*      */       public boolean allowPopup(Object[] relatedObjects, int configID) {
/*  228 */         DownloadManager dm = (DownloadManager)LogRelationUtils.queryForClass(relatedObjects, DownloadManager.class);
/*      */         
/*      */ 
/*  231 */         if (dm == null) {
/*  232 */           return true;
/*      */         }
/*  234 */         if (dm.getDownloadState().getFlag(16L)) {
/*  235 */           return false;
/*      */         }
/*      */         
/*  238 */         return true;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected MainWindowImpl(final Display display, final IUIIntializer uiInitializer)
/*      */   {
/*  259 */     this.delayedCore = true;
/*  260 */     this.display = display;
/*  261 */     this.uiInitializer = uiInitializer;
/*  262 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  264 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*      */         try {
/*  268 */           MainWindowImpl.this.createWindow(uiInitializer);
/*      */         } catch (Throwable e) {
/*  270 */           Logger.log(new LogAlert(false, "Error Initialize MainWindow", e));
/*      */         }
/*      */         
/*  273 */         while ((!display.isDisposed()) && (display.readAndDispatch())) {}
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void init(final AzureusCore core)
/*      */   {
/*  285 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*  288 */         MainWindowImpl.this._init(core);
/*  289 */         if (MainWindowImpl.this.uiInitializer != null) {
/*  290 */           MainWindowImpl.this.uiInitializer.abortProgress();
/*      */         }
/*      */       }
/*  293 */     });
/*  294 */     com.aelitis.azureus.ui.swt.uiupdater.UIUpdaterSWT.getInstance().addUpdater(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void _init(AzureusCore core)
/*      */   {
/*  302 */     this.core = core;
/*      */     
/*  304 */     this.disposedOrDisposing = false;
/*      */     
/*  306 */     StimulusRPC.hookListeners(core, this);
/*      */     
/*  308 */     if (this.uiSWTInstanceImpl == null) {
/*  309 */       this.uiSWTInstanceImpl = new UISWTInstanceImpl();
/*  310 */       this.uiSWTInstanceImpl.init(this.uiInitializer);
/*      */     }
/*      */     
/*  313 */     postPluginSetup(core);
/*      */     
/*      */ 
/*      */ 
/*  317 */     GlobalManager gm = core.getGlobalManager();
/*  318 */     this.dms_Startup = ((DownloadManager[])gm.getDownloadManagers().toArray(new DownloadManager[0]));
/*      */     
/*  320 */     gm.addListener(new GlobalManagerListener()
/*      */     {
/*      */       public void seedingStatusChanged(boolean seeding_only_mode, boolean b) {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void downloadManagerRemoved(DownloadManager dm) {}
/*      */       
/*      */ 
/*  329 */       public void downloadManagerAdded(DownloadManager dm) { MainWindowImpl.this.downloadAdded(new DownloadManager[] { dm }); } public void destroyed() {} public void destroyInitiated() {} }, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  342 */     Alerts.addListener(new Alerts.AlertListener()
/*      */     {
/*      */       public boolean allowPopup(Object[] relatedObjects, int configID) {
/*  345 */         DownloadManager dm = (DownloadManager)LogRelationUtils.queryForClass(relatedObjects, DownloadManager.class);
/*      */         
/*      */ 
/*  348 */         if (dm == null) {
/*  349 */           return true;
/*      */         }
/*  351 */         if (dm.getDownloadState().getFlag(16L)) {
/*  352 */           return false;
/*      */         }
/*      */         
/*  355 */         return true;
/*      */       }
/*      */       
/*      */ 
/*  359 */     });
/*  360 */     core.triggerLifeCycleComponentCreated(this.uiFunctions);
/*      */     
/*  362 */     processStartupDMS();
/*      */   }
/*      */   
/*      */   private void postPluginSetup(AzureusCore core)
/*      */   {
/*  367 */     if (core == null) {
/*  368 */       return;
/*      */     }
/*      */     
/*      */ 
/*  372 */     VuzeActivitiesManager.initialize(core);
/*      */     
/*      */ 
/*  375 */     org.gudy.azureus2.ui.swt.views.utils.LocProvUtils.initialise(core);
/*      */     
/*  377 */     if (!Constants.isSafeMode)
/*      */     {
/*      */ 
/*  380 */       COConfigurationManager.removeParameter("GUI_SWT_share_count_at_close");
/*      */       
/*  382 */       MainHelpers.initTransferBar();
/*      */       
/*  384 */       COConfigurationManager.addAndFireParameterListener("IconBar.enabled", new ParameterListener()
/*      */       {
/*      */         public void parameterChanged(String parameterName) {
/*  387 */           MainWindowImpl.this.setVisible(2, COConfigurationManager.getBooleanParameter(parameterName));
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  393 */     new org.gudy.azureus2.ui.swt.sharing.progress.ProgressWindow(this.display);
/*      */   }
/*      */   
/*      */ 
/*      */   private void processStartupDMS()
/*      */   {
/*  399 */     AEThread2 thread = new AEThread2("v3.mw.dmAdded", true) {
/*      */       public void run() {
/*  401 */         long startTime = SystemTime.getCurrentTime();
/*  402 */         if ((MainWindowImpl.this.dms_Startup == null) || (MainWindowImpl.this.dms_Startup.length == 0)) {
/*  403 */           MainWindowImpl.this.dms_Startup = null;
/*  404 */           return;
/*      */         }
/*      */         
/*  407 */         MainWindowImpl.this.downloadAdded(MainWindowImpl.this.dms_Startup);
/*      */         
/*  409 */         MainWindowImpl.this.dms_Startup = null;
/*      */         
/*  411 */         System.out.println("psDMS " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       }
/*      */       
/*  414 */     };
/*  415 */     thread.setPriority(1);
/*  416 */     thread.start();
/*      */   }
/*      */   
/*      */   private void downloadAdded(DownloadManager[] dms) {
/*  420 */     boolean oneIsNotPlatformAndPersistent = false;
/*  421 */     for (final DownloadManager dm : dms) {
/*  422 */       if (dm != null)
/*      */       {
/*      */ 
/*      */ 
/*  426 */         DownloadManagerState dmState = dm.getDownloadState();
/*      */         
/*  428 */         TOTorrent torrent = dm.getTorrent();
/*  429 */         if (torrent != null)
/*      */         {
/*      */ 
/*      */ 
/*  433 */           int pfi = PlatformTorrentUtils.getContentPrimaryFileIndex(torrent);
/*      */           
/*  435 */           if (pfi >= 0) {
/*  436 */             dmState.setIntAttribute("primaryfileidx", pfi);
/*      */           }
/*      */           
/*  439 */           if ((ConfigurationChecker.isNewVersion()) && (dm.getAssumedComplete())) {
/*  440 */             String lastVersion = COConfigurationManager.getStringParameter("Last Version");
/*  441 */             if (Constants.compareVersions(lastVersion, "3.1.1.1") <= 0)
/*      */             {
/*  443 */               long completedTime = dmState.getLongParameter("stats.download.completed.time");
/*  444 */               if (completedTime < SystemTime.getOffsetTime(-60000L)) {
/*  445 */                 PlatformTorrentUtils.setHasBeenOpened(dm, true);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  450 */           boolean isContent = (PlatformTorrentUtils.isContent(torrent, true)) || (PlatformTorrentUtils.getContentNetworkID(torrent) == 3L);
/*      */           
/*      */ 
/*  453 */           if ((!oneIsNotPlatformAndPersistent) && (!isContent) && (!dmState.getFlag(16L)) && (dm.isPersistent()))
/*      */           {
/*  455 */             oneIsNotPlatformAndPersistent = true;
/*      */           }
/*      */           
/*  458 */           if (isContent) {
/*  459 */             long now = SystemTime.getCurrentTime();
/*      */             
/*  461 */             long expiresOn = PlatformTorrentUtils.getExpiresOn(torrent);
/*  462 */             if (expiresOn > now) {
/*  463 */               SimpleTimer.addEvent("dm Expirey", expiresOn, new TimerEventPerformer()
/*      */               {
/*      */                 public void perform(TimerEvent event) {
/*  466 */                   dm.getDownloadState().setFlag(16L, true);
/*      */                   
/*  468 */                   org.gudy.azureus2.ui.swt.views.utils.ManagerUtils.asyncStopDelete(dm, 70, true, true, null);
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  476 */     if ((oneIsNotPlatformAndPersistent) && (this.dms_Startup == null)) {
/*  477 */       DonationWindow.checkForDonationPopup();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void createWindow(IUIIntializer uiInitializer)
/*      */   {
/*  489 */     long startTime = SystemTime.getCurrentTime();
/*      */     
/*  491 */     UIFunctionsSWT existing_uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*      */     
/*  493 */     this.uiFunctions = new UIFunctionsImpl(this);
/*      */     
/*  495 */     UIFunctionsManager.setUIFunctions(this.uiFunctions);
/*      */     
/*  497 */     Utils.disposeComposite(this.shell);
/*      */     
/*  499 */     increaseProgress(uiInitializer, "splash.initializeGui");
/*      */     
/*  501 */     System.out.println("UIFunctions/ImageLoad took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */     
/*  503 */     startTime = SystemTime.getCurrentTime();
/*      */     
/*  505 */     this.shell = (existing_uif == null ? new Shell(this.display, 1264) : existing_uif.getMainShell());
/*      */     
/*  507 */     if (Constants.isWindows) {
/*      */       try {
/*  509 */         Class<?> ehancerClass = Class.forName("org.gudy.azureus2.ui.swt.win32.Win32UIEnhancer");
/*  510 */         Method method = ehancerClass.getMethod("initMainShell", new Class[] { Shell.class });
/*      */         
/*      */ 
/*      */ 
/*  514 */         method.invoke(null, new Object[] { this.shell });
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  518 */         Debug.outNoStack(Debug.getCompressedStackTrace(e, 0, 30), true);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  523 */       this.shell.setData("class", this);
/*  524 */       this.shell.setText(UIFunctions.MAIN_WINDOW_NAME);
/*  525 */       Utils.setShellIcon(this.shell);
/*  526 */       Utils.linkShellMetricsToConfig(this.shell, "window");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  531 */       System.out.println("new shell took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  533 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  535 */       PlatformConfigMessenger.addPlatformLoginCompleteListener(new com.aelitis.azureus.core.messenger.config.PlatformConfigMessenger.PlatformLoginCompleteListener() {
/*      */         public void platformLoginComplete() {
/*  537 */           Utils.execSWTThread(new AERunnable() {
/*      */             public void runSupport() {
/*  539 */               MainWindowImpl.this.setupUsageTracker();
/*      */             }
/*      */             
/*      */           });
/*      */         }
/*  544 */       });
/*  545 */       increaseProgress(uiInitializer, "v3.splash.initSkin");
/*      */       
/*  547 */       this.skin = com.aelitis.azureus.ui.swt.skin.SWTSkinFactory.getInstance();
/*  548 */       if (Utils.isAZ2UI()) {
/*  549 */         SWTSkinProperties skinProperties = this.skin.getSkinProperties();
/*  550 */         String skinPath = "com/aelitis/azureus/ui/skin/skin3_classic";
/*  551 */         ResourceBundle rb = ResourceBundle.getBundle(skinPath);
/*  552 */         skinProperties.addResourceBundle(rb, skinPath);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  558 */       this.uiFunctions.setSkin(this.skin);
/*      */       
/*  560 */       System.out.println("new shell setup took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  562 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  564 */       initSkinListeners();
/*      */       
/*  566 */       increaseProgress(uiInitializer, "v3.splash.initSkin");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  571 */       String startID = Utils.isAZ2UI() ? "classic.shell" : "main.shell";
/*  572 */       this.skin.initialize(this.shell, startID, uiInitializer);
/*      */       
/*  574 */       increaseProgress(uiInitializer, "v3.splash.initSkin");
/*  575 */       System.out.println("skin init took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  577 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  579 */       if (Utils.isAZ2UI()) {
/*  580 */         this.menu = new org.gudy.azureus2.ui.swt.mainwindow.MainMenu(this.shell);
/*      */       } else {
/*  582 */         this.menu = new MainMenu(this.skin, this.shell);
/*      */       }
/*  584 */       this.shell.setData("MainMenu", this.menu);
/*      */       
/*  586 */       System.out.println("MainMenu init took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  588 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  590 */       if (Constants.isOSX) {
/*  591 */         if (Utils.isCarbon) {
/*      */           try
/*      */           {
/*  594 */             Class<?> ehancerClass = Class.forName("org.gudy.azureus2.ui.swt.osx.CarbonUIEnhancer");
/*      */             
/*  596 */             Method method = ehancerClass.getMethod("registerToolbarToggle", new Class[] { Shell.class });
/*      */             
/*      */ 
/*      */ 
/*  600 */             method.invoke(null, new Object[] { this.shell });
/*      */ 
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/*  605 */             Debug.printStackTrace(e);
/*      */           }
/*  607 */         } else if (Utils.isCocoa) {
/*      */           try
/*      */           {
/*  610 */             Class<?> ehancerClass = Class.forName("org.gudy.azureus2.ui.swt.osx.CocoaUIEnhancer");
/*      */             
/*  612 */             Method mGetInstance = ehancerClass.getMethod("getInstance", new Class[0]);
/*      */             
/*  614 */             Object claObj = mGetInstance.invoke(null, new Object[0]);
/*      */             
/*  616 */             Method mregTBToggle = claObj.getClass().getMethod("registerToolbarToggle", new Class[] { Shell.class });
/*      */             
/*      */ 
/*      */ 
/*  620 */             if (mregTBToggle != null) {
/*  621 */               mregTBToggle.invoke(claObj, new Object[] { this.shell });
/*      */             }
/*      */             
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  627 */             if (!Constants.isOSX_10_7_OrHigher) {
/*  628 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  634 */         Listener toggleListener = new Listener() {
/*      */           public void handleEvent(Event event) {
/*  636 */             SWTSkinObject so = MainWindowImpl.this.skin.getSkinObject("global-toolbar");
/*  637 */             if (so != null) {
/*  638 */               so.setVisible(!so.isVisible());
/*      */             }
/*      */           }
/*  641 */         };
/*  642 */         this.shell.addListener(17, toggleListener);
/*  643 */         this.shell.addListener(18, toggleListener);
/*      */         
/*  645 */         System.out.println("createWindow init took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */         
/*  647 */         startTime = SystemTime.getCurrentTime();
/*      */       }
/*      */       
/*      */ 
/*  651 */       increaseProgress(uiInitializer, "v3.splash.initSkin");
/*      */       
/*  653 */       this.skin.layout();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  660 */         Utils.createTorrentDropTarget(this.shell, false);
/*      */       } catch (Throwable e) {
/*  662 */         Logger.log(new LogEvent(LOGID, "Drag and Drop not available", e));
/*      */       }
/*      */       
/*  665 */       this.shell.addDisposeListener(new DisposeListener() {
/*      */         public void widgetDisposed(DisposeEvent e) {
/*  667 */           MainWindowImpl.this.dispose(false, false);
/*      */         }
/*      */         
/*  670 */       });
/*  671 */       this.shell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
/*      */         public void shellClosed(ShellEvent event) {
/*  673 */           if (MainWindowImpl.this.disposedOrDisposing) {
/*  674 */             return;
/*      */           }
/*  676 */           if ((MainWindowImpl.this.systemTraySWT != null) && (COConfigurationManager.getBooleanParameter("Enable System Tray")) && (COConfigurationManager.getBooleanParameter("Close To Tray")))
/*      */           {
/*      */ 
/*      */ 
/*  680 */             MainWindowImpl.this.minimizeToTray(event);
/*      */           } else {
/*  682 */             event.doit = MainWindowImpl.this.dispose(false, false);
/*      */           }
/*      */         }
/*      */         
/*      */         public void shellActivated(ShellEvent e) {
/*  687 */           Shell shellAppModal = Utils.findFirstShellWithStyle(65536);
/*  688 */           if (shellAppModal != null) {
/*  689 */             shellAppModal.forceActive();
/*      */           } else {
/*  691 */             MainWindowImpl.this.shell.forceActive();
/*      */           }
/*      */         }
/*      */         
/*      */         public void shellIconified(ShellEvent event) {
/*  696 */           if (MainWindowImpl.this.disposedOrDisposing) {
/*  697 */             return;
/*      */           }
/*  699 */           if ((MainWindowImpl.this.systemTraySWT != null) && (COConfigurationManager.getBooleanParameter("Enable System Tray")) && (COConfigurationManager.getBooleanParameter("Minimize To Tray")))
/*      */           {
/*      */ 
/*      */ 
/*  703 */             MainWindowImpl.this.minimizeToTray(event);
/*      */           }
/*      */         }
/*      */         
/*      */         public void shellDeiconified(ShellEvent e) {
/*  708 */           if ((Constants.isOSX) && (COConfigurationManager.getBooleanParameter("Password enabled")))
/*      */           {
/*  710 */             MainWindowImpl.this.shell.setVisible(false);
/*  711 */             if (PasswordWindow.showPasswordWindow(MainWindowImpl.this.display)) {
/*  712 */               MainWindowImpl.this.shell.setVisible(true);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*  717 */       });
/*  718 */       this.display.addFilter(1, new Listener()
/*      */       {
/*      */         public void handleEvent(Event event) {
/*  721 */           Control focus_control = MainWindowImpl.this.display.getFocusControl();
/*  722 */           if ((focus_control != null) && (focus_control.getShell() != MainWindowImpl.this.shell)) {
/*  723 */             return;
/*      */           }
/*  725 */           int key = event.character;
/*  726 */           if (((event.stateMask & SWT.MOD1) != 0) && (event.character <= '\032') && (event.character > 0))
/*      */           {
/*  728 */             key += 96;
/*      */           }
/*  730 */           if ((key == 108) && ((event.stateMask & SWT.MOD1) != 0))
/*      */           {
/*  732 */             if (MainWindowImpl.this.core == null) {
/*  733 */               return;
/*      */             }
/*  735 */             GlobalManager gm = MainWindowImpl.this.core.getGlobalManager();
/*  736 */             if (gm != null) {
/*  737 */               UIFunctionsManagerSWT.getUIFunctionsSWT().openTorrentWindow();
/*  738 */               event.doit = false;
/*      */             }
/*      */           }
/*  741 */           else if ((key == 100) && ((event.stateMask & SWT.MOD1) != 0))
/*      */           {
/*      */ 
/*  744 */             if (Constants.isCVSVersion())
/*      */             {
/*  746 */               Utils.dump(MainWindowImpl.this.shell);
/*      */             }
/*  748 */           } else if ((key == 102) && ((event.stateMask & SWT.MOD1 + 131072) == SWT.MOD1 + 131072))
/*      */           {
/*      */ 
/*  751 */             MainWindowImpl.this.shell.setFullScreen(!MainWindowImpl.this.shell.getFullScreen());
/*      */           }
/*      */           
/*      */         }
/*  755 */       });
/*  756 */       increaseProgress(uiInitializer, "v3.splash.initSkin");
/*  757 */       System.out.println("pre skin widgets init took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  759 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  761 */       if (this.core != null) {
/*  762 */         StimulusRPC.hookListeners(this.core, this);
/*      */       }
/*      */       
/*  765 */       increaseProgress(uiInitializer, "v3.splash.initSkin");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  770 */       initMDI();
/*  771 */       System.out.println("skin widgets (1/2) init took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  773 */       startTime = SystemTime.getCurrentTime();
/*  774 */       initWidgets2();
/*      */       
/*  776 */       increaseProgress(uiInitializer, "v3.splash.initSkin");
/*  777 */       System.out.println("skin widgets (2/2) init took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  779 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  781 */       System.out.println("pre SWTInstance init took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  783 */       increaseProgress(uiInitializer, "v3.splash.hookPluginUI");
/*  784 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  786 */       TableColumnCreatorV3.initCoreColumns();
/*      */       
/*  788 */       System.out.println("Init Core Columns took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  790 */       increaseProgress(uiInitializer, "v3.splash.hookPluginUI");
/*  791 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  796 */       this.uiSWTInstanceImpl = new UISWTInstanceImpl();
/*  797 */       this.uiSWTInstanceImpl.init(uiInitializer);
/*      */       
/*  799 */       System.out.println("SWTInstance init took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  801 */       increaseProgress(uiInitializer, "splash.initializeGui");
/*  802 */       startTime = SystemTime.getCurrentTime();
/*      */     } catch (Throwable t) {
/*      */       String configID;
/*  805 */       Debug.out(t);
/*      */     } finally {
/*      */       String configID;
/*  808 */       String configID = "pluginbar.visible";
/*  809 */       if (!ConfigurationDefaults.getInstance().doesParameterDefaultExist(configID))
/*      */       {
/*  811 */         COConfigurationManager.setBooleanDefault(configID, true);
/*      */       }
/*  813 */       setVisible(4, (COConfigurationManager.getBooleanParameter(configID)) && (COConfigurationManager.getIntParameter("User Mode") > 1));
/*      */       
/*      */ 
/*      */ 
/*  817 */       setVisible(2, COConfigurationManager.getBooleanParameter("IconBar.enabled"));
/*      */       
/*      */ 
/*  820 */       this.shell.layout(true, true);
/*      */       
/*  822 */       System.out.println("shell.layout took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  824 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  826 */       showMainWindow();
/*      */       
/*      */ 
/*      */ 
/*  830 */       increaseProgress(uiInitializer, "splash.initializeGui");
/*      */       
/*  832 */       System.out.println("shell.open took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  834 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  836 */       processStartupDMS();
/*      */       
/*  838 */       System.out.println("processStartupDMS took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  840 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  842 */       if (this.core != null) {
/*  843 */         postPluginSetup(this.core);
/*      */       }
/*      */       
/*  846 */       System.out.println("postPluginSetup init took " + (SystemTime.getCurrentTime() - startTime) + "ms");
/*      */       
/*  848 */       startTime = SystemTime.getCurrentTime();
/*      */       
/*  850 */       com.aelitis.azureus.util.NavigationHelper.addListener(new com.aelitis.azureus.util.NavigationHelper.navigationListener() {
/*      */         public void processCommand(final int type, final String[] args) {
/*  852 */           Utils.execSWTThread(new AERunnable()
/*      */           {
/*      */             public void runSupport() {
/*  855 */               UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*      */               
/*  857 */               if (type == 1) {
/*  858 */                 MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  859 */                 if (mdi == null) {
/*  860 */                   return;
/*      */                 }
/*  862 */                 mdi.showEntryByID(args[0]);
/*      */                 
/*  864 */                 if (uif != null)
/*      */                 {
/*  866 */                   uif.bringToFront();
/*      */                 }
/*  868 */               } else if (type != 2) {}
/*      */             }
/*      */           });
/*      */         }
/*      */       });
/*      */       
/*      */ 
/*  875 */       if (!Constants.isOSX)
/*      */       {
/*  877 */         COConfigurationManager.addAndFireParameterListener("Show Status In Window Title", new ParameterListener()
/*      */         {
/*      */           private TimerEventPeriodic timer;
/*      */           
/*      */ 
/*      */           private String old_text;
/*      */           
/*      */           private String my_last_text;
/*      */           
/*      */ 
/*      */           public void parameterChanged(final String name)
/*      */           {
/*  889 */             Utils.execSWTThread(new AERunnable()
/*      */             {
/*      */ 
/*      */               public void runSupport()
/*      */               {
/*      */ 
/*  895 */                 boolean enable = COConfigurationManager.getBooleanParameter(name);
/*      */                 
/*  897 */                 if (enable)
/*      */                 {
/*  899 */                   if (MainWindowImpl.17.this.timer == null)
/*      */                   {
/*  901 */                     MainWindowImpl.17.this.timer = SimpleTimer.addPeriodicEvent("window.title.updater", 1000L, new TimerEventPerformer()
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/*      */                       public void perform(TimerEvent event)
/*      */                       {
/*      */ 
/*      */ 
/*  910 */                         Utils.execSWTThread(new AERunnable()
/*      */                         {
/*      */ 
/*      */                           public void runSupport()
/*      */                           {
/*      */ 
/*  916 */                             if (MainWindowImpl.this.shell.isDisposed())
/*      */                             {
/*  918 */                               return;
/*      */                             }
/*      */                             
/*  921 */                             String current_txt = MainWindowImpl.this.shell.getText();
/*      */                             
/*  923 */                             if ((current_txt != null) && (!current_txt.equals(MainWindowImpl.17.this.my_last_text)))
/*      */                             {
/*  925 */                               MainWindowImpl.17.this.old_text = current_txt;
/*      */                             }
/*      */                             
/*  928 */                             String txt = MainWindowImpl.this.getCurrentTitleText();
/*      */                             
/*  930 */                             if (txt != null)
/*      */                             {
/*  932 */                               if (!txt.equals(current_txt))
/*      */                               {
/*  934 */                                 MainWindowImpl.this.shell.setText(txt);
/*      */                               }
/*      */                               
/*  937 */                               MainWindowImpl.17.this.my_last_text = txt;
/*      */                             }
/*      */                           }
/*      */                         });
/*      */                       }
/*      */                     });
/*      */                   }
/*      */                 }
/*      */                 else {
/*  946 */                   if (MainWindowImpl.17.this.timer != null)
/*      */                   {
/*  948 */                     MainWindowImpl.17.this.timer.cancel();
/*      */                     
/*  950 */                     MainWindowImpl.17.this.timer = null;
/*      */                   }
/*      */                   
/*  953 */                   if ((MainWindowImpl.17.this.old_text != null) && (!MainWindowImpl.this.shell.isDisposed()))
/*      */                   {
/*  955 */                     MainWindowImpl.this.shell.setText(MainWindowImpl.17.this.old_text);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*  966 */   private String last_eta_str = null;
/*      */   
/*      */   private long last_eta;
/*      */   private int eta_tick_count;
/*      */   
/*      */   private String getCurrentTitleText()
/*      */   {
/*  973 */     if (this.core == null)
/*      */     {
/*  975 */       return null;
/*      */     }
/*      */     
/*  978 */     GlobalManager gm = this.core.getGlobalManager();
/*      */     
/*  980 */     if (gm == null)
/*      */     {
/*  982 */       return null;
/*      */     }
/*      */     
/*  985 */     GlobalManagerStats stats = gm.getStats();
/*      */     
/*  987 */     int down = stats.getDataReceiveRate() + stats.getProtocolReceiveRate();
/*  988 */     int up = stats.getDataSendRate() + stats.getProtocolSendRate();
/*      */     
/*  990 */     this.eta_tick_count += 1;
/*      */     
/*  992 */     String eta_str = this.last_eta_str;
/*      */     
/*  994 */     if ((eta_str == null) || (this.last_eta < 120L) || (this.eta_tick_count % 10 == 0))
/*      */     {
/*      */ 
/*      */ 
/*  998 */       long min_eta = Long.MAX_VALUE;
/*  999 */       int num_downloading = 0;
/*      */       
/* 1001 */       List<DownloadManager> dms = gm.getDownloadManagers();
/*      */       
/* 1003 */       for (DownloadManager dm : dms)
/*      */       {
/* 1005 */         if (dm.getState() == 50)
/*      */         {
/* 1007 */           num_downloading++;
/*      */           
/* 1009 */           long dm_eta = dm.getStats().getSmoothedETA();
/*      */           
/* 1011 */           if (dm_eta < min_eta)
/*      */           {
/* 1013 */             min_eta = dm_eta;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1018 */       if (min_eta == Long.MAX_VALUE)
/*      */       {
/* 1020 */         min_eta = 1827387392L;
/*      */       }
/*      */       
/* 1023 */       this.last_eta = min_eta;
/*      */       
/* 1025 */       eta_str = this.last_eta_str = num_downloading == 0 ? "" : DisplayFormatters.formatETA(min_eta);
/*      */     }
/*      */     
/*      */ 
/* 1029 */     String down_str = formatRateCompact(down);
/* 1030 */     String up_str = formatRateCompact(up);
/*      */     
/* 1032 */     StringBuilder result = new StringBuilder(50);
/*      */     
/* 1034 */     result.append(MessageText.getString("ConfigView.download.abbreviated"));
/* 1035 */     result.append(" ");
/* 1036 */     result.append(down_str);
/* 1037 */     result.append(" ");
/* 1038 */     result.append(MessageText.getString("ConfigView.upload.abbreviated"));
/* 1039 */     result.append(" ");
/* 1040 */     result.append(up_str);
/*      */     
/* 1042 */     if (eta_str.length() > 0)
/*      */     {
/* 1044 */       result.append(" ");
/* 1045 */       result.append(MessageText.getString("ConfigView.eta.abbreviated"));
/* 1046 */       result.append(" ");
/* 1047 */       result.append(eta_str);
/*      */     }
/*      */     
/* 1050 */     return result.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String formatRateCompact(int rate)
/*      */   {
/* 1057 */     String str = DisplayFormatters.formatCustomRate("title.rate", rate);
/*      */     
/* 1059 */     if (str == null)
/*      */     {
/* 1061 */       str = DisplayFormatters.formatByteCountToKiBEtc(rate, false, true, 2, 1);
/*      */       
/* 1063 */       String[] bits = str.split(" ");
/*      */       
/* 1065 */       if (bits.length == 2)
/*      */       {
/* 1067 */         String sep = String.valueOf(DisplayFormatters.getDecimalSeparator());
/*      */         
/* 1069 */         String num = bits[0];
/* 1070 */         String unit = bits[1];
/*      */         
/* 1072 */         int num_len = num.length();
/*      */         
/* 1074 */         if (num_len < 4)
/*      */         {
/* 1076 */           if (!num.contains(sep))
/*      */           {
/* 1078 */             num = num + sep;
/*      */             
/* 1080 */             num_len++;
/*      */           }
/*      */           
/* 1083 */           while (num_len < 4)
/*      */           {
/* 1085 */             num = num + "0";
/*      */             
/* 1087 */             num_len++;
/*      */           }
/*      */         }
/* 1090 */         if (num_len > 4)
/*      */         {
/* 1092 */           num = num.substring(0, 4);
/*      */           
/* 1094 */           num_len = 4;
/*      */         }
/*      */         
/*      */ 
/* 1098 */         if (num.endsWith(sep))
/*      */         {
/* 1100 */           num = num.substring(0, num_len - 1) + " ";
/*      */         }
/*      */         
/* 1103 */         str = num + " " + unit.charAt(0);
/*      */       }
/*      */     }
/*      */     
/* 1107 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void increaseProgress(IUIIntializer uiInitializer, String taskKey)
/*      */   {
/* 1117 */     if (uiInitializer != null) {
/* 1118 */       uiInitializer.increaseProgress();
/* 1119 */       if (taskKey != null) {
/* 1120 */         uiInitializer.reportCurrentTask(MessageText.getString(taskKey));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean dispose(final boolean for_restart, final boolean close_already_in_progress)
/*      */   {
/* 1142 */     if (this.disposedOrDisposing) {
/* 1143 */       return true;
/*      */     }
/* 1145 */     Boolean b = Utils.execSWTThreadWithBool("v3.MainWindow.dispose", new org.gudy.azureus2.core3.util.AERunnableBoolean()
/*      */     {
/*      */       public boolean runSupport() {
/* 1148 */         return MainWindowImpl.this._dispose(for_restart, close_already_in_progress);
/*      */       }
/* 1150 */     });
/* 1151 */     return (b == null) || (b.booleanValue());
/*      */   }
/*      */   
/*      */   private boolean _dispose(final boolean bForRestart, boolean bCloseAlreadyInProgress) {
/* 1155 */     if (this.disposedOrDisposing) {
/* 1156 */       return true;
/*      */     }
/*      */     
/* 1159 */     this.disposedOrDisposing = true;
/* 1160 */     if ((this.core != null) && (!UIExitUtilsSWT.canClose(this.core.getGlobalManager(), bForRestart)))
/*      */     {
/* 1162 */       this.disposedOrDisposing = false;
/* 1163 */       return false;
/*      */     }
/*      */     
/* 1166 */     this.isReady = false;
/*      */     
/* 1168 */     UIExitUtilsSWT.uiShutdown();
/*      */     
/* 1170 */     if (this.systemTraySWT != null) {
/* 1171 */       this.systemTraySWT.dispose();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1181 */       if (this.core != null) {
/* 1182 */         AllTransfersBar transfer_bar = AllTransfersBar.getBarIfOpen(this.core.getGlobalManager());
/* 1183 */         if (transfer_bar != null) {
/* 1184 */           transfer_bar.forceSaveLocation();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception ignore) {}
/*      */     
/* 1190 */     mapTrackUsage_mon.enter();
/*      */     try {
/* 1192 */       if (mapTrackUsage != null) {
/* 1193 */         String id = getUsageActiveTabID();
/* 1194 */         if (id != null) {
/* 1195 */           if (this.lastShellStatus == null) {
/* 1196 */             this.lastShellStatus = id;
/*      */           }
/* 1198 */           updateMapTrackUsage(this.lastShellStatus);
/*      */         }
/*      */         
/* 1201 */         Map<String, Object> map = new HashMap();
/* 1202 */         map.put("version", "5.7.6.0");
/*      */         
/* 1204 */         map.put("statsmap", mapTrackUsage);
/*      */         
/* 1206 */         FileUtil.writeResilientFile(new File(SystemProperties.getUserPath(), "timingstats.dat"), map);
/*      */       }
/*      */     }
/*      */     finally {
/* 1210 */       mapTrackUsage_mon.exit();
/*      */     }
/*      */     
/* 1213 */     if (!SWTThread.getInstance().isTerminated()) {
/* 1214 */       Utils.getOffOfSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/* 1216 */           if (!SWTThread.getInstance().isTerminated()) {
/* 1217 */             SWTThread.getInstance().getInitializer().stopIt(bForRestart, false);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1223 */     return true;
/*      */   }
/*      */   
/*      */   private String getUsageActiveTabID() {
/*      */     try {
/* 1228 */       MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 1229 */       if (mdi != null) {
/* 1230 */         MdiEntry curEntry = mdi.getCurrentEntry();
/* 1231 */         if (curEntry == null) {
/* 1232 */           return "none";
/*      */         }
/* 1234 */         String id = curEntry.getLogID();
/* 1235 */         return id == null ? "null" : id;
/*      */       }
/*      */     } catch (Exception e) {
/* 1238 */       String name = e.getClass().getName();
/* 1239 */       int i = name.indexOf('.');
/* 1240 */       if (i > 0) {
/* 1241 */         return name.substring(i);
/*      */       }
/* 1243 */       return name;
/*      */     }
/* 1245 */     return "unknown";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setupUsageTracker()
/*      */   {
/* 1252 */     mapTrackUsage_mon.enter();
/*      */     try {
/* 1254 */       File f = new File(SystemProperties.getUserPath(), "timingstats.dat");
/*      */       
/* 1256 */       if ((COConfigurationManager.getBooleanParameter("Send Version Info")) && (PlatformConfigMessenger.allowSendStats()))
/*      */       {
/*      */ 
/* 1259 */         mapTrackUsage = new HashMap();
/*      */         
/* 1261 */         if (f.exists()) {
/* 1262 */           Map<?, ?> oldMapTrackUsage = FileUtil.readResilientFile(f);
/* 1263 */           String version = MapUtils.getMapString(oldMapTrackUsage, "version", null);
/*      */           
/* 1265 */           Map<?, ?> map = MapUtils.getMapMap(oldMapTrackUsage, "statsmap", null);
/* 1266 */           if ((version != null) && (map != null)) {
/* 1267 */             PlatformConfigMessenger.sendUsageStats(map, f.lastModified(), version, null);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1272 */         SimpleTimer.addPeriodicEvent("UsageTracker", 1000L, new TimerEventPerformer()
/*      */         {
/* 1274 */           long lLastMouseMove = SystemTime.getCurrentTime();
/*      */           
/* 1276 */           Point ptLastMousePos = new Point(0, 0);
/*      */           
/*      */           public void perform(TimerEvent event) {
/* 1279 */             Utils.execSWTThread(new AERunnable() {
/*      */               public void runSupport() {
/* 1281 */                 if ((MainWindowImpl.this.shell == null) || (MainWindowImpl.this.shell.isDisposed()) || (MainWindowImpl.this.shell.getDisplay().getActiveShell() == null))
/*      */                 {
/*      */ 
/*      */ 
/* 1285 */                   if (MainWindowImpl.20.this.ptLastMousePos.x > 0) {
/* 1286 */                     MainWindowImpl.20.this.ptLastMousePos.x = 0;
/* 1287 */                     MainWindowImpl.20.this.ptLastMousePos.y = 0;
/* 1288 */                     MainWindowImpl.20.this.lLastMouseMove = 0L;
/*      */                   }
/* 1290 */                   return;
/*      */                 }
/*      */                 
/* 1293 */                 Point pt = MainWindowImpl.this.shell.getDisplay().getCursorLocation();
/* 1294 */                 if (pt.equals(MainWindowImpl.20.this.ptLastMousePos)) {
/* 1295 */                   return;
/*      */                 }
/* 1297 */                 MainWindowImpl.20.this.ptLastMousePos = pt;
/*      */                 
/* 1299 */                 long now = SystemTime.getCurrentTime();
/* 1300 */                 if (MainWindowImpl.20.this.lLastMouseMove > 0L) {
/* 1301 */                   long diff = now - MainWindowImpl.20.this.lLastMouseMove;
/* 1302 */                   if (diff < 10000L) {
/* 1303 */                     MainWindowImpl.access$1814(MainWindowImpl.this, diff);
/*      */                   } else {
/* 1305 */                     MainWindowImpl.access$1914(MainWindowImpl.this, diff);
/*      */                   }
/*      */                 }
/*      */                 
/* 1309 */                 MainWindowImpl.20.this.lLastMouseMove = now;
/*      */               }
/*      */               
/*      */             });
/*      */           }
/* 1314 */         });
/* 1315 */         Listener lActivateDeactivate = new Listener() {
/*      */           long start;
/*      */           
/*      */           public void handleEvent(Event event) {
/* 1319 */             if (event.type == 26) {
/* 1320 */               MainWindowImpl.this.lCurrentTrackTimeIdle = 0L;
/* 1321 */               if ((this.start > 0L) && (MainWindowImpl.this.lastShellStatus != null)) {
/* 1322 */                 MainWindowImpl.this.lCurrentTrackTime = (SystemTime.getCurrentTime() - this.start);
/* 1323 */                 MainWindowImpl.this.updateMapTrackUsage(MainWindowImpl.this.lastShellStatus);
/*      */               }
/* 1325 */               MainWindowImpl.this.lastShellStatus = null;
/*      */             } else {
/* 1327 */               MainWindowImpl.this.updateMapTrackUsage(MainWindowImpl.access$2200(MainWindowImpl.this));
/* 1328 */               if (MainWindowImpl.this.shell.getMinimized()) {
/* 1329 */                 MainWindowImpl.this.lastShellStatus = "idle-minimized";
/* 1330 */               } else if (!MainWindowImpl.this.shell.isVisible()) {
/* 1331 */                 MainWindowImpl.this.lastShellStatus = "idle-invisible";
/*      */               } else {
/* 1333 */                 MainWindowImpl.this.lastShellStatus = "idle-nofocus";
/*      */               }
/* 1335 */               this.start = SystemTime.getCurrentTime();
/*      */             }
/*      */           }
/* 1338 */         };
/* 1339 */         this.shell.addListener(26, lActivateDeactivate);
/* 1340 */         this.shell.addListener(27, lActivateDeactivate);
/*      */       }
/*      */       else {
/* 1343 */         mapTrackUsage = null;
/*      */         try
/*      */         {
/* 1346 */           if (f.exists()) {
/* 1347 */             f.delete();
/*      */           }
/*      */         }
/*      */         catch (Exception e) {}
/*      */       }
/*      */     } catch (Exception e) {
/* 1353 */       Debug.out(e);
/*      */     } finally {
/* 1355 */       mapTrackUsage_mon.exit();
/*      */     }
/*      */   }
/*      */   
/* 1359 */   private Set<Shell> minimized_on_hide = new java.util.HashSet();
/*      */   
/*      */   private void showMainWindow() {
/* 1362 */     COConfigurationManager.addAndFireParameterListener("Show Download Basket", new ParameterListener() {
/*      */       public void parameterChanged(String parameterName) {
/* 1364 */         MainWindowImpl.this.configureDownloadBasket();
/*      */       }
/*      */       
/* 1367 */     });
/* 1368 */     boolean isOSX = Constants.isOSX;
/* 1369 */     boolean bEnableTray = COConfigurationManager.getBooleanParameter("Enable System Tray");
/* 1370 */     boolean bPassworded = COConfigurationManager.getBooleanParameter("Password enabled");
/* 1371 */     boolean bStartMinimize = (bEnableTray) && ((bPassworded) || (COConfigurationManager.getBooleanParameter("Start Minimized")));
/*      */     
/*      */ 
/* 1374 */     SWTSkinObject soMain = this.skin.getSkinObject("main");
/* 1375 */     if (soMain != null) {
/* 1376 */       soMain.getControl().setVisible(true);
/*      */     }
/*      */     
/* 1379 */     this.shell.addListener(22, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1381 */         System.out.println("---------SHOWN AT " + SystemTime.getCurrentTime() + ";" + (SystemTime.getCurrentTime() - Initializer.startTime) + "ms");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1387 */         if (MainWindowImpl.this.statusBar != null) {
/* 1388 */           Utils.execSWTThreadLater(10, new Runnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/* 1395 */               MainWindowImpl.this.statusBar.relayout();
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1402 */         ShellManager.sharedManager().performForShells(new Listener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void handleEvent(Event event)
/*      */           {
/*      */ 
/* 1409 */             Shell this_shell = (Shell)event.widget;
/*      */             
/* 1411 */             if ((this_shell.getParent() == null) && (!this_shell.isVisible()))
/*      */             {
/*      */               boolean minimize;
/*      */               
/* 1415 */               synchronized (MainWindowImpl.this.minimized_on_hide)
/*      */               {
/* 1417 */                 minimize = MainWindowImpl.this.minimized_on_hide.remove(this_shell);
/*      */               }
/*      */               
/* 1420 */               this_shell.setVisible(true);
/*      */               
/* 1422 */               if (minimize)
/*      */               {
/* 1424 */                 this_shell.setMinimized(true);
/*      */               }
/*      */               else
/*      */               {
/* 1428 */                 this_shell.moveAbove(MainWindowImpl.this.shell);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */     
/* 1436 */     if (!bStartMinimize) {
/* 1437 */       this.shell.open();
/* 1438 */       if (!isOSX) {
/* 1439 */         this.shell.forceActive();
/*      */       }
/* 1441 */     } else if (Utils.isCarbon) {
/* 1442 */       this.shell.setVisible(true);
/* 1443 */       this.shell.setMinimized(true);
/*      */     }
/*      */     
/*      */ 
/* 1447 */     if (this.delayedCore)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/* 1452 */         long endSWTDispatchOn = SystemTime.getOffsetTime(5000L);
/*      */         
/* 1454 */         while ((SystemTime.getCurrentTime() < endSWTDispatchOn) && (!this.display.isDisposed()) && (this.display.readAndDispatch())) {}
/*      */       } catch (Exception e) {
/* 1456 */         Debug.out(e);
/*      */       }
/*      */       
/* 1459 */       System.out.println("---------DONE DISPATCH AT " + SystemTime.getCurrentTime() + ";" + (SystemTime.getCurrentTime() - Initializer.startTime) + "ms");
/*      */       
/*      */ 
/* 1462 */       if (this.display.isDisposed()) {
/* 1463 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1467 */     if (bEnableTray)
/*      */     {
/*      */       try {
/* 1470 */         this.systemTraySWT = SystemTraySWT.getTray();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1474 */         e.printStackTrace();
/* 1475 */         Logger.log(new LogEvent(LOGID, 3, "Upgrade to SWT3.0M8 or later for system tray support."));
/*      */       }
/*      */       
/*      */ 
/* 1479 */       if (bStartMinimize) {
/* 1480 */         minimizeToTray(null);
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 1485 */       else if (bPassworded) {
/* 1486 */         minimizeToTray(null);
/* 1487 */         setVisible(true);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1494 */     if (this.uiInitializer != null)
/*      */     {
/* 1496 */       this.uiInitializer.initializationComplete();
/*      */     }
/*      */     
/* 1499 */     boolean run_speed_test = false;
/*      */     
/* 1501 */     if ((!Utils.isAZ2UI()) && (!COConfigurationManager.getBooleanParameter("SpeedTest Completed")))
/*      */     {
/*      */ 
/* 1504 */       if (!ConfigurationChecker.isNewInstall())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1509 */         if (com.aelitis.azureus.core.util.FeatureAvailability.triggerSpeedTestV1())
/*      */         {
/* 1511 */           long upload_limit = COConfigurationManager.getLongParameter("Max Upload Speed KBs");
/* 1512 */           boolean auto_up = COConfigurationManager.getBooleanParameter("Auto Upload Speed Enabled");
/*      */           
/* 1514 */           if (auto_up)
/*      */           {
/* 1516 */             if (upload_limit <= 18L)
/*      */             {
/* 1518 */               run_speed_test = true;
/*      */             }
/*      */           }
/*      */           else {
/* 1522 */             boolean up_seed_limit = COConfigurationManager.getBooleanParameter("enable.seedingonly.upload.rate");
/*      */             
/* 1524 */             if ((upload_limit == 0L) && (!up_seed_limit))
/*      */             {
/* 1526 */               run_speed_test = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1533 */     if (run_speed_test)
/*      */     {
/* 1535 */       org.gudy.azureus2.ui.swt.speedtest.SpeedTestSelector.runMLABTest(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/* 1541 */           WelcomeView.setWaitLoadingURL(false);
/*      */         }
/*      */         
/*      */       });
/*      */     } else {
/* 1546 */       WelcomeView.setWaitLoadingURL(false);
/*      */     }
/*      */     
/* 1549 */     if (Utils.isAZ2UI()) {
/* 1550 */       if (!COConfigurationManager.getBooleanParameter("Wizard Completed"))
/*      */       {
/* 1552 */         org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT.waitForCoreRunning(new com.aelitis.azureus.core.AzureusCoreRunningListener() {
/*      */           public void azureusCoreRunning(AzureusCore core) {
/* 1554 */             new org.gudy.azureus2.ui.swt.config.wizard.ConfigureWizard(false, 0);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1559 */       checkForWhatsNewWindow();
/*      */     }
/*      */     
/* 1562 */     org.gudy.azureus2.ui.swt.associations.AssociationChecker.checkAssociations();
/*      */     
/*      */ 
/* 1565 */     Map<?, ?> map = VersionCheckClient.getSingleton().getMostRecentVersionCheckData();
/* 1566 */     DonationWindow.setInitialAskHours(MapUtils.getMapInt(map, "donations.askhrs", DonationWindow.getInitialAskHours()));
/*      */     
/*      */ 
/* 1569 */     if (this.core != null) {
/* 1570 */       this.core.triggerLifeCycleComponentCreated(this.uiFunctions);
/*      */     }
/*      */     
/* 1573 */     System.out.println("---------READY AT " + SystemTime.getCurrentTime() + ";" + (SystemTime.getCurrentTime() - Initializer.startTime) + "ms");
/*      */     
/* 1575 */     this.isReady = true;
/*      */   }
/*      */   
/*      */   private void configureDownloadBasket()
/*      */   {
/* 1580 */     if (COConfigurationManager.getBooleanParameter("Show Download Basket")) {
/* 1581 */       if (this.downloadBasket == null) {
/* 1582 */         this.downloadBasket = new TrayWindow();
/* 1583 */         this.downloadBasket.setVisible(true);
/*      */       }
/* 1585 */     } else if (this.downloadBasket != null) {
/* 1586 */       this.downloadBasket.setVisible(false);
/* 1587 */       this.downloadBasket = null;
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkForWhatsNewWindow() {
/* 1592 */     String CONFIG_LASTSHOWN = "welcome.version.lastshown";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1600 */       String lastShown = "";
/* 1601 */       boolean bIsStringParam = true;
/*      */       try {
/* 1603 */         lastShown = COConfigurationManager.getStringParameter("welcome.version.lastshown", "");
/*      */       }
/*      */       catch (Exception e) {
/* 1606 */         bIsStringParam = false;
/*      */       }
/*      */       
/* 1609 */       if (lastShown.length() == 0)
/*      */       {
/* 1611 */         int latestDisplayed = COConfigurationManager.getIntParameter("welcome.version.lastshown", 0);
/*      */         
/* 1613 */         if (latestDisplayed > 0) {
/* 1614 */           bIsStringParam = false;
/* 1615 */           String s = "" + latestDisplayed;
/* 1616 */           for (int i = 0; i < s.length(); i++) {
/* 1617 */             if (i != 0) {
/* 1618 */               lastShown = lastShown + ".";
/*      */             }
/* 1620 */             lastShown = lastShown + s.charAt(i);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1625 */       if (Constants.compareVersions(lastShown, Constants.getBaseVersion()) < 0) {
/* 1626 */         new org.gudy.azureus2.ui.swt.welcome.WelcomeWindow(this.shell);
/* 1627 */         if (!bIsStringParam)
/*      */         {
/* 1629 */           COConfigurationManager.removeParameter("welcome.version.lastshown");
/*      */         }
/* 1631 */         COConfigurationManager.setParameter("welcome.version.lastshown", Constants.getBaseVersion());
/*      */         
/* 1633 */         COConfigurationManager.save();
/*      */       }
/*      */     } catch (Exception e) {
/* 1636 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setHideAll(final boolean hide)
/*      */   {
/* 1644 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 1650 */         if (hide)
/*      */         {
/* 1652 */           MainWindowImpl.this.setVisible(false, true);
/*      */           
/* 1654 */           if (MainWindowImpl.this.systemTraySWT != null)
/*      */           {
/* 1656 */             MainWindowImpl.this.systemTraySWT.dispose();
/*      */           }
/*      */         }
/*      */         else {
/* 1660 */           MainWindowImpl.this.setVisible(true, true);
/*      */           
/* 1662 */           if (COConfigurationManager.getBooleanParameter("Enable System Tray"))
/*      */           {
/* 1664 */             MainWindowImpl.this.systemTraySWT = SystemTraySWT.getTray();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private void setVisible(boolean visible) {
/* 1672 */     setVisible(visible, true);
/*      */   }
/*      */   
/*      */   public void setVisible(final boolean visible, boolean tryTricks) {
/* 1676 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1678 */         String debug = COConfigurationManager.getStringParameter("adv.setting.ui.debug.window.show", "");
/*      */         
/* 1680 */         if (debug.equals("1")) {
/* 1681 */           Debug.out("MW::setVisible");
/*      */         }
/*      */         
/* 1684 */         boolean currentlyVisible = (MainWindowImpl.this.shell.getVisible()) && (!MainWindowImpl.this.shell.getMinimized());
/* 1685 */         if ((visible) && (!currentlyVisible) && 
/* 1686 */           (COConfigurationManager.getBooleanParameter("Password enabled")) && 
/* 1687 */           (!PasswordWindow.showPasswordWindow(MainWindowImpl.this.display))) {
/* 1688 */           MainWindowImpl.this.shell.setVisible(false);
/* 1689 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1694 */         if (!MainWindowImpl.this.isReady) {
/* 1695 */           return;
/*      */         }
/*      */         
/* 1698 */         ArrayList<Shell> wasVisibleList = null;
/* 1699 */         boolean bHideAndShow = false;
/*      */         
/*      */ 
/* 1702 */         if (bHideAndShow) {
/* 1703 */           wasVisibleList = new ArrayList();
/*      */           
/*      */ 
/*      */           try
/*      */           {
/* 1708 */             MainWindowImpl.this.shell.setMinimized(true);
/* 1709 */             Shell[] shells = MainWindowImpl.this.shell.getDisplay().getShells();
/* 1710 */             for (int i = 0; i < shells.length; i++) {
/* 1711 */               if (shells[i].isVisible()) {
/* 1712 */                 wasVisibleList.add(shells[i]);
/* 1713 */                 shells[i].setVisible(false);
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Exception e) {}
/*      */         }
/*      */         
/* 1720 */         if (visible) {
/* 1721 */           if (MainWindowImpl.this.shell.getMinimized()) {
/* 1722 */             MainWindowImpl.this.shell.setMinimized(false);
/*      */           }
/* 1724 */           if ((!currentlyVisible) && (COConfigurationManager.getBooleanParameter("window.maximized")))
/*      */           {
/* 1726 */             MainWindowImpl.this.shell.setMaximized(true);
/*      */           }
/*      */         }
/*      */         else {
/* 1730 */           COConfigurationManager.setParameter("window.maximized", MainWindowImpl.this.shell.getMaximized());
/*      */         }
/*      */         
/*      */ 
/* 1734 */         MainWindowImpl.this.shell.setVisible(visible);
/* 1735 */         if (visible) {
/* 1736 */           MainWindowImpl.this.shell.forceActive();
/*      */           
/* 1738 */           if (bHideAndShow) {
/*      */             try {
/* 1740 */               Shell[] shells = MainWindowImpl.this.shell.getDisplay().getShells();
/* 1741 */               for (int i = 0; i < shells.length; i++) {
/* 1742 */                 if (shells[i] != MainWindowImpl.this.shell) {
/* 1743 */                   if ((wasVisibleList != null) && (wasVisibleList.contains(shells[i])))
/*      */                   {
/* 1745 */                     shells[i].setVisible(visible);
/*      */                   }
/* 1747 */                   shells[i].setFocus();
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Exception e) {}
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void minimizeToTray(ShellEvent event)
/*      */   {
/* 1761 */     if (event != null) {
/* 1762 */       event.doit = false;
/*      */     }
/*      */     
/*      */ 
/* 1766 */     COConfigurationManager.setParameter("window.maximized", this.shell.getMaximized());
/*      */     
/* 1768 */     this.shell.setVisible(false);
/*      */     
/* 1770 */     ShellManager.sharedManager().performForShells(new Listener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/*      */ 
/* 1777 */         final Shell shell = (Shell)event.widget;
/*      */         
/* 1779 */         if (shell.getParent() == null)
/*      */         {
/* 1781 */           if (shell.getMinimized())
/*      */           {
/* 1783 */             synchronized (MainWindowImpl.this.minimized_on_hide)
/*      */             {
/* 1785 */               MainWindowImpl.this.minimized_on_hide.add(shell);
/*      */               
/* 1787 */               shell.addDisposeListener(new DisposeListener()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void widgetDisposed(DisposeEvent e)
/*      */                 {
/*      */ 
/* 1794 */                   synchronized (MainWindowImpl.this.minimized_on_hide)
/*      */                   {
/* 1796 */                     MainWindowImpl.this.minimized_on_hide.remove(shell);
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */           
/* 1803 */           shell.setVisible(false);
/*      */         }
/*      */         
/*      */       }
/* 1807 */     });
/* 1808 */     MiniBarManager.getManager().setAllVisible(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initSkinListeners()
/*      */   {
/* 1816 */     UISkinnableManagerSWT skinnableManagerSWT = UISkinnableManagerSWT.getInstance();
/* 1817 */     skinnableManagerSWT.addSkinnableListener(MessageBoxShell.class.toString(), new com.aelitis.azureus.ui.swt.UISkinnableSWTListener()
/*      */     {
/*      */ 
/*      */       public void skinBeforeComponents(Composite composite, Object skinnableObject, Object[] relatedObjects)
/*      */       {
/* 1822 */         MessageBoxShell shell = (MessageBoxShell)skinnableObject;
/*      */         
/* 1824 */         TOTorrent torrent = null;
/* 1825 */         DownloadManager dm = (DownloadManager)LogRelationUtils.queryForClass(relatedObjects, DownloadManager.class);
/*      */         
/* 1827 */         if (dm != null) {
/* 1828 */           torrent = dm.getTorrent();
/*      */         } else {
/* 1830 */           torrent = (TOTorrent)LogRelationUtils.queryForClass(relatedObjects, TOTorrent.class);
/*      */         }
/*      */         
/*      */ 
/* 1834 */         if ((torrent != null) && (shell.getLeftImage() == null)) {
/* 1835 */           byte[] contentThumbnail = PlatformTorrentUtils.getContentThumbnail(torrent);
/* 1836 */           if (contentThumbnail != null) {
/*      */             try {
/* 1838 */               ByteArrayInputStream bis = new ByteArrayInputStream(contentThumbnail);
/*      */               
/* 1840 */               final Image img = new Image(Display.getDefault(), bis);
/*      */               
/* 1842 */               shell.setLeftImage(img);
/*      */               
/* 1844 */               composite.addDisposeListener(new DisposeListener() {
/*      */                 public void widgetDisposed(DisposeEvent e) {
/* 1846 */                   if (!img.isDisposed()) {
/* 1847 */                     img.dispose();
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */             catch (Exception e) {}
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void skinAfterComponents(Composite composite, Object skinnableObject, Object[] relatedObjects) {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void initMDI()
/*      */   {
/* 1865 */     Class<?> classMDI = Utils.isAZ2UI() ? com.aelitis.azureus.ui.swt.mdi.TabbedMDI.class : com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBar.class;
/*      */     try
/*      */     {
/* 1868 */       SWTSkinObject skinObject = this.skin.getSkinObject("mdi");
/* 1869 */       if (null != skinObject) {
/* 1870 */         BaseMDI mdi = (BaseMDI)classMDI.newInstance();
/* 1871 */         mdi.setMainSkinObject(skinObject);
/* 1872 */         skinObject.addListener(mdi);
/* 1873 */         MainMDISetup.setupSideBar(mdi, this);
/*      */       }
/*      */     } catch (Throwable t) {
/* 1876 */       Debug.out(t);
/*      */     }
/*      */   }
/*      */   
/*      */   private void initWidgets2() {
/* 1881 */     SWTSkinObject skinObject = this.skin.getSkinObject("statusbar");
/* 1882 */     if (skinObject != null) {
/* 1883 */       Composite cArea = (Composite)skinObject.getControl();
/*      */       
/* 1885 */       this.statusBar = new MainStatusBar();
/* 1886 */       Composite composite = this.statusBar.initStatusBar(cArea);
/*      */       
/* 1888 */       composite.setLayoutData(Utils.getFilledFormData());
/*      */     }
/*      */     
/* 1891 */     skinObject = this.skin.getSkinObject("search-text");
/* 1892 */     if (skinObject != null) {
/* 1893 */       attachSearchBox(skinObject);
/*      */     }
/*      */     
/* 1896 */     skinObject = this.skin.getSkinObject("add-torrent");
/* 1897 */     if ((skinObject instanceof SWTSkinObjectButton)) {
/* 1898 */       SWTSkinObjectButton btn = (SWTSkinObjectButton)skinObject;
/* 1899 */       btn.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*      */       {
/*      */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask)
/*      */         {
/* 1903 */           UIFunctionsManagerSWT.getUIFunctionsSWT().openTorrentWindow();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1908 */     skinObject = this.skin.getSkinObject("pluginbar");
/* 1909 */     if (skinObject != null) {
/* 1910 */       Menu topbarMenu = new Menu(this.shell, 8);
/*      */       
/* 1912 */       if (COConfigurationManager.getIntParameter("User Mode") > 1) {
/* 1913 */         MenuItem mi = MainMenu.createViewMenuItem(this.skin, topbarMenu, "v3.MainWindow.menu.view.pluginbar", "pluginbar.visible", "pluginbar", true, -1);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1919 */         if (Utils.isAZ2UI())
/*      */         {
/*      */ 
/*      */ 
/* 1923 */           String str = mi.getText();
/*      */           
/* 1925 */           int pos = str.indexOf("\t");
/*      */           
/* 1927 */           if (pos != -1)
/*      */           {
/* 1929 */             str = str.substring(0, pos).trim();
/*      */             
/* 1931 */             mi.setText(str);
/*      */           }
/*      */           
/* 1934 */           mi.setAccelerator(0);
/*      */         }
/*      */       }
/*      */       
/* 1938 */       new MenuItem(topbarMenu, 2);
/*      */       
/* 1940 */       final MenuItem itemClipMon = new MenuItem(topbarMenu, 32);
/* 1941 */       Messages.setLanguageText(itemClipMon, "label.monitor.clipboard");
/*      */       
/* 1943 */       itemClipMon.addSelectionListener(new SelectionAdapter() {
/*      */         public void widgetSelected(SelectionEvent e) {
/* 1945 */           COConfigurationManager.setParameter("Monitor Clipboard For Torrents", itemClipMon.getSelection());
/*      */         }
/*      */         
/* 1948 */       });
/* 1949 */       boolean enabled = COConfigurationManager.getBooleanParameter("Monitor Clipboard For Torrents");
/* 1950 */       itemClipMon.setSelection(enabled);
/*      */       
/* 1952 */       COConfigurationManager.addAndFireParameterListener("Monitor Clipboard For Torrents", new ParameterListener()
/*      */       {
/*      */         private volatile AEThread2 monitor_thread;
/*      */         
/*      */         private Clipboard clipboard;
/*      */         
/*      */         private String last_text;
/*      */         
/*      */ 
/*      */         public void parameterChanged(String parameterName)
/*      */         {
/* 1963 */           boolean enabled = COConfigurationManager.getBooleanParameter(parameterName);
/*      */           
/* 1965 */           if (enabled)
/*      */           {
/* 1967 */             if (this.clipboard == null)
/*      */             {
/* 1969 */               this.clipboard = new Clipboard(Display.getDefault());
/*      */             }
/*      */             
/* 1972 */             if (this.monitor_thread == null)
/*      */             {
/* 1974 */               final AEThread2[] new_thread = { null };
/*      */               
/* 1976 */               this.monitor_thread = (new_thread[0 = new AEThread2("Clipboard Monitor")
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/* 1982 */                   Runnable checker = new Runnable()
/*      */                   {
/*      */ 
/*      */                     public void run()
/*      */                     {
/*      */ 
/* 1988 */                       if ((MainWindowImpl.32.this.monitor_thread != MainWindowImpl.32.1.this.val$new_thread[0]) || (MainWindowImpl.32.this.clipboard == null))
/*      */                       {
/* 1990 */                         return;
/*      */                       }
/*      */                       
/* 1993 */                       String text = (String)MainWindowImpl.32.this.clipboard.getContents(org.eclipse.swt.dnd.TextTransfer.getInstance());
/*      */                       
/* 1995 */                       if ((text != null) && (text.length() <= 2048))
/*      */                       {
/* 1997 */                         if ((MainWindowImpl.32.this.last_text == null) || (!MainWindowImpl.32.this.last_text.equals(text)))
/*      */                         {
/* 1999 */                           MainWindowImpl.32.this.last_text = text;
/*      */                           
/* 2001 */                           MainWindowImpl.this.addTorrentsFromClipboard(text);
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   };
/*      */                   
/*      */ 
/*      */ 
/*      */                   try
/*      */                   {
/* 2011 */                     Utils.execSWTThread(checker);
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2019 */                     if (MainWindowImpl.32.this.monitor_thread == new_thread[0])
/*      */                     {
/*      */ 
/*      */                       try
/*      */                       {
/*      */ 
/*      */ 
/* 2026 */                         Thread.sleep(500L);
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/* 2030 */                         Debug.out(e);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/* 2015 */                     Debug.out(e);
/*      */                     
/*      */ 
/*      */ 
/* 2019 */                     if (MainWindowImpl.32.this.monitor_thread == new_thread[0])
/*      */                     {
/*      */ 
/*      */                       try
/*      */                       {
/*      */ 
/*      */ 
/* 2026 */                         Thread.sleep(500L);
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/* 2030 */                         Debug.out(e);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   finally
/*      */                   {
/* 2019 */                     while (MainWindowImpl.32.this.monitor_thread == new_thread[0])
/*      */                     {
/*      */ 
/*      */ 
/*      */                       try
/*      */                       {
/*      */ 
/* 2026 */                         Thread.sleep(500L);
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/* 2030 */                         Debug.out(e);
/*      */                         
/* 2032 */                         break; } throw ((Throwable)localObject);
/*      */                     }
/*      */                     
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/* 2039 */               });
/* 2040 */               this.monitor_thread.start();
/*      */             }
/*      */           }
/*      */           else {
/* 2044 */             this.monitor_thread = null;
/* 2045 */             this.last_text = null;
/*      */             
/* 2047 */             if (this.clipboard != null)
/*      */             {
/* 2049 */               this.clipboard.dispose();
/*      */               
/* 2051 */               this.clipboard = null;
/*      */             }
/*      */             
/*      */           }
/*      */         }
/* 2056 */       });
/* 2057 */       new MenuItem(topbarMenu, 2);
/*      */       
/* 2059 */       com.aelitis.azureus.ui.swt.search.SearchUtils.addMenus(topbarMenu);
/*      */       
/* 2061 */       addMenuAndNonTextChildren((Composite)skinObject.getControl(), topbarMenu);
/*      */       
/* 2063 */       skinObject = this.skin.getSkinObject("global-toolbar");
/* 2064 */       if (skinObject != null) {
/* 2065 */         addMenuAndNonTextChildren((Composite)skinObject.getControl(), topbarMenu);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void addMenuAndNonTextChildren(Composite parent, Menu menu)
/*      */   {
/* 2072 */     parent.setMenu(menu);
/*      */     
/* 2074 */     Control[] children = parent.getChildren();
/* 2075 */     for (int i = 0; i < children.length; i++) {
/* 2076 */       Control control = children[i];
/* 2077 */       if ((control instanceof Composite)) {
/* 2078 */         Composite c = (Composite)control;
/* 2079 */         addMenuAndNonTextChildren(c, menu);
/* 2080 */       } else if (!(control instanceof Text)) {
/* 2081 */         control.setMenu(menu);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void addTorrentsFromClipboard(String text)
/*      */   {
/* 2090 */     String[] splitters = { "\r\n", "\n", "\r", "\t" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2097 */     String[] lines = null;
/*      */     
/* 2099 */     for (int i = 0; i < splitters.length; i++) {
/* 2100 */       if (text.contains(splitters[i])) {
/* 2101 */         lines = text.split(splitters[i]);
/* 2102 */         break;
/*      */       }
/*      */     }
/*      */     
/* 2106 */     if (lines == null)
/*      */     {
/* 2108 */       lines = new String[] { text };
/*      */     }
/*      */     
/* 2111 */     for (int i = 0; i < lines.length; i++)
/*      */     {
/* 2113 */       String line = lines[i].trim();
/*      */       
/* 2115 */       if ((line.startsWith("\"")) && (line.endsWith("\"")))
/*      */       {
/* 2117 */         if (line.length() < 3)
/*      */         {
/* 2119 */           line = "";
/*      */         }
/*      */         else
/*      */         {
/* 2123 */           line = line.substring(1, line.length() - 2);
/*      */         }
/*      */       }
/*      */       
/* 2127 */       if (org.gudy.azureus2.core3.util.UrlUtils.isURL(line))
/*      */       {
/* 2129 */         Map<String, Object> options = new HashMap();
/*      */         
/* 2131 */         options.put("hideErrors", Boolean.valueOf(true));
/*      */         
/* 2133 */         org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener.openTorrent(line, options);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void attachSearchBox(SWTSkinObject skinObject)
/*      */   {
/* 2143 */     Composite cArea = (Composite)skinObject.getControl();
/*      */     
/* 2145 */     final Text text = new Text(cArea, 0);
/* 2146 */     text.setMessage(MessageText.getString("v3.MainWindow.search.defaultText"));
/* 2147 */     org.eclipse.swt.layout.FormData filledFormData = Utils.getFilledFormData();
/* 2148 */     text.setLayoutData(filledFormData);
/*      */     
/* 2150 */     text.setData("ObfusticateImage", new ObfusticateImage() {
/*      */       public Image obfusticatedImage(Image image) {
/* 2152 */         Point location = Utils.getLocationRelativeToShell(text);
/* 2153 */         Point size = text.getSize();
/* 2154 */         org.gudy.azureus2.ui.swt.debug.UIDebugGenerator.obfusticateArea(image, new Rectangle(location.x, location.y, size.x, size.y));
/*      */         
/* 2156 */         return image;
/*      */       }
/*      */       
/* 2159 */     });
/* 2160 */     text.addListener(11, new Listener() {
/* 2161 */       Font lastFont = null;
/* 2162 */       int lastHeight = -1;
/*      */       
/*      */       public void handleEvent(Event event) {
/* 2165 */         Text text = (Text)event.widget;
/*      */         
/* 2167 */         int h = text.getClientArea().height - 2;
/*      */         
/* 2169 */         if (h == this.lastHeight) {
/* 2170 */           return;
/*      */         }
/*      */         
/* 2173 */         this.lastHeight = h;
/* 2174 */         Font font = com.aelitis.azureus.ui.swt.utils.FontUtils.getFontWithHeight(text.getFont(), null, h);
/* 2175 */         if (font != null) {
/* 2176 */           text.setFont(font);
/*      */           
/* 2178 */           if (this.lastFont == null)
/*      */           {
/* 2180 */             text.addDisposeListener(new DisposeListener() {
/*      */               public void widgetDisposed(DisposeEvent e) {
/* 2182 */                 Text text = (Text)e.widget;
/* 2183 */                 text.setFont(null);
/* 2184 */                 Utils.disposeSWTObjects(new Object[] { MainWindowImpl.34.this.lastFont });
/*      */               }
/*      */               
/*      */ 
/*      */             });
/*      */           }
/*      */           else {
/* 2191 */             Utils.disposeSWTObjects(new Object[] { this.lastFont });
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 2196 */           this.lastFont = font;
/*      */         }
/*      */         
/*      */       }
/* 2200 */     });
/* 2201 */     text.setTextLimit(2048);
/*      */     
/* 2203 */     if (Constants.isWindows) {
/* 2204 */       text.addListener(3, new Listener() {
/*      */         public void handleEvent(Event event) {
/* 2206 */           if (event.count == 3) {
/* 2207 */             text.selectAll();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 2213 */     String tooltip = MessageText.getString("v3.MainWindow.search.tooltip");
/*      */     
/* 2215 */     text.setToolTipText(tooltip);
/*      */     
/* 2217 */     SWTSkinProperties properties = skinObject.getProperties();
/* 2218 */     this.colorSearchTextBG = properties.getColor("color.search.text.bg");
/* 2219 */     this.colorSearchTextFG = properties.getColor("color.search.text.fg");
/*      */     
/* 2221 */     if (this.colorSearchTextBG != null) {
/* 2222 */       text.setBackground(this.colorSearchTextBG);
/*      */     }
/*      */     
/* 2225 */     final TextWithHistory twh = new TextWithHistory("mainwindow.search.history", text);
/*      */     
/* 2227 */     text.addKeyListener(new org.eclipse.swt.events.KeyListener() {
/*      */       public void keyPressed(KeyEvent e) {
/* 2229 */         int key = e.character;
/*      */         
/* 2231 */         if (e.stateMask == SWT.MOD1)
/*      */         {
/* 2233 */           if ((key <= 26) && (key > 0)) {
/* 2234 */             key += 96;
/*      */           }
/*      */           
/* 2237 */           if (key == 97) {
/* 2238 */             text.selectAll();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void keyReleased(KeyEvent arg0) {}
/* 2248 */     });
/* 2249 */     text.addListener(1, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 2252 */         Text text = (Text)event.widget;
/* 2253 */         if (event.keyCode == 27) {
/* 2254 */           text.setText("");
/* 2255 */           return;
/*      */         }
/* 2257 */         if ((event.character == '\r') && 
/* 2258 */           (event.doit)) {
/* 2259 */           String expression = text.getText();
/*      */           
/* 2261 */           MainWindowImpl.this.uiFunctions.doSearch(expression);
/*      */           
/* 2263 */           twh.addHistory(expression);
/*      */         }
/*      */         
/*      */       }
/*      */       
/* 2268 */     });
/* 2269 */     SWTSkinObject searchGo = this.skin.getSkinObject("search-go");
/* 2270 */     if (searchGo != null) {
/* 2271 */       SWTSkinButtonUtility btnGo = new SWTSkinButtonUtility(searchGo);
/* 2272 */       btnGo.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*      */       {
/*      */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/* 2275 */           String sSearchText = text.getText().trim();
/* 2276 */           MainWindowImpl.this.uiFunctions.doSearch(sSearchText);
/*      */           
/* 2278 */           twh.addHistory(sSearchText);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 2283 */     SWTSkinObject so = this.skin.getSkinObject("search-dropdown");
/* 2284 */     if (so != null) {
/* 2285 */       SWTSkinButtonUtility btnSearchDD = new SWTSkinButtonUtility(so);
/* 2286 */       btnSearchDD.setTooltipID("v3.MainWindow.search.tooltip");
/* 2287 */       btnSearchDD.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*      */       {
/*      */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int button, int stateMask)
/*      */         {
/* 2291 */           if (button == 1)
/*      */           {
/* 2293 */             String sSearchText = text.getText().trim();
/*      */             
/* 2295 */             MainWindowImpl.this.uiFunctions.doSearch(sSearchText);
/*      */             
/* 2297 */             twh.addHistory(sSearchText);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void updateMapTrackUsage(String sTabID)
/*      */   {
/* 2309 */     if (mapTrackUsage != null) {
/* 2310 */       mapTrackUsage_mon.enter();
/*      */       try {
/* 2312 */         if (this.lCurrentTrackTime > 1000L) {
/* 2313 */           addUsageStat(sTabID, this.lCurrentTrackTime);
/*      */         }
/*      */         
/*      */ 
/* 2317 */         if (this.lCurrentTrackTimeIdle > 1000L) {
/* 2318 */           String id = "idle-" + sTabID;
/* 2319 */           addUsageStat(id, this.lCurrentTrackTimeIdle);
/*      */         }
/*      */       } finally {
/* 2322 */         mapTrackUsage_mon.exit();
/*      */       }
/*      */     }
/*      */     
/* 2326 */     this.lCurrentTrackTime = 0L;
/* 2327 */     this.lCurrentTrackTimeIdle = 0L;
/*      */   }
/*      */   
/*      */   private static void addUsageStat(String id, long value) {
/* 2331 */     if (id == null) {
/* 2332 */       return;
/*      */     }
/* 2334 */     if (id.length() > 150) {
/* 2335 */       id = id.substring(0, 150);
/*      */     }
/* 2337 */     if (mapTrackUsage != null) {
/* 2338 */       mapTrackUsage_mon.enter();
/*      */       try {
/* 2340 */         List currentLength = (List)mapTrackUsage.get(id);
/* 2341 */         if (currentLength == null) {
/* 2342 */           currentLength = new ArrayList();
/* 2343 */           currentLength.add(Integer.valueOf(1));
/* 2344 */           currentLength.add(Long.valueOf(value / 100L));
/*      */         } else {
/* 2346 */           List oldList = currentLength;
/* 2347 */           currentLength = new ArrayList();
/* 2348 */           currentLength.add(Long.valueOf(((Number)oldList.get(0)).longValue() + 1L));
/* 2349 */           currentLength.add(Long.valueOf(((Number)oldList.get(1)).longValue() + value / 1000L));
/*      */         }
/* 2351 */         mapTrackUsage.put(id, currentLength);
/*      */       } finally {
/* 2353 */         mapTrackUsage_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Shell getShell()
/*      */   {
/* 2361 */     return this.shell;
/*      */   }
/*      */   
/*      */   public UISWTInstanceImpl getUISWTInstanceImpl() {
/* 2365 */     return this.uiSWTInstanceImpl;
/*      */   }
/*      */   
/*      */   public MainStatusBar getMainStatusBar() {
/* 2369 */     return this.statusBar;
/*      */   }
/*      */   
/*      */   public boolean isVisible(int windowElement) {
/* 2373 */     if (windowElement == 2) {
/* 2374 */       SWTSkinObject skinObject = this.skin.getSkinObject("global-toolbar");
/* 2375 */       if (skinObject != null) {
/* 2376 */         return skinObject.isVisible();
/*      */       }
/* 2378 */     } else if (windowElement == 4) {
/* 2379 */       SWTSkinObject skinObject = this.skin.getSkinObject("pluginbar");
/* 2380 */       if (skinObject != null) {
/* 2381 */         return skinObject.isVisible();
/*      */       }
/* 2383 */     } else if (windowElement != 3)
/*      */     {
/* 2385 */       if (windowElement != 1) {}
/*      */     }
/*      */     
/*      */ 
/* 2389 */     return false;
/*      */   }
/*      */   
/*      */   public void setVisible(int windowElement, boolean value) {
/* 2393 */     if (windowElement == 2) {
/* 2394 */       SWTSkinUtils.setVisibility(this.skin, "IconBar.enabled", "global-toolbar", value, true, true);
/*      */     }
/* 2396 */     else if (windowElement == 4)
/*      */     {
/* 2398 */       SWTSkinUtils.setVisibility(this.skin, "pluginbar.visible", "pluginbar", value, true, true);
/*      */ 
/*      */     }
/* 2401 */     else if (windowElement != 3)
/*      */     {
/* 2403 */       if (windowElement != 1) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Rectangle getMetrics(int windowElement)
/*      */   {
/* 2410 */     if (windowElement != 2) {
/* 2411 */       if (windowElement == 4)
/*      */       {
/* 2413 */         SWTSkinObject skinObject = this.skin.getSkinObject("pluginbar");
/* 2414 */         if (skinObject != null) {
/* 2415 */           return skinObject.getControl().getBounds();
/*      */         }
/*      */       } else {
/* 2418 */         if (windowElement == 3)
/*      */         {
/* 2420 */           return this.statusBar.getBounds();
/*      */         }
/* 2422 */         if (windowElement == 6)
/*      */         {
/* 2424 */           return this.shell.getClientArea();
/*      */         }
/* 2426 */         if (windowElement == 7)
/*      */         {
/* 2428 */           Rectangle r = getMetrics(6);
/* 2429 */           r.height -= getMetrics(4).height;
/* 2430 */           r.height -= getMetrics(2).height;
/* 2431 */           r.height -= getMetrics(3).height;
/* 2432 */           return r;
/*      */         }
/*      */       }
/*      */     }
/* 2436 */     return new Rectangle(0, 0, 0, 0);
/*      */   }
/*      */   
/*      */   private SWTSkin getSkin() {
/* 2440 */     return this.skin;
/*      */   }
/*      */   
/*      */   public boolean isReady() {
/* 2444 */     return this.isReady;
/*      */   }
/*      */   
/*      */ 
/*      */   public Image generateObfusticatedImage()
/*      */   {
/* 2450 */     Rectangle shellBounds = this.shell.getBounds();
/* 2451 */     Rectangle shellClientArea = this.shell.getClientArea();
/*      */     
/* 2453 */     Image fullImage = new Image(this.display, shellBounds.width, shellBounds.height);
/* 2454 */     Image subImage = new Image(this.display, shellClientArea.width, shellClientArea.height);
/*      */     
/* 2456 */     GC gc = new GC(this.display);
/*      */     try {
/* 2458 */       gc.copyArea(fullImage, shellBounds.x, shellBounds.y);
/*      */     } finally {
/* 2460 */       gc.dispose();
/*      */     }
/* 2462 */     GC gcShell = new GC(this.shell);
/*      */     try {
/* 2464 */       gcShell.copyArea(subImage, 0, 0);
/*      */     } finally {
/* 2466 */       gcShell.dispose();
/*      */     }
/* 2468 */     GC gcFullImage = new GC(fullImage);
/*      */     try {
/* 2470 */       Point location = this.shell.toDisplay(0, 0);
/* 2471 */       gcFullImage.drawImage(subImage, location.x - shellBounds.x, location.y - shellBounds.y);
/*      */     }
/*      */     finally {
/* 2474 */       gcFullImage.dispose();
/*      */     }
/* 2476 */     subImage.dispose();
/*      */     
/* 2478 */     Control[] children = this.shell.getChildren();
/* 2479 */     for (int i = 0; i < children.length; i++) {
/* 2480 */       Control control = children[i];
/* 2481 */       SWTSkinObject so = (SWTSkinObject)control.getData("SkinObject");
/* 2482 */       if ((so instanceof ObfusticateImage)) {
/* 2483 */         ObfusticateImage oi = (ObfusticateImage)so;
/* 2484 */         oi.obfusticatedImage(fullImage);
/*      */       }
/*      */     }
/*      */     
/* 2488 */     Rectangle monitorClientArea = this.shell.getMonitor().getClientArea();
/* 2489 */     Rectangle trimmedShellBounds = shellBounds.intersection(monitorClientArea);
/*      */     
/* 2491 */     if (!trimmedShellBounds.equals(shellBounds)) {
/* 2492 */       subImage = new Image(this.display, trimmedShellBounds.width, trimmedShellBounds.height);
/*      */       
/* 2494 */       GC gcCrop = new GC(subImage);
/*      */       try {
/* 2496 */         gcCrop.drawImage(fullImage, shellBounds.x - trimmedShellBounds.x, shellBounds.y - trimmedShellBounds.y);
/*      */       }
/*      */       finally {
/* 2499 */         gcCrop.dispose();
/* 2500 */         fullImage.dispose();
/* 2501 */         fullImage = subImage;
/*      */       }
/*      */     }
/*      */     
/* 2505 */     return fullImage;
/*      */   }
/*      */   
/*      */ 
/*      */   public void mdiEntrySelected(MdiEntry newEntry, MdiEntry oldEntry)
/*      */   {
/* 2511 */     if (newEntry == null) {
/* 2512 */       return;
/*      */     }
/*      */     
/* 2515 */     if ((mapTrackUsage != null) && (oldEntry != null)) {
/* 2516 */       oldEntry.removeListener(this);
/*      */       
/* 2518 */       String id2 = null;
/* 2519 */       MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 2520 */       if (mdi != null) {
/* 2521 */         id2 = oldEntry.getLogID();
/*      */       }
/* 2523 */       if (id2 == null) {
/* 2524 */         id2 = oldEntry.getId();
/*      */       }
/*      */       
/* 2527 */       updateMapTrackUsage(id2);
/*      */     }
/*      */     
/* 2530 */     if (mapTrackUsage != null) {
/* 2531 */       newEntry.addListener(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void mdiEntryLogIdChanged(MdiEntry sideBarEntrySWT, String oldID, String newID)
/*      */   {
/* 2538 */     if (oldID == null) {
/* 2539 */       oldID = "null";
/*      */     }
/* 2541 */     updateMapTrackUsage(oldID);
/*      */   }
/*      */   
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 2546 */     writer.println("SWT UI");
/*      */     try
/*      */     {
/* 2549 */       writer.indent();
/*      */       
/* 2551 */       TableColumnManager.getInstance().generateDiagnostics(writer);
/*      */     }
/*      */     finally {
/* 2554 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSelectedLanguageItem() {
/* 2559 */     Messages.updateLanguageForControl(this.shell);
/*      */     
/* 2561 */     if (this.systemTraySWT != null) {
/* 2562 */       this.systemTraySWT.updateLanguage();
/*      */     }
/*      */     
/* 2565 */     if (this.statusBar != null) {
/* 2566 */       this.statusBar.refreshStatusText();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2571 */     this.skin.triggerLanguageChange();
/*      */     
/* 2573 */     if (this.statusBar != null) {
/* 2574 */       this.statusBar.updateStatusText();
/*      */     }
/*      */     
/* 2577 */     if (this.menu != null) {
/* 2578 */       org.gudy.azureus2.ui.swt.mainwindow.MenuFactory.updateMenuText(this.menu.getMenu("menu.bar"));
/*      */     }
/*      */   }
/*      */   
/*      */   public IMainMenu getMainMenu() {
/* 2583 */     return this.menu;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void updateUI() {}
/*      */   
/*      */ 
/*      */   public String getUpdateUIName()
/*      */   {
/* 2593 */     return "MainWindow";
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/main/MainWindowImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */