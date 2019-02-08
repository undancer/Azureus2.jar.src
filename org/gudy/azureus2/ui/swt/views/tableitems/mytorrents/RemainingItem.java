/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ public class RemainingItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 45 */   public static final Class DATASOURCE_TYPE = DownloadTypeIncomplete.class;
/*    */   
/*    */   public static final String COLUMN_ID = "remaining";
/*    */   
/*    */   public RemainingItem(String sTableID)
/*    */   {
/* 51 */     super(DATASOURCE_TYPE, "remaining", 2, 70, sTableID);
/* 52 */     addDataSourceType(DiskManagerFileInfo.class);
/* 53 */     setRefreshInterval(-2);
/* 54 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 58 */     info.addCategories(new String[] { "content", "progress" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 64 */   private boolean bLastValueEstimate = false;
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 67 */     long lRemaining = getRemaining(cell);
/*    */     
/* 69 */     if ((!cell.setSortValue(lRemaining)) && (cell.isValid())) {
/* 70 */       return;
/*    */     }
/*    */     
/* 73 */     if (this.bLastValueEstimate) {
/* 74 */       cell.setText("~ " + DisplayFormatters.formatByteCountToKiBEtc(lRemaining));
/*    */     } else {
/* 76 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtc(lRemaining));
/*    */     }
/*    */   }
/*    */   
/*    */   private long getRemaining(TableCell cell) {
/* 81 */     Object ds = cell.getDataSource();
/* 82 */     if ((ds instanceof DownloadManager)) {
/* 83 */       DownloadManager manager = (DownloadManager)cell.getDataSource();
/* 84 */       if (manager != null) {
/* 85 */         return manager.getStats().getRemainingExcludingDND();
/*    */       }
/* 87 */     } else if ((ds instanceof DiskManagerFileInfo)) {
/* 88 */       DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)ds;
/* 89 */       return fileInfo.getLength() - fileInfo.getDownloaded();
/*    */     }
/* 91 */     return 0L;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/RemainingItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */