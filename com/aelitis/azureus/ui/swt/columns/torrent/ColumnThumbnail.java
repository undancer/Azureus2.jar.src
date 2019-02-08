/*     */ package com.aelitis.azureus.ui.swt.columns.torrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnImpl;
/*     */ import com.aelitis.azureus.ui.swt.utils.TorrentUIUtilsV3;
/*     */ import com.aelitis.azureus.ui.swt.utils.TorrentUIUtilsV3.ContentImageLoadedListener;
/*     */ import com.aelitis.azureus.util.DataSourceUtils;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableCellSWTPaintListener;
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
/*     */ public class ColumnThumbnail
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellSWTPaintListener, TableCellToolTipListener
/*     */ {
/*     */   public static final String COLUMN_ID = "Thumbnail";
/*     */   private static final int WIDTH_SMALL = 35;
/*     */   private static final int WIDTH_BIG = 60;
/*     */   private static final int WIDTH_ACTIVITY = 80;
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info)
/*     */   {
/*  61 */     info.addCategories(new String[] { "content" });
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
/*  73 */   private Map mapCellTorrent = new HashMap();
/*     */   
/*     */   public ColumnThumbnail(String sTableID)
/*     */   {
/*  77 */     super("Thumbnail", 3, -2, 0, sTableID);
/*  78 */     if ("Activity.big".equals(sTableID)) {
/*  79 */       initializeAsGraphic(80);
/*     */     } else {
/*  81 */       initializeAsGraphic(sTableID.endsWith(".big") ? 60 : 35);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ColumnThumbnail(TableColumn column)
/*     */   {
/*  89 */     super(null, null);
/*     */     
/*  91 */     column.initialize(3, -2, 60);
/*  92 */     column.addListeners(this);
/*     */     
/*     */ 
/*  95 */     ((TableColumnImpl)column).addCellOtherListener("SWTPaint", this);
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell) {
/*  99 */     this.mapCellTorrent.remove(cell);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 104 */     Object ds = cell.getDataSource();
/* 105 */     TOTorrent newTorrent = DataSourceUtils.getTorrent(ds);
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
/* 118 */     long sortIndex = PlatformTorrentUtils.isContent(newTorrent, true) ? 0L : 1L;
/* 119 */     boolean bChanged = cell.setSortValue(sortIndex);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 124 */     TOTorrent torrent = (TOTorrent)this.mapCellTorrent.get(cell);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 129 */     if ((!cell.isShown()) || ((newTorrent == torrent) && (!bChanged) && (cell.isValid())))
/*     */     {
/* 131 */       return;
/*     */     }
/*     */     
/* 134 */     torrent = newTorrent;
/* 135 */     this.mapCellTorrent.put(cell, torrent);
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, final TableCellSWT cell)
/*     */   {
/* 140 */     Object ds = cell.getDataSource();
/*     */     
/* 142 */     Rectangle cellBounds = cell.getBounds();
/*     */     
/* 144 */     Image[] imgThumbnail = TorrentUIUtilsV3.getContentImage(ds, (cellBounds.width >= 20) && (cellBounds.height >= 20), new TorrentUIUtilsV3.ContentImageLoadedListener()
/*     */     {
/*     */       public void contentImageLoaded(Image image, boolean wasReturned)
/*     */       {
/* 148 */         if (!wasReturned)
/*     */         {
/*     */ 
/* 151 */           cell.invalidate();
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 156 */     if ((imgThumbnail == null) || (imgThumbnail[0] == null))
/*     */     {
/* 158 */       return;
/*     */     }
/*     */     
/* 161 */     if (cellBounds.height > 30) {
/* 162 */       cellBounds.y += 2;
/* 163 */       cellBounds.height -= 4;
/*     */     }
/*     */     
/* 166 */     Rectangle imgBounds = imgThumbnail[0].getBounds();
/*     */     int dstWidth;
/*     */     int dstWidth;
/*     */     int dstHeight;
/* 170 */     if (imgBounds.height > cellBounds.height) {
/* 171 */       int dstHeight = cellBounds.height;
/* 172 */       dstWidth = imgBounds.width * cellBounds.height / imgBounds.height; } else { int dstHeight;
/* 173 */       if (imgBounds.width > cellBounds.width) {
/* 174 */         int dstWidth = cellBounds.width - 4;
/* 175 */         dstHeight = imgBounds.height * cellBounds.width / imgBounds.width;
/*     */       } else {
/* 177 */         dstWidth = imgBounds.width;
/* 178 */         dstHeight = imgBounds.height;
/*     */       }
/*     */     }
/*     */     try {
/* 182 */       gc.setAdvanced(true);
/* 183 */       gc.setInterpolation(2);
/*     */     }
/*     */     catch (Exception e) {}
/* 186 */     int x = cellBounds.x + (cellBounds.width - dstWidth + 1) / 2;
/* 187 */     int y = cellBounds.y + (cellBounds.height - dstHeight + 1) / 2;
/* 188 */     if ((dstWidth > 0) && (dstHeight > 0) && (!imgBounds.isEmpty())) {
/* 189 */       Rectangle dst = new Rectangle(x, y, dstWidth, dstHeight);
/* 190 */       Rectangle lastClipping = gc.getClipping();
/*     */       try {
/* 192 */         Utils.setClipping(gc, cellBounds);
/*     */         
/* 194 */         for (int i = 0; i < imgThumbnail.length; i++) {
/* 195 */           Image image = imgThumbnail[i];
/* 196 */           if (image != null)
/*     */           {
/*     */ 
/* 199 */             Rectangle srcBounds = image.getBounds();
/* 200 */             if (i == 0) {
/* 201 */               int w = dstWidth;
/* 202 */               int h = dstHeight;
/* 203 */               if (imgThumbnail.length > 1) {
/* 204 */                 w = w * 9 / 10;
/* 205 */                 h = h * 9 / 10;
/*     */               }
/* 207 */               gc.drawImage(image, srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height, x, y, w, h);
/*     */             }
/*     */             else {
/* 210 */               int w = dstWidth * 3 / 8;
/* 211 */               int h = dstHeight * 3 / 8;
/* 212 */               gc.drawImage(image, srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height, x + dstWidth - w, y + dstHeight - h, w, h);
/*     */             }
/*     */           }
/*     */         }
/*     */       } catch (Exception e) {
/* 217 */         Debug.out(e);
/*     */       } finally {
/* 219 */         Utils.setClipping(gc, lastClipping);
/*     */       }
/*     */     }
/*     */     
/* 223 */     TorrentUIUtilsV3.releaseContentImage(ds);
/*     */   }
/*     */   
/*     */   public void cellHover(TableCell cell)
/*     */   {
/* 228 */     final Object ds = cell.getDataSource();
/* 229 */     Image[] imgThumbnail = TorrentUIUtilsV3.getContentImage(ds, true, new TorrentUIUtilsV3.ContentImageLoadedListener() {
/*     */       public void contentImageLoaded(Image image, boolean wasReturned) {
/* 231 */         TorrentUIUtilsV3.releaseContentImage(ds);
/*     */       }
/*     */       
/* 234 */     });
/* 235 */     cell.setToolTip(imgThumbnail == null ? null : imgThumbnail[0]);
/*     */   }
/*     */   
/*     */   public void cellHoverComplete(TableCell cell)
/*     */   {
/* 240 */     Object ds = cell.getDataSource();
/* 241 */     TorrentUIUtilsV3.releaseContentImage(ds);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/torrent/ColumnThumbnail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */