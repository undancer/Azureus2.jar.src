/*     */ package com.aelitis.azureus.ui.swt.columns.search;
/*     */ 
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.swt.search.SBC_SearchResult;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ColumnSearchResultSite
/*     */   implements TableCellSWTPaintListener, TableCellAddedListener, TableCellRefreshListener
/*     */ {
/*     */   public static final String COLUMN_ID = "site";
/*  48 */   private static int WIDTH = 38;
/*     */   
/*     */   public ColumnSearchResultSite(TableColumn column)
/*     */   {
/*  52 */     column.initialize(3, -2, WIDTH);
/*  53 */     column.addListeners(this);
/*  54 */     column.setRefreshInterval(-3);
/*  55 */     column.setType(2);
/*     */     
/*  57 */     if ((column instanceof TableColumnCore))
/*     */     {
/*  59 */       ((TableColumnCore)column).addCellOtherListener("SWTPaint", this);
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell) {
/*  64 */     SBC_SearchResult entry = (SBC_SearchResult)cell.getDataSource();
/*     */     
/*  66 */     Rectangle cellBounds = cell.getBounds();
/*     */     
/*  68 */     Image img = entry.getIcon();
/*     */     
/*  70 */     if ((img != null) && (!img.isDisposed())) {
/*  71 */       Rectangle imgBounds = img.getBounds();
/*  72 */       if ((cellBounds.width < imgBounds.width) || (cellBounds.height < imgBounds.height)) {
/*  73 */         float dx = cellBounds.width / imgBounds.width;
/*  74 */         float dy = cellBounds.height / imgBounds.height;
/*  75 */         float d = Math.min(dx, dy);
/*  76 */         int newWidth = (int)(imgBounds.width * d);
/*  77 */         int newHeight = (int)(imgBounds.height * d);
/*     */         
/*  79 */         gc.drawImage(img, 0, 0, imgBounds.width, imgBounds.height, cellBounds.x + (cellBounds.width - newWidth) / 2, cellBounds.y + (cellBounds.height - newHeight) / 2, newWidth, newHeight);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*  84 */         gc.drawImage(img, cellBounds.x + (cellBounds.width - imgBounds.width) / 2, cellBounds.y + (cellBounds.height - imgBounds.height) / 2);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/*  92 */     cell.setMarginWidth(0);
/*  93 */     cell.setMarginHeight(0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  97 */     SBC_SearchResult entry = (SBC_SearchResult)cell.getDataSource();
/*     */     
/*  99 */     if (entry != null)
/*     */     {
/* 101 */       long sortVal = entry.getEngine().getId();
/*     */       
/* 103 */       if ((!cell.setSortValue(sortVal)) && (cell.isValid())) {
/* 104 */         return;
/*     */       }
/*     */       
/* 107 */       String name = entry.getEngine().getName();
/*     */       
/* 109 */       Image img = entry.getIcon();
/*     */       
/* 111 */       cell.setText((img == null) || (img.isDisposed()) ? name : null);
/*     */       
/* 113 */       cell.setToolTip(name);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/search/ColumnSearchResultSite.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */