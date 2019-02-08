/*     */ package org.gudy.azureus2.ui.swt.views.piece;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.custom.ScrolledComposite;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseTrackAdapter;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.ScrollBar;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerImpl;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerPieceListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuBuilder;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.Legend;
/*     */ import org.gudy.azureus2.ui.swt.debug.UIDebugGenerator;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
/*     */ import org.gudy.azureus2.ui.swt.views.ViewUtils;
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
/*     */ public class PieceInfoView
/*     */   implements DownloadManagerPieceListener, UISWTViewCoreEventListenerEx
/*     */ {
/*     */   private static final int BLOCK_FILLSIZE = 14;
/*     */   private static final int BLOCK_SPACING = 3;
/*     */   private static final int BLOCK_SIZE = 17;
/*     */   private static final int BLOCKCOLOR_HAVE = 0;
/*     */   private static final int BLOCKCOLORL_NOHAVE = 1;
/*     */   private static final int BLOCKCOLOR_TRANSFER = 2;
/*     */   private static final int BLOCKCOLOR_NEXT = 3;
/*     */   private static final int BLOCKCOLOR_AVAILCOUNT = 4;
/*     */   public static final String MSGID_PREFIX = "PieceInfoView";
/*     */   private Composite pieceInfoComposite;
/*     */   private ScrolledComposite sc;
/*     */   protected Canvas pieceInfoCanvas;
/*     */   private Color[] blockColors;
/*     */   private Label topLabel;
/* 111 */   private String topLabelLHS = "";
/* 112 */   private String topLabelRHS = "";
/*     */   
/*     */ 
/*     */   private Label imageLabel;
/*     */   
/* 117 */   private int graphicsUpdate = COConfigurationManager.getIntParameter("Graphics Update") * 2;
/*     */   
/* 119 */   private int loopFactor = 0;
/*     */   
/* 121 */   private Font font = null;
/*     */   
/* 123 */   Image img = null;
/*     */   
/*     */ 
/*     */   private DownloadManager dlm;
/*     */   
/*     */ 
/*     */   BlockInfo[] oldBlockInfo;
/*     */   
/*     */ 
/*     */   public PieceInfoView()
/*     */   {
/* 134 */     this.blockColors = new Color[] { Colors.blues[9], Colors.white, Colors.red, Colors.fadedRed, Colors.black };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isCloneable()
/*     */   {
/* 146 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public UISWTViewCoreEventListener getClone()
/*     */   {
/* 152 */     return new PieceInfoView();
/*     */   }
/*     */   
/*     */ 
/*     */   private void dataSourceChanged(Object newDataSource)
/*     */   {
/* 158 */     DownloadManager newManager = ViewUtils.getDownloadManagerFromDataSource(newDataSource);
/*     */     
/* 160 */     if (newManager != null)
/*     */     {
/* 162 */       this.oldBlockInfo = null;
/*     */     }
/* 164 */     else if ((newDataSource instanceof Object[])) {
/* 165 */       Object[] objects = (Object[])newDataSource;
/* 166 */       if ((objects.length > 0) && ((objects[0] instanceof PEPiece))) {
/* 167 */         PEPiece piece = (PEPiece)objects[0];
/* 168 */         DiskManager diskManager = piece.getDMPiece().getManager();
/* 169 */         if ((diskManager instanceof DiskManagerImpl)) {
/* 170 */           DiskManagerImpl dmi = (DiskManagerImpl)diskManager;
/* 171 */           newManager = dmi.getDownloadManager();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 176 */     synchronized (this) {
/* 177 */       if (this.dlm != null) {
/* 178 */         this.dlm.removePieceListener(this);
/*     */       }
/* 180 */       this.dlm = newManager;
/* 181 */       if (this.dlm != null) {
/* 182 */         this.dlm.addPieceListener(this, false);
/*     */       }
/*     */     }
/*     */     
/* 186 */     if (newManager != null) {
/* 187 */       fillPieceInfoSection();
/*     */     }
/*     */   }
/*     */   
/*     */   private String getFullTitle() {
/* 192 */     return MessageText.getString("PeersView.BlockView.title");
/*     */   }
/*     */   
/*     */   private void initialize(Composite composite) {
/* 196 */     if ((this.pieceInfoComposite != null) && (!this.pieceInfoComposite.isDisposed())) {
/* 197 */       Logger.log(new LogEvent(LogIDs.GUI, 3, "PeerInfoView already initialized! Stack: " + Debug.getStackTrace(true, false)));
/*     */       
/*     */ 
/* 200 */       delete();
/*     */     }
/* 202 */     createPeerInfoPanel(composite);
/*     */     
/* 204 */     fillPieceInfoSection();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Composite createPeerInfoPanel(Composite parent)
/*     */   {
/* 214 */     this.pieceInfoComposite = new Composite(parent, 0);
/* 215 */     GridLayout layout = new GridLayout();
/* 216 */     layout.numColumns = 2;
/* 217 */     layout.horizontalSpacing = 0;
/* 218 */     layout.verticalSpacing = 0;
/* 219 */     layout.marginHeight = 0;
/* 220 */     layout.marginWidth = 0;
/* 221 */     this.pieceInfoComposite.setLayout(layout);
/* 222 */     GridData gridData = new GridData(4, 4, true, true);
/* 223 */     this.pieceInfoComposite.setLayoutData(gridData);
/*     */     
/* 225 */     this.imageLabel = new Label(this.pieceInfoComposite, 0);
/* 226 */     gridData = new GridData();
/* 227 */     this.imageLabel.setLayoutData(gridData);
/*     */     
/* 229 */     this.topLabel = new Label(this.pieceInfoComposite, 0);
/* 230 */     gridData = new GridData(4, -1, false, false);
/* 231 */     this.topLabel.setLayoutData(gridData);
/*     */     
/* 233 */     this.sc = new ScrolledComposite(this.pieceInfoComposite, 512);
/* 234 */     this.sc.setExpandHorizontal(true);
/* 235 */     this.sc.setExpandVertical(true);
/* 236 */     layout = new GridLayout();
/* 237 */     layout.horizontalSpacing = 0;
/* 238 */     layout.verticalSpacing = 0;
/* 239 */     layout.marginHeight = 0;
/* 240 */     layout.marginWidth = 0;
/* 241 */     this.sc.setLayout(layout);
/* 242 */     gridData = new GridData(4, 4, true, true, 2, 1);
/* 243 */     this.sc.setLayoutData(gridData);
/* 244 */     this.sc.getVerticalBar().setIncrement(17);
/*     */     
/* 246 */     this.pieceInfoCanvas = new Canvas(this.sc, 1310720);
/* 247 */     gridData = new GridData(4, -1, true, false);
/* 248 */     this.pieceInfoCanvas.setLayoutData(gridData);
/* 249 */     this.pieceInfoCanvas.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 251 */         if ((e.width <= 0) || (e.height <= 0))
/* 252 */           return;
/*     */         try {
/* 254 */           Rectangle bounds = PieceInfoView.this.img == null ? null : PieceInfoView.this.img.getBounds();
/* 255 */           if ((bounds == null) || (PieceInfoView.this.dlm == null) || (PieceInfoView.this.dlm.getPeerManager() == null)) {
/* 256 */             e.gc.fillRectangle(e.x, e.y, e.width, e.height);
/*     */           } else {
/* 258 */             if (e.x + e.width > bounds.width) {
/* 259 */               e.gc.fillRectangle(bounds.width, e.y, e.x + e.width - bounds.width + 1, e.height);
/*     */             }
/* 261 */             if (e.y + e.height > bounds.height) {
/* 262 */               e.gc.fillRectangle(e.x, bounds.height, e.width, e.y + e.height - bounds.height + 1);
/*     */             }
/*     */             
/* 265 */             int width = Math.min(e.width, bounds.width - e.x);
/* 266 */             int height = Math.min(e.height, bounds.height - e.y);
/* 267 */             e.gc.drawImage(PieceInfoView.this.img, e.x, e.y, width, height, e.x, e.y, width, height);
/*     */           }
/*     */           
/*     */         }
/*     */         catch (Exception ex) {}
/*     */       }
/* 273 */     });
/* 274 */     Listener doNothingListener = new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {}
/* 277 */     };
/* 278 */     this.pieceInfoCanvas.addListener(1, doNothingListener);
/*     */     
/* 280 */     this.pieceInfoCanvas.addListener(11, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 282 */         synchronized (PieceInfoView.this) {
/* 283 */           if (PieceInfoView.this.alreadyFilling) {
/* 284 */             return;
/*     */           }
/*     */           
/* 287 */           PieceInfoView.this.alreadyFilling = true;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 292 */         Utils.execSWTThreadLater(0, new AERunnable() {
/*     */           public void runSupport() {
/* 294 */             if (PieceInfoView.this.img != null) {
/* 295 */               int iOldColCount = PieceInfoView.this.img.getBounds().width / 17;
/* 296 */               int iNewColCount = PieceInfoView.this.pieceInfoCanvas.getClientArea().width / 17;
/*     */               
/* 298 */               if (iOldColCount != iNewColCount)
/* 299 */                 PieceInfoView.this.refreshInfoCanvas();
/*     */             }
/* 301 */             synchronized (PieceInfoView.this) {
/* 302 */               PieceInfoView.this.alreadyFilling = false;
/*     */             }
/*     */             
/*     */           }
/*     */         });
/*     */       }
/* 308 */     });
/* 309 */     this.sc.setContent(this.pieceInfoCanvas);
/*     */     
/* 311 */     this.pieceInfoCanvas.addMouseTrackListener(new MouseTrackAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void mouseHover(MouseEvent event)
/*     */       {
/*     */ 
/* 318 */         int piece_number = PieceInfoView.this.getPieceNumber(event.x, event.y);
/*     */         
/* 320 */         if (piece_number >= 0)
/*     */         {
/* 322 */           DiskManager disk_manager = PieceInfoView.this.dlm.getDiskManager();
/* 323 */           PEPeerManager pm = PieceInfoView.this.dlm.getPeerManager();
/*     */           
/* 325 */           DiskManagerPiece dm_piece = disk_manager.getPiece(piece_number);
/* 326 */           PEPiece pm_piece = pm.getPiece(piece_number);
/*     */           
/* 328 */           String text = "Piece " + piece_number + ": " + dm_piece.getString();
/*     */           
/* 330 */           if (pm_piece != null)
/*     */           {
/* 332 */             text = text + ", active: " + pm_piece.getString();
/*     */ 
/*     */ 
/*     */           }
/* 336 */           else if ((dm_piece.isNeeded()) && (!dm_piece.isDone()))
/*     */           {
/* 338 */             text = text + ", inactive: " + pm.getPiecePicker().getPieceString(piece_number);
/*     */           }
/*     */           
/*     */ 
/* 342 */           PieceInfoView.this.topLabelRHS = text;
/*     */         }
/*     */         else
/*     */         {
/* 346 */           PieceInfoView.this.topLabelRHS = "";
/*     */         }
/*     */         
/* 349 */         PieceInfoView.this.updateTopLabel();
/*     */       }
/*     */       
/* 352 */     });
/* 353 */     final Menu menu = new Menu(this.pieceInfoCanvas.getShell(), 8);
/*     */     
/* 355 */     this.pieceInfoCanvas.setMenu(menu);
/*     */     
/* 357 */     this.pieceInfoCanvas.addListener(35, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/*     */ 
/* 365 */         Point pt = PieceInfoView.this.pieceInfoCanvas.toControl(event.x, event.y);
/*     */         
/* 367 */         int piece_number = PieceInfoView.this.getPieceNumber(pt.x, pt.y);
/*     */         
/* 369 */         menu.setData("pieceNumber", Integer.valueOf(piece_number));
/*     */       }
/*     */       
/* 372 */     });
/* 373 */     MenuBuildUtils.addMaintenanceListenerForMenu(menu, new MenuBuildUtils.MenuBuilder()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void buildMenu(Menu menu, MenuEvent event)
/*     */       {
/*     */ 
/*     */ 
/* 382 */         Integer pn = (Integer)menu.getData("pieceNumber");
/*     */         
/* 384 */         if ((pn != null) && (pn.intValue() != -1))
/*     */         {
/* 386 */           DownloadManager download_manager = PieceInfoView.this.dlm;
/*     */           
/* 388 */           if (download_manager == null)
/*     */           {
/* 390 */             return;
/*     */           }
/*     */           
/* 393 */           DiskManager disk_manager = download_manager.getDiskManager();
/* 394 */           PEPeerManager peer_manager = download_manager.getPeerManager();
/*     */           
/* 396 */           if ((disk_manager == null) || (peer_manager == null))
/*     */           {
/* 398 */             return;
/*     */           }
/*     */           
/* 401 */           final PiecePicker picker = peer_manager.getPiecePicker();
/*     */           
/* 403 */           DiskManagerPiece[] dm_pieces = disk_manager.getPieces();
/* 404 */           PEPiece[] pe_pieces = peer_manager.getPieces();
/*     */           
/* 406 */           final int piece_number = pn.intValue();
/*     */           
/* 408 */           final DiskManagerPiece dm_piece = dm_pieces[piece_number];
/* 409 */           final PEPiece pm_piece = pe_pieces[piece_number];
/*     */           
/* 411 */           final MenuItem force_piece = new MenuItem(menu, 32);
/*     */           
/* 413 */           Messages.setLanguageText(force_piece, "label.force.piece");
/*     */           
/* 415 */           boolean done = dm_piece.isDone();
/*     */           
/* 417 */           force_piece.setEnabled(!done);
/*     */           
/* 419 */           if (!done)
/*     */           {
/* 421 */             force_piece.setSelection(picker.isForcePiece(piece_number));
/*     */             
/* 423 */             force_piece.addSelectionListener(new SelectionAdapter()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void widgetSelected(SelectionEvent e)
/*     */               {
/*     */ 
/* 430 */                 picker.setForcePiece(piece_number, force_piece.getSelection());
/*     */               }
/*     */             });
/*     */           }
/*     */           
/* 435 */           MenuItem reset_piece = new MenuItem(menu, 8);
/*     */           
/* 437 */           Messages.setLanguageText(reset_piece, "label.reset.piece");
/*     */           
/* 439 */           boolean can_reset = (dm_piece.isDone()) || (dm_piece.getNbWritten() > 0);
/*     */           
/* 441 */           reset_piece.setEnabled(can_reset);
/*     */           
/* 443 */           reset_piece.addSelectionListener(new SelectionAdapter()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void widgetSelected(SelectionEvent e)
/*     */             {
/*     */ 
/* 450 */               dm_piece.reset();
/*     */               
/* 452 */               if (pm_piece != null)
/*     */               {
/* 454 */                 pm_piece.reset();
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */ 
/*     */           });
/*     */         }
/*     */       }
/* 463 */     });
/* 464 */     Legend.createLegendComposite(this.pieceInfoComposite, this.blockColors, new String[] { "PiecesView.BlockView.Have", "PiecesView.BlockView.NoHave", "PeersView.BlockView.Transfer", "PeersView.BlockView.NextRequest", "PeersView.BlockView.AvailCount" }, new GridData(4, -1, true, false, 2, 1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 473 */     int iFontPixelsHeight = 10;
/* 474 */     int iFontPointHeight = iFontPixelsHeight * 72 / Utils.getDPIRaw(this.pieceInfoCanvas.getDisplay()).y;
/*     */     
/* 476 */     Font f = this.pieceInfoCanvas.getFont();
/* 477 */     FontData[] fontData = f.getFontData();
/* 478 */     fontData[0].setHeight(iFontPointHeight);
/* 479 */     this.font = new Font(this.pieceInfoCanvas.getDisplay(), fontData);
/*     */     
/* 481 */     return this.pieceInfoComposite;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int getPieceNumber(int x, int y)
/*     */   {
/* 489 */     DownloadManager manager = this.dlm;
/*     */     
/* 491 */     if (manager == null)
/*     */     {
/* 493 */       return -1;
/*     */     }
/*     */     
/* 496 */     PEPeerManager pm = manager.getPeerManager();
/*     */     
/* 498 */     if (pm == null)
/*     */     {
/* 500 */       return -1;
/*     */     }
/*     */     
/* 503 */     Rectangle bounds = this.pieceInfoCanvas.getClientArea();
/*     */     
/* 505 */     if ((bounds.width <= 0) || (bounds.height <= 0))
/*     */     {
/* 507 */       return -1;
/*     */     }
/*     */     
/* 510 */     int iNumCols = bounds.width / 17;
/*     */     
/* 512 */     int x_block = x / 17;
/* 513 */     int y_block = y / 17;
/*     */     
/* 515 */     int piece_number = y_block * iNumCols + x_block;
/*     */     
/* 517 */     if (piece_number >= pm.getPiecePicker().getNumberOfPieces())
/*     */     {
/* 519 */       return -1;
/*     */     }
/*     */     
/*     */ 
/* 523 */     return piece_number;
/*     */   }
/*     */   
/*     */ 
/* 527 */   private boolean alreadyFilling = false;
/*     */   private UISWTView swtView;
/*     */   
/*     */   private void fillPieceInfoSection()
/*     */   {
/* 532 */     synchronized (this) {
/* 533 */       if (this.alreadyFilling) {
/* 534 */         return;
/*     */       }
/* 536 */       this.alreadyFilling = true;
/*     */     }
/*     */     
/* 539 */     Utils.execSWTThreadLater(100, new AERunnable() {
/*     */       public void runSupport() {
/* 541 */         synchronized (PieceInfoView.this) {
/* 542 */           if (!PieceInfoView.this.alreadyFilling) {
/* 543 */             return;
/*     */           }
/*     */         }
/*     */         try
/*     */         {
/* 548 */           if ((PieceInfoView.this.imageLabel == null) || (PieceInfoView.this.imageLabel.isDisposed())) {
/*     */             return;
/*     */           }
/*     */           
/* 552 */           if (PieceInfoView.this.imageLabel.getImage() != null) {
/* 553 */             Image image = PieceInfoView.this.imageLabel.getImage();
/* 554 */             PieceInfoView.this.imageLabel.setImage(null);
/* 555 */             image.dispose();
/*     */           }
/*     */           
/* 558 */           PieceInfoView.this.refreshInfoCanvas();
/*     */         } finally {
/* 560 */           synchronized (PieceInfoView.this) {
/* 561 */             PieceInfoView.this.alreadyFilling = false;
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 569 */     if (this.loopFactor++ % this.graphicsUpdate == 0) {
/* 570 */       refreshInfoCanvas();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void updateTopLabel()
/*     */   {
/* 577 */     String text = this.topLabelLHS;
/*     */     
/* 579 */     if ((text.length() > 0) && (this.topLabelRHS.length() > 0))
/*     */     {
/* 581 */       text = text + "; " + this.topLabelRHS;
/*     */     }
/*     */     
/* 584 */     this.topLabel.setText(text);
/*     */   }
/*     */   
/*     */   protected void refreshInfoCanvas() {
/* 588 */     synchronized (this) {
/* 589 */       this.alreadyFilling = false;
/*     */     }
/*     */     
/* 592 */     if ((this.pieceInfoCanvas == null) || (this.pieceInfoCanvas.isDisposed()) || (!this.pieceInfoCanvas.isVisible()))
/*     */     {
/* 594 */       return;
/*     */     }
/* 596 */     this.pieceInfoCanvas.layout(true);
/* 597 */     Rectangle bounds = this.pieceInfoCanvas.getClientArea();
/* 598 */     if ((bounds.width <= 0) || (bounds.height <= 0)) {
/* 599 */       this.topLabelLHS = "";
/* 600 */       updateTopLabel();
/* 601 */       return;
/*     */     }
/*     */     
/* 604 */     if (this.dlm == null) {
/* 605 */       GC gc = new GC(this.pieceInfoCanvas);
/* 606 */       gc.fillRectangle(bounds);
/* 607 */       gc.dispose();
/* 608 */       this.topLabelLHS = MessageText.getString("view.one.download.only");
/* 609 */       this.topLabelRHS = "";
/* 610 */       updateTopLabel();
/*     */       
/* 612 */       return;
/*     */     }
/*     */     
/* 615 */     PEPeerManager pm = this.dlm.getPeerManager();
/*     */     
/* 617 */     DiskManager dm = this.dlm.getDiskManager();
/*     */     
/* 619 */     if ((pm == null) || (dm == null)) {
/* 620 */       GC gc = new GC(this.pieceInfoCanvas);
/* 621 */       gc.fillRectangle(bounds);
/* 622 */       gc.dispose();
/* 623 */       this.topLabelLHS = "";
/* 624 */       updateTopLabel();
/*     */       
/* 626 */       return;
/*     */     }
/*     */     
/* 629 */     int iNumCols = bounds.width / 17;
/* 630 */     int iNeededHeight = ((dm.getNbPieces() - 1) / iNumCols + 1) * 17;
/*     */     
/* 632 */     if ((this.img != null) && (!this.img.isDisposed())) {
/* 633 */       Rectangle imgBounds = this.img.getBounds();
/* 634 */       if ((imgBounds.width != bounds.width) || (imgBounds.height != iNeededHeight)) {
/* 635 */         this.oldBlockInfo = null;
/* 636 */         this.img.dispose();
/* 637 */         this.img = null;
/*     */       }
/*     */     }
/*     */     
/* 641 */     DiskManagerPiece[] dm_pieces = dm.getPieces();
/*     */     
/* 643 */     PEPiece[] currentDLPieces = pm.getPieces();
/* 644 */     byte[] uploadingPieces = new byte[dm_pieces.length];
/*     */     
/*     */ 
/* 647 */     Iterator<PEPeer> peer_it = pm.getPeers().iterator();
/* 648 */     while (peer_it.hasNext()) {
/* 649 */       PEPeer peer = (PEPeer)peer_it.next();
/* 650 */       int[] peerRequestedPieces = peer.getIncomingRequestedPieceNumbers();
/* 651 */       if ((peerRequestedPieces != null) && (peerRequestedPieces.length > 0)) {
/* 652 */         int pieceNum = peerRequestedPieces[0];
/* 653 */         if (uploadingPieces[pieceNum] < 2)
/* 654 */           uploadingPieces[pieceNum] = 2;
/* 655 */         for (int j = 1; j < peerRequestedPieces.length; j++) {
/* 656 */           pieceNum = peerRequestedPieces[j];
/* 657 */           if (uploadingPieces[pieceNum] < 1) {
/* 658 */             uploadingPieces[pieceNum] = 1;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 664 */     if (this.sc.getMinHeight() != iNeededHeight) {
/* 665 */       this.sc.setMinHeight(iNeededHeight);
/* 666 */       this.sc.layout(true, true);
/* 667 */       bounds = this.pieceInfoCanvas.getClientArea();
/*     */     }
/*     */     
/* 670 */     int[] availability = pm.getAvailability();
/*     */     
/* 672 */     int minAvailability = Integer.MAX_VALUE;
/* 673 */     int minAvailability2 = Integer.MAX_VALUE;
/* 674 */     if ((availability != null) && (availability.length > 0)) {
/* 675 */       for (int i = 0; i < availability.length; i++) {
/* 676 */         if ((availability[i] != 0) && (availability[i] < minAvailability)) {
/* 677 */           minAvailability2 = minAvailability;
/* 678 */           minAvailability = availability[i];
/* 679 */           if (minAvailability == 1) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 686 */     if (this.img == null) {
/* 687 */       this.img = new Image(this.pieceInfoCanvas.getDisplay(), bounds.width, iNeededHeight);
/* 688 */       this.oldBlockInfo = null;
/*     */     }
/* 690 */     GC gcImg = new GC(this.img);
/*     */     
/*     */ 
/* 693 */     BlockInfo[] newBlockInfo = new BlockInfo[dm_pieces.length];
/*     */     
/* 695 */     int iRow = 0;
/*     */     try
/*     */     {
/* 698 */       gcImg.setAdvanced(true);
/*     */       
/* 700 */       if (this.oldBlockInfo == null) {
/* 701 */         gcImg.setBackground(this.pieceInfoCanvas.getBackground());
/* 702 */         gcImg.fillRectangle(0, 0, bounds.width, iNeededHeight);
/*     */       }
/*     */       
/* 705 */       gcImg.setFont(this.font);
/*     */       
/* 707 */       int iCol = 0;
/* 708 */       for (int i = 0; i < dm_pieces.length; i++) {
/* 709 */         if (iCol >= iNumCols) {
/* 710 */           iCol = 0;
/* 711 */           iRow++;
/*     */         }
/*     */         
/* 714 */         newBlockInfo[i] = new BlockInfo();
/*     */         
/*     */ 
/* 717 */         boolean done = dm_pieces[i].isDone();
/* 718 */         int iXPos = iCol * 17 + 1;
/* 719 */         int iYPos = iRow * 17 + 1;
/*     */         
/* 721 */         if (done) {
/* 722 */           int colorIndex = 0;
/* 723 */           newBlockInfo[i].haveWidth = 14;
/*     */         }
/*     */         else {
/* 726 */           boolean partiallyDone = dm_pieces[i].getNbWritten() > 0;
/*     */           
/* 728 */           int width = 14;
/* 729 */           if (partiallyDone) {
/* 730 */             int iNewWidth = (int)(dm_pieces[i].getNbWritten() / dm_pieces[i].getNbBlocks() * width);
/* 731 */             if (iNewWidth >= width) {
/* 732 */               iNewWidth = width - 1;
/* 733 */             } else if (iNewWidth <= 0) {
/* 734 */               iNewWidth = 1;
/*     */             }
/* 736 */             newBlockInfo[i].haveWidth = iNewWidth;
/*     */           }
/*     */         }
/*     */         
/* 740 */         if ((currentDLPieces[i] != null) && (currentDLPieces[i].hasUndownloadedBlock())) {
/* 741 */           newBlockInfo[i].downloadingIndicator = true;
/*     */         }
/*     */         
/* 744 */         newBlockInfo[i].uploadingIndicator = (uploadingPieces[i] > 0);
/*     */         
/* 746 */         if (newBlockInfo[i].uploadingIndicator) {
/* 747 */           newBlockInfo[i].uploadingIndicatorSmall = (uploadingPieces[i] < 2);
/*     */         }
/*     */         
/*     */ 
/* 751 */         if (availability != null) {
/* 752 */           newBlockInfo[i].availNum = availability[i];
/* 753 */           if (minAvailability2 == availability[i]) {
/* 754 */             newBlockInfo[i].availDotted = true;
/*     */           }
/*     */         } else {
/* 757 */           newBlockInfo[i].availNum = -1;
/*     */         }
/*     */         
/* 760 */         if ((this.oldBlockInfo != null) && (i < this.oldBlockInfo.length) && (this.oldBlockInfo[i].sameAs(newBlockInfo[i])))
/*     */         {
/* 762 */           iCol++;
/*     */         }
/*     */         else
/*     */         {
/* 766 */           gcImg.setBackground(this.pieceInfoCanvas.getBackground());
/* 767 */           gcImg.fillRectangle(iCol * 17, iRow * 17, 17, 17);
/*     */           
/*     */ 
/* 770 */           int colorIndex = 0;
/* 771 */           gcImg.setBackground(this.blockColors[colorIndex]);
/* 772 */           gcImg.fillRectangle(iXPos, iYPos, newBlockInfo[i].haveWidth, 14);
/*     */           
/* 774 */           colorIndex = 1;
/* 775 */           gcImg.setBackground(this.blockColors[colorIndex]);
/* 776 */           gcImg.fillRectangle(iXPos + newBlockInfo[i].haveWidth, iYPos, 14 - newBlockInfo[i].haveWidth, 14);
/*     */           
/* 778 */           if (newBlockInfo[i].downloadingIndicator) {
/* 779 */             drawDownloadIndicator(gcImg, iXPos, iYPos, false);
/*     */           }
/*     */           
/* 782 */           if (newBlockInfo[i].uploadingIndicator) {
/* 783 */             drawUploadIndicator(gcImg, iXPos, iYPos, newBlockInfo[i].uploadingIndicatorSmall);
/*     */           }
/*     */           
/* 786 */           if (newBlockInfo[i].availNum != -1) {
/* 787 */             if (minAvailability == newBlockInfo[i].availNum) {
/* 788 */               gcImg.setForeground(this.blockColors[4]);
/* 789 */               gcImg.drawRectangle(iXPos - 1, iYPos - 1, 15, 15);
/*     */             }
/*     */             
/* 792 */             if (minAvailability2 == newBlockInfo[i].availNum) {
/* 793 */               gcImg.setLineStyle(3);
/* 794 */               gcImg.setForeground(this.blockColors[4]);
/* 795 */               gcImg.drawRectangle(iXPos - 1, iYPos - 1, 15, 15);
/*     */               
/* 797 */               gcImg.setLineStyle(1);
/*     */             }
/*     */             
/* 800 */             String sNumber = String.valueOf(newBlockInfo[i].availNum);
/* 801 */             Point size = gcImg.stringExtent(sNumber);
/*     */             
/* 803 */             if (newBlockInfo[i].availNum < 100) {
/* 804 */               int x = iXPos + 7 - size.x / 2;
/* 805 */               int y = iYPos + 7 - size.y / 2;
/* 806 */               gcImg.setForeground(this.blockColors[4]);
/* 807 */               gcImg.drawText(sNumber, x, y, true);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 812 */           iCol++;
/*     */         } }
/* 814 */       this.oldBlockInfo = newBlockInfo;
/*     */     } catch (Exception e) {
/* 816 */       Logger.log(new LogEvent(LogIDs.GUI, "drawing piece map", e));
/*     */     } finally {
/* 818 */       gcImg.dispose();
/*     */     }
/*     */     
/* 821 */     this.topLabelLHS = MessageText.getString("PiecesView.BlockView.Header", new String[] { "" + iNumCols, "" + (iRow + 1), "" + dm_pieces.length });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 828 */     updateTopLabel();
/*     */     
/* 830 */     this.pieceInfoCanvas.redraw();
/*     */   }
/*     */   
/*     */   private void drawDownloadIndicator(GC gcImg, int iXPos, int iYPos, boolean small)
/*     */   {
/* 835 */     if (small) {
/* 836 */       gcImg.setBackground(this.blockColors[3]);
/* 837 */       gcImg.fillPolygon(new int[] { iXPos + 2, iYPos + 2, iXPos + 14 - 1, iYPos + 2, iXPos + 7, iYPos + 14 - 1 });
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/* 846 */       gcImg.setBackground(this.blockColors[2]);
/* 847 */       gcImg.fillPolygon(new int[] { iXPos, iYPos, iXPos + 14, iYPos, iXPos + 7, iYPos + 14 });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void drawUploadIndicator(GC gcImg, int iXPos, int iYPos, boolean small)
/*     */   {
/* 859 */     if (!small) {
/* 860 */       gcImg.setBackground(this.blockColors[2]);
/* 861 */       gcImg.fillPolygon(new int[] { iXPos, iYPos + 14, iXPos + 14, iYPos + 14, iXPos + 7, iYPos });
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 871 */       gcImg.setBackground(this.blockColors[3]);
/* 872 */       gcImg.fillPolygon(new int[] { iXPos + 1, iYPos + 14 - 2, iXPos + 14 - 2, iYPos + 14 - 2, iXPos + 7, iYPos + 2 });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Composite getComposite()
/*     */   {
/* 885 */     return this.pieceInfoComposite;
/*     */   }
/*     */   
/*     */   private void delete() {
/* 889 */     if ((this.imageLabel != null) && (!this.imageLabel.isDisposed()) && (this.imageLabel.getImage() != null))
/*     */     {
/* 891 */       Image image = this.imageLabel.getImage();
/* 892 */       this.imageLabel.setImage(null);
/* 893 */       image.dispose();
/*     */     }
/*     */     
/* 896 */     if ((this.img != null) && (!this.img.isDisposed())) {
/* 897 */       this.img.dispose();
/* 898 */       this.img = null;
/*     */     }
/*     */     
/* 901 */     if ((this.font != null) && (!this.font.isDisposed())) {
/* 902 */       this.font.dispose();
/* 903 */       this.font = null;
/*     */     }
/*     */     
/* 906 */     synchronized (this) {
/* 907 */       if (this.dlm != null) {
/* 908 */         this.dlm.removePieceListener(this);
/* 909 */         this.dlm = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private Image obfusticatedImage(Image image) {
/* 915 */     UIDebugGenerator.obfusticateArea(image, this.topLabel, "");
/* 916 */     return image;
/*     */   }
/*     */   
/*     */   public void pieceAdded(PEPiece piece)
/*     */   {
/* 921 */     fillPieceInfoSection();
/*     */   }
/*     */   
/*     */   public void pieceRemoved(PEPiece piece)
/*     */   {
/* 926 */     fillPieceInfoSection();
/*     */   }
/*     */   
/*     */ 
/*     */   private static class BlockInfo
/*     */   {
/*     */     public int haveWidth;
/*     */     int availNum;
/*     */     boolean availDotted;
/*     */     boolean uploadingIndicator;
/*     */     boolean uploadingIndicatorSmall;
/*     */     boolean downloadingIndicator;
/*     */     
/*     */     public BlockInfo()
/*     */     {
/* 941 */       this.haveWidth = -1;
/*     */     }
/*     */     
/*     */     public boolean sameAs(BlockInfo otherBlockInfo) {
/* 945 */       return (this.haveWidth == otherBlockInfo.haveWidth) && (this.availNum == otherBlockInfo.availNum) && (this.availDotted == otherBlockInfo.availDotted) && (this.uploadingIndicator == otherBlockInfo.uploadingIndicator) && (this.uploadingIndicatorSmall == otherBlockInfo.uploadingIndicatorSmall) && (this.downloadingIndicator == otherBlockInfo.downloadingIndicator);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean eventOccurred(UISWTViewEvent event)
/*     */   {
/* 955 */     switch (event.getType()) {
/*     */     case 0: 
/* 957 */       this.swtView = ((UISWTView)event.getData());
/* 958 */       this.swtView.setTitle(getFullTitle());
/* 959 */       break;
/*     */     
/*     */     case 7: 
/* 962 */       delete();
/* 963 */       break;
/*     */     
/*     */     case 2: 
/* 966 */       initialize((Composite)event.getData());
/* 967 */       break;
/*     */     
/*     */     case 6: 
/* 970 */       Messages.updateLanguageForControl(getComposite());
/* 971 */       this.swtView.setTitle(getFullTitle());
/* 972 */       break;
/*     */     
/*     */     case 1: 
/* 975 */       dataSourceChanged(event.getData());
/* 976 */       break;
/*     */     
/*     */     case 3: 
/*     */       break;
/*     */     
/*     */     case 5: 
/* 982 */       refresh();
/* 983 */       break;
/*     */     
/*     */     case 9: 
/* 986 */       Object data = event.getData();
/* 987 */       if ((data instanceof Map)) {
/* 988 */         obfusticatedImage((Image)MapUtils.getMapObject((Map)data, "image", null, Image.class));
/*     */       }
/*     */       
/*     */       break;
/*     */     }
/*     */     
/* 994 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/piece/PieceInfoView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */