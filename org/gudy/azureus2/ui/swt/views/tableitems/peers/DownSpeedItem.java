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
/*    */ public class DownSpeedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "downloadspeed";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 44 */     info.addCategories(new String[] { "bytes" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public DownSpeedItem(String table_id)
/*    */   {
/* 51 */     super("downloadspeed", 2, -2, 65, table_id);
/* 52 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 56 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 57 */     long data_value = 0L;
/* 58 */     long prot_value = 0L;
/*    */     
/* 60 */     if (peer != null) {
/* 61 */       data_value = peer.getStats().getDataReceiveRate();
/* 62 */       prot_value = peer.getStats().getProtocolReceiveRate();
/*    */     }
/* 64 */     long sort_value = (data_value << 32) + prot_value;
/*    */     
/* 66 */     if ((!cell.setSortValue(sort_value)) && (cell.isValid())) {
/* 67 */       return;
/*    */     }
/* 69 */     cell.setText(DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(data_value, prot_value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/DownSpeedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */