/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.pieces;
/*    */ 
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.core3.peer.PEPiece;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TypeItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public TypeItem()
/*    */   {
/* 40 */     super("type", 2, -2, 80, "Pieces");
/* 41 */     setRefreshInterval(-2);
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 45 */     PEPiece piece = (PEPiece)cell.getDataSource();
/* 46 */     long value = piece.getSpeed() > 2 ? 1L : piece == null ? 0L : 0L;
/*    */     
/* 48 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 49 */       return;
/*    */     }
/*    */     
/* 52 */     cell.setText(MessageText.getString("PiecesView.typeItem." + value));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/pieces/TypeItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */