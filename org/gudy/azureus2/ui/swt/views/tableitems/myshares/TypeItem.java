/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.myshares;
/*    */ 
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*    */ import org.gudy.azureus2.plugins.sharing.ShareResourceDirContents;
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
/*    */ public class TypeItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public TypeItem()
/*    */   {
/* 37 */     super("type", -2, 100, "MyShares");
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 41 */     ShareResource item = (ShareResource)cell.getDataSource();
/*    */     
/* 43 */     String text = "";
/*    */     
/* 45 */     if (item != null) {
/* 46 */       int type = item.getType();
/*    */       
/* 48 */       if ((!cell.setSortValue(type)) && (cell.isValid())) {
/* 49 */         return;
/*    */       }
/*    */       
/* 52 */       if (type == 2) {
/* 53 */         text = MessageText.getString("MySharesView.type.dir");
/* 54 */       } else if (type == 1) {
/* 55 */         text = MessageText.getString("MySharesView.type.file");
/* 56 */       } else if (type == 3) {
/* 57 */         ShareResourceDirContents s = (ShareResourceDirContents)item;
/* 58 */         if (s.isRecursive()) {
/* 59 */           text = MessageText.getString("MySharesView.type.dircontentsrecursive");
/*    */         } else {
/* 61 */           text = MessageText.getString("MySharesView.type.dircontents");
/*    */         }
/*    */       }
/*    */     }
/* 65 */     cell.setText(text);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/myshares/TypeItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */