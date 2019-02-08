/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
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
/*    */ public class DownItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 45 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   public static final String COLUMN_ID = "down";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 50 */     info.addCategories(new String[] { "content", "progress", "bytes" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DownItem(String sTableID)
/*    */   {
/* 59 */     super(DATASOURCE_TYPE, "down", 2, 70, sTableID);
/* 60 */     addDataSourceType(DiskManagerFileInfo.class);
/* 61 */     setRefreshInterval(-2);
/* 62 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 66 */     Object ds = cell.getDataSource();
/* 67 */     long value = 0L;
/* 68 */     if ((ds instanceof DownloadManager)) {
/* 69 */       DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 70 */       value = dm.getStats().getTotalGoodDataBytesReceived();
/* 71 */     } else if ((ds instanceof DiskManagerFileInfo)) {
/* 72 */       DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/* 73 */       value = fileInfo.getDownloaded();
/*    */     }
/* 75 */     if ((!cell.setSortValue(value)) && (cell.isValid()))
/* 76 */       return;
/* 77 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtc(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/DownItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */