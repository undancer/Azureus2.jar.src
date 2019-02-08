/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;
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
/*    */ public class ColumnPeerNetwork
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public ColumnPeerNetwork(String table_id)
/*    */   {
/* 36 */     super("network", -1, 65, table_id);
/* 37 */     setRefreshInterval(-3);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 41 */     info.addCategories(new String[] { "protocol", "connection" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 48 */     Object ds = cell.getDataSource();
/* 49 */     String text = "";
/* 50 */     Comparable val = null;
/* 51 */     if ((ds instanceof PEPeerTransport)) {
/* 52 */       PEPeerTransport peer = (PEPeerTransport)ds;
/*    */       
/* 54 */       PeerItem identity = peer.getPeerItemIdentity();
/*    */       
/* 56 */       if (identity != null) {
/* 57 */         val = text = identity.getNetwork();
/*    */       }
/*    */     }
/*    */     
/* 61 */     if ((!cell.setSortValue(val)) && (cell.isValid())) {
/* 62 */       return;
/*    */     }
/*    */     
/* 65 */     cell.setText(text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/ColumnPeerNetwork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */