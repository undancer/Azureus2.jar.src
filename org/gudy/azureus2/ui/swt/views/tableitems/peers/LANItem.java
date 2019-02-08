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
/*    */ public class LANItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "lan";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 44 */     info.addCategories(new String[] { "connection" });
/*    */   }
/*    */   
/*    */ 
/*    */   public LANItem(String table_id)
/*    */   {
/* 50 */     super("lan", 3, -1, 20, table_id);
/* 51 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 55 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 56 */     boolean lan = peer == null ? false : peer.isLANLocal();
/*    */     
/* 58 */     if ((!cell.setSortValue(lan ? 1L : 0L)) && (cell.isValid())) {
/* 59 */       return;
/*    */     }
/* 61 */     cell.setText(lan ? "*" : "");
/*    */     
/* 63 */     TableRow row = cell.getTableRow();
/* 64 */     if (row != null) {
/* 65 */       row.setForeground(Utils.colorToIntArray(lan ? Colors.blue : null));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/LANItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */