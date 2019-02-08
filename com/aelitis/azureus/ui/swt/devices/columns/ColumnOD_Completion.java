/*     */ package com.aelitis.azureus.ui.swt.devices.columns;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.DeviceOfflineDownload;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnImpl;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellDisposeListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnExtraInfoListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
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
/*     */ public class ColumnOD_Completion
/*     */   implements TableCellAddedListener, TableCellRefreshListener, TableCellDisposeListener, TableCellSWTPaintListener, TableColumnExtraInfoListener
/*     */ {
/*     */   private static final int borderWidth = 1;
/*     */   public static final String COLUMN_ID = "od_completion";
/*     */   private static Font fontText;
/*  57 */   private Map<TableCell, Integer> mapCellLastPercentDone = new HashMap();
/*     */   
/*  59 */   private int marginHeight = -1;
/*     */   
/*     */   Color textColor;
/*     */   
/*     */   public ColumnOD_Completion(TableColumn column)
/*     */   {
/*  65 */     column.initialize(1, -2, 145);
/*  66 */     column.addListeners(this);
/*     */     
/*     */ 
/*  69 */     ((TableColumnImpl)column).addCellOtherListener("SWTPaint", this);
/*  70 */     column.setType(2);
/*  71 */     column.setRefreshInterval(-1);
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  75 */     info.addCategories(new String[] { "essential" });
/*     */     
/*     */ 
/*  78 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/*  83 */     if (this.marginHeight != -1) {
/*  84 */       cell.setMarginHeight(this.marginHeight);
/*     */     } else {
/*  86 */       cell.setMarginHeight(2);
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispose(TableCell cell)
/*     */   {
/*  92 */     this.mapCellLastPercentDone.remove(cell);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  97 */     DeviceOfflineDownload od = (DeviceOfflineDownload)cell.getDataSource();
/*     */     
/*  99 */     int percentDone = getPerThouDone(od);
/*     */     
/* 101 */     Integer intObj = (Integer)this.mapCellLastPercentDone.get(cell);
/* 102 */     int lastPercentDone = intObj == null ? 0 : intObj.intValue();
/*     */     
/* 104 */     if ((!cell.setSortValue(percentDone)) && (cell.isValid()) && (lastPercentDone == percentDone)) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cellPaint(GC gcImage, TableCellSWT cell)
/*     */   {
/* 112 */     DeviceOfflineDownload od = (DeviceOfflineDownload)cell.getDataSource();
/*     */     
/* 114 */     Rectangle bounds = cell.getBounds();
/*     */     
/* 116 */     int yOfs = (bounds.height - 13) / 2;
/* 117 */     int x1 = bounds.width - 1 - 2;
/* 118 */     int y1 = bounds.height - 3 - yOfs;
/*     */     
/* 120 */     if ((x1 < 10) || (y1 < 3)) {
/* 121 */       return;
/*     */     }
/*     */     
/* 124 */     if (!od.isTransfering())
/*     */     {
/* 126 */       gcImage.fillRectangle(bounds);
/*     */       
/* 128 */       return;
/*     */     }
/*     */     
/* 131 */     int percentDone = getPerThouDone(od);
/*     */     
/* 133 */     this.mapCellLastPercentDone.put(cell, new Integer(percentDone));
/*     */     
/* 135 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 136 */     Image imgEnd = imageLoader.getImage("tc_bar_end");
/* 137 */     Image img0 = imageLoader.getImage("tc_bar_0");
/* 138 */     Image img1 = imageLoader.getImage("tc_bar_1");
/*     */     
/*     */ 
/* 141 */     if (!imgEnd.isDisposed()) {
/* 142 */       gcImage.drawImage(imgEnd, bounds.x, bounds.y + yOfs);
/* 143 */       gcImage.drawImage(imgEnd, bounds.x + x1 + 1, bounds.y + yOfs);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 148 */     int limit = x1 * percentDone / 1000;
/*     */     
/* 150 */     if ((!img1.isDisposed()) && (limit > 0)) {
/* 151 */       Rectangle imgBounds = img1.getBounds();
/* 152 */       gcImage.drawImage(img1, 0, 0, imgBounds.width, imgBounds.height, bounds.x + 1, bounds.y + yOfs, limit, imgBounds.height);
/*     */     }
/*     */     
/* 155 */     if ((percentDone < 1000) && (!img0.isDisposed())) {
/* 156 */       Rectangle imgBounds = img0.getBounds();
/* 157 */       gcImage.drawImage(img0, 0, 0, imgBounds.width, imgBounds.height, bounds.x + limit + 1, bounds.y + yOfs, x1 - limit, imgBounds.height);
/*     */     }
/*     */     
/*     */ 
/* 161 */     imageLoader.releaseImage("tc_bar_end");
/* 162 */     imageLoader.releaseImage("tc_bar_0");
/* 163 */     imageLoader.releaseImage("tc_bar_1");
/*     */     
/* 165 */     if (this.textColor == null) {
/* 166 */       this.textColor = ColorCache.getColor(gcImage.getDevice(), "#006600");
/*     */     }
/*     */     
/* 169 */     gcImage.setForeground(this.textColor);
/*     */     
/* 171 */     if (fontText == null) {
/* 172 */       fontText = FontUtils.getFontWithHeight(gcImage.getFont(), gcImage, 10);
/*     */     }
/*     */     
/* 175 */     gcImage.setFont(fontText);
/*     */     
/* 177 */     String sText = DisplayFormatters.formatPercentFromThousands(percentDone);
/*     */     
/* 179 */     GCStringPrinter.printString(gcImage, sText, new Rectangle(bounds.x + 4, bounds.y + yOfs, bounds.width - 4, 13), true, false, 16777216);
/*     */   }
/*     */   
/*     */ 
/*     */   private int getPerThouDone(DeviceOfflineDownload od)
/*     */   {
/* 185 */     if (od == null) {
/* 186 */       return 0;
/*     */     }
/* 188 */     long total = od.getCurrentTransferSize();
/* 189 */     long rem = od.getRemaining();
/*     */     
/* 191 */     if ((total == 0L) || (total < rem))
/*     */     {
/* 193 */       return 0;
/*     */     }
/*     */     
/* 196 */     if (rem == 0L)
/*     */     {
/* 198 */       return 1000;
/*     */     }
/*     */     
/* 201 */     return (int)(1000L * (total - rem) / total);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/columns/ColumnOD_Completion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */