/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.pieces;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPiece;
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
/*    */ public class CompletedItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public CompletedItem()
/*    */   {
/* 36 */     super("completed", 2, -2, 80, "Pieces");
/* 37 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void fillTableColumnInfo(TableColumnInfo info) {
/* 41 */     info.addCategories(new String[] { "progress" });
/*    */   }
/*    */   
/*    */ 
/*    */   public void refresh(TableCell cell)
/*    */   {
/* 47 */     PEPiece piece = (PEPiece)cell.getDataSource();
/* 48 */     long value = piece == null ? 0L : piece.getNbWritten();
/*    */     
/* 50 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 51 */       return;
/*    */     }
/*    */     
/* 54 */     cell.setText("" + value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/pieces/CompletedItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */