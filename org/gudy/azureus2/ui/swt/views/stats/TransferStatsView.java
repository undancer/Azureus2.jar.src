/*      */ package org.gudy.azureus2.ui.swt.views.stats;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*      */ import com.aelitis.azureus.core.networkmanager.ProtocolStartpoint;
/*      */ import com.aelitis.azureus.core.networkmanager.Transport;
/*      */ import com.aelitis.azureus.core.networkmanager.TransportStartpoint;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.proxy.AEProxySelector;
/*      */ import com.aelitis.azureus.core.proxy.AEProxySelectorFactory;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingMapper;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingSource;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingZone;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerFactory;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.Proxy;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.custom.ScrolledComposite;
/*      */ import org.eclipse.swt.custom.StackLayout;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.FontMetrics;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Group;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.TabFolder;
/*      */ import org.eclipse.swt.widgets.TabItem;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerRateController;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*      */ import org.gudy.azureus2.core3.stats.transfer.OverallStats;
/*      */ import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.TextViewerWindow;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*      */ import org.gudy.azureus2.ui.swt.components.Legend;
/*      */ import org.gudy.azureus2.ui.swt.components.graphics.PingGraphic;
/*      */ import org.gudy.azureus2.ui.swt.components.graphics.Plot3D;
/*      */ import org.gudy.azureus2.ui.swt.components.graphics.Scale;
/*      */ import org.gudy.azureus2.ui.swt.components.graphics.SpeedGraphic;
/*      */ import org.gudy.azureus2.ui.swt.components.graphics.ValueFormater;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TransferStatsView
/*      */   implements UISWTViewCoreEventListener
/*      */ {
/*      */   public static final String MSGID_PREFIX = "TransferStatsView";
/*      */   private static final int MAX_DISPLAYED_PING_MILLIS = 1199;
/*      */   private static final int MAX_DISPLAYED_PING_MILLIS_DISP = 1200;
/*      */   private GlobalManager global_manager;
/*      */   private GlobalManagerStats stats;
/*      */   private SpeedManager speedManager;
/*      */   private OverallStats totalStats;
/*      */   private Composite mainPanel;
/*      */   private Composite blahPanel;
/*      */   private BufferedLabel asn;
/*      */   private BufferedLabel estUpCap;
/*      */   private BufferedLabel estDownCap;
/*      */   private BufferedLabel uploadBiaser;
/*      */   private BufferedLabel currentIP;
/*      */   private Composite connectionPanel;
/*      */   private BufferedLabel upload_label;
/*      */   private BufferedLabel connection_label;
/*      */   private SpeedGraphic upload_graphic;
/*      */   private SpeedGraphic connection_graphic;
/*      */   private TabFolder con_folder;
/*      */   private long last_route_update;
/*      */   private Composite route_comp;
/*  143 */   private BufferedLabel[][] route_labels = new BufferedLabel[0][0];
/*  144 */   private Map<String, Long> route_last_seen = new HashMap();
/*      */   private Composite generalPanel;
/*      */   private BufferedLabel totalLabel;
/*      */   private BufferedLabel nowUp;
/*      */   private BufferedLabel nowDown;
/*      */   private BufferedLabel sessionDown;
/*      */   private BufferedLabel sessionUp;
/*      */   private BufferedLabel session_ratio;
/*      */   private BufferedLabel sessionTime;
/*      */   private BufferedLabel totalDown;
/*      */   private BufferedLabel totalUp;
/*      */   private BufferedLabel total_ratio;
/*      */   private BufferedLabel totalTime;
/*      */   private Label socksState;
/*      */   private BufferedLabel socksCurrent;
/*      */   private BufferedLabel socksFails;
/*      */   private Label socksMore;
/*      */   private Group autoSpeedPanel;
/*      */   private StackLayout autoSpeedPanelLayout;
/*  163 */   private Composite autoSpeedInfoPanel; private Composite autoSpeedDisabledPanel; private PingGraphic pingGraph; private plotView[] plot_views; private zoneView[] zone_views; private limitToTextHelper limit_to_text = new limitToTextHelper();
/*      */   
/*  165 */   private final DecimalFormat formatter = new DecimalFormat("##.#");
/*      */   
/*      */   private boolean initialised;
/*      */   
/*      */   private UISWTView swtView;
/*      */   
/*      */   public TransferStatsView()
/*      */   {
/*  173 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/*  175 */         TransferStatsView.this.global_manager = core.getGlobalManager();
/*  176 */         TransferStatsView.this.stats = TransferStatsView.this.global_manager.getStats();
/*  177 */         TransferStatsView.this.speedManager = core.getSpeedManager();
/*  178 */         TransferStatsView.this.totalStats = StatsFactory.getStats();
/*      */       }
/*  180 */     });
/*  181 */     this.pingGraph = PingGraphic.getInstance();
/*      */   }
/*      */   
/*      */ 
/*      */   private void initialize(Composite composite)
/*      */   {
/*  187 */     this.mainPanel = new Composite(composite, 0);
/*  188 */     GridLayout mainLayout = new GridLayout();
/*  189 */     this.mainPanel.setLayout(mainLayout);
/*      */     
/*  191 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/*  193 */         Utils.execSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  195 */             if ((TransferStatsView.this.mainPanel == null) || (TransferStatsView.this.mainPanel.isDisposed())) {
/*  196 */               return;
/*      */             }
/*  198 */             TransferStatsView.this.createGeneralPanel();
/*  199 */             TransferStatsView.this.createConnectionPanel();
/*  200 */             TransferStatsView.this.createCapacityPanel();
/*  201 */             TransferStatsView.this.createAutoSpeedPanel();
/*      */             
/*  203 */             TransferStatsView.this.initialised = true;
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private void createGeneralPanel() {
/*  211 */     this.generalPanel = new Composite(this.mainPanel, 0);
/*  212 */     GridLayout outerLayout = new GridLayout();
/*  213 */     outerLayout.numColumns = 2;
/*  214 */     this.generalPanel.setLayout(outerLayout);
/*  215 */     GridData gridData = new GridData(768);
/*  216 */     Utils.setLayoutData(this.generalPanel, gridData);
/*      */     
/*  218 */     Composite generalStatsPanel = new Composite(this.generalPanel, 2048);
/*  219 */     GridData generalStatsPanelGridData = new GridData(768);
/*  220 */     generalStatsPanelGridData.grabExcessHorizontalSpace = true;
/*  221 */     Utils.setLayoutData(generalStatsPanel, generalStatsPanelGridData);
/*      */     
/*  223 */     GridLayout panelLayout = new GridLayout();
/*  224 */     panelLayout.numColumns = 5;
/*  225 */     panelLayout.makeColumnsEqualWidth = true;
/*  226 */     generalStatsPanel.setLayout(panelLayout);
/*      */     
/*      */ 
/*  229 */     Label lbl = new Label(generalStatsPanel, 0);
/*      */     
/*  231 */     lbl = new Label(generalStatsPanel, 0);
/*  232 */     Messages.setLanguageText(lbl, "SpeedView.stats.downloaded");
/*      */     
/*  234 */     lbl = new Label(generalStatsPanel, 0);
/*  235 */     Messages.setLanguageText(lbl, "SpeedView.stats.uploaded");
/*      */     
/*  237 */     lbl = new Label(generalStatsPanel, 0);
/*  238 */     Messages.setLanguageText(lbl, "SpeedView.stats.ratio");
/*      */     
/*  240 */     lbl = new Label(generalStatsPanel, 0);
/*  241 */     Messages.setLanguageText(lbl, "SpeedView.stats.uptime");
/*      */     
/*  243 */     lbl = new Label(generalStatsPanel, 0);
/*  244 */     lbl = new Label(generalStatsPanel, 0);
/*  245 */     lbl = new Label(generalStatsPanel, 0);
/*  246 */     lbl = new Label(generalStatsPanel, 0);
/*  247 */     lbl = new Label(generalStatsPanel, 0);
/*      */     
/*      */ 
/*  250 */     Label nowLabel = new Label(generalStatsPanel, 0);
/*  251 */     gridData = new GridData(768);
/*  252 */     Utils.setLayoutData(nowLabel, gridData);
/*  253 */     Messages.setLanguageText(nowLabel, "SpeedView.stats.now");
/*      */     
/*  255 */     this.nowDown = new BufferedLabel(generalStatsPanel, 536870912);
/*  256 */     gridData = new GridData(768);
/*  257 */     Utils.setLayoutData(this.nowDown, gridData);
/*      */     
/*  259 */     this.nowUp = new BufferedLabel(generalStatsPanel, 536870912);
/*  260 */     gridData = new GridData(768);
/*  261 */     Utils.setLayoutData(this.nowUp, gridData);
/*      */     
/*  263 */     lbl = new Label(generalStatsPanel, 0);
/*  264 */     lbl = new Label(generalStatsPanel, 0);
/*      */     
/*      */ 
/*      */ 
/*  268 */     Label sessionLabel = new Label(generalStatsPanel, 0);
/*  269 */     gridData = new GridData(768);
/*  270 */     Utils.setLayoutData(sessionLabel, gridData);
/*      */     
/*  272 */     Messages.setLanguageText(sessionLabel, "SpeedView.stats.session");
/*  273 */     this.sessionDown = new BufferedLabel(generalStatsPanel, 536870912);
/*  274 */     gridData = new GridData(768);
/*  275 */     Utils.setLayoutData(this.sessionDown, gridData);
/*      */     
/*  277 */     this.sessionUp = new BufferedLabel(generalStatsPanel, 536870912);
/*  278 */     gridData = new GridData(768);
/*  279 */     Utils.setLayoutData(this.sessionUp, gridData);
/*      */     
/*  281 */     this.session_ratio = new BufferedLabel(generalStatsPanel, 536870912);
/*  282 */     gridData = new GridData(768);
/*  283 */     Utils.setLayoutData(this.session_ratio, gridData);
/*      */     
/*  285 */     this.sessionTime = new BufferedLabel(generalStatsPanel, 536870912);
/*  286 */     gridData = new GridData(768);
/*  287 */     Utils.setLayoutData(this.sessionTime, gridData);
/*      */     
/*      */ 
/*      */ 
/*  291 */     this.totalLabel = new BufferedLabel(generalStatsPanel, 536870912);
/*  292 */     gridData = new GridData(768);
/*  293 */     Utils.setLayoutData(this.totalLabel, gridData);
/*  294 */     Messages.setLanguageText(this.totalLabel.getWidget(), "SpeedView.stats.total");
/*      */     
/*  296 */     this.totalDown = new BufferedLabel(generalStatsPanel, 536870912);
/*  297 */     gridData = new GridData(768);
/*  298 */     Utils.setLayoutData(this.totalDown, gridData);
/*      */     
/*  300 */     this.totalUp = new BufferedLabel(generalStatsPanel, 536870912);
/*  301 */     gridData = new GridData(768);
/*  302 */     Utils.setLayoutData(this.totalUp, gridData);
/*      */     
/*  304 */     this.total_ratio = new BufferedLabel(generalStatsPanel, 536870912);
/*  305 */     gridData = new GridData(768);
/*  306 */     Utils.setLayoutData(this.total_ratio, gridData);
/*      */     
/*  308 */     this.totalTime = new BufferedLabel(generalStatsPanel, 536870912);
/*  309 */     gridData = new GridData(768);
/*  310 */     Utils.setLayoutData(this.totalTime, gridData);
/*      */     
/*  312 */     for (Object obj : new Object[] { nowLabel, sessionLabel, this.totalLabel })
/*      */     {
/*      */       Control control;
/*      */       Control control;
/*  316 */       if ((obj instanceof BufferedLabel))
/*      */       {
/*  318 */         control = ((BufferedLabel)obj).getControl();
/*      */       }
/*      */       else
/*      */       {
/*  322 */         control = (Label)obj;
/*      */       }
/*  324 */       Menu menu = new Menu(control.getShell(), 8);
/*  325 */       control.setMenu(menu);
/*  326 */       MenuItem item = new MenuItem(menu, 0);
/*  327 */       Messages.setLanguageText(item, "MainWindow.menu.view.configuration");
/*  328 */       item.addSelectionListener(new SelectionAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/*      */ 
/*  335 */           UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*      */           
/*  337 */           if (uif != null)
/*      */           {
/*  339 */             uif.getMDI().showEntryByID("ConfigView", "Stats");
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  348 */     Composite generalSocksPanel = new Composite(this.generalPanel, 2048);
/*  349 */     GridData generalSocksData = new GridData();
/*  350 */     Utils.setLayoutData(generalSocksPanel, generalSocksData);
/*      */     
/*  352 */     GridLayout socksLayout = new GridLayout();
/*  353 */     socksLayout.numColumns = 2;
/*  354 */     generalSocksPanel.setLayout(socksLayout);
/*      */     
/*  356 */     lbl = new Label(generalSocksPanel, 0);
/*  357 */     Messages.setLanguageText(lbl, "label.socks");
/*      */     
/*  359 */     lbl = new Label(generalSocksPanel, 0);
/*      */     
/*      */ 
/*      */ 
/*  363 */     lbl = new Label(generalSocksPanel, 0);
/*  364 */     lbl.setText(MessageText.getString("label.proxy") + ":");
/*      */     
/*  366 */     this.socksState = new Label(generalSocksPanel, 0);
/*  367 */     gridData = new GridData(768);
/*  368 */     gridData.widthHint = 120;
/*  369 */     Utils.setLayoutData(this.socksState, gridData);
/*      */     
/*      */ 
/*      */ 
/*  373 */     lbl = new Label(generalSocksPanel, 0);
/*  374 */     lbl.setText(MessageText.getString("PeersView.state") + ":");
/*      */     
/*  376 */     this.socksCurrent = new BufferedLabel(generalSocksPanel, 536870912);
/*  377 */     gridData = new GridData(768);
/*  378 */     Utils.setLayoutData(this.socksCurrent, gridData);
/*      */     
/*      */ 
/*      */ 
/*  382 */     lbl = new Label(generalSocksPanel, 0);
/*  383 */     lbl.setText(MessageText.getString("label.fails") + ":");
/*      */     
/*  385 */     this.socksFails = new BufferedLabel(generalSocksPanel, 536870912);
/*  386 */     gridData = new GridData(768);
/*  387 */     Utils.setLayoutData(this.socksFails, gridData);
/*      */     
/*      */ 
/*      */ 
/*  391 */     lbl = new Label(generalSocksPanel, 0);
/*      */     
/*  393 */     gridData = new GridData(768);
/*  394 */     gridData.horizontalAlignment = 3;
/*  395 */     this.socksMore = new Label(generalSocksPanel, 0);
/*  396 */     this.socksMore.setText(MessageText.getString("label.more") + "...");
/*  397 */     Utils.setLayoutData(this.socksMore, gridData);
/*  398 */     this.socksMore.setCursor(this.socksMore.getDisplay().getSystemCursor(21));
/*  399 */     this.socksMore.setForeground(Colors.blue);
/*  400 */     this.socksMore.addMouseListener(new MouseAdapter() {
/*      */       public void mouseDoubleClick(MouseEvent arg0) {
/*  402 */         TransferStatsView.this.showSOCKSInfo();
/*      */       }
/*      */       
/*  405 */       public void mouseUp(MouseEvent arg0) { TransferStatsView.this.showSOCKSInfo();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  411 */     });
/*  412 */     Point socks_size = generalSocksPanel.computeSize(-1, -1);
/*  413 */     Rectangle trim = generalSocksPanel.computeTrim(0, 0, socks_size.x, socks_size.y);
/*  414 */     generalStatsPanelGridData.heightHint = (socks_size.y - (trim.height - socks_size.y));
/*      */   }
/*      */   
/*      */ 
/*      */   private void showSOCKSInfo()
/*      */   {
/*  420 */     AEProxySelector proxy_selector = AEProxySelectorFactory.getSelector();
/*      */     
/*  422 */     String info = proxy_selector.getInfo();
/*      */     
/*  424 */     TextViewerWindow viewer = new TextViewerWindow(MessageText.getString("proxy.info.title"), null, info, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void createCapacityPanel()
/*      */   {
/*  434 */     this.blahPanel = new Composite(this.mainPanel, 0);
/*  435 */     GridData blahPanelData = new GridData(768);
/*  436 */     Utils.setLayoutData(this.blahPanel, blahPanelData);
/*      */     
/*  438 */     GridLayout panelLayout = new GridLayout();
/*  439 */     panelLayout.numColumns = 8;
/*  440 */     this.blahPanel.setLayout(panelLayout);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  446 */     Label label = new Label(this.blahPanel, 0);
/*  447 */     Messages.setLanguageText(label, "SpeedView.stats.asn");
/*  448 */     this.asn = new BufferedLabel(this.blahPanel, 0);
/*  449 */     GridData gridData = new GridData(768);
/*  450 */     Utils.setLayoutData(this.asn, gridData);
/*      */     
/*  452 */     label = new Label(this.blahPanel, 0);
/*  453 */     Messages.setLanguageText(label, "label.current_ip");
/*  454 */     this.currentIP = new BufferedLabel(this.blahPanel, 0);
/*  455 */     gridData = new GridData(768);
/*  456 */     Utils.setLayoutData(this.currentIP, gridData);
/*      */     
/*  458 */     label = new Label(this.blahPanel, 0);
/*  459 */     Messages.setLanguageText(label, "SpeedView.stats.estupcap");
/*  460 */     this.estUpCap = new BufferedLabel(this.blahPanel, 0);
/*  461 */     gridData = new GridData(768);
/*  462 */     Utils.setLayoutData(this.estUpCap, gridData);
/*      */     
/*  464 */     label = new Label(this.blahPanel, 0);
/*  465 */     Messages.setLanguageText(label, "SpeedView.stats.estdowncap");
/*  466 */     this.estDownCap = new BufferedLabel(this.blahPanel, 0);
/*  467 */     gridData = new GridData(768);
/*  468 */     Utils.setLayoutData(this.estDownCap, gridData);
/*      */     
/*  470 */     label = new Label(this.blahPanel, 0);
/*  471 */     Messages.setLanguageText(label, "SpeedView.stats.upbias");
/*  472 */     this.uploadBiaser = new BufferedLabel(this.blahPanel, 0);
/*  473 */     gridData = new GridData(768);
/*  474 */     gridData.horizontalSpan = 7;
/*  475 */     Utils.setLayoutData(this.uploadBiaser, gridData);
/*      */   }
/*      */   
/*      */ 
/*      */   private void createConnectionPanel()
/*      */   {
/*  481 */     this.connectionPanel = new Composite(this.mainPanel, 0);
/*  482 */     GridData gridData = new GridData(768);
/*  483 */     Utils.setLayoutData(this.connectionPanel, gridData);
/*      */     
/*  485 */     GridLayout panelLayout = new GridLayout();
/*  486 */     panelLayout.numColumns = 2;
/*  487 */     panelLayout.makeColumnsEqualWidth = true;
/*  488 */     this.connectionPanel.setLayout(panelLayout);
/*      */     
/*  490 */     Composite conn_area = new Composite(this.connectionPanel, 0);
/*  491 */     gridData = new GridData(768);
/*  492 */     Utils.setLayoutData(conn_area, gridData);
/*      */     
/*  494 */     panelLayout = new GridLayout();
/*  495 */     panelLayout.numColumns = 2;
/*  496 */     conn_area.setLayout(panelLayout);
/*      */     
/*  498 */     Label label = new Label(conn_area, 0);
/*  499 */     Messages.setLanguageText(label, "SpeedView.stats.con");
/*      */     
/*  501 */     this.connection_label = new BufferedLabel(conn_area, 536870912);
/*  502 */     gridData = new GridData(768);
/*  503 */     Utils.setLayoutData(this.connection_label, gridData);
/*      */     
/*  505 */     Composite upload_area = new Composite(this.connectionPanel, 0);
/*  506 */     gridData = new GridData(768);
/*  507 */     Utils.setLayoutData(upload_area, gridData);
/*      */     
/*  509 */     panelLayout = new GridLayout();
/*  510 */     panelLayout.numColumns = 2;
/*  511 */     upload_area.setLayout(panelLayout);
/*      */     
/*  513 */     label = new Label(upload_area, 0);
/*  514 */     Messages.setLanguageText(label, "SpeedView.stats.upload");
/*      */     
/*  516 */     this.upload_label = new BufferedLabel(upload_area, 536870912);
/*  517 */     gridData = new GridData(768);
/*  518 */     Utils.setLayoutData(this.upload_label, gridData);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  523 */     this.con_folder = new TabFolder(this.connectionPanel, 16384);
/*  524 */     gridData = new GridData(1808);
/*  525 */     gridData.horizontalSpan = 1;
/*  526 */     Utils.setLayoutData(this.con_folder, gridData);
/*  527 */     this.con_folder.setBackground(Colors.background);
/*      */     
/*      */ 
/*      */ 
/*  531 */     TabItem conn_item = new TabItem(this.con_folder, 0);
/*      */     
/*  533 */     conn_item.setText(MessageText.getString("label.connections"));
/*      */     
/*  535 */     Canvas connection_canvas = new Canvas(this.con_folder, 262144);
/*  536 */     conn_item.setControl(connection_canvas);
/*  537 */     gridData = new GridData(1808);
/*  538 */     gridData.heightHint = 200;
/*  539 */     Utils.setLayoutData(connection_canvas, gridData);
/*  540 */     this.connection_graphic = SpeedGraphic.getInstance(new Scale(false), new ValueFormater()
/*      */     {
/*      */ 
/*      */ 
/*      */       public String format(int value)
/*      */       {
/*      */ 
/*      */ 
/*  548 */         return String.valueOf(value);
/*      */       }
/*      */       
/*  551 */     });
/*  552 */     this.connection_graphic.initialize(connection_canvas);
/*  553 */     Color[] colors = this.connection_graphic.colors;
/*      */     
/*  555 */     this.connection_graphic.setLineColors(colors);
/*      */     
/*      */ 
/*      */ 
/*  559 */     TabItem route_info_tab = new TabItem(this.con_folder, 0);
/*      */     
/*  561 */     route_info_tab.setText(MessageText.getString("label.routing"));
/*      */     
/*  563 */     Composite route_tab_comp = new Composite(this.con_folder, 0);
/*  564 */     Utils.setLayoutData(route_tab_comp, new GridData(4, 4, true, true));
/*  565 */     GridLayout routeTabLayout = new GridLayout();
/*  566 */     routeTabLayout.numColumns = 1;
/*  567 */     route_tab_comp.setLayout(routeTabLayout);
/*      */     
/*  569 */     route_info_tab.setControl(route_tab_comp);
/*      */     
/*  571 */     ScrolledComposite sc = new ScrolledComposite(route_tab_comp, 512);
/*  572 */     Utils.setLayoutData(sc, new GridData(4, 4, true, true));
/*      */     
/*  574 */     this.route_comp = new Composite(sc, 0);
/*      */     
/*  576 */     Utils.setLayoutData(this.route_comp, new GridData(4, 4, true, true));
/*  577 */     GridLayout routeLayout = new GridLayout();
/*  578 */     routeLayout.numColumns = 3;
/*      */     
/*  580 */     this.route_comp.setLayout(routeLayout);
/*      */     
/*  582 */     sc.setContent(this.route_comp);
/*      */     
/*  584 */     buildRouteComponent(5);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  589 */     Canvas upload_canvas = new Canvas(this.connectionPanel, 262144);
/*  590 */     gridData = new GridData(1808);
/*  591 */     gridData.heightHint = 200;
/*  592 */     Utils.setLayoutData(upload_canvas, gridData);
/*  593 */     this.upload_graphic = SpeedGraphic.getInstance(new ValueFormater()
/*      */     {
/*      */ 
/*      */ 
/*      */       public String format(int value)
/*      */       {
/*      */ 
/*  600 */         return DisplayFormatters.formatByteCountToKiBEtc(value);
/*      */       }
/*      */       
/*  603 */     });
/*  604 */     this.upload_graphic.initialize(upload_canvas);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void buildRouteComponent(int rows)
/*      */   {
/*  612 */     boolean changed = false;
/*      */     
/*  614 */     if (rows <= this.route_labels.length)
/*      */     {
/*  616 */       for (int i = rows; i < this.route_labels.length; i++)
/*      */       {
/*  618 */         for (int j = 0; j < 3; j++)
/*      */         {
/*  620 */           this.route_labels[i][j].setText("");
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  625 */       Control[] labels = this.route_comp.getChildren();
/*  626 */       for (int i = 0; i < labels.length; i++) {
/*  627 */         labels[i].dispose();
/*      */       }
/*      */       
/*  630 */       Label h1 = new Label(this.route_comp, 0);
/*  631 */       Utils.setLayoutData(h1, new GridData(768));
/*  632 */       h1.setText(MessageText.getString("label.route"));
/*  633 */       Label h2 = new Label(this.route_comp, 0);
/*  634 */       Utils.setLayoutData(h2, new GridData(768));
/*  635 */       h2.setText(MessageText.getString("tps.type.incoming"));
/*  636 */       Label h3 = new Label(this.route_comp, 0);
/*  637 */       Utils.setLayoutData(h3, new GridData(768));
/*  638 */       h3.setText(MessageText.getString("label.outgoing"));
/*      */       
/*  640 */       new Label(this.route_comp, 0);
/*  641 */       new Label(this.route_comp, 0);
/*  642 */       new Label(this.route_comp, 0);
/*      */       
/*  644 */       this.route_labels = new BufferedLabel[rows][3];
/*      */       
/*  646 */       for (int i = 0; i < rows; i++)
/*      */       {
/*  648 */         for (int j = 0; j < 3; j++) {
/*  649 */           BufferedLabel l = new BufferedLabel(this.route_comp, 536870912);
/*  650 */           GridData gridData = new GridData(768);
/*  651 */           Utils.setLayoutData(l, gridData);
/*  652 */           this.route_labels[i][j] = l;
/*      */         }
/*      */       }
/*      */       
/*  656 */       changed = true;
/*      */     }
/*      */     
/*  659 */     Point size = this.route_comp.computeSize(this.route_comp.getParent().getSize().x, -1);
/*      */     
/*  661 */     changed = (changed) || (!this.route_comp.getSize().equals(size));
/*      */     
/*  663 */     this.route_comp.setSize(size);
/*      */     
/*  665 */     if (!changed)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  670 */       for (int i = 0; i < this.route_labels.length; i++) {
/*  671 */         for (int j = 0; j < 3; j++) {
/*  672 */           BufferedLabel lab = this.route_labels[i][j];
/*  673 */           if ((lab.getControl().getSize().y == 0) && (lab.getText().length() > 0)) {
/*  674 */             changed = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  680 */     if (changed)
/*      */     {
/*  682 */       this.route_comp.getParent().layout(true, true);
/*      */     }
/*      */     
/*  685 */     this.route_comp.update();
/*      */   }
/*      */   
/*      */   private void createAutoSpeedPanel() {
/*  689 */     this.autoSpeedPanel = new Group(this.mainPanel, 0);
/*  690 */     GridData generalPanelData = new GridData(1808);
/*  691 */     Utils.setLayoutData(this.autoSpeedPanel, generalPanelData);
/*  692 */     Messages.setLanguageText(this.autoSpeedPanel, "SpeedView.stats.autospeed", new String[] { String.valueOf(1200) });
/*      */     
/*      */ 
/*  695 */     this.autoSpeedPanelLayout = new StackLayout();
/*  696 */     this.autoSpeedPanel.setLayout(this.autoSpeedPanelLayout);
/*      */     
/*  698 */     this.autoSpeedInfoPanel = new Composite(this.autoSpeedPanel, 0);
/*  699 */     Utils.setLayoutData(this.autoSpeedInfoPanel, new GridData(1808));
/*  700 */     GridLayout layout = new GridLayout();
/*  701 */     layout.numColumns = 8;
/*  702 */     layout.makeColumnsEqualWidth = true;
/*  703 */     this.autoSpeedInfoPanel.setLayout(layout);
/*      */     
/*  705 */     Canvas pingCanvas = new Canvas(this.autoSpeedInfoPanel, 262144);
/*  706 */     GridData gridData = new GridData(1808);
/*  707 */     gridData.horizontalSpan = 4;
/*  708 */     Utils.setLayoutData(pingCanvas, gridData);
/*      */     
/*  710 */     this.pingGraph.initialize(pingCanvas);
/*      */     
/*  712 */     TabFolder folder = new TabFolder(this.autoSpeedInfoPanel, 16384);
/*  713 */     gridData = new GridData(1808);
/*  714 */     gridData.horizontalSpan = 4;
/*  715 */     Utils.setLayoutData(folder, gridData);
/*  716 */     folder.setBackground(Colors.background);
/*      */     
/*  718 */     ValueFormater speed_formatter = new ValueFormater()
/*      */     {
/*      */ 
/*      */ 
/*      */       public String format(int value)
/*      */       {
/*      */ 
/*  725 */         return DisplayFormatters.formatByteCountToKiBEtc(value);
/*      */       }
/*      */       
/*  728 */     };
/*  729 */     ValueFormater time_formatter = new ValueFormater()
/*      */     {
/*      */ 
/*      */ 
/*      */       public String format(int value)
/*      */       {
/*      */ 
/*  736 */         return value + " ms";
/*      */       }
/*      */       
/*  739 */     };
/*  740 */     ValueFormater[] formatters = { speed_formatter, speed_formatter, time_formatter };
/*      */     
/*  742 */     String[] labels = { "up", "down", "ping" };
/*      */     
/*  744 */     SpeedManagerPingMapper[] mappers = this.speedManager.getMappers();
/*      */     
/*  746 */     this.plot_views = new plotView[mappers.length];
/*  747 */     this.zone_views = new zoneView[mappers.length];
/*      */     
/*  749 */     for (int i = 0; i < mappers.length; i++)
/*      */     {
/*  751 */       SpeedManagerPingMapper mapper = mappers[i];
/*      */       
/*  753 */       TabItem plot_item = new TabItem(folder, 0);
/*      */       
/*  755 */       plot_item.setText("Plot " + mapper.getName());
/*      */       
/*  757 */       Canvas plotCanvas = new Canvas(folder, 262144);
/*  758 */       gridData = new GridData(1808);
/*  759 */       Utils.setLayoutData(plotCanvas, gridData);
/*      */       
/*  761 */       this.plot_views[i] = new plotView(mapper, plotCanvas, labels, formatters);
/*      */       
/*  763 */       plot_item.setControl(plotCanvas);
/*      */       
/*  765 */       TabItem zones_item = new TabItem(folder, 0);
/*  766 */       zones_item.setText("Zones " + mapper.getName());
/*      */       
/*  768 */       Canvas zoneCanvas = new Canvas(folder, 262144);
/*  769 */       gridData = new GridData(1808);
/*  770 */       Utils.setLayoutData(zoneCanvas, gridData);
/*      */       
/*  772 */       this.zone_views[i] = new zoneView(mapper, zoneCanvas, labels, formatters);
/*      */       
/*  774 */       zones_item.setControl(zoneCanvas);
/*      */     }
/*      */     
/*  777 */     this.autoSpeedDisabledPanel = new Composite(this.autoSpeedPanel, 0);
/*  778 */     this.autoSpeedDisabledPanel.setLayout(new GridLayout());
/*  779 */     Label disabled = new Label(this.autoSpeedDisabledPanel, 0);
/*  780 */     disabled.setEnabled(false);
/*  781 */     Messages.setLanguageText(disabled, "SpeedView.stats.autospeed.disabled");
/*  782 */     Utils.setLayoutData(disabled, new GridData(832));
/*      */     
/*  784 */     this.autoSpeedPanelLayout.topControl = (this.speedManager.isAvailable() ? this.autoSpeedInfoPanel : this.autoSpeedDisabledPanel);
/*      */     
/*  786 */     gridData = new GridData(768);
/*  787 */     gridData.horizontalSpan = 8;
/*      */     
/*  789 */     Legend.createLegendComposite(this.autoSpeedInfoPanel, PingGraphic.defaultColors, new String[] { "TransferStatsView.legend.pingaverage", "TransferStatsView.legend.ping1", "TransferStatsView.legend.ping2", "TransferStatsView.legend.ping3" }, gridData);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void delete()
/*      */   {
/*  801 */     Utils.disposeComposite(this.generalPanel);
/*  802 */     Utils.disposeComposite(this.blahPanel);
/*      */     
/*  804 */     if (this.upload_graphic != null) {
/*  805 */       this.upload_graphic.dispose();
/*      */     }
/*      */     
/*  808 */     if (this.connection_graphic != null) {
/*  809 */       this.connection_graphic.dispose();
/*      */     }
/*      */     
/*  812 */     if (this.pingGraph != null) {
/*  813 */       this.pingGraph.dispose();
/*      */     }
/*      */     
/*  816 */     if (this.plot_views != null) {
/*  817 */       for (int i = 0; i < this.plot_views.length; i++)
/*      */       {
/*  819 */         this.plot_views[i].dispose();
/*      */       }
/*      */     }
/*      */     
/*  823 */     if (this.zone_views != null) {
/*  824 */       for (int i = 0; i < this.zone_views.length; i++)
/*      */       {
/*  826 */         this.zone_views[i].dispose();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private Composite getComposite() {
/*  832 */     return this.mainPanel;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void refresh()
/*      */   {
/*  839 */     if (!this.initialised) {
/*  840 */       return;
/*      */     }
/*  842 */     refreshGeneral();
/*      */     
/*  844 */     refreshCapacityPanel();
/*      */     
/*  846 */     refreshConnectionPanel();
/*      */     
/*  848 */     refreshPingPanel();
/*      */   }
/*      */   
/*      */   private void refreshGeneral() {
/*  852 */     if (this.stats == null) {
/*  853 */       return;
/*      */     }
/*      */     
/*  856 */     int now_prot_down_rate = this.stats.getProtocolReceiveRate();
/*  857 */     int now_prot_up_rate = this.stats.getProtocolSendRate();
/*      */     
/*  859 */     int now_total_down_rate = this.stats.getDataReceiveRate() + now_prot_down_rate;
/*  860 */     int now_total_up_rate = this.stats.getDataSendRate() + now_prot_up_rate;
/*      */     
/*  862 */     float now_perc_down = now_prot_down_rate * 100 / (now_total_down_rate == 0 ? 1 : now_total_down_rate);
/*  863 */     float now_perc_up = now_prot_up_rate * 100 / (now_total_up_rate == 0 ? 1 : now_total_up_rate);
/*      */     
/*  865 */     this.nowDown.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(now_total_down_rate) + "  (" + DisplayFormatters.formatByteCountToKiBEtcPerSec(now_prot_down_rate) + ", " + this.formatter.format(now_perc_down) + "%)");
/*      */     
/*      */ 
/*      */ 
/*  869 */     this.nowUp.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(now_total_up_rate) + "  (" + DisplayFormatters.formatByteCountToKiBEtcPerSec(now_prot_up_rate) + ", " + this.formatter.format(now_perc_up) + "%)");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  875 */     long session_prot_received = this.stats.getTotalProtocolBytesReceived();
/*  876 */     long session_prot_sent = this.stats.getTotalProtocolBytesSent();
/*      */     
/*  878 */     long session_total_received = this.stats.getTotalDataBytesReceived() + session_prot_received;
/*  879 */     long session_total_sent = this.stats.getTotalDataBytesSent() + session_prot_sent;
/*      */     
/*  881 */     float session_perc_received = (float)(session_prot_received * 100L) / (float)(session_total_received == 0L ? 1L : session_total_received);
/*  882 */     float session_perc_sent = (float)(session_prot_sent * 100L) / (float)(session_total_sent == 0L ? 1L : session_total_sent);
/*      */     
/*  884 */     this.sessionDown.setText(DisplayFormatters.formatByteCountToKiBEtc(session_total_received) + "  (" + DisplayFormatters.formatByteCountToKiBEtc(session_prot_received) + ", " + this.formatter.format(session_perc_received) + "%)");
/*      */     
/*      */ 
/*      */ 
/*  888 */     this.sessionUp.setText(DisplayFormatters.formatByteCountToKiBEtc(session_total_sent) + "  (" + DisplayFormatters.formatByteCountToKiBEtc(session_prot_sent) + ", " + this.formatter.format(session_perc_sent) + "%)");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  894 */     if (this.totalStats != null) {
/*  895 */       long mark = this.totalStats.getMarkTime();
/*  896 */       if (mark > 0L) {
/*  897 */         Messages.setLanguageText(this.totalLabel.getWidget(), "SpeedView.stats.total.since", new String[] { new SimpleDateFormat().format(new Date(mark)) });
/*      */       } else {
/*  899 */         Messages.setLanguageText(this.totalLabel.getWidget(), "SpeedView.stats.total");
/*      */       }
/*      */       
/*  902 */       long dl_bytes = this.totalStats.getDownloadedBytes(true);
/*  903 */       long ul_bytes = this.totalStats.getUploadedBytes(true);
/*      */       
/*  905 */       this.totalDown.setText(DisplayFormatters.formatByteCountToKiBEtc(dl_bytes));
/*  906 */       this.totalUp.setText(DisplayFormatters.formatByteCountToKiBEtc(ul_bytes));
/*      */       
/*  908 */       long session_up_time = this.totalStats.getSessionUpTime();
/*  909 */       long total_up_time = this.totalStats.getTotalUpTime(true);
/*      */       
/*  911 */       this.sessionTime.setText(session_up_time == 0L ? "" : DisplayFormatters.formatETA(session_up_time));
/*  912 */       this.totalTime.setText(total_up_time == 0L ? "" : DisplayFormatters.formatETA(total_up_time));
/*      */       
/*      */ 
/*  915 */       long t_ratio_raw = 1000L * ul_bytes / (dl_bytes == 0L ? 1L : dl_bytes);
/*  916 */       long s_ratio_raw = 1000L * session_total_sent / (session_total_received == 0L ? 1L : session_total_received);
/*      */       
/*  918 */       String t_ratio = "";
/*  919 */       String s_ratio = "";
/*      */       
/*  921 */       String partial = String.valueOf(t_ratio_raw % 1000L);
/*  922 */       while (partial.length() < 3) {
/*  923 */         partial = "0" + partial;
/*      */       }
/*  925 */       t_ratio = t_ratio_raw / 1000L + "." + partial;
/*      */       
/*  927 */       partial = String.valueOf(s_ratio_raw % 1000L);
/*  928 */       while (partial.length() < 3) {
/*  929 */         partial = "0" + partial;
/*      */       }
/*  931 */       s_ratio = s_ratio_raw / 1000L + "." + partial;
/*      */       
/*      */ 
/*  934 */       this.total_ratio.setText(t_ratio);
/*  935 */       this.session_ratio.setText(s_ratio);
/*      */     }
/*      */     
/*  938 */     AEProxySelector proxy_selector = AEProxySelectorFactory.getSelector();
/*      */     
/*  940 */     Proxy proxy = proxy_selector.getActiveProxy();
/*      */     
/*  942 */     this.socksMore.setEnabled(proxy != null);
/*      */     
/*  944 */     if (Constants.isOSX)
/*      */     {
/*  946 */       this.socksMore.setForeground(proxy == null ? Colors.light_grey : Colors.blue);
/*      */     }
/*      */     
/*  949 */     this.socksState.setText(proxy == null ? MessageText.getString("label.inactive") : ((InetSocketAddress)proxy.address()).getHostName());
/*      */     
/*  951 */     if (proxy == null)
/*      */     {
/*  953 */       this.socksCurrent.setText("");
/*      */       
/*  955 */       this.socksFails.setText("");
/*      */     }
/*      */     else {
/*  958 */       long last_con = proxy_selector.getLastConnectionTime();
/*  959 */       long last_fail = proxy_selector.getLastFailTime();
/*  960 */       int total_cons = proxy_selector.getConnectionCount();
/*  961 */       int total_fails = proxy_selector.getFailCount();
/*      */       
/*  963 */       long now = SystemTime.getMonotonousTime();
/*      */       
/*  965 */       long con_ago = now - last_con;
/*  966 */       long fail_ago = now - last_fail;
/*      */       
/*      */       String state_str;
/*      */       String state_str;
/*  970 */       if (last_fail < 0L)
/*      */       {
/*  972 */         state_str = "PeerManager.status.ok";
/*      */       }
/*      */       else {
/*      */         String state_str;
/*  976 */         if (fail_ago > 60000L) {
/*      */           String state_str;
/*  978 */           if (con_ago < fail_ago)
/*      */           {
/*  980 */             state_str = "PeerManager.status.ok";
/*      */           }
/*      */           else
/*      */           {
/*  984 */             state_str = "SpeedView.stats.unknown";
/*      */           }
/*      */         }
/*      */         else {
/*  988 */           state_str = "ManagerItem.error";
/*      */         }
/*      */       }
/*      */       
/*  992 */       this.socksCurrent.setText(MessageText.getString(state_str) + ", con=" + total_cons);
/*      */       
/*  994 */       long fail_ago_secs = fail_ago / 1000L;
/*      */       
/*  996 */       if (fail_ago_secs == 0L)
/*      */       {
/*  998 */         fail_ago_secs = 1L;
/*      */       }
/*      */       
/* 1001 */       this.socksFails.setText(DisplayFormatters.formatETA(fail_ago_secs, false) + " " + MessageText.getString("label.ago") + ", tot=" + total_fails);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void refreshCapacityPanel()
/*      */   {
/* 1008 */     if (this.speedManager == null) {
/* 1009 */       return;
/*      */     }
/*      */     
/* 1012 */     this.asn.setText(this.speedManager.getASN());
/*      */     
/* 1014 */     this.estUpCap.setText(this.limit_to_text.getLimitText(this.speedManager.getEstimatedUploadCapacityBytesPerSec()));
/*      */     
/* 1016 */     this.estDownCap.setText(this.limit_to_text.getLimitText(this.speedManager.getEstimatedDownloadCapacityBytesPerSec()));
/*      */     
/* 1018 */     this.uploadBiaser.setText(DownloadManagerRateController.getString());
/*      */     
/* 1020 */     InetAddress current_ip = NetworkAdmin.getSingleton().getDefaultPublicAddress();
/*      */     
/* 1022 */     this.currentIP.setText(current_ip == null ? "" : current_ip.getHostAddress());
/*      */   }
/*      */   
/*      */ 
/*      */   private void refreshConnectionPanel()
/*      */   {
/* 1028 */     if (this.global_manager == null) {
/* 1029 */       return;
/*      */     }
/*      */     
/* 1032 */     int total_connections = 0;
/* 1033 */     int total_con_queued = 0;
/* 1034 */     int total_con_blocked = 0;
/* 1035 */     int total_con_unchoked = 0;
/*      */     
/* 1037 */     int total_data_queued = 0;
/*      */     
/* 1039 */     int total_in = 0;
/*      */     
/* 1041 */     List<DownloadManager> dms = this.global_manager.getDownloadManagers();
/*      */     
/* 1043 */     for (DownloadManager dm : dms)
/*      */     {
/* 1045 */       PEPeerManager pm = dm.getPeerManager();
/*      */       
/* 1047 */       if (pm != null)
/*      */       {
/* 1049 */         total_data_queued += pm.getBytesQueuedForUpload();
/*      */         
/* 1051 */         total_connections += pm.getNbPeers() + pm.getNbSeeds();
/*      */         
/* 1053 */         total_con_queued += pm.getNbPeersWithUploadQueued();
/* 1054 */         total_con_blocked += pm.getNbPeersWithUploadBlocked();
/*      */         
/* 1056 */         total_con_unchoked += pm.getNbPeersUnchoked();
/*      */         
/* 1058 */         total_in += pm.getNbRemoteTCPConnections() + pm.getNbRemoteUDPConnections() + pm.getNbRemoteUTPConnections();
/*      */       }
/*      */     }
/*      */     
/* 1062 */     this.connection_label.setText(MessageText.getString("SpeedView.stats.con_details", new String[] { String.valueOf(total_connections) + "[" + MessageText.getString("label.in").toLowerCase() + ":" + total_in + "]", String.valueOf(total_con_unchoked), String.valueOf(total_con_queued), String.valueOf(total_con_blocked) }));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1069 */     this.connection_graphic.addIntsValue(new int[] { total_connections, total_con_unchoked, total_con_queued, total_con_blocked });
/*      */     
/* 1071 */     this.upload_label.setText(MessageText.getString("SpeedView.stats.upload_details", new String[] { DisplayFormatters.formatByteCountToKiBEtc(total_data_queued) }));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1076 */     this.upload_graphic.addIntValue(total_data_queued);
/*      */     
/* 1078 */     this.upload_graphic.refresh(false);
/* 1079 */     this.connection_graphic.refresh(false);
/*      */     
/* 1081 */     if (this.con_folder.getSelectionIndex() == 1)
/*      */     {
/* 1083 */       long now = SystemTime.getMonotonousTime();
/*      */       
/* 1085 */       if (now - this.last_route_update >= 2000L)
/*      */       {
/* 1087 */         this.last_route_update = now;
/*      */         
/* 1089 */         NetworkAdmin na = NetworkAdmin.getSingleton();
/*      */         
/* 1091 */         Map<InetAddress, String> ip_to_name_map = new HashMap();
/*      */         
/* 1093 */         Map<String, RouteInfo> name_to_route_map = new HashMap();
/*      */         
/* 1095 */         RouteInfo udp_info = null;
/* 1096 */         RouteInfo unknown_info = null;
/*      */         
/* 1098 */         List<PRUDPPacketHandler> udp_handlers = PRUDPPacketHandlerFactory.getHandlers();
/*      */         
/* 1100 */         InetAddress udp_bind_ip = null;
/*      */         
/* 1102 */         for (PRUDPPacketHandler handler : udp_handlers)
/*      */         {
/* 1104 */           if (handler.hasPrimordialHandler())
/*      */           {
/* 1106 */             udp_bind_ip = handler.getBindIP();
/*      */             
/* 1108 */             if (udp_bind_ip != null)
/*      */             {
/* 1110 */               if (udp_bind_ip.isAnyLocalAddress())
/*      */               {
/* 1112 */                 udp_bind_ip = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1118 */         for (DownloadManager dm : dms)
/*      */         {
/* 1120 */           PEPeerManager pm = dm.getPeerManager();
/*      */           
/* 1122 */           if (pm != null)
/*      */           {
/* 1124 */             List<PEPeer> peers = pm.getPeers();
/*      */             
/* 1126 */             for (PEPeer p : peers)
/*      */             {
/* 1128 */               NetworkConnection nc = PluginCoreUtils.unwrap(p.getPluginConnection());
/*      */               
/* 1130 */               boolean done = false;
/*      */               
/* 1132 */               if (nc != null)
/*      */               {
/* 1134 */                 Transport transport = nc.getTransport();
/*      */                 
/* 1136 */                 if (transport != null)
/*      */                 {
/* 1138 */                   if (transport.isTCP())
/*      */                   {
/* 1140 */                     TransportStartpoint start = transport.getTransportStartpoint();
/*      */                     
/* 1142 */                     if (start != null)
/*      */                     {
/* 1144 */                       InetSocketAddress socket_address = start.getProtocolStartpoint().getAddress();
/*      */                       
/* 1146 */                       if (socket_address != null)
/*      */                       {
/* 1148 */                         InetAddress address = socket_address.getAddress();
/*      */                         
/*      */                         String name;
/*      */                         String name;
/* 1152 */                         if (address.isAnyLocalAddress())
/*      */                         {
/* 1154 */                           name = "* (TCP)";
/*      */                         }
/*      */                         else
/*      */                         {
/* 1158 */                           name = (String)ip_to_name_map.get(address);
/*      */                         }
/*      */                         
/* 1161 */                         if (name == null)
/*      */                         {
/* 1163 */                           name = na.classifyRoute(address);
/*      */                           
/* 1165 */                           ip_to_name_map.put(address, name);
/*      */                         }
/*      */                         
/* 1168 */                         if (transport.isSOCKS())
/*      */                         {
/* 1170 */                           name = name + " (SOCKS)";
/*      */                         }
/*      */                         
/* 1173 */                         RouteInfo info = (RouteInfo)name_to_route_map.get(name);
/*      */                         
/* 1175 */                         if (info == null)
/*      */                         {
/* 1177 */                           info = new RouteInfo(name, null);
/*      */                           
/* 1179 */                           name_to_route_map.put(name, info);
/*      */                           
/* 1181 */                           this.route_last_seen.put(name, Long.valueOf(now));
/*      */                         }
/*      */                         
/* 1184 */                         info.update(p);
/*      */                         
/* 1186 */                         done = true;
/*      */                       }
/*      */                       
/*      */                     }
/*      */                   }
/* 1191 */                   else if (udp_bind_ip != null)
/*      */                   {
/*      */ 
/*      */ 
/* 1195 */                     String name = (String)ip_to_name_map.get(udp_bind_ip);
/*      */                     RouteInfo info;
/* 1197 */                     if (name == null)
/*      */                     {
/* 1199 */                       name = na.classifyRoute(udp_bind_ip);
/*      */                       
/* 1201 */                       ip_to_name_map.put(udp_bind_ip, name);
/*      */                       
/* 1203 */                       RouteInfo info = (RouteInfo)name_to_route_map.get(name);
/*      */                       
/* 1205 */                       this.route_last_seen.put(name, Long.valueOf(now));
/*      */                       
/* 1207 */                       if (info == null)
/*      */                       {
/* 1209 */                         info = new RouteInfo(name, null);
/*      */                         
/* 1211 */                         name_to_route_map.put(name, info);
/*      */                       }
/*      */                     }
/*      */                     else
/*      */                     {
/* 1216 */                       info = (RouteInfo)name_to_route_map.get(name);
/*      */                     }
/*      */                     
/* 1219 */                     info.update(p);
/*      */                     
/* 1221 */                     done = true;
/*      */                   }
/*      */                   else
/*      */                   {
/* 1225 */                     if (udp_info == null)
/*      */                     {
/* 1227 */                       udp_info = new RouteInfo("* (UDP)", null);
/*      */                       
/* 1229 */                       name_to_route_map.put(udp_info.getName(), udp_info);
/*      */                       
/* 1231 */                       this.route_last_seen.put(udp_info.getName(), Long.valueOf(now));
/*      */                     }
/*      */                     
/* 1234 */                     udp_info.update(p);
/*      */                     
/* 1236 */                     done = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/* 1242 */               if (!done)
/*      */               {
/* 1244 */                 if (unknown_info == null)
/*      */                 {
/* 1246 */                   unknown_info = new RouteInfo("Pending", null);
/*      */                   
/* 1248 */                   name_to_route_map.put(unknown_info.getName(), unknown_info);
/*      */                   
/* 1250 */                   this.route_last_seen.put(unknown_info.getName(), Long.valueOf(now));
/*      */                 }
/*      */                 
/* 1253 */                 unknown_info.update(p);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1259 */         List<RouteInfo> rows = new ArrayList();
/*      */         
/* 1261 */         Iterator<Map.Entry<String, Long>> it = this.route_last_seen.entrySet().iterator();
/*      */         
/* 1263 */         while (it.hasNext())
/*      */         {
/* 1265 */           Map.Entry<String, Long> entry = (Map.Entry)it.next();
/*      */           
/* 1267 */           long when = ((Long)entry.getValue()).longValue();
/*      */           
/* 1269 */           if (now - when > 60000L)
/*      */           {
/* 1271 */             it.remove();
/*      */           }
/* 1273 */           else if (when != now)
/*      */           {
/* 1275 */             rows.add(new RouteInfo((String)entry.getKey(), null));
/*      */           }
/*      */         }
/*      */         
/* 1279 */         rows.addAll(name_to_route_map.values());
/*      */         
/* 1281 */         Collections.sort(rows, new Comparator()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public int compare(TransferStatsView.RouteInfo o1, TransferStatsView.RouteInfo o2)
/*      */           {
/*      */ 
/*      */ 
/* 1290 */             String n1 = TransferStatsView.RouteInfo.access$1300(o1);
/* 1291 */             String n2 = TransferStatsView.RouteInfo.access$1300(o2);
/*      */             
/*      */ 
/* 1294 */             if ((n1.startsWith("*")) || (n1.equals("Pending"))) {
/* 1295 */               n1 = "zzzz" + n1;
/*      */             }
/* 1297 */             if ((n2.startsWith("*")) || (n2.equals("Pending"))) {
/* 1298 */               n2 = "zzzz" + n2;
/*      */             }
/*      */             
/* 1301 */             return n1.compareTo(n2);
/*      */           }
/*      */           
/* 1304 */         });
/* 1305 */         buildRouteComponent(rows.size());
/*      */         
/* 1307 */         for (int i = 0; i < rows.size(); i++)
/*      */         {
/* 1309 */           RouteInfo info = (RouteInfo)rows.get(i);
/*      */           
/* 1311 */           this.route_labels[i][0].setText(info.getName());
/* 1312 */           this.route_labels[i][1].setText(info.getIncomingString());
/* 1313 */           this.route_labels[i][2].setText(info.getOutgoingString());
/*      */         }
/*      */         
/* 1316 */         buildRouteComponent(rows.size());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class RouteInfo
/*      */   {
/*      */     private String name;
/* 1325 */     private TransferStatsView.RouteInfoRecord incoming = new TransferStatsView.RouteInfoRecord(null);
/* 1326 */     private TransferStatsView.RouteInfoRecord outgoing = new TransferStatsView.RouteInfoRecord(null);
/*      */     
/*      */ 
/*      */ 
/*      */     private RouteInfo(String _name)
/*      */     {
/* 1332 */       this.name = _name;
/*      */     }
/*      */     
/*      */ 
/*      */     private String getName()
/*      */     {
/* 1338 */       return this.name;
/*      */     }
/*      */     
/*      */ 
/*      */     private String getIncomingString()
/*      */     {
/* 1344 */       return TransferStatsView.RouteInfoRecord.access$1700(this.incoming);
/*      */     }
/*      */     
/*      */ 
/*      */     private String getOutgoingString()
/*      */     {
/* 1350 */       return TransferStatsView.RouteInfoRecord.access$1700(this.outgoing);
/*      */     }
/*      */     
/*      */ 
/*      */     private void update(PEPeer peer)
/*      */     {
/*      */       TransferStatsView.RouteInfoRecord record;
/*      */       
/*      */       TransferStatsView.RouteInfoRecord record;
/* 1359 */       if (peer.isIncoming())
/*      */       {
/* 1361 */         record = this.incoming;
/*      */       }
/*      */       else
/*      */       {
/* 1365 */         record = this.outgoing;
/*      */       }
/*      */       
/* 1368 */       TransferStatsView.RouteInfoRecord.access$1800(record, peer);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class RouteInfoRecord
/*      */   {
/*      */     private int peer_count;
/*      */     
/*      */     private int up_rate;
/*      */     
/*      */     private int down_rate;
/*      */     
/*      */     private void update(PEPeer peer)
/*      */     {
/* 1383 */       this.peer_count += 1;
/*      */       
/* 1385 */       PEPeerStats stats = peer.getStats();
/*      */       
/* 1387 */       this.up_rate = ((int)(this.up_rate + (stats.getDataSendRate() + stats.getProtocolSendRate())));
/* 1388 */       this.down_rate = ((int)(this.down_rate + (stats.getDataReceiveRate() + stats.getProtocolReceiveRate())));
/*      */     }
/*      */     
/*      */ 
/*      */     private String getString()
/*      */     {
/* 1394 */       if (this.peer_count == 0)
/*      */       {
/* 1396 */         return "0";
/*      */       }
/*      */       
/* 1399 */       return this.peer_count + ": up=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(this.up_rate) + ", down=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(this.down_rate);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void refreshPingPanel()
/*      */   {
/* 1406 */     if (this.speedManager == null) {
/* 1407 */       return;
/*      */     }
/* 1409 */     if (this.speedManager.isAvailable()) {
/* 1410 */       this.autoSpeedPanelLayout.topControl = this.autoSpeedInfoPanel;
/* 1411 */       this.autoSpeedPanel.layout();
/*      */       
/* 1413 */       this.pingGraph.refresh();
/* 1414 */       for (int i = 0; i < this.plot_views.length; i++)
/*      */       {
/* 1416 */         this.plot_views[i].refresh();
/*      */       }
/*      */       
/* 1419 */       for (int i = 0; i < this.zone_views.length; i++)
/*      */       {
/* 1421 */         this.zone_views[i].refresh();
/*      */       }
/*      */     }
/*      */     else {
/* 1425 */       this.autoSpeedPanelLayout.topControl = this.autoSpeedDisabledPanel;
/* 1426 */       this.autoSpeedPanel.layout();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void periodicUpdate()
/*      */   {
/* 1434 */     if (this.speedManager == null) {
/* 1435 */       return;
/*      */     }
/* 1437 */     if (this.speedManager.isAvailable()) {
/* 1438 */       SpeedManagerPingSource[] sources = this.speedManager.getPingSources();
/* 1439 */       if (sources.length > 0) {
/* 1440 */         int[] pings = new int[sources.length];
/* 1441 */         for (int i = 0; i < sources.length; i++)
/*      */         {
/* 1443 */           SpeedManagerPingSource source = sources[i];
/*      */           
/* 1445 */           if (source != null)
/*      */           {
/* 1447 */             int ping = source.getPingTime();
/*      */             
/* 1449 */             ping = Math.min(ping, 1199);
/*      */             
/* 1451 */             pings[i] = ping;
/*      */           }
/*      */         }
/* 1454 */         this.pingGraph.addIntsValue(pings);
/*      */         
/* 1456 */         if (this.plot_views != null) {
/* 1457 */           for (plotView view : this.plot_views) {
/* 1458 */             if (view != null) {
/* 1459 */               view.update();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1464 */         if (this.zone_views != null) {
/* 1465 */           for (zoneView view : this.zone_views) {
/* 1466 */             if (view != null) {
/* 1467 */               view.update();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getMapperTitle(SpeedManagerPingMapper mapper)
/*      */   {
/* 1480 */     if (mapper.isActive())
/*      */     {
/* 1482 */       SpeedManagerLimitEstimate up_1 = mapper.getEstimatedUploadLimit(false);
/* 1483 */       SpeedManagerLimitEstimate up_2 = mapper.getEstimatedUploadLimit(true);
/* 1484 */       SpeedManagerLimitEstimate down_1 = mapper.getEstimatedDownloadLimit(false);
/* 1485 */       SpeedManagerLimitEstimate down_2 = mapper.getEstimatedDownloadLimit(true);
/*      */       
/* 1487 */       return "ul=" + DisplayFormatters.formatByteCountToKiBEtc(up_1.getBytesPerSec()) + ":" + DisplayFormatters.formatByteCountToKiBEtc(up_2.getBytesPerSec()) + ",dl=" + DisplayFormatters.formatByteCountToKiBEtc(down_1.getBytesPerSec()) + ":" + DisplayFormatters.formatByteCountToKiBEtc(down_2.getBytesPerSec()) + ",mr=" + DisplayFormatters.formatDecimal(mapper.getCurrentMetricRating(), 2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1492 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   class plotView
/*      */   {
/*      */     private SpeedManagerPingMapper mapper;
/*      */     
/*      */ 
/*      */     private Plot3D plotGraph;
/*      */     
/*      */ 
/*      */ 
/*      */     protected plotView(SpeedManagerPingMapper _mapper, Canvas _canvas, String[] _labels, ValueFormater[] _formatters)
/*      */     {
/* 1508 */       this.mapper = _mapper;
/*      */       
/* 1510 */       this.plotGraph = new Plot3D(_labels, _formatters);
/*      */       
/* 1512 */       this.plotGraph.setMaxZ(1199);
/*      */       
/* 1514 */       this.plotGraph.initialize(_canvas);
/*      */     }
/*      */     
/*      */ 
/*      */     protected void update()
/*      */     {
/* 1520 */       int[][] history = this.mapper.getHistory();
/*      */       
/* 1522 */       this.plotGraph.update(history);
/*      */       
/* 1524 */       this.plotGraph.setTitle(TransferStatsView.this.getMapperTitle(this.mapper));
/*      */     }
/*      */     
/*      */ 
/*      */     protected void refresh()
/*      */     {
/* 1530 */       this.plotGraph.refresh(false);
/*      */     }
/*      */     
/*      */ 
/*      */     protected void dispose()
/*      */     {
/* 1536 */       this.plotGraph.dispose();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   class zoneView
/*      */     implements ParameterListener
/*      */   {
/*      */     private SpeedManagerPingMapper mapper;
/*      */     
/* 1546 */     private SpeedManagerPingZone[] zones = new SpeedManagerPingZone[0];
/*      */     
/*      */     private Canvas canvas;
/*      */     
/*      */     private ValueFormater[] formatters;
/*      */     
/*      */     private String[] labels;
/*      */     
/* 1554 */     private String title = "";
/*      */     
/*      */ 
/*      */     private int refresh_count;
/*      */     
/*      */ 
/*      */     private int graphicsUpdate;
/*      */     
/*      */     private Point old_size;
/*      */     
/*      */     protected Image buffer_image;
/*      */     
/*      */ 
/*      */     protected zoneView(SpeedManagerPingMapper _mapper, Canvas _canvas, String[] _labels, ValueFormater[] _formatters)
/*      */     {
/* 1569 */       this.mapper = _mapper;
/* 1570 */       this.canvas = _canvas;
/* 1571 */       this.labels = _labels;
/* 1572 */       this.formatters = _formatters;
/*      */       
/* 1574 */       COConfigurationManager.addAndFireParameterListener("Graphics Update", this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void parameterChanged(String name)
/*      */     {
/* 1581 */       this.graphicsUpdate = COConfigurationManager.getIntParameter(name);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void update()
/*      */     {
/* 1588 */       this.zones = this.mapper.getZones();
/*      */       
/* 1590 */       this.title = TransferStatsView.this.getMapperTitle(this.mapper);
/*      */     }
/*      */     
/*      */ 
/*      */     private void refresh()
/*      */     {
/* 1596 */       if (this.canvas.isDisposed())
/*      */       {
/* 1598 */         return;
/*      */       }
/*      */       
/* 1601 */       Rectangle bounds = this.canvas.getClientArea();
/*      */       
/* 1603 */       if ((bounds.height < 30) || (bounds.width < 100) || (bounds.width > 2000) || (bounds.height > 2000))
/*      */       {
/* 1605 */         return;
/*      */       }
/*      */       
/* 1608 */       boolean size_changed = (this.old_size == null) || (this.old_size.x != bounds.width) || (this.old_size.y != bounds.height);
/*      */       
/* 1610 */       this.old_size = new Point(bounds.width, bounds.height);
/*      */       
/* 1612 */       this.refresh_count += 1;
/*      */       
/* 1614 */       if (this.refresh_count > this.graphicsUpdate)
/*      */       {
/* 1616 */         this.refresh_count = 0;
/*      */       }
/*      */       
/* 1619 */       if ((this.refresh_count == 0) || (size_changed))
/*      */       {
/* 1621 */         if ((this.buffer_image != null) && (!this.buffer_image.isDisposed()))
/*      */         {
/* 1623 */           this.buffer_image.dispose();
/*      */         }
/*      */         
/* 1626 */         this.buffer_image = draw(bounds);
/*      */       }
/*      */       
/* 1629 */       if (this.buffer_image != null)
/*      */       {
/* 1631 */         GC gc = new GC(this.canvas);
/*      */         
/* 1633 */         gc.drawImage(this.buffer_image, bounds.x, bounds.y);
/*      */         
/* 1635 */         gc.dispose();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private Image draw(Rectangle bounds)
/*      */     {
/* 1643 */       int PAD_TOP = 10;
/* 1644 */       int PAD_BOTTOM = 10;
/* 1645 */       int PAD_RIGHT = 10;
/* 1646 */       int PAD_LEFT = 10;
/*      */       
/* 1648 */       int usable_width = bounds.width - 10 - 10;
/* 1649 */       int usable_height = bounds.height - 10 - 10;
/*      */       
/* 1651 */       Image image = new Image(this.canvas.getDisplay(), bounds);
/*      */       
/* 1653 */       GC gc = new GC(image);
/*      */       try
/*      */       {
/* 1656 */         gc.setAntialias(1);
/*      */       }
/*      */       catch (Exception e) {}
/*      */       
/*      */ 
/* 1661 */       int font_height = gc.getFontMetrics().getHeight();
/* 1662 */       int char_width = gc.getFontMetrics().getAverageCharWidth();
/*      */       
/*      */ 
/* 1665 */       Color[] colours = TransferStatsView.access$2000(TransferStatsView.this)[0].plotGraph.getColours();
/*      */       
/* 1667 */       int max_x = 0;
/* 1668 */       int max_y = 0;
/*      */       
/* 1670 */       if (this.zones.length > 0)
/*      */       {
/* 1672 */         int max_metric = 0;
/*      */         
/* 1674 */         for (int i = 0; i < this.zones.length; i++)
/*      */         {
/* 1676 */           SpeedManagerPingZone zone = this.zones[i];
/*      */           
/* 1678 */           int metric = zone.getMetric();
/*      */           
/* 1680 */           if (metric > 0)
/*      */           {
/* 1682 */             max_metric = Math.max(max_metric, metric);
/*      */             
/* 1684 */             max_x = Math.max(max_x, zone.getUploadEndBytesPerSec());
/* 1685 */             max_y = Math.max(max_y, zone.getDownloadEndBytesPerSec());
/*      */           }
/*      */         }
/*      */         
/* 1689 */         if ((max_x > 0) && (max_y > 0))
/*      */         {
/* 1691 */           double x_ratio = usable_width / max_x;
/* 1692 */           double y_ratio = usable_height / max_y;
/*      */           
/* 1694 */           List<Object[]> texts = new ArrayList();
/*      */           
/* 1696 */           for (int i = 0; i < this.zones.length; i++)
/*      */           {
/* 1698 */             SpeedManagerPingZone zone = this.zones[i];
/*      */             
/* 1700 */             int metric = zone.getMetric();
/* 1701 */             int x1 = zone.getUploadStartBytesPerSec();
/* 1702 */             int y1 = zone.getDownloadStartBytesPerSec();
/* 1703 */             int x2 = zone.getUploadEndBytesPerSec();
/* 1704 */             int y2 = zone.getDownloadEndBytesPerSec();
/*      */             
/* 1706 */             if (metric > 0)
/*      */             {
/* 1708 */               int colour_index = (int)(metric * colours.length / max_metric);
/*      */               
/* 1710 */               if (colour_index >= colours.length)
/*      */               {
/* 1712 */                 colour_index = colours.length - 1;
/*      */               }
/*      */               
/* 1715 */               gc.setBackground(colours[colour_index]);
/*      */               
/* 1717 */               int x = 10 + (int)(x1 * x_ratio);
/* 1718 */               int y = 10 + (int)(y1 * y_ratio);
/* 1719 */               int width = (int)Math.ceil((x2 - x1 + 1) * x_ratio);
/* 1720 */               int height = (int)Math.ceil((y2 - y1 + 1) * y_ratio);
/*      */               
/* 1722 */               int y_draw = usable_height + 10 + 10 - y - height;
/*      */               
/* 1724 */               gc.fillRectangle(x, y_draw, width, height);
/*      */               
/* 1726 */               int text_metric = zone.getMetric();
/*      */               
/* 1728 */               String text = String.valueOf(metric);
/*      */               
/* 1730 */               int text_width = text.length() * char_width + 4;
/*      */               
/* 1732 */               if ((width >= text_width) && (height >= font_height))
/*      */               {
/*      */ 
/* 1735 */                 Rectangle text_rect = new Rectangle(x + (width - text_width) / 2, y_draw + (height - font_height) / 2, text_width, font_height);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1743 */                 Iterator<Object[]> it = texts.iterator();
/*      */                 
/* 1745 */                 while (it.hasNext())
/*      */                 {
/* 1747 */                   Object[] old = (Object[])it.next();
/*      */                   
/* 1749 */                   Rectangle old_coords = (Rectangle)old[1];
/*      */                   
/* 1751 */                   if (old_coords.intersects(text_rect))
/*      */                   {
/* 1753 */                     it.remove();
/*      */                   }
/*      */                 }
/*      */                 
/* 1757 */                 texts.add(new Object[] { new Integer(text_metric), text_rect });
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1764 */           int text_num = texts.size();
/*      */           
/* 1766 */           for (int i = text_num > 100 ? text_num - 100 : 0; i < text_num; i++)
/*      */           {
/* 1768 */             Object[] entry = (Object[])texts.get(i);
/*      */             
/* 1770 */             String str = String.valueOf(entry[0]);
/*      */             
/* 1772 */             Rectangle rect = (Rectangle)entry[1];
/*      */             
/* 1774 */             gc.drawText(str, rect.x, rect.y, 1);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1781 */       int x_axis_left_x = 10;
/* 1782 */       int x_axis_left_y = usable_height + 10;
/*      */       
/* 1784 */       int x_axis_right_x = 10 + usable_width;
/* 1785 */       int x_axis_right_y = x_axis_left_y;
/*      */       
/*      */ 
/* 1788 */       gc.drawLine(x_axis_left_x, x_axis_left_y, x_axis_right_x, x_axis_right_y);
/* 1789 */       gc.drawLine(usable_width, x_axis_right_y - 4, x_axis_right_x, x_axis_right_y);
/* 1790 */       gc.drawLine(usable_width, x_axis_right_y + 4, x_axis_right_x, x_axis_right_y);
/*      */       
/* 1792 */       for (int i = 1; i < 10; i++)
/*      */       {
/* 1794 */         int x = x_axis_left_x + (x_axis_right_x - x_axis_left_x) * i / 10;
/*      */         
/* 1796 */         gc.drawLine(x, x_axis_left_y, x, x_axis_left_y + 4);
/*      */       }
/*      */       
/* 1799 */       SpeedManagerLimitEstimate le = this.mapper.getEstimatedUploadLimit(false);
/*      */       
/* 1801 */       if (le != null)
/*      */       {
/* 1803 */         gc.setForeground(Colors.grey);
/*      */         
/* 1805 */         int[][] segs = le.getSegments();
/*      */         
/* 1807 */         if (segs.length > 0)
/*      */         {
/* 1809 */           int max_metric = 0;
/* 1810 */           int max_pos = 0;
/*      */           
/* 1812 */           for (int i = 0; i < segs.length; i++)
/*      */           {
/* 1814 */             int[] seg = segs[i];
/*      */             
/* 1816 */             max_metric = Math.max(max_metric, seg[0]);
/* 1817 */             max_pos = Math.max(max_pos, seg[2]);
/*      */           }
/*      */           
/* 1820 */           double metric_ratio = max_metric == 0 ? 1.0D : 50.0F / max_metric;
/* 1821 */           double pos_ratio = max_pos == 0 ? 1.0D : usable_width / max_pos;
/*      */           
/* 1823 */           int prev_x = 1;
/* 1824 */           int prev_y = 1;
/*      */           
/* 1826 */           for (int i = 0; i < segs.length; i++)
/*      */           {
/* 1828 */             int[] seg = segs[i];
/*      */             
/* 1830 */             int next_x = (int)((seg[1] + (seg[2] - seg[1]) / 2) * pos_ratio) + 1;
/* 1831 */             int next_y = (int)(seg[0] * metric_ratio) + 1;
/*      */             
/* 1833 */             gc.drawLine(x_axis_left_x + prev_x, x_axis_left_y - prev_y, x_axis_left_x + next_x, x_axis_left_y - next_y);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1839 */             prev_x = next_x;
/* 1840 */             prev_y = next_y;
/*      */           }
/*      */         }
/*      */         
/* 1844 */         gc.setForeground(Colors.black);
/*      */       }
/*      */       
/* 1847 */       SpeedManagerLimitEstimate[] bad_up = this.mapper.getBadUploadHistory();
/*      */       
/* 1849 */       if (bad_up.length > 0)
/*      */       {
/* 1851 */         gc.setLineWidth(3);
/*      */         
/* 1853 */         gc.setForeground(Colors.red);
/*      */         
/* 1855 */         for (int i = 0; i < bad_up.length; i++)
/*      */         {
/* 1857 */           int speed = bad_up[i].getBytesPerSec();
/*      */           
/* 1859 */           int x = max_x == 0 ? 0 : speed * usable_width / max_x;
/*      */           
/* 1861 */           gc.drawLine(x_axis_left_x + x, x_axis_left_y - 0, x_axis_left_x + x, x_axis_left_y - 10);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1869 */         gc.setForeground(Colors.black);
/*      */         
/* 1871 */         gc.setLineWidth(1);
/*      */       }
/*      */       
/* 1874 */       String x_text = this.labels[0] + " - " + this.formatters[0].format(max_x + 1);
/*      */       
/* 1876 */       gc.drawText(x_text, x_axis_right_x - 20 - x_text.length() * char_width, x_axis_right_y - font_height - 2, 1);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1883 */       int y_axis_bottom_x = 10;
/* 1884 */       int y_axis_bottom_y = usable_height + 10;
/*      */       
/* 1886 */       int y_axis_top_x = 10;
/* 1887 */       int y_axis_top_y = 10;
/*      */       
/* 1889 */       gc.drawLine(y_axis_bottom_x, y_axis_bottom_y, y_axis_top_x, y_axis_top_y);
/*      */       
/* 1891 */       gc.drawLine(y_axis_top_x - 4, y_axis_top_y + 10, y_axis_top_x, y_axis_top_y);
/* 1892 */       gc.drawLine(y_axis_top_x + 4, y_axis_top_y + 10, y_axis_top_x, y_axis_top_y);
/*      */       
/* 1894 */       for (int i = 1; i < 10; i++)
/*      */       {
/* 1896 */         int y = y_axis_bottom_y + (y_axis_top_y - y_axis_bottom_y) * i / 10;
/*      */         
/* 1898 */         gc.drawLine(y_axis_bottom_x, y, y_axis_bottom_x - 4, y);
/*      */       }
/*      */       
/* 1901 */       le = this.mapper.getEstimatedDownloadLimit(false);
/*      */       
/* 1903 */       if (le != null)
/*      */       {
/* 1905 */         gc.setForeground(Colors.grey);
/*      */         
/* 1907 */         int[][] segs = le.getSegments();
/*      */         
/* 1909 */         if (segs.length > 0)
/*      */         {
/* 1911 */           int max_metric = 0;
/* 1912 */           int max_pos = 0;
/*      */           
/* 1914 */           for (int i = 0; i < segs.length; i++)
/*      */           {
/* 1916 */             int[] seg = segs[i];
/*      */             
/* 1918 */             max_metric = Math.max(max_metric, seg[0]);
/* 1919 */             max_pos = Math.max(max_pos, seg[2]);
/*      */           }
/*      */           
/* 1922 */           double metric_ratio = max_metric == 0 ? 1.0D : 50.0F / max_metric;
/* 1923 */           double pos_ratio = max_pos == 0 ? 1.0D : usable_height / max_pos;
/*      */           
/* 1925 */           int prev_x = 1;
/* 1926 */           int prev_y = 1;
/*      */           
/* 1928 */           for (int i = 0; i < segs.length; i++)
/*      */           {
/* 1930 */             int[] seg = segs[i];
/*      */             
/* 1932 */             int next_x = (int)(seg[0] * metric_ratio) + 1;
/* 1933 */             int next_y = (int)((seg[1] + (seg[2] - seg[1]) / 2) * pos_ratio) + 1;
/*      */             
/* 1935 */             gc.drawLine(y_axis_bottom_x + prev_x, y_axis_bottom_y - prev_y, y_axis_bottom_x + next_x, y_axis_bottom_y - next_y);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1941 */             prev_x = next_x;
/* 1942 */             prev_y = next_y;
/*      */           }
/*      */         }
/*      */         
/* 1946 */         gc.setForeground(Colors.black);
/*      */       }
/*      */       
/* 1949 */       SpeedManagerLimitEstimate[] bad_down = this.mapper.getBadDownloadHistory();
/*      */       
/* 1951 */       if (bad_down.length > 0)
/*      */       {
/* 1953 */         gc.setForeground(Colors.red);
/*      */         
/* 1955 */         gc.setLineWidth(3);
/*      */         
/* 1957 */         for (int i = 0; i < bad_down.length; i++)
/*      */         {
/* 1959 */           int speed = bad_down[i].getBytesPerSec();
/*      */           
/* 1961 */           int y = max_y == 0 ? 0 : speed * usable_height / max_y;
/*      */           
/* 1963 */           gc.drawLine(y_axis_bottom_x + 0, y_axis_bottom_y - y, y_axis_bottom_x + 10, y_axis_bottom_y - y);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1970 */         gc.setForeground(Colors.black);
/*      */         
/* 1972 */         gc.setLineWidth(1);
/*      */       }
/*      */       
/* 1975 */       String y_text = this.labels[1] + " - " + this.formatters[1].format(max_y + 1);
/*      */       
/* 1977 */       gc.drawText(y_text, y_axis_top_x + 4, y_axis_top_y + 2, 1);
/*      */       
/* 1979 */       gc.drawText(this.title, (bounds.width - this.title.length() * char_width) / 2, 1, 1);
/*      */       
/* 1981 */       gc.dispose();
/*      */       
/* 1983 */       return image;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void dispose()
/*      */     {
/* 1989 */       if ((this.buffer_image != null) && (!this.buffer_image.isDisposed()))
/*      */       {
/* 1991 */         this.buffer_image.dispose();
/*      */       }
/*      */       
/* 1994 */       COConfigurationManager.removeParameterListener("Graphics Update", this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static class limitToTextHelper
/*      */   {
/*      */     String msg_text_unknown;
/*      */     
/*      */     String msg_text_estimate;
/*      */     
/*      */     String msg_text_choke_estimate;
/*      */     String msg_text_measured_min;
/*      */     String msg_text_measured;
/*      */     String msg_text_manual;
/*      */     String msg_unlimited;
/*      */     String[] setable_types;
/*      */     
/*      */     public limitToTextHelper()
/*      */     {
/* 2014 */       this.msg_text_unknown = MessageText.getString("SpeedView.stats.unknown");
/* 2015 */       this.msg_text_estimate = MessageText.getString("SpeedView.stats.estimate");
/* 2016 */       this.msg_text_choke_estimate = MessageText.getString("SpeedView.stats.estimatechoke");
/* 2017 */       this.msg_text_measured = MessageText.getString("SpeedView.stats.measured");
/* 2018 */       this.msg_text_measured_min = MessageText.getString("SpeedView.stats.measuredmin");
/* 2019 */       this.msg_text_manual = MessageText.getString("SpeedView.stats.manual");
/*      */       
/* 2021 */       this.msg_unlimited = MessageText.getString("ConfigView.unlimited");
/*      */       
/* 2023 */       this.setable_types = new String[] { "", this.msg_text_estimate, this.msg_text_measured, this.msg_text_manual };
/*      */     }
/*      */     
/*      */ 
/*      */     public String[] getSettableTypes()
/*      */     {
/* 2029 */       return this.setable_types;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getSettableType(SpeedManagerLimitEstimate limit)
/*      */     {
/* 2036 */       float type = limit.getEstimateType();
/*      */       
/*      */       String text;
/*      */       String text;
/* 2040 */       if (type == -0.1F)
/*      */       {
/* 2042 */         text = "";
/*      */       } else { String text;
/* 2044 */         if (type == 1.0F)
/*      */         {
/* 2046 */           text = this.msg_text_manual;
/*      */         } else { String text;
/* 2048 */           if (type == 0.9F)
/*      */           {
/* 2050 */             text = this.msg_text_measured;
/*      */           } else { String text;
/* 2052 */             if (type == 0.8F)
/*      */             {
/* 2054 */               text = this.msg_text_measured;
/*      */             } else { String text;
/* 2056 */               if (type == 0.5F)
/*      */               {
/* 2058 */                 text = this.msg_text_estimate;
/*      */               }
/*      */               else
/*      */               {
/* 2062 */                 text = this.msg_text_estimate; }
/*      */             }
/*      */           } } }
/* 2065 */       return text;
/*      */     }
/*      */     
/*      */ 
/*      */     public String typeToText(float type)
/*      */     {
/*      */       String text;
/*      */       String text;
/* 2073 */       if (type == -0.1F)
/*      */       {
/* 2075 */         text = this.msg_text_unknown;
/*      */       } else { String text;
/* 2077 */         if (type == 1.0F)
/*      */         {
/* 2079 */           text = this.msg_text_manual;
/*      */         } else { String text;
/* 2081 */           if (type == 0.9F)
/*      */           {
/* 2083 */             text = this.msg_text_measured;
/*      */           } else { String text;
/* 2085 */             if (type == 0.8F)
/*      */             {
/* 2087 */               text = this.msg_text_measured_min;
/*      */             } else { String text;
/* 2089 */               if (type == 0.5F)
/*      */               {
/* 2091 */                 text = this.msg_text_choke_estimate;
/*      */               }
/*      */               else
/*      */               {
/* 2095 */                 text = this.msg_text_estimate; }
/*      */             }
/*      */           } } }
/* 2098 */       return text;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public float textToType(String text)
/*      */     {
/* 2105 */       if (text.equals(this.msg_text_estimate))
/*      */       {
/* 2107 */         return 0.0F;
/*      */       }
/* 2109 */       if (text.equals(this.msg_text_choke_estimate))
/*      */       {
/* 2111 */         return 0.5F;
/*      */       }
/* 2113 */       if (text.equals(this.msg_text_measured))
/*      */       {
/* 2115 */         return 0.9F;
/*      */       }
/* 2117 */       if (text.equals(this.msg_text_manual))
/*      */       {
/* 2119 */         return 1.0F;
/*      */       }
/*      */       
/*      */ 
/* 2123 */       return -0.1F;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getLimitText(SpeedManagerLimitEstimate limit)
/*      */     {
/* 2131 */       float type = limit.getEstimateType();
/*      */       
/* 2133 */       String text = typeToText(type);
/*      */       
/* 2135 */       int l = limit.getBytesPerSec();
/*      */       
/* 2137 */       if (l == 0)
/*      */       {
/* 2139 */         return this.msg_unlimited + " (" + text + ")";
/*      */       }
/*      */       
/*      */ 
/* 2143 */       return DisplayFormatters.formatByteCountToKiBEtcPerSec(l) + " (" + text + ")";
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getUnlimited()
/*      */     {
/* 2150 */       return this.msg_unlimited;
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean eventOccurred(UISWTViewEvent event) {
/* 2155 */     switch (event.getType()) {
/*      */     case 0: 
/* 2157 */       this.swtView = ((UISWTView)event.getData());
/* 2158 */       this.swtView.setTitle(MessageText.getString("TransferStatsView.title.full"));
/* 2159 */       break;
/*      */     
/*      */     case 7: 
/* 2162 */       delete();
/* 2163 */       break;
/*      */     
/*      */     case 2: 
/* 2166 */       initialize((Composite)event.getData());
/* 2167 */       break;
/*      */     
/*      */     case 6: 
/* 2170 */       Messages.updateLanguageForControl(getComposite());
/* 2171 */       break;
/*      */     
/*      */     case 1: 
/*      */       break;
/*      */     
/*      */ 
/*      */     case 3: 
/* 2178 */       if (this.generalPanel != null) {
/* 2179 */         this.generalPanel.layout(true, true);
/*      */       }
/*      */       
/*      */       break;
/*      */     case 5: 
/* 2184 */       refresh();
/* 2185 */       break;
/*      */     
/*      */     case 256: 
/* 2188 */       periodicUpdate();
/*      */     }
/*      */     
/*      */     
/* 2192 */     return true;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/stats/TransferStatsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */