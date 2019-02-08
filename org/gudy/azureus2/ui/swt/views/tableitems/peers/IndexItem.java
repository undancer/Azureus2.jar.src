/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.peers;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableColumnInfo;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
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
/*    */ public class IndexItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public static final String COLUMN_ID = "#";
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info)
/*    */   {
/* 34 */     info.addCategories(new String[] { "settings" });
/*    */   }
/*    */   
/*    */ 
/*    */   public IndexItem(String table_id)
/*    */   {
/* 40 */     super("#", 2, -1, 20, table_id);
/* 41 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 46 */     TableRow row = cell.getTableRow();
/*    */     
/* 48 */     if (row != null) {
/* 49 */       int index = row.getIndex();
/*    */       
/* 51 */       if ((!cell.setSortValue(index)) && (cell.isValid())) {
/* 52 */         return;
/*    */       }
/* 54 */       cell.setText("" + (index + 1));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/peers/IndexItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */