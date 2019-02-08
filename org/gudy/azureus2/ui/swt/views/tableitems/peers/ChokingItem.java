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
/*    */ 
/*    */ public class ChokingItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   private static final String COLUMN_ID = "C2";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 44 */     info.addCategories(new String[] { "protocol" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ChokingItem(String table_id)
/*    */   {
/* 51 */     super("C2", 3, -1, 20, table_id);
/* 52 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 56 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 57 */     long value = peer == null ? 0L : peer.isChokedByMe() ? 1 : 0;
/*    */     
/* 59 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 60 */       return;
/*    */     }
/* 62 */     cell.setText(value == 1L ? "*" : "");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/ChokingItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */