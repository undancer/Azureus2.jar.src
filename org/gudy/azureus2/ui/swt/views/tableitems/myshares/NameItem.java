/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.myshares;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
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
/*    */ public class NameItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public NameItem()
/*    */   {
/* 36 */     super("name", -2, 400, "MyShares");
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 40 */     ShareResource item = (ShareResource)cell.getDataSource();
/* 41 */     if (item == null) {
/* 42 */       cell.setText("");
/*    */     } else {
/* 44 */       cell.setText(item.getName());
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/myshares/NameItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */