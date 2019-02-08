/*     */ package org.gudy.azureus2.ui.swt.views.file;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
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
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuBuilder;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.Legend;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
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
/*     */ public class FileInfoView
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*     */   private static final int BLOCK_FILLSIZE = 14;
/*     */   private static final int BLOCK_SPACING = 2;
/*     */   private static final int BLOCK_SIZE = 16;
/*     */   private static final int BLOCKCOLOR_DONE = 0;
/*     */   private static final int BLOCKCOLOR_SKIPPED = 1;
/*     */   private static final int BLOCKCOLOR_ACTIVE = 2;
/*     */   private static final int BLOCKCOLOR_NEEDED = 3;
/*     */   private Composite fileInfoComposite;
/*     */   private ScrolledComposite sc;
/*     */   protected Canvas fileInfoCanvas;
/*     */   private Color[] blockColors;
/*     */   private Label topLabel;
/*  84 */   private int graphicsUpdate = COConfigurationManager.getIntParameter("Graphics Update") * 2;
/*     */   
/*     */ 
/*  87 */   private int loopFactor = 0;
/*     */   
/*     */   private DiskManagerFileInfo file;
/*     */   
/*  91 */   private Font font = null;
/*     */   
/*  93 */   Image img = null;
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean refreshInfoCanvasQueued;
/*     */   
/*     */ 
/*     */   private UISWTView swtView;
/*     */   
/*     */ 
/*     */ 
/*     */   public FileInfoView()
/*     */   {
/* 106 */     this.blockColors = new Color[] { Colors.blues[9], Colors.white, Colors.red, Colors.green };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void dataSourceChanged(Object newDataSource)
/*     */   {
/* 116 */     if (((newDataSource instanceof Object[])) && (((Object[])newDataSource).length > 0))
/*     */     {
/* 118 */       newDataSource = ((Object[])(Object[])newDataSource)[0];
/*     */     }
/*     */     
/* 121 */     if ((newDataSource instanceof DiskManagerFileInfo)) {
/* 122 */       this.file = ((DiskManagerFileInfo)newDataSource);
/*     */     } else {
/* 124 */       this.file = null;
/*     */     }
/*     */     
/* 127 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 129 */         FileInfoView.this.fillFileInfoSection();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private String getFullTitle() {
/* 135 */     return MessageText.getString("FileView.BlockView.title");
/*     */   }
/*     */   
/*     */   private void initialize(Composite composite) {
/* 139 */     if ((this.fileInfoComposite != null) && (!this.fileInfoComposite.isDisposed())) {
/* 140 */       Logger.log(new LogEvent(LogIDs.GUI, 3, "FileInfoView already initialized! Stack: " + Debug.getStackTrace(true, false)));
/*     */       
/*     */ 
/* 143 */       delete();
/*     */     }
/* 145 */     createFileInfoPanel(composite);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Composite createFileInfoPanel(Composite parent)
/*     */   {
/* 155 */     this.fileInfoComposite = new Composite(parent, 0);
/* 156 */     GridLayout layout = new GridLayout();
/* 157 */     layout.numColumns = 2;
/* 158 */     layout.horizontalSpacing = 0;
/* 159 */     layout.verticalSpacing = 0;
/* 160 */     layout.marginHeight = 0;
/* 161 */     layout.marginWidth = 0;
/* 162 */     this.fileInfoComposite.setLayout(layout);
/* 163 */     GridData gridData = new GridData(4, 4, true, true);
/* 164 */     this.fileInfoComposite.setLayoutData(gridData);
/*     */     
/* 166 */     new Label(this.fileInfoComposite, 0).setLayoutData(new GridData());
/*     */     
/* 168 */     this.topLabel = new Label(this.fileInfoComposite, 0);
/* 169 */     gridData = new GridData(4, -1, false, false);
/* 170 */     this.topLabel.setLayoutData(gridData);
/*     */     
/* 172 */     this.sc = new ScrolledComposite(this.fileInfoComposite, 512);
/* 173 */     this.sc.setExpandHorizontal(true);
/* 174 */     this.sc.setExpandVertical(true);
/* 175 */     layout = new GridLayout();
/* 176 */     layout.horizontalSpacing = 0;
/* 177 */     layout.verticalSpacing = 0;
/* 178 */     layout.marginHeight = 0;
/* 179 */     layout.marginWidth = 0;
/* 180 */     this.sc.setLayout(layout);
/* 181 */     gridData = new GridData(4, 4, true, true, 2, 1);
/* 182 */     this.sc.setLayoutData(gridData);
/*     */     
/* 184 */     this.fileInfoCanvas = new Canvas(this.sc, 1310720);
/* 185 */     gridData = new GridData(4, -1, true, false);
/* 186 */     this.fileInfoCanvas.setLayoutData(gridData);
/* 187 */     this.fileInfoCanvas.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 189 */         if ((e.width <= 0) || (e.height <= 0))
/* 190 */           return;
/*     */         try {
/* 192 */           Rectangle bounds = FileInfoView.this.img == null ? null : FileInfoView.this.img.getBounds();
/* 193 */           if (bounds == null) {
/* 194 */             e.gc.fillRectangle(e.x, e.y, e.width, e.height);
/*     */           } else {
/* 196 */             if (e.x + e.width > bounds.width) {
/* 197 */               e.gc.fillRectangle(bounds.width, e.y, e.x + e.width - bounds.width + 1, e.height);
/*     */             }
/* 199 */             if (e.y + e.height > bounds.height) {
/* 200 */               e.gc.fillRectangle(e.x, bounds.height, e.width, e.y + e.height - bounds.height + 1);
/*     */             }
/*     */             
/* 203 */             int width = Math.min(e.width, bounds.width - e.x);
/* 204 */             int height = Math.min(e.height, bounds.height - e.y);
/* 205 */             e.gc.drawImage(FileInfoView.this.img, e.x, e.y, width, height, e.x, e.y, width, height);
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         catch (Exception ex) {}
/*     */       }
/* 212 */     });
/* 213 */     this.fileInfoCanvas.addMouseTrackListener(new MouseTrackAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void mouseHover(MouseEvent event)
/*     */       {
/*     */ 
/* 220 */         FileInfoView.this.showPieceDetails(event.x, event.y);
/*     */       }
/*     */       
/* 223 */     });
/* 224 */     Listener doNothingListener = new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {}
/* 227 */     };
/* 228 */     this.fileInfoCanvas.addListener(1, doNothingListener);
/*     */     
/* 230 */     final Menu menu = new Menu(this.fileInfoCanvas.getShell(), 8);
/*     */     
/* 232 */     this.fileInfoCanvas.setMenu(menu);
/*     */     
/* 234 */     this.fileInfoCanvas.addListener(35, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/*     */ 
/* 242 */         Point pt = FileInfoView.this.fileInfoCanvas.toControl(event.x, event.y);
/*     */         
/* 244 */         int piece_number = FileInfoView.this.getPieceNumber(pt.x, pt.y);
/*     */         
/* 246 */         menu.setData("pieceNumber", Integer.valueOf(piece_number));
/*     */       }
/*     */       
/* 249 */     });
/* 250 */     MenuBuildUtils.addMaintenanceListenerForMenu(menu, new MenuBuildUtils.MenuBuilder()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void buildMenu(Menu menu, MenuEvent event)
/*     */       {
/*     */ 
/*     */ 
/* 259 */         Integer pn = (Integer)menu.getData("pieceNumber");
/*     */         
/* 261 */         if ((pn != null) && (pn.intValue() != -1))
/*     */         {
/* 263 */           DownloadManager download_manager = FileInfoView.this.file.getDownloadManager();
/*     */           
/* 265 */           if (download_manager == null)
/*     */           {
/* 267 */             return;
/*     */           }
/*     */           
/* 270 */           DiskManager disk_manager = download_manager.getDiskManager();
/* 271 */           PEPeerManager peer_manager = download_manager.getPeerManager();
/*     */           
/* 273 */           if ((disk_manager == null) || (peer_manager == null))
/*     */           {
/* 275 */             return;
/*     */           }
/*     */           
/* 278 */           final PiecePicker picker = peer_manager.getPiecePicker();
/*     */           
/* 280 */           DiskManagerPiece[] dm_pieces = disk_manager.getPieces();
/* 281 */           PEPiece[] pe_pieces = peer_manager.getPieces();
/*     */           
/* 283 */           final int piece_number = pn.intValue();
/*     */           
/* 285 */           final DiskManagerPiece dm_piece = dm_pieces[piece_number];
/* 286 */           final PEPiece pe_piece = pe_pieces[piece_number];
/*     */           
/* 288 */           final MenuItem force_piece = new MenuItem(menu, 32);
/*     */           
/* 290 */           Messages.setLanguageText(force_piece, "label.force.piece");
/*     */           
/* 292 */           boolean done = dm_piece.isDone();
/*     */           
/* 294 */           force_piece.setEnabled(!done);
/*     */           
/* 296 */           if (!done)
/*     */           {
/* 298 */             force_piece.setSelection(picker.isForcePiece(piece_number));
/*     */             
/* 300 */             force_piece.addSelectionListener(new SelectionAdapter()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void widgetSelected(SelectionEvent e)
/*     */               {
/*     */ 
/* 307 */                 picker.setForcePiece(piece_number, force_piece.getSelection());
/*     */               }
/*     */             });
/*     */           }
/*     */           
/* 312 */           MenuItem reset_piece = new MenuItem(menu, 8);
/*     */           
/* 314 */           Messages.setLanguageText(reset_piece, "label.reset.piece");
/*     */           
/* 316 */           boolean can_reset = (dm_piece.isDone()) || (dm_piece.getNbWritten() > 0);
/*     */           
/* 318 */           reset_piece.setEnabled(can_reset);
/*     */           
/* 320 */           reset_piece.addSelectionListener(new SelectionAdapter()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void widgetSelected(SelectionEvent e)
/*     */             {
/*     */ 
/* 327 */               dm_piece.reset();
/*     */               
/* 329 */               if (pe_piece != null)
/*     */               {
/* 331 */                 pe_piece.reset();
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */           });
/*     */         }
/*     */       }
/* 339 */     });
/* 340 */     this.fileInfoCanvas.addListener(11, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e)
/*     */       {
/* 344 */         if (FileInfoView.this.refreshInfoCanvasQueued) {
/* 345 */           return;
/*     */         }
/*     */         
/* 348 */         FileInfoView.this.refreshInfoCanvasQueued = true;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 353 */         Utils.execSWTThreadLater(0, new AERunnable() {
/*     */           public void runSupport() {
/* 355 */             if (FileInfoView.this.img != null) {
/* 356 */               int iOldColCount = FileInfoView.this.img.getBounds().width / 16;
/* 357 */               int iNewColCount = FileInfoView.this.fileInfoCanvas.getClientArea().width / 16;
/* 358 */               if (iOldColCount != iNewColCount) {
/* 359 */                 FileInfoView.this.refreshInfoCanvas();
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 365 */     });
/* 366 */     this.sc.setContent(this.fileInfoCanvas);
/*     */     
/* 368 */     Legend.createLegendComposite(this.fileInfoComposite, this.blockColors, new String[] { "FileView.BlockView.Done", "FileView.BlockView.Skipped", "FileView.BlockView.Active", "FileView.BlockView.Outstanding" }, new GridData(4, -1, true, false, 2, 1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 378 */     int iFontPixelsHeight = 10;
/* 379 */     int iFontPointHeight = iFontPixelsHeight * 72 / Utils.getDPIRaw(this.fileInfoCanvas.getDisplay()).y;
/*     */     
/* 381 */     Font f = this.fileInfoCanvas.getFont();
/* 382 */     FontData[] fontData = f.getFontData();
/* 383 */     fontData[0].setHeight(iFontPointHeight);
/* 384 */     this.font = new Font(this.fileInfoCanvas.getDisplay(), fontData);
/*     */     
/* 386 */     return this.fileInfoComposite;
/*     */   }
/*     */   
/*     */   private void fillFileInfoSection() {
/* 390 */     if ((this.topLabel == null) || (this.topLabel.isDisposed())) {
/* 391 */       return;
/*     */     }
/* 393 */     this.topLabel.setText("");
/*     */     
/* 395 */     refreshInfoCanvas();
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 399 */     if (this.loopFactor++ % this.graphicsUpdate == 0) {
/* 400 */       refreshInfoCanvas();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int getPieceNumber(int x, int y)
/*     */   {
/* 410 */     Rectangle bounds = this.fileInfoCanvas.getClientArea();
/*     */     
/* 412 */     if ((bounds.width <= 0) || (bounds.height <= 0))
/*     */     {
/* 414 */       return -1;
/*     */     }
/*     */     
/* 417 */     if (this.file == null)
/*     */     {
/* 419 */       return -1;
/*     */     }
/*     */     
/* 422 */     DownloadManager download_manager = this.file.getDownloadManager();
/*     */     
/* 424 */     if (download_manager == null)
/*     */     {
/* 426 */       return -1;
/*     */     }
/*     */     
/* 429 */     DiskManager disk_manager = download_manager.getDiskManager();
/* 430 */     PEPeerManager peer_manager = download_manager.getPeerManager();
/*     */     
/* 432 */     if ((disk_manager == null) || (peer_manager == null))
/*     */     {
/* 434 */       return -1;
/*     */     }
/*     */     
/* 437 */     int first_piece = this.file.getFirstPieceNumber();
/* 438 */     int num_pieces = this.file.getNbPieces();
/*     */     
/* 440 */     int iNumCols = bounds.width / 16;
/*     */     
/* 442 */     int x_block = x / 16;
/* 443 */     int y_block = y / 16;
/*     */     
/* 445 */     int piece_number = y_block * iNumCols + x_block + first_piece;
/*     */     
/* 447 */     if ((piece_number >= first_piece) && (piece_number < first_piece + num_pieces))
/*     */     {
/* 449 */       return piece_number;
/*     */     }
/*     */     
/*     */ 
/* 453 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void showPieceDetails(int x, int y)
/*     */   {
/* 462 */     int piece_number = getPieceNumber(x, y);
/*     */     
/* 464 */     if (piece_number >= 0)
/*     */     {
/* 466 */       DownloadManager download_manager = this.file.getDownloadManager();
/*     */       
/* 468 */       if (download_manager == null)
/*     */       {
/* 470 */         this.topLabel.setText("");
/*     */         
/* 472 */         return;
/*     */       }
/*     */       
/* 475 */       DiskManager disk_manager = download_manager.getDiskManager();
/* 476 */       PEPeerManager peer_manager = download_manager.getPeerManager();
/*     */       
/* 478 */       if ((disk_manager == null) || (peer_manager == null))
/*     */       {
/* 480 */         this.topLabel.setText("");
/*     */         
/* 482 */         return;
/*     */       }
/*     */       
/* 485 */       DiskManagerPiece[] dm_pieces = disk_manager.getPieces();
/* 486 */       PEPiece[] pe_pieces = peer_manager.getPieces();
/*     */       
/* 488 */       DiskManagerPiece dm_piece = dm_pieces[piece_number];
/* 489 */       PEPiece pe_piece = pe_pieces[piece_number];
/*     */       
/* 491 */       String text = "Piece " + piece_number + ": " + dm_piece.getString();
/*     */       
/* 493 */       if (pe_piece != null)
/*     */       {
/* 495 */         text = text + ", active: " + pe_piece.getString();
/*     */ 
/*     */ 
/*     */       }
/* 499 */       else if ((dm_piece.isNeeded()) && (!dm_piece.isDone()))
/*     */       {
/* 501 */         text = text + ", inactive: " + peer_manager.getPiecePicker().getPieceString(piece_number);
/*     */       }
/*     */       
/*     */ 
/* 505 */       this.topLabel.setText(text);
/*     */     }
/*     */     else
/*     */     {
/* 509 */       this.topLabel.setText("");
/*     */     }
/*     */   }
/*     */   
/*     */   protected void refreshInfoCanvas() {
/* 514 */     if ((this.fileInfoCanvas == null) || (this.fileInfoCanvas.isDisposed())) {
/* 515 */       return;
/*     */     }
/* 517 */     this.refreshInfoCanvasQueued = false;
/* 518 */     Rectangle bounds = this.fileInfoCanvas.getClientArea();
/* 519 */     if ((bounds.width <= 0) || (bounds.height <= 0)) {
/* 520 */       return;
/*     */     }
/* 522 */     if ((this.img != null) && (!this.img.isDisposed())) {
/* 523 */       this.img.dispose();
/* 524 */       this.img = null;
/*     */     }
/*     */     
/* 527 */     DownloadManager download_manager = this.file == null ? null : this.file.getDownloadManager();
/*     */     
/* 529 */     DiskManager disk_manager = download_manager == null ? null : download_manager.getDiskManager();
/* 530 */     PEPeerManager peer_manager = download_manager == null ? null : download_manager.getPeerManager();
/*     */     
/* 532 */     if ((this.file == null) || (disk_manager == null) || (peer_manager == null)) {
/* 533 */       GC gc = new GC(this.fileInfoCanvas);
/* 534 */       gc.fillRectangle(bounds);
/* 535 */       gc.dispose();
/*     */       
/* 537 */       return;
/*     */     }
/*     */     
/* 540 */     int first_piece = this.file.getFirstPieceNumber();
/* 541 */     int num_pieces = this.file.getNbPieces();
/*     */     
/* 543 */     int iNumCols = bounds.width / 16;
/* 544 */     int iNeededHeight = ((num_pieces - 1) / iNumCols + 1) * 16;
/*     */     
/* 546 */     if (this.sc.getMinHeight() != iNeededHeight) {
/* 547 */       this.sc.setMinHeight(iNeededHeight);
/* 548 */       this.sc.layout(true, true);
/* 549 */       bounds = this.fileInfoCanvas.getClientArea();
/*     */     }
/*     */     
/* 552 */     this.img = new Image(this.fileInfoCanvas.getDisplay(), bounds.width, iNeededHeight);
/* 553 */     GC gcImg = new GC(this.img);
/*     */     try
/*     */     {
/* 556 */       gcImg.setBackground(this.fileInfoCanvas.getBackground());
/* 557 */       gcImg.fillRectangle(0, 0, bounds.width, bounds.height);
/*     */       
/*     */ 
/* 560 */       DiskManagerPiece[] dm_pieces = disk_manager.getPieces();
/* 561 */       PEPiece[] pe_pieces = peer_manager.getPieces();
/*     */       
/* 563 */       int iRow = 0;
/* 564 */       int iCol = 0;
/*     */       
/* 566 */       for (int i = first_piece; i < first_piece + num_pieces; i++)
/*     */       {
/* 568 */         DiskManagerPiece dm_piece = dm_pieces[i];
/* 569 */         PEPiece pe_piece = pe_pieces[i];
/*     */         
/*     */ 
/*     */ 
/* 573 */         int iXPos = iCol * 16;
/* 574 */         int iYPos = iRow * 16;
/*     */         int colorIndex;
/* 576 */         int colorIndex; if (dm_piece.isDone())
/*     */         {
/* 578 */           colorIndex = 0;
/*     */         } else { int colorIndex;
/* 580 */           if (!dm_piece.isNeeded())
/*     */           {
/* 582 */             colorIndex = 1;
/*     */           } else { int colorIndex;
/* 584 */             if (pe_piece != null)
/*     */             {
/* 586 */               colorIndex = 2;
/*     */             }
/*     */             else
/*     */             {
/* 590 */               colorIndex = 3; }
/*     */           }
/*     */         }
/* 593 */         gcImg.setBackground(this.blockColors[colorIndex]);
/* 594 */         gcImg.fillRectangle(iXPos, iYPos, 14, 14);
/*     */         
/*     */ 
/* 597 */         iCol++;
/* 598 */         if (iCol >= iNumCols) {
/* 599 */           iCol = 0;
/* 600 */           iRow++;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 605 */       Logger.log(new LogEvent(LogIDs.GUI, "drawing piece map", e));
/*     */     } finally {
/* 607 */       gcImg.dispose();
/*     */     }
/*     */     
/* 610 */     this.fileInfoCanvas.redraw();
/*     */   }
/*     */   
/*     */   private Composite getComposite() {
/* 614 */     return this.fileInfoComposite;
/*     */   }
/*     */   
/*     */   private void delete() {
/* 618 */     if ((this.img != null) && (!this.img.isDisposed())) {
/* 619 */       this.img.dispose();
/* 620 */       this.img = null;
/*     */     }
/*     */     
/* 623 */     if ((this.font != null) && (!this.font.isDisposed())) {
/* 624 */       this.font.dispose();
/* 625 */       this.font = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 630 */     switch (event.getType()) {
/*     */     case 0: 
/* 632 */       this.swtView = ((UISWTView)event.getData());
/* 633 */       this.swtView.setTitle(getFullTitle());
/* 634 */       break;
/*     */     
/*     */     case 7: 
/* 637 */       delete();
/* 638 */       break;
/*     */     
/*     */     case 2: 
/* 641 */       initialize((Composite)event.getData());
/* 642 */       break;
/*     */     
/*     */     case 6: 
/* 645 */       Messages.updateLanguageForControl(getComposite());
/* 646 */       this.swtView.setTitle(getFullTitle());
/* 647 */       break;
/*     */     
/*     */     case 1: 
/* 650 */       dataSourceChanged(event.getData());
/* 651 */       break;
/*     */     
/*     */     case 3: 
/* 654 */       refreshInfoCanvas();
/* 655 */       break;
/*     */     
/*     */     case 5: 
/* 658 */       refresh();
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 663 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/file/FileInfoView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */