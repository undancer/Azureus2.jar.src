/*     */ package com.aelitis.azureus.ui.swt.columns.vuzeactivity;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader.ImageDownloaderListener;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ColumnActivityType
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellSWTPaintListener, TableCellRefreshListener
/*     */ {
/*     */   public static final String COLUMN_ID = "activityType";
/*  52 */   private static int WIDTH = 42;
/*     */   
/*  54 */   private static SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss a, EEEE, MMMM d, yyyy");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnActivityType(String tableID)
/*     */   {
/*  65 */     super("activityType", tableID);
/*     */     
/*  67 */     initializeAsGraphic(WIDTH);
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, final TableCellSWT cell)
/*     */   {
/*  72 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)cell.getDataSource();
/*     */     
/*  74 */     Image imgIcon = null;
/*  75 */     String iconID = entry.getIconID();
/*  76 */     if (iconID != null) {
/*  77 */       ImageLoader imageLoader = ImageLoader.getInstance();
/*  78 */       if (iconID.startsWith("http")) {
/*  79 */         imgIcon = imageLoader.getUrlImage(iconID, new ImageLoader.ImageDownloaderListener()
/*     */         {
/*     */           public void imageDownloaded(Image image, boolean returnedImmediately)
/*     */           {
/*  83 */             if (returnedImmediately) {
/*  84 */               return;
/*     */             }
/*  86 */             cell.invalidate();
/*     */           }
/*     */         });
/*  89 */         if (imgIcon != null) {}
/*     */       }
/*     */       else
/*     */       {
/*  93 */         imgIcon = imageLoader.getImage(iconID);
/*     */       }
/*     */       
/*  96 */       if (ImageLoader.isRealImage(imgIcon)) {
/*  97 */         Rectangle cellBounds = cell.getBounds();
/*  98 */         Rectangle imgBounds = imgIcon.getBounds();
/*  99 */         gc.drawImage(imgIcon, cellBounds.x + (cellBounds.width - imgBounds.width) / 2, cellBounds.y + (cellBounds.height - imgBounds.height) / 2);
/*     */       }
/*     */       
/*     */ 
/* 103 */       imageLoader.releaseImage(iconID);
/*     */     }
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/* 109 */     VuzeActivitiesEntry entry = (VuzeActivitiesEntry)cell.getDataSource();
/* 110 */     String sort = entry.getTypeID() + entry.getIconID();
/*     */     
/* 112 */     if ((cell.setSortValue(sort)) || (!cell.isValid())) {
/* 113 */       String ts = timeFormat.format(new Date(entry.getTimestamp()));
/* 114 */       cell.setToolTip("Activity occurred on " + ts);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/vuzeactivity/ColumnActivityType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */