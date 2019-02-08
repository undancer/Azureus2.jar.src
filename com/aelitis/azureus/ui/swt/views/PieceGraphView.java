/*     */ package com.aelitis.azureus.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class PieceGraphView
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*  57 */   private boolean onePiecePerBlock = false;
/*     */   
/*  59 */   private int BLOCK_FILLSIZE = 21;
/*     */   
/*     */   private static final int BLOCK_SPACING = 3;
/*     */   
/*  63 */   private int BLOCK_SIZE = this.BLOCK_FILLSIZE + 3;
/*     */   
/*     */   private static final int BLOCKCOLOR_HAVEALL = 0;
/*     */   
/*     */   private static final int BLOCKCOLOR_NOHAVE = 1;
/*     */   
/*     */   private static final int BLOCKCOLOR_UPLOADING = 2;
/*     */   
/*     */   private static final int BLOCKCOLOR_DOWNLOADING = 3;
/*     */   
/*     */   private static final int BLOCKCOLOR_NOAVAIL = 4;
/*     */   
/*     */   private static final int BLOCKCOLOR_HAVESOME = 5;
/*     */   
/*     */   private Color[] blockColors;
/*     */   
/*     */   private Canvas canvas;
/*     */   
/*     */   private Image img;
/*     */   
/*     */   private Image imgHaveAll;
/*     */   
/*     */   private Image imgNoHave;
/*     */   
/*     */   private DownloadManager dlm;
/*     */   
/*     */   private Comparator compFindPEPiece;
/*     */   
/*     */   private final SWTSkinProperties properties;
/*     */   
/*     */   private double[] squareCache;
/*     */   private UISWTView swtView;
/*     */   
/*     */   public PieceGraphView()
/*     */   {
/*  98 */     this.properties = SWTSkinFactory.getInstance().getSkinProperties();
/*     */   }
/*     */   
/*     */ 
/*     */   private void initialize(Composite parent)
/*     */   {
/* 104 */     this.blockColors = new Color[] { this.properties.getColor("color.pieceview.alldone"), this.properties.getColor("color.pieceview.notdone"), this.properties.getColor("color.pieceview.uploading"), this.properties.getColor("color.pieceview.downloading"), this.properties.getColor("color.pieceview.noavail"), this.properties.getColor("color.pieceview.havesome") };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 113 */     this.compFindPEPiece = new Comparator() {
/*     */       public int compare(Object arg0, Object arg1) {
/* 115 */         int arg0no = (arg0 instanceof PEPiece) ? ((PEPiece)arg0).getPieceNumber() : ((Long)arg0).intValue();
/*     */         
/* 117 */         int arg1no = (arg1 instanceof PEPiece) ? ((PEPiece)arg1).getPieceNumber() : ((Long)arg1).intValue();
/*     */         
/* 119 */         return arg0no - arg1no;
/*     */       }
/*     */       
/* 122 */     };
/* 123 */     this.canvas = new Canvas(parent, 262144);
/* 124 */     this.canvas.setLayout(new FillLayout());
/*     */     
/* 126 */     this.canvas.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 128 */         if ((PieceGraphView.this.img != null) && (!PieceGraphView.this.img.isDisposed())) {
/* 129 */           Rectangle bounds = PieceGraphView.this.img.getBounds();
/* 130 */           if ((bounds.width >= e.width) && (bounds.height >= e.height)) {
/* 131 */             e.gc.drawImage(PieceGraphView.this.img, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
/*     */           }
/*     */         }
/*     */         else {
/* 135 */           e.gc.fillRectangle(e.x, e.y, e.width, e.height);
/*     */         }
/*     */         
/*     */       }
/* 139 */     });
/* 140 */     this.canvas.addListener(11, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 142 */         PieceGraphView.this.calcBlockSize();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void dataSourceChanged(Object newDataSource)
/*     */   {
/* 149 */     if ((newDataSource instanceof DownloadManager)) {
/* 150 */       this.dlm = ((DownloadManager)newDataSource);
/*     */     } else {
/* 152 */       this.dlm = null;
/*     */     }
/* 154 */     calcBlockSize();
/*     */   }
/*     */   
/*     */   private void refresh()
/*     */   {
/* 159 */     buildImage();
/*     */   }
/*     */   
/*     */   private void calcBlockSize() {
/* 163 */     if (!this.onePiecePerBlock) {
/* 164 */       buildImage();
/* 165 */       return;
/*     */     }
/* 167 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 169 */         if ((PieceGraphView.this.canvas == null) || (PieceGraphView.this.canvas.isDisposed())) {
/* 170 */           return;
/*     */         }
/* 172 */         TOTorrent torrent = PieceGraphView.this.dlm == null ? null : PieceGraphView.this.dlm.getTorrent();
/* 173 */         if (torrent == null) {
/* 174 */           PieceGraphView.this.BLOCK_SIZE = 24;
/*     */         } else {
/* 176 */           long numPieces = torrent.getNumberOfPieces();
/* 177 */           Rectangle bounds = PieceGraphView.this.canvas.getClientArea();
/* 178 */           PieceGraphView.this.BLOCK_SIZE = ((int)Math.sqrt(bounds.width * bounds.height / numPieces));
/*     */           
/* 180 */           if (PieceGraphView.this.BLOCK_SIZE <= 0) {
/* 181 */             PieceGraphView.this.BLOCK_SIZE = 1;
/*     */           }
/*     */           
/*     */ 
/* 185 */           int numCanFit = bounds.width / PieceGraphView.this.BLOCK_SIZE * (bounds.height / PieceGraphView.this.BLOCK_SIZE);
/*     */           
/* 187 */           if (numCanFit < numPieces) {
/* 188 */             PieceGraphView.access$410(PieceGraphView.this);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 193 */           if (PieceGraphView.this.BLOCK_SIZE < 2) {
/* 194 */             PieceGraphView.this.BLOCK_SIZE = 2;
/*     */           }
/*     */         }
/*     */         
/* 198 */         PieceGraphView.this.BLOCK_FILLSIZE = (PieceGraphView.this.BLOCK_SIZE - 3);
/*     */         
/* 200 */         Utils.disposeSWTObjects(new Object[] { PieceGraphView.this.imgHaveAll, PieceGraphView.this.imgNoHave });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 205 */         PieceGraphView.this.buildImage();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void buildImage() {
/* 211 */     if ((this.canvas == null) || (this.canvas.isDisposed())) {
/* 212 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 217 */     Rectangle bounds = this.canvas.getClientArea();
/* 218 */     if (bounds.isEmpty()) {
/* 219 */       return;
/*     */     }
/*     */     
/* 222 */     if (this.dlm == null) {
/* 223 */       this.canvas.redraw();
/* 224 */       return;
/*     */     }
/*     */     
/* 227 */     PEPeerManager pm = this.dlm.getPeerManager();
/*     */     
/* 229 */     DiskManager dm = this.dlm.getDiskManager();
/*     */     
/* 231 */     if ((pm == null) || (dm == null)) {
/* 232 */       this.canvas.redraw();
/* 233 */       return;
/*     */     }
/*     */     
/* 236 */     DiskManagerPiece[] dm_pieces = dm.getPieces();
/*     */     
/* 238 */     if ((dm_pieces == null) || (dm_pieces.length == 0)) {
/* 239 */       this.canvas.redraw();
/* 240 */       return;
/*     */     }
/*     */     
/* 243 */     int numPieces = dm_pieces.length;
/*     */     
/* 245 */     if ((this.imgHaveAll == null) || (this.imgHaveAll.isDisposed())) {
/* 246 */       this.imgHaveAll = new Image(this.canvas.getDisplay(), this.BLOCK_SIZE, this.BLOCK_SIZE);
/* 247 */       GC gc = new GC(this.imgHaveAll);
/*     */       try {
/*     */         try {
/* 250 */           gc.setAntialias(1);
/*     */         }
/*     */         catch (Exception e) {}
/*     */         
/* 254 */         gc.setBackground(this.canvas.getBackground());
/* 255 */         gc.fillRectangle(this.imgHaveAll.getBounds());
/*     */         
/* 257 */         gc.setBackground(this.blockColors[0]);
/* 258 */         gc.fillRoundRectangle(1, 1, this.BLOCK_FILLSIZE, this.BLOCK_FILLSIZE, this.BLOCK_FILLSIZE, this.BLOCK_FILLSIZE);
/*     */       }
/*     */       finally {
/* 261 */         gc.dispose();
/*     */       }
/*     */     }
/*     */     
/* 265 */     if ((this.imgNoHave == null) || (this.imgNoHave.isDisposed())) {
/* 266 */       this.imgNoHave = new Image(this.canvas.getDisplay(), this.BLOCK_SIZE, this.BLOCK_SIZE);
/* 267 */       GC gc = new GC(this.imgNoHave);
/*     */       try {
/*     */         try {
/* 270 */           gc.setAntialias(1);
/*     */         }
/*     */         catch (Exception e) {}
/*     */         
/* 274 */         gc.setBackground(this.canvas.getBackground());
/* 275 */         gc.fillRectangle(this.imgNoHave.getBounds());
/*     */         
/* 277 */         gc.setBackground(this.blockColors[1]);
/* 278 */         gc.fillRoundRectangle(1, 1, this.BLOCK_FILLSIZE, this.BLOCK_FILLSIZE, this.BLOCK_FILLSIZE, this.BLOCK_FILLSIZE);
/*     */       }
/*     */       finally {
/* 281 */         gc.dispose();
/*     */       }
/*     */     }
/*     */     
/* 285 */     boolean clearImage = (this.img == null) || (this.img.isDisposed()) || (this.img.getBounds().width != bounds.width) || (this.img.getBounds().height != bounds.height);
/*     */     
/*     */ 
/* 288 */     if (clearImage) {
/* 289 */       if ((this.img != null) && (!this.img.isDisposed())) {
/* 290 */         this.img.dispose();
/*     */       }
/*     */       
/* 293 */       this.img = new Image(this.canvas.getDisplay(), bounds.width, bounds.height);
/* 294 */       this.squareCache = null;
/*     */     }
/*     */     
/* 297 */     PEPiece[] currentDLPieces = this.dlm.getCurrentPieces();
/* 298 */     Arrays.sort(currentDLPieces, this.compFindPEPiece);
/*     */     
/*     */ 
/* 301 */     ArrayList currentULPieces = new ArrayList();
/* 302 */     ArrayList futureULPieces = new ArrayList();
/* 303 */     PEPeer[] peers = (PEPeer[])pm.getPeers().toArray(new PEPeer[0]);
/* 304 */     for (int i = 0; i < peers.length; i++) {
/* 305 */       PEPeer peer = peers[i];
/* 306 */       int[] peerRequestedPieces = peer.getIncomingRequestedPieceNumbers();
/* 307 */       if ((peerRequestedPieces != null) && (peerRequestedPieces.length > 0)) {
/* 308 */         currentULPieces.add(new Long(peerRequestedPieces[0]));
/* 309 */         for (int j = 1; j < peerRequestedPieces.length; j++) {
/* 310 */           futureULPieces.add(new Long(peerRequestedPieces[j]));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 315 */       Collections.sort(currentULPieces);
/* 316 */       Collections.sort(futureULPieces);
/*     */     }
/*     */     
/* 319 */     int iNumCols = bounds.width / this.BLOCK_SIZE;
/* 320 */     int iNumRows = bounds.height / this.BLOCK_SIZE;
/* 321 */     int numSquares = this.onePiecePerBlock ? numPieces : iNumCols * iNumRows;
/* 322 */     double numPiecesPerSquare = numPieces / numSquares;
/*     */     
/*     */ 
/* 325 */     if ((this.squareCache == null) || (this.squareCache.length != numSquares)) {
/* 326 */       this.squareCache = new double[numSquares];
/* 327 */       Arrays.fill(this.squareCache, -1.0D);
/*     */     }
/*     */     
/* 330 */     int[] availability = pm.getAvailability();
/*     */     
/* 332 */     int numRedraws = 0;
/*     */     
/* 334 */     GC gc = new GC(this.img);
/*     */     try {
/* 336 */       int iRow = 0;
/* 337 */       if (clearImage) {
/* 338 */         gc.setBackground(this.canvas.getBackground());
/* 339 */         gc.fillRectangle(bounds);
/*     */       }
/*     */       try
/*     */       {
/* 343 */         gc.setAdvanced(true);
/* 344 */         gc.setAntialias(1);
/* 345 */         gc.setInterpolation(2);
/*     */       }
/*     */       catch (Exception e) {}
/*     */       
/* 349 */       int iCol = 0;
/* 350 */       for (int squareNo = 0; squareNo < numSquares; squareNo++) {
/* 351 */         if (iCol >= iNumCols) {
/* 352 */           iCol = 0;
/* 353 */           iRow++;
/*     */         }
/*     */         
/* 356 */         int startNo = (int)(squareNo * numPiecesPerSquare);
/* 357 */         int count = (int)((squareNo + 1) * numPiecesPerSquare) - startNo;
/* 358 */         if (count == 0) {
/* 359 */           count = 1;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 365 */         double pctDone = getPercentDone(startNo, count, dm_pieces);
/*     */         
/*     */ 
/*     */ 
/* 369 */         int iXPos = iCol * this.BLOCK_SIZE;
/* 370 */         int iYPos = iRow * this.BLOCK_SIZE;
/*     */         
/* 372 */         if (pctDone == 1.0D) {
/* 373 */           if (this.squareCache[squareNo] != pctDone) {
/* 374 */             this.squareCache[squareNo] = pctDone;
/* 375 */             gc.drawImage(this.imgHaveAll, iXPos, iYPos);
/* 376 */             if (!clearImage) {
/* 377 */               numRedraws++;
/* 378 */               this.canvas.redraw(iXPos, iYPos, this.BLOCK_SIZE, this.BLOCK_SIZE, false);
/*     */             }
/*     */           }
/* 381 */         } else if (pctDone == 0.0D) {
/* 382 */           if (this.squareCache[squareNo] != pctDone) {
/* 383 */             this.squareCache[squareNo] = pctDone;
/* 384 */             gc.drawImage(this.imgNoHave, iXPos, iYPos);
/* 385 */             if (!clearImage) {
/* 386 */               numRedraws++;
/* 387 */               this.canvas.redraw(iXPos, iYPos, this.BLOCK_SIZE, this.BLOCK_SIZE, false);
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/* 392 */           boolean isDownloading = false;
/* 393 */           for (int i = startNo; i < startNo + count; i++) {
/* 394 */             if (Arrays.binarySearch(currentDLPieces, new Long(i), this.compFindPEPiece) >= 0)
/*     */             {
/* 396 */               isDownloading = true;
/* 397 */               break;
/*     */             }
/*     */           }
/*     */           
/* 401 */           double val = pctDone + (isDownloading ? 0 : 1);
/* 402 */           if (this.squareCache[squareNo] != val) {
/* 403 */             this.squareCache[squareNo] = val;
/* 404 */             gc.drawImage(this.imgNoHave, iXPos, iYPos);
/*     */             
/* 406 */             int size = (int)(this.BLOCK_FILLSIZE * pctDone);
/* 407 */             if (size == 0) {
/* 408 */               size = 1;
/*     */             }
/* 410 */             int q = (int)((this.BLOCK_FILLSIZE - size) / 2.0D + 0.5D) + 1;
/*     */             
/* 412 */             int colorIndex = isDownloading ? 3 : 5;
/*     */             
/* 414 */             gc.setBackground(this.blockColors[colorIndex]);
/* 415 */             gc.fillOval(iXPos + q, iYPos + q, size, size);
/*     */             
/* 417 */             if (!clearImage) {
/* 418 */               numRedraws++;
/* 419 */               this.canvas.redraw(iXPos, iYPos, this.BLOCK_SIZE, this.BLOCK_SIZE, false);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 424 */         for (int i = startNo; i < startNo + count; i++) {
/* 425 */           if (Collections.binarySearch(currentULPieces, new Long(i)) >= 0) {
/* 426 */             int colorIndex = 2;
/* 427 */             int size = this.BLOCK_FILLSIZE + 1;
/*     */             
/* 429 */             gc.setForeground(this.blockColors[colorIndex]);
/* 430 */             gc.drawRoundRectangle(iXPos, iYPos, size, size, size, size);
/* 431 */             if (!clearImage) {
/* 432 */               numRedraws++;
/* 433 */               this.canvas.redraw(iXPos, iYPos, this.BLOCK_SIZE, this.BLOCK_SIZE, false);
/*     */             }
/* 435 */             this.squareCache[squareNo] = -1.0D;
/* 436 */             break; }
/* 437 */           if (Collections.binarySearch(futureULPieces, new Long(i)) >= 0) {
/* 438 */             int colorIndex = 2;
/* 439 */             int size = this.BLOCK_FILLSIZE + 1;
/*     */             
/* 441 */             gc.setForeground(this.blockColors[colorIndex]);
/* 442 */             gc.setLineStyle(3);
/* 443 */             gc.drawRoundRectangle(iXPos, iYPos, size, size, size, size);
/* 444 */             if (!clearImage) {
/* 445 */               numRedraws++;
/* 446 */               this.canvas.redraw(iXPos, iYPos, this.BLOCK_SIZE, this.BLOCK_SIZE, false);
/*     */             }
/* 448 */             gc.setLineStyle(1);
/* 449 */             this.squareCache[squareNo] = -1.0D;
/* 450 */             break;
/*     */           }
/*     */         }
/*     */         
/* 454 */         if (availability != null) {
/* 455 */           boolean hasNoAvail = false;
/* 456 */           for (int i = startNo; i < startNo + count; i++) {
/* 457 */             if (availability[i] == 0) {
/* 458 */               hasNoAvail = true;
/* 459 */               this.squareCache[squareNo] = -1.0D;
/* 460 */               break;
/*     */             }
/*     */           }
/*     */           
/* 464 */           if (hasNoAvail) {
/* 465 */             gc.setForeground(this.blockColors[4]);
/* 466 */             gc.drawRectangle(iXPos, iYPos, this.BLOCK_FILLSIZE + 1, this.BLOCK_FILLSIZE + 1);
/*     */             
/* 468 */             if (!clearImage) {
/* 469 */               numRedraws++;
/* 470 */               this.canvas.redraw(iXPos, iYPos, this.BLOCK_SIZE, this.BLOCK_SIZE, false);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 475 */         iCol++;
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 479 */       Debug.out(e);
/*     */     } finally {
/* 481 */       gc.dispose();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   private double getPercentDone(int startNo, int count, DiskManagerPiece[] dm_pieces)
/*     */   {
/* 497 */     int totalComplete = 0;
/* 498 */     int totalBlocks = 0;
/* 499 */     for (int i = startNo; i < startNo + count; i++) {
/* 500 */       DiskManagerPiece piece = dm_pieces[i];
/* 501 */       int numBlocks = piece.getNbBlocks();
/* 502 */       totalBlocks += numBlocks;
/*     */       
/* 504 */       if (piece.isDone()) {
/* 505 */         totalComplete += numBlocks;
/*     */       }
/*     */       else {
/* 508 */         totalComplete += piece.getNbWritten();
/*     */       }
/*     */     }
/* 511 */     return totalComplete / totalBlocks;
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 515 */     switch (event.getType()) {
/*     */     case 0: 
/* 517 */       this.swtView = ((UISWTView)event.getData());
/* 518 */       break;
/*     */     
/*     */     case 7: 
/* 521 */       delete();
/* 522 */       break;
/*     */     
/*     */     case 2: 
/* 525 */       initialize((Composite)event.getData());
/* 526 */       break;
/*     */     
/*     */     case 6: 
/* 529 */       Messages.updateLanguageForControl(this.canvas);
/* 530 */       break;
/*     */     
/*     */     case 1: 
/* 533 */       dataSourceChanged(event.getData());
/* 534 */       break;
/*     */     
/*     */ 
/*     */     case 5: 
/* 538 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 542 */     return true;
/*     */   }
/*     */   
/*     */   private void delete() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/PieceGraphView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */