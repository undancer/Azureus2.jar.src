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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PieceItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public PieceItem(String table_id)
/*    */   {
/* 41 */     super("piece", 2, -1, 40, table_id);
/* 42 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 46 */     info.addCategories(new String[] { "content" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 52 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 53 */     int value = peer == null ? 0 : peer.getLastPiece();
/*    */     
/* 55 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 56 */       return;
/*    */     }
/* 58 */     cell.setText("" + value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/PieceItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */