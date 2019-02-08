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
/*    */ public class SwarmAverageSpeed
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 38 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "swarm_average_speed";
/*    */   
/*    */   public SwarmAverageSpeed(String sTableID)
/*    */   {
/* 44 */     super(DATASOURCE_TYPE, "swarm_average_speed", 2, 70, sTableID);
/* 45 */     setRefreshInterval(-2);
/* 46 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 50 */     info.addCategories(new String[] { "swarm" });
/*    */     
/*    */ 
/* 53 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 57 */     long speed = -1L;
/*    */     
/* 59 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/* 60 */     if (dm != null) {
/* 61 */       speed = dm.getStats().getTotalAveragePerPeer();
/*    */     }
/*    */     
/* 64 */     if ((!cell.setSortValue(speed)) && (cell.isValid())) {
/* 65 */       return;
/*    */     }
/*    */     
/* 68 */     if (speed < 0L) {
/* 69 */       cell.setText("");
/*    */     }
/*    */     else {
/* 72 */       cell.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(speed));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SwarmAverageSpeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */