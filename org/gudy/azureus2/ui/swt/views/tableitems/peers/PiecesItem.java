/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.Graphic;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTGraphicImpl;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
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
/*     */ public class PiecesItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener, TableCellRefreshListener, TableCellDisposeListener, DiskManagerListener
/*     */ {
/*     */   private static final int INDEX_COLOR_FADEDSTARTS = 10;
/*     */   private static final int borderHorizontalSize = 1;
/*     */   private static final int borderVerticalSize = 1;
/*     */   private static final int borderSplit = 1;
/*     */   private static final int completionHeight = 2;
/*     */   private int row_count;
/*     */   
/*     */   public PiecesItem(String table_id)
/*     */   {
/*  75 */     super("pieces", table_id);
/*  76 */     initializeAsGraphic(-2, 200);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  80 */     info.addCategories(new String[] { "content" });
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/*  86 */     synchronized (this) {
/*  87 */       this.row_count += 1;
/*     */     }
/*  89 */     cell.setFillCell(true);
/*  90 */     Object ds = cell.getDataSource();
/*  91 */     if ((ds instanceof PEPeer)) {
/*  92 */       PEPeer peer = (PEPeer)ds;
/*  93 */       DiskManager diskmanager = peer.getManager().getDiskManager();
/*     */       
/*  95 */       if ((diskmanager.getRemaining() > 0L) && 
/*  96 */         (!diskmanager.hasListener(this))) {
/*  97 */         diskmanager.addListener(this);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell)
/*     */   {
/* 104 */     synchronized (this) {
/* 105 */       this.row_count -= 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 110 */     final List<Image> to_dispose = new ArrayList();
/*     */     
/* 112 */     PEPeer infoObj = (PEPeer)cell.getDataSource();
/*     */     
/* 114 */     if (infoObj != null)
/*     */     {
/* 116 */       Image img = (Image)infoObj.getData("PiecesImage");
/*     */       
/* 118 */       if (img != null)
/*     */       {
/* 120 */         to_dispose.add(img);
/*     */       }
/*     */       
/* 123 */       infoObj.setData("PiecesImageBuffer", null);
/* 124 */       infoObj.setData("PiecesImage", null);
/*     */     }
/*     */     
/* 127 */     Graphic graphic = cell.getGraphic();
/*     */     
/* 129 */     if ((graphic instanceof UISWTGraphic))
/*     */     {
/* 131 */       Image img = ((UISWTGraphic)graphic).getImage();
/*     */       
/* 133 */       if ((img != null) && (to_dispose.contains(img)))
/*     */       {
/* 135 */         to_dispose.add(img);
/*     */       }
/*     */     }
/*     */     
/* 139 */     if (to_dispose.size() > 0) {
/* 140 */       Utils.execSWTThread(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/* 145 */           for (Image img : to_dispose)
/*     */           {
/* 147 */             if (!img.isDisposed())
/*     */             {
/* 149 */               img.dispose();
/*     */             }
/*     */           }
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
/*     */ 
/*     */   public void refresh(final TableCell cell)
/*     */   {
/* 165 */     final PEPeer infoObj = (PEPeer)cell.getDataSource();
/* 166 */     long lCompleted = infoObj == null ? 0L : infoObj.getPercentDoneInThousandNotation();
/*     */     
/*     */ 
/* 169 */     if ((!cell.setSortValue(lCompleted)) && (cell.isValid())) {
/* 170 */       return;
/*     */     }
/*     */     
/* 173 */     if (infoObj == null) {
/* 174 */       return;
/*     */     }
/* 176 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport()
/*     */       {
/* 180 */         if (cell.isDisposed())
/*     */         {
/* 182 */           return;
/*     */         }
/*     */         
/*     */ 
/* 186 */         int newWidth = cell.getWidth();
/* 187 */         if (newWidth <= 0)
/* 188 */           return;
/* 189 */         int newHeight = cell.getHeight();
/*     */         
/* 191 */         int x0 = 1;
/* 192 */         int x1 = newWidth - 1 - 1;
/* 193 */         int y0 = 4;
/* 194 */         int y1 = newHeight - 1 - 1;
/* 195 */         int drawWidth = x1 - x0 + 1;
/* 196 */         if ((drawWidth < 10) || (y1 < 3))
/* 197 */           return;
/* 198 */         int[] imageBuffer = (int[])infoObj.getData("PiecesImageBuffer");
/* 199 */         boolean bImageBufferValid = (imageBuffer != null) && (imageBuffer.length == drawWidth);
/*     */         
/*     */ 
/* 202 */         Image image = (Image)infoObj.getData("PiecesImage");
/*     */         
/*     */         boolean bImageChanged;
/*     */         boolean bImageChanged;
/* 206 */         if ((image == null) || (image.isDisposed())) {
/* 207 */           bImageChanged = true;
/*     */         } else {
/* 209 */           Rectangle imageBounds = image.getBounds();
/* 210 */           bImageChanged = (imageBounds.width != newWidth) || (imageBounds.height != newHeight);
/*     */         }
/*     */         GC gcImage;
/* 213 */         if (bImageChanged) {
/* 214 */           if ((image != null) && (!image.isDisposed())) {
/* 215 */             image.dispose();
/*     */           }
/* 217 */           image = new Image(SWTThread.getInstance().getDisplay(), newWidth, newHeight);
/*     */           
/* 219 */           Rectangle imageBounds = image.getBounds();
/* 220 */           bImageBufferValid = false;
/*     */           
/*     */ 
/* 223 */           GC gcImage = new GC(image);
/* 224 */           gcImage.setForeground(Colors.grey);
/*     */           
/*     */ 
/* 227 */           gcImage.drawRectangle(0, 0, newWidth - 1, newHeight - 1);
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
/* 238 */           gcImage.setForeground(Colors.white);
/* 239 */           gcImage.drawLine(x0, 3, x1, 3);
/*     */         }
/*     */         else
/*     */         {
/* 243 */           gcImage = new GC(image);
/*     */         }
/*     */         
/* 246 */         BitFlags peerHave = infoObj.getAvailable();
/* 247 */         boolean established = ((PEPeerTransport)infoObj).getConnectionState() == 4;
/*     */         
/* 249 */         if ((established) && (peerHave != null) && (peerHave.flags.length > 0)) {
/* 250 */           if ((imageBuffer == null) || (imageBuffer.length != drawWidth)) {
/* 251 */             imageBuffer = new int[drawWidth];
/*     */           }
/* 253 */           boolean[] available = peerHave.flags;
/*     */           try
/*     */           {
/* 256 */             int nbComplete = 0;
/* 257 */             int nbPieces = available.length;
/*     */             
/* 259 */             DiskManager disk_manager = infoObj.getManager().getDiskManager();
/* 260 */             DiskManagerPiece[] pieces = disk_manager == null ? null : disk_manager.getPieces();
/*     */             
/*     */ 
/*     */ 
/* 264 */             int a1 = 0;
/* 265 */             for (int i = 0; i < drawWidth; i++) { int a0;
/* 266 */               if (i == 0)
/*     */               {
/* 268 */                 int a0 = 0;
/* 269 */                 a1 = nbPieces / drawWidth;
/* 270 */                 if (a1 == 0) {
/* 271 */                   a1 = 1;
/*     */                 }
/*     */               } else {
/* 274 */                 a0 = a1;
/* 275 */                 a1 = (i + 1) * nbPieces / drawWidth;
/*     */               }
/*     */               
/*     */ 
/* 279 */               int nbNeeded = 0;
/*     */               int index;
/* 281 */               int index; if (a1 <= a0) {
/* 282 */                 index = imageBuffer[(i - 1)];
/*     */               } else {
/* 284 */                 int nbAvailable = 0;
/* 285 */                 for (int j = a0; j < a1; j++) {
/* 286 */                   if (available[j] != 0) {
/* 287 */                     if ((pieces == null) || (!pieces[j].isDone())) {
/* 288 */                       nbNeeded++;
/*     */                     }
/* 290 */                     nbAvailable++;
/*     */                   }
/*     */                 }
/* 293 */                 nbComplete += nbAvailable;
/* 294 */                 index = nbAvailable * 9 / (a1 - a0);
/* 295 */                 if (nbNeeded <= nbAvailable / 2) {
/* 296 */                   index += 10;
/*     */                 }
/*     */               }
/* 299 */               if (imageBuffer[i] != index) {
/* 300 */                 imageBuffer[i] = index;
/* 301 */                 if (bImageBufferValid) {
/* 302 */                   bImageChanged = true;
/* 303 */                   if (imageBuffer[i] >= 10) {
/* 304 */                     gcImage.setForeground(Colors.faded[(index - 10)]);
/*     */                   }
/*     */                   else
/* 307 */                     gcImage.setForeground(Colors.blues[index]);
/* 308 */                   gcImage.drawLine(i + x0, y0, i + x0, y1);
/*     */                 }
/*     */               }
/*     */             }
/* 312 */             if ((!bImageBufferValid) && 
/* 313 */               (established)) {
/* 314 */               int iLastIndex = imageBuffer[0];
/* 315 */               int iWidth = 1;
/* 316 */               for (int i = 1; i < drawWidth; i++) {
/* 317 */                 if (iLastIndex == imageBuffer[i]) {
/* 318 */                   iWidth++;
/*     */                 } else {
/* 320 */                   if (iLastIndex >= 10) {
/* 321 */                     gcImage.setBackground(Colors.faded[(iLastIndex - 10)]);
/*     */                   }
/*     */                   else
/* 324 */                     gcImage.setBackground(Colors.blues[iLastIndex]);
/* 325 */                   gcImage.fillRectangle(i - iWidth + x0, y0, iWidth, y1 - y0 + 1);
/*     */                   
/* 327 */                   iWidth = 1;
/* 328 */                   iLastIndex = imageBuffer[i];
/*     */                 }
/*     */               }
/* 331 */               if (iLastIndex >= 10) {
/* 332 */                 gcImage.setBackground(Colors.faded[(iLastIndex - 10)]);
/*     */               }
/*     */               else
/* 335 */                 gcImage.setBackground(Colors.blues[iLastIndex]);
/* 336 */               gcImage.fillRectangle(x1 - iWidth + 1, y0, iWidth, y1 - y0 + 1);
/* 337 */               bImageChanged = true;
/*     */             }
/*     */             
/*     */ 
/* 341 */             int limit = drawWidth * nbComplete / nbPieces;
/* 342 */             if (limit < drawWidth) {
/* 343 */               gcImage.setBackground(Colors.blues[0]);
/* 344 */               gcImage.fillRectangle(limit + x0, 1, x1 - limit, 2);
/*     */             }
/*     */             
/* 347 */             gcImage.setBackground(Colors.colorProgressBar);
/* 348 */             gcImage.fillRectangle(x0, 1, limit, 2);
/*     */           }
/*     */           catch (Exception e) {
/* 351 */             System.out.println("Error Drawing PiecesItem");
/* 352 */             Debug.printStackTrace(e);
/*     */           }
/*     */         } else {
/* 355 */           gcImage.setForeground(Colors.grey);
/* 356 */           gcImage.setBackground(Colors.grey);
/* 357 */           gcImage.fillRectangle(x0, y0, newWidth, y1);
/*     */         }
/* 359 */         gcImage.dispose();
/*     */         
/* 361 */         Image oldImage = null;
/* 362 */         Graphic graphic = cell.getGraphic();
/* 363 */         if ((graphic instanceof UISWTGraphic)) {
/* 364 */           oldImage = ((UISWTGraphic)graphic).getImage();
/*     */         }
/* 366 */         if ((bImageChanged) || (image != oldImage) || (!cell.isValid())) {
/* 367 */           if ((cell instanceof TableCellSWT)) {
/* 368 */             ((TableCellSWT)cell).setGraphic(image);
/*     */           } else {
/* 370 */             cell.setGraphic(new UISWTGraphicImpl(image));
/*     */           }
/*     */           
/* 373 */           if ((oldImage != null) && (image != oldImage) && (!oldImage.isDisposed()))
/*     */           {
/* 375 */             oldImage.dispose();
/*     */           }
/*     */           
/* 378 */           if ((bImageChanged) || (image != oldImage)) {
/* 379 */             cell.invalidate();
/*     */           }
/* 381 */           infoObj.setData("PiecesImage", image);
/* 382 */           infoObj.setData("PiecesImageBuffer", imageBuffer);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fileAccessModeChanged(DiskManagerFileInfo file, int old_mode, int new_mode) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void filePriorityChanged(DiskManagerFileInfo file) {}
/*     */   
/*     */ 
/*     */   public void pieceDoneChanged(DiskManagerPiece piece)
/*     */   {
/* 399 */     DiskManager diskmanager = piece.getManager();
/*     */     
/*     */     boolean remove_listener;
/* 402 */     synchronized (this) {
/* 403 */       remove_listener = this.row_count == 0;
/*     */     }
/*     */     
/* 406 */     if (remove_listener) {
/* 407 */       diskmanager.removeListener(this);
/*     */     } else {
/* 409 */       invalidateCells();
/*     */       
/* 411 */       if (diskmanager.getRemaining() == 0L)
/*     */       {
/* 413 */         diskmanager.removeListener(this);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void stateChanged(int oldState, int newState) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/PiecesItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */