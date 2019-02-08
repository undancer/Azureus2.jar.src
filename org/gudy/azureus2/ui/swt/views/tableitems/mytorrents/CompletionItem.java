/*     */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*     */ import org.gudy.azureus2.plugins.ui.Graphic;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTGraphic;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
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
/*     */ public class CompletionItem
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellAddedListener, TableCellRefreshListener, TableCellDisposeListener, TableCellSWTPaintListener
/*     */ {
/*  55 */   public static final Class DATASOURCE_TYPE = DownloadTypeIncomplete.class;
/*     */   
/*     */   private static final int borderWidth = 1;
/*     */   
/*     */   public static final String COLUMN_ID = "completion";
/*     */   
/*     */   private static Font fontText;
/*     */   
/*  63 */   private Map mapCellLastPercentDone = new HashMap();
/*     */   
/*  65 */   private int marginHeight = -1;
/*     */   
/*     */   Color textColor;
/*     */   
/*     */ 
/*     */   public CompletionItem(String sTableID)
/*     */   {
/*  72 */     this(sTableID, -1);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  76 */     info.addCategories(new String[] { "progress" });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CompletionItem(String sTableID, int marginHeight)
/*     */   {
/*  87 */     super(DATASOURCE_TYPE, "completion", 1, 150, sTableID);
/*  88 */     this.marginHeight = marginHeight;
/*  89 */     initializeAsGraphic(-1, 150);
/*  90 */     setMinWidth(50);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/*  95 */     if (this.marginHeight != -1) {
/*  96 */       cell.setMarginHeight(this.marginHeight);
/*     */     } else {
/*  98 */       cell.setMarginHeight(2);
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell)
/*     */   {
/* 104 */     this.mapCellLastPercentDone.remove(cell);
/* 105 */     Graphic graphic = cell.getGraphic();
/* 106 */     if ((graphic instanceof UISWTGraphic)) {
/* 107 */       Image img = ((UISWTGraphic)graphic).getImage();
/* 108 */       if ((img != null) && (!img.isDisposed())) {
/* 109 */         img.dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 116 */     int percentDone = getPercentDone(cell);
/*     */     
/* 118 */     Integer intObj = (Integer)this.mapCellLastPercentDone.get(cell);
/* 119 */     int lastPercentDone = intObj == null ? 0 : intObj.intValue();
/*     */     
/* 121 */     if ((!cell.setSortValue(percentDone)) && (cell.isValid()) && (lastPercentDone == percentDone)) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cellPaint(GC gcImage, TableCellSWT cell)
/*     */   {
/* 129 */     int percentDone = getPercentDone(cell);
/*     */     
/* 131 */     Rectangle bounds = cell.getBounds();
/*     */     
/* 133 */     int yOfs = (bounds.height - 13) / 2;
/* 134 */     int x1 = bounds.width - 1 - 2;
/* 135 */     int y1 = bounds.height - 3 - yOfs;
/*     */     
/* 137 */     if ((x1 < 10) || (y1 < 3)) {
/* 138 */       return;
/*     */     }
/* 140 */     int textYofs = 0;
/*     */     
/* 142 */     if (y1 >= 28) {
/* 143 */       yOfs = 2;
/* 144 */       y1 = 16;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 150 */     this.mapCellLastPercentDone.put(cell, new Integer(percentDone));
/*     */     
/*     */ 
/* 153 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 154 */     Image imgEnd = imageLoader.getImage("dl_bar_end");
/* 155 */     Image img0 = imageLoader.getImage("dl_bar_0");
/* 156 */     Image img1 = imageLoader.getImage("dl_bar_1");
/*     */     
/*     */ 
/* 159 */     if (!imgEnd.isDisposed()) {
/* 160 */       gcImage.drawImage(imgEnd, bounds.x, bounds.y + yOfs);
/* 161 */       gcImage.drawImage(imgEnd, bounds.x + x1 + 1, bounds.y + yOfs);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 172 */     int limit = x1 * percentDone / 1000;
/*     */     
/* 174 */     if ((!img1.isDisposed()) && (limit > 0)) {
/* 175 */       Rectangle imgBounds = img1.getBounds();
/* 176 */       gcImage.drawImage(img1, 0, 0, imgBounds.width, imgBounds.height, bounds.x + 1, bounds.y + yOfs, limit, imgBounds.height);
/*     */     }
/*     */     
/* 179 */     if ((percentDone < 1000) && (!img0.isDisposed())) {
/* 180 */       Rectangle imgBounds = img0.getBounds();
/* 181 */       gcImage.drawImage(img0, 0, 0, imgBounds.width, imgBounds.height, bounds.x + limit + 1, bounds.y + yOfs, x1 - limit, imgBounds.height);
/*     */     }
/*     */     
/*     */ 
/* 185 */     imageLoader.releaseImage("dl_bar_end");
/* 186 */     imageLoader.releaseImage("dl_bar_0");
/* 187 */     imageLoader.releaseImage("dl_bar_1");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 197 */     if (this.textColor == null) {
/* 198 */       this.textColor = ColorCache.getColor(gcImage.getDevice(), "#005ACF");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 206 */     gcImage.setForeground(this.textColor);
/*     */     
/*     */ 
/* 209 */     String sPercent = DisplayFormatters.formatPercentFromThousands(percentDone);
/* 210 */     GCStringPrinter.printString(gcImage, sPercent, new Rectangle(bounds.x + 4, bounds.y + yOfs, bounds.width - 4, 13), true, false, 16777216);
/*     */   }
/*     */   
/*     */ 
/*     */   private int getPercentDone(TableCell cell)
/*     */   {
/* 216 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 217 */     if (dm == null) {
/* 218 */       return 0;
/*     */     }
/*     */     
/* 221 */     return dm.getStats().getPercentDoneExcludingDND();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/CompletionItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */