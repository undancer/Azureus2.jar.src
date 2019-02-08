/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*    */ import org.gudy.azureus2.core3.util.TimeFormatter;
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
/*    */ public class SecondsDownloadingItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 44 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "secondsdownloading";
/*    */   
/*    */   public SecondsDownloadingItem(String sTableID)
/*    */   {
/* 50 */     super(DATASOURCE_TYPE, "secondsdownloading", 2, 70, sTableID);
/* 51 */     setRefreshInterval(-2);
/* 52 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 56 */     info.addCategories(new String[] { "time" });
/*    */     
/*    */ 
/* 59 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 63 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 64 */     long value = dm == null ? 0L : dm.getStats().getSecondsDownloading();
/*    */     
/* 66 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 67 */       return;
/*    */     }
/* 69 */     cell.setText(TimeFormatter.format(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SecondsDownloadingItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */