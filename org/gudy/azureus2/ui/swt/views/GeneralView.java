/*      */ package org.gudy.azureus2.ui.swt.views;
/*      */ 
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*      */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*      */ import com.aelitis.azureus.util.MapUtils;
/*      */ import java.io.File;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.text.DecimalFormat;
/*      */ import java.util.Map;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.swt.custom.ScrolledComposite;
/*      */ import org.eclipse.swt.dnd.Clipboard;
/*      */ import org.eclipse.swt.dnd.TextTransfer;
/*      */ import org.eclipse.swt.dnd.Transfer;
/*      */ import org.eclipse.swt.events.ControlAdapter;
/*      */ import org.eclipse.swt.events.ControlEvent;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Group;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Link;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*      */ import org.gudy.azureus2.ui.swt.debug.UIDebugGenerator;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
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
/*      */ 
/*      */ public class GeneralView
/*      */   implements ParameterListener, UISWTViewCoreEventListenerEx, UIPluginViewToolBarListener
/*      */ {
/*      */   public static final String MSGID_PREFIX = "GeneralView";
/*   97 */   protected AEMonitor this_mon = new AEMonitor("GeneralView");
/*      */   
/*      */   private Display display;
/*  100 */   private DownloadManager manager = null;
/*      */   
/*      */   int[] piecesStateCache;
/*      */   
/*      */   long piecesStateSkippedMarker;
/*      */   
/*      */   boolean piecesStateFileBoundariesDone;
/*      */   
/*      */   int loopFactor;
/*      */   
/*      */   Composite genComposite;
/*      */   
/*      */   Composite gFile;
/*      */   
/*      */   Canvas piecesImage;
/*      */   
/*      */   Image pImage;
/*      */   BufferedLabel piecesPercent;
/*      */   Canvas availabilityImage;
/*      */   Image aImage;
/*      */   BufferedLabel availabilityPercent;
/*      */   Group gTransfer;
/*      */   BufferedLabel timeElapsed;
/*      */   BufferedLabel timeRemaining;
/*      */   BufferedLabel download;
/*      */   BufferedLabel downloadSpeed;
/*      */   BufferedLabel upload;
/*      */   BufferedLabel uploadSpeed;
/*      */   BufferedLabel totalSpeed;
/*      */   BufferedLabel ave_completion;
/*      */   BufferedLabel distributedCopies;
/*      */   BufferedLabel seeds;
/*      */   BufferedLabel peers;
/*      */   BufferedLabel completedLbl;
/*      */   Group gInfo;
/*      */   BufferedLabel fileName;
/*      */   BufferedLabel torrentStatus;
/*      */   BufferedLabel fileSize;
/*      */   BufferedLabel saveIn;
/*      */   BufferedLabel hash;
/*      */   BufferedLabel pieceNumber;
/*      */   BufferedLabel pieceSize;
/*      */   Control lblComment;
/*      */   BufferedLabel creation_date;
/*      */   BufferedLabel privateStatus;
/*      */   Control user_comment;
/*      */   BufferedLabel hashFails;
/*      */   BufferedLabel shareRatio;
/*  148 */   private int graphicsUpdate = COConfigurationManager.getIntParameter("Graphics Update");
/*      */   
/*      */ 
/*      */   private boolean piecesImageRefreshNeeded;
/*      */   
/*      */ 
/*      */   private Composite parent;
/*      */   
/*      */   private ScrolledComposite scrolled_comp;
/*      */   
/*      */   private UISWTView swtView;
/*      */   
/*      */ 
/*      */   public void dataSourceChanged(Object newDataSource)
/*      */   {
/*  163 */     DownloadManager newManager = ViewUtils.getDownloadManagerFromDataSource(newDataSource);
/*      */     
/*  165 */     if (newManager == this.manager) {
/*  166 */       return;
/*      */     }
/*      */     
/*  169 */     this.manager = newManager;
/*      */     
/*  171 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  173 */         GeneralView.this.swt_refreshInfo();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isCloneable()
/*      */   {
/*  181 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public UISWTViewCoreEventListener getClone()
/*      */   {
/*  187 */     return new GeneralView();
/*      */   }
/*      */   
/*      */   public void initialize(Composite composite) {
/*  191 */     this.parent = composite;
/*      */     
/*  193 */     this.scrolled_comp = new ScrolledComposite(composite, 512);
/*  194 */     this.scrolled_comp.setExpandHorizontal(true);
/*  195 */     this.scrolled_comp.setExpandVertical(true);
/*  196 */     GridLayout layout = new GridLayout();
/*  197 */     layout.horizontalSpacing = 0;
/*  198 */     layout.verticalSpacing = 0;
/*  199 */     layout.marginHeight = 0;
/*  200 */     layout.marginWidth = 0;
/*  201 */     this.scrolled_comp.setLayout(layout);
/*  202 */     GridData gridData = new GridData(4, 4, true, true, 2, 1);
/*  203 */     Utils.setLayoutData(this.scrolled_comp, gridData);
/*      */     
/*  205 */     this.genComposite = new Canvas(this.scrolled_comp, 0);
/*      */     
/*      */ 
/*  208 */     GridLayout genLayout = new GridLayout();
/*  209 */     genLayout.marginHeight = 0;
/*      */     try {
/*  211 */       genLayout.marginTop = 5;
/*      */     }
/*      */     catch (NoSuchFieldError e) {}
/*      */     
/*  215 */     genLayout.marginWidth = 2;
/*  216 */     genLayout.numColumns = 1;
/*  217 */     this.genComposite.setLayout(genLayout);
/*      */     
/*  219 */     this.scrolled_comp.setContent(this.genComposite);
/*  220 */     this.scrolled_comp.addControlListener(new ControlAdapter() {
/*      */       public void controlResized(ControlEvent e) {
/*  222 */         GeneralView.this.piecesImageRefreshNeeded = true;
/*  223 */         Utils.updateScrolledComposite(GeneralView.this.scrolled_comp);
/*      */       }
/*      */       
/*      */ 
/*  227 */     });
/*  228 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/*  230 */         GeneralView.this.swt_refreshInfo();
/*      */       }
/*      */       
/*  233 */     });
/*  234 */     COConfigurationManager.addParameterListener("Graphics Update", this);
/*      */   }
/*      */   
/*      */   private void swt_refreshInfo() {
/*  238 */     if ((this.manager == null) || (this.parent == null) || (this.parent.isDisposed())) {
/*  239 */       ViewUtils.setViewRequiresOneDownload(this.genComposite);
/*  240 */       return;
/*      */     }
/*      */     
/*  243 */     Utils.disposeComposite(this.genComposite, false);
/*      */     
/*  245 */     this.piecesStateCache = new int[this.manager.getNbPieces()];
/*      */     
/*  247 */     this.piecesStateSkippedMarker = 0L;
/*  248 */     this.piecesStateFileBoundariesDone = false;
/*      */     
/*  250 */     this.display = this.parent.getDisplay();
/*      */     
/*  252 */     this.gFile = new Composite(this.genComposite, 8);
/*  253 */     GridData gridData = new GridData(768);
/*  254 */     Utils.setLayoutData(this.gFile, gridData);
/*  255 */     GridLayout fileLayout = new GridLayout();
/*  256 */     fileLayout.marginHeight = 0;
/*  257 */     fileLayout.marginWidth = 10;
/*  258 */     fileLayout.numColumns = 3;
/*  259 */     this.gFile.setLayout(fileLayout);
/*      */     
/*  261 */     Label piecesInfo = new Label(this.gFile, 16384);
/*  262 */     Messages.setLanguageText(piecesInfo, "GeneralView.section.downloaded");
/*  263 */     gridData = new GridData(32);
/*  264 */     Utils.setLayoutData(piecesInfo, gridData);
/*      */     
/*  266 */     this.piecesImage = new Canvas(this.gFile, 536870912);
/*  267 */     gridData = new GridData(768);
/*  268 */     gridData.widthHint = 150;
/*  269 */     gridData.heightHint = 25;
/*  270 */     Utils.setLayoutData(this.piecesImage, gridData);
/*      */     
/*  272 */     this.piecesPercent = new BufferedLabel(this.gFile, 537001984);
/*  273 */     gridData = new GridData(128);
/*  274 */     gridData.widthHint = 50;
/*  275 */     Utils.setLayoutData(this.piecesPercent, gridData);
/*      */     
/*  277 */     Label availabilityInfo = new Label(this.gFile, 16384);
/*  278 */     Messages.setLanguageText(availabilityInfo, "GeneralView.section.availability");
/*  279 */     gridData = new GridData(32);
/*  280 */     Utils.setLayoutData(availabilityInfo, gridData);
/*      */     
/*  282 */     this.availabilityImage = new Canvas(this.gFile, 536870912);
/*  283 */     gridData = new GridData(768);
/*  284 */     gridData.widthHint = 150;
/*  285 */     gridData.heightHint = 25;
/*  286 */     Utils.setLayoutData(this.availabilityImage, gridData);
/*  287 */     Messages.setLanguageText(this.availabilityImage, "GeneralView.label.status.pieces_available.tooltip");
/*      */     
/*  289 */     this.availabilityPercent = new BufferedLabel(this.gFile, 537001984);
/*  290 */     gridData = new GridData(128);
/*  291 */     gridData.widthHint = 50;
/*  292 */     Utils.setLayoutData(this.availabilityPercent, gridData);
/*  293 */     Messages.setLanguageText(this.availabilityPercent.getWidget(), "GeneralView.label.status.pieces_available.tooltip");
/*      */     
/*  295 */     this.gTransfer = new Group(this.genComposite, 8);
/*  296 */     Messages.setLanguageText(this.gTransfer, "GeneralView.section.transfer");
/*  297 */     gridData = new GridData(768);
/*  298 */     Utils.setLayoutData(this.gTransfer, gridData);
/*      */     
/*  300 */     GridLayout layoutTransfer = new GridLayout();
/*  301 */     layoutTransfer.numColumns = 6;
/*  302 */     this.gTransfer.setLayout(layoutTransfer);
/*      */     
/*  304 */     Label label = new Label(this.gTransfer, 16384);
/*  305 */     Messages.setLanguageText(label, "GeneralView.label.timeelapsed");
/*  306 */     this.timeElapsed = new BufferedLabel(this.gTransfer, 536887296);
/*  307 */     gridData = new GridData(768);
/*  308 */     Utils.setLayoutData(this.timeElapsed, gridData);
/*  309 */     label = new Label(this.gTransfer, 16384);
/*  310 */     Messages.setLanguageText(label, "GeneralView.label.remaining");
/*  311 */     this.timeRemaining = new BufferedLabel(this.gTransfer, 536887296);
/*  312 */     gridData = new GridData(768);
/*  313 */     Utils.setLayoutData(this.timeRemaining, gridData);
/*  314 */     label = new Label(this.gTransfer, 16384);
/*  315 */     Messages.setLanguageText(label, "GeneralView.label.shareRatio");
/*  316 */     this.shareRatio = new BufferedLabel(this.gTransfer, 536887296);
/*  317 */     gridData = new GridData(768);
/*  318 */     Utils.setLayoutData(this.shareRatio, gridData);
/*      */     
/*  320 */     label = new Label(this.gTransfer, 16384);
/*  321 */     Messages.setLanguageText(label, "GeneralView.label.downloaded");
/*  322 */     this.download = new BufferedLabel(this.gTransfer, 536887296);
/*  323 */     gridData = new GridData(768);
/*  324 */     Utils.setLayoutData(this.download, gridData);
/*  325 */     label = new Label(this.gTransfer, 16384);
/*  326 */     Messages.setLanguageText(label, "GeneralView.label.downloadspeed");
/*  327 */     this.downloadSpeed = new BufferedLabel(this.gTransfer, 536887296);
/*  328 */     gridData = new GridData(768);
/*  329 */     Utils.setLayoutData(this.downloadSpeed, gridData);
/*  330 */     label = new Label(this.gTransfer, 16384);
/*  331 */     Messages.setLanguageText(label, "GeneralView.label.hashfails");
/*  332 */     this.hashFails = new BufferedLabel(this.gTransfer, 16384);
/*  333 */     gridData = new GridData(768);
/*  334 */     Utils.setLayoutData(this.hashFails, gridData);
/*      */     
/*  336 */     label = new Label(this.gTransfer, 16384);
/*  337 */     Messages.setLanguageText(label, "GeneralView.label.uploaded");
/*  338 */     this.upload = new BufferedLabel(this.gTransfer, 536887296);
/*  339 */     gridData = new GridData(768);
/*  340 */     Utils.setLayoutData(this.upload, gridData);
/*  341 */     label = new Label(this.gTransfer, 16384);
/*  342 */     Messages.setLanguageText(label, "GeneralView.label.uploadspeed");
/*  343 */     this.uploadSpeed = new BufferedLabel(this.gTransfer, 536887296);
/*  344 */     gridData = new GridData(768);
/*  345 */     gridData.horizontalSpan = 3;
/*  346 */     Utils.setLayoutData(this.uploadSpeed, gridData);
/*      */     
/*      */ 
/*      */ 
/*  350 */     label = new Label(this.gTransfer, 16384);
/*  351 */     Messages.setLanguageText(label, "GeneralView.label.seeds");
/*  352 */     this.seeds = new BufferedLabel(this.gTransfer, 536887296);
/*  353 */     gridData = new GridData(768);
/*  354 */     Utils.setLayoutData(this.seeds, gridData);
/*      */     
/*  356 */     label = new Label(this.gTransfer, 16384);
/*  357 */     Messages.setLanguageText(label, "GeneralView.label.peers");
/*  358 */     this.peers = new BufferedLabel(this.gTransfer, 536887296);
/*  359 */     gridData = new GridData(768);
/*  360 */     Utils.setLayoutData(this.peers, gridData);
/*      */     
/*  362 */     label = new Label(this.gTransfer, 16384);
/*  363 */     Messages.setLanguageText(label, "GeneralView.label.completed");
/*  364 */     this.completedLbl = new BufferedLabel(this.gTransfer, 536887296);
/*  365 */     gridData = new GridData(768);
/*  366 */     Utils.setLayoutData(this.completedLbl, gridData);
/*      */     
/*      */ 
/*      */ 
/*  370 */     label = new Label(this.gTransfer, 16384);
/*  371 */     Messages.setLanguageText(label, "GeneralView.label.totalspeed");
/*  372 */     this.totalSpeed = new BufferedLabel(this.gTransfer, 536887296);
/*  373 */     gridData = new GridData(768);
/*  374 */     Utils.setLayoutData(this.totalSpeed, gridData);
/*      */     
/*      */ 
/*  377 */     label = new Label(this.gTransfer, 16384);
/*  378 */     Messages.setLanguageText(label, "GeneralView.label.swarm_average_completion");
/*  379 */     this.ave_completion = new BufferedLabel(this.gTransfer, 536887296);
/*  380 */     gridData = new GridData(768);
/*  381 */     Utils.setLayoutData(this.ave_completion, gridData);
/*      */     
/*  383 */     label = new Label(this.gTransfer, 16384);
/*  384 */     Messages.setLanguageText(label, "GeneralView.label.distributedCopies");
/*  385 */     this.distributedCopies = new BufferedLabel(this.gTransfer, 536887296);
/*  386 */     gridData = new GridData(768);
/*  387 */     Utils.setLayoutData(this.distributedCopies, gridData);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  392 */     this.gInfo = new Group(this.genComposite, 8);
/*  393 */     Messages.setLanguageText(this.gInfo, "GeneralView.section.info");
/*  394 */     gridData = new GridData(1808);
/*  395 */     Utils.setLayoutData(this.gInfo, gridData);
/*      */     
/*  397 */     GridLayout layoutInfo = new GridLayout();
/*  398 */     layoutInfo.numColumns = 4;
/*  399 */     this.gInfo.setLayout(layoutInfo);
/*      */     
/*  401 */     label = new Label(this.gInfo, 16384);
/*  402 */     Messages.setLanguageText(label, "GeneralView.label.filename");
/*  403 */     this.fileName = new BufferedLabel(this.gInfo, 16384);
/*  404 */     gridData = new GridData(768);
/*  405 */     Utils.setLayoutData(this.fileName, gridData);
/*      */     
/*  407 */     label = new Label(this.gInfo, 16384);
/*  408 */     Messages.setLanguageText(label, "GeneralView.label.status");
/*  409 */     this.torrentStatus = new BufferedLabel(this.gInfo, 16384);
/*  410 */     gridData = new GridData(768);
/*  411 */     Utils.setLayoutData(this.torrentStatus, gridData);
/*      */     
/*  413 */     label = new Label(this.gInfo, 16384);
/*  414 */     Messages.setLanguageText(label, "GeneralView.label.savein");
/*  415 */     this.saveIn = new BufferedLabel(this.gInfo, 16384);
/*  416 */     gridData = new GridData(768);
/*  417 */     gridData.horizontalSpan = 3;
/*  418 */     Utils.setLayoutData(this.saveIn, gridData);
/*      */     
/*  420 */     label = new Label(this.gInfo, 16384);
/*  421 */     Messages.setLanguageText(label, "GeneralView.label.totalsize");
/*  422 */     this.fileSize = new BufferedLabel(this.gInfo, 16384);
/*  423 */     gridData = new GridData(768);
/*  424 */     Utils.setLayoutData(this.fileSize, gridData);
/*      */     
/*  426 */     label = new Label(this.gInfo, 16384);
/*  427 */     Messages.setLanguageText(label, "GeneralView.label.numberofpieces");
/*  428 */     this.pieceNumber = new BufferedLabel(this.gInfo, 16384);
/*  429 */     gridData = new GridData(768);
/*  430 */     Utils.setLayoutData(this.pieceNumber, gridData);
/*      */     
/*  432 */     label = new Label(this.gInfo, 16384);
/*  433 */     Messages.setLanguageText(label, "GeneralView.label.hash");
/*  434 */     this.hash = new BufferedLabel(this.gInfo, 16384);
/*  435 */     Messages.setLanguageText(this.hash.getWidget(), "GeneralView.label.hash.tooltip", true);
/*      */     
/*  437 */     gridData = new GridData(768);
/*  438 */     Utils.setLayoutData(this.hash, gridData);
/*      */     
/*  440 */     this.hash.setCursor(this.display.getSystemCursor(21));
/*  441 */     this.hash.setForeground(Colors.blue);
/*  442 */     label.addMouseListener(new MouseAdapter() {
/*      */       public void mouseDoubleClick(MouseEvent arg0) {
/*  444 */         String hash_str = GeneralView.this.hash.getText();
/*  445 */         if ((hash_str != null) && (hash_str.length() != 0))
/*  446 */           new Clipboard(GeneralView.this.display).setContents(new Object[] { hash_str.replaceAll(" ", "") }, new Transfer[] { TextTransfer.getInstance() });
/*      */       }
/*      */       
/*  449 */       public void mouseDown(MouseEvent arg0) { String hash_str = GeneralView.this.hash.getText();
/*  450 */         if ((hash_str != null) && (hash_str.length() != 0))
/*  451 */           new Clipboard(GeneralView.this.display).setContents(new Object[] { hash_str.replaceAll(" ", "") }, new Transfer[] { TextTransfer.getInstance() });
/*      */       }
/*  453 */     });
/*  454 */     this.hash.addMouseListener(new MouseAdapter() {
/*      */       public void mouseDoubleClick(MouseEvent arg0) {
/*  456 */         String hash_str = GeneralView.this.hash.getText();
/*  457 */         if ((hash_str != null) && (hash_str.length() != 0))
/*  458 */           new Clipboard(GeneralView.this.display).setContents(new Object[] { hash_str.replaceAll(" ", "") }, new Transfer[] { TextTransfer.getInstance() });
/*      */       }
/*      */       
/*  461 */       public void mouseDown(MouseEvent arg0) { String hash_str = GeneralView.this.hash.getText();
/*  462 */         if ((hash_str != null) && (hash_str.length() != 0)) {
/*  463 */           new Clipboard(GeneralView.this.display).setContents(new Object[] { hash_str.replaceAll(" ", "") }, new Transfer[] { TextTransfer.getInstance() });
/*      */         }
/*      */         
/*      */       }
/*  467 */     });
/*  468 */     label = new Label(this.gInfo, 16384);
/*  469 */     Messages.setLanguageText(label, "GeneralView.label.size");
/*  470 */     this.pieceSize = new BufferedLabel(this.gInfo, 16384);
/*  471 */     gridData = new GridData(768);
/*  472 */     Utils.setLayoutData(this.pieceSize, gridData);
/*      */     
/*  474 */     label = new Label(this.gInfo, 16384);
/*  475 */     Messages.setLanguageText(label, "GeneralView.label.creationdate");
/*  476 */     this.creation_date = new BufferedLabel(this.gInfo, 16384);
/*  477 */     gridData = new GridData(768);
/*  478 */     Utils.setLayoutData(this.creation_date, gridData);
/*      */     
/*  480 */     label = new Label(this.gInfo, 16384);
/*  481 */     Messages.setLanguageText(label, "GeneralView.label.private");
/*  482 */     this.privateStatus = new BufferedLabel(this.gInfo, 16384);
/*  483 */     gridData = new GridData(768);
/*  484 */     Utils.setLayoutData(this.privateStatus, gridData);
/*      */     
/*      */ 
/*  487 */     label = new Label(this.gInfo, 16384);
/*  488 */     gridData = new GridData(768);
/*  489 */     gridData.horizontalSpan = 4;
/*  490 */     Utils.setLayoutData(label, gridData);
/*      */     
/*      */ 
/*  493 */     label = new Label(this.gInfo, 16384);
/*  494 */     label.setCursor(this.display.getSystemCursor(21));
/*  495 */     label.setForeground(Colors.blue);
/*  496 */     Messages.setLanguageText(label, "GeneralView.label.user_comment");
/*      */     try
/*      */     {
/*  499 */       this.user_comment = new Link(this.gInfo, 16448);
/*  500 */       ((Link)this.user_comment).addSelectionListener(new SelectionAdapter() {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  502 */           Utils.launch(e.text);
/*      */         }
/*      */       });
/*      */     } catch (Throwable e) {
/*  506 */       this.user_comment = new Label(this.gInfo, 16448);
/*      */     }
/*      */     
/*  509 */     gridData = new GridData(768);
/*  510 */     gridData.horizontalSpan = 3;
/*  511 */     Utils.setLayoutData(this.user_comment, gridData);
/*      */     
/*  513 */     label.addMouseListener(new MouseAdapter() {
/*      */       private void editComment() {
/*  515 */         TorrentUtil.promptUserForComment(new DownloadManager[] { GeneralView.this.manager });
/*      */       }
/*      */       
/*  518 */       public void mouseDoubleClick(MouseEvent arg0) { editComment(); }
/*  519 */       public void mouseDown(MouseEvent arg0) { editComment();
/*      */       }
/*  521 */     });
/*  522 */     label = new Label(this.gInfo, 16384);
/*  523 */     gridData = new GridData(2);
/*  524 */     Utils.setLayoutData(label, gridData);
/*  525 */     Messages.setLanguageText(label, "GeneralView.label.comment");
/*      */     try
/*      */     {
/*  528 */       this.lblComment = new Link(this.gInfo, 16448);
/*  529 */       ((Link)this.lblComment).addSelectionListener(new SelectionAdapter() {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  531 */           Utils.launch(e.text);
/*      */         }
/*      */       });
/*      */     } catch (Throwable e) {
/*  535 */       this.lblComment = new Label(this.gInfo, 16448);
/*      */     }
/*  537 */     gridData = new GridData(1808);
/*  538 */     gridData.horizontalSpan = 3;
/*  539 */     Utils.setLayoutData(this.lblComment, gridData);
/*      */     
/*      */ 
/*  542 */     this.piecesImage.addListener(9, new Listener() {
/*      */       public void handleEvent(Event e) {
/*  544 */         if ((GeneralView.this.pImage == null) || (GeneralView.this.pImage.isDisposed())) {
/*  545 */           return;
/*      */         }
/*  547 */         e.gc.drawImage(GeneralView.this.pImage, 0, 0);
/*      */       }
/*  549 */     });
/*  550 */     this.availabilityImage.addListener(9, new Listener() {
/*      */       public void handleEvent(Event e) {
/*  552 */         if ((GeneralView.this.aImage == null) || (GeneralView.this.aImage.isDisposed())) {
/*  553 */           return;
/*      */         }
/*  555 */         e.gc.drawImage(GeneralView.this.aImage, 0, 0);
/*      */       }
/*      */       
/*  558 */     });
/*  559 */     this.genComposite.layout();
/*      */     
/*  561 */     updateAvailability();
/*  562 */     updatePiecesInfo(true);
/*      */     
/*  564 */     Utils.updateScrolledComposite(this.scrolled_comp);
/*      */   }
/*      */   
/*      */   public Composite getComposite()
/*      */   {
/*  569 */     return this.genComposite;
/*      */   }
/*      */   
/*      */   public void refresh() {
/*  573 */     if ((this.gFile == null) || (this.gFile.isDisposed()) || (this.manager == null)) {
/*  574 */       return;
/*      */     }
/*  576 */     this.loopFactor += 1;
/*  577 */     if (this.loopFactor % this.graphicsUpdate == 0) {
/*  578 */       updateAvailability();
/*  579 */       this.availabilityImage.redraw();
/*  580 */       updatePiecesInfo(false);
/*  581 */       this.piecesImage.redraw();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  586 */     DiskManager dm = this.manager.getDiskManager();
/*      */     
/*      */ 
/*  589 */     String eta = DisplayFormatters.formatETA(this.manager.getStats().getSmoothedETA());
/*      */     String remaining;
/*  591 */     String remaining; if (dm != null)
/*      */     {
/*  593 */       long rem = dm.getRemainingExcludingDND();
/*      */       
/*  595 */       String data_rem = DisplayFormatters.formatByteCountToKiBEtc(rem);
/*      */       
/*      */       String remaining;
/*      */       
/*  599 */       if (rem > 0L)
/*      */       {
/*  601 */         remaining = eta + (eta.length() == 0 ? "" : " ") + data_rem;
/*      */       }
/*      */       else
/*      */       {
/*      */         String remaining;
/*      */         
/*  607 */         if (eta.length() == 0)
/*      */         {
/*  609 */           remaining = data_rem;
/*      */         }
/*      */         else {
/*  612 */           remaining = eta;
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  619 */       remaining = eta;
/*      */     }
/*      */     
/*      */ 
/*  623 */     setTime(this.manager.getStats().getElapsedTime(), remaining);
/*      */     
/*  625 */     TRTrackerScraperResponse hd = this.manager.getTrackerScrapeResponse();
/*  626 */     String seeds_str = this.manager.getNbSeeds() + " " + MessageText.getString("GeneralView.label.connected");
/*  627 */     String peers_str = this.manager.getNbPeers() + " " + MessageText.getString("GeneralView.label.connected");
/*      */     String completed;
/*  629 */     String completed; if ((hd != null) && (hd.isValid())) {
/*  630 */       seeds_str = seeds_str + " ( " + hd.getSeeds() + " " + MessageText.getString("GeneralView.label.in_swarm") + " )";
/*  631 */       peers_str = peers_str + " ( " + hd.getPeers() + " " + MessageText.getString("GeneralView.label.in_swarm") + " )";
/*  632 */       completed = hd.getCompleted() > -1 ? Integer.toString(hd.getCompleted()) : "?";
/*      */     }
/*      */     else {
/*  635 */       completed = "?";
/*      */     }
/*      */     
/*  638 */     String _shareRatio = "";
/*  639 */     int sr = this.manager.getStats().getShareRatio();
/*      */     
/*  641 */     if (sr == -1) _shareRatio = "∞";
/*  642 */     if (sr > 0) {
/*  643 */       String partial = "" + sr % 1000;
/*  644 */       while (partial.length() < 3) partial = "0" + partial;
/*  645 */       _shareRatio = sr / 1000 + "." + partial;
/*      */     }
/*      */     
/*      */ 
/*  649 */     DownloadManagerStats stats = this.manager.getStats();
/*      */     
/*  651 */     String swarm_speed = DisplayFormatters.formatByteCountToKiBEtcPerSec(stats.getTotalAverage()) + " ( " + DisplayFormatters.formatByteCountToKiBEtcPerSec(stats.getTotalAveragePerPeer()) + " " + MessageText.getString("GeneralView.label.averagespeed") + " )";
/*      */     
/*  653 */     String swarm_completion = "";
/*  654 */     String distributedCopies = "0.000";
/*  655 */     String piecesDoneAndSum = "" + this.manager.getNbPieces();
/*      */     
/*  657 */     PEPeerManager pm = this.manager.getPeerManager();
/*  658 */     if (pm != null) {
/*  659 */       int comp = pm.getAverageCompletionInThousandNotation();
/*  660 */       if (comp >= 0) {
/*  661 */         swarm_completion = DisplayFormatters.formatPercentFromThousands(comp);
/*      */       }
/*      */       
/*  664 */       piecesDoneAndSum = pm.getPiecePicker().getNbPiecesDone() + "/" + piecesDoneAndSum;
/*      */       
/*  666 */       distributedCopies = new DecimalFormat("0.000").format(pm.getPiecePicker().getMinAvailability() - pm.getNbSeeds() - ((pm.isSeeding()) && (stats.getDownloadCompleted(false) == 1000) ? 1 : 0));
/*      */     }
/*      */     
/*      */ 
/*  670 */     int kInB = DisplayFormatters.getKinB();
/*      */     
/*  672 */     setStats(DisplayFormatters.formatDownloaded(stats), DisplayFormatters.formatByteCountToKiBEtc(stats.getTotalDataBytesSent()), DisplayFormatters.formatByteCountToKiBEtcPerSec(stats.getDataReceiveRate()), DisplayFormatters.formatByteCountToKiBEtcPerSec(stats.getDataSendRate()), swarm_speed, "" + this.manager.getStats().getDownloadRateLimitBytesPerSecond() / kInB, "" + this.manager.getStats().getUploadRateLimitBytesPerSecond() / kInB, seeds_str, peers_str, completed, DisplayFormatters.formatHashFails(this.manager), _shareRatio, swarm_completion, distributedCopies);
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
/*  689 */     TOTorrent torrent = this.manager.getTorrent();
/*      */     
/*  691 */     String creation_date = DisplayFormatters.formatDate(this.manager.getTorrentCreationDate() * 1000L);
/*  692 */     byte[] created_by = torrent == null ? null : torrent.getCreatedBy();
/*  693 */     if (created_by != null) {
/*      */       try {
/*  695 */         creation_date = MessageText.getString("GeneralView.torrent_created_on_and_by", new String[] { creation_date, new String(created_by, "UTF8") });
/*      */       }
/*      */       catch (UnsupportedEncodingException e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  702 */     setInfos(this.manager.getDisplayName(), DisplayFormatters.formatByteCountToKiBEtc(this.manager.getSize()), DisplayFormatters.formatDownloadStatus(this.manager), this.manager.getState() == 100, this.manager.getSaveLocation().toString(), TorrentUtils.nicePrintTorrentHash(torrent), piecesDoneAndSum, this.manager.getPieceLength(), this.manager.getTorrentComment(), creation_date, this.manager.getDownloadState().getUserComment(), MessageText.getString("GeneralView." + ((torrent != null) && (torrent.getPrivate()) ? "yes" : "no")));
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
/*  719 */     if (this.loopFactor == 2) {
/*  720 */       getComposite().layout(true);
/*      */     }
/*      */   }
/*      */   
/*      */   public void delete() {
/*  725 */     if (this.aImage != null)
/*  726 */       this.aImage.dispose();
/*  727 */     this.aImage = null;
/*  728 */     if (this.pImage != null)
/*  729 */       this.pImage.dispose();
/*  730 */     this.pImage = null;
/*  731 */     Utils.disposeComposite(this.genComposite);
/*  732 */     COConfigurationManager.removeParameterListener("Graphics Update", this);
/*      */   }
/*      */   
/*      */   private String getFullTitle() {
/*  736 */     return MessageText.getString("GeneralView.title.full");
/*      */   }
/*      */   
/*      */   private void updateAvailability() {
/*  740 */     if (this.manager == null) {
/*  741 */       return;
/*      */     }
/*      */     try {
/*  744 */       this.this_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*  748 */       PEPeerManager pm = this.manager.getPeerManager();
/*      */       int[] available;
/*  750 */       int[] available; if (this.manager.getPeerManager() == null) {
/*  751 */         if (this.availabilityPercent.getText().length() > 0)
/*      */         {
/*  753 */           this.availabilityPercent.setText("");
/*      */         }
/*      */         
/*  756 */         available = new int[this.manager.getNbPieces()];
/*      */       } else {
/*  758 */         available = pm.getAvailability();
/*      */       }
/*      */       
/*  761 */       if ((this.display == null) || (this.display.isDisposed())) {
/*      */         return;
/*      */       }
/*  764 */       if ((this.availabilityImage == null) || (this.availabilityImage.isDisposed())) {
/*      */         return;
/*      */       }
/*  767 */       Rectangle bounds = this.availabilityImage.getClientArea();
/*      */       
/*  769 */       int xMax = bounds.width - 2;
/*      */       
/*  771 */       int yMax = bounds.height - 2;
/*      */       
/*  773 */       if ((xMax < 10) || (yMax < 5)) {
/*      */         return;
/*      */       }
/*      */       
/*  777 */       if ((this.aImage != null) && (!this.aImage.isDisposed())) {
/*  778 */         this.aImage.dispose();
/*      */       }
/*  780 */       this.aImage = new Image(this.display, bounds.width, bounds.height);
/*      */       
/*  782 */       GC gcImage = new GC(this.aImage);
/*      */       try
/*      */       {
/*  785 */         gcImage.setForeground(Colors.grey);
/*  786 */         gcImage.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
/*  787 */         int allMin = 0;
/*  788 */         int allMax = 0;
/*  789 */         int total = 0;
/*  790 */         String sTotal = "000";
/*  791 */         if (available != null)
/*      */         {
/*  793 */           allMin = available.length == 0 ? 0 : available[0];
/*  794 */           allMax = available.length == 0 ? 0 : available[0];
/*  795 */           int nbPieces = available.length;
/*  796 */           for (int i = 0; i < nbPieces; i++) {
/*  797 */             if (available[i] < allMin)
/*  798 */               allMin = available[i];
/*  799 */             if (available[i] > allMax)
/*  800 */               allMax = available[i];
/*      */           }
/*  802 */           int maxAboveMin = allMax - allMin;
/*  803 */           if (maxAboveMin == 0)
/*      */           {
/*  805 */             gcImage.setBackground(Colors.blues[9]);
/*  806 */             gcImage.fillRectangle(1, 1, xMax, yMax);
/*      */           } else {
/*  808 */             for (int i = 0; i < nbPieces; i++) {
/*  809 */               if (available[i] > allMin)
/*  810 */                 total++;
/*      */             }
/*  812 */             total = total * 1000 / nbPieces;
/*  813 */             sTotal = "" + total;
/*  814 */             if (total < 10) sTotal = "0" + sTotal;
/*  815 */             if (total < 100) { sTotal = "0" + sTotal;
/*      */             }
/*  817 */             for (int i = 0; i < xMax; i++) {
/*  818 */               int a0 = i * nbPieces / xMax;
/*  819 */               int a1 = (i + 1) * nbPieces / xMax;
/*  820 */               if (a1 == a0)
/*  821 */                 a1++;
/*  822 */               if (a1 > nbPieces)
/*  823 */                 a1 = nbPieces;
/*  824 */               int max = 0;
/*  825 */               int min = available[a0];
/*  826 */               int Pi = 1000;
/*  827 */               for (int j = a0; j < a1; j++) {
/*  828 */                 if (available[j] > max)
/*  829 */                   max = available[j];
/*  830 */                 if (available[j] < min)
/*  831 */                   min = available[j];
/*  832 */                 Pi *= available[j];
/*  833 */                 Pi /= (available[j] + 1);
/*      */               }
/*  835 */               int pond = Pi;
/*  836 */               if (max == 0) {
/*  837 */                 pond = 0;
/*      */               } else {
/*  839 */                 int PiM = 1000;
/*  840 */                 for (int j = a0; j < a1; j++) {
/*  841 */                   PiM *= (max + 1);
/*  842 */                   PiM /= max;
/*      */                 }
/*  844 */                 pond *= PiM;
/*  845 */                 pond /= 1000;
/*  846 */                 pond *= (max - min);
/*  847 */                 pond /= 1000;
/*  848 */                 pond += min; }
/*      */               int index;
/*      */               int index;
/*  851 */               if ((pond <= 0) || (allMax == 0)) {
/*  852 */                 index = 0;
/*      */               }
/*      */               else {
/*  855 */                 index = (pond - allMin) * 8 / maxAboveMin + 1;
/*      */                 
/*  857 */                 if (index > 9) {
/*  858 */                   index = 9;
/*      */                 }
/*      */               }
/*      */               
/*  862 */               gcImage.setBackground(Colors.blues[index]);
/*  863 */               gcImage.fillRectangle(i + 1, 1, 1, yMax);
/*      */             }
/*      */           }
/*      */         }
/*  867 */         if ((this.availabilityPercent == null) || (this.availabilityPercent.isDisposed()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  873 */           gcImage.dispose();
/*      */         } else {
/*  870 */           this.availabilityPercent.setText(allMin + "." + sTotal);
/*      */         }
/*      */       } finally {
/*  873 */         gcImage.dispose();
/*      */       }
/*      */     }
/*      */     finally {
/*  877 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private void updatePiecesInfo(boolean bForce) {
/*  882 */     if (this.manager == null) {
/*  883 */       return;
/*      */     }
/*      */     try {
/*  886 */       this.this_mon.enter();
/*      */       
/*  888 */       if ((this.display == null) || (this.display.isDisposed())) {
/*      */         return;
/*      */       }
/*  891 */       if ((this.piecesImage == null) || (this.piecesImage.isDisposed())) {
/*      */         return;
/*      */       }
/*  894 */       if (this.piecesImageRefreshNeeded) {
/*  895 */         bForce = true;
/*  896 */         this.piecesImageRefreshNeeded = false;
/*      */       }
/*      */       
/*  899 */       DiskManager dm = this.manager.getDiskManager();
/*      */       
/*  901 */       int nbPieces = this.manager.getNbPieces();
/*      */       
/*      */ 
/*      */ 
/*  905 */       int[] oldPiecesState = this.piecesStateCache;
/*      */       boolean valid;
/*  907 */       boolean valid; if ((oldPiecesState == null) || (oldPiecesState.length != nbPieces))
/*      */       {
/*  909 */         valid = false;
/*      */       }
/*      */       else
/*      */       {
/*  913 */         valid = !bForce;
/*      */       }
/*      */       
/*      */ 
/*  917 */       int[] newPiecesState = new int[nbPieces];
/*      */       
/*  919 */       int PS_NONE = 0;
/*  920 */       int PS_DONE = 1;
/*  921 */       int PS_SKIPPED = 2;
/*  922 */       int PS_FILE_BOUNDARY = 4;
/*      */       
/*  924 */       if (dm != null)
/*      */       {
/*  926 */         DiskManagerPiece[] dm_pieces = dm.getPieces();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  934 */         int dm_state = dm.getState();
/*      */         boolean update_skipped;
/*  936 */         boolean update_skipped; boolean update_boundaries; if ((dm_state == 3) || (dm_state == 4)) {
/*      */           boolean update_boundaries;
/*  938 */           if (!valid) {
/*  939 */             boolean update_skipped = true;
/*  940 */             update_boundaries = true; } else { boolean update_boundaries;
/*      */             boolean update_boundaries;
/*  942 */             if (this.piecesStateFileBoundariesDone) {
/*  943 */               update_boundaries = false;
/*      */             } else {
/*  945 */               this.piecesStateFileBoundariesDone = true;
/*  946 */               update_boundaries = true;
/*      */             }
/*  948 */             long marker = dm.getPriorityChangeMarker();
/*  949 */             boolean update_skipped; if (marker == this.piecesStateSkippedMarker) {
/*  950 */               update_skipped = false;
/*      */             } else {
/*  952 */               this.piecesStateSkippedMarker = marker;
/*  953 */               update_skipped = true;
/*      */             }
/*      */           }
/*      */         } else {
/*  957 */           update_skipped = false;
/*  958 */           update_boundaries = false;
/*      */         }
/*      */         
/*  961 */         for (int i = 0; i < nbPieces; i++)
/*      */         {
/*  963 */           DiskManagerPiece piece = dm_pieces[i];
/*      */           
/*  965 */           int state = piece.isDone() ? 1 : 0;
/*      */           
/*  967 */           if (update_skipped) {
/*  968 */             if (piece.isSkipped()) {
/*  969 */               state |= 0x2;
/*      */             }
/*      */           } else {
/*  972 */             state |= oldPiecesState[i] & 0x2;
/*      */           }
/*      */           
/*  975 */           if (update_boundaries)
/*      */           {
/*  977 */             if (piece.spansFiles()) {
/*  978 */               state |= 0x4;
/*      */             }
/*      */           } else {
/*  981 */             state |= oldPiecesState[i] & 0x4;
/*      */           }
/*      */           
/*  984 */           newPiecesState[i] = state;
/*      */           
/*  986 */           if (valid)
/*      */           {
/*  988 */             if (oldPiecesState[i] != state)
/*      */             {
/*  990 */               valid = false;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  996 */       this.piecesStateCache = newPiecesState;
/*      */       
/*  998 */       if (!valid) {
/*  999 */         Rectangle bounds = this.piecesImage.getClientArea();
/* 1000 */         int xMax = bounds.width - 2;
/* 1001 */         int yMax = bounds.height - 2 - 6;
/* 1002 */         if ((xMax < 10) || (yMax < 5)) {
/*      */           return;
/*      */         }
/*      */         
/* 1006 */         int total = this.manager.getStats().getDownloadCompleted(true);
/*      */         
/* 1008 */         if ((this.pImage != null) && (!this.pImage.isDisposed())) {
/* 1009 */           this.pImage.dispose();
/*      */         }
/*      */         
/* 1012 */         this.pImage = new Image(this.display, bounds.width, bounds.height);
/*      */         
/* 1014 */         GC gcImage = new GC(this.pImage);
/*      */         try {
/* 1016 */           gcImage.setForeground(Colors.grey);
/* 1017 */           gcImage.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
/* 1018 */           gcImage.drawLine(1, 6, xMax, 6);
/*      */           
/* 1020 */           if ((newPiecesState != null) && (newPiecesState.length != 0))
/*      */           {
/* 1022 */             int[] boundariesHandled = new int[newPiecesState.length];
/*      */             
/* 1024 */             for (int i = 0; i < xMax; i++) {
/* 1025 */               int a0 = i * nbPieces / xMax;
/* 1026 */               int a1 = (i + 1) * nbPieces / xMax;
/* 1027 */               if (a1 == a0)
/* 1028 */                 a1++;
/* 1029 */               if (a1 > nbPieces)
/* 1030 */                 a1 = nbPieces;
/* 1031 */               int nbAvailable = 0;
/* 1032 */               int nbSkipped = 0;
/* 1033 */               boolean hasFileBoundary = false;
/*      */               
/* 1035 */               for (int j = a0; j < a1; j++) {
/* 1036 */                 int ps = newPiecesState[j];
/* 1037 */                 if ((ps & 0x1) != 0) {
/* 1038 */                   nbAvailable++;
/*      */                 }
/* 1040 */                 if ((ps & 0x2) != 0) {
/* 1041 */                   nbSkipped++;
/*      */                 }
/* 1043 */                 if (((ps & 0x4) != 0) && 
/* 1044 */                   (boundariesHandled[j] < 2)) {
/* 1045 */                   boundariesHandled[j] += 1;
/*      */                   
/* 1047 */                   hasFileBoundary = true;
/*      */                 }
/*      */               }
/*      */               
/* 1051 */               if ((nbAvailable == 0) && (nbSkipped > 0)) {
/* 1052 */                 gcImage.setBackground(Colors.grey);
/* 1053 */                 gcImage.fillRectangle(i + 1, 7, 1, yMax);
/*      */               } else {
/* 1055 */                 int index = nbAvailable * 9 / (a1 - a0);
/* 1056 */                 gcImage.setBackground(Colors.blues[index]);
/* 1057 */                 gcImage.fillRectangle(i + 1, 7, 1, yMax);
/*      */               }
/*      */               
/* 1060 */               if (hasFileBoundary) {
/* 1061 */                 gcImage.setBackground(Colors.green);
/* 1062 */                 gcImage.fillRectangle(i + 1, 7 + yMax - 6, 1, 6);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1068 */           int limit = xMax * total / 1000;
/* 1069 */           gcImage.setBackground(Colors.colorProgressBar);
/* 1070 */           gcImage.fillRectangle(1, 1, limit, 5);
/* 1071 */           if (limit < xMax) {
/* 1072 */             gcImage.setBackground(Colors.blues[0]);
/* 1073 */             gcImage.fillRectangle(limit + 1, 1, xMax - limit, 5);
/*      */           }
/*      */         }
/*      */         finally {
/* 1077 */           gcImage.dispose();
/*      */         }
/*      */         
/* 1080 */         if ((this.piecesPercent != null) && (!this.piecesPercent.isDisposed())) {
/* 1081 */           this.piecesPercent.setText(DisplayFormatters.formatPercentFromThousands(total));
/*      */         }
/* 1083 */         if ((this.pImage == null) || (this.pImage.isDisposed())) {
/*      */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1089 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private void setTime(String elapsed, String remaining) {
/* 1094 */     this.timeElapsed.setText(elapsed);
/* 1095 */     this.timeRemaining.setText(remaining);
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
/*      */   private void setStats(String dl, String ul, String dls, String uls, String ts, String dl_speed, String ul_speed, String s, String p, String completed, String hash_fails, String share_ratio, String ave_comp, String distr_copies)
/*      */   {
/* 1113 */     if ((this.display == null) || (this.display.isDisposed())) {
/* 1114 */       return;
/*      */     }
/* 1116 */     this.download.setText(dl);
/* 1117 */     this.downloadSpeed.setText(dls);
/* 1118 */     this.upload.setText(ul);
/* 1119 */     this.uploadSpeed.setText(uls);
/* 1120 */     this.totalSpeed.setText(ts);
/* 1121 */     this.ave_completion.setText(ave_comp);
/* 1122 */     this.distributedCopies.setText(distr_copies);
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
/* 1136 */     this.seeds.setText(s);
/* 1137 */     this.peers.setText(p);
/* 1138 */     this.completedLbl.setText(completed);
/* 1139 */     this.hashFails.setText(hash_fails);
/* 1140 */     this.shareRatio.setText(share_ratio);
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
/*      */   private void setInfos(final String _fileName, final String _fileSize, final String _torrentStatus, final boolean _statusIsError, final String _path, final String _hash, final String _pieceData, final String _pieceLength, final String _comment, final String _creation_date, final String _user_comment, final String isPrivate)
/*      */   {
/* 1157 */     if ((this.display == null) || (this.display.isDisposed()))
/* 1158 */       return;
/* 1159 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 1162 */         GeneralView.this.fileName.setText(_fileName);
/* 1163 */         GeneralView.this.fileSize.setText(_fileSize);
/* 1164 */         GeneralView.this.torrentStatus.setText(_torrentStatus);
/* 1165 */         int pos = _torrentStatus.indexOf("http://");
/* 1166 */         if (pos > 0) {
/* 1167 */           GeneralView.this.torrentStatus.setLink(UrlUtils.getURL(_torrentStatus));
/*      */         } else {
/* 1169 */           GeneralView.this.torrentStatus.setLink(null);
/*      */         }
/* 1171 */         GeneralView.this.torrentStatus.setForeground(_statusIsError ? Colors.red : null);
/* 1172 */         GeneralView.this.saveIn.setText(_path);
/* 1173 */         GeneralView.this.hash.setText(_hash);
/* 1174 */         GeneralView.this.pieceNumber.setText(_pieceData);
/* 1175 */         GeneralView.this.pieceSize.setText(_pieceLength);
/* 1176 */         GeneralView.this.creation_date.setText(_creation_date);
/* 1177 */         GeneralView.this.privateStatus.setText(isPrivate);
/* 1178 */         boolean do_relayout = false;
/* 1179 */         do_relayout = GeneralView.setCommentAndFormatLinks(GeneralView.this.lblComment, (_comment.length() > 5000) && (Constants.isWindowsXP) ? _comment.substring(0, 5000) : _comment) | do_relayout;
/* 1180 */         do_relayout = GeneralView.setCommentAndFormatLinks(GeneralView.this.user_comment, _user_comment) | do_relayout;
/* 1181 */         if (do_relayout)
/*      */         {
/* 1183 */           GeneralView.this.gInfo.layout();
/* 1184 */           Utils.updateScrolledComposite(GeneralView.this.scrolled_comp);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private static boolean setCommentAndFormatLinks(Control c, String new_comment) {
/* 1191 */     String old_comment = (String)c.getData("comment");
/* 1192 */     if (new_comment == null) new_comment = "";
/* 1193 */     if (new_comment.equals(old_comment)) { return false;
/*      */     }
/* 1195 */     c.setData("comment", new_comment);
/* 1196 */     if ((c instanceof Label)) {
/* 1197 */       ((Label)c).setText(new_comment);
/* 1198 */     } else if ((c instanceof Link))
/*      */     {
/* 1200 */       String sNewComment = new_comment.replaceAll("([^=\">][\\s]+|^)((?:https?://|chat:)[\\S]+)", "$1<A HREF=\"$2\">$2</A>");
/*      */       
/*      */ 
/* 1203 */       sNewComment = sNewComment.replaceAll("(href=)(htt[^\\s>]+)", "$1\"$2\"");
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 1208 */         Pattern p = Pattern.compile("(?i)(<A HREF=[^>]*>)([^<]*</A>)");
/*      */         
/* 1210 */         Matcher m = p.matcher(sNewComment);
/*      */         
/* 1212 */         boolean result = m.find();
/*      */         
/* 1214 */         if (result)
/*      */         {
/* 1216 */           StringBuffer sb = new StringBuffer();
/*      */           
/* 1218 */           while (result)
/*      */           {
/* 1220 */             m.appendReplacement(sb, m.group(1));
/*      */             
/* 1222 */             String str = m.group(2);
/*      */             
/* 1224 */             sb.append(UrlUtils.decode(str));
/*      */             
/* 1226 */             result = m.find();
/*      */           }
/*      */           
/* 1229 */           m.appendTail(sb);
/*      */           
/* 1231 */           sNewComment = sb.toString();
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1241 */       ((Link)c).setText(sNewComment);
/*      */     }
/*      */     
/* 1244 */     return true;
/*      */   }
/*      */   
/*      */   public void parameterChanged(String parameterName) {
/* 1248 */     this.graphicsUpdate = COConfigurationManager.getIntParameter("Graphics Update");
/*      */   }
/*      */   
/*      */   private Image obfusticatedImage(Image image) {
/* 1252 */     if (this.fileName == null) {
/* 1253 */       return image;
/*      */     }
/* 1255 */     UIDebugGenerator.obfusticateArea(image, (Control)this.fileName.getWidget(), this.manager == null ? "" : this.manager.toString());
/*      */     
/* 1257 */     UIDebugGenerator.obfusticateArea(image, (Control)this.saveIn.getWidget(), Debug.secretFileName(this.saveIn.getText()));
/*      */     
/* 1259 */     return image;
/*      */   }
/*      */   
/*      */   public boolean eventOccurred(UISWTViewEvent event) {
/* 1263 */     switch (event.getType()) {
/*      */     case 0: 
/* 1265 */       this.swtView = event.getView();
/* 1266 */       this.swtView.setTitle(getFullTitle());
/* 1267 */       this.swtView.setToolBarListener(this);
/* 1268 */       break;
/*      */     
/*      */     case 7: 
/* 1271 */       delete();
/* 1272 */       break;
/*      */     
/*      */     case 2: 
/* 1275 */       initialize((Composite)event.getData());
/* 1276 */       break;
/*      */     
/*      */     case 6: 
/* 1279 */       Messages.updateLanguageForControl(getComposite());
/* 1280 */       this.swtView.setTitle(getFullTitle());
/* 1281 */       break;
/*      */     
/*      */     case 1: 
/* 1284 */       dataSourceChanged(event.getData());
/* 1285 */       break;
/*      */     
/*      */     case 3: 
/* 1288 */       String id = "DMDetails_General";
/* 1289 */       if (this.manager != null) {
/* 1290 */         if (this.manager.getTorrent() != null) {
/* 1291 */           id = id + "." + this.manager.getInternalName();
/*      */         } else {
/* 1293 */           id = id + ":" + this.manager.getSize();
/*      */         }
/* 1295 */         SelectedContentManager.changeCurrentlySelectedContent(id, new SelectedContent[] { new SelectedContent(this.manager) });
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1300 */         SelectedContentManager.changeCurrentlySelectedContent(id, null);
/*      */       }
/*      */       
/* 1303 */       break;
/*      */     
/*      */     case 4: 
/* 1306 */       SelectedContentManager.clearCurrentlySelectedContent();
/* 1307 */       break;
/*      */     
/*      */     case 5: 
/* 1310 */       refresh();
/* 1311 */       break;
/*      */     
/*      */     case 9: 
/* 1314 */       Object data = event.getData();
/* 1315 */       if ((data instanceof Map)) {
/* 1316 */         obfusticatedImage((Image)MapUtils.getMapObject((Map)data, "image", null, Image.class));
/*      */       }
/*      */       
/*      */       break;
/*      */     }
/*      */     
/* 1322 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*      */   {
/* 1330 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void refreshToolBarItems(Map<String, Long> list)
/*      */   {
/* 1337 */     Map<String, Long> states = TorrentUtil.calculateToolbarStates(SelectedContentManager.getCurrentlySelectedContent(), null);
/*      */     
/* 1339 */     list.putAll(states);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/GeneralView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */