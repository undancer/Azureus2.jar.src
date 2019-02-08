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
/*    */ public class HandshakeReservedBytesItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "handshake_reserved";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 37 */     info.addCategories(new String[] { "protocol" });
/*    */   }
/*    */   
/*    */ 
/*    */   public HandshakeReservedBytesItem(String table_id)
/*    */   {
/* 43 */     super("handshake_reserved", -1, 80, table_id);
/* 44 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 48 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 49 */     byte[] handshake_reserved = null;
/* 50 */     if (peer != null) { handshake_reserved = peer.getHandshakeReservedBytes();
/*    */     }
/* 52 */     if (handshake_reserved == null) {
/* 53 */       cell.setText("");return;
/*    */     }
/* 55 */     cell.setText(ByteFormatter.nicePrint(handshake_reserved, false));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/HandshakeReservedBytesItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */