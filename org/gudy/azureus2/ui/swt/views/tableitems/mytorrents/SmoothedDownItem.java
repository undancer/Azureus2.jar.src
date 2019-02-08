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
/*    */ public class SmoothedDownItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 38 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */ 
/*    */   public static final String COLUMN_ID = "smoothdown";
/*    */   
/*    */ 
/*    */   public SmoothedDownItem(String sTableID)
/*    */   {
/* 46 */     super(DATASOURCE_TYPE, "smoothdown", 2, 70, sTableID);
/* 47 */     setRefreshInterval(-2);
/* 48 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 52 */     info.addCategories(new String[] { "bytes" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 62 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*    */     
/* 64 */     long value = dm == null ? 0L : dm.getStats().getSmoothedDataReceiveRate();
/*    */     
/* 66 */     if ((!cell.setSortValue(value)) && (cell.isValid()))
/*    */     {
/* 68 */       return;
/*    */     }
/*    */     
/* 71 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SmoothedDownItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */