/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.files;
/*     */ 
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.ui.Graphic;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellLightRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellVisibilityListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*     */ public class ProgressGraphItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener, TableCellDisposeListener, TableCellVisibilityListener
/*     */ {
/*     */   private static final int borderWidth = 1;
/*     */   
/*     */   public ProgressGraphItem()
/*     */   {
/*  52 */     super("pieces", "Files");
/*  53 */     initializeAsGraphic(-2, 200);
/*  54 */     setMinWidth(100);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  58 */     info.addCategories(new String[] { "progress" });
/*     */     
/*     */ 
/*  61 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell) {
/*  65 */     new Cell(cell);
/*     */   }
/*     */   
/*     */   public void cellVisibilityChanged(TableCell cell, int visibility) {
/*  69 */     if (visibility == 1) {
/*  70 */       dispose(cell);
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell) {
/*  75 */     Graphic graphic = cell.getGraphic();
/*  76 */     if ((graphic instanceof UISWTGraphic))
/*     */     {
/*  78 */       Image img = ((UISWTGraphic)graphic).getImage();
/*  79 */       if ((img != null) && (!img.isDisposed())) {
/*  80 */         img.dispose();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*  85 */         ((UISWTGraphic)graphic).setImage(null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private class Cell implements TableCellLightRefreshListener
/*     */   {
/*  92 */     int lastPercentDone = 0;
/*  93 */     private long last_draw_time = SystemTime.getCurrentTime();
/*  94 */     private boolean bNoRed = false;
/*  95 */     private boolean was_running = false;
/*     */     
/*     */     public Cell(TableCell cell) {
/*  98 */       cell.setFillCell(true);
/*  99 */       cell.addListeners(this);
/*     */     }
/*     */     
/*     */     public void refresh(TableCell cell) {
/* 103 */       refresh(cell, false);
/*     */     }
/*     */     
/*     */     public void refresh(TableCell cell, boolean sortOnly) {
/* 107 */       DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)cell.getDataSource();
/* 108 */       DiskManager manager = fileInfo == null ? null : fileInfo.getDiskManager();
/*     */       
/* 110 */       int percentDone = 0;
/*     */       int sortOrder;
/* 112 */       int sortOrder; if (manager == null) {
/* 113 */         sortOrder = -1;
/*     */       } else {
/* 115 */         if ((fileInfo != null) && (fileInfo.getLength() != 0L)) {
/* 116 */           percentDone = (int)(1000L * fileInfo.getDownloaded() / fileInfo.getLength());
/*     */         }
/* 118 */         sortOrder = percentDone;
/*     */       }
/* 120 */       cell.setSortValue(sortOrder);
/* 121 */       if (sortOnly)
/*     */       {
/* 123 */         ProgressGraphItem.this.dispose(cell);
/* 124 */         return;
/*     */       }
/*     */       
/*     */ 
/* 128 */       int newWidth = cell.getWidth();
/* 129 */       if (newWidth <= 0)
/* 130 */         return;
/* 131 */       int newHeight = cell.getHeight();
/* 132 */       int x1 = newWidth - 1 - 1;
/* 133 */       int y1 = newHeight - 1 - 1;
/*     */       
/* 135 */       if ((x1 < 10) || (y1 < 3)) {
/* 136 */         return;
/*     */       }
/*     */       
/*     */ 
/* 140 */       boolean running = manager != null;
/* 141 */       boolean hasGraphic = false;
/* 142 */       Graphic graphic = cell.getGraphic();
/* 143 */       if ((graphic instanceof UISWTGraphic)) {
/* 144 */         Image img = ((UISWTGraphic)graphic).getImage();
/* 145 */         hasGraphic = (img != null) && (!img.isDisposed());
/*     */       }
/* 147 */       boolean bImageBufferValid = (this.lastPercentDone == percentDone) && (cell.isValid()) && (this.bNoRed) && (running == this.was_running) && (hasGraphic);
/*     */       
/*     */ 
/* 150 */       if (bImageBufferValid) {
/* 151 */         return;
/*     */       }
/* 153 */       this.was_running = running;
/* 154 */       this.lastPercentDone = percentDone;
/* 155 */       Image piecesImage = null;
/*     */       
/* 157 */       if ((graphic instanceof UISWTGraphic))
/* 158 */         piecesImage = ((UISWTGraphic)graphic).getImage();
/* 159 */       if ((piecesImage != null) && (!piecesImage.isDisposed())) {
/* 160 */         piecesImage.dispose();
/*     */       }
/* 162 */       if (!running) {
/* 163 */         cell.setGraphic(null);
/* 164 */         return;
/*     */       }
/*     */       
/* 167 */       piecesImage = new Image(SWTThread.getInstance().getDisplay(), newWidth, newHeight);
/* 168 */       GC gcImage = new GC(piecesImage);
/*     */       
/*     */ 
/* 171 */       DownloadManager download_manager = fileInfo == null ? null : fileInfo.getDownloadManager();
/* 172 */       PEPeerManager peer_manager = download_manager == null ? null : download_manager.getPeerManager();
/* 173 */       PEPiece[] pe_pieces = peer_manager == null ? null : peer_manager.getPieces();
/* 174 */       long now = SystemTime.getCurrentTime();
/*     */       
/* 176 */       if ((fileInfo != null) && (manager != null))
/*     */       {
/* 178 */         if (percentDone == 1000)
/*     */         {
/* 180 */           gcImage.setForeground(Colors.blues[9]);
/* 181 */           gcImage.setBackground(Colors.blues[9]);
/* 182 */           gcImage.fillRectangle(1, 1, newWidth - 2, newHeight - 2);
/*     */         }
/*     */         else {
/* 185 */           int firstPiece = fileInfo.getFirstPieceNumber();
/* 186 */           int nbPieces = fileInfo.getNbPieces();
/* 187 */           DiskManagerPiece[] dm_pieces = manager.getPieces();
/* 188 */           this.bNoRed = true;
/* 189 */           for (int i = 0; i < newWidth; i++)
/*     */           {
/* 191 */             int a0 = i * nbPieces / newWidth;
/* 192 */             int a1 = (i + 1) * nbPieces / newWidth;
/* 193 */             if (a1 == a0)
/* 194 */               a1++;
/* 195 */             if ((a1 > nbPieces) && (nbPieces != 0))
/* 196 */               a1 = nbPieces;
/* 197 */             int nbAvailable = 0;
/* 198 */             boolean written = false;
/* 199 */             boolean partially_written = false;
/* 200 */             if (firstPiece >= 0)
/* 201 */               for (int j = a0; j < a1; j++)
/*     */               {
/* 203 */                 int this_index = j + firstPiece;
/* 204 */                 DiskManagerPiece dm_piece = dm_pieces[this_index];
/* 205 */                 if (dm_piece.isDone())
/* 206 */                   nbAvailable++;
/* 207 */                 if (!written)
/*     */                 {
/* 209 */                   if (pe_pieces != null)
/*     */                   {
/* 211 */                     PEPiece pe_piece = pe_pieces[this_index];
/* 212 */                     if (pe_piece != null)
/* 213 */                       written = (written) || (pe_piece.getLastDownloadTime(now) + 500L > this.last_draw_time);
/*     */                   }
/* 215 */                   if ((!written) && (!partially_written))
/*     */                   {
/* 217 */                     boolean[] blocks = dm_piece.getWritten();
/* 218 */                     if (blocks != null)
/* 219 */                       for (int k = 0; k < blocks.length; k++)
/* 220 */                         if (blocks[k] != 0)
/*     */                         {
/* 222 */                           partially_written = true;
/* 223 */                           break;
/*     */                         }
/*     */                   }
/*     */                 }
/*     */               } else
/* 228 */               nbAvailable = 1;
/* 229 */             gcImage.setBackground(partially_written ? Colors.grey : written ? Colors.red : Colors.blues[(nbAvailable * 9 / (a1 - a0))]);
/* 230 */             gcImage.fillRectangle(i, 1, 1, newHeight - 2);
/* 231 */             if (written)
/* 232 */               this.bNoRed = false;
/*     */           }
/* 234 */           gcImage.setForeground(Colors.grey);
/*     */         }
/*     */       } else {
/* 237 */         gcImage.setForeground(Colors.grey);
/*     */       }
/* 239 */       if (manager != null)
/* 240 */         gcImage.drawRectangle(0, 0, newWidth - 1, newHeight - 1);
/* 241 */       gcImage.dispose();
/*     */       
/* 243 */       this.last_draw_time = now;
/*     */       
/* 245 */       if ((cell instanceof TableCellSWT)) {
/* 246 */         ((TableCellSWT)cell).setGraphic(piecesImage);
/*     */       } else {
/* 248 */         cell.setGraphic(new UISWTGraphicImpl(piecesImage));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/files/ProgressGraphItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */