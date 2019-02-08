/*     */ package com.aelitis.azureus.ui.swt.columns.subscriptions;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.subscriptions.SBC_SubscriptionResult;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
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
/*     */ public class ColumnSubResultNew
/*     */   implements TableCellSWTPaintListener, TableCellAddedListener, TableCellRefreshListener, TableCellMouseListener
/*     */ {
/*     */   public static final String COLUMN_ID = "new";
/*  44 */   private static int WIDTH = 38;
/*     */   
/*     */   private static Image imgNew;
/*     */   
/*     */   private static Image imgOld;
/*     */   
/*     */ 
/*     */   public ColumnSubResultNew(TableColumn column)
/*     */   {
/*  53 */     column.initialize(3, -2, WIDTH);
/*  54 */     column.addListeners(this);
/*  55 */     column.setRefreshInterval(-1);
/*  56 */     column.setType(2);
/*     */     
/*  58 */     if ((column instanceof TableColumnCore))
/*     */     {
/*  60 */       ((TableColumnCore)column).addCellOtherListener("SWTPaint", this);
/*     */     }
/*     */     
/*  63 */     imgNew = ImageLoader.getInstance().getImage("image.activity.unread");
/*  64 */     imgOld = ImageLoader.getInstance().getImage("image.activity.read");
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell) {
/*  68 */     SBC_SubscriptionResult entry = (SBC_SubscriptionResult)cell.getDataSource();
/*     */     
/*  70 */     Rectangle cellBounds = cell.getBounds();
/*  71 */     Image img = (entry == null) || (entry.getRead()) ? imgOld : imgNew;
/*     */     
/*  73 */     if ((img != null) && (!img.isDisposed())) {
/*  74 */       Rectangle imgBounds = img.getBounds();
/*  75 */       gc.drawImage(img, cellBounds.x + (cellBounds.width - imgBounds.width) / 2, cellBounds.y + (cellBounds.height - imgBounds.height) / 2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/*  82 */     cell.setMarginWidth(0);
/*  83 */     cell.setMarginHeight(0);
/*     */     
/*  85 */     if ((cell instanceof TableCellSWT))
/*     */     {
/*  87 */       ((TableCellSWT)cell).setCursorID(21);
/*     */     }
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  92 */     SBC_SubscriptionResult entry = (SBC_SubscriptionResult)cell.getDataSource();
/*     */     
/*  94 */     if (entry != null)
/*     */     {
/*  96 */       boolean unread = !entry.getRead();
/*     */       
/*  98 */       long sortVal = ((unread ? 2 : 1) << 62) + (SystemTime.getCurrentTime() - entry.getTime()) / 1000L;
/*     */       
/* 100 */       if ((!cell.setSortValue(sortVal)) && (cell.isValid())) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 107 */     if ((event.eventType == 0) && (event.button == 1))
/*     */     {
/* 109 */       SBC_SubscriptionResult entry = (SBC_SubscriptionResult)event.cell.getDataSource();
/*     */       
/* 111 */       if (entry != null)
/*     */       {
/* 113 */         entry.setRead(!entry.getRead());
/*     */         
/* 115 */         event.cell.invalidate();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubResultNew.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */