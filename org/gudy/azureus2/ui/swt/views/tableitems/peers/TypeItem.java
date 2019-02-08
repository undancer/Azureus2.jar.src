/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellToolTipListener;
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
/*    */ public class TypeItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener, TableCellToolTipListener
/*    */ {
/*    */   public TypeItem(String table_id)
/*    */   {
/* 41 */     super("T", 3, -2, 20, table_id);
/* 42 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 46 */     info.addCategories(new String[] { "connection" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 52 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 53 */     long value = peer == null ? 0L : peer.isIncoming() ? 1 : 0;
/*    */     
/* 55 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 56 */       return;
/*    */     }
/* 58 */     cell.setText(value == 1L ? "R" : "L");
/*    */   }
/*    */   
/*    */   public void cellHover(TableCell cell) {
/* 62 */     String ID = "PeersView.T." + cell.getText() + ".tooltip";
/* 63 */     String sTooltip = MessageText.getString(ID, "");
/* 64 */     if (sTooltip.length() > 0)
/* 65 */       cell.setToolTip(sTooltip);
/*    */   }
/*    */   
/*    */   public void cellHoverComplete(TableCell cell) {
/* 69 */     cell.setToolTip(null);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/TypeItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */