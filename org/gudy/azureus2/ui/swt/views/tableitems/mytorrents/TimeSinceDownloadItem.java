/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*    */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*    */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
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
/*    */ 
/*    */ public class TimeSinceDownloadItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 45 */   public static final Class DATASOURCE_TYPE = DownloadTypeIncomplete.class;
/*    */   
/*    */   public static final String COLUMN_ID = "timesincedownload";
/*    */   
/*    */   public TimeSinceDownloadItem(String sTableID)
/*    */   {
/* 51 */     super(DATASOURCE_TYPE, "timesincedownload", 2, 70, sTableID);
/* 52 */     setRefreshInterval(-2);
/* 53 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 57 */     info.addCategories(new String[] { "time" });
/*    */     
/*    */ 
/* 60 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 64 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*    */     
/* 66 */     int value = dm == null ? -2 : dm.getStats().getTimeSinceLastDataReceivedInSeconds();
/*    */     
/* 68 */     if ((!cell.setSortValue(value == -1 ? 2147483647L : value)) && (cell.isValid())) {
/* 69 */       return;
/*    */     }
/* 71 */     cell.setText(value == -1 ? "∞" : value == -2 ? "" : TimeFormatter.format(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/TimeSinceDownloadItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */