/*     */ package com.aelitis.azureus.ui.swt.columns.searchsubs;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.utils.SearchSubsResultBase;
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
/*     */ public class ColumnSearchSubResultType
/*     */   implements TableCellSWTPaintListener, TableCellAddedListener, TableCellRefreshListener
/*     */ {
/*     */   public static final String COLUMN_ID = "type";
/*  42 */   private static int WIDTH = 45;
/*     */   
/*     */   private static Image imgVideo;
/*     */   
/*     */   private static Image imgAudio;
/*     */   private static Image imgGame;
/*     */   private static Image imgOther;
/*     */   
/*     */   public ColumnSearchSubResultType(TableColumn column)
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
/*     */     
/*  62 */     imgVideo = ImageLoader.getInstance().getImage("column.image.ct_video");
/*  63 */     imgAudio = ImageLoader.getInstance().getImage("column.image.ct_audio");
/*  64 */     imgGame = ImageLoader.getInstance().getImage("column.image.ct_game");
/*  65 */     imgOther = ImageLoader.getInstance().getImage("column.image.ct_other");
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell) {
/*  69 */     SearchSubsResultBase entry = (SearchSubsResultBase)cell.getDataSource();
/*     */     
/*  71 */     Rectangle cellBounds = cell.getBounds();
/*     */     Image img;
/*  73 */     Image img; if (entry == null) {
/*  74 */       img = imgOther;
/*     */     } else {
/*  76 */       int ct = entry.getContentType();
/*  77 */       switch (ct) {
/*     */       case 0: 
/*  79 */         img = imgOther;
/*  80 */         break;
/*     */       
/*     */       case 1: 
/*  83 */         img = imgVideo;
/*  84 */         break;
/*     */       
/*     */       case 2: 
/*  87 */         img = imgAudio;
/*  88 */         break;
/*     */       
/*     */       case 3: 
/*  91 */         img = imgGame;
/*  92 */         break;
/*     */       
/*     */       default: 
/*  95 */         img = imgOther;
/*     */       }
/*     */       
/*     */     }
/*     */     
/*     */ 
/* 101 */     if ((img != null) && (!img.isDisposed())) {
/* 102 */       Rectangle imgBounds = img.getBounds();
/* 103 */       gc.drawImage(img, cellBounds.x + (cellBounds.width - imgBounds.width) / 2, cellBounds.y + (cellBounds.height - imgBounds.height) / 2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/* 110 */     cell.setMarginWidth(0);
/* 111 */     cell.setMarginHeight(0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/* 115 */     SearchSubsResultBase entry = (SearchSubsResultBase)cell.getDataSource();
/*     */     
/* 117 */     if (entry != null)
/*     */     {
/* 119 */       if ((!cell.setSortValue(entry.getContentType())) && (cell.isValid())) {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/searchsubs/ColumnSearchSubResultType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */