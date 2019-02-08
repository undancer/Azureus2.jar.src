/*      */ package org.gudy.azureus2.ui.swt.mainwindow;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.dht.DHT;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIStatusTextClickListener;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.custom.CLabel;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.PaintEvent;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.stats.transfer.OverallStats;
/*      */ import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.network.ConnectionManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.ui.swt.AZProgressBar;
/*      */ import org.gudy.azureus2.ui.swt.Alerts;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.progress.IProgressReport;
/*      */ import org.gudy.azureus2.ui.swt.progress.IProgressReporter;
/*      */ import org.gudy.azureus2.ui.swt.progress.ProgressReporterWindow;
/*      */ import org.gudy.azureus2.ui.swt.progress.ProgressReportingManager;
/*      */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*      */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*      */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter.URLInfo;
/*      */ import org.gudy.azureus2.ui.swt.update.UpdateWindow;
/*      */ import org.gudy.azureus2.ui.swt.views.stats.StatsView;
/*      */ 
/*      */ public class MainStatusBar implements IMainStatusBar, com.aelitis.azureus.ui.common.updater.UIUpdatableAlways
/*      */ {
/*      */   private static final String STATUS_ICON_WARN = "sb_warning";
/*      */   private static final String ID = "MainStatusBar";
/*   83 */   private AEMonitor this_mon = new AEMonitor("MainStatusBar");
/*      */   
/*      */   private UpdateWindow updateWindow;
/*      */   
/*      */   private Composite parent;
/*      */   
/*      */   private Composite statusBar;
/*      */   
/*      */   private CLabel statusText;
/*   92 */   private String statusTextKey = "";
/*      */   
/*   94 */   private String statusImageKey = null;
/*      */   
/*   96 */   private Image statusImage = null;
/*      */   
/*      */   private AZProgressBar progressBar;
/*      */   
/*      */   private CLabelPadding ipBlocked;
/*      */   
/*      */   private CLabelPadding srStatus;
/*      */   
/*      */   private CLabelPadding natStatus;
/*      */   
/*      */   private CLabelPadding dhtStatus;
/*      */   
/*      */   private CLabelPadding statusDown;
/*      */   
/*      */   private CLabelPadding statusUp;
/*      */   
/*      */   private Composite plugin_label_composite;
/*      */   
/*  114 */   private ArrayList<Runnable> listRunAfterInit = new ArrayList();
/*      */   
/*      */ 
/*      */   private Display display;
/*      */   
/*  119 */   private long last_sr_ratio = -1L;
/*      */   
/*  121 */   private int last_sr_status = -1;
/*      */   
/*  123 */   private int lastNATstatus = -1;
/*      */   
/*  125 */   private String lastNATimageID = null;
/*      */   
/*  127 */   private int lastDHTstatus = -1;
/*      */   
/*  129 */   private long lastDHTcount = -1L;
/*      */   
/*      */ 
/*      */   private NumberFormat numberFormat;
/*      */   
/*      */ 
/*      */   private OverallStats overall_stats;
/*      */   
/*      */ 
/*      */   private ConnectionManager connection_manager;
/*      */   
/*      */ 
/*      */   private DHTPlugin dhtPlugin;
/*      */   
/*      */ 
/*      */   private UIFunctions uiFunctions;
/*      */   
/*      */ 
/*      */   private UIStatusTextClickListener clickListener;
/*      */   
/*      */   private static final int borderFlag = 32;
/*      */   
/*  151 */   private boolean isAZ3 = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  157 */   private ProgressReportingManager PRManager = ProgressReportingManager.getInstance();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  162 */   private GridData progressGridData = new GridData(131072, 16777216, false, false);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private CLabelPadding progressViewerImageLabel;
/*      */   
/*      */ 
/*      */ 
/*  171 */   private String lastSRimageID = null;
/*      */   
/*      */   private int last_dl_limit;
/*      */   
/*  175 */   private long last_rec_data = -1L;
/*      */   
/*      */   private long last_rec_prot;
/*      */   
/*  179 */   private long[] max_rec = { 0L };
/*  180 */   private long[] max_sent = { 0L };
/*      */   
/*      */   private Image imgRec;
/*      */   
/*      */   private Image imgSent;
/*      */   private Image warningIcon;
/*      */   private Image warningGreyIcon;
/*      */   private Image infoIcon;
/*      */   private CLabelPadding statusWarnings;
/*      */   private TimerEventPeriodic alert_flasher_event;
/*      */   private long alert_flasher_event_start_time;
/*      */   private boolean alert_flash_activate;
/*      */   
/*      */   public MainStatusBar()
/*      */   {
/*  195 */     this.numberFormat = NumberFormat.getInstance();
/*      */     
/*  197 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/*  199 */         PluginManager pm = core.getPluginManager();
/*  200 */         MainStatusBar.this.connection_manager = PluginInitializer.getDefaultInterface().getConnectionManager();
/*  201 */         PluginInterface dht_pi = pm.getPluginInterfaceByClass(DHTPlugin.class);
/*  202 */         if (dht_pi != null) {
/*  203 */           MainStatusBar.this.dhtPlugin = ((DHTPlugin)dht_pi.getPlugin());
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Composite initStatusBar(Composite _parent)
/*      */   {
/*  214 */     this.parent = _parent;
/*  215 */     this.display = this.parent.getDisplay();
/*  216 */     this.uiFunctions = com.aelitis.azureus.ui.UIFunctionsManager.getUIFunctions();
/*  217 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*      */     
/*      */ 
/*      */ 
/*  221 */     org.eclipse.swt.graphics.Color fgColor = this.parent.getForeground();
/*      */     
/*  223 */     this.statusBar = new Composite(this.parent, 0);
/*  224 */     this.statusBar.setForeground(fgColor);
/*  225 */     this.isAZ3 = "az3".equalsIgnoreCase(COConfigurationManager.getStringParameter("ui"));
/*      */     
/*  227 */     this.statusBar.getShell().addListener(20, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  229 */         Utils.execSWTThreadLater(0, new AERunnable() {
/*      */           public void runSupport() {
/*  231 */             if (!MainStatusBar.this.statusBar.isDisposed()) {
/*  232 */               MainStatusBar.this.statusBar.layout();
/*      */             }
/*      */             
/*      */           }
/*      */         });
/*      */       }
/*  238 */     });
/*  239 */     GridLayout layout_status = new GridLayout();
/*  240 */     layout_status.numColumns = 20;
/*  241 */     layout_status.horizontalSpacing = 0;
/*  242 */     layout_status.verticalSpacing = 0;
/*  243 */     layout_status.marginHeight = 0;
/*  244 */     if (Constants.isOSX) {
/*      */       try
/*      */       {
/*  247 */         layout_status.marginRight = 15;
/*      */       }
/*      */       catch (NoSuchFieldError e) {
/*  250 */         layout_status.marginWidth = 15;
/*      */       }
/*      */     } else {
/*  253 */       layout_status.marginWidth = 0;
/*      */     }
/*  255 */     this.statusBar.setLayout(layout_status);
/*      */     
/*      */ 
/*  258 */     this.statusText = new CLabel(this.statusBar, 32);
/*  259 */     this.statusText.setForeground(fgColor);
/*  260 */     Utils.setLayoutData(this.statusText, new GridData(784));
/*      */     
/*      */ 
/*  263 */     addStatusBarMenu(this.statusText);
/*      */     
/*  265 */     GC gc = new GC(this.statusText);
/*      */     
/*  267 */     int height = Math.max(16, gc.getFontMetrics().getHeight()) + 6;
/*  268 */     gc.dispose();
/*      */     
/*  270 */     FormData formData = new FormData();
/*  271 */     formData.height = height;
/*  272 */     formData.bottom = new FormAttachment(100, 0);
/*  273 */     formData.left = new FormAttachment(0, 0);
/*  274 */     formData.right = new FormAttachment(100, 0);
/*  275 */     this.statusBar.setLayoutData(formData);
/*      */     
/*  277 */     Listener listener = new Listener() {
/*      */       public void handleEvent(Event e) {
/*  279 */         if (MainStatusBar.this.clickListener == null) {
/*  280 */           if (MainStatusBar.this.updateWindow != null) {
/*  281 */             MainStatusBar.this.updateWindow.show();
/*      */           }
/*      */         } else {
/*  284 */           MainStatusBar.this.clickListener.UIStatusTextClicked();
/*      */         }
/*      */         
/*      */       }
/*  288 */     };
/*  289 */     this.statusText.addListener(4, listener);
/*  290 */     this.statusText.addListener(8, listener);
/*      */     
/*      */ 
/*      */ 
/*  294 */     if (Constants.isOSX) {
/*  295 */       this.progressBar = new AZProgressBar(this.statusBar, true);
/*      */     } else {
/*  297 */       this.progressBar = new AZProgressBar(this.statusBar, false);
/*      */     }
/*      */     
/*  300 */     this.progressBar.setVisible(false);
/*  301 */     this.progressGridData = new GridData(16777216, 16777216, false, false);
/*  302 */     this.progressGridData.widthHint = 5;
/*  303 */     Utils.setLayoutData(this.progressBar, this.progressGridData);
/*      */     
/*  305 */     addRIP();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  311 */     this.progressViewerImageLabel = new CLabelPadding(this.statusBar, 0);
/*      */     
/*  313 */     this.progressViewerImageLabel.setToolTipText(MessageText.getString("Progress.reporting.statusbar.button.tooltip"));
/*  314 */     this.progressViewerImageLabel.addMouseListener(new MouseAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void mouseDown(MouseEvent e)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  324 */         IProgressReporter[] reporters = MainStatusBar.this.PRManager.getReportersArray(false);
/*  325 */         if (reporters.length == 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  330 */           if (!ProgressReporterWindow.isShowingEmpty()) {
/*  331 */             ProgressReporterWindow.open(reporters, 32);
/*      */           }
/*      */           
/*      */         }
/*      */         else {
/*  336 */           for (int i = 0; i < reporters.length; i++) {
/*  337 */             if (!ProgressReporterWindow.isOpened(reporters[i])) {
/*  338 */               ProgressReporterWindow.open(reporters, 32);
/*      */               
/*  340 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  345 */     });
/*  346 */     this.progressViewerImageLabel.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/*  348 */         ImageLoader imageLoader = ImageLoader.getInstance();
/*  349 */         imageLoader.releaseImage("progress_error");
/*  350 */         imageLoader.releaseImage("progress_info");
/*  351 */         imageLoader.releaseImage("progress_viewer");
/*      */       }
/*      */       
/*  354 */     });
/*  355 */     this.plugin_label_composite = new Composite(this.statusBar, 0);
/*  356 */     this.plugin_label_composite.setForeground(fgColor);
/*  357 */     GridLayout gridLayout = new GridLayout();
/*  358 */     gridLayout.horizontalSpacing = 0;
/*  359 */     gridLayout.verticalSpacing = 0;
/*  360 */     gridLayout.marginHeight = 0;
/*  361 */     gridLayout.marginBottom = 0;
/*  362 */     gridLayout.marginTop = 0;
/*  363 */     gridLayout.marginLeft = 0;
/*  364 */     gridLayout.marginRight = 0;
/*  365 */     gridLayout.numColumns = 20;
/*      */     
/*  367 */     GridData gridData = new GridData(1040);
/*  368 */     gridData.heightHint = height;
/*  369 */     gridData.minimumHeight = height;
/*  370 */     this.plugin_label_composite.setLayout(gridLayout);
/*  371 */     this.plugin_label_composite.setLayoutData(gridData);
/*      */     
/*  373 */     this.srStatus = new CLabelPadding(this.statusBar, 32);
/*  374 */     this.srStatus.setText(MessageText.getString("SpeedView.stats.ratio"));
/*      */     
/*  376 */     COConfigurationManager.addAndFireParameterListener("Status Area Show SR", new ParameterListener()
/*      */     {
/*      */       public void parameterChanged(String parameterName) {
/*  379 */         MainStatusBar.this.srStatus.setVisible(COConfigurationManager.getBooleanParameter(parameterName));
/*  380 */         MainStatusBar.this.statusBar.layout();
/*      */       }
/*      */       
/*  383 */     });
/*  384 */     this.natStatus = new CLabelPadding(this.statusBar, 32);
/*  385 */     this.natStatus.setText("");
/*      */     
/*  387 */     COConfigurationManager.addAndFireParameterListener("Status Area Show NAT", new ParameterListener()
/*      */     {
/*      */       public void parameterChanged(String parameterName) {
/*  390 */         MainStatusBar.this.natStatus.setVisible(COConfigurationManager.getBooleanParameter(parameterName));
/*  391 */         MainStatusBar.this.statusBar.layout();
/*      */       }
/*      */       
/*  394 */     });
/*  395 */     this.dhtStatus = new CLabelPadding(this.statusBar, 32);
/*  396 */     this.dhtStatus.setText("");
/*  397 */     this.dhtStatus.setToolTipText(MessageText.getString("MainWindow.dht.status.tooltip"));
/*      */     
/*  399 */     COConfigurationManager.addAndFireParameterListener("Status Area Show DDB", new ParameterListener()
/*      */     {
/*      */       public void parameterChanged(String parameterName) {
/*  402 */         MainStatusBar.this.dhtStatus.setVisible(COConfigurationManager.getBooleanParameter(parameterName));
/*  403 */         MainStatusBar.this.statusBar.layout();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  408 */     });
/*  409 */     this.ipBlocked = new CLabelPadding(this.statusBar, 32);
/*  410 */     this.ipBlocked.setText("{} IPs:");
/*  411 */     Messages.setLanguageText(this.ipBlocked, "MainWindow.IPs.tooltip");
/*  412 */     this.ipBlocked.addListener(8, new ListenerNeedingCoreRunning() {
/*      */       public void handleEvent(AzureusCore core, Event event) {
/*  414 */         org.gudy.azureus2.ui.swt.BlockedIpsWindow.showBlockedIps(core, MainStatusBar.this.parent.getShell());
/*      */       }
/*      */       
/*  417 */     });
/*  418 */     final Menu menuIPFilter = new Menu(this.statusBar.getShell(), 8);
/*  419 */     this.ipBlocked.setMenu(menuIPFilter);
/*      */     
/*  421 */     menuIPFilter.addListener(22, new Listener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(Event e)
/*      */       {
/*      */ 
/*  428 */         MenuItem[] oldItems = menuIPFilter.getItems();
/*      */         
/*  430 */         for (int i = 0; i < oldItems.length; i++)
/*      */         {
/*  432 */           oldItems[i].dispose();
/*      */         }
/*      */         
/*  435 */         if (!AzureusCoreFactory.isCoreRunning())
/*      */         {
/*  437 */           return;
/*      */         }
/*      */         
/*  440 */         AzureusCore azureusCore = AzureusCoreFactory.getSingleton();
/*      */         
/*  442 */         final IpFilter ip_filter = azureusCore.getIpFilterManager().getIPFilter();
/*      */         
/*  444 */         final MenuItem ipfEnable = new MenuItem(menuIPFilter, 32);
/*      */         
/*  446 */         ipfEnable.setSelection(ip_filter.isEnabled());
/*      */         
/*  448 */         Messages.setLanguageText(ipfEnable, "MyTorrentsView.menu.ipf_enable");
/*      */         
/*  450 */         ipfEnable.addSelectionListener(new SelectionAdapter()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*      */ 
/*  457 */             ip_filter.setEnabled(ipfEnable.getSelection());
/*      */           }
/*      */           
/*  460 */         });
/*  461 */         MenuItem ipfOptions = new MenuItem(menuIPFilter, 8);
/*      */         
/*  463 */         Messages.setLanguageText(ipfOptions, "ipfilter.options");
/*      */         
/*  465 */         ipfOptions.addSelectionListener(new SelectionAdapter()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*      */ 
/*  472 */             UIFunctions uif = com.aelitis.azureus.ui.UIFunctionsManager.getUIFunctions();
/*      */             
/*  474 */             if (uif != null)
/*      */             {
/*  476 */               uif.getMDI().showEntryByID("ConfigView", "ipfilter");
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         });
/*      */       }
/*  483 */     });
/*  484 */     COConfigurationManager.addAndFireParameterListener("Status Area Show IPF", new ParameterListener()
/*      */     {
/*      */       public void parameterChanged(String parameterName) {
/*  487 */         MainStatusBar.this.ipBlocked.setVisible(COConfigurationManager.getBooleanParameter(parameterName));
/*  488 */         MainStatusBar.this.statusBar.layout();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  494 */     });
/*  495 */     this.statusDown = new CLabelPadding(this.statusBar, 32);
/*  496 */     this.statusDown.setImage(imageLoader.getImage("down"));
/*      */     
/*  498 */     Messages.setLanguageText(this.statusDown, "MainWindow.status.updowndetails.tooltip");
/*      */     
/*      */ 
/*  501 */     Listener lStats = new Listener() {
/*      */       public void handleEvent(Event e) {
/*  503 */         MainStatusBar.this.uiFunctions.getMDI().loadEntryByID(StatsView.VIEW_ID, true, false, "TransferStatsView");
/*      */       }
/*      */       
/*  506 */     };
/*  507 */     this.statusUp = new CLabelPadding(this.statusBar, 32);
/*  508 */     this.statusUp.setImage(imageLoader.getImage("up"));
/*      */     
/*  510 */     Messages.setLanguageText(this.statusUp, "MainWindow.status.updowndetails.tooltip");
/*      */     
/*      */ 
/*  513 */     this.statusDown.addListener(8, lStats);
/*  514 */     this.statusUp.addListener(8, lStats);
/*      */     
/*  516 */     Listener lDHT = new Listener() {
/*      */       public void handleEvent(Event e) {
/*  518 */         MainStatusBar.this.uiFunctions.getMDI().loadEntryByID(StatsView.VIEW_ID, true, false, "DHTView");
/*      */       }
/*      */       
/*  521 */     };
/*  522 */     this.dhtStatus.addListener(8, lDHT);
/*      */     
/*  524 */     Listener lSR = new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  527 */         MainStatusBar.this.uiFunctions.getMDI().loadEntryByID(StatsView.VIEW_ID, true, false, "SpeedView");
/*      */         
/*  529 */         OverallStats stats = StatsFactory.getStats();
/*      */         
/*  531 */         if (stats == null) {
/*  532 */           return;
/*      */         }
/*      */         
/*  535 */         long ratio = 1000L * stats.getUploadedBytes() / (stats.getDownloadedBytes() + 1L);
/*      */         
/*  537 */         if (ratio < 900L) {}
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  543 */     };
/*  544 */     this.srStatus.addListener(8, lSR);
/*      */     
/*  546 */     Listener lNAT = new ListenerNeedingCoreRunning() {
/*      */       public void handleEvent(AzureusCore core, Event e) {
/*  548 */         MainStatusBar.this.uiFunctions.getMDI().loadEntryByID("ConfigView", true, false, "server");
/*      */         
/*      */ 
/*      */ 
/*  552 */         if (PluginInitializer.getDefaultInterface().getConnectionManager().getNATStatus() != 1) {
/*  553 */           Utils.launch("http://wiki.vuze.com/w/NAT_problem");
/*      */         }
/*      */         
/*      */       }
/*  557 */     };
/*  558 */     this.natStatus.addListener(8, lNAT);
/*      */     
/*  560 */     boolean bSpeedMenu = COConfigurationManager.getBooleanParameter("GUI_SWT_bOldSpeedMenu");
/*      */     
/*  562 */     if (bSpeedMenu)
/*      */     {
/*  564 */       final Menu menuUpSpeed = new Menu(this.statusBar.getShell(), 8);
/*  565 */       menuUpSpeed.addListener(22, new Listener() {
/*      */         public void handleEvent(Event e) {
/*  567 */           if (!AzureusCoreFactory.isCoreRunning()) {
/*  568 */             return;
/*      */           }
/*  570 */           AzureusCore core = AzureusCoreFactory.getSingleton();
/*  571 */           GlobalManager globalManager = core.getGlobalManager();
/*      */           
/*  573 */           SelectableSpeedMenu.generateMenuItems(menuUpSpeed, core, globalManager, true);
/*      */         }
/*      */         
/*  576 */       });
/*  577 */       this.statusUp.setMenu(menuUpSpeed);
/*      */     }
/*      */     else {
/*  580 */       this.statusUp.addMouseListener(new MouseAdapter() {
/*      */         public void mouseDown(MouseEvent e) {
/*  582 */           if ((e.button != 3) && ((e.button != 1) || (e.stateMask != 262144))) {
/*  583 */             return;
/*      */           }
/*  585 */           Event event = new Event();
/*  586 */           event.type = 4;
/*  587 */           event.widget = e.widget;
/*  588 */           event.stateMask = e.stateMask;
/*  589 */           event.button = e.button;
/*  590 */           e.widget.getDisplay().post(event);
/*      */           
/*  592 */           CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*      */             public void azureusCoreRunning(AzureusCore core) {
/*  594 */               SelectableSpeedMenu.invokeSlider(MainStatusBar.this.statusUp, core, true);
/*      */             }
/*      */           });
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  601 */     if (bSpeedMenu) {
/*  602 */       final Menu menuDownSpeed = new Menu(this.statusBar.getShell(), 8);
/*  603 */       menuDownSpeed.addListener(22, new Listener() {
/*      */         public void handleEvent(Event e) {
/*  605 */           if (!AzureusCoreFactory.isCoreRunning()) {
/*  606 */             return;
/*      */           }
/*  608 */           AzureusCore core = AzureusCoreFactory.getSingleton();
/*  609 */           GlobalManager globalManager = core.getGlobalManager();
/*      */           
/*  611 */           SelectableSpeedMenu.generateMenuItems(menuDownSpeed, core, globalManager, false);
/*      */         }
/*      */         
/*  614 */       });
/*  615 */       this.statusDown.setMenu(menuDownSpeed);
/*      */     } else {
/*  617 */       this.statusDown.addMouseListener(new MouseAdapter() {
/*      */         public void mouseDown(MouseEvent e) {
/*  619 */           if ((e.button != 3) && ((e.button != 1) || (e.stateMask != 262144))) {
/*  620 */             return;
/*      */           }
/*  622 */           Event event = new Event();
/*  623 */           event.type = 4;
/*  624 */           event.widget = e.widget;
/*  625 */           event.stateMask = e.stateMask;
/*  626 */           event.button = e.button;
/*  627 */           e.widget.getDisplay().post(event);
/*      */           
/*  629 */           CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*      */             public void azureusCoreRunning(AzureusCore core) {
/*  631 */               SelectableSpeedMenu.invokeSlider(MainStatusBar.this.statusDown, core, false);
/*      */             }
/*      */           });
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  638 */     this.statusWarnings = new CLabelPadding(this.statusBar, 32);
/*  639 */     this.warningIcon = imageLoader.getImage("image.sidebar.vitality.alert");
/*  640 */     this.warningGreyIcon = imageLoader.getImage("image.sidebar.vitality.alert-gray");
/*  641 */     this.infoIcon = imageLoader.getImage("image.sidebar.vitality.info");
/*  642 */     updateStatusWarnings(null, false);
/*  643 */     Messages.setLanguageText(this.statusWarnings, "MainWindow.status.warning.tooltip");
/*      */     
/*  645 */     Alerts.addMessageHistoryListener(new org.gudy.azureus2.ui.swt.Alerts.AlertHistoryListener() {
/*      */       public void alertHistoryAdded(LogAlert alert) {
/*  647 */         MainStatusBar.this.updateStatusWarnings(alert, true);
/*      */       }
/*      */       
/*  650 */       public void alertHistoryRemoved(LogAlert alert) { MainStatusBar.this.updateStatusWarnings(alert, false);
/*      */       }
/*  652 */     });
/*  653 */     this.statusWarnings.addMouseListener(new org.eclipse.swt.events.MouseListener() {
/*      */       public void mouseUp(MouseEvent e) {
/*  655 */         if (e.button != 1) {
/*  656 */           return;
/*      */         }
/*  658 */         if (SystemWarningWindow.numWarningWindowsOpen > 0) {
/*  659 */           return;
/*      */         }
/*  661 */         ArrayList<LogAlert> alerts = Alerts.getUnviewedLogAlerts();
/*  662 */         if (alerts.size() == 0) {
/*  663 */           return;
/*      */         }
/*      */         
/*  666 */         Shell shell = MainStatusBar.this.statusWarnings.getShell();
/*  667 */         Rectangle bounds = MainStatusBar.this.statusWarnings.getClientArea();
/*  668 */         Point ptBottomRight = MainStatusBar.this.statusWarnings.toDisplay(bounds.x + bounds.width, bounds.y);
/*  669 */         new SystemWarningWindow((LogAlert)alerts.get(0), ptBottomRight, shell, 0);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void mouseDown(MouseEvent e) {}
/*      */       
/*      */ 
/*      */       public void mouseDoubleClick(MouseEvent e) {}
/*  678 */     });
/*  679 */     Menu menuStatusWarnings = new Menu(this.statusBar.getShell(), 8);
/*  680 */     this.statusWarnings.setMenu(menuStatusWarnings);
/*  681 */     final MenuItem dismissAllItem = new MenuItem(menuStatusWarnings, 8);
/*  682 */     menuStatusWarnings.addListener(22, new Listener() {
/*      */       public void handleEvent(Event e) {
/*  684 */         dismissAllItem.setEnabled(Alerts.getUnviewedLogAlerts().size() > 0);
/*      */       }
/*      */       
/*  687 */     });
/*  688 */     Messages.setLanguageText(dismissAllItem, "label.dismiss.all");
/*  689 */     dismissAllItem.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  691 */         ArrayList<LogAlert> alerts = Alerts.getUnviewedLogAlerts();
/*      */         
/*  693 */         for (LogAlert a : alerts)
/*      */         {
/*  695 */           Alerts.markAlertAsViewed(a);
/*      */         }
/*      */       }
/*  698 */     });
/*  699 */     COConfigurationManager.addAndFireParameterListener("status.rategraphs", new ParameterListener()
/*      */     {
/*      */       public void parameterChanged(String parameterName) {
/*  702 */         Utils.execSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  704 */             boolean doRateGraphs = COConfigurationManager.getBooleanParameter("status.rategraphs");
/*  705 */             if (doRateGraphs) {
/*  706 */               if ((MainStatusBar.this.imgRec == null) || (MainStatusBar.this.imgRec.isDisposed())) {
/*  707 */                 MainStatusBar.this.imgRec = new Image(MainStatusBar.this.display, 100, 20);
/*  708 */                 GC gc = new GC(MainStatusBar.this.imgRec);
/*  709 */                 gc.setBackground(MainStatusBar.this.statusDown.getBackground());
/*  710 */                 gc.fillRectangle(0, 0, 100, 20);
/*  711 */                 gc.dispose();
/*  712 */                 MainStatusBar.this.statusDown.setBackgroundImage(MainStatusBar.this.imgRec);
/*      */               }
/*      */               
/*  715 */               if ((MainStatusBar.this.imgSent == null) || (MainStatusBar.this.imgSent.isDisposed())) {
/*  716 */                 MainStatusBar.this.imgSent = new Image(MainStatusBar.this.display, 100, 20);
/*  717 */                 GC gc = new GC(MainStatusBar.this.imgSent);
/*  718 */                 gc.setBackground(MainStatusBar.this.statusUp.getBackground());
/*  719 */                 gc.fillRectangle(0, 0, 100, 20);
/*  720 */                 gc.dispose();
/*  721 */                 MainStatusBar.this.statusUp.setBackgroundImage(MainStatusBar.this.imgSent);
/*      */               }
/*      */             } else {
/*  724 */               MainStatusBar.this.statusUp.setBackgroundImage(null);
/*  725 */               MainStatusBar.this.statusDown.setBackgroundImage(null);
/*  726 */               Utils.disposeSWTObjects(new Object[] { MainStatusBar.this.imgRec, MainStatusBar.this.imgSent });
/*  727 */               MainStatusBar.this.imgRec = MainStatusBar.access$1702(MainStatusBar.this, null);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */ 
/*      */         });
/*      */       }
/*  735 */     });
/*  736 */     this.PRManager.addListener(new ProgressListener(null));
/*      */     
/*  738 */     this.uiFunctions.getUIUpdater().addUpdater(this);
/*      */     
/*      */ 
/*  741 */     this.this_mon.enter();
/*      */     ArrayList<Runnable> list;
/*  743 */     try { list = this.listRunAfterInit;
/*  744 */       this.listRunAfterInit = null;
/*      */     } finally {
/*  746 */       this.this_mon.exit();
/*      */     }
/*  748 */     for (Runnable runnable : list) {
/*      */       try {
/*  750 */         runnable.run();
/*      */       } catch (Exception e) {
/*  752 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*  756 */     this.statusBar.layout(true);
/*      */     
/*  758 */     return this.statusBar;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateStatusWarnings(final LogAlert current_alert, final boolean current_added)
/*      */   {
/*  767 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  769 */         if ((MainStatusBar.this.statusWarnings == null) || (MainStatusBar.this.statusWarnings.isDisposed())) {
/*  770 */           return;
/*      */         }
/*      */         
/*  773 */         ArrayList<LogAlert> alerts = Alerts.getUnviewedLogAlerts();
/*  774 */         int count = alerts.size();
/*      */         
/*  776 */         Image icon = MainStatusBar.this.infoIcon;
/*      */         
/*  778 */         for (LogAlert alert : alerts) {
/*  779 */           int type = alert.getType();
/*      */           
/*  781 */           if ((type == 3) || (type == 2))
/*      */           {
/*  783 */             icon = MainStatusBar.this.warningIcon;
/*      */             
/*  785 */             break;
/*      */           }
/*      */         }
/*      */         
/*  789 */         if (MainStatusBar.this.statusWarnings.getImage() != icon) {
/*  790 */           MainStatusBar.this.statusWarnings.setImage(icon);
/*      */         }
/*      */         
/*  793 */         MainStatusBar.this.statusWarnings.setVisible(count > 0);
/*  794 */         MainStatusBar.this.statusWarnings.setText("" + count);
/*  795 */         MainStatusBar.this.statusWarnings.layoutNow();
/*      */         
/*  797 */         if (current_added)
/*      */         {
/*  799 */           MainStatusBar.this.alert_flash_activate = true;
/*      */           
/*  801 */           if (current_alert.getType() != 1)
/*      */           {
/*  803 */             MainStatusBar.this.alert_flasher_event_start_time = SystemTime.getMonotonousTime();
/*      */             
/*  805 */             if (MainStatusBar.this.alert_flasher_event == null)
/*      */             {
/*  807 */               MainStatusBar.this.alert_flasher_event = org.gudy.azureus2.core3.util.SimpleTimer.addPeriodicEvent("MSB:alertFlasher", 500L, new org.gudy.azureus2.core3.util.TimerEventPerformer()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  813 */                 private long last_tick_time = -1L;
/*      */                 
/*      */ 
/*      */ 
/*      */                 public void perform(TimerEvent event)
/*      */                 {
/*  819 */                   Utils.execSWTThread(new AERunnable()
/*      */                   {
/*      */ 
/*      */                     public void runSupport()
/*      */                     {
/*      */ 
/*  825 */                       long now = SystemTime.getMonotonousTime();
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/*  830 */                       if ((MainStatusBar.25.1.this.last_tick_time != -1L) && (now - MainStatusBar.25.1.this.last_tick_time < 400L))
/*      */                       {
/*      */ 
/*  833 */                         return;
/*      */                       }
/*      */                       
/*  836 */                       MainStatusBar.25.1.this.last_tick_time = now;
/*      */                       
/*      */ 
/*      */ 
/*  840 */                       if ((MainStatusBar.this.statusWarnings == null) || (MainStatusBar.this.statusWarnings.isDisposed()) || (MainStatusBar.this.alert_flasher_event == null) || (!MainStatusBar.this.alert_flash_activate))
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/*  845 */                         if (MainStatusBar.this.alert_flasher_event != null)
/*      */                         {
/*  847 */                           MainStatusBar.this.alert_flasher_event.cancel();
/*      */                           
/*  849 */                           MainStatusBar.this.alert_flasher_event = null;
/*      */                         }
/*      */                         
/*  852 */                         return;
/*      */                       }
/*      */                       
/*  855 */                       Image current_icon = MainStatusBar.this.statusWarnings.getImage();
/*      */                       
/*  857 */                       if ((now > MainStatusBar.this.alert_flasher_event_start_time + 15000L) && (current_icon == MainStatusBar.this.warningIcon))
/*      */                       {
/*      */ 
/*  860 */                         MainStatusBar.this.alert_flasher_event.cancel();
/*      */                         
/*  862 */                         MainStatusBar.this.alert_flasher_event = null;
/*      */                         
/*  864 */                         return;
/*      */                       }
/*      */                       
/*  867 */                       Image target_icon = current_icon == MainStatusBar.this.warningIcon ? MainStatusBar.this.warningGreyIcon : MainStatusBar.this.warningIcon;
/*      */                       
/*  869 */                       MainStatusBar.this.statusWarnings.setImage(target_icon);
/*      */                     }
/*      */                   });
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  878 */           MainStatusBar.this.alert_flash_activate = false;
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public void relayout()
/*      */   {
/*  887 */     this.parent.layout(true, true);
/*      */   }
/*      */   
/*      */   private void addFeedBack() {
/*  891 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/*  893 */         Utils.execSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  895 */             MainStatusBar.this._addFeedBack();
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void _addFeedBack()
/*      */   {
/*  909 */     OverallStats stats = StatsFactory.getStats();
/*      */     
/*  911 */     long secs_uptime = stats.getTotalUpTime();
/*      */     
/*  913 */     long last_uptime = COConfigurationManager.getLongParameter("statusbar.feedback.uptime", 0L);
/*      */     
/*      */ 
/*  916 */     if (last_uptime == 0L)
/*      */     {
/*  918 */       COConfigurationManager.setParameter("statusbar.feedback.uptime", secs_uptime);
/*      */ 
/*      */     }
/*  921 */     else if (secs_uptime - last_uptime > 900L)
/*      */     {
/*  923 */       createStatusEntry(new IMainStatusBar.CLabelUpdater() {
/*      */         public boolean update(MainStatusBar.CLabelPadding label) {
/*  925 */           return false;
/*      */         }
/*      */         
/*      */         public void created(MainStatusBar.CLabelPadding feedback) {
/*  929 */           feedback.setText(MessageText.getString("statusbar.feedback"));
/*      */           
/*  931 */           Listener feedback_listener = new Listener()
/*      */           {
/*      */             public void handleEvent(Event e) {
/*  934 */               String url = "feedback.start?" + Utils.getWidgetBGColorURLParam() + "&fromWeb=false&os.name=" + UrlUtils.encode(Constants.OSName) + "&os.version=" + UrlUtils.encode(System.getProperty("os.version")) + "&java.version=" + UrlUtils.encode(Constants.JAVA_VERSION);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  942 */               com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT.getUIFunctionsSWT().viewURL(url, null, 600, 520, true, false);
/*      */             }
/*      */             
/*      */ 
/*  946 */           };
/*  947 */           feedback.setToolTipText(MessageText.getString("statusbar.feedback.tooltip"));
/*  948 */           feedback.setCursor(MainStatusBar.this.display.getSystemCursor(21));
/*  949 */           feedback.setForeground(Colors.blue);
/*  950 */           feedback.addListener(4, feedback_listener);
/*  951 */           feedback.addListener(8, feedback_listener);
/*      */           
/*  953 */           feedback.setVisible(true);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void addRIP()
/*      */   {
/*  962 */     if (!COConfigurationManager.getBooleanParameter("Status Area Show RIP"))
/*      */     {
/*  964 */       return;
/*      */     }
/*      */     
/*  967 */     createStatusEntry(new IMainStatusBar.CLabelUpdater() {
/*      */       public boolean update(MainStatusBar.CLabelPadding label) {
/*  969 */         return false;
/*      */       }
/*      */       
/*      */       public void created(final MainStatusBar.CLabelPadding feedback) {
/*  973 */         feedback.setText(MessageText.getString("respect.ip"));
/*      */         
/*  975 */         final String url_str = MessageText.getString("respect.ip.url");
/*      */         
/*  977 */         Listener feedback_listener = new Listener()
/*      */         {
/*      */           public void handleEvent(Event e) {
/*  980 */             if ((e.type == 4) && (e.button != 1)) {
/*  981 */               return;
/*      */             }
/*      */             try
/*      */             {
/*  985 */               Utils.launch(new java.net.URL(url_str));
/*      */             }
/*      */             catch (Throwable f)
/*      */             {
/*  989 */               Debug.out(f);
/*      */             }
/*      */             
/*      */           }
/*  993 */         };
/*  994 */         feedback.setData(url_str);
/*      */         
/*  996 */         Menu menu = new Menu(feedback.getShell(), 8);
/*      */         
/*  998 */         feedback.setMenu(menu);
/*      */         
/* 1000 */         MenuItem item = new MenuItem(menu, 0);
/*      */         
/* 1002 */         item.setText(MessageText.getString("sharing.progress.hide"));
/*      */         
/* 1004 */         item.addSelectionListener(new SelectionAdapter()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void widgetSelected(SelectionEvent arg0)
/*      */           {
/*      */ 
/* 1011 */             COConfigurationManager.setParameter("Status Area Show RIP", false);
/*      */             
/* 1013 */             feedback.setVisible(false);
/*      */             
/* 1015 */             MainStatusBar.this.layoutPluginComposite();
/*      */ 
/*      */           }
/*      */           
/*      */ 
/* 1020 */         });
/* 1021 */         ClipboardCopy.addCopyToClipMenu(menu, url_str);
/*      */         
/* 1023 */         feedback.setToolTipText(url_str);
/*      */         
/* 1025 */         feedback.setCursor(MainStatusBar.this.display.getSystemCursor(21));
/* 1026 */         feedback.setForeground(Colors.blue);
/*      */         
/* 1028 */         feedback.addListener(4, feedback_listener);
/* 1029 */         feedback.addListener(8, feedback_listener);
/*      */         
/* 1031 */         feedback.setVisible(true);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addStatusBarMenu(Composite cSB)
/*      */   {
/* 1042 */     if (!Constants.isCVSVersion()) {
/* 1043 */       return;
/*      */     }
/* 1045 */     Menu menu = new Menu(cSB);
/* 1046 */     cSB.setMenu(menu);
/*      */     
/* 1048 */     MenuItem itemShow = new MenuItem(menu, 64);
/* 1049 */     itemShow.setText("Show");
/* 1050 */     Menu menuShow = new Menu(itemShow);
/* 1051 */     itemShow.setMenu(menuShow);
/*      */     
/* 1053 */     String[] statusAreaLangs = { "ConfigView.section.style.status.show_sr", "ConfigView.section.style.status.show_nat", "ConfigView.section.style.status.show_ddb", "ConfigView.section.style.status.show_ipf" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1059 */     String[] statusAreaConfig = { "Status Area Show SR", "Status Area Show NAT", "Status Area Show DDB", "Status Area Show IPF" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1066 */     for (int i = 0; i < statusAreaConfig.length; i++) {
/* 1067 */       final String configID = statusAreaConfig[i];
/* 1068 */       String langID = statusAreaLangs[i];
/*      */       
/* 1070 */       final MenuItem item = new MenuItem(menuShow, 32);
/* 1071 */       Messages.setLanguageText(item, langID);
/* 1072 */       item.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/* 1074 */           COConfigurationManager.setParameter(configID, !COConfigurationManager.getBooleanParameter(configID));
/*      */         }
/*      */         
/* 1077 */       });
/* 1078 */       menuShow.addListener(22, new Listener() {
/*      */         public void handleEvent(Event event) {
/* 1080 */           item.setSelection(COConfigurationManager.getBooleanParameter(configID));
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setStatusText(String keyedSentence)
/*      */   {
/* 1092 */     this.statusTextKey = (keyedSentence == null ? "" : keyedSentence);
/* 1093 */     setStatusImageKey(null);
/* 1094 */     this.clickListener = null;
/* 1095 */     if (this.statusTextKey.length() == 0) {
/* 1096 */       resetStatus();
/*      */     }
/*      */     
/* 1099 */     updateStatusText();
/*      */   }
/*      */   
/*      */   private void setStatusImageKey(String newStatusImageKey) {
/* 1103 */     if (("" + this.statusImageKey).equals("" + newStatusImageKey)) {
/* 1104 */       return;
/*      */     }
/* 1106 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 1107 */     if (this.statusImageKey != null) {
/* 1108 */       imageLoader.releaseImage(this.statusImageKey);
/*      */     }
/* 1110 */     this.statusImageKey = newStatusImageKey;
/* 1111 */     if (this.statusImageKey != null) {
/* 1112 */       this.statusImage = imageLoader.getImage(this.statusImageKey);
/*      */     } else {
/* 1114 */       this.statusImage = null;
/*      */     }
/*      */   }
/*      */   
/*      */   private void resetStatus() {
/* 1119 */     if (Constants.isCVSVersion()) {
/* 1120 */       this.statusTextKey = "MainWindow.status.unofficialversion (5.7.6.0)";
/*      */       
/* 1122 */       setStatusImageKey("sb_warning");
/* 1123 */     } else if ((!Constants.isOSX) && (COConfigurationManager.getStringParameter("ui").equals("az2"))) {
/* 1124 */       this.statusTextKey = (Constants.APP_NAME + " " + "5.7.6.0");
/* 1125 */       setStatusImageKey(null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setStatusText(int statustype, String string, UIStatusTextClickListener l)
/*      */   {
/* 1137 */     this.statusTextKey = (string == null ? "" : string);
/*      */     
/* 1139 */     if (this.statusTextKey.length() == 0) {
/* 1140 */       resetStatus();
/*      */     }
/*      */     
/* 1143 */     this.clickListener = l;
/* 1144 */     if (statustype == 1) {
/* 1145 */       setStatusImageKey("sb_warning");
/*      */     }
/* 1147 */     if (statustype == 1) {
/* 1148 */       setStatusImageKey("sb_warning");
/*      */     } else {
/* 1150 */       setStatusImageKey(null);
/*      */     }
/*      */     
/* 1153 */     updateStatusText();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updateStatusText()
/*      */   {
/* 1161 */     if ((this.display == null) || (this.display.isDisposed())) return;
/*      */     String text;
/*      */     final String text;
/* 1164 */     if (this.updateWindow != null) {
/* 1165 */       text = "MainWindow.updateavail";
/*      */     } else {
/* 1167 */       text = this.statusTextKey;
/*      */     }
/* 1169 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1171 */         if ((MainStatusBar.this.statusText != null) && (!MainStatusBar.this.statusText.isDisposed())) {
/* 1172 */           MainStatusBar.this.statusText.setText(MessageText.getStringForSentence(text));
/* 1173 */           MainStatusBar.this.statusText.setImage(MainStatusBar.this.statusImage);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void refreshStatusText()
/*      */   {
/* 1184 */     if ((this.statusText != null) && (!this.statusText.isDisposed())) {
/* 1185 */       this.statusText.update();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setUpdateNeeded(UpdateWindow updateWindow)
/*      */   {
/* 1193 */     this.updateWindow = updateWindow;
/* 1194 */     if (updateWindow != null) {
/* 1195 */       this.statusText.setCursor(this.display.getSystemCursor(21));
/* 1196 */       this.statusText.setForeground(Colors.colorWarning);
/* 1197 */       updateStatusText();
/*      */     } else {
/* 1199 */       this.statusText.setCursor(null);
/* 1200 */       this.statusText.setForeground(null);
/* 1201 */       updateStatusText();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/* 1207 */   boolean was_hidden = false;
/*      */   
/*      */   public void updateUI() {
/* 1210 */     updateUI(true);
/*      */   }
/*      */   
/*      */   public void updateUI(boolean is_visible) {
/* 1214 */     if (this.statusBar.isDisposed()) {
/* 1215 */       this.uiFunctions.getUIUpdater().removeUpdater(this);
/* 1216 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1221 */     boolean is_hidden = (!is_visible) || (this.statusBar.getDisplay().getFocusControl() == null);
/*      */     
/* 1223 */     if (is_hidden)
/*      */     {
/* 1225 */       this.was_hidden = true;
/*      */ 
/*      */ 
/*      */     }
/* 1229 */     else if (this.was_hidden)
/*      */     {
/* 1231 */       this.statusBar.layout(true, true);
/*      */       
/* 1233 */       this.was_hidden = false;
/*      */     }
/*      */     
/*      */ 
/* 1237 */     if (!is_visible)
/*      */     {
/* 1239 */       return;
/*      */     }
/*      */     
/*      */ 
/* 1243 */     Control[] plugin_elements = this.plugin_label_composite.getChildren();
/* 1244 */     for (int i = 0; i < plugin_elements.length; i++) {
/* 1245 */       if ((plugin_elements[i] instanceof UpdateableCLabel)) {
/* 1246 */         ((UpdateableCLabel)plugin_elements[i]).checkForRefresh();
/*      */       }
/*      */     }
/*      */     
/* 1250 */     if (this.ipBlocked.isVisible()) {
/* 1251 */       updateIPBlocked();
/*      */     }
/*      */     
/* 1254 */     if (this.srStatus.isVisible()) {
/* 1255 */       updateShareRatioStatus();
/*      */     }
/*      */     
/*      */ 
/* 1259 */     if (this.natStatus.isVisible()) {
/* 1260 */       updateNatStatus();
/*      */     }
/*      */     
/* 1263 */     if (this.dhtStatus.isVisible()) {
/* 1264 */       updateDHTStatus();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1269 */     if (AzureusCoreFactory.isCoreRunning()) {
/* 1270 */       AzureusCore core = AzureusCoreFactory.getSingleton();
/* 1271 */       GlobalManager gm = core.getGlobalManager();
/* 1272 */       GlobalManagerStats stats = gm.getStats();
/*      */       
/* 1274 */       int dl_limit = NetworkManager.getMaxDownloadRateBPS() / 1024;
/* 1275 */       long rec_data = stats.getDataReceiveRate();
/* 1276 */       long rec_prot = stats.getProtocolReceiveRate();
/*      */       
/* 1278 */       if ((this.last_dl_limit != dl_limit) || (this.last_rec_data != rec_data) || (this.last_rec_prot != rec_prot)) {
/* 1279 */         this.last_dl_limit = dl_limit;
/* 1280 */         this.last_rec_data = rec_data;
/* 1281 */         this.last_rec_prot = rec_prot;
/*      */         
/* 1283 */         this.statusDown.setText((dl_limit == 0 ? "" : new StringBuilder().append("[").append(dl_limit).append("K] ").toString()) + DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(rec_data, rec_prot));
/*      */       }
/*      */       
/*      */ 
/* 1287 */       boolean auto_up = (TransferSpeedValidator.isAutoSpeedActive(gm)) && (TransferSpeedValidator.isAutoUploadAvailable(core));
/*      */       
/*      */ 
/* 1290 */       int ul_limit_norm = NetworkManager.getMaxUploadRateBPSNormal() / 1024;
/*      */       String seeding_only;
/*      */       String seeding_only;
/* 1293 */       if (NetworkManager.isSeedingOnlyUploadRate()) {
/* 1294 */         int ul_limit_seed = NetworkManager.getMaxUploadRateBPSSeedingOnly() / 1024;
/* 1295 */         String seeding_only; if (ul_limit_seed == 0) {
/* 1296 */           seeding_only = "+∞K";
/*      */         } else {
/* 1298 */           int diff = ul_limit_seed - ul_limit_norm;
/* 1299 */           seeding_only = (diff >= 0 ? "+" : "") + diff + "K";
/*      */         }
/*      */       } else {
/* 1302 */         seeding_only = "";
/*      */       }
/*      */       
/* 1305 */       int sent_data = stats.getDataSendRate();
/* 1306 */       if ((this.imgRec != null) && (!this.imgRec.isDisposed())) {
/* 1307 */         updateGraph(this.statusDown, this.imgRec, rec_data, this.max_rec);
/* 1308 */         updateGraph(this.statusUp, this.imgSent, sent_data, this.max_sent);
/*      */       }
/*      */       
/*      */ 
/* 1312 */       this.statusUp.setText((ul_limit_norm == 0 ? "" : new StringBuilder().append("[").append(ul_limit_norm).append("K").append(seeding_only).append("]").toString()) + (auto_up ? "* " : " ") + DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(sent_data, stats.getProtocolSendRate()));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void updateGraph(CLabelPadding label, Image img, long newVal, long[] max)
/*      */   {
/* 1322 */     GC gc = new GC(img);
/*      */     try {
/* 1324 */       long val = newVal;
/* 1325 */       Rectangle bounds = img.getBounds();
/* 1326 */       int padding = 2;
/* 1327 */       int x = bounds.width - 2 - 2;
/* 1328 */       if (val > max[0]) {
/* 1329 */         int y = 20 - (int)(max[0] * 20L / val);
/* 1330 */         gc.setBackground(label.getBackground());
/* 1331 */         gc.fillRectangle(2, 0, x, y);
/*      */         
/* 1333 */         gc.copyArea(3, 0, x, 20, 2, y);
/* 1334 */         max[0] = val;
/*      */       } else {
/* 1336 */         gc.copyArea(3, 0, x, 20, 2, 0);
/*      */       }
/*      */       
/* 1339 */       gc.setForeground(label.getBackground());
/* 1340 */       int breakPoint = 20 - (max[0] == 0L ? 0 : (int)(val * 20L / max[0]));
/*      */       
/* 1342 */       gc.drawLine(x, 0, x, breakPoint);
/* 1343 */       gc.setForeground(Colors.blues[5]);
/* 1344 */       gc.drawLine(x, breakPoint, x, 20);
/*      */     } finally {
/* 1346 */       gc.dispose();
/*      */     }
/* 1348 */     label.redraw();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void updateDHTStatus()
/*      */   {
/* 1357 */     if (this.dhtPlugin == null) {
/* 1358 */       return;
/*      */     }
/*      */     
/* 1361 */     int dht_status = this.dhtPlugin.getStatus();
/* 1362 */     long dht_count = -1L;
/*      */     
/* 1364 */     if (dht_status == 3) {
/* 1365 */       DHT[] dhts = this.dhtPlugin.getDHTs();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1370 */       dht_count = dhts[0].getControl().getStats().getEstimatedDHTSize();
/*      */     }
/*      */     
/*      */ 
/* 1374 */     if ((this.lastDHTstatus != dht_status) || (this.lastDHTcount != dht_count)) {
/* 1375 */       boolean hasImage = this.dhtStatus.getImage() != null;
/* 1376 */       boolean needImage = true;
/* 1377 */       switch (dht_status)
/*      */       {
/*      */       case 3: 
/* 1380 */         this.dhtStatus.setToolTipText(MessageText.getString("MainWindow.dht.status.tooltip"));
/* 1381 */         this.dhtStatus.setText(MessageText.getString("MainWindow.dht.status.users").replaceAll("%1", this.numberFormat.format(dht_count)));
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
/* 1398 */         break;
/*      */       
/*      */ 
/*      */       case 1: 
/* 1402 */         this.dhtStatus.setText(MessageText.getString("MainWindow.dht.status.disabled"));
/* 1403 */         break;
/*      */       
/*      */ 
/*      */       case 2: 
/* 1407 */         this.dhtStatus.setText(MessageText.getString("MainWindow.dht.status.initializing"));
/* 1408 */         break;
/*      */       
/*      */ 
/*      */       case 4: 
/* 1412 */         this.dhtStatus.setText(MessageText.getString("MainWindow.dht.status.failed"));
/* 1413 */         break;
/*      */       
/*      */       default: 
/* 1416 */         needImage = false;
/*      */       }
/*      */       
/*      */       
/* 1420 */       if (hasImage != needImage) {
/* 1421 */         ImageLoader imageLoader = ImageLoader.getInstance();
/* 1422 */         if (needImage) {
/* 1423 */           Image img = imageLoader.getImage("sb_count");
/* 1424 */           this.dhtStatus.setImage(img);
/*      */         } else {
/* 1426 */           imageLoader.releaseImage("sb_count");
/* 1427 */           this.dhtStatus.setImage(null);
/*      */         }
/*      */       }
/* 1430 */       this.lastDHTstatus = dht_status;
/* 1431 */       this.lastDHTcount = dht_count;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void updateNatStatus()
/*      */   {
/* 1442 */     if (this.connection_manager == null) {
/* 1443 */       return;
/*      */     }
/*      */     
/* 1446 */     int nat_status = this.connection_manager.getNATStatus();
/*      */     
/* 1448 */     if (this.lastNATstatus != nat_status)
/*      */     {
/*      */       String imgID;
/*      */       String tooltipID;
/*      */       String statusID;
/* 1453 */       switch (nat_status) {
/*      */       case 0: 
/* 1455 */         imgID = "grayled";
/* 1456 */         tooltipID = "MainWindow.nat.status.tooltip.unknown";
/* 1457 */         statusID = "MainWindow.nat.status.unknown";
/* 1458 */         break;
/*      */       
/*      */       case 1: 
/* 1461 */         imgID = "greenled";
/* 1462 */         tooltipID = "MainWindow.nat.status.tooltip.ok";
/* 1463 */         statusID = "MainWindow.nat.status.ok";
/* 1464 */         break;
/*      */       
/*      */       case 2: 
/* 1467 */         imgID = "yellowled";
/* 1468 */         tooltipID = "MainWindow.nat.status.tooltip.probok";
/* 1469 */         statusID = "MainWindow.nat.status.probok";
/* 1470 */         break;
/*      */       
/*      */       default: 
/* 1473 */         imgID = "redled";
/* 1474 */         tooltipID = "MainWindow.nat.status.tooltip.bad";
/* 1475 */         statusID = "MainWindow.nat.status.bad";
/*      */       }
/*      */       
/*      */       
/* 1479 */       if (!imgID.equals(this.lastNATimageID)) {
/* 1480 */         ImageLoader imageLoader = ImageLoader.getInstance();
/* 1481 */         this.natStatus.setImage(imageLoader.getImage(imgID));
/*      */         
/* 1483 */         if (this.lastNATimageID != null) {
/* 1484 */           imageLoader.releaseImage(this.lastNATimageID);
/*      */         }
/* 1486 */         this.lastNATimageID = imgID;
/*      */       }
/*      */       
/* 1489 */       this.natStatus.setToolTipText(MessageText.getString(tooltipID));
/* 1490 */       this.natStatus.setText(MessageText.getString(statusID));
/* 1491 */       this.lastNATstatus = nat_status;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void updateShareRatioStatus()
/*      */   {
/* 1503 */     if (this.overall_stats == null) {
/* 1504 */       this.overall_stats = StatsFactory.getStats();
/*      */       
/* 1506 */       if (this.overall_stats == null) {
/* 1507 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1511 */     long ratio = 1000L * this.overall_stats.getUploadedBytes() / (this.overall_stats.getDownloadedBytes() + 1L);
/*      */     
/*      */     int sr_status;
/*      */     int sr_status;
/* 1515 */     if (ratio < 500L)
/*      */     {
/* 1517 */       sr_status = 0;
/*      */     } else { int sr_status;
/* 1519 */       if (ratio < 900L)
/*      */       {
/* 1521 */         sr_status = 1;
/*      */       }
/*      */       else
/*      */       {
/* 1525 */         sr_status = 2;
/*      */       }
/*      */     }
/* 1528 */     if (sr_status != this.last_sr_status)
/*      */     {
/*      */       String imgID;
/*      */       
/* 1532 */       switch (sr_status) {
/*      */       case 2: 
/* 1534 */         imgID = "greenled";
/* 1535 */         break;
/*      */       
/*      */       case 1: 
/* 1538 */         imgID = "yellowled";
/* 1539 */         break;
/*      */       
/*      */       default: 
/* 1542 */         imgID = "redled";
/*      */       }
/*      */       
/*      */       
/* 1546 */       if (!imgID.equals(this.lastSRimageID)) {
/* 1547 */         ImageLoader imageLoader = ImageLoader.getInstance();
/* 1548 */         this.srStatus.setImage(imageLoader.getImage(imgID));
/* 1549 */         if (this.lastSRimageID != null) {
/* 1550 */           imageLoader.releaseImage(this.lastSRimageID);
/*      */         }
/* 1552 */         this.lastSRimageID = imgID;
/*      */       }
/*      */       
/* 1555 */       this.last_sr_status = sr_status;
/*      */     }
/*      */     
/* 1558 */     if (ratio != this.last_sr_ratio)
/*      */     {
/*      */       String tooltipID;
/*      */       
/* 1562 */       switch (sr_status) {
/*      */       case 2: 
/* 1564 */         tooltipID = "MainWindow.sr.status.tooltip.ok";
/* 1565 */         break;
/*      */       
/*      */       case 1: 
/* 1568 */         tooltipID = "MainWindow.sr.status.tooltip.poor";
/* 1569 */         break;
/*      */       
/*      */       default: 
/* 1572 */         tooltipID = "MainWindow.sr.status.tooltip.bad";
/*      */       }
/*      */       
/*      */       
/* 1576 */       String ratio_str = "";
/*      */       
/* 1578 */       String partial = "" + ratio % 1000L;
/*      */       
/* 1580 */       while (partial.length() < 3)
/*      */       {
/* 1582 */         partial = "0" + partial;
/*      */       }
/*      */       
/* 1585 */       ratio_str = ratio / 1000L + "." + partial;
/*      */       
/* 1587 */       this.srStatus.setToolTipText(MessageText.getString(tooltipID, new String[] { ratio_str }));
/*      */       
/*      */ 
/*      */ 
/* 1591 */       this.last_sr_ratio = ratio;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void updateIPBlocked()
/*      */   {
/* 1601 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 1602 */       return;
/*      */     }
/*      */     
/* 1605 */     AzureusCore azureusCore = AzureusCoreFactory.getSingleton();
/*      */     
/*      */ 
/* 1608 */     IpFilter ip_filter = azureusCore.getIpFilterManager().getIPFilter();
/*      */     
/* 1610 */     this.ipBlocked.setForeground(this.display.getSystemColor(ip_filter.isEnabled() ? 21 : 18));
/*      */     
/* 1612 */     this.ipBlocked.setText("IPs: " + this.numberFormat.format(ip_filter.getNbRanges()) + " - " + this.numberFormat.format(ip_filter.getNbIpsBlockedAndLoggable()) + "/" + this.numberFormat.format(ip_filter.getNbBannedIps()) + "/" + this.numberFormat.format(azureusCore.getIpFilterManager().getBadIps().getNbBadIps()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1621 */     this.ipBlocked.setToolTipText(MessageText.getString("MainWindow.IPs.tooltip", new String[] { ip_filter.isEnabled() ? DisplayFormatters.formatDateShort(ip_filter.getLastUpdateTime()) : MessageText.getString("ipfilter.disabled") }));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDebugInfo(String string)
/*      */   {
/* 1632 */     if ((this.statusText != null) && (!this.statusText.isDisposed()))
/* 1633 */       this.statusText.setToolTipText(string);
/*      */   }
/*      */   
/*      */   public boolean isMouseOver() {
/* 1637 */     if ((this.statusText == null) || (this.statusText.isDisposed())) {
/* 1638 */       return false;
/*      */     }
/* 1640 */     return this.statusText.getDisplay().getCursorControl() == this.statusText;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public class CLabelPadding
/*      */     extends Canvas
/*      */     implements org.eclipse.swt.events.PaintListener
/*      */   {
/* 1654 */     private int lastWidth = 0;
/*      */     
/* 1656 */     private long widthSetOn = 0L;
/*      */     
/*      */     private static final int KEEPWIDTHFOR_MS = 30000;
/*      */     
/* 1660 */     private String text = "";
/*      */     
/*      */ 
/*      */     private String tooltip_text;
/*      */     
/*      */ 
/*      */     private boolean hovering;
/*      */     
/*      */ 
/*      */     private Image image;
/*      */     
/*      */     private Image bgImage;
/*      */     
/*      */ 
/*      */     public CLabelPadding(Composite parent, int style)
/*      */     {
/* 1676 */       super(style | 0x20000000);
/*      */       
/* 1678 */       GridData gridData = new GridData(80);
/*      */       
/* 1680 */       setLayoutData(gridData);
/* 1681 */       setForeground(parent.getForeground());
/*      */       
/* 1683 */       addPaintListener(this);
/*      */       
/* 1685 */       addMouseTrackListener(new org.eclipse.swt.events.MouseTrackAdapter()
/*      */       {
/*      */ 
/*      */         public void mouseEnter(MouseEvent e)
/*      */         {
/*      */ 
/* 1691 */           MainStatusBar.CLabelPadding.this.hovering = true;
/*      */         }
/*      */         
/*      */ 
/*      */         public void mouseExit(MouseEvent e)
/*      */         {
/* 1697 */           MainStatusBar.CLabelPadding.this.hovering = false;
/*      */         }
/*      */         
/*      */         public void mouseHover(MouseEvent e) {
/* 1701 */           String existing = MainStatusBar.CLabelPadding.this.getToolTipText();
/* 1702 */           if ((existing == null) || (!existing.equals(MainStatusBar.CLabelPadding.this.tooltip_text))) {
/* 1703 */             MainStatusBar.CLabelPadding.this.setToolTipText(MainStatusBar.CLabelPadding.this.tooltip_text);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setToolTipText(String str)
/*      */     {
/* 1713 */       if (str == this.tooltip_text) {
/* 1714 */         return;
/*      */       }
/* 1716 */       if ((str != null) && (this.tooltip_text != null) && (str.equals(this.tooltip_text))) {
/* 1717 */         return;
/*      */       }
/*      */       
/* 1720 */       this.tooltip_text = str;
/*      */       
/* 1722 */       if (this.hovering)
/*      */       {
/* 1724 */         super.setToolTipText(str);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getToolTipText()
/*      */     {
/* 1732 */       return this.tooltip_text;
/*      */     }
/*      */     
/*      */     public void paintControl(PaintEvent e) {
/* 1736 */       Point size = getSize();
/* 1737 */       e.gc.setAdvanced(true);
/* 1738 */       if ((this.bgImage != null) && (!this.bgImage.isDisposed())) {
/* 1739 */         Rectangle bounds = this.bgImage.getBounds();
/* 1740 */         if (MainStatusBar.this.display.getCursorControl() != this) {
/* 1741 */           e.gc.setAlpha(100);
/*      */         }
/* 1743 */         e.gc.drawImage(this.bgImage, 0, 0, bounds.width, bounds.height, 0, 2, size.x, size.y - 4);
/*      */         
/* 1745 */         e.gc.setAlpha(255);
/*      */       }
/* 1747 */       Rectangle clientArea = getClientArea();
/*      */       
/*      */ 
/* 1750 */       Image image = getImage();
/* 1751 */       Rectangle imageBounds = null;
/* 1752 */       if ((image != null) && (!image.isDisposed())) {
/* 1753 */         imageBounds = image.getBounds();
/*      */       }
/* 1755 */       GCStringPrinter sp = new GCStringPrinter(e.gc, getText(), clientArea, true, true, 16777216);
/*      */       
/* 1757 */       sp.calculateMetrics();
/*      */       
/* 1759 */       if (sp.hasHitUrl()) {
/* 1760 */         GCStringPrinter.URLInfo[] hitUrlInfo = sp.getHitUrlInfo();
/* 1761 */         for (int i = 0; i < hitUrlInfo.length; i++) {
/* 1762 */           GCStringPrinter.URLInfo info = hitUrlInfo[i];
/* 1763 */           info.urlUnderline = true;
/*      */         }
/*      */       }
/*      */       
/* 1767 */       Point textSize = sp.getCalculatedSize();
/*      */       
/* 1769 */       if (imageBounds != null) {
/* 1770 */         int pad = 2;
/* 1771 */         int ofs = imageBounds.width + imageBounds.x;
/* 1772 */         int xStartImage = (clientArea.width - textSize.x - ofs - pad) / 2;
/* 1773 */         e.gc.drawImage(image, xStartImage, clientArea.height / 2 - imageBounds.height / 2);
/*      */         
/* 1775 */         clientArea.x += xStartImage + ofs + pad;
/* 1776 */         clientArea.width -= xStartImage + ofs + pad;
/*      */       } else {
/* 1778 */         int ofs = clientArea.width / 2 - textSize.x / 2;
/* 1779 */         clientArea.x += ofs;
/* 1780 */         clientArea.width -= ofs;
/*      */       }
/* 1782 */       sp.printString(e.gc, clientArea, 16384);
/*      */       
/* 1784 */       int x = clientArea.x + clientArea.width - 1;
/* 1785 */       e.gc.setAlpha(20);
/* 1786 */       e.gc.drawLine(x, 3, x, clientArea.height - 3);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Point computeSize(int wHint, int hHint)
/*      */     {
/* 1793 */       return computeSize(wHint, hHint, true);
/*      */     }
/*      */     
/*      */     public Point computeSize(int wHint, int hHint, boolean changed) {
/*      */       try {
/* 1798 */         return computeSize(wHint, hHint, changed, false);
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/* 1802 */         Debug.out("Error while computing size for CLabel with text:" + getText() + "; " + t.toString());
/*      */       }
/* 1804 */       return new Point(0, 0);
/*      */     }
/*      */     
/*      */ 
/*      */     public Point computeSize(int wHint, int hHint, boolean changed, boolean realWidth)
/*      */     {
/* 1810 */       if (!isVisible()) {
/* 1811 */         return new Point(0, 0);
/*      */       }
/*      */       
/* 1814 */       if ((wHint != -1) && (hHint != -1)) {
/* 1815 */         return new Point(wHint, hHint);
/*      */       }
/* 1817 */       Point pt = new Point(wHint, hHint);
/*      */       
/* 1819 */       Point lastSize = new Point(0, 0);
/*      */       
/* 1821 */       Image image = getImage();
/* 1822 */       if ((image != null) && (!image.isDisposed())) {
/* 1823 */         Rectangle bounds = image.getBounds();
/* 1824 */         int ofs = bounds.width + bounds.x + 5;
/* 1825 */         lastSize.x += ofs;
/* 1826 */         lastSize.y = bounds.height;
/*      */       }
/*      */       
/* 1829 */       GC gc = new GC(this);
/* 1830 */       GCStringPrinter sp = new GCStringPrinter(gc, getText(), new Rectangle(0, 0, 10000, 20), true, true, 16384);
/*      */       
/* 1832 */       sp.calculateMetrics();
/* 1833 */       Point lastTextSize = sp.getCalculatedSize();
/* 1834 */       gc.dispose();
/*      */       
/* 1836 */       lastSize.x += lastTextSize.x + 10;
/* 1837 */       lastSize.y = Math.max(lastSize.y, lastTextSize.y);
/*      */       
/* 1839 */       if (wHint == -1) {
/* 1840 */         pt.x = lastSize.x;
/*      */       }
/* 1842 */       if (hHint == -1) {
/* 1843 */         pt.y = lastSize.y;
/*      */       }
/*      */       
/* 1846 */       if (!realWidth) {
/* 1847 */         long now = System.currentTimeMillis();
/* 1848 */         if ((this.lastWidth > pt.x) && (now - this.widthSetOn < 30000L)) {
/* 1849 */           pt.x = this.lastWidth;
/*      */         } else {
/* 1851 */           if (this.lastWidth != pt.x) {
/* 1852 */             this.lastWidth = pt.x;
/*      */           }
/* 1854 */           this.widthSetOn = now;
/*      */         }
/*      */       }
/*      */       
/* 1858 */       return pt;
/*      */     }
/*      */     
/*      */     public void setImage(Image image)
/*      */     {
/* 1863 */       this.image = image;
/*      */       
/* 1865 */       redraw();
/*      */     }
/*      */     
/*      */     public Image getImage() {
/* 1869 */       return this.image;
/*      */     }
/*      */     
/*      */     public void setBackgroundImage(Image image) {
/* 1873 */       this.bgImage = image;
/*      */       
/* 1875 */       redraw();
/*      */     }
/*      */     
/*      */     public Image getBackgroundImage() {
/* 1879 */       return this.bgImage;
/*      */     }
/*      */     
/*      */     public String getText() {
/* 1883 */       return this.text;
/*      */     }
/*      */     
/*      */     public void setText(String text) {
/* 1887 */       if (text == null) {
/* 1888 */         text = "";
/*      */       }
/* 1890 */       if (text.equals(getText())) {
/* 1891 */         return;
/*      */       }
/* 1893 */       this.text = text;
/* 1894 */       int oldWidth = this.lastWidth;
/* 1895 */       Point pt = computeSize(-1, -1, true, true);
/* 1896 */       if ((pt.x > oldWidth) && (text.length() > 0)) {
/* 1897 */         MainStatusBar.this.statusBar.layout();
/* 1898 */       } else if (pt.x < oldWidth) {
/* 1899 */         Utils.execSWTThreadLater(30000, new AERunnable() {
/*      */           public void runSupport() {
/* 1901 */             if ((MainStatusBar.this.statusBar == null) || (MainStatusBar.this.statusBar.isDisposed())) {
/* 1902 */               return;
/*      */             }
/* 1904 */             MainStatusBar.this.statusBar.layout();
/*      */           }
/*      */         });
/*      */       }
/* 1908 */       redraw();
/*      */     }
/*      */     
/*      */ 
/*      */     public void reset()
/*      */     {
/* 1914 */       this.widthSetOn = 0L;
/* 1915 */       this.lastWidth = 0;
/*      */     }
/*      */     
/*      */     public void layoutNow() {
/* 1919 */       this.widthSetOn = 0L;
/* 1920 */       MainStatusBar.this.statusBar.layout();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class UpdateableCLabel
/*      */     extends MainStatusBar.CLabelPadding
/*      */   {
/*      */     private IMainStatusBar.CLabelUpdater updater;
/*      */     
/*      */     public UpdateableCLabel(Composite parent, int style, IMainStatusBar.CLabelUpdater updater)
/*      */     {
/* 1932 */       super(parent, style);
/* 1933 */       this.updater = updater;
/*      */     }
/*      */     
/*      */     private void checkForRefresh() {
/* 1937 */       if (this.updater.update(this)) {
/* 1938 */         MainStatusBar.this.layoutPluginComposite();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void createStatusEntry(final IMainStatusBar.CLabelUpdater updater) {
/* 1944 */     AERunnable r = new AERunnable() {
/*      */       public void runSupport() {
/* 1946 */         MainStatusBar.UpdateableCLabel result = new MainStatusBar.UpdateableCLabel(MainStatusBar.this, MainStatusBar.this.plugin_label_composite, 32, updater);
/*      */         
/* 1948 */         Utils.setLayoutData(result, new GridData(1808));
/* 1949 */         MainStatusBar.this.layoutPluginComposite();
/* 1950 */         updater.created(result);
/*      */       }
/* 1952 */     };
/* 1953 */     this.this_mon.enter();
/*      */     try {
/* 1955 */       if (this.listRunAfterInit != null) {
/* 1956 */         this.listRunAfterInit.add(r); return;
/*      */       }
/*      */     }
/*      */     finally {
/* 1960 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1963 */     Utils.execSWTThread(r);
/*      */   }
/*      */   
/*      */ 
/*      */   private void layoutPluginComposite()
/*      */   {
/* 1969 */     Control[] plugin_elements = this.plugin_label_composite.getChildren();
/* 1970 */     for (int i = 0; i < plugin_elements.length; i++) {
/* 1971 */       if ((plugin_elements[i] instanceof UpdateableCLabel)) {
/* 1972 */         ((UpdateableCLabel)plugin_elements[i]).reset();
/*      */       }
/*      */     }
/* 1975 */     this.statusBar.layout();
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
/*      */   private void showProgressBar(boolean state)
/*      */   {
/* 1988 */     if ((state) && (!this.progressBar.isVisible())) {
/* 1989 */       this.progressGridData.widthHint = 100;
/* 1990 */       this.progressBar.setVisible(true);
/* 1991 */       this.statusBar.layout();
/* 1992 */     } else if ((!state) && (this.progressBar.isVisible())) {
/* 1993 */       this.progressBar.setVisible(false);
/* 1994 */       this.progressGridData.widthHint = 0;
/* 1995 */       this.statusBar.layout();
/*      */     }
/*      */   }
/*      */   
/*      */   public Rectangle getBounds()
/*      */   {
/* 2001 */     if (null != this.statusBar) {
/* 2002 */       return this.statusBar.getBounds();
/*      */     }
/* 2004 */     return null;
/*      */   }
/*      */   
/*      */   public String getUpdateUIName()
/*      */   {
/* 2009 */     return "MainStatusBar";
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
/*      */ 
/*      */ 
/*      */   private class ProgressListener
/*      */     implements org.gudy.azureus2.ui.swt.progress.IProgressReportingListener
/*      */   {
/* 2032 */     private String lastProgressImageID = null;
/*      */     
/* 2034 */     private Set<IProgressReporter> pending_updates = new java.util.HashSet();
/*      */     
/*      */ 
/*      */     private ProgressListener()
/*      */     {
/* 2039 */       Utils.execSWTThread(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 2045 */           MainStatusBar.ProgressListener.this.swt_setProgressImage();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void swt_updateProgressBarDisplay(IProgressReport pReport)
/*      */     {
/* 2054 */       if ((null == MainStatusBar.this.progressBar) || (MainStatusBar.this.progressBar.isDisposed()))
/*      */       {
/* 2056 */         return;
/*      */       }
/*      */       
/*      */ 
/* 2060 */       if (null != pReport)
/*      */       {
/*      */ 
/*      */ 
/* 2064 */         MainStatusBar.this.progressBar.setMinimum(pReport.getMinimum());
/* 2065 */         MainStatusBar.this.progressBar.setMaximum(pReport.getMaximum());
/* 2066 */         MainStatusBar.this.progressBar.setIndeterminate(pReport.isIndeterminate());
/* 2067 */         MainStatusBar.this.progressBar.setPercentage(pReport.getPercentage());
/* 2068 */         MainStatusBar.this.showProgressBar(true);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2073 */         if (MainStatusBar.this.isAZ3) {
/* 2074 */           MainStatusBar.this.statusText.setText(pReport.getName());
/*      */         } else {
/* 2076 */           MainStatusBar.this.setStatusText(pReport.getName());
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 2084 */         MainStatusBar.this.showProgressBar(false);
/*      */         
/* 2086 */         if (MainStatusBar.this.isAZ3) {
/* 2087 */           MainStatusBar.this.statusText.setText("");
/*      */         } else {
/* 2089 */           MainStatusBar.this.setStatusText(null);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     private void swt_setProgressImage()
/*      */     {
/* 2096 */       if (MainStatusBar.this.progressViewerImageLabel.isDisposed()) {
/*      */         return;
/*      */       }
/*      */       
/*      */       String imageID;
/*      */       String imageID;
/* 2102 */       if (MainStatusBar.this.PRManager.getReporterCount(2) > 0) {
/* 2103 */         imageID = "progress_error"; } else { String imageID;
/* 2104 */         if (MainStatusBar.this.PRManager.getReporterCount(0) > 0) {
/* 2105 */           imageID = "progress_info";
/*      */         } else {
/* 2107 */           imageID = "progress_viewer";
/*      */         }
/*      */       }
/* 2110 */       if (!imageID.equals(this.lastProgressImageID))
/*      */       {
/* 2112 */         ImageLoader imageLoader = ImageLoader.getInstance();
/* 2113 */         MainStatusBar.this.progressViewerImageLabel.setImage(imageLoader.getImage(imageID));
/* 2114 */         if (this.lastProgressImageID != null) {
/* 2115 */           imageLoader.releaseImage(this.lastProgressImageID);
/*      */         }
/* 2117 */         this.lastProgressImageID = imageID;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public int reporting(final int eventType, final IProgressReporter reporter)
/*      */     {
/* 2126 */       if (eventType == 3)
/*      */       {
/*      */ 
/*      */ 
/* 2130 */         synchronized (this.pending_updates)
/*      */         {
/* 2132 */           if (this.pending_updates.contains(reporter))
/*      */           {
/* 2134 */             return 0;
/*      */           }
/*      */           
/* 2137 */           this.pending_updates.add(reporter);
/*      */         }
/*      */       }
/*      */       
/* 2141 */       Utils.execSWTThread(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 2147 */           MainStatusBar.ProgressListener.this.swt_reporting(eventType, reporter);
/*      */         }
/* 2149 */       });
/* 2150 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private int swt_reporting(int eventType, IProgressReporter reporter)
/*      */     {
/* 2158 */       synchronized (this.pending_updates)
/*      */       {
/*      */ 
/*      */ 
/* 2162 */         this.pending_updates.remove(reporter);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2168 */       swt_setProgressImage();
/*      */       
/* 2170 */       if (null == reporter) {
/* 2171 */         return 0;
/*      */       }
/*      */       
/* 2174 */       if (2 == eventType) {
/* 2175 */         swt_updateFromPrevious();
/* 2176 */       } else if ((1 == eventType) || (3 == eventType))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2181 */         IProgressReport pReport = reporter.getProgressReport();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2187 */         if (pReport.isInErrorState())
/*      */         {
/* 2189 */           if ("reporterType_updater".equals(pReport.getReporterType()))
/*      */           {
/*      */ 
/*      */ 
/* 2193 */             return 0;
/*      */           }
/*      */           
/* 2196 */           IProgressReporter final_reporter = reporter;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2202 */           if ((!ProgressReporterWindow.isOpened(final_reporter)) && 
/* 2203 */             (!ProgressReporterWindow.isOpened(final_reporter))) {
/* 2204 */             ProgressReporterWindow.open(final_reporter, 0);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2213 */         if (!pReport.isActive()) {
/* 2214 */           swt_updateFromPrevious();
/*      */         } else {
/* 2216 */           swt_update(pReport);
/*      */         }
/*      */       }
/*      */       
/* 2220 */       return 0;
/*      */     }
/*      */     
/*      */     private void swt_update(IProgressReport pReport)
/*      */     {
/* 2225 */       if (null == pReport) {
/* 2226 */         swt_updateProgressBarDisplay(null);
/* 2227 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2234 */       if (MainStatusBar.this.PRManager.hasMultipleActive())
/*      */       {
/* 2236 */         MainStatusBar.this.setStatusText(pReport.getName());
/* 2237 */         MainStatusBar.this.progressBar.setIndeterminate(true);
/* 2238 */         MainStatusBar.this.showProgressBar(true);
/*      */       }
/*      */       else {
/* 2241 */         swt_updateProgressBarDisplay(pReport);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void swt_updateFromPrevious()
/*      */     {
/* 2249 */       IProgressReporter previousReporter = MainStatusBar.this.PRManager.getNextActiveReporter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2254 */       if (null != previousReporter) {
/* 2255 */         swt_update(previousReporter.getProgressReport());
/*      */       } else {
/* 2257 */         swt_update(null);
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/MainStatusBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */