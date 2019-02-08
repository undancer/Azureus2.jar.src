/*     */ package com.aelitis.azureus.ui.swt.columns.subscriptions;
/*     */ 
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
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
/*     */ public class ColumnSubscriptionNew
/*     */   extends CoreTableColumnSWT
/*     */   implements TableCellRefreshListener, TableCellSWTPaintListener
/*     */ {
/*     */   public static final String COLUMN_ID = "new";
/*  42 */   private static int WIDTH = 38;
/*     */   
/*     */ 
/*     */   private static Image imgNew;
/*     */   
/*     */ 
/*     */   private Rectangle imgBounds;
/*     */   
/*     */ 
/*     */   public ColumnSubscriptionNew(String tableID)
/*     */   {
/*  53 */     super("new", tableID);
/*     */     
/*  55 */     initializeAsGraphic(WIDTH);
/*  56 */     setRefreshInterval(-2);
/*  57 */     setMinWidth(WIDTH);
/*  58 */     setMaxWidth(WIDTH);
/*  59 */     setVisible(true);
/*  60 */     imgNew = ImageLoader.getInstance().getImage("image.activity.unread");
/*  61 */     this.imgBounds = imgNew.getBounds();
/*     */   }
/*     */   
/*     */   public void fillTableColumnInfo(TableColumnInfo info) {
/*  65 */     info.addCategories(new String[] { "essential" });
/*     */     
/*     */ 
/*  68 */     info.setProficiency((byte)0);
/*     */   }
/*     */   
/*     */   public void cellPaint(GC gc, TableCellSWT cell)
/*     */   {
/*  73 */     Subscription sub = (Subscription)cell.getDataSource();
/*     */     
/*  75 */     if (sub.getHistory().getNumUnread() > 0) {
/*  76 */       Rectangle cellBounds = cell.getBounds();
/*  77 */       gc.drawImage(imgNew, cellBounds.x + (cellBounds.width - this.imgBounds.width) / 2, cellBounds.y + (cellBounds.height - this.imgBounds.height) / 2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cellAdded(TableCell cell)
/*     */   {
/*  85 */     cell.setMarginWidth(0);
/*  86 */     cell.setMarginHeight(0);
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell)
/*     */   {
/*  91 */     Subscription sub = (Subscription)cell.getDataSource();
/*     */     
/*  93 */     boolean isRead = sub.getHistory().getNumUnread() > 0;
/*  94 */     int sortVal = isRead ? 1 : 0;
/*     */     
/*  96 */     if ((!cell.setSortValue(sortVal)) && (cell.isValid())) {
/*  97 */       return;
/*     */     }
/*     */     
/* 100 */     cell.invalidate();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/columns/subscriptions/ColumnSubscriptionNew.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */