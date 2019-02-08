/*     */ package com.aelitis.azureus.ui.swt.columns.vuzeactivity;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellAddedListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*     */ 
/*     */ public class ColumnActivityNew
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellSWTPaintListener, TableCellAddedListener, TableCellRefreshListener, TableCellMouseListener
/*     */ {
/*     */   public static final String COLUMN_ID = "activityNew";
/*  48 */   private static int WIDTH = 38;
/*     */   
/*     */ 
/*     */   private static Image imgNew;
/*     */   
/*     */ 
/*     */   private static Image imgOld;
/*     */   
/*     */ 
/*     */   public ColumnActivityNew(String tableID)
/*     */   {
/*  59 */     super("activityNew", tableID);
/*     */     
/*  61 */     initializeAsGraphic(WIDTH);
/*  62 */     setAlignment(3);
/*  63 */     imgNew = ImageLoader.getInstance().getImage("image.activity.unread");
/*  64 */     imgOld = ImageLoader.getInstance().getImage("image.activity.read");
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/*  69 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)cell.getDataSource();
/*     */     
/*  71 */     Rectangle cellBounds = cell.getBounds();
/*  72 */     Image img = entry.getReadOn() <= 0L ? imgNew : imgOld;
/*     */     
/*  74 */     if ((img != null) && (!img.isDisposed())) {
/*  75 */       Rectangle imgBounds = img.getBounds();
/*  76 */       gc.drawImage(img, cellBounds.x + (cellBounds.width - imgBounds.width) / 2, cellBounds.y + (cellBounds.height - imgBounds.height) / 2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/*  84 */     cell.setMarginWidth(0);
/*  85 */     cell.setMarginHeight(0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  90 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)cell.getDataSource();
/*     */     
/*  92 */     boolean isRead = entry.getReadOn() > 0L;
/*  93 */     int sortVal = isRead ? 1 : 0;
/*     */     
/*  95 */     if (cell.setSortValue(sortVal)) {
/*  96 */       cell.invalidate();
/*     */     }
/*     */   }
/*     */   
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 102 */     if ((event.eventType == 0) && (event.button == 1))
/*     */     {
/* 104 */       VuzeActivitiesEntry entry = (VuzeActivitiesEntry)event.cell.getDataSource();
/*     */       
/* 106 */       if (entry.canFlipRead()) {
/* 107 */         entry.setRead(!entry.isRead());
/* 108 */         event.cell.invalidate();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/vuzeactivity/ColumnActivityNew.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */