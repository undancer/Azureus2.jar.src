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
/*    */ public class SecondsSeedingItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 43 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "secondsseeding";
/*    */   
/*    */   public SecondsSeedingItem(String sTableID)
/*    */   {
/* 49 */     super(DATASOURCE_TYPE, "secondsseeding", 2, 70, sTableID);
/* 50 */     setRefreshInterval(-2);
/* 51 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 55 */     info.addCategories(new String[] { "sharing", "time" });
/*    */     
/*    */ 
/*    */ 
/* 59 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 63 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 64 */     long value = dm == null ? 0L : dm.getStats().getSecondsDownloading() + dm.getStats().getSecondsOnlySeeding();
/*    */     
/*    */ 
/* 67 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 68 */       return;
/*    */     }
/* 70 */     cell.setText(TimeFormatter.format(value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SecondsSeedingItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */