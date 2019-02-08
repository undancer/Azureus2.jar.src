/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerState;
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
/*    */ public class MergedDataItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 41 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "mergeddata";
/*    */   
/*    */   public MergedDataItem(String sTableID)
/*    */   {
/* 47 */     super(DATASOURCE_TYPE, "mergeddata", 2, 70, sTableID);
/*    */     
/* 49 */     setRefreshInterval(-2);
/*    */     
/* 51 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 55 */     info.addCategories(new String[] { "bytes" });
/*    */     
/*    */ 
/* 58 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 62 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*    */     
/* 64 */     long value = dm == null ? 0L : dm.getDownloadState().getLongAttribute("mergedata");
/*    */     
/* 66 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 67 */       return;
/*    */     }
/* 69 */     cell.setText(value == 0L ? "" : DisplayFormatters.formatByteCountToKiBEtc(value));
/*    */     
/* 71 */     String info = dm.isSwarmMerging();
/*    */     
/* 73 */     if (info == null) {
/* 74 */       info = "";
/*    */     }
/* 76 */     cell.setToolTip(info);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/MergedDataItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */