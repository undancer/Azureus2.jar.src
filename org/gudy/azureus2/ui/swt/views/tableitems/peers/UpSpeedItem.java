/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ public class UpSpeedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public UpSpeedItem(String table_id)
/*    */   {
/* 42 */     super("uploadspeed", 2, -2, 65, table_id);
/* 43 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 47 */     info.addCategories(new String[] { "bytes" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 53 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 54 */     long data_value = 0L;
/* 55 */     long prot_value = 0L;
/*    */     
/* 57 */     if (peer != null) {
/* 58 */       data_value = peer.getStats().getDataSendRate();
/* 59 */       prot_value = peer.getStats().getProtocolSendRate();
/*    */     }
/* 61 */     long sort_value = (data_value << 32) + prot_value;
/*    */     
/* 63 */     if ((!cell.setSortValue(sort_value)) && (cell.isValid())) {
/* 64 */       return;
/*    */     }
/* 66 */     cell.setText(DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(data_value, prot_value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/UpSpeedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */