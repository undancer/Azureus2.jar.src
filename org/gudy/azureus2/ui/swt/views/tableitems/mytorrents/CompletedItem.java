/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.ui.swt.views.table.CoreTableColumnSWT;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CompletedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 31 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "completed";
/*    */   
/*    */   public CompletedItem(String sTableID)
/*    */   {
/* 37 */     super(DATASOURCE_TYPE, "completed", 2, 50, sTableID);
/* 38 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/* 41 */   public void fillTableColumnInfo(TableColumnInfo info) { info.addCategories(new String[] { "progress" }); }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 48 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 49 */     if (dm == null) {
/* 50 */       return;
/*    */     }
/* 52 */     TRTrackerScraperResponse resp = dm.getTrackerScrapeResponse();
/* 53 */     if (resp == null) {
/* 54 */       return;
/*    */     }
/* 56 */     int completed = resp.getCompleted();
/* 57 */     if ((cell.setSortValue(completed)) || (!cell.isValid())) {
/* 58 */       cell.setText(completed == -1 ? "?" : Integer.toString(completed));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/CompletedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */