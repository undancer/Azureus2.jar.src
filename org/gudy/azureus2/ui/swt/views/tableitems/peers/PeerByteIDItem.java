/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.core3.util.ByteFormatter;
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
/*    */ public class PeerByteIDItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public PeerByteIDItem(String table_id)
/*    */   {
/* 35 */     super("peer_byte_id", -1, 100, table_id);
/* 36 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 40 */     info.addCategories(new String[] { "identification" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 46 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 47 */     if (peer == null) { cell.setText("");return; }
/* 48 */     byte[] peer_id = peer.getId();
/* 49 */     if (peer_id == null) { cell.setText("");return; }
/* 50 */     cell.setText(ByteFormatter.nicePrint(peer_id));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/PeerByteIDItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */