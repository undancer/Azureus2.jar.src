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
/*    */ public class InterestingItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "I2";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 43 */     info.addCategories(new String[] { "protocol" });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public InterestingItem(String table_id)
/*    */   {
/* 50 */     super("I2", 3, -1, 20, table_id);
/* 51 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 55 */     PEPeer peer = (PEPeer)cell.getDataSource();
/* 56 */     long value = peer == null ? 0L : peer.isInterested() ? 1 : 0;
/*    */     
/* 58 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 59 */       return;
/*    */     }
/* 61 */     cell.setText(value == 1L ? "*" : "");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/InterestingItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */