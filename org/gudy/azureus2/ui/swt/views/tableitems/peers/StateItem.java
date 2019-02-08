/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
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
/*    */ public class StateItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public StateItem(String table_id)
/*    */   {
/* 37 */     super("state", -2, 65, table_id);
/* 38 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 42 */     info.addCategories(new String[] { "protocol", "connection" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 49 */     PEPeerTransport peer = (PEPeerTransport)cell.getDataSource();
/* 50 */     String state_text = "";
/* 51 */     if (peer != null) {
/* 52 */       int state = peer.getConnectionState();
/*    */       
/* 54 */       if ((!cell.setSortValue(state)) && (cell.isValid())) {
/* 55 */         return;
/*    */       }
/*    */       
/* 58 */       switch (state) {
/*    */       case 0: 
/* 60 */         state_text = MessageText.getString("PeersView.state.pending");
/* 61 */         break;
/*    */       case 1: 
/* 63 */         state_text = MessageText.getString("PeersView.state.connecting");
/* 64 */         break;
/*    */       case 2: 
/* 66 */         state_text = MessageText.getString("PeersView.state.handshake");
/* 67 */         break;
/*    */       case 4: 
/* 69 */         state_text = MessageText.getString("PeersView.state.established");
/*    */       }
/*    */       
/*    */     }
/*    */     
/* 74 */     cell.setText(state_text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/StateItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */