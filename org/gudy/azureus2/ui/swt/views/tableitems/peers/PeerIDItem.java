/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import java.io.UnsupportedEncodingException;
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
/*    */ public class PeerIDItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public PeerIDItem(String table_id)
/*    */   {
/* 34 */     super("peer_id", -1, 100, table_id);
/* 35 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 39 */     info.addCategories(new String[] { "identification" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 45 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 46 */     if (peer == null) { cell.setText("");return;
/*    */     }
/* 48 */     byte[] peer_id = peer.getId();
/* 49 */     if (peer_id == null) { cell.setText("");return;
/*    */     }
/* 51 */     try { String text = new String(peer_id, 0, peer_id.length, "ISO-8859-1");
/* 52 */       text = text.replace('\f', ' ');
/* 53 */       text = text.replace('\n', ' ');
/* 54 */       cell.setText(text);
/*    */     }
/*    */     catch (UnsupportedEncodingException uee) {
/* 57 */       cell.setText("");
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/PeerIDItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */