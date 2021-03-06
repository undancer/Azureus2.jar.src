/*    */ package org.gudy.azureus2.ui.swt.views.tableitems.pieces;
/*    */ 
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
/*    */ public class BlockCountItem
/*    */   extends CoreTableColumnSWT
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   public BlockCountItem()
/*    */   {
/* 36 */     super("numberofblocks", 2, -2, 65, "Pieces");
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 40 */     PEPiece piece = (PEPiece)cell.getDataSource();
/* 41 */     long value = piece == null ? 0L : piece.getNbBlocks();
/*    */     
/* 43 */     if ((!cell.setSortValue(value)) && (cell.isValid())) {
/* 44 */       return;
/*    */     }
/*    */     
/* 47 */     cell.setText("" + value);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/tableitems/pieces/BlockCountItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */