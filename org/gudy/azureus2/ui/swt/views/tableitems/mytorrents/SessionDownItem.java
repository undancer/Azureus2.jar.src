/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ public class SessionDownItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 44 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "sessiondown";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 49 */     info.addCategories(new String[] { "content", "progress", "bytes" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SessionDownItem(String sTableID)
/*    */   {
/* 58 */     super(DATASOURCE_TYPE, "sessiondown", 2, 70, sTableID);
/* 59 */     setRefreshInterval(-2);
/* 60 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 64 */     Object ds = cell.getDataSource();
/* 65 */     long value = 0L;
/* 66 */     if ((ds instanceof DownloadManager)) {
/* 67 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 68 */       value = dm.getStats().getSessionDataBytesReceived();
/*    */     }
/* 70 */     if ((!cell.setSortValue(value)) && (cell.isValid()))
/* 71 */       return;
/* 72 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtc(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SessionDownItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */