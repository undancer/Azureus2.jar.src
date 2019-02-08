/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.pieces;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManager;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerFactory;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerStats;
/*     */ import java.util.Arrays;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
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
/*     */ public class BlocksItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener, TableCellRefreshListener, TableCellDisposeListener
/*     */ {
/*     */   private static final int COLOR_REQUESTED = 0;
/*     */   private static final int COLOR_WRITTEN = 1;
/*     */   private static final int COLOR_DOWNLOADED = 2;
/*     */   private static final int COLOR_INCACHE = 3;
/*  63 */   public static final Color[] colors = { Colors.blues[2], Colors.blues[9], Colors.red, Colors.grey };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  70 */   private static CacheFileManagerStats cacheStats = null;
/*     */   
/*     */   public BlocksItem()
/*     */   {
/*  74 */     super("blocks", "Pieces");
/*  75 */     initializeAsGraphic(-2, 200);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  79 */     info.addCategories(new String[] { "progress" });
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/*  85 */     if (cacheStats == null) {
/*     */       try {
/*  87 */         cacheStats = CacheFileManagerFactory.getSingleton().getStats();
/*     */       } catch (Exception e) {
/*  89 */         e.printStackTrace();
/*     */       }
/*     */     }
/*  92 */     cell.setFillCell(true);
/*     */   }
/*     */   
/*     */   public void dispose(final TableCell cell) {
/*  96 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/*  99 */         Image img = ((TableCellSWT)cell).getGraphicSWT();
/* 100 */         if ((img != null) && (!img.isDisposed())) {
/* 101 */           img.dispose();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void refresh(final TableCell cell) {
/* 108 */     final PEPiece pePiece = (PEPiece)cell.getDataSource();
/* 109 */     if (pePiece == null) {
/* 110 */       cell.setSortValue(0L);
/* 111 */       dispose(cell);
/* 112 */       cell.setGraphic(null);
/* 113 */       return;
/*     */     }
/*     */     
/* 116 */     cell.setSortValue(pePiece.getNbWritten());
/*     */     
/* 118 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport()
/*     */       {
/* 122 */         long lNumBlocks = pePiece.getNbBlocks();
/*     */         
/* 124 */         int newWidth = cell.getWidth();
/* 125 */         if (newWidth <= 0) {
/* 126 */           BlocksItem.this.dispose(cell);
/* 127 */           cell.setGraphic(null);
/* 128 */           return;
/*     */         }
/* 130 */         int newHeight = cell.getHeight();
/*     */         
/* 132 */         int x1 = newWidth - 2;
/* 133 */         int y1 = newHeight - 3;
/* 134 */         if ((x1 < 10) || (y1 < 3)) {
/* 135 */           BlocksItem.this.dispose(cell);
/* 136 */           cell.setGraphic(null);
/* 137 */           return;
/*     */         }
/* 139 */         Image image = new Image(SWTThread.getInstance().getDisplay(), newWidth, newHeight);
/*     */         
/*     */ 
/* 142 */         GC gcImage = new GC(image);
/* 143 */         gcImage.setForeground(Colors.grey);
/* 144 */         gcImage.drawRectangle(0, 0, x1 + 1, y1 + 1);
/* 145 */         int blocksPerPixel = 0;
/* 146 */         int iPixelsPerBlock = 0;
/* 147 */         int pxRes = 0;
/* 148 */         long pxBlockStep = 0L;
/* 149 */         int factor = 4;
/*     */         
/* 151 */         while (iPixelsPerBlock <= 0) {
/* 152 */           blocksPerPixel++;
/* 153 */           iPixelsPerBlock = (int)((x1 + 1) / (lNumBlocks / blocksPerPixel));
/*     */         }
/*     */         
/* 156 */         pxRes = (int)(x1 - lNumBlocks / blocksPerPixel * iPixelsPerBlock);
/* 157 */         if (pxRes <= 0)
/* 158 */           pxRes = 1;
/* 159 */         pxBlockStep = lNumBlocks * factor / pxRes;
/* 160 */         long addBlocks = lNumBlocks * factor / pxBlockStep;
/* 161 */         if (addBlocks * iPixelsPerBlock > pxRes) {
/* 162 */           pxBlockStep += 1L;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 168 */         TOTorrent torrent = pePiece.getManager().getDiskManager().getTorrent();
/*     */         
/* 170 */         boolean[] written = pePiece.getDMPiece().getWritten();
/* 171 */         boolean piece_written = pePiece.isWritten();
/* 172 */         int drawnWidth = 0;
/* 173 */         int blockStep = 0;
/*     */         
/* 175 */         int pieceNumber = pePiece.getPieceNumber();
/* 176 */         long[] offsets = new long[(int)lNumBlocks];
/* 177 */         long[] lengths = (long[])offsets.clone();
/* 178 */         Arrays.fill(offsets, pePiece.getManager().getDiskManager().getPieceLength() * pieceNumber);
/*     */         
/*     */ 
/* 181 */         for (int i = 0; i < lNumBlocks;) { lengths[i] = pePiece.getBlockSize(i);offsets[i] += 16384 * i;
/* 182 */           i++;
/*     */         }
/*     */         
/* 185 */         boolean[] isCached = BlocksItem.cacheStats == null ? new boolean[(int)lNumBlocks] : BlocksItem.cacheStats.getBytesInCache(torrent, offsets, lengths);
/*     */         
/*     */ 
/* 188 */         for (int i = 0; i < lNumBlocks; i += blocksPerPixel) {
/* 189 */           int nextWidth = iPixelsPerBlock;
/*     */           
/* 191 */           blockStep += blocksPerPixel * factor;
/* 192 */           if (blockStep >= pxBlockStep) {
/* 193 */             nextWidth += (int)(blockStep / pxBlockStep);
/* 194 */             blockStep = (int)(blockStep - pxBlockStep);
/*     */           }
/*     */           
/* 197 */           if (i >= lNumBlocks - blocksPerPixel) {
/* 198 */             nextWidth = x1 - drawnWidth;
/*     */           }
/* 200 */           Color color = Colors.white;
/*     */           
/* 202 */           if (((written == null) && (piece_written)) || ((written != null) && (written[i] != 0)))
/*     */           {
/*     */ 
/* 205 */             color = BlocksItem.colors[1];
/*     */           }
/* 207 */           else if (pePiece.isDownloaded(i))
/*     */           {
/* 209 */             color = BlocksItem.colors[2];
/*     */           }
/* 211 */           else if (pePiece.isRequested(i))
/*     */           {
/* 213 */             color = BlocksItem.colors[0];
/*     */           }
/*     */           
/* 216 */           gcImage.setBackground(color);
/* 217 */           gcImage.fillRectangle(drawnWidth + 1, 1, nextWidth, y1);
/*     */           
/* 219 */           if (isCached[i] != 0) {
/* 220 */             gcImage.setBackground(BlocksItem.colors[3]);
/* 221 */             gcImage.fillRectangle(drawnWidth + 1, 1, nextWidth, 3);
/*     */           }
/*     */           
/*     */ 
/* 225 */           drawnWidth += nextWidth;
/*     */         }
/*     */         
/* 228 */         gcImage.dispose();
/*     */         
/* 230 */         Image oldImage = null;
/* 231 */         Graphic graphic = cell.getGraphic();
/* 232 */         if ((graphic instanceof UISWTGraphic)) {
/* 233 */           oldImage = ((UISWTGraphic)graphic).getImage();
/*     */         }
/*     */         
/* 236 */         if ((cell instanceof TableCellSWT)) {
/* 237 */           ((TableCellSWT)cell).setGraphic(image);
/*     */         } else {
/* 239 */           cell.setGraphic(new UISWTGraphicImpl(image));
/*     */         }
/* 241 */         if ((oldImage != null) && (!oldImage.isDisposed())) {
/* 242 */           oldImage.dispose();
/*     */         }
/* 244 */         gcImage.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/pieces/BlocksItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */