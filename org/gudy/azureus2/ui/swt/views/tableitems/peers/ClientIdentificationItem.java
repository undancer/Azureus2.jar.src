/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
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
/*    */ public class ClientIdentificationItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "client_identification";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 37 */     info.addCategories(new String[] { "identification" });
/*    */   }
/*    */   
/*    */ 
/*    */   public ClientIdentificationItem(String table_id)
/*    */   {
/* 43 */     super("client_identification", -1, 200, table_id);
/* 44 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 51 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 52 */     if (peer == null) { cell.setText("");return; }
/* 53 */     String peer_id_name = peer.getClientNameFromPeerID();
/* 54 */     String peer_handshake_name = peer.getClientNameFromExtensionHandshake();
/*    */     
/* 56 */     if (peer_id_name == null) peer_id_name = "";
/* 57 */     if (peer_handshake_name == null) { peer_handshake_name = "";
/*    */     }
/* 59 */     if ((peer_id_name.equals("")) && (peer_handshake_name.equals(""))) {
/* 60 */       cell.setText("");return;
/*    */     }
/*    */     
/* 63 */     String result = peer_id_name;
/* 64 */     if (!peer_handshake_name.equals("")) result = result + " / " + peer_handshake_name;
/* 65 */     cell.setText(result);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/ClientIdentificationItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */