/*     */ package com.aelitis.azureus.ui.swt.columns.subscriptions;
/*     */ 
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellMouseListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*     */ public class ColumnSubscriptionName
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellSWTPaintListener, TableCellMouseListener
/*     */ {
/*  47 */   public static String COLUMN_ID = "name";
/*     */   
/*     */ 
/*  50 */   int imageWidth = -1;
/*  51 */   int imageHeight = -1;
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  54 */     info.addCategories(new String[] { "essential" });
/*     */     
/*     */ 
/*  57 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public ColumnSubscriptionName(String sTableID)
/*     */   {
/*  62 */     super(COLUMN_ID, -2, 350, sTableID);
/*  63 */     setRefreshInterval(-2);
/*  64 */     setMinWidth(300);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  68 */     String name = null;
/*  69 */     Subscription sub = (Subscription)cell.getDataSource();
/*  70 */     if (sub != null) {
/*  71 */       name = sub.getName();
/*     */     }
/*  73 */     if (name == null) {
/*  74 */       name = "";
/*     */     }
/*     */     
/*  77 */     if ((!cell.setSortValue(name)) && (cell.isValid())) {}
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/*  83 */     Rectangle bounds = cell.getBounds();
/*     */     
/*  85 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  86 */     Image viewImage = imageLoader.getImage("ic_view");
/*  87 */     if ((this.imageWidth == -1) || (this.imageHeight == -1)) {
/*  88 */       this.imageWidth = viewImage.getBounds().width;
/*  89 */       this.imageHeight = viewImage.getBounds().height;
/*     */     }
/*     */     
/*  92 */     bounds.width -= this.imageWidth + 5;
/*     */     
/*  94 */     GCStringPrinter.printString(gc, cell.getSortValue().toString(), bounds, true, false, 16384);
/*     */     
/*  96 */     Subscription sub = (Subscription)cell.getDataSource();
/*     */     
/*  98 */     if ((sub != null) && (!sub.isSearchTemplate()))
/*     */     {
/* 100 */       gc.drawImage(viewImage, bounds.x + bounds.width, bounds.y + bounds.height / 2 - this.imageHeight / 2);
/*     */     }
/*     */     
/* 103 */     imageLoader.releaseImage("ic_view");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cellMouseTrigger(TableCellMouseEvent event)
/*     */   {
/* 110 */     if ((event.eventType == 1) && (event.button == 1))
/*     */     {
/* 112 */       TableCell cell = event.cell;
/* 113 */       int cellWidth = cell.getWidth();
/* 114 */       if ((event.x > cellWidth - this.imageWidth - 5) && (event.x < cellWidth - 5)) {
/* 115 */         Subscription sub = (Subscription)cell.getDataSource();
/* 116 */         if ((sub != null) && (!sub.isSearchTemplate())) {
/* 117 */           String key = "Subscription_" + ByteFormatter.encodeString(sub.getPublicKey());
/* 118 */           MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 119 */           if (mdi != null) {
/* 120 */             mdi.showEntryByID(key);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubscriptionName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */