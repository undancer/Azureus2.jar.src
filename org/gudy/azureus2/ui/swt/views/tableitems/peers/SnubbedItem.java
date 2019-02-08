/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*    */ public class SnubbedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public SnubbedItem(String table_id)
/*    */   {
/* 43 */     super("S", 3, -1, 20, table_id);
/* 44 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 48 */     info.addCategories(new String[] { "protocol" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 54 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 55 */     boolean bSnubbed = peer == null ? false : peer.isSnubbed();
/*    */     
/* 57 */     if ((!cell.setSortValue(bSnubbed ? 1L : 0L)) && (cell.isValid())) {
/* 58 */       return;
/*    */     }
/* 60 */     cell.setText(bSnubbed ? "*" : "");
/*    */     
/* 62 */     TableRow row = cell.getTableRow();
/* 63 */     if (row != null) {
/* 64 */       row.setForeground(Utils.colorToIntArray(bSnubbed ? Colors.grey : null));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/SnubbedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */