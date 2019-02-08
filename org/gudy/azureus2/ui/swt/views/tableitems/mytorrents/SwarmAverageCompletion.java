/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerManager;
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
/*    */ public class SwarmAverageCompletion
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/* 40 */   public static final Class DATASOURCE_TYPE = Download.class;
/*    */   
/*    */   public static final String COLUMN_ID = "swarm_average_completion";
/*    */   
/*    */   public SwarmAverageCompletion(String sTableID)
/*    */   {
/* 46 */     super(DATASOURCE_TYPE, "swarm_average_completion", 2, 70, sTableID);
/* 47 */     setRefreshInterval(-1);
/* 48 */     setMinWidthAuto(true);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 52 */     info.addCategories(new String[] { "swarm", "progress" });
/*    */     
/*    */ 
/*    */ 
/* 56 */     info.setProficiency((byte)1);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 60 */     int average = -1;
/*    */     
/* 62 */     DownloadManager dm = (DownloadManager)cell.getDataSource();
/*    */     
/* 64 */     if (dm != null) {
/* 65 */       PEPeerManager pm = dm.getPeerManager();
/*    */       
/* 67 */       if (pm != null) {
/* 68 */         average = pm.getAverageCompletionInThousandNotation();
/*    */       }
/*    */     }
/*    */     
/* 72 */     if ((!cell.setSortValue(average)) && (cell.isValid())) {
/* 73 */       return;
/*    */     }
/*    */     
/* 76 */     if (average < 0) {
/* 77 */       cell.setText("");
/*    */     }
/*    */     else {
/* 80 */       cell.setText(DisplayFormatters.formatPercentFromThousands(average));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/mytorrents/SwarmAverageCompletion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */