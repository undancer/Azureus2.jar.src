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
/*    */ public class UpItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 44 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "up";
/*    */   
/*    */   public UpItem(String sTableID)
/*    */   {
/* 50 */     super(DATASOURCE_TYPE, "up", 2, 70, sTableID);
/* 51 */     setRefreshInterval(-2);
/* 52 */     setMinWidthAuto(true);
/*    */     
/* 54 */     setPosition(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 58 */     info.addCategories(new String[] { "sharing", "bytes" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 65 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 66 */     long value = dm == null ? 0L : dm.getStats().getTotalDataBytesSent();
/*    */     
/* 68 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 69 */       return;
/*    */     }
/* 71 */     cell.setText(DisplayFormatters.formatByteCountToKiBEtc(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/UpItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */