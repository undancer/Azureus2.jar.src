/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*    */ import org.gudy.azureus2.core3.config.ParameterListener;
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ public class UpItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   protected static boolean separate_prot_data_stats;
/*    */   protected static boolean data_stats_only;
/*    */   
/*    */   static
/*    */   {
/* 45 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "config.style.dataStatsOnly", "config.style.separateProtDataStats" }, new ParameterListener()
/*    */     {
/*    */       public void parameterChanged(String x)
/*    */       {
/* 49 */         UpItem.separate_prot_data_stats = COConfigurationManager.getBooleanParameter("config.style.separateProtDataStats");
/* 50 */         UpItem.data_stats_only = COConfigurationManager.getBooleanParameter("config.style.dataStatsOnly");
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   public UpItem(String table_id)
/*    */   {
/* 57 */     super("upload", 2, -1, 70, table_id);
/* 58 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 62 */     info.addCategories(new String[] { "bytes" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 68 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 69 */     long data_value = 0L;
/* 70 */     long prot_value = 0L;
/*    */     
/* 72 */     if (peer != null) {
/* 73 */       data_value = peer.getStats().getTotalDataBytesSent();
/* 74 */       prot_value = peer.getStats().getTotalProtocolBytesSent(); }
/*    */     long sort_value;
/*    */     long sort_value;
/* 77 */     if (separate_prot_data_stats) {
/* 78 */       sort_value = (data_value << 24) + prot_value; } else { long sort_value;
/* 79 */       if (data_stats_only) {
/* 80 */         sort_value = data_value;
/*    */       } else {
/* 82 */         sort_value = data_value + prot_value;
/*    */       }
/*    */     }
/* 85 */     if ((!cell.setSortValue(sort_value)) && (cell.isValid())) {
/* 86 */       return;
/*    */     }
/* 88 */     cell.setText(DisplayFormatters.formatDataProtByteCountToKiBEtc(data_value, prot_value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/UpItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */